package com.guaji.game.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.GuildSoulCfg;
import com.guaji.game.config.MottoCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.GuildBuffEntity;
import com.guaji.game.entity.MottoEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.GuildBuff.GuildBuffReq;
import com.guaji.game.protocol.GuildBuff.GuildBuffRes;
import com.guaji.game.protocol.GuildBuff.TalentBuffInfo;
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
public class PlayerGuildBuffModule extends PlayerModule {
 	static final int SYNC_GUILDBUFF = 0; // 同步
	static final int LEVELUP_GUILDBUFF = 1; // 升級
	static final int RESET_GUILDBUFF = 2; // 重置
	/**
	 * 
	 * 构造
	 * 
	 * @param player
	 */
	public PlayerGuildBuffModule(Player player) {
		super(player);
		listenProto(HP.code.Guild_Buff_C); 
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
		 if (protocol.checkType(HP.code.Guild_Buff_C)) {
			// 公會驗證
			if (!player.getPlayerData().isJoinGuild()) {
				player.sendError(protocol.getType(), Status.error.ALLIANCE_NO_JOIN);
				return false;
			}
			
			GuildBuffReq req = protocol.parseProtocol(GuildBuffReq.getDefaultInstance());
			int action = req.getAction();
			//long mottoId = req.getMottoId();
			// 业务分支处理
			switch (action) {
				case SYNC_GUILDBUFF:
					onSyncGuildBuffInfo(action);
				break;
				case LEVELUP_GUILDBUFF:
					onLevelupGuildBuff(protocol,action);
				break;
				case RESET_GUILDBUFF:
					onResetGuildBuff(protocol,action);
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
		
		// 加载状态数据
	 	GuildBuffEntity guildbuffEntity = player.getPlayerData().loadGuildBuffEntity();
	 	guildbuffEntity.resetAllBigTalent();
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
	 * 同步公會魔點資訊
	 * @param mottId
	 */
	private void onSyncGuildBuffInfo(int action,int... ids) {
		GuildBuffRes.Builder builder = GuildBuffRes.newBuilder();
		builder.setAction(action);
		boolean usefree = player.getPlayerData().getGuildBuffEntity().getFreeReset();
		builder.setFreeReset(usefree);
		Map<Integer,Integer> talentMap = player.getPlayerData().getGuildBuffEntity().getBuffMap();
		if (ids.length == 0) {
			for (Map.Entry<Integer,Integer> entry : talentMap.entrySet()) {
				TalentBuffInfo.Builder infobuilder = TalentBuffInfo.newBuilder();
				infobuilder.setId(entry.getKey());
				infobuilder.setLv(entry.getValue());
				builder.addTBInfo(infobuilder);
			}
		} else {
			for (Integer id : ids) {
				TalentBuffInfo.Builder infobuilder = TalentBuffInfo.newBuilder();
				infobuilder.setId(id);
				if (talentMap.containsKey(id)) {
					infobuilder.setLv(talentMap.get(id));
				} else {
					infobuilder.setLv(0);
				}
				builder.addTBInfo(infobuilder);
			}
		} 
		player.sendProtocol(Protocol.valueOf(HP.code.Guild_Buff_S_VALUE, builder));
	}
	/**
	 * 升級魔點小天賦
	 * @param id
	 */
	private void onLevelupGuildBuff(Protocol protocol,int action) {
		GuildBuffReq req = protocol.parseProtocol(GuildBuffReq.getDefaultInstance());
		 int id = req.getId();
		 if (id <= GsConst.GuildBuffConst.Type_Base){
			 sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			 return;
		 }
		 int prof = id / GsConst.GuildBuffConst.Type_Base;
		 if (((id % GsConst.GuildBuffConst.Type_Base) == 0) 
				 || ((id % GsConst.GuildBuffConst.Type_Base) > GsConst.GuildBuffConst.MaxTalentID)
				 || (prof > GsConst.GuildBuffConst.MaxProf)){
			 sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			 return;
		 }
		 
		 Map<Integer,Integer>talentMap = player.getPlayerData().getGuildBuffEntity().getBuffMap();
		 
		 int nowlv = 0;
		 
		if (talentMap.containsKey(id)) {
			nowlv = talentMap.get(id);
		}
		int targetlv = nowlv + 1;
		
		// 對應大天賦ID
		int BigTalentId = getBigTalentID(id);
		
		int BigTalentLv = 0;
		if (talentMap.containsKey(BigTalentId)) {
			BigTalentLv = talentMap.get(BigTalentId);
		}
		
		int LimitLv = (BigTalentLv + 1 ) *10;
		// 檢查等級限制
		if ((targetlv >= GsConst.GuildBuffConst.MaxTalentLV) || (targetlv >= LimitLv)) {
			 sendError(protocol.getType(), Status.error.TALENT_LEVEL_OVER_BOUND_VALUE);
			 return;
		}
		 
		GuildSoulCfg GSCfg = GuildSoulCfg.getGuildSoulCfg(id, targetlv);
		
		if (GSCfg == null) {
			 sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			 return;
		}
		
		String costItems = GSCfg.getCost();
		
		if (costItems.isEmpty()) {
			player.sendError(protocol.getType(),Status.error.ITEM_NOT_FOUND_VALUE); 
			return;
		}
		
		ConsumeItems consumeItems = ConsumeItems.valueOf();
	
		List<ItemInfo> itemList = ItemInfo.valueListOf(costItems);
		
		boolean isAdd = consumeItems.addConsumeInfo(player.getPlayerData(),itemList);
		
		if (isAdd && consumeItems.checkConsume(player)) {
			if(!consumeItems.consumeTakeAffect(player, Action.GUILD_SOUL_LEVELUP)) {
				player.sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
				return; 
			} 
		} else {
			player.sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
			return; 
		}
		
		player.getPlayerData().getGuildBuffEntity().setBuffLv(id, targetlv);
		
		player.getPlayerData().getGuildBuffEntity().calBigTalent(prof);
		
		player.getPlayerData().getGuildBuffEntity().notifyUpdate(true);
		
		onSyncGuildBuffInfo(action,id,BigTalentId);
		
		// 刷新英雄属性
		List<RoleEntity> herolist = player.getPlayerData().getHero();
		for (RoleEntity ahero : herolist) {
			if (ahero.getProfession() == prof) {
				PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), ahero);
				player.getPlayerData().syncRoleInfo(ahero.getId());
			}
		}
		
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.GUILD_SOUL_LEVELUP,Params.valueOf("prof", prof),
				Params.valueOf("id", id),
				Params.valueOf("nowlv", nowlv),
				Params.valueOf("LimitLv", LimitLv),
				Params.valueOf("targetlv", targetlv),
				Params.valueOf("BigTalentLv",player.getPlayerData().getGuildBuffEntity().getBuffMap().get(BigTalentId)));
		 
	}
	
