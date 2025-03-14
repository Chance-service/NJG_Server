package com.guaji.game.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.obj.ObjBase;
import org.guaji.os.GuaJiRand;
import org.guaji.os.MyException;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.attribute.AttrInfoObj;
import com.guaji.game.attribute.Attribute;
import com.guaji.game.config.ChatShieldCfg;
import com.guaji.game.config.ClassSoulCfg;
import com.guaji.game.config.ElementSoulCfg;
import com.guaji.game.config.EquipCfg;
import com.guaji.game.config.FetterCfg;
import com.guaji.game.config.FetterEquipCfg;
import com.guaji.game.config.FightValueCfg;
import com.guaji.game.config.GodlyAttrCfg;
import com.guaji.game.config.GuildSoulCfg;
import com.guaji.game.config.HeroAwakeCfg;
import com.guaji.game.config.LeaderSoulCfg;
import com.guaji.game.config.MottoCfg;
import com.guaji.game.config.NewSkillCfg;
import com.guaji.game.config.RoleCfg;
import com.guaji.game.config.RoleSkinCfg;
import com.guaji.game.config.SecretBuffCfg;
import com.guaji.game.config.SecretMsgCfg;
import com.guaji.game.config.SignCfg;
import com.guaji.game.config.SpriteSoulCfg;
import com.guaji.game.config.StarSoulCfg;
import com.guaji.game.config.SuitCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.config.TeamBuffCfg;
import com.guaji.game.entity.ArchiveEntity;
import com.guaji.game.entity.BadgeEntity;
import com.guaji.game.entity.EighteenPrincesEntity;
import com.guaji.game.entity.ElementEntity;
import com.guaji.game.entity.EquipEntity;
import com.guaji.game.entity.FormationEntity;
import com.guaji.game.entity.MottoEntity;
import com.guaji.game.entity.MutualEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.entity.PlayerStarSoulEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.entity.RoleSkinEntity;
import com.guaji.game.entity.SecretMsgEntity;
import com.guaji.game.entity.SignEntity;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.ProfRankManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.module.sevendaylogin.SevenDayQuestEventBus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.protocol.Attribute.Attr;
import com.guaji.game.protocol.Attribute.AttrInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Const.SevenDayEventType;
import com.guaji.game.protocol.Const.attr;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.EighteenPrinces.HPPlayerHelpMercenaryRet;
import com.guaji.game.protocol.EighteenPrinces.HelpItemInfo;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Player.HPCommentMsgRet;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Reward.HPPlayerReward;
import com.guaji.game.protocol.Reward.RewardInfo;
import com.guaji.game.protocol.Sign.SignRespones;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo.Builder;
import com.guaji.game.protocol.Status;

import net.sf.json.JSONObject;

public class PlayerUtil {
	/**
	 * 计算角色属性总战力
	 *
	 * @return
	 */
	public static int calcFightValue(RoleEntity roleEntity) {
		if (roleEntity == null) {
			return 0;
		}
		float value = getFightValue(roleEntity.getAttribute(),roleEntity.getIsMagic());
		roleEntity.setFightValue((int)value);
		return (int) value;
	}
	
	/**
	 * 计算角色属性裸體战力
	 *
	 * @return
	 */
	public static int calcNakedFightValue(RoleEntity roleEntity) {
		if (roleEntity == null) {
			return 0;
		}
		float value = getFightValue(roleEntity.getNakedattribute(),roleEntity.getIsMagic());
		roleEntity.setNakedFight((int)value);
		return (int) value;
	}

	/**
	 * 计算玩家总战斗力(激活英雄)不計算玩家
	 *
	 * @return
	 */
	public static int calcAllFightValue(PlayerData playerData) {

		List<RoleEntity> roleEntities = playerData.getRoleEntities();
		if (roleEntities == null)
			return 0;
		int allFightValue = 0;
		for (RoleEntity entity : roleEntities) {
			//if (entity.getType() == Const.roleType.MAIN_ROLE_VALUE
			if ((entity.getRoleState() == Const.RoleActiviteState.IS_ACTIVITE_VALUE) && entity.isArmy()) {
				allFightValue += calcFightValue(entity);
			}
		}

		return allFightValue;
	}
	
	/**
	 * 获取基础战力
	 *
	 * @param attrId
	 * @return
	 */
	public static float getFightValue(Attribute attribute,boolean IsMagic) {
		float value = 0;
		Map<Object, FightValueCfg> fightMap = ConfigManager.getInstance().getConfigMap(FightValueCfg.class);
		if (fightMap == null) {
			return 0;
		}
		int jumpAttr = IsMagic ? Const.attr.ATTACK_attr_VALUE : Const.attr.MAGIC_attr_VALUE;
		for (Entry<Object, FightValueCfg> cfg : fightMap.entrySet()) {
			FightValueCfg fightCfg = fightMap.get(cfg.getKey());
			if ((int) cfg.getKey() == jumpAttr) {
				continue;
			}
			int attrValue = attribute.getValue((int) cfg.getKey());
			
			if (attrValue != 0) {
				value += ((attrValue-fightCfg.getParam2())*fightCfg.getParam1()) ;
			}
		}
		return value;
	}
	
	/**
	 * 刷新裸體戰力值(MakerPlace專用)
	 */
	
//	public static int calcNudeFightValue(RoleEntity roleEntity) {
//		Attribute attribute = roleEntity.getAttribute();
//		attribute.clear();
//		// 躶体属性
//		attribute.add(roleEntity.getBaseAttr());
//		// 刷新通用属性
//		doNudeRefresh(roleEntity, attribute);
//		
//		return (int)getFightValue(roleEntity);
//	}
	

