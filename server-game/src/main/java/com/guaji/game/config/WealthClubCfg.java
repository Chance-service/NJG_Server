package com.guaji.game.config;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;

/**
 * 财富俱乐部功能配置
 */
@ConfigManager.XmlResource(file = "xml/wealthClub.xml", struct = "list")
public class WealthClubCfg extends ConfigBase {

	/**
	 * 结算时间
	 */
	private final String settleTime;

	/**
	 * 返利比公式
	 */
	private final String formula;

	/**
	 * 充值人数小于设定初始数值
	 */
	private final int belowNumber;

	/**
	 * 每小时自动添加充值人数
	 */
	private final int autoAdd;

	/**
	 * 返利金额上限
	 */
	private final int amountLimit;
	
	/**
	 * 邮件奖励
	 */
	private final String reward;
	
	/**
	 * 数字---结算时间
	 */
	private long settleTimeValue;
	

	public WealthClubCfg() {

		this.settleTime = null;
		this.formula = null;
		this.belowNumber = 0;
		this.autoAdd = 0;
		this.amountLimit = 0;
		this.reward = null;
	}

	@Override
	protected boolean assemble() {
		return this.settleTimeLogic();
	}

	@Override
	protected boolean checkValid() {
		return true;
	}
	
	public String getSettleTime() {
		return settleTime;
	}

	public String getFormula() {
		return formula;
	}

	public int getBelowNumber() {
		return belowNumber;
	}

	public int getAutoAdd() {
		return autoAdd;
	}

	public int getAmountLimit() {
		return amountLimit;
	}

	public String getReward() {
		return reward;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

	/**
	 * 结算时间计算
	 * @return
	 */
	public boolean settleTimeLogic() {
		
		// 时间计算
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = null;
		try {
			SimpleDateFormat sd_Format = new SimpleDateFormat("yyyy-MM-dd");
			date = sdFormat.parse(sd_Format.format(GuaJiTime.getMillisecond()) + " " + this.settleTime);
			this.settleTimeValue = date.getTime();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public long getSettleTimeValue() {
		return settleTimeValue;
	}

	public synchronized void updateSettleTimeValue() {
		this.settleTimeValue = this.settleTimeValue + 24 * 60 * 60 * 1000L;;
	}
	
}
