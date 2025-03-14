package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.DropItems;

@ConfigManager.XmlResource(file = "xml/activityStage191_196.xml", struct = "map")
public class ActivityStage196Cfg extends ConfigBase {
	
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	//下一關卡
	private final int nextid;
	// 怪物id列表
	private final String monsterIds;
	private List<Integer> monsterIdList;
	// 掉落
	private final String dropItems;
	private DropItems items;
	
	private final int unlockTime;
	
	private final int replay;
	
	private static Map<Integer,List<ActivityStage196Cfg>> typeMapCfg;
	
	private static Set<Integer> allType;
	
	private static  Map<Integer,Integer> typeMaxStar;

	public ActivityStage196Cfg() {
		this.id = 0;
		this.nextid = 0;
		this.monsterIds = "";
		this.dropItems = "";
		this.unlockTime = 0;
		this.replay = 0;
		this.items = new DropItems();
		monsterIdList = new ArrayList<Integer>();
		allType = new HashSet<>();
		typeMaxStar = new HashMap<>();
		typeMapCfg = new HashMap<>();
	}

	public int getId() {
		return id;
	}
	
	public List<Integer> getMonsterIdList() {
		return monsterIdList;
	}

	public void setMonsterIdList(List<Integer> monsterIdList) {
		this.monsterIdList = monsterIdList;
	}

	public DropItems getItems() {
		return items;
	}

	public void setItems(DropItems items) {
		this.items = items;
	}

	public int getNextid() {
		return nextid;
	}

	public int getUnlockTime() {
		return unlockTime;
	}

	public int getReplay() {
		return replay;
	}
	
	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		if (monsterIds.equals("")) {
			return false;
		}

		for (String monsterIdStr : monsterIds.split(",")) {
			monsterIdList.add(Integer.valueOf(monsterIdStr));
		}

		if (!items.initByString(dropItems)) {
			items = new DropItems();
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
}
