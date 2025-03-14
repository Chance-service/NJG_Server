package com.guaji.game.module.activity.recharge;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.ContinueRechargeCfg;
import com.guaji.game.config.ContinueRechargeMoneyCfg;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年4月8日 下午4:02:22 类说明
 */
public class ContinueMoneyRechargeStatus {

	/**
	 * @Fields continueRechargeMoney :周期活动内累计充值日元
	 */
	private int continueRechargeMoney;

	/**
	 * @Fields continueRechargeDays :连续充值天数
	 */
	private int continueRechargeDays;
	/**
	 * @Fields lastRechargeTime : 最后一次充值日期
	 */
	private int lastRechargeTime;
	/**
	 * @Fields gotAwardCfgIds :已经领取的所有奖励配置
	 */
	private Set<Integer> gotAwardCfgIds;

	public ContinueMoneyRechargeStatus() {

		continueRechargeMoney = 0;
		lastRechargeTime = 0;
		gotAwardCfgIds = new HashSet<Integer>();
	}

	public int getContinueRechargeMoney() {
		return continueRechargeMoney;
	}

	public void setContinueRechargeMoney(int continueRechargeMoney) {
		this.continueRechargeMoney = continueRechargeMoney;
	}

	public int getLastRechargeTime() {
		return lastRechargeTime;
	}

	public void setLastRechargeTime(int lastRechargeTime) {
		this.lastRechargeTime = lastRechargeTime;
	}

	public Set<Integer> getGotAwardCfgIds() {
		return gotAwardCfgIds;
	}

	public void setGotAwardCfgIds(Set<Integer> gotAwardCfgIds) {
		this.gotAwardCfgIds = gotAwardCfgIds;
	}

	public int getContinueRechargeDays() {
		return continueRechargeDays;
	}

	public void setContinueRechargeDays(int continueRechargeDays) {
		this.continueRechargeDays = continueRechargeDays;
	}

	/**
	 * 增加连续充值天数
	 */
	public boolean addContinueRechargeDays() {
		int nextDay0Time = (int) (GuaJiTime.getNextAM0Date() / 1000);
		if (nextDay0Time > lastRechargeTime) {
			continueRechargeDays++;
			lastRechargeTime = nextDay0Time;
			return true;
		}
		return false;
	}

	/**
	 * @param money 充值金额
	 * @return
	 */
	public boolean addContinueRechargeMoney(int money) {

		this.continueRechargeMoney += money;
		return true;
	}

	/**
	 * 是否红点提示，已经充值，且奖励没有领取
	 */
	public boolean showRedPoint() {
		Map<Object, ContinueRechargeMoneyCfg> cfgList = ConfigManager.getInstance()
				.getConfigMap(ContinueRechargeMoneyCfg.class);

		for (ContinueRechargeMoneyCfg cfg : cfgList.values()) {
			// 
			if (getContinueRechargeMoney() >= cfg.getnTotalMoney() && !isAlreadyGot(cfg.getId())) {
				// 累计充值金额未满足
				return true;
			}

		}
		return false;
	}

	/**
	 * 对应配置对应的奖励是否已经领取过
	 * 
	 * @param cfgId
	 * @return
	 */
	public boolean isAlreadyGot(int cfgId) {
		if (gotAwardCfgIds.contains((Integer) cfgId)) {
			return true;
		}
		return false;
	}

	/**
	 * 添加领取过的cfgId
	 */
	public void addGotAwardCfgId(int cfgId) {
		gotAwardCfgIds.add(cfgId);
	}

}
