package com.guaji.game.item;

import java.util.ArrayList;
import java.util.List;

import org.guaji.log.Log;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.MyException;

import com.google.protobuf.ProtocolMessageEnum;
import com.guaji.game.entity.ElementEntity;
import com.guaji.game.entity.EquipEntity;
import com.guaji.game.entity.ItemEntity;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.manager.crossbattle.CrossBattleService;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerData;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.Const.itemType;
import com.guaji.game.protocol.Consume.ConsumeItem;
import com.guaji.game.protocol.Consume.HPConsumeInfo;
import com.guaji.game.protocol.Consume.SyncAttrInfo;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsConst.PlayerItemCheckResult;

public class ConsumeItems {
	/**
	 * 消耗信息
	 */
	private HPConsumeInfo.Builder consumeInfo;//存储一个实例对象（HPConsumeInfo.Builder类型，工厂模式初始化和生成相应的message对象）

	public ConsumeItems() {
		consumeInfo = HPConsumeInfo.newBuilder();

		SyncAttrInfo.Builder builder = SyncAttrInfo.newBuilder();
		consumeInfo.setAttrInfo(builder);
	}

	public static ConsumeItems valueOf() {
		return new ConsumeItems();
	}

	public static ConsumeItems valueOf(ProtocolMessageEnum type, int count) {
		ConsumeItems consumeItems = new ConsumeItems();
		consumeItems.addChangeInfo(type, count);
		return consumeItems;
	}
	
	public static ConsumeItems valueOf(ProtocolMessageEnum type, int id, int itemId, int count) {
		ConsumeItems consumeItems = new ConsumeItems();
		consumeItems.addChangeInfo(type, id, itemId, count);
		return consumeItems;
	}

	public HPConsumeInfo.Builder getBuilder() {
		return consumeInfo;
	}

	/**
	 * type 参考 Const.changeInfo
	 * 
	 * @param type
	 */
	public void addChangeInfo(ProtocolMessageEnum type, int count) {
		SyncAttrInfo.Builder builder = consumeInfo.getAttrInfoBuilder();
		switch (type.getNumber()) {
		case Const.changeType.CHANGE_GOLD_VALUE:
			builder.setGold(count);
			break;

		case Const.changeType.CHANGE_COIN_VALUE:
			builder.setCoin(count);
			break;

		case Const.changeType.CHANGE_EXP_VALUE:
			builder.setExp(count);
			break;

		case Const.changeType.CHANGE_LEVEL_VALUE:
			builder.setLevel(count);
			break;

		case Const.changeType.CHANGE_VIPLEVEL_VALUE:
			builder.setVipLevel(count);
			break;
			
		case Const.changeType.CHANGE_SMELT_VALUE_VALUE:
			builder.setSmeltValue(count);
			break;
			
		case Const.changeType.CHANGE_CONTRIBUTION_VALUE:
			builder.setContribution(count);
			break;
			
		case Const.changeType.CHANGE_HONOR_VALUE_VALUE:
			builder.setHonorValue(count);
			break;
			
		case Const.changeType.CHANGE_REPUTATION_VALUE_VALUE:
			builder.setReputationValue(count);
			break;
			
		case Const.changeType.CHANGE_CRYSTAL_VALUE:
			builder.setCrystalValue(count);
			break;
			
		case Const.changeType.CHANGE_CROSS_COIN_VALUE:
			builder.setCrossCoin(count);
			break;
			
		case Const.changeType.CHANGE_FRIENDSHIP_VALUE:
			builder.setFriendship(count);
			break;
			
		default:
			Log.errPrintln("unsupport change info: " + type.getNumber());
			break;
		}
	}

	/**
	 * 添加修改信息
	 * 
	 * @param type
	 * @param id
	 * @param itemId
	 * @param count
	 */
	public void addChangeInfo(ProtocolMessageEnum type, long id, int itemId, long count) {
		ConsumeItem.Builder builder = ConsumeItem.newBuilder();
		builder.setType(type.getNumber());
		builder.setId(id);
		builder.setItemId(itemId);
		builder.setCount(count);
		consumeInfo.addConsumeItem(builder);
	}

