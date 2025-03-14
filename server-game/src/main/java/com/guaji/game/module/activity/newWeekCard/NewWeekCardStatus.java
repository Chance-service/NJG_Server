package com.guaji.game.module.activity.newWeekCard;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.NewWeekCardCfg;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.util.PlayerUtil;

public class NewWeekCardStatus {
	
	private static final String LAST_TAKE_REWARD_TIME = "lastRewadTime";
	private static final String START_DATE = "startDate";


	
	private Map<Integer, HashMap<String, Date>> cardInfo;
		
	public NewWeekCardStatus() 
	{
		cardInfo = new HashMap<Integer,HashMap<String,Date>>();

	}
	
	/**
	 * 获取上次领取奖励时间
	 * @return
	 */
	public Date getLastRewadTime(int id) {
		if(!cardInfo.containsKey(id))
		{
			return null;
		}
		
		if(!cardInfo.get(id).containsKey(LAST_TAKE_REWARD_TIME))
		{
			return null;
		}
		
		return (Date) cardInfo.get(id).get(LAST_TAKE_REWARD_TIME);
	}

	/**
	 * 设置上次领取时间
	 * @param lastRewadTime
	 */
	public boolean setLastRewadTime(int id,Date lastRewadTime) {
		
		if(!cardInfo.containsKey(id))
		{
			return false;
		}
		
		cardInfo.get(id).put(LAST_TAKE_REWARD_TIME, lastRewadTime);
		return true;

	}
	
	/**
	 * 今天的月卡奖励是否领取
	 * @return
	 */
	public boolean isRewardToday(int id){
		
		if(!cardInfo.containsKey(id))
		{
			return false;
		}
		
		if(cardInfo.get(id).get(LAST_TAKE_REWARD_TIME) == null) {
			return false;
		}
		return GuaJiTime.isToday(cardInfo.get(id).get(LAST_TAKE_REWARD_TIME));
	}
	

	
	/**
	 * 指定周卡是否激活（购买）
	 * @return
	 */
	public boolean checkNewWeekCardActivate(int id) 
	{
		if(cardInfo.containsKey(id))
		{
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * 当前月卡剩余天数
	 * @return
	 */
	public int getLeftDays(int id) 
	{
		if(!cardInfo.containsKey(id))
		{
			return -1;
		}

		if(!cardInfo.get(id).containsKey(START_DATE))
		{
			return -1;
		}
		
		NewWeekCardCfg newWeekCardCfg = ConfigManager.getInstance().getConfigByKey(NewWeekCardCfg.class, id);
		int result = newWeekCardCfg.getDays() - GuaJiTime.calcBetweenDays(GuaJiTime.getCalendar().getTime(),(Date)cardInfo.get(id).get(START_DATE));
		return result <= 0 ? 0 : result;
	}

	
	public Date getStartDate(int id) {
		return  cardInfo.get(id).get(START_DATE);
	}

	public void setStartDate(int id,Date startDate) {
		HashMap<String, Date> info = new HashMap<String,Date>();
		info.put(START_DATE, startDate);
		cardInfo.put(id, info);
	}
	
	
	/**
	 * 激活周卡
	 * @param id
	 * @return
	 */
	public boolean activateNewWeekCard(int id, Player player)
	{
		if(!cardInfo.containsKey(id))
		{
			this.setStartDate(id,new Date(GuaJiTime.getMillisecond()));
			// 刷新周卡经验加成
			if(player != null){
				PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), player.getPlayerData().getMainRole());
				player.getPlayerData().syncRoleInfo(player.getPlayerData().getMainRole().getId());
			}
			return true;
		}
		return false;
	}
	
	/**
	 * 领取周卡
	 */
	public boolean getNewWeekCardAward(int id)
	{
		if(!cardInfo.containsKey(id))
		{
			return false;
		}
		
		if(isRewardToday(id))
		{
			return false;
		}	
		if (!setLastRewadTime(id,new Date(GuaJiTime.getMillisecond())))
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * 是否红点提示，已经激活了某个周卡，且奖励没有领取
	 */
	public boolean showRedPoint()
	{
		Iterator<Entry<Integer, HashMap<String, Date>>> it = cardInfo.entrySet().iterator();
		
		boolean flag = false;
		
		while(it.hasNext())
		{
			int id = it.next().getKey();
			
			if(!isRewardToday(id))
			{
				flag = true;
				break;
			}

		}
		return flag;
	}
	
	
	
		
	/**
	 * 周卡到期处理
	 */
	
	public boolean refreshNewWeekCard(PlayerData playerData)
	{
		Iterator<Entry<Integer, HashMap<String, Date>>> it = cardInfo.entrySet().iterator();
		
		boolean flag = false;
		
		while(it.hasNext())
		{
			int id = it.next().getKey();
			
			if(getLeftDays(id) == 0)
			{
				it.remove();
				// 刷新周卡经验加成
				PlayerUtil.refreshOnlineAttribute(playerData, playerData.getMainRole());
				playerData.syncRoleInfo(playerData.getMainRole().getId());
				flag = true;
			}
		}
		return flag;

	}

	public Map<Integer, HashMap<String, Date>> getCardInfo() {
		return cardInfo;
	}

	public void setCardInfo(Map<Integer, HashMap<String, Date>> cardInfo) {
		this.cardInfo = cardInfo;
	}
	
	


}
