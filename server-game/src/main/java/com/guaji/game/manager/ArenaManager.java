package com.guaji.game.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.guaji.app.AppObj;
import org.guaji.cache.CacheObj;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;
import org.guaji.thread.GuaJiTask;
import org.guaji.xid.GuaJiXID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guaji.game.GsApp;
import com.guaji.game.ServerData;
import com.guaji.game.config.ArenaSnapCreateRuleCfg;
import com.guaji.game.config.FunctionUnlockCfg;
import com.guaji.game.config.RankAwardCfg;
import com.guaji.game.config.RankNpcCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.config.TitleCfg;
import com.guaji.game.entity.ArenaEntity;
import com.guaji.game.entity.ArenaReportEntity;
import com.guaji.game.entity.ArenaSnapShotEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.manager.status.ArenaStatus;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Arena.ArenaItemInfo;
import com.guaji.game.protocol.Arena.HPArenaChallengeReportRes;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Mail.MailType;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.util.ArenaUtil;
import com.guaji.game.util.GsConst;

/**
 * 竞技场数据管理器
 */
public class ArenaManager extends AppObj {
	/**
	 * 日志记录器
	 */
	private static Logger logger = LoggerFactory.getLogger("Server");

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	/**
	 * 全局对象, 便于访问
	 */
	private static ArenaManager instance = null;

	/**
	 * 玩家竞技场数据 1. <playerId, ArenaEntity> 玩家Id索引 2. <rank, ArenaEntity> 竞技场排名索引
	 */
	private Map<Integer, ArenaEntity> playerIdArenaEntityMap;
	private Map<Integer, ArenaEntity> rankArenaEntityMap;

	/**
	 * 竞技场快照信息
	 */
	private Map<String, List<ArenaSnapShotEntity>> arenaSnapShotEntityMap;

	/**
	 * 获取全局实例对象
	 */
	public static ArenaManager getInstance() {
		return instance;
	}

	/**
	 * 发奖任务
	 */
	protected static class GrantArenaRankAwardTask extends GuaJiTask {
		// 排行榜信息
		private Map<Integer, ArenaEntity> arenaRankMap = null;

		// 构造
		protected GrantArenaRankAwardTask(Map<Integer, ArenaEntity> arenaRankMap) {
			this.arenaRankMap = arenaRankMap;
		}

		@Override
		protected int run() {
			ArenaManager.getInstance().grantArenaDayRankAward(arenaRankMap);
			return 0;
		}

		@Override
		protected CacheObj clone() {
			return null;
		}
	}

	/**
	 * 构造函数
	 */
	public ArenaManager(GuaJiXID xid) {
		super(xid);
		playerIdArenaEntityMap = new ConcurrentHashMap<Integer, ArenaEntity>();
		rankArenaEntityMap = new ConcurrentHashMap<Integer, ArenaEntity>();
		arenaSnapShotEntityMap = new ConcurrentHashMap<String, List<ArenaSnapShotEntity>>();
		if (instance == null) {
			instance = this;
		}
	}

	/**
	 * 数据加载
	 */
	public boolean init() {
		List<ArenaEntity> arenaEntitys = DBManager.getInstance()
				.query("from ArenaEntity where invalid = 0 order by rank asc");
		if (arenaEntitys == null) {
			return false;
		}

		Map<Integer, ArenaEntity> tmpPIndexRankMap = new ConcurrentHashMap<Integer, ArenaEntity>();
		Map<Integer, ArenaEntity> tmpRIndexRankMap = new ConcurrentHashMap<Integer, ArenaEntity>();
		// 该修复算法依赖一个排序好的排名序列
		int correctCount = 0;
		for (ArenaEntity rankInfo : arenaEntitys) {
			int rank = rankInfo.getRank();
			int correctRank = rank;

			// 由于来源数据是一个排序好的序列，所以序列中的某个元素只能和之前的重复，不会和之后的重复
			boolean isRepeat = false;
			do {
				isRepeat = tmpRIndexRankMap.containsKey(correctRank);
				if (isRepeat) {
					correctRank++;
				}
			} while (isRepeat);

			// 修正数据落地
			if (rank != correctRank) {
				rankInfo.setRank(correctRank);
				DBManager.getInstance().update(rankInfo);
				correctCount++;
			}

			tmpPIndexRankMap.put(rankInfo.getPlayerId(), rankInfo);
			tmpRIndexRankMap.put(rankInfo.getRank(), rankInfo);
		}

		playerIdArenaEntityMap = tmpPIndexRankMap;
		rankArenaEntityMap = tmpRIndexRankMap;

		// 若无数据提前生成

		logger.info("correct repeat challenge rank, correct count:" + correctCount);

		/*
		 * if (!loadArenaSnap()) { logger.info("loadArenaSnap  error:"); return false;
		 * 
		 * }
		 */
		return true;
	}

