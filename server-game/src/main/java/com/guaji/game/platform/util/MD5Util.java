package com.guaji.game.platform.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class MD5Util {
    
    public static String toMD5(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(plain.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toMD5(String plain, String secretkey) {
    	return toMD5( plain + secretkey);
    }
    
    
    public static String fileChecksum(FileInputStream fis) {
        
        MessageDigest md;
        try {
            byte[] dataBytes = new byte[1024];
            md = MessageDigest.getInstance("MD5");
            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            };
            byte[] mdbytes = md.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
        
    }
	/**
	 * 32位md5
	 * @param sourceStr
	 * @return
	 */
	public static String MD5(String sourceStr) {
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(sourceStr.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString();
			System.out.println("MD5(" + sourceStr + ",32) = " + result);
			System.out.println("MD5(" + sourceStr + ",16) = " + buf.toString().substring(8, 24));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * md5 字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String getMd5Str(String str) {
		byte[] md5Bytes = getMd5Bytes(str);
		return md5ToHexString(md5Bytes);
	}

	/**
	 * 获得MD5加密字节数组
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] getMd5Bytes(String str) {
		byte[] result = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] input = str.getBytes("UTF-8");
			result = md5.digest(input);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 字节数组转换为16进制字符串
	 * 
	 * @param md5
	 * @return
	 */
	public static String md5ToHexString(byte[] md5) {
		StringBuffer buffer = new StringBuffer();
		int length = md5.length;
		for (int i = 0; i < length; i++) {
			buffer.append(byteHEX(md5[i]));
		}
		return buffer.toString().toLowerCase();
	}

	/**
	 * 字节转16进制字符串
	 * 
	 * @param ib
	 * @return
	 */
	public static String byteHEX(byte ib) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] ob = new char[2];
		ob[0] = Digit[(ib >>> 4) & 0X0F];
		ob[1] = Digit[ib & 0X0F];
		String s = new String(ob);
		return s;
	}
}
