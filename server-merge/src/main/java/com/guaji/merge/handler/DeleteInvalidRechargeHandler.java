package com.guaji.merge.handler;

import com.guaji.merge.config.DbConfigInfo;
import com.guaji.merge.config.Master;
import com.guaji.merge.config.Slave;
import com.guaji.merge.config.db.DbInfo;
import com.guaji.merge.db.C3P0Impl;

/**
 * 删除无效充值记录
 * 
 * @author tianzhiyuan
 *
 */
public class DeleteInvalidRechargeHandler extends AbsDataHandler {

	@Override
	public void execute(Master master, DbInfo dbInfo) throws Exception {
		for (Slave slave : master.getSlaveList()) {
			deleteRecharge(slave.getImpl(), slave.getDbInfo(), dbInfo);
		}
	}

	/**
	 * 
	 * 删除无效充值记录
	 * @param impl
	 * @param dbInfo
	 * @throws Exception
	 */
	private void deleteRecharge(C3P0Impl impl, DbConfigInfo dbcfg, DbInfo dbInfo) throws Exception {
		long startTime = System.currentTimeMillis();
		actionLogger.info("开始删除recharge中空值数据");
		impl.executeUpdate("delete from recharge where orderSerial='' or  orderSerial=null");
		timeLogger.info("-------------------删除" + dbcfg.getServerId() + "充值表无效记录："
				+ (System.currentTimeMillis() - startTime) / 1000 + "s-------------------");
	}
}
