package com.guaji.game.battle;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.MyException;

import com.guaji.game.protocol.Attribute.Attr;
import com.guaji.game.protocol.Attribute.AttrInfo;
import com.guaji.game.attribute.MapAttr;
import com.guaji.game.config.ElementAttrCfg;
import com.guaji.game.config.ElementCfg;
import com.guaji.game.config.ElementDecomposeCfg;
import com.guaji.game.config.EquipCfg;
import com.guaji.game.config.ItemCfg;
import com.guaji.game.config.MonsterCfg;
import com.guaji.game.config.NewMapCfg;
import com.guaji.game.config.VipPrivilegeCfg;
import com.guaji.game.entity.ItemEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.DropItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.AwardUtil;
import com.guaji.game.util.ElementUtil;
import com.guaji.game.util.EquipUtil;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.WeightUtil;
import com.guaji.game.protocol.Battle;
import com.guaji.game.protocol.Battle.DropAward;
import com.guaji.game.protocol.Battle.DropAward.Builder;
import com.guaji.game.protocol.Battle.DropAward.DetailElement;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.changeType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Reward.HPPlayerReward;
import com.guaji.game.protocol.Reward.RewardInfo;

public class MapReward {
	/**
	 * 战斗类型
	 */
	int battleType;
	/**
	 * 玩家对象
	 */
	Player player;
	/**
	 * 地图配置
	 */
	NewMapCfg mapCfg;
	/**
	 * 最大装备数量限制
	 */
	private int maxEquipCount;
	/**
	 * 物品id映射表
	 */
	Map<Integer, Long> itemIdCount;
	/**
	 * 所打的怪物id列表
	 */
	List<MonsterCfg> monsterList;
	/**
	 * 奖励信息
	 */
	AwardItems awardItems;
	/**
	 * 消耗信息
	 */
	ConsumeItems consumeItems;
	/**
	 * 设置延迟发奖时间
	 */
	int delayRewardTime;
	/**
	 * 是否发放
	 */
	boolean hasReward;
	/**
	 * 掉落信息
	 */
	DropAward.Builder dropAward;
	/**
	 * 最大元素数量限制
	 */
	private int maxElementCount;

	public MapReward(Player player, NewMapCfg mapCfg, int battleType) {
		this.player = player;
		this.mapCfg = mapCfg;
		this.battleType = battleType;
		this.hasReward = false;
		
		this.dropAward = DropAward.newBuilder();
		this.awardItems = new AwardItems();
		this.consumeItems = new ConsumeItems();
		this.maxEquipCount = EquipUtil.getEmptyEquipSlotCount(player);
		this.maxElementCount = ElementUtil.getEmptyElementSlotCount(player);
		this.itemIdCount = player.getPlayerData().getItemIdCountMap();
	}

	public boolean init(Player player, NewMapCfg mapCfg, int battleType) {
		this.player = player;
		this.mapCfg = mapCfg;
		this.battleType = battleType;
		this.hasReward = false;
		
		this.maxEquipCount = EquipUtil.getEmptyEquipSlotCount(player);
		this.maxElementCount = ElementUtil.getEmptyElementSlotCount(player);
		this.itemIdCount = player.getPlayerData().getItemIdCountMap();
		return this.player != null && this.mapCfg != null;
	}
	
	public int getDelayRewardTime() {
		return delayRewardTime;
	}

	public void setDelayRewardTime(int delayRewardTime) {
		this.delayRewardTime = delayRewardTime;
	}
	
	public boolean hasReward() {
		return hasReward;
	}
	
	public DropAward.Builder getDropAward() {
		return dropAward;
	}

	public AwardItems getAwardItems() {
		return awardItems;
	}

	public ConsumeItems getConsumeItems() {
		return consumeItems;
	}

	public int getBattleType() {
		return battleType;
	}
	
	public List<MonsterCfg> getMonsterList() {
		return monsterList;
	}

	public void setMonsterList(List<MonsterCfg> monsterList) {
		this.monsterList = monsterList;
	}
	
