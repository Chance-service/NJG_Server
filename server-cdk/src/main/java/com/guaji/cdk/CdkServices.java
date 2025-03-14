package com.guaji.cdk;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.guaji.db.cache.MemCacheDB;
import org.guaji.os.GuaJiRand;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;
import org.guaji.util.services.CdkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guaji.cdk.data.CdkInfo;
import com.guaji.cdk.http.param.DelCdkParam;
import com.guaji.cdk.http.param.GenCdkParam;
import com.guaji.cdk.http.param.QueryCdkParam;
import com.guaji.cdk.http.param.ResetRewardParam;
import com.guaji.cdk.http.param.UseCdkParam;
import com.guaji.cdk.util.CdkUtil;
import com.guaji.cdk.data.CdkType;

/**
 * CDK服务接口
 * 
 */
public class CdkServices {
	private static Logger logger = LoggerFactory.getLogger("CdkReward");

	// 定义memcached访问关键字
	private static String REDIS_CDK_DATA_FMT = "org.guaji.cdk.datas.%s";
	private static String REDIS_CDK_TYPE_FMT = "org.guaji.cdk.type";
	private static String VALID_CDK_KEYS = "023456789abcdefghijkmnopqrstuvwxyz"; // 不包含l和1，首位不能为0
	private static String VALID_CDK16NUM_KEYS = "0123456789"; // 不包含l和1，首位不能为0

	/**
	 * 运行状态
	 */
	volatile boolean running = true;
	/**
	 * redis客户端
	 */
	private MemCacheDB redisClient = null;
	/**
	 * 单例实例对象
	 */
	private static CdkServices instance = null;

	/**
	 * 获取实例对象
	 * 
	 * @return
	 */
	public static CdkServices getInstance() {
		if (instance == null) {
			instance = new CdkServices();
		}
		return instance;
	}

	/**
	 * 初始化
	 */
	public boolean initMC(String addr, int timeout,int redisPort,String pwd) {
		// 创建连接
		redisClient = new MemCacheDB();
		if (!redisClient.initAsRedis(addr,redisPort,pwd,timeout)) {
			return false;
		}
		// 加载类型奖励
		// loadTypeRewards();
		return true;
	}

	/**
	 * 日志记录
	 * 
	 * @param msg
	 */
	public void logMsg(String msg) {
		logger.info(msg);
	}

	/**
	 * 生成激活码
	 * 
	 * @param cdkSerial
	 */
	public void fillGameAndType(char[] cdkSerial, int len) {
		

		try {
			for (int i = 0; i < len; i++) {
				if (i == 0) {
					cdkSerial[i] = VALID_CDK_KEYS.charAt(GuaJiRand.randInt(1, VALID_CDK_KEYS.length() - 1));
				} else {
					cdkSerial[i] = VALID_CDK_KEYS.charAt(GuaJiRand.randInt(0, VALID_CDK_KEYS.length() - 1));
				}
			}

		} catch (Exception e) {
			MyException.catchException(e);
		}
	}
	
	
	/**
	 * 生成激活码
	 * 
	 * @param cdkSerial
	 */
	public void fillCdkCodeByType(char[] cdkSerial, int len,CdkType Type) {
		try {
			
			switch(Type)
			{
				case UNICODE8NUM:
				{
					for (int i = 0; i < len; i++) {
						if (i == 0) {
							cdkSerial[i] = VALID_CDK_KEYS.charAt(GuaJiRand.randInt(1, VALID_CDK_KEYS.length() - 1));
						} else {
							cdkSerial[i] = VALID_CDK_KEYS.charAt(GuaJiRand.randInt(0, VALID_CDK_KEYS.length() - 1));
						}
					}
				}
				break;
				case UNICODE16NUM:
				{
					for (int i = 0; i < len; i++) {
						if (i == 0) {
							cdkSerial[i] = VALID_CDK16NUM_KEYS.charAt(GuaJiRand.randInt(1, VALID_CDK16NUM_KEYS.length() - 1));
						} else {
							cdkSerial[i] = VALID_CDK16NUM_KEYS.charAt(GuaJiRand.randInt(0, VALID_CDK16NUM_KEYS.length() - 1));
						}
					}
				}
				break;
			}
			
			

		} catch (Exception e) {
			MyException.catchException(e);
		}
	}

