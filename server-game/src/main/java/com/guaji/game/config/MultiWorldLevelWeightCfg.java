package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;

import com.guaji.game.util.Tuple2;

@ConfigManager.XmlResource(file = "xml/multiWorldLevelWeight.xml", struct = "list")
public class MultiWorldLevelWeightCfg extends ConfigBase {
	@Id
	private final int worldLevelId;
	// 星级
	private final int star;
	// 副本数量权重
	private final String countWeight;
	// 副本列表权重
	private final String mapsWorldLevelId;

	// 星集合
	private static Map<Integer, List<MultiWorldLevelWeightCfg>> multiRandomStarMap;

	// 服务器级别
	private static Map<Integer, List<MultiWorldLevelWeightCfg>> multiLevel;

	public MultiWorldLevelWeightCfg() {
		this.worldLevelId = 0;
		this.star = 0;
		this.countWeight = "";
		this.mapsWorldLevelId = "";
		multiRandomStarMap = new HashMap<>();
		multiLevel = new HashMap<>();
	}

	public int getWorldLevelId() {
		return worldLevelId;
	}

	public int getStar() {
		return star;
	}

	public String getCountWeight() {
		return countWeight;
	}

	public String getMapsWorldLevelId() {
		return mapsWorldLevelId;
	}

	public static Map<Integer, List<MultiWorldLevelWeightCfg>> getMultiRandomStarMap() {
		return multiRandomStarMap;
	}

	/**
	 * 随机副本数量
	 */
	public int getRandMapCount() {
		String countWeight = getCountWeight();
		if (countWeight == null || countWeight.isEmpty()) {
			return 0;
		}
		Tuple2<List<Integer>, List<Integer>> randList = initByString(countWeight);
		int multiCount = GuaJiRand.randonWeightObject(randList.first, randList.second);
		return multiCount;
	}

	/**
	 * 随机副本ID
	 * 
	 * @param multiCount
	 * @return
	 */
	public List<Integer> getMultiIdList(int multiCount) {
		String multiId = getMapsWorldLevelId();
		if (multiId == null || multiId.isEmpty()) {
			return null;
		}
		Tuple2<List<Integer>, List<Integer>> randList = initByString(multiId);
		List<Integer> mapList = GuaJiRand.randonWeightObject(randList.first, randList.second, multiCount);
		return mapList;
	}

	/**
	 * 返回随机对象
	 * 
	 * @param infos
	 * @return
	 */
	public Tuple2<List<Integer>, List<Integer>> initByString(String infos) {
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

	/**
	 * 返回当前星级副本信息
	 * 
	 * @param star
	 * @return
	 */
	public static List<MultiWorldLevelWeightCfg> getStarMultiItems(int star) {
		return multiRandomStarMap.get(star);
	}

	/**
	 * 返回服务器级别对应数据
	 * @param serverLevel
	 * @return
	 */
	public static List<MultiWorldLevelWeightCfg> getServerLevel(int serverLevel){
		return multiLevel.get(serverLevel);
	}
	
	@Override
	protected boolean assemble() {
		if (!countWeight.isEmpty() && countWeight != null) {
			if (multiRandomStarMap.containsKey(star)) {
				multiRandomStarMap.get(star).add(this);
			} else {
				List<MultiWorldLevelWeightCfg> list = new ArrayList<MultiWorldLevelWeightCfg>();
				list.add(this);
				multiRandomStarMap.put(star, list);
			}
		}

		if (multiLevel.containsKey(worldLevelId)) {
			multiLevel.get(worldLevelId).add(this);
		} else {
			List<MultiWorldLevelWeightCfg> list = new ArrayList<MultiWorldLevelWeightCfg>();
			list.add(this);
			multiLevel.put(worldLevelId, list);
		}
		return true;
	}

	@Override
	protected boolean checkValid() {

		return true;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}

}
