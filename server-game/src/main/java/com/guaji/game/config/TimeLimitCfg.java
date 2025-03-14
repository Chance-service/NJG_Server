package com.guaji.game.config;

import java.util.LinkedList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.ItemInfo;

@ConfigManager.XmlResource(file = "xml/timeLimit.xml", struct = "map")
public class TimeLimitCfg extends ConfigBase {
	@Id
	private final int id;

	private final String items;

	private final String limitGetItem;

	private List<ItemInfo> itemInfoList;

	private final int serverLimit;

	private final int serverLimitType;

	private final int personalLimit;

	private final int personalLimitType;

	private final int vipLimit;

	private final int levelLimit;

	private final int price;


	public TimeLimitCfg() {
		this.id = 0;
		this.items = null;
		this.serverLimit = 0;
		this.personalLimit = 0;
		this.vipLimit = 0;
		this.levelLimit = 0;
		this.price = 0;
		this.personalLimitType = 0;
		this.serverLimitType = 0;
		this.limitGetItem = null;
	}

	@Override
	protected boolean assemble() {
		if (this.items != null) {
			this.itemInfoList = new LinkedList<>();
			String[] itemInfoStrs = this.items.split(",");
			for (String itemInfoString : itemInfoStrs) {
				ItemInfo itemInfo = ItemInfo.valueOf(itemInfoString);
				this.itemInfoList.add(itemInfo);
			}
		}

		

		return super.assemble();
	}

	@Override
	protected boolean checkValid() {
		return super.checkValid();
	}

	public int getId() {
		return id;
	}

	public String getItems() {
		return items;
	}

	public int getServerLimit() {
		return serverLimit;
	}

	public int getPersonalLimit() {
		return personalLimit;
	}

	public int getVipLimit() {
		return vipLimit;
	}

	public int getLevelLimit() {
		return levelLimit;
	}

	public int getPrice() {
		return price;
	}

	public int getServerLimitType() {
		return serverLimitType;
	}

	public int getPersonalLimitType() {
		return personalLimitType;
	}


	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

	public String getLimitGetItem() {
		return limitGetItem;
	}




}
