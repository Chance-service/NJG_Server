package com.guaji.game.log;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.guaji.db.DBManager;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;

import com.guaji.game.GsConfig;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.player.Player;
import com.guaji.game.util.PlayerUtil;

import net.sf.json.JSONObject;

/**
 * 行为日志
 */
public class BehaviorLogger {
    /**
     * 日志源
     */
    public static enum Source {
        /**
         * 用户操作
         */
        USER_OPERATION,
        /**
         * 系统操作
         */
        SYS_OPERATION,
        /**
         * GM操作
         */
        GM_OPERATION,
        /**
         * 武将移除
         */
        ROLE_REMOVE,
        /**
         * 角色添加
         */
        ROLE_ADD,
        /**
         * 装备移除
         */
        EQUIP_REMOVE,
        /**
         * 装备增加
         */
        EQUIP_ADD,
        /**
         * 元素增加
         */
        ELEMENT_ADD,
        /**
         * 道具移除
         */
        TOOLS_REMOVE,
        /**
         * 道具增加
         */
        TOOLS_ADD,
        /**
         * 属性改变
         */
        PLAYER_ATTR_CHANGE,
        /**
         * 钻石消耗
         */
        GOLD_REDUCE,
        /**
         * 游戏奖励
         */
        GAME_SYS_REWARD,
        /**
         * 邮件添加
         */
        EMAIL_ADD,
        /**
         * 邮件读取
         */
        EMAIL_REMOVE,
        /**
         * 元素删除
         */
        ELEMENT_REMOVE,

        /**
         * 装备移除
         */
        BADGE_REMOVE,
        /**
         * 装备增加
         */
        BADGE_ADD,
        /**
         *皮膚增加
         */
        SKIN_ADD,
        /**
         * 未知源
         */
        UNKNOWN_SOURCE;
    }

