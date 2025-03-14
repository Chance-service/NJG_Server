package com.redisserver.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 客户端请求绑定gp
 * @author Callan
 */
public class GooglePlayBindingServlet extends HttpServlet {

	private static final long serialVersionUID = 424894698022935105L;
	

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
		
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// 取数据
		String googlePlayId = request.getParameter("googlePlayId");
		String accode = request.getParameter("accode");
		// 数据合法性判断
		if (null == googlePlayId || "".equals(googlePlayId) || null == accode || "".equals(accode)) 
		{
			return;
		}
		
		if(!AccountDataCenter.getInstance().checkAccodeInAccodeKeyTable(accode))
		{
			responseClient(response,"0","2");
			return;
		}
		
		if(AccountDataCenter.getInstance().getGpidFromAccodeKeyTable(accode) == null)
		{
			//已存在的accode中，但是没有找到gpid的字段（没有初始化）
			responseClient(response,"0","3");
			return;
		}
		
		
		if(AccountDataCenter.getInstance().checkGpidInGpidKeyTable(googlePlayId))
		{
			//此gp账号已经绑定过其他puid账号了
			responseClient(response,"0","1");
			return;
		}

		if(AccountDataCenter.getInstance().getGpidFromAccodeKeyTable(accode).equals(""))
		{
			//绑定
			//AccountDataCenter.getInstance().modifyAccodeKeyTable(accode, null, googlePlayId, null,null);
			//AccountDataCenter.getInstance().addToGpidKeyTable(accode, googlePlayId);
			//事务控制
			if(AccountDataCenter.getInstance().GooglePlayBind(googlePlayId, accode))
				responseClient(response,"1","0");
			else
				responseClient(response,"0","5");
			//AccountDataCenter.getInstance().GooglePlayBind(googlePlayId, accode);
			
		}
		else
		{
			//该puid已经绑定了gp账号
			responseClient(response,"0","4");
			return;
		}

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
