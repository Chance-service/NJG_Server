package com.guaji.merge.handler;

import com.guaji.merge.config.DbConfigInfo;
import com.guaji.merge.config.Master;
import com.guaji.merge.config.db.DbInfo;
import com.guaji.merge.db.C3P0Impl;

public class UpdataEighteenPrincesHandler extends AbsDataHandler {

	@Override
	public void execute(Master master, DbInfo dbInfo) throws Exception {
		updataEighteenPrinces(master.getImpl(),null,dbInfo);
	}
	
	private void updataEighteenPrinces(C3P0Impl impl, DbConfigInfo dbcfg, DbInfo dbInfo) throws Exception {
		String sql = "update eighteenprinces set helpHistory='{}',enemyformation='{}' where invalid=0";
		impl.executeUpdate(sql);
	}

}