	/**
	 * 同步改变信息
	 * 
	 * @param player
	 * @return
	 */
	public boolean pushChange(Player player) {
		HPConsumeInfo consumes = consumeInfo.build();
		SyncAttrInfo syncAttrInfo = consumes.getAttrInfo();
		boolean isSuccess = false;
		
		if(syncAttrInfo.getCoin() > 0) {
			consumeInfo.getAttrInfoBuilder().setCoin(player.getCoin());
			isSuccess = true;
		}
		
		if(syncAttrInfo.getGold() > 0) {
			consumeInfo.getAttrInfoBuilder().setGold(player.getGold());
			isSuccess = true;
		}
		
		if(syncAttrInfo.getSmeltValue() > 0) {
			consumeInfo.getAttrInfoBuilder().setSmeltValue(player.getSmeltValue());
			isSuccess = true;
		}
		
		if(syncAttrInfo.getHonorValue() > 0) {
			consumeInfo.getAttrInfoBuilder().setHonorValue(player.getPlayerData().getPlayerEntity().getHonorValue());
			isSuccess = true;
		}
		
		if(syncAttrInfo.getReputationValue() > 0) {
			consumeInfo.getAttrInfoBuilder().setReputationValue(player.getPlayerData().getPlayerEntity().getReputationValue());
			isSuccess = true;
		}
		
		if(syncAttrInfo.getCrystalValue() > 0) {
			consumeInfo.getAttrInfoBuilder().setCrystalValue(player.getPlayerData().getPlayerEntity().getCrystalValue());
			isSuccess = true;
		}
		
		if (syncAttrInfo.getCrossCoin() > 0) {
			int crossCoin = CrossBattleService.getInstance().getCrossCoin(player.getId());
			consumeInfo.getAttrInfoBuilder().setCrossCoin(crossCoin);
			isSuccess = true;
		}
		
		if (syncAttrInfo.getFriendship() > 0) {
			consumeInfo.getAttrInfoBuilder().setFriendship(player.getPlayerData().getStateEntity().getFriendship());
			isSuccess = true;
		}
		
		if(isSuccess || consumes.getConsumeItemList().size() > 0){
			player.sendProtocol(Protocol.valueOf(HP.code.PLAYER_CONSUME_S, consumeInfo));
		}
		
		return true;
	}

	/**
	 * 检测是否可消耗
	 * 
	 * @return
	 */
	public boolean checkConsume(Player player) {
		return checkConsume0(player) == 0;
	}

	/**
	 * 数据消耗
	 */
	public boolean consumeTakeAffect(Player player, Action action) {
		HPConsumeInfo consumes = consumeInfo.build();
		SyncAttrInfo syncAttrInfo = consumes.getAttrInfo();
		try{
			if(syncAttrInfo.getCoin() > 0) {
				player.consumeCoin(syncAttrInfo.getCoin(), action);
			}
			
			if(syncAttrInfo.getGold() > 0) {
				player.consumeGold(syncAttrInfo.getGold(), action);
			}
			
			if(syncAttrInfo.getSmeltValue() > 0) {
				player.consumeSmeltValue(syncAttrInfo.getSmeltValue(), action);
			}
			
			if(syncAttrInfo.getContribution() > 0) {
				player.consumeContribution(syncAttrInfo.getContribution(), action);
			}
			
			if(syncAttrInfo.getHonorValue() > 0) {
				player.consumeHonor(syncAttrInfo.getHonorValue(), action);
			}
			
			if(syncAttrInfo.getReputationValue() > 0) {
				player.consumeReputationValue(syncAttrInfo.getReputationValue(), action);
			}
			
			if(syncAttrInfo.getCrystalValue() > 0) {
				player.consumeCrystalValue(syncAttrInfo.getCrystalValue(), action);
			}
			
			if(syncAttrInfo.getCrossCoin() > 0) {
				CrossBattleService.getInstance().consumeCrossCoin(player.getId(), syncAttrInfo.getCrossCoin());
			}
			
			if (syncAttrInfo.getFriendship() > 0) {
				player.consumeFriendship(syncAttrInfo.getFriendship(), action);
			}
			
			for(ConsumeItem consumeItem : consumes.getConsumeItemList()) {
				if(consumeItem.getType() == changeType.CHANGE_EQUIP_VALUE) {
					//检测装备 
					long equipId = consumeItem.getId();
					EquipEntity equipEntity = player.getPlayerData().getEquipById(equipId);
					player.consumeEquip(equipEntity.getId(), action);
				} else if(consumeItem.getType() == changeType.CHANGE_TOOLS_VALUE) {
					//检测道具
					int itemId = consumeItem.getItemId();
					//检测消耗道具
					ActivityUtil.triggerConsumeItem(player.getPlayerData(),itemId,(int)consumeItem.getCount());
					player.consumeTools(itemId, consumeItem.getCount(), action);
				} else if(consumeItem.getType() == changeType.CHANGE_ELEMENT_VALUE) {
					//检测元素
					long eleId = consumeItem.getId();
					ElementEntity ele = player.getPlayerData().getElementById(eleId);
					if(ele != null) {
						player.consumElement(ele.getId(), action);
					}
				}
			}
			
			// 推送消耗信息
			pushChange(player);
		}catch (Exception e) {
			MyException.catchException(e);
			return false;
		}
		return true;
	}
	
