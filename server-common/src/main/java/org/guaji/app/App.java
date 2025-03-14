package org.guaji.app;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.util.ConcurrentHashSet;
import org.guaji.app.task.MsgTask;
import org.guaji.app.task.ProtoTask;
import org.guaji.app.task.TickTask;
import org.guaji.cache.Cache;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.intercept.InterceptHandler;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.nativeapi.NativeApi;
import org.guaji.net.GuaJiNetManager;
import org.guaji.net.NetStatistics;
import org.guaji.net.GuaJiSession;
import org.guaji.net.protocol.Protocol;
import org.guaji.obj.ObjBase;
import org.guaji.obj.ObjManager;
import org.guaji.os.MyException;
import org.guaji.os.OSOperator;
import org.guaji.os.GuaJiShutdownHook;
import org.guaji.os.GuaJiTime;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.service.ServiceManager;
import org.guaji.shell.ShellExecutor;
import org.guaji.thread.GuaJiTask;
import org.guaji.thread.GuaJiThreadPool;
import org.guaji.timer.TimerManager;
import org.guaji.util.GuaJiTickable;
import org.guaji.xid.GuaJiXID;
import org.guaji.zmq.GuaJiZmq;
import org.guaji.zmq.GuaJiZmqManager;

/**
 * 应用层封装
 */
public abstract class App extends AppObj {
	/**
	 * 循环退出状态
	 */
	private static int LOOP_BREAK = 0x0001;
	/**
	 * 循环关闭状态
	 */
	private static int LOOP_CLOSED = 0x0002;
	/**
	 * 单例使用
	 */
	protected static App instance;
	/**
	 * 工作路径
	 */
	protected String workPath;
	/**
	 * 当前时间(毫秒, 用于初略的逻辑时间计算)
	 */
	protected long currentTime;
	/**
	 * 更新对象列表
	 */
	protected Set<GuaJiTickable> tickables;
	/**
	 * 是否在运行中
	 */
	protected volatile boolean running;
	/**
	 * 是否退出循环
	 */
	protected volatile int loopState;
	/**
	 * 上次清理对象事件
	 */
	protected long lastRemoveObjTime;
	/**
	 * 是否允许执行shell
	 */
	protected boolean shellEnable;
	/**
	 * 应用配置对象
	 */
	protected AppCfg appCfg;
	/**
	 * 消息逻辑线程池
	 */
	protected GuaJiThreadPool msgExecutor;
	/**
	 * 任务逻辑线程池
	 */
	protected GuaJiThreadPool taskExecutor;
	
	/**
	 * 消息逻辑线程池
	 */
	protected GuaJiThreadPool tickExecutor;
	/**
	 * 对象管理器
	 */
	protected Map<Integer, ObjManager<GuaJiXID, AppObj>> objMans;
	/**
	 * 当前活跃会话列表
	 */
	protected Set<GuaJiSession> activeSessions;
	/**
	 * 对象id列表
	 */
	protected Collection<GuaJiXID> objXidList;
	/**
	 * 对象列表
	 */
	protected Collection<AppObj> appObjList;
	/**
	 * tick时使用xid线程分类表
	 */
	protected Map<Integer, List<GuaJiXID>> threadTickXids;
	/**
	 * 拦截器
	 */
	protected Map<String, InterceptHandler> interceptMap;
	
	/**
	 * 获取全局管理器
	 * 
	 * @return
	 */
	public static App getInstance() {
		return instance;
	}

	/**
	 * 默认构造函数
	 */
	public App(GuaJiXID xid) {
		super(xid);

		if (instance != null) {
			throw new RuntimeException("app instance exist");
		}
		instance = this;
		Thread.currentThread().setName("AppMain");

		// 初始化工作目录
		workPath = System.getProperty("user.dir") + File.separator;
		loopState = LOOP_CLOSED;
		shellEnable = true;
		
		// 初始化系统对象
		appCfg = new AppCfg();
		tickables = new ConcurrentHashSet<GuaJiTickable>();
		activeSessions = new ConcurrentHashSet<GuaJiSession>();
		interceptMap = new ConcurrentHashMap<String, InterceptHandler>();
		objMans = new TreeMap<Integer, ObjManager<GuaJiXID, AppObj>>();
		objXidList = new LinkedList<GuaJiXID>();
		appObjList = new LinkedList<AppObj>();
		lastRemoveObjTime = GuaJiTime.getMillisecond();
	}

