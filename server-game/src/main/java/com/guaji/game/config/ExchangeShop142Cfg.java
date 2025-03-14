package com.guaji.game.config;

import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;

@ConfigManager.XmlResource(file = "xml/exchangeShop142.xml", struct = "map")
public class ExchangeShop142Cfg extends ConfigBase {
    @Id
    private final String exchangeId;

    private final int maxExchangeTimes;

    private final String awardInfo;
    private final String consumeInfo;

    private AwardItems exchangeAwardInfo;

    private List<ItemInfo> exchangeConsumeInfo;

    /**
     * 兑换次数是否需要重置
     */
    protected final int isReset;

    public ExchangeShop142Cfg() {
        exchangeId = null;
        maxExchangeTimes = 0;
        consumeInfo = null;
        awardInfo = null;
        exchangeAwardInfo = null;
        exchangeConsumeInfo = null;
        isReset = 0;
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

    public String getExchangeId() {
        return exchangeId;
    }

    public int getMaxExchangeTimes() {
        return maxExchangeTimes;
    }

    public boolean getIsReset() {
        return isReset == 1;
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
