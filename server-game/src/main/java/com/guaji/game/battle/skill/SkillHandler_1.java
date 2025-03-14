package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class SkillHandler_1 extends SkillHandler {

	public SkillHandler_1(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	*對敵方當前MP最高單體造成140%(params1)物理傷害，並賦予"沉默"(params2)3秒(params3)，且我方全體賦予"物理特化I"(params4)5秒(params5)，自身返回40點MP(params6)
	*該次傷害不計算該buff
	*buff與返回mp不受miss影響
	 */
	@Override
	public boolean checkSkillAction(Battlefield battlefield, NewBattleRole ActRole ,NewBattleRole targetRole
			,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {
		List<Double> params = getParams();
		double p1 = params.get(0);
		double p2 = params.get(1);
		double p3 = params.get(2);
		double p4 = params.get(3);
		double p5 = params.get(4);
//		double p6 = params.get(5);
	
		if (targetRole.IsEnemy(ActRole.getPos())) {
			if (!checkSkillHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,p1)) {
				return false;
			}
			//給予deBuff
			targetRole.addBuff((int)p2, battlefield.getBattleTime(), (int)p3*1000);
		} else {
			// 給予全員Buff
			targetRole.addBuff((int)p4, battlefield.getBattleTime(),(int)p5*1000);
			// 檢查給予自己MP
//			if (ActRole.IsMyself(targetRole.getPos())) {
//				int NewMp = ActionInfo.getNewMp();
//				if (NewMp > (int)p6) {
//					return false;
//				}
//				ActRole.setMp(NewMp);
//			}
		}
		return true;
	}

}
