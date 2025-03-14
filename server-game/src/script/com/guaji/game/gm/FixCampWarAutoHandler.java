package com.guaji.game.gm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.guaji.db.DBManager;
import org.guaji.os.GuaJiTime;
import org.guaji.script.GuaJiScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.CampWarAutoJoinEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.manager.CampWarManager;
import com.guaji.game.manager.MailManager;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Mail.MailType;
import com.sun.net.httpserver.HttpExchange;

public class FixCampWarAutoHandler  extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {
		Logger logger = LoggerFactory.getLogger("Server");
		int lastAvgCoins = CampWarManager.getInstance().getLastAvgCoins();
		int lastAvgReputations = CampWarManager.getInstance().getLastAvgReputation();
		int lastStageId = calcLastWarStageId();
		logger.info("lastAvgCoins : {}, lastAvgReputations :{}, lastStageId: {}",
				lastAvgCoins, lastAvgReputations, lastStageId);
		
		// 给自动阵营战的玩家发平均奖励
		List<CampWarAutoJoinEntity> campWarAutoJoinEntities = DBManager.getInstance().query("from CampWarAutoJoinEntity where stageId = ?", lastStageId);
		if(campWarAutoJoinEntities.size() > 0){
			CampWarAutoJoinEntity tmp = campWarAutoJoinEntities.get(0);
			tmp.convertAutoJoinMap();
			ConcurrentHashMap<Integer, Integer> autoCampWarPlayerIds = tmp.getAutoCampWarPlayerIds();
			logger.info("auto playerId : {}", autoCampWarPlayerIds.keySet());
			AwardItems awardItems = new AwardItems();
			awardItems.addCoin(lastAvgCoins);
			awardItems.addReputationValue(lastAvgReputations);
			for(int playerId : autoCampWarPlayerIds.keySet()){
				MailManager.createMail(playerId, MailType.Reward_VALUE, GsConst.MailId.CAMPWAR_AUTO_AWARDS, "阵营战投资奖励", awardItems, "2014-10-31");
			}
		}
	}
	
	public int calcLastWarStageId() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		long todayAM0Time = GuaJiTime.getAM0Date().getTime();
		SysBasicCfg sysCfg = SysBasicCfg.getInstance();
		int todayBattleStopTime  = (int)(todayAM0Time/1000) + sysCfg.getCampWarBattleStopTime();
		int curSeconds = GuaJiTime.getSeconds();
		if(curSeconds < todayBattleStopTime){
			Calendar calendar = GuaJiTime.getCalendar();
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			String date = sdf.format(calendar.getTime());
			return Integer.valueOf(date);
		} else {
			String date = sdf.format(GuaJiTime.getAM0Date());
			return Integer.valueOf(date);
		}
	}
}
