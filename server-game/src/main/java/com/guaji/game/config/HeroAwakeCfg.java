package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.attribute.Attribute;

@ConfigManager.XmlResource(file = "xml/Hero_Awake.xml", struct = "map")
public class HeroAwakeCfg extends ConfigBase {
	/**
	 * 配置id(等級)
	 */
	@Id
	protected final int ID;	
	/**
	 * 星數限制
	 */
	protected final int Star;
	/**
	 * 消耗魂魄道具
	 */
	protected final int Shard;
	/**
	 * 獎勵
	 */
	protected final String Attr;
	/**
	 * 升級消耗
	 */
	protected final String Cost;
	/**
	 * 基礎屬性倍率
	 */
	protected final float States;
	/**
	 * SP
	 */
	protected final String SP;
	
	
	private Attribute attribute;


	public HeroAwakeCfg() {
		ID = 0;
		Star = 0;
		Shard = 0;
		Attr = "";
		Cost = "";
		States = 0.0f;
		SP = "";
		attribute = null;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		attribute = Attribute.valueOf(Attr);
		return true;
	}
	
	public int getId() {
		return ID;
	}
	
	public int getStar() {
		return this.Star;
	}
	
	public int getShard() {
		return this.Shard;
	}

	public String getAttr() {
		return this.Attr;
	}
	
	public String getCost() {
		return this.Cost;
	}
	
	public float getStates() {
		return this.States;
	}
	
	public Attribute getAttribute() {
		return this.attribute.clone();
	}
}
