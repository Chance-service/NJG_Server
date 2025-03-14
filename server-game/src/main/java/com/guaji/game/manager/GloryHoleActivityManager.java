package com.guaji.game.manager;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.guaji.app.AppObj;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;
import org.guaji.xid.GuaJiXID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guaji.game.ServerData;
import com.guaji.game.config.ActivityCfg.ActivityItem;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ReleaseGHRank175AwardCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.ActivityEntity;
import com.guaji.game.entity.GhRankActivityEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.manager.status.GloryHoleStatus;
import com.guaji.game.module.activity.activity175.Activity175Rank;
import com.guaji.game.module.activity.activity175.Activity175Status;
import com.guaji.game.protocol.Activity5.Activity175RankItem;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Mail;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsConst.GloryHoleAwardType;
import com.guaji.game.util.GsConst.GloryHoleGameTime;
import com.guaji.game.util.GsConst.GloryHoleRankType;

/**
 * 壁尻排行管理
 */
public class GloryHoleActivityManager extends AppObj {

	private final int maxRewardNum = ReleaseGHRank175AwardCfg.getMaxRewardNum(); //100
	private final int maxDisplayNum = 10;

	/**
	 * 日志记录器
	 */
	private static Logger logger = LoggerFactory.getLogger("Server");

	/**
	 * 活动ID
	 */
	private final int activityId = Const.ActivityId.ACTIVITY175_Glory_Hole_VALUE;

	/**
	 * 模块Tick周期
	 */
	private int tickIndex = 0;
	
	/**
	 * 上次刷新隊伍分數時間
	 */
	private long lastFreshTime = 0;
	/**
	 * 当前活动对应的奖励发放数据
	 */
	private GhRankActivityEntity ghRankctivityEntity = null;
	/**
	 * 計算時間用
	 */
	private long lastTickTime = 0;

//	/**
//	 * 当前活动对应的奖励发放数据
//	 */
//	private UrRankHistoryEntity urRankHistoryEntity = null;

	/**
	 * 所有玩家積分
	 */
	private List<Activity175Rank> rankList = new LinkedList<Activity175Rank>();
	/**
	 * 歷史最高積分
	 */
	private List<Activity175Rank> MaxList = new LinkedList<Activity175Rank>();
	
	/**
	 * 隊伍Max積分
	 */
	private Map<Integer,Integer> TeamMaxMap = new ConcurrentHashMap<Integer,Integer>();
	
	/**
	 * 各隊伍玩家ID
	 */
	private Map<Integer,Integer> TeamPlayerMap = new ConcurrentHashMap<Integer,Integer>();

	/**
	 * 所有玩家活动数据及排行
	 */
	private Map<Integer, Integer> allPlayerIdRankMap = new ConcurrentHashMap<Integer, Integer>();
	
	private Map<Integer, Integer> allMaxRankMap = new ConcurrentHashMap<Integer, Integer>();
	
	
	private static int joinCount = 0;

	/**
	 * 所有玩家历史抽卡积分
	 */
	//private List<Activity175Rank> rankHistoryList = new LinkedList<Activity175Rank>();

	/**
	 * 历史数据及排行
	 */
	//private Map<Integer, Activity175Rank> allPlayerIdRankHistoryMap = new ConcurrentHashMap<Integer, Activity175Rank>();

	/**
	 * 全局对象, 便于访问
	 */
	private static GloryHoleActivityManager instance = null;
	
	/**
	 * 获取全局实例对象
	 */
	public static GloryHoleActivityManager getInstance() {
		return instance;
	}

