package org.guaji.util.services;

import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.guaji.log.Log;
import org.guaji.os.MyException;
import org.guaji.os.OSOperator;

public class CdkService {
	/**
	 * 前端访问状态码
	 */
	public static final int CDK_STATUS_OK = 0; // 可使用
	public static final int CDK_STATUS_NONEXIST = 1; // 不存在
	public static final int CDK_STATUS_FUTURE = 2; // 时间未到
	public static final int CDK_STATUS_PAST = 3; // 已过期
	public static final int CDK_STATUS_USED = 4; // 已使用
	public static final int CDK_STATUS_PLAT = 5; // 平台不正确
	public static final int CDK_STATUS_TYPE_MULTI = 6; // 已使用过相关类型
	public static final int CDK_STATUS_USEDUNIQUE = 7; // 已经使用过唯一吗不能使用统一码
	public static final int CDK_STATUS_USEUNITE = 8; // 已经使用过统一码不能使用唯一码

	/**
	 * 后台操作错误码
	 */
	public static final int CDK_TYPE_EXIST = 10; // CDK类型已存在
	public static final int CDK_PARAM_ERROR = 11; // 参数错误
	public static final int CDK_TYPE_NONEXIST = 12; // 类型不存在

	/**
	 * 常量定义
	 */
	public static final int CDK_TOTAL_LENGTH = 10; // CDK长度
	public static final int CDK_DIGIT_MIN_COUNT = 3; // 数字最少个数
	public static final int CDK_CHAR_MIN_COUNT = 3; // 字符最少个数
	public static final int CDK_HEADER_SIZE = 2; // cdk标记字节
	public static final int CDK_NAMT_TYPE_LEN = 8; // 名字和类型占用的长度
	public static final int CDK_TOTAL_UNICODE16_LENGTH = 16; // 16位的唯一码
	public static final int CDK_TOTAL_UNICODE08_LENGTH = 8; // 8位的唯一码

	/**
	 * 服务器信息
	 */
	private String gameName = "";
	private String platform = "";
	private String serverId = "";
	private String token = "";

	/**
	 * http对象
	 */
	private HttpClient httpClient = null;
	private GetMethod getMethod = null;

	/**
	 * cdk服务参数串格式
	 */
	private static final String usePath = "/use_cdk";
	private static final String useQuery = "playerid=%d&puid=%s&playername=%s&cdk=%s";

	/**
	 * 实例对象
	 */
	private static CdkService instance = null;

	/**
	 * 获取全局实例对象
	 * 
	 * @return
	 */
	public static CdkService getInstance() {
		if (instance == null) {
			instance = new CdkService();
		}
		return instance;
	}

	/**
	 * 构造函数
	 */
	private CdkService() {
		httpClient = null;
		getMethod = null;
	}

	/**
	 * 初始化cdk服务
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
			Log.errPrintln("install cdk service failed.");
			return false;
		}

		return true;
	}

	/**
	 * 设置校验令牌
	 * 
	 * @param token
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * 从cdk读取游戏名
	 * 
	 * @param cdk
	 * @return
	 */
	public static String getGameNameFromCdk(String cdk) {
		if (cdk.length() == CDK_TOTAL_LENGTH) {
			char[] cdkGameName = { cdk.charAt(1), cdk.charAt(3) };
			return String.valueOf(cdkGameName);
		}
		return "";
	}

	/**
	 * 从cdk读取游戏名
	 * 
	 * @param cdk
	 * @return
	 */
	public static String getTypeNameFromCdk(String cdk) {
		if (cdk.length() == CDK_TOTAL_LENGTH) {
			char[] cdkTypeName = { cdk.charAt(5), cdk.charAt(7) };
			return String.valueOf(cdkTypeName);
		}
		return "";
	}

	/**
	 * 同类型限制多次使用
	 * 
	 * @param type
	 * @return
	 */
	public static boolean typeLimitMultiUse(String type) {
		return true;
	}

	/**
	 * 使用CDK, 外部需要先判断是否已使用过同类型的CDK
	 * 
	 * @param cdk
	 * @return status {0: ok}
	 */
	public synchronized int useCdk(String puid, int playerid, String playername, String serverId, String cdk,
			StringBuffer rewardRef) {
		if ((httpClient != null && getMethod != null && cdk != null)
				&& (cdk.length() == CDK_TOTAL_UNICODE16_LENGTH || cdk.length() == CDK_TOTAL_UNICODE08_LENGTH)) {
			String queryParam = String.format(useQuery, playerid, puid, playername, cdk);
			try {
				queryParam = URLEncoder.encode(queryParam, "UTF-8");

				// 添加令牌校验
				if (token != null && token.length() > 0) {
					queryParam += URLEncoder.encode("&token=" + token, "UTF-8");
				}

			} catch (Exception e) {
				MyException.catchException(e);
			}

			getMethod.setPath(usePath);
			getMethod.setQueryString(queryParam);
			try {
				int statusCode = httpClient.executeMethod(getMethod);
				if (statusCode == HttpStatus.SC_OK) {
					String response = getMethod.getResponseBodyAsString();
					Map<String, String> respMap = OSOperator.jsonToMap(response);
					int status = CDK_STATUS_NONEXIST;
					if (respMap.containsKey("status")) {
						status = Integer.valueOf(respMap.get("status"));
					}

					if (status == CDK_STATUS_OK && respMap.containsKey("reward")) {
						rewardRef.append(respMap.get("reward"));
					}
					return status;
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return CDK_STATUS_NONEXIST;
	}
}
