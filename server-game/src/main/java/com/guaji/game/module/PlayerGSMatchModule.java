package com.guaji.game.module;

import org.guaji.annotation.ProtocolHandlerAnno;
import org.guaji.net.client.ClientSession;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.manager.crossserver.CrossServerManager;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 跨服匹配战模块;
 */
public class PlayerGSMatchModule extends PlayerModule {
	/**
	 * 构造
	 * 
	 * @param player
	 */
	public PlayerGSMatchModule(Player player) {
		super(player);
	}

	@Override
	protected boolean onPlayerLogin() {
		// 更新玩家匹配信息
//		player.updateGlobalMatchInfo(false);

		return super.onPlayerLogin();
	}

	private boolean checkGlobalServerSessionActive(int protoCode) {
		ClientSession clientSession = CrossServerManager.getInstance().getCSSession();
		if (clientSession == null || !clientSession.isActive()) {
			// 全局服务器未连接上
			sendError(protoCode, Status.error.CROSS_SERVER_CONNECT_FAIL);
			return false;
		}
		return true;
	}

	/**
	 * 请求匹配战页面信息;
	 * 
	 * @param protocol
	 */
	@ProtocolHandlerAnno(code = HP.code.GS_MATCH_PAGEINFO_C_VALUE)
	private void getMatchPageInfo(Protocol protocol) {
		if (!checkGlobalServerSessionActive(protocol.getType())) {
			return;
		}
		CrossServerManager.getInstance().getCSSession().sendProtocol(Protocol.valueOf(HP.code.GS_MATCH_PAGEINFO_C_VALUE));
	}

}
