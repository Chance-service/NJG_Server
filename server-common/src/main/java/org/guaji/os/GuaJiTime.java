package org.guaji.os;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间通用处理
 */
public class GuaJiTime {
	private static long msOffset = 0;

	/***
	 * 2000年的时间戳 用做计算dayID（从2000年一月一日起到现在的天数间隔就是dayID）
	 */
	private static final long DATE_2000 = DATE_FORMATOR("2000-01-01 00:00:00").getTime();
	/** 一整天的毫秒数 */
	private static final long MS_WHOLE_DAY = 86400000;

	/**
	 * 获取时间偏移
	 * 
	 * @return
	 */
	public static long getMsOffset() {
		return msOffset;
	}

	/**
	 * 设置时间偏移
	 * 
	 * @param msOffset
	 */
	public static void setMsOffset(long msOffset) {
		GuaJiTime.msOffset = msOffset;
	}

	/**
	 * 获取系统时间
	 * 
	 * @return
	 */
	public static Calendar getCalendar() {
		Calendar calendar = Calendar.getInstance();
		if (getMsOffset() != 0) {
			calendar.setTimeInMillis(calendar.getTimeInMillis() + getMsOffset());
		}
		return calendar;
	}

	/**
	 * 获取系统距1970年1月1日总毫秒
	 * 
	 * @return
	 */
	public static long getMillisecond() {
		return getCalendar().getTimeInMillis() + getMsOffset();
	}

	/**
	 * 获取系统距1970年1月1日总秒
	 * 
	 * @return
	 */
	public static int getSeconds() {
		return (int) ((getCalendar().getTimeInMillis() + getMsOffset()) / 1000);
	}

