package org.guaji.db.cache;


import java.util.List;

import org.guaji.log.Log;
import org.guaji.os.MyException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.SafeEncoder;

public class MemCacheDB {
	/**
	 * redis 客户端连接池
	 */
	private JedisPool jedisPool;
	
	private Jedis jedis;
	
	
	/**
	 * 初始化redis客户端
	 * 
	 * @param addr
	 * @param port
	 * @return
	 */
	public boolean initAsRedis(String addr, int port,String pwd,int timeout) {
		try {
			
			
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxWaitMillis(6000);
			config.setTestOnBorrow(true);
			jedisPool = new JedisPool(config,addr,port,timeout,pwd);
			if (jedisPool.getNumActive() < 0) {
				Log.errPrintln("init jedis's pool fail ......");
				return false;
			}
			jedis = jedisPool.getResource();
			Log.logPrintln("jedis pool initialize, addr: " + addr + ", port: " + port);
			return true;
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return false;
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		if (jedisPool != null) {
			return jedis.get(key);
		}
		return null;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public byte[] getBytes(String key) {
		if (jedisPool != null) {
			return jedis.get(key.getBytes());
		}
		return null;
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean setString(String key, String value) {
		if (jedisPool != null) {
			return jedis.set(key, (String)value) != null;
		}
		return false;
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean setBytes(String key, byte[] value) {
		if (jedisPool != null) {
			return jedis.set(key.getBytes(), value) != null;
		}
		return false;
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean setString(String key, String value, int expireSeconds) {
		if (jedisPool != null) {
			jedis.set(key, (String)value);
			jedis.expire(key, expireSeconds);
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean setBytes(String key, byte[] value, int expireSeconds) {
		if (jedisPool != null) {
			jedis.set(key.getBytes(), value);
			jedis.expire(key.getBytes(), expireSeconds);
			return true;
		}
		return false;
	}
	
	/**
	 * 存在key
	 * 
	 * @param key
	 * @return
	 */
	public boolean exists(String key) {
		if (jedisPool != null) {
			return jedis.exists(key);
		}
		return false;
	}
	
	/**
	 * 删除key
	 * 
	 * @param key
	 * @return
	 */
	public boolean delete(String key) {
		if (jedisPool != null) {
			return jedis.del(key) > 0;
		}
		return false;
	}
	
	/**
	 * 存储到redis key-list
	 * @param id
	 * @param cdk
	 */
	public void addList(String key, byte[] cdk) {
		if (jedisPool != null) {
			jedis.lpush(cdkSrc(key), cdk);
		}
	}
	
	/**
	 * 通过key取出list
	 * @param id
	 * @return
	 */
	public List<byte[]> getList(String key) {
		if (jedisPool != null) {
			return jedis.lrange(cdkSrc(key), 0, -1);
		}
		return null;
	}
	
	public static final byte[] cdkSrc(String id) {
		return SafeEncoder.encode(id);
	}
}
