package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 跨服筛选区间配置数据
 * 
 * @author Nannan.Gao
 * @date 2017-8-11 14:25:38
 */
@ConfigManager.XmlResource(file = "xml/sectionValue.xml", struct = "list")
public class SectionValueCfg extends ConfigBase {

	/**
	 * 最小排名区间
	 */
	private final int minRank;

	/**
	 * 向上匹配规则区间
	 */
	private final int upSection;

	/**
	 * 向下匹配规则区间
	 */
	private final int downSection;
	
	/**
	 * 随机排名比当前玩家高的对手个数
	 */
	private final int randomUp;

	/**
	 *  随机排名比当前玩家低的对手个数
	 */
	private final int randomDown;
	
	public SectionValueCfg() {
		minRank = 0;
		upSection = 0;
		downSection = 0;
		randomUp = 0;
		randomDown = 0;
	}
	
	@Override
	protected boolean assemble() {
		if (upSection + downSection < randomUp + randomDown) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	public int getMinRank() {
		return minRank;
	}

	public int getUpSection() {
		return upSection;
	}

	public int getDownSection() {
		return downSection;
	}

	public int getRandomUp() {
		return randomUp;
	}

	public int getRandomDown() {
		return randomDown;
	}
	
}
