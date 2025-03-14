package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/sign_New.xml", struct = "map")
public class SignNewCfg extends ConfigBase {
	
	/**
	 * 配置id
	 */
	@Id
	private final int rechargeid;
	
	/**
	 * 领奖所需累计充值数额
	 */
	private final int signid;
	/**
	 * 跳出的假物品
	 */
	private final String itemid;

	
	public SignNewCfg() {
		this.rechargeid = 0;
		this.signid = 0;
		this.itemid = "";
	}

	

	public int getRechargeid() {
		return rechargeid;
	}

	public int getSignid() {
		return signid;
	}

	

	public String getItemid() {
		return itemid;
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
