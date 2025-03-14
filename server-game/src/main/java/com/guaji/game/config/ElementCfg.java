package com.guaji.game.config;

import java.util.LinkedList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.ItemInfo;
import com.guaji.game.util.WeightUtil;
import com.guaji.game.util.WeightUtil.WeightItem;

@ConfigManager.XmlResource(file = "xml/element.xml", struct = "map")
public class ElementCfg extends ConfigBase {

	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	
	/**
	 * 品质
	 */
	protected final int quality;
	/**
	 * 是否开放
	 */
	protected final int isOpen;
	
	/**
	 * 默认基础
	 */
	private final String defaultBasicStr ;
	/**
	 * 默认附加
	 */
	private final String defaultExtraStr ;
	/**
	 * 随机基础
	 */
	private final String randomBasicStr ;
	/**
	 * 随机附加
	 */
	private final String randomExtraStr2 ;
	/**
	 * 随机附加
	 */
	private final String randomExtraStr3 ;
	/**
	 * 随机附加
	 */
	private final String randomExtraStr4 ;
	/**
	 * 普通重铸
	 */
	private final String recastCommon ;
	/**
	 * 高级重铸
	 */
	private final String recastAdvance ;
	/**
	 * 重铸消耗
	 */
	private final String recastCost ;
	
	private List<ItemInfo> recastConsumeInfo;
	/**
	 * 职业限定
	 */
	private final int profLimit ;

	private List<WeightItem<Integer>> randomBasic;

	private List<WeightItem<Integer>> randomExtra2Pool;
	
	private List<WeightItem<Integer>> randomExtra3Pool;
	
	private List<WeightItem<Integer>> randomExtra4Pool;
	
	private List<WeightItem<Integer>> recastCommonPool;
	
	private List<WeightItem<Integer>> recastAdvancePool;

	private List<Integer> defaultBasicIds;
	
	private List<Integer> defaultExtraIds;
	/**
	 * 生成时 是否走随机属性
	 */
	private final int isInitAttr ;
	/**
	 * 额外属性条数上限
	 */
	private final int extraCountLimit;
	/**
	 * 最小等级
	 */
	private final int minLevel ;
	/**
	 * 最大等级
	 */
	private final int maxLevel ;
	
	public ElementCfg() {
		id = 0;
		quality = 0;
		isOpen = 1;
		profLimit = 0;
		this.defaultBasicIds  = new LinkedList<>();
		this.randomBasic = new LinkedList<>();
		this.defaultExtraIds = new LinkedList<>();
		this.randomExtra2Pool = new LinkedList<>();
		this.randomExtra3Pool = new LinkedList<>();
		this.randomExtra4Pool = new LinkedList<>();
		this.recastCommonPool = new LinkedList<>();
		this.recastAdvancePool = new LinkedList<>();
		this.defaultBasicStr = null;
		this.defaultExtraStr = null;
		this.randomBasicStr = null;
		this.randomExtraStr2 = null;
		this.randomExtraStr3 = null;
		this.randomExtraStr4 = null;
		this.recastCommon = null;
		this.recastAdvance = null;
		this.isInitAttr = 0;
		this.extraCountLimit = 0;
		recastCost = null;
		minLevel = 1;
		maxLevel = 1;
	}
	
	public int getId() {
		return id;
	}

	public int getQuality() {
		return quality;
	}

	public int getIsOpen() {
		return isOpen;
	}

