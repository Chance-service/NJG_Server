package com.guaji.game.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.os.GuaJiRand;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;

import com.guaji.game.attribute.Attribute;
import com.guaji.game.battle.BattleUtil;
import com.guaji.game.config.BPCfg;
import com.guaji.game.config.ElementCfg;
import com.guaji.game.config.EquipCfg;
import com.guaji.game.config.EquipStrengthCfg;
import com.guaji.game.config.GodlyAttrCfg;
import com.guaji.game.config.GodlyLevelExpCfg;
import com.guaji.game.config.ItemCfg;
import com.guaji.game.config.RoleEquipCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.config.EquipCfg.AttrValue;
import com.guaji.game.entity.EquipEntity;
import com.guaji.game.entity.MutualEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.attr;

/**
 * 装备帮助类
 */
public class EquipUtil {

	/**
	 * 计算装备评分
	 * 
	 * @return
	 */

	public static int calcEquipScore(EquipEntity equipEntity) {
		int arrttype = 0;
		BPCfg bpCfg = null;
		int equipScore = 0;
		Map<Integer,Integer> primaryMap = equipEntity.getPrimaryAttrMap();
		Map<Integer,Integer> secMap = equipEntity.getSecAttrMap();
		
		for (Map.Entry<Integer, Integer> entry : primaryMap.entrySet()) {
			arrttype = entry.getKey();
			bpCfg = ConfigManager.getInstance().getConfigByKey(BPCfg.class, arrttype);
			if (bpCfg != null) {
				equipScore = equipScore + ((int)BattleUtil.calRoundValue(entry.getValue()*bpCfg.getValuse(),0));
			}
		}
		
		for (Map.Entry<Integer, Integer> entry : secMap.entrySet()) {
			arrttype = entry.getKey();
			bpCfg = ConfigManager.getInstance().getConfigByKey(BPCfg.class, arrttype);
			if (bpCfg != null) {
				equipScore = equipScore + ((int)BattleUtil.calRoundValue(entry.getValue()*bpCfg.getValuse(),0));
			}
		}
		return equipScore;
	}

	/**
	 * 获得装备所穿戴的角色
	 */
	public static RoleEntity getEquipDressRole(Player player, EquipEntity equipEntity) {
		List<RoleEntity> roleEntities = player.getPlayerData().getRoleEntities();
		for (RoleEntity roleEntity : roleEntities) {
			if (roleEntity.checkEquipInDress(equipEntity.getId())) {
				return roleEntity;
			}
		}
		return null;
	}
	
	/**
	 * 获得装备所穿戴的角色
	 */
	public static RoleEntity getEquipDressRole(int playerId, EquipEntity equipEntity) {
		List<RoleEntity> roleEntities  = DBManager.getInstance()
				.query("from RoleEntity where playerId = ? and invalid = 0 order by id asc ",playerId);
		for (RoleEntity roleEntity : roleEntities) {
			if (roleEntity.checkEquipInDress(equipEntity.getId())) {
				return roleEntity;
			}
		}
		return null;
	}


	/**
	 * 生成装备
	 * 
	 * @param equipId
	 * @param isFullAttr
	 * @param punchSize
	 * @return
	 */
	public static EquipEntity generateEquip(Player player, int equipId, int godlyRate) {
		return generateEquip(player, equipId, godlyRate, false, 0, false);
	}

