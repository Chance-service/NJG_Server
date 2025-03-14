package com.guaji.cdk.http.param;

import java.util.Map;

import org.guaji.os.MyException;

import com.guaji.cdk.util.CdkUtil;

/**
 * cdk生成参数
 * 
 */
public class GenCdkParam {
	private String id;
	private String reward;
	private String channel;
	private String cdkNum;
	private String starttime;
	private String endtime;
	private String game;
	private int count;
	private String type;

	public GenCdkParam() {
		reward = "";
		count = 0;
	}

	public void toLowerCase() {
		if (reward != null) {
			reward = reward.toLowerCase();
		}
	}

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * 参数解析
	 * 
	 * @param params
	 * @return
	 */
	public boolean initParam(Map<String, String> params) {
		try {
			reward = params.get("reward");

			count = Integer.valueOf(params.get("count"));
			channel=params.get("channel");
			cdkNum=params.get("cdkNum");
			starttime=params.get("starttime");
			endtime=params.get("endtime");
			game=params.get("game");
			type=params.get("type");
		} catch (Exception e) {
			MyException.catchException(e);
			return false;
		}
		return true;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getCdkNum() {
		return cdkNum;
	}

	public void setCdkNum(String cdkNum) {
		this.cdkNum = cdkNum;
	}

	public String getStartTime() {
		return starttime;
	}

	public void setStartTime(String startTime) {
		this.starttime = startTime;
	}

	public String getEndTime() {
		return endtime;
	}

	public void setEndTime(String endTime) {
		this.endtime = endTime;
	}
	public String getMessageStr()
	{
		String retStr="game="+game+"&type="+type+"&channel="+channel+"&reward="+reward+"&createtime="+CdkUtil.getDateString()+"&starttime="+starttime+"&endtime="+endtime;
		return retStr;
	}

	public String getType() {
		return type;
	}

	
	
	
	
}
