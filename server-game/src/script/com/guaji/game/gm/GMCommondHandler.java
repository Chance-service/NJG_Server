package com.guaji.game.gm;

import java.lang.reflect.Method;

import org.guaji.script.GuaJiScript;
import org.guaji.script.GuaJiScriptManager;

import com.guaji.game.item.AwardItems;
import com.guaji.game.manager.MailManager;
import com.guaji.game.protocol.Mail;
import com.sun.net.httpserver.HttpExchange;

import net.sf.json.JSONObject;

/**
 * gm命令
 * 
 * @author ManGao
 * 
 */
public class GMCommondHandler extends GuaJiScript {

	@Override
	public void action(String params, HttpExchange httpExchange) {

		System.out.println("CMCommondHandler...params=" + params);
		String[] parameters = params.split(",");
		String methodName = parameters[0];
		String[] args = new String[parameters.length - 1];
		System.arraycopy(parameters, 1, args, 0, args.length);

		Method[] methods = GMCommondHandler.class.getDeclaredMethods();
		for (Method method : methods) {
			String name = method.getName();
			if (name.equals(methodName)) {
				try {
					method.invoke(null, httpExchange, args);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		JSONObject json = new JSONObject();
		json.put("status", "1");
		json.put("msg", "params=" + params);
		GuaJiScriptManager.sendResponse(httpExchange, json.toString());
	}

	public static void reward(HttpExchange htt, String... args) {

		AwardItems items = AwardItems.valueOf(args[1]);
		MailManager.createMail(Integer.valueOf(args[0]),
				Mail.MailType.Reward_VALUE, 0, "gm reward", items);
	}

}
