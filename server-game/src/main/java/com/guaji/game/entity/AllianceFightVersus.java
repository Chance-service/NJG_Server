package com.guaji.game.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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

import com.guaji.game.config.AllianceBattleTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.protocol.AllianceBattle.AFUnitState;
import com.guaji.game.protocol.AllianceBattle.AllianceBattleState;
import com.guaji.game.protocol.AllianceBattle.FightGroup;
import com.guaji.game.util.GameUtil;

@Entity
@Table(name = "alliance_fight_versus")
@SuppressWarnings("serial")
public class AllianceFightVersus extends DBEntity{

	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id ;
	
	@Column(name = "stageId", unique = true)
	private int stageId ;
	
	/**
	 * 哪一个组的战斗 {@link }
	 */
	@Column(name = "fightGroup", unique = true)
	private int fightGroup ;
	
	@Column(name = "leftId")
	private int leftId ;
	
	@Column(name = "rightId")
	private int rightId ;
	
	@Column(name = "winId")
	private int winId ;
	
	private int state ;
	
	@Transient
	private List<Integer> investLeftInfoList ;
	
	@Column(name = "investLeftStr")
	private String investLeftStr ;
	
	@Transient
	private List<Integer> investRightInfoList ;
	
	@Column(name = "investRightStr")
	private String investRightStr ;
	
	@Transient
	private List<AllianceFightUnit> fightUnits ;
	
	@Column(name = "isRewardInvest")
	private boolean isRewardInvest ;
 	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = GuaJiTime.getCalendar().getTime();
	
	@Column(name = "updateTime")
	protected Date updateTime;
	
	@Column(name = "invalid")
	protected boolean invalid;
	
	public static class InvestInfo{
		
		private int playerId ;
		
		private int allianceId ; 
		
		private boolean isReward ;
		
		public static InvestInfo valueOf(int playerId, int allianceId, boolean isReward) {
			InvestInfo investInfo = new InvestInfo();
			investInfo.setPlayerId(playerId);
			investInfo.setAllianceId(allianceId);
			investInfo.setReward(isReward);
			return investInfo;
		}
		
		public String toString() {
			return String.format("%d_%d_%d", playerId, allianceId, isReward ? 1 : 0);
		}
		
		public int getPlayerId() {
			return playerId;
		}

		public void setPlayerId(int playerId) {
			this.playerId = playerId;
		}

		public int getAllianceId() {
			return allianceId;
		}

		public void setAllianceId(int allianceId) {
			this.allianceId = allianceId;
		}

		public boolean isReward() {
			return isReward;
		}

		public void setReward(boolean isReward) {
			this.isReward = isReward;
		}
	}
	
	public AllianceFightVersus() {
		this.fightUnits = new LinkedList<>();
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.investLeftInfoList = new LinkedList<>();
		this.investRightInfoList = new LinkedList<>();
		this.investLeftStr = GameUtil.join(this.getInvestLeftInfoList(), ",");
		this.investRightStr = GameUtil.join(this.getInvestRightInfoList(), ",");
	}
	
