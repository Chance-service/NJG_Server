package com.redisserver.servlet;

import java.io.IOException;
import java.io.PrintWriter;

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
public class FaceBookBindingServlet extends HttpServlet {

	private static final long serialVersionUID = 424894698022935105L;

//	private final Logger logger = Logger.getLogger(FaceBookBindingRequest.class.getName());

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// 取数据
		String faceBookId = request.getParameter("faceBookId");
		String puid = request.getParameter("puid");
		// 数据合法性判断
		if (null == faceBookId || "".equals(faceBookId) || null == puid || "".equals(puid)) {
			return;
		}
		// 获取redis链接
		if (null == ServiceContext.jedisPool) {
			return;
		}

		PrintWriter pw = response.getWriter();
		synchronized (ServiceContext.jedisPool) {
			try {
				Jedis jedis = ServiceContext.jedisPool.getResource();
				if (null == jedis) {
					return;
				}
				// 有数据,则校验puid是否相同
				String jpuid = jedis.get(faceBookId);
				if (null != jpuid) {
					if (puid.equals(jpuid)) {
						pw.print(true);
					} else {
						pw.print(false);
					}
				} else {
					jedis.set(faceBookId, puid);
					pw.print(true);
				}
				if (jedis != null && ServiceContext.jedisPool !=null) {
					ServiceContext.jedisPool.returnResource(jedis);
				}
			} catch (Exception e) {
				pw.print(false);
			}
		}
		pw.flush();
		pw.close();
	}

}
