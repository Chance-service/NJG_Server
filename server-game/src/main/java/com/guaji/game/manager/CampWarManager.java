package com.guaji.game.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.guaji.app.AppObj;
import org.guaji.cache.CacheObj;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.msg.Msg;
import org.guaji.os.GuaJiRand;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;
import org.guaji.thread.GuaJiTask;
import org.guaji.xid.GuaJiXID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guaji.game.GsApp;
import com.guaji.game.battle.BattleRole;
import com.guaji.game.battle.Battlefield;
import com.guaji.game.config.CampWarWinStreakCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.CampEntity;
import com.guaji.game.entity.CampWarAutoJoinEntity;
import com.guaji.game.entity.CampWarEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.protocol.Attribute.Attr;
import com.guaji.game.protocol.Battle;
import com.guaji.game.protocol.Battle.BattleInfo;
import com.guaji.game.protocol.CampWar;
import com.guaji.game.protocol.CampWar.CampInfo;
import com.guaji.game.protocol.CampWar.CampReportInfo;
import com.guaji.game.protocol.CampWar.CampStateType;
import com.guaji.game.protocol.CampWar.MultiKillRankInfo;
import com.guaji.game.protocol.CampWar.PersonalCampWarInfo;
import com.guaji.game.protocol.CampWar.reportType;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Const.attr;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.Mail.MailType;
import com.guaji.game.protocol.Player.RoleInfo;
import com.guaji.game.protocol.Player.RoleInfo.Builder;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;

/**
 * 阵营战管理器
 */
public class CampWarManager extends AppObj {
	/**
	 * 日志记录器
	 */
	private static Logger logger = LoggerFactory.getLogger("Server");
	
	/**
	 * 阵营战战报结构
	 * @author rudy
	 */
	public class CampReport implements Comparable<CampReport>{
		// 阵营战战报类型
		private int reportType;
		// 参数数量
		private int argsNum;
		// 参数字符
		private List<String> argStrings;
		// 先手方阵营Id
		private int campId;
		
		// 排序参数，用于排行
		private int sortArg;
		// 生成时间
		private long createTime;
		
		public CampReport(int reportType, int round){
			this.reportType = reportType;
			this.argsNum = 0;
			this.argStrings = new ArrayList<String>();
			this.argStrings.add(String.valueOf(round));
			this.campId = 0;
			this.createTime = GuaJiTime.getMillisecond();
			this.sortArg = 0;
		}
		
		public int getReportType() {
			return reportType;
		}

		public int getArgNum(){
			return this.argsNum;
		}
		
		public void addArgString(String argString){
			argStrings.add(argString);
			this.argsNum = argStrings.size();
		}
		
		public void updateArgString(int index, String argString){
			if(argStrings.size() > index){
				argStrings.set(index, argString);
			}
		}
		
		public void setSortArg(int sortArg) {
			this.sortArg = sortArg;
		}
		
		public int getSortArg() {
			return sortArg;
		}

		public void setCampId(int campId) {
			this.campId = campId;
		}
		
		public long getCreateTime() {
			return createTime;
		}

		/**
		 * 只有连胜和终结战报时才会调用
		 */
		@Override
		public int compareTo(CampReport o) {
			if(this.createTime == o.getCreateTime())
				return 0;
			
			if(CampWar.reportType.CONTINUE_WIN_TYPE_VALUE == this.reportType
					|| CampWar.reportType.END_WIN_TYPE_VALUE == this.reportType){
				return (this.sortArg > o.getSortArg())?-1:1;
			}
			
			return (this.createTime > o.getCreateTime())?-1:1;
		}
		
		public CampReportInfo.Builder toCampReportInfo(){
			CampReportInfo.Builder builder = CampReportInfo.newBuilder();
			builder.setReportType(this.reportType);
			builder.setArgsNum(this.argsNum);
			if(this.campId != 0) {
				builder.setCampId(this.campId);
			}
			
			for(String argStr : argStrings){
				builder.addArgString(argStr);
			}
			return builder;
		}
	}
	
	/**
	 * 扣除自动阵营战玩家的钻石
	 * @author xpf
	 */
	public class DeductAutoPlayerTask extends GuaJiTask {
		private ConcurrentHashMap<Integer, Integer> autoCampWarPlayerIds;
		
		public DeductAutoPlayerTask(ConcurrentHashMap<Integer, Integer> autoPlayerIds) {
			this.autoCampWarPlayerIds = autoPlayerIds;
		}
		
		@Override
		protected int run() {
			CampWarManager.getInstance().deductAutoPlayerGold(this.autoCampWarPlayerIds);
			return 0;
		}

		@Override
		protected CacheObj clone() {
			return null;
		}
		
	}
	
	/**
	 * 单轮阵营战Task
	 * @author xpf
	 */
	public class CamWarRoundTask extends GuaJiTask {
		private CampEntity leftCamp;
		private CampEntity rightCamp;
		
		public CamWarRoundTask(CampEntity leftCamp, CampEntity rightCamp){
			this.leftCamp = leftCamp;
			this.rightCamp = rightCamp;
		}
		
		@Override
		protected int run() {
			doFight(leftCamp, rightCamp);
			
			// 告知所有玩家阵营战进入下一轮
			Msg msg = Msg.valueOf(GsConst.MsgType.CAMPWAR_BATTLE_ROUND_CHANGE);
			GsApp.getInstance().broadcastMsg(msg, GsApp.getInstance().getObjMan(GsConst.ObjType.PLAYER));
			return 0;
		}
		
		protected void doFight(CampEntity leftCamp, CampEntity rightCamp){
			List<CampWarEntity> leftCampWarEntities =  leftCamp.getCampWarEntities();
			List<CampWarEntity> rightCampWarEntities = rightCamp.getCampWarEntities();
			Collections.shuffle(leftCampWarEntities);
			Collections.shuffle(rightCampWarEntities);
			int minSize = Math.min(leftCampWarEntities.size(), rightCampWarEntities.size());
			if(leftCampWarEntities.size() != rightCampWarEntities.size()) {
				//人数不平等
				int sideMore = leftCampWarEntities.size() > rightCampWarEntities.size() ? 1 : 0;
				List<CampWarEntity> warEntities =  leftCampWarEntities.size() > rightCampWarEntities.size() ? leftCampWarEntities : rightCampWarEntities;
				for(int i = minSize ; i < warEntities.size();i++) {
					CampWarEntity campWarEntity = warEntities.get(i);
					PlayerSnapshotInfo.Builder playerSnapshotInfo = SnapShotManager.getInstance().getPlayerSnapShot(campWarEntity.getPlayerId());
					RoleInfo roleInfo = playerSnapshotInfo.getMainRoleInfo();
					if(sideMore > 0) {
						CampWarManager.getInstance().onRoundFightEnd(campWarEntity.getPlayerId(), 0, 1, roleInfo.toBuilder(),null);
					}else{
						CampWarManager.getInstance().onRoundFightEnd(0, campWarEntity.getPlayerId(), -1, null, roleInfo.toBuilder());
					}
				}
			}
			
			for(int i = 0; i < minSize; i++){
				int factor = GuaJiRand.randInt();
				CampWarEntity attacker = (factor % 2 == 0) ? leftCampWarEntities.get(i) : rightCampWarEntities.get(i);
				CampWarEntity defencer = (factor % 2 == 0) ? rightCampWarEntities.get(i) : leftCampWarEntities.get(i);
				int attackerId = attacker.getPlayerId();
				int defenceId = defencer.getPlayerId();
				try{
					PlayerSnapshotInfo.Builder attackSnapShotInfo = SnapShotManager.getInstance().getPlayerSnapShot(attackerId);
					PlayerSnapshotInfo.Builder defenceSnapShotInfo = SnapShotManager.getInstance().getPlayerSnapShot(defenceId);
					RoleInfo.Builder attakerRoleInfoBuilder =  attackSnapShotInfo.getMainRoleInfo().toBuilder();
					RoleInfo.Builder defenceRoleInfoBuilder =  defenceSnapShotInfo.getMainRoleInfo().toBuilder();
					
					// 攻击方
					BattleRole battleRoleAttack = new BattleRole(attackerId, attachInspire(attakerRoleInfoBuilder, attacker));
					List<BattleRole> attackerList = new LinkedList<>();
					attackerList.add(battleRoleAttack);
					/* 佣兵
					RoleInfo.Builder attakerMercenaryInfoBuilder = SnapShotManager.getInstance().getFightMercenaryInfo(attackerId);
					if (attakerMercenaryInfoBuilder != null) {
						attackerList.add(new BattleRole(attachInspire(attakerMercenaryInfoBuilder, attacker)));
					}
					*/
					
					// 防守方
					BattleRole battleRoleDefence = new BattleRole(defenceId, attachInspire(defenceRoleInfoBuilder, defencer));
					List<BattleRole> defenceList = new LinkedList<>();
					defenceList.add(battleRoleDefence);
					/* 佣兵
					RoleInfo.Builder defenceMercenaryInfoBuilder = SnapShotManager.getInstance().getFightMercenaryInfo(defenceId);
					if (defenceMercenaryInfoBuilder != null) {
						defenceList.add(new BattleRole(attachInspire(defenceMercenaryInfoBuilder, defencer)));
					}
					*/
					
					// 战斗
					Battlefield battlefield = new Battlefield();
					BattleInfo.Builder battleBuilder = battlefield.fighting(Battle.battleType.BATTLE_PVP_TEAM_VALUE, attackerList, defenceList, null);
					
					// 保存剩余血量
					//battlefield.recordRoleState();
					attacker.setCurRemainBlood(battleRoleAttack.getHp());
					defencer.setCurRemainBlood(battleRoleDefence.getHp());
					attacker.notifyUpdate(true);
					defencer.notifyUpdate(true);
					
					int result = battleBuilder.getFightResult();
					CampWarManager.getInstance().onRoundFightEnd(attackerId, defenceId, result, attakerRoleInfoBuilder, defenceRoleInfoBuilder);
				} catch (Exception e){
					MyException.catchException(e);
				}
			}
		}
		
