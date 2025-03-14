package com.redisserver.servlet;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.guaji.cryption.Md5;
import org.guaji.log.Log;

import com.redisserver.JediseConfig;
import com.redisserver.ServiceContext;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * 用户数据中心，方便查询和返查询，避免redies并发问题
 * 
 * @author Callan
 */
public class AccountDataCenter extends HttpServlet {

	private static final long serialVersionUID = 424894698022935105L;

	private final Logger logger = Logger.getLogger(JediseConfig.class.getName());

	private static AccountDataCenter instance;

	private static ConcurrentHashMap<String, HashMap<String, String>> accodeKeyTable;
	private static ConcurrentHashMap<String, String> gcidKeyTable;
	private static ConcurrentHashMap<String, String> gpidKeyTable;

	private static ConcurrentHashMap<String, HashMap<String, String>> dvidKeyTable;// 设备ID索引，key为上次登录accode，和移行设备码

	private static final String GCID = "gcid";
	private static final String GPID = "gpid";
	private static final String DVID = "dvid";
	private static final String ACCODE = "accode";
	private static final String PWD = "password";
	private static final String MVCODE = "moveaccode";

	// 苹果绑定前缀
	private static final String KEY_GCBD = "GC";
	// 谷歌绑定前缀
	private static final String KEY_GPBD = "GP";

	public static AccountDataCenter getInstance() {
		if (instance != null) {
			return instance;
		}

		instance = new AccountDataCenter();

		return instance;
	}

	public void initData() {
		String accode = null;
		String dvid;

		accodeKeyTable = new ConcurrentHashMap<>();
		gcidKeyTable = new ConcurrentHashMap<>();
		gpidKeyTable = new ConcurrentHashMap<>();

		dvidKeyTable = new ConcurrentHashMap<>();

		if (ServiceContext.jedisPool == null) {
			return;
		}

		int count = 0;

		/*
		 * synchronized (ServiceContext.jedisPool) { try { Jedis jedis =
		 * ServiceContext.jedisPool.getResource();
		 * 
		 * if (jedis == null) { return; }
		 * 
		 * Set<String> puids = jedis.keys("*");
		 * 
		 * Iterator<String> it = puids.iterator();
		 * 
		 * while (it.hasNext()) { count++; accode = it.next();
		 * 
		 * if (!jedis.type(accode).equals("hash")) { String type = jedis.type(accode);
		 * logger.error("读取总表数据错误!" + accode + type);
		 * 
		 * continue; }
		 * 
		 * if (jedis.hlen(accode) != 4) { // 设备对应最后一次的code读取 if (jedis.hlen(accode) ==
		 * 2) { dvid = accode;
		 * 
		 * if (!"".equals(dvid)) { List<String> lid = jedis.hmget(dvid, ACCODE, MVCODE);
		 * 
		 * if (lid.size() != 2) { logger.error("数据库中有错误的数据信息！");
		 * 
		 * continue; } else { if (!dvidKeyTable.containsKey(dvid)) { HashMap<String,
		 * String> map = new HashMap<String, String>(); map.put(ACCODE, lid.get(0));
		 * map.put(MVCODE, lid.get(1)); dvidKeyTable.put(dvid, map);
		 * 
		 * } else { logger.error("读取总表数据错误！"); continue; } }
		 * 
		 * } else { logger.error("设备码为空！"); continue;
		 * 
		 * } } continue; }
		 * 
		 * List<String> lid = jedis.hmget(accode, GCID, GPID, DVID, PWD);
		 * 
		 * if (lid.size() != 4) { logger.error("数据库中有错误的数据信息！");
		 * 
		 * continue; } else { // 构建总表，puid索引 if (!accodeKeyTable.containsKey(accode)) {
		 * HashMap<String, String> map = new HashMap<String, String>();
		 * 
		 * map.put(GCID, lid.get(0)); map.put(GPID, lid.get(1)); map.put(DVID,
		 * lid.get(2)); map.put(PWD, lid.get(3)); accodeKeyTable.put(accode, map); }
		 * else { logger.error("读取总表数据错误！"); continue;
		 * 
		 * }
		 * 
		 * // 构建gcid翻查表 if (!"".equals(lid.get(0))) { if
		 * (!gcidKeyTable.containsKey(lid.get(0))) { gcidKeyTable.put(lid.get(0),
		 * accode); } }
		 * 
		 * // 构建gpid反查表 if (!"".equals(lid.get(1))) { if
		 * (!gpidKeyTable.containsKey(lid.get(1))) { gpidKeyTable.put(lid.get(1),
		 * accode); } }
		 * 
		 * }
		 * 
		 * }
		 * 
		 * } catch (Exception e) {
		 * 
		 * logger.error("读取总表数据错误！" + e + accode); return; } }
		 */

		logger.info("加载结束，共完成加载条目" + count + "个.");

		ServiceContext.setAccountDataCenterState(true);
		return;
	}

