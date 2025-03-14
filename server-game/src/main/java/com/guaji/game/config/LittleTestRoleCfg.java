package com.guaji.game.config;



import java.util.LinkedList;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiRand;

/**
 * 性奴小學戰隊角色表
 */
@ConfigManager.XmlResource(file = "xml/LTRole.xml", struct = "map")
public class LittleTestRoleCfg extends ConfigBase {
	/**
	 * 角色ID
	 */
	@Id
	private final int Role;
	/**
	 * 血量
	 */
	private final int HP ;
	/**
	 * 攻擊力
	 */
	private final int ATK;
	/**
	 * 速度
	 */
	private final int Speed;
	/**
	 * 角色種類
	 */
	private final int Type;
	/**
	 * 技能ID
	 */
	private final int SkillID;
//	/**
//	 * Boss角專用(親密度)
//	 */
	private final int Intimacy;
	
	private static List <LittleTestRoleCfg> BossList = new LinkedList<>();
	
	public LittleTestRoleCfg() {
		Role = 0;
		HP = 0;
		ATK = 0;
		Speed = 0;
		Type = 0;
		SkillID = 0;
		Intimacy = 0;
	}

	public int getRole() {
		return Role;
	}

	public int getHP() {
		return HP;
	}
	
	public int getATK() {
		return ATK;
	}
	public int getSpeed() {
		return Speed;
	}
	public int getType() {
		return Type;
	}
	
	public int getSkillID() {
		return SkillID;
	}

	public int getIntimacy() {
		return Intimacy;
	}
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
		BossList.clear();
		//specialTimesMap.clear();
	}

	@Override
	protected boolean assemble() {
		if (Type == 0) {
			BossList.add(this);
		}
		return true;
	}

	@Override
	protected boolean checkValid() {
		return true;
	}
	
	public static LittleTestRoleCfg RandBoss() {
		if (BossList.size() == 0) {
			return null;
		}else {
			int[] index = GuaJiRand.randomCommon(0, BossList.size()-1 , 1);
			return BossList.get(index[0]);
		}
	}
}