	/**
	 * 初始化框架
	 * 
	 * @param appCfg
	 * @return
	 */
	public boolean init(AppCfg appCfg) {
		this.appCfg = appCfg;
		if (this.appCfg == null) {
			return false;
		}

		// 添加库加载目录		
		OSOperator.addUsrPath(System.getProperty("user.dir") + "/lib");
		OSOperator.addUsrPath(System.getProperty("user.dir") + "/hawk");
		new File(".").list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (dir.isDirectory() && name.endsWith("lib")) {
					OSOperator.addUsrPath(System.getProperty("user.dir") + "/" + name);
					return true;
				}
				return false;
			}
		});
		/*
		try {
			// 初始化
			System.loadLibrary("hawk");
//			if (!NativeApi.initHawk()) {
//				return false;
//			}
		} catch (Exception e) {
			MyException.catchException(e);
		}*/
		
		// 设置打印输出标记
		Log.enableConsole(appCfg.console);
		// 设置系统时间偏移
		GuaJiTime.setMsOffset(appCfg.calendarOffset);
		// 初始化系统时间
		currentTime = GuaJiTime.getMillisecond();
		// 打印系统信息
		OSOperator.printOsEnv();
		// 开启关闭钩子
		GuaJiShutdownHook.getInstance().install();
		// 定时器管理器初始化
		TimerManager.getInstance().init(false);

		// 脚本初始化
		if (appCfg.scriptXml != null && appCfg.scriptXml.length() > 0) {
			if (!GuaJiScriptManager.getInstance().init(appCfg.scriptXml)) {
				return false;
			}
		}

		// 对象缓存
		if (appCfg.objCache) {
			Protocol.setCache(new Cache(Protocol.valueOf()));
			ProtoTask.setCache(new Cache(ProtoTask.valueOf()));
			//初始化cache池。创建一个存根
			TickTask.setCache(new Cache(TickTask.valueOf()));
		}
		
		// 初始化zmq管理器
		GuaJiZmqManager.getInstance().init(GuaJiZmq.HZMQ_CONTEXT_THREAD);

		// 添加网络统计到更新列表
		addTickable(NetStatistics.getInstance());
		
		// 初始化配置
		if (appCfg.configPackages != null && appCfg.configPackages.length() > 0) {
			if (!ConfigManager.getInstance().init(appCfg.configPackages)) {
				System.err.println("----------------------------------------------------------------------");
				System.err.println("-------------config crashed-------------");
				System.err.println("----------------------------------------------------------------------");
				return false;
			}
		}

		// 初始化service管理对象
