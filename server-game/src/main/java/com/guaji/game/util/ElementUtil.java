package com.guaji.game.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigManager;
import org.guaji.os.MyException;
import org.guaji.os.GuaJiRand;

import com.guaji.game.protocol.Attribute.Attr;
import com.guaji.game.protocol.Attribute.AttrInfo;
import com.guaji.game.attribute.Attribute;
import com.guaji.game.config.ElementAttrCfg;
import com.guaji.game.config.ElementCfg;
import com.guaji.game.config.LevelAttrRatioCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.ElementEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.player.Player;
import com.guaji.game.util.WeightUtil.WeightItem;
import com.guaji.game.protocol.Const;

/**
 * 元素帮助类
 */
public class ElementUtil {

	/**
	 * 获得装备所穿戴的角色
	 */
	public static RoleEntity getElementDressRole(Player player, ElementEntity elementEntity) {
		List<RoleEntity> roleEntities = player.getPlayerData().getRoleEntities();
		for (RoleEntity roleEntity : roleEntities) {
			if (roleEntity.checkElementInDress(elementEntity.getId())) {
				return roleEntity;
			}
		}
		return null;
	}
	

	/**
	 * 生成元素
	 * 
	 * @param player
	 * @param eleItemId
	 * @return
	 */
	public static ElementEntity generateElement(Player player, int eleItemId, AttrInfo.Builder attr) {
		try {
			ElementCfg elementCfg = ConfigManager.getInstance().getConfigByKey(ElementCfg.class, eleItemId);
			if (elementCfg == null) {
				return null;
			}

			ElementEntity elementEntity = new ElementEntity();
			elementEntity.setPlayerId(player.getId());
			elementEntity.setItemId(elementCfg.getId());
			elementEntity.setQuality(elementCfg.getQuality());
			elementEntity.setLevel(GuaJiRand.randInt(elementCfg.getMinLevel(), elementCfg.getMaxLevel()));
			if(attr == null) {
				// 生成默认属性
				List<Integer> defaultBasicIds = elementCfg.getDefaultBasicIds();
				if(defaultBasicIds != null && defaultBasicIds.size() > 0) {
					for(Integer attrCfgId : defaultBasicIds) {
						ElementAttrCfg elementAttrCfg = ConfigManager.getInstance().getConfigByKey(ElementAttrCfg.class, attrCfgId);
						if(elementAttrCfg != null) {
							elementEntity.addBasicAttr(elementAttrCfg.getAttrId(),elementAttrCfg.getAttrValue());
						}
					}
				}
			} else {
				for (Attr.Builder eacheAttr : attr.getAttributeBuilderList()) {
					elementEntity.addBasicAttr(eacheAttr.getAttrId(), eacheAttr.getAttrValue());
				}
			}
			
			List<Integer> defaultExtraIds = elementCfg.getDefaultExtraIds();
			if(defaultExtraIds != null && defaultExtraIds.size() > 0) {
				for(Integer attrCfgId : defaultExtraIds) {
					ElementAttrCfg elementAttrCfg = ConfigManager.getInstance().getConfigByKey(ElementAttrCfg.class, attrCfgId);
					if(elementAttrCfg != null) {
						elementEntity.addExtraAttr(elementAttrCfg.getAttrId(),elementAttrCfg.getAttrValue());
					}
				}
			}
			if(attr == null) {
				for(int i = 0 ; i < elementCfg.getIsInitAttr(); i++) {
					advanceElement(elementEntity);
				}
			}
			refreshAttribute(elementEntity);
			
			return elementEntity;
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return null;
	}

	public static ElementEntity generateElement(Player player, int eleItemId) {
		return generateElement(player, eleItemId, null);
	}
	
	public static void advanceElementAttr(ElementCfg elementCfg, AttrInfo.Builder attrBuilder) {
		List<WeightItem<Integer>> basicRandomList = new LinkedList<>();
		basicRandomList.addAll(elementCfg.getRandomBasic());
		if(basicRandomList.size() > 0) {
			Iterator<WeightItem<Integer>> iter = basicRandomList.iterator();
			while(iter.hasNext()) {
				WeightItem<Integer> item = iter.next();
				ElementAttrCfg elementAttrCfg = ConfigManager.getInstance().getConfigByKey(ElementAttrCfg.class, item.getValue());
				boolean isHad = false;
				List<Attr.Builder> attributeBuilderList = attrBuilder.getAttributeBuilderList();
				for (Attr.Builder builder : attributeBuilderList) {
					if (builder.getAttrId() == elementAttrCfg.getAttrId()) {
						isHad = true;
					}
				}
				if(elementAttrCfg == null || isHad) {
					iter.remove();
				}
			}
			
			if(basicRandomList.size() > 0) {
				Integer attrCfgId = WeightUtil.random(basicRandomList);	
				ElementAttrCfg elementAttrCfg = ConfigManager.getInstance().getConfigByKey(ElementAttrCfg.class, attrCfgId);
				if(elementAttrCfg != null) {
					attrBuilder.addAttribute(Attr.newBuilder().setAttrId(elementAttrCfg.getAttrId()).setAttrValue(elementAttrCfg.getAttrValue()));
				}
			}
		}
	}
	
	/**
	 * 元素进阶属性生成
	 * @param elementEntity
	 */
	public static void advanceElement(ElementEntity elementEntity) {
		ElementCfg elementCfg = ConfigManager.getInstance().getConfigByKey(ElementCfg.class, elementEntity.getItemId());
		List<WeightItem<Integer>> basicRandomList = new LinkedList<>();
		basicRandomList.addAll(elementCfg.getRandomBasic());
		if(basicRandomList.size() > 0) {
			Iterator<WeightItem<Integer>> iter = basicRandomList.iterator();
			while(iter.hasNext()) {
				WeightItem<Integer> item = iter.next();
				ElementAttrCfg elementAttrCfg = ConfigManager.getInstance().getConfigByKey(ElementAttrCfg.class, item.getValue());
				if(elementAttrCfg == null || elementEntity.getBasicAttr().containsAttr(elementAttrCfg.getAttrId())) {
					iter.remove();
				}
			}
			
			if(basicRandomList.size() > 0) {
				Integer attrCfgId = WeightUtil.random(basicRandomList);	
				ElementAttrCfg elementAttrCfg = ConfigManager.getInstance().getConfigByKey(ElementAttrCfg.class, attrCfgId);
				if(elementAttrCfg != null) {
					elementEntity.addBasicAttr(elementAttrCfg.getAttrId(),elementAttrCfg.getAttrValue());
				}
			}
		}
		
		// 超出额外属性条数
		if(elementEntity == null || elementEntity.getExtraAttr().size() >= elementCfg.getExtraCountLimit()) {
			return ;
		}
		
		List<WeightItem<Integer>> extraRandomList = new LinkedList<>();
		if(elementEntity.getQuality() == 2) {
			extraRandomList.addAll(elementCfg.getRandomExtra2Pool());
		} else if(elementEntity.getQuality() == 3) {
			extraRandomList.addAll(elementCfg.getRandomExtra3Pool());
		} else if(elementEntity.getQuality() == 4) {
			extraRandomList.addAll(elementCfg.getRandomExtra4Pool());
		} else {
			return ;
		}
		if(extraRandomList.size() > 0) {
			Iterator<WeightItem<Integer>> iter = extraRandomList.iterator();
			while(iter.hasNext()) {
				WeightItem<Integer> item = iter.next();
				ElementAttrCfg elementAttrCfg = ConfigManager.getInstance().getConfigByKey(ElementAttrCfg.class, item.getValue());
				if(elementAttrCfg == null || elementEntity.getExtraAttr().containsAttr(elementAttrCfg.getAttrId())) {
					iter.remove();
				}
			}
			
			if(extraRandomList.size() > 0) {
				Integer attrCfgId = WeightUtil.random(extraRandomList);	
				ElementAttrCfg elementAttrCfg = ConfigManager.getInstance().getConfigByKey(ElementAttrCfg.class, attrCfgId);
				if(elementAttrCfg != null && elementAttrCfg.getAttrId() > 0) {
					elementEntity.addExtraAttr(elementAttrCfg.getAttrId(),elementAttrCfg.getAttrValue());
				}
			}
		}
	}
	
	/**
	 * 刷新元素属性
	 * 
	 * @param equipEntity
	 */
	public static Attribute refreshAttribute(ElementEntity elementEntity) {
		// 计算等级加成
		elementEntity.getAttribute().clear();
		for(Map.Entry<Const.attr, Integer> entry : elementEntity.getBasicAttr().getAttrMap().entrySet()) {
			float ratio = LevelAttrRatioCfg.getRatio(elementEntity.getLevel(), entry.getKey().getNumber());
			elementEntity.getAttribute().add(entry.getKey(), (int)(ratio * entry.getValue()));
		}
		
		for(Map.Entry<Const.attr, Integer> entry : elementEntity.getExtraAttr().getAttrMap().entrySet()) {
			float ratio = LevelAttrRatioCfg.getRatio(elementEntity.getLevel(), entry.getKey().getNumber());
			elementEntity.getAttribute().add(entry.getKey(), (int)(ratio * entry.getValue()));
		}
		
		return elementEntity.getAttribute();
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
	 * 获取空元素格子数
	 * @return
	 */
	public static int getEmptyElementSlotCount(Player player) {
		int dressElementSize = 0;
		
		for (RoleEntity roleEntity : player.getPlayerData().getRoleEntities()) {
			for (int i=0; i<=SysBasicCfg.getInstance().getMaxElementSize(); i++) {
				if (roleEntity.getElementByIndex(i) > 0) {
					dressElementSize ++;
				}
			}
		}
		
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		int emptySize = stateEntity.getElementBagSize() - (player.getPlayerData().getElementEntities().size() - dressElementSize);
		return Math.max(0, emptySize);
	}
	
}