    /**
     * 行为定义
     */
    public static enum Action {
        /**
         * 无明显Action操作的行为
         */
        NULL,
        /**
         * 初始化数据
         */
        INIT,
        /**
         * 系统行为
         */
        SYSTEM,
        /**
         * 登录游戏
         */
        LOGIN_GAME,
        /**
         * 登出游戏
         */
        LOGOUT_GAME,
        /**
         * 每日重置
         */
        DAILY_RESET,
        /**
         * 创建角色
         */
        CREATE_ROLE,
        /**
         * 修改姓名
         */
        ROLE_RENAME,
        /**
         * 角色升级
         */
        ROLE_LEVEL_UP,
        /**
         * 首次充值
         */
        FIRST_RECHARGE,
        /**
         * 充值
         */
        RECHARGE,
        /**
         * 购买道具
         */
        BUY_TOOL,
        /**
         * 使用道具
         */
        TOOL_USE,
        /**
         * 使用小时卡
         */
        TOOL_USE_HOUR_CARD,
        /**
         * 出售道具
         */
        TOOL_SELL,
        /**
         * 刷新神秘商店
         */
        REFRESH_SHOP,
        /**
         * 神秘商店购买
         */
        SHOP_BUY,
        /**
         * 装备再造
         */
        EQUIP_FORGE,
        /**
         * 装备出售
         */
        EQUIP_SELL,
        /**
         * 装备洗炼
         */
        EQUIP_WASH,
        /**
         * 装备高级洗炼
         */
        EQUIP_SUPER_WASH,
        /**
         * 装备强化
         */
        EQUIP_EHANCE,
        /**
         * 扩充装备包囊
         */
        EXT_EQUIP_BAG,
        /**
         * 传书领取 邮件
         */
        SYS_MSG,
        /**
         * GM发放奖励
         */
        GM_AWARD,
        /**
         * CDK兑换
         */
        CDK_REWARD,
        /**
         * 每日首登
         */
        DAILY_FIRST_LOGIN,
        /**
         * 后台充值
         */
        GM_RECHARGE,
        /**
         * 踢出玩家
         */
        GM_KICKOUT,
        /**
         * GM封号处理
         */
        GM_FORBIDEN,
        /**
         * 充值钻石消耗
         */
        FINANCE_GOLD_COST,
        /**
         * 钻石消耗
         */
        GOLD_COST,
        /**
         * 金币消耗
         */
        COIN_COST,
        /**
         * 使用cdk
         */
        USE_CDK,
        /**
         * 游戏奖励
         */
        GAME_SYS_REWARD,
        /**
         * 角色商城刷新
         */
        SHOP_REFASH,
        /**
         * 角色黑市刷新
         */
        MYSTERY_SHOP_REFRESH,
        /**
         * 角色商城金币购买
         */
        SHOP_BUY_COIN,
        /**
         * 角色商城钻石购买
         */
        SHOP_BUY_GOLD,
        /**
         * 角色商城购买道具
         */
        SHOP_BUY_TOOLS,
        /**
         * 角色黑市购买道具
         */
        MYSTERY_SHOP_BUY_TOOLS,
        /**
         * 竞拍
         */
        MYSTERY_SHOP_AUCTION,
        /**
         * 竞拍结束发放奖励
         */
        MYSTERY_SHOP_AUCTION_AWARD,
        /**
         * 竞拍结束回收竞拍池钻石
         */
        MYSTERY_SHOP_AUCTION_RECOVERY,
        /**
         * 邮件奖励道具
         */
        MAIL_REWARD_TOOLS,
        /**
         * 创建邮件
         */
        EMAIL_CREATE,
        /**
         * 删除邮件
         */
        EMAIL_REMOVE,
        /**
         * 用钻石购买金币
         */
        SHOP_COIN_BUY,
        /**
         * 公会签到得金币
         */
        ALLIANCE_REPORT_COIN,
        /**
         * 公会签到得贡献
         */
        ALLIANCE_REPORT_CONTRIBUTION,
        /**
         * 公会签到得钻石
         */
        ALLIANCE_REPORT_GOLD,
        /**
         * 公会操作
         */
        ALLIANCE_REPORT_OPER,
        /**
         * 公会开启BOSS
         */
        ALLIANCE_OPEN_BOSS,
        /**
         * 公会BOSS被击败
         */
        ALLIANCE_BOSS_VICTORY,
        /**
         * 鼓舞扣除钻石
         */
        ALLIANCE_CONSUME_ADD_PROP,
        /**
         * 公会商店消耗贡献
         */
        ALLIANCE_CONSUME_CONTRIBUTION,
        /**
         * 公会商店刷新消耗贡献
         */
        ALLIANCE_REFRESH_SHOP_CONSUME_CONTRIBUTION,
        /**
         * 公会创建消耗
         */
        ALLIANCE_CREATE_CONSUME,
        /**
         * 加入公会
         */
        ALLIANCE_JOIN_ALLIANCE,
        /**
         * 自动参加公会boss战扣钻
         */
        ALLIANCE_BOSS_AUTO_JOIN,
        /**
         * 加入公会boss战
         */
        JOIN_ALLIANCE_BOSS,
        /**
         * 装备神器吞噬
         */
        EQUIP_SWALLOW,
        /**
         * 装备打造刷新
         */
        EQUIP_SMELT_REFRESH,
        /**
         * 装备熔炼
         */
        EQUIP_SMELT,
        /**
         * 装备打孔
         */
        EQUIP_PUNCH,
        /**
         * 购买竞技场挑战次数
         */
        BUY_ARENA_CHALLENGE_TIMES,
        /**
         * 刷新竞技场对手列表
         */
        ARENA_REFRESH_OPPONENT_LIST,
        /**
         * 竞技场挑战
         */
        ARENA_CHALLENGE,
        /**
         * 装备打造
         */
        EQUIP_CREATE,
        /**
         * 神器传承
         */
        EQUIP_EXTEND,
        /**
         * 任务奖励
         */
        MISSION_BONUS,
        /**
         * 新手任务奖励;
         */
        QUEST_REWARD,
        /**
         * 新手任务阶段性奖励;
         */
        QUEST_STEP_REWARD,
        /**
         * 角色洗炼
         */
        ROLE_BAPTIZE,
        /**
         * 佣兵洗练属性
         */
        ROLE_BAPTIZE_ATTR,
        /**
         * 装备背包扩充
         */
        EQUIP_BAG_EXTEND,
        /**
         * 宝石镶嵌
         */
        EQUIP_STONE_DRESS,
        /**
         * 购买快速战斗
         */
        BUY_FAST_FIGHT_TIMES,
        /**
         * 购买boss挑战次数
         */
        BUY_BOSS_FIGHT_TIMES,
        /**
         * 购买精英副本挑战次数
         */
        BUY_ELITE_MAP_TIMES,
        /**
         * 离线结算
         */
        OFFLINE_ACCOUNT,
        /**
         * 快速战斗
         */
        FAST_FIGHTING,
        /**
         * 地图战斗
         */
        MONSTER_FIGHTING,
        /**
         * 地图Boss战斗
         */
        BOSS_FIGHTING,
        /**
         * 经验副本战斗
         */
        ELITE_MAP_FIGHTING,
        /**
         * 竞技场战斗
         */
        ARENA_FIGHTING,
        /**
         * 团战创建队伍
         */
        TEAM_BATTLE_CREATE_TEAM,
        /**
         * 团战T人
         */
        TEAM_BATTLE_KICK_UP_MEMBER,
        /**
         * 参加团战(报名＋创建队伍)
         */
        TAKE_PART_IN_TEAM_BATTLE,
        /**
         * 取消团战报名
         */
        CANCEL_TEAM_BATTLE,
        /**
         * 参加阵营战斗
         */
        TAKE_PART_IN_CAMPWAR,
        /**
         * 领取礼物
         */
        FETCH_GIFT,
        /**
         * 读取邮件
         */
        EMAIL_READ,
        /**
         * 购买月卡
         */
        MONTH_CARD_BUY,
        /**
         * 月卡领取奖励
         */
        MONTH_CARD_REWARD,
        /**
         * 累计充值奖励
         */
        ACC_RECHARGE_AWARDS,
        /**
         * 累计登录活动奖励
         */
        ACC_LOGIN_AWARDS,
        /**
         * 连续充值奖励
         */
        CONTINUE_RECHARGE_AWARDS,
        /**
         * 连续累积充值131（非新手）
         */
        CONTINUE_RECHARGE131_AWARDS,
        /**
         * 累计消费奖励
         */
        ACC_CONSUME_AWARDS,
        /**
         * 换装
         */
        EQUIP_DRESS,
        /**
         * 卸下
         */
        EQUIP_UNDRESS,
        /**
         * 装备技能
         */
        SKILL_CARRAY,
        /**
         * 升级技能
         */
        SKILL_LEVELUP,
        /**
         * 开启技能专精
         */
        SKILL_OPEN_ENHANCE,
        /**
         * 道具出售
         */
        ITEM_SELL,
        /**
         * 中秋换字兑换
         */
        WORDS_EXCHANGE,
        /**
         * 公测字兑换
         */
        WORDS_EXCHANGE_SPECIAL,
        /**
         * 公测字回收
         */
        WORDS_EXCHANGE_SPECIAL_CYCLE,
        /**
         * boss扫荡
         */
        BOSS_WIPE,
        /**
         * 屬性迴廊,新地城
         */
        ELITE_MAP_WIPE,
      /**
       * 地下城
      */
      DUNGEON_WIPE,
      /**
       * 循環地下城
      */
      CYCLESTAGE_WIPE,
        /**
         * 战斗奖励
         */
      BATTLE_REWARD,
        /**
         *新手首次領取 战斗奖励
         */
      FIRST_BATTLE_REWARD,
        /**
         * 荣誉商店刷新
         */
        HONOR_SHOP_REFRESH,
        /**
         * 荣誉商店购买
         */
        HONOR_SHOP_BUY,
        /**
         * 特殊神器打造
         */
        EQUIP_SPECIAL_CREATE,
        /**
         * 神器合成
         */
        EQUIP_COMPOUND,
        /**
         * 阵营战鼓舞
         */
        CAMPWAR_INSPIRE,
        /**
         * 开宝箱
         */
        OPEN_TREASURE,
        /**
         * 加入阵营战
         */
        JOIN_CAMPWAR,
        /**
         * 自动投资阵营战
         */
        AUTO_CAMPWAR,
        /**
         * 熔炼打造装备刷新
         */
        SMELT_EQUIP_REFRESH,
        /**
         * 勾选自动加入boss战
         */
        AUTO_BOSS_FIGHTING,
        /**
         * 公会经验增加, 公会Boss获胜
         */
        ALLIANCE_EXP_ADD,
        /**
         * 每日单笔充值返利
         */
        SINGLE_RECHARGE,
        /**
         * 每日单笔充值奖励
         */
        SINGLE_RECHARGE_AWARDS,
        /**
         * 充值返利活动每日返利
         */
        RECHARGE_REBATE_EVERYDAY_AWARDS,
        /**
         * 称号改变
         */
        TITLE_CHANGE,
        /**
         * 周卡奖励
         */
        WEEK_CARD_REWARD,
        /**
         * 周卡每日奖励
         */
        WEEK_CARD_DAILY_REWARD,
        /**
         * 领取vip福利
         */
        VIP_WELFARE_REWARD,
        /**
         * 提升等級
         */
        HERO_UP_LEVEL,
        /**
         * 提升等級一鍵
         */
        HERO_UP_LEVEL_BYONE,
        /**
         * 突破星星
         */
        HERO_UP_STAR,
    /**
     *	 英雄覺醒
     */
        HERO_AWAKE,
        /**
         * 升阶皮膚解鎖相簿
         */
        ROLE_UP_STAGE2,
        /**
         * 光环激活
         */
        ROLE_RING_ACTIVE,
        /**
         * 远征物资道具使用
         */
        EXPEDITION_ITEM_USE,
        /**
         * 经验副本地图
         */
        ELITE_MAP,
        /**
         * 远征物资经验增加
         */
        EXPEDITION_EXP_ADD,
        /**
         * 宝石合成
         */
        GEM_COMPOUND,
        /**
         * 疯狂转盘
         */
        CRAZY_ROULETTE,
        /**
         * 疯狂转盘积分兑换
         */
        CRAZY_ROULETTE_EXCHANGE,
        /**
         * 首充礼包
         */
        FIRST_GIFTPACK_REWARD,
        /**
         * 钻石增加
         */
        GOLD_ADD,
        /**
         * 限时限购购买
         */
        TIME_LIMIT_BUY,
        /**
         * 评价奖励
         */
        EVALUATE_REWARDS,
        /**
         * 宝石卸下
         */
        EQUIP_STONE_DOWN,
        /**
         * 帮会战投资
         */
        ALLIANCE_BATTLE_INVEST,
        /**
         * 幸运宝箱奖励
         */
        LUCKY_TREASURE_AWARDS,
        /**
         * 好友邀请奖励
         */
        INVITE_FRIEND_AWARDS,
        /**
         * 好友邀请兑换奖励
         */
        INVITE_FRIEND_EXCHANGE_AWARDS,
        /**
         * 五星奖励
         */
        STAR_EVALUATION,
        /**
         * 雪地探宝购买体力
         */
        SNOWFIELD_BUY_PHYC,
        /**
         * 雪地探宝
         */
        SNOWFIELD_DRAW,
        /**
         * 雪地探宝兑换
         */
        SNOWFIELD_EXCHANGE,
        /**
         * 工会战鼓舞
         */
        ALLIANCE_BATTLE_INSPIRE,
        /**
         * 夺宝奇兵开宝箱
         */
        TREASURE_RAIDER_SEARCH,
        /**
         * 多人副本购买商品
         */
        MULTI_ELITE_SHOP_BUY,
        /**
         * 购买多人副本次数
         */
        BUY_MULTI_ELITE_TIMES,
        /**
         * 多人副本战斗胜利
         */
        MULTI_ELITE_BATTLE_WIN,
        /**
         * 世界boss复活
         */
        WORLD_BOSS_REBIRTH,
        /**
         * 设置世界boss状态
         */
        WORLD_BOSS_AUTO_SETTING,
        /**
         * 世界boss自动战斗
         */
        WORLD_AUTO_FIGHT,
        /**
         * 世界boss攻击出手
         */
        WORLD_BOSS_ACTION,
        /**
         * 世界BossBuff随机
         */
        WORLD_BOSS_BUFF_RANDOM,
        /**
         * 世界bossBuff购买升级
         */
        WORLD_BOSS_BUFF_UPGRADE,
        /**
         * 部族的奖励发奖
         */
        COMMENDATION_RWARD,
        /**
         * 财神献礼
         */
        FORTUNE,
        /**
         * 装备分解
         */
        EQUIP_DECOMPOSE,
        /**
         * 装备进化
         */
        EQUIP_EVOLUTION,
        /**
         * Facebook好友索取(tzy)
         */
        FRIEND_ASKTICK,
        /**
         * 膜拜
         */
        WORSHIP,
        /**
         * 跨服押注
         */
        CS_BET,
        /**
         * 跨服押注失败
         */
        CS_BET_FAIL,
        /**
         * 跨服战领取奖励
         */
        CROSS_SERVER_REWARD,
        /**
         * 跨服押注奖励
         */
        CS_BET_REWARD,
        /**
         * 财富俱乐部返利
         */
        GOLD_CLUB_REBATE,
        /**
         * 抢红包得钻石
         */
        GRAB_RED_ENVELOPE,
        /**
         * 发红包固定奖励
         */
        GIVE_RED_ENVELOPE,
        /**
         * 系统红包奖励
         */
        SYS_RED_ENVELOPE,
        /**
         * 万家灯火掉落
         */
        FIND_TREASURE_LIGHT,
        /**
         * 聊天彩蛋
         */
        CHAT_LUCK,
        /**
         * 光环升级
         */
        ROLE_RING_INC_EXP,
        /**
         * 改名字
         */
        CHANGE_NAME,
        /**
         * 刷新多人副本雇员列表
         */
        REFRESH_MULTIELITE_HIRE_LIST,
        /**
         * 激活终身卡
         */
        ACTIVATE_FOREVER_CARD,
        /**
         * 领取终身卡每日钻石
         */
        DAILY_GOLD_AWARD,
        /**
         * 元素背包扩展
         */
        ELEMENT_BAG_EXTEND,
        /**
         * 元素分解
         */
        ELEMENT_DECOMPOSE,
        /**
         * 元素升级
         */
        ELEMENT_LVL_UP,
        /**
         * 元素进阶
         */
        ELEMENT_ADVANCED,
        /**
         * 元素重铸
         */
        ELEMENT_RECAST,
        /**
         * 帐号绑定
         */
        ACCOUNT_BOUND,
        /**
         * 升级星魂
         */
        UPDATE_TALENT,
        /**
         * 清空真气属性
         */
        CLEAR_TALENT,
        /**
         * 角色转生
         */
        REBIRTH_TALENT,
        /**
         * 兑换金豆
         */
        EXCHANGE_GOLD_BEAN,
        /**
         * 金豆消耗
         */
        GOLD_BEAN_COST,
        /**
         * 购买呀呀商场商品
         */
        BUY_YAYA_GOODS,
        /**
         * 回滚出错神器经验;
         */
        ROLLBACK_EQUIP_EXP,
        /**
         * 英雄令任务完成;
         */
        HERO_TASK_COMPLETE,
        /**
         * 英雄令任务更新;
         */
        HERO_TASK_UPDATE,
        /**
         * 翅膀升级;
         */
        WINGS_LEVEL_UP,
        /**
         * 翅膀引导
         */
        WINGS_LEAD_GET,
        /**
         * 修改新手任务
         */
        MODIFY_GUIDE,
        /**
         * 未知行为
         */
        UNKONWN_ACTION,
        /**
         * 升级增加星魂
         */
        ADD_TALENT,
        /**
         * 英雄令商店购买
         */
        TOKEN_SHOP_BUY_TOOLS,
        /**
         * 领取打折礼包
         */
        SALE_SALEPACKED_REWARD,
        /**
         * vip礼包
         */
        VIP_PACKAGE_REWARD,
        /**
         * 月卡充值
         */
        MONTH_RECHARGE,
        /**
         * 礼包充值
         */
        SALEGIFT_RECHARGE,
        /**
         * 限时限购
         */
        LIMIT_RECHARGE,
        /**
         * 兑换礼包
         */
        EXCHANGE_SEVEN,
        /**
         * 自动领取佣兵任务完成
         */
        COMPLETE_EXPEDITION_TASK,
        /**
         * 快速完成远征任务
         */
        FAST_COMPLETE_EXPEDITION_TASK,
        /**
         * 刷新任务
         */
        REFRESH_EXPEDITION_TASK,
        /**
         * 领取日常任务奖励
         */
        TAKE_DAILY_QUEST_AWARD,
        /**
         * 领取壁尻日常任务奖励
         */
        GLORYHOLE_DAILY_QUEST_AWARD,
        /**
         * 	领取週任务奖励
         */
        TAKE_WEEKLY_QUEST_AWARD,
       /*
        * 	领取一般任务奖励
        */
       TAKE_MISSION_AWARD,
       /**
        /**
         * 捞金鱼
         */
        FISHING_GOLDFISH,
        /**
         * 捞鱼加积分
         */
        GOLDFISH_SCORE,
        /**
         * 單人強敵加积分
         */
        SINGLE_BOSS_SCORE,
        /**
         * 季爬塔更新樓層
         */
        SEASON_TOWER_UPDATE_FLOOR,

