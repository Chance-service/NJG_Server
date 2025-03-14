package com.guaji.game.module.alliance;

import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Alliance.ApplyAllianceState;
import com.guaji.game.protocol.Alliance.HPApplyIntoAllianceC;
import com.guaji.game.protocol.Alliance.HPApplyIntoAllianceS;
import com.guaji.game.protocol.Const.AllianCheckButton;
import com.guaji.game.protocol.Const.ApplyState;
import com.guaji.game.bean.ApplyAllianceStates;
import com.guaji.game.bean.PlayerEmailData;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.MailManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.AllianceUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsonUtil;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Mail;
import com.guaji.game.protocol.Status;

/**
 * 申请加入公会
 */
public class ApplyIntoAllianceHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		HPApplyIntoAllianceC par = protocol.parseProtocol(HPApplyIntoAllianceC.getDefaultInstance());
		int allianceId = par.getAllianceId();
		// 玩家公会数据
		PlayerAllianceEntity playerAlliance = player.getPlayerData().getPlayerAllianceEntity();
		// 公会数据
		AllianceEntity entity = AllianceManager.getInstance().getAlliance(allianceId);
		if (entity == null) {
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NONEXISTENT);
			return true;
		}
		//是否勾选通过会长同意按钮
		if(entity.getHasCheckLeaderMail() == AllianCheckButton.CHECK_BUTTON_TYPE_1_VALUE) {
			//未勾选，提示报错
			player.sendError(protocol.getType(), Status.error.CHECK_NO_BUTTON_ALLIANCE_FAIL);
			return true;
		}
		//申请的公会玩家战力是否达到
		if(player.getFightValue() < entity.getJoinLimit()){
			// 战斗力不足
			player.sendError(protocol.getType(), Status.error.FIGHT_VALUE_NOT_ENOUGH);
			return true;
		}
		//申请的公会人数是否已满
		if(entity.getMemberList().size() >= AllianceUtil.getAllianceMaxPop(entity.getLevel())){
			// 公会已满
			player.sendError(protocol.getType(), Status.error.ALLIANCE_FULL_ERROR);
			return true;
		}
		//申请公会是否CD
		if(playerAlliance.getExitTime() > System.currentTimeMillis()){
			// 退出后再次加入时间未到
			player.sendError(protocol.getType(), Status.error.ALLIANCE_JOIN_ERROR_VALUE);
			return true;
		}
		Map<Integer, ApplyAllianceStates> applyAllianceMap = playerAlliance.getApplyAllianceDataMap();
		//申请的公会是否达到上限
		if(applyAllianceMap != null && applyAllianceMap.size() > 0 && applyAllianceMap.size() >= SysBasicCfg.getInstance().getApplyAddAllianceMaxCount()) {
			// 达到上限,不能申请
			player.sendError(protocol.getType(), Status.error.APPLY_ALLIANCE_EXCEED_MAX);
			return true;
		}
		//申请验证公会长的邮件是否已满(暂时没看到邮件上限操作)
		if(AllianceManager.getInstance().checkApplyAllianceMax(entity.getPlayerId())){
			//可申请加入公会数量已达到最大上限
			player.sendError(protocol.getType(), Status.error.APPLY_ADD_ALLIANCE_MAX_FAIL);
			return true;
		}
		ApplyAllianceStates aState = applyAllianceMap.get(allianceId);
		if(aState == null) {
			aState = new ApplyAllianceStates();
			aState.setAllianceId(allianceId);
			aState.setRefusedJoinTime(0l);
		}else if(aState.getState() == ApplyState.APPLY_STATE_1_VALUE) {
			//已经申请过该公会
			player.sendError(protocol.getType(), Status.error.REPEAT_APPLY_ALLIANCE_FAIL);
			return true;
		}else if(aState.getState() == ApplyState.APPLY_STATE_2_VALUE && aState.getRefusedJoinTime() > GuaJiTime.getMillisecond()) {
			//再次申请该公会CD中
			player.sendError(protocol.getType(), Status.error.AGAIN_APPLY_ALLIANCE_CD);
			return true;
		}
		aState.setState(ApplyState.APPLY_STATE_1_VALUE);
		applyAllianceMap.put(allianceId, aState);
		playerAlliance.notifyUpdate(true);
		//发邮件给公会会长
		PlayerEntity playerEntity = player.getPlayerData().getPlayerEntity();
		//构建json对象
		PlayerEmailData emailData = new PlayerEmailData(playerEntity, allianceId);
		emailData.setRebirthStage(AllianceManager.getInstance().isRebirthStageOne(player));
		String content = GsonUtil.getJsonInstance().toJson(emailData, PlayerEmailData.class);
		//发邮件
		MailManager.createSysMail(entity.getPlayerId(), Mail.MailType.Normal_VALUE, GsConst.MailId.APPLY_ADD_ALLIANCE, "给公会会长发送申请加入公会的邮件", null, content);
		HPApplyIntoAllianceS.Builder ret = HPApplyIntoAllianceS.newBuilder();
		for (Map.Entry<Integer, ApplyAllianceStates> entry : applyAllianceMap.entrySet()) {
			ApplyAllianceState.Builder bean = ApplyAllianceState.newBuilder();
			bean.setAllianceId(entry.getKey());
			bean.setState(entry.getValue().getState());
			bean.setRefusedJoinTime(entry.getValue().getRefusedJoinTime());
			ret.addAllianceState(bean);
		}
		player.sendProtocol(Protocol.valueOf(HP.code.APPLY_INTO_ALLIANCE_S, ret));
		
		return true;
	}
	
}
