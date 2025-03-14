package com.server.paynotice;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.server.paynotice.util.Md5Util;

public class App1 {
	public static void main(String[] args){
		
		
		
		String puid="33cdf5ba81f1";
		String[] arr=puid.split("_");
	    System.out.println("ddddddddddddddddddddddd"+arr[0]);
		String product="jp.co.school.battle.monthauto";
		System.out.println("dddddddddddddddd"+product.substring(product.indexOf("month")));
		Object stats=200;
	
		
		Calendar exprieCalendar = Calendar.getInstance();
		
		
		if(stats.equals(200))
		{
			
			System.out.println("xiangdeng");
		}
		
		long time = 1528660800000L;
		Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(format.format(date));
		System.out.println(date);
		String SECRET = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApob3ht6thVsAwRD/cFNtYOv7lK2xgirPkM0MyhmmSrRvF0gWxq9ToaehTs0inI6/znGJWGDpHX5j2eqZRs8Qjn+WDlXrjyPxSxdUAKooOpenKrhx7evwXgeA2GvZQ2ZQRK82yK2iyUxipSzEf7cAB26VFiL5hqscPFgwvctnWkAoa2pA5cMN3irBcCVCrnE+YKrjR+zKemagzwM19UOjte8CzLkkj+F0pj35zJVCZgo8DR8PeGbSBtz+J2ydPN5aZexsaJ9DsmxK96X2aZQUCSJrs4CRi6amRSeR/jabcryPBsIhFU+pR+rICLJYPKKrgsJGDwp6CzV74MTbVGTugwIDAQAB";
		
		String strOrderInfo="{\"orderId\":\"GPA.3305-6013-4800-09534\",\"packageName\":\"jp.co.school.battle\",\"productId\":\"jp.co.school.battle.60\",\"purchaseTime\":1547278269906,\"purchaseState\":0,\"developerPayload\":\"88a9b6cc34677d857e19778b76fc2094-6元正常充值档位-jp.co.school.battle.60-sg_C21F83A3843F\",\"purchaseToken\":\"ljhfhjkegbpcafaiblcacmnk.AO-J1OxZW15H_eRsDMo5mrzyiSDkhrMHxwFiZ_iY3Sr7IlqdXf4_qfMI-ILJIIkAjQ_mEizbCy9cWb5bv43EVckcInJ0mJcNTOFe5iR-NvXpV30JBPqdcfGj8P63rxPM9I54QKY7YOV3\"}";
		String signaOrderinfo="dDtnasuZWbWSvMGFHKotzg+b1GpQwy0WoOII8i4PG/g689vwfGcqJ4m8uIltVV1QM5rez6FMLqApCUBJeCgKHBP59tfT2o+3L6eND411K6xPVZXVEGJAQqTM3A6XNoKYrYUlpsgi1zuNNYOY/uzK91hQi8E9e7zsAWM99k2Xsc0322SEM2yKblpIgf1U3lbUdhm4rIqJGmTpx5YwOVE8wIrQrPJTAm2SCby8S/6QL6ZNhAPPImMCQP3rRMQpgTBZUJqgINDVp61S73g6YSBTSIs3BOHg+i8oIZZfRA0RsfUVVnJWcc/ycTMNVPelMa3tEjAqCwK/x9ChVk1DnYvn/A==";
		boolean isOk=verifyGoogleIap(SECRET,strOrderInfo,signaOrderinfo);
		/*
		String source = "11133677071806192053501002007407DJ500.0{\"productName\":\"6\",\"playerId\":\"123\"}1529412830a8374da41b7ffc722877119b96c4999a";
		System.out.println(source);
		System.out.println("sdkMd5--"+"6b3be1a9b9fadb945592b35a8028fe85");
		String md51 = Md5Util.getMd5Str(source);
		String md52 = Md5Util.MD5(source);
		System.out.println("md51----"+md51);
		System.out.println("md51----"+md52);
		String formatStr = String.format("test %s", "hahah");
		System.out.println(formatStr);
		*/
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			Date d = sdf.parse("2017-03-31 03:44:40");
			Calendar cld = Calendar.getInstance();
			cld.setTime(d);
			cld.add(Calendar.MONTH, 1);
			Date d2 = cld.getTime();
			System.out.println(sdf.format(d)+"加一月："+sdf.format(d2));
			
			//闰年的情况
			d = sdf.parse("2016-01-31 03:44:40");
			cld = Calendar.getInstance();
			cld.setTime(d);
			cld.add(Calendar.MONTH, 1);
			d2 = cld.getTime();
			System.out.println(sdf.format(d)+"加一月："+sdf.format(d2));
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		/*
		String googleOriginalJson="eyJvcmRlcklkIjoiR1BBLjMzNzctMzE5Ny0zMTY0LTY3OTcyIiwicGFja2FnZU5hbWUiOiJqcC5jby5zY2hvb2wuYmF0dGxlIiwicHJvZHVjdElkIjoianAuY28uc2Nob29sLmJhdHRsZS4zMDAiLCJwdXJjaGFzZVRpbWUiOjE1NDcyODg0MzE0NTAsInB1cmNoYXNlU3RhdGUiOjAsImRldmVsb3BlclBheWxvYWQiOiIxMTczNGNmZGQ5Nzg1MDc3YjJmMjI5YjU0MDMzOTdmZS0zMOWFg+ato+W4uOWFheWAvOaho+S9jS1qcC5jby5zY2hvb2wuYmF0dGxlLjMwMC1zZ19DMjFGODNBMzg0M0YiLCJwdXJjaGFzZVRva2VuIjoibWlkYWxtam1jb2Vra2VocG9paWZqYW1hLkFPLUoxT3hfRlVMTzkwcjdRblNzVkpiUEpkVU54dHpGaGFpLTlDaGVyN1RCMVZrUEpiX1NqYl9ZX3hfcWFrWWp4U3J0ODczbjdSUktmQS1vdHdwV3NWYWR5XzhubEJobjlmb1FSSVhlN3FwRmdHNm1rdHdKV1hnVEFyVHpXM0xUdE1rU19hTTE4MGd6In0=";
		
		String signedData=new String(Base64.getDecoder().decode(googleOriginalJson));
		
		String signaOrderinfo2="FQshYecE12KLjwla17TtDq1Cd2g7AJzOB9ZCfj1enYcVjYw49eNkul+I29zg2YclFBHq1qeJxOqNCotW4u6lJBB2jHGDbZZNC7wNi72YSKcGCU+xL34D981+VbuI7K1gov5TATcHZFPKx3IUG4OLtYSEoeESheARdN0mXbK9sFiaK7uTKPnD36jkhw390rQoWJUE4zLv9HFbr5O9RUcdCe8VSUg9TX9FG/uYwVgIYj3D/TgedwJR4eO8+ZT6LcX4/eCufXt45mGL0iiKdbhh+qrqWqEkvIulIOaeUJJbyomfpoHDuqNvytCFWxn8yXeUxumWULMNCb5I0MEDn27x7Q==";
		
		boolean isok2=verifyGoogleIap(SECRET,signedData,signaOrderinfo2);
		
		
		parserParam("key=eyJvcmRlcklkIjoiR1BBLjMzNzctMzE5Ny0zMTY0LTY3OTcyIiwicGFja2FnZU5hbWUiOiJqcC5jby5zY2hvb2wuYmF0dGxlIiwicHJvZHVjdElkIjoianAuY28uc2Nob29sLmJhdHRsZS4zMDAiLCJwdXJjaGFzZVRpbWUiOjE1NDcyODg0MzE0NTAsInB1cmNoYXNlU3RhdGUiOjAsImRldmVsb3BlclBheWxvYWQiOiIxMTczNGNmZGQ5Nzg1MDc3YjJmMjI5YjU0MDMzOTdmZS0zMOWFg+ato+W4uOWFheWAvOaho+S9jS1qcC5jby5zY2hvb2wuYmF0dGxlLjMwMC1zZ19DMjFGODNBMzg0M0YiLCJwdXJjaGFzZVRva2VuIjoibWlkYWxtam1jb2Vra2VocG9paWZqYW1hLkFPLUoxT3hfRlVMTzkwcjdRblNzVkpiUEpkVU54dHpGaGFpLTlDaGVyN1RCMVZrUEpiX1NqYl9ZX3hfcWFrWWp4U3J0ODczbjdSUktmQS1vdHdwV3NWYWR5XzhubEJobjlmb1FSSVhlN3FwRmdHNm1rdHdKV1hnVEFyVHpXM0xUdE1rU19hTTE4MGd6In0=");
	
	*/
	}
	
	

