package com.guaji.merge.config;

import java.util.ArrayList;
import java.util.List;

import com.guaji.merge.config.db.DbInfo;
import com.guaji.merge.db.C3P0Impl;
import com.guaji.merge.db.DBSource;
import com.guaji.merge.handler.DeleteActivityHandler;
import com.guaji.merge.handler.DeleteInvalidDataHandler;
import com.guaji.merge.handler.DeleteInvalidEmailHandler;
import com.guaji.merge.handler.DeleteInvalidRechargeHandler;
import com.guaji.merge.handler.DeleteLosePlayerHandler;
import com.guaji.merge.handler.IDataHandler;
import com.guaji.merge.handler.MegerAwardHandler;
//import com.guaji.merge.handler.MegerAwardHandler;
import com.guaji.merge.handler.ResetArenaRankHandler;
import com.guaji.merge.handler.UpdataAllianceInfoHandler;
import com.guaji.merge.handler.UpdataEighteenPrincesHandler;
import com.guaji.merge.handler.UpdataNameHandler;
import com.guaji.merge.handler.UpdataPlayerInfoHandler;
import com.guaji.merge.handler.UpdataStatusInfoHandler;
import com.guaji.merge.handler.UpdataTitleHandler;
import com.guaji.merge.handler.UpdateWorldBossHandler;
import com.guaji.merge.util.DBUtil;

/**
 * 主服务器
 * 
 * @author Administrator
 * 
 */
public class Master {

	private C3P0Impl impl;
	private DbConfigInfo dbInfo;
	private List<Slave> slaveList;
	private List<IDataHandler> beforeMergeHandlerList;
	private List<IDataHandler> afterMergeHandlerList;

	public Master() {
		this.slaveList = new ArrayList<Slave>();
		//合服前删除所有无效数据
		this.beforeMergeHandlerList = new ArrayList<IDataHandler>();
		//删除索引
		this.beforeMergeHandlerList.add(new DeleteInvalidDataHandler());
		this.beforeMergeHandlerList.add(new DeleteInvalidEmailHandler());
		this.beforeMergeHandlerList.add(new DeleteInvalidRechargeHandler());
		this.beforeMergeHandlerList.add(new DeleteLosePlayerHandler());
//		this.beforeMergeHandlerList.add(new DeleteLosePlayerHandler());
		
		//合服后处理数据
		this.afterMergeHandlerList = new ArrayList<IDataHandler>();
		this.afterMergeHandlerList.add(new ResetArenaRankHandler());
		this.afterMergeHandlerList.add(new DeleteActivityHandler());
		this.afterMergeHandlerList.add(new UpdataAllianceInfoHandler());
		this.afterMergeHandlerList.add(new UpdataStatusInfoHandler());
		this.afterMergeHandlerList.add(new UpdataTitleHandler());
		this.afterMergeHandlerList.add(new UpdataPlayerInfoHandler());
		this.afterMergeHandlerList.add(new UpdataNameHandler());
		this.afterMergeHandlerList.add(new UpdataEighteenPrincesHandler());
		this.afterMergeHandlerList.add(new MegerAwardHandler());
		this.afterMergeHandlerList.add(new UpdateWorldBossHandler());
		
		
	//	this.afterMergeHandlerList.add(new ResetPalyerIDs());
	}

	/**
	 * 连接数据库
	 */
	public void connect() {
		impl = new C3P0Impl(new DBSource(dbInfo));
		for (Slave slave : slaveList) {
			slave.connect();
		}
	}

	
	/**
	 * 检测表结构
	 * @param dbInfo
	 * @return
	 */
	public boolean check(DbInfo dbInfo) {
		boolean isSuccess = true;
		isSuccess = DBUtil.checkTable(this.dbInfo.getDb(), impl, dbInfo);
		if (!isSuccess) {
			return isSuccess;
		}
		for (Slave slave : slaveList) {
			isSuccess = DBUtil.checkTable(slave.getDbInfo().getDb(), slave.getImpl(), dbInfo);
			if (!isSuccess) {
				return isSuccess;
			}
		}
		return isSuccess;
	}

	
    /**
     * 合服前操作
     * @param dbInfo
     * @throws Exception
     */
	public void handleBeforeMerge(DbInfo dbInfo) throws Exception {
		for (IDataHandler iDataHandler : beforeMergeHandlerList) {
			iDataHandler.execute(this, dbInfo);
		}
	}
	
	/**
	 * 合并数据库
	 * 
	 * @param dbInfo
	 */
	public void merge(DbInfo dbInfo) {
		for (Slave slave : slaveList) {
			
			slave.merge(impl, dbInfo);
		}
	}

	
    /**
     * 合服后操作
     * @param dbInfo
     * @throws Exception
     */
	public void handleAfterMerge(DbInfo dbInfo) throws Exception {
		for (IDataHandler iDataHandler : afterMergeHandlerList) {
			iDataHandler.execute(this, dbInfo);
		}
	}
	
	public void addSlave(Slave slave) {
		slaveList.add(slave);
	}

	public DbConfigInfo getDbInfo() {
		return dbInfo;
	}

	public void setDbInfo(DbConfigInfo dbInfo) {
		this.dbInfo = dbInfo;
	}

	public List<Slave> getSlaveList() {
		return slaveList;
	}

	public C3P0Impl getImpl() {
		return impl;
	}

}
