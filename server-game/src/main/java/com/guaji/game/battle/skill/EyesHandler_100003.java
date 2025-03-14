package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class EyesHandler_100003 extends SkillHandler {

	public EyesHandler_100003(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 角色施放技能時，對當前目標賦予"石化"(param1)2秒(param2)
	 */
	@Override
	public boolean checkSkillAction(Battlefield battlefield, NewBattleRole ActRole ,NewBattleRole targetRole
			,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {
		
		if (!targetRole.IsEnemy(ActRole.getPos())){
			return false;
		}
		
		List<Double> params = getParams();
		// 給予deBuff
		double p1 = params.get(0);
		double p2 = params.get(1);
		
		if (ActRole.UsedSkillId(getSkillId())) {
			return false;
		} else {
			targetRole.addBuff((int)p1,battlefield.getBattleTime(), (int)p2*1000);
			ActRole.addUsedSkillId(getSkillId());
		}
			
		return true;
	}

}
