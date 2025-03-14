package com.guaji.game.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.util.ConcurrentHashSet;
import org.guaji.app.AppObj;
import org.guaji.cache.CacheObj;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;
import org.guaji.thread.GuaJiTask;
import org.guaji.xid.GuaJiXID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guaji.game.GsApp;
import com.guaji.game.battle.BattleRole;
import com.guaji.game.battle.Battlefield;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.TeamBattleCacheEntity;
import com.guaji.game.entity.TeamBattleReportEntity;
import com.guaji.game.entity.TeamEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.AwardItems.Item;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.module.PlayerTitleModule;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Battle;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Mail.MailType;
import com.guaji.game.protocol.Mail.TbPlayerInfo;
import com.guaji.game.protocol.Mail.TbPlayerVSInfo;
import com.guaji.game.protocol.Mail.TeamRoundInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.TeamBattle.MemberInfo;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;

/**
 * 团战数据管理器
 */
public class TeamBattleManager extends AppObj {
	/**
	 * 日志记录器
	 */
	private static Logger logger = LoggerFactory.getLogger("Server");

	/**
	 * 团战发奖业务封装（缘由：创建奖励邮件比较耗时）
	 * 
	 * @author xpf
	 */
	public class TeamBattleRewardTask extends GuaJiTask {
		// 团战队伍
		private TeamEntity teamEntity;
		// 是否是冠军
		private boolean isfirst;

		public TeamBattleRewardTask(TeamEntity teamEntity, boolean isfirst) {
			this.teamEntity = teamEntity;
			this.isfirst = isfirst;
		}

		@Override
		protected int run() {
			// 发放队伍奖励
			grantTeamBattleAward(teamEntity, isfirst);
			return 0;
		}

		@Override
		protected CacheObj clone() {
			return new TeamBattleRewardTask(teamEntity, isfirst);
		}

