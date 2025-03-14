package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.util.MonsterItem;

@ConfigManager.XmlResource(file = "xml/SeasonTower194.xml", struct = "map")
public class SeasonTowerCfg extends ConfigBase {
	
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	/**
	 * 	下一個關卡
	 */
	private final int nextstage;
	/**
	 * 怪物隊伍對應
	 */
	private final String monsterid;	
	
	private List<MonsterItem> BossIDIdxList;
	/**
	 * 獎勵
	 */
	private final String reward;

	public SeasonTowerCfg() {
		this.id = 0;
		this.nextstage = 0;
		this.reward = "";
		this.monsterid = "";
		this.BossIDIdxList = new ArrayList<>();
	}

	public int getId() {
		return id;
	}
	
	public List<MonsterItem> getBossIDIdxList() {
		return BossIDIdxList;
	}

	public int getNextstage() {
		return nextstage;
	}

	public String getReward() {
		return reward;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		if (!this.monsterid.isEmpty()) {
			String[] bossids = this.monsterid.split(",");
			for (String str : bossids) {
				String[] sss = str.split("_");
				MonsterItem aItem = new MonsterItem(0,0.0f,0);
				if (sss.length == 3) {
					aItem.initByString(str);
				}
				
				BossIDIdxList.add(aItem);
			}
		} else {
			return false;
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
