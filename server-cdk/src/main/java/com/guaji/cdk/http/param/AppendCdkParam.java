package com.guaji.cdk.http.param;

import java.util.Map;

import org.guaji.os.MyException;
import org.guaji.util.services.CdkService;

/**
 * cdk生成参数
 * 
 */
public class AppendCdkParam {
	private String game;
	private String platform;
	private String type;
	private int count;

	public AppendCdkParam() {
		game = "";
		platform = "";
		type = "";
		count = 0;
	}

	public void toLowerCase() {
		if (game != null) {
			game = game.toLowerCase();
		}

		if (platform != null) {
			platform = platform.toLowerCase();
		}

		if (type != null) {
			type = type.toLowerCase();
		}
	}

	public boolean isValid() {
		return game.length() >= 2 && type.length() >= 2 && count > 0;
	}

	public String getGame() {
		return game;
	}

	public void setGame(String game) {
		this.game = game;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * 参数解析
	 * 
	 * @param params
	 * @return
	 */
	public boolean initParam(Map<String, String> params) {
		try {
			game = params.get("game");
			type = params.get("type");

			if (game.length() > CdkService.CDK_HEADER_SIZE) {
				game = game.substring(0, CdkService.CDK_HEADER_SIZE);
			}

			if (type.length() > CdkService.CDK_HEADER_SIZE) {
				type = type.substring(0, CdkService.CDK_HEADER_SIZE);
			}

			if (params.containsKey("platform")) {
				platform = params.get("platform");
			}

			count = Integer.valueOf(params.get("count"));

		} catch (Exception e) {
			MyException.catchException(e);
			return false;
		}
		return true;
	}
}
