package com.guaji.game.module;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;

import org.guaji.annotation.MessageHandlerAnno;
import org.guaji.annotation.ProtocolHandlerAnno;
import org.guaji.config.ConfigManager;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;

import com.guaji.game.GsApp;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.bean.GvgOccupyBean;
import com.guaji.game.config.EquipCfg;
import com.guaji.game.config.GvgCfg;
import com.guaji.game.config.GvgCitiesCfg;
import com.guaji.game.config.ItemCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.GvgAllianceEntity;
import com.guaji.game.entity.GvgCityEntity;
import com.guaji.game.entity.GvgCityRewardEntity;
import com.guaji.game.entity.GvgHistoryRankEntity;
import com.guaji.game.entity.GvgLogEntity;
import com.guaji.game.entity.GvgRewardEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.ChatManager;
import com.guaji.game.manager.MailManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.manager.gvg.GvgManager;
import com.guaji.game.manager.gvg.GvgService;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.GroupVsFunction.AllianceRankItem;
import com.guaji.game.protocol.GroupVsFunction.BuyReviveRequest;
import com.guaji.game.protocol.GroupVsFunction.BuyReviveResponse;
import com.guaji.game.protocol.GroupVsFunction.ChangeDefenceOrderRequest;
import com.guaji.game.protocol.GroupVsFunction.ChangeDefenceOrderResponse;
import com.guaji.game.protocol.GroupVsFunction.CityBattleRecord;
import com.guaji.game.protocol.GroupVsFunction.CityInfo;
import com.guaji.game.protocol.GroupVsFunction.CityRewardResponse;
import com.guaji.game.protocol.GroupVsFunction.CityStatus;
import com.guaji.game.protocol.GroupVsFunction.CityTeamRequest;
import com.guaji.game.protocol.GroupVsFunction.CityTeamResponse;
import com.guaji.game.protocol.GroupVsFunction.DeclareBattleRequest;
import com.guaji.game.protocol.GroupVsFunction.DeclareBattleResponse;
import com.guaji.game.protocol.GroupVsFunction.GVGStatus;
import com.guaji.game.protocol.GroupVsFunction.GVGStatusChange;
import com.guaji.game.protocol.GroupVsFunction.GuildInfo;
import com.guaji.game.protocol.GroupVsFunction.GvgConfig;
import com.guaji.game.protocol.GroupVsFunction.GvgRoleInfo;
import com.guaji.game.protocol.GroupVsFunction.MapInfoResponse;
import com.guaji.game.protocol.GroupVsFunction.PlayerRoleListResponse;
import com.guaji.game.protocol.GroupVsFunction.PlayerTeamListResponse;
import com.guaji.game.protocol.GroupVsFunction.RoleStatus;
import com.guaji.game.protocol.GroupVsFunction.SeasonRankingsResponse;
import com.guaji.game.protocol.GroupVsFunction.SendRoleRequest;
import com.guaji.game.protocol.GroupVsFunction.SendRoleResponse;
import com.guaji.game.protocol.GroupVsFunction.TeamInfo;
import com.guaji.game.protocol.GroupVsFunction.TeamNumberPush;
import com.guaji.game.protocol.GroupVsFunction.TeamNumberResponse;
import com.guaji.game.protocol.GroupVsFunction.VitalityRank;
import com.guaji.game.protocol.GroupVsFunction.VitalityRanksResponse;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Mail;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.EquipUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsConst.Alliance;
import com.guaji.game.util.PlayerUtil;

/**
 * GVG功能模块
 */
public class PlayerGvgModule extends PlayerModule {

	/**
	 * 每秒执行
	 */
	private long millisecond = 0L;

	public PlayerGvgModule(Player player) {
		super(player);
	}

	/**
	 * 定时更新
	 */
	@Override
	public boolean onTick() {
		// 循环帧
		if (millisecond > GuaJiTime.getMillisecond()) {
			return true;
		}
		millisecond = GuaJiTime.getMillisecond() + 1000;
		// 清理数据
		GvgRewardEntity rewardEntity = player.getPlayerData().getGvgRewardEntity();
		if (rewardEntity.getRefreshTime() < GuaJiTime.getMillisecond()) {
			rewardEntity.clearReceived();
		}
		return super.onTick();
	}

	@Override
	protected boolean onPlayerLogin() {
		player.getPlayerData().loadGvgRewardEntity();
		return super.onPlayerLogin();
	}

	/**
	 * 配置时间请求
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.GVG_CONFIG_C_VALUE)
	protected void gvgConfig(Protocol hawkProtocol) {
		// 构建返回数据
		GvgConfig.Builder response = GvgConfig.newBuilder();
		response.setDeclareStart(GvgCfg.getInstance().getDeclareStart());
		response.setDeclareEnd(GvgCfg.getInstance().getDeclareEnd());
		response.setBattleStart(GvgCfg.getInstance().getBattleStart());
		response.setBattleEnd(GvgCfg.getInstance().getBattleEnd());
		// 添加距离下次赛季开启剩余时间
		response.setSuplyTime(GvgManager.getInstance().getSuplyTimeNextSeason());
		response.setIsGVGOpen(GvgService.getInstance().isOpeanFunction());
		response.setReviveStartTime(GvgCfg.getInstance().getReviveStart());
		sendProtocol(Protocol.valueOf(HP.code.GVG_CONFIG_S_VALUE, response));
	}

	/**
	 * 地图数据
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.GVG_MAP_INFO_C_VALUE)
	protected void mapInfoLogic(Protocol hawkProtocol) {
		if (!this.isCanInvolved(HP.code.GVG_MAP_INFO_C_VALUE)) {
			return;
		}
		// 城池数据集合
		TreeMap<Integer, GvgCityEntity> cities = GvgService.getInstance().getCities();
		// 构建返回数据
		MapInfoResponse.Builder response = MapInfoResponse.newBuilder();
		// 进行阶段
		GVGStatus gvgStatus = GvgManager.getInstance().getGvgStatus();
		response.setStatus(gvgStatus);
		response.setCurrentTime(GuaJiTime.getMillisecond());
		response.setWaitSuplyTime(GvgService.getInstance().getWaitingSurplus());
		// 城池列表
		for (GvgCityEntity cityEntity : cities.values()) {
			CityInfo.Builder builder = CityInfo.newBuilder();
			builder.setCityId(cityEntity.getCityId());
			// 城池状态
			CityStatus cityStatus = GvgManager.getInstance().getCityStatus(cityEntity, gvgStatus);
			if (cityEntity.getFightbackTime() - GuaJiTime.getMillisecond() > 0) {
				builder.setFightbackTime(cityEntity.getFightbackTime() - GuaJiTime.getMillisecond());
			}
			if (cityStatus == CityStatus.CITY_STATUS_REATTACK) {
				builder.setReAtkGuildId(cityEntity.getMarauderId());
			} else {
				// 宣战者公会数据
				if (cityEntity.getMarauderId() > 0) {
					AllianceEntity alliance = AllianceManager.getInstance().getAlliance(cityEntity.getMarauderId());
					if (alliance != null) {
						GuildInfo.Builder marauder = GuildInfo.newBuilder();
						marauder.setGuildId(cityEntity.getMarauderId());
						marauder.setName(alliance.getName());
						builder.setAtkGuild(marauder);
					}
				}
			}
			builder.setStatus(cityStatus);
			// 持有者公会数据
			if (cityEntity.getHolderId() > 0) {
				AllianceEntity alliance = AllianceManager.getInstance().getAlliance(cityEntity.getHolderId());
				if (alliance != null) {
					GuildInfo.Builder holder = GuildInfo.newBuilder();
					holder.setGuildId(cityEntity.getHolderId());
					holder.setName(alliance.getName());
					builder.setDefGuild(holder);
				}
			}
			builder.setDefTeamNum(cityEntity.getDefenderList().size());
			builder.setIsReAtk(cityEntity.isFightback());
			// 添加城池数据
			response.addCitys(builder);
		}
		// 城池战况LOG
		BlockingQueue<GvgLogEntity> gvgLogs = GvgService.getInstance().getGvgLogs();
		for (GvgLogEntity gvgLog : gvgLogs) {
			long time = gvgLog.getCreateTime().getTime();
			if (time + GvgCfg.getInstance().getGvgLogDay() <= GuaJiTime.getMillisecond()) {
				GvgLogEntity _gvgLog = gvgLogs.remove();
				_gvgLog.delete(false);
				continue;
			}
			// 构建数据
			CityBattleRecord.Builder builder = CityBattleRecord.newBuilder();
			builder.setCityId(gvgLog.getCityId());
			builder.setAtkName(gvgLog.getAttackerName());
			if (gvgLog.getDefenderName() != null) {
				builder.setDefName(gvgLog.getDefenderName());
			}
			builder.setIsAtkWin(gvgLog.getResult());
			builder.setIsReAtk(gvgLog.isFightback());
			response.addRecords(builder);
		}
		sendProtocol(Protocol.valueOf(HP.code.GVG_MAP_INFO_S_VALUE, response));
	}

	/**
	 * 攻防队伍数信息
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.TEAM_NUMBER_C_VALUE)
	protected void teamNumberLogic(Protocol hawkProtocol) {
		if (!this.isCanInvolved(HP.code.TEAM_NUMBER_C_VALUE)) {
			return;
		}
		TeamNumberResponse.Builder response = TeamNumberResponse.newBuilder();
		long millisecond = GuaJiTime.getMillisecond();

		// 城池数据集合
		TreeMap<Integer, GvgCityEntity> cities = GvgService.getInstance().getCities();
		// 城池列表
		for (GvgCityEntity cityEntity : cities.values()) {
			TeamNumberPush.Builder builder = TeamNumberPush.newBuilder();
			builder.setCityId(cityEntity.getCityId());
			builder.setAtkNumbers(cityEntity.getAttackerList().size());
			if (cityEntity.getHolderId() > 0) {
				builder.setDefNumbers(cityEntity.getDefenderList().size());
			}
			builder.setCurrentTime(millisecond);
			response.addTeams(builder);
		}
		sendProtocol(Protocol.valueOf(HP.code.TEAM_NUMBER_S_VALUE, response));
	}

	/**
	 * 查看今天的排行信息
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.TODAY_VITALITY_RANKS_C_VALUE)
	protected void todayRanksLogic(Protocol hawkProtocol) {
		// 功能开启判定
		if (!GvgService.getInstance().isOpeanFunction() || !GvgManager.getInstance().isIsOpenFunction()) {
			sendError(HP.code.TODAY_VITALITY_RANKS_C_VALUE, Status.error.GVG_FUNCTION_CLOSE_VALUE);
			return;
		}
		// 构建返回数据
		VitalityRanksResponse.Builder response = VitalityRanksResponse.newBuilder();
		ConcurrentSkipListSet<GvgAllianceEntity> alliances = GvgService.getInstance().getAlliances();
		Iterator<GvgAllianceEntity> iterator = alliances.iterator();
		int index = 1;
		while (iterator.hasNext()) {
			
			if (index > GvgCfg.getInstance().getRankNumber()) {
				break;
			}
			GvgAllianceEntity alliance = iterator.next();
			VitalityRank.Builder rankBuilder = VitalityRank.newBuilder();
			rankBuilder.setRank(index);
			rankBuilder.setId(alliance.getAllianceId());
			// 公会数据
			AllianceEntity allianceData = AllianceManager.getInstance().getAlliance(alliance.getAllianceId());
			if (allianceData == null) {
				continue;
			}
			rankBuilder.setLevel(allianceData.getLevel());
			rankBuilder.setName(allianceData.getName());
			rankBuilder.setValue(alliance.getAddCount());
			rankBuilder.setMasterName(allianceData.getPlayerName());
		
			// 添加排行数据
			response.addRanks(rankBuilder);
			
			index++;
		}
		response.setRankCount(response.getRanksCount());
		sendProtocol(Protocol.valueOf(HP.code.TODAY_VITALITY_RANKS_S_VALUE, response));
	}

	/**
	 * 查看上季排名信息
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.YESTERDAY_VITALITY_RANKS_C_VALUE)
	protected void yesterdayRanksLogic(Protocol hawkProtocol) {
		/*
		 * if (!this.isCanInvolved(HP.code.YESTERDAY_VITALITY_RANKS_C_VALUE)) { return;
		 * }
		 */
		SeasonRankingsResponse.Builder response = SeasonRankingsResponse.newBuilder();
		// 排名信息
		List<GvgHistoryRankEntity> historyRanks = GvgService.getInstance().getHistorySeaonRank();
		for (GvgHistoryRankEntity historyRank : historyRanks) {

			AllianceRankItem.Builder rankBuilder = AllianceRankItem.newBuilder();
			rankBuilder.setHoldCityInfo(historyRank.getHoldCityInfo());
			rankBuilder.setId(historyRank.getAllianceId());
			rankBuilder.setName(historyRank.getAllianceName());
			rankBuilder.setMasterName(historyRank.getMasterName());
			rankBuilder.setRank(historyRank.getRank());
			rankBuilder.setScore(historyRank.getScore());

			rankBuilder.setLevel(String.valueOf(historyRank.getLevel()));
			response.addRanks(rankBuilder);
		}

