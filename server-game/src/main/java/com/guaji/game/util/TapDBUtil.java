package com.guaji.game.util;

import org.guaji.cache.CacheObj;
import org.guaji.msg.Msg;
import org.guaji.thread.GuaJiTask;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.manager.TapDBManager;
import com.guaji.game.manager.CampWarManager.DeductAutoPlayerTask;
import com.guaji.game.player.Player;

import net.sf.json.JSONObject;

public class TapDBUtil {
	
    /**
     * 日志源
     */
    public static class TapDBSource {
    	/**
    	 * 未定義
    	 */
    	public static int None = 0;
    	/**
    	 * 信件獎勵
    	 */
    	public static int Mail_Reward = 101;
    	/**
    	 * 道具使用
    	 */
    	public static int ItemUse = 102;
    	/**
    	 * 金銀寶山
    	 */
    	public static int Shop_Buy_Coin = 103;
    	/**
    	 * 秘密信條
    	 */
    	public static int Secret_Message= 104;
    	/**
    	 * 好友
    	 */
    	public static int Friend = 105;
    	/**
    	 * 裝備合成
    	 */
    	public static int Equip_Forge = 106;
    	/**
    	 * 符石合成
    	 */
    	public static int Badge_Fusion = 107;
    	/**
    	 * 每日任務獎勵
    	 */
    	public static int Daily_Quest = 108;
    	/**
    	 * 每日任務進度點獎勵
    	 */
    	public static int Daily_Point = 109;
    	/**
    	 * 每週任務獎勵
    	 */
    	public static int Weekly_Quest = 110;
    	/**
    	 * 每週任務進度點獎勵
    	 */
    	public static int Weekly_Point = 111;
    	/**
    	 * 七日季獎勵
    	 */
    	public static int Seven_Day = 112;
    	/**
    	 * 七日季任務進度點獎勵
    	 */
    	public static int Seven_Day_Point = 113;
    	/**
    	 * 成就任務獎勵
    	 */
    	public static int Quest_Reward = 114;
    	/**
    	 * 300抽領獎
    	 */
    	public static int Activity180_SUMMON = 115;
    	/**
    	 * 900抽領獎
    	 */
    	public static int Activity167_SUMMON = 116;
    	/**
    	 * 登入獎勵領獎
    	 */
    	public static int Activity117_Login_Reward = 117;
    	/**
    	 * 儲值相關獲得
    	 */
    	public static int Recharge = 118;
    	/**
    	 * 每日商店
    	 */
    	public static int Daily_Shop = 119;
    	/**
    	 * 神秘商店
    	 */
    	public static int Mystery_Shop = 120;
    	/**
    	 * 競技商店
    	 */
    	public static int Arena_Shop = 121;
    	/**
    	 * 活動購買道具
    	 */
    	public static int Act_Exchange = 125;
    	/**
    	 * 通行證獲得道具
    	 */
    	public static int Pass_Port = 126;
    	/**
    	 * 月卡獲得道具
    	 */
    	public static int Month_Card = 127;
    	/**
    	 * 領取累儲累消獎勵
    	 */
    	public static int Recharage_Bounce = 128;
    	/**
    	 * 地下城
    	 */
    	public static int ELITE_Map = 201;
    	/**
    	 * 競技場
    	 */
    	public static int Arena_PvP = 202;
    	/*
    	 * 屬性迴廊
    	 */
    	public static int Dungeon_Map = 203;
    	/**
    	 * 排行榜領獎
    	 */
    	public static int Rank_Gift = 204;
    	/**
    	 * 循環關卡
    	 */
    	public static int Cycle_Stage = 205;
    	/**
    	 * 掛機獎勵
    	 */
    	public static int Battle_Reward = 301;
    	/**
    	 * 快速戰鬥
    	 */
    	public static int Fast_Fighting = 302;
    	/**
    	 * 主線戰鬥
    	 */
    	public static int Challenge_Boss = 303;
    	/**
    	 * 忍娘領鑽
    	 */
    	public static int Hero_drama_Gift = 401;
    	/**
    	 * 英雄重置
    	 */
    	public static int Hero_Reset_Lv = 402;
    	/**
    	 * 忍娘召喚
    	 */
    	public static int Chosen_One = 501;
    	/**
    	 * 專武召喚
    	 */
    	public static int Call_Of_Equip = 502;
    	/**
    	 * 友情召喚
    	 */
    	public static int Call_Of_Friendship = 503;
    	/**
    	 * 屬性召喚
    	 */
    	public static int Call_Of_Race = 504;
    	
    }
    
