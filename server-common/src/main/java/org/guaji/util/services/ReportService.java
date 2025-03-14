package org.guaji.util.services;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.guaji.app.App;
import org.guaji.app.AppCfg;
import org.guaji.log.Log;
import org.guaji.os.MyException;
import org.guaji.os.OSOperator;
import org.guaji.os.GuaJiTime;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.util.GuaJiTickable;
import org.guaji.zmq.GuaJiZmq;
import org.guaji.zmq.GuaJiZmqManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据上报服务
 */
public class ReportService extends GuaJiTickable {
	/**
	 * 调试日志对象
	 */
	static Logger reportLogger = LoggerFactory.getLogger("Report");

	/**
	 * 钻石类型定义
	 */
	public static int REPORT_GOLD_TYPE_PAY = 1;
	public static int REPORT_GOLD_TYPE_SYS = 2;
	
	/**
	 * 钻石改变类型(1: 增加, 2: 减少)
	 */
	public static int REPORT_GOLD_CHANGE_INC = 1;
	public static int REPORT_GOLD_CHANGE_DEC = 2;	

	/**
	 * 上报注册数据
	 */
	public static class RegisterData {
		public int birthServerId;
		public String puid;
		public String device;
		public String deviceInfo;
		public int playerId;
		public String time;

		public RegisterData() {
		}

		public RegisterData(int birthServerId,String puid, String device,String deviceInfo, int playerId, String time) {
			this.birthServerId = birthServerId;
			this.puid = puid;
			this.device = device;
			this.deviceInfo = deviceInfo;
			this.playerId = playerId;
			this.time = time;
		}
	}

	/**
	 * 上报登陆数据
	 */
	public static class LoginData {
		public int birthServerId;
		/**动作 login,logout*/
		public String action="";
		public String puid;
		public String device;
		public String deviceInfo;
		public int playerId;
		public int playerLevel;
		public int vipLevel;
		/**在线时长*/
		public int period;
		public String time;

		public LoginData(int birthServerId, String action, String puid, String device,String deviceInfo, int playerId,int playerLevel,int vipLevel,int period, String time) {
			this.birthServerId = birthServerId;
			this.action = action;
			this.puid = puid;
			this.device = device;
			this.deviceInfo = deviceInfo;
			this.playerId = playerId;
			this.playerLevel = playerLevel;
			this.vipLevel = vipLevel;
			this.period = period;
			this.time = time;
		}
	}

	/**
	 * 上报充值数据
	 */
	public static class RechargeData {
		public int birthServerId;
		public String puid;
		public String device;
		public String deviceInfo;
		public int playerId;
		public String playerName;
		public int playerLevel;
		public int vipLevel;
		public String orderId;
		public String productId;
		public float payMoney;
		public String currency;
		public String time;

		public RechargeData(int birthServerId,String puid, String device,String deviceInfo, int playerId, String playerName, int playerLevel,int vipLevel, String orderId, String productId, float payMoney, String currency, String time) {
			this.birthServerId = birthServerId;
			this.puid = puid;
			this.device = device;
			this.deviceInfo = deviceInfo;
			this.playerId = playerId;
			this.playerName = playerName;
			this.playerLevel = playerLevel;
			this.vipLevel = vipLevel;
			this.orderId = orderId;
			this.productId = productId;
			this.payMoney = payMoney;
			this.currency = currency;
			this.time = time;
		}
		
	}

	/**
	 * 上报钻石数据
	 */
	public static class GoldData {
		public String serverId;
		public String puid;
		public String device;
		public int playerId;
		public int playerLevel;
		public int changeType;
		public String changeAction;
		public int goldType;
		public int gold;
		public String time;

		public GoldData() {
			this.time = GuaJiTime.getTimeString();
		}

		public GoldData(String serverId,String puid, String device, int playerId, int playerLevel, int changeType, String changeAction, int goldType, int gold, String time) {
			this.serverId = serverId;
			this.puid = puid;
			this.device = device;
			this.playerId = playerId;
			this.playerLevel = playerLevel;
			this.changeType = changeType;
			this.changeAction = changeAction;
			this.goldType = goldType;
			this.gold = gold;
			this.time = time;
		}

	}

