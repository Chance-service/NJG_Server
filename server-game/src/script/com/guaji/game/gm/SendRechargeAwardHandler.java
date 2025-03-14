package com.guaji.game.gm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.guaji.db.DBManager;
import org.guaji.script.GuaJiScript;

import com.guaji.game.item.AwardItems;
import com.guaji.game.manager.MailManager;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Mail;
import com.sun.net.httpserver.HttpExchange;

public class SendRechargeAwardHandler extends GuaJiScript {

	@Override
	public void action(String params, HttpExchange httpExchange) {

		String sql = "select playerId from recharge where goodsId=30 and createTime >='2015-05-08 00:00:00' and createTime<'2015-06-01 00:00:00'  group by playerId";
		List<Integer> playerIdList = DBManager.getInstance().executeQuery(sql);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = "2015-06-05 00:00:00";
		try {
			Date date = sdf.parse(time);
			AwardItems awardItems = AwardItems.valueOf("30000_90009_10");
			
			for (Integer playerId : playerIdList) {
				MailManager.createMail((int)playerId, Mail.MailType.Reward_VALUE, 0, date, "5월 월정액 구매 보상", awardItems, GsConst.EmailClassification.COMMON);
			}
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		
	}

}
