package com.guaji.game.entity;

import java.util.Date;
import java.util.HashSet;
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

import com.guaji.game.util.GameUtil;

@Entity
@Table(name = "title")
@SuppressWarnings("serial")
public class TitleEntity extends DBEntity{
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id;
	
	@Column(name = "playerid")
	private int playerId;
	
	/**
	 * 已获得称号
	 */
	@Column(name ="finishIds")
	private String finishIds = "";
	
	/**
	 * 当前使用称号
	 */
	@Column(name = "useId")
	private int useId = 0;
	
	/**
	 * 玩家获得成就是否变化
	 */
	@Column(name = "ischange")
	private int isChange = 0;


	/**
	 * 团战冠军称号
	 */
	@Column(name = "teambattlechampiondate")
	private Date teamBattleChampionDate;

	/**
	 * 已获得称号集合
	 */
	@Transient
	private Set<Integer> finishIdSet;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime = null;
	
	public TitleEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
		finishIdSet = new HashSet<Integer>();
	}
	
	public static TitleEntity valueOf(int playerId) {
		
		TitleEntity entity = new TitleEntity();
		entity.setPlayerId(playerId);
		return entity;
	}

	@Column(name = "invalid")
	private boolean invalid;
	
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

	public String getFinishIds() {
		return finishIds;
	}

	public void setFinishIds(String finishIds) {
		this.finishIds = finishIds;
	}

	public int getUseId() {
		return useId;
	}

	public void setUseId(int useId) {
		this.useId = useId;
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
	
	public int getIsChange() {
		return isChange;
	}

	public void setIsChange(int isChange) {
		this.isChange = isChange;
	}
	
	public Set<Integer> getFinishIdSet() {
		return finishIdSet;
	}

	public void setFinishIdSet(Set<Integer> finishIdSet) {
		this.finishIdSet = finishIdSet;
	}
	

	public boolean addFinishId(int targetId) {
		if(this.finishIdSet.contains(targetId)) {
			return false;
		}else{
			this.finishIdSet.add(targetId);
			convertFinishSetToStr();
			this.isChange = 1;
			return true;
		}
	}
	
	/**
	 * finishidSet 转化为 String
	 */
	public void convertFinishSetToStr() {
		this.finishIds = GameUtil.join(finishIdSet, ",");
	}

	/**
	 * 删除完成称号
	 * @param targetId
	 */
	public boolean  removeFinishId(int targetId) {
		if(this.finishIdSet.contains(targetId)) {
			this.finishIdSet.remove(Integer.valueOf(targetId));
			convertFinishSetToStr();
			this.isChange = 1;
			return true;
		}
		return false;
	}

	public boolean contains(int titleId) {
		return this.finishIdSet.contains(titleId);
	}
	
	/**
	 * 数据存储转化成内存操作对象
	 */
	public void convert() {
		this.finishIdSet.clear();
		
		String[] finishIdStrs = finishIds.split(",");
		for(String finishIdStr : finishIdStrs) {
			if(finishIdStr.length() > 0) {
				this.finishIdSet.add(Integer.valueOf(finishIdStr));
			}
		}
		
	}
	
	public Date getTeamBattleChampionDate() {
		return teamBattleChampionDate;
	}

	public void setTeamBattleChampionDate(Date teamBattleChampionDate) {
		this.teamBattleChampionDate = teamBattleChampionDate;
	}

}
