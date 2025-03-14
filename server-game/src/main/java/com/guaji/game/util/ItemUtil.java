package com.guaji.game.util;

import com.guaji.game.item.ItemInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.Const.itemType;

/**
 * Item utils;
 * 
 * @author crazyjohn
 *
 */
public class ItemUtil {
	/**
	 * 根据道具Id计算宝石等级
	 * 
	 * @param itemId
	 * @return
	 */
	public static int calcGemLevel(int itemId, int itemType) {
		if (isGem(itemType)) {
			int gemLevel = itemId % 100;
			return gemLevel;
		}
		return -1;
	}

	/**
	 * 根据道具Id判断是否是宝石
	 * 
	 * @param itemId
	 * @return
	 */
	public static boolean isGem(int itemType) {
		if (itemType == Const.toolType.GEM_VALUE) {
			return true;
		}
		return false;
	}
	
	/**
	 * 取chanageType
	 * @param itemInfo
	 * @return
	 */
	public static int getChangeType(ItemInfo itemInfo) {
		int ret = 0;
		if(itemInfo.getType() / GsConst.ITEM_TYPE_BASE == itemType.PLAYER_ATTR_VALUE) {
			switch (itemInfo.getItemId()) {
				case Const.playerAttr.GOLD_VALUE:
					ret = 2;
					break;
				case Const.playerAttr.COIN_VALUE:	
					ret = 1;
					break;
				default:
					break;
			}
		} else if (itemInfo.getType() / GsConst.ITEM_TYPE_BASE == itemType.TOOL_VALUE){
			ret = changeType.CHANGE_TOOLS_VALUE;
		}
		return ret;
	}
}
