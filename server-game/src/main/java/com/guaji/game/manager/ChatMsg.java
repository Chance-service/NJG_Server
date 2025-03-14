package com.guaji.game.manager;

import java.util.HashMap;
import java.util.Map;

import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Chat.HPChatMsg;
import com.guaji.game.protocol.Chat.HPChatMsg.Params;

/**
 * 聊天信息
 */
public class ChatMsg {
	/**
	 * 时间
	 */
	private long msTime;
	/**
	 * 0:世界聊天,1:公会聊天
	 */
	private int type;
	/**
	 * 玩家id
	 */
	private int playerId;
	/**
	 * 玩家名字
	 */
	private String name;
	/**
	 * 玩家等级
	 */
	private int level;
	/**
	 * 转生阶段
	 */
	private int rebirthStage;
	/**
	 * 主角配置id
	 */
	private int roleItemId;
	/**
	 * 聊天消息
	 */
	private String chatMsg;
	
	/**
	 * 内容类型 0是字符串，1是json
	 */
	private int msgType;
	/*
	 * 
	 * 国家语言类型
	 */
	private int i18Flag;
	
	/**
	 * 玩家类型
	 */
	private int playerType = 0;
	/**
	 * 公会ID 
	 */
	private int allianceId = 0;
	/**
	 * 聊天消息
	 */
	private String arenaInfo;
	/**
	 * 称号Id
	 */
	private int titleId;
	/**
	 * 备用参数字段
	 */
	private Map<String, String> params;
	/**
	 * 功能Id 1 是组队副本邀请
	 */
	private int modelId;
	
	/**
	 * 皮肤ID
	 */
	private int skinId;
	/**
	 * avatar
	 */
	private int avatarId;
	/**
	 * headIcon 头像
	 */
	private String headIcon;
	
	public ChatMsg() {
		name = "";
		chatMsg = "";
		arenaInfo = "";
		headIcon = "";
		msTime = GuaJiTime.getMillisecond();
		params = new HashMap<String, String>();
	}

	/**
	 * 构造函数
	 * 
	 * @param playerId
	 * @param name
	 * @param level
	 * @param roleItemId
	 * @param chatMsg
	 */
	public ChatMsg(int type,int playerId, String name, int level, int rebirthStage, int roleItemId, String chatMsg,int allianceId, int titleId) {
		this.type = type;
		this.playerId = playerId;
		this.name = name;
		this.level = level;
		this.rebirthStage = rebirthStage;
		this.roleItemId = roleItemId;
		this.chatMsg = chatMsg;
		this.allianceId = allianceId;
		this.titleId = titleId;
		this.msTime = GuaJiTime.getMillisecond();
	}

	public int getTitleId() {
		return titleId;
	}

	public void setTitleId(int titleId) {
		this.titleId = titleId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getRoleItemId() {
		return roleItemId;
	}

	public void setRoleItemId(int roleItemId) {
		this.roleItemId = roleItemId;
	}

	public String getChatMsg() {
		return chatMsg;
	}

	public void setChatMsg(String chatMsg) {
		this.chatMsg = chatMsg;
	}

	public int getAllianceId() {
		return allianceId;
	}

	public void setAllianceId(int allianceId) {
		this.allianceId = allianceId;
	}

	public String getArenaInfo() {
		return arenaInfo;
	}

	public void setArenaInfo(String arenaInfo) {
		this.arenaInfo = arenaInfo;
	}
	
	public long getMsTime() {
		return msTime;
	}

	public void setMsTime(long msTime) {
		this.msTime = msTime;
	}
	
	public int getPlayerType() {
		return playerType;
	}

	public void setPlayerType(int playerType) {
		this.playerType = playerType;
	}
	
	
		public void addParam(String key, String value){
		params.put(key, value);
	}
	
	public int getModelId() {
		return modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public int getRebirthStage() {
		return rebirthStage;
	}

	public void setRebirthStage(int rebirthStage) {
		this.rebirthStage = rebirthStage;
	}
	
	public int getMsgType() {
		return msgType;
	}
	
	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}
	
	public int getI18Flag() {
		return i18Flag;
	}
	
	public void setI18Flag(int i18Flag) {
		this.i18Flag = i18Flag;
	}
	
	
	public String getHeadIcon() {
		return headIcon;
	}

	public void setHeadIcon(String headIcon) {
		this.headIcon = headIcon;
	}

	/**
	 * 生成builder信息
	 * 
	 * @return
	 */
	public HPChatMsg.Builder genBuilder() {
		HPChatMsg.Builder builder = HPChatMsg.newBuilder();
		builder.setType(type);
		builder.setPlayerId(playerId);
		builder.setName(name);
		builder.setLevel(level);
		builder.setRebirthStage(rebirthStage);
		builder.setMsTime(msTime/1000);
		builder.setRoleItemId(roleItemId);
		builder.setChatMsg(chatMsg);
		builder.setTitleId(titleId);
		builder.setModelId(modelId);
		builder.setSkinId(skinId);
		builder.setHeadIcon(headIcon);
	
		if(params.size() > 0){
			for(Map.Entry<String,String> entry : params.entrySet()){
				Params.Builder paramBuilder = Params.newBuilder();
				paramBuilder.setName(entry.getKey());
				paramBuilder.setValue(entry.getValue());
				builder.addParams(paramBuilder);
			}
		}
		
		if (arenaInfo != null && arenaInfo.length() > 0) {
			builder.setArea(arenaInfo);
		}
		
		if(playerType > 0) {
			builder.setPlayerType(playerType);
		}
		builder.setMsgType(getMsgType());
		builder.setI18NTag(getI18Flag());
		builder.setAvatarId(avatarId);
		return builder;
	}

	public void setSkinId(int skinId) {
		this.skinId = skinId;
	}

	public void setAvatarId(int avatarId) {
		this.avatarId = avatarId;
	}
}