	/**
	 * 生成装备
	 * 
	 * @param equipId
	 * @param isFullAttr
	 * @param punchSize
	 * @return
	 */
	public static EquipEntity generateEquip(Player player, int equipId, int godlyRate, boolean isSecondGodly, int punchSize, boolean isFullAttr) {
		try {
			EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipId);
			if (equipCfg == null) {
				return null;
			}

			EquipEntity equipEntity = new EquipEntity();
			equipEntity.setPlayerId(player.getId());
			equipEntity.setEquipId(equipCfg.getId());
			equipEntity.setGodlyAttrId(0);

			if (punchSize > 0) {
				for (int i = 1; i <= punchSize; i++) {
					equipEntity.setGemId(i, 0);
				}
			} else if (equipCfg.getLevel() >= 10) {
				// 随机孔的状态
				int punchCount = SysBasicCfg.getInstance().randomPunchCount();
				for (int i = 1; i <= punchCount; i++) {
					equipEntity.setGemId(i, 0);
				}
			}
			// 生成主属性 改讀表
//			int primaryAttrCount = 0;
//			List<EquipCfg.AttrValue> primaryAttr = equipCfg.getPrimaryAttr();
//			if (primaryAttr != null && primaryAttr.size() > 0) {
//				for (EquipCfg.AttrValue attrValue : primaryAttr) {
//					if (attrValue.isValid()) {
//						if (primaryAttrCount == 0) {
//							equipEntity.setPrimaryAttrType1(attrValue.getAttrId());
//							if (isFullAttr) {
//								equipEntity.setPrimaryAttrValue1(attrValue.getMaxVal());
//							} else {
//								equipEntity.setPrimaryAttrValue1(attrValue.randomAttrValue());
//							}
//						} else if (primaryAttrCount == 1) {
//							equipEntity.setPrimaryAttrType2(attrValue.getAttrId());
//							if (isFullAttr) {
//								equipEntity.setPrimaryAttrValue2(attrValue.getMaxVal());
//							} else {
//								equipEntity.setPrimaryAttrValue2(attrValue.randomAttrValue());
//							}
//						} else if (primaryAttrCount == 2) {
//							equipEntity.setPrimaryAttrType3(attrValue.getAttrId());
//							if (isFullAttr) {
//								equipEntity.setPrimaryAttrValue3(attrValue.getMaxVal());
//							} else {
//								equipEntity.setPrimaryAttrValue3(attrValue.randomAttrValue());
//							}
//						}
//						primaryAttrCount++;
//					}
//				}
//			}

			// 生成副属性
			if (equipCfg.getMaxAttrPoint() >= equipCfg.getMinAttrPoint()) {
				int attrPoint = 0;
				try {
					attrPoint = GuaJiRand.randInt(equipCfg.getMinAttrPoint(), equipCfg.getMaxAttrPoint());
				} catch (Exception e) {
					MyException.catchException(e);
				}

				List<Integer> attrVals = null;
				List<Integer> attrIds = new LinkedList<Integer>();
				attrIds.add(Const.attr.STRENGHT_VALUE);
				attrIds.add(Const.attr.AGILITY_VALUE);
				attrIds.add(Const.attr.INTELLECT_VALUE);
				attrIds.add(Const.attr.STAMINA_VALUE);
				GuaJiRand.randomOrder(attrIds);

				if (equipCfg.getQuality() == Const.equipQuality.GREEN_VALUE) {
					attrVals = GuaJiRand.randomIncision(attrPoint, 1, 1);
				} else if (equipCfg.getQuality() == Const.equipQuality.BLUE_VALUE) {
					attrVals = GuaJiRand.randomIncision(attrPoint, 2, (int) (attrPoint / 2 * 0.1));
				} else if (equipCfg.getQuality() == Const.equipQuality.PURPLE_VALUE) {
					attrVals = GuaJiRand.randomIncision(attrPoint, 3, (int) (attrPoint / 3 * 0.1));
				} else if (equipCfg.getQuality() >= Const.equipQuality.ORANGE_VALUE) {
					attrVals = GuaJiRand.randomIncision(attrPoint, 4, (int) (0.1 * attrPoint / 4));
				}
				if (attrVals != null && attrVals.size() > 0) {
					int secondaryAttrCount = 0;
					if (attrVals.size() >= 1) {
						equipEntity.setSecondaryAttrType1(attrIds.get(secondaryAttrCount));
						equipEntity.setSecondaryAttrValue1(attrVals.get(secondaryAttrCount));
						secondaryAttrCount++;
					}

					if (attrVals.size() >= 2) {
						equipEntity.setSecondaryAttrType2(attrIds.get(secondaryAttrCount));
						equipEntity.setSecondaryAttrValue2(attrVals.get(secondaryAttrCount));
						secondaryAttrCount++;
					}

					if (attrVals.size() >= 3) {
						equipEntity.setSecondaryAttrType3(attrIds.get(secondaryAttrCount));
						equipEntity.setSecondaryAttrValue3(attrVals.get(secondaryAttrCount));
						secondaryAttrCount++;
					}

					if (attrVals.size() >= 4) {
						equipEntity.setSecondaryAttrType4(attrIds.get(secondaryAttrCount));
						equipEntity.setSecondaryAttrValue4(attrVals.get(secondaryAttrCount));
						secondaryAttrCount++;
					}
				}
			}

			if (equipCfg.getGodlyAttr() > 0) {
				// 必定是神器
				equipEntity.setGodlyAttrId(equipCfg.getGodlyAttr()); 
				equipEntity.setStarExp(0);
				equipEntity.setStarLevel(1);
			} else {
				// 判断是否出神器
				if (equipCfg.getQuality() == 5 && GuaJiRand.randInt(10000) < godlyRate) {
					equipEntity.setGodlyAttrId(SysBasicCfg.getInstance().getGodlyAttrId(equipCfg.getPart()));
					equipEntity.setStarExp(0);
					equipEntity.setStarLevel(1);
				}
			} 

			if (isSecondGodly) {
				equipEntity.setGodlyAttrId2(SysBasicCfg.getInstance().getGodlyAttrId2(equipCfg.getPart()));
				equipEntity.setStarExp2(0);
				equipEntity.setStarLevel2(1);
			}

			refreshAttribute(equipEntity, player.getPlayerData());

			return equipEntity;
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return null;
	}

