package com.guaji.game.battle;


import java.util.List;

import org.guaji.config.ConfigManager;
import org.guaji.log.Log;

import com.guaji.game.config.NewSkillCfg;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;
import com.guaji.game.util.GsConst;


public class SkillHandler {
	
	private int skillId ;
	
	private NewSkillCfg SkillCfg;
	
	public SkillHandler(int skillId) {
		this.skillId = skillId;
		this.SkillCfg = ConfigManager.getInstance().getConfigByKey(NewSkillCfg.class, this.skillId);
	}
	/**
	 * 技能行为
	 * 
	 * @param battlefield
	 * @param NewBattleRole
	 * @param NewBattleRole
	 * @param NewBattleRoleInfo
	 * @param NewBattleRoleInfo
	 */
	public boolean checkSkillAction(Battlefield battlefield, NewBattleRole ActRole ,NewBattleRole targetRole
			,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {
		return true;
	}
	/**
	 * 檢查技能傷害(default)
	 * @return
	 */
	public boolean checkSkillHurt(NewBattleRole ActRole ,NewBattleRole targetRole,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {
		return checkSkillHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,1.0,0,true,true);
	}
	/**
	 * 檢查技能傷害 (onlyparams)
	 * @return
	 */
	public boolean checkSkillHurt(NewBattleRole ActRole ,NewBattleRole targetRole,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo,double onlyparams) {
		return checkSkillHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,onlyparams,0,true,true);
	}
	
	/**
	 * 檢查技能傷害(addHurt)
	 * @return
	 */
	public boolean checkSkillHurt(NewBattleRole ActRole ,NewBattleRole targetRole,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo,double onlyparams,int addHurt) {
		return checkSkillHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,onlyparams,addHurt,true,true);
	}
	
	/**
	 * 檢查技能傷害(divSATA)
	 * @return
	 */
	public boolean checkSkillHurt(NewBattleRole ActRole ,NewBattleRole targetRole,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo,double onlyparams,int addHurt,boolean decHurt) {
		return checkSkillHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,onlyparams,addHurt,decHurt,true);
	}
	
	/**
	 * 檢查技能傷害(給予參數)
	 * @param,onlyparams(技能傷害倍率),addHurt(直接傷害值),decHurt(是否計算減傷),divSATK(是否傷害除連擊數)
	 * @return
	 */
	public boolean checkSkillHurt(NewBattleRole ActRole ,NewBattleRole targetRole,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo,double onlyparams,int addHurt,boolean decHurt,boolean divSATK) {
		
		int atkStatus = targetRoleInfo.getStatus();
		int chkHurt = BattleUtil.calLogGetHurt(targetRoleInfo.getNowHp(),targetRoleInfo.getNewHp(),targetRoleInfo.getNowShield(),targetRoleInfo.getNewShield());
		boolean isCri = (atkStatus == 2);
		
		if (targetRole.Istrigger(GsConst.TriggerType.BUFF,GsConst.Buff.UNDEAD)) {
			if (!targetRole.checkIsUndeadHP(targetRoleInfo,false)) {
				Log.debugInfo("ret:8 checkIsUndeadHP ==> skillId():{},ActPos:{},ActType:{},ActItemId:{},tarPos:{},tarType:{},tarItemId:{},tarNowShield:{},tarNewShield:{}",getSkillId(),ActRole.getPos(),ActRole.getRoleType(),ActRole.getitemId(),targetRole.getPos(),targetRole.getRoleType(),targetRole.getitemId(),targetRoleInfo.getNowShield(),targetRoleInfo.getNewShield());
				return false;
			} else {
				return true;
			}
		}
		
		//減傷
		double reduction = 0.0;
		
		if (decHurt) {
			reduction =	BattleUtil.calReduction(ActRole,targetRole,false);
		}
		//攻擊力
		double atk = ActRole.getAttck();
		//屬性加成
		double elementRate = BattleUtil.calElementRate(ActRole,targetRole);
		// Buff
		BuffValue buffval = BuffUtil.checkAllDmgBuffValue(ActRole,targetRole);
		//基礎傷害
		//double baseDmg = BattleUtil.mul(atk,BattleUtil.mul(BattleUtil.mul(BattleUtil.sub(1.0,reduction),elementRate),onlyparams)); //(int)(atk * (1 - reduction) * elementRate);
		double baseDmg = BattleUtil.mulmul(atk,BattleUtil.sub(1.0,reduction),elementRate,onlyparams,buffval.getAllMul());
		//暴傷
		double criRate = 1.0;
		
		if (isCri) {
			criRate = BattleUtil.calFinalCriDmgRate(ActRole,targetRole,false,false);
		}
		
		//double dmg = BattleUtil.add(BattleUtil.mul(baseDmg,criRate),(double)addHurt);
		
		//最終傷害(四捨五入)
		int SATK = 1;
		if (divSATK) {
			SATK = ActRole.getSATK(getSkillId());
		}
		double dmg = BattleUtil.div(BattleUtil.mul(baseDmg,criRate),(double)SATK,0);
		//dmg = BattleUtil.calRoundValue(dmg, 0);
		
		int finaldmg = (int)dmg + addHurt;
		
		boolean checkret =  BattleUtil.rangeInDefind(chkHurt,finaldmg-2,finaldmg+1);
		if (!checkret) {
			Log.debugInfo("Skill BaseDmg:{},reduction:{},atk:{},elementRate:{},onlyparams:{},ActBuff:{},targetBuff:{},buffValue:{},auraValue:{},markValue:{}"
					,baseDmg,reduction,atk,elementRate,onlyparams,ActRole.getBuffList(),targetRole.getBuffList(),buffval.getBuffValue(),buffval.getAuraValue(),buffval.getMarkValue());

			Log.debugInfo("Skill finaldmg: {},addhurt: {},SATK:{},dmg:{},chkhurt: {}, criRate: {},skillId():{},ActPos:{},ActType:{},ActItemId:{},tarPos:{},tarType:{},tarItemId:{}",
					finaldmg,addHurt,SATK,dmg,chkHurt,criRate,getSkillId(),ActRole.getPos(),ActRole.getRoleType(),ActRole.getitemId(),targetRole.getPos(),targetRole.getRoleType(),targetRole.getitemId());
		}
		
		return checkret;
	}
	
