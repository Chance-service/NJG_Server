package com.guaji.game.util;

import com.guaji.game.protocol.Const;

/**
 * 游戏常量定义
 */
public class GsConst {
	// 极速战斗
	public static final boolean DEBUG_FAST_BATTLE = false;
	// 概率最小基数
	public static final int RANDOM_BASE_VALUE = 1;
	// 万位改了基础
	public static final int RANDOM_MYRIABIT_BASE = 10000;
	// 类型定义基数
	public static final int ITEM_TYPE_BASE = 10000;
	// 抽奖错误物品ID定义
	public static final int UNKONWN_ITEM_ID = 99999;
	// 角色最大技能个数
	public static final int MAX_SKILL_COUNT = 5;
	// 每小时秒数
	public static final int ONE_HOUR_SEC = 3600;
	// 每分钟秒数
	public static final int ONE_MIN_SEC = 60;
	// 每天秒数
	public static final int ONE_DAY_SEC = ONE_HOUR_SEC * 24;
	// 暴击系数
	public static final float CRITICAL_DAMAGE_VALUE = 0.0001f;
	// 好友推荐个数
	public static final int FRIEND_RECOMMAND_SIZE = 10;
	// 狂暴回合数
	public static final int MONSTER_RAGE_MAX_TURN = 100;
	// 呀呀主播钻石兑换金豆比例
	public static final int YAYA_EXCHANGE_RATIO = 10;
	// 呀呀主播排行重置间隔(毫秒)
	public static final long RESET_YAYA_RANK_INTERVAL = 7 * 24 * 60 * 60 * 1000;
	// 翅膀排行最大个数
	public static final int MAX_WING_RANK_NUM = 5;

	// 排行榜最大个数
	public static final int MAX_RANK_NUM = 500;

	// 排行榜最大个数
	public static final int MAX_RANK_DISPLAY_NUM = 100;
	
	/**
	 * 玩家頭圖分割基數
	 */
	public static final int HEADICON_BASE = 1000;
	/**
	 *神秘刷新收費消耗 20鑽石
	 */
	public static final int MYSTERY_REFRESH_COST = 20;
	
	/*
	 * 加強召喚天數基數
	 */
	public static final int SUMMON_TYPE_BASE = 10000;
	
	public static  class SignMark {
		/**
		 * 秘密信條通行證
		 */
		public static int SecretMsgPass = 50;
		/*
		 * 四倍快速戰鬥
		 */
		public static int Four_Fighting = 51;
	}
	
	/*
	 * TapDB UID基數
	 */
	public static final long TAPUID_BASE = 1000000;
	/**
	 * 英雄屬性種類
	 */
	public static class HeroAttrType {
		public static int Fire = 1;
		public static int Water = 2;
		public static int Wind = 3;
		public static int Light = 4;
		public static int Dark = 5;
	}
	
	public static class PlatformById {
		public static int H365 = 1;
		public static int R18 = 2;
		public static int JSG = 3;
		public static int LSJ = 4;
		public static int MURA = 5;
		public static int KUSO = 6;
	}
	
	/**
	 * 儲值GoodsType
	 * 	(type == 1)// 月卡   活動編號83
		(type == 11)// 消耗型月卡 活動編號130
		(type == 6)// 新周卡  活動編號96
		(type == 3)// 打折礼包  活動編號82
		(type == 4)// 限时限购  活動編號26
		(type == 8)// 聊天皮肤  活動編號100
		(type == 0)// 邮件处理  無
		(type == 5)// 折扣礼包 活動編號94
		(type == 12)// 等級通行證 活動編號162
		(type == 13)// 關卡通行證 活動編號163
		(type == 14)// 爬塔通行證 活動編號164
		(type == 15)// 每月付費獎勵(高級) 活動編號161
		(type == 16)// 每月付費獎勵(豪華) 活動編號161
		(type == 17)// 特權購買 活動編號168
		(type == 18)// 等級彈跳禮包 活動編號132
		(type == 19)// 關卡彈跳禮包 活動編號151
		(type == 20)// 活動彈跳禮包 活動編號169
		(type == 21)// 活動彈跳禮包 活動編號170
		(type == 22)// 關卡通行證2 活動編號163
		(type == 23)// 階段禮包 活動編號179
		(type == 24)// 失敗禮包 活動編號177
		(type == 25)// 活動彈跳禮包 活動編號181
		(type == 26)// 活動彈跳禮包 活動編號182
		(type == 27)// 活動彈跳禮包 活動編號183
		(type == 28)// 活動彈跳禮包 活動編號184
		(type == 29)// 活動彈跳禮包 活動編號185
		(type == 30)// 活動彈跳禮包 活動編號186
		(type == 31)// 加強活動彈跳禮包 活動編號187
	 */
	
	public static class GoodsType{
		/**
		 * 0.鑽石郵件處裡
		 */
		public static final int Mail = 0;
		/**
		 * 1.月卡   活動編號83
		 */
	    public static final int MonthCord = 1;
		/**
		 * 2.無
		 */
	    public static final int Type2 = 2;
		/**
		 * 3.打折禮包 活動編號82
		 */
	    public static final int SalePacket= 3;
		/**
		 * 4.限時限購  活動編號26
		 */
	    public static final int LimitRecharge = 4;
		/**
		 * 5.折扣禮包  活動編號94
		 */
	    public static final int DiscountGift = 5;
		/**
		 * 6.周卡 活動編號96
		 */
	    public static final int NewWeekCard = 6;
		/**
		 * 7.無
		 */
	    public static final int Type7 = 7;
		/**
		 * 8.聊天皮膚 活動編號100
		 */
	    public static final int ChatSkin = 8;
		/**
		 * 9.無
		 */
	    public static final int Type9 = 9;
		/**
		 * 10.(舊周卡)進階月卡:大月卡 活動編號127 goodsId:74
		 */
	    public static final int BigMonthCord = 10;
		/**
		 * 11.超值月卡 小月卡 goodsId:32
		 */
	    public static final int SmallMonthCord = 11;
		/**
		 * 12.等級成長通行證 活動編號162
		 */
	    public static final int LVPass = 12;
		/**
		 * 13.關卡成長通行證 活動編號163
		 */
	    public static final int MapPass = 13;
		/**
		 * 14.爬塔成長通行證 活動編號164
		 */
	    public static final int TowerPass = 14;
		/**
		 * 15.每月付費獎勵(高級) 活動編號161
		 */
	    public static final int SupportCalender1 = 15;
		/**
		 * 16.每月付費獎勵(奢華) 活動編號161
		 */
	    public static final int SupportCalender2 = 16;
		/**
		 * 17.特權購買 活動編號168
		 */
	    public static final int SubScription = 17;
		/**
		 * 18.等級彈跳禮包 活動編號132
		 */
	    public static final int LevelGift = 18;
		/**
		 * 19.關卡彈跳禮包 活動編號151
		 */
	    public static final int  StageGift = 19;
		/**
		 * 20.活動彈跳禮包 活動編號169
		 */
	    public static final int ActivityGift = 20;
		/**
		 * 21.活動彈跳禮包 活動編號170
		 */
	    public static final int JumpGift = 21;
		/**
		 * 22.關卡通行證2 活動編號163
		 */
	    public static final int MapPass2 = 22;
		/**
		 * 23.階段禮包 失敗禮包 活動編號179
		 */
	    public static final int StepGift = 23;
		/**
		 * 24.失敗禮包 活動編號177
		 */
	    public static final int FailedGif = 24;
		/**
		 * 25.活動彈跳禮包 活動編號181
		 */
	    public static final int JumpGift181 = 25;
		/**
		 * 26.活動彈跳禮包 活動編號182
		 */
	    public static final int JumpGift182 = 26;
		/**
		 * 27.活動彈跳禮包 活動編號183
		 */
	    public static final int JumpGift183 = 27 ;
		/**
		 * 28.活動彈跳禮包 活動編號184
		 */
	    public static final int JumpGift184 = 28 ;
		/**
		 * 29.活動彈跳禮包 活動編號185
		 */
	    public static final int JumpGift185 = 29 ;
		/**
		 * 30.活動彈跳禮包 活動編號186
		 */
	    public static final int JumpGift186 = 30 ;
		/**
		 * 31.活動彈跳禮包 活動編號187
		 */
	    public static final int MaxJump187 = 31 ;
		/**
		 * 32.活動彈跳禮包
		 */
	    public static final int SignGift = 32 ;
		
//		public static GoodsType valueOf(int Type) {
//			if (Type <= 0 && Type >= GoodsType.values().length) {
//				throw new ArrayIndexOutOfBoundsException();
//			}
//			return GoodsType.values()[Type];
//		}
	}
	
	/*
	 * int最大正整數範圍
	 */
	public static final int MAX_INT_RANGE = 2147483647;
	
	/**
	 * 	符石精煉ID加鎖遮罩
	 */
	public static final int BADGE_LOCK_MASK = 10000000;
	
	/**
	 * DailyQuest Type 對應表格參數
	 */
	
	public static class DailyQuestType {
		// 登入
		public static final int LOGIN = 1;
		// 儲值不限額X
		public static final int ON_RECHARAGE = 2;
		// 儲值限額X
		public static final int ON_RECHARAGE_LIMILT = 3;
		// 快速戰鬥
		public static final int FAST_FIGHT = 4;
		// FB分享X
		public static final int FB_SHARE = 5;
		// 菁英副本(NG 地下城)
		public static final int  ELITE_MISSION = 6;
		// 普通副本X
		public static final int Normal_MISSION = 7;
		// 英雄升星
		public static final int ROLE_UPGRADE_STAR = 8;
		// 競技場
		public static final int JING_JI_CHANG = 9;
		// 裝備強化
		public static final int EQUIP_ENHANCE = 10;
		// 裝備熔煉
		public static final int EQUIP_SMELT = 11;
		// 派遣
		public static final int ROLE_BOUNTY = 12;
		// 18路X
		public static final int EIGHTEEN  = 13;
		// 英雄升級
		public static final int ROLE_UPGRADE_LEVEL = 14;
		// 裝備鍛造
		public static final int EQUIP_UPGRADE = 15;
		// 領取掛機獎勵
		public static final int TAKE_FIGHT_AWARD = 16;
		// 裝備鍛造
		public static final int BADGE_FUSION = 17;
		// 鑽石兌換(寶庫點金)
		public static final int MONEY_COLLETION = 18;
		// 世界頻道發言
		public static final int WORLD_SPEAK = 19;
		// 贈送友誼點數
		public static final int GIVE_FIRENDSHIP = 20;
		// 召喚英雄動作
		public static final int CALL_HERO = 21;
		// 進入壁尻活動次數
		public static final int GLORYHOLE_JOINTIMES = 22;
		// 每日活動達成分數
		public static final int DAILY_POINT = 23;
		
	}
	
