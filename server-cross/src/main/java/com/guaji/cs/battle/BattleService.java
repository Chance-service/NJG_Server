package com.guaji.cs.battle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.guaji.config.ConfigManager;
import org.guaji.net.GuaJiSession;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;
import org.guaji.os.GuaJiTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guaji.cs.CrossServer;
import com.guaji.cs.common.BuilderUtil;
import com.guaji.cs.common.RankComparator;
import com.guaji.cs.db.BattleData;
import com.guaji.cs.db.DBManager;
import com.guaji.cs.db.DbOpUtil;
import com.guaji.cs.db.PlayerData;
import com.guaji.cs.db.RankData;
import com.guaji.cs.net.handler.SessionHandler;
import com.guaji.game.config.ContinueWinScoreCfg;
import com.guaji.game.config.CrossBattleCfg;
import com.guaji.game.config.CrossGroupCfg;
import com.guaji.game.config.SectionValueCfg;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.CsBattle.SyncRankData;

/**
 * 战斗服务类
 */
public class BattleService {

	/**
	 * 跨服战日志
	 */
	private Logger battleLog = LoggerFactory.getLogger("BattleLog");

	/**
	 * 玩家数据<key:玩家唯一标识, value:玩家数据>
	 */
	private Map<String, PlayerData> playerMap = new ConcurrentHashMap<String, PlayerData>();

	/**
	 * 战斗结果<key:玩家唯一标识, value:玩家数据集合>
	 */
	private Map<String, List<BattleData>> battleMap = new ConcurrentHashMap<String, List<BattleData>>();;

	/**
	 * 竞技排名
	 */
	private List<RankData> rankList = new ArrayList<RankData>();
	
	/**
	 * 推送过账号的服务器
	 */
	private List<String> serverIdentifies = new ArrayList<String>();

	/**
	 * 是否清理过
	 */
	private boolean isRun = false;

	
	private static final BattleService instatce = new BattleService();

	private BattleService() {
	}

	public static BattleService getInstance() {
		return instatce;
	}

	/**
	 * 清理当前赛季数据
	 */
	public void clearSeasonData() {
		Calendar calendar = GuaJiTime.getCalendar();
		// 周一清理数据
		if (calendar.get(Calendar.DAY_OF_WEEK) != CrossBattleCfg.getInstance().getStartDay()) {
			return;
		}
		// 晚上21点之前不处理
		if (calendar.get(Calendar.HOUR_OF_DAY) < CrossBattleCfg.getInstance().getStartTime()) {
			return;
		}
		// 没清理过
		if (!isRun) {
			// 21点清理数据
			if (calendar.get(Calendar.HOUR_OF_DAY) != CrossBattleCfg.getInstance().getStartTime()) {
				return;
			}
			battleLog.info("Start clear all data ......");
			DBManager.getInstance().clearCrossData();
			playerMap.clear();
			for (List<BattleData> battles : battleMap.values()) {
				battles.clear();
				battles = null;
			}
			battleMap.clear();
			serverIdentifies.clear();
			rankList.clear();
			isRun = true;
			battleLog.info("Clear all data end ......");
		} else {
			// 23点重置标识处理
			if (calendar.get(Calendar.HOUR_OF_DAY) >= CrossBattleCfg.getInstance().getEndTime()) {
				isRun = false;
				battleLog.info(String.format("Reset isRun value %b", isRun));
			}
		}
	}
	
	/**
	 * 添加服务器标识
	 * 
	 * @param serverIdentify
	 */
	public void addServerIdentify(String serverIdentify) {
		if (serverIdentifies.contains(serverIdentify)) {
			return;
		}
		serverIdentifies.add(serverIdentify);
	}

	/**
	 * 获取玩家数据
	 * 
	 * @param identify
	 * @return
	 */
	public PlayerData getPlayer(String identify) {
		PlayerData playerData = playerMap.get(identify);
		return playerData;
	}

	/**
	 * 设置玩家缓存数据
	 * 
	 * @param players
	 */
	public void setPayerMap(Collection<PlayerData> players) {
		if (players == null) {
			return;
		}
		// 加入缓存容器
		for (PlayerData playerData : players) {
			playerMap.put(playerData.getIdentify(), playerData);
		}
	}

