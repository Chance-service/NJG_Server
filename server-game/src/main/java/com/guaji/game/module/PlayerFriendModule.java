package com.guaji.game.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.guaji.annotation.MessageHandlerAnno;
import org.guaji.db.DBManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.GsConfig;
import com.guaji.game.ServerData;
import com.guaji.game.config.FBAccountBindRewardCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.FriendEntity;
import com.guaji.game.entity.MsgEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.manager.ArenaManager;
import com.guaji.game.manager.ChatManager;
import com.guaji.game.manager.IpAddrManager;
import com.guaji.game.manager.MailManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.module.quest.QuestEventBus;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.BuilderUtil;
import com.guaji.game.util.FriendUtil;
import com.guaji.game.util.GJLocal;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.util.RedisUtil.FBFriendBean;
import com.guaji.game.util.TapDBUtil.TapDBSource;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.FriendChatMsgType;
import com.guaji.game.protocol.Const.QuestEventType;
import com.guaji.game.protocol.Friend.FBFriendItem;
import com.guaji.game.protocol.Friend.FriendItem;
import com.guaji.game.protocol.Friend.FriendMsg;
import com.guaji.game.protocol.Friend.HPAgreeApplyFriend;
import com.guaji.game.protocol.Friend.HPApplyFriend;
import com.guaji.game.protocol.Friend.HPFBFriendBindMsg;
import com.guaji.game.protocol.Friend.HPFBFriendBindMsgRet;
import com.guaji.game.protocol.Friend.HPFindFriendReq;
import com.guaji.game.protocol.Friend.HPFriendAdd;
import com.guaji.game.protocol.Friend.HPFriendAddRet;
import com.guaji.game.protocol.Friend.HPFriendAskTicket;
import com.guaji.game.protocol.Friend.HPFriendAskTicketRet;
import com.guaji.game.protocol.Friend.HPFriendDel;
import com.guaji.game.protocol.Friend.HPFriendDelRet;
import com.guaji.game.protocol.Friend.HPFriendListFaceBook;
import com.guaji.game.protocol.Friend.HPFriendListFaceBookRet;
import com.guaji.game.protocol.Friend.HPFriendListKakao;
import com.guaji.game.protocol.Friend.HPFriendListKakaoRet;
import com.guaji.game.protocol.Friend.HPFriendListRet;
import com.guaji.game.protocol.Friend.HPGetFriendshipReq;
import com.guaji.game.protocol.Friend.HPGetFriendshipRes;
import com.guaji.game.protocol.Friend.HPGiftFriendshipReq;
import com.guaji.game.protocol.Friend.HPGiftFriendshipRes;
import com.guaji.game.protocol.Friend.HPMsgBoxInfo;
import com.guaji.game.protocol.Friend.HPMsgList;
import com.guaji.game.protocol.Friend.HPMsgListInfo;
import com.guaji.game.protocol.Friend.HPMsgPush;
import com.guaji.game.protocol.Friend.HPMsgShield;
import com.guaji.game.protocol.Friend.HPRefuseApplyFriend;
import com.guaji.game.protocol.Friend.HPSendMessage;
import com.guaji.game.protocol.Friend.HPShieldList;
import com.guaji.game.protocol.Friend.MsgBoxUnit;
import com.guaji.game.protocol.FriendRecommend.FriendRecommendItem;
import com.guaji.game.protocol.FriendRecommend.HPAddFriend;
import com.guaji.game.protocol.FriendRecommend.HPFriendRecommendRet;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Mail;
import com.guaji.game.protocol.Reward.RewardItem;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.protocol.Status;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 好友模块
 */
public class PlayerFriendModule extends PlayerModule {

	/**
	 * FaceBook message type init
	 */
	private static final int FACEBOOK_INIT = 0;

	/**
	 * FaceBook message type binding success
	 */
	private static final int FACEBOOK_SUCCESS = 1;

	public PlayerFriendModule(Player player) {
		super(player);
		// 好友
		listenProto(HP.code.FRIEND_LIST_C_VALUE);
		// listenProto(HP.code.FRIEND_ADD_C_VALUE);
		listenProto(HP.code.FRIEND_DELETE_C_VALUE);
		// 消息
		listenProto(HP.code.MESSAGE_BOX_INFO_C_VALUE);
		listenProto(HP.code.MESSAGE_LIST_C_VALUE);
		listenProto(HP.code.MESSAGE_SHIELD_C_VALUE);
		listenProto(HP.code.MESSAGE_CANCEL_SHIELD_C_VALUE);
		listenProto(HP.code.MESSAGE_SEND_C_VALUE);
		// 好友推荐
		listenProto(HP.code.RECOMMEND_FRIEND_LIST_C_VALUE);
		listenProto(HP.code.RECOMMEND_FRIEND_ADD_C_VALUE);
		// kekao 好友
		listenProto(HP.code.FRIEND_LIST_KAKAO_C);
		// facebook好友(tzy)
		if (GJLocal.isLocal(GJLocal.R2)) {
			listenProto(HP.code.FB_BIND_STATU_C_VALUE);// 账号绑定
			listenProto(HP.code.FRIEND_LIST_FACEBOOK_C_VALUE);// 获取好友列表信息
			listenProto(HP.code.FRIEND_ASK_TICKET_C_VALUE);// facebook 通知服务器索取
		}

		// 新增协议
		listenProto(HP.code.FRIEND_APPLY_LIST_C_VALUE);
		listenProto(HP.code.FRIEND_FIND_C_VALUE);
		listenProto(HP.code.FRIEND_APPLY_C_VALUE);
		listenProto(HP.code.FRIEND_AGREE_C_VALUE);
		listenProto(HP.code.FRIEND_REFUSE_C_VALUE);
		// 友誼點數相關
		listenProto(HP.code.FRIEND_POINT_GIFT_C_VALUE);
		listenProto(HP.code.FRIEND_POINT_GET_C_VALUE);

	}

	@Override
	protected boolean onPlayerLogin() {
		FriendEntity friendEntity = player.getPlayerData().loadFriendEntity();
		if (friendEntity == null) {
			player.getPlayerData().createFriendEntity();
		}

		pushShieldList();

		// 登录将Facebook信息放到redis上(tzy)
		if (GJLocal.isLocal(GJLocal.R2)) {
			pushFBFriendInfo();
		}

		// 登陆推送消息盒子
		onMsgBoxInfo();
		return super.onPlayerLogin();
	}

	public void pushShieldList() {
		// 推送屏蔽列表
		HPShieldList.Builder shieldListBuilder = HPShieldList.newBuilder();
		shieldListBuilder.addAllShieldPlayerId(player.getPlayerData().getFriendEntity().getAllShieldIds());
		player.sendProtocol(Protocol.valueOf(HP.code.FRIEND_SHIELD_LIST_S_VALUE, shieldListBuilder));
	}

