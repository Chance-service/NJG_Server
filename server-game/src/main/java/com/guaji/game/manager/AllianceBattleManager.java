package com.guaji.game.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;
import org.guaji.thread.GuaJiTask;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.GsConfig;
import com.guaji.game.battle.BattleRole;
import com.guaji.game.battle.Battlefield;
import com.guaji.game.config.AllianceBattleBuffCfg;
import com.guaji.game.config.AllianceBattleInspireCfg;
import com.guaji.game.config.AllianceBattleRewardCfg;
import com.guaji.game.config.AllianceBattleTimeCfg;
import com.guaji.game.config.InvestRewardCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.AllianceBattleInfo;
import com.guaji.game.entity.AllianceBattleItem;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.AllianceFightUnit;
import com.guaji.game.entity.AllianceFightVersus;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.AllianceBattle.AllianceBattleState;
import com.guaji.game.protocol.AllianceBattle.FightGroup;
import com.guaji.game.protocol.AllianceBattle.HPAFMainEnterSync;
import com.guaji.game.protocol.AllianceBattle.HPAllianceDrawRet;
import com.guaji.game.protocol.AllianceBattle.HPAllianceTeamFightRet;
import com.guaji.game.protocol.Battle;
import com.guaji.game.protocol.Battle.BattleInfo;
import com.guaji.game.protocol.Const.attr;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Mail;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.AllianceUtil;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GsConst;

/**
 * 公会争霸赛
 */
public class AllianceBattleManager extends AppObj {

	private static AllianceBattleManager instance;

	public static AllianceBattleManager getInstance() {
		return instance;
	}

	public static Map<AllianceBattleState, FightGroup> GROUP_REGISTER_MAP = new HashMap<>();

	static {
		GROUP_REGISTER_MAP.put(AllianceBattleState.FS32_16_FIGHTING, FightGroup.GROUP_32);
		GROUP_REGISTER_MAP.put(AllianceBattleState.FS16_8_FIGHTING, FightGroup.GROUP_16);
		GROUP_REGISTER_MAP.put(AllianceBattleState.FS8_4_FIGHTING, FightGroup.GROUP_8);
		GROUP_REGISTER_MAP.put(AllianceBattleState.FS4_2_FIGHTING, FightGroup.GROUP_4);
		GROUP_REGISTER_MAP.put(AllianceBattleState.FS2_1_FIGHTING, FightGroup.GROUP_2);
		GROUP_REGISTER_MAP.put(AllianceBattleState.Draw_Lots_WAIT, FightGroup.GROUP_32);
		GROUP_REGISTER_MAP.put(AllianceBattleState.Publicity_WAIT, FightGroup.GROUP_32);
		GROUP_REGISTER_MAP.put(AllianceBattleState.FS16_8_WAIT, FightGroup.GROUP_16);
		GROUP_REGISTER_MAP.put(AllianceBattleState.FS8_4_WAIT, FightGroup.GROUP_8);
		GROUP_REGISTER_MAP.put(AllianceBattleState.FS4_2_WAIT, FightGroup.GROUP_4);
		GROUP_REGISTER_MAP.put(AllianceBattleState.FS2_1_WAIT, FightGroup.GROUP_2);
	}

	public static Map<FightGroup, Integer> GROUP_TOP_MAP = new HashMap<>();

	static {
		GROUP_TOP_MAP.put(FightGroup.GROUP_32, GsConst.AllianceBattle.TOP_32);
		GROUP_TOP_MAP.put(FightGroup.GROUP_16, GsConst.AllianceBattle.TOP_16);
		GROUP_TOP_MAP.put(FightGroup.GROUP_8, GsConst.AllianceBattle.TOP_8);
		GROUP_TOP_MAP.put(FightGroup.GROUP_4, GsConst.AllianceBattle.TOP_4);
		GROUP_TOP_MAP.put(FightGroup.GROUP_2, GsConst.AllianceBattle.TOP_2);
	}

	public static Map<Integer, FightGroup> TOP_GROUP_MAP = new HashMap<>();

	static {
		TOP_GROUP_MAP.put(GsConst.AllianceBattle.TOP_32, FightGroup.GROUP_32);
		TOP_GROUP_MAP.put(GsConst.AllianceBattle.TOP_16, FightGroup.GROUP_16);
		TOP_GROUP_MAP.put(GsConst.AllianceBattle.TOP_8, FightGroup.GROUP_8);
		TOP_GROUP_MAP.put(GsConst.AllianceBattle.TOP_4, FightGroup.GROUP_4);
		TOP_GROUP_MAP.put(GsConst.AllianceBattle.TOP_2, FightGroup.GROUP_2);
		TOP_GROUP_MAP.put(GsConst.AllianceBattle.TOP_1, FightGroup.GROUP_2);
	}

	/**
	 * 当前争霸赛的阶段
	 */
	private AllianceBattleState curBattleState;

	/**
	 * 所有公会的排序集合
	 */
	private SortedSet<AllianceBattleItem> battleItemSet;

	private Map<Integer, AllianceBattleItem> battleItemMap = new ConcurrentHashMap<Integer, AllianceBattleItem>();

	private static Comparator<AllianceBattleItem> battleItemComparator = new Comparator<AllianceBattleItem>() {

		@Override
		public int compare(AllianceBattleItem o1, AllianceBattleItem o2) {
			if (o1.getAllianceId() == o2.getAllianceId()) {
				return 0;
			}
			if (o1.getVitality() != o2.getVitality()) {
				return o2.getVitality() - o1.getVitality();
			}

			if (o1.getAllianceLevel() != o2.getAllianceLevel()) {
				return o2.getAllianceLevel() - o1.getAllianceLevel();
			}

			if (o1.getAllianceExp() != o2.getAllianceExp()) {
				return o2.getAllianceExp() - o1.getAllianceExp();
			}

			return o1.getAllianceId() - o2.getAllianceId();
		}
	};

	/**
	 * 当前工会战的期数
	 */
	private int curStageId = 0;

	/**
	 * 上期
	 */
	private int lastStageId = 0;

	/**
	 * 当前工会战的信息
	 */
	private AllianceBattleInfo curAllianceBattleInfo;

	/**
	 * 对阵表
	 */
	private AllianceBattleAgainstInfo battleAgainstInfo;

	/**
	 * 上一期对阵表
	 */
	private AllianceBattleAgainstInfo lastBattleAgainstInfo;

	/**
	 * 历届冠军Id
	 */
	private List<Integer> allChampionAllianceIdList;

	public AllianceBattleManager(GuaJiXID xid) {
		super(xid);
		this.battleItemSet = Collections.synchronizedSortedSet(new TreeSet<>(battleItemComparator));
		instance = this;
	}

