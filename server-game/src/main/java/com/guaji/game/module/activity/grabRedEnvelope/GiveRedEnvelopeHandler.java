package com.guaji.game.module.activity.grabRedEnvelope;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.HPGiveRedEnvelope;
import com.guaji.game.protocol.Activity2.HPGiveRedEnvelopeRet;
import com.guaji.game.GsApp;
import com.guaji.game.ServerData;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.ArenaEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.AwardItems.Item;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.manager.ArenaManager;
import com.guaji.game.manager.ChatManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class GiveRedEnvelopeHandler implements IProtocolHandler {
	/**
	 * 本服所有发放红包的玩家和祝福语
	 */
	public static Map<Integer, String> giveRedEnvelopeMap = new ConcurrentHashMap<Integer, String>();
	
	/**
	 * 随机取一个发放红包的玩家
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map.Entry<Integer, String> randomOneGiveRedEnvelopePlayerWishes(){
		RedEnvelopeServerStatus serverStatus = ServerData.getInstance().getServerStatus(
				GsConst.ServerStatusId.RED_ENVELOPE, RedEnvelopeServerStatus.class);
		if(giveRedEnvelopeMap.size() == 0 && serverStatus.getServerRedEnvelopeAmount() > 0){
			// 导入假数据
			List<ArenaEntity> arenaEntities = ArenaManager.getInstance().getTopNArenaData(10);
			for(ArenaEntity arenaEntity : arenaEntities){
				if(arenaEntity != null){
					giveRedEnvelopeMap.put(arenaEntity.getPlayerId(), "null");
				}
			}
		}
		
		if(giveRedEnvelopeMap.size() == 0 ){
			return null;
		}
		
		Object[] entrys = giveRedEnvelopeMap.entrySet().toArray();
		Random random = new Random();
		Object entry = entrys[random.nextInt(entrys.length)];
		return ((Map.Entry<Integer, String>) entry);
	}
	
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.GRAB_RED_ENVELOPE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if(timeCfg == null){
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		HPGiveRedEnvelope req = protocol.parseProtocol(HPGiveRedEnvelope.getDefaultInstance());
		String wishes = req.getWishes();
		
		RedEnvelopeStatus personalStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), 
				activityId, timeCfg.getStageId(), RedEnvelopeStatus.class);
		if(personalStatus.getMyRedEnvelope() <= 0){
			// 你已经没有红包了
			player.sendError(protocol.getType(), Status.error.NO_RED_ENVELOPE);
			return true;
		}
		int realRedEnvelopeNum = personalStatus.giveRedEnvelope(SysBasicCfg.getInstance().getGiveRedEnvelopeAmount());
		// 聊天广播
		String chatMsg = ChatManager.getMsgJson(SysBasicCfg.getInstance().getGiveRedEnvelopeTxtFmt(), player.getName(), wishes);
		GsApp.getInstance().broadcastChatWorldMsg("", chatMsg);
		
		RedEnvelopeServerStatus serverStatus = ServerData.getInstance().getServerStatus(
				GsConst.ServerStatusId.RED_ENVELOPE, RedEnvelopeServerStatus.class);
		if(serverStatus.getStageId() != timeCfg.getStageId()){
			serverStatus.reset(timeCfg.getStageId());
		}
		
		// 发红包固定奖励
		AwardItems awardItems = AwardItems.valueOf(SysBasicCfg.getInstance().getGiveRredEnvelopeAwards());
		List<Item> items = awardItems.getAwardItems();
		for(Item item : items){
			item.count *= realRedEnvelopeNum;
		}
		awardItems.rewardTakeAffectAndPush(player, Action.GIVE_RED_ENVELOPE,1);
		
		HPGiveRedEnvelopeRet.Builder ret = HPGiveRedEnvelopeRet.newBuilder();
		ret.setLeftTimes(timeCfg.calcActivitySurplusTime());
		ret.setMyRedEnvelope(personalStatus.getMyRedEnvelope());
		ret.setServerRedEnvelope(serverStatus.getServerRedEnvelopeAmount());
		player.sendProtocol(Protocol.valueOf(HP.code.GIVE_RED_ENVELOPE_S, ret));
		
		giveRedEnvelopeMap.put(player.getId(), wishes);
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		ServerData.getInstance().updateServerData(GsConst.ServerStatusId.RED_ENVELOPE);
		
		BehaviorLogger.log4Platform(player, Action.GIVE_RED_ENVELOPE, Params.valueOf("grantAmount", realRedEnvelopeNum));
		return true;
	}
}
