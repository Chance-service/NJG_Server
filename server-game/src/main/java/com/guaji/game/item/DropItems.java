package com.guaji.game.item;

import java.util.LinkedList;
import java.util.List;

import org.guaji.os.MyException;
import org.guaji.os.GuaJiRand;

import com.guaji.game.util.GsConst;

/**
 * 概率掉落物品
 */
public class DropItems {
	/**
	 * 掉落物品定义
	 */
	public static class Item {
		public int type;
		public int id;
		public long count;
		public int rate;
		public float rateRatio;
		
		public Item() {
			rate = GsConst.RANDOM_MYRIABIT_BASE;
			rateRatio = 0;
		}

		public Item(int type, int id, long count, int rate) {
			this.type = type;
			this.id = id;
			this.count = count;
			this.rate = rate;
			this.rateRatio = 0;
		}
		
		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public long getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public int getRate() {
			return rate;
		}

		public void setRate(int rate) {
			this.rate = rate;
		}

		public float getRateRatio() {
			return rateRatio;
		}

		public void setRateRatio(float rateRatio) {
			this.rateRatio = rateRatio;
		}
		
		@Override
		public String toString() {
			return String.format("%d_%d_%d", type, id, count);
		}

		@Override
		public Item clone() {
			Item item = new Item();
			item.type = this.type;
			item.id = this.id;
			item.count = this.count;
			item.rate = this.rate;
			item.rateRatio = this.rateRatio;
			return item;
		}
	}

	private List<Item> dropItems;

	public DropItems() {
		dropItems = new LinkedList<Item>();
	}

	public List<Item> getDropItems() {
		return dropItems;
	}

	public void setDropItems(List<Item> dropItems) {
		this.dropItems = dropItems;
	}

	public void addItem(Item item) {
		this.dropItems.add(item);
	}

	public void addItems(List<Item> items) {
		for (Item item : items) {
			dropItems.add(item);
		}
	}

	public void clearItems() {
		dropItems.clear();
	}

	/**
	 * 更加配置初始化掉落配置内存数据
	 * 
	 * @param info
	 * @return
	 */
	public boolean initByString(String info) {
		if (info != null && info.length() > 0 && !info.equals("0") && !info.equals("none")) {
			String[] dropItemArray = info.split(",");
			for (int i = 0; i < dropItemArray.length; i++) {
				String[] items = dropItemArray[i].split("_");
				if (items.length < 3) {
					return false;
				}

				Item dropItem = new Item();
				dropItem.type = Integer.parseInt(items[0]);
				dropItem.id = Integer.parseInt(items[1]);
				dropItem.count = Long.parseLong(items[2]);
				if (items.length > 3) {
					dropItem.rate = Integer.parseInt(items[3]);
				}
				
				if (items.length > 4) {
					dropItem.rateRatio = Float.parseFloat(items[4]);
				}
				dropItems.add(dropItem);
			}
			return true;
		}
		return false;
	}

	/**
	 * 计算掉落
	 * 
	 * @return
	 */
	public List<Item> calcDrop() {
		List<Item> drops = new LinkedList<Item>();
		for (Item item : dropItems) {
			try {
				if (GuaJiRand.randInt(GsConst.RANDOM_BASE_VALUE, GsConst.RANDOM_MYRIABIT_BASE) <= item.rate) {
					drops.add(item.clone());
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return drops;
	}
	
	/**
	 * 计算掉落(可受外部加成比例影响)
	 * 
	 * @return
	 */
	public List<Item> calcDropByBlankTable(float globalAwardRatio, float specialAwardRatio) {
		List<Item> drops = new LinkedList<Item>();
		for (Item item : dropItems) {
			try {
				// 属性加成影响掉率率
				int dropRate = (int)((1.0f + specialAwardRatio * item.getRateRatio()) * item.getRate());
				
				// 全服掉率率
				if (globalAwardRatio > 1.0f) {
					dropRate = (int)(globalAwardRatio * dropRate);
				}
				
				if (GuaJiRand.randInt(GsConst.RANDOM_BASE_VALUE, GsConst.RANDOM_MYRIABIT_BASE) <= dropRate) {
					drops.add(item.clone());
					break;
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return drops;
	}
	
	/**
	 * 计算掉落(可受外部加成比例影响)
	 * 
	 * @return
	 */
	public List<Item> calcDropByRatio(float globalAwardRatio, float specialAwardRatio) {
		List<Item> drops = new LinkedList<Item>();
		for (Item item : dropItems) {
			try {
				// 属性加成影响掉率率
				int dropRate = (int)((1.0f + specialAwardRatio * item.getRateRatio()) * item.getRate());
				
				// 全服掉率率
				if (globalAwardRatio > 1.0f) {
					dropRate = (int)(globalAwardRatio * dropRate);
				}
				
				if (GuaJiRand.randInt(GsConst.RANDOM_BASE_VALUE, GsConst.RANDOM_MYRIABIT_BASE) <= dropRate) {
					drops.add(item.clone());
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return drops;
	}
	/**
	 * 計算唯一掉落(按設定機率)
	 * 
	 * @return
	 */
	public Item calcDropByOnly() {
		Item drops = new Item();
		List<Integer> RateList = new LinkedList<>();
		for (Item item : dropItems) {
			RateList.add(item.getRate());
		}
		
		try {
			return GuaJiRand.randonWeightObject(dropItems, RateList).clone();
		} catch (Exception e) {
			MyException.catchException(e);
		}
		
		return drops;
	}
}
