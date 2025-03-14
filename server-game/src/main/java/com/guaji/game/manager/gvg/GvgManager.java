package com.guaji.game.manager.gvg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiRand;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;
import org.guaji.xid.GuaJiXID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guaji.game.GsApp;
import com.guaji.game.attribute.Attribute;
import com.guaji.game.battle.BattleRole;
import com.guaji.game.battle.Battlefield;
import com.guaji.game.bean.GvgBattleResultBean;
import com.guaji.game.bean.GvgOccupyBean;
import com.guaji.game.config.GvgCfg;
import com.guaji.game.config.GvgCitiesCfg;
import com.guaji.game.config.GvgCitiesCfg.UnlockLimit;
import com.guaji.game.config.GvgDebuffCfg;
import com.guaji.game.config.GvgSeasonAwdCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.GvgAllianceEntity;
import com.guaji.game.entity.GvgCityEntity;
import com.guaji.game.entity.GvgTimeEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.entity.VitalityRankEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.ChatManager;
import com.guaji.game.manager.MailManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Battle.BattleInfo;
import com.guaji.game.protocol.Battle.battleType;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.attr;
import com.guaji.game.protocol.GroupVsFunction.CityBattleInfo;
import com.guaji.game.protocol.GroupVsFunction.CityBattlePush;
import com.guaji.game.protocol.GroupVsFunction.CityInfo;
import com.guaji.game.protocol.GroupVsFunction.CityStatus;
import com.guaji.game.protocol.GroupVsFunction.GVGStatus;
import com.guaji.game.protocol.GroupVsFunction.GuildInfo;
import com.guaji.game.protocol.GroupVsFunction.TeamNumberPush;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Mail;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsConst.Alliance;
import com.guaji.game.util.PlayerUtil;

/**
 * GVG管理
 */
/**
 * @author Administrator
 *
 */
public class GvgManager extends AppObj {

	/**
	 * gvg 日志
	 */
	private final static Logger logger = LoggerFactory.getLogger("GVG");

	private static GvgManager instance = null;
	/**
	 * 每秒执行
	 */
	private long millisecond = 0L;

	/**
	 * 记录4点 这一分钟是否已重置
	 */
	private boolean isHaveReset = false;

	/**
	 * 缓存佣兵快照<key:roleId value:快照数据>
	 */
	private Map<Integer, PlayerSnapshotInfo.Builder> snapshotCache = new HashMap<Integer, PlayerSnapshotInfo.Builder>();

	/**
	 * 缓存战斗结果<key:cityId value:战斗数据>
	 */
	private ConcurrentHashMap<Integer, Battlefield> battleCache = new ConcurrentHashMap<Integer, Battlefield>();

	/**
	 * 城池战斗时间缓存<key:cityId, value:下次战争时间点>
	 */
	private Map<Integer, Long> battleTimeCache = new ConcurrentHashMap<Integer, Long>();

	/**
	 * 城池防御者是NPC缓存<key:cityId, value:战斗结果>
	 */
	private Map<Integer, Integer> defendersNpc = new HashMap<Integer, Integer>();

	/**
	 * 战斗日志<key:cityId value:日志数据>
	 */
	private ConcurrentHashMap<Integer, List<CityBattleInfo>> battleLogsCache = new ConcurrentHashMap<Integer, List<CityBattleInfo>>();

	/**
	 * 连续杀敌次数缓存<key:playerId value:杀敌数据>
	 */
	private Map<Integer, List<GvgKillEnemy>> killEnemyCache = new HashMap<Integer, List<GvgKillEnemy>>();

	/**
	 * 缓存反攻需要清理数据的玩家ID<key:城池ID, value:玩家ID集合>
	 */
	private Map<Integer, Set<Integer>> playerIds = new HashMap<Integer, Set<Integer>>();

	/**
	 * 缓存派遣小于三人队伍的玩家列表
	 */
	private List<Integer> playerLimitIds = new ArrayList<Integer>();
	
	
	/**
	 * 缓存派遣小于三人队伍的玩家列表
	 */
	private List<Integer> playerDefenderLimitIds = new ArrayList<Integer>();
	
	/**
	 * 当日参与的玩家的列表
	 */
	private List<Integer> todayPlayerIds = new ArrayList<Integer>();

	/**
	 * 控制功能开关
	 */
	private boolean isOpenFunction = true;

	public GvgManager(GuaJiXID xid) {
		super(xid);
		if (instance == null) {
			instance = this;
		}
	}

	public static GvgManager getInstance() {
		return instance;
	}

	public void setOpenFunction(boolean isOpenFunction) {
		this.isOpenFunction = isOpenFunction;
	}

	public boolean isIsOpenFunction() {
		return this.isOpenFunction;
	}

	/**
	 * 功能初始化
	 */
	public void init() {
		// 加载初始数据
		GvgService.getInstance().init();

		// 加载参与的玩家id 
		for (GvgCityEntity cityEntity : GvgService.getInstance().getCities().values()) {

			for (GvgOccupyBean attackItem : cityEntity.getAttackerList()) {
				if(!this.todayPlayerIds.contains(attackItem.getPlayerId()))
					this.todayPlayerIds.add(attackItem.getPlayerId());
				
			}

			for (GvgOccupyBean defenderItem : cityEntity.getDefenderList()) {
				if(!this.todayPlayerIds.contains(defenderItem.getPlayerId()))
					this.todayPlayerIds.add(defenderItem.getPlayerId());
			}

		}

	}

	@Override
	public boolean onTick() {

		if (!isOpenFunction) {
			return true;
		}

		// 循环帧
		long nowTime = GuaJiTime.getMillisecond();
		if (millisecond > nowTime) {
			return true;
		}

		millisecond = nowTime + 1000L;// 间隔一秒

		// 重置期内
		if (GvgService.getInstance().isResetDate()) {
			// 重置排行榜数据 并更新下次重置时间
			if (isNeedReset(nowTime) && !isHaveReset) {
				resetGvgData(nowTime);
				isHaveReset = true;
			}
		} else {

			// 功能未开启判定走逻辑
			if (!GvgService.getInstance().isOpeanFunction()) {
				return true;
			}
			isHaveReset = false;
			
			//从生成名单志可以宣战时间点未空闲状态
			if(this.getGvgStatus()==GVGStatus.GVG_STATUS_WAITING)
			{
				return true;
			}
			
			// 推送GVG阶段
			this.pushGvgState();
			// 战斗计算
			long todaySecond = nowTime - GuaJiTime.getAM0Date().getTime();
			if (todaySecond > GvgCfg.getInstance().getBattleStart()
					&& todaySecond < GvgCfg.getInstance().getBattleEnd()) {
				if (snapshotCache.size() <= 0) {
					// 添加快照数据
					Log.gvgLog("copy snapshot");
					this.copySnapshotCache();
				}
				this.gvgBattleLogic();
			}
			// 赛季结束会清除gvgCityTime
			// 跨天更新清理数据
			if (nowTime >= GvgService.getInstance().getRefreshTime()) {
				resetCityData();
			}

		}
		return true;
	}

	/**
	 * 重置gvg 城战数据
	 */
	public void resetGvgData(long nowTime) {

		if (AllianceManager.getInstance().getAllianceMap().size() < GvgCfg.getInstance().getAllianceSize())
			return;

		// 活动没开启不生成数据
		if (!GvgService.getInstance().isOpeanFunction()) {
			if (GvgService.getInstance().getTimeCity() != null) {
				GvgService.getInstance().getTimeCity().setInvalid(false);
			} else {
				GvgTimeEntity timeEntity = GvgTimeEntity.createEntity();
				GvgService.getInstance().setTimeEntity(timeEntity);
			}
		}

		// 从小到大排序
		List<Integer> resetDayList = GvgCfg.getInstance().getResetDayList();
		Collections.sort(resetDayList);
		long resetTime = GvgCfg.getInstance().getResetTime();
		long theLastResetOfMonth = GuaJiTime.getTheDayAM0Date(resetDayList.get(resetDayList.size() - 1)) + resetTime;
		long nextResetDate = 0;
		if (nowTime >= theLastResetOfMonth) {
			nextResetDate = GuaJiTime.getNextMonthDayAM0Date(resetDayList.get(0)) + resetTime;
		} else {
			for (Integer day : resetDayList) {
				if (day > GuaJiTime.getMonthDay()) {
					nextResetDate = GuaJiTime.getTheDayAM0Date(day) + resetTime;
					break;
				}
			}
		}
		GvgService.getInstance().updateAlliances();
		GvgService.getInstance().updateRank();
		GvgService.getInstance().updateCity(false);
		GvgService.getInstance().updateGvgTime(false);
		GvgService.getInstance().updateResetTime(nextResetDate);
		GvgService.getInstance().updatePushState(GVGStatus.GVG_STATUS_WAITING_VALUE);
		allocatingCityLogic();

	}

	/**
	 * 重置 宣战数据 及奖励数据
	 */
	private void resetCityData() {

		GvgService.getInstance().resetCity();
		GvgService.getInstance().updateGvgTime(true);
		GvgService.getInstance().updateCity(true);
	}

