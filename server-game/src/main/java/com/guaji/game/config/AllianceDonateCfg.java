package com.guaji.game.config;

import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.item.ItemInfo;

/**
 * 联盟贡献配置
 */
@ConfigManager.XmlResource(file = "xml/allianceDonate.xml", struct = "list")
public class AllianceDonateCfg extends ConfigBase{
	/***id列*/
	@Id
	private final int id;
	/***类型 */
	private final int type;
	/***活力值*/
	private final int activeValue;
	/***消耗*/
	private final String consume;
	private List<ItemInfo> consumeItem;
	/**联盟奖励*/
	private final String allianceAward;
	private List<ItemInfo> allianceAwardItem;
	/**个人奖励*/
	private final String personAward;
	private List<ItemInfo> personAwardItem;
	
	public AllianceDonateCfg(){
		id = 0;
		type = 0;
		activeValue = 0;
		consume="";
		allianceAward="";
		personAward="";
	}
	/**
	 * id
	 * @return
	 */
	public int getId() {
		return id;
	}
	/**
	 * 类型
	 * @return
	 */
	public int getType() {
		return type;
	}
	/**
	 * 活力值
	 * @return
	 */
	public int getActiveValue() {
		return activeValue;
	}
	/**
	 * 消耗
	 * @return
	 */
	public String getConsume() {
		return consume;
	}
	/**
	 * 联盟奖励
	 * @return
	 */
	public String getAllianceAward() {
		return allianceAward;
	}
	/**
	 * 个人奖励
	 * @return
	 */
	public String getPersonAward() {
		return personAward;
	}
	/**
	 * 消耗物品
	 * @return
	 */
	public List<ItemInfo> getConsumeItem() {
		return consumeItem;
	}
	/**
	 * 联盟奖励物品
	 * @return
	 */
	public List<ItemInfo> getAllianceAwardItem() {
		return allianceAwardItem;
	}
	/**
	 * 个人奖励物品
	 * @return
	 */
	public List<ItemInfo> getPersonAwardItem() {
		return personAwardItem;
	}
	
	@Override
	protected boolean assemble() {
		// TODO Auto-generated method stub
		if(consume!=null&&consume.trim().length()>0){
			consumeItem = ItemInfo.valueListOf(consume);
		}
		if(allianceAward!=null&&allianceAward.trim().length()>0){
			allianceAwardItem = ItemInfo.valueListOf(allianceAward);
		}
		if(personAward!=null&&personAward.trim().length()>0){
			personAwardItem = ItemInfo.valueListOf(personAward);
		}
		return super.assemble();
	}
	
}
