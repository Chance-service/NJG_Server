package com.guaji.game.module;

import java.util.Date;

import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.SignCfg;
import com.guaji.game.entity.SignEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.manager.ChatManager;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Player.HPSysSetting;
import com.guaji.game.protocol.RoleOpr.HPRoleUPLevelRes;
import com.guaji.game.protocol.Sign.SignRequest;
import com.guaji.game.protocol.Sign.SignRespones;

/**
 * 状态模块
 */
public class PlayerStateModule extends PlayerModule {
	/**
	 * 构造函数
	 * 
	 * @param player
	 */
	public PlayerStateModule(Player player) {
		super(player);

		listenProto(HP.code.SYS_SETTING_C);
		listenProto(HP.code.SIGN_SYNC_C);
	}

	private int tickIndex = 0;

	/**
	 * 更新
	 * 
	 * @return
	 */
	@Override
	public boolean onTick() {
		// 在线跨天重置
		if (player.getEntity().getResetTime() == null || !GuaJiTime.isToday(player.getEntity().getResetTime())) {
			player.handleDailyFirstLogin(true);
		}
		// 每一项单独检测
		if (++tickIndex % 100 == 0) {
			player.handleReset();

			player.handleYaYaRankReset();
			
			player.handleHaremReset();
		}

		return super.onTick();
	}

	/**
	 * 消息响应
	 * 
	 * @param msg
	 * @return
	 */
	@Override
	public boolean onMessage(Msg msg) {
		return super.onMessage(msg);
	}

