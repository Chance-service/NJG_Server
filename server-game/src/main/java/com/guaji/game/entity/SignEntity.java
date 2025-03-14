package com.guaji.game.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import com.guaji.game.player.Player;
import com.guaji.game.util.GsonUtil;

@SuppressWarnings("serial")
@Entity
@Table(name = "player_sign")
public class SignEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "ASSIGNED", strategy = "assigned")
	@GeneratedValue(generator = "ASSIGNED")
	@Column(name = "playerId", nullable = false, unique = true)
	private int playerId;

	@Column(name = "signStr",columnDefinition = "TEXT")
	private String signStr;

	@Transient
	private Set<Integer> SignSet;
	
	@Column(name = "createTime")
	private Date createTime;

	@Column(name = "updateTime")
	private Date updateTime;

	@Column(name = "invalid")
	private boolean invalid;

	public SignEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.SignSet = new HashSet<Integer>();
		this.signStr  =GsonUtil.getJsonInstance().toJson(this.SignSet);
	}

	public void convert() {
		if (getSignStr() != null) {
			this.SignSet = GsonUtil.getJsonInstance().fromJson(this.getSignStr(), new TypeToken<HashSet<Integer>>() {
			}.getType());
		}
	}

	/**
	 * 
	 * 轉存成json格式
	 */
	public void SaveSign() {
		this.setSignStr(GsonUtil.getJsonInstance().toJson(this.SignSet));
		this.notifyUpdate(true);
	}

	public void addSign(int mark) {
		this.SignSet.add(mark);
	}
	
	public void delSign(int mark) {
		this.SignSet.remove(mark);
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

	public String getSignStr() {
		return signStr;
	}

	public void setSignStr(String signStr) {
		this.signStr = signStr;
	}

	public static SignEntity valueOf(Player player) {
		SignEntity signEntity = new SignEntity();
		signEntity.playerId = player.getId();
		return signEntity;
	}

	public Set<Integer> getSignSet() {
		return SignSet;
	}
	
	public boolean isSign(int id) {
		return SignSet.contains(id);
	}

}