	public GloryHoleActivityManager(GuaJiXID xid) {

		super(xid);

		if (instance == null) {
			//rankList = new ArrayList<>();
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
			if (timeConfig != null && (!timeConfig.isEnd())) {
				// 排行数据加载
				countScore(timeConfig,getGloryHoleStartTime());
				// 發獎標記加載
				this.loadAwardSign();
				
				GloryHoleStatus gloryholeStatus = ServerData.getInstance().getServerStatus(GsConst.ServerStatusId.GLORYHOLE,
						GloryHoleStatus.class);
				if (gloryholeStatus != null) {
					joinCount = gloryholeStatus.getJoinCount();
				}
			}
		}
	}
	

/**
 * 重新計算積分排名
 * @param timeConfig
 * @param countTime (只計算這時段資訊)
 */
	public void countScore(ActivityTimeCfg timeConfig,long countTime) {
		// 排行数据加载
		List<ActivityEntity<Activity175Status>> rankEntity = DBManager.getInstance().query(
				"from ActivityEntity where activityId = ? and stageId = ?", activityId,
				timeConfig.getStageId());
		List<Activity175Rank> allRankList = new LinkedList<Activity175Rank>();
		//List<Activity175Rank> allMaxList = new LinkedList<Activity175Rank>();
		boolean iscount = true;
		if (null != rankEntity && rankEntity.size() > 0) {
			Activity175Rank item = null;
			for (ActivityEntity<Activity175Status> activityEntity : rankEntity) {
				Activity175Status status = activityEntity.getActivityStatus(Activity175Status.class);
				if (status != null && status.getTeam() != 0 && GuaJiTime.isSameDay(status.getJoinTime(),countTime)) {
					iscount = GuaJiTime.isSameDay(status.getScoreTime(),countTime);
					if ((status.getScore() > 0)) {
						if 	(iscount) { // 只計算該天活動的人
							item = new Activity175Rank(activityEntity.getPlayerId(), status.getScore());
							allRankList.add(item);
							int TeamId = status.getTeam();
							
							if (TeamMaxMap.containsKey(TeamId)) {
								TeamMaxMap.replace(TeamId,TeamMaxMap.get(TeamId)+status.getScore());
							} else {
								TeamMaxMap.put(TeamId, status.getScore());
							}
						}
					}
					// 計算隊伍總得分
//					if (status.getMaxScore() > 0) {
//						
//						int TeamId = status.getTeam();
//						
//						if (TeamMaxMap.containsKey(TeamId)) {
//							TeamMaxMap.replace(TeamId,TeamMaxMap.get(TeamId)+status.getMaxScore());
//						} else {
//							TeamMaxMap.put(TeamId, status.getMaxScore());
//						}
//												
//						item = new Activity175Rank(activityEntity.getPlayerId(), status.getMaxScore());
//						allMaxList.add(item);
//					}
					
					if (!TeamPlayerMap.containsKey(activityEntity.getPlayerId())){
						TeamPlayerMap.put(activityEntity.getPlayerId(), status.getTeam());
					} else {
						TeamPlayerMap.replace(activityEntity.getPlayerId(), status.getTeam());
					}
				}
			}
		}
				
		// 排行
		Collections.sort(allRankList);
		//Collections.sort(allMaxList);
		
		// 将排行数据存放到全局变量
		this.rankList.addAll(allRankList);
		int index = 0;
		for (Activity175Rank item : this.rankList) {
			allPlayerIdRankMap.put(item.getPlayerId(), index);
			index++;
		}
		
//		this.MaxList.addAll(allMaxList);
//		index = 0;
//		for (Activity175Rank item : this.MaxList) {
//			allMaxRankMap.put(item.getPlayerId(), index);
//			index++;
//		}
	}
	
	/**
	 * 	載入發獎時間標記
	 */
	public void loadAwardSign() {

		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			return;
		}
		
		// 發獎標記加载
		List<GhRankActivityEntity> RankEntityList = DBManager.getInstance()
				.query("from GhRankActivityEntity where invalid = 0");

		if (RankEntityList != null && RankEntityList.size() != 0) {
			ghRankctivityEntity = RankEntityList.get(0);
			Log.logPrintln("GloryHole lastaward time ms: " + this.ghRankctivityEntity.getRefreshTime());
		}
		
		Date startDate = new Date(getGloryHoleStartTime());
		
		Log.logPrintln("GloryHole Start Date s: " + startDate);
		

