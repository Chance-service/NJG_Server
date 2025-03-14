package com.guaji.game.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;

@ConfigManager.XmlResource(file = "xml/RechargeBounceTimeIndex.xml", struct = "map")
public class RechargeBounceTimeIndexCfg extends ConfigBase {
	
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	
	/** 活动开始时间 */
	private final String startTime;

	/** 活动截至时间 */
	private final String endTime;
	
	/** 开启时间 */
	private long lStartTime ;

	/** 结束时间 */
	private long lEndTime ;
	
	
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
	
	
	public RechargeBounceTimeIndexCfg() {
		this.id = 0;
		this.startTime = "";
		this.endTime = "";
		this.lStartTime = 0;
		this.lEndTime = 0;
	}

	public int getId() {
		return id;
	}
	
	public long getlStartTime() {
		return lStartTime;
	}

	public long getlEndTime() {
		return lEndTime;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		try {
			if ((!this.startTime.isEmpty()) && (!this.endTime.isEmpty())){
				this.lStartTime = DATE_FORMAT.parse(this.startTime).getTime();
				this.lEndTime = DATE_FORMAT.parse(this.endTime).getTime();
			}
		} catch (ParseException e) {
			MyException.catchException(e);
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
		if (!result) {
			throw new RuntimeException("RechargeBounceTimeIndex must be endTime > startTime, Cfg id : " + this.id);
		}
		return true;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
	
	/**
	 * 是否在時間內
	 * @return
	 */
	public  boolean isValid() {
		long currentTime = GuaJiTime.getMillisecond();
		if (currentTime >= this.lStartTime && currentTime <= this.lEndTime) {
			return true;
		}
		return false;
	}
	
	
}
