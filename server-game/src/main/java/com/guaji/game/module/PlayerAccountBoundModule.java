package com.guaji.game.module;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.guaji.annotation.ProtocolHandlerAnno;
import org.guaji.app.App;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.MyException;

import com.guaji.game.GsConfig;
import com.guaji.game.ServerData;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.protocol.AccountBound.HPAccountBoundConfirm;
import com.guaji.game.protocol.AccountBound.HPAccountBoundConfirmRet;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 玩家帐号绑定;
 */
public class PlayerAccountBoundModule extends PlayerModule {
	
	/** 帐号绑定功能未开启 */
	//private static final int ACCOUNT_ALREADY_NOT_OPEN = 4;
	
	/** 游客身份 */
	//private static final int ACCOUNT_VISITOR = 1;
	
	/** 帐号已绑定,但未领奖 */
	//private static final int ACCOUNT_ALREADY_BOUND_AWARD_NO = 2;
	
	/** 帐号已绑定,已领奖 */
	private static final int ACCOUNT_ALREADY_BOUND_AWARD_YES = 3;
	
	private static final int ACCOUNT_IS_BOUND = 0;	//已綁工口帳號
	
	public PlayerAccountBoundModule(Player player) {
		super(player);
	}

	@Override
	protected boolean onPlayerLogin() {
		return syncAccountStatus();
	}

	/**
	 * 进入帐号绑定界面;
	 * 
	 * @param protocol
	 */
	@ProtocolHandlerAnno(code = HP.code.ACCOUNT_BOUND_INFO_C_VALUE)
	private void onAccountBoundInfo(Protocol protocol) {
		syncAccountStatus();
	}
	
	/**
	 * 玩家帐号绑定领奖;
	 * 
	 * @param protocol
	 */
	@ProtocolHandlerAnno(code = HP.code.ACCOUNT_BOUND_REWARD_C_VALUE)
	private void onAccountBoundReward(Protocol protocol) {
		// 检查帐号绑定是否开启
//		if (!SysBasicCfg.getInstance().isAccountBoundOpen()) {
//			sendStatusProto(ACCOUNT_ALREADY_NOT_OPEN);
//			return;
//		}
//		int accountBoundStatus = player.getPlayerData().getStateEntity().getAccountBoundStatus();
//		YouaiPlatformUtil youaiUtil = new YouaiPlatformUtil();
//		UserType userType = youaiUtil.queryUserType(player.getDevice(), player.getDeviceName(), player.getPlatform(), player.getPuid());
//		
//		if (accountBoundStatus != ACCOUNT_ALREADY_BOUND_AWARD_NO || userType.equals(UserType.GUEST_ACCOUNT)) {
//			player.sendError(HP.code.ACCOUNT_BOUND_REWARD_C_VALUE, Status.error.CAN_NOT_ACCOUNT_BOUND_REWARD_VALUE);
//			return;
//		}
//		// 发放钻石奖励
//		AwardItems awards = new AwardItems();
//		awards.addGold(SysBasicCfg.getInstance().getAccountBoundReward());
//		awards.rewardTakeAffectAndPush(player, Action.ACCOUNT_BOUND,1);
//		
//		// 日志记录
//		BehaviorLogger.log4Platform(player, Action.ACCOUNT_BOUND, Params.valueOf("accountBoundReward", SysBasicCfg.getInstance().getAccountBoundReward()));
//		
//		changePlayerStatusEntity(ACCOUNT_ALREADY_BOUND_AWARD_YES);
//		sendStatusProto(ACCOUNT_ALREADY_BOUND_AWARD_YES);
//		sendRewardProto();
		
		// 检查帐号绑定是否开启

		HPAccountBoundConfirm req = protocol.parseProtocol(HPAccountBoundConfirm.getDefaultInstance());
		String userid = req.getUserId();
		String puId = player.getPuid();
		int isguest = player.getPlayerData().getPlayerEntity().getisguest();
		int sid = GsConfig.getInstance().getServerId();
//		if (sid == 1)
//		{//測試伺服器
//			sid = 6;
//		}
		int playerId = ServerData.getInstance().getPlayerIdByPuid(userid, sid);
		if (isguest == ACCOUNT_IS_BOUND || userid.isEmpty() || (playerId != 0)) {
			player.sendError(HP.code.ACCOUNT_BOUND_REWARD_C_VALUE, Status.error.CAN_NOT_ACCOUNT_BOUND_REWARD_VALUE);
			return;
		}
		
		// 发放钻石奖励
//		AwardItems awards = new AwardItems();
//		awards.addGold(SysBasicCfg.getInstance().getAccountBoundReward());
//		awards.rewardTakeAffectAndPush(player, Action.ACCOUNT_BOUND,1);
		
		// 日志记录
		BehaviorLogger.log4Platform(player, Action.ACCOUNT_BOUND, Params.valueOf("originaccount",player.getPuid()),
				Params.valueOf("newaccount",userid));
		
		changePlayerguestEntity(ACCOUNT_IS_BOUND, userid);
		//sendStatusProto(ACCOUNT_ALREADY_BOUND_AWARD_YES);
		sendRewardProto();
		
		// 伺服器如果不止開著一台需要更改其他伺服器玩家帳號
		if  (App.getInstance().getAppCfg().isDebug()) {
			if (sid == 6) {
				changeOtherServerPuId(9,puId,userid);
			}
			if (sid == 9) {
				changeOtherServerPuId(6,puId,userid);
			}
		}
		
		
		
	}

