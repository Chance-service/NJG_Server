package com.guaji.game.manager;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.msg.Msg;
import org.guaji.os.GuaJiTime;
import org.guaji.util.services.PlatformService;
import org.guaji.xid.GuaJiXID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guaji.game.GsApp;
import com.guaji.game.config.ActivityCfg.ActivityItem;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ExpeditionArmoryRankingCfg;
import com.guaji.game.config.ExpeditionArmoryStageCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.ActivityEntity;
import com.guaji.game.entity.ExpeditionArmoryEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.module.activity.expeditionArmory.ExpeditionArmoryStatus;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity.ExpeditionArmoryRankItem;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Mail.MailType;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;

public class ExpeditionArmoryManager extends AppObj {

	private Logger logger = LoggerFactory.getLogger("Server");

	// 活动id
	private final int activityId = Const.ActivityId.EXPEDITION_ARMORY_VALUE;
	// 最大活动阶段
	private ExpeditionArmoryStageCfg maxDonateStageCfg = null;
	// 模块Tick周期
	private int tickIndex = 0;

	/**
	 * 当前活动对应的远征的对象
	 */
	private ExpeditionArmoryEntity currentActiveExpeditionArmory = null;

	/**
	 * 所有玩家活动数据及排行
	 */
	private ConcurrentSkipListSet<ExpeditionArmoryStatus> expeditionArmorySet;

	public ExpeditionArmoryManager(GuaJiXID xid) {
		super(xid);
		expeditionArmorySet = new ConcurrentSkipListSet<ExpeditionArmoryStatus>();

		if (instance == null) {
			instance = this;
		}
	}

	/**
	 * 全局对象, 便于访问
	 */
	private static ExpeditionArmoryManager instance = null;

	/**
	 * 获取全局实例对象
	 */
	public static ExpeditionArmoryManager getInstance() {
		return instance;
	}

	/**
	 * 初始化
	 */
	public void init() {
		// 获取最大活动阶段配置
		TreeMap<Object, ExpeditionArmoryStageCfg> stageCfgs = (TreeMap<Object, ExpeditionArmoryStageCfg>) ConfigManager.getInstance().getConfigMap(ExpeditionArmoryStageCfg.class);
		maxDonateStageCfg = stageCfgs.lastEntry().getValue();

		// 加载正在进行的数据
		ActivityItem activityItem = ActivityManager.getInstance().getActiveActivityItem(activityId);
		if (activityItem != null) {
			ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
			if (activityTimeCfg != null) {
				// 如果活动正在开着 查询数据库有没有活动信息
				List<ExpeditionArmoryEntity> expeditionArmoryEntitys = DBManager.getInstance().query("from ExpeditionArmoryEntity where stageId = ?", activityTimeCfg.getStageId());
				ExpeditionArmoryEntity expeditionArmoryEntity = null;
				if (expeditionArmoryEntitys.size() > 0) {
					expeditionArmoryEntity = expeditionArmoryEntitys.get(0);
				}
				if (expeditionArmoryEntity == null) {
					// 创建活动信息
					expeditionArmoryEntity = createExpeditionArmoryEntity(activityTimeCfg.getStageId());
				}
				this.currentActiveExpeditionArmory = expeditionArmoryEntity;

				// 拼装数据库已有的活动信息
				List<ActivityEntity<ExpeditionArmoryStatus>> activityEntities = DBManager.getInstance().query("from ActivityEntity where activityId = ? and stageId = ?", activityId, activityTimeCfg.getStageId());
				for (ActivityEntity<ExpeditionArmoryStatus> activityEntity : activityEntities) {
					this.expeditionArmorySet.add(activityEntity.getActivityStatus(ExpeditionArmoryStatus.class));
				}
				refreshRank();

			}
		}
	}

