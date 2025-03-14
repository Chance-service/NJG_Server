package com.guaji.game.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.msg.Msg;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.config.ActivityCfg.ActivityItem;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.RankAward193Cfg;
import com.guaji.game.config.SeasonTowerRankAwardCfg;
import com.guaji.game.config.SeasonTowerTimeCfg;
import com.guaji.game.entity.ActivityEntity;
import com.guaji.game.entity.SeasonTowerRewardEntity;
import com.guaji.game.entity.SingleBossRewardEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.module.activity.activity194.Activity194Status;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity5.SingleBossRankMessage;
import com.guaji.game.protocol.Activity6.SeasonTowerRankInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Mail.MailType;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;

/**
 * 捞鱼活动积分排行管理
 */
public class SeasonTowerRankManager extends AppObj {

	/**
	 * 活动ID
	 */
	private final int activityId = Const.ActivityId.ACTIVITY194_SeasonTower_VALUE;

	/**
	 * 模块Tick周期
	 */
	private int tickIndex = 0;

	/**
	 * 当前活动对应的捞鱼奖励发放数据
	 */
	private SeasonTowerRewardEntity rewardEntity = null;
	
	/**
	 * 所有玩家活动数据及排行
	 */
	private List<Activity194Status> rankList;
	
	/**
	 * 目前爬塔期數
	 */
	private int timeIdx = 0;
	
	/**
	 * 全局对象, 便于访问
	 */
	private static SeasonTowerRankManager instance = null;

	/**
	 * 获取全局实例对象
	 */
	public static SeasonTowerRankManager getInstance() {
		return instance;
	}

