
package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/accConsumeItem.xml", struct = "list")
public class AccConsumeItemCfg extends ConfigBase {

	/**
	 * 配置id
	 */
	@Id
	private final int id;

	/**
	 * 活动编号
	 */
	private final int activityId;

	/**
	 * @Fields conItemType :消耗的道具类型
	 */
	private final int conItemType;
	/**
	 * 目标档位编号
	 */
	private final int cfgId;

	/**
	 * 领奖消耗目标道具次数
	 */
	private final int needTimes;

	/**
	 * 奖励
	 */
	private final String awards;

	public AccConsumeItemCfg() {
		this.id = 0;
		this.needTimes = 0;
		this.awards = "";
		this.activityId = 0;
		this.cfgId = 0;
		this.conItemType = 0;
	}

	public int getId() {
		return id;
	}

	public String getAwards() {
		return awards;
	}

	public int getActivityId() {
		return activityId;
	}

	public int getCfgId() {
		return cfgId;
	}

	public int getConItemType() {
		return conItemType;
	}

	public int getNeedTimes() {
		return needTimes;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		return true;
	}

	/**
	 * 检测有消息
	 * 
	 * @return
	 */
	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

	/**
	 * @param cfgId 周期编号
	 * @return
	 */
	public static List<AccConsumeItemCfg> getConsumeItems(int cfgId) {
		List<AccConsumeItemCfg> list = new ArrayList<AccConsumeItemCfg>();
		List<AccConsumeItemCfg> rewardCfgs = ConfigManager.getInstance().getConfigList(AccConsumeItemCfg.class);
		for (AccConsumeItemCfg itemCfg : rewardCfgs) {
			if (itemCfg.getCfgId() == cfgId) {
				list.add(itemCfg);
			}
		}
		return list;
	}

	/**
	 * @param cfgId 周期编号
	 * @return
	 */
	public static AccConsumeItemCfg getConsumeItemsByItemType(int cfgId, int itemType) {
		List<AccConsumeItemCfg> rewardCfgs = ConfigManager.getInstance().getConfigList(AccConsumeItemCfg.class);
		for (AccConsumeItemCfg itemCfg : rewardCfgs) {
			if (itemCfg.getCfgId() == cfgId && itemCfg.getConItemType() == itemType) {
				return itemCfg;
			}
		}
		return null;
	}

	public static AccConsumeItemCfg getConsumeItemById(int goodId) {
		List<AccConsumeItemCfg> rewardCfgs = ConfigManager.getInstance().getConfigList(AccConsumeItemCfg.class);
		for (AccConsumeItemCfg rewardCfg : rewardCfgs) {
			if (rewardCfg != null && rewardCfg.getId()== goodId) {
				return rewardCfg;
			}
		}
		return null;
	}
}
