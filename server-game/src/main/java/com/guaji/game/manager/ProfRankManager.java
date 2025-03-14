package com.guaji.game.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.guaji.app.AppObj;
import org.guaji.cache.CacheObj;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.msg.Msg;
import org.guaji.thread.GuaJiTask;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.ProfRankAwardCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.player.PlayerData;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Mail.MailType;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.ProfRank.RankItemInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsConst.ProfType;

/**
 * 职业排名管理器
 */
public class ProfRankManager extends AppObj {

	private final int maxRankNum = SysBasicCfg.getInstance().getMaxProfRankNum();
	private final int maxRewardNum = SysBasicCfg.getInstance().getMaxProfRankRewardNum();

	private Map<Integer, Integer> warriorPlayerIdRankMap = new ConcurrentHashMap<Integer, Integer>();
	private Map<Integer, Integer> hunterPlayerIdRankMap = new ConcurrentHashMap<Integer, Integer>();
	private Map<Integer, Integer> masterPlayerIdRankMap = new ConcurrentHashMap<Integer, Integer>();
	private Map<Integer, Integer> allPlayerIdRankMap = new ConcurrentHashMap<Integer, Integer>();

	private List<PlayerEntity> warriorRanks = new LinkedList<PlayerEntity>();
	private List<PlayerEntity> hunterRanks = new LinkedList<PlayerEntity>();
	private List<PlayerEntity> masterRanks = new LinkedList<PlayerEntity>();
	private List<PlayerEntity> allRanks = new LinkedList<PlayerEntity>();

	private static ProfRankManager instance = null;

	public ProfRankManager(GuaJiXID xid) {
		super(xid);
		if (instance == null) {
			instance = this;
		}
	}

	public static ProfRankManager getInstance() {
		return instance;
	}

	public void init() {
		List<PlayerEntity> warriorList = DBManager.getInstance().limitQuery(
				"from PlayerEntity where prof = 1 and fightValue > 0 order by fightValue desc", 0, maxRankNum);
		warriorRanks.addAll(warriorList);

		for (int i = 0; i < warriorRanks.size(); i++) {
			warriorPlayerIdRankMap.put(warriorRanks.get(i).getId(), i);
		}

		List<PlayerEntity> hunterList = DBManager.getInstance().limitQuery(
				"from PlayerEntity where prof = 2 and fightValue > 0 order by fightValue desc", 0, maxRankNum);
		hunterRanks.addAll(hunterList);

		for (int i = 0; i < hunterRanks.size(); i++) {
			hunterPlayerIdRankMap.put(hunterRanks.get(i).getId(), i);
		}

		List<PlayerEntity> masterList = DBManager.getInstance().limitQuery(
				"from PlayerEntity where prof = 3 and fightValue > 0 order by fightValue desc", 0, maxRankNum);
		masterRanks.addAll(masterList);

		for (int i = 0; i < masterRanks.size(); i++) {
			masterPlayerIdRankMap.put(masterRanks.get(i).getId(), i);
		}

		List<PlayerEntity> allList = DBManager.getInstance()
				.limitQuery("from PlayerEntity where fightValue > 0 order by fightValue desc", 0, maxRankNum);
		allRanks.addAll(allList);

		for (int i = 0; i < allRanks.size(); i++) {
			allPlayerIdRankMap.put(allRanks.get(i).getId(), i);
		}
	}

	@Override
	public boolean onMessage(Msg msg) {
		if (msg.getMsg() == GsConst.MsgType.FIGHT_VALUE_CHANGE) {
			PlayerData playerData = (PlayerData) msg.getParam(0);
			updataRankSet(playerData, playerData.getMainRole().getProfession());
			updataRankSet(playerData, 0);
			return true;
		}
		return super.onMessage(msg);
	}

	/**
	 * 线程主执行函数
	 */
	@Override
	public boolean onTick() {
		return true;
	}

	/**
	 * 获取排名前topN名玩家数据
	 * 
	 * @param type 职业类型
	 * @param topN 前n名
	 * @return
	 */
	public Map<Integer, Integer> getTopNRank(int type, int topN) {
		Map<Integer, Integer> ret = new HashMap<Integer, Integer>();
		List<PlayerEntity> rankList = getRanksByType(type);
		for (int i = 0; i < topN; i++) {
			if (rankList.size() > i) {
				ret.put(rankList.get(i).getId(), i);
			}
		}

		return ret;
	}

