package com.guaji.game.battle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guaji.config.ConfigManager;
import org.guaji.log.Log;

import com.guaji.game.attribute.Attribute;
import com.guaji.game.battle.skill.SkillType;
import com.guaji.game.config.BuffCfg;
import com.guaji.game.config.Hero_NGListCfg;
import com.guaji.game.config.NewHeroClassCfg;
import com.guaji.game.config.NewMonsterCfg;
import com.guaji.game.config.NewSkillCfg;
import com.guaji.game.protocol.Attribute.Attr;
import com.guaji.game.protocol.Attribute.AttrInfo;
import com.guaji.game.protocol.Battle.NewBattleLog.NewBattleRoleInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Player.RoleSkill;
import com.guaji.game.util.GsConst;

public class NewBattleRole {
	/**
	 * 玩家id
	 */
	private int playerId;
	/**
	 * 表格索引ID
	 */
	private int itemId;
	/**
	 * RoleType 1.Leader 2.Hero 3.Monster
	 */
	private int type;
	/**
	 * 占位位置
	 */
	private int pos;
	/**
	 * 等级
	 */
	private int level;
	/**
	 * 血量
	 */
	private int hp;
	/**
	 * 魔力
	 */
	private int mp;
	/**
	 *護盾 
	 */
	private int shield;
	
	/**
	 * 状态(正常, 逃跑, 死亡)
	 */
	private int state;
	/**
	 * 打出普攻時間(毫秒)
	 */
	private int ATKTime;
	/**
	 * 普攻ID流水,已連擊次數
	 */
	private Map<Integer,Integer> ATKGroupId ;
	/**
	 * 目前普攻鎖定對象Pos,攻擊次數
	 */
	private Map<Integer,Integer> FoucusPos ;
	/**
	 * 技能資訊<技能ID,<施放時間,技能流水,次數>>
	 */
	private Map<Integer,CheckSkillInfo> SKLGroupId;
	/**
	 * Buff資訊<BuffID,<施放時間,技能流水,次數>>
	 */
	private List<NewBuff> Buffs;
	/**
	 * 紀錄需判斷使用過技能ID;
	 */
	private Set<Integer> UsedSkillId;
	/**
	 * 被動作用技能ID
	 */
	private Set<Integer> PassiveSkill;
	/**
	 * 被動作用BuffID
	 */
	private Set<Integer> PassiveBuff;
	/**
	 * 友軍列表(包含自己)
	 */
	private Map<Integer,NewBattleRole> firendList;
	/**
	 * 敵軍列表
	 */
	private Map<Integer,NewBattleRole> enemyList;
	/**
	 * 角色信息
	 */
	private RoleInfo.Builder roleInfo;
	/**
	 * 角色職業信息
	 */
	private Hero_NGListCfg RoleCfg;
	/**
	 * 战斗属性数据
	 */
	private Attribute attribute;
	/**
	 * List<String>triggerList
	 */
	private List<String> triggerList;
	/**
	 * 動作時間
	 */
	private int markTime;
	/**
	 * 死亡時間
	 */
	private int deadTime;
	/**
	 * skill 3001,3101,1101例外施放補血量暫存
	 */
	private int skillAddhp;
	/**
	 * 技能攻擊獲得MP組 Pos,Mp
	 */
	private Map<Integer,Integer> SKLGetMp;
	/**
	 * Boss初始剩餘HP
	 */
	private int BossHp ;
	/**
	 * 战斗特殊标记Map
	 */
	//private Map<Integer, Object> specialFlagMap;
	
	public NewBattleRole(RoleInfo.Builder roleInfo,int pos,int BossHP) {
		reset();
		this.pos = pos;
		this.roleInfo = roleInfo.clone();
		this.level = roleInfo.getLevel();
		this.itemId = this.roleInfo.getItemId();
		this.type = this.roleInfo.getType();
		this.BossHp = BossHP;
		init();
	}
	
	public NewBattleRole(int playerId, RoleInfo.Builder roleInfo,int pos) {
		reset();
		this.playerId = playerId;
		this.pos = pos;
		this.roleInfo = roleInfo.clone();
		this.level = roleInfo.getLevel();
		this.itemId = this.roleInfo.getItemId();
		this.type = this.roleInfo.getType();
		init();
	}
	
	public NewBattleRole(RoleInfo.Builder roleInfo,int pos) {
		reset();
		this.pos = pos;
		this.roleInfo = roleInfo.clone();
		this.level = roleInfo.getLevel();
		this.itemId = this.roleInfo.getItemId();
		this.type = this.roleInfo.getType();
		init();
	}
	
	private void reset() {
		this.playerId = 0;
		this.pos = 0;
		this.level = 0;
		this.hp = 0;
		this.mp = 0;
		this.state = 0;
		this.ATKTime = 0;
		this.RoleCfg = null;
		this.roleInfo = null;
		this.itemId = 0;
		this.type = 0;
		this.attribute = new Attribute();
		this.SKLGroupId = new HashMap<Integer,CheckSkillInfo>();
		this.ATKGroupId = new HashMap<Integer,Integer>();
		this.firendList = new HashMap<Integer,NewBattleRole>();
		this.enemyList = new HashMap<Integer,NewBattleRole>();
		this.FoucusPos = new HashMap<Integer,Integer>();
		this.Buffs = new LinkedList<NewBuff>();
		this.UsedSkillId = new HashSet<>();
		this.triggerList = new ArrayList<>();
		this.PassiveSkill = new HashSet<>();
		this.PassiveBuff = new HashSet<>();
		this.SKLGetMp = new HashMap<>();
		this.markTime = 0;
		this.deadTime = 0;
		this.skillAddhp = 0;
		this.BossHp = 0;
	}
		