	/**
	 * 城池初始化分配
	 */
	private void allocatingCityLogic() {

		Collection<GvgCitiesCfg> configs = ConfigManager.getInstance().getConfigMap(GvgCitiesCfg.class).values();

		List<Integer> lowCityList = new ArrayList<Integer>();
		List<Integer> reviveCityList = new ArrayList<Integer>();

		
		

		for (GvgCitiesCfg gvgConfig : configs) {
			if (gvgConfig.getLevel() == 1) {
				lowCityList.add(gvgConfig.getCityId());
			}
			if(gvgConfig.getLevel()==0)
			{
				reviveCityList.add(gvgConfig.getCityId());
			}
		}
		
		// 随机分配城池
		Collections.shuffle(lowCityList);
		Collections.shuffle(reviveCityList);
		
		// 排名
		GvgCityEntity cityEntity = null;
		List<VitalityRankEntity> vitalitylistRank = GvgService.getInstance().getVitalityRanks();
	
		for (int i = 0; i < vitalitylistRank.size(); i++) {
			//一级城分配
			if(i<lowCityList.size())
			{
				// 城池配置数据
				GvgCitiesCfg config = ConfigManager.getInstance().getConfigByKey(GvgCitiesCfg.class, lowCityList.get(i));
				if (config == null) {
					continue;
				}

				cityEntity = GvgService.getInstance().getCityEntity(config.getCityId());
				if (cityEntity == null) {
					cityEntity = GvgCityEntity.createEntity(config.getCityId(), vitalitylistRank.get(i).getAllianceId(), 0);
					if (cityEntity != null)
						GvgService.getInstance().addCityEntity(cityEntity);
				} else {
					cityEntity.setHolderId(vitalitylistRank.get(i).getAllianceId());
					cityEntity.setMarauderId(0);
					cityEntity.notifyUpdate(false);
				}
			}else {//复活点分配
				// 城池配置数据
				GvgCitiesCfg config = ConfigManager.getInstance().getConfigByKey(GvgCitiesCfg.class, reviveCityList.get(i-lowCityList.size()));
				if (config == null) {
					continue;
				}
				cityEntity = GvgService.getInstance().getCityEntity(config.getCityId());

				if (cityEntity == null) {
					cityEntity = GvgCityEntity.createEntity(config.getCityId(), vitalitylistRank.get(i).getAllianceId(), 0);
					if (cityEntity != null)
						GvgService.getInstance().addCityEntity(cityEntity);
				} else {
					cityEntity.setHolderId(vitalitylistRank.get(i).getAllianceId());
					cityEntity.setMarauderId(0);
					cityEntity.notifyUpdate(false);
				}
				
			}
			
		}

	}

	/**
	 * @return
	 */
	public boolean isWaitingStatus(Calendar now) {

		// 筹备期
		long todaySecond = now.getTimeInMillis() - GuaJiTime.getAM0Date().getTime();
		// 进行阶段
		int dayOfMonth = now.get(Calendar.DAY_OF_MONTH);
		if (dayOfMonth == 1) {
			if (todaySecond < GvgCfg.getInstance().getDeclareStart())
				return true;
		} else {
			if (GvgCfg.getInstance().getResetDayList().size() > 0) {
				// 时间段配置
				if (dayOfMonth >= GvgCfg.getInstance().getResetDayList()
						.get(GvgCfg.getInstance().getResetDayList().size() - 1)) {
					int theLastResetDay = GvgCfg.getInstance().getResetDayList()
							.get(GvgCfg.getInstance().getResetDayList().size() - 1);
					Calendar resetCal = GuaJiTime.getCalendar();
					resetCal.set(Calendar.DAY_OF_MONTH, theLastResetDay);
					long theLastReseTime = GuaJiTime.getAM0Date(resetCal.getTime()).getTime()
							+ GvgCfg.getInstance().getResetTime();
					if (GuaJiTime.getCalendar().getTime().getTime() > theLastReseTime)
						return true;

				} else {
					int theNearResetDay = dayOfMonth;
					for (int index = 0; index < GvgCfg.getInstance().getResetDayList().size(); index++) {
						if (theNearResetDay >= GvgCfg.getInstance().getResetDayList().size()) {
							theNearResetDay = GvgCfg.getInstance().getResetDayList().get(index);
							break;
						}
					}

					Calendar resetCal = GuaJiTime.getCalendar();
					resetCal.set(Calendar.DAY_OF_MONTH, theNearResetDay);

					long beginTime = GuaJiTime.getAM0Date(resetCal.getTime()).getTime()
							+ GvgCfg.getInstance().getResetTime();
					resetCal.add(Calendar.DAY_OF_MONTH, 1);
					long endTime = GuaJiTime.getAM0Date(resetCal.getTime()).getTime()
							+ GvgCfg.getInstance().getDeclareStart();
					if (now.getTime().getTime() <= endTime && now.getTime().getTime() >= beginTime)
						return true;
				}

			}
		}

		return false;
	}

	/**
	 * 当前GVG阶段
	 * 
	 * @return
	 */
	public GVGStatus getGvgStatus() {

		// 生成名单后至次日8点半
		if (isWaitingStatus(GuaJiTime.getCalendar())) {
			return GVGStatus.GVG_STATUS_WAITING;
		}

		long todaySecond = GuaJiTime.getMillisecond() - GuaJiTime.getAM0Date().getTime();
		if (todaySecond > GvgCfg.getInstance().getDeclareStart()
				&& todaySecond < GvgCfg.getInstance().getDeclareEnd()) {
			return GVGStatus.GVG_STATUS_PREPARE;
		} else if (todaySecond > GvgCfg.getInstance().getBattleStart()
				&& todaySecond < GvgCfg.getInstance().getBattleEnd()) {
			return GVGStatus.GVG_STATUS_FIGHTING;
		} else if (todaySecond >= GvgCfg.getInstance().getBattleEnd()// fight 结束后1分钟内发奖励
				&& todaySecond < (GvgCfg.getInstance().getBattleEnd() + 60000)) {
			return GVGStatus.GVG_STATUS_AWARD;
		} else {
			return GVGStatus.GVG_STATUS_ENDING;
		}
	}

	/**
	 * 获取城池状态
	 * 
	 * @param cityEntity
	 * @param gvgStatus
	 * @return
	 */
	public CityStatus getCityStatus(GvgCityEntity cityEntity, GVGStatus gvgStatus) {
		boolean isStatus = false;
		if (gvgStatus == GVGStatus.GVG_STATUS_PREPARE) {
			// 是否被宣战
			isStatus = cityEntity.getMarauderId() > 0;
			return isStatus ? CityStatus.CITY_STATUS_DECLARED : CityStatus.CITY_STATUS_NORMAL;
		} else if (gvgStatus == GVGStatus.GVG_STATUS_FIGHTING) {
			// 战斗期间
			if (cityEntity.getMarauderId() == 0) {
				return CityStatus.CITY_STATUS_FORBIDDEN;
			}
			// 城池反攻切换
			if (cityEntity.getFightbackTime() > 0) {
				if (cityEntity.isFightback()) {
					isStatus = cityEntity.getFightbackTime() <= GuaJiTime.getMillisecond();
					return isStatus ? CityStatus.CITY_STATUS_FIGHTING : CityStatus.CITY_STATUS_DECLARED;
				}
				if (cityEntity.getFightbackTime() > GuaJiTime.getMillisecond()) {
					return CityStatus.CITY_STATUS_REATTACK;
				}
				return CityStatus.CITY_STATUS_FORBIDDEN;
			}
			return CityStatus.CITY_STATUS_FIGHTING;
		}
		// 闲置期间
		isStatus = cityEntity.getMarauderId() > 0;
		return isStatus ? CityStatus.CITY_STATUS_DECLARED : CityStatus.CITY_STATUS_FORBIDDEN;
	}

	/**
	 * 宣战权限判定
	 * 
	 * @param allianceEntity
	 * @param playerId
	 * @param isFightback
	 * @return
	 */
	public int declareBattlePower(PlayerAllianceEntity allianceEntity, int playerId, boolean isFightback) {
		// 公会成员
		if (allianceEntity.getAllianceId() == 0) {
			return Status.error.ALLIANCE_NO_JOIN_VALUE;
		}
		// 公会权限
		if (allianceEntity.getPostion() != Alliance.ALLIANCE_POS_MAIN
				&& allianceEntity.getPostion() != Alliance.ALLIANCE_POS_COPYMAIN) {
			return Status.error.ALLIANCE_NO_MAIN_VALUE;
		}
		// 反攻阶段
		if (isFightback) {
			return 0;
		}
		// 是否有宣战权限
		List<VitalityRankEntity> rankEntities = GvgService.getInstance().getVitalityRanks();
		boolean isHasPower = false;
		int index = 1;
		for (VitalityRankEntity rankEntity : rankEntities) {
			if (allianceEntity.getAllianceId() != rankEntity.getAllianceId()) {
				index++;
				continue;
			}
			if (index <= GvgCfg.getInstance().getDeclarePower()) {
				isHasPower = true;
			}
			break;
		}
		// 再加入复活的判断

		if (!isHasPower) {
			return Status.error.NOT_DECLARE_PWOER_VALUE;
		}
		return 0;
	}

	/**
	 * 宣战可用次数判定
	 *
	 * @param alliance
	 * @return
	 */
	public int declareBattleTimes(GvgAllianceEntity alliance) {
		// 宣战次数判定
		if (alliance.getDeclareTimes() >= GvgCfg.getInstance().getDeclareTimes()) {
			return Status.error.NOT_DECLARE_TIMES_VALUE;
		}
		return 0;
	}

