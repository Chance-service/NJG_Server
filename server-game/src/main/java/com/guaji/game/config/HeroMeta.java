package com.guaji.game.config;

import com.guaji.game.util.GsonUtil;

public class HeroMeta {
	/**
	 * 配置id
	 */
	private  int ID;
	/**
	 *種族
	 */
	private int Race;
	/**
	 *性別
	 */
	private int gender;
	/**
	 *職業
	 */
	private int job;
	/**
	 *左眼
	 */
	private int eye_L;
	/**
	 *右眼
	 */
	private int eye_R;
	/**
	 *皮膚
	 */
	private int skin;
	/**
	 *髮型
	 */
	private int hair;
	/**
	 *髮色
	 */
	private int haircolor;
	/**
	 *血統
	 */
	private int lineage;
	/**
	 *屬性
	 */
	private int attr;
	/**
	 *聲音
	 */
	private int voice;

	public HeroMeta() {
		ID = 0;
		Race = 0;
		gender = 0;
		job = 0;
		eye_L = 0;
		eye_R = 0;
		skin = 0;
		hair = 0;
		haircolor = 0;
		lineage = 0;
		attr = 0;
		voice = 0;
	}
	
	public int getID() {
		return this.ID;
	}
	
	public int getGender() {
		return this.gender;
	}
	
	public int getRace() {
		return this.Race;
	}
	
	public int getjob() {
		return this.job;
	}
	
	public int geteye_L() {
		return this.eye_L;
	}
	
	public int geteye_R() {
		return this.eye_R;
	}
	
	public int  getskin() {
		return this.skin;
	}
	
	public int  gethair() {
		return this.hair;
	}
	
	public int gethaircolor() {
		return this.haircolor;
	}
	
	public int getlineage() {
		return this.lineage;
	}
	
	public int getattr() {
		return this.attr;
	}
	
	public int getvoice() {
		return this.voice;
	}
	
	public String getDataString() {
		return String.format("%d_%d_%d_%d_%d_%02d_%02d_%d_%d_%d_%02d_%d",Race,gender,job,lineage,attr,eye_L,eye_R,skin,voice,hair,haircolor,ID);
	}
	
	public String getIconString() {
		return String.format("%d%d%d%02d%02d%d%d%02d",Race,gender,job,eye_L,eye_R,skin,hair,haircolor);
	}
	
	public String getPngName() {
		return String.format("%d-%d-%d-%d-%d-%d-%d-%d",Race,gender,job,eye_L,eye_R,skin,hair,haircolor);
	}
	
	//-------------------------------
	public void setID(int id) {
		this.ID = id;
	}
	
	public void setGender(int sex) {
		this.gender = sex;
	}
	
	public void setRace(int race) {
		this.Race = race;
	}
	
	public void setjob(int prof) {
		this.job = prof;
	}
	
	public void seteye_L(int eye) {
		this.eye_L = eye;
	}
	
	public void seteye_R(int eye) {
		this.eye_R = eye;
	}
	
	public void  setskin(int askin) {
		this.skin = askin;
	}
	
	public void  sethair(int ahair) {
		this.hair = ahair;
	}
	
	public void sethaircolor(int color) {
		this.haircolor = color;
	}
	
	public void setlineage(int alineage) {
		this.lineage = alineage;
	}
	
	public void setattr(int aval) {
		this.attr = aval;
	}
	
	public void setvoice(int avoice) {
		this.voice = avoice;
	}
		
	@Override
	public String toString() {
		String json = GsonUtil.getJsonInstance().toJson(this);
		return json;
	}
	
	public String fixtoString() {
		String json = GsonUtil.getJsonInstance().toJson(this);
		String fixjon = json.replace("job", "Job:").replace("gender", "Gender");
		return fixjon;
	}

}
