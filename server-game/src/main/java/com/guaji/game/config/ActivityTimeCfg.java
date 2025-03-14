package com.guaji.game.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;

@ConfigManager.XmlResource(file = "xml/activityTime.xml", struct = "list")
public class ActivityTimeCfg extends ConfigBase implements Comparable<ActivityTimeCfg> {

	/** 配置id */
	@Id
	private final int id;

	/** 对应的活动Id */
	private final int activityId;

	/** 第几期活动（-1 表示不按期开放） */
	private final int stageId;

	/** 活动开始时间 */
	private final String startTime;

	/** 活动截至时间 */
	private final String endTime;

	/** 活动关闭时间（前端看不到） */
	private final String closeTime;

	/** 开启时间 */
	private long lStartTime = 0;

	/** 结束时间 */
	private long lEndTime = 0;

	/** 失效时间 */
	private long lCloseTime = 0;

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

	public ActivityTimeCfg() {
		this.id = 0;
		this.activityId = 0;
		this.stageId = 0;
		this.startTime = "";
		this.endTime = "";
		this.closeTime = "";
	}

	public int getId() {
		return id;
	}

	public int getActivityId() {
		return activityId;
	}

	public int getStageId() {
		return stageId;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public String getCloseTime() {
		return closeTime;
	}

	public long getlStartTime() {
		return lStartTime;
	}

	public void setlStartTime(long lStartTime) {
		this.lStartTime = lStartTime;
	}

	public long getlEndTime() {
		return lEndTime;
	}

	public void setlEndTime(long lEndTime) {
		this.lEndTime = lEndTime;
	}

	public long getlCloseTime() {
		return lCloseTime;
	}

	public void setlCloseTime(long lCloseTime) {
		this.lCloseTime = lCloseTime;
	}

	@Override
	protected boolean assemble() {

		if (this.stageId > 0) {
			try {
				this.lStartTime = DATE_FORMAT.parse(this.startTime).getTime();
				this.lEndTime = DATE_FORMAT.parse(this.endTime).getTime();
				this.lCloseTime = DATE_FORMAT.parse(this.closeTime).getTime();
			} catch (ParseException e) {
				MyException.catchException(e);
			}
		}
		return true;
	}

	@Override
	protected boolean checkValid() {

		List<ActivityTimeCfg> allTimeCfgs = ConfigManager.getInstance().getConfigList(ActivityTimeCfg.class);
		for (ActivityTimeCfg cfg : allTimeCfgs) {
			if (cfg.getActivityId() == this.activityId && cfg.getStageId() == this.stageId && cfg.getId() != this.id) {
				throw new RuntimeException("same stageId of one activity in activityTimeCfg, ActivityTimeCfg id : " + this.id);
			}
		}

		boolean result = this.lCloseTime >= this.lEndTime && this.lEndTime > this.lStartTime;
		if (this.stageId > 0 && !result) {
			throw new RuntimeException("activityTime must be closeTime >= endTime > startTime, ActivityTimeCfg id : " + this.id);
		}
		return true;
	}

	/**
	 * 活动是否截止
	 * 
	 * @return
	 */
	public boolean isEnd() {

		if (stageId < 0) {
			return false;
		}

		long currentTime = GuaJiTime.getMillisecond();
		if (currentTime > this.lEndTime) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 活动是否激活，激活指活动没有close，只要活动是激活状态前端就会显示
	 * 
	 * @return
	 */
	public boolean isActive() {
		if (stageId < 0) {
			return true;
		}

		long currentTime = GuaJiTime.getMillisecond();
		if (currentTime >= this.lStartTime && currentTime <= this.lCloseTime) {
			return true;
		}
		return false;
	}

	/**
	 * 活动是否激活，激活指活动没有end，只要活动是激活状态前端就会显示
	 * 
	 * @return
	 */
	public boolean isActiveToEnd() {
		if (stageId < 0) {
			return true;
		}

		long currentTime = GuaJiTime.getMillisecond();
		if (currentTime >= this.lStartTime && currentTime <= this.lEndTime) {
			return true;
		}
		return false;
	}

	/**
	 * 计算活动剩余时间
	 * 
	 * @return
	 */
	public int calcActivitySurplusTime() {
		long currentTime = GuaJiTime.getMillisecond();
		int surplusTime = (int) ((this.lEndTime - currentTime) / 1000);
		return Math.max(surplusTime, 0);
	}

	/**
	 * 计算活动面板关闭剩余时间
	 * 
	 * @return
	 */
	public int calcActivityCloseTime() {
		long currentTime = GuaJiTime.getMillisecond();
		int surplusTime = (int) ((this.lCloseTime - currentTime) / 1000);
		return Math.max(surplusTime, 0);
	}

	@Override
	public int compareTo(ActivityTimeCfg o) {
		return this.id - o.getId();
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
