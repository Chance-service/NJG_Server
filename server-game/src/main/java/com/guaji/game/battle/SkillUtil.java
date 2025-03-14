package com.guaji.game.battle;

import java.util.List;

import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.protocol.Battle.BattleInfo;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;
import com.guaji.game.config.NewSkillCfg;
import com.guaji.game.config.SkillCfg;
import com.guaji.game.config.SkillLevelCfg;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.manager.SkillHandlerManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.Buff;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Skill.HPSkillInfo;

public class SkillUtil {
	
	/**
	 * 檢查技能效果
	 */
	public static boolean checkSkillEffect(Battlefield battlefield,NewBattleRole ActRole ,NewBattleRole targetRole,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo,int skillid) {
		SkillHandler skillHandler = SkillHandlerManager.getInstance().getSkillHandler(skillid);
		if (skillHandler == null) {
			return false;
		}
		if (!skillHandler.checkSkillAction(battlefield, ActRole, targetRole, ActionInfo, targetRoleInfo)) {
			return false;
		}
		return true;
	}
	
	/**
	 * 计算技能效果
	 * 
	 * @param battleRole
	 * @param skillId
	 * @param actions
	 */
	public static void calcSkillEffect(Battlefield battlefield, BattleRole battleRole, SkillLevelCfg skillLevelCfg, List<BattleInfo.Action.Builder> actions) {
		
	}
	
	/**
	 * 同步技能套装
	 * 
	 * @param player
	 * @param roleEntity
	 */
	public static void pushSkillInfo(Player player, RoleEntity roleEntity) {
		if (roleEntity == null) {
			List<RoleEntity> roleEntities = player.getPlayerData().getRoleEntities();
			for(RoleEntity entity : roleEntities) {
				pushSkillInfo(player, entity);
			}
			return;
		}
		
		HPSkillInfo.Builder skillInfoBuilder = HPSkillInfo.newBuilder();
		skillInfoBuilder.setRoleId(roleEntity.getId());
		
		// 开启的技能格子数
		int skillSlotNum = GameUtil.getSkillSlotNumByLevel(roleEntity);
		for (int i=0; i<GsConst.MAX_SKILL_COUNT; i++) {
			if (i < skillSlotNum) {
				skillInfoBuilder.addSkillId1(roleEntity.getSkillId(i));
				
				if (roleEntity.getType() == Const.roleType.MAIN_ROLE_VALUE) {
					List<Integer> skillId2List = roleEntity.getSkill2idList();
					List<Integer> skillId3List = roleEntity.getSkill3idList();
					if (i < skillId2List.size()) {
						skillInfoBuilder.addSkillId2(skillId2List.get(i));
					} else {
						skillInfoBuilder.addSkillId2(0);
					}
					
					if (i < skillId3List.size()) {
						skillInfoBuilder.addSkillId3(skillId3List.get(i));
					} else {
						skillInfoBuilder.addSkillId3(0);
					}
				}
			}
		}
		
		player.sendProtocol(Protocol.valueOf(HP.code.SKILL_BAG_S,skillInfoBuilder));
	}
	 /**
	  * 取技能表
	  */
	 public static NewSkillCfg getSkillCfg(int skillId) {
		NewSkillCfg cfg = ConfigManager.getInstance().getConfigByKey(NewSkillCfg.class, skillId);
		return cfg;
	
	 }
	 
	 /**
	  * 取技能CDTime
	  */
	 public static int getSkillCDTime(int skillId) {
		NewSkillCfg cfg = getSkillCfg(skillId);
		if (cfg != null) {
			return cfg.getCd();
		}
		 return -1;
	 }
	 /**
	  * 取技能消耗
	  */
	 public static int getSkillCost(int skillId) {
		NewSkillCfg cfg = getSkillCfg(skillId);
		if (cfg != null) {
			return cfg.getCost();
		}
		 return -1;
	 }
	 
	 /**
	  * 取技能類型 1.攻擊 2. 治癒 3.數值被動
	  */
	 public static int getSkillType(int skillId) {
		NewSkillCfg cfg = getSkillCfg(skillId);
		if (cfg != null) {
			return cfg.getSkillType();
		}
		 return -1;
	 }
	 
	 /**
	  * 取技能類型 1.攻擊 2. 治癒 3.數值被動
	  */
	 public static List<Double> getParams(int skillId) {
		NewSkillCfg cfg = getSkillCfg(skillId);
		if (cfg != null) {
			return cfg.getValues();
		}
		 return null;
	 }

}
