package com.guaji.game.module;

import java.util.Date;
import java.util.List;

import org.guaji.db.DBManager;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Arena.ArenaItemInfo;
import com.guaji.game.config.TitleCfg;
import com.guaji.game.entity.EquipEntity;
import com.guaji.game.entity.PlayerEntity;
import com.guaji.game.entity.TitleEntity;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.ArenaManager;
import com.guaji.game.manager.ProfRankManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.PlayerTitle.ChooseTitleId;
import com.guaji.game.protocol.PlayerTitle.TitleInfo;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;

/**
 * 玩家称号模块
 */
public class PlayerTitleModule extends PlayerModule{

	public PlayerTitleModule(Player player) {
		super(player);
		/**
		 * 目前该功能不用 TODO
		 */
//		listenProto(HP.code.CHANGE_TITLE_C);//改变当前使用称号
//		
//		listenMsg(GsConst.MsgType.ARENA_RANK_CHARGE);//竞技场排名变更
//		listenMsg(GsConst.MsgType.VIP_LEVEL_CHANGE);//vip等级变更
//		listenMsg(GsConst.MsgType.CONTINUE_LOGIN);//连续登陆
//		listenMsg(GsConst.MsgType.ROLE_GODLY);//玩家身上神器数量
//		listenMsg(GsConst.MsgType.PROF_RANK_CHANGE);//职业排名变更
//		listenMsg(GsConst.MsgType.EQUIP_STAR);//装备星数
//		listenMsg(GsConst.MsgType.TEAM_BATTLE_CHAMPION);//团战冠军
	}
	
	@Override
	protected boolean onPlayerLogin() {
		
		player.getPlayerData().loadTitleEntity();
		return true;
	}
	
	@Override
	protected boolean onPlayerAssemble() {
		TitleEntity entity = player.getPlayerData().getTitleEntity();
		checkPlayerTitle(player);
		//团战冠军称号的判断（时间为空或者获得称号时间超出一周移除）
		if(entity.contains(GsConst.Title.STORM_KING)) {
			Date receiveDate = entity.getTeamBattleChampionDate();
			if(receiveDate == null) {
				entity.removeFinishId(GsConst.Title.STORM_KING);
				if(entity.getUseId() == GsConst.Title.STORM_KING) {
					entity.setUseId(0);	
				}
				entity.notifyUpdate(true);
			} else {
				long time = receiveDate.getTime();
				long curTime = GuaJiTime.getCalendar().getTime().getTime();
				if((int)(curTime - time)/1000 > 7*24*3600) {
					entity.removeFinishId(GsConst.Title.STORM_KING);
					if(entity.getUseId() == GsConst.Title.STORM_KING) {
						entity.setUseId(0);	
					}
					entity.notifyUpdate(true);
				}	
			}
			
		}
		
		updatePlayerSnap(entity);
		player.getPlayerData().syncTitleInfo();
		
		return super.onPlayerAssemble();
	}

	@Override
	public boolean onMessage(Msg msg) {
		
		if (msg.getMsg() == GsConst.MsgType.ARENA_RANK_CHARGE) {
			//竞技场类
			updatePlayerTitleArenaRank((int)msg.getParam(0), (int)msg.getParam(1), (int)msg.getParam(2));
			return true;
		} else if(msg.getMsg() == GsConst.MsgType.VIP_LEVEL_CHANGE) {
			//vip类
			updatePlayerVipTitle((int)msg.getParam(0));
			return true;
		} else if(msg.getMsg() == GsConst.MsgType.CONTINUE_LOGIN) {
			//连续登录类
			updatePlayerContinueLoginTitle((int)msg.getParam(0));
			return true;
		} else if(msg.getMsg() == GsConst.MsgType.ROLE_GODLY) {
			//神器类
			updatePlayerTitleGodly((int)msg.getParam(0));
			return true;
		} else if(msg.getMsg() == GsConst.MsgType.PROF_RANK_CHANGE) {
			//职业排行类
			updatePlayerTitleProfRankTop1((int)msg.getParam(0), (int)msg.getParam(1), (int)msg.getParam(2), (int)msg.getParam(3));
			return true;
		}else if(msg.getMsg() == GsConst.MsgType.EQUIP_STAR) {
			//10星神器
			updatePlayerTitleEquipStar((int)msg.getParam(0), (int)msg.getParam(1));
			return true;
		} else if(msg.getMsg() == GsConst.MsgType.TEAM_BATTLE_CHAMPION) {
			//风暴之王
			updatePlayerTitleTeamBattleChampion((int) msg.getParam(0), (int) msg.getParam(1));
			return true;
		}
		return super.onMessage(msg);
	}

