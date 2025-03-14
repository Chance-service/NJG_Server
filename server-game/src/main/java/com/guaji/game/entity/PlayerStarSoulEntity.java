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

import org.apache.commons.lang.StringUtils;
import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.util.GsonUtil;

/**
 * 星魂(聖所)实体,激活只影响主角的属性
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "player_star_soul")
public class PlayerStarSoulEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id;

	/** 玩家id */
	@Column(name = "playerId", nullable = false)
	private int playerId;

	/** 星魂信息 */
	@Column(name = "starSoul",columnDefinition = "varchar(2048) not null")
	private String starSoul;

	/** 当前星魂信息 */
	@Transient
	private Map<Integer, Integer> starSoulMap;
	
	/** 精靈島信息 */
	@Column(name = "spriteSoul",columnDefinition = "varchar(2048) not null")
	private String spriteSoul;

	/** 当前精靈島信息 */
	@Transient
	private Map<Integer, Integer> spriteSoulMap;
	
	/** 主角星魂信息 */
	@Column(name = "leaderSoul",columnDefinition = "varchar(2048) not null")
	private String leaderSoul;

	/** 当前主角星魂信息 */
	@Transient
	private Map<Integer, Integer> leaderSoulMap;
	
	/** 職業星魂信息 */
	@Column(name = "classSoul",columnDefinition = "varchar(2048) not null")
	private String classSoul;

	/** 当前職業星魂信息 */
	@Transient
	private Map<Integer, Integer> classSoulMap;
	
	/** (元素)屬性星魂信息 */
	@Column(name = "elementSoul",columnDefinition = "varchar(2048) not null")
	private String elementSoul;

	/** 当前(元素)屬性星魂信息 */
	@Transient
	private Map<Integer, Integer> elementSoulMap;

	@Column(name = "createTime", nullable = false)
	protected Date createTime;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

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

	public String getStarSoul() {
		return starSoul;
	}

	public void setStarSoul(String starSoul) {
		this.starSoul = starSoul;
	}
	
	public String getSpriteSoul() {
		return spriteSoul;
	}


	public void setSpriteSoul(String spriteSoul) {
		this.spriteSoul = spriteSoul;
	}
	
	public String getLeaderSoul() {
		return leaderSoul;
	}

	public void setLeaderSoul(String leaderSoul) {
		this.leaderSoul = leaderSoul;
	}

	public PlayerStarSoulEntity() {
		this.playerId = 0;
		this.starSoul = "";
		this.spriteSoul = "";
		this.leaderSoul ="";
		this.classSoul = "";
		this.elementSoul = "";
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.starSoulMap = new HashMap<Integer, Integer>();
		this.spriteSoulMap = new HashMap<Integer, Integer>();
		this.leaderSoulMap = new HashMap<Integer, Integer>();
		this.classSoulMap = new HashMap<Integer, Integer>();
		this.elementSoulMap = new HashMap<Integer, Integer>();
	}

	public PlayerStarSoulEntity(int playerId) {
		this.playerId = playerId;
		this.starSoul = "";
		this.spriteSoul = "";
		this.leaderSoul ="";
		this.classSoul = "";
		this.elementSoul = "";
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.starSoulMap = new HashMap<Integer, Integer>();
		this.spriteSoulMap = new HashMap<Integer, Integer>();
		this.leaderSoulMap = new HashMap<Integer, Integer>();
		this.classSoulMap = new HashMap<Integer, Integer>();
		this.elementSoulMap = new HashMap<Integer, Integer>();
	}

	public Map<Integer, Integer> getStarSoulMap() {
		if (StringUtils.isEmpty(starSoul))
			return this.starSoulMap;
		return jsonStr2map(starSoul);
	}

	public void setStarSoulMap(Map<Integer, Integer> starSoulMap) {
		this.starSoulMap = starSoulMap;
		if (this.starSoulMap == null) {
			this.starSoul = "";
		}
		this.starSoul = map2jsonStr(this.starSoulMap);
	}
	
	public Map<Integer, Integer> getSpriteSoulMap() {
		if (StringUtils.isEmpty(spriteSoul))
			return this.spriteSoulMap;
		return jsonStr2map(spriteSoul);
	}

	public void setSpriteSoulMap(Map<Integer, Integer> spriteSoulMap) {
		this.spriteSoulMap = spriteSoulMap;
		if (this.spriteSoulMap == null) {
			this.spriteSoul = "";
		}
		this.spriteSoul = map2jsonStr(this.spriteSoulMap);
	}
	
	public Map<Integer, Integer> getLeaderSoulMap() {
		if (StringUtils.isEmpty(leaderSoul))
			return this.leaderSoulMap;
		return jsonStr2map(leaderSoul);
	}

	public void setLeaderSoulMap(Map<Integer, Integer> leaderSoulMap) {
		this.leaderSoulMap = leaderSoulMap;
		if (this.leaderSoulMap == null) {
			this.leaderSoul = "";
		}
		this.leaderSoul = map2jsonStr(this.leaderSoulMap);
	}
	
	public Map<Integer, Integer> getClassSoulMap() {
		if (StringUtils.isEmpty(classSoul))
			return this.classSoulMap;
		return jsonStr2map(classSoul);
	}

	public void setClassSoulMap(Map<Integer, Integer> classSoulMap) {
		this.classSoulMap = classSoulMap;
		if (this.classSoulMap == null) {
			this.classSoul = "";
		}
		this.classSoul = map2jsonStr(this.classSoulMap);
	}
	
	public Map<Integer, Integer> getElementSoulMap() {
		if (StringUtils.isEmpty(elementSoul))
			return this.elementSoulMap;
		return jsonStr2map(elementSoul);
	}

	public void setElementSoulMap(Map<Integer, Integer> elementSoulMap) {
		this.elementSoulMap = elementSoulMap;
		if (this.elementSoulMap == null) {
			this.elementSoul = "";
		}
		this.elementSoul = map2jsonStr(this.elementSoulMap);
	}

	@Override
	public Date getCreateTime() {
		return this.createTime;
	}

	@Override
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;

	}

	@Override
	public Date getUpdateTime() {
		return this.updateTime;
	}

	@Override
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;

	}

	@Override
	public boolean isInvalid() {
		return this.invalid;
	}

	@Override
	public void setInvalid(boolean invalid) {
		this.invalid = invalid;

	}
	
	public String map2jsonStr(Map<Integer, Integer> SoulMap) {
		return GsonUtil.getJsonInstance().toJson(SoulMap, new TypeToken<HashMap<Integer, Integer>>() {
		}.getType());
	}

	public Map<Integer, Integer> jsonStr2map(String jsonStr) {
		return GsonUtil.getJsonInstance().fromJson(jsonStr, new TypeToken<HashMap<Integer, Integer>>() {
		}.getType());
	}
	
}
