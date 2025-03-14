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
import com.guaji.game.util.GsonUtil;

@SuppressWarnings("serial")
@Entity
@Table(name = "honor_shop")
public class HonorShopEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "ASSIGNED", strategy = "assigned")
	@GeneratedValue(generator = "ASSIGNED")
	@Column(name = "playerId", nullable = false, unique = true)
	private int playerId;

	/**
	 * 商城中售卖道具数据
	 */
	@Column(name = "shopItemMapStr")
	private String shopItemMapStr;

	@Transient
	private Map<Integer, HonorShopItem> honorShopMap;
	
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

	public HonorShopEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.setHonorShopMap(new HashMap<Integer, HonorShopItem>());
	}

	public void convert() {
		if (getShopItemMapStr() != null) {
			this.honorShopMap = GsonUtil.getJsonInstance().fromJson(this.getShopItemMapStr(), new TypeToken<HashMap<Integer, HonorShopItem>>() {
			}.getType());
		}
		if(honorShopMap==null){
			this.setHonorShopMap(new HashMap<Integer, HonorShopItem>());
		}
	}

	public void addShopItem(HonorShopItem honorShopItem) {
		this.getHonorShopMap().put(honorShopItem.getId(), honorShopItem);
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

	public static HonorShopEntity valueOf(int playerId) {
		HonorShopEntity honorShopEntity = new HonorShopEntity();
		honorShopEntity.playerId = playerId;
		honorShopEntity.shopItemMapStr="";
		return honorShopEntity;
	}

	
//	public void refresh() {
//		//如何能知道玩家的武将是否收集齐全呢
//		this.getHonorShopMap().clear();
//		// HonorShopCfg honorShopCfg = HonorShopCfg.getHonorShopCfg(this.lucky);
//		List<HonorShopCfg> cfgs = ConfigManager.getInstance().getConfigList(HonorShopCfg.class);
//		//检测玩家的主将是否收集齐全
//		
//		String[] items = honorShopCfg.getItems().split(",");
////		int showCount = GsConst.HonorShop.HONOR_SHOP_ITEM_SIZE;
////		
////		// for (int i = 0; i < GsConst.HonorShop.HONOR_SHOP_ITEM_SIZE; i++) {
//		 ItemInfo itemInfo = honorShopCfg.random();
////		// HonorShopItem honorShopItem = new HonorShopItem();
////		// honorShopItem.setItemType(itemInfo.getType());
////		// honorShopItem.setItemId(itemInfo.getItemId());
////		// honorShopItem.setCostHonor(itemInfo.getQuantity());
////		// honorShopItem.setId(i + 1);
////		//
////		// this.addShopItem(honorShopItem);
////		// }
////
////		for (int i = 0; i < items.length; i++) {
////			HonorShopItem honorShopItem = HonorShopItem.valueOf(items[i]);
////			this.addShopItem(honorShopItem);
////		}
//		this.shopItemMapStr = GsonUtil.getJsonInstance().toJson(this.getHonorShopMap());
//	}

	public Map<Integer, HonorShopItem> getHonorShopMap() {
		return honorShopMap;
	}

	public void setHonorShopMap(Map<Integer, HonorShopItem> honorShopMap) {
		this.honorShopMap = honorShopMap;
	}
	
	public HonorShopItem getHornorShopItem(int id) {
		return this.honorShopMap.get(id);
	}

	public void increaseRefreshCount() {
		this.refreshCount += 1;
	}

	
	public void reset() {
		this.lucky = 0;
		this.getHonorShopMap().clear();
		
		this.refreshCount = 0;
	}
}
