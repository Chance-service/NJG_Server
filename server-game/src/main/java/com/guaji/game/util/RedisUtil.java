package com.guaji.game.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 
 * @author ManGao
 *
 */
public class RedisUtil {

	public static void main(String[] args) throws Exception {

		JedisPool pool = new JedisPool("203.90.234.115", 6379);
		Jedis jedis = pool.getResource();

		for (int i = 10; i < 16; i++) {
			Random r = new Random();
			FBFriendBean bean = new FBFriendBean();
			bean.setArenaRank(r.nextInt(56660));
			bean.setFightValue(r.nextInt(10000));
			bean.setId("R2_" + i);
			bean.setLastServer(String.valueOf(r.nextInt(15)));
			bean.setLevel(i * 5);
			bean.setVip(0);
			bean.putRedis(jedis);
		}
		FBFriendBean bean = getRedisObj(jedis, FBFriendBean.class, "R2_14");
		System.out.println(bean.getArenaRank());
		pool.close();
	}

	public static void reSetValue(Jedis jedis, IRedisSave obj, String... fileds) {

		Map<String, String> map = getObjRedisMap(obj, fileds);
		if (map == null || map.size() < 1) {
			return;
		}
		jedis.hmset(obj.getId(), map);
	}

	/**
	 * @param jedis
	 * @param clazz
	 * @param key
	 * @return
	 */
	public static <T> T getRedisObj(Jedis jedis, Class<T> clazz, String key) {

		T t = null;

		if (!jedis.exists(key)) {
			return null;
		}
		try {

			t = clazz.newInstance();
			Map<String, String> map = jedis.hgetAll(key);

			for (Field field : clazz.getDeclaredFields()) {

				field.setAccessible(true);
				String value = map.get(field.getName());
				if (value == null) {
					if (!field.getName().equals("id")) {
						continue;
					}
					value = key;
				}

				Class<?> filedClazz = field.getType();
				Constructor<?> con = filedClazz.getConstructor(value.getClass());
				field.set(t, con.newInstance(value));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return t;
	}

	public static Map<String, String> getObjRedisMap(Object obj, String... fileds) {
		if (fileds.length > 0) {
			Map<String, String> map = new HashMap<String, String>();
			try {
				Class<?> clazz = obj.getClass();

				for (String name : fileds) {

					Field field = clazz.getDeclaredField(name);
					if (field == null) {
						continue;
					}
					field.setAccessible(true);
					map.put(field.getName(), field.get(obj).toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return map;

		} else {

			return getObjRedisMap(obj);
		}
	}

	public static Map<String, String> getObjRedisMap(Object obj) {

		Map<String, String> map = new HashMap<String, String>();

		try {
			Class<?> clazz = obj.getClass();

			for (Field field : clazz.getDeclaredFields()) {
				field.setAccessible(true);
				map.put(field.getName(), field.get(obj) + "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;

	}

	/**
	 * 
	 * redis封装对象接口
	 */
	static interface IRedisSave {
		/**
		 * 获取对象id
		 * 
		 * @return
		 */
		public String getId();

		public void putRedis(Jedis jedis);
	}

	/**
	 * Facebook好友信息封装类
	 * 
	 * @author tianzhiyuan
	 *
	 */
	public static class FBFriendBean implements IRedisSave {
		private String id;// puid
		private String lastServer;// 最后登录服务器id
		private Integer level;// 等级
		private Integer fightValue;// 战力
		private Integer vip;// VIP等级
		private Integer arenaRank;// 竞技场排名

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getLastServer() {
			return lastServer;
		}

		public void setLastServer(String lastServer) {
			this.lastServer = lastServer;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public int getFightValue() {
			return fightValue;
		}

		public void setFightValue(int fightValue) {
			this.fightValue = fightValue;
		}

		public int getVip() {
			return vip;
		}

		public void setVip(int vip) {
			this.vip = vip;
		}

		public int getArenaRank() {
			return arenaRank;
		}

		public void setArenaRank(int arenaRank) {
			this.arenaRank = arenaRank;
		}

		public void putRedis(Jedis jedis) {
			reSetValue(jedis, this, "level", "lastServer", "fightValue", "vip", "arenaRank");
		}

	}

}
