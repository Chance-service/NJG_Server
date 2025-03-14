package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class SkillHandler_600102 extends SkillHandler {

	public SkillHandler_600102(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 *MP高於30%(params1)時發動，對敵方全體造成350%(params2)物理傷害，並賦予"暈眩"(param3)5秒(param4)
	 */
	@Override
	public boolean checkSkillAction(Battlefield battlefield, NewBattleRole ActRole ,NewBattleRole targetRole
			,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {
		
		if (!targetRole.IsEnemy(ActRole.getPos())) {
			return false;
		}
		
		List<Double> params = getParams();
		double p2 = params.get(1);
		double p3 = params.get(2);
		double p4 = params.get(3);
				
		if (!checkSkillHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,p2)) {
			return false;
		}
		
		targetRole.addBuff((int)p3,battlefield.getBattleTime(), (int)p4*1000);
			
		return true;
	}
}
