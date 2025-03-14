package com.guaji.game.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.guaji.app.App;
import org.guaji.app.AppObj;
import org.guaji.cache.CacheObj;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.msg.Msg;
import org.guaji.net.GuaJiSession;
import org.guaji.net.protocol.Protocol;
import org.guaji.thread.GuaJiTask;
import org.guaji.xid.GuaJiXID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.guaji.game.GsApp;
import com.guaji.game.ServerData;
import com.guaji.game.config.GmPuidCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.ChatMsgEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.protocol.Chat.HPPushChat;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.NoticeType;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Notice.HPNotice;
import com.guaji.game.protocol.Notice.NoticeItem;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.ProtoUtil;

/**
 * 聊天管理器,加入全服提醒推送
 */
public class ChatManager extends AppObj {
	private static final Logger logger = LoggerFactory.getLogger("Chat");

	/**
	 * 聊天消息缓存
	 */
//	private static int CHAT_MSG_CHUNK = 60;

	private class ChatCache {
		// 缓存索引
		int cacheIndex;
		// 缓存消息
		ChatMsg[] cacheMsg;

		// 构造
		ChatCache() {
			cacheIndex = 0;
			cacheMsg = new ChatMsg[SysBasicCfg.getInstance().getChat_msg_chunk()];
		}
	}

	/**
	 * 索引锁
	 */
	private Lock chatListLock;
	/**
	 * 索引锁
	 */
	private Lock noticeListLock;

	/**
	 * 包队列
	 */
	List<ChatMsg> chatMsgList;
	/**
	 * 提醒消息队列
	 */
	List<NoticeMsg> noticeMsgList;
	/**
	 * 世界聊天会话列表
	 */

	Map<GuaJiSession, GuaJiSession> chatSessions;
	/**
	 * 公会sesion 公会ID, 角色ID,session
	 */
	private Map<Integer, Map<Integer, GuaJiSession>> allianceSessions;
	/**
	 * 缓存的消息队列
	 */
	private Map<Integer, ChatCache> chatMsgCache;
	/**
	 * 全局对象, 便于访问
	 */
	private static ChatManager instance = null;

	/**
	 * 获取全局实例对象
	 */
	public static ChatManager getInstance() {
		return instance;
	}

	/**
	 * 消息缓存数据
	 */
	private List<ChatMsgEntity> chatMsgEntities = null;

	/**
	 * 构造函数
	 */
	public ChatManager(GuaJiXID xid) {
		super(xid);
		if (instance == null) {
			instance = this;
		}

		chatListLock = new ReentrantLock();
		noticeListLock = new ReentrantLock();
		chatSessions = new ConcurrentHashMap<GuaJiSession, GuaJiSession>();
		allianceSessions = new ConcurrentHashMap<Integer, Map<Integer, GuaJiSession>>();
		chatMsgList = new LinkedList<ChatMsg>();
		noticeMsgList = new LinkedList<NoticeMsg>();
		chatMsgCache = new ConcurrentHashMap<Integer, ChatCache>();// 消息缓存索引表
		// 设置普通聊天缓存
//		chatMsgCache.put(0, new ChatCache());

		initMsg();
	}

	/**
	 * 从数据库加载聊天消息
	 */
	public void initMsg() {
		if (chatMsgEntities == null) {
			chatMsgEntities = DBManager.getInstance().query("from ChatMsgEntity where invalid = 0 order by id asc");
		}

		if (chatMsgEntities != null) {
			for (ChatMsgEntity chatMsgEntity : chatMsgEntities) {
				chatMsgCache.put(chatMsgEntity.getTypeId(), new ChatCache());

				chatMsgCache.get(chatMsgEntity.getTypeId()).cacheIndex = chatMsgEntity.getIndex();

				List<ChatMsg> msgList = null;
				msgList = chatMsgEntity.convert();

				if (msgList != null) {
					int i = 0;
					for (ChatMsg msg : msgList) {
						chatMsgCache.get(chatMsgEntity.getTypeId()).cacheMsg[i] = msg;
						i++;
					}
				}
			}
		}

		return;
	}

