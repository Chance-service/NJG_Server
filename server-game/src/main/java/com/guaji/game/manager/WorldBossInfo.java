package com.guaji.game.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;
import org.guaji.os.GuaJiTime;
import org.guaji.thread.GuaJiTask;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;

import com.guaji.game.config.NewMonsterCfg;

import com.guaji.game.config.WorldBossAwardsCfg;

import com.guaji.game.config.WorldBossCfg;

import com.guaji.game.config.WorldBossNpcCfg;


import com.guaji.game.entity.PlayerWorldBossEntity;

import com.guaji.game.entity.WorldBossEntity;
import com.guaji.game.item.AwardItems;

import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.module.sevendaylogin.SevenDayQuestEventBus;
import com.guaji.game.player.Player;

import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Const.SevenDayEventType;

import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Mail;

import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.WorldBoss.BossState;

import com.guaji.game.protocol.WorldBoss.HPBossStatePush;


import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;

public class WorldBossInfo {

	//private WorldBossTimeCfg worldBossTimeCfg;

	/**
	 * 当前boss战的详细信息
	 */
	private WorldBossEntity worldBossEntity;

	/**
	 * 个人boss战集合 <playerId,PlayerWorldBossEntity>
	 */
	private Map<Integer, PlayerWorldBossEntity> playerWorldBossMap;

	/**
	 * 公会boss战集合 <allianceId,harm,PlayerWorldBossEntity>
	 */
	//private Map<Integer, Tuple2<Long, Set<PlayerWorldBossEntity>>> allianceWorldBossMap;

	/**
	 * 结束是否提醒
	 */
	private boolean endNotify;
	/**
	 * 開始時間
	 */
	private Date startDate ;

	public WorldBossInfo(Date startDate) {
		this.playerWorldBossMap = new ConcurrentHashMap<Integer, PlayerWorldBossEntity>();
		this.setStartDate(startDate);
	}
	
	public WorldBossInfo(WorldBossEntity BossEntity) {
		this.playerWorldBossMap = new ConcurrentHashMap<Integer, PlayerWorldBossEntity>();
		this.setStartDate(BossEntity.getStartDate());
	}
	
	/**
	 * 加载世界boss信息
	 */
	public void loadFromDb() {
		WorldBossEntity worldBossEntity = DBManager.getInstance().fetch(WorldBossEntity.class,
				"from WorldBossEntity where invalid = 0 and startDate = ? ", getStartDate());

		if (worldBossEntity != null) {
			this.worldBossEntity = worldBossEntity;
			//this.worldBossEntity.converData();
		}

		if (this.worldBossEntity != null) {
			List<PlayerWorldBossEntity> playerWorldBossEntities = DBManager.getInstance().query(
					"from PlayerWorldBossEntity where invalid = 0 and worldBossId = ? ", this.worldBossEntity.getId());
			for (PlayerWorldBossEntity playerWorldBossEntity : playerWorldBossEntities) {
				playerWorldBossMap.put(playerWorldBossEntity.getPlayerId(), playerWorldBossEntity);
				//addAllianceBossHarm(playerWorldBossEntity, playerWorldBossEntity.getHarm());
			}
		}
	}
	
	public void loadFromDb(WorldBossEntity BossEntity) {
		
		if (BossEntity != null) {
			this.worldBossEntity = BossEntity;
			//this.worldBossEntity.converData();
		}

		if (this.worldBossEntity != null) {
			List<PlayerWorldBossEntity> playerWorldBossEntities = DBManager.getInstance().query(
					"from PlayerWorldBossEntity where invalid = 0 and worldBossId = ? ", this.worldBossEntity.getId());
			for (PlayerWorldBossEntity playerWorldBossEntity : playerWorldBossEntities) {
				playerWorldBossMap.put(playerWorldBossEntity.getPlayerId(), playerWorldBossEntity);
				//addAllianceBossHarm(playerWorldBossEntity, playerWorldBossEntity.getHarm());
			}
		}
	}

	public boolean isEmpty() {
		return this.worldBossEntity == null;
	}

