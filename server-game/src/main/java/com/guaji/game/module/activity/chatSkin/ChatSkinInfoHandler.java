package com.guaji.game.module.activity.chatSkin;

import java.util.Map;
import java.util.Map.Entry;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Activity2.HPChatSkinActivityInfo;
import com.guaji.game.protocol.Activity2.HPChatSkinActivityInfo.ChatSkinActInfo;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ChatSkinCfg;
import com.guaji.game.entity.ChatSkinEntity;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 获取聊天皮肤信息
 */
public class ChatSkinInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.CHAT_SKIN_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return false;
		}

		//聊天皮肤数据
		ChatSkinEntity entity = player.getPlayerData().getChatSkinEntity();

		//协议返回
		HPChatSkinActivityInfo.Builder ret = HPChatSkinActivityInfo.newBuilder();

		// 活动剩余秒数
		ret.setLeftTime((int)((timeCfg.getlEndTime() - GuaJiTime.getCalendar().getTimeInMillis()) / 1000));
		// 皮肤配置
		Map<Object, ChatSkinCfg> cfgs = ConfigManager.getInstance().getConfigMap(ChatSkinCfg.class);
		for (Entry<Object, ChatSkinCfg> e : cfgs.entrySet()) {
			ChatSkinCfg cfg = e.getValue();
			int skinId = cfg.getSkinId();
			ChatSkinActInfo.Builder b = ChatSkinActInfo.newBuilder();
			b.setSkinId(skinId);
			b.setBought(entity.canBuy(skinId, timeCfg.getStageId()) ? 0 : 1);
			b.setSkinTime(cfg.getDays());
			b.setPrice(cfg.getCostGold());
			b.setRechargeId(cfg.getGoodsId());
			ret.addSkins(b);
		}

		player.sendProtocol(Protocol.valueOf(HP.code.CHAT_SKIN_INFO_S_VALUE, ret));

		return true;
	}

}
