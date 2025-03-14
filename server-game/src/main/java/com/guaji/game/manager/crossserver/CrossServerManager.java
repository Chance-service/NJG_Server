package com.guaji.game.manager.crossserver;

import org.guaji.app.AppObj;
import org.guaji.log.Log;
import org.guaji.net.client.ClientSession;
import org.guaji.net.protocol.Protocol;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.CrossBattleCfg;
import com.guaji.game.config.CrossGroupCfg;
import com.guaji.game.manager.crossbattle.CrossBattleManager;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.CsBattle.BattleRecordResponse;
import com.guaji.game.protocol.CsBattle.BattleResponse;
import com.guaji.game.protocol.CsBattle.ChallengeResponse;
import com.guaji.game.protocol.CsBattle.DefenderResponse;
import com.guaji.game.protocol.CsBattle.OSMainInfoResponse;
import com.guaji.game.protocol.CsBattle.PlayerInfoSyncResponse;
import com.guaji.game.protocol.CsBattle.PlayerSnapshotResponse;
import com.guaji.game.protocol.CsBattle.RankResponse;
import com.guaji.game.protocol.CsBattle.RefreshVsResponse;
import com.guaji.game.protocol.CsBattle.ServerRegister;
import com.guaji.game.protocol.CsBattle.SyncRankData;

/**
 * 跨服管理器
 */
public class CrossServerManager extends AppObj {
	
	private ClientSession clientSession;
	
	private static CrossServerManager instance;
	
	private int tickIndex = 0;
	
	public CrossServerManager(GuaJiXID xid) {
		super(xid);
		instance = this;
	}
	
	public static CrossServerManager getInstance() {
		return instance;
	}

	public ClientSession getCSSession() {
		return this.clientSession;
	}
	
	@Override
	public boolean onTick() {
		if(tickIndex++ % 100 == 0) {
			if(!clientSession.isActive()) {
				this.connect();
			}
		}
		return super.onTick();
	}

	/**
	 * 服务器之间连接
	 */
	private void connect() {
		if(!CrossBattleCfg.getInstance().getCrossserverOpen()) {
			return;
		}
		// 跨服配置
		CrossGroupCfg csGroupCfg = CrossGroupCfg.getGroupCfg(GsApp.getInstance().getServerIdentify());
		if(csGroupCfg == null) {
			Log.crossLog("cross server group not exist, identify: " + GsApp.getInstance().getServerIdentify());
			return;
		}
		// 建立连接
		boolean connect = clientSession.connect(csGroupCfg.getIp(), csGroupCfg.getPort(), 500);
		if (connect) {
			Log.crossLog("coneect cross server success , ip: " + csGroupCfg.getIp() + " ,port: " + csGroupCfg.getPort());
		} else {
			Log.crossLog("coneect cross server failed , ip: " + csGroupCfg.getIp() + " ,port: " + csGroupCfg.getPort());
		}
		// 发送注册协议
		ServerRegister.Builder register = ServerRegister.newBuilder();
		register.setVersion(2);
		register.setServerIdentify(GsApp.getInstance().getServerIdentify());
		clientSession.sendProtocol(Protocol.valueOf(HP.code.OPCODE_REGISTER_VALUE, register));
	}

	public void init() {
		clientSession = new ClientSession() {
			@Override
			protected boolean onReceived(Object message) {
				Protocol protocol = (Protocol)message;
				onProto(protocol);
				return false;
			}
		};
	}
	
	/**
	 * 处理服务器之间的协议
	 * 
	 * @param protocol
	 */
	private void onProto(Protocol protocol) {
		switch (protocol.getType()) {
		case HP.code.PVP_VS_INFO_VALUE:
			// 打开界面协议
			CrossBattleManager.getInstance().responseMainInfo(protocol.parseProtocol(OSMainInfoResponse.getDefaultInstance()));
			break;
		case HP.code.SYNC_PLAYER_DATA_VALUE:
			// 同步账号数据
			CrossBattleManager.getInstance().responseSyncData(protocol.parseProtocol(PlayerInfoSyncResponse.getDefaultInstance()));
			break;
		case HP.code.REFRESH_VS_INFO_VALUE:
			// 刷新对战数据
			CrossBattleManager.getInstance().responseRefreshVs(protocol.parseProtocol(RefreshVsResponse.getDefaultInstance()));
			break;
		case HP.code.PVP_VS_CHALLENGE_VALUE:
			// 战斗结束--返回挑战者数据
			CrossBattleManager.getInstance().responseChallenge(protocol.parseProtocol(ChallengeResponse.getDefaultInstance()));
			break;
		case HP.code.PVP_VS_DEFENDER_VALUE:
			// 战斗结束--返回被挑战者数据
			CrossBattleManager.getInstance().responseDefender(protocol.parseProtocol(DefenderResponse.getDefaultInstance()));
			break;
		case HP.code.PVP_BATTLELIST_VALUE:
			// 查看战报列表
			CrossBattleManager.getInstance().responseBattleList(protocol.parseProtocol(BattleRecordResponse.getDefaultInstance()));
			break;
		case HP.code.PVP_BATTLE_DATA_VALUE:
			// 查看战报列表
			CrossBattleManager.getInstance().responseBattleData(protocol.parseProtocol(BattleResponse.getDefaultInstance()));
			break;
		case HP.code.RANK_MESSAGE_VALUE:
			// 排行榜数据
			CrossBattleManager.getInstance().responseRankMessage(protocol.parseProtocol(RankResponse.getDefaultInstance()));
			break;
		case HP.code.PLAYER_ROLES_INFO_VALUE:
			// 查看玩家详情
			CrossBattleManager.getInstance().responsePlayerSnapshot(protocol.parseProtocol(PlayerSnapshotResponse.getDefaultInstance()));
			break;
		case HP.code.SYNC_RANDOM_RANK_VALUE:
			// 同步随机排行数据
			CrossBattleManager.getInstance().syncRankData(protocol.parseProtocol(SyncRankData.getDefaultInstance()));
			break;
		default:
			break;
		}
	}

}
