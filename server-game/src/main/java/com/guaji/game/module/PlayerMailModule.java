package com.guaji.game.module;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.guaji.db.DBManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.MyException;
import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Const.ApplyAddAllianceUpEmail;
import com.guaji.game.protocol.Const.NoticeType;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.EmailEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.MailManager;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.ProtoUtil;
import com.guaji.game.util.TapDBUtil;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Mail;
import com.guaji.game.protocol.Mail.MailType;
import com.guaji.game.protocol.Mail.OPMailGet;
import com.guaji.game.protocol.Mail.OPMailGetRet;
import com.guaji.game.protocol.Mail.OPMailInfo;
import com.guaji.game.protocol.Mail.OPMailInfoRet;
import com.guaji.game.protocol.Notice.HPNotice;
import com.guaji.game.protocol.Notice.NoticeItem;
import com.guaji.game.protocol.Status;

/**
 * 邮件消息处理模块
 */
public class PlayerMailModule extends PlayerModule {
	
	/**
	 * 最后读取最大邮件的
	 */
	private int lastReadMaxEmailId = 0;
	
	/**
	 * 最后加载最大邮件的effectTime
	 */
	private long lastLoadMaxEmailTime = 0;

	public PlayerMailModule(Player player) {
		
		super(player);

		listenProto(HP.code.MAIL_INFO_C_VALUE);
		listenProto(HP.code.MAIL_GET_C_VALUE);

		listenMsg(GsConst.MsgType.MAIL_CREATE);
	}

	@Override
	public boolean onTick() {
		return super.onTick();
	}

	@Override
	public boolean onMessage(Msg msg) {
		
		if (msg.getMsg() == GsConst.MsgType.MAIL_CREATE) {
			EmailEntity emailEntity = msg.getParam(0);
			onMailCreate(emailEntity);
			return true;
		}
		return super.onMessage(msg);
	}

	/**
	 * 创建邮件
	 */
	private void onMailCreate(EmailEntity emailEntity) {
		
		emailEntity.setPlayerId(player.getId());
		player.getPlayerData().addEmaliEntity(emailEntity);
		// 推送前端显示
		lastLoadMaxEmailTime = getCurrentMaxTime();
		
		if(emailEntity.getType() == MailType.ARENA_ALL_VALUE){
			if(emailEntity.getMailId() == GsConst.MailId.ARENA_RANK_DROP_RECORD ||
			   emailEntity.getMailId() == GsConst.MailId.ARENA_CHALLANGE_SELF_FAIL) {
				pushMailNotice(1, NoticeType.ARENA_ALL_SIGNUP);
			}
		} else {
			pushMailNotice(1, NoticeType.NEW_MAIL);
		}
	}

