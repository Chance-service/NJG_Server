package com.guaji.game.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.msg.Msg;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.ServerData;
import com.guaji.game.config.ActivityCfg;
import com.guaji.game.config.ActivityCfg.ActivityItem;
import com.guaji.game.config.NewMapCfg;
import com.guaji.game.config.RankGiftCft;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.MapStatisticsEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.module.activity.rankGift.RankGiftPlayerData;
import com.guaji.game.module.activity.rankGift.RankGiftServerStatus;
import com.guaji.game.module.activity.rankGift.RankGiftServerStatus.ArenaRankType;
import com.guaji.game.protocol.Activity2.HPRankGiftInfo;
import com.guaji.game.protocol.Activity2.HPRankGiftItem;
import com.guaji.game.protocol.Arena.ArenaItemInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Mail;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;

/**
 * 排名献礼活动全局管理器;
 */
public class RankGiftManager extends AppObj {
	/** 为NPC */
	private static final int NPC_RANK = 0;
	/** 为玩家 */
	private static final int PLAYER_RANK = 1;
	private static final int ARENA_TYPE = 1;
	private static final int EXP_TYPE = 2;

	/** 管理两倍的经验排名数 */
	private static int RANK_GIFT_PLAYER_NUM = SysBasicCfg.getInstance().getRankGiftPlayerNum() * 2;
	/** 根据经验排名的playerEntity列表 */
	private List<RankGiftPlayerData> expRankPlayerEntity;
	/** tick计数 */
	protected int tickIndex = 0;
	/** 排行各分段奖励配置 */
	private List<RankGiftCft> configList;
	/** 玩家所在地图信息 */
	private Map<Integer, NewMapCfg> playerMapCfg;
	/** 活动配置 */
	private ActivityItem activityCfgs;
	/** 开放时间 */
	private long openTime;
	/** 活动存在时间(包括开放时间和展示时间) */
	private long notCloseTime;

	public RankGiftManager(GuaJiXID xid) {
		super(xid);
		this.expRankPlayerEntity = new ArrayList<RankGiftPlayerData>();
		this.playerMapCfg = new HashMap<Integer, NewMapCfg>();
		this.configList = ConfigManager.getInstance().getConfigList(RankGiftCft.class);
		if (instance == null) {
			instance = this;
		}
	}

	/**
	 * 全局对象, 便于访问
	 */
	private static RankGiftManager instance = null;

	/**
	 * 获取全局实例对象
	 */
	public static RankGiftManager getInstance() {
		return instance;
	}

	/**
	 * 初始化
	 */
	public void init() {
		setRankGiftCfg();
		loadExpRankList();
	}

	/**
	 * 装载活动配置
	 */
	private void setRankGiftCfg() {
		List<ActivityItem> allActivityCfgs = ActivityCfg.getActivityItemList();
		for (ActivityItem activityItem : allActivityCfgs) {
			if (activityItem.getId() == Const.ActivityId.RANK_GIFT_VALUE) {
				this.activityCfgs = activityItem;
				break;
			}
		}
		openTime = ((int) activityCfgs.getParam("rankHours")) * 3600 * 1000;
		long hours = (int) activityCfgs.getParam("openHours");
		notCloseTime = hours * 3600 * 1000;
	}

	/**
	 * 更新, 子类在处理自身逻辑后需要调用父类接口
	 * 
	 * @return
	 */
	@Override
	public boolean onTick() {
		// if (++tickIndex % 200 == 0) {
		// RankGiftServerStatus serverStatus =
		// ServerData.getInstance().getServerStatus(
		// GsConst.ServerStatusId.RANK_GIFT, RankGiftServerStatus.class);
		// if (!serverStatus.isGrantAwards() && !isActivityNotEnd()) {
		// // 检测到如果活动截止了,就马上发奖;
		// toGetRankExp();
		// grantRankAwards();
		// }
		// tickIndex = 0;
		// }
		return true;
	}

