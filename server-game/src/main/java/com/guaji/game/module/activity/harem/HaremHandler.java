package com.guaji.game.module.activity.harem;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPHaremDraw;
import com.guaji.game.protocol.Activity2.HPHaremDrawRet;
import com.guaji.game.protocol.Activity2.HPHaremInfo;
import com.guaji.game.protocol.Activity2.HPSyncHaremRet;
import com.guaji.game.config.HaremConstCfg;
import com.guaji.game.entity.HaremActivityEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 王的后宫抽卡 类型：普通、高级、限定(其中限定有开启时间)、新手限定(有限定时间) <br>
 * 消耗顺序：免费 > 抽奖券（不同类型抽奖券不一样）> 元宝
 */
public class HaremHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		HPHaremDraw request = protocol.parseProtocol(HPHaremDraw.getDefaultInstance());
		// 不同的活动类型
		int haremType = request.getType();
		// 判断活动类型有效时间
		Map<Object, HaremConstCfg> HaremConstCfgMap = ConfigManager.getInstance().getConfigMap(HaremConstCfg.class);
		HaremConstCfg constCfg = HaremConstCfgMap.get(haremType);
		// 活动实体类型
		HaremActivityEntity harem = player.getPlayerData().getHaremActivityEntity();
		/** 抽奖次数 **/
		int drawTimes = request.getTimes();

		if (haremType == Const.HaremType.HAREM_TYPE_COMMON_VALUE) {
			if (drawTimes < HaremManager.TIMES_TYPE_SINGLE && drawTimes > HaremManager.TIMES_TYPE_COMBO) {
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
				return true;
			}
		} else {
			if (drawTimes != HaremManager.TIMES_TYPE_SINGLE && drawTimes != HaremManager.TIMES_TYPE_COMBO) {
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
				return true;
			}
		}

		switch (haremType) {
		case Const.HaremType.HAREM_TYPE_STRICT_VALUE:
		case Const.HaremType.HAREM_TYPE_LIMIT_VALUE:
			// 限定活动类型
			if (!HaremManager.isStrictOpen(player, constCfg)) {
				player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
				return true;
			}
			break;
		case Const.HaremType.HAREM_TYPE_NEW_STRICT_VALUE:
			// 新手限定活动类型
			if (!HaremManager.isNewStrictOpen(harem)) {
				player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
				return true;
			}
			break;
		case Const.HaremType.HAREM_TYPE_COMMON_VALUE:// 检查是否大于今天上限
			// 新手限定活动类型
			if (harem.getCommonDayTotalTimes() + drawTimes > constCfg.getMaxDayTotalTimes()) {
				// 操作最大次数
				player.sendError(protocol.getType(), Status.error.MAX_MULTIELITE_BUY_TIMES_VALUE);
				return true;
			}

			break;
		default:
			// 其他类型活动
			Date endDate = constCfg.getEndDate();
			if (endDate.getTime() <= System.currentTimeMillis()) {
				player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
				return true;
			}
			break;
		}

		/** 检测并消耗 **/
		int retCode = HaremManager.checkAndConsume(player, harem, haremType, drawTimes);
		if (retCode != 0) {
			player.sendError(protocol.getType(), retCode);
			return true;
		}
		/** 抽奖和发奖 **/
		List<AwardItems> awardItemList = HaremManager.drawAndGive(player, harem, haremType, drawTimes);
		/** 奖品返回 **/
		HPHaremDrawRet.Builder haremDrawBuilder = HPHaremDrawRet.newBuilder();
		for (AwardItems itemInfo : awardItemList) {
			haremDrawBuilder.addReward(itemInfo.toString());
		}
		player.sendProtocol(Protocol.valueOf(HP.code.HAREM_DRAW_S_VALUE, haremDrawBuilder));
		/** 更新活动实体 **/
		int deltaScore = constCfg.getAddScore();
		harem.setScore(harem.getScore() + deltaScore * drawTimes);
		harem.notifyUpdate();
		/** 推送一次同步包 **/
		HPSyncHaremRet.Builder response = HPSyncHaremRet.newBuilder();
		HPHaremInfo.Builder haremInfo = HaremManager.getHaremInfo(player, haremType);
		response.addHaremInfo(haremInfo);
		response.setScore(harem.getScore());
		player.sendProtocol(Protocol.valueOf(HP.code.SYNC_HAREM_S_VALUE, response));
		return true;
	}

}
