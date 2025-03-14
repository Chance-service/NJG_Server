package com.guaji.game.module.activity.salePacket;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.guaji.log.Log;
import org.guaji.os.GuaJiTime;

/**
 * 打折礼包数据
 * 
 * @author Callan
 *
 */
public class SalePacketStatus {
	/**
	 * 已经购买的折扣礼包,key礼包id，value上次领取时间
	 */
	private HashMap<Integer, SalePacketItem> packetTable;

	/**
	 * @Fields lastDate : TODO(上次重置时间)
	 */
	private Date lastDate;

	public Date getLastDate() {
		return lastDate;
	}

	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}

	public SalePacketStatus() {
		packetTable = new HashMap<Integer, SalePacketItem>();
		lastDate = GuaJiTime.getCalendar().getTime();
	}

	public SalePacketStatus(HashMap<Integer, SalePacketItem> packetTable) {
		super();
		this.packetTable = packetTable;
		lastDate = GuaJiTime.getCalendar().getTime();
	}

	/**
	 * 添加到已购买列表
	 * 
	 * @param id
	 */
	public boolean addPacket(int id) {
		if (!packetTable.containsKey(id)) {
			SalePacketItem item = new SalePacketItem(0, 0);
			packetTable.put(id, item);
		}
		return true;
	}

	/**
	 * 是否红点提示，已经购买了折扣礼包，但是没有领取过
	 */
	public boolean showRedPoint() {
		Iterator<Map.Entry<Integer, SalePacketItem>> it = packetTable.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<Integer, SalePacketItem> entry = it.next();

			SalePacketItem item = entry.getValue();

			if (item != null && item.getState() == 1) {
				return true;
			}

		}

		return false;
	}

	/**
	 * 移出已购买列表中的某个物品
	 */
	public void removePacket(int id) {
		if (packetTable.containsKey(id)) {
			packetTable.remove(id);
		}
		return;
	}

	/**
	 * 领取礼包
	 */
	public boolean getPacket(int id) {
		if (packetTable.containsKey(id)) {
			if (packetTable.get(id) == null) {
				SalePacketItem item = new SalePacketItem(0, 0);
				packetTable.put(id, item);
			} else {

				SalePacketItem item = packetTable.get(id);
				item.setState(0);
				packetTable.put(id, item);
			}
			return true;
		}
		return false;
	}

	/**
	 * 获取某个id礼包的领取时间
	 */
	public long getPacketGetTime(int id) {
		if (packetTable.containsKey(id)) {
			if (packetTable.get(id) == null) {
				return 0;
			}

			return 0;
		}

		Log.logPrintln("得到非法id时间，id为" + String.valueOf(id));
		// log报错
		return -1;
	}

	public int getPacketState(int id) {

		if (packetTable.containsKey(id)) {
			if (packetTable.get(id) == null) {
				return 0;
			}

			return packetTable.get(id).getState();
		}
		return 0;
	}

	/**
	 * 获取当前信息
	 */
	public HashMap<Integer, SalePacketItem> getInfo() {
		return packetTable;
	}

	/**
	 * 刷新礼包状态：移出已经购买列表中，领取时间存在且不在今天的物品（隔天刷新）
	 */
	public boolean refreshPacketTable() {
		
		boolean flag = false;
		if(!GuaJiTime.isSameDay(lastDate.getTime(),GuaJiTime.getCalendar().getTime().getTime())) {
			Iterator<Map.Entry<Integer, SalePacketItem>> it = packetTable.entrySet().iterator();
		
			while (it.hasNext()) {
				Map.Entry<Integer, SalePacketItem> entry = it.next();

				SalePacketItem item = entry.getValue();

				if (item == null) {
					continue;
				}
				item.setBuyTime(0);
				item.setState(0);
			}
			//记得重置时间
			this.setLastDate(GuaJiTime.getCalendar().getTime());
			flag=true;
		}
		

		return flag;
	}

}
