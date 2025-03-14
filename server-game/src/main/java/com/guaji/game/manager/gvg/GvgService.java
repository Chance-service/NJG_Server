package com.guaji.game.manager.gvg;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.os.GuaJiTime;

import com.guaji.game.GsApp;
import com.guaji.game.bean.GvgBattleResultBean;
import com.guaji.game.bean.GvgOccupyBean;
import com.guaji.game.config.GvgCfg;
import com.guaji.game.config.GvgCitiesCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.GvgAllianceEntity;
import com.guaji.game.entity.GvgCityEntity;
import com.guaji.game.entity.GvgCityRewardEntity;
import com.guaji.game.entity.GvgHistoryRankEntity;
import com.guaji.game.entity.GvgLogEntity;
import com.guaji.game.entity.GvgTimeEntity;
import com.guaji.game.entity.VitalityRankEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.ChatManager;
import com.guaji.game.protocol.GroupVsFunction.GVGStatus;

/**
 * GVG服务
 */
public class GvgService {

	/**
	 * 锁
	 */
	private Lock lock = new ReentrantLock();

	/**
	 * gvg功能开启数据
	 */
	private GvgTimeEntity timeEntity = null;

	/**
	 * GVG公会数据集合
	 */
	private ConcurrentSkipListSet<GvgAllianceEntity> alliances = new ConcurrentSkipListSet<GvgAllianceEntity>(
			new VitalityComparator());

	/**
	 * 前一天元气增加排行
	 */
	private List<VitalityRankEntity> vitalityRanks = new ArrayList<VitalityRankEntity>();

	/**
	 * 城池数据<key:城池ID vaule:城池数据>
	 */
	private TreeMap<Integer, GvgCityEntity> cities = new TreeMap<Integer, GvgCityEntity>();

	/**
	 * 公会城池城池奖励数据<key:公会ID value:城池奖励数据>
	 */
	private ConcurrentHashMap<Integer, GvgCityRewardEntity> cityRewards = new ConcurrentHashMap<Integer, GvgCityRewardEntity>();

	/**
	 * 城池战斗LOG记录
	 */
	private BlockingQueue<GvgLogEntity> gvgLogs = new ArrayBlockingQueue<GvgLogEntity>(
			GvgCfg.getInstance().getGvgLogTotal());

	/**
	 * 历史赛季排名
	 */
	List<GvgHistoryRankEntity> lasteasonResultRanks = new ArrayList<GvgHistoryRankEntity>();

	private static final GvgService instance = new GvgService();

	private GvgService() {

	}

	public static GvgService getInstance() {
		return instance;
	}

	/**
	 * 数据加载
	 */
	public void init() {
		// 加载GVG功能数据
		GvgTimeEntity _timeEntity = DBManager.getInstance().fetch(GvgTimeEntity.class,
				"from GvgTimeEntity where invalid = 0");
		this.timeEntity = _timeEntity;
		// 加载GVG公会数据
		List<GvgAllianceEntity> _alliances = DBManager.getInstance().query("from GvgAllianceEntity where invalid = 0");
		if (_alliances != null && _alliances.size() > 0) {
			for (GvgAllianceEntity _alliance : _alliances) {
				alliances.add(_alliance);
			}
		}
		// 加载公会元气增加排行
		List<VitalityRankEntity> _ranks = DBManager.getInstance()
				.query("from VitalityRankEntity where invalid = 0 order by rank asc");
		if (_ranks != null && _ranks.size() > 0) {
			vitalityRanks.addAll(_ranks);
		}
		// 加载GVG城池
		List<GvgCityEntity> _cities = DBManager.getInstance().query("from GvgCityEntity where invalid = 0");
		if (_cities != null && _cities.size() > 0) {
			for (GvgCityEntity city : _cities) {
				city.init();
				cities.put(city.getCityId(), city);
			}
		}
		// 加载GVG城池奖励
		List<GvgCityRewardEntity> _cityRewards = DBManager.getInstance()
				.query("from GvgCityRewardEntity where invalid = 0");
		if (_cityRewards != null && _cityRewards.size() > 0) {
			for (GvgCityRewardEntity cityReward : _cityRewards) {
				cityReward.init();
				cityRewards.put(cityReward.getAllianceId(), cityReward);
			}
		}
		// 加载GVG城池奖励
		List<GvgLogEntity> _gvgLogs = DBManager.getInstance()
				.query("from GvgLogEntity where invalid = 0 order by createTime desc");
		if (_gvgLogs != null && _gvgLogs.size() > 0) {
			for (GvgLogEntity gvgLog : _gvgLogs) {
				gvgLogs.add(gvgLog);
			}
		}

		// 加载历史赛季排名
		List<GvgHistoryRankEntity> _gvgHistoryRank = DBManager.getInstance()
				.query("from GvgHistoryRankEntity where invalid = 0 order by rank asc");

		if (_gvgHistoryRank != null && _gvgHistoryRank.size() > 0) {
			for (GvgHistoryRankEntity gvgHistoryRank : _gvgHistoryRank) {
				lasteasonResultRanks.add(gvgHistoryRank);
			}
		}

	}

