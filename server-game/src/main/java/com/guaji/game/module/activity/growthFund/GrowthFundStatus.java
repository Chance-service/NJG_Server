package com.guaji.game.module.activity.growthFund;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigManager;

import com.guaji.game.config.GrowthFundCfg;
import com.guaji.game.config.SysBasicCfg;

public class GrowthFundStatus {

	/**
	 * 是否购买
	 */
	private boolean bought;
	
	/**
	 * 领取的奖励ID
	 */
	private List<Integer> rewardIds = new ArrayList<Integer>();

	public boolean isBought() {
		return bought;
	}

	public List<Integer> getRewardIds() {
		return rewardIds;
	}

	public void setBought(boolean bought) {
		this.bought = bought;
	}
	
	/**
	 * 对应配置对应的奖励是否已经领取过
	 * 
	 * @param cfgId
	 * @return
	 */
	public boolean hasGot(int rewardId) {
		if (rewardIds.contains(rewardId)) {
			return true;
		}
		return false;
	}

	/**
	 * 添加领取过的奖励ID
	 */
	public void addRewardId(int rewardId) {
		rewardIds.add(rewardId);
	}
	
	/**
	 * 是否红点提示，VIP等级满足为购买，或已购买有奖励未领取
	 */
	public boolean showRedPoint(int vipLevel, int level){
		
		if(vipLevel >= SysBasicCfg.getInstance().getGrowthVipLevel() && !bought){
			//未购买
			return true;
		}else if(bought){
			//已购买
			Map<Object,GrowthFundCfg> cfgs = ConfigManager.getInstance().getConfigMap(GrowthFundCfg.class);
			if(cfgs != null && cfgs.size() > 0){
				for(GrowthFundCfg cfg : cfgs.values()){
					if(level >= cfg.getLevelLimit() && !rewardIds.contains(cfg.getId())){
						return true;
					}
				}
			}
		}
		
		return false;
	}
}
