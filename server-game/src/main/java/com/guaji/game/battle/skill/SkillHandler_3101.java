package com.guaji.game.battle.skill;

import java.util.List;

import org.guaji.log.Log;

import com.guaji.game.battle.BattleUtil;
import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.BuffUtil;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.battle.SkillHandler;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;
import com.guaji.game.util.GsConst;

public class SkillHandler_3101 extends SkillHandler {

	public SkillHandler_3101(int skillId) {
		super(skillId);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 指定當前HP比例最低的2位友方單體，為其恢復魔法攻擊力240%(params1)的HP
	 */
	@Override
	public boolean checkSkillAction(Battlefield battlefield, NewBattleRole ActRole ,NewBattleRole targetRole
			,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {
		
		//觸動被動 Passive_ID_10008 補隊友然後會補自己(取捕最多隊友)
		
		if (targetRole.IsEnemy(ActRole.getPos())) {
			return false;
		}
		
		if ((ActRole.Istrigger(GsConst.TriggerType.SKILL,SkillType.Passive_ID_10008))&&(targetRole.IsMyself(ActRole.getPos()))) { // 不在這個地方檢查
			return true; 
		}
		
		List<Double> params = getParams();
		double p1 = params.get(0);
		
		int atkStatus = targetRoleInfo.getStatus();

		int addhp = BattleUtil.calLogGetRecovery(targetRoleInfo.getNowHp(),targetRoleInfo.getNewHp());
		
		int maxadd = targetRole.getMaxhp() - targetRole.getHp();
		
		boolean isCri = (atkStatus == 2);
		//攻擊力
		double atk  = ActRole.getAttck();
		//基礎回復量
		double baseDmg = BattleUtil.mul((double)atk,p1);
		
		double criRate = 1.0;
		
		if (isCri) {
			criRate = BattleUtil.calFinalCriDmgRate(ActRole,targetRole,true,false);
		}
		
        //最終回復量(四捨五入)
        double buffValue = BuffUtil.checkHealBuffValue(ActRole);  //施法者造成治療buff
        double buffValue2 = BuffUtil.checkBeHealBuffValue(targetRole);    //目標受到治療buff
        double dmg = BattleUtil.mul(BattleUtil.mul(baseDmg, criRate),BattleUtil.mul(buffValue,buffValue2));//math.floor(baseDmg * criRate * buffValue * buffValue2 + 0.5)
		dmg = BattleUtil.calRoundValue(dmg, 0);
		
		int finaldmg = (int)dmg;
		
		boolean checkret = false;
		if (finaldmg >= addhp) {
			checkret = (addhp == finaldmg);
		} else {
			checkret = (addhp == maxadd);
		}
		
		if (!checkret) {
			Log.debugInfo("Skill BaseDmg:{},atk:{},ActBuff:{},targetBuff:{}"
					,baseDmg,atk,ActRole.getBuffList(),ActRole.getBuffList(),targetRole.getBuffList());
			Log.debugInfo("buffValue:{},buffValue2:{}",buffValue,buffValue2);
			Log.debugInfo("Skill finaldmg: {},checkaddhp: {}, criRate: {},skillId():{},ActPos:{},ActType:{},ActItemId:{},tarPos:{},tarType:{},tarItemId:{}",
					finaldmg,addhp,criRate,getSkillId(),ActRole.getPos(),ActRole.getRoleType(),ActRole.getitemId(),targetRole.getPos(),targetRole.getRoleType(),targetRole.getitemId());
		} else {
			
			if (ActRole.getSkilladdhp() < finaldmg) {
				ActRole.setSkilladdhp(finaldmg);
			}
		}
		
		return checkret;
		
	}
}