	/**
	 * 检测是否可消耗
	 * @param player
	 * @param hpCode
	 * @return
	 */
	public boolean checkConsume(Player player, int hpCode) {
		int result = checkConsume0(player);
		if(result > 0) {
			if(hpCode > 0) {
				switch (result) {
					case PlayerItemCheckResult.COINS_NOT_ENOUGH:
						player.sendError(hpCode, Status.error.COINS_NOT_ENOUGH);
						break;
					case PlayerItemCheckResult.GOLD_NOT_ENOUGH:
						player.sendError(hpCode, Status.error.GOLD_NOT_ENOUGH);
						break;
					case PlayerItemCheckResult.SMELT_VALUE_NOT_ENOUGH:
						player.sendError(hpCode, Status.error.EQUIP_CREATE_SMELT_VALUE_NOT_ENOUGH);
						break;
					case PlayerItemCheckResult.EQUIP_NOI_ENOUGH:
						player.sendError(hpCode, Status.error.EQUIP_NOT_FOUND);
						break;
					case PlayerItemCheckResult.TOOLS_NOT_ENOUGH:
						player.sendError(hpCode, Status.error.ITEM_NOT_ENOUGH);
						break;
					case PlayerItemCheckResult.HONOR_NOT_ENOUGH:
						player.sendError(hpCode, Status.error.HORNOR_NOT_ENOUGH);
						break;
					case PlayerItemCheckResult.	REPUTATION_NOT_ENOUGH:
						player.sendError(hpCode, Status.error.REPUTATION_NOT_ENOUGH);
						break;
					case PlayerItemCheckResult.CRYSTAL_NOT_ENOUGH:
						player.sendError(hpCode, Status.error.CRYSTAL_NOT_ENOUGH);
						break;
					case PlayerItemCheckResult.	ELEMENT_NOI_ENOUGH:
						player.sendError(hpCode, Status.error.ELEMENT_NOI_ENOUGH);
						break;
					case PlayerItemCheckResult.CONTRIBUTION_NOT_ENOUGH:
						player.sendError(hpCode, Status.error.ALLIANCE_NOT_CONTRIBUTION);
						break;
					case PlayerItemCheckResult.CROSSCOINS_NOT_ENOUGH:
						player.sendError(hpCode, Status.error.CROSSCOINS_NOT_ENOUGH);
						break;
					case PlayerItemCheckResult.FRIENDSHIP_NOT_ENOUGH:
						player.sendError(hpCode, Status.error.FRIENDSHIP_NOT_ENOUGH);
						break;
					default:
						break;
					}
			}
			return false;
		}
		return true;
	}
	
