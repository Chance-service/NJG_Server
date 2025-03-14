package com.guaji.game.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.bean.HeroTokenShopBean;
import com.guaji.game.bean.HeroTokenTaskBean;
import com.guaji.game.util.GsonUtil;

/**
 * 英雄令任务实体存储
 * 
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hero_token_task")
public class HeroTokenTaskEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "ASSIGNED", strategy = "assigned")
	@GeneratedValue(generator = "ASSIGNED")
	@Column(name = "playerId", nullable = false, unique = true)
	private int playerId;

	@Column(name = "tasks")
	private String tasks;

	/** 存放当前任务信息 */
	@Transient
	private List<HeroTokenTaskBean> taskList;

	/** 存放taskList中完成任务的索引，不然无法区分 */
	@Column(name = "completeTasks")
	private String completeTasks;

	@Transient
	private Set<Integer> completeTaskSet;
	
	/**
	 * 商店购买数据
	 */
	@Column(name = "shopData", nullable = false)
	private String shopData;
	
	/** 
	 * 商店购买数据转换
	 */
	@Transient
	private List<HeroTokenShopBean> shopList;

	@Column(name = "createTime")
	private Date createTime;

	@Column(name = "updateTime")
	private Date updateTime;

	@Column(name = "invalid")
	private boolean invalid;

	public HeroTokenTaskEntity() {
		this.taskList = new ArrayList<HeroTokenTaskBean>();
		this.completeTaskSet = new HashSet<>();
		this.shopList = new ArrayList<HeroTokenShopBean>();
		this.tasks = GsonUtil.getJsonInstance().toJson(taskList);
		this.completeTasks = GsonUtil.getJsonInstance().toJson(completeTaskSet);
		this.shopData = GsonUtil.getJsonInstance().toJson(shopList);
	}

	public static HeroTokenTaskEntity valueOf(int playerId) {
		HeroTokenTaskEntity entity = new HeroTokenTaskEntity();
		entity.setPlayerId(playerId);
		return entity;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public List<HeroTokenTaskBean> getTaskList() {
		return taskList;
	}

	public void addTask(int taskId) {
		HeroTokenTaskBean bean = new HeroTokenTaskBean();
		bean.setTaskId(taskId);
		bean.setCount(0);
		this.taskList.add(bean);
		this.tasks = GsonUtil.getJsonInstance().toJson(this.taskList);
	}

	public Set<Integer> getCompleteTaskSet() {
		return completeTaskSet;
	}

	public void addCompleteTaskSet(int id) {
		this.completeTaskSet.add(id);
		this.completeTasks = GsonUtil.getJsonInstance().toJson(this.completeTaskSet);
	}

	public void addCount(int id, int count) {
		HeroTokenTaskBean bean = taskList.get(id);
		bean.setCount(bean.getCount() + count);
		this.tasks = GsonUtil.getJsonInstance().toJson(this.taskList);
	}
	
	public List<HeroTokenShopBean> getShopList() {
		return shopList;
	}
	
	public void updataShopData() {
		this.shopData = GsonUtil.getJsonInstance().toJson(shopList);
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

	public int getTaskCount() {
		return taskList.size();
	}

	/**
	 * 数据存储转化成内存操作对象
	 */
	public void convert() {
		if (this.tasks != null && !"".equals(this.tasks)) {
			this.taskList = GsonUtil.getJsonInstance().fromJson(this.tasks,
					new TypeToken<List<HeroTokenTaskBean>>() {
					}.getType());
		}

		if (this.completeTasks != null && !"".equals(this.completeTasks)) {
			this.completeTaskSet = GsonUtil.getJsonInstance().fromJson(
					this.completeTasks, new TypeToken<Set<Integer>>() {
					}.getType());
		}
		
		if (this.shopData != null && !"".equals(this.shopData)) {
			this.shopList = GsonUtil.getJsonInstance().fromJson(this.shopData,
					new TypeToken<List<HeroTokenShopBean>>() {
					}.getType());
		}
	}

	/**
	 * 重置任务
	 */
	public void reset() {
		Iterator<HeroTokenTaskBean> it = taskList.iterator();
		int index = 0;
		while (it.hasNext()) {
			it.next();
			if (completeTaskSet.contains(index)) {
				it.remove();
			}
			index++;
		}
		completeTaskSet.clear();
		shopList.clear();
		
		this.tasks = GsonUtil.getJsonInstance().toJson(this.taskList);
		this.completeTasks = GsonUtil.getJsonInstance().toJson(this.completeTaskSet);
		this.shopData = GsonUtil.getJsonInstance().toJson(this.shopList);
	}
}