	/**
	 * 單人強敵成就任務種類
	 */
	
	public static class SingleBossQuestType {
		/**
		 * 通關關卡LV
		 */
		public static int PassLv = 1;
		/**
		 * 到達最高分數
		 */
		public static int MaxScore = 2;
		/**
		 * 累積分數
		 */
		public static int TotalScore = 3;
		/**
		 * 挑戰次數
		 */
		public static int ChanllengeTime = 4;
		
	}
	
	/**
	 * 帳號類型
	 */
	public static class AccType {
		// 參觀者帳號
		public static int GuestAcc = 1;
		// 綁定者帳號
		public static int BindAcc = 0;
	}
	
	/**
	 * 对象类型
	 */
	public static class ObjType {
		// 玩家对象
		public static int PLAYER = 1;
		// 管理器对象
		public static int MANAGER = 10;
	}
	
	/**
	 * tapdb屬性動作參數
	 *
	 */
	public static enum tapDBPropertyMotion{
		/**
		 * 	對於需要保證只有首次設定時有效的屬性
		 * 	，可以使用 initialise 進行賦值操作
		 * 	，僅當前值為空時賦值操作才會生效
		 * 	，如當前值不為空，則賦值操作會被忽略
		 */
		initialise,
		/**
		 * 	對於常規的設備屬性
		 * 	，可使用改接口進行賦值操作
		 * 	，新的屬性值將會直接覆寫舊的屬性值
		 */
		update,
		/**
		 * 	對於數值類型的屬性
		 * 	，可以使用此介面進行累加操作
		 * 	，呼叫後 TapDB 將對原屬性值進行累加後儲存結果值。
		 */
		add
	}
	
	/**
	 * tapdb屬性名參數
	 *
	 */
	public static class tapDBPropertyName{
		/*
		 * 玩家帳號
		 */
		public static String uid = "#uid";
		/*
		 * 紀錄玩家當前關卡
		 */
		public static String pass_mapid = "#pass_mapid";
		/*
		 * 紀錄設備當前步驟
		 */
		//public static String device_step ="#device_step";
		/*
		 * 紀錄新手目標
		 */
		public static String teach_target="#teach_target";
		/*
		 * 紀錄新手教學步驟
		 */
		public static String teach_step="#teach_step";
		
		/*
		 * 紀錄玩家挑戰地圖
		 */
		public static String battle_mapid = "#battle_mapid";
		/*
		 * 紀錄玩家挑戰(進攻)隊伍
		 */
		public static String battle_team = "#battle_team";
		/**
		 * 紀錄防守隊伍
		 */
		public static String battle_team_def = "#battle_team_def";
		/*
		 * 紀錄玩家挑戰結果(由clent傳入 server轉傳)
		 */
		public static String battle_result = "#battle_result";
		/*
		 * 紀錄功方資訊(由clent傳入 server轉傳)
		 */
		public static String battle_result_atk = "#battle_result_atk";
		/*
		 * 紀錄守方資訊(由clent傳入 server轉傳)
		 */
		public static String battle_result_def = "#battle_result_def";
		/*
		 * 遊玩壁尻使用的次數
		 */
		public static String gloryhole_times = "#gloryhole_times";
		/*
		 * 遊玩的種類(team)0.練習 1= 前  2= 後
		 */
		public static String gloryhole_type = "#gloryhole_type";
		/*
		 * 壁尻使用道具
		 */
		public static String gloryhole_useitem = "#gloryhole_useitem";
		/*
		 * 壁尻的分數
		 */
		public static String gloryhole_score = "#gloryhole_score";
		/*
		 * 英雄itemid
		 */
		public static String hero_id = "#hero_id";
		/*
		 * MessageId
		 */
		public static String message_id = "#message_id";
		
		public static String item_id = "#item_id";
		
		public static String count = "#count";
		
		public static String get_action = "#get_action";
		
		public static String use_action = "#use_action";
		
		public static String get_action_str = "#get_action_str";
		
		public static String use_action_str = "#use_action_str";
		
	}
	/**
	 *  tapdb事件名參數
	 * @author Tinlin_Home
	 *
	 */
	public static class tapDBEventName{
		public static String  event_device_step = "#event_device_step";
		public static String  event_teach_step = "#event_teach_step";
		public static String  event_battle_map = "#event_battle_map";
		public static String  event_battle_pvp = "#event_battle_pvp";
		public static String  event_gloryhole_start = "#event_gloryhole_start";
		public static String  event_gloryhole_result = "#event_gloryhole_result";
		public static String  event_get_item = "#event_get_item";
		public static String  event_use_item = "#event_use_item";
		public static String  event_secretmessage = "#event_secretmessage";
	}
	
	/**
	 * 秘密信條訊息類型
	 */
//	public static class SecretMsgType {
//		// 一般訊息
//		public static int Normal = 1;
//		// 關卡訊息
//		public static int Level = 2;
//	}
//	
	/**
	 * 標記協定動作種類
	 */
	public static class SignProtoType {
		public static int Sync_All = 0;
		public static int Inquire_Sign = 1;
		public static int Change_Sign = 2;
		public static int Modify_Sign = 3;
	}
	
	/**
	 * 公會魔典相關常數
	 */
	public static class GuildBuffConst {
		/**
		 * 最大小天賦
		 */
		public static int MaxTalentID = 6;
		/**
		 * 最大職業編號
		 */
		public static int MaxProf = 4;
		/**
		 * 最大小天賦等級
		 */
		public static int MaxTalentLV = 50;
		/**
		 * 天賦定義基數 職業*基數+天賦ID
		 */
		public static int Type_Base = 10;
	}
	
	/**
	 * 伺服器成就類型
	 */
	public static class SvrMissionType{
		public static int Player_Fight_Value = 1;
		public static int Player_Level = 2;
		public static int Player_Current_Map = 3;
		public static int Hero_Attr_Light = 4;
		public static int Hero_Attr_dark = 5;
		public static int Hero_Attr_Water = 6;
		public static int Hero_Attr_Fire = 7;
		public static int Hero_Attr_Wind = 8;
	}
	
	/**
	 * 175壁尻活動排行榜類型
	 */
	
	public static class GloryHoleRankType{
		public static int DAILY = 0;
		public static int TOTALMAX = 1;
	}
	
	/**
	 * 175壁尻活動使用道具類型
	 * 
	 */
	public static class GloryHoleItemType{
		/**
		 * 0.增加遊戲時長
		 */
		public static int ADDTIME = 0;
		/**
		 * 1.增加Feverbar
		 */
		public static int ADDBAR = 1;
		/**
		 * 2.抵銷
		 */
		public static int OFFSET = 2;
		/**
		 * 3.增益
		 */
		public static int ADDGAIN = 3;
		/**
		 * 4.道具數量
		 */
		public static int ItemCount = 4;
	}
	
	/**
	 * 175壁尻活動排行榜類型
	 */
	public static class GloryHoleGameTime{
		/**
		 * 開始時間
		 */
		public static int START = 0;
		/**
		 * 結束時間
		 */
		public static int END = 1;
		/**
		 * 結算
		 */
		public static int COUNT =2;
	}
	
	/**
	 * 175壁尻活動團隊獎勵種類
	 */
	public static class GloryHoleAwardType{
		public static int WINER = 0;
		public static int LOSER = 1;
	}
	
	
	/**
	 * 192.累儲累消累充活動
	 * 
	 */
	public static class RechargeBounceType{
		/**
		 * 累積儲值(1)
		 */
		public static int Deposit = 1;
		/**
		 * 單筆消費(2)
		 */
		public static int Single = 2;
		/**
		 * 累積消費(3)
		 */
		public static int consums = 3;
		/**
		 * type total
		 */
		public static int typeCount = 3;
	}
	
	/**
	 * 儲值幸運資料類型
	 */
	public static class RechargeLuckyType{
		// 基礎機率
		public static int BASE = 0;
		// 累加機率
		public static int ADD = 1;
	}
	
	/**
	 * 觸發ID種類
	 */
	public static class TriggerType{
		/**
		 * 1.技能
		 */
		public static int SKILL = 1;
		/**
		 * 2.BUFF
		 */
		public static int BUFF = 2;
	}
	
	/*
	 * 戰鬥類型
	 */
	public static class BattleType{
		/**
		 *  0.測試用 
		 */
		public static int DEBUG = 0;
		/**
		 *  1.關卡 
		 */
		public static int LEVEL = 1;
		/**
		 *  2.副本
		 */
		public static int EVENT = 2;
		/**
		 *  3.PVP 
		 */
		public static int ARENA = 3;
		/**
		 *  4.世界Boss 
		 */
		public static int Boss = 4;
		/**
		 *  5.地下城 
		 */
		public static int Dungeon = 5;
		/**
		 *  6.循環活動關卡
		 */
		public static int CycleActivity = 6;
		/**
		 *  7.單人強敵Boss
		 */
		public static int SingleBoss = 7;
		/**
		 *  8.單人強敵Boss模擬
		 */
		public static int SingleBoss_Mock = 8;
		/**
		 *  9.賽季爬塔
		 */
		public static int SeasonTower = 9;
		/**
		 * 10.九宮格拼圖
		 */
		public static int PuzzleBattle = 10;
	
		
	}
	/*
	 * 戰鬥人物狀態
	 */
	public static class PersonState{
		/*
		 * 0.戰鬥存活中
		 */
		public static int PERSON_FIGHT = 0;
		/**
		 * 1.死透涼涼
		 */
		public static int PERSON_DEAD = 1;
	}
	
