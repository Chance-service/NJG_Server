package com.guaji.game.battle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guaji.config.ConfigManager;
import org.guaji.os.MyException;
import org.guaji.os.GuaJiRand;

import com.guaji.game.protocol.Attribute.Attr;
import com.guaji.game.protocol.Attribute.AttrInfo;
import com.guaji.game.attribute.AttrInfoObj;
import com.guaji.game.attribute.Attribute;
import com.guaji.game.config.BuffCfg;
import com.guaji.game.config.MonsterCfg;
import com.guaji.game.config.MultiMonsterCfg;
import com.guaji.game.config.RingCfg;
import com.guaji.game.config.RoleCfg;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Battle;
import com.guaji.game.protocol.Battle.BattleInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.attr;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Player.RoleSkill;

/**
 * 战斗角色
 */
public class BattleRole {
	/**
	 * 玩家id
	 */
	private int playerId;
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
	 * 状态(正常, 逃跑, 死亡)
	 */
	private int state;
	/**
	 * 标记
	 */
	private int flag;
	/**
	 * 逃跑回合数
	 */
	private int escapeRound;
	/**
	 * 杀敌数量
	 */
	private int killEnemyNum;
	/**
	 * 战斗积分
	 */
	private int battleScore;
	/**
	 * 狂暴开始回合数
	 */
	protected int rageRound;

	/**
	 * 可复活的被动技能
	 */
	protected Map<Integer, Integer> reviveTimes;

	/**
	 * 狂暴伤害层加成
	 */
	protected float rageAddition;
	/**
	 * 角色信息
	 */
	private RoleInfo.Builder roleInfo;
	/**
	 * 当前buff
	 */
	private List<BattleBuff> buffs;
	/**
	 * 技能开始冷却回合
	 */
	private Map<Integer, Integer> skillCds;
	/**
	 * 战斗属性数据
	 */
	private Attribute attribute;
	/**
	 * 战斗特殊标记Map
	 */
	private Map<Integer, Object> specialFlagMap;
	// 是否狂乱
	private int isFrenzy;

	/**
	 * 是否是协战武将
	 */
	private boolean isHelper;

	public BattleRole(RoleInfo.Builder roleInfo) {
		this.pos = 0;
		this.level = 0;
		this.hp = 0;
		this.mp = 0;
		this.state = 0;
		this.playerId = 0;
		this.killEnemyNum = 0;
		this.battleScore = 0;
		this.escapeRound = 0;
		this.flag = 0;
		this.roleInfo = roleInfo.clone();
		this.buffs = new LinkedList<BattleBuff>();
		this.skillCds = new HashMap<Integer, Integer>();
		this.reviveTimes = new HashMap<>();
		this.attribute = new Attribute();
		this.specialFlagMap = new HashMap<>();
		this.isHelper = false;
		init();
	}

	public BattleRole(int playerId, RoleInfo.Builder roleInfo) {
		this(roleInfo);
		this.playerId = playerId;
	}

	public BattleRole(int playerId, RoleInfo.Builder roleInfo, boolean isHelper) {
		this(roleInfo);
		this.playerId = playerId;
		this.isHelper = isHelper;
	}

