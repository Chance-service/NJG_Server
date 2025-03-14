package com.guaji.game.module.yaya;

import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;

/**
 * 呀呀语音模块;
 */
public class PlayerYYModule extends PlayerModule {

	public PlayerYYModule(Player player) {
		super(player);
	}

	@Override
	protected boolean onPlayerLogin() {
//		player.getPlayerData().syncYayaInfo();
		return super.onPlayerLogin();
	}

	/**
	 * 获取YY兑换金豆信息
	 * 
	 * @param protocol
	 * @return
	 */
//	@ProtocolHandlerAnno(code = HP.code.YAYA_EXCHANGE_PAGE_C_VALUE)
//	protected void onGetYAYAInfo(Protocol hawkProtocol) {
//		int todayRechargeNum = player.getPlayerData().getPlayerEntity().getTodayRechargeNum();
//		int todyExchangeGoldBeanCost = player.getPlayerData().getPlayerEntity().getExchangeGoldBeanCostRmbGold();
//		HPExchangeGoldBeanPageRet.Builder ret = HPExchangeGoldBeanPageRet.newBuilder();
//		if (todyExchangeGoldBeanCost > todayRechargeNum) {
//			// 做一个容错修正
//			todyExchangeGoldBeanCost = todayRechargeNum;
//		}
//		ret.setTodayRechargeGold(todayRechargeNum);
//		ret.setTodayExchangeCostGold(todyExchangeGoldBeanCost);
//		ret.setGoldBean(player.getGoldBean());
//		player.sendProtocol(Protocol.valueOf(HP.code.YAYA_EXCHANGE_PAGE_S_VALUE, ret));
//	}
}
