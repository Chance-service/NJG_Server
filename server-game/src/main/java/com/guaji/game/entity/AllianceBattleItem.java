package com.guaji.game.entity;

import java.util.Date;
import java.util.HashMap;
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

import org.guaji.config.ConfigManager;
import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.battle.BattleRole;
import com.guaji.game.config.AllianceBattleBuffCfg;
import com.guaji.game.config.AllianceBattleInspireCfg;
import com.guaji.game.manager.AllianceBattleManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.protocol.AllianceBattle.AllianceBattleState;
import com.guaji.game.protocol.AllianceBattle.FightGroup;
import com.guaji.game.protocol.Attribute.Attr;
import com.guaji.game.protocol.Const.attr;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsonUtil;

/**
 * 参与公会战的公会的简易对象,负责保存相关信息
 */
@Entity
@Table(name = "alliance_battle_item")
@SuppressWarnings("serial")
public class AllianceBattleItem extends DBEntity {
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id ;
	
	@Column(name = "allianceId", nullable = false)
	private int allianceId ;
	
	@Column(name = "stageId", nullable = false)
	private int stageId ;
	
	@Column(name = "vitality", nullable = false)
	private int vitality ;
	
	@Column(name = "allianceName")
	private String allianceName ;
	
	@Column(name = "captainName")
	private String captainName ;
	
	@Column(name = "allianceLevel")
	private int allianceLevel ;
	
	// 挂载公会信息用作排序 - 经验
	@Transient
	private int allianceExp ;
	
	@Column(name = "battleResult")
	private int battleResult ;
	
	@Column(name = "teamMapStr")
	private String teamMapStr;
	
	@Transient
	private List<Integer> memberList;
	
	@Column(name = "memberListStr")
	private String memberListStr;
	
	@Transient
	private Map<Integer, List<Integer>> teamMap ;

	@Transient
	private Map<Integer,InspireInfo> inspireInfoMap;
	
	@Column(name = "inspireInfoMapStr")
	private String inspireInfoMapStr;
	
	/**
	 * 连胜次数
	 */
	@Column(name = "buffId")
	private int buffId = 0;
	
	@Column(name = "createTime", nullable = false)
	protected Date createTime = GuaJiTime.getCalendar().getTime();
	
	@Column(name = "updateTime")
	protected Date updateTime;
	
	@Column(name = "invalid")
	protected boolean invalid;
	
	/**
	 * 是否进行过抽签
	 */
	@Column(name = "hasDraw")
	protected boolean hasDraw;
	
	public AllianceBattleItem(){
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.teamMap = new ConcurrentHashMap<Integer, List<Integer>>();
		this.memberList = new LinkedList<>();
		this.inspireInfoMap = new HashMap<>();
		this.inspireInfoMapStr = GsonUtil.getJsonInstance().toJson(this.inspireInfoMap);
	}
	
	public static class InspireInfo {
		
		private HashMap<Integer, Integer> inspireMap = new HashMap<>();
		
		public void addInspireTimes(int fightGroup) {
			if(this.inspireMap.containsKey(fightGroup) && this.inspireMap.get(fightGroup) != null) {
				this.inspireMap.put(fightGroup, this.inspireMap.get(fightGroup) + 1);
			}else{
				this.inspireMap.put(fightGroup, 1);
			}
		}

		public int getTotalInspireTimes() {
			int count = 0;
			for(Integer inspireTimes : this.inspireMap.values()) {
				count += inspireTimes;
			}
			return count;
		}

		public int getInspireTimes(int fightGroup) {
			if(this.inspireMap.get(fightGroup) != null) {
				return this.inspireMap.get(fightGroup);
			}
			return 0;
		}