	/**
	 * 宣战城池判定
	 * 
	 * @param allianceId 宣战者公会ID
	 * @param cityId
	 * @return
	 */
	public int declareBattleCity(int allianceId, int cityId) {
		// 城池配置数据
		GvgCitiesCfg config = ConfigManager.getInstance().getConfigByKey(GvgCitiesCfg.class, cityId);
		if (config == null) {
			return Status.error.PARAMS_INVALID_VALUE;
		}
		// 当前城池是否被宣战
		GvgCityEntity cityEntity = GvgService.getInstance().getCityEntity(cityId);
		if (cityEntity == null) {
			cityEntity = GvgCityEntity.createEntity(cityId, 0, 0);
			GvgService.getInstance().addCityEntity(cityEntity);
		}
		if (cityEntity.getMarauderId() > 0) {
			return Status.error.DECLARED_WAR_VALUE;
		}
		if (allianceId == cityEntity.getHolderId()) {
			return Status.error.CANNOT_DECLARED_WAR_VALUE;
		}

		/*
		 * // 是否已有城池 boolean isHaveCity = false; // 攻打城池条件判定 UnlockLimit unlockLimit =
		 * config.getUnlockLimit(); TreeMap<Integer, GvgCityEntity> cities =
		 * GvgService.getInstance().getCities(); int cityCount = 0; for (GvgCityEntity
		 * chainCity : cities.values()) { if (chainCity.getHolderId() != allianceId) {
		 * continue; } // 已拥有城池 isHaveCity = true; // 特殊城池宣战处理 if (chainCity.getCityId()
		 * == GvgCfg.getInstance().getCityId() && config.getLevel() ==
		 * GvgCfg.getInstance().getCityLevel()) {
		 * cityEntity.updateMarauderId(allianceId); return 0; } // 符合条件的城池统计 if
		 * (unlockLimit != null) { GvgCitiesCfg cityConfig =
		 * ConfigManager.getInstance().getConfigByKey(GvgCitiesCfg.class,
		 * chainCity.getCityId()); if (cityConfig != null && cityConfig.getLevel() ==
		 * unlockLimit.getCityLevel()) { cityCount += 1; } } else { break; } } if
		 * (unlockLimit != null && cityCount < unlockLimit.getCount()) { return
		 * Status.error.NOT_ENOUGH_COUNT_CITY_VALUE; }
		 */

		// 城池连通判定
		List<Integer> chainIds = config.getChainIdList();
		for (int chainId : chainIds) {
			GvgCityEntity chainCity = GvgService.getInstance().getCityEntity(chainId);
			if (chainCity == null) {
				continue;
			}
			if (chainCity.getHolderId() == allianceId) {
				// 更新数据
				cityEntity.updateMarauderId(allianceId);
				return 0;
			}
		}
		return Status.error.NOT_CHAIN_CITY_VALUE;
	}

	/**
	 * @param allianceId 公会编号
	 * @param playerID   游戏玩家编号
	 * @param psoId      复活点
	 * @param consume    复活开销
	 * @return
	 */
	public int obtainRevivePos(int allianceId, int playerID, int cityId, int consume) {

		// 城池配置数据
		GvgCitiesCfg config = ConfigManager.getInstance().getConfigByKey(GvgCitiesCfg.class, cityId);
		if (config == null) {
			return Status.error.PARAMS_INVALID_VALUE;
		}

		// 当前复活点是否被占用
		GvgCityEntity cityEntity = GvgService.getInstance().getCityEntity(cityId);
		if (cityEntity == null) {
			cityEntity = GvgCityEntity.createEntity(cityId, 0, 0);
			GvgService.getInstance().addCityEntity(cityEntity);
		}

		if (allianceId == cityEntity.getHolderId()) {
			return Status.error.ALLIANCE_HAVE_OCCUPY_REVIVEPOINT_VALUE;
		}

		long todaySecond = GuaJiTime.getMillisecond() - GuaJiTime.getAM0Date().getTime();

		// 宣战期至复活期内是不能宣战的
		if (todaySecond < GvgCfg.getInstance().getReviveStart()
				&& todaySecond > GvgCfg.getInstance().getDeclareStart()) {
			return Status.error.ALLIANCE_NO_TIME_BUYREVIVE_VALUE;
		}

		// 是否有宣战权限
		List<VitalityRankEntity> rankEntities = GvgService.getInstance().getVitalityRanks();
		boolean isHasPower = false;
		int index = 1;
		for (VitalityRankEntity rankEntity : rankEntities) {
			if (allianceId != rankEntity.getAllianceId()) {
				index++;
				continue;
			}
			if (index <= GvgCfg.getInstance().getDeclarePower()) {
				isHasPower = true;
			}
			break;
		}

		boolean isHave = GvgManager.getInstance().isHaveCity(allianceId);
		// 丢失所有城池 且 有宣战权 才可复活
		if (isHave || !isHasPower) {
			return Status.error.ALLIANCE_HAVE_OCCUPY_REVIVEPOINT_VALUE;
		}

		cityEntity.setHolderId(allianceId);
		cityEntity.notifyUpdate();

		return 0;
	}

	/**
	 * 推送GVG阶段
	 */
	private void pushGvgState() {
		GVGStatus status = this.getGvgStatus();
		boolean isPush = false;
		int gvgState = GvgService.getInstance().getPushState();
		// 状态改变推送判断
		if (status == GVGStatus.GVG_STATUS_PREPARE && gvgState != GVGStatus.GVG_STATUS_PREPARE_VALUE) {
			GvgService.getInstance().updatePushState(GVGStatus.GVG_STATUS_PREPARE_VALUE);
			logger.info("gvgstate change from %d to %d", gvgState, GVGStatus.GVG_STATUS_PREPARE_VALUE);
			isPush = true;
		} else if (status == GVGStatus.GVG_STATUS_FIGHTING && gvgState != GVGStatus.GVG_STATUS_FIGHTING_VALUE) {
			GvgService.getInstance().updatePushState(GVGStatus.GVG_STATUS_FIGHTING_VALUE);
			logger.info("gvgstate change from %d to %d", gvgState, GVGStatus.GVG_STATUS_FIGHTING_VALUE);
			isPush = true;
		} else if (status == GVGStatus.GVG_STATUS_AWARD && gvgState != GVGStatus.GVG_STATUS_AWARD_VALUE) {

			logger.info("gvgstate change from %d to %d", gvgState, GVGStatus.GVG_STATUS_AWARD_VALUE);
			// 执行一次发送奖励
			GvgService.getInstance().updatePushState(GVGStatus.GVG_STATUS_AWARD_VALUE);
			try {
				ciytSendRewards();
			} catch (Exception e) {
				logger.error("GVG Bad Award", e);
			}

			// 重置复活点数据
			GvgService.getInstance().resetReviveCity();
			logger.info("GvgManager  resetReviveCity");
			this.gvgLogsAndUpdateCity();
			// 更新timeCity状态为为未开启
			int dayOfMonth = GuaJiTime.getMonthDay() + 1;
			if (GvgCfg.getInstance().getResetDayList().contains(dayOfMonth)) {
				// 先生成上赛季排名 清除之前信息
				GvgService.getInstance().SeasonRankingCreate();

				logger.info("GvgManager  SeasonRankingCreate");
				GvgService.getInstance().clearCityData();
				logger.info("GvgManager  clearCityData");

			}

			logger.info("GvgManager  clear All Cache");
			// 清理缓存数据
			snapshotCache.clear();
			battleCache.clear();
			battleTimeCache.clear();
			defendersNpc.clear();
			battleLogsCache.clear();
			playerIds.clear();
			killEnemyCache.clear();
			playerLimitIds.clear();
			playerDefenderLimitIds.clear();
			isPush = true;

		} else if (status == GVGStatus.GVG_STATUS_ENDING && gvgState != GVGStatus.GVG_STATUS_ENDING_VALUE) {
			logger.info("gvgstate change from %d to %d", gvgState, GVGStatus.GVG_STATUS_ENDING_VALUE);
			GvgService.getInstance().updatePushState(GVGStatus.GVG_STATUS_ENDING_VALUE);
			isPush = true;

		}
		// 推送状态数据
		if (isPush) {
			Log.gvgLog(String.format("push state:%d", status.getNumber()));
			Msg message = Msg.valueOf(GsConst.MsgType.GVG_STATE);
			GsApp.getInstance().broadcastMsg(message, GsApp.getInstance().getObjMan(GsConst.ObjType.PLAYER));
		}
	}

	/**
	 * 缓存快照数据
	 */
	private void copySnapshotCache() {
		// 提取城池数据
		synchronized (snapshotCache) {
			TreeMap<Integer, GvgCityEntity> cityEntities = GvgService.getInstance().getCities();
			// 提取克隆账号快照
			Set<Integer> memberIds = new HashSet<Integer>();
			for (GvgCityEntity cityEntity : cityEntities.values()) {
				boolean isCheckout = false;
				// 攻击者公会
				AllianceEntity _marauderAlliance = AllianceManager.getInstance()
						.getAlliance(cityEntity.getMarauderId());
				if (_marauderAlliance != null) {
					memberIds.addAll(_marauderAlliance.getMemberList());
					// 退出公会检测
					List<GvgOccupyBean> attackers = cityEntity.getAttackerList();
					if (attackers != null && attackers.size() > 0) {
						Iterator<GvgOccupyBean> iterator = attackers.iterator();
						while (iterator.hasNext()) {
							GvgOccupyBean occupyBean = iterator.next();
							if (!memberIds.contains(occupyBean.getPlayerId())) {
								iterator.remove();
								isCheckout = true;
							}
						}
					}
				}
				// 持有者公会
				AllianceEntity _holderAlliance = AllianceManager.getInstance().getAlliance(cityEntity.getHolderId());
				if (_holderAlliance != null) {
					memberIds.addAll(_holderAlliance.getMemberList());
					// 退出公会检测
					List<GvgOccupyBean> defenders = cityEntity.getDefenderList();
					if (defenders != null && defenders.size() > 0) {
						Iterator<GvgOccupyBean> iterator = defenders.iterator();
						while (iterator.hasNext()) {
							GvgOccupyBean occupyBean = iterator.next();
							if (!memberIds.contains(occupyBean.getPlayerId())) {
								iterator.remove();
								isCheckout = true;
							}
						}
					}
				}
				// 更新数据
				if (isCheckout) {
					cityEntity.notifyUpdate();
				}
			}
			// 添加镜像数据
			for (Integer memberId : memberIds) {
				this.addSnapshot(memberId);
			}
		}
	}

