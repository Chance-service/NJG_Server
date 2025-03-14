package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.BattleUtil;
import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class SkillHandler_1001 extends SkillHandler {

	public SkillHandler_1001(int skillId) {
		super(skillId);
	}
	/**
	 * 無視目標物理與魔法防禦，對敵方單體造成140%(params1)物理傷害+目標生命上限5%(params2)傷害
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
		
		//攻擊力
		int atk =(int)ActRole.getAttck();
		
		double calhurt = BattleUtil.round(BattleUtil.mul((double)targetRole.getMaxhp(),p2),0);
		
		int aHurt = (int)calhurt ;
		aHurt = Math.min(aHurt, atk*5); //生命%數額外傷害的技能上限為atk5倍
		return checkSkillHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,p1,aHurt,false);
	}
}