	public int getPlayerId() {
		return playerId;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public int getLevel() {
		return level;
	}

	public int getHp() {
		return hp;
	}

	public int getMaxDmg() {
		return getAttrValue(Const.attr.MAXDMG);
	}

	public void setHp(int hp) {
		this.hp = Math.max(hp, 0);
		this.hp = Math.min(this.hp, getAttrValue(Const.attr.HP));
		updateState();
	}

	public int getMp() {
		return mp;
	}

	public void setMp(int mp) {
		this.mp = Math.max(mp, 0);
		this.mp = Math.min(this.mp, getAttrValue(Const.attr.MP));
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getKillEnemyNum() {
		return killEnemyNum;
	}

	public void setKillEnemyNum(int killEnemyNum) {
		this.killEnemyNum = killEnemyNum;
	}

	public int getBattleScore() {
		return battleScore;
	}

	public void setBattleScore(int battleScore) {
		this.battleScore = battleScore;
	}

	public int getRoleType() {
		return roleInfo.getType();
	}

	public int getEscapeRound() {
		return escapeRound;
	}
	


	public RoleInfo.Builder getRoleInfo() {
		return roleInfo;
	}

	public List<BattleBuff> getBuffs() {
		return buffs;
	}

	public void setBuffs(List<BattleBuff> buffs) {
		this.buffs = buffs;
	}

	private void init() {
		level = roleInfo.getLevel();

		// 怪物设置逃跑回合数
		escapeRound = 99;
		if (getRoleType() == Const.roleType.MONSTER_VALUE) {
			MonsterCfg monsterCfg = ConfigManager.getInstance().getConfigByKey(MonsterCfg.class, roleInfo.getItemId());
			if (monsterCfg != null) {
				escapeRound = monsterCfg.getRunRound();
				rageRound = monsterCfg.getRageRound();
				rageAddition = monsterCfg.getRageAddition();
				isFrenzy = monsterCfg.isFrenzy();
			}
		} else if (getRoleType() == Const.roleType.MAIN_ROLE_VALUE || GameUtil.isHero(getRoleType())) {
			RoleCfg roleCfg = ConfigManager.getInstance().getConfigByKey(RoleCfg.class, roleInfo.getItemId());
			if (roleCfg != null) {
				rageRound = roleCfg.getRageRound();
				rageAddition = roleCfg.getRageAddition();
			}
			// 检查是否有复活次数
			if (this.roleInfo != null) {
				for (Integer rindId : this.roleInfo.getRingIdList()) {
					// 解锁光环
					RingCfg ringCfg = ConfigManager.getInstance().getConfigByKey(RingCfg.class, rindId);
					// 有复活的被动技能
					if (ringCfg != null && ringCfg.getReviveTimes() > 0) {
						if (this.reviveTimes.containsKey(ringCfg.getId())) {
							this.reviveTimes.put(ringCfg.getId(),
									this.reviveTimes.get(ringCfg.getId()) + ringCfg.getReviveTimes());
						} else {
							this.reviveTimes.put(ringCfg.getId(), ringCfg.getReviveTimes());
						}
					}
				}
			}
		}
//		} else if (getRoleType() == Const.roleType.MULTI_MONSTER_VALUE) {
//			MultiMonsterCfg monsterCfg = ConfigManager.getInstance().getConfigByKey(MultiMonsterCfg.class,
//					roleInfo.getRoleId());
//			if (monsterCfg != null) {
//				escapeRound = monsterCfg.getRunRound();
//				rageRound = monsterCfg.getRageRound();
//				rageAddition = monsterCfg.getRageAddition();
//				isFrenzy = monsterCfg.isFrenzy();
//			}
//		}

		// 属性综合
		AttrInfo attrInfo = roleInfo.getAttribute();
		for (int i = 0; i < attrInfo.getAttributeCount(); i++) {
			Attr attr = attrInfo.getAttribute(i);
			if (attr != null) {
				attribute.add(attr.getAttrId(), attr.getAttrValue());
			}
		}

		hp = attribute.getValue(Const.attr.HP);
		mp = attribute.getValue(Const.attr.MP);
	}

	public BattleInfo.Character.Builder genCharacter() {
		BattleInfo.Character.Builder builder = BattleInfo.Character.newBuilder();
		builder.setType(roleInfo.getType());
		builder.setId(roleInfo.getRoleId());
		builder.setItemId(roleInfo.getItemId());
		builder.setLevel(roleInfo.getLevel());
		builder.setPos(pos);
		builder.setName(roleInfo.getName());
		builder.setHp(getAttrValue(Const.attr.HP));
		builder.setMp(getAttrValue(Const.attr.MP));
		builder.setState(0);
		builder.setPlayerId(playerId);
		builder.setFlag(flag);
		builder.setCurHp(this.getHp());
		builder.setCurMp(this.getMp());
		builder.setRebirthStage(roleInfo.getRebirthStage());
		builder.setAvatarId(roleInfo.getAvatarId());
		return builder;
	}

	public BattleInfo.Person.Builder genPerson() {
		BattleInfo.Person.Builder builder = BattleInfo.Person.newBuilder();
		builder.setPos(pos);
		builder.setHp(hp);
		builder.setMp(mp);
		builder.setStatus(state);
		builder.setRoleId(roleInfo.getRoleId());

		for (BattleBuff buff : buffs) {
			if (buff.isValid() && buff.isVisible()) {
				builder.addBuffInfo(buff.genBuilder());
			}
		}
		return builder;
	}

	public BattleInfo.Person.Builder updatePersonBuilder(BattleInfo.Person.Builder builder) {
		builder.setPos(pos);
		builder.setHp(hp);
		builder.setMp(mp);
		builder.setStatus(state);

		builder.clearBuffInfo();
		for (BattleBuff buff : buffs) {
			if (buff.isValid() && buff.isVisible()) {
				builder.addBuffInfo(buff.genBuilder());
			}
		}
		return builder;
	}

	public boolean isAlive() {
		return hp > 0 && state == GsConst.PersonState.PERSON_FIGHT;
	}

	public int updateState() {
		if (hp <= 0) {
			state = GsConst.PersonState.PERSON_DEAD;
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

		// 致盲
		if (checkBuffValid(Const.Buff.BLIND_VALUE)) {
			BattleBuff battleBuff = this.getBuff(Const.Buff.BLIND_VALUE);
			if (attrType.getNumber() == Const.attr.HIT_VALUE) {
				attrValue = (int) ((1 - battleBuff.getBuffArgs(0) * 0.0001f) * attrValue);
			}
		}

		// 鼓舞
		if (checkBuffValid(Const.Buff.INSPIRE_VALUE)) {
			BattleBuff battleBuff = this.getBuff(Const.Buff.INSPIRE_VALUE);
			if (battleBuff != null) {
				// 伤害提升30%
				if (attrType.getNumber() == Const.attr.MINDMG_VALUE
						|| attrType.getNumber() == Const.attr.MAXDMG_VALUE) {
					attrValue = (int) ((1 + battleBuff.getBuffArgs(0) * 0.0001f) * attrValue);
				}
				// 护甲提升50%
				if (attrType.getNumber() == Const.attr.ARMOR_VALUE) {
					attrValue = (int) ((1 + battleBuff.getBuffArgs(1) * 0.0001f) * attrValue);
				}
			}
		}

		// 霸体
		if (checkBuffValid(Const.Buff.SUPER_ARMOR_VALUE)) {
			BattleBuff battleBuff = this.getBuff(Const.Buff.SUPER_ARMOR_VALUE);
			if (battleBuff != null) {
				// 伤害
				if (attrType.getNumber() == Const.attr.MINDMG_VALUE
						|| attrType.getNumber() == Const.attr.MAXDMG_VALUE) {
					attrValue += battleBuff.getBuffArgs(0);
				}
				// 护甲
				if (attrType.getNumber() == Const.attr.ARMOR_VALUE) {
					attrValue += battleBuff.getBuffArgs(1);
				}
			}
		}

		// 虚弱
		if (checkBuffValid(Const.Buff.WEAKNESS_VALUE)) {
			// 伤害降低50%
			if (attrType.getNumber() == Const.attr.MINDMG_VALUE || attrType.getNumber() == Const.attr.MAXDMG_VALUE) {
				attrValue = (int) (0.5f * attrValue);
			}
		}
		
		// 恐懼
		if (checkBuffValid(Const.Buff.SCARE_VALUE)) {
			BattleBuff battleBuff = this.getBuff(Const.Buff.SCARE_VALUE);
			// 伤害降低XX%
			if (attrType.getNumber() == Const.attr.MINDMG_VALUE || attrType.getNumber() == Const.attr.MAXDMG_VALUE) {
				float value = 0.7f + (float)battleBuff.getBuffArgs(0)/100;
				attrValue = (int) (value * attrValue);
			}
		}

		// 破甲
		BattleBuff armorBuff = this.getBuff(Const.Buff.ARMOUR_VALUE);
		if (armorBuff != null && armorBuff.isValid()) {
			// 护甲降低80%
			if (attrType.getNumber() == Const.attr.ARMOR_VALUE) {
				attrValue = (int) ((1 - armorBuff.getBuffArgs(0) * 0.0001f) * attrValue);
			}
		}

		// 加攻
		BattleBuff additionBuff = this.getBuff(Const.Buff.ADDITION_DMG_VALUE);
		if (additionBuff != null && additionBuff.isValid()) {
			// 伤害提升100%
			if (attrType.getNumber() == Const.attr.MINDMG_VALUE || attrType.getNumber() == Const.attr.MAXDMG_VALUE) {
				attrValue = (int) ((1 + additionBuff.getBuffArgs(0) * 0.0001f) * attrValue);
			}

			// 免控
			if (attrType.getNumber() == Const.attr.BUFF_AVOID_CONTROL_VALUE) {
				attrValue = (int) additionBuff.getBuffArgs(1) + attrValue;
				// attrValue =(int) ((1 + additionBuff.getBuffArgs(1) * 0.0001f) * attrValue);
			}
		}

		// 鲁莽
		BattleBuff recklessBuff = this.getBuff(Const.Buff.RECKLESS_VALUE);
		if (recklessBuff != null && recklessBuff.isValid()) {
			// 伤害提升100%
			if (attrType.getNumber() == Const.attr.MINDMG_VALUE || attrType.getNumber() == Const.attr.MAXDMG_VALUE) {
				attrValue = (int) ((1 + recklessBuff.getBuffArgs(0) * 0.0001f) * attrValue);
			}
		}

		// 风破
		BattleBuff windBreakBuff = this.getBuff(Const.Buff.WINDBREAK_VALUE);
		if (windBreakBuff != null && windBreakBuff.isValid()) {
			// 伤害提升30%
			if (attrType.getNumber() == Const.attr.MINDMG_VALUE || attrType.getNumber() == Const.attr.MAXDMG_VALUE) {
				attrValue = (int) ((1 + windBreakBuff.getBuffArgs(0) * 0.0001f) * attrValue);
			}
		}

		// 魔箭
		BattleBuff magicBuff = this.getBuff(Const.Buff.MAGIC_ATTACK_VALUE);
		if (magicBuff != null && magicBuff.isValid()) {
			if (attrType.getNumber() == Const.attr.MAGDEF_VALUE) {
				attrValue = (int) (attrValue * (magicBuff.getBuffArgs(1) * 0.0001f + 1.0f));
			}
		}

		BattleBuff markBuff = this.getBuff(Const.Buff.MARK_VALUE);
		if (markBuff != null && markBuff.isValid()) {
			// 护甲与抗性
			if (attrType.getNumber() == Const.attr.ARMOR_VALUE) {
				attrValue = (int) ((1 - markBuff.getBuffArgs(0) * 0.0001f) * attrValue);
			}
			if (attrType.getNumber() == Const.attr.PHYDEF_VALUE) {
				attrValue = (int) ((1 - markBuff.getBuffArgs(0) * 0.0001f) * attrValue);
			}
			if (attrType.getNumber() == Const.attr.MAGDEF_VALUE) {
				attrValue = (int) ((1 - markBuff.getBuffArgs(0) * 0.0001f) * attrValue);
			}
		}

		BattleBuff beenFrozenBuff = this.getBuff(Const.Buff.BEEN_FROZEN_VALUE);
		if (beenFrozenBuff != null && beenFrozenBuff.isValid()) {
			// 护甲提升
			if (attrType.getNumber() == Const.attr.ARMOR_VALUE) {
				attrValue = (int) ((1 + beenFrozenBuff.getBuffArgs(0) * 0.0001) * attrValue);
			}
		}

		BattleBuff ironArmourBuff = this.getBuff(Const.Buff.IRON_ARMOUR_VALUE);
		if (ironArmourBuff != null && ironArmourBuff.isValid()) {
			// 护甲提升
			if (attrType.getNumber() == Const.attr.ARMOR_VALUE) {
				attrValue = (int) ((1 + ironArmourBuff.getBuffArgs(0) * 0.0001) * attrValue);
			}
		}

		// 削弱攻击
		BattleBuff disAttackBuff = this.getBuff(Const.Buff.DIS_ATTACK_VALUE);
		if (disAttackBuff != null && disAttackBuff.isValid()) {
			// 降低攻击
			if (attrType.getNumber() == Const.attr.MINDMG_VALUE || attrType.getNumber() == Const.attr.MAXDMG_VALUE) {
				attrValue = (int) ((1 - disAttackBuff.getBuffArgs(0) * 0.0001f) * attrValue);
			}
		}

		// 物理 法术防御同时降低
		BattleBuff shreddingBuff = this.getBuff(Const.Buff.SHREDDING_VALUE);
		if (shreddingBuff != null && shreddingBuff.isValid()) {
			if (attrType.getNumber() == Const.attr.PHYDEF_VALUE) {
				attrValue = (int) ((1 - shreddingBuff.getBuffArgs(0) * 0.0001f) * attrValue);
			}
			if (attrType.getNumber() == Const.attr.MAGDEF_VALUE) {
				attrValue = (int) ((1 - shreddingBuff.getBuffArgs(0) * 0.0001f) * attrValue);
			}
		}

		// 削弱防御
		BattleBuff disDefenceBuff = this.getBuff(Const.Buff.DIS_DEFENCE_VALUE);
		if (disDefenceBuff != null && disDefenceBuff.isValid()) {
			// 降低防御
			if (attrType.getNumber() == Const.attr.ARMOR_VALUE) {
				attrValue = (int) ((1 - disDefenceBuff.getBuffArgs(0) * 0.0001f) * attrValue);
			}
		}

		return attrValue;
	}

	public List<RoleSkill> getBattleSkills(int battleType) {
		if (roleInfo.getType() == Const.roleType.MAIN_ROLE_VALUE && roleInfo.getSkills2List().size() > 0) {
			if (battleType == Battle.battleType.BATTLE_PVP_CAMP_VALUE
					|| battleType == Battle.battleType.BATTLE_PVE_BOSS_VALUE
					|| battleType == Battle.battleType.BATTLE_PVE_ELITE_BOSS_VALUE
					|| battleType == Battle.battleType.BATTLE_ALLIANCE_BATTLE_VALUE
					|| battleType == Battle.battleType.BATTLE_PVE_MULTI_ELITE_VALUE
					|| battleType == Battle.battleType.BATTLE_CROSS_SERVER_VALUE
					|| battleType == Battle.battleType.BATTLE_REBIRTH_BOSS_VALUE) {
				return roleInfo.getSkills2List();
			}

			if (battleType == Battle.battleType.BATTLE_PVP_ARENA_VALUE) {
				if (pos % 2 == 0) {
					return roleInfo.getSkills2List();
				}
				return roleInfo.getSkills3List();
			}
		}
		return roleInfo.getSkillsList();
	}

	public int getCarrySkillSize(int battleType) {
		int size = 0;
		for (RoleSkill roleSkill : getBattleSkills(battleType)) {
			if (roleSkill.getSkillId() > 0) {
				size++;
			}
		}
		return size;
	}

	public boolean beginSkillCd(int battleType, int skillId, int cool, int curRound) {
		if (skillId > 0) {
			skillCds.put(skillId, curRound + cool + getCarrySkillSize(battleType));
			return true;
		}
		return false;
	}

	public boolean canUseSkill(int skillId, int curRound) {
		if (!skillCds.containsKey(skillId) || curRound > skillCds.get(skillId)) {
			return true;
		}
		return false;
	}

	public boolean checkBuffValid(int buffId) {
		for (BattleBuff buff : buffs) {
			if (buff.getBuffId() == buffId && buff.isValid()) {
				return true;
			}
		}
		return false;
	}

	public BattleBuff getBuff(int buffId) {
		for (BattleBuff buff : buffs) {
			if (buff.getBuffId() == buffId && buff.isValid()) {
				return buff;
			}
		}
		return null;
	}

	public int decreaseBuffRound() {
		int count = 0;
		for (BattleBuff buff : buffs) {
			if (buff.isValid()) {
				count++;
				buff.decreaseRound();
			}
		}
		return count;
	}

	/**
	 * 添加buff;
	 * 
	 * @param buffId    buffId;
	 * @param buffRound 作用回合;
	 * @param overlap   叠加次数;
	 * @param args      参数列表;
	 * @return 如果添加成功返回对应的Buff，添加失败返回null；
	 */
	public BattleBuff addBuff(int buffId, int buffRound, boolean overlap, int... args) {
		BuffCfg buffCfg = ConfigManager.getInstance().getConfigByKey(BuffCfg.class, buffId);
		if (buffCfg == null) {
			return null;
		}

		// 多人副本的boss免疫眩晕、冰冻、沉默、中毒
		if (buffCfg.getId() == Const.Buff.DIZZINESS_VALUE || buffCfg.getId() == Const.Buff.FROST_VALUE
				|| buffCfg.getId() == Const.Buff.SILENCE_VALUE || buffCfg.getId() == Const.Buff.POISON_VALUE) {
			Object isTeamMapBoss = this.getSpecialFlag(GsConst.SkillSpecialFlag.MULTI_ELITE_MAP_BOSS);
			if (isTeamMapBoss != null && (Boolean) isTeamMapBoss) {
				return null;
			}
		}

		// 不良buff
		if (!buffCfg.isGain()) {
			// 狂暴免疫所有不良状态
			if (checkBuffValid(Const.Buff.RAGE_VALUE) || checkBuffValid(Const.Buff.FRENZY_VALUE)) {
				return null;
			}

			// 是否有免控神器属性
			if (this.getAttrValue(Const.attr.BUFF_AVOID_CONTROL) > 0) {
				try {
					int randVal = GuaJiRand.randInt(GsConst.RANDOM_BASE_VALUE, GsConst.RANDOM_MYRIABIT_BASE);
					if (randVal <= this.getAttrValue(Const.attr.BUFF_AVOID_CONTROL)) {
						return null;
					}
				} catch (Exception e) {
					MyException.catchException(e);
				}
			}
		}
		// ediby: crazyjohn 2015-5-23
		// 这里很费解，类型如果一样，那么就把相同类型的buffId都设置成一样？那么id和type的意义分别是什么？
		boolean isExist = false;
		for (BattleBuff buff : buffs) {
			if (buff.getBuffType() == buffCfg.getType()) {
				isExist = true;
				buff.setBuffId(buffCfg.getId());
				if (buffRound > buff.getBuffRound()) {
					buff.setBuffRound(buffRound);
				}
				for (int i = 0; i < args.length; i++) {
					buff.setBuffArg(i, args[i]);
				}
				// 叠加
				if (overlap) {
					buff.setOverlap(buff.getOverlap() + 1);
				}
				return buff;
			}
		}

		if (!isExist) {
			BattleBuff buff = new BattleBuff(buffCfg.getId(), buffCfg.getType(), buffRound);
			buff.setVisible(buffCfg.getVisible() > 0);
			buff.addBuffArg(args);
			buffs.add(buff);
			return buff;
		}
		return null;
	}

	public BattleBuff addOrReplaceBuff(int buffId, int buffRound, int... args) {
		// 不良buff
		if (buffId == 1 || buffId == 2 || buffId == 3) {
			// 狂暴免疫所有不良状态
			if (checkBuffValid(Const.Buff.RAGE_VALUE)) {
				return null;
			}

			// 是否有免控神器属性
			if (this.getAttrValue(Const.attr.BUFF_AVOID_CONTROL) > 0) {
				try {
					int randVal = GuaJiRand.randInt(GsConst.RANDOM_BASE_VALUE, GsConst.RANDOM_MYRIABIT_BASE);
					if (randVal <= this.getAttrValue(Const.attr.BUFF_AVOID_CONTROL)) {
						return null;
					}
				} catch (Exception e) {
					MyException.catchException(e);
				}
			}
		}

		BuffCfg buffCfg = ConfigManager.getInstance().getConfigByKey(BuffCfg.class, buffId);
		if (buffCfg == null) {
			return null;
		}

		Iterator<BattleBuff> buffIter = this.buffs.iterator();
		while (buffIter.hasNext()) {
			BattleBuff buff = buffIter.next();
			if (buff != null && buff.getBuffId() == buffId) {
				buffIter.remove();
			}
		}

		BattleBuff buff = new BattleBuff(buffCfg.getId(), buffCfg.getType(), buffRound);
		buff.setVisible(buffCfg.getVisible() > 0);
		buff.addBuffArg(args);
		buffs.add(buff);
		return buff;
	}

	/**
	 * 叠加buff
	 * 
	 * @param buffId
	 * @param overlap
	 */
	public void overlapBuff(int buffId, int overlap) {
		BuffCfg buffCfg = ConfigManager.getInstance().getConfigByKey(BuffCfg.class, buffId);
		if (buffCfg == null) {
			return;
		}

		for (BattleBuff buff : buffs) {
			if (buff.getBuffType() == buffCfg.getType()) {
				buff.setOverlap(buff.getOverlap() + overlap);
				break;
			}
		}
	}

	/**
	 * 移除buff editby: crazyjohn 2015-5-23这个api名字是移除buff，但是实际上没有做remove的动作，让人匪夷所思；
	 * 
	 * @param buffId
	 * @return
	 */
	public boolean removeBuff(int buffId) {
		Iterator<BattleBuff> iterator = buffs.iterator();
		while (iterator.hasNext()) {
			BattleBuff buff = iterator.next();
			if (buff.getBuffId() == buffId) {
				buff.setBuffRound(0);
				buff.setOverlap(0);
			}
		}
		return true;
	}

	/**
	 * 移除buff ；
	 * 
	 * @param buffId
	 * @return
	 */
	public boolean removeBuffById(int buffId) {
		Iterator<BattleBuff> iterator = buffs.iterator();
		while (iterator.hasNext()) {
			BattleBuff buff = iterator.next();
			if (buff.getBuffId() == buffId) {
				iterator.remove();
				break;
			}
		}
		return true;
	}

	/**
	 * 清除所有buff
	 * 
	 * @return
	 */
	public void removeAllBuff() {
		buffs.clear();
	}

	/**
	 * 移除损益buff
	 * 
	 * @param
	 * @return
	 */
	public boolean removeDebuff() {
		Iterator<BattleBuff> iterator = buffs.iterator();
		while (iterator.hasNext()) {
			BattleBuff buff = iterator.next();
			BuffCfg cfg = ConfigManager.getInstance().getConfigByKey(BuffCfg.class, buff.getBuffId());
			/** 表示损益buff */
			if (cfg.getGain() == 0) {
				iterator.remove();
			}
		}
		return true;
	}

	public int getRageRound() {
		return rageRound;
	}

	public float getRageAddition() {
		return rageAddition;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public void clearSkillCds() {
		this.skillCds.clear();
	}

	/**
	 * attch prof special buff
	 */
	public void addProfBuff() {
		int warriorBuffLevel = this.attribute.getValue(attr.BUFF_WARRIOR_VALUE);
		int hunterBuffLevel = this.attribute.getValue(attr.BUFF_HUNTER_VALUE);
		int magicBuffLevel = this.roleInfo.getSkillSpecializeLevel();
		if (warriorBuffLevel > 0) {
			this.addBuff(Const.Buff.WARRIOR_BUFF_VALUE, 100, true, warriorBuffLevel);
		} else if (hunterBuffLevel > 0) {
			this.addBuff(Const.Buff.HUNTER_BUFF_VALUE, 100, true, hunterBuffLevel);
		} else if (magicBuffLevel > 0) {
			this.addBuff(Const.Buff.MAGIC_BUFF_VALUE, 100, true, magicBuffLevel);
		}
	}

	public void setSpecialFlag(int type, Object value) {
		this.specialFlagMap.put(type, value);
	}

	public Object getSpecialFlag(int type) {
		return this.specialFlagMap.get(type);
	}

	public void clearSpecialFlagMap() {
		specialFlagMap.clear();
	}

	public void removeSkillSpecialFlag(int flag) {
		this.specialFlagMap.remove(flag);
	}

	public Attribute getAttribute() {
		return attribute;
	}

	/**
	 * 移除一个增益buff
	 */
	public int removeOneBuff() {
		Iterator<BattleBuff> buffIter = this.buffs.iterator();
		while (buffIter.hasNext()) {
			BattleBuff battleBuff = buffIter.next();
			BuffCfg buffCfg = ConfigManager.getInstance().getConfigByKey(BuffCfg.class, battleBuff.getBuffId());
			if (buffCfg != null && buffCfg.getGain() == 1 && buffCfg.getDispel() == 1) {
				buffIter.remove();
				return battleBuff.getBuffId();
			}
		}
		return 0;
	}

	/**
	 * 移除一个减益buff
	 */
	public int removeOneDeBuff() {
		Iterator<BattleBuff> buffIter = this.buffs.iterator();
		while (buffIter.hasNext()) {
			BattleBuff battleBuff = buffIter.next();
			BuffCfg buffCfg = ConfigManager.getInstance().getConfigByKey(BuffCfg.class, battleBuff.getBuffId());
			if (buffCfg != null && !buffCfg.isGain()) {
				buffIter.remove();
				return battleBuff.getBuffId();
			}
		}
		return 0;
	}

	/**
	 * 移除所有的增益buff,并返回所移除的BuffId
	 */
	public Set<Integer> removeAllGainBuff() {
		Set<Integer> set = new HashSet<Integer>();
		Iterator<BattleBuff> buffIter = this.buffs.iterator();
		while (buffIter.hasNext()) {
			BattleBuff battleBuff = buffIter.next();
			BuffCfg buffCfg = ConfigManager.getInstance().getConfigByKey(BuffCfg.class, battleBuff.getBuffId());
			if (buffCfg != null && buffCfg.getGain() == 1 && buffCfg.getDispel() == 1) {
				buffIter.remove();
				set.add(battleBuff.getBuffId());
			}
		}
		return set;
	}

	/**
	 * 获取所有的增益buff
	 */
	public Set<Integer> getGainBuffs() {
		Set<Integer> set = new HashSet<Integer>();
		Iterator<BattleBuff> buffIter = this.buffs.iterator();
		while (buffIter.hasNext()) {
			BattleBuff battleBuff = buffIter.next();
			BuffCfg buffCfg = ConfigManager.getInstance().getConfigByKey(BuffCfg.class, battleBuff.getBuffId());
			if (buffCfg != null && buffCfg.getGain() == 1 && buffCfg.getDispel() == 1) {
				set.add(battleBuff.getBuffId());
			}
		}
		return set;
	}

	/**
	 * 获取所有的增益buff
	 */
	public Set<Integer> getDeBuffs() {
		Set<Integer> set = new HashSet<Integer>();
		Iterator<BattleBuff> buffIter = this.buffs.iterator();
		while (buffIter.hasNext()) {
			BattleBuff battleBuff = buffIter.next();
			BuffCfg buffCfg = ConfigManager.getInstance().getConfigByKey(BuffCfg.class, battleBuff.getBuffId());
			if (buffCfg != null && !buffCfg.isGain() && battleBuff.isValid()) {
				set.add(battleBuff.getBuffId());
			}
		}
		return set;
	}

	/**
	 * 是否有有害buff
	 */
	public boolean hasDebuf() {
		for (BattleBuff buff : buffs) {
			BuffCfg cfg = ConfigManager.getInstance().getConfigByKey(BuffCfg.class, buff.getBuffId());
			if (!cfg.isGain() && buff.isValid()) {
				return true;
			}
		}
		return false;
	}

	public int isFrenzy() {
		return isFrenzy;
	}

	public void setFrenzy(int isFrenzy) {
		this.isFrenzy = isFrenzy;
	}

	public boolean isHelper() {
		return isHelper;
	}

	public void setHelper(boolean isHelper) {
		this.isHelper = isHelper;
	}

	public Map<Integer, Integer> getReviveTimes() {
		return reviveTimes;
	}

	/**
	 * @return 是否可复活
	 */
	public boolean isCanRevive() {

		for (Integer key : this.reviveTimes.keySet()) {
			Integer value = this.reviveTimes.get(key);
			if (value > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return 检测是否可复活 true 复活成功 false 复活失败
	 */
	public boolean revive() {

		if (this.getHp() > 0) {
			return false;
		}

		removeAllGainBuff();
		removeDebuff();

		// 可复活的被动技能
		for (Integer ringId : reviveTimes.keySet()) {
			Integer value = reviveTimes.get(ringId);
			RingCfg ringCfg = ConfigManager.getInstance().getConfigByKey(RingCfg.class, ringId);
			if (value > 0 && ringCfg != null) {
				if (ringCfg.getReviveAttrs() != null && ringCfg.getReviveAttrs().length() > 0) {
					List<AttrInfoObj> ringAttrInfoList = AttrInfoObj.valueOfs(ringCfg.getReviveAttrs());
					for (AttrInfoObj ringA : ringAttrInfoList) {
						if (ringA.getAttrType() != Const.attr.HP_VALUE && ringA.getAttrType() == Const.attr.MP_VALUE) {
							continue;
						}
						int addAttr = 0;
						if (ringA.getAddType() == GsConst.RingValueType.NUMERICAL_TYPE) {
							addAttr = ringA.getAttrValue();
						} else if (ringA.getAddType() == GsConst.RingValueType.PERCENTAGE_TYPE) {

							addAttr = (int) (getAttrValue(Const.attr.valueOf(ringA.getAttrType()))
									* (0.0001f * ringA.getAttrValue()));
						}
						if (ringA.getAttrType() == Const.attr.HP_VALUE) {
							setHp(getHp() + addAttr);
						} else if (ringA.getAttrType() == Const.attr.MP_VALUE) {
							setMp(getMp() + addAttr);
						}
					}
					// buff参数
					if (ringCfg.getBuffs() != null && !ringCfg.getBuffs().isEmpty()) {
						String[] items = ringCfg.getBuffs().split("_");
						if (items.length == 4) {

							this.addBuff(Integer.parseInt(items[0]), Integer.parseInt(items[1]), false,
									Integer.parseInt(items[2]), Integer.parseInt(items[3]));
						}
					}
				}

				state = GsConst.PersonState.PERSON_FIGHT;
				// 减少一次复活次数
				reviveTimes.put(ringId, value - 1);
				break;
			}
		}
		return this.getHp() > 0;
	}

}
