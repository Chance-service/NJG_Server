package com.guaji.game.manager.snapshot;

import java.util.Date;
import java.util.Iterator;

import org.guaji.db.DBManager;
import org.guaji.os.MyException;
import org.guaji.os.GuaJiTime;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.entity.SnapshotEntity;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo.Builder;

/**
 * 本地快照实现;
 */
public class LocalSnapShotStrategy extends BaseSnapShotStrategy {
	/** 实体缓存 */
	private ConcurrentLinkedHashMap<Integer, SnapshotEntity> playerSnapShotMap;

	@Override
	public boolean init() {
		ConcurrentLinkedHashMap.Builder<Integer, SnapshotEntity> mapBuilder = new ConcurrentLinkedHashMap.Builder<Integer, SnapshotEntity>();
		mapBuilder.maximumWeightedCapacity(SysBasicCfg.getInstance().getMaxPlayerSnapShotQty());
		playerSnapShotMap = mapBuilder.build();
		return true;
	}

	@Override
	public boolean setSnapShot(int playerId, Builder playerSnapshotInfo) {
		SnapshotEntity snapshotEntity = playerSnapShotMap.get(playerId);
		if (snapshotEntity == null) {
			snapshotEntity = new SnapshotEntity();
			playerSnapShotMap.put(playerId, snapshotEntity);
			snapshotEntity.setPlayerId(playerId);
		}
		snapshotEntity.setSnapshotInfo(playerSnapshotInfo);
		snapshotEntity.notifyUpdate(true);
		return true;
	}

	/**
	 * 从内存中拉取数据，如果没有，从数据库快照拉取，然后缓存
	 * @param playerId
	 * @return
	 */
	public SnapshotEntity getFromRemoteCache(int playerId) {
		// 缓存中取
		SnapshotEntity playerSnapshotEntity = playerSnapShotMap.get(playerId);
		if (playerSnapshotEntity != null) {
			return playerSnapshotEntity;
		}
		// 数据库中取
		playerSnapshotEntity = loadFromDB(playerId);
		if (playerSnapshotEntity == null) {
			return null;
		}
		// 放入缓存
		playerSnapshotEntity.setUpdateTime(GuaJiTime.getCalendar().getTime());
		playerSnapShotMap.put(playerSnapshotEntity.getPlayerId(), playerSnapshotEntity);
		return playerSnapshotEntity;
	}

	@Override
	public boolean onAllianceDataChanged(PlayerAllianceEntity allianceEntity) {
		int playerId = allianceEntity.getPlayerId();
		PlayerSnapshotInfo.Builder snapshotInfo = querySnapShot(playerId);
		if (snapshotInfo != null) {
			snapshotInfo.setAllianceInfo(BuilderUtil.genAllianceBuilder(allianceEntity));
		}
		if (playerSnapShotMap.containsKey(playerId)) {
			SnapshotEntity snapshotEntity = playerSnapShotMap.get(playerId);
			snapshotEntity.setSnapshotInfo(snapshotInfo);
			snapshotEntity.notifyUpdate(true);
		}
		return true;
	}

	@Override
	protected void evictCacheData(long curTime) {
		int size = playerSnapShotMap.size();
		try {
			Date now = GuaJiTime.getCalendar().getTime();
			Iterator<SnapshotEntity> iterator = playerSnapShotMap.values().iterator();
			while (iterator.hasNext()) {
				SnapshotEntity entity = iterator.next();
				Date entityDate = entity.getUpdateTime();
				if (now.getTime() - entityDate.getTime() > 3 * 60 * 1000) {
					iterator.remove();
				}
			}
			logger.info("local - removeSnapshotEntityOnTick : {}/{}", playerSnapShotMap.size(), size);

		} catch (Exception e) {
			e.printStackTrace();
			MyException.catchException(e);
		}
	}

	@Override
	public void onPlayerLogin(int playerId) {
		playerSnapShotMap.remove(playerId);
	}

	@Override
	protected Builder querySnapShot(int playerId) {
		// 从本地缓存(内存中)获取,如果内存不存在从数据库快照中拉取
		SnapshotEntity playerSnapshotEntity = getFromRemoteCache(playerId);
		if (playerSnapshotEntity != null) {
			return playerSnapshotEntity.getSnapshotInfo();
		}
		//不在缓存中，且数据库不存在快照，那么创建快照，并存到本地缓存
		playerSnapshotEntity = this.createSnapShotEntity(playerId);
		if (null == playerSnapshotEntity) {
			return null;
		}
		playerSnapShotMap.put(playerId, playerSnapshotEntity);
		DBManager.getInstance().create(playerSnapshotEntity);
		return playerSnapshotEntity.getSnapshotInfo();
	}

}
