package com.guaji.merge.handler;

import java.util.List;

import com.guaji.merge.config.Master;
import com.guaji.merge.config.db.DbInfo;
import com.guaji.merge.config.db.Table;
/**
 * 重置竞技场排行
 * @author tianzhiyuan
 *
 */
public class ResetArenaRankHandler extends AbsDataHandler{

	@Override
	public void execute(Master master, DbInfo dbInfo) throws Exception {
		resetArenaRank(master, dbInfo);
	}
	
	
	/**
	 * 重置竞技排名
	 * 
	 * @param masterImpl
	 * @param dbInfo
	 */
	private void resetArenaRank(Master master,DbInfo dbInfo) {
		String tableName = "player";
		String sql = "select id from " + tableName + " order by fightValue desc,id desc limit 3000";
		actionLogger.info("执行查询==>" + sql);
		List<String> value = master.getImpl().executeQuery(sql, tableName, dbInfo, master.getDbInfo().getServerId(), true);
		if (value != null && value.size() > 0) {
			Table table = new Table();
			table.setName("arena");
			String batchSql = getArenaSql(table, value);
			actionLogger.info("往master中写入表【" + table.getName() + "】");
			master.getImpl().insert(batchSql);
		}

	}
	
	/**
	 * 拼接插入竞技场语句
	 * 
	 * @param table
	 * @param sqlList
	 * @param count
	 * @param everyCount
	 * @return
	 */
	private String getArenaSql(Table table, List<String> sqlList) {
		StringBuffer sb = new StringBuffer();
		sb.append("insert into ").append(table.getName()).append(" (playerId,rank,createTime) values ");
		for (int i = 1; i <= sqlList.size(); i++) {
			String sql = sqlList.get(i - 1);
			sql = sql.substring(0, sql.length() - 1);
			if (i == sqlList.size()) {
				sb.append(sql).append(",'");
				sb.append(i).append("',NOW()").append(")");
			} else {
				sb.append(sql).append(",'").append(i).append("',NOW()").append("),");
			}
		}
		return sb.toString();
	}

}
