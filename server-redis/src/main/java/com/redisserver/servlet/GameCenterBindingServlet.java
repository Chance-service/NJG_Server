package com.redisserver.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 客户端请求绑定gc
 * 
 * @author Callan
 */
public class GameCenterBindingServlet extends HttpServlet {

	private static final long serialVersionUID = 424894698022935105L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 取数据
		String gameCenterId = request.getParameter("gameCenterId");
		String accode = request.getParameter("accode");
		// 数据合法性判断
		if (null == gameCenterId || "".equals(gameCenterId) || null == accode || "".equals(accode)) {
			return;
		}

		if (!AccountDataCenter.getInstance().checkAccodeInAccodeKeyTable(accode)) {
			responseClient(response, "0", "2");
			return;
		}

		if (AccountDataCenter.getInstance().getGcidFromAccodeKeyTable(accode) == null) {
			// 已存在的accode中，没有找到gcid的字段（没有初始化）
			responseClient(response, "0", "3");
			return;
		}

		if (AccountDataCenter.getInstance().checkGcidInGcidKeyTable(gameCenterId)) {
			// 此gc账号已经绑定过其他puid账号了
			responseClient(response, "0", "1");
			return;
		}

		// redis 中取数据为空
		if (AccountDataCenter.getInstance().getGcidFromAccodeKeyTable(accode).equals("")) {
			// 绑定
			// AccountDataCenter.getInstance().modifyAccodeKeyTable(accode, gameCenterId,
			// null, null,null);
			// AccountDataCenter.getInstance().addToGcidKeyTable(accode,
			// gameCenterId);//必定是新的
			if (AccountDataCenter.getInstance().GameCenterBind(gameCenterId, accode))
				responseClient(response, "1", "0");
			else
				responseClient(response, "0", "5");//很可能是因为操作redis 导致的失败

			// AccountDataCenter.getInstance().GameCenterBind(gameCenterId, accode);

		} else {
			// 该puid已经绑定了gc账号
			responseClient(response, "0", "4");
			return;
		}

	}

	/**
	 * 反馈客户端
	 */
	private void responseClient(HttpServletResponse response, String successFlag, String reason)
			throws ServletException, IOException {
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