	/**
	 * 保存到数据库(不进行异步存储，需要时进行同步存储)
	 */
	public void saveToDB() {
		Iterator<Entry<Integer, ChatCache>> it = chatMsgCache.entrySet().iterator();

		while (it.hasNext()) {
			Entry<Integer, ChatCache> map = it.next();
			int typeId = map.getKey();
			int index = map.getValue().cacheIndex;

			List<ChatMsg> msgList = new ArrayList<ChatMsg>();

			for (int i = 0; i < map.getValue().cacheMsg.length; i++) {
				msgList.add(map.getValue().cacheMsg[i]);
			}

			if (chatMsgEntities == null) {
				// 创建
				ChatMsgEntity chatMsgEntity = ChatMsgEntity.valueOf(typeId, index);
				chatMsgEntity.reConvert(msgList);
				DBManager.getInstance().create(chatMsgEntity);
				chatMsgEntities.add(chatMsgEntity);

			} else {
				for (ChatMsgEntity chatMsgEntity : chatMsgEntities) {
					if (chatMsgEntity.getTypeId() == typeId) {
						// 更新
						chatMsgEntity.setIndex(index);
						chatMsgEntity.reConvert(msgList);
						chatMsgEntity.notifyUpdate(false);
						break;
					}
				}
				// 创建
				ChatMsgEntity chatMsgEntity = ChatMsgEntity.valueOf(typeId, index);
				chatMsgEntity.reConvert(msgList);
				DBManager.getInstance().create(chatMsgEntity);
				chatMsgEntities.add(chatMsgEntity);
			}
		}

	}

	private final Comparator<ChatMsg> MSG_TIME_SORT = new Comparator<ChatMsg>() {

		@Override
		public int compare(ChatMsg o1, ChatMsg o2) {

			if (o1 == null || o2 == null) {
				return 0;
			}

			if (o1.getMsTime() > o2.getMsTime()) {
				return 1;
			} else if (o1.getMsTime() < o2.getMsTime()) {
				return -1;
			}

			return 0;
		}

	};

	/**
	 * 添加活跃会话
	 * 
	 * @param session
	 */
	public void addSession(GuaJiSession session, boolean needLastChat) {
		if (session != null) {

			ChatCache cache = chatMsgCache.get(0);
			if (cache == null) {
				cache = new ChatCache();
				chatMsgCache.put(0, cache);
			}
			if (needLastChat) {
				HPPushChat.Builder builder = HPPushChat.newBuilder();
				// 按时间排序
				ChatMsg[] array = cache.cacheMsg.clone();
				Arrays.sort(array, MSG_TIME_SORT);
				for (int i = 0; i < SysBasicCfg.getInstance().getChat_msg_chunk(); i++) {
					if (array[i] != null) {
						builder.addChatMsg(array[i].genBuilder());
					}
				}
				if (builder.getChatMsgCount() > 0) {
					session.sendProtocol(Protocol.valueOf(HP.code.PUSH_CHAT_S, builder));
				}
			}
			chatSessions.put(session, session);
		}
	}

	/**
	 * 添加活跃会话
	 * 
	 * @param session
	 */
	public void addSession(GuaJiSession session, boolean needLastChat, boolean isLogin) {
		if (session != null) {

			ChatCache cache = chatMsgCache.get(0);
			if (cache == null) {
				cache = new ChatCache();
				chatMsgCache.put(0, cache);
			}
			if (needLastChat && !isLogin) {
				HPPushChat.Builder builder = HPPushChat.newBuilder();
				// 按时间排序
				ChatMsg[] array = cache.cacheMsg.clone();
				Arrays.sort(array, MSG_TIME_SORT);
				for (int i = 0; i < SysBasicCfg.getInstance().getChat_msg_chunk(); i++) {
					if (array[i] != null) {
						builder.addChatMsg(array[i].genBuilder());
					}
				}
				if (builder.getChatMsgCount() > 0) {
					session.sendProtocol(Protocol.valueOf(HP.code.PUSH_CHAT_S, builder));
				}
			}
			chatSessions.put(session, session);
		}
	}

