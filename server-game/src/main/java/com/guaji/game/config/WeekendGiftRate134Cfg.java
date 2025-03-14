package com.guaji.game.config;

import java.util.HashMap;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/weekendGiftRate134.xml", struct = "map")
public class WeekendGiftRate134Cfg extends ConfigBase {

    /**
     * 配置id
     */
    @Id
    private final int id;

    /**
     * 类型（同星期中的第几天开启配置）
     */
    private final int multiple;

    /**
     * 奖励
     */
    private final int rate;

    /**
     * 奖池掉落物权重分布
     */
    private static Map<Integer, Integer> poolItemsMap = new HashMap<>();


    public WeekendGiftRate134Cfg() {
        this.id = 0;
        this.multiple = 0;
        this.rate = 0;
    }

    public int getId() {
        return id;
    }

    public int getMultiple() {
        return multiple;
    }

    public int getRate() {
        return rate;
    }


    public static int calcRate(int rand) {
        int acc = 0;
        for (Map.Entry<Integer, Integer> entry : poolItemsMap.entrySet()) {
            acc += entry.getValue();
            if (rand <= acc) {
                return entry.getKey();
            }
        }
        return 0;
    }


    /**
     * 数据格式转化
     */
    @Override
    protected boolean assemble() {
        poolItemsMap.put(this.multiple, this.rate);
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
