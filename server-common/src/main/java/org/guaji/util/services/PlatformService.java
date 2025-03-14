package org.guaji.util.services;

import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.guaji.log.Log;
import org.guaji.os.MyException;

public class PlatformService {
	
	/**
	 * 服务器信息
	 */
	private String gameName = "";
	private String platform = "";
	private String serverId = "";
	
	/**
	 * http对象
	 */
	private HttpClient httpClient = null;
	private GetMethod  getMethod  = null;
	
	/**
	 * platform服务参数串格式
	 */
	private static final String usePath  = "RedisServer/ServerScoreUp";
	private static final String usePath2 = "RedisServer/ServerScoreDown";
	private static final String useQuery = "serverId=%s&score=%d";

	/**
	 * 实例对象
	 */
	private static PlatformService instance = null;

	/**
	 * 获取全局实例对象
	 * 
	 * @return
	 */
	public static PlatformService getInstance() {
		if (instance == null) {
			instance = new PlatformService();
		}
		return instance;
	}

	/**
	 * 构造函数
	 */
	private PlatformService() {
		httpClient = null;
		getMethod = null;
	}

	/**
	 * 初始化platform服务
	 * 
	 * @return
	 */
	public boolean install(String gameName, String platform, String serverId, String host, int timeout) {
		try {
			this.gameName = gameName;
			this.platform = platform;
			this.serverId = serverId;
			
			if (httpClient == null) {
				httpClient = new HttpClient();
				httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
				httpClient.getHttpConnectionManager().getParams().setSoTimeout(timeout);
			}
	
			if (getMethod == null) {
				getMethod = new GetMethod(host);
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}

		if (httpClient == null || getMethod == null) {
			Log.errPrintln("install platform service failed.");
			return false;
		}

		return true;
	}
	
	/**
	 * 上传服务器积分
	 * @return
	 */
	
	public synchronized int upServerActivityScore(int score) {
		if (httpClient != null && getMethod != null) {
			String queryParam = String.format(useQuery, this.serverId,score);
			try {
				queryParam = URLEncoder.encode(queryParam, "UTF-8");
				
			} catch (Exception e) {
				MyException.catchException(e);
			}

			getMethod.setPath(usePath);
			getMethod.setQueryString(queryParam);
			try {
				int statusCode = httpClient.executeMethod(getMethod);
				if (statusCode == HttpStatus.SC_OK) {
					String response = getMethod.getResponseBodyAsString();
					String[] s = response.split("|");
					
					if(s.length != 2)
					{
						return 0;
					}
					else
					{
						if(Integer.valueOf(s[0]) == 1)
						{
							return 1;
						}
						else
						{
							return 0;
						}
					}
					
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return 0;
	}
	
	/**
	 * 获取服务器总积分
	 * @return
	 */
	
	public synchronized int getServerActivityScore() {
		if (httpClient != null && getMethod != null) {

			getMethod.setPath(usePath2);
			getMethod.setQueryString("");
			try {
				int statusCode = httpClient.executeMethod(getMethod);
				if (statusCode == HttpStatus.SC_OK) 
				{
					String response = getMethod.getResponseBodyAsString();

					return Integer.valueOf(response); 
					
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return 0;
	}
	
	
}
