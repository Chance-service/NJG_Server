package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.ItemInfo;

@ConfigManager.XmlResource(file = "xml/honorShop.xml", struct = "list")
public class HonorShopCfg extends ConfigBase {
	/**
	 * id
	 */
	@Id
	private final int id;
	/**
	 * 道具
	 */
	private final String itemStr;

	private ItemInfo item;
	

	public HonorShopCfg() {
		id = 0;
		itemStr = "";
		item = null;
	}

	@Override
	protected boolean assemble() {
		String[] es = itemStr.split("_");
		ItemInfo itemInfo = new ItemInfo();
		itemInfo.setType(Integer.valueOf(es[0]));
		itemInfo.setItemId(Integer.valueOf(es[1]));
		itemInfo.setQuantity(Integer.valueOf(es[2]));
		this.item = itemInfo;
		return super.assemble();
	}

	@Override
	protected boolean checkValid() {
		return super.checkValid();
	}
	/**
	 * 配置ID
	 * @return
	 */
	public int getId() {
		return id;
	}
	/**
	 * 物品对象
	 * @return
	 */
	public ItemInfo getItem() {
		return item;
	}
}
