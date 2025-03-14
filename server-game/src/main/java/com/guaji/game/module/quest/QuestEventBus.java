package com.guaji.game.module.quest;


import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.msg.Msg;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.QuestCfg;
import com.guaji.game.entity.QuestEntity;
import com.guaji.game.entity.QuestItem;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const.QuestEventType;

/**
 * The quest eventBus;
 */
public class QuestEventBus {

	/**
	 * Fire quest event;
	 * 
	 * @param eventType
	 *            the quest event type;
	 * @param count
	 *            operate count;
	 * @param xid
	 *            xid;
	 */
	public static void fireQuestEvent(QuestEventType eventType, long count, GuaJiXID xid) {
		Msg questMsg = Msg.valueOf(GsConst.MsgType.QUEST_EVENT);
		questMsg.pushParam(new QuestEvent(eventType, count));
		GsApp.getInstance().postMsg(xid, questMsg);
	}

	/**
	 * Fire quest event, and the operation count is 1;
	 * 
	 * @param eventType
	 * @param xid
	 */
	public static void fireQuestEventOneTime(QuestEventType eventType, GuaJiXID xid) {
		fireQuestEvent(eventType, 1, xid);
	}

	/**
	 * 离线任务完成处理
	 * 
	 * @param playerId
	 * @param questEvent
	 */
	public static void fireQuestEventWhenPlayerOffline(int playerId, QuestEventType questEvent, int questCount) {
		// 离线任务数据操作
		QuestEntity questEntity = DBManager.getInstance().fetch(QuestEntity.class, "from QuestEntity where playerId = ? and invalid = 0",
				playerId);
		if (questEntity != null) {
			questEntity.loadQuest();
			for (QuestItem eachQuest : questEntity.getQuestItemMap().values()) {
				// init counter
				eachQuest.initCounter(questEntity);
				QuestCfg config = ConfigManager.getInstance().getConfigByKey(QuestCfg.class, eachQuest.getItemId());
				if (config == null) {
					continue;
				}
				if (config.getTargetType() != questEvent.getNumber()) {
					continue;
				}
				eachQuest.onQuestEvent(new QuestEvent(questEvent, questCount));
			}
		}
	}

}
