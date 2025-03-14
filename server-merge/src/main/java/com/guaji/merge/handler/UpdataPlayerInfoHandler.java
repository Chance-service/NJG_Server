package com.guaji.merge.handler;

import java.util.TimeZone;

import com.guaji.merge.config.DbConfigInfo;
import com.guaji.merge.config.Master;
import com.guaji.merge.config.Slave;
import com.guaji.merge.config.db.DbInfo;
import com.guaji.merge.db.C3P0Impl;

/**
 * 处理status相关数据
 * 
 * @author tianzhiyuan
 *
 */
public class UpdataPlayerInfoHandler extends AbsDataHandler {

	@Override
	public void execute(Master master, DbInfo dbInfo) throws Exception {
		/*
		 * for (Slave slave : master.getSlaveList()) { updateStatus(slave.getImpl(),
		 * slave.getDbInfo(), dbInfo); }
		 */
		updateStatus(master.getImpl(), null, dbInfo);
	}

	private void updateStatus(C3P0Impl impl, DbConfigInfo dbcfg, DbInfo dbInfo) throws Exception {
		
		Long nowTime =System.currentTimeMillis();
		Long todayLeftTime=1000*3600*24-(nowTime + TimeZone.getDefault().getRawOffset())% (1000*3600*24);
		Long todayStartTime=Long.parseLong("0");
		if(todayLeftTime<1000*3600*12) {
			todayStartTime=	(nowTime+todayLeftTime)/1000;
		}else {
			todayStartTime=(nowTime - (nowTime + TimeZone.getDefault().getRawOffset())% (1000*3600*24))/1000;
		}
		String sql=String.format("update player set mergeTime=%d", todayStartTime.intValue());
		impl.executeUpdate(sql);
	}
	
	private void updatePlayerIdsStatus(C3P0Impl impl, DbConfigInfo dbcfg, DbInfo dbInfo) throws Exception {
		
		Long nowTime =System.currentTimeMillis();
		Long todayStartTime = (nowTime - (nowTime + TimeZone.getDefault().getRawOffset())% (1000*3600*24))/1000;
		String sql=String.format("update player set mergeTime=%d", todayStartTime.intValue());
		impl.executeUpdate(sql);
	}
}