	/**
	 * 获取快照数据
	 */
	public boolean isNullSnapshpt(int playerId) {
		if (snapshotCache.size() <= 0) {
			return false;
		}
		PlayerSnapshotInfo.Builder snapshot = snapshotCache.get(playerId);
		if (snapshot == null) {
			return true;
		}
		return false;
	}

	/**
	 * 添加快照数据
	 * 
	 * @param playerId
	 */
	private void addSnapshot(int playerId) {
		PlayerSnapshotInfo.Builder snapshot = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
		if (snapshot == null) {
			return;
		}
		if (snapshotCache.containsKey(playerId)) {
			return;
		}
		snapshotCache.put(playerId, snapshot.clone());
		Log.gvgLog(String.format("add snapshot playerId: %d", playerId));
	}

	/**
	 * 是否有快照缓存数据
	 * 
	 * @return
	 */
	public boolean isHaveSnapshot() {
		return snapshotCache.size() > 0;
	}

	/**
	 * 获取备份的镜像数据
	 * 
	 * @param playerId
	 * @return
	 */
	public PlayerSnapshotInfo.Builder getSnapshot(int playerId) {
		if (snapshotCache.containsKey(playerId)) {
			return snapshotCache.get(playerId);
		}
		return null;
	}

	/**
	 * 佣兵是否能参战
	 * 
	 * @param playerId
	 * @param roleId
	 * @return
	 */
	public boolean isCanJoinOfRole(int playerId, int roleId) {
		PlayerSnapshotInfo.Builder snapshotData = snapshotCache.get(playerId);
		// 镜像数据中是否有当前佣兵
		if (snapshotData != null) {
			List<RoleInfo.Builder> mercenaryList = snapshotData.getMercenaryInfoBuilderList();
			if (mercenaryList == null || mercenaryList.size() <= 0) {
				return false;
			}
			for (RoleInfo.Builder roleInfo : mercenaryList) {
				if (roleInfo.getRoleId() != roleId) {
					continue;
				}
//				if (roleInfo.getHide()) {
//					return false;
//				}
				return true;
			}
			return false;
		}
		if (snapshotCache.size() > 0) {
			return false;
		}
		return true;
	}

	/**
	 * 是否需要重置城战数据
	 * 
	 * @return true 需要重置 false 不需要重置
	 */
	public boolean isNeedReset(long nowTime) {
		Calendar nowCal = Calendar.getInstance();
		nowCal.setTimeInMillis(nowTime);

		if (GvgService.getInstance().getTimeCity() != null) {
			if (nowTime > GvgService.getInstance().getResetTime()) {
				return true;
			}
		} else {

			int dayOfMonth = nowCal.get(Calendar.DAY_OF_MONTH);
			long resetTime = GuaJiTime.getAM0Date().getTime() + GvgCfg.getInstance().getResetTime();
			if (GvgCfg.getInstance().getResetDayList().contains(dayOfMonth) && nowTime >= resetTime) {
				return true;
			}

		}

		return false;
	}

	/**
	 * 获取城池战斗日志
	 * 
	 * @param cityId
	 * @return
	 */
	public List<CityBattleInfo> getCityBattleLog(int cityId) {
		return battleLogsCache.get(cityId);
	}

	/**
	 * 获取城池战报
	 * 
	 * @param cityId
	 * @return
	 */
	public Battlefield getCityBattle(int cityId) {
		return battleCache.get(cityId);
	}

	/**
	 * 获取战斗结束时间
	 * 
	 * @param cityId
	 * @return
	 */
	public long getBattleTime(int cityId) {
		if (battleTimeCache.containsKey(cityId)) {
			return battleTimeCache.get(cityId);
		}
		return 0;
	}

	/**
	 * 战斗逻辑处理
	 */
	private void gvgBattleLogic() {
		// 提取城池数据
		final TreeMap<Integer, GvgCityEntity> cities = GvgService.getInstance().getCities();
		long battleTime = 0L;
		synchronized (cities) {
			for (GvgCityEntity cityEntity : cities.values()) {
				int cityId = cityEntity.getCityId();
				// 攻击防御NPC只需要胜利一次
				if (defendersNpc.containsKey(cityId) && defendersNpc.get(cityId) > 0) {
					continue;
				}
				// 战斗时间校验
				if (battleTimeCache.containsKey(cityId)) {
					battleTime = battleTimeCache.get(cityId);
					if (battleTime > GuaJiTime.getMillisecond()) {
						continue;
					}
				}
				// 战斗单元初始化
				List<BattleRole> attackers = new LinkedList<BattleRole>();
				List<BattleRole> defenders = new LinkedList<BattleRole>();
				// 防御者和攻击者数据
				int attackerId = this.getAttackers(attackers, cityEntity);
				if (attackers.size() == 0) {
					// 没反攻切换城池持有者
					if (cityEntity.getFightbackTime() > 0L
							&& cityEntity.getFightbackTime() <= GuaJiTime.getMillisecond()
							&& !cityEntity.isFightback()) {
						Log.gvgLog("attacker size: 0 --- changeHolder");
						this.changeHolder(cityEntity, false, 0);
					}
					continue;
				}
				int defenderId = this.getDefenders(defenders, cityEntity);
				if (defenders.size() == 0) {
					if (cityEntity.getMarauderId() == 0) {
						battleTimeCache.put(cityId, GvgCfg.getInstance().getBattleEnd() + 1000);
						continue;
					}
					if (cityEntity.getFightbackTime() <= 0L) {
						// 反攻保护
						Log.gvgLog("defender size: 0 --- fightBackProtected");
						this.fightBackProtected(cityEntity, 1);
					} else if (cityEntity.getFightbackTime() < GuaJiTime.getMillisecond()
							&& cityEntity.getAttackerList().size() > 0) {
						// 城池持有权是否切换
						Log.gvgLog("defender size: 0 --- changeHolder");
						this.changeHolder(cityEntity, true, 1);
					}
					continue;
				}
				Log.gvgLog(String.format("battle start cityId: %d", cityId));
				// 战斗计算
				Battlefield battlefield = new Battlefield();
				battlefield.fighting(battleType.BATTLE_GVG_CITY_VALUE, attackers, defenders, null);
				Log.gvgLog(String.format("battle result %d", battlefield.getBattleResult()));

				// 添加缓存数据
				battleCache.put(cityId, battlefield);
				// 清理战斗队伍数据
				int killEnemies = 0;
				if (battlefield.getBattleResult() <= 0) {
					// 攻击者失败
					cityEntity.removeAttacker();
					// 播报
					if (cityEntity.getAttackerList().size() <= 0) {
						AllianceEntity holderAlliance = AllianceManager.getInstance()
								.getAlliance(cityEntity.getHolderId());
						AllianceEntity marauderAlliance = AllianceManager.getInstance()
								.getAlliance(cityEntity.getMarauderId());
						if (marauderAlliance != null && holderAlliance != null) {
							for (Integer memberId : marauderAlliance.getMemberList()) {
								Player _player = PlayerUtil.queryPlayer(memberId);
								if (_player == null || !_player.isOnline()) {
									continue;
								}
								String message = ChatManager.getMsgJson(GvgCfg.getInstance().getZeroAttacker(),
										holderAlliance.getName(), cityEntity.getCityId());
								ChatManager.getInstance().postChat(_player, message,
										Const.chatType.CHAT_ALLIANCE_SYSTEM_VALUE, 1);
								break;
							}
							for (Integer memberId : holderAlliance.getMemberList()) {
								Player _player = PlayerUtil.queryPlayer(memberId);
								if (_player == null || !_player.isOnline()) {
									continue;
								}
								String message = ChatManager.getMsgJson(GvgCfg.getInstance().getZeroDefender(),
										marauderAlliance.getName(), cityEntity.getCityId());
								ChatManager.getInstance().postChat(_player, message,
										Const.chatType.CHAT_ALLIANCE_SYSTEM_VALUE, 1);
								break;
							}
						}
					}
					// 记录攻击者杀敌个数
					killEnemies = this.addkillEnemy(cityId, defenderId, attackerId, attackers.size());
					Log.gvgLog(String.format("surplus attacker: %s", cityEntity.getAttackerIds()));
				} else {
					if (defendersNpc.containsKey(cityId)) {
						defendersNpc.put(cityId, 1);
					} else {
						// 防御者失败
						cityEntity.removeDefender();
					}
					// 记录攻击者杀敌个数
					killEnemies = this.addkillEnemy(cityId, attackerId, defenderId, defenders.size());
					Log.gvgLog(String.format("surplus defender: %s", cityEntity.getDefenderIds()));
				}
				// 推送战报数据
				String attackerName = this.getPlayerName(attackerId);
				String defenderName = this.getPlayerName(defenderId);
				this.pushCityBattle(cityEntity, battlefield.getBattleInfo(), battlefield.getBattleResult(),
						attackerName, defenderName);
				// 下场战斗时间计算
				battleTime = battlefield.getBattleInfo().getBattleTime() * 1000L;
				if (battleTime > GvgCfg.getInstance().getBattleTime()) {
					battleTime = GvgCfg.getInstance().getBattleTime();
				}
				battleTime += GuaJiTime.getMillisecond();
				battleTimeCache.put(cityId, battleTime);
				// 添加反攻清理玩家缓存数据
				Set<Integer> ids = playerIds.get(cityId);
				if (ids == null) {
					ids = new HashSet<Integer>();
					this.playerIds.put(cityId, ids);
				}
				ids.add(defenderId);
				ids.add(attackerId);
				// 构建城池战斗日志
				this.builderBattleLog(cityId, attackerName, defenderName, battlefield.getBattleResult(), killEnemies);
				// 战后攻击者胜利处理
				if (battlefield.getBattleResult() > 0) {
					// 城池战斗结束
					if (cityEntity.getDefenderList().size() <= 0) {
						if (this.isBattleEnd(cityEntity)) {
							// 城池持有权是否切换
							this.changeHolder(cityEntity, true, 1);
						} else {
							// 反攻保护
							this.fightBackProtected(cityEntity, 1);
						}
					}
				}
				// 推送攻防队伍数量
				this.pushTeamNumber(cityEntity);
				Log.gvgLog(
						String.format("city %d battle end next battle time: %d", cityId, battleTimeCache.get(cityId)));
			}
		}
	}

