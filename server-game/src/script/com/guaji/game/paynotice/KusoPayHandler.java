package com.guaji.game.paynotice;

import java.net.URLEncoder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.guaji.app.App;
import org.guaji.log.Log;
import org.guaji.os.MyException;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guaji.game.ServerData;
import com.guaji.game.config.RechargeConfig;
import com.guaji.game.config.RechargeConfig.RechargeItem;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.recharge.RechargeManager;
import com.guaji.game.recharge.RechargeParam;
import com.guaji.game.util.AESSignUtil;
import com.guaji.game.util.PlayerUtil;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import net.sf.json.JSONObject;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.lang.StringBuilder;

public class KusoPayHandler  extends GuaJiScript{
	private String sanboxAppid = "nzh5yq3ncmpf4gbb";//內部測試
	private final Logger logger = LoggerFactory.getLogger("Recharge");
	private final String mchKey = "MdXeK5Gydtjkl@Bd";
	private final String secret = "iHIFn@YJKcctt4PO";
	@Override
	public void action(String params, HttpExchange httpExchange) {
		JSONObject json = new JSONObject();
		json.put("response","OK");
		//String retStr = json.toString();
		try {
			
			Headers requestHeaders = httpExchange.getRequestHeaders();
			if (!requestHeaders.containsKey("sign")) {
				json.put("status", -1);
				json.put("msg", "kusoorder request, because no sign !");
				GuaJiScriptManager.sendResponse(httpExchange, json.toString());
				return;
			}
			
			String signature = requestHeaders.getFirst("sign");
			
			InputStreamReader reader = new InputStreamReader(httpExchange.getRequestBody(), "UTF-8");
			BufferedReader br = new BufferedReader(reader);
			int b;
			StringBuilder buf = new StringBuilder(512);
			while ((b = br.read()) != -1) {
			    buf.append((char) b);
			}
				
			br.close();
			reader.close();
			String body = buf.toString();
			
			JSONObject jsonObject = JSONObject.fromObject(body);
			
			String orderId = jsonObject.getString("id");
			String nonce = jsonObject.getString("nonce");
			int amount = jsonObject.getInt("amount");
			String currency = jsonObject.getString("currency");
			String status = jsonObject.getString("status");
			String createdAt = jsonObject.getString("createdAt");
			String updatedAt = jsonObject.getString("updatedAt");
			
			if (!RechargeManager.getInstance().KusoQueryOrderId(orderId)) {
				json.put("status", -999);
				json.put("msg", " not found orderId !");
				GuaJiScriptManager.sendResponse(httpExchange, json.toString());
				return;
			}
			
			// 須按字母順序
			StringBuffer plainBuffer = new StringBuffer();
			plainBuffer.append("amount=");
			plainBuffer.append(String.valueOf(amount));
			plainBuffer.append("&createdAt=");
			plainBuffer.append(createdAt);
			plainBuffer.append("&currency=");
			plainBuffer.append(currency);
			plainBuffer.append("&id=");
			plainBuffer.append(orderId);
			plainBuffer.append("&nonce=");
			plainBuffer.append(nonce);
			plainBuffer.append("&status=");
			plainBuffer.append(status);
			plainBuffer.append("&updatedAt=");
			plainBuffer.append(updatedAt);

			String plainText  =  AESSignUtil.aesEncrypt(plainBuffer.toString(), mchKey, secret);
			String sign = AESSignUtil.getSHA256Str(plainText);
			
			if (!signature.equals(sign)) {
				json.put("status", -7);
				json.put("msg", "signature not match!");
				GuaJiScriptManager.sendResponse(httpExchange, json.toString());
				return;
			}
			
			String method = httpExchange.getRequestMethod();
			String ip = httpExchange.getRemoteAddress().getAddress().toString();
			logger.info("recharge json parse: " + params + " method: " + method + " ip:" + ip);
			RechargeParam rechargeParam = RechargeParam.valueOf(params);
			if (rechargeParam == null) {
				json.put("status", -2);
				json.put("msg", "rechargeParam == null !");
				GuaJiScriptManager.sendResponse(httpExchange, json.toString());
				return;
			}
			
			int playerId = ServerData.getInstance().getPlayerIdByPuid(rechargeParam.getPuid(),
					rechargeParam.getServerId());
			
			Player player = PlayerUtil.queryPlayer(playerId);
			PlayerData playerData = null;
			if (player != null) {
				playerData = player.getPlayerData();
			} else {
				json.put("status", -3);
				json.put("msg", "player == null !");
				GuaJiScriptManager.sendResponse(httpExchange, json.toString());
				return;
			}
			
			if (!rechargeParam.getPlatform().contains("Kuso") && !method.contains("POST"))
			{
				json.put("status", -4);
				json.put("msg", " Platform != Kuso || method != Post !");
				GuaJiScriptManager.sendResponse(httpExchange, json.toString());
				return;
			}
			
			String platform = rechargeParam.getPlatform().trim().toLowerCase();
			RechargeConfig rechargeConfig = RechargeConfig.getRechargeConfig(platform);
			
			if (rechargeConfig == null) {
				json.put("status", -5);
				json.put("msg", " not found rechargeConfig !");
				GuaJiScriptManager.sendResponse(httpExchange, json.toString());
				return;
			}
			
//			String kusoAppid = "ojd31nr7523qt1pq";
//			if (App.getInstance().getAppCfg().isDebug()) {
//				kusoAppid = sanboxAppid;
//			}
			
			JSONObject ret = RechargeManager.getInstance().handleRecharge(rechargeParam);
			int errno = ret.getInt("errno");
			String errmsg = ret.getString("errmsg");
			if(errno==0){
				String productName = ret.getString("productName");
				if (rechargeParam.getPlatform().contains("kuso"))
				{
					double USAD = (Double.parseDouble(rechargeParam.getPayMoney())/8);
					boolean bl = RechargeManager.getInstance().dohtapdb(player.getTabDBAppId(), rechargeParam.getOrderSerial(),rechargeParam.getPuid(),"USD",USAD,rechargeParam.getGoodsId(),productName);
				}
			}else{
				json.put("status", -5);
				json.put("msg",errmsg);
				GuaJiScriptManager.sendResponse(httpExchange, json.toString());
				return;
			}

			GuaJiScriptManager.sendResponse(httpExchange, json.toString());
		} catch (Exception e) {
			MyException.catchException(e);
			// 记录进充值日志
			logger.error(MyException.formatStackMsg(e));
			json.put("status", -6);
			GuaJiScriptManager.sendResponse(httpExchange, json.toString());
		}
	}
}