	/**
	 * @param session
	 * @param alliaceId
	 * @param playerId  添加公会集合
	 */
	public void addAllianceSession(GuaJiSession session, Integer alliaceId, Integer playerId) {
		// 公会ID有效添加公会列表
		if (alliaceId != null && playerId != null && session != null) {
			Map<Integer, GuaJiSession> sessionMap = allianceSessions.get(alliaceId);
			if (sessionMap == null) {
				sessionMap = new ConcurrentHashMap<Integer, GuaJiSession>();
				allianceSessions.put(alliaceId, sessionMap);
			}

			ChatCache cache = chatMsgCache.get(alliaceId);
			if (cache == null) {
				cache = new ChatCache();
				chatMsgCache.put(alliaceId, cache);
			}

			// 按时间排序
			ChatMsg[] array = cache.cacheMsg.clone();
			Arrays.sort(array, MSG_TIME_SORT);
			HPPushChat.Builder builder = HPPushChat.newBuilder();
			for (int i = 0; i < SysBasicCfg.getInstance().getChat_msg_chunk(); i++) {
				if (array[i] != null) {
					builder.addChatMsg(array[i].genBuilder());
				}
			}

			if (builder.getChatMsgCount() > 0) {
				session.sendProtocol(Protocol.valueOf(HP.code.PUSH_CHAT_S, builder));
			}

			sessionMap.put(playerId, session);
		}
	}

	/**
	 * @param session
	 * @param alliaceId
	 * @param playerId  添加公会集合
	 */
	public void addAllianceSession(GuaJiSession session, Integer alliaceId, Integer playerId, boolean isLogin) {
		// 公会ID有效添加公会列表
		if (alliaceId != null && playerId != null && session != null) {
			Map<Integer, GuaJiSession> sessionMap = allianceSessions.get(alliaceId);
			if (sessionMap == null) {
				sessionMap = new ConcurrentHashMap<Integer, GuaJiSession>();
				allianceSessions.put(alliaceId, sessionMap);
			}

			ChatCache cache = chatMsgCache.get(alliaceId);
			if (cache == null) {
				cache = new ChatCache();
				chatMsgCache.put(alliaceId, cache);
			}

			if (!isLogin) {
				// 按时间排序
				ChatMsg[] array = cache.cacheMsg.clone();
				Arrays.sort(array, MSG_TIME_SORT);
				HPPushChat.Builder builder = HPPushChat.newBuilder();
				for (int i = 0; i < SysBasicCfg.getInstance().getChat_msg_chunk(); i++) {
					if (array[i] != null) {
						builder.addChatMsg(array[i].genBuilder());
					}
				}

				if (builder.getChatMsgCount() > 0) {
					session.sendProtocol(Protocol.valueOf(HP.code.PUSH_CHAT_S, builder));
				}

			}

			sessionMap.put(playerId, session);
		}
	}

	/**
	 * 移除会话对象
	 * 
	 * @param session
	 */
	public void removeSession(GuaJiSession session) {
		if (session != null) {
			chatSessions.remove(session);
		}
	}

	/**
	 * 移除公会聊天会话
	 * 
	 * @param alliaceId
	 * @param playerId
	 */
	public void removeAllaiceSession(Integer alliaceId, Integer playerId) {
		if (alliaceId == null) {
			return;
		}

		Map<Integer, GuaJiSession> sesssions = allianceSessions.get(alliaceId);
		if (sesssions != null) {
			sesssions.remove(playerId);
		}
	}



	/**
	 * 将公会信息清空
	 * 
	 * @param allianceId
	 */
	public void removeAlliace(Integer allianceId) {
		allianceSessions.remove(allianceId);
	}

	/**
	 * 返回公会所有成员session
	 * 
	 * @param allianceId
	 * @return
	 */
	public Map<Integer, GuaJiSession> getAllianceSession(Integer allianceId) {
		return allianceSessions.get(allianceId);
	}

	/**
	 * 投递聊天消息(由player自身调用)
	 * 
	 * @param player
	 * @param chatMsg
	 * @param type    0:世界聊天,1:公会聊天
	 */
	public void postChat(Player player, String chatMsg, int type, int jsonType) {
		// 改名和加入公会走公会系统频道
		postChat(player, chatMsg, type, jsonType, 0);
	}

