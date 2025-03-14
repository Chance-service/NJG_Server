package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.log.Log;
import org.guaji.os.GuaJiRand;
import org.guaji.os.MyException;

import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;

/**
 * 新神将投放奖池
 */
@ConfigManager.XmlResource(file = "xml/releaseURDrop166.xml", struct = "map")
public class ReleaseURDropCfg166 extends ConfigBase {
	/**
	 * 掉落物组ID
	 */
	@Id
	private final int id;
	/**
	 * 奖励物品
	 */
	private final String rewards;
	/**
	 * 权重
	 */
	private final int drawRate;
	/**
	 * 
	 */
	private static List<Integer> drawRateList =  new ArrayList<Integer>();
	/**
	 * Map<獎池ID, List<索引ID>>
	 */
	private static List<Integer>idList =  new ArrayList<Integer>();
	

	public ReleaseURDropCfg166() {
		id = 0;
		rewards = "";
		drawRate = 0;
		drawRateList = new ArrayList<Integer>();
		idList = new ArrayList<Integer>();
	}

	public int getId() {
		return id;
	}


	public String getRewards() {
		return rewards;
	}

	public int getDrawRate() {
		return drawRate;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
		drawRateList.clear();
		idList.clear();
	}

	@Override
	protected boolean assemble() {
		// 特殊次数掉落
	

		drawRateList.add(this.getDrawRate());
		
		idList.add(this.getId());
		
		if (drawRateList.size() != idList.size()) {
			Log.errPrintln("activity166DropCfg reward size error");
			return false;
		}
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}
	
	public static ReleaseURDropCfg166 RandomReward() {
		
		ReleaseURDropCfg166 acfg = null;
		
	    int rid = GuaJiRand.randonWeightObject(idList,drawRateList);	
		
		acfg = ConfigManager.getInstance().getConfigByKey(ReleaseURDropCfg166.class,rid);
		
		return acfg;
	}
	
	public static List<ItemInfo> getDropItem(int dropId) {
		List<ItemInfo> items = new ArrayList<ItemInfo>();
		try {
			ReleaseURDropCfg166 dropCfg = ConfigManager.getInstance().getConfigByKey(ReleaseURDropCfg166.class, dropId);
			AwardItems awards = AwardItems.valueOf(dropCfg.getRewards());
			items.addAll(awards.getAwardItemInfos());
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return items;
	}
}