	/**
	 * 是否允许开启
	 * 
	 * @return
	 */
	public boolean isAllowOpen() {
		if (this.curAllianceBattleInfo == null) {
			Calendar calendar = GuaJiTime.getFirstDayCalendarOfCurWeek();
			long firstDayTime = calendar.getTimeInMillis();
			calendar.add(Calendar.DATE, SysBasicCfg.getInstance().getAllowAllianceBattleOpenWeekDay());
			Date startDate = calendar.getTime();
			calendar.add(Calendar.DATE, 7 - SysBasicCfg.getInstance().getAllowAllianceBattleOpenWeekDay());
			Date endDate = calendar.getTime();

			long curTime = GuaJiTime.getMillisecond();
			if (curTime >= startDate.getTime() && curTime <= endDate.getTime()) {
				return false;
			}

			Date serverOpenDate = GsConfig.getInstance().getServerOpenDate();
			if (serverOpenDate != null) {
				long serOpenFirstDayTime = GuaJiTime.getFirstDayOfWeek(serverOpenDate).getTime();
				if (firstDayTime == serOpenFirstDayTime) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * 捞取历届冠军Id
	 */
	public void loadChampionAllianceId() {
		allChampionAllianceIdList = DBManager.getInstance().executeQuery(
				"select allianceId from alliance_battle_item where battleResult = 1 and createTime > '2015-01-12 00:00:00'");
	}

	/**
	 * 初始化公会争霸信息，依赖公会信息加载
	 */
	public void init() {
		loadChampionAllianceId();
		// 计算当前期号
		this.curStageId = Integer.valueOf(GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getFirstDayOfCurWeek()));
		this.setBattleAgainstInfo(new AllianceBattleAgainstInfo(curStageId));
		// 计算上一期期号
		Calendar calendar = GuaJiTime.getCalendar();
		calendar.add(Calendar.DATE, -7);
		this.lastStageId = Integer.valueOf(GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getFirstDayOfWeek(calendar)));
		this.setLastBattleAgainstInfo(new AllianceBattleAgainstInfo(lastStageId));
		// 加载上期数据
		loadLastStageData();
		// 从数据库捞取数据

		// 把元气排行捞取进来
		List<AllianceBattleItem> battleItems = DBManager.getInstance().query("from AllianceBattleItem where stageId = ? and invalid = 0", getCurStageId());
		for (AllianceBattleItem battleItem : battleItems) {
			battleItem.convertData();
			this.battleItemMap.put(battleItem.getAllianceId(), battleItem);
			// 不自动分配队伍
			// battleItem.autoJoinTeam();
			this.battleItemSet.add(battleItem);
		}
		this.getBattleAgainstInfo().addBattleItems(this.battleItemSet);

		// 捞取基础数据
		this.curAllianceBattleInfo = DBManager.getInstance().fetch(AllianceBattleInfo.class, "from AllianceBattleInfo where stageId = ? and invalid = 0",
				getCurStageId());
		if (this.curAllianceBattleInfo == null) {
			if (isAllowOpen()) {
				this.curAllianceBattleInfo = new AllianceBattleInfo();
				this.curAllianceBattleInfo.setStageId(this.getCurStageId());
				this.curAllianceBattleInfo.setState(AllianceBattleState.PREPARE_VALUE);
				DBManager.getInstance().create(this.curAllianceBattleInfo);
			}
		} else {
			this.curAllianceBattleInfo.convertData();
		}
		// 先把公会按等级排序
		List<AllianceEntity> allianceEntities = new LinkedList<>();
		allianceEntities.addAll(AllianceManager.getInstance().getAllianceMap().values());
		Collections.sort(allianceEntities, AllianceUtil.SORTALLIANCE);
		if (allianceEntities.size() >= GsConst.AllianceBattle.ALLIANCE_BATTLE_RANK_SIZE) {
			int index = 0;
			while (this.battleItemSet.size() < GsConst.AllianceBattle.ALLIANCE_BATTLE_RANK_SIZE) {
				AllianceEntity allianceEntity = allianceEntities.get(index);
				index++;
				if (this.battleItemMap.containsKey(allianceEntity.getId())) {
					continue;
				}
				this.addAllianceVitality(allianceEntity.getId(), 0);
				this.battleItemMap.get(allianceEntity.getId()).notifyUpdate(true);
			}
		}
		// 捞取对阵表
		List<AllianceFightVersus> fightVersusList = DBManager.getInstance().query("from AllianceFightVersus where stageId = ? and invalid = 0",
				getCurStageId());
		for (AllianceFightVersus fightVersus : fightVersusList) {
			fightVersus.convertData();
			this.getBattleAgainstInfo().addFightVersus(fightVersus);
		}
		// 捞取对战具体信息
		List<AllianceFightUnit> fightUnitList = DBManager.getInstance().query("from AllianceFightUnit where stageId = ? and invalid = 0", getCurStageId());
		for (AllianceFightUnit fightUnit : fightUnitList) {
			AllianceFightVersus versus = this.getBattleAgainstInfo().getAllianceFightVersus(fightUnit.getVersusId());
			if (versus != null) {
				fightUnit.convertData();
				versus.addFightUnit(fightUnit);
			}
		}
	}

	/**
	 * 按等级排名填充
	 */
	private void fillBattleItemsWithLevel() {
		List<AllianceEntity> allianceEntities = new LinkedList<>();
		allianceEntities.addAll(AllianceManager.getInstance().getAllianceMap().values());
		Collections.sort(allianceEntities, AllianceUtil.SORTALLIANCE);
		int index = 0;
		for (AllianceEntity allianceEntity : allianceEntities) {
			if (index >= GsConst.AllianceBattle.ALLIANCE_BATTLE_RANK_SIZE) {
				break;
			}
			if (allianceEntity != null) {
				this.addAllianceVitality(allianceEntity.getId(), 0);
				index++;
			}
		}
	}

	private void loadLastStageData() {
		// 捞取对阵表
		List<AllianceFightVersus> fightVersusList = DBManager.getInstance().query("from AllianceFightVersus where stageId = ? and invalid = 0 ",
				lastStageId);
		for (AllianceFightVersus fightVersus : fightVersusList) {
			fightVersus.convertData();
			this.getLastBattleAgainstInfo().addFightVersus(fightVersus);
		}
		// 捞取对战具体信息
		List<AllianceFightUnit> fightUnitList = DBManager.getInstance().query("from AllianceFightUnit where stageId = ? and invalid = 0", lastStageId);
		for (AllianceFightUnit fightUnit : fightUnitList) {
			AllianceFightVersus versus = this.getLastBattleAgainstInfo().getAllianceFightVersus(fightUnit.getVersusId());
			if (versus != null) {
				fightUnit.convertData();
				versus.addFightUnit(fightUnit);
			}
		}
		// 把上期元气排行捞取进来
		List<AllianceBattleItem> battleItems = DBManager.getInstance().query("from AllianceBattleItem where stageId = ? and invalid = 0", lastStageId);
		for (AllianceBattleItem battleItem : battleItems) {
			battleItem.convertData();
		}
		this.lastBattleAgainstInfo.addBattleItems(battleItems);
	}

	private int tickIndex = 0;

	@Override
	public boolean onTick() {
		if (tickIndex++ % 100 == 0) {
			this.curStageId = Integer.valueOf(GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getFirstDayOfCurWeek()));
			// 计算当前阶段
			this.curBattleState = calcCurBattleState();

			if (this.curAllianceBattleInfo == null && curBattleState != null && this.curBattleState != AllianceBattleState.PREPARE) {
				return false;
			}

			if (this.curAllianceBattleInfo != null) {
				if(this.curStageId != curAllianceBattleInfo.getStageId() && !GuaJiTime.isFirstDayOfWeek(curStageId)) {
					Log.logPrintln("此处就是bug的所发生的地方 curStageId | " + this.curStageId + " | curAllianceBattleInfoStageId | " +curAllianceBattleInfo.getStageId());
					return false;
				}
				if (this.curAllianceBattleInfo.getState() < this.curBattleState.getNumber()) {
					// 需要跳阶段
					AllianceBattleState nextState = AllianceBattleTimeCfg.getNextBattleState(AllianceBattleState.valueOf(this.curAllianceBattleInfo.getState()));
					if (nextState == null) {
						return false;
					}
					if (nextState == AllianceBattleState.Draw_Lots_WAIT || this.battleItemSet.size() >= GsConst.AllianceBattle.ALLIANCE_BATTLE_RANK_SIZE) {
						dealWithState(nextState);
					}
					this.curAllianceBattleInfo.setState(nextState.getNumber());
					this.curAllianceBattleInfo.notifyUpdate(true);
				}
			}

			// 表示开启下一期
			if ((this.curAllianceBattleInfo == null || this.curAllianceBattleInfo.getState() > AllianceBattleState.PREPARE_VALUE)
					&& this.curBattleState == AllianceBattleState.PREPARE) {
				// 先捞取所有冠军Id
				loadChampionAllianceId();

				this.setLastBattleAgainstInfo(getBattleAgainstInfo());
				this.setBattleAgainstInfo(new AllianceBattleAgainstInfo(this.curStageId));
				// 创建BattleInfo
				this.curAllianceBattleInfo = DBManager.getInstance().fetch(AllianceBattleInfo.class,
						"from AllianceBattleInfo where stageId = ? and invalid = 0", getCurStageId());
				if (this.curAllianceBattleInfo == null) {
					this.curAllianceBattleInfo = new AllianceBattleInfo();
					this.curAllianceBattleInfo.setStageId(this.getCurStageId());
					this.curAllianceBattleInfo.setState(AllianceBattleState.PREPARE_VALUE);
					DBManager.getInstance().create(this.curAllianceBattleInfo);
				} else {
					this.curAllianceBattleInfo.setStageId(this.getCurStageId());
					this.curAllianceBattleInfo.setState(AllianceBattleState.PREPARE_VALUE);
					this.curAllianceBattleInfo.notifyUpdate(true);
				}
				// 清理元气排行
				this.battleItemSet.clear();
				this.battleItemMap.clear();
				// 按当前等级排名填充
				fillBattleItemsWithLevel();
			}
		}
		return false;
	}

