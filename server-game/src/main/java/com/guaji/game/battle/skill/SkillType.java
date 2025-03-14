/**
 * 
 */
package com.guaji.game.battle.skill;

/**
 * 技能id集合
 */
public class SkillType {
	public static final int Eye_MARK = 100000; // 眼睛技能遮罩
	public static final int Eye_ID_100001 = 100001; // 當自身獲得3個(param1)以上debuff，立即驅散身上所有可驅散的debuff，並獲得"堅守I"(param2)10秒(param3)
	public static final int Eye_ID_100002 = 100002; // 每次施放技能時回復20MP
	public static final int Eye_ID_100003 = 100003; // 角色施放技能時，對當前目標賦予"石化"(param1)2秒(param2)
	public static final int Eye_ID_100004 = 100004; // 戰鬥開始時，賦予隨機敵人"戰術鎖定"(param1)
	public static final int Eye_ID_100005 = 100005; // 戰鬥開始時，賦予自身"憤怒"(param1)
	public static final int Eye_ID_100006 = 100006; // 戰鬥開始時，賦予自身"虔誠"(param1)
	public static final int Eye_ID_100009 = 100009; // 戰鬥開始時，賦予自身"銜尾蛇"(param1)
	public static final int Eye_ID_100018 = 100018; // 首次施放技能時，對當前HP比例最高的敵人賦予"崩壞"(param1)
	public static final int Eye_ID_100019 = 100019; // 戰鬥開始時，賦予所有友軍"強攻"(param1)持續5秒(param2)
	
	public static final int Passive_ID_10001 = 10001; // 永久提升自身雙防15%(params1)
	public static final int Passive_ID_10002 = 10002; // 普通攻擊命中後額外獲得3點MP(param1)，使用技能攻擊命中後額外獲得5點MP(param2)
	public static final int Passive_ID_10003 = 10003; //X 恢復傷藥(skill3001)指定目標不是自身時，且自身血量低於50%(param1)，自身也恢復相同治療量
	public static final int Passive_ID_10004 = 10004; // 永久提升自身閃避率15%(params1)
	public static final int Passive_ID_10005 = 10005; // 每3次(param1)普通攻擊可賦予當前目標"自然印記I"(param2)5秒(param3)
	
	public static final int Passive_ID_10006 = 10006; // 永久提升自身雙防25%(params1)
	public static final int Passive_ID_10007 = 10007; // 普通攻擊命中後額外獲得7點MP(param1)，使用技能攻擊命中後額外獲得12點MP(param2)
	public static final int Passive_ID_10008 = 10008; //X 聖女祈禱(skill3101)指定目標不是自身時，且自身血量低於60%(param1)，自身也恢復相同治療量
	public static final int Passive_ID_10009 = 10009; // 永久提升自身閃避率25%(params1)
	public static final int Passive_ID_10010 = 10010; // 每3次(param1)普通攻擊可賦予當前目標"自然印記II"(param2)5秒(param3)
	public static final int Passive_ID_10099 = 10099; // 提高自身20%魔法防禦、15%物理防禦、15%魔法攻擊，並且不會受到狂亂(params4)影響
	
