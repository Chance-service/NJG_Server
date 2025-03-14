package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.attribute.AttrInfoObj;

@ConfigManager.XmlResource(file = "xml/teamBuff.xml", struct = "map")
public class TeamBuffCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int id;	
	/**
	 * 元素屬性
	 */
	protected final int Attr;
	/**
	 * 數量
	 */
	protected final int Num;
	/**
	 * 對應增幅屬性
	 */
	protected final String Buff;
	private List<AttrInfoObj> attrInfos;
	
	public TeamBuffCfg() {
		id = 0;
		Attr = 0;
		Num = 0;
		Buff = "";
	}



	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		
		if (!Buff.isEmpty()){
			attrInfos = AttrInfoObj.valueOfs(Buff);
		}
		
		return true;
	}
	
	public int getId() {
		return id;
	}
	
	public int getAttr() {
		return this.Attr;
	}
	
	public int getNum() {
		return this.Num;
	}
	
	public String getBuff() {
		return Buff;
	}

	public List<AttrInfoObj> getAttrs() {
		return attrInfos;
	}
	
	public static List<AttrInfoObj> getMatchBuff(Map<Integer,Integer>attrMap){
		 List<AttrInfoObj> retInfo = new ArrayList<>();
		 Map<Object, TeamBuffCfg> cfgList = ConfigManager.getInstance().getConfigMap(TeamBuffCfg.class);
		 for (TeamBuffCfg cfg : cfgList.values()) {
			 if (attrMap.containsKey(cfg.getAttr()) && attrMap.get(cfg.getAttr()) == cfg.getNum()) {
				 retInfo.addAll(cfg.getAttrs());
			 }
		 }
		return retInfo;
	}
	
}
