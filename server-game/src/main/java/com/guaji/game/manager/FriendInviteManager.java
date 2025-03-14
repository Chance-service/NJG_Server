package com.guaji.game.manager;

import java.util.Random;

import org.guaji.app.AppObj;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.util.GsConst;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 好友邀请管理器
 *
 */
public class FriendInviteManager extends AppObj{
	private static FriendInviteManager instance = null;
	
	public FriendInviteManager(GuaJiXID xid) {
		super(xid);
		
		if(instance == null){
			instance = this;
		}
	}
	
	public static FriendInviteManager getInstance() {
		return instance;
	}
	
	public boolean init(){
		return true;
	}
	
	/**
	 * 生成全服邀请码, 并录入Redis
	 * @param playerId
	 * @return
	 */
	public String genInviteCode(){
		String rawInviteCode = randomString(8, null);
		String inviteCodeKey = String.format(GsConst.RedisCacheObjKey.PLAYER_INVITECODE_KEY_FMT, rawInviteCode);
		
		JedisPool jedisPool = GsApp.getInstance().getJedisPool();
		try (Jedis jedis = jedisPool.getResource()) {
			int times = 0;
			while(jedis.exists(inviteCodeKey)){
				rawInviteCode = randomString(8, null);
				inviteCodeKey = String.format(GsConst.RedisCacheObjKey.PLAYER_INVITECODE_KEY_FMT, rawInviteCode);
				
				if (++times >= 9) break;
			}
			jedis.set(inviteCodeKey, "0");
		}
		return rawInviteCode;
	}
	
	/**
	 * 根据给定的字符序列生成随机长度字符串
	 * 
	 * @param length
	 * @return
	 */
	public static String randomString(int length, String charSequence) {
		if(charSequence == null || charSequence.equals("")){
			charSequence = "abcdefghijklmnopqrstuvwxyz0123456789";
		}
		Random random = new Random();  
		StringBuilder builder = new StringBuilder();   
		for (int i = 0; i < length; i++) {     
		    int number = random.nextInt(charSequence.length());     
		    builder.append(charSequence.charAt(number));     
		}     
		return builder.toString();
	}
	
	/**
	 * 兑换邀请码
	 * @param inviteCode
	 * @return
	 */
	public boolean exchangeInviteCode(String friendInviteCode){
		friendInviteCode = friendInviteCode.toLowerCase();
		friendInviteCode = String.format(GsConst.RedisCacheObjKey.PLAYER_INVITECODE_KEY_FMT, friendInviteCode);
		JedisPool jedisPool = GsApp.getInstance().getJedisPool();
		// Jedis implements Closable. Hence, the jedis instance will be auto-closed after the last statement.
		try (Jedis jedis = jedisPool.getResource()) {
			String times = jedis.get(friendInviteCode);
			if(times != null){
				times = String.valueOf(Integer.valueOf(times) + 1);
				jedis.set(friendInviteCode, times);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取邀请好友数量
	 * @param myInviteCode
	 * @return
	 */
	public int getInviteFriendAmount(String myInviteCode){
		myInviteCode = myInviteCode.toLowerCase();
		myInviteCode = String.format(GsConst.RedisCacheObjKey.PLAYER_INVITECODE_KEY_FMT, myInviteCode);
		JedisPool jedisPool = GsApp.getInstance().getJedisPool();
		try (Jedis jedis = jedisPool.getResource()) {
			String amount = jedis.get(myInviteCode);
			if(amount != null){
				return Integer.valueOf(amount);
			}
		}
		return 0;
	}
}
