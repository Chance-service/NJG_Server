package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.attribute.Attribute;

@ConfigManager.XmlResource(file = "xml/worldBossBuff.xml", struct = "map")
public class WorldBossBuffCfg extends ConfigBase {

	/**
	 * 最大id
	 */
	private static int MAX_ID = 0;

	@Id
	private final int id;

	/**
	 * buff价格
	 */
	private final int buffPrice;

	/**
	 * 添加类型（主角=1，佣2，全部=3）
	 */
	private final int addRoleType;

	/**
	 * buff权重
	 */
	private final int buffWeight;

	/**
	 * buff属性值
	 */
	private final String buffAttrs;

	/**
	 * buff 属性
	 */
	public Attribute attribute;

	public WorldBossBuffCfg() {
		this.id = 0;
		this.buffPrice = 0;
		addRoleType = 0;
		this.buffWeight = 0;
		this.buffAttrs = "";
	}

	@Override
	protected boolean assemble() {
		if (buffAttrs != null && buffAttrs.length() > 0) {
			attribute = Attribute.valueOf(buffAttrs);
		}

		if (MAX_ID < id) {
			MAX_ID = id;
		}

		return true;
	}

	public int getId() {
		return id;
	}

	public int getBuffPrice() {
		return buffPrice;
	}

	public int getBuffWeight() {
		return buffWeight;
	}

	public String getBuffAttrs() {
		return buffAttrs;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public static int getMAX_ID() {
		return MAX_ID;
	}

	public static void setMAX_ID(int mAX_ID) {
		MAX_ID = mAX_ID;
	}

	public int getAddRoleType() {
		return addRoleType;
	}

}
