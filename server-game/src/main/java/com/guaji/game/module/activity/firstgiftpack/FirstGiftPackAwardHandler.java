package com.guaji.game.module.activity.firstgiftpack;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Activity.HPFirstRechargeGiftAwardRet;
import com.guaji.game.GsApp;
import com.guaji.game.config.FirstGiftPackCfg;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

public class FirstGiftPackAwardHandler implements IProtocolHandler {
	//1:已领取, 0:未领取   2:首充 3 未首充
		public static final int GOTED = 1, NOTGET = 0,FIRSTPAY = 1,NOFIRSTPAY = 0;

		@Override
		public boolean onProtocol(AppObj appObj, Protocol protocol) {
			
			Player player = (Player) appObj;
			RoleEntity mainRole = null;
			if(player != null){
				mainRole = player.getPlayerData().getMainRole();
			}
			//获取是否有过充值
			boolean isFirstRecharge = player.getPlayerData().getPlayerEntity().getRecharge() == 0 ? true : false;
			int roleId = mainRole.getItemId() > 3 ? mainRole.getItemId() - 3 : mainRole.getItemId();
			FirstGiftPackCfg cfg = ConfigManager.getInstance().getConfigByKey(FirstGiftPackCfg.class, roleId);
			if(cfg == null){
				player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
				return true;
			}
			//根据活动的ID获得该首充礼包活动
			FirstGiftPackStatus giftStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), Const.ActivityId.FIRST_GIFTPACK_VALUE, 0, FirstGiftPackStatus.class);
			//该活动时候已经结束或者领取
			if(giftStatus.getLastAwareTime() != null){
				// 奖励已领取
				player.sendError(protocol.getType(), Status.error.ACTIVITY_AWARDS_HAS_GOT);
				return true;
			}
			
			AwardItems awardItems = AwardItems.valueOf(cfg.getFirstgiftpack());//获取礼包
			awardItems.rewardTakeAffectAndPush(player, Action.FIRST_GIFTPACK_REWARD,2);//记录领取日志
			//设置新的属性值
			giftStatus.setGiftStatus(GOTED);
			if(isFirstRecharge == true){//用户充值单中没有过任何充值的记录
				giftStatus.setIsFirstPay(NOFIRSTPAY);
			}else{
				giftStatus.setIsFirstPay(FIRSTPAY);
			}
			
			giftStatus.setLastAwareTime(GuaJiTime.getCalendar().getTime());
			player.getPlayerData().updateActivity(Const.ActivityId.FIRST_GIFTPACK_VALUE, 0,true);
			//记录server及platform日志
			BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.FIRST_GIFTPACK_REWARD, Params.valueOf("content", cfg.getFirstgiftpack()));
			BehaviorLogger.log4Platform(player, Action.FIRST_GIFTPACK_REWARD);
			//创建新的领取对象并更改新的状态
			HPFirstRechargeGiftAwardRet.Builder builder = HPFirstRechargeGiftAwardRet.newBuilder();
			builder.setGiftStatus(giftStatus.getGiftStatus());//设置领取奖励的新状态
			//发送新的protocol协议信息<server端>
			player.sendProtocol(Protocol.valueOf(HP.code.FIRST_GIFTPACK_AWARD_S, builder));
			
			Msg msg = Msg.valueOf(GsConst.MsgType.FIRST_GIFT_GET_SUCCESS, player.getXid());
			GsApp.getInstance().postMsg(msg);
			
			return true;
		}


}
