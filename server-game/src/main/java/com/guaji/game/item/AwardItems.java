package com.guaji.game.item;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.MyException;

import com.guaji.game.GsApp;
import com.guaji.game.config.RoleRelatedCfg;
import com.guaji.game.entity.BadgeEntity;
import com.guaji.game.entity.ElementEntity;
import com.guaji.game.entity.EquipEntity;
import com.guaji.game.entity.ItemEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.crossbattle.CrossBattleService;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Attribute.AttrInfo;
import com.guaji.game.protocol.Battle.DropAward.DetailElement;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.itemType;
import com.guaji.game.protocol.Const.playerAttr;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Reward.HPPlayerReward;
import com.guaji.game.protocol.Reward.RewardInfo;
import com.guaji.game.protocol.Reward.RewardItem;
import com.guaji.game.protocol.Reward.RewardType;
import com.guaji.game.util.BadgeUtil;
import com.guaji.game.util.EquipUtil;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.TapDBUtil;

/**
 * 奖励信息内存数据 禁忌: 此对象不可重复复用, 避免奖励累加, 切记
 */
public class AwardItems {
	/**
	 * 奖励项
	 */
	public static class Item {
		public int type;
		public int id;
		public  long count;
		public int level;
		public int quality;

		/**
		 * 如果是装备，装备的神器概率
		 */
		private int godlyRatio;
		/**
		 * 如果是装备，装备的开孔数量
		 */
		private int punchSize;
		/**
		 * 如果是装备，装备随机属性是否最大上限属性
		 */
		private boolean isFullAttr;

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public long getCount() {
			return count;
		}

		public void setCount(long count) {
			this.count = count;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public int getQuality() {
			return quality;
		}

		public void setQuality(int quality) {
			this.quality = quality;
		}

		@Override
		public String toString() {
			if (this.godlyRatio > 0 || isFullAttr || this.punchSize > 0) {
				return String.format("%d_%d_%d_%d_%d_%d", type, id, count, godlyRatio, punchSize, isFullAttr ? 1 : 0);
			} else {
				return String.format("%d_%d_%d", type, id, count);
			}
		}

		@Override
		public Item clone() {
			Item item = new Item();
			item.type = this.type;
			item.id = this.id;
			item.count = this.count;
			item.level = this.level;
			item.quality = this.quality;
			return item;
		}

		public void addCount(int count) {
			this.count += count;
		}

		public int getGodlyRatio() {
			return godlyRatio;
		}

		public void setGodlyRatio(int godlyRatio) {
			this.godlyRatio = godlyRatio;
		}

		public int getPunchSize() {
			return punchSize;
		}

		public void setPunchSize(int punchSize) {
			this.punchSize = punchSize;
		}

		public boolean isFullAttr() {
			return isFullAttr;
		}

		public void setFullAttr(boolean isFullAttr) {
			this.isFullAttr = isFullAttr;
		}
	}

	// 所有奖励
	private List<Item> awardItems;

	// 是否发放奖励
	private boolean hasTakeAffect = false;

	/**
	 * 构造函数
	 */
	public AwardItems() {
		awardItems = new LinkedList<Item>();
	}

	/**
	 * 判断是否有奖励
	 */
	public boolean hasAwardItem() {
		return awardItems.size() > 0;
	}

	/**
	 * 克隆奖励对象
	 */
	@Override
	public AwardItems clone() {
		AwardItems newAward = new AwardItems();
		for (Item item : awardItems) {
			newAward.awardItems.add(item.clone());
		}
		return newAward;
	}

	/**
	 * 生成存储字符串
	 *
	 * @return
	 */
	public String toDbString() {
		String result = "";
		for (int i = 0; i < awardItems.size(); i++) {
			if (i > 0) {
				result += ",";
			}
			result += awardItems.get(i).toString();
		}
		return result;
	}

	@Override
	public String toString() {
		String result = "";
		for (int i = 0; i < awardItems.size(); i++) {
			if (i > 0) {
				result += ",";
			}
			result += awardItems.get(i).toString();
		}
		return result;
	}

