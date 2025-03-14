package com.guaji.game.manager.shop.strategy;

import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.module.activity.consumeitem.ConsumeItem;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.GemShopCfg;
import com.guaji.game.entity.ItemEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.manager.shop.ShopScervice;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.GemShopBuy;
import com.guaji.game.protocol.Const.ShopType;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Shop.BuyShopItemsRequest;
import com.guaji.game.protocol.Shop.BuyShopItemsResponse;
import com.guaji.game.protocol.Status;

/**
 * 宝石商店
 *
 * @author zdz
 */
public class GemMarkey extends ShopScervice<GemShopCfg> {
    /**
     * 宝石商店
     */
    private static final GemMarkey instance = new GemMarkey();

    public static GemMarkey getInstance() {
        return instance;
    }

    @Override
    public void shopBuyLogic(Player player, BuyShopItemsRequest request) {

        int buyShopId = request.getId();
        int number = request.getAmount();
        int buyType = request.getBuyType();

        GemShopCfg gemShopCfg = ConfigManager.getInstance().getConfigByKey(GemShopCfg.class, buyShopId);

        if (gemShopCfg == null) {
            // 商品不存在
            player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.CONFIG_NOT_FOUND);
            return;
        }

        int buyTypeCfg = gemShopCfg.getGemVolumeBuy();


        if (gemShopCfg.getVIPVisible() > player.getVipLevel()) {
            // vip等级不足
            player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.VIP_NOT_ENOUGH_VALUE);
            return;
        }

        if (buyType == GemShopBuy.GOLDBUY_VALUE) {
            // 消耗钻石
            if (buyTypeCfg != 1 && buyTypeCfg != 3) {
                player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.GOLD_NOT_ENOUGH);

                return;
            }

            if (player.getGold() >= gemShopCfg.getGoldPrice() * number) {
                player.consumeGold(gemShopCfg.getGoldPrice() * number, Action.EQUIP_STONE_SHOP_BUY_GOLD);
                ConsumeItems.valueOf(changeType.CHANGE_GOLD, gemShopCfg.getGoldPrice() * number).pushChange(player);
            } else {
                // 钻石不足
                player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.GOLD_NOT_ENOUGH);
                return;
            }
        } else if (buyType == GemShopBuy.GEMVOLUMEBUY_VALUE) {
            // 单次不能超过最大值
            if (number > SysBasicCfg.getInstance().getShopGemBuyMaxOnce()) {
                player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.SHOP_GEM_BUY_ONCE_MAX);
            }
            // 消耗宝石券
            if (buyTypeCfg != 2 && buyTypeCfg != 3) {
                player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.GEM_VOLUME_NOT_ENOUGH);
                return;
            }
            // 消耗宝石卷
            ItemInfo itemInfo = gemShopCfg.getConsumeMaterialInfo();
            // 消耗金币
            ItemInfo coinInfo = gemShopCfg.getConsumeCoinInfo();
            ItemEntity itemEntity = player.getPlayerData().getItemByItemId(itemInfo.getItemId());
            int count = 0;
            if (null != itemInfo && coinInfo != null) {
                count = (int)itemInfo.getQuantity() * number;
                //消耗宝石卷
                ConsumeItems consumeItems = new ConsumeItems();
                consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), itemEntity.getItemId(), count);
                if (!consumeItems.checkConsume(player) || !consumeItems.consumeTakeAffect(player, Action.EQUIP_STONE_SHOP_BUY_VOLUME)) {
                    player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.GEM_VOLUME_NOT_ENOUGH);
                    return;
                }
                //消耗金币
                int coinCost = (int)coinInfo.getQuantity() * number;
                ConsumeItems consumeItemCoin = new ConsumeItems();
                consumeItemCoin.addChangeInfo(changeType.CHANGE_COIN, coinCost);
                if (!consumeItemCoin.checkConsume(player) || !consumeItemCoin.consumeTakeAffect(player, Action.EQUIP_STONE_SHOP_BUY_VOLUME)) {
                    player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.COINS_NOT_ENOUGH);
                    return;
                }
            } else {
                player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.CONFIG_ERROR);
                return;
            }
        } else {
            // 购买类型错误
            player.sendError(HP.code.SHOP_BUY_S_VALUE, Status.error.CONFIG_NOT_FOUND);
            return;
        }
        // 加入新的宝石
        AwardItems awardItems = new AwardItems();
        awardItems.addItem(Const.itemType.TOOL_VALUE * GsConst.ITEM_TYPE_BASE, gemShopCfg.getGemId(), number);
        awardItems.rewardTakeAffectAndPush(player, Action.EQUIP_STONE_SHOP_BUY, 1);

        // 构建返回数据包
        BuyShopItemsResponse.Builder builder = this.createResponse();
        player.sendProtocol(Protocol.valueOf(HP.code.SHOP_BUY_S_VALUE, builder));
    }

    @Override
    public ShopType getShopType() {
        return Const.ShopType.GEM_MARKET;
    }
}
