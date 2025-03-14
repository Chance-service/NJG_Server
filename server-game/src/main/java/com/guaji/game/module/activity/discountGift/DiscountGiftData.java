package com.guaji.game.module.activity.discountGift;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.guaji.config.ConfigManager;
import org.guaji.os.MyException;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.DiscountGiftCfg;
import com.guaji.game.config.FunctionUnlockCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.protocol.Activity2.DiscountInfo;
import com.guaji.game.protocol.Activity2.HPDiscountInfoRet;
import com.guaji.game.protocol.Const;
import com.guaji.game.util.ActivityUtil;

/**
 * 折扣礼包数据
 *
 */
public class DiscountGiftData {
	/**
	 * 礼包数据,key:goodsId,value:一条礼包数据
	 */
	private Map<Integer, OneDiscountGiftData> data = new HashMap<Integer, OneDiscountGiftData>();
	
	/**
	 * 显示红点
	 */
	private boolean shouldShowPoint = true;

	public DiscountGiftData() {
		shouldShowPoint = true;
	}

	public DiscountGiftData(PlayerData playerData) {
		refresh(playerData,playerData.getPlayerEntity().getLevel());
		shouldShowPoint = true;
	}

	/**
	 * 购买一个礼包
	 */
	public boolean buyGift(PlayerData playerData ,int goodsId) {
		OneDiscountGiftData oneData = data.get(goodsId);
		if (oneData == null) {
			oneData = new OneDiscountGiftData();
		}
		if (oneData.getStatus() != 1) {
			return false;
		}
		DiscountGiftCfg cfg = ConfigManager.getInstance().getConfigByKey(DiscountGiftCfg.class, goodsId);
		try {
			if (cfg.getFree() == 1) {
				return false;
			}
			if (cfg.getLimitNum() > 0 && oneData.getBuyTimes() >= cfg.getLimitNum())
				return false;
			
			int countdownTime = cfg.getCountdownTime();
			
			if (cfg.getActivityId() == Const.ActivityId.ACTIVITY150_LIMIT_GIFT_VALUE) {
				countdownTime = playerData.getStateEntity().calcNewbieSurplusTime();
			}
			if (countdownTime <= 0)
				return false;
		} catch (Exception e) {
			MyException.catchException(e);
		}
		oneData.setBuyTimes(oneData.getBuyTimes() + 1);
		oneData.setStatus(2);
		return true;
	}

	/**
	 * 重置购买次数
	 * 
	 * @param lastTime
	 *            : 上次重置时间
	 */
	public void resetBuyTimes(Date lastDate) {
		// 和上次重置时间是否是同一周
		boolean sameWeek = false;
		// 和上次重置时间是否是同一月
		boolean sameMonth = false;
		if (lastDate != null) {
			Calendar lastCalendar = Calendar.getInstance();
			lastCalendar.setTime(lastDate);
			sameWeek = GuaJiTime.getFirstDayOfWeek(lastCalendar).equals(GuaJiTime.getFirstDayOfWeek(Calendar.getInstance())) ? true : false;
			sameMonth = GuaJiTime.isSameMonth(lastCalendar,Calendar.getInstance());
		}
		boolean refresh = false;
		for (Entry<Integer, OneDiscountGiftData> oneData : data.entrySet()) {
			DiscountGiftCfg cfg = ConfigManager.getInstance().getConfigByKey(DiscountGiftCfg.class, oneData.getKey());
			refresh = false;
			if (cfg != null) {
				if (cfg.getLimitType() == 1) {
					// 每日刷新
					refresh = true;
				} else if (cfg.getLimitType() == 2) {
					// 每周刷新
					refresh = (!sameWeek);
				} else if (cfg.getLimitType() == 4) {
					// 每月刷新
					refresh = (!sameMonth);
				}
				
				if (refresh) {
					oneData.getValue().setBuyTimes(0);
					if (oneData.getValue().getStatus() == 0) { // 不可購買不可領
						if (cfg.getFree() == 0) {
							oneData.getValue().setStatus(1); // 可購買
						} else {
							oneData.getValue().setStatus(2); // 可領取
						}
					}
				}
				
			}
		}
	}

