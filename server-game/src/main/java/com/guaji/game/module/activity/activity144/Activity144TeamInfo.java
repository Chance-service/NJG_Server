package com.guaji.game.module.activity.activity144;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;

import com.guaji.game.config.LittleTestRoleCfg;
import com.guaji.game.config.LittleTestSkillCfg;
import com.guaji.game.config.LittleTestSkillCfg.ArgData;

public class Activity144TeamInfo extends Activity144BaseInfo  {
	
	/**
	 * 參戰的後輩id
	 */
	private List<Integer>RoleAry;
	/**
	 * 角色擁有的技能  Roleid / skillid
	 */	
    private Map<Integer,Integer> SkillMap;
	/**
	 * 所有角色加成獎品數量
	 */	
    private int addaward;    
   
    	
	public Activity144TeamInfo(Activity144Status status) {
		this.RoleAry = new LinkedList<>();
		this.SkillMap = new HashMap<>();
		String Rolestr = status.getRoles();
		this.addaward = 0;
		this.BaseATK = 0;
		if (!Rolestr.equals("")) {
			String[] ss = Rolestr.split("_");
			for(String s : ss) {
				int rid = Integer.parseInt(s);
				this.RoleAry.add(rid);
				LittleTestRoleCfg Cfg = ConfigManager.getInstance().getConfigMap(LittleTestRoleCfg.class).get(rid);
				if (Cfg != null) {
					BaseATK = BaseATK+Cfg.getATK();
					BaseSpeed = BaseSpeed+Cfg.getSpeed();
					SkillMap.put(rid,Cfg.getSkillID());
					LittleTestSkillCfg skillcfg = ConfigManager.getInstance().getConfigMap(LittleTestSkillCfg.class).get(Cfg.getSkillID());
					if (skillcfg != null)
					{
						Map<Integer,ArgData> BuffArgMap = skillcfg.getBuffArgMap();
						for (Integer Buffid: BuffArgMap.keySet()) {
							if (Buffid == 501) {
								this.addaward = this.addaward+1;
								break;
							}
						}
					}
				}
			}
		}
		this.NowHP = status.getTeamHP();
		this.MaxHP = status.getTeamMaxHP();
		this.Speed = status.getTeamSpeed();
		this.Buff = new HashMap<String,BuffData>();
		this.RoundBuff = new ArrayList<BuffData>();
		this.BuffVal = new HashMap<>();
		
		String BuffStr = status.getTeamBuff();
		
		BuffStrtoAllBuff(BuffStr);
	}
	
	@Override
	public void setStatus(Activity144Status status) {
		String RoleStr = ConverRoleListToStr();
		status.setRoles(RoleStr);
		status.setTeamHP(getNowHP());
		status.setTeamMaxHP(getMaxHP());
		status.setTeamSpeed(getSpeed());
		String BuffStr = ConverBuffToStr(getBuffMap());		
		status.setTeamBuff(BuffStr);
		String NowBuffStr = ConverBuffToStr(getRoundBuffList());
		status.setTeamRoundBuff(NowBuffStr);
	}
	
	public void AddBuff(int actNo,Activity144BossInfo BossData) {
		boolean selfreset = false;
		boolean enemyreset = false;
		for (Integer Roleid: SkillMap.keySet()) {
			LittleTestSkillCfg skillcfg = ConfigManager.getInstance().getConfigMap(LittleTestSkillCfg.class).get(SkillMap.get(Roleid));
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
							setRoundBuff(Roleid,Buffid,Arg1,times);
							selfreset = true;
						}else if (target == LittleTestSkillCfg.target_enemy) { // add enemy
							BossData.setRoundBuff(Roleid,Buffid,Arg1,times);
							enemyreset =true;
						}else if (target == LittleTestSkillCfg.target_all) { // add enemy
							setRoundBuff(Roleid,Buffid,Arg1,times);
							selfreset = true;
							BossData.setRoundBuff(Roleid,Buffid,Arg1,times);
							enemyreset =true;
						}
					}	
				}
			}
		}
		
		if (selfreset) {
			RoundBufftoAllBuff();
			resetBuffvalue();
		}
		
		if (enemyreset) {
			BossData.RoundBufftoAllBuff();
			BossData.resetBuffvalue();
		}
	}
			
	public String ConverRoleListToStr() {
		String RoleStr = "";
		if ((RoleAry != null) && (RoleAry.size() > 0)) {
			for (Integer rid : RoleAry) {
				if (RoleStr.equals("")) {
					RoleStr = String.format("%d",rid);
				}
				else {
					RoleStr = RoleStr+String.format("_%d",rid);
				}
			}
		}
		return RoleStr;
	}
				
	public List<Integer> getRoleAry(){
		return RoleAry;
	}
	
	public int getaddaward() {
		return this.addaward;
	}
			
}
