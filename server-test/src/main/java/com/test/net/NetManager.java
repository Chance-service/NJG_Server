package com.test.net;

import org.apache.mina.core.service.IoConnector;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.test.codec.Decoder;
import com.test.codec.Encoder;
import com.test.iohandler.TestGuaJiIoHandler;

public class NetManager {
	/**
	 *
	 */
	private static IoConnector connector;
	
	static{
		if (connector == null) {
			connector = new NioSocketConnector();
			//
			OrderedThreadPoolExecutor executor = new OrderedThreadPoolExecutor(Runtime.getRuntime().availableProcessors() + 1);
			connector.getFilterChain().addLast("threadPool", new ExecutorFilter(executor));

			connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(Encoder.class, Decoder.class));
			connector.setHandler(new TestGuaJiIoHandler());
		}
	}
	
	/**
	 *
	 * 
	 * @return
	 */
	public static IoConnector getConnector() {
//		IoConnector connector = new NioSocketConnector();
//
//		OrderedThreadPoolExecutor executor = new OrderedThreadPoolExecutor(Runtime.getRuntime().availableProcessors() + 1);
//		connector.getFilterChain().addLast("threadPool", new ExecutorFilter(executor));
//
//		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(Encoder.class, Decoder.class));
//		connector.setHandler(new ClientIoHandler());
		return connector;
	}
	
}
