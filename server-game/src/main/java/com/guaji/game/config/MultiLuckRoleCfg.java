package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;

import com.guaji.game.util.Tuple2;

@ConfigManager.XmlResource(file = "xml/multiLuckRole.xml", struct = "map")
public class MultiLuckRoleCfg extends ConfigBase {

	@Id
	private final String id;

	// 是否开启
	private final int isOpen1;
	// 佣兵权重池
	private final String luckRole1;
	// 提供的属性
	private final String attr1;
	// 字典Str
	private final String name1;

	public MultiLuckRoleCfg() {
		this.id = null;
		this.isOpen1 = 0;
		this.luckRole1 = null;
		this.attr1 = null;
		this.name1 = null;
	}

	@Override
	protected boolean assemble() {
		return true;
	}

	@Override
	protected boolean checkValid() {

		return true;
	}

	/**
	 * 获取今天对应的佣兵
	 * 
	 * @return
	 */
	public Map<Integer, String> getMultiLuckRole() {
		Map<Integer, String> map = new HashMap<Integer, String>();
		// 判断(1=开放 0=关闭)
		if (this.isOpen1 == 1) {
			Tuple2<List<Integer>, List<Integer>> randomOneTup = initByString(this.luckRole1);
			map.put(GuaJiRand.randonWeightObject(randomOneTup.first, randomOneTup.second), this.getId());
		}
		return map;
	}

	private Tuple2<List<Integer>, List<Integer>> initByString(String infos) {
		List<Integer> firstList = new ArrayList<Integer>();
		List<Integer> secondList = new ArrayList<Integer>();
		Tuple2<List<Integer>, List<Integer>> tuple2 = new Tuple2<List<Integer>, List<Integer>>(firstList, secondList);
		if (infos != null) {
			for (String info : infos.split(",")) {
				String[] items = info.split("_");
				if (items.length != 2) {
					continue;
				}
				firstList.add(Integer.valueOf(items[0]));
				secondList.add(Integer.valueOf(items[1]));
			}
		}
		return tuple2;
	}

	public String getId() {
		return id;
	}

	public int getIsOpen1() {
		return isOpen1;
	}

	public String getLuckRole1() {
		return luckRole1;
	}

	public String getAttr1() {
		return attr1;
	}

	public String getName1() {
		return name1;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