        /**
         * 套装升级
         */
        EQUIP_UPGRADE,
        /**
         * 水晶商店刷新
         */
        CRYSTAL_SHOP_REFRESH,
        /**
         * 水晶商店购买
         */
        CRYSTAL_SHOP_BUY,

        /**
         * 旧宝石兑换
         */
        EQUIP_STONE_EXCHANGE,
        /**
         * 新宝石商店购买
         */
        EQUIP_STONE_SHOP_BUY,

        /**
         * 新宝石商店钻石购买
         */
        EQUIP_STONE_SHOP_BUY_GOLD,
        /**
         * 新宝石商店宝石卷购买
         */
        EQUIP_STONE_SHOP_BUY_VOLUME,
        /**
         * 神器锻造
         */
        FORGING_EQUIP,

        /**
         * 气枪打靶
         */
        SHOOT_ACTIVITY,

        /**
         * 魔王宝藏
         */
        PRINCE_DEVILS,
        /**
         * 魔王宝藏积分兑换
         */
        PRINCE_DEVILS_EXCHANGE,
        /**
         * ios专属礼包
         */
        IOS_GIT,
        /**
         * 佣兵激活
         */
        ROLE_EMPOLY,
        /**
         * 魂魄超出自动转换材料
         */
        ROLE_SOUL,
        /**
         * 领取新周卡
         */
        NEW_WEEK_CARD_AWARD,
        /**
         * 公会BOSS战失败（时间到了Boss没死）
         */
        ALLIANCE_BOSS_FAIL,
        /**
         * 购买成长基金
         */
        BUY_GROWTH_FUND,
        /**
         * 成长基金江立
         */
        GROWTH_FUND_REWARD,

