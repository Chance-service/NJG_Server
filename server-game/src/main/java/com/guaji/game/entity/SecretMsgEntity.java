package com.guaji.game.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
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
import com.guaji.game.util.GsonUtil;
/**
 * 秘密信條基礎數據
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "secretmsg")
public class SecretMsgEntity extends DBEntity {
    @Id
    @GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
    @GeneratedValue(generator = "AUTO_INCREMENT")
    @Column(name = "id", unique = true)
    private int id = 0;
    /**
     * 玩家ID
     */
    @Column(name = "playerId")
    private int playerId = 0;
    /**
     * 英雄ID
     */
    @Column(name = "itemId")
    private int itemId = 0;
    /**
     * 解鎖最大照片ID
     */
    @Column(name = "ablumMaxId",columnDefinition = "int default 0")
    private int ablumMaxId = 0;
    
	@Column(name = "choiceMsgStr",columnDefinition = "varchar(4096) not null")
	private String choiceMsgStr;
	
	@Column(name = "freeCfgStr",columnDefinition = "varchar(1024) not null")
	private String freeCfgStr;
	
	@Column(name = "costCfgStr",columnDefinition = "varchar(1024) not null")
	private String costCfgStr;
	
	@Column(name = "unlockCfgStr",columnDefinition = "varchar(1024) not null")
	private String unlockCfgStr;
	
	@Transient
	private LinkedHashMap<Integer,Integer> choiceMsgMap;
	/**
	 * 紀錄已領免費CfgId
	 */
	@Transient
	private Set<Integer> FreeCfgId;
	/**
	 * 紀錄已領收費CfgId
	 */
	@Transient
	private Set<Integer> CostCfgId;
	/**
	 * 紀錄解鎖照片ID
	 */
	@Transient
	private Set<Integer> unlockCfgId;
	  
    @Column(name = "createTime", nullable = false)
    protected Date createTime = null;

    @Column(name = "updateTime")
    protected Date updateTime;

    @Column(name = "invalid")
    protected boolean invalid;
    
    public SecretMsgEntity() {
    	this.createTime = GuaJiTime.getCalendar().getTime();
    	this.choiceMsgMap = new LinkedHashMap<>();
    	this.FreeCfgId = new HashSet<>();
    	this.CostCfgId = new HashSet<>();
    	this.unlockCfgId = new HashSet<>();
    	this.choiceMsgStr = GsonUtil.getJsonInstance().toJson(choiceMsgMap);
    	this.freeCfgStr = GsonUtil.getJsonInstance().toJson(FreeCfgId);
    	this.costCfgStr = GsonUtil.getJsonInstance().toJson(CostCfgId);
    	this.unlockCfgStr = GsonUtil.getJsonInstance().toJson(unlockCfgId);
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
    
    public int getAblumMaxId() {
		return ablumMaxId;
	}

	public void setAblumMaxId(int ablumMaxId) {
		this.ablumMaxId = ablumMaxId;
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
    
    public void addChoiceMsg(int id,int choice) {
    		this.choiceMsgMap.put(id,choice);
    		this.choiceMsgStr = GsonUtil.getJsonInstance().toJson(choiceMsgMap);
    }
    
    public Map<Integer,Integer> getChoiceMsgMap() {
    	return this.choiceMsgMap;
    }
    
    public void setChoiceMsgMap(LinkedHashMap<Integer, Integer> choiceMsgMap) {
		this.choiceMsgMap = choiceMsgMap;
		this.choiceMsgStr = GsonUtil.getJsonInstance().toJson(choiceMsgMap);
	}

	/**
     *	 取回答留言數量
     * @return
     */
    public int getMsgCount() {
    	return this.choiceMsgMap.size();
    }
    
    public void convert() {
    	this.choiceMsgMap = GsonUtil.getJsonInstance().fromJson(this.choiceMsgStr,new TypeToken<LinkedHashMap<Integer,Integer>>() {}.getType());
    	this.FreeCfgId = GsonUtil.getJsonInstance().fromJson(this.freeCfgStr,new TypeToken<HashSet<Integer>>() {}.getType());
    	this.CostCfgId = GsonUtil.getJsonInstance().fromJson(this.costCfgStr,new TypeToken<HashSet<Integer>>() {}.getType());
    	this.unlockCfgId = GsonUtil.getJsonInstance().fromJson(this.unlockCfgStr,new TypeToken<HashSet<Integer>>() {}.getType());
    }
    
	public Set<Integer> getFreeCfgId(){
		if (this.FreeCfgId == null) {
			this.FreeCfgId = new HashSet<>();
		}
		return this.FreeCfgId;
	}
	
	public void addFreeCfgId(int cfgId) {
		if (this.FreeCfgId == null) {
			this.FreeCfgId = new HashSet<>();
		}
		if (!this.FreeCfgId.contains(cfgId)) {
			this.FreeCfgId.add(cfgId);
			this.freeCfgStr = GsonUtil.getJsonInstance().toJson(this.FreeCfgId);
		}
	}
	
	public Set<Integer> getCostCfgId(){
		if (this.CostCfgId == null) {
			this.CostCfgId = new HashSet<>();
		}
		return this.CostCfgId;
	}
	
	public void addCostCfgId(int cfgId) {
		if (this.CostCfgId == null) {
			this.CostCfgId = new HashSet<>();
		}
		if (!this.CostCfgId.contains(cfgId)) {
			this.CostCfgId.add(cfgId);
			this.costCfgStr = GsonUtil.getJsonInstance().toJson(this.CostCfgId);
		}
	}

	public Set<Integer> getUnlockCfgId() {
		if (this.unlockCfgId == null) {
			this.unlockCfgId = new HashSet<>();
		}
		return unlockCfgId;
	}

	public void addUnlockCfgId(int cfgId) {
		if (this.unlockCfgId == null) {
			this.unlockCfgId = new HashSet<>();
		}
		if (!this.unlockCfgId.contains(cfgId)) {
			this.unlockCfgId.add(cfgId);
			this.unlockCfgStr = GsonUtil.getJsonInstance().toJson(this.unlockCfgId);
		}
	}
}
