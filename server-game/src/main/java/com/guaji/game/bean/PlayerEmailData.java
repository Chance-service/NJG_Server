package com.guaji.game.bean;

import org.guaji.os.GuaJiTime;

import com.guaji.game.entity.PlayerEntity;

/**
 * 玩家邮件数据
 */
public class PlayerEmailData {
	
	/**
	 * 玩家ID
	 */
	private int id;

	/**
	 * 玩家名称
	 */
	private String playerName;
	
	/**
	 * 玩家等级
	 */
	private int level;
	
	/**
	 * 玩家职业
	 */
	private int prof;
	
	/**
	 * 玩家战力值
	 */
	private int fightValue;
	
	/**
	 * 申请时间or拒绝时间
	 */
	private String createTime;
	
	/**
	 * 申请的公会Id
	 */
	private int allianceId;
	
	/**
	 * 申请的公会名称
	 */
	private String allianceName;
	
	/**
	 * 拒绝理由
	 */
	private String content;
	
	/**
	 * 是否转生
	 */
	private boolean rebirthStage;
	
	public PlayerEmailData(){};
	
	public PlayerEmailData(PlayerEntity playerEntity, int allianceId) {
		this.id = playerEntity.getId();
		this.playerName = playerEntity.getName();
		this.level = playerEntity.getLevel();
		this.prof = playerEntity.getProf();
		this.fightValue = playerEntity.getFightValue();
		this.createTime = GuaJiTime.getTimeString();
		this.allianceId = allianceId;
	}
	
	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getProf() {
		return prof;
	}

	public void setProf(int prof) {
		this.prof = prof;
	}

	public int getFightValue() {
		return fightValue;
	}

	public void setFightValue(int fightValue) {
		this.fightValue = fightValue;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public int getAllianceId() {
		return allianceId;
	}

	public void setAllianceId(int allianceId) {
		this.allianceId = allianceId;
	}

	public String getAllianceName() {
		return allianceName;
	}

	public void setAllianceName(String allianceName) {
		this.allianceName = allianceName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isRebirthStage() {
		return rebirthStage;
	}

	public void setRebirthStage(boolean rebirthStage) {
		this.rebirthStage = rebirthStage;
	}
	
}