	public AwardItems addItem(Item item) {
		// 检测堆叠
		Item existItem = null;
		if (GameUtil.itemCanOverlap(item.type)) {
			for (Item tmpItem : awardItems) {
				if (item.type == tmpItem.type && item.id == tmpItem.id) {
					existItem = tmpItem;
					break;
				}
			}
		}

		// 添加
		if (existItem == null) {
			this.awardItems.add(item);
		} else {
			existItem.count += item.count;
		}

		return this;
	}

	public AwardItems addItem(int itemType, int itemId, long count) {
		Item awardItem = new Item();
		awardItem.type = itemType;
		awardItem.id = itemId;
		awardItem.count = count;
		addItem(awardItem);
		return this;
	}

	public AwardItems addEquip(int itemType, int itemId, int count, int godlyRatio, boolean isFullAttr, int punchSize) {
		Item awardItem = new Item();
		awardItem.type = itemType;
		awardItem.id = itemId;
		awardItem.count = count;
		awardItem.godlyRatio = godlyRatio;
		awardItem.isFullAttr = isFullAttr;
		awardItem.punchSize = punchSize;
		addItem(awardItem);
		return this;
	}

	public AwardItems addItem(DropItems.Item item) {
		Item awardItem = new Item();
		awardItem.type = item.type;
		awardItem.id = item.id;
		awardItem.count = item.count;
		addItem(awardItem);
		return this;
	}

	public AwardItems addItem(ItemInfo itemInfo) {
		Item awardItem = new Item();
		awardItem.type = itemInfo.type;
		awardItem.id = itemInfo.itemId;
		awardItem.count = itemInfo.quantity;
		addItem(awardItem);
		return this;
	}

	public AwardItems addItemInfos(List<ItemInfo> itemInfos) {
		for (ItemInfo itemInfo : itemInfos) {
			addItem(itemInfo);
		}
		return this;
	}

	private Item getItem(int type, int itemId) {
		for (Item item : awardItems) {
			if (item.getType() == type && item.getId() == itemId) {
				return item;
			}
		}
		Item item = new Item();
		item.setType(type);
		item.setId(itemId);
		awardItems.add(item);
		return item;
	}

	public List<Item> getAwardItems() {
		return awardItems;
	}

	public List<ItemInfo> getAwardItemInfos() {
		List<ItemInfo> awardInfos = new ArrayList<ItemInfo>();
		for (Item item : awardItems) {
			ItemInfo info = ItemInfo.valueOf(item.getType(), item.getId(), item.getCount());
			awardInfos.add(info);
		}
		return awardInfos;
	}

	public AwardItems addGold(int gold) {
		getItem(itemType.PLAYER_ATTR_VALUE * GsConst.ITEM_TYPE_BASE, playerAttr.GOLD_VALUE).addCount(gold);
		return this;
	}

	public AwardItems addCoin(int coin) {
		getItem(itemType.PLAYER_ATTR_VALUE * GsConst.ITEM_TYPE_BASE, playerAttr.COIN_VALUE).addCount(coin);
		return this;
	}

	public AwardItems setLevel(int level) {
		getItem(itemType.PLAYER_ATTR_VALUE * GsConst.ITEM_TYPE_BASE, playerAttr.LEVEL_VALUE).addCount(level);
		return this;
	}

	public AwardItems addExp(int exp) {
		getItem(itemType.PLAYER_ATTR_VALUE * GsConst.ITEM_TYPE_BASE, playerAttr.EXP_VALUE).addCount(exp);
		return this;
	}

	public AwardItems setVipLevel(int level) {
		getItem(itemType.PLAYER_ATTR_VALUE * GsConst.ITEM_TYPE_BASE, playerAttr.VIPLEVEL_VALUE).addCount(level);
		return this;
	}

	public AwardItems addSmeltValue(int smeltValue) {
		getItem(itemType.PLAYER_ATTR_VALUE * GsConst.ITEM_TYPE_BASE, playerAttr.SMELT_VALUE_VALUE).addCount(smeltValue);
		return this;
	}

