package com.guaji.game.gm;

import java.util.Map;

import org.guaji.db.DBManager;
import org.guaji.os.MyException;
import org.guaji.os.GuaJiTime;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.config.RechargeConfig;
import com.guaji.game.entity.GmRechargeEntity;
import com.guaji.game.entity.RechargeEntity;
import com.guaji.game.recharge.RechargeManager;
import com.sun.net.httpserver.HttpExchange;

/**
 * 后台充值脚本
 * http://0.0.0.0:5132/recharge?user=123123&params=playerid:9;rechargeNum:120;goodsId:1
 * http://0.0.0.0:5132/recharge?user=123123&params=playerid:9;rechargeNum:9800;goodsId:6
 * 
 * rechargeNum 参考xml/rechargeConfig/rechargeConfig.xml的costMoney
 * goodsId参考xml/rechargeConfig/rechargeConfig.xml的id
 * @date 2014-8-22
 */
public class GMRechargeHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (paramsMap.containsKey("playerid")) {
			int playerId = Integer.valueOf(paramsMap.get("playerid"));
			int rechargeNum = paramsMap.get("rechargeNum") == null ? 0 : Integer.valueOf(paramsMap.get("rechargeNum").trim());
			String platform = paramsMap.get("platform") == null ? "" : paramsMap.get("platform").trim();
			int goodsId = paramsMap.get("goodsId") == null ? 0 : Integer.valueOf(paramsMap.get("goodsId").trim());
		    String newOrderSerial = "GM_"+GuaJiTime.getMillisecond()+"_"+playerId;
			String orderSerial = paramsMap.get("orderSerial") == null ? newOrderSerial : paramsMap.get("orderSerial").trim();
			if(rechargeNum == 0 && goodsId == 0){
				GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":"+ "rechargeNum and goodsId is zero together" +"}");
				return;
			}

			RechargeEntity rechargeEntity = null;
			RechargeConfig rechargeConfig = RechargeConfig.getRechargeConfig(platform);
			if (rechargeConfig == null) {
				rechargeConfig = RechargeConfig.getRechargeConfig("");
			}

			RechargeConfig.RechargeItem rechargeItem = rechargeConfig.get(goodsId);
			if (rechargeItem != null) {
				rechargeEntity = RechargeManager.getInstance().doRecharge(playerId, orderSerial, rechargeItem.getCostMoney(),
						rechargeItem.getId(), rechargeItem.getCostMoney(), rechargeItem.getCurrency(), 
						rechargeItem.getAmount(), rechargeItem.getAddNum(), rechargeConfig.getMoneyGoldRatio(),rechargeItem.getType(),rechargeItem.getCostAmount(),true, platform, true,0);
			} else {
				rechargeEntity = RechargeManager.getInstance().doRecharge(playerId, orderSerial, rechargeNum, 0, rechargeNum, 
						rechargeConfig.getCurrency(), rechargeNum * rechargeConfig.getMoneyGoldRatio(), 0, rechargeConfig.getMoneyGoldRatio(),0,0,true, platform, true,0);//type后加的先不管;
			}
			
			if (rechargeEntity != null) {
				try {
					GmRechargeEntity gmRechargeEntity = new GmRechargeEntity();
					gmRechargeEntity.setPuid(rechargeEntity.getPuid());
					gmRechargeEntity.setPlayerId(rechargeEntity.getPlayerId());
					gmRechargeEntity.setGoodsId(rechargeEntity.getGoodsId());
					gmRechargeEntity.setGoodsCost(rechargeEntity.getGoodsCost());
					gmRechargeEntity.setIsFirstPay(rechargeEntity.getIsFirstPay());
					gmRechargeEntity.setAddGold(rechargeEntity.getAddGold());
					DBManager.getInstance().create(gmRechargeEntity);
				} catch (Exception e) {
					MyException.catchException(e);
				}
			}
			
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
		} else {
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"invalid params\"}");
		}
	}
}
