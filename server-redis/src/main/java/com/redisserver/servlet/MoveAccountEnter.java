package com.redisserver.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MoveAccountEnter extends HttpServlet
{
	private static final long serialVersionUID = 424894698022935105L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
		
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// 取数据
		String password = request.getParameter("password");
		String accode = request.getParameter("accode");
		String dvid = request.getParameter("dvid");
		// 数据合法性判断
		if (null == accode || "".equals(accode) || null == dvid || "".equals(dvid) || null == password || "".equals(password) ||password.length()>32) 
		{
			responseClient(response,"0","1");

			return;
		}
		
		//待移行账号是否存在
		if(!AccountDataCenter.getInstance().checkAccodeInAccodeKeyTable(accode))
		{
			responseClient(response,"0","2");
			return;
		}
		//待移行账号安全密码是否存在
		if(!password.equals(AccountDataCenter.getInstance().getPwdFromAccodeKeyTable(accode)))
		{
			responseClient(response,"0","3");
			return;
		} 
		// 登录过该游戏的设备账号
		if( AccountDataCenter.getInstance().checkDvidInDvidKeyTable(dvid) && AccountDataCenter.getInstance().checkAccodeInAccodeKeyTable(accode))
		{
			//该设备移行账号不为空   且  该设备移行账号不等于目标账号
			if(!AccountDataCenter.getInstance().getMvcodeFromDvidKeyTable(dvid).equals("") && !AccountDataCenter.getInstance().getMvcodeFromDvidKeyTable(dvid).equals(accode))
			{
				responseClient(response,"0","4");

				return;
			}
			
			AccountDataCenter.getInstance().modifyMvcodeDvid(accode, dvid);
			//设置移行码
			//AccountDataCenter.getInstance().modifyMvcodeDvidKeyTable(accode, dvid);
			

		}
		else //没有登录过的手机设备
		{		
			
			//addAccountInfo
			if(!AccountDataCenter.getInstance().addAccountInfo(accode, "", "", dvid, "",accode))
			{
				responseClient(response,"1","5");//移行失败
			}
			//AccountDataCenter.getInstance().addNewAccountInfo(accode, "", "", dvid, "");
			//AccountDataCenter.getInstance().modifyMvcodeDvidKeyTable(accode, dvid);

		}
		
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