	public AwardItems addHonorValue(int honorValue) {
		getItem(itemType.PLAYER_ATTR_VALUE * GsConst.ITEM_TYPE_BASE, playerAttr.HONOR_VALUE_VALUE).addCount(honorValue);
		return this;
	}

	public AwardItems addReputationValue(int reputationValue) {
		getItem(itemType.PLAYER_ATTR_VALUE * GsConst.ITEM_TYPE_BASE, playerAttr.REPUTATION_VALUE_VALUE)
				.addCount(reputationValue);
		return this;
	}

	public AwardItems addCrystalValue(int crystalValue) {
		getItem(itemType.PLAYER_ATTR_VALUE * GsConst.ITEM_TYPE_BASE, playerAttr.CRYSTAL_VALUE_VALUE)
				.addCount(crystalValue);
		return this;
	}

	public AwardItems addContribution(int value) {
		getItem(itemType.PLAYER_ATTR_VALUE * GsConst.ITEM_TYPE_BASE, playerAttr.CONTRIBUTION_VALUE).addCount(value);
		return this;
	}

	public AwardItems addAllianceExp(int value) {
		getItem(itemType.PLAYER_ATTR_VALUE * GsConst.ITEM_TYPE_BASE, playerAttr.ALLIANCE_EXP_VALUE).addCount(value);
		return this;
	}

	public AwardItems addBossFightTimes(int value) {
		getItem(itemType.PLAYER_ATTR_VALUE * GsConst.ITEM_TYPE_BASE, playerAttr.BOSS_TIMES_VALUE).addCount(value);
		return this;
	}

	public AwardItems addAllianceVitality(int value) {
		getItem(itemType.PLAYER_ATTR_VALUE * GsConst.ITEM_TYPE_BASE, playerAttr.ALLIANCE_VITALITY_VALUE)
				.addCount(value);
		return this;
	}

	public AwardItems AddMultiEliteScore(int value) {
		getItem(itemType.PLAYER_ATTR_VALUE * GsConst.ITEM_TYPE_BASE, playerAttr.MULTI_ELITE_SCORE_VALUE)
				.addCount(value);
		return this;
	}

	public AwardItems addCrossCoin(int value) {
		getItem(itemType.PLAYER_ATTR_VALUE * GsConst.ITEM_TYPE_BASE, playerAttr.CROSS_COIN_VALUE).addCount(value);
		return this;
	}
	
	public AwardItems addFriendship(int value) {
		getItem(itemType.PLAYER_ATTR_VALUE * GsConst.ITEM_TYPE_BASE, playerAttr.FRIENDSHIP_VALUE_VALUE).addCount(value);
		return this;
	}
	
	public AwardItems addVipPoint(int value) {
		getItem(itemType.PLAYER_ATTR_VALUE * GsConst.ITEM_TYPE_BASE, playerAttr.VIPPOINT_VALUE_VALUE).addCount(value);
		return this;
	}

	public boolean initByString(String info) {
		if (info != null && info.length() > 0 && !info.equals("0") && !info.equals("none")) {
			String[] awardItemArray = info.split(",");
			for (int i = 0; i < awardItemArray.length; i++) {
				String[] items = awardItemArray[i].split("_");
				if (items.length >= 3) {
					Item awardItem = new Item();
					awardItem.type = Integer.parseInt(items[0]);
					awardItem.id = Integer.parseInt(items[1]);
					awardItem.count = Integer.parseInt(items[2]);
					if (items.length >= 6) {
						awardItem.godlyRatio = Integer.parseInt(items[3]);
						awardItem.punchSize = Integer.parseInt(items[4]);
						awardItem.isFullAttr = Integer.parseInt(items[5]) > 0;
					}
					addItem(awardItem);
				}
			}
			return awardItems.size() > 0;
		}
		return false;
	}

	public static AwardItems valueOf(String info) {
		AwardItems awardItems = new AwardItems();
		if (awardItems.initByString(info)) {
			return awardItems;
		}
		return null;
	}

	public AwardItems appendAward(AwardItems awards) {
		if (awards != null) {
			for (Item item : awards.awardItems) {
				addItem(item.type, item.id, item.count);
			}
		}
		return this;
	}

