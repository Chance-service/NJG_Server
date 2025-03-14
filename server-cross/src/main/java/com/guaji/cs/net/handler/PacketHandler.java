package com.guaji.cs.net.handler;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.guaji.net.protocol.Protocol;

import com.guaji.cs.CrossServer;
import com.guaji.cs.battle.BattleManager;
import com.guaji.cs.tick.ITickable;
import com.guaji.game.protocol.HP;

/**
 * 协议处理句柄
 */
public class PacketHandler implements ITickable {
	/**
	 * 队列写索引
	 */
	private volatile int writeIndex;
	
	/**
	 * 队列写索引
	 */
	private volatile int curIndex;
	
	/**
	 * 索引锁
	 */
	private Lock indexLock;
	
	/**
	 * 包队列
	 */
	private List<Protocol>[] packetList;

	/**
	 * 单例对象
	 */
	private static final PacketHandler instance = new PacketHandler();

	/**
	 * 获取单例句柄
	 * 
	 * @return
	 */
	public static PacketHandler getInstance() {
		return instance;
	}

	/**
	 * 构造函数
	 */
	@SuppressWarnings("unchecked")
	private PacketHandler() {
		writeIndex = 0;
		indexLock = new ReentrantLock();
		packetList = new List[] { new LinkedList<Protocol>(), new LinkedList<Protocol>() };
		CrossServer.getInstance().addTickable(this);
	}

	/**
	 * 接收到数据包, 添加到当前可写的协议队列
	 * 
	 * @param packet
	 */
	public void onRecvPacket(Protocol packet) {
		indexLock.lock();
		try {
			packetList[writeIndex].add(packet);
		} finally {
			indexLock.unlock();
		}
	}

	/**
	 * 帧更新
	 */
	@Override
	public void onTick() {
		// 交换读写列表
		indexLock.lock();
		curIndex = writeIndex;
		try {
			writeIndex = 1 - writeIndex;
		} finally {
			indexLock.unlock();
		}
		// 处理协议
		for (Protocol packet : packetList[curIndex]) {
			handlePacket(packet);
		}
		packetList[curIndex].clear();
	}

	/**
	 * 协议处理
	 * 
	 * @param packet
	 */
	public boolean handlePacket(Protocol packet) {
		if (packet == null || packet.getSession() == null) {
			return false;
		}
		try {
			int opcode = packet.getType();
			switch (opcode) {
			
			case HP.code.OPCODE_REGISTER_VALUE:
				// 服务器注册
				SessionHandler.getInstance().onRegister(packet);
				break;
			case HP.code.PUSH_PLAYER_DATA_VALUE:
				// 接收推送玩家数据
				BattleManager.getInstance().onPlayerSignup(packet);
				break;
			case HP.code.PVP_VS_INFO_VALUE:
				BattleManager.getInstance().showMainLogic(packet);
				break;
			case HP.code.SYNC_PLAYER_DATA_VALUE:
				// 刷新玩家数据
				BattleManager.getInstance().syncPlayerData(packet);
				break;
			case HP.code.REFRESH_VS_INFO_VALUE:
				// 刷新对手数据
				BattleManager.getInstance().refreshVsLogic(packet);
				break;
			case HP.code.PVP_VS_CHALLENGE_VALUE:
				// 请求战斗
				BattleManager.getInstance().challengeData(packet);
				break;
			case HP.code.PVP_BATTLELIST_VALUE:
				// 查看战报列表
				BattleManager.getInstance().seeBattleList(packet);
				break;
			case HP.code.PVP_BATTLE_DATA_VALUE:
				// 查看战报
				BattleManager.getInstance().seeBattleData(packet);
				break;
			case HP.code.RANK_MESSAGE_VALUE:
				// 排行榜数据
				BattleManager.getInstance().rankMessage(packet);
				break;
			case HP.code.PLAYER_ROLES_INFO_VALUE:
				// 查看玩家详情
				BattleManager.getInstance().queryPlayerSnapshot(packet);
				break;
			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
