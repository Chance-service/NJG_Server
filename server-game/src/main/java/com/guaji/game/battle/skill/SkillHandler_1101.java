package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.BattleUtil;
import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;
import com.guaji.game.util.GsConst;

public class SkillHandler_1101 extends SkillHandler {

	public SkillHandler_1101(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 無視目標物理與魔法防禦，對敵方單體造成200%(params1)物理傷害並恢復自身等量HP，且"嘲諷"(params2)敵方全體4秒(params3)
	 */
	@Override
	public boolean checkSkillAction(Battlefield battlefield, NewBattleRole ActRole ,NewBattleRole targetRole
			,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {
				
		List<Double> params = getParams();
		double p1 = params.get(0);
		int chkHurt = BattleUtil.calLogGetHurt(targetRoleInfo.getNowHp(),targetRoleInfo.getNewHp(),targetRoleInfo.getNowShield(),targetRoleInfo.getNewShield());
		boolean isHit = (chkHurt > 0) || (targetRole.Istrigger(GsConst.TriggerType.BUFF,GsConst.Buff.UNDEAD)); // 觸發攻擊
		
		if (targetRole.IsEnemy(ActRole.getPos())) {
			
			if (isHit) {
				if (!checkSkillHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,p1,0,false)) {
					return false;
				}
				// 恢復自身等量HP
				ActRole.setSkilladdhp(chkHurt);
			}

			// 給予deBuff
			double p2 = params.get(1);
			double p3 = params.get(2);
			targetRole.addBuff((int)p2,battlefield.getBattleTime(),(int)p3*1000);
		} else {
			if (ActRole.IsMyself(targetRole.getPos())) {
				return true;
			} else {
				return false;
			}
		}
			
		return true;
	}
}
