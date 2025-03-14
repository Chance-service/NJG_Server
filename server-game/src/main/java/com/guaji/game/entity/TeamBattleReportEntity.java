package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.os.MyException;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.google.protobuf.InvalidProtocolBufferException;
import com.guaji.game.protocol.Mail.TeamRoundInfo;

@Entity
@Table(name = "team_battle_report")
@SuppressWarnings("serial")
public class TeamBattleReportEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id = 0;
	
	@Column(name = "stageId", nullable = false)
	private int stageId = 0;
	
	@Column(name = "leftTeamId", nullable = false)
	private int leftTeamId = 0;
	
	@Column(name = "rightTeamId", nullable = false)
	private int rightTeamId = 0;
	
	@Column(name = "round", nullable = false)
	private int round = 0;
	
	@Column(name = "teamRoundInfo", nullable = false)
	private byte[] teamRoundInfo = null;
	
	@Column(name = "result", nullable = false)
	private int result = 0;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = GuaJiTime.getCalendar().getTime();
	
	@Column(name = "updateTime")
	protected Date updateTime;
	
	@Column(name = "invalid")
	protected boolean invalid;
	
	@Transient
	private TeamRoundInfo.Builder teamRoundInfoBuilder;

	public TeamBattleReportEntity(){
		
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

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public int getLeftTeamId() {
		return leftTeamId;
	}

	public void setLeftTeamId(int leftTeamId) {
		this.leftTeamId = leftTeamId;
	}

	public int getRightTeamId() {
		return rightTeamId;
	}

	public void setRightTeamId(int rightTeamId) {
		this.rightTeamId = rightTeamId;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
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
	
	public byte[] getTeamRoundInfo() {
		return teamRoundInfo;
	}

	public void setTeamRoundInfo(byte[] teamRoundInfo) {
		this.teamRoundInfo = teamRoundInfo;
	}

	public TeamRoundInfo.Builder getTeamRoundInfoBuilder() {
		return teamRoundInfoBuilder;
	}

	public void setTeamRoundInfoBuilder(TeamRoundInfo.Builder teamRoundInfoBuilder) {
		this.teamRoundInfoBuilder = teamRoundInfoBuilder;
		this.teamRoundInfo = teamRoundInfoBuilder.build().toByteArray();
	}

	/**
	 * 将字节流反序列化为protoBuff对象
	 */
	public TeamRoundInfo.Builder convertTeamRoundInfo() {
		try {
			if(teamRoundInfo != null){
				this.teamRoundInfoBuilder = TeamRoundInfo.parseFrom(teamRoundInfo).toBuilder();
			}
		} catch (InvalidProtocolBufferException e) {
			MyException.catchException(e);
		}
		return this.teamRoundInfoBuilder;
	}
	
	@Override
	public void notifyUpdate(boolean async) {
		this.teamRoundInfo = teamRoundInfoBuilder.build().toByteArray();
		super.notifyUpdate(async);
	}
}
