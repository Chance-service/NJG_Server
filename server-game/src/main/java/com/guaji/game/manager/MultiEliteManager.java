package com.guaji.game.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.MultiEliteTimeCfg;
import com.guaji.game.config.MultiLuckRoleCfg;
import com.guaji.game.config.MultiMapCfg;
import com.guaji.game.config.MultiWorldLevelCfg;
import com.guaji.game.config.MultiWorldLevelWeightCfg;
import com.guaji.game.entity.MultiEliteReportEntity;
import com.guaji.game.entity.MultiEliteRoomInfo;
import com.guaji.game.entity.MultiElitetInfoEntity;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Mail.MailType;
import com.guaji.game.protocol.MultiElite.HPMultiEliteStatePush;
import com.guaji.game.protocol.MultiElite.MultiEliteBattleResult;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsonUtil;

/**
 * 多人副本管理器
 */
public class MultiEliteManager extends AppObj {

	/**
	 * 多人副本副本实体
	 */
	private MultiElitetInfoEntity multiElitetInfoEntity;
	/**
	 * 副本对应房间集合
	 */
	private Map<Integer, RoomManager> allMultiEliteRoomManMap;
	/**
	 * 下一场多人副本时间配置
	 */
	private long nextMultiEliteTime;
	/**
	 * 全局对象, 便于访问
	 */
	private static MultiEliteManager instance = null;
	/**
	 * 通知红点
	 */
	private boolean isNotice = false;

	/**
	 * 获取全局实例对象
	 */
	public static MultiEliteManager getInstance() {
		return instance;
	}

	public MultiEliteManager(GuaJiXID xid) {
		super(xid);
		allMultiEliteRoomManMap = new ConcurrentHashMap<Integer, RoomManager>();
		if (instance == null) {
			instance = this;
		}
	}

	/**
	 * 加载数据
	 */
	public MultiElitetInfoEntity loadMultiEliteInfoEntity() {
		multiElitetInfoEntity = DBManager.getInstance().fetch(MultiElitetInfoEntity.class, "from MultiElitetInfoEntity where invalid = 0");
		if (multiElitetInfoEntity != null) {
			// 转换字符串到对象
			multiElitetInfoEntity.convertToList();
			this.nextMultiEliteTime = multiElitetInfoEntity.getNextRefreshTime();
		} else {
			multiElitetInfoEntity = new MultiElitetInfoEntity();
			init();
		}

		MultiEliteTimeCfg timeCfg = MultiEliteTimeCfg.getCurMultiEliteTimeCfg();
		if (timeCfg != null) {
			isNotice = true;
		}
		return multiElitetInfoEntity;
	}

	/**
	 * 每次开放刷新副本难度
	 * 
	 * @param isOpenServer
	 *            是否是启动服务器
	 * @return
	 */
	public boolean init() {
		multiElitetInfoEntity.getMultiMapList().clear();
		multiElitetInfoEntity.getMultiLuckRoleMap().clear();

		// 获取今日的副本级别
		int serverLevel = MultiWorldLevelCfg.getServerMultiLevel();

		// 获取当前级别所有星级配置
		List<MultiWorldLevelWeightCfg> multiServerLevel = MultiWorldLevelWeightCfg.getServerLevel(serverLevel);

		if (multiServerLevel == null || multiServerLevel.size() <= 0) {
			return false;
		}

		// 每星级取N个副本
		for (MultiWorldLevelWeightCfg starCfg : multiServerLevel) {
			// 副本个数
			int multiCount = starCfg.getRandMapCount();
			if (multiCount <= 0) {
				continue;
			}

			List<Integer> multiMapList = starCfg.getMultiIdList(multiCount);
			if (multiMapList == null || multiMapList.size() <= 0) {
				continue;
			}
			multiElitetInfoEntity.getMultiMapList().addAll(multiMapList);
		}
		// 当前福将
		String dateStr = GuaJiTime.DATE_FORMATOR_DAYNUM(new Date());
		MultiLuckRoleCfg luckRoleCfg = ConfigManager.getInstance().getConfigByKey(MultiLuckRoleCfg.class, dateStr);
		if (luckRoleCfg != null) {
			multiElitetInfoEntity.setMultiLuckRoleMap(luckRoleCfg.getMultiLuckRole());
		}

		this.nextMultiEliteTime = MultiEliteTimeCfg.getNextMultiEliteTimeCfg().getStartDate().getTimeInMillis();
		multiElitetInfoEntity.setNextRefreshTime(nextMultiEliteTime);
		multiElitetInfoEntity.notifyUpdate();
		// log
		Log.logPrintln("nextMultiEliteTime " + nextMultiEliteTime + "mapList " + multiElitetInfoEntity.getMultiMapList().toString() + "luckRole "
				+ multiElitetInfoEntity.getMultiLuckRoleStr());
		return true;
	}

