package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class SkillHandler_1102 extends SkillHandler {

	public SkillHandler_1102(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 生命低於50%(params1)時，獲得"堅守II"(params2)10秒(params3)，並驅散自身所有DBUFF，每場戰鬥僅發動1次
	 */
	
	@Override
	public boolean checkSkillAction(Battlefield battlefield, NewBattleRole ActRole ,NewBattleRole targetRole
			,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {
		
		if (!targetRole.IsMyself(ActRole.getPos())) {
			return false;
		}
		
		List<Double> params = getParams();
//		double p1 = params.get(0);//BattleUtil.div((double)params.get(0),100.0,2);
		double p2 = params.get(1);
		double p3 = params.get(2);
		// 發動條件改訂在skill表
//		double hpPercent =  BattleUtil.div((double)targetRole.getHp(),(double)targetRole.getMaxhp(),2);
//		if (hpPercent > p1) {
//			return false;
//		}
		if (ActRole.UsedSkillId(getSkillId())) {
			return false;
		} else {
			targetRole.addBuff((int)p2,battlefield.getBattleTime(), (int)p3*1000);
			ActRole.addUsedSkillId(getSkillId());
		}
		
		return true;
	}
}
