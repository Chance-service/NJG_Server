package com.guaji.game.gm;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import org.guaji.db.DBManager;
import org.guaji.script.GuaJiScript;

import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.MailManager;
import com.guaji.game.protocol.Mail;
import com.sun.net.httpserver.HttpExchange;

public class PlayerRewardContentHandler extends GuaJiScript {

	@Override
	public void action(String params, HttpExchange httpExchange) {
		int serverId = com.guaji.game.GsConfig.getInstance().getServerId();
		Properties prop = new Properties();// 属性集合对象
		FileInputStream fis;
		String propertyName = "";
		try {
			propertyName = "script/" + serverId + ".properties";
			fis = new FileInputStream(propertyName);
			prop.load(fis);// 将属性文件流装载到Properties对象中
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] allianceInfo = prop.getProperty(String.valueOf(serverId)).split(",");//serverId=公会Id&会长Id,公会Id2&会长Id2.......
		String reward = prop.getProperty("reward");// 发给工会会员的信息
		String leaderReward = prop.getProperty("LeaderReward");// 发给会长的信息

		
		for (String string : allianceInfo) {
			String allianceId = string.split("&")[0];//公会Id
			String leaderId = string.split("&")[1];//会长Id
			// 给会长发邮件
			AwardItems awardItemsLeader = AwardItems.valueOf(leaderReward);
			if (awardItemsLeader != null) {
				MailManager.createMail(Integer.parseInt(leaderId),
						Mail.MailType.Reward_VALUE, 0, "Приз русскоговорящим гильдиям- Лидерам", awardItemsLeader);
			} else {
				MailManager.createMail(Integer.parseInt(leaderId),
						Mail.MailType.Normal_VALUE, 0, "Приз русскоговорящим гильдиям- Лидерам", null);
			}
			// 日志记录
			BehaviorLogger.log4GM("", Source.GM_OPERATION, Action.GM_AWARD, 
					Params.valueOf("reward", reward), 
					Params.valueOf("message", "Приз русскоговорящим гильдиям- Лидерам"));

			List<PlayerAllianceEntity> playerAllianceEntity = DBManager
					.getInstance().query(
							"from PlayerAllianceEntity where allianceId='"
									+ allianceId + "' and postion <> 2");

			// 给所有公会会员发邮件
			for (PlayerAllianceEntity playerAlliance : playerAllianceEntity) {
				AwardItems awardItems = AwardItems.valueOf(reward);
				// 发放传书
				if (awardItems != null) {
					MailManager.createMail(playerAlliance.getPlayerId(),
							Mail.MailType.Reward_VALUE, 0, "Приз русскоговорящим гильдиям- Участникам", awardItems);
				} else {
					MailManager.createMail(playerAlliance.getPlayerId(),
							Mail.MailType.Normal_VALUE, 0, "Приз русскоговорящим гильдиям- Участникам", null);
				}
			}
			// 日志记录
			BehaviorLogger.log4GM("", Source.GM_OPERATION, Action.GM_AWARD, 
					Params.valueOf("reward", reward), 
					Params.valueOf("message", "Приз русскоговорящим гильдиям- Участникам"));

//			File file = new File(propertyName);
//			if (file.exists()) {
//				file.delete();
//			}
		}
	}
}