	/**
	 * 如果战斗的时候发现之前已有脏数据 并且无法判断是否打完时 清除之前的战斗数据，重新开始
	 */
	private void clearPreDirtyVersusData(FightGroup group) {
		List<AllianceFightVersus> versusList = this.getBattleAgainstInfo().getFightVersusByGroup(group);
		if (versusList != null && versusList.size() > 0) {
			for (AllianceFightVersus versus : versusList) {
				for (AllianceFightUnit fightUnit : versus.getFightUnits()) {
					fightUnit.delete();
				}
				versus.getFightUnits().clear();
				versus.delete();
			}
			versusList.clear();
		}
	}

	/**
	 * 如果战斗的时候发现之前已有脏数据 并且无法判断是否打完时 清除之前的战斗数据，重新开始
	 */
	private void clearPreDirtyFightData(FightGroup group) {
		List<AllianceFightVersus> versusList = this.getBattleAgainstInfo().getFightVersusByGroup(group);
		if (versusList != null && versusList.size() > 0) {
			for (AllianceFightVersus versus : versusList) {
				for (AllianceFightUnit fightUnit : versus.getFightUnits()) {
					fightUnit.delete();
				}
				versus.getFightUnits().clear();
			}
		}
	}

	/**
	 * 处理各个阶段的操作
	 * 
	 * @param nextState
	 */
	private boolean dealWithState(AllianceBattleState nextState) {
		switch (nextState) {
		// 抽签阶段
		case Draw_Lots_WAIT:
			// 停止元气积累，算出前32名
			List<AllianceBattleItem> topBattleItems = getAllianceBattleItems();
			if (topBattleItems.size() < GsConst.AllianceBattle.ALLIANCE_BATTLE_RANK_SIZE) {
				// 公会不够
				return false;
			}
			// 每个公会自动分配一下队伍
			for (AllianceBattleItem battleItem : topBattleItems) {
				battleItem.autoJoinTeam();
				battleItem.notifyUpdate(true);
			}
			clearPreDirtyVersusData(FightGroup.GROUP_32);
			Set<AllianceBattleItem> itemSet = new HashSet<>();
			itemSet.addAll(this.battleItemSet);
			itemSet.removeAll(topBattleItems);
			// 发放资格赛（没有进入32强的）奖励
			deliverRewards(itemSet, GsConst.AllianceBattle.TOP_COMMON);
			this.getBattleAgainstInfo().addVersusList(FightGroup.GROUP_32_VALUE,
					this.curAllianceBattleInfo.calcFightVersus(FightGroup.GROUP_32, topBattleItems));
			this.curAllianceBattleInfo.notifyUpdate(true);
			// 发入选32强的邮件
			sendMailNotice(topBattleItems);
			break;
		case FS32_16_FIGHTING:
			// 32进16的投资结束 开始打斗
			// 战斗
			clearPreDirtyFightData(FightGroup.GROUP_32);
			List<Integer> winIds = fight(FightGroup.GROUP_32);
			clearPreDirtyVersusData(FightGroup.GROUP_16);
			this.getBattleAgainstInfo().addVersusList(FightGroup.GROUP_16_VALUE, this.curAllianceBattleInfo.calcFightVersusByIds(FightGroup.GROUP_16, winIds));
			break;
		case FS16_8_WAIT:
			// 发送奖励
			deliverGroupRewards(FightGroup.GROUP_32, GsConst.AllianceBattle.TOP_32);
			deliverGroupInvestReward(FightGroup.GROUP_32, getRewardTimeStrByBattleState(nextState));
			break;
		case FS16_8_FIGHTING:
			clearPreDirtyFightData(FightGroup.GROUP_16);
			winIds = fight(FightGroup.GROUP_16);
			clearPreDirtyVersusData(FightGroup.GROUP_8);
			this.getBattleAgainstInfo().addVersusList(FightGroup.GROUP_8_VALUE, this.curAllianceBattleInfo.calcFightVersusByIds(FightGroup.GROUP_8, winIds));
			break;
		case FS8_4_WAIT:
			// 发送奖励
			deliverGroupRewards(FightGroup.GROUP_16, GsConst.AllianceBattle.TOP_16);
			deliverGroupInvestReward(FightGroup.GROUP_16, getRewardTimeStrByBattleState(nextState));
			break;
		case FS8_4_FIGHTING:
			clearPreDirtyFightData(FightGroup.GROUP_8);
			winIds = fight(FightGroup.GROUP_8);
			clearPreDirtyVersusData(FightGroup.GROUP_4);
			this.getBattleAgainstInfo().addVersusList(FightGroup.GROUP_4_VALUE, this.curAllianceBattleInfo.calcFightVersusByIds(FightGroup.GROUP_4, winIds));
			break;
		case FS4_2_WAIT:
			// 发送奖励
			deliverGroupRewards(FightGroup.GROUP_8, GsConst.AllianceBattle.TOP_8);
			deliverGroupInvestReward(FightGroup.GROUP_8, getRewardTimeStrByBattleState(nextState));
			break;
		case FS4_2_FIGHTING:
			clearPreDirtyFightData(FightGroup.GROUP_4);
			winIds = fight(FightGroup.GROUP_4);
			clearPreDirtyVersusData(FightGroup.GROUP_2);
			this.getBattleAgainstInfo().addVersusList(FightGroup.GROUP_2_VALUE, this.curAllianceBattleInfo.calcFightVersusByIds(FightGroup.GROUP_2, winIds));
			break;
		case FS2_1_WAIT:
			// 发送奖励
			deliverGroupRewards(FightGroup.GROUP_4, GsConst.AllianceBattle.TOP_4);
			deliverGroupInvestReward(FightGroup.GROUP_4, getRewardTimeStrByBattleState(nextState));
			break;
		case FS2_1_FIGHTING:
			clearPreDirtyFightData(FightGroup.GROUP_2);
			winIds = fight(FightGroup.GROUP_2);
			if(winIds.size()>0)
			{
				// 产生冠军
				this.curAllianceBattleInfo.setChampion(winIds.get(0));
				List<AllianceFightVersus> versusList = this.getBattleAgainstInfo().getFightVersusByGroup(FightGroup.GROUP_2);
				AllianceFightVersus versus = versusList.get(0);
				int runnerUpId = versus.getLeftId() == winIds.get(0) ? versus.getRightId() : versus.getLeftId();
				this.curAllianceBattleInfo.setRunnerUp(runnerUpId);
				this.curAllianceBattleInfo.notifyUpdate(true);
			}
			break;
		case SHOW_TIME:
			// 发送奖励
			deliverGroupRewards(FightGroup.GROUP_2, GsConst.AllianceBattle.TOP_2);
			deliverGroupInvestReward(FightGroup.GROUP_2, getRewardTimeStrByBattleState(nextState));
			break;
		default:
			break;
		}

		return true;
	}

