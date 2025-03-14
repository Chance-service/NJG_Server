package com.guaji.game.gm;

import java.util.Map;

import org.guaji.db.DBManager;
import org.guaji.os.GuaJiTime;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.xid.GuaJiXID;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.guaji.game.protocol.Const.NoticeType;
import com.guaji.game.GsApp;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.ChatManager;
import com.guaji.game.manager.MailManager;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Mail;
import com.sun.net.httpserver.HttpExchange;
/**
 * 玩家发奖励
 * curl 'localhost:5132/playerreward?user=admin&params=playerid:19;reward:10000_1004_1000;message=121212,&user=admin'
 *
 */
public class PlayerRewardHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		if (paramsMap.containsKey("playerid") && params.contains("message")) {
			int playerid = Integer.valueOf(paramsMap.get("playerid"));
			String message = paramsMap.containsKey("message")?paramsMap.get("message"):"";
			String reward = "", channel = "";
			if (paramsMap.containsKey("reward")) {
				reward = paramsMap.get("reward");
			}
			if (paramsMap.containsKey("channel")) {
				channel = paramsMap.get("channel");
			}

			// 错误屏蔽
			if (playerid == 0) {
				GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":3,\"msg\":\"playerid is 0\"}");
				return;
			}

			// 渠道修正
			if (channel.length() > 0 && ("0".equals(channel) || "null".equals(channel))) {
				channel = "";
			}

			// 发放奖励
			if (reward.length() > 0 || message.length() > 0) {
				AwardItems awardItems = AwardItems.valueOf(reward);
				if (playerid == -1) {
					Session session = DBManager.getInstance().getSession();
					Transaction transaction = session.beginTransaction();

					StringBuilder sb = new StringBuilder();
					sb.append("INSERT INTO email(playerId, type, title, content, classification, effectTime, createTime) SELECT id,");
					sb.append("?,").append("?,").append("?,").append("?,'").append(GuaJiTime.getTimeString()).
					append("','").append(GuaJiTime.getTimeString());
					
					if (channel != null && channel.length() > 0) {
						sb.append("' from player where puid like '").append(channel).append("_%'");
					} else {
						sb.append("' from player");
					}

					SQLQuery query = session.createSQLQuery(sb.toString());
					query.setString(1, message);
					if (awardItems != null) {
						query.setString(2, awardItems.toString());
						query.setInteger(0, Mail.MailType.Reward_VALUE);
					} else {
						query.setString(2, "");
						query.setInteger(0, Mail.MailType.Normal_VALUE);
					}
					query.setInteger(3, GsConst.EmailClassification.SYSTEM);	
					query.executeUpdate();
					session.clear();
					transaction.commit();

					// 通知
					GuaJiXID hawkXID = GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.CHAT);
					ChatManager chatManager = (ChatManager) GsApp.getInstance().queryObject(hawkXID).getImpl();
					chatManager.postNotice(NoticeType.NEW_MAIL, 1);

				} else {
					// 发放传书
					if (awardItems != null) {
						MailManager.createSysMail(playerid, Mail.MailType.Reward_VALUE, 0, message, awardItems);
					} else {
						MailManager.createSysMail(playerid, Mail.MailType.Normal_VALUE, 0, message, null);
					}
				}

				// 日志记录
				BehaviorLogger.log4GM("", Source.GM_OPERATION, Action.GM_AWARD, 
						Params.valueOf("reward", reward), 
						Params.valueOf("message", message));

				GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
			}
		} else {
			GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":2,\"msg\":\"invalid params\"}");
		}
	}
}
