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
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.manager.shop.strategy.MysteryMarket;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsonUtil;

@SuppressWarnings("serial")
@Entity
@Table(name = "mystery_shop")
public class MysteryShopEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "ASSIGNED", strategy = "assigned")
	@GeneratedValue(generator = "ASSIGNED")
	@Column(name = "playerId", nullable = false, unique = true)
	private int playerId;

	@Column(name = "shopItemMapStr",columnDefinition = "TEXT")
	private String shopItemMapStr;

	@Transient
	private Map<Integer, DailyShopItem> mysteryShopMap;
	
	@Column(name = "freeRefresh")
	private int freeRefresh;
	
	@Column(name = "costRefresh")
	private int costRefresh;
	/**
	 * 免費刷新的時間
	 */
	@Column(name = "RefreshTime",columnDefinition = "int default 0")
	private int RefreshTime = 0;

	@Column(name = "createTime")
	private Date createTime;

	@Column(name = "updateTime")
	private Date updateTime;

	@Column(name = "invalid")
	private boolean invalid;

	public MysteryShopEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.mysteryShopMap = new HashMap<Integer, DailyShopItem>(6);
	}

	public void convert() {
		if (getShopItemMapStr() != null) {
			this.mysteryShopMap = GsonUtil.getJsonInstance().fromJson(this.getShopItemMapStr(), new TypeToken<HashMap<Integer, DailyShopItem>>() {
			}.getType());
		}
	}

	/**
	 * 删除已购买物品
	 * 
	 * @param map
	 */
//	public void removeShopItem(Map<Integer, DailyShopItem> map) {
//		Iterator<DailyShopItem> it = mysteryShopMap.values().iterator();
//		while (it.hasNext()) {
//			if (map.containsKey(it.next().getId())) {
//				it.remove();
//			}
//		}
//		this.setShopItemMapStr(GsonUtil.getJsonInstance().toJson(this.getShopMap()));
//	}
	
	/**
	 * 
	 * 轉存成json格式
	 */
	public void SaveShopItem() {
		this.setShopItemMapStr(GsonUtil.getJsonInstance().toJson(this.getShopMap()));
	}

	public void addShopItem(DailyShopItem ShopItem) {
		this.getShopMap().put(ShopItem.getId(), ShopItem);
	}
	
	public void decFreeRefresh() {
		this.freeRefresh = Math.max(this.freeRefresh-1,0);
	}
	
	public void incFreeRefresh(int value) {
		this.freeRefresh = Math.min(this.freeRefresh+value,SysBasicCfg.getInstance().getMysteryMaxFreeRefresh()); // 最大五次
	}
	
	public int getFreeRefresh() {
		return freeRefresh;
	}

	public void setFreeRefresh(int refresh) {
		this.freeRefresh = refresh;
	}
	
	public void decCostRefresh() {
		this.costRefresh = Math.max(this.costRefresh-1,0);
	}
	
	public int getCostRefresh() {
		return costRefresh;
	}

	public void setCostRefresh(int refresh) {
		this.costRefresh = refresh;
	}
	
	public int getRefreshTime() {
		return RefreshTime;
	}
	
	public void setRefreshTime(int value) {
		this.RefreshTime = value;
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

	public static MysteryShopEntity valueOf(Player player) {
		MysteryShopEntity MysteryShopEntity = new MysteryShopEntity();
		MysteryShopEntity.playerId = player.getId();
		MysteryShopEntity.setFreeRefresh(SysBasicCfg.getInstance().getMysteryMaxFreeRefresh());
		MysteryShopEntity.setCostRefresh(SysBasicCfg.getInstance().getMysteryMaxRefresh());
		MysteryMarket.getInstance().shopItemInfo(player, MysteryShopEntity,true);
		return MysteryShopEntity;
	}

	public Map<Integer, DailyShopItem> getShopMap() {
		return mysteryShopMap;
	}

	public DailyShopItem getShopItem(int id) {
		return this.mysteryShopMap.get(id);
	}
	
	/**
	 * 跨天重置付費刷新次數
	 */
	public void reset() {
		setCostRefresh(SysBasicCfg.getInstance().getMysteryMaxRefresh());
	}
}
