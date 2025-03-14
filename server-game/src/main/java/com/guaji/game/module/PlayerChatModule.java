package com.guaji.game.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.StringUtils;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;

import com.guaji.game.ServerData;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.ChatLuckCfg;
import com.guaji.game.config.ChatShieldCfg;
import com.guaji.game.config.FunctionUnlockCfg;
import com.guaji.game.config.GmPuidCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.ChatRecordEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.manager.ChatManager;
import com.guaji.game.manager.ChatMsg;
import com.guaji.game.module.activity.chatLuck.ChatLuckStatus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.protocol.Activity2.HPPushChatLuck;
import com.guaji.game.protocol.Chat.HPPushChat;
import com.guaji.game.protocol.Chat.HPSendChat;
import com.guaji.game.protocol.SysProtocol.HPErrorCode;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.PlayerUtil;

/**
 * 聊天模块
 */
public class PlayerChatModule extends PlayerModule {
	/**
	 * 上次发送聊天时间
	 */
	private long lastChatTime;
	/**
	 * 连续发送错误次数
	 */
	private int sbChatTimes = 0;
	/**
	 * 上次聊天信息
	 */
	private String lastChatMsg = "";
	// 模块Tick周期
	private int tickIndex;

	private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
	private Lock rLock = rwLock.readLock();
	private Lock wLock = rwLock.writeLock();
	/***
	 * 之前的聊天信息
	 */
	private Map<String, ChatRecordEntity> chatMap = new HashMap<String, ChatRecordEntity>();

	/**
	 * 构造函数
	 * 
	 * @param player
	 */
	public PlayerChatModule(Player player) {
		super(player);

		lastChatTime = 0;
		listenProto(HP.code.SEND_CHAT_C);
		listenProto(HP.code.SEND_LOGINCHAT_C);
	}

	@Override
	protected boolean onPlayerLogin() {
		// TODO 从数据库中将数据load出来
		loadChatRecord();
		return super.onPlayerLogin();
	}

	private void loadChatRecord() {
		wLock.lock();
		try {
			int playerId = player.getId();
			List<ChatRecordEntity> queryList = DBManager.getInstance().query("from ChatRecordEntity where playerID = ?",
					playerId);
			if (queryList == null) {
				return;
			}
			for (ChatRecordEntity entity : queryList) {
				String key = entity.getChatMsg();
				chatMap.put(key, entity);
			}
		} finally {
			wLock.unlock();
		}
	}

	/**
	 * 更新
	 * 
	 * @return
	 */
	@Override
	public boolean onTick() {
		// TODO 删除掉过期的数据
		deleteExprie();
		return super.onTick();
	}

	/**
	 * 删除过期数据
	 */
	private void deleteExprie() {
		if ((++tickIndex) % 100 != 0) {
			return;
		}
		tickIndex = 0;
		wLock.lock();
		try {
			// 每隔一秒删除一次
			long currentTime = GuaJiTime.getMillisecond();
			long interval = SysBasicCfg.getInstance().getChatSameMsgInterval();
			long deleteTime = currentTime - interval;
			List<Integer> removeIds = new ArrayList<Integer>();
			Set<String> keySet = chatMap.keySet();
			for (String key : keySet) {
				ChatRecordEntity entity = chatMap.get(key);
				if (entity.getCreateTime().getTime() < deleteTime) {
					removeIds.add(entity.getId());
				}
			}
			if (removeIds.isEmpty()) {
				return;
			}
//			DBManager.getInstance()
//					.executeUpdate("delete from chat_record where id in (" + StringUtils.join(removeIds, ",") + ")");
			for (Integer key : removeIds) {
				chatMap.remove(String.valueOf(key));
			}
		} catch (Exception e) {
			MyException.catchException(e);
		} finally {
			wLock.unlock();
		}
	}

	/**
	 * 消息响应
	 * 
	 * @param msg
	 * @return
	 */
	@Override
	public boolean onMessage(Msg msg) {
		return super.onMessage(msg);
	}