	/**
	 * 从cdk读取游戏名
	 * 
	 * @param cdk
	 * @return
	 */
	public String getGameNameFromCdk(String cdk) {
		if (cdk.length() == CdkService.CDK_TOTAL_LENGTH) {
			char[] gameName = { cdk.charAt(1), cdk.charAt(3) };
			return String.valueOf(gameName);
		}
		return "";
	}

	/**
	 * 从cdk读取游戏名
	 * 
	 * @param cdk
	 * @return
	 */
	public String getTypeNameFromCdk(String cdk) {
		if (cdk.length() == CdkService.CDK_TOTAL_LENGTH) {
			char[] typeName = { cdk.charAt(5), cdk.charAt(7) };
			return String.valueOf(typeName);
		}
		return "";
	}

	/**
	 * 检测CDK有效性
	 * 
	 * @param game
	 * @param type
	 * @param cdk
	 * @return
	 */
	public boolean checkCdkValid(String game, String type, String cdk) {
		String gameName = getGameNameFromCdk(cdk);
		String typeName = getTypeNameFromCdk(cdk);
		System.out.println(String.format("checkCdkValid...,gameName=%s,typeName=%s,cdk=%s", gameName, typeName, cdk));
		if (gameName.equals(game) && typeName.equals(type)) {
			int charCount = 0;
			int digitCount = 0;
			for (int i = CdkService.CDK_NAMT_TYPE_LEN; i < CdkService.CDK_TOTAL_LENGTH; i++) {
				char ch = cdk.charAt(i);
				if (ch >= '0' && ch <= '9') {
					digitCount++;
				} else if (ch >= 'a' && ch <= 'z') {
					charCount++;
				}
			}
			return digitCount >= CdkService.CDK_DIGIT_MIN_COUNT && charCount >= CdkService.CDK_CHAR_MIN_COUNT;
		}
		return false;
	}

	/**
	 * 添加奖励类型
	 * 
	 * @param type
	 * @param rewards
	 * @return
	 */
	// public boolean addCdkTypeReward(CdkTypeReward typeReward) {
	// if (!typeRewardMap.containsKey(typeReward.getCdkGameType())) {
	// try {
	// typeRewardMap.put(typeReward.getCdkGameType(), typeReward);
	// String typeRewardInfos = CdkUtil.typeRewardsToString(typeRewardMap);
	// if (memCachedClient.setString(MC_CDK_TYPES_KEY, typeRewardInfos)) {
	// logMsg("Add TypeReward: " + typeReward.toString() + ", Time: " +
	// GuaJiTime.getTimeString());
	// return true;
	// }
	// } catch (Exception e) {
	// MyException.catchException(e);
	// }
	// }
	// return false;
	// }

	/**
	 * 加载所有的类型奖励
	 */
	// private void loadTypeRewards() {
	// try {
	// String types = memCachedClient.getString(MC_CDK_TYPES_KEY);
	// if (types != null) {
	// typeRewardMap = CdkUtil.stringToTypeRewards(types);
	// for (Map.Entry<String, CdkTypeReward> entry : typeRewardMap.entrySet()) {
	// Log.logPrintln("Load Cdk Type: " + entry.getValue().toString());
	// }
	// } else {
	// boolean addRet = memCachedClient.setString(MC_CDK_TYPES_KEY, "{}");
	// if (!addRet) {
	// Log.logPrintln("Add MC_CDK_TYPES_KEY:{} Error");
	// }
	// typeRewardMap = new HashMap<String, CdkTypeReward>();
	// }
	// } catch (Exception e) {
	// MyException.catchException(e);
	// }
	// }

	/**
	 * 字符串jsonmap转换为字符串
	 * 
	 * @param jsonMap
	 * @return
	 */
	// public Map<String, CdkTypeReward> getGameTypeRewards(String game) {
	// Map<String, CdkTypeReward> typeRewardsInfos = new HashMap<String,
	// CdkTypeReward>();
	// for (Map.Entry<String, CdkTypeReward> entry : typeRewardMap.entrySet()) {
	// if (entry.getValue().getGame().equals(game)) {
	// typeRewardsInfos.put(entry.getValue().getType(), entry.getValue());
	// }
	// }
	// return typeRewardsInfos;
	// }

