package com.guaji.game.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;

import com.guaji.game.GsApp;
import com.guaji.game.attribute.Attribute;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.AllianceBattleTimeCfg;
import com.guaji.game.config.AllianceBattleVersusCfg;
import com.guaji.game.config.BadgeGachaListCfg;
import com.guaji.game.config.ClientSettingCfg;
import com.guaji.game.config.CrossBattleCfg;
import com.guaji.game.config.CrossGroupCfg;
import com.guaji.game.config.ElementCfg;
import com.guaji.game.config.EquipCfg;
import com.guaji.game.config.EquipCfg.AttrValue;
import com.guaji.game.config.FortuneCfg;
import com.guaji.game.config.GodlyLevelExpCfg;
import com.guaji.game.config.HaremExchangeCfg;
import com.guaji.game.config.ItemCfg;
import com.guaji.game.config.NewMonsterCfg;
import com.guaji.game.config.PrinceDevilsCostCfg;
import com.guaji.game.config.PrinceDevilsExchangeCfg;
import com.guaji.game.config.SalePacketCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.config.TowerMonsterCfg;
import com.guaji.game.config.WeekCardCfg;
import com.guaji.game.config.WorldBossAwardsCfg;
import com.guaji.game.config.WorldBossCfg;
import com.guaji.game.entity.AllianceBattleItem;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.AllianceFightUnit;
import com.guaji.game.entity.AllianceFightVersus;
import com.guaji.game.entity.BadgeEntity;
import com.guaji.game.entity.BulletinEntity;
import com.guaji.game.entity.CampWarEntity;
import com.guaji.game.entity.EighteenPrincesEntity;
import com.guaji.game.entity.ElementEntity;
import com.guaji.game.entity.EmailEntity;
import com.guaji.game.entity.EquipEntity;
import com.guaji.game.entity.FacebookShareEntity;
import com.guaji.game.entity.HaremActivityEntity;
import com.guaji.game.entity.ItemEntity;
import com.guaji.game.entity.MaidenEncounterEntity;
import com.guaji.game.entity.MapStatisticsEntity;
import com.guaji.game.entity.MsgEntity;
import com.guaji.game.entity.MultiEliteRoomInfo;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.entity.PlayerPrinceDevilsEntity;
import com.guaji.game.entity.PlayerWorldBossEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.entity.RoleRingEntity;
import com.guaji.game.entity.SecretMsgEntity;
import com.guaji.game.entity.SkillEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.entity.TeamEntity;
import com.guaji.game.entity.TitleEntity;
import com.guaji.game.entity.WorldBossEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.AwardItems.Item;
import com.guaji.game.manager.AllianceBattleAgainstInfo;
import com.guaji.game.manager.AllianceBattleManager;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.MultiEliteManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.manager.WorldBossInfo;
import com.guaji.game.module.activity.commendationTribe.CommendationTribeStatus;
import com.guaji.game.module.activity.consumMonthCard.ConMonthCardStatus;
import com.guaji.game.module.activity.consumWeekCard.ConWeekCardStatus;
import com.guaji.game.module.activity.exchange.ExchangeStatus;
import com.guaji.game.module.activity.exchangeShop.ExchangeShopStatus;
import com.guaji.game.module.activity.fortune.FortuneStatus;
import com.guaji.game.module.activity.monthcard.MonthCardStatus;
import com.guaji.game.module.activity.salePacket.SalePacketStatus;
import com.guaji.game.module.activity.vipPackage.VipPackageStatus;
import com.guaji.game.module.activity.weekCard.WeekCardStatus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.protocol.Activity.HPCommendationTribe;
import com.guaji.game.protocol.Activity.HPFortuneInfo;
import com.guaji.game.protocol.Activity.HPMonthCardInfoRet;
import com.guaji.game.protocol.Activity.HPSalePacketInfoRet;
import com.guaji.game.protocol.Activity.HPSalePacketItem;
import com.guaji.game.protocol.Activity.HPWeekCardInfoRet;
import com.guaji.game.protocol.Activity2.HPExchangeInfoRet;
import com.guaji.game.protocol.Activity2.HPHaremScoreInfo;
import com.guaji.game.protocol.Activity2.HPHaremScorePanelRes;
import com.guaji.game.protocol.Activity2.HPPrinceDevilsPanelInfoRes;
import com.guaji.game.protocol.Activity2.HPPrinceDevilsScoreExchangeRes;
import com.guaji.game.protocol.Activity2.HPVipPackageInfoRet;
import com.guaji.game.protocol.Activity2.MaidenEncounterExchangeInfo;
import com.guaji.game.protocol.Activity2.PrinceDevilsGoodsInfo;
import com.guaji.game.protocol.Activity2.PrinceDevilsIndexInfo;
import com.guaji.game.protocol.Activity2.SyncMaidenEncounterExchangeRes;
import com.guaji.game.protocol.Activity4.ConsumeMonthCardInfoRet;
import com.guaji.game.protocol.Activity4.ConsumeWeekCardInfoRet;
import com.guaji.game.protocol.Alliance.AllianceInfo;
import com.guaji.game.protocol.AllianceBattle.AFDetailUnit;
import com.guaji.game.protocol.AllianceBattle.AFFightList;
import com.guaji.game.protocol.AllianceBattle.AFRankList;
import com.guaji.game.protocol.AllianceBattle.AFUnit;
import com.guaji.game.protocol.AllianceBattle.AFUnitState;
import com.guaji.game.protocol.AllianceBattle.AllianceBattleState;
import com.guaji.game.protocol.AllianceBattle.AllianceItemInfo;
import com.guaji.game.protocol.AllianceBattle.FightGroup;
import com.guaji.game.protocol.AllianceBattle.HPAllianceTeamFightRet;
import com.guaji.game.protocol.Attribute.Attr;
import com.guaji.game.protocol.Attribute.AttrInfo;
import com.guaji.game.protocol.Battle.BattleInfo;
import com.guaji.game.protocol.Battle.HPMapStatisticsSync;
import com.guaji.game.protocol.Bulletin.BulletTitleItem;
import com.guaji.game.protocol.Bulletin.BulletinContentRsp;
import com.guaji.game.protocol.Bulletin.BulletinTitleInfo;
import com.guaji.game.protocol.CampWar.MultiKillRankInfo;
import com.guaji.game.protocol.CampWar.PersonalCampWarInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.ShopType;
import com.guaji.game.protocol.Const.toolType;
import com.guaji.game.protocol.EighteenPrinces.HPSyncMedicalKitInfoRet;
import com.guaji.game.protocol.EighteenPrinces.MedicalKitItem;
import com.guaji.game.protocol.Element.ElementInfo;
import com.guaji.game.protocol.Equip.EquipAttr;
import com.guaji.game.protocol.Equip.EquipInfo;
import com.guaji.game.protocol.Equip.GemInfo;
import com.guaji.game.protocol.Friend.FBFriendItem;
import com.guaji.game.protocol.Friend.FriendItem;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.HeroToken.ShopStatusBean;
import com.guaji.game.protocol.Item.ItemInfo;
import com.guaji.game.protocol.Mail;
import com.guaji.game.protocol.Mail.MailInfo;
import com.guaji.game.protocol.Mail.MailType;
import com.guaji.game.protocol.Mail.OPMailInfoRet;
import com.guaji.game.protocol.Msg.MsgInfo;
import com.guaji.game.protocol.MultiElite.MulitiFriendItem;
import com.guaji.game.protocol.Player.CLIcheckInfo;
import com.guaji.game.protocol.Player.HPClientSetting;
import com.guaji.game.protocol.Player.HPPlayerStateSync;
import com.guaji.game.protocol.Player.Params;
import com.guaji.game.protocol.Player.PlayerInfo;
import com.guaji.game.protocol.Player.RoleDress;
import com.guaji.game.protocol.Player.RoleElement;
import com.guaji.game.protocol.Player.RoleEquip;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Player.RoleRingInfo;
import com.guaji.game.protocol.Player.RoleSkill;
import com.guaji.game.protocol.PlayerTitle.TitleInfo;
import com.guaji.game.protocol.Reward;
import com.guaji.game.protocol.Reward.RewardItem;
import com.guaji.game.protocol.SecretMsg.historyInfo;
import com.guaji.game.protocol.SecretMsg.secretMsgHeroInfo;
import com.guaji.game.protocol.SecretMsg.syncSecretMsg;
import com.guaji.game.protocol.Shop.PushShopRedPoint;
import com.guaji.game.protocol.Skill.SkillInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.TeamBattle.MemberInfo;
import com.guaji.game.protocol.WorldBoss.BossRankItem;
import com.guaji.game.protocol.WorldBoss.HPBossHarmRank;
import com.guaji.game.protocol.WorldBoss.HPWorldBossInfo;
import com.guaji.game.util.RedisUtil.FBFriendBean;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 协议同步对象生成帮助类, 大部分接口的Player参数没有必要
 * 
 * @author hawk
 */
public class BuilderUtil {
	/**
	 * 生成玩家协议同步信息
	 * 
	 * @return
	 */
	public static PlayerInfo.Builder genPlayerBuilder(PlayerEntity playerEntity, PlayerData playerData, int talentNum) {
		PlayerInfo.Builder builder = PlayerInfo.newBuilder();
		builder.setPlayerId(playerEntity.getId());
		builder.setGold(playerEntity.getTotalGold());
		builder.setCoin(playerEntity.getCoin());
		builder.setRecharge(playerEntity.getRecharge());
		builder.setVipLevel(playerEntity.getVipLevel());
		builder.setSmeltValue(playerEntity.getSmeltValue());
		builder.setHonorValue(playerEntity.getHonorValue());
		builder.setReputationValue(playerEntity.getReputationValue());
		builder.setTalentNum(talentNum);
		builder.setPayMoney((int) playerEntity.getPayMoney());
		builder.setHeadIcon(playerEntity.getHeadIcon());
		String signature = playerEntity.getSignature();
		if (signature != null && signature.length() > 0) {
			builder.setSignature(GameUtil.filterString(signature));
		}
		builder.setCrystal(playerEntity.getCrystalValue());
		if (playerData == null) {
			return builder;
		}
		FacebookShareEntity fe = playerData.loadFaceBookShare().get(GuaJiTime.getDateString(new Date()));
		if (fe != null) {
			builder.setFacebookCount(fe.getCount());
		} else {
			builder.setFacebookCount(0);
		}
		// 设置月卡属性
		builder.setMonthCardLeftDay(0);
		MonthCardStatus monthCardStatus = ActivityUtil.getMonthCardStatus(playerData);
		if (monthCardStatus != null) {
			int left = monthCardStatus.getLeftDays();
			left = left <= 0 ? 0 : left;
			// int leftDay = (monthCardStatus.getMonthActiveCount() - 1 )*30 +
			// left - 1;
			// leftDay = leftDay <= 0 ? 0 : leftDay;
			builder.setMonthCardLeftDay(left);
		}
		return builder;
	}

