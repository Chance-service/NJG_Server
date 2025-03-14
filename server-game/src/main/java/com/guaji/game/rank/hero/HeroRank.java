package com.guaji.game.rank.hero;

import java.util.ArrayList;
import java.util.List;

import org.guaji.app.App;
import org.guaji.app.AppObj;
import org.guaji.db.DBManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.obj.ObjBase;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.entity.RoleEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.manager.RankManager;
import com.guaji.game.manager.SnapShotManager;
import com.guaji.game.module.PlayerProfRankModule;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Ranks.HPHeroRankInfo;
import com.guaji.game.protocol.Ranks.HeroTopRankListRet;
import com.guaji.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.guaji.game.rank.BaseRank;
import com.guaji.game.rank.IRankingObj;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;

public abstract class HeroRank extends BaseRank {

	@Override
	public void loadRank() {

		List<Object> rowInfos = DBManager.getInstance().executeQuery(getQuerySql());
		String aStr = "";
		for (Object rowInfo : rowInfos) {
			Object[] colInfos = (Object[]) rowInfo;
			//"SELECT playerId,itemId,fightvalue,level,starLevel
			aStr = String.format("%d,%d,%d,%d,%d,%d",
					(Integer)colInfos[0],(Integer)colInfos[1],(Integer)colInfos[2],
					(Integer)colInfos[3],(Integer)colInfos[4],(Integer)colInfos[5]);
			HeroInfoRank heroInfo = new HeroInfoRank(aStr);
			heroInfo.setRankType(this.getRankType());
			//setRankParam(aStr, heroInfo);
			updateRank(heroInfo);
		}
		openRank();	
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void buildRankObjs(List list) {
		List<IRankingObj> topLimitRank = getTopLimitRank(GsConst.MAX_RANK_DISPLAY_NUM);
		for (IRankingObj iRankingObj : topLimitRank) {
			HeroInfoRank heroInfoRank = (HeroInfoRank) iRankingObj;
			int playerId = heroInfoRank.getId();
			PlayerSnapshotInfo.Builder playerSnapShot = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
			int allianceId = playerSnapShot.getAllianceInfo().getAllianceId();
			
			String allianceName ="";
			if(AllianceManager.getInstance().getAlliance(allianceId)!=null)
			{
				allianceName=AllianceManager.getInstance().getAlliance(allianceId).getName();
			}else {
				allianceName =PlayerProfRankModule.NO_ALLIANCE;
			}
			HPHeroRankInfo.Builder builder = HPHeroRankInfo.newBuilder();
			builder.setPlayerName(playerSnapShot.getMainRoleInfo().getName());
			builder.setPlayerId(playerId);
			builder.setAllianceName(allianceName);
			builder.setItemId(heroInfoRank.getItemId());
			builder.setSkinId(heroInfoRank.getSkinId());
			builder.setLevel(heroInfoRank.getLevel());
			builder.setStarLevel(heroInfoRank.getStarLevel());
			builder.setRankNum(heroInfoRank.getRankPos());
			builder.setRankData(heroInfoRank.GetRankData());
			list.add(builder.build());
		}
	}

	@Override
	public void parseStr(String str) {

		HeroInfoRank winRank = new HeroInfoRank(str);
		winRank.setRankType(this.getRankType());
		updateRank(winRank);
	}

	@Override
	public void sendRank(String str) {

		int playerId = Integer.parseInt(str);
		ObjBase<GuaJiXID, AppObj> objBase = App.getInstance()
				.queryObject(GuaJiXID.valueOf(GsConst.ObjType.PLAYER, playerId));
		if (objBase == null) {
			return;
		}
		Player player = (Player) objBase.getImpl();
		List<HPHeroRankInfo> levelRankInfos = new ArrayList<HPHeroRankInfo>();
		HeroTopRankListRet.Builder builder = HeroTopRankListRet.newBuilder();
		levelRankInfos.clear();

		RankManager.getInstance().getRankByType(this.getRankType(), levelRankInfos);
		builder.addAllHeroItemInfo(levelRankInfos);
		builder.setRankType(this.getRankType());

		int allianceId = player.getPlayerData().getPlayerAllianceEntity().getAllianceId();
		String allianceName = allianceId == 0 ? PlayerProfRankModule.NO_ALLIANCE
				: AllianceManager.getInstance().getAlliance(allianceId).getName();
		HPHeroRankInfo.Builder myRankItem = HPHeroRankInfo.newBuilder();
		myRankItem.setPlayerName(player.getName());
		myRankItem.setPlayerId(playerId);
		myRankItem.setAllianceName(allianceName);
		
		HeroInfoRank mySelfinfo = (HeroInfoRank) this.getRankingObjById(playerId);
		if (mySelfinfo != null) {
			myRankItem.setItemId(mySelfinfo.getItemId());
			myRankItem.setSkinId(mySelfinfo.getSkinId());
			myRankItem.setLevel(mySelfinfo.getLevel());
			myRankItem.setStarLevel(mySelfinfo.getStarLevel());
			myRankItem.setRankNum(mySelfinfo.getRankPos());
			myRankItem.setRankData(mySelfinfo.GetRankData());
		} else {
			int attr = GameUtil.getAttrByRankType(this.getRankType());
			RoleEntity heroEntity = player.getPlayerData().getBestHeroByAttr(attr);
			if (heroEntity != null) {
				myRankItem.setItemId(heroEntity.getItemId());
				myRankItem.setSkinId(heroEntity.getSkinId());
				myRankItem.setLevel(heroEntity.getLevel());
				myRankItem.setStarLevel(heroEntity.getStarLevel());
				myRankItem.setRankNum(0);
				myRankItem.setRankData(heroEntity.getFightValue());
			} else {
				myRankItem.setItemId(-1);
				myRankItem.setSkinId(-1);
				myRankItem.setLevel(-1);
				myRankItem.setStarLevel(-1);
				myRankItem.setRankNum(-1);
				myRankItem.setRankData(-1);
			}
		}
		builder.setMySelf(myRankItem);
		player.sendProtocol(Protocol.valueOf(HP.code.RANKING_LIST_S_VALUE, builder));
	}

	@Override
	protected void afterCloseRank() {

	}

	@Override
	protected void afterOpenRank() {

	}

	/**
	 * @return 返回具体执行的sql 语句
	 */
	protected abstract String getQuerySql();

	/**
	 * @param playerEntity 玩家数据
	 * @param infoRank     排行榜数据
	 */
	protected abstract void setRankParam(String ParamStr, HeroInfoRank infoRank);

}
