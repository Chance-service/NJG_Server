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
@ConfigManager.XmlResource(file = "xml/releaseURDrop154.xml", struct = "map")
public class ReleaseURDropCfg154 extends ConfigBase {
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
	 * Map<獎池ID, List<所有權重>>
	 */
	private static List<Integer> weightList ;
	
	/**
	 * Map<獎池ID, List<索引ID>>
	 */
	private static List<Integer> idList;
	

	public ReleaseURDropCfg154() {
		id = 0;
		rewards = "";
		drawRate = 0;
		weightList = new ArrayList<Integer>();
		idList =   new ArrayList<Integer>();

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
	}

	@Override
	protected boolean assemble() {
		// 特殊次数掉落
		
		weightList.add(this.getDrawRate());
		
		idList.add(this.getId());
		
		if (weightList.size() != idList.size()) {
			Log.errPrintln("activity154DropCfg reward size error");
			return false;
		}
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}
	
	public static ReleaseURDropCfg154 RandomReward() {
		ReleaseURDropCfg154 acfg = null;
		int rid = GuaJiRand.randonWeightObject(idList,weightList);	
		acfg = ConfigManager.getInstance().getConfigByKey(ReleaseURDropCfg154.class,rid);
		return acfg;
	}
	
	public static List<ItemInfo> getDropItem(int dropId) {
		List<ItemInfo> items = new ArrayList<ItemInfo>();
		try {
			ReleaseURDropCfg154 dropCfg = ConfigManager.getInstance().getConfigByKey(ReleaseURDropCfg154.class, dropId);
			AwardItems awards = AwardItems.valueOf(dropCfg.getRewards());
			items.addAll(awards.getAwardItemInfos());
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return items;
	}
}
