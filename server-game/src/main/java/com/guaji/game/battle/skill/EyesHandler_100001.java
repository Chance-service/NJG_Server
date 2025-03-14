package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class EyesHandler_100001 extends SkillHandler {

	public EyesHandler_100001(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 當自身獲得3個(param1)以上debuff，立即驅散身上所有可驅散的debuff，並獲得"堅守I"(param2)10秒(param3)
	 */
	@Override
	public boolean checkSkillAction(Battlefield battlefield, NewBattleRole ActRole ,NewBattleRole targetRole
			,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {
		
		if (!targetRole.IsMyself(ActRole.getPos())) {
			return false;
		}
		
		
		List<Double> params = getParams();
		// 給予Buff
		double p2 = params.get(1);
		double p3 = params.get(2);
		
		if (ActRole.UsedSkillId(getSkillId())) {
			return false;
		} else {
			targetRole.removeDebuff();
			targetRole.addBuff((int)p2,battlefield.getBattleTime(), (int)p3*1000);
			ActRole.addUsedSkillId(getSkillId());
		}		
		return true;
		
	}
}
