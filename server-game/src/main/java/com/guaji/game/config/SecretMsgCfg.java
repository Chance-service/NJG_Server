package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;

@ConfigManager.XmlResource(file = "xml/SecretMessage.xml", struct = "map")
public class SecretMsgCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	/**
	 * 英雄Id
	 */
	private final int hero;
	/**
	 * 選項及對應分數
	 */
	private final String score;
	/**
	 * rand用群組
	 */
	private final int group;
	/**
	 * 分拆選項及分數
	 * 
	 */
	private final List<List<Integer>> ChoiceScore;
	/**
	 * 分拆選項及分數
	 * 
	 */
	private static Map<Integer,List<Integer>> HeroMsgMap = new HashMap<>();
	/**
	 * 關卡訊息
	 */
//	private static Map<Integer,Integer> PassMapMsg = new HashMap<>();
	
	public SecretMsgCfg() {
		this.group = 0;
		this.id = 0;
		this.hero = 0;
		this.score = "";
		this.ChoiceScore = new ArrayList<>();
	}
	
    @Override
    protected boolean assemble() {
    	ChoiceScore.clear();
        if (score != null && score.length() > 0) {
        	String[] ss = score.split(",");
        	for (String s: ss) {
        		String[] allvalue = s.split("_");
        		List<Integer> aList = new ArrayList<>();
        		for (String value : allvalue ) {
        			aList.add(Integer.valueOf(value.trim()));
        		}
        		ChoiceScore.add(aList);
        	}
        }
        
        //一般訊息
  
        if (!HeroMsgMap.containsKey(this.hero)) {
    		List<Integer> aList = new ArrayList<>();
    		aList.add(this.id);
    		HeroMsgMap.put(this.hero, aList);
        } else {
        	HeroMsgMap.get(this.hero).add(this.id);
        }

        
//        // 關卡訊息
//        if (this.type == GsConst.SecretMsgType.Level) {
//        	if (!PassMapMsg.containsKey(this.ratio)) {
//        		PassMapMsg.put(this.ratio, this.id);
//        	}
//        }
           
        return true;
    }
    
    public int getId() {
    	return this.id;
    }
    
    public int getHero() {
    	return this.hero;
    }
    
    public String getScore() {
    	return this.score;
    }
    
    public int getGroup() {
		return group;
	}

	public static int getHeroMaxMsg(int heroId) {
    	if (HeroMsgMap.containsKey(heroId)) {
    		return HeroMsgMap.get(heroId).size();
    	}
    	return 0;
    }
    
    /**
     * 	取出所有該英雄所有訊息
     * @param heroId
     * @return
     */
    public static List<Integer> getHeroAllMsg(int heroId) {
    	if (HeroMsgMap.containsKey(heroId)) {
    		return HeroMsgMap.get(heroId);
    	}
    	return null;
    }
    
//    public static int getPassMapMsg(int passMapId) {
//    	if (PassMapMsg.containsKey(passMapId)) {
//    		return PassMapMsg.get(passMapId) ;
//    	}
//    	return 0;
//    }
    
    public List<Integer> getChoiceMsgList(int choice){
    	List<Integer> Listemtry = new ArrayList<>(3);
    	
    	Listemtry.add(0);
    	Listemtry.add(0);
    	Listemtry.add(0);
    	
    	if (choice != -1) {
    		return ChoiceScore.get(choice);
    	}
    	return Listemtry;
    }
    
//    public static int RandomSecretMsgId(List<Integer> HeroList,List<Integer>WaitMsg,List<Integer> completeMsgId) {
//    	if (HeroList.size() > 0) {
//    		try { 
//	    		List<Integer> WeightList = new ArrayList<>();
//	    		int HeroId = 0;
//	    		if (HeroList.size() == 1) {
//	    			
//	    			HeroId = HeroList.get(0);
//	    			
//	    		} else {
//		    		for (int itemId : HeroList) {
//		    			Hero_NGListCfg heroCfg = ConfigManager.getInstance().getConfigByKey(Hero_NGListCfg.class, itemId);
//		    			if (heroCfg != null) {
//		    				WeightList.add(heroCfg.getSayso());
//		    			} else {
//		    				WeightList.add(0);
//		    			}
//		    		}
//		    		HeroId = GuaJiRand.randonWeightObject(HeroList, WeightList);
//	    		}
//	    		
//    			List<Integer> MsgList = HeroMsgMap.get(HeroId);
//    			TreeSet<Integer> allMsgList = new TreeSet<>(MsgList);
//    			allMsgList.removeAll(WaitMsg);
//    			allMsgList.removeAll(completeMsgId);
//    			
//    			return (allMsgList.size() > 0) ? allMsgList.first():0 ;
//    		} catch (Exception e) {
//    			MyException.catchException(e);
//    		}
//    	}
//    	return 0;
//    }
    
    public static LinkedHashMap<Integer,Integer> initMsg (int heroId){
    	LinkedHashMap<Integer,Integer> aMap = new LinkedHashMap<>();
    	List<Integer> rlist =  RandomAllMsg(heroId);
    	for (Integer msgId : rlist) {
    		aMap.put(msgId, -1);  // 設未回答
    	}
    	return aMap;
    }
        
    private static List<Integer> RandomAllMsg(int heroId){
    	List<Integer> rlist = new ArrayList<>();
    	if (HeroMsgMap.containsKey(heroId)) {
    		Map<Integer,List<Integer>> randMap = new HashMap<>();
    		// random group
    		for (Integer msgId:HeroMsgMap.get(heroId)) {
    			SecretMsgCfg scfg = ConfigManager.getInstance().getConfigByKey(SecretMsgCfg.class, msgId);
    			if (scfg != null) {
    				if (randMap.containsKey(scfg.getGroup())) {
    					randMap.get(scfg.getGroup()).add(msgId);
    				} else {
    					List<Integer> alist = new ArrayList<>();
    					alist.add(msgId);
    					randMap.put(scfg.getGroup(),alist);
    				}
    			}
    		}
    		
    		for (Integer key : randMap.keySet()) {
    			GuaJiRand.randomOrder(randMap.get(key));
    			rlist.addAll(randMap.get(key));
    		}
    	}
    	return rlist;
    }
    
    
}
