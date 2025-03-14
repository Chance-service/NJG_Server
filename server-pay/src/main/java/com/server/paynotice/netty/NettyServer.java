package com.server.paynotice.netty;

import com.server.paynotice.handler.ServerInitalizeHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
	public static boolean bind(int port) {
		ServerBootstrap sb = new ServerBootstrap();
		NioEventLoopGroup bossGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
		NioEventLoopGroup workGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);
		sb.group(bossGroup, workGroup);
		sb.channel(NioServerSocketChannel.class);
		sb.option(ChannelOption.SO_BACKLOG, 128);
		sb.option(ChannelOption.SO_REUSEADDR, false);
		sb.option(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(false));
		sb.childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(false));
		sb.childOption(ChannelOption.SO_KEEPALIVE, false);
		sb.childOption(ChannelOption.TCP_NODELAY, true);
		sb.childHandler(new ServerInitalizeHandler());
		try {

			ChannelFuture future = sb.bind(port).sync();
			if (future.isSuccess()) {
				System.out.println("服务端启动成功");
				return true;
			} else {
				System.out.println("服务端启动失败");
				future.cause().printStackTrace();
				bossGroup.shutdownGracefully(); // 关闭线程组
				workGroup.shutdownGracefully();
				return false;
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
			
			return false;
		}
	}
}
