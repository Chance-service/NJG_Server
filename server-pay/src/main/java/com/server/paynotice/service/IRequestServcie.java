package com.server.paynotice.service;

import java.util.Map;

import io.netty.channel.Channel;

public interface IRequestServcie {
	public boolean verify(Map<String, String> param,Channel channel);
	public void request(Map<String, String> param,Channel channel);
}
