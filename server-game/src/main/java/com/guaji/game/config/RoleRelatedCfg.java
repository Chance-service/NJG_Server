package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 佣兵升阶
 * 
 * @author Administrator
 *
 */
@ConfigManager.XmlResource(file = "xml/roleRelated.xml", struct = "map")
public class RoleRelatedCfg extends ConfigBase {
	@Id
	protected final int id;

	/**
	 * 类型(1=等级，2=vip，3=碎片)
	 */
	protected final int costType;

	/**
	 * 限制等级激活
	 */
	protected final int costLevel;

	/**
	 * 超出碎片，自动兑换成宝石
	 */
	protected final String exchange;
	
	/**
	 * 控制跑馬燈是否需要顯示 0 = 不需要 ,1 = 需要跑馬燈
	 */
	protected final int marqueeOn;

	/**
	 * 碎片上限
	 */
	protected final int limitCount;

	public RoleRelatedCfg() {
		id = 0;
		costType = 0;
		costLevel = 0;
		exchange = null;
		limitCount = 0;
		marqueeOn = 0;
	}

	public int getId() {
		return id;
	}

	@Override
	protected boolean assemble() {
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	public int getCostType() {
		return costType;
	}

	public int getCostLevel() {
		return costLevel;
	}

	public String getExchange() {
		return exchange;
	}

	public int getLimitCount() {
		return limitCount;
	}
	
	public boolean isMarqueeOn() {
		return marqueeOn == 1;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

}
