package com.guaji.game.gm;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.guaji.app.AppObj;
import org.guaji.cache.CacheObj;
import org.guaji.db.DBEntity;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.net.GuaJiNetManager;
import org.guaji.net.GuaJiSession;
import org.guaji.net.protocol.Protocol;
import org.guaji.obj.ObjManager;
import org.guaji.os.MyException;
import org.guaji.os.OSOperator;
import org.guaji.os.GuaJiTime;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.thread.GuaJiTask;
import org.guaji.util.services.ReportService;
import org.guaji.xid.GuaJiXID;

import com.google.gson.JsonObject;
import com.guaji.game.GsApp;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.entity.RechargeEntity;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Player.HPPlayerKickout;
import com.sun.net.httpserver.HttpExchange;

/**
 * 配置重新加载
 */
@SuppressWarnings("unused")
public class SysOperationHandler extends GuaJiScript {

	private void closeNet() {
		try {
			// 关闭网络
			GuaJiNetManager.getInstance().close();
		} catch (Exception e) {
			MyException.catchException(e);
		}
		
		Set<GuaJiSession> sessions = GsApp.getInstance().getActiveSessions();
		for (GuaJiSession session : sessions) {
			session.close(true);
		}
		OSOperator.osSleep(1000);
	}
	
	private void disablePlayerCache() {
		ObjManager<GuaJiXID, AppObj> objMan = GsApp.getInstance().getObjMan(GsConst.ObjType.PLAYER);
		objMan.setObjTimeout(0);
	}
	
