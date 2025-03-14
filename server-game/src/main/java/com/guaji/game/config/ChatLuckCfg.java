package com.guaji.game.config;

import java.util.Date;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;

@ConfigManager.XmlResource(file = "xml/chatLuck.xml", struct = "list")
public class ChatLuckCfg extends ConfigBase {
	/**
	 * 开始时间
	 */
	private final Date start;
	/**
	 * 开始时间
	 */
	private final Date end;
	
	private final String keyWords;
	/**
	 * 奖励
	 */
	private final String reward;
	
	public ChatLuckCfg(){
		this.start = null;
		this.end = null;
		this.keyWords = "";
		this.reward = "";
	}
	
	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		return true;
	}
	
	/**
	 * 检测有消息
	 * @return
	 */
	@Override
	protected boolean checkValid() {
		return true;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

	public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}

	public String getKeyWords() {
		return keyWords;
	}

	public String getReward() {
		return reward;
	}
	
	public static ChatLuckCfg triggerChatLuck(String words) {
		long curTime = GuaJiTime.getMillisecond();
		for(ChatLuckCfg chatLuckCfg : ConfigManager.getInstance().getConfigList(ChatLuckCfg.class)) {
			if(curTime >= chatLuckCfg.getStart().getTime() && curTime <= chatLuckCfg.getEnd().getTime() && words.indexOf(chatLuckCfg.getKeyWords()) >= 0) {
				return chatLuckCfg;
			}
		}
		return null;
	}
	
}

