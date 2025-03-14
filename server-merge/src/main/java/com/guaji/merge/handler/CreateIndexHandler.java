package com.guaji.merge.handler;

import java.util.ArrayList;
import java.util.List;

import com.guaji.merge.config.DbConfigInfo;
import com.guaji.merge.config.Master;
import com.guaji.merge.config.Slave;
import com.guaji.merge.config.db.DbInfo;
import com.guaji.merge.config.db.Table;
import com.guaji.merge.db.C3P0Impl;

public class CreateIndexHandler extends AbsDataHandler {

	@Override
	public void execute(Master master, DbInfo dbInfo) throws Exception {
		List<String> tableList = new ArrayList<String>();
		for (Table table : dbInfo.getTableList()) {
			if (table.getIsDelete() == 1) {
				tableList.add(table.getName());
			}
		}
		for (Slave slave : master.getSlaveList()) {
			deletePlayerIds(dbInfo, tableList, slave.getImpl(), slave.getDbInfo());
		}

	}

	/**
	 * 删除小号
	 * 
	 * @param dbInfo
	 * @param tableList
	 * @param impl
	 * @throws Exception
	 */
	private void deletePlayerIds(DbInfo dbInfo, List<String> tableList, C3P0Impl impl, DbConfigInfo dbconfig)
			throws Exception {
		long startTime = System.currentTimeMillis();
		int allCount = getDeletePlayerCount(impl);
		int times = getTimes(allCount, dbInfo.getEveryCount());
		actionLogger.info("表【" + "player" + "】,总记录数:" + allCount + ",需要分【" + times + "】处理");
		for (int i = 0; i < times; i++) {
			actionLogger.info("开始处理第" + (i + 1) + "次");
			String sql = "select player.id from player where level < 12 and vipLevel <= 0 and fightValue < 1000 and DATEDIFF(NOW(),player.loginTime) > 30 limit "
					+ (i * dbInfo.getEveryCount()) + "," + dbInfo.getEveryCount();
			actionLogger.info("执行查询==>" + sql);
			List<String> value = impl.executeQuery(sql, "player", dbInfo, dbconfig.getServerId(), true);
			if (value != null && value.size() > 0) {
				for (String table : tableList) {
					String batchSql = getDeletePlayerIdSql(table, value);
					actionLogger.info("删除【" + table + "】" + "开始处理第" + (i + 1) + "次");
					impl.executeUpdate(batchSql);
				}
			}
		}
		timeLogger.info("-------------------删除" + dbconfig.getServerId() + "服小号" + allCount + "个："
				+ (System.currentTimeMillis() - startTime) / 1000 + "s-------------------");
	}

	/**
	 * 拼删除小号sql
	 * 
	 * @param table
	 * @param sqlList
	 * @return
	 */
	private String getDeletePlayerIdSql(String table, List<String> sqlList) {
		StringBuffer sb = new StringBuffer();
		String cloum = "";
		if (table.equals("player")) {
			cloum = "id";
		} else {
			cloum = "playerId";
		}
		sb.append("delete from ").append(table).append(" where " + cloum + " in(");
		for (int i = 1; i <= sqlList.size(); i++) {
			String sql = sqlList.get(i - 1);
			sql = sql.substring(2, sql.length() - 2);
			if (i == sqlList.size()) {
				sb.append(sql).append(")");
			} else {
				sb.append(sql).append(",");
			}
		}
		return sb.toString();
	}

}