	/**
	 * 功能发放奖励
	 *
	 * @param player
	 * @param action
	 * @param async
	 * @return
	 */
	public RewardInfo.Builder rewardTakeAffect(Player player, Action action, List<DetailElement.Builder> builderList,int tapAction,Params... params) {
		RewardInfo.Builder builder = RewardInfo.newBuilder();

		// 转换在items中的属性奖励
		for (Item item : awardItems) {
			// 传输细节类型
			item.type = GameUtil.convertToStandardItemType(item.type);

			// 配置出错误
			if (item.getCount() <= 0 || item.getId() <= 0) {
				continue;
			}
		}

		// 实体对象类奖励
		if (!hasTakeAffect) {
			deliverItemAwards(player, builder, action, builderList,tapAction,params);
		}

		for (Item item : awardItems) {
			// 特殊处理
			roleSoul(item, player, action);

			RewardItem.Builder rewardItemBuilder = RewardItem.newBuilder();
			rewardItemBuilder.setItemId(item.getId());
			rewardItemBuilder.setItemCount(item.getCount());
			rewardItemBuilder.setItemType(item.getType());
			builder.addShowItems(rewardItemBuilder);
		}

		return builder;
	}

	private void roleSoul(Item item, Player player, Action action) {
		if (item.getType() / 10000 == Const.itemType.SOUL_VALUE) {
			// ID命名为佣兵ID
			RoleEntity entity = player.getPlayerData().getMercenaryByItemId(item.getId());
			if (entity != null) {

				int roleItemId = item.getId();
				entity.setSoulCount((int)(entity.getSoulCount() + item.getCount()));
				RoleRelatedCfg cfg = ConfigManager.getInstance().getConfigByKey(RoleRelatedCfg.class,
						entity.getItemId());
				if (cfg != null) {
					// 超出魂魄，兑换成材料
					if (cfg.getCostType() == GsConst.RoleSoulExchangeType.ROLE_SOUL) {
						// 抽到SSR/UR时弹出跑马灯
						if (entity.getSoulCount() >= cfg.getLimitCount() && !entity.isBroadcasted()) {
//							RoleCfg roleCfg = ConfigManager.getInstance().getConfigByKey(RoleCfg.class,
//									entity.getItemId());
							if (null != entity.getRoleCfg() && cfg.isMarqueeOn()
									&& entity.getRoleState() != Const.RoleActiviteState.IS_ACTIVITE_VALUE) {
								String msg = player.getName() + "#DD#" + String.valueOf(entity.getItemId())+ "#D#" + 8;
								GsApp.getInstance().broadcastChatWorldMsg(msg, msg);
								entity.setBroadcasted(true);
							}
						}
						if (entity.getSoulCount() >= cfg.getLimitCount()) {
							if (cfg.getExchange() != null || !cfg.getExchange().isEmpty()) {
								int sendCount = entity.getSoulCount() - cfg.getLimitCount();
								int itemCount = 0;
								while (sendCount > 0) {
									itemCount += ItemInfo.valueOf(cfg.getExchange()).getQuantity();
									sendCount--;
								}

								if (entity.getRoleState() != Const.RoleActiviteState.IS_ACTIVITE_VALUE) {
									entity.setRoleState(Const.RoleActiviteState.CAN_ACTIVITE_VALUE);
									BehaviorLogger.log4Platform(player, Action.ACTIVITE_MERCENARY_CAN_ACTIVITE, Params.valueOf("itemId", entity.getItemId()));
								}

								//多余的碎片转换成小钱
								if (itemCount > 0) {
									item.setCount(itemCount);
									item.setId(ItemInfo.valueOf(cfg.getExchange()).getItemId());
									item.setType(ItemInfo.valueOf(cfg.getExchange()).getType());
									item.setGodlyRatio(cfg.getId());
									int itemType = item.type / GsConst.ITEM_TYPE_BASE;
									switch(itemType)
									{
										case Const.itemType.PLAYER_ATTR_VALUE:
											player.increaseCrystalValue((int)item.getCount(), action);
											break;
										case Const.itemType.TOOL_VALUE:
											ItemEntity itemEntity = player.increaseTools(item.getId(), item.getCount(), action);
											if (itemEntity != null) {
												player.getPlayerData().syncItemInfo(itemEntity.getId());
												BehaviorLogger.log4Platform(player, action, Params.valueOf("id", itemEntity.getId()),
														Params.valueOf("itemId", itemEntity.getItemId()),
														Params.valueOf("itemCount", item.getCount()));
											}
										break;
										default:
											player.increaseCrystalValue((int)item.getCount(), action);
										break;
									}
									
								}

								entity.setSoulCount(cfg.getLimitCount());
								player.getPlayerData().syncPlayerInfo();
							}

						}

						// 同步收集的碎片数量
						player.getPlayerData().syncMercenarySoulInfo(roleItemId);

					}
					BehaviorLogger.log4Service(player, Source.SYS_OPERATION, action,
							Params.valueOf("itemId", item.getId()), Params.valueOf("itemCount", item.getCount()));

				}
				entity.notifyUpdate();
			}
		}
	}

