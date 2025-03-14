package com.server.paynotice.schedule;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;

public class ScheduleService {
	private static Logger logger = Logger.getLogger(ScheduleService.class);
	
	public static void executeLoadXml(){
		try {
			// 生成一个job。名字为storageJob，Group为storageGroup，任务执行类为StorageData.class
			JobDetail job = ScheduleManager.generatorJob("loadxmlJob",
					"loadxmlGroup", LoadXMLShcedule.class);
			// 生成一个Trigger。名字为storageTrigger，Group为storageGroup，触发时间为每日凌晨3点
			CronTrigger cronTrigger = ScheduleManager.generatorTrigger(
					"loadxmlTrigger", "loadxmlGroup", "0/5 * * * * ?");
			ScheduleManager.scheduleJob(job, cronTrigger);
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	/**
	 * 执行表数据转移
	 */
	public static void executeTransdata()
	{
		try {
			// 生成一个job。名字为storageJob，Group为storageGroup，任务执行类为StorageData.class
			JobDetail job = ScheduleManager.generatorJob("transJob",
					"transGroup", TransDataShcedule.class);
			// 生成一个Trigger。名字为storageTrigger，Group为storageGroup，触发时间为每日凌晨3点
			CronTrigger cronTrigger = ScheduleManager.generatorTrigger(
					"transTrigger", "transGroup", "0/5 * * * * ?");
			ScheduleManager.scheduleJob(job, cronTrigger);
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	/**
	 * 执行过期检测
	 */
	public static void executeExpire()
	{
		try {
			// 生成一个job。名字为storageJob，Group为storageGroup，任务执行类为StorageData.class
			JobDetail job = ScheduleManager.generatorJob("expireJob",
					"expireGroup", ExpireShcedule.class);
			// 生成一个Trigger。名字为storageTrigger，Group为storageGroup，触发时间为每日凌晨3点
			CronTrigger cronTrigger = ScheduleManager.generatorTrigger(
					"expireTrigger", "expireGroup", "0/5 * * * * ?");
			ScheduleManager.scheduleJob(job, cronTrigger);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("",e);
		}
	}
}