	/**
	 * 上报新手指引数据
	 */
	public static class TutorialData {
		public int birthServerId;
		public String puid;
		public String device;
		public String deviceInfo;
		public int playerId;
		public int playerLevel;
		public int vipLevel;
		public int guidId;
		public int step;
		public String time;


		public TutorialData(int birthServerId, String puid, String device, String deviceInfo, int playerId, int playerLevel, int vipLevel, int guidId, int step, String time) {
			this.birthServerId = birthServerId;
			this.puid = puid;
			this.device = device;
			this.deviceInfo = deviceInfo;
			this.playerId = playerId;
			this.playerLevel = playerLevel;
			this.vipLevel = vipLevel;
			this.guidId = guidId;
			this.step = step;
			this.time = time;
		}
	}
	
	/**
	 * 上报服务器在线数据
	 */
	public static class ServerData {
		/**当前在线人数*/
		public int curPlayers;
		/**最高在线人数*/
		public int peakPlayers;

		public ServerData(int curPlayers, int peakPlayers) {
			this.curPlayers = curPlayers;
			this.peakPlayers = peakPlayers;
		}
	}

	/**
	 * 上报通用数据
	 */
	public static class CommonData {
		public String puid;
		public String device;
		public int playerId;
		public String time;
		public List<String> args;

		public CommonData() {
		}

		public CommonData(String puid, String device, int playerId, String time) {
			this.puid = puid;
			this.device = device;
			this.playerId = playerId;
			this.time = time;
		}

		public void setArgs(String... args) {
			if (args != null) {
				if (this.args == null) {
					this.args = new ArrayList<String>(args.length);
				}

				for (String arg : args) {
					if (arg.length() > 0) {
						this.args.add(arg);
					}
				}
			}
		}
	}

	private static final String registerPath = "/report_register";
	private static final String loginPath = "/report_login";
	private static final String rechargePath = "/report_recharge";
	private static final String goldPath = "/report_gold";
	private static final String tutorialPath = "/report_tutorial";
	private static final String serverPath = "/report_server";
	private static final String commonPath = "/report_data";
	private static final String fetchIpPath = "/fetch_myip";
	
	// 所有的query都能添加token作为服务器校验令牌
	private static final String rechargeQuery = "game=%s&curserver=%d&birthserver=%d&platform=%s&puid=%s&device=%s&deviceinfo=%s&playerid=%d&playername=%s&playerlevel=%d&viplevel=%d&orderid=%s&productid=%s&pay=%f&currency=%s&time=%s";
	private static final String tutorialQuery = "game=%s&curserver=%d&birthserver=%d&platform=%s&puid=%s&device=%s&deviceinfo=%s&playerid=%d&playerlevel=%d&viplevel=%d&guidid=%d&step=%d&time=%s";
	private static final String registerQuery = "game=%s&curserver=%d&birthserver=%d&platform=%s&puid=%s&device=%s&deviceinfo=%s&playerid=%d&time=%s";
	private static final String loginQuery = "game=%s&curserver=%d&birthserver=%d&action=%s&platform=%s&puid=%s&device=%s&deviceinfo=%s&playerid=%d&playerlevel=%d&viplevel=%d&period=%d&time=%s";
	private static final String serverQuery = "game=%s&curserver=%d&platform=%s&curplayers=%d&peakplayers=%d&time=%s";
	private static final String goldQuery = "game=%s&platform=%s&server=%s&puid=%s&device=%s&playerid=%d&playerlevel=%d&changetype=%d&changeaction=%s&goldtype=%d&gold=%d&time=%s";
	private static final String commonQuery = "game=%s&platform=%s&server=%s&puid=%s&device=%s&playerid=%d&time=%s";

	/**
	 * 服务器信息
	 */
	private String myHostIp = "";
	private String gameName = "";
	private String platform = "";
	private int curServerId;
	private int retryTimes = 1;
	private String token = "";
	
