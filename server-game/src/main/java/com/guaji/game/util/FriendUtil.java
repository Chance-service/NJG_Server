package com.guaji.game.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.guaji.os.GuaJiRand;

import com.guaji.game.GsApp;
import com.guaji.game.ServerData;
import com.guaji.game.entity.IpAddrEntity;
import com.guaji.game.manager.ArenaManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.RedisUtil.FBFriendBean;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 好友帮助类（tzy）
 * 
 * @author tianzhiyuan
 *
 */
public class FriendUtil {

	// 刷新Facebook好友信息
	public static void refreshFBFriendInfo(Player player) {
		if (GJLocal.isLocal(GJLocal.R2)) {
			JedisPool jedisPool = GsApp.getInstance().getJedisPool();
			try (Jedis jedis = jedisPool.getResource()) {
				FBFriendBean bean = RedisUtil.getRedisObj(jedis, FBFriendBean.class, player.getPuid());
				if (bean != null) {
					bean.setLevel(player.getLevel());
					bean.setVip(player.getVipLevel());
					bean.setFightValue(PlayerUtil.calcAllFightValue(player.getPlayerData()));
					bean.setArenaRank(ArenaManager.getInstance().getRankByPlayerId(player.getId()));
					bean.putRedis(jedis);
				}
			}
		}

	}
	
	public static Collection<Integer> randomFriend(Set<Integer> exculdeId) {
		Set<Integer> target = new HashSet<>();

		Set<Integer> sourcePidSet = new HashSet<>();
		
		Set<Integer> onlineSet = ServerData.getInstance().getOnlineMap().keySet();
		
		sourcePidSet.addAll(onlineSet);

		sourcePidSet.removeAll(exculdeId);
		
		if(sourcePidSet.size() < GsConst.FRIEND_RECOMMAND_SIZE) {
			target.addAll(sourcePidSet);
			sourcePidSet.clear();
			
			Set<Integer> allPlayerSet = ServerData.getInstance().getPlayerIdToPuid().keySet();
					//查找全人类
			sourcePidSet.addAll(allPlayerSet);
			sourcePidSet.removeAll(target);
			sourcePidSet.removeAll(exculdeId);
			
			if(sourcePidSet.size() <= (GsConst.FRIEND_RECOMMAND_SIZE - target.size())) {
			// 伺服器人太少沒辦法了
				target.addAll(sourcePidSet);
				return target;
			}else{
				target.addAll(random(sourcePidSet,GsConst.FRIEND_RECOMMAND_SIZE - target.size()));
				return target;
			}
			
		} else {
			return random(sourcePidSet, GsConst.FRIEND_RECOMMAND_SIZE);
		}
	}
	
	private static List<Integer> random(Set<Integer> playerIdSet, int count) {
		List<Integer> source = new ArrayList<>(playerIdSet.size());
		source.addAll(playerIdSet);
		List<Integer> target = new ArrayList<>(count);
		for(int i =0;i < count;i++) {
			int index = GuaJiRand.randInt(source.size() - 1);
			Integer value = source.get(index);
			target.add(value);
			source.remove(index);
		}		return target;
	}
}
