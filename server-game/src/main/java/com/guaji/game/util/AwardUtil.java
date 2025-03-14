package com.guaji.game.util;

import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;

import com.guaji.game.config.AwardGroupCfg;
import com.guaji.game.item.ItemInfo;

public class AwardUtil {
	/**
	 * 掉落组生成物品
	 * 
	 * @param dropId
	 * @return
	 */
	public static ItemInfo randomDrop(int dropId) {
		AwardGroupCfg awardGroupCfg = ConfigManager.getInstance().getConfigByKey(AwardGroupCfg.class, dropId);
		if (awardGroupCfg != null) {
			return GuaJiRand.randonWeightObject(awardGroupCfg.getItemInfos(), awardGroupCfg.getItemWeights()).clone();
		}
		return null;
	}
	/**
	 * 掉落组生成物品
	 * 
	 * @param dropId
	 * @return
	 */
	public static ItemInfo randomDrop(int dropId, int quantity) {
		ItemInfo itemInfo = randomDrop(dropId);
		itemInfo.setQuantity(itemInfo.getQuantity() * quantity);
		return itemInfo;
	}
}
