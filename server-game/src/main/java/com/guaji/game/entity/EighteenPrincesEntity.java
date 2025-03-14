package com.guaji.game.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.item.HelpMercenaryInfo;
import com.guaji.game.protocol.Const.EighteenPrincesMedicalKitTYPE;
import com.guaji.game.util.GsonUtil;

@Entity
@Table(name = "eighteenprinces")
public class EighteenPrincesEntity extends DBEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "playerId")
	private int playerId;

	@Column(name = "layerId", columnDefinition = "int default 0")
	private int layerId;
	
	@Column(name = "dropItemId", columnDefinition = "int default 0")
	private int dropItemId;
	
	@Column(name = "firstChallenge")
	private int firstChallenge;

	@Column(name = "formation")
	private String formation;

	@Column(name = "formation_history")
	private String formationHistory;

	@Column(name = "help")
	private String help;

	@Column(name = "helpHistory")
	private String helpHistory;

	@Column(name = "eighteenPrinceCount")
	private int eighteenPrinceCount;

	@Column(name = "eighteenPrinceHelpRoleItemId")
	private int eighteenPrinceHelpRoleItemId;

	@Column(name = "resetTime")
	private int resetTime;

	@Column(name = "enemyformation")
	private String enemyformation;

	@Column(name = "bigMedicalKit")
	private int bigMedicalKit;

	@Column(name = "midleMedicalKit")
	private int midleMedicalKit;

	@Column(name = "smallMedicalKit")
	private int smallMedicalKit;

	@Column(name = "createTime", nullable = false)
	protected Date createTime;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	@Transient
	private boolean formationAlive;

	/**
	 * 玩家
	 */
	@Transient
	private List<Integer> formationIds;

	@Transient
	private Map<Integer, HelpMercenaryInfo> formationHistoryMap;

	@Transient
	private List<Integer> helpIds;

	@Transient
	private Map<Integer, HelpMercenaryInfo> helpHistoryMap;

	@Transient
	private Map<Integer, HelpMercenaryInfo> enemyFormationMap;

	public EighteenPrincesEntity() {
		this.playerId = 0;
		this.layerId = 0;
		this.firstChallenge = 1;
		this.formationHistoryMap = new HashMap<>();
		this.helpHistoryMap = new HashMap<>();
		this.enemyFormationMap = new HashMap<>();
		this.formationIds = new ArrayList<>();
		this.helpIds = new ArrayList<>();
		this.formation = GsonUtil.getJsonInstance().toJson(this.formationIds);
		this.formationHistory = GsonUtil.getJsonInstance().toJson(this.formationHistoryMap);
		this.help = GsonUtil.getJsonInstance().toJson(this.helpIds);
		this.helpHistory = GsonUtil.getJsonInstance().toJson(this.helpHistoryMap);
		this.enemyformation = GsonUtil.getJsonInstance().toJson(this.enemyFormationMap);
		this.eighteenPrinceCount = 0;
		this.eighteenPrinceHelpRoleItemId = 0;
		this.formationAlive = true;
		this.bigMedicalKit = 0;
		this.midleMedicalKit = 0;
		this.smallMedicalKit = 0;
		this.dropItemId=0;
		this.createTime = GuaJiTime.getCalendar().getTime();
	}

	public EighteenPrincesEntity(int playerId, int itemId, List<Integer> itemIds) {
		this.playerId = playerId;
		this.layerId = 0;
		this.firstChallenge = 1;
		this.formationHistoryMap = new HashMap<>();
		this.helpHistoryMap = new HashMap<>();
		this.enemyFormationMap = new HashMap<>();
		this.formationIds = new ArrayList<>();
		this.helpIds = new ArrayList<>();
		// 设置出战阵型
		this.setFormationIds(itemIds);
		for (Integer item : itemIds) {
			HelpMercenaryInfo mercenaryInfo = new HelpMercenaryInfo(100, 100, playerId);
			this.formationHistoryMap.put(item, mercenaryInfo);
		}
		this.formation = GsonUtil.getJsonInstance().toJson(this.formationIds);
		this.formationHistory = GsonUtil.getJsonInstance().toJson(this.formationHistoryMap);
		this.help = GsonUtil.getJsonInstance().toJson(this.helpIds);
		this.helpHistory = GsonUtil.getJsonInstance().toJson(this.helpHistoryMap);
		this.formation = GsonUtil.getJsonInstance().toJson(formationIds);
		this.help = GsonUtil.getJsonInstance().toJson(this.helpIds);
		this.eighteenPrinceCount = 0;
		this.eighteenPrinceHelpRoleItemId = itemId;
		this.enemyformation = GsonUtil.getJsonInstance().toJson(this.enemyFormationMap);
		this.formationAlive = true;
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.bigMedicalKit = 0;
		this.midleMedicalKit = 0;
		this.smallMedicalKit = 0;
		this.resetTime = (int) (GuaJiTime.getNextAM0Date() / 1000);
		this.dropItemId=0;
	}

	/**
	 * 
	 */
	public void convertFormation() {

		if (formation != null && formation.length() > 0) {
			formationIds.clear();
			this.formationIds = GsonUtil.getJsonInstance().fromJson(this.formation,
					new TypeToken<LinkedList<Integer>>() {
					}.getType());
		}

	}

	public void convertHelp() {
		if (help != null && help.length() > 0) {
			helpIds.clear();
			this.helpIds = GsonUtil.getJsonInstance().fromJson(this.help, new TypeToken<LinkedList<Integer>>() {
			}.getType());
		}
	}

	public void convert() {

		if (this.formationHistory != null && this.formationHistory.length() > 0) {
			this.formationHistoryMap.clear();
			this.formationHistoryMap = GsonUtil.getJsonInstance().fromJson(this.formationHistory,
					new TypeToken<HashMap<Integer, HelpMercenaryInfo>>() {
					}.getType());
		}

		if (this.helpHistory != null && this.helpHistory.length() > 0) {
			this.helpHistoryMap.clear();
			this.helpHistoryMap = GsonUtil.getJsonInstance().fromJson(this.helpHistory,
					new TypeToken<HashMap<Integer, HelpMercenaryInfo>>() {
					}.getType());
		}

		if (formation != null && formation.length() > 0) {
			formationIds.clear();
			this.formationIds = GsonUtil.getJsonInstance().fromJson(this.formation,
					new TypeToken<LinkedList<Integer>>() {
					}.getType());
		}

		if (this.help != null && this.help.length() > 0) {
			this.helpIds.clear();
			this.helpIds = GsonUtil.getJsonInstance().fromJson(this.help, new TypeToken<LinkedList<Integer>>() {
			}.getType());
		}
		if (this.enemyformation != null && this.enemyformation.length() > 0) {
			this.enemyFormationMap.clear();
			this.enemyFormationMap = GsonUtil.getJsonInstance().fromJson(this.enemyformation,
					new TypeToken<HashMap<Integer, HelpMercenaryInfo>>() {
					}.getType());
		}

	}

	/**
	 * 
	 * 增加副将编号
	 * 
	 * @param 副将id
	 */
	public void addFormation(int itemId) {
		this.formationIds.add(itemId);
		this.formation = GsonUtil.getJsonInstance().toJson(formationIds);
	}

	/**
	 * @param itemId
	 */
	public void addHelp(int itemId) {
		this.helpIds.add(itemId);
		this.help = GsonUtil.getJsonInstance().toJson(helpIds);
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getLayerId() {
		return layerId;
	}

	public void setLayerId(int layerId) {
		this.layerId = layerId;
	}

	public boolean isFirstChallenge() {
		return firstChallenge == 1;
	}

	public void setFirstChallenge(int firstChallenge) {
		this.firstChallenge = firstChallenge;
	}

	public Map<Integer, HelpMercenaryInfo> getFormationHistoryMap() {
		return formationHistoryMap;
	}

	public void setFormationHistoryMap(Map<Integer, HelpMercenaryInfo> formationHistoryMap) {
		this.formationHistoryMap = formationHistoryMap;
		this.formationHistory = GsonUtil.getJsonInstance().toJson(this.formationHistoryMap);
	}

	public void addFormationMercenaryInfo(int itemId, HelpMercenaryInfo mercenaryinfo) {
		this.formationHistoryMap.put(itemId, mercenaryinfo);
	}

	public Map<Integer, HelpMercenaryInfo> getHelpHistoryMap() {
		return helpHistoryMap;
	}

	public void setHelpHistoryMap(Map<Integer, HelpMercenaryInfo> helpHistoryMap) {
		this.helpHistoryMap = helpHistoryMap;
		this.helpHistory = GsonUtil.getJsonInstance().toJson(this.helpHistoryMap);
	}

	public void addHelpMercenaryInfo(int itemId, HelpMercenaryInfo mercenaryinfo) {
		this.helpHistoryMap.put(itemId, mercenaryinfo);
	}

	public int getEighteenPrinceCount() {
		return eighteenPrinceCount;
	}

	public void setEighteenPrinceCount(int eighteenPrinceCount) {
		this.eighteenPrinceCount = eighteenPrinceCount;
	}

	public int getEighteenPrinceHelpRoleItemId() {
		return eighteenPrinceHelpRoleItemId;
	}

	public void setEighteenPrinceHelpRoleItemId(int eighteenPrinceHelpRoleItemId) {
		this.eighteenPrinceHelpRoleItemId = eighteenPrinceHelpRoleItemId;
	}

	public boolean isFormationAlive() {
		return formationAlive;
	}

	public void setFormationAlive(boolean formationAlive) {
		this.formationAlive = formationAlive;
	}

	public List<Integer> getFormationIds() {
		return formationIds;
	}

	public void setFormationIds(List<Integer> formationIds) {
		this.formationIds = formationIds;
		this.formation = GsonUtil.getJsonInstance().toJson(this.formationIds);
	}

	public void clearFormationIds() {
		this.formationIds.clear();
		this.formation = GsonUtil.getJsonInstance().toJson(this.formationIds);

	}

	public List<Integer> getHelpIds() {
		return helpIds;
	}

	public boolean isHelpHaveAlive() {

		for (Integer playerId : helpIds) {
			if (this.getHelpHistoryMap().get(playerId) != null && this.getHelpHistoryMap().get(playerId).getHp() > 0) {
				return true;
			}

		}
		return false;
	}

	public boolean isFormationHaveAlive() {

		for (Integer itemId : this.formationIds) {
			if (this.getFormationHistoryMap().get(itemId).getHp() > 0) {
				return true;
			}

		}

		return false;
	}

	public void setHelpIds(List<Integer> helpIds) {
		this.helpIds = helpIds;
		this.help = GsonUtil.getJsonInstance().toJson(this.helpIds);

	}

	public void clearHelpIds() {
		this.helpIds.clear();
		this.help = GsonUtil.getJsonInstance().toJson(this.helpIds);

	}

	public int getResetTime() {
		return resetTime;
	}

	public void setResetTime(int resetTime) {
		this.resetTime = resetTime;
	}

	public String getEnemyformation() {
		return enemyformation;
	}

	public void setEnemyformation(String enemyformation) {
		this.enemyformation = enemyformation;
	}

	public Map<Integer, HelpMercenaryInfo> getEnemyFormationMap() {
		return enemyFormationMap;
	}

	public void setEnemyFormationMap(Map<Integer, HelpMercenaryInfo> enemyFormationMap) {
		this.enemyFormationMap = enemyFormationMap;
	}

	public void addEnemyMercenaryInfo(int itemId, HelpMercenaryInfo mercenaryinfo) {
		this.enemyFormationMap.put(itemId, mercenaryinfo);
	}

	public int getBigMedicalKit() {
		return bigMedicalKit;
	}

	public void setBigMedicalKit(int bigMedicalKit) {
		this.bigMedicalKit = bigMedicalKit;
	}

	public int getMidleMedicalKit() {
		return midleMedicalKit;
	}

	public void setMidleMedicalKit(int midleMedicalKit) {
		this.midleMedicalKit = midleMedicalKit;
	}

	public int getSmallMedicalKit() {
		return smallMedicalKit;
	}

	public void setSmallMedicalKit(int smallMedicalKit) {
		this.smallMedicalKit = smallMedicalKit;
	}

	public int getMedicalKitCount(EighteenPrincesMedicalKitTYPE type) {
		int count = 0;
		switch (type) {
		case BIGMEDICALKIT: {
			count = this.getBigMedicalKit();
		}
			break;
		case MIDLEMEDICALKIT: {
			count = this.getMidleMedicalKit();

		}
			break;
		case SMALLMEDICALKIT: {
			count = this.getSmallMedicalKit();
		}
			break;
		}
		return count;
	}

	public void useMedicalKit(EighteenPrincesMedicalKitTYPE type, int count) {

		int costNum = this.getMedicalKitCount(type) < count ? this.getMedicalKitCount(type) : count;
		switch (type) {
		case BIGMEDICALKIT: {
			this.setBigMedicalKit(this.getBigMedicalKit() - costNum);
		}
			break;
		case MIDLEMEDICALKIT: {
			this.setMidleMedicalKit(this.getMidleMedicalKit() - costNum);
		}
			break;
		case SMALLMEDICALKIT: {
			this.setSmallMedicalKit(this.getSmallMedicalKit() - costNum);

		}
			break;
		}

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

	public int getDropItemId() {
		return dropItemId;
	}

	public void setDropItemId(int dropItemId) {
		this.dropItemId = dropItemId;
	}

	@Override
	public void notifyUpdate() {
		this.formationHistory = GsonUtil.getJsonInstance().toJson(this.formationHistoryMap);
		this.helpHistory = GsonUtil.getJsonInstance().toJson(this.helpHistoryMap);
		this.help = GsonUtil.getJsonInstance().toJson(this.helpIds);
		this.enemyformation = GsonUtil.getJsonInstance().toJson(this.enemyFormationMap);
		super.notifyUpdate();
	}

}
