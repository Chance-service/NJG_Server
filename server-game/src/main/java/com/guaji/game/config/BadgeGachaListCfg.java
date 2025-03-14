package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;

import com.guaji.game.util.GsConst;

@ConfigManager.XmlResource(file = "xml/badgegachaList.xml", struct = "map")
public class BadgeGachaListCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	private final int ID;
	/**
	 * 對應badgeCfg skillpool
	 */
	private final int group;
	/**
	 * 權重
	 */
	private final int rate;
	/**
	 * 稀有度,特殊洗鍊用
	 */
	private final int rare;
	/**
	 * 種類
	 */
	private final int type;
	/**
	 * 對應生成skill
	 */
	private final int skill;
	
	private static Map<Integer,List<BadgeGachaListCfg>> assortMap;

	public BadgeGachaListCfg() {
		this.ID = 0;
		this.group = 0;
		this.rate = 0;
		this.rare = 0;
		this.type = 0;
		this.skill = 0;
		assortMap = new HashMap<>();
	}


	public int getID() {
		return ID;
	}


	public int getGroup() {
		return group;
	}


	public int getRate() {
		return rate;
	}


	public int getRare() {
		return rare;
	}


	public int getType() {
		return type;
	}


	public int getSkill() {
		return skill;
	}


	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		if (this.group > 0) {
			if (assortMap.containsKey(this.group)){
				assortMap.get(this.group).add(this);
			} else {
				List<BadgeGachaListCfg> alist = new ArrayList<>();
				alist.add(this);
				assortMap.put(this.group,alist);
			}
		}
		return true;
	}

	/**
	 * 检测有消息
	 * 
	 * @return
	 */
	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
	
	/**
	 * 符石一般洗鍊
	 * @param group
	 * @param slotNum
	 * @param lockSlot
	 * @param lockId
	 * @return
	 */
	public static List<Integer> getRandomBadage(int group, int slotNum,Map<Integer,Integer>lockSlot) {
		List <Integer> refineList = new ArrayList<>();
		BadgeGachaListCfg scfg = null;
		if (assortMap.containsKey(group)) {
			List<Integer> weightList = new ArrayList<>();
			List<BadgeGachaListCfg> itemList = new ArrayList<>();
			List<Integer> skipType = new ArrayList<>();
			int count = 0;
			for (int i = 0; i < slotNum ; i++) {
				count++;
				
				if (lockSlot != null) {
					if (lockSlot.containsKey(count) && (lockSlot.get(count)!= 0)) {
						int lockId = lockSlot.get(count);
						refineList.add(lockId);
						continue;
					}
				}
				weightList.clear();
				itemList.clear();
				skipType.clear();
				for (Integer Id: refineList) {
					int cfgId = (Id > GsConst.BADGE_LOCK_MASK)? (Id % GsConst.BADGE_LOCK_MASK) : Id;
					scfg = ConfigManager.getInstance().getConfigByKey(BadgeGachaListCfg.class, cfgId);
					skipType.add(scfg.type);
				}
				for (BadgeGachaListCfg acfg : assortMap.get(group)) {
					if ((skipType.size()> 0) && (skipType.contains(acfg.type))) { // type same skip
						continue;
					}
					weightList.add(acfg.rate);
					itemList.add(acfg);
				}
				BadgeGachaListCfg rcfg = GuaJiRand.randonWeightObject(itemList,weightList);
				if (rcfg != null) {
					refineList.add(rcfg.getID());
				}
			}
		}
		return refineList;
	}
	/**
	 *  指定(特殊洗鍊)
	 * @param group
	 * @param slot
	 * @return
	 */
	public static int getChoseRefine(int group, int rare , Set<Integer> typeSet) {
		if (assortMap.containsKey(group)) {
			List<Integer> weightList = new ArrayList<>();
			List<BadgeGachaListCfg> itemList = new ArrayList<>();
			for (BadgeGachaListCfg acfg : assortMap.get(group)) {
				if ((typeSet.contains(acfg.getType())) || (acfg.getRare() != rare)) {
					continue;
				}
				weightList.add(acfg.rate);
				itemList.add(acfg);
			}
			BadgeGachaListCfg rcfg = GuaJiRand.randonWeightObject(itemList,weightList);
			if (rcfg != null) {
				return rcfg.getID();
			}
		}
		return -1;
	}
	
}
