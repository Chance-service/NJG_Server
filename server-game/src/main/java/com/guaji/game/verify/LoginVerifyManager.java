package com.guaji.game.verify;

import java.util.HashMap;
import java.util.Map;

import com.guaji.game.protocol.SDKName;

public class LoginVerifyManager {
	/**
	 * key sdk Channel name
	 * value 登录验证类
	 */
	private static Map<String, ILoginVerify> loginVerifyMap = new HashMap<String,ILoginVerify>();
	static{
		loginVerifyMap.put("android_h365",new H365LoginVerify());
		//loginVerifyMap.put("H54647", new H54647());
		//loginVerifyMap.put(SDKName.PBSDKName.yougu_channel_android.name(), new YouGuLoginVerify());
		//loginVerifyMap.put(SDKName.PBSDKName.yougu_channel_ios.name(), new YouGuLoginVerify());
		
		//	loginVerifyMap.put(SDKName.PBSDKName.longxiao_channel_ios.name(), new LongXiaoLoginVerify());
		//	loginVerifyMap.put(SDKName.PBSDKName.longxiao_channel_android.name(), new LongXiaoLoginVerify());
	}
	/**
	 * 获取登录验证对象
	 * @param channelName
	 * @return
	 */
	public static ILoginVerify getLoginVerify(String channelName){
		return loginVerifyMap.get(channelName);
	}
}
