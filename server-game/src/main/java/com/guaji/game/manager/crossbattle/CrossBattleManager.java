package com.guaji.game.manager.crossbattle;

import java.util.Calendar;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.CrossBattleCfg;
import com.guaji.game.config.VipPrivilegeCfg;
import com.guaji.game.entity.CrossBattleEntity;
import com.guaji.game.entity.CrossPlayerEntity;
import com.guaji.game.manager.crossserver.CrossServerManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.CsBattle.BattleRecordResponse;
import com.guaji.game.protocol.CsBattle.BattleResponse;
import com.guaji.game.protocol.CsBattle.ChallengeResponse;
import com.guaji.game.protocol.CsBattle.DefenderResponse;
import com.guaji.game.protocol.CsBattle.OSMainInfoResponse;
import com.guaji.game.protocol.CsBattle.PlayerBean;
import com.guaji.game.protocol.CsBattle.PlayerInfoSyncResponse;
import com.guaji.game.protocol.CsBattle.PlayerSnapshotResponse;
import com.guaji.game.protocol.CsBattle.RankResponse;
import com.guaji.game.protocol.CsBattle.RefreshVsResponse;
import com.guaji.game.protocol.CsBattle.SyncRankData;

/**
 * 跨服竞技管理
 */
public class CrossBattleManager extends AppObj {

	/**
	 * 循环帧计数
	 */
	private long tickTime = GuaJiTime.getMillisecond();
	
	/**
	 * tick间隔
	 */
	private final long INTERVAL_TIME = 5000L;
	
	
	private static CrossBattleManager instance;

	public CrossBattleManager(GuaJiXID xid) {
		super(xid);
		if (instance == null) {
			instance = this;
		}
	}

	public static CrossBattleManager getInstance() {
		return instance;
	}

	@Override
	public boolean onTick() {
		if(!CrossServerManager.getInstance().getCSSession().isActive()) {
			return true;
		}
		// 推送跨服状态
		CrossBattleService.getInstance().pushCrossState();
		if (GuaJiTime.getMillisecond() - tickTime > INTERVAL_TIME) {
			tickTime = GuaJiTime.getMillisecond();
			// 每日推送竞技数据
			CrossBattleService.getInstance().sendPlayerData();
			// 取发奖时间
			CrossBattleEntity entity = CrossBattleService.getInstance().getEntity();
			if (null == entity) {
				return true;
			}
			// 发送奖励并更新时间
			if (GuaJiTime.getMillisecond() > entity.getRewardTime()) {
				// 赛季结束---发送奖励
				Calendar calendar = GuaJiTime.getCalendar();
				int nowDay = calendar.get(Calendar.DAY_OF_WEEK);
				if (nowDay != CrossBattleCfg.getInstance().getEndDay()) {
					return true;
				}
				int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
				if (hourOfDay < CrossBattleCfg.getInstance().getStartTime()) {
					return true;
				}
				// 发赛季奖励
				CrossBattleService.getInstance().sendSeasonReward();
				// 更新数据
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				entity.updateRewardTime(calendar.getTimeInMillis());
			}
		}
		return true;
	}
	
	/**
	 * 打开界面
	 * 
	 * @param rsponse
	 */
	public void responseMainInfo(OSMainInfoResponse rsponse) {
		int playerId = PlayerUtil.getPlayerIdFromIdentify(rsponse.getSelfInfo().getIdentify());
		if(playerId == 0) {
			return;
		}
		Player player = PlayerUtil.queryPlayer(playerId);
		if(player != null) {
			OSMainInfoResponse.Builder builder = rsponse.toBuilder();
			CrossPlayerEntity entity = CrossBattleService.getInstance().getCrossPlayer(playerId);
			VipPrivilegeCfg vipData = ConfigManager.getInstance().getConfigByKey(VipPrivilegeCfg.class, player.getVipLevel());
			if (null != entity) {
				builder.setLeftTimes(entity.getBattleTimes());
				builder.setLeftBuyTimes(vipData.getCrossBattleTimes() - entity.getBuyTimes());
			}
			player.sendProtocol(Protocol.valueOf(HP.code.OSPVP_VS_INFO_S_VALUE, builder));
		}
	}
	
