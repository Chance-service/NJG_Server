package com.guaji.game.module.alliance;

import org.apache.commons.lang.StringUtils;
import org.guaji.app.AppObj;
import org.guaji.db.DBManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Alliance.HPChangeAllianceName;
import com.guaji.game.GsApp;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.ChatManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.AllianceUtil;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 公会改名，增加了能否改名标记，每个公会改名只有一次机会。
 */
public class AllianceChangeNameHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		if (player == null) {
			return true;
		}
		HPChangeAllianceName request = protocol.parseProtocol(HPChangeAllianceName.getDefaultInstance());

		// 获取新的公会名称
		String newName = request.getNewName();
		if (StringUtils.isBlank(newName)) {
			player.sendError(HP.code.ALLIANCE_CHANGE_NAME_C_VALUE, Status.error.ALLIANCE_NAME_NULL);
			return true;
		}

		// 检测公会的合法性
		int allianceId = player.getPlayerData().getPlayerAllianceEntity().getAllianceId();
		AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
		if (allianceEntity == null) {
			// 公会不存在
			player.sendError(HP.code.ALLIANCE_CHANGE_NAME_C_VALUE, Status.error.ALLIANCE_NONEXISTENT);
			return true;
		}

		// 检测是否可以改名字
		if (!allianceEntity.canChangeName()) {
			player.sendError(HP.code.ALLIANCE_CHANGE_NAME_C_VALUE, Status.error.ALLIANCE_CREATE_NAME_ERROR);
			return true;
		}

		// 检查公会名格式否合法
		if (!AllianceUtil.checkName(newName)) {
			player.sendError(HP.code.ALLIANCE_CHANGE_NAME_C_VALUE, Status.error.ALLIANCE_CREATE_NAME_ERROR);
			return true;
		}

		// 公会名是否已存在
		if (AllianceManager.getInstance().isExistName(newName)) {
			player.sendError(HP.code.ALLIANCE_CHANGE_NAME_C_VALUE, Status.error.ALLIANCE_NAME_EXIST_ERROR);
			return true;
		}

		// 只有公会会长才可以改
		if (player.getPlayerData().getPlayerAllianceEntity().getPostion() != GsConst.Alliance.ALLIANCE_POS_MAIN) {
			player.sendError(HP.code.ALLIANCE_CHANGE_NAME_C_VALUE, Status.error.ALLIANCE_NO_MAIN);
			return true;
		}

		// 获取老名字
		String oldName = allianceEntity.getName();

		// 删除之前的名字缓存
		AllianceManager.getInstance().removeNameFromExist(oldName);

		// 添加存在名字到缓存
		AllianceManager.getInstance().addName(newName);

		// 设置新名字
		allianceEntity.setName(newName);

		// 修改改名状态
		allianceEntity.setCanChangeName(false);

		// 更新公会信息
		DBManager.getInstance().update(allianceEntity);

		// 发送玩家公会聊天标记
		GameUtil.sendAllianceChatTag(player);

		// 返回修改成功包
		HPChangeAllianceName.Builder response = HPChangeAllianceName.newBuilder();
		response.setNewName(newName);
		player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_CHANGE_NAME_S_VALUE, response));

		// 推送公会同步信息
		player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_CREATE_S_VALUE,
		        AllianceManager.getInstance().getAllianceInfo(allianceEntity, player.getId(), player.getGold())));

		String notice = ChatManager.getMsgJson(SysBasicCfg.getInstance().getChangeAllianceNameNotice(), oldName, newName);
		// 发送世界聊天
		GsApp.getInstance().broadcastChatWorldMsg(null, notice);

		return true;
	}
}
