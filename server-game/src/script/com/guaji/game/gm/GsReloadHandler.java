package com.guaji.game.gm;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.guaji.app.App;
import org.guaji.config.ClassScaner;
import org.guaji.config.ConfigStorage;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.nativeapi.NativeApi;
import org.guaji.os.MyException;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.hibernate.cfg.Configuration;

import com.guaji.game.GsConfig;
import com.sun.net.httpserver.HttpExchange;

/**
 * gs配置加载
 * curl 'localhost:5132/gscheck?&user=admin'
 */
public class GsReloadHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {
		try {

			new ConfigStorage(GsConfig.getInstance().getClass());
			// 初始化数据库连接
			if (GsConfig.getInstance().getDbHbmXml() != null && GsConfig.getInstance().getDbConnUrl() != null
					&& GsConfig.getInstance().getDbUserName() != null && GsConfig.getInstance().getDbPassWord() != null) {
				if (!reload(GsConfig.getInstance().getDbHbmXml(), GsConfig.getInstance().getDbConnUrl(), GsConfig
						.getInstance().getDbUserName(), GsConfig.getInstance().getDbPassWord(), GsConfig.getInstance()
						.getEntityPackages())) {
					return;
				}
				// 开启数据库异步落地
				if (GsConfig.getInstance().getDbAsyncPeriod() > 0) {
					int dbThreadNum = GsConfig.getInstance().getDbThreads();
					if (dbThreadNum <= 0) {
						dbThreadNum = GsConfig.getInstance().getThreadNum();
					}

					if (dbThreadNum > 0) {
						DBManager.getInstance().startAsyncThread(GsConfig.getInstance().getDbAsyncPeriod(),
								dbThreadNum);
						Log.logPrintln(String.format("start dbExecutor, threadNum: %d", dbThreadNum));
					}
				}
			}

			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
		} catch (Exception e) {
			MyException.catchException(e);
		}
	}

	public boolean reload(String hbmXml, String connUrl, String userName, String passWord, String entityPackages) {
		// 检测
//		if (!NativeApi.checkHawk()) {
//			return false;
//		}

		try {
			String fileName = App.getInstance().getWorkPath() + hbmXml;
			Configuration conf = new Configuration();
			conf.configure(new File(fileName));

			// 重新数据库设置
			conf.setProperty("hibernate.connection.url", connUrl);
			conf.setProperty("hibernate.connection.username", userName);
			conf.setProperty("hibernate.connection.password", passWord);

			Field field = DBManager.getInstance().getClass().getDeclaredField("conf".trim());
			field.setAccessible(true);
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			field.set(DBManager.getInstance(), conf);

			String[] entityPackageArray = entityPackages.split(",");
			if (entityPackageArray != null) {
				for (String entityPackage : entityPackageArray) {
					Log.logPrintln("init entity package: " + entityPackage);
					List<Class<?>> classList = ClassScaner.scanClassesFilter(entityPackage.trim(), Entity.class);
					for (Class<?> entityClass : classList) {
						if (entityClass.getAnnotation(Table.class) != null) {
							conf.addAnnotatedClass(entityClass);
							Log.logPrintln("scan database entity: " + entityClass.getSimpleName());
						}
					}
				}
			}
			field = DBManager.getInstance().getClass().getDeclaredField("sessionFactory".trim());
			field.setAccessible(true);
			modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			field.set(DBManager.getInstance(), conf.buildSessionFactory());
			if (App.getInstance().getAppCfg().isDebug()) {
				Log.logPrintln(String.format("init database, connUrl: %s, userName: %s, pwd: %s, hbmXml: %s",
						connUrl, userName, passWord, hbmXml));
			} else {
				Log.logPrintln(String.format(
						"init database, connUrl: %s, userName: ******, pwd: ******, hbmXml: %s", connUrl, hbmXml));
			}
			return true;
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return false;
	}
}
