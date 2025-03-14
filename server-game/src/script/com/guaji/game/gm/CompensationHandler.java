package com.guaji.game.gm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.log.Log;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.protocol.AllianceBattle.FightGroup;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.AllianceFightVersus;
import com.guaji.game.item.AwardItems;
import com.guaji.game.manager.AllianceBattleAgainstInfo;
import com.guaji.game.manager.AllianceBattleManager;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.MailManager;
import com.guaji.game.protocol.Mail;
import com.sun.net.httpserver.HttpExchange;

public class CompensationHandler extends GuaJiScript{

	@Override
	public void action(String params, HttpExchange httpExchange) {
		
		AllianceBattleAgainstInfo lastAgainstInfo = AllianceBattleManager.getInstance().getLastBattleAgainstInfo();
		
		Map<Integer, Boolean> rewardMap = new HashMap<Integer, Boolean>();
		Map<Integer, List<AllianceFightVersus>> allianceAgainstMap = lastAgainstInfo.getAgainstMap();
		for(AllianceFightVersus versus : allianceAgainstMap.get(FightGroup.GROUP_4_VALUE)) {
			if(versus != null) {
				int leftId = versus.getLeftId();
				int rightId = versus.getRightId();
				AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(leftId);
				if(allianceEntity != null) {
					for(int memberId : allianceEntity.getMemberList()) {
						if(!rewardMap.containsKey(memberId) || !rewardMap.get(memberId)) {
							MailManager.createMail(memberId, Mail.MailType.Reward_VALUE, 0, "上届公会战出现物穿队伍加成计算错误，影响部分四强队伍的排名，已进行修复，对受此影响的玩家进行补偿", 
									AwardItems.valueOf("10000_1011_500,30000_41001_5,30000_91001_2"));
							Log.logPrintln("send alliance battle compensation--config error, allianceId: " + leftId + "playerId: " + memberId);
							rewardMap.put(memberId, true);
						}
					}
				}
				allianceEntity = AllianceManager.getInstance().getAlliance(rightId);
				if(allianceEntity != null) {
					for(int memberId : allianceEntity.getMemberList()) {
						if(!rewardMap.containsKey(memberId) || !rewardMap.get(memberId)) {
							MailManager.createMail(memberId, Mail.MailType.Reward_VALUE, 0, "上届公会战出现物穿队伍加成计算错误，影响部分四强队伍的排名，已进行修复，对受此影响的玩家进行补偿", 
									AwardItems.valueOf("10000_1011_500,30000_41001_5,30000_91001_2"));
							Log.logPrintln("send alliance battle compensation--config error, allianceId: " + rightId + "playerId: " + memberId);
							rewardMap.put(memberId, true);
						}
					}
				}
			}
		}
		
		GuaJiScriptManager.sendResponse(httpExchange, "{status:1}");
	}

}
