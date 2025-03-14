package com.guaji.game.battle;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guaji.db.DBManager;
import org.guaji.log.Log;

import com.guaji.game.battle.skill.SkillType;
import com.guaji.game.config.NewMapCfg;
import com.guaji.game.entity.CheatCheckEntity;
import com.guaji.game.entity.GuildBuffEntity;
import com.guaji.game.protocol.Battle.BattleInfo;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.util.GsConst;

import net.sf.json.JSONObject;

public class Battlefield {
	/**
	 * 战斗类型
	 */
	protected int battleType;
	/**
	 * 戰場ID
	 */	
	protected int battleId;
	/**
	 * 地圖ID
	 */	
	protected int mapId;
	/**
	 * 进攻方
	 */
	protected List<Integer> attackers;
	/**
	 * 防守方
	 */
	protected List<Integer> defenders;
	/**
	 * 防守方ID
	 */
	protected int defendId;
	
	/**
	 * 進攻方戰力
	 */
	protected int attackPower;
	/**
	 * 防守方戰力
	 */
	protected int defenPower;
	
	/**
	 * 进攻方
	 */
	protected Map<Integer,NewBattleRole> m_attackers;
	/**
	 * 防守方
	 */
	protected Map<Integer,NewBattleRole> m_defenders;
	/**
	 * 进攻方应援
	 */
//	protected List<BattleRole> attackAssistance;

	/**
	 * 防守方应援
	 */
//	protected List<BattleRole> defendAssistance;

	/**
	 * 可选目标
	 */
	//protected List<NewBattleRole> targetRoles;
	/**
	 * 战斗结果信息
	 */
	protected BattleInfo.Builder battleInfo;
	/**
	 * 参照角色数量
	 */
	protected int battleRoleCount;
	/**
	 * 当前轮数
	 */
	protected int battleRound;
	/**
	 * 最大战斗轮数
	 */
	protected int maxBattleRound;
	/**
	 * 是否發了獎品
	 */
	protected boolean battleFinish;
	/**
	 * 战斗是否準備好
	 */
	protected boolean battleStandby;
	/**
	 * 当前结果
	 */
	protected int battleResult;
	/**
	 * client 演出結果
	 */
	protected int clientRet;
	/**
	 * 戰鬥歷時
	 */
	protected int battleTime;
	/**
	 * logy總數量
	 */
	protected int totalLogId;
	/**
	 * 現在第幾份Id
	 */
	protected int nowLogId;

	/**
	 * 須重試
	 */
	protected boolean retest;
	/**
	 * 是否為怪物
	 */
	protected boolean isMonster;
	/**
	 * 戰場是否已付費
	 */
	protected boolean cost;


	public Battlefield() {
		battleType = 0;
		battleId = 0;
		mapId = 0;
		m_attackers = new HashMap<>();
		m_defenders = new HashMap<>();
		battleInfo = null;
		battleRoleCount = 0;
		battleRound = 0;
		battleResult = 0;
		battleTime = 0;
		maxBattleRound = 0;
		totalLogId = 0;
		nowLogId = 0;
		defendId = 0;
		battleStandby = false;
		battleFinish = false;
		retest = false;
		
		attackers = new ArrayList<>();
		defenders = new ArrayList<>();
		
		attackPower = 0;
		defenPower = 0;
		isMonster = false;
		cost = false;
	}
	
	public void init() {

	}
	
	public int getBattleTime() {
		return battleTime;
	}
	
	public int gettotalLogId(){
		return totalLogId;
	}
	
	public void settotalLogId(int total){
		totalLogId = total;
	}
	
	public int getMapId(){
		return mapId;
	}
	
	public void setMapId(int mapid){
		mapId = mapid;
	}
	
	public int getnowLogId(){
		return nowLogId;
	}
	
	public void setnowLogId(int nowId){
		nowLogId = nowId;
	}

	public int getBattleType() {
		return battleType;
	}
	
	public void setBattleType(int atype) {
		this.battleType = atype;
	}
	
	public void setBattleId(int id) {
		this.battleId = id;
	}
	
	public int getBattleId() {
		return battleId;
	}
	
	public boolean getBattleStandby() {
		return battleStandby;
	}
	
	public void setBattleStandby(boolean swit) {
		this.battleStandby = swit;
	}
	
	public boolean getBattleFinish() {
		return battleFinish;
	}
	
	public void setBattleFinish(boolean swit) {
		battleFinish = swit ;
	}

	public int getBattleRoleCount() {
		return battleRoleCount;
	}

	public int getBattleRound() {
		return battleRound;
	}
	
	public int getclientRet() {
		return clientRet;
	}
	
	public void setclientRet(int ret) {
		this.clientRet = ret;
	}
	
	public boolean IsRetest() {
		return retest;
	}
	
	public void setRetest(boolean test) {
		this.retest = test;
	}
	
	
	
//	public List<NewBattleRole> getAttackers() {
//		return attackers;
//	}
//
//	public List<NewBattleRole> getDefenders() {
//		return defenders;
//	}
	
//	public void setAttackers(List<NewBattleRole> rolelist) {
//		this.attackers = rolelist;
//	}
//
//	public void setDefenders(List<NewBattleRole> rolelist) {
//		this.defenders = rolelist;
//	}

	public int getDefendId() {
		return defendId;
	}

