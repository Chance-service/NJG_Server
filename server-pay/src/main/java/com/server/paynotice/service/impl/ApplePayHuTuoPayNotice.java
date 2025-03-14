package com.server.paynotice.service.impl;

import java.net.URLEncoder;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ApplePayHuTuoPayNotice extends RequestService {
	private static Logger logger = Logger.getLogger(ApplePayHuTuoPayNotice.class);
	private static final String SECRET = "09ab139094d746fab29b624aba5085cb";
	private static final String APPName = "jp.co.school.battle";

	@Override
	public void response(Object content, Channel channel) {
		// TODO Auto-generated method stub
		super.response(content, channel);
	}

	@Override
	public void response(String content, Channel channel) {
		// TODO Auto-generated method stub
		super.response(content, channel);
	}

	@Override
	public boolean verify(Map<String, String> param, Channel channel) {
		// TODO Auto-generated method stub
		return super.verify(param, channel);
	}

	@Override
	public void request(Map<String, String> param, Channel channel) {

		// TODO Auto-generated method stub
		String retStr = "{\"errno\":1,\"errmsg\":\"充值失败\"}";
		String channelorder = param.get(KEYS.REQ_APPLE_CHANNEL_ORDER);
		String sid = param.get(KEYS.REQ_APPLE_SERVER_ID);
		String uid = param.get(KEYS.REQ_APPLE_CUSTOMER_ID);
		String currencyCode = param.get(KEYS.REQ_APPLE_CURRENCY);
		String receipt = param.get(KEYS.REQ_APPLE_RECEIPT).replaceAll(" ", "+");
		String sku = param.get(KEYS.REQ_APPLE_SKU);
		String price = param.get(KEYS.REQ_APPLE_SKU_PRICE);
		// 平台参数
		String platform = param.get(KEYS.REQ_PLATFORM);
		String productName = sku;
		String orderId = channelorder;
		String amount = param.get("amount");
		String extinfo = param.get("ext");
		Long pPayTime = Long.parseLong("0");
		HttpClient httpClient = null;
		HttpGet httpGet = null;
		HttpResponse response = null;

		try {

			if (extinfo == null || extinfo.trim().equals("")) {
				// 返回错误消息，游戏服务端需要的数据不存在
				retStr = "{\"errno\":1,\"errmsg\":\"游戏服务需要的参数未透传\"}";
				response(retStr, channel);
				logger.error(String.format("pay notice failed extinfo null, platform : %s, orderno : %s",
						AppConst.PAY_CHANNEL_APPLE, orderId));
				return;
			}

			// 苹果平台验证串为空
			if (receipt == null) {
				retStr = "{\"errno\":1,\"errmsg\":\"请求苹果验证出错\"}";
				response(retStr, channel);
				logger.error(String.format("receipt param  error : %s, receipt : %s", AppConst.PAY_CHANNEL_APPLE, sku));
				return;
			}

			if (RechargeConfig.getRechargeConfig(platform) == null) {
				logger.error(String.format("pay item not found platform : %s file", AppConst.PAY_CHANNEL_APPLE));
				retStr = "{\"errno\":1,\"errmsg\":\"没有找对应平台充值配置文件\"}";
				response(retStr, channel);
				return;
			}

			JSONObject retJson = validateIap(receipt);

			if (retJson.getInt("status") != 21007) {
				// ios7及以上版本生成环境验证
				// etc. 其他老环境不再做验证

				if (retJson.containsKey("receipt")) {

					JSONObject receiptJson = retJson.getJSONObject("receipt");

					if (receiptJson.containsKey("bundle_id") && !receiptJson.getString("bundle_id").equals(APPName)) {
						retStr = "{\"errno\":1,\"errmsg\":\"包名参数不匹配\"}";
						logger.error(String.format("receipt param  error : %s, receipt : %s",
								AppConst.PAY_CHANNEL_APPLE, productName));
						response(retStr, channel);
						return;
					}

					if (receiptJson.containsKey("in_app")) {

						JSONArray inApp = receiptJson.getJSONArray("in_app");

						if (productName.equals("(null)")) {
							for (int index = 0; index < inApp.size(); index++) {
								Long curOrderPurchase = Long
										.parseLong(inApp.getJSONObject(index).getString("purchase_date_ms"));
								if (curOrderPurchase > pPayTime) {
									orderId = inApp.getJSONObject(index).getString("transaction_id");
									pPayTime = Long.parseLong(inApp.getJSONObject(index).getString("purchase_date_ms"));
									productName = inApp.getJSONObject(index).getString("product_id");
								}
							}
							if (pPayTime == 0) {
								logger.error(String.format("验证失败, retJson=%s", retJson));
								response(retStr, channel);
								return;
							}
						} else {
							//boolean isfound = false;
							
							for (int index = 0; index < inApp.size(); index++) {
								if (productName.equals(inApp.getJSONObject(index).getString("product_id"))) {
									Long curOrderPurchase = Long
											.parseLong(inApp.getJSONObject(index).getString("purchase_date_ms"));
									if (curOrderPurchase > pPayTime) {
										orderId = inApp.getJSONObject(index).getString("transaction_id");
										pPayTime = Long.parseLong(
												inApp.getJSONObject(index).getString("purchase_date_ms"));
										productName = inApp.getJSONObject(index).getString("product_id");
									}
								}
							}

							if (pPayTime == 0) {
								logger.error(String.format("验证失败, retJson=%s", retJson));
								response(retStr, channel);
								return;
							}

						}

					} else {
						logger.error(String.format("验证失败, retJson=%s", retJson));
						response(retStr, channel);
						return;
					}
				} else {
					logger.error(String.format("验证失败, retJson=%s", retJson));
					response(retStr, channel);
					return;
				}
			}

			OrderType orderType = OrderType.CONSUMABLE;
			amount = String.valueOf(RechargeConfig.getRechargeConfig(platform).getByName(productName).getCostMoney());
			String productType = RechargeConfig.getRechargeConfig(platform).getByName(productName).getProductType();
			orderType = OrderType.valueOf(productType.toUpperCase());

			if (RechargeConfig.getRechargeConfig(platform).getByName(productName) == null) {
				logger.error(String.format("pay item not found platform : %s, good : %s", AppConst.PAY_CHANNEL_APPLE,
						productName));
				retStr = "{\"errno\":1,\"errmsg\":\"没有找到商品配置项\"}";
				response(retStr, channel);
				return;
			}

			// 通知给具体的游戏服务器
			PayNoticeBean bean = PayNoticeUrlManager.getPayNoticBean(AppConst.SDK_HUTUO, Integer.parseInt(sid));
			if (bean == null) {
				// 返回错误消息，游戏服务端还未配置该服务器的支付
				retStr = "{\"errno\":1,\"errmsg\":\"游戏区服未配置\"}";
				response(retStr, channel);
				logger.error(String.format(
						"pay notice failed serverid unconfigured , platform : %s, orderno : %s , serverid : %s",
						AppConst.PAY_CHANNEL_APPLE, orderId, sid));
				return;
			}

			List<PayNoticeInfo> orders = PayNoticeInfoService.getOrderInfoByOrderNo(orderId,
					AppConst.PAY_CHANNEL_APPLE);

			// 订单不为空,直接通知客户端联系客服补单
			if (orders.size() != 0) {
				retStr = "{\"errno\":1,\"errmsg\":\"该订单已存在请联系客服补单\"}";
				response(retStr, channel);
				return;
			}

			JSONObject extJson = JSONObject.fromObject(extinfo);
			String os = "apple";
			String version = "1.1.0";
			if (extJson.containsKey("os")) {
				os = extJson.getString("os").toString();
			}
			if (extJson.containsKey("version")) {
				version = extJson.getString("version");
			}

			// 平台支付时间
			Timestamp platPayTime = new Timestamp(pPayTime);
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
			info.setSdkChannel(AppConst.PAY_CHANNEL_APPLE);
			info.setOrderNo(orderId);
			info.setPayNoticeUrl(url);
			info.setOrderType(orderType.value());

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
				autoSubOrder.setSdkChannel(AppConst.PAY_CHANNEL_APPLE);
				autoSubOrder.setValidateReceipt(receipt);
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

			httpClient = HttpClients.custom().build();
			httpGet = new HttpGet(noticeUrl.toString());
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
			logger.error(
					String.format("pay notice exception, platform : %s, orderno : %s", AppConst.SDK_YOUGU, orderId));
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

	public JSONObject validateIap(String receipt) {
		return validateIap("https://buy.itunes.apple.com/verifyReceipt", receipt);
	}

	private JSONObject validateIap(String url, String receipt) {

		HttpClient httpClient = null;
		HttpPost httpPost = null;
		HttpResponse response = null;
		// 将数据存储到数据库中，以备补单需求
		try {
			httpClient = HttpClients.custom().build();

			httpPost = new HttpPost(url);
			RequestConfig reqConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
			httpPost.setConfig(reqConfig);
			httpPost.setHeader(HttpHeaders.CONNECTION, "close");

			JSONObject data = new JSONObject();
			data.put("receipt-data", receipt);
			data.put("password", SECRET);

			StringEntity entity = new StringEntity(data.toString());
			entity.setContentEncoding("utf-8");
			entity.setContentType("application/json");
			httpPost.setEntity(entity);
			response = httpClient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			String resultStr = EntityUtils.toString(httpEntity);

			JSONObject result = JSONObject.fromObject(resultStr);
			httpPost.releaseConnection();

			if (result.getInt("status") == 21007) {
				return validateIap("https://sandbox.itunes.apple.com/verifyReceipt", receipt);
			}
			return result;

		} catch (Exception e) {

			logger.error(e);
			logger.error(String.format("pay validate exception, platform : %s, receiptData : %s", AppConst.SDK_HUTUO,
					receipt));
			// 返回错误消息
			e.printStackTrace();
			return null;
		} finally {
			if (response != null) {
				EntityUtils.consumeQuietly(response.getEntity());
			}
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
		}

	}

}
