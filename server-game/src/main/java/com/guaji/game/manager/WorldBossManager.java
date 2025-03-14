package com.guaji.game.manager;

import java.util.Date;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.db.DBManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.WorldBossAwardsCfg;
import com.guaji.game.config.WorldBossCfg;
import com.guaji.game.config.WorldBossFailingCfg;
import com.guaji.game.entity.PlayerWorldBossEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.entity.WorldBossEntity;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.WorldBoss.BossRankItem;
import com.guaji.game.protocol.WorldBoss.BossState;
import com.guaji.game.protocol.WorldBoss.HPBossHarmRank;
import com.guaji.game.protocol.WorldBoss.HPBossStatePush;
import com.guaji.game.protocol.WorldBoss.HPWorldBossInfo;
import com.guaji.game.util.BuilderUtil;


/**
 * 世界boss管理器
 */
public class WorldBossManager extends AppObj {

	private static WorldBossManager instance;
	private boolean isNotice;

	public WorldBossManager(GuaJiXID xid) {
		super(xid);
		instance = this;
		//isNotice = false;
	}

	public static WorldBossManager getInstance() {
		return instance;
	}

	/**
	 * 当前boss战的状态
	 */
	private WorldBossInfo curBossInfo;

	/**
	 * 上一场boss战的状态
	 */
	private WorldBossInfo lastBossInfo;
	/**
	 * 下一场boss时间配置
	 */
	private Date nextStartTime;

	/**
	 * 当前BOSS弱点配置
	 */
	private WorldBossFailingCfg curBossFailingCfg;

	/**
	 * 初始化
	 */
	public void init() {
		setNextStartTime(null);
		WorldBossEntity BossEntity = getLastDBBossEntity();
		Date curDate = GuaJiTime.getCalendar().getTime();
		if (BossEntity == null) { // 沒有開過
			setCurBossInfo(new WorldBossInfo(curDate));
			this.curBossInfo.loadFromDb();
			if (this.curBossInfo.isEmpty()) {
				this.curBossInfo.createBossInfo();
			}
		} else {
			if ((BossEntity.getDeadTime() == 0)&&(!BossEntity.isEnd())) { // 沒死 not isDead
				setCurBossInfo(new WorldBossInfo(BossEntity));
				this.curBossInfo.loadFromDb(BossEntity);
			} else {
				setLastBossInfo(new WorldBossInfo(BossEntity));
				getLastBossInfo().loadFromDb(BossEntity);
				// Boss重生但是也過期了,創現在重生
				if (curDate.getTime() > (BossEntity.getRebirthTime()+ WorldBossCfg.getInstance().getDuration())) {
					setCurBossInfo(new WorldBossInfo(curDate));
					this.curBossInfo.loadFromDb();
					if (this.curBossInfo.isEmpty()) {
						this.curBossInfo.createBossInfo();
					}
				} else {
					Date nextDate = BossEntity.getRebirthDate();
					setNextStartTime(nextDate);	
				}
			}
		}
	}

	@Override
	public boolean onTick() {
		long curTime = GuaJiTime.getMillisecond();
		if (this.curBossInfo != null && !this.curBossInfo.isEmpty()) {
			if (this.curBossInfo.isEnd() && !this.curBossInfo.isEndNotify()) {
				this.curBossInfo.setEndNotify(true);
				// 开启新一期
		
				HPBossStatePush.Builder stateBuilder = HPBossStatePush.newBuilder();
				// 关闭红点
				stateBuilder.setState(BossState.LAST_RESULT_SHOW_VALUE);
				GsApp.getInstance()
						.broadcastProtocol(Protocol.valueOf(HP.code.WORLD_BOSS_STATE_PUSH_VALUE, stateBuilder));
			} else {
				if (!this.curBossInfo.isBossDead() && !curBossInfo.isEnd()) {
					this.curBossInfo.action();
				}
			}
			if(this.curBossInfo.isEnd()||this.curBossInfo.isBossDead()) {
				if (this.curBossInfo != null&&this.curBossInfo!=this.lastBossInfo) {
					this.lastBossInfo = this.curBossInfo;
					Date nextDate = curBossInfo.getWorldBossEntity().getRebirthDate();
					setNextStartTime(nextDate);
				}
			}
		}

		if (this.getNextStartTime() != null) {

			// 提前一分钟发跑马灯
//			if (!isNotice && curTime > (this.getNextStartTime().getTime()
//					- WorldBossCfg.getInstance().getPreSecNotice())) {
//				// 跑马灯 不需要动态的参数
//				String chat = ChatManager.getMsgJson(WorldBossCfg.getInstance().getWorldBossBroadcast());
//				GsApp.getInstance().broadcastChatWorldMsg(chat, null);
//				isNotice = true;
//			}

			if (curTime >= this.getNextStartTime().getTime()) {
//				isNotice = false;
				
				this.curBossInfo = new WorldBossInfo(this.getNextStartTime());
				this.curBossInfo.createBossInfo();

				HPBossStatePush.Builder stateBuilder = HPBossStatePush.newBuilder();
				// 推送红点
				stateBuilder.setState(BossState.SHOWING_VALUE);
				stateBuilder.setStartTime(this.curBossInfo.getStartDate().getTime());
				// 推送所有玩家
				GsApp.getInstance()
						.broadcastProtocol(Protocol.valueOf(HP.code.WORLD_BOSS_STATE_PUSH_VALUE, stateBuilder));

				this.setNextStartTime(null); //剛開啟不知道下一場,設為空的
			}
		}

		return super.onTick();
	}

	public WorldBossInfo getCurBossInfo() {
		return curBossInfo;
	}

