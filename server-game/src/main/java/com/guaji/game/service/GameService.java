package com.guaji.game.service;

import org.guaji.app.AppObj;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.service.IService;

import com.guaji.game.player.Player;

/**
 * 游戏服务基础类
 */
public abstract class GameService implements IService {
	/**
	 * 获取服务名字
	 * 
	 * @return 返回名字
	 */
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	/**
	 * 协议处理
	 * 
	 * @return 返回true即表示协议拦截
	 */
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		return onProtocol((Player) appObj, protocol);
	}

	/**
	 * 消息处理
	 * 
	 * @return 返回true即表示消息拦截
	 */
	@Override
	public abstract boolean onMessage(AppObj appObj, Msg msg);

	/**
	 * 协议处理
	 * 
	 * @param appObj
	 * @param protocol
	 * @return 返回true即表示协议拦截
	 */
	public abstract boolean onProtocol(Player appObj, Protocol protocol);
}