	private void deliverGroupRewards(FightGroup group, int top) {
		List<AllianceFightVersus> versusList = this.getBattleAgainstInfo().getFightVersusByGroup(group);
		if (versusList != null) {
			Set<AllianceBattleItem> battleItems = new HashSet<>();

			AllianceBattleItem championBattleItem = null;
			for (AllianceFightVersus versus : versusList) {
				if (versus != null && versus.getFailId() > 0) {
					battleItems.add(getBattleItem(versus.getFailId()));
				}

				if (top == GsConst.AllianceBattle.TOP_2) {
					championBattleItem = getBattleItem(versus.getWinId());
				}
			}

			deliverRewards(battleItems, top);
			// 冠军特殊处理
			if (championBattleItem != null) {
				sendSingleRewards(championBattleItem, GsConst.AllianceBattle.TOP_1, getRewardTimeStr(GsConst.AllianceBattle.TOP_2));
			}
		}
	}

	/**
	 * 获得发放时间点的字符串
	 * 
	 * @param top
	 * @return
	 */
	private String getRewardTimeStr(int top) {
		if (top == GsConst.AllianceBattle.TOP_COMMON) {
			return getRewardTimeStrByBattleState(AllianceBattleState.Draw_Lots_WAIT);
		} else if (top == GsConst.AllianceBattle.TOP_32) {
			return getRewardTimeStrByBattleState(AllianceBattleState.FS16_8_WAIT);
		} else if (top == GsConst.AllianceBattle.TOP_16) {
			return getRewardTimeStrByBattleState(AllianceBattleState.FS8_4_WAIT);
		} else if (top == GsConst.AllianceBattle.TOP_8) {
			return getRewardTimeStrByBattleState(AllianceBattleState.FS4_2_WAIT);
		} else if (top == GsConst.AllianceBattle.TOP_4) {
			return getRewardTimeStrByBattleState(AllianceBattleState.FS2_1_WAIT);
		} else if (top == GsConst.AllianceBattle.TOP_2) {
			return getRewardTimeStrByBattleState(AllianceBattleState.SHOW_TIME);
		}
		return "";
	}

	private String getRewardTimeStrByBattleState(AllianceBattleState battleState) {
		Date startDate = AllianceBattleTimeCfg.getCfg(battleState.getNumber()).getStartSpecifiedDate(this.curStageId);
		if (startDate == null) {
			return "";
		} else {
			return GuaJiTime.getTimeString(startDate);
		}
	}

	/**
	 * 发送结果奖励
	 * 
	 * @param itemSet
	 * @param topCommon
	 */
	private void deliverRewards(Set<AllianceBattleItem> battleItems, int top) {
		if (battleItems != null) {
			for (AllianceBattleItem battleItem : battleItems) {
				try {
					sendSingleRewards(battleItem, top, getRewardTimeStr(top));
				} catch (Exception e) {
					MyException.catchException(e);
				}
			}
		}
	}

	/**
	 * 发送中签32强的邮件
	 * 
	 * @param topBattleItems
	 */
	private void sendMailNotice(List<AllianceBattleItem> topBattleItems) {
		if (topBattleItems != null) {
			for (AllianceBattleItem battleItem : topBattleItems) {
				try {
					AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(battleItem.getAllianceId());
					if (allianceEntity == null) {
						Log.logPrintln("alliance not found , allianceId : " + battleItem.getAllianceId());
						return;
					}

					MailManager.createMail(allianceEntity.getPlayerId(), Mail.MailType.Normal_VALUE, GsConst.MailId.ALLIANCE_BATTLE_SELECTED, "给公会会长发送公会战中签资格通知邮件", null);
				} catch (Exception e) {
					MyException.catchException(e);
				}
			}
		}
	}

	private void sendSingleRewards(AllianceBattleItem battleItem, int top, String dateStr) {
		AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(battleItem.getAllianceId());
		if (allianceEntity == null) {
			Log.logPrintln("alliance not found , allianceId : " + battleItem.getAllianceId());
			return;
		}

		List<Integer> memberList = battleItem.getMemberList();
		if (memberList == null) {
			Log.logPrintln("alliance member list not found , allianceId : " + battleItem.getAllianceId());
			return;
		}

		for (int playerId : memberList) {
			if (!allianceEntity.getMemberList().contains(Integer.valueOf(playerId))) {
				continue;
			}

			PlayerSnapshotInfo.Builder snapShotBuilder = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
			if (snapShotBuilder == null) {
				Log.logPrintln("deliver alliance battle result reward failed , playerId: " + playerId);
				continue;
			}

			AllianceBattleRewardCfg rewardConfig = AllianceBattleRewardCfg.getRewardCfg(top, snapShotBuilder.getAllianceInfo().getPostion());
			if (rewardConfig == null) {
				Log.logPrintln("allianceBattleRewardCfg not found , top: " + top + " ,position: " + snapShotBuilder.getAllianceInfo().getPostion());
				continue;
			}

			Log.logPrintln("send alliance battle reward success ,top : " + top + " ,allianceId: " + allianceEntity.getId() + " ,playerId: " + playerId);

			// 看是否有额外的鼓舞奖励发放
			FightGroup fightGroup = TOP_GROUP_MAP.get(top);
			AwardItems awardItems = new AwardItems();
			String rewardStr = rewardConfig.getReward();
			List<ItemInfo> itemInfos = ItemInfo.valueListOf(rewardStr);
			int[] extraRatio = new int[] { 0, 0, 0 };
			if (fightGroup != null) {
				for (int i = 1; i <= fightGroup.getNumber(); i++) {
					int inspireTimes = battleItem.getInspireTimes(playerId, i);
					for (int j = 1; j <= inspireTimes; j++) {
						AllianceBattleInspireCfg inspireCfg = AllianceBattleInspireCfg.getInspireCfg(fightGroup.getNumber(), j);
						extraRatio[0] += inspireCfg.getExtraReward1();
						extraRatio[1] += inspireCfg.getExtraReward2();
						extraRatio[2] += inspireCfg.getExtraReward3();
					}
				}
			}

			int buffId = battleItem.getBuffId();
			if (buffId > 0) {
				AllianceBattleBuffCfg allianceBattleBuffCfg = ConfigManager.getInstance().getConfigByKey(AllianceBattleBuffCfg.class, buffId);
				if (allianceBattleBuffCfg != null) {
					extraRatio[0] += allianceBattleBuffCfg.getExtraReward1();
					extraRatio[1] += allianceBattleBuffCfg.getExtraReward2();
					extraRatio[2] += allianceBattleBuffCfg.getExtraReward3();
				}
			}

			ItemInfo itemInfo = null;
			for (int i = 0; i < itemInfos.size(); i++) {
				itemInfo = itemInfos.get(i);
				if (itemInfo != null && i < extraRatio.length) {
					if (i == 0) {
						// 第一个是百分比
						itemInfo.setQuantity((int) (itemInfo.getQuantity() * ((float) extraRatio[i] / 10000 + 1)));
					} else {
						// 后面的是数量
						itemInfo.setQuantity(itemInfo.getQuantity() + extraRatio[i]);
					}
				}
				awardItems.addItem(itemInfo);
			}

			MailManager.createMail(playerId, Mail.MailType.Reward_VALUE, GsConst.MailId.ALLIANCE_BATTLE_RESULT_REWARD, "", awardItems, dateStr,
					allianceEntity.getName(), rewardConfig.getGameType(), rewardConfig.getPositionShow());
		}
	}

