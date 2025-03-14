package com.guaji.game.battle;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.guaji.config.ConfigManager;
import org.guaji.log.Log;
import org.guaji.os.GuaJiRand;
import org.guaji.os.MyException;

import com.guaji.game.battle.skill.SkillType;
import com.guaji.game.config.BattleParamCfg;
import com.guaji.game.config.NewSkillCfg;
import com.guaji.game.config.SkillLevelCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.protocol.Attribute.Attr;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Player.RoleSkill;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.util.GsConst;

/**
 * 战斗常用
 */
public class BattleUtil {
	/**
	 * 計算(目標)爆擊耐性(%)
	 */
	
	/**
	 *計算流向取出傷害值
	 */
	public static int calLogGetHurt(int hp , int new_hp, int shield , int new_shield) {
		int hurt = 0;
		if (shield >= new_shield) {
			hurt = shield - new_shield;
		}
		if (hp >= new_hp) {
			hurt = hurt +(hp-new_hp);
		}
		return hurt;
	}
	
	/**
	 *計算流向取出補血值
	 */
	public static int calLogGetRecovery(int hp , int new_hp) {
		int addhp = 0;
		if (new_hp >= hp) {
			addhp = new_hp - hp;
		}
		return addhp;
	}
	/**
	 * 計算護盾回復量 
	 */
	public static int calLogGetShield(int shield,int newShield) {
		int addshield = 0;
		if (newShield >= shield) {
			addshield = newShield - shield;
		}
		return addshield;
	}
	
	/**
	 * 最終爆擊增傷
	 */
	public static double calFinalCriDmgRate(NewBattleRole ActRole ,NewBattleRole targetRole,boolean isSkipResist,boolean isNormalAtk) {
		double criResist = targetRole.getCritResist();
		double targetResist = isSkipResist ? 1.0 : sub(1.0,criResist);
		double cirDmgRate = ActRole.getCritDmg(isNormalAtk);
		double criRate = add(1.0,mul(cirDmgRate,targetResist));
		return calRoundValue(criRate,2);
	}
	
