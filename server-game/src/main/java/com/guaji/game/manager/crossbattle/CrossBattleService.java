package com.guaji.game.manager.crossbattle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;

import com.guaji.game.GsApp;
import com.guaji.game.config.CrossBattleCfg;
import com.guaji.game.config.CrossRankRewardCfg;
import com.guaji.game.config.CrossSeasonRewardCfg;
import com.guaji.game.entity.ArenaEntity;
import com.guaji.game.entity.CrossBattleEntity;
import com.guaji.game.entity.CrossPlayerEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.ArenaManager;
import com.guaji.game.manager.MailManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.manager.crossserver.CrossServerManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Mail.MailType;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.CsBattle.CrossBattleData;
import com.guaji.game.protocol.CsBattle.EnterState;
import com.guaji.game.protocol.CsBattle.PlayerSignup;

/**
 * 跨服竞技服务类
 */
public class CrossBattleService {
	
	private final static CrossBattleService instance = new CrossBattleService();
	
	/**
	 * 跨服竞技刷新数据
	 */
	private CrossBattleEntity crossEntity;
	
	/**
	 * 数据集合 <playerId, CrossPlayerEntity>
	 */
	private ConcurrentHashMap<Integer, CrossPlayerEntity> entityMap = new ConcurrentHashMap<Integer, CrossPlayerEntity>();;
	
	/**
	 * 门票容器
	 */
	private List<Integer> ticketsId = new ArrayList<Integer>();
	

	private CrossBattleService() {
	}
	
	public static CrossBattleService getInstance() {
		return instance;
	}
	
	/**
	 * 获取跨服竞技刷新数据
	 */
	public CrossBattleEntity getEntity() {
		return this.crossEntity;
	}
	
	/**
	 * 初始数据加载
	 */
	public void init() {
		if(!CrossBattleCfg.getInstance().getCrossserverOpen()) {
			return;
		}
		// 加载跨服数据
		crossEntity = DBManager.getInstance().fetch(CrossBattleEntity.class, "from CrossBattleEntity where id=? and invalid=0", 1);
		if (null != crossEntity) {
			crossEntity.loadCrossBattle();
			ticketsId.addAll(crossEntity.getIdList());
		}
		// 加载跨服玩家数据
		List<CrossPlayerEntity> entityList = DBManager.getInstance().query("from CrossPlayerEntity where invalid=0");
		if (entityList != null && entityList.size() > 0) {
			for (CrossPlayerEntity entity : entityList) {
				entityMap.put(entity.getPlayerId(), entity);
			}
		}
	}
	
	/**
	 * 获取跨服竞技玩家对象
	 * 
	 * @param playerId
	 */
	public CrossPlayerEntity getCrossPlayer(int playerId) {
		return entityMap.get(playerId);
	}
	
	/**
	 * 加载玩家巅峰竞技数据
	 * 
	 * @param playerId
	 */
	public void loadFromDB(int playerId) {
		if (!entityMap.containsKey(playerId)) {
			CrossPlayerEntity entity = DBManager.getInstance().fetch(CrossPlayerEntity.class, "from CrossPlayerEntity where playerId=? and invalid=0", playerId);
			if (entity == null) {
				return;
			}
			entityMap.put(playerId, entity);
		}
	}
	
	/**
	 * 每日推送竞技数据
	 */
	public void sendPlayerData() {
		// 推送数据
		if (crossEntity == null) {
			crossEntity = new CrossBattleEntity();
			crossEntity.initRefreshTime();
			DBManager.getInstance().create(crossEntity);
			// 筛选数据
			List<Integer> idList = this.choosePlayerData();
			// 更新参加跨服战账号
			crossEntity.updateIdList(idList);
			// 门票容器
			ticketsId.addAll(idList);
		}
		if (GuaJiTime.getMillisecond() > crossEntity.getRefreshTime()) {
			// 发送每日奖励
			this.sendEverydayReward();
			// 筛选数据
			List<Integer> idList = this.choosePlayerData();
			// 更新每日刷新数据时间
			crossEntity.updateRefreshTime();
			// 更新参加跨服战账号唯一标识
			crossEntity.updateIdList(idList);
			// 门票容器
			ticketsId.addAll(idList);
			Log.crossLog("CrossBattleService next refreshTime " + crossEntity.getRefreshTime());
		}
	}
	
	/**
	 * 清理每日数据
	 */
	private void clearDayData() {
		this.ticketsId.clear();
		for (CrossPlayerEntity entity : entityMap.values()) {
			entity.clearDayData();
			Log.crossLog("CrossBattleService clear everyday data");
		}
	}
	
	/**
	 * 清理赛季数据
	 */
	private void clearSeasonData() {
		for (CrossPlayerEntity entity : entityMap.values()) {
			entity.clearSeasonData();
			Log.crossLog("CrossBattleService clear stage data");
		}
		crossEntity.updateIdList(new ArrayList<Integer>());
	}
	