	/**
	 * 生成角色的协议同步信息(Player对象必须, 需要从中取出装备信息)
	 * 
	 * @param equipEntities
	 * @return
	 */
	public static RoleInfo.Builder genRoleBuilder(PlayerData playerData, RoleEntity roleEntity,
			List<EquipEntity> equipEntities, List<SkillEntity> skillEntities, List<ElementEntity> elementEntities,
			List<BadgeEntity> badgeEntities) {
		RoleInfo.Builder builder = RoleInfo.newBuilder();
		builder.setRoleId(roleEntity.getId());
		builder.setType(roleEntity.getType());
		builder.setItemId(roleEntity.getItemId());
		builder.setName(roleEntity.getName());
		builder.setProf(roleEntity.getProfession());
		builder.setLevel(roleEntity.getLevel());
		builder.setExp((long) roleEntity.getExp());
		builder.setStatus(roleEntity.getStatus());
		builder.setStarLevel(roleEntity.getStarLevel());
		builder.setStarExp(roleEntity.getStarExp());
		builder.setStageLevel(roleEntity.getStageLevel());
		builder.setStageLevel2(roleEntity.getStageLevel2());
		int FightValue = PlayerUtil.calcFightValue(roleEntity);
		builder.setFight(FightValue);
		builder.setMarsterFight(PlayerUtil.calcAllFightValue(playerData));
		builder.setAttribute(genAttributeBuilder(roleEntity.getAttribute()));
		builder.setRebirthStage(roleEntity.getRebirthStage());
		builder.setActiviteState(roleEntity.getRoleState());
		builder.setSkinId(roleEntity.getSkinId());

		int nakedFight = roleEntity.getNakedFight();
		int equipFight = FightValue > nakedFight ? (FightValue - nakedFight) : 0;
		builder.setEquipFight(equipFight);

		int value = roleEntity.getIsMagic() ? 1 : 0;

		builder.setIsMagic(value);

		builder.setElements(roleEntity.getElement());
		builder.setATKSpeed(roleEntity.getATKSpeed());

		builder.setNATK(roleEntity.getNATK());

		builder.setATKMP(roleEntity.getATKMP());

		builder.setDEFMP(roleEntity.getDEFMP());

		builder.setSkillMp(roleEntity.getSKMP());

		builder.setClassCorrection(roleEntity.getClassCorrection());

		if (roleEntity.getType() == Const.roleType.MAIN_ROLE_VALUE) {
			builder.setSkillSpecializeLevel(playerData.getSpecializeLevel());
			builder.setAvatarId(playerData.getUsedAvatarId());
		} else {
			builder.setSkillSpecializeLevel(0);
		}

		if (roleEntity.getRoleBaptizeAttr() != null) {
			builder.setBaptizeAttr(genAttributeBuilder(roleEntity.getRoleBaptizeAttr()));
		}

		if (roleEntity.getBaseAttr() != null) {
			builder.setBaseAttr(genAttributeBuilder(roleEntity.getBaseAttr()));
		}

//		if (roleEntity.getStarLevel() >= RoleUpStarCfg.getMaxStarLevel()) {
//			builder.setIsStage(true);
//		} else {
		builder.setIsStage(false);
//		}

		for (int ring : roleEntity.getRingList()) {
			builder.addRingId(ring);
		}

		// 装备信息
		if (equipEntities != null) {
			roleInfoBuilderAttachRoleEquip(equipEntities, builder, Const.equipPart.HELMET_VALUE,
					roleEntity.getEquip1());
			roleInfoBuilderAttachRoleEquip(equipEntities, builder, Const.equipPart.RING_VALUE, roleEntity.getEquip2());
			roleInfoBuilderAttachRoleEquip(equipEntities, builder, Const.equipPart.BELT_VALUE, roleEntity.getEquip3());
			roleInfoBuilderAttachRoleEquip(equipEntities, builder, Const.equipPart.CUIRASS_VALUE,
					roleEntity.getEquip4());
			roleInfoBuilderAttachRoleEquip(equipEntities, builder, Const.equipPart.WEAPON1_VALUE,
					roleEntity.getEquip5());
			roleInfoBuilderAttachRoleEquip(equipEntities, builder, Const.equipPart.WEAPON2_VALUE,
					roleEntity.getEquip6());
			roleInfoBuilderAttachRoleEquip(equipEntities, builder, Const.equipPart.LEGGUARD_VALUE,
					roleEntity.getEquip7());
			roleInfoBuilderAttachRoleEquip(equipEntities, builder, Const.equipPart.SHOES_VALUE, roleEntity.getEquip8());
			roleInfoBuilderAttachRoleEquip(equipEntities, builder, Const.equipPart.GLOVE_VALUE, roleEntity.getEquip9());
			roleInfoBuilderAttachRoleEquip(equipEntities, builder, Const.equipPart.NECKLACE_VALUE,
					roleEntity.getEquip10());
		}

		// 技能信息(主角, 佣兵)
		// Hero_NGListCfg ClassCfg = roleEntity.getRoleCfg();
		if ((roleEntity != null)) {
			List<Integer> skillList = roleEntity.getSkillAll();
			for (Integer i : skillList) {
				int skillId = i; // skill id
				RoleSkill.Builder skillBuilder = RoleSkill.newBuilder();
				skillBuilder.setSkillId(0);
				skillBuilder.setItemId(skillId);
				builder.addSkills(skillBuilder);
			}

			for (int skinId : playerData.loadRoleSkinEntity().getSkinSet()) {
				if (roleEntity.isMySkin(skinId)) {
					builder.addOwnSkin(skinId);
				}
			}
			// 獲取英雄腳色(徽章)符文技能
//			for (Long badge :roleEntity.getBadgeMap().values()) {
//				if (badge == 0) {
//					continue;
//				}
//				BadgeEntity badgeEntity = playerData.getBadgeById(badge);
//				if (badgeEntity != null) {
//					RoleSkill.Builder skillBuilder = RoleSkill.newBuilder();
//					skillBuilder.setSkillId(0);
//					skillBuilder.setItemId(badgeEntity.getSkill());
//					builder.addSkills2(skillBuilder);
//				}
//			}
//				for (int skillId : roleEntity.getEyeSkill()) {
//					RoleSkill.Builder skillBuilder = RoleSkill.newBuilder();
//					skillBuilder.setSkillId(0);
//					skillBuilder.setItemId(skillId);
//					builder.addSkills2(skillBuilder);
//				}
		}

//		for (int i = 0; i < SysBasicCfg.getInstance().getMaxElementSize(); i++) {
//			long eleId = roleEntity.getElementByIndex(i);
//			if (eleId > 0) {
//				for (ElementEntity elementEntity : elementEntities) {
//					if (elementEntity.getId() == eleId) {
//						RoleElement.Builder elementBuilder = RoleElement.newBuilder();
//						elementBuilder.setElementId(elementEntity.getId());
//						elementBuilder.setElementItemId(elementEntity.getItemId());
//						elementBuilder.setLevel(elementEntity.getLevel());
//						elementBuilder.setIndex(i + 1);
//						elementBuilder.setQuality(elementEntity.getQuality());
//						elementBuilder.setType(1);
//						builder.addElements(elementBuilder);
//					}
//				}
//			} else {
//				RoleElement.Builder elementBuilder = RoleElement.newBuilder();
//				elementBuilder.setElementId(0);
//				elementBuilder.setElementItemId(0);
//				elementBuilder.setLevel(0);
//				elementBuilder.setIndex(i + 1);
//				elementBuilder.setQuality(0);
//				elementBuilder.setType(1);
//				builder.addElements(elementBuilder);
//			}
//		}

		// 徽章信息
		if (badgeEntities != null) {
			for (Entry<Integer, Long> item : roleEntity.getBadgeMap().entrySet()) {

				for (BadgeEntity badgeEntity : badgeEntities) {
					if (badgeEntity.getId() != item.getValue()) {
						continue;
					}
					RoleDress.Builder roleDress = RoleDress.newBuilder();
					roleDress.setId(item.getValue());
					roleDress.setItemId(badgeEntity.getBadgeId());
					
					if (badgeEntity.getSkillList().size() > 0) {
						for (int reId : badgeEntity.getSkillList())
						{
							int cfgId = (reId > GsConst.BADGE_LOCK_MASK)? reId % GsConst.BADGE_LOCK_MASK : reId;
							
							BadgeGachaListCfg scfg = ConfigManager.getInstance().getConfigByKey(BadgeGachaListCfg.class, cfgId);
							if (scfg != null) {
								roleDress.addSkillId(scfg.getSkill());
							}
						}
					}
					roleDress.setLoc(item.getKey());
					builder.addDress(roleDress);
				}
			}
		}

		return builder;
	}

	/**
	 * 生成物品同步协议信息builder
	 * 
	 * @return
	 */
	public static ItemInfo.Builder genItemBuilder(ItemEntity itemEntity) {
		ItemInfo.Builder builder = ItemInfo.newBuilder();
		builder.setId(itemEntity.getId());
		builder.setItemId(itemEntity.getItemId());
		builder.setCount(itemEntity.getItemCount());
		builder.setStatus(itemEntity.getStatus());
		ItemCfg itemCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, itemEntity.getItemId());
		if (itemCfg.getType() == toolType.GEM_VALUE) {
			builder.setExp(itemEntity.getLevelUpTimes());
		}
		return builder;
	}

	/**
	 * 生成装备实体的builder信息
	 * 
	 * @return
	 */
	public static EquipInfo.Builder genEquipBuilder(EquipEntity equipEntity) {

		EquipInfo.Builder builder = EquipInfo.newBuilder();
		builder.setEquipId(equipEntity.getEquipId());
		builder.setId(equipEntity.getId());
		if (equipEntity.getStarLevel() > 1) {
			builder.setStarExp(0);
			// 减去上一级经验
			GodlyLevelExpCfg godlyLevelExpCfg = GodlyLevelExpCfg.getConfigByLevel(equipEntity.getStarLevel() - 1);
			builder.setStarExp(equipEntity.getStarExp() - godlyLevelExpCfg.getExp());
		} else {
			builder.setStarExp(equipEntity.getStarExp());
		}
		builder.setStarLevel(equipEntity.getStarLevel());
		builder.setStatus(equipEntity.getStatus());
		builder.setStrength(equipEntity.getStrength());
		builder.setGodlyAttrId(equipEntity.getGodlyAttrId());

		EquipCfg cfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipEntity.getEquipId());
		if (cfg == null) {
			builder.setScore(0);
		} else {
			builder.setScore(cfg.getEquipScore());
		}

		builder.setLock(false);
		if (equipEntity.getStarLevel2() > 1) {
			builder.setStarExp2(0);
			// 减去上一级经验
			GodlyLevelExpCfg godlyLevelExpCfg = GodlyLevelExpCfg.getConfigByLevel(equipEntity.getStarLevel2() - 1);
			builder.setStarExp2(equipEntity.getStarExp2() - godlyLevelExpCfg.getExp2());
		} else {
			builder.setStarExp2(equipEntity.getStarExp2());
		}
		builder.setStarLevel2(equipEntity.getStarLevel2());
		builder.setGodlyAttrId2(equipEntity.getGodlyAttrId2());

		// 主属性
		Attribute equipAttribute = equipEntity.getAttribute();
		
		if (cfg != null) {
			for (AttrValue attrValue:cfg.getPrimaryAttr()) {
				equipInfoBuilderAttachAttr(builder,attrValue.getAttrId(),equipAttribute.getValue(attrValue.getAttrId()),Const.equipAttrGrade.PRIMARY_ATTR_VALUE);
			}
		}
