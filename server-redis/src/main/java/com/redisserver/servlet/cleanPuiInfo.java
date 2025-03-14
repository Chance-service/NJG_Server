package com.redisserver.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class cleanPuiInfo extends HttpServlet
{
	private static final long serialVersionUID = 424894698022935105L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
		
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// 取数据
		String accode = request.getParameter("accode");
//		String dvid = request.getParameter("dvid");
		// 数据合法性判断
		if (null == accode || "".equals(accode)) 
		{
			return;
		}
		
		if(!AccountDataCenter.getInstance().checkAccodeInAccodeKeyTable(accode))
		{
			responseClient(response,"0","1");
			return;
		}
				
		AccountDataCenter.getInstance().cleanAccodeKeyTableByAccode(accode);
		
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
		sb.append(reason);
		
		pw.print(sb.toString());

		pw.flush();
		pw.close();
		
		return;
	}
}
