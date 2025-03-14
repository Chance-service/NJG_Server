package com.guaji.merge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import com.guaji.bean.ServerInfo;
import com.guaji.merge.config.DbConfigInfo;
import com.guaji.merge.config.Master;
import com.guaji.merge.config.ServerInfoConfig;
import com.guaji.merge.config.db.DbInfo;
import com.guaji.merge.config.db.DbInfoConfig;
import com.guaji.merge.util.DBUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class App {

	public static Log log = LogFactory.getLog("TimeLog");
	public static Log logger = LogFactory.getLog("ConsoleLog");
	public static Log exception = LogFactory.getLog("exception");

	public static Map<Integer, ServerInfo> serverList = null;

	public static void main(String[] args) {
		try {

			// 加载log4j 的配置
			PropertyConfigurator.configure("log4j.properties");
			DbInfoConfig.getDbConfig().readXML();
			DbInfo dbInfo = DbInfoConfig.getDbConfig().getDbInfo();
			dbInfo.checkType();

			DbConfigInfo masterDbConfig = ServerInfoConfig.getServerInfoConfig().getMaster().getDbInfo();
			// 检查数据库是否创建成功
			boolean result = DBUtil.checkDataBase(masterDbConfig.getDb(), masterDbConfig);
			if (!result) {
				log.info("------------------无法正常创建数据库成功!----------------- ");
				return;
			}
			serverList = new HashMap<>();
			StringBuffer sbf = new StringBuffer();
			InputStreamReader isr = new InputStreamReader(new FileInputStream("servers.json"), "UTF-8");
			BufferedReader reader = new BufferedReader(isr);
			try {

				String tempStr;
				while ((tempStr = reader.readLine()) != null) {
					sbf.append(tempStr);
				}
				reader.close();
				JSONObject jObjf = JSONObject.fromObject(sbf.toString());
				if (jObjf.has("severs")) {
					JSONArray jsArry = jObjf.getJSONArray("severs");
					for (int i = 0; i < jsArry.size(); i++) {
						JSONObject jsonItem = (JSONObject) jsArry.get(i);
						ServerInfo server = new ServerInfo(jsonItem.getString("name"), jsonItem.getString("state"),
								jsonItem.getString("address"), jsonItem.getInt("port"), jsonItem.getInt("order"),
								jsonItem.getInt("id"));
						serverList.put(jsonItem.getInt("id"), server);
						System.out.print("name:"+jsonItem.getString("name"));
					}
				}

			} catch (IOException e) {
				e.printStackTrace(); 
				return;
			} finally {
				reader.close();
			}

			// 批量创建表
			boolean createTable = DBUtil.execSqlFileByMysql(masterDbConfig,
					String.format("sql%sdbtmpl.sql", File.separator));
			if (!createTable) {
				log.info("------------------批量创建表------------------ ");
				return;
			}

			log.info("------------------开始执行数据合服操作------------------ ");
			// 执行批量创建表
			Master master = ServerInfoConfig.getServerInfoConfig().getMaster();
			master.connect();
			boolean isSuccess = master.check(dbInfo);
			if (isSuccess) {
				long startTime = System.currentTimeMillis();
				master.handleBeforeMerge(dbInfo);
				long otherTime = System.currentTimeMillis();
				log.info("-------------------准备时间：" + (otherTime - startTime) / 1000 + "s-------------------");
				master.merge(dbInfo);
				long mergeTime = System.currentTimeMillis();
				log.info("-------------------合服时间：" + (mergeTime - otherTime) / 1000 + "s-------------------");
				master.handleAfterMerge(dbInfo);
				long resetTime = System.currentTimeMillis();
				log.info("-------------------重置排行时间：" + (resetTime - mergeTime) / 1000 + "s-------------------");
				log.info("-------------------合服总时间：" + (resetTime - startTime) / 1000 + "s-------------------");
			}
		} catch (Exception e) {
			exception.error("异常", e);
			e.printStackTrace();
		}
	}
}
