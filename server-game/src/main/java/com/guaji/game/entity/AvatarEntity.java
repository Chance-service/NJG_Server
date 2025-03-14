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

import com.guaji.game.protocol.RoleOpr.AvatarInfo;

@SuppressWarnings("serial")
@Entity
@Table(name = "avatar")
public class AvatarEntity extends DBEntity {

	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id;

	// 玩家ID
	@Column(name = "playerId")
	private int playerId;

	//Avatar ID
	@Column(name = "avatarId")
	private int avatarId;
		
	//是否查看
	private boolean checked = false;

	//结束时间
	private long endTime = -1;
		
	@Column(name = "createTime")
	private Date createTime;

	@Column(name = "updateTime")
	private Date updateTime;

	@Column(name = "invalid")
	private boolean invalid;

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

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public int getAvatarId() {
		return avatarId;
	}

	public void setAvatarId(int avatarId) {
		this.avatarId = avatarId;
	}
	
	/**
	 * 是否过期
	 * */
	public boolean isOverdue(){
		if(endTime == -1){
			return false;
		}else{
			if(this.endTime <= GuaJiTime.getMillisecond()){
				return true;
			}else{
				return false;
			}	
		}
	}
	
	public AvatarInfo.Builder toBuilder(){
		AvatarInfo.Builder b = AvatarInfo.newBuilder();
		b.setId(id);
		b.setAvatarId(avatarId);
		b.setChecked(checked);
		if(endTime == -1){
			b.setEndTime(-1);
		}else{
			long remaindTime = (endTime - GuaJiTime.getCalendar().getTimeInMillis()) / 1000;
			b.setEndTime(remaindTime > 0 ? remaindTime : 0);	
		}
		return b;
	}
}
