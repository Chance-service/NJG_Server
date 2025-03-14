package com.guaji.game.module.activity.activity144;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.guaji.os.GuaJiRand;
import org.guaji.os.MyException;

import com.guaji.game.config.LittleTestQuestionCfg;
import com.guaji.game.config.LittleTestSkillCfg;
import com.guaji.game.module.activity.activity144.Activity144BaseInfo.BuffData;

public class Activity144BaseInfo {

	class BuffData{
		int userid;
		int buffid;
		int value;
		int times;
	};
	/**
	 * 目前血量
	 */
	protected int NowHP;
	/**
	 * 最大血量
	 */
	protected int MaxHP;
	/**
	 * 總速度
	 */
	protected int Speed;
	/**
	 * 攻擊力
	 */	
	protected int ATK;
	/**
	 *基礎速度 
	 */
	protected int BaseSpeed;
	/**
	 * 基礎攻擊力
	 */	
	protected int BaseATK;
	/**
	 *身上所有Buff 
	 */
	protected Map<String,BuffData> Buff;
	/**
	 * 當局的Buff 
	 */
	protected List<BuffData>RoundBuff;
	/**
	 * 身上總Buff Value 
	 */
	protected Map<Integer,Float> BuffVal;
		
	public void setStatus(Activity144Status status) {
	}
		
	public String ConverBuffToStr(List<BuffData>aBufflist) {
		String BuffStr = "";
		for(BuffData aData :aBufflist) {
			if (BuffStr.equals("")) {
				BuffStr = String.format("%d_%d_%d_%d",aData.userid,aData.buffid,aData.value,aData.times);
			}else {
				BuffStr = BuffStr + String.format(";%d_%d_%d_%d",aData.userid,aData.buffid,aData.value,aData.times);
			}	
		}
		return BuffStr;
	}
	
	public String ConverBuffToStr(Map<String,BuffData>aBuffMap) {
		String BuffStr = "";
		for(BuffData aData : aBuffMap.values()) {
			if (BuffStr.equals("")) {
				BuffStr = String.format("%d_%d_%d_%d",aData.userid,aData.buffid,aData.value,aData.times);
			}else {
				BuffStr = BuffStr + String.format(";%d_%d_%d_%d",aData.userid,aData.buffid,aData.value,aData.times);
			}	
		}
		return BuffStr;
	}
			
	public void resetBuffvalue() {
		BuffVal.clear();
		for ( String strkey :Buff.keySet()) {
			BuffData aData = Buff.get(strkey);
			if (BuffVal.containsKey(aData.buffid)) {
				float Val = BuffVal.get(aData.buffid);
				BuffVal.replace(aData.buffid,Val*(1+aData.value/100));
			}else {
				BuffVal.put(aData.buffid,(float)(1+aData.value/100));
			}
		}
		
		// 速度上升遞減
		if (BuffVal.containsKey(LittleTestSkillCfg.Buff_AddSpeed)) {
			float value = BuffVal.get(LittleTestSkillCfg.Buff_AddSpeed);
			if (value >= 2.0) {
				value = (float)(1.0+(value-1)/(value*0.5));
				BuffVal.replace(LittleTestSkillCfg.Buff_AddSpeed,value);
			}
		}
		
		countsalfvalue();
	}
	
	public void countsalfvalue() {
		
//		public static final int Buff_AddSpeed = 1;			// 增加速度
//		public static final int Buff_AddAtk = 2; 			// 增加攻擊
//		public static final int Buff_DecHurt = 3; 			// 減少被擊傷害
//		public static final int Buff_AddHP = 4;				// 恢復血量
//		public static final int Buff_AddFinalSpeed = 5;		// 增加最終速度
//		public static final int Buff_DecAtk = 101;			// 減少攻擊力
//		public static final int Buff_AddHurt = 102;			// 增加被擊傷害
//		public static final int Buff_DecSpeed = 103;		// 減少速度
//		public static final int Buff_DecFinalSpeed = 104;	// 減少最終速度
		
		setATK(getBaseATK());
		setSpeed(getBaseSpeed());
		for (Integer buffkind :BuffVal.keySet()) {
			switch (buffkind) {
			case LittleTestSkillCfg.Buff_AddSpeed:
			case LittleTestSkillCfg.Buff_DecSpeed:
					setSpeed((int)(getSpeed()*BuffVal.get(buffkind)));
				break;
			case LittleTestSkillCfg.Buff_AddAtk:
			case LittleTestSkillCfg.Buff_DecAtk:
					float dval = BuffVal.get(buffkind);
					setATK((int)(getATK()*dval));
				break;
//			case LittleTestSkillCfg.Buff_AddSpeedValue:
//				break;
//			case LittleTestSkillCfg.Buff_Atk_double:
//					setATK(getATK()*BuffVal.get(buffkind));
//				break;
			//case LittleTestSkillCfg.Buff_AddDefense:
			//	break;
			//case LittleTestSkillCfg.Buff_AddHP:
			//	break;
			default:
				break;
			}
		}
		
		if (BuffVal.containsKey(LittleTestSkillCfg.Buff_AddFinalSpeed)) {
			setSpeed((int)(getSpeed()+BuffVal.get(LittleTestSkillCfg.Buff_AddFinalSpeed)));
		}
		
		if (BuffVal.containsKey(LittleTestSkillCfg.Buff_DecFinalSpeed)) {
			setSpeed((int)(getSpeed()-BuffVal.get(LittleTestSkillCfg.Buff_DecFinalSpeed)));
		}
	}
	