//		equipInfoBuilderAttachAttr(builder, equipEntity.getPrimaryAttrType1(),
//				equipAttribute.getValue(equipEntity.getPrimaryAttrType1()), Const.equipAttrGrade.PRIMARY_ATTR_VALUE);
//		equipInfoBuilderAttachAttr(builder, equipEntity.getPrimaryAttrType2(),
//				equipAttribute.getValue(equipEntity.getPrimaryAttrType2()), Const.equipAttrGrade.PRIMARY_ATTR_VALUE);
//		equipInfoBuilderAttachAttr(builder, equipEntity.getPrimaryAttrType3(),
//				equipAttribute.getValue(equipEntity.getPrimaryAttrType3()), Const.equipAttrGrade.PRIMARY_ATTR_VALUE);

		// 副属性
		equipInfoBuilderAttachAttr(builder, equipEntity.getSecondaryAttrType1(),
				equipAttribute.getValue(equipEntity.getSecondaryAttrType1()),
				Const.equipAttrGrade.SECONDARY_ATTR_VALUE);
		equipInfoBuilderAttachAttr(builder, equipEntity.getSecondaryAttrType2(),
				equipAttribute.getValue(equipEntity.getSecondaryAttrType2()),
				Const.equipAttrGrade.SECONDARY_ATTR_VALUE);
		equipInfoBuilderAttachAttr(builder, equipEntity.getSecondaryAttrType3(),
				equipAttribute.getValue(equipEntity.getSecondaryAttrType3()),
				Const.equipAttrGrade.SECONDARY_ATTR_VALUE);
		equipInfoBuilderAttachAttr(builder, equipEntity.getSecondaryAttrType4(),
				equipAttribute.getValue(equipEntity.getSecondaryAttrType4()),
				Const.equipAttrGrade.SECONDARY_ATTR_VALUE);

		// 宝石数据
		equipInfoBuilderAttachGemAttr(builder, 1, equipEntity.getGem1());
		equipInfoBuilderAttachGemAttr(builder, 2, equipEntity.getGem2());
		equipInfoBuilderAttachGemAttr(builder, 3, equipEntity.getGem3());
		equipInfoBuilderAttachGemAttr(builder, 4, equipEntity.getGem4());

		return builder;
	}

	/**
	 * 生成元素实体的builder信息
	 * 
	 * @return
	 */
	public static ElementInfo.Builder genElementBuilder(ElementEntity elementEntity) {
		ElementInfo.Builder builder = ElementInfo.newBuilder();
		builder.setId(elementEntity.getId());
		ElementCfg elementCfg = ConfigManager.getInstance().getConfigByKey(ElementCfg.class, elementEntity.getItemId());
		if (elementCfg != null) {
			builder.setProfLimit(elementCfg.getProfLimit());
		} else {
			builder.setProfLimit(0);
		}

		builder.setExp(elementEntity.getExp());
		builder.setLevel(elementEntity.getLevel());
		builder.setQuality(elementEntity.getQuality());

		// 主属性
		Attribute basicAttr = elementEntity.getBasicAttr();
		AttrInfo.Builder attrInfoBuilder = AttrInfo.newBuilder();
		for (Map.Entry<Const.attr, Integer> entry : basicAttr.getAttrMap().entrySet()) {
			Attr.Builder attrBuilder = Attr.newBuilder();
			attrBuilder.setAttrId(entry.getKey().getNumber());
			attrBuilder.setAttrValue(entry.getValue());
			attrInfoBuilder.addAttribute(attrBuilder);
		}
		builder.setBasicAttrs(attrInfoBuilder);
		// 附带属性
		Attribute extraAttr = elementEntity.getExtraAttr();
		AttrInfo.Builder extraAttrInfoBuilder = AttrInfo.newBuilder();
		for (Map.Entry<Const.attr, Integer> entry : extraAttr.getAttrMap().entrySet()) {
			Attr.Builder attrBuilder = Attr.newBuilder();
			attrBuilder.setAttrId(entry.getKey().getNumber());
			attrBuilder.setAttrValue(entry.getValue());
			extraAttrInfoBuilder.addAttribute(attrBuilder);
		}
		builder.setExtraAttrs(extraAttrInfoBuilder);
		builder.setItemId(elementEntity.getItemId());
		if (elementEntity.getRecastAttrId() > 0) {
			builder.setRecastAttrId(elementEntity.getRecastAttrId());

			for (int i = 0; i < elementEntity.getRecastAttrIds().size(); i++) {
				Attr.Builder attrBuilder = Attr.newBuilder();
				attrBuilder.setAttrId(elementEntity.getRecastAttrIds().get(i));
				attrBuilder.setAttrValue(elementEntity.getRecastAttrValues().get(i));
				builder.addAttrs(attrBuilder);
			}
		}
		return builder;
	}

	/**
	 * 生成技能协议同步builder
	 * 
	 * @param skillEntity
	 * @return
	 */
	public static SkillInfo.Builder genSkillBuilder(SkillEntity skillEntity) {
		SkillInfo.Builder builder = SkillInfo.newBuilder();
		builder.setId(skillEntity.getId());
		builder.setItemId(skillEntity.getItemId());
		builder.setSkillLevel(skillEntity.getSkillLevel());
		builder.setStatus(skillEntity.getStatus());
		builder.setRoleId(skillEntity.getRoleId());
		builder.setExp(skillEntity.getExp());
		return builder;
	}

	/**
	 * 生成光环协议同步builder
	 * 
	 * @param skillEntity
	 * @return
	 */
	public static RoleRingInfo.Builder genRingInfoBuilder(RoleRingEntity roleRingEntity) {
		RoleRingInfo.Builder builder = RoleRingInfo.newBuilder();
		builder.setRingId(roleRingEntity.getId());
		builder.setItemId(roleRingEntity.getItemId());
		builder.setLevel(roleRingEntity.getLevel());
		builder.setExp(roleRingEntity.getExp());
		builder.setRoleId(roleRingEntity.getRoleId());
		builder.setLvlUpTimes(roleRingEntity.getLvlUpTimes());
		return builder;
	}

	/**
	 * 属性协议同步builder
	 * 
	 * @param attribute
	 * @return
	 */
	public static AttrInfo.Builder genAttributeBuilder(Attribute attribute) {
		Map<Const.attr, Integer> attrMap = attribute.getAttrMap();

		AttrInfo.Builder builder = AttrInfo.newBuilder();
		Iterator<Map.Entry<Const.attr, Integer>> iterator = attrMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Const.attr, Integer> entry = iterator.next();
//			if (entry.getValue() > 0) {
			Attr.Builder attrBuilder = Attr.newBuilder();
			attrBuilder.setAttrId(entry.getKey().getNumber());
			attrBuilder.setAttrValue(entry.getValue());
			builder.addAttribute(attrBuilder);
//			}
		}
		return builder;
	}

	/**
	 * 状态协议同步builder
	 * 
	 * @param player
	 * @param stateEntity
	 * @return
	 */
	public static HPPlayerStateSync.Builder genStateBuilder(StateEntity stateEntity, MonthCardStatus monthCardStatus,
			boolean isFirstLogin) {
		HPPlayerStateSync.Builder builder = HPPlayerStateSync.newBuilder();

		builder.setBuyCoinTimes(0);
		builder.setCurrentEquipBagSize(stateEntity.getEquipBagSize());
		builder.setLeftEquipBagExtendTimes(stateEntity.getLeftEquipBagExtendTimes());
		builder.setAutoSellEquip(stateEntity.getAutoSellEquip());
		builder.setChatClose(stateEntity.getChatClose());
		builder.setFastFightTimes(stateEntity.getFastFightTimes());
		builder.setFastFightBuyTimes(stateEntity.getFastFightBuyTimes());
		builder.setBossFightTimes(stateEntity.getBossFightTimes());
		builder.setBossFightBuyTimes(stateEntity.getBossFightBuyTimes());
		builder.setNextBattleTime(stateEntity.getNextBattleTime());
		builder.setCurBattleMap(stateEntity.getCurBattleMap());
		builder.setPassMapId(stateEntity.getPassMapId());
		builder.setOnlyText(stateEntity.isOnlyText() ? 1 : 0);
		if (SysBasicCfg.getInstance().getGiftAward() != null) {
			builder.setGiftStatus(stateEntity.getGiftStatus());
		} else {
			builder.setGiftStatus(-1);
		}
		builder.setBossWipe(stateEntity.getWipeBoss());
		builder.setEquipSmeltRefesh(stateEntity.getEquipSmeltRefesh());
		Date gongceCompleteDay = stateEntity.getGongceWordDay();
		if (gongceCompleteDay != null) {
			int spaceDays = GuaJiTime.calcBetweenDays(gongceCompleteDay, GuaJiTime.getCalendar().getTime()) + 1;
			builder.setGongceCompleteDays(spaceDays);
		} else {
			builder.setGongceCompleteDays(0);
		}
		// 设置字体大小
		builder.setMusicOn(stateEntity.getMusic());
		builder.setSoundOn(stateEntity.getSound());
		builder.setShowArea(stateEntity.isShowArea() ? 1 : 0);
		builder.setPassEliteMapId(stateEntity.getPassEliteMapId());
		builder.setStarStoneTimes(stateEntity.getStarStoneTimes());
		builder.setEliteFightTimes(stateEntity.getEliteMapTimes());
		builder.setEliteFightBuyTimes(stateEntity.getEliteMapBuyTimes());
		builder.setBattleTimes(stateEntity.getMultiFirstBattle());
		builder.setNewGuideState(stateEntity.getNewGuideState());
		builder.setIsFirstLogin(isFirstLogin);
		if (CrossBattleCfg.getInstance().getCrossserverOpen()
				&& CrossGroupCfg.getGroupCfg(GsApp.getInstance().getServerIdentify()) != null) {
			builder.setIsCSOPen(true);
		} else {
			builder.setIsCSOPen(false);
		}
		builder.setElementBagSize(stateEntity.getElementBagSize());
		builder.setMultiEliteTimes(stateEntity.getMultiEliteTimes());
		builder.setAutoDecoElement(stateEntity.getAutoDecoElement());
		builder.setHourCardUseCountOneDay(stateEntity.getHourCardUseCountOneDay());
		builder.setLeftFreeFastFightTimes(monthCardStatus.getLeftFreeFastFightTimes());
		builder.setLeftFreeRefreshShopTimes(monthCardStatus.getLeftFreeRefreshShopTimes());
		builder.setLeftFreeRefreshMakeEquipTimes(monthCardStatus.getLeftFreeRefreshMakeEquipTimes());
		builder.setGemShopBuyCount(stateEntity.getGemShopBuyCount());
		builder.setCurrentBadgeBagSize(stateEntity.getBadgeBagSize());
		builder.setLeftBadgeBagExtendTimes(stateEntity.getLeftBadgeBagExtendTimes());
		builder.setMultiEliteBuyTimes(stateEntity.getTodayBuyMultiEliteTimes());
		builder.setFriendship(stateEntity.getFriendship());
		builder.setVipPoint(stateEntity.getVipPoint());
		return builder;
	}

	/**
	 * 邮件协议列表
	 * 
	 * @param player
	 * @param emailEntities
	 * @param maxId
	 * @return
	 */
	public static OPMailInfoRet.Builder getMailInfoBuilder(Player player, List<Integer> delMail,
			Map<Integer, EmailEntity> emailEntities, int maxId) {

		OPMailInfoRet.Builder builder = OPMailInfoRet.newBuilder();
		builder.setVersion(1);
		builder.addAllDelmail(delMail);
		for (Entry<Integer, EmailEntity> entry : emailEntities.entrySet()) {
			if (entry.getValue().getId() <= maxId && entry.getValue().getMailId() != GsConst.MailId.MONTH_CARD_REWARD) {
				continue;
			}

			MailInfo.Builder infoBuilder = MailInfo.newBuilder();
			infoBuilder.setId(entry.getValue().getId());
			infoBuilder.setType(Mail.MailType.valueOf(entry.getValue().getType()));
			infoBuilder.setTitle(entry.getValue().getTitle());
			infoBuilder.setMailClassify(entry.getValue().getClassification());
			AwardItems awardItems = AwardItems.valueOf(entry.getValue().getContent());
			if (awardItems != null) {
				List<Item> items = awardItems.getAwardItems();
				for (Item item : items) {
					Reward.RewardItem.Builder itemBuilder = Reward.RewardItem.newBuilder();
					itemBuilder.setItemId(item.getId());
					itemBuilder.setItemCount(item.getCount());
					itemBuilder.setItemType(item.getType());
					infoBuilder.addItem(itemBuilder);
				}
			}
			infoBuilder.setContent(entry.getValue().getContent());
			infoBuilder.setMailId(entry.getValue().getMailId());
			// 韩国玩家不登录就不给他发当天的邮件
			if (GJLocal.isLocal(GJLocal.KOREAN)) {
				EmailEntity email = entry.getValue();
				if (email.getMailId() == GsConst.MailId.MONTH_CARD_REWARD) {
					// 邮件的生效时间小于今天的0点,把邮件删除
					if (email.getEffectTime().getTime() < GuaJiTime.getAM0Date().getTime()) {
						email.delete();
						continue;
					}
				}
			}

			EmailEntity emailEntity = entry.getValue();
			if (emailEntity.getType() != MailType.MULTI_ELITE_VALUE) {
				for (String param : entry.getValue().getParamsList()) {
					infoBuilder.addParams(param);
				}
			}

			if ((emailEntity.getMailId() >= GsConst.MailId.APPLY_ADD_ALLIANCE
					&& emailEntity.getMailId() <= GsConst.MailId.CHANGE_MAIN_SENDTO_NEW_MAIN)
					|| emailEntity.getType() == MailType.MULTI_ELITE_VALUE) {
				infoBuilder.setPassthroughParams(entry.getValue().getParams());
			}

			infoBuilder.setCreateTime((int) (entry.getValue().getCreateTime().getTime() / 1000));
			builder.addMails(infoBuilder);
		}
		return builder;
	}

	/**
	 * 生成地图统计信息协议
	 * 
	 * @param mapStatisticsEntity
	 * @return
	 */
	public static HPMapStatisticsSync.Builder genMapStatisticsBuilder(MapStatisticsEntity mapStatisticsEntity) {
		HPMapStatisticsSync.Builder builder = HPMapStatisticsSync.newBuilder();
		builder.setMapId(mapStatisticsEntity.getMapId());
		builder.setFightTimes(mapStatisticsEntity.getFightTimes());
		builder.setAverageTime(mapStatisticsEntity.getAverageTime());
		builder.setWinRate(mapStatisticsEntity.getWinRate());
		builder.setEquipRate(mapStatisticsEntity.getEquipRate());
		builder.setExpRate(mapStatisticsEntity.getExpRate());
		builder.setCoinRate(mapStatisticsEntity.getCoinRate());
		builder.setItemOneCount(mapStatisticsEntity.getItemOneRate());
		return builder;
	}

	/**
	 * 生成多人副本怪物对应的角色信息
	 * 
	 * @param monsterId
	 * @return
	 */
	public static RoleInfo.Builder genMultiEliteMonsterRoleInfoBuilder(int monsterId) {
//		MultiMonsterCfg monsterCfg = ConfigManager.getInstance().getConfigByKey(MultiMonsterCfg.class, monsterId);
//		if (monsterCfg != null) {
//			RoleInfo.Builder builder = RoleInfo.newBuilder();
//			builder.setRoleId(monsterId);
//			builder.setType(Const.roleType.MULTI_MONSTER_VALUE);
//			builder.setItemId(monsterCfg.getItemId());
//			builder.setProf(monsterCfg.getProfession());
//			// name 为 客户端使用
//			builder.setName(monsterCfg.getName());
//			builder.setLevel(monsterCfg.getLevel());
//			builder.setExp(0);
//			builder.setFight(monsterCfg.getFight());
//			builder.setStatus(0);
//
//			List<Integer> skillIds = monsterCfg.getSkillIds();
//			for (Integer skillId : skillIds) {
//				RoleSkill.Builder skillBuilder = RoleSkill.newBuilder();
//				skillBuilder.setSkillId(skillId);
//				skillBuilder.setItemId(skillId);
//				builder.addSkills(skillBuilder);
//			}
//			builder.setAttribute(genAttributeBuilder(monsterCfg.getAttribute()));
//
//			return builder;
//		}
		return null;
	}

	/**
	 * 挂载角色装备信息
	 * 
	 * @param equipEntities
	 * @param builder
	 * @param part
	 * @param equipId
	 */
	public static void roleInfoBuilderAttachRoleEquip(List<EquipEntity> equipEntities, RoleInfo.Builder builder,
			int part, long equipId) {
		if (builder != null && part > 0 && equipId > 0) {
			for (EquipEntity equipEntity : equipEntities) {
				if (equipEntity.getId() != equipId) {
					continue;
				}

				RoleEquip.Builder roleEquipBuilder = RoleEquip.newBuilder();
				roleEquipBuilder.setPart(part);
				roleEquipBuilder.setEquipId(equipEntity.getId());
				roleEquipBuilder.setEquipItemId(equipEntity.getEquipId());
				roleEquipBuilder.setStrength(equipEntity.getStrength());

				// 宝石属性
				roleEquipBuilderAttachGemAttr(roleEquipBuilder, 1, equipEntity.getGem1());
				roleEquipBuilderAttachGemAttr(roleEquipBuilder, 2, equipEntity.getGem2());
				roleEquipBuilderAttachGemAttr(roleEquipBuilder, 3, equipEntity.getGem3());
				roleEquipBuilderAttachGemAttr(roleEquipBuilder, 4, equipEntity.getGem4());
				builder.addEquips(roleEquipBuilder);
				break;
			}
		}
	}

	/**
	 * 添加宝石属性
	 * 
	 * @param builder
	 * @param pos
	 * @param gemId
	 */
	public static void equipInfoBuilderAttachGemAttr(EquipInfo.Builder builder, int pos, int gemId) {
		GemInfo.Builder gemInfo = GemInfo.newBuilder();
		gemInfo.setPos(pos);
		if (gemId >= 0) {
			gemInfo.setGemItemId(gemId);
		} else {
			gemInfo.setGemItemId(-1);
		}
		builder.addGemInfos(gemInfo);
	}

	/**
	 * 装备挂载宝石信息
	 * 
	 * @param builder
	 * @param pos
	 * @param gemId
	 */
	public static void roleEquipBuilderAttachGemAttr(RoleEquip.Builder builder, int pos, int gemId) {
		if (gemId >= 0) {
			GemInfo.Builder gemInfo = GemInfo.newBuilder();
			gemInfo.setPos(pos);
			gemInfo.setGemItemId(gemId);
			builder.addGemInfo(gemInfo);
		}
	}

	/**
	 * 增加装备builder的属性数据
	 * 
	 * @param builder
	 * @param attrType
	 * @param attrValue
	 * @param attrGrade
	 */
	public static void equipInfoBuilderAttachAttr(EquipInfo.Builder builder, int attrType, int attrValue,
			int attrGrade) {
		if (attrType > 0 && attrValue > 0) {
			EquipAttr.Builder equipAttrBuilder = EquipAttr.newBuilder();
			Attr.Builder attrInfoBuilder = Attr.newBuilder();
			attrInfoBuilder.setAttrId(attrType);
			attrInfoBuilder.setAttrValue(attrValue);
			equipAttrBuilder.setAttrData(attrInfoBuilder);
			equipAttrBuilder.setAttrGrade(attrGrade);
			builder.addAttrInfos(equipAttrBuilder);
		}
	}

	/**
	 * 生成ItemInfo builder
	 * 
	 * @param itemInfo
	 * @return
	 */
	public static RewardItem.Builder genItemInfoBuilder(com.guaji.game.item.ItemInfo itemInfo) {
		RewardItem.Builder itemInfoBuilder = RewardItem.newBuilder();
		itemInfoBuilder.setItemId(itemInfo.getItemId());
		itemInfoBuilder.setItemType(itemInfo.getType());
		itemInfoBuilder.setItemCount(itemInfo.getQuantity());
		return itemInfoBuilder;
	}

	/**
	 * 生成MsgInfo Builder
	 */
	public static MsgInfo.Builder genMsgInfoBuilder(MsgEntity msgEntity, int senderRoleCfgId) {
		MsgInfo.Builder builder = MsgInfo.newBuilder();
		builder.setMsgId(msgEntity.getId());
		builder.setSourcePlayerId(msgEntity.getSenderId());
		builder.setContent(msgEntity.getContent());
		builder.setMsgTime(msgEntity.getCreateTime().toString());
		builder.setRoleId(senderRoleCfgId);
		return builder;
	}

	/**
	 * 生成团战队伍成员信息
	 */
	public static List<MemberInfo.Builder> genTeamBattleMemberBuilder(TeamEntity teamEntity) {
		List<MemberInfo.Builder> memberInfoList = new ArrayList<MemberInfo.Builder>();
		// 深Copy，保存当前队伍成员，防止正在遍历时成员变更
		ArrayList<Integer> tmpMembers = new ArrayList<Integer>(teamEntity.getTeamMembers());
		for (int memberId : tmpMembers) {
			MemberInfo.Builder builder = MemberInfo.newBuilder();
			PlayerSnapshotInfo.Builder snapshotInfo = SnapShotManager.getInstance().getPlayerSnapShot(memberId);
			RoleInfo mainRoleInfo = snapshotInfo.getMainRoleInfo();
			builder.setRoleCfgId(mainRoleInfo.getItemId());
			builder.setPlayerId(snapshotInfo.getPlayerInfo().getPlayerId());
			builder.setPlayerName(mainRoleInfo.getName());
			builder.setFightValue(mainRoleInfo.getFight());

			// 玩家竞技技能
			for (int index = 0; index < mainRoleInfo.getSkills2Count(); index++) {
				RoleSkill roleSkill = mainRoleInfo.getSkills2(index);
				if (roleSkill.getSkillId() > 0 && roleSkill.getItemId() > 0) {
					builder.addSkillCfgId(roleSkill.getItemId());
					builder.addSkillLevel(roleSkill.getLevel());
				}
			}
			int isCaptain = (memberId == teamEntity.getCaptainId()) ? GsConst.TeamBattle.IS_CAPTAIN
					: GsConst.TeamBattle.IS_NOT_CAPTAIN;
			builder.setIsCaptain(isCaptain);
			memberInfoList.add(builder);
		}

		return memberInfoList;
	}

	public static RewardItem.Builder genEuiqpRewardBuilder(EquipEntity equipEntity) {
		RewardItem.Builder itemBuilder = RewardItem.newBuilder();
		itemBuilder.setItemId(equipEntity.getEquipId());
		itemBuilder.setItemCount(1);
		itemBuilder.setItemType(Const.itemType.EQUIP_VALUE * GsConst.ITEM_TYPE_BASE);
		if (equipEntity.getGodlyAttrId() > 0) {
			itemBuilder.setItemStatus(1);
		}
		return itemBuilder;
	}

	public static RewardItem.Builder genEuiqpRewardBuilder(EquipEntity equipEntity, int count) {
		RewardItem.Builder itemBuilder = RewardItem.newBuilder();
		itemBuilder.setItemId(equipEntity.getEquipId());
		itemBuilder.setItemCount(count);
		itemBuilder.setItemType(Const.itemType.EQUIP_VALUE * GsConst.ITEM_TYPE_BASE);
		if (equipEntity.getGodlyAttrId() > 0) {
			itemBuilder.setItemStatus(1);
		}
		return itemBuilder;
	}

	public static RewardItem.Builder genItemRewardBuilder(ItemEntity itemEntity) {
		RewardItem.Builder itemBuilder = RewardItem.newBuilder();
		itemBuilder.setItemId(itemEntity.getItemId());
		itemBuilder.setItemCount(itemEntity.getItemCount());
		itemBuilder.setItemType(Const.itemType.TOOL_VALUE * GsConst.ITEM_TYPE_BASE);
		return itemBuilder;
	}

	/**
	 * 构造月卡Info的builder
	 * 
	 * @param monthCardStatus
	 * @return
	 */
	public static HPMonthCardInfoRet.Builder genMonthCardStatus(MonthCardStatus monthCardStatus) {
		HPMonthCardInfoRet.Builder builder = HPMonthCardInfoRet.newBuilder();
		builder.setActiveCfgId(monthCardStatus.getCurrentActiveCfgId());
		builder.setIsTodayRewardGot(monthCardStatus.isRewardToday());
		builder.setLeftDays(monthCardStatus.getLeftDays());
		return builder;
	}

	/**
	 * 构造消耗型月卡Info的builder
	 * 
	 * @param monthCardStatus
	 * @return
	 */
	public static ConsumeMonthCardInfoRet.Builder genConMonthCardStatus(ConMonthCardStatus monthCardStatus) {

		ConsumeMonthCardInfoRet.Builder builder = ConsumeMonthCardInfoRet.newBuilder();
		builder.setActiveCfgId(monthCardStatus.getCurrentActiveCfgId());
		builder.setIsTodayRewardGot(monthCardStatus.isRewardToday());
		builder.setIsBuyRewardGot(monthCardStatus.getLastRewadTime() != null); // 有上次領取時間就代表已經領取
		builder.setLeftDays(monthCardStatus.getLeftDays());
		return builder;
	}

	/**
	 * 构造打折礼包信息
	 */
	public static HPSalePacketInfoRet.Builder genSalePacketStatus(int lastTime, SalePacketStatus salePacketStatus) {
		HPSalePacketInfoRet.Builder builder = HPSalePacketInfoRet.newBuilder();
		Map<Object, SalePacketCfg> configMap = ConfigManager.getInstance().getConfigMap(SalePacketCfg.class);

		for (SalePacketCfg cfg : configMap.values()) {
			HPSalePacketItem.Builder itemInfo = HPSalePacketItem.newBuilder();
			if (salePacketStatus.getInfo().containsKey(cfg.getId())) {
				itemInfo.setBuytime(salePacketStatus.getInfo().get(cfg.getId()).getBuyTime());
				itemInfo.setState(salePacketStatus.getInfo().get(cfg.getId()).getState());
				itemInfo.setGoodid(cfg.getId());
			} else {

				itemInfo.setBuytime(0);
				itemInfo.setState(0);
				itemInfo.setGoodid(cfg.getId());
			}
			builder.addSalePacketLst(itemInfo);

		}
		builder.setLeftTime(lastTime);

		return builder;
	}

	/**
	 * vip礼包信息
	 */
	public static HPVipPackageInfoRet.Builder genVipPackageStatus(VipPackageStatus vipPackageStatus) {
		HPVipPackageInfoRet.Builder builder = HPVipPackageInfoRet.newBuilder();
		ArrayList<Integer> packetList = new ArrayList<Integer>();
		ArrayList<Long> packetListGetTime = new ArrayList<Long>();
		Iterator<Integer> it = vipPackageStatus.getInfo().keySet().iterator();

		while (it.hasNext()) {
			int id = it.next();
			packetList.add(id);
			packetListGetTime.add(vipPackageStatus.getPacketGetTime(id));
		}

		builder.addAllVipPackageList(packetList);
		builder.addAllGetVipPackageTime(packetListGetTime);
		builder.setDefault(1);

		return builder;
	}

	/**
	 * 构造七夕折扣信息
	 */
	public static HPExchangeInfoRet.Builder genExchangeStatus(int lastTime, ExchangeStatus exchangeStatus) {
		HPExchangeInfoRet.Builder builder = HPExchangeInfoRet.newBuilder();
		ArrayList<String> idList = new ArrayList<String>();
		ArrayList<Integer> countList = new ArrayList<Integer>();
		Iterator<String> it = exchangeStatus.getExchangeInfo().keySet().iterator();

		while (it.hasNext()) {
			String id = it.next();
			idList.add(id);
			countList.add(exchangeStatus.getExchangeCount(id));
		}

		builder.addAllExchangeIdList(idList);
		builder.addAllExchangeTimes(countList);
		builder.setLastCount(lastTime);
		return builder;
	}

	/**
	 * 构造兑换所信息
	 */
	public static HPExchangeInfoRet.Builder genExchangeShopStatus(int lastTime, ExchangeShopStatus exchangeStatus,
			Map<String, Integer> exchangeMap) {
		HPExchangeInfoRet.Builder builder = HPExchangeInfoRet.newBuilder();
		ArrayList<String> idList = new ArrayList<String>();
		ArrayList<Integer> countList = new ArrayList<Integer>();
		Iterator<String> it = exchangeStatus.getExchangeInfo().keySet().iterator();

		while (it.hasNext()) {
			String id = it.next();
			idList.add(id);
			countList.add(exchangeStatus.getExchangeCount(id));
		}

		// 副将兑换数量（无需重置）
		for (Entry<String, Integer> entry : exchangeMap.entrySet()) {
			idList.add(entry.getKey());
			countList.add(entry.getValue());
		}

		builder.addAllExchangeIdList(idList);
		builder.addAllExchangeTimes(countList);
		builder.setLastCount(lastTime);
		return builder;
	}

	/**
	 * 好友builder构造
	 * 
	 * @param playerIds
	 * @param onlineOnly true:只要在线好友，false:所有好友
	 * @return
	 */
	public static List<FriendItem.Builder> genFriendItemBuilders(Set<Integer> playerIds, boolean onlineOnly) {
		if (playerIds == null || playerIds.size() == 0) {
			return null;
		}
		List<FriendItem.Builder> friends = new ArrayList<FriendItem.Builder>();
		for (int playerId : playerIds) {
			Player player = PlayerUtil.queryPlayer(playerId);
			if (onlineOnly && (player == null || !player.isOnline())) {
				continue;
			}
			FriendItem.Builder friend = genSingleFriendItem(playerId);
			if (friend != null) {
				friends.add(friend);
			}
		}
		return friends;
	}

	/**
	 * 单个好友builder构造
	 * 
	 * @param playerId
	 * @return
	 */
	public static FriendItem.Builder genSingleFriendItem(int playerId) {
		PlayerSnapshotInfo.Builder snapShotBuilder = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
		if (snapShotBuilder == null) {
			return null;
		}
		FriendItem.Builder friendItemBuilder = FriendItem.newBuilder();
		friendItemBuilder.setPlayerId(snapShotBuilder.getPlayerId());
		friendItemBuilder.setLevel(snapShotBuilder.getMainRoleInfo().getLevel());
		friendItemBuilder.setName(snapShotBuilder.getMainRoleInfo().getName());
		friendItemBuilder.setFightValue(snapShotBuilder.getMainRoleInfo().getMarsterFight());
		friendItemBuilder.setRoleId(snapShotBuilder.getMainRoleInfo().getItemId());
		friendItemBuilder.setRebirthStage(snapShotBuilder.getMainRoleInfo().getRebirthStage());
		friendItemBuilder.setSignature(snapShotBuilder.getPlayerInfo().getSignature());
		friendItemBuilder.setHeadIcon(snapShotBuilder.getPlayerInfo().getHeadIcon());
		Player player = PlayerUtil.queryPlayer(playerId);
		if (player != null && player.isOnline()) {
			friendItemBuilder.setOfflineTime(0);
		} else {
			friendItemBuilder
					.setOfflineTime((int) (GuaJiTime.getMillisecond() / 1000 - snapShotBuilder.getLastLogoutTime()));
		}
		friendItemBuilder.setAvatarId(snapShotBuilder.getMainRoleInfo().getAvatarId());
		return friendItemBuilder;
	}

	/**
	 * 单个好友builder构造
	 * 
	 * @param playerId
	 * @return
	 */
	public static FriendItem.Builder genSingleFriendItemByName(String name) {
		PlayerSnapshotInfo.Builder snapShotBuilder = SnapShotManager.getInstance().getPlayerSnapShot(name);
		if (snapShotBuilder == null) {
			return null;
		}

		FriendItem.Builder friendItemBuilder = FriendItem.newBuilder();
		friendItemBuilder.setPlayerId(snapShotBuilder.getPlayerId());
		friendItemBuilder.setLevel(snapShotBuilder.getMainRoleInfo().getLevel());
		friendItemBuilder.setName(snapShotBuilder.getMainRoleInfo().getName());
		friendItemBuilder.setFightValue(snapShotBuilder.getMainRoleInfo().getMarsterFight());
		friendItemBuilder.setRoleId(snapShotBuilder.getMainRoleInfo().getItemId());
		friendItemBuilder.setRebirthStage(snapShotBuilder.getMainRoleInfo().getRebirthStage());
		friendItemBuilder.setSignature(snapShotBuilder.getPlayerInfo().getSignature());
		friendItemBuilder.setHeadIcon(snapShotBuilder.getPlayerInfo().getHeadIcon());

		Player player = PlayerUtil.queryPlayer(snapShotBuilder.getPlayerId());
		if (player != null && player.isOnline()) {
			friendItemBuilder.setOfflineTime(0);
		} else {
			friendItemBuilder
					.setOfflineTime((int) (GuaJiTime.getMillisecond() / 1000 - snapShotBuilder.getLastLogoutTime()));
		}
		friendItemBuilder.setAvatarId(snapShotBuilder.getMainRoleInfo().getAvatarId());
		return friendItemBuilder;
	}

	public static FriendItem.Builder genFriendItemBuilder(int playerId, String puid) {
		PlayerSnapshotInfo.Builder snapShotBuilder = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
		if (snapShotBuilder == null) {
			return null;
		}
		FriendItem.Builder friendItemBuilder = FriendItem.newBuilder();
		friendItemBuilder.setPlayerId(snapShotBuilder.getPlayerId());
		friendItemBuilder.setLevel(snapShotBuilder.getMainRoleInfo().getLevel());
		friendItemBuilder.setName(snapShotBuilder.getMainRoleInfo().getName());
		friendItemBuilder.setFightValue(snapShotBuilder.getMainRoleInfo().getFight());
		friendItemBuilder.setRoleId(snapShotBuilder.getMainRoleInfo().getItemId());
		friendItemBuilder.setRebirthStage(snapShotBuilder.getMainRoleInfo().getRebirthStage());
		friendItemBuilder.setAvatarId(snapShotBuilder.getMainRoleInfo().getAvatarId());
		return friendItemBuilder;
	}

	/**
	 * facebook好友builder构造
	 * 
	 * @param playerId
	 * @return
	 */
	public static FBFriendItem.Builder genFBFriendItemBuilder(String puid) {
		FBFriendItem.Builder friendItemBuilder = FBFriendItem.newBuilder();
		JedisPool jedisPool = GsApp.getInstance().getJedisPool();
		try (Jedis jedis = jedisPool.getResource()) {
			FBFriendBean bean = RedisUtil.getRedisObj(jedis, FBFriendBean.class, puid);
			if (bean == null) {
				return null;
			}
			friendItemBuilder.setUid(puid);
			friendItemBuilder.setArenaRank(bean.getArenaRank());
			friendItemBuilder.setFightValue(bean.getFightValue());
			friendItemBuilder.setLastserver(bean.getLastServer());
			friendItemBuilder.setLevel(bean.getLevel());
			friendItemBuilder.setVip(bean.getVip());
			return friendItemBuilder;
		}
	}

	/**
	 * 生成玩家公会信息builder
	 * 
	 * @param playerAllianceEntity
	 * @return
	 */
	public static AllianceInfo.Builder genAllianceBuilder(PlayerAllianceEntity playerAllianceEntity) {
		AllianceInfo.Builder builder = AllianceInfo.newBuilder();

		builder.setId(playerAllianceEntity.getId());
		builder.setPlayerId(playerAllianceEntity.getPlayerId());
		builder.setAllianceId(playerAllianceEntity.getAllianceId());
		builder.setContribution(playerAllianceEntity.getContribution());
		builder.setPostion(playerAllianceEntity.getPostion());
		builder.setAutoFight(playerAllianceEntity.getAutoFight());
		builder.setTotalVitality(playerAllianceEntity.getVitality());
		AllianceEntity entity = AllianceManager.getInstance().getAlliance(playerAllianceEntity.getAllianceId());
		if (entity != null) {
			builder.setAllianceName(entity.getName());
		}

		return builder;
	}

	/**
	 * 生成个人阵营信息
	 * 
	 * @param campWarEntity
	 * @return
	 */
	public static PersonalCampWarInfo.Builder genPersonalCampWarInfo(CampWarEntity campWarEntity) {
		PersonalCampWarInfo.Builder info = PersonalCampWarInfo.newBuilder();
		info.setCurRemainBlood(campWarEntity.getCurRemainBlood());
		info.setCurMaxBlood(campWarEntity.getCurMaxBlood());
		info.setInspireTimes(campWarEntity.getInspireTimes());
		int ratio = SysBasicCfg.getInstance().getCampWarInspireBonuses();
		info.setBonuses(campWarEntity.getInspireTimes() * ratio);
		info.setMaxWinStreak(campWarEntity.getMaxWinStreak());
		info.setCurWinStreak(campWarEntity.getCurWinStreak());
		info.setTotalWin(campWarEntity.getTotalWin());
		info.setTotalLose(campWarEntity.getTotalLose());
		info.setTotalReputation(campWarEntity.getTotalReputation());
		info.setTotalCoins(campWarEntity.getTotalCoins());
		info.setCampId(campWarEntity.getCampId());
		return info;
	}

	/**
	 * 生成个人阵营信息
	 * 
	 * @param campWarEntity
	 * @return
	 */
	public static MultiKillRankInfo.Builder genCampWarMultiKillRankInfo(CampWarEntity campWarEntity, int rank) {
		MultiKillRankInfo.Builder info = MultiKillRankInfo.newBuilder();
		info.setRank(rank);
		info.setPlayerId(campWarEntity.getPlayerId());
		info.setRoleItemId(campWarEntity.getRoleCfgId());
		info.setPlayerName(campWarEntity.getPlayerName());
		info.setMultiKillQty(campWarEntity.getMaxWinStreak());
		info.setCampId(campWarEntity.getCampId());
		info.setReputation(campWarEntity.getTotalReputation());
		return info;
	}

	/**
	 * 生成称号信息
	 * 
	 * @param titleEntity
	 * @return
	 */
	public static TitleInfo.Builder genTitleBuilder(TitleEntity titleEntity) {
		TitleInfo.Builder builder = TitleInfo.newBuilder();
		for (int titleId : titleEntity.getFinishIdSet()) {
			builder.addTitleIds(titleId);
		}
		builder.setTitleId(titleEntity.getUseId());
		return builder;
	}

	/**
	 * 生成周卡活动信息
	 * 
	 * @param weekCardStatus
	 * @param activityTimeCfg
	 * @return
	 */
	public static HPWeekCardInfoRet.Builder genWeekCardInfo(WeekCardStatus weekCardStatus,
			ActivityTimeCfg activityTimeCfg) {
		HPWeekCardInfoRet.Builder weekCardInfoBuilder = HPWeekCardInfoRet.newBuilder();
		int currentActiveCfgId = weekCardStatus.getCurrentActiveCfgId();
		weekCardInfoBuilder.setActiveWeekCardId(currentActiveCfgId);

		Map<Object, WeekCardCfg> weekCardCfgs = ConfigManager.getInstance().getConfigMap(WeekCardCfg.class);
		weekCardInfoBuilder.setLevelUpProductId(-1);
		if (weekCardCfgs.containsKey(currentActiveCfgId)) {
			WeekCardCfg cfg = weekCardCfgs.get(currentActiveCfgId);
			weekCardInfoBuilder.setLevelUpProductId(cfg.getLevelUpGoodsId());
		}
		weekCardInfoBuilder.setLeftDays(weekCardStatus.getLeftDays());
		if (weekCardStatus.getLeftDays() > 0) {
			weekCardInfoBuilder.setIsNeedYestReward(weekCardStatus.isNeedYestReward() ? 1 : 0);
			weekCardInfoBuilder.setIsTodayReward(weekCardStatus.isRewardToday() ? 1 : 0);
			weekCardInfoBuilder.setLeftSenconds(activityTimeCfg.calcActivitySurplusTime());

		} else {
			weekCardInfoBuilder.setIsNeedYestReward(1);
			weekCardInfoBuilder.setIsTodayReward(1);
			weekCardInfoBuilder.setLeftSenconds(1);
		}

		for (Map.Entry<Object, WeekCardCfg> entry : weekCardCfgs.entrySet()) {
			WeekCardCfg weekCardCfg = entry.getValue();
			weekCardInfoBuilder.addProductId(weekCardCfg.getGoodsId());
		}
		return weekCardInfoBuilder;
	}

	/**
	 * 生成周卡活动信息
	 * 
	 * @param weekCardStatus
	 * @param activityTimeCfg
	 * @return
	 */
	public static ConsumeWeekCardInfoRet.Builder genConsumeWeekCardInfo(ConWeekCardStatus weekCardStatus,
			ActivityTimeCfg activityTimeCfg) {
		ConsumeWeekCardInfoRet.Builder weekCardInfoBuilder = ConsumeWeekCardInfoRet.newBuilder();
		int currentActiveCfgId = weekCardStatus.getCurrentActiveCfgId();
		weekCardInfoBuilder.setActiveWeekCardId(currentActiveCfgId);
		weekCardInfoBuilder.setLeftDays(weekCardStatus.getLeftDays());
		if (weekCardStatus.getLeftDays() > 0) {
			weekCardInfoBuilder.setIsTodayReward(weekCardStatus.isRewardToday() ? 1 : 0);
			weekCardInfoBuilder.setLeftSenconds(activityTimeCfg.calcActivitySurplusTime());
			weekCardInfoBuilder.setIsBuyRewardGot(true); // 有上次領取時間就代表已經領取
		} else {
			weekCardInfoBuilder.setIsTodayReward(1);
			weekCardInfoBuilder.setLeftSenconds(1);
			weekCardInfoBuilder.setIsBuyRewardGot(true);
		}
		return weekCardInfoBuilder;
	}

	public static HPClientSetting.Builder genClientSetting() {
		HPClientSetting.Builder clientSettingBuilder = HPClientSetting.newBuilder();
		for (ClientSettingCfg csc : ConfigManager.getInstance().getConfigList(ClientSettingCfg.class)) {
			Params.Builder params = Params.newBuilder();
			params.setKey(csc.getKey());
			params.setValue(csc.getValue());

			clientSettingBuilder.addParams(params);
		}
		return clientSettingBuilder;
	}

	public static AFRankList.Builder genAllianceBattleResultList() {
		AllianceBattleManager allianceBattleManager = AllianceBattleManager.getInstance();
		List<AllianceBattleItem> battleItems = allianceBattleManager.getTop8BattleItems();
		AFRankList.Builder rankListBuilder = AFRankList.newBuilder();
		for (AllianceBattleItem battleItem : battleItems) {
			rankListBuilder.addRankItemInfos(genAllianceBattleItem(battleItem));
		}

		return rankListBuilder;
	}

	public static AFRankList.Builder genAllianceBattleRankList(Player player, int selfAllianceId) {
		AllianceBattleManager allianceBattleManager = AllianceBattleManager.getInstance();
		List<AllianceBattleItem> battleItems = allianceBattleManager.getAllianceBattleItems();
		// 上期数据集
		AllianceBattleAgainstInfo lastAgainstInfo = allianceBattleManager.getLastBattleAgainstInfo();
		AFRankList.Builder rankListBuilder = AFRankList.newBuilder();
		for (AllianceBattleItem battleItem : battleItems) {
			AllianceItemInfo.Builder itemInfoBuilder = genAllianceBattleItem(battleItem);
			if (lastAgainstInfo != null) {
				// 如果有上期数据 就显示8强
				int lastBattleResult = lastAgainstInfo.getFightResult(battleItem.getAllianceId());
				if (lastBattleResult > 0 && lastBattleResult <= GsConst.AllianceBattle.TOP_8) {
					itemInfoBuilder.setLastResult(lastBattleResult);
				}
			}
			rankListBuilder.addRankItemInfos(itemInfoBuilder);
		}

		AllianceBattleItem battleItem = allianceBattleManager.getBattleItem(selfAllianceId);
		if (battleItem != null) {
			rankListBuilder.setSelfTotalVitality(battleItem.getVitality());
			int rankIndex = allianceBattleManager.getAllianceRank(selfAllianceId);
			rankListBuilder.setSelfRank(rankIndex);

			AllianceBattleVersusCfg versusCfg = null;

			if (rankIndex <= GsConst.AllianceBattle.ALLIANCE_BATTLE_RANK_SIZE) {
				versusCfg = AllianceBattleVersusCfg.getBattleVersusCfg(FightGroup.GROUP_32_VALUE, rankIndex);
				if (versusCfg == null) {
					versusCfg = AllianceBattleVersusCfg.getBattleVersusCfg(FightGroup.GROUP_32_VALUE,
							GsConst.AllianceBattle.ALLIANCE_BATTLE_RANK_SIZE - rankIndex + 1);
				}
			}

			if (versusCfg != null) {
				int targetIndex = versusCfg.getLeftIndex() == rankIndex ? versusCfg.getRightIndex()
						: versusCfg.getLeftIndex();
				AllianceBattleItem targetBattleItem = allianceBattleManager.getBatleItemByRank(targetIndex - 1);
				if (targetBattleItem != null) {
					// meiyou duishou
					rankListBuilder.setEstimateAllianceItemInfo(genAllianceBattleItem(targetBattleItem));
				}
			}
		}
		if (battleItem != null) {
			rankListBuilder.setHasJoined(battleItem.getMemberTeamIndex(player.getId()) > 0);
		}

		return rankListBuilder;
	}

	public static AllianceItemInfo.Builder genAllianceBattleItem(AllianceBattleItem battleItem) {
		AllianceItemInfo.Builder itemInfoBuilder = AllianceItemInfo.newBuilder();
		itemInfoBuilder.setId(battleItem.getAllianceId());
		itemInfoBuilder.setName(battleItem.getAllianceName());
		itemInfoBuilder.setCaptainName(battleItem.getCaptainName());
		itemInfoBuilder.setLevel(battleItem.getAllianceLevel());
		itemInfoBuilder.setVitality(battleItem.getVitality());
		itemInfoBuilder.setMemSize(battleItem.getMemberList().size());
		// 如果是结束领取奖励阶段 就有战斗结果 （冠军，亚军，4强，8强）
		if (battleItem.getBattleResult() > 0) {
			itemInfoBuilder.setResult(battleItem.getBattleResult());
		}
		itemInfoBuilder.setBuffTimes(battleItem.getBuffId());
		return itemInfoBuilder;
	}

	public static AFFightList.Builder genAllianceFightList(Player player, int allianceId,
			AllianceBattleAgainstInfo againstInfo, boolean isLast) {
		Map<Integer, List<AllianceFightVersus>> allianceAgainstMap = againstInfo.getAgainstMap();
		AFFightList.Builder fightAgainstBuilder = AFFightList.newBuilder();
		int endIndex = FightGroup.GROUP_2_VALUE;
		if (!isLast) {
			FightGroup curFightGroup = AllianceBattleManager.getInstance().getCurFightGroup();
			if (curFightGroup == null) {
				endIndex = FightGroup.GROUP_2_VALUE;
			} else {
				endIndex = curFightGroup.getNumber();
			}
		}
		for (int i = FightGroup.GROUP_32_VALUE; i <= endIndex; i++) {
			List<AllianceFightVersus> fightUnits = allianceAgainstMap.get(i);
			if (fightUnits != null && fightUnits.size() > 0) {
				for (AllianceFightVersus fightUnit : fightUnits) {
					AFUnit.Builder fightUnitBuilder = AFUnit.newBuilder();
					fightUnitBuilder.setId(fightUnit.getId());
					fightUnitBuilder.setLeftId(fightUnit.getLeftId());
					fightUnitBuilder.setRightId(fightUnit.getRightId());
					AllianceBattleItem left = againstInfo.getBattleItem(fightUnit.getLeftId());
					if (left != null) {
						fightUnitBuilder.setLeftName(left.getAllianceName());
						fightUnitBuilder.setLeftBuffId(left.getBuffId());
					} else {
						fightUnitBuilder.setLeftName("未知");
						fightUnitBuilder.setLeftBuffId(0);
					}

					AllianceBattleItem right = againstInfo.getBattleItem(fightUnit.getRightId());
					if (right != null) {
						fightUnitBuilder.setRightName(right.getAllianceName());
						fightUnitBuilder.setRightBuffId(right.getBuffId());
					} else {
						fightUnitBuilder.setRightName("未知");
						fightUnitBuilder.setRightBuffId(0);
					}

					if (fightUnit.getWinId() > 0) {
						fightUnitBuilder.setWinId(fightUnit.getWinId());
					}

					int investAllianceId = fightUnit.getInvestAllianceId(player.getId());
					if (investAllianceId > 0) {
						fightUnitBuilder.setInvestedId(investAllianceId);
					}
					AFUnitState state = fightUnit.getFightState();
					fightUnitBuilder.setState(state);
					if (i == FightGroup.GROUP_32_VALUE) {
						fightAgainstBuilder.addRound3216(fightUnitBuilder);
					} else if (i == FightGroup.GROUP_16_VALUE) {
						fightAgainstBuilder.addRound168(fightUnitBuilder);
					} else if (i == FightGroup.GROUP_8_VALUE) {
						fightAgainstBuilder.addRound84(fightUnitBuilder);
					} else if (i == FightGroup.GROUP_4_VALUE) {
						fightAgainstBuilder.addRound42(fightUnitBuilder);
					} else if (i == FightGroup.GROUP_2_VALUE) {
						fightAgainstBuilder.addRound21(fightUnitBuilder);
					}

				}
			}
		}

		if (!isLast) {
			AllianceBattleManager battleManager = AllianceBattleManager.getInstance();
			if (battleManager.getCurBattleState() != AllianceBattleState.PREPARE
					&& battleManager.getCurBattleState() != AllianceBattleState.SHOW_TIME) {
				// 返回对阵目标
				FightGroup fightGroup = AllianceBattleManager.GROUP_REGISTER_MAP.get(battleManager.getCurBattleState());
				AllianceFightVersus versus = battleManager.getAllianceFightVersus(fightGroup.getNumber(), allianceId,
						isLast);
				if (versus != null) {
					int targetAllianceId = versus.getLeftId() == allianceId ? versus.getRightId() : versus.getLeftId();
					AllianceBattleItem battleItem = battleManager.getBattleItem(targetAllianceId);
					fightAgainstBuilder.setEstimateAllianceItemInfo(genAllianceBattleItem(battleItem));
				} else {
					// 表示这个帮会已经输了 找出 谁把他干掉的
					versus = battleManager.getAllianceFailFightVersus(allianceId, isLast);
					if (versus != null) {
						fightAgainstBuilder.setFailureGroup(FightGroup.valueOf(versus.getFightGroup()));
						int targetAllianceId = versus.getLeftId() == allianceId ? versus.getRightId()
								: versus.getLeftId();
						AllianceBattleItem battleItem = battleManager.getBattleItem(targetAllianceId);
						fightAgainstBuilder.setEstimateAllianceItemInfo(genAllianceBattleItem(battleItem));
					}
				}
			}
		}

		return fightAgainstBuilder;
	}

	public static HPAllianceTeamFightRet.Builder genAllianceFightVersus(int allianceId, AllianceFightVersus versus) {
		HPAllianceTeamFightRet.Builder builder = HPAllianceTeamFightRet.newBuilder();
		builder.setFightGroup(FightGroup.valueOf(versus.getFightGroup()));
		if (versus.getLeftId() > 0) {
			builder.setLeftName(AllianceBattleManager.getInstance().getAllianceName(versus.getLeftId()));
		}

		if (versus.getRightId() > 0) {
			builder.setRightName(AllianceBattleManager.getInstance().getAllianceName(versus.getRightId()));
		}
		AllianceBattleManager battleManager = AllianceBattleManager.getInstance();
		AllianceBattleTimeCfg timeCfg = AllianceBattleTimeCfg.getCfg(battleManager.getCurBattleState().getNumber());
		long nowTime = GuaJiTime.getMillisecond();
		long startTime = timeCfg.getStartSpecifiedDate(battleManager.getCurStageId()).getTime();

		int index = 1;
		for (AllianceFightUnit fightUnit : versus.getFightUnits()) {
			AFDetailUnit.Builder unitBuilder = AFDetailUnit.newBuilder();
			unitBuilder.setId(fightUnit.getId());
			unitBuilder.setLeftTeamIndex(fightUnit.getLeftIndex());
			unitBuilder.setRightTeamIndex(fightUnit.getRightIndex() - 3);
			int spaceTime = SysBasicCfg.getInstance().getAllianceBattleTime() * 1000;
			if (nowTime >= startTime) {
				unitBuilder.setLeftTime((int) (index * spaceTime + startTime - nowTime) / 1000);
			}

			if (fightUnit.getWinIndex() > 0) {
				unitBuilder.setWinId(fightUnit.getWinIndex());
			}

			builder.addDetailUnit(unitBuilder);

			index++;
		}

		if (versus.getLeftId() == allianceId || allianceId == versus.getRightId()) {
			builder.setIsSelfCurBattle(true);
		} else {
			builder.setIsSelfCurBattle(false);
		}

		return builder;
	}

	/**
	 * 构造世界boss个人伤害排行结构体
	 * 
	 * @param worldBossInfo
	 * @return
	 */
	public static HPBossHarmRank.Builder genBossHarmRank(WorldBossInfo worldBossInfo) {

		HPBossHarmRank.Builder builder = HPBossHarmRank.newBuilder();

		// 获取前10名玩家信息
		List<PlayerWorldBossEntity> playerWorldBossEntities = worldBossInfo
				.getPlayerRankTop(WorldBossCfg.getInstance().getWorldBossRankCount());
		int index = 0;
		for (PlayerWorldBossEntity pwb : playerWorldBossEntities) {
			BossRankItem.Builder bossRankItemBuilder = BossRankItem.newBuilder();
			int playerId = pwb.getPlayerId();
			if (playerId > 0) {
				PlayerSnapshotInfo.Builder snapBuilder = SnapShotManager.getInstance().getPlayerSnapShot(playerId)
						.clone();
				if (snapBuilder != null) {
					int rank = ++index;
					bossRankItemBuilder.setPlayerName(snapBuilder.getMainRoleInfo().getName());
					bossRankItemBuilder.setHarm(pwb.getHarm());
					bossRankItemBuilder.setRankIndex(rank);
					bossRankItemBuilder.setType(Const.WorldBossRankType.BOSS_PERSON_RANK_TYPE_VALUE);
					bossRankItemBuilder.setRewardInfo(WorldBossAwardsCfg
							.getPlayerRankAwards(worldBossInfo.getWorldBossEntity().getAwardsId(), rank));
					bossRankItemBuilder.setAttacksTimes(pwb.getAttackTimes());
					builder.addBossRankItem(bossRankItemBuilder);
				}
			}
		}

		// 最后一击玩家
		int killPlayerId = worldBossInfo.getWorldBossEntity().getLastKillPlayerId();
		if (killPlayerId > 0) {
			PlayerWorldBossEntity pwb = worldBossInfo.getPlayerWorldBoss(killPlayerId);
			if (pwb != null) {
				PlayerSnapshotInfo.Builder snapBuilder = SnapShotManager.getInstance().getPlayerSnapShot(killPlayerId)
						.clone();
				if (snapBuilder != null) {
					BossRankItem.Builder bossRankItemBuilder = BossRankItem.newBuilder();
					bossRankItemBuilder.setPlayerName(snapBuilder.getMainRoleInfo().getName());
					bossRankItemBuilder.setHarm(pwb.getHarm());
					bossRankItemBuilder.setRankIndex(0);
					bossRankItemBuilder.setType(Const.WorldBossRankType.BOSS_LAST_KILL_RANT_TYPE_VALUE);
					bossRankItemBuilder.setRewardInfo(
							WorldBossAwardsCfg.getKillAwards(worldBossInfo.getWorldBossEntity().getAwardsId()));
					bossRankItemBuilder.setAttacksTimes(pwb.getAttackTimes());
					if (snapBuilder.getAllianceInfo() != null) {
						bossRankItemBuilder.setAllianceName(snapBuilder.getAllianceInfo().getAllianceName());
					}

					builder.addBossRankItem(bossRankItemBuilder);
				}
			}
		}
		builder.setCurrBossHp(worldBossInfo.getWorldBossEntity().getCurrBossHp());
		return builder;
	}

	/**
	 * 构造世界boss联盟伤害排行结构体
	 * 
	 * @param worldBossInfo
	 * @return
	 */