		private RoleInfo.Builder attachInspire(RoleInfo.Builder roleInfoBuilder, CampWarEntity campWarEntity){
			for(Attr.Builder attrInfo : roleInfoBuilder.getAttributeBuilder().getAttributeBuilderList()) {
				if(attrInfo.getAttrId() == attr.MINDMG_VALUE) {
					attrInfo.setAttrValue(attrInfo.getAttrValue() * (1 + campWarEntity.getInspireTimes() * SysBasicCfg.getInstance().getCampWarInspireBonuses() / 100));
				}
				if(attrInfo.getAttrId() == attr.MAXDMG_VALUE) {
					attrInfo.setAttrValue(attrInfo.getAttrValue() * (1 + campWarEntity.getInspireTimes() * SysBasicCfg.getInstance().getCampWarInspireBonuses() / 100));
				}
				if(attrInfo.getAttrId() == attr.HP_VALUE) {
					attrInfo.setAttrValue(campWarEntity.getCurRemainBlood());
				}
			}
			return roleInfoBuilder;
		}
		
		@Override
		protected CacheObj clone() {
			return null;
		}
		
	}
	
	/**
	 * 阵营战结束发奖
	 * @author xpf
	 */
	public class CampWarRewardTask extends GuaJiTask{
		private Map<Integer, CampWarEntity> curPlayerIdCampEntityMap;
		
		public CampWarRewardTask(Map<Integer, CampWarEntity> playerIdCampEntityMap){
			this.curPlayerIdCampEntityMap = playerIdCampEntityMap;
		}
		
		@Override
		protected int run() {
			CampWarManager.getInstance().grantCampWarAward(curPlayerIdCampEntityMap);
			return 0;
		}
		
		@Override
		protected CacheObj clone() {
			return null;
		}
		
	}
	
	/**
	 * 全局对象, 便于访问
	 */
	private static CampWarManager instance = null;
	
	/**
	 * 上期阵营站战排行数据
	 */
	
	// 上期阵营战期号
	private int lastWarStageId;
	// 上期期阵营战连杀排行
	private Set<CampWarEntity> lastMultiKillRankSet;
	// 上期阵营战平均声望
	private int lastAvgReputation;
	// 上期阵营战平均金币
	private int lastAvgCoins;
	// 上期获胜阵营ID
	private int lastWinCampId;
	
	/**
	 * 本期阵营战数据
	 */
	// 本期阵营战期号
	private int curWarStageId;
	// 当前阵营战状态
	private int status;
	// 本期阵营战战斗了几轮
	private int round;
	// 下次Tick触发时间
	private int nextPeriod;
	// 当前round的连胜战报和终结战报列表
	private Map<Integer, Set<CampReport>> campReportMap;
	// 当前round的个人战报
	private Map<Integer, CampReport> personalReportMap;
	// 本期阵营战连杀排行
	private SortedSet<CampWarEntity> curMultiKillRankSet;
	// 本期阵营战报名玩家数据
	private Map<Integer, CampWarEntity> curPlayerIdCampEntityMap;
	// 阵营信息
	private Map<Integer, CampEntity> campMap;
	// 本期自动阵营战玩家ID缓存
	private CampWarAutoJoinEntity curAutoJoinEntity = null;
	
	/**
	 * 获取全局实例对象
	 */
	public static CampWarManager getInstance() {
		return instance;
	}

	public CampWarManager(GuaJiXID xid) {
		super(xid);
		
		this.lastWinCampId = 0;
		this.lastWarStageId = 0;
		this.curWarStageId = 0;
		this.status = 0;
		this.lastAvgReputation = 0;
		this.lastAvgCoins = 0;
		this.round = 0;
		this.nextPeriod = 0;
		
		this.lastMultiKillRankSet = Collections.synchronizedSortedSet(new TreeSet<CampWarEntity>());
		this.curMultiKillRankSet = Collections.synchronizedSortedSet(new TreeSet<CampWarEntity>());
		this.curPlayerIdCampEntityMap = new ConcurrentHashMap<Integer, CampWarEntity>();
		this.campMap = new TreeMap<Integer, CampEntity>();
		this.campReportMap = new ConcurrentHashMap<Integer, Set<CampReport>>();
		this.personalReportMap = new ConcurrentHashMap<Integer, CampReport>();
		
		if (instance == null) {
			instance = this;
		}
	}
	
	public boolean init(){
		// 计算本期数据
		this.curWarStageId = calcWarStageId();
		this.lastWarStageId = calcLastWarStageId();
		this.status = calcCurWarStatus();
		this.nextPeriod = calcNextPeriod();
		this.round = calcCurBattleRound();
		
		loadAutoJoinPlayerIds();
		loadCampData();
		loadCampWarPlayerData();
		return true;
	}
	
	public boolean reset(){
		// 上期团战数据清理
		this.clear();
		
		// 计算本期数据
		this.curWarStageId = calcWarStageId();
		this.lastWarStageId = calcLastWarStageId();
		this.status = calcCurWarStatus();
		this.nextPeriod = calcNextPeriod();
		this.round = calcCurBattleRound();
		
		// 构造新的自动阵营战玩家Id缓存对象
		CampWarAutoJoinEntity tmpAutoJoinEntity = new CampWarAutoJoinEntity(this.curWarStageId);
		tmpAutoJoinEntity.setAutoJoinPlayerIdsStr(this.curAutoJoinEntity.getAutoJoinPlayerIdsStr());
		tmpAutoJoinEntity.convertAutoJoinMap();
		DBManager.getInstance().create(tmpAutoJoinEntity);
		this.curAutoJoinEntity = tmpAutoJoinEntity;
		
		// 创建下期阵营数据
		CampEntity leftCampEntity = new CampEntity(GsConst.CampWar.LEFT_CAMP_ID, this.curWarStageId);
		CampEntity rightCampEntity = new CampEntity(GsConst.CampWar.RIGHT_CAMP_ID, this.curWarStageId);
		DBManager.getInstance().create(leftCampEntity);
		DBManager.getInstance().create(rightCampEntity);
		campMap.put(leftCampEntity.getCampId(), leftCampEntity);
		campMap.put(rightCampEntity.getCampId(), rightCampEntity);
		
		return true;
	}
	
