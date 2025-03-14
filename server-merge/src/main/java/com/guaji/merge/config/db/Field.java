package com.guaji.merge.config.db;

import java.util.HashMap;
import java.util.Map;

import com.guaji.merge.App;

public class Field {

	private String name;
	private String selfTable;
	private int type;
	private String tablename;
	private String column;
	private String separator;

	// type=4时出现字符包围时使用
	private String leftChar;
	private String rightChar;

	private int maxValue = -1;// 主server上当前字段的最大值
	private int tempValue = 0;// 多个从服务器的情况，当其中一个slave合并玩之后，需要改变maxvalue
	//新旧id缓冲<serverId<oldId,newId>>
	private Map<Integer, Map<Integer, Integer>> idMap =new HashMap<Integer, Map<Integer,Integer>>();

	public Field() {

	}

	public Field(String name, int type, String tablename, String column) {
		this.name = name;
		this.type = type;
		this.tablename = tablename;
		this.column = column;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public String getLeftChar() {
		return leftChar;
	}

	public void setLeftChar(String leftChar) {
		this.leftChar = leftChar;
	}

	public String getRightChar() {
		return rightChar;
	}

	public void setRightChar(String rightChar) {
		this.rightChar = rightChar;
	}

	public int getTempValue() {
		return tempValue;
	}

	//修改id重派规则注释
	public void setTempValue(int tempValue) {
		if (tempValue > this.tempValue) {
			this.tempValue = tempValue;
		}
	}
	
	//修改id重派规则注释	
	public void setTempValue(int value, int serverId) {
		this.tempValue+=1;
		Map<Integer, Integer> map=this.idMap.get(serverId);
		if (map==null) {
			map=new HashMap<Integer,Integer>();
			this.idMap.put(serverId, map);
		}
		 map.put(value, this.tempValue);
	}
	
	public String getSelfTable() {
		return selfTable;
	}

	public void setSelfTable(String selfTable) {
		this.selfTable = selfTable;
	}

	public Integer getNewValue(int serverId,int value){
		if (idMap.get(serverId)==null) {
			App.log.info(selfTable+"表的 "+name+"字段"+serverId+"服映射表为空");
			return null;
		}
		return this.idMap.get(serverId).get(value);
	}

}
