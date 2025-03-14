package com.guaji.game.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.mina.util.ConcurrentHashSet;
import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsonUtil;

@Entity
@Table(name = "team_battle_cache")
@SuppressWarnings("serial")
public class TeamBattleCacheEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id;
	
	@Column(name = "battleState", nullable = false)
	private int battleState = 0;
	
	@Column(name = "waitPlayerIdStr", nullable = false)
	private String waitPlayerIdStr = "";
	
	@Column(name = "nextRoundTeamsAgainstPlanStr", nullable = false)
	private String nextRoundTeamsAgainstPlanStr = "";
	
	@Column(name = "stageId", nullable = false)
	private int stageId = 0;
	
	@Column(name = "lastSaveTime", nullable = false)
	private int lastSaveTime = 0;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;
	
	// 等待队伍的玩家队列
	@Transient
	private ConcurrentHashSet<Integer> waitPlayerIds;
	
	// 下轮团队对阵表 
	@Transient
	private HashMap<Integer, Integer> nextRoundTeamsAgainstPlan;
	
	/*********************************************************/
	
	public TeamBattleCacheEntity(){
		this.battleState = GsConst.TeamBattle.STATE_PREPARE;
		waitPlayerIds =  new ConcurrentHashSet<Integer>();
		nextRoundTeamsAgainstPlan = new HashMap<Integer, Integer>();
		createTime = GuaJiTime.getCalendar().getTime();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBattleState() {
		return battleState;
	}

	public void setBattleState(int battleState) {
		this.battleState = battleState;
	}

	public String getWaitPlayerIdStr() {
		waitPlayerIdStr = GsonUtil.getJsonInstance().toJson(waitPlayerIds);
		return waitPlayerIdStr;
	}

	public void setWaitPlayerIdStr(String waitPlayerIdStr) {
		this.waitPlayerIdStr = waitPlayerIdStr;
	}
	
	public String getNextRoundTeamsAgainstPlanStr() {
		waitPlayerIdStr = GsonUtil.getJsonInstance().toJson(nextRoundTeamsAgainstPlan);
		return nextRoundTeamsAgainstPlanStr;
	}

	public void setNextRoundTeamsAgainstPlanStr(String teamsAgainstPlanStr) {
		this.nextRoundTeamsAgainstPlanStr = teamsAgainstPlanStr;
	}

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public int getLastSaveTime() {
		return lastSaveTime;
	}

	public void setLastSaveTime(int lastSaveTime) {
		this.lastSaveTime = lastSaveTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}
	
	/*********************************************************/
	
	public ConcurrentHashSet<Integer> getWaitPlayerIds() {
		if (waitPlayerIdStr != null && !"".equals(waitPlayerIdStr) && waitPlayerIds.size() == 0) {
			HashSet<Integer> tmpWaitPlayerIds = GsonUtil.getJsonInstance().fromJson(waitPlayerIdStr, new TypeToken<HashSet<Integer>>() {}.getType());
			this.waitPlayerIds = new ConcurrentHashSet<Integer>(tmpWaitPlayerIds);
		}
		return waitPlayerIds;
	}

	public void setWaitPlayerIds(ConcurrentHashSet<Integer> waitPlayerIds) {
		this.waitPlayerIdStr = GsonUtil.getJsonInstance().toJson(waitPlayerIds);
		this.waitPlayerIds = waitPlayerIds;
	}
	
	public HashMap<Integer, Integer> getNextRoundTeamsAgainstPlan() {
		if (nextRoundTeamsAgainstPlanStr != null && !"".equals(nextRoundTeamsAgainstPlanStr) && nextRoundTeamsAgainstPlan.size() == 0) {
			this.nextRoundTeamsAgainstPlan = GsonUtil.getJsonInstance().fromJson(nextRoundTeamsAgainstPlanStr, new TypeToken<HashMap<Integer, Integer>>() {}.getType());
		}
		return nextRoundTeamsAgainstPlan;
	}

	public void setNextRoundTeamsAgainstPlan(HashMap<Integer, Integer> teamsAgainstPlan) {
		this.nextRoundTeamsAgainstPlanStr = GsonUtil.getJsonInstance().toJson(teamsAgainstPlan);
		this.nextRoundTeamsAgainstPlan = teamsAgainstPlan;
	}
}