	/**
	 * 分发投资奖励
	 * 
	 * @param group
	 */
	private void deliverGroupInvestReward(final FightGroup group, final String dateStr) {
		final List<AllianceFightVersus> versusList = this.getBattleAgainstInfo().getFightVersusByGroup(group);
		if (versusList != null) {
			GsApp.getInstance().postCommonTask(new GuaJiTask() {
				@Override
				protected int run() {
					for (AllianceFightVersus versus : versusList) {
						if (versus.getWinId() <= 0) {
							Log.logPrintln("versus deliver invest fail ,beacuse versus has no result , versusId: " + versus.getId());
							continue;
						}
						if (versus.isRewardInvest()) {
							Log.logPrintln("versus deliver invest fail ,beacuse versus has rewarded already , versusId: " + versus.getId());
							continue;
						}
						List<Integer> playerWinIds = null;
						List<Integer> playerFailIds = null;
						if (versus.getWinId() == versus.getLeftId()) {
							// 攻击方胜利
							playerWinIds = versus.getInvestLeftInfoList();
							playerFailIds = versus.getInvestRightInfoList();
						} else {
							playerWinIds = versus.getInvestRightInfoList();
							playerFailIds = versus.getInvestLeftInfoList();
						}

						InvestRewardCfg investRewardCfg = ConfigManager.getInstance().getConfigByKey(InvestRewardCfg.class, group.getNumber());
						if (investRewardCfg == null) {
							Log.logPrintln(String.format("invest reward fail, stageId : %d , versusId: %d ", AllianceBattleManager.getInstance()
									.getCurStageId(), versus.getId()));
							continue;
						}
						for (Integer playerId : playerWinIds) {
							MailManager.createMail(playerId, Mail.MailType.Reward_VALUE, GsConst.MailId.ALLIANCE_BATTLE_INVEST_WIN_AWARD, "",
									investRewardCfg.getWinAwardItems(), dateStr, AllianceBattleManager.getInstance().getAllianceName(versus.getLeftId()),
									AllianceBattleManager.getInstance().getAllianceName(versus.getRightId()));
						}
						for (Integer playerId : playerFailIds) {
							MailManager.createMail(playerId, Mail.MailType.Reward_VALUE, GsConst.MailId.ALLIANCE_BATTLE_INVEST_FAIL_AWARD, "",
									investRewardCfg.getFailAwardItems(), dateStr, AllianceBattleManager.getInstance().getAllianceName(versus.getLeftId()),
									AllianceBattleManager.getInstance().getAllianceName(versus.getRightId()));
						}

						versus.setRewardInvest(true);
						versus.notifyUpdate(false);
					}
					return 0;
				}
			});
		}
	}

	/**
	 * 获得公会名称
	 * 
	 * @param allianceId
	 * @return
	 */
	public String getAllianceName(int allianceId) {
		AllianceBattleItem battleItem = getBattleItem(allianceId);
		if (battleItem == null) {
			return "";
		} else {
			return battleItem.getAllianceName();
		}
	}

	/**
	 * 真实的战斗
	 * 
	 * @param group
	 */
	private List<Integer> fight(FightGroup group) {
		List<Integer> winIds = new LinkedList<>();
		List<AllianceFightVersus> versusList = this.getBattleAgainstInfo().getFightVersusByGroup(group);
		if (versusList == null) {
			return winIds;
		}
		Log.logPrintln("alliance battle begin , group : " + group);
		for (AllianceFightVersus versus : versusList) {
			try {
				int result = fightVersus(versus);
				if (result > 0) {
					versus.setWinId(result);
					versus.notifyUpdate(true);
					winIds.add(result);

					AllianceBattleItem battleItem = AllianceBattleManager.getInstance().getBattleItem(versus.getFailId());
					if (battleItem != null) {
						battleItem.setBattleResult(GROUP_TOP_MAP.get(group));
						battleItem.notifyUpdate(false);
					}

					if (group == FightGroup.GROUP_2) {
						battleItem = AllianceBattleManager.getInstance().getBattleItem(result);
						battleItem.setBattleResult(GsConst.AllianceBattle.TOP_1);
						battleItem.notifyUpdate(false);
					}

					Log.logPrintln("alliance battle versus complete , versusId : " + versus.getId());
				}
			} catch (Exception e) {
				MyException.catchException(e);
				Log.logPrintln("alliance battle versus failed,versusId : " + versus.getId());
			}
		}

		return winIds;
	}

	/**
	 * 单个战场战斗
	 * 
	 * @param versus
	 */
	private int fightVersus(AllianceFightVersus versus) {
		int attackAllianceId = versus.getLeftId();
		int defenceAllianceId = versus.getRightId();
		AllianceBattleItem attackAllianceItem = getBattleItem(attackAllianceId);
		AllianceBattleItem defenceAllianceItem = getBattleItem(defenceAllianceId);
		if (attackAllianceItem == null || defenceAllianceItem == null) {
			Log.logPrintln("error : allianceEntity not found : " + attackAllianceId + "," + defenceAllianceId);
			return -1;
		}
		// 因为血量要保存，需要在这边保存之前的战斗结束的单元
		Map<Integer, List<BattleRole>> attackTeamMap = new HashMap<>();
		for (int teamIndex : GsConst.AllianceBattle.ALL_TEAM) {
			List<BattleRole> battleRoles = attackAllianceItem.getTeamBattleRoleList(teamIndex, versus.getFightGroup());
			List<Integer> buff = SysBasicCfg.getInstance().getAllianceBattleTeamBuff(teamIndex);
			if (buff != null && buff.size() >= 2) {
				for (BattleRole battleRole : battleRoles) {
					battleRole.addAttrValue(attr.valueOf(buff.get(0)), buff.get(1));
				}
			}
			attackTeamMap.put(teamIndex, battleRoles);
		}

		Map<Integer, List<BattleRole>> defenceTeamMap = new HashMap<>();
		for (int teamIndex : GsConst.AllianceBattle.ALL_TEAM) {
			List<BattleRole> battleRoles = defenceAllianceItem.getTeamBattleRoleList(teamIndex, versus.getFightGroup());
			List<Integer> buff = SysBasicCfg.getInstance().getAllianceBattleTeamBuff(teamIndex);
			for (BattleRole battleRole : battleRoles) {
				battleRole.addAttrValue(attr.valueOf(buff.get(0)), buff.get(1));
			}
			defenceTeamMap.put(teamIndex, battleRoles);
		}

		List<Integer> aliveTeamIndex = new LinkedList<>();
		// 先进行第一轮
		for (int teamIndex : GsConst.AllianceBattle.ALL_TEAM) {
			int defenceIndex = teamIndex + GsConst.AllianceBattle.ALL_TEAM.length;
			AllianceFightUnit allianceFightUnit = new AllianceFightUnit();
			allianceFightUnit.setLeftIndex(teamIndex);
			allianceFightUnit.setRightIndex(defenceIndex);
			allianceFightUnit.setStageId(this.getCurStageId());
			allianceFightUnit.setVersusId(versus.getId());
			allianceFightUnit.setLeftBattleRoles(attackTeamMap.get(teamIndex));
			allianceFightUnit.setRightBattleRoles(defenceTeamMap.get(teamIndex));

			versus.addFightUnit(allianceFightUnit);
			fightUnit(allianceFightUnit);

			// 把死掉的单位移除掉
			clearDeadRole(attackTeamMap);
			clearDeadRole(defenceTeamMap);

			aliveTeamIndex.add(allianceFightUnit.getWinIndex());
		}

		// 进行第二轮
		int[] nextFightIndex = calcNexRoundVersusIndex(aliveTeamIndex);
		int leftAliveIndex = nextFightIndex[0];
		int rightAliveIndex = nextFightIndex[1];
		while (leftAliveIndex > 0 && rightAliveIndex > 0) {
			AllianceFightUnit allianceFightUnit = new AllianceFightUnit();
			allianceFightUnit.setLeftIndex(leftAliveIndex);
			allianceFightUnit.setRightIndex(rightAliveIndex);
			allianceFightUnit.setStageId(this.getCurStageId());
			allianceFightUnit.setVersusId(versus.getId());
			allianceFightUnit.setLeftBattleRoles(attackTeamMap.get(leftAliveIndex));
			allianceFightUnit.setRightBattleRoles(defenceTeamMap.get(rightAliveIndex - 3));

			versus.addFightUnit(allianceFightUnit);
			fightUnit(allianceFightUnit);

			// 把死掉的单位移除掉
			clearDeadRole(attackTeamMap);
			clearDeadRole(defenceTeamMap);

			aliveTeamIndex.remove(allianceFightUnit.getFailIndex());

			nextFightIndex = calcNexRoundVersusIndex(aliveTeamIndex);
			leftAliveIndex = nextFightIndex[0];
			rightAliveIndex = nextFightIndex[1];
		}

		if (aliveTeamIndex.get(0) <= GsConst.AllianceBattle.ALL_TEAM.length) {
			// left win
			return attackAllianceId;
		} else {
			return defenceAllianceId;
		}
	}

