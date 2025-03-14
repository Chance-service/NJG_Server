package com.redisserver.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import redis.clients.jedis.Jedis;

import com.redisserver.ServiceContext;

/**
 * 客户端请求FaceBook绑定数据
 * @author Nannan.Gao
 */
public class FaceBookFriendServlet extends HttpServlet {

	private static final long serialVersionUID = 424894698022935105L;

//	private final Logger logger = Logger.getLogger(FaceBookFriendServlet.class.getName());

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String faceBookId = request.getParameter("faceBookId");
		// 数据合法性判断
		if (null == faceBookId || "".equals(faceBookId)) {
			return;
		}
		String ids[] = faceBookId.split(",");
		List<String> keyList = new ArrayList<String>();
		for (String key : ids) {
			keyList.add(key);
		}

		// 获取redis链接
		if (null == ServiceContext.jedisPool) {
			return;
		}
		
		PrintWriter pw = response.getWriter();
		StringBuilder sb = new StringBuilder();
		synchronized (ServiceContext.jedisPool) {
			try {
				Jedis jedis = ServiceContext.jedisPool.getResource();
				if (null == jedis) {
					return;
				}
				// 数据查找(若没有,则添加)
				for (String key : keyList) {
					String value = jedis.get(key);
					if (null != value) {
						sb.append(key);
						sb.append(",");
						sb.append(value);
						sb.append("$");
					}
				}
				if (jedis != null && ServiceContext.jedisPool !=null) {
					ServiceContext.jedisPool.returnResource(jedis);
				}
			} catch (Exception e) {
				pw.print(sb.toString());
				pw.flush();
				pw.close();
		    }
		}
		pw.print(sb.toString());
		pw.flush();
		pw.close();
	}
	
}
