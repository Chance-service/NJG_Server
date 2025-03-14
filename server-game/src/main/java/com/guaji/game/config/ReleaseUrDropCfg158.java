package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;

/**
* @author 作者 E-mail:Jeremy@163.com
* @version 创建时间：Mar 15, 2019 11:44:16 AM
* 类说明
*/
/**
 * @author hanchao
 *
 */
@ConfigManager.XmlResource(file = "xml/releaseURDrop158.xml", struct = "map")
public class ReleaseUrDropCfg158 extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	/**
	 * 奖励物品
	 */
	private final String rewards;
	/**
	 * 機率
	 */
	private final String drawRate;
	/**
	 * 隨機兌換
	 */
	private final String exchange;
	/**
	 * 权重
	 */
	private final String exchangeRate;
	/**
	 *兌換消耗
	 */
	private final int consume;
	/*
	 * 獎勵表
	 */
	private final List<String> rewardList;
	/*
	 * 獎勵權重
	 */
	private final List<Integer> drawRateList;
	/*
	 * 兌換表
	 */
	private final List<String> exchangeList;
	/*
	 * 兌換權重
	 */
	private final List<Integer> exchangeRateList;
	

	public ReleaseUrDropCfg158() {
		id=0;
		rewards="";
		drawRate="";
		exchange= "";
		exchangeRate="";
		consume = 0;
		rewardList = new ArrayList<>();
		drawRateList = new ArrayList<>();
		exchangeList = new ArrayList<>();
		exchangeRateList = new ArrayList<>();
	}

	@Override
	protected boolean assemble() {
		// TODO Auto-generated method stub
		if (!rewards.isEmpty()) {
			rewardList.clear();
			String[] ss = rewards.split(",");
			for(String s : ss) {
				rewardList.add(s);
			}
		}
		
		if (!drawRate.isEmpty()) {
			drawRateList.clear();
			String[] ss = drawRate.split(",");
			for(String s : ss) {
				drawRateList.add(Integer.valueOf(s.trim()));
			}
		}
		
		if (!exchange.isEmpty()) {
			exchangeList.clear();
			String[] ss = exchange.split(",");
			for(String s : ss) {
				exchangeList.add(s);
			}
		}
		
		if (!exchangeRate.isEmpty()) {
			exchangeRateList.clear();
			String[] ss = exchangeRate.split(",");
			for(String s : ss) {
				exchangeRateList.add(Integer.valueOf(s.trim()));
			}
		}
		
		return super.assemble();
	}

	@Override
	protected boolean checkValid() {
		// TODO Auto-generated method stub
		return super.checkValid();
	}

	public int getId() {
		return id;
	}
	
	public int getConsume(){
		return consume;
	}
	
	public String randomRewrad() {
		return GuaJiRand.randonWeightObject(this.rewardList,this.drawRateList);
	}
	
	public String randomExchange() {
		return GuaJiRand.randonWeightObject(this.exchangeList,this.exchangeRateList);
	}


	
}
