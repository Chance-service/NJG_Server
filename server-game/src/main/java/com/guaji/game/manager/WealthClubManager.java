package com.guaji.game.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.ActivityCfg.ActivityItem;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.WealthClubCfg;
import com.guaji.game.entity.ActivityEntity;
import com.guaji.game.entity.WealthClubEntity;
import com.guaji.game.entity.WealthData;
import com.guaji.game.item.AwardItems;
import com.guaji.game.module.activity.wealthClub.WealthClubStatus;
import com.guaji.game.protocol.Activity2.HPGoldClubStatusRet;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Mail.MailType;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;

/**
 * 财富聚乐部管理类
 */
public class WealthClubManager extends AppObj {

	/**
	 * 全局对象, 便于访问
	 */
	private static WealthClubManager instance = null;

	/**
	 * 当前活动对应的财富聚乐部数据
	 */
	private WealthClubEntity entity = null;
	
	/**
	 * 活动处于阶段（活动开放期有意义，0正常，1结算）
	 */
	static private int onStageStatus;

	/**
	 * 一小时
	 */
	private final long AN_HOUR = 60 * 60 * 1000;

	/**
	 * 缓存玩家充值数据--用于发邮件奖励<key：账号ID value:当日充值总金额>
	 */
	private Map<Integer, Integer> rechargeMap = new HashMap<Integer, Integer>();

	/**
	 * 获取全局实例对象
	 */
	public static WealthClubManager getInstance() {
		return instance;
	}

	public WealthClubManager(GuaJiXID xid) {

		super(xid);
		if (instance == null) {
			instance = this;
		}
		onStageStatus = 0;
	}
	
	/**
	 * 更新活动中的状态
	 */
	private void updateStageStauts()
	{
		WealthClubCfg config = ConfigManager.getInstance().getConfigByIndex(WealthClubCfg.class, 0);

		if(onStageStatus == 1)//结算状态
		{
			if (GuaJiTime.getMillisecond() > config.getSettleTimeValue() + 5 * 60 * 1000) {
				// 5分钟--跳到的第二天结算时间数据更新
				config.updateSettleTimeValue();
				onStageStatus = 0;
				HPGoldClubStatusRet.Builder builder = HPGoldClubStatusRet.newBuilder();
				builder.setStageStatus(onStageStatus);
				Protocol protocol = Protocol.valueOf(HP.code.WEALTH_STAGE_STATUS_S_VALUE,builder);
				GsApp.getInstance().broadcastProtocol(protocol);
			}
		}
		else//正常状态
		{
			if (GuaJiTime.getMillisecond() > config.getSettleTimeValue()) 
			{
				onStageStatus = 1;
				HPGoldClubStatusRet.Builder builder = HPGoldClubStatusRet.newBuilder();
				builder.setStageStatus(onStageStatus);
				Protocol protocol = Protocol.valueOf(HP.code.WEALTH_STAGE_STATUS_S_VALUE,builder);
				GsApp.getInstance().broadcastProtocol(protocol);
			}
		}
		return;
	}
	
	/**
	 * 获取活动中的状态
	 */
	public static int getStageStatus()
	{
		return onStageStatus;
	}

