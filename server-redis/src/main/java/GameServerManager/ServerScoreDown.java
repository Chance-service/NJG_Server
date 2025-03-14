package GameServerManager;


import java.io.IOException;

import java.io.PrintWriter;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.redisserver.servlet.AccountDataCenter;

public class ServerScoreDown extends HttpServlet{
	private static final long serialVersionUID = 424894698022935105L;
	

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
		
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		int totalScore = GameServerDataCenter.getInstance().getAllServerScore();
		responseClient(response,totalScore);

	}
	
	/**
	 * 反馈客户端
	 */
	private void responseClient(HttpServletResponse response,int totalScore)throws ServletException, IOException
	{
		PrintWriter pw = response.getWriter();
		StringBuilder sb = new StringBuilder();

		sb.append(totalScore);
		
		pw.print(sb.toString());

		pw.flush();
		pw.close();
		
		return;
	}


}
