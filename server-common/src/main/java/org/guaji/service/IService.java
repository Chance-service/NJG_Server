package org.guaji.service;

import org.guaji.app.AppObj;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;

/**
 * 通用service接口
 * 
 * @author xulinqs
 * 
 */
public interface IService {
	/**
	 * 获得Service name
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * 处理协议
	 * 
	 * @param obj
	 * @param protocol
	 */
	public boolean onProtocol(AppObj appObj, Protocol protocol);

	/**
	 * 处理消息
	 * 
	 * @param obj
	 * @param protocol
	 */
	public boolean onMessage(AppObj appObj, Msg msg);
}
