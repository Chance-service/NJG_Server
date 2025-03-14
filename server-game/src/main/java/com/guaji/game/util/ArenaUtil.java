package com.guaji.game.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;

import com.guaji.game.config.ArenaBuyTimesCfg;
import com.guaji.game.config.NewMonsterCfg;
import com.guaji.game.config.RankAwardCfg;
import com.guaji.game.config.RankMatchCfg;
import com.guaji.game.config.RankNpcCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.ArenaEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.protocol.Arena.ArenaItemInfo;
import com.guaji.game.protocol.Arena.ArenaRoleInfo;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;

public class ArenaUtil {
	
	/**
	 * 生成玩家竞技场协议对象
	 * @param arenaEntity
	 * @return
	 */
	public static ArenaItemInfo.Builder genPlayerArenaInfo(ArenaEntity arenaEntity) {
		ArenaItemInfo.Builder info = ArenaItemInfo.newBuilder();
		int type = ArenaUtil.getRankAwardType();
		if (arenaEntity != null) {
			// 竞技场信息
			info.setIdentityType(GsConst.Arena.PLAYER_OPPONENT);
			info.setPlayerId(arenaEntity.getPlayerId());
			info.setRank(arenaEntity.getRank());
			
			SnapShotManager snapShotMan = SnapShotManager.getInstance();
			PlayerSnapshotInfo.Builder snapshotInfo = snapShotMan.getPlayerSnapShot(arenaEntity.getPlayerId());
			
			// 主角信息
			RoleInfo mainRoleInfo = snapshotInfo.getMainRoleInfo();
			info.setCfgItemId(mainRoleInfo.getItemId());
			info.setLevel(mainRoleInfo.getLevel());
			info.setName(mainRoleInfo.getName());
			// 职业
			//int roleItemId = mainRoleInfo.getItemId();
			//RoleCfg roleCfg = ConfigManager.getInstance().getConfigByKey(RoleCfg.class, roleItemId);
			//if(roleCfg != null) {
			info.setProf(mainRoleInfo.getProf());
			//}
			
		
			RankAwardCfg awardCfg = RankAwardCfg.getAwardCfgByRank(arenaEntity.getRank(),type);
			info.setRankAwardsStr(awardCfg.getAwardStr());
			info.setHeadIcon(snapshotInfo.getPlayerInfo().getHeadIcon());
			//公会
			//设置公会名称
			AllianceEntity playerAlliance = AllianceManager.getInstance().getAlliance(snapshotInfo.getAllianceInfo().getAllianceId());
			if(playerAlliance != null){
				info.setAllianceName(playerAlliance.getName());
				info.setAllianceId(snapshotInfo.getAllianceInfo().getAllianceId());
			}
				
			//设置出战佣兵
			List<Integer> figtinglist = snapshotInfo.getFightingRoleIdList();
			List<RoleInfo> mercenaryInfos = snapshotInfo.getMercenaryInfoList();
			
			int totalfight = 0;
			
			for (int itemId :figtinglist) {
				ArenaRoleInfo.Builder roleInfo = ArenaRoleInfo.newBuilder();
				roleInfo.setItemId(0);
				for(RoleInfo role : mercenaryInfos) {
					if (role.getItemId() == itemId) {
						roleInfo.setItemId(itemId);
						roleInfo.setStarLevel(role.getStarLevel());
						roleInfo.setSkinId(role.getSkinId());
						roleInfo.setFightValue(role.getFight());
						roleInfo.setLevel(role.getLevel());
						totalfight = totalfight + role.getFight();
					}
				}				
				info.addRoleItemInfo(roleInfo);
			}
			info.setFightValue(totalfight);		
		}
		return info;
	}

	/**
	 * 计算玩家可挑战的名次
	 * 
	 * @param playerArenaRank
	 * @return
	 * @throws MyException
	 */
	public static List<Integer> calcChallengeRank(int playerArenaRank) {
		List<Integer> rankList = new ArrayList<Integer>();
		List<RankMatchCfg> rankCfgs = ConfigManager.getInstance().getConfigList(RankMatchCfg.class);
		if (playerArenaRank > 20){
			for (RankMatchCfg rankCfg : rankCfgs) {
				int rank = 0;
				// 向上取整
				int low = (int) Math.ceil(playerArenaRank * rankCfg.getMinRate());
				int	high = (int) Math.ceil(playerArenaRank * rankCfg.getMaxRate()) - 1;
				try {
					rank = GuaJiRand.randInt(low, high);
				} catch (Exception e) {
					MyException.catchException(e);
				}
				rankList.add(rank);
			}
		} else if (playerArenaRank > 7) {
			if(SysBasicCfg.getInstance().getPageDefenderQty() > 6){
				throw new RuntimeException("SysBasicCfg pageDefenderQty can not over 6.");
			}
			
			while(rankList.size() < SysBasicCfg.getInstance().getPageDefenderQty()){
				int rank = 0;
				try {
					rank = GuaJiRand.randInt(1, playerArenaRank - 1);
				} catch (Exception e) {
					MyException.catchException(e);
				}
				
				if(!rankList.contains(rank)){
					rankList.add(rank);
				}
			}
		} else if(playerArenaRank > 0){
			rankList = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
			rankList.remove((Integer)playerArenaRank);
		}
		
		return rankList;
	}

//	/**
//	 * 根据排名获取竞技场每日排名奖励配置
//	 * @param rank
//	 * @return
//	 */
//	public static RankAwardCfg getAwardCfgByRank(int rank){
//		TreeMap<Object, RankAwardCfg> cfgMap = (TreeMap<Object, RankAwardCfg>) ConfigManager.getInstance().getConfigMap(RankAwardCfg.class);
//		for (Map.Entry<Object, RankAwardCfg> entry : cfgMap.entrySet()) {
//			RankAwardCfg cfg = entry.getValue();
//			if (rank <= cfg.getMinRank())
//				return cfg;
//		}
//		return cfgMap.lastEntry().getValue();
//	}
	
