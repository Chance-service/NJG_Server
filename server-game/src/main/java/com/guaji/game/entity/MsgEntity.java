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

import com.guaji.game.protocol.Const;

/**
 * 留言实体对象
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "msg")
public class MsgEntity extends DBEntity implements Comparable<MsgEntity>{
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id;
	
	@Column(name = "moduleId", unique = true)
	private int moduleId = 0;
	
	@Column(name = "senderId", nullable = false)
	private int senderId = 0;
	
	@Column(name = "recverId", nullable = false)
	private int recverId = 0;
	
	@Column(name = "content", nullable = false)
	private String content = "";
	
	@Column(name = "senderSkinId", nullable = false)
	private int senderSkinId = 0;
	
	@Column(name = "createSysTime", nullable = false)
	protected int createSysTime = 0;
	
	@Column(name = "lastReadTime", nullable = false)
	protected int lastReadTime = 0;
	
	@Column(name = "msgType", nullable = false)
	private int msgType = Const.FriendChatMsgType.LEAVE_MSG_VALUE;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;
	
	@Column(name = "updateTime")
	protected Date updateTime;
	
	@Column(name = "jsonType")
	protected int jsonType=0;

	@Column(name = "invalid")
	protected boolean invalid;
	
	public MsgEntity(){
		this.createSysTime = GuaJiTime.getSeconds();
		this.lastReadTime = 0;
		this.createTime = GuaJiTime.getCalendar().getTime();
	}
	
	public MsgEntity(int senderId, int recverId, String content, int moduleId){
		this.senderId = senderId;
		this.recverId = recverId;
		this.content = content;
		this.moduleId = moduleId;
		this.createSysTime = GuaJiTime.getSeconds();
		this.lastReadTime = 0;
		this.createTime = GuaJiTime.getCalendar().getTime();
	}

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

	public int getRecverId() {
		return recverId;
	}

	public void setRecverId(int recverId) {
		this.recverId = recverId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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
	
	public int getModuleId() {
		return moduleId;
	}

	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}

	public int getCreateSysTime() {
		return createSysTime;
	}

	public void setCreateSysTime(int createSysTime) {
		this.createSysTime = createSysTime;
	}

	public int getLastReadTime() {
		return lastReadTime;
	}

	public void setLastReadTime(int lastReadTime) {
		this.lastReadTime = lastReadTime;
	}

	@Override
	public int compareTo(MsgEntity o) {
		if(!super.equals(o)){
			return this.createTime.compareTo(o.getCreateTime());
		}
		return 0;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public int getJsonType() {
		return jsonType;
	}

	public void setJsonType(int jsonType) {
		this.jsonType = jsonType;
	}

	public int getSenderSkinId() {
		return senderSkinId;
	}

	public void setSenderSkinId(int senderSkinId) {
		this.senderSkinId = senderSkinId;
	}
}
