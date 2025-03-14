package com.guaji.game.manager;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.guaji.game.entity.AllianceBattleItem;
import com.guaji.game.entity.AllianceFightUnit;
import com.guaji.game.entity.AllianceFightVersus;
import com.guaji.game.protocol.AllianceBattle.FightGroup;

/**
 * 每一期的工会战数据封装
 * @author xulinqs
 *
 */
public class AllianceBattleAgainstInfo {

	private Map<Integer, List<AllianceFightVersus>> againstMap ;
	
	private Map<Integer, AllianceBattleItem> battleItemMap ;
	
	private int stageId = 0;
	
	public AllianceBattleAgainstInfo(int stageId){
		this.setStageId(stageId);
		setAgainstMap(new ConcurrentHashMap<Integer, List<AllianceFightVersus>>());
		// 预先把对阵表的Map创建好
		this.getAgainstMap().put(FightGroup.GROUP_32_VALUE, new LinkedList<AllianceFightVersus>());
		this.getAgainstMap().put(FightGroup.GROUP_16_VALUE, new LinkedList<AllianceFightVersus>());
		this.getAgainstMap().put(FightGroup.GROUP_8_VALUE, new LinkedList<AllianceFightVersus>());
		this.getAgainstMap().put(FightGroup.GROUP_4_VALUE, new LinkedList<AllianceFightVersus>());
		this.getAgainstMap().put(FightGroup.GROUP_2_VALUE, new LinkedList<AllianceFightVersus>());
		
		this.battleItemMap = new ConcurrentHashMap<Integer, AllianceBattleItem>();
	}
	
	public void addBattleItems(Collection<AllianceBattleItem> items) {
		for(AllianceBattleItem item : items) {
			this.battleItemMap.put(item.getAllianceId(), item);
		}
	}
	
	public void addBattleItem(AllianceBattleItem item) {
		this.battleItemMap.put(item.getAllianceId(), item);
	}
	
	/**
	 * 塞入对阵表
	 * 
	 * @param fightVersus
	 */
	public void addFightVersus(AllianceFightVersus fightVersus) {
		List<AllianceFightVersus> fightVersusList = this.getAgainstMap().get(fightVersus.getFightGroup());
		if (fightVersusList == null) {
			fightVersusList = new LinkedList<>();
			this.getAgainstMap().put(fightVersus.getFightGroup(), fightVersusList);
		}
		fightVersusList.add(fightVersus);
	}
	
	/**
	 * 获取某一场对阵信息
	 * @param versusId
	 * @return
	 */
	public AllianceFightVersus getAllianceFightVersus(int versusId) {
		for(List<AllianceFightVersus> versusList : this.getAgainstMap().values()) {
			for(AllianceFightVersus versus : versusList) {
				if(versus.getId() == versusId) {
					return versus;
				}
			}
		}
		return null;
	}
	
	/**
	 * 获取某一轮对阵信息
	 * @param versusId
	 * @return
	 */
	public List<AllianceFightVersus> getAllianceFightVersusByGroup(int fightGroup) {
		return this.againstMap.get(fightGroup);
	}
	
	
	/**
	 * 获得战斗小单元
	 * @param battleId
	 * @return
	 */
	public AllianceFightUnit getAllianceFightUnit(int battleId) {
		for(List<AllianceFightVersus> versusList : this.getAgainstMap().values()) {
			for(AllianceFightVersus versus : versusList){
				for(AllianceFightUnit fightUnit : versus.getFightUnits()) {
					if(fightUnit.getId() == battleId) {
						return fightUnit;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 根据fightGroup获得这个阶段参与的所有的AllianceBattleItem
	 * @param group
	 */
	public List<AllianceBattleItem> getAllianceBattleItemsByGroup(FightGroup group) {
		List<AllianceBattleItem> battleItems = new LinkedList<>();
		List<AllianceFightVersus> versusList = this.getAgainstMap().get(group.getNumber());
		if(versusList != null) {
			for(AllianceFightVersus fightVersus : versusList) {
				battleItems.add(getBattleItem(fightVersus.getLeftId()));
				battleItems.add(getBattleItem(fightVersus.getRightId()));
			}
		}
		return battleItems;
	}
	
	/**
	 * 根据fightGroup获得这个阶段参与的所有的AllianceFightVersus
	 * @param group
	 */
	public List<AllianceFightVersus> getFightVersusByGroup(FightGroup group) {
		return this.getAgainstMap().get(group.getNumber());
	}
	
	public AllianceBattleItem getBattleItem(int allianceId) {
		return this.battleItemMap.get(allianceId);
	}

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public void addVersusList(int group, List<AllianceFightVersus> fightVersus) {
		this.getAgainstMap().put(group, fightVersus);
	}

	public Map<Integer, List<AllianceFightVersus>> getAgainstMap() {
		return againstMap;
	}

	public void setAgainstMap(Map<Integer, List<AllianceFightVersus>> againstMap) {
		this.againstMap = againstMap;
	}
	
	public AllianceFightVersus getAllianceFightVersus(int fightGroup, int allianceId) {
		List<AllianceFightVersus> versusList = this.againstMap.get(fightGroup);
		if(versusList != null) {
			for(AllianceFightVersus versus : versusList){
				if(versus.getFightGroup() == fightGroup && (versus.getLeftId() == allianceId || versus.getRightId() == allianceId)) {
					return versus;
				}
			}
		}
		return null;
	}
	
	public boolean isDataEmpty(){
		for(List<AllianceFightVersus> value : this.againstMap.values()) {
			if(value.size() > 0) {
				return false;
			}
		}
		return true;
	}

	public boolean isAllianceBattle(int allianceId) {
		List<AllianceFightVersus> versusList = this.againstMap.get(FightGroup.GROUP_32_VALUE);
		if(versusList != null) {
			for(AllianceFightVersus versus : versusList) {
				if(versus.getLeftId() == allianceId || versus.getRightId() == allianceId) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public int getFightResult(int allianceId) {
		AllianceBattleItem allianceBattleItem = this.getBattleItem(allianceId);
		if(allianceBattleItem != null) {
			return allianceBattleItem.getBattleResult();
		}
		return 0;
	}

	public AllianceFightVersus getAllianceFailFightVersus(int allianceId) {
		for(int i = FightGroup.GROUP_2_VALUE;i >= FightGroup.GROUP_32_VALUE;i--) {
			List<AllianceFightVersus> versusList = this.againstMap.get(i);
			if(versusList != null) {
				for(AllianceFightVersus versus : versusList){
					if(allianceId == versus.getFailId()) {
						return versus;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 获得冠军帮会Id
	 * @return
	 */
	public int getChampionAllianceId() {
		List<AllianceFightVersus> versusList = this.againstMap.get(FightGroup.GROUP_2_VALUE);
		if(versusList != null && versusList.size() > 0) {
			return versusList.get(0).getWinId();
		}
		return 0;
	}
}
