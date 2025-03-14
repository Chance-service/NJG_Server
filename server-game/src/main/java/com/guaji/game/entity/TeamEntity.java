package com.guaji.game.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.util.GsonUtil;

/**
 * 团战队伍实体对象
 */
@Entity
@Table(name = "team")
@SuppressWarnings("serial")
public class TeamEntity extends DBEntity implements Comparable<TeamEntity> {
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id;

	// 队长playerId
	@Column(name = "captainId", nullable = false)
	private int captainId;

	// 成员playerId Json字符串
	@Column(name = "teamMemberStr", nullable = false)
	private String teamMemberStr = "";

	// 团战期号
	@Column(name = "stageId", nullable = false)
	private int stageId;

	// 团队总战斗力
	@Column(name = "totalFight", nullable = false)
	private int totalFight = 0;

	// 团队战斗轮数
	@Column(name = "round", nullable = false)
	private int round = 0;
	
	// 是否已经被淘汰
	@Column(name = "isWeedOut", nullable = false)
	private int isWeedOut = 0;
	
	// 队长踢人次数
	@Column(name = "kickTimes", nullable = false)
	private int kickTimes = 0;

	@Column(name = "createTime", nullable = false)
	protected Date createTime;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	@Transient
	private List<Integer> teamMembers;
	
	// 踢过的玩家历史记录
	@Transient
	private Set<Integer> kickPlayerIds;

	public TeamEntity() {
		teamMembers = new ArrayList<Integer>();
		kickPlayerIds = new HashSet<Integer>();
		createTime = GuaJiTime.getCalendar().getTime();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCaptainId() {
		return captainId;
	}

	public void setCaptainId(int captainId) {
		this.captainId = captainId;
	}

	public String getTeamMemberStr() {
		teamMemberStr = GsonUtil.getJsonInstance().toJson(teamMembers);
		return teamMemberStr;
	}

	public void setTeamMemberStr(String teamMemberStr) {
		this.teamMemberStr = teamMemberStr;
	}

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public int getTotalFight() {
		return totalFight;
	}

	public void setTotalFight(int totalFight) {
		this.totalFight = totalFight;
	}
	
	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public int getIsWeedOut() {
		return isWeedOut;
	}

	public void setIsWeedOut(int isWeedOut) {
		this.isWeedOut = isWeedOut;
	}
	
	public int getKickTimes() {
		return kickTimes;
	}

	public void setKickTimes(int kickTimes) {
		this.kickTimes = kickTimes;
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
	
	public Set<Integer> getKickPlayerIds() {
		return kickPlayerIds;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	public synchronized List<Integer> getTeamMembers() {
		if (teamMemberStr != null && !"".equals(teamMemberStr) && teamMembers.size() == 0) {
			this.teamMembers = GsonUtil.getJsonInstance().fromJson(teamMemberStr, new TypeToken<ArrayList<Integer>>(){}.getType());
		}
		return teamMembers;
	}

	public synchronized void removePlayer(int playerId) {
		if (teamMembers.contains(playerId)) {
			teamMembers.remove((Integer) playerId);
		}
		teamMemberStr = GsonUtil.getJsonInstance().toJson(teamMembers);
	}

	public synchronized void addPlayer(int playerId) {
		if (!teamMembers.contains(playerId)) {
			teamMembers.add(playerId);
		}
		teamMemberStr = GsonUtil.getJsonInstance().toJson(teamMembers);
	}

	public boolean isFull() {
		return teamMembers.size() >= SysBasicCfg.getInstance().getTeamBattleMaxPlayerQty();
	}
	
	/**
	 * 踢人
	 * @param memberPlayerId
	 * @return
	 */
	public boolean kickMember(int memberPlayerId){
		removePlayer((Integer) memberPlayerId);
		kickTimes += 1;
		kickPlayerIds.add(memberPlayerId);
		notifyUpdate(true);
		return true;
	}
	

	@Override
	public void notifyUpdate(boolean async) {
		this.teamMemberStr = GsonUtil.getJsonInstance().toJson(teamMembers);
		super.notifyUpdate(async);
	}

	/**
	 * 比对团队人数, 队员人数少的优先分配
	 * @param o
	 * @return
	 */
	@Override
	public int compareTo(TeamEntity o) {
		if(this.id == o.getId()){
			return 0;
		}
		
		if (this.teamMembers.size() != o.getTeamMembers().size()){
			return this.teamMembers.size() - o.getTeamMembers().size();
		}
		
		if (this.getId() > o.getId()) {
			return 1;
		}
		
		return -1;
	}
}
