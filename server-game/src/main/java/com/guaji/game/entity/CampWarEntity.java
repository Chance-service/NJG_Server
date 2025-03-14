package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.guaji.game.config.SysBasicCfg;

/**
 * 玩家阵营战数据实体
 */

@SuppressWarnings("serial")
@Entity
@Table(name = "campwar")
public class CampWarEntity extends DBEntity implements Comparable<CampWarEntity>{
	// 数据库实体id
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id;
	
	// 所属玩家id
	@Column(name = "playerId", nullable = false)
	private int playerId;
	
	// 玩家名
	@Column(name = "playerName", nullable = false)
	private String playerName = "";
	
	// 角色配置Id
	@Column(name = "roleCfgId", nullable = false)
	private int roleCfgId = 0;
	
	// 阵营战期号
	@Column(name = "stageId", nullable = false)
	private int stageId;
	
	// 报名时玩家战斗力
	@Column(name = "fightValue", nullable = false)
	private int fightValue = 0;
	
	// 所属阵营
	@Column(name = "campId", nullable = false)
	private int campId = 0;
	
	// 玩家加入战场时基础血量
	@Column(name = "baseMaxBlood", nullable = false)
	private int baseMaxBlood = 0;
	
	// 玩家当前剩余血量
	@Column(name = "curRemainBlood", nullable = false)
	private int curRemainBlood = 0;
	
	// 玩家当前最大血量
	@Column(name = "curMaxBlood", nullable = false)
	private int curMaxBlood = 0;
	
	// 鼓舞次数
	@Column(name = "inspireTimes", nullable = false)
	private int inspireTimes = 0;
	
	// 最大连胜
	@Column(name = "maxWinStreak", nullable = false)
	private int maxWinStreak = 0;
	
	// 当前连胜
	@Column(name = "curWinStreak", nullable = false)
	private int curWinStreak = 0;
	
	// 总计胜利场次
	@Column(name = "totalWin", nullable = false)
	private int totalWin = 0;
	
	// 总计失败场次
	@Column(name = "totalLose", nullable = false)
	private int totalLose = 0;
	
	// 结束获得总声望
	@Column(name = "totalReputation", nullable = false)
	private int totalReputation = 0;
	
	// 结束后获得总金币
	@Column(name = "totalCoins", nullable = false)
	private int totalCoins = 0;
	
	// 本期战斗结果记录
	@Column(name = "allBattleResult", nullable = false)
	private String allBattleResult = "";
	
	// 因鼓舞造成的未结算到剩余血量上的血量增加值
	@Column(name = "addRemainBlood", nullable = false)
	private int addRemainBlood = 0;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;
	
	@Column(name = "updateTime")
	protected Date updateTime;
	
	@Column(name = "invalid")
	protected boolean invalid;
	
	public CampWarEntity(){
		createTime = GuaJiTime.getCalendar().getTime();
	}
	
	public CampWarEntity(int playerId, String playerName, int roleCfgId, int stageId, int campId, int baseMaxBlood, int fightValue){
		createTime = GuaJiTime.getCalendar().getTime();
		this.playerId = playerId;
		this.playerName = playerName;
		this.roleCfgId = roleCfgId;
		this.fightValue = fightValue;
		this.stageId = stageId;
		this.campId = campId;
		this.baseMaxBlood = baseMaxBlood;
		this.curMaxBlood = baseMaxBlood;
		this.curRemainBlood = baseMaxBlood;
	}
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getCampId() {
		return campId;
	}

	public void setCampId(int campId) {
		this.campId = campId;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	
	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public int getRoleCfgId() {
		return roleCfgId;
	}

	public void setRoleCfgId(int roleCfgId) {
		this.roleCfgId = roleCfgId;
	}

	public int getFightValue() {
		return fightValue;
	}

	public void setFightValue(int fightValue) {
		this.fightValue = fightValue;
	}

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public int getBaseMaxBlood() {
		return baseMaxBlood;
	}

	public int getCurRemainBlood() {
		return curRemainBlood;
	}
	
	public void setCurRemainBlood(int curRemainBlood) {
		this.curRemainBlood = curRemainBlood;
		this.curRemainBlood += this.addRemainBlood;
		this.addRemainBlood = 0;
	}
	
	public int getCurMaxBlood() {
		return curMaxBlood;
	}

	public void setCurMaxBlood(int curMaxBlood) {
		this.curMaxBlood = curMaxBlood;
	}

	public int getInspireTimes() {
		return inspireTimes;
	}

	public void setInspireTimes(int inspireTimes) {
		this.inspireTimes = inspireTimes;
	}

	public int getMaxWinStreak() {
		return maxWinStreak;
	}

	public void setMaxWinStreak(int maxWinStreak) {
		this.maxWinStreak = maxWinStreak;
	}

	public int getCurWinStreak() {
		return curWinStreak;
	}

	public void setCurWinStreak(int curWinStreak) {
		this.curWinStreak = curWinStreak;
	}

	public int getTotalWin() {
		return totalWin;
	}

	public void setTotalWin(int totalWin) {
		this.totalWin = totalWin;
	}

	public int getTotalLose() {
		return totalLose;
	}

	public void setTotalLose(int totalLose) {
		this.totalLose = totalLose;
	}

	public int getTotalReputation() {
		return totalReputation;
	}

	public void setTotalReputation(int totalReputation) {
		this.totalReputation = totalReputation;
	}

	public int getTotalCoins() {
		return totalCoins;
	}

	public void setTotalCoins(int totalCoins) {
		this.totalCoins = totalCoins;
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

	@Override
	public int compareTo(CampWarEntity o) {
		if(this.id == o.getId()){
			return 0;
		}
		
		if(this.maxWinStreak != o.getMaxWinStreak()){
			return o.getMaxWinStreak() - this.maxWinStreak;
		} else {
			if(this.fightValue != o.getFightValue()){
				return o.getFightValue() - this.fightValue;
			} else {
				return this.playerId -o.getPlayerId(); 
			}
		}
	}
	
	/**
	 * 鼓舞加成
	 */
	public void inspire(){
		this.inspireTimes ++;
		
		int ratio = SysBasicCfg.getInstance().getCampWarInspireBonuses();
		int addBlood = this.baseMaxBlood * ratio / 100;
		
		this.curMaxBlood += addBlood;
		this.addRemainBlood = addBlood;
	}
	
	/**
	 * 复活
	 */
	public void revive(){
		int ratio = SysBasicCfg.getInstance().getCampWarInspireBonuses();
		this.curMaxBlood = this.baseMaxBlood*(100 + inspireTimes*ratio) / 100;
		this.curRemainBlood = this.curMaxBlood;
	}
	
	/**
	 * 添加单轮战斗结果
	 */
	public void addRoundResult(int result){
		this.allBattleResult += result;
	}
	
	/**
	 * 停服重启是还原鼓舞加成
	 */
	public void restoreInspire(){
		int ratio = SysBasicCfg.getInstance().getCampWarInspireBonuses();
		int addBlood = this.baseMaxBlood * ratio / 100;
		this.curMaxBlood = this.baseMaxBlood + addBlood * this.inspireTimes;
		this.curRemainBlood = curMaxBlood;
	}
}
