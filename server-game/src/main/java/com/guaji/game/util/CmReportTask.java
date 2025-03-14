package com.guaji.game.util;

import org.guaji.cache.CacheObj;
import org.guaji.thread.GuaJiTask;

import com.guaji.game.cmreport.CmReportManager;
import com.guaji.game.player.Player;

public class CmReportTask extends GuaJiTask {
	/**
	 * 上报玩家信息
	 */
	private Player player;
	
	/**
	 * 构造
	 * 
	 * @param player
	 */
	public CmReportTask(Player player) {
		this.player = player;
	}
	
	@Override
	protected int run() {
		CmReportManager.getInstance().reportCmActivePlayer(player, true);
		return 0;
	}

	@Override
	protected CacheObj clone() {
		return null;
	}
}
