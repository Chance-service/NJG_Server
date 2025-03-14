package com.guaji.game.config;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;

@ConfigManager.XmlResource(file = "xml/wordsExchange.xml", struct = "list")
public class WordsExchangeCfg extends ConfigBase {
	/**
	 * 活动类型 1.终极豪礼 2.乐享好礼 3.节日好礼 4.单字大礼
	 */
	private final int type;
	/**
	 * 需要的道具字符串
	 */
	private final String itemNeed ;
	
	private List<ItemInfo> needItems ;
	
	/**
	 * 兑换出来的字符串
	 */
	private final String itemObtain;
	
	private AwardItems awardItems;
	
	/**
	 * 每日兑换次数限制
	 */
	private final int dailyLimit ;
	
	private static Set<Integer> allTypes = new HashSet<>();;
	
	public WordsExchangeCfg(){
		this.type = 0;
		this.dailyLimit = 0;
		this.itemNeed = null;
		this.itemObtain = "";
		needItems = new LinkedList<>();
	}
	
	
	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		if(itemNeed != null && !"".equals(itemNeed)) {
			String[] strs = itemNeed.split(",");
			for(String str : strs) {
				needItems.add(ItemInfo.valueOf(str));
			}
		}
		if(itemObtain != null && !"".equals(itemObtain)) {
			awardItems = AwardItems.valueOf(itemObtain);
		}
		
		allTypes.add(type);
		
		return true;
	}
	
	/**
	 * 检测有消息
	 * @return
	 */
	@Override
	protected boolean checkValid() {
		return true;
	}

	public AwardItems getAwardItems() {
		return awardItems;
	}

	public void setAwardItems(AwardItems awardItems) {
		this.awardItems = awardItems;
	}

	public List<ItemInfo> getNeedItems() {
		return needItems;
	}

	public void setNeedItems(List<ItemInfo> needItems) {
		this.needItems = needItems;
	}


	public int getType() {
		return type;
	}

	public int getDailyLimit() {
		return dailyLimit;
	}
	
	/**
	 * 获得兑换字的配置
	 * @param type
	 * @param needItemId
	 * @return
	 */
	public static WordsExchangeCfg getWordsExchangeCfg(int type,int needItemId) {
		for(WordsExchangeCfg exchangeCfg : ConfigManager.getInstance().getConfigList(WordsExchangeCfg.class)) {
			if(exchangeCfg.getType() == type && (needItemId == 0 || exchangeCfg.containsNeedItem(needItemId))) {
				return exchangeCfg;
			}
		}
		return null;
	}

	private boolean containsNeedItem(int needItemId) {
		for(ItemInfo itemInfo : needItems) {
			if(itemInfo.getItemId() == needItemId) {
				return true;
			}
		}
		return false;
	}

	public static Set<Integer> getAllTypes() {
		return allTypes;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
	
}

