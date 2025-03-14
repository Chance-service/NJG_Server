package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.guaji.db.DBEntity;
import org.guaji.db.DBManager;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

/**
 * @author Administrator
 * 	联盟复活操作数据
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "gvg_revive_log")
public class GvgReviveLogEntity extends DBEntity{

	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id;
	
	
	/**
	 *   操作复活工会名称
	 */
	@Column(name = "allianceName", nullable = false)
	private String attackerName;
	
	/**
	 * 	持有复活点的用户编号
	 */
	@Column(name = "playerId", nullable = false)
	private int playerId;
	
	
	/**
	 * 	持有复活点的联盟编号
	 */
	@Column(name = "allianceId", nullable = false) 
	private int allianceId;
	
	/**
	 * 	复活消耗的元宝
	 */
	@Column(name = "consume", nullable = false) 
	private int consume;
	
	
	/**
	 * 	复活点路径
	 */
	@Column(name = "pointId", nullable = false) 
	private int pointId;
	


	/**
	 *	 操作复活时间点
	 */
	@Column(name = "createtime")
	protected Date createtime;

	
	public GvgReviveLogEntity() {
		createtime = GuaJiTime.getCalendar().getTime();
	}

	public static GvgReviveLogEntity createEntity(int _playerId, int _allianceId,String _attackerName, int _consume, int _pointId) {
	
		GvgReviveLogEntity entity = new GvgReviveLogEntity();
		entity.allianceId=_allianceId;
		entity.attackerName=_attackerName;
		entity.consume=_consume;
		entity.playerId=_playerId;
		entity.pointId=_pointId;
		if(DBManager.getInstance().create(entity)) {
			return entity;
		}
			
		
	
		return null;
	}

	public int getPlayerId() {
		return playerId;
	}


	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}


	public int getAllianceId() {
		return allianceId;
	}


	public void setAllianceId(int allianceId) {
		this.allianceId = allianceId;
	}


	public Date getCreatetime() {
		return createtime;
	}


	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
	
	public String getAttackerName() {
		return attackerName;
	}

	public void setAttackerName(String attackerName) {
		this.attackerName = attackerName;
	}

	public int getConsume() {
		return consume;
	}

	public void setConsume(int consume) {
		this.consume = consume;
	}

	public int getPointId() {
		return pointId;
	}

	public void setPointId(int pointId) {
		this.pointId = pointId;
	}

	@Override
	public Date getCreateTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCreateTime(Date createTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Date getUpdateTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUpdateTime(Date updateTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isInvalid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setInvalid(boolean invalid) {
		// TODO Auto-generated method stub
		
	}

	
}