	/**
	 * 同步玩家数据
	 * 
	 * @param rsponse
	 */
	public void responseSyncData(PlayerInfoSyncResponse rsponse) {
		int playerId = PlayerUtil.getPlayerIdFromIdentify(rsponse.getIdentify());
		if(playerId == 0) {
			return;
		}
		Player player = PlayerUtil.queryPlayer(playerId);
		if(player != null) {
			player.sendProtocol(Protocol.valueOf(HP.code.OSPVP_SYNC_PLAYER_S_VALUE, rsponse.toBuilder()));
		}
	}
	
	/**
	 * 刷新对战数据
	 * 
	 * @param response
	 */
	public void responseRefreshVs(RefreshVsResponse response) {
		int playerId = PlayerUtil.getPlayerIdFromIdentify(response.getIdentify());
		if(playerId == 0) {
			return;
		}
		Player player = PlayerUtil.queryPlayer(playerId);
		if(player != null) {
			player.sendProtocol(Protocol.valueOf(HP.code.OSPVP_REFRESH_VS_INFO_S_VALUE, response.toBuilder()));	
		}
	}
	
	/**
	 * 战斗结束--返回挑战者数据包
	 * 
	 * @param response
	 */
	public void responseChallenge(ChallengeResponse response) {
		int playerId = PlayerUtil.getPlayerIdFromIdentify(response.getIdentify());
		if(playerId == 0) {
			return;
		}
		// 更新被挑战者数据
		Player player = PlayerUtil.queryPlayer(playerId);
		CrossPlayerEntity entity = CrossBattleService.getInstance().getCrossPlayer(playerId);
		if (null != entity) {
			entity.setScore(response.getScore());
			entity.setRank(response.getRank());
			entity.notifyUpdate();
		}
		if(player != null) {
			ChallengeResponse.Builder builder = response.toBuilder();
			if (null != entity) {
				// 剩余挑战次数
				builder.setLeftTimes(entity.getBattleTimes());
				VipPrivilegeCfg vipData = ConfigManager.getInstance().getConfigByKey(VipPrivilegeCfg.class, player.getVipLevel());
				builder.setLeftBuyTimes(vipData.getCrossBattleTimes() - entity.getBuyTimes());
			}
			player.sendProtocol(Protocol.valueOf(HP.code.OSPVP_VS_CHALLENGE_S_VALUE, builder));	
		}
	}
	
	/**
	 * 战斗结束--返回被挑战者数据包
	 * 
	 * @param response
	 */
	public void responseDefender(DefenderResponse response) {
		int playerId = PlayerUtil.getPlayerIdFromIdentify(response.getIdentify());
		if(playerId == 0) {
			return;
		}
		// 更新被挑战者数据
		CrossPlayerEntity entity = CrossBattleService.getInstance().getCrossPlayer(playerId);
		if (null != entity) {
			entity.setScore(response.getScore());
			entity.setRank(response.getRank());
			entity.notifyUpdate();
		}
		// 下发竞技记录
		Player player = PlayerUtil.queryPlayer(playerId);
		if(player != null) {
			player.sendProtocol(Protocol.valueOf(HP.code.OSPVP_VS_DEFENDER_S_VALUE, response.toBuilder()));
		}
		// 更新排行数据
		if (response.getRankDatasCount() > 0) {
			List<SyncRankData> rankDatas = response.getRankDatasList();
			for (SyncRankData rankData : rankDatas) {
				String serverName = PlayerUtil.getServerName(rankData.getIdentify());
				if (serverName.equals(GsApp.getInstance().getServerIdentify())) {
					int _playerId = PlayerUtil.getPlayerIdFromIdentify(rankData.getIdentify());
					CrossPlayerEntity _entity = CrossBattleService.getInstance().getCrossPlayer(_playerId);
					if (null != _entity) {
						_entity.setScore(rankData.getScore());
						_entity.setRank(rankData.getRank());
						_entity.notifyUpdate();
					}
				}
			}
		}
	}
	
