package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.BattleUtil;
import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class SkillHandler_4001 extends SkillHandler{

	public SkillHandler_4001(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 對敵方單體進行二連擊(params1)，第二擊(params1)必為暴擊且附加目標當前生命5%(params2)傷害
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
		
		int Actcount = ActRole.getSKLCount(getSkillId(),targetRole.getPos());
		if (Actcount == -1) {
			return false;
		}
		
		int atkcount =(int)p1;
		
		if (Actcount == atkcount) {
			//攻擊力
			int atk =(int)ActRole.getAttck();
			
			double calhurt = BattleUtil.mul((double)targetRole.getHp(),p2);
			int addHurt = (int)BattleUtil.round(calhurt, 0);
			addHurt = Math.min(addHurt,atk*5); // 生命%數額外傷害的技能上限為atk5倍
			if (!checkSkillHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,1.0,addHurt,true,false)) {
				return false;
			}
		} else {
			return checkSkillHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,1.0,0,true,false);
		}
		
		return true;
	}
}
