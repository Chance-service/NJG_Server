package com.guaji.game.module.activity.activity166;

import java.util.ArrayList;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.GachaListCfg;
import com.guaji.game.config.PackBoxCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.DropItems.Item;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity5.Activity166Draw;
import com.guaji.game.protocol.Activity5.Activity166Info;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.TapDBUtil.TapDBSource;

/*** @author 作者 Ting Lin
* @version 创建时间：2023年10月18日
* 类说明
*/
public class Activity166Handler implements IProtocolHandler{
	static final int TIMES_TYPE_SINGLE = 1;
	static final int TIMES_TYPE_TEN = 10;

	@Override // RELEASE_UR_INFO_C
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY166_CALL_OF_FRIENDSHIP_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		Activity166Draw req = protocol.parseProtocol(Activity166Draw.getDefaultInstance());
		int searchTimes = req.getTimes();

		if (searchTimes != TIMES_TYPE_SINGLE && searchTimes != TIMES_TYPE_TEN) {
			// 活动参数错误
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID);
			return true;
		}
		
		GachaListCfg gachaCfg = ConfigManager.getInstance().getConfigByKey(GachaListCfg.class, activityId);
		
		if (gachaCfg == null) {
			player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return true;
		}
		
		//Activity166Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, timeCfg.getStageId(), Activity166Status.class);

		int singleCost = SysBasicCfg.getInstance().getCallOfFriendshipSingleCost();
		int tenCost = SysBasicCfg.getInstance().getCallOfFriendshipTenCost();
		
		// 计算实际花费友情點數
		int payPoint = (searchTimes == TIMES_TYPE_TEN)? tenCost : singleCost;

		if (payPoint > player.getFriendship()) {
			// 友情點不足
			player.sendError(protocol.getType(), Status.error.FRIENDSHIP_NOT_ENOUGH_VALUE);
			return true;
		}
		// 扣除友情點
		if (payPoint > 0) {
			player.consumeFriendship(payPoint, Action.ACTIVITY166_CALL_OF_FRIENDSHIP);
			ConsumeItems.valueOf(changeType.CHANGE_FRIENDSHIP, payPoint).pushChange(player);
		}
		List<String> awardsList = new ArrayList<>();
		List<Integer> recordId = new ArrayList<Integer>();
		// 执行抽奖逻辑
		int boxId = 0;
		for (int i = 1; i <= searchTimes; i++) {
			AwardItems awards = new AwardItems();
			
			if ((searchTimes == TIMES_TYPE_TEN) && (i == 10)) { // 進入十抽池
				boxId = gachaCfg.getRandomBoxId(GachaListCfg.BOX_TYPE_TEN);
			} else {
				boxId = gachaCfg.getRandomBoxId(GachaListCfg.BOX_TYPE_SINGLE);
			}
			
			PackBoxCfg dropCfg = ConfigManager.getInstance().getConfigByKey(PackBoxCfg.class, boxId);
			
			if (dropCfg == null) {
				player.sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND);
				return true;
		    }
			// 掉落物品
			Item boxItem = dropCfg.getBoxDropItems().calcDropByOnly();
			
			awards.addItem(boxItem);
			awardsList.add(boxItem.toString());
			recordId.add(dropCfg.getId());
			
			// 抽獎禮,有抽有送
			ItemInfo gachaItem = ItemInfo.valueOf(gachaCfg.getGachaGift());
			if (gachaItem != null) {
				awards.addItem(gachaItem);;
			}
		
			awards.rewardTakeAffectAndPush(player, Action.ACTIVITY166_CALL_OF_FRIENDSHIP, 0,TapDBSource.Call_Of_Friendship);
		}

		Activity166Info.Builder builder = Activity166InfoHandler.generateInfo();

		for (String itemStr : awardsList) {
			builder.addReward(itemStr);
		}
		
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY166_CALL_OF_FRIENDSHIP_DRAW_S, builder));
		
		Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.CALL_HERO,
				GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
		hawkMsg.pushParam(searchTimes);
		GsApp.getInstance().postMsg(hawkMsg);

		// BI 日志 (freeTimes 本次寻宝开始前今日剩余的免费次数)
		BehaviorLogger.log4Service(player,Source.USER_OPERATION, Action.ACTIVITY166_CALL_OF_FRIENDSHIP, Params.valueOf("searchTimes", searchTimes),
				Params.valueOf("cfgId", recordId),Params.valueOf("costPoint", payPoint), Params.valueOf("awards", awardsList));
		return true;
	}
}
