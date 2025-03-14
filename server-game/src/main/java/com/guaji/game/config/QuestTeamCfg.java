package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

/**
 * 任务阶段配置;
 * 
 * @author crazyjohn
 *
 */
@ConfigManager.XmlResource(file = "xml/questTeam.xml", struct = "map")
public class QuestTeamCfg extends ConfigBase {

	@Id
	protected final int id;
	/** 激活等级 */
	protected final int openLevel;
	/** 任务列表*/
	protected final String questListStr;
	
	private List<Integer> questList;

	public QuestTeamCfg() {
		this.id = 0;
		this.openLevel = 0;
		this.questListStr = "";
		this.questList = new ArrayList<Integer>();
	}

	public int getId() {
		return id;
	}

//	public int getOpenLevel() {
//		return openLevel;
//	}
	
	public String getQuestListStr()
	{
		return this.questListStr;
	}

	public List<Integer> getQuestList() {
		return questList;
	}
	
	@Override
	protected boolean assemble() 
	{
		if(this.getQuestListStr() != null && !this.getQuestListStr().equals(""))
		{
			String[] arg = this.getQuestListStr().split(",");
			
			for(String var:arg)
			{
				QuestCfg config = ConfigManager.getInstance().getConfigByKey(QuestCfg.class, Integer.valueOf(var));
				
				if(config == null)
				{
					return false;
				}
				this.questList.add(Integer.valueOf(var));
			}
			
		}
		return true;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