	/**
	 * 取出今日是日結算還是季結算
	 */
	
	public static int getRankAwardType() {
		Date openDate = SysBasicCfg.getInstance().getArenaStartDate();
		Date curDate = new Date();
		int day = GuaJiTime.calcBetweenDays(openDate, curDate);
		if ((day % SysBasicCfg.getInstance().getArenaCycleDay()) == 0) {
			return 1; // 季結算
		}
		return 0; // 日結算
	}
	
	
	/**
	 * 根据竞技场排行获取Npc配置
	 * 
	 * @param rank
	 * @return
	 */
	public static RankNpcCfg getNpcCfgByRank(int rank) {
		TreeMap<Object, RankNpcCfg> cfgMap = (TreeMap<Object, RankNpcCfg>) ConfigManager.getInstance().getConfigMap(RankNpcCfg.class);
		for (Map.Entry<Object, RankNpcCfg> entry : cfgMap.entrySet()) {
			RankNpcCfg cfg = entry.getValue();
			if (rank <= cfg.getMinRank())
				return cfg;
		}
		return cfgMap.lastEntry().getValue();
	}

	/**
	 * 根据MonsterCfg配置生成竞技场信息
	 * 
	 * @return
	 */
	public static ArenaItemInfo.Builder genRobotArenaInfo(int monsterId, int rank, String awardStr,RankNpcCfg npcCfg) {
		ArenaItemInfo.Builder info = ArenaItemInfo.newBuilder();
		NewMonsterCfg monsterCfg = ConfigManager.getInstance().getConfigByKey(NewMonsterCfg.class, monsterId);
		if (monsterCfg != null) {
			info.setIdentityType(GsConst.Arena.ROBOT_OPPONENT);
			info.setCfgItemId(monsterCfg.getid()); // 怪物隨機一隻當隊長頭像
			info.setRank(rank);
			info.setName("");
			info.setLevel(50);
			info.setProf(monsterCfg.getProfession());			
			
			int fightValue = 0; // 隊伍戰力
			for(int mId :npcCfg.getMonsterIdList()){
				NewMonsterCfg mcfg = ConfigManager.getInstance().getConfigByKey(NewMonsterCfg.class, mId);
				if (mcfg != null) {
					fightValue = fightValue + mcfg.getFight();
					ArenaRoleInfo.Builder roleInfo = ArenaRoleInfo.newBuilder();
					roleInfo.setItemId(mId);
					roleInfo.setFightValue(mcfg.getFight());
					roleInfo.setLevel(mcfg.getLevel());
					info.addRoleItemInfo(roleInfo);
				}
			}
			
			info.setFightValue(fightValue);

			info.setRankAwardsStr(awardStr);
		}
		return info;
	}

	/**
	 * 根据购买次数获得价格配置
	 * @param buyTimes
	 * @return
	 */
	public static ArenaBuyTimesCfg getPriceCfgByBuyTimes(int buyTimes){
		List<ArenaBuyTimesCfg> cfgs = ConfigManager.getInstance().getConfigList(ArenaBuyTimesCfg.class);
		for(ArenaBuyTimesCfg cfg : cfgs){
			if(buyTimes <= cfg.getMaxTimes())
				return cfg;
		}
		return cfgs.get(cfgs.size()-1);
	}
	
	/**
	 * 計算競技場季結算剩餘秒數
	 * 
	 */
	public static int calArenaSeasonSec() {
		long currentTime = GuaJiTime.getMillisecond();
		long cycleday = SysBasicCfg.getInstance().getArenaCycleDay();
		long begintime = SysBasicCfg.getInstance().getArenaStartDate().getTime();
		long caltime = 0;
		boolean isfind = false;
		for (int c = 1 ; c <= 10000 ;c++) {
			caltime = begintime+(cycleday*24*60*60*1000*c);
			if (caltime > currentTime) {
				isfind = true;
				break;
			}
		}
		if (isfind) {
			int surplusTime = (int) ((caltime - currentTime) / 1000);
			return Math.max(surplusTime, 0);
		}
		return -1;
	}
	
}
