package com.guaji.game.gm;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.guaji.app.App;
import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.obj.ObjBase;
import org.guaji.os.GuaJiRand;
import org.guaji.os.MyException;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.xid.GuaJiXID;

import com.google.gson.JsonObject;
import com.guaji.game.GsApp;
import com.guaji.game.ServerData;
import com.guaji.game.config.AllianceCfg;
import com.guaji.game.config.GvgCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.GvgCityEntity;
import com.guaji.game.entity.GvgTimeEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.ChatManager;
import com.guaji.game.manager.gvg.GvgService;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Alliance.HPAllianceEnterS;
import com.guaji.game.protocol.Const.AllianCheckButton;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.sun.net.httpserver.HttpExchange;

/**
 * http://127.0.0.1:5132/genRobot?user=admin&params=num:数量 GM创建帮会 帮会的名字由系统生成
 * 帮主也有系统生成
 */
public class GMCreateAllianceHandler extends GuaJiScript {
	protected static int mainRoleItemIDs[] = { 1, 2, 3 };

	@Override
	public void action(String params, HttpExchange httpExchange) {
		// TODO Auto-generated method stub
		// 创建机器人
		// 给机器人加金币
		// 创建机器人的帮会
		try {
			params = URLDecoder.decode(params, "UTF-8");
			Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
			int num = 1;
			if (paramsMap.containsKey("num")) {
				num = Integer.valueOf(paramsMap.get("num"));
			}
			List<Integer> bhIDs = new ArrayList<Integer>();
			// 指定职业的机器人
			for (int i = 0; i < num; i++) {
				Player player = createRobot();
				if (player == null) {
					break;
				}
				int itemIndex = GuaJiRand.random(0, mainRoleItemIDs.length - 1);
				int itemID = mainRoleItemIDs[itemIndex];
				// 主将ID随机获得，名字和puid一样
				player.getPlayerData().createMainRole(itemID, player.getPuid());
				// 将玩家的等级升起来
				addLevel(player);
				// 给机器人加钱
				addGold(player);
				// 创建帮会
				int bhID = createAlliance(player);
				bhIDs.add(bhID);
			}
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("status", 1);
			jsonObject.addProperty("newBHList", StringUtils.join(bhIDs, ","));
			GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
		} catch (Exception e) {
			MyException.catchException(e);
		}
	}

