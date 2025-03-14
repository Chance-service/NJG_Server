package com.guaji.game.config;

import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.attribute.Attribute;

@ConfigManager.XmlResource(file = "xml/GuildSoul.xml", struct = "map")
public class GuildSoulCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int ID;	
	/**
	 * 天賦種類
	 */
	protected final int Type;
	/**
	 * 等級
	 */
	protected final int Level;
	/**
	 * 獎勵
	 */
	protected final String Attr;
	/**
	 * 升級消耗
	 */
	protected final String Cost;
	
	private Attribute attribute;


	public GuildSoulCfg() {
		ID = 0;
		Type = 0;
		Level = 0;
		Attr = "";
		Cost = "";
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
	
	public int getLevel() {
		return this.Level;
	}
	
	public int getType() {
		return this.Type;
	}

	public String getAttr() {
		return this.Attr;
	}
	
	public String getCost() {
		return this.Cost;
	}

	public static GuildSoulCfg getGuildSoulCfg(int type,int lv) {
		GuildSoulCfg cfg = null;
		Map<Object,GuildSoulCfg> cfgMap = ConfigManager.getInstance().getConfigMap(GuildSoulCfg.class);
		for (GuildSoulCfg acfg : cfgMap.values()) {
			if ((lv == acfg.getLevel())&&(type == acfg.getType())) {
				cfg = acfg;
				break;
			}
		}
		return cfg;
	}
	
	public Attribute getAttribute() {
		return this.attribute.clone();
	}
}
