package com.guaji.game.gm;

import java.util.List;

import org.guaji.db.DBManager;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.entity.ServerDataEntity;
import com.guaji.game.util.GsConst;
import com.sun.net.httpserver.HttpExchange;

/**
 * 修复红包活动与排名献礼serverdata 数据库数据 ID重复 问题;
 * 格式: curl 'localhost:5132/fixserverdata?&user=admin'
 */
public class FixServerDataRedEnvelope extends GuaJiScript{

	@SuppressWarnings({ "rawtypes" })
	@Override
	public void action(String params, HttpExchange httpExchange) {
		
		List<ServerDataEntity> redEnvelopeData = DBManager.getInstance().query("from ServerDataEntity where id = ? and invalid = 0", GsConst.ServerStatusId.RED_ENVELOPE);
		if (redEnvelopeData.size() != 0) {
			ServerDataEntity serverDataEntity = redEnvelopeData.get(0);
			DBManager.getInstance().delete(serverDataEntity);
			
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
		} else {
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2}");
		}
		
	}

}