	/**
	 * 将Facebook信息放到redis上(tzy)
	 */
	public void pushFBFriendInfo() {
		JedisPool jedisPool = GsApp.getInstance().getJedisPool();
		try (Jedis jedis = jedisPool.getResource()) {
			FBFriendBean bean = new FBFriendBean();
			bean.setId(player.getPuid());
			bean.setLevel(player.getLevel());
			bean.setVip(player.getVipLevel());
			bean.setLastServer(String.valueOf(GsConfig.getInstance().getServerId()));
			bean.setFightValue(PlayerUtil.calcAllFightValue(player.getPlayerData()));
			bean.setArenaRank(ArenaManager.getInstance().getRankByPlayerId(player.getId()));
			bean.putRedis(jedis);
		}
	}

	@Override
	protected boolean onPlayerAssemble() {
		return super.onPlayerAssemble();
	}

	@Override
	public boolean onProtocol(Protocol protocol) {
		if (protocol.checkType(HP.code.FRIEND_LIST_C_VALUE)) {
			// 查询所有好友信息
			onFetchFriendsInfo();
		} else if (protocol.checkType(HP.code.FRIEND_ADD_C_VALUE)) {
			HPFriendAdd params = protocol.parseProtocol(HPFriendAdd.getDefaultInstance());
			// 添加好友
			onFriendAdd(params.getTargetId());
		} else if (protocol.checkType(HP.code.FRIEND_DELETE_C_VALUE)) {
			// 删除好友
			onFriendDel(protocol.parseProtocol(HPFriendDel.getDefaultInstance()));
		} else if (protocol.checkType(HP.code.MESSAGE_BOX_INFO_C_VALUE)) {
			// 查询消息盒子
			onMsgBoxInfo();
		} else if (protocol.checkType(HP.code.MESSAGE_SEND_C_VALUE)) {
			HPSendMessage params = protocol.parseProtocol(HPSendMessage.getDefaultInstance());
			int targetId = params.getTargetId();
			if (targetId <= 0) {
				sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return true;
			}
			String msg = params.getMessage();
			// 发送消息
			onMsgSend(targetId, msg);
		} else if (protocol.checkType(HP.code.MESSAGE_SHIELD_C_VALUE)) {
			HPMsgShield params = protocol.parseProtocol(HPMsgShield.getDefaultInstance());
			int targetId = params.getPlayerId();
			if (targetId <= 0) {
				sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return true;
			}
			if (onMsgShieldAdd(targetId)) {
				sendProtocol(Protocol.valueOf(HP.code.MESSAGE_SHIELD_S_VALUE, params.toBuilder()));
			}
		} else if (protocol.checkType(HP.code.MESSAGE_CANCEL_SHIELD_C_VALUE)) {
			HPMsgShield params = protocol.parseProtocol(HPMsgShield.getDefaultInstance());
			int targetId = params.getPlayerId();
			if (targetId <= 0) {
				sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return true;
			}
			onMsgShieldCancel(targetId);
			sendProtocol(Protocol.valueOf(HP.code.MESSAGE_CANCEL_SHIELD_S_VALUE, params.toBuilder()));
		} else if (protocol.checkType(HP.code.MESSAGE_LIST_C_VALUE)) {
			HPMsgList params = protocol.parseProtocol(HPMsgList.getDefaultInstance());
			int targetId = params.getPlayerId();
			if (targetId <= 0) {
				sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
				return true;
			}
			onMsgList(targetId);
		} else if (protocol.checkType(HP.code.RECOMMEND_FRIEND_LIST_C_VALUE)) {
			onFriendRecommendList();
		} else if (protocol.checkType(HP.code.RECOMMEND_FRIEND_ADD_C_VALUE)) {
			HPAddFriend params = protocol.parseProtocol(HPAddFriend.getDefaultInstance());
			int targetId = params.getPlayerId();
			onFriendRecommendAdd(targetId);
		} else if (protocol.checkType(HP.code.FRIEND_LIST_KAKAO_C)) {
			// 获取kakao好友列表
			onFetchKaKaoFriendInfo(protocol.parseProtocol(HPFriendListKakao.getDefaultInstance()));
			return true;
		} else if (protocol.checkType(HP.code.FRIEND_LIST_FACEBOOK_C)) {
			// 获取Facebook好友列表信息(tzy)
			HPFriendListFaceBook params = protocol.parseProtocol(HPFriendListFaceBook.getDefaultInstance());
			List<String> friendList = params.getIdinfoList();
			onFacebookFriendInfo(friendList);
		} else if (protocol.checkType(HP.code.FRIEND_ASK_TICKET_C)) {
			// facebook 好友索取(tzy)
			HPFriendAskTicket params = protocol.parseProtocol(HPFriendAskTicket.getDefaultInstance());
			String friendId = params.getUid();
			onAskTicket(friendId);
		} else if (protocol.checkType(HP.code.FB_BIND_STATU_C_VALUE)) {
			HPFBFriendBindMsg bindMsg = protocol.parseProtocol(HPFBFriendBindMsg.getDefaultInstance());
			int msgtype = bindMsg.getMsgtype();
			onBindMsgRet(msgtype);

		} else if (protocol.checkType(HP.code.FRIEND_APPLY_LIST_C_VALUE)) {
			// 查询所有好友申请信息
			onFetchApplyFriendsInfo();

		} else if (protocol.checkType(HP.code.FRIEND_FIND_C_VALUE)) {
			// 查找好友
			onFindFriend(protocol.parseProtocol(HPFindFriendReq.getDefaultInstance()));

		} else if (protocol.checkType(HP.code.FRIEND_APPLY_C_VALUE)) {
			// 申请好友
			onApplyFriend(protocol.parseProtocol(HPApplyFriend.getDefaultInstance()));

		} else if (protocol.checkType(HP.code.FRIEND_AGREE_C_VALUE)) {
			// 同意好友申请
			onAgreeApplyFriend(protocol.parseProtocol(HPAgreeApplyFriend.getDefaultInstance()));

		} else if (protocol.checkType(HP.code.FRIEND_REFUSE_C_VALUE)) {
			// 同意好友申请
			onRefuseApplyFriend(protocol.parseProtocol(HPRefuseApplyFriend.getDefaultInstance()));

		} else if(protocol.checkType(HP.code.FRIEND_POINT_GET_C_VALUE)) {
			HPGetFriendshipReq params = protocol.parseProtocol(HPGetFriendshipReq.getDefaultInstance());
			//領取好友點數
			onGetFriendship(params.getFriendId());
		} else if  (protocol.checkType(HP.code.FRIEND_POINT_GIFT_C_VALUE)) {
			HPGiftFriendshipReq params = protocol.parseProtocol(HPGiftFriendshipReq.getDefaultInstance());
			// 贈送好友點數
			onGiftFriendship(params.getFriendId());
		}
		return super.onProtocol(protocol);
	}

