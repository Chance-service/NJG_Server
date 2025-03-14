package com.guaji.merge.config.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;

import com.guaji.merge.App;
import com.guaji.merge.util.StringUtil;

public class MergeHandler implements ResultSetHandler<List<String>> {

	private String tableName;
	private DbInfo dbInfo;
	private int serverId;
	private boolean mergeOver;
	private List<Object> oldPrimaryKeys = new ArrayList<Object>();

	public MergeHandler(String tableName, DbInfo dbInfo, int serverId, boolean mergeOver) {
		this.tableName = tableName;
		this.dbInfo = dbInfo;
		this.serverId = serverId;
		this.mergeOver = mergeOver;
	}

	@Override
	public List<String> handle(ResultSet rs) throws SQLException {
		oldPrimaryKeys.clear();
		List<String> sqlList = new ArrayList<String>();
		while (rs.next()) {
			String record = mergeHandler(rs);
			if (record == null) {
				continue;
			}
			sqlList.add(record);
		}
		return sqlList;
	}

	private String mergeHandler(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int cols = rsmd.getColumnCount();
		boolean isInvalid = false;
		StringBuffer sql = new StringBuffer();
	
		sql.append("(");
		for (int i = 1; i <= cols; i++) {
			String columnName = rsmd.getColumnName(i);
			Field field = dbInfo.getField(tableName, columnName);
			Object obj = rs.getObject(i);
			// add to primarykeys
			Table table = dbInfo.getTable(tableName);
			if (field.getName().equals(table.getPrimaryKey())) {
				oldPrimaryKeys.add(getPrimaryValue(tableName, rs.getObject(i)));
			}
			
			if (obj == null) {
				sql.append(obj);
			} else {
				sql.append("'");
				if (field.getType() == FieldType.auto_increment.value()) {
					int value = getIntValue(rs.getObject(i));
					if (mergeOver) {// 目前合服后操作表只要player，所以只操作自增类型
						sql.append(value);
					} else {
						sql.append(getNewValue(field, value));
					}
				
				} else if (field.getType() == FieldType.dependency.value()) {
					Field dependencyField = dbInfo.getField(field.getTablename(), field.getColumn());
					int value = getIntValue(rs.getObject(i));
					int newId = getDependencyValue(dependencyField, value);
					if (newId == 0 && table.getPrimaryKey().equals(field.getName())) {
						isInvalid = true;
						break;
					}
					sql.append(newId);
					// 修改id重派规则注释 sql.append(getNewValue(dependencyField,
					// value));
				} else if (field.getType() == FieldType.multi_dependency.value()) {
					Field dependencyField = dbInfo.getField(field.getTablename(), field.getColumn());
					handlerMulti(String.valueOf(rs.getObject(i)), field, dependencyField, sql);
				} else if (field.getType() == FieldType.multi_dependency_surround.value()) {
					Field dependencyField = dbInfo.getField(field.getTablename(), field.getColumn());
					String value = String.valueOf(rs.getObject(i));
					if (!StringUtil.isNull(value)) {
						value = value.replace(field.getLeftChar(), "").replace(field.getRightChar(), "");
					}
					sql.append(field.getLeftChar());
					handlerMulti(value, field, dependencyField, sql);
					sql.append(field.getRightChar());
				} else if (field.getType() == FieldType.back_auto_increment.value()) {
					int value = getIntValue(rs.getObject(i));
					if (mergeOver) {// 目前合服后操作表只要player，所以只操作自增类型
						sql.append(value);
					} else {
						sql.append(getBackDependencyValue(field, value));
					}
				} else if (field.getType() == FieldType.back_auto_increment_dependency.value()) {
					Field dependencyField = dbInfo.getField(field.getTablename(), field.getColumn());
					int value = getIntValue(rs.getObject(i));
					sql.append(getBackNewValue(dependencyField, value));
					// 修改id重派规则注释 sql.append(getNewValue(dependencyField,
					// value));
				} else if (field.getType() == FieldType.back_auto_increment_multi_dependency_surround.value()) {
					Field dependencyField = dbInfo.getField(field.getTablename(), field.getColumn());
					String value = String.valueOf(rs.getObject(i));
					if (!StringUtil.isNull(value)) {
						value = value.replace(field.getLeftChar(), "").replace(field.getRightChar(), "");
					}
					sql.append(field.getLeftChar());
					handlerBackMulti(value, field, dependencyField, sql);
					sql.append(field.getRightChar());
				} else {
					String value = String.valueOf(obj);
					value = value.replace("\\", "\\\\");
					value = value.replace("'", "\\'");
					sql.append(value);
				}
				sql.append("'");
			}
			if (i != cols) {
				sql.append(",");
			}
		}
		sql.append(")");

		if (isInvalid) {
			return null;
		}
		return sql.toString();
	}