	@Override
	public boolean onProtocol(Protocol protocol) {
		if(protocol.checkType(HP.code.CHANGE_TITLE_C)) {
			//修改正在使用的称号
			modifyUseTitle(protocol.parseProtocol(ChooseTitleId.getDefaultInstance())); 
			return true;
		}
		return super.onProtocol(protocol);
	}
	
	/**
	 * 修改正在使用的称号
	 * @param parseProtocol
	 */
	public void modifyUseTitle(ChooseTitleId parseProtocol) {
		
		int titleId = parseProtocol.getTitleId();
		
		TitleEntity title = player.getPlayerData().getTitleEntity();
		
		if(titleId != 0 && !title.contains(titleId)) {
			return;
		}
		title.setUseId(titleId);
		updatePlayerSnap(title);
		title.notifyUpdate(true);
		player.getPlayerData().syncTitleInfo();
	}
	
	/**
	 * 竞技场排名
	 * @param challengePlayerId 被挑战玩家的id
	 * @param challengeTitleId  被挑战玩家的称号
	 * @param playerTitleId     当前玩家的称号
	 */
	private void updatePlayerTitleArenaRank(int challengePlayerId, int challengeTitleId, int playerTitleId) {
		//修改当前玩家称号
		addFinishTitle(challengeTitleId);
		if(playerTitleId != 0) {
			removeFinishTitle(playerTitleId);
		}
		//修改被挑战玩家称号（这里应该会有并发问题）
		if(challengePlayerId != 0) {
			updateOtherPlayerTitle(challengePlayerId, challengeTitleId, 2, false);
			if(playerTitleId != 0)
				updateOtherPlayerTitle(challengePlayerId, playerTitleId, 1, false);
		}
	}
	
	/**
	 * 连续登录
	 * @param titleId
	 */
	private void updatePlayerContinueLoginTitle(int titleId) {
		addFinishTitle(titleId);
	}
	
	/**
	 * vip等级
	 * @param titleId
	 */
	private void updatePlayerVipTitle(int titleId) {
		addFinishTitle(titleId);
	}
	

	/**
	 * 玩家达成狂耀炫闪称号
	 * @param playerId
	 */
	private void updatePlayerTitleGodly(int titleId) {
		
		addFinishTitle(titleId);
	}
	
	/**
	 * 10星神器称号
	 * @param param
	 */
	private void updatePlayerTitleEquipStar(int titleId1, int titleId2) {
		
		if(titleId1 > 0)
			addFinishTitle(titleId1);
		if(titleId2 > 0)
			addFinishTitle(titleId2);
	}
	
	/**
	 * 职业排行榜称号
	 * @param challengeId
	 * @param challengedId
	 * @param prof
	 */
	private void updatePlayerTitleProfRankTop1(int oldTopId, int prof, int newTopId, int type) {
		
		int titleId = getProfRankTitleIdByProfAndRank(prof, type);
		
		if(oldTopId == player.getId()) {
			removeFinishTitle(titleId);
			//更新其他玩家称号
			updateOtherPlayerTitle(newTopId, titleId, 1, false);
			
		}else if(newTopId == player.getId()){
			addFinishTitle(titleId);
			if(oldTopId != 0) {
				updateOtherPlayerTitle(oldTopId, titleId, 2, false);
			}
		} else {
			if(oldTopId != 0)
				updateOtherPlayerTitle(oldTopId, titleId, 2, false);
			updateOtherPlayerTitle(newTopId, titleId, 1, false);
			
		}
	}
	
	/**
	 * 团战冠军
	 * @param param
	 */
	private void updatePlayerTitleTeamBattleChampion(int playerId, int titleId) {
		
		updateOtherPlayerTitle(playerId, titleId, 1, true);
	}
	
	/**
	 * 添加完成的称号
	 * @param titleId
	 */
	private void addFinishTitle(int titleId) {
		addFinishTitle(player, titleId, false);
	}
	
