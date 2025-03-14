package com.guaji.merge.config;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.guaji.merge.App;
import com.guaji.merge.config.db.DbInfo;
import com.guaji.merge.config.db.Table;
import com.guaji.merge.db.C3P0Impl;
import com.guaji.merge.db.DBSource;

/**
 * 从服务器
 * 
 * @author Administrator
 * 
 */
public class Slave {

	private static final String MORE_AND_EQUALS = " >= ";
	private static final String MORE = " > ";
	private static Log logger = App.logger;

	private C3P0Impl impl;
	private DbConfigInfo dbInfo;

	public void connect() {
		impl = new C3P0Impl(new DBSource(dbInfo));
	}

	public DbConfigInfo getDbInfo() {
		return dbInfo;
	}

	public void setDbInfo(DbConfigInfo dbInfo) {
		this.dbInfo = dbInfo;
	}

	public C3P0Impl getImpl() {
		return impl;
	}

	public void merge(C3P0Impl masterImpl, DbInfo db) {
		logger.info("开始合并" + dbInfo.toString());
		List<Table> tableList = db.getTableList();
		for (Table table : tableList) {
			if (table.getIsMerge() == 1) {
				long startTime = System.currentTimeMillis();
				logger.info("从slave中读取表【" + table.getName() + "】");
				int allCount = getRecordCount(table);
				int times = getTimes(allCount, db.getEveryCount());
				logger.info("表【" + table.getName() + "】,总记录数:" + allCount + ",需要分【" + times + "】处理");

				Object minPrimaryValue = queryMinPrimaryKeyValue(table);
				String compareOperator = MORE_AND_EQUALS;
				for (int i = 0; i < times; i++) {
					logger.info("开始处理第" + (i + 1) + "次");
					String sql = "select " + table.getFiledStr(false) + " from " + table.getName() + " where "
							+ table.getPrimaryKey() + compareOperator + minPrimaryValue + " order by "
							+ table.getPrimaryKey() + " ASC limit " + db.getEveryCount();

					long innerBegin = System.currentTimeMillis();
					
					List<String> values = impl.executeQuery(sql, table.getName(), db,this.getDbInfo().getServerId(), false);

					compareOperator = MORE;
					if (!filterTables(table)) {
						logger.info(String.format("Query from: %d, count: %d, cost time: %d ms", minPrimaryValue,
								db.getEveryCount(), (System.currentTimeMillis() - innerBegin)));
					}
					if (values != null && values.size() > 0) {
						String batchSql = getBatchSql(table, values);
						logger.info("往master中写入表【" + table.getName() + "】");
						innerBegin = System.currentTimeMillis();
						masterImpl.insert(batchSql);
						
						if (!filterTables(table)) {
							logger.info(String.format("Insert from: %d, count: %d, cost time: %d ms", minPrimaryValue,
									db.getEveryCount(), (System.currentTimeMillis() - innerBegin)));
						}
						// get max primary key
						minPrimaryValue = impl.getCurrentMinPrimaryKeyValue();
					}
				}
				App.log.info("-------------------合并" + dbInfo.getServerId() + "服" + "【" + table.getName() + "】表:"
						+ (System.currentTimeMillis() - startTime) / 1000 + "s-------------------");
			}

		}
	}

	/**
	 * 查询指定表的最大的主键值
	 * 
	 * @param table
	 * @return
	 */
	private Object queryMinPrimaryKeyValue(Table table) {
		List<Map<String, Object>> result = impl
				.executeQuery("select min(" + table.getPrimaryKey() + ") from " + table.getName());
		String value = String.valueOf(result.get(0).get("min(" + table.getPrimaryKey() + ")"));
		if (value == null || "null".equalsIgnoreCase(value)) {
			return 0;
		}
		// recharge
		if (filterTables(table)) {
			return "'" + String.valueOf(value) + "'";
		}
		return Integer.valueOf(value);
	}

	private boolean filterTables(Table table) {
		return table.getName().equals("recharge");
	}

	/**
	 * 获取批量sql
	 * 
	 * @param sqlList
	 * @return
	 */
	private String getBatchSql(Table table, List<String> sqlList) {
		StringBuffer sb = new StringBuffer();
		// sb.append("insert into ").append(table.getName()).append(" values ");
		sb.append("insert into ").append(table.getName()).append(" ").append(table.getFiledStr(true))
				.append(" values ");
		for (int i = 0; i < sqlList.size(); i++) {
			String sql = sqlList.get(i);
			if (i == sqlList.size() - 1) {
				sb.append(sql);
			} else {
				sb.append(sql).append(",");
			}
		}
		return sb.toString();
	}

	private int getRecordCount(Table table) {
		String selectSql = "select count(*) FROM " + table.getName();
		List<Map<String, Object>> count = impl.executeQuery(selectSql);
		return Integer.valueOf(String.valueOf(count.get(0).get("count(*)")));
	}

	public int getTimes(int count, int everyCount) {
		int times = count / everyCount;
		int surplus = count % everyCount;
		if (surplus != 0) {
			times = times + 1;
		}
		return times;
	}

}
