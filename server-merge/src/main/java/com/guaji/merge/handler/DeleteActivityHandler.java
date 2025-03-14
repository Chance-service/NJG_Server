package com.guaji.merge.handler;

import com.guaji.merge.config.DbConfigInfo;
import com.guaji.merge.config.Master;
import com.guaji.merge.config.Slave;
import com.guaji.merge.config.db.DbInfo;
import com.guaji.merge.db.C3P0Impl;
/**
 * 删除远征物资活动
 * @author tianzhiyuan
 *
 */
public class DeleteActivityHandler extends AbsDataHandler{

	@Override
	public void execute(Master master, DbInfo dbInfo) throws Exception {
		/*
		for (Slave slave : master.getSlaveList()) {
			deleteActivityId(slave.getImpl(), slave.getDbInfo(), dbInfo);
		}
		*/
		deleteActivityId(master.getImpl(),master.getDbInfo(),dbInfo);
	}

	
	/**
	 * 删除活动远程物资
	 * @param impl
	 * @param dbcfg
	 * @param dbInfo
	 */
	private void deleteActivityId(C3P0Impl impl, DbConfigInfo dbcfg, DbInfo dbInfo){
		long startTime = System.currentTimeMillis();
		actionLogger.info("【" + dbcfg.toString() + "】表:player_activity" + "远程物资活动删除");
		String sql = "delete from player_activity where activityId in(25,126,3,128)";
		impl.executeUpdate(sql);
		timeLogger.info("-------------------删除" + dbcfg.getServerId() + "远程物资活动："
				+ (System.currentTimeMillis() - startTime) / 1000 + "s-------------------");
	}
}