	/**
	 * @param player
	 * @param chatMsg
	 * @param type
	 * @param jsonType
	 */
	public void postChat(Player player, String chatMsg, int type, int jsonType, int i18n) {
		PlayerData playerData = player.getPlayerData();
		RoleEntity roleEntity = playerData.getMainRole();
		if (roleEntity != null) {
			chatListLock.lock();
			try {
				ChatMsg msgObj = new ChatMsg();
				msgObj.setType(type);
				msgObj.setPlayerId(playerData.getPlayerEntity().getId());
				msgObj.setName(roleEntity.getName());
				msgObj.setLevel(roleEntity.getLevel());
				msgObj.setRebirthStage(roleEntity.getRebirthStage());
				msgObj.setRoleItemId(roleEntity.getItemId());
				msgObj.setChatMsg(GameUtil.filterString(chatMsg));
				if (playerData.getTitleEntity() != null) {
					msgObj.setTitleId(playerData.getTitleEntity().getUseId());
				}
				msgObj.setMsgType(jsonType);
				msgObj.setI18Flag(i18n);
				msgObj.setSkinId(player.getPlayerData().loadChatSkinEntity().getCurSkinId());
				msgObj.setAvatarId(player.getPlayerData().getUsedAvatarId());
				//msgObj.setHeadIcon(player.getPlayerData().getPlayerEntity().getHeroDataString());
				msgObj.setHeadIcon(String.valueOf(player.getPlayerData().getPlayerEntity().getHeadIcon()));
				// gm 账号
				GmPuidCfg gmPuid = ConfigManager.getInstance().getConfigByKey(GmPuidCfg.class, player.getPuid());
				if (gmPuid != null) {
					msgObj.setPlayerType(gmPuid.getType());
				}

				if (playerData.getStateEntity() != null && playerData.getStateEntity().isShowArea()
						&& playerData.getIpAddrEntity() != null) {
					msgObj.setArenaInfo(playerData.getIpAddrEntity().getPosition());
				}

				if (type == Const.chatType.CHAT_ALLIANCE_VALUE || type == Const.chatType.CHAT_ALLIANCE_SYSTEM_VALUE) {
					int allianceId = player.getPlayerData().getPlayerAllianceEntity().getAllianceId();
					if (allianceId == 0) {
						return;
					}
					msgObj.setAllianceId(allianceId);
				}

				chatMsgList.add(msgObj);

				// 添加到缓存
				if (type == Const.chatType.CHAT_WORLD_VALUE) {
					ChatCache cache = chatMsgCache.get(0);
					cache.cacheMsg[cache.cacheIndex++] = msgObj;
					cache.cacheIndex %= SysBasicCfg.getInstance().getChat_msg_chunk();
					// 推送世界发言任务
					QuestEventBus.fireQuestEventOneTime(QuestEventType.WORLD_SPEAK, player.getXid());
					
					Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.WORLD_SPEAK,
							GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
					hawkMsg.pushParam(1);
					GsApp.getInstance().postMsg(hawkMsg);

				} else if (type == Const.chatType.CHAT_ALLIANCE_VALUE
						|| type == Const.chatType.CHAT_ALLIANCE_SYSTEM_VALUE) {
					ChatCache cache = chatMsgCache.get(msgObj.getAllianceId());
					if (cache != null) {
						cache.cacheMsg[cache.cacheIndex++] = msgObj;
						cache.cacheIndex %= SysBasicCfg.getInstance().getChat_msg_chunk();
					}
				}

			} finally {
				chatListLock.unlock();
			}
		}
	}

	/**
	 * 直接添加广播包
	 * 
	 * @param msgObj
	 */
	public void postBroadcast(ChatMsg msgObj) {
		chatListLock.lock();
		try {
			chatMsgList.add(msgObj);
		} finally {
			chatListLock.unlock();
		}
	}

	/**
	 * 投递提醒消息
	 * 
	 * @param player
	 * @param chatMsg
	 * @param type    0:世界聊天,1:公会聊天
	 */
	public void postNotice(NoticeType noticeType, int count, String... params) {
		noticeListLock.lock();
		try {
			NoticeMsg msgObj = new NoticeMsg();
			msgObj.setNoticeType(noticeType);
			msgObj.setCount(count);
			msgObj.setParams(params);
			noticeMsgList.add(msgObj);
		} finally {
			noticeListLock.unlock();
		}
	}