	public void setRoundBuff(int userid ,int buffid, int value ,int times) {
		BuffData Nowbuff = new BuffData();
		Nowbuff.userid = userid;
		Nowbuff.buffid = buffid;
		Nowbuff.value = value;
		Nowbuff.times = times;
		RoundBuff.add(Nowbuff);
	}
	
	public void RoundBufftoAllBuff() {
		for (BuffData Newbuff:RoundBuff){
			String strkey = String.format("%d%d",Newbuff.userid,Newbuff.buffid);
			if (Buff.containsKey(strkey)){
				Buff.replace(strkey, Newbuff);
			}else {
				Buff.put(strkey,Newbuff);
			}
		}
	}
	
	public void BuffStrtoAllBuff(String BuffStr) {
		if	(BuffStr != null && BuffStr.length() > 0){
			String[] BuffAry = BuffStr.split(";");
			for (String aBuff :BuffAry) {
				String[] ValAry = aBuff.split("_");
				if (ValAry.length < 4) {
					continue;
				}
				int buffid = Integer.parseInt(ValAry[1]);
				BuffData bufdata = new BuffData();
				bufdata.userid = Integer.parseInt(ValAry[0]);
				bufdata.buffid =  buffid;
				bufdata.value = Integer.parseInt(ValAry[2]);
				bufdata.times = Integer.parseInt(ValAry[3]);
				String strkey = ValAry[0]+ValAry[1];
				Buff.put(strkey,bufdata);
			}
		}
	}
	
	public void finishBuff() {
		List <String> delList = new ArrayList<>();
		for(Map.Entry<String,BuffData> entry : Buff.entrySet()) {
			if (entry.getValue().times >= 2){
				entry.getValue().times = entry.getValue().times -1;
			}else {
				entry.getValue().times = 0;
				delList.add(entry.getKey());
			}
		}
		for(String key:delList) {
			if (Buff.containsKey(key)){
				Buff.remove(key);
			}
		}
		
		if (delList.size()!= 0) {
			resetBuffvalue();
		}
	}
	
	public void RecoveryHP() {
		if (BuffVal.containsKey(LittleTestSkillCfg.Buff_AddHP)) {
			float value = BuffVal.get(LittleTestSkillCfg.Buff_AddHP);
			setNowHP(getNowHP()+(int)value);
		}
	}
	
	public boolean AttackEnemy(Activity144BaseInfo EenemyData,boolean right ,LittleTestQuestionCfg QuestCfg) {
		int hurt = getATK();
		float bet = 1;
		if (right) {
			try {
				bet = GuaJiRand.randInt(QuestCfg.getMinAT()/100,QuestCfg.getMaxAT()/100);
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		else{
			try {
				bet = GuaJiRand.randInt(QuestCfg.getMinAF()/100,QuestCfg.getMaxAF()/100);
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		hurt = (int)(hurt*bet);
		Map<Integer,Float> BuffValMap = EenemyData.getBuffValMap();
		float aval = 0;
		if (BuffValMap.containsKey(LittleTestSkillCfg.Buff_AddHurt)) {
			aval =BuffValMap.get(LittleTestSkillCfg.Buff_AddHurt);
			hurt = (int)(hurt*aval);
		}
		if (BuffValMap.containsKey(LittleTestSkillCfg.Buff_DecHurt)) {
			aval =BuffValMap.get(LittleTestSkillCfg.Buff_DecHurt);
			hurt =(int)(hurt*aval);
		}
		
		EenemyData.setNowHP(Math.max(0,EenemyData.getNowHP()-hurt));
		
		if (EenemyData.getNowHP() == 0) {
			return true;
		}
		return false;
	}
	
	public int getNowHP() {
		return NowHP;
	}

	public void setNowHP(int NowHP) {
		this.NowHP = NowHP;
	}
	
	public int getMaxHP() {
		return MaxHP;
	}
	
	public void setMaxHP(int MaxHp) {
		this.MaxHP = MaxHp;
	}
		
	public int getSpeed() {
		return Speed;
	}
	
	public void setSpeed(int Speed) {
		this.Speed = Speed;
	}
	
	public int getATK() {
		return ATK;
	}
	
	public void setATK(int ATK) {
		this.ATK = ATK;
	}
	
	public int getBaseSpeed() {
		return BaseSpeed;
	}
	
	public void setBaseSpeed(int BsaeSpeed) {
		this.BaseSpeed = BsaeSpeed;
	}
		
	public int getBaseATK() {
		return BaseATK;
	}
	
	public void setBaseATK(int BaseATK) {
		this.BaseATK = BaseATK;
	}
	
	public Map<String,BuffData> getBuffMap(){
		return Buff;
	}
	
	public List<BuffData> getRoundBuffList(){
		return RoundBuff;
	}
	
	public Map<Integer,Float> getBuffValMap(){
		return BuffVal;
	}
		
}
