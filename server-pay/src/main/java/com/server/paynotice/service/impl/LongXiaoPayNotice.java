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

public class LongXiaoPayNotice  extends RequestService {
	private static Logger logger = Logger.getLogger(YouGuPayNotice.class);
	private static final String KEY = "DjsPYrG2w0iMC9";
	@Override
	public boolean verify(Map<String, String> param, Channel channel) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void request(Map<String, String> param, Channel channel) {
		// TODO Auto-generated method stub
		String retStr = "{\"errno\":1,\"errmsg\":\"充值失败\"}";
		String game_id = param.get("game_id");
		String order_id = param.get("order_id");
		String uid = param.get("uid");
		String sid = param.get("sid");
		String cp_order_id = param.get("cp_order_id");
		String roleid = param.get("roleid");
		String roleName = param.get("rolename");
		String order_money = param.get("order_money");
		String productid = param.get("productid");
		String pay_type = param.get("pay_type");
		String extinfo = param.get("ext");
		String time = param.get("time");
		String sign = param.get("sign");
		try {
			StringBuffer joinMd5 = new StringBuffer();
			joinMd5.append(game_id);
			joinMd5.append(time);
			joinMd5.append(KEY);
			joinMd5.append(uid);
			joinMd5.append(order_money);
			joinMd5.append(cp_order_id);
			String sourceStr = joinMd5.toString().trim();
			String mySign = Md5Util.MD5(sourceStr);
			if(!sign.equals(mySign)){
				//返回错误消息
				retStr = "sign verify failed";
				response(retStr, channel);
				logger.error(String.format("longxiao pay notice token verify failed, platform : %s, orderno : %s, sdk token : %s, my token : %s, source str : %s", AppConst.SDK_YOUGU,order_id,mySign,sign,sourceStr));
				return;
			}
			//通知给具体的游戏服务器
			PayNoticeBean bean = PayNoticeUrlManager.getPayNoticBean(AppConst.SDK_LONGXIAO, Integer.parseInt(sid));
			if(bean==null){
				//返回错误消息，游戏服务端还未配置该服务器的支付
				retStr = "游戏区服未配置";
				response(retStr, channel);
				logger.error(String.format("pay notice failed serverid unconfigured , platform : %s, orderno : %s , serverid : %s", AppConst.SDK_YOUGU,order_id,sid));
				return;
			}
			if(extinfo==null||extinfo.trim().equals("")){
				//返回错误消息，游戏服务端需要的数据不存在
				retStr = "{\"errno\":1,\"errmsg\":\"游戏服务需要的参数未透传\"}";
				response(retStr, channel);
				logger.error(String.format("pay notice failed extinfo null, platform : %s, orderno : %s", AppConst.SDK_LONGXIAO,order_id));
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
			rechargeParam.setPayMoney(order_money);
			rechargeParam.setTest(false);
			rechargeParam.setOrderSerial(order_id);
			rechargeParam.setPlatform(AppConst.SDK_LONGXIAO);
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
			info.setAmount(Double.parseDouble(order_money));
			info.setRmbRate(1.0D);
			info.setVersion(version);
			info.setCurrency("CNY");
			info.setGameName("叫我女王大人");
			info.setProductName(productName);
			info.setOs(os);
			info.setSdkChannel(AppConst.SDK_LONGXIAO);
			info.setOrderNo(order_id);
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
			int statusCode = Integer.parseInt(resultJson.getString("errno"));
			String msg = resultJson.getString("errmsg");
			if(statusCode==0){
				msg = "success";
			}
			response(msg,channel);
			PayNoticeInfoService.update(statusCode, id);
		}catch(Exception e){
			logger.error(e);
			logger.error(String.format("pay notice exception, platform : %s, orderno : %s", AppConst.SDK_LONGXIAO,order_id));
			//返回错误消息
			e.printStackTrace();
			retStr = "充值回调服务器出错";
			response(retStr, channel);
		}
	}

}
