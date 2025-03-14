package com.guaji.game.module.alliance;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.AllianceDonateCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Alliance.HPAllianceDonateInfoResp;
import com.guaji.game.protocol.Alliance.HPAllianceDonateReq;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
/**
 * 联盟捐献
 */
public class AllianceDonateHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		HPAllianceDonateReq req = protocol.parseProtocol(HPAllianceDonateReq.getDefaultInstance());
		int donateId = req.getDonateId();
		List<AllianceDonateCfg> cfgs = ConfigManager.getInstance().getConfigList(AllianceDonateCfg.class);
		AllianceDonateCfg donateCfg = null;
		for(AllianceDonateCfg cfg:cfgs){
			if(cfg.getId()==donateId){
				donateCfg = cfg;
				break;
			}
		}
		if(donateCfg==null){
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return true;
		}
		
		// 玩家公会实体
		PlayerAllianceEntity playerAllianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		int allianceId = playerAllianceEntity.getAllianceId(); 
		if(allianceId == 0){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NO_JOIN);
			return true;
		}
		// 公会实体
		AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
		if(allianceEntity == null){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NONEXISTENT);
			return true;
		}
		
		int activeValue = allianceEntity.getActiveValue();
		//today day id
		int todayDayID = GuaJiTime.getDayIDBase2000();
		//联盟活跃度
		//捐献条目
		Map<Integer,Integer> map = playerAllianceEntity.getDailyDonateMap();
		//今日是否捐献过
		int todayDonateId = -1;
		if(map!=null&&map.size()==0){
			Set<Integer> keySet = map.keySet();
			for(Integer key:keySet){
				int dayId = map.get(key);
				if(dayId!=todayDayID){
					continue;
				}
				todayDonateId = key;
				break;
			}
		}
		//已经捐献过了，不能再捐献
		if(todayDonateId>0){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_HAD_DONATED_VALUE);
			return true;
		}
		
		List<ItemInfo> consumeInfo = donateCfg.getConsumeItem();
		if(consumeInfo!=null&&consumeInfo.size()>0){
			ConsumeItems consumeItems = new ConsumeItems();
			if (!consumeItems.addConsumeInfo(player.getPlayerData(), consumeInfo)) {
				player.sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH_VALUE);
				return true;
			}
			// 消耗数据校验
			if (!consumeItems.checkConsume(player, HP.code.EQUIP_BUILD_EVENT_C_VALUE)) {
				return true;
			}
			// 扣除消耗数据
			consumeItems.consumeTakeAffect(player, Action.ALLIANCE_DONATE);
		}
		List<ItemInfo> allianceAward = donateCfg.getAllianceAwardItem();
		List<ItemInfo> personAward = donateCfg.getPersonAwardItem();
		
		AwardItems items = new AwardItems();
		items.addItemInfos(allianceAward);
		items.rewardTakeAffect(player, Action.ALLIANCE_DONATE_AWARD);
		
		AwardItems personItems = new AwardItems();
		personItems.addItemInfos(personAward);
		personItems.rewardTakeAffect(player, Action.ALLIANCE_DONATE_PERSON_AWARD);
		
		//将消息发送给客户端
		HPAllianceDonateInfoResp.Builder builder = AllianceManager.getInstance().buildAllianceDonateInfo(activeValue, todayDonateId);
		//发送给客户端
		player.sendProtocol(protocol);
		return false;
	}

}
