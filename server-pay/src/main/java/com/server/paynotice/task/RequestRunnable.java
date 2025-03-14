package com.server.paynotice.task;

import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.server.paynotice.pojo.GenResponse;
import com.server.paynotice.service.IRequestServcie;
import com.server.paynotice.service.RequestDispatcher;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class RequestRunnable implements Runnable {
	private static Logger logger = Logger.getLogger(RequestRunnable.class);
	private String uri;
	private Map<String, String> param;
	private Channel channel;

	public RequestRunnable(String uri, Map<String, String> param, Channel channel) {
		// TODO Auto-generated constructor stub
		this.uri = uri;
		this.param = param;
		this.channel = channel;
	}

	//@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		IRequestServcie service = RequestDispatcher.valueOf(uri);
		if (service == null) {
			// 未定义的uri
			GenResponse content = new GenResponse();
			content.setStatus(-400);
			content.setMsg("internal server erro");
			Gson gson = new Gson();
			String json = gson.toJson(content);
			ByteBuf buff = Unpooled.wrappedBuffer(json.getBytes());
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, buff);
			response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=utf-8");
			response.headers().set(HttpHeaderNames.CONTENT_LENGTH, buff.readableBytes());
			channel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
			logger.error("undefine uri [ "+uri+" ]");
			return;
		}
		if (service.verify(param, channel)) {
			service.request(param, channel);
		}
		long endTime = System.currentTimeMillis();
		long time = endTime-startTime;
		logger.info("uri=" + uri + "|times="+time);
	}

}