	/**
	 * 編隊相關
	 */
	public static class FormationType {
		/** 
		 * 編隊起始編號,進攻地圖陣型使用
		 */
		public static int FormationBegin = 1; // 挑戰競技or所有地圖(進攻)陣型使用
		/** 
		 * 編隊結尾編號 , 競技場(防守)使用
		 */
		public static int FormationEnd = 8;  // 競技場(防守)使用
		// 英雄隊員人數				  	
		public static int HeroMaxMember = 5;
		// 一隊成員數量
		public static int FormationMember = 9;
	}
	/**
	 * 攻擊相關
	 */
	public static class AttackType {
		/**
		 * 普攻作用
		 */
		public static int NomarlATK = 1;
		/**
		 * 技能作用
		 */
		public static int SkillACT = 2;
		/**
		 * 狀態 
		 */
		public static int Buff_DeBuff = 3;
		/**
		 *  開始普攻擊
		 */
		public static int StartNomarl = 4;
		/**
		 * 開始技能攻擊
		 */
		public static int StartSkill = 5;
	}
	/**
	 * Buff 編號
	 */
	public static class Buff{
		/**
		 * 堅守I 1
		 */
		public static int STABLE_I = 1;
		/**
		 * 堅守II 2
		 */
		public static int STABLE_II = 2;
		/**
		 * 憤怒 3
		 */
		public static int RAGE = 3;
		/**
		 *  虔誠 4
		 */
		public static int PIOUS = 4;
		/**
		 * 開花 5
		 */
		public static int PETAL = 5;
		/**
		 * 銜尾蛇 6
		 */
		public static int OUROBOROS = 6;
		/**
		 * 連鎖防禦 7
		 */
		public static int DEFENSE_CHAIN_A = 7;
		/**
		 * 專注 8
		 */
		public static int CONCENTRATION = 8; 
		/**
		 * 太陽神 9
		 */
		public static int APOLLO = 9;
		/**
		 *  月光 10
		 */
		public static int MOONLIGHT = 10;
		/**
		 * 迴光 11
		 */
		public static int REBIRTH = 11;
		/**
		 * 強攻 12
		 */
		public static int FORCE = 12;
		/**
		 * 物理特化 13
		 */
	    public static int PHYSICAL_SPECIALIZATION = 13;
		/**
		 * 免疫 14
		 */
		public static int IMMUNITY = 14;
		/**
		 * 守護 15
		 */
		public static int GUARD = 15;
		/**
		 * 突擊 16
		 */
		public static int ASSAULT_A = 16;
		/**
		 * 權能 17
		 */
		public static int POWER = 17;
		/**
		 * 急速 18
		 */
		public static int RAPID_A = 18;
		/**
		 * 不屈 19
		 */
		public static int UNDEAD = 19;
		/**
		 *  奧術A 20
		 */
		public static int ARCANE_A = 20;
		/**
		 * 啟蒙 21
		 */
		public static int ENLIGHTENMENT = 21;
		/**
		 * 魔力溢出 22
		 */
		public static int MANA_OVERFLOW = 22;
		/**
		 * 追擊 23
		 */
		public static int CHASE = 23;
		/**
		 * 鼓舞 24
		 */
		public static int BOOST_I_A = 24;
		/**
		 * 暗影 25
		 */
		public static int SHADOW_A = 25;
		/**
		 * 精確I 26
		 */
		public static int PRECISION_I = 26;
		/**
		 * 精確II 27
		 */
		public static int PRECISION_II = 27;
		/**
		 * 鼓舞II 28
		 */
		public static int BOOST_II_A = 28;
		/**
		 * 防禦鎖鏈 29
		 */
		public static int DEFENSE_CHAIN_B = 29;
		/**
		 * 鼓舞I 30
		 */
		public static int BOOST_I_B = 30;
		/**
		 * 暗影 31
		 */
		public static int SHADOW_B = 31;
		/**
		 * 鼓舞II 32
		 */
		public static int BOOST_II_B = 32;
		/**
		 *  突擊 33
		 */
		public static int ASSAULT_B = 33;
		/**
		 * 奧術B 34
		 */
		public static int ARCANE_B = 34;
		/**
		 * 急速 54
		 */
		public static int RAPID_B = 54;
		/**
		 *  再生 55
		 */
		public static int RECOVERY = 55;
		// Debuff--------------------------------------------------------------------------------------------------
		/**
		 * 侵蝕 35
		 */
		public static int EROSION = 35;
		/**
		 * 防禦破壞I 37
		 */
		public static int DESTROY_I = 36;
		/**
		 * 防禦破壞II 38
		 */
		public static int DESTROY_II = 37;
		/**
		 * 自然印記I  38
		 */
		public static int NATURE_I = 38;
		/**
		 * 自然印記II 39
		 */
		public static int NATURE_II = 39;
		/**
		 * 石化 40
		 */
		public static int STONE = 40;
		/**
		 * 戰術鎖定 41
		 */
		public static int TACTICAL_VISOR = 41;
		/**
		 * 眷屬 42
		 */
		public static int DEPENDENTS = 42;
		/**
		 * 狂亂 43
		 */
		public static int FRENZY = 43;
		/**
		 * 凍結 44
		 */
		public static int FREEZE = 44;
		/**
		 *  衰弱II 45
		 */
		public static int WEAK_II = 45;
		/**
		 *  崩壞46
		 */
		public static int COLLAPSE = 46;
		/**
		 * 沉默 47
		 */
		public static int SILENCE = 47;
		/**
		 * 衰弱I 48
		 */
		public static int WEAK_I = 48;
		/**
		 * 致盲 49
		 */
		public static int BLIND = 49;
		/**
		 * 魔力鎖鏈 50
		 */
		public static int MAGIC_LOCK = 50;
		/**
		 * 暈眩 51
		 */
		public static int DIZZY = 51;
		/**
		 * 燒傷 52
		 */
		public static int BURN = 52;
		/**
		 *  衰弱III 53
		 */
		public static int WEAK_III = 53;
		/**
		 *  凍傷I 56
		 */
		public static int FROSTBITE_I = 56;
		/**
		 * 
		 */
		public static int TAUNT = 57;
		/**
		 * 防禦破壞III
		 */
		public static int DESTROY_III = 58;
		/**
		 * 凍傷II
		 */
		public static int FROSTBITE_II = 59;
	}
	/**
	 * Buff數值類型
	 */
	public static class BUFF_TYPE {
		public static int NORMAL_BUFF = 1;
		// 光環Buff
		public static int AURA_BUFF = 2;
		// 標記
		public static int MARK = 3;
		// 主光環
		public static int AURA = 4;
	}
	/**
	 * 英雄元素屬性相關
	 */
	public static class ElementType {
		//無屬性
		public static int None = 0;
		//火屬性
		public static int FIRE = 1;
		//水屬性
		public static int WATER = 2;
		//自然屬性
		public static int NATURE = 3;
		//光屬性
		public static int LIGHT = 4;
		//暗屬性
		public static int DARK = 5;	
	}
	
	/**
	 * 技能類型
	 */
	public static class SkillType {
		//1.攻擊
		public static int ATK = 1;
		//2.治癒
		public static int CURE = 2;
		//3.被動
		public static int PASSIVE = 3;
	}
	
	/**
	 * NFT裝備狀態
	 */
	public static class NFTNotifyType {
		//1.需通知上鍊中
		public static int NEED_NOTICE = 1;
		//2.已通知上鍊
		public static int NOTIFIED = 2;
	}
	/**
	 * 系统对象id
	 */
	public static class ObjId {

		// 应用程序
		public static final int APP = 1;
		// 聊天室
		public static final int CHAT = 2;
		// 竞技场
		public static final int ARENA = 3;
		// 玩家快照
		public static final int SNAPSHOT = 4;
		// 公会
		public static final int ALLIANCE = 5;
		// 统计
		public static final int STATISTICS = 6;
		// 团战
		public static final int TEAM_BATTLE = 7;
		// 充值
		public static final int RECHARGE = 8;
		// 活动
		public static final int ACTIVITY = 9;
		// 职业排行
		public static final int PROFRANK = 10;
		// 阵营战
		public static final int CAMPWAR = 11;
		// 远征物资
		public static final int EXPEDITION_ARMORY = 12;
		// 工会战
		public static final int ALLIANCE_BATTLE = 13;
		// 好友邀请
		public static final int FRIEND_INVITE = 14;
		// 跨服
		public static final int CROSS_SERVER = 15;
		// worldboss
		public static final int WORLD_BOSS = 16;
		// 多人副本
		public static final int MULTI_ELITE = 17;
		// 排行献礼
		public static final int RANK_GIFT = 18;
		// 战力排行
		public static final int FIGHT_VALUE_RANK = 19;
		// 呀呀主播商城
		public static final int YAYASHOP = 21;
		// 呀呀兑换排行
		public static final int YAYA_RANK = 22;
		// 排行
		public static final int RANK_MANAGER = 23;
		// 捞鱼排行榜
		public static final int GOLDFISH_RANK = 24;
		// 神装锻造
		public static final int FORGING_EQUIP = 25;
		// 财富俱乐部
		public static final int WEALTH_CLUB = 26;
		// 气枪打靶
		public static final int SHOOT_ACTIVITY = 27;
		// GVG
		public static final int GVG_FUNCTION = 28;
		// 跨服战
		public static final int CROSS_BATTLE = 29;
		// UR抽卡积分排行
		public static final int UR_RANK = 30;
		// 18 路诸侯
		public static final int EighteenPrinces = 31;
		// 伺服器成就管理
		public static final int RECORD_FIRST_MANAGER = 32;
		// 戰鬥測試管理
		public static final int BattleDebug_MANAGER = 33;
		// 挖礦排行
		public static final int Mining_RANK_MANAGER = 34;
		// 壁尻排行
		public static final int GloryHole_RANK_MANAGER = 35;
		// 公告管理
		public static final int Bulletin_MANAGER = 36;
		// tabdb管理
		public static final int TabDB_MANAGER = 37;
		// 單人強敵排行榜
		public static final int SingleBoss_RANK_MANAGER = 38;
		// 賽季爬塔排行榜
		public static final int SeasonTower_RANK_MANAGER = 39;
	}

	/**
	 * 消息定义
	 */
	public static class MsgType {
		// 连接断开
		public static final int SESSION_CLOSED = 2;
		// 玩家上线
		public static final int PLAYER_LOGIN = 3;
		// 玩家初始化完成
		public static final int PLAYER_ASSEMBLE = 4;

		/**
		 * 使用道具
		 */
		// 使用小时卡
		public static final int USE_HOUR_CARD = 3001;

		/**
		 * 快照管理器消息定义
		 */
		// 上线删除快照数据
		public static final int ONLINE_REMOVE_OFFLINE_SNAPSHOT = 4002;
		// 工会离线玩家信息同步
		public static final int OFFLINE_ALLIANCE_INFO = 4003;

		/**
		 * 装备模块
		 */
		public static final int REDUCE_PLAYER_EQUIP_EXP = 5001;

