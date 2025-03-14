package com.guaji.game.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.guaji.os.MyException;
import org.guaji.os.GuaJiRand;


/**
 * 权重计算工具
 * 
 * @author xulinqs
 * 
 */
public class WeightUtil {
	/**
	 * 权重项
	 */
	public static class WeightItem<T> {
		private T value;
		private int weight;

		public static <T> WeightItem<T> valueOf(T value, int weight) {
			WeightItem<T> weightItem = new WeightItem<>();
			weightItem.weight = weight;
			weightItem.value = value;
			return weightItem;
		}

		public int getWeight() {
			return weight;
		}

		public void setWeight(int weight) {
			this.weight = weight;
		}

		public T getValue() {
			return value;
		}

		public void setValue(T value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return "value:" + value + ",weight:" + weight;
		}
	}

	/**
	 * 根据权重列表获得东西
	 * 
	 * @param itemList
	 * @return
	 */
	public static <T> T random(List<WeightItem<T>> itemList) {
		int totalWeight = 0;
		for (WeightItem<T> item : itemList) {
			totalWeight += item.getWeight();
		}

		try {
			int accumulative = 0;
			int randomWeight = GuaJiRand.randInt(1, totalWeight);
			for (int i = 0; i < itemList.size(); i++) {
				accumulative += itemList.get(i).weight;
				if (randomWeight <= accumulative) {
					return itemList.get(i).getValue();
				}
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}

		return null;
	}
	
	public static <T> List<T> randomList(List<WeightItem<T>> itemList, int count) {
		int totalWeight = 0;
		for (WeightItem<T> item : itemList) {
			totalWeight += item.getWeight();
		}
		List<T> list = new ArrayList<>();
		for (int j = 0; j < count; j++) {
			try {
				int accumulative = 0;
				int randomWeight = GuaJiRand.randInt(1, totalWeight);
				for (int i = 0; i < itemList.size(); i++) {
					accumulative += itemList.get(i).weight;
					if (randomWeight <= accumulative) {
						list.add(itemList.get(i).getValue());
						break;
					}
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return list;
	}
	

	/**
	 * 根据权重列表获得Id
	 * 
	 * @param itemList
	 * @return
	 */
	public static Integer random(String idWeights) {
		return random(convertToList(idWeights));
	}

	/**
	 * 把权重当做概率计算掉落
	 * 
	 * @param itemList
	 * @return
	 */
	public static <T> List<T> calcAsRandDrop(List<WeightItem<T>> itemList) {
		List<T> drops = new LinkedList<>();
		for (WeightItem<T> item : itemList) {
			try {
				if (GuaJiRand.randInt(GsConst.RANDOM_BASE_VALUE, GsConst.RANDOM_MYRIABIT_BASE) <= item.weight) {
					drops.add(item.getValue());
				}
			} catch (Exception e) {
				MyException.catchException(e);
			}
		}
		return drops;
	}

	/**
	 * 字符串转换为id权重项列表
	 * 
	 * @param idWeights
	 * @return
	 */
	public static List<WeightItem<Integer>> convertToList(String idWeights) {
		List<WeightItem<Integer>> itemList = new LinkedList<>();
		String items[] = idWeights.split(",");
		if (items.length > 0) {
			for (String item : items) {
				String[] idWeight = item.split("_");
				itemList.add(WeightItem.valueOf(Integer.valueOf(idWeight[0]), Integer.valueOf(idWeight[1])));
			}
		}
		return itemList;
	}

}
