package com.guaji.game.module.activity.activity144;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;
import org.guaji.os.MyException;

import com.guaji.game.config.LittleTestQuestionCfg;
import com.guaji.game.config.LittleTestRoleCfg;
import com.guaji.game.config.LittleTestSkillCfg;
import com.guaji.game.config.LittleTestSkillCfg.ArgData;

public class Activity144BossInfo extends Activity144BaseInfo {
	
	/**
	 * Bossid
	 */
	private int Bossid;
	/**
	 * Boss擁有的技能
	 */	
    private int BossSkill;
	/**
	 * 擊殺可獲得積分
	 */	
    private int Intimacy;
    	
	public Activity144BossInfo(Activity144Status status) {
		this.Bossid = status.getBossid();
		this.NowHP = status.getBossHP();
		this.MaxHP = status.getBossMaxHP();
		this.Speed = status.getBossSpeed();
		
		LittleTestRoleCfg Cfg = ConfigManager.getInstance().getConfigMap(LittleTestRoleCfg.class).get(Bossid);
		if (Cfg != null) {
			this.BaseATK = Cfg.getATK();
			this.BaseSpeed = Cfg.getSpeed();
			this.BossSkill = Cfg.getSkillID();
			this.Intimacy =  Cfg.getIntimacy();
		}
		this.Buff = new HashMap<String,BuffData>();
		this.RoundBuff = new ArrayList<BuffData>();
		this.BuffVal = new HashMap<>();
		
		String BuffStr = status.getBossBuff();
		
		BuffStrtoAllBuff(BuffStr);
	}
	
	@Override
	public void setStatus(Activity144Status status) {
		status.setBossid(getBossid());
		status.setBossHP(getNowHP());
		status.setBossMaxHP(getMaxHP());
		status.setBossSpeed(getSpeed());
		String BuffStr = ConverBuffToStr(getBuffMap());		
		status.setBossBuff(BuffStr);
		String NowBuffStr = ConverBuffToStr(getRoundBuffList());
		status.setBossRoundBuff(NowBuffStr);
	}
		
	public void AddBuff(int actNo ,Activity144TeamInfo TeamData ) {
		boolean selfreset = false;
		boolean enemyreset = false;
		LittleTestSkillCfg skillcfg = ConfigManager.getInstance().getConfigMap(LittleTestSkillCfg.class).get(getBossSkill());
		if ((skillcfg != null)&&(skillcfg.getSF() == actNo)) {
			if (GuaJiRand.randPercentRate(skillcfg.getRate())) {
				Map<Integer,ArgData> BuffArgMap = skillcfg.getBuffArgMap();
				for (Integer Buffid: BuffArgMap.keySet()) {
					int Arg1 = BuffArgMap.get(Buffid).getArg(LittleTestSkillCfg.Buff_Arg1);
					int target = BuffArgMap.get(Buffid).getArg(LittleTestSkillCfg.Buff_Arg2);
					int times = BuffArgMap.get(Buffid).gettimes();
					if ((Arg1 == 0)||(times == 0)||(Buffid == 0)||(target== 0))
						break;
					if (target == LittleTestSkillCfg.target_self) { // add self
						setRoundBuff(getBossid(),Buffid,Arg1,times);
						selfreset = true;
					}else if (target == LittleTestSkillCfg.target_enemy){ // add enemy
						TeamData.setRoundBuff(getBossid(),Buffid,Arg1,times);
						enemyreset =true;
					}
					else if (target == LittleTestSkillCfg.target_all) { // add all
						setRoundBuff(getBossid(),Buffid,Arg1,times);
						selfreset = true;
						TeamData.setRoundBuff(getBossid(),Buffid,Arg1,times);
						enemyreset =true;
					}
				}	
			}
		}
		
		if (selfreset) {
			RoundBufftoAllBuff();
			resetBuffvalue();
		}
		
		if (enemyreset) {
			TeamData.RoundBufftoAllBuff();
			TeamData.resetBuffvalue();
		}
	}
				
	public int getBossid() {
		return Bossid;
	}

	public void setBossid(int Bossid) {
		this.Bossid = Bossid;
	}

	public int getBaseSpeed() {
		return BaseSpeed;
	}
	
	public int getBossSkill() {
		return BossSkill;
	}
	
	public int getIntimacy() {
		return Intimacy;
	}

}
