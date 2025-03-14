package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/buff_New.xml", struct = "map")
public class BuffCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	/**
	 * 群組(同一群只能有一個BuffID)
	 */
	protected final int group;
	/**
	 * 类型
	 */
	protected final int type;
	/**
	 * 疊加次數
	 */
	protected final int stack;
	/**
	 * 群組(覆蓋權限大蓋小)
	 */
	protected final int priority;
	/**
	 * buff參數
	 */
	protected final String  values;
	/**
	 * 是否为增益buff
	 */
	protected final int gain;
	
	/**
	 * 是否可见
	 */
	protected final int visible;
	
	/**
	 * 是否可以驱散
	 */
	private final int dispel;
	
	/**
	 * 一般數值陣列
	 */
	private List<Double> ValueList;
	
	public BuffCfg() {
		id = 0;
		group = 0;
		type = 0;
		priority = 0;
		values ="";
		stack = 0;
		gain = 0;
		visible = 1;
		dispel= 0;
		ValueList = new ArrayList<>();
	}

	public int getId() {
		return id;
	}
	
	public int getGroup() {
		return group;
	}

	public int getType() {
		return type;
	}
	
	public int getPriorty() {
		return priority;
	}
	
	public int getStack() {
		return stack;
	}

	public int getGain() {
		return gain;
	}

	public int getVisible() {
		return visible;
	}
	
	@Override
	protected boolean assemble() {
		ValueList.clear();
		if (!values.isEmpty()) {
			String[] Sks = values.split(",");
			for (String sid : Sks) {
				ValueList.add(Double.valueOf(sid.trim()));
			}
		}
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
	
	public int getDispel() {
		return dispel;
	}
	
	public boolean isGain() {
		return gain > 0;
	}
	
	public List<Double> getParams(){
		return ValueList;
	}
}
