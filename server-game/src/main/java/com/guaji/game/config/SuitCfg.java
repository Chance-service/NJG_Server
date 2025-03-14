package com.guaji.game.config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.attribute.Attribute;
import com.guaji.game.protocol.Const;

@ConfigManager.XmlResource(file = "xml/suit.xml", struct = "list")
public class SuitCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	private final int id;
	/**
	 * 套装Id
	 */
	private final int suitId;
	/**
	 * 装备匹配数量
	 */
	private final int count;
	/**
	 * 套装属性字符串
	 */
	private final String attr;
	/**
	 * 技能等级加成
	 */
	private final String skillLevel;
	/**
	 * 等级相关属性
	 */
	private final String levelAttribute;

	private Map<Integer, Integer> skillEnhanceMap;

	/**
	 * 套装属性(等级无关)
	 */
	private Attribute suitAttr;

	/**
	 * 套装属性(等级相关)
	 */
	private Attribute suitAttrLevel;

	public SuitCfg() {
		this.id = 0;
		this.suitId = 0;
		this.attr = null;
		this.suitAttr = null;
		this.suitAttrLevel = null;
		this.count = 2;
		this.skillLevel = "";
		this.levelAttribute = null;
		this.skillEnhanceMap = new HashMap<Integer, Integer>();
	}

	public int getId() {
		return id;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		if (this.attr != null && this.attr.length() > 0) {
			if (this.attr != null && this.attr.length() > 0) {
				this.suitAttr = Attribute.valueOf(this.attr);
			}
		}

		// 等级相关属性
		if (this.levelAttribute != null && this.levelAttribute.length() > 0) {
			// 字符解析
			String result[] = levelAttribute.split(",");
			suitAttrLevel = new Attribute();
			suitAttrLevel.clear();
			for (String value : result) {
				String values[] = value.split("_");
				// 属性类型校验
				int attrType = Integer.parseInt(values[0]);
				Const.attr attrTypeEnum = Const.attr.valueOf(attrType);
				if (null != attrTypeEnum) {
					suitAttrLevel.add(attrTypeEnum, Integer.parseInt(values[1]));
				}
			}
			this.suitAttrLevel = Attribute.valueOf(this.levelAttribute);
		}

		if (this.skillLevel != null && this.skillLevel.length() > 0 && !"0".equals(this.skillLevel)) {
			String[] skillLevelArr = this.skillLevel.split(",");
			for (String sl : skillLevelArr) {
				if ("0".equals(sl)) {
					continue;
				}
				String[] ss = sl.split(",");
				for (String s : ss) {
					String[] sv = s.split("_");
					this.skillEnhanceMap.put(Integer.valueOf(sv[0]), Integer.valueOf(sv[1]));
				}
			}
		}
		return true;
	}

	public int getSkillEnhanceLevel(int skillId) {
		if (skillEnhanceMap.containsKey(skillId)) {
			return this.skillEnhanceMap.get(skillId);
		} else {
			return 0;
		}
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

	public String getAttr() {
		return attr;
	}

	public int getCount() {
		return count;
	}

	public int getSuitId() {
		return suitId;
	}

	public static SuitCfg getSuitCfg(int suitId, int count) {
		if (suitId == 0) {
			return null;
		}
		List<SuitCfg> suitCfgs = ConfigManager.getInstance().getConfigList(SuitCfg.class);
		List<SuitCfg> scfgs = new LinkedList<>();
		for (SuitCfg suitCfg : suitCfgs) {
			if (suitCfg.getSuitId() == suitId) {
				scfgs.add(suitCfg);//把是此套装的描述行添加
			}
		}
		if (scfgs.size() == 0) {
			return null;
		}
		int index = -1;
		for (int i = 0; i < scfgs.size() - 1; i++) {
			if (scfgs.get(i).count <= count && scfgs.get(i + 1).count > count) {
				index = i;
				break;
			}
		}
		if (index < 0 && scfgs.get(scfgs.size() - 1).count <= count) {
			index = scfgs.size() - 1;
		}
		if (index < 0) {
			return null;
		}
		return scfgs.get(index);
	}

	public Attribute getSuitAttr() {
		return suitAttr;
	}

	public void setSuitAttr(Attribute suitAttr) {
		this.suitAttr = suitAttr;
	}

	public Attribute getSuitAttrLevel() {
		return suitAttrLevel;
	}

	public void setSuitAttrLevel(Attribute suitAttrLevel) {
		this.suitAttrLevel = suitAttrLevel;
	}

	/**
	 * 等级相关属性
	 * 
	 * @return
	 */
	public String getLevelAttribute() {
		return levelAttribute;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