		if (timeCfg.isEnd()) {
			this.ghRankctivityEntity = null;
		} else {
			if (this.ghRankctivityEntity == null) {
				this.ghRankctivityEntity = new GhRankActivityEntity();
				long refreshTime = 0;
				this.ghRankctivityEntity.setRefreshTime(refreshTime);
				DBManager.getInstance().create(ghRankctivityEntity);
				Log.logPrintln("GloryHole lastaward time ms: " + this.ghRankctivityEntity.getRefreshTime());
			}	
		}
	}

	/**
	 * 获得前N名数据构建
	 *
	 * @param topNumber
	 * @return
	 */
	public List<Activity175RankItem.Builder> getRankTop(int kind) {

		Map<Integer, Integer> everyRankRepeatMap = new HashMap<Integer, Integer>();
		List<Integer> scoreList = new LinkedList<Integer>();
		int totalNum = 0;
		int rankNum = 1;
		List<Activity175RankItem.Builder> list = new LinkedList<>();
		
		List<Activity175Rank> alist = kind == GloryHoleRankType.DAILY ? this.rankList : this.MaxList;
				
		for (int i = 0; i < alist.size(); i++) {
			if (alist.get(i).getScore() == 0)
				continue;
			if (totalNum > maxDisplayNum) {
				break;
			}
			if (!scoreList.contains(alist.get(i).getScore())) {
				scoreList.add(alist.get(i).getScore());
				rankNum = everyRankRepeatMap.size() + 1;
			}

			everyRankRepeatMap.put(alist.get(i).getPlayerId(), rankNum);
			totalNum++;
		}

		int ascore = 0;
		for (Integer key : everyRankRepeatMap.keySet()) {
			Activity175RankItem.Builder builder = Activity175RankItem.newBuilder();
			PlayerSnapshotInfo.Builder snapshot = SnapShotManager.getInstance().getPlayerSnapShot(key);
			if (snapshot != null) {
				builder.setTeamId(getTeampPlayerId(key));
				builder.setHeaderId(snapshot.getPlayerInfo().getHeadIcon());
				builder.setName(snapshot.getMainRoleInfo().getName());
			}

			builder.setRank(everyRankRepeatMap.get(key));
			ascore = kind == GloryHoleRankType.DAILY ? getPlayerScore(key) : getPlayerMaxScore(key);
			builder.setScore(ascore);

			builder.setPlayerId(key);
			list.add(builder);

		}
		return list;
	}

	public int getPlayerScore(int playerId) {
		for (int i = 0; i < this.rankList.size(); i++) {
			if (this.rankList.get(i).getPlayerId() == playerId)
				return this.rankList.get(i).getScore();
		}
		return 0;
	}
	
	public int getPlayerMaxScore(int playerId) {
		for (int i = 0; i < this.MaxList.size(); i++) {
			if (this.MaxList.get(i).getPlayerId() == playerId)
				return this.MaxList.get(i).getScore();
		}
		return 0;
	}
	/**
	 * 取得團隊積分
	 * @return
	 */
	public Map<Integer,Integer> getTeamMaxMap(){
		return TeamMaxMap;
	}
	/**
	 * 取得玩家TeamId
	 * @param playerId
	 * @return TeamId
	 */
	public int getTeampPlayerId(int playerId) {
		if (TeamPlayerMap.containsKey(playerId)) {
			return TeamPlayerMap.get(playerId);
		}
		return 0;
	}

//	public void refreshHistoryData() {
//		if (urRankHistoryEntity != null) {
//			String rankList = urRankHistoryEntity.getRanklist();
//			this.rankHistoryList = GsonUtil.getJsonInstance().fromJson(rankList,
//					new TypeToken<List<Activity175Rank>>() {
//					}.getType());
//			for (Activity175Rank item : this.rankHistoryList) {
//				allPlayerIdRankHistoryMap.put(item.getPlayerId(), item);
//			}
//		}
//	}

