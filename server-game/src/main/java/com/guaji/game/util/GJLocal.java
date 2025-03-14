package com.guaji.game.util;

import com.guaji.game.GsConfig;

/**
 * 平台常量
 * 
 * @author ManGao
 * 
 */
public enum GJLocal {

	// 韩国平台
	KOREAN("korean"),
	//R2平台
	R2("R2");

	private String localName;

	private GJLocal(String name) {
		this.localName = name;
	}

	public String getLocalName() {
		return localName;
	}

	/**
	 * 判断是否正确的平台
	 * 
	 * @param local
	 * @return
	 */
	public static boolean isLocal(GJLocal local) {

		if (local.getLocalName().equals(GsConfig.getInstance().getLanguage())) {
			return true;
		}
		return false;
	}

}