		// 角色更改名字
		public static final int CHANGE_NAME = 6001;

		/**
		 * 竞技场消息定义
		 */
		// 发起排名挑战
		public static final int ARENA_CHALLENGE = 8001;
		// 挑战结束
		public static final int CHALLENGE_FINISHED = 8002;
		// 同步竞技场页面
		public static final int SYNC_ARENA_INFO = 8003;
		// 戰鬥重新測試
		public static final int BATTLE_REPEAT = 8004;

		/**
		 * 公会消息定义
		 */
		public static final int ADD_ALLIANCE_BOSS_VITALITY = 9001;
		// 帮派单个玩家攻击BOSS
		public static final int SINGLE_ATTACK_ALLIANCE_BOSS = 9002;
		// 帮派解散
		public static final int ALLIANCE_REMOVE = 9003;
		// 帮派成员离开
		public static final int ALLIANCE_MEMBER_REMOVE = 9004;
		// 帮派成员添加
		public static final int ALLIANCE_MEMBER_ADD = 9005;
		// 帮派升级
		public static final int ALLIANCE_LELEL_UP = 9006;

		// 工会战 元气 收集 触发排行变化
		public static final int ALLIANCE_VITALITY_CHANGE = 9101;

		/**
		 * 创建邮件
		 */
		public static final int MAIL_CREATE = 13001;

		/**
		 * 留言模块消息定义
		 */
		// 有人给我发留言
		public static final int MSG_TO_ME = 14001;

		/**
		 * 团战模块消息定义
		 */
		// 被踢出团战队伍
		public static final int KICK_OUT_ME = 15001;
		// 团战状态切换
		public static final int TEAM_BATTLE_CHANGE = 15002;

		/**
		 * 称号消息定义
		 */
		// 竞技场称号类型变化
		public static final int ARENA_RANK_CHARGE = 17001;
		// vip称号类型变化
		public static final int VIP_LEVEL_CHANGE = 17002;
		// 签到称号类型变化
		public static final int CONTINUE_LOGIN = 17003;
		// 竞技场排名区间类型变化
		public static final int ARENA_RANK_EXTENT = 17004;
		// 职业排行榜称号发生变化
		public static final int PROF_RANK_CHANGE = 17005;
		// 玩家身上神器数量
		public static final int ROLE_GODLY = 17006;
		// 团战冠军
		public static final int TEAM_BATTLE_CHAMPION = 17007;
		// 玩家神器星数称号
		public static final int EQUIP_STAR = 17008;

		/**
		 * 阵营战模块消息定义
		 */
		// 状态切换
		public static final int CAMPWAR_STATUS_CHANGE = 21001;
		// 战斗轮次发生改变
		public static final int CAMPWAR_BATTLE_ROUND_CHANGE = 21002;
		// 本轮阵营战结束，进入公示时间
		public static final int CAMPWAR_END = 21003;
		// 本期阵营战关闭，开始下一场阵营战报名
		public static final int CAMPWAR_CLOSE = 21004;

		/**
		 * 多人副本模块消息定义
		 */
		// 同步房间内玩家列表
		public static final int SYNC_ROOM_MEMBER_LIST = 25001;
		// 踢出房间内玩家
		public static final int KICK_OUT_IN_ROOM_PLAYER = 25002;
		// 玩家进入房间
		public static final int PLAYER_ENTER_ROOM = 25003;
		// 战斗状态改变
		public static final int BATTLE_STATE_CHANGE = 25005;
		// 战斗胜利
		public static final int MULTI_ELITE_BATTLE_SETTLE = 25006;
		// 战斗开始通知队友
		public static final int BATTLE_START_NOTICE = 25008;
		/**
		 * 活动模块消息定义
		 */
		// 活动列表发生改变
		public static final int ACTIVITY_LIST_CHANGE = 80001;
		// 活动奖励全部领取
		public static final int ALL_ACTIVITY_AWARDS_GOT = 80002;
		// 月卡充值成功提示
		public static final int MONTH_CARD_SUC = 80101;
		// 消耗月卡充值成功提示
		public static final int CONSUME_MONTH_CARD_SUC = 80102;
		// 周卡卡充值成功提示
		public static final int WEEK_CARD_SUC = 81301;
		// 周卡卡充值成功提示
		public static final int CONSUME_WEEK_CARD_SUC = 81302;
		// 连续充值天数增加
		public static final int RECHARGE_DAYS_INCREASE = 80301;
		// 累计消费数额增加
		public static final int ACC_CONSUME_INCREASE = 80401;
		// 远征物资的经验添加
		public static final int EXPEDITION_ARMORY_EXP_ADD = 82501;
		// 远征物资活动状态切换
		public static final int EXPEDITION_ARMORY_STATUS_CHANGE = 82502;
		// 排行献礼活动刷新经验排名
		public static final int RANK_GIFT_RESET_EXP_RANK = 82503;
		// 排名献礼活动玩家改变名字
		public static final int PLAYER_NAME_CHANGE = 82504;
		// 呀呀主播兑换金豆数量改变
		public static final int YAYA_COUNT_CHANGE_RANK = 83301;
		// 首冲领取完
		public static final int FIRST_GIFT_GET_SUCCESS = 83302;
		// 捞鱼活动添加积分
		public static final int GOLDFISH_ADD_SCORE = 83401;
		// 神装锻造发送邮件
		public static final int FORGING_EQUIP_EMAIL = 83501;
		// 打靶活动
		public static final int SHOOT_ACTIVITY = 83502;
		// 新手UR活动
		public static final int NEW_UR_ACTIVITY = 83503;
		
		// 單人強敵活动增加積分
		public static final int SINGLE_BOSS_ACTIVITY = 83504;
		
		// 爬塔活动增加樓層
		public static final int SEASON_TOWER_ACTIVITY = 83505;
		/**
		 * 职业排行消息定义
		 */
		// 玩家战力发生变化
		public static final int FIGHT_VALUE_CHANGE = 16001;

		/**
		 * 新手任务
		 */
		// 任务事件
		public static final int QUEST_EVENT = 26001;
		// 新建任务事件
		public static final int NEW_QUEST_EVENT = 26002;
		// 任务完成时间
		public static final int QUEST_FINISHED_EVENT = 26003;

		/**
		 * 角色转生
		 */
		public static final int CHALLENGE_BOSS_RESULT_EVENT = 29001;
		public static final int LEVELUP_TALENT_NUM = 29002;

		/**
		 * 英雄令任务
		 */
		public static final int HERO_TOKEN_BATTLE_TASK = 31001;

		/**
		 * 排行变化消息
		 */
		public static final int ON_RANK_CHANGE = 32001;
		// 获取排行
		public static final int ON_RANK_GET = 32002;

		/* GVG */
		/**
		 * GVG阶段状态推送
		 */
		public static final int GVG_STATE = 33001;

		/* 跨服状态推送 */
		/**
		 * 跨服状态推送
		 */
		public static final int CROSS_SATATE = 33002;
		
		/**
		 * 伺服器成就任務
		 */
		// 伺服器成就變化
		public static final int ON_SERVER_MISSION_CHANGE = 34001;
		
		/**
		 * 日常任务Module消息
		 */
		public static class DailyQuestMsg {
			public static final int MSG_BASE = 40000;
			public static final int LOGIN_DAY = 40001;
			public static final int ON_RECHARGE = 40002;
			public static final int ON_RECHARGE_LIMIT = 40003;
			public static final int FAST_FIGHT = 40004;
			public static final int FACE_BOOK_SHARE = 40005;
			public static final int ELITE_MISSION_WIN = 40006;
			public static final int NOR_MISSION_WIN = 40007;
			public static final int ROLE_UPGRADE_STAR = 40008;
			public static final int JING_JI_CHANG_FIGHT = 40009;
			public static final int EQUIP_ENHANCE = 40010;
			public static final int SMELT_EQUIP = 40011;
			public static final int ROLE_EXPEDITION_COUNT = 40012;
			public static final int EIGHTEENPRINCESCHANGE = 40013;
			public static final int ROLE_UPGRADE_LEVEL = 40014;
			public static final int EQUIP_FORGE = 40015;
			public static final int TAKE_FIGHT_AWARD = 40016;
			public static final int BADGE_FUSION = 40017;
			public static final int MONEY_COLLETION = 40018;
			public static final int WORLD_SPEAK = 40019;
			public static final int GIVE_FIRENDSHIP = 40020;
			public static final int CALL_HERO = 40021;
			public static final int GLORYHOLE_JOINTIMES = 40022;
			public static final int DAILY_POINT = 40023;
			public static final int CYCLE_STAGE_JOINTIMES = 40024;
			//public static final int GLORY_HOLE_LOGIN_DAY = 40023;
			//public static final int GLORY_HOLE_TAKE_FIGHT_AWARD = 40024;
			//public static final int GLORY_HOLE_GIVE_FIRENDSHIP = 40025;
			
			//public static final int GLORY_HOLE_EQUIP_FORGE = 40027;
		}
				
		/**
		 * 佣兵自动激活
		 */
		public static final int AUTO_EMPLOY_ROLE = 50000;

		public static final int FORMATION_MODIFY = 50001;
		
		/**
		 *TapDB事件回傳
		 */
		public static final int TAPDB_EVENT_RECORD = 50002;
		
//		public static final int NOTIFY_ROLE_PANEL = 50002;
//		
//		public static final int TEST_ROLE_SURMOUNT = 50003;

		/**
		 * 7日之师任务事件
		 */
		// 任务事件
		public static final int SEVENDAY_EVENT = 60000;

		/**
		 * UR抽卡排行
		 */
		public static final int UR_RANK_ADD_SCORE = 70000;

		/**
		 * UR抽卡排行
		 */
		public static final int UR_RANK_PLAYERINFO_SYNC = 70001;
		
		/**
		 * 挖礦排行分數增加
		 */
		public static final int MINING_RANK_ADD_SCORE = 70002;
		
		/**
		 * 壁尻排行榜每日分數更新
		 */
		public static final int GH_RANK_ADD_SCORE = 70003;
		
		/**
		 * 壁尻排行榜最高積更新
		 */
		public static final int GH_RANK_ADD_MAXSCORE = 70004;


		/**
		 * 18路诸侯 协战武将
		 */
		public static final int EIGHTEENPRINCESD_USE_MERCENARYINFO = 80000;

		/**
		 * 18路诸侯 同步协战武将记录数据
		 */
		public static final int EIGHTEENPRINCESD_HELPHISTORY_SYNC = 80002;

