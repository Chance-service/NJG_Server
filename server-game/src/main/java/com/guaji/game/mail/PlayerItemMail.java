package com.guaji.game.mail;

import com.guaji.game.entity.EmailEntity;
import com.guaji.game.item.AwardItems;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.log.BehaviorLogger.Params;
import com.guaji.game.player.Player;
import com.guaji.game.util.TapDBUtil;

/**
 * 奖励类型邮件
 */
public class PlayerItemMail extends PlayerBaseMail {

	/**
	 * 奖励对象
	 */
	private AwardItems mailItems = new AwardItems();

	@Override
	public EmailEntity getMailEntity() {

		return super.getMailEntity();
	}

	@Override
	public void init(EmailEntity mailEntity) {

		super.init(mailEntity);
		if (getReward().length() > 0) {
			mailItems.initByString(getReward());
		}
	}

	/**
	 * 取得奖励内容
	 * 
	 * @return
	 */
	private String getReward() {

		return getMailEntity().getContent();
	}

	/**
	 * 取得邮件奖励
	 * 
	 * @return
	 */
	@Override
	public AwardItems getAwardItems() {

		return mailItems;
	}

	@Override
	public boolean mail(Player player) {

		mailItems.rewardTakeAffectAndPush(player, Action.MAIL_REWARD_TOOLS,1,TapDBUtil.TapDBSource.Mail_Reward,Params.valueOf("MailId",getMailEntity().getMailId()));
		return super.mail(player);
	}
}