//	public static HPBossHarmRank.Builder genBossHarmAllianceRank(WorldBossInfo worldBossInfo,
//			HPBossHarmRank.Builder builder) {
//
//		// 获取前10名联盟信息
//		List<Tuple2<Integer, Long>> allianceWorldBossEntities = worldBossInfo
//				.getAllianceRankTop(WorldBossCfg.getInstance().getWorldBossRankCount());
//		int alliance_index = 0;
//		for (Tuple2<Integer, Long> allianceTup : allianceWorldBossEntities) {
//			BossRankItem.Builder bossRankItemBuilder = BossRankItem.newBuilder();
//			if (allianceTup.first > 0) {
//				AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceTup.first);
//				if (allianceEntity != null) {
//					int rank = ++alliance_index;
//					bossRankItemBuilder.setPlayerName(allianceEntity.getName());
//					bossRankItemBuilder.setHarm(allianceTup.second);
//					bossRankItemBuilder.setRankIndex(rank);
//					bossRankItemBuilder.setType(Const.WorldBossRankType.BOSS_ALLIANCE_RANK_TYPE_VALUE);
//					bossRankItemBuilder.setRewardInfo(WorldBossAllianceAwardsCfg
//							.getAllianceRankAwards(worldBossInfo.getWorldBossEntity().getAwardsId(), rank));
//					bossRankItemBuilder.setAttacksTimes(0);
//					bossRankItemBuilder.setAllianceName(allianceEntity.getName());
//					builder.addBossRankAllianceItem(bossRankItemBuilder);
//				}
//			}
//		}
//		return builder;
//	}

	/**
	 * 构建boss结算面板信息
	 * 
	 * @param bossInfoBuilder
	 * @param bossInfo
	 * @param player
	 */
	public static void getBossRankPanelInfo(HPWorldBossInfo.Builder bossInfoBuilder, WorldBossInfo bossInfo,
			Player player) {
		if (bossInfo == null) {
			return;
		}
		// 个人伤害排行
		HPBossHarmRank.Builder harmRankBuilder = BuilderUtil.genBossHarmRank(bossInfo);
		PlayerWorldBossEntity playerWorldBossEntity = bossInfo.getPlayerWorldBoss(player.getId());
		if (playerWorldBossEntity != null) {
			harmRankBuilder.setSelfHarm(playerWorldBossEntity.getHarm());
			harmRankBuilder.setSelfAttacksTimes(playerWorldBossEntity.getAttackTimes());
		} else {
			harmRankBuilder.setSelfHarm(0);
			harmRankBuilder.setSelfAttacksTimes(0);
		}
		// 联盟伤害排行
		// BuilderUtil.genBossHarmAllianceRank(bossInfo, harmRankBuilder);
		bossInfoBuilder.setLastBossInfo(harmRankBuilder);
		bossInfoBuilder.setBossInfo(BuilderUtil.genWorldBossInfo(bossInfo.getWorldBossEntity()));

		// 个人及个人联盟排名信息
		int playerRank = bossInfo.getCurRankInfo(player.getId());
		if (playerRank != 0) {
			BossRankItem.Builder playerRankBuilder = BossRankItem.newBuilder();
			playerRankBuilder.setPlayerName(player.getName());
			playerRankBuilder.setHarm(playerWorldBossEntity != null ? playerWorldBossEntity.getHarm() : 0);
			playerRankBuilder.setRewardInfo(
					WorldBossAwardsCfg.getPlayerRankAwards(bossInfo.getWorldBossEntity().getAwardsId(), playerRank));
			playerRankBuilder.setType(Const.WorldBossRankType.BOSS_PERSON_RANK_TYPE_VALUE);
			playerRankBuilder.setRankIndex(playerRank);
			bossInfoBuilder.setCurRank(playerRankBuilder);
		}
		// 自己联盟排行信息
//		BossRankItem.Builder allianceRankBuilder = BossRankItem.newBuilder();
//		PlayerAllianceEntity allianceEntity = player.getPlayerData().getPlayerAllianceEntity();
//		if (allianceEntity != null && allianceEntity.getAllianceId() > 0) {
//			int allianceRank = bossInfo.getCurAllianceRankInfo(allianceEntity.getAllianceId());
//			if (allianceRank != 0) {
//				allianceRankBuilder.setPlayerName(player.getName());
//				allianceRankBuilder.setHarm(playerWorldBossEntity != null ? playerWorldBossEntity.getHarm() : 0);
//				allianceRankBuilder.setRewardInfo(WorldBossAllianceAwardsCfg
//						.getAllianceRankAwards(bossInfo.getWorldBossEntity().getAwardsId(), allianceRank));
//				allianceRankBuilder.setType(Const.WorldBossRankType.BOSS_ALLIANCE_RANK_TYPE_VALUE);
//				allianceRankBuilder.setRankIndex(allianceRank);
//
//				if (allianceEntity != null) {
//					bossInfoBuilder.setCurAllianceRank(allianceRankBuilder);
//				}
//			}
//		}

	}

	/**
	 * 组装世界bossBuff信息
	 * 
	 * @param stateEntity
	 * @param playerWorldBossEntity
	 */