	private void init() {
		// 属性综合
		AttrInfo attrInfo = roleInfo.getAttribute();
		for (int i = 0; i < attrInfo.getAttributeCount(); i++) {
			Attr attr = attrInfo.getAttribute(i);
			if (attr != null) {
				attribute.add(attr.getAttrId(), attr.getAttrValue());
			}
		}
		
		// WorldBoss初始緊接上場攻擊剩餘血量
		if ((this.BossHp > 0)&&(this.getRoleType() == Const.roleType.WORLDBOSS_VALUE)){
			hp = this.BossHp;
		} else {
			hp = attribute.getValue(Const.attr.HP);
		}
		
		mp = attribute.getValue(Const.attr.MP);		
	}
	
	public int getATKTime() {
		return ATKTime;
	}
	
	public void setATKTime(int atime) {
		this.ATKTime = atime;
	}
	
	public int getBossInitHp() {
		return this.BossHp;
	}
	
	public void setTriggerList(List<String> alist,int markTime) {
		Set<Integer> tmpSkill = new HashSet<>();
		Set<Integer> tmpBuff = new HashSet<>(); 
		if (alist.size() > 0) {
			for(String aStr : alist) {
				String [] str = aStr.split("_");
				int atype = Integer.valueOf(str[0]);
				int aid = Integer.valueOf(str[1]);
				if (atype == GsConst.TriggerType.SKILL) {
						tmpSkill.add(aid);
				}
				if (atype == GsConst.TriggerType.BUFF) {
						tmpBuff.add(aid);
				}
			}
			if (tmpBuff.contains(GsConst.Buff.UNDEAD)&&!PassiveBuff.contains(GsConst.Buff.UNDEAD)) { // 第一次觸發被動Buff
				NewBuff abuff = getBuff(GsConst.Buff.UNDEAD);
				if (abuff != null) {
					double p2 = SkillUtil.getParams(SkillType.Passive_ID_90004).get(1);
					addBuff(GsConst.Buff.UNDEAD,markTime,(int)p2*1000);
				}
			}
		}
		this.triggerList = alist;
		this.PassiveSkill = tmpSkill;
		this.PassiveBuff = tmpBuff;
	}
	/**
	 * 取得用過的技能(判斷一場只能使用一次技能)
	 * @param skillId
	 * @return
	 */
	public boolean UsedSkillId(int skillId) {
		return UsedSkillId.contains(skillId);
	}
	/**
	 * 加入用過的技能
	 * @param skillId
	 * @return
	 */
	public void addUsedSkillId(int skillId) {
		if (!UsedSkillId(skillId)) {
			UsedSkillId.add(skillId);
		}
	}
	
	/**
	 * 移除用過的技能
	 * @param skillId
	 * @return
	 */
	public void removeUsedSkillId(int skillId) {
		if (UsedSkillId(skillId)) {
			UsedSkillId.remove(skillId);
		}
	}	
	/**
	 * 檢查累計普攻攻擊目標,累積次數(for 弓手)
	 */
	public void setATKFoucus(int Pos) {
		if (FoucusPos.size() > 0) {
			if (FoucusPos.containsKey(Pos)){
				FoucusPos.put(Pos,FoucusPos.get(Pos)+1);
			} else {
				FoucusPos.clear();
				FoucusPos.put(Pos,1);
			}
		} else {
			FoucusPos.put(Pos,1);
		}
	}
	
	/**
	 * 取出普攻目標連續攻擊次數(for 弓手)
	 */
	public int getATKFoucus(int Pos) {
		if (FoucusPos.containsKey(Pos)) {
			return FoucusPos.get(Pos);
		}
		return -1;
	}
	/**
	 * 清除普弓連續目標攻擊次數
	 */
	public void clearATKFoucus() {
		FoucusPos.clear();
	}
	
	/**
	 * 添加buff;
	 * 
	 * @param buffId    buffId;
	 * @param addTime    addTime;
	 * @param effectTimes    effectTimes;
	 * @return 如果添加成功返回对应的Buff，添加失败返回null；
	 */
	
	public NewBuff addBuff(int buffId,int addTime,int effectTimes) {
		BuffCfg buffCfg = ConfigManager.getInstance().getConfigByKey(BuffCfg.class, buffId);
		if (buffCfg == null) {
			return null;
		}
		
		if ((isInImmunity())&&(!buffCfg.isGain())) { // 免疫(debuff無法加入)
			return null;
		}
		if ((buffId == GsConst.Buff.STONE)||(buffId == GsConst.Buff.FRENZY)||(buffId == GsConst.Buff.SILENCE)||
				(buffId == GsConst.Buff.FREEZE)||(buffId == GsConst.Buff.DIZZY)) {
			if (checkSkillsID(SkillType.Passive_ID_999999)) { // WorldBoss對控場被動免疫
				return null;
			}
		}
		int ExpireDate = (effectTimes > 0) ? (addTime + effectTimes) : 0;
		for (NewBuff buff : Buffs) { 
			
			if ((buff.getGroup() == buffCfg.getGroup())) { // replace
				if (buff.getPriorty() < buffCfg.getPriorty()) {
					buff.setBuffId(buffId);
					buff.setExpireTimes(ExpireDate);
					buff.setOverlap(0);
					buff.UpdateMarkTime(addTime);
					getSpecialBuff(buff);
					return buff;
				}
				
				if (buff.getBuffId() == buffId) { // updateTime
					if (ExpireDate > buff.getExpireTimes()) {
						buff.setExpireTimes(ExpireDate);
						buff.UpdateMarkTime(addTime);
						getSpecialBuff(buff);
						return buff;
					}
				}	
			}
			
		}
		
		NewBuff newbuff = new NewBuff(buffId,addTime,ExpireDate);
		Buffs.add(newbuff);
		getSpecialBuff(newbuff);
		return newbuff;
	}
	/**
	 * 特殊BUFF獲得處理
	 */
	public void getSpecialBuff(NewBuff buff) {
		if (buff.getBuffId() == GsConst.Buff.IMMUNITY) { // 免疫
			//清空debuff
			removeDebuff();
		}
		if (buff.getBuffId() == GsConst.Buff.POWER) { // 權能
			//獲得護盾
			double valueArr = buff.getArgs().get(2);
			double sval = BattleUtil.mul((double)getMaxhp(),valueArr);
			int shield = (int) BattleUtil.calRoundValue(sval, 0);
			setShield(shield);
		} 
//		if (buff.getBuffId() == GsConst.Buff.IMMUNITY) { // 狂亂
//			//強制切換目標,client端用
//		}
		
		if (buff.getBuffType() == GsConst.BUFF_TYPE.AURA) { // 靈氣Buf 給全隊其他Buff
			AddAuraBuff(buff.getBuffId(),buff.getNowMarkTime());
		}
		
	}
	
