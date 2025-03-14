package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.Battlefield;

import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class SkillHandler_501001 extends SkillHandler {

	public SkillHandler_501001(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 先驅散全部敵方目標BUFF，再造成350%(param1)魔法傷害，並賦予目標"崩壞"(param2)
	 */
	@Override
	public boolean checkSkillAction(Battlefield battlefield, NewBattleRole ActRole ,NewBattleRole targetRole
			,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {
		
		if (!targetRole.IsEnemy(ActRole.getPos())) {
			return false;
		}
		
		List<Double> params = getParams();
		double p1 = params.get(0);
		
		int Actcount = ActRole.getSKLCount(getSkillId(),targetRole.getPos());
		if (Actcount == -1) {
			return false;
		}
		
		if (Actcount == 1) {
			targetRole.removeAllGainBuff();
			return true;
		}
		
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
