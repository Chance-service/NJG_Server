package com.guaji.merge.config.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbInfo {

	private int everyCount;
	private int deleteCount;
	private List<Table> tableList = new ArrayList<Table>();
	private Map<String, Table> tableMap = new HashMap<String, Table>();

	/**
	 * 增加数据结构，方便后续的查询
	 */
	public void addStruct() {
		for (Table table : tableList) {
			tableMap.put(table.getName(), table);
			table.addStruct();
		}
	}

	// 修改id重派规则注释
	// public void increaseMaxValue() {
	// for (Table table : tableList) {
	// if (table.getIsMerge() == 1) {
	// table.increaseMaxValue();
	// }
	// }
	// }

	public void addTable(Table table) {
		tableList.add(table);
	}

	public List<Table> getTableList() {
		return tableList;
	}

	/**
	 * 通过表名和字段名获取字段对象
	 * 
	 * @param tableName
	 *            表名
	 * @param key
	 *            字段名
	 * @return
	 */
	public Field getField(String tableName, String key) {
		Table table = tableMap.get(tableName);
		return table.getField(key);
	}

	/**
	 * 字段依赖类型检查
	 */
	public void checkType() {
		for (Table table : tableList) {
			List<Field> fieldList = table.getFieldList();
			for (Field field : fieldList) {
				if (field.getType() == FieldType.dependency.value()
						|| field.getType() == FieldType.multi_dependency.value()) {
					if (field.getTablename() == null || field.getTablename().equals("")) {
						throw new RuntimeException("字段【" + field.getName() + "】类型是" + field.getType()
								+ ",但是没有tablename属性");
					}
					if (field.getColumn() == null || field.getColumn().equals("")) {
						throw new RuntimeException("字段【" + field.getName() + "】类型是" + field.getType() + ",但是没有column属性");
					}

					Table tempT = tableMap.get(field.getTablename());
					Field tempF = tempT.getField(field.getColumn());
					if (tempF.getType() != FieldType.auto_increment.value()) {
						throw new RuntimeException("字段【" + field.getName() + "】依赖于表【" + tempT.getName() + "】中的【"
								+ tempF.getName() + "】字段,但是【" + tempF.getName() + "】字段类型不是auto_increment");
					}
				}
			}
		}
	}

	/**
	 * 获取字段类型是auto_increment，并取出当前的最大值
	 * 
	 * @param master
	 */
	// 修改id重派规则注释
	// public void loadMaxValue(Master master) {
	// logger.info("读取master库,记录FieldType为auto_increment的最大值");
	// for (Table table : tableList) {
	// if (table.getIsMerge() == 1) {
	// List<Field> fieldList = table.getFieldList();
	// for (Field field : fieldList) {
	// if (field.getType() == FieldType.auto_increment.value()) {
	// // String selectSql = "select " + field.getName()
	// // + " FROM " + table.getName() + " order by "
	// // + field.getName() + " DESC limit 0,1";
	//
	// String selectSql = "select count(" + field.getName()
	// + ") FROM " + table.getName();
	// List<Map<String, Object>> value = master.getImpl()
	// .executeQuery(selectSql);
	// if (value == null || value.size() == 0) {
	// // field.setMaxValue(0);
	// } else {
	// String obj = String.valueOf(value.get(0).get(
	// "count("+field.getName()+")"));
	// // field.setMaxValue(Integer.valueOf(obj));
	// }
	// // logger.info("dbInfo"+master.getDbInfo()+"table=" + table.getName() +
	// ",field="
	// // + field.getName() + ",maxValue="
	// // + field.getMaxValue());
	// }
	// }
	// }
	// }
	// }

	public int getEveryCount() {
		return everyCount;
	}

	public void setEveryCount(int everyCount) {
		this.everyCount = everyCount;
	}

	public int getDeleteCount() {
		return deleteCount;
	}

	public void setDeleteCount(int deleteCount) {
		this.deleteCount = deleteCount;
	}

	public Map<String, Table> getTableMap() {
		return tableMap;
	}

	public Table getTable(String tableName) {
		return tableMap.get(tableName);
	}

}