	/**
	 * 把死掉的单位移除掉,和清除buff状态
	 * 
	 * @param teamMap
	 */
	private void clearDeadRole(Map<Integer, List<BattleRole>> teamMap) {
		for (List<BattleRole> battleRoles : teamMap.values()) {
			Iterator<BattleRole> iter = battleRoles.iterator();
			while (iter.hasNext()) {
				BattleRole role = iter.next();
				role.removeAllBuff();
				role.removeDebuff();
				role.clearSkillCds();
				if (role.getHp() <= 0) {
					iter.remove();
				}
			}
		}
	}

	/**
	 * 
	 * @param aliveTeamIndex
	 * @return
	 */
	private int[] calcNexRoundVersusIndex(List<Integer> aliveTeamIndex) {
		int leftAliveIndex = 0;
		int rightAliveIndex = 0;
		for (Integer aliveIndex : aliveTeamIndex) {
			if (aliveIndex <= GsConst.AllianceBattle.ALL_TEAM.length && leftAliveIndex == 0) {
				leftAliveIndex = aliveIndex;
			} else if (aliveIndex > GsConst.AllianceBattle.ALL_TEAM.length && rightAliveIndex == 0) {
				rightAliveIndex = aliveIndex;
			}
		}
		return new int[] { leftAliveIndex, rightAliveIndex };
	}

	/**
	 * 每一个小组战斗
	 * 
	 * @param allianceFightUnit
	 */
	private void fightUnit(AllianceFightUnit allianceFightUnit) {
		Battlefield battlefield = new Battlefield();
		List<BattleRole> attackers = allianceFightUnit.getLeftBattleRoles();
		List<BattleRole> defenders = allianceFightUnit.getRightBattleRoles();
		if (attackers == null || attackers.size() == 0) {
			// 进攻方输
			allianceFightUnit.setWinIndex(allianceFightUnit.getRightIndex());
		} else if (defenders == null || defenders.size() == 0) {
			// 防守方输
			allianceFightUnit.setWinIndex(allianceFightUnit.getLeftIndex());
		} else {
			BattleInfo.Builder battleInfoBuilder = battlefield.fighting(Battle.battleType.BATTLE_ALLIANCE_BATTLE_VALUE, attackers, defenders, null);
			allianceFightUnit.setFightReportBattle(battleInfoBuilder.build());
			if (battleInfoBuilder.getFightResult() > 0) {
				allianceFightUnit.setWinIndex(allianceFightUnit.getLeftIndex());
			} else {
				allianceFightUnit.setWinIndex(allianceFightUnit.getRightIndex());
			}
		}

		DBManager.getInstance().create(allianceFightUnit);
	}

	@Override
	public boolean onMessage(Msg msg) {
		if (this.curAllianceBattleInfo == null) {
			return true;
		}
		if (this.getCurBattleState() != AllianceBattleState.PREPARE) {
			return true;
		}
		if (msg.getMsg() == GsConst.MsgType.ALLIANCE_VITALITY_CHANGE) {
			// 添加元气
			int allinaceId = msg.getParam(0);
			int vitality = msg.getParam(1);
			if (allinaceId > 0 && vitality > 0) {
				addAllianceVitality(allinaceId, vitality);
			}
			return true;
		} else if (msg.getMsg() == GsConst.MsgType.ALLIANCE_REMOVE) {
			int allianceId = msg.getParam(0);
			if (allianceId > 0) {
				AllianceBattleItem battleItem = getBattleItem(allianceId);
				if (battleItem != null) {
					this.battleItemMap.remove(allianceId);
					this.battleItemSet.remove(battleItem);
					battleItem.delete();
				}
			}
			return true;
		} else if (msg.getMsg() == GsConst.MsgType.ALLIANCE_MEMBER_REMOVE) {
			int allianceId = msg.getParam(0);
			int memberId = msg.getParam(1);
			if (allianceId > 0 && memberId > 0) {
				AllianceBattleItem battleItem = getBattleItem(allianceId);
				if (battleItem != null) {
					battleItem.removeMem(memberId);

					battleItem.notifyUpdate(true);
				}
			}
			return true;
		} else if (msg.getMsg() == GsConst.MsgType.ALLIANCE_MEMBER_ADD) {
			int allianceId = msg.getParam(0);
			int memberId = msg.getParam(1);
			if (allianceId > 0 && memberId > 0) {
				AllianceBattleItem battleItem = getBattleItem(allianceId);
				if (battleItem != null) {
					battleItem.addMem(memberId);

					battleItem.notifyUpdate(true);
				}
			}
			return true;
		} else if (msg.getMsg() == GsConst.MsgType.ALLIANCE_LELEL_UP) {
			int allianceId = msg.getParam(0);
			int level = msg.getParam(1);
			if (allianceId > 0 && level > 0) {
				AllianceBattleItem battleItem = getBattleItem(allianceId);
				if (battleItem != null) {
					battleItem.setAllianceLevel(level);
					battleItem.notifyUpdate(true);
				}
			}
			return true;
		}

		return false;
	}

	private AllianceBattleState calcCurBattleState() {
		int battleStageId = AllianceBattleTimeCfg.getCurBattleStageId(this.getCurStageId());
		if (battleStageId < 0) {
			// 无效的状态
			return AllianceBattleState.PREPARE;
		}
		return AllianceBattleState.valueOf(battleStageId);
	}

	/**
	 * 增加元气
	 * 
	 * @param allianceId
	 * @param vitality
	 */
	public void addAllianceVitality(int allianceId, int vitality) {
		if (battleItemMap.containsKey(allianceId)) {
			AllianceBattleItem allianceBattleItem = battleItemMap.get(allianceId);
			synchronized(battleItemSet){
				battleItemSet.remove(allianceBattleItem);
				allianceBattleItem.increaseVitality(vitality);
				battleItemSet.add(allianceBattleItem);
			}
			allianceBattleItem.notifyUpdate(true);
		} else {
			AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
			AllianceBattleItem allianceBattleItem = AllianceBattleItem.valueOf(allianceEntity);
			allianceBattleItem.setVitality(vitality);
			allianceBattleItem.setStageId(this.curStageId);
			allianceBattleItem.setHasDraw(false);
			DBManager.getInstance().create(allianceBattleItem);
			battleItemMap.put(allianceId, allianceBattleItem);
			battleItemSet.add(allianceBattleItem);
			this.battleAgainstInfo.addBattleItem(allianceBattleItem);

			allianceBattleItem.autoJoinTeam();
		}
	}

