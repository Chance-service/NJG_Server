package com.guaji.game.manager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.guaji.app.AppObj;
import org.guaji.db.DBManager;
import org.guaji.msg.Msg;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.ActivityCfg.ActivityItem;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.ActivityEntity;
import com.guaji.game.entity.ForgingEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.module.activity.forging.ForgingStatus;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Mail.MailType;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;

/**
 * 神装锻造活动管理
 */
public class ForgingManager extends AppObj {

	/**
	 * 活动ID
	 */
	private final int activityId = Const.ActivityId.GODEQUIP_FORGING_VALUE;

	/**
	 * 当前活动对应的数据
	 */
	private ForgingEntity entity = null;

	/**
	 * 全局对象, 便于访问
	 */
	private static ForgingManager instance = null;

	/**
	 * 参加获得的玩家ID集合
	 */
	private Set<Integer> idSet = new HashSet<Integer>();

	/**
	 * 免费锻造缓存<key:玩家ID value:免费时间戳>
	 */
	// private Map<Integer, Long> freeTimeMap = new HashMap<Integer, Long>();

	/**
	 * 获取全局实例对象
	 */
	public static ForgingManager getInstance() {
		return instance;
	}

	public ForgingManager(GuaJiXID xid) {

		super(xid);
		if (instance == null) {
			instance = this;
		}
	}

	/**
	 * 初始化
	 */
	public void init() {

		// 加载正在进行的数据
		ActivityItem activityItem = ActivityManager.getInstance().getActiveActivityItem(activityId);
		if (activityItem != null) {
			ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
			if (timeConfig != null) {
				// 如果活动正在开着 查询数据库有没有活动信息
				List<ForgingEntity> _rewardEntity = DBManager.getInstance().query("from ForgingEntity where stageId = ?",
						timeConfig.getStageId());
				if (null != _rewardEntity && _rewardEntity.size() > 0) {
					this.entity = _rewardEntity.get(0);
					this.entity.init();
				}
				// 创建活动信息
				if (this.entity == null) {
					this.entity = this.createEntity(timeConfig.getStageId());
				}
				// 参加过该活动的玩家ID数据加载
				List<ActivityEntity<ForgingStatus>> status = DBManager.getInstance()
						.query("from ActivityEntity where activityId = ? and stageId = ?", activityId, timeConfig.getStageId());
				if (null != status && status.size() > 0) {
					for (ActivityEntity<ForgingStatus> a_Entity : status) {
						this.idSet.add(a_Entity.getPlayerId());
						/*
						 * ForgingStatus _status =
						 * a_Entity.getActivityStatus(ForgingStatus.class);
						 * this.freeTimeMap.put(a_Entity.getPlayerId(),
						 * _status.getFreeTime());
						 */
					}
				}
			}
		}
	}

	@Override
	public boolean onTick() {

		ActivityItem activityItem = ActivityManager.getInstance().getActiveActivityItem(activityId);
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeToEndCfg(activityId);
		if (activityItem != null) {
			if (timeConfig != null) {
				// 创建活动信息
				if (this.entity == null) {
					this.entity = this.createEntity(timeConfig.getStageId());
				} else {
					if(this.entity.getStageId() != timeConfig.getStageId()){
						this.entity.setInvalid(true);
						this.entity.notifyUpdate(false);
						this.entity = this.createEntity(timeConfig.getStageId());
					}
				}
			}
		}

		/*
		 * Iterator<Entry<Integer, Long>> iterator =
		 * this.freeTimeMap.entrySet().iterator(); while (iterator.hasNext()) {
		 * Entry<Integer, Long> entry = iterator.next(); if (entry.getValue() <
		 * GuaJiTime.getMillisecond()) { GuaJiXID targetXid =
		 * GuaJiXID.valueOf(GsConst.ObjType.PLAYER, entry.getKey()); if
		 * (targetXid != null) { ObjBase<GuaJiXID, AppObj> objBase =
		 * GsApp.getInstance().lockObject(targetXid); if (objBase != null &&
		 * objBase.isObjValid()) { Player targetPlayer = (Player)
		 * objBase.getImpl(); EquipBuildNotice.Builder builder =
		 * EquipBuildNotice.newBuilder(); builder.setIsFree(true);
		 * targetPlayer.sendProtocol(Protocol.valueOf(HP.code.
		 * EQUIP_BUILD_NOTICE_S_VALUE, builder)); } } } }
		 */
		return true;
	}

	@Override
	public boolean onMessage(Msg msg) {

		if (msg.getMsg() == GsConst.MsgType.FORGING_EQUIP_EMAIL) {

			if (msg.getParams().size() < 3) {
				return true;
			}
			String name = msg.getParam(0);
			String getRawards = msg.getParam(1);
			String rawards = msg.getParam(2);

			this.sendRawards(name, getRawards, rawards);
		}
		return true;
	}

	/**
	 * 创建活动信息
	 * 
	 * @param stageId
	 */
	private ForgingEntity createEntity(int stageId) {

		ForgingEntity entity = new ForgingEntity();
		entity.init();
		// 活动开放期号
		entity.setStageId(stageId);
		DBManager.getInstance().create(entity);
		return entity;
	}

	/**
	 * 神器锻造数据
	 * 
	 * @return
	 */
	public ForgingEntity getEntity() {
		return entity;
	}

	/**
	 * 添加参与活动玩家ID
	 */
	public void addId(int playerId) {

		this.idSet.add(playerId);
	}

	/**
	 * 添加免费锻造缓存
	 * 
	 * @param playerId
	 * @param freeTime
	 */
	// public void addFreeTimeMap(int playerId, long freeTime) {
	//
	// this.freeTimeMap.put(playerId, freeTime);
	// }

	/**
	 * 发放世界邮件
	 * 
	 * @param rawards
	 */
	private void sendRawards(String name, String getRawards, String rawards) {

		// 广播
		GsApp.getInstance().broadcastChatWorldMsg(SysBasicCfg.getInstance().getForgingBroadcastId() + "#1#" + name + "#1#" + getRawards, null);
		// 奖励发放
		AwardItems awardItems = AwardItems.valueOf(rawards);
		for (Integer playerId : this.idSet) {
			if (null != playerId && playerId > 0) {
				// 发阶段奖励邮件
				MailManager.createMail(playerId, MailType.Reward_VALUE, GsConst.MailId.FORGING_REWARD, "锻造活动获得极品道具奖励", awardItems,
						GuaJiTime.getTimeString(), name, getRawards);
			}
		}
	}

}
