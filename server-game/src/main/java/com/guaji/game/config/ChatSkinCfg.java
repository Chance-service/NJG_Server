package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/chatSkin.xml", struct = "map")
public class ChatSkinCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	private final int skinId;
	private final int goodsId;
	private final int costGold;
	private final int days;

 	public ChatSkinCfg() {
		goodsId = 0;
		skinId = 0;
		days = 0;
		costGold = 0;
	}

	public int getGoodsId() {
		return goodsId;
	}

	public int getSkinId() {
		return skinId;
	}

	public int getDays() {
		return days;
	}

	public int getCostGold() {
		return costGold;
	}
}
