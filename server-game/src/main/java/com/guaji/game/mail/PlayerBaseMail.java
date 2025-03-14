package com.guaji.game.mail;

import com.guaji.game.entity.EmailEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.player.Player;

/**
 * 角色邮件基本基类
 * 
 */
public class PlayerBaseMail {

	/**
	 * 邮件存储entity
	 */
	private EmailEntity mailEntity;

	public PlayerBaseMail() {

	}

	public PlayerBaseMail(EmailEntity entity) {
		this.mailEntity = entity;
	}

	public EmailEntity getMailEntity() {

		return mailEntity;
	}

	/**
	 * 初始化
	 * 
	 * @param mailEntity
	 */
	public void init(EmailEntity mailEntity) {

		this.mailEntity = mailEntity;
	}

	/**
	 * 邮件唯一id
	 * 
	 * @return
	 */
	public int getId() {
		return mailEntity.getId();
	}

	public int getPlayerId() {
		return mailEntity.getPlayerId();
	}

	/**
	 * 邮件类型
	 * 
	 * @return
	 */
	public int getMailType() {

		return mailEntity.getType();
	}

	/**
	 * 邮件标题
	 * 
	 * @return
	 */
	public String getMailTitles() {
		return mailEntity.getTitle();
	}

	public AwardItems getAwardItems() {

		return null;
	}

	/**
	 * 对角色执行邮件的内部操作
	 * 
	 * @return
	 */
	public boolean mail(Player player) {

		return true;
	}

	/**
	 * 邮件内容
	 * @return
	 */
	public String getContent(){
		return mailEntity.getContent();
	}
}
