package org.guaji.cryption;

import java.security.MessageDigest;

import org.guaji.os.MyException;
import org.guaji.os.OSOperator;

/**
 * SHA加密封装
 */
public class SHACrypt {
	/**
	 * 加密字节数组
	 * 
	 * @param bytes
	 * @param offset
	 * @param size
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] bytes, int offset, int size) throws Exception {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA");
		messageDigest.update(bytes, offset, size);
		return messageDigest.digest();
	}

	/**
	 * 加密字节数组, 返回字符串
	 * 
	 * @param bytes
	 * @return
	 */
	public static String encrypt(byte[] bytes) {
		try {
			byte[] shaBytes = encrypt(bytes, 0, bytes.length);
			return OSOperator.bytesToHexString(shaBytes);
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return null;
	}
}