	/**
	 * 刷新装备属性
	 * 
	 * @param equipEntity
	 */
	public static Attribute refreshAttribute(EquipEntity equipEntity, PlayerData playerData) {
		if (equipEntity == null) {
			return null;
		}
		EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipEntity.getEquipId());
		if (equipCfg == null) {
			return null;
		}
		Attribute attribute = equipEntity.getAttribute();
		attribute.clear();

		// 主属性(强化可影响) 小魚改不用品級  equipCfg.getQuality() 索引表格 都抓第一欄
		EquipStrengthCfg equipStrengthCfg = ConfigManager.getInstance().getConfigByKey(EquipStrengthCfg.class,1);

		float soul = equipCfg.getPart() == Const.equipPart.WEAPON2_VALUE ? 0.25f :1.0f; //賦魂武器只能25%
		if (equipStrengthCfg != null) {
			int primaryValueRate = equipStrengthCfg.getPrimaryAttrValue(equipEntity.getStrength());
			
			int primaryAttrValue = 0;
			// 改讀表
			for(AttrValue attrValue : equipCfg.getPrimaryAttr()) {
				primaryAttrValue = (int) (attrValue.getMaxVal() * (1.0f + primaryValueRate / 10000.0f) *soul);
				attribute.add(attrValue.getAttrId(), primaryAttrValue);
			}

//			int primaryAttrValue1 = (int) (equipEntity.getPrimaryAttrValue1() * (1.0f + primaryValueRate / 10000.0f) *soul);
//			attribute.add(equipEntity.getPrimaryAttrType1(), primaryAttrValue1);
//
//			int primaryAttrValue2 = (int) (equipEntity.getPrimaryAttrValue2() * (1.0f + primaryValueRate / 10000.0f)*soul);
//			attribute.add(equipEntity.getPrimaryAttrType2(), primaryAttrValue2);
//			
//			int primaryAttrValue3 = (int) (equipEntity.getPrimaryAttrValue3() * (1.0f + primaryValueRate / 10000.0f)*soul);
//			attribute.add(equipEntity.getPrimaryAttrType3(), primaryAttrValue3);
		} else {
			
			// 改讀表
			for(AttrValue attrValue : equipCfg.getPrimaryAttr()) {
				attribute.add(attrValue.getAttrId(),(int)(attrValue.getMaxVal()*soul));
			}
//			attribute.add(equipEntity.getPrimaryAttrType1(),(int) (equipEntity.getPrimaryAttrValue1()*soul));
//			attribute.add(equipEntity.getPrimaryAttrType2(),(int) (equipEntity.getPrimaryAttrValue2()*soul));
//			attribute.add(equipEntity.getPrimaryAttrType3(),(int) (equipEntity.getPrimaryAttrValue3()*soul));
		}

