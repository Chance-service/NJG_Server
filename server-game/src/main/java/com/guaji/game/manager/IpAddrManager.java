package com.guaji.game.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.mina.util.ConcurrentHashSet;
import org.guaji.os.GuaJiRand;

import com.guaji.game.entity.IpAddrEntity;
import com.guaji.game.player.PlayerData;
import com.guaji.game.util.GsConst;

public class IpAddrManager {
	
	private final static IpAddrEntity EMPTY_IPADDR ;
	static {
		EMPTY_IPADDR = new IpAddrEntity();
		EMPTY_IPADDR.setBeginIpInt(0);
		EMPTY_IPADDR.setEndIpInt(0);
	}
	
	private static IpAddrManager instance = null;
	
	public static IpAddrManager getInstance() {
		if(instance == null) {
			instance = new IpAddrManager();
		}
		return instance;
	}
	
	private Map<IpAddrEntity, Set<Integer>> ipAddrMap ;
	
	/**
	 * 省份对应的所有IpAddrEntity
	 */
	private Map<Integer, List<IpAddrEntity>> ipProvince ;
	
	/**
	 * 所有玩家id
	 */
	private Set<Integer> allPlayerIdSet ;
	
	public IpAddrManager() {
		ipAddrMap = new ConcurrentHashMap<>();
		ipProvince = new ConcurrentHashMap<>();
		allPlayerIdSet = new ConcurrentHashSet<>();
	}
	
	public void add(IpAddrEntity ipAddrEntity, int playerId) {
		if(ipAddrEntity == null) {
			ipAddrEntity = EMPTY_IPADDR;
		}
		Set<Integer> playerIdSet = ipAddrMap.get(ipAddrEntity);
		if(playerIdSet == null) {
			playerIdSet = new ConcurrentHashSet<>();
			ipAddrMap.put(ipAddrEntity, playerIdSet);
		}
		allPlayerIdSet.add(playerId);
		playerIdSet.add(playerId);
		
		if(ipAddrEntity.getProvince() >= 0) {
			List<IpAddrEntity> provinceCities = ipProvince.get(ipAddrEntity.getProvince());
			if(provinceCities == null) {
				provinceCities = new CopyOnWriteArrayList<>();
				ipProvince.put(ipAddrEntity.getProvince(), provinceCities);
			}
			provinceCities.add(ipAddrEntity);
		}
	}
	
	public void remove(PlayerData playerData) {
		IpAddrEntity ipAddrEntity = playerData.getIpAddrEntity();
		if(ipAddrEntity == null) {
			ipAddrEntity = EMPTY_IPADDR;
		}
		Set<Integer> playerIdSet = ipAddrMap.get(ipAddrEntity);
		if(playerIdSet != null) {
			playerIdSet.remove(playerData.getId());
		}
	}
	
	public Collection<Integer> randomFriend(IpAddrEntity ipAddrEntity,Set<Integer> exculdeId) {
		Set<Integer> target = new HashSet<>();
		if(ipAddrEntity == null) {
			ipAddrEntity = EMPTY_IPADDR;
		}
		Set<Integer> sourcePidSet = new HashSet<>();
		//查找本市的
		List<IpAddrEntity> pList = ipProvince.get(ipAddrEntity.getProvince());
		if(pList != null) {
			for(IpAddrEntity iae : pList){
				if(iae.getCity() == ipAddrEntity.getCity()) {
					sourcePidSet.addAll(ipAddrMap.get(iae));
				}
			}
		}
		
		sourcePidSet.removeAll(exculdeId);
		if(sourcePidSet.size() < GsConst.FRIEND_RECOMMAND_SIZE) {
			target.addAll(sourcePidSet);
			sourcePidSet.clear();
			//走省 再查找
			pList = ipProvince.get(ipAddrEntity.getProvince());
			if(pList != null) {
				for(IpAddrEntity iae : ipProvince.get(ipAddrEntity.getProvince())){
					if(iae.getCity() != ipAddrEntity.getCity()) {
						sourcePidSet.addAll(ipAddrMap.get(iae));
					}
				}
			}
			
			if(sourcePidSet.size() <= (GsConst.FRIEND_RECOMMAND_SIZE - target.size())) {
				target.addAll(sourcePidSet);
				sourcePidSet.clear();
				//查找全人类
				sourcePidSet.addAll(allPlayerIdSet);
				sourcePidSet.removeAll(target);
				sourcePidSet.removeAll(exculdeId);
				
				if(sourcePidSet.size() <= (GsConst.FRIEND_RECOMMAND_SIZE - target.size())) {
					//没有办法 没那么多人在线
					target.addAll(sourcePidSet);
					return target;
				}else{
					target.addAll(random(sourcePidSet,GsConst.FRIEND_RECOMMAND_SIZE - target.size()));
					return target;
				}
			}else{
				target.addAll(random(sourcePidSet,GsConst.FRIEND_RECOMMAND_SIZE - target.size()));
				return target;
			}
			
		} else {
			return random(sourcePidSet, GsConst.FRIEND_RECOMMAND_SIZE);
		}
	}
	
	private List<Integer> random(Set<Integer> playerIdSet, int count) {
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
