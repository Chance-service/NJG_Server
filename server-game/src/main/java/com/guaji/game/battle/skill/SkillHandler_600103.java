package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.BuffUtil;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;
import com.guaji.game.util.GsConst;

public class SkillHandler_600103 extends SkillHandler {

	public SkillHandler_600103(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 *HP低於50%(params1)時發動，呼喚浪潮場地持續30秒(param3)，並對敵方全體賦予"凍傷II"(param2)30秒(param3)；
	 	火屬性的目標額外賦予"凍結"(param4)5秒(param5)，再對敵方全體造成400%(param6)魔法傷害
	 */
	
	@Override
	public boolean checkSkillAction(Battlefield battlefield, NewBattleRole ActRole ,NewBattleRole targetRole
			,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {
		
		if (!targetRole.IsEnemy(ActRole.getPos())) {
			return false;
		}
		
		List<Double> params = getParams();
		double p2 = params.get(1);
		double p3 = params.get(2);
		double p4 = params.get(3);
		double p5 = params.get(4);
		double p6 = params.get(5);
		
		int Actcount = ActRole.getSKLCount(getSkillId(),targetRole.getPos());
		if (Actcount == -1) {
			return false;
		}
		
		if (Actcount == 1) {
			targetRole.addBuff((int)p2,battlefield.getBattleTime(), (int)p3*1000);
			// 取得轉換屬性
			List<Integer> tarNewElement = BuffUtil.forceChangeElement(targetRole);
			
			if (tarNewElement.size() > 0) {
				for(int element :tarNewElement) {
					if (element == GsConst.ElementType.FIRE) {
						targetRole.addBuff((int)p4,battlefield.getBattleTime(), (int)p5*1000);
						break;
					}
				}
			} else {
				if (targetRole.getElement() == GsConst.ElementType.FIRE) {
					targetRole.addBuff((int)p4,battlefield.getBattleTime(), (int)p5*1000);
				}
			}
			return true;
		}
				
		if (!checkSkillHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,p6)) {
			return false;
		}
		

		return true;
	}
}
