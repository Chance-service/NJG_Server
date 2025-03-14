package com.guaji.game.module;

import java.util.List;

import com.guaji.game.util.*;
import org.guaji.annotation.ProtocolHandlerAnno;
import org.guaji.app.App;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.net.GuaJiSession;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.util.services.ReportService;

import com.guaji.game.ServerData;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.LoginEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.GsConst.AdjustActionType;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Login.HPLogin;
import com.guaji.game.protocol.Login.HPLoginRet;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.SysProtocol.HPErrorCode;
import org.guaji.net.GuaJiNetManager;

/**
 * 玩家登陆模块
 */
public class PlayerLoginModule extends PlayerModule {
	private int loginTime;

	/**
	 * 构造函数
	 * 
	 * @param player
	 */
	public PlayerLoginModule(Player player) {
		super(player);
	}

	/**
	 * 更新
	 * 
	 * @return
	 */
	@Override
	public boolean onTick() {
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
	 * 登陆协议处理
	 * 
	 * @param session
	 * @param protocol
	 * EnterGame
	 */
	@ProtocolHandlerAnno(code = HP.code.LOGIN_C_VALUE)
	private boolean onPlayerLogin(Protocol param) {
		GuaJiSession session = param.getSession();
		int hpCode = param.getType();
		HPLogin protocol = param.parseProtocol(HPLogin.getDefaultInstance());

		// 在线人数达到上限
		int sessionMaxSize = App.getInstance().getAppCfg().getSessionMaxSize();
		if (sessionMaxSize > 0 && ServerData.getInstance().getOnlinePlayer() >= sessionMaxSize) {
			session.sendProtocol(ProtoUtil.genErrorProtocol(hpCode, Status.error.ONLINE_MAX_LIMIT_VALUE, 1));
			return false;
		}

		String versionStr = protocol.getVersion();
		String[] verStrs = versionStr.split("\\.");
		if (verStrs.length == 3) {
			int version = Integer.valueOf(verStrs[2]);
			int sysVersion = SysBasicCfg.getInstance().getSysVersion();
			if (version < sysVersion) {
				HPErrorCode.Builder builder = HPErrorCode.newBuilder();
				builder.setHpCode(hpCode);
				// appstore 特殊需求
				builder.setErrMsg(SysBasicCfg.getInstance().getAppVersionNotice());
				builder.setErrCode(0);
				// 表示文字提示
				builder.setErrFlag(1);
				session.sendProtocol(Protocol.valueOf(HP.sys.ERROR_CODE, builder));
				return false;
			}
		}

		String puid = protocol.getPuid().trim().toLowerCase();
		puid = GameUtil.ito_91_amendPuid(puid);
		int serverId = protocol.getServerId();
		String device = protocol.getDeviceId().trim().toLowerCase();
		String platInfo = protocol.getPlatform().trim().toLowerCase();
		String langArea = protocol.getLangArea().trim();

		// 解析平台名称
		// phonetype#osversion#OS#platform#channel#id#mac#rmac&rip&route
		String[] platInfos = platInfo.split("#");
		String phoneType = "";
		String osVersion = "";
		String osName = "";
		String platName = "";
		String channel = "";
		@SuppressWarnings("unused")
		String storeId = "";
		String deviceMac = "020000000000";
		String routeInfo = "";
		for (int i = 0; i < platInfos.length; i++) {
			if (i == 0) {
				phoneType = platInfos[i];
			} else if (i == 1) {
				osVersion = platInfos[i];
			} else if (i == 2) {
				osName = platInfos[i];
			} else if (i == 3) {
				platName = platInfos[i];
			} else if (i == 4) {
				channel = platInfos[i];
			} else if (i == 5) {
				storeId = platInfos[i];
			} else if (i == 6) {
				deviceMac = platInfos[i];
			} else if (i == 7) {
				routeInfo = platInfos[i];
			}
		}

		// 整理平台名字
		String platform = channel;
		if (!channel.startsWith(platName)) {
			platform = platName + "_" + channel;
		}
		//在这里拦截各个渠道的登录处理
		
		player.setPlatformId(GameUtil.transtoPlatformId(channel));
		
		// 机器信息
		String phoneInfo = osName + "#" + osVersion + "#" + phoneType;
		player.setDeviceMac(deviceMac);
		player.setRouteInfo(routeInfo);
		player.setDeviceName(phoneType + "#" + osVersion);
		
		// 加载玩家实体信息
		PlayerEntity playerEntity = player.getPlayerData().loadPlayer(puid, serverId);
		
		if (playerEntity == null) {
			session.sendProtocol(ProtoUtil.genErrorProtocol(hpCode, Status.error.REGISTER_NOT_EXIST_VALUE, 1));
			return false;
		}
		
		String pwd = protocol.getPasswd().trim().toLowerCase();
		if (!playerEntity.getpwd().equals(pwd))
		{
			session.sendProtocol(ProtoUtil.genErrorProtocol(hpCode, Status.error.PASSWORD_ERROR_VALUE, 1));
			return false;
		}

		// 更新玩家设备相关信息
		if (playerEntity != null) {
			boolean needUpdate = false;
			if (playerEntity.getDevice() == null || playerEntity.getDevice().length() <= 0) {
				playerEntity.setDevice(device);
				needUpdate = true;
			}

			if (playerEntity.getPlatform() == null || playerEntity.getPlatform().length() <= 0) {
				playerEntity.setPlatform(platform);
				needUpdate = true;
			}

			if (playerEntity.getPhoneInfo() == null || playerEntity.getPhoneInfo().length() <= 0) {
				playerEntity.setPhoneInfo(phoneInfo);
				needUpdate = true;
			}

			if (playerEntity.getLangArea() == null || playerEntity.getLangArea().length() <= 0) {
				playerEntity.setLangArea(langArea);
				needUpdate = true;
			}

			// 回写设备信息
			if (needUpdate) {
				playerEntity.notifyUpdate(true);
			}
		}

		
		// 玩家对象信息错误
		if (playerEntity == null || playerEntity.getId() <= 0) {
			session.sendProtocol(ProtoUtil.genErrorProtocol(hpCode, Status.error.PLAYER_CREATE_FAILED_VALUE, 1));
			return false;
		}

		if (playerEntity.getForbidenTime() != null && System.currentTimeMillis() < playerEntity.getForbidenTime().getTime()) {
			session.sendProtocol(ProtoUtil.genErrorProtocol(hpCode, Status.error.PLAYER_FORBIDDEN_VALUE, 1));
			return false;
		}
		
		if (GuaJiNetManager.getInstance().checkBlackDevicetables(device))
		{
			session.sendProtocol(ProtoUtil.genErrorProtocol(hpCode, Status.error.ONLINE_MAX_LIMIT_VALUE, 1));
			return false;
		}
			

		
		HPLoginRet.Builder response = HPLoginRet.newBuilder();
		response.setPlayerId(playerEntity.getId());
		response.setIsGuest(playerEntity.getisguest());

		// 提取主角配置id
		List<RoleEntity> roleEntities = player.getPlayerData().loadRoleEntities();
		player.getPlayerData().loadSecretMsgEntities();
		player.getPlayerData().loadMottoEntities();
		if (roleEntities != null) {
			for (RoleEntity roleEntity : roleEntities) {
				if (playerEntity.getLevel() > 0) {
////					if(null != roleEntity) {
////						roleEntity.setExp(playerEntity.getExp());
////						roleEntity.setLevel(playerEntity.getLevel());
////					}
				
				} else if (roleEntity.getType() == Const.roleType.MAIN_ROLE_VALUE) {
					playerEntity.setExp(roleEntity.getExp());
					playerEntity.setLevel(roleEntity.getLevel());
					playerEntity.notifyUpdate(true);
					break;
				}
			}

			RoleEntity roleEntity = player.getPlayerData().getMainRole();
			if (roleEntity != null) {
				response.setRoleItemId(roleEntity.getItemId());
			}
		}

		// 设置时间戳
		response.setTimeStamp(GuaJiTime.getSeconds());
		// 绑定会话
		player.setSession(session);

		player.getPlayerData().loadPlayerTalentEntity();
	
		loginTime = GuaJiTime.getSeconds();
		// 发送登陆成功协议
		sendProtocol(Protocol.valueOf(HP.code.LOGIN_S, response));
		// 同步玩家信息
		player.getPlayerData().syncPlayerInfo();
		
		if (player.getPlayerData().getMainRole() != null) {
			// 设置职业
			if (playerEntity.getProf() <= 0) {
				playerEntity.setProf(player.getProf());
				playerEntity.notifyUpdate(true);
			}
			// adjust 开始游戏
			AdjustEventUtil.sentAdjustEventInfo(player,AdjustActionType.CLICKSTARTGAME,0);
			
			// 通知玩家其他模块玩家登陆成功
			Msg msg = Msg.valueOf(GsConst.MsgType.PLAYER_LOGIN, player.getXid());// dispatch登录事件（先player对象，再对所有关心模块）
			if (!App.getInstance().postMsg(msg)) {
				Log.errPrintln("post player login message failed: " + playerEntity.getName());
			}

			// 登录次数相关活动(老虎机返利活动)
			ActivityUtil.restActivity137Status(player.getPlayerData(),protocol.getIsReLogin());
			
			// 俄罗斯轮盘活动
			ActivityUtil.restActivity140Status(player.getPlayerData(),protocol.getIsReLogin());

			// 平台日志
			BehaviorLogger.log4Platform(player, Action.LOGIN_GAME, Params.valueOf("mainRole", player.getPlayerData().getMainRole().getId()),
					Params.valueOf("ipaddr", session.getIpAddr()));
		} else {
			// 平台日志
			BehaviorLogger.log4Platform(player, Action.LOGIN_GAME, Params.valueOf("mainRole", 0), Params.valueOf("ipaddr", session.getIpAddr()));
		}
		// 登陆信息上报
		ReportService.LoginData loginData = new ReportService.LoginData(serverId,"login", puid, device,phoneInfo, player.getId(),player.getLevel(),player.getVipLevel(),0,
				GuaJiTime.getTimeString());
		ReportService.getInstance().report(loginData);
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.LOGIN_GAME, Params.valueOf("level", player.getLevel()),
				Params.valueOf("gold", player.getGold()), Params.valueOf("coin", player.getCoin()), Params.valueOf("level", player.getLevel()),
				Params.valueOf("vipLevel", player.getVipLevel()), Params.valueOf("deviceId", device), Params.valueOf("platform", platform),
				Params.valueOf("platInfo", platInfo), Params.valueOf("ipaddr", session.getIpAddr()));
		
		return true;
	}