	/**
	 * 回傳攻擊職業補正的回魔
	 * @return
	 */
	public static int calClassMp(NewBattleRole ActRole ,NewBattleRole targetRole) {
	    double atkmp = (double)ActRole.getATKMP();
	    double correction =	targetRole.getClassCorrection();
	    double finalmp = mul(atkmp,correction);
		return (int)calRoundValue(finalmp, 0);
	}
	/**
	 * 判斷數字範圍
	 */
	public static boolean rangeInDefind(int current , int min, int max) {
		return Math.max(min, current) == Math.min(current, max);
	}
	/**
	 * 計算元素屬性加成
	 */		
	public static double calElementRate (NewBattleRole ActRole ,NewBattleRole targetRole) {
		List<Integer> atkList = new ArrayList<>();
		atkList.add(ActRole.getElement());
		List<Integer> atkNewElement = BuffUtil.forceChangeElement(ActRole);
		List<Integer> tarList = new ArrayList<>();
		tarList.add(targetRole.getElement());
		List<Integer> tarNewElement = BuffUtil.forceChangeElement(targetRole);
		
		if (atkNewElement.size() > 0) {
			atkList = atkNewElement;
		}
		
		if (tarNewElement.size() > 0) {
			tarList = tarNewElement;
		}

		double elementRate = 1.0 ;
		for (Integer atkElement : atkList ) {
			for (Integer tarElement : tarList ) {
				if (atkElement == GsConst.ElementType.FIRE) {
					if (tarElement ==  GsConst.ElementType.NATURE) {
						 elementRate = elementRate + 0.3;
					} else if (tarElement ==  GsConst.ElementType.WATER) {
						 elementRate = elementRate - 0.3;
					}
				} else if (atkElement == GsConst.ElementType.WATER) {
					if (tarElement ==  GsConst.ElementType.FIRE) {
						 elementRate = elementRate + 0.3;
					} else if (tarElement ==  GsConst.ElementType.NATURE) {
						 elementRate = elementRate - 0.3;
					}
				} else if (atkElement == GsConst.ElementType.NATURE) {
					if (tarElement ==  GsConst.ElementType.WATER) {
						 elementRate = elementRate + 0.3;
					} else if (tarElement ==  GsConst.ElementType.FIRE) {
						 elementRate = elementRate - 0.3;
					}
					
				} else if (atkElement == GsConst.ElementType.LIGHT) {
					if (tarElement ==  GsConst.ElementType.DARK) {
						elementRate = elementRate + 0.3;
					}
				} else if (atkElement == GsConst.ElementType.DARK) {
					if (tarElement ==  GsConst.ElementType.LIGHT) {
						elementRate = elementRate + 0.3;
					}
				}
			}
		}
		return elementRate;
	}
	/**
	 * 計算物/魔防(計算穿透力)
	 */		
	public static double calDef (NewBattleRole ActRole ,NewBattleRole targetRole,boolean isNormalAtk) {
		double baseDef  = targetRole.getBaseDef(ActRole.IsMagic());
		double penetrate = ActRole.getPenetrate(isNormalAtk);
		double def = mul(baseDef,sub(1.0,penetrate)) ; //baseDef*(1-penetrate);
		Log.debugInfo("calDef funtion baseDef:{},penetrate:{},def:{}",baseDef,penetrate,def);
		return calRoundValue(def,0); 
	}
	/**
	 * 計算物/魔防(不計算穿透力)
	 */		
	public static double calDef2 (NewBattleRole targetRole,boolean isMagic) {
		double def  = targetRole.getBaseDef(isMagic);
		Log.debugInfo("calDef2 funtion==,def:{},isMagic:{}",def,isMagic);
		return calRoundValue(def,0); 
	}
	/**
	 * 計算減傷(%)
	 */	
	public static double calReduction(NewBattleRole ActRole ,NewBattleRole targetRole,boolean isNormalAtk) {
		double def = calDef(ActRole,targetRole,isNormalAtk);
		double reduction = div(def,add(def,300.0),2);// def / (def + 300);
		Log.debugInfo(" calReduction==def:{},reduction:{},isNormalAtk:{}",def,reduction,isNormalAtk);
		return  reduction;
	}
	/**
	 * 計算減傷(%)(不計算穿透力)
	 */	
	public static double calReduction2(NewBattleRole targetRole,boolean isMagic) {
		double def = calDef2(targetRole,isMagic);
		double reduction = div(def,add(def,300.0),2);// def / (def + 300);
		Log.debugInfo(" calReduction2==def:{},reduction:{},isMagic:{}",def,reduction,isMagic);
		return  reduction;
	}
	/**
	 * 計算基礎傷害
	 */	
	public static double calBaseDamage(NewBattleRole ActRole ,NewBattleRole targetRole,boolean isNormalAtk) {		
		//減傷
		double reduction = calReduction(ActRole,targetRole,isNormalAtk);
		//攻擊力
		double atk = ActRole.getAttck();
		//屬性加成
		double elementRate = calElementRate(ActRole,targetRole);
		// Buff
		BuffValue buffval = BuffUtil.checkAllDmgBuffValue(ActRole,targetRole);
		//最終傷害
		double ddmg = mul(mul(atk,mul(sub(1.0,reduction),elementRate)),buffval.getAllMul()); //(int)(atk * (1 - reduction) * elementRate);
		ddmg = calRoundValue(ddmg,0);
		Log.debugInfo("calBaseDmg ddmg:{},reduction:{},atk:{},elementRate:{},ActBuff:{},targetBuff:{},buffValue:{},auraValue:{},markValue:{}"
				,ddmg,reduction,atk,elementRate,ActRole.getBuffList(),targetRole.getBuffList(),buffval.getBuffValue(),buffval.getAuraValue(),buffval.getMarkValue());
		return ddmg;
	}
	/**
	 * 檢查普功最終一次傷害
	 */
	public static boolean checkDamage(NewBattleRole ActRole ,NewBattleRole targetRole,NewBattleRoleInfo targetRoleInfo,int hurt,boolean isCri,int markTime) {
		
		boolean checkret = true;
		
		if (targetRole.Istrigger(GsConst.TriggerType.BUFF,GsConst.Buff.UNDEAD)) {
			if (!targetRole.checkIsUndeadHP(targetRoleInfo,false)) {
				Log.debugInfo("ret:7 checkIsUndeadHP");
				return false;
			}
		} else { 
			double baseDmg = calBaseDamage(ActRole,targetRole,true);
			baseDmg = div(baseDmg,(double)ActRole.getNATK(),0); //baseDmg / ActRole.getNATK();
			
	        //爆傷
	        double criRate = 1.0 ;
	        
	        if (isCri) {
	        	criRate = calFinalCriDmgRate(ActRole,targetRole,false,true); //1 + ActRole.getCritDmg(); //* (1 - 爆傷抵免)
	        }
	        //最終傷害(四捨五入)
	        int dmg = (int)calRoundValue(mul(baseDmg,criRate),0); //(int)calRoundValue(baseDmg * criRate, 0);
			checkret =  rangeInDefind(hurt,dmg-2,dmg+1);
			if (!checkret) {
				
				Log.debugInfo("NormalAtkcheck dmg: {},hurt: {}, criRate: {},ActNATK():{},ActPos:{},ActType:{},ActItemId:{},tarPos:{},tarType:{},tarItemId:{}",
							dmg,hurt,criRate,ActRole.getNATK(),ActRole.getPos(),ActRole.getRoleType(),ActRole.getitemId(),targetRole.getPos(),targetRole.getRoleType(),targetRole.getitemId());
			}
		}
		
		if (ActRole.Istrigger(GsConst.TriggerType.SKILL,SkillType.Passive_ID_10005)) {
			int count = ActRole.getATKFoucus(targetRole.getPos());
			double p1 = SkillUtil.getParams(SkillType.Passive_ID_10005).get(0);
			if (count == p1) {
				double p2 = SkillUtil.getParams(SkillType.Passive_ID_10005).get(1);
				double p3 = SkillUtil.getParams(SkillType.Passive_ID_10005).get(2);
				targetRole.addBuff((int)p2,markTime,(int)p3*1000);
				ActRole.clearATKFoucus();
			}
		}
		
		if (ActRole.Istrigger(GsConst.TriggerType.SKILL,SkillType.Passive_ID_10010)) {
			int count = ActRole.getATKFoucus(targetRole.getPos());
			double p1 = SkillUtil.getParams(SkillType.Passive_ID_10010).get(0);
			if (count == p1) {
				double p2 = SkillUtil.getParams(SkillType.Passive_ID_10010).get(1);
				double p3 = SkillUtil.getParams(SkillType.Passive_ID_10010).get(2);
				targetRole.addBuff((int)p2,markTime,(int)p3*1000);
				ActRole.clearATKFoucus();
			}
		}
		
		return checkret;
	}
	/**
	 * 檢查Dot傷害
	 */
	public static boolean checkDotDamage(NewBattleRole ActRole ,NewBattleRole targetRole,int buffId,int hurt) {
		NewBuff buff = targetRole.getBuff(buffId);
		if (buff == null) {
			Log.debugInfo("ret:9 checkDotDamage: buff == null");
			return false;
		}
		double value = 0;
		double rvalue = 0;
		double ddmg = 0;
		int dmg = 0;
		boolean check = false;
		if (buffId == GsConst.Buff.EROSION) {
			value = buff.getArgs().get(1);
			rvalue = calReduction2(targetRole,true);
			if (targetRole.getRoleType() == Const.roleType.WORLDBOSS_VALUE) {
				ddmg = mulmul((double)targetRole.getAttck(),value,sub(1.0,rvalue));
			} else {
				ddmg = mulmul((double)targetRole.getMaxhp(),value,sub(1.0,rvalue));
			}
			dmg = (int)calRoundValue(ddmg, 0);
			check = (hurt == dmg);
			if (!check) {
				Log.debugInfo("ret:9 checkDotDamage:buffId:{},cHurt:{},value:{},rvalue:{},ddmg:{},dmg:{}",buffId,hurt,rvalue,ddmg,dmg);
			}
			return check;
		} else if (buffId == GsConst.Buff.BURN) {
			value = buff.getArgs().get(1);
			rvalue = calReduction2(targetRole,false);
			if (targetRole.getRoleType() == Const.roleType.WORLDBOSS_VALUE) {
				ddmg = mulmul((double)targetRole.getAttck(),value,sub(1.0,rvalue));
			} else {
				ddmg = mulmul((double)targetRole.getMaxhp(),value,sub(1.0,rvalue));
			}
			dmg = (int)calRoundValue(ddmg, 0);
			check = (hurt == dmg);
			if (!check) {
				Log.debugInfo("ret:9 checkDotDamage:buffId:{},cHurt:{},value:{},rvalue:{},ddmg:{},dmg:{}",buffId,hurt,rvalue,ddmg,dmg);
			}
			return check;
		} else {
			Log.debugInfo("ret:9 checkDotDamage:  buff not find cal");
			return false;
		}
	 }
	
