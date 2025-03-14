package com.guaji.game.module.activity.consumMonthCard;

import java.util.Date;

import org.guaji.config.ConfigManager;
import org.guaji.log.Log;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.ConsumeMonthCardCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.util.TapDBUtil.TapDBSource;

public class ConMonthCardStatus {
	/**
	 * 月卡激活状态
	 */
	private boolean activateFlag;

	/**
	 * 上次领奖时间
	 */
	private Date lastRewadTime;

//	/**
//	 * 额外次数自动领取时间
//	 */
//	private Date lastTimesAwardTime;

	/**
	 * 月卡开始时间
	 */
	private Date startDate;

	

	/**
	 * 月卡ID
	 */
	private int monthCardId;

//	/**
//	 * 剩余免费战斗次数
//	 */
//	private int leftFreeFastFightTimes;
//
//	/**
//	 * 剩余免费商店刷新次数
//	 */
//	private int leftFreeRefreshShopTimes;
//
//	/**
//	 * 剩余免费打造装备刷新次数
//	 */
//	private int leftFreeRefreshMakeEquipTimes;

	public ConMonthCardStatus() {
		activateFlag = false;
		lastRewadTime = null;
		startDate = null;
//		this.lastTimesAwardTime = null;
//		leftFreeFastFightTimes = 0;
//		leftFreeRefreshShopTimes = 0;
//		leftFreeRefreshMakeEquipTimes = 0;

		monthCardId = 0;
	}

//	/**
//	 * 获取剩余免费战斗次数
//	 */
//	public int getLeftFreeFastFightTimes() {
//		return leftFreeFastFightTimes;
//	}

//	/**
//	 * 消耗剩余免费快速战斗次数
//	 */
//	public boolean expendLeftFreeFastFightTimes(int times) {
//		if (leftFreeFastFightTimes >= times) {
//			leftFreeFastFightTimes -= times;
//			return true;
//		} else {
//			return false;
//		}
//	}

//	/**
//	 * 获取剩余免费商店刷新次数
//	 */
//	public int getLeftFreeRefreshShopTimes() {
//		return leftFreeRefreshShopTimes;
//	}
//
//	/**
//	 * 消耗剩余免费商店刷新次数
//	 */
//	public boolean expendLeftFreeRefreshShopTimes(int times) {
//		if (leftFreeRefreshShopTimes >= times) {
//			leftFreeRefreshShopTimes -= times;
//			return true;
//		} else {
//			return false;
//		}
//	}

//	/**
//	 * 获取剩余免费打造装备刷新次数
//	 */
//	public int getLeftFreeRefreshMakeEquipTimes() {
//		return leftFreeRefreshMakeEquipTimes;
//	}
//
//	/**
//	 * 消耗剩余免费打造装备刷新次数
//	 */
//	public boolean expendLeftFreeRefreshMakeEquipTimes(int times) {
//		if (leftFreeRefreshMakeEquipTimes >= times) {
//			leftFreeRefreshMakeEquipTimes -= times;
//			return true;
//		} else {
//			return false;
//		}
//	}

	/**
	 * 获取上次领取奖励时间
	 *
	 * @return
	 */
	public Date getLastRewadTime() {
		return lastRewadTime;
	}

	/**
	 * 获取上次领取时间
	 *
	 * @param lastRewadTime
	 */
	public void setLastRewadTime(Date lastRewadTime) {
		this.lastRewadTime = lastRewadTime;
	}

	/**
	 * 今天的月卡奖励是否领取
	 *
	 * @return
	 */
	public boolean isRewardToday() {
		if (lastRewadTime == null) {
			return false;
		}
		return GuaJiTime.isToday(lastRewadTime);
	}

	/**
	 * 是否红点提示，已经激活了月卡，且奖励没有领取
	 */
	public boolean showRedPoint() {
		if (activateFlag == false) {
			return false;
		}

		if (isRewardToday()) {
			return false;
		}

		return true;
	}

	/**
	 * 今天额外次数是否领取
	 */
//	private boolean isTimesAwardToday() {
//		if (lastTimesAwardTime == null) {
//			return false;
//		}
//
//		return GuaJiTime.isToday(lastTimesAwardTime);
//	}

	/**
	 * 设置今天额外次数领取时间
	 */
//	private void setLastTimesAwardTime(Date time) {
//		lastTimesAwardTime = time;
//		return;
//	}

	/**
	 * 获得当前激活的月卡配置Id
	 *
	 * @return
	 */
	public int getCurrentActiveCfgId() {
		return monthCardId;
	}

