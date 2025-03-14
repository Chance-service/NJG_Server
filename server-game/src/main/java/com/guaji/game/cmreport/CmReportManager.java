package com.guaji.game.cmreport;

import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.guaji.log.Log;
import org.guaji.os.MyException;
import org.guaji.os.OSOperator;
import org.guaji.os.GuaJiTime;

import com.guaji.game.GsApp;
import com.guaji.game.GsConfig;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.player.Player;

/**
 * 猎豹移动服务
 */
public class CmReportManager {
	/**
	 * http对象
	 */
	private HttpClient httpClient = null;
	private GetMethod getMethod = null;
	private static final String cmPath = "/1/api/offerwall/active";
	private static final String cmQuery = "client_id=111463152&uid=%s&name=%s&rmac=%s&rip=%s&uip=%s&idfa=%s&mac=%s&udid=%s&route=%s&device=%s&time=%d";
	
	/**
	 * 全局静态对象
	 */
	private static CmReportManager instance = null;

	/**
	 * 获取全局静态对象
	 * 
	 * @return
	 */
	public static CmReportManager getInstance() {
		if (instance == null) {
			instance = new CmReportManager();
		}
		return instance;
	}
	
	public boolean init() {
		if ("appstore".equals(GsConfig.getInstance().getPlatform()) && SysBasicCfg.getInstance().getCmHost().length() > 0) {
			if (httpClient == null) {
				httpClient = new HttpClient();
				httpClient.getHttpConnectionManager().getParams().setSoTimeout(SysBasicCfg.getInstance().getCmTimeout());
				httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(SysBasicCfg.getInstance().getCmTimeout());
			}
	
			if (getMethod == null) {
				getMethod = new GetMethod(SysBasicCfg.getInstance().getCmHost());
			}
		}
		return true;
	}
	
	public synchronized boolean reportCmActivePlayer(Player player, boolean direct) {
		if (httpClient != null && getMethod != null) {
			if (!direct) {
				return GsApp.getInstance().postCommonTask(new CmReportTask(player), GsApp.getInstance().getThreadNum()-1);
			} else {
				// 每次设置超时, 能动态修改
				httpClient.getHttpConnectionManager().getParams().setSoTimeout(SysBasicCfg.getInstance().getCmTimeout());
				httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(SysBasicCfg.getInstance().getCmTimeout());
				
				String rmac = "";
				String rip = "";
				String route = "";
				try {
					String items[] = player.getRouteInfo().split("&");
					if (items != null) {
						if (items.length > 0) {
							rmac = items[0];
						}
						
						if (items.length > 1) {
							rip = items[1];
						}
						
						if (items.length > 2) {
							route = items[2];
						}
					}
					
					String idfa = player.getDevice();
					idfa = idfa.replace("-", "");
					
					String uid = player.getPuid();
					if (uid.indexOf("_") > 0) {
						items = uid.split("_");
						if (items != null && items.length > 0) {
							uid = items[items.length - 1];
						}
					}
					
					String queryParam = String.format(cmQuery, 
							URLEncoder.encode(uid, "UTF-8"), 
							URLEncoder.encode(player.getName(), "UTF-8"),
							URLEncoder.encode(rmac, "UTF-8"), 
							URLEncoder.encode(rip, "UTF-8"), 
							URLEncoder.encode(player.getIp(), "UTF-8"), 
							URLEncoder.encode(idfa, "UTF-8"), 
							URLEncoder.encode(player.getDeviceMac(), "UTF-8"), "", 
							URLEncoder.encode(route, "UTF-8"), 
							URLEncoder.encode(player.getDeviceName(), "UTF-8"), 
							GuaJiTime.getSeconds());

					int status = 0;
					for (int i=0; i<3; i++) {
						try {
							getMethod.setPath(cmPath);
							getMethod.setQueryString(queryParam);					
							status = httpClient.executeMethod(getMethod);
							break;
						} catch (Exception e) {
							MyException.catchException(e);
							OSOperator.osSleep(100);
						}
					}
					
					String response = getMethod.getResponseBodyAsString();
					if (status == HttpStatus.SC_OK) {
						Log.logPrintln("cm_report succ: " + cmPath + "?" + queryParam + ", response: " + response);
					} else {
						Log.logPrintln("cm_report failed: " + cmPath + "?" + queryParam + ", response: " + response);
					}
					
					if (player.getPlayerData() != null && player.getPlayerData().getStateEntity() != null) {
						player.getPlayerData().getStateEntity().setPlatformData(response);
						player.getPlayerData().getStateEntity().notifyUpdate(true);
					}
				} catch (Exception e) {
					MyException.catchException(e);
				}
				return true;
			}
		}
		return false;
	}
}
