package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/SingleBoss.xml", struct = "map")
public class SingleBossCfg extends ConfigBase {
	
	/**
	 * 配置id(對應怪的等級)
	 */
	@Id
	private final int id;
	
	/**
	 * 怪物隊伍對應
	 */
	private final String monsterid;	
	
	private List<Integer> BossIDIdxList;
	
	private final float rate;
	/**
	 * 血量獎勵分級
	 */
	private final String stagepoint;
	
	private List<Integer> bloodList;
	/**
	 * 血量獎勵
	 */
	private final String stagereward1;
	private final String stagereward2;
	private final String stagereward3;
	private final String stagereward4;
	private final String stagereward5;
	private final String stagereward6;
	private final String stagereward7;
	private final String stagereward8;
	private final String stagereward9;
	private final String stagereward10;
	
	private List<String> rewardList;

	public SingleBossCfg() {
		this.id = 0;
		this.monsterid = "";
		this.rate = 0.0f;
		this.stagepoint = "";
		this.stagereward1 = "";
		this.stagereward2 = "";
		this.stagereward3= "";
		this.stagereward4= "";
		this.stagereward5= "";
		this.stagereward6= "";
		this.stagereward7= "";
		this.stagereward8= "";
		this.stagereward9= "";
		this.stagereward10= "";
		this.BossIDIdxList= new ArrayList<>();
		this.bloodList = new ArrayList<>();
		this.rewardList = new ArrayList<>();
	}

	public int getId() {
		return id;
	}
	
	public float getRate() {
		return rate;
	}

	public List<Integer> getBossIDIdxList() {
		return BossIDIdxList;
	}

	public void setBossIDIdxList(List<Integer> bossIDIdxList) {
		BossIDIdxList = bossIDIdxList;
	}

	public List<Integer> getBloodList() {
		return bloodList;
	}

	public void setBloodList(List<Integer> bloodList) {
		this.bloodList = bloodList;
	}

	public List<String> getRewardList() {
		return rewardList;
	}

	public void setRewardList(List<String> rewardList) {
		this.rewardList = rewardList;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		this.bloodList.clear();
		if (!stagepoint.isEmpty()) {
			String[] Sks = stagepoint.split(",");
			for (String str : Sks) {
				bloodList.add(Integer.valueOf(str.trim()));
			}
		} else {
			return false;
		}
		
		
		if (!this.monsterid.isEmpty()) {
			String[] bossids = this.monsterid.split(",");
			for (String id : bossids) {
				BossIDIdxList.add(Integer.valueOf(id));
			}
		} else {
			return false;
		}
		
		this.rewardList.clear();
		this.rewardList.add(this.stagereward1);
		this.rewardList.add(this.stagereward2);
		this.rewardList.add(this.stagereward3);
		this.rewardList.add(this.stagereward4);
		this.rewardList.add(this.stagereward5);
		this.rewardList.add(this.stagereward6);
		this.rewardList.add(this.stagereward7);
		this.rewardList.add(this.stagereward8);
		this.rewardList.add(this.stagereward9);
		this.rewardList.add(this.stagereward10);
		
		if (this.bloodList.size() > this.rewardList.size()) {
			// 獎勵血量超出要給的獎勵
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
