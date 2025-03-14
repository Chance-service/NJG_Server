package com.guaji.game.module;

import java.util.ArrayList;
import java.util.List;

import org.guaji.annotation.ProtocolHandlerAnno;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.attribute.Attribute;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.config.WingsCfg;
import com.guaji.game.entity.PlayerWingsEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ConsumeItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.util.WeightUtil;
import com.guaji.game.util.WeightUtil.WeightItem;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.RankType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.SynchroPlayerMsg.SynchroPlayerMessage;
import com.guaji.game.protocol.Wings.HPWingGetLeadRet;
import com.guaji.game.protocol.Wings.HPWingLevelupRet;
import com.guaji.game.protocol.Wings.HPWingLevelupRet.Builder;

/**
 * 翅膀系统;
 */
public class PlayerWingModule extends PlayerModule {
	private PlayerWingsEntity playerWingsEntity;
	public static final int WING_TYPE = 1;
	public static final String NO_ALLIANCE = "no alliance";

	public PlayerWingModule(Player player) {
		super(player);
	}

	@Override
	protected boolean onPlayerLogin() {
		playerWingsEntity = player.getPlayerData().loadPlayerWingsEntity();
		return true;
	}

	@Override
	protected boolean onPlayerAssemble() {
		SynchroPlayerMessage.Builder builder = SynchroPlayerMessage.newBuilder();
		builder.setWingLevel(playerWingsEntity.getLevel());
		builder.setWingLuckyNum(playerWingsEntity.getLuckyNum());
		return player.sendProtocol(Protocol.valueOf(HP.code.SYNCHRO_PLAYER_MESSAGE_S_VALUE, builder));
	}

	/**
	 * 翅膀获取
	 * 
	 * @return
	 */
	@ProtocolHandlerAnno(code = HP.code.WING_GET_LEAD_C_VALUE)
	private boolean onWingGetLead(Protocol protocol) {
		if (player.getLevel() < Const.WingsConstant.WINGS_OPEN_LEVEL_VALUE) {
			player.sendError(HP.code.WING_LEVEL_UP_S_VALUE, Status.error.LEVEL_NOT_LIMIT_VALUE);
			return false;
		}
		if (playerWingsEntity.getLevel() >= 1 || playerWingsEntity.getStarTime() != 0) {
			player.sendError(HP.code.WING_LEVEL_UP_S_VALUE, Status.error.WING_LEAD_HAD_GOT_VALUE);
			return false;
		}
		AwardItems award = AwardItems.valueOf(SysBasicCfg.getInstance().getWingLeadGet());
		award.rewardTakeAffectAndPush(player, Action.WINGS_LEAD_GET,1);
		HPWingGetLeadRet.Builder builder = HPWingGetLeadRet.newBuilder();
		builder.setVersion(1);
		playerWingsEntity.setStarTime(GuaJiTime.getMillisecond());
		player.sendProtocol(Protocol.valueOf(HP.code.WING_GET_LEAD_S_VALUE, builder));
		playerWingsEntity.notifyUpdate(true);
		BehaviorLogger.log4Service(player, Source.PLAYER_ATTR_CHANGE, Action.WINGS_LEAD_GET,
				Params.valueOf("action", Action.WINGS_LEAD_GET), Params.valueOf("addItem", award),
				Params.valueOf("wingLevel", playerWingsEntity.getLevel()));
		return true;
	}

	/**
	 * 翅膀升级
	 * 
	 * @return
	 */
	@ProtocolHandlerAnno(code = HP.code.WING_LEVEL_UP_C_VALUE)
	private boolean onWinglevelUp(Protocol protocol) {
		if (player.getLevel() < Const.WingsConstant.WINGS_OPEN_LEVEL_VALUE) {
			player.sendError(HP.code.WING_LEVEL_UP_S_VALUE, Status.error.LEVEL_NOT_LIMIT_VALUE);
			return false;
		}
		if (playerWingsEntity.getLevel() >= SysBasicCfg.getInstance().getMaxWingLevel()) {
			playerWingsEntity.setLevel(SysBasicCfg.getInstance().getMaxWingLevel());
			playerWingsEntity.notifyUpdate(true);
			player.sendError(HP.code.WING_LEVEL_UP_S_VALUE, Status.error.TALENT_LEVEL_OVER_BOUND_VALUE);
			return false;
		}
		ConsumeItems consumeItems = ConsumeItems.valueOf();
		int prof = player.getProf();
		List<ItemInfo> costItems = WingsCfg.getCostItem(WING_TYPE, playerWingsEntity.getLevel(), prof);
		if (!consumeItems.addConsumeInfo(player.getPlayerData(), costItems)) {
			player.sendError(protocol.getType(), Status.error.ITEM_NOT_ENOUGH);
			return false;
		}
		if (!consumeItems.checkConsume(player, HP.code.WING_LEVEL_UP_C_VALUE)) {
			return false;
		}
		consumeItems.consumeTakeAffect(player, Action.WINGS_LEVEL_UP);
		WingsCfg wingsCfg = WingsCfg.getWingCfgByKey(WING_TYPE, playerWingsEntity.getLevel(), prof);
		HPWingLevelupRet.Builder builder = genWingsLevelupBuilder(wingsCfg);
		return player.sendProtocol(Protocol.valueOf(HP.code.WING_LEVEL_UP_S_VALUE, builder));
	}