	public SeasonTowerRankManager(GuaJiXID xid) {

		super(xid);
		rankList = new ArrayList<Activity194Status>();
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
			timeIdx = SeasonTowerTimeCfg.getValidTimeIdx();
			if ((timeConfig != null)&&(timeIdx > 0)) {
				// 如果活动正在开着 查询数据库有没有活动信息
				List<SeasonTowerRewardEntity> _rewardEntity = DBManager.getInstance().query("from SeasonTowerRewardEntity where timeIdx = ?", timeIdx);
				if (null != _rewardEntity && _rewardEntity.size() > 0) {
					rewardEntity = _rewardEntity.get(0);
				}
				
				
				// 创建活动信息
				if (rewardEntity == null) {
					rewardEntity = this.createEntity(timeIdx);
				}
				// 排行数据加载
				List<ActivityEntity<Activity194Status>> rankEntity = DBManager.getInstance().query("from ActivityEntity where activityId = ? and stageId = ?", activityId, timeConfig.getStageId());
				if (null != rankEntity && rankEntity.size() > 0) {
					for (ActivityEntity<Activity194Status> activityEntity : rankEntity) {
						Activity194Status status = activityEntity.getActivityStatus(Activity194Status.class);
						if ((status.getTimeIndex() == timeIdx)&&(status.getDoneFloor() > 1 )&&(status.getDoneTime()>0))  {
							status.setNowfloor(status.getDoneFloor());
							this.rankList.add(status);
						}
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
			int NowtimeIdx = SeasonTowerTimeCfg.getValidTimeIdx();
			
			if (NowtimeIdx != -1) {
				if (NowtimeIdx != timeIdx) {
					synchronized (this.rankList) {
						this.rankList.clear();
					}
					timeIdx = NowtimeIdx;
				}
			}
			
			if (activityItem != null) {
				if (timeConfig != null) {
					if (timeIdx > 0) {
						// 活动正在进行中
						isRunning = true;
						// 创建活动信息
						if (rewardEntity.getTimeIdx() != timeIdx) {
							// 如果活动正在开着 查询数据库有没有活动信息
							rewardEntity = null;
							List<SeasonTowerRewardEntity> _rewardEntity = DBManager.getInstance().query("from SeasonTowerRewardEntity where timeIdx = ?", timeIdx);
							if (null != _rewardEntity && _rewardEntity.size() > 0) {
								rewardEntity = _rewardEntity.get(0);
								rewardEntity.setSendReward(false);
							}
							// 创建活动信息
							if (rewardEntity == null) {
								rewardEntity = this.createEntity(timeIdx);
							}
							
						} 
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
		
		if (msg.getMsg() == GsConst.MsgType.SEASON_TOWER_ACTIVITY) {

			if (msg.getParams().size() < 3) {
				return true;
			}
			int floor = msg.getParam(0);
			Player player = msg.getParam(1);
			long doneTime = msg.getParam(2);
			// 樓層不得小于1
			if (floor <= 1) {
				return true;
			}
			this.addFloor(player, floor,doneTime);
		}
		return true;
	}

	/**
	 * 创建活动信息
	 * 
	 * @param stageId
	 */
	private SeasonTowerRewardEntity createEntity(int timeIdx) {

		SeasonTowerRewardEntity rewardEntity = new SeasonTowerRewardEntity();
		// 活动开放期号
		rewardEntity.setTimeIdx(timeIdx);
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
			Iterator<Activity194Status> iterator = this.rankList.iterator();
			while (iterator.hasNext()) {
				Activity194Status status = iterator.next();
				if (status.getPlayerId() != 0 && status.getNowfloor() > 1) {
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
	public List<SeasonTowerRankInfo.Builder> getRankTop(int topNumber) {

		synchronized (this.rankList) {
			int number = 1;
			List<SeasonTowerRankInfo.Builder> list = new LinkedList<SeasonTowerRankInfo.Builder>();
			for (Activity194Status status : this.rankList) {
				if (number <= topNumber) {
					SeasonTowerRankInfo.Builder builder = SeasonTowerRankInfo.newBuilder();
					builder.setRank(status.getRank());
					builder.setName(status.getName());
					builder.setMaxFloor(status.getNowfloor());
					builder.setDoneTime(status.getDoneTime());
					
					//builder.setPlayerId(status.getPlayerId());
					PlayerSnapshotInfo.Builder snapshot = SnapShotManager.getInstance().getPlayerSnapShot(status.getPlayerId());
					if (snapshot != null) {
						builder.setHeadIcon(snapshot.getPlayerInfo().getHeadIcon());
						//builder.setSkin(snapshot.getPlayerInfo());
					} else {
						builder.setHeadIcon(1);
						//builder.setSkin(0);
					}
					
					
					list.add(builder);
				} else {
					break;
				}
				number++;
			}
			return list;
		}
	}
	
	public Integer getPlayerRank(int playerId) {
		Map<Integer, Integer> everyRankNumMap = new HashMap<Integer, Integer>();
		int rankNum = 0;
		synchronized (this.rankList) {
			for (Activity194Status status : this.rankList) {

				rankNum = everyRankNumMap.size() + 1;
				
				everyRankNumMap.put(status.getPlayerId(), rankNum);
			}
			
			if (everyRankNumMap.containsKey(playerId)) {
				return everyRankNumMap.get(playerId);
			} else {
				return 0;
			}
		}
	}
	
	/**
	 * 取得排行榜資料
	 * @param playerId
	 * @return
	 */
	public Activity194Status getPlayerStatus(int playerId) {
		synchronized (this.rankList) {
			for (Activity194Status status : this.rankList) {
				if (status.getPlayerId() == playerId) {
					return status;
				}
			}
		}
		return null;
	}

	/**
	 * 增加玩家活动积分
	 * 
	 * @param score
	 * @param player
	 */
	private void addFloor(Player player, int floor , long doneTime) {
		
		synchronized (this.rankList) {
			if (player != null && floor > 1) {
				boolean isRegister = true;
				Iterator<Activity194Status> iterator = this.rankList.iterator();
				while (iterator.hasNext()) {
					Activity194Status status = iterator.next();
					if (status.getPlayerId() == player.getId()) {
						status.setNowfloor(floor);
						status.setDoneTime(doneTime);
						isRegister = false;
						break;
					}
				}
				 //新用户参与
				if (isRegister) {
					Activity194Status status = new Activity194Status();
					status.setPlayerId(player.getId());
					status.setName(player.getName());
					status.setNowfloor(floor);
					status.setDoneTime(doneTime);
					this.rankList.add(status);
				}
			}
		}
		// 更新排行榜
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.SEASON_TOWER_UPDATE_FLOOR, Params.valueOf("floor", floor));
	}

	/**
	 * 发放排名奖励
	 */
	private void sendRankAwards() {

		List<SeasonTowerRankAwardCfg> rewardList = ConfigManager.getInstance().getConfigList(SeasonTowerRankAwardCfg.class);
		for (Activity194Status status : this.rankList) {
			for (SeasonTowerRankAwardCfg rewardCfg : rewardList) {
				if (status.getRank() <= rewardCfg.getMinRank() && status.getRank() > 0) {
					// 发阶段奖励邮件
					AwardItems awardItems = AwardItems.valueOf(rewardCfg.getTotalAward());
					MailManager.createMail(status.getPlayerId(), MailType.Reward_VALUE, GsConst.MailId.SEASON_TOWER_MAIL, "季爬塔活动排名奖励", awardItems, String.valueOf(status.getRank()));
					break;
				}
			}
		}
	}
		
}
