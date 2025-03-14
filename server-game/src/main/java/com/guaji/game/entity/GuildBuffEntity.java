package com.guaji.game.entity;

import java.util.Date;
import java.util.HashMap;
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
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsonUtil;
/**
 * 公會魔典基礎數據
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "guild_buff")
public class GuildBuffEntity extends DBEntity {    
	@Id
	@GenericGenerator(name = "ASSIGNED", strategy = "assigned")
	@GeneratedValue(generator = "ASSIGNED")
	@Column(name = "playerId", nullable = false, unique = true)
	private int playerId;
    
	/**
	 * 天賦儲存
	 */
	@Column(name = "buffStr",columnDefinition = "varchar(2048) not null")
	private String buffStr;
	
	@Transient
	private Map<Integer,Integer> buffMap;
	
    @Column(name = "freeReset")
    protected boolean freeReset;
            
    @Column(name = "createTime", nullable = false)
    protected Date createTime = null;

    @Column(name = "updateTime")
    protected Date updateTime;

    @Column(name = "invalid")
    protected boolean invalid;
    
    public GuildBuffEntity() {
    	this.createTime = GuaJiTime.getCalendar().getTime();
		this.buffMap = new HashMap<>();
		this.buffStr =  GsonUtil.getJsonInstance().toJson(this.buffMap);
		this.freeReset = false;
    }
    
    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
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
    
    public void convertData() {
		if (buffStr != null && buffStr.length() > 0) {
			buffMap = GsonUtil.getJsonInstance().fromJson(buffStr, new TypeToken<HashMap<Integer, Integer>>() {
			}.getType());
		} else {
			buffMap = new HashMap<>();
		}
    }
    
    public Map<Integer,Integer> getBuffMap(){
    	return this.buffMap;
    }
    
    /**
      * 取出天賦等級
     * @param idx
     * @return
     */
    public int getBuffLv(int idx) {
    	if  (this.buffMap.containsKey(idx)) {
    		return this.buffMap.get(idx);
    	}
    	return 0;
    }
    /**
      * 設定天賦等級
     * @param idx
     * @param lv
     */
    public void setBuffLv(int idx,int lv) {
    	if (this.buffMap.containsKey(idx)) {
    		this.buffMap.replace(idx, lv);
    	} else {
    		this.buffMap.put(idx, lv);
    	}
    	this.buffStr = GsonUtil.getJsonInstance().toJson(this.buffMap);
    }
    /**
     * 	 計算並重置該職業大天賦
     * @param prof
     */
    public void calBigTalent(int prof) {
    	if ((prof <= 0) || (prof > GsConst.GuildBuffConst.MaxProf)) {
    		return ;
    	}
    	int BTalent = prof*GsConst.GuildBuffConst.Type_Base;
    	
		int minlv = GsConst.GuildBuffConst.MaxTalentLV;
		int lv = 0;
		// 取最小天賦裡面等級最小的
		for(int i = 1; i <= GsConst.GuildBuffConst.MaxTalentID; i++){
			int talentId = BTalent + i;
			lv = getBuffLv(talentId);
			minlv = Math.min(lv,minlv);
		}
		int BigLv = (minlv / 10); //大天賦等級 = 小天賦等級/10取商(6個中最小的)
		setBuffLv(BTalent,BigLv);
    }
    /**
     *	 重置所有大天賦
     */
    public void resetAllBigTalent() {
    	for (int prof = 1 ; prof <= GsConst.GuildBuffConst.MaxProf ; prof++) {
    		calBigTalent(prof);
    	}
    	
    }
    
    public boolean getFreeReset() {
    	return freeReset;
    }
    
    public void setFreeReset(boolean bol) {
    	this.freeReset = bol;
    }
}
