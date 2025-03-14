package com.server.paynotice.schedule;

import java.util.ArrayList;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.server.paynotice.dbservice.CancelUserService;
import com.server.paynotice.dbservice.SnapshotService;
import com.server.paynotice.pojo.CancelUser;
import com.server.paynotice.pojo.Snapshot;
import com.server.paynotice.util.DateUtil;

public class TransDataShcedule implements Job{
	//@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		List<CancelUser> list = CancelUserService.queryUsersByDays(3);
		if(list==null||list.isEmpty())
		{
			return;
		}
		//即将删除的id集合
		List<Integer> ids = new ArrayList<Integer>();
		//即将插入t_cancelsnapshot表中的数据集合
		List<Snapshot> snapshots = new ArrayList<Snapshot>();
		for(CancelUser user : list)
		{
			if(user==null)
			{
				continue;
			}
			ids.add(user.getId());
			Snapshot snapshot = new Snapshot();
			snapshot.setAction(3);
			snapshot.setUserid(user.getUserid());
			snapshot.setInserttime(DateUtil.getCurrentTimestamp());
			snapshots.add(snapshot);
		}
		CancelUserService.delUser(ids);
		SnapshotService.insertSnapshots(snapshots);
	}

}