        /**
         * GVG城池奖励
         */
        GVG_CITY_REWAED,
        /**
         * GVG宝箱奖励
         */
        GVG_CITY_BOX,
        /**
         * 派遣攻击佣兵
         */
        GVG_ATTACKER,
        /**
         * 派遣防御佣兵
         */
        GVG_DEFENDER,
        /**
         * GVG公会宣战
         */
        GVG_DECLARE_BATTLE,
        /**
         * 购买复活权
         */
        GVG_BUY_REVIVE,
        /**
         * GVG反攻
         */
        GVG_DECLARE_FIGHTBACK,

        /***
         * 玩家点击活动入口记录
         */
        ACTIVITY_ENTRY_RECORD,
        /***
         * 新夺宝奇兵（神将投放（用于抽各种神将的活动，比如一身是胆、卧龙初登场））
         */
        NEW_TREASURE_RAIDER,
        /***
         * 星魂
         */
        STAR_SOUL,
        /**
         * 	精靈星魂
         */
        SPRITE_SOUL,
        /**
         * 	主星魂
         */
        LEADER_SOUL,
        /**
         *	職業星魂
         */
        CLASS_SOUL,
        /**
         * 	屬性星魂
         */
        ELEMENT_SOUL,
        /**
         * 
         * 购买聊天皮肤
         */
        BUY_CHAT_SKIN,
        /**
         * 王的后宫兑换
         */
        HAREM_EXCHANGE,
        /**
         * 王的后宫抽卡活动
         */
        HAREM_ACTIVITY,
        /**
         * 购买打折礼包
         */
        DISCOUNT_GIFT_BUY,
        /**
         * 購買成長通行證
         */
        GROWTH_PASS_BUY,
        /**
         * 领取打折礼包
         */
        DISCOUNT_GIFT_REWARD,
        /**
         * 万能碎片兑换
         */
        FRAGMENT_EXCHANGE,
        /**
         * 仙女的保佑活动
         */
        FAIRY_BLESS,
        /**
         * 少女的邂逅互动
         */
        MAIDEN_ENCOUNTER,
        /**
         * 开启羁绊
         */
        OPEN_FETTER,
        /**
         * 開啟相生
         */
        OPEN_MUTUAL,
        /**
         * 增加公会元气
         */
        ADD_VITALITY,
        /**
         * 修改公会会长
         */
        CHANGE_ALLIANCE_MAIN,
        /**
         * 补丁：转移佣兵育成属性
         */
        TRANSFER_BAPTIZE_ATTR,
        /**
         * 鬼节活动
         */
        OBON,
        /**
         * 购买跨服挑战次数
         */
        BUY_CROSS_BATTLE_TIMES,
        /**
         * 跨服商店购买
         */
        CROSS_SHOP_BUY,
        /**
         * 天降元宝活动
         */
        WELFARE_REWARD,
        /**
         * 新手UR活动
         */
        NEW_UR,
        /**
         * 大转盘活动抽奖
         */
        TURNTABLE_DRAW,
        /**
         * 大转盘活动兑换
         */
        TURNTABLE_EXCHANGE,
        /**
         * 万圣节活动抽奖
         */
        HALLOWEEN_DRAW,
        /**
         * 万圣节活动兑换
         */
        HALLOWEEN_EXCHANGE,
        /**
         * 新神将投放活动
         */
        RELEASE_UR,
        /**
         * 时装投放活动
         */
        DRAW_FASHION,

        /***
         * 联盟捐献
         */
        ALLIANCE_DONATE,