	@Override
	public boolean onTick() {
		if (allMultiEliteRoomManMap.size() > 0) {
			Iterator<Entry<Integer, RoomManager>> it = allMultiEliteRoomManMap.entrySet().iterator();
			while (it.hasNext()) {
				it.next().getValue().onTick();
			}
		}

		long now=GuaJiTime.getCalendar().getTimeInMillis();
		long curTime = GuaJiTime.getMillisecond();
		if (this.nextMultiEliteTime != 0) {
			if (curTime >= this.nextMultiEliteTime) {
				HPMultiEliteStatePush.Builder stateBuilder = HPMultiEliteStatePush.newBuilder();
				// 开启红点
				stateBuilder.setState(1);
				isNotice = true;
				// 刷新副本
				init();
				
				Log.logPrintln(String.format("state 1  %tF",now));
				// 推送所有玩家
				GsApp.getInstance().broadcastProtocol(Protocol.valueOf(HP.code.MULIELTIE_PUSH_STATE_S_VALUE, stateBuilder));
				this.nextMultiEliteTime = MultiEliteTimeCfg.getNextMultiEliteTimeCfg().getStartDate().getTimeInMillis();

			}
		}

		MultiEliteTimeCfg timeCfg = MultiEliteTimeCfg.getCurMultiEliteTimeCfg();
		if (timeCfg == null && isNotice) {
			HPMultiEliteStatePush.Builder stateBuilder = HPMultiEliteStatePush.newBuilder();
			// 关闭红点
			stateBuilder.setState(0);

			Log.logPrintln(String.format("state 0  %tF",now));
			// 推送所有玩家
			GsApp.getInstance().broadcastProtocol(Protocol.valueOf(HP.code.MULIELTIE_PUSH_STATE_S_VALUE, stateBuilder));
			isNotice = false;
			allMultiEliteRoomManMap.clear();
		}

		return true;
	}

	/**
	 * 获取玩家当前所在的房间
	 * 
	 * @param playerId
	 * @return 未在任何房间返回0
	 */
	public int getPlayerRoomId(int playerId) {
		synchronized (allMultiEliteRoomManMap) {
			for (RoomManager rm : allMultiEliteRoomManMap.values()) {
				int roomId = rm.getPlayerRoomId(playerId);
				if (roomId > 0) {
					return roomId;
				}
			}
		}
		return 0;
	}

	/**
	 * 获取玩家当前所在房间
	 */
	public MultiEliteRoomInfo getCurInRoom(int playerId) {
		int myRoomId = getPlayerRoomId(playerId);
		synchronized (allMultiEliteRoomManMap) {
			for (RoomManager listRoom : allMultiEliteRoomManMap.values()) {
				if (listRoom != null && listRoom.getRoom(myRoomId) != null) {
					return listRoom.getRoom(myRoomId);
				}
			}
		}
		return null;
	}

	/**
	 * 获取对应房间管理器
	 * 
	 * @param eliteId
	 * @return
	 */
	public RoomManager getMultiEliteRoom(int mapId) {
		return allMultiEliteRoomManMap.get(mapId);
	}

