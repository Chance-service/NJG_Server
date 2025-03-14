package com.guaji.game.gm;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.guaji.log.Log;
import org.guaji.os.MyException;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.item.AwardItems;
import com.guaji.game.manager.MailManager;
import com.guaji.game.protocol.Mail;
import com.sun.net.httpserver.HttpExchange;

public class MonthCardFixRewardHandler extends GuaJiScript {

	@SuppressWarnings("resource")
	@Override
	public void action(String params, HttpExchange httpExchange) {
		String filePath = "d:/20141106.txt";
		//2014-11-05 23:59:35,654 - fix monthcard enter problem,player :40923
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(filePath), "UTF-8"));
			
			String line = null;
			Set<Integer> playerIdSet = new HashSet<>();
			while((line = reader.readLine()) != null) {
				String[] ss = line.split(":");
				int playerId = Integer.valueOf(ss[3]);
				playerIdSet.add(playerId);
			}
			
			for(Integer playerId : playerIdSet) {
				AwardItems awardItems = AwardItems.valueOf("10000_1001_200,10000_1002_200000");
				MailManager.createMail(playerId, Mail.MailType.Reward_VALUE, 0, "针对5日晚账号状态异常，导致无法登陆游戏的问题进行补偿，感谢您的理解与支持", awardItems);
				Log.logPrintln("send monthcard login problem reward ,playerId :" + playerId);
			}
		}catch(Exception e) {
			MyException.catchException(e);
			GuaJiScriptManager.sendResponse(httpExchange, "{status:2}");
		}
		
		GuaJiScriptManager.sendResponse(httpExchange, "{status:1}");
	}

}