        /***
         * 联盟捐献奖励
         */
        ALLIANCE_DONATE_AWARD,

        /**
         * 联盟捐献个人奖励
         */
        ALLIANCE_DONATE_PERSON_AWARD,

        /**
         * 登陆签到奖励
         */
        ACC_LOGIN_SIGNED_AWARDS,
        /**
         * 登陆签到箱子奖励
         */
        ACC_LOGIN_SIGNEDCHEST_AWARDS,
        /**
         * 7日之诗任务领奖
         */
        ACC_LOGIN_SEVENDAY_TASK_AWARDS,
        /**
         * 7日之诗成就领奖
         */
        ACC_LOGIN_SEVENDAY_POING_AWARDS,
        /**
         * 7日之诗登陆领奖
         */
        ACC_LOGIN_SEVENDAY_AWARDS,
        /**
         * UR抽奖
         */
        RELEASELOTTERY_AWARDS,

        /**
         * UR 抽奖重置
         */
        RELEASE_UR_RESET,
        /**
         * UR 抽奖重置2
         */
        RELEASE_UR_RESET2,
        /**
         * 新神将投放活动
         */
        RELEASE_UR2,

        /**
         * 新神将投放活动
         */
        RELEASELOTTERY_AWARD2,

        /***
         * 新夺宝奇兵（神将投放（用于抽各种神将的活动，比如一身是胆、卧龙初登场））
         */
        NEW_TREASURE_RAIDER2,

        /**
         * 连续充值奖励
         */
        CONTINUE_RECHARGEMONEY_AWARDS,
        


        /***
         * 新夺宝奇兵（神将投放（用于抽各种神将的活动，比如一身是胆、卧龙初登场））
         */
        NEW_TREASURE_RAIDER3,

        /**
         * 新神将投放复刻版
         */
        RELEASE_UR3,

        /**
         * 活动 121 领奖
         */
        RELEASELOTTERY3_AWARDS,

        /**
         * UR 活动121
         */
        RELEASE_UR_RESET3,

        /**
         * 活跃度达标活动
         */
        ACTIVECOMPLIANCE_AWARDS,

        /**
         * 束缚彼女
         */
        Activity123,
        /**
         * 束缚彼女积分抽奖
         */
        Activity123LOTTERY_AWARD,

        /**
         * 购买头像
         */
        BUYHEADICON,


        /**
         * 高级角色洗炼
         */
        ROLE_SENIORBAPTIZE,
		/**
		 * 武器屋抽取消耗
		 */
		WEAPON_DRAW,
		/**
		 * 天降元宝-按注册时间走
		 */
		WELFAREBYREGDATE_REWARD,
		
		/**
		 * 武器召唤师
		 */
		Activity127_UR,
		/**
		 * 消耗型周卡领奖
		 */
		CONSUME_WEEK_CARD_DAILY_REWARD,
		/**
		 * 消耗型月卡领奖
		 */
		CONSUME_MONTH_CARD_REWARD,

        /**
         * UR 抽卡 积分排行
         */
        Activity128_UR,
    	 /**
         * 消耗型月卡充值
         */
        CONSUME_MONTH_RECHARGE,

        ACTIVITY132_LEVEL_GIFT,

        ACTIVITY134_WEEKEND_GIFT,

        ACTIVITY124_RECHARGE_RETURN,
        NEW_TREASURE_RAIDER4,
        EIGHTEEN_PRINCES_HELP_REWARD,
        EIGHTEEN_PRINCE_CHALLENGE,
        ACTIVITY137_RECHARGE_RETURN,
        /**
         *	 徽章背包扩充
         */
        BADGE_BAG_EXTEND,

        /**
         * 	徽章升级
         */
        BADGE_FUSION,
        /**
         * 	徽章精煉
         */
        BADGE_REFINE,
        /**
         * 	徽章合成上鎖
         */
        BADGE_LOCK,
        /**
         * 	徽章升级
         */
        NEW_TREASURE_RAIDER139,
        /**
         * 18路诸侯使用副将
         */
    	EIGHTEENPRINCESD_USE_MERCENARY,
    	  /**
         * 18路诸侯 双倍购买
         */
    	EIGHTEEN_PRINCE_DOUBLEBUY,
    	  /**
         * 18路诸侯 双倍购买
         */
    	ACTIVITE_MERCENARY_CAN_ACTIVITE,
    	
    	 /**
         * 140Draw
         */
    	ACTIVITY140_LOTTERY,

		/* 大富翁 */
    	ACTIVITY141_RICHMAN,

        /**
		/* 海盜寶箱 */
    	ACTIVITY143_PIRATE,
    	
        /**
		/* 性奴小學堂 */
    	ACTIVITY144_LITTLE_TEST,
    	
        /**
		/* 精選召喚 */
    	ACTIVITY146_CHOSEN_ONE,
    	
        /**
		/* PICK UP召喚 */
    	ACTIVITY172_PICK_UP,
    	
        /**
		/* 新角召喚 */
    	ACTIVITY173_NEW_ROLE,
    	
        /**
		/* 專武召喚 */
    	ACTIVITY178_CALL_OF_EQUIP,
    	    	
        /**
		/* 許願輪盤 */
    	ACTIVITY147_WISHING_WELLS,
        /**
		/* 小瑪莉 */
    	ACTIVITY148_MARRY_GAME,
        /**
		/* 關卡禮包*/
    	ACTIVITY151_STAGE_GIFT,
    	
		/*領取英雄劇情禮物*/
    	ACTIVITY152_GOT_HERO_DRAMA_GIFT,
    	
		/*領取排行榜禮物*/
    	ACTIVITY153_GOT_RANK_GIFT,
    	
		/*精靈召喚*/
    	ACTIVITY154_CALL_OF_SPRITE,
    	
		/*每日登入領十抽*/
    	ACTIVITY157_LOGIN_TEN_DRAW,
    	
		/*種族召喚*/
    	ACTIVITY158_CALL_OF_RACE,
    	
		/*領取友情值*/
    	GOT_FRIENDSHIP,
    	
    	/*重置等級*/
    	Role_ReSet_Level,
    	
    	/*重置裝備強化*/
    	EQUIP_Enhance_ReSet,
    	
    	/*激活箴言*/
    	ACTIVATION_MOTTO,
    	
    	/*升級箴言*/
    	STAR_UP_MOTTO,
    	/**
    	 * 修正遠征狀態
    	 */
    	FIX_STATUS_EXPEDITION,
    	
        /**
         * 159累積VIP點數奖励
         */
    	ACTIVITY159_VIP_POINT_COLLECT_AWARDS,
    	
