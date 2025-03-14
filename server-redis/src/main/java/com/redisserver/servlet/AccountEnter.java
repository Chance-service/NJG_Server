package com.redisserver.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.guaji.cryption.Md5;

import com.google.gson.Gson;
import com.redisserver.JediseConfig;
import com.sun.mail.handlers.message_rfc822;
import java.util.LinkedHashMap;
import com.sun.xml.internal.xsom.impl.scd.Iterators.Map;

/**
 * 客户端请求平台登录
 * 
 * @author Callan
 */
public class AccountEnter extends HttpServlet {

	private static final long serialVersionUID = 424894698022935105L;

	private final Logger logger = Logger.getLogger(AccountEnter.class.getName());

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 取数据
		String dvid = request.getParameter("dvid");// 设备ID
		String platform = request.getParameter("platform");// 系统
		String pid = request.getParameter("pid");// 合作方平台
		
	
		logger.info("dvid:" + dvid + "--------platform:" + platform + "--------pid:" + pid);

		// 数据合法性判断
		if (null == dvid || "".equals(dvid) || pid == null || platform == null) {
			responseClient(response, "0", "1", "", "", "", "");
			return;
		}

		String accode = null;
		// 优先处理移行的逻辑
		if (AccountDataCenter.getInstance().checkDvidInDvidKeyTable(dvid)) {

			logger.debug("dvid:" + dvid + "--------have move account");
			// 该设备是做过移行的设备
			String moveCode = AccountDataCenter.getInstance().getMvcodeFromDvidKeyTable(dvid);
			String gcid = AccountDataCenter.getInstance().getGcidFromAccodeKeyTable(moveCode);
			String gpid = AccountDataCenter.getInstance().getGpidFromAccodeKeyTable(moveCode);

			if (!moveCode.equals("")) {
				// 这里并没有做上次登录次设备的code处理，为了让玩家有机会找到以前的号
				responseClient(response, "1", "0", "0", gcid, gpid, moveCode);
				return;
			}
		}
		/*
		if (!("".equals(pid)))// 有平台账号
		{
			if (platform.equals("ios")) {
				if (AccountDataCenter.getInstance().checkGcidInGcidKeyTable(pid))// 绑定过
				{

					accode = AccountDataCenter.getInstance().getAccodeFromGcidKeyTable(pid);

					if (accode == null) {
						responseClient(response, "0", "3", "", "", "", "");

						// 报错
						return;
					}

					// 有可能和上次登陆手机不一致
					AccountDataCenter.getInstance().modifyAccodeKeyTable(accode, pid, null, dvid, null);// 密码默认为空

					// 若该设备登陆过其他号 修改 acccode 为当前号
					if (AccountDataCenter.getInstance().checkDvidInDvidKeyTable(dvid))// 此设备已经存在了一个上次登录puid
					{
						AccountDataCenter.getInstance().modifyDvidKeyTable(accode, dvid);
					} else {
						// 合成一步
						AccountDataCenter.getInstance().addToDvidKeyTable(accode, dvid);

						AccountDataCenter.getInstance().addMvcodeToDvidKeyTable("", dvid);

					}

				} else// 没有绑定
				{
					if (AccountDataCenter.getInstance().checkDvidInDvidKeyTable(dvid))// 上次此设备有登录的账号
					{
						accode = AccountDataCenter.getInstance().getAccodeFromDvidKeyTable(dvid);// 此设备上一次登录的accode
					}
				}

			} else {
				if (platform.equals("android")) {

					if (AccountDataCenter.getInstance().checkGpidInGpidKeyTable(pid))// 绑定过
					{

						accode = AccountDataCenter.getInstance().getAccodeFromGpidKeyTable(pid);

						if (accode == null) {
							responseClient(response, "0", "3", "", "", "", "");

							// 报错
							return;
						}

						// 本次登录设备变更
						AccountDataCenter.getInstance().modifyAccodeKeyTable(accode, null, pid, dvid, null);

						if (AccountDataCenter.getInstance().checkDvidInDvidKeyTable(dvid))// 此设备已经存在了一个上次登录puid
						{
							AccountDataCenter.getInstance().modifyDvidKeyTable(accode, dvid);
						} else {
							AccountDataCenter.getInstance().addToDvidKeyTable(accode, dvid);
							AccountDataCenter.getInstance().addMvcodeToDvidKeyTable("", dvid);

						}

					} else// 没有绑定
					{
						if (AccountDataCenter.getInstance().checkDvidInDvidKeyTable(dvid))// 上次此设备没有登录的账号
						{
							accode = AccountDataCenter.getInstance().getAccodeFromDvidKeyTable(dvid);

						}
					}

				} else// 其他平台
				{

					if (AccountDataCenter.getInstance().checkDvidInDvidKeyTable(dvid))// 上次此设备没有登录的账号
					{
						accode = AccountDataCenter.getInstance().getAccodeFromDvidKeyTable(dvid);
					}

				}
			}

		} else// 无平台账号
		{
			// 此设备上登陆过账号已经生成过设备记录
			if (AccountDataCenter.getInstance().checkDvidInDvidKeyTable(dvid))// 上次此设备有登录的账号
			{
				accode = AccountDataCenter.getInstance().getAccodeFromDvidKeyTable(dvid);

			}

		}
		 */
		