	public void setDefendId(int defendId) {
		this.defendId = defendId;
	}

	public List<Integer> getAttackers() {
		return attackers;
	}

	public void setAttackers(List<Integer> attackers) {
		this.attackers = attackers;
	}

	public List<Integer> getDefenders() {
		return defenders;
	}

	public void setDefenders(List<Integer> defenders) {
		this.defenders = defenders;
	}

	public int getAttackPower() {
		return attackPower;
	}

	public void setAttackPower(int attackPower) {
		this.attackPower = attackPower;
	}

	public int getDefenPower() {
		return defenPower;
	}

	public void setDefenPower(int defenPower) {
		this.defenPower = defenPower;
	}

	public boolean isMonster() {
		return isMonster;
	}

	public void setMonster(boolean isMonster) {
		this.isMonster = isMonster;
	}
	
	public boolean isCost() {
		return cost;
	}

	public void setCost(boolean cost) {
		this.cost = cost;
	}

	public Map<Integer,NewBattleRole> getm_Attackers() {
		return m_attackers;
	}

	public Map<Integer,NewBattleRole> getm_Defenders() {
		return m_defenders;
	}
	
	public void setm_Attackers(Map<Integer,NewBattleRole> rolemap) {
		this.m_attackers = rolemap;
		
		for (Integer Pos :m_attackers.keySet()) {
			m_attackers.get(Pos).setfirendList(rolemap);
		}
		
		for (Integer Pos :m_defenders.keySet()) {
			m_defenders.get(Pos).setenemyList(rolemap);
		}
	}

	public void setm_Defenders(Map<Integer,NewBattleRole> rolemap) {
		this.m_defenders = rolemap;
		
		for (Integer Pos :m_defenders.keySet()) {
			m_defenders.get(Pos).setfirendList(rolemap);
		}
		
		for (Integer Pos :m_attackers.keySet()) {
			m_attackers.get(Pos).setenemyList(rolemap);
		}
		
	}
	
	public BattleInfo.Builder getBattleInfo() {
		return battleInfo;
	}

	public int getBattleResult() {
		return battleResult;
	}

	
	public boolean checkPos(int pos) {
		if ((m_attackers.containsKey(pos))&&(m_defenders.containsKey(pos))) {
			return false;
		}
		if (m_attackers.containsKey(pos)) {
			return true;
		}
		if (m_defenders.containsKey(pos)) {
			return true;
		}
		return false;
	}
	/**
	 * 抓取位置戰鬥角色
	 */	
	public NewBattleRole getPosBattleRole(int pos) {
		NewBattleRole ActRole = null;
		if ((m_attackers.containsKey(pos))) {
			ActRole = m_attackers.get(pos);
		} else if (m_defenders.containsKey(pos)) {
			ActRole = m_defenders.get(pos);
		}
		return ActRole;
	}
	/**
	 * 是否為敵對
	 */
	public boolean IsEnemy(int pos1 ,int pos2) {
		if (m_attackers.containsKey(pos1) && (m_attackers.containsKey(pos2))) {
			return false;
		}
		if (m_defenders.containsKey(pos1) && (m_defenders.containsKey(pos2))) {
			return false;
		}
		return true;
	}
	/**
	 * 檢查受擊回復MP
	 */
	public boolean checkHurtGetMP(NewBattleRole targetRole,NewBattleRoleInfo targetRoleInfo) {
		if (targetRole.getState() == GsConst.PersonState.PERSON_DEAD) {
			return true;
		}
		int newmp = targetRoleInfo.getNewMp();
		int chkAddmp = newmp - targetRoleInfo.getNowMp();
		int targetDEFMP = targetRole.getDEFMP();
		return (chkAddmp <= targetDEFMP);
	}
	
	/**
	 * 檢查攻擊獲得MP
	 */
	public boolean checkATKGetMP(NewBattleRole ActRole,NewBattleRole targetRole,NewBattleRoleInfo ActionInfo) {
		int act = ActionInfo.getAction();
		int skilladd = 0;
		double doubletans = 0.0;
		
		List<Double> params = null;
		
		if (act == GsConst.AttackType.NomarlATK) {
			if (ActRole.Istrigger(GsConst.TriggerType.SKILL,SkillType.Passive_ID_10002)) {
				params = SkillUtil.getParams(SkillType.Passive_ID_10002);
			}
			if (params != null) {
				doubletans = params.get(0);
				skilladd = (int)doubletans;
			}
			params = null;
			if (ActRole.Istrigger(GsConst.TriggerType.SKILL,SkillType.Passive_ID_10007)){
				params = SkillUtil.getParams(SkillType.Passive_ID_10007);
			}
			if (params != null) {
				doubletans = params.get(0);
				skilladd = (int)doubletans + skilladd;
			}
		}
				
		int buffmp = Double.valueOf(BuffUtil.checkMpGainValue(ActRole)).intValue();
		int newmp = ActionInfo.getNewMp();
		int chkAddmp = newmp - ActionInfo.getNowMp();
		int Svratkmp = BattleUtil.calClassMp(ActRole,targetRole)+skilladd+buffmp;
		if (chkAddmp > Svratkmp) {
			Log.debugInfo("checkATKGetMP====chkAddmp: {},newmp: {},buffmp:{},skilladd:{},Svratkmp:{} ",chkAddmp,newmp,buffmp,skilladd,Svratkmp);
		}
		return  (chkAddmp <= Svratkmp);	
	}
	
