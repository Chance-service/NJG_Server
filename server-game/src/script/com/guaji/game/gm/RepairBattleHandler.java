package com.guaji.game.gm;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.guaji.app.AppObj;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.obj.ObjBase;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsConst;
import com.sun.net.httpserver.HttpExchange;

/**
 * 玩家定时修复帮主处理
 */
public class RepairBattleHandler extends GuaJiScript {
	
	@SuppressWarnings("resource")
	@Override
	public void action(String params, final HttpExchange httpExchange) {
		String path = "d://20141031.txt";
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(path), "UTF-8"));
			
			String line = null;
			while((line = reader.readLine()) != null) {
				String[] ss = line.split("\t");
				if(ss[ss.length - 1].equals("1")) {
					String playerIdStr = ss[1];
					//record it
					Log.logPrintln("has received , playerId:" + playerIdStr);
					int playerId = Integer.valueOf(playerIdStr) ;
					Player player = null;
					//10000_1002_3388630,10000_1011_15539
					String[] items = ss[5].split(",");
					ItemInfo coinItemInfo = new ItemInfo(items[0]);
					ItemInfo reItemInfo = new ItemInfo(items[1]);
					int costCoins = coinItemInfo.getQuantity();
					int costRe = reItemInfo.getQuantity();
					
					boolean isOnline = false;
					GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId);
					ObjBase<GuaJiXID, AppObj> objBase = GsApp.getInstance().lockObject(xid);
					try {
						if (objBase != null && objBase.isObjValid()) {
							player = (Player) objBase.getImpl();
							if(player != null) {
								int oldRe = player.getPlayerData().getPlayerEntity().getReputationValue();
								if(oldRe < costRe) {
									player.consumeReputationValue(oldRe, Action.GM_AWARD);	
									Log.logPrintln("has cost re : playerId :" + playerIdStr + ",left:" + 
									(costRe - oldRe) + ",costRe:" + costRe);
								}else{
									player.consumeReputationValue(costRe, Action.GM_AWARD);	
									Log.logPrintln("has not cost re : playerId :" + playerIdStr + ",costRe:" + costRe);
								}
								
								long oldCoins = player.getCoin();
								if(oldCoins < costCoins) {
									player.consumeCoin(oldCoins , Action.GM_AWARD);	
									Log.logPrintln("has cost coins : playerId :" + playerIdStr + ",left:" + 
									(costCoins - oldCoins) + ",costCoins:" + costCoins);
								}else{
									player.consumeCoin(costCoins, Action.GM_AWARD);	
									Log.logPrintln("has not cost coins : playerId :" + playerIdStr + ",costCoins:" + costCoins);
								}
								isOnline = true;
							}
						}
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						if (objBase != null) {
							objBase.unlockObj();
						}
					}
					
					if(!isOnline) {
						
						PlayerEntity playerEntity = DBManager.getInstance().fetch(PlayerEntity.class , "from PlayerEntity where id = ?", playerId);
						if(playerEntity != null) {
							if(playerEntity.getReputationValue() < costRe) {
								int oldRe = playerEntity.getReputationValue();
								playerEntity.setReputationValue(0);	
								Log.logPrintln("has cost re : playerId :" + playerIdStr + ",left:" + 
										(costRe - oldRe) + ",costRe:" + costRe);
							}else{
								playerEntity.setReputationValue(playerEntity.getReputationValue() - costRe);	
								Log.logPrintln("has not cost re : playerId :" + playerIdStr + ",costRe:" + costRe);
							}
							
							if(playerEntity.getCoin() < costCoins) {
								long oldCoins = playerEntity.getCoin();
								playerEntity.setCoin(0);
								Log.logPrintln("has cost coins : playerId :" + playerIdStr + ",left:" + 
								(costCoins - oldCoins) + ",costCoins:" + costCoins);
							}else{
								playerEntity.setCoin(playerEntity.getCoin() - costCoins);	
								Log.logPrintln("has not cost coins : playerId :" + playerIdStr + ",costCoins:" + costCoins);
							}
							
							playerEntity.notifyUpdate(false);
						}
						
					}
				}
					
			}
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 回复状态
		GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
	}
}
