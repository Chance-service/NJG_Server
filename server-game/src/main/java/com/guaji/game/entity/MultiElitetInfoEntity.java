package com.guaji.game.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.util.GsonUtil;

@Entity
@Table(name = "multi_elite_info")
@SuppressWarnings("serial")
public class MultiElitetInfoEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "AUTO_INCREAMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREAMENT")
	@Column(name = "id", unique = true)
	private int id;

	/**
	 * 多人副本列表ID
	 */
	@Column(name = "multiMapStr")
	protected String multiMapStr = null;

	@Transient
	private List<Integer> multiMapList = new ArrayList<>();

	/**
	 * 当前时间副本福将ID
	 */
	@Column(name = "multiLuckRoleStr")
	protected String multiLuckRoleStr = null;

	/**
	 * 下次刷新时间
	 */
	protected long nextRefreshTime = 0;

	@Transient
	private Map<Integer, String> multiLuckRoleMap = new HashMap<Integer, String>();

	@Column(name = "createTime")
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	public MultiElitetInfoEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public Date getCreateTime() {
		return this.createTime;
	}

	@Override
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;

	}

	@Override
	public Date getUpdateTime() {
		return this.updateTime;
	}

	@Override
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;

	}

	@Override
	public boolean isInvalid() {
		return this.invalid;
	}

	@Override
	public void setInvalid(boolean invalid) {
		this.invalid = invalid;

	}

	/**
	 * 字符串存储转换为内存数据
	 */
	public void convertToList() {
		if (this.multiMapStr != null) {
			multiMapList = GsonUtil.getJsonInstance().fromJson(this.multiMapStr, new TypeToken<List<Integer>>() {
			}.getType());
		}

		if (this.multiLuckRoleStr != null) {
			multiLuckRoleMap = GsonUtil.getJsonInstance().fromJson(this.multiLuckRoleStr, new TypeToken<Map<Integer, String>>() {
			}.getType());
		}
	}

	/**
	 * 将内存数据转化为json字符串
	 */
	public void reConvert() {
		this.multiMapStr = GsonUtil.getJsonInstance().toJson(multiMapList);
		this.multiLuckRoleStr = GsonUtil.getJsonInstance().toJson(multiLuckRoleMap);
	}

	public long getNextRefreshTime() {
		return nextRefreshTime;
	}

	public void setNextRefreshTime(long nextRefreshTime) {
		this.nextRefreshTime = nextRefreshTime;
	}

	public String getMultiMapStr() {
		return multiMapStr;
	}

	public void setMultiMapStr(String multiMapStr) {
		this.multiMapStr = multiMapStr;
	}

	public List<Integer> getMultiMapList() {
		return multiMapList;
	}

	public void setMultiMapList(List<Integer> multiMapList) {
		this.multiMapList = multiMapList;
	}

	public String getMultiLuckRoleStr() {
		return multiLuckRoleStr;
	}

	public void setMultiLuckRoleStr(String multiLuckRoleStr) {
		this.multiLuckRoleStr = multiLuckRoleStr;
	}

	public Map<Integer, String> getMultiLuckRoleMap() {
		return multiLuckRoleMap;
	}

	public void setMultiLuckRoleMap(Map<Integer, String> multiLuckRoleMap) {
		this.multiLuckRoleMap = multiLuckRoleMap;
	}

	@Override
	public void notifyUpdate() {
		reConvert();
		super.notifyUpdate();
	}

}
