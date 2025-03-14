package com.guaji.game.module.activity.expeditionArmory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.ExpeditionArmoryStageCfg;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.protocol.Activity.ExpeditionArmoryStage;

/**
 * 远征物资 个人状态
 * 
 * @author xulinqs
 */
public class ExpeditionArmoryStatus implements Comparable<ExpeditionArmoryStatus> {
	private int playerId;
	private int rank;
	private String playerName;
	private long updateTime;
	private Map<Integer, OneStagePersonalStatus> expeditionMap;
	//个人领取最后奖励状态
	private int takeLastAwardFlag;

	public ExpeditionArmoryStatus() {
		playerId = 0;
		rank = 0;
		playerName = "";
		takeLastAwardFlag = 0;
		updateTime = GuaJiTime.getMillisecond();
		expeditionMap = new ConcurrentHashMap<Integer, OneStagePersonalStatus>();
	}
	
	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
	
	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public long getUpdateTime() {
		return updateTime;
	}

	public void addExp(int stage, int exp) {
		
		OneStagePersonalStatus personalStatus = null;
		if (expeditionMap.containsKey(stage)) {
			personalStatus = expeditionMap.get(stage);
		} else {
			personalStatus = new OneStagePersonalStatus(stage);
		}
		personalStatus.addPersonalStageExp(exp);
	
		BehaviorLogger.log4Service(playerId, Source.USER_OPERATION, Action.EXPEDITION_EXP_ADD, Params.valueOf("stage",  stage), Params.valueOf("addExp", exp), Params.valueOf("afterExp", personalStatus.getPersonalStageExp()));
		expeditionMap.put(stage, personalStatus);
		this.updateTime = GuaJiTime.getMillisecond();
	}
		
	public int getStageExp(int stage) {
		if (expeditionMap.containsKey(stage)) {
			return expeditionMap.get(stage).getPersonalStageExp();
		}
		return 0;
	}

	public int getTotalExp() {
		int totalValue = 0;
		for (OneStagePersonalStatus personalStatus : expeditionMap.values()) {
			totalValue += personalStatus.getPersonalStageExp();
		}
		return totalValue;
	}

	public List<ExpeditionArmoryStage.Builder> getAllStageStatusBuilder(){
		build();
		List<ExpeditionArmoryStage.Builder> list = new ArrayList<ExpeditionArmoryStage.Builder>();
		for(Map.Entry<Integer, OneStagePersonalStatus> entry : expeditionMap.entrySet()){
			list.add(entry.getValue().genExpeditionArmoryStageBuilder());
		}
		return list;
	}
	
	public int getTakeLastStageAwardFlag()
	{
		return takeLastAwardFlag;
	}
	
	public void setTakeLastStageAwardFlag(int flag)
	{
		takeLastAwardFlag = flag;
		return;
	}

	/**
	 * 构造活动全阶段缓存对象
	 */
	private void build(){
		Map<Object, ExpeditionArmoryStageCfg> cfgs = ConfigManager.getInstance().getConfigMap(ExpeditionArmoryStageCfg.class);
		if(expeditionMap.size() < cfgs.size()){
			for(ExpeditionArmoryStageCfg cfg : cfgs.values()){
				if(!expeditionMap.containsKey(cfg.getStage())){
					OneStagePersonalStatus personalStatus = new OneStagePersonalStatus(cfg.getStage());
					expeditionMap.put(personalStatus.getStage(), personalStatus);
				}
			}
		}
	}
	
	@Override
	public int compareTo(ExpeditionArmoryStatus o) {
		if (getPlayerId() == o.getPlayerId()) {
			return 0;
		}
		int totalExp1 = o.getTotalExp();
		int totalExp2 = getTotalExp();
		if (totalExp1 != totalExp2) {
			return totalExp1 - totalExp2;
		} else {
			return (getUpdateTime() - o.getUpdateTime()) > 0 ? 1 : -1;
		}
	}

}