	/**
	 * 协议响应
	 * 
	 * @param protocol
	 * @return
	 */
	@Override
	public boolean onProtocol(Protocol protocol) {
		if (protocol.checkType(HP.code.SEND_CHAT_C)) {
			HPSendChat chat = protocol.parseProtocol(HPSendChat.getDefaultInstance());
			int type = chat.getChatType();
			switch (type) {
			case 0:// 世界聊天
			case 7:
				if (!FunctionUnlockCfg.checkUnlock(player,null,Const.FunctionType.chat_Unlock)){
					player.sendError(protocol.getType(), Status.error.CONDITION_NOT_ENOUGH);
					return true;
				}
				break;
			default:
				break;
			}

			onPlayerChat(protocol.parseProtocol(HPSendChat.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.SEND_LOGINCHAT_C)) {
			ChatManager.getInstance().sendPlayerLoginChat(player);
		}
		return super.onProtocol(protocol);
	}

	/**
	 * 检查聊天记录 如果在规定间隔时间内发重复的内容，则不可以继续聊天
	 * 
	 * @return
	 */
	private boolean checkChatRecord(String chatMsg) {
		long lastChatTime = 0L;
		long currTime = GuaJiTime.getMillisecond();
		long interval = SysBasicCfg.getInstance().getChatSameMsgInterval();
		rLock.lock();
		try {
			if (!chatMap.containsKey(chatMsg)) {
				return true;
			}
			ChatRecordEntity entity = chatMap.get(chatMsg);
			lastChatTime = entity.getCreateTime().getTime();
			return (currTime - lastChatTime) > interval;
		} finally {
			rLock.unlock();
		}
	}

	/**
	 * 检查聊天记录 如果在规定间隔时间内发重复的内容，则不可以继续聊天
	 * 
	 * @return
	 */
	private int getLastChatRecord(String chatMsg) {
		long lastChatTime = 0L;
		long currTime = GuaJiTime.getMillisecond();
		long interval = SysBasicCfg.getInstance().getChatSameMsgInterval();
		rLock.lock();
		try {
			if (!chatMap.containsKey(chatMsg)) {
				return 0;
			}
			ChatRecordEntity entity = chatMap.get(chatMsg);
			lastChatTime = entity.getCreateTime().getTime();
			long remainTime = interval - (currTime - lastChatTime);
			long remainSecond = remainTime / 1000;
			if (remainTime > 0 && remainSecond == 0) {
				remainSecond = 1;
			} else if (remainSecond < 0) {
				remainSecond = 0;
			}
			return (int) remainSecond;
		} finally {
			rLock.unlock();
		}
	}

	/**
	 * 新增聊天记录
	 * 
	 * @param chatMsg
	 */
	private void addNewRecord(String chatMsg) {
		wLock.lock();
		try {
			// 异步添加到数据库中
			if (chatMap.containsKey(chatMsg)) {
				return;
			}
			int playerId = player.getId();
			long currTime = GuaJiTime.getMillisecond();
			ChatRecordEntity entity = new ChatRecordEntity();
			entity.setPlayerId(playerId);
			entity.setChatMsg(chatMsg);
			entity.setCreateTime(new Date(currTime));
//			entity.notifyCreate();
			chatMap.put(chatMsg, entity);
		} finally {
			wLock.unlock();
		}
	}

	private String chatCDMsg(String front, int remainSec) {
		String msg = "";
		if ((remainSec / 3600) > 0) {
			// 精确到几小时。几分钟
			int hour = remainSec / 3600;
			int sec = remainSec % 3600;
			int minu = sec / 60;
			if (sec > 0 && minu == 0) {
				minu = 1;
			}
			if (minu == 0) {
				msg = front + hour + "時間後再度お試しください";
			} else {
				msg = front + hour + "時間" + minu + "分後再度お試しください";
			}
		} else if ((remainSec / 60) > 0) {
			int minu = remainSec / 60;
			int sec = remainSec % 60;
			if (sec > 0) {
				msg = front + minu + "分" + sec + "秒後再度お試しください";
			} else {
				msg = front + minu + "分後再度お試しください";
			}
		} else {
			msg = front + remainSec + "秒後再度お試しください";
		}
		return msg;
	}

	/**
	 * 检测聊天投递
	 * 
	 * @param chatMsg
	 * @return
	 */
	private boolean checkPostChat(String chatMsg, int chatType) {
		try {
			long currTime = GuaJiTime.getMillisecond();

			if (!checkChatRecord(chatMsg)) {
				HPErrorCode.Builder builder = HPErrorCode.newBuilder();
				builder.setHpCode(HP.code.SEND_CHAT_C_VALUE);
				builder.setErrCode(Status.error.CHAT_TO_FEST_VALUE);
				builder.setErrFlag(1);
				int remainSecond = getLastChatRecord(chatMsg);
				// builder.setErrMsg(chatCDMsg("発言が早すぎます。", remainSecond));
				//builder.setErrMsg("発言が早すぎます。少々お待ちください。");
				sendProtocol(Protocol.valueOf(HP.sys.ERROR_CODE, builder));
				return false;
			}
			addNewRecord(chatMsg);
			// gm 账号
			GmPuidCfg gmPuid = ConfigManager.getInstance().getConfigByKey(GmPuidCfg.class, player.getPuid());
			if (gmPuid != null) {
				return true;
			}

			// 禁言判断
			Date silentTime = player.getEntity().getSilentTime();
			if ((silentTime != null && silentTime.getTime() >= currTime) || chatMsg.equals(lastChatMsg)
					|| PlayerUtil.hasDirtyKey(chatMsg)) {
				// 让他自娱自乐
				HPPushChat.Builder msgBuilder = HPPushChat.newBuilder();
				ChatMsg msgObj = new ChatMsg();
				msgObj.setType(chatType);
				msgObj.setPlayerId(player.getId());
				msgObj.setName(player.getName());
				msgObj.setLevel(player.getLevel());
				msgObj.setRoleItemId(player.getPlayerData().getMainRole().getItemId());
				msgObj.setChatMsg(GameUtil.filterString(chatMsg));
				msgObj.setTitleId(player.getPlayerData().getTitleEntity().getUseId());
				msgObj.setSkinId(player.getPlayerData().loadChatSkinEntity().getCurSkinId());
				msgObj.setAvatarId(player.getPlayerData().getUsedAvatarId());
				msgBuilder.addChatMsg(msgObj.genBuilder());
				sendProtocol(Protocol.valueOf(HP.code.PUSH_CHAT_S, msgBuilder));
				return false;
			}

			if (ServerData.getInstance().isSilentPhone(player.getPuid())) {
				HPErrorCode.Builder builder = HPErrorCode.newBuilder();
				builder.setHpCode(HP.code.SEND_CHAT_C_VALUE);
				builder.setErrCode(Status.error.CHAT_QUIET_VALUE);
				builder.setErrFlag(1);
				//builder.setErrMsg("ただいま、発言禁止ペナルティ中。");
				sendProtocol(Protocol.valueOf(HP.sys.ERROR_CODE, builder));
				return false;
			}
			long chatElapse = SysBasicCfg.getInstance().getChatElapse();
			// 时间冷却
			if (currTime - lastChatTime <= chatElapse) {
				sbChatTimes++;
//				if (sbChatTimes >= 5) {
//					ServerData.getInstance().addSilentPhone(player.getPuid());
//					player.getSession().close(false);
//				}
				long remianTime = currTime - lastChatTime;
				int remainSecond = (int) ((chatElapse - remianTime) / 1000);
				if (remainSecond < 0) {
					return true;
				} else if (remainSecond <= 0 && remianTime > 0) {
					remainSecond = 1;
				}
				HPErrorCode.Builder builder = HPErrorCode.newBuilder();
				builder.setHpCode(HP.code.SEND_CHAT_C_VALUE);
				builder.setErrCode(Status.error.CHAT_TO_FEST_VALUE);
				builder.setErrFlag(1);
				// builder.setErrMsg(chatCDMsg("発言が早すぎます。", remainSecond));

				//builder.setErrMsg("発言が早すぎます。少々お待ちください。");
				sendProtocol(Protocol.valueOf(HP.sys.ERROR_CODE, builder));
				return false;
			}
			lastChatTime = currTime;// SysBasicCfg.getInstance().getChatElapse();
			sbChatTimes = 0;

			// 聊天文字长度
			if (SysBasicCfg.getInstance().getChatMsgMaxLen() < chatMsg.length()) {
				HPErrorCode.Builder builder = HPErrorCode.newBuilder();
				builder.setHpCode(HP.code.SEND_CHAT_C_VALUE);
				builder.setErrCode(Status.error.CHAT_MSG_MAX_LEN_VALUE);
				builder.setErrFlag(1);
				//builder.setErrMsg("聊天内容超出限制长度，限制长度为" + SysBasicCfg.getInstance().getChatMsgMaxLen() + "个字符");
				sendProtocol(Protocol.valueOf(HP.sys.ERROR_CODE, builder));
				return false;
			}
			return true;
		} catch (Exception e) {
			HPErrorCode.Builder builder = HPErrorCode.newBuilder();
			builder.setHpCode(HP.code.SEND_CHAT_C_VALUE);
			builder.setErrCode(Status.error.CHAT_SERVER_BUSY_VALUE);
			builder.setErrFlag(1);
			//builder.setErrMsg("ただいま大変混み合っております！");
			sendProtocol(Protocol.valueOf(HP.sys.ERROR_CODE, builder));
			return false;
		}
	}

	/**
	 * 玩家聊天处理
	 * 
	 * @param protocol
	 */
	private void onPlayerChat(HPSendChat protocol) {
		// 聊天合法&频率校验
		if (checkPostChat(protocol.getChatMsg(), protocol.getChatType())) {
			String chatMsg = protocol.getChatMsg().trim();

			ChatManager.getInstance().postChat(player, chatMsg, protocol.getChatType(), 0, protocol.getI18NTag());

			ActivityTimeCfg activityTimeCfg = ActivityUtil.getCurActivityTimeCfg(Const.ActivityId.CHAT_LUCK_VALUE);
			if (activityTimeCfg != null && !activityTimeCfg.isEnd()) {
				ChatLuckStatus chatLuckStatus = ActivityUtil.getActivityStatus(player.getPlayerData(),
						Const.ActivityId.CHAT_LUCK_VALUE, activityTimeCfg.getStageId(), ChatLuckStatus.class);
				if (chatLuckStatus == null) {
					return;
				}

				// 彩蛋活动开启
				ChatLuckCfg chatLuckCfg = ChatLuckCfg.triggerChatLuck(chatMsg);
				if (chatLuckCfg == null) {
					return;
				}

				// if (chatLuckStatus.isTriggered(chatLuckCfg.getKeyWords())) {
				// return;
				// }

				chatLuckStatus.addTriggeredKey(chatLuckCfg.getKeyWords());

				AwardItems awardItems = AwardItems.valueOf(chatLuckCfg.getReward());
				awardItems.rewardTakeAffectAndPush(player, Action.CHAT_LUCK, 1);

				player.getPlayerData().updateActivity(Const.ActivityId.CHAT_LUCK_VALUE, activityTimeCfg.getStageId());

				sendProtocol(Protocol.valueOf(HP.code.CHAT_LUCK_PUSH_S, HPPushChatLuck.newBuilder().setVersion(1)));
			}
		}
	}
}