	/**
	 * 计算二级属性(次方法不可重复调用, 慎重)
	 *
	 * @param attribute
	 * @param profession
	 */
	public static void updateSecondaryAttribute(RoleEntity roleEntity,PlayerData playerData) {
		
		Attribute attribute = roleEntity.getAttribute();
		
		Attribute Nakedattribute = roleEntity.getNakedattribute();

		// 属性增加百分比
		Attribute additionAttrMap = new Attribute();
		Attribute addNakedAttrMap = new Attribute();
		Attribute ArenaAttr = roleEntity.getArenaAttr();
		Attribute ArenaAttrMap = null;

		// 光环属性加成（数值）
//		if (roleEntity != null) {
//			refreshRoleRingNumeric(playerData, roleEntity, attribute, false);
//		}

		double value = 0.0f;
		// 101
		value = attribute.getValue(Const.attr.STAMINA)*10;
		attribute.add(Const.attr.HP,(int)value);
		Nakedattribute.add(Const.attr.HP,(int)value);
		//106
		value = attribute.getValue(Const.attr.STRENGHT)*30+attribute.getValue(Const.attr.STAMINA)*50;
		value = value / 100;
		value = Math.round(value * 1.0f) / 1.0f;
		attribute.add(Const.attr.PHYDEF,(int)value);
		Nakedattribute.add(Const.attr.PHYDEF,(int)value);
		// 107
		value = attribute.getValue(Const.attr.INTELLECT)*30+attribute.getValue(Const.attr.STAMINA)*50;
		value = value / 100;
		value = Math.round(value * 1.0f) / 1.0f;
		attribute.add(Const.attr.MAGDEF,(int)value);
		Nakedattribute.add(Const.attr.MAGDEF,(int)value);
		// 108
		value = attribute.getValue(Const.attr.AGILITY)*50;
		value = value / 100;
		value = Math.round(value * 1.0f) / 1.0f;
		attribute.add(Const.attr.CRITICAL,(int)value);
		Nakedattribute.add(Const.attr.CRITICAL,(int)value);
		// 109
		value = attribute.getValue(Const.attr.STRENGHT)*20+attribute.getValue(Const.attr.INTELLECT)*20+
				attribute.getValue(Const.attr.AGILITY)*30;
		value = value / 100;
		value = Math.round(value * 1.0f) / 1.0f;
		attribute.add(Const.attr.HIT,(int)value);
		Nakedattribute.add(Const.attr.HIT,(int)value);
		// 110
		value = attribute.getValue(Const.attr.STRENGHT)*15+attribute.getValue(Const.attr.INTELLECT)*15+
				attribute.getValue(Const.attr.AGILITY)*30-attribute.getValue(Const.attr.STAMINA)*5;
		value = value / 100;
		value = Math.round(value * 1.0f) / 1.0f;
		attribute.add(Const.attr.DODGE,(int)value);
		Nakedattribute.add(Const.attr.DODGE,(int)value);
		//111
		value = attribute.getValue(Const.attr.STAMINA)*20;
		value = value / 100;
		value = Math.round(value * 1.0f) / 1.0f;
		attribute.add(Const.attr.RESILIENCE,(int)value);
		Nakedattribute.add(Const.attr.RESILIENCE,(int)value);
		// 113
		value = attribute.getValue(Const.attr.STRENGHT)*100+attribute.getValue(Const.attr.AGILITY)*25;
		value = value / 100;
		value = Math.round(value * 1.0f) / 1.0f;
		attribute.add(Const.attr.ATTACK_attr,(int)value);
		Nakedattribute.add(Const.attr.ATTACK_attr,(int)value);
		// 114
		value = attribute.getValue(Const.attr.INTELLECT)*100+attribute.getValue(Const.attr.AGILITY)*25;
		value = value / 100;
		value = Math.round(value * 1.0f) / 1.0f;
		attribute.add(Const.attr.MAGIC_attr,(int)value);
		Nakedattribute.add(Const.attr.MAGIC_attr,(int)value);

		// 神器属性修改最高血量上限
		if (attribute.containsAttr(Const.attr.HP) && attribute.containsAttr(Const.attr.BUFF_MAX_HP)) {
			int avalue = attribute.getValue(Const.attr.HP);
			int rate = attribute.getValue(Const.attr.BUFF_MAX_HP);
			avalue = (int) ((1.0f + rate * 0.0001f) * avalue);
			attribute.setAttr(Const.attr.HP, avalue);
		}

		// 计算神器加成属性
		if (attribute.containsAttr(attr.BUFF_PHYDEF_ADD)) {
			attribute.add(attr.PHYDEF,
					(int) ((long) attribute.getValue(attr.PHYDEF) * attribute.getValue(attr.BUFF_PHYDEF_ADD) / 10000));
		}

		if (attribute.containsAttr(attr.BUFF_MAGDEF_ADD)) {
			attribute.add(attr.MAGDEF,
					(int) ((long) attribute.getValue(attr.MAGDEF) * attribute.getValue(attr.BUFF_MAGDEF_ADD) / 10000));
		}

		if (attribute.containsAttr(attr.BUFF_CRITICAL_ADD)) {
			attribute.add(attr.CRITICAL, (int) ((long) attribute.getValue(attr.CRITICAL)
					* attribute.getValue(attr.BUFF_CRITICAL_ADD) / 10000));
		}

		if (attribute.containsAttr(attr.BUFF_RESILIENCE_ADD)) {
			attribute.add(attr.RESILIENCE, (int) ((long) attribute.getValue(attr.RESILIENCE)
					* attribute.getValue(attr.BUFF_RESILIENCE_ADD) / 10000));
		}

		if (attribute.containsAttr(attr.BUFF_DODGE_ADD)) {
			attribute.add(attr.DODGE,
					(int) ((long) attribute.getValue(attr.DODGE) * attribute.getValue(attr.BUFF_DODGE_ADD) / 10000));
		}

		if (attribute.containsAttr(attr.BUFF_ARMOR_ADD)) {
			attribute.add(attr.ARMOR,
					(int) ((long) attribute.getValue(attr.ARMOR) * attribute.getValue(attr.BUFF_ARMOR_ADD) / 10000));
		}

		if (attribute.containsAttr(attr.BUFF_HIT_ADD)) {
			attribute.add(attr.HIT,
					(int) ((long) attribute.getValue(attr.HIT) * attribute.getValue(attr.BUFF_HIT_ADD) / 10000));
		}

		if (attribute.containsAttr(attr.BUFF_SKILL_DAMAGE_ADD)) {
			attribute.add(attr.ATTACK_attr, (int) ((attribute.getValue(attr.BUFF_SKILL_DAMAGE_ADD) / 10000.0f)
					* attribute.getValue(attr.ATTACK_attr)));
			attribute.add(attr.MAGIC_attr, (int) ((attribute.getValue(attr.BUFF_SKILL_DAMAGE_ADD) / 10000.0f)
					* attribute.getValue(attr.MAGIC_attr)));
		}

		//////////////////////////////////////////////////////////////////////////////////////////////
		// 总体属性加成

		if (roleEntity != null) {
			// 计算当前星级配置
//			RoleUpStarCfg curStarCfg = RoleUpStarCfg.getRoleUpStarCfg(roleEntity.getItemId(), roleEntity.getStarLevel());
//			// 佣兵星级属性加成
//			if (curStarCfg != null) {
//				additionAttrMap.add(Const.attr.PHYDEF, curStarCfg.getPhyDefStarRatio());
//				additionAttrMap.add(Const.attr.HIT, curStarCfg.getHitStarRatio());
//				additionAttrMap.add(Const.attr.CRITICAL, curStarCfg.getCritStarRatio());
//				additionAttrMap.add(Const.attr.DODGE, curStarCfg.getDodgeStarRatio());
//				additionAttrMap.add(Const.attr.MAGDEF, curStarCfg.getMagicDefStarRatio());
//				additionAttrMap.add(Const.attr.HP, curStarCfg.getHpStarRatio());
//				additionAttrMap.add(Const.attr.RESILIENCE, curStarCfg.getResilienceStarRatio());
//				additionAttrMap.add(Const.attr.MINDMG, curStarCfg.getDamageStarRatio());
//				additionAttrMap.add(Const.attr.MAXDMG, curStarCfg.getDamageStarRatio());
//				additionAttrMap.add(Const.attr.MP, curStarCfg.getMpStarRatio());
//				additionAttrMap.add(Const.attr.ARMOR, curStarCfg.getArmorStarRatio());
//			}

			// 光环属性加成(百分比)
			//refreshRoleRingPercent(playerData, roleEntity, additionAttrMap, true);
		}

		// 刷新羈絆属性,相生武器
		if (roleEntity != null && roleEntity.isHero()) {
			// 羈絆
			ArchiveEntity archiveEntity = playerData.getArchiveEntity();
			if (archiveEntity != null && archiveEntity.getOpenFetters() != null) {
				List<AttrInfoObj> attrInfoList = new ArrayList<AttrInfoObj>();
				for (int id : archiveEntity.getOpenFetters()) {
					FetterCfg fetterCfg = ConfigManager.getInstance().getConfigByKey(FetterCfg.class, id);
					attrInfoList.addAll(AttrInfoObj.valueOfs(fetterCfg.getNewAttribute(playerData)));
				}
				addCommonAttr(attribute, additionAttrMap, attrInfoList, playerData.getPlayerEntity().getLevel());
				addCommonAttr(Nakedattribute, addNakedAttrMap, attrInfoList, playerData.getPlayerEntity().getLevel());
			}
			//相生武器
			MutualEntity mutualEntity = playerData.getMutualEntity();
			if (mutualEntity != null && mutualEntity.getOpenMutual() != null) {
				List<AttrInfoObj> attrInfoList = new ArrayList<AttrInfoObj>();
				for (int id : mutualEntity.getOpenMutual()) {
					FetterEquipCfg mutualCfg = ConfigManager.getInstance().getConfigByKey(FetterEquipCfg.class, id);
					attrInfoList.addAll(AttrInfoObj.valueOfs(mutualCfg.getNewAttribute(mutualEntity)));
				}
				addCommonAttr(attribute, additionAttrMap, attrInfoList, playerData.getPlayerEntity().getLevel());
				addCommonAttr(Nakedattribute, addNakedAttrMap, attrInfoList, playerData.getPlayerEntity().getLevel());
			}

		}
		
		// 計算元素陣型
		if (roleEntity != null && roleEntity.isHero()) {
			ArenaAttr.clear();
			ArenaAttr.add(attribute); // 計算陣形再把之前沒有計算一次加過來
			ArenaAttrMap = additionAttrMap.clone();
			
			FormationEntity formationEntity = playerData.getFormationByType(GsConst.FormationType.FormationBegin);
			if (formationEntity != null) {
				List<Integer> defaultItemIds = new ArrayList<>();
				defaultItemIds.addAll(formationEntity.getFightingArray());
				if (defaultItemIds.contains(roleEntity.getId())) {
					Map<Integer,Integer> attrmap = new HashMap<>();
					for (Integer roleId :defaultItemIds) {
						RoleEntity heroEntity = playerData.getMercenaryById(roleId);
						if (heroEntity != null) {
							int attr = heroEntity.getAttr();
							Integer counts = attrmap.get(attr);
							// ++counts要注意,是加完參數再導入
							attrmap.put(attr,counts == null ? 1 : ++counts);
						}
					}
					List<AttrInfoObj> attrInfoList = new ArrayList<AttrInfoObj>();
				
					attrInfoList = TeamBuffCfg.getMatchBuff(attrmap);
					addCommonAttr(attribute, additionAttrMap, attrInfoList, playerData.getPlayerEntity().getLevel());
					addCommonAttr(Nakedattribute, addNakedAttrMap, attrInfoList, playerData.getPlayerEntity().getLevel());
				}
			}
			
			FormationEntity ArenaformationEntity = playerData.getFormationByType(GsConst.FormationType.FormationEnd);
			if (ArenaformationEntity != null) {
				List<Integer> defaultItemIds = new ArrayList<>();
				defaultItemIds.addAll(ArenaformationEntity.getFightingArray());
				if (defaultItemIds.contains(roleEntity.getId())) {
					Map<Integer,Integer> attrmap = new HashMap<>();
					for (Integer roleId :defaultItemIds) {
						RoleEntity heroEntity = playerData.getMercenaryById(roleId);
						if (heroEntity != null) {
							int attr = heroEntity.getAttr();
							Integer counts = attrmap.get(attr);
							attrmap.put(attr,counts == null ? 1 : ++counts);
						}
					}
					List<AttrInfoObj> attrInfoList = new ArrayList<AttrInfoObj>();
				
					attrInfoList = TeamBuffCfg.getMatchBuff(attrmap);
					addCommonAttr(ArenaAttr, ArenaAttrMap, attrInfoList, playerData.getPlayerEntity().getLevel());
				}
			}
			
			// 永久技能屬性------------------------------------------------------------
			if (roleEntity.isArmy()) { //英雄 ,小精靈 有%乘數的不可亂移動
				List<Integer> SkillList = roleEntity.getSkillAll();
				for (Integer itemId :SkillList) {
					NewSkillCfg skillcfg =  ConfigManager.getInstance().getConfigByKey(NewSkillCfg.class, itemId);
					if (skillcfg != null) {
						int need_role = skillcfg.getRole_need();
						if (need_role != 0) {
							RoleEntity needEntity = playerData.getMercenaryByItemId(need_role);
							if (needEntity == null) {
								continue;
							}
							formationEntity = playerData.getFormationByType(GsConst.FormationType.FormationBegin);
							if (formationEntity == null) {
								continue;
							}
							if(!formationEntity.getFightingArray().contains(roleEntity.getId())) {
								continue;
							}
							if(!formationEntity.getFightingArray().contains(needEntity.getId())) {
								continue;
							}
						}
						List<AttrInfoObj> attrInfoList = skillcfg.getAttrInfoList();
						if (attrInfoList != null) {
							//Attribute additionAttr = new Attribute();
							addCommonAttr(attribute, additionAttrMap, attrInfoList, playerData.getPlayerEntity().getLevel());
							addCommonAttr(Nakedattribute, addNakedAttrMap, attrInfoList, playerData.getPlayerEntity().getLevel());
							//attribute.additionAttr(additionAttr);
						}
					}
				}
				
				
				for (Integer itemId :SkillList) {
					NewSkillCfg skillcfg =  ConfigManager.getInstance().getConfigByKey(NewSkillCfg.class, itemId);
					if (skillcfg != null) {
						int need_role = skillcfg.getRole_need();
						if (need_role != 0) {
							RoleEntity needEntity = playerData.getMercenaryByItemId(need_role);
							if (needEntity == null) {
								continue;
							}
							ArenaformationEntity = playerData.getFormationByType(GsConst.FormationType.FormationEnd);
							if (ArenaformationEntity == null) {
								continue;
							}
							if(!ArenaformationEntity.getFightingArray().contains(roleEntity.getId())) {
								continue;
							}
							if(!ArenaformationEntity.getFightingArray().contains(needEntity.getId())) {
								continue;
							}
						}
						List<AttrInfoObj> attrInfoList = skillcfg.getAttrInfoList();
						if (attrInfoList != null) {
							//Attribute additionAttr = new Attribute();
							addCommonAttr(ArenaAttr, ArenaAttrMap, attrInfoList, playerData.getPlayerEntity().getLevel());
							//ArenaAttr.additionAttr(additionAttr);
						}
					}
				}
				
				formationEntity = playerData.getFormationByType(GsConst.FormationType.FormationBegin);
				if (formationEntity != null) {
					// 在隊伍內有加強同屬性的技能
					if (formationEntity.getFightingArray().contains(roleEntity.getId())) {
						Map<Integer,Integer> elementMap = new HashMap<>();
						for (int roleId : formationEntity.getFightingArray()) {
							if (roleId == 0) {
								continue;
							}
							RoleEntity heroEntity = playerData.getMercenaryById(roleId);
						    if (heroEntity == null) {
						    	continue;
						    }
						   
						    for (Integer itemId :heroEntity.getSkillAll()) {
						    	NewSkillCfg skillcfg =  ConfigManager.getInstance().getConfigByKey(NewSkillCfg.class, itemId);
						    	if (skillcfg == null) {
						    		continue;
						    	}
						    	if (skillcfg.getElement() == 0) {
						    		continue;
						    	}
						    	int  element = skillcfg.getElement();
						    	if ((elementMap.containsKey(element))&&(elementMap.get(element) < skillcfg.getId())) {
						    		elementMap.replace(element, skillcfg.getId());
						    	} else {
						    		elementMap.put(element,skillcfg.getId());
						    	}
						    }
						}
						if (elementMap.containsKey(roleEntity.getAttr())) {
							int skillId = elementMap.get(roleEntity.getAttr());
							if (skillId > 0) {
								NewSkillCfg skillcfg =  ConfigManager.getInstance().getConfigByKey(NewSkillCfg.class, skillId);
								if (skillcfg != null) {
									List<AttrInfoObj> attrInfoList = skillcfg.getAttrInfoList();
									if (attrInfoList != null) {
										//Attribute additionAttr = new Attribute();
										addCommonAttr(attribute, additionAttrMap, attrInfoList, playerData.getPlayerEntity().getLevel());
										addCommonAttr(Nakedattribute, addNakedAttrMap, attrInfoList, playerData.getPlayerEntity().getLevel());
										//ArenaAttr.additionAttr(additionAttr);
									}
								}
							}
						}
					}
				}
				
				ArenaformationEntity = playerData.getFormationByType(GsConst.FormationType.FormationEnd);
				if (ArenaformationEntity != null) {
					// 在隊伍內有加強同屬性的技能
					if (ArenaformationEntity.getFightingArray().contains(roleEntity.getId())) {
						Map<Integer,Integer> elementMap = new HashMap<>();
						for (int roleId : ArenaformationEntity.getFightingArray()) {
							if (roleId == 0) {
								continue;
							}
							RoleEntity heroEntity = playerData.getMercenaryById(roleId);
						    if (heroEntity == null) {
						    	continue;
						    }
						   
						    for (Integer itemId :heroEntity.getSkillAll()) {
						    	NewSkillCfg skillcfg =  ConfigManager.getInstance().getConfigByKey(NewSkillCfg.class, itemId);
						    	if (skillcfg == null) {
						    		continue;
						    	}
						    	if (skillcfg.getElement() == 0) {
						    		continue;
						    	}
						    	int  element = skillcfg.getElement();
						    	if ((elementMap.containsKey(element))&&(elementMap.get(element) < skillcfg.getId())) {
						    		elementMap.replace(element, skillcfg.getId());
						    	} else {
						    		elementMap.put(element,skillcfg.getId());
						    	}
						    }
						}
						if (elementMap.containsKey(roleEntity.getAttr())) {
							int skillId = elementMap.get(roleEntity.getAttr());
							if (skillId > 0) {
								NewSkillCfg skillcfg =  ConfigManager.getInstance().getConfigByKey(NewSkillCfg.class, skillId);
								if (skillcfg != null) {
									List<AttrInfoObj> attrInfoList = skillcfg.getAttrInfoList();
									if (attrInfoList != null) {
										//Attribute additionAttr = new Attribute();
										addCommonAttr(ArenaAttr, ArenaAttrMap, attrInfoList, playerData.getPlayerEntity().getLevel());
										//ArenaAttr.additionAttr(additionAttr);
									}
								}
							}
						}
					}
				}
			}
		}
		
		// 加成运算
		attribute.additionAttr(additionAttrMap);
		Nakedattribute.additionAttr(addNakedAttrMap);
		ArenaAttr.additionAttr(ArenaAttrMap);
	}

