package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.BattleUtil;
import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class SkillHandler_9902 extends SkillHandler {

	public SkillHandler_9902(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 *HP低於30%(params1)時發動，使我方全體賦予500%(params2)魔法攻擊力的護盾，BNB英雄額外賦予再生(params3)8秒(params4)
	 */
	@Override
	public boolean checkSkillAction(Battlefield battlefield, NewBattleRole ActRole ,NewBattleRole targetRole
			,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {
		
		if (targetRole.IsEnemy(ActRole.getPos())) {
			return false;
		}
		
		List<Double> params = getParams();
		//double p1 = params.get(0);
		double p2 = params.get(1);
		double p3 = params.get(2);
		double p4 = params.get(3);
		
		double atk = ActRole.getAttck();
		
		int addShield = (int)BattleUtil.round(BattleUtil.mul(atk,p2),0);
		
		int calshield = BattleUtil.calLogGetShield(targetRoleInfo.getNowShield(),targetRoleInfo.getNewShield());
		
		if (addShield != calshield) {
			return false;
		}
						
		if (targetRole.IsBNBHero()) {
			targetRole.addBuff((int)p3,battlefield.getBattleTime(), (int)p4*1000);
		}
		
		targetRole.setShield(targetRoleInfo.getNewShield());
			
		return true;
	}
}
