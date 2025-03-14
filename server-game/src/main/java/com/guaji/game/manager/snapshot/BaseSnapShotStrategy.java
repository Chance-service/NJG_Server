package com.guaji.game.manager.snapshot;

import java.util.ArrayList;
import java.util.List;

import com.guaji.game.entity.*;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.os.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guaji.game.GsApp;
import com.guaji.game.ServerData;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.player.OfflinePlayerData;
import com.guaji.game.player.Player;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.ElementUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Player.PlayerInfo;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo.Builder;

/**
 * 基础快照实现;
 */
public abstract class BaseSnapShotStrategy implements ISnapShotStrategy {

	protected Logger logger = LoggerFactory.getLogger("Server");

	/** the last tick time */
	protected long lastTickTime = 0;

	@Override
	public boolean onTick() {
		long curTime = GsApp.getInstance().getCurrentTime();
		if (curTime - lastTickTime >= GsConst.ManObjTickPeriod.SNAPSHOT_MAN_TICK) {
			lastTickTime = curTime;
			// do tick things
			return doTickThings(curTime);
		}
		return true;
	}

	protected void evictCacheData(long curTime) {
		// do nothing
	}

	protected boolean doTickThings(long curTime) {
		// do evict
		evictCacheData(curTime);
		return true;
	}

	@Override
	public Builder getSnapShot(int playerId) {
		// 在线逻辑
		boolean isOnline = ServerData.getInstance().isPlayerOnline(playerId);
		if (isOnline) {
			// 在线玩家直接从PlayerObj中获取
			PlayerSnapshotInfo.Builder snapshotInfo = getOnlinePlayerSnapshotById(playerId);
			if (snapshotInfo != null) {
				return snapshotInfo;
			}
		}
		return querySnapShot(playerId);
	}

