package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class SkillHandler_2101 extends SkillHandler {

	public SkillHandler_2101(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 隨機對敵方單體攻擊3次(params1)，每次造成140%(params2)魔法傷害，並賦予"侵蝕"(params3)8秒(params4)
	 */
	@Override
	public boolean checkSkillAction(Battlefield battlefield, NewBattleRole ActRole ,NewBattleRole targetRole
			,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {
		
		if (!targetRole.IsEnemy(ActRole.getPos())) {
			return false;
		}
		
		List<Double> params = getParams();
		double p1 = params.get(0);
		double p2 = params.get(1);
		double p3 = params.get(2);
		double p4 = params.get(3);
		if (!ActRole.checkSKLTotal(getSkillId(),(int)p1)) {
			return false;
		}
		if (!checkSkillHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,p2,0,true,false)) {
			return false;
		}
		
		// 給予deBuff
		targetRole.addBuff((int)p3,battlefield.getBattleTime(),(int)p4*1000);
		
		return true;
	}
}