	// 提取公会数据
	/*
	
	
	*/

	/**
	 * @Title: gvgAttackCityNotice @Description:广播 @param @param marauderId
	 *         宣战者公会Id @param @param holderId 收成公会Id @param @param cityId
	 *         城池id @return void 返回类型 @throws
	 */
	public void gvgAttackCityNotice(int marauderId, int holderId, int cityId) {

		try {
			AllianceEntity marauderAlliance = AllianceManager.getInstance().getAlliance(marauderId);
			if (marauderAlliance == null) {
				return;
			}
			AllianceEntity holderAlliance = AllianceManager.getInstance().getAlliance(holderId);
			if (holderAlliance == null) {
				return;
			}

			String message = ChatManager.getMsgJson(GvgCfg.getInstance().getAttackCityNotice(),
					marauderAlliance.getName(), holderAlliance.getName(), cityId);

			GsApp.getInstance().broadcastChatWorldMsg(null, message);
		} catch (Exception e) {
			MyException.catchException(e);
		}

	}

	/**
	 * 提取防御者数据
	 * 
	 * @param defenders
	 * @param cityEntity
	 * @return
	 */
	private int getDefenders(List<BattleRole> defenders, GvgCityEntity cityEntity) {
//		Battlefield oldBattlefield = battleCache.get(cityEntity.getCityId());
//		boolean battleResult = false;
//		if (oldBattlefield != null) {
//			if (oldBattlefield.getBattleResult() <= 0) {
//				for (BattleRole battleRole : oldBattlefield.getDefenders()) {
//					battleRole.clearSkillCds();
//					battleRole.removeAllBuff();
//					battleRole.clearSpecialFlagMap();
//					defenders.add(battleRole);
//				}
//				battleResult = true;
//			}
//		}
//		// 默认防御者NPC
//		if (defenders.size() == 0 && cityEntity.getHolderId() == 0) {
//			GvgCitiesCfg cityConfig = ConfigManager.getInstance().getConfigByKey(GvgCitiesCfg.class,
//					cityEntity.getCityId());
//			if (cityConfig == null) {
//				return 0;
//			}
//			// 默认NPC防御者
//			for (int monsterId : cityConfig.getMonsterIdList()) {
//				RoleInfo.Builder monsterRole = BuilderUtil.genMonsterRoleInfoBuilder(monsterId, false);
//				if (monsterRole == null) {
//					continue;
//				}
//				defenders.add(new BattleRole(monsterRole));
//			}
//			cityEntity.getDefenderList().clear();
//			defendersNpc.put(cityEntity.getCityId(), 0);
//		}
//		// 提取玩家克隆数据
//		if (cityEntity.getDefenderList().size() <= 0) {
//			return 0;
//		}
//		GvgOccupyBean occupyBean = cityEntity.getDefenderList().get(0);
//		if (occupyBean == null) {
//			return 0;
//		}
//		if (defenders.size() > 0) {
//			// DEBUFF更新
//			if (battleResult) {
//				this.updateDebuff(occupyBean.getPlayerId(), cityEntity.getCityId(), defenders);
//			}
//			return occupyBean.getPlayerId();
//		}
//		// 获取快照数据
//		PlayerSnapshotInfo.Builder snapshot = snapshotCache.get(occupyBean.getPlayerId());
//		if (snapshot == null) {
//			// 玩家不在该公会
//			Log.gvgLog("防御者不在公会 ： " + occupyBean.getPlayerId());
//			return 0;
//		}
//		// 战斗单元添加
//		List<RoleInfo.Builder> mercenaryList = snapshot.getMercenaryInfoBuilderList();
//		if (mercenaryList != null && mercenaryList.size() > 0) {
//			for (int roleId : occupyBean.getRoleIds()) {
//				for (RoleInfo.Builder roleInfo : mercenaryList) {
//					if (roleInfo.getRoleId() == roleId) {
//						defenders.add(new BattleRole(snapshot.getPlayerId(), roleInfo));
//						break;
//					}
//				}
//			}
//			// DEBUFF更新
//			if (battleResult) {
//				this.updateDebuff(occupyBean.getPlayerId(), cityEntity.getCityId(), defenders);
//			}
//		}
//		return occupyBean.getPlayerId();
		return 0;
	}

	/**
	 * 提取攻击者数据
	 * 
	 * @param attackers
	 * @param cityEntity
	 * @return
	 */
	private int getAttackers(List<BattleRole> attackers, GvgCityEntity cityEntity) {
//		Battlefield oldBattlefield = battleCache.get(cityEntity.getCityId());
//		boolean battleResult = false;
//		if (oldBattlefield != null) {
//			if (oldBattlefield.getBattleResult() > 0) {
//				for (BattleRole battleRole : oldBattlefield.getAttackers()) {
//					battleRole.clearSkillCds();
//					battleRole.removeAllBuff();
//					battleRole.clearSpecialFlagMap();
//					attackers.add(battleRole);
//				}
//				battleResult = true;
//			}
//		}
//		// 提取玩家克隆数据
//		if (cityEntity.getAttackerList().size() <= 0) {
//			return 0;
//		}
//		GvgOccupyBean occupyBean = cityEntity.getAttackerList().get(0);
//		if (occupyBean == null) {
//			return 0;
//		}
//		PlayerSnapshotInfo.Builder snapshot = snapshotCache.get(occupyBean.getPlayerId());
//		if (snapshot == null) {
//			// 玩家不再该公会
//			Log.gvgLog("攻击者不在公会： " + occupyBean.getPlayerId());
//			return 0;
//		}
//		// 公会的防御者
//		if (attackers.size() == 0) {
//			// 战斗单元添加
//			List<RoleInfo.Builder> mercenaryList = snapshot.getMercenaryInfoBuilderList();
//			if (mercenaryList != null && mercenaryList.size() > 0) {
//				for (int roleId : occupyBean.getRoleIds()) {
//					for (RoleInfo.Builder roleInfo : mercenaryList) {
//						if (roleInfo.getRoleId() == roleId) {
//							attackers.add(new BattleRole(snapshot.getPlayerId(), roleInfo));
//							break;
//						}
//					}
//				}
//			}
//		}
//		// DEBUFF更新
//		if (battleResult) {
//			this.updateDebuff(occupyBean.getPlayerId(), cityEntity.getCityId(), attackers);
//		}
//		return occupyBean.getPlayerId();
		return 0;
	}

	/**
	 * 记录攻击者杀敌个数
	 * 
	 * @param cityId
	 * @param winId
	 * @param failId
	 * @param killNumber
	 * @return
	 */
	private int addkillEnemy(int cityId, int winId, int failId, int killNumber) {
		int value = killNumber;
		if (winId > 0) {
			List<GvgKillEnemy> killEnemise = null;
			if (killEnemyCache.containsKey(winId)) {
				killEnemise = killEnemyCache.get(winId);
			} else {
				killEnemise = new ArrayList<GvgKillEnemy>();
				killEnemyCache.put(winId, killEnemise);
			}
			GvgKillEnemy killEnemy = null;
			for (GvgKillEnemy _killEnemy : killEnemise) {
				if (cityId == _killEnemy.getCityId()) {
					killEnemy = _killEnemy;
					break;
				}
			}
			if (killEnemy == null) {
				killEnemy = new GvgKillEnemy(cityId, killNumber);
				killEnemise.add(killEnemy);
			} else {
				value += killEnemy.getKillNumber();
				killEnemy.setKillNumber(value);
			}
			Log.gvgLog(String.format("add player %d city %d kill enemy total %d", winId, cityId,
					killEnemy.getKillNumber()));
		}
		// 移除失败者连杀数据
		if (failId > 0 && killEnemyCache.containsKey(failId)) {
			List<GvgKillEnemy> killEnemise = killEnemyCache.get(failId);
			if (killEnemise == null || killEnemise.size() <= 0) {
				return value;
			}
			for (GvgKillEnemy killEnemy : killEnemise) {
				if (cityId == killEnemy.getCityId()) {
					killEnemise.remove(killEnemy);
					break;
				}
			}
		}
		return value;
	}

	/**
	 * 获取账号名称
	 * 
	 * @param playerId
	 * @return
	 */
	public String getPlayerName(int playerId) {
		PlayerSnapshotInfo.Builder snapshot = snapshotCache.get(playerId);
		if (snapshot == null) {
			return null;
		}
		RoleInfo mainRole = snapshot.getMainRoleInfo();
		return mainRole.getName();
	}