	/**
	 * 添加玩家数据
	 * 
	 * @param playerData
	 * @return
	 */
	public PlayerData addPlayer(final PlayerData playerData) {
		synchronized (playerMap) {
			if (playerMap.containsKey(playerData.getIdentify())) {
				DBManager.getInstance().addDbOperation(playerData, DbOpUtil.UPDATE);
				battleLog.info(String.format("update player %s data", playerData.getIdentify()));
			} else {
				DBManager.getInstance().addDbOperation(playerData, DbOpUtil.INSERT);
				battleLog.info(String.format("add player %s data", playerData.getIdentify()));
			}
			// 更新缓存数据
			playerMap.put(playerData.getIdentify(), playerData);
			return playerData;
		}
	}

	/**
	 * 获取玩家竞技战斗数据
	 * 
	 * @param identify
	 * @return
	 */
	public List<BattleData> getBattleData(String identify) {
		return battleMap.get(identify);
	}

	/**
	 * 设置战报缓存数据
	 * 
	 * @param battles
	 */
	public void setBattleMap(Collection<BattleData> battles) {
		if (battles == null) {
			return;
		}
		for (BattleData battleData : battles) {
			this.addBattleData(battleData, false);
		}
	}

	/**
	 * 添加战斗结果
	 * 
	 * @param battleData
	 * @param isInsert
	 */
	public void addBattleData(BattleData battleData, boolean isInsert) {
		if (battleData == null) {
			return;
		}
		synchronized (battleMap) {
			// 获取战报缓存数据
			List<BattleData> battleList = new ArrayList<BattleData>();
			if (battleMap.containsKey(battleData.getIdentify())) {
				battleList = this.getBattleData(battleData.getIdentify());
				battleList.add(battleData);
			} else {
				battleList.add(battleData);
				battleMap.put(battleData.getIdentify(), battleList);
			}
			// 超过记录条数处理
			if (battleList.size() > CrossBattleCfg.getInstance().getBattleTotal()) {
				BattleData _battleData = battleList.remove(0);
				DBManager.getInstance().addDbOperation(_battleData, DbOpUtil.DELETE);
			}
			// 数据落地
			if (isInsert) {
				DBManager.getInstance().addDbOperation(battleData, DbOpUtil.INSERT);
			}
		}
	}

	/**
	 * 获取排行数据
	 * 
	 * @return
	 */
	public List<RankData> getRankList() {
		return rankList;
	}

	/**
	 * 设置排行缓存数据
	 * 
	 * @param ranks
	 */
	public void setRankList(Collection<RankData> ranks) {
		if (ranks == null) {
			return;
		}
		for (RankData rankData : ranks) {
			rankList.add(rankData);
		}
	}
	
	/**
	 * 获取账号排行数据
	 * 
	 * @param identify
	 */
	public RankData getRankData(String identify) {
		synchronized (rankList) {
			for (RankData rankData : rankList) {
				if (rankData.getIdentify().equals(identify)) {
					return rankData;
				}
			}
		}
		return null;
	}

	/**
	 * 添加排行数据
	 * 
	 * @param identify
	 * @return
	 */
	public void addRankData(String identify) {
		synchronized (rankList) {
			if (rankList.size() == 0) {
				return;
			}
			for (RankData rankData : rankList) {
				if (!rankData.getIdentify().equals(identify)) {
					continue;
				}
				return;
			}
			// 缓存中没有找到数据
			RankData rankData = new RankData(identify, CrossBattleCfg.getInstance().getScoreInit(), false);
			rankList.add(rankData);
			DBManager.getInstance().addDbOperation(rankData, DbOpUtil.INSERT);
		}
	}

	/**
	 * 竞技排名更新
	 */
	public void updateRank() {
		synchronized (rankList) {
			if (rankList.size() <= 0) {
				return;
			}
			// 积分排行
			Collections.sort(rankList, RankComparator.getInstance());
			int rankIndex = 1;
			// 更新排名数据
			for (RankData rankData : rankList) {
				if (rankData.getRank() != rankIndex) {
					rankData.setRank(rankIndex);
					DBManager.getInstance().addDbOperation(rankData, DbOpUtil.UPDATE);
				}
				rankIndex++;
			}
		}
	}

