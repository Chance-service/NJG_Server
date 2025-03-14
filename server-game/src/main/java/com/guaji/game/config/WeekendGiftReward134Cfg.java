package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/weekendGiftReward134.xml", struct = "map")
public class WeekendGiftReward134Cfg extends ConfigBase {

    /**
     * 配置id
     */
    @Id
    private final int id;

    /**
     * 类型（同星期中的第几天开启配置）
     */
    private final int type;

    /**
     * 奖励
     */
    private final String awards;


    public WeekendGiftReward134Cfg() {
        this.id = 0;
        this.type = 0;
        this.awards = "";
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getAwards() {
        return awards;
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
