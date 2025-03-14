package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.log.Log;

import com.guaji.game.item.ItemInfo;

@ConfigManager.XmlResource(file = "xml/WishingMilestone147.xml", struct = "map")
public class WishingMilestoneCfg extends ConfigBase {
	/**
	 * 配置id;
	 */
	@Id
	private final int id;
	/**
	 * 幸運度
	 */
	private final String points;
	/**
	 * 對應獎勵
	 */
	private final String reward ;
	/**
	 * 可兌換幸運度
	 */
	private List<Integer> PointList;
	/**
	 * 相對應獎勵
	 */
	private List<ItemInfo> rewardList;

	public WishingMilestoneCfg() {
		// 初始化值
		this.id = 0;
		this.points = "";
		this.reward = "";
		this.PointList = new ArrayList<Integer>();
		this.rewardList = new ArrayList<ItemInfo>();
	}


	public static WishingMilestoneCfg getWingCfgByKey(int key) {
		WishingMilestoneCfg wellCfg = ConfigManager.getInstance().getConfigByKey(WishingMilestoneCfg.class, key);
		return wellCfg;
	}
	
	public int getId() {
		return id;
	}

	public List<Integer> getPointList() {
		return PointList;
	}

	public List<ItemInfo>  getRewardList() {
		return rewardList;
	}
	
	@Override
	protected boolean assemble() {
		
		PointList.clear();
		if (StringUtils.isNotEmpty(points)){
			String[] conuts = points.split(",");
			for (String aconut : conuts) {
				PointList.add(Integer.valueOf(aconut.trim()));
			}
		}
		rewardList.clear();
		if (StringUtils.isNotEmpty(reward)){
			rewardList = ItemInfo.valueListOf(reward);
		}
		
		if (PointList.size() != rewardList.size()) {
			Log.errPrintln("WishingMilestone147.xml reward size error");
			return false;
		}
		
		return true;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
		
	}
	
}