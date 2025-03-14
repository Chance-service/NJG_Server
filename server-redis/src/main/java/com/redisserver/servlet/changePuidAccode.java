package com.redisserver.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class changePuidAccode extends HttpServlet
{
	private static final long serialVersionUID = 424894698022935105L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

/**
 * 没有任何绑定进行数据移行，或者跨平台之用，设备上次登录的accode的变更,移行步骤为先执行此请求，然后直接法登录就好
 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// 取数据
		String accode = request.getParameter("accode");
		String dvid = request.getParameter("dvid");
		
		// 数据合法性判断
		if (null == accode || "".equals(accode) || null == dvid || "".equals(dvid)) 
		{
			return;
		}
		
		String accodeUp = accode.trim().toUpperCase();
		
		if(!AccountDataCenter.getInstance().checkAccodeInAccodeKeyTable(accodeUp))
		{
			responseClient(response,"0","1");

			return;
		}
		
		AccountDataCenter.getInstance().modifyDvidKeyTable(accodeUp, dvid);
		
		responseClient(response,"1","0");


	}
	
	/**
	 * 反馈客户端
	 */
	private void responseClient(HttpServletResponse response,String successFlag,String reason)throws ServletException, IOException
	{
		PrintWriter pw = response.getWriter();
		StringBuilder sb = new StringBuilder();
		
		sb.append(successFlag);
		sb.append("|");
		sb.append(reason);
		
		pw.print(sb.toString());

		pw.flush();
		pw.close();
		
		return;
	}

}
