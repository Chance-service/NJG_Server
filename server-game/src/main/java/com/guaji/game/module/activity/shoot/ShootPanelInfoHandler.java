package com.guaji.game.module.activity.shoot;

import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;

import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Activity2.HPPanelPriceInfo;
import com.guaji.game.protocol.Activity2.HPShootPanelInfoRes;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ShootCostCfg;
import com.guaji.game.entity.ShootActivityEntity;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 气枪打靶，面板请求
 */
public class ShootPanelInfoHandler implements IProtocolHandler {
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.SHOOT_ACTIVITY_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);

		ShootActivityEntity entity = ShootActivityManager.getInstance().getShootActivityEntity();

		if (entity == null) {
			// 参数错误
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}

	

		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		HPShootPanelInfoRes.Builder ret = HPShootPanelInfoRes.newBuilder();
		ShootActivityInfo shootInfo = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(),
				ShootActivityInfo.class);

		Map<Object, ShootCostCfg> typeCfgs = ConfigManager.getInstance().getConfigMap(ShootCostCfg.class);
		if (typeCfgs == null || typeCfgs.size() == 0) {
			// 参数错误
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}

		int currentTime = GuaJiTime.getSeconds();

		// 组装协议内容
		for (ShootCostCfg typeCfg : typeCfgs.values()) {
			// 构建面板信息
			HPPanelPriceInfo.Builder builder = HPPanelPriceInfo.newBuilder();
			builder.setShootType(typeCfg.getType());
			builder.setOneTimePrice(typeCfg.getOneTimeCost());
			builder.setTenTimePrice(typeCfg.getTenTimeCost());

			int alreadyTime = shootInfo.getTodayFindTimes(typeCfg.getType());
			int refreshTime = typeCfg.getFreeRefreshTime() * 60 * 60;
			int time = currentTime - alreadyTime > refreshTime ? 0 : alreadyTime + refreshTime - currentTime;
			ret.addFreeTime(time);
			ret.addShootPriceInfo(builder);
		}

		ret.setRewardStateId(entity.getShootState());
		ret.setLeftTime(0);
		player.sendProtocol(Protocol.valueOf(HP.code.SHOOT_PANEL_S_VALUE, ret));
		return true;
	}
}
