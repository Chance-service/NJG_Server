package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年4月8日 下午4:13:15 类说明
 */
@ConfigManager.XmlResource(file = "xml/DailyRecharge159.xml", struct = "map")
public class DailyRecharge159Cfg extends ConfigBase {

	/**
	 * @Fields id :配置id
	 */
	@Id
	private final int ID;

	/**
	 * @Fields consume :累计VIP點數
	 */
	private final int consume;

	/**
	 * @Fields awards :对应所有的奖励
	 */
	private final String reward;

	public DailyRecharge159Cfg() {
		this.ID = 0;
		this.consume = 0;
		this.reward = "";
	}

	public int getId() {
		return ID;
	}

	public int getConsume() {
		return consume;
	}

	public String getReward() {
		return reward;
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
