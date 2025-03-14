package com.guaji.game.korean;

import java.util.List;

import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;
import org.guaji.util.GuaJiTickable;

import com.guaji.game.config.KoreanBroadcastCfg;
import com.guaji.game.manager.ChatManager;
import com.guaji.game.manager.ChatMsg;
import com.guaji.game.protocol.Const;

public class BroadCastService extends GuaJiTickable{
	
	
	private int curTickNum = 0;
	private int lastTickTime = 0;

	@Override
	public String getName() {
		return "koreanBroadCast";
	}

	@Override
	public void onTick() {
		List<KoreanBroadcastCfg> msgs= ConfigManager.getInstance().getConfigList(KoreanBroadcastCfg.class);
		if(GuaJiTime.getSeconds() - lastTickTime >= 10) {
			ChatMsg chatMsg = new ChatMsg();
			chatMsg.setType(Const.chatType.WORLD_BROADCAST_VALUE);
			chatMsg.setChatMsg(msgs.get(curTickNum).getMessage());
			ChatManager.getInstance().postBroadcast(chatMsg);
			lastTickTime = GuaJiTime.getSeconds();
			curTickNum++;
			curTickNum %= msgs.size();
		}
	}
}
