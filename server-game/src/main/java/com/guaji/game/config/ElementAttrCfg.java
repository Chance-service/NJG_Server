package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 元素属性定义列表
 * @author xulinqs
 *
 */
@ConfigManager.XmlResource(file = "xml/elementAttr.xml", struct = "map")
public class ElementAttrCfg extends ConfigBase {

	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	
	/**
	 * 属性Id
	 */
	protected final int attrId;
	/**
	 * 类型
	 */
	protected final int type;
	/**
	 * 属性值 
	 */
	protected final int attrValue;
	
	public ElementAttrCfg() {
		this.id = 0;
		this.attrId = 0;
		this.type = 0;
		this.attrValue = 0;
	}
	
	
	
	public int getId() {
		return id;
	}

	public int getAttrId() {
		return attrId;
	}

	public int getType() {
		return type;
	}

	public int getAttrValue() {
		return attrValue;
	}

	@Override
	protected boolean assemble() {
		return super.assemble();
	}
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
