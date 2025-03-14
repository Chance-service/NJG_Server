package com.guaji.game.module.activity.consumeitem;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年4月7日 下午5:52:10 类说明
 */
public class AccConItemStatus {

	/**
	 * @Fields consumeItems :购买对应的商品信息
	 */
	private Map<Integer, ConsumeItem> consumeItems;

	public AccConItemStatus() {

		this.consumeItems = new HashMap<Integer, ConsumeItem>();
	}

	public Map<Integer, ConsumeItem> getConsumeItems() {
		return consumeItems;
	}

	/**
	 * @Title: addGoodItem @Description: TODO(这里用一句话描述这个方法的作用) @param @param
	 * dataId @param @param goodItem 设定文件 @return void 返回类型 @throws
	 */
	public void addGoodItem(Integer dataId, ConsumeItem goodItem) {

		this.consumeItems.put(dataId, goodItem);
	}

	/**
	 * @Title: addGoodBuyTime @Description:添加商品购买次数 @param @param dataId
	 * 购买物品编号 @param @param buyTime 购买次数 @return void 返回类型 @throws
	 */
	public void addGoodBuyTime(Integer dataId, int buyTime) {
		if (this.consumeItems.containsKey(dataId))
			this.consumeItems.get(dataId).setBuytime(this.consumeItems.get(dataId).getBuytime() + buyTime);
		else
			this.consumeItems.put(dataId, new ConsumeItem(dataId.intValue(), buyTime, 0));
	}

}
