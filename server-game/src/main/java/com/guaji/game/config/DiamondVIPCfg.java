package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

// 儲值鑽石獲得聲望對應表
@ConfigManager.XmlResource(file = "xml/DiamondVIP.xml", struct = "map")
public class DiamondVIPCfg extends ConfigBase {
	
	/**
	 * 配置 對應goodsId
	 */
	@Id
	private final int goodsId;
	
	/**
	 * 儲值某商品獲得VIP聲望
	 */
	private final int gainVIP;
	

	public DiamondVIPCfg() {
		this.goodsId = 0;
		this.gainVIP = 0;
	}


	public int getGoodsId() {
		return goodsId;
	}

	public int getGainVIP() {
		return gainVIP;
	}



	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		return true;
	}

	/**
	 * 检测有消息
	 * 
	 * @return
	 */
	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
