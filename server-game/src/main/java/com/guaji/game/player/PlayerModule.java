package com.guaji.game.player;

import org.guaji.app.ObjModule;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;

import com.google.protobuf.ProtocolMessageEnum;

/**
 * 玩家模块基类
 * 
 * @author hawk
 */
public class PlayerModule extends ObjModule {
	/**
	 * 玩家对象
	 */
	protected Player player = null;

	/**
	 * 构造函数
	 * 
	 * @param player
	 */
	public PlayerModule(Player player) {
		super(player);

		this.player = player;
	}

	/**
	 * 玩家上线处理
	 * 
	 * @return
	 */
	protected boolean onPlayerLogin() {
		return true;
	}

	/**
	 * 玩家组装完成, 主要用来后期数据同步
	 * @return
	 */
	protected boolean onPlayerAssemble() {
		return true;
	}
	
	/**
	 * 玩家下线处理
	 * 
	 * @return
	 */
	protected boolean onPlayerLogout() {
		return true;
	}

	/**
	 * 消息处理
	 */
	public boolean onMessage(Msg msg) {
		return super.onMessage(msg);
	}

	/**
	 * 发送协议
	 * 
	 * @param protocol
	 * @return
	 */
	public boolean sendProtocol(Protocol protocol) {
		if (player != null) {
			return player.sendProtocol(protocol);
		}
		return false;
	}

	/**
	 * 通知错误码
	 * 
	 * @param errCode
	 */
	public void sendError(int hpCode, int errCode) {
		player.sendError(hpCode, errCode);
	}

	/**
	 * 通知错误码
	 * 
	 * @param errCode
	 */
	public void sendError(int hpCode, int errCode, int errFlag) {
		player.sendError(hpCode, errCode, errFlag);
	}
	
	/**
	 * 通知错误码
	 * 
	 * @param errCode
	 */
	public void sendError(int hpCode, ProtocolMessageEnum errCode) {
		player.sendError(hpCode, errCode);
	}
}
