package com.guaji.game.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

//import com.mysql.jdbc.StringUtils;

/**
 * 万能碎片兑换活动配置
 * 
 * @author Melvin.Mao
 * @date Jun 12, 2017 2:39:36 PM
 */
@ConfigManager.XmlResource(file = "xml/fragmentExchange.xml", struct = "map")
public class FragmentExchangeCfg extends ConfigBase {

	public static final int EXCHANGE_TYPE_COMMON = 1;
	public static final int EXCHANGE_TYPE_ADVANCED = 2;

	/**
	 * 配置id
	 */
	@Id
	private final int id;

	/**
	 * 兑换类型（1:普通;2:高级）
	 */
	private final int type;

	/**
	 * 被兑换的碎片集合
	 */
	private final String costFragment;

	/**
	 * 消耗的物品
	 */
	private final String costItem;

	/**
	 * 目标碎片
	 */
	private final String rewardFragement;

	/**
	 * 将index和costFragment集合对应起来
	 */
	public static Map<Integer, List<String>> costFragmentMap = new HashMap<Integer, List<String>>();

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public String getCostFragment() {
		return costFragment;
	}

	public String getCostItem() {
		return costItem;
	}

	public String getRewardFragement() {
		return rewardFragement;
	}

	public Map<Integer, List<String>> getCostFragmentMap() {
		return costFragmentMap;
	}

	public FragmentExchangeCfg() {
		this.id = 0;
		this.type = 0;
		this.costFragment = "";
		this.costItem = "";
		this.rewardFragement = "";
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		if ((costFragment == null)||(costFragment).isEmpty()){
			return false;
		}
		String[] costFragments = this.costFragment.split(",");
		List<String> list = Arrays.asList(costFragments);
		costFragmentMap.put(this.id, list);
		return true;
	}

	/**
	 * 检测有消息
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

}
