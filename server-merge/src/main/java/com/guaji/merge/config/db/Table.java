package com.guaji.merge.config.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {

	private String name;
	private int isMerge;// 是否要合并 (1:合并,0:不合并)
	private List<Field> fieldList = new ArrayList<Field>();
	private Map<String, Field> fieldMap = new HashMap<String, Field>();
	private int isDelete;// 是否删除无效playerId
	/** 默认是id */
	private String primaryKey = "id";
	private int primaryKeyIndex = 0;
	
	public void addStruct() {

		for (int i = 0; i < fieldList.size(); i++) {
			Field field = fieldList.get(i);
			fieldMap.put(field.getName(), field);
			// set primary key index
			if (field.getName().equals(primaryKey)) {
				primaryKeyIndex = i;
			}
		}
	}

	// 修改id重派规则注释
	public void increaseMaxValue() {
		for (Field field : fieldList) {
			field.setMaxValue(field.getTempValue());
		}
	}

	public Table() {

	}

	public Table(String name) {
		this.name = name;
	}

	public void addField(Field field) {
		fieldList.add(field);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Field> getFieldList() {
		return fieldList;
	}

	public Field getField(String key) {
		return fieldMap.get(key);
	}

	public int getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(int isDelete) {
		this.isDelete = isDelete;
	}

	public int getIsMerge() {
		return isMerge;
	}

	public void setIsMerge(int isMerge) {
		this.isMerge = isMerge;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	/**
	 * 获得表字段列表
	 * 
	 * @param needBracket
	 *            （返回值是否需要加括号）
	 * @return
	 */
	public String getFiledStr(boolean needBracket) {
		StringBuffer sb = new StringBuffer();
		if (needBracket) {
			sb.append("(");
		}
		for (int i = 0; i < fieldList.size(); i++) {
			sb.append(fieldList.get(i).getName());
			if (i != (fieldList.size() - 1)) {
				sb.append(",");
			} else {
				if (needBracket) {
					sb.append(")");
				}
			}
		}
		return sb.toString();
	}

	public int getPrimaryKeyIndex() {
		return primaryKeyIndex;
	}
}