//		if (appCfg.servicePath != null && appCfg.servicePath.length() > 0) {
//			if (!ServiceManager.getInstance().init(appCfg.servicePath)) {
//				return false;
//			}
//		}
		
		// 开启消息线程池
		int msgThreads = appCfg.getThreadNum();
		if (msgExecutor == null && msgThreads > 0 && appCfg.isMsgTaskMode()) {
			msgExecutor = new GuaJiThreadPool("MsgExecutor");
			if (!msgExecutor.initPool(msgThreads) || !msgExecutor.start()) {
				Log.errPrintln(String.format("init msgExecutor failed, threadNum: %d", msgThreads));
				return false;
			}
			Log.logPrintln(String.format("start msgExecutor, threadNum: %d", msgThreads));
		}
		//初始化tick类任务
		int tickThreads = appCfg.getTickThreads();
		if (tickExecutor == null && tickThreads > 0) {
			tickExecutor = new GuaJiThreadPool("TickExecutor");
			if (!tickExecutor.initPool(tickThreads) || !tickExecutor.start()) {
				Log.errPrintln(String.format("init tickExecutor failed, threadNum: %d", tickThreads));
				return false;
			}
			Log.logPrintln(String.format("start tickExecutor, threadNum: %d", tickThreads));
		}
		// 开启任务线程池
		int taskThreadNum = appCfg.getTaskThreads();
		if (taskThreadNum <= 0) {
			taskThreadNum = appCfg.getThreadNum();
		}
		if (taskExecutor == null && taskThreadNum > 0) {
			taskExecutor = new GuaJiThreadPool("TaskExecutor");
			if (!taskExecutor.initPool(taskThreadNum) || !taskExecutor.start()) {
				Log.errPrintln(String.format("init taskExecutor failed, threadNum: %d", taskThreadNum));
				return false;
			}
			Log.logPrintln(String.format("start taskExecutor, threadNum: %d", taskThreadNum));
		}
		

		// 初始化数据库连接
		if (appCfg.dbHbmXml != null && appCfg.dbConnUrl != null && appCfg.dbUserName != null && appCfg.dbPassWord != null) {
			if (!DBManager.getInstance().init(appCfg.dbHbmXml, appCfg.dbConnUrl, appCfg.dbUserName, appCfg.dbPassWord, appCfg.entityPackages)) {
				return false;
			}

			// 开启数据库异步落地
			if (appCfg.dbAsyncPeriod > 0) {
				int dbThreadNum = appCfg.dbThreads;
				if (dbThreadNum <= 0) {
					dbThreadNum = appCfg.threadNum;
				}
				if (dbThreadNum > 0) {
					DBManager.getInstance().startAsyncThread(appCfg.dbAsyncPeriod, dbThreadNum);
					Log.logPrintln(String.format("start dbExecutor, threadNum: %d", dbThreadNum));
				}
			}
		}
		
		// 自动脚本运行
		GuaJiScriptManager.getInstance().autoRunScript();
		
		return true;
	}

	/**
	 * 开启网络
	 * 
	 * @return
	 */
	public boolean startNetwork() {
		if (appCfg.acceptorPort > 0) {
			if (!GuaJiNetManager.getInstance().initFromAppCfg(appCfg)) {
				Log.errPrintln("init network failed, port: " + appCfg.acceptorPort);
				return false;
			}
			
			Log.logPrintln("start network, port: " + appCfg.acceptorPort);
			System.out.println("start network, port: " + appCfg.acceptorPort);
		}
		return true;
	}
	
	/**
	 * 获取工作目录
	 * 
	 * @return
	 */
	public String getWorkPath() {
		return workPath;
	}

	/**
	 * 获取当前系统时间
	 * 
	 * @return
	 */
	public long getCurrentTime() {
		return currentTime;
	}

	/**
	 * 获取应用配置对象
	 * 
	 * @return
	 */
	public AppCfg getAppCfg() {
		return appCfg;
	}

	/**
	 * 设置应用配置对象
	 * 
	 * @return
	 */
	public void setAppCfg(AppCfg appCfg) {
		this.appCfg = appCfg;
	}
	
	/**
	 * 是否运行状态
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * 通知退出循环
	 * 
	 * @return
	 */
	public boolean breakLoop() {
		loopState |= LOOP_BREAK;
		return true;
	}

	/**
	 * 开启或关闭shell命令执行权限
	 * 
	 * @param enable
	 */
	public void enableShell(boolean enable) {
		this.shellEnable = enable;
	}
	
	/*
	 * 取得线程池
	 * 
	 */
	public GuaJiThreadPool getHawkThreadPool(){
		
		return taskExecutor;
	}
	

	/**
	 * 获取线程id列表
	 * 
	 * @return
	 */
	public Collection<Long> getThreadIds() {
		List<Long> threadIds = new LinkedList<Long>();
		for (int i = 0; msgExecutor != null && i < msgExecutor.getThreadNum(); i++) {
			threadIds.add(msgExecutor.getThreadId(i));
		}
		return threadIds;
	}

	/**
	 * 获取活跃会话集合
	 * 
	 * @return
	 */
	public Set<GuaJiSession> getActiveSessions() {
		return activeSessions;
	}

	/**
	 * 获取tickable的集合
	 * @return
	 */
	public Set<GuaJiTickable> getTickables() {
		return tickables;
	}
	
	/**
	 * 添加可tick对象
	 * 
	 * @param tickable
	 */
	public void addTickable(GuaJiTickable tickable) {
		tickables.add(tickable);
	}

	/**
	 * 移除tick对象
	 * 
	 * @param tickable
	 */
	public void removeTickable(GuaJiTickable tickable) {
		tickables.remove(tickable);
	}

	/**
	 * 移除tick对象
	 * 
	 * @param tickable
	 */
	public void removeTickable(String name) {
		Iterator<GuaJiTickable> iterator = tickables.iterator();
		while (iterator.hasNext()) {
			try {
				GuaJiTickable tickable = iterator.next();
				if (tickable != null && tickable.getName().equals(name)) {
					iterator.remove();
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
	}
	
	/**
	 * 清空tick对象
	 */
	public void clearTickable() {
		tickables.clear();
	}
	
	/**
	 * 添加拦截器
	 * @param name
	 * @param handler
	 */
	public void addInterceptHandler(String name, InterceptHandler handler) {
		interceptMap.put(name, handler);
	}
	
	/**
	 * 获取拦截器
	 * @param name
	 * @param handler
	 */
	public InterceptHandler getInterceptHandler(String name) {
		return interceptMap.get(name);
	}
	
	/**
	 * 移除拦截器
	 * @param name
	 */
	public void removeInterceptHandler(String name) {
		interceptMap.remove(name);
	}
	
	/**
	 * 清除拦截器
	 */
	public void clearInterceptHandler() {
		interceptMap.clear();
	}
	
	/**
	 * 是否为debug模式
	 * 
	 * @return
	 */
	public boolean isDebug() {
		return appCfg.isDebug;
	}

	/**
	 * 设置上次对象移除时间
	 * @param lastRemoveObjTime
	 */
	public void setLastRemoveObjTime(long lastRemoveObjTime) {
		this.lastRemoveObjTime = lastRemoveObjTime;
	}
	
	/**
	 * 启动服务
	 * 
	 * @return
	 */
	public boolean run() {
		if (!running) {
			// 检测网络是否开启
			if (appCfg.acceptorPort > 0 && GuaJiNetManager.getInstance().getAcceptor() == null) {
				if (!startNetwork()) {
					return false;
				}
			}
			
			// 设置状态
			running = true;
			loopState = 0;
			
			Log.logPrintln("server running......");
			System.out.println("server running......");
			while (running && (loopState & LOOP_BREAK) == 0) {
				currentTime = GuaJiTime.getMillisecond();

				// 逻辑帧更新
				try {
					onTick();
				} catch (Exception e) {
					MyException.catchException(e);
				}
				
				OSOperator.osSleep(appCfg.tickPeriod);
			}			
			onClosed();
			running = false;
			Log.logPrintln("GuaJi main loop exit");
			return true;
		}
		return false;
	}

	/**
	 * 帧更新
	 */
	@Override
	public boolean onTick() {
		// 更新检测
//		if (!NativeApi.tickHawk()) {
//			return false;
//		}
		
		// tick对象的更新
		for (GuaJiTickable tickable : tickables) {
			if (tickable.isTickable()) {
				tickable.onTick();
			}
		}
		
		// 对象管理器的更新(每小时一个周期)
		if (currentTime - lastRemoveObjTime >= 1*60*1000) {
			lastRemoveObjTime = currentTime;
			int removeCount = 0;
			for (Entry<Integer, ObjManager<GuaJiXID, AppObj>> entry : objMans.entrySet()) {
				ObjManager<GuaJiXID, AppObj> objMan = entry.getValue();
				if (objMan != null && objMan.getObjTimeout() > 0) {
					// 清理超时对象
					List<AppObj> removeAppObjs = objMan.removeTimeoutObj(currentTime);
					if (removeAppObjs != null) {
						for (AppObj appObj : removeAppObjs) {
							onRemoveTimeoutObj(appObj);
						}
						removeCount = removeAppObjs.size();
					}
					Log.logPrintln(String.format("app remove timeout obj, manager: %d, count: %d", entry.getKey(), removeCount));
				}
			}
		}
		
		// 对象更新
		for (Entry<Integer, ObjManager<GuaJiXID, AppObj>> entry : objMans.entrySet()) {
			ObjManager<GuaJiXID, AppObj> objMan = entry.getValue();
			
			if (objMan != null) {
				if (appCfg.isMsgTaskMode()) {
					objXidList.clear();
					if (objMan.collectObjKey(objXidList, null) > 0) {
						postTick(objXidList);
					}
				} else {
					appObjList.clear();
					objMan.collectObjValue(appObjList, null);
					for (AppObj appObj : appObjList) {
						if (appObj != this) {
							appObj.onTick();
						}
					}
				}
			}
		}
		
		return super.onTick();
	}

	/**
	 * 移除超时应用对象
	 * @param appObj
	 */
	protected void onRemoveTimeoutObj(AppObj appObj) {
	}

	/**
	 * 处理shell命令, 不可手动调用, 由脚本管理器调用
	 * 
	 * @param params
	 */
	public String onShellCommand(String cmd, long timeout) {
		if (shellEnable && cmd != null && cmd.length() > 0) {
			String result = ShellExecutor.execute(cmd, timeout);
			Log.logPrintln("shell command: " + cmd + "\r\n" + result);
			return result;
		}
		return null;
	}
	
	/**
	 * 程序被关闭时的回调
	 */
	public void onShutdown() {
		breakLoop();
		
		// 等待循环状态
		while ((loopState & LOOP_CLOSED) != LOOP_CLOSED) {
			OSOperator.sleep();
		}
	}

	/**
	 * 应用程序退出时回调
	 */
	protected void onClosed() {
		try {
			try {
				// 关闭网络
				GuaJiNetManager.getInstance().close();
			} catch (Exception e) {
				MyException.catchException(e);
			}
			
			try {
				// 停止定时器管理器
				TimerManager.getInstance().stop();
			} catch (Exception e) {
				MyException.catchException(e);
			}
			
			try {
				// 停止数据库
				DBManager.getInstance().stop();
			} catch (Exception e) {
				MyException.catchException(e);
			}
			
			try {
				// 等待消息线程结束
				if (msgExecutor != null) {
					msgExecutor.close(true);
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
			
			try {
				// 等待任务线程池结束
				if (tickExecutor != null) {
					tickExecutor.close(true);
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
			
			
			try {
				// 等待任务线程池结束
				if (taskExecutor != null) {
					taskExecutor.close(true);
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
			
			try {
				// 关闭脚本管理器
				GuaJiScriptManager.getInstance().close();
			} catch (Exception e) {
				MyException.catchException(e);
			}
			
			try {
				// 关闭数据库
				DBManager.getInstance().close();
			} catch (Exception e) {
				MyException.catchException(e);
			}
			
		} finally {
			// 设置关闭状态
			loopState |= LOOP_CLOSED;
		}
	}

	/**
	 * 获取指定类型的管理器
	 * 
	 * @param type
	 * @return
	 */
	public ObjManager<GuaJiXID, AppObj> getObjMan(int type) {
		return objMans.get(type);
	}

	/**
	 * 注册创建对象管理器
	 * 
	 * @param type
	 * @return
	 */
	protected ObjManager<GuaJiXID, AppObj> createObjMan(int type) {
		ObjManager<GuaJiXID, AppObj> objMan = getObjMan(type);
		if (objMan == null) {
			objMan = new ObjManager<GuaJiXID, AppObj>(appCfg.isMsgTaskMode());
			objMans.put(type, objMan);
		}
		return objMan;
	}

	/**
	 * 创建对象通用接口
	 * 
	 * @param xid
	 * @param sid
	 * @return
	 */
	public ObjBase<GuaJiXID, AppObj> createObj(GuaJiXID xid) {
		if (xid.isValid()) {
			// 获取管理器
			ObjManager<GuaJiXID, AppObj> objMan = getObjMan(xid.getType());
			if (objMan == null) {
				Log.errPrintln("objMan type nonentity: " + xid.getType());
				return null;
			}

			// 创建应用层对象
			AppObj appObj = onCreateObj(xid);
			if (appObj != null) {
				// 添加到管理器容器
				ObjBase<GuaJiXID, AppObj> objBase = objMan.allocObject(xid, appObj);
				return objBase;
			}
		}
		Log.errPrintln("create obj failed: " + xid);
		return null;
	}

	/**
	 * 应用层创建对象, 应用层必须重写此函数
	 * 
	 * @param xid
	 * @return
	 */
	protected AppObj onCreateObj(GuaJiXID xid) {
		return null;
	}

	/**
	 * 查询指定id对象, 非线程安全
	 * 
	 * @param xid
	 * @return
	 */
	public ObjBase<GuaJiXID, AppObj> queryObject(GuaJiXID xid) {
		if (xid != null && xid.isValid()) {
			ObjManager<GuaJiXID, AppObj> objMan = objMans.get(xid.getType());
			if (objMan != null) {
				return objMan.queryObject(xid);
			}
		}
		return null;
	}

	/**
	 * 查询唯一id对象, 必须把返回对象进行unlockObj操作以避免不解锁
	 * 
	 * @param xid
	 * @return
	 */
	public ObjBase<GuaJiXID, AppObj> lockObject(GuaJiXID xid) {
		if (xid != null && xid.isValid()) {
			ObjManager<GuaJiXID, AppObj> objMan = objMans.get(xid.getType());
			if (objMan != null) {
				ObjBase<GuaJiXID, AppObj> objBase = objMan.queryObject(xid);
				if (objBase != null) {
					objBase.lockObj();
					return objBase;
				}
			}
		}
		return null;
	}

	/**
	 * 销毁对象
	 * 
	 * @param xid
	 * @return
	 */
	public boolean removeObj(GuaJiXID xid) {
		if (xid.isValid()) {
			// 获取管理器
			ObjManager<GuaJiXID, AppObj> objMan = getObjMan(xid.getType());
			if (objMan != null) {
				objMan.freeObject(xid);
				return true;
			}
		}
		return false;
	}

	/**
	 * 投递通用型任务到线程池处理
	 * 
	 * @param task
	 * @return
	 */
	public boolean postCommonTask(GuaJiTask task) {
		if (running && task != null && taskExecutor != null) {
			return taskExecutor.addTask(task);
		}
		return false;
	}

	/**
	 * 投递通用型任务到线程池处理
	 * 
	 * @param task
	 * @return
	 */
	public boolean postCommonTask(GuaJiTask task, int threadIdx) {
		if (running && task != null && taskExecutor != null) {
			return taskExecutor.addTask(task, Math.abs(threadIdx), false);
		}
		return false;
	}
	
	/**
	 * 投递任务到消息线程组
	 * 
	 * @param task
	 * @return
	 * @throws Exception 
	 */
	public boolean postMsgTask(MsgTask task) {
		if (running && task != null) {
			int threadIdx = task.getXid().getHashThread(appCfg.getThreadNum());
			return postMsgTask(task, threadIdx);
		}
		return false;
	}
	
	/**
	 * 投递tick类任务到消息线程组
	 * 
	 * @param task
	 * @param threadIdx
	 * @return
	 */
	protected boolean postTickTask(GuaJiTask task, int threadIdx) {
		if (running && task != null) {
			return tickExecutor.addTask(task, Math.abs(threadIdx), false);
		}
		return false;
	}
	/**
	 * 投递任务到消息线程组
	 * 
	 * @param task
	 * @param threadIdx
	 * @return
	 */
	protected boolean postMsgTask(GuaJiTask task, int threadIdx) {
		if (running && task != null) {
			return msgExecutor.addTask(task, Math.abs(threadIdx), false);
		}
		return false;
	}

	/**
	 * 接收到协议后投递到应用
	 * 
	 * @param xid
	 * @param protocol
	 * @return
	 */
	public boolean postProtocol(GuaJiXID xid, Protocol protocol) {
		if (running && xid != null && protocol != null) {
			if (appCfg.isMsgTaskMode()) {
				int threads = appCfg.getThreadNum();
				int threadIdx = xid.getHashThread(threads);
				return postMsgTask(ProtoTask.valueOf(xid, protocol), threadIdx);
			} else {
				return dispatchProto(xid, protocol);
			}
		}
		return false;
	}

	/**
	 * 向特定对象投递消息
	 * 
	 * @param xid
	 * @param msg
	 * @return
	 */
	public boolean postMsg(GuaJiXID xid, Msg msg) {
		if (running && xid != null && msg != null) {
			msg.setTarget(xid);
			return postMsg(msg);
		}
		return false;
	}

	/**
	 * 直接投递消息
	 * 
	 * @param msg
	 * @return
	 */
	public boolean postMsg(Msg msg) {
		if (running && msg != null) {
			if (appCfg.isMsgTaskMode()) {
				int threads = appCfg.getThreadNum();
				int threadIdx = msg.getTarget().getHashThread(threads);
				
				if (App.getInstance().getAppCfg().isDebug()) {
					Log.logPrintln(String.format("post message: %d, target: %s, thread: %d", msg.getMsg(), msg.getTarget().toString(), threadIdx));
				}
				
				return postMsgTask(MsgTask.valueOf(msg.getTarget(), msg), threadIdx);
			} else {
				return dispatchMsg(msg.getTarget(), msg);
			}
		}
		return false;
	}

	/**
	 * 群发消息
	 * 
	 * @param xidList
	 * @param msg
	 * @return
	 */
	public boolean postMsg(Collection<GuaJiXID> xidList, Msg msg) {
		if (running && xidList != null && xidList.size() > 0 && msg != null) {
			if (appCfg.isMsgTaskMode()) {
				int threads = appCfg.getThreadNum();
				Map<Integer, List<GuaJiXID>> threadXidMap = new HashMap<Integer, List<GuaJiXID>>();
				// 计算xid列表所属线程
				for (GuaJiXID xid : xidList) {
					int threadIdx = xid.getHashThread(threads);
					List<GuaJiXID> threadXidList = threadXidMap.get(threadIdx);
					if (threadXidList == null) {
						threadXidList = new LinkedList<GuaJiXID>();
						threadXidMap.put(threadIdx, threadXidList);
					}
					threadXidList.add(xid);
				}
	
				// 按线程投递消息
				for (Map.Entry<Integer, List<GuaJiXID>> entry : threadXidMap.entrySet()) {
					postMsgTask(MsgTask.valueOf(entry.getValue(), msg), entry.getKey());
				}
			} else {
				for (GuaJiXID xid : xidList) {
					dispatchMsg(xid, msg);
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 提交更新, 只会在主线程调用
	 * 
	 * @param xidList
	 * @return
	 */
	public boolean postTick(Collection<GuaJiXID> xidList) {
		if (running && xidList != null && xidList.size() > 0) {
			if (appCfg.isMsgTaskMode()) {
				// 先创建线程tick表
				int tickThreads = appCfg.getTickThreads();
				if (threadTickXids == null) {
					threadTickXids = new HashMap<Integer, List<GuaJiXID>>();
					for (int i = 0; i < tickThreads; i++) {
						threadTickXids.put(i, new LinkedList<GuaJiXID>());
					}
				} else {
					for (int i = 0; i < tickThreads; i++) {
						threadTickXids.get(i).clear();
					}
				}
	
				if (xidList != null && xidList.size() > 0) {
					// 计算xid列表所属线程
					for (GuaJiXID xid : xidList) {
						int threadIdx = xid.getHashThread(tickThreads);
						// app对象本身不参与线程tick更新计算, 本身的tick在主线程执行
						if (!xid.equals(this.objXid)) {
							threadTickXids.get(threadIdx).add(xid);
						}
					}
	
					// 按线程投递消息
					for (Map.Entry<Integer, List<GuaJiXID>> entry : threadTickXids.entrySet()) {
						if (entry.getValue().size() > 0) {
							// 不存在即创建
							postTickTask(TickTask.valueOf(entry.getValue()), entry.getKey());
						}
					}
				}
			} else {
				for (GuaJiXID xid : xidList) {
					if (!xid.equals(this.objXid)) {
						dispatchTick(xid);
					}
				}
			}
		}
		return true;
	}

	/**
	 * 广播消息
	 * 
	 * @param msg
	 * @param xidList
	 * @return
	 */
	public boolean broadcastMsg(Msg msg, Collection<GuaJiXID> xidList) {
		if (running && msg != null && xidList != null) {
			return postMsg(xidList, msg);
		}
		return false;
	}

	/**
	 * 广播消息
	 * 
	 * @param msg
	 * @param objMan
	 * @return
	 */
	public boolean broadcastMsg(Msg msg, ObjManager<GuaJiXID, AppObj> objMan) {
		if (msg != null && objMan != null) {
			List<GuaJiXID> xidList = new LinkedList<GuaJiXID>();
			objMan.collectObjKey(xidList, null);
			return postMsg(xidList, msg);
		}
		return false;
	}

	/**
	 * 协议广播
	 * 
	 * @param protocol
	 * @return
	 */
	public boolean broadcastProtocol(Protocol protocol) {
		for (GuaJiSession session : activeSessions) {
			try {
				session.sendProtocol(protocol);
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return true;
	}

	/**
	 * 分发协议
	 * 
	 * @param xid
	 * @param protocol
	 * @return
	 */
	public boolean dispatchProto(GuaJiXID xid, Protocol protocol) {
		if (xid != null && protocol != null) {
			if (xid.isValid()) {
				ObjBase<GuaJiXID, AppObj> objBase = lockObject(xid); //分发到哪个对象
				if (objBase != null) {
					long beginTimeMs = GuaJiTime.getMillisecond();
					try {
						if (objBase.isObjValid()) {
							objBase.setVisitTime(currentTime);
							InterceptHandler interceptHandler = getInterceptHandler(objBase.getImpl().getClass().getName());
							if (interceptHandler != null && interceptHandler.onProtocol(objBase.getImpl(), protocol)) {
								return true;
							}
							return objBase.getImpl().onProtocol(protocol);
						}
					} catch (Exception e) {
						MyException.catchException(e);
					} finally {
						objBase.unlockObj();
						long costTimeMs = GuaJiTime.getMillisecond() - beginTimeMs;
						if (costTimeMs > App.getInstance().getAppCfg().getProtoTimeout()) {
							Log.logPrintln("protocol timeout, protocol: " + protocol.getType() + ", costtime: " + costTimeMs);
						}
					}
				}
			} else {
				return onProtocol(protocol);
			}
		}
		return false;
	}

	/**
	 * 分发消息
	 * 
	 * @param xid
	 * @param msg
	 * @return
	 * @throws Exception 
	 */
	public boolean dispatchMsg(GuaJiXID xid, Msg msg) {
		if (xid != null && msg != null) {
			if (xid.isValid()) {
				if (App.getInstance().getAppCfg().isDebug()) {
					Log.logPrintln(String.format("dispatch message: %d, target: %s", msg.getMsg(), xid.toString()));
				}
				
				ObjBase<GuaJiXID, AppObj> objBase = lockObject(xid);
				if (objBase != null) {
					long beginTimeMs = GuaJiTime.getMillisecond();
					try {
						if (objBase.isObjValid()) {
							InterceptHandler interceptHandler = getInterceptHandler(objBase.getImpl().getClass().getName());
							if (interceptHandler != null && interceptHandler.onMessage(objBase.getImpl(), msg)) {
								return true;
							}
							return objBase.getImpl().onMessage(msg);
						}
					} catch (Exception e) {
						MyException.catchException(e);
					} finally {
						objBase.unlockObj();
						long costTimeMs = GuaJiTime.getMillisecond() - beginTimeMs;
						if (costTimeMs > App.getInstance().getAppCfg().getProtoTimeout()) {
							Log.logPrintln("message timeout, msg: " + msg.getMsg() + ", costtime: " + costTimeMs);
						}
					}
				}
			} else {
				return onMessage(msg);
			}
		}
		return false;
	}

	/**
	 * 分发更新事件
	 * 
	 * @param xid
	 * @return
	 * @throws Exception 
	 */
	public boolean dispatchTick(GuaJiXID xid) {
		if (xid != null) {
			if (xid.isValid()) {
				ObjBase<GuaJiXID, AppObj> objBase = lockObject(xid);
				if (objBase != null) {
					try {
						if (objBase.isObjValid()) {
							InterceptHandler interceptHandler = getInterceptHandler(objBase.getImpl().getClass().getName());
							if (interceptHandler != null && interceptHandler.onTick(objBase.getImpl())) {
								return true;
							}
							return objBase.getImpl().onTick();
						}
					} catch (Exception e) {
						MyException.catchException(e);
					} finally {
						objBase.unlockObj();
					}
				}
			} else {
				return onTick();
			}
		}
		return false;
	}

	/**
	 * 会话开启回调
	 * 
	 * @param session
	 */
	public boolean onSessionOpened(GuaJiSession session) {
		activeSessions.add(session);
		return true;
	}

	/**
	 * 会话协议回调, 由IO线程直接调用, 非线程安全
	 * 
	 * @param session
	 * @param protocol
	 * @return
	 */
	public boolean onSessionProtocol(GuaJiSession session, Protocol protocol) {
		if (running && session != null && protocol != null && session.getAppObject() != null) {
			if (appCfg.isMsgTaskMode()) {
				return postProtocol(session.getAppObject().getXid(), protocol);
			} else {
				session.getAppObject().onProtocol(protocol);
				return true;
			}
		}
		return false;
	}

	/**
	 * 会话关闭回调
	 * 
	 * @param session
	 */
	public void onSessionClosed(GuaJiSession session) {
		activeSessions.remove(session);
		if(session !=null){
			
			Log.logPrintln(String.format("player setSession onSessionClosed nul %s",session.getIpAddr()));
			session.setAppObject(null);
		}
	}

	/**
	 * 检测是否成功
	 * 
	 * @return
	 */
	public boolean checkConfigData() {
		return true;
	}

	/**
	 * 报告异常信息(主要通过邮件)
	 * 
	 * @param e
	 */
	public void reportException(Exception e) {
	}
}
