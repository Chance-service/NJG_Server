package com.guaji.game.module.sevendaylogin;

import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.msg.Msg;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.QuestCfg;
import com.guaji.game.config.SevenDayQuestCfg;
import com.guaji.game.entity.QuestEntity;
import com.guaji.game.entity.QuestItem;
import com.guaji.game.entity.SevenDayQuestEntity;
import com.guaji.game.entity.SevenDayQuestItem;
import com.guaji.game.module.quest.QuestEvent;
import com.guaji.game.protocol.Const.SevenDayEventType;
import com.guaji.game.util.GsConst;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年1月24日 下午5:32:50 类说明
 */
public class SevenDayQuestEventBus {

	/**
	 * Fire quest event;
	 * 
	 * @param eventType the quest event type;
	 * @param count     operate count;
	 * @param xid       xid;
	 */
	public static void fireQuestEvent(SevenDayEventType eventType, long count, GuaJiXID xid) {

		Msg questMsg = Msg.valueOf(GsConst.MsgType.SEVENDAY_EVENT);
		questMsg.pushParam(new SevenDayEvent(eventType, count));
		GsApp.getInstance().postMsg(xid, questMsg);
	}

	/**
	 * Fire quest event, and the operation count is 1;
	 * 
	 * @param eventType
	 * @param xid
	 */
	public static void fireQuestEventOneTime(SevenDayEventType eventType, GuaJiXID xid) {
		fireQuestEvent(eventType, 1, xid);
	}

	/**
	 * 离线任务完成处理
	 * 
	 * @param playerId
	 * @param questEvent
	 */
	public static void fireQuestEventWhenPlayerOffline(int playerId, SevenDayEventType questEvent, int questCount) {
		
		SevenDayQuestEntity questEntity = DBManager.getInstance().fetch(SevenDayQuestEntity.class, "from SevenDayQuestEntity where playerId = ? and invalid = 0",
					playerId);
			if (questEntity != null) {
				questEntity.loadQuest();
				for (SevenDayQuestItem eachQuest : questEntity.getQuestMap().values()) {
					eachQuest.initCounter(questEntity);
					
					SevenDayQuestCfg config = ConfigManager.getInstance().getConfigByKey(SevenDayQuestCfg.class, eachQuest.getId());
					if (config == null) {
						continue;
					}
					if (config.getTargetType() != questEvent.getNumber()) {
						continue;
					}
					eachQuest.onQuestEvent(new SevenDayEvent(questEvent,(long)questCount));
				}
			}
	}

}