	/**
	 * 获得前32名的 帮会信息
	 * 
	 * @return
	 */
	public List<AllianceBattleItem> getAllianceBattleItems() {
		List<AllianceBattleItem> battleItems = new ArrayList<AllianceBattleItem>(GsConst.AllianceBattle.ALLIANCE_BATTLE_RANK_SIZE);
		int index = 0;
		for (AllianceBattleItem battleItem : battleItemSet) {
			if (index >= GsConst.AllianceBattle.ALLIANCE_BATTLE_RANK_SIZE) {
				break;
			}
			if(battleItems.contains(battleItem)){
				continue;
			}
			battleItems.add(battleItem);
			index++;
		}
		return battleItems;
	}

	/**
	 * 获得具体某个帮会的排名
	 * 
	 * @param allianceId
	 * @return
	 */
	public int getAllianceRank(int allianceId) {
		AllianceBattleItem allianceBattleItem = this.battleItemMap.get(allianceId);
		int index = 0;
		for (AllianceBattleItem battleItem : battleItemSet) {
			index++;
			if (battleItem == allianceBattleItem) {
				return index;
			}
		}
		return -1;
	}

	public AllianceBattleItem getBatleItemByRank(int rankIndex) {
		int index = 0;
		if (rankIndex < 0 || rankIndex >= battleItemSet.size()) {
			return null;
		}
		for (AllianceBattleItem battleItem : battleItemSet) {
			if (index == rankIndex) {
				return battleItem;
			}
			index++;
		}
		return null;
	}

	public AllianceBattleState getCurBattleState() {
		//公会战没开
		if(this.curAllianceBattleInfo!=null)
			return AllianceBattleState.valueOf(this.curAllianceBattleInfo.getState());
		else
			return null;
	}

	public void setCurBattleState(AllianceBattleState curBattleState) {
		this.curBattleState = curBattleState;
	}

	public AllianceBattleItem getBattleItem(int allianceId) {
		return this.battleItemMap.get(allianceId);
	}

