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

import com.guaji.game.recharge.RechargeManager;
import com.guaji.game.recharge.RechargeParam;
import com.sun.net.httpserver.HttpExchange;

import net.sf.json.JSONObject;

public class PayNoticeHandler  extends GuaJiScript{
	private String sanboxAppid = "nzh5yq3ncmpf4gbb";//內部測試
	private final Logger logger = LoggerFactory.getLogger("Recharge");
	@Override
	public void action(String params, HttpExchange httpExchange) {		
		String retStr = "{\"errno\":1,\"errmsg\":\"充值失败\"}";
		try {
			String method = httpExchange.getRequestMethod();
			String ip = httpExchange.getRemoteAddress().getAddress().toString();
			logger.info("recharge json parse: " + params + " method: " + method + " ip:" + ip);
			RechargeParam rechargeParam = RechargeParam.valueOf(params);
			
			if (rechargeParam.getPlatform().contains("h365") && method.contains("POST"))
			{
				retStr = "{\"response_code\":\"OK\"}";
				GuaJiScriptManager.sendResponse(httpExchange, retStr);
				return;
			}
			
			if (rechargeParam.getPlatform().contains("h365") && method.contains("GET"))
			{
				String status = rechargeParam.getstatus();
				if (!status.equals("2"))
				{
					retStr = "{\"response_code\":\"OK\"}";
					GuaJiScriptManager.sendResponse(httpExchange, retStr);
					return;
				}
			}
			
			if (rechargeParam != null) {
				String h365Appid = "kad6kdj4p1vjwuzg";
				if (App.getInstance().getAppCfg().isDebug()) {
					h365Appid = sanboxAppid;
				}
				JSONObject ret = RechargeManager.getInstance().handleRecharge(rechargeParam);
				int errno = ret.getInt("errno");
				String errmsg = ret.getString("errmsg");
				if(errno==0){
					String productName = ret.getString("productName");
					retStr = "{\"response_code\":\"OK\"}";//"{\"errno\":0,\"errmsg\":\"成功\"}";
					if (rechargeParam.getPlatform().contains("h365"))
					{
						double USAD = (Double.parseDouble(rechargeParam.getPayMoney()) / 100);
						boolean bl = RechargeManager.getInstance().dohtapdb(h365Appid, rechargeParam.getOrderSerial(),rechargeParam.getPuid(),"USD",USAD,rechargeParam.getGoodsId(),productName);
					}
				}else{
					retStr = String.format("{\"errno\":1,\"errmsg\":\"%s\"}",errmsg);
				}
			}else{
				//充值失败
				retStr = "{\"errno\":1,\"errmsg\":\"初始化充值对象失败\"}";
			}
			if (rechargeParam.getPlatform().contains("h365"))
				retStr = "{\"response_code\":\"OK\"}";	//h365不管什麼都送ok
			GuaJiScriptManager.sendResponse(httpExchange, retStr);
		} catch (Exception e) {
			MyException.catchException(e);
			// 记录进充值日志
			logger.error(MyException.formatStackMsg(e));
			retStr = "{\"errno\":1,\"errmsg\":\"payNotice exception\"}";
			GuaJiScriptManager.sendResponse(httpExchange, retStr);
		}
	}
}
