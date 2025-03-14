package com.guaji.game.manager.snapshot;

import java.util.Calendar;
import java.util.Date;

import org.guaji.os.GuaJiTime;

import com.guaji.game.GsConfig;
import com.guaji.game.ServerData;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.entity.SnapshotEntity;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo.Builder;

public abstract class BaseRemoteSnapShotStrategy extends BaseSnapShotStrategy {

	protected String genSnapshotKey(int playerId) {
		String game = GsConfig.getInstance().getGameId();
		String platform = GsConfig.getInstance().getPlatform();
		int serverId = ServerData.getInstance().queryServerIdByPlayerId(playerId);
		String puid = ServerData.getInstance().queryPuidByPlayerId(playerId);
		String objKey = String.format(GsConst.MemCacheObjKey.MC_SNAPSHOT_OBJKEY_FMT, game, platform, serverId, puid);
		return objKey;
	}

	@Override
	protected Builder querySnapShot(int playerId) {
		// get from cache
		Builder cache = getRemoteSnapshotCache(playerId);
		if (cache != null) {
			return cache;
		}
		// create entity
		SnapshotEntity entity = this.createSnapShotEntity(playerId);
		if (entity != null) {
			setRemoteSnapshotCache(entity.getSnapshotInfo());
			return entity.getSnapshotInfo();
		}
		return null;
	}

	protected PlayerSnapshotInfo.Builder convertSnapshot(byte[] snapshotBytes) {
		return BuilderUtil.convertSnapshot(snapshotBytes);
	}

	/**
	 * 更新远端Memcache玩家快照缓存
	 * 
	 * @param snapshot
	 * @return
	 */
	protected boolean setRemoteSnapshotCache(PlayerSnapshotInfo.Builder snapshot) {
		if (snapshot != null) {
			int playerId = snapshot.getPlayerId();
			String objKey = genSnapshotKey(playerId);

			Calendar calendar = GuaJiTime.getCalendar();
			calendar.add(Calendar.HOUR_OF_DAY, SysBasicCfg.getInstance().getCmcObjInvalidHours());
			Date expiry = calendar.getTime();

			byte[] snapshotBytes = snapshot.build().toByteArray();
			doCacheSet(objKey, snapshotBytes, expiry);
		}
		return true;
	}

	@Override
	public boolean onAllianceDataChanged(PlayerAllianceEntity allianceEntity) {
		int offlinePlayerId = allianceEntity.getPlayerId();
		PlayerSnapshotInfo.Builder playerSnapshotInfo = getRemoteSnapshotCache(offlinePlayerId);
		if (playerSnapshotInfo != null) {
			playerSnapshotInfo.setAllianceInfo(BuilderUtil.genAllianceBuilder(allianceEntity));
			return setRemoteSnapshotCache(playerSnapshotInfo);
		}
		return true;
	}

	@Override
	public void onPlayerLogin(int playerId) {

	}

	protected abstract boolean doCacheSet(String key, byte[] bytes, Date expiry);

	protected abstract PlayerSnapshotInfo.Builder getRemoteSnapshotCache(int playerId);

}
