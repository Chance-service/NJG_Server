package com.guaji.game.entity;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;

import com.guaji.game.attribute.EliteMapAttr;
import com.guaji.game.attribute.MapAttr;

import net.sf.json.JSONArray;

@SuppressWarnings("serial")
@Entity
@Table(name = "map")
public class MapEntity extends DBEntity {
	@Id
	@Column(name = "playerId")
	private int playerId = 0;

	/**
	 * 状态集
	 */
	@Column(name = "state")
	private String state = "";

	/**
	 * 精英副本
	 */
	@Column(name = "eliteState")
	private String eliteState = "";
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	@Transient
	protected List<MapAttr> mapAttrs;

	@Transient
	protected List<EliteMapAttr> eliteMapAttrs;
	
	/**
	 * 是否领取过宝石奖励(老玩家)
	 */
	@Column(name = "hasGemPrice")
	private boolean hasGemPrice;
	
	public MapEntity() {
		createTime = GuaJiTime.getCalendar().getTime();
		mapAttrs = new LinkedList<MapAttr>();
		eliteMapAttrs = new LinkedList<EliteMapAttr>();
		
		state = "[]";
		eliteState = "[]";
		hasGemPrice = false;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public String getEliteState() {
		return eliteState;
	}

	public void setEliteState(String eliteState) {
		this.eliteState = eliteState;
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

	public List<EliteMapAttr> getEliteMapAttr() {
		return eliteMapAttrs;
	}
	
	@SuppressWarnings("unchecked")
	public void convertMapAttr() {
		if (state != null && state.length() > 0 && mapAttrs.size() <= 0) {
			JSONArray array = JSONArray.fromObject(state);
			List<MapAttr> attrs = ((List<MapAttr>) JSONArray.toCollection(array, MapAttr.class));
			if (attrs != null) {
				mapAttrs = attrs;
			}
		}
		
		if (eliteState != null && eliteState.length() > 0 && eliteMapAttrs.size() <= 0) {
			JSONArray array = JSONArray.fromObject(eliteState);
			List<EliteMapAttr> attrs = ((List<EliteMapAttr>) JSONArray.toCollection(array, EliteMapAttr.class));
			if (attrs != null) {
				eliteMapAttrs = attrs;
			}
		}
	}
	/**
	 * 获取地图属性
	 * 
	 * @param mapId
	 * @return
	 */
	public MapAttr getMapAttr(int mapId) {
		if (mapId > 0) {
			for (MapAttr attr : mapAttrs) {
				if (mapId == attr.getMapId()) {
					return attr;
				}
			}
		}
		return null;
	}
	
	public MapAttr createMapAttr(int mapId) {
		if (mapId > 0 && mapAttrs != null) {
			for (MapAttr attr : mapAttrs) {
				if (mapId == attr.getMapId()) {
					return attr;
				}
			}
			
			MapAttr attr = new MapAttr(mapId, 0);
			mapAttrs.add(attr);
			return attr;
		}
		return null;
	}
	
	public EliteMapAttr getEliteMapAttr(int level) {
		if (level > 0) {
			for (EliteMapAttr attr : eliteMapAttrs) {
				if (level == attr.getLevel()) {
					return attr;
				}
			}
		}
		return null;
	}
	
	public EliteMapAttr createEliteMapAttr(int level) {
		if (level > 0 && eliteMapAttrs != null) {
			for (EliteMapAttr attr : eliteMapAttrs) {
				if (level == attr.getLevel()) {
					return attr;
				}
			}
			
			EliteMapAttr attr = new EliteMapAttr(level, 0);
			eliteMapAttrs.add(attr);
			return attr;
		}
		return null;
	}
	
	@Override
	public void notifyUpdate(boolean async) {
		if (mapAttrs != null) {
			JSONArray array = JSONArray.fromObject(mapAttrs);
			if (array != null) {
				state = array.toString();
			}
		}
		
		if (eliteMapAttrs != null) {
			JSONArray array = JSONArray.fromObject(eliteMapAttrs);
			if (array != null) {
				eliteState = array.toString();
			}
		}
		
		super.notifyUpdate(async);
	}

	public List<MapAttr> getMapAttrs() {
		return mapAttrs;
	}

	public List<EliteMapAttr> getEliteMapAttrs() {
		return eliteMapAttrs;
	}

	public boolean isHasGemPrice() {
		return hasGemPrice;
	}

	public void setHasGemPrice(boolean hasGemPrice) {
		this.hasGemPrice = hasGemPrice;
	}
	
	
	
}