//	/**
//	 * 檢查技能連擊傷害 
//	 * @return
//	 */
//	public boolean checkmultipleHurt(NewBattleRole ActRole ,NewBattleRole targetRole,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo,double onlyparams) {
//		return checkmultipleHurt(ActRole,targetRole,ActionInfo,targetRoleInfo,onlyparams,0);
//	}
//	
//	/**
//	 * 檢查技能連擊傷害(附加上額外傷害) 
//	 * @return
//	 */
//	public boolean checkmultipleHurt(NewBattleRole ActRole ,NewBattleRole targetRole,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo,double onlyparams,int addHurt) {
//		
//		int atkStatus = targetRoleInfo.getStatus();
//		int chkHurt = BattleUtil.calLogGetHurt(targetRoleInfo.getNowHp(),targetRoleInfo.getNewHp(),targetRoleInfo.getNowShield(),targetRoleInfo.getNewShield());
//		boolean isCri = (atkStatus == 2);
//		
//		//減傷
//		double reduction = BattleUtil.calReduction(ActRole,targetRole,false);
//		//攻擊力
//		double atk = ActRole.getAttck();
//		//屬性加成
//		double elementRate = BattleUtil.calElementRate(ActRole,targetRole);
//		// Buff
//		BuffValue buffval = BuffUtil.checkAllDmgBuffValue(ActRole,targetRole);
//		//基礎傷害
//		//double baseDmg = BattleUtil.mul(atk,BattleUtil.mul(BattleUtil.mul(BattleUtil.sub(1.0,reduction),elementRate),onlyparams)); //(int)(atk * (1 - reduction) * elementRate);
//		double baseDmg = BattleUtil.mulmul(atk,BattleUtil.sub(1.0,reduction),elementRate,onlyparams,buffval.getAllMul());
//		//暴傷
//		double criRate = 1.0;
//		
//		if (isCri) {
//			criRate = BattleUtil.calFinalCriDmgRate(ActRole,targetRole,false,false);
//		}
//		//最終傷害(四捨五入)
//		int SATK = ActRole.getSATK(getSkillId());
//		double dmg = BattleUtil.div(BattleUtil.mul(baseDmg,criRate),(double)SATK,0);
//		
//		int finaldmg = (int)dmg + addHurt;
//		
//		boolean checkret =  BattleUtil.rangeInDefind(chkHurt,finaldmg-2,finaldmg+1);
//		if (!checkret) {
//			
//			Log.debugInfo("dmg: {},hurt: {}, criRate: {},skillId():{},ActPos:{},ActType:{},ActItemId:{},tarPos:{},tarType:{},tarItemId:{}",
//					finaldmg,chkHurt,criRate,getSkillId(),ActRole.getPos(),ActRole.getRoleType(),ActRole.getitemId(),targetRole.getPos(),targetRole.getRoleType(),targetRole.getitemId());
//		}
//		
//		return checkret;
//	}
	
	public int getSkillId() {
		return skillId;
	}
	
	public List<Double> getParams(){
		if (SkillCfg != null) {
			return SkillCfg.getValues();
		}
		return null;
	}

}
