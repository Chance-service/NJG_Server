package com.guaji.cdk.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * cdk工具类
 * 
 */
public class CdkUtil {
	// /**
	// * json对象转换为map
	// *
	// * @param jsonString
	// * @return
	// */
	// public static Map<String, CdkTypeReward> stringToTypeRewards(String
	// typeRewards) {
	// Map<String, CdkTypeReward> typeRewardMap = new HashMap<String,
	// CdkTypeReward>();
	//
	// JSONObject jsonObject = JSONObject.fromObject(typeRewards);
	// Iterator keyIt = jsonObject.keys();
	// while (keyIt.hasNext()) {
	// String key = "";
	// String value = "";
	// try {
	// key = (String) keyIt.next();
	// value = (String) jsonObject.get(key);
	//
	// CdkTypeReward typeReward = new CdkTypeReward();
	// if (typeReward.parse(value)) {
	// typeRewardMap.put(key, typeReward);
	// }
	// } catch (Exception e) {
	// Log.logPrintln(String.format("TypeRewards: %s, Key: %s, Value: %s",
	// typeRewards, key, value));
	// }
	// }
	// return typeRewardMap;
	// }
	//
	// /**
	// * 字符串jsonmap转换为字符串
	// *
	// * @param jsonMap
	// * @return
	// */
	// public static String typeRewardsToString(Map<String, CdkTypeReward>
	// typeRewardMap) {
	// Map<String, String> typeInfos = new HashMap<String, String>();
	// for (Map.Entry<String, CdkTypeReward> entry : typeRewardMap.entrySet()) {
	// typeInfos.put(entry.getKey(), entry.getValue().toString());
	// }
	// JSONObject jsonObject = JSONObject.fromObject(typeInfos);
	// return jsonObject.toString();
	// }

	/**
	 * 获取日期字符串
	 * 
	 * @return
	 */
	public static String getDateString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return dateFormat.format(Calendar.getInstance().getTime());
	}

	/**
	 * 对象转换为字节数组
	 * 
	 * @param obj
	 * @return
	 */
	public static byte[] ObjectToByte(Object obj) {
		byte[] bytes = null;
		try {
			// object to bytearray
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);

			bytes = bo.toByteArray();

			bo.close();
			oo.close();
		} catch (Exception e) {
			System.out.println("translation" + e.getMessage());
			e.printStackTrace();
		}
		return bytes;
	}

	/**
	 * 字节数组转化为对象
	 * 
	 * @param bytes
	 * @return
	 */
	public static Object ByteToObject(byte[] bytes) {
		Object obj = null;
		try {
			// bytearray to object
			ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
			ObjectInputStream oi = new ObjectInputStream(bi);

			obj = oi.readObject();
			bi.close();
			oi.close();
		} catch (Exception e) {
			System.out.println("translation" + e.getMessage());
			e.printStackTrace();
		}
		return obj;
	}
}