	/**
	 * 筛选合法的玩家数据
	 */
	private List<Integer> choosePlayerData() {
		CrossBattleData.Builder builder = CrossBattleData.newBuilder();
		List<ArenaEntity> arenaRank = ArenaManager.getInstance().getTopNArenaData(CrossBattleCfg.getInstance().getArenaRank());
		Log.crossLog("CrossBattleService arena rank size " + arenaRank.size());
		List<Integer> playerIds = new ArrayList<Integer>();
		// 取排行榜数据,发放门票
		for (ArenaEntity entity : arenaRank) {
			if (null != entity) {
				// 查询快照信息
				PlayerSnapshotInfo.Builder snapshot = SnapShotManager.getInstance().getPlayerSnapShot(entity.getPlayerId());
				if (null == snapshot) {
					Log.crossLog("CrossBattleService snapshot message is null : " + entity.getPlayerId());
					continue;
				}
				// 获取主角色信息
				RoleInfo mainRole = snapshot.getMainRoleInfo();
				if (null == mainRole) {
					Log.crossLog("CrossBattleService main role message is null : " + entity.getPlayerId());
					continue;
				}
				// 是否满足条件
				if (CrossBattleCfg.getInstance().getOpenLevel() > mainRole.getLevel()) {
					Log.crossLog("CrossBattleService limit Level : " + entity.getPlayerId());
					continue;
				}
				// 数据包构
				PlayerSignup.Builder _builder = PlayerSignup.newBuilder();
				_builder.setIdentify(PlayerUtil.getPlayerIdentify(entity.getPlayerId()));
				_builder.setServerName(GsApp.getInstance().getServerIdentify());
				_builder.setSnapshot(snapshot);
				builder.addPlayerData(_builder);
				// 创建巅峰竞技数据
				boolean isSuccess = this.createEntity(entity.getPlayerId());
				if (isSuccess) {
					playerIds.add(entity.getPlayerId());
					Log.crossLog("CrossBattleService success : " + entity.getPlayerId());
				} else {
					Log.crossLog("CrossBattleService filed " + entity.getPlayerId());
				}
			}
		}
		CrossServerManager.getInstance().getCSSession().sendProtocol(Protocol.valueOf(HP.code.PUSH_PLAYER_DATA_VALUE, builder));
		return playerIds;
	}
	
	/**
	 * 创建玩家竞技数据
	 * 
	 * @param playerId
	 */
	public boolean createEntity(final int playerId) {
		if (entityMap.containsKey(playerId)) {
			return true;
		}
		CrossPlayerEntity entity = new CrossPlayerEntity();
		entity.setPlayerId(playerId);
		entityMap.put(playerId, entity);
		// 存储数据
		DBManager.getInstance().create(entity);
		return true;
	}
	
	/**
	 * 添加跨服币
	 * 
	 * @param playerId
	 * @param value
	 */
	public void addCrossCoin(final int playerId, final int value) {
		if(value < 0){
			return;
		}
		CrossPlayerEntity entity = entityMap.get(playerId);
		if (null == entity) {
			return;
		}
		entity.setCrossCoin(entity.getCrossCoin() + value);
		entity.notifyUpdate(true);
	}
	
	/**
	 * 获取跨服币
	 * 
	 * @param playerId
	 */
	public int getCrossCoin(int playerId) {
		CrossPlayerEntity entity = entityMap.get(playerId);
		if (null == entity) {
			return 0;
		}
		return entity.getCrossCoin();
	}
	
	/**
	 * 消耗跨服币
	 * 
	 * @param playerId
	 * @param value
	 */
	public void consumeCrossCoin(final int playerId, final int value) {
		CrossPlayerEntity entity = entityMap.get(playerId);
		if (null == entity) {
			return;
		}
		int crossCoin = entity.getCrossCoin() - value;
		if (crossCoin < 0) {
			crossCoin = 0;
		}
		entity.setCrossCoin(crossCoin);
		entity.notifyUpdate(true);
		Player player = PlayerUtil.queryPlayer(playerId);
		if(player != null) {
			BehaviorLogger.log4Service(player, Source.PLAYER_ATTR_CHANGE, Action.CROSS_SHOP_BUY, Params.valueOf("playerAttr", Const.playerAttr.CROSS_COIN_VALUE), Params.valueOf("sub", value), Params.valueOf("after", entity.getCrossCoin()));
		}
	}
	
	/**
	 * 推送跨服状态
	 */
	public void pushCrossState() {
		if (crossEntity == null) {
			return;
		}
		int pushState = crossEntity.getPushState();
		EnterState state = this.isCanEnter(0);
		if (pushState != state.getNumber()) {
			crossEntity.setPushState(state.getNumber());
			crossEntity.notifyUpdate();
			Msg message = Msg.valueOf(GsConst.MsgType.CROSS_SATATE);
			GsApp.getInstance().broadcastMsg(message, GsApp.getInstance().getObjMan(GsConst.ObjType.PLAYER));
		}
	}
	
