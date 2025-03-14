package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class SkillHandler_3102 extends SkillHandler {

	public SkillHandler_3102(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 攻擊時有45%(params1)機率觸發，對當前目標造成130%(params2)魔法傷害
	 */
	@Override
	public boolean checkSkillAction(Battlefield battlefield, NewBattleRole ActRole ,NewBattleRole targetRole
			,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {
		
		if (!targetRole.IsEnemy(ActRole.getPos())) {
			return false;
		}
		
		List<Double> params = getParams();
		double p2 = params.get(1);
		return checkSkillHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,p2);
	}
}