	@Override
	public boolean onProtocol(Protocol protocol) {
		// 邮件协议初始化
		if (protocol.checkType(HP.code.MAIL_INFO_C)) {
			onMailInfo(protocol.parseProtocol(OPMailInfo.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.MAIL_GET_C)) {
			onReadMail(protocol.getType(), protocol.parseProtocol(OPMailGet.getDefaultInstance()));
			return true;
		}
		return super.onProtocol(protocol);
	}

	@Override
	protected boolean onPlayerLogin() {
		// 加载玩家邮件
		player.getPlayerData().loadEmailEntities();
		if(lastLoadMaxEmailTime == 0) {
			lastLoadMaxEmailTime = getCurrentMaxTime();
		}
		lastReadMaxEmailId = 0;
		try {
			// 时间配置有效性
			String strDate = SysBasicCfg.getInstance().getMergeTime();
			if (null == strDate || strDate.equals("")) {
				return true;
			}
			Date startDate = ActivityTimeCfg.DATE_FORMAT.parse(strDate);
			// 是否是合服后创建的
			if (player.getEntity().getCreateTime().after(startDate)) {
				return true;
			}
			if (player.getEntity().getLevel() <= SysBasicCfg.getInstance().getMergePlayerLevel()) {
				String rewards = SysBasicCfg.getInstance().getMergeServerRewards();
				rewards = String.format(rewards, SysBasicCfg.getInstance().getMergePlayerLevel() - player.getEntity().getLevel());
				if (null != rewards && !rewards.equals("")) {
					AwardItems awardItems = AwardItems.valueOf(rewards);
					MailManager.createSysMail(player.getEntity().getId(), Mail.MailType.Reward_VALUE, GsConst.MailId.MEGER_SERVER_REWARDS, "", awardItems);
				}
			}
		} catch (ParseException e) {
    		MyException.catchException(e);
		}
		
		return true;
	}

	@Override
	protected boolean onPlayerAssemble() {
		if (player.getPlayerData().getEmailEntities().size() > 0) {
			// 检测是否有新邮件
			for(EmailEntity emailEntity : player.getPlayerData().getEmailEntities().values()) {
				if(emailEntity == null) {
					continue;
				}
				if(emailEntity.getType() == MailType.Reward_VALUE  || (emailEntity.getMailId() >= GsConst.MailId.APPLY_ADD_ALLIANCE && emailEntity.getMailId() <= GsConst.MailId.EMAIL_FAILURE_NOT_OPER)) {
					pushMailNotice(1, NoticeType.NEW_MAIL);
					break;
				}
			}
			// 检测是否有新竞技记录
			for(EmailEntity emailEntity : player.getPlayerData().getEmailEntities().values()) {
				if((emailEntity.getMailId() == GsConst.MailId.ARENA_RANK_DROP_RECORD || emailEntity.getMailId() == GsConst.MailId.ARENA_CHALLANGE_SELF_FAIL) 
						&& emailEntity.getId() > player.getPlayerData().getStateEntity().getMaxArenaRecordId()){
					pushMailNotice(1, NoticeType.ARENA_ALL_SIGNUP);
					break;
				}
			}
		}
		return true;
	}
	
	/**
	 * 推送新邮件提醒
	 * @param count
	 */
	private void pushMailNotice(int count, NoticeType noticeType) {
		
		HPNotice.Builder noticeBuilder = HPNotice.newBuilder();
		NoticeItem.Builder noticeItemBuilder = NoticeItem.newBuilder();
		noticeItemBuilder.setCount(1);
		noticeItemBuilder.setNoticeType(noticeType);
		noticeBuilder.addNotices(noticeItemBuilder);
		sendProtocol(Protocol.valueOf(HP.code.NOTICE_PUSH_VALUE, noticeBuilder));
	}

	@Override
	protected boolean onPlayerLogout() {
		
		lastReadMaxEmailId = 0;
		return true;
	}
	
	/**
	 * 邮件信息
	 * @param protocol
	 * @return
	 */
	protected boolean onMailInfo(OPMailInfo protocol) {
		// 检查数据库是否有添加
		checkDbAdd();
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		Map<Integer, EmailEntity> myMails = player.getPlayerData().getEmailEntities();
		List<Integer> delMail = new ArrayList<>();
		// 检查竞技记录数量
		checkArenaAllRecord(delMail);
		// 檢查數量限制
		checkLimitAllMail(delMail);
		OPMailInfoRet.Builder builder = BuilderUtil.getMailInfoBuilder(player,delMail, myMails, lastReadMaxEmailId);
		if (stateEntity != null) {
			stateEntity.setLastReadMailTime(GuaJiTime.getCalendar().getTime());
			stateEntity.notifyUpdate(true);
		}
		lastReadMaxEmailId = getCurrentMaxId();
		
		Protocol retProtocol = Protocol.valueOf(HP.code.MAIL_INFO_S, builder);
		sendProtocol(ProtoUtil.compressProtocol(retProtocol));
		return true;
	}

	private int getCurrentMaxId() {
		int maxId = 0;
		for (Entry<Integer, EmailEntity> entry : player.getPlayerData().getEmailEntities().entrySet()) {
			if (entry.getValue().getId() > maxId) {
				maxId = entry.getValue().getId();
			}
		}
		return maxId;
	}

	private long getCurrentMaxTime() {
		long maxTime = 0;
		for (Entry<Integer, EmailEntity> entry : player.getPlayerData().getEmailEntities().entrySet()) {
			if (entry.getValue().getEffectTime().getTime() > maxTime) {
				maxTime = entry.getValue().getId();
			}
		}
		return maxTime;
	}
	
	protected boolean onReadMail(int hpCode, OPMailGet protocol) {
		int type = 0;
		if(protocol.hasType()){
			type = protocol.getType();
			int emailClassify = 0;
			if(protocol.hasMailClassify()) {
				emailClassify = protocol.getMailClassify();
			}
			if(emailClassify <= 0 || emailClassify > 2) {
				sendError(hpCode, Status.error.PARAMS_INVALID_VALUE);
				return false;
			}
			if(type == 1) {
				// 删除所有
				List<EmailEntity> emailEntities = player.getPlayerData().getAllExpactRewardmailEntities(emailClassify);
				for(EmailEntity emailEntity : emailEntities) {
					if(emailEntity.getMailId() == GsConst.MailId.APPLY_ADD_ALLIANCE) {
						continue;
					}
					if (player.getPlayerData().removeEmailEntity(emailEntity)) {
						if (emailEntity.getContent() != null && !"".equals(emailEntity.getContent())) {
							AwardItems mailItems = AwardItems.valueOf(emailEntity.getContent());
							mailItems.rewardTakeAffectAndPush(player, Action.MAIL_REWARD_TOOLS,1,TapDBUtil.TapDBSource.Mail_Reward,Params.valueOf("MailId",emailEntity.getMailId()));
						}
					}
					BehaviorLogger.log4Service(player, Source.EMAIL_REMOVE, Action.EMAIL_READ, 
							Params.valueOf("id", emailEntity.getId()),
							Params.valueOf("mailId", emailEntity.getMailId()),
							Params.valueOf("mailType", emailEntity.getType()), 
							Params.valueOf("title", emailEntity.getTitle()),
							Params.valueOf("content", emailEntity.getContent()));
				}
				OPMailGetRet.Builder mailGetBuilder = OPMailGetRet.newBuilder();
				mailGetBuilder.setId(0);
				mailGetBuilder.setType(1);
				mailGetBuilder.setMailClassify(emailClassify);
				sendProtocol(Protocol.valueOf(HP.code.MAIL_GET_S, mailGetBuilder));
				return true;
			} else if(type == 2){
				// 领取全部奖励
				List<EmailEntity> emailEntities = player.getPlayerData().getAllRewardEmailEntities(emailClassify);
				AwardItems awardItems = new AwardItems();
				for(EmailEntity emailEntity : emailEntities) {
					if (player.getPlayerData().removeEmailEntity(emailEntity)) {
						if (emailEntity.getContent() != null && !"".equals(emailEntity.getContent())) {
							awardItems.appendAward(AwardItems.valueOf(emailEntity.getContent()));
						}
					}
					
					BehaviorLogger.log4Service(player, Source.EMAIL_REMOVE, Action.EMAIL_READ, 
							Params.valueOf("id", emailEntity.getId()),
							Params.valueOf("mailId", emailEntity.getMailId()),
							Params.valueOf("mailType", emailEntity.getType()), 
							Params.valueOf("title", emailEntity.getTitle()),
							Params.valueOf("content", emailEntity.getContent()));
				}
				awardItems.rewardTakeAffectAndPush(player, Action.EMAIL_READ,1);
				OPMailGetRet.Builder mailGetBuilder = OPMailGetRet.newBuilder();
				mailGetBuilder.setId(0);
				mailGetBuilder.setType(2);
				mailGetBuilder.setMailClassify(emailClassify);
				sendProtocol(Protocol.valueOf(HP.code.MAIL_GET_S, mailGetBuilder));
				return true;
			}
		}
		
		if(type == 0 && protocol.getId() <= 0) {
			sendError(hpCode, Status.error.PARAMS_INVALID_VALUE);
			return false;
		}
		
		EmailEntity emailEntity = player.getPlayerData().getEmailById(protocol.getId());
		if (emailEntity == null) {
			sendError(hpCode, Status.error.MAIL_NOT_FOUND);
			return false;
		}

		// 移除邮件并发奖
		if (player.getPlayerData().removeEmailEntity(emailEntity)) {
			if (emailEntity.getContent() != null && !"".equals(emailEntity.getContent())) {
				AwardItems mailItems = AwardItems.valueOf(emailEntity.getContent());
				mailItems.rewardTakeAffectAndPush(player, Action.MAIL_REWARD_TOOLS,1,TapDBUtil.TapDBSource.Mail_Reward,Params.valueOf("MailId",emailEntity.getMailId()));
			}
		}

		OPMailGetRet.Builder mailGetBuilder = OPMailGetRet.newBuilder();
		mailGetBuilder.setId(emailEntity.getId());
		sendProtocol(Protocol.valueOf(HP.code.MAIL_GET_S, mailGetBuilder));
		
		BehaviorLogger.log4Service(player, Source.EMAIL_REMOVE, Action.EMAIL_READ, 
				Params.valueOf("mailId", emailEntity.getId()),
				Params.valueOf("mailType", emailEntity.getType()), 
				Params.valueOf("title", emailEntity.getTitle()),
				Params.valueOf("content", emailEntity.getContent()));
		
		return true;
	}

	/**
	 * 检查数据库是否有新邮件
	 */
	public void checkDbAdd() {
		List<EmailEntity> emailEntities = DBManager.getInstance()
				.query("from EmailEntity where playerId = ? and effectTime <= ? and effectTime > ? and invalid = 0", 
						player.getId(), GuaJiTime.getCalendar().getTime(), new Date(lastLoadMaxEmailTime));
		// 有新邮件 删除纯文字邮件 并且创建日期 3天前的
		Date curDate = GuaJiTime.getAM0Date();
		for (EmailEntity emailEntity : emailEntities) {
			emailEntity.convertData();
			
			if(emailEntity.getType() == Mail.MailType.ARENA_ALL_VALUE){
				switch(emailEntity.getMailId()){
				case GsConst.MailId.ARENA_RANK_UP:
					while(emailEntity.getParamsList().size() < 11){
						emailEntity.addParams(String.valueOf(0));
					}
					break;
				case GsConst.MailId.ARENA_CHALLANGE_SELF_FAIL:
					while(emailEntity.getParamsList().size() < 8){
						emailEntity.addParams(String.valueOf(0));
					}
					break;
				case GsConst.MailId.ARENA_CHALLANGE_OTHER_FAIL:
					while(emailEntity.getParamsList().size() < 8){
						emailEntity.addParams(String.valueOf(0));
					}
					break;
				case GsConst.MailId.ARENA_RANK_DROP_RECORD:
					while(emailEntity.getParamsList().size() < 10){
						emailEntity.addParams(String.valueOf(0));
					}
					break;
				}
			}
			player.getPlayerData().addEmaliEntity(emailEntity);
			//20210527 增加競技場獎勵mailid==1超過三天(EmailDelDay)時會刪除郵件
			if((emailEntity.getType() != Mail.MailType.Reward_VALUE || emailEntity.getMailId() == GsConst.MailId.ARENA_AWARD)&& 
					GuaJiTime.calcBetweenDays(curDate, emailEntity.getCreateTime()) > SysBasicCfg.getInstance().getEmailDelDay()) {
				if(emailEntity.getMailId() == GsConst.MailId.APPLY_ADD_ALLIANCE) {
					// 玩家公会数据
					PlayerAllianceEntity playerAlliance = player.getPlayerData().getPlayerAllianceEntity();
					if(playerAlliance != null) {
						// 公会数据
						AllianceEntity entity = AllianceManager.getInstance().getAlliance(playerAlliance.getAllianceId());
						//如果当前会长还存在未确认的申请加入邮件，则进行删除
						AllianceManager.getInstance().deleteApplyAddAllianceMsg(player, player.getId(), entity, ApplyAddAllianceUpEmail.UPEMAIL_TYPE_1_VALUE, emailEntity);
					}
				}
				player.getPlayerData().removeEmailEntity(emailEntity);
			}
		}
		lastLoadMaxEmailTime = getCurrentMaxTime();
	}
	
	/**
	 * 检查竞技记录数量，达到上限时删除最老历史记录
	 */
	public void checkArenaAllRecord(List<Integer> delMail){
		Map<Integer, EmailEntity> myMails = player.getPlayerData().getEmailEntities();
		TreeSet<Integer> arenaAllIds = new TreeSet<Integer>();
		for(Map.Entry<Integer, EmailEntity> entry : myMails.entrySet()){
			EmailEntity entity = entry.getValue();
			if(entity.getType() == MailType.ARENA_ALL_VALUE ){
				arenaAllIds.add(entity.getId());
			}
		}
		
		// 删除超过的邮件数
		if(arenaAllIds.size() > SysBasicCfg.getInstance().getMaxArenaRankRecord()){
			int overCount = arenaAllIds.size() - SysBasicCfg.getInstance().getMaxArenaRankRecord();
			for(int i = 0; i < overCount; i++){
				Integer first = arenaAllIds.first();
				arenaAllIds.remove(first);
				myMails.get(first).delete();
				myMails.remove(first);
				delMail.add(first);
			}
		}
	}
	/**
	 * 檢查所有信件最大限制,刪除最老紀錄
	 */
	public void checkLimitAllMail(List<Integer> delMail) {
		Map<Integer, EmailEntity> myMails = player.getPlayerData().getEmailEntities();
		TreeSet<Integer> AllIds = new TreeSet<Integer>(myMails.keySet());
		// 删除超过的邮件数
		if(AllIds.size() > SysBasicCfg.getInstance().getMaxMailLimit()){
			int overCount = AllIds.size() - SysBasicCfg.getInstance().getMaxMailLimit();
			for(int i = 0; i < overCount; i++){
				Integer first = AllIds.first();
				AllIds.remove(first);
				myMails.get(first).delete();
				myMails.remove(first);
				delMail.add(first);
			}
		}
	}
}