	public void setCurBossInfo(WorldBossInfo curBossInfo) {
		this.curBossInfo = curBossInfo;
	}

	public WorldBossInfo getLastBossInfo() {
		return lastBossInfo;
	}

	public void setLastBossInfo(WorldBossInfo lastBossInfo) {
		this.lastBossInfo = lastBossInfo;
	}
	
	public void setNextStartTime(Date nextTime) {
		this.nextStartTime = nextTime;
	}
	
	public Date getNextStartTime() {
		return this.nextStartTime;
	}

	public WorldBossFailingCfg getCurBossFailingCfg() {
		return curBossFailingCfg;
	}
	
	/**
	 * 抓DB最新Boss資訊
	 */
	public WorldBossEntity getLastDBBossEntity() {
		List<WorldBossEntity> worldBossEntities  = DBManager.getInstance().limitQuery("from WorldBossEntity where invalid = 0 order by startDate desc",0,1);
		
		WorldBossEntity worldBossEntity = null;//DBManager.getInstance().fetch(WorldBossEntity.class,"from WorldBossEntity where invalid = 0 order by startDate desc limit 1");
		if (worldBossEntities.size() > 0) {
			worldBossEntity = worldBossEntities.get(0);
		} 
		return worldBossEntity;
	}
	

	/**
	 * 同步boss信息
	 * 
	 * @param player
	 */
	public void syncBossInfo(Player player) {
		// 当前boss信息
		WorldBossInfo worldBossInfo = WorldBossManager.getInstance().getCurBossInfo();
		
		if (worldBossInfo == null || worldBossInfo.isEmpty()) {
			worldBossInfo = WorldBossManager.getInstance().getLastBossInfo();
		}

		HPWorldBossInfo.Builder bossInfoBuilder = HPWorldBossInfo.newBuilder();
		
		StateEntity stateEntity = player.getPlayerData().getStateEntity();

		if (worldBossInfo != null && !worldBossInfo.isEmpty()) {
			PlayerWorldBossEntity playerWorldBossEntity = worldBossInfo.getPlayerWorldBoss(player.getId());
			if (playerWorldBossEntity == null) {
				
				playerWorldBossEntity = worldBossInfo.addPlayer(player.getId());
			}

			// 个人伤害排行
			HPBossHarmRank.Builder harmRankBuilder = BuilderUtil.genBossHarmRank(worldBossInfo);
			if (playerWorldBossEntity != null) {
				harmRankBuilder.setSelfHarm(playerWorldBossEntity.getHarm());
				harmRankBuilder.setSelfAttacksTimes(playerWorldBossEntity.getAttackTimes());
			} else {
				harmRankBuilder.setSelfHarm(0);
				harmRankBuilder.setSelfAttacksTimes(0);
			}

			// 联盟伤害排行
			//BuilderUtil.genBossHarmAllianceRank(worldBossInfo, harmRankBuilder);

			// 自己排名信息
			int playerRank = worldBossInfo.getCurRankInfo(player.getId());
			if (playerRank != 0) {
				BossRankItem.Builder playerRankBuilder = BossRankItem.newBuilder();
				playerRankBuilder.setPlayerName(player.getName());
				playerRankBuilder.setHarm(playerWorldBossEntity.getHarm());
				playerRankBuilder.setRewardInfo(
						WorldBossAwardsCfg.getPlayerRankAwards(worldBossInfo.getWorldBossEntity().getAwardsId(),
								worldBossInfo.getCurRankInfo(player.getId())));
				playerRankBuilder.setType(Const.WorldBossRankType.BOSS_PERSON_RANK_TYPE_VALUE);
				playerRankBuilder.setRankIndex(playerRank);
				bossInfoBuilder.setCurRank(playerRankBuilder);
			}

			// boss是否被击杀
			if (worldBossInfo.isBossDead()|| worldBossInfo.isEnd()) {
				bossInfoBuilder.setBossState(BossState.LAST_RESULT_SHOW);
				bossInfoBuilder.setLastBossInfo(harmRankBuilder);
				bossInfoBuilder.setBossInfo(BuilderUtil.genWorldBossInfo(worldBossInfo.getWorldBossEntity()));
				// 離死亡重生多少時間(毫秒)
				long rebirthtime = worldBossInfo.getWorldBossEntity().getRebirthTime() - GuaJiTime.getMillisecond();
				bossInfoBuilder.setLeftTime((int)rebirthtime);
			} else {
				bossInfoBuilder.setBossState(BossState.SHOWING);
				bossInfoBuilder.setLeftTime(worldBossInfo.getLeftTime());
			}
			
			//bossInfoBuilder.setNeedRebirthCost(playerWorldBossEntity.getNeedCostRebirthGold());

			//bossInfoBuilder
			//		.setRebirthLeftTime(playerWorldBossEntity.getActionLeftTime(Const.WorldBossIsFree.WORLD_BOSS_FREE));
			//bossInfoBuilder
			//		.setActionLeftTime(playerWorldBossEntity.getActionLeftTime(Const.WorldBossIsFree.WORLD_BOSS_GOLD));

			bossInfoBuilder.setCurRankItemInfo(harmRankBuilder);
			bossInfoBuilder.setBossInfo(BuilderUtil.genWorldBossInfo(worldBossInfo.getWorldBossEntity()));
			
			bossInfoBuilder.setChallengTime(WorldBossCfg.getInstance().getWorldBossFreeTimes() - stateEntity.getWorldBossBuffFreeTimes());
		}
		player.sendProtocol(Protocol.valueOf(HP.code.FETCH_WORLD_BOSS_INFO_S_VALUE, bossInfoBuilder));
	}

}
