package com.server.paynotice.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
//import org.apache.commons.codec.binary.Base64;
//import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.server.paynotice.bean.AutoSubMessage;
import com.server.paynotice.bean.GuaJiRechargeParam;
import com.server.paynotice.bean.OrderType;
import com.server.paynotice.bean.PayNoticeBean;
import com.server.paynotice.bean.SubscribeType;
import com.server.paynotice.common.AppConst;
import com.server.paynotice.dbservice.PayNoticeInfoService;
import com.server.paynotice.pojo.AutoSubOrder;
import com.server.paynotice.pojo.PayNoticeInfo;
import com.server.paynotice.service.RequestService;
import com.server.paynotice.util.DateUtil;
import com.server.paynotice.util.KEYS;
import com.server.paynotice.util.Util;
import com.server.paynotice.xmlparser.PayNoticeUrlManager;
import com.server.paynotice.xmlparser.RechargeConfig;

import io.netty.channel.Channel;
import net.sf.json.JSONObject;

public class GooglePayHuTuoPayNotice extends RequestService {
	enum VerifyGoogleProperty {
		KIND, PURCHASETIME, PURCHASESTATE, CONSUMPTIONSTATE, DEVELOPERPAYLOAD, MSG, STATE
	}

	private static Logger logger = Logger.getLogger(GooglePayHuTuoPayNotice.class);
	private static final String SECRET = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApob3ht6thVsAwRD/cFNtYOv7lK2xgirPkM0MyhmmSrRvF0gWxq9ToaehTs0inI6/znGJWGDpHX5j2eqZRs8Qjn+WDlXrjyPxSxdUAKooOpenKrhx7evwXgeA2GvZQ2ZQRK82yK2iyUxipSzEf7cAB26VFiL5hqscPFgwvctnWkAoa2pA5cMN3irBcCVCrnE+YKrjR+zKemagzwM19UOjte8CzLkkj+F0pj35zJVCZgo8DR8PeGbSBtz+J2ydPN5aZexsaJ9DsmxK96X2aZQUCSJrs4CRi6amRSeR/jabcryPBsIhFU+pR+rICLJYPKKrgsJGDwp6CzV74MTbVGTugwIDAQAB";
	private static final String APPName = "jp.co.school.battle";

