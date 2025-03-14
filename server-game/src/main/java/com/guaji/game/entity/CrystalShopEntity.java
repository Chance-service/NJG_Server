package com.guaji.game.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import com.guaji.game.manager.shop.strategy.CrystalMarkey;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsonUtil;

@SuppressWarnings("serial")
@Entity
@Table(name = "crystal_shop")
public class CrystalShopEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "ASSIGNED", strategy = "assigned")
	@GeneratedValue(generator = "ASSIGNED")
	@Column(name = "playerId", nullable = false, unique = true)
	private int playerId;

	@Column(name = "shopItemMapStr")
	private String shopItemMapStr;

	@Transient
	private Map<Integer, CrystalShopItem> crystalShopMap;

	@Column(name = "lucky")
	private int lucky;

	@Column(name = "refreshCount")
	private int refreshCount;

	@Column(name = "createTime")
	private Date createTime;

	@Column(name = "updateTime")
	private Date updateTime;

	@Column(name = "invalid")
	private boolean invalid;

	public CrystalShopEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.crystalShopMap = new HashMap<Integer, CrystalShopItem>(6);
	}

	public void convert() {
		if (getShopItemMapStr() != null) {
			this.crystalShopMap = GsonUtil.getJsonInstance().fromJson(this.getShopItemMapStr(), new TypeToken<HashMap<Integer, CrystalShopItem>>() {
			}.getType());
		}
	}

	/**
	 * 删除已购买物品
	 * 
	 * @param map
	 */
	public void removeShopItem(Map<Integer, CrystalShopItem> map) {
		Iterator<CrystalShopItem> it = crystalShopMap.values().iterator();
		while (it.hasNext()) {
			if (map.containsKey(it.next().getId())) {
				it.remove();
			}
		}
		this.setShopItemMapStr(GsonUtil.getJsonInstance().toJson(this.getCrystalShopMap()));
	}

	public void addShopItem(CrystalShopItem crystalShopItem) {
		this.getCrystalShopMap().put(crystalShopItem.getId(), crystalShopItem);
	}

	public void increaseLuckyValue(int value) {
		this.lucky += value;
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

	public static CrystalShopEntity valueOf(Player player) {
		CrystalShopEntity crystalShopEntity = new CrystalShopEntity();
		crystalShopEntity.playerId = player.getId();
		CrystalMarkey.getInstance().shopItemInfo(player, crystalShopEntity,true);
		return crystalShopEntity;
	}

	public int getNextRefreshCost() {
		return SysBasicCfg.getInstance().getCrystalRefreshCost(refreshCount);
	}

	public Map<Integer, CrystalShopItem> getCrystalShopMap() {
		return crystalShopMap;
	}

	public CrystalShopItem getCrystalShopItem(int id) {
		return this.crystalShopMap.get(id);
	}

	public void increaseRefreshCount() {
		this.refreshCount += 1;
	}

	public int getLucky() {
		return lucky;
	}

	public void setLucky(int lucky) {
		this.lucky = lucky;
	}

	public int getRefreshCount() {
		return refreshCount;
	}

	public void setRefreshCount(int refreshCount) {
		this.refreshCount = refreshCount;
	}

	/**
	 * 跨天重置
	 */
	public void reset() {
		// this.refresh(vipLevel);
		this.refreshCount = 0;
	}
}