	/**
	 * 更新玩家排名
	 * 
	 * @param player 玩家信息
	 */
	public void updataRankSet(PlayerData playerData, int prof) {
		// 职业
//		int prof = playerData.getMainRole().getRoleCfg().getProfession();
		List<PlayerEntity> ranks = getRanksByType(prof);

		PlayerEntity oldTop1 = null;
		PlayerEntity oldTop2 = null;
		PlayerEntity oldTop3 = null;
		if (ranks.size() >= 1)
			oldTop1 = ranks.get(0);
		if (ranks.size() >= 2)
			oldTop2 = ranks.get(1);
		if (ranks.size() >= 3)
			oldTop3 = ranks.get(2);

		Map<Integer, Integer> playerIdRank = getPlayerIdRankByType(prof);

		PlayerEntity playerEntity = playerData.getPlayerEntity();

		int playerId = playerEntity.getId();
		int playerFightValue = playerEntity.getFightValue();

		// 当前排名中所有用户数据
		int playerRank = Math.max(0, ranks.size() - 1);

		if (playerIdRank.containsKey(playerId)) {
			playerRank = playerIdRank.get(playerId);
			ranks.remove(playerRank);// 变化玩家移除榜单
		}

		int curIndex = 0;
		for (; curIndex < ranks.size(); curIndex++) {
			PlayerEntity entity = ranks.get(curIndex);
			if (entity.getFightValue() < playerFightValue) {
				ranks.add(curIndex, playerEntity);// 加入到适当的位置
				break;
			}
		}

		if (curIndex == ranks.size() && ranks.size() < maxRankNum) {
			ranks.add(playerEntity);// 不够总排名的个数，加入到 队列尾部
		}

		while (ranks.size() > maxRankNum) {// 超了从尾部开始移出
			ranks.remove(ranks.size() - 1);
		}

		PlayerEntity newTop1 = null;
		PlayerEntity newTop2 = null;
		PlayerEntity newTop3 = null;
		if (ranks.size() >= 1)
			newTop1 = ranks.get(0);
		if (ranks.size() >= 2)
			newTop2 = ranks.get(1);
		if (ranks.size() >= 3)
			newTop3 = ranks.get(2);

		/** 玩家称号变化---------------------------------------- */
		if (prof != 0) {
			if (oldTop1 != null && newTop1 != null) {
				if (oldTop1.getId() != newTop1.getId()) {
					// 排名第一的玩家发生了变化,需要修改称号
					Msg msg = Msg.valueOf(GsConst.MsgType.PROF_RANK_CHANGE,
							GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerData.getId()));
					msg.pushParam(oldTop1.getId()); // 老的第一
					msg.pushParam(oldTop1.getProf()); // 玩家的职业
					msg.pushParam(newTop1.getId()); // 新的第一
					msg.pushParam(1);
					GsApp.getInstance().postMsg(msg);
				}
			}

			if (oldTop2 != null && newTop2 != null) {
				if (oldTop2.getId() != newTop2.getId()) {
					Msg msg = Msg.valueOf(GsConst.MsgType.PROF_RANK_CHANGE,
							GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerData.getId()));
					msg.pushParam(oldTop2.getId()); // 老的第二
					msg.pushParam(oldTop2.getProf()); // 玩家的职业
					msg.pushParam(newTop2.getId()); // 新的第二
					msg.pushParam(2);
					GsApp.getInstance().postMsg(msg);
				}
			}