	private void addFinishTitle(Player player, int titleId, boolean isTeamBattle) {
		TitleEntity title = player.getPlayerData().getTitleEntity();
		title.addFinishId(titleId);
		if(isTeamBattle) {
			title.setTeamBattleChampionDate(GuaJiTime.getCalendar().getTime());
		}
		title.notifyUpdate(true);
		//更新玩家快照
		updatePlayerSnap(title);
		//同步前端信息
		player.getPlayerData().syncTitleInfo();
		
		BehaviorLogger.log4Service(player, Source.SYS_OPERATION, Action.TITLE_CHANGE, Params.valueOf("titleId", titleId), Params.valueOf("status", 1));
	}
	
	private void removeFinishTitle(Player player, int titleId) {
		
		TitleEntity title = player.getPlayerData().getTitleEntity();
		title.removeFinishId(titleId);
		if(title.getUseId() == titleId) {
			title.setUseId(0);
		}
		title.notifyUpdate(true);
		updatePlayerSnap(title);
		player.getPlayerData().syncTitleInfo();
		
		BehaviorLogger.log4Service(player, Source.SYS_OPERATION, Action.TITLE_CHANGE, Params.valueOf("titleId", titleId), Params.valueOf("status", 2));
	}
	
	/**移除称号*/
	private void removeFinishTitle(int titleId) {
		removeFinishTitle(player, titleId);
	}
	
	
	/**
	 * 更新玩家快照
	 * @param title
	 */
	private static void updatePlayerSnap(TitleEntity title) {
		PlayerSnapshotInfo.Builder snapshot = SnapShotManager.getInstance().getPlayerSnapShot(title.getPlayerId());
		if(snapshot == null){
			return;
		}
		TitleInfo.Builder titleBuilder = TitleInfo.newBuilder();
		for(int titleId : title.getFinishIdSet()) {
			titleBuilder.addTitleIds(titleId);
		}
		titleBuilder.setTitleId(title.getUseId());
		snapshot.setTitleInfo(titleBuilder);
	}
	
	/**
	 * 修改其他玩家称号
	 * @param playerId 玩家id
	 * @param titleId 称号id
	 * @param type 修改类型, 1:添加;2:减少
	 */
	private void updateOtherPlayerTitle(int playerId, int titleId, int type, boolean isTeamBattle) {
		Player player = PlayerUtil.queryPlayer(playerId); 
		if(player != null) {
			if(type == 1) {
				addFinishTitle(player, titleId, isTeamBattle);
			} else if(type == 2){
				removeFinishTitle(player, titleId);
			}
		} else {
			updateOfflinePlayerTitle(playerId, titleId, type, isTeamBattle);
		}
	}
	
	/**
	 * 离线修改玩家称号
	 * @param playerId
	 * @param titleId
	 * @param type
	 * @param isTeamBattle
	 */
	public static synchronized void updateOfflinePlayerTitle(int playerId, int titleId, int type, boolean isTeamBattle) {
		TitleEntity playerTitle = null;
		List<TitleEntity> titles = DBManager.getInstance().query("from TitleEntity where playerid=? and invalid=0", playerId);
		if(titles.size() > 0) {
			playerTitle = titles.get(0);
			if(type == 1) {
				playerTitle.addFinishId(titleId);
			} else if(type == 2) {
				playerTitle.removeFinishId(titleId);
				if(playerTitle.getUseId() == titleId) {
					playerTitle.setUseId(0);
				}
			}
			if(isTeamBattle) {
				playerTitle.setTeamBattleChampionDate(GuaJiTime.getCalendar().getTime());
			}
			updatePlayerSnap(playerTitle);
			DBManager.getInstance().update(playerTitle);
			BehaviorLogger.log4Service(playerId, Source.SYS_OPERATION, Action.TITLE_CHANGE, Params.valueOf("titleId", titleId), Params.valueOf("status", type));
		}
	}
	
