package com.guaji.game.battle.skill;

import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class SkillHandler_599901 extends SkillHandler {

	public SkillHandler_599901(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 *(新手教學用技能)對全體目標造成100%目標血量傷害，此攻擊必定命中
	 */
	@Override
	public boolean checkSkillAction(Battlefield battlefield, NewBattleRole ActRole ,NewBattleRole targetRole
			,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {
		
		if (!targetRole.IsEnemy(ActRole.getPos())) {
			return false;
		}
		
//		List<Double> params = getParams();
//		double p1 = params.get(0);
//		double p2 = params.get(1);
//		double p3 = params.get(2);
				
		if (!checkSkillHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,1.0,targetRole.getHp())) {
			return false;
		}
			
		return true;
	}
}