		if (!("".equals(pid)))// 有平台账号
		{
			if (platform.equals("ios")||platform.equals("android"))
			{
				if(!AccountDataCenter.getInstance().checkGcidInGcidKeyTable(pid)&&!AccountDataCenter.getInstance().checkGpidInGpidKeyTable(pid))
				{
					if (AccountDataCenter.getInstance().checkDvidInDvidKeyTable(dvid))// 上次此设备没有登录的账号
					{
						accode = AccountDataCenter.getInstance().getAccodeFromDvidKeyTable(dvid);
					}
				}else
				{
					if(platform.equals("ios"))
						accode = AccountDataCenter.getInstance().getAccodeFromGcidKeyTable(pid);
					else
						accode = AccountDataCenter.getInstance().getAccodeFromGpidKeyTable(pid);
					
					if (accode == null) {
						responseClient(response, "0", "3", "", "", "", "");

						// 报错
						return;
					}

					// 本次登录设备变更
					AccountDataCenter.getInstance().modifyAccodeKeyTable(accode, null, pid, dvid, null);

					if (AccountDataCenter.getInstance().checkDvidInDvidKeyTable(dvid))// 此设备已经存在了一个上次登录puid
					{
						//AccountDataCenter.getInstance().modifyDvidKeyTable(accode, dvid);
						AccountDataCenter.getInstance().modifyDvidKeyAccode(accode, dvid);
					} else {
						//AccountDataCenter.getInstance().addToDvidKeyTable(accode, dvid);
						//AccountDataCenter.getInstance().addMvcodeToDvidKeyTable("", dvid);
						AccountDataCenter.getInstance().addDvdInfoToDvidKeyTable(accode,"",dvid);

					}		
				}
			}
			else
			{
				if (AccountDataCenter.getInstance().checkDvidInDvidKeyTable(dvid))// 上次此设备没有登录的账号
				{
					accode = AccountDataCenter.getInstance().getAccodeFromDvidKeyTable(dvid);
				}
			}
		}else
		{
			// 此设备上登陆过账号已经生成过设备记录
			if (AccountDataCenter.getInstance().checkDvidInDvidKeyTable(dvid))// 上次此设备有登录的账号
			{
				accode = AccountDataCenter.getInstance().getAccodeFromDvidKeyTable(dvid);
			}
		}
		if (accode == null) {
			accode = Md5.makeMD5(dvid).substring(8, 20).toUpperCase();
			//AccountDataCenter.getInstance().addNewAccountInfo(accode, "", "", dvid, "");
			if(!AccountDataCenter.getInstance().addAccountInfo(accode, "", "", dvid, "",""))
			{
				responseClient(response, "0", "4", "0", "", "", "");
				return;
			}
		}
		/*
		// 删除冗余的设备信息对应accode信息（原因：使用cleanPuid的时候，没有对该puid所有使用过的dvid的上次登录进行删除。。。。,或者其他反查表中有不存在accode信息）
		if (!AccountDataCenter.getInstance().checkAccodeInAccodeKeyTable(accode)) {
			AccountDataCenter.getInstance().deleteDvidKeyTable(dvid);
			AccountDataCenter.getInstance().deleteGcidKeyTable(pid);
			AccountDataCenter.getInstance().deleteGpidKeyTable(pid);
			responseClient(response, "0", "4", "0", "", "", "");
			return;

		}
		 */
		String gcid = AccountDataCenter.getInstance().getGcidFromAccodeKeyTable(accode);
		String gpid = AccountDataCenter.getInstance().getGpidFromAccodeKeyTable(accode);

		responseClient(response, "1", "0", "0", gcid, gpid, accode);

	}

	/**
	 * 反馈客户端
	 */
	private void responseClient(HttpServletResponse response, String successFlag, String reason, String newFlag,
			String gcid, String gpid, String accode) throws ServletException, IOException {
		PrintWriter pw = response.getWriter();
		StringBuilder sb = new StringBuilder();

//		Gson gson = new Gson();

//		LinkedHashMap<String, String> message = new LinkedHashMap<String,String>();
//		message.put("successFlag", successFlag);
		sb.append(successFlag);
		sb.append("|");
		sb.append(reason);
		sb.append("|");
		sb.append(newFlag);
		sb.append("|");
		sb.append(gcid);
		sb.append("|");
		sb.append(gpid);
		sb.append("|");
		sb.append(accode);

		logger.info("response to client..." + sb.toString());

		pw.print(sb.toString());

		pw.flush();
		pw.close();

		return;
	}

}