	@Override
	public boolean verify(Map<String, String> param, Channel channel) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void request(Map<String, String> param, Channel channel) {

		// TODO Auto-generated method stub
		String retStr = "{\"errno\":1,\"errmsg\":\"充值失败\"}";
		String sid = param.get(KEYS.REQ_GOOGLE_SERVER_ID);
		String uid = param.get(KEYS.REQ_GOOGLE_CUSTOMER_ID);
		String currencyCode = param.get(KEYS.REQ_GOOGLE_CURRENCY);
		String amount = param.get("amount");
		String extinfo = param.get("ext");
		String token = param.get("token");
		String googleOriginalJson = param.get(KEYS.REQ_GOOGLE_JSON).replaceAll(" ", "+");
		String googleSignature = param.get(KEYS.REQ_GOOGLE_SIGNATURE).replaceAll(" ", "+");
		// 平台参数
		String platform = param.get(KEYS.REQ_PLATFORM);
		String signedData = new String(Base64.getDecoder().decode(googleOriginalJson));
		JSONObject purchase = JSONObject.fromObject(signedData);
		String productName = "";
		String orderId = "";
		String packageName = "";
		String purchaseToken = "";
		Long cancelDate = Long.parseLong("0");
		HttpGet httpGet = null;
		HttpResponse response = null;
		HttpClient httpClient = null;
		try {

			// 签名串验证
			if (!verifyGoogleIap(SECRET, signedData, googleSignature)) {
				retStr = "{\"errno\":1,\"errmsg\":\"token验证失败\"}";
				response(retStr, channel);
				logger.error(String.format(
						"google pay notice token verify failed, platform : %s, orderno : %s, sdk token : %s, my token : %s, source str : %s",
						AppConst.PAY_CHANNEL_GOOGLE, orderId, token, googleSignature, googleOriginalJson));
				return;
			}
			// 签名串验证通过 purchase 肯定有正常数据
			productName = purchase.getString("productId");
			orderId = purchase.getString("orderId");
			packageName = purchase.getString("packageName");
			purchaseToken = purchase.getString("purchaseToken");
			// app 包名校验
			if (!packageName.equals(APPName)) {
				// 返回错误消息，游戏包名不存在！
				retStr = "{\"errno\":1,\"errmsg\":\"游戏app包名不正确\"}";
				response(retStr, channel);

				logger.error(String.format(
						"google pay packageName error, platform : %s, orderno : %s, sdk token : %s, my token : %s, source str : %s",
						AppConst.PAY_CHANNEL_GOOGLE, orderId, token, googleSignature, googleOriginalJson));

				return;
			}

			if (RechargeConfig.getRechargeConfig(platform) == null) {
				logger.error(String.format("pay item not found platform : %s file", AppConst.PAY_CHANNEL_APPLE));
				retStr = "{\"errno\":1,\"errmsg\":\"没有找对应平台配置文件\"}";
				response(retStr, channel);
				return;
			}

			if (RechargeConfig.getRechargeConfig(platform).getByName(productName) == null) {
				logger.error(String.format("pay item not found platform : %s, good : %s", AppConst.PAY_CHANNEL_APPLE,
						productName));
				retStr = "{\"errno\":1,\"errmsg\":\"没有找到商品配置项\"}";
				response(retStr, channel);
				return;
			}

			OrderType orderType = OrderType.CONSUMABLE;
			// 金额以服务器配置为准
			amount = String.valueOf(RechargeConfig.getRechargeConfig(platform).getByName(productName).getCostMoney());
			String productType = RechargeConfig.getRechargeConfig(platform).getByName(productName).getProductType();
			orderType = OrderType.valueOf(productType.toUpperCase());
			if (orderType == null)
				orderType = OrderType.CONSUMABLE;
			// 通知给具体的游戏服务器
			PayNoticeBean bean = PayNoticeUrlManager.getPayNoticBean(AppConst.SDK_HUTUO, Integer.parseInt(sid));
			if (bean == null) {
				// 返回错误消息，游戏服务端还未配置该服务器的支付
				retStr = "{\"errno\":1,\"errmsg\":\"游戏区服未配置\"}";
				response(retStr, channel);
				logger.error(String.format(
						"pay notice failed serverid unconfigured , platform : %s, orderno : %s , serverid : %s",
						AppConst.PAY_CHANNEL_GOOGLE, orderId, sid));
				return;
			}
			if (extinfo == null || extinfo.trim().equals("")) {
				// 返回错误消息，游戏服务端需要的数据不存在
				retStr = "{\"errno\":1,\"errmsg\":\"游戏服务需要的参数未透传\"}";
				response(retStr, channel);
				logger.error(String.format("pay notice failed extinfo null, platform : %s, orderno : %s",
						AppConst.PAY_CHANNEL_GOOGLE, orderId));
				return;
			}

			List<PayNoticeInfo> orders = PayNoticeInfoService.getOrderInfoByOrderNo(orderId,
					AppConst.PAY_CHANNEL_GOOGLE);

			// 订单不为空,直接通知客户端联系客服补单
			if (orders.size() != 0) {
				retStr = "{\"errno\":1,\"errmsg\":\"该订单已存在请联系客服补单\"}";
				logger.error(String.format("orders has exsit platform : %s, orderno : %s", AppConst.PAY_CHANNEL_GOOGLE,
						orderId));
				response(retStr, channel);
				return;
			}

			JSONObject extJson = JSONObject.fromObject(extinfo);
			String os = "android";
			String version = "1.1.0";

			if (extJson.containsKey("os")) {
				os = extJson.getString("os").toString();
			}
			if (extJson.containsKey("version")) {
				version = extJson.getString("version");
			}
			// 平台支付时间
			Timestamp platPayTime = new Timestamp(Long.parseLong(purchase.getString("purchaseTime")));
			Calendar exprieCalendar = Calendar.getInstance();
			if (orderType == OrderType.SUBSCRIBE) {
				// 平台支付时间
				exprieCalendar.setTime(platPayTime);
				SubscribeType subType = Util.getSubTypeByName(productName);
				if (subType == SubscribeType.MONTHCARD)
					exprieCalendar.add(Calendar.MONTH, 1);
				else if (subType == SubscribeType.WEEKCARD)
					exprieCalendar.add(Calendar.DAY_OF_YEAR, 7);
			}

			GuaJiRechargeParam rechargeParam = new GuaJiRechargeParam();
			rechargeParam.setGoodsId(productName);
			rechargeParam.setGoodsCount(1);
			rechargeParam.setPuid(uid);
			rechargeParam.setPayMoney(amount);
			rechargeParam.setTest(false);
			rechargeParam.setOrderSerial(orderId);
			rechargeParam.setPlatform(platform);
			rechargeParam.setServerId(Integer.parseInt(sid));
			rechargeParam.setExpirtTime(exprieCalendar.getTimeInMillis());

			JSONObject jObject = JSONObject.fromObject(rechargeParam);
			String noticeParam = jObject.toString();
			String baseNoticeUrl = bean.getBaseUrl();
			StringBuffer noticeUrl = new StringBuffer();
			noticeUrl.append(baseNoticeUrl);
			noticeUrl.append("?params=");
			noticeUrl.append(URLEncoder.encode(noticeParam, "UTF-8"));
			String url = noticeUrl.toString().trim();

			PayNoticeInfo info = new PayNoticeInfo();
			info.setSid(Integer.parseInt(sid));
			info.setUid(uid);
			info.setAmount(Double.parseDouble(amount));
			info.setRmbRate(1.0D);
			info.setVersion(version);
			info.setCurrency(currencyCode);
			info.setGameName("一骑学院");
			info.setProductName(productName);
			info.setOs(os);
			info.setSdkChannel(AppConst.PAY_CHANNEL_GOOGLE);
			info.setOrderNo(orderId);
			info.setPayNoticeUrl(url);
			info.setOrderType(orderType.value());

			info.setPuid("pingtai");
			// 平台支付时间
			info.setPltformPayTime(platPayTime);
			info.setPayTime(new Timestamp(System.currentTimeMillis()));
			int id = 0;
			if (orderType == OrderType.SUBSCRIBE) {
				AutoSubOrder autoSubOrder = new AutoSubOrder();
				// 自动订阅到期时间
				Date exprieDate = new Timestamp(exprieCalendar.getTimeInMillis());
				autoSubOrder.setPlatAccNum("");
				autoSubOrder.setOrderNum(orderId);
				autoSubOrder.setSdkChannel(AppConst.PAY_CHANNEL_GOOGLE);
				autoSubOrder.setValidateReceipt(purchaseToken);
				autoSubOrder.setStatus(1);
				autoSubOrder.setExpirDate(exprieDate);
				id = PayNoticeInfoService.insertOrderByTx(info, autoSubOrder);
			
			} else {
				id = PayNoticeInfoService.insert(info);

			}
			if (id == 0) {
				retStr = "{\"errno\":1,\"errmsg\":\"插入数据失败\"}";
				response(retStr, channel);
				return;
			}
			// 将数据存储到数据库中，以备补单需求
			httpClient = HttpClients.custom().build();
			httpGet = new HttpGet(noticeUrl.toString());
			RequestConfig reqConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
			httpGet.setConfig(reqConfig);
			httpGet.setHeader(HttpHeaders.CONNECTION, "close");
			response = httpClient.execute(httpGet);

			HttpEntity entity = response.getEntity();
			long contentLenth = entity.getContentLength();
			byte[] retData = new byte[(int) contentLenth];
			entity.getContent().read(retData);
			String resultStr = new String(retData, "UTF-8");
			JSONObject resultJson = JSONObject.fromObject(resultStr);
			response(resultStr, channel);
			logger.info(String.format("puid=%s send good by %s", uid, resultStr));
			int statusCode = Integer.parseInt(resultJson.getString("errno"));

			Timestamp getGoodTime = new Timestamp(System.currentTimeMillis());
			// 从游戏支付接口返回的状态 需要转换下
			statusCode = (statusCode == 0 ? 1 : 0);

			PayNoticeInfoService.update(statusCode, getGoodTime, info.getId());
		} catch (Exception e) {
			logger.error(e);
			logger.error(String.format("pay notice exception, platform : %s, orderno : %s", AppConst.PAY_CHANNEL_GOOGLE,
					orderId));
			// 返回错误消息
			e.printStackTrace();
			retStr = "{\"errno\":1,\"errmsg\":\"充值回调服务器出错\"}";
			response(retStr, channel);
		} finally {
			if (response != null) {
				EntityUtils.consumeQuietly(response.getEntity());
			}
			if (httpGet != null) {
				httpGet.releaseConnection();
			}
		}
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
	private boolean verifyGoogleIap(String base64key, String originalJson, String signature) {
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

	private String getAcessToken() {

		try {
			URL obj = new URL(AppConst.OAUTH_URL);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.addRequestProperty("grant_type", AppConst.GRANT_TYPE);
			con.addRequestProperty("client_id", AppConst.CLIENT_ID);
			con.addRequestProperty("client_secret", AppConst.CLIENT_SECRET);
			con.addRequestProperty("refresh_token", AppConst.REFRESH_TOKEN);
			con.setConnectTimeout(30000);
			con.setReadTimeout(30000);
			int responseCode = con.getResponseCode();
			if (responseCode == 200) {

				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				// Parse Google Response
				JSONObject responseJson = JSONObject.fromObject(response.toString());
				logger.info(String.format("google getAcessToken responseJson  rep=%s", responseJson.toString()));
				if (responseJson.containsKey("access_token"))
					return responseJson.getString("access_token");
			}

		} catch (Exception ex) {
			logger.error(String.format("google getAcessToken Acesse Error  Ex:%s", ex.toString()));
		}
		return "";
	}

	/**
	 * @param packageName   包名
	 * @param productId     商品id
	 * @param purchaseToken 内购订单中purchaseToken
	 * @return
	 */

	private EnumMap<VerifyGoogleProperty, Object> validateGoogleIap(String packageName, String productId,
			String purchaseToken, String accessToken) {
		EnumMap<VerifyGoogleProperty, Object> retMap = new EnumMap<VerifyGoogleProperty, Object>(
				VerifyGoogleProperty.class);
		String url = String.format("%s/%s/purchases/products/%s/tokens/%s?access_token=%s", AppConst.CHK_ORDERURL,
				packageName, productId, purchaseToken, accessToken);
		logger.debug(String.format("validateGoogleIap url=%s", url));
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setConnectTimeout(30000);
			con.setReadTimeout(30000);
			int responseCode = con.getResponseCode();
			if (responseCode == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				// 都
				JSONObject responseJson = JSONObject.fromObject(response.toString());
				if (responseJson == null) {
					retMap.put(VerifyGoogleProperty.MSG, "json parse error ");

					return null;
				}
				logger.info(String.format("aws responseJson  rep=%s", responseJson.toString()));
				if (responseJson != null) {
					retMap.put(VerifyGoogleProperty.STATE, responseCode);
					retMap.put(VerifyGoogleProperty.KIND, responseJson.getString("androidpublisher#productPurchase"));
					retMap.put(VerifyGoogleProperty.PURCHASETIME, responseJson.getString("purchaseTimeMillis"));
					retMap.put(VerifyGoogleProperty.PURCHASESTATE, responseJson.getString("purchaseState"));
					retMap.put(VerifyGoogleProperty.CONSUMPTIONSTATE, responseJson.getString("consumptionState"));
					retMap.put(VerifyGoogleProperty.CONSUMPTIONSTATE, responseJson.getString("developerPayload"));
				}

			}
		} catch (Exception e) {
			logger.error(String.format("pay notice exception, platform : %s, orderno : %s", AppConst.PAY_CHANNEL_GOOGLE,
					purchaseToken));
			// 返回错误消息
			e.printStackTrace();
		}
		return null;
	}

}
