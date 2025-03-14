package com.guaji.game.config;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

@ConfigManager.XmlResource(file = "xml/GloryHoleQuest.xml", struct = "map")
public class GloryHoleMissionCfg extends ConfigBase {
	
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	
	/**
	 * 领奖所需累计充值数额
	 */
	private final int type;
	/**
	 * 目標次數
	 */
	private final int target;
	/**
	 * 奖励
	 */
	private final String awards;
	/**
	 * 所有
	 */
	private static Set<Integer> allType = new HashSet<>();
	
	public GloryHoleMissionCfg() {
		this.id = 0;
		this.type = 0;
		this.target = 0;
		this.awards = "";
		allType.clear();
	}

	public int getId() {
		return id;
	}
	
	public int getType() {
		return type;
	}
	
	public int getTarget() {
		return target;
	}

	public String getAwards() {
		return awards;
	}

	public static Set<Integer> getAllType(){
		return allType;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
				
		allType.add(getType());
		
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
	
	/**
	 * 取出獎勵
	 * @param type
	 * @param count
	 * @return
	 */
	public static String getAwardbyTypeCount(int type ,int count) {
		if (!getAllType().contains(type)){
			return "";
		}
        Map<Object, GloryHoleMissionCfg> cfgList = ConfigManager.getInstance().getConfigMap(GloryHoleMissionCfg.class);
        for (GloryHoleMissionCfg cfgItem : cfgList.values()) {
        	if ((cfgItem.getType() == type)&&(cfgItem.getTarget() == count)){
        		return cfgItem.getAwards();
        	}
        }
		return "";
		
	}
}
