package com.guaji.merge.config.db;

public enum FieldType {

	normal(0, "正常模式"), //
	auto_increment(1, "自动增长"), //
	dependency(2, "依赖"), //
	multi_dependency(3, "依赖多个"), //
	multi_dependency_surround(4, "依赖多个被包围"),//
	back_auto_increment(5, "反向自增"),//
	back_auto_increment_dependency(6, "反向自增依赖"), //
	back_auto_increment_multi_dependency_surround(7, "反向自增依赖多个被包围");//
	private int value;
	private String name;

	private FieldType(int value, String name) {
		this.value = value;
		this.name = name;
	}

	public int value() {
		return value;
	}

	public String getName() {
		return name;
	}
}
