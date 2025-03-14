package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class SkillHandler_2 extends SkillHandler {

	public SkillHandler_2(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 * MP高於70%(params1)時發動，獲得"免疫"(params2)及"守護"(params3)5秒(params4)
	 */
	@Override
	public boolean checkSkillAction(Battlefield battlefield, NewBattleRole ActRole ,NewBattleRole targetRole
			,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {
		
		if (!targetRole.IsMyself(ActRole.getPos())) {
			return false;
		}
		
		List<Double> params = getParams();
//		double p1 = params.get(0);
		double p2 = params.get(1);
		double p3 = params.get(2);
		double p4 = params.get(3);
//		double mpPercent =  BattleUtil.div((double)targetRole.getMp(),(double)targetRole.getMaxmp(),2);
//		if (mpPercent <= p1) {
//			return false;
//		}
		// 給予Buff
		targetRole.addBuff((int)p2,battlefield.getBattleTime(), (int)p4*1000);
		targetRole.addBuff((int)p3,battlefield.getBattleTime(), (int)p4*1000);
		return true;
	}
}
