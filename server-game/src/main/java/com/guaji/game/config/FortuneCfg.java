package com.guaji.game.config;

import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/fortune.xml", struct = "list")
public class FortuneCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	/**
	 * 所需充值钻石数
	 */
	private final int needRechargeGold;
	/**
	 * 获得最小钻石数
	 */
	private final int rewardMin ;
	/**
	 * 获得最大钻石数
	 */
	private final int rewardMax ;
	
	public FortuneCfg(){
		this.id = 0;
		this.needRechargeGold = 0;
		this.rewardMin = 0;
		this.rewardMax = 0;
	}
	
	public int getId() {
		return id;
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

	public int getNeedRechargeGold() {
		return needRechargeGold;
	}

	public int getRewardMin() {
		return rewardMin;
	}

	public int getRewardMax() {
		return rewardMax;
	}

	public static FortuneCfg getFortuneCfg(int id) {
		List<FortuneCfg> fortuneCfgs = ConfigManager.getInstance().getConfigList(FortuneCfg.class);
		for(FortuneCfg fortuneCfg : fortuneCfgs) {
			if(fortuneCfg.getId() == id) {
				return fortuneCfg;
			}
		}
		return null;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}