        /**
         * 160连续充值奖励
         */
    	ACTIVITY160_NP_RECHARGEMONEY_AWARDS,
        /**
		/* 收費打卡 */
    	ACTIVITY161_SUPPORT_CALENDAR,
    	/**
    	 * 公會魔典升級小天賦
    	 */
    	GUILD_SOUL_LEVELUP,
    	/**
    	 * 公會魔典重置小天賦
    	 */
    	GUILD_SOUL_RESET,

    	
		/* 成長禮物 */
    	ACTIVITY162_GROWTH_LV,
		/* 成長禮物 */
    	ACTIVITY163_GROWTH_CH,
		/* 成長禮物 */
    	ACTIVITY164_GROWTH_TW,
        /**
		/* 挖礦活動 */
    	ACTIVITY165_MINING,
        /**
		/* 秘密信條 */
    	SECRET_MESSAGE,
        /**
		/* 友情召喚 */
    	ACTIVITY166_CALL_OF_FRIENDSHIP,
        /**
		/* 首儲1500抽*/
    	ACTIVITY167_COMSUME_SUMMON,
        /**
		/* 特權證*/
    	ACTIVITY168_SUBSCRIPTION,
        /**
        /* 彈跳活動禮包*/
    	ACTIVITY169_JUMP_GIFT,
        /**
        /* 彈跳活動禮包*/
    	ACTIVITY170_JUMP_GIFT,
        /**
		/* 壁尻活動 */
    	ACTIVITY175_GLORY_HOLE,
    	/**
    	 * 活動兌換
    	 */
    	ACTIVITY176_ACT_EXCHANGE,
    	/**
    	 * 闖關失敗禮包
    	 */
    	ACTIVITY177_STAGE_FAILED,
    	/**
    	 * 階段禮包
    	 */
    	ACTIVITY179_Step_GIFT,
		/**
		 * 免費1500抽
		 */
    	ACTIVITY180_FREE_SUMMON,
        /**
        /* 彈跳活動禮包*/
    	ACTIVITY181_JUMP_GIFT,
        /**
        /* 彈跳活動禮包*/
    	ACTIVITY182_JUMP_GIFT,
        /**
        /* 彈跳活動禮包*/
    	ACTIVITY183_JUMP_GIFT,
        /**
        /* 彈跳活動禮包*/
    	ACTIVITY184_JUMP_GIFT,
        /**
        /* 彈跳活動禮包*/
    	ACTIVITY185_JUMP_GIFT,
        /**
        /* 彈跳活動禮包*/
    	ACTIVITY186_JUMP_GIFT,
        /**
        /* 加強彈跳活動禮包*/
    	ACTIVITY187_MAX_JUMP,
        /**
        /* 加強彈跳活動禮包*/
    	ACTIVITY190_STEP_SUMMON,
        /**
		/* 循環關卡活動 */
    	ACTIVITY191_CycleStage,
        /**
		/* 循環關卡活動 */
    	ACTIVITY191_CycleStage_WIPE,
        /**
		/* 循環關卡活動物品初始*/
    	ACTIVITY191_CycleStage_ItemInit,
        /**
		/* 循環關卡活動物品初始*/
    	ACTIVITY191_CycleStage_ItemClear,
    	/**
    	 * 循環關卡活動物品重置
    	 */
    	ACTIVITY191_CycleStage_DailyRest,
    	/**
    	 * 累儲累消累充活動
    	 */
    	ACTIVITY192_Recharge_Bounce,
        /*
         * 	领取一般任务奖励
         */
    	/**
    	 * 單人強敵活動
    	 */
    	ACTIVITY193_Single_Boss,
    	/**
    	 * 單人強敵成就任務獎勵
    	 */
        Single_Boss_Quest_AWARD,
    	/**
    	 * 單人強敵活動,重置活動代幣
    	 */
    	ACTIVITY193_Single_Boss_InitItem,
    	/**
    	 * 賽季爬塔活動
    	 */
    	ACTIVITY194_Season_Tower,
    	/**
    	 * 九宮格活動
    	 */
    	ACTIVITY195_Puzzle_Battle,
    	/**
    	 * 九宮格活動使用鑰匙
    	 */
    	ACTIVITY195_Puzzle_UseKey,
        /**
		/* 循環關卡活動 */
    	ACTIVITY196_CycleStage,
        /**
		/* 循環關卡活動 */
    	ACTIVITY196_CycleStage_WIPE,
        /**
		/* 循環關卡活動物品初始*/
    	ACTIVITY196_CycleStage_ItemInit,
        /**
		/* 循環關卡活動物品初始*/
    	ACTIVITY196_CycleStage_ItemClear,
    	/**
    	 * 循環關卡活動物品重置
    	 */
    	ACTIVITY196_CycleStage_DailyRest,
    	/**
    	 * 拍賣直購
    	 */
    	Bidding_Direct_Buy,
    	/**
    	 * 拍賣競標物品
    	 */
    	Bidding_Bid_Item,
    	/**
    	 * 拍賣退款
    	 */
    	Bidding_Refund,
    	/**
    	 * 拍賣刷新競標最高金額
    	 */
    	Bidding_refresh,
    	/**
    	 * 超級Pick池
    	 */
    	ACTIVITY197_SUPER_PICKUP,
    	/**
    	 * 屬性限制爬塔活動
    	 */
    	ACTIVITY198_LIMIT_TOWER;
        /**
         * 行为名
         */
        private String actionName;

        /**
         * 获取行为名
         *
         * @return
         */
        public String getActionName() {
            return actionName;
        }

        /**
         * 构造函数
         */
        private Action() {
            this("");
        }

        /**
         * 构造函数
         */
        private Action(String value) {
            this.actionName = value;
        }
    }

    /**
     * 日志参数
     */
    public static class Params {
        private String name;
        private Object value;

        public static final String AFTER = "after";

        public static final String COST = "cost";

