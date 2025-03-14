package com.guaji.game.module.alliance;

import java.util.Map;
import java.util.TreeMap;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.protocol.Alliance.HPApprovalRefusedOperC;
import com.guaji.game.protocol.Alliance.HPApprovalRefusedOperS;
import com.guaji.game.bean.ApplyAllianceStates;
import com.guaji.game.config.GvgCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.EmailEntity;
import com.guaji.game.entity.GvgCityEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.MailManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.manager.gvg.GvgService;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.AllianOperState;
import com.guaji.game.protocol.Const.ApplyState;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Mail;
import com.guaji.game.protocol.Status;

/**
 * 批准或者拒绝加入公会(公会长操作)
 */
public class ApprovalRefusedOperHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		HPApprovalRefusedOperC par = protocol.parseProtocol(HPApprovalRefusedOperC.getDefaultInstance());
		int targetAllianceId = par.getAllianceId();
		int playerId = par.getPlayerId();
		int state = par.getState();
		int emailId = par.getEmailId();
		Map<Integer, EmailEntity> myMails = player.getPlayerData().getEmailEntities();
		// 申请玩家数据(返回在线玩家对象)
		Player targetPlayer = AllianceManager.getInstance().whetherOnline(playerId);
		PlayerAllianceEntity playerAlliance = null;
		boolean isOnline = true;
		if(targetPlayer != null) {
			//在线玩家数据集
			playerAlliance = targetPlayer.getPlayerData().getPlayerAllianceEntity();
		}else {
			//离线数据Player
			PlayerEntity playerEntity = AllianceManager.getInstance().getPlayer(playerId);
			if (playerEntity != null) {
				targetPlayer = new Player(GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId));
				targetPlayer.getPlayerData().setPlayerEntity(playerEntity);
			}
			//离线数据
			playerAlliance = AllianceManager.getInstance().getPlayerAlliance(playerId);
			targetPlayer.getPlayerData().setPlayerAllianceEntity(playerAlliance);
			isOnline = false;
		}
		// 公会数据
		AllianceEntity entity = AllianceManager.getInstance().getAlliance(targetAllianceId);
		if(entity == null){
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return false;
		}
		if (entity.getPlayerId() != player.getId()) {//判断是否为会长操作
			//非会长不允许操作
			player.sendError(protocol.getType(), Status.error.NOPRESIDENT_NOTALLOW_OPERATE);
			return true;
		}
		
		if(AllianceManager.getInstance().isIntoAlliance(entity, playerId)) {
			//已经进入该公会
			player.sendError(protocol.getType(), Status.error.ALREADY_INTO_ALLIANCE);
			return true;
		}
		
		Map<Integer, ApplyAllianceStates> applyAllianceMap = playerAlliance.getApplyAllianceDataMap();
		//同意加入公会操作
		if(state == AllianOperState.APPROVAL_TYPE_1_VALUE) {
			//验证玩家是否加入其它公会
			if(playerAlliance.getAllianceId() > 0 && playerAlliance.getAllianceId() != targetAllianceId) {
				//玩家加入到其它公会去了
				player.sendError(protocol.getType(), Status.error.PLAYER_INIO_OTHTER_ALLIANCE);
				return true;
			}
			if(applyAllianceMap != null && applyAllianceMap.get(entity.getId()) != null) {
				//玩家成功加入公会 
				int result = AllianceManager.getInstance().checkAddAlliance(targetPlayer, entity, player, protocol.getType(), playerAlliance);
				if(result < 0) {
					return true;
				}
				AllianceManager.getInstance().joinAlliance(targetPlayer, playerAlliance, entity, protocol.getType(), isOnline, Const.AddAllianceState.MAIN_AGREE_TYPE_2_VALUE);
				//清除玩家公会申请数据
				playerAlliance.getApplyAllianceDataMap().clear();
				// 添加聊天框皮肤
				TreeMap<Integer, GvgCityEntity> cities = GvgService.getInstance().getCities();
				for (GvgCityEntity cityEntity : cities.values()) {
					if (targetAllianceId != cityEntity.getHolderId()) {
						continue;
					}
					if (cityEntity.getCityId() == GvgCfg.getInstance().getCityId()) {
						ActivityUtil.changeChatSkin(targetPlayer.getId(), GvgCfg.getInstance().getSkinId(), 1);
						break;
					}
				}
			}
		}else if(state == AllianOperState.Refused_TYPE_2_VALUE) { //拒绝加入公会操作
			//更新拒绝状态
			ApplyAllianceStates aState = applyAllianceMap.get(targetAllianceId);
			if(aState != null) {
				aState.setAllianceId(targetAllianceId);
				//设置状态为拒绝
				aState.setState(ApplyState.APPLY_STATE_2_VALUE);
				//设置拒绝后再次申请的CD时间
				aState.setRefusedJoinTime(System.currentTimeMillis() + (60 * 60000) * SysBasicCfg.getInstance().getRefreshApplyAddAllianceTime());
				//发送拒绝邮件给玩家
				MailManager.createSysMail(playerId, Mail.MailType.Normal_VALUE, GsConst.MailId.REFUSED_INIO_ALLIANCE, "给申请者发放拒绝邮件" +targetPlayer.getId() , null, GuaJiTime.getTimeString(), entity.getName(), par.getContent());
			}
		}else {
			//参数异常
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			return true;
		}
		if(isOnline) {
			playerAlliance.notifyUpdate(true);
		}else {
			playerAlliance.notifyUpdate(false);
			SnapShotManager.getInstance().getCurrentStrategy().onAllianceDataChanged(playerAlliance);
			AllianceManager.getInstance().offlineRefresh(playerAlliance);
		}
		//清除申请的邮件
		if(myMails.get(emailId) != null) {
			player.getPlayerData().removeEmailEntity(myMails.get(emailId));
		}
		HPApprovalRefusedOperS.Builder ret = HPApprovalRefusedOperS.newBuilder();
		ret.setAllianceId(targetAllianceId);
		ret.setPlayerId(playerId);
		ret.setState(state);
		ret.setEmailId(emailId);
		player.sendProtocol(Protocol.valueOf(HP.code.APPROVAL_REFUSED_OPER_S, ret));
		return true;
	}
}
