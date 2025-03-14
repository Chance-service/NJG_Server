package com.guaji.game.gm;

import java.util.List;

import org.guaji.db.DBManager;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.google.gson.JsonObject;
import com.guaji.game.entity.EmailEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.manager.MailManager;
import com.guaji.game.protocol.Mail;
import com.sun.net.httpserver.HttpExchange;

public class GiveBossContributionHandler extends GuaJiScript{

	@SuppressWarnings("static-access")
	@Override
	public void action(String params, HttpExchange httpExchange) {
		List<EmailEntity> emailEntities = DBManager.getInstance().query("from EmailEntity where content = ? and createTime > '2014-09-28 15:00:00'", "10000_1007_0");
		AwardItems awardItems = AwardItems.valueOf("10000_1007_1000,10000_1001_20");
		String playerIds = "";
		int count = 0;
		for(EmailEntity emailEntity : emailEntities) {
			MailManager.createMail(emailEntity.getPlayerId(), Mail.MailType.Reward_VALUE, 0, "vip3自动boss贡献0的补偿", awardItems, "");
			playerIds += emailEntity.getPlayerId() + ",";
			count++;
		}
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("playerIds", playerIds);
		jsonObject.addProperty("count", count);
		GuaJiScriptManager.getInstance().sendResponse(httpExchange, jsonObject.toString());
	}

}
