package com.guaji.game.config;
/**
* @author 作者 E-mail:Jeremy@163.com
* @version 创建时间：May 6, 2019 2:29:32 PM
* 类说明
*/

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/ActDailyPointReward.xml", struct = "map")
public class ActiveComplianceCfg extends ConfigBase {

	/**
	 * 配置id
	 */
	@Id
	private final int id;

	/**
	 * 奖励
	 */
	private final String award;

	public ActiveComplianceCfg() {
		id = 0;
		award = "";
	}

	public int getId() {
		return id;
	}

	public String getAward() {
		return award;
	}

	@Override
	protected boolean assemble() {
		return super.assemble();
	}

	@Override
	protected boolean checkValid() {
		return super.checkValid();
	}

	@Override
	public Object getKey() {
		return super.getKey();
	}

}