	@Override
	public boolean onTick() {
		if (++tickIndex % 100 == 0) {
			
			boolean isActivityRunning = false;
			ActivityItem activityItem = ActivityManager.getInstance().getActiveActivityItem(activityId);
			if (activityItem != null) {
				ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeToEndCfg(activityId);
				if (activityTimeCfg != null) {
					// 活动正在进行中		
					if(tickIndex % 3600 == 0)
					{
						PlatformService.getInstance().upServerActivityScore(currentActiveExpeditionArmory.getTotalStageExp());
					}
								
					isActivityRunning = true;
					if (getCurrentActiveExpeditionArmory() == null) {
						currentActiveExpeditionArmory = createExpeditionArmoryEntity(activityTimeCfg.getStageId());
					}

					// 系统每隔一段时间自动增加阶段经验
					int stage = currentActiveExpeditionArmory.getCurDonateStage();
					int addExp = SysBasicCfg.getInstance().getExpeditionArmorySysAddExp();
					int startState = SysBasicCfg.getInstance().getExpeditionArmoryAutoAddExpStage();

					if (stage >= startState && stage < maxDonateStageCfg.getStage()
							&& GuaJiTime.getSeconds() >= currentActiveExpeditionArmory.getNextSysAutoAddStageExpTime()
							&& SysBasicCfg.getInstance().getOpenExpeditionArmoryAutoAddExp() > 0) {
						do {
							// 下个阶段还需贡献
							int leftExp = currentActiveExpeditionArmory.calcLeftExp();
							if (leftExp < 0) {
								leftExp = 0;
							}
							// 实际增加贡献
							int actualAddExp = Math.min(addExp, leftExp);
							addExp -= actualAddExp;
							if (actualAddExp == leftExp) {
								int curDonateStage = getCurDonateStage();
								// 发放阶段奖励
								logger.info("onTick() 发放阶段奖励: stage=" + curDonateStage + " 总参与人数：" + expeditionArmorySet.size());
								grantStageAwards(curDonateStage);
								// 阶段切换广播
								String chatWorldMsg = String.format(SysBasicCfg.getInstance().getExpeditionArmoryStageChangeMsg(), curDonateStage);
								GsApp.getInstance().broadcastChatWorldMsg(chatWorldMsg, chatWorldMsg);
							}
							// 增加服务器活动贡献
							currentActiveExpeditionArmory.increaseExp(actualAddExp);
						} while (addExp > 0);         

						int nextTime = SysBasicCfg.getInstance().getExpeditionArmoryAutoAddExpTime() + GuaJiTime.getSeconds();
						currentActiveExpeditionArmory.setNextSysAutoAddStageExpTime(nextTime);
						currentActiveExpeditionArmory.notifyUpdate();
					}
				}
			}

			// 活动关闭,发奖励
			if (!isActivityRunning && getCurrentActiveExpeditionArmory() != null) {

				if (!currentActiveExpeditionArmory.isGrantRank()) {
					// 设置排行奖励已发放
					currentActiveExpeditionArmory.setGrantRank(true);
					// 排名定格
					refreshRank();
					// 发排行奖励
					grantRankAwards();
					// 告知所有玩家活动状态切换
					Msg msg = Msg.valueOf(GsConst.MsgType.EXPEDITION_ARMORY_STATUS_CHANGE);
					GsApp.getInstance().broadcastMsg(msg, GsApp.getInstance().getObjMan(GsConst.ObjType.PLAYER));
				}
				// stopActivity();
			}

			ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);

			if (activityTimeCfg == null && getCurrentActiveExpeditionArmory() != null) {
				stopActivity();
			}
		}

