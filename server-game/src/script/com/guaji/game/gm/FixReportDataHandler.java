package com.guaji.game.gm;

import java.util.Calendar;
import java.util.List;

import org.guaji.db.DBManager;
import org.guaji.os.MyException;
import org.guaji.os.GuaJiTime;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.util.services.ReportService;

import com.google.gson.JsonObject;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.entity.RechargeEntity;
import com.sun.net.httpserver.HttpExchange;

/**
 * 玩家定时修复帮主处理
 */
public class FixReportDataHandler extends GuaJiScript {
	
	@Override
	public void action(String params, final HttpExchange httpExchange) {
		int rechargeCount = 0;
		int registerCount = 0;
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			// 加载4号之后所有的充值
			List<RechargeEntity> rechargeEntities = DBManager.getInstance().query("from RechargeEntity where createTime >= ?", calendar.getTime());
//			for(RechargeEntity rechargeEntity : rechargeEntities) {
//				ReportService.RechargeData rechargeData = new ReportService.RechargeData();
//				rechargeData.setPuid(rechargeEntity.getPuid());
//				rechargeData.setDevice(rechargeEntity.getDevice());
//				rechargeData.setPlayerId(rechargeEntity.getPlayerId());
//				rechargeData.setPlayerName(rechargeEntity.getPlayerName());
//				rechargeData.setPlayerLevel(rechargeEntity.getLevel());
//				rechargeData.setOrderId(rechargeEntity.getOrderSerial());
//				rechargeData.setPayMoney(rechargeEntity.getGoodsCost());
//				rechargeData.setCurrency(rechargeEntity.getCurrency());
//				rechargeData.setTime(GuaJiTime.getTimeString(rechargeEntity.getCreateTime()));
//				ReportService.getInstance().report(rechargeData);
//				rechargeCount ++;
//			}
			rechargeCount = rechargeEntities==null?0:rechargeEntities.size();
			
			List<PlayerEntity> playerEntities = DBManager.getInstance().query("from PlayerEntity where createTime >= ?", calendar.getTime());
//			for(PlayerEntity playerEntity : playerEntities) {
//				ReportService.RegisterData registerData = new ReportService.RegisterData(playerEntity.getServerId()+"", playerEntity.getPuid(),
//							playerEntity.getDevice(), playerEntity.getId(), GuaJiTime.getTimeString(playerEntity.getCreateTime()));
//				ReportService.getInstance().report(registerData);
//				registerCount++;
//			}
			registerCount = playerEntities==null?0:playerEntities.size();
		} catch (Exception e) {
			MyException.catchException(e);
		}
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("status", 1);
		jsonObject.addProperty("registerCount", registerCount);
		jsonObject.addProperty("rechargeCount", rechargeCount);
		// 回复状态
		GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
	}
}