	/**
	 * 当前月卡剩余天数
	 *
	 * @return
	 */
	public int getLeftDays() {
		if (monthCardId == 0) {
			return -1;
		}

		ConsumeMonthCardCfg monthCardCfg = ConfigManager.getInstance().getConfigByKey(ConsumeMonthCardCfg.class, monthCardId);
		int result = monthCardCfg.getDays() - GuaJiTime.calcBetweenDays(GuaJiTime.getCalendar().getTime(), startDate);
		return result <= 0 ? 0 : result;

	}

	/**
	 * 月卡设定
	 */
	public void setMonthCardId(int id) {
		monthCardId = id;
		return;
	}

	/**
	 * 获取月卡id
	 */
	public int getMonthCardId() {
		return monthCardId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}



	/**
	 * 激活月卡
	 *
	 * @param id
	 * @return
	 */
	public boolean activateMonthCard(int id, Player player) {
		if (this.activateFlag == false) {
			this.activateFlag = true;

			monthCardId = id;

			//resetMonthCartTimesAward();

			//setLastTimesAwardTime(new Date(GuaJiTime.getMillisecond()));

			this.setStartDate(new Date(GuaJiTime.getMillisecond()));

			// 月卡生效
			if (player != null) {
				ConsumeMonthCardCfg cfg = ConfigManager.getInstance().getConfigByKey(ConsumeMonthCardCfg.class, 73);// 1为唯一月卡标识，不想定义宏了
				if (cfg != null) {
					AwardItems awardItems = AwardItems.valueOf(cfg.getBuyReward());
					awardItems.rewardTakeAffectAndPush(player, Action.CONSUME_MONTH_CARD_REWARD, 2,TapDBSource.Recharge,Params.valueOf("goodsId", id));// 记录领取日志
					player.getPlayerData().setLastRecharage(cfg.getBuyReward());
				}
				PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), player.getPlayerData().getMainRole());
				player.getPlayerData().syncRoleInfo(player.getPlayerData().getMainRole().getId());
			}

			return true;
		}
		Log.errPrintln("月卡激活后不能继续激活！");
		return false;
	}

	/**
	
	
	/**
	 * 领取月卡奖励
	 */
	public boolean getMonthCardAward() {
		if (!activateFlag) {
			return false;
		}

		if (isRewardToday()) {
			return false;
		}
		setLastRewadTime(new Date(GuaJiTime.getMillisecond()));

		return true;
	}

//	/**
//	 * 自动重置月卡额外次数奖励
//	 */
//	private void resetMonthCartTimesAward() {
//		if (monthCardId == 0) {
//			return;
//		}
//
//		ConsumeMonthCardCfg cfg = ConfigManager.getInstance().getConfigByKey(ConsumeMonthCardCfg.class, monthCardId);
//
//		leftFreeFastFightTimes = cfg.getFreeFastFightTimes();
//		leftFreeRefreshShopTimes = cfg.getFreeRefreshShopTimes();
//		leftFreeRefreshMakeEquipTimes = cfg.getFreeRefreshMakeEquipTimes();
//		return;
//	}

//	/**
//	 * 失去月卡额外次数奖励
//	 */
//	private void lostMonthCartTimesAward() {
//		leftFreeFastFightTimes = 0;
//		leftFreeRefreshShopTimes = 0;
//		leftFreeRefreshMakeEquipTimes = 0;
//		return;
//	}

	/**
	 * 月卡到期处理
	 */

	public void monthCardOver(PlayerData playerData) {
		this.setMonthCardId(0);
		this.activateFlag = false;
		this.startDate = null;
		this.lastRewadTime = null;
		//this.lastTimesAwardTime = null;

		// 月卡属性过期
		PlayerUtil.refreshOnlineAttribute(playerData, playerData.getMainRole());
		playerData.syncRoleInfo(playerData.getMainRole().getId());

		// 失去额外次数奖励
		//lostMonthCartTimesAward();
		return;
	}

	/**
	 * 刷新月卡状态(上线,打开界面，定时器检测,返回值为改变与否)
	 */
	public boolean refresh(PlayerData playerData) {
		// 未激活
		if (!this.activateFlag) {
			return false;
		}

		// 激活，检查过期

		if (getLeftDays() == 0) {
			monthCardOver(playerData);
			return true;
		}

//		if (!isTimesAwardToday())// 新的一天
//		{
//			resetMonthCartTimesAward();
//			setLastTimesAwardTime(new Date(GuaJiTime.getMillisecond()));
//			return true;
//
//		}

		return false;
	}

	public boolean isActivateFlag() {
		return activateFlag;
	}

	public void setActivateFlag(boolean activateFlag) {
		this.activateFlag = activateFlag;
	}
}