		response.setRankCount(response.getRanksCount());
		sendProtocol(Protocol.valueOf(HP.code.YESTERDAY_VITALITY_RANKS_S_VALUE, response));

	}

	/**
	 * 城池宣战
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.DECLARE_BATTLE_C_VALUE)
	protected void declareBattleLogic(Protocol hawkProtocol) {
		if (!this.isCanInvolved(HP.code.DECLARE_BATTLE_C_VALUE)) {
			return;
		}

		// 发起宣战权判定
		PlayerAllianceEntity allianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		// 城池侵略者公会数据
		AllianceEntity marauderAlliance = AllianceManager.getInstance().getAlliance(allianceEntity.getAllianceId());
		if (marauderAlliance == null) {
			sendError(HP.code.DECLARE_BATTLE_C_VALUE, Status.error.ALLIANCE_NONEXISTENT_VALUE);
			return;
		}

		// 检查公会占领城池
		int hasCityType = GvgManager.getInstance().allianceHasCityType(allianceEntity.getAllianceId());
		if (hasCityType == 0) {
			sendError(HP.code.DECLARE_BATTLE_C_VALUE, Status.error.ALLIANCE_NONEXISTENT_VALUE);
			return;
		}

		int result = GvgManager.getInstance().declareBattlePower(allianceEntity, player.getId(), false);
		if (result != 0) {
			sendError(HP.code.DECLARE_BATTLE_C_VALUE, result);
			return;
		}

		// 宣战次数判定
		GvgAllianceEntity gvgEntity = null;
		ConcurrentSkipListSet<GvgAllianceEntity> alliances = GvgService.getInstance().getAlliances();
		Iterator<GvgAllianceEntity> iterator = alliances.iterator();
		while (iterator.hasNext()) {
			// 获得宣战权公会是否有玩家所在的公会
			GvgAllianceEntity entity = iterator.next();
			if (entity.getAllianceId() == allianceEntity.getAllianceId()) {
				gvgEntity = entity;
				break;
			}
		}
		if (gvgEntity == null) {
			// 构建记录数据
			GvgAllianceEntity entity = GvgAllianceEntity.valueOf(allianceEntity.getAllianceId(), 0);
			GvgService.getInstance().updateAlliances(entity);
			gvgEntity = entity;
		}

		if (hasCityType == 2)// 正常宣战
		{
			// 进行阶段
			GVGStatus gvgStatus = GvgManager.getInstance().getGvgStatus();
			if (gvgStatus != GVGStatus.GVG_STATUS_PREPARE) {
				sendError(HP.code.DECLARE_BATTLE_C_VALUE, Status.error.NOT_DECLARED_BATTEL_STATUS_VALUE);
				return;
			}

		}

		result = GvgManager.getInstance().declareBattleTimes(gvgEntity);
		if (result != 0) {
			sendError(HP.code.DECLARE_BATTLE_C_VALUE, result);
			return;
		}

		// 解析数据
		DeclareBattleRequest request = hawkProtocol.parseProtocol(DeclareBattleRequest.getDefaultInstance());
		// 宣战城池判断
		result = GvgManager.getInstance().declareBattleCity(allianceEntity.getAllianceId(), request.getCityId());
		if (result != 0) {
			sendError(HP.code.DECLARE_BATTLE_C_VALUE, result);
			return;
		}
		if (!GvgManager.getInstance().getTodayPlayerIds().contains(player.getId()))
			GvgManager.getInstance().getTodayPlayerIds().add(player.getId());

		// 数据更新
		gvgEntity.updateDeclareTimes();

		BehaviorLogger.log4Service(player.getId(), Source.USER_OPERATION, Action.GVG_DECLARE_BATTLE,
				Params.valueOf("cityId", request.getCityId()));
		// 构建返回数据
		this.sendDeclareBattle(HP.code.DECLARE_BATTLE_S_VALUE, request.getCityId(), marauderAlliance);
	}

	/**
	 * 城池反攻宣战
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.FIGHT_BACK_C_VALUE)
	protected void declareFightbackLogic(Protocol hawkProtocol) {
		/*
		 * 
		 * if (!this.isCanInvolved(HP.code.FIGHT_BACK_C_VALUE)) { return; } // 进行阶段
		 * GVGStatus gvgStatus = GvgManager.getInstance().getGvgStatus(); if (gvgStatus
		 * != GVGStatus.GVG_STATUS_FIGHTING) { sendError(HP.code.FIGHT_BACK_C_VALUE,
		 * Status.error.NOT_STATUS_FIGHTING_VALUE); return; } // 发起宣战权判定
		 * PlayerAllianceEntity allianceEntity =
		 * player.getPlayerData().getPlayerAllianceEntity(); // 城池侵略者公会数据 AllianceEntity
		 * marauderAlliance =
		 * AllianceManager.getInstance().getAlliance(allianceEntity.getAllianceId()); if
		 * (marauderAlliance == null) { sendError(HP.code.FIGHT_BACK_C_VALUE,
		 * Status.error.ALLIANCE_NONEXISTENT_VALUE); return; } int result =
		 * GvgManager.getInstance().declareBattlePower(allianceEntity, player.getId(),
		 * true); if (result != 0) { sendError(HP.code.FIGHT_BACK_C_VALUE, result);
		 * return; } // 解析数据 DeclareBattleRequest request =
		 * hawkProtocol.parseProtocol(DeclareBattleRequest.getDefaultInstance());
		 * GvgCityEntity cityEntity =
		 * GvgService.getInstance().getCityEntity(request.getCityId()); synchronized
		 * (cityEntity) { if (cityEntity.getFightbackTime() > 0 &&
		 * cityEntity.getFightbackTime() < GuaJiTime.getMillisecond()) {
		 * sendError(HP.code.FIGHT_BACK_C_VALUE, Status.error.FIGHTBACK_OVERTIME_VALUE);
		 * return; } // 不能对自己的城池宣战 if (allianceEntity.getAllianceId() ==
		 * cityEntity.getHolderId()) { sendError(HP.code.FIGHT_BACK_C_VALUE,
		 * Status.error.CANNOT_DECLARED_WAR_VALUE); return; }
		 * cityEntity.updateIsFightBack(); } BehaviorLogger.log4Service(player.getId(),
		 * Source.USER_OPERATION, Action.GVG_DECLARE_FIGHTBACK, Params.valueOf("cityId",
		 * request.getCityId())); // 构建返回数据
		 * this.sendDeclareBattle(HP.code.FIGHT_BACK_S_VALUE, request.getCityId(),
		 * marauderAlliance);
		 */

	}

	/**
	 * 攻击佣兵派遣
	 *
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.CITY_ATTACKER_C_VALUE)
	protected void sendAttackerLogic(Protocol hawkProtocol) {
		if (!this.isCanInvolved(HP.code.CITY_ATTACKER_C_VALUE)) {
			return;
		}
		// 进行阶段
		GVGStatus gvgStatus = GvgManager.getInstance().getGvgStatus();
		if (gvgStatus != GVGStatus.GVG_STATUS_FIGHTING) {
			sendError(HP.code.CITY_ATTACKER_C_VALUE, Status.error.NOT_STATUS_FIGHTING_VALUE);
			return;
		}
		if (GvgManager.getInstance().isNullSnapshpt(player.getId())) {
			sendError(HP.code.CITY_ATTACKER_C_VALUE, Status.error.NOT_CAN_JOIN_VALUE);
			return;
		}
		// 数据解析
		SendRoleRequest request = hawkProtocol.parseProtocol(SendRoleRequest.getDefaultInstance());
		// 数据校验
		if (request.getRoleIdsList().size() <= 0) {
			sendError(HP.code.CITY_ATTACKER_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		if (request.getRoleIdsList().size() > GvgCfg.getInstance().getRoleMax()) {
			sendError(HP.code.CITY_ATTACKER_C_VALUE, Status.error.SEND_ROLDE_LIMIT_VALUE);
			return;
		}
		// 玩家公会数据
		PlayerAllianceEntity allianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		if (allianceEntity.getAllianceId() == 0) {
			sendError(HP.code.CITY_ATTACKER_C_VALUE, Status.error.ALLIANCE_NO_JOIN_VALUE);
			return;
		}
		GvgCityEntity cityEntity = GvgService.getInstance().getCityEntity(request.getCityId());
		if (cityEntity == null || cityEntity.getMarauderId() != allianceEntity.getAllianceId()) {
			sendError(HP.code.CITY_ATTACKER_C_VALUE, Status.error.NOT_DECLARED_WAR_VALUE);
			return;
		}

		// 请求驻屯列表小于最大驻屯数
		if (request.getRoleIdsList().size() < GvgCfg.getInstance().getRoleMax()) {

			if (GvgManager.getInstance().getPlayerLimitIds().contains(player.getId())) {
				sendError(HP.code.CITY_ATTACKER_C_VALUE, Status.error.NOT_CAN_EXCEED_ONETEAM_LESSTHANTHREE_VALUE);
				return;
			}
			GvgManager.getInstance().getPlayerLimitIds().add(player.getId());
		}

		if (cityEntity.getFightbackTime() > 0) {
			if (!cityEntity.isFightback()) {
				sendError(HP.code.CITY_ATTACKER_C_VALUE, Status.error.NOT_FIGHTBACK_VALUE);
				return;
			}
			if (cityEntity.getFightbackTime() > GuaJiTime.getMillisecond()) {
				sendError(HP.code.CITY_ATTACKER_C_VALUE, Status.error.FIGHTBACK_PROTECT_VALUE);
				return;
			}
		}
		this.sendRoleLogic(request, cityEntity.getMarauderId(), HP.code.CITY_ATTACKER_C_VALUE,
				GvgCfg.getInstance().getAttackPower());
	}

	/**
	 * 防御佣兵派遣
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.CITY_DEFENDER_C_VALUE)
	protected void sendDefenderLogic(Protocol hawkProtocol) {
		if (!this.isCanInvolved(HP.code.CITY_DEFENDER_C_VALUE)) {
			return;
		}
		if (GvgManager.getInstance().isNullSnapshpt(player.getId())) {
			sendError(HP.code.CITY_DEFENDER_C_VALUE, Status.error.NOT_CAN_JOIN_VALUE);
			return;
		}
		//结束期不能再宣战
		GVGStatus gvgStatus = GvgManager.getInstance().getGvgStatus();
		if (gvgStatus == GVGStatus.GVG_STATUS_ENDING||gvgStatus==GVGStatus.GVG_STATUS_AWARD) {
			sendError(HP.code.CITY_DEFENDER_C_VALUE, Status.error.NOT_STATION_SELF_CITY_VALUE);
			return;
		}
		
		// 数据解析
		SendRoleRequest request = hawkProtocol.parseProtocol(SendRoleRequest.getDefaultInstance());
		// 数据校验
		if (request.getRoleIdsList().size() <= 0) {
			sendError(HP.code.CITY_DEFENDER_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		if (request.getRoleIdsList().size() > GvgCfg.getInstance().getRoleMax()) {
			sendError(HP.code.CITY_DEFENDER_C_VALUE, Status.error.SEND_ROLDE_LIMIT_VALUE);
			return;
		}
		// 玩家公会数据
		PlayerAllianceEntity allianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		if (allianceEntity.getAllianceId() == 0) {
			sendError(HP.code.CITY_DEFENDER_C_VALUE, Status.error.ALLIANCE_NO_JOIN_VALUE);
			return;
		}
		GvgCityEntity cityEntity = GvgService.getInstance().getCityEntity(request.getCityId());
		if (cityEntity == null || cityEntity.getHolderId() != allianceEntity.getAllianceId()) {
			sendError(HP.code.CITY_ATTACKER_C_VALUE, Status.error.NOT_OCCUPY_CITY_VALUE);
			return;
		}
		
		// 请求驻屯列表小于最大驻屯数
		if (request.getRoleIdsList().size() < GvgCfg.getInstance().getRoleMax()) {

			if (GvgManager.getInstance().getPlayerDefenderLimitIds().contains(player.getId())) {
				sendError(HP.code.CITY_DEFENDER_C_VALUE, Status.error.NOT_CAN_EXCEED_ONETEAM_LESSTHANTHREE_VALUE);
				return;
			}
			GvgManager.getInstance().getPlayerDefenderLimitIds().add(player.getId());
		}
		// 数据
		this.sendRoleLogic(request, cityEntity.getHolderId(), HP.code.CITY_DEFENDER_C_VALUE,
				GvgCfg.getInstance().getDefendPower());
	}

	/**
	 * 查看进攻队伍列表
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.VIEW_ATTACK_TEAM_C_VALUE)
	protected void viewAttackTeam(Protocol hawkProtocol) {
		CityTeamRequest request = hawkProtocol.parseProtocol(CityTeamRequest.getDefaultInstance());
		// 城池数据
		GvgCityEntity cityEntity = GvgService.getInstance().getCityEntity(request.getCityId());
		if (cityEntity == null) {
			sendError(HP.code.VIEW_ATTACK_TEAM_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		synchronized (cityEntity) {
			// 构建返回数据
			CityTeamResponse.Builder response = CityTeamResponse.newBuilder();
			response.setCityId(request.getCityId());
			int index = 1;
			for (GvgOccupyBean occupyBean : cityEntity.getAttackerList()) {
				PlayerSnapshotInfo.Builder snapShotInfo = GvgManager.getInstance()
						.getSnapshot(occupyBean.getPlayerId());
				if (snapShotInfo == null) {
					snapShotInfo = SnapShotManager.getInstance().getPlayerSnapShot(occupyBean.getPlayerId());
				}
				if (snapShotInfo == null) {
					continue;
				}
				TeamInfo.Builder teamInfo = this.builderTeamInfo(snapShotInfo, index, occupyBean);
				if (teamInfo != null) {
					response.addTeams(teamInfo);
				}
			}
			sendProtocol(Protocol.valueOf(HP.code.VIEW_ATTACK_TEAM_S_VALUE, response));
		}
	}

	/**
	 * 查看驻守队伍列表
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.VIEW_DEFENDER_TEAM_C_VALUE)
	protected void viewDefenderTeam(Protocol hawkProtocol) {
		CityTeamRequest request = hawkProtocol.parseProtocol(CityTeamRequest.getDefaultInstance());
		// 城池数据
		GvgCityEntity cityEntity = GvgService.getInstance().getCityEntity(request.getCityId());
		if (cityEntity == null) {
			sendError(HP.code.VIEW_ATTACK_TEAM_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		// 构建返回数据
		CityTeamResponse.Builder response = CityTeamResponse.newBuilder();
		response.setCityId(request.getCityId());
		int index = 0;
		synchronized (cityEntity) {
			for (GvgOccupyBean occupyBean : cityEntity.getDefenderList()) {
				PlayerSnapshotInfo.Builder snapShotInfo = GvgManager.getInstance()
						.getSnapshot(occupyBean.getPlayerId());
				if (snapShotInfo == null) {
					snapShotInfo = SnapShotManager.getInstance().getPlayerSnapShot(occupyBean.getPlayerId());
				}
				if (snapShotInfo == null) {
					continue;
				}
				if (snapShotInfo.getAllianceInfo().getAllianceId() != cityEntity.getHolderId()) {
					Log.gvgLog("查看驻屯队伍，不在公会玩家：      " + snapShotInfo.getPlayerId());
					continue;
				}
				TeamInfo.Builder teamInfo = this.builderTeamInfo(snapShotInfo, index, occupyBean);
				if (teamInfo != null) {
					response.addTeams(teamInfo);
					index++;
				}
			}
		}
		sendProtocol(Protocol.valueOf(HP.code.VIEW_DEFENDER_TEAM_S_VALUE, response));
	}

	/**
	 * 驻守方顺序调整
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.DEFENDER_REORDER_C_VALUE)
	protected void defenderReorder(Protocol hawkProtocol) {
		if (!this.isCanInvolved(HP.code.DEFENDER_REORDER_C_VALUE)) {
			return;
		}
		// 解析数据
		ChangeDefenceOrderRequest request = hawkProtocol.parseProtocol(ChangeDefenceOrderRequest.getDefaultInstance());
		// 取城池数据
		GvgCityEntity cityEntity = GvgService.getInstance().getCityEntity(request.getCityId());
		if (cityEntity == null) {
			sendError(HP.code.DEFENDER_REORDER_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		// 返回数据包
		ChangeDefenceOrderResponse.Builder response = ChangeDefenceOrderResponse.newBuilder();
		synchronized (cityEntity) {
			// 玩家公会数据
			PlayerAllianceEntity allianceEntity = player.getPlayerData().getPlayerAllianceEntity();
			if (cityEntity.getHolderId() != allianceEntity.getAllianceId()) {
				sendError(HP.code.DEFENDER_REORDER_C_VALUE, Status.error.NOT_MY_CITY_VALUE);
				return;
			}
			// 权限判定
			if (allianceEntity.getPostion() != Alliance.ALLIANCE_POS_MAIN
					&& allianceEntity.getPostion() != Alliance.ALLIANCE_POS_COPYMAIN) {
				sendError(HP.code.DEFENDER_REORDER_C_VALUE, Status.error.ALLIANCE_NO_MAIN_VALUE);
				return;
			}
			// 参数校验
			List<GvgOccupyBean> occupyList = cityEntity.getDefenderList();
			if (request.getOldTeamId() >= occupyList.size() || request.getNewTeamId() >= occupyList.size()) {
				response.setResult(0);
				sendProtocol(Protocol.valueOf(HP.code.DEFENDER_REORDER_S_VALUE, response));
				return;
			}
			// 战斗阶段
			GVGStatus gvgStatus = GvgManager.getInstance().getGvgStatus();
			if (gvgStatus == GVGStatus.GVG_STATUS_FIGHTING
					&& (request.getOldTeamId() == 0 || request.getNewTeamId() == 0)) {
				sendError(HP.code.DEFENDER_REORDER_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
				return;
			}
			// 调整顺序
			GvgOccupyBean oldOccupy = occupyList.get(request.getOldTeamId());
			GvgOccupyBean newOccupy = occupyList.get(request.getNewTeamId());
			occupyList.set(request.getOldTeamId(), newOccupy);
			occupyList.set(request.getNewTeamId(), oldOccupy);
		}
		response.setResult(1);
		sendProtocol(Protocol.valueOf(HP.code.DEFENDER_REORDER_S_VALUE, response));
	}

	/**
	 * 查看账户佣兵列表
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.VIEW_ROLE_TEAM_C_VALUE)
	protected void viewRoleTeam(Protocol hawkProtocol) {
		if (!this.isCanInvolved(HP.code.VIEW_ROLE_TEAM_C_VALUE)) {
			return;
		}
		// 初始化佣兵信息列表对象
		PlayerRoleListResponse.Builder response = PlayerRoleListResponse.newBuilder();
		// 玩家公会数据
		PlayerAllianceEntity allianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		if (allianceEntity.getAllianceId() == 0) {
			sendError(HP.code.VIEW_ROLE_TEAM_C_VALUE, Status.error.ALLIANCE_NO_JOIN_VALUE);
			return;
		}
		// 缓存统计使用中的角色ID
		List<Integer> attackerIds = new ArrayList<Integer>();
		List<Integer> defenderIds = new ArrayList<Integer>();
		this.statisticsRoleId(allianceEntity.getAllianceId(), attackerIds, defenderIds);
		// 构建佣兵数据包
		List<RoleEntity> roleEntities = player.getPlayerData().getRoleEntities();
		for (RoleEntity roleEntity : roleEntities) {
			// 佣兵激活判定
			if (roleEntity.getRoleState() == Const.RoleActiviteState.NOT_ACTIVITE_VALUE
					|| roleEntity.getType() == GsConst.RoleType.MAIN_ROLE) {
				continue;
			}
			if (!GvgManager.getInstance().isCanJoinOfRole(player.getId(), roleEntity.getId())) {
				continue;
			}
//			if (!GvgManager.getInstance().isHaveSnapshot()) {
//				if (roleEntity.isHide()) {
//					continue;
//				}
//			}
			// 体力值判断
			this.refreshRolePower(roleEntity);
			// 构建账号简介数据
			GvgRoleInfo.Builder roleInfo = null;
			if (defenderIds.contains(roleEntity.getId())) {
				// 防守状态
				roleInfo = builderRoleInfo(RoleStatus.DEFENDER_STATUS_DEF, roleEntity);
			} else if (attackerIds.contains(roleEntity.getId())) {
				// 进攻状态
				roleInfo = builderRoleInfo(RoleStatus.ATTACKER_STATUS_DEF, roleEntity);
			} else {
				// 闲置状态
				roleInfo = builderRoleInfo(RoleStatus.ROLE_STATUS_NORMAL, roleEntity);
			}
			response.addRoles(roleInfo);
		}

		if (response.getRolesCount() > 0) {
			sendProtocol(Protocol.valueOf(HP.code.VIEW_ROLE_TEAM_S_VALUE, response));
		} else {
			sendError(HP.code.VIEW_ROLE_TEAM_C_VALUE, Status.error.JOIN_ALLIANCE_AFTER_GVG_VALUE);
		}
	}

	/**
	 * 查看账户所有报名队伍列表
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.VIEW_TEAM_C_VALUE)
	protected void viewTeamList(Protocol hawkProtocol) {
		if (!this.isCanInvolved(HP.code.VIEW_TEAM_C_VALUE)) {
			return;
		}
		// 玩家公会数据
		PlayerAllianceEntity allianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		if (allianceEntity.getAllianceId() == 0) {
			sendError(HP.code.VIEW_TEAM_C_VALUE, Status.error.ALLIANCE_NO_JOIN_VALUE);
			return;
		}
		// 城池数据集合
		final TreeMap<Integer, GvgCityEntity> cities = GvgService.getInstance().getCities();
		// 初始化返回数据对象
		PlayerTeamListResponse.Builder response = PlayerTeamListResponse.newBuilder();
		for (GvgCityEntity city : cities.values()) {
			TeamInfo.Builder teamInfo = TeamInfo.newBuilder();
			teamInfo.setCityId(city.getCityId());
			List<GvgOccupyBean> roleLists = new ArrayList<GvgOccupyBean>();
			// 己方城池防守队列
			if (city.getHolderId() == allianceEntity.getAllianceId()) {
				roleLists.addAll(city.getDefenderList());
			}
			// 进攻城池进攻队列
			if (city.getMarauderId() == allianceEntity.getAllianceId()) {
				roleLists.addAll(city.getAttackerList());
			}
			if (roleLists.size() <= 0) {
				continue;
			}
			// 构建数据
			for (GvgOccupyBean occupyBean : roleLists) {
				teamInfo.clearRoleIds();
				if (occupyBean.getPlayerId() != player.getId()) {
					continue;
				}
				for (RoleEntity roleEntity : player.getPlayerData().getRoleEntities()) {
					if (roleEntity.getRoleState() == Const.RoleActiviteState.NOT_ACTIVITE_VALUE
							|| roleEntity.getType() == GsConst.RoleType.MAIN_ROLE) {
						continue;
					}
					if (occupyBean.getRoleIds().contains(roleEntity.getId())) {
						teamInfo.addRoleIds(roleEntity.getItemId());
					}
				}
				response.addTeams(teamInfo);
			}
		}
		response.setNumber(response.getTeamsCount());
		sendProtocol(Protocol.valueOf(HP.code.VIEW_TEAM_S_VALUE, response));
	}

	/**
	 * 城池奖励数据展示
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.CITY_REWARD_SHOW_C_VALUE)
	protected void cityRewardShow(Protocol hawkProtocol) {
		if (!this.isCanInvolved(HP.code.CITY_REWARD_SHOW_C_VALUE)) {
			return;
		}
		// 账号公会数据
		PlayerAllianceEntity allianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		if (allianceEntity.getAllianceId() == 0) {
			sendError(HP.code.CITY_REWARD_SHOW_C_VALUE, Status.error.ALLIANCE_NO_JOIN_VALUE);
			return;
		}
		// 构建返回数据包
		CityRewardResponse.Builder response = CityRewardResponse.newBuilder();
		GvgCityRewardEntity rewardEntity = GvgService.getInstance().getCityReward(allianceEntity.getAllianceId());
		if (rewardEntity == null) {
			response.setReward("-1");
		} else {
			List<Integer> cityIdList = rewardEntity.getCityIdList();
			GvgRewardEntity gvgReward = player.getPlayerData().getGvgRewardEntity();
			List<Integer> receivedList = gvgReward.getReceivedList();
			AwardItems awardItems = new AwardItems();
			for (Integer cityId : cityIdList) {
				// 数据填充
				if (!receivedList.contains(0)) {
					GvgCitiesCfg config = ConfigManager.getInstance().getConfigByKey(GvgCitiesCfg.class, cityId);
					if (config != null && config.getItemInfos() != null) {
						awardItems.addItemInfos(config.getItemInfos());
					}
				}
				// 城池数据
				if (receivedList.contains(cityId)) {
					response.addRewardedCityIds(cityId);
					continue;
				}
				response.addRewardingCityIds(cityId);
			}
			response.setReward(awardItems.toString());
		}
		sendProtocol(Protocol.valueOf(HP.code.CITY_REWARD_SHOW_S_VALUE, response));
	}

	/**
	 * 领取城池税收奖励
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.GET_CITY_REWARD_C_VALUE)
	protected void getCityRewaed(Protocol hawkProtocol) {
		if (!this.isCanInvolved(HP.code.GET_CITY_REWARD_C_VALUE)) {
			return;
		}
		// 奖励是否领取过
		GvgRewardEntity gvgReward = player.getPlayerData().getGvgRewardEntity();
		List<Integer> receivedList = gvgReward.getReceivedList();
		if (receivedList.contains(0)) {
			sendError(HP.code.GET_CITY_REWARD_C_VALUE, Status.error.GIFT_REWARDED_VALUE);
			return;
		}
		// 账号公会数据
		PlayerAllianceEntity allianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		if (allianceEntity.getAllianceId() == 0) {
			sendError(HP.code.GET_CITY_REWARD_C_VALUE, Status.error.ALLIANCE_NO_JOIN_VALUE);
			return;
		}
		GvgCityRewardEntity rewardEntity = GvgService.getInstance().getCityReward(allianceEntity.getAllianceId());
		if (rewardEntity == null) {
			sendError(HP.code.GET_CITY_REWARD_C_VALUE, Status.error.NOT_CITY_REWARD_VALUE);
			return;
		}
		// 奖励缓存对象
		AwardItems awardItems = new AwardItems();
		// 宝箱等级计算
		final List<Integer> cityIdList = rewardEntity.getCityIdList();
		for (Integer cityId : cityIdList) {
			GvgCitiesCfg config = ConfigManager.getInstance().getConfigByKey(GvgCitiesCfg.class, cityId);
			if (config == null) {
				continue;
			}
			if (config.getItemInfos() == null) {
				continue;
			}
			// 城池默认奖励
			awardItems.addItemInfos(config.getItemInfos());
		}
		// 发奖更新数据
		awardItems.rewardTakeAffectAndPush(player, Action.GVG_CITY_REWAED, 1);
		gvgReward.updateReceived(0);
	}

	/**
	 * 领取城池宝箱奖励
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.GET_CITY_BOX_C_VALUE)
	protected void getCityBox(Protocol hawkProtocol) {

		/*
		 * if (!this.isCanInvolved(HP.code.GET_CITY_BOX_C_VALUE)) { return; } // 账号公会数据
		 * PlayerAllianceEntity allianceEntity =
		 * player.getPlayerData().getPlayerAllianceEntity(); if
		 * (allianceEntity.getAllianceId() == 0) {
		 * sendError(HP.code.GET_CITY_BOX_C_VALUE, Status.error.ALLIANCE_NO_JOIN_VALUE);
		 * return; } GvgCityRewardEntity rewardEntity =
		 * GvgService.getInstance().getCityReward(allianceEntity.getAllianceId()); if
		 * (rewardEntity == null) { sendError(HP.code.GET_CITY_BOX_C_VALUE,
		 * Status.error.NOT_CITY_REWARD_VALUE); return; } // 解析数据 RewardRequest request
		 * = hawkProtocol.parseProtocol(RewardRequest.getDefaultInstance()); final
		 * List<Integer> cityIdList = rewardEntity.getCityIdList(); if
		 * (!cityIdList.contains(request.getCityId())) {
		 * sendError(HP.code.GET_CITY_BOX_C_VALUE, Status.error.NOT_CITY_REWARD_VALUE);
		 * return; } // 奖励是否领取过 GvgRewardEntity gvgReward =
		 * player.getPlayerData().getGvgRewardEntity(); List<Integer> receivedList =
		 * gvgReward.getReceivedList(); if (receivedList.contains(request.getCityId()))
		 * { sendError(HP.code.GET_CITY_BOX_C_VALUE, Status.error.GIFT_REWARDED_VALUE);
		 * return; } // 奖励缓存对象 AwardItems awardItems = new AwardItems(); // 宝箱等级计算
		 * GvgCitiesCfg config =
		 * ConfigManager.getInstance().getConfigByKey(GvgCitiesCfg.class,
		 * request.getCityId()); int boxLevel = config.getBoxLevel(); if (boxLevel == 0)
		 * { sendError(HP.code.GET_CITY_REWARD_C_VALUE,
		 * Status.error.NOT_CITY_REWARD_VALUE); return; } // 取宝箱配置数据 GvgBoxCfg boxConfig
		 * = ConfigManager.getInstance().getConfigByKey(GvgBoxCfg.class, boxLevel);
		 * List<DropItems.Item> dropItems = boxConfig.getDropItems().calcDrop(); //
		 * 自动售出率 Integer sellRatio =
		 * ActivityUtil.getEquipSellActivity(player.getPlayerData().getPlayerEntity().
		 * getCreateTime()); // 最大装备数量限制 int maxEquipCount =
		 * EquipUtil.getEmptyEquipSlotCount(player); for (DropItems.Item item :
		 * dropItems) { ItemInfo itemInfo = null; // 是否从奖励组获得 if (item.id <= 0) {
		 * itemInfo = AwardUtil.randomDrop(item.getType());
		 * itemInfo.setQuantity(itemInfo.getQuantity() * item.getCount()); } else {
		 * itemInfo = new ItemInfo(item.getType(), item.getId(), item.getCount()); } //
		 * 物品类型计算 int itemType = GameUtil.convertToStandardItemType(itemInfo.getType())
		 * / GsConst.ITEM_TYPE_BASE; // 装备掉落 if (itemType ==
		 * Const.itemType.PLAYER_ATTR_VALUE || itemType == Const.itemType.SOUL_VALUE) {
		 * awardItems.addItem(itemInfo); } else if (itemType ==
		 * Const.itemType.EQUIP_VALUE) { maxEquipCount = this.equipAward(awardItems,
		 * itemInfo, maxEquipCount, sellRatio); } else if (itemType ==
		 * Const.itemType.TOOL_VALUE) { this.toolAward(awardItems, itemInfo); } } //
		 * 发奖更新数据 awardItems.rewardTakeAffectAndPush(player, Action.GVG_CITY_BOX, 2);
		 * gvgReward.updateReceived(request.getCityId());
		 */
	}

	/**
	 * 推送GVG状态
	 */
	@MessageHandlerAnno(code = GsConst.MsgType.GVG_STATE)
	private boolean onRankChange(Msg msg) {
		// 账号等级限定
		if (player.getLevel() >= GvgCfg.getInstance().getOpenLevel()) {
			// 取数据推送一下
			GVGStatus status = GvgManager.getInstance().getGvgStatus();
			GVGStatusChange.Builder response = GVGStatusChange.newBuilder();
			response.setStatus(status);
			sendProtocol(Protocol.valueOf(HP.code.PUSH_GVG_STATE_S_VALUE, response));
		}
		return true;
	}

	/**
	 * 查看城池战斗情况列表
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.VIEW_CITY_BATTLE_C_VALUE)
	protected void viewCityBattleLogic(Protocol hawkProtocol) {
//		if (!this.isCanInvolved(HP.code.VIEW_CITY_BATTLE_C_VALUE)) {
//			return;
//		}
//		// 账号公会数据
//		PlayerAllianceEntity allianceEntity = player.getPlayerData().getPlayerAllianceEntity();
//		if (allianceEntity.getAllianceId() == 0) {
//			sendError(HP.code.VIEW_CITY_BATTLE_C_VALUE, Status.error.ALLIANCE_NO_JOIN_VALUE);
//			return;
//		}
//		// 解析数据
//		CityBattleInfoRequest request = hawkProtocol.parseProtocol(CityBattleInfoRequest.getDefaultInstance());
//		// 取城池数据
//		GvgCityEntity cityEntity = GvgService.getInstance().getCityEntity(request.getCityId());
//		if (cityEntity == null) {
//			sendError(HP.code.VIEW_CITY_BATTLE_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
//			return;
//		}
//		// 构建返回数据
//		CityBattleInfoResponse.Builder response = CityBattleInfoResponse.newBuilder();
//		response.setCityId(request.getCityId());
//		response.setCurrentTime(GuaJiTime.getMillisecond());
//		List<CityBattleInfo> battleLog = GvgManager.getInstance().getCityBattleLog(request.getCityId());
//		if (battleLog != null && battleLog.size() > 0) {
//			response.addAllBattleLogs(battleLog);
//		}
//		Battlefield battleField = GvgManager.getInstance().getCityBattle(request.getCityId());
//		long battleTime = GvgManager.getInstance().getBattleTime(request.getCityId());
//		if (battleField != null) {
//			response.setBattle(battleField.getBattleInfo());
//			String attackerName = this.getName(battleField.getAttackers());
//			if (attackerName != null) {
//				response.setAttackerName(attackerName);
//			}
//			String defenderrName = this.getName(battleField.getDefenders());
//			if (defenderrName != null) {
//				response.setDefenderName(defenderrName);
//			}
//			response.setBattleTime(battleTime);
//		}
//		sendProtocol(Protocol.valueOf(HP.code.VIEW_CITY_BATTLE_S_VALUE, response));
	}

	/**
	 * 购买复活权
	 * 
	 * @param hawkProtocol
	 */
	@ProtocolHandlerAnno(code = HP.code.BUY_REVIVE_C_VALUE)
	protected void buyRevive(Protocol hawkProtocol) {
		if (!this.isCanInvolved(HP.code.BUY_REVIVE_C_VALUE)) {
			return;
		}

		// 账号公会数据
		PlayerAllianceEntity playerAllianceEntity = player.getPlayerData().getPlayerAllianceEntity();

		if (playerAllianceEntity == null) {
			sendError(HP.code.BUY_REVIVE_S_VALUE, Status.error.ROLE_NO_ALLIANCE);
			return;
		}
		if (playerAllianceEntity == null || playerAllianceEntity.getPostion() == 0) {

			sendError(HP.code.BUY_REVIVE_S_VALUE, Status.error.ALLIANCE_NO_MAIN_VALUE);
			return;
		}

		BuyReviveRequest request = hawkProtocol.parseProtocol(BuyReviveRequest.getDefaultInstance());
		// 无效参数
		if (request.getCityId() == 0) {
			sendError(HP.code.BUY_REVIVE_S_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}

		GvgCityEntity cityEntity = GvgService.getInstance().getCityEntity(request.getCityId());
		if (cityEntity == null) {
			cityEntity = GvgCityEntity.createEntity(request.getCityId(), 0, 0);
			GvgService.getInstance().addCityEntity(cityEntity);
		}
		// 复活点已被占用
		if (cityEntity.getHolderId() != 0) {
			sendError(HP.code.BUY_REVIVE_S_VALUE, Status.error.ALLIANCE_HAVE_OCCUPY_REVIVEPOINT_VALUE);
			return;
		}

		// 声望判断
		AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(playerAllianceEntity.getAllianceId());
		if (GvgCfg.getInstance().getNeedVitality() > allianceEntity.getBossVitality()) {
			sendError(HP.code.BUY_REVIVE_S_VALUE, Status.error.NOT_HAVE_ENOUGH_VITALITY_VALUE);
			return;
		}
		BehaviorLogger.log4Service(player.getId(), Source.USER_OPERATION, Action.GVG_BUY_REVIVE,
				Params.valueOf("cityId", request.getCityId()));

		// 扣除声望
		allianceEntity.deductBossVitality(GvgCfg.getInstance().getNeedVitality());

		int result = GvgManager.getInstance().obtainRevivePos(playerAllianceEntity.getAllianceId(), player.getId(),
				request.getCityId(), GvgCfg.getInstance().getNeedVitality());
		if (result != 0) {
			sendError(HP.code.BUY_REVIVE_S_VALUE, result);
			return;
		}

		sendBuyRevive(HP.code.BUY_REVIVE_S_VALUE, request.getCityId(), allianceEntity);

	}

	/**
	 * 满足开放条件
	 * 
	 * @param code
	 * @return
	 */
	private boolean isCanInvolved(int code) {

		// 功能开启判定
		if (!GvgService.getInstance().isOpeanFunction() || !GvgManager.getInstance().isIsOpenFunction()) {
			sendError(code, Status.error.GVG_FUNCTION_CLOSE_VALUE);
			return false;
		}

		// 时间点或则不满足15个公会
		if (!GvgService.getInstance().isOpeanFunction()) {
			sendError(code, Status.error.GVG_PLAY_CLOSE_VALUE);
			return false;
		}

		// 账号等级限定
		if (player.getLevel() < GvgCfg.getInstance().getOpenLevel()) {
			sendError(code, Status.error.NOT_LEVEL_OVER_LIMIT_VALUE);
			return false;
		}
		return true;
	}

	/**
	 * 宣战返回数据构建
	 * 
	 * @param code
	 * @param cityId
	 * @param marauderAlliance
	 */
	private void sendDeclareBattle(int code, int cityId, AllianceEntity marauderAlliance) {
		// 城池数据
		GvgCityEntity cityEntity = GvgService.getInstance().getCityEntity(cityId);
		// 构建返回数据
		DeclareBattleResponse.Builder response = DeclareBattleResponse.newBuilder();
		CityInfo.Builder cityInfo = CityInfo.newBuilder();
		cityInfo.setCityId(cityId);
		cityInfo.setStatus(CityStatus.CITY_STATUS_DECLARED);
		cityInfo.setDefTeamNum(cityEntity.getDefenderList().size());
		if (cityEntity.getFightbackTime() - GuaJiTime.getMillisecond() > 0) {
			cityInfo.setFightbackTime(cityEntity.getFightbackTime() - GuaJiTime.getMillisecond());
		}
		cityInfo.setIsReAtk(cityEntity.isFightback());
		// 侵略者数据构建
		GuildInfo.Builder marauder = GuildInfo.newBuilder();
		marauder.setGuildId(cityEntity.getMarauderId());
		marauder.setName(marauderAlliance.getName());
		cityInfo.setAtkGuild(marauder);
		// 城池持有者公会数据
		AllianceEntity holderAlliance = AllianceManager.getInstance().getAlliance(cityEntity.getHolderId());
		if (holderAlliance != null) {
			// 持有者数据构建
			GuildInfo.Builder holder = GuildInfo.newBuilder();
			holder.setGuildId(cityEntity.getHolderId());
			holder.setName(holderAlliance.getName());
			cityInfo.setDefGuild(holder);
			// 被宣战方邮件通知
			int mailId = cityEntity.isFightback() ? GsConst.MailId.FIGHTBACK_DEFENDER : GsConst.MailId.GVG_HOLDER;
			for (Integer memberId : holderAlliance.getMemberList()) {
				MailManager.createSysMail(memberId, Mail.MailType.GVG_MAIL_VALUE, mailId, "被宣战方", null,
						GuaJiTime.getTimeString(), marauderAlliance.getName(), "" + cityId);
			}
		}
		response.setCitys(cityInfo);
		// 宣战方邮件通知
		if (holderAlliance == null) {
			for (Integer memberId : marauderAlliance.getMemberList()) {
				MailManager.createSysMail(memberId, Mail.MailType.GVG_MAIL_VALUE, GsConst.MailId.GVG_MARAUDER_NPC,
						"宣战方", null, GuaJiTime.getTimeString(), "" + cityId);
			}
		} else {
			int mailId = cityEntity.isFightback() ? GsConst.MailId.FIGHTBACK_ATTACKER : GsConst.MailId.GVG_MARAUDER;
			for (Integer memberId : marauderAlliance.getMemberList()) {
				MailManager.createSysMail(memberId, Mail.MailType.GVG_MAIL_VALUE, mailId, "宣战方", null,
						GuaJiTime.getTimeString(), holderAlliance.getName(), "" + cityId);
			}
		}
		// 宣战跑马灯播报
		String chat = this.getBroadMessage(cityEntity.isFightback(), marauderAlliance.getName(), holderAlliance,
				cityId);
		GsApp.getInstance().broadcastChatWorldMsg(chat, chat);
		// 宣战数据同步
		TreeMap<Integer, GvgCityEntity> cities = GvgService.getInstance().getCities();
		Set<Integer> memberIds = new HashSet<Integer>();
		for (GvgCityEntity city : cities.values()) {
			AllianceEntity _marauderAlliance = AllianceManager.getInstance().getAlliance(city.getMarauderId());
			if (_marauderAlliance != null) {
				memberIds.addAll(_marauderAlliance.getMemberList());
			}
			AllianceEntity _holderAlliance = AllianceManager.getInstance().getAlliance(city.getHolderId());
			if (_holderAlliance != null) {
				memberIds.addAll(_holderAlliance.getMemberList());
			}
		}
		for (Integer memberId : memberIds) {
			Player _player = PlayerUtil.queryPlayer(memberId);
			if (_player != null && _player.isOnline()) {
				_player.sendProtocol(Protocol.valueOf(code, response));
			}
		}
	}

	/**
	 * 发送购买复活权信息
	 * 
	 * @param code
	 * @param cityId
	 * @param marauderAlliance
	 */
	private void sendBuyRevive(int code, int cityId, AllianceEntity marauderAlliance) {

		// 城池数据
		GvgCityEntity cityEntity = GvgService.getInstance().getCityEntity(cityId);
		// 构建返回数据
		BuyReviveResponse.Builder response = BuyReviveResponse.newBuilder();
		CityInfo.Builder cityInfo = CityInfo.newBuilder();
		cityInfo.setCityId(cityId);
		cityInfo.setStatus(CityStatus.CITY_STATUS_OCCUPY);
		cityInfo.setDefTeamNum(cityEntity.getDefenderList().size());
		if (cityEntity.getFightbackTime() - GuaJiTime.getMillisecond() > 0) {
			cityInfo.setFightbackTime(cityEntity.getFightbackTime() - GuaJiTime.getMillisecond());
		}
		cityInfo.setIsReAtk(false);

		// 侵略者数据构建
		GuildInfo.Builder marauder = GuildInfo.newBuilder();
		marauder.setGuildId(marauderAlliance.getId());
		marauder.setName(marauderAlliance.getName());
		cityInfo.setDefGuild(marauder);
		cityInfo.setAtkGuild(marauder);

		response.setCityInfo(cityInfo);

		for (Integer memberId : marauderAlliance.getMemberList()) {
			MailManager.createSysMail(memberId, Mail.MailType.GVG_MAIL_VALUE, GsConst.MailId.GVG_MARAUDER_NPC, "复活方",
					null, GuaJiTime.getTimeString(), "" + cityId);
		}
		// 宣战跑马灯播报
		String chat = this.getBroadMessage(cityEntity.isFightback(), marauderAlliance.getName(), null, cityId);
		GsApp.getInstance().broadcastChatWorldMsg(chat, chat);
		// 宣战数据同步
		TreeMap<Integer, GvgCityEntity> cities = GvgService.getInstance().getCities();
		Set<Integer> memberIds = new HashSet<Integer>();
		for (GvgCityEntity city : cities.values()) {
			AllianceEntity _marauderAlliance = AllianceManager.getInstance().getAlliance(city.getMarauderId());
			if (_marauderAlliance != null) {
				memberIds.addAll(_marauderAlliance.getMemberList());
			}
			AllianceEntity _holderAlliance = AllianceManager.getInstance().getAlliance(city.getHolderId());
			if (_holderAlliance != null) {
				memberIds.addAll(_holderAlliance.getMemberList());
			}
		}
		for (Integer memberId : memberIds) {
			Player _player = PlayerUtil.queryPlayer(memberId);
			if (_player != null && _player.isOnline()) {
				_player.sendProtocol(Protocol.valueOf(code, response));
			}
		}
	}

	/**
	 * 宣战播报字典
	 * 
	 * @param isFightback
	 * @param marauderAlliance
	 * @param holderAlliance
	 * @param cityId
	 * @return
	 */
	private String getBroadMessage(boolean isFightback, String marauderName, AllianceEntity holderAlliance,
			int cityId) {
		String message = null;
		if (isFightback) {
			message = ChatManager.getMsgJson(GvgCfg.getInstance().getDeclareFightback(), marauderName,
					holderAlliance.getName(), cityId);
			return message;
		}
		if (holderAlliance == null) {
			message = ChatManager.getMsgJson(GvgCfg.getInstance().getDeclareBattleNpc(), marauderName, cityId);
		} else {
			message = ChatManager.getMsgJson(GvgCfg.getInstance().getDeclareBattle(), marauderName,
					holderAlliance.getName(), cityId);
		}
		return message;
	}

	/**
	 * 统计使用中的佣兵ID
	 *
	 * @param allianceId
	 * @param attackerIds
	 * @param defenderIds
	 */
	private void statisticsRoleId(int allianceId, List<Integer> attackerIds, List<Integer> defenderIds) {
		// 城池数据集合
		final TreeMap<Integer, GvgCityEntity> cities = GvgService.getInstance().getCities();
		// 派遣佣兵ID统计
		for (GvgCityEntity city : cities.values()) {
			// 己方城池防守队列
			if (defenderIds != null && city.getHolderId() == allianceId) {
				List<GvgOccupyBean> defenderList = city.getDefenderList();
				for (GvgOccupyBean occupyBean : defenderList) {
					if (occupyBean.getPlayerId() != player.getId()) {
						continue;
					}
					defenderIds.addAll(occupyBean.getRoleIds());
					// 添加映射佣兵ID
					List<Integer> reflectIds = new ArrayList<Integer>();
					for (int roleId : occupyBean.getRoleIds()) {
						List<Integer> skinGroups = PlayerUtil.getMercenarySkinGroup(player.getId(), roleId);
						reflectIds.addAll(skinGroups);
					}
					defenderIds.addAll(reflectIds);
				}
				continue;
			}
			// 进攻城池进攻队列
			if (attackerIds != null && city.getMarauderId() == allianceId) {
				List<GvgOccupyBean> attackerList = city.getAttackerList();
				for (GvgOccupyBean occupyBean : attackerList) {
					if (occupyBean.getPlayerId() != player.getId()) {
						continue;
					}
					attackerIds.addAll(occupyBean.getRoleIds());
					// 添加映射佣兵ID
					List<Integer> reflectIds = new ArrayList<Integer>();
					for (int roleId : occupyBean.getRoleIds()) {
						List<Integer> skinGroups = PlayerUtil.getMercenarySkinGroup(player.getId(), roleId);
						reflectIds.addAll(skinGroups);
					}
					attackerIds.addAll(reflectIds);
				}
			}
		}
	}

	/**
	 * 刷新佣兵体力值
	 * 
	 * @param roleEntity
	 */
	private void refreshRolePower(RoleEntity roleEntity) {
		// 体力值判断
		if (roleEntity.getRefreshTime() < GuaJiTime.getMillisecond()) {
			roleEntity.setRefreshTime(GuaJiTime.getNextAM0Date());
			roleEntity.setPower(GvgCfg.getInstance().getRolePower());
			roleEntity.notifyUpdate();
		}
	}

	/**
	 * 派遣佣兵逻辑处理
	 * 
	 * @param request
	 * @param allianceId
	 * @param code
	 * @param power
	 */
	private void sendRoleLogic(SendRoleRequest request, int allianceId, int code, int power) {
		// 缓存统计使用中的角色ID
		List<Integer> attackerIds = new ArrayList<Integer>();
		List<Integer> defenderIds = new ArrayList<Integer>();
		this.statisticsRoleId(allianceId, attackerIds, defenderIds);
		// 是否拥有 使用 体力是不是够
		List<RoleEntity> roleEntities = player.getPlayerData().getRoleEntities();
		List<Integer> reflectIds = new ArrayList<Integer>();
		for (int roleId : request.getRoleIdsList()) {
			// 是否使用中
			if (attackerIds.contains(roleId) || defenderIds.contains(roleId)) {
				sendError(code, Status.error.ROLE_IN_USE_VALUE);
				return;
			}
			if (!GvgManager.getInstance().isCanJoinOfRole(player.getId(), roleId)) {
				sendError(code, Status.error.NEW_ROLE_NOT_CAN_JOIN_VALUE);
				return;
			}
			// 是否拥有和体力值判断
			boolean isHave = true;
			for (RoleEntity roleEntity : roleEntities) {
				// 佣兵派遣
				if (roleEntity.getId() != roleId) {
					continue;
				}
				// 佣兵是否激活
				if (roleEntity.getRoleState() == Const.RoleActiviteState.NOT_ACTIVITE_VALUE) {
					break;
				}
				// 不能派遣主将上阵
				if (roleEntity.getType() == GsConst.RoleType.MAIN_ROLE) {
					sendError(code, Status.error.MAIN_NOT_CAN_JOIN_VALUE);
					return;
				}
				// 体力值判断
				this.refreshRolePower(roleEntity);
				if (roleEntity.getPower() < power) {
					sendError(code, Status.error.NOT_ENOUGH_PWOER_VALUE);
					return;
				}
				isHave = true;
				break;
			}
			// 佣兵不存在
			if (!isHave) {
				sendError(code, Status.error.MERCENARY_NOT_FOUND_VALUE);
				return;
			}
			// 添加映射佣兵ID
			List<Integer> skinGroups = PlayerUtil.getMercenarySkinGroup(player.getId(), roleId);
			reflectIds.addAll(skinGroups);
		}
		// 更新体力
		reflectIds.addAll(request.getRoleIdsList());
		Log.gvgLog("power logic " + reflectIds.toString());
		for (RoleEntity roleEntity : roleEntities) {
			if (reflectIds.contains(roleEntity.getId())) {
				this.refreshRolePower(roleEntity);
				roleEntity.setPower(roleEntity.getPower() - power);
				Log.gvgLog("power logic " + roleEntity.getId() + " " + roleEntity.getPower());
				roleEntity.notifyUpdate();
			}
		}
		GvgOccupyBean occupyBean = new GvgOccupyBean();
		occupyBean.setPlayerId(player.getId());
		occupyBean.addAllRoleId(request.getRoleIdsList());
		
		if (!GvgManager.getInstance().getTodayPlayerIds().contains(player.getId()))
			GvgManager.getInstance().getTodayPlayerIds().add(player.getId());
		// 更新城池数据
		GvgCityEntity cityEntity = GvgService.getInstance().getCityEntity(request.getCityId());
		synchronized (cityEntity) {
			if (cityEntity.getHolderId() == allianceId) {
				cityEntity.addDefender(occupyBean);
				BehaviorLogger.log4Service(player.getId(), Source.USER_OPERATION, Action.GVG_DEFENDER,
						Params.valueOf("roleIds", request.getRoleIdsList()));
			} else {
				if (cityEntity.getAttackerList().size() <= 0) {
					AllianceEntity holderAlliance = AllianceManager.getInstance().getAlliance(cityEntity.getHolderId());
					AllianceEntity marauderAlliance = AllianceManager.getInstance()
							.getAlliance(cityEntity.getMarauderId());
					if (holderAlliance != null && marauderAlliance != null) {
						String message = ChatManager.getMsgJson(GvgCfg.getInstance().getSendRoleAttacker(),
								player.getName(), holderAlliance.getName(), cityEntity.getCityId());
						ChatManager.getInstance().postChat(player, message, Const.chatType.CHAT_ALLIANCE_SYSTEM_VALUE,
								1);
						for (Integer memberId : holderAlliance.getMemberList()) {
							Player _player = PlayerUtil.queryPlayer(memberId);
							if (_player == null || !_player.isOnline()) {
								continue;
							}
							message = ChatManager.getMsgJson(GvgCfg.getInstance().getSendRoleDefender(),
									cityEntity.getCityId(), marauderAlliance.getName(), player.getName());
							ChatManager.getInstance().postChat(_player, message,
									Const.chatType.CHAT_ALLIANCE_SYSTEM_VALUE, 1);
							break;
						}
					}
				}

				// 开始攻击
				GvgManager.getInstance().gvgAttackCityNotice(cityEntity.getMarauderId(), cityEntity.getHolderId(),
						request.getCityId());

				cityEntity.addAttacker(occupyBean);
				BehaviorLogger.log4Service(player.getId(), Source.USER_OPERATION, Action.GVG_ATTACKER,
						Params.valueOf("roleIds", request.getRoleIdsList()));
			}
			// 推送攻防队伍数量
			GvgManager.getInstance().pushTeamNumber(cityEntity);
		}
		// 返回数据
		SendRoleResponse.Builder response = SendRoleResponse.newBuilder();
		response.setResult(1);
		int type = (code == HP.code.CITY_DEFENDER_C_VALUE ? HP.code.CITY_DEFENDER_S_VALUE
				: HP.code.CITY_ATTACKER_S_VALUE);
		sendProtocol(Protocol.valueOf(type, response));
	}

	/**
	 * 构建佣兵信息
	 * 
	 * @param roleStatus
	 * @param entity
	 * @return
	 */
	private GvgRoleInfo.Builder builderRoleInfo(RoleStatus roleStatus, RoleEntity roleEntity) {
		GvgRoleInfo.Builder roleInfo = GvgRoleInfo.newBuilder();
		roleInfo.setRoleId(roleEntity.getId());
		roleInfo.setStatus(roleStatus);
		roleInfo.setEnergy(roleEntity.getPower());
		PlayerSnapshotInfo.Builder snapshot = GvgManager.getInstance().getSnapshot(roleEntity.getPlayerId());
		if (snapshot != null) {
			for (RoleInfo _roleInfo : snapshot.getMercenaryInfoList()) {
				if (_roleInfo.getRoleId() == roleEntity.getId()) {
					roleInfo.setFightNum(_roleInfo.getFight());
					break;
				}
			}
		}
		return roleInfo;
	}

	/**
	 * 构建报名队伍信息
	 * 
	 * @param snapShotInfo
	 * @param index
	 * @param occupyBean
	 * @return
	 */
	private TeamInfo.Builder builderTeamInfo(PlayerSnapshotInfo.Builder snapShotInfo, int index,
			GvgOccupyBean occupyBean) {
		RoleInfo mainRole = snapShotInfo.getMainRoleInfo();
		TeamInfo.Builder teamInfo = TeamInfo.newBuilder();
		teamInfo.setPlayerName(mainRole.getName());
		teamInfo.setPlayerLevel(mainRole.getLevel());
		teamInfo.setRebirthStage(mainRole.getRebirthStage());
		int marsterFight = 0;
		for (int roleId : occupyBean.getRoleIds()) {
			for (RoleInfo roleInfo : snapShotInfo.getMercenaryInfoList()) {
				if (roleInfo.getRoleId() == roleId) {
					teamInfo.addRoleIds(roleInfo.getItemId());
					marsterFight = marsterFight + roleInfo.getFight();
					break;
				}
			}
		}
		if (marsterFight == 0) {
			return null;
		}
		teamInfo.setFightNum(marsterFight);
		teamInfo.setTeamId(index);
		teamInfo.setPlayerId(occupyBean.getPlayerId());
		return teamInfo;
	}

	/**
	 * 装备奖励
	 * 
	 * @param awardItems
	 * @param itemInfo
	 * @param maxCount
	 * @param sellRatio
	 * @return
	 */
	private int equipAward(AwardItems awardItems, ItemInfo itemInfo, int maxCount, Integer sellRatio) {
		// 装备配置数据
		EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, itemInfo.getItemId());
		if (equipCfg == null) {
			return maxCount;
		}
		if (EquipUtil.checkAutoSellEquip(player, equipCfg)) {
			// 售出金币
			if (sellRatio != null) {
				awardItems.addCoin((int)(equipCfg.getSellPrice() * itemInfo.getQuantity() * sellRatio));
			} else {
				awardItems.addCoin((int)(equipCfg.getSellPrice() * itemInfo.getQuantity()));
			}
		} else {
			// 可入包裹数量校验
			int dropCount = Math.min((int)itemInfo.getQuantity(), maxCount);
			maxCount -= dropCount;
			// 包裹空间不足
			if (dropCount > 0) {
				itemInfo.setQuantity(dropCount);
				// 添加到奖励信息
				awardItems.addItem(itemInfo);
			}
			// 售出金币
			int sellCount = (int)(itemInfo.getQuantity() - dropCount);
			if (sellCount > 0) {
				if (sellRatio != null) {
					awardItems.addCoin(equipCfg.getSellPrice() * sellCount * sellRatio);
				} else {
					awardItems.addCoin(equipCfg.getSellPrice() * sellCount);
				}
			}
		}
		return maxCount;
	}

	/**
	 * 道具奖励
	 * 
	 * @param awardItems
	 * @param itemInfo
	 */
	private void toolAward(AwardItems awardItems, ItemInfo itemInfo) {
		// 道具掉落
		ItemCfg itemCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, itemInfo.getItemId());
		if (itemCfg == null) {
			return;
		}
		// 添加到奖励信息
		if (itemCfg.getType() != Const.toolType.TREASURE_VALUE) {
			awardItems.addItem(itemInfo);
		}
	}

	/**
	 * 获取战斗账号名称
	 * 
	 * @param battleRoles
	 * @return
	 */
	private String getName(List<NewBattleRole> battleRoles) {
		if (battleRoles != null && battleRoles.size() > 0) {
			NewBattleRole battleRole = battleRoles.get(0);
			int playerId = battleRole.getPlayerId();
			String playerName = GvgManager.getInstance().getPlayerName(playerId);
			return playerName;
		}
		return null;
	}
}
