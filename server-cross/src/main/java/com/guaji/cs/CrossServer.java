package com.guaji.cs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;

import org.guaji.config.ConfigManager;
import org.guaji.log.Log;
import org.guaji.nativeapi.NativeApi;
import org.guaji.net.GuaJiNetManager;
import org.guaji.os.MyException;
import org.guaji.os.OSOperator;

import com.guaji.cs.battle.BattleManager;
import com.guaji.cs.db.DBManager;
import com.guaji.cs.net.handler.PacketHandler;
import com.guaji.cs.net.handler.SessionHandler;
import com.guaji.cs.tick.ITickable;
import com.guaji.game.manager.SkillHandlerManager;

/**
 * 跨服服务器
 */
public class CrossServer {
	
	/**
	 * 服务器是否允许中
	 */
	private volatile boolean running;
	
	/**
	 * 可更新列表
	 */
	private Set<ITickable> tickableSet;
	
	/**
	 * 跨服ID
	 */
	private int csId;
	
	/**
	 * 单例对象
	 */
	private static final CrossServer instance = new CrossServer();

	/**
	 * 获取实例
	 */
	public static CrossServer getInstance() {
		return instance;
	}

	/**
	 * 默认构造函数
	 */
	private CrossServer() {
		// 初始化变量
		running = true;
		tickableSet = new HashSet<ITickable>();
	}

	/**
	 * 是否运行中
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * 使用配置文件初始化
	 * 
	 * @param cfgFile
	 * @return
	 */
	public boolean init(String cfgFile) {
		// 初始化单例, 避免多线程安全问题
		DBManager.getInstance();
		BattleManager.getInstance();
		PacketHandler.getInstance();
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

		try {
			// 初始化
//			System.loadLibrary("hawk");
//			if (!NativeApi.initHawk()) {
//				return false;
//			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		// 读取crossserver所需要的xml配置文件
		try {
			ConfigManager.getInstance().init("com.guaji.game.config,com.guaji.cs.config");
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 读取配置文件
		ResourceBundle bundle = null;
		try {
			bundle = new PropertyResourceBundle(new BufferedInputStream(new FileInputStream(cfgFile)));
			// 初始化网络
			int netPort = Integer.valueOf(bundle.getString("port").trim());
			String dbHost = bundle.getString("dbHost").trim();
			String dbUser = bundle.getString("dbUser").trim();
			String dbPwd = bundle.getString("dbPwd").trim();
			csId = Integer.valueOf(bundle.getString("csId").trim());
			int ioChains = Integer.valueOf(bundle.getString("ioChains").trim());
			if (!initNet(netPort, ioChains) || !initDB(dbHost, dbUser, dbPwd)) {
				return false;
			}
			Log.logPrintln("server running......");
			SkillHandlerManager.getInstance().init();
			// 加载数据
			DBManager.getInstance().loadCrossData();
		} catch (IOException e) {
			e.printStackTrace();
			Log.errPrintln("InitCfg Failed, CfgFile: " + cfgFile);
			return false;
		}
		return true;
	}

	/**
	 * 初始化网络
	 * 
	 * @param port
	 * @param ioChains
	 * @return
	 */
	private boolean initNet(int port, int ioChains) {
		GuaJiNetManager.getInstance().init(port, ioChains, SessionHandler.getInstance());
		Log.logPrintln("InitNet OK, Port: " + port);
		return true;
	}

	/**
	 * DB连接
	 * 
	 * @param dbHost
	 * @param dbUser
	 * @param dbPwd
	 * @return
	 */
	private boolean initDB(String dbHost, String dbUser, String dbPwd) {
		if (DBManager.getInstance().init(dbHost, dbUser, dbPwd)) {
			Log.logPrintln(String.format("InitDB OK, dbHost: %s, dbUser: %s, dbPwd: %s", dbHost, dbUser, dbPwd));
			return true;
		}
		Log.errPrintln(String.format("InitDB Failed, dbHost: %s, dbUser: %s, dbPwd: %s", dbHost, dbUser, dbPwd));
		return false;
	}

	/**
	 * 退出服务主循环
	 */
	public void breakLoop() {
		running = false;
	}

	/**
	 * 定时更新
	 */
	private void onTick() {
		for (ITickable tick : tickableSet) {
			tick.onTick();
		}
	}

	/**
	 * 添加可更新对象列表
	 * 
	 * @param tickable
	 */
	public void addTickable(ITickable tickable) {
		tickableSet.add(tickable);
	}

	/**
	 * 移除可更新对象
	 * 
	 * @param tickable
	 */
	public void removeTickable(ITickable tickable) {
		tickableSet.remove(tickable);
	}

	/**
	 * 启动服务器
	 */
	public void run() {
		final int TICK_PERIOD = 20;
		Log.logPrintln("MainLoop Running OK.");
		while (running) {
			try {
				this.onTick();
				if (!DBManager.getInstance().hasCachedDbOperation()) {
					Thread.sleep(TICK_PERIOD);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// DB落地
		DBManager.getInstance().flushDbOperation(true);
	}

	/**
	 * 获取连接本跨服的GameServer个数
	 * 
	 * @return
	 */
	public int getCsId() {
		return csId;
	}
	
}
