package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;

import org.guaji.db.DBEntity;

import com.guaji.game.protocol.Const.FriendChatMsgType;

/**
 * 好友消息
 * @author xulinqs
 *
 */
@SuppressWarnings("serial")
public class FriendMsgEntity extends DBEntity{
	
	private int id ;
	
	private int senderId ;
	
	private int receiveId ;
	
	private int senderName ;
	
	private int receiveName ;
	
	private int msgType = FriendChatMsgType.PLAYER_MSG_VALUE;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;
	
	@Column(name = "updateTime")
	protected Date updateTime;
	
	@Column(name = "invalid")
	protected boolean invalid;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSenderId() {
		return senderId;
	}

	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}

	public int getReceiveId() {
		return receiveId;
	}

	public void setReceiveId(int receiveId) {
		this.receiveId = receiveId;
	}

	public int getSenderName() {
		return senderName;
	}

	public void setSenderName(int senderName) {
		this.senderName = senderName;
	}

	public int getReceiveName() {
		return receiveName;
	}

	public void setReceiveName(int receiveName) {
		this.receiveName = receiveName;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
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
	
}
