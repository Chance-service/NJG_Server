package com.guaji.game.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.guaji.app.AppObj;
import org.guaji.msg.Msg;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guaji.game.GsApp;
import com.guaji.game.GsConfig;
import com.guaji.game.config.ActivityCfg;
import com.guaji.game.config.ActivityCfg.ActivityItem;
import com.guaji.game.entity.HaremActivityEntity;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.FunctionUnlockCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity.OpenActivity;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.module.activity.ActiveCompliance.ActiveStatus;
import com.guaji.game.module.activity.activity144.Activity144LittleTestManager;

/**
 * 服务器活动管理
 */

public class ActivityManager extends AppObj {

    /**
     * 当前正在激活状态下的活动信息
     *
     * @author rudy
     */
    public class ActiveActivityData implements Comparable<ActiveActivityData> {
        private int activityId;
        private int stageId;
        private int limitLevel;

        public ActiveActivityData() {
            this.activityId = 0;
            this.stageId = 0;
            this.limitLevel = 0;
        }

        public int getActivityId() {
            return activityId;
        }

        public void setActivityId(int activityId) {
            this.activityId = activityId;
        }

        public int getStageId() {
            return stageId;
        }

        public void setStageId(int stageId) {
            this.stageId = stageId;
        }

        public int getLimitLevel() {
            return limitLevel;
        }

        public void setLimitLevel(int limitLevel) {
            this.limitLevel = limitLevel;
        }

        public OpenActivity.Builder toActivityBuilder() {
            OpenActivity.Builder activityBuilder = OpenActivity.newBuilder();
            activityBuilder.setActivityId(this.activityId);
            activityBuilder.setStageId(this.stageId);
            return activityBuilder;
        }


        @Override
        public int compareTo(ActiveActivityData o) {
            if (this.activityId == o.getActivityId() && this.stageId == o.getStageId()) {
                return 0;
            } else {
                return this.activityId - o.getActivityId();
            }
        }
    }

    /**
     * 活动管理器Tick周期时间
     */
    private long lastTickTime = 0;

    /**
     * 日志对象
     */
    public static Logger logger = LoggerFactory.getLogger("Server");

    /**
     * 当前所有正在开放的活动
     */
    private TreeSet<ActiveActivityData> activeActivitySet;
    private TreeSet<Integer> curActivityIdList;

    /**
     * 全局对象, 便于访问
     */
    private static ActivityManager instance = null;

    public ActivityManager(GuaJiXID xid) {
        super(xid);
        activeActivitySet = new TreeSet<ActiveActivityData>();
        curActivityIdList = new TreeSet<Integer>();
        if (instance == null) {
            instance = this;
        }
    }

    /**
     * 获取全局实例对象
     */
    public static ActivityManager getInstance() {
        return instance;
    }

    private static long openServerTime = 0;

    public boolean init() {
        String serviceDate = GsConfig.getInstance().getServiceDate();
        openServerTime = GuaJiTime.DATE_FORMATOR_DAYNUM(serviceDate).getTime();
        loadCurActiveActivity();
        return true;
    }