    public static void Event_Battle_PVE(Player player,int mapId,String teamStr,String result) {
		//  for tapDB result
		JSONObject jsonobj = new JSONObject();
		jsonobj.put(GsConst.tapDBPropertyName.uid,player.getTapDBUId());
		jsonobj.put(GsConst.tapDBPropertyName.battle_mapid,mapId);
		jsonobj.put(GsConst.tapDBPropertyName.battle_team,teamStr);
		String[] resultArray = result.split("#");
		
		if (resultArray.length >= 3) {
			jsonobj.put(GsConst.tapDBPropertyName.battle_result,resultArray[0]);
			jsonobj.put(GsConst.tapDBPropertyName.battle_result_atk,resultArray[1]);
			jsonobj.put(GsConst.tapDBPropertyName.battle_result_def,resultArray[2]);
		} else {
			jsonobj.put(GsConst.tapDBPropertyName.battle_result,resultArray[0]);
			jsonobj.put(GsConst.tapDBPropertyName.battle_result_atk,"");
			jsonobj.put(GsConst.tapDBPropertyName.battle_result_def,"");
		}
		
		Msg msg = Msg.valueOf(GsConst.MsgType.TAPDB_EVENT_RECORD);
		msg.pushParam(player);
		msg.pushParam(GsConst.tapDBEventName.event_battle_map);
		msg.pushParam(jsonobj);
		msg.pushParam(false);
		GuaJiXID targetXId = GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.TabDB_MANAGER);
		GsApp.getInstance().postMsg(targetXId, msg);
		
