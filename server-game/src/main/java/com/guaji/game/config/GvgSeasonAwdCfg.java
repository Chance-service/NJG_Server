package com.guaji.game.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;

import com.guaji.game.item.AwardItems;


/**
 * GVG城池宝箱
 */
@ConfigManager.XmlResource(file = "xml/gvgSeasonAward.xml", struct = "map")
public class GvgSeasonAwdCfg extends ConfigBase {

	
	/** 
	*赛季标识
	*/ 
	@Id
	private final int id;

	/** 赛季开启时间 */
	private final String beginTime;

	/** 赛季结束时间 */
	private final String endTime;

	/** 赛季期数 */
	private final int stageId;
	/** 赛季开启时间 */
	private long lbeginTime = 0;

	/** 赛季结束时间 */
	private long lendTime = 0;

	/**
	 * 赛季奖励字符串
	 */
	private final String rewards;

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

	/**
	 * 可选择装备列表
	 */
	private Map<Integer, AwardItems> rewardsMap;

	public GvgSeasonAwdCfg() {
		id = 0;
		lbeginTime = 0;
		stageId = 0;
		beginTime = "";
		endTime = "";
		lendTime = 0;
		this.rewardsMap = new HashMap<Integer, AwardItems>();
		rewards = "";

	}

	@Override
	protected boolean assemble() {
		this.rewardsMap.clear();

		if (this.stageId > 0) {
			try {
				this.lbeginTime = DATE_FORMAT.parse(this.beginTime).getTime();
				this.lendTime = DATE_FORMAT.parse(this.endTime).getTime();
			} catch (ParseException e) {
				MyException.catchException(e);
			}
		}

		if (rewards != null && rewards.length() > 0 && !rewards.equals("none") && !rewards.equals("")) {
			String[] items = rewards.split("\\|");

			for (String strItem : items) {

				String[] rankAwardArr = strItem.split("\\:");
				if (rankAwardArr.length < 2)
					return false;
				String rankAward = rankAwardArr[1];
				AwardItems awardItems = AwardItems.valueOf(rankAward);
				if (awardItems == null)
					return false;
				if (this.rewardsMap.containsKey(Integer.parseInt(rankAwardArr[0])))
					return false;
				this.rewardsMap.put(Integer.parseInt(rankAwardArr[0]), awardItems);
			}

			return true;
		} else {
			return false;
		}

	}

	@Override
	protected boolean checkValid() {

		return true;
	}

	public Map<Integer, AwardItems> getRewardsMap() {
		return rewardsMap;
	}

	public void setRewardsMap(Map<Integer, AwardItems> rewardsMap) {
		this.rewardsMap = rewardsMap;
	}

	public int getId() {
		return id;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	

	public String getRewards() {
		return rewards;
	}

	public int getStageId() {
		return stageId;
	}

	public long getLbeginTime() {
		return lbeginTime;
	}

	public void setLbeginTime(long lbeginTime) {
		this.lbeginTime = lbeginTime;
	}

	public long getLendTime() {
		return lendTime;
	}

	public void setLendTime(long lendTime) {
		this.lendTime = lendTime;
	}

	public static GvgSeasonAwdCfg getSeasonAwdCfg() {
		for (GvgSeasonAwdCfg awdCfg : ConfigManager.getInstance().getConfigMap(GvgSeasonAwdCfg.class).values()) {
			long nowTime = GuaJiTime.getMillisecond();
			if (nowTime <= awdCfg.getLendTime() && nowTime >= awdCfg.getLbeginTime()) {
				return awdCfg;
			}

		}
		return null;
	}

}
