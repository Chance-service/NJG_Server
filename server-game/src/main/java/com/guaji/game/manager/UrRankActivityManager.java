package com.guaji.game.manager;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.config.ActivityCfg.ActivityItem;
import com.guaji.game.GsApp;
import com.guaji.game.ServerData;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ReleaseURRank128AwardCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.ActivityEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.entity.UrRankActivityEntity;
import com.guaji.game.entity.UrRankHistoryEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.module.activity.activity128.Activity128Rank;
import com.guaji.game.module.activity.activity128.Activity128Status;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.protocol.Activity4.*;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Mail;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;

import com.guaji.game.util.GsonUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * UR抽奖积分排行管理
 */
public class UrRankActivityManager extends AppObj {

	private final int maxRankNum = SysBasicCfg.getInstance().getActivity128UrRankMaxNum();
	private final int maxRewardNum = 5;
	private final int maxDisplayNum = 10;

	/**
	 * 日志记录器
	 */
	private static Logger logger = LoggerFactory.getLogger("Server");

	/**
	 * 活动ID
	 */
	private final int activityId = Const.ActivityId.ACTIVITY128_UR_VALUE;

	/**
	 * 模块Tick周期
	 */
	private int tickIndex = 0;

	/**
	 * 当前活动对应的奖励发放数据
	 */
	private UrRankActivityEntity urRankActivityEntity = null;

	/**
	 * 当前活动对应的奖励发放数据
	 */
	private UrRankHistoryEntity urRankHistoryEntity = null;

	/**
	 * 所有玩家抽卡积分
	 */
	private List<Activity128Rank> rankList = new LinkedList<Activity128Rank>();

	/**
	 * 所有玩家活动数据及排行
	 */
	private Map<Integer, Integer> allPlayerIdRankMap = new ConcurrentHashMap<Integer, Integer>();

	/**
	 * 所有玩家历史抽卡积分
	 */
	private List<Activity128Rank> rankHistoryList = new LinkedList<Activity128Rank>();

	/**
	 * 历史数据及排行
	 */
	private Map<Integer, Activity128Rank> allPlayerIdRankHistoryMap = new ConcurrentHashMap<Integer, Activity128Rank>();

	/**
	 * 全局对象, 便于访问
	 */
	private static UrRankActivityManager instance = null;

	/**
	 * 获取全局实例对象
	 */
	public static UrRankActivityManager getInstance() {
		return instance;
	}

	public UrRankActivityManager(GuaJiXID xid) {

		super(xid);

		if (instance == null) {
			rankList = new ArrayList<>();
			instance = this;
		}
	}

	/**
	 * 初始化
	 */
	public void init() {

		// 加载正在进行的数据
		ActivityItem activityItem = ActivityManager.getInstance().getActiveActivityItem(activityId);
		if (activityItem != null) {
			ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
			if (timeConfig != null) {
				// 排行数据加载
				List<ActivityEntity<Activity128Status>> rankEntity = DBManager.getInstance().query(
						"from ActivityEntity where activityId = ? and stageId = ?", activityId,
						timeConfig.getStageId());
				List<Activity128Rank> allRankList = new LinkedList<Activity128Rank>();
				if (null != rankEntity && rankEntity.size() > 0) {
					Activity128Rank item = null;
					for (ActivityEntity<Activity128Status> activityEntity : rankEntity) {
						Activity128Status status = activityEntity.getActivityStatus(Activity128Status.class);
						if (status != null && status.getScore() > 0) {

							item = new Activity128Rank(activityEntity.getPlayerId(), status.getScore());
							allRankList.add(item);
						}
					}
				}
				// 排行
				Collections.sort(allRankList);
				// 将排行数据存放到全局变量
				int selNum = SysBasicCfg.getInstance().getActivity128UrRankMaxNum() > allRankList.size()
						? allRankList.size()
						: SysBasicCfg.getInstance().getActivity128UrRankMaxNum();
				if (selNum > 0)
					this.rankList.addAll(allRankList.subList(0, selNum));

				int index = 0;
				for (Activity128Rank item : this.rankList) {
					allPlayerIdRankMap.put(item.getPlayerId(), index);
					index++;
				}
				// 排行数据加载
				List<UrRankActivityEntity> urRankEntityList = DBManager.getInstance()
						.query("from UrRankActivityEntity where invalid = 0");

				if (urRankEntityList != null && urRankEntityList.size() != 0) {
					urRankActivityEntity = urRankEntityList.get(0);
				}
				// 排行数据加载
				List<UrRankHistoryEntity> urHistoryEntityList = DBManager.getInstance().query(
						"from UrRankHistoryEntity where stageId = ? and invalid = 0 order by createTime desc",
						timeConfig.getStageId());

				if (urHistoryEntityList != null && urHistoryEntityList.size() > 0) {
					urRankHistoryEntity = urHistoryEntityList.get(0);
				}
				this.refresh();
				this.refreshHistoryData();
			}
		}
	}