	/**
	 * 掉落发奖和消耗
	 * 
	 * @return
	 */
	public boolean dropTakeAffect(Action action) {
		try {
			if (!hasReward) {
				if (action == null) {
					if (battleType == Battle.battleType.BATTLE_PVE_MONSTER_VALUE) {
						action = Action.MONSTER_FIGHTING;
					} else if (battleType == Battle.battleType.BATTLE_PVE_BOSS_VALUE) {
						action = Action.BOSS_FIGHTING;
					} else if (battleType == Battle.battleType.BATTLE_PVE_ELITE_BOSS_VALUE) {
						action = Action.ELITE_MAP_FIGHTING;
					} 
				}
				
				// 异步发奖
				if (consumeItems.checkConsume(player) && consumeItems.consumeTakeAffect(player, action)) {
					List<DetailElement.Builder> builderList = dropAward.getDetailElementBuilderList();
					RewardInfo.Builder builder = awardItems.rewardTakeAffect(player, action, builderList,0);
					hasReward = true;
					
					// 通知推送
					HPPlayerReward.Builder playerRewardBuilder = HPPlayerReward.newBuilder();
					playerRewardBuilder.setFlag(0);
					playerRewardBuilder.setRewards(builder);
					player.sendProtocol(Protocol.valueOf(HP.code.PLAYER_AWARD_S_VALUE, playerRewardBuilder));
					
					BehaviorLogger.log4Service(player, Source.SYS_OPERATION, Action.BATTLE_REWARD, 
							Params.valueOf("action", action),
							Params.valueOf("battleType", battleType),
							Params.valueOf("mapId", mapCfg.getId()),
							Params.valueOf("award", awardItems.toString()));
					
					return true;
				}
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return false;
	}

	/**
	 * 计算地图掉落
	 * 
	 * @param player
	 * @param mapCfg
	 * @param battleType
	 * @param detailDrop
	 * @return
	 */
	public boolean calcMapDrop(float globalAwardRatio, boolean offlineDrop) {
		if (mapCfg == null) {
			return false;
		}

		List<DropItems.Item> dropItems = null;
		List<DropItems.Item> profDrops = null;
		if (battleType == Battle.battleType.BATTLE_PVE_BOSS_VALUE) {
			if (mapCfg.getBossDropItems() != null) {
				dropItems = mapCfg.getBossDropItems().calcDrop();
			}
			
			// boss击杀的额外奖励
			VipPrivilegeCfg vipPrivilegeCfg = ConfigManager.getInstance().getConfigByKey(VipPrivilegeCfg.class, player.getVipLevel());
			if (vipPrivilegeCfg != null) {
				AwardItems bossExtAward = vipPrivilegeCfg.getBossExtAward();
				if (bossExtAward != null) {
					for (AwardItems.Item item : bossExtAward.getAwardItems()) {
						dropItems.add(new DropItems.Item(item.getType(), item.getId(), item.getCount(), GsConst.RANDOM_MYRIABIT_BASE));
					}
				}
			}
		} else if (battleType == Battle.battleType.BATTLE_PVE_MONSTER_VALUE) {
			// 地图按次掉落属性
			MapAttr mapAttr = player.getPlayerData().getMapEntity().getMapAttr(mapCfg.getId());
//			if (!offlineDrop) {
//				if (mapAttr == null || mapAttr.getFightTimes() == 0) {
//					if (mapCfg.getFirstDrop() != null) {
//						profDrops = mapCfg.getFirstDrop().getDropItems();
//					}
//				} else if (mapAttr == null || mapAttr.getFightTimes() == 1) {
//					if (mapCfg.getSecondDrop() != null) {
//						profDrops = mapCfg.getSecondDrop().getDropItems();
//					}
//				}
//			}
			
			if (profDrops != null) {
				dropItems = new LinkedList<DropItems.Item>();
				dropItems.add(profDrops.get(0));
			}
		} else if (battleType == Battle.battleType.BATTLE_PVE_ELITE_BOSS_VALUE) {
			// 地图按次掉落属性
			MapAttr mapAttr = player.getPlayerData().getMapEntity().getMapAttr(mapCfg.getId());
//			if (mapAttr == null || mapAttr.getFightTimes() == 0) {
//				if (mapCfg.getFirstDrop() != null && mapCfg.getFirstDrop().getDropItems().size() > 0) {
//					dropItems = mapCfg.getFirstDrop().getDropItems();
//				}
//			}
		}

		// 特殊掉落没有成功即采用普通掉落
//		if (dropItems == null && mapCfg.getNormalDrop() != null) {
//			// 神器属性对装备掉落率的影响
//			int equipDrop = player.getPlayerData().getMainRole().getAttribute().getValue(Const.attr.BUFF_EQUIP_DROP);
//			float specialAwardRatio = 1.0f + 0.0001f * equipDrop;
//			if (battleType == Battle.battleType.BATTLE_PVE_MONSTER_VALUE) {
//				dropItems = mapCfg.getNormalDrop().calcDropByBlankTable(globalAwardRatio, specialAwardRatio);
//			} else if (battleType == Battle.battleType.BATTLE_PVE_BOSS_VALUE) {
//				dropItems = mapCfg.getNormalDrop().calcDropByRatio(globalAwardRatio, specialAwardRatio);
//			} else {
//				dropItems = mapCfg.getNormalDrop().calcDropByRatio(globalAwardRatio, specialAwardRatio);
//			}
//		}

		// 掉落错误
		if (dropItems == null || dropItems.size() <= 0) {
			return false;
		}

		for (DropItems.Item item : dropItems) {
			ItemInfo itemInfo = null;
			// 是否从奖励组获得
			if (item.id <= 0) {
				itemInfo = AwardUtil.randomDrop(item.getType());
				itemInfo.setQuantity(itemInfo.getQuantity() * item.getCount());
			} else {
				itemInfo = new ItemInfo(item.getType(), item.getId(), item.getCount());
			}

			int itemType = GameUtil.convertToStandardItemType(itemInfo.getType()) / GsConst.ITEM_TYPE_BASE;
			// 装备掉落
			if (itemType == Const.itemType.EQUIP_VALUE) {
				EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, itemInfo.getItemId());
				if (equipCfg == null) {
					continue;
				}

				Integer equipSellRatio = ActivityUtil.getEquipSellActivity(player.getPlayerData().getPlayerEntity()
						.getCreateTime());
				// 自动售出
				if (EquipUtil.checkAutoSellEquip(player, equipCfg)) {
					// 售出金币
					if(equipSellRatio != null) {
						awardItems.addCoin(equipCfg.getSellPrice() *(int) itemInfo.getQuantity()  * equipSellRatio);
					}else{
						awardItems.addCoin(equipCfg.getSellPrice() *(int) itemInfo.getQuantity());
					}

					// 添加到掉落信息
					addEquipToDropAward(dropAward, equipCfg, (int)itemInfo.getQuantity(), true, offlineDrop, equipSellRatio);
				} else {
					// 可入包裹数量校验
					int dropCount = Math.min((int)itemInfo.getQuantity(), maxEquipCount);
					maxEquipCount -= dropCount;
							
					// 包裹空间不足
					if (dropCount > 0) {
						itemInfo.setQuantity(dropCount);
						// 添加到奖励信息
						awardItems.addItem(itemInfo);
						// 添加到掉落信息
						addEquipToDropAward(dropAward, equipCfg, dropCount, false, offlineDrop, equipSellRatio);
					}

					// 没有掉落的即自动售出
					int sellCount = (int)itemInfo.getQuantity() - dropCount;
					if (sellCount > 0) {
						// 售出金币
						if(equipSellRatio != null) {
							awardItems.addCoin(equipCfg.getSellPrice() * sellCount * equipSellRatio);
						}else{
							awardItems.addCoin(equipCfg.getSellPrice() * sellCount);
						}

						// 添加到掉落信息
						addEquipToDropAward(dropAward, equipCfg, sellCount, true, offlineDrop, equipSellRatio);
					}
				}
			}

			// 元素掉落
			if (itemType == Const.itemType.ELEMENT_VALUE) {
				ElementCfg elementCfg = ConfigManager.getInstance().getConfigByKey(ElementCfg.class,
						itemInfo.getItemId());
				if (elementCfg == null) {
					continue;
				}
				if (EquipUtil.checkAutoSellElement(player, elementCfg)) {
					AwardItems decoAwards = new AwardItems();
					
					decoAwards.appendAward(ElementDecomposeCfg.getDecomposeAwards(elementCfg.getQuality()));
					
					awardItems.appendAward(decoAwards);
					// 添加到掉落信息
					addElementToDropAward(dropAward, elementCfg, elementCfg.getQuality(), true, offlineDrop, decoAwards);
				} else {

					// 可入包裹数量校验
					int dropCount = Math.min((int)itemInfo.getQuantity(), this.maxElementCount);
					maxElementCount -= dropCount;
							
					// 包裹空间不足
					if (dropCount > 0) {
						itemInfo.setQuantity(dropCount);
						// 添加到奖励信息
						awardItems.addItem(itemInfo);
						// 添加到掉落信息
						addElementToDropAward(dropAward, elementCfg, dropCount, false, offlineDrop,null);
						
					}

					// 没有掉落的即自动分解
					int decoCount = (int)itemInfo.getQuantity() - dropCount;
					if (decoCount > 0) {
						AwardItems decoAwards = new AwardItems();
						for(int i=0; i<decoCount; i++) {
							decoAwards.appendAward(ElementDecomposeCfg.getDecomposeAwards(elementCfg.getQuality()));
						}
						awardItems.appendAward(decoAwards);
						// 添加到掉落信息
						addElementToDropAward(dropAward, elementCfg, decoCount, true, offlineDrop,decoAwards);
					}
				}
			}

			// 道具掉落
			if (itemType == Const.itemType.TOOL_VALUE) {
				// 获取宝箱道具配置(暂时不支持随机池)
				ItemCfg itemCfg = ConfigManager.getInstance().getConfigByKey(ItemCfg.class, itemInfo.getItemId());
				if (itemCfg == null) {
					continue;
				}

				if (itemCfg.getType() != Const.toolType.TREASURE_VALUE) {
					// 添加到奖励信息
					awardItems.addItem(itemInfo);
					// 添加到掉落信息
					addItemToDropAward(dropAward, itemInfo);
					continue;
				}
				
				// 检测配对物品
				ItemEntity needItemEntity = null;
				long canGotCount = itemInfo.getQuantity();
				if (itemCfg.getNeedItem() > 0) {
					if (itemIdCount.containsKey(itemCfg.getNeedItem()) && itemIdCount.get(itemCfg.getNeedItem()) > 0) {
						long itemCount = itemIdCount.get(itemCfg.getNeedItem());
						canGotCount = Math.min(itemInfo.getQuantity(), itemCount);
						itemCount -= canGotCount;
						itemIdCount.put(itemCfg.getNeedItem(), itemCount);

						needItemEntity = player.getPlayerData().getItemByItemId(itemCfg.getNeedItem());
					} else {
						canGotCount = 0;
					}
				}

				// 配对物品不足, 直接丢弃部分
				if (itemInfo.getQuantity() > canGotCount) {
					discardDropTreasure(dropAward, itemInfo.getItemId(), (int)(itemInfo.getQuantity() - canGotCount));
				}

				// 填写消耗配对道具信息
				if (needItemEntity != null) {
					consumeItems.addChangeInfo(changeType.CHANGE_TOOLS, needItemEntity.getId(),
							needItemEntity.getItemId(), (int)canGotCount);
				}

				// 分步开宝箱
				for (int i = 0; i < canGotCount; i++) {
					// 开宝箱
					DropAward.Treasure.Builder treasureBuilder = DropAward.Treasure.newBuilder();
					treasureBuilder.setItemId(itemInfo.getItemId());
					treasureBuilder.setCount(1);
					treasureBuilder.setState(Battle.treasureState.TREASURE_OPEN_VALUE);

					// 开宝箱
					ItemInfo treasureItemInfo = WeightUtil.random(itemCfg.getContainDataWeightList());
					if (treasureItemInfo != null) {
						if (treasureItemInfo.getItemId() == 0) {
							ItemInfo treasureDropItemInfo = AwardUtil.randomDrop(treasureItemInfo.getType());
							if (treasureDropItemInfo != null) {
								// 添加到奖励信息
								awardItems.addItem(treasureDropItemInfo);
								// 添加到掉落信息
								addTreasureDropItem(treasureBuilder, treasureDropItemInfo);
							}
						} else {
							// 添加到奖励信息
							awardItems.addItem(treasureItemInfo);
							// 添加到掉落信息
							addTreasureDropItem(treasureBuilder, treasureItemInfo);
						}
					}
					// 幸运宝箱额外掉落
					ActivityUtil.triggerLuckyTreasure(this, treasureBuilder, itemCfg.getId());
					dropAward.addTreasure(treasureBuilder);
				}
			}
		}
		return true;
	}

	private void addElementToDropAward(Builder dropAward2, ElementCfg elementCfg, int count, boolean isAutoSell,
			boolean offlineDrop, AwardItems decoAwards) {
		if (!offlineDrop) {
			boolean equipExist = false;
			for (int i = 0; i < dropAward.getDetailElementCount(); i++) {
				DropAward.DetailElement.Builder elementBuilder = dropAward.getDetailElementBuilder(i);
				if (elementBuilder.getItemId() == elementCfg.getId()) {
					// 直接获得元素
					if (decoAwards == null && !isAutoSell) {
						equipExist = true;
						elementBuilder.setCount(elementBuilder.getCount() + count);
						createBaseElementAttr(elementCfg, elementBuilder);
					}

					// 自动分解
					if (decoAwards != null && isAutoSell) {
						equipExist = true;
						elementBuilder.setCount(elementBuilder.getCount() + count);
						AwardItems orginAwards = AwardItems.valueOf(elementBuilder.getDecoInfo());
						if(orginAwards != null) {
							decoAwards.appendAward(orginAwards);
						}
						elementBuilder.setDecoInfo(decoAwards.toString());
					}
				}
			}

			// 不存在, 直接添加实例
			if (!equipExist) {
				DropAward.DetailElement.Builder elementBuilder = DropAward.DetailElement.newBuilder();
				elementBuilder.setItemId(elementCfg.getId());
				elementBuilder.setCount(count);
				if (decoAwards != null) {
					elementBuilder.setDecoInfo(decoAwards.toString());
				}
				createBaseElementAttr(elementCfg, elementBuilder);
				dropAward.addDetailElement(elementBuilder);
			}

		} else {
			boolean qualityExist = false;
			for (int i = 0; i < dropAward.getElementCount(); i++) {
				DropAward.Element.Builder elemeBuilder = dropAward.getElementBuilder(i);
				if (elemeBuilder.getQuality() == elementCfg.getQuality()) {
					// 直接获得元素
					if (decoAwards == null && !isAutoSell) {
						qualityExist = true;
						elemeBuilder.setCount(elemeBuilder.getCount() + count);
					}

					// 自动售出
					if (decoAwards != null && isAutoSell) {
						qualityExist = true;
						elemeBuilder.setCount(elemeBuilder.getCount() + count);
						AwardItems orginAwards = AwardItems.valueOf(elemeBuilder.getDecoInfo());
						if(orginAwards != null) {
							decoAwards.appendAward(orginAwards);
						}
						elemeBuilder.setDecoInfo(decoAwards.toString());
					}
				}
			}

			// 不存在, 直接添加实例
			if (!qualityExist) {
				DropAward.Element.Builder elementBuilder = DropAward.Element.newBuilder();
				elementBuilder.setQuality(elementCfg.getQuality());
				elementBuilder.setCount(count);
				if (decoAwards != null) {
					elementBuilder.setDecoInfo(decoAwards.toString());
				}

				dropAward.addElement(elementBuilder);
			}
		}
	}

	/**
	 * 提前生成元素的属性, 但并未生成元素;(策划蛋疼的设定和元素掉落蛋疼的设计)
	 * 
	 * @param elementCfg
	 * @param elemeBuilder
	 */
	private void createBaseElementAttr(ElementCfg elementCfg, DropAward.DetailElement.Builder elemeBuilder) {
		// 生成默认属性
		List<Integer> defaultBasicIds = elementCfg.getDefaultBasicIds();
		if (defaultBasicIds != null && defaultBasicIds.size() > 0) {
			for (Integer attrCfgId : defaultBasicIds) {
				ElementAttrCfg elementAttrCfg = ConfigManager.getInstance().getConfigByKey(ElementAttrCfg.class,
						attrCfgId);
				if (elementAttrCfg != null) {
					AttrInfo.Builder attrBuilder = AttrInfo.newBuilder();
					attrBuilder.addAttribute(Attr.newBuilder().setAttrId(elementAttrCfg.getAttrId())
							.setAttrValue(elementAttrCfg.getAttrValue()));
					for (int i = 0; i < elementCfg.getIsInitAttr(); i++) {
						ElementUtil.advanceElementAttr(elementCfg, attrBuilder);
					}
					elemeBuilder.setBasicAttrs(attrBuilder);
				}
			}
		}
		for (int i = 0; i < elementCfg.getIsInitAttr(); i++) {
			AttrInfo.Builder attrBuilder = AttrInfo.newBuilder();
			ElementUtil.advanceElementAttr(elementCfg, attrBuilder);
			elemeBuilder.setBasicAttrs(attrBuilder);
		}
	}

	/**
	 * 把装备信息添加到掉落奖励中
	 * 
	 * @param dropAward
	 * @param equipCfg
	 * @param equipSellRatio 
	 */
	protected void addEquipToDropAward(DropAward.Builder dropAward, EquipCfg equipCfg, int count, boolean isAutoSell,
			boolean offlineDrop, Integer equipSellRatio) {
		int equipSellRatioValue = 1;
		if(equipSellRatio != null) {
			equipSellRatioValue = equipSellRatio;
		}
		if (!offlineDrop) {
			boolean equipExist = false;
			for (int i = 0; i < dropAward.getDetailEquipCount(); i++) {
				DropAward.DetailEquip.Builder equipBuilder = dropAward.getDetailEquipBuilder(i);
				if (equipBuilder.getItemId() == equipCfg.getId()) {
					// 直接获得装备
					if (equipBuilder.getSellCoin() <= 0 && !isAutoSell) {
						equipExist = true;
						equipBuilder.setCount(equipBuilder.getCount() + count);
					}

					// 自动售出
					if (equipBuilder.getSellCoin() > 0 && isAutoSell) {
						equipExist = true;
						equipBuilder.setCount(equipBuilder.getCount() + count);
						equipBuilder.setSellCoin(equipBuilder.getSellCoin() + equipSellRatioValue * count
								* equipCfg.getSellPrice());
					}
				}
			}

			// 不存在, 直接添加实例
			if (!equipExist) {
				DropAward.DetailEquip.Builder equipBuilder = DropAward.DetailEquip.newBuilder();
				equipBuilder.setItemId(equipCfg.getId());
				equipBuilder.setCount(count);
				if (isAutoSell) {
					equipBuilder.setSellCoin(equipSellRatioValue * count * equipCfg.getSellPrice());
				}

				dropAward.addDetailEquip(equipBuilder);
			}

		} else {
			boolean qualityExist = false;
			for (int i = 0; i < dropAward.getEquipCount(); i++) {
				DropAward.Equip.Builder equipBuilder = dropAward.getEquipBuilder(i);
				if (equipBuilder.getQuality() == equipCfg.getQuality()) {
					// 直接获得装备
					if (equipBuilder.getSellCoin() <= 0 && !isAutoSell) {
						qualityExist = true;
						equipBuilder.setCount(equipBuilder.getCount() + count);
					}

					// 自动售出
					if (equipBuilder.getSellCoin() > 0 && isAutoSell) {
						qualityExist = true;
						equipBuilder.setCount(equipBuilder.getCount() + count);
						equipBuilder.setSellCoin(equipBuilder.getSellCoin() + equipSellRatioValue * count
								* equipCfg.getSellPrice());
					}
				}
			}

			// 不存在, 直接添加实例
			if (!qualityExist) {
				DropAward.Equip.Builder equipBuilder = DropAward.Equip.newBuilder();
				equipBuilder.setQuality(equipCfg.getQuality());
				equipBuilder.setCount(count);
				if (isAutoSell) {
					equipBuilder.setSellCoin(equipSellRatioValue * count * equipCfg.getSellPrice());
				}

				dropAward.addEquip(equipBuilder);
			}
		}
	}

	/**
	 * 把物品信息添加到掉落奖励中
	 * 
	 * @param dropAward
	 * @param equipCfg
	 * @param count
	 */
	protected void addItemToDropAward(DropAward.Builder dropAward, ItemInfo itemInfo) {
		boolean itemExist = false;
		for (int i = 0; i < dropAward.getItemCount(); i++) {
			DropAward.Item.Builder itemBuilder = dropAward.getItemBuilder(i);
			if (itemBuilder.getItemId() == itemInfo.getItemId()) {
				// 直接获得
				itemExist = true;
				itemBuilder.setItemCount(itemBuilder.getItemCount() + (int)itemInfo.getQuantity());
			}
		}

		// 不存在, 直接添加实例
		if (!itemExist) {
			DropAward.Item.Builder itemBuilder = DropAward.Item.newBuilder();
			itemBuilder.setItemType(itemInfo.getType());
			itemBuilder.setItemId(itemInfo.getItemId());
			itemBuilder.setItemCount((int)itemInfo.getQuantity());
			dropAward.addItem(itemBuilder);
		}
	}
	
	/**
	 * 把物品信息添加到掉落奖励中
	 * 
	 * @param dropAward
	 * @param equipCfg
	 * @param count
	 */
	protected void addTreasureDropItem(DropAward.Treasure.Builder treasureBuilder, ItemInfo itemInfo) {
		boolean itemExist = false;
		for (int i = 0; i < treasureBuilder.getItemCount(); i++) {
			DropAward.Item.Builder itemBuilder = treasureBuilder.getItemBuilder(i);
			if (itemBuilder.getItemId() == itemInfo.getItemId()) {
				itemExist = true;
				itemBuilder.setItemCount(treasureBuilder.getItemCount() + (int)itemInfo.getQuantity());
			}
		}

		if (!itemExist) {
			DropAward.Item.Builder itemBuilder = DropAward.Item.newBuilder();
			itemBuilder.setItemType(itemInfo.getType());
			itemBuilder.setItemId(itemInfo.getItemId());
			itemBuilder.setItemCount((int)itemInfo.getQuantity());
			treasureBuilder.addItem(itemBuilder);
		}
	}

	/**
	 * 宝箱无钥匙直接丢弃
	 * 
	 * @param dropAward
	 * @param itemInfo
	 * @param count
	 */
	protected void discardDropTreasure(DropAward.Builder dropAward, int treasureId, int count) {
		boolean treasureExist = false;
		for (int i = 0; i < dropAward.getTreasureCount(); i++) {
			DropAward.Treasure.Builder treasureBuilder = dropAward.getTreasureBuilder(i);
			if (treasureBuilder.getState() == Battle.treasureState.TREASURE_DISCARD_VALUE
					&& treasureBuilder.getItemId() == treasureId) {
				treasureExist = true;
				treasureBuilder.setCount(treasureBuilder.getCount() + count);
			}
		}
	
		if (!treasureExist) {
			DropAward.Treasure.Builder treasureBuilder = DropAward.Treasure.newBuilder();
			treasureBuilder.setItemId(treasureId);
			treasureBuilder.setCount(1);
			treasureBuilder.setState(Battle.treasureState.TREASURE_DISCARD_VALUE);
			dropAward.addTreasure(treasureBuilder);
		}
	}
}
