package com.guaji.game.config;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.log.Log;
import org.guaji.os.GuaJiRand;

/**
 * 性奴小學堂獎勵
 */
@ConfigManager.XmlResource(file = "xml/LTAward.xml", struct = "list")
public class LittleTestRewardCfg extends ConfigBase {
	/**
	 * 答對數量
	 */
	private final int Correct;
	/**
	 * 可抽獎數
	 */
	private final int Count;
	/**
	 * 分數字串
	 */
	private final String Score;
	/**
	 * 獎勵
	 */
	private final String Award;
	/**
	 * 分數數據表
	 */
	private final List<Integer> ScoreList;
	/**
	 * 獎品表
	 */
	private final List<List<String>> allList;
	/**
	 * 全表
	 */
	private static List<LittleTestRewardCfg> totalList =  new LinkedList<>();
	
	public LittleTestRewardCfg() {
		Correct = 0;
		Count = 0;
		Score = "";
		Award = "";
		ScoreList = new LinkedList<>();
		allList = new LinkedList<>();
	}
	
	public int getCorrect() {
		return Correct;
	}
	
	public int getCount() {
		return Count;
	}
	
	public String getScore() {
		return Score;
	}

	public String getAward() {
		return Award;
	}
	
	public List<Integer> getScoreList(){
		return ScoreList;
	}
	
	public List<List<String>> getallList(){
		return allList;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
		totalList.clear();
	}

	@Override
	protected boolean assemble() {
		// 記錄全表
		totalList.add(this);
		
		// 轉換為數字表
		ScoreList.clear();
		if (StringUtils.isNotEmpty(Score)){
			String[] conuts = Score.split(",");
			for (String aconut : conuts) {
				ScoreList.add(Integer.valueOf(aconut.trim()));
			}
		}
		allList.clear();
		if (StringUtils.isNotEmpty(Award)){
			String[] awards = Award.split(";");
			for (String str : awards) {
				List<String> giftlist =  new LinkedList<String>();
				if (StringUtils.isNotEmpty(str)){
					String[] gifts = str.split(",");
					for (String agift : gifts) {
						giftlist.add(agift);
					}
				}
				allList.add(giftlist);
			}
		}
		
		if (ScoreList.size() != allList.size()) {
			Log.errPrintln("activity143PirateDropCfg ScoreList and allList size error");
			return false;
		}
		
//	測試用	if (totalList.size() == 4) {
//			List<String> retList = new LinkedList<String>();
//			retList = GetPlayerAward(8,60000);
//		}
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	*按玩家答題得分得到隨機獎勵
	 */
	public static List<String> GetPlayerAward(int correct , int score, int addcount) {
		LittleTestRewardCfg useCfg = null;
		List<String> retList = new LinkedList<String>();
		for (LittleTestRewardCfg aCfg : totalList) {
			if (correct >= aCfg.getCorrect()) {
				useCfg = aCfg;
			}
		}
		int index = -1;
		for (int aScore :useCfg.getScoreList()) {
			if (score >= aScore) {
				index++;
			}				
		}
		if (index == -1) {
			index = 0;
		}
		List<String> awradlist = useCfg.getallList().get(index);
		int getcount = useCfg.getCount() + addcount;

		int[] ranary = GuaJiRand.randomCommon(0, awradlist.size()-1 , getcount);
		
		for (int i = 0; i < ranary.length; i++) {
			retList.add(awradlist.get(ranary[i]));
		}
		
		return retList;
	}
	
}