	public static final int Passive_ID_90001 = 90001; // 提高包含自身以及隊伍中所有英雄5%物理攻擊，受到傷害降低10%(param1)**靈氣33
	public static final int Passive_ID_90002 = 90002; // 戰鬥開始時，賦予自身"權能"(param1)
	public static final int Passive_ID_90003 = 90003; // 提高包含自身以及隊伍中所有英雄33%攻擊速度(param1)**靈氣18
	public static final int Passive_ID_90004 = 90004; // 戰鬥開始時，賦予自身"不屈"(param1)
	public static final int Passive_ID_90005 = 90005; // 提高包含自身以及隊伍中所有英雄5%魔法攻擊，受到傷害降低10%(param1)**靈氣20
	public static final int Passive_ID_90006 = 90006; // 戰鬥開始時，對隊伍中魔法攻擊力最高的英雄賦予"啟蒙"(param1)
	public static final int Passive_ID_90007 = 90007; // 永久提升自身暴擊率12%(params1)
	public static final int Passive_ID_90008 = 90008; // 戰鬥開始時，賦予自身"魔力溢出"(param1)
	public static final int Passive_ID_90009 = 90009; // 戰鬥開始時，賦予自身"追擊"(param1)
	public static final int Passive_ID_90010 = 90010; // 戰鬥開始時，提高包含自身以及隊伍中賦予"鼓舞I"(param2)30秒(param1)；30秒後自身獲得"強攻"
	public static final int Passive_ID_90011 = 90011; // 提高包含自身以及隊伍中所有英雄10%閃避率(param1)**靈氣25
	public static final int Passive_ID_90012 = 90012; // 當自身3秒內沒有受到攻擊時，賦予自身"精確II"(params1)；受到攻擊時變為"精確I"(params2)**開場時會獲得"精確II"
	public static final int Passive_ID_999999 = 999999; // 無視石化40、狂亂43、凍結44、沉默47、暈眩51
	/**
	*對敵方當前MP最高單體造成140%(params1)物理傷害，並賦予"沉默"(params2)3秒(params3)，且我方全體賦予"物理特化I"(params4)5秒(params5)，自身返回40點MP(params6)
	*該次傷害不計算該buff
	*buff與返回mp不受miss影響
	 */
	public static final int SKILL_ID_1 = 1;
	/**
	 * 無視目標物理與魔法防禦，對敵方單體造成140%(params1)物理傷害+目標生命上限5%(params2)傷害
	 */
	public static final int SKILL_ID_1001 = 1001;
	/**
	 * 生命低於50%(params1)時，獲得"堅守I"(params2)10秒(params3)，每場戰鬥僅發動1次
	 */
	public static final int SKILL_ID_1002 = 1002;
	/**
	 * 無視目標物理與魔法防禦，對敵方單體造成200%(params1)物理傷害並恢復自身等量HP，且"嘲諷"(params2)敵方全體4秒(params3)
	 */
	public static final int SKILL_ID_1101 = 1101;
	/**
	 * 生命低於50%(params1)時，獲得"堅守II"(params2)10秒(params3)，並驅散自身所有DBUFF，每場戰鬥僅發動1次
	 */
	public static final int SKILL_ID_1102 = 1102;
	/**
	 * 隨機對敵方單體攻擊3次(params1)，每次造成140%(params2)魔法傷害，並賦予"侵蝕"(params3)8秒(params4)
	 */
	public static final int SKILL_ID_2101 = 2101;
	/**
	 * 對生命低於50%(params1)的敵方單體造成60%(params2)的魔法傷害
	 */
	public static final int SKILL_ID_2102 = 2102;
	/**
	 * 指定當前HP比例最低的2位友方單體，為其恢復魔法攻擊力240%(params1)的HP
	 */	
	public static final int SKILL_ID_3101 = 3101;
	/**
	 * 攻擊時有45%(params1)機率觸發，對當前目標造成130%(params2)魔法傷害
	 */
	public static final int SKILL_ID_3102 = 3102;
	
	
	//public static final int SKILL_ID_1009 = 1009;
	/**
	 * MP高於70%(params1)時發動，對物理攻擊力最高的敵方單體造成140%(params2)魔法傷害並賦予"致盲"(params3)5秒(params4)，此攻擊必定命中
	 */
	public static final int SKILL_ID_2 = 2;
	/**
	 * 隨機對敵方單體攻擊3次(params1)，每次造成120%(params2)魔法傷害，此攻擊必定命中
	 */
	public static final int SKILL_ID_2001 = 2001;
	/**
	 * 對生命低於50%(params1)的敵方單體造成40%(params2)的魔法傷害
	 */
	public static final int SKILL_ID_2002 = 2002;
	
	//public static final int SKILL_ID_2009 = 2009;
	
