package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;


public class PassiveHandler_90010 extends SkillHandler {

	public PassiveHandler_90010(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 戰鬥開始時，提高包含自身以及隊伍中賦予"鼓舞I"(param2)30秒(param1)；30秒後自身獲得"強攻"
	 */
	@Override
	public boolean checkSkillAction(Battlefield battlefield, NewBattleRole ActRole ,NewBattleRole targetRole
			,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {

		if (targetRole.IsEnemy(ActRole.getPos())){
			return false;
		}
		
		List<Double> params = getParams();
		// 給予Buff
		double p1 = params.get(0); //30sec
		double p2 = params.get(1); //buff 24
		double p3 = params.get(2); //buff 12
		
		if (targetRole.IsMyself(ActRole.getPos())){
			if (!targetRole.UsedSkillId(getSkillId())) {
				targetRole.addBuff((int)p2,battlefield.getBattleTime(),(int)p1*1000);
				targetRole.addUsedSkillId(getSkillId());
			} else {
				targetRole.removeBuffById((int)p2);
				targetRole.addBuff((int)p3,battlefield.getBattleTime(),0);
			}
		} else { //靈氣30會自動賦予其他玩家
			//targetRole.addBuff((int)p2,battlefield.getBattleTime(),(int)p1*1000);
		}
		
		return true;
	}
}
