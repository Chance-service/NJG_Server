package com.guaji.game.module.activity.foreverCard;

import java.util.Date;
import java.util.HashMap;

import org.guaji.os.GuaJiTime;

import com.guaji.game.GsConfig;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.util.GsConst;

/**
 * 终身卡活动状态;
 * 
 * @author qianhang
 *
 */
public class ForeverCardStatus {

	/** 玩家终身卡的状态 */
	private volatile int cardStatus;

	/** 每天已充值的数额 */
	private HashMap<String, Integer> oneDayRecharge;

	/**
	 * 每日已消费的数额
	 */
	private HashMap<String, Integer> oneDayConsume;
	
	public ForeverCardStatus() {
		oneDayRecharge = new HashMap<String, Integer>();
		oneDayConsume = new HashMap<String, Integer>();
		cardStatus = 1;
	}

	/**
	 * 获取今天剩余需要充值数额;
	 * 
	 * @return
	 */
	public int getTodayLastRecharge() {
		int activateNeedGold = SysBasicCfg.getInstance().getForeverCardCanActivateNeedGold();
		int result = activateNeedGold - getTodayRechargeAmount();
		return result >= 0 ? result : 0;
	}
	
	/**
	 * 获取今天剩余需要消费数额;
	 * 
	 * @return
	 */
	public int getTodayLastConsume() {
		int activateNeedGold = SysBasicCfg.getInstance().getActivateForeverCardGold();
		int result = activateNeedGold - getTodayConsumeAmount();
		return result >= 0 ? result : 0;
	}
	
	/**
	 * 累加今天充值数额;
	 * 
	 * @param rechargeAmout
	 */
	public void addTodayRecharge(int rechargeAmout) {
		Date am0Date = GuaJiTime.getAM0Date();
		this.oneDayRecharge.put(am0Date.toString(), rechargeAmout + getTodayRechargeAmount());
	}
	
	/**
	 * 累加今天消费数额;
	 * 
	 * @param consumeAmout
	 */
	public void addAccConsumeGold(int consumeAmout){
		Date am0Date = GuaJiTime.getAM0Date();
		this.oneDayConsume.put(am0Date.toString(), consumeAmout + getTodayConsumeAmount());
	}

	/**
	 * 获取今天已充值数额;
	 * 
	 * @return
	 */
	private int getTodayRechargeAmount() {
		Date am0Date = GuaJiTime.getAM0Date();
		Integer recharge = 0;
		synchronized (this) {
			if (this.oneDayRecharge.get(am0Date.toString()) == null) {
				this.oneDayRecharge.clear();
				this.oneDayRecharge.put(am0Date.toString(), 0);
			}
			recharge = this.oneDayRecharge.get(am0Date.toString());
		}
		return recharge;
	}
	
	/**
	 * 获取今天已消费数额;
	 * 
	 * @return
	 */
	private int getTodayConsumeAmount() {
		Date am0Date = GuaJiTime.getAM0Date();
		Integer consume = 0;
		synchronized (this) {
			if (this.oneDayConsume.get(am0Date.toString()) == null) {
				this.oneDayConsume.clear();
				this.oneDayConsume.put(am0Date.toString(), 0);
			}
			consume = this.oneDayConsume.get(am0Date.toString());
		}
		return consume;
	}

	public int getCardStatus() {
		isChangedCardStatus();
		return cardStatus;
	}

	public void setCardStatus(int i) {
		this.cardStatus = i;
	}

	/**
	 * 检查玩家终身卡状态, 主要用于隔天重置;
	 */
	private void isChangedCardStatus() {
		Date am0Date = GuaJiTime.getAM0Date();
		Integer todayRecharge = oneDayRecharge.get(am0Date.toString());
		Integer todayConsume = oneDayConsume.get(am0Date.toString());
		if (cardStatus > GsConst.ForeverStatus.UNOPEN_ABLE) {
			if (todayRecharge == null) {
				cardStatus = GsConst.ForeverStatus.OPEN_UNDRAW;
				getTodayRechargeAmount();
			}
			return;
		}
		int activateNeedGold = SysBasicCfg.getInstance().getForeverCardCanActivateNeedGold();
		int consumeGold = SysBasicCfg.getInstance().getActivateForeverCardGold();
		
		if (GsConfig.getInstance().getPlatform().equals(SysBasicCfg.getInstance().getPlatformForeverCard().trim())) {
			if (todayRecharge == null || todayConsume == null || todayRecharge < activateNeedGold || todayConsume < consumeGold) {
				cardStatus = GsConst.ForeverStatus.UNOPEN_UNABLE;
			}else if(todayConsume >= consumeGold && todayRecharge >= activateNeedGold){
				cardStatus = GsConst.ForeverStatus.OPEN_UNDRAW;
			}
		}else{
			if (todayRecharge == null || todayRecharge < activateNeedGold) {
				cardStatus = GsConst.ForeverStatus.UNOPEN_UNABLE;
			} else {
				cardStatus = GsConst.ForeverStatus.UNOPEN_ABLE;
			}
		}
	}

	/**
	 * 是否有资格激活终身卡;
	 * 
	 * @return
	 */
	public boolean canActivate() {
		isChangedCardStatus();
		if (cardStatus == GsConst.ForeverStatus.UNOPEN_ABLE) {
			return true;
		}
		return false;
	}

	/**
	 * 是否已领取今日奖励;
	 * 
	 * @return
	 */
	public boolean isDrawAward() {
		isChangedCardStatus();
		if (cardStatus == GsConst.ForeverStatus.OPEN_DRAW) {
			return true;
		}
		return false;
	}

	/**
	 * 是否激活终身卡
	 * 
	 * @return
	 */
	public boolean isOpen() {

		if (cardStatus > GsConst.ForeverStatus.UNOPEN_ABLE) {
			return true;
		}
		return false;
	}

}
