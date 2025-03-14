package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.guaji.db.DBEntity;
import org.hibernate.annotations.GenericGenerator;

/**
 * 公告實體存檔
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "bulletin")
public class BulletinEntity extends DBEntity {
	/**
	 * 索引id
	 */
	@Id
	@GenericGenerator(name = "ASSIGNED", strategy = "assigned")
	@GeneratedValue(generator = "ASSIGNED")
	@Column(name = "id",nullable = false, unique = true)
	private int id;
	/**
	 * 平台ID 0.all 1.H365 2.EROR18 3.JSG 4.LSJ 5.MURA 6.KUSO
	 */
	@Column(name = "platformId",nullable = false)
	private int platformId = 0 ;
	/**
	 * type 0.一般公告 1.活動公告
	 */
	@Column(name = "type", nullable = false)
	private int type = 0;
	/**
	 * 排序
	 */
	@Column(name = "sort", nullable = false)
	private int sort = 0;
	/*
	 * 標題html字串 or 連結圖片字串
	 */
	@Column(name = "title",columnDefinition = "varchar(256) not null")
	private String title ;
	/*
	 * 內文
	 */
	@Column(name = "txturl",columnDefinition = "varchar(256) not null")
	private String txturl ;
	/**
	 *  1.顯示 2.隱藏
	 */
	@Column(name = "visible", nullable = false)
	private int visible = 1;
	/**
	 * 公告有效起始時間
	 */
	@Column(name = "beginTime")
	private Date  beginTime ;
	/**
	 * 公告有效截止時間
	 */
	@Column(name = "endTime")
	private Date  endTime ;
	/**
	 * 修改時間
	 */
	@Column(name = "fixTime")
	private Date  fixTime ;
	/**
	 * 寫入本地DB更新時間
	 */
	@Column(name = "updateTime")
	private Date  updateTime ;
	
	@Column(name = "createTime")
	private Date  createTime ;
	
	@Column(name = "invalid")
	private boolean invalid ;
	
	public BulletinEntity() {
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTxturl() {
		return txturl;
	}

	public void setTxturl(String txturl) {
		this.txturl = txturl;
	}

	public int getVisible() {
		return visible;
	}

	public void setVisible(int visible) {
		this.visible = visible;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	public Date getFixTime() {
		return fixTime;
	}

	public void setFixTime(Date fixTime) {
		this.fixTime = fixTime;
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