	private void disableDbMultiThread() {
		Field dbMultiThreadField = OSOperator.getClassField(GsApp.getInstance().getAppCfg(), "dbMultiThread");
		if (dbMultiThreadField != null) {
			try {
				dbMultiThreadField.setAccessible(true);
				final Field modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
				modifiersField.setInt(dbMultiThreadField, dbMultiThreadField.getModifiers() & ~Modifier.FINAL);
				
				dbMultiThreadField.setBoolean(GsApp.getInstance().getAppCfg(), false);
				
				modifiersField.setInt(dbMultiThreadField, dbMultiThreadField.getModifiers() | Modifier.FINAL);
				modifiersField.setAccessible(false);
				dbMultiThreadField.setAccessible(false);
				
				//Log.logPrintln("dbmanager multi thread: " + GsApp.getInstance().getAppCfg().isDbMultiThread());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void setDbPeriod(int msTime) {
		Field asyncPeriodField = OSOperator.getClassField(DBManager.getInstance(), "asyncPeriod");
		if (asyncPeriodField != null) {
			asyncPeriodField.setAccessible(true);
			try {
				asyncPeriodField.setInt(DBManager.getInstance(), msTime);
				Log.logPrintln("dbmanager async period: " + asyncPeriodField.getInt(DBManager.getInstance()));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class EntitySaveTask extends GuaJiTask {
		DBEntity dbEntity = null;
		public EntitySaveTask(DBEntity entity) {
			this.dbEntity = entity;
		}
		
		@Override
		protected int run() {
			if (dbEntity != null) {
				DBManager.getInstance().update(dbEntity);
			}
			return 0;
		}

		@Override
		protected CacheObj clone() {
			return null;
		}
	}
	
	private void dbSaveWithTask(boolean commonTask) {
		try {
			Field entityListField = OSOperator.getClassField(DBManager.getInstance(), "entityList");
			if (entityListField != null) {
				entityListField.setAccessible(true);
				
				@SuppressWarnings("unchecked")
				List<DBEntity> entityList = (List<DBEntity>) entityListField.get(DBManager.getInstance());
				if (entityList != null) {
					try {
						for (DBEntity dbEntity : entityList) {
							dbEntity.isInvalid();
						}
					} catch (Exception e) {
					}
					
					try {
						Log.logPrintln("sysop entity total count: " + entityList.size());

						int entityCount = 0;
						for (DBEntity dbEntity : entityList) {
							if (commonTask) {
								GsApp.getInstance().postCommonTask(new EntitySaveTask(dbEntity));
							}
							entityCount ++;
						}
						
						Log.logPrintln("sysop entity post count: " + entityCount);
						entityList.clear();
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
	}
	
	protected void kickoutAll() {
		Set<GuaJiSession> sessions = GsApp.getInstance().getActiveSessions();
		for (GuaJiSession session : sessions) {
			if (session != null && session.isActive()) {
				try {
					HPPlayerKickout.Builder builder = HPPlayerKickout.newBuilder();
					builder.setReason(2);
					session.sendProtocol(Protocol.valueOf(HP.code.PLAYER_KICKOUT_S, builder));
				} catch (Exception e) {
					MyException.catchException(e);
				}
			}
		}
	}
	
	protected void showDBState(HttpExchange httpExchange) {
		String times = "";
//		LinkedList<DBEntity> entities = DBManager.getInstance().getAsyncList();
//		synchronized (entities) {
//			for (int i=1; i<=100; i++) {
//				times += ", " + entities.get(entities.size() - i).getEntityState().get();
//			}
//		}

		GuaJiScriptManager.sendResponse(httpExchange, "{\"status\": " + DBManager.getInstance().getAsyncList().size() + "}" + times);
	}
	
	protected void reportData(HttpExchange httpExchange) {
		int rechargeCount = 0;
		int registerCount = 0;
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			
			// 加载4号之后所有的充值
			List<RechargeEntity> rechargeEntities = DBManager.getInstance().query("from RechargeEntity where createTime >= ?", calendar.getTime());
//			for(RechargeEntity rechargeEntity : rechargeEntities) {
//				ReportService.RechargeData rechargeData = new ReportService.RechargeData();
//				rechargeData.setPuid(rechargeEntity.getPuid());
//				rechargeData.setDevice(rechargeEntity.getDevice());
//				rechargeData.setPlayerId(rechargeEntity.getPlayerId());
//				rechargeData.setPlayerName(rechargeEntity.getPlayerName());
//				rechargeData.setPlayerLevel(rechargeEntity.getLevel());
//				rechargeData.setOrderId(rechargeEntity.getOrderSerial());
//				rechargeData.setPayMoney(rechargeEntity.getGoodsCost());
//				rechargeData.setCurrency(rechargeEntity.getCurrency());
//				rechargeData.setTime(GuaJiTime.getTimeString(rechargeEntity.getCreateTime()));
//				ReportService.getInstance().report(rechargeData);
//				rechargeCount ++;
//			}
			rechargeCount = rechargeEntities==null?0:rechargeEntities.size();
			List<PlayerEntity> playerEntities = DBManager.getInstance().query("from PlayerEntity where createTime >= ?", calendar.getTime());
//			for(PlayerEntity playerEntity : playerEntities) {
//				ReportService.RegisterData registerData = new ReportService.RegisterData(playerEntity.getServerId()+"",playerEntity.getPuid(),
//							playerEntity.getDevice(), playerEntity.getId(), GuaJiTime.getTimeString(playerEntity.getCreateTime()));
//				ReportService.getInstance().report(registerData);
//				registerCount++;
//			}
			registerCount = playerEntities==null?0:playerEntities.size();
		} catch (Exception e) {
			MyException.catchException(e);
		}
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("status", 1);
		jsonObject.addProperty("registerCount", registerCount);
		jsonObject.addProperty("rechargeCount", rechargeCount);
		// 回复状态
		GuaJiScriptManager.sendResponse(httpExchange, jsonObject.toString());
	}
	
	@Override
	public void action(String params, HttpExchange httpExchange) {
		// closeNet();
		// dbSaveWithTask(true);
		// setDbPeriod(600000);
		// disablePlayerCache();
		
		// dbSaveWithTask(true);
		
		// disableDbMultiThread();
		
		reportData(httpExchange);
	}
}