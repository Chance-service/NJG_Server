package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 任务配置;
 * 
 * @author crazyjohn
 *
 */
@ConfigManager.XmlResource(file = "xml/quest.xml", struct = "map")
public class QuestCfg extends ConfigBase {

	@Id
	protected final int id;
	/** 任务组id */
	protected final int team;
	/**
	 * 成就or任务(1任务 2成就 3.新手任務4.等級福利 5.是新手戰力活動)
	 */
	protected final int questType;
	/** 任务目标类型 */
	protected final int targetType;
	/** 目标操作次数 */
	protected final int needCount;
	/** 完成奖励 */
	protected final String reward;
	/** 任务类型(次数, 或者状态) */
	//1.給予數值加存值(累加++) 2.給予數大於等於存值時取代存值 >= 3.給予數減掉存值取代存值 4.給予數小於存值取代存值
	protected final int type;
	/** 任务开启等级 */
	protected final int openLevel;

	public QuestCfg() {
		this.id = 0;
		this.team = 0;
		this.targetType = 0;
		this.needCount = 0;
		this.reward = "";
		this.type = 0;
		questType = 0;
		this.openLevel = 0;
	}

	public int getId() {
		return id;
	}

	public int getTeam() {
		return team;
	}
	/**
	 * 對應 proto.Const.QuestEventType
	 * @return
	 */
	public int getTargetType() {
		return targetType;
	}

	public int getType() {
		return type;
	}

	public int getQuestType() {
		return questType;
	}

	public int getNeedCount() {
		return needCount;
	}

	public String getReward() {
		return reward;
	}

	public int getOpenLevel() {
		return openLevel;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
