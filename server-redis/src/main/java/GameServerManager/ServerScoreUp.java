package GameServerManager;


import java.io.IOException;

import java.io.PrintWriter;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServerScoreUp extends HttpServlet{
	private static final long serialVersionUID = 424894698022935105L;
	

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
		
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// 取数据
		String serverId = request.getParameter("serverId");
		String scoreStr = request.getParameter("score");
		// 数据合法性判断
		if (null == serverId || "".equals(serverId) || null == scoreStr || "".equals(scoreStr) ) 
		{
			return;
		}
		
		if(serverId.equals("allClean"))
		{
			GameServerDataCenter.getInstance().cleanAllServerScore();
			responseClient(response,"1","0");

			return;
		}
		
		int score = Integer.valueOf(scoreStr);
		
		GameServerDataCenter.getInstance().modifyServerScore(serverId, score);

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
