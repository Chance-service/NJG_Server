package com.guaji.game.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.util.GsonUtil;

/**
 * 每日数据统计
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "daily_statistics")
public class DailyStatisticsEntity extends DBEntity {
	@Id
	@Column(name = "date", nullable = false)
	public String date;
	
	@Column(name = "totalUsers")
	public int totalUsers;
	
	@Column(name = "totalDevice")
	public int totalDevice;
	
	@Column(name = "totalPayUsers")
	public int totalPayUsers;
	
	@Column(name = "totalPayDevice")
	public int totalPayDevice;
	
	@Column(name = "totalPayMoney")
	public int totalPayMoney;
	
	@Column(name = "newUsers")
	public int newUsers;
	
	@Column(name = "newDevice")
	public int newDevice;
	
	@Column(name = "dailyActiveUsers")
	public int dailyActiveUsers;
	
	@Column(name = "userRetentionRate")
	public float userRetentionRate;
	
	@Column(name = "deviceRetentionRate")
	public float deviceRetentionRate;
	
	@Column(name = "payUsers")
	public int payUsers;
	
	@Column(name = "payDevice")
	public int payDevice;
	
	@Column(name = "payMoney")
	public int payMoney;
	/**
	 * 已創角命名玩家人數
	 */
	@Column(name = "nameDone")
	public int nameDone;
	/**
	 * 完成新手教學人數	
	 */
	@Column(name = "teachDone")
	public int teachDone;
	/**
	 * 消費禮包人數
	 */
	@Column(name = "costItem")
	public int costItem;
	/**
	 * 看完開頭動畫的人
	 */
	@Column(name = "ftuxDone")
	public int ftuxDone;
	/**
	 * 看完HCG的人
	 */
	@Column(name = "hcgDone")
	public int hcgDone;
	
	/**關卡人數統計*/
	@Column(name = "passMapStr",columnDefinition = "varchar(2048) not null")
	private String passMapStr;
	
	/**關卡人數統計系統 */
	@Transient
	private Map<Integer, Integer> passMap;
	

	@Column(name = "createTime", nullable = false)
	protected Date createTime;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;
	
	public DailyStatisticsEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.passMap = new HashMap<Integer, Integer>();
		converStr();
	}
	/**
	 * 轉換資料成儲存型態
	 *
	 */
	public void converStr() {
		this.passMapStr = GsonUtil.getJsonInstance().toJson(passMap, new TypeToken<HashMap<Integer, Integer>>() {
		}.getType());;
	}
	/**
	 * (字串)儲存型態轉成物件資料型態
	 */
	public void convertData() {
		if (!passMapStr.isEmpty()) {
			this.passMap =  GsonUtil.getJsonInstance().fromJson(this.passMapStr,new TypeToken<HashMap<Integer, Integer>>() {}.getType());
		} else {
			this.passMap = new HashMap<Integer, Integer>();
			converStr();
		}
	}
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getTotalUsers() {
		return totalUsers;
	}

	public void setTotalUsers(int totalUsers) {
		this.totalUsers = totalUsers;
	}

	public int getTotalDevice() {
		return totalDevice;
	}

	public void setTotalDevice(int totalDevice) {
		this.totalDevice = totalDevice;
	}

	public int getTotalPayUsers() {
		return totalPayUsers;
	}

	public void setTotalPayUsers(int totalPayUsers) {
		this.totalPayUsers = totalPayUsers;
	}

	public int getTotalPayDevice() {
		return totalPayDevice;
	}

	public void setTotalPayDevice(int totalPayDevice) {
		this.totalPayDevice = totalPayDevice;
	}

	public int getTotalPayMoney() {
		return totalPayMoney;
	}

	public void setTotalPayMoney(int totalPayMoney) {
		this.totalPayMoney = totalPayMoney;
	}

	public int getNewUsers() {
		return newUsers;
	}

	public void setNewUsers(int newUsers) {
		this.newUsers = newUsers;
	}

	public int getNewDevice() {
		return newDevice;
	}

	public void setNewDevice(int newDevice) {
		this.newDevice = newDevice;
	}

	public int getDailyActiveUsers() {
		return dailyActiveUsers;
	}

	public void setDailyActiveUsers(int dailyActiveUsers) {
		this.dailyActiveUsers = dailyActiveUsers;
	}

	public int getPayUsers() {
		return payUsers;
	}

	public void setPayUsers(int payUsers) {
		this.payUsers = payUsers;
	}

	public int getPayDevice() {
		return payDevice;
	}

	public void setPayDevice(int payDevice) {
		this.payDevice = payDevice;
	}

	public int getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(int payMoney) {
		this.payMoney = payMoney;
	}

	public float getUserRetentionRate() {
		return userRetentionRate;
	}

	public void setUserRetentionRate(float userRetentionRate) {
		this.userRetentionRate = userRetentionRate;
	}

	public float getDeviceRetentionRate() {
		return deviceRetentionRate;
	}

	public void setDeviceRetentionRate(float deviceRetentionRate) {
		this.deviceRetentionRate = deviceRetentionRate;
	}
	
	public int getTeachDone() {
		return teachDone;
	}

	public void setTeachDone(int teachDone) {
		this.teachDone = teachDone;
	}
	
	public int getFtuxDone() {
		return ftuxDone;
	}
	
	public void setFtuxDone(int ftuxDone) {
		this.ftuxDone = ftuxDone;
	}
	
	public int getHcgDone() {
		return hcgDone;
	}

	public void setHcgDone(int hcgDone) {
		this.hcgDone = hcgDone;
	}
	
	public int getNameDone() {
		return nameDone;
	}
	public void setNameDone(int nameDone) {
		this.nameDone = nameDone;
	}
	
	public int getCostItem() {
		return costItem;
	}
	public void setCostItem(int costItem) {
		this.costItem = costItem;
	}
	public Map<Integer, Integer> getPassMap() {
		return passMap;
	}

	public void setPassMap(Map<Integer, Integer> passMap) {
		this.passMap = passMap;
		converStr();
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
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
}
