package com.guaji.game.battle;

import java.util.ArrayList;
import java.util.List;

import com.guaji.game.util.GsConst;

public class BuffUtil {

	//----------------------NewBattle--------------------------------------------------------------
	/**
	 * 攻擊力加成
	 */
	public static BuffValue checkAtkBuffValue(NewBattleRole ActRole,boolean IsPhy) {
		BuffValue bufval = new BuffValue(true);
		NewBuff buff = null;
		buff = ActRole.getBuff(GsConst.Buff.PETAL);	// 開花
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),BattleUtil.mul(buff.getArgs().get(2),(double)buff.getOverlap()));
		}
		
		buff = ActRole.getBuff(GsConst.Buff.FORCE);	// 強攻
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		buff = ActRole.getBuff(GsConst.Buff.PRECISION_II); // 精確II
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(1));
		}
		
		buff = ActRole.getBuff(GsConst.Buff.BOOST_I_B); // 鼓舞I
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		buff = ActRole.getBuff(GsConst.Buff.BOOST_II_B); // 鼓舞II
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		buff = ActRole.getBuff(GsConst.Buff.DEPENDENTS); // 眷屬
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		buff = ActRole.getBuff(GsConst.Buff.WEAK_I); // 衰弱I
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		buff = ActRole.getBuff(GsConst.Buff.WEAK_II); // 衰弱II
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		buff = ActRole.getBuff(GsConst.Buff.WEAK_III); // 衰弱III
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		if (IsPhy) {
			buff = ActRole.getBuff(GsConst.Buff.OUROBOROS); // 銜尾蛇
			if (buff != null) {
				bufval.addValue(buff.getBuffType(),BattleUtil.mul(buff.getArgs().get(1),(double)buff.getOverlap()));
			}
			
			buff = ActRole.getBuff(GsConst.Buff.PHYSICAL_SPECIALIZATION); // 物理特化
			if (buff != null) {
				bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
			}
			
			buff = ActRole.getBuff(GsConst.Buff.ASSAULT_B); // 突擊
			if (buff != null) {
				bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
			}
		}else {
			buff = ActRole.getBuff(GsConst.Buff.ENLIGHTENMENT); // 啟蒙
			if (buff != null) {
				bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
			}
			
			buff = ActRole.getBuff(GsConst.Buff.MANA_OVERFLOW); // 魔力溢出
			if (buff != null) {
				bufval.addValue(buff.getBuffType(),BattleUtil.mul(buff.getArgs().get(0),(double)buff.getOverlap()));
			}
			
			buff = ActRole.getBuff(GsConst.Buff.ARCANE_B); // 奧術
			if (buff != null) {
				bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
			}
			
			buff = ActRole.getBuff(GsConst.Buff.MAGIC_LOCK); // 魔力鎖鏈
			if (buff != null) {
				bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
			}
		}
		bufval.MaxZero();
		return bufval;
	}
	/**
	 * 防禦力加成
	 */
	public static BuffValue checkDefBuffValue(NewBattleRole ActRole,boolean IsPhy) {
		BuffValue bufval = new BuffValue(true);
		NewBuff buff = null;
		
		buff = ActRole.getBuff(GsConst.Buff.STABLE_I); // 堅守I
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		buff = ActRole.getBuff(GsConst.Buff.STABLE_II); // 堅守II
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		buff = ActRole.getBuff(GsConst.Buff.DEFENSE_CHAIN_B); // 防禦鎖鏈
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		buff = ActRole.getBuff(GsConst.Buff.DESTROY_I); // 防禦破壞I
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		buff = ActRole.getBuff(GsConst.Buff.DESTROY_II); // 防禦破壞II
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		buff = ActRole.getBuff(GsConst.Buff.DESTROY_III); // 防禦破壞III
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		buff = ActRole.getBuff(GsConst.Buff.COLLAPSE); // 崩壞
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		buff = ActRole.getBuff(GsConst.Buff.FROSTBITE_I); // 凍傷I
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}

		buff = ActRole.getBuff(GsConst.Buff.FROSTBITE_II); // 凍傷II
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		if (IsPhy) {
			
			
		} else {
			buff = ActRole.getBuff(GsConst.Buff.MANA_OVERFLOW); // 魔力溢出
			if (buff != null) {
				bufval.addValue(buff.getBuffType(),BattleUtil.mul(buff.getArgs().get(1),(double)buff.getOverlap()));
			}
			
			buff = ActRole.getBuff(GsConst.Buff.ARCANE_B); // 奧術
			if (buff != null) {
				bufval.addValue(buff.getBuffType(),buff.getArgs().get(1));
			}
		}
		
		buff = ActRole.getBuff(GsConst.Buff.FREEZE); // 凍結
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		bufval.MaxZero();
		return bufval;
	}
	/**
	 * 防禦力穿透加成
	 */
	
	public static BuffValue checkDefPenetrateBuffValue(NewBattleRole ActRole,boolean isNomalAtk) {
		BuffValue bufval = new BuffValue(false);
		NewBuff buff = null;
		
		buff = ActRole.getBuff(GsConst.Buff.CONCENTRATION); // 專注
		if ((buff != null)&&(isNomalAtk)) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(1));
		}
		
		return bufval;
	}
	
	/**
	 * 傷害加成(自己增傷+敵方受傷加成)
	 */
	public static BuffValue checkAllDmgBuffValue(NewBattleRole ActRole,NewBattleRole targetRole) {
		BuffValue bufval1 = checkDmgBuffValue(ActRole,targetRole);
		BuffValue bufval2 = checkBeDmgBuffValue(ActRole,targetRole);
		bufval1.setAddObj(bufval2);
		bufval1.subBuffValue(1.0);
		bufval1.subAuraValue(1.0);
		bufval1.subMarkValue(1.0);
		return bufval1;
	}
	/**
	 * 造成傷害加成
	 * @param ActRole
	 * @param targetRole
	 * @return
	 */
	public static BuffValue checkDmgBuffValue(NewBattleRole ActRole,NewBattleRole targetRole) {
		BuffValue bufval = new BuffValue(true);
		NewBuff buff = null;
		
		buff = ActRole.getBuff(GsConst.Buff.CHASE); // 追擊
		if (buff != null) {
			double hpPer = (double)targetRole.getHp()/(double)targetRole.getMaxhp();
			if (hpPer < buff.getArgs().get(0)) {
				bufval.addValue(buff.getBuffType(),buff.getArgs().get(1));
			}
		}
		bufval.MaxZero();
		return bufval;
	}
	/**
	 * 受到傷害加成或減傷
	 * @param targetRole
	 * @return
	 */
	public static BuffValue checkBeDmgBuffValue(NewBattleRole ActRole,NewBattleRole targetRole) {
		BuffValue bufval = new BuffValue(true);
		NewBuff buff = null;
		
		buff = targetRole.getBuff(GsConst.Buff.PIOUS); // 虔誠
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		buff = targetRole.getBuff(GsConst.Buff.PETAL); // 開花
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),BattleUtil.mul(buff.getArgs().get(1),(double)buff.getOverlap()));
		}
		
		buff = targetRole.getBuff(GsConst.Buff.GUARD); // 守護
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		buff = targetRole.getBuff(GsConst.Buff.ASSAULT_B); // 突擊
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(1));
		}
