package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/SecretAlbum.xml", struct = "map")
public class SecretAlbumCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	/**
	 * 對應英雄Id
	 */
	private final int itemId;
	/**
	 * 對應屬性 0.好感度 1.親密度 2.性感度
	 */
	private final int type;
	/**
	 * 對應英雄Id
	 */
	private final int score;
	/**
	 * 獎勵
	 */
	private final String reward;
	/**
	 * 付費獎勵
	 */
	private final String cost_reward;
	
	/**
	 * 各個屬性解鎖照片分數
	 * 
	 */
	private static Map<Integer,Map<Integer,List<SecretAlbumCfg>>> unlockMap = new HashMap<>(); // heroId,tyep,idlist
	
	public SecretAlbumCfg() {
		
		this.id = 0;
		this.itemId = 0;
		this.type = 0;
		this.score = 0;
		this.reward = "";
		this.cost_reward="";
	}
	
    @Override
    protected boolean assemble() {
    	
    	if (unlockMap.containsKey(getItemId())) {
    		if (unlockMap.get(getItemId()).containsKey(getType())) {
    			unlockMap.get(getItemId()).get(getType()).add(this);
    		} else {
        		List<SecretAlbumCfg> alist = new ArrayList<>();
        		alist.add(this);
    			unlockMap.get(getItemId()).put(getType(),alist);
    		}
    	} else {
    		List<SecretAlbumCfg> alist = new ArrayList<>();
    		alist.add(this);
    		Map<Integer,List<SecretAlbumCfg>> amp = new HashMap<>();
    		amp.put(getType(), alist);
    		unlockMap.put(getItemId(),amp);
    	}
            
        return true;
    }
    
    public int getId() {
    	return this.id;
    }

	public int getItemId() {
		return itemId;
	}

	public int getType() {
		return type;
	}

	public int getScore() {
		return score;
	}
	
	public String getReward() {
		return reward;
	}
	
	public String getCost_reward() {
		return cost_reward;
	}

	public static List<SecretAlbumCfg> getAlbumListByHeroId(int heroId){
		if (unlockMap.containsKey(heroId)) {
			return unlockMap.get(heroId).get(0); // 只取好感度相簿列表
		} else {
			return null;
		}
		
	}
	
	public static int getUnlockScore(int heroId,int cfgId) {
		List<SecretAlbumCfg> albumCfgList = SecretAlbumCfg.getAlbumListByHeroId(heroId);
		int totalScore = 0;
		for (SecretAlbumCfg sCfg : albumCfgList) {
			totalScore = totalScore + sCfg.getScore();
			if (sCfg.getId() == cfgId) {
				break;
			}
		}
		return totalScore ;
	}
        
}
