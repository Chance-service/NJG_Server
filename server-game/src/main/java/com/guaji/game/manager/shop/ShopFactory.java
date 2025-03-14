package com.guaji.game.manager.shop;

import com.guaji.game.manager.shop.strategy.AllianceMarket;
import com.guaji.game.manager.shop.strategy.ArenaMarket;
import com.guaji.game.manager.shop.strategy.CoinsMarket;
import com.guaji.game.manager.shop.strategy.CrossMarket;
import com.guaji.game.manager.shop.strategy.CrystalMarkey;
import com.guaji.game.manager.shop.strategy.CycleStageMarket;
import com.guaji.game.manager.shop.strategy.DailyMarket;
import com.guaji.game.manager.shop.strategy.MysteryMarket;
import com.guaji.game.manager.shop.strategy.RaceMarket;
import com.guaji.game.manager.shop.strategy.SkinMarket;
import com.guaji.game.manager.shop.strategy.TempleMarket;
import com.guaji.game.manager.shop.strategy.GemMarkey;
import com.guaji.game.manager.shop.strategy.GodSeaMarket;
import com.guaji.game.manager.shop.strategy.GuildMarket;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.ShopType;

/**
 * 商店简单加工类
 * 
 * @author zdz
 */
public class ShopFactory {

	/**
	 * 工厂单例
	 */
	private static final ShopFactory instance = new ShopFactory();

	public static ShopFactory getInstance() {
		return instance;
	}

	/**
	 * 获取服务实例对象
	 * 
	 * @param shopType
	 * @return
	 */
	public ShopScervice<?> getShopHandler(ShopType shopType) {

		switch (shopType.getNumber()) {

		case ShopType.COINS_MARKET_VALUE:
			return CoinsMarket.getInstance();

		case ShopType.ALLIANCE_MARKET_VALUE:
			return AllianceMarket.getInstance();

		case ShopType.ARENA_MARKET_VALUE:
			return ArenaMarket.getInstance();

		case ShopType.CRYSTAL_MARKET_VALUE:
			return CrystalMarkey.getInstance();
						
		case ShopType.GEM_MARKET_VALUE:
			return GemMarkey.getInstance();
			
		case ShopType.DAILY_MARKET_VALUE:
			return DailyMarket.getInstance();
			
		case ShopType.MYSTERY_MARKET_VALUE:
			return MysteryMarket.getInstance();
			
		case ShopType.GUILD_MARKET_VALUE:
			return GuildMarket.getInstance();
			
		case ShopType.CROSS_MARKET_VALUE:
			return CrossMarket.getInstance();
			
		case ShopType.TEMPLE_MARKET_VALUE:
			return TempleMarket.getInstance();
			
		case ShopType.RACE_MARKET_VALUE:
			return RaceMarket.getInstance();
			
		case ShopType.GODSEA_MARKET_VALUE:
			return GodSeaMarket.getInstance();
		case ShopType.SKIN_MARKET_VALUE:
			return SkinMarket.getInstance();
			
		case ShopType.CYCLESTAGE_MARKET_VALUE:
			return CycleStageMarket.getInstance();
			
		default:
			return null;
		}
	}
}
