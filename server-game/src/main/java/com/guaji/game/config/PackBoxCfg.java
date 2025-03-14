package com.guaji.game.config;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.DropItems;

@ConfigManager.XmlResource(file = "xml/packbox.xml", struct = "map")
public class PackBoxCfg extends ConfigBase {
	
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	
	/**
	 * 奖励
	 */
	private final String item;
	/**
	 * 轉換掉落物
	 */
	private final DropItems BoxDropItems;

	public PackBoxCfg() {
		this.id = 0;
		this.item = "";
		this.BoxDropItems = new DropItems();
	}

	public int getId() {
		return id;
	}
	
	public String getItem() {
		return item;
	}
	
	public DropItems getBoxDropItems () {
		return BoxDropItems;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		
		if (!BoxDropItems.initByString(item)) {
			return false;
		}
		return true;
	}

	/**
	 * 检测有消息
	 * 
	 * @return
	 */
	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
