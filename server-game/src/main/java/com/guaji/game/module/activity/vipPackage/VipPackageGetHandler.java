package com.guaji.game.module.activity.vipPackage;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity2.GetVipPackageAward;
import com.guaji.game.protocol.Activity2.HPGetVipPackageAward;
import com.guaji.game.config.VipPackageCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class VipPackageGetHandler implements IProtocolHandler{
	
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		
		Player player = (Player) appObj;
		
		VipPackageStatus vipPackageStatus = ActivityUtil.getVipPackageStatus(player.getPlayerData());
		
		if(vipPackageStatus == null) 
		{
			return true;
		}
		
		GetVipPackageAward request = protocol.parseProtocol(GetVipPackageAward.getDefaultInstance());
		
		int id = request.getVipPackageId();
				
		VipPackageCfg cfg = ConfigManager.getInstance().getConfigByKey(VipPackageCfg.class, id);
		
		if(cfg == null){
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
			return true;
		}
		
		if(cfg.getVipLimit()>player.getVipLevel())
		{
			player.sendError(protocol.getType(), Status.error.VIP_NOT_ENOUGH);
			return true;
		}
		
		if(!vipPackageStatus.getPacket(id))
		{
			player.sendError(protocol.getType(), Status.error.VIP_PACKET_GET_AWARD_VALUE);
			return true;
		}
		
		int goldCost = cfg.getPrice();
		
		if (player.getGold() < goldCost) 
		{
			player.sendError(protocol.getType(), Status.error.GOLD_NOT_ENOUGH_VALUE);
			return true;
		}

		player.consumeGold(goldCost, Action.VIP_PACKAGE_REWARD);
		ConsumeItems.valueOf(changeType.CHANGE_GOLD, goldCost).pushChange(player);
	
		AwardItems awardItems = AwardItems.valueOf(cfg.getAwardStr());//获取礼包
		awardItems.rewardTakeAffectAndPush(player, Action.VIP_PACKAGE_REWARD,2);//记录领取日志
		
		player.getPlayerData().updateActivity(Const.ActivityId.VIP_PACKAGE_VALUE, 0,true);
		
		//获取首充礼包的实体信息并设置新的领取状态
		HPGetVipPackageAward.Builder builder = HPGetVipPackageAward.newBuilder();
		builder.setVipPackageId(id);//礼包id
		builder.setGetTime(vipPackageStatus.getPacketGetTime(id));//礼包领取时间（状态）
		//发送服务器礼包协议
		player.sendProtocol(Protocol.valueOf(HP.code.VIP_PACKETAGE_GET_AWARD_S, builder));

		return true;
	}

}
