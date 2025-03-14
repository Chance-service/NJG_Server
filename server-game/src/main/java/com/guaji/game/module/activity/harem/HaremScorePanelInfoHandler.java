package com.guaji.game.module.activity.harem;

import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPHaremScorePanelRes;
import com.guaji.game.config.HaremExchangeCfg;
import com.guaji.game.entity.HaremActivityEntity;
import com.guaji.game.player.Player;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 王的后宫,积分面板信息
 */
public class HaremScorePanelInfoHandler implements IProtocolHandler {
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;
		Map<Object, HaremExchangeCfg> cfgMap = ConfigManager.getInstance().getConfigMap(HaremExchangeCfg.class);
		if (cfgMap == null || cfgMap.size() <= 0) {
			// 参数错误
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}

		// 活动实体
		HaremActivityEntity entity = player.getPlayerData().getHaremActivityEntity();

		// 组装协议
		HPHaremScorePanelRes.Builder builder = BuilderUtil.getHaremExchangeBuilders(entity, cfgMap, 1);
		player.sendProtocol(Protocol.valueOf(HP.code.HAREM_PANEL_INFO_S_VALUE, builder));
		return true;
	}
}
