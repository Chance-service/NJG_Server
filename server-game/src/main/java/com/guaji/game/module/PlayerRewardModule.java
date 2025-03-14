package com.guaji.game.module;

import java.util.List;

import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.util.services.CdkService;

import com.guaji.game.protocol.Cdk.HPUseCdk;
import com.guaji.game.protocol.Cdk.HPUseCdkRet;
import com.guaji.game.config.MonthCardCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.util.GameUtil;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Player.OpenGitStatus;
import com.guaji.game.protocol.Reward.HPFetchGift;
import com.guaji.game.protocol.Reward.HPJPActivityStatusRet;
import com.guaji.game.protocol.Reward.RewardItem;
import com.guaji.game.protocol.Status;

/**
 * 激活码模块
 */
public class PlayerRewardModule extends PlayerModule {

	private static int GIFT_OPEN_SERVER = 1;

	/**
	 * 构造
	 * 
	 * @param player
	 */
	public PlayerRewardModule(Player player) {
		super(player);

		listenProto(HP.code.USE_CDK_C);
		listenProto(HP.code.FETCH_GIFT_C);
	}

	/**
	 * 更新
	 * 
	 * @return
	 */
	@Override
	public boolean onTick() {
		return super.onTick();
	}

	/**
	 * 消息响应
	 * 
	 * @param msg
	 * @return
	 */
	@Override
	public boolean onMessage(Msg msg) {
		return super.onMessage(msg);
	}

	/**
	 * 协议响应
	 * 
	 * @param protocol
	 * @return
	 */
	@Override
	public boolean onProtocol(Protocol protocol) {
		if (protocol.checkType(HP.code.USE_CDK_C)) {

			if (SysBasicCfg.getInstance().getGiftExchangeSwitch() != 0) {
				onUseCdk(protocol.parseProtocol(HPUseCdk.getDefaultInstance()));
			}
			return true;
		} else if (protocol.checkType(HP.code.FETCH_GIFT_C)) {
			onFetchGift(protocol.parseProtocol(HPFetchGift.getDefaultInstance()));
			return true;
		}
		return super.onProtocol(protocol);
	}

	/**
	 * 玩家上线处理
	 * 
	 * @return
	 */
	protected boolean onPlayerLogin() {
		return true;
	}

	/**
	 * 玩家组装完成, 主要用来后期数据同步
	 * 
	 * @return
	 */
	protected boolean onPlayerAssemble() {
		// TODO 废弃活动
		// getJPActivityStatus();
		return true;
	}

	/**
	 * 玩家下线处理
	 * 
	 * @return
	 */
	protected boolean onPlayerLogout() {
		return true;
	}