	public boolean isHaveCity(int allianceId) {

		TreeMap<Integer, GvgCityEntity> cities = GvgService.getInstance().getCities();
		for (GvgCityEntity cityEntity : cities.values()) {
			if (allianceId == cityEntity.getHolderId()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 战斗结束判断
	 * 
	 * @param cityEntity
	 */
	private boolean isBattleEnd(GvgCityEntity cityEntity) {
		// 防御NPC不能反攻
		if (defendersNpc.containsKey(cityEntity.getCityId())) {
			return true;
		}
		// 反攻判断
		if (cityEntity.getFightbackTime() == 0L) {
			return false;
		}
		// 整场战斗结束
		return true;
	}

	/**
	 * 城池切换到反攻保护
	 *
	 * @param cityEntity
	 * @param battleResult
	 */
	private void fightBackProtected(GvgCityEntity cityEntity, int battleResult) {
		if (cityEntity.getFightbackTime() == 0L) {
			// 返回体力
			this.returnPower(cityEntity);
			// 添加日志记录
			this.addLogAndPush(cityEntity, true, battleResult);
			// 反攻战斗计算时间点
			long fightBackTime = GuaJiTime.getMillisecond() + GvgCfg.getInstance().getFightbackTime();
			// 更新下次战斗计算时间
			battleTimeCache.put(cityEntity.getCityId(), fightBackTime);
			// 更新数据
			cityEntity.setFightbackTime(fightBackTime);
			int oldHolderId = cityEntity.getHolderId();
			cityEntity.setHolderId(cityEntity.getMarauderId());
			cityEntity.setMarauderId(oldHolderId);
			cityEntity.clearAttacker();
			cityEntity.clearDefender();
			// 清理战斗数据
			battleCache.remove(cityEntity.getCityId());
			battleLogsCache.remove(cityEntity.getCityId());
			// 清理战斗连杀数据
			Set<Integer> ids = playerIds.get(cityEntity.getCityId());
			if (ids != null && ids.size() > 0) {
				for (int playerId : ids) {
					List<GvgKillEnemy> killEnemies = killEnemyCache.get(playerId);
					if (killEnemies == null || killEnemies.size() <= 0) {
						continue;
					}
					for (GvgKillEnemy killEnemy : killEnemies) {
						if (killEnemy.getCityId() == cityEntity.getCityId()) {
							killEnemies.remove(killEnemy);
							Log.gvgLog(String.format("remove player %d city %d kill enemy %d cache", playerId,
									cityEntity.getCityId(), killEnemy.getKillNumber()));
							break;
						}
					}
				}
				playerIds.remove(cityEntity.getCityId());
			}
			Log.gvgLog(String.format("0、update city %d holderId %d marauderId %d", cityEntity.getCityId(),
					cityEntity.getHolderId(), cityEntity.getMarauderId()));
		}
	}

	/**
	 * 战斗结束切换城池持有者
	 *
	 * @param cityEntity
	 * @param isChange
	 * @param battleResult
	 */
	private void changeHolder(GvgCityEntity cityEntity, boolean isChange, int battleResult) {
		if (cityEntity.getMarauderId() > 0) {
			Log.gvgLog(String.format("1、update city %d holderId %d marauderId %d isChange %b", cityEntity.getCityId(),
					cityEntity.getHolderId(), cityEntity.getMarauderId(), isChange));
			int holderId = cityEntity.getHolderId();
			int marauderId = cityEntity.getMarauderId();
			// 添加日志记录
			this.addLogAndPush(cityEntity, false, battleResult);
			// 返回体力
			this.returnPower(cityEntity);
			// 更新数据
			if (isChange) {
				holderId = cityEntity.getMarauderId();
				marauderId = cityEntity.getHolderId();
				cityEntity.setHolderId(cityEntity.getMarauderId());
			}
			cityEntity.clearAttacker();
			cityEntity.setMarauderId(0);
			cityEntity.setFightback(false);
			cityEntity.setFightbackTime(0);
			cityEntity.notifyUpdate();
			// 获取聊天框
			if (cityEntity.getCityId() == GvgCfg.getInstance().getCityId()) {
				this.changeChatSkin(holderId, marauderId);
			}
			// 清理战斗数据
			battleCache.remove(cityEntity.getCityId());
			battleLogsCache.remove(cityEntity.getCityId());
		}
		battleTimeCache.put(cityEntity.getCityId(), GvgCfg.getInstance().getBattleEnd() + 1000);
	}

	/**
	 * 城池被攻打下来后添加日志、推送数据
	 * 
	 * @param cityEntity
	 * @param isSendMail
	 * @param battleResult
	 */
	private void addLogAndPush(GvgCityEntity cityEntity, boolean isSendMail, int battleResult) {
		// 添加日志记录
		AllianceEntity marauderAlliance = AllianceManager.getInstance().getAlliance(cityEntity.getMarauderId());
		if (marauderAlliance != null) {
			String attackerName = marauderAlliance.getName();
			// 需要推送的账号ID
			List<Integer> memberIds = new ArrayList<Integer>();
			memberIds.addAll(marauderAlliance.getMemberList());
			// 原城池持有者公会
			AllianceEntity holderAlliance = AllianceManager.getInstance().getAlliance(cityEntity.getHolderId());
			String defenderName = null;
			if (holderAlliance != null) {
				defenderName = holderAlliance.getName();
				memberIds.addAll(holderAlliance.getMemberList());
				// 发送邮件
				if (isSendMail) {
					// 攻击方
					for (Integer memberId : marauderAlliance.getMemberList()) {
						MailManager.createSysMail(memberId, Mail.MailType.GVG_MAIL_VALUE,
								GsConst.MailId.GVG_ATTACKER_WIN, "GVG攻击方", null, GuaJiTime.getTimeString(),
								defenderName, "" + cityEntity.getCityId());
					}
					// 防御方
					for (Integer memberId : holderAlliance.getMemberList()) {
						MailManager.createSysMail(memberId, Mail.MailType.GVG_MAIL_VALUE,
								GsConst.MailId.GVG_DEFENDER_FAIL, "GVG防御方", null, GuaJiTime.getTimeString(),
								attackerName, "" + cityEntity.getCityId());
					}
				}
			}
			GvgService.getInstance().addGvgLogs(cityEntity, attackerName, defenderName, battleResult);
			// 推送城池数据
			String allianceName = battleResult > 0 ? attackerName : defenderName;
			this.pushCityInfo(cityEntity, memberIds, allianceName, battleResult);
		}
	}

	/**
	 * 推送城池更新数据
	 *
	 * @param cityEntity
	 * @param memberIds
	 * @param allianceName
	 * @param battleResult
	 */
	private void pushCityInfo(GvgCityEntity cityEntity, List<Integer> memberIds, String allianceName,
			int battleResult) {
		// 构建推送数据
		CityInfo.Builder cityBuilder = CityInfo.newBuilder();
		cityBuilder.setCityId(cityEntity.getCityId());
		cityBuilder.setIsReAtk(false);
		if (cityEntity.isFightback()) {
			cityBuilder.setStatus(CityStatus.CITY_STATUS_FORBIDDEN);
		} else {
			if (cityEntity.getFightbackTime() > 0 && cityEntity.getFightbackTime() < GuaJiTime.getMillisecond()) {
				cityBuilder.setStatus(CityStatus.CITY_STATUS_FORBIDDEN);
			} else {
				if (!defendersNpc.containsKey(cityEntity.getCityId())) {
					cityBuilder.setStatus(CityStatus.CITY_STATUS_REATTACK);
					cityBuilder.clearIsReAtk();
					cityBuilder.setIsReAtk(true);
					cityBuilder.setFightbackTime(GvgCfg.getInstance().getFightbackTime());
				} else {
					cityBuilder.setStatus(CityStatus.CITY_STATUS_FORBIDDEN);
				}
			}
			cityBuilder.setReAtkGuildId(cityEntity.getHolderId());
		}
		GuildInfo.Builder builder = GuildInfo.newBuilder();
		if (battleResult > 0) {
			builder.setGuildId(cityEntity.getMarauderId());
		} else {
			builder.setGuildId(cityEntity.getHolderId());
		}
		builder.setName(allianceName);
		cityBuilder.setDefGuild(builder);
		// 推送城池数据
		for (Integer memberId : memberIds) {
			Player _player = PlayerUtil.queryPlayer(memberId);
			if (_player != null && _player.isOnline()) {
				_player.sendProtocol(Protocol.valueOf(HP.code.PUSH_CITY_STATE_S_VALUE, cityBuilder));
			}
		}
	}

	/**
	 * 添加城池战斗日志
	 *
	 * @param cityId
	 * @param attackerName
	 * @param defenderName
	 * @param battleResult
	 * @param killEnemies  杀敌个数
	 */
	private void builderBattleLog(int cityId, String attackerName, String defenderName, int battleResult,
			int killEnemies) {
		// 获取城池日志
		List<CityBattleInfo> battleLogs = battleLogsCache.get(cityId);
		if (battleLogs == null) {
			battleLogs = new ArrayList<CityBattleInfo>();
		}
		CityBattleInfo.Builder battleLog = CityBattleInfo.newBuilder();
		battleLog.setAtkName(attackerName);
		if (defenderName != null) {
			battleLog.setDefName(defenderName);
		}
		battleLog.setIsAtkWin(battleResult);
		battleLog.setContinueWin(killEnemies);
		battleLogs.add(battleLog.build());
		battleLogsCache.put(cityId, battleLogs);
	}

	/**
	 * 战斗阶段结束添加日志并更新数据
	 */
	private void gvgLogsAndUpdateCity() {
		long todaySecond = GuaJiTime.getMillisecond() - GuaJiTime.getAM0Date().getTime();
		if (todaySecond < GvgCfg.getInstance().getBattleEnd()) {
			return;
		}
		// 更新城池数据
		TreeMap<Integer, GvgCityEntity> cities = GvgService.getInstance().getCities();
		synchronized (cities) {
			for (GvgCityEntity cityEntity : cities.values()) {
				// 侵略者公会
				AllianceEntity marauderAlliance = AllianceManager.getInstance().getAlliance(cityEntity.getMarauderId());
				if (marauderAlliance == null) {
					// 更新数据
					this.updateCityEntity(cityEntity);
					continue;
				}
				// 持有者公会
				AllianceEntity holderAlliance = AllianceManager.getInstance().getAlliance(cityEntity.getHolderId());
				String defenderName = null;
				if (holderAlliance != null) {
					defenderName = holderAlliance.getName();
				}
				String attackerName = marauderAlliance.getName();
				if (cityEntity.getDefenderList().size() <= 0 && cityEntity.getAttackerList().size() > 0
						&& cityEntity.getMarauderId() > 0) {
					// 城池持有者切换
					int holderId = cityEntity.getHolderId();
					cityEntity.setHolderId(cityEntity.getMarauderId());
					cityEntity.setMarauderId(holderId);
					Log.gvgLog(String.format("2、update city %d holderId %d marauderId %d", cityEntity.getCityId(),
							cityEntity.getHolderId(), cityEntity.getMarauderId()));
					// 添加日志
					GvgService.getInstance().addGvgLogs(cityEntity, attackerName, defenderName, 1);
				} else {
					GvgService.getInstance().addGvgLogs(cityEntity, attackerName, defenderName, 0);
				}
				// 更新数据
				this.updateCityEntity(cityEntity);
			}
		}
	}

	private void ciytSendRewards() {

		// 遍历所有城池
		TreeMap<Integer, GvgCityEntity> cities = GvgService.getInstance().getCities();
		List<VitalityRankEntity> vitalityRanks = GvgService.getInstance().getVitalityRanks();
		// Gvg战斗结果列表
		List<GvgBattleResultBean> battleResultRanks = new ArrayList<GvgBattleResultBean>();
		// 发送邮件时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(GuaJiTime.getAM0Date());
		int rank = 1;
		for (VitalityRankEntity vitalityRank : vitalityRanks) {
			int allianceId = vitalityRank.getAllianceId();

			// 只取前15名
			if (rank > GvgCfg.getInstance().getRankNumber()) {
				break;
			}

			AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
			if (allianceEntity == null)
				continue;

			GvgBattleResultBean itemBattleResult = new GvgBattleResultBean(allianceId, rank);
			// 构造奖励列表
			AwardItems everydayAward = new AwardItems();
			for (GvgCityEntity cityEntity : cities.values()) {
				// 是否有帮会占领
				if (cityEntity.getHolderId() <= 0 || cityEntity.getHolderId() != allianceEntity.getId()) {
					continue;
				}
				// 获取奖励配置数据
				GvgCitiesCfg config = ConfigManager.getInstance().getConfigByKey(GvgCitiesCfg.class,
						cityEntity.getCityId());

				if (config == null)
					continue;

				if (config.getLevel() == 0)
					continue;

				// 排除复活点数据
				everydayAward.addItemInfos(config.getItemInfos());
				if (config.getLevel() == 1) {
					itemBattleResult.AddLevel1CityNum();
				} else if (config.getLevel() == 2) {
					itemBattleResult.AddLevel2CityNum();
				} else if (config.getLevel() == 3) {
					itemBattleResult.AddLevel3CityNum();
				}
				itemBattleResult.AddBattleScore(config.getObtainScore());
			}
			battleResultRanks.add(itemBattleResult);
			// 帮派成员发放奖励
			for (int playerID : allianceEntity.getMemberList()) {
				if (itemBattleResult.getTotalCityNum() == 0)
					MailManager.createMail(playerID, Mail.MailType.Normal_VALUE, GsConst.MailId.EVERYDAY_NOCITY_MAIL,
							"", null, date, allianceEntity.getName());
				else {
					if (GvgManager.getInstance().getTodayPlayerIds().contains(playerID)) {
						MailManager.createMail(playerID, Mail.MailType.Reward_VALUE, GsConst.MailId.EVERYDAY_MAIL,
								"城战每日奖励", everydayAward, date, allianceEntity.getName());
					}

				}

			}

			rank++;
		}

		// 赛季重置前一天发送赛季奖励
		int dayOfMonth = GuaJiTime.getMonthDay() + 1;// 取前一天 if
		if (GvgCfg.getInstance().getResetDayList().contains(dayOfMonth)) {
			// 取宝箱配置数据
			GvgSeasonAwdCfg gvgBox = GvgSeasonAwdCfg.getSeasonAwdCfg();// 获得当前赛季奖励

			if (gvgBox == null) {
				logger.info("gvg  GvgSeasonAwdCfg  not found");
				return;
			}
			// 拥有城池排序
			battleResultRanks.sort(new HoldeCityComparator());
			for (int i = 0; i < battleResultRanks.size(); i++) {

				AllianceEntity allianceEntity = AllianceManager.getInstance()
						.getAlliance(battleResultRanks.get(i).getAllianceId());
				if (allianceEntity == null)
					continue;
				// 只有固定前几名发送奖励
				int gvgRank = i + 1;

				if (!gvgBox.getRewardsMap().containsKey(gvgRank)) {
					logger.info("gvg  GvgSeasonAwdCfg  have not rank{} award", gvgRank);
					continue;
				}

				// 构造奖励列表
				AwardItems seansonAward = gvgBox.getRewardsMap().get(gvgRank);
				if (seansonAward != null) {
					// 帮派成员发放奖励
					for (int playerID : allianceEntity.getMemberList()) {
						MailManager.createMail(playerID, Mail.MailType.Reward_VALUE, GsConst.MailId.SEANSON_MAIL,
								"城战赛季奖励", seansonAward, date, String.valueOf(gvgRank));
					}
				}

			}
		}
		// 发完奖励 清除参与用户编号
		GvgManager.getInstance().getTodayPlayerIds().clear();

	}

	/**
	 * 战斗结束更新数据
	 * 
	 * @param cityEntity
	 */
	private void updateCityEntity(GvgCityEntity cityEntity) {
		// 聊天框奖励
		if (cityEntity.getCityId() == GvgCfg.getInstance().getCityId()) {
			this.changeChatSkin(cityEntity.getHolderId(), cityEntity.getMarauderId());
		}
		// 返回体力
		this.returnPower(cityEntity);
		// 更新数据
		cityEntity.clearAttacker();
		cityEntity.setMarauderId(0);
		cityEntity.setFightback(false);
		cityEntity.setFightbackTime(0);
		cityEntity.notifyUpdate();
	}

	/**
	 * 聊天框获取
	 * 
	 * @param holderId
	 * @param marauderId
	 */
	private void changeChatSkin(int holderId, int marauderId) {
		// 防守者公会添加聊天框
		AllianceEntity holderAlliance = AllianceManager.getInstance().getAlliance(holderId);
		if (holderAlliance != null) {
			for (int memberId : holderAlliance.getMemberList()) {
				Log.gvgLog(String.format("get chat skin allianceId %d member %s", holderId, memberId));
				ActivityUtil.changeChatSkin(memberId, GvgCfg.getInstance().getSkinId(), 1);
			}
		} else {
			Log.gvgLog(String.format("get chat skin alliance is null id %d", holderId));
		}
		// 侵略者公会删除聊天框
		AllianceEntity marauderAlliance = AllianceManager.getInstance().getAlliance(marauderId);
		if (marauderAlliance != null) {
			for (int memberId : marauderAlliance.getMemberList()) {
				Log.gvgLog(String.format("remove chat skin allianceId %d member %s", marauderId, memberId));
				ActivityUtil.changeChatSkin(memberId, GvgCfg.getInstance().getSkinId(), 2);
			}
		} else {
			Log.gvgLog(String.format("remove chat skin alliance is null id %d", marauderId));
		}
	}

	/**
	 * 未参战的攻击者返还体力值
	 * 
	 * @param cityEntity
	 */
	private void returnPower(GvgCityEntity cityEntity) {
		boolean isFrist = true;
		for (GvgOccupyBean occupyBean : cityEntity.getAttackerList()) {
			if (isFrist) {
				isFrist = false;
				continue;
			}
			Player _player = PlayerUtil.queryPlayer(occupyBean.getPlayerId());
			if (_player == null || !_player.isOnline()) {
				// 不在线玩家处理
				for (int roleId : occupyBean.getRoleIds()) {
					DBManager.getInstance()
							.update(String.format("update RoleEntity set power = power + %d where id = %d",
									GvgCfg.getInstance().getAttackPower(), roleId));
				}
				continue;
			}
			List<RoleEntity> roleEntities = _player.getPlayerData().getRoleEntities();
			for (RoleEntity roleEntity : roleEntities) {
				// 佣兵是否激活
				if (roleEntity.getRoleState() == Const.RoleActiviteState.NOT_ACTIVITE_VALUE
						|| roleEntity.getType() == GsConst.RoleType.MAIN_ROLE) {
					continue;
				}
				if (occupyBean.getRoleIds().contains(roleEntity.getId())) {
					roleEntity.setPower(roleEntity.getPower() + GvgCfg.getInstance().getAttackPower());
					roleEntity.notifyUpdate();
				}
			}
		}
	}

	/**
	 * 推送攻防队伍数量
	 * 
	 * @param cityEntity
	 */
	public void pushTeamNumber(GvgCityEntity cityEntity) {
		// 宣战数据同步
		TreeMap<Integer, GvgCityEntity> cities = GvgService.getInstance().getCities();
		Set<Integer> memberIds = new HashSet<Integer>();
		for (GvgCityEntity city : cities.values()) {
			AllianceEntity _marauderAlliance = AllianceManager.getInstance().getAlliance(city.getMarauderId());
			if (_marauderAlliance != null) {
				memberIds.addAll(_marauderAlliance.getMemberList());
			}
			AllianceEntity _holderAlliance = AllianceManager.getInstance().getAlliance(city.getHolderId());
			if (_holderAlliance != null) {
				memberIds.addAll(_holderAlliance.getMemberList());
			}
		}
		// 构建推送数据
		TeamNumberPush.Builder response = TeamNumberPush.newBuilder();
		response.setCityId(cityEntity.getCityId());
		response.setAtkNumbers(cityEntity.getAttackerList().size());
		if (cityEntity.getHolderId() > 0) {
			response.setDefNumbers(cityEntity.getDefenderList().size());
		}
		response.setCurrentTime(GuaJiTime.getMillisecond());
		for (Integer memberId : memberIds) {
			Player _player = PlayerUtil.queryPlayer(memberId);
			if (_player != null && _player.isOnline()) {
				_player.sendProtocol(Protocol.valueOf(HP.code.TEAM_NUMBER_PUSH_S_VALUE, response));
			}
		}
	}

	/**
	 * @param allianceId 工会编号
	 * @return 0 没有城池 1 复活点 2 真正的城池
	 */
	public int allianceHasCityType(int allianceId) {

		// 宣战数据同步
		TreeMap<Integer, GvgCityEntity> cities = GvgService.getInstance().getCities();
		for (GvgCityEntity city : cities.values()) {

			if (city.getHolderId() == allianceId) {
				GvgCitiesCfg config = ConfigManager.getInstance().getConfigByKey(GvgCitiesCfg.class, city.getCityId());
				if (config != null) {
					if (config.getLevel() > 0)
						return 2;
					else
						return 1;

				}
			}
		}

		return 0;

	}

	/**
	 * 推送GVG战报
	 * 
	 * @param cityEntity
	 * @param battle
	 * @param result
	 * @param attackerName
	 * @param defenderName
	 */
	private void pushCityBattle(GvgCityEntity cityEntity, BattleInfo.Builder battle, int result, String attackerName,
			String defenderName) {
		// 提取公会数据
		AllianceEntity marauderAlliance = AllianceManager.getInstance().getAlliance(cityEntity.getMarauderId());
		if (marauderAlliance == null) {
			return;
		}
		AllianceEntity holderAlliance = AllianceManager.getInstance().getAlliance(cityEntity.getHolderId());
		if (holderAlliance == null) {
			return;
		}
		// 构建返回数据
		CityBattlePush.Builder response = CityBattlePush.newBuilder();
		response.setCityId(cityEntity.getCityId());
		// 侵略者公会
		List<Integer> memberIds = new ArrayList<Integer>();
		String marauderName = marauderAlliance.getName();
		memberIds.addAll(marauderAlliance.getMemberList());
		// 防守者公会
		String holderName = holderAlliance.getName();
		memberIds.addAll(holderAlliance.getMemberList());
		// 推送数据
		for (Integer memberId : memberIds) {
			// 战报推送
			Player _player = PlayerUtil.queryPlayer(memberId);
			if (_player != null && _player.isOnline()) {
				_player.sendProtocol(Protocol.valueOf(HP.code.PUSH_GVG_BATTLE_S_VALUE, response));
			}
		}
		// 战况播报
		if (holderName == null || marauderName == null) {
			return;
		}
		if (result > 0 && cityEntity.getDefenderList().size() <= 0) {
			// 攻击者胜利
			String key = cityEntity.isFightback() ? GvgCfg.getInstance().getFightbackWin()
					: GvgCfg.getInstance().getAttackerWin();
			String chat = ChatManager.getMsgJson(key, marauderName, holderName, cityEntity.getCityId());
			GsApp.getInstance().broadcastChatWorldMsg(chat, chat);
		}
	}

	/**
	 * 替换添加新DEBUFF
	 * 
	 * @param playerId
	 * @param cityId
	 * @param battleRoles
	 */
	private void updateDebuff(int playerId, int cityId, List<BattleRole> battleRoles) {
		if (killEnemyCache.containsKey(playerId)) {
			GvgKillEnemy killEnemy = this.searchKillEnemy(playerId, cityId);
			if (killEnemy == null) {
				return;
			}
			List<GvgDebuffCfg> debuffConfigs = ConfigManager.getInstance().getConfigList(GvgDebuffCfg.class);
			for (GvgDebuffCfg debuffCfg : debuffConfigs) {
				// 查找配置数据
				if (killEnemy.getKillNumber() < debuffCfg.getKillNum()) {
					continue;
				}
				if (killEnemy.getDebuffId() == debuffCfg.getId()) {
					break;
				}
				Log.gvgLog(String.format("cityId %d playerId %d kill enemy total %d", cityId, playerId,
						killEnemy.getKillNumber()));
				// 提取旧DEBUFF配置
				GvgDebuffCfg oldDebuffCfg = null;
				if (killEnemy.getDebuffId() > 0) {
					oldDebuffCfg = debuffConfigs.get(killEnemy.getDebuffId() - 1);
					Log.gvgLog(String.format("old debuff %s", oldDebuffCfg.toString()));
				}
				// 更新 DEBUFF ID
				killEnemy.setDebuffId(debuffCfg.getId());
				Log.gvgLog(String.format("new debuff %s", debuffCfg.toString()));
				for (BattleRole battleRole : battleRoles) {
					// 清除旧DEBUFF
					if (oldDebuffCfg != null) {
						// 配置属性
						Attribute attributeCfg = Attribute.valueOf(oldDebuffCfg.getDebuffAttrs());
						// 玩家属性
						Attribute attr = battleRole.getAttribute();
						if (attributeCfg != null) {
							for (Entry<attr, Integer> attrMap : attributeCfg.getAttrMap().entrySet()) {
								int attrValue = attr.getValue(attrMap.getKey());
								int _attrValue = this.presentAttrValue(attrMap.getValue(), attrValue);
								attr.set(attrMap.getKey(), _attrValue);
							}
						}
					}
					// 添加新DEBUFF
					// 配置属性
					Attribute attributeCfg = Attribute.valueOf(debuffCfg.getDebuffAttrs());
					// 玩家属性
					Attribute attr = battleRole.getAttribute();
					if (attributeCfg != null) {
						for (Entry<attr, Integer> attrMap : attributeCfg.getAttrMap().entrySet()) {
							int attrValue = attr.getValue(attrMap.getKey());
							int _attrValue = this.restoreAttrValue(attrMap.getValue(), attrValue);
							attr.set(attrMap.getKey(), _attrValue);
						}
					}
				}
				break;
			}
		}
	}

	/**
	 * 查找连杀缓存
	 * 
	 * @param playerId
	 * @param cityId
	 * @return
	 */
	private GvgKillEnemy searchKillEnemy(int playerId, int cityId) {
		List<GvgKillEnemy> killEnemies = killEnemyCache.get(playerId);
		if (killEnemies == null || killEnemies.size() <= 0) {
			return null;
		}
		for (GvgKillEnemy killEnemy : killEnemies) {
			if (cityId == killEnemy.getCityId()) {
				return killEnemy;
			}
		}
		return null;
	}

	/**
	 * 移除DEBUFF更新值
	 * 
	 * @param debuffValue
	 * @param attrValue
	 * @return
	 */
	private int presentAttrValue(float debuffValue, int attrValue) {
		float ratio = 1 + debuffValue / 10000;
		if (ratio == 0.0f) {
			return attrValue;
		}
		int newAttrValue = (int) (attrValue / ratio);
		return newAttrValue;
	}

	/**
	 * 添加DEBUFF更新值
	 * 
	 * @param debuffValue
	 * @param attrValue
	 * @return
	 */
	private int restoreAttrValue(float debuffValue, int attrValue) {
		float ratio = 1 + debuffValue / 10000;
		int newAttrValue = (int) (attrValue * ratio);
		return newAttrValue;
	}

	/**
	 * @return 距离下次赛季开启所剩余的时间
	 */
	public long getSuplyTimeNextSeason() {

		List<Integer> dayList = GvgCfg.getInstance().getResetDayList();
		// 从小到大排序
		Collections.sort(dayList);
		List<Integer> resetDayList = GvgCfg.getInstance().getResetDayList();
		if (resetDayList.size() == 0) {
			return Long.MAX_VALUE;
		}

		long nowMillSec = GuaJiTime.getMillisecond();
		long resetTime = GvgCfg.getInstance().getResetTime();
		long theLastResetOfMonth = GuaJiTime.getTheDayAM0Date(resetDayList.get(resetDayList.size() - 1)) + resetTime;
		if (nowMillSec >= theLastResetOfMonth) {
			return GuaJiTime.getNextMonthDayAM0Date(dayList.get(0)) + resetTime - resetTime;
		} else {
			for (Integer dayOfMonth : resetDayList) {
				long reset = GuaJiTime.getDayAM0Date(dayOfMonth) + resetTime;
				if (reset > nowMillSec) {
					return reset - nowMillSec;
				}
			}
		}

		return 0;
	}

	public List<Integer> getPlayerLimitIds() {
		return playerLimitIds;
	}

	public List<Integer> getTodayPlayerIds() {
		return todayPlayerIds;
	}

	public List<Integer> getPlayerDefenderLimitIds() {
		return playerDefenderLimitIds;
	}
	
	

	
}
