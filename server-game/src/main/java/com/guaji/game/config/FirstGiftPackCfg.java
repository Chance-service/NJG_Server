package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/firstgiftpack.xml", struct = "map")
public class FirstGiftPackCfg extends ConfigBase {
	
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	/**
	 * 获得装备
	 */
	protected final String firstgiftpack;
	/**
	 * 对应角色类型
	 */
   protected final int forwhichClass;
   
   /**
    * 构造方法
    */
   public FirstGiftPackCfg(){
	   id = 0;
	   firstgiftpack = "";
	   forwhichClass = 0;
   }
   
   public int getId() {
		return id;
	}

	public String getFirstgiftpack() {
		return firstgiftpack;
	}

	public int getForwhichClass() {
		return forwhichClass;
	}

	@Override
	protected boolean assemble() {
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}
  
}
