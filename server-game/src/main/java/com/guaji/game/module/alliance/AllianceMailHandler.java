package com.guaji.game.module.alliance;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.guaji.app.AppObj;
import org.guaji.log.Log;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Alliance.HPAllianceEmailC;
import com.guaji.game.protocol.Alliance.HPAllianceEmailS;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.MailManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Mail;
import com.guaji.game.protocol.Status;


public class AllianceMailHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player)appObj;
		HPAllianceEmailC hpAllianceEmail = protocol.parseProtocol(HPAllianceEmailC.getDefaultInstance());
		int allianceId = player.getPlayerData().getPlayerAllianceEntity().getAllianceId(); 
		if(allianceId == 0){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NO_JOIN);
			return true;
		}
		AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
		if(allianceEntity == null){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NONEXISTENT);
			return true;
		}
		
		if(player.getPlayerData().getPlayerAllianceEntity().getPostion() != GsConst.Alliance.ALLIANCE_POS_MAIN){
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NO_MAIN);
			return true;
		}
		String content = hpAllianceEmail.getEmailContent();
		int contentLength = content.length();
		if(contentLength <= 0 || contentLength > 50){
			player.sendError(protocol.getType(), Status.error.MAIL_CONTENT_COUNT);
			return true;
		}
		
		int defaultSendMailNum = SysBasicCfg.getInstance().getSendEmailNum(); 
		int dalilySendMailNum = allianceEntity.getSendEmailNum();
		Date sendEmailTime = allianceEntity.getSendEmailTime();
		Set<Integer> memberSet = allianceEntity.getMemberList();
		if(!isSameDay(sendEmailTime)){
			dalilySendMailNum = 0;
			for (Integer playerId : memberSet) {
				MailManager.createMail(playerId, Mail.MailType.Normal_VALUE, GsConst.MailId.GUILDER_MAIL, "", null,content);
				Log.logPrintln("alliance captain send email to member ,playerId :" + playerId +" content: "+content);
			}
			allianceEntity.setSendEmailNum(++dalilySendMailNum);
			allianceEntity.setSendEmailTime(new Date());
			allianceEntity.notifyUpdate(true);
		}else{
			if(dalilySendMailNum >= defaultSendMailNum){
				player.sendError(protocol.getType(), Status.error.MAIL_SEND_NUM_ERROR);
				return true;
			}else{
				for (Integer playerId : memberSet) {
					MailManager.createMail(playerId, Mail.MailType.Normal_VALUE, GsConst.MailId.GUILDER_MAIL, "", null,content);
					Log.logPrintln("alliance captain send email to member ,playerId :" + playerId +" content: "+content);
				}
				allianceEntity.setSendEmailNum(++dalilySendMailNum);
				allianceEntity.setSendEmailTime(new Date());
				allianceEntity.notifyUpdate(true);
			}
		}
		
		HPAllianceEmailS.Builder builder = HPAllianceEmailS.newBuilder();
		int resultMsg = Status.error.MAIL_SEND_SUCCESS_VALUE;
		builder.setEmailSendResult(String.valueOf(resultMsg));
		player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_MAIL_S_VALUE, builder));
		return true;
	}

	public static boolean isSameDay(Date date){
		if(date == null){
			return false;
		}
		Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(new Date());
        boolean isSameDate = cal1.get(Calendar.DAY_OF_MONTH) == cal2
                        .get(Calendar.DAY_OF_MONTH);
        return isSameDate;
	}
	
	
}
