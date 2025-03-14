package com.guaji.game.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.MottoCfg;
import com.guaji.game.entity.MottoEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Motto.MottoRequest;
import com.guaji.game.protocol.Motto.MottoRespones;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;

/**
 * 箴言系統模塊
 * 
 * @author hawk
 */
public class PlayerMottoModule extends PlayerModule {
	static final int SYNC_MOTTO = 0; // 同步
	static final int MOTTO_ACTIVATION = 1; // 激活
	static final int MOTTO_STARUP = 2; // 升星
	/**
	 * 
	 * 构造
	 * 
	 * @param player
	 */
	public PlayerMottoModule(Player player) {
		super(player);
		listenProto(HP.code.MOTTO_SYSTEM_C); 
	}

	/**
	 * 更新
	 * 
	 * @return
	 */
	@Override
	public boolean onTick() {
		return super.onTick();
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
		if (protocol.checkType(HP.code.MOTTO_SYSTEM_C)) {
			MottoRequest req = protocol.parseProtocol(MottoRequest.getDefaultInstance());
			int action = req.getAction();
			long mottoId = req.getMottoId();
			// 业务分支处理
			switch (action) {
			case SYNC_MOTTO:
				onSyncMottoInfo(action);
				break;
			case MOTTO_ACTIVATION:
				onMottoActivation(protocol,action,mottoId);
				break;
			case MOTTO_STARUP:
				onMottoStarUp(protocol,action,mottoId);
				break;
			default:
				player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				break;
			}
			return true;
		}
		return super.onProtocol(protocol);
	}
	/**
	 * 玩家上线处理（派发完成模块加载）
	 * 
	 * @return
	 */
	@Override
	protected boolean onPlayerLogin() {
		
		List<MottoEntity> mottoEntities = player.getPlayerData().loadMottoEntities();
		Map<Integer, MottoCfg> mottoCfgMap = MottoCfg.getMottoInfoMap();
		
		Map<Integer, Integer> mottoCacheMap = new HashMap<>();
		if (mottoEntities.size() != mottoCfgMap.size()) {
			for (MottoEntity entity : mottoEntities) {
				mottoCacheMap.put(entity.getItemId(), entity.getItemId());
			}
			
			// 對比配置是否新增新箴言
			for (Entry<Integer, MottoCfg> c : mottoCfgMap.entrySet()) {
				if (!mottoCacheMap.containsKey(c.getKey())) {
					player.getPlayerData().createMotto(c.getKey());
				}
			}
		}
		return true;
	}

	/**
	 * 组装完成
	 */
	@Override
	protected boolean onPlayerAssemble() {
		return true;
	}

	/**
	 * 玩家下线处理
	 * 
	 * @return
	 */
	@Override
	protected boolean onPlayerLogout() {
		return true;
	}
	/**
	 * 同步箴言資訊
	 * @param mottId
	 */
	private void onSyncMottoInfo(int action,Long... ids) {
		MottoRespones.Builder builder = MottoRespones.newBuilder();
		builder.setAction(action);
		builder.setSyncInfo(player.getPlayerData().genMottoSynInfo(ids));
		player.sendProtocol(Protocol.valueOf(HP.code.MOTTO_SYSTEM_S_VALUE, builder));
	}
	/**
	 * 激活箴言
	 * @param mottId
	 */
	private void onMottoActivation(Protocol protocol,int action,Long mottId) {
		MottoEntity mottoEntity =  player.getPlayerData().getMottoById(mottId);
		if (mottoEntity == null) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		if (mottoEntity.getStar() > 0) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		int itemId = mottoEntity.getItemId();
		
		MottoCfg cfg = ConfigManager.getInstance().getConfigByKey(MottoCfg.class, itemId);
		
		if (cfg == null) {
			sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return;
		}
		
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		ItemInfo itemInfo = new ItemInfo(Const.itemType.TOOL_VALUE * GsConst.ITEM_TYPE_BASE,itemId,cfg.getActivation());
		
		List<ItemInfo> itemList = new ArrayList<ItemInfo>();
		itemList.add(itemInfo);
		boolean isAdd = false ;
		isAdd = consumeItems.addConsumeInfo(player.getPlayerData(),itemList);
		
		if (isAdd && consumeItems.checkConsume(player)) {
			if(!consumeItems.consumeTakeAffect(player, Action.ACTIVATION_MOTTO)) {
				sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
				return; 
			} 
		} else {
			sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
			return; 
		}
		
		mottoEntity.setStar(1);
		mottoEntity.notifyUpdate(false);
		
		onSyncMottoInfo(action,mottId);
		
		// 刷新英雄属性
		List<RoleEntity> herolist = player.getPlayerData().getHero();
		for (RoleEntity ahero : herolist) {
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), ahero);
		}
		player.getPlayerData().syncRoleInfo(0);
		
	}
	
	/**
	 * 升級箴言
	 * @param mottId
	 */
	private void onMottoStarUp(Protocol protocol,int action,Long mottId) {
		MottoEntity mottoEntity =  player.getPlayerData().getMottoById(mottId);
		if (mottoEntity == null) {
			sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		if (mottoEntity.getStar() <= 0) { // 未激活
			sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		int itemId = mottoEntity.getItemId();
		
		MottoCfg cfg = ConfigManager.getInstance().getConfigByKey(MottoCfg.class, itemId);
		
		if (cfg == null) {
			sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return;
		}
		
		int star = mottoEntity.getStar();
		
		if (star >=  cfg.getMaxStar()) { // 未激活
			sendError(protocol.getType(), Status.error.ITEM_LEVEL_UP_TARGET_EMPTY);
			return;
		}
		
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		ItemInfo itemInfo = new ItemInfo(Const.itemType.TOOL_VALUE * GsConst.ITEM_TYPE_BASE,itemId,cfg.getCostByStar(star));
		
		List<ItemInfo> itemList = new ArrayList<ItemInfo>();
		itemList.add(itemInfo);
		boolean isAdd = false ;
		isAdd = consumeItems.addConsumeInfo(player.getPlayerData(),itemList);
		
		if (isAdd && consumeItems.checkConsume(player)) {
			if(!consumeItems.consumeTakeAffect(player, Action.STAR_UP_MOTTO)) {
				sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
				return; 
			} 
		} else {
			sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
			return; 
		}
		
		mottoEntity.setStar(star+1);
		mottoEntity.notifyUpdate(false);
		
		onSyncMottoInfo(action,mottId);
		
		// 刷新英雄属性
		List<RoleEntity> herolist = player.getPlayerData().getHero();
		for (RoleEntity ahero : herolist) {
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), ahero);
		}
		player.getPlayerData().syncRoleInfo(0);
	}

}
