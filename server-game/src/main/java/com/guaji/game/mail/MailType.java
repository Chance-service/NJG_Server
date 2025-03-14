package com.guaji.game.mail;

import org.guaji.os.MyException;

import com.guaji.game.protocol.Mail;

/**
 * 邮件类型
 */
public enum MailType {

	/**
	 * 普通类型
	 */
	MAIL_TYPE_GENERAL(Mail.MailType.Normal_VALUE, PlayerBaseMail.class),
	/**
	 * 带奖励的类型
	 */
	MAIL_TYPE_REWARD(Mail.MailType.Reward_VALUE, PlayerItemMail.class),
	/**
	 * 战报类型
	 */
	MAIL_TYPE_BATTLE_REPORT(Mail.MailType.Battle_VALUE, PlayerBaseMail.class), 
	/**
	 * 邮箱竞技记录
	 */
	MAIL_TYPE_ARENA(Mail.MailType.ARENA_VALUE, PlayerBaseMail.class), 
	/**
	 * 竞技场竞技记录
	 */
	MAIL_TYPE_ARENA_ALL(Mail.MailType.ARENA_ALL_VALUE, PlayerBaseMail.class);
	
	/**
	 * 邮件类型
	 */
	private int type;

	/**
	 * 邮件处理对象
	 */
	private Class<? extends PlayerBaseMail> typeClass;

	private MailType(int type, Class<? extends PlayerBaseMail> typeClass) {
		this.type = type;
		this.typeClass = typeClass;
	}

	public int getType() {
		return type;
	}

	public Class<? extends PlayerBaseMail> getTypeClass() {

		return typeClass;
	}

	/**
	 * 构建邮件对象
	 * 
	 * @param type
	 * @return
	 */
	public static PlayerBaseMail getInstanceByType(int type) {
		for (MailType mailType : values()) {
			if (mailType.type == type) {

				try {
					return mailType.getTypeClass().newInstance();
				} catch (Exception e) {
					MyException.catchException(e);
				}
			}
		}
		return null;
	}
}
