package com.test.proto;

import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.AbstractMessage.Builder;
import com.guaji.game.protocol.HP;

public class ProtoGenerorManager {
	private static Map<Integer, IProtoGeneror> protoMaps = new HashMap<Integer, IProtoGeneror>();
	static{
		protoMaps.put(HP.sys.HEART_BEAT_VALUE, new Proto_Heartbeat());//3
		protoMaps.put(HP.code.LOGIN_C_VALUE,new Proto_Login());//1000
		protoMaps.put(HP.code.MERCENERY_EXPEDITION_INFO_C_VALUE, new Proto_ME());//84407
//		protoMaps.put(HP.code.BATTLE_FAST_FIGHT_C_VALUE, new Proto_FastBattle());//11001
		protoMaps.put(HP.code.EQUIP_SMELT_C_VALUE, new Proto_EquipSmelt());//5019
		protoMaps.put(HP.code.ARENA_RANKING_LIST_C_VALUE, new Proto_ArenaRankList());//8007
		protoMaps.put(HP.code.SHOP_ITEM_C_VALUE, new Proto_ShopItem());//10001
		protoMaps.put(HP.code.PROF_RANK_LIST_C_VALUE, new Proto_ProRank());//18001
		protoMaps.put(HP.code.MAIL_INFO_C_VALUE,new Proto_Mail());//13001
		protoMaps.put(HP.code.ACTION_INTO_RECORD_C_VALUE, new Proto_ActionRecord());//90001
		protoMaps.put(HP.code.RED_POINT_LIST_C_VALUE, new Proto_RedPoint());
		protoMaps.put(HP.code.FETCH_SHOP_LIST_C_VALUE, new Proto_FetchShopList());//16003
		protoMaps.put(HP.code.SEND_CHAT_C_VALUE, new Proto_Chat());//200
		protoMaps.put(HP.code.ROLE_CREATE_C_VALUE, new Proto_CreateRole());
	}
	
	public static int commands[] = 
		{
				HP.code.MERCENERY_EXPEDITION_INFO_C_VALUE,
//				HP.code.BATTLE_FAST_FIGHT_C_VALUE,
				HP.code.EQUIP_SMELT_C_VALUE,
				HP.code.ARENA_RANKING_LIST_C_VALUE,
				HP.code.SHOP_ITEM_C_VALUE,
				HP.code.PROF_RANK_LIST_C_VALUE,
				HP.code.MAIL_INFO_C_VALUE,
//				HP.code.ACTION_INTO_RECORD_C_VALUE,
//				HP.code.RED_POINT_LIST_C_VALUE,
				HP.code.FETCH_SHOP_LIST_C_VALUE,
//				HP.code.SEND_CHAT_C_VALUE
		};
	
	
	public static IProtoGeneror getBuilder(int command){
		return protoMaps.get(command);
	}
	
}