	/**
	 * 檢查Dot治療
	 */
	public static boolean checkDotHealth(NewBattleRole ActRole ,NewBattleRole targetRole,int buffId,int addhp) {
		NewBuff buff = targetRole.getBuff(buffId);
		if (buff == null) {
			Log.debugInfo("ret:9 checkDotHealth: buff == null");
			return false;
		}
		double value = 0;
		double ddmg = 0;
		int dmg = 0;
		int maxadd = targetRole.getMaxhp() - targetRole.getHp();
		boolean check = false;
		if (buffId == GsConst.Buff.RECOVERY) {
			value = buff.getArgs().get(1);
			//rvalue = calReduction2(targetRole,true);
			ddmg = mul((double)targetRole.getMaxhp(),value);
			dmg = (int)calRoundValue(ddmg, 0);
			if (dmg >= addhp) {
				check = (addhp == dmg);
			} else {
				check = (addhp == maxadd);
			}
			if (!check) {
				Log.debugInfo("ret:9 checkDotHealth:buffId:{},c_addhp:{},value:{},ddmg:{},dmg:{}",buffId,addhp,ddmg,dmg);
			}
			return check;
		} else {
			Log.debugInfo("ret:9 checkDotHealth: buff not find cal");
			return false;
		}
	 }	
	/**
	 * 計算四捨五入數值
	 * 
	 * @return
	 */
	public static double calRoundValue(double value,int digit) {
			return round(value,digit);
	}
	/**
	 * 计算公会boss伤害
	 * 
	 * @return
	 */
	public static int calcAllianceBossDamage(int playerId) {
		int minDmg = 0;
		int maxDmg = 0;
		PlayerSnapshotInfo.Builder snapShotBuilder = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
		for (Attr attr : snapShotBuilder.getMainRoleInfo().getAttribute().getAttributeList()) {
			if (attr.getAttrId() == Const.attr.MINDMG_VALUE) {
				minDmg = attr.getAttrValue();
			} else if (attr.getAttrId() == Const.attr.MAXDMG_VALUE) {
				maxDmg = attr.getAttrValue();
			}
		}
		try {
			return GuaJiRand.randInt(minDmg, maxDmg);
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return (minDmg + maxDmg) / 2;
	}

	/**
	 * 添加行为时间
	 * 
	 * @param actionType
	 * @return
	 */
	public static int getActionTime(int actionType) {
		int actionTime = 0;
		switch (actionType) {
//		case Battle.actionType.ACTION_RECOVERMP_VALUE:
//			actionTime = 1000;
//			break;
//
//		case Battle.actionType.ACTION_RECOVERHP_VALUE:
//			actionTime = 0;
//			break;
//
//		case Battle.actionType.ACTION_SKILL_VALUE:
//			actionTime = 1000;
//			break;
//
//		case Battle.actionType.ACTION_BUFF_REDUCE_HP_VALUE:
//			actionTime = 1000;
//			break;
//
//		case Battle.actionType.ACTION_REVERSE_DAMAGE_VALUE:
//			actionTime = 0;
//			break;
//
//		case Battle.actionType.ACTION_RAGE_DAMAGE_VALUE:
//			actionTime = 1000;
//			break;
//
//		case Battle.actionType.ACTION_DISPEL_VALUE:
//			actionTime = 1000;
//			break;

		default:
			break;
		}

		if (GsConst.DEBUG_FAST_BATTLE) {
			actionTime /= 10;
		}
		return (int) (actionTime * SysBasicCfg.getInstance().getFightActionTimeRatio());
	}

	/**
	 * 计算属性参数修正比值
	 * 
	 * @param battleRole
	 * @param attrType
	 * @return
	 */
	public static float getAttrRate(BattleRole battleRole, BattleRole opponent, Const.attr attrType) {
		BattleParamCfg battleParamCfg = ConfigManager.getInstance().getConfigByKey(BattleParamCfg.class,
				battleRole.getLevel());
		if (battleParamCfg != null) {
			switch (attrType.getNumber()) {
			case Const.attr.ARMOR_VALUE:
				float armorValue = 1.0f * battleRole.getAttrValue(attrType);
				armorValue = armorValue * (1.0f - getAttrRate(opponent, battleRole, Const.attr.BUFF_AVOID_ARMOR));
				return armorValue / (armorValue + battleParamCfg.getArmor());

			case Const.attr.PHYDEF_VALUE:
				float phyDefValue = 1.0f * battleRole.getAttrValue(attrType);
				phyDefValue = phyDefValue
						* (1.0f - getAttrRate(opponent, battleRole, Const.attr.BUFF_PHYDEF_PENETRATE));
				return 1.0f * phyDefValue / (phyDefValue + battleParamCfg.getPhyDef());

			case Const.attr.MAGDEF_VALUE:
				float magDefValue = 1.0f * battleRole.getAttrValue(attrType);
				magDefValue = magDefValue
						* (1.0f - getAttrRate(opponent, battleRole, Const.attr.BUFF_MAGDEF_PENETRATE));
				return 1.0f * magDefValue / (magDefValue + battleParamCfg.getMagDef());

			case Const.attr.HIT_VALUE:
				return 1.0f * battleRole.getAttrValue(attrType)
						/ (battleRole.getAttrValue(attrType) + battleParamCfg.getHit());

			case Const.attr.DODGE_VALUE:
				return 1.0f * battleRole.getAttrValue(attrType)
						/ (battleRole.getAttrValue(attrType) + battleParamCfg.getDodge());

			case Const.attr.CRITICAL_VALUE:
				return 1.0f * battleRole.getAttrValue(attrType)
						/ (battleRole.getAttrValue(attrType) + battleParamCfg.getCritical());

			case Const.attr.RESILIENCE_VALUE:
				return 1.0f * battleRole.getAttrValue(attrType)
						/ (battleRole.getAttrValue(attrType) + battleParamCfg.getResilience());

			case Const.attr.BUFF_AVOID_ARMOR_VALUE:
				return 1.0f * battleRole.getAttrValue(attrType)
						/ (battleRole.getAttrValue(attrType) + battleParamCfg.getAvoidArmor());

			case Const.attr.BUFF_PHYDEF_PENETRATE_VALUE:
				return 1.0f * battleRole.getAttrValue(attrType)
						/ (battleRole.getAttrValue(attrType) + battleParamCfg.getPhyDefPenetrate());

			case Const.attr.BUFF_MAGDEF_PENETRATE_VALUE:
				return 1.0f * battleRole.getAttrValue(attrType)
						/ (battleRole.getAttrValue(attrType) + battleParamCfg.getMagDefPenetrate());

			default:
				break;
			}
		}
		return 1.0f;
	}

	/**
	 * 计算攻击结果(命中, 暴击, 闪避: Battle.attackResult)
	 * 
	 * @return
	 */
	public static int calcAttackResult(BattleRole attacker, BattleRole defender, int skillId, int attackType,
			SkillLevelCfg... cfgs) {
		// 命中与暴击标记
//		boolean isHit = false;
//		boolean isCrit = false;
//
//		// 狂暴buff必然暴击
//		if (attacker.checkBuffValid(Const.Buff.RAGE_VALUE)
//				&& !SysBasicCfg.getInstance().getWarriorNotCritSkillIdList().contains(skillId)) {
//			return Battle.attackResult.RESULT_CRIT_VALUE;
//		}
//		if (attacker.checkBuffValid(Const.Buff.FRENZY_VALUE)
//				&& !SysBasicCfg.getInstance().getWarriorNotCritSkillIdList().contains(skillId)) {
//			return Battle.attackResult.RESULT_CRIT_VALUE;
//		}
//		// 勇猛
//		if (attacker.checkBuffValid(Const.Buff.POWERFUL_VALUE)
//				&& !SysBasicCfg.getInstance().getWarriorNotCritSkillIdList().contains(skillId)) {
//			return Battle.attackResult.RESULT_CRIT_VALUE;
//		}
//
//		if (attackType == Battle.attackType.ATTACK_MAG_VALUE) {
//			isHit = true;
//		} else {
//			BattleBuff battleBuff = attacker.getBuff(Const.Buff.RAGE_VALUE);
//			int buffHitAdd = 0;
//			if (battleBuff != null && battleBuff.isValid()) {
//				buffHitAdd = battleBuff.getBuffArgs(0);
//			}
//			BattleBuff missBuff = defender.getBuff(Const.Buff.MISS_VALUE);
//			int missAdd = 0;
//			if (missBuff != null && missBuff.isValid()) {
//				missAdd = missBuff.getBuffArgs(0);
//			}
//			float hitRate = 1.0f + getAttrRate(attacker, defender, Const.attr.HIT)
//					- getAttrRate(defender, attacker, Const.attr.DODGE)
//					+ (attacker.getLevel() - defender.getLevel()) * 0.01f * SysBasicCfg.getInstance().getHitLevelRate()
//					+ (buffHitAdd * 0.0001f) - (missAdd * 0.0001f);
//
//			// 修正命中
//			hitRate = Math.max(0.1f, hitRate);
//			if (0.01f * GuaJiRand.randInt(100) <= hitRate) {
//				isHit = true;
//			}
//		}
//
//		// 技能增加暴击率
//		float critical = getAttrRate(attacker, defender, Const.attr.CRITICAL);
//		if (attackType != Battle.attackType.ATTACK_MAG_VALUE) {
////			if (skillId == SkillType.SKILL_ID_110 || skillId == SkillType.SKILL_ID_2042
////					|| skillId == SkillType.SKILL_ID_2402 || skillId == SkillType.SKILL_ID_2452) {
////				NewSkillCfg cfg = ConfigManager.getInstance().getConfigByKey(NewSkillCfg.class, skillId);
////				if (cfg != null) {
////					critical += cfgs[0].getArg2() * 0.0001f;
////				}
////			}
//
//			float critRate = critical - getAttrRate(defender, attacker, Const.attr.RESILIENCE)
//					+ (attacker.getLevel() - defender.getLevel()) * 0.01f
//							* SysBasicCfg.getInstance().getCritLevelRate();
//
//			if (attacker.getLevel() - defender.getLevel() > 10) {
//				critRate += 0.2f;
//			}
//
//			if (0.01f * GuaJiRand.randInt(100) <= critRate) {
//				isCrit = true;
//			}
//		}
//
//		if (!isHit) {
//			return Battle.attackResult.RESULT_MISS_VALUE;
//		} else if (isCrit) {
//			return Battle.attackResult.RESULT_CRIT_VALUE;
//		}
		return 0;//Battle.attackResult.RESULT_HIT_VALUE;
	}

	/**
	 * 计算攻击结果(命中, 暴击, 闪避: Battle.attackResult)
	 * 
	 * @return
	 */
	public static int calcAttackResult(BattleRole attacker, BattleRole defender, int skillId, int attackType,
			int paramData) {
		// 命中与暴击标记
//		boolean isHit = false;
//		boolean isCrit = false;
//
//		// 狂暴buff必然暴击
//		if (attacker.checkBuffValid(Const.Buff.RAGE_VALUE)
//				&& !SysBasicCfg.getInstance().getWarriorNotCritSkillIdList().contains(skillId)) {
//			return Battle.attackResult.RESULT_CRIT_VALUE;
//		}
//		if (attacker.checkBuffValid(Const.Buff.FRENZY_VALUE)
//				&& !SysBasicCfg.getInstance().getWarriorNotCritSkillIdList().contains(skillId)) {
//			return Battle.attackResult.RESULT_CRIT_VALUE;
//		}
//		// 勇猛
//		if (attacker.checkBuffValid(Const.Buff.POWERFUL_VALUE)
//				&& !SysBasicCfg.getInstance().getWarriorNotCritSkillIdList().contains(skillId)) {
//			return Battle.attackResult.RESULT_CRIT_VALUE;
//		}
//
//		if (attackType == Battle.attackType.ATTACK_MAG_VALUE) {
//			isHit = true;
//		} else {
//			BattleBuff battleBuff = attacker.getBuff(Const.Buff.RAGE_VALUE);
//			int buffHitAdd = 0;
//			if (battleBuff != null && battleBuff.isValid()) {
//				buffHitAdd = battleBuff.getBuffArgs(0);
//			}
//			BattleBuff missBuff = defender.getBuff(Const.Buff.MISS_VALUE);
//			int missAdd = 0;
//			if (missBuff != null && missBuff.isValid()) {
//				missAdd = missBuff.getBuffArgs(0);
//			}
//			float hitRate = 1.0f + getAttrRate(attacker, defender, Const.attr.HIT)
//					- getAttrRate(defender, attacker, Const.attr.DODGE)
//					+ (attacker.getLevel() - defender.getLevel()) * 0.01f * SysBasicCfg.getInstance().getHitLevelRate()
//					+ (buffHitAdd * 0.0001f) - (missAdd * 0.0001f);
//
//			// 修正命中
//			hitRate = Math.max(0.1f, hitRate);
//			if (0.01f * GuaJiRand.randInt(100) <= hitRate) {
//				isHit = true;
//			}
//		}
//
//		// 技能增加暴击率
//		float critical = getAttrRate(attacker, defender, Const.attr.CRITICAL);
//		if (attackType != Battle.attackType.ATTACK_MAG_VALUE) {
//			// 根据攻击次数增加暴击概率
////			if (skillId == SkillType.SKILL_ID_2798) {
////				//
////				critical += paramData * 0.0001f;
////			}
//			float critRate = critical - getAttrRate(defender, attacker, Const.attr.RESILIENCE)
//					+ (attacker.getLevel() - defender.getLevel()) * 0.01f
//							* SysBasicCfg.getInstance().getCritLevelRate();
//
//			if (attacker.getLevel() - defender.getLevel() > 10) {
//				critRate += 0.2f;
//			}
//
//			if (0.01f * GuaJiRand.randInt(100) <= critRate) {
//				isCrit = true;
//			}
//		}
//
//		if (!isHit) {
//			return Battle.attackResult.RESULT_MISS_VALUE;
//		} else if (isCrit) {
//			return Battle.attackResult.RESULT_CRIT_VALUE;
//		}
		return 0;//Battle.attackResult.RESULT_HIT_VALUE;
	}

	/**
	 * 计算法师攻击结果(暴击: Battle.attackResult)
	 * 
	 * @return
	 */
	public static int calcMagicAttackResult(BattleRole attacker, BattleRole defender, int skillId, int attackType) {
//		if (attacker.checkBuffValid(Const.Buff.MAGIC_BUFF_VALUE)) {
//			float critRate = getAttrRate(attacker, defender, Const.attr.CRITICAL)
//					- getAttrRate(defender, attacker, Const.attr.RESILIENCE)
//					+ (attacker.getLevel() - defender.getLevel()) * 0.01f
//							* SysBasicCfg.getInstance().getCritLevelRate();
//
//			if (attacker.getLevel() - defender.getLevel() > 10) {
//				critRate += 0.2f;
//			}
//
//			if (0.01f * GuaJiRand.randInt(100) <= critRate) {
//				return Battle.attackResult.RESULT_CRIT_VALUE;
//			}
//		}

		return 0 ;//Battle.attackResult.RESULT_HIT_VALUE;
	}

	/**
	 * 计算非法师职业暴击伤害
	 * 
	 * @param battleRole
	 * @param damage
	 * @return
	 */
	public static int calcAllCriticalDamage(BattleRole battleRole, int damage) {
		int dmg = (int) (damage
				* (1.5f + battleRole.getAttrValue(Const.attr.BUFF_CRITICAL_DAMAGE) * GsConst.CRITICAL_DAMAGE_VALUE));
		return dmg;
	}

	/**
	 * 选择可使用技能id
	 * 
	 * @param battleRole
	 * @return
	 */
	public static SkillLevelCfg selectUseSkill(Battlefield battlefield, BattleRole battleRole, int battleType) {

		List<RoleSkill> roleSkills = battleRole.getBattleSkills(battleType);
		for (RoleSkill roleSkill : roleSkills) {
			if (battleRole.canUseSkill(roleSkill.getItemId(), battlefield.getBattleRound())) {
				NewSkillCfg skillCfg = ConfigManager.getInstance().getConfigByKey(NewSkillCfg.class, roleSkill.getItemId());
				int level = roleSkill.getLevel() > 0 ? roleSkill.getLevel() : 1;
				SkillLevelCfg skillLevelCfg = SkillLevelCfg.getSkillLevelCfg(roleSkill.getItemId(), level, battleType);
				if (skillCfg != null && skillLevelCfg != null) {
//					if (skillCfg.getId() != SkillType.SKILL_ID_2805 && skillCfg.getId() != SkillType.SKILL_ID_2833) {
//						// 沉默buff
//						if (battleRole.checkBuffValid(Const.Buff.SILENCE_VALUE)) {
//							return null;
//						}
//					}

					// 充能buff对 奥术冲击 技能的蓝消耗判断
					BattleBuff buff = battleRole.getBuff(Const.Buff.SUPPLY_ENERGY_VALUE);
					if (buff != null && skillCfg.getId() == 27) {
						if (battleRole.getMp() < skillLevelCfg.getCostMP() + 500 * buff.getOverlap()) {
							battleRole.removeBuff(Const.Buff.SUPPLY_ENERGY_VALUE);
						}
					}

					if (battleRole.getMp() >= skillLevelCfg.getCostMP()) {
						return skillLevelCfg;
					}
				}
			}
		}
		return null;
	}

  /**
   * 提供精確的加法運算。
  *
  * @param value1 被加數
  * @param value2 加數
  * @return 兩個引數的和
  */
	public static double add(Double value1,Double value2) {
		BigDecimal b1 = new BigDecimal(Double.toString(value1));
		BigDecimal b2 = new BigDecimal(Double.toString(value2));
		
		return b1.add(b2).doubleValue();
	}
	
	public static double addadd(Double ...values) {
		BigDecimal retb = new BigDecimal(Double.toString(0.0));
		for (Double aVal : values) {
			BigDecimal b = new BigDecimal(Double.toString(aVal));
			retb = retb.add(b);
		}
		
		return retb.doubleValue();
	}

	/**
	* 提供精確的減法運算。
	*
	* @param value1 被減數
	* @param value2 減數
	* @return 兩個引數的差
	*/

	 public static double sub(Double value1,Double value2) {

	   BigDecimal b1 = new BigDecimal(Double.toString(value1));

	  BigDecimal b2 = new BigDecimal(Double.toString(value2));

	  return b1.subtract(b2).doubleValue();

	}

	/**
	* 提供精確的乘法運算。
	*
	* @param value1 被乘數
	* @param value2 乘數
	* @return 兩個引數的積
	*/

	public static Double mul(Double value1,Double value2) {

	 BigDecimal b1 = new BigDecimal(Double.toString(value1));

	  BigDecimal b2 = new BigDecimal(Double.toString(value2));

	 return b1.multiply(b2).doubleValue();

	}
	
	public static Double mulmul(Double ...values) {
		
		BigDecimal retb = new BigDecimal(Double.toString(1.0));
		for (Double aval : values) {
			BigDecimal b = new BigDecimal(Double.toString(aval));
			retb = retb.multiply(b);
		}
		
		return  retb.doubleValue();
	}

	/**
	 * 提供（相對）精確的除法運算，當發生除不盡的情況時， 精確到小數點以後10位，以後的數字四捨五入。
	 *
	 * @param dividend 被除數
	 * @param divisor 除數
	 * @return 兩個引數的商
	 */

	public static Double divide(Double dividend,Double divisor) {

	  return div(dividend,divisor,2);

	}

	/**
	* 提供（相對）精確的除法運算。 當發生除不盡的情況時，由scale引數指定精度，以後的數字四捨五入。
	*
	* @param dividend 被除數
	* @param divisor 除數
	* @param scale  表示表示需要精確到小數點以後幾位。
	* @return 兩個引數的商
	*/

	public static Double div(Double dividend,Double divisor,Integer scale) {

	  if (scale < 0) {

	    throw new IllegalArgumentException("The scale must be a positive integer or zero");

	  }

	  BigDecimal b1 = new BigDecimal(Double.toString(dividend));

	  BigDecimal b2 = new BigDecimal(Double.toString(divisor));

	   return b1.divide(b2,scale,RoundingMode.HALF_UP).doubleValue();

	}

	 /**
	 * 提供指定數值的（精確）小數位四捨五入處理。
	 *
	 * @param value 需要四捨五入的數字
	 * @param scale 小數點後保留幾位
	 * @return 四捨五入後的結果
	 */

	public static double round(double value,int scale){

	   if(scale<0){

	    throw new IllegalArgumentException("The scale must be a positive integer or zero");

	   }
	   
	  BigDecimal b = new BigDecimal(Double.toString(value));

	  BigDecimal one = BigDecimal.ONE;

	  return b.divide(one,scale,RoundingMode.HALF_UP).doubleValue();

	}
}
