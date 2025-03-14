package com.guaji.game.battle.skill;

import java.util.List;

import com.guaji.game.battle.Battlefield;

import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;

public class SkillHandler_500701 extends SkillHandler {

	public SkillHandler_500701(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 對敵方單體造成5次50%(param1)傷害，最後一擊造成150%(param2)傷害，並賦予"暈眩"(param3)2秒(param4)
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
		double p4 = params.get(3);
		
		int Actcount = ActRole.getSKLCount(getSkillId(),targetRole.getPos());
		if (Actcount == -1) {
			return false;
		}
		int atkcount = ActRole.getSATK(getSkillId());
		
		double useParams = (Actcount == atkcount)? p1 : p2;
		
		if (!checkSkillHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,useParams)) {
			return false;
		}
		
		targetRole.addBuff((int)p3,battlefield.getBattleTime(), (int)p4*1000);
		
		return true;

	}
}
