package com.guaji.game.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * AESSignUtil;
 * 
 * @author Ting LIN
 *
 */
public class AESSignUtil {
	/**
	 * AES加密
	 */
	public static String aesEncrypt(String plainText, String key, String iv) throws Exception {
        // 转换 key 和 iv 为字节数组
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] ivBytes = iv.getBytes(StandardCharsets.UTF_8);

        // 初始化 AES Cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);

        // 执行加密
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // 使用 Base64 编码返回
        return Base64.getEncoder().encodeToString(encrypted);
	}
	
	/**
	 *  SHA256 後 16 进制编码
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String getSHA256Str(String str) throws Exception {
		MessageDigest digest;
		String encdeStr = "";
		
		digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(str.getBytes(StandardCharsets.UTF_8));
        encdeStr = Hex.encodeHexString(hash);
        
		return encdeStr;
	}
}
