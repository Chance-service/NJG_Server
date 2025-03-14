package com.guaji.game.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.guaji.config.ConfigManager;

import com.guaji.game.config.AllianceCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.PlayerAllianceEntity;

public class AllianceUtil {
	
	/**
	 * 公会等级, 最大经验
	 */
	public static HashMap<Integer, Integer> levelMap = new HashMap<Integer, Integer>();
	/**
	 * 公会最大等级
	 */
	public static int maxLevel = 0;

	/**
	 * 公会等级, 最大人数
	 */
	public static HashMap<Integer, Integer> popMap = new HashMap<Integer, Integer>();;
	
	/**
	 * 重加载公会等级对应的经验和人数
	 */
	public static void loadAllianceLevelCfg() {
		if (levelMap.size() == 0 || popMap.size() == 0) {
			Map<Object, AllianceCfg> allianceCfgMap = ConfigManager.getInstance().getConfigMap(AllianceCfg.class);
			for (AllianceCfg allianceCfg : allianceCfgMap.values()) {
				Integer v = levelMap.get(allianceCfg.getLevel());
				if ( v == null ) {
					levelMap.put(allianceCfg.getLevel(), allianceCfg.getExpEnd());
					popMap.put(allianceCfg.getLevel(), allianceCfg.getPopSize());
				} else {
					if (v < allianceCfg.getExpEnd()) {
						levelMap.put(allianceCfg.getLevel(), allianceCfg.getExpEnd());
						popMap.put(allianceCfg.getLevel(), allianceCfg.getPopSize());
					}
				}
			}
			for (Integer i : levelMap.keySet()) {
				if (i > maxLevel) {
					maxLevel = i;
				}
			}
		}
	}

	/**
	 * 等级数据清空
	 */
	public static void clearLevelmap() {
		levelMap.clear();
		popMap.clear();
	}
	
	/**
	 * 通过公会等级获取该等级对应的最大经验值
	 * @param allianceLevel
	 * @return
	 */
	public static int getAllianceMaxExp(int allianceLevel){
		loadAllianceLevelCfg();
		return levelMap.get(allianceLevel);
	}
	
	/**
	 * 通过公会等级获取该等级对应的最大人数
	 * @param allianceLevel
	 * @return
	 */
	public static int getAllianceMaxPop(int allianceLevel){
		loadAllianceLevelCfg();
		return popMap.get(allianceLevel);
	}

	/**
	 * 根据 公会等级 和 经验 获取公会配置信息
	 * @param level
	 * @param exp
	 * @return
	 */
	public static AllianceCfg getAllianceCfg(int level, int exp) {
		Map<Object, AllianceCfg> map = ConfigManager.getInstance().getConfigMap(AllianceCfg.class);
		for (AllianceCfg cfg : map.values()) {
			if (cfg.getLevel() == level && (exp >= cfg.getExpStart() && exp <= cfg.getExpEnd()))
				return cfg;
		}
		return null;
	}
	
	/**
	 * 计算开启公会boss需要的钻石
	 * @param sum
	 *            开启次数
	 * @return 开启boss需要消费钻石
	 */
	public static int openBossGold(int sum) {
		return (sum * (sum - 1) / 2 + 1) * 100;
	}
	
	/**
	 * 检测公会名称是否合法
	 * @param name
	 * @return
	 */
	public static boolean checkName(String name) {  
		String regEx = "^[0-9A-Za-z\u00ff-\uff9e]{2,6}$";  
		Pattern pat = Pattern.compile(regEx);  
		Matcher mat = pat.matcher(name);  
		return mat.find();     
    }
	
	/**
	 * 根据自身等级和公会等级计算签到所得金币
	 * @param playerLevel
	 * @param allianceLevel
	 * @return 获取签到金币
	 */
	public static int calcSignInCoin(int playerLevel, int allianceLevel){
		return (int) (Math.log10(allianceLevel*10) * playerLevel * 700);
	}
	
	/**
	 * 计算Boss血量百分比
	 * @param max
	 * @param current
	 * @return 
	 */
	public static int calcBossHPPercent(int max, int current) {
		float m = max, c = current;
		int rem = (int) ((float) (c / m) * 100);
		if (rem <= 0)
			return 0;
		return rem;
	}

	/**
	 * 获得公会最大等级
	 * @return
	 */
	public static int getAllianceMaxLevel(){
		return maxLevel;
	}
	/**
	 * 公会排行比较器
	 */
	public final static Comparator<AllianceEntity> SORTALLIANCE = new Comparator<AllianceEntity>() {
		@Override
		public int compare(AllianceEntity o1, AllianceEntity o2) {
			if(o1.getId() == o2.getId()) return 0;
			if (o1.getLevel() < o2.getLevel()) {
				return 1;
			} else if (o1.getLevel() == o2.getLevel()) {
				if (o1.getExp() == o2.getExp()) {
					if (o1.getId() > o2.getId())
						return 1;
					else 
						return -1;
				}
				if (o1.getExp() < o2.getExp()) {
					return 1;
				} else {
					return -1;
				}
			} else {
				return -1;
			}
		}
	};
	/**
	 * 公会排行比较器
	 */
	public final static Comparator<AllianceEntity> SORTALLIANCESCORE = new Comparator<AllianceEntity>() {
		@Override
		public int compare(AllianceEntity o1, AllianceEntity o2) {
			if(o1.getId() == o2.getId()) return 0;
			if (o1.getScoreValue() < o2.getScoreValue()) {
				return 1;
			} else if (o1.getScoreValue() == o2.getScoreValue()) {
				if (o1.getExp() == o2.getExp()) {
					if (o1.getId() > o2.getId())
						return 1;
					else 
						return -1;
				}
				if (o1.getExp() < o2.getExp()) {
					return 1;
				} else {
					return -1;
				}
			} else {
				return -1;
			}
		}
	};
	
	
	/**
	 * 公会元气比较器
	 */
	public final static Comparator<PlayerAllianceEntity> PLAYERALLIANCESORT = new Comparator<PlayerAllianceEntity>() {
		@Override
		public int compare(PlayerAllianceEntity o1, PlayerAllianceEntity o2) {
			 if (o1.getVitality() == o2.getVitality()) {
				 return 0;
			 }
			 if (o1.getVitality() > o2.getVitality()) {
				 return -1;
			 }else {
				 return 1;
			 }
		}
	};
}