//	public Activity128Rank getPlayerHistoryRank(int playerId) {
//
//		Map<Integer, Activity128Rank> everyRankNumMap = new HashMap<Integer, Activity128Rank>();
//		List<Integer> scoreList = new LinkedList<Integer>();
//		int rankNum = 0;
//		synchronized (this.rankHistoryList) {
//			Activity128Rank temp = null;
//			for (int i = 0; i < this.rankHistoryList.size(); i++) {
//				if (this.rankHistoryList.get(i).getScore() == 0)
//					continue;
//				if (!scoreList.contains(this.rankHistoryList.get(i).getScore())) {
//					scoreList.add(this.rankHistoryList.get(i).getScore());
//					rankNum = everyRankNumMap.size() + 1;
//				}
//				temp = new Activity128Rank(this.rankHistoryList.get(i).getPlayerId(),
//						this.rankHistoryList.get(i).getScore(), rankNum);
//				everyRankNumMap.put(this.rankHistoryList.get(i).getPlayerId(), temp);
//			}
//		}
//
//		if (everyRankNumMap.containsKey(playerId)) {
//			return everyRankNumMap.get(playerId);
//		} else {
//			return null;
//		}
//
//	}

//	public List<Activity128RankItem.Builder> getRankHistoryTop() {
//
//		List<Activity128RankItem.Builder> list = new LinkedList<>();
//		if (this.rankHistoryList != null) {
//			Map<Integer, Integer> everyRankRepeatMap = new HashMap<Integer, Integer>();
//			List<Integer> scoreList = new LinkedList<Integer>();
//			int totalNum = 0;
//			int rankNum = 0;
//			for (int i = 0; i < this.rankHistoryList.size(); i++) {
//				if (this.rankHistoryList.get(i).getScore() == 0)
//					continue;
//				if (totalNum > maxDisplayNum) {
//					break;
//				}
//				if (!scoreList.contains(this.rankHistoryList.get(i).getScore())) {
//
//					scoreList.add(this.rankHistoryList.get(i).getScore());
//					rankNum = everyRankRepeatMap.size() + 1;
//
//				}
//
//				everyRankRepeatMap.put(this.rankHistoryList.get(i).getPlayerId(), rankNum);
//				totalNum++;
//			}
//
//			for (Integer key : everyRankRepeatMap.keySet()) {
//				Activity128RankItem.Builder builder = Activity128RankItem.newBuilder();
//				PlayerSnapshotInfo.Builder snapshot = SnapShotManager.getInstance().getPlayerSnapShot(key);
//				if (snapshot != null) {
//					builder.setProf(snapshot.getMainRoleInfo().getProf());
//					builder.setProf(snapshot.getMainRoleInfo().getProf());
//					builder.setHeaderId(snapshot.getPlayerInfo().getHeadIcon());
//					builder.setName(snapshot.getMainRoleInfo().getName());
//				}
//
//				builder.setRank(everyRankRepeatMap.get(key));
//				builder.setScore(this.allPlayerIdRankHistoryMap.get(key).getScore());
//				builder.setPlayerId(key);
//				list.add(builder);
//			}
//		}
//		return list;
//	}

	public GhRankActivityEntity getUrRankActivityEntity() {
		return ghRankctivityEntity;
	}

