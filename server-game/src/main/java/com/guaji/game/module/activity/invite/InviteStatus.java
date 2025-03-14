package com.guaji.game.module.activity.invite;

import java.util.HashSet;
import java.util.Set;

import com.guaji.game.manager.FriendInviteManager;

public class InviteStatus {
	// 我的邀请码
	private String myInviteCode;
	// 目前我邀请的人数
	private int curInviteAmount;
	// 已经领取的邀请奖励id
	private Set<Integer> gotConfigIds;
	// 是否已经兑换过邀请码
	private boolean isExchanged;
	// 兑换过的邀请码
	private String exchangedInviteCode;
	
	public InviteStatus(){
		myInviteCode = "";
		exchangedInviteCode = "";
		isExchanged = false;
		gotConfigIds = new HashSet<Integer>();
	}
	
	public String getMyInviteCode() {
		if(myInviteCode.equals("")){
			myInviteCode = FriendInviteManager.getInstance().genInviteCode();
		}
		return myInviteCode;
	}

	public int getCurInviteAmount() {
		curInviteAmount = FriendInviteManager.getInstance().getInviteFriendAmount(getMyInviteCode());
		return curInviteAmount;
	}

	public Set<Integer> getGotConfigIds() {
		return gotConfigIds;
	}

	public boolean isExchanged() {
		return isExchanged;
	}

	public boolean exchangeInviteCode(String exchangedInviteCode) {
		System.out.println("+++++++++++++++++++++ exchangedInviteCode" + exchangedInviteCode);
		if(!isExchanged && exchangedInviteCode != null && 
				!exchangedInviteCode.equals("") && !exchangedInviteCode.equals(myInviteCode)){
			if(FriendInviteManager.getInstance().exchangeInviteCode(exchangedInviteCode)){
				this.exchangedInviteCode = exchangedInviteCode;
				this.isExchanged = true;
				return true;
			}
		}
		return false;
	}

	public String getExchangedInviteCode() {
		return exchangedInviteCode;
	}

	public void addGotAwardCfgId(int getCfgId) {
		gotConfigIds.add(getCfgId);
	}
}
