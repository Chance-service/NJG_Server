package com.guaji.game.gm;

import org.guaji.config.ConfigManager;
import org.guaji.os.MyException;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.config.ActivityCfg;
import com.guaji.game.util.ActivityUtil;
import com.sun.net.httpserver.HttpExchange;

/**
 * 配置重新加载
 */
public class XmlReloadHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {
		try {
			// 活动配置加载
			ActivityCfg.load();
			// 非活动配置加载
			ConfigManager.getInstance().updateReload();
			// 活动时间重归类
			ActivityUtil.activityTimeCfgsClassify();
			
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
		} catch (Exception e) {
			MyException.catchException(e);
		}
	}
}
