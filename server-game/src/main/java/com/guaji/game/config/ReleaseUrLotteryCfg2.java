package com.guaji.game.config;

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
@ConfigManager.XmlResource(file = "xml/releaseURLottery2.xml", struct = "map")
public class ReleaseUrLotteryCfg2 extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	private final int Id;
		
	/**
	 * 奖励物品
	 */
	private final String awards;
	
	

	public ReleaseUrLotteryCfg2() {
	
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


	public String getAwards() {
		return awards;
	}

	
	public int getId() {
		return Id;
	}


	
}
