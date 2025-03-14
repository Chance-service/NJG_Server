package com.guaji.game.config;

import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;

@ConfigManager.XmlResource(file = "xml/ActivityExchange.xml", struct = "map")
public class ActivityExchange176 extends ConfigBase {
    @Id
    private final int exchangeId;
    /**
     * 	適用活動ID
     */
    private final int ActivityID;
    /**
     * 	最大兌換次數
     */
    private final int maxExchangeTimes;
    /**
     * 	欲兌換物
     */
    private final String awardInfo;
    /**
     * 	消耗物
     */
    private final String consumeInfo;
    /**
     * 	欲兌換物列表
     */
    private AwardItems exchangeAwardInfo;
    /**
     * 	消耗物列表
     */
    private List<ItemInfo> exchangeConsumeInfo;

    public ActivityExchange176() {
        exchangeId = 0;
        ActivityID = 0;
        maxExchangeTimes = 0;
        consumeInfo = null;
        awardInfo = null;
        exchangeAwardInfo = null;
        exchangeConsumeInfo = null;
    }

    public AwardItems getExchangeAwardInfo() {
        return exchangeAwardInfo;
    }

    public void setExchangeAwardInfo(AwardItems exchangeAwardInfo) {
        this.exchangeAwardInfo = exchangeAwardInfo;
    }

    public List<ItemInfo> getExchangeConsumeInfo() {
        return exchangeConsumeInfo;
    }

    public void setExchangeConsumeInfo(List<ItemInfo> exchangeConsumeInfo) {
        this.exchangeConsumeInfo = exchangeConsumeInfo;
    }

    public int getExchangeId() {
        return exchangeId;
    }

    public int getMaxExchangeTimes() {
        return maxExchangeTimes;
    }
    
    public int getActivityID() {
		return ActivityID;
	}
    
    
	public String getAwardInfo() {
		return awardInfo;
	}

	public String getConsumeInfo() {
		return consumeInfo;
	}

	@Override
    protected boolean assemble() {
        if (this.consumeInfo != null && this.consumeInfo.length() > 0 && !this.consumeInfo.equals("0")) {
            this.setExchangeConsumeInfo(ItemInfo.valueListOf(this.consumeInfo));
        }
        if (this.awardInfo != null && this.awardInfo.length() > 0 && !this.awardInfo.equals("0")) {
            this.setExchangeAwardInfo(AwardItems.valueOf(this.awardInfo));
        }
        return super.assemble();
    }

    /**
     * 清理相关静态数据
     */
    protected void clearStaticData() {
    }

    @Override
    protected boolean checkValid() {
        return true;
    }

}