	/**
	 * 功能开启数据
	 * 
	 * @param timeEntity
	 */
	public void setTimeEntity(GvgTimeEntity timeEntity) {
		this.timeEntity = timeEntity;
	}

	/**
	 * GVG功能是否开启
	 */
	public boolean isOpeanFunction() {
		if (timeEntity == null || timeEntity.isInvalid() == true) {
			return false;
		}
		return true;
	}

	/**
	 * 是否是重置时间
	 * 
	 * @return
	 */
	public boolean isResetDate() {
		int dayOfMonth = GuaJiTime.getMonthDay();
		return GvgCfg.getInstance().getResetDayList().contains(dayOfMonth);
	}

	/**
	 * 刷新时间
	 */
	public long getRefreshTime() {
		return timeEntity.getRefreshTime();
	}

	/**
	 * 重置时间
	 */
	public long getResetTime() {
		return timeEntity.getResettime();
	}

	/**
	 * 更新记录
	 * 
	 * @param isUpdate
	 */
	public void updateGvgTime(boolean isUpdate) {
		timeEntity.updateGvgTime(isUpdate);
	}

	/**
	 * 更新排名重置时间
	 * 
	 * @param isUpdate
	 */
	public void updateResetTime(long resetTime) {
		timeEntity.updateResetTime(resetTime);
	}

	/**
	 * 获取推送阶段
	 * 
	 * @return
	 */
	public int getPushState() {
		return timeEntity.getPushState();
	}

	/**
	 * 更新推送数据
	 *
	 * @param pushState
	 */
	public void updatePushState(int pushState) {
		timeEntity.updatePushState(pushState);
	}

	/**
	 * 元气增加集合
	 */
	public ConcurrentSkipListSet<GvgAllianceEntity> getAlliances() {
		return alliances;
	}

	/**
	 * GVG初始化今日元气
	 */
	public void initGvgAlliances() {
		// 填充数据
		synchronized (alliances) {
			ConcurrentHashMap<Integer, AllianceEntity> allianceMap = AllianceManager.getInstance().getAllianceMap();
			if (allianceMap == null || allianceMap.size() <= 0) {
				return;
			}
			// 赋值比较排序
			List<AllianceEntity> list = new ArrayList<AllianceEntity>(allianceMap.values());
			Collections.sort(list, new Comparator<AllianceEntity>() {
				@Override
				public int compare(AllianceEntity entity1, AllianceEntity entity2) {
					// ID排序
					if (entity1.getLevel() > entity2.getLevel()) {
						return -1;
					}
					if (entity1.getLevel() < entity2.getLevel()) {
						return 1;
					}
					if (entity1.getId() > entity2.getId()) {
						return -1;
					}
					if (entity1.getId() < entity2.getId()) {
						return 1;
					}
					return 0;
				}
			});
			// 添加公会数据

			alliances.clear();

			// int index = 1;
			for (AllianceEntity allianceEntity : list) {
				GvgAllianceEntity _gvgEntity = GvgAllianceEntity.createEntity(allianceEntity.getId(),
						allianceEntity.getScoreValue(), allianceEntity.getLevel());
				alliances.add(_gvgEntity);
			}
		}
	}

	/**
	 * 查找元气增加缓存元素
	 *
	 * @param element
	 */
	public void updateAlliances(GvgAllianceEntity element) {
		// 更新数据
		synchronized (alliances) {
			Iterator<GvgAllianceEntity> iterator = alliances.iterator();
			while (iterator.hasNext()) {
				GvgAllianceEntity entity = iterator.next();
				if (entity.getAllianceId() == element.getAllianceId()) {
					alliances.remove(entity);
					int addCount = element.getAddCount();
					entity.updateAddCount(addCount);
					alliances.add(entity);
					return;
				}
			}
			// 没有缓存数据
			if (DBManager.getInstance().create(element)) {
				alliances.add(element);
			}
		}
	}

