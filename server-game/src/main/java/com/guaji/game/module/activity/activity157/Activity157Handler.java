package com.guaji.game.module.activity.activity157;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity5.LoginTenDrawRequest;
import com.guaji.game.protocol.Activity5.LoginTenDrawResponse;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;

/*** @author 作者 Ting Lin
* @version 创建时间：2023年7月4日
* 类说明
*/
public class Activity157Handler implements IProtocolHandler{
	static final int SYNC_INFO = 0; // 同步
	static final int Get_DRAW = 1; // 領十抽
	
	@Override 
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY157_LOGIN_TEN_DRAW_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		LoginTenDrawRequest req = protocol.parseProtocol(LoginTenDrawRequest.getDefaultInstance());
		int action = req.getAction();
	
		// 获取活动数据
		int stageId = timeCfg.getStageId();
		Activity157Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity157Status.class);
		
		if (null == status) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}
					
		// 业务分支处理
		switch (action) {
		case SYNC_INFO:
			SyncInfo(action, player, status);
			break;
		case Get_DRAW:
			getDraw(action,player,status,timeCfg);
			break;
		default:
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			break;
		}
		return true;
	}
	
	/**
	 * 同步資訊
	 * @param action
	 * @param player
	 * @param timeCfg
	 * @param status
	 */
	private static void SyncInfo(int action,Player player, Activity157Status status) {
		LoginTenDrawResponse.Builder builder = generateInfo(action,player,status);
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY154_S_VALUE, builder));
	}
	/**
	 * 抽獎
	 * @param action 1.領十抽 
	 * @param type
	 * @param player
	 * @param status
	 */
	static  void getDraw(int action,Player player,Activity157Status status,ActivityTimeCfg timeCfg) {
						
		if (status.getIsGot()) {
			player.sendError(HP.code.ACTIVITY157_C_VALUE,Status.error.AWARD_ALREADY_GOT_ERROR); 
			return;
		}
		
		int oldTimes = status.getTimes();
		if (oldTimes >= SysBasicCfg.getInstance().getLoginTenDrawDays()) {
			player.sendError(HP.code.ACTIVITY157_C_VALUE,Status.error.ALREADY_GOT_LIMIT_ERROR); 
			return;
		}
		
		int activityId = Const.ActivityId.ACTIVITY157_LOGIN_TEN_DRAW_VALUE;
		String awradStr = SysBasicCfg.getInstance().getLoginTenDrawAward();;
		AwardItems nowawardItems = new AwardItems();
		status.incTimes(1);
		ItemInfo nowitemInfos = ItemInfo.valueOf(awradStr);
		nowawardItems.addItem(nowitemInfos);
		nowawardItems.rewardTakeAffectAndPush(player, Action.ACTIVITY157_LOGIN_TEN_DRAW, 0);
		
		// BI 日志 ()
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.ACTIVITY157_LOGIN_TEN_DRAW, Params.valueOf("action", action),
				Params.valueOf("oldTimes", oldTimes),
				Params.valueOf("Times", status.getTimes()),
				Params.valueOf("awardItems", nowawardItems.toDbString()));
		
		player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
		
		LoginTenDrawResponse.Builder builder = generateInfo(action,player,status);

		builder.setReward(nowawardItems.toString());
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY157_S_VALUE, builder));
		
	}
	
	/**
	 * 取得獎池協定資訊
	 * @param action
	 * @param player
	 * @param kind
	 * @param status
	 * @param activityTimeCfg
	 * @return
	 */
	public static LoginTenDrawResponse.Builder generateInfo(int action,Player player,Activity157Status status) {
		
		LoginTenDrawResponse.Builder builder = LoginTenDrawResponse.newBuilder();
				
		builder.setAction(action);
		builder.setIsGot(status.getIsGot());
		builder.setTimes(status.getTimes());
		
		return builder;
	}
	/**
	 * 重置活動領取狀態
	 * @param player
	 */
	
	public static void restGot(Player player) {
        try {
        	
            if (player == null || player.getPlayerData() == null) {
                return;
            }
            
            int activityId = Const.ActivityId.ACTIVITY157_LOGIN_TEN_DRAW_VALUE;

            ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
            if (activityTimeCfg == null) {
                // 活动已关闭
                return;
            }
            
            Activity157Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, activityTimeCfg.getStageId(), Activity157Status.class);

            // 每日重置已領取狀態
            status.setIsGot(false);
            
            player.getPlayerData().updateActivity(activityId, activityTimeCfg.getStageId());

        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
