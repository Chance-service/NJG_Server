package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.attribute.Attribute;

@ConfigManager.XmlResource(file = "xml/roleEquip.xml", struct = "map")
public class RoleEquipCfg extends ConfigBase {

	/**
	 * 配置id
	 */
	@Id
	protected final int id;

	/**
	 * 佣兵ID
	 */
	protected final String roleId;
	protected List<Integer> roleIdList;
	
	/**
	 * 等级不相关属性
	 */
	protected final String attr;

	/**
	 * 等级相关属性
	 */
	private final String levelAttribute;

	/**
	 * 等级不相关
	 */
	private Attribute equipAttrInfo;

	/**
	 * 套装属性(等级相关)
	 */
	private Attribute equipAttrLevel;

	public RoleEquipCfg() {
		id = 0;
		roleId = null;
		equipAttrInfo = null;
		levelAttribute = null;
		equipAttrLevel = null;
		attr = null;
	}

	@Override
	protected boolean assemble() {
		if (attr != null && attr.length() > 0) {
			equipAttrInfo = Attribute.valueOf(attr);
		}

		// 等级相关属性
		if (this.levelAttribute != null && this.levelAttribute.length() > 0) {
			this.equipAttrLevel = Attribute.valueOf(this.levelAttribute);
		}
		if(!StringUtils.isEmpty(roleId)){
			String[] arr = roleId.split(",");
			roleIdList = new ArrayList<Integer>(arr.length);
			for(String id : arr){
				roleIdList.add(Integer.valueOf(id));
			}
		}
		return true;
	}

	public int getId() {
		return id;
	}

	public String getAttr() {
		return attr;
	}

	public void setEquipAttrInfo(Attribute equipAttrInfo) {
		this.equipAttrInfo = equipAttrInfo;
	}

	public Attribute getEquipAttrInfo() {
		return equipAttrInfo;
	}

	public Attribute getEquipAttrLevel() {
		return equipAttrLevel;
	}

	public void setEquipAttrLevel(Attribute equipAttrLevel) {
		this.equipAttrLevel = equipAttrLevel;
	}

	public String getLevelAttribute() {
		return levelAttribute;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}
	
	public boolean containRoleId(int roleId){
		if(roleIdList == null || roleIdList.size() == 0){
			return false;
		}else{
			return roleIdList.contains(roleId);
		}
	}

	public List<Integer> getRoleIdList() {
		return roleIdList;
	}
		
}
