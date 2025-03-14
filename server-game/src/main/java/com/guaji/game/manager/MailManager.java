package com.guaji.game.manager;

import java.util.Date;

import org.guaji.db.DBManager;
import org.guaji.msg.Msg;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.guaji.game.GsApp;
import com.guaji.game.ServerData;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.EmailEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.protocol.Const.NoticeType;
import com.guaji.game.protocol.Mail;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;

/**
 * 邮件管理器
 */
public class MailManager {

	/**
	 * 创建新邮件
	 */
	public static void createMail(int playerId, int mailType, int mailId, String title, AwardItems awardItems,
			String... params) {
		createMail(playerId, mailType, mailId, null, title, awardItems, GsConst.EmailClassification.COMMON, params);
	}

	/**
	 * 创建系统新邮件
	 */
	public static void createSysMail(int playerId, int mailType, int mailId, String title, AwardItems awardItems,
			String... params) {
		createMail(playerId, mailType, mailId, null, title, awardItems, GsConst.EmailClassification.SYSTEM, params);
	}

	/**
	 * 创建邮件
	 */
	public static void createMail(int playerId, int mailType, int mailId, Date effectTime, String title,
			AwardItems awardItems, int mailClassification, String... params) {

		// 是否是屏蔽的邮件id
		if (SysBasicCfg.getInstance().getScreenedMailIdList().contains(mailId))
			return;

		EmailEntity entity = new EmailEntity();
		entity.setPlayerId(playerId);
		entity.setType(mailType);
		entity.setTitle(title);
		entity.setMailId(mailId);
		if (effectTime != null) {
			entity.setEffectTime(effectTime);
		}
		if (awardItems == null) {
			entity.setContent("");
		} else {
			entity.setContent(awardItems.toDbString());
		}
		entity.setParamsValue(params);
		entity.setClassification(mailClassification);
		DBManager.getInstance().create(entity);

		// 日志记录
		BehaviorLogger.log4Service(playerId, Source.EMAIL_ADD, Action.EMAIL_CREATE,
				Params.valueOf("playerId", playerId), Params.valueOf("mailId", entity.getMailId()),
				Params.valueOf("mailType", mailType), Params.valueOf("title", entity.getTitle()),
				Params.valueOf("content", entity.getContent()));

		// 如果玩家在线要发通知
		if (ServerData.getInstance().isPlayerOnline(playerId)
				&& (GuaJiTime.getMillisecond() >= entity.getEffectTime().getTime())) {
			Msg msg = Msg.valueOf(GsConst.MsgType.MAIL_CREATE, GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId));
			msg.pushParam(entity);
			GsApp.getInstance().postMsg(msg);
		}
	}

	public static void createServerEmail(int mailType, int mailId, String title, AwardItems awardItems,
			String... params) {
		Session session = DBManager.getInstance().getSession();
		Transaction transaction = session.beginTransaction();

		StringBuilder sb = new StringBuilder();
		sb.append(
				"INSERT INTO email(playerId, type, title, content, classification, params,  mailId, effectTime, createTime) SELECT id,");
		sb.append("?,").append("?,").append("?,").append("?,").append("?,").append("?,'")
				.append(GuaJiTime.getTimeString()).append("','").append(GuaJiTime.getTimeString())
				.append("' from player where 1 = 1 ");
		SQLQuery query = session.createSQLQuery(sb.toString());
		query.setString(1, title);
		if (awardItems != null) {
			query.setString(2, awardItems.toString());
			query.setInteger(0, Mail.MailType.Reward_VALUE);
		} else {
			query.setString(2, "");
			query.setInteger(0, Mail.MailType.Normal_VALUE);
		}
		query.setInteger(3, GsConst.EmailClassification.COMMON);
		query.setString(4, GameUtil.join(params, ","));
		query.setInteger(5, mailId);
		query.executeUpdate();
		session.clear();
		transaction.commit();

		// 通知
		GuaJiXID xID = GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.CHAT);
		ChatManager chatManager = (ChatManager) GsApp.getInstance().queryObject(xID).getImpl();
		chatManager.postNotice(NoticeType.NEW_MAIL, 1);

	}
}
