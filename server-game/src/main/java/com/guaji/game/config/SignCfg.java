package com.guaji.game.config;

import java.util.LinkedList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/sign.xml", struct = "map")
public class SignCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	
	/**
	 * 類型
	 */
	private final int type;
	/**
	 * 增修權限限制 0.S 1.S,C
	 */
	private final int limit;
	
	
	private static List<Integer> cContral = new LinkedList<>();
	
	
	public SignCfg() {
		this.id = 0;
		this.type = 0;
		this.limit = 0;
	}

	public int getId() {
		return id;
	}
	
	public int getType() {
		return type;
	}

	public int getLimit() {
		return limit;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		if (this.limit == 1) {
			cContral.add(this.id);
		}
		return true;
	}

	/**
	 * 检测有消息
	 * 
	 * @return
	 */
	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
	
	/**
	 * 檢查client端是否有此標記的權限
	 * @param signId
	 * @return
	 */
	public static boolean checkCcontral(int signId) {
		return cContral.contains(signId);
	}
		
}
