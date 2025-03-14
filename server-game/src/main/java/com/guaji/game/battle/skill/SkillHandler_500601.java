package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.BattleUtil;
import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;


public class SkillHandler_500601 extends SkillHandler {

	public SkillHandler_500601(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 對敵方單體造成200%(param1)傷害，若目標血量低於50%(param2)則提升為440%(param3)傷害
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
		
		
		double targetHpPer = BattleUtil.div((double)targetRole.getHp(),(double)targetRole.getMaxhp(),2);
		
		double useParams = (targetHpPer >= p2)? p1 : p3;
		
		if (!checkSkillHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,useParams)) {
			return false;
		}

		return true;
	}
}
