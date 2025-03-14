package com.guaji.game.module.activity.shoot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.config.ActivityCfg.ActivityItem;
import com.guaji.game.entity.ShootActivityEntity;
import com.guaji.game.manager.ActivityManager;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;

/**
 * 气枪打靶
 * 
 * @author zdz
 *
 */
public class ShootActivityManager extends AppObj {

	private static ShootActivityManager instance;

	public ShootActivityManager(GuaJiXID xid) {
		super(xid);
		if (instance == null) {
			instance = this;
		}
	}

	public static ShootActivityManager getInstance() {
		return instance;
	}

	private ShootActivityEntity shootActivityEntity = null;

	/**
	 * 活动是否显示
	 */
	private boolean isShow = true;

	/**
	 * 关闭活动的时间
	 */
	private long closeActivityTime = 0;

	public void init() {
		ActivityItem activityItem = ActivityManager.getInstance().getActiveActivityItem(Const.ActivityId.SHOOT_ACTIVITY_VALUE);
		if (activityItem != null) {
			ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.SHOOT_ACTIVITY_VALUE);
			if (activityTimeCfg != null) {
				List<Object> entityList = DBManager.getInstance().query("from ShootActivityEntity where invalid = 0");
				if (entityList != null && entityList.size() > 0) {
					this.shootActivityEntity = (ShootActivityEntity) entityList.get(0);
				}

			}
		}

		this.refresh();
	}

	public ShootActivityEntity getShootActivityEntity() {
		return shootActivityEntity;
	}

	public void setShootActivityEntity(ShootActivityEntity shootActivityEntity) {
		this.shootActivityEntity = shootActivityEntity;
	}

	/**
	 * 创建活动数据
	 */
	public void refresh() {
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.SHOOT_ACTIVITY_VALUE);
		if (timeCfg == null) {
			return;
		}
		String startTime = timeCfg.getStartTime().replace("_", " ");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long startTimeMillis = 0;
		Log.logPrintln("shoot start time s: " + timeCfg.getStartTime());
		try {
			startTimeMillis = sdf.parse(startTime).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (timeCfg == null || timeCfg.isEnd()) {
			this.shootActivityEntity = null;
		} else {
			if (this.shootActivityEntity == null) {
				// 活动开始时间
				int activityTime = (int) (startTimeMillis / 1000);
				// 每4天时间
				int dayTime = 4 * 24 * 60 * 60;
				// 每一轮时间
				int eightTime = 8 * 24 * 60 * 60;
				// 当前时间
				int cur = GuaJiTime.getSeconds();

				this.shootActivityEntity = new ShootActivityEntity();

				// 周期阶段标识（A奖池 or B奖池）
				//int stateTime = (cur - activityTime) % eightTime;
				int shootState = Const.shootRewardState.SHOOT_A_VALUE;;
				/*
				if (stateTime >= 0 && stateTime < dayTime) {
					shootState = Const.shootRewardState.SHOOT_A_VALUE;
				} else if (stateTime >= dayTime) {
					shootState = Const.shootRewardState.SHOOT_B_VALUE;
				}
				 */
				this.shootActivityEntity.setShootState(shootState);
				int refreshTime = activityTime + (((cur - activityTime) / dayTime) + 1) * dayTime;
				this.shootActivityEntity.setShootRefreshTime(refreshTime);
				DBManager.getInstance().create(this.shootActivityEntity);
			}
		}
		Log.logPrintln("shoot start time ms: " + startTimeMillis);
	}

	@Override
	public boolean onTick() {
		
		/*
		ActivityItem activityItem = ActivityManager.getInstance().getActiveActivityItem(Const.ActivityId.SHOOT_ACTIVITY_VALUE);

		if (activityItem == null) {
			return false;
		}

		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.SHOOT_ACTIVITY_VALUE);
		if (timeCfg == null) {
			return false;
		}
		
		if (!isShow) {
			if (GuaJiTime.getMillisecond() - this.closeActivityTime > SysBasicCfg.getInstance().getShootCloseTime() * 60 * 60 * 1000) {
				isShow = true;
				Msg msg = Msg.valueOf(GsConst.MsgType.SHOOT_ACTIVITY);
				GsApp.getInstance().broadcastMsg(msg, GsApp.getInstance().getObjMan(GsConst.ObjType.PLAYER));
				Log.logPrintln("change shoot Open " + isShow);
			}
		} else {
			long lastRefreshTime = 0;
			if(shootActivityEntity!=null){
				lastRefreshTime = shootActivityEntity.getShootRefreshTime();
			}
			if (ActivityUtil.checkShootRewardTime(lastRefreshTime) && isShow) {

				if (shootActivityEntity == null) {
					refresh();
				}

				String startTime = timeCfg.getStartTime().replace("_", " ");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				long startTimeMillis = 0;
				try {
					startTimeMillis = sdf.parse(startTime).getTime();
				} catch (ParseException e) {
					e.printStackTrace();
				}

				int dayTime = 4 * 24 * 60 * 60;
				int cur = GuaJiTime.getSeconds();
				int activityTime = (int) (startTimeMillis / 1000);
				int refreshTime = (activityTime + (((cur - activityTime) / dayTime) + 1) * dayTime);

				int eightTime = 8 * 24 * 60 * 60;
				int stateTime = (cur - activityTime) % eightTime;
				int shootState = Const.shootRewardState.SHOOT_A_VALUE;
				if (stateTime >= 0 && stateTime < dayTime) {
					shootState = Const.shootRewardState.SHOOT_A_VALUE;
				} else if (stateTime >= dayTime) {
					shootState = Const.shootRewardState.SHOOT_B_VALUE;
				}

				shootActivityEntity.setShootState(shootState);
				this.closeActivityTime = GuaJiTime.getMillisecond();
				isShow = false;

				shootActivityEntity.setShootRefreshTime(refreshTime);
				Msg msg = Msg.valueOf(GsConst.MsgType.SHOOT_ACTIVITY);
				GsApp.getInstance().broadcastMsg(msg, GsApp.getInstance().getObjMan(GsConst.ObjType.PLAYER));
				shootActivityEntity.notifyUpdate();
				Log.logPrintln("change shoot isClose " + isShow + " refreshTime:" + refreshTime + "  shootState:" +shootState );
			}
		}
		*/
		return true;
	}

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	public long getCloseActivityTime() {
		return closeActivityTime;
	}

	public void setCloseActivityTime(long closeActivityTime) {
		this.closeActivityTime = closeActivityTime;
	}

}
