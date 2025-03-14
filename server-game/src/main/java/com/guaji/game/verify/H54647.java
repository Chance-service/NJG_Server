package com.guaji.game.verify;

import java.net.URLEncoder;

//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpStatus;
//import org.apache.commons.httpclient.methods.GetMethod;
//import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.guaji.log.Log;
import org.guaji.net.GuaJiSession;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;

import com.guaji.game.platform.util.MD5Util;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Login.HPLogin;
import com.guaji.game.protocol.SysProtocol.HPErrorCode;

import net.sf.json.JSONObject;

public class H54647 implements ILoginVerify{
	
	private static final String BASE_URL = "https://gen.exchangcall.com/api/checkBindGameStatus";//exchangeCoins";
	private static final String GAME_ID = "31";
	private static final String KEY = "639F011AA237E1A643639AF77C3663F7";
	
	
	@Override
	public boolean loginVerify(GuaJiSession session, Protocol protocol) {
		HPLogin hpLogin = protocol.parseProtocol(HPLogin.getDefaultInstance());
		String puid = hpLogin.getPuid().trim().toLowerCase();
		String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiZGYyODIyYzItMmZlYy00NTEwLTg5N2ItNWQwMDA2ZGU2NTA1IiwidG9rZW5faWQiOiIxOWY1YTk4Mi0wZTc4LTQwNTAtOTRhNS0wOGM2NmU5Y2FjM2UiLCJpYXQiOjE2MjAxODUyOTF9.l4JzDyzAad6gKyPex2XYbl_qk_J6QVV4m3R68zxifpJF3d3O5BLyjo8VT6mCyaWeowquyDE8CgQiniUYxHtWfUU0iStcjfxo9vXxtnoWm-IXhcI9gDgK6wVFglOnctNRl28cEHWNIoBy2j6HrnVfGnSHww_4AhqRlgYWg2ERmeBmFkksNtZZzq6WUByH3EDOmGPgZMQxeg9ca2kh0oVjnqaoIQlnxlrQtEpT0EWLb_-m4N7OAhqSp1L4PO8tWJn0fhBQ3oh0TCJGUgYaOe381Cj567gZp1Fw8U2QpqeBVtpAIUNdZ1HDcFQ-3GI5CeyXmF4LUMDNVu8_23zMOnUmzA";
		String req_base64 = "ewoidHJhbnNhY3Rpb25faWQiIDogIlRFU1QwMDAwMDEiLAoiZXhjaGFuZ2VfY29pbnMiIDogMjAwLAp9Cg==";
		if (token == null || token.trim().equals("")) {
			// token为空
			return false;
		}
		try {
			long curSecond = GuaJiTime.getMillisecond()/1000;
			StringBuffer joinMd5 = new StringBuffer();
			joinMd5.append("game_id:");
			joinMd5.append(GAME_ID);
			joinMd5.append("&request:");
			joinMd5.append(req_base64);
			joinMd5.append("&key:");
			joinMd5.append(KEY);
			String sign = MD5Util.MD5(joinMd5.toString()).toUpperCase();
			System.err.println("MD5 原串："+joinMd5.toString());
			System.err.println("md5 串："+sign);
			StringBuffer reqBuffer = new StringBuffer();
			reqBuffer.append("game_id=");
			reqBuffer.append(GAME_ID);
//			reqBuffer.append("&signMsg:");
//			reqBuffer.append(sign);
//			reqBuffer.append("&request:");
//			reqBuffer.append(req_base64);
			String reqUrl = reqBuffer.toString();
			System.err.println("登录验证url："+reqUrl);
			
			HttpClient httpClient = null;
			HttpPost httpPost = null;
			HttpResponse response = null;
			
			httpClient = HttpClients.custom().build();

			httpPost = new HttpPost(BASE_URL);
			RequestConfig reqConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
			httpPost.setConfig(reqConfig);
//			httpPost.setHeader(HttpHeaders.CONNECTION, "close");
			httpPost.setHeader("Authorization", token);
			httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
			httpPost.setHeader("Accept", "application/json");

			String postRequest = URLEncoder.encode(reqBuffer.toString(), "UTF-8");
			StringEntity entity = new StringEntity(postRequest);
			entity.setContentEncoding("utf-8");
			entity.setContentType("application/x-www-form-urlencoded");
			httpPost.setEntity(entity);
			response = httpClient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			String resultStr = EntityUtils.toString(httpEntity);
			System.err.println("resultStr："+resultStr);
			return false;
		} catch (Exception e) {
			// TODO: handle exception
			MyException.catchException(e);
			return false;
		}
	}

}
