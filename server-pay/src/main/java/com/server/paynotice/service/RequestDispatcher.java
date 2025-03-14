package com.server.paynotice.service;

import java.util.HashMap;
import java.util.Map;

import com.server.paynotice.common.RequestUri;
import com.server.paynotice.service.impl.AddCancelUser;
import com.server.paynotice.service.impl.AmazonPayHuTuoPayNotice;
import com.server.paynotice.service.impl.ApplePayHuTuoPayNotice;
import com.server.paynotice.service.impl.ClientQueryUser;
import com.server.paynotice.service.impl.DelCancelUser;
import com.server.paynotice.service.impl.GooglePayHuTuoPayNotice;
import com.server.paynotice.service.impl.LongXiaoPayNotice;
import com.server.paynotice.service.impl.QueryCancelUser;
import com.server.paynotice.service.impl.TestNet;
import com.server.paynotice.service.impl.YouGuPayNotice;

public class RequestDispatcher {

	static Map<String, IRequestServcie> requestMap = new HashMap<String, IRequestServcie>();
	static {
		requestMap.put(RequestUri.ADD_CANCELUSER, new AddCancelUser());
		requestMap.put(RequestUri.DEL_CACNELUSER, new DelCancelUser());
		requestMap.put(RequestUri.QUERY_CACNELUSER, new QueryCancelUser());
		requestMap.put(RequestUri.CLIENT_QUERY_USER, new ClientQueryUser());

		/** 虎拓谷歌支付通知 */
		requestMap.put(RequestUri.GOOGLE_PAY_HUTUO_PAYNOTICE, new GooglePayHuTuoPayNotice());
		/** 虎拓苹果支付通知 */
		requestMap.put(RequestUri.APPLE_PAY_HUTUO_PAYNOTICE, new ApplePayHuTuoPayNotice());
		/** 虎拓亚马逊支付通知 */
		requestMap.put(RequestUri.AWS_PAY_HUTUO_PAYNOTICE,new AmazonPayHuTuoPayNotice());

		requestMap.put(RequestUri.TEST_NET, new TestNet());

	}

	public static IRequestServcie valueOf(String uri) {
		return requestMap.get(uri);
	}
}