	/**
	 * 获取一年中的天
	 * 
	 * @return
	 */
	public static int getYearDay() {
		return getCalendar().get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * 获取一个月中第几天
	 * 
	 * @return
	 */
	public static int getMonthDay() {
		return getCalendar().get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取一年中的周
	 * 
	 * @return
	 */
	public static int getYearWeek() {
		return getCalendar().get(Calendar.WEEK_OF_YEAR);
	}
	
	/**
	 * 获取現在幾月
	 * 1..12
	 * @return
	 */
	public static int getNowMonth() {
		return getCalendar().get(Calendar.MONTH) + 1 ;
	}
	
	/**
	 * 获取系统当前时间
	 * 
	 * @return
	 */
	public static Timestamp getTimestamp() {
		Timestamp ts = new Timestamp(getMillisecond());
		return ts;
	}

	/**
	 * 获取当日0点时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getAM0Date() {
		Calendar calendar = getCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 获取指定日期的0点时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getAM0Date(Date date) {
		Calendar calendar = getCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 获取指定日期的0点01分时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getAM0001Date(Date date) {
		Calendar calendar = getCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 1);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 获取指定时间的指定时间
	 * 
	 * @param date
	 * @param hour
	 * @return
	 */
	public static Date getConditionDate(Date date, int hour) {
		Calendar calendar = getCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 获取指定日期的0点02分时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getAM0002Date(Date date) {
		Calendar calendar = getCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 2);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 是否是相同的日子（月 和 天 相同）
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static boolean isSameDay(long time1, long time2) {
		Calendar dt1 = getCalendar();
		Calendar dt2 = getCalendar();
		dt1.setTime(new Date(time1));
		dt2.setTime(new Date(time2));
		if (dt1.get(Calendar.MONTH) == dt2.get(Calendar.MONTH)
				&& dt1.get(Calendar.DAY_OF_MONTH) == dt2.get(Calendar.DAY_OF_MONTH)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 是否為同一個月
	 *  * @return
	 */
	
	public static boolean isSameMonth(Calendar calendar1 ,Calendar calender2) {
		if ((calendar1.get(Calendar.YEAR) == calender2.get(Calendar.YEAR)) && 
		(calendar1.get(Calendar.MONTH) == calender2.get(Calendar.MONTH))) {
			return true;
		}
		return false;
	}

	/**
	 * 是否为当天
	 * 
	 * @param time1
	 * @return
	 */
	public static boolean isToday(long time1) {
		Calendar dt1 = getCalendar();
		Calendar dt2 = getCalendar();
		dt1.setTime(new Date(time1));
		if (dt1.get(Calendar.MONTH) == dt2.get(Calendar.MONTH)
				&& dt1.get(Calendar.DAY_OF_MONTH) == dt2.get(Calendar.DAY_OF_MONTH)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 是否为当天
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static boolean isToday(Date date) {
		Calendar dt1 = getCalendar();
		Calendar dt2 = getCalendar();
		dt2.setTime(date);
		if (dt1.get(Calendar.MONTH) == dt2.get(Calendar.MONTH)
				&& dt1.get(Calendar.DAY_OF_MONTH) == dt2.get(Calendar.DAY_OF_MONTH)) {
			return true;
		}
		return false;
	}

	/**
	 * 格式化日期
	 * 
	 * @return
	 */
	public static String getTimeString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(getCalendar().getTime());
	}

	/**
	 * 格式化日期
	 * 
	 * @return
	 */
	public static String getTimeString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	/**
	 * 格式化日期
	 * 
	 * @return
	 */
	public static String getDateString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(getCalendar().getTime());
	}

	/**
	 * 格式化日期
	 * 
	 * @return
	 */
	public static String getDateString(Calendar calendar) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(calendar.getTime());
	}

	/**
	 * 格式化日期
	 * 
	 * @return
	 */
	public static String getDateString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date.getTime());
	}

	/**
	 * 字符串转换为日历格式 yyyy-MM-dd
	 * 
	 * @param info
	 * @return
	 */
	public static Date DATE_FORMATOR_YYYYMMDD(String info) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			sdf.parse(info);
		} catch (ParseException e) {
			MyException.catchException(e);
		}
		return sdf.getCalendar().getTime();
	}
	/**
	 * 字符串转换为日历格式 輸出N天後
	 * @param info
	 * @return
	 */
	public static String DATE_FORMATOR_NEXT_NDay(String info,int nday) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			sdf.parse(info);
		} catch (ParseException e) {
			MyException.catchException(e);
		}
		Calendar afterDayCalendar =(Calendar) sdf.getCalendar().clone();
		afterDayCalendar.add(Calendar.DAY_OF_YEAR, nday);
		
		return sdf.format(afterDayCalendar.getTime());
	}	
	/**
	 * 字符串转换为日历格式 yyyy-MM-dd
	 * 
	 * @param info
	 * @return
	 */
	public static String DATE_FORMATOR_YYYYMMDD(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	/**
	 * 字符串转换为日历格式 yyyy年MM月dd日
	 * 
	 * @param info
	 * @return
	 */
	public static Date DATE_FORMATOR_CHN(String info) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		try {
			sdf.parse(info);
		} catch (ParseException e) {
			MyException.catchException(e);
		}
		return sdf.getCalendar().getTime();
	}

	/**
	 * 字符串转换为日历格式 yyyyMMdd
	 * 
	 * @param info
	 * @return
	 */
	public static Date DATE_FORMATOR_DAYNUM(String info) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			sdf.parse(info);
		} catch (ParseException e) {
			MyException.catchException(e);
		}
		return sdf.getCalendar().getTime();
	}

	/**
	 * 字符串转换为日历格式 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param info
	 * @return
	 */
	public static Date DATE_FORMATOR(String info) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			sdf.parse(info);
		} catch (ParseException e) {
			MyException.catchException(e);
		}
		return sdf.getCalendar().getTime();
	}

	/**
	 * 字符串转换为日历格式 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param info
	 * @return
	 */
	public static Date DATE_FORMATOR_AF(String info) {
		// 公会战特殊时间处理格式
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHH:mm:ss");
		try {
			dateFormat.parse(info);
		} catch (ParseException e) {
			MyException.catchException(e);
		}
		return dateFormat.getCalendar().getTime();
	}

	/**
	 * 字符串转换为日历格式 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param info
	 * @return
	 */
	public static String DATE_FORMATOR_DAYNUM(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(date);
	}

	/**
	 * 获取两个日期之间的天数差
	 * 
	 * @param cal1
	 * @param cal2
	 * @return
	 */
	public static int calendarDiff(Calendar cal1, Calendar cal2) {
		return (cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR)) * 365 - cal2.get(Calendar.DAY_OF_YEAR)
				+ cal1.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * 设置当前时间的小时和分钟转化为毫秒
	 * 
	 * @param hour
	 * @param minute
	 * @return
	 */
	public static long setTimeHourMinute(int hour, int minute) {
		Calendar calendar = getCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	/**
	 * 获得指定小时分钟的完整时间
	 * 
	 * @param hour
	 * @param minute
	 * @return
	 */
	public static long getTimeHourMinute(String hourminute) {
		String args[] = hourminute.trim().split("\\:");
		int hour = Integer.parseInt(args[0]);
		int minute = Integer.parseInt(args[1]);
		Calendar calendar = getCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	/**
	 * @return 获取第二天零点
	 */
	public static long getNextAM0Date() {
		Calendar calendar = getCalendar();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}
	
	/**
	 * @return 下個月一號零點
	 */
	public static long getNextMonthAM0Date() {
		Calendar calendar = getCalendar();
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}
	
	/**
	 * @return 下週週一零點
	 */
	public static long getNextWeekAM0Date() {
		Calendar calendar = getCalendar();

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		
		if (dayOfWeek == 1) {
			calendar.add(Calendar.DATE,dayOfWeek);
		} else {
			calendar.add(Calendar.DATE,9-dayOfWeek);
		}

		return calendar.getTimeInMillis();
	}

	/**
	 * 获取第N天零点
	 * 
	 * @param day
	 * @return
	 */
	public static long getDayAM0Date(int day) {
		Calendar calendar = getCalendar();
		calendar.add(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}
	
	/**
	 * 获取第N天零点
	 * 
	 * @param day
	 * @return
	 */
	public static long getTheDayAM0Date(int day) {
		Calendar calendar = getCalendar();
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	/**
	 * @param day 第几天
	 * @return
	 */
	public static long getNextMonthDayAM0Date(int day) {
		Calendar calendar = getCalendar();
		calendar.add(Calendar.MONTH,1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	/**
	 * 获取两个时间相隔的天数
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int calcBetweenDays(Date startDate, Date endDate) {
		if ((startDate == null) || (endDate == null)) {
			return 0;
		}
		Date startDate0AM = getAM0Date(startDate);
		Date endDate0AM = getAM0Date(endDate);
		long v1 = startDate0AM.getTime() - endDate0AM.getTime();
		return Math.abs((int) divideAndRoundUp(v1, 86400000.0D, 0));
	}

	private static double divideAndRoundUp(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("the scale must be a positive integer or zero");
		}

		BigDecimal bd1 = new BigDecimal(v1);
		BigDecimal bd2 = new BigDecimal(v2);
		return bd1.divide(bd2, scale, 0).doubleValue();
	}

	/**
	 * 获得当前这一周的第一天，中国是周一
	 * 
	 * @return
	 */
	public static Date getFirstDayOfCurWeek() {
		Calendar calendar = getCalendar();
		return getFirstDayOfWeek(calendar);
	}

	/**
	 * 当前日期是否为本周周一
	 * 
	 * @param curDay
	 * @return
	 */
	public static boolean isFirstDayOfWeek(int curDay) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); // 设置时间格式
		Calendar cal = Calendar.getInstance();
		Date time;
		try {
			time = sdf.parse(curDay + "");
			cal.setTime(time);

			// 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
			if (1 == dayWeek) {
				cal.add(Calendar.DAY_OF_MONTH, -1);
			}

			cal.setFirstDayOfWeek(Calendar.MONDAY);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int day = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
		return Integer.parseInt(sdf.format(cal.getTime())) == curDay ? true : false;
	}

	/**
	 * 获得当前这一周的第一天，中国是周一
	 * 
	 * @return
	 */
	public static Calendar getFirstDayCalendarOfCurWeek() {
		Calendar calendar = getCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == 1) {
			calendar.add(Calendar.DATE, -6);
		} else {
			calendar.add(Calendar.DATE, (2 - dayOfWeek));
		}
		return calendar;
	}
	
	/**
	 * 獲取當月有幾天
	 */
	
	public static int daysCountOfMonth() {
		Calendar calendar = getCalendar();
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	
	/**
	  * 獲得星期幾 
	 * 
	 * @return 0.日 1.一 2.二......
	 */
	public static int getDaysOfWeek() {
		Calendar calendar = getCalendar();
		int weekday = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 獲取星期的第幾日
		return weekday;
	}

	/**
	 * 获得指定某个日期那一周的第一天，中国是周一
	 * 
	 * @return
	 */
	public static Date getFirstDayOfWeek(Date date) {
		Calendar calendar = getCalendar();
		calendar.setTime(date);
		return getFirstDayOfWeek(calendar);
	}

	/**
	 * 获得指定某个日期那一周的第一天，中国是周一
	 * 
	 * @return
	 */
	public static Date getFirstDayOfWeek(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == 1) {
			calendar.add(Calendar.DATE, -6);
		} else {
			calendar.add(Calendar.DATE, (2 - dayOfWeek));
		}
		return calendar.getTime();
	}
	
	/**
	 * 获取dayID 今天距离2000年的天数就是日期
	 * 
	 * @return
	 */
	public static int getDayIDBase2000() {
		Date today = getAM0Date();
		long todayTime = today.getTime();
		long dayDiff = (todayTime - DATE_2000) / MS_WHOLE_DAY;
		return Math.abs((int) dayDiff);
	}

	/**
	 * @param 当前年份
	 * @return true 是润年 false 非闰年
	 */
	public static boolean isLeapYear(int year) {
		if (year % 4 == 0 && year % 100 != 0)
			return true;
		else if (year % 400 == 0)
			return true;
		else
			return false;
	}

}
