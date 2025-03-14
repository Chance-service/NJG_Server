package com.guaji.game.module.activity.consume;

import java.util.HashSet;
import java.util.Set;

public class AccConsumeStatus {
	// 活动期间累计充值钻石数
	private int accConsumeGold;
	// 该期活动已领取的奖励CfgId
	private Set<Integer> gotAwardCfgIds;

	public AccConsumeStatus() {
		accConsumeGold = 0;
		gotAwardCfgIds = new HashSet<Integer>();
	}

	public int getAccConsumeGold() {
		return accConsumeGold;
	}

	public void setAccConsumeGold(int accConsumeGold) {
		this.accConsumeGold = accConsumeGold;
	}

	public Set<Integer> getGotAwardCfgIds() {
		return gotAwardCfgIds;
	}

	public void setGotAwardCfgIds(Set<Integer> gotAwardCfgIds) {
		this.gotAwardCfgIds = gotAwardCfgIds;
	}

	/**
	 * 增加消费数额
	 */
	public void addAccConsumeAmount(int amount) {
		accConsumeGold += amount;
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
