package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 部族的嘉奖数据
 * @author Administrator
 */
@ConfigManager.XmlResource(file = "xml/commendationTribe.xml", struct = "map")
public class CommentdationTribeCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	/**
	 * 必定跳阶段所需幸运值
	 */
	private final int needLuckyValue;
	/**
	 * 每点击一次增加的幸运值
	 */
	private final int perClickLuckyAdd ;
	/**
	 * 通过概率，万分比
	 */
	private final int passRate ;
	/**
	 * 每点击一次消耗多少积分
	 */
	private final int scoreCost ;
	/**
	 * 奖励
	 */
	private final String awards;
	/**
	 * 钻石兑换率
	 */
	private final int goldExcCount;
	
	public CommentdationTribeCfg(){
		this.id = 0;
		this.needLuckyValue = 0;
		this.perClickLuckyAdd = 0;
		this.passRate = 0;
		this.awards = "";
		this.goldExcCount = 100;
		this.scoreCost = 1;
	}
	
	public int getId() {
		return id;
	}

	public String getAwards() {
		return awards;
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

	public int getNeedLuckyValue() {
		return needLuckyValue;
	}

	public int getPerClickLuckyAdd() {
		return perClickLuckyAdd;
	}
	
	public int getPassRate() {
		return passRate;
	}

	public int getGoldExcCount() {
		return goldExcCount;
	}

	public int getScoreCost() {
		return scoreCost;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}