    /**
     * 加载所有已激活的活动
     *
     * @return
     */
    private boolean loadCurActiveActivity() {
        TreeSet<ActiveActivityData> tmpActiveActivitySet = new TreeSet<ActiveActivityData>();
        TreeSet<Integer> tmpActivityIdList = new TreeSet<Integer>();

        List<ActivityItem> allActivityCfgs = ActivityCfg.getActivityItemList();
        for (ActivityItem activityCfg : allActivityCfgs) {

            int timeType = activityCfg.getActivityTimeType().ordinal();
            ActiveActivityData activityData = new ActiveActivityData();
            activityData.setActivityId(activityCfg.getId());
            if (timeType == GsConst.ActivityTimeType.ALWARDS_OPEN.ordinal()) {
                // 永久开放活动
                activityData.setStageId(-1);
                tmpActiveActivitySet.add(activityData);
                tmpActivityIdList.add(activityCfg.getId());
            } else if (timeType == GsConst.ActivityTimeType.SERVER_OPEN_DELYS.ordinal()) {
                // 开服活动

                long delayTime = (int) activityCfg.getParam("startHoursAfterOpenSer");
                long activeTime = openServerTime + delayTime * 3600 * 1000;
                long curTime = GsApp.getInstance().getCurrentTime();

                //必须同时满足下面两个时间
                if (curTime < activeTime)//开后延迟段时间
                {
                    continue;
                } else {
                    // 开服延后点时间开放
                    Date limitDate = (Date) activityCfg.getParam("activeDay");
                    long limitTime = limitDate.getTime();
                    if (curTime < limitTime) {
                        continue;
                    }
                }

                // 判断活动是否正在开启
                long opTime = (int) activityCfg.getParam("openHours");
                long openTime = opTime * 3600 * 1000;
                long activityCloseTime = openServerTime + openTime;

                // 配置表中是否有配置
                int activityId = activityCfg.getId();
                ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);

                if (curTime < activityCloseTime && timeCfg != null) {
                    activityData.setStageId(-1);
                    tmpActiveActivitySet.add(activityData);
                    tmpActivityIdList.add(activityCfg.getId());
                }
            } else if (timeType == GsConst.ActivityTimeType.CYCLE_TIME_OPEN.ordinal()) {
                // 周期开放活动
                int activityId = activityCfg.getId();
                // 开服时间在limitDate之前的服务器不开放此活动
                Date limitDate = (Date) activityCfg.getParam("activeDay");
                if (limitDate != null) {
                    long limitTime = limitDate.getTime();
                    if (openServerTime < limitTime) {
                        continue;
                    }
                }

                ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
                if (timeCfg != null) {
                    activityData.setStageId(timeCfg.getStageId());
                    tmpActiveActivitySet.add(activityData);
                    tmpActivityIdList.add(activityCfg.getId());
                }
            }
        }

        // 检查活动列表是否变更
        boolean isChange = false;
        if (tmpActiveActivitySet.size() != activeActivitySet.size()) {
            isChange = true;
        } else {
            Object[] activeActivitys = activeActivitySet.toArray();
            Object[] tmpActiveActivitys = tmpActiveActivitySet.toArray();
            for (int i = 0; i < activeActivitys.length; i++) {
                ActiveActivityData oldData = (ActiveActivityData) activeActivitys[i];
                ActiveActivityData curData = (ActiveActivityData) tmpActiveActivitys[i];
                if (oldData.compareTo(curData) != 0) {
                    isChange = true;
                }
            }
        }
        if (isChange) {
            logger.info("activity list was changed, cur activityId List: {}", tmpActivityIdList.toString());
            // 告知所有玩家活动列表更新
            Msg msg = Msg.valueOf(GsConst.MsgType.ACTIVITY_LIST_CHANGE);
            GsApp.getInstance().broadcastMsg(msg, GsApp.getInstance().getObjMan(GsConst.ObjType.PLAYER));
        }

