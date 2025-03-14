package com.guaji.merge.util;

public class StringUtil {

	public static int[] commasStringToIntArray(String commasString,
			String separator) {
		if (isNull(commasString)) {
			return new int[0];
		}
		return toPrimitiveIntArray(commasString.split(separator));
	}

	/**
	 * 将字符串数组转换成基本int数据
	 * 
	 * @param strArray
	 * @return
	 */
	public static int[] toPrimitiveIntArray(String[] strArray) {
		int[] intValues = new int[strArray.length];
		for (int i = 0; i < strArray.length; i++) {
			if (isNull(strArray[i])) {
				intValues[i] = 0;
			} else {
				intValues[i] = Integer.valueOf(strArray[i].trim());
			}
		}
		return intValues;
	}

	public static boolean isNull(String value) {
		if (value == null || value.equals("")) {
			return true;
		}
		return false;
	}

}
