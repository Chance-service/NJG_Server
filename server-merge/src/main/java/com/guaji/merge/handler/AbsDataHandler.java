package com.guaji.merge.handler;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.guaji.merge.App;
import com.guaji.merge.config.db.Table;
import com.guaji.merge.db.C3P0Impl;

/**
 * otherHandler抽象类
 * 
 * @author tianzhiyuan
 *
 */
public abstract class AbsDataHandler implements IDataHandler {

	/** 行为日志 */
	protected Log actionLogger = App.logger;
	/** 时间日志 */
	protected Log timeLogger = App.log;
	
	/**
	 * 无效数据的总数
	 * 
	 * @param impl
	 * @param table
	 * @return
	 */
	protected int getInvalidCount(C3P0Impl impl, Table table) throws Exception {
		List<Map<String, Object>> count = null;
		String selectSql = "select count(*) FROM " + table.getName() + " where invalid = 1";
		count = impl.executeQuery(selectSql);
		return Integer.valueOf(String.valueOf(count.get(0).get("count(*)")));
	}

	/**
	 * 返回数据总数
	 * 
	 * @param impl
	 * @param tableName
	 * @return
	 */
	protected int getDataCount(C3P0Impl impl, String tableName) throws Exception{
		String selectSql = "select count(*) FROM " + tableName;
		List<Map<String, Object>> count = impl.executeQuery(selectSql);
		return Integer.valueOf(String.valueOf(count.get(0).get("count(*)")));
	}
	
	/**
	 * 删除小号的总数
	 * 
	 * @param impl
	 * @return
	 */
	protected int getDeletePlayerCount(C3P0Impl impl) throws Exception{
		String selectSql = "select count(*) from player where level < 12 and vipLevel <= 0 and fightValue < 1000 and DATEDIFF(NOW(),player.loginTime) > 15";
		List<Map<String, Object>> count = impl.executeQuery(selectSql);
		return Integer.valueOf(String.valueOf(count.get(0).get("count(*)")));
	}

	/**
	 * 返回删除邮件的总数
	 * 
	 * @param impl
	 * @return
	 */
	protected int getEmailCount(C3P0Impl impl) throws Exception{
		String selectSql = "select count(*) FROM email" + " where type != 2";
		List<Map<String, Object>> count = impl.executeQuery(selectSql);
		return Integer.valueOf(String.valueOf(count.get(0).get("count(*)")));
	}

	/**
	 * 返回批量执行次数
	 * @param count
	 * @param everyCount
	 * @return
	 */
	protected int getTimes(int count, int everyCount) {
		int times = count / everyCount;
		int surplus = count % everyCount;
		if (surplus != 0) {
			times = times + 1;
		}
		return times;
	}
}
