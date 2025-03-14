package com.guaji.game.module.activity.chatSkin;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPChatSkinBuy;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ChatSkinCfg;
import com.guaji.game.entity.ChatSkinEntity;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 购买聊天皮肤
 */
public class ChatSkinBuyHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;

		// 检测活动是否开放
		int activityId = Const.ActivityId.CHAT_SKIN_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(HP.code.CHAT_SKIN_BUY_S_VALUE, Status.error.ACTIVITY_CLOSE);
			return false;
		}

		HPChatSkinBuy req = protocol.parseProtocol(HPChatSkinBuy.getDefaultInstance());
		int skinId = req.getSkinId();

		// 聊天皮肤数据
		ChatSkinEntity entity = player.getPlayerData().getChatSkinEntity();

		// 不可购买
		if (!entity.canBuy(skinId, timeCfg.getStageId())) {
			return false;
		}

		ChatSkinCfg cfg = ConfigManager.getInstance().getConfigByKey(ChatSkinCfg.class, skinId);
		// 配置不存在
		if (cfg == null) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return false;
		}
		// 不能用钻石购买
		if (cfg.getCostGold() == 0) {
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return false;
		}
		// 钻石不足
		if (player.getGold() < cfg.getCostGold()) {
			player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH_VALUE);
			return false;
		}
		// 减钻石
		player.consumeGold(cfg.getCostGold(), Action.BUY_CHAT_SKIN);
		ConsumeItems.valueOf(changeType.CHANGE_GOLD, cfg.getCostGold()).pushChange(player);
		// 购买成功
		entity.buy(skinId, timeCfg.getStageId());
		// 更新DB
		entity.reConvert();
		entity.notifyUpdate();

		HPChatSkinBuy.Builder ret = HPChatSkinBuy.newBuilder();
		ret.setSkinId(skinId);
		player.sendProtocol(Protocol.valueOf(HP.code.CHAT_SKIN_BUY_S_VALUE, ret));
		return true;
	}

}