	/**
	 * 创建boss信息
	 */
	public boolean createBossInfo() {
		this.worldBossEntity = new WorldBossEntity();
		
		NewMonsterCfg BossNpcCfg = RandWorldBossId();

		this.worldBossEntity.setStartDate(this.getStartDate());
		this.worldBossEntity.setLastKillPlayerId(0);

		boolean bossGenSuc = false;
		
		if (BossNpcCfg != null) {
			this.worldBossEntity.setBossNpcId(BossNpcCfg.getid());
			this.worldBossEntity.setCurrBossHp(BossNpcCfg.getHP());
			this.worldBossEntity.setMaxBossHp(BossNpcCfg.getHP());
			bossGenSuc = true;
		}

		if (bossGenSuc) {
			// boss创建成功
			this.worldBossEntity.notifyCreate();
			Log.gveLog("create Boss success: " + worldBossEntity.getId());
		} else {
			this.worldBossEntity = null;
		}
		return bossGenSuc;
	}

	/**
	 * boss杀了多长时间
	 * 
	 * @return
	 */
	private int getBossKillTime() {
		if (this.worldBossEntity.getDeadTime() <= 0) {
			return WorldBossCfg.getInstance().getDuration();
		}
		return (int) (this.worldBossEntity.getDeadTime() - this.worldBossEntity.getStartDate().getTime());
	}

	/**
	 * 判断是否结束
	 * 
	 * @return
	 */
	public boolean isEnd() {
		if (this.isEmpty())
			return false;
		else
			return this.getWorldBossEntity().isEnd();
		//return GuaJiTime.getMillisecond() > this.getEndTime(); 暫時沒有結束時間
	}

	/**
	 * 获得剩余时间单位是秒
	 * 
	 * @return
	 */
	public int getLeftTime() {
		if (this.isEmpty())
			return 0;
		else
			return (int)(this.getWorldBossEntity().getEndTime()- GuaJiTime.getMillisecond());
				
	}
	
	/**
	 * 出手打boss
	 */
	public void action() {

		//Date curDate = GuaJiTime.getCalendar().getTime();
		//long curTime = curDate.getTime();

		Iterator<PlayerWorldBossEntity> iterator = playerWorldBossMap.values().iterator();
		while (iterator.hasNext()) {
			PlayerWorldBossEntity playerWorldBossEntity = iterator.next();
			boolean isKill = false;

			boolean isAttack = playerWorldBossEntity.getAttack();
			
			if (isAttack) {
				isKill = attack(playerWorldBossEntity);
				playerWorldBossEntity.setAttack(0);
				playerWorldBossEntity.notifyUpdate();
			}
			
			if (isKill) {
				onBossDead();
				break;
			}
		}
	}

