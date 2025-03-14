package com.guaji.game.module;

import java.util.List;
import java.util.Map;

import org.guaji.annotation.ProtocolHandlerAnno;
import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ArchiveCfg;
import com.guaji.game.config.FetterCfg;
import com.guaji.game.entity.ArchiveEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.RoleOpr.ArchiveItem;
import com.guaji.game.protocol.RoleOpr.HPArchiveInfoRes;
import com.guaji.game.protocol.RoleOpr.HPFetchOtherArchiveInfoReq;
import com.guaji.game.protocol.RoleOpr.HPOpenFetterReq;
import com.guaji.game.protocol.RoleOpr.HPOpenFetterRes;
import com.guaji.game.protocol.Status;

/**
 * 图鉴模块
 */
public class PlayerArchiveModule extends PlayerModule {

	public PlayerArchiveModule(Player player) {
		super(player);
		
	}

	/**
	 * 玩家登陆处理(数据同步)
	 */
	@Override
	protected boolean onPlayerLogin() {
		super.onPlayerLogin();
		player.getPlayerData().loadArchiveEntity();
		return true;
	}
	
	/*
	 * 查询佣兵收集信息
	 */
	@ProtocolHandlerAnno(code=HP.code.FETCH_ARCHIVE_INFO_C_VALUE)
	private void onFetchArchiveInfo(Protocol protocol) {
		//获取玩家佣兵信息
		List<RoleEntity> roleEntities = player.getPlayerData().getMercenary();
		HPArchiveInfoRes.Builder resp = HPArchiveInfoRes.newBuilder();
		resp.setPlayerId(player.getId());
		//佣兵收集信息
		if(roleEntities != null && roleEntities.size() > 0){
			for(RoleEntity role : roleEntities){
				ArchiveItem.Builder b = ArchiveItem.newBuilder();
				b.setRoleId(role.getItemId());
				b.setActivated(role.getRoleState() == Const.RoleActiviteState.IS_ACTIVITE_VALUE ? true : false);
				b.setSoulCount(role.getSoulCount());
				resp.addItems(b);
			}	
		}
		//开启的羁绊
		ArchiveEntity archiveEntity = player.getPlayerData().getArchiveEntity();
		if(archiveEntity != null && archiveEntity.getOpenFetters() != null){
			for(int fetterId : archiveEntity.getOpenFetters()){
				FetterCfg fetterCfg = ConfigManager.getInstance().getConfigByKey(FetterCfg.class, fetterId);
				if (fetterCfg != null) {
					resp.addOpenFetters(fetterId);
					resp.addStar(fetterCfg.getGroupMinStar(player.getPlayerData()));
				}
		
			}
		}
		player.sendProtocol(Protocol.valueOf(HP.code.FETCH_ARCHIVE_INFO_S_VALUE, resp));
	}
	
	/*
	 * 查询其它玩家佣兵收集信息
	 */
	@ProtocolHandlerAnno(code=HP.code.FETCH_OTHER_ARCHIVE_INFO_C_VALUE)
	private void onFetchOtherArchiveInfo(Protocol protocol) {
		HPFetchOtherArchiveInfoReq req = protocol.parseProtocol(HPFetchOtherArchiveInfoReq.getDefaultInstance());
		//玩家ID
		int targetId = req.getPlayerId();
		//查询玩家
		Player target = PlayerUtil.queryPlayer(targetId);
		//获取佣兵数据
		List<RoleEntity> roleEntities = null;
		if(target != null){
			//缓存中有玩家数据
			roleEntities = target.getPlayerData().getMercenary();
		}else{
			//缓存中没有玩家数据，直接从DB加载
			roleEntities = DBManager.getInstance().query("from RoleEntity where playerId = ? and type = ? and invalid = 0 order by id asc ", targetId, GsConst.RoleType.MERCENARY);
			
		}
		HPArchiveInfoRes.Builder resp = HPArchiveInfoRes.newBuilder();
		resp.setPlayerId(targetId);
		if(roleEntities != null && roleEntities.size() > 0){
			for(RoleEntity role : roleEntities){
				ArchiveItem.Builder b = ArchiveItem.newBuilder();
				b.setRoleId(role.getItemId());
				b.setActivated(role.getRoleState() == Const.RoleActiviteState.IS_ACTIVITE_VALUE ? true : false);
				b.setSoulCount(role.getSoulCount());
				resp.addItems(b);
			}	
		}
		//返回数据
		player.sendProtocol(Protocol.valueOf(HP.code.FETCH_OTHER_ARCHIVE_INFO_S_VALUE, resp));
	}
	
	/*
	 * 开启羁绊
	 */
	@ProtocolHandlerAnno(code=HP.code.OPEN_FETTER_C_VALUE)
	private void onFetchOpenFetter(Protocol protocol) {
		HPOpenFetterReq req = protocol.parseProtocol(HPOpenFetterReq.getDefaultInstance());
		//羁绊ID
		int fetterId = req.getFetterId();
		//图鉴Entity
		ArchiveEntity entity = player.getPlayerData().getArchiveEntity();
		//羁绊已经开启
		if(entity.getOpenFetters().contains(fetterId)){
			sendError(protocol.getType(), Status.error.FETTER_IS_ACTIVED_VALUE);
			return;
		}
		//读取配置
		FetterCfg fetterCfg = ConfigManager.getInstance().getConfigByKey(FetterCfg.class, fetterId);
		if(fetterCfg==null){
			sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
			return;
		}
		//验证开启条件
		if(fetterCfg != null && fetterCfg.getArchiveIds() != null){
			for(int id : fetterCfg.getArchiveIds()){
//				ArchiveCfg archiveCfg = ConfigManager.getInstance().getConfigByKey(ArchiveCfg.class, id);
//				if(archiveCfg==null){
//					sendError(protocol.getType(), Status.error.CONFIG_NOT_FOUND_VALUE);
//					return;
//				}
//				if(archiveCfg.getRefer() == 0){
//					sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
//					return;
//				}
				RoleEntity roleEntity = player.getPlayerData().getMercenaryByItemId(id);
				if(roleEntity == null || roleEntity.getRoleState() != Const.RoleActiviteState.IS_ACTIVITE_VALUE){
					sendError(protocol.getType(), Status.error.MERCENARY_NOT_FOUND_VALUE);
					return;
				}
			}
		}
		//发放奖励
		AwardItems awardItems = AwardItems.valueOf(fetterCfg.getAwardItem());
		awardItems.rewardTakeAffect(player, Action.OPEN_FETTER);
		//设置开启状态
		entity.getOpenFetters().add(fetterId);
		//entity.addalbumMap(fetterId, 1);	//測試用
		//保存DB
		entity.reConvert();
		entity.notifyUpdate();
		//刷新佣兵属性
		List<RoleEntity> activatedMercenaries = player.getPlayerData().getActiviceMercenary();
		for(RoleEntity roleEntity : activatedMercenaries){
			PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
		}
		player.getPlayerData().syncActivitedMercenaryRoleInfo();
		
		//推送客户端
		HPOpenFetterRes.Builder resp = HPOpenFetterRes.newBuilder();
		resp.setFetterId(fetterId);
		player.sendProtocol(Protocol.valueOf(HP.code.OPEN_FETTER_S_VALUE, resp));
	}
}
