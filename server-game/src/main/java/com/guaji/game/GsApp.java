package com.guaji.game;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.PropertyConfigurator;
import org.guaji.app.App;
import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.config.ConfigStorage;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.net.GuaJiSession;
import org.guaji.net.protocol.Protocol;
import org.guaji.obj.ObjBase;
import org.guaji.obj.ObjManager;
import org.guaji.os.GuaJiShutdownHook;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;
import org.guaji.os.OSOperator;
import org.guaji.script.GuaJiScriptManager;
import org.guaji.util.services.CdkService;
import org.guaji.util.services.EmailService;
import org.guaji.util.services.PlatformService;
import org.guaji.util.services.ReportService;
import org.guaji.xid.GuaJiXID;
import org.guaji.zmq.GuaJiZmq;
import org.guaji.zmq.GuaJiZmqManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danga.MemCached.SockIOPool;
import com.google.protobuf.ByteString;
import com.guaji.game.callback.ShutdownCallback;
//import com.guaji.game.cmreport.CmReportManager;
import com.guaji.game.config.ActivityCfg;
import com.guaji.game.config.GrayPuidCfg;
import com.guaji.game.config.RechargeConfig;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.IpAddrEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.korean.BroadCastService;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.manager.ActivityManager;
import com.guaji.game.manager.AllianceBattleManager;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.ArenaManager;
import com.guaji.game.manager.BattleDebugManager;
import com.guaji.game.manager.BulletinManager;
import com.guaji.game.manager.CampWarManager;
import com.guaji.game.manager.ChatManager;
import com.guaji.game.manager.ChatMsg;
import com.guaji.game.manager.DailyStatisticsManager;
import com.guaji.game.manager.ExpeditionArmoryManager;
import com.guaji.game.manager.FightValueRankManager;
import com.guaji.game.manager.ForgingManager;
import com.guaji.game.manager.FriendInviteManager;
import com.guaji.game.manager.GloryHoleActivityManager;
import com.guaji.game.manager.GoldfishRankManager;
import com.guaji.game.manager.MiningActivityManager;
import com.guaji.game.manager.MultiEliteManager;
import com.guaji.game.manager.ProfRankManager;
import com.guaji.game.manager.RankGiftManager;
import com.guaji.game.manager.RankManager;
import com.guaji.game.manager.RecordFirstManager;
import com.guaji.game.manager.SeasonTowerRankManager;
import com.guaji.game.manager.SingleBossRankManager;
import com.guaji.game.manager.SkillHandlerManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.manager.TapDBManager;
import com.guaji.game.manager.TeamBattleManager;
import com.guaji.game.manager.UrRankActivityManager;
import com.guaji.game.manager.WealthClubManager;
import com.guaji.game.manager.WorldBossManager;
import com.guaji.game.manager.crossbattle.CrossBattleManager;
import com.guaji.game.manager.crossbattle.CrossBattleService;
import com.guaji.game.manager.crossserver.CrossServerManager;
import com.guaji.game.manager.gvg.GvgManager;
import com.guaji.game.module.activity.shoot.ShootActivityManager;
import com.guaji.game.module.activity.timeLimit.TimeLimitManager;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Login.HPLogin;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.SysProtocol.HPDataWarpper;
import com.guaji.game.protocol.SysProtocol.HPHeartBeat;
import com.guaji.game.protocol.SysProtocol.HPTimeZoneRet;
import com.guaji.game.recharge.RechargeManager;
import com.guaji.game.recharge.SyncSubscriptionService;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GJLocal;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.ProtoUtil;
import com.guaji.game.verify.ILoginVerify;
import com.guaji.game.verify.LoginVerifyManager;

import net.sf.json.JSONObject;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 游戏应用布局管理（相当于gameMenager）
 */
public class GsApp extends App {
    /**
     * 协议日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger("Server");

    /**
     * ip服务对象
     */
    private GuaJiZmq ipZmq;

    /**
     * puid登陆时间
     */
    private Map<String, Long> puidLoginTime;

    /**
     * redis 客户端连接池
     */
    private JedisPool jedisPool;

    /**
     * 服务器标示
     */
    private String serverIdentify;
    
    /**
     * 消息处理的状态信息
     */
    private int lastGameShowTime;

    /**
     * 全局静态对象
     */
    private static GsApp instance = null;

    /**
     * 获取全局静态对象
     *
     * @return
     */
    public static GsApp getInstance() {
        return instance;
    }

    public int getThreadNum() {
        return appCfg.getTaskThreads();
    }

    /**
     * 构造函数
     */
    public GsApp() {
        super(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.APP));

        if (instance == null) {
            instance = this;
        }

