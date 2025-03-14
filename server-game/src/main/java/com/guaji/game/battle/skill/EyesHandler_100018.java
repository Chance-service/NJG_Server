package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class EyesHandler_100018 extends SkillHandler {

	public EyesHandler_100018(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 首次施放技能時，對當前HP比例最高的敵人賦予"崩壞"(param1)
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
		
		if (ActRole.UsedSkillId(getSkillId())) {
			return false;
		} else {
			targetRole.addBuff((int)p1,battlefield.getBattleTime(), 0);
			ActRole.addUsedSkillId(getSkillId());
		}

		return true;
	}
}
