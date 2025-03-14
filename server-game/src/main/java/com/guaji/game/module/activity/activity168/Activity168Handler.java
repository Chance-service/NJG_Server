package com.guaji.game.module.activity.activity168;

import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity5.SubScriptionReq;
import com.guaji.game.protocol.Activity5.SubScriptionResp;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;

/**
 * 特權領取協定
 */
public class Activity168Handler implements IProtocolHandler {
	static final int Sync = 0;
	//static final int Get_Rward = 1;

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		// TODO Auto-generated method stub

		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY168_SubScription_VALUE;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeConfig == null || player == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		// 解析接受到的数据
		SubScriptionReq request = protocol.parseProtocol(SubScriptionReq.getDefaultInstance());

		int action = request.getAction();
				
		// 数据人错误
		if ( action != 0) {
			// 无效参数
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return true;
		}
				
		int stageId = timeConfig.getStageId();
		Activity168Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity168Status.class);
		
		if (status == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		
		// 业务分支处理
		switch (action) {
		case Sync: // 只有同步由Recharage購買
			SyncInfo(action,player,status);
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
	private static void SyncInfo(int action,Player player, Activity168Status status) {
		SubScriptionResp.Builder builder = getBuilder(action,status);
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY168_SUBSCRIPTION_S, builder));
	}
	
	private static SubScriptionResp.Builder getBuilder(int action,Activity168Status status) {
		// 返回包
		SubScriptionResp.Builder response = SubScriptionResp.newBuilder();
		
		response.setAction(action);
		for(Map.Entry<Integer,Integer> entry : status.getActivateId().entrySet() ) {
			response.addActivateId(entry.getKey());
			response.addTimes(entry.getValue());
		} 
		
		return response;
	}

}