	/**
	 * 更新数据到redies
	 */
	private void updateDataToRedies(String accode) {
		synchronized (ServiceContext.jedisPool) {
			try {
				Jedis jedis = ServiceContext.jedisPool.getResource();
				jedis.hmset(accode, accodeKeyTable.get(accode));

				if (jedis != null && ServiceContext.jedisPool != null) {
					ServiceContext.jedisPool.returnResource(jedis);
				}
			} catch (Exception e) {
				logger.error("修改数据库错误" + e);
			}
		}
		return;
	}

	private void updateDataToRediesDvidTable(String dvid) {
		synchronized (ServiceContext.jedisPool) {
			try {
				Jedis jedis = ServiceContext.jedisPool.getResource();
				jedis.hmset(dvid, dvidKeyTable.get(dvid));

				if (jedis != null && ServiceContext.jedisPool != null) {
					ServiceContext.jedisPool.returnResource(jedis);
				}
			} catch (Exception e) {
				logger.error("修改数据库错误" + e);
			}
		}
		return;
	}

	/**
	 * 删除某个accode条目
	 */

	private void deleteDataToRedies(String accode) {
		synchronized (ServiceContext.jedisPool) {
			try {
				Jedis jedis = ServiceContext.jedisPool.getResource();
				jedis.del(accode);

				if (jedis != null && ServiceContext.jedisPool != null) {
					ServiceContext.jedisPool.returnResource(jedis);
				}
			} catch (Exception e) {
				logger.error("删除条目完成" + e);
			}
		}

	}

	/**
	 * 添加一个新的条目到总表（accodeKeyTable）
	 * 
	 * @param puid
	 * @param fbid
	 * @param gcid
	 * @param gpid
	 * @param accode
	 */
	private void addToAccodeKeyTable(String accode, String gcid, String gpid, String dvid, String pwd) {
		if (accodeKeyTable.containsKey(accode)) {
			logger.error("添加失败，accode存在，accode为" + accode);
			return;
		}

		HashMap<String, String> map = new HashMap<String, String>();

		map.put(GCID, gcid);
		map.put(GPID, gpid);
		map.put(DVID, dvid);
		map.put(PWD, pwd);

		accodeKeyTable.put(accode, map);

		updateDataToRedies(accode);

		return;
	}

	/**
	 * 添加gcid到 gcid反查表
	 * 
	 * @param accode
	 * @param gcid
	 */
	public void addToGcidKeyTable(String accode, String gcid) {
		if (gcidKeyTable.containsKey(gcid)) {
			logger.error("添加失败，gcid存在，gcid为" + gcid);
			return;
		}
		gcidKeyTable.put(gcid, accode);
		return;
	}

	/**
	 * 添加gpid到gpid反查表
	 * 
	 * @param puid
	 * @param gpid
	 */
	public void addToGpidKeyTable(String accode, String gpid) {
		if (gpidKeyTable.containsKey(gpid)) {
			logger.error("添加失败，gpid存在，gpid为" + gpid);
			return;
		}
		gpidKeyTable.put(gpid, accode);
		return;
	}

	/**
	 * 添加pwd到总表
	 * 
	 * @param puid
	 * @param gpid
	 */
	public void addPwdToAccodeKeyTable(String accode, String pwd) {
		if (accodeKeyTable.containsKey(accode)) {
			logger.error("添加失败，accode存在，accode为" + accode);
			return;
		}
		accodeKeyTable.get(accode).put(PWD, pwd);
		return;
	}

	/**
	 * 添加accode到dvid反查表
	 * 
	 * @param ACCODE
	 * @param DVID
	 */
	public void addToDvidKeyTable(String accode, String dvid) {
		if (dvidKeyTable.containsKey(dvid)) {
			logger.error("添加失败，dvid存在，dvid为" + dvid);
			return;
		}
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(ACCODE, accode);
		dvidKeyTable.put(dvid, map);

		return;
	}

	/**
	 * 添加mvcode到dvid反查表
	 * 
	 * @param MVCODE
	 * @param DVID
	 */
	public void addMvcodeToDvidKeyTable(String mvcode, String dvid) {
		if (!dvidKeyTable.containsKey(dvid)) {
			logger.error("添加失败，dvid存在，dvid为" + dvid);
			return;
		}
		dvidKeyTable.get(dvid).put(MVCODE, mvcode);
		return;
	}

	/**
	 * 从总表中获取gcid的值
	 * 
	 * @param puid
	 * @return
	 */
	public String getGcidFromAccodeKeyTable(String accode) {
		/*
		 * if (accodeKeyTable.containsKey(accode)) { HashMap<String, String> map =
		 * accodeKeyTable.get(accode);
		 * 
		 * if (map.containsKey(GCID)) { String gcid = map.get(GCID); return gcid; } }
		 * 
		 * return null;
		 */
		if (ServiceContext.jedisPool == null) {
			return null;
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return null;
		}

		try {

			if (jedis.exists(accode)) {
				Map<String, String> accMap = jedis.hgetAll(accode);
				return accMap.get(GCID);
			} else {
				return null;
			}

		} catch (Exception ex) {
			logger.error(accode + "***GcidFromAccodeKey 异常");
			closeJedis(jedis);
			return null;
		} finally {
			closeJedis(jedis);
		}

	}

