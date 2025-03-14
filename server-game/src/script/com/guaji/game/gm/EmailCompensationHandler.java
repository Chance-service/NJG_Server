package com.guaji.game.gm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.guaji.db.DBManager;
import org.guaji.os.MyException;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.MailManager;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Mail.MailType;
import com.guaji.game.util.ConfigUtil;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;
import com.sun.net.httpserver.HttpExchange;

import net.sf.json.JSONObject;

/**
 * 邮件补偿
 */
public class EmailCompensationHandler extends GuaJiScript {

	// 参数解析: params=content:xxx;reward:xxx;playerIds:xxxx,xxxx;&user=efun
	//                  邮件内容        补偿信息       xxx表示给一个玩家发奖励;xxxx,xxxx,......表示给多个个玩家发奖励
	@Override
	public void action(String params, HttpExchange httpExchange) {
		
		JSONObject json = new JSONObject();
		
		Map<String, String> paramsMap = GuaJiScriptManager.paramsToMap(params);

		String content = paramsMap.get("content");
		String reward = paramsMap.get("reward");
		String strId = paramsMap.get("playerIds");
		String isAllStr = paramsMap.get("isAll");
		boolean isAll = Boolean.parseBoolean(isAllStr);
		if (null == content || null == reward || null == isAllStr) {
			json.put("status", "2");
			json.put("msg", "date is null: content " + content + ";reward " + reward + ";isAllStr " + isAll);
			GuaJiScriptManager.sendResponse(httpExchange, json.toString());
			return;
		}
		
		List<ItemInfo> itemList =  ItemInfo.valueListOf(reward);
		
		//check Item
		for (ItemInfo aInfo : itemList) {
			int itemType = GameUtil.convertToStandardItemType(aInfo.getType()) / GsConst.ITEM_TYPE_BASE;
			if ((itemType ==  Const.itemType.EQUIP_VALUE) || (itemType ==  Const.itemType.BADGE_VALUE)){
				if (aInfo.getQuantity() > 10) {
					json.put("status", "2");
					json.put("msg", "This item cannot be larger than 10, ItemType :" + String.valueOf(aInfo.getType()) );
					GuaJiScriptManager.sendResponse(httpExchange, json.toString());
					return;
				}
			} 
			if (!ConfigUtil.check(aInfo.getType(), aInfo.getItemId())) {
				json.put("status", "3");
				json.put("msg", "This item has not been defined yet :" +  aInfo.toString());
				GuaJiScriptManager.sendResponse(httpExchange, json.toString());
				return;
			}
		}
		
		// 解析playerId
		List<Integer> playerIdList = new ArrayList<Integer>();
		if(isAll){
			try {
				List<Integer> playerids = DBManager.getInstance().executeQuery("select id from player where invalid = 0 and loginTime > DATE_ADD(now(), INTERVAL -30 DAY)");
				if(playerids!=null){
					playerIdList = playerids;
				}
			} catch (Exception e) {
				// TODO: handle exception
				MyException.catchException(e);
			}
		}else{
			String playerIds[] = strId.split(",");
			// 数据类型转换和校验
			for (int i = 0; i < playerIds.length; i++) {
				String playerId = playerIds[i];
				try {
					Integer integer = Integer.valueOf(playerId);
					playerIdList.add(integer);
				} catch (Exception e) {
					json.put("status", "4");
					json.put("msg", "playerIds is wrongfulness : " + strId);
					GuaJiScriptManager.sendResponse(httpExchange, json.toString());
					return;
				}
			}
		}
		
		// 数据合法，那就发邮件吧！
		for (Integer _playerId : playerIdList) {
			AwardItems awardItems = AwardItems.valueOf(reward);
			MailManager.createMail(_playerId, MailType.Reward_VALUE, 0, content, awardItems);
		}
		
		// 日志记录
		BehaviorLogger.log4GM(strId, Source.GM_OPERATION, Action.GM_AWARD, Params.valueOf("reward", reward), Params.valueOf("msg", content));
		
		json.put("status", "1");
		json.put("msg", "Email compensation successfully");
		GuaJiScriptManager.sendResponse(httpExchange, json.toString());
	}

}