	/**
	 * 元气增加集合更新
	 */
	public void updateAlliances() {
		synchronized (alliances) {
			for (GvgAllianceEntity alliance : alliances) {
				DBManager.getInstance().delete(alliance);
			}
			// 清理缓存
			this.initGvgAlliances();
		}
	}

	/**
	 * 获取前一天元气增加排行数据
	 */
	public List<VitalityRankEntity> getVitalityRanks() {
		return vitalityRanks;
	}

	/**
	 * 获取赛季历史排名
	 */
	public List<GvgHistoryRankEntity> getHistorySeaonRank() {
		return lasteasonResultRanks;
	}

	/**
	 * 更新前一天元气增加排行数据
	 */
	public void updateRank() {
		lock.lock();
		try {
			// 清理数据
			for (VitalityRankEntity vitalityRank : vitalityRanks) {
				DBManager.getInstance().delete(vitalityRank);
			}
			this.vitalityRanks.clear();
			// 添加新数据
			int index = 1;

			ConcurrentSkipListSet<GvgAllianceEntity> cloneSet = new ConcurrentSkipListSet<GvgAllianceEntity>(
					new VitalityComparator());
			synchronized (alliances) {
				cloneSet.addAll(alliances);
			}
			for (GvgAllianceEntity vitality : cloneSet) {
				if (index > GvgCfg.getInstance().getRankNumber()) {
					return;
				}
				
				VitalityRankEntity vitalityRank = VitalityRankEntity.createEntity(index, vitality.getAllianceId(),
						vitality.getAddCount());
				if (vitalityRank == null) {
					continue;
				}

				// 添加缓存数据
				vitalityRanks.add(vitalityRank);
				
				index++;
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 添加城池数据
	 *
	 * @param cityEntity
	 */
	public void addCityEntity(GvgCityEntity cityEntity) {
		cities.put(cityEntity.getCityId(), cityEntity);
	}

	/**
	 * 获取城池数据集合
	 */
	public TreeMap<Integer, GvgCityEntity> getCities() {
		return cities;
	}

	/**
	 * 获取单个城池数据
	 * 
	 * @param cityId
	 * @return
	 */
	public GvgCityEntity getCityEntity(int cityId) {
		return cities.get(cityId);
	}

	/**
	 * 添加城池奖励数据
	 * 
	 * @param allianceId
	 * @param cityId
	 */
	public void addCityReward(int allianceId, int cityId) {
		GvgCityRewardEntity rewardEntity = cityRewards.get(allianceId);
		if (rewardEntity != null) {
			// 更新数据
			rewardEntity.updateCityId(cityId);
		} else {
			// 添加新数据
			rewardEntity = GvgCityRewardEntity.createEntity(allianceId, cityId);
			if (rewardEntity != null) {
				cityRewards.put(allianceId, rewardEntity);
			}
		}
	}

	/**
	 * 获取城池奖励数据
	 * 
	 * @param allianceId
	 * @return
	 */
	public GvgCityRewardEntity getCityReward(int allianceId) {
		return cityRewards.get(allianceId);
	}

	public GvgTimeEntity getTimeCity() {
		return timeEntity;
	}

	/**
	 * 每日清理城池数据并更新城池奖励数据
	 * 
	 * @param isUpdate
	 */
	public void updateCity(boolean isUpdate) {
		lock.lock();
		try {
			// 更新新数据
			for (GvgCityEntity cityEntity : cities.values()) {
				// 清理数据
				cityEntity.clearDefender();
				cityEntity.clearAttacker();
				if (cityEntity.getHolderId() <= 0) {
					continue;
				}

			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 重置城战所有数据重新开始
	 * 
	 * 
	 */
	public void resetCity() {
		synchronized (alliances) {
			Iterator<GvgAllianceEntity> iterator = alliances.iterator();
			while (iterator.hasNext()) {
				GvgAllianceEntity entity = iterator.next();
				entity.setDeclareTimes(0);
				entity.notifyUpdate();
			}
		}
	}

	/**
	 * 重置复活城数据
	 */
	public void resetReviveCity() {

		Collection<GvgCitiesCfg> configs = ConfigManager.getInstance().getConfigMap(GvgCitiesCfg.class).values();
		for (GvgCitiesCfg gvgConfig : configs) {
			// 非复活点数据不重置
			if (gvgConfig.getLevel() != 0)
				continue;

			GvgCityEntity cityEntity = cities.get(gvgConfig.getCityId());
			if (cityEntity != null && cityEntity.getHolderId() != 0) {
				cityEntity.setHolderId(0);
				cityEntity.notifyUpdate();
			}

		}

	}

	/**
	 * 获取日志数据
	 */
	public BlockingQueue<GvgLogEntity> getGvgLogs() {
		return gvgLogs;
	}

	/**
	 * 添加GVG日志
	 *
	 * @param cityEntity
	 * @param attackerName
	 * @param defenderName
	 * @param result
	 */
	public void addGvgLogs(GvgCityEntity cityEntity, String attackerName, String defenderName, int result) {
		if (cityEntity.getFightbackTime() > 0 && !cityEntity.isFightback()) {
			return;
		}
		// 记录条数判断
		if (gvgLogs.size() >= GvgCfg.getInstance().getGvgLogTotal()) {
			GvgLogEntity gvgLog = gvgLogs.remove();
			DBManager.getInstance().delete(gvgLog);
		}
		// 记录天数判定
		for (GvgLogEntity gvgLog : gvgLogs) {
			long time = gvgLog.getCreateTime().getTime();
			if (time + GvgCfg.getInstance().getGvgLogDay() <= GuaJiTime.getMillisecond()) {
				GvgLogEntity _gvgLog = gvgLogs.remove();
				DBManager.getInstance().delete(_gvgLog);
			} else {
				break;
			}
		}
		// 创建记录
		boolean isFightback = cityEntity.isFightback();
		GvgLogEntity entity = GvgLogEntity.createEntity(attackerName, defenderName, isFightback, result,
				cityEntity.getCityId());
		if (entity != null) {
			gvgLogs.add(entity);
		}
	}

	/**
	 * 更新对战列表数据
	 * 
	 * @param playerId
	 * @param fromRoleId
	 * @param toRoleId
	 */
	public void updateOccupy(int playerId, int fromRoleId, int toRoleId) {
		lock.lock();
		try {
			GVGStatus gvgStatus = GvgManager.getInstance().getGvgStatus();
			if (gvgStatus == GVGStatus.GVG_STATUS_FIGHTING) {
				return;
			}
			for (GvgCityEntity cityEntity : cities.values()) {
				// 防御佣兵
				List<GvgOccupyBean> defenderList = cityEntity.getDefenderList();
				boolean isContimue = this.isContinue(playerId, fromRoleId, toRoleId, defenderList);
				if (!isContimue) {
					cityEntity.notifyUpdate();
					return;
				}
				// 攻击佣兵
				List<GvgOccupyBean> attackerList = cityEntity.getAttackerList();
				isContimue = this.isContinue(playerId, fromRoleId, toRoleId, attackerList);
				if (!isContimue) {
					cityEntity.notifyUpdate();
					return;
				}
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 是否继续查找佣兵派遣数据
	 * 
	 * @param playerId
	 * @param fromRoleId
	 * @param toRoleId
	 * @param occupyList
	 * @return
	 */
	private boolean isContinue(int playerId, int fromRoleId, int toRoleId, List<GvgOccupyBean> occupyList) {
		for (GvgOccupyBean occupyBean : occupyList) {
			if (occupyBean.getPlayerId() != playerId) {
				continue;
			}
			int index = occupyBean.getRoleIds().indexOf(fromRoleId);
			if (index >= 0) {
				occupyBean.getRoleIds().set(index, toRoleId);
				return false;
			}
		}
		return true;
	}

	public long getWaitingSurplus() {

		Calendar nowCal = GuaJiTime.getCalendar();
		// 筹备期
		long todaySecond = nowCal.getTimeInMillis() - GuaJiTime.getAM0Date().getTime();
		// 进行阶段
		int dayOfMonth = nowCal.get(Calendar.DAY_OF_MONTH);
		if (dayOfMonth == 1) {
			if (todaySecond < GvgCfg.getInstance().getDeclareStart())
				return GuaJiTime.getAM0Date().getTime() + GvgCfg.getInstance().getDeclareStart()
						- nowCal.getTimeInMillis();
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

					if (nowCal.getTime().getTime() > theLastReseTime) {

						resetCal.add(Calendar.MONTH, 1);
						resetCal.set(Calendar.DAY_OF_MONTH, 1);

						long endTime = GuaJiTime.getAM0Date(resetCal.getTime()).getTime()
								+ GvgCfg.getInstance().getDeclareStart();

						return endTime - nowCal.getTime().getTime();
					}

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
					if (nowCal.getTime().getTime() <= endTime && nowCal.getTime().getTime() >= beginTime) {
						return endTime - nowCal.getTime().getTime();
					}
				}
			}
		}
		return 0;
	}

	/**
	 * 生成赛季排名
	 */
	public void SeasonRankingCreate() {
		TreeMap<Integer, GvgCityEntity> cities = GvgService.getInstance().getCities();
		List<VitalityRankEntity> vitalityRanks = GvgService.getInstance().getVitalityRanks();
		// Gvg战斗结果列表
		List<GvgBattleResultBean> battleResultRanks = new ArrayList<GvgBattleResultBean>();
		// 发送邮件时间
		int rank = 1;
		for (VitalityRankEntity vitalityRank : vitalityRanks) {
			int allianceId = vitalityRank.getAllianceId();

			// 只取前15名
			if (rank > GvgCfg.getInstance().getRankNumber()) {
				break;
			}
			// 获取帮会编号
			AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
			if (allianceEntity == null)
				continue;

			// gvg结果
			GvgBattleResultBean itemBattleResult = new GvgBattleResultBean(allianceId, rank);

			for (GvgCityEntity cityEntity : cities.values()) {
				// 是否有帮会占领
				if (cityEntity.getHolderId() <= 0 || cityEntity.getHolderId() != allianceEntity.getId()) {
					continue;
				}

				// 获取奖励配置数据
				GvgCitiesCfg config = ConfigManager.getInstance().getConfigByKey(GvgCitiesCfg.class,
						cityEntity.getCityId());

				// 该城配置不存在或则是复活点
				if (config == null || config.getLevel() == 0)
					continue;

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

		}

		// 清除上季排名
		synchronized (lasteasonResultRanks) {
			for (GvgHistoryRankEntity historyRank : lasteasonResultRanks) {
				DBManager.getInstance().delete(historyRank);
			}
			lasteasonResultRanks.clear();
		}
		
		// 拥有城池排序
		int cityRank = 1;
		battleResultRanks.sort(new HoldeCityComparator());
		for (int i = 0; i < battleResultRanks.size(); i++) {

			AllianceEntity allianceEntity = AllianceManager.getInstance()
					.getAlliance(battleResultRanks.get(i).getAllianceId());
			if (allianceEntity == null)
				continue;
			
	
			//前三名发送跑马灯
			if (cityRank <= GvgCfg.getInstance().getGvgBroadRank()) {
				
				String msg = ChatManager.getMsgJson(GvgCfg.getInstance().getGvgSeasonEnd(),
						allianceEntity.getName(), cityRank);
				GsApp.getInstance().broadcastChatWorldMsg(msg, msg);
			}

			GvgHistoryRankEntity historyRank = GvgHistoryRankEntity.createEntity(allianceEntity.getName(),
					allianceEntity.getPlayerName(), allianceEntity.getId(), battleResultRanks.get(i).getCityNumInfo(),
					cityRank, battleResultRanks.get(i).getnTotalScore(), allianceEntity.getLevel());
			if (historyRank != null)
				lasteasonResultRanks.add(historyRank);

			cityRank++;
		}

	}

	/**
	 * 赛季结束清除所有城战数据
	 */
	public void clearCityData() {
		lock.lock();
		try {
			// 清除城战数据
			if (timeEntity != null) {
				DBManager.getInstance().delete(timeEntity);
				timeEntity = null;
			}
			// 清除占领城池信息
			for (GvgCityEntity cityEntity : cities.values()) {
				DBManager.getInstance().delete(cityEntity);
			}
			this.cities.clear();
			// 清除排名信息
			for (VitalityRankEntity vitalityRank : vitalityRanks) {
				DBManager.getInstance().delete(vitalityRank);
			}
			this.vitalityRanks.clear();
			// 清除奖励数据
			for (GvgCityRewardEntity cityReward : cityRewards.values()) {
				DBManager.getInstance().delete(cityReward);
			}
			this.cityRewards.clear();

		} finally {
			lock.unlock();
		}
	}

}