	/**
	 * 刷新佣兵光环百分比加成
	 *
	 * @param playerData
	 * @param roleEntity
	 * @param attribute
	 * @param additionAttrMap
	 * @param                 isMumerical(是否是百分比)
	 */
//	private static void refreshRoleRingPercent(PlayerData playerData, RoleEntity roleEntity, Attribute additionAttrMap,
//			boolean isMumerical) {
//
//		List<Integer> ringList = getRingAttrList(roleEntity, playerData);
//
//		if (roleEntity.isHero()) {
//			for (Integer ringId : ringList) {
//				RingCfg ringCfg = ConfigManager.getInstance().getConfigByKey(RingCfg.class, ringId);
//				if (ringCfg != null && ringCfg.isAddAttr()) {
//					List<AttrInfoObj> ringAttrInfoList = AttrInfoObj.valueOfs(ringCfg.getAttrs());
//					for (AttrInfoObj ringA : ringAttrInfoList) {
//						if (isMumerical) {
//							if (ringA.getAddType() == GsConst.RingValueType.NUMERICAL_TYPE) {
//								additionAttrMap.add(Const.attr.valueOf(ringA.getAttrType()), ringA.getAttrValue());
//							}
//						}
//					}
//				}
//			}
//		} else {
//			for (Integer ringId : ringList) {
//				RingCfg ringCfg = ConfigManager.getInstance().getConfigByKey(RingCfg.class, ringId);
//				if (ringCfg != null && ringCfg.isAddAttr()) {
//					if (ringCfg.getType() == GsConst.RingAttType.ROLE_MERCENARY_TYPE) {
//						List<AttrInfoObj> ringAttrInfoList = AttrInfoObj.valueOfs(ringCfg.getAttrs());
//						for (AttrInfoObj ringA : ringAttrInfoList) {
//							if (isMumerical) {
//								if (ringA.getAddType() == GsConst.RingValueType.NUMERICAL_TYPE) {
//									additionAttrMap.add(Const.attr.valueOf(ringA.getAttrType()), ringA.getAttrValue());
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//	}

	/**
	 * 获取佣兵光环
	 *
	 * @param roleEntity
	 * @param playerData
	 * @return
	 */
//	private static List<Integer> getRingAttrList(RoleEntity roleEntity, PlayerData playerData) {
//		List<Integer> ringList = new ArrayList<>();
//		// role
//		if (roleEntity.isHero()) {
//			ringList = roleEntity.getRingList();
//		} else {
//			List<RoleEntity> roleEntities = playerData.getRoleEntities();
//			// 所有光环ID
//			for (RoleEntity entity : roleEntities) {
////				if (entity.getRoleState() == Const.RoleActiviteState.IS_ACTIVITE_VALUE) {
//					ringList.addAll(entity.getRingList());
////				}
//			}
//		}
//		return ringList;
//	}

	/**
	 * 刷新佣兵光环数值加成
	 *
	 * @param playerData
	 * @param roleEntity
	 * @param attribute
	 * @param additionAttrMap
	 * @param                 isMumerical(是否是百分比)
	 */
//	private static void refreshRoleRingNumeric(PlayerData playerData, RoleEntity roleEntity, Attribute attribute,
//			boolean isMumerical) {
//
//		List<Integer> ringList = getRingAttrList(roleEntity, playerData);
//
//		if (roleEntity.isHero()) {
//			for (Integer ringId : ringList) {
//				RingCfg ringCfg = ConfigManager.getInstance().getConfigByKey(RingCfg.class, ringId);
//				if (ringCfg != null && ringCfg.isAddAttr()) {
//					List<AttrInfoObj> ringAttrInfoList = AttrInfoObj.valueOfs(ringCfg.getAttrs());
//					for (AttrInfoObj ringA : ringAttrInfoList) {
//						if (!isMumerical) {
//							if (ringA.getAddType() == GsConst.RingValueType.NUMERICAL_TYPE) {
//								attribute.add(ringA.getAttrType(), ringA.getAttrValue());
//							} else if (ringA.getAddType() == GsConst.RingValueType.PERCENTAGE_GRADE_TYPE) {
//								// 数值 * 等级
//								attribute.add(ringA.getAttrType(), ringA.getAttrValue() * roleEntity.getLevel());
//							}
//						}
//					}
//				}
//			}
//		} else {
//			for (Integer ringId : ringList) {
//				RingCfg ringCfg = ConfigManager.getInstance().getConfigByKey(RingCfg.class, ringId);
//				if (ringCfg != null && ringCfg.isAddAttr()) {
//					List<AttrInfoObj> ringAttrInfoList = AttrInfoObj.valueOfs(ringCfg.getAttrs());
//					if (ringCfg.getType() == GsConst.RingAttType.ROLE_MERCENARY_TYPE) {
//						for (AttrInfoObj ringA : ringAttrInfoList) {
//							if (!isMumerical) {
//								if (ringA.getAddType() == GsConst.RingValueType.PERCENTAGE_TYPE) {
//									attribute.add(ringA.getAttrType(), ringA.getAttrValue());
//								} else if (ringA.getAddType() == GsConst.RingValueType.PERCENTAGE_GRADE_TYPE) {
//									// 数值 * 等级
//									attribute.add(ringA.getAttrType(), ringA.getAttrValue() * roleEntity.getLevel());
//								}
//							}
//						}
//					}
//				}
//			}
//
//		}
//	}

	/**
	 * 刷新离线玩家属性;
	 *
	 * @param playerData
	 * @param roleEntity
	 * @return
	 */
	public static Attribute refreshOfflineAttribute(PlayerData playerData, RoleEntity roleEntity) {
		// 刷新通用属性
		doAttributesRefresh(playerData,roleEntity,false);
		
		return roleEntity.getAttribute();
	}

	/**
	 * 单独刷新主角战力 (比如佣兵出战以后，只有等属性刷新以后 pleayerEntity.getFightValue(),才会刷新)
	 */
	public static void refreshFightValue(PlayerData playerData) {
		// oldFightValue
		int oldFightValue = playerData.getPlayerEntity().getFightValue();
		// newFightValue
		int newFightValue = PlayerUtil.calcAllFightValue(playerData);
		// update total FightValue
		if (newFightValue != oldFightValue) {
			playerData.getPlayerEntity().setFightValue(newFightValue);
			playerData.getPlayerEntity().notifyUpdate();
			ProfRankManager manager = ProfRankManager.getInstance();
			int prof = playerData.getPlayerEntity().getProf();
			int minFightValue = manager.getMinFightValueFromByType(prof);
			if (newFightValue > minFightValue || manager.getPlayerRank(playerData, prof) >= 0) {
				Msg msg = Msg.valueOf(GsConst.MsgType.FIGHT_VALUE_CHANGE,
						GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.PROFRANK));
				msg.pushParam(playerData);
				GsApp.getInstance().postMsg(msg);
			}
			// 7日之诗战力提升
			if (newFightValue > oldFightValue)
				SevenDayQuestEventBus.fireQuestEvent(SevenDayEventType.MAINROLE_SCORE, newFightValue,
						GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerData.getPlayerEntity().getId()));

			// adjust 战力达到3万
			if (newFightValue > oldFightValue && oldFightValue < 30000 && newFightValue >= 30000) {
				playerData.syncAdjustScoreEvent();

			}