		// 四个副属性
		attribute.add(equipEntity.getSecondaryAttrType1(),(int) (equipEntity.getSecondaryAttrValue1()*soul));
		attribute.add(equipEntity.getSecondaryAttrType2(),(int) (equipEntity.getSecondaryAttrValue2()*soul));
		attribute.add(equipEntity.getSecondaryAttrType3(),(int) (equipEntity.getSecondaryAttrValue3()*soul));
		attribute.add(equipEntity.getSecondaryAttrType4(),(int) (equipEntity.getSecondaryAttrValue4()*soul));

		// 神器属性
		if (equipEntity.getGodlyAttrId() > 0) {
			GodlyAttrCfg godlyAttrCfg = ConfigManager.getInstance().getConfigByKey(GodlyAttrCfg.class, equipEntity.getGodlyAttrId());
			if (godlyAttrCfg != null) {
				attribute.add(equipEntity.getGodlyAttrId(), godlyAttrCfg.getLevelAttr(equipEntity.getStarLevel()));
			} else {
				Log.errPrintln("godly attr is not exist: " + equipEntity.getGodlyAttrId());
			}
		}

		// 神器属性2
		if (equipEntity.getGodlyAttrId2() > 0) {
			GodlyAttrCfg godlyAttrCfg = ConfigManager.getInstance().getConfigByKey(GodlyAttrCfg.class, equipEntity.getGodlyAttrId2());
			if (godlyAttrCfg != null) {
				attribute.add(equipEntity.getGodlyAttrId2(), godlyAttrCfg.getLevelAttr(equipEntity.getStarLevel2()));
			} else {
				Log.errPrintln("godly attr is not exist: " + equipEntity.getGodlyAttrId2());
			}
		}

