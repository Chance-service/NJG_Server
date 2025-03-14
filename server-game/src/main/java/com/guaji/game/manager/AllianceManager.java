package com.guaji.game.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.obj.ObjBase;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;
import org.guaji.xid.GuaJiXID;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.GsApp;
import com.guaji.game.ServerData;
import com.guaji.game.battle.BattleUtil;
import com.guaji.game.bean.ApplyAllianceStates;
import com.guaji.game.bean.PlayerEmailData;
import com.guaji.game.config.AllianceCfg;
import com.guaji.game.config.AllianceDonateCfg;
import com.guaji.game.config.AllianceShopCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.EmailEntity;
import com.guaji.game.entity.GvgAllianceEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.gvg.GvgService;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.protocol.Alliance.AllianceMember;
import com.guaji.game.protocol.Alliance.HPAllianceBossHarmS;
import com.guaji.game.protocol.Alliance.HPAllianceDonateInfoItem;
import com.guaji.game.protocol.Alliance.HPAllianceDonateInfoResp;
import com.guaji.game.protocol.Alliance.HPAllianceEnterS;
import com.guaji.game.protocol.Alliance.HPAllianceInfoS;
import com.guaji.game.protocol.Alliance.HPAllianceMemberS;
import com.guaji.game.protocol.Alliance.HPApplyAllianceEmailRemoves;
import com.guaji.game.protocol.Alliance.HPBossVitalitySyncS;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.ApplyAddAllianceUpEmail;
import com.guaji.game.protocol.Const.ExitAllianceState;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Const.RankType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Mail;
import com.guaji.game.protocol.Mail.MailType;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.AdjustEventUtil;
import com.guaji.game.util.AllianceUtil;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsConst.AdjustActionType;
import com.guaji.game.util.GsonUtil;
import com.guaji.game.util.PlayerUtil;

/**
 * 公会管理器
 */

public class AllianceManager extends AppObj {

	/**
	 * 全局对象, 便于访问
	 */
	private static AllianceManager instance = null;

	/**
	 * 获取全局实例对象
	 */
	public static AllianceManager getInstance() {
		return instance;
	}

	/**
	 * 公会管理对象 公会ID,对象
	 */
	private ConcurrentHashMap<Integer, AllianceEntity> allianceMap;

	/**
	 * 公会管理战力管理 公会ID,战力
	 */
	private ConcurrentHashMap<Integer, Integer> allianceScoreMap;

	/**
	 * 已存在公会名字
	 */
	private Set<String> existName;

	// 公会转让检测
	private boolean alliancePermissionBool = false;

	/**
	 * 每秒执行
	 */
	private long millisecond = 0L;

	/**
	 * 是否当天(公会加入列表0点更新)
	 */
	private Date isTodayDate = new Date();

	/**
	 * 构造函数
	 */
	public AllianceManager(GuaJiXID xid) {
		super(xid);
		if (instance == null) {
			instance = this;
		}
		allianceMap = new ConcurrentHashMap<Integer, AllianceEntity>();
	}

	/**
	 * 数据加载
	 */
	public boolean init() {
		AllianceUtil.loadAllianceLevelCfg();
		List<AllianceEntity> allianceEntities = DBManager.getInstance().query("from AllianceEntity where invalid = 0");
		List<PlayerAllianceEntity> allianceMembers = DBManager.getInstance()
				.query("from PlayerAllianceEntity where allianceId > 0 and invalid = 0");

		if (allianceEntities.size() > 0) {
			for (AllianceEntity allianceEntity : allianceEntities) {
				allianceEntity.init();

				if (allianceEntity.getBossMaxTime() < 0) {
					AllianceCfg allianceCfg = AllianceUtil.getAllianceCfg(allianceEntity.getLevel(),
							allianceEntity.getExp());
					if (allianceCfg == null)
						throw new NullPointerException(
								" AllianceCfg not find level: " + allianceEntity.getLevel() + " data !!!");
					allianceEntity.setBossOpen(false);
					allianceEntity.setBossId(allianceCfg.getBossId());
					allianceEntity.setBossHp(allianceCfg.getBossHp());
					allianceEntity.getPlayerAddMap().clear();
					allianceEntity.setBossAttTime(0);
					allianceEntity.setBossMaxTime(0);
					allianceEntity.getBossJoinMap().clear();
					allianceEntity.notifyUpdate(true);

				}

				// 填充成员
				for (PlayerAllianceEntity playerAllianceEntity : allianceMembers) {
					if (playerAllianceEntity.getAllianceId() == allianceEntity.getId()) {
						allianceEntity.addMember(playerAllianceEntity.getPlayerId());
					}
				}

				// 服务器
				Integer allianceScore = 0;
				Set<Integer> memberList = allianceEntity.getMemberList();
				for (Integer memberId : memberList) {
					List<PlayerEntity> players = DBManager.getInstance()
							.query(String.format("from PlayerEntity where id=%d", memberId));

					if (players.size() > 0) {
						allianceScore = allianceScore + players.get(0).getFightValue();
					}
				}

				allianceEntity.setScoreValue(allianceScore);

				addAlliance(allianceEntity);

			}
		}

		// 公会数量达到目标条件
		/*
		 * if (allianceEntities != null && allianceEntities.size() >=
		 * GvgCfg.getInstance().getAllianceSize()) { if
		 * (!GvgService.getInstance().isOpeanFunction()) { GvgTimeEntity timeEntity =
		 * GvgTimeEntity.createEntity();
		 * GvgService.getInstance().setTimeEntity(timeEntity); } }
		 */

		List<String> existNameList = DBManager.getInstance().query("select name from AllianceEntity");

		if (existName == null) {
			existName = new CopyOnWriteArraySet<String>();
			existName.addAll(existNameList);
		} else {
			existName.addAll(existNameList);
		}
		return true;
	}

