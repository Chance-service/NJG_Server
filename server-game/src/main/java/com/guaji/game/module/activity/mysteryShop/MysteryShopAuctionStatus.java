package com.guaji.game.module.activity.mysteryShop;

/**
 * 黑市竞拍 serverdata表中statusstr字段相关内容实体对象（只存最高竞拍价格的玩家）
 * 
 * @author Darcy
 * 
 */
public class MysteryShopAuctionStatus {

	/** 玩家 */
	private int playerId;

	/** 物品id */
	private int itemId;

	/** 竞拍价格 */
	private int auctionPrice;

	/** 活动期数 */
	private int stageId;

	/** 活动Id */
	private int activityId;

	/** 竞拍物品类型 */
	private int itemType;

	/** 物品数量 */
	private int itemCount;
	
	/** 是否发奖 0未发 1已发 */
	private int isAward;
	
	public void clear(){
		playerId = 0;
		itemId = 0;
		auctionPrice = 0;
		stageId = 0;
		activityId = 0;
		itemType = 0;
		itemCount = 0;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getAuctionPrice() {
		return auctionPrice;
	}

	public void setAuctionPrice(int auctionPrice) {
		this.auctionPrice = auctionPrice;
	}

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

	public int getItemType() {
		return itemType;
	}

	public void setItemType(int itemType) {
		this.itemType = itemType;
	}

	public int getItemCount() {
		return itemCount;
	}

	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	public int getIsAward() {
		return isAward;
	}

	public void setIsAward(int isAward) {
		this.isAward = isAward;
	}

}