//	public UrRankHistoryEntity getUrRankHistoryEntity() {
//		return urRankHistoryEntity;
//	}

	public Integer getPlayerRank(int playerId,int kind) {

		Map<Integer, Integer> everyRankNumMap = new HashMap<Integer, Integer>();
		List<Integer> scoreList = new LinkedList<Integer>();
		int rankNum = 0;
		
		// daily score
		if (kind == GloryHoleRankType.DAILY) {
			synchronized (this.rankList) {
				for (int i = 0; i < this.rankList.size(); i++) {
					if (this.rankList.get(i).getScore() == 0)
						continue;
					if (!scoreList.contains(this.rankList.get(i).getScore())) {
						scoreList.add(this.rankList.get(i).getScore());
						rankNum = everyRankNumMap.size() + 1;
					}
					everyRankNumMap.put(this.rankList.get(i).getPlayerId(), rankNum);
				}
	
			}
		}else{
			synchronized (this.MaxList) {
				for (int i = 0; i < this.MaxList.size(); i++) {
					if (this.MaxList.get(i).getScore() == 0)
						continue;
					if (!scoreList.contains(this.MaxList.get(i).getScore())) {
						scoreList.add(this.MaxList.get(i).getScore());
						rankNum = everyRankNumMap.size() + 1;
					}
					everyRankNumMap.put(this.MaxList.get(i).getPlayerId(), rankNum);
				}
	
			}
		}
		
		if (everyRankNumMap.containsKey(playerId)) {
			return everyRankNumMap.get(playerId);
		} else {
			return 0;
		}
	}
	
	@Override
	public boolean onTick() {
		ActivityItem activityItem = ActivityManager.getInstance().getActiveActivityItem(activityId);
		if (activityItem == null)
			return true;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		if ((timeConfig == null))
			return true;

		if (++tickIndex % 100 == 0) {

			if (this.ghRankctivityEntity == null)
				return true;
			
			//int days = GuaJiTime.getDaysOfWeek();
			
			if (timeConfig.isActiveToEnd()) {
				
				long TeamFreshTime = GuaJiTime.getTimeHourMinute(SysBasicCfg.getInstance().getAct175GameTime(GloryHoleGameTime.START));//GuaJiTime.setTimeHourMinute(0,0); // 今天晚上00點
				long endFreshTime = GuaJiTime.getTimeHourMinute(SysBasicCfg.getInstance().getAct175GameTime(GloryHoleGameTime.END));//GuaJiTime.setTimeHourMinute(23,00); // 今天晚上23點00
									
				// 遊戲期間
				if ((GuaJiTime.getMillisecond() >= TeamFreshTime) && (GuaJiTime.getMillisecond() < endFreshTime)) {
					long currTime = GuaJiTime.getMillisecond();
					long interval = 5*60*1000; // (進入活動時間)五分刷一次
					if (lastFreshTime == 0) { // 剛進入不刷新(清空每日)
						clearAllData();
						this.TeamPlayerMap.clear();
						lastFreshTime = currTime;
					}
					if ((currTime - lastFreshTime) >= interval) {
						clearAllData();
						countScore(timeConfig,getGloryHoleStartTime()); // rankTime 今天活動開始時間00:00
						lastFreshTime = currTime;
					}
					
				} else {
					lastFreshTime = 0;
				}

				//-----------------------------------------發獎
				long lastawardTime = this.ghRankctivityEntity.getRefreshTime();
				
				// (發放禮物時間)
				if (!GuaJiTime.isToday(lastawardTime) && rankList.size() > 0) {
						//logger.info("reset GloryHoleActivityManager rankList before");
					synchronized (this.rankList) {
						//logger.info("reset GloryHoleActivityManager rankList after");
						
						long awardTime = GuaJiTime
								.getTimeHourMinute(SysBasicCfg.getInstance().getAct175GameTime(GloryHoleGameTime.COUNT)); // 23:30

						if ((GuaJiTime.getMillisecond() >= awardTime)){ // 需檢查是不是過期的刷新時間(才發送)
							logger.info("GloryHoleActivityManager sendAwards");
							// 发奖
							this.sendRankAwards(GloryHoleRankType.DAILY);
							
							this.sendTeamAwards(timeConfig);
														
							// 更新最後一次發獎時間
							this.ghRankctivityEntity.setRefreshTime(GuaJiTime.getMillisecond());
							this.ghRankctivityEntity.notifyUpdate();
						}
					}
				}

				long clearTime = GuaJiTime.setTimeHourMinute(23,58); // 今天晚上23點58分
				long endclearTime = GuaJiTime.setTimeHourMinute(23,59); // 今天晚上23點59分
				if ((GuaJiTime.getMillisecond() >= clearTime) && (GuaJiTime.getMillisecond() <= endclearTime)) {
					clearAllData();
					this.TeamPlayerMap.clear();
				}				
			}
		}
		
		long currTime = GuaJiTime.getMillisecond();
		// 每十分鐘更新一次
		if (currTime - lastTickTime >= 600000) {
			lastTickTime = currTime;
			GloryHoleStatus gloryholeStatus = ServerData.getInstance().getServerStatus(GsConst.ServerStatusId.GLORYHOLE,
					GloryHoleStatus.class);
			if (gloryholeStatus != null) {
				if (getJoinCount() >  gloryholeStatus.getJoinCount()) {
					gloryholeStatus.setJoinCount(getJoinCount());
					ServerData.getInstance().updateServerData(GsConst.ServerStatusId.GLORYHOLE);
				}
			}
		}
		
		return true;
	}
	
	public void clearAllData() {
		this.rankList.clear();
		this.allPlayerIdRankMap.clear();
		this.MaxList.clear();
		this.allMaxRankMap.clear();
		this.TeamMaxMap.clear();
	}