	@Override
	public boolean onMessage(Msg msg) {
		if (isActivityNotEnd() && msg.getMsg() == GsConst.MsgType.RANK_GIFT_RESET_EXP_RANK) {
			int playerId = msg.getParam(0);
			String name = msg.getParam(1);
			int level = msg.getParam(2);
			long exp = msg.getParam(3);
			resetExpRank(playerId, name, level, exp);
			return true;
		} else if (msg.getMsg() == GsConst.MsgType.PLAYER_NAME_CHANGE) {
			int playerId = msg.getParam(0);
			String name = msg.getParam(1);
			playerChangeName(playerId, name);
			return true;
		}
		return super.onMessage(msg);
	}

	/**
	 * 重新进行经验排行
	 */
	protected void toGetRankExp() {
		loadStatusMap();
		for (RankGiftPlayerData rankData : expRankPlayerEntity) {
			int lastLogoutTime = getLogoutTime(rankData.getPlayerId());
			if (lastLogoutTime == 0)
				continue;
			countOfflineExp(rankData, lastLogoutTime);
		}
		Collections.sort(expRankPlayerEntity);
		setExpRank(expRankPlayerEntity);
	}

	/**
	 * 查询出玩家的进行到的地图信息;
	 */
	private void loadStatusMap() {
		StringBuffer ids = new StringBuffer();
		for (RankGiftPlayerData rankData : expRankPlayerEntity) {
			ids.append(rankData.getPlayerId() + ",");
		}
		if (ids.length() == 0) {
			return;
		}
		String idStr = ids.substring(0, ids.length() - 1);
		List<StateEntity> stateEntitys = DBManager.getInstance().query("from StateEntity where playerId in (" + idStr + ")");
		for (StateEntity stateEntity : stateEntitys) {
			int curMapId = stateEntity.getCurBattleMap();
			if (curMapId <= 0) {
				curMapId = NewMapCfg.getMinMapId();
			}
			NewMapCfg mapCfg = ConfigManager.getInstance().getConfigByKey(NewMapCfg.class, curMapId);
			playerMapCfg.put(stateEntity.getPlayerId(), mapCfg);
		}
	}

	/**
	 * 计算玩家离线经验
	 * 
	 * @param rankData
	 * @param lastLogoutTime
	 */
	private void countOfflineExp(RankGiftPlayerData rankData, int lastLogoutTime) {
//		int offlineTime = GuaJiTime.getSeconds() - lastLogoutTime;
//		List<MapStatisticsEntity> mapStatisticsEntities = DBManager.getInstance().query("from MapStatisticsEntity where playerId = ? and invalid = 0", rankData.getPlayerId());
//		MapStatisticsEntity mapStatisticsEntity = null;
//		if (mapStatisticsEntities != null && mapStatisticsEntities.size() > 0) {
//			mapStatisticsEntity = mapStatisticsEntities.get(0);
//		}
//		NewMapCfg mapCfg = playerMapCfg.get(rankData.getPlayerId());
//		if (mapCfg == null) {
//			return;
//		}
//		int fightTimesRate = Math.min(mapStatisticsEntity.getFightTimes(), mapCfg.getMaxFightTimes());
//		int canFightTimes = (int) Math.rint((offlineTime / 3600.0f) * fightTimesRate);
//		int winTimes = (int) Math.rint(canFightTimes * mapStatisticsEntity.getWinRate() * 0.01f);
//		int expAward = (int) Math.rint((offlineTime / 3600.0f) * mapStatisticsEntity.getExpRate());
//		if (expAward <= winTimes * mapCfg.getExpDrop()) {
//			expAward = winTimes * mapCfg.getExpDrop();
//		}
//		long exp = rankData.getPlayerExp();
//		rankData.setPlayerExp(exp + expAward);
	}

	/**
	 * 获得玩家上次离线时间,如果当前在线则返回0;
	 * 
	 * @param playerId
	 * @return
	 */
	private int getLogoutTime(int playerId) {
		if (PlayerUtil.queryPlayer(playerId) == null || !PlayerUtil.queryPlayer(playerId).isOnline()) {
			PlayerSnapshotInfo.Builder playerSnapShot = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
			return playerSnapShot.getLastLogoutTime();
		} else {
			return 0;
		}
	}