			QuestEventBus.fireQuestEvent(QuestEventType.FIGHT_POINT_UP, newFightValue,
					GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerData.getPlayerEntity().getId()));
		}
	}

	/**
	 * 刷新在线玩家角色属性
	 *
	 * @param roleEntit
	 */
	public static Attribute refreshOnlineAttribute(PlayerData playerData, RoleEntity roleEntity) {
//		Attribute attribute = roleEntity.getAttribute();
//		attribute.clearall();
//		// 裸体属性
//		attribute.add(roleEntity.getBaseAttr());

		// 刷新通用属性
		doAttributesRefresh(playerData, roleEntity,true);

		// 檢測總戰力改變
		refreshFightValue(playerData);
		
		// 計算單一角色裸體戰力變化
		calcNakedFightValue(roleEntity);

		// 在线玩家快照刷新
		playerData.refreshOnlinePlayerSnapshot();
		
		return roleEntity.getAttribute();
	}
	
	/**
	 * 抽出来的通用的刷新属性方法; {@link #refreshOnlineAttribute(PlayerData, RoleEntity)}
	 * {@link #refreshOfflineAttribute(PlayerData, RoleEntity)} 都来调用它实现刷新逻辑;
	 *
	 * @param playerData
	 * @param roleEntity
	 * @param attribute
	 */
	private static void doAttributesRefresh(PlayerData playerData, RoleEntity roleEntity,boolean isOnline) {
		//int prof = playerData.getMainRole().getProfession();
		Attribute attribute = roleEntity.getAttribute();
		attribute.clear();
		Attribute Nakedattribute = roleEntity.getNakedattribute();
		Nakedattribute.clear();
		// 基礎属性
		attribute.add(roleEntity.getBaseAttr());
		Nakedattribute.add(roleEntity.getBaseAttr());
		// 装备属性
		for (int i = Const.equipPart.HELMET_VALUE; i <= Const.equipPart.NECKLACE_VALUE; i++) {
			EquipEntity equipEntity = playerData.getEquipById(roleEntity.getPartEquipId(i));
			if (equipEntity != null) {
				Attribute equipAttribute = EquipUtil.refreshAttribute(equipEntity, playerData);
				if (equipAttribute != null) {
					attribute.add(equipAttribute);
				}
			}
		}


		// 徽章(符文)属性
		for (Long badge : roleEntity.getBadgeMap().values()) {
			if (badge == 0) {
				continue;
			}
			BadgeEntity badgeEntity = playerData.getBadgeById(badge);
			if (badgeEntity != null) {
				Attribute badgeAttribute = badgeEntity.getAttribute();//BadgeUtil.refreshAttribute(badgeEntity, playerData);
				if (badgeAttribute != null) {
					attribute.add(badgeAttribute);
					Nakedattribute.add(badgeAttribute);
				}
			}
		}
		
		//箴言屬性
		for (MottoEntity mottoEntity: playerData.getActMotto()) {
			int itemId = mottoEntity.getItemId();
			int star = mottoEntity.getStar();
			MottoCfg cfg = ConfigManager.getInstance().getConfigByKey(MottoCfg.class, itemId);
			if (cfg == null) {
				continue;
			}
			Attribute mottoAttribute = cfg.getAttributeByStar(playerData, star);
			if (mottoAttribute != null) {
				attribute.add(mottoAttribute);
				Nakedattribute.add(mottoAttribute);
			}
		}
		
		
		if (roleEntity.isHero()) {
			// 秘密信條屬性
//			Map<Integer,Integer> valueMap = PlayerUtil.calcSecretMsgValue(playerData,roleEntity.getItemId());
//			Attribute secretAttribute = getSecretMsgAttr(valueMap);
//			attribute.add(secretAttribute);
			
			if (playerData.isJoinGuild()) {
				Attribute guildBuffAttribute = getGuildBuffAttr(playerData,roleEntity.getProfession());
				if (guildBuffAttribute != null) {
					attribute.add(guildBuffAttribute);
					Nakedattribute.add(guildBuffAttribute);
				}
			}
			// 英雄覺醒
			HeroAwakeCfg AwakeCfg = roleEntity.getAwakeCfg();
			if (AwakeCfg != null) {
				attribute.add(AwakeCfg.getAttribute());
				Nakedattribute.add(AwakeCfg.getAttribute());
			}
		}
		
		List<SuitCfg> suitCfgs = PlayerUtil.calcEffectSuits(playerData, roleEntity);
		
	
		// 套装信息
			//List<SuitCfg> suitCfgs = PlayerUtil.calcEffectSuits(playerData, roleEntity);
		for (SuitCfg suiCfg : suitCfgs) {
			// 等级无关
			attribute.add(suiCfg.getSuitAttr());
			// 等级有关
			Map<attr, Integer> attrMap = suiCfg.getSuitAttrLevel().getAttrMap();
			if (attrMap != null && attrMap.size() > 0) {
				for (Entry<attr, Integer> att : attrMap.entrySet()) {
					attribute.add(att.getKey(), att.getValue() * playerData.getPlayerEntity().getLevel());
				}
			}
		}

				
		// 只有主角才有元素属性
		if (roleEntity.getType() == GsConst.RoleType.MAIN_ROLE) {
			// 元素信息
			for (int i = 0; i < SysBasicCfg.getInstance().getMaxElementSize(); i++) {
				long eleId = roleEntity.getElementByIndex(i);
				if (eleId > 0) {
					ElementEntity elementEntity = playerData.getElementById(eleId);
					if (elementEntity != null) {
						attribute.add(elementEntity.getAttribute());
						Nakedattribute.add(elementEntity.getAttribute());
					}
				}
			}

			// 真气系统加成属性
//			PlayerTalentEntity playerTalentEntity = playerData.loadPlayerTalentEntity();
//			Map<attr, TalentElementAttr> talentMap = playerTalentEntity.getTalentMap();
//			Iterator<TalentElementAttr> iterator = talentMap.values().iterator();
//			while (iterator.hasNext()) {
//				PlayerTalentEntity.TalentElementAttr talentElementAttr = (PlayerTalentEntity.TalentElementAttr) iterator
//						.next();
//				if (talentElementAttr.getTalentAttrvalue() == 0) {
//					continue;
//				}
//				attribute.add(talentElementAttr.getElementId(), talentElementAttr.getTalentAttrvalue());
//				Nakedattribute.add(talentElementAttr.getElementId(), talentElementAttr.getTalentAttrvalue());
//			}

			// 翅膀系统加成属性
//			PlayerWingsEntity wingsEntity = playerData.loadPlayerWingsEntity();
//			if (wingsEntity.getLevel() != 0) {
//				Map<attr, Integer> attrMap = WingsCfg.getAttrMap(PlayerWingModule.WING_TYPE, wingsEntity.getLevel(),
//						prof);
//				for (attr eachAttr : attrMap.keySet()) {
//					attribute.add(eachAttr, attrMap.get(eachAttr));
//					Nakedattribute.add(eachAttr, attrMap.get(eachAttr));
//				}
//			}
		}

//		// 月卡未到期
//		if (playerData.isMonthCard() != null) {
//			MonthCardCfg cfg = ConfigManager.getInstance().getConfigByKey(MonthCardCfg.class,
//					playerData.isMonthCard().getMonthCardId());
//			if (cfg != null) {
//				attribute.add(Const.attr.BUFF_EXP_DROP, (int) cfg.getAddExpBuff());
//			}
//		}
//
//		// 周卡
//		if (playerData.isWeekCardAttr() != 0) {
//			attribute.add(Const.attr.BUFF_EXP_DROP, playerData.isWeekCardAttr());
//		}

		// 消耗型经验加成
//		if (playerData.isConsumeWeekCardAttr() != 0) {
//			attribute.add(Const.attr.BUFF_EXP_DROP, playerData.isConsumeWeekCardAttr());
//		}

		// 佣兵培养属性加成
//		if (roleEntity.isHero()) {
//			Attribute baptizeAttr = roleEntity.getRoleBaptizeAttr();
//			if (baptizeAttr != null) {
//				attribute.add(baptizeAttr);
//			}
//		}

		// 激活星魂(聖所)后隊伍的属性加成
		PlayerStarSoulEntity SoulEntity = playerData.loadPlayerStarSoulEntity();
		if (roleEntity.isHero()) {
			
			Map<Integer, Integer> map = SoulEntity.getStarSoulMap();
			for (Entry<Integer, Integer> entry : map.entrySet()) {
				StarSoulCfg cfg = StarSoulCfg.getCfg(entry.getKey(), entry.getValue());
				attribute.add(cfg.getAttribute());
				Nakedattribute.add(cfg.getAttribute());
			}
			
			map = SoulEntity.getLeaderSoulMap();
			for (Entry<Integer, Integer> entry : map.entrySet()) {
				LeaderSoulCfg cfg = LeaderSoulCfg.getCfg(entry.getKey(), entry.getValue());
				attribute.add(cfg.getAttribute());
				Nakedattribute.add(cfg.getAttribute());
			}
			
			map = SoulEntity.getClassSoulMap();
			int key = roleEntity.getProfession();
			if (map.containsKey(key)) {
				ClassSoulCfg cfg = ClassSoulCfg.getCfg(key,map.get(key));
				attribute.add(cfg.getAttribute());
				Nakedattribute.add(cfg.getAttribute());
			}
			
			map = SoulEntity.getElementSoulMap();
			key = roleEntity.getElement();
			if (map.containsKey(key)) {
				ElementSoulCfg cfg = ElementSoulCfg.getCfg(key,map.get(key));
				attribute.add(cfg.getAttribute());
				Nakedattribute.add(cfg.getAttribute());
			}
		}
		// 精靈島的屬性加成
		if (roleEntity.isSprite()) {
			Map<Integer, Integer> spritemap = SoulEntity.getSpriteSoulMap();
			for (Entry<Integer, Integer> entry : spritemap.entrySet()) {
				SpriteSoulCfg cfg = SpriteSoulCfg.getCfg(entry.getKey(), entry.getValue());
				attribute.add(cfg.getAttribute());
				Nakedattribute.add(cfg.getAttribute());
			}
		}
		// 英雄角色皮膚屬性加成
		if (roleEntity.isHero()) {
			RoleSkinEntity roleSkinEntity = playerData.loadRoleSkinEntity();
			Set<Integer> aSet = roleSkinEntity.getSkinSet();
			for (Integer skinId : aSet) {
				// 擁有全隊皮膚屬性
				RoleSkinCfg cfg = ConfigManager.getInstance().getConfigByKey(RoleSkinCfg.class, skinId);
				if (cfg != null) {
					attribute.add(cfg.getTeamAttr());
					if (roleEntity.getSkinId() ==  skinId) { // 角色裝備獲得屬性
						attribute.add(cfg.getOwnAttr());
						Nakedattribute.add(cfg.getOwnAttr());
					}
				}
			}
		}

		// 刷新
		if ((roleEntity == playerData.getMainRole())) {
			updateGodlyAttrAttribute(playerData, roleEntity);
		}

		// 缘分加成属性
		/**/
//		List<Integer> allMercenary = new ArrayList<>();
//		if (playerData.getFormationByType(1) != null) {
//			playerData.getFormationByType(1).getFightingArray().stream().filter(item -> item != 0)
//					.forEach(item -> allMercenary.add(item));
////			playerData.getFormationByType(1).getAssistanceArrayList().stream().filter(item -> item != 0)
////					.forEach(item -> allMercenary.add(item));
//			if (luckByMercenaryGroup.getRoleLuckyItems().containsKey(roleEntity.getItemId())
//					&& allMercenary.contains(roleEntity.getItemId())) {
//				Optional.ofNullable(luckByMercenaryGroup.getRoleLuckyItems().get(roleEntity.getItemId()))
//						.ifPresent(luckGroupList -> {
//							luckGroupList.forEach(luckItem -> {
//								System.out.println("allMercenary" + allMercenary.toString());
//								System.out.println("getRefMercenaryIds" + luckItem.getRefMercenaryIds().toString());
//								if (allMercenary.containsAll(luckItem.getRefMercenaryIds())) {
//									luckItem.getAttrs().forEach((key, value) -> {
//										attribute.add(key, value);
//									});
//
//								}
//							});
//						});
//			}
//
//		}

		int iceAttackRatio = attribute.getValue(Const.attr.ICE_ATTACK_RATIO);
		if (iceAttackRatio > 0) {
			attribute.set(Const.attr.ICE_ATTACK,
					(int) (attribute.getValue(Const.attr.ICE_ATTACK) * (1 + iceAttackRatio * 0.0001f)));
			Nakedattribute.set(Const.attr.ICE_ATTACK,
					(int) (attribute.getValue(Const.attr.ICE_ATTACK) * (1 + iceAttackRatio * 0.0001f)));
		}
		int fireAttackRatio = attribute.getValue(Const.attr.FIRE_ATTACK_RATIO);
		if (fireAttackRatio > 0) {
			attribute.set(Const.attr.FIRE_ATTACK,
					(int) (attribute.getValue(Const.attr.FIRE_ATTACK) * (1 + fireAttackRatio * 0.0001f)));
			Nakedattribute.set(Const.attr.FIRE_ATTACK,
					(int) (attribute.getValue(Const.attr.FIRE_ATTACK) * (1 + fireAttackRatio * 0.0001f)));
		}
		int thunderAttackRatio = attribute.getValue(Const.attr.THUNDER_ATTACK_RATIO);
		if (thunderAttackRatio > 0) {
			attribute.set(Const.attr.THUNDER_ATTACK,
					(int) (attribute.getValue(Const.attr.THUNDER_ATTACK) * (1 + thunderAttackRatio * 0.0001f)));
			Nakedattribute.set(Const.attr.THUNDER_ATTACK,
					(int) (attribute.getValue(Const.attr.THUNDER_ATTACK) * (1 + thunderAttackRatio * 0.0001f)));
		}
		int iceDefRatio = attribute.getValue(Const.attr.ICE_DEFENCE_RATIO);
		if (iceDefRatio > 0) {
			attribute.set(Const.attr.ICE_DEFENCE,
					(int) (attribute.getValue(Const.attr.ICE_DEFENCE) * (1 + iceDefRatio * 0.0001f)));
			Nakedattribute.set(Const.attr.ICE_DEFENCE,
					(int) (attribute.getValue(Const.attr.ICE_DEFENCE) * (1 + iceDefRatio * 0.0001f)));
		}
		int fireDefRatio = attribute.getValue(Const.attr.FIRE_DEFENCE_RATIO);
		if (fireDefRatio > 0) {
			attribute.set(Const.attr.FIRE_DEFENCE,
					(int) (attribute.getValue(Const.attr.FIRE_DEFENCE) * (1 + fireDefRatio * 0.0001f)));
			Nakedattribute.set(Const.attr.FIRE_DEFENCE,
					(int) (attribute.getValue(Const.attr.FIRE_DEFENCE) * (1 + fireDefRatio * 0.0001f)));
		}
		int thunderDefRatio = attribute.getValue(Const.attr.THUNDER_DENFENCE_RATIO);
		if (thunderDefRatio > 0) {
			attribute.set(Const.attr.THUNDER_DENFENCE,
					(int) (attribute.getValue(Const.attr.THUNDER_DENFENCE) * (1 + thunderDefRatio * 0.0001f)));
			Nakedattribute.set(Const.attr.THUNDER_DENFENCE,
					(int) (attribute.getValue(Const.attr.THUNDER_DENFENCE) * (1 + thunderDefRatio * 0.0001f)));
		}

		// 计算二级属性
		updateSecondaryAttribute(roleEntity, playerData);

		// 刷新
//		if (roleEntity == playerData.getMainRole()) {
//			List<SkillEntity> skillEntities = playerData.getSkillEntities();
//			if (skillEntities != null) {
//				// 刷新套装技能影响
//				for (SkillEntity skillEntity : playerData.getSkillEntities()) {
//					boolean isSkillLevelChange = false;
//					int skillLevel = 0;
//					for (SuitCfg suitCfg : suitCfgs) {
//						int eSkillLevel = suitCfg.getSkillEnhanceLevel(skillEntity.getItemId());
//						if (eSkillLevel > 0) {
//							skillLevel += eSkillLevel;
//							isSkillLevelChange = true;
//						}
//					}
//					int oldSkillEnhanceSkillLevel = skillEntity.getSkillEnhanceLevel();
//					skillEntity.setSkillEnhanceLevel(skillLevel);
//					if (oldSkillEnhanceSkillLevel > 0 && skillLevel == 0) {
//						isSkillLevelChange = true;
//					}
//
//					if (isOnline && isSkillLevelChange && playerData.isAssembleFinish()) {
//						playerData.syncSkillInfo(skillEntity.getId());
//					}
//				}
//			}

			// // 查看职业特有技能属性有没有添加
//			if (playerData.getPlayerEntity().isSkillEnhanceOpen()) {
//				switch (roleEntity.getRoleCfg().getProfession()) {
//				case Const.prof.WARRIOR_VALUE:
//					attribute.set(Const.attr.BUFF_WARRIOR, playerData.getSpecializeLevel());
//					break;
//				case Const.prof.HUNTER_VALUE:
//					attribute.set(Const.attr.BUFF_HUNTER, playerData.getSpecializeLevel());
//					break;
//				default:
//					break;
//				}
//			}
//		}

		// 只有主角才有元素属性
//		if (roleEntity.getType() == GsConst.RoleType.MAIN_ROLE) {
//			// 主角Avatar属性加成
//			AvatarEntity usedAvatar = playerData.getUsedAvatar();
//			if (usedAvatar != null) {
//				AvatarCfg cfg = ConfigManager.getInstance().getConfigByKey(AvatarCfg.class, usedAvatar.getAvatarId());
//				if (cfg != null) {
//					Attribute additionAttr = new Attribute();
//					List<AttrInfoObj> attrInfoList = new ArrayList<AttrInfoObj>();
//					attrInfoList.addAll(AttrInfoObj.valueOfs(cfg.getAttrs()));
//					addCommonAttr(attribute, additionAttr, attrInfoList, playerData.getPlayerEntity().getLevel());
//					attribute.additionAttr(additionAttr);
//				}
//			}
//		}
		
		// add by callan 属性修正，属性刷新完毕后进行修正
//		if (attribute.getValue(Const.attr.MINDMG) > attribute.getValue(Const.attr.MAXDMG)) {
//			attribute.set(Const.attr.MINDMG, attribute.getValue(Const.attr.MAXDMG));
//		}
		// 馬超突破3技能

	}
	
	/**
	 * 刷新离线玩家角色属性
	 *
	 * @param roleEntity
	 * @param equipEntities
	 * @param roleRingEntities
	 * @return
	 */
	public static Attribute refreshOffinePlayerAttribute(RoleEntity roleEntity, List<EquipEntity> equipEntities,
			PlayerData playerData) {
		Attribute attribute = roleEntity.getAttribute();
		attribute.clear();
		// 添加角色基础属性
		attribute.add(roleEntity.getBaseAttr());
		// 将角色穿着的装备属性累加到角色属性上（包含装备上镶嵌的宝石）
		if (equipEntities != null) {
			AttachRoleEquip(attribute, equipEntities, Const.equipPart.HELMET_VALUE, roleEntity.getEquip1(), playerData);
			AttachRoleEquip(attribute, equipEntities, Const.equipPart.RING_VALUE, roleEntity.getEquip2(), playerData);
			AttachRoleEquip(attribute, equipEntities, Const.equipPart.BELT_VALUE, roleEntity.getEquip3(), playerData);
			AttachRoleEquip(attribute, equipEntities, Const.equipPart.CUIRASS_VALUE, roleEntity.getEquip4(),
					playerData);
			AttachRoleEquip(attribute, equipEntities, Const.equipPart.WEAPON1_VALUE, roleEntity.getEquip5(),
					playerData);
			AttachRoleEquip(attribute, equipEntities, Const.equipPart.WEAPON2_VALUE, roleEntity.getEquip6(),
					playerData);
			AttachRoleEquip(attribute, equipEntities, Const.equipPart.LEGGUARD_VALUE, roleEntity.getEquip7(),
					playerData);
			AttachRoleEquip(attribute, equipEntities, Const.equipPart.SHOES_VALUE, roleEntity.getEquip8(), playerData);
			AttachRoleEquip(attribute, equipEntities, Const.equipPart.GLOVE_VALUE, roleEntity.getEquip9(), playerData);
			AttachRoleEquip(attribute, equipEntities, Const.equipPart.NECKLACE_VALUE, roleEntity.getEquip10(),
					playerData);
		}
		// 根据一级属性计算二级属性
		updateSecondaryAttribute(roleEntity, playerData);
		return attribute;
	}

	private static void AttachRoleEquip(Attribute attribute, List<EquipEntity> equipEntities, int part, long equipId,
			PlayerData playerData) {
		if (part > 0 && equipId > 0) {
			for (EquipEntity equipEntity : equipEntities) {
				if (equipEntity.getId() != equipId) {
					continue;
				}
				attribute.add(EquipUtil.refreshAttribute(equipEntity, playerData));
			}
		}
	}

	/**
	 * 全身强化等级属性
	 *
	 * @param player
	 * @param roleEntity
	 */
	private static void updateGodlyAttrAttribute(PlayerData playerData, RoleEntity roleEntity) {
		int minEquipStrengthLevel = playerData.getPlayerEntity().getLevel();
		for (int part = Const.equipPart.HELMET_VALUE; part <= Const.equipPart.NECKLACE_VALUE; part++) {
			long equipId = playerData.getMainRole().getPartEquipId(part);
			if (equipId <= 0) {
				minEquipStrengthLevel = 0;
				break;
			}

			EquipEntity equipEntity = playerData.getEquipById(equipId);
			if (equipEntity != null && equipEntity.getStrength() < minEquipStrengthLevel) {
				minEquipStrengthLevel = equipEntity.getStrength();
			}
		}

		if (minEquipStrengthLevel > 0) {
			for (int part = Const.equipPart.HELMET_VALUE; part <= Const.equipPart.NECKLACE_VALUE; part++) {
				long equipId = playerData.getMainRole().getPartEquipId(part);
				EquipEntity equipEntity = playerData.getEquipById(equipId);
				if (equipEntity == null) {
					continue;
				}
				// 神器属性
				GodlyAttrCfg godlyAttrCfg = ConfigManager.getInstance().getConfigByKey(GodlyAttrCfg.class,
						equipEntity.getGodlyAttrId());
				if (godlyAttrCfg != null) {
					// 补丁 : 修复玩家神器星际为0的数据库bug ---editor by tzy
					if (equipEntity.getStarLevel() == 0) {
						equipEntity.setStarLevel(1);
						equipEntity.notifyUpdate(true);
					}
					// 全身装备最小强化等级除以10 minEquipStrengthLevel / 5
					int secondGodlyAttrValue = godlyAttrCfg
							.getLevelAttr(Math.min(minEquipStrengthLevel / 5, equipEntity.getStarLevel()));
					equipEntity.getAttribute().add(equipEntity.getGodlyAttrId(), secondGodlyAttrValue);
					roleEntity.getAttribute().add(equipEntity.getGodlyAttrId(), secondGodlyAttrValue);
				}
				// 神器属性
				godlyAttrCfg = ConfigManager.getInstance().getConfigByKey(GodlyAttrCfg.class,
						equipEntity.getGodlyAttrId2());
				if (godlyAttrCfg != null) {
					// 补丁 : 修复玩家神器星际为0的数据库bug ---editor by tzy
					if (equipEntity.getStarLevel() == 0) {
						equipEntity.setStarLevel(1);
						equipEntity.notifyUpdate(true);
					}

					int secondGodlyAttrValue = godlyAttrCfg
							.getLevelAttr(Math.min(minEquipStrengthLevel / 5, equipEntity.getStarLevel2()));
					equipEntity.getAttribute().add(equipEntity.getGodlyAttrId2(), secondGodlyAttrValue);
					roleEntity.getAttribute().add(equipEntity.getGodlyAttrId2(), secondGodlyAttrValue);
				}
			}
		}
	}

	/**
	 * 查询Player
	 *
	 * @param playerId
	 * @return
	 */
	public static Player queryPlayer(int playerId) {
		ObjBase<GuaJiXID, AppObj> objBase = GsApp.getInstance()
				.queryObject(GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId));
		if (objBase != null) {
			return (Player) objBase.getImpl();
		}
		return null;
	}

	/**
	 * 随机分配二级属性
	 *
	 * @return
	 */
	public static List<Integer> randomRoleSecondlyAttr(int totalCount, int pert, boolean isSuperBaptize) {
		// 写死
		List<Integer> list = new ArrayList<>();
		if (pert == 1) {
			list.add(totalCount);
		} else if (pert == 2) {
			try {
				double low = 0.5;
				double high = 0.9;
				if (isSuperBaptize) {
					low = 0.65;
					high = 0.9;
				}
				int v = GuaJiRand.randInt((int) (totalCount * low), (int) (totalCount * high));
				list.add(v);
				list.add(totalCount - v);
			} catch (Exception e) {
				MyException.catchException(e);
			}
		} else if (pert == 3) {
			try {
				double low1 = 0.5, high1 = 0.9;
				double low2 = 0.2, high2 = 0.8;
				if (isSuperBaptize) {
					low1 = 0.15;
					high1 = 0.6;
					low2 = 0.1;
					high2 = 0.9;
				}
				int v = GuaJiRand.randInt((int) (totalCount * low1), (int) (totalCount * high1));
				int v1 = GuaJiRand.randInt((int) (v * low2), (int) (v * high2));
				list.add(v1);
				list.add(v - v1);
				list.add(totalCount - v);
			} catch (Exception e) {
				MyException.catchException(e);
			}
		} else if (pert == 4) {
			try {
				double low1 = 0.5, high1 = 0.9;
				double low2 = 0.2, high2 = 0.8;
				double low3 = 0.45, high3 = 0.55;
				if (isSuperBaptize) {
					low1 = 0.5;
					high1 = 0.9;
					low2 = 0.1;
					high2 = 0.9;
					low3 = 0.45;
					high3 = 0.55;
				}
				int v = GuaJiRand.randInt((int) (totalCount * low1), (int) (totalCount * high1));
				int v1 = GuaJiRand.randInt((int) (v * low2), (int) (v * high2));
				list.add(v1);
				list.add(v - v1);
				int v2 = GuaJiRand.randInt((int) ((totalCount - v) * low3), (int) ((totalCount - v) * high3));
				list.add(v2);
				list.add(totalCount - v2 - v);
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return list;
	}

	/**
	 * push word notice
	 *
	 * @param player
	 * @param action
	 * @param builder
	 * @return
	 */
	// 1飘字 2弹窗
	public static RewardInfo.Builder pushRewards(Player player, RewardInfo.Builder builder, int flag) {
		HPPlayerReward.Builder playerRewardBuilder = HPPlayerReward.newBuilder();
		playerRewardBuilder.setRewards(builder);
		playerRewardBuilder.setFlag(flag);
		player.sendProtocol(Protocol.valueOf(HP.code.PLAYER_AWARD_S_VALUE, playerRewardBuilder));
		return builder;
	}

	/**
	 * 四大属性改變推送
	 */
	public static void pushAttrChange(Player player, Attribute oldAttr, Attribute newAttr) {
		if (player == null || oldAttr == null || newAttr == null) {
			return;
		}
		AttrInfo.Builder changeAttr = AttrInfo.newBuilder();
		if (newAttr != null) {
			for (attr at : attr.values()) {
				
				if (at == Const.attr.STRENGHT || at == Const.attr.AGILITY ||
						at == Const.attr.INTELLECT || at == Const.attr.STAMINA	) {
					
					if (newAttr.containsAttr(at)) {
		
						int value = newAttr.getValue(at) - oldAttr.getValue(at);
		
						// 属性经验加成递减不提示
						if (Const.attr.valueOf(at.getNumber()) == Const.attr.BUFF_EXP_DROP) {
							if (value < 0) {
								break;
							}
						}
		
						Attr.Builder attrBuilder = Attr.newBuilder();
						attrBuilder.setAttrId(at.getNumber());
						if (value != 0) {
							attrBuilder.setAttrValue(newAttr.getValue(at) - oldAttr.getValue(at));
							changeAttr.addAttribute(attrBuilder);
						}
					}
				}
			}
		}
		player.sendProtocol(Protocol.valueOf(HP.code.ATTRIBUTE_CHANGE_NOTICE, changeAttr));
	}
	
	/**
	 * 所有屬性改變推送
	 * @param player
	 * @param oldAttr
	 * @param newAttr
	 */
	public static void pushAllAttrChange(Player player, Attribute oldAttr, Attribute newAttr) {
		if (player == null || oldAttr == null || newAttr == null) {
			return;
		}
		AttrInfo.Builder changeAttr = AttrInfo.newBuilder();
		if (newAttr != null) {
			for (attr at : attr.values()) {					
				if (newAttr.containsAttr(at)) {
	
					int value = newAttr.getValue(at) - oldAttr.getValue(at);
	
					// 属性经验加成递减不提示
					if (Const.attr.valueOf(at.getNumber()) == Const.attr.BUFF_EXP_DROP) {
						if (value < 0) {
							break;
						}
					}
	
					Attr.Builder attrBuilder = Attr.newBuilder();
					attrBuilder.setAttrId(at.getNumber());
					if (value != 0) {
						attrBuilder.setAttrValue(newAttr.getValue(at) - oldAttr.getValue(at));
						changeAttr.addAttribute(attrBuilder);
					}
				}
			}
		}
		player.sendProtocol(Protocol.valueOf(HP.code.ATTRIBUTE_CHANGE_NOTICE, changeAttr));
	}

	/**
	 * 元素属性推送
	 */
	public static void pushElementAttrChange(Player player, Map<Const.attr, Integer> oldEle,
			Map<Const.attr, Integer> newEle) {
		if (player == null || oldEle == null || newEle == null) {
			return;
		}
		AttrInfo.Builder changeAttr = AttrInfo.newBuilder();
		if (newEle != null) {
			for (Map.Entry<Const.attr, Integer> entry : oldEle.entrySet()) {
				Const.attr key = entry.getKey();
				if (newEle.containsKey(key)) {
					int value = newEle.get(key) - entry.getValue();
					if (value != 0) {
						Attr.Builder attrBuilder = Attr.newBuilder();
						attrBuilder.setAttrId(key.getNumber());
						attrBuilder.setAttrValue(value);
						changeAttr.addAttribute(attrBuilder);
					}
				}
			}
		}
		player.sendProtocol(Protocol.valueOf(HP.code.ATTRIBUTE_CHANGE_NOTICE, changeAttr));
		return;
	}

	/**
	 * 自动参加公会boss战扣钻
	 *
	 * @param snapShotInfo
	 * @param allianceAutoFightCostGold
	 */
	public static boolean deductAutoAllianceBossGold(Builder snapShotInfo, int allianceAutoFightCostGold,
			int allianceId) {
		if (allianceAutoFightCostGold <= 0) {
			return false;
		}

		Player player = PlayerUtil.queryPlayer(snapShotInfo.getPlayerId());
		if (player != null) {
			if (player.getGold() >= allianceAutoFightCostGold) {
				// 在线玩家扣费
				player.consumeGold(allianceAutoFightCostGold, Action.ALLIANCE_BOSS_AUTO_JOIN);
				ConsumeItems.valueOf(changeType.CHANGE_GOLD, allianceAutoFightCostGold).pushChange(player);
				BehaviorLogger.log4Platform(player, Action.JOIN_ALLIANCE_BOSS, Params.valueOf("allianceId", allianceId),
						Params.valueOf("isAuto", "true"));
				return true;
			}
		}
		// 离线玩家扣费
		List<PlayerEntity> playerEntities = DBManager.getInstance().query("from PlayerEntity where id = ?",
				snapShotInfo.getPlayerId());
		if (playerEntities.size() > 0) {
			PlayerEntity playerEntity = (PlayerEntity) playerEntities.get(0);
			if (playerEntity.getTotalGold() >= allianceAutoFightCostGold) {
				PlayerData playerData = new PlayerData(null);
				playerData.setPlayerEntity(playerEntity);
				playerData.loadActivity();
				offlinePlayerConsumeGold(playerData, allianceAutoFightCostGold, Action.ALLIANCE_BOSS_AUTO_JOIN);
				BehaviorLogger.log4Platform(playerEntity, Action.JOIN_ALLIANCE_BOSS,
						Params.valueOf("allianceId", allianceId), Params.valueOf("isAuto", "true"));
				return true;
			}

		}
		return false;
	}

	/**
	 * 离线玩家消耗钻石
	 *
	 * @param gold
	 * @param action
	 */
	public static void offlinePlayerConsumeGold(PlayerData playerData, int consumeGold, Action action) {
		PlayerEntity playerEntity = playerData.getPlayerEntity();
		int totalGold = playerEntity.getTotalGold();
		if (consumeGold <= 0 || consumeGold > totalGold) {
			throw new RuntimeException("offlinePlayerConsumeGold");
		}

		int rmbGold = playerEntity.getRmbGold();
		if (consumeGold <= rmbGold) {
			playerEntity.setRmbGold(rmbGold - consumeGold);

			BehaviorLogger.log4Platform(playerData.getPlayerEntity(), Action.FINANCE_GOLD_COST,
					Params.valueOf("money", consumeGold), Params.valueOf("wpnum", 1),
					Params.valueOf("price", consumeGold), Params.valueOf("wpid", 0),
					Params.valueOf("wptype", action.name()));
		} else {
			playerEntity.setSysGold(playerEntity.getSysGold() - (consumeGold - playerEntity.getRmbGold()));
			playerEntity.setRmbGold(0);

			BehaviorLogger.log4Platform(playerEntity, Action.FINANCE_GOLD_COST, Params.valueOf("money", rmbGold),
					Params.valueOf("wpnum", 1), Params.valueOf("price", rmbGold), Params.valueOf("wpid", 0),
					Params.valueOf("wptype", action.name()));

			BehaviorLogger.log4Platform(playerEntity, Action.GOLD_COST, Params.valueOf("money", consumeGold - rmbGold),
					Params.valueOf("wpnum", 1), Params.valueOf("price", consumeGold - rmbGold),
					Params.valueOf("wpid", 0), Params.valueOf("wptype", action.name()));
		}

		playerData.getPlayerEntity().notifyUpdate(false);

		// 计入累计消费
		ActivityUtil.addAccConsumeGold(playerData, consumeGold);

		BehaviorLogger.log4Service(playerData.getPlayerEntity(), Source.PLAYER_ATTR_CHANGE, action,
				Params.valueOf("playerAttr", Const.playerAttr.GOLD_VALUE), Params.valueOf("sub", consumeGold),
				Params.valueOf("after", totalGold - consumeGold));
	}

	/**
	 * 检测玩家身上有多少件神器
	 *
	 * @param roleEntity
	 * @param playerData
	 * @return
	 */
	public static int calRoleGodly(RoleEntity roleEntity, PlayerData playerData) {

		int ret = 0;
		if (roleEntity.getEquip1() != 0 && playerData.getEquipById(roleEntity.getEquip1()).isGodly())
			ret++;
		if (roleEntity.getEquip2() != 0 && playerData.getEquipById(roleEntity.getEquip2()).isGodly())
			ret++;
		if (roleEntity.getEquip3() != 0 && playerData.getEquipById(roleEntity.getEquip3()).isGodly())
			ret++;
		if (roleEntity.getEquip4() != 0 && playerData.getEquipById(roleEntity.getEquip4()).isGodly())
			ret++;
		if (roleEntity.getEquip5() != 0 && playerData.getEquipById(roleEntity.getEquip5()).isGodly())
			ret++;
		if (roleEntity.getEquip6() != 0 && playerData.getEquipById(roleEntity.getEquip6()).isGodly())
			ret++;
		if (roleEntity.getEquip7() != 0 && playerData.getEquipById(roleEntity.getEquip7()).isGodly())
			ret++;
		if (roleEntity.getEquip8() != 0 && playerData.getEquipById(roleEntity.getEquip8()).isGodly())
			ret++;
		if (roleEntity.getEquip9() != 0 && playerData.getEquipById(roleEntity.getEquip9()).isGodly())
			ret++;
		if (roleEntity.getEquip10() != 0 && playerData.getEquipById(roleEntity.getEquip10()).isGodly())
			ret++;
		return ret;

	}

	/**
	 * 只有玩家数据在线才可调用此方法
	 *
	 * @return
	 */
	public static List<SuitCfg> calcEffectSuits(PlayerData playerData, RoleEntity roleEntity) {
		List<SuitCfg> suitCfgs = new LinkedList<>();
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		for (long equipId : roleEntity.getEquipIds()) {
			if (equipId > 0) {
				EquipEntity equipEntity = playerData.getEquipById(equipId);
				if (equipEntity != null) {
					EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class,
							equipEntity.getEquipId());
					if (equipCfg != null) {
						if (result.containsKey(equipCfg.getSuitId())) {
							result.put(equipCfg.getSuitId(), result.get(equipCfg.getSuitId()) + 1);
						} else {
							result.put(equipCfg.getSuitId(), 1);
						}
					}
				}
			}
		}

		for (Map.Entry<Integer, Integer> entry : result.entrySet()) {
			SuitCfg suitCfg = SuitCfg.getSuitCfg(entry.getKey(), entry.getValue());
			if (suitCfg != null) {
				suitCfgs.add(suitCfg);
			}
		}

		return suitCfgs;
	}

	/**
	 * 获得玩家跨服战中的唯一表示
	 *
	 * @param playerId
	 */
	public static String getPlayerIdentify(int playerId) {
		return GsApp.getInstance().getServerIdentify() + "*" + playerId;
	}

	/**
	 * 获取玩家ID
	 *
	 * @param identify
	 * @return
	 */
	public static int getPlayerIdFromIdentify(String identify) {
		if (identify == null || "".equals(identify))
			return 0;
		int playerIndex = identify.lastIndexOf("*");
		String playerId = identify.substring(playerIndex + 1, identify.length());
		return Integer.parseInt(playerId);
	}

	/**
	 * 获取玩家所在的服务器名称
	 *
	 * @param identify
	 * @return
	 */
	public static String getServerName(String identify) {
		if (identify == null || "".equals(identify)) {
			return "";
		}
		int index = identify.lastIndexOf("*");
		String serverName = identify.substring(0, index);
		return serverName;
	}

	/**
	 * R2游戏评论(条件达成是触发)
	 *
	 * @param player
	 */
	public static boolean gameComment(Player player) {
		if (GJLocal.isLocal(GJLocal.R2)) {
			PlayerEntity playerEntity = player.getPlayerData().getPlayerEntity();
			Map<String, Object> map = new HashMap<String, Object>();
			String commentStr = playerEntity.getIsComment();
			HPCommentMsgRet.Builder builder = HPCommentMsgRet.newBuilder();
			if (commentStr == null || commentStr.isEmpty()) {
				map.put("isComment", false);
				map.put("time", "0000-00-00 00:00:00");
				map.put("remindNum", 0);
				playerEntity.setIsComment(JSONObject.fromObject(map).toString());
				playerEntity.notifyUpdate(false);
				builder.setNumber(0);
				player.sendProtocol(Protocol.valueOf(HP.code.ROLE_GAME_COMMENT_S, builder));
			} else {
				JSONObject comObj = JSONObject.fromObject(commentStr);
				boolean isComment = comObj.getBoolean("isComment");
				if (isComment) {// 已评论
					return false;
				}
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String dateStr = comObj.getString("time");
				if (dateStr == null) {
					return false;
				}
				try {
					Date date = df.parse(dateStr);
					if (isPassDate(date)) {// 当前时间超过待评论时间
						builder.setNumber(0);
						player.sendProtocol(Protocol.valueOf(HP.code.ROLE_GAME_COMMENT_S, builder));
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	/**
	 * 当前时间是否大于等于评论时间
	 *
	 * @param date
	 * @return
	 */
	public static boolean isPassDate(Date date) {
		if (date == null) {
			return false;
		}
		return date.before(new Date());
	}

	/**
	 * 时间天数处理
	 *
	 * @param beginDate
	 * @param day
	 * @param type      (add/sub)
	 * @return
	 * @throws ParseException
	 */

	public static String dealWithDate(Date beginDate, int day, String type) throws ParseException {
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar date = Calendar.getInstance();
		date.setTime(beginDate);
		if (type.equals("add")) {
			date.set(Calendar.DATE, date.get(Calendar.DATE) + day);
		} else if (type.equals("sub")) {
			date.set(Calendar.DATE, date.get(Calendar.DATE) - day);
		}
		String endDate = dft.format(date.getTime());
		return endDate;
	}

	/**
	 * 添加属性
	 *
	 * @param attribute
	 * @param attrInfoList
	 * @param roleInfo
	 */
	public static void addCommonAttr(Attribute attribute, Attribute additionAttrMap, List<AttrInfoObj> attrInfoList,
			int level) {
		for (AttrInfoObj attr : attrInfoList) {
			if (attr.getAddType() == GsConst.RingValueType.PERCENTAGE_TYPE) {
				additionAttrMap.add(Const.attr.valueOf(attr.getAttrType()), attr.getAttrValue());
			} else if (attr.getAddType() == GsConst.RingValueType.NUMERICAL_TYPE) {
				attribute.add(attr.getAttrType(), attr.getAttrValue());
			} else {
				// 数值 * 等级
				attribute.add(attr.getAttrType(), attr.getAttrValue() * level);
			}
		}
	}

	/**
	 * 获取佣兵所有皮肤（或者根据皮肤获取佣兵以及其它皮肤）
	 */
	public static List<Integer> getMercenarySkinGroup(int playerId, int roleId) {
		List<Integer> mercenaryGroup = new ArrayList<Integer>();
		List<RoleEntity> allRoles = null;
		Player player = PlayerUtil.queryPlayer(playerId);
		if (player != null) {
			// 在线
			allRoles = player.getPlayerData().loadRoleEntities();
		} else {
			// 离线
			allRoles = DBManager.getInstance().query("from RoleEntity where playerId = ? and invalid = 0", playerId);
			if (allRoles != null) {
				for (RoleEntity role : allRoles) {
					role.convertRing();
				}
			}
		}
		if (allRoles == null) {
			return mercenaryGroup;
		}
		RoleEntity targetRole = null;
		for (RoleEntity role : allRoles) {
			if (role.getId() == roleId) {
				targetRole = role;
				break;
			}
		}
		if (targetRole == null) {
			return mercenaryGroup;
		}
		mercenaryGroup = new ArrayList<Integer>();
		RoleCfg targetCfg = ConfigManager.getInstance().getConfigByKey(RoleCfg.class, targetRole.getItemId());
		for (RoleEntity role : allRoles) {
			if (role.getId() == roleId) {
				continue;
			}
			if (!role.isHero()) {
				continue;
			}
			RoleCfg cfg = ConfigManager.getInstance().getConfigByKey(RoleCfg.class, role.getItemId());
			if (cfg == null) {
				continue;
			}
			if (targetCfg.getBaseId() == role.getItemId() || cfg.getBaseId() == targetRole.getItemId()
					|| (targetCfg.getBaseId() > 0 && targetCfg.getBaseId() == cfg.getBaseId())) {
				mercenaryGroup.add(role.getId());
			}
		}
		return mercenaryGroup;
	}

	public static void syncEighteenPrinces(Player player, EighteenPrincesEntity entity) {
		if (player == null) {
			return;
		}
		if (entity == null) {
			return;
		}
		HPPlayerHelpMercenaryRet.Builder builder = HPPlayerHelpMercenaryRet.newBuilder();
		HelpItemInfo.Builder helpItem = HelpItemInfo.newBuilder();
		helpItem.setLeftCount(SysBasicCfg.getInstance().getEighteenMaxUseTimes() - entity.getEighteenPrinceCount());
		helpItem.setPlayerId(player.getId());
		helpItem.setName(player.getName());
		helpItem.setRoleItemId(entity.getEighteenPrinceHelpRoleItemId());

		RoleInfo.Builder roleInfoBuilder = SnapShotManager.getInstance().getActiviteRoleInfo(player.getId(),
				entity.getEighteenPrinceHelpRoleItemId());
		if (roleInfoBuilder == null) {
			helpItem.setFightValue(0);
			helpItem.setLevel(0);
		} else {
			helpItem.setFightValue(roleInfoBuilder.getFight());
			helpItem.setLevel(roleInfoBuilder.getLevel());
		}
		builder.addHelpMercenary(helpItem);
		player.sendProtocol(Protocol.valueOf(HP.code.EIGHTEENPRINCES_HELPMERCENARY_S_VALUE, builder));
	}
	/**
	 * 計算信條各項數值 0.好感度 1. 親密度 2.性感度
	 * @param player
	 * @return
	 */
	public static Map<Integer,Integer> calcSecretMsgValue(PlayerData playerData,int heroId){
		Map<Integer,Integer> retMap = new HashMap<>();
		SecretMsgEntity smEntity = playerData.getSecretMsgByItem(heroId);
		if (smEntity != null) {
			Map<Integer,Integer> choiceMap = smEntity.getChoiceMsgMap();
			if ((choiceMap != null)) {
				if (smEntity.getMsgCount() > 0) {
					//Set<Integer> ownPic = new HashSet<>();
					for (Map.Entry<Integer,Integer> entry: choiceMap.entrySet() ) {
						SecretMsgCfg cfg = ConfigManager.getInstance().getConfigByKey(SecretMsgCfg.class,entry.getKey());
						if (cfg == null) {
							continue;
						}
						if ((entry.getValue() < 0) || (entry.getValue() > 1))  {
							continue;
						}
						List<Integer> valueList = cfg.getChoiceMsgList(entry.getValue());
						int key = 0;
						for (int value : valueList) {
							if (retMap.containsKey(key)) {
								retMap.replace(key, retMap.get(key)+value);
							} else {
								retMap.put(key,value);
							}
							key++;
						}
//						if (unlockPic != null) {
//							Map<Integer,List<SecretAlbumCfg>> albumCfgMap = SecretAlbumCfg.getCfgByHeroId(heroId);
//							for(int i = 0 ; i < 3 ;i ++) {
//								if (albumCfgMap.containsKey(i)) {
//									for (SecretAlbumCfg acfg : albumCfgMap.get(i)){
//										if (!ownPic.contains(acfg.getPicNumber())) {
//											if (retMap.get(i) >= acfg.getScore()) {
//												ownPic.add(acfg.getPicNumber());
//												if (unlockPic.containsKey(entry.getKey())) {
//													unlockPic.get(entry.getKey()).add(acfg.getPicNumber());
//												} else {
//													List<Integer> alist = new ArrayList<>();
//													alist.add(acfg.getPicNumber());
//													unlockPic.put(entry.getKey(), alist);
//												}
//											}
//										}
//									}
//								}
//							}
//						}
					}
				} else {
					for (int i = 0 ; i < 3 ; i++) {
						retMap.put(i, 0);
					}
				}
			}
		}
		return retMap;
	}
	
	/**
	 * 
	 * 取秘密信條增加屬性
	 */
	
	public static Attribute getSecretMsgAttr(Map<Integer,Integer> valueMap) {
		Attribute attribute = new Attribute();
		int score = 0;
		for (Map.Entry<Integer, Integer> entry : valueMap.entrySet()) {
			SecretBuffCfg sbcfg = ConfigManager.getInstance().getConfigByKey(SecretBuffCfg.class, entry.getKey()+1);
			if (sbcfg != null) {
				score = entry.getValue();
				if (score > 0) {
					attribute.add(sbcfg.getAttribute(score));
				}
			}
		}
		return attribute;
	}
	
	/**
	 * 計算訊息種類數量
	 */
	public static int getSecretMsgCount(List<Integer> msgList) {
		int count = 0;
		for (int msgId : msgList) {
			SecretMsgCfg cfg = ConfigManager.getInstance().getConfigByKey(SecretMsgCfg.class, msgId);
			if ((cfg != null)) {
				count++;
			}
		}
		return count;
	}
	
	/*
	 * 檢查玩家隊伍正確性
	 * 
	 */
	public static boolean checkRolePos(Player player,Map<Integer,Integer> rolePosMap,List<String> role_posList) {
		rolePosMap.clear();
		// 有傳入攜帶英雄
		if ((role_posList == null) || (role_posList.size() <= 0)) {
			player.sendError(HP.code.BATTLE_FORMATION_S_VALUE, Status.error.PARAMS_INVALID);
			return false;
		}
		boolean haveHero = false;
		for (String role_pos :role_posList ) { //隊伍檢查,隊伍不包含leader
			if (!role_pos.isEmpty()) {
				String[] Sks = role_pos.split("_");
				int roleid = Integer.valueOf(Sks[0].trim());
				int pos = Integer.valueOf(Sks[1].trim());
				RoleEntity roleEntity = player.getPlayerData().getMercenaryById(roleid);
				if (roleEntity == null || !roleEntity.isArmy()) {
					player.sendError(HP.code.BATTLE_FORMATION_S_VALUE, Status.error.ROLE_NOT_FOUND);
					return false;
				}
				
				if (roleEntity.isHero()){
					haveHero = true;
				}
				
//				if (roleEntity.getRoleState() == Const.RoleStatus.EXPEDITION_VALUE) {
//					player.sendError(HP.code.BATTLE_FORMATION_S_VALUE, Status.error.FORMATION_CAN_NOT_USE_EXPEDITION_VALUE);
//					return false;
//				}
				
				if (roleEntity.getRoleState() != Const.RoleActiviteState.IS_ACTIVITE_VALUE) {
					player.sendError(HP.code.BATTLE_FORMATION_S_VALUE, Status.error.PARAMS_INVALID);
					return false;
				}
											
				if ((pos < 1) || (pos > GsConst.FormationType.FormationMember)) { // client只能送英雄站位
					player.sendError(HP.code.BATTLE_FORMATION_S_VALUE, Status.error.PARAMS_INVALID);
					return false;
				}
				if ((rolePosMap.containsValue(pos)) || (rolePosMap.containsKey(roleid))) {
					// 檢查重複站位
					player.sendError(HP.code.BATTLE_FORMATION_S_VALUE, Status.error.PARAMS_INVALID);
					return false;
				}
				rolePosMap.put(roleid, pos);
			} else {
				player.sendError(HP.code.BATTLE_FORMATION_S_VALUE, Status.error.PARAMS_INVALID);
				return false;
			}
		}
		
		if (!haveHero) {
			player.sendError(HP.code.BATTLE_FORMATION_S_VALUE, Status.error.ROLE_NOT_FOUND);
			return false;
		}
		return true;
	}
	
	/**
	 * 轉換至元素陣型編隊
	 */
	public static void transFormation(Player player ,Map<Integer,Integer> rolePosMap,int formationType) {
		List<Integer> itemIds = new ArrayList<>();
		Map<Integer,Integer> posRoleMap = new HashMap<>();
		//倒轉 Map
		for (Map.Entry<Integer, Integer> entry : rolePosMap.entrySet()) {
			posRoleMap.put(entry.getValue(),entry.getKey());
		}

		for (int pos = 1 ; pos <=  GsConst.FormationType.FormationMember ; pos++) {
			if (posRoleMap.containsKey(pos)) {
				itemIds.add(posRoleMap.get(pos));
			} else {
				itemIds.add(0);
			}
		}
		
		FormationEntity defaultFormationEntity = player.getPlayerData().getFormationByType(formationType);
		List<Integer> defaultItemIds = new ArrayList<>();
		defaultItemIds.addAll(defaultFormationEntity.getFightingArray());
		
		boolean update = false; // 是否更新編隊
		//int idx = 0;
		if (itemIds.size() == defaultItemIds.size()) {
			if (itemIds.containsAll(defaultItemIds)) {
				// 檢查陣型站位
				//idx = 0;
				for (Integer id :itemIds) {
					if (defaultItemIds.get(0) != id) {
						update = true;
						break;
					}
					//idx++;
				}
			} else {
				update = true;
			}
		} else {
			update = true;
		}
		
		if (update) {
			defaultFormationEntity.replaceMercenaryTeamList(itemIds);
			// 存储编队
			defaultFormationEntity.notifyUpdate();
			
			// 同步默认阵型状态
			Msg hawkMsg = Msg.valueOf(GsConst.MsgType.FORMATION_MODIFY,
					GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
			hawkMsg.pushParam(formationType);
			GsApp.getInstance().postMsg(hawkMsg);
			
			// 休息差集(剛休息角色狀態設定)
			List<Integer> restList = new ArrayList<>();
			restList.addAll(defaultItemIds);
			restList.removeAll(itemIds);
			for (Integer roleId : restList) {
				if (roleId > 0) {
					RoleEntity roleEntity = player.getPlayerData().getMercenaryById(roleId);
					if (formationType == GsConst.FormationType.FormationBegin) {
						roleEntity.decStatus(Const.RoleStatus.FIGHTING_VALUE);
					}
					// 重整移除隊伍陣法效果
					PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
					player.getPlayerData().syncRoleInfo(roleId);
				}
			}

			// 上阵差集(剛上陣的角色狀態設定)
			List<Integer> fightList = new ArrayList<>();
			fightList.addAll(itemIds);
			fightList.removeAll(defaultItemIds);
			for (Integer roleId : fightList) {
				if (roleId > 0) {
					RoleEntity roleEntity = player.getPlayerData().getMercenaryById(roleId);
					if (formationType == GsConst.FormationType.FormationBegin) {
						roleEntity.incStatus(Const.RoleStatus.FIGHTING_VALUE);
					}
				}
			}
			
			// 目前所有上陣隊伍的角色重算身上的陣法加值狀態
			for (Integer roleId : itemIds) {
				if (roleId > 0) {
					RoleEntity roleEntity = player.getPlayerData().getMercenaryById(roleId);
					// 重整增加隊伍陣法效果
					PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
					player.getPlayerData().syncRoleInfo(roleId);
				}
			}
		}
	}
	
	/**
	 * 
	 * 取公會魔典增加屬性
	 */
	public static Attribute getGuildBuffAttr(PlayerData playerData, int prof) {
		Attribute attribute = new Attribute();
		if ((prof <= 0) || (prof > GsConst.GuildBuffConst.MaxProf)) {
			 return null;
		 }
		Map<Integer,Integer> talentMap = playerData.getGuildBuffEntity().getBuffMap();
		
		// 對應大天賦ID
		int BigTalentId = prof *GsConst.GuildBuffConst.Type_Base;
		
		int talentId = 0;
		int talentLv = 0;
		for (int i = 0 ; i <= GsConst.GuildBuffConst.MaxTalentID ; i++) {
			talentId = BigTalentId + i;
			if (talentMap.containsKey(talentId)) {
				talentLv = talentMap.get(talentId);
				GuildSoulCfg GSCfg = GuildSoulCfg.getGuildSoulCfg(talentId, talentLv);
				if (GSCfg != null) {
					attribute.add(GSCfg.getAttribute());
				}
			}
		}
		
		return attribute;
	}
	/**
	 * 修改身上標記
	 * @param player
	 * @param signId
	 * @param add
	 * @param synctoC
	 * @return
	 */
	public static boolean modifyPlayerSign(Player player,int signId,boolean add ,boolean synctoC) {
		SignCfg aCfg = ConfigManager.getInstance().getConfigByKey(SignCfg.class, signId);
		if (aCfg == null) {
			return false;
		}
		
		SignEntity signEntity = player.getPlayerData().getSignEntity();
		
		boolean needSave = false;
		if (add) {
			if (!signEntity.getSignSet().contains(signId)) {
				needSave = true;
				signEntity.addSign(signId);
			}
		} else {
			if (signEntity.getSignSet().contains(signId)) {
				needSave = true;
				signEntity.delSign(signId);
			}
		}
		if (needSave) {
			signEntity.SaveSign();
		}
		
		if (synctoC) {
			SignRespones.Builder builder = SignRespones.newBuilder();
			builder.setAction(GsConst.SignProtoType.Change_Sign);
			builder.setState(add);
			builder.addSignId(signId);
			player.sendProtocol(Protocol.valueOf(HP.code.SIGN_SYNC_S, builder));
		}
		return true;
	}
	
	
	/**
	 * 是否有脏字
	 * 
	 * @param chatMsg
	 * @return
	 */
	public static boolean hasDirtyKey(String chatMsg) {
		List<ChatShieldCfg> chatShieldCfgs = ConfigManager.getInstance().getConfigList(ChatShieldCfg.class);
		for (ChatShieldCfg cfg : chatShieldCfgs) {
			if (chatMsg.indexOf(cfg.getKey()) >= 0) {
				return true;
			}
		}
		return false;
	}
	
}