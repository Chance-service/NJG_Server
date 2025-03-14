package com.guaji.game.manager;

import java.util.ArrayList;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.msg.Msg;
import org.guaji.os.MyException;
import org.guaji.xid.GuaJiXID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guaji.game.GsConfig;
import com.guaji.game.ServerData;
import com.guaji.game.manager.snapshot.ISnapShotStrategy;
import com.guaji.game.manager.snapshot.LocalSnapShotStrategy;
import com.guaji.game.manager.snapshot.SnapShotType;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.util.GsConst;

/**
 * <ol>
 * 管理器的几点说明：
 * <li>管理器对外暴露所有需要快照服务的接口</li>
 * <li>管理器内部功能代理给快照策略 {@link ISnapShotStrategy} 实现，可以通过配置来设置首选的快照策略</li>
 * <li>原则上首选远端cache来解决读压力的问题，如果远端挂掉可以通过心跳检测然后进行备胎策略切换</li>
 * </ol>
 *
 */
public class SnapShotManager extends AppObj {
	/** logger */
	protected Logger logger = LoggerFactory.getLogger("Server");
	/** instance */
	private static SnapShotManager instance = null;
	/** 当前的策略 */
	private ISnapShotStrategy currentStrategy;
	/** 备胎策略 */
	private ISnapShotStrategy backupStrategy = new LocalSnapShotStrategy();

	public static SnapShotManager getInstance() {
		return instance;
	}

	public SnapShotManager(GuaJiXID xid) {
		super(xid);

		if (instance == null) {
			instance = this;
		}
	}

	/**
	 * 初始化;
	 * 
	 * @return
	 */
	public boolean init() {
		// build default
		currentStrategy = buildDefaultStrategy(GsConfig.getInstance().getSnapShotStrategy());
		return currentStrategy.init();
	}

	private ISnapShotStrategy buildDefaultStrategy(int snapShotStrategy) {
		return SnapShotType.typeOf(snapShotStrategy).create();
	}

	@Override
	public boolean onTick() {
		boolean workWell = currentStrategy.onTick();
		if (!workWell) {
			logger.error(String.format("Current strategy: %s not work well, switch to the secondary strategy.",
					this.currentStrategy.getClass().getSimpleName()));
			this.switchToLocalStrategy();
		}
		return true;
	}

	/**
	 * 切换策略;<br>
	 */
	private void switchToLocalStrategy() {
		// init and switch
		this.backupStrategy.init();
		this.currentStrategy = backupStrategy;
	}

	/**
	 * 切换到指定的快照策略;
	 * 
	 * @param strategyType
	 */
	public void switchToStrategy(int strategyType) {
		ISnapShotStrategy strategy = SnapShotType.typeOf(strategyType).create();
		strategy.init();
		this.currentStrategy = strategy;
		// info
		logger.info(String.format("Current strategy: %s , switch to the strategy.", this.currentStrategy.getClass().getSimpleName(), strategy.getClass().getSimpleName()));
	}

	public ISnapShotStrategy getCurrentStrategy() {
		return currentStrategy;
	}
	
	/**
	 * 快照管理器消息处理
	 */
	@Override
	public boolean onMessage(Msg msg) {
		if (msg.getMsg() == GsConst.MsgType.ONLINE_REMOVE_OFFLINE_SNAPSHOT) {
			int onlinePlayerId = msg.getParam(0);
			currentStrategy.onPlayerLogin(onlinePlayerId);
			return true;
		}
		return false;
	}

	/**
	 * 获取玩家快照信息，如果返回null则玩家不存在
	 * 
	 * @param playerId
	 * @return
	 */
	public PlayerSnapshotInfo.Builder getPlayerSnapShot(int playerId) {
		return currentStrategy.getSnapShot(playerId);
	}

	/**
	 * 获取玩家出战佣兵信息
	 * 
	 * @return
	 */
	public List<RoleInfo.Builder> getFightMercenaryInfo(int playerId) {
		List<RoleInfo.Builder> roleList = new ArrayList<>();
		try {
			PlayerSnapshotInfo.Builder playerSnapshot = getPlayerSnapShot(playerId).clone();
			if (playerSnapshot != null) {
				List<Integer> roleIdCloneList = new ArrayList<>(playerSnapshot.getFightingRoleIdList());
				if (roleIdCloneList != null && roleIdCloneList.size() > 0) {
					for (Integer itemId : roleIdCloneList) {
						if (itemId > 0) {
							RoleInfo.Builder roleInfo = getActiviteRoleInfo(playerId, itemId);
							roleList.add(roleInfo); // null 為空位
						} else {
							roleList.add(null);
						}
						
					}
				}
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return roleList;
	}

	public List<RoleInfo.Builder> getFightAssistanceInfo(int playerId){
		List<RoleInfo.Builder> roleList = new ArrayList<>();
		try {
			
			PlayerSnapshotInfo.Builder playerSnapshot = getPlayerSnapShot(playerId).clone();
			if (playerSnapshot != null) {
				List<Integer> roleIdCloneList = new ArrayList<>(playerSnapshot.getFightingRoleIdList());
				if (roleIdCloneList != null && roleIdCloneList.size() > 0) {
					for (Integer itemId : roleIdCloneList) {
						RoleInfo.Builder roleInfo = getActiviteRoleInfo(playerId, itemId);
						if (roleInfo != null) {
							roleList.add(roleInfo);
						}
					}
				}
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return roleList;
	}

	/**
	 * 获取已激活佣兵信息
	 * @param playerId
	 * @param itemId
	 * @return
	 */
	public RoleInfo.Builder getActiviteRoleInfo(int playerId, int idx) {
		try {
			PlayerSnapshotInfo.Builder playerSnapshot = getPlayerSnapShot(playerId).clone();
			if (playerSnapshot != null) {
				List<RoleInfo.Builder> shotList = new ArrayList<>(playerSnapshot.getMercenaryInfoBuilderList());
				if (shotList != null && shotList.size() > 0) {
					for (RoleInfo.Builder mercenaryInfo : shotList) {
						if (mercenaryInfo.getActiviteState() == Const.RoleActiviteState.IS_ACTIVITE_VALUE && idx == mercenaryInfo.getItemId()) {
							return mercenaryInfo;
						}
					}
				}
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return null;
	}

	/**
	 * 缓存玩家快照
	 * 
	 * @param player
	 * @return
	 */
	public boolean cacheSnapshot(int playerId, PlayerSnapshotInfo.Builder playerSnapshotInfo) {
		return currentStrategy.setSnapShot(playerId, playerSnapshotInfo);
	}

	/**
	 * 根据玩家名称获取快照;
	 * 
	 * @param playerName
	 * @return
	 */
	public PlayerSnapshotInfo.Builder getPlayerSnapShot(String playerName) {
		// playerId从缓存中获取
		int playerId = ServerData.getInstance().queryPlayerIdByName(playerName);
		if (playerId <= 0) {
			return null;
		}
		return getPlayerSnapShot(playerId);
	}
}