	/**
	 * 根据游戏的public key验证支付时从Google Market返回的signedData与signature的值是否对应
	 *
	 * @param base64key    ：配置在Google Play开发者平台上的公钥
	 * @param originalJson ：支付成功时响应的物品信息
	 * @param signature    ：已加密后的签名
	 * @return boolean：true 验证成功<br/>
	 *         false 验证失败
	 */
	private static boolean verifyGoogleIap(String base64key, String originalJson, String signature) {
		try {
			// 解密出验证key
			byte[] decodedKey = Base64.getDecoder().decode(base64key);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
		
			// 验证票据
			Signature sig = Signature.getInstance("SHA1withRSA");
			sig.initVerify(publicKey);
			sig.update(originalJson.getBytes());
			return sig.verify(Base64.getDecoder().decode(signature));
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		} catch (InvalidKeyException ex) {
			ex.printStackTrace();
		} catch (SignatureException ex) {
			ex.printStackTrace();
		} catch (InvalidKeySpecException ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	
	private static Map<String, String> parserParam(String param) {
		Map<String, String> paramMap = new HashMap<String, String>();
		String[] params = param.split("&");
		if (params.length != 0) {
			for (String p : params) {
				String key;
				String value;
				
				String[] parry = p.split("=");
				
				if (parry.length == 0) {
					continue;
				}
				/*
				if (parry.length == 1) {
					key = parry[0];
					value = "";
				} else if (parry.length == 2) {
					key = parry[0];
					value = parry[1];
				} else {
					int firstIndex=p.indexOf("=");
					key = p.substring(0,firstIndex);
					value = p.substring(firstIndex);
				}
				*/
				if(parry.length == 1)
				{
					key = parry[0];
					value = "";
				}else
				{
					int firstIndex=p.indexOf("=");
					key = p.substring(0,firstIndex);
					value = p.substring(firstIndex+1);
				}
			
				
				paramMap.put(key, value);
			}
		}
		return paramMap;
	}
}