	/**
	 * 是否能进入战场
	 * 
	 * @param playerId
	 * @return
	 */
	public EnterState isCanEnter(int playerId) {
		Calendar calendar = GuaJiTime.getCalendar();
		int nowDay = calendar.get(Calendar.DAY_OF_WEEK);
		int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
		// 维护结算中
		if(hourOfDay >= CrossBattleCfg.getInstance().getStartTime() && hourOfDay < CrossBattleCfg.getInstance().getEndTime()) {
			return EnterState.DATA_MAINTAIN;
		}
		if (nowDay < CrossBattleCfg.getInstance().getStartDay()) {
			return EnterState.DATA_MAINTAIN;
		}
		if (nowDay == CrossBattleCfg.getInstance().getEndDay() && hourOfDay >= CrossBattleCfg.getInstance().getStartTime()) {
			return EnterState.DATA_MAINTAIN;
		}
		if (nowDay == CrossBattleCfg.getInstance().getStartDay() && hourOfDay < CrossBattleCfg.getInstance().getEndTime()) {
			return EnterState.DATA_MAINTAIN;
		}
		// 条件是否满足
		if (playerId > 0) {
			if (!this.crossEntity.isContains(playerId)) {
				return EnterState.NOT_ENOUGH_CONDITION;
			}
		}
		return EnterState.NORMAL;
	}
	
	/**
	 * 挑战次数校验
	 *
	 * @param player
	 * @return
	 */
	public boolean battleTimes(Player player) {
		// 取数据
		CrossPlayerEntity entity = entityMap.get(player.getId());
		if (null != entity) {
			// 是否还有挑战次数
			if (entity.getBattleTimes() > 0) {
				entity.setBattleTimes(entity.getBattleTimes() - 1);
				entity.notifyUpdate(true);
				Log.crossLog(String.format("CrossBattleService player %d left battle times %d", player.getId(), entity.getBattleTimes()));
				return true;
			}
		}
		return false;
	}

	/**
	 * 跟具每日排行数据发送每日奖励
	 */
	public void sendEverydayReward() {
		// 门票校验
		if(null == ticketsId || ticketsId.size() <= 0) {
			return;
		}
		//周一不发奖
		if(GuaJiTime.getCalendar().get(Calendar.DAY_OF_WEEK) != 2){
			// 奖励配置
			List<CrossRankRewardCfg> rewardList = ConfigManager.getInstance().getConfigList(CrossRankRewardCfg.class);
			// 发送奖励
			for (CrossPlayerEntity entity : entityMap.values()) {
				int playerId = entity.getPlayerId();
				if (ticketsId.contains(playerId)) {
					for (CrossRankRewardCfg rewardCfg : rewardList) {
						if (entity.getRank() <= rewardCfg.getMinRank() && entity.getRank() > 0) {
							// 发阶段奖励邮件
							AwardItems awardItems = AwardItems.valueOf(rewardCfg.getAwards());
							MailManager.createMail(playerId, MailType.Reward_VALUE, GsConst.MailId.CROSS_RANK_REWARD, "每日跨服排名奖励", awardItems,
													GuaJiTime.getTimeString(), String.valueOf(entity.getRank()), rewardCfg.getAwards());
							break;
						}
					}
					Log.crossLog(String.format("CrossBattleService send everyday reward player %d rank %d", entity.getPlayerId(), entity.getRank()));
				} else {
					Log.crossLog(String.format("CrossBattleService send everyday reward player %d no tickets ", entity.getPlayerId()));
				}
			}
		} else {
			this.clearSeasonData();
		}
		// 清理需要重置的数据
		this.clearDayData();
	}
	
	/**
	 * 跟具排行数据发送赛季奖励
	 */
	public void sendSeasonReward() {
		// 奖励配置
		List<CrossSeasonRewardCfg> rewardList = ConfigManager.getInstance().getConfigList(CrossSeasonRewardCfg.class);
		// 发送奖励
		for (CrossPlayerEntity entity : entityMap.values()) {
			int playerId = entity.getPlayerId();
			for (CrossSeasonRewardCfg rewardCfg : rewardList) {
				if (entity.getRank() <= rewardCfg.getMinRank() && entity.getRank() > 0) {
					// 发阶段奖励邮件
					AwardItems awardItems = AwardItems.valueOf(rewardCfg.getAwards());
					MailManager.createMail(playerId, MailType.Reward_VALUE, GsConst.MailId.CROSS_SEASON_REWARD, "跨服赛季排名奖励", awardItems,
											GuaJiTime.getTimeString(), String.valueOf(entity.getRank()), rewardCfg.getAwards());
					break;
				}
			}
			Log.crossLog(String.format("CrossBattleService send season reward player %d rank %d", entity.getPlayerId(), entity.getRank()));
		}
	}
	
}