	public void convertData() {
		this.getInvestLeftInfoList().clear();
		this.getInvestRightInfoList().clear();
		if(this.investLeftStr != null && this.investLeftStr.length() > 0) {
			String[] investInfoStrs =  this.investLeftStr.split(",");
			for(String investStr : investInfoStrs) {
				this.getInvestLeftInfoList().add(Integer.valueOf(investStr));
			}
		}
		if(this.investRightStr != null && this.investRightStr.length() > 0) {
			String[] investInfoStrs =  this.investRightStr.split(",");
			for(String investStr : investInfoStrs) {
				this.getInvestRightInfoList().add(Integer.valueOf(investStr));
			}
		}
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public int getFightGroup() {
		return fightGroup;
	}

	public void setFightGroup(int fightGroup) {
		this.fightGroup = fightGroup;
	}

	public int getLeftId() {
		return leftId;
	}

	public void setLeftId(int leftId) {
		this.leftId = leftId;
	}

	public int getRightId() {
		return rightId;
	}

	public void setRightId(int rightId) {
		this.rightId = rightId;
	}

	public int getWinId() {
		return winId;
	}

	public void setWinId(int winId) {
		this.winId = winId;
	}

	public int getFailId(){
		return this.winId == leftId ? rightId : leftId;
	}
	
	public int getState() {
		return state;
	}
	
	public static Map<FightGroup, AllianceBattleState> GROUP_REGISTER_MAP = new HashMap<>();
	
	static {
		GROUP_REGISTER_MAP.put(FightGroup.GROUP_32,AllianceBattleState.FS32_16_FIGHTING);
		GROUP_REGISTER_MAP.put(FightGroup.GROUP_16,AllianceBattleState.FS16_8_FIGHTING);
		GROUP_REGISTER_MAP.put(FightGroup.GROUP_8,AllianceBattleState.FS8_4_FIGHTING);
		GROUP_REGISTER_MAP.put(FightGroup.GROUP_4,AllianceBattleState.FS4_2_FIGHTING);
		GROUP_REGISTER_MAP.put(FightGroup.GROUP_2,AllianceBattleState.FS2_1_FIGHTING);
	}
	
	public AFUnitState getFightState() {
		AllianceBattleTimeCfg timeCfg = AllianceBattleTimeCfg.getCfg(GROUP_REGISTER_MAP.get(FightGroup.valueOf(fightGroup)).getNumber());
		long nowTime = GuaJiTime.getMillisecond();
		long startTime = timeCfg.getStartSpecifiedDate(stageId).getTime();
		if(nowTime < startTime) {
			return AFUnitState.AF_NONE;
		}
		int spaceTimes = (int)(nowTime - startTime) / 1000 / SysBasicCfg.getInstance().getAllianceBattleTime();
		
		if(spaceTimes < 0) {
			return AFUnitState.AF_NONE;
		}
		if(spaceTimes >= this.getFightUnits().size()) {
			return AFUnitState.AF_END;
		}
		
		return AFUnitState.AF_FIGHTING;
	}

	public void setState(int state) {
		this.state = state;
	}

	/**
	 * 添加每一个小team战斗信息
	 * @param allianceFightUnit
	 */
	public void addFightUnit(AllianceFightUnit allianceFightUnit) {
		this.getFightUnits().add(allianceFightUnit);
	}

	public List<AllianceFightUnit> getFightUnits() {
		return fightUnits;
	}

	public void setFightUnits(List<AllianceFightUnit> fightUnits) {
		this.fightUnits = fightUnits;
	}

	public void addInvest(int playerId, int allianceId){
		if(allianceId == leftId) {
			this.getInvestLeftInfoList().add(playerId);
			this.investLeftStr = GameUtil.join(this.getInvestLeftInfoList(), ",");
		}else{
			this.getInvestRightInfoList().add(playerId);
			this.investRightStr = GameUtil.join(this.getInvestRightInfoList(), ",");
		}
	}

	public boolean containsPlayer(int playerId) {
		return this.getInvestLeftInfoList().contains(Integer.valueOf(playerId)) || this.getInvestRightInfoList().contains(Integer.valueOf(playerId));
	}
	
	public boolean isRewardInvest() {
		return isRewardInvest;
	}

	public void setRewardInvest(boolean isRewardInvest) {
		this.isRewardInvest = isRewardInvest;
	}

	public int getInvestAllianceId(int playerId) {
		if(this.getInvestLeftInfoList().contains(Integer.valueOf(playerId))) {
			return leftId;
		}else if(this.getInvestRightInfoList().contains(Integer.valueOf(playerId))){
			return rightId;
		}
		return 0;
	}

	public List<Integer> getInvestLeftInfoList() {
		return investLeftInfoList;
	}

	public void setInvestLeftInfoList(List<Integer> investLeftInfoList) {
		this.investLeftInfoList = investLeftInfoList;
	}

	public List<Integer> getInvestRightInfoList() {
		return investRightInfoList;
	}

	public void setInvestRightInfoList(List<Integer> investRightInfoList) {
		this.investRightInfoList = investRightInfoList;
	}
	
}
