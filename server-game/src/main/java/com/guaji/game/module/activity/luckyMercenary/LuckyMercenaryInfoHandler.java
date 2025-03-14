package com.guaji.game.module.activity.luckyMercenary;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import org.guaji.config.ConfigManager;
import com.guaji.game.protocol.Activity2.HPLuckyMercenary;
import com.guaji.game.protocol.Activity2.HPLuckyMercenaryInfoRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.LuckyMercenaryActivityCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/***
 * 幸运福将活动
 */

public class LuckyMercenaryInfoHandler implements IProtocolHandler {
	/** 给客户端发送的消息每次都一样，所以讲消息缓存起来，减少创建开支 */
	private static ThreadLocal<HPLuckyMercenaryInfoRet.Builder> cache = new ThreadLocal<HPLuckyMercenaryInfoRet.Builder>();

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		Player player = (Player) appObj;
		int activityId = Const.ActivityId.Lucky_MERCENARY_VALUE;
		// 检测活动是否开放
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);

		if (timeCfg == null) {
			if (cache.get() != null) {
				cache.remove();
			}
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		// 获取配置对象
		Map<Object, LuckyMercenaryActivityCfg> cfgMap = ConfigManager.getInstance().getConfigMap(LuckyMercenaryActivityCfg.class);
		if (cfgMap == null || cfgMap.size() == 0) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}

		HPLuckyMercenaryInfoRet.Builder ret = cache.get();
		if (ret == null) {
			ret = HPLuckyMercenaryInfoRet.newBuilder();
			Map<Integer, List<LuckyMercenaryActivityCfg>> luckyMap = LuckyMercenaryActivityCfg.getRoleLuckyItems();
			if (luckyMap == null || luckyMap.size() <= 0) {
				player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
				return true;
			}
			for (Entry<Integer, List<LuckyMercenaryActivityCfg>> entry : luckyMap.entrySet()) {
				HPLuckyMercenary.Builder item = HPLuckyMercenary.newBuilder();
				item.setMercenaryID(entry.getKey());
				List<LuckyMercenaryActivityCfg> cfgList = entry.getValue();
				for (LuckyMercenaryActivityCfg activityCfg : cfgList) {
					item.addId(activityCfg.getId());
				}
				ret.addLuckMercenaryItem(item);
			}
			cache.set(ret);
		}

		// 设置活动剩余时间
		ret.setLeftTime(timeCfg.calcActivitySurplusTime());
		// 给客户端发送出去
		player.sendProtocol(Protocol.valueOf(HP.code.LUCK_MERCENARY_S, ret));
		return true;
	}

}
