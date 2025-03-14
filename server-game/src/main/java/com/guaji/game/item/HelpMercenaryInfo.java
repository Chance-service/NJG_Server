package com.guaji.game.item;

public class HelpMercenaryInfo {

	/**
	 * 剩余血量
	 */
	private int hp;
	/**
	 * 剩余魔法值
	 */
	private int mp;

	/**
	 * 所属玩家id
	 */
	private int playerId;

	public HelpMercenaryInfo() {
		// TODO Auto-generated constructor stub
	}

	public HelpMercenaryInfo(int hp, int mp, int playerId) {
		super();
		this.hp = hp;
		this.mp = mp;
		this.playerId = playerId;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getMp() {
		return mp;
	}

	public void setMp(int mp) {
		this.mp = mp;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public boolean isAlive() {
		return this.getHp() > 0;
	}

}