//	@Override
//	public boolean onMessage(Msg msg) {
//		if (msg.getMsg() == GsConst.MsgType.GH_RANK_ADD_SCORE) {
//
//			if (msg.getParams().size() < 2) {
//				return true;
//			}
//			Player player = msg.getParam(1);
//			this.updateRankSet(player.getPlayerData(),GloryHoleRankType.DAILY);
//		}
//		
//		if (msg.getMsg() == GsConst.MsgType.GH_RANK_ADD_MAXSCORE) {
//
//			if (msg.getParams().size() < 2) {
//				return true;
//			}
//			Player player = msg.getParam(1);
//			this.updateRankSet(player.getPlayerData(),GloryHoleRankType.TOTALMAX);
//		}
//		return true;
//	}

	/**
	 * 发放排名奖励
	 */
	private void sendRankAwards(int kind) {
		Map<Integer, Integer> everyRankNumMap = new HashMap<Integer, Integer>();
		List<Integer> scoreList = new LinkedList<Integer>();
		int rankNum = 0;
		
		List<Activity175Rank> aList = kind == GloryHoleRankType.DAILY ? this.rankList : this.MaxList;
		
		for (int i = 0; i < aList.size(); i++) {
			if (aList.get(i).getScore() == 0)
				continue;
			if (!scoreList.contains(aList.get(i).getScore())) {
				scoreList.add(aList.get(i).getScore());
				rankNum = everyRankNumMap.size() + 1;
			}

			if (rankNum > maxRewardNum)
				break;

			everyRankNumMap.put(aList.get(i).getPlayerId(), rankNum);
		}
		// 发奖
		for (Integer key : everyRankNumMap.keySet()) {
			
			ReleaseGHRank175AwardCfg cfg = ReleaseGHRank175AwardCfg.getCfgbyRank(everyRankNumMap.get(key));
			if (cfg == null)
				continue;
			String awardStr = kind == GloryHoleRankType.DAILY ? cfg.getDailyAward() : cfg.getTotalAward();
			AwardItems everydayAward = AwardItems.valueOf(awardStr);
			// 发送邮件时间
			int mailId = kind == GloryHoleRankType.DAILY ? GsConst.MailId.GH_DAILY_MAIL : GsConst.MailId.GH_HIGH_SCORE_MAIL;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String date = sdf.format(GuaJiTime.getAM0Date());
			MailManager.createMail(key, Mail.MailType.Reward_VALUE, mailId, "GH积分排名奖励",
					everydayAward, date, String.valueOf(everyRankNumMap.get(key)));

		}
	}
	/**
	 * 發放團隊禮物
	 */
	private void sendTeamAwards(ActivityTimeCfg timeConfig) {
		Map<Integer,Set<Integer>> TeamPlayerMap = new HashMap<Integer,Set<Integer>>();
		// 排行数据加载
		List<ActivityEntity<Activity175Status>> rankEntity = DBManager.getInstance().query(
				"from ActivityEntity where activityId = ? and stageId = ?", activityId,
				timeConfig.getStageId());
		int TeamId = 0;
		if (null != rankEntity && rankEntity.size() > 0) {
			for (ActivityEntity<Activity175Status> activityEntity : rankEntity) {
				Activity175Status status = activityEntity.getActivityStatus(Activity175Status.class);
				if (status != null && status.getTeam() != 0 && GuaJiTime.isSameDay(status.getJoinTime(),getGloryHoleStartTime())) {
					TeamId = status.getTeam();
					if (TeamPlayerMap.containsKey(TeamId)) {
						TeamPlayerMap.get(TeamId).add(activityEntity.getPlayerId());
					} else {
						Set<Integer> aset = new HashSet<>();
						aset.add(activityEntity.getPlayerId());
						TeamPlayerMap.put(TeamId, aset);
					}
				}
			}
		}
		int TeamAScore = TeamMaxMap.containsKey(1) ? TeamMaxMap.get(1):0;
		int TeamBSocre = TeamMaxMap.containsKey(2) ? TeamMaxMap.get(2):0;
		String awardStr = "";
		int mailId = 0;
		for (int team = 1 ; team <= 2 ; team++) {
			if (TeamPlayerMap.containsKey(team)) {
				for (int playerId : TeamPlayerMap.get(team)) {
					awardStr = "";
					mailId = 0;
					if (TeamAScore == TeamBSocre) {
						awardStr = SysBasicCfg.getInstance().getAct175TeamAward(GloryHoleAwardType.WINER);
						mailId = GsConst.MailId.GH_TEAM_WINER_MAIL;
					} else if (TeamAScore > TeamBSocre) {
						mailId = team == 1 ? GsConst.MailId.GH_TEAM_WINER_MAIL : GsConst.MailId.GH_TEAM_LOSER_MAIL;
						awardStr = team == 1 ? SysBasicCfg.getInstance().getAct175TeamAward(GloryHoleAwardType.WINER):SysBasicCfg.getInstance().getAct175TeamAward(GloryHoleAwardType.LOSER);
					} else {
						mailId = team == 2 ? GsConst.MailId.GH_TEAM_WINER_MAIL : GsConst.MailId.GH_TEAM_LOSER_MAIL;
						awardStr = team == 2 ? SysBasicCfg.getInstance().getAct175TeamAward(GloryHoleAwardType.WINER):SysBasicCfg.getInstance().getAct175TeamAward(GloryHoleAwardType.LOSER);
					}
					AwardItems everydayAward = AwardItems.valueOf(awardStr);
					// 发送邮件时间
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String date = sdf.format(GuaJiTime.getAM0Date());
					MailManager.createMail(playerId, Mail.MailType.Reward_VALUE, mailId, "GH團隊奖励",
							everydayAward, date);
				}
			}
		}
	}
	/**
	 * 排行榜時間(活動開始時間)
	 */
	public long getGloryHoleStartTime() {
		long rankTime = GuaJiTime.getMillisecond();
		ActivityItem activityItem = ActivityManager.getInstance().getActiveActivityItem(activityId);
		if (activityItem == null)
			return rankTime;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		if ((timeConfig == null)||(timeConfig.isEnd()))
			return rankTime;
		
		// 抓今天開始遊戲日
		rankTime = GuaJiTime.getTimeHourMinute(SysBasicCfg.getInstance().getAct175GameTime(GloryHoleGameTime.START)); // 比賽開始時間00點
		
//		int days = GuaJiTime.getDaysOfWeek();
//		// 活動時間 星期 1, 3 ,5
//		
//		if (days == 0){ // 星期日當天沒活動,抓上次活動時間()
//			rankTime -= 48 * 60 * 60 * 1000;
//		}
//		
//		if (days == 1) {
//			if (GuaJiTime.getMillisecond() < rankTime) { // 活動當天還沒到點,抓上星期五活動時間
//				rankTime -= 72 * 60 * 60 * 1000;
//			}
//		}
//		
//		 if ((days == 3) || (days == 5)) {
//			if (GuaJiTime.getMillisecond() < rankTime) { // 活動當天還沒到點,抓上次活動時間
//				rankTime -= 48 * 60 * 60 * 1000;
//			}
//		}
//		
//		if ((days == 2) || (days == 4) || (days == 6)) { // 星期2,4,6沒活動,抓上次活動時間
//				rankTime -= 24 * 60 * 60 * 1000;
//		}
		
		return rankTime;
	}
	
	/**
	 * 參予人數佔存
	 * @return
	 */
	public int getJoinCount() {
		return joinCount;
	}
	
	/**
	 * 增加參與活動人數
	 */
	public synchronized void updateJoinCount() {
		try {
			int addCount = 1;
			joinCount = Math.min(Math.max(joinCount + addCount,0),GsConst.MAX_INT_RANGE);
		} catch (Exception e) {
			MyException.catchException(e);
		}
	}
	
	
	
}