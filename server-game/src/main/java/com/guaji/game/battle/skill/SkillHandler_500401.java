package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.Battlefield;

import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class SkillHandler_500401 extends SkillHandler {

	public SkillHandler_500401(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 對敵方單體造成240%(param1)傷害，並賦予"自然印記I"(param2)10秒(param3)
	 */
	@Override
	public boolean checkSkillAction(Battlefield battlefield, NewBattleRole ActRole ,NewBattleRole targetRole
			,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {
		
		if (!targetRole.IsEnemy(ActRole.getPos())) {
			return false;
		}
		
		List<Double> params = getParams();
		double p1 = params.get(0);
		
		if (!checkSkillHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,p1)) {
			return false;
		}
		// 給予deBuff
		double p2 = params.get(1);
		double p3 = params.get(2);

		targetRole.addBuff((int)p2,battlefield.getBattleTime(), (int)p3*1000);
		
		return true;
	}
}
