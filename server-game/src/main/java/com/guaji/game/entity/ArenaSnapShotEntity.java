package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.util.BuilderUtil;

/**
 * 竞技场战报
 */

@Entity
@Table(name = "arena_snapshot")
@SuppressWarnings("serial")
public class ArenaSnapShotEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "AUTO_INCREAMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREAMENT")
	@Column(name = "id", unique = true)
	private int id;

	/**
	 * 系统类型
	 */
	@Column(name = "systype")
	private int systype = 0;
	/**
	 * 生成日期
	 */
	@Column(name = "builddate")
	private int builddate = 0;

	/**
	 * 竞技场排名
	 */
	@Column(name = "rank")
	private int rank = 0;

	/**
	 * 当前排名对应角色信息（1是 玩家 2 怪）
	 */
	@Column(name = "type")
	private int type = 0;

	/**
	 * type 为2 时对应的怪的编号
	 */
	@Column(name = "playerId")
	private int playerId = 0;

	/**
	 * type 为1 时玩家快照信息
	 */
	@Column(name = "snapshot")
	private byte[] snapshot = null;

	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	public ArenaSnapShotEntity() {
		createTime = GuaJiTime.getCalendar().getTime();
	}

	public ArenaSnapShotEntity(int playerId, int rank, int systype,int type,PlayerSnapshotInfo.Builder snapshotInfo) {
		this.playerId = playerId;
		this.rank = rank;
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.systype = systype;
		this.type=type;
	    //当前时间
	    this.builddate= Integer.parseInt( GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date()));
		if (snapshotInfo != null) {
			this.snapshot = snapshotInfo.build().toByteArray();
		}
	}
	
	public ArenaSnapShotEntity(int playerId, int rank, int systype,int type,Date buildDate,PlayerSnapshotInfo.Builder snapshotInfo) {
		this.playerId = playerId;
		this.rank = rank;
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.systype = systype;
		this.type=type;
	    //当前时间
	    this.builddate= Integer.parseInt( GuaJiTime.DATE_FORMATOR_DAYNUM(buildDate));
		if (snapshotInfo != null) {
			this.snapshot = snapshotInfo.build().toByteArray();
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSystype() {
		return systype;
	}

	public void setSystype(int systype) {
		this.systype = systype;
	}

	public int getBuilddate() {
		return builddate;
	}

	public void setBuilddate(int builddate) {
		this.builddate = builddate;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public byte[] getSnapshot() {
		return snapshot;
	}

	public void setSnapshot(byte[] snapshot) {
		this.snapshot = snapshot;
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

	public PlayerSnapshotInfo.Builder getSnapshotInfo() {
		return convertSnapshot();
	}

	public void setSnapshotInfo(PlayerSnapshotInfo.Builder snapshotInfo) {
		if (snapshotInfo != null) {
			this.snapshot = snapshotInfo.build().toByteArray();
		}
	}

	public PlayerSnapshotInfo.Builder convertSnapshot() {
		return BuilderUtil.convertSnapshot(snapshot);
	}

	@Override
	public void notifyUpdate(boolean async) {
		// this.battleResult = battleResultBuilder.build().toByteArray();
		super.notifyUpdate(async);
	}
}
