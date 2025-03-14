package com.guaji.game.config;

import java.util.Collection;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/releaseUrRank175Award.xml", struct = "map")
public class ReleaseGHRank175AwardCfg extends ConfigBase{

    /**
     * 配置id
     */
    @Id
    private final int id;
    /**
     * 最小符合發獎名次
     */
    private final int minRank;
    /**
     * 每日发放奖励
     */
    private final String DailyAward;
    
    /**
     * 季賽最高分发放奖励
     */
    private final String TotalAward;
    /**
     * 最大獎勵名次
     */
    private static int  maxRewardNum;



    public ReleaseGHRank175AwardCfg() {
        this.id = 0;
        this.minRank = 0;
        this.DailyAward="";
        this.TotalAward="";
        maxRewardNum = 0;
    }

    public int getId() {
        return id;
    }
    
    
    
	public int getMinRank() {
		return minRank;
	}

	public String getDailyAward() {
		return DailyAward;
	}

	public String getTotalAward() {
		return TotalAward;
	}
	
	public static int getMaxRewardNum() {
		return maxRewardNum;
	}

	@Override
    protected boolean assemble() {
		
		if (getMinRank() > maxRewardNum) {
			maxRewardNum = getMinRank();
		}
		
        return true;
    }

    @Override
    protected boolean checkValid() {

        return true;
    }
    
    public static ReleaseGHRank175AwardCfg getCfgbyRank(int rank) {
    	Collection<ReleaseGHRank175AwardCfg> cfgList = ConfigManager.getInstance().getConfigMap(ReleaseGHRank175AwardCfg.class).values();
    	for (ReleaseGHRank175AwardCfg acfg : cfgList) {
    		if (rank <= acfg.getMinRank()) {
    			return acfg;
    		} 
    	}
    	return null;
    }
}
