package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.attribute.Attribute;

@ConfigManager.XmlResource(file = "xml/worldBossFailing.xml", struct = "map")
public class WorldBossFailingCfg extends ConfigBase {

	@Id
	private final int id;

	/**
	 * 开始血量
	 */
	private final int startHp;

	/**
	 * 结束血量
	 */
	private final int endHp;

	/**
	 * 持续时间
	 */
	private final long time;

	/**
	 * buff 出现次数
	 */
	private final int count;

	/**
	 * 出现buff间隔时间
	 */
	private final long intervalTime;

	/**
	 * buff 属性
	 */
	public Attribute attribute;

	public WorldBossFailingCfg() {
		this.id = 0;
		this.time = 0;
		this.startHp = 0;
		this.endHp = 0;
		this.count = 0;
		this.intervalTime = 0;
	}

	@Override
	protected boolean assemble() {
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

	public int getStartHp() {
		return startHp;
	}

	public int getEndHp() {
		return endHp;
	}

	public long getTime() {
		return time;
	}

	public int getCount() {
		return count;
	}

	public long getIntervalTime() {
		return intervalTime;
	}

}
