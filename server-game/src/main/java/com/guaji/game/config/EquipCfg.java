package com.guaji.game.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;
import org.guaji.os.MyException;

import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;

@ConfigManager.XmlResource(file = "xml/equip.xml", struct = "map")
public class EquipCfg extends ConfigBase {

	/**
	 * 配置id
	 */
	@Id
	protected final int id;

	/**
	 * 部位
	 */
	protected final int part;

	/**
	 * 品质
	 */
	protected final int quality;

	/**
	 * 等级
	 */
	protected final int level;

	/**
	 * 职业
	 */
	protected final String profession;

	private List<Integer> professionLimitList;

	/**
	 * 出售价格
	 */
	protected final int sellPrice;

	/**
	 * 熔炼获得熔炼值
	 */
	protected final int smeltGain;

	/**
	 * 锻造所需消耗
	 */
	protected final String costItem_All;

	/**
	 * 洗练消耗金币
	 */
	protected final int washCoinCost;

	/**
	 * 装备主属性
	 */
	protected final String equipAttr;

	/**
	 * 装备附加属性值总和区间
	 */
	protected final String attrRange;

	/**
	 * 神器属性
	 */
	protected final int godlyAttr;

	/**
	 * 是否开放
	 */
	protected final int isOpen;

	/**
	 * 打孔消耗字符串
	 */
	protected final String punchCost;

	/**
	 * 装备级别
	 */
	private final int grade;

	/**
	 * 主属性
	 */
	protected List<AttrValue> primaryAttr;

	/**
	 * 副属性最小点数
	 */
	protected int minAttrPoint;

	/**
	 * 副属性最大点数
	 */
	protected int maxAttrPoint;

	/**
	 * 套装Id
	 */
	private final int suitId;

	/**
	 * 装备分解获得
	 */
	protected final String decompose;

	private AwardItems decomposeAwardInfo;

	/**
	 * 装备进化所需获得
	 */
	protected final String evolution;

	private List<ItemInfo> evolutionConsumeInfo;

	/**
	 * 装备进化目标Id
	 */
	private final int evolutionTargetId;

	/**
	 * 是否可分解
	 */
	private final boolean decomposeable;

	/**
	 * 套装类型(0普通装备;1=R,2=SR,3=SSSR,4=UR)
	 */
	private final int suitQuality;

	/**
	 * 装备评分
	 */
	private final int equipScore;

	/** 套装升级需要的材料 */
	private final String upgradeMaterial;

	/** 套装升级需要的材料集合-由upgradeMaterial字段转换而来 */
	private List<ItemInfo> upgradeMaterialList;

	/** 套装升级后的ID */
	private final int upgradeId;

	/**
	 * 套装升级需要的可选材料优先
	 */
	private final int firstMaterialId;

	/**
	 * 套装升级需要的可选材料次先
	 */
	private final int mixedMaterialId;

	/**
	 * 升级所需要的可选材料总和
	 */
	private final int totalMaterialCount;

	/**
	 * 专属套装ID
	 */
	private final int roleAttrId;
	/**
	 * 專武技能
	 */
	private final String skill;
	/**
	 * 給Market裝備圖NFT專有
	 */
	private final String EquipmentIcon;
	/**
	 * 給Market裝備名稱NFT專有
	 */
	private final String Name;
	
	/**
	 * 相生裝備星數
	 */
	private final int star;
	
	/**
	 * 相生ID對應 Mutual 
	 */
	private final int series;

	/**
	 * 可用装备列表
	 */
	private static List<EquipCfg> avaliableEquipCfgs = new ArrayList<>(1500);
	/**
	 * 可用装备列表
	 */
	private List<ItemInfo> costItemList;

	public EquipCfg() {
		id = 0;
		part = 0;
		quality = 0;
		level = 0;
		roleAttrId = 0;
		profession = null;
		sellPrice = 0;
		smeltGain = 0;
		costItem_All = null;
		washCoinCost = 0;
		suitQuality = 0;
		equipAttr = null;
		attrRange = null;
		godlyAttr = 0;
		punchCost = null;
		equipScore = 0;
		grade = 0;
		isOpen = 1;
		suitId = 0;
		evolutionTargetId = 0;
		decompose = null;
		evolution = null;

		minAttrPoint = 0;
		maxAttrPoint = 0;
		primaryAttr = new LinkedList<AttrValue>();
		costItemList = new ArrayList<ItemInfo>();
		professionLimitList = new ArrayList<>(2);

		decomposeable = true;
		upgradeMaterial = null;
		upgradeId = 0;
		firstMaterialId = 0;
		mixedMaterialId = 0;
		totalMaterialCount = 0;
		skill = "";
		EquipmentIcon = "";
		Name = "";
		star = 0;
		series = 0;	
	}

