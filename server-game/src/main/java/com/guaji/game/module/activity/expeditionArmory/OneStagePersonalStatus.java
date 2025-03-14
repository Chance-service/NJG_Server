package com.guaji.game.module.activity.expeditionArmory;

import org.guaji.config.ConfigManager;

import com.guaji.game.config.ExpeditionArmoryStageCfg;
import com.guaji.game.entity.ExpeditionArmoryEntity;
import com.guaji.game.manager.ExpeditionArmoryManager;
import com.guaji.game.protocol.Activity.ExpeditionArmoryStage;

public class OneStagePersonalStatus {
	// 活动阶段
	private int stage;
	// 阶段个人经验
	private int personalStageExp;

	
	public OneStagePersonalStatus(){
	}
	
	public OneStagePersonalStatus(int stage){
		this.stage = stage;
		this.personalStageExp = 0;
		
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	public int getPersonalStageExp() {
		return personalStageExp;
	}

	public void setPersonalStageExp(int personalStageExp){
		this.personalStageExp = personalStageExp;
	}
	
	public void addPersonalStageExp(int personalStageExp) {
		this.personalStageExp += personalStageExp;
	}

	public ExpeditionArmoryStage.Builder genExpeditionArmoryStageBuilder(){
		ExpeditionArmoryStage.Builder builder = ExpeditionArmoryStage.newBuilder();
		builder.setStage(stage);
		ExpeditionArmoryStageCfg cfg = ConfigManager.getInstance().getConfigByKey(ExpeditionArmoryStageCfg.class, stage);
		builder.setNeedExp(cfg.getNeedExp());
		ExpeditionArmoryEntity expeditionArmoryEntity = ExpeditionArmoryManager.getInstance().getCurrentActiveExpeditionArmory();
		int curStage = expeditionArmoryEntity.getCurDonateStage();
		if(curStage > stage){
			builder.setCurExp(cfg.getNeedExp());
		} else if (curStage == stage){
			builder.setCurExp(expeditionArmoryEntity.getStageExp());
		} else {
			builder.setCurExp(0);
		}
		
		builder.setPersonalStageExp(personalStageExp);
		return builder;
	}
}
