package com.guaji.game.module;

import org.guaji.annotation.ProtocolHandlerAnno;
import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;
import org.guaji.util.services.ReportService;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.protocol.Guide.GuideInfoBean;
import com.guaji.game.protocol.Guide.HPPlayStorySync;
import com.guaji.game.protocol.Guide.HPResetGuideInfo;
import com.guaji.game.GsApp;
import com.guaji.game.entity.StateEntity;
import com.guaji.game.log.BehaviorLogger;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.log.BehaviorLogger.Source;
import com.guaji.game.manager.TapDBManager;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.util.AdjustEventUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsConst.GuideType;

import net.sf.json.JSONObject;

import com.guaji.game.protocol.HP;

/**
 * 新手引导模块
 * 
 * 海外该模块暂时不用
 *
 */
public class PlayerGuideModule extends PlayerModule {

	public PlayerGuideModule(Player player) {
		super(player);
	}

	/**
	 * 修改新手引导信息
	 * 
	 * @param protocol
	 * @return
	 */
	@ProtocolHandlerAnno(code = HP.code.RESET_GUIDE_INFO_C_VALUE)
	protected void onResetGuideInfo(Protocol hawkProtocol) {
		
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		HPResetGuideInfo protocol = hawkProtocol.parseProtocol(HPResetGuideInfo.getDefaultInstance());
		GuideInfoBean bean = protocol.getGuideInfoBean();
		int guideId = bean.getGuideId();
		int step = bean.getStep();
		
		if (guideId == GuideType.FIRST_STEP && stateEntity.getGuideMap().size() > 1) {
			step = 0;
		}
		
		JSONObject jsonobj = new JSONObject();
		
		jsonobj.put(GsConst.tapDBPropertyName.uid,player.getTapDBUId());
		jsonobj.put(GsConst.tapDBPropertyName.teach_target,guideId);
		jsonobj.put(GsConst.tapDBPropertyName.teach_step,step);
		
		Msg msg = Msg.valueOf(GsConst.MsgType.TAPDB_EVENT_RECORD);
		msg.pushParam(player);
		msg.pushParam(GsConst.tapDBEventName.event_teach_step);
		msg.pushParam(jsonobj);
		msg.pushParam(false);
		GuaJiXID targetXId = GuaJiXID.valueOf(GsConst.ObjType.MANAGER, GsConst.ObjId.TabDB_MANAGER);
		GsApp.getInstance().postMsg(targetXId, msg);
		
		//TapDBManager.getInstance().tapdbEvent(player.getTabDBAppId(),player.get, GsConst.tapDBEventName.event_teach_step, jsonobj, false);
		
		stateEntity.addGuideMap(guideId, step);
		stateEntity.notifyUpdate();
		
		// 记录日志
		//BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.MODIFY_GUIDE, Params.valueOf("guideBean", bean));
		BehaviorLogger.log4Service(player, Source.USER_OPERATION, Action.MODIFY_GUIDE, Params.valueOf("guideId", guideId),Params.valueOf("step", step));
	}
	@ProtocolHandlerAnno(code = HP.code.PLAYSTORYDONE_SYNC_C_VALUE)
	protected void onsetPlayStoryInfo(Protocol hawkProtocol) {
		StateEntity stateEntity = player.getPlayerData().getStateEntity();
		if (stateEntity != null) {
			HPPlayStorySync protocol = hawkProtocol.parseProtocol(HPPlayStorySync.getDefaultInstance());
			int done = protocol.getIsDone();
			stateEntity.setplaystory(done);
			stateEntity.notifyUpdate();
		}
	}
}