			if (oldTop3 != null && newTop3 != null) {
				if (oldTop3.getId() != newTop3.getId()) {
					Msg msg = Msg.valueOf(GsConst.MsgType.PROF_RANK_CHANGE,
							GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerData.getId()));
					msg.pushParam(oldTop3.getId()); // 老的第三
					msg.pushParam(oldTop3.getProf()); // 玩家的职业
					msg.pushParam(newTop3.getId()); // 新的第三
					msg.pushParam(3);
					GsApp.getInstance().postMsg(msg);
				}
			}
		}
		/** ---------------------------------------------------- */
		// 历史排名比当前名称更靠前
		if (playerRank < curIndex) {
			Set<Map.Entry<Integer, Integer>> entrys = playerIdRank.entrySet();
			/*
			for (Map.Entry<Integer, Integer> entry : entrys) {
				if (entry.getValue() >= playerRank && entry.getValue() <= curIndex) {
					playerIdRank.remove(entry.getKey());
				}
			}
			//变化的玩家名次向后排序
			for (int i = playerRank; i <= curIndex && i < ranks.size(); i++) {
				playerIdRank.put(ranks.get(i).getId(), i);
			}
			*/
			
			for (Map.Entry<Integer, Integer> entry : entrys) {
				if (entry.getValue() >= playerRank) {
					playerIdRank.remove(entry.getKey());
				}
			}
			//变化的玩家名次向后排序
			for (int i = playerRank;i < ranks.size(); i++) {
				playerIdRank.put(ranks.get(i).getId(), i);
			}

		} else if (playerRank > curIndex) {// 历史排名比当前排名更靠后
			Set<Map.Entry<Integer, Integer>> entrys = playerIdRank.entrySet();
			/*
			for (Map.Entry<Integer, Integer> entry : entrys) {
				if (entry.getValue() <= playerRank && entry.getValue() >= curIndex) {
					playerIdRank.remove(entry.getKey());
				}
			}
			//变化的玩家名次向前移动
			for (int i = curIndex; i <= playerRank && i < ranks.size(); i++) {
				playerIdRank.put(ranks.get(i).getId(), i);
			}*/
			
			for (Map.Entry<Integer, Integer> entry : entrys) {
				if (entry.getValue() >= curIndex) {
					playerIdRank.remove(entry.getKey());
				}
			}
			//变化的玩家名次向前移动
			for (int i = curIndex;i < ranks.size(); i++) {
				playerIdRank.put(ranks.get(i).getId(), i);
			}
		} else {// 排名相同
			playerIdRank.put(playerData.getId(), curIndex);
		}
	}

	/**
	 * 发放排行奖励
	 */
	@SuppressWarnings("unused")
	private void grantRankAward() {
		final Map<Integer, Integer> warriors = getTopNRank(ProfType.WARRIOR, maxRewardNum);
		final Map<Integer, Integer> masters = getTopNRank(ProfType.MASTER, maxRewardNum);
		final Map<Integer, Integer> hunters = getTopNRank(ProfType.HUNTER, maxRewardNum);
		final Map<Integer, Integer> all = getTopNRank(0, maxRewardNum);

		GsApp.getInstance().postCommonTask(new GuaJiTask() {
			@Override
			protected CacheObj clone() {
				return null;
			}

			@Override
			protected int run() {
				sendReward(warriors, masters, hunters, all);
				return 0;
			}
		});
	}

	/**
	 * 发送奖励
	 * 
	 * @param warriors
	 * @param masters
	 * @param hunters
	 */
	private void sendReward(Map<Integer, Integer> warriors, Map<Integer, Integer> masters,
			Map<Integer, Integer> hunters, Map<Integer, Integer> all) {

		for (Map.Entry<Integer, Integer> entry : warriors.entrySet()) {
			pushRewardMail(entry.getKey(), entry.getValue() + 1);
		}

		for (Map.Entry<Integer, Integer> entry : masters.entrySet()) {
			pushRewardMail(entry.getKey(), entry.getValue() + 1);
		}

		for (Map.Entry<Integer, Integer> entry : masters.entrySet()) {
			pushRewardMail(entry.getKey(), entry.getValue() + 1);
		}

		for (Map.Entry<Integer, Integer> entry : all.entrySet()) {
			pushRewardMail(entry.getKey(), entry.getValue() + 1);
		}
	}

	/**
	 * 发放奖励邮件
	 */
	private void pushRewardMail(int playerId, int rank) {

		ProfRankAwardCfg awardCfg = getAwardCfgByRank(rank);
		AwardItems awardItems = AwardItems.valueOf(awardCfg.getAwardStr());

		// 奖励邮件
		MailManager.createMail(playerId, MailType.Reward_VALUE, GsConst.MailId.PROF_RANK_REWARD, "战力排行奖励", awardItems,
				String.valueOf(rank));
	}

	/**
	 * 根据排名获取排名奖励配置
	 * 
	 * @param rank
	 * @return
	 */
	private ProfRankAwardCfg getAwardCfgByRank(int rank) {
		TreeMap<Object, ProfRankAwardCfg> cfgMap = (TreeMap<Object, ProfRankAwardCfg>) ConfigManager.getInstance()
				.getConfigMap(ProfRankAwardCfg.class);
		for (Map.Entry<Object, ProfRankAwardCfg> entry : cfgMap.entrySet()) {
			ProfRankAwardCfg cfg = entry.getValue();
			if (rank <= cfg.getMinRank())
				return cfg;
		}
		return cfgMap.lastEntry().getValue();
	}

	/**
	 * 获取玩家的排名
	 * 
	 * @param player 玩家信息
	 * @param type   玩家职业类型。
	 * @return
	 */
	public int getPlayerRank(PlayerData playerData, int type) {
		int playerId = playerData.getPlayerEntity().getId();
		// 职业
		int prof = playerData.getMainRole().getProfession();

		if (type == 0) {
			prof = 0;
		}
		Map<Integer, Integer> cur = getPlayerIdRankByType(prof);
		if (cur != null && cur.containsKey(playerId)) {
			return cur.get(playerId);
		}
		return -1;
	}

	/**
	 * 构建返回客户端协议
	 * 
	 * @param entitys
	 * @return
	 */
	public List<RankItemInfo.Builder> genRankBuilder(Map<Integer, Integer> entitys) {
		List<RankItemInfo.Builder> builders = new ArrayList<RankItemInfo.Builder>();
		SnapShotManager snapShotMan = SnapShotManager.getInstance();
		PlayerSnapshotInfo.Builder snapshotInfo = null;
		RoleInfo mainRoleInfo = null;

		for (Map.Entry<Integer, Integer> entry : entitys.entrySet()) {

			snapshotInfo = snapShotMan.getPlayerSnapShot(entry.getKey());
			if (snapshotInfo != null) {
				mainRoleInfo = snapshotInfo.getMainRoleInfo();
				RankItemInfo.Builder builder = RankItemInfo.newBuilder();
				builder.setLevel(mainRoleInfo.getLevel());
				builder.setCfgItemId(mainRoleInfo.getItemId());
				builder.setName(mainRoleInfo.getName());
				builder.setRank(entry.getValue() + 1);
				builder.setFightValue(mainRoleInfo.getMarsterFight());
				builder.setSignature(snapshotInfo.getPlayerInfo().getSignature());
				builder.setPlayerId(entry.getKey());
				builder.setTitle(snapshotInfo.getTitleInfo().getTitleId());
				builder.setRebirthStage(mainRoleInfo.getRebirthStage());
				builder.setAvatarId(mainRoleInfo.getAvatarId());
				builder.setHeadIcon(snapshotInfo.getPlayerInfo().getHeadIcon());
				// 设置公会名称
				AllianceEntity playerAlliance = AllianceManager.getInstance()
						.getAlliance(snapshotInfo.getAllianceInfo().getAllianceId());
				if (playerAlliance != null) {
					builder.setAllianceName(playerAlliance.getName());
					builder.setAllianceId(snapshotInfo.getAllianceInfo().getAllianceId());
				}
				
				List<Integer> figtinglist = snapshotInfo.getFightingRoleIdList();
				// 设置出战佣兵
				List<RoleInfo> mercenaryInfos = snapshotInfo.getMercenaryInfoList();
				for (RoleInfo role : mercenaryInfos) {
					if (figtinglist.contains(role.getRoleId())) {
						builder.setRoleItemId(role.getItemId());
						break;
					}
				}
				builders.add(builder);
			}
		}
		return builders;
	}

	/**
	 * 根据职业类型,获取对应的排行信息
	 * 
	 * @param type
	 * @return
	 */
	private List<PlayerEntity> getRanksByType(int type) {
		if (type == ProfType.WARRIOR) {
			return warriorRanks;
		} else if (type == ProfType.MASTER) {
			return masterRanks;
		} else if (type == ProfType.HUNTER) {
			return hunterRanks;
		} else if (type == 0) {
			return allRanks;
		}
		return null;
	}

	private Map<Integer, Integer> getPlayerIdRankByType(int type) {
		if (type == ProfType.WARRIOR) {
			return warriorPlayerIdRankMap;
		} else if (type == ProfType.MASTER) {
			return masterPlayerIdRankMap;
		} else if (type == ProfType.HUNTER) {
			return hunterPlayerIdRankMap;
		} else if (type == 0) {
			return allPlayerIdRankMap;
		}
		return null;
	}

	public int getMinFightValueFromByType(int type) {

		List<PlayerEntity> curRanks = getRanksByType(type);
		if (curRanks.size() <= maxRankNum) {
			return 0;
		}
		return curRanks.get(curRanks.size() - 1).getFightValue();

	}

	/**
	 * 获取职业排名
	 * 
	 * @param playerId
	 * @param prof
	 * @return
	 */
	public int getPlayerRankByPlayerId(int playerId, int prof) {
		Map<Integer, Integer> map = getPlayerIdRankByType(prof);
		Integer ret = map.get(playerId);
		if (ret != null)
			return ret;
		return -1;
	}

}