	/**
	 * 同步工会战信息
	 */
	public void getSyncBattleInfo(Player player, int allianceId) {
		HPAFMainEnterSync.Builder enterAFMainSync = HPAFMainEnterSync.newBuilder();
		AllianceBattleItem battleItem = this.getBattleItem(allianceId);
		if (battleItem != null) {
			enterAFMainSync.setHasDraw(battleItem.getHasDraw());
		}
		AllianceBattleState curState = AllianceBattleState.valueOf(this.curAllianceBattleInfo.getState());
		enterAFMainSync.setBattleState(curState);
		AllianceBattleTimeCfg timeCfg = AllianceBattleTimeCfg.getCfg(curState.getNumber());
		enterAFMainSync.setLeftTime((int) (timeCfg.getEndSpecifiedDate(this.getCurStageId()).getTime() - GuaJiTime.getMillisecond()) / 1000);
		if (curState == AllianceBattleState.PREPARE || curState == AllianceBattleState.Draw_Lots_WAIT) {
			enterAFMainSync.setRankList(BuilderUtil.genAllianceBattleRankList(player, allianceId));
		} else {
			if (this.battleItemSet.size() < GsConst.AllianceBattle.ALLIANCE_BATTLE_RANK_SIZE) {
				player.sendError(HP.code.ALLIANCE_BATTLE_ENTER_C_VALUE, Status.error.ALLIANCE_FIGHT_COUNT_LIMIT);
				return;
			}

			// 发送对阵信息
			enterAFMainSync.setFightList(BuilderUtil.genAllianceFightList(player, allianceId, this.getBattleAgainstInfo(), false));

			if (curState == AllianceBattleState.SHOW_TIME) {
				enterAFMainSync.setRankList(BuilderUtil.genAllianceBattleResultList());
			} else {
				// 如果正在战斗过程中 直接发送战斗信息
				AllianceBattleState curBattleState = AllianceBattleManager.getInstance().getCurBattleState();
				if (curBattleState == AllianceBattleState.FS2_1_FIGHTING || curBattleState == AllianceBattleState.FS32_16_FIGHTING
						|| curBattleState == AllianceBattleState.FS16_8_FIGHTING || curBattleState == AllianceBattleState.FS8_4_FIGHTING
						|| curBattleState == AllianceBattleState.FS4_2_FIGHTING) {
					AllianceFightVersus versus = AllianceBattleManager.getInstance().getAllianceFightVersus(GROUP_REGISTER_MAP.get(curBattleState).getNumber(),
							allianceId, false);
					if (versus != null) {
						HPAllianceTeamFightRet.Builder fightRetBuilder = BuilderUtil.genAllianceFightVersus(allianceId, versus);
						fightRetBuilder.setIsSelfCurBattle(true);
						enterAFMainSync.setTeamFight(fightRetBuilder);
					}
				}
				if (battleItem != null) {
					int rankIndex = this.getAllianceRank(allianceId);
					if (rankIndex <= GsConst.AllianceBattle.ALLIANCE_BATTLE_RANK_SIZE) {
						if (curBattleState == AllianceBattleState.Publicity_WAIT || curBattleState == AllianceBattleState.FS32_16_FIGHTING) {
							enterAFMainSync.setSelfInspireNum(battleItem.getInspireTimes(player.getId(), FightGroup.GROUP_32_VALUE));
							enterAFMainSync.setTotalSelfInspireNum(battleItem.getTotalInspireTimes(player.getId()));
						} else if (curBattleState == AllianceBattleState.FS16_8_WAIT || curBattleState == AllianceBattleState.FS16_8_FIGHTING) {
							enterAFMainSync.setSelfInspireNum(battleItem.getInspireTimes(player.getId(), FightGroup.GROUP_16_VALUE));
							enterAFMainSync.setTotalSelfInspireNum(battleItem.getTotalInspireTimes(player.getId()));
						} else if (curBattleState == AllianceBattleState.FS8_4_WAIT || curBattleState == AllianceBattleState.FS8_4_FIGHTING) {
							enterAFMainSync.setSelfInspireNum(battleItem.getInspireTimes(player.getId(), FightGroup.GROUP_8_VALUE));
							enterAFMainSync.setTotalSelfInspireNum(battleItem.getTotalInspireTimes(player.getId()));
						} else if (curBattleState == AllianceBattleState.FS4_2_WAIT || curBattleState == AllianceBattleState.FS4_2_FIGHTING) {
							enterAFMainSync.setSelfInspireNum(battleItem.getInspireTimes(player.getId(), FightGroup.GROUP_4_VALUE));
							enterAFMainSync.setTotalSelfInspireNum(battleItem.getTotalInspireTimes(player.getId()));
						} else if (curBattleState == AllianceBattleState.FS2_1_WAIT || curBattleState == AllianceBattleState.FS2_1_FIGHTING) {
							enterAFMainSync.setSelfInspireNum(battleItem.getInspireTimes(player.getId(), FightGroup.GROUP_2_VALUE));
							enterAFMainSync.setTotalSelfInspireNum(battleItem.getTotalInspireTimes(player.getId()));
						}
					}
				}
			}
		}

		player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_BATTLE_ENTER_S, enterAFMainSync));
	}

	public AllianceFightVersus getAllianceFightVersus(int fightGroup, int allianceId, boolean isLast) {
		if (isLast) {
			return this.getLastBattleAgainstInfo().getAllianceFightVersus(fightGroup, allianceId);
		} else {
			return this.getBattleAgainstInfo().getAllianceFightVersus(fightGroup, allianceId);
		}
	}

	public int getCurStageId() {
		return curStageId;
	}

	/**
	 * 如果是正在战斗的阶段 会返回对应的FightGroup的阶段
	 * 
	 * @return
	 */
	public FightGroup getCurFightGroup() {
		AllianceBattleState curBattleState = this.getCurBattleState();
		if (curBattleState == AllianceBattleState.PREPARE || curBattleState == AllianceBattleState.SHOW_TIME) {
			return null;
		}

		return GROUP_REGISTER_MAP.get(curBattleState);
	}

	/**
	 * 获得战斗小单元
	 * 
	 * @param battleId
	 * @return
	 */
	public AllianceFightUnit getAllianceFightUnit(int battleId) {
		AllianceFightUnit fightUnit = this.getBattleAgainstInfo().getAllianceFightUnit(battleId);
		if (fightUnit == null) {
			fightUnit = this.getLastBattleAgainstInfo().getAllianceFightUnit(battleId);
		}
		return fightUnit;
	}

	public List<AllianceBattleItem> getTop8BattleItems() {
		List<AllianceBattleItem> battleItems = new LinkedList<>();
		for (AllianceBattleItem battleItem : this.battleItemSet) {
			if (battleItem.getBattleResult() <= GsConst.AllianceBattle.TOP_8 && battleItem.getBattleResult() > 0) {
				if (battleItems.size() == 0) {
					battleItems.add(battleItem);
				} else {
					boolean isAdd = false;
					for (int i = 0; i < battleItems.size(); i++) {
						if (battleItem.getBattleResult() <= battleItems.get(i).getBattleResult()) {
							battleItems.add(i, battleItem);
							isAdd = true;
							break;
						}
					}
					if (!isAdd) {
						battleItems.add(battleItem);
					}
				}
			}
		}
		return battleItems;
	}

	public AllianceBattleAgainstInfo getLastBattleAgainstInfo() {
		return lastBattleAgainstInfo;
	}

	public void setLastBattleAgainstInfo(AllianceBattleAgainstInfo lastBattleAgainstInfo) {
		this.lastBattleAgainstInfo = lastBattleAgainstInfo;
	}

	public AllianceBattleAgainstInfo getBattleAgainstInfo() {
		return battleAgainstInfo;
	}

	public void setBattleAgainstInfo(AllianceBattleAgainstInfo battleAgainstInfo) {
		this.battleAgainstInfo = battleAgainstInfo;
	}

	public AllianceBattleItem createTempBattleItem(AllianceEntity allianceEntity) {
		addAllianceVitality(allianceEntity.getId(), 0);
		return this.getBattleItem(allianceEntity.getId());
	}

	/**
	 * 判断公会是否在战斗中
	 * 
	 * @param id
	 */
	public boolean isAllianceInBattle(int allianceId) {
		if (this.curAllianceBattleInfo == null) {
			return false;
		}
		if (this.getCurBattleState() != AllianceBattleState.PREPARE && this.getCurBattleState() != AllianceBattleState.SHOW_TIME) {
			return this.battleAgainstInfo.isAllianceBattle(allianceId);
		}
		return false;
	}

	public AllianceFightVersus getAllianceFailFightVersus(int allianceId, boolean isLast) {
		if (isLast) {
			return this.getLastBattleAgainstInfo().getAllianceFailFightVersus(allianceId);
		} else {
			return this.getBattleAgainstInfo().getAllianceFailFightVersus(allianceId);
		}
	}

	public int[] getLastChampionInfo() {
		int[] result = new int[] { 0, 0 };
		if (allChampionAllianceIdList.size() > 0) {
			int continueIndex = 0;
			int lastChampionId = allChampionAllianceIdList.get(allChampionAllianceIdList.size() - 1);
			if (lastChampionId > 0) {
				for (int i = allChampionAllianceIdList.size() - 1; i >= 0; i--) {
					if (allChampionAllianceIdList.get(i) == lastChampionId) {
						continueIndex++;
					} else {
						break;
					}
				}
			}
			result[0] = lastChampionId;
			result[1] = continueIndex;
		}

		return result;
	}

	/**
	 * 抽取对应的工会
	 * 
	 * @param allianceEntity
	 */
	public void drawAllianceBattleValue(Player player, PlayerAllianceEntity allianceEntity) {
		AllianceBattleState curState = AllianceBattleState.valueOf(this.curAllianceBattleInfo.getState());
		// 只允许在抽签阶段进行抽签操作
		if (curState.getNumber() == AllianceBattleState.Draw_Lots_WAIT_VALUE) {
			Map<Integer, List<AllianceFightVersus>> allianceAgainstMap = this.getBattleAgainstInfo().getAgainstMap();
			if (allianceAgainstMap != null) {
				List<AllianceFightVersus> fightVersus = allianceAgainstMap.get(FightGroup.GROUP_32_VALUE);
				if (fightVersus != null && fightVersus.size() > 0) {
					HPAllianceDrawRet.Builder alliancedraw = HPAllianceDrawRet.newBuilder();
					int targetAllianceId = 0;
					int i = 0;
					boolean flag = false;
					for (AllianceFightVersus versus : fightVersus) {
						i += 2;
						if (versus.getLeftId() == allianceEntity.getAllianceId()) {
							targetAllianceId = versus.getRightId();
							flag = true;
							i--;
							break;
						} else if (versus.getRightId() == allianceEntity.getAllianceId()) {
							targetAllianceId = versus.getLeftId();
							flag = true;
							break;
						}
					}
					if (flag) {
						alliancedraw.setIndex(i);
						alliancedraw.setAllianceId(targetAllianceId);
						player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_BATTLE_DRAW_S, alliancedraw));
						// 更新公会战抽签状态
						AllianceBattleItem allianceBattleItem = AllianceBattleManager.getInstance().battleItemMap.get(allianceEntity.getAllianceId());
						allianceBattleItem.setHasDraw(true);
						allianceBattleItem.notifyUpdate(false);
					} else {
						player.sendError(HP.code.ALLIANCE_BATTLE_DRAW_C_VALUE, Status.error.ALLIANCE_NOT_IN_BATTLE);
						return;
					}
				} else {
					player.sendError(HP.code.ALLIANCE_BATTLE_DRAW_C_VALUE, Status.error.ALLIANCE_BATTLE_32_DATA_FAIL);
					return;
				}
			} else {
				player.sendError(HP.code.ALLIANCE_BATTLE_DRAW_C_VALUE, Status.error.ALLIANCE_BATTLE_DATA_FAIL);
				return;
			}
		} else {
			player.sendError(HP.code.ALLIANCE_BATTLE_DRAW_C_VALUE, Status.error.ALLIANCE_BATTLE_DRAWTIME_FAIL);
			return;
		}
	}
	
	public static void main(String[] args) {
		 SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式  
         Calendar cal = Calendar.getInstance();  
         Date time;
		 try {
				time = sdf.parse("2016-10-16 14:22:47");
         cal.setTime(time);  
         System.out.println("要计算日期为:"+sdf.format(cal.getTime())); //输出要计算日期  
         
         //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了  
         int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天  
         if(1 == dayWeek) {  
            cal.add(Calendar.DAY_OF_MONTH, -1);  
         }  
         
         cal.setFirstDayOfWeek(Calendar.MONDAY);
		} catch (ParseException e) {
				
				e.printStackTrace();
		}
		int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天  
	    cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值   
	    System.out.println("所在周星期一的日期："+sdf.format(cal.getTime()));
	}

}