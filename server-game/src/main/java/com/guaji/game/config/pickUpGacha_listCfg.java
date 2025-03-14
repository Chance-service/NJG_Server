package com.guaji.game.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;

@ConfigManager.XmlResource(file = "xml/pickUpGacha_list.xml", struct = "map")
public class pickUpGacha_listCfg extends ConfigBase {
	
	public static final int BOX_TYPE_SINGLE = 1;
	public static final int BOX_TYPE_ASSURE = 2;
	public static final int BOX_TYPE_TEN = 10;
	
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	/**
	 * 免費次數
	 */
	private final int freeCount;
	/**
	 * 召喚券
	 */
	private final String ticket;
	/**
	 * 單抽鑽石數
	 */
	private final int singleCost;
	/**
	 * 十抽鑽石數
	 */
	private final int tenCost;
	/**
	 * 	單抽Box
	 */
	private final String packbox_single;
	/**
	 * 	第十抽
	 */
	private final String packbox_ten;
	/**
	 * 	每抽反利
	 */
	private final String gachaGift;
	/**
	 * 第幾抽保底
	 */
	private final int guarant;
	/**
	 * 	保底池
	 */
	private final String packbox_assure;
	/** 
	 * 	活动开始时间 
	 * */
	private final String startTime;
	/** 
	 * 活动关闭时间（前端看不到） 
	 */
	private final String closeTime;
	
	/** 开启时间 */
	private long lStartTime = 0;

	/** 失效时间 */
	private long lCloseTime = 0;
	
	private Map<Integer,ArrayList<Integer>> drawBoxId ;
	
	private Map<Integer,ArrayList<Integer>> drawRate ;
	
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

	

	public pickUpGacha_listCfg() {
		this.id = 0;
		this.freeCount = 0;
		this.ticket = "";
		this.singleCost = 0;
		this.tenCost = 0;
		this.packbox_single = "";
		this.packbox_ten = "";
		this.gachaGift = "";
		this.guarant = 0;
		this.packbox_assure = "";
		this.startTime = "";
		this.closeTime = "";
		this.drawBoxId = new HashMap<>();
		this.drawRate = new HashMap<>();
		
	}

	public int getId() {
		return id;
	}
	
	public int getFreeCount() {
		return freeCount;
	}

	public String getTicket() {
		return ticket;
	}

	public int getSingleCost() {
		return singleCost;
	}

	public int getTenCost() {
		return tenCost;
	}

	public String getPackbox_single() {
		return packbox_single;
	}

	public String getPackbox_ten() {
		return packbox_ten;
	}

	public String getGachaGift() {
		return gachaGift;
	}

	public int getGuarant() {
		return guarant;
	}

	public String getPackbox_assure() {
		return packbox_assure;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getCloseTime() {
		return closeTime;
	}
	
	/**
	 * pu池是否有效
	 * @return
	 */
	public boolean isActive() {
		long currentTime = GuaJiTime.getMillisecond();
		if (currentTime >= this.lStartTime && currentTime <= this.lCloseTime) {
			return true;
		}
		return false;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		try {
			this.lStartTime = DATE_FORMAT.parse(this.startTime).getTime();
			this.lCloseTime = DATE_FORMAT.parse(this.closeTime).getTime();
		} catch (ParseException e) {
			MyException.catchException(e);
		}
		
		Map<Integer,String> aMap = new HashMap<>();
		
		aMap.put(BOX_TYPE_SINGLE, getPackbox_single());
		aMap.put(BOX_TYPE_ASSURE, getPackbox_assure());
		aMap.put(BOX_TYPE_TEN, getPackbox_ten());
		
		for (Map.Entry<Integer, String> entry : aMap.entrySet()) {
			if (!entry.getValue().isEmpty()) {
				int keytype = entry.getKey();
				String[] info = entry.getValue().split(",");
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
				if (entry.getKey() != BOX_TYPE_ASSURE) {
					return false;
				} else if (getGuarant() != 0) {
					return false;
				}
			}
		}		
		return true;
	}

	/**
	 * 检测有消息
	 * 
	 * @return
	 */
	@Override
	protected boolean checkValid() {
		
		boolean result = this.lCloseTime >= this.lStartTime;
		if (!result) {
			throw new RuntimeException("pickUpGacha_list.xml must be closeTime >= startTime , pickUpGacha_listCfg id : " + this.id);
		}
		return true;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
	
	/**
	 * 取出所有有效的pickup池
	 */
	public static Map<Integer,pickUpGacha_listCfg> getActivePickUpCfg (){
		Map<Integer,pickUpGacha_listCfg> CfgMap = new HashMap<>();
		
		for (pickUpGacha_listCfg pickupCfg : ConfigManager.getInstance().getConfigMap(pickUpGacha_listCfg.class).values()) {
			if (pickupCfg.isActive()) {
				CfgMap.put(pickupCfg.getId(), pickupCfg);
			}
		}
		return CfgMap;
	}
	
	/**
	 * 抽出獎箱
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
	/**
	 * 計算現在到關閉剩餘時間
	 * @return
	 */
	public int calcCloseTime() {
		long currentTime = GuaJiTime.getMillisecond();
		int surplusTime = (int) ((this.lCloseTime - currentTime) / 1000);
		return Math.max(surplusTime, 0);
	}

}
