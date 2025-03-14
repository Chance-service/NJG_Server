package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.AwardItems;

@ConfigManager.KVResource(file = "xml/worldBoss.cfg")
public class WorldBossCfg extends ConfigBase {

	/**
	 * 世界boss持续时间 单位毫秒
	 */
	private final int duration;
	/**
	 * 世界boss復活时间 单位毫秒
	 */
	private final int bossRebirthTime;
	/**
	 * 行动奖励
	 */
	private final String actionRewwards;

	/**
	 * 排行榜显示数量
	 */
	private final int worldBossRankCount;

	/**
	 * createBossInfoRank 创建boss排名信息
	 */
	private final int createBossInfoRank;

	/**
	 * 每日挑戰次数
	 */
	private final int worldBossFreeTimes;
	
	/**
	 * 任务完成排名限制数量
	 */
	private final int worldBossQuestRank;
	/**
	 * 行動獎勵轉換格式
	 */
	private AwardItems actionAwardsInfo;
	/**
	 * 全局静态对象
	 */
	private static WorldBossCfg instance = null;
	/**
	 * 获取全局静态对象
	 * 
	 * @return
	 */
	public static WorldBossCfg getInstance() {
		return instance;
	}

	public WorldBossCfg() {
		this.duration = 14400000;
		this.bossRebirthTime = 14400000;
		this.createBossInfoRank = 0;
		this.actionRewwards = "10000_1002_10000";
		this.worldBossRankCount = 10;
		this.worldBossFreeTimes = 0;
		this.worldBossQuestRank = 0;
		instance = this;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		if (this.actionRewwards != null && this.actionRewwards.length() > 0) {
			this.actionAwardsInfo = AwardItems.valueOf(this.actionRewwards);
		}
		return true;
	}

	/**
	 * 检测有消息
	 * 
	 * @return
	 */
	@Override
	protected boolean checkValid() {
		return true;
	}

	public int getDuration() {
		return duration;
	}
	
	public int getBossRirthTime() {
		return bossRebirthTime;
	}

	public String getActionRewwards() {
		return actionRewwards;
	}

	public AwardItems getActionAwardsInfo() {
		return actionAwardsInfo;
	}
	
	public int getWorldBossRankCount() {
		return worldBossRankCount;
	}

	public int getCreateBossInfoRank() {
		return createBossInfoRank;
	}

	public int getWorldBossFreeTimes() {
		return worldBossFreeTimes;
	}
	
	public int getWorldBossQuestRank() {
		return worldBossQuestRank;
	}
	
	public String getWorldBossBroadcast() {
		return "@WorldBossBroadcast";
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
