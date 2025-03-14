package com.guaji.game.config;

import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.AwardItems;

@ConfigManager.XmlResource(file = "xml/allianceBattleReward.xml", struct = "list")
public class AllianceBattleRewardCfg extends ConfigBase {

	private final int top ;
	
	private final int position ;
	
	private final String reward ;
	
	private AwardItems awardItems ;
	
	private final String getKey ;
	
	private final String gameType ;
	
	private final String positionShow ;

	public AllianceBattleRewardCfg() {
		this.top = 0;
		this.position = 0;
		this.reward = null;
		this.getKey = null;
		this.gameType = null;
		this.positionShow = null;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		if(reward != null && reward.length() > 0) {
			this.awardItems = AwardItems.valueOf(reward);
		}
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

	public static AllianceBattleRewardCfg getRewardCfg(int top, int position) {
		List<AllianceBattleRewardCfg> rewardCfgs = ConfigManager.getInstance().getConfigList(AllianceBattleRewardCfg.class);
		for(AllianceBattleRewardCfg rewardCfg : rewardCfgs) {
			if(rewardCfg != null && rewardCfg.top == top && rewardCfg.position == position) {
				return rewardCfg;
			}
		}
		return null;
	}
	
	public int getTop() {
		return top;
	}

	public int getPosition() {
		return position;
	}

	public String getReward() {
		return reward;
	}

	public AwardItems getAwardItems() {
		return awardItems;
	}

	public void setAwardItems(AwardItems awardItems) {
		this.awardItems = awardItems;
	}

	public String getGetKey() {
		return getKey;
	}

	public String getGameType() {
		return gameType;
	}

	public String getPositionShow() {
		return positionShow;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
