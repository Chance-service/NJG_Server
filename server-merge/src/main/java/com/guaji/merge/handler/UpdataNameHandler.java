package com.guaji.merge.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.guaji.merge.App;
import com.guaji.merge.config.Master;
import com.guaji.merge.config.db.DbInfo;
import com.guaji.merge.util.DBUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.google.gson.Gson;

/**
 * 修改玩家名字、公会名字
 * 
 * @author tianzhiyuan
 *
 */
public class UpdataNameHandler extends AbsDataHandler {

	@Override
	public void execute(Master master, DbInfo dbInfo) throws Exception {
		/*
		 * for (Slave slave : master.getSlaveList()) { updateName(slave.getImpl(),
		 * slave.getDbInfo().getAddServerId(), slave.getDbInfo().getServerId()); }
		 */
		updateName(master, dbInfo);
	}

	/**
	 * 更新玩家的name name+"s_"+serverId 还有公会名
	 * 
	 * @param impl
	 * @param serverId
	 * @throws Exception
	 */
	private void updateName(Master master, DbInfo dbInfo) throws Exception {

		// 修改重复的角色名
		String tableName = "player";
		String sql = "select `id`,`name`,`serverId` from " + tableName + " order by id asc";
		actionLogger.info("执行查询==>" + sql);
		List<String> playerNames = new LinkedList<String>();
		Map<String, Integer> playerServerIdMap = new HashMap<String, Integer>();
		List<Map<String, Object>> playerValues = master.getImpl().executeQuery(sql);
		List<String> batchSql = new LinkedList<String>();
		for (Map<String, Object> map : playerValues) {

			playerServerIdMap.put(String.valueOf(map.get("id")), Integer.parseInt(String.valueOf(map.get("serverId"))));
			if (!playerNames.contains(String.valueOf(map.get("name")))) {
				playerNames.add(String.valueOf(map.get("name")));
			} else {

				String newName = "";
				if (!App.serverList.containsKey(map.get("serverId"))) {
					newName = String.format("%s[s%s]", String.valueOf(map.get("name")),
							String.valueOf(map.get("serverId")));
				} else {
					newName = String.format("%s[s%s]", String.valueOf(map.get("name")),
							App.serverList.get(map.get("serverId")).getName().substring(0, 2));
				}

				batchSql.add(String.format("update %s set name='%s' where id=%s", tableName, newName,
						String.valueOf(map.get("id"))));
				// 同步更role中的角色名
				batchSql.add(String.format("UPDATE role SET name='%s' WHERE playerId=%s AND TYPE=1", newName,
						String.valueOf(map.get("id"))));

				batchSql.add(String.format("UPDATE login SET playerName='%s' WHERE playerId=%s", newName,
						String.valueOf(map.get("id"))));

				batchSql.add(String.format("UPDATE alliance SET playerName='%s' WHERE playerId=%s", newName,
						String.valueOf(map.get("id"))));
			}
		}
		tableName = "alliance";
		sql = "select id,playerId,name from " + tableName + " order by id asc";
		actionLogger.info("执行查询==>" + sql);
		List<String> allianceNames = new LinkedList<String>();
		List<Map<String, Object>> allianceValues = master.getImpl().executeQuery(sql);

		for (Map<String, Object> map : allianceValues) {
			if (!allianceNames.contains(String.valueOf(map.get("name")))) {
				allianceNames.add(String.valueOf(map.get("name")));
			} else {
				// 需要改名
				Integer serverId = playerServerIdMap.get(String.valueOf(map.get("playerId")));
				String newAllianceName = String.format("%s[%d]", String.valueOf(map.get("name")), serverId);
				if (App.serverList.containsKey(serverId)) {
					newAllianceName = String.format("%s[%s]", String.valueOf(map.get("name")),
							App.serverList.get(serverId).getName().substring(0, 2));
				}
				batchSql.add(String.format("update %s set name='%s' where id=%s", tableName, newAllianceName,
						String.valueOf(map.get("id"))));
			}
		}

		boolean result = DBUtil.execBatchSql(master.getDbInfo(), batchSql);
		if (!result) {
			throw new Exception("修改重名语句失败");
		}

		// 修改重复的帮会名
	}

	public void GetServerName() {

	}
}
