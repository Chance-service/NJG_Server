package com.guaji.game.config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.attribute.Attribute;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.toolType;
import com.guaji.game.util.ConfigUtil;
import com.guaji.game.util.WeightUtil.WeightItem;

@ConfigManager.XmlResource(file = "xml/item.xml", struct = "map")
public class ItemCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	/**
	 * 类型
	 */
	protected final int type;
	/**
	 * 等级限制, 0无限制
	 */
	protected final int levelLimit;
	/**
	 * 职业限制, 0无限制
	 */
	protected final int profLimit;
	/**
	 * 价格
	 */
	protected final int price;
	/**
	 * 配对物品
	 */
	protected final int needItem;
	/**
	 * 包含物品
	 */
	protected final String containItem;
	/**
	 * 非宝石升级消耗 (宝石升级消耗不在用这个字段)
	 */
	protected final String levelUpCost;

	private int levelUpCostCoins;

	private int levelUpCostItemId;

	private int levelUpCount;

	private final int levelUpCostGold;

	private final int stoneExp;

	/** 技能升级消耗提供的经验 */
	private final int skillExp;

	/**
	 * 宝石升级概率
	 */
	protected final int levelUpRate;
	/**
	 * 保底次数
	 */
	protected final String maxConsumption;

	private int maxLevelNum;

	private int levelUpAddExp;
	/**
	 * 宝石升级目标
	 */
	protected final int levelUpItem;
	/**
	 * 力量
	 */
	protected final int strenght;
	/**
	 * 敏捷
	 */
	protected final int agility;
	/**
	 * 智力
	 */
	protected final int intellect;
	/**
	 * 耐力
	 */
	protected final int stamina;
	/**
	 * 打造消耗熔炼值
	 */
	private final int smeltValue;
	/**
	 * 品质
	 */
	private final int quality;
	/**
	 * 增加运气值
	 */
	private final int addLuck;
	/**
	 * 高级奖池所需运气值
	 */
	private final int needLuck;
	/**
	 * 高级奖池
	 */
	private final String superPool;
	/** 小时卡时间 */
	private final int hourCardTime;

	/** 套装合成 **/
	private int suitEquipId = 0;

	private int suitEquipCount = 0;

	private int needCoin = 0;

	/** 套装碎片兑换 **/
	private final String exchangeStr;
	/**
	 * 神器经验值
	 */
	private int godlyExp;

	/** 英雄令任务id */
	protected final int heroTokenTaskId;

	private List<WeightItem<ItemInfo>> containDataWeightList;

	private List<WeightItem<ItemInfo>> containSuperItemList;

	/**
	 * 宝石种类
	 */
	private static Map<Integer, TreeSet<Integer>> gemTypeMap;

	/**
	 * 属性数据
	 */
	Attribute attribute;

	/**
	 * 宝石类型 15种分类
	 */
	private final int gemType;

	/**
	 * 可镶嵌的装备部位Id
	 */
	private final String embedEquip;

	/**
	 * 宝石可穿戴的装备Id集合
	 */
	private List<Integer> embedEquipIdList;

	/**
	 * 宝石属性
	 */
	private final String gemAttr;

	/**
	 * 宝石消耗的材料(xml表配置对应)
	 */
	private final String gemLevelUpCost;

	/**
	 * 回收奖励资源
	 */
	private final String recycleItem;

	/**
	 * 消耗的材料
	 */
	private List<ItemInfo> consumeMaterialList;

	/**
	 * boss挑战次数
	 */
	private int bossChallengeTimes;

	private Map<Integer, AwardItems> equipExMap;

	/**
	 * 可选择装备列表
	 */
	private Map<Integer, AwardItems> equipSelMap;

	/**
	 * 装备部位限定值
	 */
	private final int equipPart;

	public ItemCfg() {
		id = 0;
		type = 0;
		levelLimit = 0;
		profLimit = 0;
		price = 0;
		needItem = 0;
		containItem = null;
		levelUpCost = null;
		levelUpRate = 0;
		levelUpItem = 0;
		strenght = 0;
		agility = 0;
		intellect = 0;
		stamina = 0;
		maxConsumption = null;
		smeltValue = 0;
		quality = 0;
		addLuck = 0;
		needLuck = 0;
		superPool = null;
		levelUpCostGold = 0;
		stoneExp = 0;
		skillExp = 0;
		heroTokenTaskId = 0;
		hourCardTime = 0;
		exchangeStr = null;

		attribute = new Attribute();
		containDataWeightList = new LinkedList<WeightItem<ItemInfo>>();
		containSuperItemList = new LinkedList<WeightItem<ItemInfo>>();
		gemTypeMap = new HashMap<Integer, TreeSet<Integer>>();

		this.equipExMap = new HashMap<Integer, AwardItems>();

		this.equipSelMap = new HashMap<Integer, AwardItems>();

		this.gemType = 0;
		this.embedEquip = null;
		this.gemAttr = null;
		this.gemLevelUpCost = null;
		this.recycleItem = null;
		embedEquipIdList = new LinkedList<Integer>();
		consumeMaterialList = new LinkedList<ItemInfo>();
		equipPart = 0;
	}

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public int getLevelLimit() {
		return levelLimit;
	}

	public int getProfLimit() {
		return profLimit;
	}

	public int getPrice() {
		return price;
	}

	public int getNeedItem() {
		return needItem;
	}

	public String getContainItem() {
		return containItem;
	}

	public String getLevelUpCost() {
		return levelUpCost;
	}

	public int getLevelUpRate() {
		return levelUpRate;
	}

	public int getLevelUpItem() {
		return levelUpItem;
	}

	public int getStrenght() {
		return strenght;
	}

	public int getAgility() {
		return agility;
	}

	public int getIntellect() {
		return intellect;
	}

	public int getStamina() {
		return stamina;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public int getLevelUpCostCoins() {
		return levelUpCostCoins;
	}

	public void setLevelUpCostCoins(int levelUpCostCoins) {
		this.levelUpCostCoins = levelUpCostCoins;
	}

	public int getLevelUpCostItemId() {
		return levelUpCostItemId;
	}

	public void setLevelUpCostItemId(int levelUpCostItemId) {
		this.levelUpCostItemId = levelUpCostItemId;
	}

	public int getLevelUpCount() {
		return levelUpCount;
	}

	public void setLevelUpCount(int levelUpCount) {
		this.levelUpCount = levelUpCount;
	}

	public int getLevelUpCostGold() {
		return levelUpCostGold;
	}

	public int getSmeltValue() {
		return smeltValue;
	}

	public int getMaxLevelNum() {
		return maxLevelNum;
	}

	public void setMaxLevelNum(int maxLevelNum) {
		this.maxLevelNum = maxLevelNum;
	}

	public int getLevelUpAddExp() {
		return levelUpAddExp;
	}

	public void setLevelUpAddExp(int levelUpAddExp) {
		this.levelUpAddExp = levelUpAddExp;
	}

	public List<WeightItem<ItemInfo>> getContainDataWeightList() {
		return containDataWeightList;
	}

	public void setContainDataWeightList(List<WeightItem<ItemInfo>> containDataWeightList) {
		this.containDataWeightList = containDataWeightList;
	}

	public List<WeightItem<ItemInfo>> getContainSuperItemList() {
		return containSuperItemList;
	}

	public void setContainSuperItemList(List<WeightItem<ItemInfo>> containSuperItemList) {
		this.containSuperItemList = containSuperItemList;
	}

	public int getQuality() {
		return quality;
	}

	public String getExchangeStr() {
		return exchangeStr;
	}

	public static List<ItemCfg> getItemCfgByType(int type) {
		List<ItemCfg> itemCfgs = new LinkedList<>();
		for (ItemCfg itemCfg : ConfigManager.getInstance().getConfigMap(ItemCfg.class).values()) {
			if (itemCfg.getType() == type) {
				itemCfgs.add(itemCfg);
			}
		}
		return itemCfgs;
	}

	public int getBossChallengeTimes() {
		return bossChallengeTimes;
	}

	public void setBossChallengeTimes(int bossChallengeTimes) {
		this.bossChallengeTimes = bossChallengeTimes;
	}

	public int getAddLuck() {
		return addLuck;
	}

	public int getNeedLuck() {
		return needLuck;
	}

	public String getSuperPool() {
		return superPool;
	}

	public int getStoneExp() {
		return stoneExp;
	}

	public int getSkillExp() {
		return skillExp;
	}

	public int getNeedCoin() {
		return needCoin;
	}

	@Override
	protected boolean assemble() {
		attribute.clear();
		containDataWeightList.clear();
		attribute.add(Const.attr.STRENGHT, strenght);
		attribute.add(Const.attr.AGILITY, agility);
		attribute.add(Const.attr.INTELLECT, intellect);
		attribute.add(Const.attr.STAMINA, stamina);
		containDataWeightList.clear();
		containSuperItemList.clear();

		if (levelUpCost != null && levelUpCost.length() > 0 && !levelUpCost.equals("none")
				&& !levelUpCost.equals("0")) {
			String[] items = levelUpCost.split("_");
			if (items.length < 3) {
				return false;
			}
			levelUpCostCoins = Integer.valueOf(items[0]);
			levelUpCostItemId = Integer.valueOf(items[1]);
			levelUpCount = Integer.valueOf(items[2]);
		}

		if (this.type == toolType.GEM_PACKAGE_VALUE || this.type == toolType.TREASURE_VALUE) {
			// 如果是宝石袋
			if (this.containItem != null && this.containItem.length() > 0 && !"0".equals(this.containItem)) {
				String[] itemArrays = containItem.split(",");
				for (String itemArray : itemArrays) {
					String[] items = itemArray.split("_");
					if (items.length != 4) {
						return false;
					}
					ItemInfo itemInfo = new ItemInfo();
					itemInfo.setType(Integer.valueOf(items[0]));
					itemInfo.setItemId(Integer.valueOf(items[1]));
					itemInfo.setQuantity(Integer.valueOf(items[2]));
					containDataWeightList.add(WeightItem.valueOf(itemInfo, Integer.valueOf(items[3])));
				}
			}
		}

		if (this.type == toolType.GEM_VALUE) {
			if (gemTypeMap.containsKey(this.gemType)) {
				gemTypeMap.get(this.gemType).add(this.id);
			} else {
				TreeSet<Integer> set = new TreeSet<>();
				set.add(this.id);
				gemTypeMap.put(this.gemType, set);
			}
		}

		if (this.type == toolType.SUIT_FRAGMENT_VALUE) {
			if (this.containItem != null && this.containItem.length() > 0 && !"0".equals(this.containItem)) {
				String[] itemArrays = containItem.split("_");
				if (itemArrays.length == 3) {
					suitEquipId = Integer.valueOf(itemArrays[1]);
					suitEquipCount = Integer.valueOf(itemArrays[0]);
					needCoin = Integer.valueOf(itemArrays[2]);
				}
			}
		}

		if (this.type == toolType.LUCK_TREASURE_VALUE) {
			if (this.containItem != null && this.containItem.length() > 0 && !"0".equals(this.containItem)) {
				String[] itemArrays = containItem.split(",");
				for (String itemArray : itemArrays) {
					String[] items = itemArray.split("_");
					if (items.length != 4) {
						return false;
					}
					ItemInfo itemInfo = new ItemInfo();
					itemInfo.setType(Integer.valueOf(items[0]));
					itemInfo.setItemId(Integer.valueOf(items[1]));
					itemInfo.setQuantity(Integer.valueOf(items[2]));
					containDataWeightList.add(WeightItem.valueOf(itemInfo, Integer.valueOf(items[3])));
				}
			}

			if (this.superPool != null && this.superPool.length() > 0 && !"0".equals(this.superPool)) {
				String[] itemArrays = superPool.split(",");
				for (String itemArray : itemArrays) {
					String[] items = itemArray.split("_");
					if (items.length != 4) {
						return false;
					}
					ItemInfo itemInfo = new ItemInfo();
					itemInfo.setType(Integer.valueOf(items[0]));
					itemInfo.setItemId(Integer.valueOf(items[1]));
					itemInfo.setQuantity(Integer.valueOf(items[2]));
					containSuperItemList.add(WeightItem.valueOf(itemInfo, Integer.valueOf(items[3])));
				}
			}
		}

		if (maxConsumption != null && maxConsumption.length() > 0 && !"0".equals(maxConsumption)) {
			String[] items = maxConsumption.split(",");
			if (items.length != 2) {
				return false;
			}
			setMaxLevelNum(Integer.valueOf(items[0]));
			setLevelUpAddExp(Integer.valueOf(items[1]));
		}

		if (this.type == Const.toolType.COMMON_GODLY_EXP_VALUE
				|| this.type == Const.toolType.REPUTATION_GODLY_EXP_VALUE) {
			this.godlyExp = Integer.valueOf(this.containItem);
		}

		if (this.type == Const.toolType.EQUIP_EXCHANGE_VALUE) {
			// 装备兑换
			if (this.containItem != null && this.containItem.length() > 0 && !"0".equals(this.containItem)) {
				String[] itemArrays = containItem.split(",");
				for (String itemArray : itemArrays) {
					String[] items = itemArray.split("_");
					if (items.length != 4) {
						return false;
					}
					AwardItems awardItems = AwardItems.valueOf(itemArray.substring(0, itemArray.lastIndexOf("_")));
					this.equipExMap.put(Integer.valueOf(items[items.length - 1]), awardItems);
				}
			}
		}

		if (this.type == Const.toolType.TREASURE_SELITEM_VALUE) {
			if (this.containItem != null && this.containItem.length() > 0 && !"0".equals(this.containItem)) {
				// String[] itemArrays = containItem.split(",");

				String[] itemArrays = containItem.split(",");
				for (String itemArray : itemArrays) {
					String[] items = itemArray.split("_");
					if (items.length != 3) {
						return false;
					}

					AwardItems awardItems = AwardItems.valueOf(itemArray);
					this.equipSelMap.put(Integer.valueOf(items[1]), awardItems);
				}
			}
		}

		if (null != gemAttr && !gemAttr.equals("") && !gemAttr.equals("0")) {
			// 字符解析
			String result[] = gemAttr.split(",");
			attribute = new Attribute();
			attribute.clear();
			for (String value : result) {
				String values[] = value.split("_");
				// 具体属性值解析
				if (values.length == 2) {
					// 属性类型校验
					int attrType = Integer.parseInt(values[0]);
					Const.attr attrTypeEnum = Const.attr.valueOf(attrType);
					if (null != attrTypeEnum) {
						attribute.add(attrTypeEnum, Integer.parseInt(values[1]));
					}
				} else {
					return false;
				}
			}
		}

		if (null != embedEquip && !embedEquip.equals("") && !embedEquip.equals("0")) {
			String[] embedEquipStr = embedEquip.split(",");
			for (String eEquip : embedEquipStr) {
				embedEquipIdList.add(Integer.parseInt(eEquip));
			}
		}

		if (this.gemLevelUpCost != null && this.gemLevelUpCost.length() > 0) {
			this.consumeMaterialList = ItemInfo.valueListOf(this.gemLevelUpCost);
		}

		return true;
	}

	@Override
	protected boolean checkValid() {
		for (WeightItem<ItemInfo> weightItem : containDataWeightList) {
			ItemInfo itemInfo = weightItem.getValue();
			if (itemInfo.getItemId() > 0) {
				if (!ConfigUtil.check(itemInfo.getType(), itemInfo.getItemId())) {
					return false;
				}
			} else {
				if (!ConfigUtil.checkAwardGroup(itemInfo.getType())) {
					return false;
				}
			}
		}

		for (WeightItem<ItemInfo> weightItem : containSuperItemList) {
			ItemInfo itemInfo = weightItem.getValue();
			if (itemInfo.getItemId() > 0) {
				if (!ConfigUtil.check(itemInfo.getType(), itemInfo.getItemId())) {
					return false;
				}
			} else {
				if (!ConfigUtil.checkAwardGroup(itemInfo.getType())) {
					return false;
				}
			}
		}

		return true;
	}

	public int getGodlyExp() {
		return godlyExp;
	}

	public void setGodlyExp(int godlyExp) {
		this.godlyExp = godlyExp;
	}

	public int getSuitEquipId() {
		return suitEquipId;
	}

	public void setSuitEquipId(int suitEquipId) {
		this.suitEquipId = suitEquipId;
	}

	public int getSuitEquipCount() {
		return suitEquipCount;
	}

	public void setSuitEquipCount(int suitEquipCount) {
		this.suitEquipCount = suitEquipCount;
	}

	public AwardItems getEquipExchange(int profId) {
		return this.equipExMap.get(profId);
	}

	public int getHeroTokenTaskId() {
		return heroTokenTaskId;
	}

	public int getHourCardTime() {
		return hourCardTime;
	}

	public int getGemType() {
		return gemType;
	}

	public String getEmbedEquip() {
		return embedEquip;
	}

	public String getGemAttr() {
		return gemAttr;
	}

	public List<Integer> getEmbedEquipIdList() {
		return embedEquipIdList;
	}

	public String getGemLevelUpCost() {
		return gemLevelUpCost;
	}

	public List<ItemInfo> getConsumeMaterialList() {
		return consumeMaterialList;
	}

	public String getRecycleItem() {
		return recycleItem;
	}

	public static Map<Integer, TreeSet<Integer>> getGemTypeMap() {
		return gemTypeMap;
	}

	

	public Map<Integer, AwardItems> getEquipSelMap() {
		return equipSelMap;
	}

	public static void setGemTypeMap(Map<Integer, TreeSet<Integer>> gemTypeMap) {
		ItemCfg.gemTypeMap = gemTypeMap;
	}

	/**
	 * 装备部位限定值
	 * 
	 * @return
	 */
	public int getEquipPart() {
		return equipPart;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
