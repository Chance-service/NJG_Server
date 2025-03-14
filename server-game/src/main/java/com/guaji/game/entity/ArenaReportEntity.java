package com.guaji.game.entity;

import java.util.Date;

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
import com.guaji.game.protocol.Arena.HPArenaChallengeReportRes;

/**
 * 竞技场战报
 */

@Entity
@Table(name = "arena_report")
@SuppressWarnings("serial")
public class ArenaReportEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "AUTO_INCREAMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREAMENT")
	@Column(name = "id", unique = true)
	private int id;

	@Column(name = "battleResult")
	private byte[] battleResult = null;

	@Transient
	private HPArenaChallengeReportRes.Builder battleResultBuilder;

	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	public ArenaReportEntity() {
		createTime = GuaJiTime.getCalendar().getTime();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public HPArenaChallengeReportRes.Builder convertResultToBuilder() {
		try {
			if (battleResult != null) {
				this.battleResultBuilder = HPArenaChallengeReportRes.parseFrom(battleResult).toBuilder();
			}
		} catch (InvalidProtocolBufferException e) {
			MyException.catchException(e);
		}
		return this.battleResultBuilder;
	}

	public HPArenaChallengeReportRes.Builder getBattleResultBuilder() {
		return battleResultBuilder;
	}

	public void setBattleResultBuilder(HPArenaChallengeReportRes.Builder battleResultBuilder) {
		this.battleResultBuilder = battleResultBuilder;
		this.battleResult = battleResultBuilder.build().toByteArray();
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

	@Override
	public void notifyUpdate(boolean async) {
		this.battleResult = battleResultBuilder.build().toByteArray();
		super.notifyUpdate(async);
	}
}