	/**
	 * get avaliable equip list
	 * 
	 * @return
	 */
	public static List<EquipCfg> getAvalibaleEquipList() {

		if (avaliableEquipCfgs.size() == 0) {
			Collection<EquipCfg> equipCfgs = ConfigManager.getInstance().getConfigMap(EquipCfg.class).values();
			for (EquipCfg equipCfg : equipCfgs) {
				if (equipCfg.isOpen == 1) {
					avaliableEquipCfgs.add(equipCfg);
				}
			}
		}
		return avaliableEquipCfgs;
	}

	/**
	 * 套装是否不可以升级
	 * 
	 * @return true:不可以升级
	 */
	public boolean isCanNotUpgrade() {
		return this.upgradeId == 0;
	}

	public int getId() {
		return id;
	}

	public int getPart() {
		return part;
	}

	public int getQuality() {
		return quality;
	}

	public int getLevel() {
		return level;
	}

	public int getSellPrice() {
		return sellPrice;
	}

	public int getSmeltGain() {
		return smeltGain;
	}

	public List<ItemInfo> getcostItemList() {
		return costItemList;
	}

	public int getWashCoinCost() {
		return washCoinCost;
	}

	public String getEquipAttr() {
		return equipAttr;
	}

	public String getAttrRange() {
		return attrRange;
	}

	public int getGodlyAttr() {
		return godlyAttr;
	}

	public List<AttrValue> getPrimaryAttr() {
		return primaryAttr;
	}

	public int getMinAttrPoint() {
		return minAttrPoint;
	}

	public int getMaxAttrPoint() {
		return maxAttrPoint;
	}

	public String getPunchCost() {
		return punchCost;
	}

	public int getGrade() {
		return grade;
	}
		
	public String getEquipmentIcon() {
		return EquipmentIcon;
	}
	
	public String getEquipName() {
		return Name;
	}
	
	public String getSkill() {
		return skill;
	}

	@Override
	protected boolean assemble() {
		primaryAttr.clear();
		professionLimitList.clear();

		if (equipAttr != null && equipAttr.length() > 0) {
			String[] items = equipAttr.split(",");
			for (String item : items) {
				AttrValue attrValCond = new AttrValue();
				if (!attrValCond.init(item)) {
					return false;
				}
				primaryAttr.add(attrValCond);
			}
		}

		if (attrRange != null && attrRange.length() > 0) {
			String[] items = attrRange.split(",");
			if (items.length != 2) {
				return false;
			}
			minAttrPoint = Integer.valueOf(items[0]);
			maxAttrPoint = Integer.valueOf(items[1]);
		}

		if (profession != null && !profession.equals("0")) {
			String[] ss = profession.split(",");
			for (String s : ss) {
				professionLimitList.add(Integer.valueOf(s));
			}
		}

		if (this.evolution != null && this.evolution.length() > 0) {
			this.setEvolutionConsumeInfo(ItemInfo.valueListOf(this.evolution));
		}

		if (this.decompose != null && this.decompose.length() > 0) {
			this.setDecomposeAwardInfo(AwardItems.valueOf(this.decompose));
		}

		if (this.upgradeMaterial != null && this.upgradeMaterial.length() > 0) {
			this.upgradeMaterialList = ItemInfo.valueListOf(this.upgradeMaterial);
		}
		
		if (this.costItem_All != null && this.costItem_All.length() > 0) {
			this.costItemList = ItemInfo.valueListOf(this.costItem_All);
		}

		return true;
	}

	/**
	 * 检测装备职业限制
	 * 
	 * @param prof
	 * @return
	 */
//	public boolean checkProfession(Const.prof prof) {
//		return checkProfession(prof.getNumber());
//	}

