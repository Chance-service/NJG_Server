package com.guaji.game.config;

import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * @author 作者 E-mail:Jeremy@163.com
 * @version 创建时间：2019年1月24日 上午11:00:32 类说明 30天登陆签到奖励配置文件
 */
@ConfigManager.XmlResource(file = "xml/SupportCalender30.xml", struct = "map")
public class SupportCalendarCfg extends ConfigBase {

	/**
	 * 配置id
	 */
	@Id
	private final int id;
	/**
	 * 類型 1.normal 2.permium
	 */
	private final int type;
	/**
	 * 月份
	 */
	private final int month;
	/**
	 * 活动期间累计登录天数
	 */
	private final int days;
	/**
	 * 奖励物品
	 */
	private final String awards;


	public SupportCalendarCfg() {
		month = 0;
		id = 0;
		type =0;
		days = 0;
		awards = "";

	}

	public int getId() {
		return id;
	}
	
	public int geType() {
		return type;
	}

	public int getDays() {
		return days;
	}

	public String getAwards() {
		return awards;
	}

	public int getMonth() {
		return month;
	}


	/**
	 * @param month 月份
	 * @param day 第几天
	 * @return
	 */
	public static SupportCalendarCfg getLoginSignedCfg(int type ,int month, int day) {

		Map<Object, SupportCalendarCfg> rewardCfgs = ConfigManager.getInstance().getConfigMap(SupportCalendarCfg.class);

		for (SupportCalendarCfg itemCfg : rewardCfgs.values()) {
			if ( itemCfg.geType() == type && itemCfg.getMonth() == month && day == itemCfg.getDays())
				return itemCfg;
		}
		return null;
	}

	@Override
	protected boolean assemble() {
		// TODO Auto-generated method stub
		return super.assemble();
	}

}
