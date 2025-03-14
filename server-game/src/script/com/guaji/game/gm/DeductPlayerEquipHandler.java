package com.guaji.game.gm;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.guaji.app.AppObj;
import org.guaji.db.DBManager;
import org.guaji.msg.Msg;
import org.guaji.obj.ObjBase;
import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.GsConfig;
import com.guaji.game.entity.EquipEntity;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsConst;
import com.sun.net.httpserver.HttpExchange;

/**
 * 文件内容格式 : android_1_102713_141733886_2_1_0 
 * 	 平台 _ serverId _ playerId _ equipId _ addExp _ usedNum1 _ usedNum2
 * 
 * 脚本命令格式 : curl 'localhost:5132/deduckplayerequip?&user=hawk'
 *
 */
public class DeductPlayerEquipHandler extends GuaJiScript {

	private Map<Integer, Map<Long, Integer[]>> playerEquipMap = new ConcurrentHashMap<Integer, Map<Long, Integer[]>>();
	private static final String path = "/tmp/fuckersEquip.txt";

	@Override
	public void action(String params, HttpExchange httpExchange) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] split = line.split("_");
				int serverId = Integer.parseInt(split[1]);
				if (!split[0].equals(GsConfig.getInstance().getPlatform())
						|| !(serverId == GsConfig.getInstance().getServerId())) {
					continue;
				}
				int playerId = Integer.parseInt(split[2]);
				long equipId = Long.parseLong(split[3]);
				Integer[] usedNum = { (Integer) Integer.parseInt(split[5]), (Integer) Integer.parseInt(split[6]) };
				addPlayerData(playerId, equipId, usedNum);
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		reduceEquipExp();

		GuaJiScriptManager.sendResponse(httpExchange, "{\"status\":1}");
	}

	/**
	 * 执行装备回滚;
	 */
	private void reduceEquipExp() {
		for (Integer playerId : playerEquipMap.keySet()) {
			GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId);
			Player player = null;
			boolean isOnline = false;
			ObjBase<GuaJiXID, AppObj> objBase = GsApp.getInstance().lockObject(xid);
			try {
				if (objBase != null && objBase.isObjValid()) {
					player = (Player) objBase.getImpl();
					if (player != null) {
						Msg equipMsg = Msg.valueOf(GsConst.MsgType.REDUCE_PLAYER_EQUIP_EXP);
						equipMsg.pushParam(playerEquipMap.get(playerId));
						GsApp.getInstance().postMsg(xid, equipMsg);
						isOnline = true;
					}
				}
			} finally {
				if (objBase != null) {
					objBase.unlockObj();
				}
			}
			if (!isOnline) {
				List<EquipEntity> equipEntities = DBManager.getInstance().query(
						"from EquipEntity where playerId = ? and invalid = 0 order by starExp desc", playerId);
				rollbackEquip(playerEquipMap.get(playerId), equipEntities, playerId);
			}
		}

	}

	/**
	 * put进出错玩家;
	 * 
	 * @param playerId
	 * @param equipId
	 * @param usedNum
	 */
	private void addPlayerData(int playerId, long equipId, Integer[] usedNum) {
		if (playerEquipMap.containsKey(playerId)) {
			addEquipData(playerEquipMap.get(playerId), equipId, usedNum);
		} else {
			Map<Long, Integer[]> equipMap = new ConcurrentHashMap<Long, Integer[]>();
			equipMap.put(equipId, usedNum);
			playerEquipMap.put(playerId, equipMap);
		}
	}

	/**
	 * put进玩家出错装备;
	 * 
	 * @param map
	 * @param equipId
	 * @param usedNum
	 */
	private void addEquipData(Map<Long, Integer[]> map, long equipId, Integer[] usedNum) {
		if (map.containsKey(equipId)) {
			Integer[] usedNum1 = { map.get(equipId)[0] + usedNum[0], map.get(equipId)[1] + usedNum[1] };
			map.put(equipId, usedNum1);
		} else {
			map.put(equipId, usedNum);
		}

	}

	/**
	 * 扣除经验;
	 * 
	 * @param equipEntity
	 * @param usedEquipNum
	 * @return
	 */
	private boolean deductEquipExp(EquipEntity equipEntity, Integer[] usedEquipNum, long oldEquipId, int playerId) {
		int starType = 0;
		int beforeExp = 0;
		int beforeExp2 = 0;

		if (equipEntity.getStarExp() < usedEquipNum[0] || equipEntity.getStarExp2() < usedEquipNum[1]) {
			return false;
		}
		beforeExp = equipEntity.getStarExp();
		equipEntity.setStarExp(equipEntity.getStarExp() - usedEquipNum[0]);
		beforeExp2 = equipEntity.getStarExp2();
		equipEntity.setStarExp2(equipEntity.getStarExp2() - usedEquipNum[1]);
		equipEntity.refreshStarLevel();
		equipEntity.notifyUpdate(false);
		BehaviorLogger.log4RollBack(playerId, Source.PLAYER_ATTR_CHANGE, Action.ROLLBACK_EQUIP_EXP,
				Params.valueOf("equipId", equipEntity.getId()), Params.valueOf("starType", starType),
				Params.valueOf("equipItemId", equipEntity.getEquipId()), Params.valueOf("beforeExp1", beforeExp),
				Params.valueOf("deductExp1", usedEquipNum[0]), Params.valueOf("afterExp1", equipEntity.getStarExp()),
				Params.valueOf("beforeExp2", beforeExp2), Params.valueOf("deductExp2", usedEquipNum[1]),
				Params.valueOf("afterExp2", equipEntity.getStarExp2()), Params.valueOf("oldEquipId", oldEquipId));
		return true;
	}

	/**
	 * 分别扣除普通神器经验和声望神器经验;
	 * 
	 * @param equipEntitys
	 * @param usedEquipNum
	 * @param oldEquipId
	 * @param playerId
	 * @return
	 */
	private boolean deductEquipExp(EquipEntity[] equipEntitys, Integer[] usedEquipNum, long oldEquipId, int playerId) {
		Integer[] starExp1 = { usedEquipNum[0], 0 };
		Integer[] starExp2 = { 0, usedEquipNum[1] };
		if (deductEquipExp(equipEntitys[0], starExp1, oldEquipId, playerId)
				&& deductEquipExp(equipEntitys[1], starExp2, oldEquipId, playerId)) {
			return true;
		}
		return false;
	}

	/**
	 * 根据传入数据,回滚神器经验;
	 * 
	 * @param equipAndNum
	 */
	protected void rollbackEquip(Map<Long, Integer[]> equipAndNum, List<EquipEntity> equipEntities, int playerId) {
		for (EquipEntity equipEntity : equipEntities) {
			if (equipEntity != null && equipAndNum.containsKey(equipEntity.getId())) {
				if (deductEquipExp(equipEntity, equipAndNum.get(equipEntity.getId()), equipEntity.getId(), playerId)) {
					equipAndNum.remove(equipEntity.getId());
				}
			}
		}
		for (long equipId : equipAndNum.keySet()) {
			EquipEntity[] equipEntitys = getTopExpEquip(equipEntities, equipAndNum.get(equipId));
			if (equipEntitys != null && deductEquipExp(equipEntitys, equipAndNum.get(equipId), equipId, playerId)) {
				equipAndNum.remove(equipId);
			}
		}
		StringBuilder sb = new StringBuilder();
		if (!equipAndNum.isEmpty()) {
			for (long equipId : equipAndNum.keySet()) {
				sb.append(" equipId:" + equipId + ", deductExp1:" + equipAndNum.get(equipId)[0]
						+ ",deductExp2:" + equipAndNum.get(equipId)[1] + "; ");
			}
		}
		BehaviorLogger.log4RollBack(playerId, Source.PLAYER_ATTR_CHANGE, Action.ROLLBACK_EQUIP_EXP,
				Params.valueOf("isSuccess", equipAndNum.isEmpty()), Params.valueOf("failEquip", sb));
	}

	/**
	 * 获得最高经验的神器;
	 * 
	 * @return
	 */
	private EquipEntity[] getTopExpEquip(List<EquipEntity> equipEntities, Integer[] starType) {
		EquipEntity topEquip = equipEntities.get(0);
		EquipEntity topEquip2 = equipEntities.get(0);
		for (EquipEntity equipEntity : equipEntities) {
			if (equipEntity.getStarExp() > starType[0] || equipEntity.getStarExp2() > starType[1]) {
				if (topEquip.getStarExp() < equipEntity.getStarExp()) {
					topEquip = equipEntity;
				}
				if (topEquip2.getStarExp2() < equipEntity.getStarExp2()) {
					topEquip2 = equipEntity;
				}
			}
		}
		EquipEntity[] equips = { topEquip, topEquip2 };
		return equips;
	}

}
