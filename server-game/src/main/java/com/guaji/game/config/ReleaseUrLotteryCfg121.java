package com.guaji.game.config;

import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
* @author 作者 E-mail:Jeremy@163.com
* @version 创建时间：Mar 15, 2019 11:44:16 AM
* 类说明
*/
/**
 * @author hanchao
 *
 */
@ConfigManager.XmlResource(file = "xml/releaseURLottery121.xml", struct = "map")
public class ReleaseUrLotteryCfg121 extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	private final int Id;
	
	/**
	 * 概率开始值
	 */
	private final int startRand;
	
	/**
	 * 概率终止值
	 */
	private final int endRand;
	
	/**
	 * 奖励物品
	 */
	private final String awards;
	
	

	public ReleaseUrLotteryCfg121() {
		startRand=0;
		endRand=0;
		Id=0;
		awards="";
	}

	@Override
	protected boolean assemble() {
		// TODO Auto-generated method stub
		return super.assemble();
	}

	@Override
	protected boolean checkValid() {
		// TODO Auto-generated method stub
		return super.checkValid();
	}

	public int getStartRand() {
		return startRand;
	}

	public int getEndRand() {
		return endRand;
	}

	public String getAwards() {
		return awards;
	}

	public static ReleaseUrLotteryCfg121  getRUrLotteryCfgByRand(int Rand)
	{
		Map<Object, ReleaseUrLotteryCfg121> configMap = ConfigManager.getInstance().getConfigMap(ReleaseUrLotteryCfg121.class);
		//遍历map中的值 
		for (ReleaseUrLotteryCfg121 value : configMap.values()) {
			if(Rand>=value.getStartRand() && Rand<value.getEndRand())
			{
				return value;
			}
		}
		return null;
	}

	public int getId() {
		return Id;
	}


	
}