	@Override
	protected boolean assemble() {
		if(this.randomBasicStr != null) {
			this.setRandomBasic(WeightUtil.convertToList(this.randomBasicStr));
		}
		
		if(this.randomExtraStr2 != null) {
			this.setRandomExtra2Pool(WeightUtil.convertToList(this.randomExtraStr2));
		}
		
		if(this.randomExtraStr3 != null) {
			this.randomExtra3Pool = WeightUtil.convertToList(this.randomExtraStr3);
		}
		
		if(this.randomExtraStr4 != null) {
			this.randomExtra4Pool = WeightUtil.convertToList(this.randomExtraStr4);
		}
		
		if(this.recastCommon != null) {
			this.recastCommonPool = WeightUtil.convertToList(this.recastCommon);
		}
		
		if(this.recastAdvance != null) {
			this.recastAdvancePool = WeightUtil.convertToList(this.recastAdvance);
		}
		
		if(this.defaultBasicStr != null && this.defaultBasicStr.length() > 0 && !"0".equals(this.defaultBasicStr)) {
			String[] dbs = this.defaultBasicStr.split(",");
			for(String s : dbs) {
				if(s != null) {
					this.getDefaultBasicIds().add(Integer.valueOf(s));
				}
			}
		}
		
		if(this.defaultExtraStr != null && this.defaultExtraStr.length() > 0 && !"0".equals(this.defaultExtraStr)) {
			String[] dbs = this.defaultExtraStr.split(",");
			for(String s : dbs) {
				if(s != null) {
					this.getDefaultExtraIds().add(Integer.valueOf(s));
				}
			}
		}
		
		if(this.recastCost != null && this.recastCost.length() > 0 && !"0".equals(this.recastCost)) {
			this.recastConsumeInfo = ItemInfo.valueListOf(this.recastCost);
		}
		
		return super.assemble();
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

	public List<Integer> getDefaultBasicIds() {
		return defaultBasicIds;
	}

	public void setDefaultBasicIds(List<Integer> defaultBasicIds) {
		this.defaultBasicIds = defaultBasicIds;
	}

	public List<Integer> getDefaultExtraIds() {
		return defaultExtraIds;
	}

	public void setDefaultExtraIds(List<Integer> defaultExtraIds) {
		this.defaultExtraIds = defaultExtraIds;
	}

	public List<WeightItem<Integer>> getRandomBasic() {
		return randomBasic;
	}

	public void setRandomBasic(List<WeightItem<Integer>> randomBasic) {
		this.randomBasic = randomBasic;
	}

	public int getProfLimit() {
		return profLimit;
	}

	public List<ItemInfo> getRecastConsumeInfo() {
		return recastConsumeInfo;
	}

	public void setRecastConsumeInfo(List<ItemInfo> recastConsumeInfo) {
		this.recastConsumeInfo = recastConsumeInfo;
	}

	public int getIsInitAttr() {
		return isInitAttr;
	}

	public List<WeightItem<Integer>> getRandomExtra2Pool() {
		return randomExtra2Pool;
	}

	public void setRandomExtra2Pool(List<WeightItem<Integer>> randomExtra2Pool) {
		this.randomExtra2Pool = randomExtra2Pool;
	}

	public List<WeightItem<Integer>> getRandomExtra3Pool() {
		return randomExtra3Pool;
	}

	public void setRandomExtra3Pool(List<WeightItem<Integer>> randomExtra3Pool) {
		this.randomExtra3Pool = randomExtra3Pool;
	}

	public List<WeightItem<Integer>> getRandomExtra4Pool() {
		return randomExtra4Pool;
	}

	public void setRandomExtra4Pool(List<WeightItem<Integer>> randomExtra4Pool) {
		this.randomExtra4Pool = randomExtra4Pool;
	}

	public List<WeightItem<Integer>> getRecastCommonPool() {
		return recastCommonPool;
	}

	public void setRecastCommonPool(List<WeightItem<Integer>> recastCommonPool) {
		this.recastCommonPool = recastCommonPool;
	}

	public List<WeightItem<Integer>> getRecastAdvancePool() {
		return recastAdvancePool;
	}

	public void setRecastAdvancePool(List<WeightItem<Integer>> recastAdvancePool) {
		this.recastAdvancePool = recastAdvancePool;
	}

	public int getExtraCountLimit() {
		return extraCountLimit;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public int getMaxLevel() {
		return maxLevel;
	}
}
