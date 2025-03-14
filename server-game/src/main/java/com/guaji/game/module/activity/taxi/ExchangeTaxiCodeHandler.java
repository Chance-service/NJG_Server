package com.guaji.game.module.activity.taxi;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;

import com.guaji.game.protocol.Activity.HPExchangeTaxiCode;
import com.guaji.game.protocol.Activity.HPExchangeTaxiCodeRet;
import com.guaji.game.GsApp;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class ExchangeTaxiCodeHandler implements IProtocolHandler {

	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		Player player = (Player) appObj;
		// 检测活动是否开放
		int activityId = Const.ActivityId.TAXI_CODE_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}
		
		if(player.getLevel() < SysBasicCfg.getInstance().getTaxiLimitLevel()){
			// 等级不足
			player.sendError(protocol.getType(), Status.error.LEVEL_NOT_LIMIT_VALUE);
			return true;
		}
		
		HPExchangeTaxiCode req = protocol.parseProtocol(HPExchangeTaxiCode.getDefaultInstance());
		String key = req.getGameKey();
		if(key == null || key.equals("") || !key.equals(SysBasicCfg.getInstance().getTaxiCodeKey())){
			player.sendError(protocol.getType(), Status.error.TAXI_KEY_ERROR);
			return true;
		}
		
		// 设备id验证
		String deviceId = player.getEntity().getDevice().trim();
		deviceId = String.format(GsConst.RedisCacheObjKey.TAXI_DEVICE_ID_FMT, deviceId);
		JedisPool jedisPool = GsApp.getInstance().getJedisPool();
		try(Jedis jedis = jedisPool.getResource()){
			if(jedis.exists(deviceId)){
				//设备已经兑换过
				player.sendError(protocol.getType(), Status.error.ALREADY_EXCHANGE_CODE);
				return true;
			}
		}
		
		TaxiCodeStatus taxiStatus = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, 
				timeCfg.getStageId(), TaxiCodeStatus.class);
		if(taxiStatus.getMyTaxiCode().equals(SysBasicCfg.getInstance().getTaxiCodeKey())){
			try(Jedis jedis = jedisPool.getResource()){
				Long num = jedis.llen(GsConst.RedisCacheObjKey.TAXI_EXCHANGE_CODE_KEY);
				if(num > 0){
					String myTaxiCode = jedis.lpop(GsConst.RedisCacheObjKey.TAXI_EXCHANGE_CODE_KEY);
					jedis.set(deviceId, myTaxiCode);
					taxiStatus.setMyTaxiCode(myTaxiCode);
					player.getPlayerData().updateActivity(activityId, timeCfg.getStageId());
				}
			}
		}
		
		HPExchangeTaxiCodeRet.Builder ret = HPExchangeTaxiCodeRet.newBuilder();
		ret.setIsExchanged(taxiStatus.isExchange()?1:0);
		if(taxiStatus.isExchange()){
			ret.setTaxiCode(taxiStatus.getMyTaxiCode());
		}
		player.sendProtocol(Protocol.valueOf(HP.code.EXCHANGE_TAXI_CODE_S, ret));
		return true;
	}
}
