package org.guaji.timer;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.guaji.os.MyException;

/**
 * 定时器管理器
 * 
 * @author xulinqs
 */
public class TimerManager {
	/**
	 * 后台静默线程
	 */
	AlarmWaiter waiter;
	/**
	 * 时间处理单位
	 */
	SortedSet<TimerEntry> timerQueue;
	/**
	 * 队列管理的锁
	 */
	Lock lock;
	/**
	 * 实例对象
	 */
	static TimerManager instance;

	/**
	 * 获取时钟管理器
	 * 
	 * @return
	 */
	public static TimerManager getInstance() {
		if (instance == null) {
			instance = new TimerManager();
		}
		return instance;
	}

	/**
	 * 创建时间管理器，AlarmWaiter负责后台时间扫描线程
	 * 
	 * @param isDaemon
	 *            后台扫描线程是否设置成daemon
	 * @param threadName
	 */
	public boolean init(boolean isDaemon) {
		lock = new ReentrantLock();
		timerQueue = new TreeSet<TimerEntry>();
		waiter = new AlarmWaiter(this, isDaemon, "AlarmManager");
		return true;
	}

	/**
	 * 特定时间点出发闹钟，如果日期已经是过去时间或者与当前时间比小于1s会抛出异常
	 * 
	 * @param name
	 * @param date
	 * @param listener
	 * @return
	 * @throws MyException
	 */
	public TimerEntry addAlarm(String name, Date date, ITimerListener listener) throws MyException {
		TimerEntry entry = new TimerEntry(name, date, listener);
		addAlarm(entry);
		return entry;
	}

	/**
	 * 设置延时处理闹钟，当前时间之后delay秒执行
	 * 
	 * @param name
	 * @param delaySecond
	 * @param isRepeating
	 * @param listener
	 * @return
	 * @exception MyException
	 */
	public TimerEntry addAlarm(String name, int delaySecond, boolean isRepeating, ITimerListener listener) throws MyException {
		TimerEntry entry = new TimerEntry(name, delaySecond, isRepeating, listener);
		addAlarm(entry);
		return entry;
	}

	/**
	 * 添加闹钟
	 * 
	 * @param second
	 *            Allowed values 0-59, or -1 for all.
	 * @param minute
	 *            Allowed values 0-59, or -1 for all.
	 * @param hour
	 *            hour of the alarm. Allowed values 0-23, or -1 for all.
	 * @param dayOfMonth
	 *            day of month of the alarm. Allowed values 1-7 (1 = Sunday, 2 = Monday, ...), or -1 for all.
	 * @param dayOfWeek
	 *            day of week of the alarm. Allowed values 1-31, or -1 for all.
	 * @param listener
	 *            the alarm listener.
	 * @return the AlarmEntry.
	 * @exception MyException
	 */
	public TimerEntry addAlarm(String name, int second, int minute, int hour, int dayOfMonth, int dayOfWeek, ITimerListener listener) throws MyException {
		TimerEntry entry = new TimerEntry(name, second, minute, hour, dayOfMonth, dayOfWeek, listener);
		addAlarm(entry);
		return entry;
	}

	/**
	 * 添加闹钟
	 * 
	 * @param second
	 *            Allowed values 0-59, or -1 for all.
	 * @param minutes
	 *            minutes of the alarm. Allowed values 0-59, or -1 for all.
	 * @param hours
	 *            hours of the alarm. Allowed values 0-23, or -1 for all.
	 * @param daysOfMonth
	 *            days of month of the alarm. Allowed values 1-7 (1 = Sunday, 2 = Monday, ...), or -1 for all.
	 * @param daysOfWeek
	 *            days of week of the alarm. Allowed values 1-31, or -1 for all.
	 * @param listener
	 *            the alarm listener.
	 * 
	 * @return the AlarmEntry.
	 * @exception MyException
	 *                if the alarm date is in the past (or less than 1 second away from the current date).
	 */
	public TimerEntry addAlarm(String name, int[] seconds, int[] minutes, int[] hours, int[] daysOfMonth, int[] daysOfWeek, ITimerListener listener) throws MyException {
		TimerEntry entry = new TimerEntry(name, seconds, minutes, hours, daysOfMonth, daysOfWeek, listener);
		addAlarm(entry);
		return entry;
	}

	/**
	 * 注册闹钟
	 * 
	 * @param minute
	 * @param hour
	 * @param dayOfMonth
	 * @param month
	 * @param dayOfWeek
	 * @param year
	 * @param listener
	 * @return
	 */
	public TimerEntry register(int second, int minute, int hour, int dayOfMonth, int dayOfWeek, ITimerListener listener) {
		TimerEntry entry = null;
		try {
			entry = new TimerEntry(null, second, minute, hour, dayOfMonth, dayOfWeek, listener);
			addAlarm(entry);
		} catch (MyException e) {
			MyException.catchException(e);
		}
		return entry;
	}

