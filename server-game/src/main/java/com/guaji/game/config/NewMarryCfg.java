package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/newplayerMarry.xml", struct = "map")
public class NewMarryCfg extends ConfigBase {
	/**
	 * 序號
	 */
	@Id
	private final int id;

	/**
	 * 商品信息
	 */
	private final String item;
	
	/**
	 * 可販賣數量
	 */
	private final int weight;
	
	public NewMarryCfg(){
		id = 0;
		item = "";
		weight = 0;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getItem() {
		return this.item;
	}
	
	public int getWeight() {
		return this.weight;
	}
}
