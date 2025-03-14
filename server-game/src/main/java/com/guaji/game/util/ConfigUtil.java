package com.guaji.game.util;

import org.guaji.config.ConfigManager;
import org.guaji.log.Log;

import com.guaji.game.config.AwardGroupCfg;
import com.guaji.game.config.BadgeCfg;
import com.guaji.game.config.ElementCfg;
import com.guaji.game.config.EquipCfg;
import com.guaji.game.config.ItemCfg;
import com.guaji.game.config.MonsterCfg;
import com.guaji.game.config.RoleSkinCfg;
import com.guaji.game.item.DropItems;
import com.guaji.game.protocol.Const;

/**
 * 配置检查
 *
 * @author xulinqs
 */
public class ConfigUtil {

    /**
     * 检测itemType的itemId
     *
     * @param itemType
     * @param itemId
     * @return
     */
    public static boolean check(int itemType, int itemId) {
        itemType = GameUtil.convertToStandardItemType(itemType) / GsConst.ITEM_TYPE_BASE;
        if (itemType == Const.itemType.PLAYER_ATTR_VALUE) {
            return true;
        } else if (itemType == Const.itemType.SKIN_VALUE) {
            if (ConfigManager.getInstance().getConfigByKey(RoleSkinCfg.class, itemId) == null) {
                Log.errPrintln("RoleSkin config not found, itemId: " + itemId);
                return false;
            }
            return true;
        } else if (itemType == Const.itemType.SKILL_VALUE) {
            return true;
        } else if (itemType == Const.itemType.EQUIP_VALUE) {
            if (ConfigManager.getInstance().getConfigByKey(EquipCfg.class, itemId) == null) {
                Log.errPrintln("equip config not found, itemId: " + itemId);
                return false;
            }
            return true;
        } else if (itemType == Const.itemType.TOOL_VALUE) {
            if (ConfigManager.getInstance().getConfigByKey(ItemCfg.class, itemId) == null) {
                Log.errPrintln("item config not found, itemId: " + itemId);
                return false;
            }
            return true;
        } else if (itemType == Const.itemType.ELEMENT_VALUE) {
            if (ConfigManager.getInstance().getConfigByKey(ElementCfg.class, itemId) == null) {
                Log.errPrintln("item config not found, elementId: " + itemId);
                return false;
            }
            return true;
        } else if (itemType == Const.itemType.SOUL_VALUE) {
            return true;
        } else if (itemType == Const.itemType.BADGE_VALUE) {
            if (ConfigManager.getInstance().getConfigByKey(BadgeCfg.class, itemId) == null) {
            	 Log.errPrintln("badge config not found, badgeId: " + itemId);
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 检测掉落组奖励
     *
     * @param awardGroupId
     * @return
     */
    public static boolean checkAwardGroup(int awardGroupId) {
        AwardGroupCfg awardGroupCfg = ConfigManager.getInstance().getConfigByKey(AwardGroupCfg.class, awardGroupId);
        if (awardGroupCfg == null) {
            Log.errPrintln("award group drop config not found, groupId：" + awardGroupId);
            return false;
        }
        return true;
    }

    /**
     * 检测掉落物品
     *
     * @param item
     * @return
     */
    public static boolean checkDropItem(DropItems.Item item) {
        if (item.getId() == 0) {
            return checkAwardGroup(item.getType());
        }

        return check(item.getType(), item.getId());
    }

    /**
     * 怪物检测
     *
     * @param monsterId
     * @return
     */
    public static boolean checkMonster(int monsterId) {
        MonsterCfg monsterCfg = ConfigManager.getInstance().getConfigByKey(MonsterCfg.class, monsterId);
        if (monsterCfg == null) {
            Log.errPrintln("monster config not found, monsterId：" + monsterId);
            return false;
        }
        return true;
    }
}
