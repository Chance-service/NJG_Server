package com.guaji.game.config;

import java.util.LinkedList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.ItemInfo;
import com.guaji.game.protocol.Battle.DropAward;

@ConfigManager.XmlResource(file = "xml/eighteenprinces.xml", struct = "map")
public class EighteenPrincesCfg extends ConfigBase {

	@Id
	private final int id;

	/**
	 * 挑战的名次
	 */
	private final int challengeArenaRank;

	/**
	 * 奖励物品
	 */
	private final String mustRewards;

	/**
	 * 奖励物品
	 */
	private final String randRewards;



	/**
	 * 物品id
	 */
	protected List<ItemInfo> itemInfos;
	/**
	 * 权重
	 */
	protected List<Integer> itemWeights;

	/**
	 * 掉落信息
	 */
	DropAward.Builder dropAward;

	public EighteenPrincesCfg() {
		id = 0;
		mustRewards = "";
		randRewards = "";
		challengeArenaRank = 0;

		itemInfos = new LinkedList<ItemInfo>();
		itemWeights = new LinkedList<Integer>();

	}

	public int getId() {
		return id;
	}

	public int getChallengeArenaRank() {
		return challengeArenaRank;

	}

	@Override
	protected boolean assemble() {
		itemInfos.clear();
		itemWeights.clear();



		if (this.randRewards != null && this.randRewards.length() > 0) {
			String[] itemWeightArray = this.randRewards.split(",");
			for (String itemWeight : itemWeightArray) {
				String[] items = itemWeight.split("_");
				if (items.length != 4) {
					return false;
				}
				itemInfos.add(ItemInfo.valueOf(Integer.valueOf(items[0]), Integer.valueOf(items[1]),
						Integer.valueOf(items[2])));
				itemWeights.add(Integer.valueOf(items[3]));
			}
		}

		return true;
	}

	@Override
	protected boolean checkValid() {
		/*
		for (ItemInfo itemInfo : itemInfos) {
			if (!ConfigUtil.check(itemInfo.getType(), itemInfo.getItemId())) {
				return false;
			}
		}*/
		return true;
	}

	public String getMustRewards() {
		return mustRewards;
	}

	public String getRandRewards() {
		return randRewards;
	}

	public List<ItemInfo> getItemInfos() {
		return itemInfos;
	}

	public List<Integer> getItemWeights() {
		return itemWeights;
	}

}
