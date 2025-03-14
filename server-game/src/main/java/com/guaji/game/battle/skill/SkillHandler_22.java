package com.guaji.game.battle.skill;

import java.util.List;


import com.guaji.game.battle.Battlefield;

import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class SkillHandler_22 extends SkillHandler {

	public SkillHandler_22(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 * MP高於70%(params1)時發動，對物理攻擊力最高的敵方單體造成140%(params2)魔法傷害並賦予"致盲"(params3)5秒(params4)，此攻擊必定命中
	 */
	@Override
	public boolean checkSkillAction(Battlefield battlefield, NewBattleRole ActRole ,NewBattleRole targetRole
			,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {
		
		if (!targetRole.IsEnemy(ActRole.getPos())) {
			return false;
		}
		
		List<Double> params = getParams();
		//double p1 = params.get(0);
		double p2 = params.get(1);
		double p3 = params.get(2);
		double p4 = params.get(3);
		// 發動條件改訂在skill表
//		double mpPercent =  BattleUtil.div((double)targetRole.getMp(),(double)targetRole.getMaxmp(),2);
//		if (mpPercent <= p1) {
//			return false;
//		}
		
		if (!checkSkillHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,p2)) {
			return false;
		}
		// 給予deBuff
		targetRole.addBuff((int)p3,battlefield.getBattleTime(),(int)p4*1000);

		return true;
	}
}
