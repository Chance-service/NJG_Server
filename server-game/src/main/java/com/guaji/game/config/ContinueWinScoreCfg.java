package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 连胜加积分配置
 * 
 * @author Nannan.Gao
 * @date 2017-8-21 11:14:07
 */
@ConfigManager.XmlResource(file = "xml/continueWinScore.xml", struct = "list")
public class ContinueWinScoreCfg extends ConfigBase {
	
	/**
	 * 连胜次数
	 */
    private final int winTimes;
    
    /**
     * 添加积分
     */
    private final int score;
    
    
    public ContinueWinScoreCfg() {
    	winTimes = 0;
    	score = 0;
    }
    
	public int getWinTimes() {
		return winTimes;
	}

	public int getScore() {
		return score;
	}

	@Override
	protected boolean assemble() {
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	@Override
	public String toString() {
		return "winTimes:" + winTimes + "  score:" + score;
	}
}
