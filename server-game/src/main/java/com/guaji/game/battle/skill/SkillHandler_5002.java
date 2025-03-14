package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class SkillHandler_5002 extends SkillHandler{

	public SkillHandler_5002(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 標記成功(Passive10005)時，對敵方單體造成80%(param1)物理傷害/2物理傷害兩次
	 */
	@Override
	public boolean checkSkillAction(Battlefield battlefield, NewBattleRole ActRole ,NewBattleRole targetRole
			,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {
		if (!targetRole.IsEnemy(ActRole.getPos())) {
			return false;
		}
		List<Double> params = getParams();
		double p1 = params.get(0);
		return checkSkillHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,p1);
	}
}
