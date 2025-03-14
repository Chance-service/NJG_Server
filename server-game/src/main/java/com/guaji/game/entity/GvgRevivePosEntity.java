package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.guaji.db.DBEntity;
import org.guaji.db.DBManager;

/**
 * @author Administrator
 * 	联盟战复活点数据	
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "gvg_revive_point")
public class GvgRevivePosEntity extends DBEntity{
	/**
	 * 复活点编号
	 */
	@Id
	@Column(name = "id", unique = true)
	private int id;
	
	/**
	 * 	联盟编号
	 */
	@Column(name = "allianceId", nullable = true)
	private int allianceId;
	
	/**
	 * 	复活点位置
	 */
	@Column(name = "point_X", nullable = true)
	private int point_X;
	
	/**
	 * 复活点位置_Y
	 */
	@Column(name = "point_Y", nullable = true)
	private int point_Y;
	
	/**
	 * 占用当前复活点的用户编号(盟主或付盟主)
	 */
	@Column(name = "playerID", nullable = true)
	private int playerID;
	
	/**
	 * 操作复活时间
	 */
	@Column(name = "actionTime", nullable = true)
	private Date actionTime;

	
	
	public static GvgRevivePosEntity createEntity(int _allianceId, int _playerId,int  _pointX, int _pointY, Date _actiontime) {
		
		GvgRevivePosEntity entity = new GvgRevivePosEntity();
		entity.allianceId=_allianceId;
		entity.point_X=_pointX;
		entity.point_Y=_pointY;
		entity.playerID=_playerId;
		entity.actionTime=_actiontime;
		if(DBManager.getInstance().create(entity)) {
			return entity;
		}
			
	
		return null;
	}
	
	
	
	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public int getAllianceId() {
		return allianceId;
	}

	public void setAllianceId(int allianceId) {
		this.allianceId = allianceId;
	}

	public int getPoint_X() {
		return point_X;
	}

	public void setPoint_X(int point_X) {
		this.point_X = point_X;
	}

	public int getPoint_Y() {
		return point_Y;
	}

	public void setPoint_Y(int point_Y) {
		this.point_Y = point_Y;
	}

	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

	public Date getActionTime() {
		return actionTime;
	}

	public void setActionTime(Date actionTime) {
		this.actionTime = actionTime;
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