	/**
	 * 检测装备职业限制
	 * 
	 * @param prof
	 * @return
	 */
	public boolean checkProfession(int prof) {
		if (professionLimitList.size() > 0) {
			return professionLimitList.indexOf(prof) >= 0 ? true : false;
		}
		return true;
	}
	
	/**
	 * 檢查玩家可以使用裝備星數
	 * 
	 * @param tier
	 * @return
	 */
	public boolean checkTierForStar(int tier) {
		if ((tier == 1)&&(quality <= 4)) {
			return true;
		}
		if ((tier == 2)&&(quality <= 5)) {
			return true;
		}
		if (((tier >= 3)&&(tier <= 4))&&(quality<=6)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 回傳职业限制
	 * 
	 * @param prof
	 * @return
	 */
	public List<Integer> getProLimit() {
		return this.professionLimitList;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	 * 根据装备等级和装备配置 随机出一件装备ID
	 * 
	 * @param equipLevel
	 * @param quality
	 * @return
	 */
	public static int randomEquipId(int equipLevel, int quality) {
		List<EquipCfg> availableEuiqpCfgList = EquipCfg.getAvalibaleEquipList();
		List<EquipCfg> targetEquipCfgList = new LinkedList<>();
		for (EquipCfg equipCfg : availableEuiqpCfgList) {
			if (equipCfg.getLevel() == equipLevel && (quality == equipCfg.getQuality() || quality == 0)) {
				targetEquipCfgList.add(equipCfg);
			}
		}

		try {
			if (targetEquipCfgList.size() > 0) {
				int randIndex = GuaJiRand.randInt(0, targetEquipCfgList.size() - 1);
				return targetEquipCfgList.get(randIndex).getId();
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return 0;
	}

	public AwardItems getDecomposeAwardInfo() {
		return decomposeAwardInfo;
	}

	public void setDecomposeAwardInfo(AwardItems decomposeAwardInfo) {
		this.decomposeAwardInfo = decomposeAwardInfo;
	}

	public List<ItemInfo> getEvolutionConsumeInfo() {
		return evolutionConsumeInfo;
	}

	public void setEvolutionConsumeInfo(List<ItemInfo> evolutionConsumeInfo) {
		this.evolutionConsumeInfo = evolutionConsumeInfo;
	}

	public int getSuitId() {
		return suitId;
	}

	public int getEvolutionTargetId() {
		return evolutionTargetId;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

	public boolean isDecomposeable() {
		return decomposeable;
	}

	public String getUpgradeMaterial() {
		return upgradeMaterial;
	}

	public List<ItemInfo> getUpgradeMaterialList() {
		return upgradeMaterialList;
	}

	public int getUpgradeId() {
		return upgradeId;
	}

	public int getTotalMaterialCount() {
		return totalMaterialCount;
	}

	public int getFirstMaterialId() {
		return firstMaterialId;
	}

	public int getMixedMaterialId() {
		return mixedMaterialId;
	}

	public int getSuitQuality() {
		return suitQuality;
	}

	public int getEquipScore() {
		return equipScore;
	}

	public int getRoleAttrId() {
		return roleAttrId;
	}
	
	public int getStar() {
		return this.star;
	}
	
	public int getSeries() {
		return this.series;
	}

	/**
	 * 装备配置属性
	 */
	public static class AttrValue {

		protected int attrId = 0;
		protected int minVal = 0;
		private int maxVal = 0;

		public int getAttrId() {
			return attrId;
		}

		public void clear() {
			attrId = 0;
			minVal = 0;
			setMaxVal(0);
		}

		public boolean isValid() {
			return attrId > 0 && minVal >= 0 && getMaxVal() >= getMaxVal();
		}

		public int randomAttrValue() {
			try {
				return GuaJiRand.randInt(minVal, getMaxVal());
			} catch (Exception e) {
				MyException.catchException(e);
			}
			return 0;
		}

		public boolean init(String info) {
			String[] items = info.split("_");
			if (items.length < 3) {
				return false;
			}
			attrId = Integer.valueOf(items[0]);
			minVal = Integer.valueOf(items[1]);
			setMaxVal(Integer.valueOf(items[2]));
			return true;
		}

		public int getMaxVal() {
			return maxVal;
		}

		public void setMaxVal(int maxVal) {
			this.maxVal = maxVal;
		}
	}

}