	/**
	 * 获取翅膀十星时间排行
	 * 
	 * @return
	 */
	@ProtocolHandlerAnno(code = HP.code.WING_QUALITY_RANK_C_VALUE)
	private boolean onWingRankInfo(Protocol protocol) {
		Msg hawkMsg = Msg.valueOf(GsConst.MsgType.ON_RANK_GET);
		hawkMsg.pushParam(RankType.WING_WHITE_TIME_RANK);
		hawkMsg.pushParam(String.valueOf(player.getId()));
		GuaJiXID targetXId = GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.RANK_MANAGER);
		return GsApp.getInstance().postMsg(targetXId, hawkMsg);
	}

	/**
	 * 进行翅膀升级;
	 * 
	 * @param wingsCfg
	 * @return
	 */
	private Builder genWingsLevelupBuilder(WingsCfg wingsCfg) {
		Attribute oldAttr = player.getPlayerData().getMainRole().getAttribute().clone();
		boolean isAttrChange = false;
		boolean isSuccess = false;
		int oldLevel = playerWingsEntity.getLevel();
		// 假设失败之后加上幸运值, 看结果会不会超过100, 如果大于100, 则判断本次升级成功;
		if (playerWingsEntity.getLuckyNum() + wingsCfg.getLuckyNum() >= Const.WingsConstant.MAX_LUCKY_NUM_VALUE
				|| playerWingsEntity.getLevel() == 0) {
			playerWingsEntity.levelup(player.getLevel());
			playerWingsEntity.resetLuckyNum();
			isAttrChange = true;
			isSuccess = true;
		} else {
			// 随机数
			List<WeightItem<Integer>> weightItems = new ArrayList<WeightItem<Integer>>();
			for (int i = WingsCfg.SUCCESS; i <= WingsCfg.BACKED; i++) {
				WeightItem<Integer> weightItem = new WeightItem<Integer>();
				weightItem.setValue(i);
				weightItem.setWeight(wingsCfg.getRate(i));
				weightItems.add(weightItem);
			}
			Integer random = WeightUtil.random(weightItems);
			// 根据随机数所在区间判成功或失败
			if (random == WingsCfg.SUCCESS) { // 升级成功
				playerWingsEntity.levelup(player.getLevel());
				playerWingsEntity.resetLuckyNum();
				isAttrChange = true;
				isSuccess = true;
			} else if (random == WingsCfg.BACKED) { // 升级失败且降级
				playerWingsEntity.leveldown();
				isAttrChange = true;
			}
		}

		if (!isSuccess && !isAttrChange) {
			playerWingsEntity.addLuckNum(wingsCfg.getLuckyNum());
		}

		playerWingsEntity.notifyUpdate(true);
		if (isAttrChange) {
			// 同步属性
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), player.getPlayerData().getMainRole());
			player.getPlayerData().syncPlayerInfo();
			player.getPlayerData().syncRoleInfo(0);
			Attribute newAttr = player.getPlayerData().getMainRole().getAttribute();
			PlayerUtil.pushAttrChange(player, oldAttr, newAttr);
		}
		BehaviorLogger.log4Service(player, Source.PLAYER_ATTR_CHANGE, Action.WINGS_LEVEL_UP,
				Params.valueOf("action", Action.WINGS_LEVEL_UP), Params.valueOf("isAttrChange", isAttrChange),
				Params.valueOf("isSuccess", isSuccess), Params.valueOf("oldLevel", oldLevel),
				Params.valueOf("newLevel", playerWingsEntity.getLevel()));

		HPWingLevelupRet.Builder builder = HPWingLevelupRet.newBuilder();
		builder.setIsLevelup(isSuccess);
		builder.setLevel(playerWingsEntity.getLevel());
		builder.setLuckyNum(playerWingsEntity.getLuckyNum());
		return builder;
	}

}
