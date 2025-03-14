package com.guaji.game.module.activity.consumeitem;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年4月17日 下午4:04:46 类说明
 */
public class ConsumeItem {
	/**
	 * @Fields dataId :购买道具对应的唯一编号
	 */
	private int goodId;
	/**
	 * @Fields buyTime :购买该商品对应的次数
	 */
	private int buytime;
	/**
	 * @Fields prizeTime :领奖到对应的次数
	 */
	private int prizeTime;
	
	
	
	
	public ConsumeItem() {
		this.goodId=0;
		this.buytime=0;
		this.prizeTime=0;
	}

	public ConsumeItem(int goodId, int buytime, int prizeTime) {
		super();
		this.goodId = goodId;
		this.buytime = buytime;
		this.prizeTime = prizeTime;
	}
	
	public int getGoodId() {
		return goodId;
	}
	public void setGoodId(int goodId) {
		this.goodId = goodId;
	}
	public int getBuytime() {
		return buytime;
	}
	public void setBuytime(int buytime) {
		this.buytime = buytime;
	}
	public int getPrizeTime() {
		return prizeTime;
	}
	public void setPrizeTime(int prizeTime) {
		this.prizeTime = prizeTime;
	}

	
	
}