		/**
		 * 18路诸侯 协战武将使用错误码同步
		 */
		public static final int EIGHTEENPRINCESD_USE_MERCENARYERROR = 80003;

		/**
		 * 18路诸侯 使用协战武将
		 */
		public static final int EIGHTEENPRINCESD_USE_FINISH = 80004;
	}

	/**
	 * 管理器对象Tick周期
	 */
	public static class ManObjTickPeriod {
		// 快照管理器tick周期
		public static final int SNAPSHOT_MAN_TICK = 60 * 1000;
		// 活动管理器tick周期
		public static final int ACTIVITY_MAN_TICK = 30 * 1000;
	}

	/**
	 * 模块定义, 模块号和对应模块的协议编号相关联，修改已有模块的协议号一定要慎重
	 */
	public static class ModuleType {

		// 状态模块
		public static final int STATE_MODULE = 1;
		// 登陆模块
		public static final int LOGIN_MODULE = 2;
		// 道具模块
		public static final int ITEM_MODULE = 3;
		// 技能模块
		public static final int SKILL_MODULE = 4;
		// 装备模块
		public static final int EQUIP_MODULE = 5;
		// 角色模块
		public static final int ROLE_MODULE = 6;
		// 聊天模块
		public static final int CHAT_MODULE = 7;
		// 竞技场模块
		public static final int ARENA_MODULE = 8;
		// 公会模块
		public static final int ALLIANCE_MODULE = 9;
		// 商店模块
		public static final int SHOP_MODULE = 10;
		// 战斗模块
		public static final int BATTLE_MODULE = 11;
		// 任务模块
		public static final int MISSION_MODULE = 12;
		// 邮件模块
		public static final int MAIL_MODULE = 13;
		// 留言模块
		public static final int MSG_MODULE = 14;
		// 团战模块
		public static final int TEAM_BATTLE_MODULE = 15;
		// 活动模块
		public static final int ACTIVITY_MODULE = 16;
		// 好友模块
		public static final int FRIEND_MODULE = 17;
		// 职业排行模块
		public static final int PROFRANK_MODULE = 18;
		// 荣誉商店模块
		public static final int HONOR_SHOP_MODULE = 19;
		// 称号
		public static final int TITLE_MODULE = 20;
		// 阵营战模块
		public static final int CAMP_WAR_MODULE = 21;
		// 工会战
		public static final int ALLIANCE_BATTLE_MODULE = 22;
		// 世界boss
		public static final int WORLD_BOSS = 23;
		// 多人副本
		public static final int MULTI_ELITE_MODULE = 24;
		// 跨服战
		public static final int CROSS_SERVER = 25;
		// 新手任务
		public static final int QUEST_MODULE = 26;
		// 元素
		public static final int ELEMENT = 27;
		// 全局服务器
		public static final int GLOBAL_SERVER = 28;
		// 帐号绑定
		public static final int ACCOUNT_BOUND = 29;
		// 角色转生
		public static final int REBIRTH = 30;
		// YY主播模块
		public static final int YAYASHOP = 31;
		// 英雄令任务模块
		public static final int HERO_TOKEN_TASK = 32;
		// 翅膀模块
		public static final int WINGS = 33;
		// 新手引导模块
		public static final int GUIDE = 34;
		// GVG模块
		public static final int GVG_MODULE = 35;
		// 奖励模块
		public static final int REWARD_MODULE = 90;
		// 保证在最后
		public static final int IDLE_MODULE = 100;
		// 黑市
		public static final int MYSTERY_SHOP_MODULE = 101;
		// facebook分享
		public static final int FACEBOOK_SHARE_EVENT = 102;
		// 账号绑定奖励
		public static final int PLAYER_BIND_PRICE = 103;
		// 佣兵远征
		public static final int EXPEDITION_TASK = 104;
		// 日常任务
		public static final int DAILY_QUEST = 110;
		// 水晶商店
		public static final int CRYSTAL_SHOP = 111;
		// 阵型
		public static final int FORMATION = 112;
		// 星魂
		public static final int STAR_SOUL_MODULE = 113;
		// 图鉴羁绊
		public static final int ARCHIVE_MODULE = 114;

		// 7日之诗活动
		public static final int ACC_LOGIN_SEVENDAY = 115;

		// 十八路诸侯
		public static final int EIGHTEENPRINCES_MODULE = 116;
		// 徽章模块
		public static final int BADGE_MODULE = 117;
		// 箴言模块
		public static final int MOTTO_MODULE = 118;
		// 公會魔典模块
		public static final int GUILDBUFF_MODULE = 119;
		// 地下城
		public static final int DUNGEON_MODULE = 120;
		// 公告欄
		public static final int BULLETIN_MODULE = 121;
		// 週任务
		public static final int WEEKLY_QUEST = 122;
		// 循環關卡 191,196
		public static final int CYCLESTAGE_MODULE = 123;
		// 單人強敵
		public static final int SINGLEBOSS_MODULE = 124;
		// 賽季爬塔
		public static final int SEASONTOWER_MODULE = 125;
		// 九宮格
		public static final int PUZZLEBATTLE_MODULE = 126;
	}

	/**
	 * 模块对象Tick周期
	 */
	public static class ModuleTickPeriod {

	}

	/**
	 * 服务器状态ID定义
	 */
	public static class ServerStatusId {
		/**
		 * 红包
		 */
		public static final int RED_ENVELOPE = 2;
		/**
		 * 排名献礼
		 */
		public static final int RANK_GIFT = 3;
		/**
		 * 竞技场
		 */
		public static final int ARENA = 4;
		/**
		 * 黑市竞拍
		 */
		public static final int MYSTERY_SHOP_AUCTION = 5;
		/**
		 * 壁尻活動
		 */
		public static final int GLORYHOLE = 6;
	}

	/**
	 * 角色类型
	 */
	public static class RoleType {
		// 主角
		public static final int MAIN_ROLE = 1;
		// 英雄
		public static final int MERCENARY = 2;
		// 怪物
		public static final int MONSTER = 3;
		// 精靈
		public static final int SPRITE = 4;
		// 世界Boss
		public static final int WORLDBOSS = 5;
	}

	/**
	 * 佣兵激活类型
	 * 
	 * @author Administrator
	 *
	 */
	public static class RoleSoulExchangeType {
		// 等级达到激活
		public static final int ROLE_LEVEL = 1;
		// vip达到激活
		public static final int ROLE_VIP = 2;
		// 材料兑换激活
		public static final int ROLE_SOUL = 3;
	}

	/**
	 * 佣兵光环属性添加类型
	 * 
	 * @author ZDZ
	 *
	 */
	public static class RingAttType {
		/**
		 * 佣兵
		 */
		public static final int MERCENARY_TYPE = 0;
		/**
		 * 佣兵 + 主角
		 */
		public static final int ROLE_MERCENARY_TYPE = 1;
	}

	/**
	 * 佣兵光环属性百分比or数值
	 * 
	 * @author ZDZ
	 *
	 */
	public static class RingValueType {
		/**
		 * 百分比		
		*/
		public static final int PERCENTAGE_TYPE = 1;
		/**
		 * 数值
		 */
		public static final int NUMERICAL_TYPE = 2;
		/**
		 * 数值 * 等级
		 */
		public static final int PERCENTAGE_GRADE_TYPE = 3;
		
	}

	/**
	 * 职业类型
	 * 
	 * @author WeiY
	 *
	 */
	public static class ProfType {

		// 战士
		public static final int WARRIOR = 1;
		// 猎人
		public static final int HUNTER = 2;
		// 法师
		public static final int MASTER = 3;
	}

	/**
	 * 装备相关
	 */
	public static class Equip {
		/**
		 * 装备打造刷新消耗钻石
		 */
		public static final int EQUIP_CREATE_REFRESH = 20;
		/**
		 * 装备精华Id
		 */
		public static final int ITEM_STRENGTH_ID = 60001;
		/**
		 * 初始熔炼刷新次数
		 */
		public static final int INIT_EQUIP_SMELT_REFRESH = 2;
		/**
		 * 最大打孔个数
		 */
		public static final int MAX_PUNCH_SIZE = 4;
		/**
		 * 装备最低洗炼等级
		 */
		public static final int BAPTIZE_MIN_LEVEL = 10;
		/**
		 * 每日装备刷新次数
		 */
		public static final int DAILY_EQUIP_CREATE_REFRESH = 2;
		/**
		 * 熔炼目标装备的等级筛选（3个权重选择）
		 */
		public static final int EQUIP_SMELT_WEIGHT_SELECT_THERE = 3;
		/**
		 * 熔炼目标装备的等级筛选（2个权重选择）
		 */
		public static final int EQUIP_SMELT_WEIGHT_SELECT_TWO = 2;
		/**
		 * 装备最大强化等级
		 */
		// public static int EQUIP_MAX_STRENGTH_LEVEL = 20;
		/**
		 * 装备附魔最低等级限制
		 */
		public static final int EQUIP_ENCHANT_MIN_LEVEL = 30;
		/**
		 * 附魔默认等级
		 */
		public static final int EQUIP_ENCHANT_DEFAULT_LEVEL = 1;
		/**
		 * 套装升级耐力上限分配百分比
		 */
		public static final float STAMINA_PERCENT_MAX_LIMIT = 0.25f;
		/**
		 * 套装升级耐力下限分配百分比
		 */
		public static final float STAMINA_PERCENT_MIN_LIMIT = 0.20f;
		/**
		 * 套装耐力上限分配百分比等级控制
		 */
		public static final int STAMINA_PERCENT_LEVEL_LIMIT = 100;
		/**
		 * 角色等级与套装升级后的等级差
		 */
		public static final int GRADE_GAP_LIMIT = 10;
	}

	public static class Wings {
		/**
		 * 羽毛的Itemid;
		 */
		public static final int ITEM_WINGS_ID = 80001;

		public static final String WHITE = "@wingshitu";
		public static final String GREEN = "@wingqishi";
		public static final String BLUE = "@wingshengling";
		public static final String PURPLE = "@wingshensheng";
		public static final String ORANGE = "@wingzhigao";
	}

	/**
	 * 佣兵相关
	 */
	public static class Role {
		/**
		 * 角色洗炼的四个属性
		 */
		public static final Const.attr[] BAPTIZE_ATTRS = new Const.attr[] { Const.attr.STRENGHT, Const.attr.AGILITY,
				Const.attr.INTELLECT, Const.attr.STAMINA };

	}

