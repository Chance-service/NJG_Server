package com.guaji.game.module.activity.timeLimit;

import org.guaji.db.DBManager;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ActivityCfg.ActivityItem;
import com.guaji.game.entity.ServerTimeLimitEntity;
import com.guaji.game.manager.ActivityManager;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.protocol.Const;

/**
 * 限时限购数据管理
 * @author xulinqs
 *
 */
public class TimeLimitManager {

	private static TimeLimitManager instance = null;
	
	public static TimeLimitManager getInstance(){
		if(instance == null) {
			instance = new TimeLimitManager();
		}
		return instance;
	}
	
	public TimeLimitManager() {
//		TimerManager.getInstance().register(0, 1, 0, -1, -1, new ITimerListener() {
//			@Override
//			public void handleAlarm(TimerEntry entry) {
//				TimeLimitManager.getInstance().refresh();
//			}
//		});
	}
	
	private ServerTimeLimitEntity curTimeLimitEntity = null;
	
	public void init() {
		ActivityItem activityItem = ActivityManager.getInstance().getActiveActivityItem(Const.ActivityId.TIME_LIMIT_PURCHASE_VALUE);
		if(activityItem != null) {
			ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.TIME_LIMIT_PURCHASE_VALUE);
			if(activityTimeCfg != null) {
				this.setCurTimeLimitEntity(DBManager.getInstance().fetch(ServerTimeLimitEntity.class, "from ServerTimeLimitEntity where stageId = ?", activityTimeCfg.getStageId()));
				if(this.curTimeLimitEntity != null) {
					this.curTimeLimitEntity.convertData();
				}
			}
		}
		
		this.refresh();
	}

	public ServerTimeLimitEntity getCurTimeLimitEntity() {
		return curTimeLimitEntity;
	}

	public void setCurTimeLimitEntity(ServerTimeLimitEntity curTimeLimitEntity) {
		this.curTimeLimitEntity = curTimeLimitEntity;
	}

	/**
	 * 把当前的活动状态刷新到当前配置里面的状态
	 */
	public void refresh() {
		
		ActivityItem activityItem = ActivityManager.getInstance().getActiveActivityItem(Const.ActivityId.TIME_LIMIT_PURCHASE_VALUE);
		if(activityItem==null)
			return ;
		
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.TIME_LIMIT_PURCHASE_VALUE);
		if(timeCfg == null || timeCfg.isEnd()){
			this.curTimeLimitEntity = null;
		}else{
			if(this.curTimeLimitEntity == null || this.curTimeLimitEntity.getStageId() != timeCfg.getStageId()) {
				// 
				this.curTimeLimitEntity = new ServerTimeLimitEntity();
				this.curTimeLimitEntity.setStageId(timeCfg.getStageId());
				DBManager.getInstance().create(this.curTimeLimitEntity);
			}
		}
	}
	
	
	
}