		return true;
	}

	@Override
	public boolean onMessage(Msg msg) {
		if (msg.getMsg() == GsConst.MsgType.EXPEDITION_ARMORY_EXP_ADD) {
			// 远征物资经验叠加
			if (msg.getParams().size() < 2) {
				return true;
			}
			int addExp = msg.getParam(0);
			Player player = msg.getParam(1);

			if (addExp <= 0) {
				return true;
			}

			do {
				// 下个阶段还需贡献
				int leftExp = currentActiveExpeditionArmory.calcLeftExp();
				if (leftExp < 0) {
					leftExp = 0;
				}
				// 实际增加贡献
				int actualAddExp = Math.min(addExp, leftExp);
				addExp -= actualAddExp;
				// 必须先增加玩家贡献
				addActivityExp(player, actualAddExp);

				if (actualAddExp == leftExp) {
					int curDonateStage = getCurDonateStage();
					// 发放阶段奖励
					logger.info("onMessage() 发放阶段奖励: stage=" + curDonateStage + " 总参与人数：" + expeditionArmorySet.size());
					grantStageAwards(curDonateStage);
					// 阶段切换广播
					String chatWorldMsg = ChatManager.getMsgJson(SysBasicCfg.getInstance().getExpeditionArmoryStageChangeMsg(), curDonateStage);
					GsApp.getInstance().broadcastChatWorldMsg(chatWorldMsg, chatWorldMsg);
				}
				// 后增加服务器活动贡献
				currentActiveExpeditionArmory.increaseExp(actualAddExp);
			} while (addExp > 0);

			return true;
		}
		return false;
	}

	/**
	 * 获取远征活动信息
	 * 
	 * @return
	 */
	public ExpeditionArmoryEntity getCurrentActiveExpeditionArmory() {
		return currentActiveExpeditionArmory;
	}

	/**
	 * 获取本期活动当前捐献阶段
	 * 
	 * @return
	 */
	public int getCurDonateStage() {
		return currentActiveExpeditionArmory.getCurDonateStage();
	}

	/**
	 * 创建活动信息
	 * 
	 * @param stageId
	 */
	private ExpeditionArmoryEntity createExpeditionArmoryEntity(int stageId) {
		ExpeditionArmoryEntity expeditionArmoryEntity = new ExpeditionArmoryEntity();
		// 活动开放期号
		expeditionArmoryEntity.setStageId(stageId);
		DBManager.getInstance().create(expeditionArmoryEntity);
		return expeditionArmoryEntity;
	}

	/**
	 * 活动关闭
	 * 
	 * @param
	 */
	private void stopActivity() {
		// 需要清空排名
		this.expeditionArmorySet.clear();
		// 对象值为NULL
		this.currentActiveExpeditionArmory = null;
	}

	/**
	 * 获得前N名
	 * 
	 * @return
	 */
	public List<ExpeditionArmoryRankItem.Builder> getTop(int topN) {
		int rank = 1;
		List<ExpeditionArmoryRankItem.Builder> list = new LinkedList<ExpeditionArmoryRankItem.Builder>();
		for (ExpeditionArmoryStatus status : this.expeditionArmorySet) {
			if (rank <= topN) {
				ExpeditionArmoryRankItem.Builder item = ExpeditionArmoryRankItem.newBuilder();
				item.setPlayerId(status.getPlayerId());
				item.setName(status.getPlayerName());
				item.setExp(status.getTotalExp());
				item.setRank(rank);
				list.add(item);
			}
			rank++;
		}
		return list;
	}

	private void checkGrantPersonStageAwards(Player player, ExpeditionArmoryStatus activityStatus) {
		if (activityStatus.getTakeLastStageAwardFlag() != 0)// 获得过
		{
			return;
		}

		int stage = getCurDonateStage();

		// add by callan 不考虑结构
		if (stage == maxDonateStageCfg.getStage()) {

			int stageExp = activityStatus.getStageExp(stage);

			ExpeditionArmoryStageCfg stageCfg = ConfigManager.getInstance().getConfigByKey(ExpeditionArmoryStageCfg.class, stage);

			if (stageExp >= stageCfg.getAwardsNeedExp()) {
				// 发阶段奖励邮件
				AwardItems awardItems = AwardItems.valueOf(stageCfg.getFinishAwards());
				MailManager.createMail(activityStatus.getPlayerId(), MailType.Reward_VALUE, GsConst.MailId.EXPEDITION_ARMORY_STAGE_AWARD, "远征物资活动阶段奖励",
						awardItems, String.valueOf(stage));
				activityStatus.setTakeLastStageAwardFlag(1);

			}
		}

		return;
	}

	/**
	 * 增加玩家活动经验
	 * 
	 * @param player
	 */
	private void addActivityExp(Player player, int addExp) {
		if (player != null && addExp > 0) {
			int activityStageId = this.currentActiveExpeditionArmory.getStageId();
			ExpeditionArmoryStatus activityStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityStageId,
					ExpeditionArmoryStatus.class);
			activityStatus.setPlayerId(player.getId());
			activityStatus.setPlayerName(player.getName());
			this.expeditionArmorySet.remove(activityStatus);
			activityStatus.addExp(getCurDonateStage(), addExp);
			this.expeditionArmorySet.add(activityStatus);
			refreshRank();
			checkGrantPersonStageAwards(player, activityStatus);
			player.getPlayerData().updateActivity(activityId, activityStageId);
		}
	}

	/**
	 * 注册个人活动数据
	 */
	public void registerPersonalStatus(Player player, ExpeditionArmoryStatus status) {
		if (!expeditionArmorySet.contains(status) && status.getTotalExp() > 0 && player.getId() > 0) {
			status.setPlayerId(player.getId());
			status.setPlayerName(player.getName());
			expeditionArmorySet.add(status);
			refreshRank();
			int activityStageId = this.currentActiveExpeditionArmory.getStageId();
			player.getPlayerData().updateActivity(activityId, activityStageId);
		}
	}

	/**
	 * 刷新活动排行
	 * 
	 * @return
	 */
	public void refreshRank() {
		int rank = 1;
		Iterator<ExpeditionArmoryStatus> iter = expeditionArmorySet.iterator();
		while (iter.hasNext()) {
			ExpeditionArmoryStatus status = iter.next();
			if (status.getPlayerId() != 0 && status.getTotalExp() > 0) {
				status.setRank(rank);
				rank++;
			} else {
				// 清除不良数据
				iter.remove();
			}
		}
	}

	/**
	 * 发阶段奖励
	 */
	public void grantStageAwards(int stage) {
		ExpeditionArmoryStageCfg stageCfg = ConfigManager.getInstance().getConfigByKey(ExpeditionArmoryStageCfg.class, stage);
		logger.info("发放阶段奖励: awardsNeedExp:" + stageCfg.getAwardsNeedExp());
		for (ExpeditionArmoryStatus status : expeditionArmorySet) {
			int stageExp = status.getStageExp(stage);
			logger.info("发放阶段奖励: playerId=" + status.getPlayerId() + " stageExp：" + stageExp);
			if (stageExp >= stageCfg.getAwardsNeedExp()) {
				// 发阶段奖励邮件
				AwardItems awardItems = AwardItems.valueOf(stageCfg.getFinishAwards());
				MailManager.createMail(status.getPlayerId(), MailType.Reward_VALUE, GsConst.MailId.EXPEDITION_ARMORY_STAGE_AWARD, "远征物资活动阶段奖励", awardItems,
						String.valueOf(stage));
			}
		}
	}

	/**
	 * 发放排名奖励
	 */
	public void grantRankAwards() {
		for (ExpeditionArmoryStatus status : expeditionArmorySet) {
			int rank = status.getRank();
			ExpeditionArmoryRankingCfg rankCfg = ActivityUtil.getExpeditionArmoryRankingCfgByRank(rank);
			if (rankCfg != null) {
				// 发阶段奖励邮件
				AwardItems awardItems = AwardItems.valueOf(rankCfg.getAwards());
				MailManager.createMail(status.getPlayerId(), MailType.Reward_VALUE, GsConst.MailId.EXPEDITION_ARMORY_RANK_AWARD, "远征物资活动排名奖励", awardItems, 	String.valueOf(rank));
			}
		}
	}
}
