package com.server.paynotice.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
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
import com.server.paynotice.bean.PayNoticeBean;
import com.server.paynotice.bean.SubscribeType;
import com.server.paynotice.common.AppConst;
import com.server.paynotice.common.Settings;
import com.server.paynotice.dbservice.PayNoticeInfoService;
import com.server.paynotice.pojo.AutoSubOrder;
import com.server.paynotice.pojo.PayNoticeInfo;
import com.server.paynotice.service.RequestService;
import com.server.paynotice.util.DateUtil;
import com.server.paynotice.util.KEYS;
import com.server.paynotice.util.Util;
import com.server.paynotice.xmlparser.PayNoticeUrlManager;
import com.server.paynotice.xmlparser.RechargeConfig;
import com.server.paynotice.bean.OrderType;
import io.netty.channel.Channel;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年3月14日 下午4:39:26 类说明
 */
public class AmazonPayHuTuoPayNotice extends RequestService {

	enum VerifyReceiptProperty {
		STATUS, RECEIPTID, PRODUCTTYPE, PRODUCTID, PURCHASEDATE, CANCELDATE, MSG, RENEWALDATE
	}

	private static Logger logger = Logger.getLogger(AmazonPayHuTuoPayNotice.class);
	private static final String SECRET = "2:ofgFiwgmM57lVmnJF6-GzF-wQx-Uy8_A-LOyYoNyNNXvgSJYSnXl_W0qdzIzdm3U:cLh9LZTyhmr1ZE4G6h7CXg==";
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

		String retStr = "{\"errno\":1,\"errmsg\":\"充值失败\"}";
		// 平台支付账单
		String channelorder = param.get(KEYS.REQ_AWS_CHANNEL_ORDER).replaceAll(" ", "+");
		// 服务器编号
		String sid = param.get(KEYS.REQ_AWS_SERVER_ID);
		// 游戏账号
		String uid = param.get(KEYS.REQ_AWS_CUSTOMER_ID);
		// 渠道账号
		String puid = param.get(KEYS.REQ_AWS_PLATFORMUID);
		// 法币代号
		String currencyCode = param.get(KEYS.REQ_AWS_CURRENCY);
		// 法币代号
		String sku = param.get(KEYS.REQ_AWS_SKU);

		// 平台参数
		String platform = param.get(KEYS.REQ_PLATFORM);
		String productName = sku;
		String orderId = channelorder;
		String amount = param.get("amount");
		String extinfo = param.get("ext");
		Long pPayTime = Long.parseLong("0");
		OrderType orderType = OrderType.CONSUMABLE;
		HttpGet httpGet = null;
		HttpResponse response = null;
		HttpClient httpClient = null;