	/**
	 * 获取kakao好友列表
	 * 
	 * @param protocol
	 */
	private void onFetchKaKaoFriendInfo(HPFriendListKakao protocol) {

		List<String> userList = protocol.getUserIdList();
		HPFriendListKakaoRet.Builder friendListBuilder = HPFriendListKakaoRet.newBuilder();

		ArrayList<Integer> kakaoPlayerList = new ArrayList<Integer>();
		for (String puid : userList) {

			int playerId = ServerData.getInstance().getPlayerIdByPuid(puid,
					player.getPlayerData().getPlayerEntity().getServerId());
			if (playerId != 0) {
				FriendItem.Builder friendItemBuilder = BuilderUtil.genFriendItemBuilder(playerId, puid);
				if (friendItemBuilder != null) {
					friendListBuilder.addFriendItem(friendItemBuilder);
					kakaoPlayerList.add(playerId);
				}
			}
		}

		FriendEntity friendEntity = player.getPlayerData().getFriendEntity();
		Set<Integer> friendIdSet = friendEntity.getFriendIdSet();
		for (int playerId : friendIdSet) {
			if (playerId > 0) {
				if (kakaoPlayerList.contains(playerId)) {
					continue;
				}
				FriendItem.Builder friendItemBuilder = BuilderUtil.genFriendItemBuilder(playerId, "");
				if (friendItemBuilder != null) {
					friendListBuilder.addFriendItem(friendItemBuilder);
				}
			}
		}
		// 把自己塞进去
		FriendItem.Builder friendItemBuilder = BuilderUtil.genFriendItemBuilder(player.getId(), "");
		if (friendItemBuilder != null) {
			if (!kakaoPlayerList.contains(player.getId())) {
				friendListBuilder.addFriendItem(friendItemBuilder);
			}
		}

		sendProtocol(Protocol.valueOf(HP.code.FRIEND_LIST_KAKAO_S, friendListBuilder));
	}

	/**
	 * 获取facebook好友信息(tzy)
	 * 
	 * @param friendList
	 */
	private void onFacebookFriendInfo(List<String> friendList) {
		HPFriendListFaceBookRet.Builder friendListBuilder = HPFriendListFaceBookRet.newBuilder();
		// listd第一个放自己的信息
		FBFriendItem.Builder myFriendItemBuilder = BuilderUtil.genFBFriendItemBuilder(player.getPuid());
		friendListBuilder.addFriendItem(myFriendItemBuilder);

		for (String puid : friendList) {
			String[] ids = puid.split("\\$");
			if (ids.length >= 2) {
				FBFriendItem.Builder friendItemBuilder = BuilderUtil.genFBFriendItemBuilder(ids[0]);
				if (friendItemBuilder != null) {
					friendListBuilder.addFriendItem(friendItemBuilder);
				}
			}
		}

		// 数据库读取已索取好友列表
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		List<String> askTicketList = stateEntity.getAskTickIdList();
		friendListBuilder.addAllAskTicketList(askTicketList);
		sendProtocol(Protocol.valueOf(HP.code.FRIEND_LIST_FACEBOOK_S_VALUE, friendListBuilder));
	}

