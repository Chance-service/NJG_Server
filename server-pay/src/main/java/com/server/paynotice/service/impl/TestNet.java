package com.server.paynotice.service.impl;

import java.util.Map;

import com.server.paynotice.pojo.GenResponse;
import com.server.paynotice.service.RequestService;

import io.netty.channel.Channel;

public class TestNet extends RequestService{

	@Override
	public void request(Map<String, String> param, Channel channel) {
		// TODO Auto-generated method stub
		GenResponse reply = new GenResponse();
		reply.setStatus(0);
		reply.setMsg("net is ok");
		response(reply, channel);
	}

	@Override
	public boolean verify(Map<String, String> param, Channel channel) {
		// TODO Auto-generated method stub
		//return super.verify(param, channel);
		
		return true;

	}

}