	/**
	 * 删除一个批次的CDK
	 * 
	 * @param param
	 * @param delCdks
	 * @return
	 */
	public boolean delCdk(DelCdkParam param) {
		try {
			List<byte[]> cdkList = redisClient.getList(param.getKey());
			if (cdkList != null && cdkList.size() > 0) {
				for (byte[] bs : cdkList) {
					String cdk = (String) CdkUtil.ByteToObject(bs);// 取出cdk
					String key = String.format(REDIS_CDK_DATA_FMT, cdk);
					if (redisClient.delete(key)) {
						logMsg("Delete Cdk: " + cdk + ", Time: " + GuaJiTime.getTimeString());
					}
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return true;
	}

	/**
	 * 删除一个CDK类型
	 * 
	 * @param type
	 * @return
	 */
	// public boolean delCdkType(DelTypeParam param) {
	// if (param.getType() != null && param.getType().length() > 0) {
	// String gameType = param.getGame() + "." + param.getType();
	// if (!typeRewardMap.containsKey(gameType)) {
	// return true;
	// }
	//
	// CdkTypeReward typeReward = typeRewardMap.remove(gameType);
	// try {
	// String typeRewardInfos = CdkUtil.typeRewardsToString(typeRewardMap);
	// if (memCachedClient.setString(MC_CDK_TYPES_KEY, typeRewardInfos)) {
	// logMsg("Delete TypeReward: " + typeReward.toString() + ", Time: " +
	// GuaJiTime.getTimeString());
	// return true;
	// }
	// } catch (Exception e) {
	// MyException.catchException(e);
	// }
	// }
	// return false;
	// }

	/**
	 * 生成cdk序列号
	 * 
	 * @param game
	 * @param type
	 * @return
	 */
	private String genCdkSerial() throws Exception {
		char[] cdkPrefix = new char[CdkService.CDK_TOTAL_LENGTH];
		fillGameAndType(cdkPrefix, CdkService.CDK_TOTAL_LENGTH);
		// 生成数据库信息
		String cdkSerial = String.valueOf(cdkPrefix);
		return cdkSerial;
	}

	/**
	 * 生成cdk序列号
	 * 
	 * @param game
	 * @param type
	 * @return
	 */
	private String genCdkSerialByType(CdkType Type) throws Exception {

		String cdkSerial = "";
		switch (Type) {
		case UNICODE8NUM: {
			char[] cdkPrefix = new char[CdkService.CDK_TOTAL_UNICODE08_LENGTH];
			fillCdkCodeByType(cdkPrefix, CdkService.CDK_TOTAL_UNICODE08_LENGTH,Type);
			cdkSerial = String.valueOf(cdkPrefix);
		}
			break;
		case UNICODE16NUM: {
			char[] cdkPrefix = new char[CdkService.CDK_TOTAL_UNICODE16_LENGTH];
			fillCdkCodeByType(cdkPrefix, CdkService.CDK_TOTAL_UNICODE16_LENGTH,Type);
			cdkSerial = String.valueOf(cdkPrefix);

		}
			break;
		}
		return cdkSerial;
	}

	/**
	 * 生成CDK
	 * 
	 * @param param
	 * @param genCdks
	 * @return
	 */
	public int genCdk(GenCdkParam param, List<String> genCdks) {

		// 创建目录
		try {
			File cdksFolder = new File(System.getProperty("user.dir") + "/cdks/");
			if (!cdksFolder.exists()) {
				cdksFolder.mkdir();
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}

		String filePath = String.format("%s/cdks/%s-%s.txt", System.getProperty("user.dir"), param.getCount(),
				CdkUtil.getDateString());

		FileWriter fileWrite = null;
		BufferedWriter bufferedWriter = null;
		try {
			fileWrite = new FileWriter(filePath, false);
			if (fileWrite != null) {
				bufferedWriter = new BufferedWriter(fileWrite);
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}

		try {
			// 生成cdk
			int genCount = 0;
			//平台+类型+
			String id = CdkUtil.getDateString();// 生成一批cdk标识.(绑定奖励)
			while (genCount < param.getCount()) {
				// 生成数据库信息
				String cdkSerial = genCdkSerial();
				if (genCdks.contains(cdkSerial)) { // 过滤重复
					logMsg("Cdk repeat: " + cdkSerial);
					genCount--;
					continue;
				}
				CdkInfo info = new CdkInfo();
				info.setId(id);
				info.setCdk(cdkSerial);
				info.setReward(param.getReward());

				// 写入数据库
				String key = String.format(REDIS_CDK_DATA_FMT, cdkSerial);
				if (redisClient.setString(key, info.toString())) {
					genCdks.add(cdkSerial);
					genCount++;

					logMsg("Generate Cdk: " + cdkSerial + ", Time: " + GuaJiTime.getTimeString());

					if (bufferedWriter != null) {
						bufferedWriter.write(cdkSerial);
						bufferedWriter.newLine();
					}
				}
				// 保存这一批次的礼包码
				redisClient.addList(id, CdkUtil.ObjectToByte(cdkSerial));
			}
			param.setId(id);
			// 保存这次生成批次的礼包码
			redisClient.addList(REDIS_CDK_TYPE_FMT, CdkUtil.ObjectToByte(id));
		} catch (Exception e) {
			MyException.catchException(e);
		} finally {
			try {
				if (bufferedWriter != null) {
					bufferedWriter.close();
					fileWrite.close();
				}
			} catch (IOException e) {
				MyException.catchException(e);
			}
		}
		return CdkService.CDK_STATUS_OK;
	}

	/**
	 * 生成CDK
	 * 
	 * @param param
	 * @param genCdks
	 * @return
	 */
	public int genCdkByType(GenCdkParam param, List<String> genCdks, CdkType Type) {

		// 创建目录
		try {
			File cdksFolder = new File(System.getProperty("user.dir") + "/cdks/");
			if (!cdksFolder.exists()) {
				cdksFolder.mkdir();
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}

		String filePath = String.format("%s/cdks/%s_%s-%s.txt", System.getProperty("user.dir"), Type.name(),
				param.getCount(), CdkUtil.getDateString());

		FileWriter fileWrite = null;
		BufferedWriter bufferedWriter = null;
		try {
			fileWrite = new FileWriter(filePath, false);
			if (fileWrite != null) {
				bufferedWriter = new BufferedWriter(fileWrite);
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}

		try {
			// 生成cdk
			int genCount = 0;
			String id = CdkUtil.getDateString();// 生成一批cdk标识.(绑定奖励)
			while (genCount < param.getCount()) {

				// 生成数据库信息
				String cdkSerial = genCdkSerialByType(Type);
				if (genCdks.contains(cdkSerial)) { // 过滤重复
					logMsg("Cdk repeat: " + cdkSerial);
					genCount--;
					continue;
				}
				CdkInfo info = new CdkInfo();
				info.setId(id);
				info.setCdk(cdkSerial);
				info.setReward(param.getReward());
				//类型
				info.setType(Type.name());

				// 写入数据库
				String key = String.format(REDIS_CDK_DATA_FMT, cdkSerial);
				if (redisClient.setString(key, info.toString())) {
					genCdks.add(cdkSerial);
					genCount++;

					logMsg("Generate Cdk: " + cdkSerial + ", Time: " + GuaJiTime.getTimeString());

					if (bufferedWriter != null) {
						bufferedWriter.write(cdkSerial);
						bufferedWriter.newLine();
					}
				}
				// 保存这一批次的礼包码
				redisClient.addList(id, CdkUtil.ObjectToByte(cdkSerial));
			}
			param.setId(id);
			// 保存这次生成批次的礼包码
			redisClient.addList(REDIS_CDK_TYPE_FMT, CdkUtil.ObjectToByte(id));
		} catch (Exception e) {
			MyException.catchException(e);
		} finally {
			try {
				if (bufferedWriter != null) {
					bufferedWriter.close();
					fileWrite.close();
				}
			} catch (IOException e) {
				MyException.catchException(e);
			}
		}
		return CdkService.CDK_STATUS_OK;
	}

	/**
	 * 重置cdk
	 * 
	 * @param param
	 * @return
	 */
	public void resetCdk(ResetRewardParam param) {
		List<byte[]> cdkList = redisClient.getList(param.getKey());
		for (byte[] bs : cdkList) {
			String cdk = (String) CdkUtil.ByteToObject(bs);// 取出cdk
			String keyCdk = String.format(REDIS_CDK_DATA_FMT, cdk);
			String cdkStr = redisClient.getString(keyCdk);
			if (cdkStr == null) {
				continue;
			}

			CdkInfo cdkInfo = new CdkInfo();
			// 如果存在,则进行重置
			if (cdkInfo.parse(cdkStr)) {
				cdkInfo.setPuid("");
				cdkInfo.setPlayername("");
				cdkInfo.setUsetime("");
				redisClient.setString(keyCdk, cdkInfo.toString());
			}
		}
	}

	/**
	 * 查询cdk信息
	 * 
	 * @param param
	 * @return
	 */
	public CdkInfo queryCdkInfo(QueryCdkParam param) {

		try {
			String key = String.format(REDIS_CDK_DATA_FMT, param.getCdk());
			String cdk = redisClient.getString(key);
			if (cdk == null) {
				return null;
			}

			CdkInfo cdkInfo = new CdkInfo();
			if (!cdkInfo.parse(cdk)) {
				return null;
			}
			return cdkInfo;
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return null;
	}

	/**
	 * 使用cdk
	 * 
	 * @param param
	 * @return
	 */
	public int useCdk(UseCdkParam param) {

		if (param.getCdk().length() != CdkService.CDK_TOTAL_UNICODE08_LENGTH
				&& param.getCdk().length() != CdkService.CDK_TOTAL_UNICODE16_LENGTH) {
			System.out.println(String.format("useCdk.check.length.error:%s!=%s", param.getCdk().length(),
					CdkService.CDK_TOTAL_LENGTH));
			return CdkService.CDK_STATUS_NONEXIST;
		}

		try {
			String key = String.format(REDIS_CDK_DATA_FMT, param.getCdk());
			String cdk = redisClient.getString(key);
			if (cdk == null) {
				return CdkService.CDK_STATUS_NONEXIST;
			}

			CdkInfo cdkInfo = new CdkInfo();
			if (!cdkInfo.parse(cdk)) {
				logMsg("Cdk Format Error: " + cdkInfo.toString());
				return CdkService.CDK_STATUS_NONEXIST;
			}

			if (cdkInfo.isBeused()) {
				logMsg("Cdk Been Used: " + cdkInfo.toString());
				return CdkService.CDK_STATUS_USED;
			}

			if (redisClient.getString(param.getPuid() + ":" + cdkInfo.getId()) != null) {
				logMsg("Cdk Have used: " + param.getPuid());
				return CdkService.CDK_STATUS_TYPE_MULTI;
			}

			cdkInfo.setUsed(param.getPuid(), param.getPlayername(), param.getServerId());
			if (redisClient.setString(key, cdkInfo.toString())) {
				logMsg("Use Cdk: " + cdkInfo.toString() + ", Time: " + GuaJiTime.getTimeString());
			}

			// 设置玩家使用该激活码(同一个批次激活码，玩家只能使用一次)
			//redisClient.setString(param.getPuid() + ":" + cdkInfo.getId(), cdk);
			// 同一个类型的卡只能使用一次
			redisClient.setString(param.getPuid() + ":" + cdkInfo.getType(), cdk);
			param.setReward(cdkInfo.getReward());
			return CdkService.CDK_STATUS_OK;

		} catch (Exception e) {
			MyException.catchException(e);
		}
		return CdkService.CDK_STATUS_NONEXIST;
	}

	/**
	 * 查询全部激活码的数据
	 * 
	 * @return
	 */
	public List<String> queryAllCdkInfo() {
		List<String> strList = new ArrayList<>();
		List<byte[]> keyList = redisClient.getList(REDIS_CDK_TYPE_FMT);
		for (byte[] keyByte : keyList) {
			String key = (String) CdkUtil.ByteToObject(keyByte);
			strList.add(key);
		}
		return strList;
	}

	/**
	 * 查询当前key下面的所有激活码
	 * 
	 * @param key
	 * @return
	 */
	public List<CdkInfo> queryKeyCdkInfo(String key) {
		List<CdkInfo> strList = new ArrayList<>();
		List<byte[]> cdkList = redisClient.getList(key);
		for (byte[] bs : cdkList) {
			String cdk = (String) CdkUtil.ByteToObject(bs);// 取出cdk
			String keyCdk = String.format(REDIS_CDK_DATA_FMT, cdk);
			String cdkStr = redisClient.getString(keyCdk);
			if (cdkStr == null) {
				continue;
			}

			CdkInfo cdkInfo = new CdkInfo();
			if (cdkInfo.parse(cdkStr)) {
				strList.add(cdkInfo);
			}
		}
		return strList;
	}

	/**
	 * 停止
	 */
	public void stop() {
		running = false;
	}
}
