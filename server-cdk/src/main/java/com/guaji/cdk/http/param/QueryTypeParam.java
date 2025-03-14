package com.guaji.cdk.http.param;

import java.util.Map;

import org.guaji.os.MyException;
import org.guaji.util.services.CdkService;

/**
 * cdk类型查询
 * 
 */
public class QueryTypeParam {
	private String game;
	private String type;

	public String getGame() {
		return game;
	}

	public void setGame(String game) {
		this.game = game;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void toLowerCase() {
		if (game != null) {
			game = game.toLowerCase();
		}

		if (type != null) {
			type = type.toLowerCase();
		}
	}

	public boolean initParam(Map<String, String> params) {
		try {
			game = params.get("game");
			if (params.containsKey("type")) {
				type = params.get("type");
			}

			if (game.length() > CdkService.CDK_HEADER_SIZE) {
				game = game.substring(0, CdkService.CDK_HEADER_SIZE);
			}

			if (type != null && type.length() > CdkService.CDK_HEADER_SIZE) {
				type = type.substring(0, CdkService.CDK_HEADER_SIZE);
			}

		} catch (Exception e) {
			MyException.catchException(e);
			return false;
		}
		return true;
	}
}