	/**
	 * 启服时，从DB捞取玩家经验数据
	 */
	private void loadExpRankList() {
		if (isActivityNotClose()) {
			if (!isActivityNotEnd()) {
				setExpRank();
			} else {
				List<PlayerEntity> playerEntitys = DBManager.getInstance().limitQuery(
						"from PlayerEntity where invalid = 0 order by level desc, exp desc", 0, RANK_GIFT_PLAYER_NUM);
				Collections.sort(playerEntitys);
				for (PlayerEntity playerEntity : playerEntitys) {
					expRankPlayerEntity.add(new RankGiftPlayerData(playerEntity));
				}
				setExpRank(expRankPlayerEntity);
			}
		}
	}

	/**
	 * 生成经验排行榜（活动截至但未关闭时）
	 */
	private void setExpRank() {
		RankGiftServerStatus serverStatus = ServerData.getInstance().getServerStatus(GsConst.ServerStatusId.RANK_GIFT, RankGiftServerStatus.class);
		List<Integer> expRankGift = serverStatus.getExpRankGift();
		for (int i = 0; i < expRankGift.size(); i++) {
			int playerId = expRankGift.get(i);
			PlayerSnapshotInfo.Builder playerSnapShot = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
			if (playerSnapShot == null || playerSnapShot.getMainRoleInfo() == null) {
				continue;
			}
			expRankPlayerEntity.add(new RankGiftPlayerData(playerId, playerSnapShot.getMainRoleInfo().getName(),
					playerSnapShot.getMainRoleInfo().getLevel(), playerSnapShot.getMainRoleInfo().getExp()));
		}
	}

	/**
	 * 当排行发生变化时,生成经验排行榜（活动开放时）
	 * 
	 * @param playerEntitys
	 */
	private void setExpRank(List<RankGiftPlayerData> rankGiftPlayerData) {
		RankGiftServerStatus serverStatus = ServerData.getInstance().getServerStatus(GsConst.ServerStatusId.RANK_GIFT, RankGiftServerStatus.class);
		serverStatus.clearExpRank();
		for (int i = 0; i < rankGiftPlayerData.size(); i++) {
			RankGiftPlayerData playerData = rankGiftPlayerData.get(i);
			serverStatus.addExpRank(playerData.getPlayerId());
		}
		ServerData.getInstance().updateServerData(GsConst.ServerStatusId.RANK_GIFT);
	}

	/**
	 * 构造排行协议对象
	 * 
	 * @param builder
	 * @param playerId
	 * @param rank
	 * @param playerName
	 * @param playerLevel
	 * @param playerExp
	 * @param isNPC
	 */
	protected void setParams(HPRankGiftItem.Builder builder, int playerId, int rank, String playerName,
			int playerLevel, long playerExp, int isNPC) {
		builder.setPlayerId(playerId);
		builder.setRank(rank);
		builder.setName(playerName);
		builder.setLevel(playerLevel);
		builder.setExp((int) playerExp);
		builder.setIsNPC(isNPC);
	}

	/**
	 * 刷新经验排名
	 * 
	 * @param player
	 * @param expAward
	 */
	private void resetExpRank(int playerId, String playerName, int level, long exp) {
		// 查看集合中是否有该玩家的信息
		for (RankGiftPlayerData rankGiftPlayerData : expRankPlayerEntity) {
			if (playerId == rankGiftPlayerData.getPlayerId()) {
				rankGiftPlayerData.setPlayerLevel(level);
				rankGiftPlayerData.setPlayerExp(exp);
				rankGiftPlayerData.setPlayerName(playerName);
				Collections.sort(expRankPlayerEntity);
				return;
			}
		}
		// 更新排名数据
		expRankPlayerEntity.add(new RankGiftPlayerData(playerId, playerName, level, exp));
		Collections.sort(expRankPlayerEntity);
		if (expRankPlayerEntity.size() > RANK_GIFT_PLAYER_NUM) {
			expRankPlayerEntity.subList(RANK_GIFT_PLAYER_NUM, expRankPlayerEntity.size()).clear();
		}
	}

