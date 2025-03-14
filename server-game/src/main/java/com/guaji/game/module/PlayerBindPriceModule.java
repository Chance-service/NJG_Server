package com.guaji.game.module;


import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.MyException;

import com.guaji.game.GsApp;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.manager.MailManager;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Mail;
import com.guaji.game.protocol.Player.HPPlayerBindPrice;
import com.guaji.game.protocol.Player.HPPlayerBindPriceRet;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 账号绑定奖励模块
 */
public class PlayerBindPriceModule extends PlayerModule {
	/**
	 * 构造函数
	 * 
	 * @param player
	 */
	public PlayerBindPriceModule(Player player) {
		super(player);

		listenProto(HP.code.PLAYER_BIND_PRICE_C);
	}

	/**
	 * 消息响应
	 * 
	 * @param msg
	 * @return
	 */
	@Override
	public boolean onMessage(Msg msg) {
		return super.onMessage(msg);
	}

	/**
	 * 协议响应
	 * 
	 * @param protocol
	 * @return
	 */
	@Override
	public boolean onProtocol(Protocol protocol) {
		if (protocol.checkType(HP.code.PLAYER_BIND_PRICE_C)) {
			playerBindPriceEvent(protocol.parseProtocol(HPPlayerBindPrice.getDefaultInstance()));
			return true;
		}
		return super.onProtocol(protocol);
	}

	/**
	 * 账号绑定奖励
	 * 
	 * @param protocol
	 * @return
	 */
	protected void playerBindPriceEvent(HPPlayerBindPrice protocol) 
	{
		
		JedisPool jedisPool = GsApp.getInstance().getJedisPool();
		Jedis jedis = null;
		boolean flag = true;
		
		synchronized(jedisPool)
		{
			try
			{
				jedis = jedisPool.getResource();
				flag = false;
				HPPlayerBindPriceRet.Builder ret = HPPlayerBindPriceRet.newBuilder();
				String key = player.getPlayerData().getPlayerEntity().getServerId() +"-"+ player.getPlayerData().getPlayerEntity().getPuid();
				if(jedis.exists(key)) {
					ret.setStatus(Player.PLAYER_INF_FAIL);
				}else {
					String rewards = SysBasicCfg.getInstance().getPlayerBindRewards();
					AwardItems awardItems = AwardItems.valueOf(rewards);
					MailManager.createSysMail(player.getEntity().getId(), Mail.MailType.Reward_VALUE, GsConst.MailId.PLAYER_BIND_PRICE, "", awardItems);
					jedis.set(key, "1");
					ret.setStatus(Player.PLAYER_INF_SUCCESS);
				}
				
				jedisPool.returnResource(jedis);
			
				sendProtocol(Protocol.valueOf(HP.code.PLAYER_BIND_PRICE_S, ret));
			}
			catch(Exception e)
			{
				MyException.catchException(e);
				if(flag)
				{
					if(jedis != null)
					{
						jedisPool.returnBrokenResource(jedis);
					}
				}
				else
				{
					if(jedis != null)
					{
						jedisPool.returnResource(jedis);
					}
				}

			}
		}
		return;
		
	}

}
