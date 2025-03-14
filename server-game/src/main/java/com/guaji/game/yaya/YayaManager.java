package com.guaji.game.yaya;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.xid.GuaJiXID;

/**
 * 呀呀主播商场管理器
 * 
 * 海外无呀呀主播
 * 
 */
public class YayaManager extends AppObj {
	/**
	 * 购买日志
	 */
//	private final Logger logger = LoggerFactory.getLogger("Yaya");

	/**
	 * 购买协议回复状态
	 */
//	private static int YAYA_SHOP_SUCCESS = 0;
//	private static int YAYA_SHOP_PLAYER_NOEXIT = 1;
//	private static int YAYA_SHOP_PLAYER_ENTITY_NULL = 2;
//	private static int YAYA_SHOP_GOLD_NOT_ENOUGH = 9;

	/**
	 * 全局实例对象
	 */
	private static YayaManager instance = null;

	public static YayaManager getInstance() {
		return instance;
	}

	/**
	 * 构造
	 * 
	 * @param xid
	 */
	public YayaManager(GuaJiXID xid) {
		super(xid);
		instance = this;
	}

	/**
	 * 处理购买协议
	 * 
	 * @param protocol
	 */
	public boolean onProtocol(Protocol protocol) {
//		if (protocol.checkType(HP.code.YAYASHOP_REQUEST_S)) {
//			try {
//				HPYaYaShop msg = protocol.parseProtocol(HPYaYaShop.getDefaultInstance());
//				String msgInfo = JsonFormat.printToString(msg);
//				logger.info("YaYaShop jsonInfo: " + msgInfo);
//				HPYaYaShopRet.Builder builder = handleShop(protocol.getSession(), msg, msgInfo);
//				if (protocol.getSession() != null) {
//					protocol.getSession().sendProtocol(Protocol.valueOf(HP.code.YAYASHOP_REPLY_S, builder));
//				} else {
//					logger.info("YaYaShop failed session null: " + msgInfo);
//				}
//			} catch (Exception e) {
//				MyException.catchException(e);
//				// 记录进充值日志
//				logger.error(MyException.formatStackMsg(e));
//			}
//			return true;
//		}
		return true;
	}

	/*private HPYaYaShopRet.Builder handleShop(GuaJiSession session, HPYaYaShop msg, String msgInfo) {
		HPYaYaShopRet.Builder builder = HPYaYaShopRet.newBuilder();
//		builder.setResult(YAYA_SHOP_FAILED);
		try {
			int playerId = ServerData.getInstance().getPlayerIdByPuid(msg.getPuid().trim().toLowerCase());
			// 取玩家实体对象
			PlayerEntity playerEntity = null;
			Player player = PlayerUtil.queryPlayer(playerId);
			PlayerData playerData = null;
			// 在线玩家
			boolean onlineRecharge = false;
			if (player != null) {
				playerEntity = player.getPlayerData().getPlayerEntity();
				playerData = player.getPlayerData();
				onlineRecharge = true;
			} else {
				List<PlayerEntity> playerEntities = DBManager.getInstance().query("from PlayerEntity where id = ?", playerId);
				if (playerEntities.size() > 0) {
					playerEntity = (PlayerEntity) playerEntities.get(0);
				} else {
					logger.info("YaYaShop failed player is not exist: " + msgInfo);
					builder.setResult(YAYA_SHOP_PLAYER_NOEXIT);
					builder.setMsg(java.net.URLDecoder.decode("用户不存在","utf-8"));
					return builder;
				}
				playerData = new PlayerData(null);
				playerData.setPlayerEntity(playerEntity);
			}
			// 实体对象不存在
			if (playerEntity == null) {
				logger.info("YaYaShop failed player entity is  null: " + msgInfo);
				builder.setResult(YAYA_SHOP_PLAYER_ENTITY_NULL);
				builder.setMsg(java.net.URLDecoder.decode("用户表为空","utf-8"));
				return builder;
			}

			int totalCost = msg.getTotalPrice();// 总共需要消耗的金豆数量
			if (totalCost > playerEntity.getGoldBean()) {
				logger.info("YaYaShop failed goldBean is not enough:" + msgInfo);
				builder.setResult(YAYA_SHOP_GOLD_NOT_ENOUGH);
				builder.setMsg(java.net.URLDecoder.decode("货币不足,是否前去购买","utf-8"));
				return builder;
			}

			if (onlineRecharge) {
				// 处理扣除金豆
				player.consumeGoldBean(totalCost, Action.BUY_YAYA_GOODS);
				// 客户端同步
				player.getPlayerData().syncYayaInfo();
			} else {
				playerEntity.setGoldBean(playerEntity.getGoldBean() - totalCost);
				playerEntity.notifyUpdate(false);
				BehaviorLogger.log4Service(playerEntity, Source.PLAYER_ATTR_CHANGE, Action.BUY_YAYA_GOODS, Params.valueOf("playerAttr", Const.playerAttr.GOLD_BEAN_VALUE), Params.valueOf("sub", totalCost), Params.valueOf("after", playerEntity.getGoldBean()));
			}
			builder.setResult(YAYA_SHOP_SUCCESS);
			builder.setBalance(playerEntity.getGoldBean());
			logger.info("YaYaShop successs:" + msgInfo);
		} catch (Exception e) {
			MyException.catchException(e);
			// 记录进YAYA日志
			logger.error(MyException.formatStackMsg(e));
		}
		return builder;
	}*/
}