	/**
	 * 协议响应
	 * 
	 * @param protocol
	 * @return
	 */
	@Override
	public boolean onProtocol(Protocol protocol) {
		if (protocol.checkType(HP.code.SYS_SETTING_C)) {
			onSysSetting(protocol.parseProtocol(HPSysSetting.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.SIGN_SYNC_C)) {
			onSignHandler(protocol);
			return true;
		}
		return super.onProtocol(protocol);
	}

	/**
	 * 玩家登陆处理(数据同步)
	 */
	@Override
	protected boolean onPlayerLogin() {
		// 加载状态数据 標記
		player.getPlayerData().loadStateEntity();
		player.getPlayerData().loadSignEntity();
		player.getPlayerData().loadItemEntities();

		// 记录连续登录
		long lastTime = player.getEntity().getLoginTime().getTime();
		int loginDay = getLoginDay(lastTime);
		int dayCount = player.getEntity().getLoginDay();
		if (getIsLogin(lastTime)) {
			player.getEntity().setLoginDay(dayCount + loginDay);
			if (loginDay == 1) {
				// 登录
				Msg msg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.LOGIN_DAY, GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
				msg.pushParam(1);
				GsApp.getInstance().postMsg(msg);
				
//				Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.GLORY_HOLE_LOGIN_DAY,
//						GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
//				hawkMsg.pushParam(1);
//				GsApp.getInstance().postMsg(hawkMsg);
		
			}
		} else {
			player.getEntity().setLoginDay(1);
		}

		// 设置登陆时间
		player.getEntity().setLoginTime(GuaJiTime.getCalendar().getTime());
		player.getEntity().notifyUpdate();

		// 跨天登入重置
		if (player.getEntity().getResetTime() == null || !GuaJiTime.isToday(player.getEntity().getResetTime())) {
			player.handleDailyFirstLogin(false);
		}

		// 同步状态数据
		player.getPlayerData().syncStateInfo();
		player.getPlayerData().syncGuideInfo();
		player.getPlayerData().syncplaystory(); 
		// 添加聊天玩家
		if (player.getPlayerData().getStateEntity().getChatClose() <= 0) {
			ChatManager.getInstance().addSession(player.getSession(), true,true);
		}

		return true;
	}

	/**
	 * 玩家下线处理
	 */
	@Override
	protected boolean onPlayerLogout() {
		// 有主角的时候, 保存下线时间
		if (player.getPlayerData().getMainRole() != null) {
			player.getEntity().setLogoutTime(GuaJiTime.getCalendar().getTime());
			player.getEntity().notifyUpdate(true);
		}
		return true;
	}

	/**
	 * 系统设置
	 * 
	 * @param protocol
	 * @return
	 */
	protected boolean onSysSetting(HPSysSetting protocol) {
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		if (protocol.hasAutoSellEquip()) {
			stateEntity.setAutoSellEquip(protocol.getAutoSellEquip());
		}

		if (protocol.hasChatClose()) {
			stateEntity.setChatClose(protocol.getChatClose());
		}

		if (protocol.hasWipeBoss() && player.getVipLevel() > 0) {
			stateEntity.setWipeBoss(protocol.getWipeBoss());
		}

		if (protocol.hasMusicOn()) {
			stateEntity.setMusic(protocol.getMusicOn());
		}

		if (protocol.hasSoundOn()) {
			stateEntity.setSound(protocol.getSoundOn());
		}

		if (protocol.hasShowArea()) {
			stateEntity.setShowArea(protocol.getShowArea() > 0);
		}

		if (protocol.hasOnlyText()) {
			stateEntity.setOnlyText(protocol.getOnlyText() > 0);
		}

		if (protocol.hasAutoDecoElement()) {
			stateEntity.setAutoDecoElement(protocol.getAutoDecoElement());
		}

		if (protocol.hasFontSize()) {
			stateEntity.setFontSize(protocol.getFontSize());
		}

		stateEntity.notifyUpdate(true);
		player.getPlayerData().syncStateInfo();

		// 修改聊天设置
		if (stateEntity.getChatClose() > 0) {
			ChatManager.getInstance().removeSession(player.getSession());
		} else {
			ChatManager.getInstance().addSession(player.getSession(), false);
		}
		return true;
	}

	/**
	 * 连续登录
	 * 
	 * @param lastLoginTime
	 *            上次登录时间
	 * @return
	 */
	public static int getLoginDay(long lastLoginTime) {
		int today0Time = (int) (GuaJiTime.getAM0Date().getTime() / 1000);
		int last0Time = (int) (GuaJiTime.getAM0Date(new Date(lastLoginTime)).getTime() / 1000);
		if (last0Time < today0Time && getIsLogin(lastLoginTime)) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * 是否隔天，连续登录清零(连续登录)
	 * 
	 * @param lastLoginTime
	 * @return
	 */
	public static boolean getIsLogin(long lastLoginTime) {
		int last0Time = (int) (GuaJiTime.getAM0Date(new Date(lastLoginTime)).getTime() / 1000);
		if (last0Time + 48 * 60 * 60 > GuaJiTime.getSeconds()) {
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] args) {
		getLoginDay(1486569600000L);
	}
	
	/**
	 * 標記處理
	 * @param protocol
	 */
	protected boolean onSignHandler(Protocol protocol) {
		SignRequest req = protocol.parseProtocol(SignRequest.getDefaultInstance());
		SignEntity signEntity = player.getPlayerData().getSignEntity();
		int action = req.getAction();
		SignRespones.Builder builder = SignRespones.newBuilder();
		builder.setAction(action);
		if (action == GsConst.SignProtoType.Sync_All) {
			builder.addAllSignId(signEntity.getSignSet());
		} else if (action == GsConst.SignProtoType.Inquire_Sign) {
			int signId = req.getSignId();
			if (signId <= 0) {
				sendError(HP.code.ROLE_UP_LEVEL_C_VALUE, Status.error.SIGN_NOT_DEFINED_VALUE);
				return false ;
			}
			boolean value = signEntity.getSignSet().contains(signId);
			builder.addSignId(signId);
			builder.setState(value);
		} else if (action == GsConst.SignProtoType.Modify_Sign) {
			int signId = req.getSignId();
			if (signId <= 0) {
				sendError(HP.code.ROLE_UP_LEVEL_C_VALUE, Status.error.SIGN_NOT_DEFINED_VALUE);
				return false ;
			}
			// 檢查權限
			if (!SignCfg.checkCcontral(signId)){
				sendError(HP.code.ROLE_UP_LEVEL_C_VALUE, Status.error.SIGN_INSUFFICIIENT_PERMISSIONS_VALUE);
				return false ;
			}
			boolean fixVal = req.getSetVal();
			boolean needSave = false;
			if (fixVal) {
			  if (!signEntity.getSignSet().contains(signId)) {
				  signEntity.getSignSet().add(signId);
				  needSave = true;
			  }
			} else {
			  if (signEntity.getSignSet().contains(signId)) {
				  signEntity.getSignSet().remove(signId);
				  needSave = true;
			  }
			}
			if (needSave) {
				signEntity.SaveSign();
			}
			builder.addSignId(signId);
			builder.setState(fixVal);
		} else {
			sendError(HP.code.ROLE_UP_LEVEL_C_VALUE, Status.error.PARAMS_INVALID);
			return false;
		}
		
		player.sendProtocol(Protocol.valueOf(HP.code.SIGN_SYNC_S, builder));
		return true ;
	} 
}
