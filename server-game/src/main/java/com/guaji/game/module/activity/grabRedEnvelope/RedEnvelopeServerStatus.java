package com.guaji.game.module.activity.grabRedEnvelope;

public class RedEnvelopeServerStatus {
	/**
	 * 服务器红包数量
	 */
	private int serverRedEnvelopeAmount;
	/**
	 * 活动期号
	 */
	private int stageId;
	/**
	 * 全服充值数额
	 */
	private int serverRechargeNum;
	
	public RedEnvelopeServerStatus(){
		serverRedEnvelopeAmount = 0;
		stageId = 0;
		serverRechargeNum = 0;
	}
	
	public RedEnvelopeServerStatus(int stageId){
		serverRedEnvelopeAmount = 0;
		this.stageId = stageId;
		serverRechargeNum = 0;
	}
	
	public int getServerRedEnvelopeAmount() {
		return serverRedEnvelopeAmount;
	}

	public void setServerRedEnvelopeAmount(int serverRedEnvelopeAmount) {
		this.serverRedEnvelopeAmount = serverRedEnvelopeAmount;
	}

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public synchronized void addServerRedEnvelopeAmount(int addNum){
		serverRedEnvelopeAmount += addNum;
	}
	
	public synchronized boolean deductServerRedEnvelopeAmount(int deductNum){
		if(serverRedEnvelopeAmount >= deductNum){
			serverRedEnvelopeAmount -= deductNum;
			return true;
		}else{
			return false;
		}
	}

	public synchronized void addServerRechargeNum(int amount){
		serverRechargeNum += amount;
	}
	
	public int getServerRechargeNum() {
		return serverRechargeNum;
	}

	public void reset(int stageId){
		if(this.stageId != stageId){
			serverRedEnvelopeAmount = 0;
			this.stageId = stageId;
			serverRechargeNum = 0;
		}
	}
}