	/**
	 * 创建公会
	 * 
	 * @param player
	 */
	private int createAlliance(Player player) {
		AllianceCfg allianceCfg = ConfigManager.getInstance().getConfigByKey(AllianceCfg.class, 1);
		if (allianceCfg == null) {
			return -1;
		}
		AllianceEntity allianceEntity = new AllianceEntity();
		// 多线程hashmap没有转currenthashmap add by callan
		allianceEntity.init();
		allianceEntity.setPlayerId(player.getId());
		allianceEntity.setPlayerName(player.getName());
		allianceEntity.setExp(0);
		allianceEntity.setLevel(allianceCfg.getLevel());
		allianceEntity.setName("RobotBH_" + player.getId());
		allianceEntity.setCreateAllianceTime(System.currentTimeMillis());
		allianceEntity.setBossOpen(false);
		allianceEntity.setBossId(allianceCfg.getBossId());
		allianceEntity.getMemberList().add(player.getId());
		allianceEntity.setHasCheckLeaderMail(AllianCheckButton.CHECK_BUTTON_TYPE_1_VALUE); // 默认新创建的公会给会长发邮件同意按钮不进行勾选
		DBManager.getInstance().create(allianceEntity);
		// 标记为会长
		player.getPlayerData().loadPlayerAlliance();
		player.getPlayerData().getPlayerAllianceEntity().setPostion(GsConst.Alliance.ALLIANCE_POS_MAIN);
		player.getPlayerData().getPlayerAllianceEntity().setAllianceId(allianceEntity.getId());
		player.getPlayerData().getPlayerAllianceEntity().notifyUpdate(true);
		// 推送加入公会任务
		QuestEventBus.fireQuestEventOneTime(QuestEventType.JOIN_ALLIANCE, player.getXid());
		// 从db创建
		AllianceManager.getInstance().addAlliance(allianceEntity);
		AllianceManager.getInstance().getExistName().add(allianceEntity.getName());
		ConsumeItems.valueOf(changeType.CHANGE_GOLD, SysBasicCfg.getInstance().getAllianceCreateGold())
				.consumeTakeAffect(player, Action.ALLIANCE_CREATE_CONSUME);
		PlayerAllianceEntity myPlayerAllianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		if (myPlayerAllianceEntity.getAllianceId() != 0) {
			ChatManager.getInstance().addAllianceSession(player.getSession(), myPlayerAllianceEntity.getAllianceId(),
					player.getId());
		}
		GameUtil.sendAllianceChatTag(player);
		player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_CREATE_S_VALUE,
				AllianceManager.getInstance().getAllianceInfo(allianceEntity, player.getId(), player.getGold())));
		AllianceManager.getInstance().sendSelfData(HPAllianceEnterS.newBuilder(), player, allianceEntity);
		// 刷新快照
		player.getPlayerData().refreshOnlinePlayerSnapshot();

		// GVG功能开启(添加时间判断机制)
		if (AllianceManager.getInstance().getAllianceMap().size() >= GvgCfg.getInstance().getAllianceSize()
				&& GvgService.getInstance().isResetDate()) {
			if (!GvgService.getInstance().isOpeanFunction()) {
				GvgTimeEntity timeEntity = GvgTimeEntity.createEntity();
				GvgService.getInstance().setTimeEntity(timeEntity);
			}
		}
		return allianceEntity.getId();
	}

	protected Player createRobot() {
		Player player = null;
		ObjBase<GuaJiXID, AppObj> objBase = null;
		try {
			long maxPlayerID = Integer
					.valueOf(DBManager.getInstance().executeQuery("select max(id) from player").get(0).toString());
			int serverId = Integer
					.valueOf(DBManager.getInstance().executeQuery("select serverid from player").get(0).toString());
			String puid = "robot_" + serverId + "_" + (maxPlayerID + 1);
			String pwd = "888888";
			puid = puid.toLowerCase();
			int playerId = ServerData.getInstance().getPlayerIdByPuid(puid, serverId);
			if (playerId == 0) {
				PlayerEntity playerEntity = new PlayerEntity(puid, serverId, "", "", "", pwd);
				if (!DBManager.getInstance().create(playerEntity)) {
					return null;
				}
				playerId = playerEntity.getId();// 数据库id为playerID
				ServerData.getInstance().addPuidAndPlayerId(puid, serverId, playerId);// puid客户端来的
				Log.logPrintln(
						String.format("GM create player entity: %d, puid: %s, serverId: %d", playerId, puid, serverId));
			}
			GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId);
			GsApp.getInstance().lockObject(xid);

			// 对象不存在即创建
			if (objBase == null || !objBase.isObjValid()) {
				objBase = GsApp.getInstance().createObj(xid);
				if (objBase != null) {
					objBase.lockObj();
				}
				Log.logPrintln(String.format("GM create player: %d, puid: %s, serverId: %d", playerId, puid, serverId));
			}
			// 会话绑定应用对象
			if (objBase != null) {
				player = (Player) objBase.getImpl();
				onPlayerLogin(player, puid, serverId);
			}

		} finally {
			if (objBase != null) {
				objBase.unlockObj();
			}
		}
		return player;
	}

	protected void addLevel(Player player) {
		String awardItems = "10000_1003_14";
		AwardItems mailItems = AwardItems.valueOf(awardItems);
		mailItems.rewardTakeAffectAndPush(player, Action.MAIL_REWARD_TOOLS, 1);
	}

	protected void addGold(Player player) {
		String awardItems = "10000_1001_10000";
		AwardItems mailItems = AwardItems.valueOf(awardItems);
		mailItems.rewardTakeAffectAndPush(player, Action.MAIL_REWARD_TOOLS, 1);
	}

	/**
	 * 加入公会
	 * 
	 * @param player
	 * @param allianceId
	 */
	protected void joinAlliance(Player player, int allianceId) {
		PlayerAllianceEntity allianceEntity = player.getPlayerData().loadPlayerAlliance();
		AllianceEntity targetAllianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
		if (targetAllianceEntity == null) {
			return;
		}
		if (targetAllianceEntity.getHasCheckLeaderMail() == AllianCheckButton.CHECK_BUTTON_TYPE_1_VALUE) {
			int result = AllianceManager.getInstance().checkAddAlliance(player, targetAllianceEntity, player, 0,
					allianceEntity);
			if (result < 0) {
				return;
			}
			AllianceManager.getInstance().joinAlliance(player, allianceEntity, targetAllianceEntity, 0, true,
					Const.AddAllianceState.INITIATIVE_ADD_TYPE_1_VALUE);
			allianceEntity.notifyUpdate(false);
		} else {
			return;
		}
		// 添加聊天框皮肤
		TreeMap<Integer, GvgCityEntity> cities = GvgService.getInstance().getCities();
		for (GvgCityEntity cityEntity : cities.values()) {
			if (allianceId != cityEntity.getHolderId()) {
				continue;
			}
			if (cityEntity.getCityId() == GvgCfg.getInstance().getCityId()) {
				ActivityUtil.changeChatSkin(player.getId(), GvgCfg.getInstance().getSkinId(), 1);
				return;
			}
		}
	}

	/**
	 * 登陆协议处理
	 * 
	 * @param session
	 * @param protocol
	 */
	protected boolean onPlayerLogin(Player player, String puid, int serverId) {

		// 在线人数达到上限
		int sessionMaxSize = App.getInstance().getAppCfg().getSessionMaxSize();
		if (sessionMaxSize > 0 && ServerData.getInstance().getOnlinePlayer() >= sessionMaxSize) {
			return false;
		}
		// 加载玩家实体信息
		PlayerEntity playerEntity = player.getPlayerData().loadPlayer(puid, serverId);
		// 玩家对象信息错误
		if (playerEntity == null || playerEntity.getId() <= 0) {
			return false;
		}

		if (playerEntity.getForbidenTime() != null
				&& System.currentTimeMillis() < playerEntity.getForbidenTime().getTime()) {
			return false;
		}
		// 提取主角配置id
		List<RoleEntity> roleEntities = player.getPlayerData().loadRoleEntities();
		if (roleEntities != null) {
			for (RoleEntity roleEntity : roleEntities) {
				if (playerEntity.getLevel() > 0) {
					if (null != roleEntity) {
						roleEntity.setExp(playerEntity.getExp());
						roleEntity.setLevel(playerEntity.getLevel());
					}
				} else if (roleEntity.getType() == Const.roleType.MAIN_ROLE_VALUE) {
					playerEntity.setExp(roleEntity.getExp());
					playerEntity.setLevel(roleEntity.getLevel());
					playerEntity.notifyUpdate(true);
				}
			}
		}
		// 阵营
		player.getPlayerData().loadFormations();
		// 技能
		player.getPlayerData().loadSkillEntities();
		// 状态
		StateEntity stateEntity = player.getPlayerData().loadStateEntity();
		stateEntity.addGuideMap(1, 0);
		stateEntity.addGuideMap(2, 10000);
		stateEntity.addGuideMap(3, 10000);
		stateEntity.notifyUpdate(false);
		// 真气
		player.getPlayerData().loadPlayerTalentEntity();
		// 发送登陆成功协议
		// 同步玩家信息
		player.getPlayerData().syncPlayerInfo();

		if (player.getPlayerData().getMainRole() != null) {
			// 设置职业
			if (playerEntity.getProf() <= 0) {
				playerEntity.setProf(player.getProf());
				playerEntity.notifyUpdate(true);
			}
			// 通知玩家其他模块玩家登陆成功
			Msg msg = Msg.valueOf(GsConst.MsgType.PLAYER_LOGIN, player.getXid());// dispatch登录事件（先player对象，再对所有关心模块）
			if (!App.getInstance().postMsg(msg)) {
				Log.errPrintln("post player login message failed: " + playerEntity.getName());
			}
		} else {
		}

		return true;
	}

}