	/**
	 * 向房间内所有成员广播消息
	 * 
	 * @param msg
	 */
	public void broadcastRoomMemberMsg(Msg msg, List<Integer> memberPlayerIds) {
		List<GuaJiXID> xidList = new ArrayList<GuaJiXID>();
		ArrayList<Integer> playerIds = new ArrayList<Integer>(memberPlayerIds);
		for (int playerId : playerIds) {
			GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId);
			xidList.add(xid);
		}
		GsApp.getInstance().postMsg(xidList, msg);
	}

	/**
	 * 通知所邀请好友
	 * 
	 * @param msg
	 */
	public void broadFriendMsg(Msg msg) {
		int playerId = msg.getParam(0);
		GuaJiXID xid = GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId);
		GsApp.getInstance().postMsg(xid, msg);
	}

	/**
	 * 日期格式化
	 */
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 战斗结束，邮件通知
	 * 
	 * @param isWin
	 * @param room
	 * @param reportEntity
	 * @param awardItems
	 */
	public void sendRoomMemberAward(boolean isWin, MultiEliteRoomInfo room, MultiEliteReportEntity reportEntity) {
//		String dateString = DATE_FORMAT.format(GuaJiTime.getMillisecond());
//		MultiMapCfg mapCfg = ConfigManager.getInstance().getConfigByKey(MultiMapCfg.class, room.getMultiEliteId());
//		if (mapCfg != null) {
//			for (int playerId : room.getMemberPlayerIds().keySet()) {
//				MultiEliteMailObj eliteMailObj = new MultiEliteMailObj(dateString, reportEntity.getId(), room.getMultiEliteId());
//				if (isWin) {
//					MailManager.createMail(playerId, MailType.MULTI_ELITE_VALUE, GsConst.MailId.MULTI_ELITE_WIN_REPORT, mapCfg.getEmailTitle(), null,
//							eliteMailObj.toString());
//				} else {
//					MailManager.createMail(playerId, MailType.MULTI_ELITE_VALUE, GsConst.MailId.MULTI_ELITE_LOSE_REPORT, mapCfg.getEmailTitle(), null,
//							eliteMailObj.toString());
//				}
//			}
//		}
	}

	/**
	 * 数据库拉取战报数据
	 * 
	 * @param lastMultiEliteBattleResultId
	 */
	public MultiEliteBattleResult.Builder loadMultiEliteBattleReport(int lastMultiEliteBattleResultId) {
		List<MultiEliteReportEntity> MultiEliteReportEntities = DBManager.getInstance().query("from MultiEliteReportEntity where id=?",
				lastMultiEliteBattleResultId);
		if (MultiEliteReportEntities.size() > 0) {
			return MultiEliteReportEntities.get(0).convertResultToBuilder();
		}
		return null;
	}

	/**
	 * 添加副本对应房间集合
	 * 
	 * @param multiId
	 * @param roomManager
	 */
	public RoomManager addAllMultiEliteRoomManMap(int mapId) {
		if (!allMultiEliteRoomManMap.containsKey(mapId)) {
			RoomManager roomManager = new RoomManager(mapId);
			allMultiEliteRoomManMap.put(mapId, roomManager);
		}
		return allMultiEliteRoomManMap.get(mapId);
	}

	/**
	 * 是否在副本开启时间内
	 * 
	 * @return
	 */
	public boolean checkIsMultTime() {
		MultiEliteTimeCfg timeCfg = MultiEliteTimeCfg.getCurMultiEliteTimeCfg();
		if (timeCfg == null) {
			return false;
		}

		return GuaJiTime.getMillisecond() >= timeCfg.getStartDate().getTimeInMillis()
				&& timeCfg.getEndDate().getTimeInMillis() > GuaJiTime.getMillisecond();
	}

	public MultiElitetInfoEntity getMultiElitetInfoEntity() {
		return multiElitetInfoEntity;
	}

	public void setMultiElitetInfoEntity(MultiElitetInfoEntity multiElitetInfoEntity) {
		this.multiElitetInfoEntity = multiElitetInfoEntity;
	} 

}

/**
 * 组装多人副本邮件json
 * 
 * @author zdz
 *
 */
class MultiEliteMailObj {
	private String dateStr;
	private int reportId;
	private int mapId;

	public MultiEliteMailObj(String dateStr, int reportId, int mapId) {
		this.dateStr = dateStr;
		this.reportId = reportId;
		this.mapId = mapId;
	}

	@Override
	public String toString() {
		String json = GsonUtil.getJsonInstance().toJson(this);
		return json;
	}

	public String getDateStr() {
		return dateStr;
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}

	public int getReportId() {
		return reportId;
	}

	public void setReportId(int reportId) {
		this.reportId = reportId;
	}

	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

}