		//TapDBManager.getInstance().tapdbEvent(player,"",GsConst.tapDBEventName.event_battle_map, jsonobj,false);
    }
    
    public static void Event_Arena_PVP(Player player,String teamStr,String defStr,String result) {
		//  for tapDB result
		JSONObject jsonobj = new JSONObject();
		jsonobj.put(GsConst.tapDBPropertyName.uid,player.getTapDBUId());
		jsonobj.put(GsConst.tapDBPropertyName.battle_team,teamStr);
		jsonobj.put(GsConst.tapDBPropertyName.battle_team_def,defStr);

		String[] resultArray = result.split("#");
		
		if (resultArray.length >= 3) {
			jsonobj.put(GsConst.tapDBPropertyName.battle_result,resultArray[0]);
			jsonobj.put(GsConst.tapDBPropertyName.battle_result_atk,resultArray[1]);
			jsonobj.put(GsConst.tapDBPropertyName.battle_result_def,resultArray[2]);
		} else {
			jsonobj.put(GsConst.tapDBPropertyName.battle_result,resultArray[0]);
			jsonobj.put(GsConst.tapDBPropertyName.battle_result_atk,"");
			jsonobj.put(GsConst.tapDBPropertyName.battle_result_def,"");
		}
		
		Msg msg = Msg.valueOf(GsConst.MsgType.TAPDB_EVENT_RECORD);
		msg.pushParam(player);
		msg.pushParam(GsConst.tapDBEventName.event_battle_pvp);
		msg.pushParam(jsonobj);
		msg.pushParam(false);
		GuaJiXID targetXId = GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.TabDB_MANAGER);
		GsApp.getInstance().postMsg(targetXId, msg);
		
		//TapDBManager.getInstance().tapdbEvent(player,"",GsConst.tapDBEventName.event_battle_pvp, jsonobj,false);
    }
	
	public static void Event_GloryHole_Start(Player player,String tapuid,int playtimes,int teamType,String usetitem) {
		//  for tapDB result
		JSONObject jsonobj = new JSONObject();
		jsonobj.put(GsConst.tapDBPropertyName.uid,tapuid);
		jsonobj.put(GsConst.tapDBPropertyName.gloryhole_times,playtimes);
		jsonobj.put(GsConst.tapDBPropertyName.gloryhole_type,teamType);
		jsonobj.put(GsConst.tapDBPropertyName.gloryhole_useitem,usetitem);
		
		Msg msg = Msg.valueOf(GsConst.MsgType.TAPDB_EVENT_RECORD);
		msg.pushParam(player);
		msg.pushParam(GsConst.tapDBEventName.event_gloryhole_start);
		msg.pushParam(jsonobj);
		msg.pushParam(false);
		GuaJiXID targetXId = GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.TabDB_MANAGER);
		GsApp.getInstance().postMsg(targetXId, msg);
		
		//TapDBManager.getInstance().tapdbEvent(player,"",GsConst.tapDBEventName.event_gloryhole_start, jsonobj,false);
	}
	
	public static void Event_GloryHole_Result(Player player,String tapuid,int playtimes,int teamType,String usetitem,int score) {
		//  for tapDB result
		JSONObject jsonobj = new JSONObject();
		jsonobj.put(GsConst.tapDBPropertyName.uid,tapuid);
		jsonobj.put(GsConst.tapDBPropertyName.gloryhole_times,playtimes);
		jsonobj.put(GsConst.tapDBPropertyName.gloryhole_type,teamType);
		jsonobj.put(GsConst.tapDBPropertyName.gloryhole_useitem,usetitem);
		jsonobj.put(GsConst.tapDBPropertyName.gloryhole_score,score);
		
		Msg msg = Msg.valueOf(GsConst.MsgType.TAPDB_EVENT_RECORD);
		msg.pushParam(player);
		msg.pushParam(GsConst.tapDBEventName.event_gloryhole_result);
		msg.pushParam(jsonobj);
		msg.pushParam(false);
		GuaJiXID targetXId = GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.TabDB_MANAGER);
		GsApp.getInstance().postMsg(targetXId, msg);
		
		//TapDBManager.getInstance().tapdbEvent(player,"",GsConst.tapDBEventName.event_gloryhole_result, jsonobj,false);
	}
	
	public static void Event_SecretMessage(Player player,String tapuid,int heroId ,int messageId) {
		//  for tapDB result
		JSONObject jsonobj = new JSONObject();
		jsonobj.put(GsConst.tapDBPropertyName.uid,tapuid);
		jsonobj.put(GsConst.tapDBPropertyName.hero_id,heroId);
		jsonobj.put(GsConst.tapDBPropertyName.message_id,messageId);
		
		Msg msg = Msg.valueOf(GsConst.MsgType.TAPDB_EVENT_RECORD);
		msg.pushParam(player);
		msg.pushParam(GsConst.tapDBEventName.event_secretmessage);
		msg.pushParam(jsonobj);
		msg.pushParam(false);
		GuaJiXID targetXId = GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.TabDB_MANAGER);
		GsApp.getInstance().postMsg(targetXId, msg);
		
		//TapDBManager.getInstance().tapdbEvent(player,"",GsConst.tapDBEventName.event_secretmessage, jsonobj,false);
	}
	
	public static void Event_UsetItem(Player player,String tapuid,int actionId,int itemdType,int itemId , int itemCount ,Params... params) {
		//  for tapDB result
		JSONObject jsonobj = new JSONObject();
		jsonobj.put(GsConst.tapDBPropertyName.uid,tapuid);
		jsonobj.put(GsConst.tapDBPropertyName.use_action,actionId);
		
		String itemStr = String.format("%d_%d",itemdType,itemId);
		jsonobj.put(GsConst.tapDBPropertyName.item_id,itemStr);
		
		jsonobj.put(GsConst.tapDBPropertyName.count,itemCount);
				
        JSONObject paramsJsonObject = new JSONObject();
        for (Params param : params) {
            paramsJsonObject.put(param.getName(), param.getValue().toString());
        }
        
        String actionStr = TapDBString(paramsJsonObject.toString());
        
		jsonobj.put(GsConst.tapDBPropertyName.use_action_str,actionStr);
		
		Msg msg = Msg.valueOf(GsConst.MsgType.TAPDB_EVENT_RECORD);
		msg.pushParam(player);
		msg.pushParam(GsConst.tapDBEventName.event_use_item);
		msg.pushParam(jsonobj);
		msg.pushParam(false);
		GuaJiXID targetXId = GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.TabDB_MANAGER);
		GsApp.getInstance().postMsg(targetXId, msg);
		
		//TapDBManager.getInstance().tapdbEvent(player,"",GsConst.tapDBEventName.event_use_item, jsonobj,false);
	}
	
	public static void Event_GetItem(Player player,String tapuid,Action action,int actionId,int itemdType,int itemId , int itemCount ,Params... params) {
		//  for tapDB result
		JSONObject jsonobj = new JSONObject();
		jsonobj.put(GsConst.tapDBPropertyName.uid,tapuid);
		jsonobj.put(GsConst.tapDBPropertyName.get_action,actionId);
		
		String itemStr = String.format("%d_%d",itemdType,itemId);
		jsonobj.put(GsConst.tapDBPropertyName.item_id,itemStr);
		
		jsonobj.put(GsConst.tapDBPropertyName.count,itemCount);
		
		String actionStr = "empty";
		
        JSONObject paramsJsonObject = new JSONObject();
        
        if (params.length > 0) {
	        for (Params param : params) {
	            paramsJsonObject.put(param.getName(), param.getValue().toString());
	        }
	        actionStr = TapDBString(paramsJsonObject.toString());
        }
		
		jsonobj.put(GsConst.tapDBPropertyName.get_action_str,actionStr);
		
		Msg msg = Msg.valueOf(GsConst.MsgType.TAPDB_EVENT_RECORD);
		msg.pushParam(player);
		msg.pushParam(GsConst.tapDBEventName.event_get_item);
		msg.pushParam(jsonobj);
		msg.pushParam(false);
		GuaJiXID targetXId = GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.TabDB_MANAGER);
		GsApp.getInstance().postMsg(targetXId, msg);
		
		//TapDBManager.getInstance().tapdbEvent(player,"",GsConst.tapDBEventName.event_get_item, jsonobj,false);
	}
	
	public static String TapDBString(String aStr) {
		char oldCharacter = '{'; // replacing character
		char newCharacter = '['; // character to be replaced
		String replaceStr = aStr.replace(oldCharacter, newCharacter);
		oldCharacter = '}'; // replacing character
		newCharacter = ']'; // character to be replaced
		replaceStr = replaceStr.replace(oldCharacter, newCharacter);
		return replaceStr;
	}

}
