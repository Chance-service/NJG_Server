package com.guaji.game.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.config.ConfigManager;
import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.attribute.Attribute;
import com.guaji.game.config.BadgeCfg;
import com.guaji.game.util.GsonUtil;

/**
 * 装备实体对象
 */

@SuppressWarnings("serial")
@Entity
@Table(name = "badge")
public class BadgeEntity extends DBEntity {
    @Id
    @GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
    @GeneratedValue(generator = "AUTO_INCREMENT")
    @Column(name = "id", unique = true)
    private long id = 0;

    @Column(name = "playerId",columnDefinition = "int default 0")
    private int playerId = 0;

    /**
     * 	徽章id
     */
    @Column(name = "badgeId",columnDefinition = "int default 0")
    private int badgeId = 0;

    /**
    * 	舊技能(已無用)
     */
    @Column(name = "skill",columnDefinition = "int default 0")
    private int skill = 0;
    /**
    * 	合成鎖
     */
    @Column(name = "fusionLock",columnDefinition = "int default 0")
    private int fusionLock = 0;
    
    /**
        * 隨機屬性
     */
    @Column(name = "attr",columnDefinition = "varchar(128) not null")
    private String attr = "";
    /**
     * 	技能列表
     */
    @Column(name = "skillStr",columnDefinition = "varchar(128) not null")
    private String skillStr = "[]";
    
	@Transient
	private List<Integer> skillList;
    /**
     * 	洗鍊列表
     */
    @Column(name = "refineStr",columnDefinition = "varchar(128) not null")
    private String refineStr = "[]";
    
	@Transient
	private List<Integer> refineList;

    
    @Column(name = "createTime", nullable = false)
    protected Date createTime = null;

    @Column(name = "updateTime")
    protected Date updateTime;

    @Column(name = "invalid")
    protected boolean invalid;

    /**
     * 属性集合
     */
    @Transient
    protected Attribute attribute;

    public BadgeEntity() {
        this.createTime = GuaJiTime.getCalendar().getTime();
        this.updateTime = GuaJiTime.getCalendar().getTime();
        this.skillList = new ArrayList<>();
        this.refineList = new ArrayList<>();
        this.skillStr = GsonUtil.getJsonInstance().toJson(this.skillList);
        this.refineStr = GsonUtil.getJsonInstance().toJson(this.refineList);
    }
    
    public void convertData() {
		if (skillStr != null && !"".equals(skillStr) && !"null".equals(skillStr)) {
			skillList = GsonUtil.getJsonInstance().fromJson(skillStr, new TypeToken<ArrayList<Integer>>() {
			}.getType());
		} else {
			skillList = new ArrayList<Integer>();
		}
		
		if (refineStr != null && !"".equals(refineStr) && !"null".equals(refineStr)) {
			refineList = GsonUtil.getJsonInstance().fromJson(refineStr, new TypeToken<ArrayList<Integer>>() {
			}.getType());
		} else {
			refineList = new ArrayList<Integer>();
		}
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }


    public int getSkill() {
        return skill;
    }
    
    public void setSkill(int skillId) {
    	this.skill = skillId;
    }

//    public void setLevel(int level) {
//        this.level = level;
//        // 推送普通神器等级事件
//        QuestEventBus.fireQuestEvent(QuestEventType.COMMON_GOD_EQUIP_LEVEL, level, GuaJiXID.valueOf(GsConst.ObjType.PLAYER, this.playerId));
//    }

    public int getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(int badgeId) {
        this.badgeId = badgeId;
    }
    
    public int getFusionLock() {
		return fusionLock;
	}

	public void setFusionLock(int fusionLock) {
		this.fusionLock = fusionLock;
	}

	@Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean isInvalid() {
        return invalid;
    }

    @Override
    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    public Attribute getAttribute() {
        if (attribute == null) {
            attribute = new Attribute();
            attribute.initByString(this.getAttr());
            attribute.add(this.getBasicAttr());
        }
        return attribute;
    }
    
    public Attribute getBasicAttr() {
    	BadgeCfg badgeCfg = ConfigManager.getInstance().getConfigByKey(BadgeCfg.class, this.getBadgeId());
    	if (badgeCfg == null) {
    		return new Attribute();
    	}
    	return badgeCfg.getBasicAttribute();
    }

	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}

	public List<Integer> getSkillList() {
		return skillList;
	}

	public void setSkillList(List<Integer> skillList) {
		this.skillList = skillList;
		this.skillStr = GsonUtil.getJsonInstance().toJson(this.skillList);
	}

	public List<Integer> getRefineList() {
		return refineList;
	}

	public void setRefineList(List<Integer> refineList) {
		this.refineList = refineList;
	    this.refineStr = GsonUtil.getJsonInstance().toJson(this.refineList);
	}
	
	public void setRefineId(int idx ,int id ) {
		this.refineList.set(idx,id);
		this.refineStr = GsonUtil.getJsonInstance().toJson(this.refineList);
	}

	public String getSkillStr() {
		return skillStr;
	}
	
	public String getRefineStr() {
		return refineStr;
	}	
}
