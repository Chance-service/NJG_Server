package com.guaji.merge.handler;

import com.guaji.merge.config.Master;
import com.guaji.merge.config.db.DbInfo;
import com.guaji.merge.db.C3P0Impl;

public class MegerAwardHandler extends AbsDataHandler {

	@Override
	public void execute(Master master, DbInfo dbInfo) throws Exception {
		
		giveMergerAward(master.getImpl(),dbInfo);
	}


	/**
	 * @param impl
	 * @param dbcfg
	 * @param dbInfo
	 * @throws Exception
	 */
	private void giveMergerAward(C3P0Impl impl,DbInfo dbInfo) throws Exception {
		String sql = "INSERT INTO email (playerId,type,mailId,title,content,params,classification,effectTime,createTime,updateTime,invalid) SELECT id,2,7005,'mergerServer','30000_101004_1','30000_101004_1',2,NOW(),NOW(),NOW(),0 FROM player";
		impl.executeUpdate(sql);
	}
}
