package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;

/**
 * 倍数掉落概率
 * 
 * @author Melvin.Mao
 * @date 2017年9月14日 下午5:42:43
 */
@ConfigManager.XmlResource(file = "xml/turntableMultiple.xml", struct = "list")
public class TurntableMultipleCfg extends ConfigBase {
	/**
	 * 掉落物组ID
	 */
	@Id
	private final int id;

	/**
	 * 倍数
	 */
	private final int multiple;

	/**
	 * 概率
	 */
	private final int rate;

	private static List<Integer> multipleList = new ArrayList<Integer>();

	private static List<Integer> weightList = new ArrayList<Integer>();

	public TurntableMultipleCfg() {
		this.id = 0;
		this.rate = 0;
		this.multiple = 0;
	}

	public int getId() {
		return id;
	}

	public int getMultiple() {
		return multiple;
	}

	public int getRate() {
		return rate;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
		multipleList.clear();
		weightList.clear();
	}

	@Override
	protected boolean assemble() {
		// 处理倍数
		multipleList.add(this.multiple);
		weightList.add(this.rate);
		return super.assemble();
	}

	/**
	 * 直接获取随机到的倍数
	 * @return
	 */
	public static int getRewardMultiple() {
		return GuaJiRand.randonWeightObject(multipleList, weightList);
	}

}
