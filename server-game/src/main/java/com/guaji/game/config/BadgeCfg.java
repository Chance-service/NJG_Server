package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;

import com.guaji.game.attribute.Attribute;

@ConfigManager.XmlResource(file = "xml/badge.xml", struct = "map")
public class BadgeCfg extends ConfigBase {

    /**
     *	 配置id
     */
    @Id
    protected final int id;
    /**
     *	階級
    */
    protected final int rank;
    /**
     *	 下一階Id
     */
    protected final int afterid; 
    /**
     * 基础属性
     */
    protected final String basicAttr;
    /**
     * 附加隨機技能
     */
    protected final String randomAttr;
    /**
     * 附加隨機技能
     */
    protected final String starAttr;
    /**
     *	 洗鍊槽數
     */
    protected final int slots;
    /**
     *	 技能池表badgeList對應ID
     */
    protected final int skillpool;
    /**
     *	 開關
     */
    protected final int unlocksp;

    /**
     * 基础属性
     */
    protected Attribute basicAttribute;

    /**
        * 隨機技能ID
     */
    protected List<Integer> SkillRand;
    
    /**
     * 技能權重列表
    */
    protected List<Integer> WeightList;
    /**
        * 隨機技能ID
    */
    protected List<String> AttrRand;
 
 /**
  * 技能權重列表
 */
 protected List<Integer> AttrWeight;

    public BadgeCfg() {
        id = 0;
        rank = 0;
        afterid = 0;
        basicAttr = null;
        starAttr = null;
        randomAttr = null;
        basicAttribute = new Attribute();
        SkillRand = new ArrayList<>();
        WeightList = new ArrayList<>();
        AttrRand = new ArrayList<>();
        AttrWeight = new ArrayList<>();
        slots = 0;
        skillpool = 0;
        unlocksp = 0;
    }


    public int getId() {
        return id;
    }


    public String getBasicAttr() {
        return basicAttr;
    }

    public String getStarAttr() {
        return starAttr;
    }

    public Attribute getBasicAttribute() {
        return basicAttribute;
    }

    @Override
    protected boolean assemble() {
        // 初始化基础属性
        basicAttribute.clear();
        
        if (basicAttr != null && basicAttr.length() > 0) {
        	basicAttribute = Attribute.valueOf(basicAttr);
        }

        //初始化隨機技能表
        SkillRand.clear();
        WeightList.clear();
        if (starAttr != null && starAttr.length() > 0) {
            String[] items = starAttr.split(",");
            for (String item : items) {
                if (item.split("_").length == 2) {
                	SkillRand.add(Integer.valueOf(item.split("_")[0]));
                	WeightList.add(Integer.valueOf(item.split("_")[1]));
                }
            }
        }
        
        AttrRand.clear();
        AttrWeight.clear();
        if (randomAttr != null && randomAttr.length() > 0) {
            String[] items = randomAttr.split(",");
            for (String item : items) {
                if (item.split("_").length == 3) {
                	AttrRand.add(String.format("%s_%s",item.split("_")[0],item.split("_")[1]));
                	AttrWeight.add(Integer.valueOf(item.split("_")[2]));
                }
            }
        }

        return true;
    }


    @Override
    protected boolean checkValid() {
        return true;
    }
    
	/**
	*隨機技能
	 */
	public int RandomSkill() {
		
		if (SkillRand.size() > 0) {
			return GuaJiRand.randonWeightObject(SkillRand, WeightList);	
		}
		
		return 0;
	}
	
	/**
	*隨機屬性
	 */
	public String RandomAttr() {
		
		if (AttrRand.size() > 0) {
			return GuaJiRand.randonWeightObject(AttrRand, AttrWeight);	
		}
		
		return "";
	}


	public int getRank() {
		return rank;
	}


	public int getAfterId() {
		return afterid;
	}


	public int getSlots() {
		return slots;
	}


	public int getSkillpool() {
		return skillpool;
	}


	public int getUnlocksp() {
		return unlocksp;
	}
	
}