	/**
	 * 广播聊天协议
	 * 
	 * @param builder
	 */
	private void broadcastChat(HPPushChat.Builder builder) {
		Iterator<Entry<GuaJiSession, GuaJiSession>> iterator = chatSessions.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<GuaJiSession, GuaJiSession> entry = iterator.next();
			entry.getKey().sendProtocol(Protocol.valueOf(HP.code.PUSH_CHAT_S, builder));
			if (!entry.getKey().isActive()) {
				iterator.remove();
			}
		}
	}

	/**
	 * 广播提醒消息
	 * 
	 * @param builder
	 */
	private void broadcastNotice(HPNotice.Builder builder) {
		Iterator<Entry<GuaJiSession, GuaJiSession>> iterator = chatSessions.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<GuaJiSession, GuaJiSession> entry = iterator.next();
			entry.getKey().sendProtocol(Protocol.valueOf(HP.code.NOTICE_PUSH, builder));
			if (!entry.getKey().isActive()) {
				iterator.remove();
			}
		}
	}

	/**
	 * 公会推消息
	 * 
	 * @param builder
	 * @param allianceId
	 */
	private void broadcastAllianceChat(HPPushChat.Builder builder, int allianceId) {
		Map<Integer, GuaJiSession> sessionMap = allianceSessions.get(allianceId);
		if (sessionMap != null) {
			Iterator<Entry<Integer, GuaJiSession>> iterator = sessionMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<Integer, GuaJiSession> entry = iterator.next();
				try {
					entry.getValue().sendProtocol(Protocol.valueOf(HP.code.PUSH_CHAT_S, builder));
					if (!entry.getValue().isActive()) {
						iterator.remove();
					}
				} catch (Exception e) {
					iterator.remove();
				}
			}
		}
	}

	/**
	 * 获取聊天消息缓存
	 * 
	 * @param type
	 */
	public ChatCache getChatMsgCacheByType(int type) {

		ChatCache cache = chatMsgCache.get(type);
		return cache;
	}

	/**
	 * 广播聊天协议
	 * 
	 * @param builder
	 */
	public void sendPlayerLoginChat(Player player) {

		if (player == null) {
			return;
		}
		HPPushChat.Builder builder = HPPushChat.newBuilder();
		// 普通聊天
		ChatCache cache = chatMsgCache.get(0);
		if (player.getPlayerData().getStateEntity().getChatClose() <= 0) {
			if (cache != null) {
				// 按时间排序
				ChatMsg[] array = cache.cacheMsg.clone();
				Arrays.sort(array, MSG_TIME_SORT);
				for (int i = 0; i < SysBasicCfg.getInstance().getChat_msg_chunk(); i++) {
					if (array[i] != null) {
						builder.addChatMsg(array[i].genBuilder());
					}
				}
			}
		}
		// 加载公会数据
		PlayerAllianceEntity allianceEntity = player.getPlayerData().loadPlayerAlliance();
		if(allianceEntity.getAllianceId()>0)
		{
			cache = chatMsgCache.get(allianceEntity.getAllianceId());
			if (cache != null) {
				// 按时间排序
				ChatMsg[] array = cache.cacheMsg.clone();
				Arrays.sort(array, MSG_TIME_SORT);
				for (int i = 0; i < SysBasicCfg.getInstance().getChat_msg_chunk(); i++) {
					if (array[i] != null) {
						builder.addChatMsg(array[i].genBuilder());
					}
				}
			}
		}
		
		if (builder.getChatMsgCount() > 0) {
			//player.sendProtocol(Protocol.valueOf(HP.code.PUSH_CHAT_S, builder));
			player.sendProtocol(ProtoUtil.compressProtocol(Protocol.valueOf(HP.code.PUSH_CHAT_S, builder)));
		}
	}

	/**
	 * 线程主执行函数
	 */
	@Override
	public boolean onTick() {
		if (chatMsgList.size() > 0) {
			App.getInstance().postCommonTask(new ChatTask());
		}

		if (noticeMsgList.size() > 0) {
			App.getInstance().postCommonTask(new NoticeTask());
		}
		return true;
	}

	/**
	 * 聊天任务
	 */
	private class ChatTask extends GuaJiTask {
		@Override
		protected CacheObj clone() {
			return new ChatTask();
		}

		@Override
		protected int run() {
			List<ChatMsg> sendChatMsgList = null;
			if (chatMsgList.size() > 0) {
				sendChatMsgList = new ArrayList<ChatMsg>(chatMsgList.size());
				chatListLock.lock();
				try {
					sendChatMsgList.addAll(chatMsgList);
					chatMsgList.clear();
				} finally {
					chatListLock.unlock();
				}
			}

			// 广播发送
			if (sendChatMsgList != null) {
				HPPushChat.Builder worldBuilder = HPPushChat.newBuilder();
				for (ChatMsg chatMsg : sendChatMsgList) {
					// plyayerId ?
					logger.info("[HPPushChat]plyayerId:[{}],playerName:[{}],chat:[{}],type:[{}]", new Object[] {
							chatMsg.getPlayerId(), chatMsg.getName(), chatMsg.getChatMsg(), chatMsg.getType() });

					// 世界聊天
					if (chatMsg.getType() == Const.chatType.CHAT_WORLD_VALUE
							|| chatMsg.getType() == Const.chatType.CHAT_BROADCAST_VALUE
							|| chatMsg.getType() == Const.chatType.WORLD_BROADCAST_VALUE) {

						if (worldBuilder.getChatMsgCount() >= SysBasicCfg.getInstance().getChat_msg_chunk()) {
							broadcastChat(worldBuilder);
							worldBuilder = HPPushChat.newBuilder();
						}
						worldBuilder.addChatMsg(chatMsg.genBuilder());
					}

					// 公会聊天
					if (chatMsg.getType() == Const.chatType.CHAT_ALLIANCE_VALUE
							|| chatMsg.getType() == Const.chatType.CHAT_ALLIANCE_SYSTEM_VALUE) {
						HPPushChat.Builder allianceBuilder = HPPushChat.newBuilder();
						allianceBuilder.addChatMsg(chatMsg.genBuilder());
						broadcastAllianceChat(allianceBuilder, chatMsg.getAllianceId());
					}
				}

				// 发送剩余信息
				if (worldBuilder.getChatMsgCount() > 0) {
					broadcastChat(worldBuilder);
				}
			}
			return 0;
		}
	}

	/**
	 * 公会通知任务
	 */
	private class NoticeTask extends GuaJiTask {
		@Override
		protected CacheObj clone() {
			return new NoticeTask();
		}

		@Override
		protected int run() {
			List<NoticeMsg> noticeMsgs = null;
			if (noticeMsgList.size() > 0) {
				noticeMsgs = new ArrayList<NoticeMsg>(noticeMsgList.size());
				noticeListLock.lock();
				try {
					noticeMsgs.addAll(noticeMsgList);
					noticeMsgList.clear();
				} finally {
					noticeListLock.unlock();
				}
			}

			// 广播发送
			if (noticeMsgs != null) {
				HPNotice.Builder noticeBuilder = HPNotice.newBuilder();
				for (NoticeMsg noticeMsg : noticeMsgs) {
					NoticeItem.Builder noticeItem = NoticeItem.newBuilder();
					noticeItem.setNoticeType(noticeMsg.getNoticeType());
					noticeItem.setCount(noticeMsg.getCount());
					for (String param : noticeMsg.getParams()) {
						noticeItem.addParams(param);
					}
					noticeBuilder.addNotices(noticeItem);
				}
				broadcastNotice(noticeBuilder);
			}
			return 0;
		}
	}

	/**
	 * 清理缓存
	 */
	public void clearCache() {
		ServerData.getInstance().clearSilentPhone();

		for (Integer key : chatMsgCache.keySet()) {
			chatMsgCache.put(key, new ChatCache());
		}
	}

	/**
	 * 取得聊天内容的json格式
	 * 
	 * @param key
	 * @param parms
	 * @return
	 */
	public static String getMsgJson(String key, Object... parms) {

		Gson gson = new Gson();
		MsgJson chatJson = new MsgJson();
		chatJson.setKey(key);
		for (Object str : parms) {
			chatJson.data.add(str);
		}
		if (parms == null || parms.length < 1) {
			chatJson.data.add("");
		}
		return gson.toJson(chatJson);
	}

	/**
	 * 聊天内容的json格式
	 * 
	 * @author ManGao
	 * 
	 */
	static class MsgJson {

		private String key;

		private List<Object> data = new ArrayList<>();

		public MsgJson() {
		}

		public String getKey() {
			return key;
		}

		public List<Object> getData() {
			return data;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public void setData(List<Object> data) {
			this.data = data;
		}
	}
}
