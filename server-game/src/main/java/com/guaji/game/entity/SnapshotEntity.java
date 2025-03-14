package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;

import com.guaji.game.util.BuilderUtil;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;

/**
 * 玩家数据快照实体对象
 */
@Entity
@Table(name = "player_snapshot")
@SuppressWarnings("serial")
public class SnapshotEntity extends DBEntity {
	@Id
	@Column(name = "playerId", nullable = false)
	private int playerId;
	
	@Column(name = "snapshot", nullable = false)
	private byte[] snapshot;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;
	
	@Column(name = "updateTime")
	protected Date updateTime;
	
	@Column(name = "invalid")
	protected boolean invalid;
	
//	@Transient
//	private PlayerSnapshotInfo.Builder snapshotInfo;
	
	@Transient
	private int lastRefreshTime;

	public SnapshotEntity(){
		this.lastRefreshTime = 0;
		this.createTime = GuaJiTime.getCalendar().getTime();
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
	
	public PlayerSnapshotInfo.Builder convertSnapshot() {
		return BuilderUtil.convertSnapshot(snapshot);
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
//			this.snapshotInfo = snapshotInfo;
			setLastRefreshTime(lastRefreshTime);
			this.snapshot = snapshotInfo.build().toByteArray();
		}
	}
	
	public int getLastRefreshTime() {
		return lastRefreshTime;
	}

	public void setLastRefreshTime(int lastRefreshTime) {
		this.lastRefreshTime = lastRefreshTime;
	}

	@Override
	public void notifyUpdate(boolean async) {
//		this.snapshot = snapshotInfo.build().toByteArray();
		super.notifyUpdate(async);
	}
}