	/**
	 * facebook好友索取(tzy)
	 * 
	 * @param friendId
	 */
	private void onAskTicket(String friendId) {

		JedisPool jedisPool = GsApp.getInstance().getJedisPool();
		try (Jedis jedis = jedisPool.getResource()) {
			if (!jedis.exists(friendId)) {
				// 没有该好友id
				sendError(HP.code.FRIEND_ASK_TICKET_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
				return;
			}
		}

		// 数据库读取
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		List<String> askTicketList = stateEntity.getAskTickIdList();
		if (askTicketList.contains(friendId)) {
			// 已经索取过
			sendError(HP.code.FRIEND_ASK_TICKET_C_VALUE, Status.error.ALREADY_ASKTICKET);
			return;
		}

		if (askTicketList.size() >= SysBasicCfg.getInstance().getAskTicketCount()) {
			// 索取次数不足
			sendError(HP.code.FRIEND_ASK_TICKET_C_VALUE, Status.error.ASKTICKET_COUNT_LIMIT);
			return;
		}

		// 提交数据库
		stateEntity.addAskTickId(friendId);
		stateEntity.notifyUpdate(true);

		if (askTicketList.size() == SysBasicCfg.getInstance().getAskTicketCount()) {
			// 索取次数达到后发奖
			AwardItems awardItems = AwardItems.valueOf(SysBasicCfg.getInstance().getAskTickAwards());
			awardItems.rewardTakeAffectAndPush(player, Action.FRIEND_ASKTICK, 1);
		}
		sendProtocol(Protocol.valueOf(HP.code.FRIEND_ASK_TICKET_S_VALUE,
				HPFriendAskTicketRet.newBuilder().setCount(askTicketList.size())));
	}

	/**
	 * facebook
	 * 
	 * @param msgtype
	 */
	private void onBindMsgRet(int msgtype) {

		HPFBFriendBindMsgRet.Builder builder = HPFBFriendBindMsgRet.newBuilder();

		FriendEntity friendEntity = player.getPlayerData().getFriendEntity();
		if (FACEBOOK_INIT == msgtype) {
			builder.setStatus(friendEntity.getBindingState());
			for (RewardItem.Builder item : FBAccountBindRewardCfg.rewardItems) {
				builder.addItem(item);
			}
			sendProtocol(Protocol.valueOf(HP.code.FB_BIND_STATU_S_VALUE, builder));
		} else if (FACEBOOK_SUCCESS == msgtype) {
			if (0 == friendEntity.getBindingState()) {
				friendEntity.setBindingState((byte) 1);
				MailManager.createMail(player.getPlayerData().getId(), Mail.MailType.Reward_VALUE,
						GsConst.MailId.FACE_BOOK_BINDING, GuaJiTime.getCalendar().getTime(), "FaceBook绑定奖励",
						FBAccountBindRewardCfg.awardItems, GsConst.EmailClassification.COMMON, "");
				friendEntity.notifyUpdate(true);
			}
		}
	}

	private void onFriendRecommendAdd(int targetId) {
		onFriendAdd(targetId);
		sendProtocol(Protocol.valueOf(HP.code.RECOMMEND_FRIEND_ADD_S_VALUE, genRecommend()));
	}

	private HPFriendRecommendRet.Builder genRecommend() {
		FriendEntity friendEntity = player.getPlayerData().getFriendEntity();
		Set<Integer> excludePlayerIdSet = new HashSet<>();
		excludePlayerIdSet.addAll(friendEntity.getFriendIdSet());
		excludePlayerIdSet.add(player.getId());
		
		Collection<Integer> playerIds = FriendUtil.randomFriend(excludePlayerIdSet);

		HPFriendRecommendRet.Builder recommendBuilder = HPFriendRecommendRet.newBuilder();
		for (int playerId : playerIds) {
			FriendRecommendItem.Builder friendRecommendItemBuilder = FriendRecommendItem.newBuilder();
			PlayerSnapshotInfo.Builder snapShot = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
			if (snapShot != null) {
				friendRecommendItemBuilder.setPlayerId(playerId);
				friendRecommendItemBuilder.setRoleId(snapShot.getMainRoleInfo().getItemId());
				friendRecommendItemBuilder.setName(snapShot.getMainRoleInfo().getName());
				friendRecommendItemBuilder.setLevel(snapShot.getMainRoleInfo().getLevel());
				friendRecommendItemBuilder.setFightValue(snapShot.getMainRoleInfo().getMarsterFight());
				friendRecommendItemBuilder.setTitleId(snapShot.getTitleInfo().getTitleId());
				friendRecommendItemBuilder.setRebirthStage(snapShot.getMainRoleInfo().getRebirthStage());
				friendRecommendItemBuilder.setHeadIcon(snapShot.getPlayerInfo().getHeadIcon());
				recommendBuilder.addFriendRecommendItem(friendRecommendItemBuilder);
			}
		}
		recommendBuilder.setVersion(1);
		return recommendBuilder;
	}

	private void onFriendRecommendList() {
		sendProtocol(Protocol.valueOf(HP.code.RECOMMEND_FRIEND_LIST_S_VALUE, genRecommend()));
	}

	/**
	 * 获取历史消息
	 * 
	 * @param targetId
	 */
	private void onMsgList(int targetId) {
		List<MsgEntity> msgList = new LinkedList<>();
		HPMsgListInfo.Builder msgListInfoBuilder = HPMsgListInfo.newBuilder();
		Map<Integer, TreeSet<MsgEntity>> msgs = player.getPlayerData().getPlayerMsgs();
		TreeSet<MsgEntity> unreadMsgs = msgs.get(targetId);
		if (unreadMsgs != null) {

			for (MsgEntity msgEntity : unreadMsgs) {
				if (msgEntity.getMsgType() == Const.FriendChatMsgType.SYSTEM_MSG_VALUE
						|| msgEntity.getMsgType() == Const.FriendChatMsgType.PLAYER_MSG_VALUE) {
					if (msgEntity.getRecverId() == player.getId() && msgEntity.getSenderId() == targetId) {
						msgList.add(msgEntity);
						FriendMsg.Builder friendMsgbuBuilder = FriendMsg.newBuilder();
						friendMsgbuBuilder.setMsgType(Const.FriendChatMsgType.valueOf(msgEntity.getMsgType()));
						friendMsgbuBuilder.setSenderId(msgEntity.getSenderId());
						PlayerSnapshotInfo.Builder sender = SnapShotManager.getInstance()
								.getPlayerSnapShot(msgEntity.getSenderId());
						friendMsgbuBuilder.setSenderName(sender.getMainRoleInfo().getName());
						friendMsgbuBuilder.setReceiveId(msgEntity.getRecverId());
						friendMsgbuBuilder.setReceiveName(player.getName());
						friendMsgbuBuilder.setMessage(msgEntity.getContent());
						friendMsgbuBuilder.setJsonType(msgEntity.getJsonType());
						friendMsgbuBuilder.setMsTime(msgEntity.getCreateTime().getTime() / 1000);
						friendMsgbuBuilder.setHeadIcon(sender.getPlayerInfo().getHeadIcon());
						friendMsgbuBuilder.setSenderIdentify("");
						if (player.getPlayerData().getStateEntity().isShowArea()
								&& player.getPlayerData().getIpAddrEntity() != null) {
							friendMsgbuBuilder.setArea(player.getPlayerData().getIpAddrEntity().getPosition());
						}
						friendMsgbuBuilder.setSkinId(msgEntity.getSenderSkinId());
						msgListInfoBuilder.addFriendMsgs(friendMsgbuBuilder);

					}
				}
			}

			player.getPlayerData().removeMsgs(msgList);
		}

		sendProtocol(Protocol.valueOf(HP.code.MESSAGE_LIST_S_VALUE, msgListInfoBuilder));
	}

	/**
	 * 处理玩家发言屏蔽
	 */
	private boolean onMsgShieldAdd(int targetId) {
		if (player.getPlayerData().getFriendEntity().getShieldSize() >= SysBasicCfg.getInstance().getMaxShieldSize()) {
			sendError(HP.code.MESSAGE_SHIELD_C_VALUE, Status.error.SHIELD_LIST_FULL);
			return false;
		}
		player.getPlayerData().getFriendEntity().setShield(targetId);
		player.getPlayerData().getFriendEntity().notifyUpdate(true);

		pushShieldList();

		return true;
	}

	/**
	 * 处理玩家发言取消屏蔽
	 */
	private void onMsgShieldCancel(int targetId) {
		player.getPlayerData().getFriendEntity().cancelShield(targetId);
		player.getPlayerData().getFriendEntity().notifyUpdate(true);

		pushShieldList();
	}

	/**
	 * 
	 */
	@MessageHandlerAnno(code = GsConst.MsgType.CHANGE_NAME)
	private void onChangeNameNotice(Msg msg) {
		String changeNameMsg = msg.getParam(0);
		FriendEntity friendEntity = player.getPlayerData().getFriendEntity();
		if (friendEntity != null) {
			for (int targetId : friendEntity.getFriendIdSet()) {
				onMsgSend(targetId, changeNameMsg, 1);
			}
		}
	}

	/**
	 * 消息发送
	 * 
	 * @param targetId
	 * @param msg
	 */
	private void onMsgSend(int targetId, String msg) {
		onMsgSend(targetId, msg, 0);
	}

	/**
	 * 消息发送
	 * 
	 * @param targetId
	 * @param msg
	 */
	private void onMsgSend(int targetId, String msg, int jsonType) {
		FriendEntity friendEntity = player.getPlayerData().getFriendEntity();
		if (!friendEntity.isDailySendMsgAllow(targetId)) {
			sendError(HP.code.MESSAGE_SEND_C_VALUE, Status.error.DAILY_SEND_PLAYER_COUNT_LIMIT);
			return;
		}
		if (friendEntity.isShield(targetId)) {
			// 自动解除屏蔽
			friendEntity.cancelShield(targetId);
			friendEntity.notifyUpdate(true);

			pushShieldList();
		}
		friendEntity.addMsgPlayerId(targetId);
		friendEntity.notifyUpdate(true);
		// 发送消息
		sendMessage(targetId, FriendChatMsgType.PLAYER_MSG_VALUE, msg, jsonType);
	}

	/**
	 * 读取消息盒子，就是未读取的消息
	 */
	private void onMsgBoxInfo() {
		Set<Integer> playerIdSet = new HashSet<>();
		Map<Integer, TreeSet<MsgEntity>> msgs = player.getPlayerData().getPlayerMsgs();
		for (Map.Entry<Integer, TreeSet<MsgEntity>> entry : msgs.entrySet()) {
			for (MsgEntity msgEntity : entry.getValue()) {
				if (msgEntity.getMsgType() == Const.FriendChatMsgType.SYSTEM_MSG_VALUE
						|| msgEntity.getMsgType() == Const.FriendChatMsgType.PLAYER_MSG_VALUE) {
					if (msgEntity.getRecverId() == player.getId()) {
						playerIdSet.add(msgEntity.getSenderId());
					}
				}
			}
		}
		HPMsgBoxInfo.Builder builder = HPMsgBoxInfo.newBuilder();
		for (Integer senderId : playerIdSet) {
			PlayerSnapshotInfo.Builder snapShotBuilder = SnapShotManager.getInstance().getPlayerSnapShot(senderId);
			if (snapShotBuilder != null) {
				MsgBoxUnit.Builder msgBuilder = MsgBoxUnit.newBuilder();
				msgBuilder.setPlayerId(senderId);
				msgBuilder.setName(snapShotBuilder.getMainRoleInfo().getName());
				msgBuilder.setLevel(snapShotBuilder.getMainRoleInfo().getLevel());
				msgBuilder.setRebirthStage(snapShotBuilder.getMainRoleInfo().getRebirthStage());
				msgBuilder.setRoleItemId(snapShotBuilder.getMainRoleInfo().getItemId());
				msgBuilder.setAvatarId(snapShotBuilder.getMainRoleInfo().getAvatarId());
				msgBuilder.setSenderIdentify("");
				msgBuilder.setHeadIcon(msgBuilder.getHeadIcon());

				builder.addMsgBoxUnits(msgBuilder);
			}
		}

		if (builder.getMsgBoxUnitsCount() > 0) {
			sendProtocol(Protocol.valueOf(HP.code.MESSAGE_BOX_INFO_S_VALUE, builder));
		}
		return;
	}

	/**
	 * 删除好友
	 */
	private void onFriendDel(HPFriendDel req) {
		List<Integer> delAry =  req.getTargetIdList();
		HPFriendDelRet.Builder myBuilder = HPFriendDelRet.newBuilder();
		for (Integer targetId : delAry) {
			if (targetId <= 0) {
				//sendError(HP.code.FRIEND_DELETE_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
				continue;
			}
			// 我的FriendEntity
			FriendEntity myEntity = player.getPlayerData().getFriendEntity();
			myEntity.removeFriend(targetId);
			myEntity.removePoint(targetId);
			myEntity.notifyUpdate();
			
			// add proto
			myBuilder.addTargetId(targetId);
			
	
			// 对方FriendEntity
			FriendEntity targetEntity = null;
			Player targetPlayer = PlayerUtil.queryPlayer(targetId);
			if (targetPlayer != null) {
				targetEntity = targetPlayer.getPlayerData().getFriendEntity();
			} else {
				targetEntity = DBManager.getInstance().fetch(FriendEntity.class,
						"from FriendEntity where playerId = ? and invalid = 0", targetId);
				if (targetEntity != null) {
					targetEntity.convert();
				}
			}
			if (targetEntity == null) {
				//sendError(HP.code.FRIEND_APPLY_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
				continue;
			}
			targetEntity.removeFriend(player.getId());
			targetEntity.removePoint(player.getId());
			targetEntity.notifyUpdate();
			// 对方在线，则推送
			if (targetPlayer != null && targetPlayer.isOnline()) {
				HPFriendDelRet.Builder targetBuilder = HPFriendDelRet.newBuilder();
				targetBuilder.addTargetId(player.getId());
				targetPlayer.sendProtocol(Protocol.valueOf(HP.code.FRIEND_DELETE_S_VALUE, targetBuilder));
			}
		}
		sendProtocol(Protocol.valueOf(HP.code.FRIEND_DELETE_S_VALUE, myBuilder));
	}

	/*
	 * 查询好友信息
	 */
	private void onFetchFriendsInfo() {
		FriendEntity entity = player.getPlayerData().getFriendEntity();
		HPFriendListRet.Builder friendListBuilder = HPFriendListRet.newBuilder();
		friendListBuilder.setFlag(0);
		List<FriendItem.Builder> friends = BuilderUtil.genFriendItemBuilders(entity.getFriendIdSet(), false);
		int point = 0;
		if (friends != null) {
			for (FriendItem.Builder aItem :friends) {
				point = entity.getGiftPoint(aItem.getPlayerId());
				aItem.setHaveGift(point > 0);
				aItem.setCanGift(!entity.isAlreadyGift(aItem.getPlayerId()));
				friendListBuilder.addFriendItem(aItem);
			}
			//friendListBuilder.addAllFriendItem(friends);
		}
		sendProtocol(Protocol.valueOf(HP.code.FRIEND_LIST_S_VALUE, friendListBuilder));
	}

	/**
	 * 添加好友
	 * 
	 * @param targetId
	 */
	private void onFriendAdd(int targetId) {
		if (targetId <= 0 || targetId == player.getId()) {
			sendError(HP.code.FRIEND_ADD_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		if (SnapShotManager.getInstance().getPlayerSnapShot(targetId) == null) {
			sendError(HP.code.FRIEND_ADD_C_VALUE, Status.error.PLAYER_NOT_FOUND);
			return;
		}
		FriendEntity friendEntity = player.getPlayerData().getFriendEntity();
		if (friendEntity.getFriendCount() >= SysBasicCfg.getInstance().getFriendCountLimit()) {
			sendError(HP.code.FRIEND_ADD_C_VALUE, Status.error.FRIEND_COUNT_FULL);
			return;
		}

		friendEntity.addFriendId(targetId);
		friendEntity.notifyUpdate(true);
		// 推送添加好友任务
		QuestEventBus.fireQuestEventOneTime(QuestEventType.ADD_FRIEND, player.getXid());
		sendProtocol(Protocol.valueOf(HP.code.FRIEND_ADD_S_VALUE, HPFriendAddRet.newBuilder().setTargetId(targetId)));
		// 自动发送系统消息提示
		sendMessage(targetId, Const.FriendChatMsgType.SYSTEM_MSG_VALUE,
				ChatManager.getMsgJson(SysBasicCfg.getInstance().getAddFriendNotice()), 1);
	}
	
	/**
	 * 領取友情點數
	 * 
	 * @param targetId
	 */
	private void onGetFriendship(int targetId) {
		if (targetId < 0 || targetId == player.getId()) {
			sendError(HP.code.FRIEND_POINT_GET_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		FriendEntity friendEntity = player.getPlayerData().getFriendEntity();
		
		if (friendEntity == null) {
			sendError(HP.code.FRIEND_POINT_GET_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		int point = 0;
		if (targetId != 0) {
			if (SnapShotManager.getInstance().getPlayerSnapShot(targetId) == null) {
				sendError(HP.code.FRIEND_POINT_GET_C_VALUE, Status.error.PLAYER_NOT_FOUND);
				return;
			}
			
			if (!friendEntity.contains(targetId)) {
				sendError(HP.code.FRIEND_POINT_GET_C_VALUE, Status.error.NOT_FRIEND);
				return;
			}
			
			point = friendEntity.getGiftPoint(targetId);
			
			if (point <= 0) {
				sendError(HP.code.FRIEND_POINT_GET_C_VALUE, Status.error.ASKTICKET_COUNT_LIMIT);
				return;
			}
			
			friendEntity.removePoint(targetId);
		} else { // 領所有玩家贈送友情點
			Map<Integer,Integer> pointMap = friendEntity.getGiftPoint();
			if (pointMap.size() == 0) {
				sendError(HP.code.FRIEND_POINT_GET_C_VALUE, Status.error.ASKTICKET_COUNT_LIMIT);
				return;
			}
			int playerId = 0;
			point = 0;
			for (Map.Entry<Integer,Integer> entry:pointMap.entrySet()) {
				playerId = entry.getKey();
				if (friendEntity.contains(playerId)) {
					point = point + entry.getValue();
				}
			}
			friendEntity.clearPoint();
		}
		
		int oldpoint = player.getPlayerData().getStateEntity().getFriendship();
		
		String reward = String.format("10000_1025_%d",point);
		
		AwardItems awards = AwardItems.valueOf(reward);
		awards.rewardTakeAffectAndPush(player, Action.GOT_FRIENDSHIP, 0,TapDBSource.Friend,Params.valueOf("oldpoint", oldpoint),
				Params.valueOf("targetId", targetId), 
		        Params.valueOf("getpoint", point));
				
		player.getPlayerData().syncStateInfo();
		
		friendEntity.notifyUpdate(true);
				
		// BI 日志 ()
		BehaviorLogger.log4Platform(player, Action.GOT_FRIENDSHIP, Params.valueOf("oldpoint", oldpoint),
				Params.valueOf("targetId", targetId), 
		        Params.valueOf("getpoint", point), 
		        Params.valueOf("newPoint", player.getPlayerData().getStateEntity().getFriendship()));
		
		
		
		// 推送添加好友任务
		//QuestEventBus.fireQuestEventOneTime(QuestEventType.ADD_FRIEND, player.getXid());
		HPGetFriendshipRes.Builder builder = HPGetFriendshipRes.newBuilder();
		builder.setFriendId(targetId);
		builder.setPoint(point);

		sendProtocol(Protocol.valueOf(HP.code.FRIEND_POINT_GET_S_VALUE,builder));
	}
	
	/**
	 * 贈送友情點數
	 * 
	 * @param targetId
	 */
	private void onGiftFriendship(int friendId) {
		
		if (friendId < 0 || friendId == player.getId()) {
			sendError(HP.code.FRIEND_POINT_GIFT_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		
		if (friendId != 0) {
			int targetId = friendId;
			if (SnapShotManager.getInstance().getPlayerSnapShot(targetId) == null) {
				sendError(HP.code.FRIEND_POINT_GIFT_C_VALUE, Status.error.PLAYER_NOT_FOUND);
				return;
			}
			FriendEntity friendEntity = player.getPlayerData().getFriendEntity();
			if (!friendEntity.contains(targetId)) {
				sendError(HP.code.FRIEND_POINT_GIFT_C_VALUE, Status.error.NOT_FRIEND);
				return;
			}
			
			if (friendEntity.isAlreadyGift(targetId)) {
				sendError(HP.code.FRIEND_POINT_GIFT_C_VALUE, Status.error.ALREADY_GIFT);
				return;
			}
			// 加入已送禮名單
			friendEntity.addGiftPlayerId(targetId);
			friendEntity.notifyUpdate(true);
			FriendEntity targetEntity = null;
			Player targetplayer = PlayerUtil.queryPlayer(targetId);
			if (targetplayer != null && targetplayer.isOnline()) {
				targetEntity = targetplayer.getPlayerData().getFriendEntity();
				targetEntity.addPoint(player.getId(), SysBasicCfg.getInstance().getFriendship());
				targetEntity.notifyUpdate(true);
				
			} else {
				targetEntity = DBManager.getInstance().fetch(FriendEntity.class,
						"from FriendEntity where playerId = ? and invalid = 0", targetId);
				if (targetEntity == null) {
					sendError(HP.code.FRIEND_POINT_GIFT_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
					return;
				} else {
					targetEntity.convert();
				}
				targetEntity.addPoint(player.getId(), SysBasicCfg.getInstance().getFriendship());
				targetEntity.notifyUpdate(true);
			}
			
			Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.GIVE_FIRENDSHIP,
					GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
			hawkMsg.pushParam(1);
			GsApp.getInstance().postMsg(hawkMsg);
			
//			Msg gMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.GLORY_HOLE_GIVE_FIRENDSHIP,
//					GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
//			gMsg.pushParam(1);
//			GsApp.getInstance().postMsg(gMsg);
			
			
			HPGiftFriendshipRes.Builder builder = HPGiftFriendshipRes.newBuilder();
			builder.setFriendId(targetId);
			sendProtocol(Protocol.valueOf(HP.code.FRIEND_POINT_GIFT_S_VALUE,builder));
		} else {  // 送全部好友
			FriendEntity friendEntity = player.getPlayerData().getFriendEntity();
			Set<Integer> fSet = friendEntity.getFriendIdSet();
			int count = 0;
			for (Integer targetId : fSet ) {
				
				if (SnapShotManager.getInstance().getPlayerSnapShot(targetId) == null) {	
					continue;
				}
				if (friendEntity.isAlreadyGift(targetId)) {
					continue;
				}
				// 加入已送禮名單
				friendEntity.addGiftPlayerId(targetId);
				
				FriendEntity targetEntity = null;
				Player targetplayer = PlayerUtil.queryPlayer(targetId);
				if (targetplayer != null && targetplayer.isOnline()) {
					targetEntity = targetplayer.getPlayerData().getFriendEntity();
					targetEntity.addPoint(player.getId(), SysBasicCfg.getInstance().getFriendship());
					targetEntity.notifyUpdate(true);
					count++;
				} else {
					targetEntity = DBManager.getInstance().fetch(FriendEntity.class,
							"from FriendEntity where playerId = ? and invalid = 0", targetId);
					if (targetEntity != null) {
						targetEntity.convert();
						targetEntity.addPoint(player.getId(), SysBasicCfg.getInstance().getFriendship());
						targetEntity.notifyUpdate(true);
						count++;
					}
				}
			}
			
			Msg hawkMsg = Msg.valueOf(GsConst.MsgType.DailyQuestMsg.GIVE_FIRENDSHIP,
					GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
			hawkMsg.pushParam(count);
			GsApp.getInstance().postMsg(hawkMsg);
			
			HPGiftFriendshipRes.Builder builder = HPGiftFriendshipRes.newBuilder();
			builder.setFriendId(0);
			sendProtocol(Protocol.valueOf(HP.code.FRIEND_POINT_GIFT_S_VALUE,builder));
			friendEntity.notifyUpdate(true);
		}
	}

	public void sendMessage(int targetId, int msgType, String msg) {

		sendMessage(targetId, msgType, msg, 0);
	}

	/**
	 * 发送消息
	 * 
	 * @param targetId
	 * @param msgType
	 */
	public void sendMessage(int targetId, int msgType, String msg, int jsonType) {
		// 对方玩家在线
		if (ServerData.getInstance().isPlayerOnline(targetId)) {
			// 判断是否屏蔽
			Player target = PlayerUtil.queryPlayer(targetId);
			if (target != null) {
				// 判断是否被对方屏蔽
				if (target.getPlayerData().getFriendEntity().isShield(player.getId())) {
					sendError(HP.code.MESSAGE_SEND_C_VALUE, Status.error.TARGET_SHIELD);
					return;
				}
				// 直接发送
				FriendMsg.Builder friendMsgbuBuilder = FriendMsg.newBuilder();

				if (msgType == FriendChatMsgType.SYSTEM_MSG_VALUE) {
					friendMsgbuBuilder.setMsgType(FriendChatMsgType.SYSTEM_MSG);
				} else {
					friendMsgbuBuilder.setMsgType(FriendChatMsgType.PLAYER_MSG);
				}

				friendMsgbuBuilder.setSenderId(player.getId());
				friendMsgbuBuilder.setSenderName(player.getName());
				friendMsgbuBuilder.setReceiveId(targetId);
				friendMsgbuBuilder.setReceiveName(player.getName());
				friendMsgbuBuilder.setMessage(msg);
				friendMsgbuBuilder.setJsonType(jsonType);
				friendMsgbuBuilder.setTitleId(player.getPlayerData().getTitleEntity().getUseId());
				if (player.getPlayerData().getStateEntity().isShowArea()
						&& player.getPlayerData().getIpAddrEntity() != null) {
					friendMsgbuBuilder.setArea(player.getPlayerData().getIpAddrEntity().getPosition());
				}
				friendMsgbuBuilder.setMsTime(GuaJiTime.getMillisecond() / 1000);
				friendMsgbuBuilder.setSkinId(player.getPlayerData().getChatSkinEntity().getCurSkinId());

				MsgBoxUnit.Builder msgBoxBuilder = MsgBoxUnit.newBuilder();
				msgBoxBuilder.setLevel(player.getLevel());
				msgBoxBuilder.setName(player.getName());
				msgBoxBuilder.setPlayerId(player.getId());
				msgBoxBuilder.setRoleItemId(player.getPlayerData().getMainRole().getItemId());
				msgBoxBuilder.setAvatarId(player.getPlayerData().getUsedAvatarId());
				msgBoxBuilder.setSenderIdentify("");
				msgBoxBuilder.setHeadIcon(player.getPlayerData().getPlayerEntity().getHeadIcon());
				target.sendProtocol(Protocol.valueOf(HP.code.MESSAGE_PUSH_S_VALUE,
						HPMsgPush.newBuilder().setFriendMsg(friendMsgbuBuilder).setMsgBoxUnit(msgBoxBuilder)));
			}
		} else {
			Player target = PlayerUtil.queryPlayer(targetId);
			boolean isShield = false;
			boolean isMemoryExist = false;
			// 如果对方内存还在的话,添加到内存
			if (target != null) {
				isMemoryExist = true;
				// 判断是否被对方屏蔽
				if (target.getPlayerData().getFriendEntity().isShield(player.getId())) {
					isShield = true;
				}
			} else {
				// 内存不存在的捞取 Friend对象
				FriendEntity friendEntity = DBManager.getInstance().fetch(FriendEntity.class,
						"from FriendEntity where playerId = ? and invalid = 0", targetId);
				if (friendEntity != null) {
					friendEntity.convert();
					if (friendEntity.isShield(player.getId())) {
						isShield = true;
					}
				}
			}

			if (isShield) {
				sendError(HP.code.MESSAGE_SEND_C_VALUE, Status.error.TARGET_SHIELD);
				return;
			}

			// 创建数据库对象
			MsgEntity msgEntity = new MsgEntity();
			msgEntity.setCreateSysTime(GuaJiTime.getSeconds());
			msgEntity.setContent(msg);
			msgEntity.setModuleId(GsConst.ModuleType.FRIEND_MODULE);
			msgEntity.setMsgType(msgType);
			msgEntity.setSenderId(player.getId());
			msgEntity.setRecverId(targetId);
			msgEntity.setJsonType(jsonType);
			msgEntity.setSenderSkinId(player.getPlayerData().loadChatSkinEntity().getCurSkinId());

			DBManager.getInstance().create(msgEntity);

			if (isMemoryExist) {
				target.getPlayerData().addOneToMeMsg(msgEntity);
			}
		}
	}

	/**
	 * 查询申请好友信息
	 */
	private void onFetchApplyFriendsInfo() {
		FriendEntity entity = player.getPlayerData().getFriendEntity();
		HPFriendListRet.Builder friendListBuilder = HPFriendListRet.newBuilder();
		friendListBuilder.setFlag(0);
		
		List<FriendItem.Builder> friends = BuilderUtil.genFriendItemBuilders(entity.getApplyFriendIdSet(), false);
		if (friends != null) {
			for (FriendItem.Builder builder : friends) {
				friendListBuilder.addFriendItem(builder);
			}
		}
//		if (friends != null) {
//			friendListBuilder.addAllFriendItem(friends);
//		}
		sendProtocol(Protocol.valueOf(HP.code.FRIEND_APPLY_LIST_S_VALUE, friendListBuilder));
	}

	/**
	 * 查找好友
	 */
	private void onFindFriend(HPFindFriendReq req) {

		FriendItem.Builder friend = null;
		if (req.hasPlayerName()) {

			String playerName = req.getPlayerName();
			if (playerName.equals("")) {
				sendError(HP.code.FRIEND_FIND_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
				return;
			}
			friend = BuilderUtil.genSingleFriendItemByName(playerName);

		} else {
			int targetId = req.getPlayerId();
			if (targetId <= 0) {
				sendError(HP.code.FRIEND_FIND_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
				return;
			}
			friend = BuilderUtil.genSingleFriendItem(targetId);
		}

		if (friend == null) {
			sendError(HP.code.FRIEND_FIND_C_VALUE, Status.error.PLAYER_NOT_FOUND);
			return;
		}
		sendProtocol(Protocol.valueOf(HP.code.FRIEND_FIND_S_VALUE, friend));
	}

	/**
	 * 申请好友
	 */
	private void onApplyFriend(HPApplyFriend req) {
		int targetId = req.getPlayerId();
		if (targetId <= 0 || targetId == player.getId()) {
			sendError(HP.code.FRIEND_APPLY_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}

		// 自己的FriendEntity
		FriendEntity myEntity = player.getPlayerData().getFriendEntity();
		// 已经是好友
		if (myEntity.isFriend(targetId)) {
			sendError(HP.code.FRIEND_APPLY_C_VALUE, Status.error.ALREADY_FRIEND_VALUE);
			return;
		}
		// 自己的好友数目已达上限
		if (SysBasicCfg.getInstance().getFriendCountLimit() > 0
				&& myEntity.getFriendCount() >= SysBasicCfg.getInstance().getFriendCountLimit()) {
			sendError(HP.code.FRIEND_APPLY_C_VALUE, Status.error.FRIEND_COUNT_FULL);
			return;
		}

		// 获取对方FriendEntity
		FriendEntity targetEntity = null;
		Player targetplayer = PlayerUtil.queryPlayer(targetId);
		if (targetplayer != null) {
			targetEntity = targetplayer.getPlayerData().getFriendEntity();
		} else {
			targetEntity = DBManager.getInstance().fetch(FriendEntity.class,
					"from FriendEntity where playerId = ? and invalid = 0", targetId);
			if (targetEntity != null) {
				targetEntity.convert();
			}
		}
		if (targetEntity == null) {
			sendError(HP.code.FRIEND_APPLY_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		// 对方好友列表已满
		if (SysBasicCfg.getInstance().getFriendCountLimit() > 0
				&& targetEntity.getFriendCount() >= SysBasicCfg.getInstance().getFriendCountLimit()) {
			sendError(HP.code.FRIEND_APPLY_C_VALUE, Status.error.FRIEND_COUNT_FULL);
			return;
		}
		// 如果对方也向我发起过好友申请，则直接互相加为好友
		if (myEntity.isApplyFriend(targetId)) {
			addFriend(myEntity, targetEntity);
			return;
		}
		// 对方申请列表已满
		if (SysBasicCfg.getInstance().getApplyFriendCountLimit() > 0
				&& targetEntity.getapplyFriendCount() >= SysBasicCfg.getInstance().getApplyFriendCountLimit()) {
			sendError(HP.code.FRIEND_APPLY_C_VALUE, Status.error.FRIEND_APPLY_COUNT_FULL_VALUE);
			return;
		}
		// 已向对方发送过好友申请
		if (targetEntity.isApplyFriend(player.getId())) {
			sendError(HP.code.FRIEND_APPLY_C_VALUE, Status.error.ALREADY_APPLY_FRIEND_VALUE);
			return;
		}
		// 添加到对方的好友申请列表中
		targetEntity.addApplyFriendId(player.getId());
		// 对方在线，推送
		if (targetplayer != null && targetplayer.isOnline()) {
			FriendItem.Builder myItem = BuilderUtil.genSingleFriendItem(player.getId());
			targetplayer.sendProtocol(Protocol.valueOf(HP.code.FRIEND_ADD_APPLY_S_VALUE, myItem));
		}
		HPApplyFriend.Builder resp = HPApplyFriend.newBuilder();
		resp.setPlayerId(req.getPlayerId());
		sendProtocol(Protocol.valueOf(HP.code.FRIEND_APPLY_S_VALUE, resp));
	}

	/**
	 * 同意好友申请
	 */
	private void onAgreeApplyFriend(HPAgreeApplyFriend req) {
		int targetId = req.getPlayerId();
		if (targetId <= 0 || targetId == player.getId()) {
			sendError(HP.code.FRIEND_APPLY_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		// 自己的FriendEntity
		FriendEntity myEntity = player.getPlayerData().getFriendEntity();
		// 不再申请列表中
		if (!myEntity.isApplyFriend(targetId)) {
			sendError(HP.code.FRIEND_APPLY_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		// 已经是好友
		if (myEntity.isFriend(targetId)) {
			sendError(HP.code.FRIEND_APPLY_C_VALUE, Status.error.ALREADY_FRIEND_VALUE);
			return;
		}
		// 自己的好友数目已达上限
		if (SysBasicCfg.getInstance().getFriendCountLimit() > 0
				&& myEntity.getFriendCount() >= SysBasicCfg.getInstance().getFriendCountLimit()) {
			sendError(HP.code.FRIEND_APPLY_C_VALUE, Status.error.FRIEND_COUNT_FULL);
			return;
		}

		// 获取对方FriendEntity
		FriendEntity targetEntity = null;
		Player targetplayer = PlayerUtil.queryPlayer(targetId);
		if (targetplayer != null) {
			targetEntity = targetplayer.getPlayerData().getFriendEntity();
		} else {
			targetEntity = DBManager.getInstance().fetch(FriendEntity.class,
					"from FriendEntity where playerId = ? and invalid = 0", targetId);
			if (targetEntity != null) {
				targetEntity.convert();
			}
		}
		if (targetEntity == null) {
			sendError(HP.code.FRIEND_APPLY_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		// 对方好友列表已满
		if (SysBasicCfg.getInstance().getFriendCountLimit() > 0
				&& targetEntity.getFriendCount() >= SysBasicCfg.getInstance().getFriendCountLimit()) {
			sendError(HP.code.FRIEND_APPLY_C_VALUE, Status.error.FRIEND_COUNT_FULL);
			return;
		}
		// 互相加为好友
		addFriend(myEntity, targetEntity);
		// 发送同意邮件给玩家
		MailManager.createSysMail(targetId, Mail.MailType.Normal_VALUE, GsConst.MailId.AGREE_APPLY_FRIEND, "", null,
				player.getName());
	}

	/**
	 * 拒绝好友申请
	 */
	private void onRefuseApplyFriend(HPRefuseApplyFriend req) {
		int targetId = req.getPlayerId();
		if (targetId <= 0 || targetId == player.getId()) {
			sendError(HP.code.FRIEND_APPLY_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		// 自己的FriendEntity
		FriendEntity myEntity = player.getPlayerData().getFriendEntity();
		// 不再申请列表中
		if (!myEntity.isApplyFriend(targetId)) {
			sendError(HP.code.FRIEND_APPLY_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return;
		}
		// 从申请列表删除
		myEntity.removeApplyFriend(targetId);
		myEntity.notifyUpdate();
		HPRefuseApplyFriend.Builder resp = HPRefuseApplyFriend.newBuilder();
		resp.setPlayerId(targetId);
		player.sendProtocol(Protocol.valueOf(HP.code.FRIEND_REFUSE_S_VALUE, resp));
		// 给申请者发邮件
		// 发送拒绝邮件给玩家
		MailManager.createSysMail(targetId, Mail.MailType.Normal_VALUE, GsConst.MailId.REFUSED_APPLY_FRIEND, "", null,
				player.getName());
	}

	// 互相添加好友
	private void addFriend(FriendEntity myEntity, FriendEntity targetEntity) {
		myEntity.addFriendId(targetEntity.getPlayerId());
		myEntity.removeApplyFriend(targetEntity.getPlayerId());
		myEntity.notifyUpdate();
		targetEntity.addFriendId(myEntity.getPlayerId());
		targetEntity.removeApplyFriend(myEntity.getPlayerId());
		targetEntity.notifyUpdate();

		// 给自己推送加好友
		FriendItem.Builder targetItem = BuilderUtil.genSingleFriendItem(targetEntity.getPlayerId());
		targetItem.setHaveGift(myEntity.getGiftPoint(targetEntity.getPlayerId()) > 0);
		targetItem.setCanGift(!myEntity.isAlreadyGift(targetEntity.getPlayerId()));
		sendProtocol(Protocol.valueOf(HP.code.FRIEND_ADD_FRIEND_S_VALUE, targetItem));
		// 推送添加好友任务
		QuestEventBus.fireQuestEventOneTime(QuestEventType.ADD_FRIEND, player.getXid());
		// 如果目标在线，则给好友推送添加好友的消息
		Player targetPlayer = PlayerUtil.queryPlayer(targetEntity.getPlayerId());
		if (targetPlayer != null && targetPlayer.isOnline()) {
			FriendItem.Builder myItem = BuilderUtil.genSingleFriendItem(myEntity.getPlayerId());
			myItem.setHaveGift(targetEntity.getGiftPoint(myEntity.getPlayerId()) > 0);
			myItem.setCanGift(!targetEntity.isAlreadyGift(myEntity.getPlayerId()));
			targetPlayer.sendProtocol(Protocol.valueOf(HP.code.FRIEND_ADD_FRIEND_S_VALUE, myItem));
			// 推送添加好友任务
			QuestEventBus.fireQuestEventOneTime(QuestEventType.ADD_FRIEND, targetPlayer.getXid());
		} else {
			// 推送添加好友任务
			QuestEventBus.fireQuestEventWhenPlayerOffline(targetEntity.getPlayerId(), QuestEventType.ADD_FRIEND, 1);
		}
	}
}