		try {

			if (extinfo == null || extinfo.trim().equals("")) {
				// 返回错误消息，游戏服务端需要的数据不存在
				retStr = "{\"errno\":1,\"errmsg\":\"游戏服务需要的参数未透传\"}";
				response(retStr, channel);
				logger.error(String.format("pay notice failed extinfo null, platform : %s, orderno : %s",
						AppConst.PAY_CHANNEL_AWS, orderId));
				return;
			}
			// 亚马逊特殊处理
			if(sku.indexOf("jp.co.school.battle.week4")==-1&&sku.indexOf("jp.co.school.battle.month3")==-1)
			{
				if (sku.indexOf("week") != -1) {
					sku = "week";
				} else if (sku.indexOf("month") != -1) {
					sku = "month";
				}
			}
			

			if (RechargeConfig.getRechargeConfig(platform) == null
					|| RechargeConfig.getRechargeConfig(platform).getByName(sku) == null) {
				retStr = "{\"errno\":1,\"errmsg\":\"充值配置项错误\"}";
				response(retStr, channel);
				logger.error(String.format("pay item not found platform : %s, good : %s", platform, sku));
				return;
			}

			amount = String.valueOf(RechargeConfig.getRechargeConfig(platform).getByName(sku).getCostMoney());

			EnumMap<VerifyReceiptProperty, Object> receiptInfo = verifyReceipt(Settings.aws_receipturl, SECRET, puid,
					channelorder);

			if (!String.valueOf(receiptInfo.get(VerifyReceiptProperty.STATUS)).equals("200")) {
				retStr = "{\"errno\":1,\"errmsg\":\"亚马逊校验订单失败\"}";
				logger.error(String.format("receipt param  error : %s, receipt : %s", AppConst.PAY_CHANNEL_AWS, sku));
				response(retStr, channel);
				return;
			}

			productName=String.valueOf(receiptInfo.get(VerifyReceiptProperty.PRODUCTID));
			// 是订阅类型
			Long platformExpirDate = Long.parseLong("0");
			if (receiptInfo.get(VerifyReceiptProperty.PRODUCTTYPE).equals("SUBSCRIPTION")) {
				orderType = OrderType.SUBSCRIBE;
				platformExpirDate = Long.parseLong(String.valueOf(receiptInfo.get(VerifyReceiptProperty.RENEWALDATE)));
			}

			// 订单编号
			orderId = String.valueOf(receiptInfo.get(VerifyReceiptProperty.RECEIPTID));
			// 支付时间
			pPayTime = Long.parseLong(String.valueOf(receiptInfo.get(VerifyReceiptProperty.PURCHASEDATE)));

			// 通知给具体的游戏服务器
			PayNoticeBean bean = PayNoticeUrlManager.getPayNoticBean(AppConst.SDK_HUTUO, Integer.parseInt(sid));
			if (bean == null) {
				// 返回错误消息，游戏服务端还未配置该服务器的支付
				retStr = "{\"errno\":1,\"errmsg\":\"游戏区服未配置\"}";
				response(retStr, channel);
				logger.error(String.format(
						"pay notice failed serverid unconfigured , platform : %s, orderno : %s , serverid : %s",
						AppConst.PAY_CHANNEL_AWS, orderId, sid));
				return;
			}

			List<PayNoticeInfo> orders = PayNoticeInfoService.getOrderInfoByOrderNo(orderId, AppConst.PAY_CHANNEL_AWS);

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
			info.setSdkChannel(AppConst.PAY_CHANNEL_AWS);
			info.setOrderNo(orderId);
			info.setPayNoticeUrl(url);
			info.setOrderType(orderType.value());

			info.setPltformPayTime(new Timestamp(pPayTime));
			info.setPayTime(new Timestamp(System.currentTimeMillis()));
			int id = 0;
			if (orderType == OrderType.SUBSCRIBE) {
				AutoSubOrder autoSubOrder = new AutoSubOrder();
				Date exprieDate = new Timestamp(exprieCalendar.getTimeInMillis());
				autoSubOrder.setPlatAccNum(puid);
				autoSubOrder.setOrderNum(orderId);
				autoSubOrder.setSdkChannel(AppConst.PAY_CHANNEL_AWS);
				autoSubOrder.setValidateReceipt(channelorder);
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
			logger.info(String.format("uid=%s puid=%s send good by %s", uid, puid, resultStr));
			int statusCode = Integer.parseInt(resultJson.getString("errno"));

			Timestamp getGoodTime = new Timestamp(System.currentTimeMillis());
			// 从游戏支付接口返回的状态 需要转换下
			statusCode = (statusCode == 0 ? 1 : 0);

			PayNoticeInfoService.update(statusCode, getGoodTime, info.getId());

		} catch (Exception e) {
			logger.error(e);
			logger.error(
					String.format("pay notice exception, platform : %s, orderno : %s", AppConst.SDK_HUTUO, orderId));
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

	public EnumMap<VerifyReceiptProperty, Object> verifyReceipt(final String receptUrl, final String developerSecret,
			final String userId, final String receiptId) {
		String url = receptUrl + "/version/1.0/verifyReceiptId/developer/" + developerSecret + "/user/" + userId
				+ "/receiptId/" + receiptId;
		EnumMap<VerifyReceiptProperty, Object> retMap = new EnumMap<VerifyReceiptProperty, Object>(
				VerifyReceiptProperty.class);
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setConnectTimeout(30000);
			con.setReadTimeout(30000);
			int responseCode = con.getResponseCode();

			switch (responseCode) {
			case 400: {
				retMap.put(VerifyReceiptProperty.STATUS, responseCode);
				retMap.put(VerifyReceiptProperty.MSG, "Amazon RVS Error: Invalid receiptID");

			}
				break;

			case 496: {
				retMap.put(VerifyReceiptProperty.STATUS, responseCode);
				retMap.put(VerifyReceiptProperty.MSG, "Amazon RVS Error: Invalid developerSecret");
			}

				break;

			case 497: {
				retMap.put(VerifyReceiptProperty.STATUS, responseCode);
				retMap.put(VerifyReceiptProperty.MSG, "Amazon RVS Error: Invalid userId");
			}
				break;

			case 500: {
				retMap.put(VerifyReceiptProperty.STATUS, responseCode);
				retMap.put(VerifyReceiptProperty.MSG, "Amazon RVS Error: Internal Server Error");
			}
				break;

			case 200: {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				// Parse RVS Response
				JSONObject responseJson = JSONObject.fromObject(response.toString());
				logger.info(String.format("aws responseJson  rep=%s", responseJson.toString()));
				boolean testTransaction = responseJson.optBoolean("testTransaction");
				retMap.put(VerifyReceiptProperty.STATUS, responseCode);
				retMap.put(VerifyReceiptProperty.MSG, "Amazon RVS success");
				retMap.put(VerifyReceiptProperty.RECEIPTID, responseJson.getString("receiptId"));
				retMap.put(VerifyReceiptProperty.PRODUCTTYPE, responseJson.getString("productType"));
				retMap.put(VerifyReceiptProperty.PRODUCTID, responseJson.getString("productId"));
				retMap.put(VerifyReceiptProperty.PURCHASEDATE, responseJson.getString("purchaseDate"));
				retMap.put(VerifyReceiptProperty.CANCELDATE, responseJson.getString("cancelDate"));
				
				if(responseJson.containsKey("renewalDate"))
					retMap.put(VerifyReceiptProperty.RENEWALDATE, responseJson.getString("renewalDate"));
				else
					retMap.put(VerifyReceiptProperty.RENEWALDATE,"0");
			

			}
				break;

			default: {
				retMap.put(VerifyReceiptProperty.STATUS, responseCode);
				retMap.put(VerifyReceiptProperty.MSG, "Amazon RVS Error: Undefined Response Code From Amazon RVS");
			}
				break;

			}

		} catch (MalformedURLException e) {
			retMap.put(VerifyReceiptProperty.STATUS, -1);
			retMap.put(VerifyReceiptProperty.MSG, "Amazon RVS MalformedURLException");
			e.printStackTrace();
		} catch (IOException e) {
			retMap.put(VerifyReceiptProperty.STATUS, -1);
			retMap.put(VerifyReceiptProperty.MSG, "Amazon RVS IOException");
			e.printStackTrace();

		} finally {

		}
		return retMap;
	}

}
