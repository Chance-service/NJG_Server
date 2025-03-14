package com.guaji.game.entity;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.msg.Msg;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;
import org.hibernate.annotations.GenericGenerator;

import com.guaji.game.GsApp;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.manager.FightValueRankManager;
import com.guaji.game.manager.RankManager;
import com.guaji.game.manager.RecordFirstManager;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const.RankType;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;

/**
 * 玩家基础数据
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "player")
public class PlayerEntity extends DBEntity implements Comparable<PlayerEntity> {
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id = 0;

	@Column(name = "puid", nullable = false)
	protected String puid = "";

	@Column(name = "serverId", nullable = false)
	protected int serverId = 0;

	@Column(name = "name", unique = true, nullable = false)
	private String name;
	
	@Column(name = "pwd", nullable = false)
	private String pwd;

	@Column(name = "rebirthStage")
	private int rebirthStage;

	@Column(name = "level")
	private int level;

	@Column(name = "exp")
	private long exp;

	@Column(name = "rmbGold")
	protected int rmbGold = 0;

	@Column(name = "gold")
	protected int gold = 0;

	@Column(name = "goldBean")
	private int goldBean = 0;// 金豆数量

	@Column(name = "exchangeGoldBeanCostrmbGold")
	private int exchangeGoldBeanCostRmbGold = 0;// 当天兑换金豆消耗钻石数量

	/** 当天充值额度 */
	@Column(name = "todayRechargeNum")
	private int todayRechargeNum;

	@Column(name = "coin")
	protected long coin = 0;

	@Column(name = "webRecharge")
	protected int webRecharge = 0;

	@Column(name = "gameRecharge")
	protected int gameRecharge = 0;
	
	@Column(name = "isguest")
	protected int isguest = 0;

	@Column(name = "recharge")
	protected int recharge = 0;

	@Column(name = "vipLevel")
	protected int vipLevel = 0;

	@Column(name = "smeltValue")
	private int smeltValue = 0;

	@Column(name = "honorValue")
	private int honorValue = 0;

	@Column(name = "fightValue")
	private int fightValue = 0;

	@Column(name = "prof")
	private int prof = 0;

	@Column(name = "reputationValue")
	private int reputationValue = 0;

	@Column(name = "skillEnhanceOpen")
	protected boolean skillEnhanceOpen;

	@Column(name = "questStep")
	protected int questStep;

	@Column(name = "signature")
	protected String signature = "";

	@Column(name = "device", nullable = false)
	protected String device = "";

	@Column(name = "platform", nullable = false)
	protected String platform = "";

	@Column(name = "phoneInfo", nullable = false)
	protected String phoneInfo = "";
	
	@Column(name = "heroDataStr", nullable = false)
	protected String heroDataStr = "";
	
	@Column(name = "headIconStr", nullable = false)
	protected String headIconStr = "";

	@Column(name = "forbidenTime")
	protected Date forbidenTime = null;

	@Column(name = "silentTime")
	protected Date silentTime = null;

	@Column(name = "loginTime")
	protected Date loginTime = null;

	@Column(name = "logoutTime")
	protected Date logoutTime = null;

	@Column(name = "resetTime")
	protected Date resetTime = null;

	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "langArea")
	private String langArea;

	@Column(name = "isComment")
	private String isComment;

	@Column(name = "googleAchieve")
	private String googleAchieve;

	@Column(name = "payMoney")
	private float payMoney;

	@Column(name = "rechargeSoul")
	private int rechargeSoul;

	@Column(name = "crystal")
	private int crystal;

	@Column(name = "loginDay")
	private int loginDay = 1;

	@Column(name = "invalid")
	protected boolean invalid;

	@Column(name = "avatarId")
	private int avatarId = 0;

	@Column(name = "headIcon")
	private int headIcon = 0;

	@Column(name = "buyIconList")
	private String buyIconList;
	
	@Column(name = "mergeTime", columnDefinition = "int default 0")
	private int mergeTime = 0;
	
	@Transient
	private List<String> iconList = new LinkedList<>();;

	public PlayerEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.loginTime = GuaJiTime.getCalendar().getTime();
	}

	public PlayerEntity(String puid, int serverId, String device, String platform, String phoneInfo, String pwd) {
		this.puid = puid;
		this.serverId = serverId;
		this.device = device;
		this.platform = platform;
		this.phoneInfo = phoneInfo;
		this.setName(puid);
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.loginTime = GuaJiTime.getCalendar().getTime();
		this.smeltValue = SysBasicCfg.getInstance().getEquipSmeltNum();
		this.pwd = pwd;
	}

	public int getId() {
		return id;
	}

	public int getGoldBean() {
		return goldBean;
	}

	public void setGoldBean(int goldBean) {
		this.goldBean = goldBean;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getExchangeGoldBeanCostRmbGold() {
		return exchangeGoldBeanCostRmbGold;
	}

	public void setExchangeGoldBeanCostRmbGold(int exchangeGoldBeanCostRmbGold) {
		this.exchangeGoldBeanCostRmbGold = exchangeGoldBeanCostRmbGold;
	}

	public boolean isSkillEnhanceOpen() {
		return skillEnhanceOpen;
	}

	public void setSkillEnhanceOpen(boolean skillEnhanceOpen) {
		this.skillEnhanceOpen = skillEnhanceOpen;
	}

	public String getPuid() {
		return puid;
	}

	public void setPuid(String puid) {
		this.puid = puid;
	}

	public int getServerId() {
		return this.serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	
	public String getpwd() {
		return this.pwd;
	}
	
	public void setpwd(String pwd) {
		this.pwd = pwd;
	}

	public int getTotalGold() {
		return gold + rmbGold;
	}

	public int getRmbGold() {
		return rmbGold;
	}

	public void setRmbGold(int rmbGold) {
		this.rmbGold = rmbGold;
	}

	public int getSysGold() {
		return gold;
	}

	public void setSysGold(int gold) {
		this.gold = gold;
	}

	public long getCoin() {
		return coin;
	}

	public void setCoin(long coin) {
		this.coin = coin;
	}

	public int getRecharge() {
		return recharge;
	}

	public void setRecharge(int recharge) {
		this.recharge = recharge;
	}

	public int getWebRecharge() {
		return webRecharge;
	}

	public void setWebRecharge(int webRecharge) {
		this.webRecharge = webRecharge;
	}

	public int getGameRecharge() {
		return gameRecharge;
	}

	public void setGameRecharge(int gameRecharge) {
		this.gameRecharge = gameRecharge;
	}
	
	public int getisguest() {
		return isguest;
	}

	public void setisguest(int isguest) {
		this.isguest = isguest;
	}

	public int getVipLevel() {
		return vipLevel;
	}

	public void setVipLevel(int vipLevel) {
		this.vipLevel = vipLevel;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public Date getLogoutTime() {
		return logoutTime;
	}

	public void setLogoutTime(Date logoutTime) {
		this.logoutTime = logoutTime;
	}

	public Date getResetTime() {
		return resetTime;
	}

	public void setResetTime(Date resetTime) {
		this.resetTime = resetTime;
	}

	public Date getForbidenTime() {
		return forbidenTime;
	}

	public void setForbidenTime(Date forbidenTime) {
		this.forbidenTime = forbidenTime;
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

	public int getSmeltValue() {
		return smeltValue;
	}

	public void setSmeltValue(int smeltValue) {
		this.smeltValue = smeltValue;
	}

	public String getPhoneInfo() {
		return phoneInfo;
	}

	public void setPhoneInfo(String phoneInfo) {
		this.phoneInfo = phoneInfo;
	}

	public Date getSilentTime() {
		return silentTime;
	}

	public void setSilentTime(Date silentTime) {
		this.silentTime = silentTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getExp() {
		return exp;
	}

	public void setExp(long exp) {
		this.exp = exp;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		if (level != this.level) {

			postRankChangeMsg(RankType.LEVEL_ALL_RANK, level, this.fightValue);
			
			postSvrChangeMsg(GsConst.SvrMissionType.Player_Level,this.getId(),level);

			switch (this.getProf()) {
			case GsConst.ProfType.WARRIOR:
				postRankChangeMsg(RankType.LEVEL_PROFJS_RANK, level, this.fightValue);
				break;
			case GsConst.ProfType.HUNTER:
				postRankChangeMsg(RankType.LEVEL_PROFGS_RANK, level, this.fightValue);
				break;
			case GsConst.ProfType.MASTER:
				postRankChangeMsg(RankType.LEVEL_PROFCS_RANK, level, this.fightValue);
				break;
			}

		}
		this.level = level;
	}

	public int getHonorValue() {
		return honorValue;
	}

	public void setHonorValue(int honorValue) {
		this.honorValue = honorValue;
	}

	public int getReputationValue() {
		return reputationValue;
	}

	public void setReputationValue(int reputationValue) {
		this.reputationValue = reputationValue;
	}

	public int getCrystalValue() {
		return crystal;
	}

	public void setCrystalValue(int crystal) {
		this.crystal = crystal;
	}

	public int getFightValue() {
		return fightValue;
	}

	public void setFightValue(int fightValue) {

		if (this.fightValue != fightValue) {
			postRankChangeMsg(RankType.SCORE_ALL_RANK, this.level, fightValue);

			switch (this.getProf()) {
			case GsConst.ProfType.WARRIOR:
				postRankChangeMsg(RankType.SCORE_PROFJS_RANK, this.level,fightValue);
				break;
			case GsConst.ProfType.HUNTER:
				postRankChangeMsg(RankType.SCORE_PROFGS_RANK, this.level,fightValue);
				break;
			case GsConst.ProfType.MASTER:
				postRankChangeMsg(RankType.SCORE_PROFCS_RANK, this.level,fightValue);
				break;
			}
			
			postSvrChangeMsg(GsConst.SvrMissionType.Player_Fight_Value,this.getId(),fightValue);

		}

		this.fightValue = fightValue;
		try {
			FightValueRankManager.getInstance().updateFightValueRankData(id, fightValue);
		} catch (Exception e) {
			MyException.catchException(e);
		}

	}

	public int getProf() {
		return prof;
	}

	public void setProf(int prof) {
		this.prof = prof;
	}

	@Override
	public void notifyUpdate(boolean async) {
		super.notifyUpdate(async);
	}

	public int getRebirthStage() {
		return rebirthStage;
	}

	public void setRebirthStage(int rebirthStage) {
		this.rebirthStage = rebirthStage;
	}

	public int getTodayRechargeNum() {
		return todayRechargeNum;
	}

	public void setTodayRechargeNum(int todayRechargeNum) {
		this.todayRechargeNum = todayRechargeNum;
	}

	@Override
	public int compareTo(PlayerEntity o) {
		if (o.getLevel() == this.getLevel()) {
			if (o.getExp() == this.getExp()) {
				return this.getId() - o.getId();
			}
			return (int) (o.getExp() - this.getExp());
		}
		return o.getLevel() - this.getLevel();
	}

	public String getLangArea() {
		return langArea;
	}

	public void setLangArea(String langArea) {
		this.langArea = langArea;
	}

	public String getIsComment() {
		return isComment;
	}

	public void setIsComment(String isComment) {
		this.isComment = isComment;
	}

	public String getGoogleAchieve() {
		return googleAchieve;
	}

	public void setGoogleAchieve(String googleAchieve) {
		this.googleAchieve = googleAchieve;
	}

	public float getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(float money) {
		this.payMoney = money;
		return;
	}

	public int getRechargeSoul() {
		return rechargeSoul;
	}

	public void setRechargeSoul(int soul) {
		this.rechargeSoul = soul;
		return;
	}

	public int getLoginDay() {
		return loginDay;
	}

	public void setLoginDay(int loginDay) {
		this.loginDay = loginDay;
	}

	public int getAvatarId() {
		return avatarId;
	}

	public void setAvatarId(int avatarId) {
		this.avatarId = avatarId;
	}

	public int getHeadIcon() {
		return headIcon;
	}

	public void setHeadIcon(int headIcon) {
		this.headIcon = headIcon;
	}
	
	public String getHeroDataString() {
		if (heroDataStr.equals("")) {
			return "0";
		}
		return heroDataStr;
	}
	
	public String getHeadIconString() {
		if(headIconStr.equals("")) {
			return "0";
		}
		return headIconStr;
	}
	
	public void setHeroDataString(String aStr) {
		heroDataStr = aStr;
	}
	
	public void setHeadIconString(String aStr) {
		headIconStr = aStr;
	}

	public String getBuyIconList() {
		return buyIconList;
	}

	public void setBuyIconList(String buyIconList) {
		this.buyIconList = buyIconList;
	}

	public List<String> getIconList() {
		return iconList;
	}

	public void addIcon(String strIcon) {
		this.iconList.add(strIcon);
		this.buyIconList = GameUtil.join(this.iconList, ",");

	}

	public void removeIcon(String strIcon) {
		this.iconList.remove(strIcon);
		this.buyIconList = GameUtil.join(this.iconList, ",");

	}

	public int getMergeTime() {
		return mergeTime;
	}

	public void setMergeTime(int mergeTime) {
		this.mergeTime = mergeTime;
	}

	public void setIconList(String[] strIconList) {
		this.iconList.clear();
		this.iconList.addAll(Arrays.asList(strIconList));
		this.buyIconList = GameUtil.join(this.iconList, ",");
	}

	/**
	 * params存储转化为内存List
	 */
	public void convertData() {

		if (buyIconList != null && !this.buyIconList.equals("")) {
			String[] paramsValue = buyIconList.split(",");
			this.iconList.clear();
			this.iconList.addAll(Arrays.asList(paramsValue));
		}
	}

	/**
	 * 各个品质到达10星后的特殊消息投递;
	 * 
	 * @param type
	 * @param usedTime
	 * @param playerLevel
	 */
	private void postRankChangeMsg(RankType type, int playerLevel, int playerScore) {
		Msg questMsg = Msg.valueOf(GsConst.MsgType.ON_RANK_CHANGE);
		questMsg.pushParam(type);
		questMsg.pushParam(String.format("%s,%s,%s", this.getId(), playerLevel, playerScore));
		GsApp.getInstance().postMsg(RankManager.getInstance().getXid(), questMsg);
	}
	/**
	 * 玩家戰力總值等級改變回報伺服器成就管理
	 * @param type
	 * @param playerId
	 * @param playerScore
	 */
	private void postSvrChangeMsg(int type,int playerId, int playerScore) {
		Msg questMsg = Msg.valueOf(GsConst.MsgType.ON_SERVER_MISSION_CHANGE);
		questMsg.pushParam(type);
		questMsg.pushParam(playerId);
		questMsg.pushParam(playerScore);
		GsApp.getInstance().postMsg(RecordFirstManager.getInstance().getXid(), questMsg);
	}
	
	/**
	 * 是否上線狀態
	 */
	public boolean Isonline() {
		Player player = PlayerUtil.queryPlayer(id);
		return (player != null);
	}
	
}
