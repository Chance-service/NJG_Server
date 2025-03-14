package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.guaji.config.ConfigManager;
import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.guaji.game.config.ExpeditionArmoryStageCfg;
import com.guaji.game.config.SysBasicCfg;


/**
 * 远征物资
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "expedition_armory")
public class ExpeditionArmoryEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id = 0;
	
	/**
	 * 活动期数
	 */
	private int stageId = 0;
	
	/**
	 * 当前捐献阶段
	 */
	private int curDonateStage = 1;
	
	/**
	 * 当前捐献阶段经验
	 */
	private int stageExp = 0;
	
	/**
	 * 是否发放最后一阶段奖励
	 */
	@Column(name = "isGrantLastStage", nullable = false)
	private boolean isGrantLastStage;
	
	/**
	 * 下次系统自动增加阶段经验的时间
	 */
	@Column(name = "nextSysAutoAddStageExpTime", nullable = false)
	private int nextSysAutoAddStageExpTime;
	
	/**
	 * 是否发放排名奖励 
	 */
	@Column(name = "isGrantRank", nullable = false)
	private boolean isGrantRank;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;
	
	public ExpeditionArmoryEntity(){
		curDonateStage = 1;
		// 从配置的阶段开始系统每隔一段时间自动增加经验
		if(this.curDonateStage == SysBasicCfg.getInstance().getExpeditionArmoryAutoAddExpStage()){
			int nextTime = SysBasicCfg.getInstance().getExpeditionArmoryAutoAddExpTime() + GuaJiTime.getSeconds();
			this.setNextSysAutoAddStageExpTime(nextTime);
		}
		stageExp = 0;
		createTime = GuaJiTime.getCalendar().getTime();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getStageId() {
		return stageId;
	}

	public void setStageId(int activityStageId) {
		this.stageId = activityStageId;
	}

	public int getCurDonateStage() {
		return curDonateStage;
	}

	public void setCurDonateStage(int curStage) {
		this.curDonateStage = curStage;
	}

	public int getStageExp() {
		return stageExp;
	}

	public void setStageExp(int exp) {
		this.stageExp = exp;
	}
	
	public boolean isGrantLastStage() {
		return isGrantLastStage;
	}

	public void setGrantLastStage(boolean isGrantLastStage) {
		this.isGrantLastStage = isGrantLastStage;
		notifyUpdate(true);
	}

	public boolean isGrantRank() {
		return isGrantRank;
	}

	public void setGrantRank(boolean isGrantRank) {
		this.isGrantRank = isGrantRank;
		notifyUpdate(true);
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}
	
	public int getNextSysAutoAddStageExpTime() {
		return nextSysAutoAddStageExpTime;
	}
	

	public void setNextSysAutoAddStageExpTime(int nextSysAutoAddStageExpTime) {
		this.nextSysAutoAddStageExpTime = nextSysAutoAddStageExpTime;
	}
	

	/**
	 * 增加活动贡献，贡献达到时自动跳到下一阶段
	 * @param addExp
	 */
	public void increaseExp(int addExp) {
		this.stageExp += addExp;
		ExpeditionArmoryStageCfg stageCfg = ConfigManager.getInstance().getConfigByKey(ExpeditionArmoryStageCfg.class, curDonateStage);
		if(stageCfg.hasNext() && this.stageExp >= stageCfg.getNeedExp()){
			this.stageExp = 0;
			this.curDonateStage = stageCfg.getNextStage();
		}
		notifyUpdate(true);
	}
	
	/**
	 * 获得服务器活动总积分
	 */
	public int getTotalStageExp()
	{
		int totalExp = 0;
		
		for( int i = 0; i<curDonateStage; i++ )
		{
			ExpeditionArmoryStageCfg stageCfg = ConfigManager.getInstance().getConfigByKey(ExpeditionArmoryStageCfg.class, i);
			totalExp += stageCfg.getNeedExp();
		}
		
		totalExp += this.stageExp;
		
		return totalExp;
	}

	/**
	 * 计算升级到下个阶段还需要多少贡献
	 * @return
	 */
	public int calcLeftExp() {
		ExpeditionArmoryStageCfg stageCfg = ConfigManager.getInstance().getConfigByKey(ExpeditionArmoryStageCfg.class, curDonateStage);
		if(stageCfg.getNeedExp() > 0 && stageCfg.hasNext()){
			return stageCfg.getNeedExp() - this.stageExp;
		}
		return Integer.MAX_VALUE;
	}
}
