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
import com.guaji.game.player.Player;
import com.guaji.game.util.GsonUtil;

@SuppressWarnings("serial")
@Entity
@Table(name = "shop_discount")
public class ShopDiscountEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "ASSIGNED", strategy = "assigned")
	@GeneratedValue(generator = "ASSIGNED")
	@Column(name = "playerId", nullable = false, unique = true)
	private int playerId;

	@Column(name = "shopGroupMapStr",columnDefinition = "TEXT")
	private String shopGroupMapStr;

	@Transient
	private Map<Integer, Integer> groupMap;
	
	@Column(name = "createTime")
	private Date createTime;

	@Column(name = "updateTime")
	private Date updateTime;

	@Column(name = "invalid")
	private boolean invalid;

	public ShopDiscountEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.groupMap = new HashMap<Integer, Integer>();
	}

	public void convert() {
		if (getShopGroupMapStr() != null) {
			this.groupMap = GsonUtil.getJsonInstance().fromJson(this.getShopGroupMapStr(), new TypeToken<HashMap<Integer, Integer>>() {
			}.getType());
		}
	}
	/**
	 * 
	 * 轉存成json格式
	 */
	public void SaveShopGroup() {
		this.setShopGroupMapStr(GsonUtil.getJsonInstance().toJson(this.getGroupMap()));
	}

	public void addShopCount(int id) {
		Integer counts = groupMap.get(id);
		// ++counts要注意,是加完參數再導入
		groupMap.put(id,counts == null ? 1 : ++counts);
		SaveShopGroup();
	}
	
	/**
	 * 直接設定商品群組ID,購買次數
	 *
	 * @param id
	 * @param count
	 */
	public void setShopCount(int id , int count) {
		groupMap.put(id,count);
		SaveShopGroup();
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

	
	public String getShopGroupMapStr() {
		return shopGroupMapStr;
	}

	public void setShopGroupMapStr(String shopGroupMapStr) {
		this.shopGroupMapStr = shopGroupMapStr;
	}

	public Map<Integer, Integer> getGroupMap() {
		return groupMap;
	}

	public static ShopDiscountEntity valueOf(Player player) {
		ShopDiscountEntity shopDiscountEntity = new ShopDiscountEntity();
		shopDiscountEntity.playerId = player.getId();
		return shopDiscountEntity;
	}


	public int getShopCount(int id) {
		if (groupMap.containsKey(id)) {
			return this.groupMap.get(id);
		}
		return 0;
	}

	/**
	 * 跨天重置
	 */
	public void reset() {
		groupMap.clear();
		SaveShopGroup();
	}
}