	/**
	 * 消息响应
	 * 
	 * @param msg
	 * @return
	 */
	@Override
	public boolean onMessage(Msg msg) {
		if (msg.getMsg() == GsConst.MsgType.SINGLE_ATTACK_ALLIANCE_BOSS) {
			AllianceEntity allianceEntity = msg.getParam(0);
			int targetId = msg.getParam(1);
			proBossSinglePlayer(allianceEntity, targetId);
		} else if (GsConst.MsgType.OFFLINE_ALLIANCE_INFO == msg.getMsg()) {
			int allianceId = msg.getParam(0);
			AllianceEntity allianceEntity = AllianceManager.getInstance().getAlliance(allianceId);
			if (allianceEntity != null) {
				HPAllianceMemberS.Builder response = AllianceManager.getInstance().getAllianceMember(allianceEntity);
				Set<Integer> allianceList = allianceEntity.getMemberList();
				for (Integer playerId : allianceList) {
					GuaJiXID targetXid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId);
					ObjBase<GuaJiXID, AppObj> objBase = GsApp.getInstance().queryObject(targetXid);
					if (objBase != null) {
						Player player = (Player) objBase.getImpl();
						player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_MEMBER_S, response));
					}
				}
			}
		}
		return true;
	}

	/**
	 * 线程主执行函数
	 */
	@Override
	public boolean onTick() {

		// 自动开启BOSS处理
		this.automaticOpenLogic();
		// 处理BOSS逻辑
		this.proAllBoss();
		// 处理公会重置
		this.processAllianceResetLogic();
		// 公会转让检测
		this.alliancePermissions();
		// 公会加入列表重置
		this.resetAllianceJoinList();

		// 循环帧
		long nowTime = GuaJiTime.getMillisecond();
		if (nowTime > millisecond) {
			// 刷新公会战力排行
			this.refreshAllianceScoreRank();
			millisecond = nowTime + 1800000L;// 间隔30分钟
		}

		return true;
	}

	/**
	 * 重置公会加入列表
	 */
	public void resetAllianceJoinList() {
		if (!GuaJiTime.isToday(isTodayDate)) {
			isTodayDate = new Date();
			for (AllianceEntity entity : this.allianceMap.values()) {
				entity.setBeforeDayAddVitality(entity.getCurDayAddVitality());
				entity.setCurDayAddVitality(0);
				entity.notifyUpdate();
			}
		}
	}

	/**
	 * 刷新公会战力排行榜
	 */
	public void refreshAllianceScoreRank() {
		// 处理公会循环
		for (AllianceEntity entity : this.allianceMap.values()) {

			// 获取成员列表
			Set<Integer> allMemlist = entity.getMemberList();
			Integer allianceScore = 0;
			// 填充成员
			for (Integer playerId : allMemlist) {

				Player targetPlayer = AllianceManager.getInstance().whetherOnline(playerId);
				if (targetPlayer != null) {
					allianceScore = allianceScore + PlayerUtil.calcAllFightValue(targetPlayer.getPlayerData());
				} else {
					PlayerSnapshotInfo.Builder playerSnapShot = SnapShotManager.getInstance()
							.getPlayerSnapShot(playerId);
					if (playerSnapShot == null)
						break;

					// 添加主角战力
					if (playerSnapShot.getMainRoleInfo() != null)
						allianceScore = allianceScore + playerSnapShot.getMainRoleInfo().getMarsterFight();
				}

			}
			entity.setScoreValue(allianceScore);
		}

	}

	/**
	 * 处理公会自动开启BOSS逻辑
	 */
	private void automaticOpenLogic() {

		Calendar calendar = Calendar.getInstance();
		int todayWeek = calendar.get(Calendar.DAY_OF_WEEK);
		// 处理公会循环
		for (AllianceEntity entity : this.allianceMap.values()) {

			AllianceCfg allianceCfg = AllianceUtil.getAllianceCfg(entity.getLevel(), entity.getExp());
			if (allianceCfg == null) {
				break;
			}
			TreeSet<Integer> dayOfWeeks = (TreeSet<Integer>) allianceCfg.getDayOfWeekSet();
			if (!dayOfWeeks.contains(todayWeek)) {
				break;
			}
			int dayOffset = 0;
			Integer nextDayOfWeek = dayOfWeeks.higher(todayWeek);
			if (nextDayOfWeek == null) {
				int first = dayOfWeeks.first();
				dayOffset = 7 - (todayWeek - first);
			} else {
				dayOffset = nextDayOfWeek - todayWeek;
			}
			// 时间判定
			List<Long> openList = entity.getAutomaticOpenList();
			if (null != openList && openList.size() > 0) {
				int index = 0;
				for (Long openTime : openList) {
					// 时间合法
					if (null != openTime && openTime > 0 && openTime < GuaJiTime.getMillisecond()) {
						// 更新下一次开启时间
						openTime = openTime + dayOffset * 24 * 60 * 60 * 1000;
						openList.set(index, openTime);
						// BOSS正在开启中
						if (entity.isBossOpen()) {
							//this.sendAllianceEmail(entity, GsConst.MailId.ALLIANCE_BOSS_OPEN_MAILID);
							entity.notifyUpdate(true);
							break;
						}
						// 公会Boss元气值不足
						if (entity.getBossVitality() < allianceCfg.getOpenBossVitality()) {
							//this.sendAllianceEmail(entity, GsConst.MailId.BOSS_VITALITY_LACK_MAILID);
							entity.notifyUpdate(true);
							break;
						}
						// 公会Boss开启次数不足
						if (entity.getEverydayBossOpenTimes() + 1 > entity.getLevel() / 5 + 2) {
							//this.sendAllianceEmail(entity, GsConst.MailId.BOSS_OPEN_TIMES_MAILID);
							entity.notifyUpdate(true);
							break;
						}
						// 条件都满足了---日了狗了---智能开启BOSS
						entity.deductBossVitality(allianceCfg.getOpenBossVitality());
						entity.setBossOpenSizeAdd();
						entity.setBossOpen(true);
						entity.setBossId(allianceCfg.getBossId());
						entity.setBossHp(allianceCfg.getBossHp());
						entity.setEverydayBossOpenTimes(entity.getEverydayBossOpenTimes() + 1);
						// 分钟为单位
						entity.setBossMaxTime(allianceCfg.getTime() * 60000);
						entity.setBossAttTime(0l);
						entity.notifyUpdate(true);
						// 自动将vip3的人加入战斗
						SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String format = sdFormat.format(GuaJiTime.getMillisecond());
						for (int memberId : entity.getMemberList()) {
							if (memberId > 0 && !entity.getBossJoinMap().containsKey(memberId)) {
								PlayerSnapshotInfo.Builder snapShotInfo = SnapShotManager.getInstance()
										.getPlayerSnapShot(memberId);
								if (snapShotInfo != null) {
//									MailManager.createSysMail(memberId, MailType.Normal_VALUE,
//											GsConst.MailId.AUTOMATIC_OPEN_BOSS, "自动解禁公会BOSS", null, format);
									if (snapShotInfo.getAllianceInfo().getAutoFight() > 0) {
										try {
											if (PlayerUtil.deductAutoAllianceBossGold(snapShotInfo,
													SysBasicCfg.getInstance().getAllianceAutoFightCostGold(),
													entity.getId())) {
												entity.getBossJoinMap().put(memberId, 0);
												// 加入boss战 先砍一刀
												Msg msg = Msg.valueOf(GsConst.MsgType.SINGLE_ATTACK_ALLIANCE_BOSS,
														GuaJiXID.valueOf(GsConst.ObjType.MANAGER,
																GsConst.ObjId.ALLIANCE));
												msg.pushParam(entity);
												msg.pushParam(memberId);
												GsApp.getInstance().postMsg(msg);
												Player target = PlayerUtil.queryPlayer(memberId);
												if (target != null && target.isOnline()) {
													target.sendProtocol(
															Protocol.valueOf(HP.code.ALLIANCE_CREATE_S_VALUE,
																	AllianceManager.getInstance().getAllianceInfo(
																			entity, target.getId(), target.getGold())));
												}
											}
										} catch (Exception e) {
											MyException.catchException(e);
										}
									}
								}
							}
						}
						break;
					}
					index++;
				}
			}
		}
	}

	/**
	 * 发送自动开启公会BOSS邮件
	 */
	private void sendAllianceEmail(AllianceEntity entity, int mailId) {

		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String format = sdFormat.format(GuaJiTime.getMillisecond());

		for (int memberId : entity.getMemberList()) {
			if (memberId > 0) {
				PlayerSnapshotInfo.Builder snapShotInfo = SnapShotManager.getInstance().getPlayerSnapShot(memberId);
				if (snapShotInfo != null) {
					// 解禁BOSS通知邮件
					MailManager.createSysMail(memberId, MailType.Normal_VALUE, mailId, "自动解禁公会BOSS", null, format);
				}
			}
		}
	}

	/**
	 * 处理公会重置逻辑
	 */
	private void processAllianceResetLogic() {
		// 处理公会重置;
		for (AllianceEntity entity : this.allianceMap.values()) {
			if (entity.getRefreshTime() < System.currentTimeMillis() || entity.getRefreshTime() == 0) {
				entity.setRefreshTime(GuaJiTime.getNextAM0Date());
				entity.setEverydayBossOpenTimes(0);
				entity.notifyUpdate(true);
			}
			// 是否首次重置
			long lastResetLuckySocreTime = entity.getLastResetLuckyScoreTime();
			if (lastResetLuckySocreTime <= 0) {
				resetAllianceEntityLuckyScore(entity);
				continue;
			}
			// 是否满足重置条件;
			long currentTime = GuaJiTime.getMillisecond();
			if (currentTime > lastResetLuckySocreTime
					&& (currentTime - lastResetLuckySocreTime) >= GsConst.Alliance.RESET_LUCKY_SCORE_INTERVAL) {
				resetAllianceEntityLuckyScore(entity);
			}
		}
	}

	/**
	 * 处理会长自动转让逻辑
	 */
	private void alliancePermissions() {
		// 重置状态
		if (GuaJiTime.getCalendar().getTime().after(GuaJiTime.getAM0Date(new Date()))
				&& GuaJiTime.getCalendar().getTime().before(GuaJiTime.getAM0001Date(new Date()))) {
			alliancePermissionBool = true;
		}
		/************************
		 * 处理会长自动转让逻辑. 每日零点零1分---零点零2分处理(只处理一次)
		 ****************************/
		if (GuaJiTime.getCalendar().getTime().after(GuaJiTime.getAM0001Date(new Date()))
				&& GuaJiTime.getCalendar().getTime().before(GuaJiTime.getAM0002Date(new Date()))
				&& alliancePermissionBool) {
			alliancePermissionBool = false;
			RunnableTest r1 = new RunnableTest(allianceMap);
			Thread t = new Thread(r1);
			t.start();
		}
	}

	/**
	 * 异步执行
	 * 
	 * @author xuy
	 */
	class RunnableTest implements Runnable {
		ConcurrentHashMap<Integer, AllianceEntity> allianceMap = null;

		public RunnableTest(ConcurrentHashMap<Integer, AllianceEntity> allianceMap) {
			this.allianceMap = allianceMap;
		}

		@Override
		public void run() {

			Log.logPrintln("==================定时会长自动转让开始=================");
			for (AllianceEntity entity : this.allianceMap.values()) {
				try {
					// 公会全部成员
					Set<Integer> playerIdSet = entity.getMemberList();
					if (playerIdSet.size() > 1 && entity.getLevel() >= 5) {// 如果公会里面除了会长以外还有其他成员,公会等级大于5级
						// 获取公会的会长信息
						List<PlayerAllianceEntity> playerEntitys = DBManager.getInstance().query(
								"from PlayerAllianceEntity where invalid = 0 and playerId = ?", entity.getPlayerId());
						if (playerEntitys != null && playerEntitys.size() > 0) {
							PlayerAllianceEntity playerAllianceEntity = playerEntitys.get(0);
							Date curDate = GuaJiTime.getAM0Date();
							// 因为在规定的时间内没有进行过元气贡献，so进行会长转让
							if (GuaJiTime.calcBetweenDays(curDate,
									playerAllianceEntity.getAddVitalityTime()) >= SysBasicCfg.getInstance()
											.getVitalityNoChangeDayTime()) {
								// 副会长数据集(按照元气值从大到小排序)
								List<PlayerAllianceEntity> viceChairmanList = new ArrayList<>();
								// 公会成员数据集(按照元气值从大到小排序)
								List<PlayerAllianceEntity> commonList = new ArrayList<>();
								// 加入副会长集合
								List<PlayerAllianceEntity> playerVice = DBManager.getInstance().query(
										"from PlayerAllianceEntity where invalid = 0 and allianceId = ? and postion = ?",
										entity.getId(), GsConst.Alliance.ALLIANCE_POS_COPYMAIN);
								if (playerVice != null && playerVice.size() > 0) {
									viceChairmanList.addAll(playerVice);
								}
								// 加入公会成员集合
								List<PlayerAllianceEntity> playerCommon = DBManager.getInstance().query(
										"from PlayerAllianceEntity where invalid = 0 and allianceId = ? and postion = ?",
										entity.getId(), GsConst.Alliance.ALLIANCE_POS_COMMON);
								if (playerCommon != null && playerCommon.size() > 0) {
									commonList.addAll(playerCommon);
								}
								// 新的转让对象
								PlayerAllianceEntity newPlayAlliance = new PlayerAllianceEntity();
								// 如果副会长不为空.则直接进行转换
								if (viceChairmanList != null && viceChairmanList.size() > 0) {
									Collections.sort(viceChairmanList, AllianceUtil.PLAYERALLIANCESORT);
									// 公会转让逻辑
									newPlayAlliance = viceChairmanList.get(0);
								} else if (commonList != null && commonList.size() > 0) { // 成员进行转让
									Collections.sort(commonList, AllianceUtil.PLAYERALLIANCESORT);
									// 公会转让逻辑
									newPlayAlliance = commonList.get(0);
								}
								// 开始会长转让
								changeMain(entity, playerAllianceEntity, newPlayAlliance.getPlayerId());
								// 发送邮件通知公会成员,老会长，新会长
								sendNoticeMail(entity, playerAllianceEntity, newPlayAlliance);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			Log.logPrintln("==================定时会长自动转让结束=================");
		}
	}

	/**
	 * 重置公会幸运值;
	 * 
	 * @param eachAlliance
	 */
	private void resetAllianceEntityLuckyScore(AllianceEntity eachAlliance) {
		eachAlliance.setLastResetLuckyScoreTime(GuaJiTime.getFirstDayCalendarOfCurWeek().getTimeInMillis());
		eachAlliance.setLuckyScore(0);
		eachAlliance.notifyUpdate(true);
	}

	/**
	 * 获取所有公会
	 * 
	 * @return
	 */
	public ConcurrentHashMap<Integer, AllianceEntity> getAllianceMap() {
		return allianceMap;
	}

	/**
	 * 获取所有公会战力
	 * 
	 * @return
	 */
	public ConcurrentHashMap<Integer, Integer> getAllianceScoreMap() {
		return allianceScoreMap;
	}

	/**
	 * 增加公会
	 * 
	 * @param allianceEntity
	 */
	public void addAlliance(AllianceEntity allianceEntity) {
		allianceMap.put(allianceEntity.getId(), allianceEntity);
	}

	/**
	 * 通过公会Id获取公会数据
	 * 
	 * @param allianceId
	 * @return
	 */
	public AllianceEntity getAlliance(int allianceId) {
		return allianceMap.get(allianceId);
	}

	/**
	 * 通过公会Id获取公会战力
	 * 
	 * @param allianceId 公会编号
	 * @return
	 */
	public int getAllianceScore(int allianceId) {

		return allianceScoreMap.get(allianceId);
	}

	/**
	 * 获取所有已经存在的公会名
	 * 
	 * @return
	 */
	public Set<String> getExistName() {
		return existName;
	}

	/**
	 * 构造公会信息回复协议
	 * 
	 * @param allianceEntity
	 * @param playerId
	 * @param remGold
	 * @return
	 */
	public HPAllianceInfoS.Builder getAllianceInfo(AllianceEntity allianceEntity, int playerId, int remGold) {
		// 修复经验数据，判断时候升级
		AllianceManager.getInstance().checkAllianceLevelUp(allianceEntity);
		HPAllianceInfoS.Builder ret = HPAllianceInfoS.newBuilder();
		ret.setId(allianceEntity.getId());
		ret.setLevel(allianceEntity.getLevel());
		ret.setCurrentExp(allianceEntity.getExp());
		ret.setNextExp(AllianceUtil.getAllianceMaxExp(ret.getLevel()));
		ret.setCurrentPop(allianceEntity.getMemberList().size());
		ret.setMaxPop(AllianceUtil.getAllianceMaxPop(ret.getLevel()));
		int remSize = allianceEntity.getBossOpenSize() - GsConst.Alliance.BOSS_FREE_TIMES;
		int bossGold = 0;
		if (remSize < 0)
			remSize = Math.abs(remSize);
		else {
			bossGold = AllianceUtil.openBossGold(remSize + 1);
			remSize = 0;
		}
		ret.setBossFunRemSize(remSize);
		if (allianceEntity.isBossOpen()) {
			if (allianceEntity.getBossJoinMap().containsKey(playerId))
				ret.setBossState(3);
			else
				ret.setBossState(2);
			ret.setBossHp(allianceEntity.getBossHp());
			ret.setBossTime(allianceEntity.getBossMaxTime() / 1000);
			ret.setBossPropAdd(allianceEntity.getPlayerAddMap(playerId) * 20);
		} else
			ret.setBossState(1);
		ret.setBossId(allianceEntity.getBossId());
		if (allianceEntity.getNotice() == null) {
			ret.setAnnoucement("");
		} else {
			ret.setAnnoucement(allianceEntity.getNotice());
		}
		ret.setBattleLimit(allianceEntity.getJoinLimit());
		ret.setBossGold(bossGold);
		ret.setRemGold(remGold);
		ret.setName(allianceEntity.getName());
		// 魔兽元气开BOSS功能
		ret.setCurBossVitality(allianceEntity.getBossVitality());
		AllianceCfg allianceCfg = AllianceUtil.getAllianceCfg(allianceEntity.getLevel(), allianceEntity.getExp());
		if (allianceCfg == null) {
			throw new NullPointerException(" AllianceCfg not find level: " + allianceEntity.getLevel() + " data !!!");
		}
		ret.setOpenBossVitality(allianceCfg.getOpenBossVitality());
		ret.setRemainderBossTimes(allianceEntity.getEverydayBossOpenTimes());
		ret.setAllBossTimes((allianceEntity.getLevel() / 5 + 2));
		ret.setHasCheckButton(allianceEntity.getHasCheckLeaderMail()); // 返回给客户端是否勾选公会会长发邮件同意的按钮
		// 公会BOSS自动解禁时间
		SimpleDateFormat sdFormat = new SimpleDateFormat("HH:mm");
		for (Long openTime : allianceEntity.getAutomaticOpenList()) {
			if (null != openTime) {
				String value = sdFormat.format(openTime);
				ret.addOpenTimeList(value);
			} else {
				ret.addOpenTimeList("");
			}
		}
		ret.setCanChangeName(allianceEntity.canChangeName());
		return ret;
	}

	/**
	 * 构造公会成员回复协议
	 * 
	 * @return
	 */
	public HPAllianceMemberS.Builder getAllianceMember(AllianceEntity allianceEntity) {
		HPAllianceMemberS.Builder response = HPAllianceMemberS.newBuilder();
		int now = (int) (GuaJiTime.getMillisecond() / 1000);
		if (allianceEntity != null) {
			for (int targetId : allianceEntity.getMemberList()) {
				PlayerSnapshotInfo.Builder builder = SnapShotManager.getInstance().getPlayerSnapShot(targetId);
				if (builder != null) {
					AllianceMember.Builder member = AllianceMember.newBuilder();
					member.setId(builder.getPlayerId());
					member.setLevel(builder.getMainRoleInfo().getLevel());
					member.setName(builder.getMainRoleInfo().getName());
					member.setBattlePoint(builder.getMainRoleInfo().getMarsterFight());
					member.setContribution(builder.getAllianceInfo().getContribution());
					if (allianceEntity.getPlayerId() == targetId) { // 判断会长
						member.setPostion(GsConst.Alliance.ALLIANCE_POS_MAIN);
					} else {
						if (builder.getAllianceInfo().getPostion() == GsConst.Alliance.ALLIANCE_POS_MAIN) {
							member.setPostion(GsConst.Alliance.ALLIANCE_POS_COMMON);
						} else {
							member.setPostion(builder.getAllianceInfo().getPostion());
						}
					}
					member.setHeadIcon(builder.getPlayerInfo().getHeadIcon());
					member.setHasReported(false);
					member.setProfession(builder.getMainRoleInfo().getProf());
					member.setRoleItemId(builder.getMainRoleInfo().getItemId());
					Player player = PlayerUtil.queryPlayer(builder.getPlayerId());
					if (player != null && player.isOnline()) {
						member.setLeftLogoutTime(0);
					} else {
						member.setLeftLogoutTime(now - builder.getLastLogoutTime());
					}
					member.setTotalVitality(builder.getAllianceInfo().getTotalVitality());
					member.setRebirthStage(builder.getMainRoleInfo().getRebirthStage());
					member.setAvatarId(builder.getMainRoleInfo().getAvatarId());
					response.addMemberList(member);
				}
			}
		}
		return response;
	}

	/**
	 * 获取刷新贡献消耗;
	 * 
	 * @param refreshShopCount
	 * @return
	 */
	public int getRefreshCostByCount(int refreshShopCount) {
		return SysBasicCfg.getInstance().getRefreshAllianceShopPrice(refreshShopCount);
	}

	/**
	 * 获取在模板中的幸运值位置;
	 * 
	 * @param luckyScore
	 * @return
	 */
	public int getConfigScore(int luckyScore) {
		// 获取所有模板;
		Collection<AllianceShopCfg> configs = ConfigManager.getInstance().getConfigMap(AllianceShopCfg.class).values();
		List<Integer> scoreList = new ArrayList<Integer>();
		for (AllianceShopCfg eachConfig : configs) {
			scoreList.add(eachConfig.getLuckyScore());
		}
		// 升序排序
		Collections.sort(scoreList);
		if (scoreList.size() <= 0) {
			return 0;
		}
		if (luckyScore <= scoreList.get(0)) {
			return scoreList.get(0);
		}
		if (luckyScore >= scoreList.get(scoreList.size() - 1)) {
			return scoreList.get(scoreList.size() - 1);
		}
		for (int i = 0; i < scoreList.size(); i++) {
			if (scoreList.get(i) == luckyScore) {
				return luckyScore;
			}
			if (scoreList.get(i) > luckyScore) {
				return scoreList.get(i - 1);
			}
		}
		return 0;
	}

	/**
	 * 回复自身公会信息
	 * 
	 * @param ret
	 * @param player
	 * @param allianceEntity
	 */
	public void sendSelfData(HPAllianceEnterS.Builder ret, Player player, AllianceEntity allianceEntity) {
		ret.setHasAlliance(player.getPlayerData().getPlayerAllianceEntity().getAllianceId() != 0);
		AllianceMember.Builder bean = AllianceMember.newBuilder();
		bean.setBattlePoint(player.getLevel());
		bean.setName(player.getName());
		bean.setBattlePoint(1);
		bean.setLevel(allianceEntity.getLevel());
		bean.setContribution(player.getPlayerData().getPlayerAllianceEntity().getContribution());
		bean.setPostion(player.getPlayerData().getPlayerAllianceEntity().getPostion());
		bean.setId(player.getPlayerData().getPlayerAllianceEntity().getPlayerId());
		bean.setHasReported(
				player.getPlayerData().getPlayerAllianceEntity().getReportTime() > System.currentTimeMillis());
		bean.setProfession(player.getPlayerData().getMainRole().getProfession());
		bean.setAutoFight(player.getPlayerData().getPlayerAllianceEntity().getAutoFight());
		bean.setAvatarId(player.getPlayerData().getUsedAvatarId());
		ret.setMyInfo(bean);

		// 判断公会是否在争霸中
		if (AllianceBattleManager.getInstance().isAllianceInBattle(allianceEntity.getId())) {
			ret.setIsInBattle(true);
		} else {
			ret.setIsInBattle(false);
		}

		player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_ENTER_S_VALUE, ret));
	}

	boolean runTag = false;

	/**
	 * 处理BOSS逻辑
	 */
	public void proAllBoss() {
		if (runTag) {
			return;
		}

		runTag = true;
		try {
			Collection<AllianceEntity> list = allianceMap.values();
			for (AllianceEntity allianceEntity : list) {
				if (!allianceEntity.isBossOpen())
					continue;
				if (allianceEntity.getBossMaxTime() <= 0) {
					clearBossAllianceEntity(allianceEntity);
					continue;
				}
				allianceEntity.proAttTime();
				if (allianceEntity.getBossAttTime() > 0) {
					continue;
				}
				proBoss(allianceEntity);
			}
		} catch (Exception e) {
			runTag = false;
			MyException.catchException(e);
		}
		runTag = false;
	}

	/**
	 * 处理boss伤害逻辑
	 * 
	 * @param allianceEntity
	 */
	private void proBoss(AllianceEntity allianceEntity) {
		int size = allianceEntity.getBossJoinMap().size();
		if (size == 0)
			return;

		int harm = 0;
		int oldHp = allianceEntity.getBossHp();
		synchronized (allianceEntity.getBossJoinMap()) {
			try {
				for (Integer targetId : allianceEntity.getBossJoinMap().keySet()) {
					Integer v = allianceEntity.getBossJoinMap().get(targetId);
					int tmpHarm = BattleUtil.calcAllianceBossDamage(targetId);
					if (tmpHarm < 0)
						tmpHarm = 0;
					harm = tmpHarm;
					Integer p = allianceEntity.getPlayerAddMap(targetId);
					if (p != 0) {
						harm = ((p * 20) * tmpHarm) / 100 + tmpHarm;
					} else {
						harm = tmpHarm;
					}

					// 打出伤害超出bosshp 进行修正值
					if (allianceEntity.getBossHp() < harm) {
						harm = allianceEntity.getBossHp();
					}

					allianceEntity.setBossHp(allianceEntity.getBossHp() - harm);

					if (v == null)
						allianceEntity.getBossJoinMap().put(targetId, harm);
					else
						allianceEntity.getBossJoinMap().put(targetId, v + harm);
				}
			} catch (Exception e) {
				Log.errPrintln("allianceEntity.getBossJoinMap()" + e);
			}
		}
		allianceEntity.setBossAttTime(GsConst.Alliance.BOSS_ATT_TIME);

		int newHp = allianceEntity.getBossHp();
		int harmHp = oldHp - newHp;
		if (harmHp != 0) {
			HPAllianceBossHarmS.Builder ret = HPAllianceBossHarmS.newBuilder();
			ret.setValue(harmHp);
			// 推送伤害
			try {
				Map<Integer, Integer> allianceJoinMap = new HashMap<>(allianceEntity.getBossJoinMap());
				for (Integer targetId : allianceJoinMap.keySet()) {
					sendBossHarm(targetId, ret);
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}

		if (allianceEntity.getBossHp() <= 0 && allianceEntity.getBossMaxTime() > 0) {
			sendBossBonus(allianceEntity.getBossJoinMap(),
					AllianceUtil.getAllianceCfg(allianceEntity.getLevel(), allianceEntity.getExp()), allianceEntity);
			clearBossAllianceEntity(allianceEntity);
		} else if (allianceEntity.getBossMaxTime() <= 0) {
			if(!allianceEntity.getBossJoinMap().isEmpty()) {
				SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String format = sdFormat.format(GuaJiTime.getMillisecond());
				for (Integer playerId : allianceEntity.getBossJoinMap().keySet()) {
					MailManager.createSysMail(playerId, MailType.Normal_VALUE,
							GsConst.MailId.ALLIANCE_BOSS_FAil, "公会Boss挑战失败", null, format);
				}
			}
			
			
			clearBossAllianceEntity(allianceEntity);
		}

		allianceEntity.notifyUpdate(true);
	}

	/**
	 * 处理boss伤害逻辑
	 * 
	 * @param allianceEntity
	 */
	private void proBossSinglePlayer(AllianceEntity allianceEntity, int targetId) {
		int harm = 0;
		int oldHp = allianceEntity.getBossHp();
		Integer v = allianceEntity.getBossJoinMap().get(targetId);
		int tmpHarm = BattleUtil.calcAllianceBossDamage(targetId);
		if (tmpHarm < 0)
			tmpHarm = 0;
		harm = tmpHarm;
		Integer p = allianceEntity.getPlayerAddMap(targetId);
		if (p != 0) {
			harm = ((p * 20) * tmpHarm) / 100 + tmpHarm;
		} else {
			harm = tmpHarm;
		}

		// 打出伤害超出bosshp 进行修正值
		if (allianceEntity.getBossHp() < harm) {
			harm = allianceEntity.getBossHp();
		}

		allianceEntity.setBossHp(allianceEntity.getBossHp() - harm);

		if (v == null)
			allianceEntity.getBossJoinMap().put(targetId, harm);
		else
			allianceEntity.getBossJoinMap().put(targetId, v + harm);

		int newHp = allianceEntity.getBossHp();
		int harmHp = oldHp - newHp;
		if (harmHp != 0) {
			HPAllianceBossHarmS.Builder ret = HPAllianceBossHarmS.newBuilder();
			ret.setValue(harmHp);
			// 推送伤害
			try {
				Map<Integer, Integer> allianceBossJoinMap = new HashMap<Integer, Integer>(
						allianceEntity.getBossJoinMap());
				for (Integer tId : allianceBossJoinMap.keySet()) {
					sendBossHarm(tId, ret);
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}

		if (allianceEntity.getBossHp() <= 0 && allianceEntity.getBossMaxTime() > 0) {
			sendBossBonus(allianceEntity.getBossJoinMap(),
					AllianceUtil.getAllianceCfg(allianceEntity.getLevel(), allianceEntity.getExp()), allianceEntity);
			clearBossAllianceEntity(allianceEntity);
		} else if (allianceEntity.getBossMaxTime() <= 0) {
			clearBossAllianceEntity(allianceEntity);
			// 公会Boss站失败日志,时间到了，Boss没死
			BehaviorLogger.log4Service(allianceEntity.getId(), Source.SYS_OPERATION, Action.ALLIANCE_BOSS_FAIL,
					Params.valueOf("allianceId", allianceEntity.getId()));
		} else
			allianceEntity.notifyUpdate(true);
	}

	/**
	 * 清理公会boss数据
	 * 
	 * @param allianceEntity
	 */
	public void clearBossAllianceEntity(AllianceEntity allianceEntity) {
		AllianceCfg allianceCfg = AllianceUtil.getAllianceCfg(allianceEntity.getLevel(), allianceEntity.getExp());
		if (allianceCfg == null)
			throw new NullPointerException(" AllianceCfg not find level: " + allianceEntity.getLevel() + " data !!!");

		allianceEntity.setBossOpen(false);
		allianceEntity.setBossId(allianceCfg.getBossId());
		allianceEntity.setBossHp(allianceCfg.getBossHp());
		allianceEntity.clearPlayerAddInfo();
		allianceEntity.setBossAttTime(0);
		allianceEntity.setBossMaxTime(0);
		allianceEntity.clearBossJoinInfo();
		allianceEntity.notifyUpdate(true);
	}

	/**
	 * 发送Boss伤害
	 * 
	 * @param playerId
	 * @param ret
	 */
	private void sendBossHarm(int playerId, HPAllianceBossHarmS.Builder ret) {
		GuaJiXID targetXid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId);
		if (targetXid != null) {
			ObjBase<GuaJiXID, AppObj> objBase = GsApp.getInstance().lockObject(targetXid);
			try {
				if (objBase != null && objBase.isObjValid()) {
					Player targetPlayer = (Player) objBase.getImpl();
					targetPlayer.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_BOSSHARM_S_VALUE, ret));
				}
			} finally {
				if (objBase != null) {
					objBase.unlockObj();
				}
			}
		}
	}

	/**
	 * 发送BOSS奖励
	 * 
	 * @param harmMap
	 * @param cfg
	 */
	private void sendBossBonus(Map<Integer, Integer> harmMap, AllianceCfg cfg, AllianceEntity allianceEntity) {
		int beforeLevel = allianceEntity.getLevel(); // 记日志用的
		int contribution = cfg.getContribution();
		int newExp = allianceEntity.getExp() + contribution;
		allianceEntity.setExp(newExp);
		checkAllianceLevelUp(allianceEntity);
		allianceEntity.notifyUpdate(true);
		// add by weiyong
		BehaviorLogger.log4Service(allianceEntity.getPlayerId(), Source.SYS_OPERATION, Action.ALLIANCE_EXP_ADD,
				Params.valueOf("allianceId", allianceEntity.getId()), Params.valueOf("add", contribution),
				Params.valueOf("after", newExp), Params.valueOf("beforeLevel", beforeLevel),
				Params.valueOf("afterLevel", allianceEntity.getLevel()));
		// boss击杀玩家伤害日志
		BehaviorLogger.log4Service(allianceEntity.getId(), Source.SYS_OPERATION, Action.ALLIANCE_BOSS_VICTORY,
				Params.valueOf("allianceId", allianceEntity.getId()),
				Params.valueOf("playerharm", GsonUtil.getJsonInstance().toJson(harmMap)));

		for (Integer playerId : harmMap.keySet()) {
			Integer value = harmMap.get(playerId);
			int percent = AllianceUtil.calcBossHPPercent(cfg.getBossHp(), value);
			int contValue = percent * contribution / 100;
			// 上限设置贡献值
			PlayerAllianceEntity entity = null;
			Player player = PlayerUtil.queryPlayer(playerId);
			if (null != player) {
				entity = player.getPlayerData().getPlayerAllianceEntity();
			} else {
				List<PlayerAllianceEntity> playerEntitys = DBManager.getInstance()
						.query("from PlayerAllianceEntity where playerId = ? and invalid = 0", playerId);
				if (playerEntitys != null && playerEntitys.size() > 0) {
					entity = playerEntitys.get(0);
				}
			}
			if (null != entity) {
				if (entity.getRefreshTime() < System.currentTimeMillis()) {
					entity.setRefreshTime(GuaJiTime.getNextAM0Date());
					entity.setEverydayContribution(0);
				}
				if (entity.getEverydayContribution() + contValue > SysBasicCfg.getInstance()
						.getEverydayContribution()) {
					contValue = SysBasicCfg.getInstance().getEverydayContribution() - entity.getEverydayContribution();
				}
				if (contValue > 0) {
					entity.setEverydayContribution(entity.getEverydayContribution() + contValue);
				}
				entity.notifyUpdate(true);
			}
			// 设置贡献值结束
			if (contValue > 0) {
				AwardItems awardItems = new AwardItems();
				awardItems.addContribution(contValue);
				MailManager.createMail(playerId, Mail.MailType.Reward_VALUE, GsConst.MailId.ALLIANCE_BOSS, "",
						awardItems, contValue + "");
			}
			// 推送参加公会BOSS任务, 分为玩家在线或者不在线的情况
			if (PlayerUtil.queryPlayer(playerId) != null) {
				QuestEventBus.fireQuestEventOneTime(QuestEventType.GONG_HUI_BOSS,
						GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId));
			} else {
				// fire offline(player data not in cache) event
				QuestEventBus.fireQuestEventWhenPlayerOffline(playerId, QuestEventType.GONG_HUI_BOSS, 1);
			}
		}
	}

	/**
	 * 检查是否升级
	 * 
	 * @param allianceEntity
	 */
	public boolean checkAllianceLevelUp(AllianceEntity allianceEntity) {
		Integer maxExp = 0;
		boolean update = false;
		int maxLevel = AllianceUtil.getAllianceMaxLevel();

		for (int i = allianceEntity.getLevel(); i <= maxLevel; i++) {
			maxExp = AllianceUtil.getAllianceMaxExp(i);
			if (maxExp > allianceEntity.getExp()) {
				break;
			}
			allianceEntity.setExp(allianceEntity.getExp() - maxExp);
			allianceEntity.setLevel(i + 1 > maxLevel ? maxLevel : i + 1);
			update = true;
		}
		if (allianceEntity.getLevel() == maxLevel) {
			if (allianceEntity.getExp() > AllianceUtil.getAllianceMaxExp(maxLevel)) {
				allianceEntity.setExp(AllianceUtil.getAllianceMaxExp(maxLevel));
			}
		}
		if (update) {
			allianceEntity.notifyUpdate(true);
			GsApp.getInstance().postMsg(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.ALLIANCE_BATTLE),
					Msg.valueOf(GsConst.MsgType.ALLIANCE_LELEL_UP).pushParam(allianceEntity.getId(),
							allianceEntity.getLevel()));
			return true;
		}
		return false;
	}

	/**
	 * 离线修改公会数据
	 * 
	 * @param playerId
	 * @param postion
	 * @param allianceId
	 */
	public void updateOfflineAllianceData(int playerId, int postion, int allianceId) {
		List<PlayerAllianceEntity> playerEntitys = DBManager.getInstance()
				.query("from PlayerAllianceEntity where playerId = ? and invalid = 0", playerId);
		if (playerEntitys != null && playerEntitys.size() > 0) {
			PlayerAllianceEntity playerAllianceEntity = playerEntitys.get(0);
			playerAllianceEntity.init();
			playerAllianceEntity.setPostion(postion);
			if (postion == GsConst.Alliance.ALLIANCE_POS_MAIN) {
				playerAllianceEntity.setAddVitalityTime(GuaJiTime.getCalendar().getTime());
			}
			playerAllianceEntity.setAllianceId(allianceId);
			playerAllianceEntity.notifyUpdate(false);
			Log.logPrintln(String.format("AllianceChangePosHandler update playerId %d postion %d", playerId,
					playerAllianceEntity.getPostion()));
			// 更新同步数据
			SnapShotManager.getInstance().getCurrentStrategy().onAllianceDataChanged(playerAllianceEntity);
			AllianceManager.getInstance().offlineRefresh(playerAllianceEntity);
		}
	}

	/**
	 * 玩家是否在线
	 * 
	 * @param playerId
	 * @return
	 */
	public Player whetherOnline(int playerId) {
		Player targetPlayer = null;
		GuaJiXID targetXid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId); // 申请的玩家是否离线
		ObjBase<GuaJiXID, AppObj> objBase = GsApp.getInstance().lockObject(targetXid);
		try {
			if (objBase != null && objBase.isObjValid()) {
				targetPlayer = (Player) objBase.getImpl();
			}
		} finally {
			if (objBase != null) {
				objBase.unlockObj();
			}
		}
		return targetPlayer;
	}

	/**
	 * 增加公会Boss魔兽元气
	 */
	public void addAllianceBossVitality(int playerId, int addVitality, Action action) {
		PlayerSnapshotInfo.Builder snapshot = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
		if (snapshot == null || snapshot.getAllianceInfo() == null) {
			return;
		}
		int allianceId = snapshot.getAllianceInfo().getAllianceId();
		if (allianceId != 0) {
			GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId);
			ObjBase<GuaJiXID, AppObj> objBase = GsApp.getInstance().lockObject(xid);
			Player player = null;
			try {
				if (objBase != null && objBase.isObjValid()) {
					player = (Player) objBase.getImpl();
					PlayerData playerData = player.getPlayerData();
					PlayerAllianceEntity playerAllianceEntity = playerData.getPlayerAllianceEntity();
					if (playerAllianceEntity != null) {
						playerAllianceEntity.addVitality(addVitality);
						playerAllianceEntity.setAddVitalityTime(GuaJiTime.getCalendar().getTime());// 更新贡献元气时间
						playerAllianceEntity.notifyUpdate(true);
						BehaviorLogger.log4Service(player, Source.SYS_OPERATION, Action.ADD_VITALITY,
								Params.valueOf("addVitality", addVitality),
								Params.valueOf("curVitality", playerAllianceEntity.getVitality()),
								Params.valueOf("addVitalityTime", playerAllianceEntity.getAddVitalityTime()));

						// 在线玩家快照刷新
						player.getPlayerData().refreshOnlinePlayerSnapshot();
					}
				}
			} finally {
				if (objBase != null) {
					objBase.unlockObj();
				}
			}

			if (player == null) {
				PlayerAllianceEntity playerAllianceEntity = DBManager.getInstance().fetch(PlayerAllianceEntity.class,
						"from PlayerAllianceEntity where playerId = ?", playerId);
				if (playerAllianceEntity != null) {
					playerAllianceEntity.addVitality(addVitality);
					playerAllianceEntity.setAddVitalityTime(GuaJiTime.getCalendar().getTime());//// 更新贡献元气时间
					playerAllianceEntity.notifyUpdate(false);
				}
			}

			AllianceEntity allianceEntity = getAlliance(allianceId);
			if (allianceEntity != null) {
				allianceEntity.addBossVitality(addVitality, action);
			}
			// GVG功能开启、添加元气增加记录缓存
			if (!GvgService.getInstance().isOpeanFunction()) {
				return;
			}
			GvgAllianceEntity entity = GvgAllianceEntity.valueOf(allianceId, addVitality);
			// 之前他统计当天声望去掉
			// GvgService.getInstance().updateAlliances(entity);
		}
	}

	/**
	 * 向玩家所在的公会广播boss元气
	 * 
	 * @param playerId
	 */
	public void broadcastBossVitality(int playerId) {
		PlayerSnapshotInfo.Builder snapshot = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
		int allianceId = snapshot.getAllianceInfo().getAllianceId();
		if (allianceId != 0) {
			HPBossVitalitySyncS.Builder ret = HPBossVitalitySyncS.newBuilder();
			AllianceEntity allianceEntity = getAlliance(allianceId);
			ret.setCurBossVitality(allianceEntity.getBossVitality());
			AllianceCfg allianceCfg = AllianceUtil.getAllianceCfg(allianceEntity.getLevel(), allianceEntity.getExp());
			if (allianceCfg == null) {
				throw new NullPointerException(
						" AllianceCfg not find level: " + allianceEntity.getLevel() + " data !!!");
			}
			ret.setOpenBossVitality(allianceCfg.getOpenBossVitality());

			for (int memberId : allianceEntity.getMemberList()) {
				if (ServerData.getInstance().isPlayerOnline(memberId)) {
				}
			}
		}
	}

	/**
	 * 验证加入公会的逻辑
	 * 
	 * @param targetPlayer           目标玩家
	 * @param targetAllianceEntity   公会
	 * @param player                 会长
	 * @param potoId
	 * @param myPlayerAllianceEntity 目标玩家公会
	 * @return
	 */
	public int checkAddAlliance(Player targetPlayer, AllianceEntity targetAllianceEntity, Player player, int potoId,
			PlayerAllianceEntity myPlayerAllianceEntity) {
		if (targetAllianceEntity == null) {
			// 公会不存在
			player.sendError(potoId, Status.error.ALLIANCE_NONEXISTENT);
			return -1;
		}

		if (targetPlayer.getFightValue() < targetAllianceEntity.getJoinLimit()) {
			// 战斗力不足
			player.sendError(potoId, Status.error.FIGHT_VALUE_NOT_ENOUGH);
			return -1;
		}

		if (targetAllianceEntity.getMemberList().size() >= AllianceUtil
				.getAllianceMaxPop(targetAllianceEntity.getLevel())) {
			// 公会已满
			player.sendError(potoId, Status.error.ALLIANCE_FULL_ERROR);
			return -1;
		}

		if (myPlayerAllianceEntity.getExitTime() > System.currentTimeMillis()) {
			// 退出后再次加入时间未到
			player.sendError(potoId, Status.error.ALLIANCE_JOIN_ERROR_VALUE);
			return -1;
		}
		return 1;
	}

	/**
	 * 加入公会
	 * 
	 * @param player
	 * @param myPlayerAllianceEntity
	 * @param targetAllianceEntity
	 * @param potoId
	 */
	public void joinAlliance(Player player, PlayerAllianceEntity myPlayerAllianceEntity,
			AllianceEntity targetAllianceEntity, int potoId, boolean isOnline, int type) {

		myPlayerAllianceEntity.setExitTime(0);
		myPlayerAllianceEntity.setAllianceId(targetAllianceEntity.getId());
		myPlayerAllianceEntity.setJoinTime(System.currentTimeMillis());
		targetAllianceEntity.getMemberList().add(player.getId());
		// 公会战信息中也要更新
		// 推送加入公会任务
		QuestEventBus.fireQuestEventOneTime(QuestEventType.JOIN_ALLIANCE, player.getXid());
		// adjust 加入公会
		AdjustEventUtil.sentAdjustEventInfo(player, AdjustActionType.GUILDJOIN, 0);
		if (isOnline) {
			player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_CREATE_S_VALUE, AllianceManager.getInstance()
					.getAllianceInfo(targetAllianceEntity, player.getId(), player.getGold())));
			AllianceManager.getInstance().sendSelfData(HPAllianceEnterS.newBuilder(), player, targetAllianceEntity);
			// 发送聊天标记
			GameUtil.sendAllianceChatTag(player);
		}

		if (myPlayerAllianceEntity.getAllianceId() != 0) {
			ChatManager.getInstance().addAllianceSession(player.getSession(),
					player.getPlayerData().getPlayerAllianceEntity().getAllianceId(), player.getId());
			String msg = "";
			if (type == Const.AddAllianceState.INITIATIVE_ADD_TYPE_1_VALUE) {
				msg = SysBasicCfg.getInstance().getAutoJoinAllianceChat();
			} else if (type == Const.AddAllianceState.MAIN_AGREE_TYPE_2_VALUE) {
				msg = SysBasicCfg.getInstance().getManualJoinAllianceChat();
			}
			ChatManager.getInstance().postChat(player, ChatManager.getMsgJson(msg, player.getName()),
					Const.chatType.CHAT_ALLIANCE_SYSTEM_VALUE, 1);
		}

		GsApp.getInstance().postMsg(GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.ALLIANCE_BATTLE), Msg
				.valueOf(GsConst.MsgType.ALLIANCE_MEMBER_ADD).pushParam(targetAllianceEntity.getId(), player.getId()));

		// 刷新快照
		if (isOnline) {
			player.getPlayerData().refreshOnlinePlayerSnapshot();
			BehaviorLogger.log4Platform(player, Action.ALLIANCE_JOIN_ALLIANCE,
					Params.valueOf("allianceId", targetAllianceEntity.getId()),
					Params.valueOf("allianceName", targetAllianceEntity.getName()),
					Params.valueOf("memberQuantity", targetAllianceEntity.getMemberList().size()),
					Params.valueOf("allianceLevel", targetAllianceEntity.getLevel()),
					Params.valueOf("chairmanId", targetAllianceEntity.getPlayerId()),
					Params.valueOf("chairmanName", targetAllianceEntity.getPlayerName()));
		} else {
			Log.logPrintln(" offline player into Alliance | allianceId :" + targetAllianceEntity.getId()
					+ " | playerId : " + player.getPlayerData().getId());
		}

		// 发邮件通知加入的会员和全体公会成员
		sendMailNotice(player, targetAllianceEntity, myPlayerAllianceEntity,
				Const.AddOrExitAlliance.ADD_ALLIANCE_TYPE_1_VALUE, 0);
	}

	/**
	 * 更换会长
	 * 
	 * @param allianceEntity
	 * @param myAllianceEntity
	 * @param targetPlayerId
	 */
	public void changeMain(AllianceEntity allianceEntity, PlayerAllianceEntity myAllianceEntity, int targetPlayerId) {
		boolean isTargetOnline = false;
		// 新会长是否在线
		Player targetPlayer = AllianceManager.getInstance().whetherOnline(targetPlayerId);
		if (targetPlayer != null) {
			isTargetOnline = true;
			PlayerAllianceEntity targetAllianceEntity = targetPlayer.getPlayerData().getPlayerAllianceEntity();
			targetAllianceEntity.setPostion(GsConst.Alliance.ALLIANCE_POS_MAIN);
			targetAllianceEntity.setAddVitalityTime(GuaJiTime.getCalendar().getTime());
			targetAllianceEntity.notifyUpdate(false);
			// 公会数据更新
			allianceEntity.setPlayerId(targetPlayer.getId());
			allianceEntity.setPlayerName(targetPlayer.getName());
			allianceEntity.notifyUpdate(false);
			// 玩家快照刷新
			SnapShotManager.getInstance().getCurrentStrategy().onAllianceDataChanged(targetAllianceEntity);
			// 同步玩家公会数据
			AllianceManager.getInstance().sendSelfData(HPAllianceEnterS.newBuilder(), targetPlayer, allianceEntity);
		}
		if (!isTargetOnline) {
			PlayerSnapshotInfo.Builder builder = SnapShotManager.getInstance().getPlayerSnapShot(targetPlayerId);
			if (builder != null) {
				allianceEntity.setPlayerId(targetPlayerId);
				allianceEntity.setPlayerName(builder.getMainRoleInfo().getName());
				allianceEntity.notifyUpdate(false);
			}
			// 修改target玩家快照工会数据
			AllianceManager.getInstance().updateOfflineAllianceData(targetPlayerId, GsConst.Alliance.ALLIANCE_POS_MAIN,
					allianceEntity.getId());
		}

		// 老会长是否在线
		Player oldPlayer = AllianceManager.getInstance().whetherOnline(myAllianceEntity.getPlayerId());
		if (oldPlayer != null) {
			// 在线玩家数据集
			myAllianceEntity = oldPlayer.getPlayerData().getPlayerAllianceEntity();
			myAllianceEntity.setPostion(GsConst.Alliance.ALLIANCE_POS_COMMON);
			myAllianceEntity.notifyUpdate(false);
			SnapShotManager.getInstance().getCurrentStrategy().onAllianceDataChanged(myAllianceEntity);
		} else {
			myAllianceEntity.setPostion(GsConst.Alliance.ALLIANCE_POS_COMMON);
			myAllianceEntity.notifyUpdate(false);
			SnapShotManager.getInstance().getCurrentStrategy().onAllianceDataChanged(myAllianceEntity);
			AllianceManager.getInstance().offlineRefresh(myAllianceEntity);
		}

		BehaviorLogger.log4Service(0, Source.SYS_OPERATION, Action.CHANGE_ALLIANCE_MAIN,
				Params.valueOf("oldPlayerId", myAllianceEntity.getPlayerId()),
				Params.valueOf("newPlayerId", targetPlayerId));

		// 如果当前会长还存在未确认的申请加入邮件，则进行删除
		deleteApplyAddAllianceMsg(oldPlayer, myAllianceEntity.getPlayerId(), allianceEntity,
				ApplyAddAllianceUpEmail.UPEMAIL_TYPE_2_VALUE);
	}

	/**
	 * 如果当前会长还存在未确认的申请加入邮件，则进行删除
	 * 
	 * @param oldPlayer
	 * @param allianceEntity
	 * @param type           1.过期删除 2.公会会长自动转让
	 */
	public void deleteApplyAddAllianceMsg(Player oldPlayer, int oldPlayerId, AllianceEntity allianceEntity, int type) {
		try {
			// 如果当前会长还存在未确认的申请加入邮件，则进行删除.
			List<EmailEntity> emailEntities = DBManager.getInstance().query(
					"from EmailEntity where playerId = ? and mailId = ?", oldPlayerId,
					GsConst.MailId.APPLY_ADD_ALLIANCE);
			if (emailEntities != null && emailEntities.size() > 0) {
				// 给未进行操作的申请加入公会邮件进行删除并且发送回执邮件.
				for (EmailEntity emailEntity : emailEntities) {
					deleteApplyAddAlliance(emailEntity, oldPlayer, oldPlayerId, allianceEntity, type);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 如果当前会长还存在未确认的申请加入邮件，则进行删除
	 * 
	 * @param oldPlayer
	 * @param allianceEntity
	 * @param type           1.过期删除 2.公会会长自动转让
	 */
	public void deleteApplyAddAllianceMsg(Player oldPlayer, int oldPlayerId, AllianceEntity allianceEntity, int type,
			EmailEntity emailEntity) {
		deleteApplyAddAlliance(emailEntity, oldPlayer, oldPlayerId, allianceEntity, type);
	}

	private void deleteApplyAddAlliance(EmailEntity emailEntity, Player oldPlayer, int oldPlayerId,
			AllianceEntity allianceEntity, int type) {
		try {
			// 给申请加入公会的玩家发送会长转让邮件
			String sk = emailEntity.getParams();
			if (sk != null) {
				PlayerEmailData emailData = GsonUtil.getJsonInstance().fromJson(sk, new TypeToken<PlayerEmailData>() {
				}.getType());
				if (type == ApplyAddAllianceUpEmail.UPEMAIL_TYPE_1_VALUE) {
					// 发邮件给申请加入公会的玩家
					MailManager.createSysMail(emailData.getId(), Mail.MailType.Normal_VALUE,
							GsConst.MailId.EMAIL_FAILURE_NOT_OPER, "会长邮件过期,不能进行审核操作" + allianceEntity.getId(), null,
							GuaJiTime.getTimeString(), allianceEntity.getName());
				} else if (type == ApplyAddAllianceUpEmail.UPEMAIL_TYPE_2_VALUE) {
					// 发邮件给申请加入公会的玩家
					MailManager.createSysMail(emailData.getId(), Mail.MailType.Normal_VALUE,
							GsConst.MailId.CHANGE_MAIN_ALLIANCE, "老会长转让,申请加入公会邮件失效" + allianceEntity.getId(), null,
							GuaJiTime.getTimeString(), allianceEntity.getName());
				}
				// 处理玩家的申请状态
				int playerId = emailData.getId();
				Player applyPlayer = AllianceManager.getInstance().whetherOnline(playerId);
				PlayerAllianceEntity playerAllianceEntity = null;
				if (applyPlayer != null) {
					playerAllianceEntity = applyPlayer.getPlayerData().getPlayerAllianceEntity();
					if (playerAllianceEntity == null) {
						playerAllianceEntity = getPlayerAlliance(playerId);
					}
					Map<Integer, ApplyAllianceStates> applyAllianceStatesMap = playerAllianceEntity
							.getApplyAllianceDataMap();
					applyAllianceStatesMap.remove(allianceEntity.getId());
					playerAllianceEntity.notifyUpdate(false);
					SnapShotManager.getInstance().getCurrentStrategy().onAllianceDataChanged(playerAllianceEntity);
				} else {
					playerAllianceEntity = getPlayerAlliance(playerId);
					if (playerAllianceEntity != null) {
						Map<Integer, ApplyAllianceStates> applyAllianceStatesMap = playerAllianceEntity
								.getApplyAllianceDataMap();
						applyAllianceStatesMap.remove(allianceEntity.getId());
						playerAllianceEntity.notifyUpdate(false);
						SnapShotManager.getInstance().getCurrentStrategy().onAllianceDataChanged(playerAllianceEntity);
						AllianceManager.getInstance().offlineRefresh(playerAllianceEntity);
					}
				}
			}
			if (type == ApplyAddAllianceUpEmail.UPEMAIL_TYPE_2_VALUE) { // 只操作公会会长转让删除
				// 老会长在线
				if (oldPlayer != null) {
					HPApplyAllianceEmailRemoves.Builder ret = HPApplyAllianceEmailRemoves.newBuilder();
					ret.setEmailId(emailEntity.getId());
					oldPlayer.sendProtocol(Protocol.valueOf(HP.code.APPLY_ALLIANCE_EMAIL_REMOVE, ret));
					oldPlayer.getPlayerData().removeEmailEntity(emailEntity);
				} else { // 老会长不在线
					emailEntity.delete(false);
					Log.logPrintln(
							"offline delete changeMain applyAddAlliance email  | emailId :" + emailEntity.getId());
				}
			} else if (type == ApplyAddAllianceUpEmail.UPEMAIL_TYPE_1_VALUE) {
				HPApplyAllianceEmailRemoves.Builder ret = HPApplyAllianceEmailRemoves.newBuilder();
				ret.setEmailId(emailEntity.getId());
				oldPlayer.sendProtocol(Protocol.valueOf(HP.code.APPLY_ALLIANCE_EMAIL_REMOVE, ret));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送邮件通知公会成员,老会长，新会长
	 * 
	 * @param allianceEntity
	 * @param playerAllianceEntity
	 * @param newPlayerAllianceEntity
	 */
	public void sendNoticeMail(AllianceEntity allianceEntity, PlayerAllianceEntity playerAllianceEntity,
			PlayerAllianceEntity newPlayerAllianceEntity) {
		// 新会长玩家数据
		Player targetPlayer = AllianceManager.getInstance().whetherOnline(newPlayerAllianceEntity.getPlayerId());
		if (targetPlayer == null) {
			// 离线数据Player
			PlayerEntity playerEntity = AllianceManager.getInstance().getPlayer(newPlayerAllianceEntity.getPlayerId());
			if (playerEntity != null) {
				targetPlayer = new Player(
						GuaJiXID.valueOf(GsConst.ObjType.PLAYER, newPlayerAllianceEntity.getPlayerId()));
				targetPlayer.getPlayerData().setPlayerEntity(playerEntity);
			}
		}
		// 发邮件给成员更换会长的邮件
		Set<Integer> setPerson = allianceEntity.getMemberList();
		for (Integer playerId : setPerson) {
			// 剔除掉老会长和新会长
			if (playerId != playerAllianceEntity.getPlayerId() && playerId != newPlayerAllianceEntity.getPlayerId()) {
				// 发邮件公会成员告知会长进行变动
				MailManager.createSysMail(playerId, Mail.MailType.Normal_VALUE,
						GsConst.MailId.CHANGE_MAIN_SENDTO_COMMON, "给公会成员发会长变动的邮件", null, GuaJiTime.getTimeString(),
						targetPlayer.getName());
			}
		}
		// 发邮件给老会长
		MailManager.createSysMail(playerAllianceEntity.getPlayerId(), Mail.MailType.Normal_VALUE,
				GsConst.MailId.CHANGE_MAIN_SENDTO_OLD_MAIN, "给老会长发会长变动的邮件", null, GuaJiTime.getTimeString(),
				targetPlayer.getName());
		// 发邮件给新会长
		MailManager.createSysMail(newPlayerAllianceEntity.getPlayerId(), Mail.MailType.Normal_VALUE,
				GsConst.MailId.CHANGE_MAIN_SENDTO_NEW_MAIN, "给新会长发会长变动的邮件", null, GuaJiTime.getTimeString());
	}

	/**
	 * 加入公会或者退出公会发邮件
	 * 
	 * @param player
	 * @param allianceEntity
	 * @param newPlayerAllianceEntity
	 * @param type
	 * @param exitType
	 */
	public void sendMailNotice(Player player, AllianceEntity allianceEntity,
			PlayerAllianceEntity newPlayerAllianceEntity, int type, int exitType) {
		// 发邮件给成员更换会长的邮件
		Set<Integer> setPerson = allianceEntity.getMemberList();
		for (Integer playerId : setPerson) {
			if (type == Const.AddOrExitAlliance.ADD_ALLIANCE_TYPE_1_VALUE) {
				if (playerId != newPlayerAllianceEntity.getPlayerId()) {
					MailManager.createSysMail(playerId, Mail.MailType.Normal_VALUE,
							GsConst.MailId.ADD_ALLIANCE_ALL_EMAIL, "加入公会-全体", null, GuaJiTime.getTimeString(),
							player.getName());
				}
			} else if (type == Const.AddOrExitAlliance.EXIT_ALLIANCE_TYPE_2_VALUE) { // 退出公会
				if (playerId != newPlayerAllianceEntity.getPlayerId()
						&& exitType == ExitAllianceState.INITIATIVE_TYPE_1_VALUE) {
					MailManager.createSysMail(playerId, Mail.MailType.Normal_VALUE,
							GsConst.MailId.OWN_EXIT_ALLIANCE_ALL_EMAIL, "退出公会-全体-自己退出", null, GuaJiTime.getTimeString(),
							player.getName());
				} else if (playerId != newPlayerAllianceEntity.getPlayerId()
						&& exitType == ExitAllianceState.MAIN_KICK_TYPE_2_VALUE) {
					MailManager.createSysMail(playerId, Mail.MailType.Normal_VALUE,
							GsConst.MailId.KICK_ALLIANCE_ALL_EMAIL, "退出公会-全体-管理员踢出", null, GuaJiTime.getTimeString(),
							player.getName());
				}
			}
		}
		// 发当事成员
		if (type == Const.AddOrExitAlliance.ADD_ALLIANCE_TYPE_1_VALUE) { // 加入公会
			MailManager.createSysMail(player.getId(), Mail.MailType.Normal_VALUE,
					GsConst.MailId.ADD_ALLIANCE_PERSON_EMAIL, "加入公会-个人", null, GuaJiTime.getTimeString(),
					allianceEntity.getName());
		} else if (type == Const.AddOrExitAlliance.EXIT_ALLIANCE_TYPE_2_VALUE
				&& exitType == ExitAllianceState.MAIN_KICK_TYPE_2_VALUE) { // 退出公会
			MailManager.createSysMail(player.getId(), Mail.MailType.Normal_VALUE,
					GsConst.MailId.EXIT_ALLIANCE_PERSON_EMAIL, "退出公会-个人", null, GuaJiTime.getTimeString(),
					allianceEntity.getName());
		}
	}

	/**
	 * 获取离线玩家公会数据
	 * 
	 * @return
	 */
	public PlayerAllianceEntity getPlayerAlliance(int playerId) {
		PlayerAllianceEntity playerAlliance = null;
		List<PlayerAllianceEntity> playerEntitys = DBManager.getInstance()
				.query("from PlayerAllianceEntity where invalid = 0 and playerId = ?", playerId);
		if (playerEntitys != null && playerEntitys.size() > 0) {
			playerAlliance = playerEntitys.get(0);
			playerAlliance.init();
		}
		return playerAlliance;
	}

	/**
	 * 获取离线玩家公会数据
	 * 
	 * @return
	 */
	public PlayerEntity getPlayer(int playerId) {
		PlayerEntity player = null;
		List<PlayerEntity> playerList = DBManager.getInstance().query("from PlayerEntity where invalid = 0 and id = ?",
				playerId);
		if (playerList != null && playerList.size() > 0) {
			player = playerList.get(0);
		}
		return player;
	}

	/**
	 * 离线数据刷新
	 * 
	 * @param allianceEntity
	 */
	public void offlineRefresh(PlayerAllianceEntity allianceEntity) {
		// 同步公会列表信息
		GuaJiXID targetXID = GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.ALLIANCE);
		Msg msgAlliance = Msg.valueOf(GsConst.MsgType.OFFLINE_ALLIANCE_INFO, targetXID);
		msgAlliance.pushParam(allianceEntity.getAllianceId());
		GsApp.getInstance().postMsg(msgAlliance);
	}

	/**
	 * 验证玩家是否在公会中
	 * 
	 * @param entity
	 * @param playerId
	 * @return
	 */
	public boolean isIntoAlliance(AllianceEntity entity, int playerId) {
		// 公会全部成员
		Set<Integer> playerIdSet = entity.getMemberList();
		if (playerIdSet != null && playerIdSet.size() > 0) {
			for (Integer pid : playerIdSet) {
				if (pid == playerId) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 验证是否已经转生
	 * 
	 * @return
	 */
	public boolean isRebirthStageOne(Player player) {
		if (player.getPlayerData().getMainRole().getRebirthStage() == 0) {
			return false;
		}
		return true;
	}

	/**
	 * 验证加入公会申请是否超过最大上限
	 * 
	 * @param playerId
	 * @return
	 */
	public boolean checkApplyAllianceMax(int playerId) {
		List<EmailEntity> emailEntities = DBManager.getInstance().query(
				"from EmailEntity where playerId = ? and mailId = ? and invalid = 0", playerId,
				GsConst.MailId.APPLY_ADD_ALLIANCE);
		if (emailEntities.size() >= SysBasicCfg.getInstance().getApplyAddAllianceMaxVal()) {
			return true;
		}
		return false;
	}

	/**
	 * 是否存在名字
	 * 
	 * @param name
	 * @return
	 */
	public boolean isExistName(String name) {
		return existName.contains(name);
	}

	/**
	 * 添加公会名字
	 */
	public boolean addName(String name) {
		return existName.add(name);
	}

	/**
	 * 移除现在现有的名字
	 */
	public void removeNameFromExist(String name) {
		if (name == null) {
			return;
		}
		if (AllianceManager.getInstance().existName.contains(name)) {
			this.existName.remove(name);
		}
	}

	public HPAllianceDonateInfoResp.Builder buildAllianceDonateInfo(int activeValue, int todayDonateId) {
		List<AllianceDonateCfg> cfgs = ConfigManager.getInstance().getConfigList(AllianceDonateCfg.class);
		HPAllianceDonateInfoResp.Builder resp = HPAllianceDonateInfoResp.newBuilder();
		resp.setActiveValue(activeValue);
		for (AllianceDonateCfg cfg : cfgs) {
			HPAllianceDonateInfoItem.Builder item = HPAllianceDonateInfoItem.newBuilder();
			item.setActiveValueAward(cfg.getActiveValue());
			item.setAllianceAward(cfg.getAllianceAward());
			item.setPersonAward(cfg.getPersonAward());
			item.setCosume(cfg.getConsume());
			if (todayDonateId > 0) {
				// 1代表可捐献,2代表以捐献，3代表不可捐献
				item.setDotateEnable((todayDonateId == cfg.getId()) ? 2 : 3);
			} else {
				item.setDotateEnable(1);
			}
			resp.addItems(item);
		}
		return resp;
	}
	

	

}