		/**
		 * 发放团战战报和奖励
		 * 
		 * @param teamEntity
		 * @param isfirst
		 */
		private void grantTeamBattleAward(TeamEntity teamEntity, boolean isfirst) {

			for (int playerId : teamEntity.getTeamMembers()) {
				if (PlayerUtil.queryPlayer(playerId) != null) {
					// 推送参加古宝山任务
					QuestEventBus.fireQuestEventOneTime(QuestEventType.GU_BAO_SHAN, GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId));
				} else {
					QuestEventBus.fireQuestEventWhenPlayerOffline(playerId, QuestEventType.GU_BAO_SHAN, 1);
				}
				PlayerSnapshotInfo.Builder snapShotInfo = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
				// 奖励基础数据
				int level = snapShotInfo.getMainRoleInfo().getLevel();
				int goldRatio = 0;
				String captainAward = null;
				String extraAward = SysBasicCfg.getInstance().getExtraAward();
				String winTeamExtraAward = null;
				// 奖励数据
				AwardItems awardItems = new AwardItems();
				if (playerId == teamEntity.getCaptainId()) {
					// 队长奖励基础数据赋值
					goldRatio = SysBasicCfg.getInstance().getCaptainGoldRatio();
					captainAward = SysBasicCfg.getInstance().getCaptainExtraAward();
					if (isfirst) {
						winTeamExtraAward = SysBasicCfg.getInstance().getWinTeamCaptainExtraAward();
					}
				} else {
					// 队员奖励基础数据赋值
					goldRatio = SysBasicCfg.getInstance().getMemberGoldRatio();
					if (isfirst) {
						winTeamExtraAward = SysBasicCfg.getInstance().getWinTeamMemberExtraAward();
					}
				}
				// 生成奖励数据
				awardItems = this.createTeamAward(level, goldRatio, teamEntity.getRound(), captainAward, extraAward, winTeamExtraAward);
				/** 添加称号信息---------------------------------- */
				if (isfirst) {
					Player player = PlayerUtil.queryPlayer(playerId);
					if (player != null) {
						Msg msg = Msg.valueOf(GsConst.MsgType.TEAM_BATTLE_CHAMPION, GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId));
						msg.pushParam(playerId); // 玩家id
						msg.pushParam(GsConst.Title.STORM_KING);
						GsApp.getInstance().postMsg(msg);
					} else {
						PlayerTitleModule.updateOfflinePlayerTitle(playerId, GsConst.Title.STORM_KING, 1, true);
					}
					logger.info("sendTitleAdd message - playerId:{}", playerId);
				}
				/** 添加称号信息---------------------------------- */
				// 前端邮件模版编号
				int awardMailId = isfirst ? GsConst.MailId.TEAM_BATTLE_WINNER_AWARD : GsConst.MailId.TEAM_BATTLE_LOSER_AWARD;
				int reportMailId = isfirst ? GsConst.MailId.TEAM_BATTLE_WINNER_REPORT : GsConst.MailId.TEAM_BATTLE_LOSER_REPORT;
				// 发奖时间
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String curTimeStr = sdf.format(GuaJiTime.getMillisecond());
				// 发放奖励（前端参数：团战期号，团战轮数）
				MailManager.createMail(playerId, MailType.Reward_VALUE, awardMailId, "团战奖励", awardItems, curTimeStr,
						String.valueOf(teamEntity.getRound()));
				// 发送战报（前端参数：团战期号，团战轮数，自己的teamId）
				MailManager.createMail(playerId, MailType.Battle_VALUE, reportMailId, "团战战报", null, curTimeStr, String.valueOf(teamEntity.getRound()),
						String.valueOf(teamEntity.getId()));
				// 增加魔兽元气
				int addVitality = SysBasicCfg.getInstance().getTeamBattleAddVitality();
				AllianceManager.getInstance().addAllianceBossVitality(playerId, addVitality, Action.TAKE_PART_IN_TEAM_BATTLE);
			}
			// 发奖日志
			logger.info("grantTeamBattle awards - stageId:{}, teamId:{}, teamMembers:{}, isfirst:{}, round:{}", teamEntity.getStageId(),
					teamEntity.getId(), teamEntity.getTeamMemberStr(), isfirst, teamEntity.getRound());
		}

		/**
		 * 团战奖励构建
		 * 
		 * @param level
		 * @param goldRatio
		 *            奖励金币系数
		 * @param round
		 *            团队战斗轮数
		 * @param captainAward
		 *            队长的额外奖励物品
		 * @param extraAward
		 *            全员额外奖励物品
		 * @param winTeamExtraAward
		 *            冠军奖励数据
		 * @return
		 */
		private AwardItems createTeamAward(int level, int goldRatio, int round, String captainAward, String extraAward, String winTeamExtraAward) {

			AwardItems awardItems = new AwardItems();
			// 奖励金币计算
			int rewardCoin = level * goldRatio + level * SysBasicCfg.getInstance().getRoundGoldRatio() * round;
			awardItems.addCoin(rewardCoin);
			// 队长额外奖励添加
			AwardItems medium = AwardItems.valueOf(captainAward);
			if (null != medium) {
				for (Item item : medium.getAwardItems()) {
					item.count *= round;
				}
				awardItems.appendAward(medium);
			}
			// 全员额外奖励添加
			medium = AwardItems.valueOf(extraAward);
			if (null != medium) {
				awardItems.appendAward(medium);
			}
			// 冠军奖励
			medium = AwardItems.valueOf(winTeamExtraAward);
			if (null != medium) {
				awardItems.appendAward(medium);
			}

			return awardItems;
		}
	}

	/**
	 * 战报生成业务封装（缘由：创建战报邮件比较耗时）
	 */
	public class TeamBattleReportTask extends GuaJiTask {
		// 团战期号
		private int stageId;
		// 战斗轮数
		private int battleState;
		// leftTeamId
		private int teamId1;
		// rightTeamId
		private int teamId2;
		// leftTeam是否胜出
		private boolean isTeam1Win;
		// leftTeam战斗角色列表
		List<BattleRole> team1BattleRoleList;
		// rightTeam战斗角色列表
		List<BattleRole> team2BattleRoleList;

		public TeamBattleReportTask(int stageId, int battleState, int teamId1, int teamId2, boolean isTeam1Win, List<BattleRole> team1BattleRoleList,
				List<BattleRole> team2BattleRoleList) {
			this.stageId = stageId;
			this.battleState = battleState;
			this.teamId1 = teamId1;
			this.teamId2 = teamId2;
			this.isTeam1Win = isTeam1Win;
			this.team1BattleRoleList = team1BattleRoleList;
			this.team2BattleRoleList = team2BattleRoleList;
		}

		@Override
		protected int run() {
			createBattleReport(teamId1, teamId2, isTeam1Win, team1BattleRoleList, team2BattleRoleList);
			return 0;
		}

		@Override
		protected CacheObj clone() {
			return new TeamBattleReportTask(stageId, battleState, teamId1, teamId2, isTeam1Win, team1BattleRoleList, team2BattleRoleList);
		}

		/**
		 * 存储团战队伍单轮对战结果
		 * 
		 * @param teamId1
		 * @param teamId2
		 * @param isTeam1Win
		 * @param team1BattleRoleList
		 * @param team2BattleRoleList
		 */
		private void createBattleReport(int teamId1, int teamId2, boolean isTeam1Win, List<BattleRole> team1BattleRoleList,
				List<BattleRole> team2BattleRoleList) {
			// 创建数据库存储对象
			TeamBattleReportEntity teamBattleReport = new TeamBattleReportEntity();
			teamBattleReport.setStageId(this.stageId);
			teamBattleReport.setLeftTeamId(teamId1);
			teamBattleReport.setRightTeamId(teamId2);
			teamBattleReport.setRound(this.battleState);
			int whoWin = isTeam1Win ? 1 : 2; // 1表示1队获胜 2标识2队获胜
			teamBattleReport.setResult(whoWin);
			TeamRoundInfo.Builder teamRoundBuilder = genTeamRoundInfo(team1BattleRoleList, team2BattleRoleList, isTeam1Win);
			teamBattleReport.setTeamRoundInfoBuilder(teamRoundBuilder);
			DBManager.getInstance().create(teamBattleReport);
		}

		/**
		 * 将团战接收后的BattleRoleList按照BattleScore从大到小排列
		 * 
		 * @param teamBattleRoleList
		 */
		private void sortBattleRoleList(List<BattleRole> teamBattleRoleList) {
			Collections.sort(teamBattleRoleList, new Comparator<BattleRole>() {
				public int compare(BattleRole arg0, BattleRole arg1) {
					return arg1.getBattleScore() - arg0.getBattleScore();
				}
			});
		}

		/**
		 * 以 team1(leftTeam)为第一视角生成战报信息
		 * 
		 * @param team1BattleRoleList
		 * @param team2BattleRoleList
		 * @param isWin
		 * @return
		 */
		private TeamRoundInfo.Builder genTeamRoundInfo(List<BattleRole> team1BattleRoleList, List<BattleRole> team2BattleRoleList, boolean isWin) {
			TeamRoundInfo.Builder builder = TeamRoundInfo.newBuilder();
			builder.setRoundId(this.battleState);
			builder.setIsWin(isWin);
			// 一般每只队伍的第一人为队长
			builder.setLeftTeamName(team1BattleRoleList.get(0).getRoleInfo().getName());
			// 假定轮空, 前端认为rightName==""时为轮空
			builder.setRightTeamName("");
			if (team2BattleRoleList.size() > 0) {
				builder.setRightTeamName(team2BattleRoleList.get(0).getRoleInfo().getName());
				sortBattleRoleList(team1BattleRoleList);
				sortBattleRoleList(team2BattleRoleList);
				List<TbPlayerVSInfo.Builder> playerVSInfos = genPlayerVSInfo(team1BattleRoleList, team2BattleRoleList);
				for (TbPlayerVSInfo.Builder playerVSInfo : playerVSInfos) {
					builder.addVsInfo(playerVSInfo);
				}
			}
			return builder;
		}

		private List<TbPlayerVSInfo.Builder> genPlayerVSInfo(List<BattleRole> team1BattleRoleList, List<BattleRole> team2BattleRoleList) {
			List<TbPlayerVSInfo.Builder> playerVSInfos = new ArrayList<TbPlayerVSInfo.Builder>();

			int size = Math.min(team1BattleRoleList.size(), team2BattleRoleList.size());
			for (int index = 0; index < size; index++) {
				TbPlayerVSInfo.Builder playerVSInfo = TbPlayerVSInfo.newBuilder();
				playerVSInfo.setLeftPlayer(genPlayerInfoBuilder(team1BattleRoleList.get(index)));
				playerVSInfo.setRightPlayer(genPlayerInfoBuilder(team2BattleRoleList.get(index)));
				playerVSInfos.add(playerVSInfo);
			}

			if (team1BattleRoleList.size() > team2BattleRoleList.size()) {
				for (int j = size - 1; j < team1BattleRoleList.size(); j++) {
					TbPlayerVSInfo.Builder playerVSInfo = TbPlayerVSInfo.newBuilder();
					playerVSInfo.setLeftPlayer(genPlayerInfoBuilder(team1BattleRoleList.get(j)));
				}
			}

			if (team2BattleRoleList.size() > team1BattleRoleList.size()) {
				for (int j = size - 1; j < team2BattleRoleList.size(); j++) {
					TbPlayerVSInfo.Builder playerVSInfo = TbPlayerVSInfo.newBuilder();
					playerVSInfo.setRightPlayer(genPlayerInfoBuilder(team2BattleRoleList.get(j)));
				}
			}
			return playerVSInfos;
		}

		private TbPlayerInfo.Builder genPlayerInfoBuilder(BattleRole battleRole) {
			TbPlayerInfo.Builder playerInfo = TbPlayerInfo.newBuilder();
			playerInfo.setPlayId(battleRole.getPlayerId());
			playerInfo.setName(battleRole.getRoleInfo().getName());
			playerInfo.setKillCount(battleRole.getKillEnemyNum());
			playerInfo.setRecord(battleRole.getBattleScore());
			return playerInfo;
		}
	}

	/**
	 * 全局对象, 便于访问
	 */
	private static TeamBattleManager instance = null;
	/**
	 * 目前创建的队伍和已分配队伍的玩家：1. <TeamID, TeamEntity> 2.<PlayerID, TeamID>
	 */
	private Map<Integer, TeamEntity> allTeams;
	private Map<Integer, Integer> allInTeamPlayers;
	/**
	 * 团战缓存数据表
	 */
	private TeamBattleCacheEntity teamBattleCacheEntity;

	/**
	 * 管理器对应的团战期号
	 */
	private int manStageId;
	/**
	 * 本期团战进行到第几轮
	 */
	private int battleState;
	/**
	 * 下次状态切换时间点
	 */
	private int nextStatePeroid = 0;

	/**
	 * 获取全局实例对象
	 */
	public static TeamBattleManager getInstance() {
		return instance;
	}

	public TeamBattleManager(GuaJiXID xid) {
		super(xid);

		manStageId = 0;
		battleState = 0;
		allTeams = new ConcurrentHashMap<Integer, TeamEntity>();
		allInTeamPlayers = new ConcurrentHashMap<Integer, Integer>();
		teamBattleCacheEntity = new TeamBattleCacheEntity();

		if (instance == null) {
			instance = this;
		}
	}

	/********************************************************/

	/**
	 * 数据加载
	 * 
	 * @return
	 */
	public boolean init() {
		// 计算当前团战期号
		this.manStageId = calcTeamBattleStageId();
		this.battleState = GsConst.TeamBattle.STATE_PREPARE;

		// 加载数据库历史数据
		loadTeamBattleDBData();

		// 计算下次团战开始时间
		int index = this.manStageId % 10 - 1;
		int date = this.manStageId / 10;
		long dayFirstTime = GuaJiTime.getAM0Date().getTime();
		int today = Integer.valueOf(GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date()));
		if (date != today) {
			dayFirstTime = GuaJiTime.getNextAM0Date();
		}

		List<Integer> teamBattleStartTimes = SysBasicCfg.getInstance().getTeamBattleStartTime();
		this.nextStatePeroid = (int) (dayFirstTime / 1000) + teamBattleStartTimes.get(index);
		return true;
	}

	/**
	 * 数据库拉取团战数据
	 */
	public void loadTeamBattleDBData() {
		List<TeamBattleCacheEntity> teamBattleCacheEntities = DBManager.getInstance().query("from TeamBattleCacheEntity where stageId = ?",
				this.manStageId);
		if (teamBattleCacheEntities.size() > 0) {
			// 有本期团战历史数据
			this.teamBattleCacheEntity = teamBattleCacheEntities.get(0);
		} else {
			// 无本期团战历史数据
			this.teamBattleCacheEntity.setStageId(this.manStageId);
			this.teamBattleCacheEntity.setLastSaveTime(GuaJiTime.getSeconds());
			DBManager.getInstance().create(teamBattleCacheEntity);
		}

		allTeams.clear();
		allInTeamPlayers.clear();
		List<TeamEntity> teamEntities = DBManager.getInstance().query("from TeamEntity where stageId = ?", this.manStageId);
		for (TeamEntity teamEntity : teamEntities) {
			// 加载数据库本期Team数据
			allTeams.put(teamEntity.getId(), teamEntity);
			for (int teamMemberId : teamEntity.getTeamMembers()) {
				allInTeamPlayers.put(teamMemberId, teamEntity.getId());
			}
		}
	}

	/**
	 * 获取当前开放的团战期号
	 * 
	 * @return
	 */
	public int getManStageId() {
		return this.manStageId;
	}

	/**
	 * 当前团战状态（0准备中 1第一轮 2第二轮 ......）
	 * 
	 * @return
	 */
	public int getCurBattleState() {
		return this.battleState;
	}

	/**
	 * 下个状态切换时间
	 * 
	 * @return
	 */
	public int getNextStatePeroid() {
		return nextStatePeroid;
	}

	/**
	 * 帧更新，状态检查
	 */
	@Override
	public boolean onTick() {
		// 实时分配玩家到空闲的队伍中
		allocateTeam();

		// 状态切换检查
		int curTime = GuaJiTime.getSeconds();
		if (curTime >= nextStatePeroid) {
			if (this.battleState == GsConst.TeamBattle.STATE_END_SWITCH) {
				// 重新初始话管理器数据
				init();
				// 告知所有玩家状态切换
				Msg msg = Msg.valueOf(GsConst.MsgType.TEAM_BATTLE_CHANGE);
				GsApp.getInstance().broadcastMsg(msg, GsApp.getInstance().getObjMan(GsConst.ObjType.PLAYER));
			} else if (this.battleState == GsConst.TeamBattle.STATE_PREPARE) {
				// 为所有没有队伍的玩家创建队伍
				noTeamPlayerCreateTeam();

				// 设置团战开始
				this.battleState = GsConst.TeamBattle.STATE_START;
				this.nextStatePeroid = GuaJiTime.getSeconds() + SysBasicCfg.getInstance().getTeamBattleRoundIntervalTime();

				// 告知所有玩家状态切换
				Msg msg = Msg.valueOf(GsConst.MsgType.TEAM_BATTLE_CHANGE);
				GsApp.getInstance().broadcastMsg(msg, GsApp.getInstance().getObjMan(GsConst.ObjType.PLAYER));

				// 计算下轮对阵分配，并将对阵数据落地
				HashMap<Integer, Integer> teamMatchMap = calcTeamMatching();
				teamBattleCacheEntity.setNextRoundTeamsAgainstPlan(teamMatchMap);
				teamBattleCacheEntity.notifyUpdate(true);

				// 没人报名参赛
				if (teamMatchMap.isEmpty()) {
					teamBattleEnd();
				}

				// 只有一队报名时只给该队发冠军奖励
				if (teamMatchMap.size() == 1) {
					int leftTeamId = (int) teamMatchMap.keySet().toArray()[0];
					int rightTeamId = (int) teamMatchMap.values().toArray()[0];
					if (rightTeamId == 0) {
						TeamEntity teamEntity = allTeams.get(leftTeamId);
						teamEntity.setRound(this.battleState);
						teamEntity.notifyUpdate(true);

						// 投递单个队伍发奖Task到后台线程
						GsApp.getInstance().postCommonTask(new TeamBattleRewardTask(teamEntity, true));

						teamBattleEnd();
						return true;
					}
				}
				// 战斗
				doFight();
			} else if (this.battleState >= GsConst.TeamBattle.STATE_START) {
				if (this.battleState > 13) { // 8192(2**13)支队伍大约81920人，超过服务器理论人数上限
					teamBattleEnd();
					Log.errPrintln("teamBattle's round over 13, teamBattle abnormality");
				}

				// 进入下一轮
				this.battleState++;
				this.nextStatePeroid = GuaJiTime.getSeconds() + SysBasicCfg.getInstance().getTeamBattleRoundIntervalTime();
				// 告知所有玩家状态切换
				Msg msg = Msg.valueOf(GsConst.MsgType.TEAM_BATTLE_CHANGE);
				GsApp.getInstance().broadcastMsg(msg, GsApp.getInstance().getObjMan(GsConst.ObjType.PLAYER));
				// 战斗
				doFight();
			}
		}
		return true;
	}

	/**
	 * 跟据当前时间计算团战期号
	 */
	private int calcTeamBattleStageId() {
		List<Integer> times = SysBasicCfg.getInstance().getTeamBattleStartTime();

		long todayFirstTime = GuaJiTime.getAM0Date().getTime();
		String date = GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date());
		int today = Integer.valueOf(date);

		int curTime = GuaJiTime.getSeconds();
		for (int index = 0; index < times.size(); index++) {
			int battleTime = (int) (todayFirstTime / 1000) + times.get(index);
			if (curTime < battleTime) {
				return today * 10 + (index + 1);
			}
		}

		long tomorrowFirstTime = GuaJiTime.getNextAM0Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(tomorrowFirstTime);
		date = GuaJiTime.DATE_FORMATOR_DAYNUM(calendar.getTime());
		int tomorrow = Integer.valueOf(date);
		return tomorrow * 10 + 1;
	}

	/**
	 * 将等待分配队伍的玩家分配到队伍中
	 */
	private void allocateTeam() {
		if (this.battleState != GsConst.TeamBattle.STATE_PREPARE)
			return;

		ConcurrentHashSet<Integer> waitTeamPlayers = teamBattleCacheEntity.getWaitPlayerIds();
		if (waitTeamPlayers.size() > 0 && allTeams.size() > 0) {
			TreeMap<TeamEntity, Integer> sortTeamMap = new TreeMap<TeamEntity, Integer>();
			// 剔除满员的队伍，并根据队员人数进行排序，人数最少的队伍优先分配
			for (Map.Entry<Integer, TeamEntity> entry : allTeams.entrySet()) {
				TeamEntity teamEntity = entry.getValue();
				if (!teamEntity.isFull()) {
					sortTeamMap.put(teamEntity, teamEntity.getTeamMembers().size());
				}
			}

			Iterator<Integer> playerIdIterator = waitTeamPlayers.iterator();
			while (sortTeamMap.size() > 0 && playerIdIterator.hasNext()) {
				// 先过滤已经进入队伍的playerId（xpf:bug补丁 - 有时候玩家会出现在两支队伍里）
				int playerId = playerIdIterator.next();
				if (allInTeamPlayers.containsKey(playerId)) {
					playerIdIterator.remove();
					continue;
				}

				// 加入队伍
				TeamEntity firstTeam = sortTeamMap.firstKey();
				if (!firstTeam.getKickPlayerIds().contains((Integer) playerId)) {
					sortTeamMap.remove(firstTeam);
					firstTeam.addPlayer(playerId);
					allInTeamPlayers.put(playerId, firstTeam.getId());
					firstTeam.notifyUpdate(true);
					// 从等待队列中删除
					playerIdIterator.remove();
					if (!firstTeam.isFull()) {
						// 如果队伍人数不满则插入sortMap继续分配队员
						sortTeamMap.put(firstTeam, firstTeam.getTeamMembers().size());
					}
				}
			}

			// 保存缓存
			teamBattleCacheEntity.setWaitPlayerIds(waitTeamPlayers);
			teamBattleCacheEntity.notifyUpdate(true);
		}
	}

	/**
	 * 将团战开始时未分配到队伍的所有玩家进行编队
	 */
	private void noTeamPlayerCreateTeam() {
		if (this.battleState != GsConst.TeamBattle.STATE_PREPARE)
			return;

		ConcurrentHashSet<Integer> waitTeamPlayers = teamBattleCacheEntity.getWaitPlayerIds();
		if (waitTeamPlayers.size() > 0) {
			Iterator<Integer> playerIdIterator = waitTeamPlayers.iterator();
			TeamEntity teamEntity = null;
			while (playerIdIterator.hasNext()) {
				// 人数够一队后创建新队伍
				if (teamEntity == null || teamEntity.isFull()) {
					if (teamEntity != null) {
						teamEntity.notifyUpdate(true);
					}

					teamEntity = new TeamEntity();
					teamEntity.setStageId(this.manStageId);
					if (DBManager.getInstance().create(teamEntity)) {
						allTeams.put(teamEntity.getId(), teamEntity);
					}
				}
				int onePlayerId = playerIdIterator.next();
				teamEntity.addPlayer(onePlayerId);
			}
			// 最后一只队伍落地
			teamEntity.notifyUpdate(true);

			waitTeamPlayers.clear();
			teamBattleCacheEntity.setWaitPlayerIds(waitTeamPlayers);
			teamBattleCacheEntity.notifyUpdate(true);
		}
	}

	/**
	 * 计算团战队伍匹配列表
	 */
	private HashMap<Integer, Integer> calcTeamMatching() {
		// 计算每只队伍的战斗力
		List<TeamEntity> nextRoundTeams = new ArrayList<TeamEntity>();
		for (Map.Entry<Integer, TeamEntity> entry : allTeams.entrySet()) {
			TeamEntity team = entry.getValue();
			// 如果已经被淘汰出局，则不计入对阵列表
			if (team.getIsWeedOut() > 0)
				continue;

			if (team.getTotalFight() == 0) {
				int totalFight = 0;
				for (int playerId : team.getTeamMembers()) {
					try {
						PlayerSnapshotInfo.Builder snapshotInfo = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
						int playerFight = snapshotInfo.getMainRoleInfo().getFight();
						totalFight += playerFight;
					} catch (Exception e) {
						MyException.catchException(e);
					}
				}
				team.setTotalFight(totalFight);
				team.notifyUpdate(true);
			}
			nextRoundTeams.add(team);
		}

		// 根据战斗力，从小到大排序
		Collections.sort(nextRoundTeams, new Comparator<TeamEntity>() {
			public int compare(TeamEntity arg0, TeamEntity arg1) {
				return arg0.getTotalFight() - arg1.getTotalFight();
			}
		});

		// 队伍匹配规则
		HashMap<Integer, Integer> againstPlanMap = new HashMap<Integer, Integer>();
		int middle = nextRoundTeams.size() / 2;
		if (nextRoundTeams.size() % 2 == 0) {
			for (int index = 0; index < middle; index++) {
				TeamEntity team1 = (TeamEntity) nextRoundTeams.get(index);
				TeamEntity team2 = (TeamEntity) nextRoundTeams.get(index + middle);
				againstPlanMap.put(team1.getId(), team2.getId());
			}
		} else {
			for (int index = 0; index < middle; index++) {
				TeamEntity team1 = (TeamEntity) nextRoundTeams.get(index);
				TeamEntity team2 = (TeamEntity) nextRoundTeams.get(index + middle + 1);
				againstPlanMap.put(team1.getId(), team2.getId());
			}
			againstPlanMap.put(nextRoundTeams.get(middle).getId(), 0);
		}
		return againstPlanMap;
	}

	/**
	 * 根据队伍信息生成BattleRoleList，为团战做准备
	 * 
	 * @param teamEntity
	 * @return
	 */
	private List<BattleRole> genTeamBattleRoleList(TeamEntity teamEntity) {
		List<BattleRole> battleRoleList = new ArrayList<BattleRole>();
		if (teamEntity != null) {
			for (int teamMemberId : teamEntity.getTeamMembers()) {
				PlayerSnapshotInfo.Builder snapShotInfo = SnapShotManager.getInstance().getPlayerSnapShot(teamMemberId);
				BattleRole battleRole = new BattleRole(teamMemberId, snapShotInfo.getMainRoleInfo().toBuilder());
				battleRoleList.add(battleRole);

				/*
				 * // 佣兵 RoleInfo.Builder mercenaryBuilder =
				 * SnapShotManager.getInstance().getFightMercenaryInfo(
				 * teamMemberId); if (mercenaryBuilder != null) {
				 * battleRoleList.add(new BattleRole(mercenaryBuilder)); }
				 */
			}
		}
		return battleRoleList;
	}

	/**
	 * 进行一轮团战，战斗计算，并生成战报，发送邮件
	 */
	private void doFight() {
		// 本轮团队对阵表
		HashMap<Integer, Integer> curRoundTeamMatchMap = teamBattleCacheEntity.getNextRoundTeamsAgainstPlan();
		if (curRoundTeamMatchMap.isEmpty()) {
			// 说明没人报名参赛
			teamBattleEnd();
			return;
		}

		// 本轮团战开打
		Battlefield battlefield = new Battlefield();
		for (Map.Entry<Integer, Integer> entry : curRoundTeamMatchMap.entrySet()) {
			int teamId1 = entry.getKey();
			int teamId2 = entry.getValue();
			TeamEntity team1Entity = allTeams.get(teamId1);
			TeamEntity team2Entity = allTeams.get(teamId2);
			List<BattleRole> team1BattleRoleList = genTeamBattleRoleList(team1Entity);
			List<BattleRole> team2BattleRoleList = genTeamBattleRoleList(team2Entity);

			boolean isTeam1Win = false;
			team1Entity.setRound(this.battleState);
			if (teamId2 != 0) {
				team2Entity.setRound(this.battleState);

				battlefield.fighting(Battle.battleType.BATTLE_PVP_TEAM_VALUE, team1BattleRoleList, team2BattleRoleList, null);
				if (battlefield.getBattleResult() > 0) {
					isTeam1Win = true;
					// 设置team2被淘汰
					team2Entity.setIsWeedOut(1);
					// 投递单个队伍发奖Task到后台0号线程
					TeamBattleRewardTask task = new TeamBattleRewardTask(team2Entity, false);
					GsApp.getInstance().postCommonTask(task);
				} else {
					// 设置team1被淘汰
					team1Entity.setIsWeedOut(1);
					// 投递单个队伍发奖Task到后台0号线程
					TeamBattleRewardTask task = new TeamBattleRewardTask(team1Entity, false);
					GsApp.getInstance().postCommonTask(task);
				}
				team2Entity.notifyUpdate(true);
			} else {
				// 轮空直接晋级下一轮
				isTeam1Win = true;
			}
			team1Entity.notifyUpdate(true);

			// 创建战报数据库存储对象
			TeamBattleReportTask task = new TeamBattleReportTask(manStageId, battleState, teamId1, teamId2, isTeam1Win, team1BattleRoleList,
					team2BattleRoleList);
			GsApp.getInstance().postCommonTask(task);
		}

		// 计算下轮对阵分配，并将对阵数据落地，防止意外停服
		HashMap<Integer, Integer> teamMatchMap = calcTeamMatching();
		teamBattleCacheEntity.setNextRoundTeamsAgainstPlan(teamMatchMap);
		teamBattleCacheEntity.notifyUpdate(true);

		// 打完N轮后（N>=1）仅剩下一队时说明为最后一轮，给第一名发奖
		if (teamMatchMap.size() == 1) {
			int leftTeamId = (int) teamMatchMap.keySet().toArray()[0];
			int rightTeamId = (int) teamMatchMap.values().toArray()[0];
			if (rightTeamId == 0) {
				TeamEntity teamEntity = allTeams.get(leftTeamId);
				teamEntity.setRound(this.battleState);
				teamEntity.notifyUpdate(true);
				// 投递单个队伍发奖Task到后台0号线程
				TeamBattleRewardTask task = new TeamBattleRewardTask(teamEntity, true);

				GsApp.getInstance().postCommonTask(task);
				teamBattleEnd();
			}
		}
	}

	/**
	 * 团战结束
	 */
	private void teamBattleEnd() {
		// 管理器状态切换
		this.battleState = GsConst.TeamBattle.STATE_END_SWITCH;
		this.nextStatePeroid = GuaJiTime.getSeconds() + SysBasicCfg.getInstance().getTeamBattleEndSwitchTime();
	}

	/********************************************************/
	/**
	 * 检测玩家是否已经报名
	 */
	public boolean isAlreadySignUp(Player player) {
		int playerId = player.getId();
		ConcurrentHashSet<Integer> waitTeamPlayers = teamBattleCacheEntity.getWaitPlayerIds();
		if (waitTeamPlayers.contains(playerId) || allInTeamPlayers.containsKey(playerId)) {
			return true;
		}
		return false;
	}

	/**
	 * 报名
	 * 
	 * @param player
	 */
	public boolean signUp(Player player) {
		if (this.battleState != GsConst.TeamBattle.STATE_PREPARE)
			return false;

		int playerId = player.getId();
		ConcurrentHashSet<Integer> waitTeamPlayers = teamBattleCacheEntity.getWaitPlayerIds();
		if (!allInTeamPlayers.containsKey(playerId) && !waitTeamPlayers.contains(playerId)) {
			waitTeamPlayers.add(playerId);
			teamBattleCacheEntity.setWaitPlayerIds(waitTeamPlayers);
			teamBattleCacheEntity.notifyUpdate(true);
			return true;
		}
		return false;
	}

	/**
	 * 取消报名
	 * 
	 * @param player
	 */
	public boolean undoSignUp(Player player) {
		if (this.battleState != GsConst.TeamBattle.STATE_PREPARE)
			return false;

		int playerId = player.getId();
		if (allInTeamPlayers.containsKey(playerId)) {
			int teamId = allInTeamPlayers.get(playerId);
			if (allTeams.containsKey(teamId)) {
				TeamEntity team = allTeams.get(teamId);
				if (playerId != team.getCaptainId()) {
					team.removePlayer(playerId);
					team.notifyUpdate(true);
				}
			}
			allInTeamPlayers.remove(playerId);
		}

		ConcurrentHashSet<Integer> waitTeamPlayers = teamBattleCacheEntity.getWaitPlayerIds();
		if (waitTeamPlayers.contains(playerId)) {
			waitTeamPlayers.remove((Integer) playerId);
			teamBattleCacheEntity.setWaitPlayerIds(waitTeamPlayers);
			teamBattleCacheEntity.notifyUpdate(true);
		}
		return true;
	}

	/**
	 * 创建队伍
	 */
	public boolean createTeam(Player player) {
		if (this.battleState != GsConst.TeamBattle.STATE_PREPARE)
			return false;

		int playerId = player.getId();
		ConcurrentHashSet<Integer> waitTeamPlayers = teamBattleCacheEntity.getWaitPlayerIds();
		if (!allInTeamPlayers.containsKey(playerId) && !waitTeamPlayers.contains(playerId)) {
			TeamEntity teamEntity = new TeamEntity();
			teamEntity.setCaptainId(playerId);
			teamEntity.addPlayer(playerId);
			teamEntity.setStageId(manStageId);
			if (DBManager.getInstance().create(teamEntity)) {
				allTeams.put(teamEntity.getId(), teamEntity);
				allInTeamPlayers.put(playerId, teamEntity.getId());
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取队长踢人次数
	 * 
	 * @param teamId
	 * @return
	 */
	public int getCaptainKickTimes(int teamId) {
		if (allTeams.containsKey(teamId)) {
			TeamEntity teamEntity = allTeams.get(teamId);
			return teamEntity.getKickTimes();
		}
		return -1;
	}

	/**
	 * 踢出团队成员
	 */
	public boolean kickTeamMember(Player captain, int memberPlayerId) {
		int captainPlayerId = captain.getId();
		if (allInTeamPlayers.containsKey(captainPlayerId)) {
			int teamId = allInTeamPlayers.get(captainPlayerId);
			if (allTeams.containsKey(teamId) && allTeams.get(teamId).getCaptainId() == captainPlayerId && captainPlayerId != memberPlayerId) {

				TeamEntity myTeam = allTeams.get(teamId);
				myTeam.kickMember(memberPlayerId);
				allInTeamPlayers.remove(memberPlayerId);

				// 把被踢的人移动到等待对列中
				ConcurrentHashSet<Integer> waitTeamPlayers = teamBattleCacheEntity.getWaitPlayerIds();
				waitTeamPlayers.add(memberPlayerId);
				teamBattleCacheEntity.setWaitPlayerIds(waitTeamPlayers);
				teamBattleCacheEntity.notifyUpdate(true);
			}
		}
		return true;
	}

	/**
	 * 获取我的队伍Id
	 */
	public int getMyTeamId(Player player) {
		int playerId = player.getId();
		if (allInTeamPlayers.containsKey(playerId)) {
			int teamId = allInTeamPlayers.get(playerId);
			return teamId;
		}
		return 0;
	}

	/**
	 * 获取我的队伍信息, 包含我的信息, 只有队长才有权限获取全队信息
	 */
	public List<MemberInfo.Builder> getMyTeamMemberInfo(Player player) {
		int playerId = player.getId();
		if (allInTeamPlayers.containsKey(playerId)) {
			int teamId = allInTeamPlayers.get(playerId);
			if (allTeams.containsKey(teamId)) {
				TeamEntity teamEntity = allTeams.get(teamId);
				if (teamEntity.getCaptainId() == playerId) {
					List<MemberInfo.Builder> myTeamMemberInfos = BuilderUtil.genTeamBattleMemberBuilder(teamEntity);
					return myTeamMemberInfos;
				}
			}
		}
		return null;
	}

}
