package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/releaseStepGiftawrd179.xml", struct = "map")
public class ReleaseStepGiftCfg extends ConfigBase {
	
	/**
	 * 配置id(goodsId)
	 */
	@Id
	private final int id;		
	/**
	 * 奖励
	 */
	private final String awards;
	
	/**
	 * 最大禮包goodsID
	 */
	private static int MaxGoodsId ;
	/**
	 * 最小禮包goodsID
	 */
	private static int MinGoodsId ;

	public ReleaseStepGiftCfg() {
		this.id = 0;
		this.awards = "";
		MaxGoodsId = 0;
		MinGoodsId = 0;
	}

	public int getId() {
		return id;
	}

	public String getAwards() {
		return awards;
	}
	
	public static int getMaxGoodsId() {
		return MaxGoodsId;
	}

	public static int getMinGoodsId() {
		return MinGoodsId;
	}

	public static void setMinGoodsId(int minGoodsId) {
		MinGoodsId = minGoodsId;
	}

	public static void setMaxGoodsId(int maxGoodsId) {
		MaxGoodsId = maxGoodsId;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		
		if (MinGoodsId == 0) {
			MinGoodsId = this.id;
		} else {
			if (MinGoodsId > this.id) {
				MinGoodsId = this.id;
			}
		}
		
		if (this.id > MaxGoodsId) {
			setMaxGoodsId(this.id);
		}
		return true;
	}

	/**
	 * 检测有消息
	 * 
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
}
