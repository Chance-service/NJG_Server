package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class PassiveHandler_90001 extends SkillHandler {

	public PassiveHandler_90001(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 提高包含自身以及隊伍中所有英雄5%物理攻擊，受到傷害降低10%(param1)**靈氣33
	 */
	@Override
	public boolean checkSkillAction(Battlefield battlefield, NewBattleRole ActRole ,NewBattleRole targetRole
			,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {

		if (targetRole.IsEnemy(ActRole.getPos())){
			return false;
		}
		
		List<Double> params = getParams();
		// 給予Buff
		double p1 = params.get(0);
		
		if (targetRole.IsMyself(ActRole.getPos())) {
			if (ActRole.UsedSkillId(getSkillId())) {
				return false;
			} else {
				targetRole.addBuff((int)p1,battlefield.getBattleTime(),0);
				ActRole.addUsedSkillId(getSkillId());
			}			
		}
						
		return true;
	}
}
