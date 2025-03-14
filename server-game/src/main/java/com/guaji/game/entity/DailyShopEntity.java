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

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.manager.shop.strategy.DailyMarket;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsonUtil;

@SuppressWarnings("serial")
@Entity
@Table(name = "daily_shop")
public class DailyShopEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "ASSIGNED", strategy = "assigned")
	@GeneratedValue(generator = "ASSIGNED")
	@Column(name = "playerId", nullable = false, unique = true)
	private int playerId;

	@Column(name = "shopItemMapStr",columnDefinition = "TEXT")
	private String shopItemMapStr;

	@Transient
	private Map<Integer, DailyShopItem> dailyShopMap;
	
	@Column(name = "createTime")
	private Date createTime;

	@Column(name = "updateTime")
	private Date updateTime;

	@Column(name = "invalid")
	private boolean invalid;

	public DailyShopEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.dailyShopMap = new HashMap<Integer, DailyShopItem>(8);
	}

	public void convert() {
		if (getShopItemMapStr() != null) {
			this.dailyShopMap = GsonUtil.getJsonInstance().fromJson(this.getShopItemMapStr(), new TypeToken<HashMap<Integer, DailyShopItem>>() {
			}.getType());
		}
	}

	/**
	 * 
	 * 轉存成json格式
	 */
	public void SaveShopItem() {
		this.setShopItemMapStr(GsonUtil.getJsonInstance().toJson(this.getDailyShopMap()));
	}

	public void addShopItem(DailyShopItem dailyShopItem) {
		this.getDailyShopMap().put(dailyShopItem.getId(), dailyShopItem);
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

	public String getShopItemMapStr() {
		return shopItemMapStr;
	}

	public void setShopItemMapStr(String shopItemMapStr) {
		this.shopItemMapStr = shopItemMapStr;
	}

	public static DailyShopEntity valueOf(Player player) {
		DailyShopEntity DailyShopEntity = new DailyShopEntity();
		DailyShopEntity.playerId = player.getId();
		DailyMarket.getInstance().shopItemInfo(player, DailyShopEntity,true);
		return DailyShopEntity;
	}

	public int getNextRefreshCost() {
		return 0;//SysBasicCfg.getInstance().getCrystalRefreshCost(refreshCount);
	}

	public Map<Integer, DailyShopItem> getDailyShopMap() {
		return dailyShopMap;
	}

	public DailyShopItem getDailyShopItem(int id) {
		return this.dailyShopMap.get(id);
	}

	/**
	 * 跨天重置
	 */
//	public void reset() {
//		this.getDailyShopMap().clear();
//	}
}
