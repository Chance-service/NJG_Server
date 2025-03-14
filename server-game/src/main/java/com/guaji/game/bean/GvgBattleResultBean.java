package com.guaji.game.bean;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：Apr 9, 2019 2:06:03 PM 类说明
 */
public class GvgBattleResultBean {
	/**
	 * 帮派编号
	 */
	private int allianceId;

	/**
	 * gvg 总积分
	 */
	private int nTotalScore;

	/**
	 * 入围排名
	 */
	private int rank;

	/**
	 * 占领1级城池数量
	 */
	private int level1CityNum;
	/**
	 * 占领2级城池数量
	 */
	private int level2CityNum;
	/**
	 * 占领3级城池数量
	 */
	private int level3CityNum;

	public int getAllianceId() {
		return allianceId;
	}

	public GvgBattleResultBean(int allianceId, int vitalityRank) {
		super();
		this.allianceId = allianceId;
		this.nTotalScore = 0;
		this.level1CityNum = 0;
		this.level2CityNum = 0;
		this.level3CityNum = 0;
		this.rank = vitalityRank;
	}

	public void setAllianceId(int allianceId) {
		this.allianceId = allianceId;
	}

	public int getLevel1CityNum() {
		return level1CityNum;
	}

	public void setLevel1CityNum(int level1CityNum) {
		this.level1CityNum = level1CityNum;
	}

	public void AddLevel1CityNum() {
		this.level1CityNum++;
	}

	public int getLevel2CityNum() {
		return level2CityNum;
	}

	public void AddLevel2CityNum() {
		this.level2CityNum++;
	}

	public void setLevel2CityNum(int level2CityNum) {
		this.level2CityNum = level2CityNum;
	}

	public int getLevel3CityNum() {
		return level3CityNum;
	}

	public void AddLevel3CityNum() {
		this.level3CityNum++;
	}

	public void setLevel3CityNum(int level3CityNum) {
		this.level3CityNum = level3CityNum;
	}

	public int getnTotalScore() {
		return nTotalScore;
	}

	public void setnTotalScore(int nTotalScore) {
		this.nTotalScore = nTotalScore;
	}

	public void AddBattleScore(int addScore) {
		this.nTotalScore += addScore;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getTotalCityNum() {

		return this.level1CityNum + this.level2CityNum + this.level3CityNum;
	}

	public String getCityNumInfo() {

		Map<String, Integer> cityNumMap = new HashMap<String, Integer>();
		cityNumMap.put("1", this.level1CityNum);
		cityNumMap.put("2", this.level2CityNum);
		cityNumMap.put("3", this.level3CityNum);
		JSONObject jsonCityNum = JSONObject.fromObject(cityNumMap);
		return jsonCityNum.toString();
	}

}
