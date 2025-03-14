package com.guaji.game.manager.snapshot;

import java.util.Date;

import org.guaji.os.MyException;

import com.danga.MemCached.MemCachedClient;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo.Builder;
import com.guaji.game.util.GsConst;

public class MemcachedSnapShotStrategy extends BaseRemoteSnapShotStrategy {
	/** memcached client */
	public static MemCachedClient client = null;
	public int remoteCacheStatus = 5;

	@Override
	public boolean init() {
		client = new MemCachedClient();
		return true;
	}

	@Override
	public boolean setSnapShot(int playerId, Builder playerSnapshotInfo) {
		return setRemoteSnapshotCache(playerSnapshotInfo);
	}

	/**
	 * 获取远端Memcache中的玩家快照缓存
	 * 
	 * @param playerId
	 * @return
	 */
	protected PlayerSnapshotInfo.Builder getRemoteSnapshotCache(int playerId) {
		String objKey = genSnapshotKey(playerId);
		Object snapshotBytes = client.get(objKey);
		if (snapshotBytes != null) {
			PlayerSnapshotInfo.Builder builder = convertSnapshot((byte[]) snapshotBytes);
			if (builder.getVersion() == SysBasicCfg.getInstance().getPlayerSnapShotVersion()) {
				return builder;
			}
		}
		return null;
	}

	/**
	 * 保持MemcacheClient连接始终活着
	 */
	private boolean keepMemCacheClientAlive() {
		if (client != null) {
			synchronized (client) {
				try {
					Object keepAliveObj = client.get(GsConst.MemCacheObjKey.MC_KEEP_ALIVE_OBJKEY);
					if (keepAliveObj == null) {
						if (!client.add(GsConst.MemCacheObjKey.MC_KEEP_ALIVE_OBJKEY, "hawk")) {
							// 保活失败, 远程CMem服务异常
							remoteCacheStatus--;
							logger.info("remote - cmemcache service exception, status : {}", remoteCacheStatus);
							return false;
						} else {
							// 保活成功, 状态重置
							remoteCacheStatus = 5;
							return true;
						}
					}
				} catch (Exception e) {
					MyException.catchException(e);
				}
			}
		}
		return false;
	}

	@Override
	protected boolean doTickThings(long curTime) {
		return keepMemCacheClientAlive();
	}

	@Override
	protected boolean doCacheSet(String key, byte[] bytes, Date expiry) {
		return client.set(key, bytes, expiry);
	}

}
