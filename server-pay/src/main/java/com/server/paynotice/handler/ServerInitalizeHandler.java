package com.server.paynotice.handler;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class ServerInitalizeHandler extends ChannelInitializer<SocketChannel> {

	private static Logger logger = Logger.getLogger(ServerInitalizeHandler.class);

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// TODO Auto-generated method stub
		ChannelPipeline cp = ch.pipeline();

		// ChannelHandler idleHandler = new ChannelIdleStateHandler(10, 10, 10);
		ChannelHandler decode = new HttpRequestDecoder();
		ChannelHandler encode = new HttpResponseEncoder();
		ChannelHandler aggregator = new HttpObjectAggregator(1024*1024*64);

		ChannelHandler handler = new ServerServiceHandler();
		// cp.addLast(idleHandler);
		cp.addLast(decode);
		cp.addLast(encode);
		cp.addLast(aggregator);
		cp.addLast(handler);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.channel().close();
		logger.error("", cause);
	}
}
