package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.BattleUtil;
import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class SkillHandler_500901 extends SkillHandler {

	public SkillHandler_500901(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 對敵方單體造成400%(param1)傷害，若目標為物理職業則最後一下額外造成最大HP10%(param2)傷害；若目標為魔法職業則最後一下額外賦予"沉默"(param3)10秒(param4)
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
		double p4 = params.get(3);
		
		int Actcount = ActRole.getSKLCount(getSkillId(),targetRole.getPos());
		if (Actcount == -1) {
			return false;
		}
		int atkNum = ActRole.getSATK(getSkillId());
		
		boolean isLast = (Actcount == atkNum);
		boolean isPhytar = (!targetRole.IsMagic());
				
		if ((isPhytar)&&(isLast)) {
			double calhurt = BattleUtil.mul((double)targetRole.getMaxhp(),p2);
			int addHurt = (int)BattleUtil.round(calhurt, 0);
			if (!checkSkillHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,p1,addHurt)) {
				return false;
			}
		} else {
			if (!checkSkillHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,p1)) {
				return false;
			}
		}
		
		if ( !isPhytar && isLast){
			targetRole.addBuff((int)p3,battlefield.getBattleTime(), (int)p4*1000);
		}
		return true;

	}
}