	/**
	 * 赛季开始、初始化随机排行榜
	 */
	public void randomRank() {
		if (rankList.size() != 0) {
			return;
		}
		if (playerMap.size() <= 0) {
			return;
		}
		// 同步账号服是否全了
		List<CrossGroupCfg> groupList = ConfigManager.getInstance().getConfigList(CrossGroupCfg.class);
		for (CrossGroupCfg groupConfig : groupList) {
			if (groupConfig.getCsId() != CrossServer.getInstance().getCsId()) {
				continue;
			}
			if (serverIdentifies.size() != groupConfig.getServerSize()) {
				return;
			}
			break;
		}
		// 排行榜不能有数据
		synchronized (rankList) {
			// 开始初始随机排行榜
			battleLog.info("Start the initial rank random list");
			for (PlayerData playerData : playerMap.values()) {
				RankData rankData = new RankData(playerData.getIdentify(), CrossBattleCfg.getInstance().getScoreInit(), false);
				rankList.add(rankData);
			}
			for (int index = 0; index < rankList.size() / 2; index++) {
				int index_1 = GuaJiRand.randInt(rankList.size() - 1);
				int index_2 = GuaJiRand.randInt(rankList.size() - 1);
				if (index_1 != index_2) {
					RankData rankData_1 = rankList.get(index_1);
					RankData rankData_2 = rankList.get(index_2);
					rankList.set(index_1, rankData_2);
					rankList.set(index_2, rankData_1);
				}
			}
			// 设置名次
			int rankIndex = 1;
			for (RankData rankData : rankList) {
				rankData.setRank(rankIndex);
				DBManager.getInstance().addDbOperation(rankData, DbOpUtil.INSERT);
				rankIndex++;
				// 同步随机排行数据
				String serverName = PlayerUtil.getServerName(rankData.getIdentify());
				if (serverName == null || "".equals(serverName)) {
					continue;
				}
				GuaJiSession session = SessionHandler.getInstance().getSession(serverName);
				if (session != null) {
					SyncRankData.Builder builder = BuilderUtil.createSyncRankData(rankData.getIdentify(), rankData.getRank(), 0);
					session.sendProtocol(Protocol.valueOf(HP.code.SYNC_RANDOM_RANK_VALUE, builder.build().toByteArray()));
				}
			}
		}
	}

	/**
	 * 根据排名获取筛选匹配数据
	 * 
	 * @param rank
	 */
	private SectionValueCfg getSectionConfig(int rank) {
		// 筛选区间配置
		List<SectionValueCfg> sectionList = ConfigManager.getInstance().getConfigList(SectionValueCfg.class);
		// 发送奖励
		for (SectionValueCfg config : sectionList) {
			if (rank <= config.getMinRank() && rank > 0) {
				return config;
			}
		}
		return null;
	}
	
	/**
	 * 筛选挑战匹配玩家
	 * 
	 * @param rankData
	 * @return
	 */
	public List<PlayerData> choiceBattlePlayer(final RankData rankData) {
		synchronized (rankList) {
			// 缓存筛选对象
			List<PlayerData> vsPlayers = new ArrayList<PlayerData>();
			SectionValueCfg sectionConfig = this.getSectionConfig(rankData.getRank());
			if (sectionConfig == null) {
				return vsPlayers;
			}
			// 筛选对象
			List<PlayerData> choicePlayers = null;
			choicePlayers = choiceUpPlayer(rankData, sectionConfig.getUpSection(), sectionConfig.getRandomUp());
			if (choicePlayers != null) {
				vsPlayers.addAll(choicePlayers);
			}
			choicePlayers = choiceDownPlayer(rankData, sectionConfig.getDownSection(), sectionConfig.getRandomDown());
			if (choicePlayers != null) {
				vsPlayers.addAll(choicePlayers);
			}
			return vsPlayers;
		}
	}

	/**
	 * 筛选排名高的玩家
	 * 
	 * @param rankData
	 * @param upSection
	 * @param count
	 * @return 
	 */
	private List<PlayerData> choiceUpPlayer(final RankData rankData, int upSection, int count) {
		battleLog.info(String.format("Choice up player size is %d", count));
		if (upSection <= 0 || count <= 0) {
			return null;
		}
		// 存选取符合条件的数据
		List<PlayerData> vsPlayers = new ArrayList<PlayerData>();
		// 选排名高的区间玩家
		int rankIndex = rankData.getRank() - upSection - 1;
		if (rankIndex < 0) {
			return null;
		}
		List<String> limitPlayers = new ArrayList<String>();
		// 取数据注意下标 -1 问题
		for (; rankIndex < rankData.getRank() - 1; rankIndex ++) {
			RankData limitData = rankList.get(rankIndex);
			limitPlayers.add(limitData.getIdentify());
		}
		// 是否需要随机取数据
		if (limitPlayers.size() <= count) {
			for (String identify : limitPlayers) {
				PlayerData playerData = playerMap.get(identify);
				if (playerData == null) {
					continue;
				}
				vsPlayers.add(playerData);
			}
			return vsPlayers;
		}
		// 随机取数据
		return this.randomGetPlayer(limitPlayers, count);
	}

