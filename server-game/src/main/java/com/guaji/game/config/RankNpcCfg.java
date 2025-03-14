package com.guaji.game.config;

import java.util.ArrayList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;
import org.guaji.os.MyException;

/**
 * 竞技场机器人配置
 * 
 */
@ConfigManager.XmlResource(file = "xml/rankNpc.xml", struct = "map")
public class RankNpcCfg extends ConfigBase {
	/**
	 * 配置id
	 */
	@Id
	protected final int id;
	/**
	 * 规则适用的最低排名
	 */
	protected final int minRank;
	/**
	 * 随机怪物Id集合
	 */
	protected final String monsterIds;

	/**
	 * 解析后的怪物Id列表
	 */
	protected List<Integer> monsterIdList;

	public RankNpcCfg() {
		id = 0;
		minRank = 0;
		monsterIds = "";

		monsterIdList = new ArrayList<Integer>();
	}

	public int getId() {
		return id;
	}

	public int getMinRank() {
		return minRank;
	}

	public String getMonsterIds() {
		return monsterIds;
	}

	@Override
	protected boolean assemble() {
		monsterIdList.clear();
		
		if ("".equals(monsterIds)) {
			return false;
		}
		
		String[] ids = monsterIds.split(",");
		for (String id : ids) {
//			if(monsterIdList.contains(Integer.valueOf(id))){
//				String tmp = "same monsterId in one rankNpc.xml's cfg, cfgid : " + this.id + ", monsterId: " + id ;
//				throw new RuntimeException(tmp);
//			}
			monsterIdList.add(Integer.valueOf(id));
		}
		
		if (monsterIdList.size() == 0) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean checkValid() {
		for(int monsterId : monsterIdList){
			if (monsterId > 0) {
				NewMonsterCfg cfg = ConfigManager.getInstance().getConfigByKey(NewMonsterCfg.class, monsterId);
				if(cfg == null){
					throw new RuntimeException("Arena rankNpc's monsterId is not in monsters.xml.");
				}
			}
		}
		
//		if(getMonsterIdList().size() < SysBasicCfg.getInstance().getPageDefenderQty()){
//			throw new RuntimeException("RankNpcCfg.MonsterIdList's size < SysBasicCfg.PageDefenderQty.");
//		}		
		return true;
	}
	
	public List<Integer> getMonsterIdList() {
		return monsterIdList;
	}

	/**
	 * 从怪物列表中随机出一个怪物Id
	 * 
	 * @return
	 * @throws MyException
	 */
	public int getOneMonsterId() {
		int high = monsterIdList.size();
		int index = 0;
		try {
			index = GuaJiRand.randInt(0, high - 1);
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return monsterIdList.get(index);
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
