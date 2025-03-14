package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/rechargeReturnCost124.xml", struct = "map")
public class RechargeReturnCost124Cfg extends ConfigBase {

    /**
     * 配置id
     */
    @Id
    private final int id;

    /**
     * 次数
     */
    private final int times;

    /**
     * 消耗
     */
    private final int cost;


    public RechargeReturnCost124Cfg() {
        this.id = 0;
        this.times = 0;
        this.cost = 0;
    }

    public int getId() {
        return id;
    }

    public int getTimes() {
        return times;
    }

    public int getCost() {
        return cost;
    }

    /**
     * 数据格式转化
     */
    @Override
    protected boolean assemble() {
        return true;
    }

    /**
     * 检测有消息
     *
     * @return
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
