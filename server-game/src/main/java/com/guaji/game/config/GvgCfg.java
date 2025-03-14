package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * GVG配置
 */
@ConfigManager.KVResource(file = "xml/gvg.cfg")
public class GvgCfg extends ConfigBase {

	private static GvgCfg instance = null;

	/**
	 * 玩家开放等级限定
	 */
	private final int openLevel;

	/**
	 * 功能开公会数量
	 */
	private final int allianceSize;

	/**
	 * 宣战开始时间
	 */
	private final long declareStart;

	/**
	 * 宣战结束时间
	 */
	private final long declareEnd;

	/**
	 * 征伐开始时间
	 */
	private final long battleStart;

	/**
	 * 征伐结束时间
	 */
	private final long battleEnd;

	/**
	 * 获得宣战权公会最大数量
	 */
	private final int declarePower;

	/**
	 * 宣战次数
	 */
	private final int declareTimes;

	/**
	 * 初始化城市个数
	 */
	private final int allocatingNumber;

	/**
	 * 元气排行记录条数
	 */
	private final int rankNumber;

	/**
	 * 最多派出角色数
	 */
	private final int roleMax;

	/**
	 * 战斗最长时间(单位:秒)
	 */
	private final int battleTime;

	/**
	 * 佣兵初始行动力
	 */
	private final int rolePower;

	/**
	 * 佣兵防御消耗行动力
	 */
	private final int defendPower;

	/**
	 * 佣兵攻击消耗行动力
	 */
	private final int attackPower;

	/**
	 * 宣战播报
	 */
	private final String declareBattle;

	/**
	 * 反攻宣战播报
	 */
	private final String declareFightback;

	/**
	 * 向NPC宣战播报
	 */
	private final String declareBattleNpc;

	/**
	 * 攻城方获胜
	 */
	private final String attackerWin;

	/**
	 * 反攻获胜
	 */
	private final String fightbackWin;

	/**
	 * log记录条数
	 */
	private final int gvgLogTotal;

	/**
	 * log记录天数
	 */
	private final long gvgLogDay;

	/**
	 * 都城ID
	 */
	private final int cityId;

	/**
	 * 都城可以直接向特定x级城池宣战
	 */
	private final int cityLevel;

	/**
	 * 奖励的聊天框ID
	 */
	private final int skinId;

	/**
	 * 反攻保护时间
	 */
	private final int fightbackTime;

	/**
	 * 派兵攻方播报
	 */
	private final String sendRoleAttacker;

	/**
	 * 派兵防守方播报
	 */
	private final String sendRoleDefender;

	/**
	 * 攻方为0攻方播报
	 */
	private final String zeroAttacker;

	/**
	 * 攻方为0防守方播报
	 */
	private final String zeroDefender;

	/**
	 * 复活需要消耗的声望
	 */
	private final int needVitality;

	/**
	 * 月份中第几天可重置
	 */
	private final String resetDay;

	/**
	 * 可操作复活的开始时间点
	 */
	private final long reviveStart;

	/**
	 * 生成入围名单的时间点
	 */
	private final long resetTime;
	
	/**
	 * 赛季结束公告标签
	 */
	private final String gvgSeasonEnd;
	
	/**
	 * 赛季播报的名次
	 */
	private final int gvgBroadRank;
	
	
	private final String attackCityNotice;
	

	/**
	 * 月份中第几天可重置
	 */
	private List<Integer> resetDayList = new ArrayList<Integer>();