	/**
	 * 从总表中获取gcid的值
	 * 
	 * @param puid
	 * @return
	 */
	public String getGpidFromAccodeKeyTable(String accode) {
		/*
		 * if (accodeKeyTable.containsKey(accode)) { HashMap<String, String> map =
		 * accodeKeyTable.get(accode);
		 * 
		 * if (map.containsKey(GPID)) { String gpid = map.get(GPID); return gpid; } }
		 * 
		 * return null;
		 */

		if (ServiceContext.jedisPool == null) {
			return null;
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return null;
		}

		try {
			if (jedis.exists(accode)) {
				Map<String, String> accMap = jedis.hgetAll(accode);
				return accMap.get(GPID);
			} else {
				return null;
			}

		} catch (Exception ex) {
			logger.error(accode + "***修改移行密码 s失败");
			closeJedis(jedis);
			return null;
		} finally {
			closeJedis(jedis);
		}

	}

	/**
	 * 从总表中获取某个accode最后一次登录的设备
	 * 
	 * @param puid
	 * @return
	 */
	public String getDvidFromAccodeKeyTable(String accode) {
		/*
		 * if (accodeKeyTable.containsKey(accode)) { HashMap<String, String> map =
		 * accodeKeyTable.get(accode);
		 * 
		 * if (map.containsKey(DVID)) { String dvid = map.get(DVID); return dvid; } }
		 * 
		 * return null;
		 */
		if (ServiceContext.jedisPool == null) {
			return null;
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return null;
		}

		try {

			Map<String, String> accMap = jedis.hgetAll(accode);
			if (accMap.isEmpty()) {
				logger.error("修改失败，dvid不存在，dvid为" + accode);
				return null;
			}
			return accMap.get(DVID);

		} catch (Exception ex) {
			logger.error(accode + "***修改移行密码 s失败");
			return null;
		}

	}

	/**
	 * 从总表中获取某个pwd
	 * 
	 * @param puid
	 * @return
	 */
	public String getPwdFromAccodeKeyTable(String accode) {
		/*
		 * if (accodeKeyTable.containsKey(accode)) { HashMap<String, String> map =
		 * accodeKeyTable.get(accode);
		 * 
		 * if (map.containsKey(PWD)) { String pwd = map.get(PWD); return pwd; } }
		 * 
		 * return null;
		 */
		if (ServiceContext.jedisPool == null) {
			return null;
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return null;
		}

		try {
			if (jedis.exists(accode)) {
				Map<String, String> accMap = jedis.hgetAll(accode);
				return accMap.get(PWD);

			}
			return null;

		} catch (Exception ex) {
			logger.error(accode + "***修改移行密码 s失败");
			closeJedis(jedis);
			return null;
		} finally {
			closeJedis(jedis);
		}

	}

	/**
	 * 获取accode从gc反查表中
	 * 
	 * @param gcid
	 * @return
	 */
	public String getAccodeFromGcidKeyTable(String gcid) {
		/*
		 * if (gcidKeyTable.containsKey(gcid)) { String accode = gcidKeyTable.get(gcid);
		 * 
		 * return accode; }
		 * 
		 * return null;
		 */

		if (ServiceContext.jedisPool == null) {
			return null;
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return null;
		}

		try {
			String bindKey = KEY_GCBD + gcid;
			if (jedis.exists(bindKey)) {
				return jedis.get(bindKey);

			} else {
				return null;
			}
			/*
			 * Map<String, String> dvidMap = jedis.hgetAll(dvid); if (dvidMap.isEmpty()) {
			 * logger.error("修改失败，dvid不存在，dvid为" + dvid); return null; }
			 */

		} catch (Exception ex) {
			logger.error(KEY_GCBD + gcid + "***修改移行密码 s失败");
			closeJedis(jedis);
			return null;
		} finally {
			closeJedis(jedis);
		}
	}

	/**
	 * 获取accode从gc反查表中
	 * 
	 * @param gpid
	 * @return
	 */
	public String getAccodeFromGpidKeyTable(String gpid) {
		/*
		 * if (gpidKeyTable.containsKey(gpid)) { String accode = gpidKeyTable.get(gpid);
		 * 
		 * return accode; }
		 * 
		 * 
		 * return null;
		 */

		if (ServiceContext.jedisPool == null) {
			return null;
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return null;
		}

		try {
			String bindKey = KEY_GPBD + gpid;
			if (jedis.exists(bindKey)) {
				return jedis.get(bindKey);
			}
			/*
			 * Map<String, String> dvidMap = jedis.hgetAll(dvid); if (dvidMap.isEmpty()) {
			 * logger.error("修改失败，dvid不存在，dvid为" + dvid); return null; }
			 */

		} catch (Exception ex) {
			logger.error(KEY_GPBD + gpid + "***修改移行密码 s失败");
			closeJedis(jedis);
			return null;
		} finally {
			closeJedis(jedis);
		}
		return null;
	}

