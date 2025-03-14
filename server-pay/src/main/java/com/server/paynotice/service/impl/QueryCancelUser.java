package com.server.paynotice.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.server.paynotice.dbservice.SnapshotService;
import com.server.paynotice.pojo.GenResponse;
import com.server.paynotice.pojo.Snapshot;
import com.server.paynotice.pojo.Snapshot1;
import com.server.paynotice.service.RequestService;
import com.server.paynotice.util.DateUtil;

import io.netty.channel.Channel;

public class QueryCancelUser extends RequestService {

	@Override
	public void request(Map<String, String> param, Channel channel) {
	
		GenResponse reply = new GenResponse();
		if (param == null || param.isEmpty()) {
			reply.setStatus(-1);
			reply.setMsg("empty lasttime");
			response(reply, channel);
			return;
		}
		String lastTime = param.get("lasttime");
		if (lastTime == null || lastTime.trim().equals("")) {
			reply.setStatus(-1);
			reply.setMsg("empty lasttime");
			response(reply, channel);
			return;
		}
		List<Snapshot> list = SnapshotService.querySnapshots(DateUtil.strToTimestamp(lastTime));
		List<Snapshot1> replyList = new ArrayList<Snapshot1>();
		for(Snapshot ss:list)
		{
			Snapshot1 s1 = new Snapshot1();
			s1.setAction(ss.getAction());
			s1.setUserid(ss.getUserid());
			s1.setId(ss.getId());
			s1.setInserttime(DateUtil.getTimeStr(ss.getInserttime()));
			replyList.add(s1);
		}
		response(replyList, channel);
	}

}
