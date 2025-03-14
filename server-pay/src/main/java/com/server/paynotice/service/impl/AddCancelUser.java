package com.server.paynotice.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.server.paynotice.dbservice.CancelUserService;
import com.server.paynotice.dbservice.SnapshotService;
import com.server.paynotice.pojo.CancelUser;
import com.server.paynotice.pojo.GenResponse;
import com.server.paynotice.service.RequestService;

import io.netty.channel.Channel;

public class AddCancelUser extends RequestService {
	private static Logger logger = Logger.getLogger(AddCancelUser.class);
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
		List<CancelUser> list = CancelUserService.queryUsersByUserid(userid);
		if (list != null && !list.isEmpty()) {
			reply.setStatus(-2);
			reply.setMsg("in cancel list");
			response(reply, channel);
			return;
		}
		int ret = CancelUserService.insertUser(userid);
		if (ret <= 0) {
			reply.setStatus(-3);
			reply.setMsg("cannot insert into cancel list");
			response(reply, channel);
			return;
		}
		SnapshotService.insertSnapshot(1, userid);
		reply.setStatus(1);
		reply.setMsg("add cancel user success");
		response(reply, channel);
	}

}
