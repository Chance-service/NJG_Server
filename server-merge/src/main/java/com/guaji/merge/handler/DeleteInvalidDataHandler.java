package com.guaji.merge.handler;

import com.guaji.merge.config.DbConfigInfo;
import com.guaji.merge.config.Master;
import com.guaji.merge.config.Slave;
import com.guaji.merge.config.db.DbInfo;
import com.guaji.merge.config.db.Table;
import com.guaji.merge.db.C3P0Impl;

/**
 * 删除无效数据
 * 
 * @author tianzhiyuan
 *
 */
public class DeleteInvalidDataHandler extends AbsDataHandler {

	@Override
	public void execute(Master master, DbInfo dbInfo) throws Exception {
		actionLogger.info("开始删除无效数据");
		for (Table table : dbInfo.getTableList()) {
			if (table.getField("invalid") != null) {
				for (Slave slave : master.getSlaveList()) {
					invokeDelete(slave.getImpl(), dbInfo, table, slave.getDbInfo());
				}
			}
		}
	}

	/**
	 * 删除无效数据
	 * 
	 * @param impl
	 * @param dbInfo
	 * @param table
	 * @param dbconfig
	 * @throws Exception
	 */
	private void invokeDelete(C3P0Impl impl, DbInfo dbInfo, Table table, DbConfigInfo dbconfig) throws Exception {
		long startTime = System.currentTimeMillis();
		int times = getTimes(getInvalidCount(impl, table), dbInfo.getDeleteCount());
		actionLogger.info("【" + dbconfig.toString() + "】表:" + table.getName() + "需要分【" + times + "】次删除");
		for (int i = 0; i < times; i++) {
			String sql = "delete from " + table.getName() + " where invalid=1 limit " + dbInfo.getDeleteCount();
			actionLogger.info("【" + dbconfig.toString() + "】第" + (i + 1) + "次执行删除无效数据==>" + sql);
			impl.executeUpdate(sql);
		}
		timeLogger.info("-------------------删除" + dbconfig.getServerId() + "服" + "【" + table.getName() + "】表:"
				+ (System.currentTimeMillis() - startTime) / 1000 + "s-------------------");
	}

}
