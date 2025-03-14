package com.guaji.game.verify;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
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

public class LongXiaoLoginVerify implements ILoginVerify{
	
	private static final String BASE_URL = "http://api.ft.asm8.com//api/channel_login_verify";
	private static final String GAME_ID = "1120";
	private static final String KEY = "MGX7A2rJhOkwuj";
	
	
	@Override
	public boolean loginVerify(GuaJiSession session, Protocol protocol) {
		HPLogin hpLogin = protocol.parseProtocol(HPLogin.getDefaultInstance());
		String puid = hpLogin.getPuid().trim().toLowerCase();
		String sessionid = hpLogin.getToken();
		if (sessionid == null || sessionid.trim().equals("")) {
			// token为空
			return false;
		}
		try {
			long curSecond = GuaJiTime.getMillisecond()/1000;
			StringBuffer joinMd5 = new StringBuffer();
			joinMd5.append(GAME_ID);
			joinMd5.append(puid);
			joinMd5.append(KEY);
			joinMd5.append(curSecond);
			String sign = MD5Util.MD5(joinMd5.toString());
			System.err.println("MD5 原串："+joinMd5.toString());
			System.err.println("md5 串："+sign);
			StringBuffer reqBuffer = new StringBuffer();
			reqBuffer.append(BASE_URL);
			reqBuffer.append("?game_id=");
			reqBuffer.append(GAME_ID);
			reqBuffer.append("&uid=");
			reqBuffer.append(puid);
			reqBuffer.append("&time=");
			reqBuffer.append(curSecond);
			reqBuffer.append("&sessionid=");
			reqBuffer.append(sessionid);
			reqBuffer.append("&sign=");
			reqBuffer.append(sign);
			String reqUrl = reqBuffer.toString();
			System.err.println("登录验证url："+reqUrl);
			HttpClient httpClient = new HttpClient();
			GetMethod getMethod = new GetMethod(reqUrl);
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode == HttpStatus.SC_OK) {
				byte[] responseBody = getMethod.getResponseBody();
				String response = new String(responseBody, "UTF-8");
				JSONObject jobj = JSONObject.fromObject(response);
				String errno = jobj.getString("status");
				String errmsg = jobj.getString("data");
				int errCode = Integer.parseInt(errno);
				if (errCode == 1) {
					return true;
				}else{
					Log.errPrintln("longxiao login verify failed puid = " + puid + " status = " + errCode+" erromsg = "+errmsg);
				}
				JSONObject dataJson = JSONObject.fromObject(errmsg);
				HPErrorCode.Builder builder = HPErrorCode.newBuilder();
				builder.setHpCode(HP.code.LOGIN_C_VALUE);
				builder.setErrCode(0);
				builder.setErrFlag(1);
				builder.setErrMsg(dataJson.getString("msg"));
				session.sendProtocol(Protocol.valueOf(HP.sys.ERROR_CODE, builder));
			} else {
				HPErrorCode.Builder builder = HPErrorCode.newBuilder();
				builder.setHpCode(HP.code.LOGIN_C_VALUE);
				builder.setErrCode(0);
				builder.setErrFlag(1);
				builder.setErrMsg("longxiao login verify failed!");
				session.sendProtocol(Protocol.valueOf(HP.sys.ERROR_CODE, builder));
				Log.errPrintln("longxiao login verify failed puid = " + puid + " httpCode = " + statusCode);
			}
			return false;
		} catch (Exception e) {
			// TODO: handle exception
			MyException.catchException(e);
			return false;
		}
	}

}
