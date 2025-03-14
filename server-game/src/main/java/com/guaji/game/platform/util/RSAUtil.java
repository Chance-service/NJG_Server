package com.guaji.game.platform.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAUtil {

	private static String pub_key_1 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDIYMeruGjt8VREeGaC2rGz5lLD"
			+ "\r"
			+ "RrViyx7YYKvRF+036b6CnyFuVyQvl0Q+rZPtA4LidMhbHKQz9C5EhtXdhrKwgqt0"
			+ "\r"
			+ "loWYnzYef7i6tvVoCowI1tHBuiTJruekDyCQwAwqAttEKt/FNGpKhMBjCAj9xIWL"
			+ "\r" + "eaC0xySsOY6JzDtokQIDAQAB" + "\r";
	private static final String RSA_KEY_ALGORITHM = "RSA";

	public static String encryptByPubKey(String data, String pPubkey) {
		try {
			byte[] pub_key = Base64ForRsa.decode(pPubkey);
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pub_key);
			KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
			PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);

			InputStream ins = new ByteArrayInputStream(data.getBytes());
			ByteArrayOutputStream writer = new ByteArrayOutputStream();
			byte[] buf = new byte[100];
			int bufl;

			while ((bufl = ins.read(buf)) != -1) {
				byte[] block = null;

				if (buf.length == bufl) {
					block = buf;
				} else {
					block = new byte[bufl];
					for (int i = 0; i < bufl; i++) {
						block[i] = buf[i];
					}
				}
				writer.write(cipher.doFinal(block));
			}

			return Base64ForRsa.encode(writer.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String encryptByPriKey(String data, String pPrikey) {
		try {
			byte[] pri_key = Base64ForRsa.decode(pPrikey);

			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(pri_key);
			KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
			PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);

			InputStream ins = new ByteArrayInputStream(data.getBytes());
			ByteArrayOutputStream writer = new ByteArrayOutputStream();
			byte[] buf = new byte[100];
			int bufl;

			while ((bufl = ins.read(buf)) != -1) {
				byte[] block = null;

				if (buf.length == bufl) {
					block = buf;
				} else {
					block = new byte[bufl];
					for (int i = 0; i < bufl; i++) {
						block[i] = buf[i];
					}
				}

				writer.write(cipher.doFinal(block));
			}

			return Base64ForRsa.encode(writer.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String decryptByPubKey(String data, String pPubkey) {
		try {
			byte[] pub_key = Base64ForRsa.decode(pPubkey);
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pub_key);

			KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
			PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, publicKey);

			byte[] _data = Base64ForRsa.decode(data);

			InputStream ins = new ByteArrayInputStream(_data);
			ByteArrayOutputStream writer = new ByteArrayOutputStream();
			byte[] buf = new byte[128];
			int bufl;

			while ((bufl = ins.read(buf)) != -1) {
				byte[] block = null;

				if (buf.length == bufl) {
					block = buf;
				} else {
					block = new byte[bufl];
					for (int i = 0; i < bufl; i++) {
						block[i] = buf[i];
					}
				}

				writer.write(cipher.doFinal(block));
			}

			return new String(writer.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String decryptByPriKey(String data, String pPrikey) {
		try {
			byte[] pri_key = Base64ForRsa.decode(pPrikey);
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(pri_key);
			KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
			PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);

			byte[] _data = Base64ForRsa.decode(data);

			InputStream ins = new ByteArrayInputStream(_data);
			ByteArrayOutputStream writer = new ByteArrayOutputStream();
			byte[] buf = new byte[128];
			int bufl;

			while ((bufl = ins.read(buf)) != -1) {
				byte[] block = null;

				if (buf.length == bufl) {
					block = buf;
				} else {
					block = new byte[bufl];
					for (int i = 0; i < bufl; i++) {
						block[i] = buf[i];
					}
				}

				writer.write(cipher.doFinal(block));
			}

			return new String(writer.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws Exception {

//		String origin = "{\"error\":\"1020\",\"errorMessage\":\"名字长度请保持在1-10位\",\"timestamp\":1378366476639,\"data\":null}";
//		System.out.println("加密前：" + Arrays.toString(origin.getBytes()));
//		String en = encryptByPriKey(origin, pri_key_1);
		// String en = encryptByPubKey(origin, pub_key_1);
//		System.out.println("加密后：" + en);
		//String result = decryptByPubKey("Uq64xpGQ0hwtrOHfZr+A/gXukUsLvdyl6abUNOPDx75emj1rvS7nQeKgN2RrrhoDdWjCHOMATydA8yONrUKIzUXmW5n8f9vAc2gWyIdNgFHb2BRvfwYQUxJWuLFUfnWp7w6D6tzQGYvqLWsS35LiKbRNr116tUpI7MJFaNtlSbuJDOZNTNki1dZ6re11Fxn09BejXGFQRnGI3aFZbv/xpAw3DVv7nfPAMF31qVsoIEbon8P5wLOJ0hSTMRVMfeyiuO9gZ+xQ9ALnCOQDe8EAL/Xv187O1zeaJb/Jx04v4sgJ4Gty3NUOfA+Y7Z3rYkHZLkBD8JJ++ldd7WjiGLYz1mhgHSNl+gD37E2VInC+DqwTnCrVTiGeWq/GRk8WLjNZMrCV/hkd9zu3cUIq5glUkmyEedY7zrUDpWqeGU+0jQzpzVTrDWLVMtIhOio403CP0hGALdAwvCJpwOchwgq8aMzp2UnF82A1vMhy4PBaJl4cqPAutt/ZXV7a3g53EOKrjCr/61UFrJqZjZTdE+/JnleAFAxM1VvZVBb71IShd+OhnsbwwlBHuDIrX8UAJh2RoY4t/BYOBV/HEBt/xy+ehV2xsvpqoOv5Ufra18yw1bxtschUJjq3bY9TFzaRhcui0N6C016tgyhcKNaDBXZn73HoIRUnSRQAwu9522xnSf0=", pub_key_1);
		String result = decryptByPubKey("FNHDpdJFHI74jUMHXGCY1hFhU2cNfe8XuQn2xP/qCkoOTgjuhvncKpSb+IjpxuPQO/YyyMXkAS/l4+CI1MKJC7jeh0KZABe9g+0eDrflOex478ARHgyMwuolMDYuUNhV1H3rmeK7pW3KAPEaywHEqJWu5stt/NhknqrkiimvCaY="
				, pub_key_1);
		System.out.println("解密后：" + result);

		// String sentStr =
		// "FkrrbUT8nZ8hrSLgTGAw5uOdCXInKkUx5OysM08To/ZXskVHiPv4d7dnnBzv2K036yXkK6AeuP+uSh5Ah9G8147TRQ1j9NyYBZjPiaFBEJlE+8r1UEWan/G+hkDHg+y625zE1Mecl/Ld49SJ/qENj7QZYFj7fqL3R10uZqrxX3I=";
		// System.out.println(decryptByPubKey(sentStr, pub_key_1));
	}

}