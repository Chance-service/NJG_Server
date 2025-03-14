package com.guaji.game.momo;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.guaji.log.Log;
import org.guaji.os.MyException;
import org.guaji.security.HttpsSocketFactory;

import net.sf.json.JSONObject;

public class MomoManager {
	/** 单次数据更新的最大条数 */
	private static final int MAX_PER_UPDATE_SIZE = 100;
	/**
	 * 陌陌平台参数
	 */
	static String momoHost = "https://game-api.immomo.com";
	static String loginCheckPath = "/game/2/server/app/check";
	static String pushOneMsgPath = "/game/2/server/msg/one";
	static String pushAllMsgPath = "/game/2/server/msg/all";
	static String updateScorePath = "/game/2/server/rank/update";
	static String resetScorePath = "/game/2/server/rank/reset";
	static String getTotalRankPath = "/game/2/server/rank/total";

	private String momoAppid = "ex_gtgj_dKg5XR";
	private String momoAppsecret = "DDCEA7EA-F75E-FDC6-0DE4-004002FB2BCE";

	/**
	 * http对象
	 */
	private HttpClient httpClient = null;
	private GetMethod getMethod = null;

	/**
	 * 全局实例对象
	 */
	private static MomoManager instance = null;

	public static MomoManager getInstance() {
		if (instance == null) {
			instance = new MomoManager();
		}
		return instance;
	}

	/**
	 * 构造
	 */
	private MomoManager() {
		// FIXME: crazyjohn 跟hawk确认初始化位置(timeout = 10s);
		init(momoAppid, momoAppsecret, 10 * 1000);
		// 模拟数据上传(1s延迟, 每隔1m一次)
		startUpdateScoreSimulator(1, 60);
	}