	/**
	 * 重置該職業魔典天賦
	 * @param mottId
	 */
	private void onResetGuildBuff(Protocol protocol,int action) {
		GuildBuffReq req = protocol.parseProtocol(GuildBuffReq.getDefaultInstance());
		 int prof = req.getId();
		 
		 if ((prof <= 0) || (prof > GsConst.GuildBuffConst.MaxProf)) {
			 sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			 return;
		 }
		 
		 Map<Integer,Integer>talentMap = player.getPlayerData().getGuildBuffEntity().getBuffMap();
		 
		 boolean needCost = player.getPlayerData().getGuildBuffEntity().getFreeReset();
			
		 if (needCost) {
		 	String costItems = SysBasicCfg.getInstance().getReSetGuildSoulItem();
			
			if (costItems.isEmpty()) {
				player.sendError(protocol.getType(),Status.error.ITEM_NOT_FOUND_VALUE); 
				return;
			}
			
			ConsumeItems consumeItems = ConsumeItems.valueOf();

			List<ItemInfo> itemList = ItemInfo.valueListOf(costItems);
 
			boolean isAdd = consumeItems.addConsumeInfo(player.getPlayerData(),itemList);
			
			if (isAdd && consumeItems.checkConsume(player)) {
				if(!consumeItems.consumeTakeAffect(player, Action.GUILD_SOUL_RESET)) {
					player.sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
					return; 
				} 
			} else {
				player.sendError(protocol.getType(),Status.error.ITEM_NOT_ENOUGH_VALUE); 
				return; 
			}
		 } else {
			 player.getPlayerData().getGuildBuffEntity().setFreeReset(true);
		 }
		 
		// 對應大天賦ID
		int BigTalentId = prof *GsConst.GuildBuffConst.Type_Base;
		
		int talentId = 0;
		String retItem = "";
		int talentLv = 0;
		// 大天賦沒有退回物所以從1開始
		for (int i = 1 ; i <= GsConst.GuildBuffConst.MaxTalentID ; i++) {
			talentId = BigTalentId + i;
			if (talentMap.containsKey(talentId)) {
				talentLv = talentMap.get(talentId);
				GuildSoulCfg GSCfg = GuildSoulCfg.getGuildSoulCfg(talentId, talentLv);
				if (GSCfg == null) {
					sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
					return;
				}
	 			if (retItem.isEmpty()) {
	 				retItem = GSCfg.getCost();
	 			} else {
	 				retItem = retItem+","+ GSCfg.getCost();
	 			}
			}
		}
		// 重置所有天賦
		List<Integer> allList = new ArrayList<>();
		for (int i = 0 ; i <= GsConst.GuildBuffConst.MaxTalentID ; i++) {
			talentId = BigTalentId + i;
			player.getPlayerData().getGuildBuffEntity().setBuffLv(talentId, 0);
			allList.add(talentId);// 紀錄需要重整天賦
			
		}
		
		int [] array = new int [allList.size()];
		
		player.getPlayerData().getGuildBuffEntity().notifyUpdate(true);

		onSyncGuildBuffInfo(action,array);
		// 刷新英雄属性
		List<RoleEntity> herolist = player.getPlayerData().getHero();
		for (RoleEntity ahero : herolist) {
			if (ahero.getProfession() == prof) {
				PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), ahero);
				player.getPlayerData().syncRoleInfo(ahero.getId());
			}
		}
		
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.GUILD_SOUL_RESET,Params.valueOf("prof", prof),
				Params.valueOf("needCost", needCost),
				Params.valueOf("retItem", retItem));
	}
	
	public int getBigTalentID(int id) {
		return (id / GsConst.GuildBuffConst.Type_Base) *GsConst.GuildBuffConst.Type_Base;
	}

}