	public RewardInfo.Builder rewardTakeAffect(Player player, Action action) {
		return rewardTakeAffect(player, action, null,0);
	}

	/**
	 * 发放奖励并推送，设定是否飘字
	 *
	 * @param player
	 * @param action
	 * @param flag   0：不飘字不弹窗 1：飘字 2：弹窗
	 */
	public RewardInfo.Builder rewardTakeAffectAndPush(Player player, Action action, int flag) {
		RewardInfo.Builder builder = rewardTakeAffect(player, action);
		HPPlayerReward.Builder playerRewardBuilder = HPPlayerReward.newBuilder();
		playerRewardBuilder.setRewards(builder);
		playerRewardBuilder.setFlag(flag);
		player.sendProtocol(Protocol.valueOf(HP.code.PLAYER_AWARD_S_VALUE, playerRewardBuilder));
		return builder;
	}

	/**
	 * 发放奖励并推送，设定是否飘字
	 *
	 * @param player
	 * @param action
	 * @param flag   0：不飘字不弹窗 1：飘字 2：弹窗
	 * @param type
	 */
	public RewardInfo.Builder rewardTakeAffectAndPush(Player player, Action action, int flag, RewardType type) {
		RewardInfo.Builder builder = rewardTakeAffect(player, action);
		HPPlayerReward.Builder playerRewardBuilder = HPPlayerReward.newBuilder();
		playerRewardBuilder.setRewards(builder);
		playerRewardBuilder.setFlag(flag);
		playerRewardBuilder.setRewardType(type.getNumber());
		player.sendProtocol(Protocol.valueOf(HP.code.PLAYER_AWARD_S_VALUE, playerRewardBuilder));
		return builder;
	}
	
	/**
	 * 发放奖励并推送，设定是否飘字
	 *
	 * @param player
	 * @param action
	 * @param flag   0：不飘字不弹窗 1：飘字 2：弹窗
	 * @param type
	 */
	public RewardInfo.Builder rewardTakeAffectAndPush(Player player, Action action, int flag,int tapAction,Params... params) {
		RewardInfo.Builder builder = rewardTakeAffect(player, action,null,tapAction,params);
		HPPlayerReward.Builder playerRewardBuilder = HPPlayerReward.newBuilder();
		playerRewardBuilder.setRewards(builder);
		playerRewardBuilder.setFlag(flag);
		player.sendProtocol(Protocol.valueOf(HP.code.PLAYER_AWARD_S_VALUE, playerRewardBuilder));
		return builder;
	}

