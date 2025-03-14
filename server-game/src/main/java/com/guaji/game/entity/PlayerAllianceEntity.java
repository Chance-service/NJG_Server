package com.guaji.game.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.log.Log;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.bean.ApplyAllianceStates;
import com.guaji.game.util.GsonUtil;

/**
 * 角色持有公会实体
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "player_alliance")
public class PlayerAllianceEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id = 0;

	@Column(name = "allianceId")
	private int allianceId = 0;

	@Column(name = "playerId")
	private int playerId = 0;

	@Column(name = "contribution")
	private int contribution = 0;

	/**
	 * 0:普通成员,1:副会长,2:会长
	 */
	@Column(name = "postion")
	private int postion;

	/**
	 * 0 :手动参加公会BOSS战, 1: vip3 以上自动参加公会BOSS战
	 */
	@Column(name = "autoFight")
	private int autoFight;

	/**
	 * 签到刷新时间
	 */
	@Column(name = "reportTime")
	private long reportTime;

	/**
	 * 商品刷新时间
	 */
	@Column(name = "shopTime")
	private long shopTime;

	/**
	 * 对应商品列表的幸运值
	 */
	@Column(name = "luckyScore")
	protected int luckyScore;

	/**
	 * 商品的刷新次数
	 */
	@Column(name = "refreshShopCount")
	protected int refreshShopCount;

	/**
	 * 刷新每日贡献值时间
	 */
	@Column(name = "refreshTime")
	private long refreshTime;

	/**
	 * 每日贡献值
	 */
	@Column(name = "everydayContribution")
	protected int everydayContribution = 0;

	/**
	 * 商品列表(内存数据, 不进行序列化)
	 */
	@Transient
	private Map<Integer, AllianceShopItem> itemMap = new HashMap<Integer, AllianceShopItem>();

	@Column(name = "exitTime")
	private long exitTime = 0;

	@Column(name = "joinTime")
	private long joinTime = 0;

	@Column(name = "vitality")
	private int vitality = 0;

	@Column(name = "addVitalityTime")
	private Date addVitalityTime = GuaJiTime.getCalendar().getTime(); // 最后一次公会元气更新时间

	/**
	 * 商品列表jsonString
	 */
	@Column(name = "shopItemsStr")
	protected String shopItemsStr;

	@Column(name = "createTime", nullable = false)
	protected Date createTime = GuaJiTime.getCalendar().getTime();

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	/**
	 * 申请加入的公会数据集
	 */
	@Column(name = "applyAllianceData")
	protected String applyAllianceData;

	@Transient
	private Map<Integer, ApplyAllianceStates> applyAllianceDataMap;

	/**
	 * 每日捐献
	 */
	@Column(name = "dailyDonateStr")
	private String dailyDonateStr;
	/**
	 * 每日捐献记录 key 捐献档位 value dayID 从2000年开始计算
	 */
	@Transient
	private Map<Integer, Integer> dailyDonateMap;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAllianceId() {
		return allianceId;
	}

	public void setAllianceId(int allianceId) {
		this.allianceId = allianceId;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getContribution() {
		return contribution;
	}

	public void setContribution(int contribution) {
		this.contribution = contribution;
	}

	public int getPostion() {
		return postion;
	}

	public void setPostion(int postion) {
		this.postion = postion;
	}

	public int getAutoFight() {
		return autoFight;
	}

	public void setAutoFight(int autoFight) {
		this.autoFight = autoFight;
	}

	public long getExitTime() {
		return exitTime;
	}

	public void setExitTime(long exitTime) {
		this.exitTime = exitTime;
	}

	public long getJoinTime() {
		return joinTime;
	}

	public void setJoinTime(long joinTime) {
		this.joinTime = joinTime;
	}

	public long getReportTime() {
		return reportTime;
	}

	public void setReportTime(long reportTime) {
		this.reportTime = reportTime;
	}

	public String getShopItemsStr() {
		if (itemMap != null) {
			shopItemsStr = GsonUtil.getJsonInstance().toJson(itemMap);
		}
		return shopItemsStr;
	}

	public void setShopItemsStr(String shopItemsStr) {
		this.shopItemsStr = shopItemsStr;
	}

	public long getShopTime() {
		return shopTime;
	}

	public void setShopTime(long shopTime) {
		this.shopTime = shopTime;
	}

	/**
	 * 道具类型_道具ID,数量
	 */
	@Transient
	private HashMap<String, Integer> shopMap;

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

	public void init() {
		try {
			itemMap = GsonUtil.getJsonInstance().fromJson(shopItemsStr,
					new TypeToken<Map<Integer, AllianceShopItem>>() {
					}.getType());
			if (itemMap == null) {
				itemMap = new HashMap<>();
			}
			applyAllianceDataMap = GsonUtil.getJsonInstance().fromJson(applyAllianceData,
					new TypeToken<HashMap<Integer, ApplyAllianceStates>>() {
					}.getType());
			dailyDonateMap = GsonUtil.getJsonInstance().fromJson(dailyDonateStr,
					new TypeToken<Map<Integer, Integer>>() {
					}.getType());
			if (dailyDonateMap == null) {
				dailyDonateMap = new HashMap<Integer, Integer>();
			}
		} catch (Exception e) {
			Log.exceptionPrint(e);
			shopMap = new HashMap<String, Integer>();
			applyAllianceDataMap = new ConcurrentHashMap<>();
			dailyDonateMap = new HashMap<Integer, Integer>();
		}

		if (shopMap == null)
			shopMap = new HashMap<String, Integer>();

		if (applyAllianceDataMap == null) {
			applyAllianceDataMap = new ConcurrentHashMap<>();
		}
	}

	public Integer getShopMap(String key) {
		Integer v = shopMap.get(key);
		if (v == null)
			return 0;
		return v;
	}

	public HashMap<String, Integer> getShopMap() {
		return shopMap;
	}

	@Override
	public void notifyUpdate(boolean async) {
		getApplyAllianceData();
		super.notifyUpdate(async);
	}

	public int getVitality() {
		return vitality;
	}

	public void setVitality(int vitality) {
		this.vitality = vitality;
	}

	public void addVitality(int vitality) {
		this.vitality += vitality;
	}

	public int getLuckyScore() {
		return luckyScore;
	}

	public void setLuckyScore(int luckyScore) {
		this.luckyScore = luckyScore;
	}

	public int getRefreshShopCount() {
		return refreshShopCount;
	}

	public void setRefreshShopCount(int refreshShopCount) {
		this.refreshShopCount = refreshShopCount;
	}

	public long getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(long refreshTime) {
		this.refreshTime = refreshTime;
	}

	public int getEverydayContribution() {
		return everydayContribution;
	}

	public void setEverydayContribution(int everydayContribution) {
		this.everydayContribution = everydayContribution;
	}

	public Map<Integer, AllianceShopItem> getItemMap() {
		return itemMap;
	}

	public void setItemMap(Map<Integer, AllianceShopItem> itemMap) {
		this.itemMap = itemMap;
	}

	public String getApplyAllianceData() {
		if (applyAllianceDataMap != null) {
			applyAllianceData = GsonUtil.getJsonInstance().toJson(applyAllianceDataMap);
		}
		return applyAllianceData;
	}

	public void setApplyAllianceData(String applyAllianceData) {
		this.applyAllianceData = applyAllianceData;
	}

	public Map<Integer, ApplyAllianceStates> getApplyAllianceDataMap() {
		return applyAllianceDataMap;
	}

	public void setApplyAllianceDataMap(Map<Integer, ApplyAllianceStates> applyAllianceDataMap) {
		this.applyAllianceDataMap = applyAllianceDataMap;
	}

	public Date getAddVitalityTime() {
		return addVitalityTime;
	}

	public void setAddVitalityTime(Date addVitalityTime) {
		this.addVitalityTime = addVitalityTime;
	}

	/**
	 * 添加商店物品Id
	 * 
	 * @param itemId
	 * @param itemCount
	 */
	public void addItem(Integer itemId, AllianceShopItem item) {
		this.itemMap.put(itemId, item);
	}

	public void clearData() {
		// 更新下次刷新时间
		this.shopTime = GuaJiTime.getNextAM0Date();
		// 清理商品数据
		this.itemMap.clear();
		this.shopItemsStr = GsonUtil.getJsonInstance().toJson(this.itemMap);
		// 清理刷新次数
		this.refreshShopCount = 0;
	}

	public void clear() {
		setShopTime(GuaJiTime.getNextAM0Date());
		// 清理商品列表;
		itemMap.clear();
		// 清理幸运值(可有可无)
		setLuckyScore(-1);
		// 清理刷新次数
		setRefreshShopCount(0);
	}

	public void convert() {
		if (getShopItemsStr() != null) {
			this.itemMap = GsonUtil.getJsonInstance().fromJson(this.getShopItemsStr(),
					new TypeToken<HashMap<Integer, AllianceShopItem>>() {
					}.getType());
		}
	}

	public Map<Integer, Integer> getDailyDonateMap() {
		return dailyDonateMap;
	}

	public void setDailyDonateMap(Map<Integer, Integer> dailyDonateMap) {
		this.dailyDonateMap = dailyDonateMap;
	}

}
