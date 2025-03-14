package com.guaji.game.module.activity.activity148;

import java.util.HashSet;
import java.util.Set;

/**
 * @author 作者 LIN
 * @version 创建时间：2023年5月19日 下午17:26:00 类说明
 */
public class Activity148Status {
//	/**
//	 * 活動開啟時間
//	 */
//	private transient Date startDate;
	/**
	 * 已獲取獎品
	 */
	private Set<Integer> gotAwards;

	public Activity148Status() {
		gotAwards = new HashSet<Integer>();
		//startDate = GuaJiTime.getAM0Date();// 获取当前时间
	}

	public Set<Integer> getGotAwards() {
		return gotAwards;
	}
	
	/**
	 * @param 新增已獲得獎勵Index
	 * @return
	 */
	public boolean addGotAwards(Integer idx) {
		if (this.gotAwards.contains(idx))
			return false;

		this.gotAwards.add(idx);

		return true;
	}
	
}