	/**
	 * 线程主执行函数
	 */
	@Override
	public boolean onTick() {
		// 发奖
		ArenaStatus arenaStatus = ServerData.getInstance().getServerStatus(GsConst.ServerStatusId.ARENA,
				ArenaStatus.class);
		if (arenaStatus.canGrantTodayAward()) {
			// 设置发奖
			arenaStatus.setLastGrantAwardTime(GuaJiTime.getSeconds());
			ServerData.getInstance().updateServerData(GsConst.ServerStatusId.ARENA);

			Map<Integer, ArenaEntity> arenaRankMap = new HashMap<Integer, ArenaEntity>();
			arenaRankMap.putAll(rankArenaEntityMap);

			// 记录排名信息
			logger.info("today arena rank award post task: " + GuaJiTime.getTimeString());
			StringBuilder stringBuilder = new StringBuilder(4096);
			for (Map.Entry<Integer, ArenaEntity> entry : arenaRankMap.entrySet()) {
				int rank = entry.getKey();
				int playerId = entry.getValue().getPlayerId();
				stringBuilder.append("rank: " + rank + ", playerId: " + playerId + "\r\n");
			}
			logger.info(stringBuilder.toString());
			// 投递发奖任务
			GsApp.getInstance().postCommonTask(new GrantArenaRankAwardTask(arenaRankMap));
		}
		// 生成pvp 快照
		saveArenaSnap();
		return true;
	}

	/**
	 * 加载 pvp 镜像数据
	 */
	public boolean loadArenaSnap() {

		// 加载竞技场历史快照信息
		List<ArenaSnapShotEntity> arenaSnapShotEntitys = DBManager.getInstance()
				.query("from ArenaSnapShotEntity where invalid = 0 order by systype asc,builddate asc,rank asc");

		if (arenaSnapShotEntitys == null) {
			return false;
		}

		for (ArenaSnapShotEntity arenaSnapShot : arenaSnapShotEntitys) {
			String mapKey = String.valueOf(arenaSnapShot.getBuilddate()) + "_"
					+ String.valueOf(arenaSnapShot.getSystype());
			if (!arenaSnapShotEntityMap.containsKey(mapKey)) {
				List<ArenaSnapShotEntity> arenaSnapList = new LinkedList<>();
				arenaSnapList.add(arenaSnapShot);
				arenaSnapShotEntityMap.put(mapKey, arenaSnapList);
			} else {
				arenaSnapShotEntityMap.get(mapKey).add(arenaSnapShot);
			}

		}

		Map<Object, ArenaSnapCreateRuleCfg> snapCreateRuleMap = ConfigManager.getInstance()
				.getConfigMap(ArenaSnapCreateRuleCfg.class);
		Date am0Today = GuaJiTime.getAM0Date();
		for (ArenaSnapCreateRuleCfg item : snapCreateRuleMap.values()) {

			for (int i = 1; i < item.getHolddays(); i++) {
				Calendar c = Calendar.getInstance();
				c.setTime(am0Today);
				// 提前几天
				c.add(Calendar.DAY_OF_YEAR, i * -1);
				String strDate = GuaJiTime.DATE_FORMATOR_DAYNUM(c.getTime());
				String mapKey = strDate + "_" + String.valueOf(item.getSystype());

				if (arenaSnapShotEntityMap.containsKey(mapKey)) {
					continue;
				}
				List<ArenaSnapShotEntity> arenaSnapList = new LinkedList<>();
				arenaSnapShotEntityMap.put(mapKey, arenaSnapList);
				List<ArenaEntity> topNArenaEntitys = getTopNArenaDataPercent(item.getMaxRank());//20210527改取用百分去取去
				ArenaSnapShotEntity arenaSnapShotEntity = null;
				for (int index = 0; index < topNArenaEntitys.size(); index++) {
					ArenaEntity arenaEntity = topNArenaEntitys.get(index);
					int rank = index + 1;
					if (arenaEntity == null) {
						RankNpcCfg npcCfg = ArenaUtil.getNpcCfgByRank(rank);
						int monsterId = npcCfg.getOneMonsterId();
						if (npcCfg.getId() == 1) {
							monsterId = npcCfg.getMonsterIdList().get(rank - 1);
						}
						arenaSnapShotEntity = new ArenaSnapShotEntity(monsterId, rank, item.getSystype(), 2,
								c.getTime(), null);
					} else {
						PlayerSnapshotInfo.Builder snapshotInfo = SnapShotManager.getInstance()
								.getPlayerSnapShot(arenaEntity.getPlayerId());
						arenaSnapShotEntity = new ArenaSnapShotEntity(arenaEntity.getPlayerId(), rank,
								item.getSystype(), 1, c.getTime(), snapshotInfo);
					}
					if (DBManager.getInstance().create(arenaSnapShotEntity)) {
						arenaSnapList.add(arenaSnapShotEntity);
					}
				}

			}
		}

		return true;
	}

