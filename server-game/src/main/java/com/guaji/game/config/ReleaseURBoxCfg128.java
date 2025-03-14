package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/releaseURBox128.xml", struct = "map")
public class ReleaseURBoxCfg128 extends ConfigBase {

	/**
	 * 配置id
	 */
	@Id
	private final int id;

	/**
	 * 领奖所需累计积分
	 */
	private final int sum;

	/**
	 * 奖励
	 */
	private final String awards;

	public ReleaseURBoxCfg128() {
		this.id = 0;
		this.sum = 0;
		this.awards = "";
	}

	public int getId() {
		return id;
	}

	public int getSum() {
		return sum;
	}

	public String getAwards() {
		return awards;
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
