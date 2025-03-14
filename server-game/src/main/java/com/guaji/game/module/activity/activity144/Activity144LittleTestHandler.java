package com.guaji.game.module.activity.activity144;

import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity3.Activity144Little_testReq;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.LittleTestQuestionCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Status;

/**
 * 万圣节活动抽奖
 */
public class Activity144LittleTestHandler implements IProtocolHandler {

	private static final int OPERATE_SYNC = 0;// 同步
	private static final int OPERATE_ANSWERS = 1;  	// 回答
	private static final int OPERATE_ACCOUNT = 2;	// 結算
	private static final int OPERATE_Get_REWARD = 3;// 領獎
	private static final int OPERATE_CreateGame = 4;// 開局初始
//	private static final int OPERATE_ClearGame = 5; // 測試清除用


	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {

		// 检测活动是否开放
		Player player = (Player) appObj;
		int activityId = Const.ActivityId.ACTIVITY144_LITTLE_TEST_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		if (timeCfg.isActiveToEnd()) {
			if (LittleTestQuestionCfg.getQuestionList().size() == 0) {
				LittleTestQuestionCfg.RandomQuestion();
			}
		}
		// 获取活动数据
		int stageId = timeCfg.getStageId();
		Activity144Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity144Status.class);
		if (null == status) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND);
			return true;
		}

		// 解析请求参数
		Activity144Little_testReq request = protocol.parseProtocol(Activity144Little_testReq.getDefaultInstance());
		int type = request.getType();
		int ans = request.getAnswers();
		List<Integer> RoleAry = request.getRoleidList();

		// 业务分支处理
		switch (type) {
		case OPERATE_SYNC:
			Activity144LittleTestManager.sync(player, timeCfg, status);
			break;
		case OPERATE_CreateGame:
			Activity144LittleTestManager.crategame(player, timeCfg, status,RoleAry);
			break;
		case OPERATE_ANSWERS:
			Activity144LittleTestManager.answers(player, timeCfg, status,ans);
			break;
		case OPERATE_ACCOUNT:
			Activity144LittleTestManager.AccountGame(player, timeCfg, status);
			break;
		case OPERATE_Get_REWARD:
			Activity144LittleTestManager.takegift(player, timeCfg, status);
			break;
//		case OPERATE_ClearGame:
//			Activity144LittleTestManager.cleargame(player, timeCfg, status);
//			break;
		default:
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			break;
		}
		return true;
	}

}
