package com.guaji.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.net.NetStatistics;
import org.guaji.os.MyException;
import org.guaji.os.GuaJiTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guaji.game.entity.ServerDataEntity;

/**
 * 服务器数据
 */
public class ServerData {
	private static final Logger logger = LoggerFactory.getLogger("Server");
	/**
	 * 注册玩家数
	 */
	private AtomicInteger registerPlayer;
	/**
	 * 在线玩家数
	 */
	private AtomicInteger onlinePlayer;
	/**
	 * puid和玩家id的映射表
	 */
	protected Map<String, Map<Integer, Integer>> puidMap;
	/**
	 * playerId和serverId映射表
	 */
	protected Map<Integer, Integer> playerIdToServerIds;
	/**
	 * playerId和puid映射表
	 */
	protected Map<Integer, String> playerIdToPuid;
	/**
	 * 玩家名和玩家id的映射表
	 */
	protected ConcurrentHashMap<String, Integer> nameMap;
	/**
	 * 在线玩家列表
	 */
	protected Map<Integer, Integer> onlineMap;
	/**
	 * 禁言设备
	 */
	protected Map<String, String> silentPhoneMap;
	/**
	 * 上次信息显示时间
	 */
	protected int lastShowTime = 0;
	
	
	/**
	 * 服务器各种状态信息
	 */
	protected List<ServerDataEntity<?>> serverDataEntities;

	/**
	 * 全局对象实例
	 */
	private static ServerData instance = null;

	/**
	 * 获取全局实例对象
	 * 
	 * @return
	 */
	public static ServerData getInstance() {
		if (instance == null) {
			instance = new ServerData();
		}
		return instance;
	}

	/**
	 * 构造
	 */
	private ServerData() {
		registerPlayer = new AtomicInteger();
		onlinePlayer = new AtomicInteger();
		puidMap = new ConcurrentHashMap<String, Map<Integer, Integer>>();
		playerIdToServerIds = new ConcurrentHashMap<Integer, Integer>();
		playerIdToPuid = new ConcurrentHashMap<Integer, String>();
		nameMap = new ConcurrentHashMap<String, Integer>();
		onlineMap = new ConcurrentHashMap<Integer, Integer>();
		silentPhoneMap = new ConcurrentHashMap<String, String>();
		lastShowTime = GuaJiTime.getSeconds();
		serverDataEntities = new ArrayList<ServerDataEntity<?>>();
	}

	/**
	 * 初始化服务器数据
	 * 
	 * @return
	 */
	public boolean init() {

		// 从db拉取玩家个数
		try {
			Log.logPrintln("load player count from db......");
			long count = DBManager.getInstance().count("select count(*) from PlayerEntity");
			registerPlayer.set((int) count);
		} catch (Exception e) {
			MyException.catchException(e);
			return false;
		}
		// 从db拉取玩家puid和id的映射表
		try {
			Log.logPrintln("load puid and serverId and playerId from db......");
			List<Object> rowInfos = DBManager.getInstance().executeQuery("select puid, serverId, id from player");
			for (Object rowInfo : rowInfos) {
				Object[] colInfos = (Object[]) rowInfo;
				addPuidAndPlayerId((String) colInfos[0], (Integer) colInfos[1], (Integer) colInfos[2]);
			}
		} catch (Exception e) {
			MyException.catchException(e);
			return false;
		}
		// 从db拉取玩家name和id的映射表
		try {
			Log.logPrintln("load playerName and playerId from db......");
			List<Object> rowInfos = DBManager.getInstance()
					.executeQuery("select name, playerId from role where type = 1");
			for (Object rowInfo : rowInfos) {
				Object[] colInfos = (Object[]) rowInfo;
				// FIXME: niepan & duozhan 这里重名数据是否会影响到合服, 如果影响, 需要做处理
				addIfNameAbsent((String) colInfos[0], (Integer) colInfos[1]);
			}
		} catch (Exception e) {
			MyException.catchException(e);
			return false;
		}
		// 拉取服务器各种状态
		serverDataEntities = DBManager.getInstance().query("from ServerDataEntity where invalid = 0");

		return true;
	}

	/**
	 * 增加注册玩家数
	 * 
	 * @return
	 */
	public int addRegisterPlayer() {
		return registerPlayer.addAndGet(1);
	}

	/**
	 * 获取注册玩家数
	 * 
	 * @return
	 */
	public int getRegisterPlayer() {
		return registerPlayer.get();
	}

	/**
	 * 增加在线玩家数
	 * 
	 * @return
	 */
	public int addOnlinePlayer() {
		return onlinePlayer.addAndGet(1);
	}

	/**
	 * 获取在线玩家数
	 * 
	 * @return
	 */
	public int getOnlinePlayer() {
		//20200909修改 by jackal
		return onlineMap.size();
		//return onlinePlayer.get();
	}
	
	
	/**
	 * 取在線玩家
	 * @return
	 */
	public Map<Integer, Integer> getOnlineMap() {
		return onlineMap;
	}

	/**
	 * 通过puid获取玩家id
	 * 
	 * @param puid
	 * @return
	 */
	public int getPlayerIdByPuid(String puid, int serverId) {
		if (puidMap.containsKey(puid)) {
			if (puidMap.get(puid).containsKey(serverId)) {
				return puidMap.get(puid).get(serverId);
			}
		}
		return 0;
	}