	private boolean rewardSpecialCdks(String cdk) {
		String cdkAward = SysBasicCfg.getInstance().getSpecialCdkInfo(cdk);
		if (cdkAward != null && cdkAward.length() > 0) {
			HPUseCdkRet.Builder builder = HPUseCdkRet.newBuilder();
			builder.setCdkey(cdk);

			// 查询是否使用过
			int status = CdkService.CDK_STATUS_OK;
			StateEntity stateEntity = player.getPlayerData().getStateEntity();
			String cdkeyTypes = stateEntity.getCdkeyType();
			if (cdkeyTypes != null && cdkeyTypes.length() > 0 && GameUtil.isCdkTypeLimitMultiUse(cdk)) {
				String[] usedTypes = cdkeyTypes.split(",");
				for (int i = 0; i < usedTypes.length; i++) {
					// 统一码没法和8位唯一码同时使用
					if (cdk.equals(usedTypes[i])) {
						status = CdkService.CDK_STATUS_TYPE_MULTI;
						break;
					}
//					} else if (usedTypes[i].length() == CdkService.CDK_TOTAL_UNICODE08_LENGTH) {
//						status = CdkService.CDK_STATUS_USEDUNIQUE;
//						break;
//					}

				}

			}

			// 发放奖励
			if (status == CdkService.CDK_STATUS_OK) {
				AwardItems awardItems = AwardItems.valueOf(cdkAward);
				if (awardItems != null) {
					awardItems.rewardTakeAffectAndPush(player, Action.USE_CDK, 2);
				}

				// 设置用户使用cdk类型
				if (cdkeyTypes == null || cdkeyTypes.length() <= 0) {
					stateEntity.setCdkeyType(cdk);
				} else {
					stateEntity.setCdkeyType(cdkeyTypes + "," + cdk);
				}
				stateEntity.notifyUpdate(true);
			}

			// 回复协议
			builder.setStatus(status);
			sendProtocol(Protocol.valueOf(HP.code.USE_CDK_S, builder));

			// 日志记录
			BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.USE_CDK, Params.valueOf("cdk", cdk),
					Params.valueOf("status", status), Params.valueOf("reward", cdkAward));

			return true;
		}
		return false;
	}

	/**
	 * 使用cdk
	 * 
	 * @param protocol
	 * @return
	 */
	protected boolean onUseCdk(HPUseCdk protocol) {
		// 处理cdk字符串
		int status = CdkService.CDK_STATUS_OK;
		String cdk = protocol.getCdkey();
		cdk = cdk.trim().toLowerCase();
		// 统一码判断
		if (rewardSpecialCdks(cdk)) {
			return true;
		} else {

			// 唯一码操作
			StateEntity stateEntity = player.getPlayerData().getStateEntity();
			String cdkeyTypes = stateEntity.getCdkeyType();
			HPUseCdkRet.Builder builder = HPUseCdkRet.newBuilder();
			builder.setCdkey(cdk);
			boolean isCanUse = false;
			if (cdkeyTypes == null || cdkeyTypes.length() == 0) {
				isCanUse = true;
			} else {
				boolean isUseSpecial = false;
				boolean isUseTypeMulit = false;
				String[] usedTypes = cdkeyTypes.split(",");
				if (usedTypes.length >= 3) {
					status = CdkService.CDK_STATUS_TYPE_MULTI;
					isCanUse = false;
				} else {
					int unicodeCount = 0;
					for (int i = 0; i < usedTypes.length; i++) {

						if (usedTypes[i] == cdk) {
							isUseTypeMulit = true;
							break;
						}
						if (cdk.length() != CdkService.CDK_TOTAL_UNICODE16_LENGTH) {
							if (usedTypes[i].length() == cdk.length()) {
								isUseTypeMulit = true;
								break;
							} else {
								if (SysBasicCfg.getInstance().getSpecialCdkInfo(usedTypes[i]) != null
										&& !SysBasicCfg.getInstance().getSpecialCdkInfo(usedTypes[i]).equals("")) {
									isUseSpecial = true;
								}
							}
						} else {
							if (usedTypes[i].length() == CdkService.CDK_TOTAL_UNICODE16_LENGTH) {
								unicodeCount++;
							}
						}
					}

					if (isUseTypeMulit == true || unicodeCount >= 2) {
						// 使用过同种类型码
						status = CdkService.CDK_STATUS_TYPE_MULTI;
						isCanUse = false;
					} else {
						if (isUseSpecial) {
							if (cdk.length() == CdkService.CDK_TOTAL_UNICODE16_LENGTH)
								isCanUse = true;
							else {
								isCanUse = false;
								// 使用统一码
								status = CdkService.CDK_STATUS_USEUNITE;
							}
						} else {
							isCanUse = true;
						}
					}

				}

			}
			if (isCanUse) {
				// 使用cdk
				StringBuffer cdkString = new StringBuffer("");
				status = CdkService.getInstance().useCdk(player.getPuid(), player.getId(), player.getName(),
						String.valueOf(player.getEntity().getServerId()), cdk, cdkString);

				// 发放奖励
				if (status == CdkService.CDK_STATUS_OK && !cdkString.toString().equals("")) {

					// 设置用户使用cdk类型
					if (cdkeyTypes == null || cdkeyTypes.length() <= 0) {
						stateEntity.setCdkeyType(cdk);
					} else {
						stateEntity.setCdkeyType(cdkeyTypes + "," + cdk);
					}
					stateEntity.notifyUpdate(true);
					AwardItems awardItems = AwardItems.valueOf(cdkString.toString());
					if (awardItems != null) {
						awardItems.rewardTakeAffectAndPush(player, Action.USE_CDK, 2);
					}

					// 日志记录
					BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.USE_CDK,
							Params.valueOf("cdk", cdk), Params.valueOf("status", status),
							Params.valueOf("reward", cdkString.toString()));
				}
			}
			// 回复协议
			builder.setStatus(status);
			sendProtocol(Protocol.valueOf(HP.code.USE_CDK_S, builder));
			return true;
		}

	}

	/**
	 * 开服礼包
	 * 
	 * @param protocol
	 * @return
	 */
	protected boolean onFetchGift(HPFetchGift protocol) {
		if (protocol.getGiftId() != GIFT_OPEN_SERVER) {
			player.sendError(HP.code.FETCH_GIFT_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return false;
		}

		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		int status = stateEntity.getGiftStatus();

		if (status != 0) {
			player.sendError(HP.code.FETCH_GIFT_C_VALUE, Status.error.ACTIVITY_AWARDS_HAS_GOT);
			return false;
		}

		List<ItemInfo> itemInfos = SysBasicCfg.getInstance().getGiftAward();

		if (itemInfos == null || itemInfos.size() == 0) {
			player.sendError(HP.code.FETCH_GIFT_C_VALUE, Status.error.CONFIG_ERROR);
			return false;
		}

		stateEntity.setGiftStatus(1);
		AwardItems items = new AwardItems();
		OpenGitStatus.Builder openGitStatusBuilder = OpenGitStatus.newBuilder();
		for (ItemInfo item : itemInfos) {
			items.addItem(item);
			RewardItem.Builder rewardItemBuilder = RewardItem.newBuilder();
			rewardItemBuilder.setItemId(item.getItemId());
			rewardItemBuilder.setItemType(item.getType());
			rewardItemBuilder.setItemCount(item.getQuantity());
			openGitStatusBuilder.addAward(rewardItemBuilder);
		}
		openGitStatusBuilder.setStatus(stateEntity.getGiftStatus());

		player.getPlayerData().syncStateInfo();
		player.sendProtocol(Protocol.valueOf(HP.code.FETCH_GIFT_S_VALUE, openGitStatusBuilder));
		items.rewardTakeAffectAndPush(player, Action.FETCH_GIFT, 2);
		stateEntity.notifyUpdate();
		return true;
	}

	/**
	 * 日本新手送月卡活动 TODO 废弃活动
	 */
	@SuppressWarnings("unused")
	private void getJPActivityStatus() {

		int activityStatus = SysBasicCfg.getInstance().getJpNewbieRewardMonthCard();

		// 活动关闭
		if (activityStatus == 0) {
			HPJPActivityStatusRet.Builder builder = HPJPActivityStatusRet.newBuilder();
			builder.setJPActivityStatus(0);
			player.sendProtocol(Protocol.valueOf(HP.code.JP_ACTIVITY_STATUS_SYNC, builder));
			return;
		}

		// 已领取
		StateEntity status = player.getPlayerData().getStateEntity();
		if (status.getNewbieRewardMonthCard() == 1) {
			HPJPActivityStatusRet.Builder builder = HPJPActivityStatusRet.newBuilder();
			builder.setJPActivityStatus(0);
			player.sendProtocol(Protocol.valueOf(HP.code.JP_ACTIVITY_STATUS_SYNC, builder));
			return;
		}

		// 发送月卡
		ActivityUtil.triggerMonthCard(player.getPlayerData(), 30,
				MonthCardCfg.getMonthCardCfgByGoodsId(30).getCostMoney());
		//player.increaseRechargeGold(300,300, 0, Action.GAME_SYS_REWARD);

		// 修改领取状态
		status.setNewbieRewardMonthCard(1);
		status.notifyUpdate(true);

		// 发送返回协议
		HPJPActivityStatusRet.Builder builder = HPJPActivityStatusRet.newBuilder();
		builder.setJPActivityStatus(1);
		player.sendProtocol(Protocol.valueOf(HP.code.JP_ACTIVITY_STATUS_SYNC, builder));

	}

}
