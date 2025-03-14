package com.guaji.game.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;

@ConfigManager.XmlResource(file = "xml/SeasonTowerTime.xml", struct = "list")
public class SeasonTowerTimeCfg extends ConfigBase {
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
	private long lStartTime = 0;

	/** 结束时间 */
	private long lEndTime = 0;
	
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
	

	public SeasonTowerTimeCfg() {
		this.id = 0;
		this.startTime = "";
		this.endTime = "";
	}

	public int getId() {
		return id;
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

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		try {
			this.lStartTime = DATE_FORMAT.parse(this.startTime).getTime();
			this.lEndTime = DATE_FORMAT.parse(this.endTime).getTime();
		} catch (ParseException e) {
			MyException.catchException(e);
			return false;
		}
		
		boolean result = this.lEndTime > this.lStartTime;
		if (!result) {
			throw new RuntimeException("SeasonTowerTime.xml must be endTime > startTime, error id : " + this.id);
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
		for (SeasonTowerTimeCfg checkcfg : ConfigManager.getInstance().getConfigList(SeasonTowerTimeCfg.class)) {
			if (checkcfg.getId() == this.getId()) {
				continue;
			}
			if ((this.getlStartTime() > checkcfg.getlStartTime()) && (this.getlStartTime() <= checkcfg.getlEndTime())) {
				throw new RuntimeException("SeasonTowerTime.xml startTime overlapping, error id : " + this.id +" "+ checkcfg.getId());
			}
			if ((this.getlEndTime() > checkcfg.getlStartTime()) && (this.getlEndTime() <= checkcfg.getlEndTime())) {
				throw new RuntimeException("SeasonTowerTime.xml endTime overlapping, error id : " + this.id +" "+ checkcfg.getId());
			}
			
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
	
	public static int getValidTimeIdx() {
		
		for (SeasonTowerTimeCfg timeCfg : ConfigManager.getInstance().getConfigList(SeasonTowerTimeCfg.class)) {
			if (timeCfg.isValid()) {
				return timeCfg.getId();
			}
		}
		
		return -1;
	}
	
	public static SeasonTowerTimeCfg geTimeIdxCfg(int timeIdx) {
		
		for (SeasonTowerTimeCfg timeCfg : ConfigManager.getInstance().getConfigList(SeasonTowerTimeCfg.class)) {
			if (timeCfg.getId() == timeIdx) {
				return timeCfg;
			}
		}
		
		return null;
	}
}
