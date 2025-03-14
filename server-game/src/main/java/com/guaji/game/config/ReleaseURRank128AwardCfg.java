package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/releaseUrRank128Award.xml", struct = "map")
public class ReleaseURRank128AwardCfg extends ConfigBase{

    /**
     * 配置id
     */
    @Id
    private final int id;


    /**
     * 发放奖励
     */
    private final String awardStr;



    public ReleaseURRank128AwardCfg() {
        this.id = 0;
        awardStr="";
    }

    public int getId() {
        return id;
    }

    

    public String getAwardStr() {
		return awardStr;
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
