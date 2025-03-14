package org.guaji.script;

import java.io.IOException;
import java.net.URLDecoder;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Map;
import java.util.Set;

import org.guaji.app.App;
import org.guaji.config.ConfigManager;
import org.guaji.cryption.Md5;
//import org.guaji.cryption.RsaCrypt;
import org.guaji.log.Log;
import org.guaji.os.GuaJiShutdownHook;
import org.guaji.os.GuaJiTime;
//import org.guaji.net.GuaJiNetManager;
import org.guaji.os.MyException;
import org.guaji.service.ServiceManager;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.sf.json.JSONObject;

/**
 * 脚本的http请求处理器
 */
public class GuaJiScriptHttpHandler implements HttpHandler {

	private static final String MD5_KEY = "EvwHHATCz2hE3Y1ca7TnS0fudylgQUE7";
	/**
	 * 配置日志对象
	 */
	static Logger logger = LoggerFactory.getLogger("Script");
	/**
	 * 是否挂起状态
	 */
	private boolean suspend = false;

	/**
	 * 脚本执行线程
	 */
	private class ScriptShellExecutor extends Thread {
		/**
		 * 参数
		 */
		String params;
		/**
		 * http请求对象
		 */
		HttpExchange httpExchange;

		/**
		 * 构造
		 */
		ScriptShellExecutor(HttpExchange httpExchange, String params) {
			this.params = params;
			this.httpExchange = httpExchange;
		}

		/**
		 * 线程执行
		 */
		@Override
		public void run() {
			String result = onShellCommand(GuaJiScriptManager.paramsToMap(params));
			if (result != null) {
				GuaJiScriptManager.sendResponse(httpExchange, result);
			}
		}
	}