	/**
	 * 增加puid和玩家id的映射
	 * 
	 * @param puid
	 * @param playerId
	 */
	public void addPuidAndPlayerId(String puid, int serverId, int playerId) {
		Map<Integer, Integer> map = null;
		if (!puidMap.containsKey(puid)) {
			map = new ConcurrentHashMap<Integer, Integer>();
		} else {
			map = puidMap.get(puid);
		}
		map.put(serverId, playerId);
		puidMap.put(puid, map);
		// 添加playerId和serverId映射
		playerIdToServerIds.put(playerId, serverId);
		playerIdToPuid.put(playerId, puid);
	}

	/**
	 * 增加name和玩家id的映射
	 * 
	 * @param name
	 * @param playerId
	 * @return true 表示当前没有重名且添加成功; false 表示当前有重名且添加失败;
	 */
	public boolean addIfNameAbsent(String name, int playerId) {
		return nameMap.putIfAbsent(name, playerId) == null;
	}

	/**
	 * 移除name 和 PlayerI的映射关系
	 */
	public void removeNameAndPlayerId(String name, int playerId) {
		if (name == null) {
			return;
		}
		if (this.nameMap.containsKey(name)) {
			this.nameMap.remove(name);
		}
	}

	/**
	 * 是否存在名字
	 * 
	 * @param name
	 * @return
	 */
	public boolean isExistName(String name) {
		return nameMap.containsKey(name);
	}

	/**
	 * 添加在线id
	 * 
	 * @param playerId
	 */
	public void addOnlinePlayerId(int playerId) {
		onlineMap.put(playerId, playerId);
	}

	/**
	 * 移除在线id
	 * 
	 * @param playerId
	 */
	public void removeOnlinePlayerId(int playerId) {
		try {
			onlineMap.remove(playerId);
		} catch (Exception e) {
			MyException.catchException(e);
		}
	}

	/**
	 * 玩家在线判断(内存数据还在，但是session断开)
	 * 
	 * @param playerId
	 * @return
	 */
	public boolean isPlayerOnline(int playerId) {
		return onlineMap.containsKey(playerId);
	}

	public void showServerInfo() {
		// 每分钟显示一个服务器信息
		if (GuaJiTime.getSeconds() - lastShowTime >= 60) {
			lastShowTime = GuaJiTime.getSeconds();

			// 记录信息
			//logger.info("online user: {}", onlineMap.size());
			logger.info("online user: {}",NetStatistics.getInstance().getCurSession());
		}
	}

	public void addSilentPhone(String puid) {
		silentPhoneMap.put(puid, puid);
	}

	public boolean isSilentPhone(String puid) {
		return silentPhoneMap.containsKey(puid);
	}

	public void removeSilentPhone(String puid) {
		silentPhoneMap.remove(puid);
	}

	public void clearSilentPhone() {
		silentPhoneMap.clear();
	}

	/**
	 * 根据服务器状态id获取对应状态实体
	 * 
	 * @param serverDataId
	 * @return
	 */
	private ServerDataEntity<?> getServerDataEntity(int serverDataId) {
		for (ServerDataEntity<?> serverDataEntity : serverDataEntities) {
			if (serverDataEntity.getId() == serverDataId) {
				return serverDataEntity;
			}
		}
		return null;
	}

	/**
	 * 创建服务器状态实体
	 */
	private void createServerDateEntity(ServerDataEntity<?> serverDataEntity) {
		serverDataEntities.add(serverDataEntity);
		DBManager.getInstance().create(serverDataEntity);
	}

	/**
	 * 获得服务器状态信息
	 * 
	 * @param playerData
	 * @param activityId
	 * @param stageId
	 * @param statusClazz
	 * @return
	 */
	public <T> T getServerStatus(int serverDataId, Class<T> statusClazz) {
		@SuppressWarnings("unchecked")
		ServerDataEntity<T> serverDataEntity = (ServerDataEntity<T>) getServerDataEntity(serverDataId);
		if (serverDataEntity == null) {
			serverDataEntity = new ServerDataEntity<T>(serverDataId);
			try {
				serverDataEntity.setServerStatus(statusClazz.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				MyException.catchException(e);
				return null;
			}
			createServerDateEntity(serverDataEntity);
		}
		return serverDataEntity.getServerStatus(statusClazz);
	}

	/**
	 * 服务器状态落地
	 * 
	 * @param serverDataId
	 */
	public void updateServerData(int serverDataId) {
		ServerDataEntity<?> serverDataEntity = getServerDataEntity(serverDataId);
		if (serverDataEntity != null) {
			serverDataEntity.notifyUpdate(true);
		}
	}

	public Map<String, Integer> nameAndIdInfos() {
		return Collections.unmodifiableMap(this.nameMap);
	}

	public int queryPlayerIdByName(String playerName) {
		if (this.nameMap.containsKey(playerName))
			return this.nameMap.get(playerName);
		else
			return -1;

	}

	/**
	 * 根据playerId获取serverId;
	 * 
	 * @param playerId
	 * @return
	 */
	public int queryServerIdByPlayerId(int playerId) {
		return playerIdToServerIds.get(playerId);
	}

	/**
	 * 根据playerId获取puid;
	 * 
	 * @param playerId
	 * @return
	 */
	public String queryPuidByPlayerId(int playerId) {
		return playerIdToPuid.get(playerId);
	}

	public Map<Integer, String> getPlayerIdToPuid() {
		return playerIdToPuid;
	}

}
