package com.guaji.game.gm;


import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.db.DBManager;
import org.guaji.intercept.InterceptHandler;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.GsApp;
import com.guaji.game.entity.EmailEntity;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.HP;
import com.sun.net.httpserver.HttpExchange;

public class MonthCardHandler extends GuaJiScript {

	@Override
	public void action(String params, HttpExchange httpExchange) {
		GsApp.getInstance().addInterceptHandler("com.guaji.game.player.Player", new MonthCardInterceptHandler());
		GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
	}

}

class MonthCardInterceptHandler extends InterceptHandler {
	
	private static final String UPDATE_SQL = "update email set effectTime = date_add(effectTime, interval -%d day) where playerId = %d and createTime > '%s'";
	
	/**
	 * 消息响应
	 * 
	 * @param msg
	 * @return false表示不拦截, 否则拦截消息不往下进行
	 */
	@Override
	public boolean onMessage(AppObj appObj, Msg msg) {
		if(msg.getMsg() == GsConst.MsgType.PLAYER_ASSEMBLE) {
			Player player = (Player)appObj;
			List<EmailEntity> emailEntities = DBManager.getInstance().query("from EmailEntity where playerId = ? and mailId = 7 order by id", player.getId());
			for(EmailEntity emailEntity : emailEntities) {
				emailEntity.convertData();
			}
			EmailEntity lastEnd = null;
			EmailEntity newStage = null;
			for(int i =0;i < emailEntities.size() - 1;i++) {
				if(emailEntities.get(i).isInvalid() && !emailEntities.get(i+1).isInvalid() && emailEntities.get(i).getParamsList().get(0).equals("0")) {
					lastEnd = emailEntities.get(i);
					newStage = emailEntities.get(i+1);
				}
			}
				
			if(lastEnd != null && newStage != null) {
				if(GuaJiTime.getAM0Date(newStage.getCreateTime()).getTime() > lastEnd.getEffectTime().getTime()) {
					if(GuaJiTime.getAM0Date(newStage.getCreateTime()).getTime() < newStage.getEffectTime().getTime()) {
						int spaceDays = GuaJiTime.calcBetweenDays(newStage.getCreateTime(), newStage.getEffectTime());
						if(spaceDays > 0) {
							String sql = String.format(UPDATE_SQL, spaceDays, player.getId(), GuaJiTime.getTimeString(lastEnd.getCreateTime()));
							Log.logPrintln("fix monthcard , sql :" + sql);
							DBManager.getInstance().executeUpdate(sql);
						}
					}
				} else {
					int spaceDays = GuaJiTime.calcBetweenDays(lastEnd.getEffectTime(), newStage.getEffectTime());
					if(spaceDays > 1) {
						String sql = String.format(UPDATE_SQL, spaceDays - 1, player.getId(), GuaJiTime.getTimeString(lastEnd.getCreateTime()));
						Log.logPrintln("fix monthcard , sql :" + sql);
						DBManager.getInstance().executeUpdate(sql);
					}
				}
			}
		}
		return false;
	}

	public boolean onProtocol(AppObj appObj, Protocol protocol) 
	{
		if(protocol.getType() == HP.code.MAIL_INFO_C_VALUE) 
		{
			/*
			Player player = (Player)appObj;
			//修复月卡的期数
			MonthCardStatus monthCardStatus = ActivityUtil.getMonthCardStatus(player.getPlayerData());
			if(monthCardStatus != null) {
				// 捞取现在生效的月卡邮件
				List<EmailEntity> emes = DBManager.getInstance().query("from EmailEntity where playerId = ? and mailId = 7 and invalid = 0 order by id", player.getId());
				
//				List<Integer> monthCardIds = monthCardStatus.getMonthCardCfgIds();
				List<Integer> monthCardIds = null;
				monthCardIds.clear();
				int activeIdNum = emes.size() % 30 == 0 ? emes.size() / 30 : emes.size() / 30 + 1;
				
				for(int i=0;i<activeIdNum;i++) {
					monthCardIds.add(1);
				}

				Log.logPrintln("playerId:" + player.getId() + ", monthCardIds: " + GameUtil.join(monthCardIds,"_"));
				
				if(emes.size() > 0) {
					EmailEntity firtEmailEntity  = emes.get(0);
					firtEmailEntity.convertData();
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(firtEmailEntity.getEffectTime().getTime());
					int leftDays = Integer.valueOf(firtEmailEntity.getParamsList().get(0));
					calendar.add(Calendar.DATE, (leftDays - 29));
					monthCardStatus.setStartDate(calendar.getTime());
				}else{
					monthCardStatus.setStartDate(null);
				}
				player.getPlayerData().updateActivity(Const.ActivityId.MONTH_CARD_VALUE, 0);
			}
		}
	
		return false;
		*/
		
		}
		return false;
	}
}
