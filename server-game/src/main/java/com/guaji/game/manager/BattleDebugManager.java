package com.guaji.game.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.app.AppObj;
import org.guaji.msg.Msg;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.battle.Battlefield;
import com.guaji.game.battle.NewBattleRole;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Battle.NewBattleLog;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;

public class BattleDebugManager  extends AppObj {
	
	private static Map<Integer,List<NewBattleLog>> ManBattleProto;
	
	private static BattleDebugManager instance;
	
	public BattleDebugManager(GuaJiXID xid) {
		super(xid);
		instance = this;
	}
	
	public static BattleDebugManager getInstance() {
		return instance;
	}
	
	/**
	 * 初始化
	 */
	public void init() {
		ManBattleProto = new HashMap<>();
	}
	
	/**
	 * 清除某玩家戰鬥Log暫存
	 */
	public void clearlog(int playerId) {
		if (playerId == 0) {
			ManBattleProto.clear();
		} else {
			if (ManBattleProto.containsKey(playerId)){
				ManBattleProto.get(playerId).clear();
			}
		}
	}
	/**
	 * 加入log
	 */
	public void addProto(int playerId,NewBattleLog proto) {

		if (ManBattleProto.containsKey(playerId)){
			ManBattleProto.get(playerId).add(proto);
		} else {
			List<NewBattleLog> protoList = new ArrayList<>();
			protoList.add(proto);
			ManBattleProto.put(playerId,protoList);
		}
	
	}
	/**
	 * 取暫存協定
	 */
	public List<NewBattleLog> getProto(int playerId) {
		if (ManBattleProto.containsKey(playerId)) {
			return ManBattleProto.get(playerId);
		}
		return null;
	}
	
	/**
	 * 檢查是否重置戰場並重新檢查Log(server測試功能)
	 */
	public void RetestBattlefield(Player player,Battlefield battlefield) {
		if (battlefield == null) {
			return;
		}
		if (player == null) {
			return;
		}
		if ((battlefield.getBattleType() == GsConst.BattleType.LEVEL)) { // test PVE
			if (battlefield.IsRetest()) {
				Battlefield newBatf = new Battlefield();
				int mapId = battlefield.getMapId();
				//NewMapCfg mapCfg = player.getPlayerData().getCurBattleMap();
				Map<Integer,NewBattleRole> monsterRoles = new HashMap<Integer,NewBattleRole>();
				int pos = 0;
				int monsterId = 0;
				for (Map.Entry<Integer,NewBattleRole> entry :battlefield.getm_Defenders().entrySet()) {
					pos = entry.getKey();
					monsterId = entry.getValue().getitemId();
					RoleInfo.Builder monsterRole = BuilderUtil.genMonsterRoleInfoBuilder(monsterId, false);
					monsterRoles.put(pos,new NewBattleRole(monsterRole,pos));
				}
				newBatf.setMapId(mapId);
				newBatf.setBattleId(battlefield.getBattleId());
				newBatf.setm_Defenders(monsterRoles);
				newBatf.setBattleType(battlefield.getBattleType());
				//-----------------------------------------------------------------------
				int roleid = 0;
				Map<Integer,Integer> rolePosMap = new HashMap<>();
				for (Map.Entry<Integer,NewBattleRole> entry :battlefield.getm_Attackers().entrySet()) {
					pos = entry.getKey();
					roleid = entry.getValue().getitemId();
					if (pos != 0) {
						rolePosMap.put(roleid, pos);
					}
				}
				Map<Integer,NewBattleRole> Heroes = player.getPlayerData().getNewBattleHeroes(rolePosMap);
				newBatf.setm_Attackers(Heroes);
				newBatf.setBattleStandby(true);
				// 置換新戰場
				battlefield = newBatf;
			}
		}
	}
	
	public void retryBattle(int playerId) {
		if (playerId == 0) {
			return ;
		}
		
		if (!ManBattleProto.containsKey(playerId)) {
			return ;
		}
		
		Player onlineplayer = PlayerUtil.queryPlayer(playerId);
		if (onlineplayer == null) {
			return ;
		}

		GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId);
		Msg aMsg = Msg.valueOf(GsConst.MsgType.BATTLE_REPEAT, xid);
		GsApp.getInstance().postMsg(aMsg);
	}
}