// --------------------------------------------------------------------------------
		buff = targetRole.getBuff(GsConst.Buff.NATURE_I); // 自然印記I
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		buff = targetRole.getBuff(GsConst.Buff.NATURE_II); // 自然印記II
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		buff = targetRole.getBuff(GsConst.Buff.STONE); // 石化
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		buff = targetRole.getBuff(GsConst.Buff.TACTICAL_VISOR); // 戰術鎖定
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		buff = targetRole.getBuff(GsConst.Buff.DEPENDENTS); // 眷屬
		if (buff != null) {
			//檢查攻擊者眼睛
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		buff = targetRole.getBuff(GsConst.Buff.FREEZE); // 凍結
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(1));
		}
				
		bufval.MaxZero();
		return bufval;
	}
	/**
	 * 爆擊率加成
	 */
	public static BuffValue checkCriBuffValue(NewBattleRole ActRole,boolean isNomalAtk) {
		BuffValue bufval = new BuffValue(false);
		NewBuff buff = null;
		
		buff = ActRole.getBuff(GsConst.Buff.RAGE); //憤怒
		if ((buff != null)&&(isNomalAtk)) {
			double value = (buff.canStack())? 0 : 1 ;
			bufval.addValue(buff.getBuffType(),value);
		}
		
		buff = ActRole.getBuff(GsConst.Buff.PRECISION_I); //精確I
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		buff = ActRole.getBuff(GsConst.Buff.PRECISION_I); //精確II
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		return bufval;
	}
	/**
	 * 爆傷加成
	 */
	public static BuffValue checkCriDmgBuffValue(NewBattleRole ActRole,boolean isNomalAtk) {
		BuffValue bufval = new BuffValue(false);
		NewBuff buff = null;
		
		buff = ActRole.getBuff(GsConst.Buff.RAGE); //憤怒
		if ((buff != null)&&(isNomalAtk)) {
			double value = (buff.canStack())? 0 : 0.5 ;
			bufval.addValue(buff.getBuffType(),value);
		}
		
		return bufval;
	}
	/**
	 * 攻速加成
	 */
	public static BuffValue checkAtkSpeedBuffValue(NewBattleRole ActRole) {
		BuffValue bufval = new BuffValue(false);
		
		NewBuff buff = null;
		
		buff = ActRole.getBuff(GsConst.Buff.CONCENTRATION); //專注
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		buff = ActRole.getBuff(GsConst.Buff.RAPID_B); //急速
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		return bufval;
	}
	
	/**
	 * 減速
	 */
	public static BuffValue checkAtkSpeedDeBuffValue(NewBattleRole ActRole) {
		BuffValue bufval = new BuffValue(false);
		
		NewBuff buff = null;
		
		buff = ActRole.getBuff(GsConst.Buff.FROSTBITE_I); //凍傷I
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(1));
		}
		
		buff = ActRole.getBuff(GsConst.Buff.FROSTBITE_II); //凍傷II
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(1));
		}
		
		return bufval;
	}
	
	/**
	 * 命中加成
	 */
	
	public static BuffValue checkHitBuffValue(NewBattleRole ActRole) {
		BuffValue bufval = new BuffValue(false);
		
		NewBuff buff = null;
		
		buff = ActRole.getBuff(GsConst.Buff.BLIND); //致盲
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
				
		return bufval;
	}
	/**
	 * 閃避加成
	 */
	public static BuffValue checkDodgeBuffValue(NewBattleRole ActRole) {
		BuffValue bufval = new BuffValue(false);
		
		NewBuff buff = null;
		
		buff = ActRole.getBuff(GsConst.Buff.SHADOW_B); //暗影
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(0));
		}
		
		buff = ActRole.getBuff(GsConst.Buff.DIZZY); //暈眩
		if (buff != null) {
			bufval.setAllValue(-100,-100,-100);
		}
				
		return bufval;
	}
	
	/**
	 * 吸血加成(普攻)
	 */
	
	public static BuffValue checkRecoverHpBuffValue(NewBattleRole ActRole) {
		BuffValue bufval = new BuffValue(false);
		
		NewBuff buff = null;
		
		buff = ActRole.getBuff(GsConst.Buff.OUROBOROS); //銜尾蛇
		if (buff != null) {
			double value = (buff.canStack())? 0 : buff.getArgs().get(2) ;
			bufval.addValue(buff.getBuffType(),value);
		}
		
		return bufval;
	}
	
	/** 
	 * 造成治療加成
	 */
	public static double checkHealBuffValue(NewBattleRole ActRole) {
		
		BuffValue bufval = new BuffValue(true);
		
		NewBuff buff = null;
		
		buff = ActRole.getBuff(GsConst.Buff.PIOUS);
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(1));
		}
		
		buff = ActRole.getBuff(GsConst.Buff.APOLLO);
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(1));
		}
		bufval.MaxZero();
		return bufval.getAllMul();
	}

	/**
	 * 受到治療加成
	 */
	public static double checkBeHealBuffValue(NewBattleRole ActRole) {
		
		BuffValue bufval = new BuffValue(true);
		
		NewBuff buff = null;
		
		buff = ActRole.getBuff(GsConst.Buff.PIOUS);
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(1));
		}
		
		buff = ActRole.getBuff(GsConst.Buff.APOLLO);
		if (buff != null) {
			bufval.addValue(buff.getBuffType(),buff.getArgs().get(1));
		}
		bufval.MaxZero();
		return bufval.getAllMul();
	}
	
	/**
	 * 額外MP獲得
	 */
	public static double checkMpGainValue(NewBattleRole ActRole) {
		BuffValue bufval = new BuffValue(false);
		
		NewBuff buff = null;
		
		buff = ActRole.getBuff(GsConst.Buff.MOONLIGHT); //月光
		if (buff != null) {
			double value = (buff.canStack())? 0 : buff.getArgs().get(0) ;
			bufval.addValue(buff.getBuffType(),value);
			if (value > 0) {
				ActRole.ResetBuffStack(GsConst.Buff.MOONLIGHT);
			}
		}
		
		return bufval.getAlladd();
	}
	
	/**
	 * 檢查復生BUFF
	 */
	public static boolean checkRebirth(NewBattleRole ActRole) {
		NewBuff buff = null;
		
		buff = ActRole.getBuff(GsConst.Buff.REBIRTH); //迴光
		if (buff != null) {
			double hpvalue = BattleUtil.round(BattleUtil.mul((double)ActRole.getMaxhp(),buff.getArgs().get(0)),0);
			double mpvalue = BattleUtil.round(BattleUtil.mul((double)ActRole.getMaxmp(),buff.getArgs().get(1)),0);
			ActRole.setHp((int)hpvalue);
			ActRole.setMp((int)mpvalue);
			ActRole.removeBuffById(GsConst.Buff.REBIRTH);
			return true;
		}
		return false;
	}
	
	public static List<Integer> forceChangeElement(NewBattleRole ActRole) {
		NewBuff buff = null;
		List<Integer> newElement = new ArrayList<>();
		buff = ActRole.getBuff(GsConst.Buff.STONE); //石化
		if (buff != null) {
			newElement.add(GsConst.ElementType.None);
		}
		return newElement;
	}
	
//	/**
//	 * 是否狂亂狀態(只可普攻)
//	 */
//	public static boolean getSpecialBuff(NewBattleRole ActRole) {
//		return false;
//	}
}
