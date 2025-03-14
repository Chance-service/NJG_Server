package com.guaji.game.gm;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.obj.ObjBase;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.GodlyLevelExpCfg;
import com.guaji.game.entity.EquipEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.manager.MailManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Mail;
import com.sun.net.httpserver.HttpExchange;

public class FixEquipHandler extends GuaJiScript {
	@Override
	public void action(String params, HttpExchange httpExchange) {
		String path = "/tmp/repair_re.txt";
		
		List<NeedToRepair> needToRepairs = new LinkedList<>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(path), "UTF-8"));
			
			String line = null;
			while((line = reader.readLine()) != null) {
				line = line.replace(":", "");
				String[] ss = line.split(" ");
				String v = ss[8];
				v = v.replace(",left", " ");
				v = v.replace(",costRe", " ");
				String[] vs = v.split(" ");
				int playerId = Integer.valueOf(vs[0]);
				int times = Integer.valueOf(vs[1]) / 1000;
				if(times == 0) {
					continue;
				}
				NeedToRepair needToRepair = new NeedToRepair();
				needToRepair.setPlayerId(playerId);
				needToRepair.setCreateTimes(times);
				
				needToRepairs.add(needToRepair);
			}
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(NeedToRepair needToRepair : needToRepairs) {
			int playerId = Integer.valueOf(needToRepair.getPlayerId());
			List<EquipEntity> equipEntities = DBManager.getInstance().limitQuery("from EquipEntity where playerId = ? and godlyAttrId2 > 0 and starExp2 > ?", 0, 1, playerId, needToRepair.getCreateTimes());
			if(equipEntities == null || equipEntities.size() == 0) {
				Log.logPrintln("deal with fixequip fail , playerId:" + playerId);
				continue;
			}
			long equipId = equipEntities.get(0).getId();
			int exp = equipEntities.get(0).getStarExp2() - Integer.valueOf(needToRepair.getCreateTimes());
			int smelt = Integer.valueOf(needToRepair.getCreateTimes() * 8000);
			int level = GodlyLevelExpCfg.getLevelByExp(exp);
			
			Log.logPrintln("deal with fixequip before,playerId: " + playerId + ",equipId:" + equipId + ",equipItemId:" + equipEntities.get(0).getEquipId()
					+ ",level:" + equipEntities.get(0).getStarLevel2() + ",exp:" + equipEntities.get(0).getStarExp2());
			
			boolean isOnline = false;
			GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId);
			Player player = null;
			ObjBase<GuaJiXID, AppObj> objBase = GsApp.getInstance().lockObject(xid);
			try {
				if (objBase != null && objBase.isObjValid()) {
					player = (Player) objBase.getImpl();
					if(player != null) {
						EquipEntity equipEntity = player.getPlayerData().getEquipById(equipId);
						if(equipEntity != null) {
							equipEntity.setStarLevel2(level);
							equipEntity.setStarExp2(exp);
							
							equipEntity.notifyUpdate(false);
						}
						
						isOnline = true;
					}
				}
			}finally{
				if(objBase != null) {
					objBase.unlockObj();
				}
			}
			
			if(!isOnline) {
				EquipEntity equipEntity = DBManager.getInstance().fetch(EquipEntity.class, "from EquipEntity where id = ?", equipId);
				if(equipEntity != null) {
					equipEntity.setStarLevel2(level);
					equipEntity.setStarExp2(exp);
					
					equipEntity.notifyUpdate(false);
				}
			}
			AwardItems awardItems = new AwardItems();
			awardItems.addSmeltValue(smelt);
			MailManager.createMail(playerId, Mail.MailType.Reward_VALUE, 0, "因战场挂机而获得的异常声望已经扣除，对于部分已经花费的声望会对神器经验进行扣除，相应的熔炼值已经返还，因此带来的不便敬请谅解。", awardItems);
			
			Log.logPrintln("deal with fixequip after,playerId: " + playerId + ",equipId:" + equipId + ",level:" + level + ",exp:" + exp);
		}
		GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
	}
}

class NeedToRepair {
	private int playerId ;
	private int createTimes ;
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getCreateTimes() {
		return createTimes;
	}
	public void setCreateTimes(int createTimes) {
		this.createTimes = createTimes;
	}
	
}
