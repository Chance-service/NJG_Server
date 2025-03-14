package com.guaji.game.verify;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.guaji.app.App;
import org.guaji.log.Log;
import org.guaji.net.GuaJiSession;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.MyException;

import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Login.HPLogin;
import com.guaji.game.protocol.SysProtocol.HPErrorCode;

import net.sf.json.JSONObject;

public class H365LoginVerify implements ILoginVerify {
	private static final String SanBox_BASE_URL = "https://sapi.h365.games/api/v1/user/profile";
	private static final String Production_BASE_URL = "https://api.h365.games/api/v1/user/profile";
	public boolean loginVerify(GuaJiSession session, Protocol protocol) {
		String BASE_URL = App.getInstance().getAppCfg().isDebug() ? SanBox_BASE_URL : Production_BASE_URL;
		HPLogin hpLogin = protocol.parseProtocol(HPLogin.getDefaultInstance());
		String puid = hpLogin.getPuid().trim().toLowerCase();
		String token = hpLogin.getToken().trim();//"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzZXNzaW9uS2V5IjoiY2M1ZjQzZjBlMWJkNDFjYWEzN2YzZTkxZDVmOWJiNmViOTI1Mjk2OWZkZTIxNjliNjYyMzdjMjgxOTE2YzFlZSIsInNlcnZpY2VOYW1lIjoiTmluamEgR2lybCIsImlhdCI6MTcyNDE0NzI5Nn0.VcbVQWZQ_5wZEkolD5ASZoMFQblDgOXisjIOyd9tqCc";//hpLogin.getToken();
		if (token == null || token.trim().equals("")) {
			// token为空
			return false;
		}
		try {
			StringBuffer reqBuffer = new StringBuffer();
			reqBuffer.append(BASE_URL);
			reqBuffer.append("?merchantId=");
			reqBuffer.append("SSP");
			reqBuffer.append("&serviceId=");
			reqBuffer.append("NJG");
			String reqUrl = reqBuffer.toString();
			
			HttpClient httpClient = HttpClients.custom().build();
			HttpGet httpGet = new HttpGet(reqUrl);
			RequestConfig reqConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
			httpGet.setConfig(reqConfig);
			httpGet.setHeader("Authorization","Bearer "+ token);
			httpGet.setHeader("Content-type", "application/json");
			HttpResponse response = null;
			
			response = httpClient.execute(httpGet);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				String data = EntityUtils.toString(response.getEntity());//,Charsets.UTF_8);
				
				JSONObject jobj = JSONObject.fromObject(data);
				int code = jobj.getInt("code");
				
				JSONObject jsonData = jobj.getJSONObject("data");
				String uuid = jsonData.getString("uuid");
				if (code == 0) {
					if (puid.equals(uuid)) {
						return true;
					} else {
						Log.errPrintln("H365 login verify failed puid not match" + puid + " code = " + code);
					}
				}else{
					Log.errPrintln("H365 login verify failed puid = " + puid + " code = " + code);
				}
				HPErrorCode.Builder builder = HPErrorCode.newBuilder();
				builder.setHpCode(HP.code.LOGIN_C_VALUE);
				builder.setErrCode(0);
				builder.setErrFlag(1);
				builder.setErrMsg("H365 login verify failed!");
				session.sendProtocol(Protocol.valueOf(HP.sys.ERROR_CODE, builder));
			} else {
				HPErrorCode.Builder builder = HPErrorCode.newBuilder();
				builder.setHpCode(HP.code.LOGIN_C_VALUE);
				builder.setErrCode(0);
				builder.setErrFlag(1);
				builder.setErrMsg("H365 login verify failed!");
				session.sendProtocol(Protocol.valueOf(HP.sys.ERROR_CODE, builder));
				Log.errPrintln("H365 login verify failed puid = " + puid + " httpCode = " + statusCode);
			}
			return false;
		} catch (Exception e) {
			// TODO: handle exception
			MyException.catchException(e);
			return false;
		}
	}
}