	/**
	 * 获取accode从dvid反查表中（获取此设备上一次登录的accode）
	 * 
	 * @param dvid
	 * @return
	 */
	public String getAccodeFromDvidKeyTable(String dvid) {
		/*
		 * if (dvidKeyTable.containsKey(dvid)) { String accode =
		 * dvidKeyTable.get(dvid).get(ACCODE);
		 * 
		 * return accode; }
		 * 
		 * return null;
		 */

		if (ServiceContext.jedisPool == null) {
			return null;
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return null;
		}

		try {
			if (jedis.exists(dvid)) {
				Map<String, String> dvidMap = jedis.hgetAll(dvid);
				return dvidMap.get(ACCODE);

			}
			/*
			 * Map<String, String> dvidMap = jedis.hgetAll(dvid); if (dvidMap.isEmpty()) {
			 * logger.error("修改失败，dvid不存在，dvid为" + dvid); return null; }
			 */

		} catch (Exception ex) {
			logger.error(dvid + "***修改移行密码 s失败");
			closeJedis(jedis);
			return null;
		} finally {
			closeJedis(jedis);
		}
		return null;
	}

	/**
	 * 获取mvcode从dvid反查表中（获取此设备上一次移行的code）
	 * 
	 * @param dvid
	 * @return
	 */
	public String getMvcodeFromDvidKeyTable(String dvid) {
		/*
		 * if (dvidKeyTable.containsKey(dvid)) { String mvcode =
		 * dvidKeyTable.get(dvid).get(MVCODE);
		 * 
		 * return mvcode; }
		 * 
		 * return null;
		 */

		if (ServiceContext.jedisPool == null) {
			return null;
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return null;
		}

		try {

			if (jedis.exists(dvid)) {
				Map<String, String> dvidMap = jedis.hgetAll(dvid);
				return dvidMap.get(MVCODE);
			} else {
				return null;
			}

		} catch (Exception ex) {
			logger.error(dvid + "***获取移行码失败");
			closeJedis(jedis);
			return null;
		} finally {
			closeJedis(jedis);
		}

	}

	/**
	 * @Title: getDeviceInfo @Description: TODO(这里用一句话描述这个方法的作用) @param @param
	 * dvid @param @return 设定文件 @return Map<String,String> 返回类型 @throws
	 */
	public Map<String, String> getDeviceInfo(String dvid) {
		if (ServiceContext.jedisPool == null) {
			return null;
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return null;
		}

		try {

			if (jedis.exists(dvid)) {
				Map<String, String> dvidMap = jedis.hgetAll(dvid);
				return dvidMap;
			} else {
				return null;
			}

		} catch (Exception ex) {
			logger.error(dvid + "***获取移行码失败");
			closeJedis(jedis);
			return null;
		} finally {
			closeJedis(jedis);
		}
	}

	/**
	 * 查找accode是否在accode索引的总表中
	 * 
	 * @param puid
	 * @return
	 */
	public boolean checkAccodeInAccodeKeyTable(String accode) {
		/*
		 * if (accodeKeyTable.containsKey(accode)) { return true; }
		 * 
		 * return false;
		 */

		if (ServiceContext.jedisPool == null) {
			return false;
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return false;
		}

		try {
			if (jedis.exists(accode)) {
				return true;
			}

		} catch (Exception ex) {
			logger.error(accode + "checkAccodeInAccodeKeyTable  检测失败 ");
			closeJedis(jedis);
			return false;
		} finally {
			closeJedis(jedis);
		}
		return false;

	}

	/**
	 * 查找gcid是否在gcidtable翻查表中
	 * 
	 * @param puid
	 * @return
	 */
	public boolean checkGcidInGcidKeyTable(String gcid) {
		/*
		 * if (gcidKeyTable.containsKey(gcid)) { return true; } return false;
		 */

		if (ServiceContext.jedisPool == null) {
			return false;
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return false;
		}

		try {

			String gcbdKey = KEY_GCBD + gcid;
			if (jedis.exists(gcbdKey))
				return true;

		} catch (Exception ex) {
			logger.error(gcid + "checkGcidInGcidKeyTable  检测失败 ");
			closeJedis(jedis);
			return false;
		} finally {
			closeJedis(jedis);
		}
		return false;

	}

	/**
	 * 查找gpid是否在gpidtable反查表中
	 * 
	 * @param gpid
	 * @return
	 */
	public boolean checkGpidInGpidKeyTable(String gpid) {
		if (gpidKeyTable.containsKey(gpid)) {
			return true;
		}
		return false;

	}