	/**
	 * 检测消耗物品或者属性数量是否充足
	 * @param player
	 * @return
	 */
	private int checkConsume0(Player player) {
		HPConsumeInfo consumes = consumeInfo.build();
		SyncAttrInfo syncAttrInfo = consumes.getAttrInfo();
		
		if(syncAttrInfo.getCoin() > 0) {
			if(player.getCoin() < syncAttrInfo.getCoin()) {
				return PlayerItemCheckResult.COINS_NOT_ENOUGH;
			}
		}
		
		if(syncAttrInfo.getGold() > 0) {
			if(player.getGold() < syncAttrInfo.getGold()) {
				return PlayerItemCheckResult.GOLD_NOT_ENOUGH;
			}
		}
		
		if (syncAttrInfo.getContribution() > 0) {
			if(player.getPlayerData().getPlayerAllianceEntity().getContribution() < syncAttrInfo.getContribution()) {
				return PlayerItemCheckResult.CONTRIBUTION_NOT_ENOUGH;
			}
		}
		
		if(syncAttrInfo.getSmeltValue() > 0) {
			if(player.getSmeltValue() < syncAttrInfo.getSmeltValue()) {
				return PlayerItemCheckResult.SMELT_VALUE_NOT_ENOUGH;
			}
		}
		
		if(syncAttrInfo.getHonorValue() > 0) {
			if(player.getPlayerData().getPlayerEntity().getHonorValue() < syncAttrInfo.getHonorValue()) {
				return PlayerItemCheckResult.HONOR_NOT_ENOUGH;
			}
		}
		
		if(syncAttrInfo.getReputationValue() > 0) {
			if(player.getPlayerData().getPlayerEntity().getReputationValue() < syncAttrInfo.getReputationValue()) {
				return PlayerItemCheckResult.REPUTATION_NOT_ENOUGH;
			}
		}
		
		if(syncAttrInfo.getCrystalValue() > 0) {
			if(player.getPlayerData().getPlayerEntity().getCrystalValue() < syncAttrInfo.getCrystalValue()) {
				return PlayerItemCheckResult.CRYSTAL_NOT_ENOUGH;
			}
		}
		
		if(syncAttrInfo.getCrossCoin() > 0) {
			int crossCoin = CrossBattleService.getInstance().getCrossCoin(player.getId());
			if(crossCoin < syncAttrInfo.getCrossCoin()) {
				return PlayerItemCheckResult.CROSSCOINS_NOT_ENOUGH;
			}
		}
		
		if(syncAttrInfo.getFriendship() > 0) {
			if(player.getPlayerData().getStateEntity().getFriendship() < syncAttrInfo.getFriendship()) {
				return PlayerItemCheckResult.FRIENDSHIP_NOT_ENOUGH;
			}
		}
		
		for(ConsumeItem consumeItem : consumes.getConsumeItemList()) {
			if(consumeItem.getType() == changeType.CHANGE_EQUIP_VALUE) {
				//检测装备 
				long equipId = consumeItem.getId();
				EquipEntity equipEntity = player.getPlayerData().getEquipById(equipId);
				if(equipEntity == null) {
					return PlayerItemCheckResult.EQUIP_NOI_ENOUGH;
				}
			} else if(consumeItem.getType() == changeType.CHANGE_TOOLS_VALUE) {
				//检测道具
				int itemId = (int) consumeItem.getId();
				ItemEntity itemEntity = player.getPlayerData().getItemById(itemId);
				if(itemEntity == null || itemEntity.getItemCount() <= 0 || itemEntity.getItemCount() < consumeItem.getCount()) {
					return PlayerItemCheckResult.TOOLS_NOT_ENOUGH;
				}
			} else if(consumeItem.getType() == changeType.CHANGE_ELEMENT_VALUE) {
				//检测元素
				int eleId = (int) consumeItem.getId();
				ElementEntity ele = player.getPlayerData().getElementById(eleId);
				if(ele == null) {
					return PlayerItemCheckResult.ELEMENT_NOI_ENOUGH;
				}
			}
		}
		
		return 0;
	}
	
	public boolean addConsumeInfo(PlayerData playerData, ItemInfo needItem) {
		List<ItemInfo> needItems = new ArrayList<ItemInfo>();
		needItems.add(needItem);
		return addConsumeInfo(playerData,needItems);
	}
	
