package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.guaji.config.ConfigManager;
import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.guaji.game.attribute.Attribute;
import com.guaji.game.config.ItemCfg;

/**
 * 物品实体对象
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "item")
public class ItemEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id = 0;
	
	@Column(name = "playerId")
	private int playerId = 0;
	
	@Column(name = "itemId")
	private int itemId = 0;
	
	@Column(name = "itemCount")
	private long itemCount = 0;
	
	@Column(name = "levelUpTimes")
	private int levelUpTimes = 0;
	
	@Column(name = "status")
	private int status = 0;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;
	
	@Column(name = "updateTime")
	protected Date updateTime;
	
	@Column(name = "invalid")
	protected boolean invalid;

	public ItemEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public long getItemCount() {
		return itemCount;
	}

	public void setItemCount(long itemCount) {
		this.itemCount = itemCount;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	public Attribute getAttribute() {
		ItemCfg itemCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, itemId);
		if (itemCfg != null) {
			return itemCfg.getAttribute();
		}
		return null;
	}

	public int getLevelUpTimes() {
		return levelUpTimes;
	}

	public void setLevelUpTimes(int levelUpTimes) {
		this.levelUpTimes = levelUpTimes;
	}
}
