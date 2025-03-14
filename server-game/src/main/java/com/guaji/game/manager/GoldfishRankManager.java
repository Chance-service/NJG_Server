package com.guaji.game.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.msg.Msg;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.config.ActivityCfg.ActivityItem;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.GoldfishRankRewardCfg;
import com.guaji.game.entity.ActivityEntity;
import com.guaji.game.entity.GoldfishRewardEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.module.activity.goldfish.GoldfishStatus;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.CatchFish.RankMessage;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Mail.MailType;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;

/**
 * 捞鱼活动积分排行管理
 */
public class GoldfishRankManager extends AppObj {

	/**
	 * 活动ID
	 */
	private final int activityId = Const.ActivityId.GOLD_FISH_VALUE;

	/**
	 * 模块Tick周期
	 */
	private int tickIndex = 0;

	/**
	 * 当前活动对应的捞鱼奖励发放数据
	 */
	private GoldfishRewardEntity rewardEntity = null;
	
	/**
	 * 所有玩家活动数据及排行
	 */
	private List<GoldfishStatus> rankList;
	
	/**
	 * 全局对象, 便于访问
	 */
	private static GoldfishRankManager instance = null;

	/**
	 * 获取全局实例对象
	 */
	public static GoldfishRankManager getInstance() {
		return instance;
	}

	public GoldfishRankManager(GuaJiXID xid) {

		super(xid);
		rankList = new ArrayList<GoldfishStatus>();
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
				List<GoldfishRewardEntity> _rewardEntity = DBManager.getInstance().query("from GoldfishRewardEntity where stageId = ?", timeConfig.getStageId());
				if (null != _rewardEntity && _rewardEntity.size() > 0) {
					rewardEntity = _rewardEntity.get(0);
				}
				// 创建活动信息
				if (rewardEntity == null) {
					rewardEntity = this.createEntity(timeConfig.getStageId());
				}
				// 排行数据加载
				List<ActivityEntity<GoldfishStatus>> rankEntity = DBManager.getInstance().query("from ActivityEntity where activityId = ? and stageId = ?", activityId, timeConfig.getStageId());
				if (null != rankEntity && rankEntity.size() > 0) {
					for (ActivityEntity<GoldfishStatus> activityEntity : rankEntity) {
						this.rankList.add(activityEntity.getActivityStatus(GoldfishStatus.class));
					}
				}
				this.refreshRank();
			}
		}
	}

	@Override
	public boolean onTick() {
		
		if (++tickIndex % 100 == 0) {
			
			boolean isRunning = false;
			ActivityItem activityItem = ActivityManager.getInstance().getActiveActivityItem(activityId);
			ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeToEndCfg(activityId);
			if (activityItem != null) {
				if (timeConfig != null) {
					// 活动正在进行中
					isRunning = true;
					// 创建活动信息
					if (rewardEntity == null) {
						rewardEntity = this.createEntity(timeConfig.getStageId());
					}
				}
			}
			// 活动关闭,发奖励
			if (!isRunning && null != this.rewardEntity) {
				if (!this.rewardEntity.isSendReward()) {
					// 排名定格
					this.refreshRank();
					// 发送排行奖励
					this.sendRankAwards();
					// 更新数据
					this.rewardEntity.setSendReward(true);
					this.rewardEntity.notifyUpdate(true);
				}
			}
		}
		return true;
	}
	
	@Override
	public boolean onMessage(Msg msg) {
		
		if (msg.getMsg() == GsConst.MsgType.GOLDFISH_ADD_SCORE) {

			if (msg.getParams().size() < 2) {
				return true;
			}
			int score = msg.getParam(0);
			Player player = msg.getParam(1);
			// 积分不得小于0
			if (score <= 0) {
				return true;
			}
			this.addScore(player, score);
		}
		return true;
	}

	/**
	 * 创建活动信息
	 * 
	 * @param stageId
	 */
	private GoldfishRewardEntity createEntity(int stageId) {

		GoldfishRewardEntity rewardEntity = new GoldfishRewardEntity();
		// 活动开放期号
		rewardEntity.setStageId(stageId);
		DBManager.getInstance().create(rewardEntity);
		return rewardEntity;
	}
	
	/**
	 * 刷新活动排行
	 * 
	 * @return
	 */
	public void refreshRank() {
		
		synchronized (this.rankList) {
			
			Collections.sort(rankList);
			int rank = 1;
			Iterator<GoldfishStatus> iterator = this.rankList.iterator();
			while (iterator.hasNext()) {
				GoldfishStatus status = iterator.next();
				if (status.getPlayerId() != 0 && status.getScore() > 0) {
					status.setRank(rank);
					rank++;
				} else {
					iterator.remove();
				}
			}
		}
	}
	
	/**
	 * 获得前N名数据构建
	 * 
	 * @param topNumber
	 * @return
	 */
	public List<RankMessage.Builder> getRankTop(int topNumber) {

		synchronized (this.rankList) {
			int number = 1;
			List<RankMessage.Builder> list = new LinkedList<RankMessage.Builder>();
			for (GoldfishStatus status : this.rankList) {
				if (number <= topNumber) {
					RankMessage.Builder builder = RankMessage.newBuilder();
					builder.setRank(status.getRank());
					builder.setName(status.getName());
					builder.setScore(status.getScore());
					builder.setPlayerId(status.getPlayerId());
					list.add(builder);
				} else {
					break;
				}
				number++;
			}
			return list;
		}
	}

	/**
	 * 增加玩家活动积分
	 * 
	 * @param score
	 * @param player
	 */
	private void addScore(Player player, int score) {
		
		synchronized (this.rankList) {
			if (player != null && score > 0) {
				boolean isRegister = true;
				Iterator<GoldfishStatus> iterator = this.rankList.iterator();
				while (iterator.hasNext()) {
					GoldfishStatus status = iterator.next();
					if (status.getPlayerId() == player.getId()) {
						status.setScore(score);
						isRegister = false;
						break;
					}
				}
				// 新用户参与
				if (isRegister) {
					GoldfishStatus status = new GoldfishStatus();
					status.setPlayerId(player.getId());
					status.setName(player.getName());
					status.setScore(score);
					this.rankList.add(status);
				}
			}
		}
		// 更新排行榜
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.GOLDFISH_SCORE, Params.valueOf("after", score));
	}

	/**
	 * 发放排名奖励
	 */
	private void sendRankAwards() {

		List<GoldfishRankRewardCfg> rewardList = ConfigManager.getInstance().getConfigList(GoldfishRankRewardCfg.class);
		for (GoldfishStatus status : this.rankList) {
			for (GoldfishRankRewardCfg rewardCfg : rewardList) {
				if (status.getRank() <= rewardCfg.getMinRank() && status.getRank() > 0) {
					// 发阶段奖励邮件
					AwardItems awardItems = AwardItems.valueOf(rewardCfg.getAwards());
					MailManager.createMail(status.getPlayerId(), MailType.Reward_VALUE, GsConst.MailId.GOLDFISH_RANK_AWARD, "捞鱼活动排名奖励", awardItems, String.valueOf(status.getRank()));
					break;
				}
			}
		}
	}
	
}