	/**
	 * 处理http请求
	 */
	@Override
	public void handle(HttpExchange httpExchange) throws IOException {

		String result = "" + GuaJiTime.getTimeString() + ":\n\n";
		String uriInfo = httpExchange.getRequestURI().getQuery();
		String path = httpExchange.getRequestURI().getPath();
		String command = path.substring(1);
		String params = null;
		String user = null;
		String sign = null;
		String token = null;

		// url携带参数分离解析
		if (uriInfo != null) {
			String[] querys = uriInfo.split("&");
			for (String query : querys) {
				int strIndex = query.indexOf("=");
				if (strIndex != -1) {
					String paramName = query.substring(0, strIndex);
					String paramValue = query.substring(strIndex+1, query.length());
					if (paramName.equals("params")) {
						params = URLDecoder.decode(paramValue, "UTF-8");
					} else if (paramName.equals("user")) {
						user = URLDecoder.decode(paramValue, "UTF-8");
					} else if (paramName.equals("sign")) {
						sign = URLDecoder.decode(paramValue, "UTF-8");
					} else if (paramName.equals("token")) {
						token = URLDecoder.decode(paramValue, "UTF-8");
					}
				}

				/*
				 * String[] pair = query.split("="); if (pair.length == 2) { if
				 * (pair[0].equals("params")) { params = URLDecoder.decode(pair[1], "UTF-8"); }
				 * else if (pair[0].equals("user")) { user = URLDecoder.decode(pair[1],
				 * "UTF-8"); }else if(pair[0].equals("sign")){ sign = URLDecoder.decode(pair[1],
				 * "UTF-8"); } }
				 */
			}
		}

		// 白名单控制
		if (GuaJiScriptManager.getInstance().getScriptConfig().isWhiteIptables()) {
			String ip = httpExchange.getRemoteAddress().toString().split(":")[0].substring(1);
			Set<String> whiteIps = GuaJiScriptManager.getInstance().getScriptConfig().getWhiteIps();
			// h365使用儲值payNotice不受IP限制
			if (whiteIps != null && whiteIps.size() > 0 &&(!SkipWhiteIP(command))) { 
				if (!whiteIps.contains(ip)) {
					Log.logPrintln(String.format("http request closed by white iptables, ipaddr: %s", ip));
					httpExchange.close();
					return;
				}
			}
		}
		
		//token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZXJjaGFudElkIjoieGRuYSIsInNlcnZpY2VJZCI6IlNXUnNaVkJoY21Ga2FYTmxMWEJ5YjJRIiwicGF5bWVudElkIjoiMTU5ODI2NjgzNTE5NTEwNjQiLCJ1dWlkIjoiNGFiNTM1YjAtYzc5Ni00ZDNlLTgwMTgtYjVkNDRiOTZjOTQyIiwib3JkZXJJZCI6IjQ4MWFkMDhkNzEwMjQ0MjI4ZTQ4MDFmMjY4M2Q1ZWE5Iiwib3JkZXJUaW1lIjoxNTk4MjY2ODM1MTk1LCJzdGF0dXMiOi0yLCJjdXJyZW5jeSI6IlVTRCIsIml0ZW0iOnsiaXRlbUlkIjoiMzIiLCJpdGVtTmFtZSI6IuiHs-WwiuaciOWNoSIsInF1YW50aXR5IjoxLCJ1bml0UHJpY2UiOjIwLCJ0b3RhbFByaWNlIjoyMCwiaW1hZ2VVcmwiOiJodHRwczovL3MzLWFwLW5vcnRoZWFzdC0xLmFtYXpvbmF3cy5jb20vZmlsZS5pZGxlcGFyYWRpc2UuY29tL2lkbGVwYXJhZGlzZS9pY29uL2lkbGVwYXJhZGlzZS5wbmciLCJkZXNjcmlwdGlvbiI6IjMw5YWD6LSt5Lmw5pyI5Y2hIn0sImlhdCI6MTU5ODI2Njg2OX0.J-iXUBOTYlNnGBNmMZXxfFETAL_UFlu_0tMyJUgC0fAaxh2Bq7Qbp9j5I0sepItE_DFZNzvJYiCs6onSShLjaVP_WTKPL-87eBMmlNuEv91qBzBob5kwIxoWwYDnKrStZhaWxDfMnRUEeNzyxIDKEb3K-rgTHO61vtrW1WhvG4I";
		
		if (command.contains("payNotice") && token != null )
		{
			//token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZXJjaGFudElkIjoieGRuYSIsInNlcnZpY2VJZCI6IlNXUnNaVkJoY21Ga2FYTmxMWEJ5YjJRIiwicGF5bWVudElkIjoiMTU5ODI2NjgzNTE5NTEwNjQiLCJ1dWlkIjoiNGFiNTM1YjAtYzc5Ni00ZDNlLTgwMTgtYjVkNDRiOTZjOTQyIiwib3JkZXJJZCI6IjQ4MWFkMDhkNzEwMjQ0MjI4ZTQ4MDFmMjY4M2Q1ZWE5Iiwib3JkZXJUaW1lIjoxNTk4MjY2ODM1MTk1LCJzdGF0dXMiOi0yLCJjdXJyZW5jeSI6IlVTRCIsIml0ZW0iOnsiaXRlbUlkIjoiMzIiLCJpdGVtTmFtZSI6IuiHs-WwiuaciOWNoSIsInF1YW50aXR5IjoxLCJ1bml0UHJpY2UiOjIwLCJ0b3RhbFByaWNlIjoyMCwiaW1hZ2VVcmwiOiJodHRwczovL3MzLWFwLW5vcnRoZWFzdC0xLmFtYXpvbmF3cy5jb20vZmlsZS5pZGxlcGFyYWRpc2UuY29tL2lkbGVwYXJhZGlzZS9pY29uL2lkbGVwYXJhZGlzZS5wbmciLCJkZXNjcmlwdGlvbiI6IjMw5YWD6LSt5Lmw5pyI5Y2hIn0sImlhdCI6MTU5ODI2Njg2OX0.J-iXUBOTYlNnGBNmMZXxfFETAL_UFlu_0tMyJUgC0fAaxh2Bq7Qbp9j5I0sepItE_DFZNzvJYiCs6onSShLjaVP_WTKPL-87eBMmlNuEv91qBzBob5kwIxoWwYDnKrStZhaWxDfMnRUEeNzyxIDKEb3K-rgTHO61vtrW1WhvG4I";
			try {
		        JwtClaims jwtClaims = verifyToken(token);

		        String status = jwtClaims.getClaimValue("status").toString();
		        JSONObject jsonObject = JSONObject.fromObject(params);
		        jsonObject.put("status", status);
		        params = jsonObject.toString();
		        
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
		
		// 必须使用登陆用户
		if (!SkipCheckUser(command) && !GuaJiScriptManager.getInstance().checkUser(user)) {
			String ip = httpExchange.getRemoteAddress().toString().split(":")[0].substring(1);
			result += "the user is banned : " + user;
			Log.logPrintln(String.format("http request closed by the user is banned, ipaddr: %s, user: %s,command: %s  ", ip,user,command));
			if (command != null) {
				//送正常命令才回傳
				GuaJiScript script = GuaJiScriptManager.getInstance().getScript(command);
				if (checkSysCommand(command)||(script != null)) { // 為系統命令或為script請求
					GuaJiScriptManager.sendResponse(httpExchange, result);
				}
			}
			httpExchange.close();
			// 验证用户名密码
			return;
		}
		
		// 正式版檢查MD5(已關閉使用)
		if (!command.contains("payNotice") && !GuaJiScriptManager.getInstance().getScriptConfig().isDebug()) {
			StringBuffer joinMd5 = new StringBuffer();
			joinMd5.append(params);
			joinMd5.append(MD5_KEY);
			String myMd5 = Md5.makeMD5(joinMd5.toString());
			if (sign == null || !myMd5.equals(sign)) {
				JSONObject json = new JSONObject();
				json.put("status", -1);
				json.put("msg", "illegal request, because sign verify failed !");
				GuaJiScriptManager.sendResponse(httpExchange, json.toString());
				httpExchange.close();
				return;
			}
		}

		if (command != null) {
			// 记录命令信息
			logger.info(String.format("script http request, command: %s, params: %s, user: %s, token: %s", command, params, user, token));

			if (checkSysCommand(command)) {
				if (user != null && user.equals(App.getInstance().getAppCfg().getAdmin())) {
					// 挂起脚本服务
					if ("suspend".equals(command)) {
						suspend = true;
						GuaJiScriptManager.sendResponse(httpExchange, "script handler suspend");
					}

					// 恢复脚本服务
					if ("resume".equals(command)) {
						suspend = false;
						GuaJiScriptManager.sendResponse(httpExchange, "script handler resume");
					}

					// 重新加载脚本
					if ("reload".equals(command)) {
						result += GuaJiScriptManager.getInstance().loadAllScript();
						GuaJiScriptManager.sendResponse(httpExchange, result);
					}

					// 更新逻辑服务组件
					if ("update_service".equals(command)) {
						if (ServiceManager.getInstance().update()) {
							GuaJiScriptManager.sendResponse(httpExchange, "update service successful");
						} else {
							GuaJiScriptManager.sendResponse(httpExchange, "update service failed");
						}
					}

					// 更新配置文件
					if ("update_config".equals(command)) {
						if (ConfigManager.getInstance().updateReload()) {
							GuaJiScriptManager.sendResponse(httpExchange, "update config successful");
						} else {
							GuaJiScriptManager.sendResponse(httpExchange, "update config failed");
						}
					}

					// 执行shell命令
					if ("shell".equals(command)) {
						new ScriptShellExecutor(httpExchange, params).start();
					}

					// 退出服务
					if ("shutdown".equals(command)) {
						GuaJiScriptManager.sendResponse(httpExchange, "script shutdown");
						Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
						if (paramsMap != null && paramsMap.containsKey("notify")
								&& "false".equals(paramsMap.get("notify"))) {
							GuaJiShutdownHook.getInstance().processShutdown(false);
						} else {
							GuaJiShutdownHook.getInstance().processShutdown(true);
						}
					}
				}
			} else if (!suspend) {
				GuaJiScript script = GuaJiScriptManager.getInstance().getScript(command);
				if (script != null) {
					try {
						// 先日志记录
						script.logger(user, params);

						script.action(params, httpExchange);
					} catch (Exception e) {
						MyException.catchException(e);
					}
				} else {
					result += "unkonwn command : " + command;
					GuaJiScriptManager.sendResponse(httpExchange, result);
				}
			}
		} else {
			result += "illicit command";
			GuaJiScriptManager.sendResponse(httpExchange, result);
			httpExchange.close();
		}
	}

	/**
	 * 执行shell命令
	 * 
	 * @param paramsMap
	 * @return
	 */
	public static String onShellCommand(Map<String, String> paramsMap) {
		if (paramsMap != null && paramsMap.containsKey("cmd")) {
			long timeout = -1;
			try {
				String urlCmd = paramsMap.get("cmd").replace('_', '/').replace('-', '=');
				String cmd = new String(Base64.getMimeDecoder().decode(urlCmd));
				if (paramsMap.containsKey("timeout")) {
					timeout = Integer.valueOf(paramsMap.get("timeout"));
				}
				return App.getInstance().onShellCommand(cmd, timeout);
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return null;
	}

	/**
	 * 检测是否为系统命令
	 * 
	 * @param command
	 * @return
	 */
	private boolean checkSysCommand(String command) {
		if ("shell".equals(command) || "suspend".equals(command) || "resume".equals(command) || "reload".equals(command)
				|| "shutdown".equals(command) || "update_service".equals(command) || "update_config".equals(command)) {
			return true;
		}
		return false;
	}
	

    /**
     * 验证jwt
     * @param token
     * @return
     * @throws Exception
     */
    private JwtClaims verifyToken(String token) throws Exception {
 
        try {
        	
        	// sanbox
        	String key = "-----BEGIN PUBLIC KEY-----\n" + 
        			"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCImSsx+O/9L/raiN/Fvvo04+L4\n" +
        			"kzwc7xM5iVbineVQ4LnIF9io1jQLhEQeay6zQZ6CtZ+agTgxZ8lKeqPV6vJFKc2H\n" +
        			"/ExHGE5cAEGccenDewnb4B7WfHNXWclwhFuWxSpE16fQ6dNw3HMTerI+ImSiuhNj\n" +
        			"YCoX1+jpN6QTDGsqrwIDAQAB\n"+
        			"-----END PUBLIC KEY-----";
            PublicKey publicKey = getPublicKey(key);
            JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                    //.setRequireExpirationTime()
                    .setDisableRequireSignature()
                    .setSkipSignatureVerification()
                    //.setVerificationKey(publicKey)
                    //.setExpectedAudience("status")//用于验证签名是否合法，可以设置多个，且可设置必须存在项，如果jwt中不包含这些内容则不通过
                    //.setRequireIssuedAt()
                    //.setRequireNotBefore()
                    //.setSkipDefaultAudienceValidation()
                    //.setEnableLiberalContentTypeHandling()
                    //.setDisableRequireSignature()
                    .build();
 
            return jwtConsumer.processToClaims(token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }    
    /**
     * 获取PublicKey对象
     * @param publicKeyBase64
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private PublicKey getPublicKey(String publicKeyBase64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String pem = publicKeyBase64
                .replaceAll("\\-*BEGIN.*KEY\\-*", "")
                .replaceAll("\\-*END.*KEY\\-*", "");

        byte[] keyBytes;
		Decoder decoder=Base64.getMimeDecoder();
		keyBytes = decoder.decode(pem);
		X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
 
		PublicKey publicKey = keyFactory.generatePublic(pubKeySpec);
		System.out.println(publicKey);
		return publicKey;
		
    }
    
    /**
     * 	客戶端用命令(略過帳號檢查)
     * @param command
     * @return
     */
    private boolean SkipCheckUser(String command) {
    	if (command.contains("bulletinInfo") || command.contains("payNotice") || command.contains("recordStep")) {
    		return true;
    	}
    	return false;
    }
    /**
     * 	略過白名單的命令
     * @param command
     * @return
     */
    private boolean SkipWhiteIP(String command) {
    	if (command.contains("payNotice") || command.contains("recordStep")) {
    		return true;
    	}
    	return false;
    }
}
