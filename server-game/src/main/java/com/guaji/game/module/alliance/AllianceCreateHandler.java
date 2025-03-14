package com.guaji.game.module.alliance;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Alliance.HPAllianceCreateC;
import com.guaji.game.protocol.Alliance.HPAllianceEnterS;
import com.guaji.game.protocol.Const.AllianCheckButton;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.config.AllianceCfg;
import com.guaji.game.config.GvgCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.GvgTimeEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.ChatManager;
import com.guaji.game.manager.gvg.GvgService;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.player.Player;
import com.guaji.game.util.AdjustEventUtil;
import com.guaji.game.util.AllianceUtil;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsConst.AdjustActionType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 创建公会
 */
public class AllianceCreateHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		if(player.getLevel() < SysBasicCfg.getInstance().getAllianceCreateLevel()){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_FUN_LEVEL_ERROR);
			return true;
		}
		if(player.getVipLevel()<SysBasicCfg.getInstance().getAllianceCreateVipLevel()){
			player.sendError(protocol.getType(), Status.error.VIP_NOT_ENOUGH_VALUE);
			return true;
		}
		if(player.getGold() < SysBasicCfg.getInstance().getAllianceCreateGold()){
			player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH);
			return true;
		}
		if(player.getPlayerData().getPlayerAllianceEntity().getAllianceId()!=0){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_CREATE_ERROR);
			return true;
		}

		HPAllianceCreateC par = protocol.parseProtocol(HPAllianceCreateC.getDefaultInstance());
		String name = par.getName();
		if(name == null){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NAME_NULL);
			return true;
		}
		name = name.trim();
		if(name.length()<=0){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NAME_NULL);
			return true;
		}
		
		if(!AllianceUtil.checkName(name)){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_CREATE_NAME_ERROR);
			return true;
		}
		
		if(AllianceManager.getInstance().getExistName().contains(name)){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NAME_EXIST_ERROR);
			return true;
		}
			
		AllianceCfg allianceCfg = ConfigManager.getInstance().getConfigByKey(AllianceCfg.class, 1);
		if(allianceCfg == null){
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}
		AllianceEntity allianceEntity = new AllianceEntity();
		//多线程hashmap没有转currenthashmap add by callan
		allianceEntity.init();
		allianceEntity.setPlayerId(player.getId());
		allianceEntity.setPlayerName(player.getName());
		allianceEntity.setExp(0);
		allianceEntity.setLevel(allianceCfg.getLevel());
		allianceEntity.setName(name);
		allianceEntity.setCreateAllianceTime(System.currentTimeMillis());
		allianceEntity.setBossOpen(false);
		allianceEntity.setBossId(allianceCfg.getBossId());
		allianceEntity.getMemberList().add(player.getId());
		allianceEntity.setHasCheckLeaderMail(AllianCheckButton.CHECK_BUTTON_TYPE_1_VALUE); //默认新创建的公会给会长发邮件同意按钮不进行勾选
		DBManager.getInstance().create(allianceEntity);
		//标记为会长
		player.getPlayerData().getPlayerAllianceEntity().setPostion(GsConst.Alliance.ALLIANCE_POS_MAIN);
		player.getPlayerData().getPlayerAllianceEntity().setAllianceId(allianceEntity.getId());
		player.getPlayerData().getPlayerAllianceEntity().notifyUpdate(true);
		// 推送加入公会任务
		QuestEventBus.fireQuestEventOneTime(QuestEventType.JOIN_ALLIANCE, player.getXid());
		// 从db创建
		AllianceManager.getInstance().addAlliance(allianceEntity);
		AllianceManager.getInstance().getExistName().add(name);
		ConsumeItems.valueOf(changeType.CHANGE_GOLD, SysBasicCfg.getInstance().getAllianceCreateGold()).consumeTakeAffect(player, Action.ALLIANCE_CREATE_CONSUME);
		PlayerAllianceEntity myPlayerAllianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		if(myPlayerAllianceEntity.getAllianceId() != 0){
			ChatManager.getInstance().addAllianceSession(player.getSession(), myPlayerAllianceEntity.getAllianceId(), player.getId());
		}
		GameUtil.sendAllianceChatTag(player);
		player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_CREATE_S_VALUE, AllianceManager.getInstance().getAllianceInfo(allianceEntity, player.getId(), player.getGold())));
		AllianceManager.getInstance().sendSelfData(HPAllianceEnterS.newBuilder(), player, allianceEntity);
		// 刷新快照
		player.getPlayerData().refreshOnlinePlayerSnapshot();
		
		// adjust 加入公会
		AdjustEventUtil.sentAdjustEventInfo(player,AdjustActionType.GUILDJOIN,0);
		
		//公会数量达到目标公会数量 
		/*
		if (AllianceManager.getInstance().getAllianceMap().size() >= GvgCfg.getInstance().getAllianceSize()) {
			if (GvgService.getInstance().isOpeanFunction()) {
				return true;
			}
			
			GvgTimeEntity timeEntity = GvgTimeEntity.createEntity();
			GvgService.getInstance().setTimeEntity(timeEntity);
		}
		*/
		
		return true;
	}
}
