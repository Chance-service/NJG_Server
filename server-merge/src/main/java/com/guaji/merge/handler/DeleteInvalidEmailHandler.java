package com.guaji.merge.handler;

import com.guaji.merge.config.DbConfigInfo;
import com.guaji.merge.config.Master;
import com.guaji.merge.config.Slave;
import com.guaji.merge.config.db.DbInfo;
import com.guaji.merge.db.C3P0Impl;

/**
 * 删除无效邮件
 * 
 * @author tianzhiyuan
 *
 */
public class DeleteInvalidEmailHandler extends AbsDataHandler {

	@Override
	public void execute(Master master, DbInfo dbInfo) throws Exception {
		for (Slave slave : master.getSlaveList()) {
			deleteEmail(slave.getImpl(), slave.getDbInfo(), dbInfo);
		}
	}

	/**
	 * 删除邮件
	 * 
	 * @param impl
	 * @param dbInfo
	 * @throws Exception
	 */
	private void deleteEmail(C3P0Impl impl, DbConfigInfo dbcfg, DbInfo dbInfo) throws Exception {
		long startTime = System.currentTimeMillis();
		int times = getTimes(getEmailCount(impl), dbInfo.getDeleteCount());
		actionLogger.info("【" + dbcfg.toString() + "】表:email" + "需要分【" + times + "】次删除");
		for (int i = 0; i < times; i++) {
			String sql = "delete from email" + " where type!=2 limit " + dbInfo.getDeleteCount();
			actionLogger.info("【" + dbcfg.toString() + "】第" + (i + 1) + "次执行删除无效数据==>" + sql);
			impl.executeUpdate(sql);
		}
		timeLogger.info("-------------------删除" + dbcfg.getServerId() + "服类型不符邮件："
				+ (System.currentTimeMillis() - startTime) / 1000 + "s-------------------");
	}
}
