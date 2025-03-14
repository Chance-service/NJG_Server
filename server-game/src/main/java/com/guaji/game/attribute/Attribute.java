package com.guaji.game.attribute;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.guaji.game.protocol.Const;

/**
 * 属性定义&属性计算
 */
public class Attribute {
	/**
	 * 属性映射表
	 */
	private Map<Const.attr, Integer> attrMap;

	public Attribute() {
		attrMap = new ConcurrentHashMap<Const.attr, Integer>();
	}

	public Map<Const.attr, Integer> getAttrMap() {
		return attrMap;
	}
	
	public void add(int attrType, int value) {
		if (attrType > 0) {
			Const.attr attrTypeEnum = Const.attr.valueOf(attrType);
			if(attrTypeEnum != null){
				if (attrMap.containsKey(attrTypeEnum)) {
					attrMap.put(attrTypeEnum, attrMap.get(attrTypeEnum) + value);
				} else {
					attrMap.put(attrTypeEnum, value);
				}
			}
		}
	}
	
	public void add(Const.attr attrType, int value) {
		if (attrMap.containsKey(attrType)) {
			attrMap.put(attrType, attrMap.get(attrType) + value);
		} else {
			attrMap.put(attrType, value);
		}
	}
	
	public void set(Const.attr attrType, int value) {
		attrMap.put(attrType, value);
	}

	public Attribute add(Attribute attr) {
		if (attr != null) {
			for (Map.Entry<Const.attr, Integer> entry : attr.attrMap.entrySet()) {
				add(entry.getKey(), attr.getValue(entry.getKey()));
			}
		}
		return this;
	}

	public Attribute sub(Attribute attr) {
		if (attr != null) {
			for (Map.Entry<Const.attr, Integer> entry : attr.attrMap.entrySet()) {
				int value = this.getValue(entry.getKey()) - attr.getValue(entry.getKey());
				set(entry.getKey(), value);
			}
		}
		return this;
	}

	public Attribute multiplicate(Attribute attr) {
		if (attr != null) {
			for (Map.Entry<Const.attr, Integer> entry : attr.attrMap.entrySet()) {
				set(entry.getKey(), (entry.getValue() * attr.getValue(entry.getKey())));
			}
		}
		return this;
	}
	
	public Attribute multiplicate(int level) {
		for (Map.Entry<Const.attr, Integer> entry : attrMap.entrySet()) {
			set(entry.getKey(), (entry.getValue() * level));
		}
		return this;
	}

	public Attribute additionAttr(Attribute addition) {
		if (addition != null) {
			for (Map.Entry<Const.attr, Integer> entry : addition.attrMap.entrySet()) {
				float additionRatio = 0.0001f * addition.getValue(entry.getKey());
				set(entry.getKey(), (int)(this.getValue(entry.getKey()) * (1.0f + additionRatio)));
			}
		}
		return this;
	}
	
	public int getValue(int attrType) {
		if(attrType <= 0) {
			return 0;
		}
		
		Const.attr attrEnumType = Const.attr.valueOf(attrType);
		if (attrMap.containsKey(attrEnumType)) {
			return attrMap.get(attrEnumType);
		}
		return 0;
	}
	
	public int getValue(Const.attr attrType) {
		if (attrMap.containsKey(attrType)) {
			return attrMap.get(attrType);
		}
		return 0;
	}

	public boolean containsAttr(int attrType) {
		if (attrType <= 0) {
			return false;
		}
		
		return attrMap.containsKey(Const.attr.valueOf(attrType));
	}
	
	public boolean containsAttr(Const.attr attrType) {
		return attrMap.containsKey(attrType);
	}
	
	public void clear() {
		for (Map.Entry<Const.attr, Integer> entry : attrMap.entrySet()) {
			this.setAttr(entry.getKey(), 0);
		}
	}
	
	public void clearall() {
			this.attrMap.clear();
	}

	public Attribute clone() {
		Attribute attribute = new Attribute();
		for (Map.Entry<Const.attr, Integer> entry : attrMap.entrySet()) {
			attribute.setAttr(entry.getKey(), entry.getValue());
		}
		return attribute;
	}
	
	public void setAttr(int attrType, int value) {
		if (attrType <= 0) {
			return;
		}
		
		attrMap.put(Const.attr.valueOf(attrType), value);
	}
	
	public void setAttr(Const.attr attrType, int value) {
		attrMap.put(attrType, value);
	}

	public int getSumAttrValue() {
		int attrValue = 0;
		for (Map.Entry<Const.attr, Integer> entry : attrMap.entrySet()) {
			attrValue += entry.getValue();
		}
		return attrValue;
	}
	
	public boolean isEmpty() {
		return this.attrMap.size() <= 0;
	}
	
	@Override
	public String toString() {
		String info = "";
		Iterator<Map.Entry<Const.attr, Integer>> iterator = attrMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Const.attr, Integer> entry = iterator.next();
			info += (entry.getKey().getNumber() + "_" + entry.getValue());
			if (iterator.hasNext()) {
				info += ",";
			}
		}
		return info;
	}

	public boolean initByString(String infos) {
		if (infos != null) {
			for (String info : infos.split(",")) {
				String[] items = info.split("_");
				if (items.length != 2) {
					continue;
				}
				add(Const.attr.valueOf(Integer.valueOf(items[0])), Integer.valueOf(items[1]));
			}
		}
		return true;
	}
	
	public static Attribute valueOf(String infos) {
		Attribute attribute = new Attribute();
		if (attribute.initByString(infos)) {
			return attribute;
		}
		return null;
	}
	
	public int size() {
		int size = 0;
		for(Map.Entry<Const.attr, Integer> entry : this.attrMap.entrySet()) {
			if(entry.getValue() > 0) {
				size++;
			}
		}
		return size;
	}

	public void removeAttr(int attrId) {
		this.attrMap.remove(Const.attr.valueOf(attrId));
	}
}