	/**
	 * 查找dvid是否在dvidtable反查表中
	 * 
	 * @param accode
	 * @return
	 */
	public boolean checkDvidInDvidKeyTable(String dvid) {
		/*
		 * if (dvidKeyTable.containsKey(dvid)) { return true; } return false;
		 */
		if (ServiceContext.jedisPool == null) {
			return false;
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return false;
		}

		try {
			if (jedis.exists(dvid))
				return true;

		} catch (Exception ex) {

			logger.error(dvid + "***检查设备列表中是否存在 设备号");
			closeJedis(jedis);
			return false;
		} finally {
			closeJedis(jedis);
		}

		return false;

	}

	/**
	 * 修改accode索引总表
	 * 
	 * @param puid
	 * @param fbid
	 * @param gcid
	 * @param gpid
	 * @param accode
	 */
	public void modifyAccodeKeyTable(String accode, String gcid, String gpid, String dvid, String pwd) {
		if (accodeKeyTable.containsKey(accode) != true) {
			logger.error("修改没有成功，accode不存在,accode为" + accode);
			return;
		}
		HashMap<String, String> map = accodeKeyTable.get(accode);

		if (gcid != null) {
			map.put(GCID, gcid);
		}

		if (gpid != null) {
			map.put(GPID, gpid);
		}

		if (dvid != null) {
			map.put(DVID, dvid);
		}

		if (pwd != null) {
			map.put(PWD, pwd);
		}

		updateDataToRedies(accode);

		return;
	}

	/**
	 * 修改gcid反查表字段
	 * 
	 * @param puid
	 * @param gcid
	 */
	public void modifyGcidKeyTable(String accode, String gcid) {
		if (gcidKeyTable.containsKey(gcid) != true) {
			logger.error("修改没有成功，gcid不存在，gcid为" + gcid);
			return;
		}
		gcidKeyTable.put(gcid, accode);
		return;
	}

	/**
	 * 修改gpid反查表字段
	 * 
	 * @param puid
	 * @param gpid
	 */
	public void modifyGpidKeyTable(String accode, String gpid) {
		if (gpidKeyTable.containsKey(gpid) != true) {
			logger.error("修改没有成功，gpid不存在，gpid为" + gpid);
			return;
		}
		gpidKeyTable.put(gpid, accode);
		return;
	}

	/**
	 * 修改PWD在总表
	 * 
	 * @param puid
	 * @param gcid
	 */
	public void modifyPwdKeyTable(String accode, String pwd) {
		if (accodeKeyTable.containsKey(accode) != true) {
			logger.error("修改没有成功，accode不存在，accode为" + accode);
			return;
		}
		accodeKeyTable.get(accode).put(PWD, pwd);

		updateDataToRedies(accode);

		return;
	}

	/**
	 * 修改accode反查表
	 * 
	 * @param puid
	 * @param accode
	 */
	public void modifyDvidKeyTable(String accode, String dvid) {
		if (dvidKeyTable.containsKey(dvid) != true) {
			logger.error("修改没有成功，dvid不存在，dvid为" + dvid);
			return;
		}

		dvidKeyTable.get(dvid).put(ACCODE, accode);

		updateDataToRediesDvidTable(dvid);

		return;
	}

	/**
	 * 修改mvcode，dvid表
	 * 
	 * @param puid
	 * @param accode
	 */
	public void modifyMvcodeDvidKeyTable(String mvcode, String dvid) {
		if (dvidKeyTable.containsKey(dvid) != true) {
			logger.error("修改没有成功，dvid不存在，dvid为" + dvid);
			return;
		}

		dvidKeyTable.get(dvid).put(MVCODE, mvcode);

		updateDataToRediesDvidTable(dvid);

		return;
	}

	/**
	 * 删除某个accode表中的条目
	 * 
	 * @param puid
	 * @param accode
	 * @return
	 */
	public void deleteAccodeKeyTable(String accode) {
		if (accodeKeyTable.containsKey(accode) != true) {
			return;
		}

		accodeKeyTable.remove(accode);

		return;
	}

