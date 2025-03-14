package com.guaji.game.config;

import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 公会商店表
 */
@ConfigManager.XmlResource(file = "xml/allianceShop.xml", struct = "list")
public class AllianceShopCfg extends ConfigBase {

	/**
	 * 商品编号
	 */
	@Id
	private final int id;

	/**
	 * 幸运值
	 */
	private final int luckyScore;

	/**
	 * 物品ID
	 */
	private final int itemId;

	/**
	 * 物品类型
	 */
	private final int itemType;

	/**
	 * 物品数量
	 */
	private final int itemCount;

	/**
	 * 购买需要消耗的贡献值
	 */
	private final int contribution;

	/**
	 * 商品权重
	 */
	private final int weight;

	public AllianceShopCfg() {

		this.id = 0;
		this.luckyScore = 0;
		this.itemId = 0;
		this.itemType = 0;
		this.itemCount = 0;
		this.contribution = 0;
		this.weight = 0;
	}

	public int getId() {
		return id;
	}

	public int getLuckyScore() {
		return luckyScore;
	}

	public int getItemId() {
		return itemId;
	}

	public int getItemType() {
		return itemType;
	}

	public int getItemCount() {
		return itemCount;
	}

	public int getContribution() {
		return contribution;
	}

	public int getWeight() {
		return weight;
	}

	@Override
	protected boolean assemble() {
		return super.assemble();
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	 * 获取商品
	 * 
	 * @param id
	 * @return
	 */
	public static AllianceShopCfg getItemInfo(int id) {
		List<AllianceShopCfg> list = ConfigManager.getInstance().getConfigList(AllianceShopCfg.class);
		for (AllianceShopCfg cfg : list) {
			if (cfg.getId() == id) {
				return cfg;
			}
		}
		return null;
	}

}