        // 替换新的活动列表
        activeActivitySet = tmpActiveActivitySet;
        curActivityIdList = tmpActivityIdList;
        return true;
    }
    
    /**
     * 活動時間變化做某事
     */
    
    public void DoActivitysometing() {
    	Activity144LittleTestManager.checkquestion_144();
    }


    /**
     * 定时检查活动配置
     */
    @Override
    public boolean onTick() {
        long curTime = GsApp.getInstance().getCurrentTime();
        if (curTime - lastTickTime >= GsConst.ManObjTickPeriod.ACTIVITY_MAN_TICK) {
            loadCurActiveActivity();
            DoActivitysometing();
            lastTickTime = curTime;
        }
        return true;
    }

    /**
     * 获取个人活动开放列表
     *
     * @param player
     * @return
     */
    public List<OpenActivity.Builder> getPersonalActiveActivityBuilderList(Player player) {
        int playerLevel = player.getLevel();
        List<OpenActivity.Builder> activityList = new ArrayList<OpenActivity.Builder>();
        // 等级限制去过过滤全服活动
        for (ActiveActivityData data : activeActivitySet) {
            if (playerLevel < data.getLimitLevel()) {
                continue;
            }
            activityList.add(data.toActivityBuilder());
        }
        // 额外判断注册天数活动 timeType =4...
        Date registerDate = player.getPlayerData().getPlayerEntity().getCreateTime();
        List<Integer> activityIds = new ArrayList<Integer>();
        if (ActivityUtil.getRegisterCycleActivityId(registerDate) > 0) {
            // 新手周期活动
            activityIds.add(Const.ActivityId.REGISTER_CYCLE_VALUE);
        }
        // 注册天数内的充值返利
        if (ActivityUtil.calcRechargeRebateActivityStatus(registerDate) > GsConst.RechargeRebateActivity.STATUS_DELAY) {
            activityIds.add(Const.ActivityId.RECHARGE_REBATE_VALUE);
        }
        //注册天数内的打折礼包
        if (ActivityUtil.calcSalePacketActivityStatus(registerDate, player.getPlayerData().getPlayerEntity().getMergeTime()) > 0) {
            activityIds.add(Const.ActivityId.SALE_PACKET_VALUE);
        }

        if (ActivityUtil.calcNewNdReisterDates(registerDate,player.getPlayerData().getPlayerEntity().getMergeTime())) {
            // 活动实体类型
            HaremActivityEntity harem = player.getPlayerData().getHaremActivityEntity();
            if (harem != null) {
                activityIds.add(Const.ActivityId.NEW_ND_VALUE);
            }

        }

        //注册天数内的连续充值活动
        if (ActivityUtil.calcContinueRechargeActivityStatus(registerDate, player.getPlayerData().getPlayerEntity().getMergeTime()) > 0) {
            activityIds.add(Const.ActivityId.CONTINUE_RECHARGE_VALUE);
        }
        
		ActiveStatus activeStatus = ActivityUtil.getActiveComplianceStatus(player.getPlayerData());
		if (activeStatus != null) {
	        if (activeStatus.calcActivitySurplusTime() > 0){
	        	activityIds.add(Const.ActivityId.ACTIVECOMPLIANCE_VALUE);
			}
		}

        //7日之诗 活动 新手限定任務
		if (FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.Newbie_Unlock)){
            activityIds.add(Const.ActivityId.ACCUMULATIVE_LOGIN_SEVEN_VALUE);
            activityIds.add(Const.ActivityId.ACTIVITY148_MARRY_GAME_VALUE);
            activityIds.add(Const.ActivityId.ACTIVITY149_THREE_DAY_VALUE);
            activityIds.add(Const.ActivityId.ACTIVITY150_LIMIT_GIFT_VALUE);
        }
               
        // 活動成就156
        if (ActivityUtil.clacQuestSurplusTime(registerDate) > 0) {
            activityIds.add(Const.ActivityId.ACTIVITY156_ACHIEVE_FIGHTVALUE_VALUE);
        }

        // 新用户许愿池活动
        if (ActivityUtil.calcWelfareRewardDates(player)) {
            activityIds.add(Const.ActivityId.WELFAREBYREGDATE_REWARD_VALUE);
        }
        
        // 階段禮包179
        if (!ActivityUtil.checkStepGifeOver(player)) {
        	// 還沒領完階段禮包
        	activityIds.add(Const.ActivityId.ACTIVITY179_Step_Gift_VALUE);
        }

        for (int activityId : activityIds) {
            if (activityId > 0) {
                OpenActivity.Builder activityBuilder = OpenActivity.newBuilder();
                activityBuilder.setActivityId(activityId);
                activityBuilder.setStageId(-1);
                activityList.add(activityBuilder);
            }
        }
        return activityList;
    }

    /**
     * 获取服务器当前所有正在开放的活动Id
     */
    public Set<Integer> getCurServerActiveActivityIds() {
        return curActivityIdList;
    }

    /**
     * 获得正在开启的指定id的活动配置
     *
     * @param activityId
     * @return
     */
    public ActivityItem getActiveActivityItem(int activityId) {
        if (curActivityIdList.contains(activityId)) {
            return ActivityCfg.getActivityItem(activityId);
        }
        return null;
    }

    public long openServerLeftTime(long openTime) {
        long activityCloseTime = openServerTime + openTime;
        long curTime = GsApp.getInstance().getCurrentTime();
        long leftTime = activityCloseTime - curTime;
        if (leftTime > 0) {
            return leftTime;
        }
        return -1;
    }
}
