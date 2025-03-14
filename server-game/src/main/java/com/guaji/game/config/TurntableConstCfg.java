package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/turntableConst.xml", struct = "map")
public class TurntableConstCfg extends ConfigBase {

	@Id
	protected final int id;
	/**
	 * CD时间
	 */
	protected final int freeCD;
	/**
	 * 一次钻石消耗
	 */
	protected final int oneCost;
	/**
	 * 十次钻石消耗
	 */
	protected final int tenCost;
	/**
	 * 单次增加积分
	 */
	protected final int oneCredits;
	/**
	 * 十次增加积分
	 */
	protected final int tenCredits;

	public TurntableConstCfg() {
		this.id = 0;
		this.freeCD = 0;
		this.oneCost = 0;
		this.tenCost = 0;
		this.oneCredits = 0;
		this.tenCredits = 0;
	}

	public int getId() {
		return id;
	}

	public int getFreeCD() {
		return freeCD;
	}

	public int getOneCost() {
		return oneCost;
	}

	public int getTenCost() {
		return tenCost;
	}

	public int getOneCredits() {
		return oneCredits;
	}

	public int getTenCredits() {
		return tenCredits;
	}

	@Override
	protected boolean assemble() {
		return super.assemble();
	}
}
