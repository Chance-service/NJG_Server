package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.BattleUtil;
import com.guaji.game.battle.Battlefield;

import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class SkillHandler_42 extends SkillHandler {

	public SkillHandler_42(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 * MP高於70%(params1)時發動，對隨機敵人造成5次(params2)25%(params3)物理傷害
	 */
	@Override
	public boolean checkSkillAction(Battlefield battlefield, NewBattleRole ActRole ,NewBattleRole targetRole
			,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {
		
		if (!targetRole.IsEnemy(ActRole.getPos())) {
			return false;
		}
		
		List<Double> params = getParams();
		double p1 = params.get(0);
		double p2 = params.get(1); // 攻擊幾次
		double p3 = params.get(2);
		//檢查攻擊次數
		if (!ActRole.checkSKLCount(getSkillId(),targetRole.getPos(),(int)p2)) {
			return false;
		}
		
		double mpPercent =  BattleUtil.div((double)ActRole.getMp(),(double)ActRole.getMaxmp(),2);
		if (mpPercent <= p1) {
			return false;
		}
		
		if (!checkSkillHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,p3,0,true,false)) {
			return false;
		}
		
		return true;
	}
}
