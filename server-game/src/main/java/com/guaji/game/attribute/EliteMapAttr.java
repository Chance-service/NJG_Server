package com.guaji.game.attribute;

/**
 * 经验副本属性
 */
public class EliteMapAttr {
	/**
	 * 等级
	 */
	private int level;
	/**
	 * 地图id
	 */
	private int mapId;

	public EliteMapAttr() {
		level = 0;
		mapId = 0;
	}

	public EliteMapAttr(int level, int mapId) {
		this.mapId = mapId;
		this.level = level;
	}

	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
}