	public boolean deleteGcidKeyTable(String gcid) {
		/*
		 * if ((!gcid.equals("")) && gcid != null) { if (gcidKeyTable.containsKey(gcid))
		 * { gcidKeyTable.remove(gcid); } } if (ServiceContext.jedisPool == null) {
		 * return false; }
		 */

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return false;
		}
		synchronized (ServiceContext.jedisPool) {
			try {

				// 事务处理
				String keyBind = KEY_GCBD + gcid;
				if (jedis.exists(keyBind)) {
					jedis.del(keyBind);
					return true;
				} else {
					return false;
				}

			} catch (Exception ex) {
				logger.error(gcid + "*** 删除无效的GCID！");
				closeJedis(jedis);
				return false;
			} finally {
				closeJedis(jedis);
			}
		}
	}

	public boolean deleteGpidKeyTable(String gpid) {
		/*
		 * if ((!gpid.equals("")) && gpid != null) { if (gpidKeyTable.containsKey(gpid))
		 * { gpidKeyTable.remove(gpid); } }
		 */

		if (ServiceContext.jedisPool == null) {
			return false;
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return false;
		}
		synchronized (ServiceContext.jedisPool) {
			try {

				// 事务处理
				String keyBind = KEY_GPBD + gpid;
				if (jedis.exists(keyBind)) {
					jedis.del(keyBind);
					return true;
				} else {
					return false;
				}

			} catch (Exception ex) {
				logger.error(gpid + "*** 删除无效的GPID！");
				closeJedis(jedis);
				return false;
			} finally {
				closeJedis(jedis);
			}
		}

	}

	public boolean deleteDvidKeyTable(String dvid) {
		/*
		 * if ((!dvid.equals("")) && dvid != null) { if (dvidKeyTable.containsKey(dvid))
		 * { dvidKeyTable.remove(dvid); } }
		 */

		if (ServiceContext.jedisPool == null) {
			return false;
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return false;
		}

		synchronized (ServiceContext.jedisPool) {
			try {

				// 事务处理
				if (jedis.exists(dvid)) {
					jedis.del(dvid);
					return true;
				} else {
					return false;
				}

			} catch (Exception ex) {
				logger.error(dvid + "*** 删除无效色设备号！");
				closeJedis(jedis);
				return false;
			} finally {
				closeJedis(jedis);
			}
		}

	}

	public boolean cleanAccodeKeyTableByAccode(String accode) {
		/*
		 * String gcid, gpid, dvid;
		 * 
		 * gcid = this.getGcidFromAccodeKeyTable(accode); gpid =
		 * this.getGpidFromAccodeKeyTable(accode); dvid =
		 * this.getDvidFromAccodeKeyTable(accode);
		 * 
		 * // puidKeyTable.get(puid).put(FBID, ""); // puidKeyTable.get(puid).put(GCID,
		 * ""); // puidKeyTable.get(puid).put(GPID, ""); //
		 * puidKeyTable.get(puid).put(ACCODE, "");
		 * 
		 * if ((!gcid.equals("")) && gcid != null) { if (gcidKeyTable.containsKey(gcid))
		 * { gcidKeyTable.remove(gcid); } }
		 * 
		 * if ((!gpid.equals("")) && gpid != null) { if (gpidKeyTable.containsKey(gpid))
		 * { gpidKeyTable.remove(gpid); } }
		 * 
		 * if ((!dvid.equals("")) && dvid != null) { if (dvidKeyTable.containsKey(dvid))
		 * { dvidKeyTable.remove(dvid); } }
		 * 
		 * // add by callan 11/6 2:16 accodeKeyTable.remove(accode);
		 * 
		 * deleteDataToRedies(accode);
		 */
		if (ServiceContext.jedisPool == null) {
			return false;
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return false;
		}

		try {

			if (jedis.exists(accode)) {
				Map<String, String> accMap = jedis.hgetAll(accode);
				String gcid = accMap.get(GCID);
				String gpid = accMap.get(GPID);
				String dvid = accMap.get(DVID);
				Transaction trans = jedis.multi();
				String bindKey = KEY_GCBD + gcid;
				trans.del(bindKey);
				bindKey = KEY_GPBD + gpid;
				trans.del(bindKey);
				trans.del(accode);
				trans.del(dvid);
				trans.exec();

			}
			return true;
		} catch (Exception ex) {
			logger.error(accode + "***GcidFromAccodeKey 异常");
			closeJedis(jedis);
			return false;
		} finally {
			closeJedis(jedis);
		}

	}

	/**
	 * 新账号入数据中心
	 * 
	 * @param puid
	 * @param fbid
	 * @param gcid
	 * @param gpid
	 * @param accode
	 */
	public void addNewAccountInfo(String accode, String gcid, String gpid, String dvid, String pwd) {
		//
		addToAccodeKeyTable(accode, gcid, gpid, dvid, pwd);
		addToGcidKeyTable(accode, gcid);
		addToGpidKeyTable(accode, gpid);
		addToDvidKeyTable(accode, dvid);
		addMvcodeToDvidKeyTable("", dvid);

		updateDataToRedies(accode);
		updateDataToRediesDvidTable(dvid);
		return;
	}

	public String getDataFromDvidKeyTable(String dvid, String key) {

		if (ServiceContext.jedisPool == null) {
			return "error";
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return "error";
		}
		//
		try {
			return jedis.hgetAll(dvid).get(key);

		} catch (Exception ex) {
			logger.error("getDataFromDvidKeyTable！ 从 redis 读取 数据失败！");
			closeJedis(jedis);

		} finally {
			closeJedis(jedis);
		}

		return "error";

	}

	/**
	 * 新账号入数据中心
	 * 
	 * @param accode 账号唯一编号
	 * @param key    账号对应 平台账号、
	 */
	public String getDataFromAccodeKeyTable(String acccode, String key) {

		if (ServiceContext.jedisPool == null) {
			return "error";
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return "error";
		}

		try {
			return jedis.hgetAll(acccode).get(key);

		} catch (Exception ex) {
			logger.error("getDataFromAccodeKeyTable！ 从 redis 读取 数据失败！");
			closeJedis(jedis);
		} finally {
			closeJedis(jedis);
		}

		return "error";

	}

	/**
	 * @param accode 设备账号
	 * @param gcid   gamecenter 账号
	 * @param gpid   平台账号
	 * @param dvid   设备序列号
	 * @param pwd    移行账号密码
	 * @return
	 */
	public boolean addAccountInfo(String accode, String gcid, String gpid, String dvid, String pwd, String mvcode) {
		if (ServiceContext.jedisPool == null) {
			return false;
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return false;
		}

		synchronized (ServiceContext.jedisPool) {
			try {
				// 事物操作
				Transaction trans = jedis.multi();
				HashMap<String, String> gloablMap = new HashMap<String, String>();
				gloablMap.put(GCID, gcid);
				gloablMap.put(GPID, gpid);
				gloablMap.put(DVID, dvid);
				gloablMap.put(PWD, pwd);
				trans.hmset(accode, gloablMap);
				HashMap<String, String> devMap = new HashMap<String, String>();
				devMap.put(MVCODE, mvcode);
				devMap.put(ACCODE, accode);
				trans.hmset(dvid, devMap);
				String gcKey = KEY_GCBD + gcid;
				trans.set(gcKey, gcid);
				String gpKey = KEY_GPBD + gpid;
				trans.set(gpKey, gpid);
				trans.exec();

			} catch (Exception e) {
				logger.error("修改数据库错误" + e);
				closeJedis(jedis);
				return false;
			} finally {
				closeJedis(jedis);
			}
		}

		return true;

	}

	/**
	 * @param accode 平台账号
	 * @param gcid   苹果平台账号
	 * @param gpid   谷歌平台账号
	 * @param dvid   设备号
	 * @param pwd    密码
	 * @return
	 */
	public boolean modifyAccountInfo(String accode, String gcid, String gpid, String dvid, String pwd) {

		return false;
	}

	/**
	 * @param accode acc账号码
	 * @param pwd    移行密码
	 * @return
	 */
	public boolean modifyPwdAccout(String accode, String pwd) {

		if (ServiceContext.jedisPool == null) {
			return false;
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return false;
		}
		synchronized (ServiceContext.jedisPool) {
			try {
				// 先取值再修改
				if (jedis.exists(accode)) {
					Map<String, String> gloablMap = jedis.hgetAll(accode);
					gloablMap.put(PWD, pwd);
					jedis.hmset(accode, gloablMap);
				} else {
					return false;
				}

			} catch (Exception ex) {
				logger.error(accode + "***修改移行密码 s失败");
				closeJedis(jedis);
				return false;
			} finally {
				closeJedis(jedis);
			}
		}
		return true;
	}

	/**
	 * 修改accode反查表
	 * 
	 * @param puid
	 * @param accode
	 */
	public boolean modifyDvidKeyAccode(String accode, String dvid) {

		if (ServiceContext.jedisPool == null) {
			return false;
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return false;
		}
		synchronized (ServiceContext.jedisPool) {
			try {

				if (jedis.exists(dvid)) {
					Map<String, String> dvidMap = jedis.hgetAll(dvid);
					dvidMap.put(ACCODE, accode);
					jedis.hmset(accode, dvidMap);
				} else {
					return false;
				}
			} catch (Exception ex) {
				logger.error(accode + "***修改设备对应的平台账号 s失败");
				closeJedis(jedis);
				return false;
			} finally {
				closeJedis(jedis);
			}
		}

		return true;
	}

	/**
	 * 移行账号最终操作
	 * 
	 * @param mvcode 移行码
	 * @param dvid   设备号
	 * @return
	 */
	public boolean modifyMvcodeDvid(String mvcode, String dvid) {

		if (ServiceContext.jedisPool == null) {
			return false;
		}
		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return false;
		}

		synchronized (ServiceContext.jedisPool) {
			try {
				if (jedis.exists(dvid)) {
					Map<String, String> dvidMap = jedis.hgetAll(dvid);
					// 先取值再修改
					dvidMap.put(MVCODE, mvcode);
					jedis.hmset(dvid, dvidMap);
					return true;
				} else {
					return false;
				}

			} catch (Exception ex) {
				logger.error(dvid + "***修改移行密码 s失败");
				closeJedis(jedis);
				return false;
			} finally {
				closeJedis(jedis);
			}
		}

	}

	/**
	 * @param gameCenterId 苹果账号标识
	 * @param acccode      平台账号
	 * @return
	 */
	public boolean GameCenterBind(String gameCenterId, String acccode) {

		if (ServiceContext.jedisPool == null) {
			return false;
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return false;
		}
		synchronized (ServiceContext.jedisPool) {
			try {

				// 事务处理 同时修改连个键值
				if (jedis.exists(acccode)) {
					Map<String, String> dvidMap = jedis.hgetAll(acccode);
					Transaction trans = jedis.multi();
					dvidMap.put(GCID, gameCenterId);
					trans.hmset(acccode, dvidMap);
					String bindKey = KEY_GCBD + gameCenterId;
					trans.set(bindKey, acccode);
					trans.exec();
					return true;
				} else {
					return false;
				}

			} catch (Exception ex) {
				logger.error(acccode + "*** 苹果账号绑定失败！");
				closeJedis(jedis);
				return false;
			} finally {
				closeJedis(jedis);
			}
		}
	}

	/**
	 * 绑定谷歌账号接口
	 * 
	 * @param googlePlayId 谷歌账号编号
	 * @param acccode      平台账号
	 * @return
	 */
	public boolean GooglePlayBind(String googlePlayId, String acccode) {

		if (ServiceContext.jedisPool == null) {
			return false;
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return false;
		}
		synchronized (ServiceContext.jedisPool) {
			try {

				// 事务处理
				if (jedis.exists(acccode)) {
					Map<String, String> accMap = jedis.hgetAll(acccode);
					Transaction trans = jedis.multi();
					accMap.put(GPID, googlePlayId);
					trans.hmset(acccode, accMap);
					String bindKey = KEY_GPBD + googlePlayId;
					trans.append(bindKey, acccode);
					trans.exec();
					return true;
				} else {
					return false;
				}

			} catch (Exception ex) {
				logger.error(acccode + "*** 谷歌账号绑定失败！");
				closeJedis(jedis);
				return false;
			} finally {
				closeJedis(jedis);
			}
		}

	}

	/**
	 * @param dvid     设备序列号
	 * @param accode   平台账号
	 * @param moveCode 移行码
	 * @return
	 */
	public boolean ModifyDvidKeyTable(String dvid, String accode, String moveCode) {

		if (ServiceContext.jedisPool == null) {
			return false;
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return false;
		}
		synchronized (ServiceContext.jedisPool) {
			try {

				// 事务处理
				if (jedis.exists(dvid)) {
					Map<String, String> devMap = jedis.hgetAll(dvid);
					if (accode != null && accode.equals("")) {
						devMap.put(ACCODE, accode);
					}
					if (moveCode != null && moveCode.equals("")) {
						devMap.put(MVCODE, moveCode);
					}
					jedis.hmset(dvid, devMap);
				} else {
					Map<String, String> devMap = new HashMap<>();
					devMap.put(ACCODE, accode);
					devMap.put(MVCODE, moveCode);
					jedis.hmset(dvid, devMap);
				}

				return true;
			} catch (Exception ex) {
				logger.error(dvid + "ModifyDvidKeyTable  redis 操作失败！");
				closeJedis(jedis);
				return false;
			} finally {
				closeJedis(jedis);
			}
		}

	}

	/**
	 * 修改总表中的密码
	 * 
	 * @param accode 平台账号
	 * @param pwd    密码
	 * @return
	 */
	public boolean modifyPwdAccTable(String accode, String pwd) {

		if (ServiceContext.jedisPool == null) {
			return false;
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return false;
		}
		synchronized (ServiceContext.jedisPool) {

			try {

				Map<String, String> accMap = jedis.hgetAll(accode);
				accMap.put(PWD, pwd);
				jedis.hmset(accode, accMap);
				return true;
			} catch (Exception ex) {
				logger.error(accode + "modifyPwdAccTable  redis 操作失败！");
				closeJedis(jedis);
				return false;
			} finally {
				closeJedis(jedis);
			}
		}
	}

	public boolean addDvdInfoToDvidKeyTable(String accode, String mvcode, String dvid) {
		if (ServiceContext.jedisPool == null) {
			return false;
		}

		Jedis jedis = ServiceContext.jedisPool.getResource();

		if (jedis == null) {
			return false;
		}
		synchronized (ServiceContext.jedisPool) {

			try {
				Map<String, String> dvdMap = new HashMap<>();
				dvdMap.put(ACCODE, accode);
				dvdMap.put(MVCODE, mvcode);
				jedis.hmset(accode, dvdMap);

			} catch (Exception ex) {
				logger.error(accode + "modifyPwdAccTable  redis 操作失败！");
				closeJedis(jedis);
				return false;
			} finally {
				closeJedis(jedis);
			}
		}
		return true;
	}

	/**
	 * 
	 * closeJedis(释放redis资源)
	 * 
	 * @Title: closeJedis @param @param jedis @return void @throws
	 */
	public void closeJedis(Jedis jedis) {
		try {
			if (jedis != null) {
				jedis.close();
			}
		} catch (Exception e) {
			logger.error("释放资源异常：" + e);
		}
	}

}
