package com.guaji.game.module.activity.expeditionArmory;

import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.protocol.Activity.HPExpeditionArmoryItemUse;
import com.guaji.game.GsApp;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ExpeditionArmoryDonateCfg;
import com.guaji.game.entity.ExpeditionArmoryEntity;
import com.guaji.game.entity.ItemEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.AwardItems.Item;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.manager.ExpeditionArmoryManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class ExpeditionArmoryUseItemHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.EXPEDITION_ARMORY_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if(timeCfg == null){
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE_VALUE);
			return true;
		}
		
		if(timeCfg.isEnd()){
			// 活动已结束
			player.sendError(protocol.getType(), Status.error.ACTIVITY_END_VALUE);
			return true;
		}
		
		ExpeditionArmoryManager activityMan = ExpeditionArmoryManager.getInstance();
		ExpeditionArmoryEntity expeditionArmoryEntity = activityMan.getCurrentActiveExpeditionArmory();
		if(expeditionArmoryEntity == null) {
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		HPExpeditionArmoryItemUse request = protocol.parseProtocol(HPExpeditionArmoryItemUse.getDefaultInstance());
		int cfgId = request.getId();
		int count = request.getCount();
		ExpeditionArmoryDonateCfg expeditionArmoryDonateCfg = ConfigManager.getInstance().getConfigByKey(
				ExpeditionArmoryDonateCfg.class, cfgId);
		if(expeditionArmoryDonateCfg == null || (count != 1 && count != 10)) {
			player.sendError(HP.code.EXPEDITION_ARMORY_INFO_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return true;
		}
		
		int itemId = expeditionArmoryDonateCfg.getDonateItemId();
		ItemEntity itemEntity = player.getPlayerData().getItemByItemId(itemId);
		if(itemEntity == null || itemEntity.getItemCount() < count) {
			player.sendError(HP.code.EXPEDITION_ARMORY_USE_ITEM_C_VALUE, Status.error.ITEM_NOT_ENOUGH_VALUE);
			return true;
		}
		
		// 消耗道具
		ConsumeItems consumeItems = ConsumeItems.valueOf(changeType.CHANGE_TOOLS, itemEntity.getId(), itemId, count);
		consumeItems.consumeTakeAffect(player, Action.EXPEDITION_ITEM_USE);
		
		// 发放宝箱
		AwardItems awardItems = AwardItems.valueOf(expeditionArmoryDonateCfg.getAward());
		List<Item> items = awardItems.getAwardItems();
		for(Item item : items){
			item.setCount(item.getCount() * count);
		}
		awardItems.rewardTakeAffectAndPush(player, Action.EXPEDITION_ITEM_USE,1);
		
		// 推送增加贡献消息
		Msg msg = Msg.valueOf(GsConst.MsgType.EXPEDITION_ARMORY_EXP_ADD);
		msg.pushParam(expeditionArmoryDonateCfg.getAddExp()* count);
		msg.pushParam(player);
		GuaJiXID targetXId = GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.EXPEDITION_ARMORY);
		GsApp.getInstance().postMsg(targetXId, msg);
		return true;
	}

}