//	public static HPBossRandomBuffRes.Builder getWorldBossBuffInfo(StateEntity stateEntity,
//			PlayerWorldBossEntity playerWorldBossEntity) {
//		HPBossRandomBuffRes.Builder builder = HPBossRandomBuffRes.newBuilder();
//		builder.setBuffRanTimes(stateEntity.getWorldBossBuffFreeTimes());
//		builder.setBuffCfgId(0);
//		builder.setBuffFreeTimes(
//				WorldBossCfg.getInstance().getWorldBossFreeTimes() - stateEntity.getWorldBossBuffFreeTimes());
//		builder.setNextPrice(
//				WorldBossCfg.getInstance().getWorldBossBuffConstGold(stateEntity.getWorldBossBuffFreeTimes() + 1));
//		return builder;
//	}

	/**
	 * 组装世界boss信息
	 * 
	 * @param worldBossEntgity
	 * @return
	 */
	public static com.guaji.game.protocol.WorldBoss.WorldBossInfo.Builder genWorldBossInfo(
			WorldBossEntity worldBossEntgity) {
		com.guaji.game.protocol.WorldBoss.WorldBossInfo.Builder builder = com.guaji.game.protocol.WorldBoss.WorldBossInfo
				.newBuilder();
		if (worldBossEntgity != null) {
			builder.setHp(worldBossEntgity.getCurrBossHp());
			builder.setMaxHp(worldBossEntgity.getMaxBossHp());
			builder.setLevel(0);
			builder.setName("");
			// builder.setRoleItemId(WorldBossCfg.getInstance().getWorldBossShowItemId());
			// if (worldBossEntgity.getBossNpcId() > 0) {
			builder.setNpcId(worldBossEntgity.getBossNpcId());
			// } else if (worldBossEntgity.getArenaNpcId() > 0) {
			// builder.setMonsterId(worldBossEntgity.getArenaNpcId());
			// }
		}
		return builder;
	}

	public static HPCommendationTribe.Builder genCommendationTribe(CommendationTribeStatus commendationTribeStatus) {
		HPCommendationTribe.Builder commBuilder = HPCommendationTribe.newBuilder();
		commBuilder.setCurStage(commendationTribeStatus.getCurStage());
		commBuilder.setCurLuckyValue(commendationTribeStatus.getLuckyValue());
		commBuilder.setLeftCount(commendationTribeStatus.getLeftCount());
		commBuilder.setCostGold(commendationTribeStatus.getCurCostGold());
		return commBuilder;
	}

	public static HPFortuneInfo.Builder genFortuneInfo(FortuneStatus fortuneStatus) {
		if (fortuneStatus == null) {
			return null;
		}
		HPFortuneInfo.Builder fortuneInfoBuilder = HPFortuneInfo.newBuilder();
		int curRecharge = fortuneStatus.getCurDayRechargeValue();
		FortuneCfg fortuneCfg = fortuneStatus.getCurActiveFortuneCfg();
		fortuneInfoBuilder.setRechargeValue(curRecharge);
		if (fortuneCfg == null) {
			fortuneInfoBuilder.setCurGiftValue(0);
			fortuneInfoBuilder.setLeftRechargeValue(-1);
		} else {
			fortuneInfoBuilder.setCurGiftValue(fortuneCfg.getNeedRechargeGold());
			fortuneInfoBuilder.setLeftRechargeValue(fortuneStatus.getLeftRechargeValue());
		}
		return fortuneInfoBuilder;
	}

	/**
	 * 英雄令商品售卖信息
	 * 
	 * @param item
	 * @param price
	 * @param buyTimes
	 * @return
	 */
	public static ShopStatusBean.Builder createShopBuilder(AwardItems.Item item, int price, int buyTimes) {

		ShopStatusBean.Builder builder = ShopStatusBean.newBuilder();
		builder.setItemId(item.getId());
		builder.setItemType(item.getType());
		builder.setItemCount((int) item.getCount());
		builder.setBuyPrice(price);
		builder.setBuyTimes(buyTimes);

		return builder;
	}

	/**
	 * 快照转换
	 * 
	 * @param snapshotBytes
	 * @return
	 */
	public static PlayerSnapshotInfo.Builder convertSnapshot(byte[] snapshotBytes) {
		try {
			PlayerSnapshotInfo snapshot = PlayerSnapshotInfo.parseFrom(snapshotBytes);
			PlayerSnapshotInfo.Builder builder = snapshot.toBuilder();
			if (!snapshot.getPlayerInfo().hasHonorValue()) {
				builder.getPlayerInfoBuilder().setHonorValue(0);
			}
			if (!snapshot.getPlayerInfo().hasReputationValue()) {
				builder.getPlayerInfoBuilder().setReputationValue(0);
			}
			if (!snapshot.getMainRoleInfo().hasStarLevel()) {
				builder.getMainRoleInfoBuilder().setStarLevel(0);
			}
			if (!snapshot.getMainRoleInfo().hasStarExp()) {
				builder.getMainRoleInfoBuilder().setStarExp(0);
			}
			if (snapshot.hasAllianceInfo() && !snapshot.getAllianceInfo().hasTotalVitality()) {
				builder.getAllianceInfoBuilder().setTotalVitality(0);
			}
			return builder;
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return null;
	}

	/**
	 * @param 战斗结构信息
	 * @return 返回最近战斗结果信息
	 */
	public static BattleInfo.Builder convertSnapBattleInfo(byte[] snapshotBytes) {
		try {
			BattleInfo snapshot = BattleInfo.parseFrom(snapshotBytes);
			;
			BattleInfo.Builder builder = snapshot.toBuilder();
			return builder;
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return null;

	}

	/**
	 * 生成魔王宝藏信息
	 * 
	 * @param entity
	 * @param luck      是否触发幸运奖励
	 * @param timeCfg
	 * @param luckAward 幸运奖励信息
	 * @return
	 */
	public static HPPrinceDevilsPanelInfoRes.Builder princeDevilsInfoBuilder(PlayerPrinceDevilsEntity entity,
			boolean luck, ActivityTimeCfg timeCfg, String luckAward) {

		HPPrinceDevilsPanelInfoRes.Builder builder = HPPrinceDevilsPanelInfoRes.newBuilder();
		builder.setActivityLeftTime(timeCfg.calcActivitySurplusTime());

		PrinceDevilsCostCfg cfg = ConfigManager.getInstance().getConfigByIndex(PrinceDevilsCostCfg.class, 0);

		int currentTime = GuaJiTime.getSeconds();
		builder.setFreeTime(currentTime - entity.getFreeTime() > cfg.getRefreshTime() ? 0
				: entity.getFreeTime() + cfg.getRefreshTime() - currentTime);
		builder.setLuck(luck);
		builder.setScore(entity.getScore());
		Iterator<Entry<Integer, String>> it = entity.getRewardInfoMap().entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, String> e = it.next();
			PrinceDevilsIndexInfo.Builder b = PrinceDevilsIndexInfo.newBuilder();
			b.setIndex(e.getKey());
			b.setAward(e.getValue());
			builder.addDevilsIndexInfo(b);
		}
		if (luck) {
			builder.setLuckAward(luckAward);
		}
		int constIndex = entity.getRewardInfoMap().size() == 0 ? 1 : entity.getRewardInfoMap().size();
		int consumeGold = PrinceDevilsCostCfg.getPrinceDevilsGoldMap(constIndex);
		builder.setConsumeGold(consumeGold);
		return builder;
	}

	/**
	 * 生成怪物对应的角色信息
	 * 
	 * @param monsterId
	 * @return
	 */
	public static RoleInfo.Builder genMonsterRoleInfoBuilder(int monsterId, boolean isBoss) {
		NewMonsterCfg monsterCfg = ConfigManager.getInstance().getConfigByKey(NewMonsterCfg.class, monsterId);
		if (monsterCfg != null) {
			RoleInfo.Builder builder = RoleInfo.newBuilder();
			builder.setRoleId(monsterId);
			if (isBoss) {
				builder.setType(Const.roleType.WORLDBOSS_VALUE);
			} else {
				builder.setType(Const.roleType.MONSTER_VALUE);
			}
			builder.setItemId(monsterId);
			builder.setProf(monsterCfg.getProfession());
			builder.setName("");
			builder.setLevel(monsterCfg.getLevel());
			builder.setExp(0);
			builder.setFight(monsterCfg.getFight());
			builder.setStatus(0);
			builder.setIsMagic(monsterCfg.getIsMagic());
			builder.setElements(monsterCfg.getElement());
			builder.setATKSpeed(monsterCfg.getATKSpeed());
			builder.setNATK(monsterCfg.getNATK());

			builder.setATKMP(monsterCfg.getATKMP());

			builder.setDEFMP(monsterCfg.getDEFMP());

			builder.setSkillMp(monsterCfg.getSKMP());

			builder.setClassCorrection(monsterCfg.getClassCorrection());

			List<Integer> skillIds = monsterCfg.getSkillList();
			for (Integer skillId : skillIds) {
				if (skillId > 0) {
					RoleSkill.Builder skillBuilder = RoleSkill.newBuilder();
					skillBuilder.setSkillId(0);
					skillBuilder.setItemId(skillId);
					builder.addSkills(skillBuilder);
				}
			}

			List<Integer> passiveIds = monsterCfg.getPassiveList();
			for (Integer skillId : passiveIds) {
				if (skillId > 0) {
					RoleSkill.Builder PassiveBuilder = RoleSkill.newBuilder();
					PassiveBuilder.setSkillId(0);
					PassiveBuilder.setItemId(skillId);
					builder.addSkills2(PassiveBuilder);
				}
			}

			builder.setAttribute(genAttributeBuilder(monsterCfg.getAttribute()));

			return builder;
		}
		return null;
	}
	
	/**
	 * 生成塔樓怪物的角色信息
	 * 
	 * @param monsterId
	 * @return
	 */
	public static RoleInfo.Builder TowerMonsterRoleInfoBuilder(int monsterId,int level,float ratio) {
		TowerMonsterCfg monsterCfg = ConfigManager.getInstance().getConfigByKey(TowerMonsterCfg.class, monsterId);
		if (monsterCfg != null) {
			RoleInfo.Builder builder = RoleInfo.newBuilder();
			builder.setRoleId(monsterId);

			builder.setType(Const.roleType.MONSTER_VALUE);
			
			builder.setItemId(monsterId);
			builder.setProf(monsterCfg.getProfession());
			builder.setName("");
			builder.setLevel(level);
			builder.setExp(0);
			builder.setFight(monsterCfg.getFight(level,ratio));
			builder.setStatus(0);
			builder.setIsMagic(monsterCfg.getIsMagic());
			builder.setElements(monsterCfg.getElement());
			builder.setATKSpeed(monsterCfg.getATKSpeed());
			builder.setNATK(monsterCfg.getNATK());

			builder.setATKMP(monsterCfg.getATKMP());

			builder.setDEFMP(monsterCfg.getDEFMP());

			builder.setSkillMp(monsterCfg.getSKMP());

			builder.setClassCorrection(monsterCfg.getClassCorrection());

			List<Integer> skillIds = monsterCfg.getSkillList();
			for (Integer skillId : skillIds) {
				if (skillId > 0) {
					RoleSkill.Builder skillBuilder = RoleSkill.newBuilder();
					skillBuilder.setSkillId(0);
					skillBuilder.setItemId(skillId);
					builder.addSkills(skillBuilder);
				}
			}

			List<Integer> passiveIds = monsterCfg.getPassiveList();
			for (Integer skillId : passiveIds) {
				if (skillId > 0) {
					RoleSkill.Builder PassiveBuilder = RoleSkill.newBuilder();
					PassiveBuilder.setSkillId(0);
					PassiveBuilder.setItemId(skillId);
					builder.addSkills2(PassiveBuilder);
				}
			}

			builder.setAttribute(genAttributeBuilder(monsterCfg.getAttribute(level,ratio)));

			return builder;
		}
		return null;
	}

	/**
	 * 生成Monster檢查对应的角色信息
	 * 
	 * @param monsterId
	 * @return
	 */
//	public static CLIcheckInfo.Builder getMonseterCKInfoBuilder(int Id) {
//		CLIcheckInfo.Builder builder = CLIcheckInfo.newBuilder();
//		NewMonsterCfg monsterCfg = ConfigManager.getInstance().getConfigByKey(NewMonsterCfg.class,Id);
//		if (monsterCfg != null) {
//			builder.setRoleId(Id);
//			builder.setType(Const.roleType.MONSTER_VALUE);
//			builder.setProf(monsterCfg.getProfession());
//			builder.addAllSkills(monsterCfg.getSkillList());
//			builder.setAttribute(genAttributeBuilder(monsterCfg.getAttribute()));
//			builder.setElements(monsterCfg.getElement());
//			return builder;
//		}
//		return null;
//	}

	public static CLIcheckInfo.Builder getRoleInfoCKInfoBuilder(RoleInfo.Builder roleInfo, int initHP) {
		if (roleInfo == null) {
			return null;
		}
		CLIcheckInfo.Builder builder = CLIcheckInfo.newBuilder();
		builder.setRoleId(roleInfo.getRoleId());
		builder.setItemId(roleInfo.getItemId());
		builder.setType(roleInfo.getType());
		builder.setProf(roleInfo.getProf());
		builder.setLv(roleInfo.getLevel());
		builder.setAttribute(roleInfo.getAttribute());

		for (RoleSkill aSkill : roleInfo.getSkillsList()) { // 主
			if (aSkill.getItemId() > 0) {
				builder.addSkills(aSkill.getItemId());
			}
		}

		for (RoleSkill aSkill : roleInfo.getSkills2List()) { // 被
			if (aSkill.getItemId() > 0) {
				builder.addSkills(aSkill.getItemId());
			}
		}
		int value = (roleInfo.getIsMagic() == 1) ? 0 : 1;
		builder.setIsPhy(value);
		builder.setElements(roleInfo.getElements());
		builder.setAtkSpeed(roleInfo.getATKSpeed());
		builder.setAtkMp(roleInfo.getATKMP());
		builder.setDefMp(roleInfo.getDEFMP());
		builder.setClassCorrection(roleInfo.getClassCorrection());
		builder.setSkinId(roleInfo.getSkinId());
		builder.setSkillMp(roleInfo.getSkillMp());
		if (initHP > 0) {
			builder.setInitHp(initHP);
		}

		return builder;
	}

	/**
	 * 构建积分面板信息
	 * 
	 * @param entity
	 * @return
	 */
	public static HPPrinceDevilsScoreExchangeRes.Builder princeDevilsScoreBuilder(PlayerPrinceDevilsEntity entity,
			HPPrinceDevilsScoreExchangeRes.Builder ret, Map<Object, PrinceDevilsExchangeCfg> cfgMap) {

		for (PrinceDevilsExchangeCfg cfg : cfgMap.values()) {
			PrinceDevilsGoodsInfo.Builder infoBuilder = PrinceDevilsGoodsInfo.newBuilder();
			// 已兑换次数
			int exchangeCount = entity.getScoreExchangeCount(cfg.getId());
			infoBuilder.setExchangeCount(exchangeCount);
			infoBuilder.setGoodsId(cfg.getExchangeItems());
			infoBuilder.setId(cfg.getId());
			infoBuilder.setSumCount(cfg.getLimitTimes());
			infoBuilder.setSingleCostScore(cfg.getCostCredits());
			ret.addGoodsInfos(infoBuilder);
		}
		ret.setSurplusScore(entity.getScore());
		return ret;
	}

	/**
	 * 多人副本单个好友builder构造
	 * 
	 * @param playerId
	 * @return
	 */
	public static MulitiFriendItem genMuliteFriendItem(Player player) {
		MulitiFriendItem.Builder friendItemBuilder = MulitiFriendItem.newBuilder();
		friendItemBuilder.setPlayerId(player.getId());
		friendItemBuilder.setLevel(player.getLevel());
		friendItemBuilder.setName(player.getName());
		friendItemBuilder.setFightValue(player.getFightValue());
		friendItemBuilder.setRoleId(player.getPlayerData().getMainRole().getItemId());
		friendItemBuilder.setRebirthStage(player.getRebirthStage());
		friendItemBuilder.setSignature(player.getEntity().getSignature());
		friendItemBuilder.setTotalCount(SysBasicCfg.getInstance().getMultiEliteDayFreeTimes());
		friendItemBuilder.setHeadIcon(player.getPlayerData().getPlayerEntity().getHeadIcon());
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		if (stateEntity != null) {
			friendItemBuilder.setResidueCount(stateEntity.getMultiEliteTimes());
		}

		MultiEliteRoomInfo roomInfo = MultiEliteManager.getInstance().getCurInRoom(player.getId());
		if (roomInfo == null) {
			friendItemBuilder.setIsTeam(false);
		} else {
			friendItemBuilder.setIsTeam(true);
		}
		return friendItemBuilder.build();
	}

	/**
	 * 多人副本好友builder构造
	 * 
	 * @param playerIds
	 * @return
	 */
	public static List<MulitiFriendItem> genMultiFriendItemBuilders(Set<Integer> playerIds, int level) {
		List<MulitiFriendItem> friends = new ArrayList<MulitiFriendItem>();
		for (int playerId : playerIds) {
			Player player = PlayerUtil.queryPlayer(playerId);
			if (player == null || !player.isOnline() || player.getLevel() < level) {
				continue;
			}

			MulitiFriendItem friend = genMuliteFriendItem(player);
			if (friend != null) {
				friends.add(friend);
			}
		}
		return friends;
	}

	/**
	 * 组装王的后宫积分面板协议
	 * 
	 * @param activityEntity
	 * @param exchangeCfg
	 */
	public static HPHaremScorePanelRes.Builder getHaremExchangeBuilders(HaremActivityEntity entity,
			Map<Object, HaremExchangeCfg> cfgMap, int times) {
		// 组装协议内容
		HPHaremScorePanelRes.Builder response = HPHaremScorePanelRes.newBuilder();
		// 所有的配置
		for (HaremExchangeCfg cfg : cfgMap.values()) {
			Map<Integer, Integer> mapEntity = entity.getExchangeMap();
			int alreadyTimes = 0;
			if (mapEntity.size() != 0) {
				if (mapEntity.get(cfg.getId()) != null) {
					alreadyTimes = mapEntity.get(cfg.getId());
				}
			}
			HPHaremScoreInfo.Builder scoreBuilder = HPHaremScoreInfo.newBuilder();
			scoreBuilder.setId(cfg.getId());
			// 已兑换次数
			scoreBuilder.setExchangeTimes(alreadyTimes);
			// 总次数
			scoreBuilder.setLimitTimes(cfg.getLimitTimes());
			// 兑换的道具
			scoreBuilder.setExchangeItems(cfg.getExchangeItems());
			if (cfg.getCostCredits() != 0) {
				scoreBuilder.setCostCredits(cfg.getCostCredits());
			} else {
				scoreBuilder.setCostItems(cfg.getCostItems());
			}
			response.addHaremScoreInfo(scoreBuilder);
		}
		response.setScore(entity.getScore());
		return response;
	}

	/**
	 * 少女的邂逅交换返回
	 */
	public static SyncMaidenEncounterExchangeRes.Builder getMaidenEncounterExchangeBuilders(
			MaidenEncounterEntity entity) {
		MaidenEncounterExchangeInfo.Builder exchangeInfo = MaidenEncounterExchangeInfo.newBuilder();
		List<MaidenEncounterExchangeInfo> builderList = new ArrayList<MaidenEncounterExchangeInfo>();
		Map<Integer, Integer> exchangeMap = entity.getExchangeMap();
		for (Integer id : exchangeMap.keySet()) {
			exchangeInfo.setId(id);
			int remainderTimes = exchangeMap.get(id);
			exchangeInfo.setRemainderTimes(remainderTimes);
			builderList.add(exchangeInfo.build());
		}
		SyncMaidenEncounterExchangeRes.Builder response = SyncMaidenEncounterExchangeRes.newBuilder();
		response.addAllInfo(builderList);
		return response;
	}

	/**
	 * 18路诸侯药箱数量返回
	 */
	public static HPSyncMedicalKitInfoRet.Builder genSyncMedicalKitInfoRetBuilders(EighteenPrincesEntity entity) {

		if (entity == null) {
			return null;
		}

		HPSyncMedicalKitInfoRet.Builder builder = HPSyncMedicalKitInfoRet.newBuilder();
		MedicalKitItem.Builder bigMedical = MedicalKitItem.newBuilder();
		bigMedical.setType(Const.playerAttr.BIG_MEDICALKIT_VALUE);
		bigMedical.setCount(entity.getBigMedicalKit());
		builder.addItem(bigMedical);

		MedicalKitItem.Builder midleMedical = MedicalKitItem.newBuilder();
		midleMedical.setType(Const.playerAttr.MIDLE_MEDICALKIT_VALUE);
		midleMedical.setCount(entity.getMidleMedicalKit());
		builder.addItem(midleMedical);

		MedicalKitItem.Builder smallMedical = MedicalKitItem.newBuilder();
		smallMedical.setType(Const.playerAttr.SMALL_MEDICALKIT_VALUE);
		smallMedical.setCount(entity.getSmallMedicalKit());
		builder.addItem(smallMedical);

		return builder;
	}

	public static syncSecretMsg.Builder genSercretMsgInfoBuilders(Player player) {
		syncSecretMsg.Builder builder = syncSecretMsg.newBuilder();
		// 問題一開始就random不由server隨機問
//		builder.addAllId(player.getPlayerData().getStateEntity().getSecretMsgList());
		int heroId = 0;
		for (SecretMsgEntity smEntity : player.getPlayerData().getSecretMsgEntities()) {
			secretMsgHeroInfo.Builder infobuilder = secretMsgHeroInfo.newBuilder();
			heroId = smEntity.getItemId();
			
			Map<Integer, Integer> valueMap = PlayerUtil.calcSecretMsgValue(player.getPlayerData(), heroId);
			infobuilder.setHeroId(heroId);
			if (valueMap.size() == 3) {
				infobuilder.setFavorability(valueMap.get(0));
				infobuilder.setIntimacy(valueMap.get(1));
				infobuilder.setSexy(valueMap.get(2));
			} else {
				infobuilder.setIntimacy(0);
				infobuilder.setFavorability(0);
				infobuilder.setSexy(0);
			}

			for (Map.Entry<Integer, Integer> entry : smEntity.getChoiceMsgMap().entrySet()) {
				historyInfo.Builder historybuilder = historyInfo.newBuilder();
				historybuilder.setQution(entry.getKey());
				historybuilder.setAnswer(entry.getValue());

				infobuilder.addHistory(historybuilder);
				if (entry.getValue() == -1) {
					break;
				}
			}
			
			List<Integer> alist = new ArrayList<>(smEntity.getCostCfgId());
			
			infobuilder.addAllCostCfgId(alist);
			
			alist = new ArrayList<>(smEntity.getFreeCfgId());
			
			infobuilder.addAllFreeCfgId(alist);
			
			alist = new ArrayList<>(smEntity.getUnlockCfgId());
			
			infobuilder.addAllUnlockCfgId(alist);
			
			
			infobuilder.setPic(smEntity.getAblumMaxId());

			builder.addHeroInfo(infobuilder);
			
		}
		
		if (player.getPlayerData().getStateEntity()!=null) {
			builder.setPower(player.getPlayerData().getStateEntity().getSecretPower());
		} else {
			builder.setPower(0);
		}
		
		return builder;
	}
	
	public static syncSecretMsg.Builder genSercretMsgPowerBuilders(Player player) {
		syncSecretMsg.Builder builder = syncSecretMsg.newBuilder();
		if (player.getPlayerData().getStateEntity()!=null) {
			builder.setPower(player.getPlayerData().getStateEntity().getSecretPower());
		} else {
			builder.setPower(0);
		}
		return builder;
	}

	public static void sendShopRedPoint(Player player, ShopType shoptype, boolean pointsiw) {
		PushShopRedPoint.Builder response = PushShopRedPoint.newBuilder();
		response.setShopType(shoptype);
		response.setShowRedPoint(pointsiw);
		player.sendProtocol(Protocol.valueOf(HP.code.SHOP_RED_POINT_S_VALUE, response));
	}
	
	/*
	 * 公告title資訊
	 */
	public static BulletinTitleInfo.Builder getBulletinTitleInfo(Player player , List<BulletinEntity> bulletinEnties) {
		long currTime = GuaJiTime.getMillisecond();
		BulletinTitleInfo.Builder builder = BulletinTitleInfo.newBuilder();
		for (BulletinEntity bulletinEntity : bulletinEnties) {
			if ((bulletinEntity.getPlatformId() != 0)&&( bulletinEntity.getPlatformId()!=player.getPlatformId())) {
				continue;
			}
			if ((currTime >=bulletinEntity.getBeginTime().getTime())&&(currTime <= bulletinEntity.getEndTime().getTime())){
				BulletTitleItem.Builder itembuilder = BulletTitleItem.newBuilder();
				itembuilder.setId(bulletinEntity.getId());
				itembuilder.setKind(bulletinEntity.getType());
				itembuilder.setTitleStr(bulletinEntity.getTitle());
				itembuilder.setUpdateTime(bulletinEntity.getUpdateTime().getTime());
				itembuilder.setSort(bulletinEntity.getSort());
				itembuilder.setShow(bulletinEntity.getVisible() == 1);
				builder.addAllInfo(itembuilder);
			}
		};
		return builder;
	}
	
	/**
	 * 公告內容
	 */
	public static BulletinContentRsp.Builder getBulletinContent(BulletinEntity bulletinEntity,int errorCode){
		BulletinContentRsp.Builder builder = BulletinContentRsp.newBuilder();
		if (errorCode == 0) {
			builder.setId(bulletinEntity.getId());
			builder.setTxturl(bulletinEntity.getTxturl());
		}
		builder.setErrorCode(errorCode);
			
		return builder;
	}
}