	public GvgCfg() {
		openLevel = 12;
		allianceSize = 15;
		declareStart = 32400000L;
		declareEnd = 64800000L;
		battleStart = 75600000L;
		battleEnd = 79200000L;
		declarePower = 6;
		declareTimes = 2;
		allocatingNumber = 15;
		rankNumber = 32;
		roleMax = 3;
		battleTime = 30000;
		rolePower = 2;
		defendPower = 1;
		attackPower = 1;
		declareBattle = "@declareBattle";
		declareFightback = "@declareFightback";
		declareBattleNpc = "@declareBattleNpc";
		attackerWin = "@attackerWin";
		fightbackWin = "@fightbackWin";
		gvgLogTotal = 50;
		gvgLogDay = 604800000L;
		cityId = 21;
		cityLevel = 2;
		skinId = 1002;
		fightbackTime = 900000;
		sendRoleAttacker = "@sendRoleAttacker";
		sendRoleDefender = "@sendRoleDefender";
		zeroAttacker = "@zeroAttacker";
		zeroDefender = "@zeroDefender";
		needVitality = 40;
		resetDay = "15,30";
		reviveStart = 79200000L;
		resetTime = 14400000;
		gvgSeasonEnd="@gvgSeasonEnd";
		gvgBroadRank=3;
		attackCityNotice="@attackCityNotice";
		instance = this;
	}

	public static GvgCfg getInstance() {
		return instance;
	}

	@Override
	protected boolean assemble() {
		String[] resetDays = resetDay.split(",");
		for (String day : resetDays) {
			resetDayList.add(Integer.parseInt(day));
		}
		return true;
	}

	@Override
	protected boolean checkValid() {
		// 城池检查
		GvgCitiesCfg cityConfig = ConfigManager.getInstance().getConfigByKey(GvgCitiesCfg.class, this.cityId);
		if (cityConfig == null) {
			return false;
		}
		return true;
	}

	public int getOpenLevel() {
		return openLevel;
	}

	public int getAllianceSize() {
		return allianceSize;
	}

	public long getDeclareStart() {
		return declareStart;
	}

	public long getDeclareEnd() {
		return declareEnd;
	}

	public long getBattleStart() {
		return battleStart;
	}

	public long getBattleEnd() {
		return battleEnd;
	}

	public int getDeclarePower() {
		return declarePower;
	}

	public int getDeclareTimes() {
		return declareTimes;
	}

	public int getAllocatingNumber() {
		return allocatingNumber;
	}

	public int getRankNumber() {
		return rankNumber;
	}

	public int getRoleMax() {
		return roleMax;
	}

	public int getBattleTime() {
		return battleTime;
	}

	public int getRolePower() {
		return rolePower;
	}

	public int getDefendPower() {
		return defendPower;
	}

	public int getAttackPower() {
		return attackPower;
	}

	public String getDeclareBattle() {
		return declareBattle;
	}

	public String getDeclareFightback() {
		return declareFightback;
	}

	public String getDeclareBattleNpc() {
		return declareBattleNpc;
	}

	public String getAttackerWin() {
		return attackerWin;
	}

	public String getFightbackWin() {
		return fightbackWin;
	}

	public int getGvgLogTotal() {
		return gvgLogTotal;
	}

	public long getGvgLogDay() {
		return gvgLogDay;
	}

	public int getCityId() {
		return cityId;
	}

	public int getCityLevel() {
		return cityLevel;
	}

	public int getSkinId() {
		return skinId;
	}

	public int getFightbackTime() {
		return fightbackTime;
	}

	public String getSendRoleAttacker() {
		return sendRoleAttacker;
	}

	public String getSendRoleDefender() {
		return sendRoleDefender;
	}

	public String getZeroAttacker() {
		return zeroAttacker;
	}

	public String getZeroDefender() {
		return zeroDefender;
	}

	public int getNeedVitality() {
		return needVitality;
	}

	public List<Integer> getResetDayList() {
		return resetDayList;
	}

	public long getReviveStart() {
		return reviveStart;
	}

	public long getResetTime() {
		return resetTime;
	}

	public String getGvgSeasonEnd() {
		return gvgSeasonEnd;
	}

	public int getGvgBroadRank() {
		return gvgBroadRank;
	}

	public String getAttackCityNotice() {
		return attackCityNotice;
	}
	
	

}