	/**
	 * 填充RankInfo协议
	 * 
	 * @param arenaRank
	 * @param playerID
	 */
	public void arenaRank(HPRankGiftInfo.Builder arenaRank, int playerID) {
		RankGiftServerStatus serverStatus = ServerData.getInstance().getServerStatus(GsConst.ServerStatusId.RANK_GIFT,
				RankGiftServerStatus.class);
		if (isActivityNotEnd()) {
			serverStatus.clearArenaRank();
			ArenaManager arenaMan = ArenaManager.getInstance();
			int rank = -1;
			List<ArenaItemInfo.Builder> rankingList = arenaMan.getRankingList(SysBasicCfg.getInstance().getRankGiftPlayerNum());
			for (ArenaItemInfo.Builder arenaItem : rankingList) {
				HPRankGiftItem.Builder rankItem = HPRankGiftItem.newBuilder();
				if (arenaItem.getIdentityType() == GsConst.Arena.ROBOT_OPPONENT) {
					setParams(rankItem, arenaItem.getPlayerId(), arenaItem.getRank(), arenaItem.getName(), 0, 0, NPC_RANK);
				} else {
					if (arenaItem.getPlayerId() == playerID) {
						rank = arenaItem.getRank();
					}
					setParams(rankItem, arenaItem.getPlayerId(), arenaItem.getRank(), arenaItem.getName(), 0, 0, PLAYER_RANK);
				}
				arenaRank.setSelfRank(rank);
				arenaRank.addRankList(rankItem);
				serverStatus.addArenaRank(rankItem.getPlayerId(), rankItem.getIsNPC(), rankItem.getName());
			}
		} else {
			List<ArenaRankType> arenaRankGift = serverStatus.getArenaRankGift();
			int rank = -1;
			for (int i = 0; i < arenaRankGift.size(); i++) {
				HPRankGiftItem.Builder rankItem = HPRankGiftItem.newBuilder();
				if (arenaRankGift.get(i).getPlayerId() == playerID & arenaRankGift.get(i).getIsNPC() == PLAYER_RANK) {
					rank = arenaRankGift.get(i).getPlayerId();
				}
				setParams(rankItem, arenaRankGift.get(i).getPlayerId(), i + 1, arenaRankGift.get(i).getName(), 0, 0, arenaRankGift.get(i).getIsNPC());
				arenaRank.setSelfRank(rank);
				arenaRank.addRankList(rankItem);
			}
		}
	}

	/**
	 * 填充RankInfo协议
	 * 
	 * @param expRank
	 * @param player
	 */
	public void expRank(HPRankGiftInfo.Builder expRank, int playerID) {
		if (!isActivityNotEnd()) {
			setExpRank(expRankPlayerEntity);
		}
		int rank = -1;
		for (int i = 0; i < expRankPlayerEntity.size(); i++) {
			RankGiftPlayerData playerData = expRankPlayerEntity.get(i);
			HPRankGiftItem.Builder builder = HPRankGiftItem.newBuilder();
			if (playerData.getPlayerId() == playerID) {
				rank = i + 1;
			}
			setParams(builder, playerData.getPlayerId(), i + 1, playerData.getPlayerName(), playerData.getPlayerLevel(), playerData.getPlayerExp(), PLAYER_RANK);
			expRank.addRankList(builder);
		}
		expRank.setSelfRank(rank);
	}

