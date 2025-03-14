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
/**
 * 箴言基礎數據
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "motto")
public class MottoEntity extends DBEntity {
    @Id
    @GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
    @GeneratedValue(generator = "AUTO_INCREMENT")
    @Column(name = "id", unique = true)
    private int id = 0;
    
    @Column(name = "playerId")
    private int playerId = 0;
    
    @Column(name = "itemId")
    private int itemId = 0;
    
    @Column(name = "star")
    private int star = 0;
    
    @Column(name = "createTime", nullable = false)
    protected Date createTime = null;

    @Column(name = "updateTime")
    protected Date updateTime;

    @Column(name = "invalid")
    protected boolean invalid;
    
    public MottoEntity() {
    	this.star = 0;
    	this.createTime = GuaJiTime.getCalendar().getTime();
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
    
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
    
    public int getStar() {
    	return this.star;
    }
    
    public void setStar(int star) {
    	this.star = star;
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