        public static Params valueOf(String name, Object value) {
            Params params = new Params();
            params.setName(name);
            params.setValue(value);
            return params;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
	/**
	 * 控制台打印
	 */
	static boolean consolePrint = false;
	
	/**
	 * LogDB紀錄
	 */
	static boolean logDbRecord = true;

    /**
     * GM日志记录器
     */
    private static final Logger GM_LOGGER = Logger.getLogger("GM");
    /**
     * 行为日志，数据流变化日志记录器
     */
    private static final Logger ACTION_LOGGER = Logger.getLogger("Action");
    /**
     * 统计平台日志记录器
     */
    private static final Logger PLATFORM_LOGGER = Logger.getLogger("PlatformDB");

    /**
     * 神器经验回滚记录器
     */
    private final static Logger DEDUCT_LOGGER = Logger.getLogger("DeductPlayerEquip");
    
    // DB紀錄
    private static final Logger GM_DB_LOGGER = Logger.getLogger("GMDB");
    private static final Logger ACTION_DB_LOGGER = Logger.getLogger("ActionDB");
    private static final Logger PLATFORM_DB_LOGGER = Logger.getLogger("PlatformDB");
    
	/**
	 * 开启控制台打印
	 * 
	 * @param enable
	 */
	public static void enableConsole(boolean enable) {
		consolePrint = enable;
	}
	
	/**
	 * 開啟logDB紀錄
	 * 
	 * @param enable
	 */
	public static void enableLogDBRecord(boolean enable) {
		logDbRecord = enable;
	}
	
    /**
     * 用于数据平台统计的日志输出
     *
     * @param player
     * @param action
     * @param params
     */
    public synchronized static void log4Platform(Player player, Action action, Params... params) {
    	log4Platform(player.getEntity(),action,params);
    }

    /**
     * 用于数据平台统计的日志输出
     *
     * @param playerEntity
     * @param action
     * @param params
     */
    public synchronized static void log4Platform(PlayerEntity playerEntity, Action action, Params... params) {
	    try {
	    	if (logDbRecord) {
	    		log4PlatformDB(playerEntity,action,params);
	    	} else {
	            JSONObject jsonObject = new JSONObject();
	            // 行为时间
	            jsonObject.put("ts", GuaJiTime.getTimeString());
	            // 平台用户Id
	            jsonObject.put("puid", playerEntity.getPuid());
	            // 服务器id
	            jsonObject.put("serverId", playerEntity.getServerId());
	            // 设备Id
	            jsonObject.put("device_id", playerEntity.getDevice());
	            // 角色Id
	            jsonObject.put("player_id", playerEntity.getId());
	            // 场景，先留空
	            jsonObject.put("scene", "scene");
	            // 等级
	            jsonObject.put("level", playerEntity.getLevel());
	            // vip等级
	            jsonObject.put("vip_level", playerEntity.getVipLevel());
	            // 动作编号
	            jsonObject.put("action", action.name());
	            // 客户端语言
	            jsonObject.put("langArea", playerEntity.getLangArea());
	            // 填充参数V1
	            JSONObject paramsJsonObject = new JSONObject();
	            for (Params param : params) {
	                paramsJsonObject.put(param.getName(), param.getValue().toString());
	            }
	            jsonObject.put("v1", paramsJsonObject.toString());
	            jsonObject.put("v2", "");
	            jsonObject.put("v3", "");
	            jsonObject.put("v4", "");
	
	            // 渠道信息
	            jsonObject.put("ip", "");
	            jsonObject.put("gameid", GsConfig.getInstance().getGameId());
	            jsonObject.put("server", GsConfig.getInstance().getPlatform() + "-" + GsConfig.getInstance().getServerId());
	            jsonObject.put("store", playerEntity.getPlatform());
	            // 手机信息
	            jsonObject.put("phoneinfo", playerEntity.getPhoneInfo());
	
	            PLATFORM_LOGGER.info(jsonObject.toString());
	    	}
	    } catch (Exception e) {
	        MyException.catchException(e);
	    }
    }
    
    /**
     * 用于数据平台统计的日志输出 for DB
     *
     * @param playerEntity
     * @param action
     * @param params
     */
    public synchronized static void log4PlatformDB(PlayerEntity playerEntity, Action action, Params... params) {
        try {
            JSONObject jsonObject = new JSONObject();
            // 平台用户Id
            MDC.put("puid", playerEntity.getPuid());
            // 服务器id
            MDC.put("serverId",GsConfig.getInstance().getServerId());
            // 设备Id
            MDC.put("device_id", playerEntity.getDevice());
            // 角色Id
            MDC.put("playerId", playerEntity.getId());
            // 场景，先留空
            //MDC.put("scene", "scene");
            // 等级
            MDC.put("level", playerEntity.getLevel());
            // vip等级
            MDC.put("vip_level", playerEntity.getVipLevel());
            // 动作编号
            MDC.put("action", action.name());
            // 客户端语言
            MDC.put("langArea", playerEntity.getLangArea());
            
            // 行为时间
//            jsonObject.put("ts", GuaJiTime.getTimeString());
//            // 平台用户Id
//            jsonObject.put("puid", playerEntity.getPuid());
//            // 服务器id
//            jsonObject.put("serverId", playerEntity.getServerId());
//            // 设备Id
//            jsonObject.put("device_id", playerEntity.getDevice());
//            // 角色Id
//            jsonObject.put("player_id", playerEntity.getId());
//            // 场景，先留空
//            jsonObject.put("scene", "scene");
//            // 等级
//            jsonObject.put("level", playerEntity.getLevel());
//            // vip等级
//            jsonObject.put("vip_level", playerEntity.getVipLevel());
//            // 动作编号
//            jsonObject.put("action", action.name());
//            // 客户端语言
//            jsonObject.put("langArea", playerEntity.getLangArea());
            // 填充参数V1
            JSONObject paramsJsonObject = new JSONObject();
            for (Params param : params) {
                paramsJsonObject.put(param.getName(), param.getValue().toString());
            }
            jsonObject.put("v1", paramsJsonObject.toString());
            jsonObject.put("v2", "");
            jsonObject.put("v3", "");
            jsonObject.put("v4", "");

            // 渠道信息
            jsonObject.put("ip", "");
            jsonObject.put("gameid", GsConfig.getInstance().getGameId());
            jsonObject.put("server", GsConfig.getInstance().getPlatform() + "-" + GsConfig.getInstance().getServerId());
            jsonObject.put("store", playerEntity.getPlatform());
            // 手机信息
            jsonObject.put("phoneinfo", playerEntity.getPhoneInfo());

            PLATFORM_DB_LOGGER.info(jsonObject.toString());
            MDC.clear();
        } catch (Exception e) {
            MyException.catchException(e);
        }
    }

    /**
     * 数据流统计，主要用户客服问题查询
     *
     * @param player
     * @param source
     * @param action
     */
    public synchronized static void log4Service(Player player, Source source, Action action, Params... params) {
    	log4Service(player.getPlayerData().getPlayerEntity(),source,action,params);
    }
    
    /**
     * 数据流统计，主要用户客服问题查询
     *
     * @param player
     * @param source
     * @param action
     */
    public synchronized static void log4ActionDB(PlayerEntity playerEntity, Source source, Action action, Params... params) {
        try {
            JSONObject jsonObject = new JSONObject();
            MDC.put("puid", playerEntity.getPuid());
            MDC.put("serverId",GsConfig.getInstance().getServerId());
            MDC.put("playerId", playerEntity.getId());
            MDC.put("playerName", playerEntity.getName());
            MDC.put("source", source.name());
            MDC.put("action", action.name());
            MDC.put("langArea", playerEntity.getLangArea());// 客户端语言
            // 行为时间
//            jsonObject.put("time", GuaJiTime.getTimeString());
//            jsonObject.put("puid", playerEntity.getPuid());
//            jsonObject.put("serverId", playerEntity.getServerId());
//            jsonObject.put("playerId", playerEntity.getId());
//            jsonObject.put("playerName", playerEntity.getName());
//            jsonObject.put("source", source.name());
//            jsonObject.put("action", action.name());
//            jsonObject.put("langArea", playerEntity.getLangArea());// 客户端语言

            JSONObject paramsJsonObject = new JSONObject();
            for (Params param : params) {
                paramsJsonObject.put(param.getName(), param.getValue().toString());
            }
            jsonObject.put("data", paramsJsonObject);

            ACTION_DB_LOGGER.info(jsonObject.toString());
            MDC.clear();
        } catch (Exception e) {
            MyException.catchException(e);
        }
    }

    /**
     * 数据流统计，主要用户客服问题查询
     *
     * @param player
     * @param source
     * @param action
     */
    public synchronized static void log4Service(PlayerEntity playerEntity, Source source, Action action, Params... params) {
	      try {
	      	if (logDbRecord) {
	    		log4ActionDB(playerEntity,source,action,params);
	    	} else {
	          JSONObject jsonObject = new JSONObject();
	          // 行为时间
	          jsonObject.put("time", GuaJiTime.getTimeString());
	          jsonObject.put("puid", playerEntity.getPuid());
	          jsonObject.put("serverId", playerEntity.getServerId());
	          jsonObject.put("playerId", playerEntity.getId());
	          jsonObject.put("playerName", playerEntity.getName());
	          jsonObject.put("source", source.name());
	          jsonObject.put("action", action.name());
	
	          JSONObject paramsJsonObject = new JSONObject();
	          for (Params param : params) {
	              paramsJsonObject.put(param.getName(), param.getValue().toString());
	          }
	          jsonObject.put("data", paramsJsonObject);
	
	          ACTION_LOGGER.info(jsonObject.toString());
	    	}
	      } catch (Exception e) {
	          MyException.catchException(e);
	      }	
    }

    /**
     * 以玩家id为key统计
     *
     * @param playerId
     * @param source
     * @param action
     * @param params
     */
    public synchronized static void log4Service(int playerId, Source source, Action action, Params... params) {
		try {
	    	Player player = PlayerUtil.queryPlayer(playerId);
	    	if (player != null) {
	    		log4Service(player,source,action,params);
	    	} else {
	    		// 离线玩家
	    		List<PlayerEntity> playerEntities = DBManager.getInstance().query("from PlayerEntity where id = ?",
	    				playerId);
	    		if (playerEntities.size() > 0) {
	    			PlayerEntity playerEntity = (PlayerEntity) playerEntities.get(0);
	    			log4Service(playerEntity,source,action,params);
	    		} else {  // 沒這個玩家
					JSONObject jsonObject = new JSONObject();
					// 行为时间
					jsonObject.put("time", GuaJiTime.getTimeString());
					jsonObject.put("playerId", playerId);
					jsonObject.put("source", source.name());
					jsonObject.put("action", action.name());
  
					JSONObject paramsJsonObject = new JSONObject();
					for (Params param : params) {
						paramsJsonObject.put(param.getName(), param.getValue().toString());
					}
					jsonObject.put("data", paramsJsonObject);
					if (logDbRecord) {
						MDC.put("puid","");
						MDC.put("serverId",GsConfig.getInstance().getServerId());
						MDC.put("playerId",playerId);
						MDC.put("playerName", "");
						MDC.put("source", source.name());
						MDC.put("action", action.name());
						MDC.put("langArea", "");// 客户端语言
						ACTION_DB_LOGGER.info(jsonObject.toString());
						MDC.clear();
					} else {
						ACTION_LOGGER.info(jsonObject.toString());
					}
	    		}
	    	}
		} catch (Exception e) {
			MyException.catchException(e);
		}
    }
    
    /**
     * GM统计，主要记录GM操作
     *
     * @param user
     * @param source
     * @param action
     */
    public  synchronized static void log4GM(String user, Source source, Action action, Params... params) {
	        try {
	        	if (logDbRecord) {
	        		log4DB_GM(user,source,action,params);
	        	} else {
		            JSONObject jsonObject = new JSONObject();
		            // 行为时间
		            jsonObject.put("time", GuaJiTime.getTimeString());
		            jsonObject.put("user", user);
		            jsonObject.put("source", source.name());
		            jsonObject.put("action", action.name());
		
		            JSONObject paramsJsonObject = new JSONObject();
		            for (Params param : params) {
		                paramsJsonObject.put(param.getName(), param.getValue().toString());
		            }
		            jsonObject.put("data", paramsJsonObject);
		
		            GM_LOGGER.info(jsonObject.toString());
		        }    
	        } catch (Exception e) {
	            MyException.catchException(e);
	        }
    }

    /**
     * 以玩家id为key统计
     *
     * @param playerId
     * @param source
     * @param action
     * @param params
     */
    public synchronized static void log4RollBack(int playerId, Source source, Action action, Params... params) {
        try {
            JSONObject jsonObject = new JSONObject();
            // 行为时间
            jsonObject.put("time", GuaJiTime.getTimeString());
            jsonObject.put("playerId", playerId);
            jsonObject.put("source", source.name());
            jsonObject.put("action", action.name());

            JSONObject paramsJsonObject = new JSONObject();
            for (Params param : params) {
                paramsJsonObject.put(param.getName(), param.getValue().toString());
            }
            jsonObject.put("data", paramsJsonObject);

            DEDUCT_LOGGER.info(jsonObject.toString());
        } catch (Exception e) {
            MyException.catchException(e);
        }
    }
    
    /**
     * 	DB统计，主要记录GM操作
     *
     * @param user
     * @param source
     * @param action
     */
    public synchronized static void log4DB_GM(String user, Source source, Action action, Params... params) {
        try {
            JSONObject jsonObject = new JSONObject();
            // 行为时间
            // for db %X
            MDC.put("user",user);
            MDC.put("serverId",GsConfig.getInstance().getServerId());
            MDC.put("source",source.name());
            MDC.put("action",action.name());
            
//            jsonObject.put("time", GuaJiTime.getTimeString());
//            jsonObject.put("user", user);
//            jsonObject.put("source", source.name());
//            jsonObject.put("action", action.name());

            JSONObject paramsJsonObject = new JSONObject();
            for (Params param : params) {
                paramsJsonObject.put(param.getName(), param.getValue().toString());
            }
            jsonObject.put("data", paramsJsonObject);
            GM_DB_LOGGER.info(jsonObject.toString());
            MDC.clear();
        } catch (Exception e) {
            MyException.catchException(e);
        }
    }
}
