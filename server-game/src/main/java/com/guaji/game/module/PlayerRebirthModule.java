package com.guaji.game.module;

import java.util.List;

import org.guaji.annotation.ProtocolHandlerAnno;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.RebirthStageCfg;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Talent.HPRebirthRet;

/**
 * 角色转生
 */
public class PlayerRebirthModule extends PlayerModule{
	
	public PlayerRebirthModule(Player player) {
		super(player);
	}
	
	/**
	 * 转生协议处理
	 * 
	 * @param protocol
	 * @return
	 */
	@ProtocolHandlerAnno(code = HP.code.ROLE_REBIRTH_C_VALUE)
	public boolean onChallengeResultMessage(Protocol protocol) {
		
		// 转生下一阶段
		int rebirthStage = player.getRebirthStage() + 1;
		// 取转生配置
		RebirthStageCfg config = ConfigManager.getInstance().getConfigByKey(RebirthStageCfg.class, rebirthStage);
		if (config == null) {
			player.sendError(HP.code.ROLE_REBIRTH_C_VALUE, Status.error.NOT_ABLE_TO_REBIRTH_BOSS_VALUE);
			return true;
		}
		
		if(!this.isChallengeBoss(config)) {
			player.sendError(HP.code.ROLE_REBIRTH_C_VALUE, Status.error.NOT_ABLE_TO_REBIRTH_BOSS_VALUE);
			return true;
		}
		
		// 刷新属性, 新增技能栏
		refreshAttrAfterRebirth(rebirthStage);
		// 进入新地图
		openNewMap(config.getRebirthMapId());
		
		HPRebirthRet.Builder builder = HPRebirthRet.newBuilder();
		builder.setIsSucccess(true);
		player.sendProtocol(Protocol.valueOf(HP.code.ROLE_REBIRTH_S_VALUE, builder));

		return true;
	}
		
	/**
	 * 开启新地图
	 * 
	 * @param mapId
	 */
	private void openNewMap(int mapId) {
//		StateEntity stateEntity = player.getPlayerData().getStateEntity();
//		NewMapCfg mapCfg = player.getPlayerData().getBattleMap(mapId);
//		if (mapCfg != null) {
//			stateEntity.setPassMapId(mapCfg.getId());
//			stateEntity.setCurBattleMap(mapCfg.getNextMapId());
//		}
//		stateEntity.notifyUpdate(true);
//		
//		player.getPlayerData().syncStateInfo();
//		
//		BehaviorLogger.log4Service(player, Source.PLAYER_ATTR_CHANGE, Action.REBIRTH_TALENT, Params.valueOf("action", Action.REBIRTH_TALENT), Params.valueOf("level", player.getLevel()));
	}
	
	/**
	 * 刷新角色属性
	 */
	private void refreshAttrAfterRebirth(int rebirthStage) {
		RoleEntity roleEntity = player.getPlayerData().getMainRole();
		List<RoleEntity> roleEntities = player.getPlayerData().getRoleEntities();
		for (RoleEntity eacheRoleEntity : roleEntities) {
			eacheRoleEntity.setRebirthStage(rebirthStage);
			eacheRoleEntity.notifyUpdate(true);
		}
		PlayerEntity playerEntity = player.getPlayerData().getPlayerEntity();
		playerEntity.setRebirthStage(rebirthStage);
		roleEntity.setRebirthStage(rebirthStage);
		playerEntity.notifyUpdate(true);
		roleEntity.notifyUpdate(true);
		player.getPlayerData().syncRoleInfo(0);
		PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
		player.increaseExp(rebirthStage, Action.REBIRTH_TALENT);
		// 同步主角信息
		player.getPlayerData().syncRoleInfo(player.getPlayerData().getMainRole().getId());
	}
	
	/**
	 * 验证是否挑战转生Boss关卡
	 * 
	 * @param config
	 * @return
	 */
	private boolean isChallengeBoss(RebirthStageCfg config) {
		
//		int level = player.getLevel();
//		StateEntity stateEntity = player.getPlayerData().getStateEntity();
//		int passMapId = stateEntity.getPassMapId();
//		if (level < config.getLevelLimit() || passMapId < config.getRebirthMapId()) {
			return false;
//		}
//		return true;
	}

}
