package com.guaji.game.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.manager.ChatMsg;
import com.guaji.game.util.GsonUtil;

@SuppressWarnings("serial")
@Entity
@Table(name = "chat_msg")
public class ChatMsgEntity extends DBEntity
{
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id;
	
	@Column(name = "createTime")
	private Date  createTime ;
	
	@Column(name = "updateTime")
	private Date  updateTime ;
	
	@Column(name = "invalid")
	private boolean invalid ;
	
	@Column(name = "typeId")
	private int typeId;
	
	@Column(name = "pos")
	private int index;
	
	@Column(name = "msg")
	private String msgStr;
	
	public ChatMsgEntity()
	{
		this.createTime = GuaJiTime.getCalendar().getTime();

	}
	
	public static ChatMsgEntity valueOf(int typeId,int index)
	{
		ChatMsgEntity chatMsgEntity = new ChatMsgEntity();
		chatMsgEntity.typeId = typeId;
		chatMsgEntity.index = index;
		chatMsgEntity.msgStr= "";
		
		return chatMsgEntity;
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
	/**
	 * 设置缓存频道
	 * @return
	 */
	public int getTypeId()
	{
		return typeId;
	}
	
	public void setTypeId(int id)
	{
		this.typeId = id;
	}
	
	public int getIndex()
	{
		return this.index;
	}
	
	/**
	 * 设置缓存位置
	 * @param index
	 */
	public void setIndex(int index)
	{
		this.index = index;
		return;
	}
	
	public void setMsgStr(String msg)
	{
		this.msgStr = msg;
		return;
	}
	
	public String getMsgStr()
	{
		return this.msgStr;
	}
	
	/**
	 * 字符串存储转换为内存数据
	 */
	public List<ChatMsg> convert() {
		if(this.msgStr != null) {
			return GsonUtil.getJsonInstance().fromJson(this.msgStr,new TypeToken<List<ChatMsg>>() {}.getType());
		}
		return null;
	}
	
	/**
	 * 字符串存储转换为内存数据
	 */
	public void convert(List<ChatMsg> list) {
		if(this.msgStr != null) {
			list = GsonUtil.getJsonInstance().fromJson(this.msgStr,new TypeToken<List<ChatMsg>>() {}.getType());
		}
		return;
	}
	
	/**
	 * 将内存数据转化为json字符串
	 */
	public void reConvert(List<ChatMsg> list)
	{
		this.msgStr = GsonUtil.getJsonInstance().toJson(list);
		return;
	}

}
