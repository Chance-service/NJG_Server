package com.guaji.game.gm;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.guaji.app.App;
import org.guaji.app.AppObj;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.obj.ObjBase;
import org.guaji.os.GuaJiRand;
import org.guaji.os.MyException;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.xid.GuaJiXID;

import com.google.gson.JsonObject;
import com.guaji.game.GsApp;
import com.guaji.game.ServerData;
import com.guaji.game.config.GvgCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.GvgCityEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.gvg.GvgService;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.AllianCheckButton;
import com.sun.net.httpserver.HttpExchange;

/**
 * 创建机器人
 * http://127.0.0.1:5132/genRobot?user=admin&params=num:数量;id:帮会id
 */
public class GeneratorRobotHandler extends GuaJiScript {
	protected static int mainRoleItemIDs[] = {1,2,3};

	@Override
	public void action(String params, HttpExchange httpExchange) {
		// TODO Auto-generated method stub
		// 机器人数量
		// 所属帮会id
		JsonObject jsonObject = new JsonObject();
		try {
			if(params==null||params.trim().equals(""))
			{
				jsonObject.addProperty("status", 0);
				jsonObject.addProperty("msg", "params not null");
				GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
				return;
			}
			params = URLDecoder.decode(params, "UTF-8");
			Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
			int num = 1;
			int allianceId = 0;
			if (paramsMap.containsKey("num")) {
				num = Integer.valueOf(paramsMap.get("num"));
			}
			if (paramsMap.containsKey("id")) {
				allianceId = Integer.valueOf(paramsMap.get("id"));
			}
			// 指定职业的机器人
			for (int i = 0; i < num; i++) {
				Player player = createRobot();
				if (player == null) {
					break;
				}
				int itemIndex = GuaJiRand.random(0,mainRoleItemIDs.length-1);
				int itemID = mainRoleItemIDs[itemIndex];
				// 主将ID随机获得，名字和puid一样
				player.getPlayerData().createMainRole(itemID, player.getPuid());
				if(allianceId>0){
					//加入指定的联盟
					//将玩家的等级升起来
					joinAlliance(player, allianceId);
					//将等级设置为15级
					addLevel(player);
				}
				addGold(player);
			}
			jsonObject.addProperty("status", 1);
			GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
		} catch (Exception e) {
			jsonObject.addProperty("status", -1);
			jsonObject.addProperty("msg", e.toString());
			GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
			MyException.catchException(e);
		}
	}
	
	protected Player createRobot() {
		Player player = null;
		ObjBase<GuaJiXID, AppObj> objBase = null;
		try {
			long maxPlayerID = Integer.valueOf(DBManager.getInstance().executeQuery("select max(id) from player").get(0).toString());
			int serverId = Integer.valueOf(DBManager.getInstance().executeQuery("select serverid from player").get(0).toString());
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
	
	protected void addLevel(Player player){
		String awardItems = "10000_1003_14";
		AwardItems mailItems = AwardItems.valueOf(awardItems);
		mailItems.rewardTakeAffectAndPush(player, Action.MAIL_REWARD_TOOLS,1);
	}
	
	protected void addGold(Player player){
		String awardItems = "10000_1001_10000";
		AwardItems mailItems = AwardItems.valueOf(awardItems);
		mailItems.rewardTakeAffectAndPush(player, Action.MAIL_REWARD_TOOLS,1);
	}

	
	/**
	 * 加入公会
	 * @param player
	 * @param allianceId
	 */
	protected void joinAlliance(Player player,int allianceId){
		PlayerAllianceEntity allianceEntity = player.getPlayerData().loadPlayerAlliance();
		AllianceEntity targetAllianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
		if(targetAllianceEntity==null){
			return;
		}
		if(targetAllianceEntity.getHasCheckLeaderMail() == AllianCheckButton.CHECK_BUTTON_TYPE_1_VALUE) {
			int result = AllianceManager.getInstance().checkAddAlliance(player, targetAllianceEntity, player, 0, allianceEntity);
			if(result < 0) {
				return;
			}
			AllianceManager.getInstance().joinAlliance(player, allianceEntity, targetAllianceEntity, 0, true, Const.AddAllianceState.INITIATIVE_ADD_TYPE_1_VALUE);
			allianceEntity.notifyUpdate(false);
		}else {
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
	protected boolean onPlayerLogin(Player player,String puid,int serverId) {

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

		if (playerEntity.getForbidenTime() != null && System.currentTimeMillis() < playerEntity.getForbidenTime().getTime()) {
			return false;
		}
		// 提取主角配置id
		List<RoleEntity> roleEntities = player.getPlayerData().loadRoleEntities();
		if (roleEntities != null) {
			for (RoleEntity roleEntity : roleEntities) {
				if (playerEntity.getLevel() > 0) {
					if(null != roleEntity) {
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
		//阵营
		player.getPlayerData().loadFormations();
		//技能
		player.getPlayerData().loadSkillEntities();
		//状态
		StateEntity stateEntity = player.getPlayerData().loadStateEntity();
		stateEntity.addGuideMap(1, 0);
		stateEntity.addGuideMap(2, 10000);
		stateEntity.addGuideMap(3, 10000);
		stateEntity.notifyUpdate(false);
		//真气
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
