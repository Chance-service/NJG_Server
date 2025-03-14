package com.test.proto;

import com.guaji.game.protocol.Chat.HPSendChat;
import com.test.robot.Robot;

public class Proto_Chat implements IProtoGeneror {

	@Override
	public byte[] genBuilder(Robot robot) {
		// TODO Auto-generated method stub
		byte[] ret = null;
		HPSendChat.Builder builder = HPSendChat.newBuilder();
		builder.setChatMsg("aadfasdfasdddd["+robot.getPlayerId()+" ]adfasdfasdf");
		builder.setChatType(7);
		ret = builder.build().toByteArray();
		return ret;
	}

}