		public int[] getAddition(int fightGroup) {
			int hpAdd = 0;
			int harmAdd = 0;
			int inpsireTimes = this.getInspireTimes(fightGroup);
			if(inpsireTimes > 0) {
				for(int i=1;i<=inpsireTimes;i++) {
					AllianceBattleInspireCfg inspireCfg = AllianceBattleInspireCfg.getInspireCfg(fightGroup, inpsireTimes);
					if(inspireCfg != null) {
						hpAdd += inspireCfg.getHpAdd();
						harmAdd += inspireCfg.getHarmAdd();
					}
				}
			}
			return new int[]{hpAdd,harmAdd};
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAllianceId() {
		return allianceId;
	}

	public void setAllianceId(int allianceId) {
		this.allianceId = allianceId;
	}

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public int getVitality() {
		return vitality;
	}

	public void setVitality(int vitality) {
		this.vitality = vitality;
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

	public void increaseVitality(int vitality) {
		this.vitality += vitality;
	}

	public int getBattleResult() {
		return battleResult;
	}

	public void setBattleResult(int battleResult) {
		this.battleResult = battleResult;
	}

	public int getAllianceLevel() {
		return allianceLevel;
	}

	public void setAllianceLevel(int allianceLevel) {
		this.allianceLevel = allianceLevel;
	}

	public int getAllianceExp() {
		return allianceExp;
	}

	public void setAllianceExp(int allianceExp) {
		this.allianceExp = allianceExp;
	}
	
	public void convertData() {
		if(createTime instanceof java.sql.Date) {
			this.createTime = new Date(createTime.getTime());
		}
		if(updateTime instanceof java.sql.Date) {
			this.updateTime = new Date(updateTime.getTime());
		}
		
		if(this.teamMapStr != null && !"".equals(teamMapStr) && !"null".equals(teamMapStr)) {
			this.teamMap = GsonUtil.getJsonInstance().fromJson(teamMapStr,new TypeToken<ConcurrentHashMap<Integer, LinkedList<Integer>>>() {}.getType());
		}
		
		if(this.memberListStr != null && !"".equals(memberListStr) && !"null".equals(memberListStr)) {
			this.memberList = GsonUtil.getJsonInstance().fromJson(memberListStr,new TypeToken<LinkedList<Integer>>() {}.getType());
		}
		
		if(this.inspireInfoMapStr != null && !"".equals(inspireInfoMapStr) && !"null".equals(inspireInfoMapStr)) {
			this.inspireInfoMap = GsonUtil.getJsonInstance().fromJson(inspireInfoMapStr,new TypeToken<HashMap<Integer, InspireInfo>>() {}.getType());
		}
	}

	public String getAllianceName() {
		return allianceName;
	}

	public void setAllianceName(String allianceName) {
		this.allianceName = allianceName;
	}

	public void addTeamMem(int playerId,int teamIndex) {
		List<Integer> teamPlayerList = this.teamMap.get(teamIndex);
		if(teamPlayerList == null) {
			teamPlayerList = new LinkedList<>();
			this.teamMap.put(teamIndex, teamPlayerList);
		}
		teamPlayerList.add(playerId);
		this.teamMapStr = GsonUtil.getJsonInstance().toJson(this.teamMap,new TypeToken<ConcurrentHashMap<Integer, LinkedList<Integer>>>() {}.getType());
	}
	
	public List<Integer> getTeamMemberIds(int teamIndex) {
		return this.teamMap.get(teamIndex);
	}
	
	public void upTeamMember(int teamIndex, int pos) {
		List<Integer> teamPlayerList = this.teamMap.get(teamIndex);
		if(teamPlayerList == null || pos >= teamPlayerList.size() || pos < 1) {
			return ;
		}
		int oldPId = teamPlayerList.get(pos - 1);
		int pId = teamPlayerList.get(pos);
		teamPlayerList.set(pos-1, pId);
		teamPlayerList.set(pos, oldPId);
	}
	
	public void downTeamMember(int teamIndex, int pos) {
		List<Integer> teamPlayerList = this.teamMap.get(teamIndex);
		if(teamPlayerList == null || pos >= (teamPlayerList.size() - 1)) {
			return ;
		}
		int oldPId = teamPlayerList.get(pos + 1);
		int pId = teamPlayerList.get(pos);
		teamPlayerList.set(pos + 1, pId);
		teamPlayerList.set(pos, oldPId);
	}

	/**
	 * 获得玩家在哪个Team
	 * @param playerId
	 * @return
	 */
	public int getMemberTeamIndex(int playerId) {
		for(int teamIndex : GsConst.AllianceBattle.ALL_TEAM) {
			List<Integer> teamMemList = this.teamMap.get(teamIndex);
			if(teamMemList != null && teamMemList.contains(playerId)) {
				return teamIndex;
			}
		}
		return 0;
	}

	/**
	 * 获得每个team的最大人数
	 * @return
	 */
	public int getTeamMemberMaxSize() {
		int allMemberSize = this.getMemberList().size();
		int teamSize = GsConst.AllianceBattle.ALL_TEAM.length;
		if(allMemberSize % teamSize == 0) {
			return allMemberSize / teamSize;
		}else{
			return allMemberSize / teamSize + 1;
		}
	}

	/**
	 * 战场移除一个玩家
	 * @param teamIndex
	 * @param memberId
	 */
	public void removeMem(int teamIndex, int memberId) {
		List<Integer> existMemberList = getTeamMemberIds(teamIndex);
		if(existMemberList != null) {
			existMemberList.remove(Integer.valueOf(memberId));
		}
		this.teamMapStr = GsonUtil.getJsonInstance().toJson(this.teamMap,new TypeToken<ConcurrentHashMap<Integer, LinkedList<Integer>>>() {}.getType());
	}
	
	public void removeMem(int memberId) {
		this.memberList.remove(Integer.valueOf(memberId));
		this.memberListStr = GsonUtil.getJsonInstance().toJson(memberList);
		for(int teamIndex : GsConst.AllianceBattle.ALL_TEAM) {
			removeMem(teamIndex,memberId);
		}
	}
	
	/**
	 * 重设TeamMap
	 * @param teamMap
	 */
	public void resetTeamMap(Map<Integer, List<Integer>> teamMap) {
		this.teamMap = teamMap;
		this.teamMapStr = GsonUtil.getJsonInstance().toJson(this.teamMap,new TypeToken<ConcurrentHashMap<Integer, LinkedList<Integer>>>() {}.getType());
	}

	/**
	 * 获得战场作战部队
	 * @param teamIndex
	 * @return
	 */
	public List<BattleRole> getTeamBattleRoleList(int teamIndex, int fightGroup) {
		List<BattleRole> teamBattleRoles = new LinkedList<BattleRole>();
		List<Integer> playerIds = getTeamMemberIds(teamIndex);
		if(playerIds == null) {
			return teamBattleRoles;
		}
		for(int playerId : playerIds) {
			PlayerSnapshotInfo.Builder snapshot = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
			if(snapshot != null) {				
				RoleInfo.Builder snapshotRole = snapshot.getMainRoleInfoBuilder().clone();
				// 添加鼓舞信息
				InspireInfo inspireInfo = this.getInspireInfo(playerId);
				if(inspireInfo != null) {
					int[] add = inspireInfo.getAddition(fightGroup);
					if(add[0] > 0) {
						Attr.Builder orginHpAttr = GameUtil.getAttrBuilder(snapshotRole, attr.HP);
						if(orginHpAttr != null) {
							orginHpAttr.setAttrValue((int)(orginHpAttr.getAttrValue() * ((float)add[0] / 10000 + 1)));
						}
					}
					if(add[1] > 0) {
						Attr.Builder orginMinHarmAttr = GameUtil.getAttrBuilder(snapshotRole, attr.MINDMG);
						if(orginMinHarmAttr != null) {
							orginMinHarmAttr.setAttrValue((int)(orginMinHarmAttr.getAttrValue() * ((float)add[1] / 10000 + 1)));
						}
						Attr.Builder orginMaxHarmAttr = GameUtil.getAttrBuilder(snapshotRole, attr.MAXDMG);
						if(orginMaxHarmAttr != null) {
							orginMaxHarmAttr.setAttrValue((int)(orginMaxHarmAttr.getAttrValue() * ((float)add[1] / 10000 + 1)));
						}
					}
				}
				
				if(this.buffId > 0) {
					AllianceBattleBuffCfg allianceBattleBuffCfg = ConfigManager.getInstance().getConfigByKey(AllianceBattleBuffCfg.class, this.buffId);
					if(allianceBattleBuffCfg != null) {
						Attr.Builder orginArmorAttr = GameUtil.getAttrBuilder(snapshotRole, attr.PHYDEF);
						if(orginArmorAttr != null) {
							orginArmorAttr.setAttrValue((int)(orginArmorAttr.getAttrValue() * ((float)allianceBattleBuffCfg.getPhyDefAdd() / 10000 + 1)));
						}
						
						Attr.Builder orginMagicDefAttr = GameUtil.getAttrBuilder(snapshotRole, attr.MAGDEF);
						if(orginMagicDefAttr != null) {
							orginMagicDefAttr.setAttrValue((int)(orginMagicDefAttr.getAttrValue() * ((float)allianceBattleBuffCfg.getMagicDefAdd() / 10000 + 1)));
						}
					}
				}
				
				BattleRole battleRole = new BattleRole(playerId, snapshotRole);
				battleRole.setFlag(snapshot.getAllianceInfo().getPostion());
				teamBattleRoles.add(battleRole);
			}
		}
		return teamBattleRoles;
	}

	public int getTeamMemberSize(int teamIndex) {
		if(this.teamMap.containsKey(teamIndex)) {
			return this.teamMap.get(teamIndex).size();
		}
		return 0;
	}

	/**
	 * 自动分配队伍
	 */
	public void autoJoinTeam() {
		int maxTeamMemSize = getTeamMemberMaxSize();
		List<Integer> leftMemberIds = new LinkedList<>();
		leftMemberIds.addAll(getMemberList());
		
		for(int teamIndex : GsConst.AllianceBattle.ALL_TEAM) {
			List<Integer> memberList = this.teamMap.get(teamIndex);
			if(memberList != null) {
				leftMemberIds.removeAll(memberList);
			}
		}
		
		for(int teamIndex : GsConst.AllianceBattle.ALL_TEAM) {
			List<Integer> memberList = this.teamMap.get(teamIndex);
			if(memberList == null) {
				memberList = new LinkedList<>();
				this.teamMap.put(teamIndex, memberList);
			}
			int mSize = memberList.size();
			for(int i =0;i < maxTeamMemSize - mSize;i++) {
				if(leftMemberIds.size() > 0) {
					memberList.add(leftMemberIds.remove(0));
				}
			}
		}
		
		this.teamMapStr = GsonUtil.getJsonInstance().toJson(this.teamMap,new TypeToken<ConcurrentHashMap<Integer, LinkedList<Integer>>>() {}.getType());
		this.notifyUpdate(true);
	}
	
	public static AllianceBattleItem valueOf(AllianceEntity allianceEntity) {
		AllianceBattleItem battleItem = new AllianceBattleItem();
		battleItem.allianceId = allianceEntity.getId();
		battleItem.allianceLevel = allianceEntity.getLevel();
		battleItem.allianceExp= allianceEntity.getExp();
		battleItem.allianceName = allianceEntity.getName();
		battleItem.captainName = allianceEntity.getPlayerName();
		battleItem.resetTeamMap(new HashMap<Integer, List<Integer>>());
		List<Integer> list = new LinkedList<>();
		list.addAll(allianceEntity.getMemberList());
		battleItem.setMemberList(list);
		
		int[] chanpionInfo = AllianceBattleManager.getInstance().getLastChampionInfo();
		if(chanpionInfo.length == 2 && chanpionInfo[0] == allianceEntity.getId()) {
			battleItem.setBuffId(chanpionInfo[1] > GsConst.AllianceBattle.STREAK_BUFF_ID_5 ? GsConst.AllianceBattle.STREAK_BUFF_ID_5 : chanpionInfo[1]);
		}
		
		return battleItem;
	}

	public List<Integer> getMemberList() {
		return memberList;
	}

	public void setMemberList(List<Integer> memberList) {
		this.memberList = memberList;
		this.memberListStr = GsonUtil.getJsonInstance().toJson(memberList);
	}

	public String getMemberListStr() {
		return memberListStr;
	}

	public void setMemberListStr(String memberListStr) {
		this.memberListStr = memberListStr;
	}

	public void addMem(int memberId) {
		if(this.memberList.contains(memberId)) {
			return ;
		}
		this.memberList.add(memberId);
		this.memberListStr = GsonUtil.getJsonInstance().toJson(memberList);
	}

	public String getCaptainName() {
		return captainName;
	}

	public void setCaptainName(String captainName) {
		this.captainName = captainName;
	}
	
	public void inpsire(int playerId, int fightGroup) {
		InspireInfo inspireInfo = this.inspireInfoMap.get(playerId);
		if(inspireInfo == null) {
			inspireInfo = new InspireInfo();
			this.inspireInfoMap.put(playerId, inspireInfo);
		}
		
		inspireInfo.addInspireTimes(fightGroup);
		this.inspireInfoMapStr = GsonUtil.getJsonInstance().toJson(inspireInfoMap);
	}
	
	public int getTotalInspireTimes(int playerId) {
		InspireInfo inspireInfo = this.inspireInfoMap.get(playerId);
		if(inspireInfo != null) {
			return inspireInfo.getTotalInspireTimes();
		}
		return 0;
	}
	
	public int getInspireTimes(int playerId, int fightGroup) {
		InspireInfo inspireInfo = this.inspireInfoMap.get(playerId);
		if(inspireInfo != null) {
			return inspireInfo.getInspireTimes(fightGroup);
		}
		return 0;
	}
	
	public int getInspireTimes(int playerId, AllianceBattleState battleState) {
		if(battleState == AllianceBattleState.Publicity_WAIT) {
			return getInspireTimes(playerId, FightGroup.GROUP_32_VALUE);
		} else if(battleState == AllianceBattleState.FS16_8_WAIT ) {
			return getInspireTimes(playerId, FightGroup.GROUP_16_VALUE);
		} else if(battleState == AllianceBattleState.FS8_4_WAIT ) {
			return getInspireTimes(playerId, FightGroup.GROUP_8_VALUE);
		} else if(battleState == AllianceBattleState.FS4_2_WAIT ) {
			return getInspireTimes(playerId, FightGroup.GROUP_4_VALUE);
		} else if(battleState == AllianceBattleState.FS2_1_WAIT ) {
			return getInspireTimes(playerId, FightGroup.GROUP_2_VALUE);
		}
		return -1;
	}

	public int getInspireAddition(Integer playerId, int fightGroup) {
		int inspireTimes = getInspireTimes(playerId, fightGroup) ;
		if(inspireTimes > 0) {
			for(int i=1;i <= inspireTimes;i++) {
				
			}
		}
		return 0;
	}

	private InspireInfo getInspireInfo(int playerId) {
		return this.inspireInfoMap.get(playerId);
	}

	public int getBuffId() {
		return this.buffId;
	}

	public void setBuffId(int buffId) {
		this.buffId = buffId;
	}

	public boolean getHasDraw() {
		return hasDraw;
	}

	public void setHasDraw(boolean hasDraw) {
		this.hasDraw = hasDraw;
	}
	
	
}

