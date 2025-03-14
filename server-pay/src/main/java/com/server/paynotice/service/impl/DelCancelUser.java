package com.server.paynotice.service.impl;

import java.util.Map;

import com.server.paynotice.dbservice.CancelUserService;
import com.server.paynotice.dbservice.SnapshotService;
import com.server.paynotice.pojo.GenResponse;
import com.server.paynotice.service.RequestService;

import io.netty.channel.Channel;

public class DelCancelUser extends RequestService {

	@Override
	public void request(Map<String, String> param, Channel channel) {
		// TODO Auto-generated method stub
		GenResponse reply = new GenResponse();
		if (param == null || param.isEmpty()) {
			reply.setStatus(-1);
			reply.setMsg("empty userid");
			response(reply, channel);
			return;
		}
		String userid = param.get("userid");
		if (userid == null || userid.trim().equals("")) {
			reply.setStatus(-1);
			reply.setMsg("empty userid");
			response(reply, channel);
			return;
		}
		int ret = CancelUserService.delUser(userid);
		if (ret > 0) {
			SnapshotService.insertSnapshot(2, userid);
		}
		reply.setStatus(1);
		reply.setMsg("del cancel user scucess");
		response(reply, channel);
	}

}