	/**
	 * 通过角色职业和排名获得相应称号
	 * @param prof
	 * @param type
	 * @return
	 */
	private int getProfRankTitleIdByProfAndRank(int prof, int type) {
		
		int titleId = 0;
		switch(prof) {
		case 1:
			switch(type) {
			case 1:
				titleId = GsConst.Title.EXTREME_AREA;
				break;
			case 2:
				titleId = GsConst.Title.BUDDHA_WARRIOR;
				break;
			case 3:
				titleId = GsConst.Title.LAND_WARRIOR;
				break;
			}
			break;
		case 2:
			switch(type) {
			case 1:
				titleId = GsConst.Title.EXTREME_HUNTING_GOD;
				break;
			case 2:
				titleId = GsConst.Title.BUDDHA_HUNTER;
				break;
			case 3:
				titleId = GsConst.Title.LAND_HUNTER;
				break;
			}
			break;
		case 3:
			switch(type) {
			case 1:
				titleId = GsConst.Title.SUPREME_LAW_OF_GOD;
				break;
			case 2:
				titleId = GsConst.Title.BUDDHA_FASTER;
				break;
			case 3:
				titleId = GsConst.Title.LAND_FASTER;
				break;
			}
			break;
		}
		return titleId;
	}
	
	/**
	 * 玩家登录时检测
	 * @param player
	 */
	private void checkPlayerTitle(Player player) {
		TitleEntity titleEntity = player.getPlayerData().getTitleEntity();
		// 竞技场类型称号
		ArenaItemInfo.Builder builder = ArenaManager.getInstance().getSelfArenaInfo(player);
		if(builder != null){
			int rank = builder.getRank();
			int arenaTitleId = TitleCfg.getTitleIdByTypeAndCondition(GsConst.MsgType.ARENA_RANK_CHARGE, rank); 
			if(arenaTitleId > 0) {
				if(!titleEntity.contains(arenaTitleId)) {
					titleEntity.addFinishId(arenaTitleId);
					titleEntity.notifyUpdate(true);
				}
			}
			
			int arenaTitleId2 = TitleCfg.getTitleIdByTypeAndCondition(GsConst.MsgType.ARENA_RANK_EXTENT, rank); 
			if(arenaTitleId2 > 0) {
				if(!titleEntity.contains(arenaTitleId2)) {
					titleEntity.addFinishId(arenaTitleId2);
					titleEntity.notifyUpdate(true);
				}
			}
		}
		//vip类型称号
		PlayerEntity playerEntity = player.getPlayerData().getPlayerEntity();
		int vipLevel = playerEntity.getVipLevel();
		List<Integer> titleIds = TitleCfg.getFinishTitleIdsByType(GsConst.MsgType.VIP_LEVEL_CHANGE, vipLevel);
		
		for(int titleId : titleIds) {
			if(!titleEntity.contains(titleId)) {
				titleEntity.addFinishId(titleId);
				titleEntity.notifyUpdate(true);
			}
		}
		
		//战力榜类型的称号
		int fightValueRank = ProfRankManager.getInstance().getPlayerRankByPlayerId(player.getId(), player.getProf());
		fightValueRank++;
		if(fightValueRank != 0) {
			int titleId = getProfRankTitleIdByProfAndRank(player.getProf(), fightValueRank);
			if(titleId != 0 && !titleEntity.contains(titleId)) {
				titleEntity.addFinishId(titleId);
				titleEntity.notifyUpdate(true);
			}
		}
		
		//身上神器称号
		int godlyCount = PlayerUtil.calRoleGodly(player.getPlayerData().getMainRole(), player.getPlayerData()); 
		int titleId = TitleCfg.getTitleIdByTypeAndCondition(GsConst.MsgType.ROLE_GODLY, godlyCount);
		if(titleId > 0 && !titleEntity.contains(titleId)) {
			titleEntity.addFinishId(titleId);
			titleEntity.notifyUpdate(true);
		}
		
		//10星神器称号
		for(EquipEntity equip : player.getPlayerData().getEquipEntities()) {
			if(equip != null) {
				int titleId1 = TitleCfg.getTitleId(GsConst.MsgType.EQUIP_STAR, equip.getStarLevel());
				int titleId2 = TitleCfg.getTitleId(GsConst.MsgType.EQUIP_STAR, equip.getStarLevel2());
				if(titleId1 > 0) {
					titleEntity.addFinishId(titleId1);
					titleEntity.notifyUpdate(true);
					break;
				}
				if(titleId2 > 0) {
					titleEntity.addFinishId(titleId2);
					titleEntity.notifyUpdate(true);
					break;
				}
			}
		}
		
	}
}
