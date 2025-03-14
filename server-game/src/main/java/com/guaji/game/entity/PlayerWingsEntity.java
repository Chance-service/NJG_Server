package com.guaji.game.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.msg.Msg;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.guaji.game.GsApp;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.manager.ChatManager;
import com.guaji.game.manager.RankManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.RankType;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;

/**
 * 玩家翅膀实体;
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "player_wing")
public class PlayerWingsEntity extends DBEntity {
	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
	private int id;

	/** 玩家id */
	@Column(name = "playerId", nullable = false)
	private int playerId;

	/** 翅膀等级 */
	@Column(name = "level", nullable = false)
	private int level;

	/** 开始升某一品质时间 */
	@Column(name = "starTime", nullable = false)
	private long starTime;

	/** 升级到白色10星花费时间 */
	@Column(name = "whiteTime", nullable = false)
	private long whiteTime;

	/** 升到绿色10星消耗时间 */
	@Column(name = "greenTime", nullable = false)
	private long greenTime;

	/** 升到蓝色10星消耗时间 */
	@Column(name = "blueTime", nullable = false)
	private long blueTime;

	/** 升到紫色10星消耗时间 */
	@Column(name = "purpleTime", nullable = false)
	private long purpleTime;

	/** 升到橙色10星消耗时间 */
	@Column(name = "orangeTime", nullable = false)
	private long orangeTime;

	/** 幸运值 */
	@Column(name = "luckyNum", nullable = false)
	private int luckyNum;

	@Column(name = "createTime", nullable = false)
	protected Date createTime = null;

	@Column(name = "updateTime")
	protected Date updateTime;

	@Column(name = "invalid")
	protected boolean invalid;

	@Transient
	private static final int LEVEL_1 = 1;
	@Transient
	private static final int LEVEL_10 = 10;
	@Transient
	private static final int LEVEL_20 = 20;
	@Transient
	private static final int LEVEL_30 = 30;
	@Transient
	private static final int LEVEL_40 = 40;
	@Transient
	private static final int LEVEL_50 = 50;

	public PlayerWingsEntity() {

	}

	public PlayerWingsEntity(int playerId) {
		this.level = 0;
		this.playerId = playerId;
		this.starTime = 0L;
		this.whiteTime = 0L;
		this.greenTime = 0L;
		this.blueTime = 0L;
		this.purpleTime = 0L;
		this.orangeTime = 0L;
		this.luckyNum = 0;
		this.createTime = GuaJiTime.getTimestamp();
	}

	public int getId() {
		return id;
	}

	public int getLevel() {
		return level;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public long getWhiteTime() {
		return whiteTime;
	}

	public long getGreenTime() {
		return greenTime;
	}

	public long getBlueTime() {
		return blueTime;
	}

	public long getPurpleTime() {
		return purpleTime;
	}

	public long getOrangeTime() {
		return orangeTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public long getStarTime() {
		return starTime;
	}

	public void setStarTime(long starTime) {
		this.starTime = starTime;
	}

	public int getLuckyNum() {
		return luckyNum;
	}

	public void setLuckyNum(int luckyNum) {
		this.luckyNum = luckyNum;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setWhiteTime(long whiteTime) {
		this.whiteTime = whiteTime;
	}

	public void setGreenTime(long greenTime) {
		this.greenTime = greenTime;
	}

	public void setBlueTime(long blueTime) {
		this.blueTime = blueTime;
	}

	public void setPurpleTime(long purpleTime) {
		this.purpleTime = purpleTime;
	}

	public void setOrangeTime(long orangeTime) {
		this.orangeTime = orangeTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	/**
	 * 翅膀升级处理, 记录某一品质开始升级时间, 依赖策划配置表, 品质不可以下降!
	 * <p>
	 * 若达到品质10星, 则将前一品质10星时间抛给RankManager;
	 * </p>
	 * 
	 * @param playerName
	 * @param allianceName
	 */
	public void levelup(int playerLevel) {
		this.level += 1;
		if ((level == LEVEL_1 && starTime == 0L) || (level == LEVEL_10 + 1 && greenTime == 0) || (level == LEVEL_20 + 1 && blueTime == 0)
				|| (level == LEVEL_30 + 1 && purpleTime == 0) || (level == LEVEL_40 + 1 && orangeTime == 0)) {
			starTime = GuaJiTime.getMillisecond();
		}
		if (level == LEVEL_10 && whiteTime == 0L) {
			whiteTime = GuaJiTime.getMillisecond() - starTime;
			// 防止作死的QA乱调时间;
			if (whiteTime < 0) {
				whiteTime = LEVEL_10;
			}
			postRankChangeMsg(RankType.WING_WHITE_TIME_RANK, whiteTime, playerLevel);
		} else if (level == LEVEL_20 && greenTime == 0L) {
			greenTime = GuaJiTime.getMillisecond() - starTime;
			if (greenTime < 0) {
				greenTime = LEVEL_20;
			}
			postRankChangeMsg(RankType.WING_GREEN_TIME_RANK, greenTime, playerLevel);
		} else if (level == LEVEL_30 && blueTime == 0L) {
			blueTime = GuaJiTime.getMillisecond() - starTime;
			if (blueTime < 0) {
				blueTime = LEVEL_30;
			}
			postRankChangeMsg(RankType.WING_BLUE_TIME_RANK, blueTime, playerLevel);
		} else if (level == LEVEL_40 && purpleTime == 0L) {
			purpleTime = GuaJiTime.getMillisecond() - starTime;
			if (purpleTime < 0) {
				purpleTime = LEVEL_40;
			}
			postRankChangeMsg(RankType.WING_PURPLE_TIME_RANK, purpleTime, playerLevel);
		} else if (level == LEVEL_50 && orangeTime == 0L) {
			orangeTime = GuaJiTime.getMillisecond() - starTime;
			if (orangeTime < 0) {
				orangeTime = LEVEL_50;
			}
			postRankChangeMsg(RankType.WING_ORANGE_TIME_RANK, orangeTime, playerLevel);
		}
	}

	/**
	 * 各个品质到达10星后的特殊消息投递;
	 * 
	 * @param type
	 * @param usedTime
	 * @param playerLevel
	 */
	private void postRankChangeMsg(RankType type, Long usedTime, int playerLevel) {
		Msg questMsg = Msg.valueOf(GsConst.MsgType.ON_RANK_CHANGE);
		questMsg.pushParam(type);
		questMsg.pushParam(String.format("%s,%s,%s", playerId, playerLevel, usedTime));
		GsApp.getInstance().postMsg(RankManager.getInstance().getXid(), questMsg);

		PlayerSnapshotInfo.Builder snapshotInfo = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
		if (snapshotInfo != null && snapshotInfo.getMainRoleInfo() != null) {
			RoleInfo mainRoleInfo = snapshotInfo.getMainRoleInfo();
			String chat = ChatManager.getMsgJson(SysBasicCfg.getInstance().getWingTenStarBroadcast(), mainRoleInfo.getName(), getQualityName(type.getNumber()));
			GsApp.getInstance().broadcastChatWorldMsg(chat, chat);
		}
	}

	/**
	 * 获得各个品质的名称;
	 * 
	 * @param number
	 * @return
	 */
	private String getQualityName(int number) {
		switch (number) {
		case 1:
			return GsConst.Wings.WHITE;
		case 2:
			return GsConst.Wings.GREEN;
		case 3:
			return GsConst.Wings.BLUE;
		case 4:
			return GsConst.Wings.PURPLE;
		case 5:
			return GsConst.Wings.ORANGE;
		default:
			return "";
		}
	}

	/**
	 * 等级下降,不会降品质;
	 */
	public void leveldown() {
		if (level == LEVEL_10 + 1 || level == LEVEL_20 + 1 || level == LEVEL_30 + 1 || level == LEVEL_40 + 1) {
			return;
		}
		this.level -= 1;
	}

	public void resetLuckyNum() {
		this.luckyNum = 0;
	}

	public void addLuckNum(int luckyNum2) {
		luckyNum += luckyNum2;
		if (luckyNum > Const.WingsConstant.MAX_LUCKY_NUM_VALUE) {
			luckyNum = Const.WingsConstant.MAX_LUCKY_NUM_VALUE;
		}
	}

}
