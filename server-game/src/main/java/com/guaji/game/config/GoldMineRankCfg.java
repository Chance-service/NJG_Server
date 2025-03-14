package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/GoldMineRank.xml", struct = "map")
public class GoldMineRankCfg extends ConfigBase{

    /**
     * 配置id
     */
    @Id
    private final int ID;


    /**
     * 发放奖励
     */
    private final String Awards;



    public GoldMineRankCfg() {
        this.ID = 0;
        Awards="";
    }

    public int getId() {
        return ID;
    }

    

    public String getAwardStr() {
		return Awards;
	}

	@Override
    protected boolean assemble() {
        return true;
    }

    @Override
    protected boolean checkValid() {

        return true;
    }
}