	/**
	 * 注册闹钟
	 * 
	 * @param seconds
	 * @param minutes
	 * @param hours
	 * @param daysOfMonth
	 * @param months
	 * @param daysOfWeek
	 * @param year
	 * @param listener
	 * @return
	 */
	public TimerEntry register(int[] seconds, int[] minutes, int[] hours, int[] daysOfMonth, int[] daysOfWeek, ITimerListener listener) {
		TimerEntry entry = null;
		try {
			entry = new TimerEntry(null, seconds, minutes, hours, daysOfMonth, daysOfWeek, listener);
			addAlarm(entry);
		} catch (MyException e) {
			MyException.catchException(e);
		}
		return entry;
	}

	/**
	 * 添加闹钟 entry
	 * 
	 * @param entry
	 * @exception MyException
	 */
	public void addAlarm(TimerEntry entry) throws MyException {
		try {
			lock.lock();
			timerQueue.add(entry);
			if (timerQueue.first().equals(entry)) {
				waiter.update(entry.alarmTime);
			}
		} catch (Exception e) {
			MyException.catchException(e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 移除指定闹钟
	 * 
	 * @param entry
	 * @return
	 */
	public boolean removeAlarm(TimerEntry entry) {
		boolean found = false;
		try {
			lock.lock();
			if (!timerQueue.isEmpty()) {
				TimerEntry wasfirst = (TimerEntry) timerQueue.first();
				found = timerQueue.remove(entry);
				if (!timerQueue.isEmpty() && entry.equals(wasfirst)) {
					waiter.update(((TimerEntry) timerQueue.first()).alarmTime);
				}
			}
		} catch (Exception e) {
			MyException.catchException(e);
		} finally {
			lock.unlock();
		}
		return found;
	}

	/**
	 * 移除所有闹钟
	 */
	public void removeAllAlarms() {
		try {
			lock.lock();
			timerQueue.clear();
		} catch (Exception e) {
			MyException.catchException(e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 停止并且移除所有闹钟
	 */
	public void stop() {
		if (waiter != null) {
			waiter.stop();
			waiter = null;
		}

		try {
			lock.lock();
			timerQueue.clear();
		} catch (Exception e) {
			MyException.catchException(e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 判断是否停止
	 * 
	 * @return
	 */
	public boolean isStopped() {
		return (waiter == null);
	}

	/**
	 * 是否存在闹钟
	 * 
	 * @param TimerEntry
	 * @return boolean 是否存在
	 */
	public boolean containsAlarm(TimerEntry alarmEntry) {
		try {
			lock.lock();
			return timerQueue.contains(alarmEntry);
		} catch (Exception e) {
			MyException.catchException(e);
		} finally {
			lock.unlock();
		}
		return false;
	}

	/**
	 * 获取当前所有闹钟
	 * 
	 * @return
	 */
	public List<TimerEntry> getAllAlarms() {
		List<TimerEntry> result = new ArrayList<TimerEntry>();
		try {
			lock.lock();
			for (TimerEntry entry : timerQueue) {
				result.add(entry);
			}
		} catch (Exception e) {
			MyException.catchException(e);
		} finally {
			lock.unlock();
		}
		return result;
	}

	/**
	 * 闹铃触发 这个方法会在时间到的时候调用 或者被自己方法调用（下一个闹钟跟这个时间在1s之内）
	 */
	protected void ringNextAlarm() {
		if (timerQueue.isEmpty()) {
			return;
		}

		TimerEntry entry = null;
		try {
			lock.lock();
			if (!timerQueue.isEmpty()) {
				entry = timerQueue.first();
				timerQueue.remove(entry);
			}
		} catch (Exception e) {
			MyException.catchException(e);
		} finally {
			lock.unlock();
		}

		if (entry == null) {
			return;
		}

		if (entry.isRingInNewThread()) {
			new Thread(new RunnableRinger(entry)).start();
		} else {
			try {
				entry.ringAlarm();
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}

		if (entry.isRepeating) {
			entry.updateAlarmTime();
			timerQueue.add(entry);
		}

		// 唤醒AlarmWaiter继续下一个
		if (!timerQueue.isEmpty()) {
			long alarmTime = timerQueue.first().alarmTime;
			if (alarmTime - System.currentTimeMillis() < 1000) {
				ringNextAlarm();
			} else {
				waiter.restart(alarmTime);
			}
		}
	}

	/**
	 * 异步处理闹钟回调
	 * 
	 */
	private class RunnableRinger implements Runnable {
		TimerEntry entry = null;

		RunnableRinger(TimerEntry entry) {
			this.entry = entry;
		}

		public void run() {
			try {
				entry.ringAlarm();
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
	}
}