	/**
	 * http对象
	 */
	private String serviceHost = "";
	private HttpClient httpClient = null;
	private GetMethod getMethod = null;

	/**
	 * zmq对象
	 */
	private GuaJiZmq reportZmq = null;

	/**
	 * 汇报数据
	 */
	private Lock reportLock = null;
	List<Object> reportDatas = null;

	/**
	 * 实例对象
	 */
	private static ReportService instance = null;

	/**
	 * 获取全局实例对象
	 * 
	 * @return
	 */
	public static ReportService getInstance() {
		if (instance == null) {
			instance = new ReportService();
		}
		return instance;
	}

	/**
	 * 构造函数
	 */
	private ReportService() {
		httpClient = null;
		getMethod = null;
		reportLock = new ReentrantLock();
		reportDatas = new LinkedList<Object>();

		if (App.getInstance() != null) {
			App.getInstance().addTickable(this);
		}
	}


	/**
	 * 初始化cdk服务
	 * 
	 * @return
	 */
	public boolean install(String gameName, String platform, String serverId, String host, int timeout) {
		return install(gameName, platform, serverId, host, timeout, App.getInstance().getAppCfg());
	}
	
	/**
	 * 初始化cdk服务
	 * 
	 * @return
	 */
	public boolean install(String gameName, String platform, String serverId, String host, int timeout, AppCfg appCfg) {
		try {
			this.gameName = gameName;
			this.platform = platform;
			this.curServerId = Integer.parseInt(serverId);
			this.serviceHost = host;

			// 可重复调用
			GuaJiZmqManager.getInstance().init(GuaJiZmq.HZMQ_CONTEXT_THREAD);
			
			if (host.indexOf("tcp://") >= 0) {
				if (!createReportZmq(host)) {
					return false;
				}
			} else {
				if (httpClient == null) {
					httpClient = new HttpClient();
					httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
					httpClient.getHttpConnectionManager().getParams().setSoTimeout(timeout);
				}

				if (getMethod == null) {
					getMethod = new GetMethod(host);
				}

				if (appCfg == null) {
					appCfg = App.getInstance().getAppCfg();
				}

				if (appCfg != null) {
					initInnerService(appCfg);
				}
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}

		if (!isValid()) {
			Log.errPrintln("install report service failed.");
			return false;
		}

		return true;
	}

	/**
	 * 初始化zmq并上报服务器信息
	 */
	protected boolean initInnerService(AppCfg appCfg) {
		int reportZmqPort = 0;
		myHostIp = appCfg.getHostIp();
		String reportInfo = fetchReportInfo();
		try {
			if (reportInfo != null && reportInfo.length() > 0) {
				reportLogger.info("report service info: " + reportInfo);

				JSONObject jsonObject = JSONObject.fromObject(reportInfo);
				if (jsonObject.containsKey("myIp")) {
					myHostIp = (String) jsonObject.get("myIp");
				}

				if (jsonObject.containsKey("zmqPort")) {
					reportZmqPort = (Integer) jsonObject.get("zmqPort");
				}
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}

		// 创建zmq连接
		if (reportZmqPort > 0) {
			try {
				String zmqHost = serviceHost.toLowerCase().replace("http:", "tcp:");
				int pos = zmqHost.indexOf(":", 6);
				if (pos > 0) {
					zmqHost = zmqHost.substring(0, pos + 1);
					zmqHost += reportZmqPort;
				}

				if (createReportZmq(zmqHost)) {
					reportLogger.info("create report zmq service success: " + zmqHost);
				} else {
					reportLogger.info("create report zmq service failed: " + zmqHost);
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}

		// 上报服务器信息
		int scriptHttpPort = 0;
		if (GuaJiScriptManager.getInstance() != null && GuaJiScriptManager.getInstance().getScriptConfig() != null) {
			scriptHttpPort = GuaJiScriptManager.getInstance().getScriptConfig().getHttpPort();
		}
		
//		String userDir = System.getProperty("user.dir");
//		userDir = userDir.replace('/', '+').replace('\\', '+');
//		String queryParam = String.format(serverQuery, gameName, platform, curServerId, myHostIp, userDir,
//				appCfg.getAcceptorPort(), scriptHttpPort, 
//				appCfg.getDbConnUrl(), appCfg.getDbUserName(), appCfg.getDbPassWord());
//
//		try {
//			queryParam = URLEncoder.encode(queryParam, "UTF-8");
//			getMethod.setPath(serverPath);
//			getMethod.setQueryString(queryParam);
//			httpClient.executeMethod(getMethod);
//			reportLogger.info("report server info success: " + serverPath + "?" + queryParam);
//		} catch (Exception e) {
//			reportLogger.info("report server info failed: " + serverPath + "?" + queryParam);
//		}

		return true;
	}

	/**
	 * 创建zmq对象
	 * 
	 * @param addr
	 * @return
	 */
	protected boolean createReportZmq(String addr) {
		if (reportZmq == null) {
			reportZmq = GuaJiZmqManager.getInstance().createZmq(GuaJiZmq.ZmqType.PUSH);
			if (!reportZmq.connect(addr)) {
				reportZmq = null;
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取上报服务器信息
	 * 
	 * @return
	 */
	protected String fetchReportInfo() {
		try {
			getMethod.setPath(fetchIpPath);
			int status = httpClient.executeMethod(getMethod);
			if (status == HttpStatus.SC_OK) {
				return new String(getMethod.getResponseBody());
			}
		} catch (Exception e) {
		}
		return "";
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
	 * 上报服务是否有效
	 * 
	 * @return
	 */
	public boolean isValid() {
		if (reportZmq == null && (httpClient == null || getMethod == null)) {
			return false;
		}
		return true;
	}

	/**
	 * 获取重试次数
	 * 
	 * @return
	 */
	public int getRetryTimes() {
		return retryTimes;
	}

	/**
	 * 设置重试次数
	 * 
	 * @param retryTimes
	 */
	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}

	/**
	 * 执行http请求
	 * 
	 * @param path
	 * @param params
	 * @return
	 */
	public synchronized int executeMethod(String path, String params) {
		if (token != null && token.length() > 0) {
			try {
				params += URLEncoder.encode("&token=" + token, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				MyException.catchException(e);
			}
		}
		
		if (reportZmq != null) {
			try {
				params = URLDecoder.decode(params, "UTF-8");
				if (!reportZmq.send(path.getBytes(), GuaJiZmq.HZMQ_SNDMORE)) {
					return -1;
				}

				if (!reportZmq.send(params.getBytes(), 0)) {
					return -1;
				}

				return 0;
			} catch (Exception e) {
				MyException.catchException(e);
			}
		} else if (getMethod != null) {
			getMethod.setPath(path);
			if (params != null && params.length() > 0) {
				getMethod.setQueryString(params);
			}

			for (int i = 0; i < retryTimes; i++) {
				try {
					return httpClient.executeMethod(getMethod);
				} catch (Exception e) {
					// MyException.catchException(e);
					OSOperator.sleep();
				}
			}
		}
		return -1;
	}

	/**
	 * 注册统计
	 * 
	 * @param registerData
	 */
	public void report(RegisterData registerData) {
		reportLock.lock();
		try {
			reportDatas.add(registerData);
		} finally {
			reportLock.unlock();
		}
	}

	/**
	 * 登陆统计
	 * 
	 * @param loginData
	 */
	public void report(LoginData loginData) {
		reportLock.lock();
		try {
			reportDatas.add(loginData);
		} finally {
			reportLock.unlock();
		}
	}

	/**
	 * 充值统计
	 * 
	 * @param rechargeData
	 */
	public void report(RechargeData rechargeData) {
		reportLock.lock();
		try {
			reportDatas.add(rechargeData);
		} finally {
			reportLock.unlock();
		}
	}

	/**
	 * 钻石统计
	 * 
	 * @param goldData
	 */
	public void report(GoldData goldData) {
		reportLock.lock();
		try {
			reportDatas.add(goldData);
		} finally {
			reportLock.unlock();
		}
	}

	/**
	 * 新手指引统计
	 * 
	 * @param goldData
	 */
	public void report(TutorialData tutorialData) {
		reportLock.lock();
		try {
			reportDatas.add(tutorialData);
		} finally {
			reportLock.unlock();
		}
	}
	
	/**
	 * 服务器信息
	 * 
	 * @param serverData
	 */
	public void report(ServerData serverData) {
		reportLock.lock();
		try {
			reportDatas.add(serverData);
		} finally {
			reportLock.unlock();
		}
	}

	/**
	 * 通用统计
	 * 
	 * @param commonData
	 */
	public void report(CommonData commonData) {
		reportLock.lock();
		try {
			reportDatas.add(commonData);
		} finally {
			reportLock.unlock();
		}
	}

	/**
	 * 上报数据
	 * 
	 * @param registerData
	 * @return
	 */
	private boolean doReport(RegisterData registerData) {
		if (isValid()) {
			try {
				String queryParam = String.format(registerQuery, gameName, curServerId, registerData.birthServerId, platform,  registerData.puid, 
						registerData.device,registerData.deviceInfo,registerData.playerId, 
						(registerData.time == null || registerData.time.length() <= 0) ? GuaJiTime.getTimeString() : registerData.time);
				reportLogger.info("report: " + registerPath + "?" + queryParam);
				queryParam = URLEncoder.encode(queryParam, "UTF-8");
				int status = executeMethod(registerPath, queryParam);
				if (status == HttpStatus.SC_OK) {
					return true;
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return false;
	}

	/**
	 * 上报数据
	 * 
	 * @param loginData
	 * @return
	 */
	private boolean doReport(LoginData loginData) {
		if (isValid()) {
			try {
				//game=%s&curserver=%d&birthserver=%d&action=%s&platform=%s&puid=%s&device=%s&deviceinfo=%s&playerid=%d&playerlevel=%d&viplevel=%d&period=%d&time=%s
				String queryParam = String.format(loginQuery, gameName, curServerId, loginData.birthServerId,loginData.action, platform, loginData.puid, loginData.device, 
						loginData.deviceInfo,loginData.playerId,loginData.playerLevel,loginData.vipLevel,loginData.period, 
						(loginData.time == null || loginData.time.length() <= 0) ? GuaJiTime.getTimeString() : loginData.time);
				reportLogger.info("report: " + loginPath + "?" + queryParam);
				queryParam = URLEncoder.encode(queryParam, "UTF-8");
				int status = executeMethod(loginPath, queryParam);
				if (status == HttpStatus.SC_OK) {
					return true;
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return false;
	}

	/**
	 * 上报数据
	 * 
	 * @param rechargeData
	 * @return
	 */
	private boolean doReport(RechargeData rechargeData) {
		if (isValid()) {
			try {
				String queryParam = String.format(rechargeQuery, gameName,curServerId,rechargeData.birthServerId, platform, rechargeData.puid, 
						rechargeData.device, rechargeData.deviceInfo, rechargeData.playerId, rechargeData.playerName, rechargeData.playerLevel, 
						rechargeData.vipLevel, rechargeData.orderId, rechargeData.productId, rechargeData.payMoney, rechargeData.currency, 
						(rechargeData.time == null || rechargeData.time.length() <= 0) ? GuaJiTime.getTimeString() : rechargeData.time);
				reportLogger.info("report: " + rechargePath + "?" + queryParam);
				queryParam = URLEncoder.encode(queryParam, "UTF-8");
				int status = executeMethod(rechargePath, queryParam);
				if (status == HttpStatus.SC_OK) {
					return true;
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return false;
	}

	/**
	 * 上报数据
	 * 
	 * @param goldData
	 * @return
	 */
	private boolean doReport(GoldData goldData) {
		if (isValid()) {
			try {
				String queryParam = String.format(goldQuery, gameName, platform, curServerId, goldData.puid, goldData.device, 
						goldData.playerId, goldData.playerLevel, goldData.changeType, goldData.changeAction, 
						goldData.goldType, goldData.gold, 
						(goldData.time == null || goldData.time.length() <= 0) ? GuaJiTime.getTimeString() : goldData.time);
				reportLogger.info("report: " + goldPath + "?" + queryParam);
				queryParam = URLEncoder.encode(queryParam, "UTF-8");
				int status = executeMethod(goldPath, queryParam);
				if (status == HttpStatus.SC_OK) {
					return true;
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return false;
	}

	/**
	 * 上报数据
	 * 
	 * @param goldData
	 * @return
	 */
	private boolean doReport(TutorialData tutorialData) {
		if (isValid()) {
			try {
				String queryParam = String.format(tutorialQuery, gameName, curServerId, tutorialData.birthServerId, platform, tutorialData.puid, tutorialData.device, 
						tutorialData.deviceInfo, tutorialData.playerId, tutorialData.playerLevel, tutorialData.vipLevel, tutorialData.guidId, tutorialData.step,
						(tutorialData.time == null || tutorialData.time.length() <= 0) ? GuaJiTime.getTimeString() : tutorialData.time);
				reportLogger.info("report: " + tutorialPath + "?" + queryParam);
				queryParam = URLEncoder.encode(queryParam, "UTF-8");
				int status = executeMethod(tutorialPath, queryParam);
				if (status == HttpStatus.SC_OK) {
					return true;
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return false;
	}
	
	/**
	 * 上报数据
	 * 
	 * @param serverData
	 * @return
	 */
	private boolean doReport(ServerData serverData) {
		if (isValid()) {
			try {
				String queryParam = String.format(serverQuery, gameName, curServerId, platform, serverData.curPlayers, serverData.peakPlayers, GuaJiTime.getTimeString());
				reportLogger.info("report: " + serverPath + "?" + queryParam);
				queryParam = URLEncoder.encode(queryParam, "UTF-8");
				int status = executeMethod(serverPath, queryParam);
				if (status == HttpStatus.SC_OK) {
					return true;
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return false;
	}

	/**
	 * 上报数据
	 * 
	 * @param commonData
	 * @return
	 */
	private boolean doReport(CommonData commonData) {
		if (isValid()) {
			try {
				String queryParam = String.format(commonQuery, gameName, platform, curServerId, 
						commonData.puid, commonData.device, commonData.playerId, 
						(commonData.time == null || commonData.time.length() <= 0) ? GuaJiTime.getTimeString() : commonData.time);

				if (commonData.args != null) {
					for (int i = 0; i < commonData.args.size(); i++) {
						queryParam += "&arg" + (i + 1) + "=" + commonData.args.get(i);
					}
				}

				reportLogger.info("report: " + commonPath + "?" + queryParam);
				queryParam = URLEncoder.encode(queryParam, "UTF-8");

				int status = executeMethod(commonPath, queryParam);
				if (status == HttpStatus.SC_OK) {
					return true;
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return false;
	}

	/**
	 * 帧更新上报数据
	 */
	@Override
	public void onTick() {
		if (reportDatas.size() > 0) {
			// 取出队列首个上报数据对象
			Object reportData = null;
			reportLock.lock();
			try {
				reportData = reportDatas.remove(0);
			} finally {
				reportLock.unlock();
			}

			// 数据上报操作
			try {
				if (gameName.length() > 0 && platform.length() > 0) {
					if (reportData instanceof RegisterData) {
						doReport((RegisterData) reportData);
					} else if (reportData instanceof LoginData) {
						doReport((LoginData) reportData);
					} else if (reportData instanceof RechargeData) {
						doReport((RechargeData) reportData);
					} else if (reportData instanceof GoldData) {
						doReport((GoldData) reportData);
					} else if (reportData instanceof TutorialData) {
						doReport((TutorialData) reportData);
					} else if (reportData instanceof CommonData) {
						doReport((CommonData) reportData);
					} else if (reportData instanceof ServerData) {
						doReport((ServerData) reportData);
					}
				}
			} catch (Exception e) {
				MyException.catchException(e);

				// 上报失败重新放回
				if (!(reportData instanceof ServerData)) {
					reportLock.lock();
					try {
						reportDatas.add(reportData);
					} finally {
						reportLock.unlock();
					}
				}
			}
		}
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
}
