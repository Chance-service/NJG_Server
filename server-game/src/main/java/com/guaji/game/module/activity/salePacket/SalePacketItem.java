package com.guaji.game.module.activity.salePacket;
/**
* @author 作者 E-mail:Jeremy@163.com
* @version 创建时间：2019年3月22日 上午12:25:09
* 类说明
*/
public class SalePacketItem {


	/**
	 * 状态 0 可购买 1 可领取
	 */
	private int state;
	
	/**
	 * 已购买次数
	 */
	private int buyTime;
	

	
	public SalePacketItem() {
		super();
	}

	public SalePacketItem(int state, int buyTime) {
		super();
		this.state = state;
		this.buyTime = buyTime;
	
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getBuyTime() {
		return buyTime;
	}

	public void setBuyTime(int buyTime) {
		this.buyTime = buyTime;
	}
	
	public void AddBuyTime()
	{
		this.buyTime=this.buyTime+1;
	}

	
	
	
}
