package com.guaji.game.module.activity.recharge;

import java.util.HashMap;
import java.util.Map;

import com.guaji.game.config.ActivityCfg;
import com.guaji.game.config.ActivityCfg.ActivityItem;
import com.guaji.game.protocol.Const;


/**
 * 充值双倍活动记录
 * @author xpf
 */
public class DoubleRechargeStatus {
	
	/**
	 * 充值信息
	 */
	private Map<Integer, Integer> rechargeMap = new HashMap<Integer, Integer>();
	
	/**
	 * 能否触发
	 * @param goodsId 
	 * @return
	 */
	public boolean canTrigger(int goodsId){
		ActivityItem activityItem = ActivityCfg.getActivityItem(Const.ActivityId.DOUBLE_RECHARGE_VALUE);
		if(activityItem != null) {
			int type = activityItem.getParam("type");
			if(type == 1) {
				// 所有项只有一项能触发
				if(rechargeMap.size() == 0){
					return true;
				}
			}else if(type == 2){
				if(rechargeMap.containsKey(goodsId) && rechargeMap.get(goodsId) > 0) {
					return false;
				}else{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * 设置商品充值
	 */
	public void setRecharge(int goodsId, int rechargeNum) {
		if(!rechargeMap.containsKey(goodsId)) {
			rechargeMap.put(goodsId, rechargeNum);
		}else{
			rechargeMap.put(goodsId, rechargeMap.get(goodsId) + rechargeNum);
		}
	}

}
