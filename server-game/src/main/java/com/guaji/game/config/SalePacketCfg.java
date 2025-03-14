package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;


@ConfigManager.XmlResource(file = "xml/salepacket.xml", struct = "map")
public class SalePacketCfg extends ConfigBase
{
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	/**
	 * 获得奖励
	 */
	protected final String salepacket;
	
	
	protected final int dayMaxBuyTime;

   
   /**
    * 构造方法
    */
   public SalePacketCfg(){
	   id = 0;
	   salepacket = "";
	   dayMaxBuyTime=0;
   }
   
   public int getId() {
		return id;
	}

	public String getSalePacket() {
		return salepacket;
	}

	@Override
	protected boolean assemble() {
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	public int getDayMaxBuyTime() {
		return dayMaxBuyTime;
	}
	
	

}