	/**
	 * 加载自动阵营战玩家Id列表
	 */
	private void loadAutoJoinPlayerIds(){
		List<CampWarAutoJoinEntity> campWarAutoJoinEntities = DBManager.getInstance().query("from CampWarAutoJoinEntity where stageId = ? or stageId = ?", curWarStageId, lastWarStageId);
		CampWarAutoJoinEntity lastAutoJoinEntity = null;
		if(campWarAutoJoinEntities.size() > 0){
			for(CampWarAutoJoinEntity autoJoinEntitiy : campWarAutoJoinEntities){
				if(autoJoinEntitiy.getStageId() == this.lastWarStageId){
					lastAutoJoinEntity = autoJoinEntitiy;
				}
				if(autoJoinEntitiy.getStageId() == this.curWarStageId){
					this.curAutoJoinEntity = autoJoinEntitiy;
					break;
				}
			}
		}
		
		if(this.curAutoJoinEntity == null){
			this.curAutoJoinEntity = new CampWarAutoJoinEntity(this.curWarStageId);
			if(lastAutoJoinEntity != null){
				this.curAutoJoinEntity.setAutoJoinPlayerIdsStr(lastAutoJoinEntity.getAutoJoinPlayerIdsStr());
				this.curAutoJoinEntity.convertAutoJoinMap();
			}
			DBManager.getInstance().create(this.curAutoJoinEntity);
		}
	}
	
	/**
	 * 加载阵营数据
	 */
	private void loadCampData(){
		List<CampEntity> campEntities = DBManager.getInstance().query("from CampEntity where stageId = ? or stageId = ?", curWarStageId, lastWarStageId);
		if(campEntities.size() > 0){
			for(CampEntity campEntity : campEntities){
				if(campEntity.getStageId() == this.curWarStageId){
					this.campMap.put(campEntity.getCampId(), campEntity);
				} else if (campEntity.getStageId() == this.lastWarStageId){
					if(campEntity.getIsWin() > 0){
						this.lastWinCampId = campEntity.getCampId();
					}
				}
			}
		}
		
		// 阵营数据不存在时创建
		if(this.campMap.size() < 2){
			CampEntity leftCampEntity = new CampEntity(GsConst.CampWar.LEFT_CAMP_ID, this.curWarStageId);
			CampEntity rightCampEntity = new CampEntity(GsConst.CampWar.RIGHT_CAMP_ID, this.curWarStageId);
			DBManager.getInstance().create(leftCampEntity);
			DBManager.getInstance().create(rightCampEntity);
			campMap.put(leftCampEntity.getCampId(), leftCampEntity);
			campMap.put(rightCampEntity.getCampId(), rightCampEntity);
		}
	}
	
	/**
	 * 加载玩家阵营战数据
	 */
	private void loadCampWarPlayerData(){
		List<CampWarEntity> campWarEntities = DBManager.getInstance().query("from CampWarEntity where stageId = ? or stageId = ?", curWarStageId, lastWarStageId);
		if(campWarEntities.size() > 0){
			Set<CampWarEntity> tmpCurMultiKillRankSet = new TreeSet<CampWarEntity>();
			Set<CampWarEntity> tmpLastMultiKillRankSet = new TreeSet<CampWarEntity>();
			
			long lastTotalReputation = 0;
			long lastTotalCoins = 0;
			for(CampWarEntity campWarEntity : campWarEntities){
				if (campWarEntity.getStageId() == this.curWarStageId){
					// 导入本期玩家阵营数据，如果正在阵营战还要导入排行数据
					if(this.status == CampStateType.CAMP_WAR_FIGHT_VALUE){
						tmpCurMultiKillRankSet.add(campWarEntity);
					}
					curPlayerIdCampEntityMap.put(campWarEntity.getPlayerId(), campWarEntity);
					if(campWarEntity.getCampId() == GsConst.CampWar.LEFT_CAMP_ID){
						campMap.get(GsConst.CampWar.LEFT_CAMP_ID).addCampWarEntity(campWarEntity);
					}else if(campWarEntity.getCampId() == GsConst.CampWar.RIGHT_CAMP_ID){
						campMap.get(GsConst.CampWar.RIGHT_CAMP_ID).addCampWarEntity(campWarEntity);
					}
				} else if (campWarEntity.getStageId() == this.lastWarStageId){
					// 统计上期玩家历史数据
					tmpLastMultiKillRankSet.add(campWarEntity);
				}
			}
			
			// 阵营战战场内连杀排行
			this.curMultiKillRankSet.clear();
			Iterator<CampWarEntity> iterCur = tmpCurMultiKillRankSet.iterator();
			for(int i = 0; i < SysBasicCfg.getInstance().getLastCampWarWinStreakMaxRank(); i++){
				if(iterCur.hasNext()){
					curMultiKillRankSet.add(iterCur.next());
				}
			}
			
			// 阵营战主界面（历史）连杀排行
			this.lastMultiKillRankSet.clear();
			Iterator<CampWarEntity> iterLast = tmpLastMultiKillRankSet.iterator();
			for(int i = 0; i < SysBasicCfg.getInstance().getLastCampWarWinStreakMaxRank(); i++){
				if(iterLast.hasNext()){
					CampWarEntity tmp = iterLast.next();
					lastTotalCoins += tmp.getTotalCoins();
					lastTotalReputation += tmp.getTotalReputation();
					lastMultiKillRankSet.add(tmp);
				}
			}
			
			// 昨日平均数据
			int lastJoinNum = lastMultiKillRankSet.size();
			if(lastJoinNum > 0) {
				this.lastAvgReputation = (int)(lastTotalReputation / lastJoinNum);
				this.lastAvgCoins = (int)(lastTotalCoins / lastJoinNum);
				
				this.lastAvgReputation *= SysBasicCfg.getInstance().getAutoCampWarAwardRatio();
				this.lastAvgCoins *= SysBasicCfg.getInstance().getAutoCampWarAwardRatio();
			} else {
				this.lastAvgReputation = 0;
				this.lastAvgCoins = 0;
			}
		}
	}
	
	
	@Override
	public boolean onTick() {
		int curSeconds = GuaJiTime.getSeconds();
		if(curSeconds >= this.nextPeriod){
			if (this.status == CampStateType.CAMP_WAR_CLOSED_VALUE) {// 阵营战进入准备阶段
				this.status = CampStateType.CAMP_WAR_PREPARE_VALUE;
				this.nextPeriod = calcNextPeriod();
				// 广播阵营战即将开始
				GsApp.getInstance().broadcastChatWorldMsg(ChatManager.getMsgJson(SysBasicCfg.getInstance().getCampWarStartWorldMsg()),
						ChatManager.getMsgJson(SysBasicCfg.getInstance().getCampWarStartChatMsg()));
				// 告知所有玩家阵营战状态切换
				Msg msg = Msg.valueOf(GsConst.MsgType.CAMPWAR_STATUS_CHANGE);
				GsApp.getInstance().broadcastMsg(msg, GsApp.getInstance().getObjMan(GsConst.ObjType.PLAYER));
			} else if(this.status == CampStateType.CAMP_WAR_PREPARE_VALUE){ // 阵营战进入战斗阶段
				this.status = CampStateType.CAMP_WAR_FIGHT_VALUE;
				this.round += 1;
				this.nextPeriod = calcNextPeriod();
				
				// 扣除所有自动玩家钻石
				GsApp.getInstance().postCommonTask(new DeductAutoPlayerTask(this.curAutoJoinEntity.getAutoCampWarPlayerIds()));
				
				// 抛战斗任务到后台线程
				GsApp.getInstance().postCommonTask(new CamWarRoundTask(campMap.get(GsConst.CampWar.LEFT_CAMP_ID), 
						campMap.get(GsConst.CampWar.RIGHT_CAMP_ID)));
			} else if(this.status == CampStateType.CAMP_WAR_FIGHT_VALUE){
				this.round += 1;
				this.nextPeriod = calcNextPeriod();
				
				this.campReportMap.clear();
				this.personalReportMap.clear();
				
				if(round <= calcMaxRound()) { // 阵营战进入下一轮战斗
					// 抛战斗任务到后台线程
					GsApp.getInstance().postCommonTask(new CamWarRoundTask(campMap.get(GsConst.CampWar.LEFT_CAMP_ID), 
							campMap.get(GsConst.CampWar.RIGHT_CAMP_ID)));
				} else { // 阵营战进入展示时间
					this.status = CampStateType.CAMP_WAR_SHOW_RESULT_VALUE;
					
					// 判定阵营输赢
					whoCampWin();
					campMap.get(GsConst.CampWar.LEFT_CAMP_ID).notifyUpdate(true);
					campMap.get(GsConst.CampWar.RIGHT_CAMP_ID).notifyUpdate(true);
					
					// 告知所有玩家阵营战结束，进入公示时间
					Msg msg = Msg.valueOf(GsConst.MsgType.CAMPWAR_END);
					GsApp.getInstance().broadcastMsg(msg, GsApp.getInstance().getObjMan(GsConst.ObjType.PLAYER));
					
					// 广播阵营战结束
					GsApp.getInstance().broadcastChatWorldMsg(ChatManager.getMsgJson(SysBasicCfg.getInstance().getCampWarStopWorldMsg()), 
							ChatManager.getMsgJson(SysBasicCfg.getInstance().getCampWarStopChatMsg()));
					
					// 抛发奖任务到后台线程
					Map<Integer, CampWarEntity> tmpPlayerIdCampEntityMap = new HashMap<Integer, CampWarEntity>();
					tmpPlayerIdCampEntityMap.putAll(this.curPlayerIdCampEntityMap);
					GsApp.getInstance().postCommonTask(new CampWarRewardTask(tmpPlayerIdCampEntityMap));
				}
			} else if(this.status == CampStateType.CAMP_WAR_SHOW_RESULT_VALUE){// 阵营战展示时间结束，阵营战关闭
				this.status = CampStateType.CAMP_WAR_CLOSED_VALUE;
				this.nextPeriod = calcNextPeriod();
				
				// 告知所有玩家阵营战关闭
				Msg msg = Msg.valueOf(GsConst.MsgType.CAMPWAR_CLOSE);
				GsApp.getInstance().broadcastMsg(msg, GsApp.getInstance().getObjMan(GsConst.ObjType.PLAYER));
				
				// 下轮阵营战初始化
				this.reset();
			}
			
			logger.info("campwar info -- curStageId : {}, lastStageId : {}, status : {}, finish round: {}", 
					this.curWarStageId, this.lastWarStageId, this.status, this.round);
		}
		return true;
	}
	
