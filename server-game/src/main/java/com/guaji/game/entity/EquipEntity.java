package com.guaji.game.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.log.Log;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.attribute.Attribute;
import com.guaji.game.config.GodlyLevelExpCfg;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.util.EquipUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsonUtil;

/**
 * 装备实体对象
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "equip")
public class EquipEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private long id = 0;

	@Column(name = "playerId")
	private int playerId = 0;

	/**
	 * 装备配置Id
	 */
	@Column(name = "equipId")
	private int equipId = 0;

	/**
	 * 装备强化等级
	 */
	@Column(name = "strength")
	private int strength = 0;

	/**
	 * 强化消耗的装备精华的道具数量
	 */
	@Column(name = "strengthItemStr")
	private String strengthItemStr;

	@Transient
	private Map<Integer, ItemInfo> strengthItemMap;

	/**
	 * 神器属性等级
	 */
	@Column(name = "starLevel")
	private int starLevel = 0;

	/**
	 * 神器经验值
	 */
	@Column(name = "starExp")
	private int starExp = 0;

	/**
	 * 神器属性id
	 */
	@Column(name = "godlyAttrId")
	private int godlyAttrId = 0;

	/**
	 * 神器属性等级2
	 */
	@Column(name = "starLevel2")
	private int starLevel2 = 0;

	/**
	 * 神器经验值2
	 */
	@Column(name = "starExp2")
	private int starExp2 = 0;

	/**
	 * 神器属性id2
	 */
	@Column(name = "godlyAttrId2")
	private int godlyAttrId2 = 0;

	/**
	 * 装备主属性1类型
	 */
	@Column(name = "primaryAttrType1")
	private int primaryAttrType1 = 0;

	/**
	 * 装备主属1性值
	 */
	@Column(name = "primaryAttrValue1")
	private int primaryAttrValue1 = 0;

	/**
	 * 装备主属性2类型
	 */
	@Column(name = "primaryAttrType2")
	private int primaryAttrType2 = 0;

	/**
	 * 装备主属性2值
	 */
	@Column(name = "primaryAttrValue2")
	private int primaryAttrValue2 = 0;
	
	/**
	 * 装备主属性3类型
	 */
	@Column(name = "primaryAttrType3")
	private int primaryAttrType3 = 0;

	/**
	 * 装备主属性3值
	 */
	@Column(name = "primaryAttrValue3")
	private int primaryAttrValue3 = 0;

	/**
	 * 装备二级属性类型1
	 */
	@Column(name = "secondaryAttrType1")
	private int secondaryAttrType1 = 0;

	/**
	 * 装备二级属性值1
	 */
	@Column(name = "secondaryAttrValue1")
	private int secondaryAttrValue1 = 0;

	/**
	 * 装备二级属性类型2
	 */
	@Column(name = "secondaryAttrType2")
	private int secondaryAttrType2 = 0;

	/**
	 * 装备二级属性值2
	 */
	@Column(name = "secondaryAttrValue2")
	private int secondaryAttrValue2 = 0;

	/**
	 * 装备二级属性类型3
	 */
	@Column(name = "secondaryAttrType3")
	private int secondaryAttrType3 = 0;

	/**
	 * 装备二级属性值3
	 */
	@Column(name = "secondaryAttrValue3")
	private int secondaryAttrValue3 = 0;

	/**
	 * 装备二级属性类型4
	 */
	@Column(name = "secondaryAttrType4")
	private int secondaryAttrType4 = 0;

	/**
	 * 装备二级属性值4
	 */
	@Column(name = "secondaryAttrValue4")
	private int secondaryAttrValue4 = 0;

	/**
	 * 一号孔的宝石
	 */
	@Column(name = "gem1")
	private int gem1 = 0;

	/**
	 * 二号孔的宝石
	 */
	@Column(name = "gem2")
	private int gem2 = 0;

	/**
	 * 三号孔的宝石
	 */
	@Column(name = "gem3")
	private int gem3 = 0;

	/**
	 * 四号孔的宝石
	 */
	@Column(name = "gem4")
	private int gem4 = 0;

	@Column(name = "status")
	private int status = 0;

	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	/**
	 * 装备属性集合
	 */
	@Transient
	protected Attribute attribute;

	public EquipEntity() {
		strengthItemMap = new HashMap<Integer, ItemInfo>();
		this.strengthItemStr = GsonUtil.getJsonInstance().toJson(this.strengthItemMap);
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.updateTime = GuaJiTime.getCalendar().getTime();
		this.gem1 = this.gem2 = this.gem3 = this.gem4 = -1;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getEquipId() {
		return equipId;
	}

	public void setEquipId(int equipId) {
		this.equipId = equipId;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
		// 推送装备强化事件
		QuestEventBus.fireQuestEventOneTime(QuestEventType.EQUIP_ENHANCE_TIMES, GuaJiXID.valueOf(GsConst.ObjType.PLAYER, this.playerId));

		QuestEventBus.fireQuestEvent(QuestEventType.EQUIP_ENHANCE_LEVEL, strength, GuaJiXID.valueOf(GsConst.ObjType.PLAYER, this.playerId));
	}

	public int getStarLevel() {
		return starLevel;
	}

	public void setStarLevel(int starLevel) {
		this.starLevel = starLevel;
		// 推送普通神器等级事件
		QuestEventBus.fireQuestEvent(QuestEventType.COMMON_GOD_EQUIP_LEVEL, starLevel, GuaJiXID.valueOf(GsConst.ObjType.PLAYER, this.playerId));
	}

	public int getStarExp() {
		return starExp;
	}

	public void setStarExp(int starExp) {
		this.starExp = starExp;
	}

	public int getGodlyAttrId() {
		return godlyAttrId;
	}

	public void setGodlyAttrId(int godlyAttrId) {
		this.godlyAttrId = godlyAttrId;
	}

	public int getPrimaryAttrType1() {
		return primaryAttrType1;
	}

	public void setPrimaryAttrType1(int primaryAttrType1) {
		this.primaryAttrType1 = primaryAttrType1;
	}

	public int getPrimaryAttrValue1() {
		return primaryAttrValue1;
	}

	public void setPrimaryAttrValue1(int primaryAttrValue1) {
		this.primaryAttrValue1 = primaryAttrValue1;
	}

	public int getPrimaryAttrType2() {
		return primaryAttrType2;
	}

	public void setPrimaryAttrType2(int primaryAttrType2) {
		this.primaryAttrType2 = primaryAttrType2;
	}

	public int getPrimaryAttrValue2() {
		return primaryAttrValue2;
	}

	public void setPrimaryAttrValue2(int primaryAttrValue2) {
		this.primaryAttrValue2 = primaryAttrValue2;
	}
	
	public int getPrimaryAttrType3() {
		return primaryAttrType3;
	}

	public void setPrimaryAttrType3(int primaryAttrType3) {
		this.primaryAttrType3 = primaryAttrType3;
	}

	public int getPrimaryAttrValue3() {
		return primaryAttrValue3;
	}

	public void setPrimaryAttrValue3(int primaryAttrValue3) {
		this.primaryAttrValue3 = primaryAttrValue3;
	}

	public int getSecondaryAttrType1() {
		return secondaryAttrType1;
	}

	public void setSecondaryAttrType1(int secondaryAttrType1) {
		this.secondaryAttrType1 = secondaryAttrType1;
	}

	public int getSecondaryAttrValue1() {
		return secondaryAttrValue1;
	}

	public void setSecondaryAttrValue1(int secondaryAttrValue1) {
		this.secondaryAttrValue1 = secondaryAttrValue1;
	}

	public int getSecondaryAttrType2() {
		return secondaryAttrType2;
	}

	public void setSecondaryAttrType2(int secondaryAttrType2) {
		this.secondaryAttrType2 = secondaryAttrType2;
	}

	public int getSecondaryAttrValue2() {
		return secondaryAttrValue2;
	}

	public void setSecondaryAttrValue2(int secondaryAttrValue2) {
		this.secondaryAttrValue2 = secondaryAttrValue2;
	}

	public int getSecondaryAttrType3() {
		return secondaryAttrType3;
	}

	public void setSecondaryAttrType3(int secondaryAttrType3) {
		this.secondaryAttrType3 = secondaryAttrType3;
	}

	public int getSecondaryAttrValue3() {
		return secondaryAttrValue3;
	}

	public void setSecondaryAttrValue3(int secondaryAttrValue3) {
		this.secondaryAttrValue3 = secondaryAttrValue3;
	}

	public int getSecondaryAttrType4() {
		return secondaryAttrType4;
	}

	public void setSecondaryAttrType4(int secondaryAttrType4) {
		this.secondaryAttrType4 = secondaryAttrType4;
	}

	public int getSecondaryAttrValue4() {
		return secondaryAttrValue4;
	}

	public void setSecondaryAttrValue4(int secondaryAttrValue4) {
		this.secondaryAttrValue4 = secondaryAttrValue4;
	}

	public int getGem1() {
		return gem1;
	}

	public void setGem1(int gem1) {
		this.gem1 = gem1;
	}

	public int getGem2() {
		return gem2;
	}

	public void setGem2(int gem2) {
		this.gem2 = gem2;
	}

	public int getGem3() {
		return gem3;
	}

	public void setGem3(int gem3) {
		this.gem3 = gem3;
	}

	public int getGem4() {
		return gem4;
	}

	public void setGem4(int gem4) {
		this.gem4 = gem4;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	public Attribute getAttribute() {
		if (attribute == null) {
			attribute = new Attribute();
		}
		return attribute;
	}

	/**
	 * 初始化宝石
	 */
	public void clearGemId() {
		int gems[] = { this.getGem1(), this.getGem2(), this.getGem3(), this.getGem4() };
		for (int i = 0; i < gems.length; i++) {
			if (gems[i] > 0) {
				gems[i] = 0;
			}
			this.setGemId(i + 1, gems[i]);
		}
	}

	/**
	 * 获取装备宝石孔个数
	 */
	public int getGemPunchCount() {
		int gems[] = { this.getGem1(), this.getGem2(), this.getGem3(), this.getGem4() };
		int count = 0;
		for (int gem : gems) {
			if (gem > -1) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 获得对应位置的宝石状态
	 * 
	 * @param index
	 * @return
	 */
	public int getGemId(int index) {
		switch (index) {
		case 1:
			return gem1;
		case 2:
			return gem2;
		case 3:
			return gem3;
		case 4:
			return gem4;
		default:
			throw new RuntimeException("equip getGemId index error : index = " + index);
		}
	}

	/**
	 * 设置对应位置上宝石状态
	 * 
	 * @param index
	 * @param gemId
	 */
	public void setGemId(int index, int gemId) {
		switch (index) {
		case 1:
			this.gem1 = gemId;
			break;
		case 2:
			this.gem2 = gemId;
			break;
		case 3:
			this.gem3 = gemId;
			break;
		case 4:
			this.gem4 = gemId;
			break;

		default:
			throw new RuntimeException("equip setGemId index error : index = " + index);
		}
	}

	/**
	 * 获得有效的二级属性条数
	 * 
	 * @return
	 */
	public int getAvailableSecAttrCount() {
		int count = 0;
		if (secondaryAttrType1 > 0 && secondaryAttrValue1 > 0) {
			count++;
		}
		if (secondaryAttrType2 > 0 && secondaryAttrValue2 > 0) {
			count++;
		}
		if (secondaryAttrType3 > 0 && secondaryAttrValue3 > 0) {
			count++;
		}
		if (secondaryAttrType4 > 0 && secondaryAttrValue4 > 0) {
			count++;
		}
		return count;
	}

	/**
	 * 刷新神器星级属性
	 */
	public void refreshStarLevel() {
		this.starLevel = GodlyLevelExpCfg.getLevelByExp(starExp);
		this.starLevel2 = GodlyLevelExpCfg.getLevelByExp2(starExp2);
		// 推送普通神器等级事件
		QuestEventBus.fireQuestEvent(QuestEventType.COMMON_GOD_EQUIP_LEVEL, starLevel, GuaJiXID.valueOf(GsConst.ObjType.PLAYER, this.playerId));
		// 推送声望神器等级事件
		QuestEventBus.fireQuestEvent(QuestEventType.HOUNOR_GOD_EQUIP_LEVEL, starLevel2, GuaJiXID.valueOf(GsConst.ObjType.PLAYER, this.playerId));
	}

	public int getScore() {
		return EquipUtil.calcEquipScore(this);
	}

	public List<Integer> getAvailableSecAttrTypes() {
		List<Integer> attrTypeList = new LinkedList<>();
		if (secondaryAttrType1 > 0) {
			attrTypeList.add(secondaryAttrType1);
		}
		if (secondaryAttrType2 > 0) {
			attrTypeList.add(secondaryAttrType2);
		}
		if (secondaryAttrType3 > 0) {
			attrTypeList.add(secondaryAttrType3);
		}
		if (secondaryAttrType4 > 0) {
			attrTypeList.add(secondaryAttrType4);
		}
		return attrTypeList;
	}
	
	public Map<Integer,Integer> getPrimaryAttrMap() {
		 Map<Integer,Integer> attrTypeMap = new HashMap<>();
		if (primaryAttrType1 > 0) {
			attrTypeMap.put(primaryAttrType1,primaryAttrValue1);
		}
		if (primaryAttrType2 > 0) {
			attrTypeMap.put(primaryAttrType2,primaryAttrValue2);
		}
		return attrTypeMap;
	}
	
	public Map<Integer,Integer> getSecAttrMap() {
		 Map<Integer,Integer> attrTypeMap = new HashMap<>();
		if (secondaryAttrType1 > 0) {
			attrTypeMap.put(secondaryAttrType1,secondaryAttrValue1);
		}
		if (secondaryAttrType2 > 0) {
			attrTypeMap.put(secondaryAttrType2,secondaryAttrValue2);
		}
		if (secondaryAttrType3 > 0) {
			attrTypeMap.put(secondaryAttrType3,secondaryAttrValue3);
		}
		if (secondaryAttrType4 > 0) {
			attrTypeMap.put(secondaryAttrType4,secondaryAttrValue4);
		}
		 return attrTypeMap;
	}

	public void setAttr(int attrType, int attrValue) {
		if (secondaryAttrType1 == attrType) {
			this.setSecondaryAttrValue1(attrValue);
		} else if (secondaryAttrType2 == attrType) {
			this.setSecondaryAttrValue2(attrValue);
		} else if (secondaryAttrType3 == attrType) {
			this.setSecondaryAttrValue3(attrValue);
		} else if (secondaryAttrType4 == attrType) {
			this.setSecondaryAttrValue4(attrValue);
		}
	}

	/**
	 * 装备上嵌了宝石
	 * 
	 * @return
	 */
	public boolean isGemDressed() {
		return gem1 > 0 || gem2 > 0 || gem3 > 0 || gem4 > 0;
	}

	public int getStarLevel2() {
		return starLevel2;
	}

	public void setStarLevel2(int starLevel2) {
		this.starLevel2 = starLevel2;
		// 推送声望神器等级事件
		QuestEventBus.fireQuestEvent(QuestEventType.HOUNOR_GOD_EQUIP_LEVEL, starLevel2, GuaJiXID.valueOf(GsConst.ObjType.PLAYER, this.playerId));
	}

	public int getStarExp2() {
		return starExp2;
	}

	public void setStarExp2(int starExp2) {
		this.starExp2 = starExp2;
	}

	public int getGodlyAttrId2() {
		return godlyAttrId2;
	}

	public void setGodlyAttrId2(int godlyAttrId2) {
		this.godlyAttrId2 = godlyAttrId2;
	}

	// 检测装备是不是神器
	public boolean isGodly() {
		return godlyAttrId > 0 || godlyAttrId2 > 0;
	}

	/**
	 * 是否是普通神器;
	 * 
	 * @return
	 */
	public boolean isCommonGodly() {
		return godlyAttrId > 0;
	}

	/**
	 * 是否是声望神器;
	 * 
	 * @return
	 */
	public boolean isHounorGodly() {
		return godlyAttrId2 > 0;
	}

	public String getStrengthItemStr() {
		return strengthItemStr;
	}

	public void setStrengthItemStr(String strengthItemStr) {
		this.strengthItemStr = strengthItemStr;
	}

	public Map<Integer, ItemInfo> getStrengthItemMap() {
		return strengthItemMap;
	}

	public void setStrengthItemMap(Map<Integer, ItemInfo> strengthItemMap) {
		this.strengthItemMap = strengthItemMap;
	}

	/**
	 * 记录强化所需材料数量
	 * 
	 * @param itemId
	 * @param count
	 */
	public void addEquipItemCount(List<ItemInfo> infos) {
		for (ItemInfo item : infos) {
			if (strengthItemMap.containsKey(item.getItemId())) {
				ItemInfo itemInfo = strengthItemMap.get(item.getItemId());
				itemInfo.setQuantity(itemInfo.getQuantity() + item.getQuantity());
			} else {
				strengthItemMap.put(item.getItemId(), item);
			}
		}

		this.strengthItemStr = GsonUtil.getJsonInstance().toJson(this.strengthItemMap);
	}

	/**
	 * 组装返还材料对象
	 * 
	 * @return
	 */
	public List<ItemInfo> getEquipItemInfo() {
		List<ItemInfo> itemInfos = new ArrayList<>();
		for (ItemInfo item : this.strengthItemMap.values()) {
			itemInfos.add(item);
		}

		return itemInfos;
	}

	/**
	 * 清理强化材料
	 */
	public void clearStrengthItemInfo() {
		Log.logPrintln("clearStrngth:" + this.strengthItemStr + " id " +this.getId()+" PlayerId:" + this.getPlayerId());
		strengthItemMap.clear();
		this.strengthItemStr = GsonUtil.getJsonInstance().toJson(this.strengthItemMap);
	}

	/**
	 * 组装数据;
	 */
	public void convert() {
		this.strengthItemMap.clear();
		this.strengthItemMap = GsonUtil.getJsonInstance().fromJson(this.strengthItemStr, new TypeToken<Map<Integer, ItemInfo>>() {
		}.getType());
	}
	
	/**
	 * 清空装备神奇属性
	 * */
	public void clearGodlyAttr(){
		godlyAttrId = 0;
		starLevel = 0;
		starExp = 0;
		godlyAttrId2 = 0;
		starLevel2 = 0;
		starExp2 = 0;
	}
}
