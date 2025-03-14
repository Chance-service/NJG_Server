package com.guaji.game.module.activity.vipPackage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.guaji.config.ConfigManager;
import org.guaji.log.Log;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.VipPackageCfg;

public class VipPackageStatus {
	

	private HashMap<Integer,Date> packetTable;
		
	public VipPackageStatus()
	{
		packetTable = new HashMap<Integer,Date>();

	}
	
	/**
	 * 移出已购买列表中的某个物品
	 */
	public void removePacket(int id)
	{
		if(packetTable.containsKey(id))
		{
			packetTable.remove(id);
		}
		return;
	}
		
	/**
	 * 领取礼包
	 */
	public boolean getPacket(int id)
	{
		if(packetTable.containsKey(id))
		{
			return false;
		}
		else
		{
			Date time = new Date(GuaJiTime.getMillisecond());
			
			packetTable.put(id, time);
			return true;
		}
	}
	
	/**
	 * 是否红点提示，当前vip状态下，存在可以领取的礼包
	 */
	public boolean showRedPoint(int vipLevel)
	{
		Map<Object, VipPackageCfg> cfgList = ConfigManager.getInstance().getConfigMap(VipPackageCfg.class);

		for(VipPackageCfg cfg:cfgList.values())
		{
			if(cfg.getVipLimit() > vipLevel)
			{
				continue;
			}
			
			if(!packetTable.containsKey(cfg.getId()))
			{
				return true;
			}

		}
		
		return false;
	}
	
	/**
	 * 获取某个id礼包的领取时间
	 */
	public long getPacketGetTime(int id)
	{
		if(packetTable.containsKey(id))
		{
			if(packetTable.get(id) == null)
			{
				return 0;
			}
			
			return packetTable.get(id).getTime();
		}
		
		Log.logPrintln("得到非法id时间，id为"+String.valueOf(id));
		//log报错
		return -1;
	}
	
	/**
	 * 获取当前信息
	 */
	public HashMap<Integer,Date> getInfo()
	{
		return packetTable;
	}
}
