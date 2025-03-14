package com.server.paynotice.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat(FORMAT);
	
	public static Timestamp strToTimestamp(String timeStr)
	{
		return Timestamp.valueOf(timeStr);
	}
	
	public static Timestamp getCurrentTimestamp()
	{
		return new Timestamp(System.currentTimeMillis());
	}
	
	public static String getTimeStr(Timestamp time)
	{
		return SIMPLE_FORMAT.format(time);
	}
	
	/**
	 * 获取指定日期的0点时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getAM0Date(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
}
