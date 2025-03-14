package com.guaji.merge.handler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.guaji.merge.config.Master;
import com.guaji.merge.config.Slave;
import com.guaji.merge.config.db.DbInfo;
import com.guaji.merge.db.C3P0Impl;

/**
 * 处理称号相关信息
 * 
 * @author tianzhiyuan
 *
 */
public class UpdataTitleHandler extends AbsDataHandler {

	@Override
	public void execute(Master master, DbInfo dbInfo) throws Exception {

		/*
		 * for (Slave slave : master.getSlaveList()) { updateTitleId(slave.getImpl(),
		 * dbInfo); }
		 */
		updateTitleId(master.getImpl(), dbInfo);
	}

	/**
	 * 玩家称号保留连续登陆
	 * 
	 * @param impl
	 * @param serverId
	 * @throws Exception
	 */
	private void updateTitleId(C3P0Impl impl, DbInfo dbInfo) throws Exception {
		List<String> titleList = Arrays.asList("8", "9", "10", "21");
		String tableName = "title";
		int allCount = getDataCount(impl, tableName);
		int times = getTimes(allCount, dbInfo.getEveryCount());
		actionLogger.info("表【" + tableName + "】,总记录数:" + allCount + ",需要分【" + times + "】处理");

		for (int i = 0; i < times; i++) {
			actionLogger.info("开始处理第" + (i + 1) + "次");
			String sql = "select id,finishIds from " + tableName + " order by id desc limit "
					+ (i * dbInfo.getEveryCount()) + "," + dbInfo.getEveryCount();

			actionLogger.info("执行查询==>" + sql);
			List<Map<String, Object>> titleMap = impl.executeQuery(sql);
			for (Map<String, Object> map : titleMap) {

				String titles = String.valueOf((map.get("finishIds")));
				if (titles.length() == 0) {
					continue;
				}
				int id = Integer.valueOf(String.valueOf((map.get("id"))));
				List<String> list = Arrays.asList(titles.split(","));
				StringBuffer sb = new StringBuffer();
				for (String string : titleList) {
					if (list.contains(string)) {
						sb.append(string).append(",");
					}
				}
				if (sb.length() != 0) {
					titles = sb.substring(0, sb.length() - 1);
				} else {
					titles = "";
				}
				impl.executeUpdate("update title set finishIds = '" + titles + "' where id = " + id);
			}
		}
		impl.executeUpdate("update title set useId = 0,ischange = 0 ");
		
		

	}
}