	/**
	 * 對敵方全體造成80%(params1)魔法傷害，並賦予"衰弱I"(params2)5秒(params3)
	 */
	public static final int SKILL_ID_21 = 21;
	/**
	 * MP高於70%(params1)時發動，對物理攻擊力最高的敵方單體造成140%(params2)魔法傷害並賦予"致盲"(params3)5秒(params4)，此攻擊必定命中
	 */
	public static final int SKILL_ID_22 = 22;
	/**
	 * 指定當前HP比例最低的友方單體，為其恢復魔法攻擊力200%(params1)的HP
	 */
	public static final int SKILL_ID_3001 = 3001;
	/**
	 * 攻擊時有35%(params1)機率觸發，對當前目標造成110%(params2)魔法傷害
	 */
	public static final int SKILL_ID_3002 = 3002;
	/**
	 * 對敵方單體進行二連擊(params1)，第二擊(params1)必為暴擊且附加目標當前生命5%(params2)傷害
	 */
	public static final int SKILL_ID_4001 = 4001;
	/**
	 * 閃避時對攻擊力最高的敵方單體造成60%(params1)物理傷害
	 */
	public static final int SKILL_ID_4002 = 4002;
	/**
	 * 對敵方魔法防禦最高的單體進行二連擊(params1)，第二擊(params1)必為暴擊且附加目標當前生命7%(params2)傷害
	 *技能施放後強制切目標
	 */
	public static final int SKILL_ID_4101 = 4101;	//
	/**
	 * 閃避時對攻擊力最高的敵方單體造成80%(params1)物理傷害
	 *技能施放後強制切目標
	 */
	public static final int SKILL_ID_4102 = 4102;	//
	/**
	*對敵方單體造成250%(params1)物理傷害，並賦予"防禦破壞II"(params2)6秒(params3)
	*該次傷害不計算該debuff
	 */
	public static final int SKILL_ID_5101 = 5101;	//
	/**
	 * 標記成功(Passive10010)時，對敵方單體造成100%(param1)物理傷害
	 */
	public static final int SKILL_ID_5102 = 5102;	//
	/**
	 * MP高於70%(params1)時發動，對隨機敵人造成5次(params2)25%(params3)物理傷害
	 */	
	public static final int SKILL_ID_41 = 41;
	/**
	 * MP高於70%(params1)時發動，對隨機敵人造成5次(params2)25%(params3)物理傷害
	 */
	public static final int SKILL_ID_42 = 42;
	/**
	 * 對敵方單體造成300%(param1)傷害小怪通用技能，300%傷害平分給每一hit
	 */
	public static final int SKILL_ID_500001 = 500001;
	/**
	*對敵方單體造成200%(params1)物理傷害，並賦予"防禦破壞I"(params2)4秒(params3)該次傷害不計算該debuff
	 */
	public static final int SKILL_ID_5001 = 5001;
	/**
	 * 對敵方全體造成125%(param1)傷害
	 */
	public static final int SKILL_ID_500101 = 500101;
	/**
	 * 標記成功(Passive10005)時，對敵方單體造成80%(param1)物理傷害/2物理傷害兩次
	 */
	public static final int SKILL_ID_5002 = 5002;
	/**
	 * 對敵方全體造成150%(param1)傷害，並賦予"防禦破壞I"(param2)5秒(param3)
	 */
	public static final int SKILL_ID_500201 = 500201;
	/**
	 * 對敵方全體造成135%(param1)傷害，並賦予"侵蝕"(param2)10秒(param3)
	 */
	public static final int SKILL_ID_500301 = 500301;
	/**
	 * 對敵方單體造成240%(param1)傷害，並賦予"自然印記I"(param2)10秒(param3)
	 */
	public static final int SKILL_ID_500401 = 500401;
	/**
	 * 對當前HP最低的敵方單體造成每下30%(param1)傷害，並賦予"暈眩"(param2)0.5秒(param3)
	 */
	public static final int SKILL_ID_500501 = 500501;
	/**
	 * 對敵方單體造成200%(param1)傷害，若目標血量低於50%(param2)則提升為440%(param3)傷害
	 */
	public static final int SKILL_ID_500601 = 500601;
	/**
	 * 對敵方單體造成5次50%(param1)傷害，最後一擊造成150%(param2)傷害，並賦予"暈眩"(param3)2秒(param4)
	 */
	public static final int SKILL_ID_500701 = 500701;
	/**
	 *對敵方全體造成440%(param1)傷害，並賦予"致盲"(param2)10秒(param3)
	 */
	public static final int SKILL_ID_500801 = 500801;
	/**
	 * 對敵方單體造成400%(param1)傷害，若目標為物理職業則最後一下額外造成最大HP10%(param2)傷害；若目標為魔法職業則最後一下額外賦予"沉默"(param3)10秒(param4)
	 */
	public static final int SKILL_ID_500901 = 500901;
	/**
	 * 先驅散全部敵方目標BUFF，再造成350%(param1)魔法傷害，並賦予目標"崩壞"(param2)
	 */
	public static final int SKILL_ID_501001 = 501001;
	/**
	 *對敵方全體造成300%(param1)傷害，並賦予"凍傷I"(param2)5秒(param3)
	 */
	public static final int SKILL_ID_501601 = 501601;
	/**
	 *對敵方全體造成300%(param1)傷害，並賦予"衰弱II"(param2)5秒(param3)
	 */
	public static final int SKILL_ID_502101 = 502101;
	/**
	 * 對敵方全體造成500%(param1)傷害，並賦予"防禦破壞III"(param2)15秒(param3)
	 */
	public static final int SKILL_ID_600101 = 600101;
	/**
	 *MP高於30%(params1)時發動，對敵方全體造成350%(params2)物理傷害，並賦予"暈眩"(param3)5秒(param4)
	 */
	public static final int SKILL_ID_600102 = 600102;
	
	public static final int SKILL_ID_600103 = 600103;
//	public static final int SKILL_ID_599901 = 599901;
	/**
	 *對敵方全體造成250%(params1)魔法傷害，並賦予暈眩(params2)3秒(params3)
	 */
	public static final int SKILL_ID_9901 = 9901;
	/**
	 *HP低於30%(params1)時發動，使我方全體賦予500%(params2)魔法攻擊力的護盾，BNB英雄額外賦予再生(params3)8秒(params4)
	 */
	public static final int SKILL_ID_9902 = 9902;

}