	/**
	 * 竞技场模块常量
	 * 
	 * @author xpf
	 */
	public static class Arena {
		/**
		 * 竞技场挑战对手身份类型
		 */
		public static final int ROBOT_OPPONENT = 1;
		public static final int PLAYER_OPPONENT = 2;

		/**
		 * 挑战结果
		 */
		public static final int CHALLENGE_FAIL = 1;
		public static final int CHALLENGE_SUCC = 2;
	}

	/**
	 * 公会模块
	 */
	public static class Alliance {

		/**
		 * 道具类型
		 */
		public static final String ALLIANCE_TYPE = "t";
		public static final String ALLIANCE_ID = "i";
		public static final String ALLIANCE_NUMBER = "q";
		public static final String ALLIANCE_CONTRIBUTION = "c"; // 贡献
		public static final String ALLIANCE_PROFESSION = "p"; // 职业
		/** 权重 */
		public static final String ALLIANCE_WEIGHT = "w";

		/*
		 * 0:普通成员, 1:副会长, 2:会长
		 */
		public static final int ALLIANCE_POS_COMMON = 0;
		public static final int ALLIANCE_POS_COPYMAIN = 1;
		public static final int ALLIANCE_POS_MAIN = 2;

		// boss 攻击频率
		public static final long BOSS_ATT_TIME = 5 * 60000;
		// boss免费次数
		public static final int BOSS_FREE_TIMES = 0;
		// 推荐列表每页最大显示公会数
		public static final int ONE_PAGE_SIZE = 10;
		// 公会商店刷新每次增长的幸运值
		public static final int REFRESH_SHOP_ADD_LUCKY = 1;
		// 公会商店幸运值最大值
		public static final int MAX_SHOP_LUCKY = 1000;
		// 公会商店幸运值重置间隔(毫秒)
		public static final long RESET_LUCKY_SCORE_INTERVAL = 7 * 24 * 60 * 60 * 1000;
	}

	/**
	 * 任务模块
	 */
	@Deprecated
	public static class Mission {
		/**
		 * 首次充值
		 */
		public static final int TYPE_FRIST_RECHARGE = 1;
		/**
		 * 充值累计
		 */
		public static final int TYPE_RECHARGE = 2;
		/**
		 * 开服天数
		 */
		public static final int TYPE_OPENSERVER = 3;
		/**
		 * 连续签到奖励
		 */
		public static final int TYPE_REPORT = 4;
		/**
		 * 击杀BOSS奖励
		 */
		public static final int TYPE_KILLBOSS = 5;
		/**
		 * 升级奖励
		 */
		public static final int TYPE_LEVELUP = 6;
		/**
		 * 充值次数
		 */
		public static final int TYPE_RECHARGESIZE = 7;
		/**
		 * 任务结束
		 */
		public static final int TYPE_END = 8;
	}

	/**
	 * 道具数量检测
	 */
	public static class PlayerItemCheckResult {
		/**
		 * 金币不足
		 */
		public static final int COINS_NOT_ENOUGH = 1;
		/**
		 * 元宝不足
		 */
		public static final int GOLD_NOT_ENOUGH = 2;
		/**
		 * 熔炼值不足
		 */
		public static final int SMELT_VALUE_NOT_ENOUGH = 3;
		public static final int LEVEL_NOT_ENOUGH = 4;
		public static final int EXP_NOT_ENOUGH = 5;
		public static final int VIPLEVEL_NOT_ENOUGH = 6;
		/**
		 * 道具不足
		 */
		public static final int TOOLS_NOT_ENOUGH = 7;
		/**
		 * 装备不足
		 */
		public static final int EQUIP_NOI_ENOUGH = 8;
		public static final int HONOR_NOT_ENOUGH = 9;
		public static final int REPUTATION_NOT_ENOUGH = 10;
		public static final int CROSSCOINS_NOT_ENOUGH = 15;
		public static final int FRIENDSHIP_NOT_ENOUGH = 16;
		
		public static final int CRYSTAL_NOT_ENOUGH = 20;

		/**
		 * 元素不足
		 */
		public static final int ELEMENT_NOI_ENOUGH = 11;

		/** 公会贡献值不足 */
		public static final int CONTRIBUTION_NOT_ENOUGH = 13;
		/** 與MarketPlace不同步 */
		public static final int MARKETPLACE_NOT_SYNC = 14;
	}

	/**
	 * 留言模块
	 * 
	 * @author xpf
	 * 
	 */
	public static class Msg {
		// 是否有新消息
		public static final int NO_NEW_MSG = 0;
		public static final int HAS_NEW_MSG = 1;

	}

	/**
	 * 团战模块
	 * 
	 * @author xpf
	 * 
	 */
	public static class TeamBattle {
		// 是否是队长
		public static final int IS_NOT_CAPTAIN = 1;
		public static final int IS_CAPTAIN = 2;

		// 是否已加入团队
		public static final int HAS_NOT_JOIN = 1;
		public static final int IS_JOIN = 2;

		// 上次团战结束，下次团战未开始报名
		public static final int STATE_END_SWITCH = -1;
		// 团战准备状态，开始报名
		public static final int STATE_PREPARE = 0;
		// 团战开始，进入战斗
		public static final int STATE_START = 1;

	}

	/**
	 * 所有邮件Id定义
	 * 
	 * @author xulin
	 */
	public static class MailId {
		/** 竞技场奖励 */
		public static final int ARENA_AWARD = 1;
		/** 竞技场排名下降(邮箱) */
		public static final int ARENA_RANK_DROP = 2;
		/** 团战胜利者奖励（冠军） */
		public static final int TEAM_BATTLE_WINNER_AWARD = 3;
		/** 团战失败者奖励 */
		public static final int TEAM_BATTLE_LOSER_AWARD = 4;
		/** 团战胜利者战报（冠军） */
		public static final int TEAM_BATTLE_WINNER_REPORT = 5;
		/** 团战失败者战报 */
		public static final int TEAM_BATTLE_LOSER_REPORT = 6;
		/** 月卡奖励 */
		public static final int MONTH_CARD_REWARD = 7;
		/** 职业排名奖励 */
		public static final int PROF_RANK_REWARD = 8;
		/** 竞技场排名上升 */
		public static final int ARENA_RANK_UP = 9;
		/** 竞技场别人挑战自己失败 */
		public static final int ARENA_CHALLANGE_SELF_FAIL = 10;
		/** 竞技场自己挑战别人失败 */
		public static final int ARENA_CHALLANGE_OTHER_FAIL = 11;
		/** 竞技场排名下降（竞技记录） */
		public static final int ARENA_RANK_DROP_RECORD = 12;
		/** 阵营战霜狼（左）阵营胜利 */
		public static final int CAMPWAR_LEFT_CAMP_WIN = 13;
		/** 阵营战霜狼（左）阵营失败 */
		public static final int CAMPWAR_LEFT_CAMP_LOSE = 14;
		/** 阵营战炎狮（右）阵营胜利 */
		public static final int CAMPWAR_RIGHT_CAMP_WIN = 15;
		/** 阵营战炎狮（右）阵营失败 */
		public static final int CAMPWAR_RIGHT_CAMP_LOSE = 16;
		/** 阵营战投资奖励邮件 */
		public static final int CAMPWAR_AUTO_AWARDS = 17;
		/** 远征物资活动阶段奖励 */
		public static final int EXPEDITION_ARMORY_STAGE_AWARD = 18;
		/** 远征物资活动排名奖励 */
		public static final int EXPEDITION_ARMORY_RANK_AWARD = 19;
		/** 工会战投资赢了奖励 */
		public static final int ALLIANCE_BATTLE_INVEST_WIN_AWARD = 20;
		/** 工会战投资输了奖励 */
		public static final int ALLIANCE_BATTLE_INVEST_FAIL_AWARD = 21;
		/** 工会战结果奖励 */
		// public static final int ALLIANCE_BATTLE_RESULT_REWARD = 22;
		/** WORLD BOSS离线攻击奖励 */
		public static final int WORLD_BOSS_OFFLINE_ACTION = 23;
		/** WORLD BOSS伤害奖励 */
		public static final int WORLD_HARM_RANK = 24;
		/** WORLD BOSS击杀奖励 */
		public static final int WORLD_BOSS_KILL = 25;
		/** 多人副本胜利奖励 */
		public static final int MULTI_ELITE_WIN_AWARD = 26;
		/** 多人副本胜利战报 */
		public static final int MULTI_ELITE_WIN_REPORT = 27;
		/** 多人副本失败战报 */
		public static final int MULTI_ELITE_LOSE_REPORT = 28;
		/** 跨服战参与将 */
		public static final int CS_PARTICIPATION = 29;
		/** 跨服战冠军服奖励 */
		public static final int CS_CHAMPION_SERVER = 30;
		/** 排名献礼竞技场奖励 */
		public static final int RANK_GIFT_ARENA = 31;
		/** 排名献礼经验奖励 */
		public static final int RANK_GIFT_EXP = 32;
		/** 排名献礼经验奖励 */
		public static final int YAYA_RANK_GIFT = 33;
		/** 跨服战每日奖励 */
		public static final int CROSS_RANK_REWARD = 40;
		/** 跨服战赛季奖励 */
		public static final int CROSS_SEASON_REWARD = 41;
		/** facebook分享 */
		public static final int FACE_BOOK_SHARE = 50;
		public static final int ALLIANCE_BOSS = 51;
		/** FaceBook绑定奖励 */
		public static final int FACE_BOOK_BINDING = 52;
		/** 合服奖励邮件 */
		public static final int MEGER_SERVER_REWARDS = 53;
		/** cdkey邮件奖励 */
		public static final int CDK_MAIL_REWARDS = 54;
		/** 账号绑定奖励 */
		public static final int PLAYER_BIND_PRICE = 55;
		/** 佣兵远征奖励 */
		public static final int EXPEDITION_TASK = 56;
		/** 捞鱼活动排名奖励 */
		public static final int GOLDFISH_RANK_AWARD = 57;
		/** 工会战结果奖励 */
		public static final int ALLIANCE_BATTLE_RESULT_REWARD = 58;
		/** 神装锻造活动奖励 */
		public static final int FORGING_REWARD = 59;
		/** 当前魔兽已经开启邮件 */
		public static final int ALLIANCE_BOSS_OPEN_MAILID = 60;
		/** 魔兽元气不足邮件 */
		public static final int BOSS_VITALITY_LACK_MAILID = 61;
		/** 魔兽解禁次数不足邮件 */
		public static final int BOSS_OPEN_TIMES_MAILID = 62;
		/** 公会手动解禁BOSS成功 */
		public static final int ALLIANCE_LIFT_BOSS = 63;
		/** 公会自动解禁BOSS成功 */
		public static final int AUTOMATIC_OPEN_BOSS = 64;
		/** 财富俱乐部奖励 */
		public static final int WEALTH_CLUB = 65;
		/** 創角奖励1 */
		public static final int CREATE_ROLE = 66;
		/** 創角奖励2 */
		public static final int CREATE_ROLE2 = 67;
		/** 創角奖励3 */
		public static final int CREATE_ROLE3 = 68;
		/**創角奖励4 */
		public static final int CREATE_ROLE4 = 69;
		
