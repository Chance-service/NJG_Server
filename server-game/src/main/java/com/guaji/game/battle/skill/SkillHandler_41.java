package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.Battlefield;

import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class SkillHandler_41 extends SkillHandler {

	public SkillHandler_41(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 對魔法攻擊力最高的敵方單體造成160%(params1)物理傷害並賦予"魔力鎖鏈"(params2)5秒(params3)
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
		
		if (!checkSkillHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,p1)) {
			return false;
		}
		// 給予deBuff
		targetRole.addBuff((int)p2,battlefield.getBattleTime(),(int)p3*1000);

		return true;
	}
}
