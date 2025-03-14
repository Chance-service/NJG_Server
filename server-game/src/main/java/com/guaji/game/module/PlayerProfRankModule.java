package com.guaji.game.module;

import java.util.List;
import java.util.Map;

import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.manager.ProfRankManager;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.ProfRank.HPProfRankingList;
import com.guaji.game.protocol.ProfRank.HPProfRankingListRet;
import com.guaji.game.protocol.ProfRank.RankItemInfo;
import com.guaji.game.protocol.Ranks.HPTopRankListGet;
import com.guaji.game.protocol.Status;
import com.guaji.game.protocol.Const.RankType;

public class PlayerProfRankModule extends PlayerModule {

	public static final String NO_ALLIANCE = "no alliance";
	
	public PlayerProfRankModule(Player player) {
		super(player);

		listenProto(HP.code.PROF_RANK_LIST_C);
		listenProto(HP.code.RANKING_LIST_C);

	}

	@Override
	public boolean onProtocol(Protocol protocol) {
		if (protocol.checkType(HP.code.PROF_RANK_LIST_C)) {
			onProfRankList(protocol.parseProtocol(HPProfRankingList.getDefaultInstance()));
			return true;
		}else if(protocol.checkType(HP.code.RANKING_LIST_C)) {
			onRankList(protocol.parseProtocol(HPTopRankListGet.getDefaultInstance()));
			return true;
		}
		return super.onProtocol(protocol);
	}

	private void onProfRankList(HPProfRankingList parseProtocol) {
		int type = parseProtocol.getProfType();
		if (type != GsConst.ProfType.WARRIOR && type != GsConst.ProfType.HUNTER && type != GsConst.ProfType.MASTER && type!=0) {
			sendError(HP.code.PROF_RANK_LIST_C_VALUE, Status.error.NOT_EXISTS_PROF_TYPE_VALUE);
			return;
		}

		ProfRankManager manager = ProfRankManager.getInstance();
		Map<Integer, Integer> entitys = manager.getTopNRank(type, SysBasicCfg.getInstance().getMaxProfRankShowNum());
		List<RankItemInfo.Builder> itemBuilders = manager.genRankBuilder(entitys);
		HPProfRankingListRet.Builder builder = HPProfRankingListRet.newBuilder();
		if (player.getProf() == type || type == 0) {
			int playerRank = manager.getPlayerRank(player.getPlayerData(), type);
			if (playerRank >= 0) {
				builder.setSelfRank(playerRank + 1);
			} else {
				builder.setSelfRank(0);
			}
		}

		for (RankItemInfo.Builder itemBuilder : itemBuilders) {
			builder.addRankInfo(itemBuilder);
		}
		builder.setVersion(1);
		sendProtocol(Protocol.valueOf(HP.code.PROF_RANK_LIST_S, builder));
	}
	private void onRankList(HPTopRankListGet parseProtocol) {
		int type = parseProtocol.getRankType();
		RankType rankType=RankType.valueOf(type);
		if(rankType==null) {
			sendError(HP.code.RANKING_LIST_C_VALUE, Status.error.NOT_EXISTS_PROF_TYPE_VALUE);
			return;
		}
		Msg hawkMsg = Msg.valueOf(GsConst.MsgType.ON_RANK_GET);
		hawkMsg.pushParam(rankType);
		hawkMsg.pushParam(String.valueOf(player.getId()));
		GuaJiXID targetXId = GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.RANK_MANAGER);
		GsApp.getInstance().postMsg(targetXId, hawkMsg);
	}

}