		/** 充值反馈 */
		public static final int PLAYER_RECHARGE_GOLD = 100;
		public static final int PLAYER_RECHARGE_MONTH_CARD = 101;
		public static final int PLAYER_RECHARGE_GIFT = 102;
		public static final int PLAYER_RECHARGE_GROWTH_PASS = 132;
		public static final int PLAYER_SUPPORT_CALENDER = 133;
		public static final int PLAYER_SUBSCRIPTION = 134;
		public static final int PLAYER_LEVEL_GIFT = 135;
		public static final int PLAYER_STAGE_GIFT = 136;
		public static final int PLAYER_FAILED_GIFT = 137;
		public static final int PLAYER_STEP_GIFT = 138;
		/** 会长邮件 */
		public static final int GUILDER_MAIL = 103;
		/** 工会战中签资格通知邮件 */
		public static final int ALLIANCE_BATTLE_SELECTED = 104;
		/** 老玩家登录奖励宝石 */
		public static final int PLAYER_LOGIN_GEM_PRICE = 105;
		/** 申请加入公会邮件 */
		public static final int APPLY_ADD_ALLIANCE = 106;
		/** 拒绝加入公会邮件 */
		public static final int REFUSED_INIO_ALLIANCE = 107;
		/** 因会长转让申请邮件已经失效 */
		public static final int CHANGE_MAIN_ALLIANCE = 108;
		/** 给公会成员发会长变动的邮件 */
		public static final int CHANGE_MAIN_SENDTO_COMMON = 109;
		/** 给老会长发会长变动的邮件 */
		public static final int CHANGE_MAIN_SENDTO_OLD_MAIN = 110;
		/** 给新会长发会长变动的邮件 */
		public static final int CHANGE_MAIN_SENDTO_NEW_MAIN = 111;
		/** 会长邮件过期,不能进行审核操作 */
		public static final int EMAIL_FAILURE_NOT_OPER = 112;
		/** 删除道具邮件 */
		public static final int DELETE_ITEM_EMAIL = 113;
		/** 加入公会邮件(个人) */
		public static final int ADD_ALLIANCE_PERSON_EMAIL = 114;
		/** 加入公会邮件(全体) */
		public static final int ADD_ALLIANCE_ALL_EMAIL = 115;
		/** 退出公会邮件(个人) */
		public static final int EXIT_ALLIANCE_PERSON_EMAIL = 116;
		/** 自己退出公会邮件(全体) */
		public static final int OWN_EXIT_ALLIANCE_ALL_EMAIL = 117;
		/** 会长踢出公会邮件(全体) */
		public static final int KICK_ALLIANCE_ALL_EMAIL = 118;
		/** 合服完成后，给会长发通知改公会名 **/
		public static final int NOTICE_CHANGE_ALLIANCE_NAME = 119;
		/** 充值周卡邮件 */
		public static final int PLAYER_RECHARGE_NEW_WEEK_CARD = 130;
		/** 充值消耗型周卡邮件 new月卡*/
		public static final int PLAYER_RECHARGE_CONSUME_MONTH_CARD = 131;
		/** 特權禮包郵件ID */
		public static final int SUBSCRIPTION_GIFT_MAIL = 139;

		/* GVG邮件 */
		/** 宣战方邮件 */
		public static final int GVG_MARAUDER = 140;
		/** 被宣战方邮件 */
		public static final int GVG_HOLDER = 141;
		/** 宣战方邮件---城池持有者是NPC */
		public static final int GVG_MARAUDER_NPC = 142;
		/** 进攻胜利---攻击方邮件 */
		public static final int GVG_ATTACKER_WIN = 143;
		/** 进攻胜利---防守方邮件 */
		public static final int GVG_DEFENDER_FAIL = 144;
		/** 发起反攻---攻击方邮件 */
		public static final int FIGHTBACK_ATTACKER = 145;
		/** 发起反攻---防守方邮件 */
		public static final int FIGHTBACK_DEFENDER = 146;
		/** 联盟伤害奖励 */
		public static final int ALLIANCE_WORLD_HARM_RANK = 147;
		/** 联盟击杀奖励 */
		public static final int ALLIANCE_WORLD_BOSS_KILL = 148;
		/** 周卡邮件ID **/
		public static final int WEEK_CARD_MAIL = 149;
		/** 周卡邮件ID **/
		public static final int CONSUME_WEEK_CARD_MAIL = 150;
		/** 竞拍价格被超价 */
		public static final int AUCTION = 3201;
		/** 竞拍成功 */
		public static final int AUCTION_SUCCESS = 3202;
		/** 同意好友申请邮件 */
		public static final int AGREE_APPLY_FRIEND = 4001;
		/** 拒绝好友申请邮件 */
		public static final int REFUSED_APPLY_FRIEND = 4002;
		/** 购买聊天皮肤成功 */
		public static final int BUY_CHAT_SKIN_SUCC = 5001;
		/** 鬼节活动礼包发放给公会所有成员 */
		public static final int OBON_GIFT_ALLIANCE = 5002;
		/** 鬼节活动礼包发放给自己 */
		public static final int OBON_GIFT_SELF = 5003;
		/** 百花美人新手限时活动提前通知邮件 **/
		public static final int HAREM_NEW_STRICT_MAIL = 5004;
		/** 新手UR抽奖活动邮件提前通知邮件 **/
		public static final int NEW_UR_MAIL = 5005;
		/** 城战每日奖励 **/
		public static final int EVERYDAY_MAIL = 7001;
		/** 城战赛季奖励 **/
		public static final int SEANSON_MAIL = 7002;
		/** 城战每日无城发邮件 **/
		public static final int EVERYDAY_NOCITY_MAIL = 7003;
		/** UR积分排行邮件编号 **/
		public static final int UR_RANKMANAGER_MAIL = 7004;
		/** 和服补偿 **/
		public static final int MERGER_SERVER_MAIL = 7005;
		/** 公会Boss挑战失败 **/
		public static final int ALLIANCE_BOSS_FAil = 7006;
		/** 挖礦積分排行邮件编号 **/
		public static final int MINING_RANKMANAGER_MAIL = 7010;
		/** 壁尻每日積分排行邮件编号 **/
		public static final int GH_DAILY_MAIL = 7011;
		/** 壁尻最高積分排行邮件编号 **/
		public static final int GH_HIGH_SCORE_MAIL = 7012;
		
		/** 壁尻隊伍勝方邮件编号 **/
		public static final int GH_TEAM_WINER_MAIL = 7013;
		/** 壁尻隊伍拜方邮件编号 **/
		public static final int GH_TEAM_LOSER_MAIL = 7014;
		/** 壁尻每日任務未領邮件编号 **/
		public static final int GH_DAILY_QUEST_MAIL = 7015;
		/** 單人強敵排行榜邮件编号 **/
		public static final int SINGLE_BOSS_MAIL = 7016;
		/** 季爬塔排行榜邮件编号 **/
		public static final int SEASON_TOWER_MAIL = 7017;
	}

	/**
	 * 活动时间类型
	 * 
	 * @author xulinqs
	 *
	 */
	public static enum ActivityTimeType {
		NONE,
		/**
		 * 一直开放 1
		 */
		ALWARDS_OPEN,
		/**
		 * 开服时间延后时间 2
		 */
		SERVER_OPEN_DELYS,
		/**
		 * 周期时间开放 3
		 */
		CYCLE_TIME_OPEN,
		/**
		 * 注册天数活动 4
		 */
		REGISTER_CYCLE,
		/**
		 * 已关闭，不开放 5
		 */
		CLOSED;

		public static ActivityTimeType valueOf(int timeType) {
			if (timeType <= 0 && timeType >= ActivityTimeType.values().length) {
				throw new ArrayIndexOutOfBoundsException();
			}
			return ActivityTimeType.values()[timeType];
		}
	}

	/**
	 * 新手充值返利活动
	 */
	public static class RechargeRebateActivity {
		// 活动已关闭
		public static final int STATUS_CLOSE = -1;
		// 延时状态(活动进入延迟天数，充值不计入活动)
		public static final int STATUS_DELAY = 0;
		// 充值状态(充值可以继续计入活动)
		public static final int STATUS_RECHARGE = 1;
		// 返利状态(充值不会计入活动，只能领取返利)
		public static final int STATUS_REBATE = 2;
	}

	/**
	 * 打折礼包
	 */
	public static class SalePacketActivity {
		public static final int STATUS_CLOSE = -1;
		public static final int STATUS_OPEN = 1;
	}

	/**
	 * 连续充值
	 */
	public static class ContinueRechargeActivity {
		public static final int STATUS_CLOSE = -1;
		public static final int STATUS_OPEN = 1;
	}

	/**
	 * 荣誉商店
	 */
	public static class HonorShop {
		public static int HONOR_SHOP_ITEM_SIZE = 6;
	}

	/**
	 * 水晶商店
	 */
	public static class CrystalShop {
		public static final int CRYSTAL_SHOP_ITEM_SIZE = 6;
	}

	/**
	 * 远征任务个数
	 */
	public static class MercenaryExpedition {
		public static final int EXPEDITION_TASK_SIZE = 5;
		public static final int EXPEDITION_ONCE_ROLE = 3;
	}

	/**
	 * 阵营战模块
	 * 
	 * @author xpf
	 *
	 */
	public static class CampWar {
		public static final int NOT_AUTO_JOIN = 0;
		public static final int AUTO_JOIN = 1;

		public static final int NOT_JOIN = 0;
		public static final int ALREADY_JOINED = 1;

		public static final int LEFT_CAMP_ID = 1;
		public static final int RIGHT_CAMP_ID = 2;
	}

