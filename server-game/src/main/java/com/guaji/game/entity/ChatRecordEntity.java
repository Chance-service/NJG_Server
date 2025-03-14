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

@Entity
@Table(name = "chat_record")
@SuppressWarnings("serial")
public class ChatRecordEntity extends DBEntity{
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id ;
	
	@Column(name = "playerId")
	private int playerId ;
	
	@Column(name = "chatMsg")
	private String chatMsg;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime ;
	
	@Column(name = "updateTime")
	protected Date updateTime;
	
	@Column(name = "invalid")
	protected boolean invalid;

	public ChatRecordEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
	}
	
	public void convertData(){
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

	public int getPlayerId() {
		return playerId;
	}
	
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	
	public String getChatMsg() {
		return chatMsg;
	}
	
	public void setChatMsg(String chatMsg) {
		this.chatMsg = chatMsg;
	}
}
