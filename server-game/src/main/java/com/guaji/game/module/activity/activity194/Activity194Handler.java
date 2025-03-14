package com.guaji.game.module.activity.activity194;

import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.net.protocol.Protocol;

import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.SeasonTowerCfg;
import com.guaji.game.config.SeasonTowerTimeCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.manager.SeasonTowerRankManager;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Activity6.SeasonTowerData;
import com.guaji.game.protocol.Activity6.SeasonTowerRankInfo;
import com.guaji.game.protocol.Activity6.SeasonTowerRanking;
import com.guaji.game.protocol.Activity6.SeasonTowerReq;
import com.guaji.game.protocol.Activity6.SeasonTowerResp;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.ActivityUtil;

/**
 * 累積購買次數召喚領取協定
 */
public class Activity194Handler implements IProtocolHandler {
	static final int seasonTower_sync = 0;
	static final int seasonTower_rankSync = 1;
	static final int seasonTower_choose = 2;
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		// TODO Auto-generated method stub
		
		Player player = (Player) appObj;
		
		// 检测活动是否开放
		int activityId = Const.ActivityId.ACTIVITY194_SeasonTower_VALUE;
		ActivityTimeCfg timeConfig = ActivityUtil.getCurActivityTimeCfg(activityId);
		
		int timeIdx = SeasonTowerTimeCfg.getValidTimeIdx();
		
		if (timeConfig == null || player == null || timeIdx == -1) {
			// 活动已关闭
			player.sendError(protocol.getType(), Status.error.ACTIVITY_CLOSE);
			return true;
		}

		// 解析接受到的数据
		SeasonTowerReq request = protocol.parseProtocol(SeasonTowerReq.getDefaultInstance());

		int action = request.getAction();
				
				
		int stageId = timeConfig.getStageId();
		Activity194Status status = ActivityUtil.getActivityStatus(player.getPlayerData(), activityId, stageId, Activity194Status.class);
		
		if (status == null) {
			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
			return true;
		}
		
		ActivityUtil.SeaSonTowerInit(player, timeConfig, status,timeIdx);
		
		
		int currfloor = status.getNowfloor();
		if (status.getAwardrecord().contains(currfloor)) {
			SeasonTowerCfg TowerCfg = ConfigManager.getInstance().getConfigByKey(SeasonTowerCfg.class, currfloor);
			if ((TowerCfg != null)&&(TowerCfg.getNextstage() != 0)) {
				int nextId = TowerCfg.getNextstage();
				status.setNowfloor(nextId);
				player.getPlayerData().updateActivity(activityId,stageId);
			}
		}
						
		// 业务分支处理
		switch (action) {
		case seasonTower_sync:
		case seasonTower_rankSync:
			SyncInfo(timeConfig,action,player,status,timeIdx);
			break;
//		case seasonTower_choose:
//			onChooseFloor(protocol,timeConfig,player,action,status);
//			break;
		default:
			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
			break;
		}
		return true;
	}
	
//	protected boolean onChooseFloor(Protocol protocol,ActivityTimeCfg timeConfig,Player player,int action,Activity194Status status) {
//		SeasonTowerReq request = protocol.parseProtocol(SeasonTowerReq.getDefaultInstance());
//		
//		if (!status.isChooseFloor()) {
//			// 狀態不能選擇樓層
//			player.sendError(protocol.getType(), Status.error.DATA_NOT_FOUND_VALUE);
//			return false;
//		}
//		
//		int id = request.getChoose();
//		
//		SeasonTowerCfg TowerCfg = ConfigManager.getInstance().getConfigByKey(SeasonTowerCfg.class, id);
//		
//		if (TowerCfg == null) {
//			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
//			return false;
//		}
//		
//		int decFloor = SysBasicCfg.getInstance().getSeasonTowerFloorContrl();
//		
//		
//		int chooselimit = (status.getRank() > decFloor) ? (status.getRank() - decFloor) : 1;
//		
//		if ((id <= 0) || (id > chooselimit)) {
//			// 條件未達成
//			player.sendError(protocol.getType(), Status.error.PARAMS_INVALID_VALUE);
//			return false;
//		}
//		
//		status.setNowfloor(id);
//		
//		status.setRank(id-1);
//		
//		status.setChooseFloor(false);
//
//		player.getPlayerData().updateActivity(timeConfig.getActivityId(),timeConfig.getStageId());
//				
//		SyncInfo(timeConfig,action,player,status,timeIdx);
//			
//		return true;
//	}
			
	/**
	 * 同步資訊
	 * @param action
	 * @param player
	 * @param timeCfg
	 * @param status
	 */
	private static void SyncInfo(ActivityTimeCfg timeConfig,int action,Player player, Activity194Status status,int timeIdx ) {
		SeasonTowerResp.Builder builder = getBuilder(timeConfig,action,player,status,timeIdx);
		player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY194_SEASON_TOWER_S, builder));
	}
	
	private static SeasonTowerResp.Builder getBuilder(ActivityTimeCfg timeConfig,int action,Player player,Activity194Status status,int timeIdx ) {
		// 返回包
		SeasonTowerResp.Builder response = SeasonTowerResp.newBuilder();
		
		response.setAction(action);

		if ((action == seasonTower_sync) || (action == seasonTower_choose)) {
								
			SeasonTowerData.Builder BaseInfo = SeasonTowerData.newBuilder();
			BaseInfo.setMaxFloor(status.getRank()); // 本身資料Rank為已攻略過的關卡(取得獎勵)
			SeasonTowerTimeCfg towertimeCfg = SeasonTowerTimeCfg.geTimeIdxCfg(timeIdx);
			if (towertimeCfg != null) {
				BaseInfo.setEndTime(towertimeCfg.getlEndTime());
			} else {
				BaseInfo.setEndTime(0);
			}
			BaseInfo.addAllTakeId(status.getAwardrecord());
			
			response.setBaseInfo(BaseInfo);
		}
		
		if (action == seasonTower_rankSync)	{
			
			// 刷新排行数据
			SeasonTowerRankManager.getInstance().refreshRank();
			
			SeasonTowerRanking.Builder rankInfo = SeasonTowerRanking.newBuilder();
			
			SeasonTowerRankInfo.Builder ownRankItem = SeasonTowerRankInfo.newBuilder();
			
	        int Rank = SeasonTowerRankManager.getInstance().getPlayerRank(player.getId());
	        if (Rank != 0) {
	        	Activity194Status sdata = SeasonTowerRankManager.getInstance().getPlayerStatus(player.getId());
	        	if (sdata != null) {
	        		ownRankItem.setMaxFloor(sdata.getNowfloor());
	        		ownRankItem.setDoneTime(sdata.getDoneTime());
	        	} else {
	        		ownRankItem.setMaxFloor(0);
	        		ownRankItem.setDoneTime(0);
	        	}
	        } else {
	        	 ownRankItem.setMaxFloor(0);
	        	 ownRankItem.setDoneTime(0);
	        }
	        ownRankItem.setRank(Rank);
	        ownRankItem.setName(player.getName());
	        ownRankItem.setHeadIcon(player.getPlayerData().getPlayerEntity().getHeadIcon());
	        rankInfo.setSelfRankItem(ownRankItem);
	        List<SeasonTowerRankInfo.Builder> rankItemList = SeasonTowerRankManager.getInstance().getRankTop(100);
	        for (SeasonTowerRankInfo.Builder item : rankItemList) {
	        	rankInfo.addOtherRankItem(item);
	        }
	        
	        response.setRankingInfo(rankInfo);
		}
		
		return response;
	}
	
}
