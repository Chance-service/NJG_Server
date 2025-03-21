package org.guaji.util.services;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.guaji.app.App;
import org.guaji.thread.GuaJiTask;
import org.guaji.thread.GuaJiThreadPool;

/**
 * 邮件服务
 */
public class EmailService {
	/**
	 * 服务信息
	 */
	private String emailHost = "";
	private int emailPort = 0;
	private String emailUser = "";
	private String emailPwd = "";
	/**
	 * 执行线程池
	 */
	protected GuaJiThreadPool executor;
	
	/**
	 * 实例对象
	 */
	private static EmailService instance = null;

	/**
	 * 获取全局实例对象
	 * 
	 * @return
	 */
	public static EmailService getInstance() {
		if (instance == null) {
			instance = new EmailService();
		}
		return instance;
	}

	/**
	 * 构造函数
	 */
	private EmailService() {
	}

	/**
	 * 初始化服务
	 * 
	 * @return
	 */
	public boolean install(String host, int port, String user, String pwd) {
		this.emailHost = host;
		this.emailPort = port;
		this.emailUser = user;
		this.emailPwd = pwd;
		return true;
	}

	/**
	 * 检测是否有效
	 * @return
	 */
	public boolean checkValid() {
		return emailHost.length() > 0 && emailUser.length() > 0;
	}
	
	/**
	 * 发送邮件
	 * 
	 * @param title
	 * @param content
	 * @param receivers
	 */
	public synchronized void sendEmail(String title, String content, List<String> receivers) {
		if (checkValid()) {
			if (App.getInstance() != null) {
				App.getInstance().postCommonTask(new EmailTask(title, content, receivers));
			} else {
				if (executor == null) {
					executor = new GuaJiThreadPool("EmailService");
					executor.initPool(2);
					executor.start();
				}
				executor.addTask(new EmailTask(title, content, receivers));
			}
		}
	}

	/**
	 * 邮件任务
	 */
	class EmailTask extends GuaJiTask {
		// 邮件标题
		private String title;
		// 邮件内容
		private String content;
		// 邮件收件人
		private List<String> receivers;

		EmailTask() {
			this.title = "";
			this.content = "";
			this.receivers = new LinkedList<String>();
		}
		
		EmailTask(String title, String content, List<String> receivers) {
			this.title = title;
			this.content = content;
			this.receivers = new LinkedList<String>();
			this.receivers.addAll(receivers);
		}

		@Override
		protected int run() {
			try {
				Email email = new SimpleEmail();
				email.setHostName(emailHost);
				email.setSmtpPort(emailPort);
				email.setAuthentication(emailUser, emailPwd);
				email.setCharset("UTF-8");
				email.setFrom(emailUser);
				for (String receiver : receivers) {
					email.addTo(receiver);
				}
				email.setSubject(title);
				email.setMsg(content);
				email.send();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}
	}
}
