package com.server.paynotice.service.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.server.paynotice.dbservice.CancelUserService;
import com.server.paynotice.dbservice.QueryInfoService;
import com.server.paynotice.dbservice.SnapshotService;
import com.server.paynotice.pojo.CancelUser;
import com.server.paynotice.pojo.GenResponse;
import com.server.paynotice.pojo.QueryInfo;
import com.server.paynotice.pojo.Snapshot;
import com.server.paynotice.service.RequestService;
import com.server.paynotice.util.DateUtil;

import io.netty.channel.Channel;

public class ClientQueryUser extends RequestService {

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
		QueryInfo info = QueryInfoService.queryByUserId(userid);
		Timestamp lastQueryTime = DateUtil.strToTimestamp("2000-01-01 00:00:00");
		if (info != null && info.getInserttime() != null) {
			lastQueryTime = info.getInserttime();
		}
		List<Snapshot> snapshotList = SnapshotService.queryUserSnapshots(lastQueryTime, userid);
		if (snapshotList != null && !snapshotList.isEmpty()) {
			reply.setStatus(-3);
			reply.setMsg("in action list");
		} else {
			reply.setStatus(0);
			reply.setMsg("enter game");
		}
		QueryInfoService.insertQueryInfo(userid);
		response(reply, channel);
	}

}
