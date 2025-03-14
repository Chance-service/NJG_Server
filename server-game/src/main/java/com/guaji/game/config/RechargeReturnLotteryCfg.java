package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;


@ConfigManager.XmlResource(file = "xml/rechargeReturnLottery.xml", struct = "map")
public class RechargeReturnLotteryCfg extends ConfigBase {

    /**
     * 配置id
     */
    @Id
    private final int Id;

    /**
     * 彩票类型
     */
    private final int ticketType;

    /**
     * 概率开始值
     */
    private final int startRand;

    /**
     * 概率终止值
     */
    private final int endRand;

    /**
     * 奖励物品
     */
    private final int awards;


    public RechargeReturnLotteryCfg() {
        Id = 0;
        ticketType = 0;
        startRand = 0;
        endRand = 0;
        awards = 0;
    }

    public int getId() {
        return Id;
    }

    public int getTicketType() {
        return ticketType;
    }

    public int getStartRand() {
        return startRand;
    }

    public int getEndRand() {
        return endRand;
    }

    public int getAwards() {
        return awards;
    }
}