	/**
	 * 玩家称号
	 */
	public static class Title {
		public static final int WORLD_EXTREME = 1; // 天下至尊
		public static final int TOP_PEOPLE = 2; // 万人之上
		public static final int KING_SUPREME = 3; // 王者之尊
		public static final int ATHLETIC_ELITE = 4; // 竞技精英
		public static final int TYRANT = 5; // 土豪
		public static final int RICH_HANDSOME = 6; // 高富帅
		public static final int GOD_TRENCH = 7; // 神壕
		public static final int MODICUM_OF_SUCCESS = 8; // 小有成就
		public static final int PERSEVERANCE_ISCOMMENDABLE = 9; // 毅力可嘉
		public static final int HUNDRED_DAYS_INTO_GOLD = 10; // 百日成金
		public static final int MAD_YAO_HYUN_FLASH = 11; // 狂耀炫闪
		public static final int SUPREME_LAW_OF_GOD = 12; // 至尊法神
		public static final int EXTREME_AREA = 13; // 至尊战神
		public static final int EXTREME_HUNTING_GOD = 14; // 至尊猎神
		public static final int BUDDHA_FASTER = 15; // 天尊法神
		public static final int BUDDHA_WARRIOR = 16; // 天尊战神
		public static final int BUDDHA_HUNTER = 17; // 天尊猎神
		public static final int LAND_FASTER = 18; // 地尊法神
		public static final int LAND_WARRIOR = 19; // 地尊战神
		public static final int LAND_HUNTER = 20; // 地尊猎神
		public static final int STORM_KING = 21; // 风暴之王
		public static final int TEN_STAR_ARTIFACT = 22; // 十星神器
	}

	/**
	 * 邮件大类别
	 */
	public static class EmailClassification {
		// 普通
		public static final int COMMON = 1;
		// 系统
		public static final int SYSTEM = 2;
	}

	/**
	 * 公会争霸
	 */
	public static class AllianceBattle {
		/**
		 * 公会显示的可以参战的最大的公会个数
		 */
		public static final int ALLIANCE_BATTLE_RANK_SIZE = 32;

		public static final int TEAM_INDEX_1 = 1;
		public static final int TEAM_INDEX_2 = 2;
		public static final int TEAM_INDEX_3 = 3;

		public static final int[] ALL_TEAM = new int[] { TEAM_INDEX_1, TEAM_INDEX_2, TEAM_INDEX_3 };

		// 左边赢
		public static final int WIN_SIDE_LEFT = 1;
		// 右边赢
		public static final int WIN_SIDE_RIGHT = 2;

		// 冠军
		public static final int TOP_1 = 1;
		// 亚军
		public static final int TOP_2 = 2;
		// 4强
		public static final int TOP_4 = 3;
		// 8强
		public static final int TOP_8 = 4;
		// 16强
		public static final int TOP_16 = 5;
		// 32强
		public static final int TOP_32 = 6;
		// 普通大众
		public static final int TOP_COMMON = 7;

		// 上届冠军buff
		public static final int STREAK_BUFF_ID_1 = 1;
		// 连胜两个阶段
		public static final int STREAK_BUFF_ID_2 = 2;
		// 连胜三个阶段
		public static final int STREAK_BUFF_ID_3 = 3;
		// 连胜四个阶段
		public static final int STREAK_BUFF_ID_4 = 4;
		// 连胜五个阶段
		public static final int STREAK_BUFF_ID_5 = 5;

	}

	/**
	 * MemCache 缓存对象Key模板
	 */
	public static class MemCacheObjKey {
		// 连接保活对象key
		public static final String MC_KEEP_ALIVE_OBJKEY = "org.hawk.cdk.author";
		// 玩家快照对象key
		public static final String MC_SNAPSHOT_OBJKEY_FMT = "snapshot.%s.%s.%s.%s";
	}

	/**
	 * Redis 缓存对象key
	 */
	public static class RedisCacheObjKey {
		// 玩家邀请码
		public static final String PLAYER_INVITECODE_KEY_FMT = "inviteCode.%s";
		// 出租车抵价码
		public static final String TAXI_EXCHANGE_CODE_KEY = "taxiCode";
		// 使用出租车抵价码的设备id
		public static final String TAXI_DEVICE_ID_FMT = "taxi.deviceid.%s";
	}

	/**
	 * 技能战斗特殊标记
	 * 
	 * @author xulinqs
	 *
	 */
	public static class SkillSpecialFlag {
		// 下次技能攻击伤害加成
		public static final int NEXT_SKILL_HARM_ADD = 1;
		// 上次技能释放
		public static final int LAST_USE_SKILL_ID = 42;
		public static final int EXTRA_MP_SKILL_ID = 27;
		public static final int MULTI_ELITE_MAP_BOSS = 100;
	}

	public static class CSBattleStage {
		// 未开启状态
		public static final int UNOPEN = 0;
		// 报名
		public static final int SIGNUP = 2;

		// 本服淘汰
		public static final int LS_KONCKOUT = 4;
		// 本服16进8
		public static final int LS_16_8 = 6;
		// 本服8进4
		public static final int LS_8_4 = 8;
		// 本服4进2
		public static final int LS_4_2 = 10;
		// 本服2进1
		public static final int LS_2_1 = 12;
		// 本服冠军
		public static final int LS_HEGEMONY_WINNER = 13;

		// 跨服淘汰
		public static final int CS_KONCKOUT = 14;
		// 跨服16进8
		public static final int CS_16_8 = 16;
		// 跨服8进4
		public static final int CS_8_4 = 18;
		// 跨服4进2
		public static final int CS_4_2 = 20;
		// 跨服2进1
		public static final int CS_2_1 = 22;
		// 跨服冠军
		public static final int CS_HEGEMONY_WINNER = 23;

		// 回顾
		public static final int REVIEW = 24;
		// 已完成
		public static final int FINISH = 26;
	};

	public static final class CSBattleRewardCategory {
		// 1:本服胜者排名
		public static final int MS_WIN_RANK = 1;
		// 2:本服败者排名
		public static final int MS_LOSE_RANK = 2;
		// 3:跨服胜者排名
		public static final int CS_WIN_RANK = 3;
		// 4:跨服败者排名
		public static final int CS_LOSE_RANK = 4;
		// 5:参与奖
		public static final int PARTICIPATION_AWARD = 5;
		// 6:冠军服阳光普照奖
		public static final int NATIONAL_AWARD = 6;

	};

	/**
	 * 押注胜负状态
	 */
	public static class BetWinState {
		// 押注失败
		public static final int LOSE = 0;
		// 押注成功
		public static final int WIN = 1;
	}

	/**
	 * 押注奖励领取状态
	 * 
	 * @author WangXP
	 */
	public static class BetRewardStatus {
		/** 未开始 1 */
		public static final int UNOPEN = 1;
		/** 未领取 2 */
		public static final int UNREWARD = 2;
		/** 已领取 3 */
		public static final int REWARDED = 3;
	}

	public static class ForeverStatus {
		/** 1:未开启,无资格 */
		public static final int UNOPEN_UNABLE = 1;
		/** 2: 未开启,有资格 */
		public static final int UNOPEN_ABLE = 2;
		/** 3:已开启,今日未领取 */
		public static final int OPEN_UNDRAW = 3;
		/** 4:已开启,今日已领取 */
		public static final int OPEN_DRAW = 4;
	}

	public static class MapType {
		/** 0: 普通地图 */
		public static final int GENERAL_MAP = 0;
		/** 1: 精英地图 */
		public static final int ELITE_MAP_ONE = 1;
		public static final int ELITE_MAP_TWO = 2;
		public static final int ELITE_MAP_THR = 3;
		/** 4: 转生地图 */
		public static final int REBIRTH_MAP = 4;
	}

	public static class MysteryShopType {
		/** 贩卖 */
		public static final int SELL = 0;
		/** 竞拍 */
		public static final int AUCTION = 1;
		/** 贩卖竞拍皆可 */
		public static final int SELL_AUCTION = 2;
		/** 黑市最大商品数 */
		public static final int MAXITEMNUM = 6;
	}

	public static class StatsdType {
		/** 玩家登录上报 */
		public static final String STATSD_PLAYER_LOGIN = "user.login";
		/** 玩家下线上报 */
		public static final String STATSD_PLAYER_LOGOUT = "user.logout";
		/** 充值金额上报 */
		public static final String STATSD_PLAYER_RECHARGE_MONEY = "user.pay.money";
		/** 在线数量上报 */
		public static final String STATSD_ONLINE_PLAYER = "user.online";
	}

	public static class GuideType {
		/** 新手引导第一步 */
		public static final int FIRST_STEP = 1;
	}

	public static class IOSActionType {
		/** 总付费额 */
		public static final String REVENUE = "uhn9tg";
	}

	public static class AndroidActionType {
		/** 总付费额 */
		/* 谷歌 */
		public static final String GOOGLE_REVENUE = "uohpqe";
		/* 亚马逊 */
		public static final String AMAZON_AREVENUE = "j3zs97";
	}

	public static class AdjustActionType {
		/* 开始游戏 */
		public static final String CLICKSTARTGAME = "clickstartgame";
		/* 创建角色 */
		public static final String CREATORSUCCESS = "creatorsuccess";
		/* 完成新手引导 */
		public static final String TURORIALSUCCESS = "tutorialsuccess";
		/* 升级到15级 */
		public static final String LEVEL15 = "level15";
		/* 升级到30级 */
		public static final String LEVEL30 = "level30";
		/* 升级到45级 */
		public static final String LEVEL45 = "level45";
		/* 升级到60级 */
		public static final String LEVEL60 = "level60";
		/* 战力到30000 */
		public static final String CE30000 = "CE30000";
		/* 加入联盟 */
		public static final String GUILDJOIN = "guildjoin";

	}
	
	/**
	 * 壁尻任務type
	 */
	public static class GloryHoleMissionType {
		/**
		 * 參與活動
		 */
		public static final int JOIN_ACTIVITY = 1;
		/**
		 * 累積分數
		 */
		public static final int ACCUMULATE_SCROES = 2;
		/**
		 * 狂熱
		 */
		public static final int FANATIC = 3;
		/**
		 * 良好判定
		 */
		public static final int GOOD_JOB = 4;
		/**
		 * 使用道具
		 */
		public static final int USE_ITEM = 5;
		/**
		 * 最高分
		 */
		public static final int HIGH_SCROE = 6;
		
	}

}