	/**
	 * 活动截止的一刻发奖并数据落地
	 */
	protected void grantRankAwards() {
		RankGiftServerStatus serverStatus = ServerData.getInstance().getServerStatus(GsConst.ServerStatusId.RANK_GIFT, RankGiftServerStatus.class);
		if (serverStatus.isGrantAwards()) {
			return;
		}
		List<ArenaRankType> arenaRankGift = serverStatus.getArenaRankGift();
		List<Integer> expRankGift = serverStatus.getExpRankGift();

		grantArenaRankAwards(arenaRankGift);
		grantexpRankAwards(expRankGift);

		serverStatus.setGrantAwards(true);
		ServerData.getInstance().updateServerData(GsConst.ServerStatusId.RANK_GIFT);
	}

	/**
	 * 竞技场排行发奖;
	 * 
	 * @param arenaRankGift
	 */
	private void grantArenaRankAwards(List<ArenaRankType> arenaRankGift) {
		for (int i = 0; i < arenaRankGift.size(); i++) {
			ArenaRankType arenaRankType = arenaRankGift.get(i);
			AwardItems awardItems = getRankAward(ARENA_TYPE, i + 1);
			if (awardItems == null)
				continue;

			if (arenaRankType.getIsNPC() == PLAYER_RANK)
				MailManager.createMail(arenaRankType.getPlayerId(), Mail.MailType.Reward_VALUE,
						GsConst.MailId.RANK_GIFT_ARENA, "排行献礼活动:竞技场排名奖励发放", awardItems, String.valueOf(i + 1));
		}
	}

	/**
	 * 经验排行发奖;
	 * 
	 * @param expRankGift
	 */
	private void grantexpRankAwards(List<Integer> expRankGift) {
		for (int i = 0; i < expRankGift.size(); i++) {
			AwardItems awardItems = getRankAward(EXP_TYPE, i + 1);
			if (awardItems == null)
				continue;
			MailManager.createMail(expRankGift.get(i), Mail.MailType.Reward_VALUE, GsConst.MailId.RANK_GIFT_EXP, "排行献礼活动:经验排名奖励发放", awardItems, String.valueOf(i + 1));
		}
	}

	/**
	 * 根据排行,返回奖励String
	 * 
	 * @param rank
	 * @return
	 */
	private AwardItems getRankAward(int type, int rank) {
		String award = "";
		for (RankGiftCft rankGiftCft : configList) {
			if (rankGiftCft.isInStage(rank) && rankGiftCft.getType() == type) {
				award = rankGiftCft.getReward();
			}
		}
		return AwardItems.valueOf(award);
	}

	/**
	 * 活动是否正在开启(未关闭)
	 * 
	 * @return
	 */
	public boolean isActivityNotEnd() {
		long openLeftTime = ActivityManager.getInstance().openServerLeftTime(openTime);
		if (openLeftTime != -1) {
			return true;
		}
		return false;
	}

	/**
	 * 活动是否关闭(true:没有关闭;false:已关闭)
	 * 
	 * @return
	 */
	public boolean isActivityNotClose() {
		long closeLeftTime = ActivityManager.getInstance().openServerLeftTime(notCloseTime);
		if (closeLeftTime != -1) {
			return true;
		}
		return false;
	}

	/**
	 * 玩家改昵称之后改变排行榜
	 * 
	 * @param playerId
	 * @param name
	 */
	private void playerChangeName(int playerId, String name) {
		for (RankGiftPlayerData rankGiftPlayerData : expRankPlayerEntity) {
			if (playerId == rankGiftPlayerData.getPlayerId()) {
				rankGiftPlayerData.setPlayerName(name);
				break;
			}
		}
	}

	/**
	 * 看传入参数是否大于最后一名;
	 * 
	 * @param level
	 * @param exp
	 * @return
	 */
	public boolean compareWithLastExpRank(int level, long exp) {
		if (expRankPlayerEntity.size() < RANK_GIFT_PLAYER_NUM) {
			return true;
		}
		RankGiftPlayerData rankGiftPlayerData = expRankPlayerEntity.get(expRankPlayerEntity.size() - 1);
		if (level > rankGiftPlayerData.getPlayerLevel()
				|| (level == rankGiftPlayerData.getPlayerLevel() && exp > rankGiftPlayerData.getPlayerExp())) {
			return true;
		}
		return false;
	}
}