	/**
	 * 查看战报列表
	 * 
	 * @param response
	 */
	public void responseBattleList(BattleRecordResponse response) {
		int playerId = PlayerUtil.getPlayerIdFromIdentify(response.getIdentify());
		if(playerId == 0) {
			return;
		}
		Player player = PlayerUtil.queryPlayer(playerId);
		if(player != null) {
			player.sendProtocol(Protocol.valueOf(HP.code.OSPVP_BATTLELIST_S_VALUE, response.toBuilder()));	
		}
	}
	
	/**
	 * 查看战报列表
	 * 
	 * @param response
	 */
	public void responseBattleData(BattleResponse response) {
		int playerId = PlayerUtil.getPlayerIdFromIdentify(response.getIdentify());
		if(playerId == 0) {
			return;
		}
		Player player = PlayerUtil.queryPlayer(playerId);
		if(player != null) {
			if (!response.hasBattle()) {
				// 战报失效提示
				player.sendError(HP.code.OSPVP_BATTLELIST_S_VALUE, Status.error.BATTLE_DATA_INVALID_VALUE);	
			} else {
				player.sendProtocol(Protocol.valueOf(HP.code.OSPVP_BATTLE_S_VALUE, response.toBuilder()));	
			}
		}
	}
	
	/**
	 * 请求排行信息
	 * 
	 * @param response
	 */
	public void responseRankMessage(RankResponse response) {
		int playerId = PlayerUtil.getPlayerIdFromIdentify(response.getIdentify());
		if(playerId == 0) {
			return;
		}
		Player player = PlayerUtil.queryPlayer(playerId);
		if(player != null) {
			player.sendProtocol(Protocol.valueOf(HP.code.OSPVP_RANK_INFO_S_VALUE, response.toBuilder()));
		}
	}
	
	/**
	 * 玩家快照查询
	 * 
	 * @param response
	 */
	public void responsePlayerSnapshot(PlayerSnapshotResponse response) {
		int playerId = PlayerUtil.getPlayerIdFromIdentify(response.getIdentify());
		if(playerId == 0) {
			return;
		}
		Player player = PlayerUtil.queryPlayer(playerId);
		if(player != null) {
			if (!response.hasSnapshot()) {
				// 没找到玩家数据
				player.sendError(HP.code.OSPVP_PLAYER_ROLES_INFO_S_VALUE, Status.error.SEE_PLAYER_INFO_FAIL_VALUE);	
			} else {
				player.sendProtocol(Protocol.valueOf(HP.code.OSPVP_PLAYER_ROLES_INFO_S_VALUE, response.toBuilder()));	
			}
		}
	}
	
	/**
	 * 同步随机排行数据
	 * 
	 * @param rankData
	 */
	public void syncRankData(SyncRankData rankData) {
		int playerId = PlayerUtil.getPlayerIdFromIdentify(rankData.getIdentify());
		CrossPlayerEntity entity = CrossBattleService.getInstance().getCrossPlayer(playerId);
		if (null != entity) {
			entity.setRank(rankData.getRank());
			entity.notifyUpdate();
		}
	}
	
	/**
	 * 构建账号信息
	 * 
	 * @param player
	 * @return
	 */
	public PlayerBean.Builder createPlayerBean(Player player) {
		PlayerBean.Builder builder = PlayerBean.newBuilder();
		builder.setIdentify(PlayerUtil.getPlayerIdentify(player.getId()));
		builder.setName(player.getName());
		builder.setServerName(GsApp.getInstance().getServerIdentify());
		CrossPlayerEntity entity = CrossBattleService.getInstance().getCrossPlayer(player.getId());
		if (null != entity) {
			if (entity.getScore() == 0) {
				builder.setScore(CrossBattleCfg.getInstance().getScoreInit());
			} else {
				builder.setScore(entity.getScore());
			}
			builder.setRank(entity.getRank());
		} else {
			builder.setScore(CrossBattleCfg.getInstance().getScoreInit());
			builder.setRank(0);
		}
		return builder;
	}
}
