package com.server.paynotice.schedule;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class ScheduleManager {
	private static Logger logger = Logger.getLogger(ScheduleManager.class);
	private static Scheduler sched;
	private static Object mutex = new Object();

	/**
	 * 生成一个Schedule
	 * 
	 * @return
	 */
	public static Scheduler genShceduler() {
		Scheduler sched = null;
		try {
			SchedulerFactory sf = new StdSchedulerFactory();
			sched = sf.getScheduler();
			sched.start();
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("",e);
		}
		return sched;
	}

	/**
	 * 执行scheuleJob
	 * 
	 * @param job
	 * @param trigger
	 */
	public static void scheduleJob(JobDetail job, Trigger trigger) {
		synchronized (mutex) {
			if (sched == null) {
				sched = genShceduler();
			}
			if (sched == null) {
				logger.error("Scheduler is null");
				return;
			}
			try {
				sched.scheduleJob(job, trigger);
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				logger.error("",e);
			}
		}
	}

	/**
	 * 生产一个JobDetail
	 * 
	 * @param name
	 * @param group
	 * @param class1
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static JobDetail generatorJob(String name, String group, Class classArg) {
		@SuppressWarnings("unchecked")
		JobDetail job = JobBuilder.newJob(classArg).withIdentity(name, group).build();
		return job;
	}

	/**
	 * 生产一个CronTrigger
	 * 
	 * @param name
	 * @param group
	 * @param cron
	 * @return
	 */
	public static CronTrigger generatorTrigger(String name, String group, String cron) {
		CronTrigger trigger = (CronTrigger) TriggerBuilder.newTrigger().withIdentity(name, group)
				.withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
		return trigger;
	}
}
