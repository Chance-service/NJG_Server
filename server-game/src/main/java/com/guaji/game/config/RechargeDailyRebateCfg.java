package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/rechareDailyRebate.xml", struct = "list")
public class RechargeDailyRebateCfg extends ConfigBase{
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	
	protected final int minRechargeSum;
	
	protected final int maxRechargeSum;
	
	protected final int rebateRate;

	protected final String mailSubject;
	
	protected final String awards;
	
	public RechargeDailyRebateCfg(){
		id = 0;
		minRechargeSum = 0;
		maxRechargeSum=0;
		rebateRate = 0;
		mailSubject= "";
		awards = "";
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the minRechargeSum
	 */
	public int getMinRechargeSum() {
		return minRechargeSum;
	}

	/**
	 * @return the maxRechargeSum
	 */
	public int getMaxRechargeSum() {
		return maxRechargeSum;
	}

	/**
	 * @return the rebateRate
	 */
	public int getRebateRate() {
		return rebateRate;
	}
	
	/**
	 * @return the mailSubject
	 */
	public String getMailSubject() {
		return mailSubject;
	}
	/**
	 * @return the awards
	 */
	public String getAwards() {
		return awards;
	}
	
	
}
