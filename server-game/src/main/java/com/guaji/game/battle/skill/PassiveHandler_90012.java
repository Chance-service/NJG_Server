package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class PassiveHandler_90012 extends SkillHandler {

	public PassiveHandler_90012(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 當自身3秒(params1)內沒有受到攻擊時，賦予自身"精確II"(params2)；受到攻擊時變為"精確I"(params3)**開場時會獲得"精確II"
	 */
	@Override
	public boolean checkSkillAction(Battlefield battlefield, NewBattleRole ActRole ,NewBattleRole targetRole
			,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {

		if (!targetRole.IsMyself(ActRole.getPos())){
			return false;
		}
		
		List<Double> params = getParams();
		// 給予Buff
		//double p1 = params.get(0);
		double p2 = params.get(1);
		double p3 = params.get(2);
		
		if (!targetRole.UsedSkillId(getSkillId())) {
			targetRole.removeBuffById((int)p3);
			targetRole.addBuff((int)p2,battlefield.getBattleTime(),0);
		} else {
			targetRole.removeBuffById((int)p2);
			targetRole.addBuff((int)p3,battlefield.getBattleTime(),0);
			targetRole.removeUsedSkillId(getSkillId());
		}
		
		return true;
	}
}
