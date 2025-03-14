package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class EyesHandler_100006 extends SkillHandler {

	public EyesHandler_100006(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 戰鬥開始時，賦予自身"虔誠"(param1)
	 */
	@Override
	public boolean checkSkillAction(Battlefield battlefield, NewBattleRole ActRole ,NewBattleRole targetRole
			,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {

		if (!targetRole.IsMyself(ActRole.getPos())){
			return false;
		}
		
		List<Double> params = getParams();
		// 給予Buff
		double p1 = params.get(0);

		if (ActRole.UsedSkillId(getSkillId())) {
			return false;
		} else {
			targetRole.addBuff((int)p1,battlefield.getBattleTime(), 0);
			ActRole.addUsedSkillId(getSkillId());
		}
		
		return true;
	}
}