	/**
	 * 同步给客户端玩家帐号绑定状态;
	 * 
	 * @return
	 */
	private boolean syncAccountStatus() {
		// 检查帐号绑定是否开启
//		if (!SysBasicCfg.getInstance().isAccountBoundOpen()) {
//			return sendStatusProto(ACCOUNT_ALREADY_NOT_OPEN);
//		}
//		int accountBoundStatus = player.getPlayerData().getStateEntity().getAccountBoundStatus();
//		YouaiPlatformUtil youaiUtil = new YouaiPlatformUtil();
//		UserType userType = youaiUtil.queryUserType(player.getDevice(), player.getDeviceName(), player.getPlatform(), player.getPuid());
//		if (userType == null || userType.equals(UserType.GUEST_ACCOUNT)) {
//			// 游客,未领奖
//			changePlayerStatusEntity(ACCOUNT_VISITOR);
//			return sendStatusProto(ACCOUNT_VISITOR);
//		}
//		if (userType.equals(UserType.OFFICIAL_ACCOUNT)) {
//			if (accountBoundStatus == ACCOUNT_ALREADY_BOUND_AWARD_YES) {
//				// 已绑定,已领奖
//				return sendStatusProto(ACCOUNT_ALREADY_BOUND_AWARD_YES);
//			} else {
//				// 已绑定,未领奖
//				changePlayerStatusEntity(ACCOUNT_ALREADY_BOUND_AWARD_NO);
//				return sendStatusProto(ACCOUNT_ALREADY_BOUND_AWARD_NO);
//			}
//		}
		return false;
	}

	/**
	 * 同步状态信息到DB;
	 * 
	 * @param status
	 */
//	private void changePlayerStatusEntity(int status) {
//		int accountBoundStatus = player.getPlayerData().getStateEntity().getAccountBoundStatus();
//		if (accountBoundStatus == status) {
//			return;
//		}
//		player.getPlayerData().getStateEntity().setAccountBoundStatus(status);
//		player.getPlayerData().getStateEntity().notifyUpdate(true);
//	}

	/**
	 * 发送玩家状态协议;
	 * 
	 * @param status
	 * @return
	 */
//	private boolean sendStatusProto(int status) {
//		HPAccountBoundRet.Builder builder = HPAccountBoundRet.newBuilder();
//		builder.setAccountStatus(status);
//		builder.setAccountReward(SysBasicCfg.getInstance().getAccountBoundReward());
//		return player.sendProtocol(Protocol.valueOf(HP.code.ACCOUNT_BOUND_INFO_S_VALUE, builder));
//	}
	
	/**
	 * 发送奖励协议;
	 * 
	 * @return
	 */
	private boolean sendRewardProto() {
		HPAccountBoundConfirmRet.Builder builder = HPAccountBoundConfirmRet.newBuilder();
		builder.setAccountStatus(ACCOUNT_ALREADY_BOUND_AWARD_YES);
		builder.setAccountReward(SysBasicCfg.getInstance().getAccountBoundReward());
		return player.sendProtocol(Protocol.valueOf(HP.code.ACCOUNT_BOUND_REWARD_S_VALUE, builder));
	}
	
	/**
	 * 同步状态信息到DB;
	 * 
	 * @param status
	 */
	private void changePlayerguestEntity(int isguest,String userid) {
		int accountisguest = player.getPlayerData().getPlayerEntity().getisguest();
		if (accountisguest == isguest) {
			return;
		}

		ServerData.getInstance().addPuidAndPlayerId(userid, player.getServerId(), player.getId());
		player.getPlayerData().getPlayerEntity().setPuid(userid);
		player.getPlayerData().getPlayerEntity().setpwd("888888");
		player.getPlayerData().getPlayerEntity().setisguest(isguest);
		player.getPlayerData().getPlayerEntity().notifyUpdate(true);
	}
	
	private boolean changeOtherServerPuId(int Svrid ,String puid,String newpuid) {
			try {
				String Url = "";
	            if (Svrid == 6) { // NGTest
		    		Url = "http://18.182.62.36:5132/changePuid?params=";
	            }
	            if (Svrid == 9) { // 內部102(156)
		    		Url = "http://220.130.219.201:5132/changePuid?params=";
	            }
	            
	            if (Url.equals("")) {
	            	return false;
	            }
			
				StringBuffer paramsBuf = new StringBuffer();
				paramsBuf.append(Url);
				paramsBuf.append("puid:");
				paramsBuf.append(puid);
				paramsBuf.append(";newpuid:");
				paramsBuf.append(newpuid);
				paramsBuf.append(";Svrid:");
				paramsBuf.append(String.valueOf(Svrid));
				paramsBuf.append("&user=");
				paramsBuf.append("hawk");			
				String reqUrl = paramsBuf.toString();
				
				HttpClient httpClient = HttpClients.custom().build();
				HttpGet httpGet = new HttpGet(reqUrl);
				RequestConfig reqConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
				httpGet.setConfig(reqConfig);
				httpGet.setHeader("Content-type", "application/json");
				HttpResponse response = null;
				
				response = httpClient.execute(httpGet);
	
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					return true;
				}
			return false;
		} catch (Exception e) {
			// TODO: handle exception
			MyException.catchException(e);
			return false;
		}
	}
	
}
