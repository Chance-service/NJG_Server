package com.guaji.game.entity;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.guaji.game.attribute.Attribute;
import com.guaji.game.util.GameUtil;

/**
 * 元素实体对象
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "element")
public class ElementEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private long id = 0;

	@Column(name = "playerId")
	private int playerId = 0;

	/**
	 * 配置Id
	 */
	@Column(name = "itemId")
	private int itemId = 0;

	/**
	 * 元素等级
	 */
	@Column(name = "level")
	private int level = 0;

	/**
	 * 元素经验
	 */
	@Column(name = "exp")
	private int exp = 0;

	/**
	 * 元素品质
	 */
	@Column(name = "quality")
	private int quality = 0;

	@Column(name = "basicAttrStr")
	private String basicAttrStr ;
	/**
	 * 基础属性集
	 */
	@Transient
	private Attribute basicAttr ;
	@Column(name = "extraAttrStr")
	private String extraAttrStr ;
	
	/**
	 * 重铸的attrIds
	 */
	@Column(name = "recastAttrIdStr")
	private String recastAttrIdStr ;
	
	@Transient
	private List<Integer> recastAttrIds ;
	
	/**
	 * 重铸的attrValues
	 */
	@Column(name = "recastAttrValueStr")
	private String recastAttrValueStr ;
	
	@Transient
	private List<Integer> recastAttrValues ;
	
	@Column(name = "recastAttrId")
	private int recastAttrId = 0;
	
	/**
	 * 额外属性集 
	 */
	@Transient
	private Attribute extraAttr ;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	/**
	 * 装备属性集合
	 */
	@Transient
	protected Attribute attribute;

	public ElementEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.updateTime = GuaJiTime.getCalendar().getTime();
		this.attribute = new Attribute();
		this.extraAttr = new Attribute();
		this.basicAttr = new Attribute();
		this.recastAttrIds = new LinkedList<>();
		this.recastAttrValues = new LinkedList<>();
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

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
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

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	/**
	 * 计算积分
	 * @return
	 */
	public int getScore() {
		return 0;
	}

	public Attribute getBasicAttr() {
		return basicAttr;
	}

	public void setBasicAttr(Attribute basicAttr) {
		this.basicAttr = basicAttr;
	}

	public Attribute getExtraAttr() {
		return extraAttr;
	}

	public void setExtraAttr(Attribute extraAttr) {
		this.extraAttr = extraAttr;
	}

	public void addBasicAttr(int attrId, int attrValue) {
		if(this.basicAttr == null) {
			this.basicAttr = new Attribute();
		}
		this.basicAttr.add(attrId, attrValue);
		this.basicAttrStr = this.basicAttr.toString();
	}
	
	public void addExtraAttr(int attrId, int attrValue) {
		if(this.extraAttr == null) {
			this.extraAttr = new Attribute();
		}
		this.extraAttr.add(attrId, attrValue);
		this.extraAttrStr = this.extraAttr.toString();
	}
	
	public void convertData() {
		if(this.basicAttrStr != null && this.basicAttrStr.length() > 0) {
			this.basicAttr = Attribute.valueOf(this.basicAttrStr);
		}
		
		if(this.extraAttrStr != null && this.extraAttrStr.length() > 0) {
			this.extraAttr = Attribute.valueOf(this.extraAttrStr);
		}
		
		if(this.recastAttrIdStr != null && this.recastAttrIdStr.length() > 0) {
			String[] ss = this.recastAttrIdStr.split(",");
			for(String s : ss) {
				this.recastAttrIds.add(Integer.valueOf(s));
			}
		}
		
		if(this.recastAttrValueStr != null && this.recastAttrValueStr.length() > 0) {
			String[] ss = this.recastAttrValueStr.split(",");
			for(String s : ss) {
				this.recastAttrValues.add(Integer.valueOf(s));
			}
		}
	}

	public List<Integer> getRecastAttrIds() {
		return recastAttrIds;
	}

	public void setRecastAttrIds(List<Integer> recastAttrIds) {
		this.recastAttrIds = recastAttrIds;
		this.recastAttrIdStr = GameUtil.join(this.recastAttrIds, ",");
	}

	public List<Integer> getRecastAttrValues() {
		return recastAttrValues;
	}

	public void setRecastAttrValues(List<Integer> recastAttrValues) {
		this.recastAttrValues = recastAttrValues;
		this.recastAttrValueStr = GameUtil.join(this.recastAttrValues, ",");
	}

	public int getRecastAttrId() {
		return recastAttrId;
	}

	public void setRecastAttrId(int recastAttrId) {
		this.recastAttrId = recastAttrId;
	}

	public void removeExtraAttr(int attrId) {
		this.extraAttr.removeAttr(attrId);
		this.extraAttrStr = this.extraAttr.toString();
	}

	public void clearRecastInfo() {
		this.recastAttrIds.clear();
		this.recastAttrValues.clear();
		this.recastAttrIdStr = "";
		this.recastAttrValueStr = "";
		this.recastAttrId = 0;
	}
	
}
