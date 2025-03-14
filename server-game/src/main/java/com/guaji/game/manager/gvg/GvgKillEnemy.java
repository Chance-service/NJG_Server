package com.guaji.game.manager.gvg;

/**
 * GVG连续杀敌DEBUFF缓存
 */
public class GvgKillEnemy {
	
	/**
	 * 城池ID
	 */
	private int cityId;

	/**
	 * 杀敌个数
	 */
	private int killNumber;
	
	/**
	 * 当前DEBUFF
	 */
	private int debuffId;
	
	
	public GvgKillEnemy() {
		
	}
	
	public GvgKillEnemy(int cityId, int killNumber) {
		this.cityId = cityId;
		this.killNumber = killNumber;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public int getKillNumber() {
		return killNumber;
	}

	public void setKillNumber(int killNumber) {
		this.killNumber = killNumber;
	}

	public int getDebuffId() {
		return debuffId;
	}

	public void setDebuffId(int debuffId) {
		this.debuffId = debuffId;
	}
	
}
