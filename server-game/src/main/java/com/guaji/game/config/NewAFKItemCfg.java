package com.guaji.game.config;


import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.DropItems;

@ConfigManager.XmlResource(file = "xml/map_AFKItem.xml", struct = "map")
public class NewAFKItemCfg extends ConfigBase{
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	
	protected final int Times;
	
	protected final String Items;
	
	protected final DropItems NormalDropItems;
		
	public NewAFKItemCfg(){
		id = 0;
		Times = 0;
		Items = null;
		NormalDropItems = new DropItems();
	}
	
	public int getTimes() {
		return Times;	
	}
	
	public DropItems getDropItems () {
		return NormalDropItems;
	}
	
	@Override
	protected boolean assemble() {
		
		if (!NormalDropItems.initByString(Items)) {
			return false;
		}	
		return true;
	}
	
	@Override
	protected boolean checkValid() {
		return true;
	}
}
