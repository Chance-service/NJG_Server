package com.guaji.game.config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.log.Log;
import org.guaji.os.GuaJiRand;

/**
 * 性奴小學堂題目表
 */
@ConfigManager.XmlResource(file = "xml/littleTest.xml", struct = "map")
public class LittleTestQuestionCfg extends ConfigBase {
	/**
	 * 最大題目數
	 */
	public static final int MAX_QUESTION_COUNT = 10;
	/**
	 * 最大回答數
	 */
	public static final int MAX_ANSWERS_COUNT = 4;
	/**
	 * 題目ID
	 */
	@Id
	private final int id;
	/**
	 * 題目答案
	 */
	private final String TF;
	/**
	 * 可獲得分數
	 */
	private final int Score;
	/**
	 * 答錯攻擊力最小範圍
	 */
	private final int MinAF;
	/**
	 *  答錯攻擊力最大範圍
	 */
	private final int MaxAF;
	/**
	 * 答對攻擊力最小範圍
	 */
	private final int MinAT;
	/**
	 *答對攻擊力最大範圍
	 */
	private final int MaxAT;
	/**
	 * 答錯Boss攻擊力最小範圍
	 */
	private final int MinDF;
	/**
	 * 答錯Boss攻擊力最大範圍
	 */
	private final int MaxDF;
	/**
	 * 答對Boss攻擊力最小範圍
	 */
	private final int MinDT;
	/**
	 *答對Boss攻擊力最大範圍
	 */
	private final int MaxDT;
	/**
	 * 題目答案數據化
	 */
	private final List<Integer> TFList;
	/**
	 * 全表
	 */
	private static Map<Integer,LittleTestQuestionCfg> totalMap = new HashMap<>();
	
	private static List<Integer> QuestionList;
	
	public LittleTestQuestionCfg() {
		id = 0;
		TF = "";
		Score = 0;
		MinAF = 0;
		MaxAF = 0;
		MinAT = 0;
		MaxAT = 0;
		MinDF = 0;
		MaxDF = 0;
		MinDT = 0;
		MaxDT = 0;
		TFList = new LinkedList<>();
		QuestionList = new LinkedList<>();
	}

	public int getId() {
		return id;
	}

	public String getTF() {
		return TF;
	}
	
	public int getScore() {
		return Score;
	}
	
	public int getMinAF() {
		return MinAF;
	}
	
	public int getMaxAF() {
		return MaxAF;
	}
	
	public int getMinAT() {
		return MinAT;
	}
	
	public int getMaxAT() {
		return MaxAT;
	}
	
	public int getMinDF() {
		return MinDF;
	}
	
	public int getMaxDF() {
		return MaxDF;
	}
	
	public int getMinDT() {
		return MinDT;
	}
	
	public int getMaxDT() {
		return MaxDT;
	}
	
	public static List<Integer> getQuestionList(){
		return QuestionList;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
		totalMap.clear();
		//specialTimesMap.clear();
	}

	@Override
	protected boolean assemble() {

		// 記錄全表
	   if (totalMap.containsKey(id)) {
		   totalMap.replace(id,this);
	   }else {
		   totalMap.put(id,this); 
	   }
	   
	   // 轉換為數字表
	   TFList.clear();
		if (StringUtils.isNotEmpty(TF)){
			String[] conuts = TF.split(",");
			for (String aconut : conuts) {
				TFList.add(Integer.valueOf(aconut.trim()));
			}
		}

		
		if (TFList.size() != MAX_ANSWERS_COUNT) {
			Log.errPrintln("LittleTestQuestionCfg TF size error");
			return false;
		}
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	*Random題目
	 */
	public static void RandomQuestion() {
		QuestionList.clear();
		int[] index = GuaJiRand.randomCommon(1, totalMap.size() , MAX_QUESTION_COUNT);
		for (int i = 0; i < index.length; i++) {
			QuestionList.add(totalMap.get(index[i]).id);
		}
		
		if (QuestionList.size() != MAX_QUESTION_COUNT) {
			Log.errPrintln("LittleTestQuestionCfg QuestionList size error");
		}
	}
	
	public boolean matchanswer(int idx) {
		if ((idx >= 1) &&(idx <= MAX_ANSWERS_COUNT)) {
			int ans = TFList.get(idx-1);
			return (ans == 1);
		}
		return false;
	}
	
}
