package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.log.Log;

import com.guaji.game.attribute.Attribute;

@ConfigManager.XmlResource(file = "xml/SecretBUFF.xml", struct = "map")
public class SecretBuffCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	/**
	 * 選項及對應分數
	 */
	private final String score;
	/**
	 * 對應屬性
	 */
	private final String state;	
	
	private final List<Integer> ScoreList;
	
	private final List<Attribute> AttributeList;
	
	public SecretBuffCfg() {
		this.id = 0;
		this.score = "";
		this.state = "";
		this.ScoreList = new ArrayList<>();
		this.AttributeList = new ArrayList<>();
	}
	
    @Override
    protected boolean assemble() {
    	ScoreList.clear();
        if (score != null && score.length() > 0) {
        	String[] ss = score.split(",");
        	for (String s: ss) {
        		ScoreList.add(Integer.valueOf(s.trim()));
        	}
        }
        AttributeList.clear();
        if (state != null && state.length() > 0) {
        	String [] aa = state.split(";");
        	for (String a : aa) {
        		AttributeList.add(Attribute.valueOf(a));
        	}
        	
        }
        
        if (AttributeList.size() != ScoreList.size()) {
        	Log.errPrintln("SecretBUFF.xml range size error");
        	return false;
        }
        
        return true;
    }
    
    public int getId() {
    	return this.id;
    }
    
    public String getScore() {
    	return this.score;
    }
    
    public String getState() {
    	return this.state;
    }
    
    public Attribute getAttribute(int score) {
    	int idx = -1;
    	Attribute attribute = new Attribute();
    	for (int ascore :ScoreList) {
    		if (score >= ascore) {
    			idx++;
    		}
    	}
    	if (idx >= 0) {
    		attribute = AttributeList.get(idx);
    	}
    	return attribute;
    }
    
}