	/**
	 * boss死亡
	 */
	private void onBossDead() {
		// 推送关闭状态
		HPBossStatePush.Builder stateBuilder = HPBossStatePush.newBuilder();
		stateBuilder.setState(BossState.LAST_RESULT_SHOW_VALUE);
		Protocol protocol = Protocol.valueOf(HP.code.WORLD_BOSS_STATE_PUSH_VALUE, stateBuilder);
		GsApp.getInstance().broadcastProtocol(protocol);

		// 补发玩家离线boss奖励--发送在线奖励
		List<PlayerWorldBossEntity> playerBossHarmList = new LinkedList<>();
		synchronized (playerWorldBossMap) {
			playerBossHarmList.addAll(playerWorldBossMap.values());
			Collections.sort(playerBossHarmList, WORLD_PLAYER_HARM_SORT);
		}

		
		// 投递发奖任务
		GsApp.getInstance().postCommonTask(
				new WroldBossAwardTask(playerBossHarmList, worldBossEntity));

		// 200名内任务推送
		List<PlayerWorldBossEntity> playerWorldBossEntities = getPlayerRankTop(
				WorldBossCfg.getInstance().getWorldBossQuestRank());
		for (int i = 0; i < playerWorldBossEntities.size(); i++) {
			int playerId = playerWorldBossEntities.get(i).getPlayerId();
			Player player = PlayerUtil.queryPlayer(playerId);
			if (player != null) {
				QuestEventBus.fireQuestEvent(QuestEventType.E_MO_RU_QIN_RANK, i + 1,
						GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId));
			} else {
				QuestEventBus.fireQuestEventWhenPlayerOffline(playerId, QuestEventType.E_MO_RU_QIN_RANK, i + 1);
			}
		}

	}

	/**
	 * 发奖任务
	 * 
	 * @author zdz
	 *
	 */
	protected static class WroldBossAwardTask extends GuaJiTask {

		private List<PlayerWorldBossEntity> playerBossHarmList;
		private WorldBossEntity worldBossEntity;


		// 构造
		protected WroldBossAwardTask(List<PlayerWorldBossEntity> playerBossHarm,
				WorldBossEntity worldBossEntity) {
			this.playerBossHarmList = playerBossHarm;
			this.worldBossEntity = worldBossEntity;

			
		}

		@Override
		protected int run() {
			int player_rank = 0;
			String dateStr = GuaJiTime.DATE_FORMATOR_YYYYMMDD(worldBossEntity.getStartDate());
			for (PlayerWorldBossEntity pwb : playerBossHarmList) {
				// 发放个人伤害排行奖励
				String awardsStr = WorldBossAwardsCfg.getPlayerRankAwards(worldBossEntity.getAwardsId(),
						++player_rank);
				if (awardsStr != null && awardsStr.length() > 0) {
					MailManager.createMail(pwb.getPlayerId(), Mail.MailType.Reward_VALUE,
							GsConst.MailId.WORLD_HARM_RANK, "", AwardItems.valueOf(awardsStr), dateStr,
							String.valueOf(player_rank));

					// 7日之诗活动
					if (PlayerUtil.queryPlayer(pwb.getPlayerId()) != null) {
						SevenDayQuestEventBus.fireQuestEventOneTime(SevenDayEventType.AUTOPVE,
								GuaJiXID.valueOf(GsConst.ObjType.PLAYER, pwb.getPlayerId()));
					} else {
						SevenDayQuestEventBus.fireQuestEventWhenPlayerOffline(pwb.getPlayerId(),
								SevenDayEventType.AUTOPVE, 1);
					}
				}

			}

//			int killPlayerId = this.worldBossEntity.getLastKillPlayerId();
//			if (killPlayerId > 0) {
//				// 最后击杀个人奖励
//				String killAwardsStr = WorldBossAwardsCfg.getKillAwards(worldBossTimeCfg.getAwardsId());
//				// 发放最后击杀个人奖励
//				if (killAwardsStr != null && killAwardsStr.length() > 0) {
//					MailManager.createMail(killPlayerId, Mail.MailType.Reward_VALUE, GsConst.MailId.WORLD_BOSS_KILL, "",
//							AwardItems.valueOf(killAwardsStr), dateStr);
//				}
//				// 最后击杀联盟奖励
//				String killAllianceAwardStr = WorldBossAllianceAwardsCfg.getKillAwards(worldBossTimeCfg.getAwardsId());
//				// 发放最后击杀联盟奖励
//				WorldBossInfo worldBossInfo = WorldBossManager.getInstance().getCurBossInfo();
//				PlayerWorldBossEntity entity = worldBossInfo.getPlayerWorldBoss(killPlayerId);
//				if (entity != null) {
//					AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(entity.getAllianceId());
//					if (allianceEntity != null) {
//						Set<Integer> playerIds = allianceEntity.getMemberList();
//						for (Integer playerId : playerIds) {
//							if (killAllianceAwardStr != null && killAllianceAwardStr.length() > 0) {
//								MailManager.createMail(playerId, Mail.MailType.Reward_VALUE,
//										GsConst.MailId.ALLIANCE_WORLD_BOSS_KILL, "",
//										AwardItems.valueOf(killAllianceAwardStr), dateStr);
//							}
//						}
//					}
//				}
//			}
			return 0;
		}
	}

	/**
	 * 单个玩家出手
	 * 
	 * @param playerWorldBossEntity
	 * @return
	 */
	private boolean attack(PlayerWorldBossEntity playerWorldBossEntity) {
		
		if (playerWorldBossEntity != null){
			long harmValue = playerWorldBossEntity.getHurt();

			// 計算Boss被攻擊血量
			long leftHp = worldBossEntity.getCurrBossHp() - harmValue;
			//long curHarm = leftHp < 0 ? worldBossEntity.getCurrBossHp() : harmValue;
			worldBossEntity.setCurrBossHp(leftHp < 0 ? 0 : leftHp);
			
			// 增加攻击次数
			playerWorldBossEntity.incAttackTimes();
			synchronized (playerWorldBossMap) {
				playerWorldBossEntity.addHarm(harmValue);
				playerWorldBossMap.put(playerWorldBossEntity.getPlayerId(), playerWorldBossEntity);
			}

			// 添加伤害值
			worldBossEntity.notifyUpdate();
			
			if (worldBossEntity.getCurrBossHp() <= 0) {
				// boss死亡
				worldBossEntity.setLastKillPlayerId(playerWorldBossEntity.getPlayerId());
				worldBossEntity.setDeadTime(GuaJiTime.getCalendar().getTime());
				worldBossEntity.notifyUpdate();

				// 世界boss最后一击
				Player last_player = PlayerUtil.queryPlayer(playerWorldBossEntity.getPlayerId());
				if (last_player != null) {
					QuestEventBus.fireQuestEvent(QuestEventType.E_MO_RU_QIN_LAST_ATTACK_COUNT, 1,
							GuaJiXID.valueOf(GsConst.ObjType.PLAYER, last_player.getId()));
				} else {
					QuestEventBus.fireQuestEventWhenPlayerOffline(playerWorldBossEntity.getPlayerId(),
							QuestEventType.E_MO_RU_QIN_LAST_ATTACK_COUNT, 1);
				}

				Log.logPrintln("lastKillWorldBossPlayerId +" + playerWorldBossEntity.getPlayerId());
				return true;
			}
		}
		
		return false;
	}

	public PlayerWorldBossEntity getPlayerWorldBoss(int playerId) {
		return this.playerWorldBossMap.get(playerId);
	}

	/**
	 * 添加玩家
	 * 
	 * @param playerId
	 * @param bossAutoState
	 * @param isRandomBuff
	 * @return
	 */
	public PlayerWorldBossEntity addPlayer(int playerId) {
		if (playerWorldBossMap.containsKey(playerId)) {
			return playerWorldBossMap.get(playerId);
		}

		PlayerSnapshotInfo.Builder snapShot = SnapShotManager.getInstance().getPlayerSnapShot(playerId).clone();
		PlayerWorldBossEntity playerWorldBossEntity = new PlayerWorldBossEntity();
		if (snapShot != null) {
			playerWorldBossEntity.setHarm(0);
			playerWorldBossEntity.setWorldBossId(this.worldBossEntity.getId());
			playerWorldBossEntity.setPlayerId(playerId);
			// 创建DB
			DBManager.getInstance().create(playerWorldBossEntity);
			Log.gveLog("create playerWorldBossEntity: " + playerWorldBossEntity.getPlayerId());
			// 添加伤害缓存
			playerWorldBossMap.put(playerWorldBossEntity.getPlayerId(), playerWorldBossEntity);
		}
		return playerWorldBossEntity;
	}

	/**
	 * 获得个人全服前几名
	 * 
	 * @param top
	 */
	public List<PlayerWorldBossEntity> getPlayerRankTop(int top) {
		int index = 0;
		List<PlayerWorldBossEntity> topList = new CopyOnWriteArrayList<>();
		synchronized (this.playerWorldBossMap) {
			List<PlayerWorldBossEntity> sortList = new LinkedList<>(playerWorldBossMap.values());
			Collections.sort(sortList, WORLD_PLAYER_HARM_SORT);

			for (PlayerWorldBossEntity playerWorldBossEntity : sortList) {
				if (playerWorldBossEntity.getHarm() <= 0) {
					continue;
				}
				if (++index > top) {
					break;
				}
				topList.add(playerWorldBossEntity);
			}
		}
		return topList;
	}

	/**
	 * 获得个人排行自己的排名信息
	 * 
	 * @param top
	 */
	public int getCurRankInfo(int playerId) {
		int index = 0;
		synchronized (this.playerWorldBossMap) {
			List<PlayerWorldBossEntity> sortList = new LinkedList<>(playerWorldBossMap.values());
			Collections.sort(sortList, WORLD_PLAYER_HARM_SORT);

			for (PlayerWorldBossEntity playerWorldBossEntity : sortList) {
				++index;
				if (playerWorldBossEntity.getPlayerId() == playerId) {
					if (playerWorldBossEntity.getHarm() <= 0) {
						index = 0;
					}
					return index;
				}
			}
		}
		return index;
	}
	
	/**
	 * 获取乱序的个人排行信息
	 * 
	 * @param allianceId
	 * @return
	 */
