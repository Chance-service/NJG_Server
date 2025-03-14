package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.BattleUtil;
import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class SkillHandler_2002 extends SkillHandler{

	public SkillHandler_2002(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 對生命低於50%(params1)的敵方單體造成40%(params2)的魔法傷害
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

		double hpPercent =  BattleUtil.div((double)targetRole.getHp(),(double)targetRole.getMaxhp(),2);
		if (hpPercent > p1) {
			return false;
		}
		
		return checkSkillHurt(ActRole ,targetRole,ActionInfo,targetRoleInfo,p2);
		
	}
}
