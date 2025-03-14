package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.AwardItems;
import com.guaji.game.protocol.Reward.RewardItem;


/**
 * FaceBook绑定奖励
 * @author Nannan.Gao
 * @date Thu Sep 10 03:30:32 EDT 201
 */
@ConfigManager.XmlResource(file = "xml/fbAccountBindReward.xml", struct = "list")
public class FBAccountBindRewardCfg extends ConfigBase {
	
	/**
	 * 数据配置表ID
	 */
	private final int id;
	
	/**
	 * 奖励数据
	 */
	private final String items;
	
	/**
	 * 奖励数据转换
	 */
	public static List<RewardItem.Builder> rewardItems = new ArrayList<RewardItem.Builder>();
	
	/**
	 * 邮件奖励对象
	 */
	public static AwardItems awardItems;
	
	public FBAccountBindRewardCfg() {
		
		this.id = 0;
		this.items = "";
	}

	@Override
	protected boolean assemble() {
		
		changeItemsObj();
		return true;
	}
	
	@Override
	protected boolean checkValid() {
		return true;
	}
	
	private void changeItemsObj () {
		
		String[] itemArray = items.split(",");
		for (int i = 0; i < itemArray.length; i++) {
			String[] items = itemArray[i].split("_");
			if (items.length >= 3) {
				RewardItem.Builder builder = RewardItem.newBuilder();
				builder.setItemType(Integer.parseInt(items[0]));
				builder.setItemId(Integer.parseInt(items[1]));
				builder.setItemCount(Integer.parseInt(items[2]));
				rewardItems.add(builder);
			}
		}
		awardItems = AwardItems.valueOf(items);
	}

	public int getId() {
		return id;
	}
	
	public String getItems() {
		return items;
	}
		
	@Override
	public String toString() {
		return "id:" + id + " items:" + items;
	}
}