//	public List<PlayerWorldBossEntity> getCurPlayerHarmInfo() {
//		List<PlayerWorldBossEntity> playerIds = new ArrayList<>();
//		synchronized (playerWorldBossMap) {
//			Collection<PlayerWorldBossEntity> playerInfo = playerWorldBossMap.values();
//			if (playerInfo != null) {
//				if (playerInfo.size() <= WorldBossCfg.getInstance().getWorldBossAllinceHarmCount()) {
//					playerIds.addAll(playerInfo);
//				} else {
//					List<PlayerWorldBossEntity> list = new ArrayList<PlayerWorldBossEntity>(playerInfo);
//					GuaJiRand.randomOrder(list);
//					playerIds.addAll(list.subList(0, WorldBossCfg.getInstance().getWorldBossAllinceHarmCount()));
//				}
//			}
//		}
//		return playerIds;
//	}

	public WorldBossEntity getWorldBossEntity() {
		return this.worldBossEntity;
	}
	
	public void setStartDate(Date aDate) {
		this.startDate = aDate;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	/**
	 * 判断boss是否被击杀
	 * 
	 * @return
	 */
	public boolean isBossDead() {
		if (worldBossEntity == null) {
			return true;
		}
		return worldBossEntity.getCurrBossHp() == 0;
	}

	public boolean isEndNotify() {
		return this.endNotify;
	}

	public void setEndNotify(boolean value) {
		this.endNotify = value;
	}

	/**
	 * 个人伤害排序
	 */
	private final static Comparator<PlayerWorldBossEntity> WORLD_PLAYER_HARM_SORT = new Comparator<PlayerWorldBossEntity>() {
		@Override
		public int compare(PlayerWorldBossEntity o1, PlayerWorldBossEntity o2) {
			return o1.getHarm() < o2.getHarm() ? 1 : o1.getHarm() > o2.getHarm() ? -1 : 0;
		}
	};

	public Map<Integer, PlayerWorldBossEntity> getPlayerWorldBossMap() {
		return playerWorldBossMap;
	}

	public void setPlayerWorldBossMap(Map<Integer, PlayerWorldBossEntity> playerWorldBossMap) {
		this.playerWorldBossMap = playerWorldBossMap;
	}

	private NewMonsterCfg RandWorldBossId() {
		Map<Object, WorldBossNpcCfg> BossNpcCfgs = ConfigManager.getInstance().getConfigMap(WorldBossNpcCfg.class);
		List<Integer> IdList = new ArrayList<>(); 
		List<Integer> RateList = new ArrayList<>();
		for (WorldBossNpcCfg NpcCfg : BossNpcCfgs.values()) {
			IdList.add(NpcCfg.getBossId());
			RateList.add(NpcCfg.getRate());
		}
		int bossid = GuaJiRand.randonWeightObject(IdList, RateList);
		
		NewMonsterCfg monsterCfg = ConfigManager.getInstance().getConfigByKey(NewMonsterCfg.class,bossid);
		return monsterCfg;
	}
}
