package com.guaji.game.module.activity.wealthClub;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.MyException;
import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Activity2.HPGoldClubInfoRet;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.WealthClubCfg;
import com.guaji.game.config.ActivityCfg.ActivityItem;
import com.guaji.game.entity.WealthData;
import com.guaji.game.manager.ActivityManager;
import com.guaji.game.manager.WealthClubManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 财富聚乐部初始化
 */
public class WealthClubInfoHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.GOLD_CLUB_VALUE;
		ActivityItem activityItem = ActivityManager.getInstance().getActiveActivityItem(Const.ActivityId.GOLD_CLUB_VALUE);
		if(activityItem==null){
			return true;
		}
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		// 活动是否关闭判断
		if (timeCfg == null) {
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		// 自动检测充值玩家数量
		WealthClubManager.getInstance().rechargePlayerNumber();
		WealthClubManager.getInstance().getEntity().notifyUpdate(true);
		// 返回数据包构建
		HPGoldClubInfoRet.Builder builder = HPGoldClubInfoRet.newBuilder();
		// 每日的财富俱乐部数据
		String dateFormat = GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date());
		WealthData wealthData = WealthClubManager.getInstance().getEntity().getWealthDataByKey(dateFormat);
		// 个人数据
		WealthClubStatus status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), WealthClubStatus.class);
		// 充值人数
		int rechargePeople = wealthData.getTotalNumber();
		builder.setRechargePeople(rechargePeople);
		// 当天充值金额
		Integer vaule = status.getRechargeMap(dateFormat);
		if (null == vaule) {
			builder.setRecharge(0);
		} else {
			builder.setRecharge(vaule);
		}
		// 当前返利比例
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		WealthClubCfg config = ConfigManager.getInstance().getConfigByIndex(WealthClubCfg.class, 0);
		try {
			String formula = String.format(config.getFormula(), rechargePeople);
			int proportion = ((Double) engine.eval(formula)).intValue();
			builder.setProportion(proportion);
		} catch (ScriptException e) {
			MyException.catchException(e);
			return false;
		}
		builder.setLeftTimes(timeCfg.calcActivitySurplusTime());
		builder.setStageStatus(WealthClubManager.getStageStatus());
		player.sendProtocol(Protocol.valueOf(HP.code.GOLD_CLUB_INFO_S, builder));
		return true;
	}
}
