package com.guaji.merge.handler;

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
public class UpdataStatusInfoHandler extends AbsDataHandler {

	@Override
	public void execute(Master master, DbInfo dbInfo) throws Exception {
		/*
		 * for (Slave slave : master.getSlaveList()) { updateStatus(slave.getImpl(),
		 * slave.getDbInfo(), dbInfo); }
		 */
		updateStatus(master.getImpl(), null, dbInfo);
	}

	private void updateStatus(C3P0Impl impl, DbConfigInfo dbcfg, DbInfo dbInfo) throws Exception {
		String sql = "update status set equipSmeltCreate = '',lastShowMultiEliteResultId = 0";
		impl.executeUpdate(sql);
	}
}
