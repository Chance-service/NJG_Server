package GameServerManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.redisserver.JediseConfig;
import com.redisserver.ServiceContext;

import redis.clients.jedis.Jedis;

public class GameServerDataCenter {
	
	private static GameServerDataCenter instance = null;
	
	private final Logger logger = Logger.getLogger(JediseConfig.class.getName());

	
	private static ConcurrentHashMap<String, Integer> serverStatus;
	private static ConcurrentHashMap<String, Integer> serverScore;
	private static ConcurrentHashMap<String, Integer> serverOnlinePlayerCount;


	public static GameServerDataCenter getInstance()
	{
		if(instance != null)
		{
			return instance;
		}
		
		instance = new GameServerDataCenter();
		
		return instance;
	}
	
	public void initData()
	{
		serverStatus = new ConcurrentHashMap<String, Integer>();
		serverScore = new ConcurrentHashMap<String, Integer>();
		serverOnlinePlayerCount = new ConcurrentHashMap<String, Integer>();
		//暂时不做硬存储
		
//		ConcurrentHashMap<String,Integer> socreData = new ConcurrentHashMap<String,Integer>();
//		
//		if(ServiceContext.jedisPool == null)
//		{			
//			return;
//		}
//		
//		int count = 0;
//		
//		synchronized (ServiceContext.jedisPool) 
//		{
//			try
//			{
//				Jedis jedis = ServiceContext.jedisPool.getResource();
//				
//				if(jedis == null)
//				{
//					return;
//				}
//				
//				if(jedis.exists(gameServer))
//				{
//					serverData = new ConcurrentHashMap<String, HashMap<String, Integer>>();
//					
//					if(jedis.hkeys(gameServer).size() !=0)
//					{
//						for(String key:jedis.hkeys(gameServer))
//						{
//							switch(key)
//							{
//								case score:
//									break;
//								case onlinePlayerCount:
//									break;
//								default:
//									logger.error("other no exist key: " + key);
//									break;
//							}
//
//						}
//					}
//					else
//					{
//						
//					}
//
//				}
//				
//			}
//			catch(Exception e)
//			{
//				
//				logger.error("读取总表数据错误！");
//				return;
//			}
//		}
//		
//		logger.info("服务器数据加载结束，共完成加载条目"+count+"个.");
//		
		ServiceContext.setGameServerDataCenterState(true);	
		return;
	}
	
	public void modifyServerScore(String serverId,int score)
	{
		serverScore.put(serverId, score);
		return;
	}
	
	public int getServerScore(String serverId)
	{
		if(serverScore.containsKey(serverId))
		{
			return serverScore.get(serverId);
		}
		else
		{
			return 0;
		}
	}
	
	public int getAllServerScore()
	{
		int totalScore = 0;
		
		synchronized(serverScore)//虽然不做modify，但是保持暂不变更
		{
/**
 * 1.8 need		
 */
//			Iterator<String> iterator = serverScore.keySet().iterator();
//			
//			while(iterator.hasNext())
//			{
//				totalScore += serverScore.get(iterator.next());
//			}
			
			Iterator<Integer> iterator = serverScore.values().iterator();
			
			while(iterator.hasNext())
			{
				totalScore += iterator.next();
			}

		}
		
		return totalScore;
	}
	
	@SuppressWarnings("unchecked")
	public void cleanAllServerScore()
	{
		synchronized(serverScore)//虽然不做modify，但是保持暂不变更
		{
		
			Iterator<String> iterator = (Iterator<String>) serverScore.keys();
			
			while(iterator.hasNext())
			{
				modifyServerScore(iterator.next(),0);
			}
		}
	}

}
