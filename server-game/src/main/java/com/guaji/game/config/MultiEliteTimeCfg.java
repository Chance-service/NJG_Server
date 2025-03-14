package com.guaji.game.config;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;

@ConfigManager.XmlResource(file = "xml/multiEliteTime.xml", struct = "list")
public class MultiEliteTimeCfg extends ConfigBase implements Cloneable {

	private final String date;

	/**
	 * 开始时间
	 */
	private final String openHour;

	/**
	 * 结束时间
	 */
	private final String endHour;

	private Calendar startDate;
	private Calendar endDate;

	public MultiEliteTimeCfg() {
		this.date = "";
		this.openHour = "";
		this.endHour = "";
	}

	public Calendar getDate(String str) {
		Calendar calendar = Calendar.getInstance();
		String[] teimStr = initString(str);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), Integer.valueOf(teimStr[0]),
				Integer.valueOf(teimStr[1]), Integer.valueOf(teimStr[2]));
		return calendar;
	}

	public static List<MultiEliteTimeCfg> getMultiEliteTimeCfg() {
		List<MultiEliteTimeCfg> timeCfgs = new LinkedList<>();
		if (timeCfgs.size() == 0) {
			for (MultiEliteTimeCfg timeCfg : ConfigManager.getInstance().getConfigList(MultiEliteTimeCfg.class)) {
				timeCfg = timeCfg.clone();
				timeCfg.setStartDate(timeCfg.getDate(timeCfg.getOpenHour()));
				timeCfg.setEndDate(timeCfg.getDate(timeCfg.getEndHour()));
				timeCfgs.add(timeCfg);
			}
		}

		return timeCfgs;
	}

	/**
	 * 获得当前时间前面一场的配置
	 * 
	 * @return
	 */
	public static MultiEliteTimeCfg getBeforeMultiEliteTimeCfg() {
		MultiEliteTimeCfg beforeTimeCfg = null;
		Calendar curCal = GuaJiTime.getCalendar();
		List<MultiEliteTimeCfg> timeCfgs = getMultiEliteTimeCfg();
		long curMilSec = curCal.getTime().getTime();
		for (MultiEliteTimeCfg cfg : timeCfgs) {
			Calendar startDate = cfg.getDate(cfg.getOpenHour());
			Calendar endDate = cfg.getDate(cfg.getEndHour());
			if (startDate != null && curMilSec > endDate.getTimeInMillis()) {
				beforeTimeCfg = cfg.clone();
				beforeTimeCfg.setStartDate(startDate);
				beforeTimeCfg.setEndDate(endDate);
				break;
			}
		}

		if (beforeTimeCfg == null) {
			curCal.add(Calendar.DAY_OF_YEAR, -1);
			timeCfgs = getMultiEliteTimeCfg();
			if (timeCfgs.size() > 0) {
				beforeTimeCfg = timeCfgs.get(timeCfgs.size() - 1);
				beforeTimeCfg = beforeTimeCfg.clone();
				beforeTimeCfg.setStartDate(curCal);
				beforeTimeCfg.setEndDate(timeCfgs.get(timeCfgs.size() - 1).getEndDate());
			}
		}

		return beforeTimeCfg;
	}

	/**
	 * 获得当前时间对应的配置
	 * 
	 * @return
	 */
	public static MultiEliteTimeCfg getCurMultiEliteTimeCfg() {
		Calendar curDate = GuaJiTime.getCalendar();
		List<MultiEliteTimeCfg> timeCfgs = getMultiEliteTimeCfg();
		long curMilSec = curDate.getTimeInMillis();
		for (MultiEliteTimeCfg timeCfg : timeCfgs) {
			Calendar startDate = timeCfg.getDate(timeCfg.getOpenHour());
			Calendar endDate = timeCfg.getDate(timeCfg.getEndHour());
			if (startDate != null && curMilSec >= startDate.getTimeInMillis() && curMilSec <= endDate.getTimeInMillis()) {
				timeCfg = timeCfg.clone();
				timeCfg.setStartDate(startDate);
				timeCfg.setEndDate(endDate);
				return timeCfg;
			}
		}
		return null;
	}

	/**
	 * 获得当前时间对应的配置(当天，跨天下期没有作处理)
	 * 
	 * @return
	 */
	public static MultiEliteTimeCfg getNextMultiEliteTimeCfg() {
		Calendar curCal = GuaJiTime.getCalendar();
		List<MultiEliteTimeCfg> timeCfgs = getMultiEliteTimeCfg();
		long curMilSec = curCal.getTime().getTime();
		MultiEliteTimeCfg nextTimeCfg = null;
		for (MultiEliteTimeCfg timeCfg : timeCfgs) {
			Calendar startDate = timeCfg.getDate(timeCfg.getOpenHour());
			if (startDate != null && curMilSec < startDate.getTimeInMillis()) {
				nextTimeCfg = timeCfg.clone();
				nextTimeCfg.setStartDate(startDate);
				break;
			}
		}

		if (nextTimeCfg == null) {
			curCal.add(Calendar.DAY_OF_YEAR, 1);
			timeCfgs = getMultiEliteTimeCfg();
			if (timeCfgs.size() > 0) {
				nextTimeCfg = timeCfgs.get(0);
				nextTimeCfg = nextTimeCfg.clone();
				String [] times = initString(nextTimeCfg.getOpenHour());
				curCal.set(Calendar.HOUR_OF_DAY , Integer.valueOf(times[0]));
				curCal.set(Calendar.MINUTE , Integer.valueOf(times[1]));
				curCal.set(Calendar.SECOND , Integer.valueOf(times[2]));
				curCal.set(Calendar.MILLISECOND, 0);
				nextTimeCfg.setStartDate(curCal);
			}
		}
		return nextTimeCfg;
	}

	public MultiEliteTimeCfg clone() {
		try {
			return (MultiEliteTimeCfg) super.clone();
		} catch (CloneNotSupportedException e) {
			MyException.catchException(e);
		}
		return new MultiEliteTimeCfg();
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
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

	public String getDate() {
		return date;
	}

	public String getOpenHour() {
		return openHour;
	}

	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	public String getEndHour() {
		return endHour;
	}

	public static String[] initString(String str) {
		String[] timeStr = str.split(":");
		return timeStr;
	}

}
