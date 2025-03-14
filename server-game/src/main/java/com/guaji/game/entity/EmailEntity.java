package com.guaji.game.entity;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;

/**
 * 邮件实体对象
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "email")
public class EmailEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	protected int id = 0;

	@Column(name = "playerId")
	protected int playerId = 0;

	@Column(name = "type")
	protected int type = 0;

	@Column(name = "title", nullable = false)
	protected String title = null;

	@Column(name = "content")
	protected String content = "";
	
	@Column(name = "mailId")
	private int mailId = 0;
	
	@Column(name = "effectTime", nullable = false)
	private Date effectTime = null;
	
	@Column(name = "params")
	protected String params;
	
	/**
	 *  邮件大分类 1 普通 2 系统
	 */
	@Column(name = "classification")
	private int classification = GsConst.EmailClassification.COMMON;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;
	
	@Column(name = "invalid")
	protected boolean invalid;

	@Transient
	private List<String> paramsList = new LinkedList<>();
	
	public EmailEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.setEffectTime(GuaJiTime.getCalendar().getTime());
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public void setInvalid(Boolean invalid) {
		this.invalid = invalid;
	}
	
	/**
	 * params存储转化为内存List
	 */
	public void convertData() {
		if(params != null) {
			String[] paramsValue = params.split(",");
			this.paramsList.clear();
			this.paramsList.addAll(Arrays.asList(paramsValue));
		}
	}

	public void setParamsValue(String[] params) {
		this.paramsList.clear();
		this.paramsList.addAll(Arrays.asList(params));
		this.params = GameUtil.join(this.paramsList, ",");
	}
	
	public void addParams(String s) {
		this.paramsList.add(s);
		this.params = GameUtil.join(this.paramsList, ",");
	}

	public int getMailId() {
		return mailId;
	}

	public void setMailId(int mailId) {
		this.mailId = mailId;
	}

	public List<String> getParamsList() {
		return paramsList;
	}

	public void setParamsList(List<String> paramsList) {
		this.paramsList = paramsList;
	}

	public Date getEffectTime() {
		return effectTime;
	}

	public void setEffectTime(Date effectTime) {
		this.effectTime = effectTime;
	}

	@Override
	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	public int getClassification() {
		return classification;
	}

	public void setClassification(int classification) {
		this.classification = classification;
	}

	public String getParams() {
		return params;
	}
	
	
}
