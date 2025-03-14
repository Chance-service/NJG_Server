package com.guaji.game.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.guaji.game.config.ChatSkinCfg;
import com.guaji.game.util.GsonUtil;

@SuppressWarnings("serial")
@Entity
@Table(name = "chat_skin")
public class ChatSkinEntity extends DBEntity {

	@Id
	@GenericGenerator(name = "ASSIGNED", strategy = "assigned")
	@GeneratedValue(generator = "ASSIGNED")
	@Column(name = "playerId", nullable = false, unique = true)
	private int playerId;

	// 当前使用的皮肤
	@Column(name = "curSkinId")
	private int curSkinId;

	// 当前拥有的皮肤
	@Column(name = "ownedSkinsStr")
	private String ownedSkinsStr = "";

	// 红点
	@Column(name = "redPoint")
	private boolean redPoint;

	@Column(name = "createTime")
	private Date createTime;

	@Column(name = "updateTime")
	private Date updateTime;

	@Column(name = "invalid")
	private boolean invalid;

	// 已购买皮肤ID
	@Transient
	private Map<Integer, ChatSkinItem> ownedSkins;

	/**
	 * 是否可购买
	 */
	public boolean canBuy(int skinId, int stageId) {
		ChatSkinItem item = ownedSkins.get(skinId);
		if (item != null && item.getStageId() == stageId) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 购买皮肤
	 */
	public boolean buy(int skinId, int stageId) {
		if (!canBuy(skinId, stageId)) {
			return false;
		}
		ChatSkinItem item = ownedSkins.get(skinId);
		if (item == null) {
			item = new ChatSkinItem();
		}
		item.setSkinId(skinId);
		item.setStageId(stageId);
		ChatSkinCfg cfg = ConfigManager.getInstance().getConfigByKey(ChatSkinCfg.class, skinId);
		item.setEndTime(Math.max(GuaJiTime.getAM0Date().getTime(), item.getEndTime()) + cfg.getDays() * 24L * 60L * 60L * 1000L);
		ownedSkins.put(skinId, item);
		curSkinId = skinId;
		return true;
	}

	/**
	 * 获取皮肤剩余天数
	 */
	public int getSkinRemainDays(int skinId) {
		ChatSkinItem item = ownedSkins.get(skinId);
		// 未购买
		if (item == null) {
			return 0;
		}
		//不限时皮肤
		if(item.getEndTime() == -1){
			return -1;
		}
		// 已结束(用当日的零点时间，而不是当前时间比较)
		if (GuaJiTime.getAM0Date().getTime() > item.getEndTime()) {
			return 0;
		} else {
			return GuaJiTime.calcBetweenDays(new Date(item.getEndTime()), new Date());
		}
	}

	/**
	 * 获取当前可用的皮肤
	 */
	public List<ChatSkinItem> availableSkins() {
		List<ChatSkinItem> list = new ArrayList<ChatSkinItem>();
		long am0 = GuaJiTime.getAM0Date().getTime();
		for (ChatSkinItem item : ownedSkins.values()) {
			if (item.getEndTime() == -1 || item.getEndTime() > am0) {
				list.add(item);
			}
		}
		return list;
	}

	/**
	 * 判断皮肤是否可用
	 */
	public boolean isAvailable(int skinId) {
		ChatSkinItem item = ownedSkins.get(skinId);
		if (item == null) {
			return false;
		}
		if (item.getEndTime() == -1 || item.getEndTime() > GuaJiTime.getAM0Date().getTime()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取当前使用的皮肤，如果当前皮肤已到期，则返回0
	 */
	public int getCurSkinId() {
		return getSkinRemainDays(curSkinId) == 0 ? 0 : curSkinId;
	}

	public void setCurSkinId(int curSkinId) {
		this.curSkinId = curSkinId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public String getOwnedSkinsStr() {
		return ownedSkinsStr;
	}

	public void setOwnedSkinsStr(String ownedSkinsStr) {
		this.ownedSkinsStr = ownedSkinsStr;
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

	/**
	 * 字符串存储转换为内存数据
	 */
	public void convert() {
		if (getOwnedSkinsStr() != null) {
			ownedSkins = GsonUtil.getJsonInstance().fromJson(this.getOwnedSkinsStr(),
					new TypeToken<HashMap<Integer, ChatSkinItem>>() {
					}.getType());
		}
		if (ownedSkins == null) {
			ownedSkins = new HashMap<Integer, ChatSkinItem>();
		}
	}

	/**
	 * 将内存数据转化为json字符串
	 */
	public void reConvert() {
		this.setOwnedSkinsStr(GsonUtil.getJsonInstance().toJson(this.ownedSkins));
		return;
	}

	public boolean isRedPoint() {
		return redPoint;
	}

	public void setRedPoint(boolean redPoint) {
		this.redPoint = redPoint;
	}
	
	/**
	 * 奖励皮肤
	 */
	public boolean reward(int skinId) {
		ChatSkinItem item = ownedSkins.get(skinId);
		if (item == null) {
			item = new ChatSkinItem();
		}
		item.setSkinId(skinId);
		//奖励的皮肤不限时
		item.setEndTime(-1);
		ownedSkins.put(skinId, item);
		curSkinId = skinId;
		return true;
	}
	
	/**
	 * 删除皮肤
	 */
	public boolean remove(int skinId) {
		ChatSkinItem item = ownedSkins.remove(skinId);
		if(item == null){
			return false;
		}
		curSkinId = 0;
		return true;
	}
}
