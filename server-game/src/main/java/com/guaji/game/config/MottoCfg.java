package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.attribute.Attribute;
import com.guaji.game.player.PlayerData;

@ConfigManager.XmlResource(file = "xml/Motto.xml", struct = "map")
public class MottoCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	/*
	 * 基本屬性
	 */
	private final String basic;
	/**
	 * 被動屬性
	 */
	private final String passive;
	/**
	 * 類型
	 */
	private final int type;
	/**
	 * 對應星等最大增值量
	 */
	private final String times;
	/**
	 * 升星所需碎片數量
	 */
	private final String cost;
	/**
	 * 所需點數
	 */
	private final String points;
    /**
        * 基础属性
     */
    protected Attribute basicAttribute;
    /**
     * 星等屬性列表 
     */
    protected List<Attribute> PassiveAttribute;
    /**
       *  最大增量列表
     */
    protected List<Integer> MaxTimesList;
    /**
     *  升星消耗碎片列表
   */
    protected List<Integer> CostList;
    /**
     *  點數列表
   */
    protected List<Integer> PointsList;
	
	public MottoCfg() {
		this.id = 0;
		this.basic = "";
		this.passive = "";
		this.type = 0;
		this.times = "";
		this.cost = "";
		this.points="";
		PassiveAttribute = new ArrayList<>();
		basicAttribute = new Attribute();
		MaxTimesList = new ArrayList<>();
		CostList = new ArrayList<>();
		PointsList = new ArrayList<>();
	}
	
    @Override
    protected boolean assemble() {
        // 初始化基础属性
        basicAttribute.clear();
        if (basic != null && basic.length() > 0) {
        	basicAttribute = Attribute.valueOf(basic);
        }
        PassiveAttribute.clear();
        if (passive != null && passive.length() > 0) {
        	String[] ss = passive.split(";");
        	for (String s : ss) {
        		Attribute attr = Attribute.valueOf(s);
        		PassiveAttribute.add(attr);
        	}
        }
        MaxTimesList.clear();
        if (times != null && times.length() > 0) {
        	String[] ss = times.split(",");
        	for (String s : ss) {
        		MaxTimesList.add(Integer.valueOf(s.trim()));
        	}
        }
        
        CostList.clear();
        if  (cost != null && cost.length() > 0) {
        	String[] ss = cost.split(",");
        	for (String s : ss) {
        		CostList.add(Integer.valueOf(s.trim()));
        	}
        }
        
        PointsList.clear();
        if  (points != null && points.length() > 0) {
        	String[] ss = points.split(",");
        	for (String s : ss) {
        		PointsList.add(Integer.valueOf(s.trim()));
        	}
        }
        
        return true;
    }
    
    public int getId() {
    	return this.id;
    }
    
    public String getBasicStr() {
    	return this.basic;
    }
    
    public String getPassiveStr() {
    	return this.passive;
    }
    
    public int getType() {
    	return this.type;
    }
    
    public String getTimesStr() {
    	return this.times;
    }
    
    public int getActivation() {
    	return this.CostList.get(0);
    }
    
    public int getMaxStar() {
    	return this.CostList.size();
    }
    
    public int getCostByStar(int star) {
    	if(star >= getMaxStar() || star < 0) {
    		return this.CostList.get(getMaxStar()-1);
    	}
    	return this.CostList.get(star);
    }
    
    public String getCostStr() {
    	return this.cost;
    }
    
    public String getPointsStr() {
    	return this.points;
    }
	
    public Attribute getBasicAttribute() {
        return basicAttribute;
    }
    
    public Attribute getAttributeByStar(PlayerData playerData,int star) {
    	Attribute attr = null;
    	if ((star > 0) && (star <= getMaxStar())) {
    		attr = PassiveAttribute.get(star-1).clone();
	    	int times = 0;
			switch (this.type) {
			case 1:
				times = Math.min(MaxTimesList.get(star-1),playerData.getStateEntity().getFirstLoginTimes());
				break;
			case 2:
				times = Math.min(MaxTimesList.get(star-1), playerData.getStateEntity().getAccConsumeGold());
				break;
			case 3:
				times = Math.min(MaxTimesList.get(star-1), playerData.getStateEntity().getPassMapId()/5);
				break;
			case 4:
				times = Math.min(MaxTimesList.get(star-1),playerData.getStateEntity().getFastFightTimes());
				break;
			case 5:
				times = Math.min(MaxTimesList.get(star-1), playerData.getStateEntity().getArenaWinTimes());
				break;
			case 6:
				times = Math.min(MaxTimesList.get(star-1), playerData.getStateEntity().getOrdealFloor());
				break;
			case 7:
				times = Math.min(MaxTimesList.get(star-1), playerData.getStateEntity().getMiningLevel());
				break;
			default:
				break;
			}
			attr.multiplicate(times);
    	}
    	return attr;
    }
    
	/**
	 * 獲取所有箴言資訊
	 * 
	 * @return
	 */
	public static Map<Integer, MottoCfg> getMottoInfoMap() {
		Map<Integer, MottoCfg> map = new HashMap<Integer, MottoCfg>();

		Map<Object, MottoCfg> MottoMap = ConfigManager.getInstance().getConfigMap(MottoCfg.class);
		for (MottoCfg cfg : MottoMap.values()) {
				map.put(cfg.getId(), cfg);
		}
		return map;
	}
	
}
