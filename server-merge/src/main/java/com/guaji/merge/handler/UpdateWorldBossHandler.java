package com.guaji.merge.handler;

import com.guaji.merge.config.Master;
import com.guaji.merge.config.db.DbInfo;
import com.guaji.merge.db.C3P0Impl;

public class UpdateWorldBossHandler extends AbsDataHandler {

	@Override
	public void execute(Master master, DbInfo dbInfo) throws Exception {
		// TODO Auto-generated method stub

		deleteinVaildWorldBoss(master.getImpl(),dbInfo);
	}
	
	
	/**
	 * @param impl
	 * @param dbcfg
	 * @param dbInfo
	 * @throws Exception
	 */
	private void deleteinVaildWorldBoss(C3P0Impl impl,DbInfo dbInfo) throws Exception {
		String sql = "DELETE FROM world_boss WHERE id IN (SELECT T.id FROM (SELECT C.id FROM world_boss C WHERE C.startDate IN(SELECT A.startDate FROM world_boss A GROUP BY A.startDate HAVING COUNT(*) > 1) AND C.id NOT IN (SELECT MIN(B.id) FROM world_boss B GROUP BY B.startDate HAVING COUNT(*) > 1)) T)";
		impl.executeUpdate(sql);
	}

}
