package com.server.paynotice.schedule;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.server.paynotice.dbservice.SnapshotService;

public class ExpireShcedule implements Job {

	//@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		SnapshotService.delSnapshotByDays(1);
	}

}