	public HPDiscountInfoRet.Builder toBuilder(PlayerData playerData,int activityId) {
		HPDiscountInfoRet.Builder builder = HPDiscountInfoRet.newBuilder();
		for (Entry<Integer, OneDiscountGiftData> oneData : data.entrySet()) {
			DiscountInfo.Builder b = DiscountInfo.newBuilder();
			b.setGoodsId(oneData.getKey());
			b.setBuyTimes(oneData.getValue().getBuyTimes());
			b.setStatus(oneData.getValue().getStatus());
			DiscountGiftCfg cfg = ConfigManager.getInstance().getConfigByKey(DiscountGiftCfg.class, oneData.getKey());
			if ((cfg == null)||(cfg.getActivityId() != activityId)) {
				continue;
			}
			try {
				int countdownTime = cfg.getCountdownTime();
				if (cfg.getActivityId() == Const.ActivityId.ACTIVITY150_LIMIT_GIFT_VALUE) {
					countdownTime = playerData.getStateEntity().calcNewbieSurplusTime();
				}
				
				b.setCountdownTime(countdownTime);

				int refreshTime = 0;
				if (cfg.getLimitType() == 1) {
					refreshTime = ActivityUtil.calcDayRefreshTime();
				}
				if (cfg.getLimitType() == 2) {
					refreshTime = ActivityUtil.calcWeekRefreshTime();
				}
				if (cfg.getLimitType() == 4) {
					refreshTime = ActivityUtil.calcMonthRefreshTime();
				}
				b.setRefreshTime(refreshTime);
				//打包
				builder.addInfo(b);
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return builder;
	}

	public OneDiscountGiftData getOneDiscountGiftData(int goodsId) {
		return data.get(goodsId);
	}

	/** 刷新礼包 */
	public void refresh(PlayerData playerData,int level) {
		Map<Object, DiscountGiftCfg> cfgs = ConfigManager.getInstance().getConfigMap(DiscountGiftCfg.class);
		int surplusTime = 0;
		for (DiscountGiftCfg cfg : cfgs.values()) {
			surplusTime = cfg.getCountdownTime();
			
			if (cfg.getActivityId() == Const.ActivityId.ACTIVITY150_LIMIT_GIFT_VALUE) {
				surplusTime = playerData.getStateEntity().calcNewbieSurplusTime();
				if ((surplusTime == -1)&&(FunctionUnlockCfg.checkUnlock(playerData.getPlayer(),null,Const.FunctionType.Newbie_Unlock))) {
					playerData.getStateEntity().setNewbieDate(GuaJiTime.getCalendar().getTime());
					surplusTime = playerData.getStateEntity().calcNewbieSurplusTime();
				}
			}
			
			
			
			if (!data.containsKey(cfg.getId()) && level >= cfg.getMinLevel() && level <= cfg.getMaxLevel() 
					&&  surplusTime > 0) {
				OneDiscountGiftData oneData = new OneDiscountGiftData();
				if (cfg.getFree() == 0) {
					oneData.setStatus(1);
				} else {
					oneData.setStatus(2);
				}
				data.put(cfg.getId(), oneData);
			}
		}
		
		Set<Integer> fixSet = new HashSet<>();
		
		for (Map.Entry<Integer,OneDiscountGiftData> entry :data.entrySet()) {
			DiscountGiftCfg giftCfg = ConfigManager.getInstance().getConfigByKey(DiscountGiftCfg.class, entry.getKey());
			if (giftCfg == null) {  // 收集沒有Cfg的禮包
				fixSet.add(entry.getKey());
			} else { // fix data
				if ((giftCfg.getFree() == 1) && (entry.getValue().getStatus() == 1)) {
					entry.getValue().setStatus(2); // 免費禮物為購買狀態時,改為可領取
				}

			}
		}
		// 刪除沒有設定檔禮包
		for (Integer cfgId : fixSet) {
			if (data.containsKey(cfgId)) {
				data.remove(cfgId);
			}
		}
		
		
	}

	public boolean shouldShowPoint() {
		return shouldShowPoint;
	}

	public void setShouldShowPoint(boolean shouldShowPoint) {
		this.shouldShowPoint = shouldShowPoint;
	}
}