	/**
	 * 填充玩家属性奖励
	 *
	 * @param player
	 * @param awardItems
	 * @param attrType
	 * @param quantity
	 */
	public static void fillPlayerAttrAward(Player player, AwardItems awardItems, int attrType, int quantity) {
		switch (attrType) {
		case Const.playerAttr.GOLD_VALUE:
			awardItems.addGold(quantity);
			break;

		case Const.playerAttr.COIN_VALUE:
			awardItems.addCoin(quantity);
			break;

		case Const.playerAttr.LEVEL_VALUE:
			awardItems.setLevel(quantity);
			break;

		case Const.playerAttr.EXP_VALUE:
			awardItems.addExp(quantity);
			break;

		case Const.playerAttr.SMELT_VALUE_VALUE:
			awardItems.addSmeltValue(quantity);
			break;

		case Const.playerAttr.CONTRIBUTION_VALUE:
			awardItems.addContribution(quantity);
			break;

		case Const.playerAttr.ALLIANCE_EXP_VALUE:
			awardItems.addAllianceExp(quantity);
			break;

		case Const.playerAttr.BOSS_TIMES_VALUE:
			awardItems.addBossFightTimes(quantity);
			break;

		case Const.playerAttr.ALLIANCE_VITALITY_VALUE:
			awardItems.addAllianceVitality(quantity);
			break;

		case Const.playerAttr.CROSS_COIN_VALUE:
			awardItems.addCrossCoin(quantity);
			break;

		default:
			break;
		}
	}

