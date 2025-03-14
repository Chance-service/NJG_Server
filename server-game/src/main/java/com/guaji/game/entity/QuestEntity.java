package com.guaji.game.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.config.ConfigManager;
import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.config.QuestCfg;
import com.guaji.game.config.QuestTeamCfg;
import com.guaji.game.protocol.Const.QuestState;
import com.guaji.game.util.GsonUtil;

/**
 * 新手任务实体;
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "quest")
public class QuestEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "ASSIGNED", strategy = "assigned")
	@GeneratedValue(generator = "ASSIGNED")
	@Column(name = "playerId", nullable = false, unique = true)
	private int playerId;

	@Column(name = "createTime")
	private Date createTime;

	@Column(name = "updateTime")
	private Date updateTime;

	@Column(name = "invalid")
	private boolean invalid;

	@Column(name = "questStr",columnDefinition = "TEXT not null")
	private String questStr = "";

	@Transient
	private Map<Integer, QuestItem> questMap;

	public QuestEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();

		this.questMap = new HashMap<Integer, QuestItem>();

		Map<Object, QuestTeamCfg> configMap = ConfigManager.getInstance().getConfigMap(QuestTeamCfg.class);

		for (QuestTeamCfg cfg : configMap.values()) {
			// 判断是否在总表中
			for (int itemId : cfg.getQuestList()) {

				QuestCfg config = ConfigManager.getInstance().getConfigByKey(QuestCfg.class, itemId);

				if (config == null) {
					continue;
				}

				QuestItem questItem = new QuestItem();
				questItem.setItemId(itemId);
				questItem.setState(QuestState.ING_VALUE);
				questItem.setFinishedCount(0);
				questItem.initCounter(this);
				questMap.put(itemId, questItem);
			}
		}

	}

	/**
	 * 字符串存储转换为内存数据
	 */
	public void convert() {
		if (this.questStr != null) {
			this.questMap = GsonUtil.getJsonInstance().fromJson(this.questStr, new TypeToken<HashMap<Integer, QuestItem>>() {
			}.getType());
		}

		for (QuestItem questItem : this.questMap.values()) {
			questItem.initCounter(this);
		}
	}
	
	/**
	 * 查漏补缺从数据表
	 */
	private void fixQuestFromConfig()
	{
		Map<Object, QuestTeamCfg> configMap = ConfigManager.getInstance().getConfigMap(QuestTeamCfg.class);
		
		boolean flag = false;

		for (QuestTeamCfg cfg : configMap.values()) {
			// 判断是否在总表中
			for (int itemId : cfg.getQuestList()) {				
				
				if(questMap.containsKey(itemId))
				{
					continue;
				}
				
				QuestCfg config = ConfigManager.getInstance().getConfigByKey(QuestCfg.class, itemId);

				if (config == null) {
					continue;
				}

				QuestItem questItem = new QuestItem();
				questItem.setItemId(itemId);
				questItem.setState(QuestState.ING_VALUE);
				questItem.setFinishedCount(0);
				questItem.initCounter(this);
				questMap.put(itemId, questItem);
				
				flag = true;
			}
		}
		
		if(flag)
		{
			this.reConvert();
		}
		return;
	}
	
	//加载任务
	public void loadQuest()
	{
		this.convert();
		fixQuestFromConfig();
		return;
	}

	/**
	 * 将内存数据转化为json字符串
	 */
	public void reConvert() {
		this.questStr = GsonUtil.getJsonInstance().toJson(this.questMap);
		return;
	}

	public static QuestEntity valueOf(int playerId) {
		QuestEntity questEntity = new QuestEntity();
		questEntity.playerId = playerId;
		questEntity.reConvert();

		return questEntity;
	}

	public void update() {
		reConvert();
		this.notifyUpdate(false);
		return;
	}

	public Map<Integer, QuestItem> getQuestItemMap() {
		return this.questMap;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	public String getQuestStr() {
		return questStr;
	}

	public void setQuestStr(String questStr) {
		this.questStr = questStr;
	}

	public Map<Integer, QuestItem> getQuestMap() {
		return questMap;
	}

	public void setQuestMap(Map<Integer, QuestItem> questMap) {
		this.questMap = questMap;
	}

}
