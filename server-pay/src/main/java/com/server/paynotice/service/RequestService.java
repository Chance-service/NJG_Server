package com.server.paynotice.service;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.Gson;
import com.server.paynotice.common.Settings;
import com.server.paynotice.pojo.GenResponse;
import com.server.paynotice.util.Md5Util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public abstract class RequestService implements IRequestServcie {
	/**
	 * 回应
	 * 
	 * @param content
	 * @param channel
	 */
	public void response(Object content, Channel channel) {
		Gson gson = new Gson();
		String json = gson.toJson(content);
		ByteBuf buff = Unpooled.wrappedBuffer(json.getBytes());
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buff);
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=utf-8");
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH, buff.readableBytes());
		channel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	/**
	 * 回应
	 * 
	 * @param content
	 * @param channel
	 */
	public void response(String content, Channel channel) {
		try {
			ByteBuf buff = Unpooled.wrappedBuffer(content.getBytes("UTF-8"));
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buff);
			response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=utf-8");
			response.headers().set(HttpHeaderNames.CONTENT_LENGTH, buff.readableBytes());
			channel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	//@Override
	public boolean verify(Map<String, String> param, Channel channel) {
		if(Settings.debug>0)
		{
			return true;
		}
		//验证参数是否合法
		StringBuffer buffer = new StringBuffer();
		buffer.append(Settings.security);
		LinkedHashMap<String, String> tree = new LinkedHashMap<String,String>();
		tree.putAll(param);
		Iterator<String> iterator = param.keySet().iterator();
		String sign = "";
		while(iterator.hasNext())
		{
			String key = iterator.next();
			String value = param.get(key).replaceAll(" ", "+");
			
			if(!key.equals("sign"))
			{
				buffer.append(key+"="+value);
			}else
			{
				sign = value;
			}
		}
		byte[] md5Bytes = Md5Util.getMd5Bytes(buffer.toString());
		String md5 = Md5Util.md5ToHexString(md5Bytes);
		if(sign.equals(md5))
		{
			return true;
		}
		GenResponse reply = new GenResponse();
		reply.setStatus(-9);
		reply.setMsg("wrong signature");
		response(reply, channel);
		return false;
	}

	//@Override
	public abstract void request(Map<String, String> param, Channel channel);
}