	/**
	 * 筛选排名低的玩家
	 * 
	 * @param rankData
	 * @param downSection
	 * @param count
	 * @return
	 */
	private List<PlayerData> choiceDownPlayer(final RankData rankData, int downSection, int count) {
		battleLog.info(String.format("Choice down player size is %d", count));
		if (count <= 0) {
			return null;
		}
		// 存选取符合条件的数据
		List<PlayerData> vsPlayers = new ArrayList<PlayerData>();
		// 选排名低区间玩家
		int rankIndex = rankData.getRank();
		if (rankIndex >= rankList.size()) {
			return null;
		}
		int limit = rankData.getRank() + downSection;
		limit = limit > rankList.size() ? rankList.size() : limit;
		List<String> limitPlayers = new ArrayList<String>();
		// 取数据无需注意下标问题
		for (; rankIndex < limit; rankIndex ++) {
			RankData limitData = rankList.get(rankIndex);
			limitPlayers.add(limitData.getIdentify());
		}
		// 是否需要随机取数据
		if (limitPlayers.size() <= count) {
			for (String identify : limitPlayers) {
				PlayerData playerData = playerMap.get(identify);
				if (playerData == null) {
					continue;
				}
				vsPlayers.add(playerData);
			}
			return vsPlayers;
		}
		// 随机取数据
		return this.randomGetPlayer(limitPlayers, count);
	}
	
	/**
	 * 随机选取符合条件的排行数据
	 * 
	 * @param limitPlayers
	 * @param count
	 * @return
	 */
	private List<PlayerData> randomGetPlayer(final List<String> limitPlayers, final int count) {
		List<PlayerData> vsPlayers = new ArrayList<PlayerData>(count);
		// 取随机下标
		Set<Integer> setIndex = new HashSet<Integer>();
		while (setIndex.size() < count) {
			int index = GuaJiRand.randInt(limitPlayers.size() - 1);
			setIndex.add(index);
		}
		// 下标排序
		List<Integer> listIndex = new ArrayList<Integer>(setIndex);
		Collections.sort(listIndex, new Comparator<Integer>() {
			@Override
			public int compare(Integer arg0, Integer arg1) {
				if (arg0 > arg1) {
					return -1;
				}
				if (arg0 < arg1) {
					return 1;
				}
				return 0;
			}
			
		});
		// 取玩家数据数据
		for (Integer index : listIndex) {
			String identify = limitPlayers.get(index);
			PlayerData playerData = playerMap.get(identify);
			if (playerData == null) {
				continue;
			}
			vsPlayers.add(playerData);
		}
		return vsPlayers;
	}
	
	/**
	 * 积分计算
	 * 
	 * @param challengeScore
	 * @param defenderScore
	 * @param winTimes
	 * @param isChallenge
	 * @return
	 */
	public int calcScore(int challengeScore, int defenderScore, int winTimes, boolean isChallenge) {
		// 积分计算值
		int calcScore = 0;
		if (isChallenge) {
			// 积分差绝对值
			if (challengeScore < defenderScore) {
				int scoreDifference = defenderScore - challengeScore;
				battleLog.info(String.format("Score difference value is %d", scoreDifference));
				if (scoreDifference > CrossBattleCfg.getInstance().getScoreDifference()) {
					// 差值分计算
					int calcPercentage = (int) (scoreDifference * CrossBattleCfg.getInstance().getPercentage());
					calcScore = calcPercentage > CrossBattleCfg.getInstance().getScoreMax() ? CrossBattleCfg.getInstance().getScoreMax() : calcPercentage;
					battleLog.info(String.format("Percentage score is %d", calcScore));
				}
			}
			// 连胜积分添加
			if (winTimes > 0) {
				List<ContinueWinScoreCfg> continueWinList = ConfigManager.getInstance().getConfigList(ContinueWinScoreCfg.class);
				for (ContinueWinScoreCfg config : continueWinList) {
					if (winTimes >= config.getWinTimes()) {
						calcScore += config.getScore();
						battleLog.info(String.format("Continue win score %d", config.getScore()));
						break;
					}
				}
			}
			challengeScore = calcScore + CrossBattleCfg.getInstance().getWinScore();
			battleLog.info(String.format("Challenge score is %d", challengeScore));
		} else {
			defenderScore = calcScore + CrossBattleCfg.getInstance().getBeChanllengedFailedScore();
			battleLog.info(String.format("Defender score is %d", defenderScore));
		}
		// 返回积分
		if (isChallenge) {
			return challengeScore;
		}
		return defenderScore;
	}
}
