package com.guaji.game.util;

public class MonsterItem {
	/**
	 *等級
	 */
	private int level;
	/**
	 * 怪物Id
	 */
	private int monsterId;
	/**
	 * 加權倍率
	 */
	private float ratio;
	
	public MonsterItem() {
		super();
	}

	public MonsterItem(int lv, float ratio, int monsterId) {
		super();
		this.level = lv;
		this.ratio = ratio;
		this.monsterId = monsterId;
	}
	
	public MonsterItem(String info) {
		initByString(info);
	}

	public boolean initByString(String info) {
		if (info != null && info.length() > 0 && !info.equals("0") && !info.equals("none")) {
			String[] items = info.split("_");
			if (items.length < 3) {
				return false;
			}
			level = Integer.parseInt(items[0]);//等級
			ratio = Float.valueOf(items[1]);//加權倍率
			monsterId = Integer.parseInt(items[2]);//怪物ID
			return true;
		}
		return false;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public float getRatio() {
		return ratio;
	}

	public void setRatio(float ratio) {
		this.ratio = ratio;
	}
}
