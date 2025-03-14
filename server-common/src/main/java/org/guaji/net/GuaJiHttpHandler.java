package org.guaji.net;

import java.io.IOException;
import java.util.Map;

import org.guaji.os.MyException;
import org.guaji.os.OSOperator;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class GuaJiHttpHandler implements HttpHandler {
	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		try {
			Map<String, String> httpParams = OSOperator.parseHttpParam(httpExchange);
			if (httpParams.containsKey("token")) {
				
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}
	}
}