	/**
	 * 创建快照
	 * 
	 * @param playerId
	 * @return
	 */
	protected SnapshotEntity createSnapShotEntity(int playerId) {
		SnapshotEntity snapshotEntity = new SnapshotEntity();
		snapshotEntity.setPlayerId(playerId);

		// mock player data
		OfflinePlayerData playerData = new OfflinePlayerData();
		// 构造builder
		PlayerSnapshotInfo.Builder playerSnapshotInfo = PlayerSnapshotInfo.newBuilder();
		playerSnapshotInfo.setVersion(SysBasicCfg.getInstance().getPlayerSnapShotVersion());
		// 拉取玩家信息
		PlayerEntity playerEntity = loadPlayer(playerId);
		playerData.setPlayerEntity(playerEntity);

		// 真气系统
		PlayerTalentEntity playerTalentEntity = loadPlayerTalentEntity(playerId);
		playerData.setTalentEntity(playerTalentEntity);

		if (playerEntity != null) {
			PlayerTalentEntity playetTalentEntity = loadPlayerTalentEntity(playerId);
			int talentNum = playetTalentEntity != null ? playetTalentEntity.getTalentNum() : 0;
			PlayerInfo.Builder playerInfo = BuilderUtil.genPlayerBuilder(playerEntity, null, talentNum);
			playerSnapshotInfo.setPlayerInfo(playerInfo);
			playerSnapshotInfo.setPlayerId(playerId);

			// 拉取主角, 佣兵, 光环信息
			List<RoleEntity> roleEntities = loadRoleEntity(playerId);
			playerData.setRoleEntities(roleEntities);
			RoleEntity mainRoleEntity = playerData.getMainRole();
			if (mainRoleEntity == null) {
				logger.info("playerId:" + playerId + " mainRoleEntity:null");
				return null;
			}
			List<EquipEntity> equipEntities = loadEquipEntities(playerId);
			List<BadgeEntity> badgeEntities = loadBadgeEntities(playerId);
			List<SkillEntity> skillEntities = loadSkillEntities(playerId);
			List<RoleRingEntity> roleRingEntities = loadRoleRingEntity(playerId);
			List<ElementEntity> elementEntities = loadRoleElementEntity(playerId);
			List<FormationEntity> formationEntities = loadFormationEntities(playerId);
			
			ArchiveEntity archiveEntity=loadArchiveEntity(playerId);

			// set to playerData
			playerData.setEquipEntities(equipEntities);
			playerData.setSkillEntities(skillEntities);
			playerData.setRoleRingEntities(roleRingEntities);
			playerData.setElementEntities(elementEntities);
			playerData.setFormationEntities(formationEntities);
			playerData.setAarchiveEntity(archiveEntity);
			// 玩家工会信息
			PlayerAllianceEntity allianceEntity = loadPlayerAllianceEntity(playerId);
			if (allianceEntity != null) {
				playerSnapshotInfo.setAllianceInfo(BuilderUtil.genAllianceBuilder(allianceEntity));
			}
			
			playerData.setPlayerAllianceEntity(allianceEntity);
		
			// 佣兵信息
			for (RoleEntity role : playerData.getMercenary()) {
				if (role == null) {
					continue;
				}
				PlayerUtil.refreshOfflineAttribute(playerData, role);
				playerSnapshotInfo.addMercenaryInfo(BuilderUtil.genRoleBuilder(playerData, role,
						playerData.getEquipEntities(), playerData.getSkillEntities(), playerData.getElementEntities(),playerData.getBadgeEntities()));
			}
			// 主角信息
			mainRoleEntity.getProfession();
			PlayerUtil.refreshOfflineAttribute(playerData, mainRoleEntity);
			// 出战佣兵阵容信息
			FormationEntity formationEntity = playerData.getFormationByType(1);
			if (formationEntity != null) {
				List<Integer> fightRoleList = formationEntity.getFightingArray();
//				List<Integer> assistanceList = formationEntity.getAssistanceArrayList();
				playerSnapshotInfo.addAllFightingRoleId(fightRoleList);
				//playerSnapshotInfo.addAllFightingRoleId(assistanceList);
			}

			// 光环信息
			if (roleRingEntities != null) {
				for (RoleRingEntity roleRingEntity : roleRingEntities) {
					playerSnapshotInfo.addRingInfos(BuilderUtil.genRingInfoBuilder(roleRingEntity));
				}
			}

			// 主角和佣兵身上的装备信息
			List<Long> equipIdList = new ArrayList<Long>();
			for (int i = Const.equipPart.HELMET_VALUE; i <= Const.equipPart.NECKLACE_VALUE; i++) {
				equipIdList.add(mainRoleEntity.getPartEquipId(i));
			}

			List<RoleEntity> mercenaryList = playerData.getMercenary();
			for (int i = Const.equipPart.CUIRASS_VALUE; i <= Const.equipPart.LEGGUARD_VALUE; i++) {
				for (RoleEntity mercenaryEntity : mercenaryList) {
					equipIdList.add(mercenaryEntity.getPartEquipId(i));
				}
			}
			for (EquipEntity equipEntity : equipEntities) {
				if (equipIdList.contains(equipEntity.getId())) {
					playerSnapshotInfo.addEquipInfo(BuilderUtil.genEquipBuilder(equipEntity));
				}
			}
			// 元素
			for (ElementEntity elementEntity : elementEntities) {
				if (mainRoleEntity.checkElementInDress(elementEntity.getId())) {
					playerSnapshotInfo.addElementInfo(BuilderUtil.genElementBuilder(elementEntity));
				}
			}

			// 技能信息
			List<SkillEntity> skillEntitys = loadSkillEntities(playerId);
			for (SkillEntity skillEntity : skillEntitys) {
				playerSnapshotInfo.addSkillInfo(BuilderUtil.genSkillBuilder(skillEntity));
			}

			RoleInfo.Builder mainRoleInfo = BuilderUtil.genRoleBuilder(playerData, mainRoleEntity, equipEntities,
					skillEntities, elementEntities,badgeEntities);
			playerSnapshotInfo.setMainRoleInfo(mainRoleInfo);

			// 玩家称号信息
			TitleEntity titleEntity = loadPlayerTitleEntity(playerId);
			if (titleEntity != null) {
				playerSnapshotInfo.setTitleInfo(BuilderUtil.genTitleBuilder(titleEntity));
			}
			snapshotEntity.setSnapshotInfo(playerSnapshotInfo);

			return snapshotEntity;
		}
		logger.info("playerId:" + playerId + " playerEntity:null");
		return null;
	}

	/**
	 * 加载玩家信息
	 * 
	 * @return
	 */
	protected PlayerEntity loadPlayer(int playerId) {
		PlayerEntity playerEntity = null;
		List<PlayerEntity> playerEntities = DBManager.getInstance().query("from PlayerEntity where id = ?", playerId);
		if (playerEntities != null && playerEntities.size() > 0) {
			playerEntity = playerEntities.get(0);
		}
		return playerEntity;
	}

