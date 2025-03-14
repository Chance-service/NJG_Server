package com.guaji.game.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;
import org.hibernate.annotations.GenericGenerator;

import com.google.protobuf.InvalidProtocolBufferException;
import com.guaji.game.battle.BattleRole;
import com.guaji.game.protocol.Battle.BattleInfo;

@Entity
@Table(name = "alliance_fight_unit")
@SuppressWarnings("serial")
public class AllianceFightUnit extends DBEntity{
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id ;
	
	@Column(name = "stageId")
	private int stageId ;
	
	@Column(name = "versusId")
	private int versusId ;
	
	@Column(name = "leftIndex")
	private int leftIndex ;
	
	@Column(name = "rightIndex")
	private int rightIndex ;
	
	@Column(name = "winIndex")
	private int winIndex ;
	
	private int state ;
	
	@Column(name = "fightReport")
	private byte[] fightReport ;
	
	@Transient
	private BattleInfo fightReportBattle;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime ;
	
	@Column(name = "updateTime")
	protected Date updateTime;
	
	@Column(name = "invalid")
	protected boolean invalid;

	@Transient
	private List<BattleRole> leftBattleRoles;
	
	@Transient
	private List<BattleRole> rightBattleRoles;
	
	public AllianceFightUnit() {
		this.createTime = GuaJiTime.getCalendar().getTime();
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

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getLeftIndex() {
		return leftIndex;
	}

	public void setLeftIndex(int leftIndex) {
		this.leftIndex = leftIndex;
	}

	public int getRightIndex() {
		return rightIndex;
	}

	public void setRightIndex(int rightIndex) {
		this.rightIndex = rightIndex;
	}

	public int getVersusId() {
		return versusId;
	}

	public void setVersusId(int versusId) {
		this.versusId = versusId;
	}

	public byte[] getFightReport() {
		return fightReport;
	}

	public void setFightReport(byte[] fightReport) {
		this.fightReport = fightReport;
	}

	public int getWinIndex() {
		return winIndex;
	}

	public void setWinIndex(int winIndex) {
		this.winIndex = winIndex;
	}

	public void setLeftBattleRoles(List<BattleRole> list) {
		this.leftBattleRoles = list;
	}

	public void setRightBattleRoles(List<BattleRole> list) {
		this.rightBattleRoles = list;
	}

	public List<BattleRole> getLeftBattleRoles() {
		return this.leftBattleRoles;
	}
	
	public List<BattleRole> getRightBattleRoles() {
		return this.rightBattleRoles;
	}
	
	public void convertData() {
		if(this.fightReport != null) {
			try {
				this.setFightReportBattle(BattleInfo.parseFrom(this.fightReport));
			} catch (InvalidProtocolBufferException e) {
				MyException.catchException(e);
			}
		}
	}

	public BattleInfo getFightReportBattle() {
		return fightReportBattle;
	}

	public void setFightReportBattle(BattleInfo fightReportBattle) {
		this.fightReportBattle = fightReportBattle;
		this.fightReport = fightReportBattle.toByteArray();
	}

	public Integer getFailIndex() {
		return this.winIndex == leftIndex ? rightIndex : leftIndex;
	}

}
