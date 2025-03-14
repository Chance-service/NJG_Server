package com.server.paynotice.xmlparser;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;

import com.server.paynotice.bean.PayNoticeBean;
import com.server.paynotice.service.impl.YouGuPayNotice;

public class PayNoticeUrlManager {
	private static Logger logger = Logger.getLogger(PayNoticeUrlManager.class);
	/**
	 * key 渠道名称
	 * value map
	 * 		key 服务器id
	 * 		value 支付通知地址对象
	 */
	
	private static ReadWriteLock loadLock = new ReentrantReadWriteLock();
	private static Lock rLock = loadLock.readLock();
	private static Lock wLock = loadLock.writeLock();
	private static Map<String, Map<Integer,PayNoticeBean>> payNoticeMaps = new HashMap<String,Map<Integer,PayNoticeBean>>();
	
	private static String cfgLocation;
	
	private static long lastModifyTime;
	
	public static void init(String cfg){
		cfgLocation = cfg;
		loadPayNoticeXML();
	}
	
	public static long getLastModifyTime(){
		return lastModifyTime;
	}
	public static String getXmlPath(){
		return cfgLocation;
	}
	/**
	 * 加载支付通知xml
	 */
	public static void loadPayNoticeXML(){
		try {
			File xmlFile = new File(cfgLocation);
			if(!xmlFile.exists()){
				logger.error("paynotice xml no suched!  "+cfgLocation);
				System.exit(0);
				return;
			}
			lastModifyTime = xmlFile.lastModified();
			Map<String, Map<Integer,PayNoticeBean>> maps = new HashMap<String,Map<Integer,PayNoticeBean>>();
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			SAXParser parser = parserFactory.newSAXParser();
			PayNoticeSaxHandler handler = new PayNoticeSaxHandler();
			parser.parse(xmlFile, handler);
			List<PayNoticeBean> beanList = handler.getBeanList();
			int size = beanList.size();
			PayNoticeBean bean = null;
			for(int i = 0;i<size;i++){
				bean = beanList.get(i);
				Map<Integer, PayNoticeBean> map = maps.get(bean.getSdkChannel());
				if(map==null){
					map = new HashMap<Integer,PayNoticeBean>();
					maps.put(bean.getSdkChannel(), map);
				}
				map.put(bean.getServerId(), bean);
			}
			wLock.lock();
			try {
				payNoticeMaps = maps;
			} finally {
				wLock.unlock();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取支付通知对象
	 * @param sdkChannel
	 * @param serverId
	 * @return
	 */
	public static PayNoticeBean getPayNoticBean(String sdkChannel,int serverId){
		rLock.lock();
		PayNoticeBean bean = null;
		try {
			Map<Integer, PayNoticeBean> map = payNoticeMaps.get(sdkChannel);
			if(map!=null){
				bean = map.get(serverId);
			}
			return bean;
		} finally {
			rLock.unlock();
		}
		
	}
}