	/**
	 * 生成在线玩家快照对象
	 * 
	 * @param playerId
	 * @return
	 */
	protected PlayerSnapshotInfo.Builder getOnlinePlayerSnapshotById(int playerId) {
		try {
			Player player = PlayerUtil.queryPlayer(playerId);
			if (player != null && (!player.isOnline() || player.isAssembleFinish())) {
				return player.getPlayerData().getOnlinePlayerSnapshot();
			} else {
				Log.errPrintln("query snapshot player null: " + playerId);
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return null;
	}

	/**
	 * 加载角色列表(主角 + 佣兵)
	 * 
	 * @param playerId      玩家Id
	 * @param mercenaryList 佣兵列表
	 * @return 主角信息
	 */
	protected List<RoleEntity> loadRoleEntity(int playerId) {

		List<RoleEntity> roleEntities = DBManager.getInstance()
				.query("from RoleEntity where playerId = ? and invalid = 0 order by id asc", playerId);
		for (RoleEntity roleEntity : roleEntities) {
			roleEntity.convert();
		}
		return roleEntities;
	}

	/**
	 * 加载装备信息
	 * 
	 * @return
	 */
	protected List<EquipEntity> loadEquipEntities(int playerId) {
		List<EquipEntity> equipEntities = DBManager.getInstance()
				.query("from EquipEntity where playerId = ? and invalid = 0 order by id asc", playerId);
		return equipEntities;
	}

	protected List<BadgeEntity> loadBadgeEntities(int playerId) {
		List<BadgeEntity> badgeEntities = DBManager.getInstance()
				.query("from BadgeEntity where playerId = ? and invalid = 0 order by id asc", playerId);
		return badgeEntities;
	}

	/**
	 * 加载阵型信息
	 */
	protected List<FormationEntity> loadFormationEntities(int playerId) {
		List<FormationEntity> formationEntities = DBManager.getInstance()
				.query("from FormationEntity where playerId = ? and invalid = 0 order by id asc", playerId);
		for (FormationEntity formationEntity : formationEntities) {
			formationEntity.convertData();
		}
		return formationEntities;
	}

	/**
	 * 加载技能信息
	 * 
	 * @return
	 */
	protected List<SkillEntity> loadSkillEntities(int playerId) {
		List<SkillEntity> skillEntities = DBManager.getInstance()
				.query("from SkillEntity where playerId = ? and invalid = 0 order by id asc", playerId);
		return skillEntities;
	}

	/**
	 * 加载玩家工会信息
	 */
	protected PlayerAllianceEntity loadPlayerAllianceEntity(int playerId) {
		List<PlayerAllianceEntity> playerAlliances = DBManager.getInstance()
				.query("from PlayerAllianceEntity where playerId = ?", playerId);
		if (playerAlliances.size() > 0) {
			playerAlliances.get(0).init();
			return playerAlliances.get(0);
		}
		return null;

	}

	/**
	 * 加载称号信息
	 * 
	 * @param playerId
	 * @return
	 */
	protected TitleEntity loadPlayerTitleEntity(int playerId) {
		List<TitleEntity> titles = DBManager.getInstance().query("from TitleEntity where playerId=? and invalid=0",
				playerId);
		if (titles.size() > 0)
			return titles.get(0);
		return null;
	}

	protected PlayerTalentEntity loadPlayerTalentEntity(int playerId) {
		List<PlayerTalentEntity> talentEntityList = DBManager.getInstance()
				.query("from PlayerTalentEntity where playerId = ?", playerId);
		if (talentEntityList != null && talentEntityList.size() > 0) {
			PlayerTalentEntity playerTalentEntity = talentEntityList.get(0);
			playerTalentEntity.convertData();
			return playerTalentEntity;
		}
		return null;
	}

	/**
	 * 加载图鉴数据
	 */
	public ArchiveEntity loadArchiveEntity(int playerId) {

		ArchiveEntity archiveEntity = DBManager.getInstance().fetch(ArchiveEntity.class,
				"from ArchiveEntity where playerId = ? and invalid = 0",playerId);
		if(archiveEntity!=null) {
			archiveEntity.convert();
		}
	

		return archiveEntity;
	}

	/**
	 * 加载角色光环数据
	 * 
	 * @param playerId
	 * @return
	 */
	protected List<RoleRingEntity> loadRoleRingEntity(int playerId) {
		List<RoleRingEntity> roleRingEntities = DBManager.getInstance().query("from RoleRingEntity where playerId = ?",
				playerId);
		return roleRingEntities;
	}

	protected List<ElementEntity> loadRoleElementEntity(int playerId) {
		List<ElementEntity> elementEntities = DBManager.getInstance()
				.query("from ElementEntity where playerId = ? and invalid = 0 order by id asc", playerId);
		for (ElementEntity elementEntity : elementEntities) {
			elementEntity.convertData();
			ElementUtil.refreshAttribute(elementEntity);
		}
		return elementEntities;
	}
	
	/**
	 * 从数据库快照表中拉取玩家快照数据
	 * 
	 * @param playerId
	 * @return
	 */
	protected SnapshotEntity loadFromDB(int playerId) {
		List<SnapshotEntity> snapshotEntities = DBManager.getInstance().query("from SnapshotEntity where playerId = ?",
				playerId);
		if (snapshotEntities != null && snapshotEntities.size() > 0) {
			SnapshotEntity snapshotEntity = snapshotEntities.get(0);
			snapshotEntity.convertSnapshot();
			return snapshotEntity;
		}
		return null;
	}

	protected abstract Builder querySnapShot(int playerId);

}