	/**
	 * 檢查某Buff堆疊,(是否)滿後清除
	 */
	public boolean checkBuffStack(int buffId,boolean fulReset) {
		if (checkBuffValid(buffId)) {
			NewBuff buff  = getBuff(buffId);
			if (buff.canStack()) {
				addBuffStack(buff);
			} else {
				if (fulReset) {
					ResetBuffStack(buff);
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * 增加Buff堆疊
	 */
	
	public boolean addBuffStack(int buffId) {		
		NewBuff buff  = getBuff(buffId);
		return addBuffStack(buff);
	}
	/**
	 * 增加Buff堆疊
	 */
	
	public boolean addBuffStack(NewBuff buff) {		
		if ((buff != null)&&(buff.canStack())){
			buff.setOverlap(buff.getOverlap()+1);
		}
		return false;
	}
	/**
	 * 重製Buff堆疊
	 * @param buffId
	 * @return
	 */
	public boolean ResetBuffStack(int buffId) {		
		NewBuff buff  = getBuff(buffId);
		return ResetBuffStack(buff);
	}
	/**
	 * 重製Buff堆疊
	 * @param NewBuff
	 * @return
	 */
	public boolean ResetBuffStack(NewBuff buff) {		
		if (buff != null){
			buff.setOverlap(0);
			return true;
		}
		return false;
	}
	
	/**
	 * 移除buff ；
	 * 
	 * @param buffId
	 * @return
	 */
	public boolean removeBuffById(int buffId) {
		Iterator<NewBuff> iterator = Buffs.iterator();
		while (iterator.hasNext()) {
			NewBuff buff = iterator.next();
			if (buff.getBuffId() == buffId) {
				iterator.remove();
				if (buff.getBuffType() == GsConst.BUFF_TYPE.AURA) {
					clearAuraBuff(buffId);
				}
				return true;
			}
		}
		return false;
	}
	
	 /**
	  * 更新身上所有Buff時間
	  */
	public void UdateBuffMarkTime(int markTime) {
		 UdateMarkTime(markTime);
		 int invalid = 0;
		 Set<Integer> removeSet = new HashSet<>();
		 for (NewBuff aBuff:Buffs) {
			 invalid = aBuff.UpdateMarkTime(markTime); // 剛失效的buffid
			 if (invalid != 0) {
				 removeSet.add(invalid);
			 }
		 }
		 // 光環時效移除隊友buff
		 for (Integer buffId : removeSet) {
			BuffCfg buffCfg = ConfigManager.getInstance().getConfigByKey(BuffCfg.class,buffId);
			if (buffCfg != null) {
				if (buffCfg.getType()== GsConst.BUFF_TYPE.AURA) {
					clearAuraBuff(buffCfg.getId());
				}
			}
		 }
	}
	
	 /**
	  * 更新角色作用時間
	  */
	public void UdateMarkTime(int markTime) {
		this.markTime = markTime;
	}
	/**
	 * 取出角色作用時間
	 * @return
	 */
	public int getMarkTime() {
		return this.markTime;
	}
	/**
	 * 設定角色死亡時間
	 * @return
	 */
	public void setDeadTime(int dtime) {
		this.deadTime = dtime;
	}
	/**
	 * 取得角色死亡時間
	 * @return
	 */
	public int getDeadTime() {
		return this.deadTime;
	}
	
	/**
	 * 設定Skill 3001補血暫存
	 * @return
	 */
	public void setSkilladdhp(int addhp) {
		this.skillAddhp = addhp;
	}
	/**
	 * 取得Skill 3001補血暫存
	 * @return
	 */
	public int getSkilladdhp() {
		return this.skillAddhp;
	}
	
	public void clearSKLGetMp() {
		SKLGetMp.clear();
	}
	public Map<Integer,Integer> getSKLGetMp() {
		return this.SKLGetMp;
	}
	public void setSKLGetMp(int pos , int mp) {
		this.SKLGetMp.put(pos,mp);
	}
	/**
	 * 檢查身上此Buff是否有效
	 * @param buffId
	 * @return
	 */
	public boolean checkBuffValid(int buffId) {
		for (NewBuff buff : Buffs) {
			if ((buff.getBuffId() == buffId) && (buff.isValid())) {
				return true;
			}
		}
		return false;
	}
	/**
	* 取身上有效Buff
	*/
	public NewBuff getBuff(int buffId) {
		for (NewBuff buff : Buffs) {
			if ((buff.getBuffId() == buffId)&&(buff.isValid())) {
				return buff;
			}
		}
		return null;
	}
	/**
	 * 取身上Buff列表
	 */
	public List<Integer> getBuffList() {
		List<Integer> aList = new ArrayList<>();
		for (NewBuff buff : Buffs) {
			if (buff.isValid()) {
				aList.add(buff.getBuffId());
			}
		}
		return aList;
	}
	
	/**
	 * 移除所有可驅散损益buff
	 * 
	 * @param
	 * @return
	 */
	public boolean removeDebuff() {
		Iterator<NewBuff> iterator = Buffs.iterator();
		while (iterator.hasNext()) {
			NewBuff buff = iterator.next();
			/** 表示损益buff */
			if (!buff.IsGain()&&buff.IsDispel()) {
				iterator.remove();
			}
		}
		return true;
	}
	
	/**
	 * 移除所有的增益buff,并返回所移除的BuffId
	 */
	public Set<Integer> removeAllGainBuff() {
		Set<Integer> set = new HashSet<Integer>();
		Iterator<NewBuff> buffIter = this.Buffs.iterator();
		while (buffIter.hasNext()) {
			NewBuff battleBuff = buffIter.next();
			if (battleBuff.IsGain()&& battleBuff.IsDispel()) {
				set.add(battleBuff.getBuffId());
				buffIter.remove();
			}
		}
		for (Integer buffId :set) {
			BuffCfg buffCfg = ConfigManager.getInstance().getConfigByKey(BuffCfg.class,buffId);
			if (buffCfg != null) {
				if (buffCfg.getType()== GsConst.BUFF_TYPE.AURA) {
					clearAuraBuff(buffCfg.getId());
				}
			}
		}
		return set;
	}
	
	/**
	 * 获取所有的增益buff
	 */
	public Set<Integer> getGainBuffs() {
		Set<Integer> set = new HashSet<Integer>();
		Iterator<NewBuff> buffIter = this.Buffs.iterator();
		while (buffIter.hasNext()) {
			NewBuff battleBuff = buffIter.next();
			if (battleBuff.IsGain()&&battleBuff.isValid()) {
				set.add(battleBuff.getBuffId());
			}
		}
		return set;
	}

	/**
	 * 获取所有的損害Debuff
	 */
	public Set<Integer> getDeBuffs() {
		Set<Integer> set = new HashSet<Integer>();
		Iterator<NewBuff> buffIter = this.Buffs.iterator();
		while (buffIter.hasNext()) {
			NewBuff battleBuff = buffIter.next();
			if (!battleBuff.IsGain() && battleBuff.isValid()) {
				set.add(battleBuff.getBuffId());
			}
		}
		return set;
	}

	/**
	 * 清除所有buff
	 * 
	 * @return
	 */
	public void removeAllBuff() {
		// 檢查自已有無光環,並清除友軍光環Buff
		List<Integer> buffList = getBuffList();
		for(Integer buffId : buffList) {
			BuffCfg buffCfg = ConfigManager.getInstance().getConfigByKey(BuffCfg.class,buffId);
			if (buffCfg != null) {
				if (buffCfg.getType()== GsConst.BUFF_TYPE.AURA) {
					clearAuraBuff(buffCfg.getId());
				}
			}
		}	
		Buffs.clear();
	}
	/**
	 * --光環解除(清除光環賦予的buff/debuff)
	 */
	public void clearAuraBuff(int buffId){
		BuffCfg buffCfg = ConfigManager.getInstance().getConfigByKey(BuffCfg.class,buffId);
		int checkId = Double.valueOf(buffCfg.getParams().get(0)).intValue();
		if (buffCfg != null) {
		    List<NewBattleRole> flist = getfirendList(true);
		    for (NewBattleRole aRole : flist) { // 友軍身上是否有光環
		    	if (aRole.checkBuffValid(buffId)) {
		    		return;
		    	}
		    }
		    flist = getfirendList(false);
		    for (NewBattleRole aRole : flist) {
		    	aRole.removeBuffById(checkId);
		    }
		}
	}
	/**
	 * 給予全隊光環Buff
	 */
	public void AddAuraBuff(int buffId,int MarkTime){
		BuffCfg buffCfg = ConfigManager.getInstance().getConfigByKey(BuffCfg.class,buffId);
		int addId = Double.valueOf(buffCfg.getParams().get(0)).intValue();
		if (buffCfg != null) {
		    List<NewBattleRole> flist = getfirendList(false);
		    for (NewBattleRole aRole : flist) { // 授予全隊Buff
		    	aRole.addBuff(addId,MarkTime, 0);
		    }
		}
	}
		
	/**
	 * 回傳角色可以普攻攻擊次數
	 * @return
	 */
	public int getNATK() {
		return roleInfo.getNATK();
	}
	
	/**
	 * 回傳角色技能可攻擊次數(主要為怪使用)
	 * @return
	 */
	public int getSATK (int skillId) {
		if  ((type == GsConst.RoleType.MONSTER)||(type == GsConst.RoleType.WORLDBOSS)) {
			NewMonsterCfg monsterCfg = ConfigManager.getInstance().getConfigByKey(NewMonsterCfg.class,this.itemId);
			if (monsterCfg != null) {
				return monsterCfg.getSATK(skillId);
			}
		} else {
			Hero_NGListCfg roleCfg = getRoleCfg();
			if (roleCfg != null) {
				return roleCfg.getSATK(skillId);
			}
		}
		return 1;
	}
	
	/**
	 * 回傳角色攻擊回魔量
	 * @return
	 */
	public int getATKMP() {
		return roleInfo.getATKMP();
	}
	
	/**
	 * 回傳角色受擊回魔量
	 * @return
	 */
	public int getDEFMP() {
		return roleInfo.getDEFMP();
	}
	
	/**
	 * 回傳角色攻擊回魔量職業補正
	 * @return
	 */
	public float getClassCorrection() {
		return roleInfo.getClassCorrection();
	}
	
	public int getPlayerId() {
		return playerId;
	}
	
	public int getPos() {
		return pos;
	}

	public int getLevel() {
		return level;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = Math.max(hp, 0);
		this.hp = Math.min(this.hp, getMaxhp());
		updateState();
	}
			
	public int getMp() {
		return mp;
	}

	public void setMp(int mp) {
		this.mp = Math.max(mp, 0);
		this.mp = Math.min(this.mp, 100); // 每個人都100
	}
	
	public int getShield() {
		return shield;
	}
	
	public void setShield(int aValue) {
		this.shield = aValue;
	}

	public int getMaxhp() {
		return getAttrValue(Const.attr.HP);
	}
	
	public int getMaxmp() {
		return 100; // 每個人都100
	}
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	public int getRoleType() {
		return type;
	}
	
	public int getitemId() {
		return itemId;
	}

	public RoleInfo.Builder getRoleInfo() {
		return roleInfo;
	}
	
	public boolean isAlive() {
		return hp > 0 && state == GsConst.PersonState.PERSON_FIGHT;
	}

	public int updateState() {
		int tmpState = getState();
		if (hp <= 0) {
			state = GsConst.PersonState.PERSON_DEAD;
		}
		if ((tmpState != state)&&(state == GsConst.PersonState.PERSON_DEAD)) { //處裡死亡事件
			removeAllBuff();
		}
		return state;
	}
	
	public void setAttrValue(Const.attr attrType, int value) {
		attribute.set(attrType, value);
	}

	public void addAttrValue(Const.attr attrType, int value) {
		attribute.add(attrType, value);
	}
	
	public int getAttrValue(Const.attr attrType) {
		int attrValue = attribute.getValue(attrType);
		return attrValue;
	}
	
	public Map<Integer,Integer> getATKGroupId(){
		return this.ATKGroupId;
	}
	/**
	 * 檢查技能是否發動過,檢查超過受擊人數
	 */
	public boolean checkSKLGropId(NewBattleRoleInfo ActionInfo,NewBattleRoleInfo targetRoleInfo) {
		int atkStatus = targetRoleInfo.getStatus();
		if (atkStatus == 4) { //getBuff not check
			return true;
		}
		int skid = ActionInfo.getSkillId();
		int number = ActionInfo.getSkillGroupId();
		if (this.SKLGroupId.containsKey(skid)) {
			// 檢查流水
			if (this.SKLGroupId.get(skid).getNumber() !=  number) {
				return false;
			}
			int Pos = targetRoleInfo.getPosId();
			
			int NowAtkCount = this.SKLGroupId.get(skid).getPosATK(Pos);
			
			// 檢查此位置受擊次數
			if (NowAtkCount >= this.SKLGroupId.get(skid).getSATK()) {
				return false;
			}
			
			this.SKLGroupId.get(skid).setPosATK(Pos, NowAtkCount+1);
			
			return true;
		}
		return false;
	}
	
	/**
	 * 取該技能在此目標第幾次作用
	 */
	public int getSKLCount(int skid ,int Pos) {
		int count = -1;
		if (this.SKLGroupId.containsKey(skid)) {
			count = this.SKLGroupId.get(skid).getPosATK(Pos);
		}
		return count;
	}
	
	/**
	 * 檢查技能對目標發動次數(是否合理)
	 */
	public boolean checkSKLCount(int skid,int Pos,int checkCount) {
		int count =  getSKLCount(skid,Pos);
		if ((count == -1) || (count > checkCount )) {
			return false;
		}
		return true;
	}
	/**
	 * 取出此技能作用總數
	 */
	
	public int getSKLTotal(int skid){
		int total = -1;
		if (this.SKLGroupId.containsKey(skid)) {
			total = this.SKLGroupId.get(skid).getATKTotal();
		}
		return total;
	}
	
	/**
	 * 檢查技能發動總數次數(是否合理)
	 */
	public boolean checkSKLTotal(int skid,int checkCount) {
		int count =  getSKLTotal(skid);
		if ((count == -1) || (count > checkCount )) {
			return false;
		}
		return true;
	}
	
	/**
	 * 檢查普功是否發動過,並且加入計數
	 */	
	public boolean checkATKGroupId(int gpid) {
		if (this.ATKGroupId.containsKey(gpid)) {
			if (this.ATKGroupId.get(gpid) >= this.getNATK()) {
				return false;
			}
			this.ATKGroupId.replace(gpid, this.ATKGroupId.get(gpid)+1);
			return true;
		}
		return false;
	}
	/**
	 * 普功開始,重置攻擊次數
	 */
	 public void reSetATK(int Id) {
			ATKGroupId.clear();
			ATKGroupId.put(Id,0);
	 }
	/**
	 * 技能開始,重置攻擊次數
	 */
	 public void SetSkillWorkTime(NewBattleRoleInfo RoleInfo,int checktime ) {
		 int skillId = RoleInfo.getSkillId();
		 int SATK = getSATK(skillId);
		 if ((skillId == SkillType.SKILL_ID_501001)||(skillId == SkillType.SKILL_ID_600103)){ // 配合client動作多加1hit
			 SATK = SATK + 1;
		 }
		 CheckSkillInfo checkskill = new CheckSkillInfo(skillId,checktime,RoleInfo.getSkillGroupId(),SATK);
		 SKLGroupId.put(skillId, checkskill);
	 }
	/**
	 * 檢查普攻攻擊CD
	 */
	 public boolean checkNomralATKCD(int checktime) {
		 if (this.getATKTime() == 0) {
			 return true;
		 }
		 if (checktime >= this.getATKTime()) {
			 int dectime = checktime -  this.getATKTime();
			 int cdtime = this.getATKSpeed();
			 return (dectime >= cdtime);
		 }
		 return false;
	 }
	 /**
	  * 檢查是否掛載此技能ID
	  */
	 public boolean checkSkillsID(int skillsId) {
		 if (skillsId == 0) {
			 return false;
		 }
		 for (RoleSkill aroleskill : this.getRoleInfo().getSkillsList())
		 {
			 if (aroleskill.getItemId() == skillsId) {
				 return true;
			 }
		 }
		 for (RoleSkill aroleskill : this.getRoleInfo().getSkills2List())
		 {
			 if (aroleskill.getItemId() == skillsId) {
				 return true;
			 }
		 }
		 return false;
	 }
	 /**
	  * 檢查技能CD
	  */
	 public boolean checkSkillsCD(NewBattleRoleInfo RoleInfo,int checktime ) {
		
		 int skillsId = RoleInfo.getSkillId();
		 if (checkSkillsID(skillsId)) {
			 int cdtime = SkillUtil.getSkillCDTime(skillsId);
			 if (cdtime == -1) {
				 return false;
			 }
			 if (cdtime == 0) {
				 return true;
			 }
			 if (this.SKLGroupId.containsKey(skillsId)) {
				 
				 int worktime = this.SKLGroupId.get(skillsId).getAddTime();
				 int counttime =  0 ;
				 if (checktime >= worktime) {
					 counttime = checktime - worktime;
					 return (counttime >= cdtime);
				 }
				 return false;
			 } else {
				 return true;
			 }
			 
		 }
		 return false;
	 }
	 /**
	  * 檢查技能消耗
	  */
	 public boolean checkSkillsCost(NewBattleRoleInfo RoleInfo) {
		 int skillsId = RoleInfo.getSkillId();
		 if (checkSkillsID(skillsId)) {
			int cost = SkillUtil.getSkillCost(skillsId);  // 消耗
			if (cost == -1) {
				return false;
			}
			
			double addmp = 0;
			if (Istrigger(GsConst.TriggerType.SKILL,SkillType.Eye_ID_100002)) { //被動技 施展技能時加mp
				addmp = SkillUtil.getParams(SkillType.Eye_ID_100002).get(0);
			}
			// 檢查log
			int countcost = 0 ;
			countcost = Math.max(RoleInfo.getNowMp() - cost,0);
			countcost = Math.min(countcost+(int)addmp,100);
			int newMp = RoleInfo.getNewMp();
			if (countcost != newMp) {
				return false;
			}
			return true;
		 }
		 return false;
	 }
	 /**
	  * 檢查技能觸發條件
	  */
	 public boolean checkSkillsTrigger(NewBattleRoleInfo RoleInfo) {
		 int skillsId = RoleInfo.getSkillId();
		 NewSkillCfg cfg = SkillUtil.getSkillCfg(skillsId);
		 if (cfg.getTriggerCon() != 0) {
			 double checkper = 0.0;
			 double cfgper = cfg.getTriggerPer();
			 if (cfg.getTriggerCon() == 1) {
				 checkper= BattleUtil.div((double)getHp(),(double)getMaxhp(),2);
				 if (checkper >= cfgper) {
					 return true;
				 }
			 } else if (cfg.getTriggerCon() == 2) {
				 checkper= BattleUtil.div((double)getHp(),(double)getMaxhp(),2);
				 if (checkper <= cfgper) {
					 return true;
				 }
			 } else if (cfg.getTriggerCon() == 3) {
				 checkper= BattleUtil.div((double)getMp(),(double)getMaxmp(),2);
				 if (checkper >= cfgper) {
					 return true;
				 }
			 } else if (cfg.getTriggerCon() == 4) {
				 checkper= BattleUtil.div((double)getMp(),(double)getMaxmp(),2);
				 if (checkper <= cfgper) {
					 return true;
				 }
			 } else {
				 return true;
			 }
			 return false;
		 } else {
			 return true;
		 }
	 }
	 
	/**
	 * 
	 * 處裡發動玩家前置作業檢查
	 */
	public boolean HandleActStatus(NewBattleRoleInfo ActionInfo,int checktime) {
		int act = ActionInfo.getAction();
		if (act == GsConst.AttackType.StartNomarl) {
			if (isInCrowdControl()) {
				Log.debugInfo("ret:4,StartSkill 5 isInCrowdControl");
				return false;
			}
			if (!checkNomralATKCD(checktime)) {
				Log.debugInfo("ret:4 checkNomralATKCD");
				return false;
			} else {
				if (ActionInfo.getSkillGroupId() == 0) {
					Log.debugInfo("ret:4 getSkillGroupId == 0");
					return false;
				}
				reSetATK(ActionInfo.getSkillGroupId());
				setATKTime(checktime);
				return true;
			}
		} else if(act == GsConst.AttackType.StartSkill){
			if (isInCrowdControl()) {
				Log.debugInfo("ret:4 ,StartSkill 5 isInCrowdControl");
				return false;
			}
			if (isInFrenzy()) {
				Log.debugInfo("ret:4 ,StartSkill 5 isInFrenzy");
				return false;
			}
			if (isInSilene()) {
				Log.debugInfo("ret:4 ,StartSkill 5 isInSilene");
				return false;
			}
			// 檢查消耗
			if (!checkSkillsCost(ActionInfo)) {
				Log.debugInfo("ret:4 ,StartSkill 5 checkSkillsCost");
				return false;
			}
			// 檢查CD
			if (!checkSkillsCD(ActionInfo,checktime)) {
				Log.debugInfo("ret:4 ,StartSkill 5 checkSkillsCD");
				return false;
			}
			// 檢查發動條件
			if (!checkSkillsTrigger(ActionInfo)) {
				Log.debugInfo("ret:4 ,StartSkill 5 checkSkillsTrigger");
				return false;
			}
			SetSkillWorkTime(ActionInfo,checktime);
			setMp(ActionInfo.getNewMp());
			return true;
		} else if(act == GsConst.AttackType.Buff_DeBuff){
			int buffid = ActionInfo.getSkillId();
			if (!checkBuffValid(buffid)){ // 身上有無此Buff
				Log.debugInfo("ret:4 ,Buff_DeBuff 3 checkBuffValid");
				return false;
			}
			return true;
		} else if(act == GsConst.AttackType.NomarlATK){ //普功作用
			Map<Integer,Integer> ATKGroupId = getATKGroupId();
			if (!ATKGroupId.containsKey(ActionInfo.getSkillGroupId())){ //是否有發起攻擊
				Log.debugInfo("ret:4 ,NomarlATK 1 getSkillGroupId");
				return false;
			}
			return true;
		} else if(act == GsConst.AttackType.SkillACT){ //技能作用
			 // 檢查掛載技能	
			 if (!checkSkillsID(ActionInfo.getSkillId())) {
				 Log.debugInfo("ret:4 ,SkillACT 2 getSkillGroupId");
				 return false;
			 }
			if (!this.SKLGroupId.containsKey(ActionInfo.getSkillId())){ //是否有發起技能
				Log.debugInfo("ret:4 ,SkillACT 2 SKLGroupId 1");
				return false;
			}
			// 檢查流水
			if (this.SKLGroupId.get(ActionInfo.getSkillId()).getNumber() != ActionInfo.getSkillGroupId()) {
				Log.debugInfo("ret:4 ,SkillACT 2 SKLGroupId 2");
				return false;
			}
			if (this.SKLGroupId.get(ActionInfo.getSkillId()).getAddTime() > checktime) {  // 作用一定比發招晚
				Log.debugInfo("ret:4 ,SkillACT 2 SKLGroupId 3");
				return false;
			}
			return true;
		}
		return false;
	}
	
    /**
     * 获取職業配置文件
     *
     * @return
     */
    public Hero_NGListCfg getRoleCfg() {
        if (RoleCfg == null) {
        	RoleCfg = ConfigManager.getInstance().getConfigByKey(Hero_NGListCfg.class, getRoleInfo().getItemId());
        }
        return RoleCfg;
    }
    
    /**
     * 是否為魔法職業
     *
     * @return
     */
    public boolean IsMagic() {
        return (this.roleInfo.getIsMagic() == 1);
    }
    
    public int getATKSpeed() {
    	double oriVal = (double)this.getRoleInfo().getATKSpeed();
    	BuffValue buff = BuffUtil.checkAtkSpeedBuffValue(this); 
        BuffValue debuff = BuffUtil.checkAtkSpeedDeBuffValue(this);		
        double buffval = BattleUtil.add(1.0, Math.max(buff.getAlladd(),0));;
        double debuffval = BattleUtil.add(1.0,Math.max(debuff.getAllMul(),0));;
        double newVal = BattleUtil.mul(oriVal, BattleUtil.div(debuffval,buffval, 2));
        Log.debugInfo("getATKSpeed(),Pos:{},debuffval:{},buffval:{},newVl:{}",getPos(),debuffval,buffval,newVal);
        Log.debugInfo("ATKSpeed_buff(),Pos:{},BuffValue:{},AuraValue:{},MarkValue:{}",getPos(),buff.getBuffValue(),buff.getAuraValue(),buff.getMarkValue());
        Log.debugInfo("ATKSpeed_debuff(),Pos:{},BuffValue:{},AuraValue:{},MarkValue:{}",getPos(),debuff.getBuffValue(),debuff.getAuraValue(),debuff.getMarkValue());
    	return (int)newVal;
    }
	
	public double getAttck() {
		BuffValue buffval = BuffUtil.checkAtkBuffValue(this,!this.IsMagic());
		if (IsMagic()) {
			return BattleUtil.calRoundValue(BattleUtil.mul((double)getAttrValue(Const.attr.MAGIC_attr),buffval.getAllMul()),0);
		}
		return BattleUtil.calRoundValue(BattleUtil.mul((double)getAttrValue(Const.attr.ATTACK_attr),buffval.getAllMul()),0);
	}
	
	public double getBaseDef(boolean IsMagic) { //取基礎物防魔防
		BuffValue buffval = BuffUtil.checkDefBuffValue(this,!IsMagic);
		if (IsMagic) { //受魔法攻擊
		 return	BattleUtil.calRoundValue(BattleUtil.mul((double)getAttrValue(Const.attr.MAGDEF),buffval.getAllMul()),0);
		}
		return BattleUtil.calRoundValue(BattleUtil.mul((double)getAttrValue(Const.attr.PHYDEF),buffval.getAllMul()),0);
	}
	
	public double getPenetrate(boolean isNormalAtk) { // 取穿透
		BuffValue buffval = BuffUtil.checkDefPenetrateBuffValue(this,isNormalAtk);
		double value = 0.0;
	    value = BattleUtil.add(BattleUtil.div((double)getAttrValue(Const.attr.BUFF_PHYDEF_PENETRATE),100.0,2),buffval.getAlladd());
	    value = BattleUtil.calRoundValue(value,2);
		return Math.min(value, 1.0);
	}
	
	public int getElement() { // 取元素
		return getRoleInfo().getElements();
	}
	
	public double getCritDmg(boolean isNormalAtk) {
		double criDmgRate = 0;
		double value = BattleUtil.div((double)getAttrValue(Const.attr.BUFF_CRITICAL_DAMAGE),100.0,2);
		BuffValue buffval = BuffUtil.checkCriDmgBuffValue(this,isNormalAtk);
		criDmgRate = BattleUtil.add(value, buffval.getAlladd());
		return BattleUtil.calRoundValue(criDmgRate, 2);
	}
	/**
	 * 取(目標)爆擊耐性(%)
	 */
	public double getCritResist() {
		int criResistValue = getAttrValue(Const.attr.RESILIENCE);
		double criResistRate = BattleUtil.div((double)criResistValue,BattleUtil.add((double)criResistValue, 300.0),2); 
		return criResistRate;
	}
	
	public boolean IsMyself(int tarpos) {
		return (getPos() == tarpos);
	}
	
	public boolean IsEnemy (int tarpos) {		
		if (IsMyself(tarpos)) {
			return false;
		}
		if ((getPos() >= 10) && (tarpos >= 10)) {
			return false;
		}
		if ((getPos() < 10) && (tarpos < 10)) {
			return false;
		}
		return true;
	}
	
	public boolean IsBNBHero() {
		if (getRoleInfo().getProf() == 99) {
			return true;
		}
		return false;
	}
	
	public void setfirendList(Map<Integer,NewBattleRole> rolemap) {
		this.firendList = rolemap;
	}
	
	public void setenemyList(Map<Integer,NewBattleRole> rolemap) {
		this.enemyList = rolemap;
	}
	/**
	 * 取其他友軍
	 * @param jumpself(略過自己)
	 * @return
	 */
	public List<NewBattleRole> getfirendList(boolean jumpself){
		List<NewBattleRole> aList = new ArrayList<>();
		for(Integer Pos : firendList.keySet()) {
			if ((jumpself) && (getPos() == Pos)){
				continue;
			}
			aList.add(firendList.get(Pos));
		}
		return aList;
	}
	/**
	 * 取出敵人
	 * @return
	 */
	public List<NewBattleRole> getenemyList(){
		List<NewBattleRole> aList = new ArrayList<>();
		for(Integer Pos : enemyList.keySet()) {
			aList.add(enemyList.get(Pos));
		}
		return aList;
	}
	
	/**
	 * 是否狂亂狀態(只可普攻)
	 */
	
	public boolean isInFrenzy() {
		if (!checkSkillsID(SkillType.Passive_ID_10099)) {
			NewBuff buff = getBuff(GsConst.Buff.FRENZY);
			if (buff != null){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 是否沉默狀態(不可施放技能)
	 */
	
	public boolean isInSilene() {
		NewBuff buff = getBuff(GsConst.Buff.SILENCE);
		if (buff != null){
			return true;
		}
		return false;
	}
	
	/**
	 * 是否定身狀態(不可行動)
	 */
	
	public boolean isInCrowdControl() {
		NewBuff buff = getBuff(GsConst.Buff.STONE);
		if (buff != null){
			return true;
		}
		buff = getBuff(GsConst.Buff.FREEZE);
		if (buff != null){
			return true;
		}
		buff= getBuff(GsConst.Buff.DIZZY);
		if (buff != null){
			return true;
		}
		return false;
	}

	/**
	 * 是否毅力狀態(鎖血%數)
	 */
	
	public double isInUnDead() {
		NewBuff buff = getBuff(GsConst.Buff.UNDEAD);
		double value = 0.0;
		if (buff != null){
			value = buff.getArgs().get(0);
		}
		return value;
	}
	
	/**
	 * 觸發毅力狀態
	 */
	
//	public boolean castInUnDead() {
//		NewBuff buff = getBuff(GsConst.Buff.UNDEAD);
//		if (buff != null){
//			return true;
//		}
//		return false;
//	}
	
	 /**
	  * 是否免疫狀態(不會獲得Debuff)
	 */
	public boolean isInImmunity() {
		NewBuff buff = getBuff(GsConst.Buff.IMMUNITY);
		if (buff != null){
			return true;
		}
		return false;
	}
	/**
	 * 檢查技能或Buff是否可被動觸
	 */
	public boolean Istrigger(int type,int id) {
		if ((this.triggerList == null)&&(this.triggerList.size() <= 0)){
			return false;
		}
		if (type == GsConst.TriggerType.SKILL) {
			if (checkSkillsID(id)&& PassiveSkill.contains(id)) {
				return true;
			}
		}
		if (type == GsConst.TriggerType.BUFF) {
			if (checkBuffValid(id)&& PassiveBuff.contains(id)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 檢查技能攻擊獲得MP
	 */
	public boolean checkSKLGetMp(NewBattleRoleInfo ActionInfo) {
		int newMp = ActionInfo.getNewMp();
		int chkAddmp =  newMp - ActionInfo.getNowMp();
		int calmp = 0;
		String record = "";
		for (Integer pos :SKLGetMp.keySet()) {
			calmp = calmp + SKLGetMp.get(pos);
			record = record + String.format("Pos:{%d}==Mp:{%d},",pos,SKLGetMp.get(pos));
		}
		if (calmp < chkAddmp) {
			Log.debugInfo("ret:11,checkSKLGetMp:{},calmp:{},chkAddmp{}", record,calmp,chkAddmp);
		}
			
		return (calmp >= chkAddmp);
	}
	/**
	 * 觸發不死Buff血量檢查
	 */
	public boolean checkIsUndeadHP(NewBattleRoleInfo targetRoleInfo,boolean skillhurt) {
		if (this.Istrigger(GsConst.TriggerType.BUFF,GsConst.Buff.UNDEAD)) {
			int chkHurt = BattleUtil.calLogGetHurt(targetRoleInfo.getNowHp(),targetRoleInfo.getNewHp(),targetRoleInfo.getNowShield(),targetRoleInfo.getNewShield());
			double aval = this.isInUnDead();
			int ret = skillhurt ? 8 :7;
			if (aval != 0.0) {
				int decshield = 0;
				if (targetRoleInfo.getNowShield() > targetRoleInfo.getNewShield()) {
					decshield = targetRoleInfo.getNowShield() - targetRoleInfo.getNewShield();
				}
				int limitHp = (int)Math.ceil(BattleUtil.mul(aval,(double)this.getMaxhp())); // 無條件進位
				int calhurt = (this.getHp() - limitHp) + decshield;
				if (chkHurt != calhurt) {
					Log.debugInfo("ret:{} targetRole.isInUnDead(),aval:{},limitHp:{},calhurt:{},chkHurt:{}",ret,aval,limitHp,calhurt,chkHurt);
					return false;
				}
			}
		}
		return true;
	}
}
