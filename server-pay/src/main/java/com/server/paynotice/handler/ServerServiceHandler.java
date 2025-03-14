package com.server.paynotice.handler;

import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.server.paynotice.pojo.GenResponse;
import com.server.paynotice.task.ReqeustExecuter;
import com.server.paynotice.task.RequestRunnable;
import com.server.paynotice.util.KEYS;
import com.sun.jndi.toolkit.url.Uri;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class ServerServiceHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private static Logger logger = Logger.getLogger(ServerServiceHandler.class);
	private HttpRequest request;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelActive(ctx);

	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		// TODO Auto-generated method stub
		HttpContent content = null;
		ByteBuf b = null;

		if (!request.decoderResult().isSuccess()) {
			sendError(ctx, BAD_REQUEST);
			return;
		}

		String uri = request.uri();
		logger.debug("recv uri------" + uri);
		if (uri.equals("/favicon.ico") || uri.equals("/")) {
			return;
		}

		if (request.method() == GET) {
			String paramStr = URLDecoder.decode(uri, "UTF-8");
			String[] params = paramStr.split("\\?");
			if (params.length < 2) {
				sendError(ctx, BAD_REQUEST);
				return;
			}
			String path = params[0];
			String param = params[1];
			executeRequest(ctx, path, param);
		} else {

			b = request.content();
			int readable = b.readableBytes();
			if (readable == 0) {
				sendError(ctx, BAD_REQUEST);
				return;
			}
			byte[] bs = new byte[readable];
			b.readBytes(bs);
			String param = new String(bs, "UTF-8");
			logger.debug("HttpContent：----" + param);
			param = URLDecoder.decode(param, "UTF-8");
			executeRequest(ctx, uri, param);
		}
		
	
	}

	/**
	 * 执行具体的请求
	 * 
	 * @param ctx
	 * @param uri
	 * @param param
	 */
	private void executeRequest(ChannelHandlerContext ctx, String uri, String param) {
		LinkedHashMap<String, String> params = parserParam(param);
		Gson gson = new Gson();
		String paramJson = gson.toJson(params,LinkedHashMap.class);
		// 转发给业务处理线程
		logger.info("uri=" + uri + "   params=" + paramJson);
		Runnable runnable = new RequestRunnable(uri, params, ctx.channel());
		ReqeustExecuter.submitTask(runnable);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		cause.printStackTrace();
		GenResponse content = new GenResponse();
		content.setStatus(-500);
		content.setMsg("inernal server erro");
		Gson gson = new Gson();
		String json = gson.toJson(content);
		ByteBuf buff = Unpooled.wrappedBuffer(json.getBytes());
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
				HttpResponseStatus.INTERNAL_SERVER_ERROR, buff);
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=utf-8");
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH, buff.readableBytes());
		ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		logger.error("", cause);
	}

	public LinkedHashMap<String, String> parserParam(String param) {
		LinkedHashMap<String, String> paramMap = new LinkedHashMap<String, String>();
		String[] params = param.split("&");
		if (params.length != 0) {
			for (String p : params) {
				String key;
				String value;
				String[] parry = p.split("=");
				if (parry.length == 0) {
					continue;
				}

				if (parry.length == 1) {
					key = parry[0];
					value = "";
				} else {
					int firstIndex = p.indexOf("=");
					key = p.substring(0, firstIndex);
					value = p.substring(firstIndex + 1).replaceAll(" ", "+");//客户端加号传服务器变加号
				}
				/*
				 * if (parry.length == 1) { key = parry[0]; value = ""; } else if (parry.length
				 * == 2) { key = parry[0]; value = parry[1]; } else { int
				 * firstIndex=p.indexOf("="); key = p.substring(0,firstIndex); value =
				 * p.substring(firstIndex); }
				 */
				paramMap.put(key, value);
			}
		}
		return paramMap;
	}

	private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status,
				Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
}
