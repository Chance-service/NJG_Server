package com.guaji.game.gm;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.os.GuaJiTime;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.google.gson.JsonObject;
import com.guaji.game.config.MonthCardCfg;
import com.guaji.game.entity.EmailEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.entity.RechargeEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.manager.MailManager;
import com.guaji.game.module.activity.monthcard.MonthCardStatus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Mail;
import com.sun.net.httpserver.HttpExchange;

public class MonthCardFixHandler extends GuaJiScript {

	@SuppressWarnings("unchecked")
	@Override
	public void action(String params, HttpExchange httpExchange) {
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);
		JsonObject jsonObject = new JsonObject();
		if(paramsMap.containsKey("playerId")) {
			int playerId = Integer.valueOf(paramsMap.get("playerId"));
			Player player = PlayerUtil.queryPlayer(playerId);
			PlayerEntity playerEntity = null;
			PlayerData playerData = null;
			if(player == null) {
				playerEntity = DBManager.getInstance().fetch(PlayerEntity.class, "from PlayerEntity where id = ?", playerId);
				playerData = new PlayerData(null);
				playerData.setPlayerEntity(playerEntity);
				playerData.loadActivity();
			}else{
				playerEntity = player.getPlayerData().getPlayerEntity();
				playerData = player.getPlayerData();
			}
			
			//修复月卡的期数
			Calendar calendar = GuaJiTime.getCalendar();
			calendar.add(Calendar.DAY_OF_YEAR, -30);
			List<RechargeEntity> rechargeEntities = DBManager.getInstance().query("from RechargeEntity where playerId = ? and goodsId = 30 and createTime > ? order by createTime"
					, playerId, calendar.getTime());
			
			
			MonthCardStatus monthCardStatus = ActivityUtil.getMonthCardStatus(playerData);
			if(monthCardStatus != null) {
				try {
					Field field = MonthCardStatus.class.getDeclaredField("monthCardCfgIds");
					field.setAccessible(true);
					List<Integer> monthCardIds = (List<Integer>) field.get(monthCardStatus);
					monthCardIds.clear();
					if(rechargeEntities.size() > 0) {
						for(int i = 0; i < rechargeEntities.size();i++) {
							monthCardIds.add(1);
						}
						
						monthCardStatus.setStartDate(rechargeEntities.get(0).getCreateTime());
					}
					playerData.updateActivity(Const.ActivityId.MONTH_CARD_VALUE, 0);
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			
			if(monthCardStatus.getLeftDays() > 0) {
				MonthCardCfg monthCardCfg = ConfigManager.getInstance().getConfigByKey(MonthCardCfg.class, monthCardStatus.getCurrentActiveCfgId());
				AwardItems awardItems = AwardItems.valueOf(monthCardCfg.getReward());
				List<EmailEntity> emailEntities = DBManager.getInstance()
						.query("from EmailEntity where playerId = ? and mailId = 7 and effectTime <= ? and effectTime > ? and invalid = 0", 
								playerId, GuaJiTime.getCalendar().getTime(), new Date());
				
				// 先把发错的邮件删掉
				int delCount = 0;
				List<EmailEntity> needRemoveEmails = DBManager.getInstance()
						.query("from EmailEntity where playerId = ? and mailId = 7 and invalid = 0", 
								playerId);
				for(EmailEntity emailEntity : needRemoveEmails) {
					emailEntity.delete();
					delCount++;
				}
				
				if(emailEntities.size() == 0) {
					//今日没有邮件
					if(monthCardStatus.getStartDate() != null) {
						calendar.setTime(monthCardStatus.getStartDate());
						calendar.set(Calendar.HOUR_OF_DAY,0);
						calendar.set(Calendar.MINUTE,0);
						calendar.set(Calendar.SECOND,0);
						calendar.set(Calendar.MILLISECOND,0);
						//造奖励邮件
						for(int i=0;i<delCount;i++) {
							calendar.add(Calendar.DAY_OF_YEAR, i);
							MailManager.createMail(playerId, Mail.MailType.Reward_VALUE, GsConst.MailId.MONTH_CARD_REWARD, 
									calendar.getTime(), "", awardItems, GsConst.EmailClassification.COMMON, String.valueOf(monthCardCfg.getDays() - i -1));
							calendar.add(Calendar.DAY_OF_YEAR, 0 - i);
						}
						
						jsonObject.addProperty("delCount", delCount);
					}
					
				}
			}
			jsonObject.addProperty("status", 1);
			jsonObject.addProperty("rechargeSize", rechargeEntities.size());
		} else {
			jsonObject.addProperty("status", 2);
		}
		
		GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
	}

}

