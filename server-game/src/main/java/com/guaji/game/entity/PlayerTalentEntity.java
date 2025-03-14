package com.guaji.game.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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

import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.attr;

@SuppressWarnings("serial")
@Entity
@Table(name = "player_talent")
public class PlayerTalentEntity extends DBEntity {
	
	/**
	 * 真气系统玩家的元素属性;
	 * 
	 * @author qianhang
	 *
	 */
	public class TalentElementAttr {
		/** 元素ID */
		private Const.attr elementId;
		/** 属性值 */
		private int talentAttrvalue;
		/** 属性等阶 */
		private int talentAttrStage;
		/** 属性等级 */
		private int talentAttrLevel;
		
		public TalentElementAttr() {
			this(Const.attr.ICE_ATTACK, 0, 0, 0);
		}
		public TalentElementAttr(attr elementId, int talentAttrvalue, int talentAttrStage, int talentAttrLevel) {
			this.elementId = elementId;
			this.talentAttrvalue = talentAttrvalue;
			this.talentAttrStage = talentAttrStage;
			this.talentAttrLevel = talentAttrLevel;
		}
		public attr getElementId() {
			return elementId;
		}
		public int getTalentAttrvalue() {
			return talentAttrvalue;
		}
		public int getTalentAttrStage() {
			return talentAttrStage;
		}
		public int getTalentAttrLevel() {
			return talentAttrLevel;
		}
		public void setElementId(attr elementId) {
			this.elementId = elementId;
		}
		public void setTalentAttrvalue(int talentAttrvalue) {
			this.talentAttrvalue = talentAttrvalue;
		}
		public void setTalentAttrStage(int talentAttrStage) {
			this.talentAttrStage = talentAttrStage;
		}
		public void setTalentAttrLevel(int talentAttrLevel) {
			this.talentAttrLevel = talentAttrLevel;
		}
		
		public String toString() {
			return this.elementId.getNumber() + "_" + this.talentAttrvalue + "_" + this.talentAttrStage + "_" + this.talentAttrLevel;
		}
	}

	/**
	 * 玩家id;
	 */
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "assigned")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "playerId", nullable = false)
	private int playerId;
	
	/**
	 * 真气点数;
	 */
	@Column(name = "talentNum", nullable = false)
	private int talentNum;
	
	/**
	 * 元素属性信息;
	 */
	@Column(name = "elementAttr", nullable = false)
	private String elementAttr;
	
	/**
	 * 属性映射表;
	 */
	@Transient
	private Map<Const.attr, TalentElementAttr> talentMap = new HashMap<Const.attr, PlayerTalentEntity.TalentElementAttr>();
	
	
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;
	
	public PlayerTalentEntity() {
		
	}
	public PlayerTalentEntity(int playerId) {
		this.playerId = playerId;
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.updateTime = GuaJiTime.getCalendar().getTime();
		this.talentNum = SysBasicCfg.getInstance().getRebirthTalentNum();
		this.elementAttr = "";
		this.invalid = false;
	}

	public int getPlayerId() {
		return playerId;
	}

	public int getTalentNum() {
		return talentNum;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public void setTalentNum(int talentNum) {
		this.talentNum = talentNum;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}
	
	/**
	 * 组装数据;
	 */
	public void convertData() {
		String[] oneAttr = this.elementAttr.split(",");
		for (String eacheAttr : oneAttr) {
			String[] eacheAttrValues = eacheAttr.split("_");
			talentMap.put(Const.attr.valueOf(Integer.parseInt(eacheAttrValues[0])), 
							new TalentElementAttr(Const.attr.valueOf(Integer.parseInt(eacheAttrValues[0])), 
												  Integer.parseInt(eacheAttrValues[1]), 
												  Integer.parseInt(eacheAttrValues[2]), 
												  Integer.parseInt(eacheAttrValues[3])));
		}
		
	}
	
	/**
	 * 转换成String;
	 */
	public void setElementAttr() {
		StringBuilder str = new StringBuilder();
		Iterator<TalentElementAttr> iterator = talentMap.values().iterator();
		while (iterator.hasNext()) {
			TalentElementAttr talentElementAttr = (TalentElementAttr) iterator.next();
			str.append(talentElementAttr.toString());
			if (iterator.hasNext()) {
				str.append(",");
			}
		}
		this.elementAttr = str.toString();
	}
	
	/**
	 * 更新该属性;
	 * 
	 * @param attrId
	 * @param talentAttrvalue
	 * @param talentAttrStage
	 * @param talentAttrLevel
	 */
	public void updateTalentAttr(int attrId,  int talentAttrvalue, int talentAttrStage, int talentAttrLevel) {
		Const.attr valueOf = Const.attr.valueOf(attrId);

		TalentElementAttr talentElementAttr = talentMap.get(valueOf);
		talentElementAttr.setTalentAttrLevel(talentAttrLevel);
		talentElementAttr.setTalentAttrStage(talentAttrStage);
		talentElementAttr.setTalentAttrvalue(talentAttrvalue);
		
		setElementAttr();
	}
	
	public Map<Const.attr, TalentElementAttr> getTalentMap() {
		return talentMap;
	}
	
	public void initElementTalent() {
		for (int i = Const.attr.ICE_ATTACK_VALUE; i <= Const.attr.THUNDER_DENFENCE_VALUE; i++) {
			talentMap.put(Const.attr.valueOf(i), new TalentElementAttr(Const.attr.valueOf(i), 0, 1, 0));
		}
		setElementAttr();
	}
	
	/**
	 * 根据元素ID获取该元素属性;
	 * 
	 * @param attrId
	 * @return
	 */
	public TalentElementAttr getOneTalent(int attrId) {
		Const.attr valueOf = Const.attr.valueOf(attrId);
		if (valueOf == null) {
			return null;
		}
		if (!talentMap.containsKey(valueOf)) {
			return null;
		}
		return talentMap.get(valueOf);
	}
	
	/**
	 * 清空某元素属性;
	 * 
	 * @param attrId
	 */
	public void talentClear(int attrId) {
		updateTalentAttr(attrId, 0, 1, 0);
	}
	
	public void levelupTalentNum(int upLevel) {
		this.talentNum += (SysBasicCfg.getInstance().getLevelupTalentNum() * upLevel);
	}
	
}
