package com.guaji.game.manager.snapshot;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.guaji.log.Log;
import org.guaji.os.MyException;
import org.guaji.thread.GuaJiTask;

import com.guaji.game.GsApp;
import com.guaji.game.GsConfig;
import com.guaji.game.ServerData;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.SnapshotEntity;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo.Builder;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisSnapShotStrategy extends BaseRemoteSnapShotStrategy {
	JedisPool jedisPool;
	Jedis client;
	volatile boolean needPushAllDatas = true;

	@Override
	public boolean init() {
		jedisPool = GsApp.getInstance().getJedisPool();
		client = jedisPool.getResource();
		// auth
		//client.auth(GsConfig.getInstance().getRedisPassword());
		// push datas
		// GsApp.getInstance().postCommonTask(new PushAllDataTask());
		pushAllDatas();
		return true;
	}

	private void pushAllDatas() {
		if (!needPushAllDatas) {
			return;
		}
		logger.info(String.format("Start to push player data to redis, begin time: %d", System.currentTimeMillis()));
		Map<String, Integer> nameAndIdInfos = ServerData.getInstance().nameAndIdInfos();
		int count = 0;
		int logInterval = 1000;
		for (Entry<String, Integer> eachEntry : nameAndIdInfos.entrySet()) {
			int playerId = eachEntry.getValue();
			// 是否已经存在
			String key = null;
			try {
				key = genSnapshotKey(playerId);
			} catch (Exception e) {
				MyException.catchException(e);
				logger.error(String.format("Generate key error, playerId: %d", playerId));
				continue;
			}
			if (contains(key)) {
				continue;
			}
			SnapshotEntity entity = createSnapShotEntity(playerId);
			if (entity == null) {
				logger.error(String.format("Can not create this snapshot entity, name: %s, playerId: %d",
						eachEntry.getKey(), playerId));
				continue;
			}
			setRemoteSnapshotCache(entity.getSnapshotInfo());
			++count;
			if (count % logInterval == 0) {
				logger.info(String.format("Already push player data count: %d", count));
			}
		}
		logger.info(String.format("Finish to push player data to redis, end time: %d, count: %d",
				System.currentTimeMillis(), nameAndIdInfos.size()));
	}

	private boolean contains(String key) {
		return client.exists(key);
	}

	@Override
	protected boolean doTickThings(long curTime) {
		long dbSize = 0;
		try {
			dbSize = client.dbSize();
		} catch (Exception e) {
			Log.exceptionPrint(e);
			return false;
		}
		logger.info(String.format("Reids dbSize: %d", dbSize));
		return true;
	}

	@Override
	public boolean setSnapShot(int playerId, Builder builder) {
		String key = this.genSnapshotKey(playerId);
		return doCacheSet(key, builder.build().toByteArray(), null);
	}

	@Override
	protected Builder getRemoteSnapshotCache(int playerId) {
		String objKey = genSnapshotKey(playerId);
		Object snapshotBytes;
		try {
			snapshotBytes = client.get(objKey.getBytes("UTF-8"));
			if (snapshotBytes != null) {
				PlayerSnapshotInfo.Builder builder = convertSnapshot((byte[]) snapshotBytes);
				if (builder.getVersion() == SysBasicCfg.getInstance().getPlayerSnapShotVersion()) {
					return builder;
				}
			}
		} catch (UnsupportedEncodingException e) {
			MyException.catchException(e);
		}

		return null;
	}

	@Override
	protected boolean doCacheSet(String key, byte[] bytes, Date expiry) {
		byte[] keyBytes;
		try {
			keyBytes = key.getBytes("UTF-8");
			client.set(keyBytes, bytes);
		} catch (UnsupportedEncodingException e) {
			MyException.catchException(e);
		}
		return true;
	}

	/**
	 * 推送数据到远端redis的任务;
	 * 
	 * @author crazyjohn;
	 *
	 */
	 class PushAllDataTask extends GuaJiTask {

		@Override
		protected int run() {
			pushAllDatas();
			return 0;
		}

	}
}
