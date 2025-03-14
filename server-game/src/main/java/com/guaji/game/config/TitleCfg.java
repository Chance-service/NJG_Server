package com.guaji.game.config;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;


@ConfigManager.XmlResource(file = "xml/titleCondition.xml", struct = "map")
public class TitleCfg extends ConfigBase{
	
	@Id
	protected final int id;
	protected final String title;
	protected final String condition;
	protected final int type;
	
	private int[] condit;

	public TitleCfg() {
		this.id = 0;
		this.condition = "";
		this.title = "";
		this.type = 0;
	}


	public String getTitle() {
		return title;
	}

	public int getType() {
		return type;
	}


	public int getId() {
		return id;
	}

	public String getCondition() {
		return condition;
	}

	@Override
	protected boolean assemble() {
		String[] conds = condition.split(",");
		condit = new int[conds.length];
		for(int i = 0; i < conds.length; i++) {
			condit[i] = Integer.parseInt(conds[i]);
		}
		return super.assemble();
	}

	@Override
	protected boolean checkValid() {
		return super.checkValid();
	}
	
	public int[] getCondit() {
		return condit;
	}


	public void setCondit(int[] condit) {
		this.condit = condit;
	}
	
	/**
	 * 通过称号类型和条件值获得相应称号（单个条件值必须等于要求值，两个值取中间值）
	 * @param type
	 * @param condition
	 * @return
	 */
	public static int getTitleIdByTypeAndCondition(int type, int condition) {
		int ret = 0;
		Map<Object, TitleCfg> cfgMap = ConfigManager.getInstance().getConfigMap(TitleCfg.class);
		for (Map.Entry<Object, TitleCfg> entry : cfgMap.entrySet()) {
			TitleCfg cfg = entry.getValue();
			if(cfg.getType() == type) {
				int[] cond = cfg.getCondit();
				if(cond.length == 2) {
					if(condition >= cond[0] && condition <= cond[1]) {
						ret = cfg.getId();
						break;
					}
				} else if(cond.length == 1) {
					if(condition == cond[0]) {
						ret = cfg.getId();
						break;
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * 通过称号类型和条件值获得相应称号列表（条件值大于等于要求值即可）
	 * @param type
	 * @param condition
	 * @return
	 */
	public static List<Integer> getFinishTitleIdsByType(int type, int condition) {
		Map<Object, TitleCfg> cfgMap = ConfigManager.getInstance().getConfigMap(TitleCfg.class);
		List<TitleCfg> cfgList = new ArrayList<TitleCfg>();
		for(Map.Entry<Object, TitleCfg> entry : cfgMap.entrySet()) {
			TitleCfg cfg = entry.getValue();
			if(cfg.getType() == type) {
				cfgList.add(cfg);
			}
		}
		List<Integer> ret = new ArrayList<Integer>();
		for(int i = 0; i < cfgList.size(); i++) {
			if(cfgList.get(i).getCondit()[0] <= condition) {
				ret.add(cfgList.get(i).getId());
			}
		}
		
		return ret;
	}
	
	/**
	 * 通过称号类型和条件值获得相应称号（条件值大于等于要求值即可）
	 * @param type
	 * @param condition
	 * @return
	 */
	public static int getTitleId(int type, int condition) {
		int ret = 0;
		Map<Object, TitleCfg> cfgMap = ConfigManager.getInstance().getConfigMap(TitleCfg.class);
		for (Map.Entry<Object, TitleCfg> entry : cfgMap.entrySet()) {
			TitleCfg cfg = entry.getValue();
			if(cfg.getType() == type) {
				int[] cond = cfg.getCondit();
				if(cond.length == 2) {
					if(condition >= cond[0] && condition <= cond[1]) {
						ret = cfg.getId();
						break;
					}
				} else if(cond.length == 1) {
					if(condition >= cond[0]) {
						ret = cfg.getId();
						break;
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