	/**
	 * 檢查技能攻擊獲得MP
	 */
	public int checkSKLGetMP(NewBattleRole ActRole,NewBattleRole targetRole,NewBattleRoleInfo ActionInfo) {
		int act = ActionInfo.getAction();
		int skillId = ActionInfo.getSkillId();
		double doubletans = 0.0;
		int tskilladd = 0;
		int uskilladd = 0;
		
		int buffmp = 0;
		int newmp = ActionInfo.getNewMp();
		int chkAddmp = newmp - ActionInfo.getNowMp();
		int calmp = 0;//BattleUtil.calClassMp(ActRole,targetRole);
		List<Double> params = null;
		if (act == GsConst.AttackType.SkillACT) {
			if (ActRole.Istrigger(GsConst.TriggerType.SKILL,SkillType.Passive_ID_10002)) {
				params = SkillUtil.getParams(SkillType.Passive_ID_10002);

			}
			if (params != null) {
				doubletans = params.get(1);
				uskilladd = (int)doubletans;
			}
			params = null;
			if (ActRole.Istrigger(GsConst.TriggerType.SKILL,SkillType.Passive_ID_10007)) {
				params = SkillUtil.getParams(SkillType.Passive_ID_10007);
			}
			if (params != null) {
				doubletans = params.get(1);
				uskilladd = (int)doubletans+uskilladd;
			}
		}
				
		if (SkillUtil.getSkillCost(skillId) != 100) { // 小招才有計算給MP
			buffmp = 0;
			calmp = BattleUtil.calClassMp(ActRole,targetRole);
		}

		int Svratkmp = calmp+tskilladd+buffmp+uskilladd;

		Log.debugInfo("checkSKLGetMP====chkAddmp: {},Svratkmp:{} ,newmp: {},calmp,buffmp:{},tskilladd:{},uskilladd:{} ",chkAddmp,Svratkmp,newmp,calmp,buffmp,tskilladd,uskilladd);

		return Svratkmp;	
	}
	/**
	 * 檢查普攻受擊,並設定攻擊後HP，MP
	 */
	public boolean checkNomarlATK(NewBattleRole ActRole ,NewBattleRole targetRole,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo,int marktime) {
		//act : 1.NomarlATK
		//受擊狀態 1.一般攻擊 2.暴擊 3.miss
		int atkStatus = targetRoleInfo.getStatus();
		int newShield = targetRoleInfo.getNewShield();
		int newhp = targetRoleInfo.getNewHp();
		int chkHurt = BattleUtil.calLogGetHurt(targetRoleInfo.getNowHp(),targetRoleInfo.getNewHp(),targetRoleInfo.getNowShield(),targetRoleInfo.getNewShield());
		
		
		if (!ActRole.checkATKGroupId(ActionInfo.getSkillGroupId())){ // miss也算攻擊過了
			Log.debugInfo("ret:7 checkATKGroupId");
			return false;
		}
		
		if ((atkStatus  == 1) || (atkStatus  == 2)){
			ActRole.setATKFoucus(targetRole.getPos());
			boolean isCri = (atkStatus  == 2);
			
			if (!BattleUtil.checkDamage(ActRole,targetRole,targetRoleInfo,chkHurt,isCri,marktime)){
				Log.debugInfo("ret:7 checkDamage");
				return false;
			}
			
			// 受擊扣血量
			targetRole.setHp(newhp);
			targetRole.setShield(newShield);
			targetRole.checkBuffStack(GsConst.Buff.PETAL,false); // 開花
			
			// 檢查受擊恢復MP
			int newmp =  targetRoleInfo.getNewMp();
			if (!checkHurtGetMP(targetRole,targetRoleInfo)) {
				Log.debugInfo("ret:7 checkHurtGetMP");
				return false;
			}
			
			
			targetRole.setMp(newmp);
			
			// 檢查攻擊者獲得MP
			newmp = ActionInfo.getNewMp();
			
			if (!checkATKGetMP(ActRole,targetRole,ActionInfo)) {
				Log.debugInfo("ret:7 checkATKGetMP");
				return false;
			}
			
			ActRole.setMp(newmp);
			ActRole.checkBuffStack(GsConst.Buff.MOONLIGHT,true); // 月光
			ActRole.checkBuffStack(GsConst.Buff.RAGE,true); // 憤怒
			ActRole.checkBuffStack(GsConst.Buff.OUROBOROS,false); // 銜尾蛇
			ActRole.checkBuffStack(GsConst.Buff.MANA_OVERFLOW,false); // 魔力溢出
			
			return true;
		} else if (atkStatus  == 3) {
			//可紀錄連續miss被打

			return true;
		}
		return false;
	}
	/**
	 * 檢查技能作用
	 */
	public boolean checkSkillHandle(NewBattleRole ActRole ,NewBattleRole targetRole,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {
		//act : 2.SkillATK
		//受擊狀態 1.一般攻擊 2.暴擊 3.miss
		int atkStatus = targetRoleInfo.getStatus();
		int skillId = ActionInfo.getSkillId();
		int newShield = targetRoleInfo.getNewShield();
		int newhp = targetRoleInfo.getNewHp();
		int atkmp = 0;
		double doubletans = 0.0;
		int chkHurt = BattleUtil.calLogGetHurt(targetRoleInfo.getNowHp(),targetRoleInfo.getNewHp(),targetRoleInfo.getNowShield(),targetRoleInfo.getNewShield());
		boolean isHit = (chkHurt > 0) || (targetRole.Istrigger(GsConst.TriggerType.BUFF,GsConst.Buff.UNDEAD)); // 觸發攻擊
		
		ActRole.checkBuffStack(GsConst.Buff.MANA_OVERFLOW,false); // 魔力溢出

		if (!ActRole.checkSKLGropId(ActionInfo,targetRoleInfo)){ // miss也算攻擊過了
			Log.debugInfo("ret:8 checkSKLGropId");
			return false;
		}
		
		
		if ((atkStatus  == 1) || (atkStatus  == 2)){
			if (!SkillUtil.checkSkillEffect(this, ActRole, targetRole,ActionInfo,targetRoleInfo,skillId)){
				Log.debugInfo("ret:8 checkSkillEffect");
				return false;
			}
			// 更新目標血量
			targetRole.setHp(newhp);
			targetRole.setShield(newShield);
			if (isHit) {
				targetRole.checkBuffStack(GsConst.Buff.PETAL, false);
			}
			
			// 檢查受擊恢復MP
			int newmp =  targetRoleInfo.getNewMp();
			if ((isHit)||(targetRole.Istrigger(GsConst.TriggerType.BUFF,GsConst.Buff.UNDEAD))) {
				if (!checkHurtGetMP(targetRole,targetRoleInfo)) {
					Log.debugInfo("ret:8 checkHurtGetMP");
					return false;
				}
				targetRole.setMp(newmp);
			}
		
			// 紀錄攻擊者各個MP

			if (isHit) {
				atkmp = checkSKLGetMP(ActRole,targetRole,ActionInfo);
				ActRole.setSKLGetMp(targetRole.getPos(), atkmp);
			} else {
				if (ActRole.IsMyself(targetRole.getPos())) {
						if (skillId == SkillType.SKILL_ID_1) {
						List<Double> params = SkillUtil.getParams(SkillType.SKILL_ID_1);
						if (params != null) {
							doubletans = params.get(5);
							atkmp = (int)doubletans;
							ActRole.setSKLGetMp(targetRole.getPos(), atkmp);
						}
					}
				}
			}
			return true;
		} else if ((atkStatus  == 3)) {
			return true;
		}
		return false;
	}
	/**
	 * 檢查Buff作用
	 */
	public boolean checkBuffHandle(NewBattleRole ActRole ,NewBattleRole targetRole,NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {
		if (!ActRole.IsMyself(targetRole.getPos())) {
			Log.debugInfo("ret:9 checkBuffHandle: !IsMyself");
			return false;
		}
		int buffid = ActionInfo.getSkillId();
		
		if (!targetRole.checkBuffValid(buffid)) {
			Log.debugInfo("ret:9 checkBuffValid");
			return false;
		}
		
		int newShield = targetRoleInfo.getNewShield();
		int newhp = targetRoleInfo.getNewHp();
		int chkHurt = BattleUtil.calLogGetHurt(targetRoleInfo.getNowHp(),targetRoleInfo.getNewHp(),targetRoleInfo.getNowShield(),targetRoleInfo.getNewShield());
		int addhp = 0;
		if (chkHurt == 0) {
			addhp =  BattleUtil.calLogGetRecovery(targetRoleInfo.getNowHp(),targetRoleInfo.getNewHp());
		}
		
		if (buffid == GsConst.Buff.POWER) { // 權能
			int shield = targetRole.getShield();
			NewBuff abuff = targetRole.getBuff(buffid);
			int maxRecoverShield = (int)BattleUtil.round(BattleUtil.mul((double)targetRole.getMaxhp(),abuff.getArgs().get(2)),0);
			int recoverShield = (int)BattleUtil.round(BattleUtil.mul((double)maxRecoverShield,abuff.getArgs().get(4)),0);
			int calshield = BattleUtil.calLogGetShield(targetRoleInfo.getNowShield(),targetRoleInfo.getNewShield());
			if (shield == 0) {  // 直接重生護盾
				if (calshield != maxRecoverShield) {
					Log.debugInfo("ret:9 maxRecoverShield,shield:{}",shield);
					return false;
				}
			} else if  (shield > 0){ // 每秒小額回復
				if (calshield != recoverShield) {
					Log.debugInfo("ret:9 recoverShield,shield:{}",shield);
					return false;
				}
			}
			targetRole.setShield(newShield);
			return true;
		}
		
		if ((buffid == GsConst.Buff.PETAL) // 開花
				||(buffid == GsConst.Buff.OUROBOROS)) { //銜尾蛇
			targetRole.ResetBuffStack(buffid);
			return true;
		}
		
		// Handle dot Hurt
		if ((buffid == GsConst.Buff.EROSION) // 侵蝕
				||(buffid == GsConst.Buff.BURN)) { //燒傷
			if (!BattleUtil.checkDotDamage(ActRole,targetRole,buffid,chkHurt)) {
				Log.debugInfo("ret:9 checkDotDamage");
				return false;
			}
			// 更新目標血量
			targetRole.setHp(newhp);
			targetRole.setShield(newShield);
			return true;
		}
		if (buffid == GsConst.Buff.RECOVERY) {
			if (!BattleUtil.checkDotHealth(ActRole,targetRole,buffid,addhp)) {
				Log.debugInfo("ret:9 checkDotHealth");
				return false;
			}
			// 更新目標血量
			targetRole.setHp(newhp);
			//targetRole.setShield(newShield);
			return true;
		}
		
		return false;
	}
	/**
	 * 開始檢查戰鬥Log
	 */
	public boolean checkBattleTime(int marktime) {
		if (this.battleTime == 0) {
			this.battleTime = marktime;
			UpdateALLBuffTime(this.battleTime);
			return true;
		} else {
			if (marktime >= this.battleTime) {
				
				int countTime = marktime - battleTime;
				if (countTime > 90000) { //90秒
					 return false;
				}
				this.battleTime = marktime;
				UpdateALLBuffTime(this.battleTime);
			} else { // 時間有問題
				 return false;
			}
			return true;
		}
	}
	/**
	 * 更新場景內所有人Buff時間
	 */
	public void UpdateALLBuffTime(int markTime) {
		for (Map.Entry<Integer, NewBattleRole> entry : m_attackers.entrySet()) {
			entry.getValue().UdateBuffMarkTime(markTime);
		}
		for (Map.Entry<Integer, NewBattleRole> entry : m_defenders.entrySet()) {
			entry.getValue().UdateBuffMarkTime(markTime);
		}
	}

	/**
	 * 開始檢查戰鬥Log
	 */
	public int checkBattlelog(NewBattleRoleInfo ActionInfo ,List<NewBattleRoleInfo>targetInfo,int id , int marktime ) {
		int ret = 0;
		if ((m_attackers == null) || (m_defenders == null) || (m_attackers.size() <= 0) || (m_defenders.size() <= 0)) {
			 ret = 1;
			 return ret;
		}
				
		if(!checkBattleTime(marktime)) {
			 ret = 999;
			 return ret;
		}
		
		int pos = ActionInfo.getPosId();
		if (!checkPos(pos)) {
			ret = 2;
			return ret;
		}
		
		int act = ActionInfo.getAction();
			
		NewBattleRole ActRole = getPosBattleRole(pos);
		
		ActRole.setTriggerList(ActionInfo.getPassiveList(),marktime);
		ActRole.setSkilladdhp(0);
		if ((act != GsConst.AttackType.NomarlATK)&&(act != GsConst.AttackType.SkillACT)) { //有可能是死亡前丟出攻擊
			if (ActRole.getState() == GsConst.PersonState.PERSON_DEAD){
				ret = 444;
				return ret;
			}
		}
		
//		if (ActRole.getState() == Battle.personState.PERSON_DEAD_VALUE){
//			//ret = 444; 僅略過
//			logger.info("id: {},ret: {},marktime: {}, Action_NowHp: {},Act_Newhp:{},Act_NowMp: {},Act_NewMp :{},Act_NowShield:{},Act_nOwshield :{},Act_nEwshield :{},ActPos:{},ActType:{},ActItemId:{}",
//					id,444,marktime,ActionInfo.getNowHp(),ActionInfo.getNewHp(),ActionInfo.getNowMp(),ActionInfo.getNewMp(),ActionInfo.getNowShield(),ActionInfo.getNewShield(),pos,ActRole.getRoleType(),ActRole.getitemId());
//			return ret; 
//			
//		}

		
		int nowhp = ActionInfo.getNowHp();
		int nowmp = ActionInfo.getNowMp();
		int nowshield = ActionInfo.getNowShield();
		
		if (ActRole.getState() != GsConst.PersonState.PERSON_DEAD){		
			if ((ActRole.getHp() != nowhp) || (ActRole.getMp() != nowmp)|| (ActRole.getShield() != nowshield))  {
				ret = 3;
				Log.debugInfo("id: {},ret: {},marktime: {}, ActRole_getHp: {},nowhp:{},ActRole_getMp: {},nowmp :{},ActRole_getShield: {},nowshield :{},ActType:{},ActItemId:{}",
						id,ret,marktime,ActRole.getHp(),nowhp,ActRole.getMp(),nowmp,ActRole.getShield(),nowshield,ActRole.getRoleType(),ActRole.getitemId());
				return ret;
			}
		}
		
		Log.debugInfo("id: {},act: {},marktime:{},====================================================================",id,act,marktime);
		
		String logStr = "attacker:=";
		for (Integer aPos : m_attackers.keySet()) {
			logStr = logStr + String.format("Pos:{%d}==Buff:[%s]==HP:%d/%d;",aPos,m_attackers.get(aPos).getBuffList().toString(),m_attackers.get(aPos).getHp(),m_attackers.get(aPos).getMaxhp());
		}
		Log.debugInfo(logStr);
		logStr = "defenders:=";
		for (Integer aPos : m_defenders.keySet()) {
			logStr = logStr + String.format("Pos:{%d}==Buff:[%s]==HP:%d/%d;",aPos,m_defenders.get(aPos).getBuffList().toString(),m_defenders.get(aPos).getHp(),m_defenders.get(aPos).getMaxhp());
		}
		Log.debugInfo(logStr);
		if (!ActRole.HandleActStatus(ActionInfo,marktime)) { //檢查行動者狀態
			ret = 4;
			Log.debugInfo("id: {},ret: {},marktime: {}, ActATKTime: {},ATKSpeed:{},Act_Pos:{},ActType:{},ActItemId:{}",
					id,ret,marktime,ActRole.getATKTime(),ActRole.getATKSpeed(),ActRole.getPos(),ActRole.getRoleType(),ActRole.getitemId());
			return ret;
		}
				
		int targetPos = -1;
		NewBattleRole targetRole = null; //targetman
		//int status = 0; //1.一般攻擊 2.暴擊 3.miss
		int skilladdhp = 0;
		ActRole.clearSKLGetMp();
		for (NewBattleRoleInfo targetRoleInfo : targetInfo) {
			targetPos = targetRoleInfo.getPosId();
			if (!checkPos(targetPos)) {
				ret = 5;
				return ret;
			}

			nowhp = targetRoleInfo.getNowHp();
			nowmp = targetRoleInfo.getNowMp();
			nowshield = targetRoleInfo.getNowShield();
			targetRole = getPosBattleRole(targetPos);
			
			targetRole.setTriggerList(targetRoleInfo.getPassiveList(),marktime);
			
			if (targetRole.getState() == GsConst.PersonState.PERSON_DEAD) { // 已經死了不檢查了
				return ret;
			}
			
			if ((targetRole.getHp() != nowhp) || (targetRole.getMp() != nowmp)||(targetRole.getShield() != nowshield)) {
				ret = 6;
				Log.debugInfo("id: {},ret: {},marktime: {}, targetRole_getHp: {},nowhp:{},targetRole_getMp: {},nowmp :{},targetRole_getShield: {},nowshield :{},ActType:{},ActItemId:{}",
						id,ret,marktime,targetRole.getHp(),nowhp,targetRole.getMp(),nowmp,targetRole.getShield(),nowshield,targetRole.getRoleType(),targetRole.getitemId());
				return ret;
			}
			//status =  targetRoleInfo.getStatus(); //受擊狀態 1.一般攻擊 2.暴擊 3.miss
			if (act == GsConst.AttackType.NomarlATK) {
				if (!checkNomarlATK(ActRole,targetRole,ActionInfo,targetRoleInfo,marktime)) {
					ret = 7;
					Log.debugInfo("id: {},checkNomarlATK ret: {},marktime: {}, ActATKTime: {},ATKSpeed:{},ActType:{},ActItemId:{},tarType:{},tarItemId:{}",
							id,ret,marktime,ActRole.getATKTime(),ActRole.getATKSpeed(),ActRole.getRoleType(),ActRole.getitemId(),targetRole.getRoleType(),targetRole.getitemId());
					return ret;
				}
			}
			
			if (act == GsConst.AttackType.SkillACT) {
				int skillId = ActionInfo.getSkillId();
				if (!checkSkillHandle(ActRole,targetRole,ActionInfo,targetRoleInfo)) {
					ret = 8;
					Log.debugInfo("id: {},checkSkillHandle ret: {},marktime: {}, ActATKTime: {},ATKSpeed:{},SkillID:{},ActType:{},ActItemId:{},tarType:{},tarItemId:{}",
							id,ret,marktime,ActRole.getATKTime(),ActRole.getATKSpeed(),ActionInfo.getSkillId(),ActRole.getRoleType(),ActRole.getitemId(),targetRole.getRoleType(),targetRole.getitemId());
					return ret;
				}
				if (skillId == SkillType.SKILL_ID_3001) {
					if (!ActRole.IsMyself(targetPos)){
						if (ActRole.Istrigger(GsConst.TriggerType.SKILL,SkillType.Passive_ID_10003)) {
							skilladdhp = ActRole.getSkilladdhp();
						}
					}
				}
				
				if (skillId == SkillType.SKILL_ID_3101) {
					if (!ActRole.IsMyself(targetPos)){
						if (ActRole.Istrigger(GsConst.TriggerType.SKILL,SkillType.Passive_ID_10008)) {
							skilladdhp = ActRole.getSkilladdhp();
						}
					}
				}
				
				if (skillId == SkillType.SKILL_ID_1101) {
					if (!ActRole.IsMyself(targetPos)){
						skilladdhp = ActRole.getSkilladdhp();
					}
				}
			}
			
			if (act == GsConst.AttackType.Buff_DeBuff) {
				if (!checkBuffHandle(ActRole,targetRole,ActionInfo,targetRoleInfo)) {
					ret = 9;
					Log.debugInfo("id: {},checkBuffHandle ret: {},marktime: {}, ActATKTime: {},ATKSpeed:{},BuffID:{},ActType:{},ActItemId:{},tarType:{},tarItemId:{}",
							id,ret,marktime,ActRole.getATKTime(),ActRole.getATKSpeed(),ActionInfo.getSkillId(),ActRole.getRoleType(),ActRole.getitemId(),targetRole.getRoleType(),targetRole.getitemId());
					return ret;
				}
			}
		}
		
		if (act == GsConst.AttackType.SkillACT) { // 攻擊完總結才檢查
			int skillId = ActionInfo.getSkillId();
			
			if (skillId == SkillType.SKILL_ID_3001) { // 例外檢查3001補血
				if (ActRole.Istrigger(GsConst.TriggerType.SKILL,SkillType.Passive_ID_10003)){
					NewBattleRoleInfo targetRoleInfo = getTargetInfoByPos(targetInfo,ActRole.getPos()); //取自己log
					targetPos = targetRoleInfo.getPosId();
					targetRole = getPosBattleRole(targetPos);
					int addhp = BattleUtil.calLogGetRecovery(targetRoleInfo.getNowHp(),targetRoleInfo.getNewHp());
					if (addhp > skilladdhp) {
						ret = 10;
						Log.debugInfo("id: {},skilladdhp ret: {},marktime: {}, ActATKTime: {},ATKSpeed:{},BuffID:{},ActType:{},ActItemId:{},tarType:{},tarItemId:{},skilladdhp:{},addhp:{}",
								id,ret,marktime,ActRole.getATKTime(),ActRole.getATKSpeed(),ActionInfo.getSkillId(),ActRole.getRoleType(),ActRole.getitemId(),targetRole.getRoleType(),targetRole.getitemId(),skilladdhp,addhp);

						return ret;
					}
				}
			}
			if (skillId == SkillType.SKILL_ID_3101) { // 例外檢查3101補血
				if (ActRole.Istrigger(GsConst.TriggerType.SKILL,SkillType.Passive_ID_10008)){
					NewBattleRoleInfo targetRoleInfo = getTargetInfoByPos(targetInfo,ActRole.getPos()); //取自己log
					targetPos = targetRoleInfo.getPosId();
					targetRole = getPosBattleRole(targetPos);
					int addhp = BattleUtil.calLogGetRecovery(targetRoleInfo.getNowHp(),targetRoleInfo.getNewHp());
					if (addhp > skilladdhp) {
						ret = 10;
						Log.debugInfo("id: {},skilladdhp ret: {},marktime: {}, ActATKTime: {},ATKSpeed:{},BuffID:{},ActType:{},ActItemId:{},tarType:{},tarItemId:{},skilladdhp:{},addhp:{}",
								id,ret,marktime,ActRole.getATKTime(),ActRole.getATKSpeed(),ActionInfo.getSkillId(),ActRole.getRoleType(),ActRole.getitemId(),targetRole.getRoleType(),targetRole.getitemId(),skilladdhp,addhp);

						return ret;
					}
				}
			}
			if (skillId == SkillType.SKILL_ID_1101) { // 例外檢查1101補血
				NewBattleRoleInfo targetRoleInfo = getTargetInfoByPos(targetInfo,ActRole.getPos()); //取自己log
				targetPos = targetRoleInfo.getPosId();
				targetRole = getPosBattleRole(targetPos);
				int addhp = BattleUtil.calLogGetRecovery(targetRoleInfo.getNowHp(),targetRoleInfo.getNewHp());
				if (addhp > skilladdhp) {
					ret = 10;
					Log.debugInfo("id: {},skilladdhp ret: {},marktime: {}, ActATKTime: {},ATKSpeed:{},BuffID:{},ActType:{},ActItemId:{},tarType:{},tarItemId:{},skilladdhp:{},addhp:{}",
							id,ret,marktime,ActRole.getATKTime(),ActRole.getATKSpeed(),ActionInfo.getSkillId(),ActRole.getRoleType(),ActRole.getitemId(),targetRole.getRoleType(),targetRole.getitemId(),skilladdhp,addhp);

					return ret;
				}

			}
			int newmp =  ActionInfo.getNewMp();
			if (!ActRole.checkSKLGetMp(ActionInfo)){
				ret = 11;
				return ret;
			}
			ActRole.setMp(newmp);
		}

		return ret;
	}

	public BattleInfo.Builder fighting(int battleType, List<BattleRole> attackers, List<BattleRole> defenders,
			NewMapCfg mapCfg) {
		if (attackers == null || attackers.size() <= 0 || defenders == null || defenders.size() <= 0) {
			return null;
		}
		return battleInfo;
	}

	public BattleInfo.Builder fighting(int battleType, List<BattleRole> attackers, List<BattleRole> attackersAssistants,
			List<BattleRole> defenders, List<BattleRole> defendersAssistants, NewMapCfg mapCfg) {

		if (attackers == null || attackers.size() <= 0 || defenders == null || defenders.size() <= 0) {
			return null;
		}
		return battleInfo;

	}

	public BattleInfo.Builder eighteenprincesfighting(int battleType, List<BattleRole> attackers,
			List<BattleRole> attackersAssistants, List<BattleRole> defenders, List<BattleRole> defendersAssistants,
			NewMapCfg mapCfg) {

		// 目标方无将时直接胜利
		if (attackers == null || attackers.size() <= 0 || defenders == null) {
			return null;
		}

		return battleInfo;

	}
	
	/**
	 * 检测攻击方全部牺牲
	 *
	 * @return
	 */
	public boolean checkAttackerSacrifice() {
		for (Integer Pos : m_attackers.keySet()) {
			NewBattleRole battleRole = m_attackers.get(Pos);
			if (battleRole.isAlive()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 检测防守方全部牺牲
	 *
	 * @return
	 */
	public boolean checkDefenderSacrifice() {
		for (Integer Pos : m_defenders.keySet()) {
			NewBattleRole battleRole = m_defenders.get(Pos);
			if (battleRole.isAlive()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 取得WorldBoss戰場初始血量
	 */
	
	public int getBossInitHP() {
		for (Integer Pos : m_defenders.keySet()) {
			NewBattleRole battleRole = m_defenders.get(Pos);
			if (battleRole.getRoleType() == Const.roleType.WORLDBOSS_VALUE) {
				return battleRole.getBossInitHp();
			}
		}
		return 0;
	}
	/*
	 * 取得WorldBoss戰鬥完血量
	 */
	public int getWorldBossCurrHP() {
		for (Integer Pos : m_defenders.keySet()) {
			NewBattleRole battleRole = m_defenders.get(Pos);
			if (battleRole.getRoleType() == Const.roleType.WORLDBOSS_VALUE) {
				return battleRole.getHp();
			}
		}
		return 0;
	}
	/**
	 * 取得整場對Boss的傷害
	 */
	public int getWorldBossHurt() {
		int initHP = getBossInitHP();
		int CurrHP = getWorldBossCurrHP();
		if (initHP >= CurrHP) {
			return initHP - CurrHP;
		}
		return 0;
	}
	/**
	 * 取某角色目標Log
	 */
	public NewBattleRoleInfo getTargetInfoByPos(List<NewBattleRoleInfo>targetInfo,int Pos) {
		NewBattleRoleInfo targetRoleInfo = null;
		int targetPos = 0;
		for (NewBattleRoleInfo oneRoleInfo : targetInfo) {
			targetPos = oneRoleInfo.getPosId();
			if (targetPos ==  Pos) {
				targetRoleInfo = oneRoleInfo;
			    break;
			}
		}
		return targetRoleInfo;
	}
	
	public int checkBosslog(NewBattleRoleInfo ActionInfo ,List<NewBattleRoleInfo>targetInfo,int id , int marktime) {
		int ret = 0;
		if ((m_attackers == null) || (m_defenders == null) || (m_attackers.size() <= 0) || (m_defenders.size() <= 0)) {
			 ret = 1;
			 return ret;
		}
				
		if(!checkBattleTime(marktime)) {
			 ret = 999;
			 return ret;
		}
		
		int pos = ActionInfo.getPosId();
		if (!checkPos(pos)) {
			ret = 2;
			return ret;
		}
		
		//int act = ActionInfo.getAction();
		
		NewBattleRole ActRole = getPosBattleRole(pos);
		
		if (ActRole.getRoleType() == GsConst.RoleType.WORLDBOSS) {
			int nowhp = ActRole.getHp();
		}
		
		int targetPos = -1;
		NewBattleRole targetRole = null; //targetman
		//int status = 0; //1.一般攻擊 2.暴擊 3.miss
		ActRole.clearSKLGetMp();
		for (NewBattleRoleInfo targetRoleInfo : targetInfo) {
			targetPos = targetRoleInfo.getPosId();
			if (!checkPos(targetPos)) {
				ret = 5;
				return ret;
			}
			targetRole = getPosBattleRole(targetPos);
			if ((targetRole != null)&&(targetRole.getRoleType() == GsConst.RoleType.WORLDBOSS)) {
				int newhp = targetRoleInfo.getNewHp();
				targetRole.setHp(newhp);
			}
		}
		return ret;
	}
	
	public void RecordCheatWin(int playerId) {
		List<CheatCheckEntity> cheatcheckEntitys = DBManager.getInstance()
				.query("from CheatCheckEntity where playerId = ? and invalid = 0", playerId);
		CheatCheckEntity checkEntity = null;
		if ((cheatcheckEntitys != null) && (cheatcheckEntitys.size() > 0)) {
			checkEntity = cheatcheckEntitys.get(0);
			checkEntity.setTotal(checkEntity.getTotal()+1);
			checkEntity.setAttackPower(getAttackPower());
			checkEntity.setAttackList(getAttackers());
			checkEntity.setDefenId(getDefendId());
			checkEntity.setDefenList(getDefenders());
			checkEntity.setDefenPower(getDefenPower());
			checkEntity.setMonster(isMonster);
			checkEntity.notifyUpdate(true);
		} else {
			checkEntity = new CheatCheckEntity(playerId,1,getAttackPower());
			checkEntity.setAttackList(getAttackers());
			checkEntity.setDefenId(getDefendId());
			checkEntity.setDefenList(getDefenders());
			checkEntity.setDefenPower(getDefenPower());
			checkEntity.setMonster(isMonster);
			checkEntity.notifyCreate();
		}
	}
	
	public String getTapDBBattleTeam(boolean isattack) {
		
		Map<Integer,NewBattleRole> battleRoles =  isattack ? getm_Attackers() : getm_Defenders();

		Set<Integer> aset = battleRoles.keySet();
		Object[] arr=aset.toArray();
        Arrays.sort(arr);
        ArrayList<String> astrList = new ArrayList<>();
		for (Object key : arr) {
			int pos = (int)key;
			int itemId = battleRoles.get(pos).getitemId();
			int lv = battleRoles.get(pos).getLevel();
			int star = battleRoles.get(pos).getRoleInfo().getStarLevel();
			String heroStr = "id:"+String.valueOf(itemId)+";";
			heroStr = heroStr+"pos:"+ String.valueOf(pos)+";";
			heroStr = heroStr+"lv:"+ String.valueOf(lv)+";";
			heroStr = heroStr+"star:"+ String.valueOf(star);
			astrList.add(heroStr);
		}
		return astrList.toString();
	}
	
	
	
}