	// 模拟更新数据;
	private void startUpdateScoreSimulator(long initialDelay, long period) {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		final AtomicInteger counter = new AtomicInteger(0);
		final String SCORE_TYPE = "1001";
		// 上传分数;
		executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				List<MomoScore> scores = new ArrayList<MomoScore>();
				for (int i = 0; i < 1; i++) {
					MomoScore score = new MomoScore();
					score.setPuid("WE52Z05kUHpPbGFhdHdCQWtSSDNBQT09");
					score.setScore(counter.incrementAndGet());
					score.setScoreType(SCORE_TYPE);
					scores.add(score);
				}
				boolean result = updateScore(scores);
				if (result) {
					// 获取分数
					getTotalRank(SCORE_TYPE, 10);
				}
			}
		}, initialDelay, period, TimeUnit.SECONDS);
	}

	/**
	 * 拆分出陌陌用户id
	 * 
	 * @param puid
	 * @return
	 */
	public static String getMomoUserId(String puid) {
		if (puid.startsWith("momo_")) {
			return puid.substring(5);
		}
		return puid;
	}

	/**
	 * 初始化平台参数
	 * 
	 * @param momoAppid
	 * @param momoAppsecret
	 * @param timeout
	 * @return
	 */
	public boolean init(String momoAppid, String momoAppsecret, int timeout) {
		if (momoAppid != null && momoAppid.length() > 0) {
			this.momoAppid = momoAppid;
		}

		if (momoAppsecret != null && momoAppsecret.length() > 0) {
			this.momoAppsecret = momoAppsecret;
		}

		if (momoAppid != null && momoAppid.length() > 0) {
			if (httpClient == null) {
				httpClient = new HttpClient();
				httpClient.getHttpConnectionManager().getParams().setSoTimeout(timeout);
				httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);

				HttpsSocketFactory.install();
			}

			if (getMethod == null) {
				getMethod = new GetMethod(momoHost);
			}
		}
		return true;
	}

	public String getMomoHost() {
		return momoHost;
	}

	public String getMomoAppid() {
		return momoAppid;
	}

	public String getMomoAppsecret() {
		return momoAppsecret;
	}

	/**
	 * 向陌陌平台请求
	 * 
	 * @param path
	 * @param params
	 * @return
	 */
	protected synchronized JSONObject doRequest(String path, String params) {
		try {
			int status = 0;
			try {
				getMethod.setPath(path);
				getMethod.setQueryString(params);
				status = httpClient.executeMethod(getMethod);
			} catch (Exception e) {
				MyException.catchException(e);
			}

			String response = getMethod.getResponseBodyAsString();
			// debug log
			Log.debugPrintln(response);
			if (status == HttpStatus.SC_OK) {
				Log.logPrintln("momoapi succ: " + loginCheckPath + "?" + params + ", response: " + response);
				return JSONObject.fromObject(response);
			} else {
				Log.logPrintln("momoapi failed: " + loginCheckPath + "?" + params + ", response: " + response);
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return null;
	}

	public boolean doLoginCheck(String vtoken, String puid) {
		try {
			String queryParam = String.format("appid=%s&app_secret=%s&vtoken=%s&userid=%s", URLEncoder.encode(momoAppid, "UTF-8"),
					URLEncoder.encode(momoAppsecret, "UTF-8"), URLEncoder.encode(vtoken, "UTF-8"), URLEncoder.encode(getMomoUserId(puid), "UTF-8"));

			JSONObject json = doRequest(loginCheckPath, queryParam);
			if (json != null) {
				return (Integer) (json.get("ec")) == 0;
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return false;
	}

	public boolean getTotalRank(String scoreType, int count) {
		try {
			String queryParam = String.format("appid=%s&app_secret=%s&score_type=%s&count=%s", URLEncoder.encode(momoAppid, "UTF-8"),
					URLEncoder.encode(momoAppsecret, "UTF-8"), URLEncoder.encode(scoreType, "UTF-8"), URLEncoder.encode(Integer.toString(count), "UTF-8"));

			JSONObject json = doRequest(getTotalRankPath, queryParam);
			if (json != null) {
				return (Integer) (json.get("ec")) == 0;
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return false;
	}

	public boolean updateScore(List<MomoScore> scores) {
		try {
			if (scores == null || scores.size() <= 0) {
				return false;
			}
			// 单次最多条数检查
			if (scores.size() > MAX_PER_UPDATE_SIZE) {
				Log.logPrintln(String.format("Update score error: scores count can not more than: %d, but current count is: %d", MAX_PER_UPDATE_SIZE,
						scores.size()));
			}
			StringBuilder data = new StringBuilder(1024);
			data.append('[');
			for (int i = 0; i < scores.size(); i++) {
				if (i > 0) {
					data.append(',');
				}
				data.append(scores.get(i).toString());
			}
			data.append(']');

			String queryParam = String.format("appid=%s&app_secret=%s&data=%s", URLEncoder.encode(momoAppid, "UTF-8"),
					URLEncoder.encode(momoAppsecret, "UTF-8"), URLEncoder.encode(data.toString(), "UTF-8"));

			JSONObject json = doRequest(updateScorePath, queryParam);
			if (json != null) {
				return (Integer) (json.get("ec")) == 0;
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return false;
	}

	public boolean resetScoreRank(String scoreType) {
		try {
			String queryParam = String.format("appid=%s&app_secret=%s&score_type=%s", URLEncoder.encode(momoAppid, "UTF-8"),
					URLEncoder.encode(momoAppsecret, "UTF-8"), URLEncoder.encode(scoreType, "UTF-8"));

			JSONObject json = doRequest(resetScorePath, queryParam);
			if (json != null) {
				return (Integer) (json.get("ec")) == 0;
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return false;
	}

	public boolean pushMsg(String content, String puid) {
		try {
			String queryParam = String.format("appid=%s&app_secret=%s&content=%s&userid=%s", URLEncoder.encode(momoAppid, "UTF-8"),
					URLEncoder.encode(momoAppsecret, "UTF-8"), URLEncoder.encode(content, "UTF-8"), URLEncoder.encode(getMomoUserId(puid), "UTF-8"));

			JSONObject json = doRequest(pushOneMsgPath, queryParam);
			if (json != null) {
				return (Integer) (json.get("ec")) == 0;
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return false;
	}

	public boolean broadcastMsg(String content) {
		try {
			String queryParam = String.format("appid=%s&app_secret=%s&content=%s", URLEncoder.encode(momoAppid, "UTF-8"),
					URLEncoder.encode(momoAppsecret, "UTF-8"), URLEncoder.encode(content, "UTF-8"));

			JSONObject json = doRequest(pushAllMsgPath, queryParam);
			if (json != null) {
				return (Integer) (json.get("ec")) == 0;
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return false;
	}
}
