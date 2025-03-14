package com.guaji.game.gm;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import org.guaji.db.DBManager;
import org.guaji.os.GuaJiTime;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.util.GuaJiTickable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guaji.game.GsApp;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.CampEntity;
import com.guaji.game.manager.CampWarManager;
import com.guaji.game.util.GsConst;
import com.sun.net.httpserver.HttpExchange;

public class FixCampResetHandler extends GuaJiScript{
	Logger logger = LoggerFactory.getLogger("Server");
	
	@Override
	public void action(String params, HttpExchange httpExchange) {
		GsApp.getInstance().addTickable(new GuaJiTickable(){
			public int lastTickTime =  (int)(GuaJiTime.getNextAM0Date()/1000);
			
			@Override
			public String getName() {
				return "fixcampreset";
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public void onTick() {
				if(GuaJiTime.getSeconds() >= lastTickTime){
					Field campMapField = getField(CampWarManager.getInstance(), "campMap");
					int curWarStageId = calcWarStageId();
					if (campMapField != null) {
						campMapField.setAccessible(true);
						Map<Integer, CampEntity> campMap;
						try {
							campMap = ((Map<Integer, CampEntity>)campMapField.get(CampWarManager.getInstance()));
							if(campMap.isEmpty()){
								// 创建下期阵营数据
								CampEntity leftCampEntity = new CampEntity(GsConst.CampWar.LEFT_CAMP_ID, curWarStageId);
								CampEntity rightCampEntity = new CampEntity(GsConst.CampWar.RIGHT_CAMP_ID, curWarStageId);
								DBManager.getInstance().create(leftCampEntity);
								DBManager.getInstance().create(rightCampEntity);
								campMap.put(leftCampEntity.getCampId(), leftCampEntity);
								campMap.put(rightCampEntity.getCampId(), rightCampEntity);
								logger.info("today campWar's campMap was fixed, stageId : ", curWarStageId);
							}
							this.lastTickTime = (int)(GuaJiTime.getNextAM0Date()/1000);
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}
				}
			}
		});
		
		GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":\"1\"}");
	}
	
	/**
	 * 计算本期阵营战期号
	 * @return
	 */
	private int calcWarStageId(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		int curSeconds = GuaJiTime.getSeconds();
		int todayShowStopTime = (int)(GuaJiTime.getAM0Date().getTime()/1000) + SysBasicCfg.getInstance().getCampWarShowStopTime();
		if(curSeconds > todayShowStopTime){
			Calendar calendar = GuaJiTime.getCalendar();
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			String date = sdf.format(calendar.getTime());
			return Integer.valueOf(date);
		} else {
			String date = sdf.format(GuaJiTime.getAM0Date());
			return Integer.valueOf(date);
		}
	}
	
	private Field getField(Object instance, String attrName) {
		Field field = null;
		if (field == null) {
			try {
				try {
					Class<?> instanceClass = instance.getClass();
					do {
						try {
							field = instanceClass.getField(attrName);
						} catch (Exception e) {
							instanceClass = instanceClass.getSuperclass();
						}
					} while (field == null);
				} catch (Exception e) {
					Class<?> instanceClass = instance.getClass();
					do {
						try {
							field = instanceClass.getDeclaredField(attrName);
						} catch (Exception ex) {
							instanceClass = instanceClass.getSuperclass();
						}
					} while (field == null);
				}
			} catch (Exception e) {
			}
		}
		return field;
	}
}
