/**
 * 
 */
package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 注册奖励配置
 * 
 * @author jht
 * 
 */
@ConfigManager.XmlResource(file = "xml/platInitReward.xml", struct = "list")
public class PlatInitRewardConfig extends ConfigBase {
	/** 平台 **/
	private final String platform;
	/** 开始时间 */
	private final long rewardStartTime;
	/** 结束时间 **/
	private final long rewardEndTime;
	/** 奖励 */
	private final String rewardInfo;
	/** 提示信息 */
	private final String rewardMsg;

	public PlatInitRewardConfig() {
		platform = "";
		rewardStartTime = 0;
		rewardEndTime = 0;
		rewardInfo = "";
		rewardMsg = "";

	}

	public String getPlatform() {
		return platform;
	}

	public long getRewardStartTime() {
		return rewardStartTime;
	}

	public long getRewardEndTime() {
		return rewardEndTime;
	}

	public String getRewardInfo() {
		return rewardInfo;
	}

	public String getRewardMsg() {
		return rewardMsg;
	}

	public boolean isValid() {
		return platform.length() > 0 && rewardInfo.length() > 0 && rewardMsg.length() > 0;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
