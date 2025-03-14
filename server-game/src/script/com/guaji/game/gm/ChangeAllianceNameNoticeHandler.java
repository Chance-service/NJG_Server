package com.guaji.game.gm;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.MailManager;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Mail.MailType;
import com.sun.net.httpserver.HttpExchange;

/**
 * 合服之后给玩家发改名卡，给会长通知改公会名邮件 如果是测试环境，为了方便QA多次公会改名测试，支持修改某个公会改名的状态
 * 开发环境：http://localhost:5132/changeAllianceNameNotice?params=allianceId:1;canChangeName:true&user=hawk
 * 生产环境：http://localhost:5132/changeAllianceNameNotice?user=hawk
 * 
 * @author Melvin.Mao
 * @date Oct 27, 2017 4:55:09 PM
 */
public class ChangeAllianceNameNoticeHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {

		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);

		boolean canChangeName = true;
		if (paramsMap.size() == 0) {
			// 修改合过服的公会可以改名标志设为true,并给会长发送改名通知
			ConcurrentHashMap<Integer, AllianceEntity> map = AllianceManager.getInstance().getAllianceMap();
			for (Iterator<AllianceEntity> iterator = map.values().iterator(); iterator.hasNext();) {
				AllianceEntity allianceEntity = (AllianceEntity) iterator.next();
				allianceEntity.setCanChangeName(canChangeName);
				allianceEntity.notifyUpdate();
				// 给公会会长发通知,改公会名
				MailManager.createSysMail(allianceEntity.getPlayerId(), MailType.Normal_VALUE, GsConst.MailId.NOTICE_CHANGE_ALLIANCE_NAME, "", null);
			}
			GuaJiScriptManager.sendResponse(httpExchange,
			        "{\"status\":\"success ! " + map.size() + " alliances affected. canChangeName=" + canChangeName + ".\"}");
		} else {
			// 方便测试环境
			String canChangeNameStr = paramsMap.get("canChangeName");
			String allianceIdStr = paramsMap.get("allianceId");
			int allianceId = 0;
			try {
				canChangeName = Boolean.valueOf(canChangeNameStr);
				allianceId = Integer.valueOf(allianceIdStr);
			} catch (Exception e) {
				e.printStackTrace();
				GuaJiScriptManager.sendResponse(httpExchange,
				        "{\"status\":\"params error ! usage : http://localhost:5132/changeAllianceNameNotice?params=allianceId:1;canChangeName:true&user=hawk\"}");
				return;
			}
			AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
			if (allianceEntity == null) {
				GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":\"alliance not found!!!\"}");
				return;
			}
			allianceEntity.setCanChangeName(canChangeName);
			allianceEntity.notifyUpdate(false);
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":\"alliance[" + allianceId + "] set canChangeName= " + canChangeName + "\"}");
			return;
		}
	}
}