        puidLoginTime = new ConcurrentHashMap<String, Long>();
    }

    /**
     * 从配置文件初始化
     *
     * @param cfg
     * @return
     */
    public boolean init(String cfg) {

        GsConfig appCfg = null;
        try {
            ConfigStorage cfgStorgae = new ConfigStorage(GsConfig.class);
            appCfg = (GsConfig) cfgStorgae.getConfigList().get(0);
            appCfg.initServerOpenDate();
        } catch (Exception e) {
            MyException.catchException(e);
            return false;
        }
        // 加载log4j 的配置
        PropertyConfigurator.configure("conf/log4j.properties");

        // 设置服务器标志
        serverIdentify = appCfg.getPlatform() + "#" + appCfg.getServerId();
        // 父类初始化
        if (!super.init(appCfg)) {
            return false;
        }
        // 初始化对象管理区
        if (!initAppObjMan()) {
            return false;
        }
        // cdk服务初始化
        if (GsConfig.getInstance().getCdkHost().length() > 0) {
            Log.logPrintln("install cdk service......");
            CdkService.getInstance().install(GsConfig.getInstance().getGameId(), GsConfig.getInstance().getPlatform(),
                    String.valueOf(GsConfig.getInstance().getServerId()), GsConfig.getInstance().getCdkHost(),
                    GsConfig.getInstance().getCdkTimeout());
        }
        if (GsConfig.getInstance().getSyncSubHost().length() > 0) {
            Log.logPrintln("install syncSubscription service......");
            SyncSubscriptionService.getInstance().install(String.valueOf(GsConfig.getInstance().getServerId()), GsConfig.getInstance().getSyncSubHost(),
                    GsConfig.getInstance().getSyncSubTimeout());
        }
        // platform服务初始化
        if (GsConfig.getInstance().getPlatformHost().length() > 0) {
            Log.logPrintln("install platform service......");
            PlatformService.getInstance().install(GsConfig.getInstance().getGameId(), GsConfig.getInstance().getPlatform(),
                    String.valueOf(GsConfig.getInstance().getServerId()), GsConfig.getInstance().getPlatformHost(),
                    GsConfig.getInstance().getPlatformTimeout());
        }
        // 数据上报服务初始化
        if (GsConfig.getInstance().getReportHost().length() > 0 && !GsConfig.getInstance().getPlatform().equals("test")
                && !GsConfig.getInstance().getPlatform().equals("ops") && !GsConfig.getInstance().getPlatform().equals("qa")) {
            Log.logPrintln("install report service......");
            ReportService.getInstance().install(GsConfig.getInstance().getGameId(), GsConfig.getInstance().getPlatform(),
                    String.valueOf(GsConfig.getInstance().getServerId()), GsConfig.getInstance().getReportHost(),
                    GsConfig.getInstance().getReportTimeout());
        }
        // 初始化ip服务
        if (GsConfig.getInstance().getIpProxyAddr() != null && GsConfig.getInstance().getIpProxyAddr().length() > 0) {
            ipZmq = GuaJiZmqManager.getInstance().createZmq(GuaJiZmq.ZmqType.REQ);
            ipZmq.connect(GsConfig.getInstance().getIpProxyAddr());
        }
        // 初始化腾讯CMem服务连接池
        if (SysBasicCfg.getInstance().getCmcStatus() > 0 && GsConfig.getInstance().getCmcHost().length() > 0) {
            Log.logPrintln("install cmem service......");
            SockIOPool pool = SockIOPool.getInstance();
            pool.setServers(new String[]{GsConfig.getInstance().getCmcHost()});
            pool.setSocketTO(SysBasicCfg.getInstance().getCmcGetsTimeOut());
            pool.initialize();
        }
        // 初始化Redis客户端
        if (GsConfig.getInstance().getRedisHost().length() > 0 && GsConfig.getInstance().getRedisPort() > 0) {
            Log.logPrintln("install redis service......");
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxWaitMillis(6000);
            config.setTestOnBorrow(true);
            jedisPool = new JedisPool(config, GsConfig.getInstance().getRedisHost(), GsConfig.getInstance().getRedisPort(), redis.clients.jedis.Protocol.DEFAULT_TIMEOUT, GsConfig.getInstance().getRedisPassword());
            if (jedisPool.getNumActive() < 0) {
                Log.errPrintln("init jedis's pool fail ......");
                return false;
            }
        }
        // 初始公告服務功能
        if (GsConfig.getInstance().getBulletinHost().length() > 0 && GsConfig.getInstance().getBulletinGameID() > 0) {
            // 公告管理
            Log.logPrintln("init BulletinManager......");
            BulletinManager.getInstance().init();
        }
        
        // 设置打印输出标记
        BehaviorLogger.enableConsole(appCfg.isConsole());
        // 设置DB紀錄action log
        BehaviorLogger.enableConsole(appCfg.isDblog());
        // 读取默认充值配置文件
        RechargeConfig.getRechargeConfig(null);
        // 读取活动配置文件
        ActivityCfg.load();
        // 设置关服回调
        GuaJiShutdownHook.getInstance().setCallback(new ShutdownCallback());
        // 初始化全局实例对象（为了获取全服务器的玩家puid和serverid）
        Log.logPrintln("init server data......");
        ServerData.getInstance().init();
        // 竞技场管理器对象初始化
        Log.logPrintln("init arena manager......");
        ArenaManager.getInstance().init();
        // GVG初始化
        Log.logPrintln("init gvg manager......");
        GvgManager.getInstance().init();

        // 快照缓存对象初始
        Log.logPrintln("init snapshot manager......");
        SnapShotManager.getInstance().init();

        // 公会初始化
        Log.logPrintln("init alliance manager......");
        AllianceManager.getInstance().init();

        // 初始化技能管理器
        SkillHandlerManager.getInstance().init();
        // 活动数据管理器
        Log.logPrintln("init activity manager......");
        ActivityManager.getInstance().init();
        // 职业排行数据管理器
        Log.logPrintln("init profRank manager......");
        ProfRankManager.getInstance().init();
        // 远征物资数据管理器
        Log.logPrintln("init expeditionArmory manage......");
        ExpeditionArmoryManager.getInstance().init();
        // 猎豹移动服务
        //CmReportManager.getInstance().init();
        // 限时限购物资数据管理器
        Log.logPrintln("init timelimit manager ......");
        TimeLimitManager.getInstance().init();
        // 滚动广播信息服务,韩国独有
        if (GJLocal.isLocal(GJLocal.KOREAN)) {
            addTickable(new BroadCastService());
        }
        // 工会战资数据管理器
        Log.logPrintln("init alliance battle manager ......");
        AllianceBattleManager.getInstance().init();
        // 世界boss数据管理器
        Log.logPrintln("init world boss manager ......");
        WorldBossManager.getInstance().init();
        // 多人副本管理器
        Log.logPrintln("init multi eliteEntity ......");
        MultiEliteManager.getInstance().loadMultiEliteInfoEntity();
        // 排名献礼
        Log.logPrintln("init rank gift manager ......");
        RankGiftManager.getInstance().init();
        // 战斗力排行管理器
        Log.logPrintln("init fightValue rank manager ......");
        FightValueRankManager.getInstance().init();
        // 排行管理器
        Log.logPrintln("init rank manager ......");
        RankManager.getInstance().init();
        // 捞鱼排行管理器
        Log.logPrintln("init goldfish manager ......");
        GoldfishRankManager.getInstance().init();
        // 神装锻造活动管理
        Log.logPrintln("init forging equip manager ......");
        ForgingManager.getInstance().init();
        // 财富俱乐部活动管理
        Log.logPrintln("init wealth club manager ......");
        WealthClubManager.getInstance().init();
        // 气枪打靶管理
        Log.logPrintln("init shoot activity manager ......");
        ShootActivityManager.getInstance().init();
        // 好友邀请数据管理器
        Set<Integer> serActivityIds = ActivityManager.getInstance().getCurServerActiveActivityIds();
        if (serActivityIds.contains((Integer) Const.ActivityId.FRIEND_INVITE_VALUE)) {
            Log.logPrintln("init friend invite manager ......");
            if (jedisPool == null || jedisPool.getNumActive() < 0) {
                Log.errPrintln("jedis's pool is null or empty......");
                return false;
            }
            FriendInviteManager.getInstance().init();
        }
        // 跨服管理器
        Log.logPrintln("init cross server manager ......");
        CrossServerManager.getInstance().init();
        // 跨服战管理初始化
        Log.logPrintln("init cross battle service ......");
        CrossBattleService.getInstance().init();
        
        // Ur 排行数据
        Log.logPrintln("init UrRankActivityManager......");
        UrRankActivityManager.getInstance().init();
        
        Log.logPrintln("init RecordFirstManager......");
        RecordFirstManager.getInstance().init();
        
        // 壁尻 排行数据
        Log.logPrintln("init GloryHoleActivityManager......");
        GloryHoleActivityManager.getInstance().init();
        
        // 單人強敵 排行数据
        Log.logPrintln("init SingleBossRankManager......");
        SingleBossRankManager.getInstance().init();
        
        // 賽季爬塔 排行数据
        Log.logPrintln("init SeasonTowerRankManager......");
        SeasonTowerRankManager.getInstance().init();
        
        // 挖礦 排行数据
        Log.logPrintln("init MiningRankActivityManager......");
        MiningActivityManager.getInstance().init();
        

        
        // 
//        Log.logPrintln("init EighteenPrincesManager......");
//        EighteenPrincesManager.getInstance().init();
        
//        //英雄管理器 
//        Log.logPrintln("init HeroManager......");
//        HeroManager.getInstance().init();
        //戰鬥測試器
        if (appCfg.isDebug()) {
        	Log.logPrintln("init BattleDebugManager......");
        	BattleDebugManager.getInstance().init();
        }
        // 开始网络服务(保证在完全初始化完成之后)
        if (!startNetwork()) {
            return false;
        }

        return true;
    }

    /**
     * 初始化应用对象管理器
     *
     * @return
     */
    private boolean initAppObjMan() {
        ObjManager<GuaJiXID, AppObj> objMan = null;

        // 创建玩家玩家类型的全局管理器
        objMan = createObjMan(GsConst.ObjType.PLAYER);
        objMan.setObjTimeout(SysBasicCfg.getInstance().getPlayerCacheTime());

        // 创建一个manager类型的管理器，再从这个对象管理其中创建一个this对象（加入到管理器中）
        objMan = createObjMan(GsConst.ObjType.MANAGER);
        // 加入到管理表中
        objMan.allocObject(getXid(), this);
        // 一下都是从manager对象管理器去创建各种module
        // 创建聊天管理器
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.CHAT));
        // 创建竞技场管理器
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.ARENA));
        // 创建玩家快照管理器
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.SNAPSHOT));
        // 创建工会管理器
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.ALLIANCE));
        // 日常统计组件
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.STATISTICS));
        // 创建充值管理器
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.RECHARGE));
        // 创建TabDB管理器
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.TabDB_MANAGER));
        // 创建活动管理器
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.ACTIVITY));
        // 创建职业排行管理器
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.PROFRANK));
        // 创建远征物资的管理器
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.EXPEDITION_ARMORY));
        // 创建公会战管理器
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.ALLIANCE_BATTLE));
        // 创建好友邀请管理器
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.FRIEND_INVITE));
        // 创建多人副本管理器
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.MULTI_ELITE));
        // 创建世界BOSS管理器
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.WORLD_BOSS));
        // 创建跨服战管理器
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.CROSS_SERVER));
        // 创建排名献礼管理器
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.RANK_GIFT));
        // 创建排名献礼管理器
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.FIGHT_VALUE_RANK));
        // 创建充值管理器
        //createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.YAYASHOP));
        // 创建排行管理器
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.RANK_MANAGER));
        // 创建捞鱼管理器
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.GOLDFISH_RANK));
        // 创建神装锻造管理器
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.FORGING_EQUIP));
        // 财富俱乐部管理器
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.WEALTH_CLUB));
        // 气枪打靶
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.SHOOT_ACTIVITY));
        // GVG
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.GVG_FUNCTION));
        // 跨服战
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.CROSS_BATTLE));
        // Ur排行榜
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.UR_RANK));
        
        // 18路诸侯
        //createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.EighteenPrinces));
        // 挖礦排行榜
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.Mining_RANK_MANAGER));
        // 壁尻排行榜
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.GloryHole_RANK_MANAGER));
        // 單人強敵排行榜
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.SingleBoss_RANK_MANAGER));
        // 賽季爬塔排行榜
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.SeasonTower_RANK_MANAGER));
        // 伺服器成就管理
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.RECORD_FIRST_MANAGER));
        // 伺服器成就管理
        createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.Bulletin_MANAGER));
        // 戰鬥測試
        if (appCfg.isDebug()) {
        	createObj(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.BattleDebug_MANAGER));
        }

        return true;
    }

    /**
     * 帧更新
     */
    @Override
    public boolean onTick() {
        if (super.onTick()) {

            // 数据上报
            try {
                ReportService.getInstance().onTick();
            } catch (Exception e) {
                MyException.catchException(e);
            }
            // 显示服务器信息
            ServerData.getInstance().showServerInfo();
            // 显示游戏中的消息消息队列数据
            showGameStatusInfo();
            
            
            return true;
        }
        return false;
    }

    /**
     * 清空玩家缓存
     */
    @Override
    protected void onRemoveTimeoutObj(AppObj appObj) {
        if (appObj != null && appObj instanceof Player) {
            Player player = (Player) appObj;
            if (player.isOnline()) {
                player.getSession().close(false);
            }

            if (player.getPlayerData() != null && player.getPlayerData().getPlayerEntity() != null) {
                logger.info("remove player: {}, puid: {}, gold: {}, coin: {}, level: {}, vip: {}", player.getId(), player.getPuid(), player.getGold(),
                        player.getCoin(), player.getLevel(), player.getVipLevel());
            }
        }
    }

    /**
     * 创建应用对象（响应底层回调）
     */
    @Override
    protected AppObj onCreateObj(GuaJiXID xid) {
        AppObj appObj = null;
        // 创建管理器
        if (xid.getType() == GsConst.ObjType.MANAGER) {
            if (xid.getId() == GsConst.ObjId.CHAT) {
                appObj = new ChatManager(xid);
            } else if (xid.getId() == GsConst.ObjId.ARENA) {
                appObj = new ArenaManager(xid);
            } else if (xid.getId() == GsConst.ObjId.SNAPSHOT) {
                appObj = new SnapShotManager(xid);
            } else if (xid.getId() == GsConst.ObjId.ALLIANCE) {
                appObj = new AllianceManager(xid);
            } else if (xid.getId() == GsConst.ObjId.STATISTICS) {
                appObj = new DailyStatisticsManager(xid);
            } else if (xid.getId() == GsConst.ObjId.TEAM_BATTLE) {
                appObj = new TeamBattleManager(xid);
            } else if (xid.getId() == GsConst.ObjId.RECHARGE) {
                appObj = new RechargeManager(xid);
            } else if (xid.getId() == GsConst.ObjId.TabDB_MANAGER) {
                appObj = new TapDBManager(xid);
            } else if (xid.getId() == GsConst.ObjId.ACTIVITY) {
                appObj = new ActivityManager(xid);
            } else if (xid.getId() == GsConst.ObjId.PROFRANK) {
                appObj = new ProfRankManager(xid);
            } else if (xid.getId() == GsConst.ObjId.CAMPWAR) {
                appObj = new CampWarManager(xid);
            } else if (xid.getId() == GsConst.ObjId.EXPEDITION_ARMORY) {
                appObj = new ExpeditionArmoryManager(xid);
            } else if (xid.getId() == GsConst.ObjId.ALLIANCE_BATTLE) {
                appObj = new AllianceBattleManager(xid);
            } else if (xid.getId() == GsConst.ObjId.FRIEND_INVITE) {
                appObj = new FriendInviteManager(xid);
            } else if (xid.getId() == GsConst.ObjId.MULTI_ELITE) {
                appObj = new MultiEliteManager(xid);
            } else if (xid.getId() == GsConst.ObjId.WORLD_BOSS) {
                appObj = new WorldBossManager(xid);
            } else if (xid.getId() == GsConst.ObjId.CROSS_SERVER) {
                appObj = new CrossServerManager(xid);
            } else if (xid.getId() == GsConst.ObjId.RANK_GIFT) {
                appObj = new RankGiftManager(xid);
            } else if (xid.getId() == GsConst.ObjId.FIGHT_VALUE_RANK) {
                appObj = new FightValueRankManager(xid);
//            } else if (xid.getId() == GsConst.ObjId.YAYASHOP) {
//                appObj = new YayaManager(xid);
            } else if (xid.getId() == GsConst.ObjId.RANK_MANAGER) {
                appObj = new RankManager(xid);
            } else if (xid.getId() == GsConst.ObjId.GOLDFISH_RANK) {
                appObj = new GoldfishRankManager(xid);
            } else if (xid.getId() == GsConst.ObjId.FORGING_EQUIP) {
                appObj = new ForgingManager(xid);
            } else if (xid.getId() == GsConst.ObjId.WEALTH_CLUB) {
                appObj = new WealthClubManager(xid);
            } else if (xid.getId() == GsConst.ObjId.SHOOT_ACTIVITY) {
                appObj = new ShootActivityManager(xid);
            } else if (xid.getId() == GsConst.ObjId.GVG_FUNCTION) {
                appObj = new GvgManager(xid);
            } else if (xid.getId() == GsConst.ObjId.CROSS_BATTLE) {
                appObj = new CrossBattleManager(xid);
            } else if (xid.getId() == GsConst.ObjId.UR_RANK){
                appObj = new UrRankActivityManager(xid);
//            }else if(xid.getId() ==GsConst.ObjId.EighteenPrinces) {
//            	appObj=new EighteenPrincesManager(xid);
            } else if (xid.getId() == GsConst.ObjId.RECORD_FIRST_MANAGER) {
            	appObj = new RecordFirstManager(xid);
            } else if (xid.getId() == GsConst.ObjId.Mining_RANK_MANAGER) {
            	appObj = new MiningActivityManager(xid);
            } else if (xid.getId() == GsConst.ObjId.GloryHole_RANK_MANAGER) {
            	appObj = new GloryHoleActivityManager(xid);
            } else if (xid.getId() == GsConst.ObjId.Bulletin_MANAGER) {
            	appObj = new BulletinManager(xid);
            } else if (xid.getId() == GsConst.ObjId.SingleBoss_RANK_MANAGER) {
            	appObj = new SingleBossRankManager(xid);
            } else if (xid.getId() == GsConst.ObjId.SeasonTower_RANK_MANAGER) {
            	appObj = new SeasonTowerRankManager(xid);
            }else if(xid.getId() ==GsConst.ObjId.BattleDebug_MANAGER) {
            	 if (appCfg.isDebug()) {
            		 appObj=new BattleDebugManager(xid);
            	 }
            }

        } else if (xid.getType() == GsConst.ObjType.PLAYER) {
            appObj = new Player(xid);
        }

        if (appObj == null) {
            Log.errPrintln("create obj failed: " + xid);
        }
        return appObj;
    }

    @Override
    protected void onClosed() {
        // 停服前统计计算
        try {
            // 临时加载这里，虽然也可以异步更新
            ChatManager.getInstance().saveToDB();
            // DailyStatisticsManager.getInstance().statistics(null, true);
        } catch (Exception e) {
            MyException.catchException(e);
        }

        super.onClosed();
    }

    /**
     * 分发消息
     */
    @Override
    public boolean dispatchMsg(GuaJiXID xid, Msg msg) {
        if (xid.equals(getXid())) {
            return onMessage(msg);
        }
        return super.dispatchMsg(xid, msg);
    }

    /**
     * 报告异常
     */
    @Override
    public void reportException(Exception e) {
        EmailService emailService = EmailService.getInstance();
        if (emailService != null) {
            String emailTiel = String.format("shaonv_exception(%s_%s_%d)", GsConfig.getInstance().getGameId(), GsConfig.getInstance().getPlatform(),
                    GsConfig.getInstance().getServerId());

            emailService.sendEmail(emailTiel, MyException.formatStackMsg(e),
                    Arrays.asList("管理员邮箱1", "管理员邮箱2"));
        }
    }

    /**
     * 会话协议回调, 由IO线程直接调用, 非线程安全(网络io过来的，先走这里；这时还没有绑定玩家，session先和gsapp来一泡)
     */
    @Override
    public boolean onSessionProtocol(GuaJiSession session, Protocol protocol) {
        // 协议解密
        protocol = ProtoUtil.decryptionProtocol(session, protocol);
        if (protocol == null) {
            return false;
        }

        /*
         * if (appCfg.isDebug() && appCfg.isWebSocket()) {
         * session.sendProtocol(ProtoUtil.compressProtocol(protocol)); return
         * true; }
         */

        long protoTime = GuaJiTime.getMillisecond();
        try {
            // 心跳协议直接处理
            if (protocol.checkType(HP.sys.HEART_BEAT)) {
                Calendar calendar = Calendar.getInstance();
                String timeZone = calendar.getTimeZone().getID();

                HPHeartBeat.Builder builder = HPHeartBeat.newBuilder();
                builder.setTimeStamp(GuaJiTime.getSeconds());
                builder.setData(ByteString.copyFromUtf8(timeZone));
                protocol.getSession().sendProtocol(Protocol.valueOf(HP.sys.HEART_BEAT, builder));
                return true;
            }

            // 获取时区偏移
            if (protocol.checkType(HP.code.TIME_ZONE_C)) {
                HPTimeZoneRet.Builder builder = HPTimeZoneRet.newBuilder();
                builder.setTimezone(Calendar.getInstance().getTimeZone().getRawOffset() / 1000);
                protocol.getSession().sendProtocol(Protocol.valueOf(HP.code.TIME_ZONE_S, builder));
                return true;
            }

            // 充值协议
            if (protocol.checkType(HP.code.RECHARGE_REQUEST_S)) {
                GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.RECHARGE);
                return postProtocol(xid, protocol);
            }

            // 呀呀主播商城协议
            if (protocol.checkType(HP.code.YAYASHOP_REQUEST_S)) {
                GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.YAYASHOP);
                return postProtocol(xid, protocol);
            }
            // 通用数据协议
            if (protocol.checkType(HP.sys.DATA_WRAPPER)) {
                HPDataWarpper dataWarpper = protocol.parseProtocol(HPDataWarpper.getDefaultInstance());
                if (dataWarpper.hasData()) {
                    String data = new String(dataWarpper.getData().toByteArray());
                    if ("restartScript".equals(data)) {
                        GuaJiScriptManager.getInstance().restart();
                    }
                    return true;
                }
            }

            try {
                if (session.getAppObject() == null) {
                    // 登陆协议
                    if (protocol.checkType(HP.code.LOGIN_C)) {
                        HPLogin loginProtocol = protocol.parseProtocol(HPLogin.getDefaultInstance());
                        String puid = loginProtocol.getPuid().trim().toLowerCase();
                        //包含渠道标识的串码
                        String platform = loginProtocol.getPlatform();
                        //puid = GameUtil.ito_91_amendPuid(puid);
                        int serverId = loginProtocol.getServerId();
                        boolean isRegisted = loginProtocol.getRegisted();	//是否創帳號
                        int isguest = loginProtocol.getIsGuest();	//是否為guest 1guest 2工口帳號
                        //密碼驗証
                        String pwd = loginProtocol.getPasswd().trim().toLowerCase();
                        if (pwd.length() < 6) {
                            //密碼長度不夠
                        	Log.logPrintln("密碼長度不夠!");
                            return false;
                        }          
                        if (!checkPuidValid(puid, serverId, isRegisted, platform, pwd, isguest, session)) {
                            return true;
                        }
                        String[] platInfos = platform.split("#");
                        if (platInfos.length < 4) {
                            //数据不全
                            return false;
                        }
                        String channel = platInfos[4];
                        //是否断线重连，正常登陆为false，断线重连为true
                        boolean isReLogin = loginProtocol.getIsReLogin();
                        //通过channel验证对应平台的登录
                        
                        if (App.getInstance().getAppCfg().isPlatformVerify()) {
	                        ILoginVerify loginVerify = LoginVerifyManager.getLoginVerify(channel);
	                        if (loginVerify != null && !isReLogin) {
	                            if (!loginVerify.loginVerify(session, protocol)) {
	                                return false;
	                            }
	                        }
                        }
                        //登录检查验证
                        // 登陆协议时间间隔控制
                        if (puidLoginTime.containsKey(puid) && GuaJiTime.getMillisecond() <= puidLoginTime.get(puid) + 5000) {
                            Log.logPrintln("登录太频繁，5秒内不能重复登录!");
                            return true;
                        }
                        
                        puidLoginTime.put(puid, GuaJiTime.getMillisecond());
                        Log.logPrintln(String.format("preparePuidSession pre puid=%s login platform=%s", puid,platform));
                        if (!preparePuidSession(puid, serverId, pwd, isRegisted, platform, isguest, session)) {// session去找其他人session中的setAppObject（player）
                        	 Log.logPrintln(String.format("preparePuidSession after failed puid=%s login platform=%s", puid,platform));
                        	 return false;
                        }
                    } else {
                        Log.errPrintln("session appobj null cannot process unlogin protocol: " + protocol.getType()+" ip:"+session.getIpAddr());
                        return false;
                    }
                }
            } catch (Exception e) {
                MyException.catchException(e);
                return false;
            }
            return super.onSessionProtocol(session, protocol);// 这里到session中存的getAppObject
        } finally {
            protoTime = GuaJiTime.getMillisecond() - protoTime;
            if (protoTime >= 20) {
                logger.info("protocol cost time exception, protocolId: {}, costTime: {}ms", protocol.getType(), protoTime);
            }
        }
    }

    /**
     * 会话关闭回调
     */
    @Override
    public void onSessionClosed(GuaJiSession session) {
        if (session != null && session.getAppObject() != null) {
            GuaJiXID xid = session.getAppObject().getXid();
            if (xid != null && xid.isValid()) {
                postMsg(Msg.valueOf(GsConst.MsgType.SESSION_CLOSED, xid));
            }
        }
        super.onSessionClosed(session);
    }

    /**
     * 检测灰度账号
     *
     * @param puid
     * @return
     */
    private boolean checkPuidValid(String puid, int serverId, boolean isRegisted,String platform, String pwd, int isguest, GuaJiSession session) {
        int grayState = GsConfig.getInstance().getGrayState();
        // 灰度状态下, 限制灰度账号
        if (grayState > 0) {
            GrayPuidCfg grayPuid = ConfigManager.getInstance().getConfigByKey(GrayPuidCfg.class, puid);
            if (grayPuid == null) {
                session.sendProtocol(ProtoUtil.genErrorProtocol(HP.code.LOGIN_C_VALUE, Status.error.SERVER_GRAY_STATE_VALUE, 1));
                return false;
            }
        }
        
        if (puid.isEmpty()){ // 帳號空白
            session.sendProtocol(ProtoUtil.genErrorProtocol(HP.code.LOGIN_C_VALUE, Status.error.PLAYER_CREATE_FAILED_VALUE, 1));
            return false;
        }

        int playerId = ServerData.getInstance().getPlayerIdByPuid(puid, serverId);
        if (playerId == 0) {
            // 注册人数达到上限
            int registerMaxSize = GsConfig.getInstance().getRegisterMaxSize();
            if (registerMaxSize > 0 && ServerData.getInstance().getRegisterPlayer() >= registerMaxSize) {
                session.sendProtocol(ProtoUtil.genErrorProtocol(HP.code.LOGIN_C_VALUE, Status.error.REGISTER_MAX_LIMIT_VALUE, 1));
                return false;
            }
            
            if (GsConfig.getInstance().isCreateLock()) {
                session.sendProtocol(ProtoUtil.genErrorProtocol(HP.code.LOGIN_C_VALUE, Status.error.REGISTER_MAX_LIMIT_VALUE, 1));
                return false;
            }
        }
        
//        if (platform.contains("hutuo"))
//        {	
//        	if (playerId == 0)
//        	{
//            	if (isguest == 0 || isguest > 2)
//            	{
//            		session.sendProtocol(ProtoUtil.genErrorProtocol(HP.code.LOGIN_C_VALUE, Status.error.PLAYER_CREATE_FAILED_VALUE, 1));
//                    return false;
//            	}
//        	}        	
// jackal mark
//        	if (playerId == 0)	//帳號不存在去loginserver判斷
//        	{
//        		//hutuo判斷是是否要註冊
//            	int type = isRegisted ? 1 : 2;	//1註冊2確認
//            	int status = chkhutuo(puid,pwd, type);
//            	if (status == 1400)
//            	{
//            		//要註冊，但已有帳號
//            		logger.info("Repeat player: {}, puid: {}, serverId: {}", playerId, puid, serverId);
//                	session.sendProtocol(ProtoUtil.genErrorProtocol(HP.code.LOGIN_C_VALUE, Status.error.REGISTER_EXIST_VALUE, 1));
//                	return false;
//            	}
//            	else if (status == 1300)
//            	{
//            		//帳號不存在
//            		session.sendProtocol(ProtoUtil.genErrorProtocol(HP.code.LOGIN_C_VALUE, Status.error.REGISTER_NOT_EXIST_VALUE, 1));
//                    return false;
//            	}
//            	else if (status == 1301 || status == -1)
//            	{
//            		//建立帳號失敗
//            		session.sendProtocol(ProtoUtil.genErrorProtocol(HP.code.LOGIN_C_VALUE, Status.error.REGISTER_MAX_LIMIT_VALUE, 1));
//                    return false;
//            	}
//            	else if (status == 1401)
//            	{
//            		//密碼錯誤
//            		session.sendProtocol(ProtoUtil.genErrorProtocol(HP.code.LOGIN_C_VALUE, Status.error.PASSWORD_ERROR_VALUE, 1));
//                    return false;
//            	}
//        	}
//            if (playerId != 0 && isRegisted)
//            {	//要註冊，但已有帳號
//            	logger.info("Repeat player: {}, puid: {}, serverId: {}", playerId, puid, serverId);
//            	session.sendProtocol(ProtoUtil.genErrorProtocol(HP.code.LOGIN_C_VALUE, Status.error.REGISTER_EXIST_VALUE, 1));
//            	return false;
//            }
            /*if (playerId == 0 && !isRegisted)
            {	//非註冊，但找不到帳號
            	logger.info("No player found: {}, puid: {}, serverId: {}", playerId, puid, serverId);
            	session.sendProtocol(ProtoUtil.genErrorProtocol(HP.code.LOGIN_C_VALUE, Status.error.REGISTER_NOT_EXIST_VALUE, 1));
            	return false;
            }*/
//        }
        return true;
    }

    /**
     * 查询ip信息
     *
     * @param ip
     * @return
     */
    public IpAddrEntity queryIpAddrEntity(String ip) {
        if (SysBasicCfg.getInstance().isIpCacheEnable() && ipZmq != null) {
            synchronized (ipZmq) {
                try {
                    ipZmq.discardMsg();
                    ipZmq.send(ip.getBytes(), 0);
                    if (ipZmq.pollEvent(GuaJiZmq.HZMQ_EVENT_READ, GsConfig.getInstance().getIpProxyTimeout()) > 0) {
                        byte[] bytes = new byte[1024];
                        int recvSize = ipZmq.recv(bytes, 0);
                        IpAddrEntity entity = OSOperator.bytesToObject(bytes, 0, recvSize);
                        if (entity.getId() > 0) {
                            return entity;
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    /**
     * 从缓存中获取玩家信息
     *
     * @param playerId
     * @return
     */
    public PlayerData queryPlayerData(int playerId) {
        PlayerData playerData = null;
        try {

        } catch (Exception e) {
            MyException.catchException(e);
        }
        return playerData;
    }

    /**
     * 保存玩家缓存信息
     *
     * @param playerData
     */
    public void savePlayerData(PlayerData playerData) {
        try {

        } catch (Exception e) {
            MyException.catchException(e);
        }
    }

    /**
     * 准备puid对应的会话
     *
     * @param puid
     * @return
     */
    private boolean preparePuidSession(String puid, int serverId, String pwd, boolean isRegisted,String platform, int isguest, GuaJiSession session) {
        int playerId = ServerData.getInstance().getPlayerIdByPuid(puid, serverId);
        if (playerId == 0 && (isRegisted || platform.contains("android_")|| platform.contains("win32"))) {
        	//h365,工口平台不檢查是否送註冊
            PlayerEntity playerEntity = new PlayerEntity(puid, serverId, "", "", "", pwd);
            playerEntity.setisguest(isguest);
            if (!DBManager.getInstance().create(playerEntity)) {
                return false;
            }
            playerId = playerEntity.getId();// 数据库id为playerID
            ServerData.getInstance().addPuidAndPlayerId(puid, serverId, playerId);// puid客户端来的
            logger.info("create player entity: {}, puid: {}, serverId: {}", playerId, puid, serverId);
        }
        // playerId still zero
        if (playerId == 0) {
        	logger.info("create player entity fail !!: {}, puid: {}, serverId: {}", playerId, puid, serverId);
        	 return false;
        }
        GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId);
        ObjBase<GuaJiXID, AppObj> objBase = lockObject(xid);
        try {
            // 对象不存在即创建
            if (objBase == null || !objBase.isObjValid()) {
                objBase = createObj(xid);
                if (objBase != null) {
                    objBase.lockObj();
                }

                logger.info("create player: {}, puid: {}, serverId: {}", playerId, puid, serverId);
            }

            // 会话绑定应用对象
            if (objBase != null) {

                Player player = (Player) objBase.getImpl();
                // 不同的session做踢出
                if (player != null && player.getSession() != null && player.getSession() != session) {
                    player.kickout(Const.kickReason.DUPLICATE_LOGIN_VALUE);
                }

                // 绑定会话对象
                session.setAppObject(objBase.getImpl());// session的绑定对象易主
            }
        } finally {
            if (objBase != null) {
                objBase.unlockObj();
            }
        }
        return true;
    }

    /**
     * 所有配置文件检查完后，根据需求对配置数据进行重组
     */
    @Override
    public boolean checkConfigData() {
        // 活动时间配置重组
        ActivityUtil.activityTimeCfgsClassify();
        return super.checkConfigData();
    }

    /***
     * 世界聊天广播 + 主页广播
     *
     * @param msg
     */
    public void broadcastChatWorldMsg(String worldMsg, String chatMsg) {
        if (worldMsg != null && !worldMsg.equals("")) {
            ChatMsg worldBroadMsg = new ChatMsg();
            worldBroadMsg.setType(Const.chatType.WORLD_BROADCAST_VALUE);
            worldBroadMsg.setChatMsg(worldMsg);
            worldBroadMsg.setMsgType(1);
            ChatManager.getInstance().postBroadcast(worldBroadMsg);
        }

        if (chatMsg != null && !chatMsg.equals("")) {
            ChatMsg chatBroadMsg = new ChatMsg();
            chatBroadMsg.setType(Const.chatType.CHAT_BROADCAST_VALUE);
            chatBroadMsg.setChatMsg(chatMsg);
            chatBroadMsg.setMsgType(1);
            ChatManager.getInstance().postBroadcast(chatBroadMsg);
        }
    }

    /**
     * 获取jedis pool
     */
    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public String getServerIdentify() {
        return serverIdentify;
    }

    public String showStatus() {
        StringBuilder result = new StringBuilder("[GameStatus]\n\n");
        // thread
        result.append("msgThreadCount: ").append(this.msgExecutor.getThreadNum()).append("\n");
        //result.append("msgThreadDump: ").append(this.msgExecutor.printDump()).append("\n");
       // result.append("taskThreadCount: ").append(this.taskExecutor.getThreadNum()).append("\n");
       // result.append("taskThreadDump: ").append(this.taskExecutor.printDump()).append("\n\n");
        //result.append("tickDumpCount: ").append(this.tickExecutor.getThreadNum()).append("\n");
        //result.append("tickDump").append(this.tickExecutor.printDump()).append("\n\n\"");
        return result.toString();
    }
    
	public void showGameStatusInfo() {
		// 每分钟显示一个服务器信息
		if (GuaJiTime.getSeconds() - lastGameShowTime >= 60) {
			lastGameShowTime = GuaJiTime.getSeconds();
			// 记录信息
			logger.info("GameStatus info: {}",showStatus());
		}
	}
	
	public String chkhutuo(String puid, String token, int type, int mid, int pid) {
		//String chkhutuoURL = "http://devgoc.bigwin-tech.com/idlepaycenter/chkregistered";
		String chkhutuoURL = "https://recharge.idleparadise.com/idlepaycenter/chkH54647";
		int sid = GsConfig.getInstance().getServerId();
		if (sid == 1)
		{
			chkhutuoURL = "http://devgoc.bigwin-tech.com/idlepaycenter/chkH54647";//內部測試用
			sid = 6;
		}
			
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		HttpResponse response = null;
		try {		
			JSONObject obj = new JSONObject();
			obj.put("sid", sid);
			obj.put("mid", (int)mid);
			obj.put("puid", puid);
			obj.put("token", token);
			obj.put("pid", (int)pid);
			obj.put("type", (int)type);
			Log.logPrintln("chkhutuoapi : " + chkhutuoURL + "?" + obj.toString());
			
			String postRequest = URLEncoder.encode(obj.toString(), "UTF-8");
			Log.logPrintln("chkhutuoapi : " + chkhutuoURL + "?" + postRequest);
			//String postRequest = "puid="+puid+"&pwd="+pwd+"&type="+type;
			
			httpClient = HttpClients.custom().build();

			httpPost = new HttpPost(chkhutuoURL);
			RequestConfig reqConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
			httpPost.setConfig(reqConfig);
			httpPost.setHeader(HttpHeaders.CONNECTION, "close");

			StringEntity entity = new StringEntity(postRequest);
			entity.setContentEncoding("utf-8");
			entity.setContentType("application/json");
			httpPost.setEntity(entity);
			response = httpClient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			String resultStr = EntityUtils.toString(httpEntity);
			Log.logPrintln("tapdbapi result: " + resultStr);
			resultStr = resultStr.trim().substring(resultStr.indexOf("{"));//有一個特殊的符號要去掉
			JSONObject result = JSONObject.fromObject(resultStr);
			httpPost.releaseConnection();
			//Log.logPrintln("coins : " + result.getInt("coins"));
			return resultStr;//(result.getString("result")=="0000")? 1 : -1;
		
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return "";
	}
}
