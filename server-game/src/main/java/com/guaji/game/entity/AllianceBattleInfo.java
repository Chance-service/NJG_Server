package com.guaji.game.entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.db.DBManager;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.manager.AllianceBattleManager;
import com.guaji.game.protocol.AllianceBattle.AFUnitState;
import com.guaji.game.protocol.AllianceBattle.FightGroup;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsonUtil;

@Entity
@Table(name = "alliance_battle_info")
@SuppressWarnings("serial")
public class AllianceBattleInfo extends DBEntity{
	@Id
	@GenericGenerator(name = "ASSIGN", strategy = "assigned")
	@GeneratedValue(generator = "ASSIGN")
	@Column(name = "stageId", unique = true)
	private int stageId ;
	
	@Column(name = "state")
	private int state ;
	
	@Column(name = "allianceItemsStr")
	private String allianceItemsStr;
	
	@Column(name = "battleResultStr")
	private String battleResultStr;
	
	/**
	 * 32强 16 8 4 2 的列表 有序
	 */
	@Transient
	private Map<Integer,List<Integer>> battleItemsMap ;

	@Transient
	private Map<Integer, List<Integer>> battleResult;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime ;
	
	@Column(name = "updateTime")
	protected Date updateTime;
	
	@Column(name = "invalid")
	protected boolean invalid;

	public AllianceBattleInfo() {
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.battleItemsMap = new ConcurrentHashMap<Integer, List<Integer>>();
		this.setBattleResult(new ConcurrentHashMap<Integer, List<Integer>>());
		this.allianceItemsStr = GsonUtil.getJsonInstance().toJson(this.battleItemsMap);
		this.battleResultStr = GsonUtil.getJsonInstance().toJson(this.getBattleResult());
	}
	
	public void convertData(){
		if(this.allianceItemsStr != null && !"".equals(this.allianceItemsStr)) {
			this.battleItemsMap = GsonUtil.getJsonInstance().fromJson(this.allianceItemsStr, new TypeToken<ConcurrentHashMap<Integer,LinkedList<Integer>>>() {}.getType());
		}
		if(this.battleResultStr != null && !"".equals(this.battleResultStr)) {
			this.battleResult = GsonUtil.getJsonInstance().fromJson(this.battleResultStr,new TypeToken<ConcurrentHashMap<Integer,LinkedList<Integer>>>() {}.getType());
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

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}


	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	/**
	 * 计算对阵列表
	 * @param group
	 * @param battleItems
	 * @return
	 */
	public List<AllianceFightVersus> calcFightVersus(FightGroup group, List<AllianceBattleItem> battleItems) {
		List<AllianceFightVersus> fightVersusList = new LinkedList<>();
		if(battleItems.size() < GsConst.AllianceBattle.ALLIANCE_BATTLE_RANK_SIZE >> (group.getNumber())) {
			return fightVersusList;
		}
		//根据阶段生成对战表
		List<Integer> battleItemIds = new LinkedList<>();
		for(AllianceBattleItem battleItem : battleItems) {
			battleItemIds.add(battleItem.getId());
		}
		this.battleItemsMap.put(group.getNumber(), battleItemIds);
		if(group == FightGroup.GROUP_32) {   //32强打乱顺序
//			for(AllianceBattleVersusCfg versusCfg : ConfigManager.getInstance().getConfigList(AllianceBattleVersusCfg.class)){
//				if(versusCfg == null) {
//					continue;
//				}
//				int leftIndex = versusCfg.getLeftIndex();
//				int rightIndex = versusCfg.getRightIndex();
//				int leftId = battleItems.get(leftIndex - 1).getAllianceId();
//				int rightId = battleItems.get(rightIndex - 1).getAllianceId();
//				AllianceFightVersus fightVersus = new AllianceFightVersus();
//				fightVersus.setLeftId(leftId);
//				fightVersus.setRightId(rightId);
//				fightVersus.setStageId(this.stageId);
//				fightVersus.setFightGroup(group.getNumber());
//				fightVersus.setState(AFUnitState.AF_FIGHTING_VALUE);
//					
//				DBManager.getInstance().create(fightVersus);
//				fightVersusList.add(fightVersus);
//			}
			Collections.shuffle(battleItems);
		}
			for(int i=0;i<battleItems.size()/2;i++) {
				AllianceFightVersus fightVersus = new AllianceFightVersus();
				fightVersus.setLeftId(battleItems.get(i*2).getAllianceId());
				fightVersus.setRightId(battleItems.get(i*2 + 1).getAllianceId());
				fightVersus.setStageId(this.stageId);
				fightVersus.setFightGroup(group.getNumber());
				fightVersus.setState(AFUnitState.AF_FIGHTING_VALUE);
					
				DBManager.getInstance().create(fightVersus);
				fightVersusList.add(fightVersus);
			}
		// 保存8强 4强 亚军 冠军 的信息
		if(group == FightGroup.GROUP_8) {
			this.getBattleResult().put(GsConst.AllianceBattle.TOP_8, battleItemIds);
		}else if(group == FightGroup.GROUP_4) {
			this.getBattleResult().put(GsConst.AllianceBattle.TOP_4, battleItemIds);
		}
		
		this.battleResultStr = GsonUtil.getJsonInstance().toJson(this.getBattleResult());
		this.allianceItemsStr = GsonUtil.getJsonInstance().toJson(this.battleItemsMap);
		
		this.notifyUpdate(false);
		
		return fightVersusList;
	}
	
	/**
	 * 设置亚军
	 * @param battleItem
	 */
	public void setRunnerUp(int allianceId) {
		AllianceBattleItem battleItem = getBattleItemByAllianceId(allianceId);
		this.battleResult.put(GsConst.AllianceBattle.TOP_2, Arrays.asList(new Integer[]{battleItem.getId()}));
		this.battleResultStr = GsonUtil.getJsonInstance().toJson(this.getBattleResult());
	}
	
	/**
	 * 设置冠军
	 * @param battleItem
	 */
	public void setChampion(int allianceId) {
		AllianceBattleItem battleItem = getBattleItemByAllianceId(allianceId);
		this.battleResult.put(GsConst.AllianceBattle.TOP_1, Arrays.asList(new Integer[]{battleItem.getId()}));
		this.battleResultStr = GsonUtil.getJsonInstance().toJson(this.getBattleResult());
	}
	
	private AllianceBattleItem getBattleItemByAllianceId(int allianceId) {
		return AllianceBattleManager.getInstance().getBattleItem(allianceId);
	}

	public List<AllianceFightVersus> calcFightVersusByIds(FightGroup group, List<Integer> winIds) {
		List<AllianceBattleItem> battleItems = new LinkedList<>();
		for(int allianceId : winIds) {
			battleItems.add(getBattleItemByAllianceId(allianceId));
		}
		return calcFightVersus(group, battleItems);
	}

	public Map<Integer, List<Integer>> getBattleResult() {
		return battleResult;
	}

	public void setBattleResult(Map<Integer, List<Integer>> battleResult) {
		this.battleResult = battleResult;
	}

	
}