	/**
	 * 具体发放奖励
	 *
	 * @param player
	 * @param action
	 * @param async
	 */
	public void deliverItemAwards(Player player, RewardInfo.Builder builder, Action action,
			List<DetailElement.Builder> builderList,int tapAction,Params... params) {
		List<Item> needCreateItemList = new LinkedList<Item>();
		List<Item> badgeCreateItemList = new LinkedList<>();
		// 实体对象奖励(/10000)
		for (Item item : awardItems) {
			// 配置出错误
			if (item.getCount() <= 0 || item.getId() <= 0) {
				continue;
			}

			int itemType = item.type / GsConst.ITEM_TYPE_BASE;
			boolean iswork = true;
			if (itemType == com.guaji.game.protocol.Const.itemType.PLAYER_ATTR_VALUE) {
				
				iswork = true;
				// 玩家属性
				switch (item.getId()) {
				case playerAttr.COIN_VALUE:
					player.increaseCoin((int)item.getCount(), action);
					builder.setCoin(player.getCoin());
					break;

				case playerAttr.GOLD_VALUE:
					player.increaseGold((int)item.getCount(), action);
					builder.setGold(player.getGold());
					break;

				case playerAttr.LEVEL_VALUE:
					player.increaseLevel((int)item.getCount(), action);
					builder.setLevel(player.getLevel());
					break;

				case playerAttr.EXP_VALUE:
					player.increaseExp((int)item.getCount(), action);
					builder.setExp(player.getExp());
					builder.setLevel(player.getLevel());
					break;

				case playerAttr.VIPLEVEL_VALUE:
					player.setVipLevel((int)item.getCount(), action);
					builder.setVipLevel(player.getVipLevel());
					break;

				case playerAttr.SMELT_VALUE_VALUE:
					player.increaseSmeltValue((int)item.getCount(), action);
					builder.setSmeltValue(player.getSmeltValue());
					break;

				case playerAttr.CONTRIBUTION_VALUE:
					player.increaseAllianceContribution((int)item.getCount(), action);
					builder.setContribution(player.getAllianceContribution());
					break;

				case playerAttr.ALLIANCE_EXP_VALUE:
					player.increaseAllianceExp((int)item.getCount(), action);
					break;

				case playerAttr.BOSS_TIMES_VALUE:
					player.increaseBossFightTimes((int)item.getCount(), action);
					builder.setBossFightTimes(player.getBossFightTimes());
					break;

				case playerAttr.HONOR_VALUE_VALUE:
					player.increaseHonorValue((int)item.getCount(), action);
					builder.setHonorValue(player.getPlayerData().getPlayerEntity().getHonorValue());
					break;

				case playerAttr.REPUTATION_VALUE_VALUE:
					player.increaseReputationValue((int)item.getCount(), action);
					builder.setReputationValue(player.getPlayerData().getPlayerEntity().getReputationValue());
					break;

				case playerAttr.CRYSTAL_VALUE_VALUE:
					player.increaseCrystalValue((int)item.getCount(), action);
					builder.setCrystalValue(player.getPlayerData().getPlayerEntity().getCrystalValue());
					break;

				case playerAttr.ELITE_MAP_TIMES_VALUE:
					player.increaseEliteMapFightTimes((int)item.getCount(), action);
					player.getPlayerData().syncStateInfo();// 变化更新
					break;
					
				case playerAttr.MULTI_ELITE_TIMES_VALUE:
					player.increaseMultiEliteTimes((int)item.getCount(), action);
					builder.setMultieliteTimes(player.getMultiEliteTimes());
					break;

//				case playerAttr.FASTFIGHT_TIMES_VALUE:
//					player.increaseFastFightTimes(item.getCount(), action);
//					player.getPlayerData().syncStateInfo();
//					break;

				case playerAttr.ALLIANCE_VITALITY_VALUE:
					AllianceManager.getInstance().addAllianceBossVitality(player.getId(),(int) item.getCount(), action);
					break;

				case playerAttr.MULTI_ELITE_SCORE_VALUE:
					player.increaseMultiEliteScore((int)item.getCount(), action);
					break;

				case Const.playerAttr.CROSS_COIN_VALUE:
					CrossBattleService.getInstance().addCrossCoin(player.getId(), (int)item.getCount());
					BehaviorLogger.log4Service(player, Source.PLAYER_ATTR_CHANGE, action,
							Params.valueOf("playerAttr", Const.playerAttr.CROSS_COIN_VALUE),
							Params.valueOf("add", item.getCount()));
					break;
				case Const.playerAttr.BIG_MEDICALKIT_VALUE:
					player.increaseEighteenPrincesMedicalKit(Const.playerAttr.BIG_MEDICALKIT_VALUE,(int) item.count, action);
					break;
				case Const.playerAttr.MIDLE_MEDICALKIT_VALUE:
					player.increaseEighteenPrincesMedicalKit(Const.playerAttr.MIDLE_MEDICALKIT_VALUE, (int)item.count, action);
					break;
				case Const.playerAttr.SMALL_MEDICALKIT_VALUE:
					player.increaseEighteenPrincesMedicalKit(Const.playerAttr.SMALL_MEDICALKIT_VALUE,(int) item.count, action);
					break;
				case Const.playerAttr.FRIENDSHIP_VALUE_VALUE:
					player.increaseFriendship((int)item.count, action);
					break;
				case Const.playerAttr.VIPPOINT_VALUE_VALUE:
					player.increaseVipPoint((int)item.count, action);
					break;

				default:
					iswork = false;
					break;
				}
				
				if (iswork) {
					TapDBUtil.Event_GetItem(player,player.getTapDBUId(),action,tapAction,item.type,item.id,(int)item.count,params);
					iswork = false;
				} else {
					iswork = true;
				}
			}
			
			try {
				switch (itemType) {
				case Const.itemType.TOOL_VALUE:
					ItemEntity itemEntity = player.increaseTools(item.getId(), item.getCount(), action);
					if (itemEntity != null) {
						player.getPlayerData().syncItemInfo(itemEntity.getId());
//						BehaviorLogger.log4Platform(player, action, Params.valueOf("id", itemEntity.getId()),
//								Params.valueOf("itemId", itemEntity.getItemId()),
//								Params.valueOf("itemCount", item.getCount()));
					} else {
						iswork = false;
					}
					break;

				case Const.itemType.EQUIP_VALUE:
					needCreateItemList.add(item);
					break;

				case Const.itemType.ELEMENT_VALUE:
					for (int i = 0; i < item.getCount(); i++) {
						AttrInfo.Builder attr = null;
						if (builderList != null) {
							for (DetailElement.Builder detailBuilder : builderList) {
								if (detailBuilder.getItemId() == item.getId()) {
									attr = detailBuilder.getBasicAttrsBuilder();
								}
							}
						}
						ElementEntity elementEntity = player.increaseElement(item.getId(), action, attr);
						if (elementEntity != null) {
							player.getPlayerData().syncElementInfo(elementEntity.getId());
						}
					}
					break;

				case Const.itemType.SKIN_VALUE:
					player.increaseRoleSkin(item, action);
					break;
				case Const.itemType.SOUL_VALUE:
//					player.increaseRoleSoul(awardItems, item, action);
					break;
				case Const.itemType.SKILL_VALUE:
					break;
				case Const.itemType.AVATAR_VALUE:
					player.getPlayerData().addAvatar(item.getId());
					break;
				case Const.itemType.BADGE_VALUE:
                    badgeCreateItemList.add(item);
				    break;

				default:
					iswork = false;
					break;
				}

			} catch (Exception e) {
				MyException.catchException(e);
			}
			
			if (iswork) {
				TapDBUtil.Event_GetItem(player,player.getTapDBUId(),action,tapAction,item.type,item.id,(int)item.count,params);
			}
		}
		


		if (needCreateItemList.size() > 0) {
			List<EquipEntity> needSyncDbList = new LinkedList<EquipEntity>();
			for (Item item : needCreateItemList) {
				for (int i = 0; i < item.getCount(); i++) {
					EquipEntity equipEntity = EquipUtil.generateEquip(player, item.getId(), item.getGodlyRatio(), false,
							item.punchSize, item.isFullAttr);
					if (equipEntity != null) {
						needSyncDbList.add(equipEntity);
					}
				}
			}

			// db创建装备
			EquipUtil.createEquipsSync(needSyncDbList);

			List<Long> equipIds = new ArrayList<Long>();
			for (EquipEntity equipEntity : needSyncDbList) {
				equipIds.add(equipEntity.getId());

				// 刷新逻辑并添加到装备列表
				EquipUtil.refreshAttribute(equipEntity, player.getPlayerData());
				if (equipEntity != null) {
					player.getPlayerData().addEquipEntity(equipEntity);
				}

				BehaviorLogger.log4Service(player, Source.EQUIP_ADD, action,
						Params.valueOf("equipId", equipEntity.getEquipId()), Params.valueOf("id", equipEntity.getId()),
						Params.valueOf("attr", equipEntity.getAttribute().toString()),
						Params.valueOf("isGodly", equipEntity.getGodlyAttrId() > 0));

				BehaviorLogger.log4Platform(player, action, Params.valueOf("equipId", equipEntity.getEquipId()),
						Params.valueOf("id", equipEntity.getId()),
						Params.valueOf("attr", equipEntity.getAttribute().toString()));
			}

			// 同步信息
			player.getPlayerData().syncEquipInfoReward(equipIds.toArray(new Long[equipIds.size()]));
		}

		if (badgeCreateItemList.size()>0){
			List<BadgeEntity> needSyncDbList = new LinkedList<>();
			for (Item item : badgeCreateItemList) {
				for (int i = 0; i < item.getCount(); i++) {
					BadgeEntity badgeEntity = BadgeUtil.generateBadge(player, item.getId());
					if (badgeEntity != null) {
						needSyncDbList.add(badgeEntity);
					}
				}
			}

			// db创建装备
			BadgeUtil.createBadgesSync(needSyncDbList);

			List<Long> badgeIds = new ArrayList<Long>();
			for (BadgeEntity badgeEntity : needSyncDbList) {
				badgeIds.add(badgeEntity.getId());

				// 刷新逻辑并添加到装备列表
				//BadgeUtil.refreshAttribute(badgeEntity, player.getPlayerData());
				if (badgeEntity != null) {
					player.getPlayerData().addBadgeEntity(badgeEntity);
				}

				BehaviorLogger.log4Service(player, Source.BADGE_ADD, action,
						Params.valueOf("badgeId", badgeEntity.getBadgeId()), Params.valueOf("id", badgeEntity.getId()),
						Params.valueOf("attr", badgeEntity.getAttribute().toString()),
						Params.valueOf("skill", badgeEntity.getSkill()));
				
				BehaviorLogger.log4Platform(player, action, Params.valueOf("badgeId", badgeEntity.getBadgeId()),
						Params.valueOf("id", badgeEntity.getId()),
						Params.valueOf("attr", badgeEntity.getAttribute().toString()),
						Params.valueOf("skill", badgeEntity.getSkill()));
			}

			// 同步信息
			player.getPlayerData().syncBadgeInfo(badgeIds.toArray(new Long[badgeIds.size()]));
        }
	}
}
