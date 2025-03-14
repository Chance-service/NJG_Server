package com.guaji.game.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;

@ConfigManager.XmlResource(file = "xml/TimeGiftConfig2.xml", struct = "map")
public class TimeGiftCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	private final int rechargeid;
	/** 活动开始时间 */
	private final String startTime;

	/** 活动截至时间 */
	private final String endTime;
	/** 开启时间 */
	private long lStartTime = 0;

	/** 结束时间 */
	private long lEndTime = 0;
	/**
	 * 可購買數量
	 */
	private final int count;
	/**
	 * 標記ID
	 */
	private final int signid;
	/**
	 * 獎項
	 */
	private final String awards;
	
	private final int usetime ;
	
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

	public TimeGiftCfg() {
		this.rechargeid = 0;
		this.startTime ="";
		this.endTime ="";
		this.count = 0;
		this.awards = "";
		this.lStartTime = 0;
		this.lEndTime = 0;
		this.usetime = 0;
		this.signid = 0;
	}

	public int getRechargeid() {
		return rechargeid;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
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

	public int getCount() {
		return count;
	}

	public String getAwards() {
		return awards;
	}
	
	public int getUseTime() {
		return usetime;
	}
	
	public int getSignid() {
		return signid;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		if (!this.getStartTime().isEmpty() && !this.getEndTime().isEmpty()) {
			try {
				this.lStartTime = DATE_FORMAT.parse(this.startTime).getTime();
				this.lEndTime = DATE_FORMAT.parse(this.endTime).getTime();
			} catch (ParseException e) {
				MyException.catchException(e);
			}
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

		boolean result = this.lEndTime > this.lStartTime;
		if (lStartTime > 0 && !result) {
			throw new RuntimeException("TimeGiftConfig2.xml must be endTime > startTime, TimeCfg rechargeid : " + this.rechargeid);
		}
		return true;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
	
	/**
	 * 禮包是否啟動狀態
	 * @return
	 */
	public boolean isActive() {
		
		if ((this.lStartTime > 0) && (this.lEndTime > 0))
		{
			long currentTime = GuaJiTime.getMillisecond();
			if (currentTime >= this.lStartTime && currentTime < this.lEndTime) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 计算活动剩余时间
	 * 
	 * @return
	 */
	public int calcSurplusTime() {
		long currentTime = GuaJiTime.getMillisecond();
		int surplusTime = (int) ((this.lEndTime - currentTime) / 1000);
		return Math.max(surplusTime, 0);
	}
}
