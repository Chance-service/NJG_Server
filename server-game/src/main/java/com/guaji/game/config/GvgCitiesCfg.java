package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.ItemInfo;
import com.guaji.game.util.ConfigUtil;

/**
 * GVG城池配置
 * 
 * @author Nannan.Gao
 * @date 2017-5-27 10:28:16
 */
@ConfigManager.XmlResource(file = "xml/gvgCity.xml", struct = "map")
public class GvgCitiesCfg extends ConfigBase {

	/**
	 * 配置城池唯一ID
	 */
	@Id
	private final int cityId;
	
	
	/**
	 *  城池名称
	 */
	private final String name;

	/**
	 * 城池等级
	 */
	private final int level;

	/**
	 * NPC配置
	 */
	private final String monsterIds;

	/**
	 * 宣战连通条件
	 */
	private final String chainIds;

	/**
	 * 高级城池宣战解锁条件
	 */
	private final String unlock;

	/**
	 * 贡品箱等级
	 */
	private final int boxLevel;

	/**
	 * 税收奖励
	 */
	private final String reward;
	
	
	/**
	 * 占领城池获得积分
	 */
	private final int obtainScore;

	/**
	 * 默认怪配置ID集合
	 */
	protected List<Integer> monsterIdList = new ArrayList<Integer>();

	/**
	 * 连通城池ID集合
	 */
	protected List<Integer> chainIdList = new ArrayList<Integer>();

	/**
	 * 高级城池宣战解锁条件
	 */
	protected UnlockLimit unlockLimit;

	/**
	 * 城池奖励数据
	 */
	protected List<ItemInfo> itemInfos;

	/**
	 * 赛季结束奖励
	 */
	protected List<ItemInfo> seasonItemInfos;

	public GvgCitiesCfg() {
		cityId = 0;
		level = 0;
		monsterIds = "";
		chainIds = "";
		unlock = "";
		boxLevel = 0;
		reward = "";
		unlockLimit = null;
		itemInfos = null;
		name = "";
		obtainScore=0;
	}

	@Override
	protected boolean assemble() {
		if (monsterIds != null && monsterIds.length() > 0 && !monsterIds.equals("")) {
			String[] ids = monsterIds.split(",");
			for (String monsterId : ids) {
				monsterIdList.add(Integer.valueOf(monsterId));
			}
		}

		if (chainIds != null && chainIds.length() > 0 && !chainIds.equals("")) {
			String[] ids = chainIds.split(",");
			for (String chainId : ids) {
				chainIdList.add(Integer.valueOf(chainId));
			}
		}

		if (unlock != null && unlock.length() > 0 && !unlock.equals("")) {
			String[] limits = this.unlock.split("_");
			if (limits == null || limits.length != 2) {
				return false;
			}
			// 解锁条件初始化
			int cityLevel = Integer.parseInt(limits[0]);
			int count = Integer.parseInt(limits[1]);
			unlockLimit = new UnlockLimit(cityLevel, count);
		}

		if (reward != null && reward.length() > 0 && !reward.equals("")) {
			itemInfos = new ArrayList<ItemInfo>();
			String[] rewards = reward.split(",");
			for (String _reward : rewards) {
				String[] strInfos = _reward.split("_");
				if (strInfos.length != 3) {
					return false;
				}
				ItemInfo itemInfo = new ItemInfo();
				itemInfo.setType(Integer.valueOf(strInfos[0]));
				itemInfo.setItemId(Integer.valueOf(strInfos[1]));
				itemInfo.setQuantity(Integer.valueOf(strInfos[2]));
				itemInfos.add(itemInfo);
			}
		}
		return super.assemble();
	}

	@Override
	protected boolean checkValid() {
		// 怪我检查
		for (Integer monsterId : monsterIdList) {
			if (!ConfigUtil.checkMonster(monsterId)) {
				return false;
			}
		}
		return true;
	}

	public static class UnlockLimit {

		/**
		 * 城池等级
		 */
		private int cityLevel;

		/**
		 * 城池数据
		 */
		private int count;

		public UnlockLimit(int cityLevel, int count) {
			this.cityLevel = cityLevel;
			this.count = count;
		}

		public int getCityLevel() {
			return cityLevel;
		}

		public int getCount() {
			return count;
		}

	}

	public int getCityId() {
		return cityId;
	}

	public int getLevel() {
		return level;
	}

	public String getMonsterIds() {
		return monsterIds;
	}

	public String getChainIds() {
		return chainIds;
	}

	public String getUnlock() {
		return unlock;
	}

	public int getBoxLevel() {
		return boxLevel;
	}

	public String getReward() {
		return reward;
	}

	/**
	 * 获取NPC配置ID集合
	 * 
	 * @return
	 */
	public List<Integer> getMonsterIdList() {
		return monsterIdList;
	}

	/**
	 * 获取连通城池ID集合
	 * 
	 * @return
	 */
	public List<Integer> getChainIdList() {
		return chainIdList;
	}

	/**
	 * 攻击城池条件判定
	 * 
	 * @return
	 */
	public UnlockLimit getUnlockLimit() {
		return unlockLimit;
	}

	/**
	 * 城池奖励数据
	 * 
	 * @return
	 */
	public List<ItemInfo> getItemInfos() {
		return itemInfos;
	}

	/**
	 * @return 赛季结束奖励
	 */
	public List<ItemInfo> getSeasonItemInfos() {
		return seasonItemInfos;
	}

	public String getName() {
		return name;
	}

	public int getObtainScore() {
		return obtainScore;
	}

}
