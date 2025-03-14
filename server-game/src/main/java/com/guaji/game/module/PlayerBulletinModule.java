package com.guaji.game.module;


import java.util.List;

import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.GsConfig;
import com.guaji.game.entity.BulletinEntity;
import com.guaji.game.manager.BulletinManager;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.protocol.Bulletin.BulletinContentRet;
import com.guaji.game.protocol.Bulletin.BulletinContentRsp;
import com.guaji.game.protocol.Bulletin.BulletinTitleInfo;
import com.guaji.game.protocol.HP;
import com.guaji.game.util.BuilderUtil;

/**
 * 玩家公告模塊
 */
public class PlayerBulletinModule extends PlayerModule {
	/**
	 * 构造函数
	 * 
	 * @param player
	 */
	public PlayerBulletinModule(Player player) {
		super(player);

		listenProto(HP.code.BULLETIN_TITLE_LIST_C);
		listenProto(HP.code.BULLETIN_CONTENT_SYNC_C);
	}

	/**
	 * 消息响应
	 * 
	 * @param msg
	 * @return
	 */
	@Override
	public boolean onMessage(Msg msg) {
		return super.onMessage(msg);
	}

	/**
	 * 协议响应
	 * 
	 * @param protocol
	 * @return
	 */
	@Override
	public boolean onProtocol(Protocol protocol) {
		if (GsConfig.getInstance().getBulletinHost().length() > 0 && GsConfig.getInstance().getBulletinGameID() > 0) {
			if (protocol.checkType(HP.code.BULLETIN_TITLE_LIST_C)) {
				onGetBulletinTitleInfo(protocol);
				return true;
			} else if (protocol.checkType(HP.code.BULLETIN_CONTENT_SYNC_C)) {
				onGetBulletinContent(protocol);
				return true;
			}
		}
		return super.onProtocol(protocol);
	}
	
	private void onGetBulletinTitleInfo(Protocol protocol) {
		List<BulletinEntity> bulletinEntiies = BulletinManager.getInstance().loadBulletinEntity();
		BulletinTitleInfo.Builder builder = BuilderUtil.getBulletinTitleInfo(player,bulletinEntiies);
		
		player.sendProtocol(Protocol.valueOf(HP.code.BULLETIN_TITLE_LIST_S,builder));
	}
	
	private void onGetBulletinContent(Protocol protocol) {
		BulletinContentRet ret = protocol.parseProtocol(BulletinContentRet.getDefaultInstance());
		int id = ret.getId();
		BulletinEntity bulletinEntity = BulletinManager.getInstance().getBulletinEntity(id);
		
		int errorCode = (bulletinEntity != null) ? 0:-1;
		
		BulletinContentRsp.Builder builder = BuilderUtil.getBulletinContent(bulletinEntity, errorCode);
		
		player.sendProtocol(Protocol.valueOf(HP.code.BULLETIN_CONTENT_SYNC_S,builder));
	}
	
}
