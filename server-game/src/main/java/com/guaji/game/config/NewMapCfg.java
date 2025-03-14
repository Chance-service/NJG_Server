package com.guaji.game.config;

import java.util.LinkedList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.DropItems;
import com.guaji.game.util.ConfigUtil;

@ConfigManager.XmlResource(file = "xml/map_New.xml", struct = "map")
public class NewMapCfg extends ConfigBase{
	/**
	 * 最小地图id
	 */
	private static int minMapId = 0;
	/**
	 * 最大地图id
	 */
	private static int maxMapId = 0;
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	/**
	 * bossid
	 */
	protected final String BossID;
	/**
	 * 下个关卡地图id
	 */
	protected final int NextID;
	/**
	 * BOSS關卡
	 */
	protected final int IsBoss;
	/**
	 * 掉落金幣
	 */
	protected final float SkyCoin;
	/**
	 * 每秒物品掉落經驗值罐參數
	 */
	protected final float Potion;
	/**
	 * 每小時物品掉落突破石
	 */
	protected final int stone;
	/**
	 * 普通掉落组
	 */
	protected final String DropItems;
	/**
	 * boss掉落组
	 */
	protected final String BossDrop;
	/**
	 * 經驗值
	 */
	protected final float EXP;
	/**
	 * 解鎖等級
	 */
	protected final int Unlock;
	/**
	 * BossID團,包含Boss戰儸儸小怪
	 */
	protected List<Integer> BossIDIdxList;
	/**
	 * 一般掉落物索引編號,對照掛機時間表
	 */
	protected List<Integer> DropItemIdxList;
	/**
	 * Boss掉落物件
	 */
	protected DropItems BossDropItems;

	
	public NewMapCfg() {
		id = 0;
		BossID = null;
		NextID = 0;
		IsBoss =0;
		SkyCoin = 0.0f;
		Potion = 0.0f;
		DropItems = null;
		BossDrop = null;
		EXP = 0.0f;
		Unlock = 0;
		BossIDIdxList= new LinkedList<Integer>();
		DropItemIdxList = new LinkedList<Integer>();
		BossDropItems = new DropItems();
		stone = 0;
	}
	
	public int getId() {
		return id;
	}
	
	public int getNextMapId() {
		return NextID;
	}
	
	public float getSkyCoin() {
		return SkyCoin;
	}
	
	public float getPotion() {
		return Potion;
	}
	
	public float getExp() {
		return EXP;
	}
	
	public int getUnlock() {
		return Unlock;
	}
	
	public static int getMinMapId() {
		return minMapId;
	}

	public static int getMaxMapId() {
		return maxMapId;
	}
	
	public List<Integer> getDropItemIdxList() {
		return DropItemIdxList;
	}
	
	
	public List<Integer> getBossIDIdxList() {
		return BossIDIdxList;
	}
	
	public DropItems getBossDropItems() {
		return BossDropItems;
	}
	
	public boolean BossClass() {
		return (IsBoss == 1);
	}
	
	public int getStone() {
		return stone;
	}

	@Override
	protected boolean assemble() {
		BossIDIdxList.clear();
		DropItemIdxList.clear();
		BossDropItems.clearItems();
		
		if (minMapId == 0 || id < minMapId) {
			minMapId = id;
		}
		
		if (maxMapId == 0 || id > maxMapId) {
				maxMapId = id;
		}
		
		if (!BossDropItems.initByString(BossDrop)) {
			return false;
		}
		
		// BossID和小怪ID拆分
		if (BossID != null && BossID.length() > 0) {
			String[] bossids = BossID.split(",");
			for (String id : bossids) {
				BossIDIdxList.add(Integer.valueOf(id));
			}
		}
		
		// 掉落物索引拆分,用來查詢掉落條件
		if (DropItems != null && DropItems.length() > 0) {
			String[] ItemIdx = DropItems.split(",");
			for (String idx : ItemIdx) {
				DropItemIdxList.add(Integer.valueOf(idx));
			}
		}
		
		return true;
	}
	
	@Override
	protected boolean checkValid() {
		
		if (BossDropItems != null) {
			for (DropItems.Item item : BossDropItems.getDropItems()) {
				if (!ConfigUtil.checkDropItem(item)) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
