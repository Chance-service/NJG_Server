package com.guaji.game.module.activity.activity144;

import java.util.Date;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
//import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;
//
//import com.guaji.game.config.LittleTestRoleCfg;

public class Activity144Status {

	/**
	 * 活动开启时间
	 */
	private Date dateTime;
	
	/**
	 * 現在第幾題 0.未開啟 1-10
	 */
	private int level;
	/**
	 * 目前題目ID
	 */
	private int queid;
	/**
	 * 下一題ID
	 */
	private int nextid;
	/**
	 * 答對題數
	 */
	private int binbom;
	/**
	 *分數 
	 */
	private int score;
	/**
	 * 獲得獎勵 
	 */
	private String rewards;
	/**
	 * 參戰後輩3人id
	 */
	private String Rolestr;
	/**
	 * 戰隊目前血量
	 */
	private int TeamHP;
	/**
	 * 戰隊最大血量
	 */
	private int TeamMaxHP;
	/**
	 * 速度
	 */
	private int TeamSpeed;
	/**
	 * TeamBuff
	 */
	private String TeamBuff;
	/**
	 * TeamRoundBuff
	 */
	private String TeamRoundBuff;
	/**
	 *Bossid
	 */
	private int Bossid ;
	/**
	 * Boss目前血量
	 */
	private int BossHP;
	/**
	 * Boss最大血量
	 */
	private int BossMaxHP;
	/**
	 * Boss速度
	 */
	private int BossSpeed;
	/**
	 * BossBuff
	 */
	private String BossBuff;
	/**
	 * TeamRoundBuff
	 */
	private String BossRoundBuff;
	/**
	 * 擊殺參數
	 */
	private int KillValue;
	
	public Activity144Status() {
		dateTime = GuaJiTime.getAM0Date();
		this.level = 0;
		this.queid = 0;
		this.nextid = 0;
		this.binbom = 0;
		this.score = 0;
		this.rewards = "";
		this.Rolestr ="";
		this.TeamHP = 0;
		this.TeamMaxHP = 0;
		this.TeamSpeed = 0;
		this.TeamBuff = "";
		this.TeamRoundBuff = "";
		this.Bossid = 0;
		this.BossHP = 0;
		this.BossMaxHP = 0;
		this.BossSpeed = 0;
		this.BossBuff = "";
		this.BossRoundBuff ="";
		this.KillValue = 0;
	}
	
	public int getlevel() {
		return level;
	}

	public void setlevel(int level) {
		this.level = level;
	}

	public int getqueid() {
		return queid;
	}

	public void setqueid(int id) {
		this.queid = id;
	}
	
	public int getnextid() {
		return nextid;
	}
	
	public void setnextid(int id) {
		this.nextid = id;
	}
	public int getbinbom() {
		return binbom;
	}
	
	public void setbinbom(int count) {
		this.binbom = count;
	}
	
	public int getscore() {
		return score;
	}
	
	public void setscore(int score) {
		this.score = score;
	}	
	
	public String getrewards() {
		return rewards;
	}

	public void setrewards(String rewards) {
		this.rewards = rewards;
	}
//-------------for team--------------------
	public String getRoles() {
		return Rolestr;
	}

	public void setRoles(String Rolestr) {
		this.Rolestr = Rolestr;
	}
		
	public int getTeamHP() {
		return TeamHP;
	}
	
	public void setTeamHP(int TeamHP) {
		this.TeamHP = TeamHP;
	}
	
	public int getTeamMaxHP() {
		return TeamMaxHP;
	}
	
	public void setTeamMaxHP(int MaxHP) {
		this.TeamMaxHP = MaxHP;
	}

	public int getTeamSpeed() {
		return TeamSpeed;
	}
	
	public void setTeamSpeed(int TeamSpeed) {
		this.TeamSpeed = TeamSpeed;
	}
	
	public String getTeamBuff() {
		return TeamBuff;
	}

	public void setTeamBuff(String TeamBuff) {
		this.TeamBuff = TeamBuff;
	}
	
	public String getTeamRoundBuff() {
		return TeamRoundBuff;
	}

	public void setTeamRoundBuff(String TeamRoundBuff) {
		this.TeamRoundBuff = TeamRoundBuff;
	}	
	
	public int getKillValue() {
		return this.KillValue;
	}
	
	public void setKillValue(int value) {
		this.KillValue = value;
	}
//----------------for team----------------------------
//----------------for boss----------------------------	
	public int getBossid() {
		return Bossid;
	}
	
	public void setBossid(int Bossid) {
		this.Bossid = Bossid;
	}
	
	public int getBossHP() {
		return BossHP;
	}
	
	public void setBossHP(int BossHP) {
		this.BossHP = BossHP;
	}
	
	public int getBossMaxHP() {
		return BossMaxHP;
	}
	
	public void setBossMaxHP(int BossMaxHP) {
		this.BossMaxHP = BossMaxHP;
	}

	public int getBossSpeed() {
		return BossSpeed;
	}
	
	public void setBossSpeed(int BossSpeed) {
		this.BossSpeed = BossSpeed;
	}
	
	public String getBossBuff() {
		return BossBuff;
	}

	public void setBossBuff(String BossBuff) {
		this.BossBuff = BossBuff;
	}
	
	public String getBossRoundBuff() {
		return BossRoundBuff;
	}

	public void setBossRoundBuff(String BossRoundBuff) {
		this.BossRoundBuff = BossRoundBuff;
	}
//---------------boss end--------------------------------------	
	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	
	
}
