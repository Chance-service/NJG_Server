package com.guaji.game.attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * 属性类型
 */
public class AttrInfoObj {
	private int attrType;
	private int addType;
	private int attrValue;

	public static List<AttrInfoObj> valueOfs(String infos) {
		List<AttrInfoObj> list = new ArrayList<AttrInfoObj>();
		for (String info : infos.split(",")) {
			String[] items = info.split("_");
			if (items.length != 3) {
				continue;
			}
			AttrInfoObj ringAttrInfo = new AttrInfoObj();
			ringAttrInfo.setAttrType(Integer.parseInt(items[0]));
			ringAttrInfo.setAddType(Integer.parseInt(items[1]));
			ringAttrInfo.setAttrValue(Integer.parseInt(items[2]));
			list.add(ringAttrInfo);
		}
		return list;
	}

	public int getAttrType() {
		return attrType;
	}

	public void setAttrType(int attrType) {
		this.attrType = attrType;
	}

	public int getAddType() {
		return addType;
	}

	public void setAddType(int addType) {
		this.addType = addType;
	}

	public int getAttrValue() {
		return attrValue;
	}

	public void setAttrValue(int attrValue) {
		this.attrValue = attrValue;
	}

}
