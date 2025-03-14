package com.guaji.game.service;

import java.util.Collection;

import org.guaji.app.AppObj;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.service.IService;
import org.guaji.service.ServiceManager;

/**
 * 服务代理, 避免直接获取服务对象操作
 */
public class ServiceProxy {
	/**
	 * 协议处理
	 * 
	 * @param appObj
	 * @param protocol
	 * @return
	 */
	public static boolean onProtocol(AppObj appObj, Protocol protocol) {
		Collection<IService> services = ServiceManager.getInstance().getServices();
		if (services != null) {
			for(IService service : services) {
				if (service.onProtocol(appObj, protocol)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 消息处理
	 * 
	 * @param serviceName
	 * @param appObj
	 * @param msg
	 * @return
	 */
	public static boolean onMessage(AppObj appObj, Msg msg) {
		Collection<IService> services = ServiceManager.getInstance().getServices();
		if (services != null) {
			for(IService service : services) {
				if (service.onMessage(appObj, msg)) {
					return true;
				}
			}
		}
		return false;
	}
}
