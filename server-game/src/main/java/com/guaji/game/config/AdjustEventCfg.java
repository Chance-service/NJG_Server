package com.guaji.game.config;

import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;





@ConfigManager.XmlResource(file = "xml/ajustEvent.xml", struct = "map")
public class AdjustEventCfg extends ConfigBase{
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	private final String eventName;

	private final String ios;
	
	private final String andriod;

	public AdjustEventCfg() {
		id=0;
		eventName="";
		ios="";
		andriod="";
	}

	public int getId() {
		return id;
	}

	public String getEventName() {
		return eventName;
	}

	public String getIos() {
		return ios;
	}

	public String getAndriod() {
		return andriod;
	}
	
	/**
	 * 获取佣兵所有佣兵
	 * 
	 * @return
	 */
	public static AdjustEventCfg getEventCfgByName(String eventName) {

		Map<Object, AdjustEventCfg> eventMap = ConfigManager.getInstance().getConfigMap(AdjustEventCfg.class);
		for (AdjustEventCfg aevent : eventMap.values()) {
			if(aevent.getEventName().equals(eventName))
				return aevent;
		}
		return null;
	}
	
}