	@Override
	protected boolean onPlayerLogin() {
		player.getPlayerData().loadLogin(player.getPuid(), player.getServerId());
		return true;
	}

	@Override
	protected boolean onPlayerLogout() {
		// 行为日志
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.LOGOUT_GAME, Params.valueOf("level", player.getLevel()),
				Params.valueOf("gold", player.getGold()), Params.valueOf("coin", player.getCoin()), Params.valueOf("level", player.getLevel()),
				Params.valueOf("vipLevel", player.getVipLevel()));

		// 重要数据下线就存储
		player.getEntity().notifyUpdate(false);

		// 更新登陆信息
		LoginEntity loginEntity = player.getPlayerData().loadLogin(player.getPuid(), player.getServerId());
		if (loginEntity != null && loginTime > 0) {
			int periodTime = GuaJiTime.getSeconds() - loginTime;
			loginEntity.setPeriod(loginEntity.getPeriod() + periodTime);
			loginEntity.notifyUpdate();
			/**登出数据上报*/
			ReportService.LoginData loginData = new ReportService.LoginData(player.getServerId(),"logout", player.getPuid(),
					player.getDevice(),player.getPhoneInfo(), player.getId(),player.getLevel(),player.getVipLevel(), periodTime,GuaJiTime.getTimeString());
			ReportService.getInstance().report(loginData);
		}

		return true;
	}

}