	public boolean addConsumeInfo(PlayerData playerData, List<ItemInfo> needItems) {
		SyncAttrInfo.Builder builder = consumeInfo.getAttrInfoBuilder();
		for(ItemInfo itemInfo : needItems) {
			if(itemInfo.getType() / GsConst.ITEM_TYPE_BASE == itemType.PLAYER_ATTR_VALUE) {
				switch (itemInfo.getItemId()) {
					case Const.playerAttr.GOLD_VALUE:
						builder.setGold((int)itemInfo.getQuantity());
						break;
					case Const.playerAttr.COIN_VALUE:	
						builder.setCoin((int)itemInfo.getQuantity());
						break;
					case Const.playerAttr.EXP_VALUE:
						builder.setExp((int)itemInfo.getQuantity());
						break;
					case Const.playerAttr.LEVEL_VALUE:
						builder.setLevel((int)itemInfo.getQuantity());
						break;
					case Const.playerAttr.VIPLEVEL_VALUE:
						builder.setVipLevel((int)itemInfo.getQuantity());
						break;
					case Const.playerAttr.SMELT_VALUE_VALUE:
						builder.setSmeltValue((int)itemInfo.getQuantity());
						break;
					case Const.playerAttr.CONTRIBUTION_VALUE:
						builder.setContribution((int)itemInfo.getQuantity());
						break;
					case Const.playerAttr.HONOR_VALUE_VALUE:
						builder.setHonorValue((int)itemInfo.getQuantity());
						break;
					case Const.playerAttr.REPUTATION_VALUE_VALUE:
						builder.setReputationValue((int)itemInfo.getQuantity());
						break;
					case Const.playerAttr.CRYSTAL_VALUE_VALUE:
						builder.setCrystalValue((int)itemInfo.getQuantity());
						break;
					case Const.playerAttr.CROSS_COIN_VALUE:
						builder.setCrossCoin((int)itemInfo.getQuantity());
						break;
					default:
						break;
				}
			}else if(itemInfo.getType() / GsConst.ITEM_TYPE_BASE == itemType.TOOL_VALUE){
				ItemEntity itemEntity = playerData.getItemByItemId(itemInfo.getItemId());
				if(itemEntity == null || itemEntity.getItemCount() < itemInfo.getQuantity()) {
					//构造失败 道具不足
					return false;
				}
				addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), itemEntity.getItemId(), itemInfo.getQuantity());
			}
		}
		return true;
	}
	/**
	 * 累加消耗物品
	 * @param playerData
	 * @param needItems
	 * @return
	 */
	public boolean IncConsumeInfo(PlayerData playerData, List<ItemInfo> needItems) {
		SyncAttrInfo.Builder builder = consumeInfo.getAttrInfoBuilder();

		for(ItemInfo itemInfo : needItems) {
			if(itemInfo.getType() / GsConst.ITEM_TYPE_BASE == itemType.PLAYER_ATTR_VALUE) {
				switch (itemInfo.getItemId()) {
					case Const.playerAttr.GOLD_VALUE:
						builder.setGold(builder.getGold()+(int)itemInfo.getQuantity());
						break;
					case Const.playerAttr.COIN_VALUE:
						builder.setCoin(builder.getCoin()+(int)itemInfo.getQuantity());
						break;
					case Const.playerAttr.EXP_VALUE:
						builder.setExp(builder.getExp()+(int)itemInfo.getQuantity());
						break;
					case Const.playerAttr.LEVEL_VALUE:
						builder.setLevel(builder.getLevel()+(int)itemInfo.getQuantity());
						break;
					case Const.playerAttr.VIPLEVEL_VALUE:
						builder.setVipLevel(builder.getVipLevel()+(int)itemInfo.getQuantity());
						break;
					case Const.playerAttr.SMELT_VALUE_VALUE:
						builder.setSmeltValue(builder.getSmeltValue()+(int)itemInfo.getQuantity());
						break;
					case Const.playerAttr.CONTRIBUTION_VALUE:
						builder.setContribution(builder.getContribution()+(int)itemInfo.getQuantity());
						break;
					case Const.playerAttr.HONOR_VALUE_VALUE:
						builder.setHonorValue(builder.getHonorValue()+(int)itemInfo.getQuantity());
						break;
					case Const.playerAttr.REPUTATION_VALUE_VALUE:
						builder.setReputationValue(builder.getReputationValue()+(int)itemInfo.getQuantity());
						break;
					case Const.playerAttr.CRYSTAL_VALUE_VALUE:
						builder.setCrystalValue(builder.getCrystalValue()+(int)itemInfo.getQuantity());
						break;
					case Const.playerAttr.CROSS_COIN_VALUE:
						builder.setCrossCoin(builder.getCrossCoin()+(int)itemInfo.getQuantity());
						break;
					default:
						break;
				}
			}else if(itemInfo.getType() / GsConst.ITEM_TYPE_BASE == itemType.TOOL_VALUE){
				ItemEntity itemEntity = playerData.getItemByItemId(itemInfo.getItemId());
				if(itemEntity == null || itemEntity.getItemCount() < itemInfo.getQuantity()) {
					//构造失败 道具不足
					return false;
				}
				addChangeInfo(changeType.CHANGE_TOOLS, itemEntity.getId(), itemEntity.getItemId(), itemInfo.getQuantity());
			}
		}
		return true;
	}
}