	/**
	 * 初始化
	 */
	public void init() {

		int activityId = Const.ActivityId.GOLD_CLUB_VALUE;
		// 加载正在进行的数据
		ActivityItem activityItem = ActivityManager.getInstance().getActiveActivityItem(activityId);
		if (activityItem != null) {
			ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
			if (timeConfig != null) {
				// 如果活动正在开着 查询数据库有没有活动信息
				List<WealthClubEntity> clubEntity = DBManager.getInstance().query("from WealthClubEntity where stageId = ?", timeConfig.getStageId());
				if (null != clubEntity && clubEntity.size() > 0) {
					this.entity = clubEntity.get(0);
					entity.init();//add by callan
				}
				// 创建活动信息
				if (this.entity == null) {
					this.entity = this.createEntity(timeConfig.getStageId());
				}
				
				this.updateStageStauts();
			}
			// 数据加载
			String dateFormat = GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date());
			List<ActivityEntity<WealthClubStatus>> statusEntity = DBManager.getInstance().query("from ActivityEntity where activityId = ? and stageId = ?", activityId, timeConfig.getStageId());
			if (null != statusEntity && statusEntity.size() > 0) {
				for (ActivityEntity<WealthClubStatus> aEntity : statusEntity) {
					WealthClubStatus status = aEntity.getActivityStatus(WealthClubStatus.class);
					Integer vaule = status.getRechargeMap(dateFormat);
					if (null != vaule && vaule > 0) {
						this.rechargeMap.put(aEntity.getPlayerId(), vaule);
					}
				}
			}
		}
	}

	/**
	 * 创建活动信息
	 * 
	 * @param stageId
	 */
	private WealthClubEntity createEntity(int stageId) {

		WealthClubEntity entity = new WealthClubEntity();
		entity.init();
		// 活动开放期号
		entity.setStageId(stageId);
		DBManager.getInstance().create(entity);
		
		return entity;
	}

	/**
	 * 获取财富聚乐部数据
	 * 
	 * @return
	 */
	public WealthClubEntity getEntity() {
		return entity;
	}

	@Override
	public boolean onTick() {
		// 时间判断
		WealthClubCfg config = ConfigManager.getInstance().getConfigByIndex(WealthClubCfg.class, 0);
		// 延迟三秒处理
		if (GuaJiTime.getMillisecond() < config.getSettleTimeValue() + 3000) {
			return true;
		}
		// 检测活动是否开放
		ActivityItem activityItem = ActivityManager.getInstance().getActiveActivityItem(Const.ActivityId.GOLD_CLUB_VALUE);
		if(activityItem==null){
			return true;
		}
		// 检测活动是否开放
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.GOLD_CLUB_VALUE);
		// 活动是否关闭判断
		if (timeCfg == null) {
			return true;
		}
		// 创建活动信息
		
		if (this.entity == null) {
			this.entity = this.createEntity(timeCfg.getStageId());
		}
		
		
		this.updateStageStauts();
		

		// 到时间发奖励邮件(处理活动时间)
		if (this.rechargeMap.size() > 0) {
			String dateFormat = GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date());
			WealthData wealthData = this.entity.getWealthDataByKey(dateFormat);
			Iterator<Entry<Integer, Integer>> iterator = this.rechargeMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<Integer, Integer> entry = iterator.next();
				this.sendRewardMail(config, entry.getKey(), entry.getValue(), wealthData.getTotalNumber());
				iterator.remove();
			}
			this.rechargeMap.clear();
		}
		return true;
	}

	/**
	 * 每小时自动检测充值玩家数量
	 */
	public synchronized void rechargePlayerNumber() {
		// 活动是否关闭判断
		ActivityItem activityItem = ActivityManager.getInstance().getActiveActivityItem(Const.ActivityId.GOLD_CLUB_VALUE);
		if(activityItem==null){
			return;
		}
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.GOLD_CLUB_VALUE);
		if (timeCfg == null) {
			return;
		}
		// 结算时间判断--是否在结算时间之前
		WealthClubCfg config = ConfigManager.getInstance().getConfigByIndex(WealthClubCfg.class, 0);
		if (GuaJiTime.getMillisecond() > config.getSettleTimeValue()) {
			return;
		}
		// 取每日数据财富俱乐部数据
		String dateFormat = GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date());
		WealthData wealthData = this.entity.getWealthDataByKey(dateFormat);
		// 满足条件自动添加充值人数
		if (GuaJiTime.getMillisecond() - wealthData.getTickTime() > this.AN_HOUR) {
			// 提取功能配置数据
			if (wealthData.getTotalNumber() < config.getBelowNumber()) {
				int totalNumber = config.getAutoAdd();
				totalNumber += wealthData.getTotalNumber();
				wealthData.setTotalNumber(totalNumber);
				wealthData.setTickTime(GuaJiTime.getMillisecond());
				entity.updateWealthData();
			}
		}
	}

	/**
	 * 增加充值人数
	 */
	public synchronized void addTotalNumber() {
		if(getEntity()==null){
			return;
		}
		String dateFormat = GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date());
		WealthData wealthData = this.entity.getWealthDataByKey(dateFormat);
		int totalNumber = 1;
		totalNumber += wealthData.getTotalNumber();
		wealthData.setTotalNumber(totalNumber);
		entity.updateWealthData();
	}

	/**
	 * 添加到今日充值金额缓存
	 * 
	 * @param playerId
	 * @param recharge
	 */
	public void addRecharge(int playerId, int recharge) {

		if (rechargeMap.containsKey(playerId)) {
			int value = rechargeMap.get(playerId);
			value += recharge;
			rechargeMap.put(playerId, value);
		} else {
			rechargeMap.put(playerId, recharge);
		}
	}
	
	/**
	 * 发送充值奖励邮件
	 * 
	 * @param config
	 * @param playerId
	 * @param recharge
	 * @param totalNumber
	 */
	private void sendRewardMail(WealthClubCfg config, int playerId, int recharge, int totalNumber) {
		
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		String goldClubFormula = String.format(config.getFormula(), totalNumber);
		try {
			Double formulaRet = (Double)engine.eval(goldClubFormula);
			int proportion = formulaRet.intValue();
			int number = (int)(recharge * proportion/100);
			String reward =  String.format(config.getReward(), number > config.getAmountLimit() ? config.getAmountLimit() : number);
			// 发阶段奖励邮件
			AwardItems awardItems = AwardItems.valueOf(reward);
			MailManager.createMail(playerId, MailType.Reward_VALUE, GsConst.MailId.WEALTH_CLUB, "财富俱乐部奖励", awardItems, String.valueOf(proportion));
		} catch (ScriptException e) {
			MyException.catchException(e);
		}
	}
}
