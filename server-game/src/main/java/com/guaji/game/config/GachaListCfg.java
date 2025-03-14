package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;
import org.guaji.os.MyException;

@ConfigManager.XmlResource(file = "xml/gacha_list.xml", struct = "map")
public class GachaListCfg extends ConfigBase {

	public static final int BOX_TYPE_SINGLE = 1;
	public static final int BOX_TYPE_ASSURE = 2;
	public static final int BOX_TYPE_TEN = 10;
	
	/**
	 * 配置id activityId
	 */
	@Id
	private final int id;
	
	/**
	 * 每單抽獎勵
	 */
	private final String packbox_single;
	/**
	 * 第十抽獎勵
	 */
	private final String packbox_ten;
	/**
	 * 保底池
	 */
	private final String packbox_assure;
	/**
	 * 抽獎禮
	 */
	private final String gachaGift;
	
	private Map<Integer,ArrayList<Integer>> drawBoxId ;
	
	private Map<Integer,ArrayList<Integer>> drawRate ;

	public GachaListCfg() {
		this.id = 0;
		this.packbox_single = "";
		this.packbox_assure = "";
		this.packbox_ten = "";
		this.gachaGift ="";
		this.drawBoxId = new HashMap<>();
		this.drawRate = new HashMap<>();
		
	}

	public int getId() {
		return id;
	}

	public String getPackbox_single() {
		return packbox_single;
	}
	
	public String getPackbox_​assure() {
		return packbox_assure;
	}

	public String getPackbox_ten() {
		return packbox_ten;
	}
	
	public String getGachaGift() {
		return gachaGift;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		
		// 每一單抽
		if (!packbox_single.isEmpty()) {
			int keytype = BOX_TYPE_SINGLE;
			String[] info = packbox_single.split(",");
			for(String str : info) {
				String[] box = str.split("_");
				
				if (drawBoxId.containsKey(keytype)) {
					drawBoxId.get(keytype).add(Integer.valueOf(box[0]));
				} else {
					ArrayList<Integer> idList = new ArrayList<>();
					idList.add(Integer.valueOf(box[0]));
					drawBoxId.put(keytype, idList);
				}
				if (drawRate.containsKey(keytype)) {
					drawRate.get(keytype).add(Integer.valueOf(box[1]));
				} else {
					ArrayList<Integer> idList = new ArrayList<>();
					idList.add(Integer.valueOf(box[1]));
					drawRate.put(keytype, idList);
				}
			}
		} else {
			return false;
		}
		
		 // 十抽的第十抽
		if (!packbox_ten.isEmpty()) {
			int keytype = BOX_TYPE_TEN;
			String[] info = packbox_ten.split(",");
			for(String str : info) {
				String[] box = str.split("_");
				
				if (drawBoxId.containsKey(keytype)) {
					drawBoxId.get(keytype).add(Integer.valueOf(box[0]));
				} else {
					ArrayList<Integer> idList = new ArrayList<>();
					idList.add(Integer.valueOf(box[0]));
					drawBoxId.put(keytype, idList);
				}
				if (drawRate.containsKey(keytype)) {
					drawRate.get(keytype).add(Integer.valueOf(box[1]));
				} else {
					ArrayList<Integer> idList = new ArrayList<>();
					idList.add(Integer.valueOf(box[1]));
					drawRate.put(keytype, idList);
				}
			}
		} else {
			return false;
		}
		
		 // 保底池建立
		if (!packbox_assure.isEmpty()) {
			int keytype = BOX_TYPE_ASSURE;
			String[] info = packbox_assure.split(",");
			for(String str : info) {
				String[] box = str.split("_");
				
				if (drawBoxId.containsKey(keytype)) {
					drawBoxId.get(keytype).add(Integer.valueOf(box[0]));
				} else {
					ArrayList<Integer> idList = new ArrayList<>();
					idList.add(Integer.valueOf(box[0]));
					drawBoxId.put(keytype, idList);
				}
				if (drawRate.containsKey(keytype)) {
					drawRate.get(keytype).add(Integer.valueOf(box[1]));
				} else {
					ArrayList<Integer> idList = new ArrayList<>();
					idList.add(Integer.valueOf(box[1]));
					drawRate.put(keytype, idList);
				}
			}
		} else {
			return false;
		}
		
//		if ((!drawBoxId.get(BOX_TYPE_SINGLE).contains(this.getGuarant())) 
//				|| (!drawBoxId.get(BOX_TYPE_TEN).contains(this.getGuarant()))) {
//			return false;
//		}
		 
		return true;
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

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
	
	/**
	 * 取出抽獎獎箱
	 * @param boxtype
	 * @return
	 */
	
	public int getRandomBoxId(int boxtype) {
		try {
			return GuaJiRand.randonWeightObject(drawBoxId.get(boxtype),drawRate.get(boxtype));
		} catch (Exception e) {
			MyException.catchException(e);
		}
		
		return -2;
	}
	
	/**
	 * 取出各類型抽獎箱列表
	 * @param boxtype
	 * @return
	 */
	public List<Integer> getPackBosList(int boxtype){
		if (drawBoxId.containsKey(boxtype)) {
			return drawBoxId.get(boxtype);
		}
		return null;
	}
}
