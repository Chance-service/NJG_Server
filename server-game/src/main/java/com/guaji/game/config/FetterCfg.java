package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.entity.RoleEntity;
import com.guaji.game.player.PlayerData;
import com.guaji.game.util.GameUtil;
//import com.mysql.jdbc.StringUtils;

@ConfigManager.XmlResource(file = "xml/fetter.xml", struct = "map")
public class FetterCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	/**
	 * 对应archive.xml表的ID
	 */
	protected final String include;
	private List<Integer> archiveIds;
	
	/**
	 * 公式 
	 */
	protected final int formula;
	/**
	 * 奖励物品
	 */
	protected final String awardItem;
	/**
	 * 奖励属性
	 */
	protected final String awardAttribute;
	private Map<Integer, Integer> attrs;
	
	public FetterCfg() {
		id = 0;
		include = "";
		formula = 0;
		awardItem = "";
		awardAttribute = "";
		archiveIds = null;
	}

	public int getId() {
		return id;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		//对应的收集ID
		if ((include == null)||(include).isEmpty()){
			return false;
		}	
		String[] arr = include.split(",");
		archiveIds = new ArrayList<>();;
		for(int i = 0; i < arr.length; i++){
			archiveIds.add(Integer.valueOf(arr[i]));
		}
		//奖励属性
		if ((awardAttribute == null)||(awardAttribute).isEmpty()){
			return false;
		}
		arr = awardAttribute.split("_");
		attrs = new HashMap<Integer, Integer>();
		attrs.put(Integer.valueOf(arr[0]), Integer.valueOf(arr[1]));
		return true;
	}
	
	public String getInclude() {
		return include;
	}

	public String getAwardItem() {
		return awardItem;
	}

	public String getAwardAttribute() {
		return awardAttribute;
	}

	public List<Integer> getArchiveIds() {
		return archiveIds;
	}

	public Map<Integer, Integer> getAttrs() {
		return attrs;
	}
	
	public String getNewAttribute(PlayerData playerData) {
		int star = getGroupMinStar(playerData);
		List<String> alist = new ArrayList<>();
		String[] ss = this.awardAttribute.split(",");
		for (String s : ss) {
			String[] attr =  s.split("_");
			int value = Integer.valueOf(attr[2]);
			value = calformula(value,star);
			alist.add(String.format("%s_%s_%d",attr[0],attr[1],value));
		}
		String newAttribute = GameUtil.join(alist,",");

		return newAttribute;
	}
	
	public int calformula(int value,int star) {
		if (this.formula == 1) {
			return value+value*Math.max((star-1),0);
		} else if (this.formula == 2) {
			return value+(value*3*Math.max((star-6),0))+(value*star/4);
		}  else if (this.formula == 3) {
			return value+value*(Math.max((star-1),0))*2;
		} else if (this.formula == 4) {
			return value+value*(Math.max((star-1),0))+(Math.max(star*2-2,0));
		}
		return value;
	}
	/**
	 * 取羈絆角色星等最低
	 * @param player
	 * @return
	 */
	public int getGroupMinStar(PlayerData playerData) {
		int star = 13;
		for(int i = 0; i < archiveIds.size(); i++){
			int itemId = archiveIds.get(i);
			RoleEntity roleEntity = playerData.getMercenaryByItemId(itemId);
			if (roleEntity == null) {
				break;
			}
			star = Math.min(roleEntity.getStarLevel(),star);
		}
		return star;
	}
	
}
