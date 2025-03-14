package com.guaji.game.manager;

import com.guaji.game.protocol.Const.NoticeType;

/**
 * 提醒信息
 */
public class NoticeMsg {
	
	/**
	 * 提醒类型
	 */
	private NoticeType noticeType;
	
	/**
	 * 提醒数量
	 */
	private int count;
	/**
	 * 提醒消息参数
	 */
	private String[] params;
	
	/**
	 * 默认构造函数
	 */
	public NoticeMsg() {
	}

	/**
	 * 构造函数
	 * @param noticeType
	 * @param count
	 * @param params
	 */
	public NoticeMsg(NoticeType noticeType,int count, String...params) {
		this.setNoticeType(noticeType);
		this.setCount(count);
		this.setParams(params);
	}

	public NoticeType getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(NoticeType noticeType) {
		this.noticeType = noticeType;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String[] getParams() {
		return params;
	}

	public void setParams(String[] params) {
		this.params = params;
	}
}
