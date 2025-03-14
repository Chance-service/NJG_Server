package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.attribute.Attribute;

@ConfigManager.XmlResource(file = "xml/worldBossFailingBuff.xml", struct = "map")
public class WorldBossFailingBuffCfg extends ConfigBase {

	@Id
	private final int id;

	/**
	 * 部位类型
	 */
	private final int type;

	/**
	 * 权重
	 */
	private final int weight;

	/**
	 * buff属性值
	 */
	private final String attrs;

	/**
	 * buff 属性
	 */
	public Attribute attribute;

	public WorldBossFailingBuffCfg() {
		this.id = 0;
		this.type = 0;
		this.weight = 0;
		this.attrs = "";
	}

	@Override
	protected boolean assemble() {
		if (attrs != null && attrs.length() > 0) {
			attribute = Attribute.valueOf(attrs);
		}

		return true;
	}

	public int getId() {
		return id;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public int getType() {
		return type;
	}

	public int getWeight() {
		return weight;
	}

	public String getAttrs() {
		return attrs;
	}

}