	/**
	 * 清理上期数据
	 */
	private void clear(){
		this.round = 0;
		this.curPlayerIdCampEntityMap.clear();
		this.campMap.clear();
		this.campReportMap.clear();
		this.personalReportMap.clear();
	}

	/**
	 * 上期阵营战期号
	 * @return
	 */
	private int calcLastWarStageId() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		long todayAM0Time = GuaJiTime.getAM0Date().getTime();
		SysBasicCfg sysCfg = SysBasicCfg.getInstance();
		int todayBattleStopTime  = (int)(todayAM0Time/1000) + sysCfg.getCampWarBattleStopTime();
		int curSeconds = GuaJiTime.getSeconds();
		if(curSeconds < todayBattleStopTime){
			Calendar calendar = GuaJiTime.getCalendar();
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			String date = sdf.format(calendar.getTime());
			return Integer.valueOf(date);
		} else {
			String date = sdf.format(GuaJiTime.getAM0Date());
			return Integer.valueOf(date);
		}
	}

	/**
	 * 计算本期阵营战期号
	 * @return
	 */
	private int calcWarStageId(){
		int curSeconds = GuaJiTime.getSeconds();
		int todayShowStopTime = (int)(GuaJiTime.getAM0Date().getTime()/1000) + SysBasicCfg.getInstance().getCampWarShowStopTime();
		if(curSeconds >= todayShowStopTime){
			Calendar calendar = GuaJiTime.getCalendar();
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			String date = GuaJiTime.DATE_FORMATOR_DAYNUM(calendar.getTime());
			return Integer.valueOf(date);
		} else {
			String date = GuaJiTime.DATE_FORMATOR_DAYNUM(GuaJiTime.getAM0Date());
			return Integer.valueOf(date);
		}
	}
	
	/**
	 * 计算本期阵营战状态
	 * @return
	 */
	private int calcCurWarStatus(){
		long todayAM0Time = GuaJiTime.getAM0Date().getTime();
		SysBasicCfg sysCfg = SysBasicCfg.getInstance();
		int todayPrepareTime  = (int)(todayAM0Time/1000) + sysCfg.getCampWarPrepareTime();
		int todayBattleStartTime  = (int)(todayAM0Time/1000) + sysCfg.getCampWarBattleStartTime();
		int todayBattleStopTime  = (int)(todayAM0Time/1000) + sysCfg.getCampWarBattleStopTime();
		int todayShowStopTime = (int)(todayAM0Time/1000) + sysCfg.getCampWarShowStopTime();
		
		int curSeconds = GuaJiTime.getSeconds();
		if(curSeconds < todayPrepareTime){
			return CampStateType.CAMP_WAR_CLOSED_VALUE;
		} else if(curSeconds < todayBattleStartTime){
			return CampStateType.CAMP_WAR_PREPARE_VALUE;
		} else if(curSeconds < todayBattleStopTime){
			return CampStateType.CAMP_WAR_FIGHT_VALUE;
		} else if(curSeconds < todayShowStopTime){
			return CampStateType.CAMP_WAR_SHOW_RESULT_VALUE;
		} else {
			return CampStateType.CAMP_WAR_CLOSED_VALUE;
		}
	}
	
	/**
	 * 计算下次Tick触发时间
	 */
	private int calcNextPeriod(){
		long todayAM0Time = GuaJiTime.getAM0Date().getTime();
		SysBasicCfg sysCfg = SysBasicCfg.getInstance();
		int todayPrepareTime = (int)(todayAM0Time/1000) + sysCfg.getCampWarPrepareTime();
		int todayBattleStartTime  = (int)(todayAM0Time/1000) + sysCfg.getCampWarBattleStartTime();
		int todayBattleStopTime  = (int)(todayAM0Time/1000) + sysCfg.getCampWarBattleStopTime();
		int todayShowStopTime = (int)(todayAM0Time/1000) + sysCfg.getCampWarShowStopTime();
		
		
		int curSeconds = GuaJiTime.getSeconds();
		if(curSeconds < todayPrepareTime){
			return todayPrepareTime;
		} else if(curSeconds < todayBattleStartTime){
			return todayBattleStartTime;
		} else if(curSeconds < todayBattleStopTime){
			return todayBattleStartTime + round * sysCfg.getCampWarBattlePeriod();
		} else if(curSeconds < todayShowStopTime){
			return todayShowStopTime;
		} else {
			int tomorrowPrepareTime  = (int)(GuaJiTime.getNextAM0Date()/1000) + sysCfg.getCampWarPrepareTime();
			return tomorrowPrepareTime;
		}
	}
	
	/**
	 * 计算当前阵营战进行的轮数
	 */
	private int calcCurBattleRound(){
		long todayAM0Time = GuaJiTime.getAM0Date().getTime();
		SysBasicCfg sysCfg = SysBasicCfg.getInstance();
		int todayBattleStartTime  = (int)(todayAM0Time/1000) + sysCfg.getCampWarBattleStartTime();
		int todayBattleStopTime  = (int)(todayAM0Time/1000) + sysCfg.getCampWarBattleStopTime();
		
		int curSeconds = GuaJiTime.getSeconds();
		if(curSeconds >= todayBattleStartTime && curSeconds < todayBattleStopTime){
			return ((curSeconds - todayBattleStartTime) / sysCfg.getCampWarBattlePeriod()) + 1;
		}
		return 0;
	}
	
	/**
	 * 计算阵营战最大轮次
	 * @return
	 */
	private int calcMaxRound(){
		SysBasicCfg sysCfg = SysBasicCfg.getInstance();
		int time = Math.max(0, Math.abs(sysCfg.getCampWarBattleStartTime() - sysCfg.getCampWarBattleStopTime()));
		return time/sysCfg.getCampWarBattlePeriod();
	} 
	
	/**
	 * 计算当前需要分配玩家的阵营
	 * @return
	 */
	private int allocateCamp(){
		CampEntity leftCamp = this.campMap.get(GsConst.CampWar.LEFT_CAMP_ID);
		CampEntity rightCamp = this.campMap.get(GsConst.CampWar.RIGHT_CAMP_ID);
		if(leftCamp.getCampPlayerNum() > rightCamp.getCampPlayerNum()){
			return GsConst.CampWar.RIGHT_CAMP_ID;
		} else if(leftCamp.getCampPlayerNum() < rightCamp.getCampPlayerNum()){
			return GsConst.CampWar.LEFT_CAMP_ID;
		} else {
			if(leftCamp.getTotalFightValue() > rightCamp.getTotalFightValue()){
				return GsConst.CampWar.RIGHT_CAMP_ID;
			} else {
				return GsConst.CampWar.LEFT_CAMP_ID;
			}
		}
	}
	
	/**
	 * 判定那方阵营获胜
	 * @return
	 */
	private int whoCampWin(){
		// 判定阵营输赢
		CampEntity leftCamp = campMap.get(GsConst.CampWar.LEFT_CAMP_ID);
		CampEntity rightCamp = campMap.get(GsConst.CampWar.RIGHT_CAMP_ID);
		if(leftCamp.getTotalBattleScore() != rightCamp.getTotalBattleScore()){
			if(leftCamp.getTotalBattleScore() > rightCamp.getTotalBattleScore()){
				leftCamp.setIsWin(1);
				return leftCamp.getCampId();
			}else{
				rightCamp.setIsWin(1);
				return rightCamp.getCampId();
			}
		}
		return 0;
	}
	
	/**
	 * 添加本轮战报
	 */
	private void addRoundReport(CampReport report){
		if(campReportMap.containsKey(report.getReportType())){
			ConcurrentSkipListSet<CampReport> campReports = (ConcurrentSkipListSet<CampReport>)campReportMap.get(report.getReportType());
			campReports.add(report);
			if(report.getReportType() == reportType.CONTINUE_WIN_TYPE_VALUE){
				if(campReports.size() > SysBasicCfg.getInstance().getCampWarMaxWinStreakReport()){
					campReports.remove(campReports.last());
				}
			}
			
			if(report.getReportType() == reportType.END_WIN_TYPE_VALUE){
				if(campReports.size() > SysBasicCfg.getInstance().getCampWarMaxEndStreakReport()){
					campReports.remove(campReports.last());
				}
			}
		} else{
			Set<CampReport> campReports = new ConcurrentSkipListSet<CampReport>();
			campReports.add(report);
			campReportMap.put(report.getReportType(), campReports);
		}
	}
	
	
	/**
	 * 获取阵营战期号
	 * @return
	 */
	public int getCurWarStageId() {
		return curWarStageId;
	}

	/**
	 * 获取阵营战当前状态
	 * @return
	 */
	public int getStatus() {
		return status;
	}
	
	/**
	 * 当前阵营战轮数
	 * @return
	 */
	public int getRound() {
		return round;
	}
	

	/**
	 * 上次获胜阵营Id
	 * @return
	 */
	public int getLastWinCampId(){
		return this.lastWinCampId;
	}
	
	/**
	 * 计算阵营战当前状态剩余时间
	 * @return
	 */
	public int calcCurStatuesLeftTime() {
		long todayAM0Time = GuaJiTime.getAM0Date().getTime();
		SysBasicCfg sysCfg = SysBasicCfg.getInstance();
		int todayBattleStartTime  = (int)(todayAM0Time/1000) + sysCfg.getCampWarBattleStartTime();
		int todayBattleStopTime  = (int)(todayAM0Time/1000) + sysCfg.getCampWarBattleStopTime();		
		int todayShowStopTime = (int)(todayAM0Time/1000) + sysCfg.getCampWarShowStopTime();
		
		int curSeconds = GuaJiTime.getSeconds();
		if(curSeconds < todayBattleStartTime){
			return todayBattleStartTime - curSeconds;
		} else if(curSeconds < todayBattleStopTime){
			return todayBattleStopTime - curSeconds;
		} else if(curSeconds < todayShowStopTime){
			return 0;
		} else{
			int tomorrowBattleStartTime = (int)(GuaJiTime.getNextAM0Date()/1000) + SysBasicCfg.getInstance().getCampWarBattleStartTime();
			return tomorrowBattleStartTime - curSeconds;
		}
	}
	
	/**
	 * 上次平均声望产出
	 * @return
	 */
	public int getLastAvgReputation() {
		return lastAvgReputation;
	}

	/**
	 * 上次平均金币产生
	 * @return
	 */
	public int getLastAvgCoins() {
		return lastAvgCoins;
	}

	/**
	 * 上期阵营战连杀排行（10 人）
	 * @return
	 */
	public List<MultiKillRankInfo.Builder> genLastCampWarRankList(){
		List<MultiKillRankInfo.Builder> lastRankList = new ArrayList<MultiKillRankInfo.Builder>();
		int rank = 0;
		for(CampWarEntity campWarEntity : lastMultiKillRankSet){
			rank++;
			MultiKillRankInfo.Builder info = BuilderUtil.genCampWarMultiKillRankInfo(campWarEntity, rank);
			lastRankList.add(info);
		}
		
		return lastRankList;
	}
	
	/**
	 * 正在进行的阵营战击杀排行（3 人）
	 * @return
	 */
	public List<CampWar.MultiKillRankInfo.Builder> getCurCampWarRankList() {
		List<MultiKillRankInfo.Builder> curRankList = new ArrayList<MultiKillRankInfo.Builder>();
		int rank = 0;
		for(CampWarEntity campWarEntity : curMultiKillRankSet){
			rank++;
			MultiKillRankInfo.Builder info = BuilderUtil.genCampWarMultiKillRankInfo(campWarEntity, rank);
			curRankList.add(info);
			if(rank >= SysBasicCfg.getInstance().getCurCampWarWinStreakMaxRank()){
				break;
			}
		}
		return curRankList;
	}

	/**
	 * 获取个人阵营战数据
	 * @param playerId
	 * @return
	 */
	public PersonalCampWarInfo.Builder getPersonalCampWarInfo(int playerId) {
		if(curPlayerIdCampEntityMap.containsKey(playerId)){
			CampWarEntity campWarEntity = curPlayerIdCampEntityMap.get(playerId);
			return BuilderUtil.genPersonalCampWarInfo(campWarEntity);
		}
		return null;
	}

	/**
	 * 判断玩家是否自动加入阵营战
	 * @param id
	 * @return
	 */
	public boolean isAutoCampWar(int playerId) {
		return curAutoJoinEntity.isAutoCarmWar(playerId);
	}

	/**
	 * 报名自动阵营战
	 */
	public void addAutoCampWar(int playerId) {
		curAutoJoinEntity.addAutoCampWar(playerId);
	}

	/**
	 * 取消自动阵营战
	 * @param playerId
	 */
	public void cancelAutoCampWar(int playerId) {
		curAutoJoinEntity.cancelAutoCarmWar(playerId);
	}
	
	/**
	 * 是否已经加入本期阵营战
	 * @param playerId
	 * @return
	 */
	public boolean isJoinBattlefield(int playerId) {
		if(curPlayerIdCampEntityMap.containsKey(playerId)){
			return true;
		}
		return false;
	}

	/**
	 * 加入阵营战战场
	 * @param player
	 * @return
	 */
	public PersonalCampWarInfo.Builder enterCampWarBattlefield(Player player) {
		CampWarEntity campWarEntity = null;
		if(curPlayerIdCampEntityMap.containsKey(player.getId())){
			campWarEntity = curPlayerIdCampEntityMap.get(player.getId());
		} else {
			PlayerData playerData = player.getPlayerData();
			RoleEntity roleEntity = playerData.getMainRole();
			int campId = allocateCamp();
			campWarEntity = new CampWarEntity(player.getId(), roleEntity.getName(), 
					roleEntity.getRoleCfg().getId(), this.curWarStageId, campId, 
					roleEntity.getAttribute().getValue(Const.attr.HP), player.getFightValue());
			campMap.get(campId).addCampWarEntity(campWarEntity);
			DBManager.getInstance().create(campWarEntity);
			curPlayerIdCampEntityMap.put(campWarEntity.getPlayerId(), campWarEntity);
			
		}
		return BuilderUtil.genPersonalCampWarInfo(campWarEntity);
	}
	
	/**
	 * 模拟加入阵营战战场
	 * @param player
	 * @return
	 */
	public void enterCampWarBattlefieldTest(int playerId) {
		CampWarEntity campWarEntity = null;
		if(curPlayerIdCampEntityMap.containsKey(playerId)){
			campWarEntity = curPlayerIdCampEntityMap.get(playerId);
		} else {
			int campId = allocateCamp();
			PlayerSnapshotInfo.Builder snapBuilder = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
			if(snapBuilder == null) {
				return ;
			}
			campWarEntity = new CampWarEntity(playerId, snapBuilder.getMainRoleInfo().getName(), 
					snapBuilder.getMainRoleInfo().getItemId(), this.curWarStageId, campId, 
					GameUtil.getAttr(snapBuilder.getMainRoleInfoBuilder(), Const.attr.HP), snapBuilder.getMainRoleInfo().getFight());
			campMap.get(campId).addCampWarEntity(campWarEntity);
			DBManager.getInstance().create(campWarEntity);
			curPlayerIdCampEntityMap.put(campWarEntity.getPlayerId(), campWarEntity);
			System.out.println("模拟加入阵营战:playerId:" + playerId + ",camp:" + campId);
		}
	}

	/**
	 * 获取当前双方阵营信息
	 * @return
	 */
	public List<CampWar.CampInfo.Builder> getAllCampInfo() {
		List<CampWar.CampInfo.Builder> campInfoList = new ArrayList<CampWar.CampInfo.Builder>();
		for(Map.Entry<Integer, CampEntity> entry : campMap.entrySet()){
			CampInfo.Builder builder = CampInfo.newBuilder();
			builder.setCampId(entry.getValue().getCampId());
			builder.setTotalScore(entry.getValue().getTotalBattleScore());
			campInfoList.add(builder);
		}
		return campInfoList;
	}

	/**
	 * 获取新增战报
	 * @return
	 */
	public List<CampReportInfo.Builder> getNewCampReportInfoList(int playerId) {
		List<CampWar.CampReportInfo.Builder> campReportlist = new ArrayList<CampReportInfo.Builder>();
		
		if(personalReportMap.containsKey(playerId)){
			CampReport personalReport = personalReportMap.get(playerId);
			campReportlist.add(personalReport.toCampReportInfo());
		}
		
		for(Map.Entry<Integer, Set<CampReport>> mapEntry : campReportMap.entrySet()){
			Set<CampReport> set = mapEntry.getValue();
			int type = mapEntry.getKey();
			if(type == CampWar.reportType.CONTINUE_WIN_TYPE_VALUE){
				int counter = 0; 
				for(CampReport report : set){
					campReportlist.add(report.toCampReportInfo());
					counter++;
					if (counter >= SysBasicCfg.getInstance().getCampWarMaxWinStreakReport()){
						break;
					}
				}
			}
		}
		
		return campReportlist;
	}
	
	/**
	 * 鼓舞加成
	 */
	public boolean inspire(int playerId) {
		if(isJoinBattlefield(playerId)){
			CampWarEntity campEntity = curPlayerIdCampEntityMap.get(playerId);
			campEntity.inspire();
			if(this.status == CampStateType.CAMP_WAR_PREPARE_VALUE){
				// 立即把鼓舞加成血量补上
				campEntity.setCurRemainBlood(campEntity.getCurRemainBlood());
			}
			campEntity.notifyUpdate(true);
			return true;
		}
		return false;
	}
	
	/**
	 * 两玩家一轮战斗结束后
	 * @param attackerId
	 * @param defenderId
	 * @param result
	 * @param attakerRoleInfoBuilder
	 * @param defenceRoleInfoBuilder
	 */
	public void onRoundFightEnd(int attackerId, int defenderId, int result, Builder attakerRoleInfoBuilder, Builder defenceRoleInfoBuilder) {
		if(curPlayerIdCampEntityMap.containsKey(attackerId)){
			int winnerId = (result > 0) ? attackerId : defenderId;
			int loserId = (result > 0) ? defenderId : attackerId;
			Builder winnerRoleInfo = (result > 0) ? attakerRoleInfoBuilder : defenceRoleInfoBuilder;
			Builder loserRoleInfo = (result > 0) ? defenceRoleInfoBuilder : attakerRoleInfoBuilder;
			
			CampWarEntity winnerCampWarEntitiy = curPlayerIdCampEntityMap.get(winnerId);
			CampWarEntity loserCampWarEntitiy = curPlayerIdCampEntityMap.get(loserId);
			
			CampReport continueWinReport = new CampReport(reportType.CONTINUE_WIN_TYPE_VALUE, this.round);
			CampReport personalWinReport = new CampReport(reportType.PERSONAL_WIN_TYPE_VALUE, this.round);
			CampReport personalLoseReport = new CampReport(reportType.PERSONAL_LOSE_TYPE_VALUE, this.round);
			
			{// 发胜者奖励
				if(winnerId > 0 && winnerRoleInfo != null && winnerCampWarEntitiy != null){
					// 当前连胜+1
					int winnerCurWinStreak = winnerCampWarEntitiy.getCurWinStreak() + 1;
					int oldMaxWinStreak = winnerCampWarEntitiy.getMaxWinStreak();
					winnerCampWarEntitiy.setCurWinStreak(winnerCurWinStreak);
					if( winnerCurWinStreak > oldMaxWinStreak ){
						// 重排战场连杀排行
						curMultiKillRankSet.remove(winnerCampWarEntitiy);
						winnerCampWarEntitiy.setMaxWinStreak(winnerCurWinStreak);
						curMultiKillRankSet.add(winnerCampWarEntitiy);
						if(curMultiKillRankSet.size() > SysBasicCfg.getInstance().getLastCampWarWinStreakMaxRank()){
							CampWarEntity last = curMultiKillRankSet.last();
							curMultiKillRankSet.remove(last);
						}
					}
					
					// 总胜场+1
					int totalWinTimes = winnerCampWarEntitiy.getTotalWin() + 1;
					winnerCampWarEntitiy.setTotalWin(totalWinTimes);
					
					// 累加单场胜利奖励金币
					int winnerLevel = winnerRoleInfo.getLevel();
					int totalCoins = winnerLevel * SysBasicCfg.getInstance().getCampWarRoundWinGoldRatio() + winnerCampWarEntitiy.getTotalCoins();
					winnerCampWarEntitiy.setTotalCoins(totalCoins);
					// 累加单场胜利奖励声望
					int totalReputation = SysBasicCfg.getInstance().getCampWarRoundWinReputation() + winnerCampWarEntitiy.getTotalReputation();
					winnerCampWarEntitiy.setTotalReputation(totalReputation);
					
					// 连胜战报
					continueWinReport.setCampId(winnerCampWarEntitiy.getCampId());//胜利者所在阵营
					continueWinReport.addArgString(winnerRoleInfo.getName());
					continueWinReport.addArgString("bye");
					continueWinReport.addArgString(String.valueOf(winnerCurWinStreak));
					continueWinReport.setSortArg(winnerCurWinStreak);
					continueWinReport.addArgString(String.valueOf(winnerLevel * SysBasicCfg.getInstance().getCampWarRoundWinGoldRatio()));
					continueWinReport.addArgString(String.valueOf(SysBasicCfg.getInstance().getCampWarRoundWinReputation()));
					// 个人战报-胜利
					personalWinReport.setCampId(winnerCampWarEntitiy.getCampId());//胜利者所在阵营
					personalWinReport.addArgString("bye");
					personalWinReport.addArgString(String.valueOf(winnerCurWinStreak));
					personalWinReport.addArgString(String.valueOf(winnerLevel * SysBasicCfg.getInstance().getCampWarRoundWinGoldRatio()));
					personalWinReport.addArgString(String.valueOf(SysBasicCfg.getInstance().getCampWarRoundWinReputation()));
					// 个人战报-失败
					personalLoseReport.setCampId(winnerCampWarEntitiy.getCampId());//胜利者所在阵营
					personalLoseReport.addArgString(winnerRoleInfo.getName());
					
					
					TreeMap<Object, CampWarWinStreakCfg> winStreakCfgMap = (TreeMap<Object, CampWarWinStreakCfg>)ConfigManager.getInstance().getConfigMap(CampWarWinStreakCfg.class);
					// 战报连杀奖励参数占位
					continueWinReport.addArgString("0");
					continueWinReport.addArgString("0");
					personalWinReport.addArgString("0");
					personalWinReport.addArgString("0");
					// 累加连杀奖励
					if(winStreakCfgMap.containsKey(winnerCurWinStreak)){
						CampWarWinStreakCfg cfg = winStreakCfgMap.get(winnerCurWinStreak);
						totalCoins += cfg.getWinCoinsRatio()*winnerRoleInfo.getLevel();
						winnerCampWarEntitiy.setTotalCoins(totalCoins);
						
						totalReputation += cfg.getWinReputation();
						winnerCampWarEntitiy.setTotalReputation(totalReputation);
						// 连胜战报
						continueWinReport.updateArgString(continueWinReport.getArgNum() -2, String.valueOf(cfg.getWinCoinsRatio()*winnerRoleInfo.getLevel()));
						continueWinReport.updateArgString(continueWinReport.getArgNum() -1, String.valueOf(cfg.getWinReputation()));
						// 个人战报-胜利
						personalWinReport.updateArgString(personalWinReport.getArgNum() -2, String.valueOf(cfg.getWinCoinsRatio()*winnerRoleInfo.getLevel()));
						personalWinReport.updateArgString(personalWinReport.getArgNum() -1, String.valueOf(cfg.getWinReputation()));
					}
					
					// 战报终结奖励参数占位
					continueWinReport.addArgString("0");
					continueWinReport.addArgString("0");
					personalWinReport.addArgString("0");
					personalWinReport.addArgString("0");
					// 累加终结他人连胜奖励
					if(loserCampWarEntitiy != null && loserRoleInfo != null){
						// 被终结的次数
						int loserCurWinStreak = loserCampWarEntitiy.getCurWinStreak();
						CampWarWinStreakCfg cfg = winStreakCfgMap.get(loserCurWinStreak);
						
						// 覆盖失败者初始名(战报构造是会首先填充一个轮数参数，所以失败者名字的index+1)
						continueWinReport.updateArgString(2, loserRoleInfo.getName());
						personalWinReport.updateArgString(1, loserRoleInfo.getName());
						
						if(cfg != null){
							totalCoins += cfg.getLoseCoinsRatio()*loserRoleInfo.getLevel();
							winnerCampWarEntitiy.setTotalCoins(totalCoins);
							
							totalReputation += cfg.getLoseReputation();
							winnerCampWarEntitiy.setTotalReputation(totalReputation);
							// 连胜战报
							continueWinReport.updateArgString(continueWinReport.getArgNum() -2, String.valueOf(cfg.getLoseCoinsRatio()*loserRoleInfo.getLevel()));
							continueWinReport.updateArgString(continueWinReport.getArgNum() -1, String.valueOf(cfg.getLoseReputation()));
							
							// 个人战报-胜利
							personalWinReport.updateArgString(personalWinReport.getArgNum() -2, String.valueOf(cfg.getLoseCoinsRatio()*loserRoleInfo.getLevel()));
							personalWinReport.updateArgString(personalWinReport.getArgNum() -1, String.valueOf(cfg.getLoseReputation()));
						}
						// 加入连胜战报
						addRoundReport(continueWinReport);
					}
					
					// 加入个人胜利战报
					personalReportMap.put(winnerId, personalWinReport);
				}
			}
			
			{// 发败者奖励
				if(loserId > 0 && loserRoleInfo != null && loserCampWarEntitiy != null){
					// 当前连胜清0
					loserCampWarEntitiy.setCurWinStreak(0);
					// 复活玩家
					loserCampWarEntitiy.revive();
					// 总败场+1
					int totalLoseTimes = loserCampWarEntitiy.getTotalLose() + 1;
					loserCampWarEntitiy.setTotalLose(totalLoseTimes);
					// 累加单场失败奖励金币
					int loserLevel = loserRoleInfo.getLevel();
					int totalCoins = loserLevel * SysBasicCfg.getInstance().getCampWarRoundWinGoldRatio()/2 + loserCampWarEntitiy.getTotalCoins();
					loserCampWarEntitiy.setTotalCoins(totalCoins);
					// 累加单场失败奖励声望
					int totalReputation = SysBasicCfg.getInstance().getCampWarRoundWinReputation()/2 + loserCampWarEntitiy.getTotalReputation();
					loserCampWarEntitiy.setTotalReputation(totalReputation);
					
					// 个人战报-失败
					personalLoseReport.addArgString(String.valueOf(loserLevel* SysBasicCfg.getInstance().getCampWarRoundWinGoldRatio()/2));
					personalLoseReport.addArgString(String.valueOf(SysBasicCfg.getInstance().getCampWarRoundWinReputation()/2));
					personalReportMap.put(loserId, personalLoseReport);
				}
			}
			
			if(winnerCampWarEntitiy != null){
				CampEntity winnerCamp = campMap.get(winnerCampWarEntitiy.getCampId());
				winnerCamp.addTotalBattleScore(SysBasicCfg.getInstance().getCampWarWinnerAddScore());
				winnerCamp.notifyUpdate(true);
				
				if(loserCampWarEntitiy != null) {
					winnerCampWarEntitiy.addRoundResult(1);
				} else {
					winnerCampWarEntitiy.addRoundResult(2);
				}
				winnerCampWarEntitiy.notifyUpdate(true);
			}
			
			if(loserCampWarEntitiy != null){
				CampEntity loserCamp = campMap.get(loserCampWarEntitiy.getCampId());
				loserCamp.addTotalBattleScore(SysBasicCfg.getInstance().getCampWarLoserAddScore());
				loserCamp.notifyUpdate(true);
				
				loserCampWarEntitiy.addRoundResult(0);
				loserCampWarEntitiy.notifyUpdate(true);
			}
		}
	}
	
	/**
	 * 本期阵营战结束后，为所有参与的玩家发放奖励
	 * @param playerIdCampEntityMap
	 */
	public void grantCampWarAward(Map<Integer, CampWarEntity> playerIdCampEntityMap) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(GuaJiTime.getAM0Date());
		
		long campWarTotalCoins = 0;
		long campWarTotalReputation = 0;
		
		// 判断哪方阵营赢, 最终收益
		this.lastWinCampId = whoCampWin();
		for(Map.Entry<Integer, CampWarEntity> entry: playerIdCampEntityMap.entrySet()){
			CampWarEntity campWarEntity = entry.getValue();
			AwardItems awardItems = new AwardItems();
			int rewardCoins = 0;
			int rewardReputation = 0;
			
			try {
				// 获胜阵营
				if(campWarEntity.getCampId() == this.lastWinCampId){
					rewardCoins = (int) (campWarEntity.getTotalCoins());
					campWarEntity.setTotalCoins(rewardCoins);
					rewardReputation = (int) (campWarEntity.getTotalReputation() + SysBasicCfg.getInstance().getWinCampExtraReputation());
					campWarEntity.setTotalReputation(rewardReputation);
					campWarEntity.notifyUpdate(true);
					
					// 统计前十名金币贡献总和
					if(this.curMultiKillRankSet.contains(campWarEntity)){
						campWarTotalCoins += rewardCoins;
						campWarTotalReputation += rewardReputation;
					}
					
					awardItems.addCoin(rewardCoins);
					awardItems.addReputationValue(rewardReputation);
					if(this.lastWinCampId == GsConst.CampWar.LEFT_CAMP_ID){
						MailManager.createMail(campWarEntity.getPlayerId(), MailType.Reward_VALUE, GsConst.MailId.CAMPWAR_LEFT_CAMP_WIN, 
								"霜狼（左）阵营胜利奖励", awardItems, date);
					}else{
						MailManager.createMail(campWarEntity.getPlayerId(), MailType.Reward_VALUE, GsConst.MailId.CAMPWAR_RIGHT_CAMP_WIN, 
								"炎狮（右）阵营胜利奖励", awardItems, date);
					}
				} else { // 失败阵营
					rewardCoins = campWarEntity.getTotalCoins();
					campWarEntity.setTotalCoins(rewardCoins);
					rewardReputation = campWarEntity.getTotalReputation();
					campWarEntity.setTotalReputation(rewardReputation);
					campWarEntity.notifyUpdate(true);
					
					// 统计前十名金币贡献总和
					if(this.curMultiKillRankSet.contains(campWarEntity)){
						campWarTotalCoins += rewardCoins;
						campWarTotalReputation += rewardReputation;
					}
					
					awardItems.addCoin(rewardCoins);
					awardItems.addReputationValue(rewardReputation);
					if(campWarEntity.getCampId() == GsConst.CampWar.LEFT_CAMP_ID){
						MailManager.createMail(campWarEntity.getPlayerId(), MailType.Reward_VALUE, GsConst.MailId.CAMPWAR_LEFT_CAMP_LOSE, 
								"霜狼（左）阵营战失败奖励", awardItems, date);
					}else{
						MailManager.createMail(campWarEntity.getPlayerId(), MailType.Reward_VALUE, GsConst.MailId.CAMPWAR_RIGHT_CAMP_LOSE, 
								"炎狮（右）阵营战失败奖励", awardItems, date);
					}
				}
				// 增加魔兽元气
				int addVitality = SysBasicCfg.getInstance().getCampWarAddVitality();
				AllianceManager.getInstance().addAllianceBossVitality(campWarEntity.getPlayerId(), addVitality, Action.TAKE_PART_IN_CAMPWAR);
				// 记录发奖日志
				BehaviorLogger.log4Service(campWarEntity.getPlayerId(), Source.SYS_OPERATION, Action.SYSTEM, 
						Params.valueOf("playerId", campWarEntity.getPlayerId()), 
						Params.valueOf("type", "grantCampWarAward"));
			} catch(Exception e) {
				MyException.catchException(e);
			}
		}
		
		// 将主界面平均奖励数据设置为刚刚结束的阵营战平均奖励
		this.lastAvgCoins = 0;
		this.lastAvgReputation = 0;
		int count = this.curMultiKillRankSet.size();
		if( count > 0){
			this.lastAvgCoins = (int)(campWarTotalCoins / count);
			this.lastAvgReputation = (int)(campWarTotalReputation / count);
		}
		this.lastAvgCoins *= SysBasicCfg.getInstance().getAutoCampWarAwardRatio();
		this.lastAvgReputation *= SysBasicCfg.getInstance().getAutoCampWarAwardRatio();
		
		// 将主界面排行数据设置为刚刚结束的阵营战排行
		this.lastMultiKillRankSet.clear();
		this.lastMultiKillRankSet.addAll(this.curMultiKillRankSet);
		this.curMultiKillRankSet.clear();
		
		// 给自动阵营战的玩家发平均奖励
		ConcurrentHashMap<Integer, Integer> autoCampWarPlayerIds = this.curAutoJoinEntity.getAutoCampWarPlayerIds();
		AwardItems awardItems = new AwardItems();
		awardItems.addCoin(lastAvgCoins);
		awardItems.addReputationValue(lastAvgReputation);
		for(int playerId : autoCampWarPlayerIds.keySet()){
			// 推送参加激流谷任务
			QuestEventBus.fireQuestEventOneTime(QuestEventType.JI_LIU_GU, GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId));
			MailManager.createMail(playerId, MailType.Reward_VALUE, GsConst.MailId.CAMPWAR_AUTO_AWARDS, "阵营战投资奖励", awardItems, date);
			// 增加战场挂机玩家所在公会的魔兽元气
			int addVitality = SysBasicCfg.getInstance().getCampWarAddVitality();
			AllianceManager.getInstance().addAllianceBossVitality(playerId, addVitality, Action.TAKE_PART_IN_CAMPWAR);
		}
	}
	
	/**
	 * 扣除自动参与的玩家钻石
	 */
	public void deductAutoPlayerGold(ConcurrentHashMap<Integer, Integer> autoCampWarPlayerIds){
		int price = SysBasicCfg.getInstance().getAutoCampWarGold();
		
		ArrayList<Integer> goldLackPlayerIds = new ArrayList<Integer>();
		for(int playerId : autoCampWarPlayerIds.keySet()){
			try{
				Player player = PlayerUtil.queryPlayer(playerId);
				if(player != null) {
					if (player.getGold() >= price) {
						// 在线玩家扣费
						player.consumeGold(price, Action.AUTO_CAMPWAR);
						ConsumeItems.valueOf(changeType.CHANGE_GOLD, price).pushChange(player);
					} else {
						goldLackPlayerIds.add(playerId);
						// 由于钻石不足投资失败，请手动加入战斗
						player.sendError(0, Status.error.AUTO_CAMPWAR_GOLD_LACK_VALUE);
					}
					continue;
				}
				
				// 离线玩家扣费
				List<PlayerEntity> playerEntities = DBManager.getInstance().query("from PlayerEntity where id = ?", playerId);
				if (playerEntities.size() > 0) {
					PlayerEntity playerEntity = (PlayerEntity) playerEntities.get(0);
					if(playerEntity.getTotalGold() >= price){
						PlayerData playerData = new PlayerData(null);
						playerData.setPlayerEntity(playerEntity);
						playerData.loadActivity();
						PlayerUtil.offlinePlayerConsumeGold(playerData, price, Action.AUTO_CAMPWAR);
					}else{
						goldLackPlayerIds.add(playerId);
					}
				} 
			} catch(Exception e){
				MyException.catchException(e);
			}
		}
		
		for(int playerId : goldLackPlayerIds){
			this.curAutoJoinEntity.cancelAutoCarmWar(playerId);
		}
		
		// 告知所有玩家阵营战状态切换
		Msg msg = Msg.valueOf(GsConst.MsgType.CAMPWAR_STATUS_CHANGE);
		GsApp.getInstance().broadcastMsg(msg, GsApp.getInstance().getObjMan(GsConst.ObjType.PLAYER));
	}
}