		// 宝石1属性
		if (equipEntity.getGem1() > 0) {
			ItemCfg itemCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, equipEntity.getGem1());
			if (itemCfg != null) {
				attribute.add(itemCfg.getAttribute());
			}
		}
		// 宝石2属性
		if (equipEntity.getGem2() > 0) {
			ItemCfg itemCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, equipEntity.getGem2());
			if (itemCfg != null) {
				attribute.add(itemCfg.getAttribute());
			}
		}
		// 宝石3属性
		if (equipEntity.getGem3() > 0) {
			ItemCfg itemCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, equipEntity.getGem3());
			if (itemCfg != null) {
				attribute.add(itemCfg.getAttribute());
			}
		}
		// 宝石4属性
		if (equipEntity.getGem4() > 0) {
			ItemCfg itemCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, equipEntity.getGem4());
			if (itemCfg != null) {
				attribute.add(itemCfg.getAttribute());
			}
		}
		
		//职业专属装备属性
		// 专属配置ID
		int equipExclusiveId = equipCfg.getRoleAttrId();
		RoleEntity roleEntity = playerData.getRoleByEquipId(equipEntity.getId());
		if(roleEntity != null){
			RoleEquipCfg roleEquipCfg = ConfigManager.getInstance().getConfigByKey(RoleEquipCfg.class, equipExclusiveId);
			if(roleEquipCfg != null && roleEquipCfg.containRoleId(roleEntity.getItemId())){
				// 等级无关
				attribute.add(roleEquipCfg.getEquipAttrInfo());
				
				// 等级有关
				if (roleEquipCfg.getEquipAttrLevel() != null) {
					Map<attr, Integer> attrMap = roleEquipCfg.getEquipAttrLevel().getAttrMap();
					if (attrMap != null && attrMap.size() > 0) {
						for (Entry<attr, Integer> att : attrMap.entrySet()) {
							attribute.add(att.getKey(), att.getValue() * roleEntity.getLevel());
						}
					}
				}
			}
		}
		
		return attribute;
	}

	/**
	 * 检测背包容量
	 * 
	 * @return true 表示空间足够 false 表示空间不够
	 */
	public static boolean checkEquipCapacity(Player player) {
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		if (player.getPlayerData().getEquipEntities().size() >= stateEntity.getEquipBagSize()) {
			return false;
		}
		return true;
	}

	/**
	 * 获取空装备格子数
	 * 
	 * @return
	 */
	public static int getEmptyEquipSlotCount(Player player) {
		int dressEquipSize = 0;

		for (RoleEntity roleEntity : player.getPlayerData().getRoleEntities()) {
			for (int i = Const.equipPart.HELMET_VALUE; i <= Const.equipPart.NECKLACE_VALUE; i++) {
				if (roleEntity.getPartEquipId(i) > 0) {
					dressEquipSize++;
				}
			}
		}

		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		int emptySize = stateEntity.getEquipBagSize() - (player.getPlayerData().getEquipEntities().size() - dressEquipSize);
		return Math.max(0, emptySize);
	}

	/**
	 * 检测装备是否自动售出
	 * 
	 * @param equipCfg
	 * @return
	 */
	public static boolean checkAutoSellEquip(Player player, EquipCfg equipCfg) {
		if (equipCfg != null) {
			if (equipCfg.getQuality() >= Const.equipQuality.ORANGE_VALUE) {
				return false;
			}

			StateEntity stateEntity = player.getPlayerData().getStateEntity();
			int autoSellMask = stateEntity.getAutoSellEquip();
			if ((autoSellMask & (1 << equipCfg.getQuality())) > 0) {
				return true;
			}

			// 非本职业售出
//			if ((autoSellMask & (1 << 5)) > 0 && !equipCfg.checkProfession(player.getProf())) {
//				return true;
//			}
		}
		return false;
	}

	/**
	 * 元素自动分解;
	 * 
	 * @param player
	 * @param equipCfg
	 * @return
	 */
	public static boolean checkAutoSellElement(Player player, ElementCfg elementCfg) {
		if (elementCfg != null) {
			if (elementCfg.getQuality() == Const.elementQuality.ELEMENT_ORANGE_VALUE) {
				return false;
			}

			StateEntity stateEntity = player.getPlayerData().getStateEntity();
			int autoSellElement = stateEntity.getAutoDecoElement();
			if ((autoSellElement & (1 << elementCfg.getQuality())) > 0) {
				return true;
			}

		}
		return false;
	}

	/**
	 * 同步数据库创建装备
	 * 
	 * @param equipEntities
	 * @return
	 */
	public static boolean createEquipsSync(List<EquipEntity> equipEntities) {
		String insertSql = "INSERT INTO equip (playerId,equipId,strength,strengthItemStr,starLevel,"
				+ "starExp,godlyAttrId,primaryAttrType1,primaryAttrValue1,primaryAttrType2,primaryAttrValue2,primaryAttrType3,primaryAttrValue3,"
				+ "secondaryAttrType1,secondaryAttrValue1,secondaryAttrType2,secondaryAttrValue2,secondaryAttrType3,"
				+ "secondaryAttrValue3,secondaryAttrType4,secondaryAttrValue4,gem1,gem2,gem3,gem4,status," + "createTime,updateTime,invalid) values ";
		StringBuilder sb = new StringBuilder(insertSql);
		for (int i = 0; i < equipEntities.size(); i++) {
			if (i == equipEntities.size() - 1) {
				sb.append(generatePartSql(equipEntities.get(i)));
			} else {
				sb.append(generatePartSql(equipEntities.get(i))).append(",");
			}
		}

		sb.append(";");
		insertSql = sb.toString();
		List<Long> primaryKeyList = DBManager.getInstance().executeInsert(insertSql);
		for (int i = 0; i < primaryKeyList.size(); i++) {
			equipEntities.get(i).setId(primaryKeyList.get(i));
		}
		return true;
	}

	private static String generatePartSql(EquipEntity equipEntity) {
		StringBuilder sb = new StringBuilder(2048).append("(");
		sb.append(equipEntity.getPlayerId()).append(",").append(equipEntity.getEquipId()).append(",").append(equipEntity.getStrength()).append(",'")
				.append(equipEntity.getStrengthItemStr()).append("',").append(equipEntity.getStarLevel()).append(",").append(equipEntity.getStarExp())
				.append(",").append(equipEntity.getGodlyAttrId()).append(",").append(equipEntity.getPrimaryAttrType1()).append(",")
				.append(equipEntity.getPrimaryAttrValue1()).append(",").append(equipEntity.getPrimaryAttrType2()).append(",")
				.append(equipEntity.getPrimaryAttrValue2()).append(",").append(equipEntity.getPrimaryAttrType3()).append(",")
				.append(equipEntity.getPrimaryAttrValue3()).append(",").append(equipEntity.getSecondaryAttrType1()).append(",")
				.append(equipEntity.getSecondaryAttrValue1()).append(",").append(equipEntity.getSecondaryAttrType2()).append(",")
				.append(equipEntity.getSecondaryAttrValue2()).append(",").append(equipEntity.getSecondaryAttrType3()).append(",")
				.append(equipEntity.getSecondaryAttrValue3()).append(",").append(equipEntity.getSecondaryAttrType4()).append(",")
				.append(equipEntity.getSecondaryAttrValue4()).append(",").append(equipEntity.getGem1()).append(",").append(equipEntity.getGem2())
				.append(",").append(equipEntity.getGem3()).append(",").append(equipEntity.getGem4()).append(",").append(equipEntity.getStatus())
				.append(",").append("'").append(GuaJiTime.getTimeString(equipEntity.getCreateTime())).append("',").append("'")
				.append(GuaJiTime.getTimeString(equipEntity.getUpdateTime())).append("',").append(equipEntity.isInvalid() ? 1 : 0).append(")");
		return sb.toString();
	}

	/**
	 * 检测神器星级
	 * 
	 * @param player
	 * @param equipEntity
	 */
	public static void checkStarExp(Player player, EquipEntity equipEntity) {
		// TODO : 补丁, 修复玩家1星神器提供 两点经验bug;
		if (equipEntity.getStarLevel() == 1 && equipEntity.getStarExp() == 1) {
			equipEntity.setStarExp(0);
			equipEntity.notifyUpdate(false);
		}
		if (equipEntity.getStarLevel2() == 1 && equipEntity.getStarExp2() == 1) {
			equipEntity.setStarExp(0);
			equipEntity.notifyUpdate(false);
		}

		if (equipEntity.getStarLevel() > GodlyLevelExpCfg.getLevelByExp(equipEntity.getStarExp())) {
			int exp = GodlyLevelExpCfg.getConfigByLevel(equipEntity.getStarLevel() - 1).getExp();
			equipEntity.setStarExp(exp);
			equipEntity.notifyUpdate(false);
		}

		if (equipEntity.getStarLevel2() > GodlyLevelExpCfg.getLevelByExp2(equipEntity.getStarExp2())) {
			int exp = GodlyLevelExpCfg.getConfigByLevel(equipEntity.getStarLevel2() - 1).getExp();
			equipEntity.setStarExp2(exp);
			equipEntity.notifyUpdate(false);
		}
	}

	/**
	 * 把装备进化的目标进行替换属性
	 * 
	 * @param equipEntity
	 */
	public static void replaceEquipItemId(EquipEntity equipEntity, EquipCfg targetCfg, Player player) {
		try {
			if (targetCfg == null) {
				return;
			}
			// 生成主属性 改成讀表
//			int primaryAttrCount = 0;
//			List<EquipCfg.AttrValue> primaryAttr = targetCfg.getPrimaryAttr();
//			if (primaryAttr != null && primaryAttr.size() > 0) {
//				for (EquipCfg.AttrValue attrValue : primaryAttr) {
//					if (attrValue.isValid()) {
//						if (primaryAttrCount == 0) {
//							equipEntity.setPrimaryAttrType1(attrValue.getAttrId());
//							equipEntity.setPrimaryAttrValue1(attrValue.randomAttrValue());
//						} else if (primaryAttrCount == 1) {
//							equipEntity.setPrimaryAttrType2(attrValue.getAttrId());
//							equipEntity.setPrimaryAttrValue2(attrValue.randomAttrValue());
//						} else if (primaryAttrCount == 2) {
//							equipEntity.setPrimaryAttrType3(attrValue.getAttrId());
//							equipEntity.setPrimaryAttrValue3(attrValue.randomAttrValue());
//						}
//						primaryAttrCount++;
//					}
//				}
//			}

			// 生成副属性
			if (targetCfg.getMaxAttrPoint() >= targetCfg.getMinAttrPoint()) {
				int attrPoint = 0;
				try {
					attrPoint = GuaJiRand.randInt(targetCfg.getMinAttrPoint(), targetCfg.getMaxAttrPoint());
				} catch (Exception e) {
					MyException.catchException(e);
				}

				List<Integer> attrVals = null;
				List<Integer> attrIds = new LinkedList<Integer>();
				attrIds.add(Const.attr.STRENGHT_VALUE);
				attrIds.add(Const.attr.AGILITY_VALUE);
				attrIds.add(Const.attr.INTELLECT_VALUE);
				attrIds.add(Const.attr.STAMINA_VALUE);
				GuaJiRand.randomOrder(attrIds);

				if (targetCfg.getQuality() == Const.equipQuality.GREEN_VALUE) {
					attrVals = GuaJiRand.randomIncision(attrPoint, 1, 1);
				} else if (targetCfg.getQuality() == Const.equipQuality.BLUE_VALUE) {
					attrVals = GuaJiRand.randomIncision(attrPoint, 2, (int) (attrPoint / 2 * 0.9));
				} else if (targetCfg.getQuality() == Const.equipQuality.PURPLE_VALUE) {
					attrVals = GuaJiRand.randomIncision(attrPoint, 3, (int) (attrPoint / 3 * 0.9));
				} else if (targetCfg.getQuality() >= Const.equipQuality.ORANGE_VALUE) {
					attrVals = GuaJiRand.randomIncision(attrPoint, 4, (int) (0.9 * attrPoint / 4));
				}
				// 装备等级大于100级,需要修正耐力属性百分比
				if (targetCfg.getLevel() > GsConst.Equip.STAMINA_PERCENT_LEVEL_LIMIT) {
					revisedStaminaValue(attrIds, attrVals, attrPoint, true);
				}

				if (attrVals != null && attrVals.size() > 0) {
					int secondaryAttrCount = 0;
					if (attrVals.size() >= 1) {
						equipEntity.setSecondaryAttrType1(attrIds.get(secondaryAttrCount));
						equipEntity.setSecondaryAttrValue1(attrVals.get(secondaryAttrCount));
						secondaryAttrCount++;
					}

					if (attrVals.size() >= 2) {
						equipEntity.setSecondaryAttrType2(attrIds.get(secondaryAttrCount));
						equipEntity.setSecondaryAttrValue2(attrVals.get(secondaryAttrCount));
						secondaryAttrCount++;
					}

					if (attrVals.size() >= 3) {
						equipEntity.setSecondaryAttrType3(attrIds.get(secondaryAttrCount));
						equipEntity.setSecondaryAttrValue3(attrVals.get(secondaryAttrCount));
						secondaryAttrCount++;
					}

					if (attrVals.size() >= 4) {
						equipEntity.setSecondaryAttrType4(attrIds.get(secondaryAttrCount));
						equipEntity.setSecondaryAttrValue4(attrVals.get(secondaryAttrCount));
						secondaryAttrCount++;
					}
				}
			}
			
			// 更新裝備相生
			if (targetCfg.getSeries() != 0) {
				MutualEntity mEntity = player.getPlayerData().loadMutualEntity();
				if (mEntity != null) {
					mEntity.addStarMap(targetCfg.getSeries(), targetCfg.getStar());
					mEntity.notifyUpdate(true);
				}
			}
			
			refreshAttribute(equipEntity, player.getPlayerData());

		} catch (Exception e) {
			MyException.catchException(e);
		}
	}

	/**
	 * 获取玩家指定品质的所有装备(除了身上穿的)
	 * 
	 * @param quality
	 * @param player
	 * @return
	 */
	public static List<Long> getEquipsByQuality(int quality, Player player) {
		List<Long> idList = new ArrayList<Long>();
		if (quality > 0) {
			List<EquipEntity> equipEntityList = player.getPlayerData().getEquipEntities();
			for (EquipEntity equipEntity : equipEntityList) {
				EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipEntity.getEquipId());
				// 过滤身上的装备
				if (player.getPlayerData().getRoleByEquipId(equipEntity.getId()) != null) {
					continue;
				}
				// 过滤套装和镶嵌有宝石的装备
				if (equipEntity.isGemDressed() || equipCfg.getSuitId() > 0) {
					continue;
				}
				// 品质相同且不是神器
				if (equipCfg.getQuality() == quality && equipEntity.getGodlyAttrId() <= 0 && equipEntity.getGodlyAttrId2() <= 0) {
					idList.add(equipEntity.getId());
				}
			}
		}
		return idList;
	}

	/**
	 * 套装超过100级修正耐力属性百分比,需要修正的是attrVals集合
	 * 
	 * @param attrIds
	 *            属性ID集合
	 * @param attrValues
	 *            属性值集合
	 * @param totalValue
	 *            属性总值
	 * @param isByIndex
	 *            是否根据根据索引查找耐力属性
	 * @return
	 */
	public static List<Integer> revisedStaminaValue(List<Integer> attrIds, List<Integer> attrValues, int totalValue, boolean isByIndex) {

		int staminaIndex = attrIds.indexOf(Const.attr.STAMINA_VALUE);
		// 没有耐力属性无需修正
		if (staminaIndex < 0) {
			return attrValues;
		}
		// 至少要有两个属性
		if (attrIds.size() < 2) {
			return attrValues;
		}
		// 当前所占百分比
		int staminaValue = 0;
		if (!isByIndex) {
			// 套装洗练处理
			staminaIndex = attrValues.size() - staminaIndex - 1;
		}
		staminaValue = attrValues.get(staminaIndex);
		float percent = (float) staminaValue / totalValue;
		// 超出范围修正
		if (percent > GsConst.Equip.STAMINA_PERCENT_MAX_LIMIT) {
			// 取耐力区间20%~25%
			float staminaLimit = 0.0f;
			try {
				staminaLimit = GuaJiRand.randFloat(GsConst.Equip.STAMINA_PERCENT_MIN_LIMIT, GsConst.Equip.STAMINA_PERCENT_MAX_LIMIT);
			} catch (MyException e) {
				e.printStackTrace();
			}
			int maxValue = (int) (totalValue * staminaLimit);
			attrValues.set(staminaIndex, maxValue);
			// 超出部随机分配给其他属性
			int limit = attrIds.size() - 1;
			int attrId = GuaJiRand.randInt(limit);
			while (staminaIndex == attrId) {
				attrId = GuaJiRand.randInt(limit);
			}
			staminaValue = staminaValue - maxValue + attrValues.get(attrId);
			attrValues.set(attrId, staminaValue);
		}
		return attrValues;
	}

	/**
	 * 获取玩家指定ItemId的装备數量(除了身上穿的)
	 * 
	 * @param itemId
	 * @param player
	 * @return
	 */
	public static int getEquipsCount(int itemId, Player player) {
		int count = 0;
		List<EquipEntity> equipEntityList = player.getPlayerData().getEquipEntities();
		// 过滤身上的装备
		for (EquipEntity equipEntity : equipEntityList) {
			if (equipEntity.getEquipId() != itemId) {
				continue;
			}
			EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, itemId);
			// 过滤身上的装备
			if (equipCfg == null) {
				return -1;
			}
			if (player.getPlayerData().getRoleByEquipId(equipEntity.getId()) != null) {
				continue;
			}
			count++;
//			// 过滤套装和镶嵌有宝石的装备
//			if (equipEntity.isGemDressed() || equipCfg.getSuitId() > 0) {
//				continue;
//			}
//			// 品质相同且不是神器
//			if (equipCfg.getQuality() == quality && equipEntity.getGodlyAttrId() <= 0 && equipEntity.getGodlyAttrId2() <= 0) {
//				idList.add(equipEntity.getId());
//			}
		}
		return count;
	}
	

}
