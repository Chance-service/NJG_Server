package com.guaji.game.verify;

import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.guaji.log.Log;
import org.guaji.net.GuaJiSession;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.MyException;

import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Login.HPLogin;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.SysProtocol.HPErrorCode;

import net.sf.json.JSONObject;

public class YouGuLoginVerify implements ILoginVerify {
	private static final String BASE_URL = "http://smi.51sfsy.com/mrj/auth";

	public boolean loginVerify(GuaJiSession session, Protocol protocol) {
		HPLogin hpLogin = protocol.parseProtocol(HPLogin.getDefaultInstance());
		String puid = hpLogin.getPuid().trim().toLowerCase();
		String token = hpLogin.getToken();
		if (token == null || token.trim().equals("")) {
			// token为空
			return false;
		}
		try {
			StringBuffer reqBuffer = new StringBuffer();
			reqBuffer.append(BASE_URL);
			reqBuffer.append("?uid=");
			reqBuffer.append(URLEncoder.encode(puid, "UTF-8"));
			reqBuffer.append("&token=");
			reqBuffer.append(URLEncoder.encode(token, "UTF-8"));
			String reqUrl = reqBuffer.toString();
			HttpClient httpClient = new HttpClient();
			GetMethod getMethod = new GetMethod(reqUrl);
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode == HttpStatus.SC_OK) {
				byte[] responseBody = getMethod.getResponseBody();
				String response = new String(responseBody, "UTF-8");
				JSONObject jobj = JSONObject.fromObject(response);
				String errno = jobj.getString("errno");
				String errmsg = jobj.getString("errmsg");
				int errCode = Integer.parseInt(errno);
				if (errCode >= 0) {
					return true;
				}else{
					Log.errPrintln("yougu login verify failed puid = " + puid + " errocode = " + errCode+" erromsg = "+errmsg);
				}
				HPErrorCode.Builder builder = HPErrorCode.newBuilder();
				builder.setHpCode(HP.code.LOGIN_C_VALUE);
				builder.setErrCode(0);
				builder.setErrFlag(1);
				builder.setErrMsg(errmsg);
				session.sendProtocol(Protocol.valueOf(HP.sys.ERROR_CODE, builder));
			} else {
				HPErrorCode.Builder builder = HPErrorCode.newBuilder();
				builder.setHpCode(HP.code.LOGIN_C_VALUE);
				builder.setErrCode(0);
				builder.setErrFlag(1);
				builder.setErrMsg("yougu login verify failed!");
				session.sendProtocol(Protocol.valueOf(HP.sys.ERROR_CODE, builder));
				Log.errPrintln("yougu login verify failed puid = " + puid + " httpCode = " + statusCode);
			}
			return false;
		} catch (Exception e) {
			// TODO: handle exception
			MyException.catchException(e);
			return false;
		}
	}
}