	private Object getPrimaryValue(String tableName, Object value) {
		if (tableName.equals("recharge")) {
			return String.valueOf(value);
		}
		return getIntValue(value);
	}

	private int getIntValue(Object obj) {
		if (obj == null || obj.equals("")) {
			return 0;
		}
		String value = String.valueOf(obj);
		return Integer.valueOf(value);
	}

	// 修改id重派规则注释
	// public int getNewValue(Field field, int value) {
	// if (value == 0) {
	// return 0;
	// }
	// int maxValue = field.getMaxValue();
	// field.setTempValue(value);
	// return maxValue + value;
	// }
	public int getNewValue(Field field, int value) {
		if (value == 0) {
			return 0;
		}
		field.setTempValue(value, serverId);
		return field.getTempValue();
	}

	public int getBackNewValue(Field field, int value) {
		if (value == 0) {
			return 0;
		}
		if (field.getNewValue(serverId, value) != null) {
			return field.getNewValue(serverId, value);
		}
		field.setTempValue(value, serverId);
		return field.getTempValue();
	}

	// 修改id重派规则注释
	public Integer getDependencyValue(Field field, int value) {
		if (value == 0) {
			return 0;
		}
		if (field.getNewValue(serverId, value) == null) {
			return 0;
		}
		return field.getNewValue(serverId, value);
	}

	// 修改id重派规则注释
	public Integer getBackDependencyValue(Field field, int value) {
		if (value == 0) {
			return 0;
		}
		if (field.getNewValue(serverId, value) == null) {
			field.setTempValue(value, serverId);
			return field.getTempValue();
		}
		return field.getNewValue(serverId, value);
	}

	private void handlerMulti(String value, Field field, Field dependencyField, StringBuffer sql) {
		int values[] = StringUtil.commasStringToIntArray(value, field.getSeparator());
		if (values == null || values.length == 0) {
			sql.append("");
		} else {
			for (int k = 0; k < values.length; k++) {
				if (k == values.length - 1) {
					sql.append(getDependencyValue(dependencyField, values[k]));
					// 修改id重派规则注释 sql.append(getNewValue(dependencyField,
					// values[k]));
				} else {
					sql.append(getDependencyValue(dependencyField, values[k])).append(field.getSeparator());
					// 修改id重派规则注释 sql.append(getNewValue(dependencyField,
					// values[k])).append(
					// field.getSeparator());
				}
			}
		}
	}

	private void handlerBackMulti(String value, Field field, Field dependencyField, StringBuffer sql) {
		int values[] = StringUtil.commasStringToIntArray(value, field.getSeparator());
		if (values == null || values.length == 0) {
			sql.append("");
		} else {
			for (int k = 0; k < values.length; k++) {
				if (k == values.length - 1) {
					sql.append(getBackNewValue(dependencyField, values[k]));
					// 修改id重派规则注释 sql.append(getNewValue(dependencyField,
					// values[k]));
				} else {
					sql.append(getBackNewValue(dependencyField, values[k])).append(field.getSeparator());
					// 修改id重派规则注释 sql.append(getNewValue(dependencyField,
					// values[k])).append(
					// field.getSeparator());
				}
			}
		}
	}

	public Object getCurrentMinPrimaryKeyValue() {
		return this.oldPrimaryKeys.get(oldPrimaryKeys.size() - 1);
	}

}
