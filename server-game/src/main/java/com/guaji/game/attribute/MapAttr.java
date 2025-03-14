package com.guaji.game.attribute;

/**
 * 地图属性
 */
public class MapAttr {
	/**
	 * 地图id
	 */
	private int mapId;
	/**
	 * 战斗次数
	 */
	private int fightTimes;

	public MapAttr() {
		mapId = 0;
		fightTimes = 0;
	}

	public MapAttr(int mapId, int fightTimes) {
		this.mapId = mapId;
		this.fightTimes = fightTimes;
	}

	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	public int getFightTimes() {
		return fightTimes;
	}

	public void setFightTimes(int fightTimes) {
		this.fightTimes = fightTimes;
	}
}