	/**
	 * 创建活动数据
	 */
	public void refresh() {

		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
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
			this.urRankActivityEntity = null;
		} else {
			if (this.urRankActivityEntity == null) {
				this.urRankActivityEntity = new UrRankActivityEntity();
				long refreshTime = GuaJiTime
						.getTimeHourMinute(SysBasicCfg.getInstance().getActivity128UrRankCalcTime());
				if (GuaJiTime.getMillisecond() >= refreshTime) {
					refreshTime += 24 * 60 * 60 * 1000;
				}
				this.urRankActivityEntity.setRefreshTime(refreshTime);
				DBManager.getInstance().create(urRankActivityEntity);
			}
		}
		Log.logPrintln("shoot start time ms: " + startTimeMillis);
	}

	/**
	 * 获得前N名数据构建
	 *
	 * @param topNumber
	 * @return
	 */
	public List<Activity128RankItem.Builder> getRankTop(int topNumber) {

		Map<Integer, Integer> everyRankRepeatMap = new HashMap<Integer, Integer>();
		List<Integer> scoreList = new LinkedList<Integer>();
		int totalNum = 0;
		int rankNum = 1;
		List<Activity128RankItem.Builder> list = new LinkedList<>();
		for (int i = 0; i < this.rankList.size(); i++) {
			if (this.rankList.get(i).getScore() == 0)
				continue;
			if (totalNum > maxDisplayNum) {
				break;
			}
			if (!scoreList.contains(this.rankList.get(i).getScore())) {
				scoreList.add(this.rankList.get(i).getScore());
				rankNum = everyRankRepeatMap.size() + 1;
			}

			everyRankRepeatMap.put(this.rankList.get(i).getPlayerId(), rankNum);
			totalNum++;
		}

		for (Integer key : everyRankRepeatMap.keySet()) {
			Activity128RankItem.Builder builder = Activity128RankItem.newBuilder();
			PlayerSnapshotInfo.Builder snapshot = SnapShotManager.getInstance().getPlayerSnapShot(key);
			if (snapshot != null) {
				builder.setProf(snapshot.getMainRoleInfo().getProf());
				builder.setProf(snapshot.getMainRoleInfo().getProf());
				builder.setHeaderId(snapshot.getPlayerInfo().getHeadIcon());
				builder.setName(snapshot.getMainRoleInfo().getName());
			}

			builder.setRank(everyRankRepeatMap.get(key));
			builder.setScore(getPlayerScore(key));

			builder.setPlayerId(key);
			list.add(builder);

		}
		return list;
	}

	public int getPlayerScore(int playerId) {
		for (int i = 0; i < this.rankList.size(); i++) {
			if (this.rankList.get(i).getPlayerId() == playerId)
				return this.rankList.get(i).getScore();
		}
		return 0;
	}

	public void refreshHistoryData() {
		if (urRankHistoryEntity != null) {
			String rankList = urRankHistoryEntity.getRanklist();
			this.rankHistoryList = GsonUtil.getJsonInstance().fromJson(rankList,
					new TypeToken<List<Activity128Rank>>() {
					}.getType());
			for (Activity128Rank item : this.rankHistoryList) {
				allPlayerIdRankHistoryMap.put(item.getPlayerId(), item);
			}
		}
	}

	public Activity128Rank getPlayerHistoryRank(int playerId) {

		Map<Integer, Activity128Rank> everyRankNumMap = new HashMap<Integer, Activity128Rank>();
		List<Integer> scoreList = new LinkedList<Integer>();
		int rankNum = 0;
		synchronized (this.rankHistoryList) {
			Activity128Rank temp = null;
			for (int i = 0; i < this.rankHistoryList.size(); i++) {
				if (this.rankHistoryList.get(i).getScore() == 0)
					continue;
				if (!scoreList.contains(this.rankHistoryList.get(i).getScore())) {
					scoreList.add(this.rankHistoryList.get(i).getScore());
					rankNum = everyRankNumMap.size() + 1;
				}
				temp = new Activity128Rank(this.rankHistoryList.get(i).getPlayerId(),
						this.rankHistoryList.get(i).getScore(), rankNum);
				everyRankNumMap.put(this.rankHistoryList.get(i).getPlayerId(), temp);
			}
		}

		if (everyRankNumMap.containsKey(playerId)) {
			return everyRankNumMap.get(playerId);
		} else {
			return null;
		}

	}

	public List<Activity128RankItem.Builder> getRankHistoryTop() {

		List<Activity128RankItem.Builder> list = new LinkedList<>();
		if (this.rankHistoryList != null) {
			Map<Integer, Integer> everyRankRepeatMap = new HashMap<Integer, Integer>();
			List<Integer> scoreList = new LinkedList<Integer>();
			int totalNum = 0;
			int rankNum = 0;
			for (int i = 0; i < this.rankHistoryList.size(); i++) {
				if (this.rankHistoryList.get(i).getScore() == 0)
					continue;
				if (totalNum > maxDisplayNum) {
					break;
				}
				if (!scoreList.contains(this.rankHistoryList.get(i).getScore())) {

					scoreList.add(this.rankHistoryList.get(i).getScore());
					rankNum = everyRankRepeatMap.size() + 1;

				}

				everyRankRepeatMap.put(this.rankHistoryList.get(i).getPlayerId(), rankNum);
				totalNum++;
			}

			for (Integer key : everyRankRepeatMap.keySet()) {
				Activity128RankItem.Builder builder = Activity128RankItem.newBuilder();
				PlayerSnapshotInfo.Builder snapshot = SnapShotManager.getInstance().getPlayerSnapShot(key);
				if (snapshot != null) {
					builder.setProf(snapshot.getMainRoleInfo().getProf());
					builder.setProf(snapshot.getMainRoleInfo().getProf());
					builder.setHeaderId(snapshot.getPlayerInfo().getHeadIcon());
					builder.setName(snapshot.getMainRoleInfo().getName());
				}

				builder.setRank(everyRankRepeatMap.get(key));
				builder.setScore(this.allPlayerIdRankHistoryMap.get(key).getScore());
				builder.setPlayerId(key);
				list.add(builder);
			}
		}
		return list;
	}

	public UrRankActivityEntity getUrRankActivityEntity() {
		return urRankActivityEntity;
	}

	public UrRankHistoryEntity getUrRankHistoryEntity() {
		return urRankHistoryEntity;
	}

	public Integer getPlayerRank(int playerId) {

		Map<Integer, Integer> everyRankNumMap = new HashMap<Integer, Integer>();
		List<Integer> scoreList = new LinkedList<Integer>();
		int rankNum = 0;
		synchronized (this.rankList) {
			for (int i = 0; i < this.rankList.size(); i++) {
				if (this.rankList.get(i).getScore() == 0)
					continue;
				if (!scoreList.contains(this.rankList.get(i).getScore())) {
					scoreList.add(this.rankList.get(i).getScore());
					rankNum = everyRankNumMap.size() + 1;
				}
				everyRankNumMap.put(this.rankList.get(i).getPlayerId(), rankNum);
			}

		}
		if (everyRankNumMap.containsKey(playerId)) {
			return everyRankNumMap.get(playerId);
		} else {
			return 0;
		}
	}

	/**
	 * 更新玩家排名
	 *
	 * @param player 玩家信息
	 */
	public void updateRankSet(PlayerData playerData) {

		ActivityItem activityItem = ActivityManager.getInstance().getActiveActivityItem(activityId);
		if (activityItem == null)
			return;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeConfig == null)
			return;
		synchronized (this.rankList) {
			PlayerEntity playerEntity = playerData.getPlayerEntity();
			int playerId = playerEntity.getId();
			Activity128Status status = ActivityUtil.getActivityStatus(playerData, activityId, timeConfig.getStageId(),
					Activity128Status.class);

			int playerScore = 0;
			if (status != null) {
				playerScore = status.getScore();
			}
			// 当前排名中所有用户数据
			int playerRank = Math.max(0, rankList.size() - 1);
			if (allPlayerIdRankMap.containsKey(playerId)) {
				playerRank = allPlayerIdRankMap.get(playerId);
				rankList.remove(playerRank);// 变化玩家移除榜单
			}
			Activity128Rank playerStatusRank = new Activity128Rank(playerData.getId(), playerScore);
			int curIndex = 0;
			for (; curIndex < this.rankList.size(); curIndex++) {
				Activity128Rank rankItem = this.rankList.get(curIndex);
				if (rankItem.getScore() < playerScore) {
					this.rankList.add(curIndex, playerStatusRank);// 加入到适当的位置
					break;
				}
			}
			if (curIndex == rankList.size() && rankList.size() < maxRankNum) {
				this.rankList.add(playerStatusRank);//
			}
			while (rankList.size() > maxRankNum) {// 超了从尾部开始移出
				rankList.remove(rankList.size() - 1);
			}

			// 历史排名比当前名称更靠前
			if (playerRank < curIndex) {
				Set<Map.Entry<Integer, Integer>> entrys = allPlayerIdRankMap.entrySet();
				for (Map.Entry<Integer, Integer> entry : entrys) {
					if (entry.getValue() >= playerRank && entry.getValue() <= curIndex) {
						allPlayerIdRankMap.remove(entry.getKey());
					}
				}
				// 变化的玩家名次向后排序
				for (int i = playerRank; i <= curIndex && i < rankList.size(); i++) {
					allPlayerIdRankMap.put(rankList.get(i).getPlayerId(), i);
				}

			} else if (playerRank > curIndex) {// 历史排名比当前排名更靠后
				Set<Map.Entry<Integer, Integer>> entrys = allPlayerIdRankMap.entrySet();
				for (Map.Entry<Integer, Integer> entry : entrys) {
					if (entry.getValue() <= playerRank && entry.getValue() >= curIndex) {
						allPlayerIdRankMap.remove(entry.getKey());
					}
				}
				// 变化的玩家名次向前移动
				for (int i = curIndex; i <= playerRank && i < rankList.size(); i++) {
					allPlayerIdRankMap.put(rankList.get(i).getPlayerId(), i);
				}
			} else {// 排名相同
				allPlayerIdRankMap.put(playerData.getId(), curIndex);
			}

		}
	}

	@Override
	public boolean onTick() {
		ActivityItem activityItem = ActivityManager.getInstance().getActiveActivityItem(activityId);
		if (activityItem == null)
			return true;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeConfig == null)
			return true;

		if (++tickIndex % 100 == 0) {

			if (this.urRankActivityEntity == null)
				return true;
			long nextFreshTime = this.urRankActivityEntity.getRefreshTime();
			// 重置数据
			if (GuaJiTime.getMillisecond() > nextFreshTime) {
					logger.info("reset UrRankActivityManager rankList before");
				synchronized (this.rankList) {
					logger.info("reset UrRankActivityManager rankList after");
					long refreshTime = GuaJiTime
							.getTimeHourMinute(SysBasicCfg.getInstance().getActivity128UrRankCalcTime());
					if (GuaJiTime.getMillisecond() >= refreshTime) {
						refreshTime += 24 * 60 * 60 * 1000;
					}
					// 发奖
					this.sendRankAwards(timeConfig.getStageId());
					// 更新历史排名
					this.urRankActivityEntity.setRefreshTime(refreshTime);
					this.urRankActivityEntity.notifyUpdate();

					this.rankList.clear();
					this.allPlayerIdRankMap.clear();

				}

			}
		}
		return true;

	}

	@Override
	public boolean onMessage(Msg msg) {
		if (msg.getMsg() == GsConst.MsgType.UR_RANK_ADD_SCORE) {

			if (msg.getParams().size() < 2) {
				return true;
			}
			Player player = msg.getParam(1);
			this.updateRankSet(player.getPlayerData());
		}
		return true;
	}

	/**
	 * 发放排名奖励
	 */
	private void sendRankAwards(int stageId) {
		Map<Integer, Integer> everyRankNumMap = new HashMap<Integer, Integer>();
		List<Integer> scoreList = new LinkedList<Integer>();
		int rankNum = 0;
		for (int i = 0; i < this.rankList.size(); i++) {
			if (this.rankList.get(i).getScore() == 0)
				continue;
			if (!scoreList.contains(this.rankList.get(i).getScore())) {
				scoreList.add(this.rankList.get(i).getScore());
				rankNum = everyRankNumMap.size() + 1;
			}

			if (rankNum > maxRewardNum)
				break;

			everyRankNumMap.put(this.rankList.get(i).getPlayerId(), rankNum);
		}
		// 发奖
		for (Integer key : everyRankNumMap.keySet()) {
			ReleaseURRank128AwardCfg cfg = ConfigManager.getInstance().getConfigByKey(ReleaseURRank128AwardCfg.class,
					everyRankNumMap.get(key));
			if (cfg == null)
				continue;
			AwardItems everydayAward = AwardItems.valueOf(cfg.getAwardStr());
			// 发送邮件时间
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String date = sdf.format(GuaJiTime.getAM0Date());
			MailManager.createMail(key, Mail.MailType.Reward_VALUE, GsConst.MailId.UR_RANKMANAGER_MAIL, "UR积分排名奖励",
					everydayAward, date, String.valueOf(everyRankNumMap.get(key)));

		}

		// 生成历史排行
		List<Activity128Rank> historyRankList = new LinkedList<Activity128Rank>();
		Activity128Rank item = null;
		for (int i = 0; i < this.rankList.size(); i++) {

			item = new Activity128Rank(this.rankList.get(i).getPlayerId(), this.rankList.get(i).getScore(), i + 1);
			historyRankList.add(item);
		}
		String historyList = JSONArray.fromObject(historyRankList).toString();
		JSONArray jsonArr = new JSONArray();
		for (Integer playerId : everyRankNumMap.keySet()) {
			JSONObject object = new JSONObject();
			object.put("playerId", playerId);
			object.put("rank", everyRankNumMap.get(playerId));
			jsonArr.add(object);
		}
		UrRankHistoryEntity lastUrRankHistoryEntity = new UrRankHistoryEntity(stageId, historyList, jsonArr.toString());
		if (lastUrRankHistoryEntity != null) {
			urRankHistoryEntity = lastUrRankHistoryEntity;
			DBManager.getInstance().create(lastUrRankHistoryEntity);
			refreshHistoryData();
		}
	}

}