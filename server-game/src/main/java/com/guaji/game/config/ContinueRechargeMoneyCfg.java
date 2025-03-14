package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年4月8日 下午4:13:15 类说明
 */
@ConfigManager.XmlResource(file = "xml/continueRechargeMoney.xml", struct = "map")
public class ContinueRechargeMoneyCfg extends ConfigBase {

	/**
	 * @Fields id :配置id
	 */
	@Id
	private final int id;

	/**
	 * @Fields nTotalMoney :累计充值金额 (单位金额)
	 */
	private final int nTotalMoney;

	/**
	 * @Fields awards :对应所有的奖励
	 */
	private final String awards;

	public ContinueRechargeMoneyCfg() {
		this.id = 0;
		this.nTotalMoney = 0;
		this.awards = "";
	}

	public int getId() {
		return id;
	}

	public int getnTotalMoney() {
		return nTotalMoney;
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
