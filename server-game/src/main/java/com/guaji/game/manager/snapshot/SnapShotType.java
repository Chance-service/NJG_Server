package com.guaji.game.manager.snapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * 快照类型;
 * 
 * @author crazyjohn
 *
 */
public enum SnapShotType {
	/** 本地快照 */
	LOCAL(1) {
		@Override
		public ISnapShotStrategy create() {
			return new LocalSnapShotStrategy();
		}
	},
	/** redis快照 */
	REDIS(2) {
		@Override
		public ISnapShotStrategy create() {
			return new RedisSnapShotStrategy();
		}
	},
	/** memcached快照 */
	MEMCACHED(3) {
		@Override
		public ISnapShotStrategy create() {
			return new MemcachedSnapShotStrategy();
		}
	};

	private int type;
	private static Map<Integer, SnapShotType> types = new HashMap<Integer, SnapShotType>();

	static {
		for (SnapShotType each : SnapShotType.values()) {
			types.put(each.type, each);
		}
	}

	SnapShotType(int type) {
		this.type = type;
	}

	public static SnapShotType typeOf(int snapShotStrategy) {
		return types.get(snapShotStrategy);
	}

	public abstract ISnapShotStrategy create();

}
