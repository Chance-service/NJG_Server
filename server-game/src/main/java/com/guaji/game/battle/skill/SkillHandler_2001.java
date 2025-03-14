package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class SkillHandler_2001 extends SkillHandler{

	public SkillHandler_2001(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 隨機對敵方單體攻擊3次(params1)，每次造成120%(params2)魔法傷害，此攻擊必定命中
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
		if (!ActRole.checkSKLTotal(getSkillId(),(int)p1)) {
			return false;
		}
		return checkSkillHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,p2,0,true,false);
	}
}
