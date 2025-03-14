package com.guaji.merge.handler;

import com.guaji.merge.config.DbConfigInfo;
import com.guaji.merge.config.Master;
import com.guaji.merge.config.Slave;
import com.guaji.merge.config.db.DbInfo;
import com.guaji.merge.db.C3P0Impl;

/**
 * 处理公会相关数据
 * 
 * @author tianzhiyuan
 *
 */
public class UpdataAllianceInfoHandler extends AbsDataHandler {

	@Override
	public void execute(Master master, DbInfo dbInfo) throws Exception {
		/*
		for (Slave slave : master.getSlaveList()) {
			updateAllianceBoss(slave.getImpl(), slave.getDbInfo(), dbInfo);
		}
		*/
		updateAllianceBoss(master.getImpl(),null,dbInfo);
	}

	/**
	 * 清除公会BOSS信息
	 * 
	 * @param impl
	 * @param dbcfg
	 * @param dbInfo
	 * @throws Exception
	 */
	private void updateAllianceBoss(C3P0Impl impl, DbConfigInfo dbcfg, DbInfo dbInfo) throws Exception {
		String sql = "update alliance set bossOpen = 0 ,bossOpenTime = 0 , bossJoinStr='',bossMaxTime=0,bossAttTime=0,bossAddProp='',canChangeName=1";
		impl.executeUpdate(sql);
	}


}
