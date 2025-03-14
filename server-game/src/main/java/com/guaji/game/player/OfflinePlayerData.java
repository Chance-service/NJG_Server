package com.guaji.game.player;

import java.util.List;

import org.guaji.db.DBManager;

import com.guaji.game.entity.ArchiveEntity;
import com.guaji.game.entity.AvatarEntity;
import com.guaji.game.entity.EquipEntity;
import com.guaji.game.entity.PlayerTalentEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.entity.RoleRingEntity;
import com.guaji.game.entity.SkillEntity;
import com.guaji.game.util.PlayerUtil;

/**
 * 离线的玩家数据，其实是PlayerData的适配器;
 * <p>
 * {@code SnapShotManager} 中的离线逻辑通过构建它来传递到
 * {@link PlayerUtil#refreshOfflineAttribute(PlayerData, RoleEntity)}中进行刷新逻辑;
 * 
 * @author crazyjohn
 *
 */
@SuppressWarnings("serial")
public class OfflinePlayerData extends PlayerData {

	public OfflinePlayerData() {
		super(null);
	}

	public void setTalentEntity(PlayerTalentEntity playerTalentEntity) {
		this.playerTalentEntity = playerTalentEntity;
	}

	public void setEquipEntities(List<EquipEntity> equipEntities) {
		this.equipEntities = equipEntities;

	}

	public void setSkillEntities(List<SkillEntity> skillEntities) {
		this.skillEntities = skillEntities;

	}

	public void setRoleRingEntities(List<RoleRingEntity> roleRingEntities) {
		this.roleRingEntities = roleRingEntities;

	}

	public void setRoleEntities(List<RoleEntity> roleEntities) {
		this.roleEntities = roleEntities;

	}
	
	public void setAarchiveEntity(ArchiveEntity avatarEntity) {
		this.archiveEntity=avatarEntity;

	}
	
}
