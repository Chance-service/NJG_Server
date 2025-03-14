package com.server.paynotice.service.impl;

import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import com.server.paynotice.bean.GuaJiRechargeParam;
import com.server.paynotice.bean.PayNoticeBean;
import com.server.paynotice.common.AppConst;
import com.server.paynotice.dbservice.PayNoticeInfoService;
import com.server.paynotice.pojo.PayNoticeInfo;
import com.server.paynotice.service.RequestService;
import com.server.paynotice.util.Md5Util;
import com.server.paynotice.xmlparser.PayNoticeUrlManager;

import io.netty.channel.Channel;
import net.sf.json.JSONObject;

public class YouGuPayNotice extends RequestService{
	private static Logger logger = Logger.getLogger(YouGuPayNotice.class);
	private static final String SECRET = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApob3ht6thVsAwRD/cFNtYOv7lK2xgirPkM0MyhmmSrRvF0gWxq9ToaehTs0inI6/znGJWGDpHX5j2eqZRs8Qjn+WDlXrjyPxSxdUAKooOpenKrhx7evwXgeA2GvZQ2ZQRK82yK2iyUxipSzEf7cAB26VFiL5hqscPFgwvctnWkAoa2pA5cMN3irBcCVCrnE+YKrjR+zKemagzwM19UOjte8CzLkkj+F0pj35zJVCZgo8DR8PeGbSBtz+J2ydPN5aZexsaJ9DsmxK96X2aZQUCSJrs4CRi6amRSeR/jabcryPBsIhFU+pR+rICLJYPKKrgsJGDwp6CzV74MTbVGTugwIDAQAB";
	@Override
	public boolean verify(Map<String, String> param, Channel channel) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void request(Map<String, String> param, Channel channel) {
		
		
		//String googleOriginalJson = body.getString(KEYS.REQ_GOOGLE_JSON);
	    //String googleSignature = body.getString(KEYS.REQ_GOOGLE_SIGNATURE);
	    //String customerId = body.getString(KEYS.REQ_GOOGLE_CUSTOMER_ID);
	    //String serverId = body.getString(KEYS.REQ_GOOGLE_SERVER_ID);
	    //String extra = body.getString(KEYS.REQ_GOOGLE_EXTRA);
	        
		// TODO Auto-generated method stub
		String retStr = "{\"errno\":1,\"errmsg\":\"充值失败\"}";
		String sid = param.get("sid");
		String uid = param.get("uid");
		String orderno = param.get("order_id");
		String amount = param.get("amount");
		String status = param.get("status");
		String extinfo = param.get("extinfo");
		String nonce = param.get("nonce");
		String token = param.get("token");
		try {
			int statusIntValue = Integer.parseInt(status);
			if(statusIntValue!=1){
				//返回错误消息
				retStr = "{\"errno\":1,\"errmsg\":\"status不为1\"}";
				response(retStr, channel);
				logger.error(String.format("yougu pay notice status value not equals 1, platform : %s, orderno : %s, sdk status : %s", AppConst.SDK_YOUGU,orderno,status));
				return;
			}
			//new
			StringBuffer joinMd5 = new StringBuffer();
			joinMd5.append(SECRET);//secret是 a8374da41b7ffc722877119b96c4999a
			joinMd5.append(uid);//uid
			joinMd5.append(sid);//sid
			joinMd5.append(orderno);//orderno
			joinMd5.append(amount);//amount
			joinMd5.append(nonce);//nonce
			String sourceStr = joinMd5.toString().trim();
			String sign = Md5Util.MD5(joinMd5.toString().trim());//16进制的md5串
			if(!sign.equals(token)){
				//返回错误消息
				retStr = "{\"errno\":1,\"errmsg\":\"token验证失败\"}";
				response(retStr, channel);
				logger.error(String.format("yougu pay notice token verify failed, platform : %s, orderno : %s, sdk token : %s, my token : %s, source str : %s", AppConst.SDK_YOUGU,orderno,token,sign,sourceStr));
				return;
			}
			//通知给具体的游戏服务器
			PayNoticeBean bean = PayNoticeUrlManager.getPayNoticBean(AppConst.SDK_YOUGU, Integer.parseInt(sid));
			if(bean==null){
				//返回错误消息，游戏服务端还未配置该服务器的支付
				retStr = "{\"errno\":1,\"errmsg\":\"游戏区服未配置\"}";
				response(retStr, channel);
				logger.error(String.format("pay notice failed serverid unconfigured , platform : %s, orderno : %s , serverid : %s", AppConst.SDK_YOUGU,orderno,sid));
				return;
			}
			if(extinfo==null||extinfo.trim().equals("")){
				//返回错误消息，游戏服务端需要的数据不存在
				retStr = "{\"errno\":1,\"errmsg\":\"游戏服务需要的参数未透传\"}";
				response(retStr, channel);
				logger.error(String.format("pay notice failed extinfo null, platform : %s, orderno : %s", AppConst.SDK_YOUGU,orderno));
				return;
			}
			
			JSONObject extJson = JSONObject.fromObject(extinfo);
			String productName = "";
			if(extJson.containsKey("name")){
				productName = extJson.get("name").toString();
			}else if(extJson.containsKey("productName")){
				productName = extJson.get("productName").toString();
			}
			String os = "android";
			String version = "1.1.0";
			if(extJson.containsKey("os")){
				os = extJson.getString("os").toString();
			}
			if(extJson.containsKey("version")){
				version = extJson.getString("version");
			}
			
			GuaJiRechargeParam rechargeParam = new GuaJiRechargeParam();
			rechargeParam.setGoodsId(productName);
			rechargeParam.setGoodsCount(1);
			rechargeParam.setPuid(uid);
			rechargeParam.setPayMoney(amount);
			rechargeParam.setTest(false);
			rechargeParam.setOrderSerial(orderno);
			rechargeParam.setPlatform(AppConst.SDK_YOUGU);
			rechargeParam.setServerId(Integer.parseInt(sid));
			
			JSONObject jObject = JSONObject.fromObject(rechargeParam);
			String noticeParam = jObject.toString();
			String baseNoticeUrl = bean.getBaseUrl();
			StringBuffer noticeUrl = new StringBuffer();
			noticeUrl.append(baseNoticeUrl);
			noticeUrl.append("?params=");
			noticeUrl.append(URLEncoder.encode(noticeParam,"UTF-8"));
			String url = noticeUrl.toString().trim();
			
			PayNoticeInfo info = new PayNoticeInfo();
			info.setSid(Integer.parseInt(sid));
			info.setUid(uid);
			info.setAmount(Double.parseDouble(amount));
			info.setRmbRate(1.0D);
			info.setVersion(version);
			info.setCurrency("CNY");
			info.setGameName("美人劫");
			info.setProductName(productName);
			info.setOs(os);
			info.setSdkChannel(AppConst.SDK_YOUGU);
			info.setOrderNo(orderno);
			info.setPayNoticeUrl(url);
			info.setPayTime(new Timestamp(System.currentTimeMillis()));
			int id = PayNoticeInfoService.insert(info);
			
			//将数据存储到数据库中，以备补单需求
			HttpClient httpClient = HttpClients.custom().build();
			HttpGet httpGet = new HttpGet(noticeUrl.toString());
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			long contentLenth = entity.getContentLength();
			byte[] retData = new byte[(int)contentLenth];
			entity.getContent().read(retData);
			String resultStr = new String(retData,"UTF-8");
			JSONObject resultJson = JSONObject.fromObject(resultStr);
			response(resultStr,channel);
			int statusCode = Integer.parseInt(resultJson.getString("errno"));
			PayNoticeInfoService.update(statusCode, id);
		}catch(Exception e){
			logger.error(e);
			logger.error(String.format("pay notice exception, platform : %s, orderno : %s", AppConst.SDK_YOUGU,orderno));
			//返回错误消息
			e.printStackTrace();
			retStr = "{\"errno\":1,\"errmsg\":\"充值回调服务器出错\"}";
			response(retStr, channel);
		}
	}

}
