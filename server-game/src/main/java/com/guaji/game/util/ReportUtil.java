package com.guaji.game.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class ReportUtil {
	public static String sqlTimestamp2String(Timestamp timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		String date = sdf.format(timestamp.getTime());
		return date;
	}
	
	public static void reportData(String date) {
		// 新增数据
//		try {
//			List<Object> results = DBManager.getInstance().executeQuery(String.format("select serverId, puid, device, id, createTime from player where createTime like '%s%%'", date));
//			if (results != null) {
//				for (int i=0; i<results.size(); i++) {
//					try {
//						Object[] objArray = (Object[])results.get(i);
//						ReportService.RegisterData registerData = new ReportService.RegisterData((String)objArray[0],
//								(String)objArray[1], (String)objArray[2], (Integer)objArray[3], sqlTimestamp2String((Timestamp)objArray[4]));
//								
//								ReportService.getInstance().report(registerData);
//					} catch (Exception e) {
//						MyException.catchException(e);
//					}
//				}
//			}
//		} catch (Exception e) {
//			MyException.catchException(e);
//		}
//		
//		// 充值数据
//		try {
//			List<Object> results = DBManager.getInstance().executeQuery(String.format("select recharge.serverId, recharge.puid, recharge.device, playerId, name, recharge.level, orderSerial, goodsCost, currency, recharge.createTime from recharge, player where recharge.createTime like '%s%%' and playerId = player.id", date));
//			if (results != null) {
//				for (int i=0; i<results.size(); i++) {
//					try {
//						Object[] objArray = (Object[])results.get(i);
//						ReportService.RechargeData rechargeData = new ReportService.RechargeData();
//						/* 
//						(String)objArray[0], (String)objArray[1], (Integer)objArray[2], (String)objArray[3],
//						(Integer)objArray[4], (String)objArray[5], (Integer)objArray[6], (String)objArray[7], sqlTimestamp2String((Timestamp)objArray[8])
//						*/
//						rechargeData.setBirthServerId((String)objArray[0]);
//						rechargeData.setPuid((String)objArray[1]);
//						rechargeData.setDevice((String)objArray[2]);
//						rechargeData.setPlayerId((Integer)objArray[3]);
//						rechargeData.setPlayerName((String)objArray[4]);
//						rechargeData.setPlayerLevel((Integer)objArray[5]);
//						rechargeData.setOrderId((String)objArray[6]);
//						rechargeData.setPayMoney((Integer)objArray[7]);
//						rechargeData.setCurrency((String)objArray[8]);
//						rechargeData.setTime(sqlTimestamp2String((Timestamp)objArray[9]));
//								
//								ReportService.getInstance().report(rechargeData);
//					} catch (Exception e) {
//						MyException.catchException(e);
//					}
//				}
//			}
//		} catch (Exception e) {
//			MyException.catchException(e);
//		}
//		
//		// 登陆数据
//		try {
//			List<Object> results = DBManager.getInstance().executeQuery(String.format("select player.serverId, login.puid, player.device, playerId, period, login.createTime from login, player where login.createTime like '%s%%' and playerId = player.id", date));
//			if (results != null) {
//				for (int i=0; i<results.size(); i++) {
//					try {
//						Object[] objArray = (Object[])results.get(i);
//						ReportService.LoginData loginData = new ReportService.LoginData((String)objArray[0],"",
//								(String)objArray[1], (String)objArray[2], (Integer)objArray[3], (Integer)objArray[3],sqlTimestamp2String((Timestamp)objArray[5]));
//								
//								ReportService.getInstance().report(loginData);
//					} catch (Exception e) {
//						MyException.catchException(e);
//					}
//				}
//			}
//		} catch (Exception e) {
//			MyException.catchException(e);
//		}
	}
}
