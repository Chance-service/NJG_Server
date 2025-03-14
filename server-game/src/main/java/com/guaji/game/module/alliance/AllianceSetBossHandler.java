package com.guaji.game.module.alliance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.guaji.app.AppObj;
import org.guaji.net.protocol.Protocol;
import org.guaji.net.protocol.IProtocolHandler;
import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.Alliance.OpenBossTimeRequest;
import com.guaji.game.entity.AllianceEntity;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.manager.AllianceManager;
import com.guaji.game.player.Player;
import com.guaji.game.util.GsConst;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 公会设置自动开启BOSS
 */
public class AllianceSetBossHandler implements IProtocolHandler {
	
	@Override
	public boolean onProtocol(AppObj appObj, Protocol protocol) {
		
		// 数据转换
		Player player = (Player) appObj;
		// 玩家公会实体
		PlayerAllianceEntity pa_entity = player.getPlayerData().getPlayerAllianceEntity();
		int allianceId = pa_entity.getAllianceId();
		if (allianceId == 0) {
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NO_JOIN);
			return true;
		}
		// 公会实体
		AllianceEntity a_entity = AllianceManager.getInstance().getAlliance(allianceId);
		if (a_entity == null) {
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NONEXISTENT);
			return true;
		}
		// 权限判定
		if (pa_entity.getPostion() != GsConst.Alliance.ALLIANCE_POS_MAIN) {
			player.sendError(protocol.getType(), Status.error.ALLIANCE_NO_MAIN);
			return true;
		}
		// 解析数据
		OpenBossTimeRequest request = protocol.parseProtocol(OpenBossTimeRequest.getDefaultInstance());
		if (null == request.getOpenTimeListList() || request.getOpenTimeListList().size() != 3) {
			player.sendError(HP.code.ALLIANCE_SET_OPEN_BOSS_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
			return true;
		}
		// 时间计算
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		List<Long> openList = new ArrayList<Long>(3);
		for (String sOpenTime : request.getOpenTimeListList()) {
			if (null != sOpenTime && !sOpenTime.equals("")) {
				Date date = null;
				try {
					SimpleDateFormat sd_Format = new SimpleDateFormat("yyyy-MM-dd");
					date = sdFormat.parse(sd_Format.format(GuaJiTime.getMillisecond()) + " " + sOpenTime);
					long openTime = this.calculationOpenTime(date);
					openList.add(openTime);
				} catch (Exception e) {
					player.sendError(HP.code.ALLIANCE_SET_OPEN_BOSS_C_VALUE, Status.error.PARAMS_INVALID_VALUE);
					return true;
				}
			}
		}
		// 同步处理
		synchronized (a_entity) {
			// 转换时间格式
			List<Long> e_openList = a_entity.getAutomaticOpenList();
			e_openList.clear();
			e_openList.addAll(openList);
			a_entity.notifyUpdate(true);
			// 返回数据包构建
			player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_CREATE_S_VALUE, AllianceManager.getInstance().getAllianceInfo(a_entity, a_entity.getId(), player.getGold())));
		}
		return true;
	}

	/**
	 * 计算开启BOSS时间
	 * @param date
	 * @return
	 */
	private long calculationOpenTime(Date date) {
		
		if (date.getTime() > GuaJiTime.getMillisecond()) {
			return date.getTime();
		}
		return date.getTime() + 24 * 60 * 60 * 1000L;
	}

}