	/**
	 * 
	 */
	public void saveArenaSnap() {
		Map<Object, ArenaSnapCreateRuleCfg> snapCreateRuleMap = ConfigManager.getInstance()
				.getConfigMap(ArenaSnapCreateRuleCfg.class);
		for (ArenaSnapCreateRuleCfg item : snapCreateRuleMap.values()) {
			long createSnapTime = GuaJiTime.getTimeHourMinute(item.getTime());
			if (GuaJiTime.getMillisecond() > createSnapTime) {
				Date am0Date = GuaJiTime.getAM0Date();
				String curDate = GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date());
				String key = curDate + "_" + String.valueOf(item.getSystype());
				if (!arenaSnapShotEntityMap.containsKey(key)) {// 没有该天数据生成
					List<ArenaSnapShotEntity> arenaSnapList = new LinkedList<>();
					arenaSnapShotEntityMap.put(key, arenaSnapList);
					List<ArenaEntity> topNArenaEntitys = getTopNArenaDataPercent(item.getMaxRank());//20210527改取用百分去取去
					ArenaSnapShotEntity arenaSnapShotEntity = null;
					for (int index = 0; index < topNArenaEntitys.size(); index++) {
						ArenaEntity arenaEntity = topNArenaEntitys.get(index);
						int rank = index + 1;
						if (arenaEntity == null) {
							RankNpcCfg npcCfg = ArenaUtil.getNpcCfgByRank(rank);
							int monsterId = npcCfg.getOneMonsterId();
							if (npcCfg.getId() == 1) {
								monsterId = npcCfg.getMonsterIdList().get(rank - 1);
							}
							arenaSnapShotEntity = new ArenaSnapShotEntity(monsterId, rank, item.getSystype(), 2, null);
						} else {
							PlayerSnapshotInfo.Builder snapshotInfo = SnapShotManager.getInstance()
									.getPlayerSnapShot(arenaEntity.getPlayerId());
							arenaSnapShotEntity = new ArenaSnapShotEntity(arenaEntity.getPlayerId(), rank,
									item.getSystype(), 1, snapshotInfo);
						}
						if (DBManager.getInstance().create(arenaSnapShotEntity)) {
							arenaSnapList.add(arenaSnapShotEntity);
							// 生成快照失败
							logger.info("create snap sucess, correct count:" + arenaSnapShotEntity.getBuilddate());
						}
					}
				}

				// 之前的数据删除掉
				Calendar c = Calendar.getInstance();
				c.setTime(am0Date);
				// 提前几天
				c.add(Calendar.DAY_OF_YEAR, item.getHolddays() * -1);
				Iterator<Map.Entry<String, List<ArenaSnapShotEntity>>> iterator = arenaSnapShotEntityMap.entrySet()
						.iterator();
				// 之前的数据删除
				while (iterator.hasNext()) {
					Map.Entry<String, List<ArenaSnapShotEntity>> entry = iterator.next();
					if (entry.getKey().split("_").length != 0) {
						Date buildDate = GuaJiTime.DATE_FORMATOR_DAYNUM(entry.getKey().split("_")[0]);
						if (buildDate.getTime() < c.getTime().getTime()) {
							// entry.getValue()
							CopyOnWriteArrayList<ArenaSnapShotEntity> cowList = new CopyOnWriteArrayList<ArenaSnapShotEntity>(
									entry.getValue());
							for (ArenaSnapShotEntity snapItem : cowList) {
								if (DBManager.getInstance().delete(snapItem)) {
									cowList.remove(snapItem);
								}
							}
							iterator.remove();// OK
						}
					}
				}
			}
		}
	}

	/**
	 * 发放竞技场每日排行奖励
	 */
	public void grantArenaDayRankAward(Map<Integer, ArenaEntity> arenaRankMap) {
		for (Map.Entry<Integer, ArenaEntity> entry : arenaRankMap.entrySet()) {
			int rank = entry.getKey();
			int playerId = entry.getValue().getPlayerId();
			int type = ArenaUtil.getRankAwardType();
			RankAwardCfg arenaAwardCfg = RankAwardCfg.getAwardCfgByRank(rank,type);
			AwardItems awardItems = AwardItems.valueOf(arenaAwardCfg.getAwardStr());

			// 竞技场奖励邮件（前端参数：今日发放奖励时的排名）
			MailManager.createMail(playerId, MailType.Reward_VALUE, GsConst.MailId.ARENA_AWARD, "每日竞技场排名奖励-排名" + rank,
					awardItems, String.valueOf(rank));

			// 前N名玩家周知（广播+世界聊天）
			int limitRank = SysBasicCfg.getInstance().getBroadcastWorldMsgRank();
			try {
				if (rank <= limitRank) {
					SnapShotManager snapShotMan = SnapShotManager.getInstance();
					PlayerSnapshotInfo.Builder snapshotInfo = snapShotMan.getPlayerSnapShot(playerId);
					if (snapshotInfo != null && snapshotInfo.getMainRoleInfo() != null) {
						RoleInfo mainRoleInfo = snapshotInfo.getMainRoleInfo();
						String msg = ChatManager.getMsgJson(SysBasicCfg.getInstance().getArenaRankBroadcast(), rank,
								mainRoleInfo.getName());
						GsApp.getInstance().broadcastChatWorldMsg(msg, msg);
					}
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		// 输出日志“今日奖励已发放”
		logger.info("today arena rank award already granted: " + GuaJiTime.getTimeString());
	}

	public List<ArenaSnapShotEntity> getArenaSnapShotListByKey(String dateSysType) {

		return arenaSnapShotEntityMap.get(dateSysType);
	}

	/**
	 * 消息响应
	 * 
	 * @param msg
	 * @return
	 */
	@Override
	public boolean onMessage(Msg msg) {
		if (msg.getMsg() == GsConst.MsgType.CHALLENGE_FINISHED) {
			onChallengeFinished(msg);
			return true;
		}
		return super.onMessage(msg);
	}

	/**
	 * 协议响应
	 * 
	 * @param protocol
	 * @return
	 */
	@Override
	public boolean onProtocol(Protocol protocol) {
		return super.onProtocol(protocol);
	}

	protected Map<Integer, ArenaEntity> getPlayerIdArenaEntityMap() {
		return playerIdArenaEntityMap;
	}

	protected Map<Integer, ArenaEntity> getRankArenaEntityMap() {
		return rankArenaEntityMap;
	}

	/**
	 * 获取前N名玩家竞技场数据，若对应排名上不存在玩家数据，则填充null
	 */
	public List<ArenaEntity> getTopNArenaData(int topN) {
		List<ArenaEntity> topNList = new ArrayList<ArenaEntity>();
		for (int rank = 1; rank <= topN; rank++) {
			if (rankArenaEntityMap.containsKey(rank)) {
				topNList.add(rankArenaEntityMap.get(rank));
			} else {
				topNList.add(null);
			}
		}
		return topNList;
	}
	
	/**
	 * 获取前N名玩家比例竞技场数据，若对应排名上不存在玩家数据，则填充null
	 */
	public List<ArenaEntity> getTopNArenaDataPercent(int topN) {
		List<ArenaEntity> topNList = new ArrayList<ArenaEntity>();
		int step = Math.max(rankArenaEntityMap.size(),100)/100;
		int rank = 1;
		for (int i = 0; i < topN; i++) {
			if (rankArenaEntityMap.containsKey(rank+i*step)) {
				topNList.add(rankArenaEntityMap.get(rank+i*step));
			} else {
				topNList.add(null);
			}
		}
		return topNList;
	}

	/**
	 * 挑战成功后交换排名
	 * 
	 * @param challengerId 挑战者PlayerId
	 * @param defenderId   守护者PlayerId
	 * @return
	 */
	private boolean swapAranaRank(int challengerId, int defenderId) {
		if (playerIdArenaEntityMap.containsKey(challengerId) && playerIdArenaEntityMap.containsKey(defenderId)) {
			ArenaEntity challengerArena = playerIdArenaEntityMap.get(challengerId);
			ArenaEntity defenderArena = playerIdArenaEntityMap.get(defenderId);
			int challengerRank = challengerArena.getRank();
			int defenderRank = defenderArena.getRank();
			// 删除历史排名
			rankArenaEntityMap.remove(challengerRank);
			rankArenaEntityMap.remove(defenderRank);
			// 交换排名
			challengerArena.setRank(defenderRank);
			defenderArena.setRank(challengerRank);
			rankArenaEntityMap.put(defenderArena.getRank(), defenderArena);
			rankArenaEntityMap.put(challengerArena.getRank(), challengerArena);
			// 数据落地
			defenderArena.notifyUpdate(true);
			challengerArena.notifyUpdate(true);
			return true;
		}
		return false;
	}

	/**
	 * 根据玩家Id获取对应的竞技场数据
	 * 
	 * @param playerId
	 * @return 查询不到时返回null
	 */
	private ArenaEntity getArenaEntityByPlayerId(int playerId) {
		return playerIdArenaEntityMap.get(playerId);
	}

	/**
	 * 根据排行名次获取对应的竞技场数据
	 * 
	 * @param playerId
	 * @return 查询不到时返回null
	 */
	private ArenaEntity getArenaEntityByRank(int rank) {
		return rankArenaEntityMap.get(rank);
	}

	/**
	 * 创建玩家竞技场数据
	 * 
	 * @return 创建成功后，返回创建的竞技场数据； 创建失败，返回 null
	 */
	private ArenaEntity createPlayerArenaEntity(int playerId) {
		// 玩家初始排行
		int sysMinRank = SysBasicCfg.getInstance().getPlayerMinInitRank();
		int initRank = sysMinRank + playerIdArenaEntityMap.size();
		ArenaEntity arena = new ArenaEntity(playerId, initRank);
		// 创建成功加入后再加入内存
		if (DBManager.getInstance().create(arena)) {
			playerIdArenaEntityMap.put(playerId, arena);
			rankArenaEntityMap.put(initRank, arena);
			return arena;
		}
		return null;
	}

	/**
	 * 设置玩家竞技场排行
	 * 
	 * @return 设置成功返回，修改后的玩家竞技场数据； 设置失败，返回null
	 */
	private ArenaEntity setPlayerRank(int playerId, int rank) {
		// 设置玩家排行时，被设置的rank上必须没有玩家数据
		if (playerIdArenaEntityMap.containsKey(playerId) && !rankArenaEntityMap.containsKey(rank)) {
			ArenaEntity arena = playerIdArenaEntityMap.get(playerId);
			int oldRank = arena.getRank();
			rankArenaEntityMap.remove(oldRank);
			arena.setRank(rank);
			rankArenaEntityMap.put(rank, arena);
			arena.notifyUpdate(true);
			return arena;
		}
		return null;
	}

	/**
	 * 获取排行榜信息
	 * 
	 * @return
	 */
	public List<ArenaItemInfo.Builder> getRankingList() {
		return getRankingList(SysBasicCfg.getInstance().getRankingListPlayerQty());
	}

	/**
	 * 获取排行榜信息
	 * 
	 * @return
	 */
	public List<ArenaItemInfo.Builder> getRankingList(int topN) {
		List<ArenaItemInfo.Builder> rankingList = new ArrayList<ArenaItemInfo.Builder>();
		List<ArenaEntity> topNArenaEntitys = getTopNArenaData(topN);
		int type = ArenaUtil.getRankAwardType();
		for (int index = 0; index < topNArenaEntitys.size(); index++) {
			ArenaEntity arenaEntity = topNArenaEntitys.get(index);
			int rank = index + 1;
			ArenaItemInfo.Builder info = null;
			if (arenaEntity == null) {
				RankNpcCfg npcCfg = ArenaUtil.getNpcCfgByRank(rank);
				int monsterId = npcCfg.getOneMonsterId();
				// 魔法数字, 策划要求特殊处理的, 为了前20名不出现重复的NPC
				if (npcCfg.getId() == 1) {
					monsterId = npcCfg.getMonsterIdList().get(rank - 1);
				}
				RankAwardCfg awardCfg = RankAwardCfg.getAwardCfgByRank(rank,type);
				info = ArenaUtil.genRobotArenaInfo(monsterId, rank, awardCfg.getAwardStr(),npcCfg);
			} else {
				info = ArenaUtil.genPlayerArenaInfo(arenaEntity);
			}
			rankingList.add(info);
		}
		return rankingList;
	}

	/**
	 * 获取玩家自身竞技场信息，如果不存在则创建
	 */
	public ArenaItemInfo.Builder getSelfArenaInfo(Player player) {
		PlayerData playerData = player.getPlayerData();
		int playerId = playerData.getId();
		ArenaEntity arenaEntity = getArenaEntityByPlayerId(playerId);
		if (arenaEntity == null) {
			if (FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.PVPARENA_Unlock)){
				arenaEntity = createPlayerArenaEntity(playerId);
			} else {
				return null;
			}
		}
		return ArenaUtil.genPlayerArenaInfo(arenaEntity);
	}

	/**
	 * 获取可挑战(守护者)列表 （有些东西很蛋疼，没办法，策划，你懂的）
	 */
	public List<ArenaItemInfo.Builder> getDefenderList(List<Integer> rankList) {
		List<ArenaItemInfo.Builder> defenderList = new ArrayList<ArenaItemInfo.Builder>();
		//List<Integer> tmpMonsterIds = new ArrayList<Integer>();// 只为了去除重复的monsterId
		int type = ArenaUtil.getRankAwardType();
		for (Integer rank : rankList) {
			ArenaEntity arenaEntity = getArenaEntityByRank(rank);
			ArenaItemInfo.Builder info = null;
			// 如果对应排名上没有玩家
			if (arenaEntity == null) {
				RankNpcCfg npcCfg = ArenaUtil.getNpcCfgByRank(rank);
//				if (npcCfg.getMonsterIdList().size() < SysBasicCfg.getInstance().getPageDefenderQty()) {
//					throw new RuntimeException("RankNpcCfg.MonsterIdList's size < SysBasicCfg.PageDefenderQty.");
//				}

				RankAwardCfg awardCfg = RankAwardCfg.getAwardCfgByRank(rank,type);
				if (npcCfg.getId() == 1) {
					int monsterId = npcCfg.getMonsterIdList().get(rank - 1);
					info = ArenaUtil.genRobotArenaInfo(monsterId, rank, awardCfg.getAwardStr(),npcCfg);
				} else {
					int monsterId = npcCfg.getOneMonsterId();
					
					info = ArenaUtil.genRobotArenaInfo(monsterId, rank, awardCfg.getAwardStr(),npcCfg);
				}
			} else {
				info = ArenaUtil.genPlayerArenaInfo(arenaEntity);
			}
			defenderList.add(info);
		}
		return defenderList;
	}

	/**
	 * 获取目前在该排名上的玩家Id，如果该排名上没有玩家则返回0
	 * 
	 * @return
	 */
	public int getPlayerIdByRank(int rank) {
		try {
			ArenaEntity arenaEntity = rankArenaEntityMap.get(rank);
			if (arenaEntity != null) {
				return arenaEntity.getPlayerId();
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return 0;
	}

	/**
	 * 获取目前在该排名上的玩家，如果该排名上没有玩家则返回null
	 * 
	 * @return
	 */
	public ArenaEntity getPlayerIdByEntity(int rank) {
		try {
			ArenaEntity arenaEntity = rankArenaEntityMap.get(rank);
			if (arenaEntity != null) {
				return arenaEntity;
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return null;
	}

	/**
	 * 获取指定玩家的rank
	 */
	public int getRankByPlayerId(int playerId) {
		ArenaEntity arenaEntity = getArenaEntityByPlayerId(playerId);
		if (arenaEntity != null) {
			return arenaEntity.getRank();
		}
		// 查不到返回-1
		return -1;
	}

	/**
	 * 挑战结束
	 * 
	 * @param msg
	 */
	private void onChallengeFinished(Msg msg) {
		// 发起挑战的玩家Id
		int challengerId = (int) msg.getParam(0);
		// 要挑战的排名
		int challeangeRank = (int) msg.getParam(1);
		// 如果挑战比自己排名还低的，则不进入排名交换
		int oldRank = getArenaEntityByPlayerId(challengerId).getRank();
		//挑戰對像排名需大於0
		if (oldRank > challeangeRank && challeangeRank > 0) {
			// 挑战结果
			int result = (int) msg.getParam(2);
			// 战报ID
			int reportId = (int) msg.getParam(3);
			// 被挑战者信息
			ArenaEntity arenaEntity = (ArenaEntity) msg.getParam(4);

			// 发起挑战者信息
			PlayerSnapshotInfo.Builder challengerSnapshot = SnapShotManager.getInstance()
					.getPlayerSnapShot(challengerId);
			RoleInfo challengerInfo = challengerSnapshot.getMainRoleInfo();
			String challengerName = challengerInfo.getName();
			int challengerLevel = challengerInfo.getLevel();
			int challengerItemId = challengerInfo.getItemId();
			int challengerRebirthStage = challengerInfo.getRebirthStage();
			int headIcon = challengerSnapshot.getPlayerInfo().getHeadIcon();
			String curTimeStr = sdf.format(GuaJiTime.getMillisecond());

			if (result == GsConst.Arena.CHALLENGE_SUCC) { // 挑战成功

				/** 如果被挑战的玩家排名是第一名,则玩家的称号需要发生改变。 */
				int challengeTitleId = TitleCfg.getTitleIdByTypeAndCondition(GsConst.MsgType.ARENA_RANK_CHARGE,
						challeangeRank); // 挑战的玩家的称号
				int playerTitleId = TitleCfg.getTitleIdByTypeAndCondition(GsConst.MsgType.ARENA_RANK_CHARGE, oldRank); // 当前的玩家的称号
				// 被挑战的称号存在,则需要发消息
				if (challengeTitleId != 0) {
					Msg msg1 = Msg.valueOf(GsConst.MsgType.ARENA_RANK_CHARGE,
							GuaJiXID.valueOf(GsConst.ObjType.PLAYER, challengerId));
					msg1.pushParam((arenaEntity != null) ? (arenaEntity.getPlayerId()) : 0); // 被挑战玩家Id,如果是NPC则传0
					msg1.pushParam(challengeTitleId); // 挑战的称号
					msg1.pushParam(playerTitleId); // 自己的称号
					GsApp.getInstance().postMsg(msg1);
				}

				int challengeTitleId2 = TitleCfg.getTitleIdByTypeAndCondition(GsConst.MsgType.ARENA_RANK_EXTENT,
						challeangeRank); // 挑战的玩家的称号
				int playerTitleId2 = TitleCfg.getTitleIdByTypeAndCondition(GsConst.MsgType.ARENA_RANK_EXTENT, oldRank); // 当前的玩家的称号
				if (challengeTitleId2 != playerTitleId2) {
					Msg msg2 = Msg.valueOf(GsConst.MsgType.ARENA_RANK_CHARGE,
							GuaJiXID.valueOf(GsConst.ObjType.PLAYER, challengerId));
					msg2.pushParam((arenaEntity != null) ? (arenaEntity.getPlayerId()) : 0); // 被挑战玩家Id,如果是NPC则传0
					msg2.pushParam(challengeTitleId2); // 挑战的称号
					msg2.pushParam(playerTitleId2); // 自己的称号
					GsApp.getInstance().postMsg(msg2);
				}

				/**
				 * ------------------------------------------------------------- ----------
				 */

				if (arenaEntity != null) {// 挑战玩家成功
					// 交换排名
					swapAranaRank(challengerId, arenaEntity.getPlayerId());
					if (ServerData.getInstance().isPlayerOnline(arenaEntity.getPlayerId())) {
						// 刷新被挑战者的排名
						GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, arenaEntity.getPlayerId());
						Msg defenderMsg = Msg.valueOf(GsConst.MsgType.SYNC_ARENA_INFO, xid);
						GsApp.getInstance().postMsg(defenderMsg);
					}

					// 向被挑战者发送邮件
					MailManager.createSysMail(arenaEntity.getPlayerId(), MailType.ARENA_ALL_VALUE,
							GsConst.MailId.ARENA_RANK_DROP_RECORD, "竞技场排名下降（竞技记录）", null, curTimeStr, challengerName,
							String.valueOf(challeangeRank), String.valueOf(oldRank), challengerName,
							String.valueOf(challengerId), String.valueOf(challengerLevel),
							String.valueOf(challengerItemId), String.valueOf(challengerRebirthStage),
							String.valueOf(reportId), String.valueOf(headIcon));

					MailManager.createSysMail(arenaEntity.getPlayerId(), MailType.ARENA_VALUE,
							GsConst.MailId.ARENA_RANK_DROP, "竞技场排名下降(邮箱)", null, curTimeStr, challengerName,
							String.valueOf(challeangeRank), String.valueOf(oldRank),
							String.valueOf(challengerRebirthStage), String.valueOf(reportId));

					// 被挑战者信息
					int defenderId = arenaEntity.getPlayerId();
					PlayerSnapshotInfo.Builder defenderSnapshot = SnapShotManager.getInstance()
							.getPlayerSnapShot(defenderId);
					RoleInfo defenderInfo = defenderSnapshot.getMainRoleInfo();
					String defenderName = defenderInfo.getName();
					int defenderLevel = defenderInfo.getLevel();
					int defenderItemId = defenderInfo.getItemId();
					int rebirthStage = defenderInfo.getRebirthStage();
					int defenderHeadIcon = defenderSnapshot.getPlayerInfo().getHeadIcon();
					// 向发起挑战者发送邮件
					MailManager.createMail(challengerId, MailType.ARENA_ALL_VALUE, GsConst.MailId.ARENA_RANK_UP,
							"竞技场排名上升", null, curTimeStr, defenderName, String.valueOf(oldRank),
							String.valueOf(challeangeRank), defenderName, String.valueOf(defenderId),
							String.valueOf(defenderLevel), String.valueOf(defenderItemId), String.valueOf(rebirthStage),
							String.valueOf(rebirthStage), String.valueOf(reportId), String.valueOf(defenderHeadIcon));

					// 前N名玩家名次变更通知
					int limitRank = SysBasicCfg.getInstance().getBroadcastWorldMsgRank();
					try {
						if (challeangeRank <= limitRank) {

							String chatMsg = ChatManager.getMsgJson(
									SysBasicCfg.getInstance().getArenaRankChangeWorldMsg(), challengerName,
									defenderName, challeangeRank);
							String worldMsg = ChatManager.getMsgJson(
									SysBasicCfg.getInstance().getArenaRankChangeBroadcast(), challengerName,
									challeangeRank);
							GsApp.getInstance().broadcastChatWorldMsg(worldMsg, chatMsg);
						}
					} catch (Exception e) {
						MyException.catchException(e);
					}

				} else { // 挑战NPC成功
					// 设置排名
					setPlayerRank(challengerId, challeangeRank);
				}

				ArenaEntity challengerArena = playerIdArenaEntityMap.get(challengerId);
				if (challengerArena != null) {
					GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, challengerId);
					QuestEventBus.fireQuestEvent(QuestEventType.JING_JI_CHANG_RANK, challengerArena.getRank(), xid);
				}
			} else if (result == GsConst.Arena.CHALLENGE_FAIL) { // 挑战失败
				if (arenaEntity != null) { // 挑战玩家失败
					// 向被挑战者发送邮件
					int defenderId = arenaEntity.getPlayerId();
					MailManager.createSysMail(defenderId, MailType.ARENA_ALL_VALUE,
							GsConst.MailId.ARENA_CHALLANGE_SELF_FAIL, "竞技场挑战自己失败", null, curTimeStr, challengerName,
							challengerName, String.valueOf(challengerId), String.valueOf(challengerLevel),
							String.valueOf(challengerItemId), String.valueOf(challengerRebirthStage),
							String.valueOf(reportId), String.valueOf(headIcon));

					// 被挑战者信息
					PlayerSnapshotInfo.Builder defenderSnapshot = SnapShotManager.getInstance()
							.getPlayerSnapShot(defenderId);
					RoleInfo defenderInfo = defenderSnapshot.getMainRoleInfo();
					String defenderName = defenderInfo.getName();
					int defenderLevel = defenderInfo.getLevel();
					int defenderItemId = defenderInfo.getItemId();
					int defenderRebirthStage = defenderInfo.getRebirthStage();
					int defenderHeadIcon = defenderSnapshot.getPlayerInfo().getHeadIcon();
					// 向发起挑战者发送邮件
					MailManager.createSysMail(challengerId, MailType.ARENA_ALL_VALUE,
							GsConst.MailId.ARENA_CHALLANGE_OTHER_FAIL, "竞技场自己挑战别人失败", null, curTimeStr, defenderName,
							defenderName, String.valueOf(defenderId), String.valueOf(defenderLevel),
							String.valueOf(defenderItemId), String.valueOf(defenderRebirthStage),
							String.valueOf(reportId), String.valueOf(defenderHeadIcon));
				}
			}
		}

		// 通知玩家
		GuaJiXID targetXID = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, challengerId);
		GsApp.getInstance().postMsg(targetXID, msg);

	}

	/**
	 * 把一个玩家从排行榜上删除，并删除数据库记录
	 */
	public void removeArenaRank(int playerId) {
		ArenaEntity arenaEntity = playerIdArenaEntityMap.get(playerId);
		if (arenaEntity != null) {
			playerIdArenaEntityMap.remove(playerId);
			rankArenaEntityMap.remove(arenaEntity.getRank());
			// arenaEntity.delete();
			DBManager.getInstance().delete(arenaEntity);
		}
	}

	/**
	 * 数据库拉取战报数据
	 * 
	 * @param reportId
	 */
	public HPArenaChallengeReportRes.Builder loadArenaReport(int reportId) {
		ArenaReportEntity arenaEntity = DBManager.getInstance().fetch(ArenaReportEntity.class,
				"from ArenaReportEntity where id=?", reportId);
		if (arenaEntity != null) {
			return arenaEntity.convertResultToBuilder();
		}
		return null;
	}
}
