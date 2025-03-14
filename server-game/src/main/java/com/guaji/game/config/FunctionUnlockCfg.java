package com.guaji.game.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;

import com.guaji.game.entity.RoleEntity;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const.FunctionType;

@ConfigManager.XmlResource(file = "xml/function_unlock.xml", struct = "map")
public class FunctionUnlockCfg extends ConfigBase {
	
	/**
	 * 配置id
	 */
	@Id
	private final int id;
	
	/**
	 * 解鎖條件解鎖數值
	 */
	private final String unlocktype;
	
	/**
	 * 解鎖條件解鎖數值
	 */
	private final String unlockvalue;
	/**
	 * 0.未開放 1.關卡 2. 玩家等級 3.忍娘等級 4.忍娘星級 5.VIP等級
	 */
	private List<Integer> typeLsit ;
	/**
	 * 對應type數值
	 */
	private List<Integer> valueList;
	
	
	public FunctionUnlockCfg() {
		this.id = 0;
		this.unlocktype = "";
		this.unlockvalue = "";
		this.typeLsit = new ArrayList<>();
		this.valueList = new ArrayList<>();
	}

	public int getId() {
		return id;
	}
	

	public List<Integer> getTypeLsit() {
		return typeLsit;
	}

	public void setTypeLsit(List<Integer> typeLsit) {
		this.typeLsit = typeLsit;
	}

	public List<Integer> getValueList() {
		return valueList;
	}

	public void setValueList(List<Integer> valueList) {
		this.valueList = valueList;
	}

	public String getUnlocktype() {
		return unlocktype;
	}

	public String getUnlockvalue() {
		return unlockvalue;
	}

	/**
	 * 数据格式转化
	 */
	@Override
	protected boolean assemble() {
		this.typeLsit.clear();
		this.valueList.clear();
        if ((unlocktype != null && unlocktype.length() > 0) &&
        	(unlockvalue != null&& unlockvalue.length() > 0)) {
            String[] types = unlocktype.split(",");
            String[] values = unlockvalue.split(",");
            if (types.length != values.length) {
            	return false;
            }
            
            int idx = 0;
            for (String type : types) {
            	typeLsit.add(Integer.valueOf(type));
            	String value = values[idx];
            	valueList.add(Integer.valueOf(value));
            	idx++;
            }
            
		}
		return true;
	}

	/**
	 * 检测有消息
	 * 
	 * @return
	 */
	@Override
	protected boolean checkValid() {
		return true;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
	
	
	/**
	 * 
	 * @param player
	 * @param roleEntity(忍娘英雄)
	 * @param funId
	 * @return
	 */
	public static boolean checkUnlock(Player player,RoleEntity roleEntity,int funId) {
		
		FunctionUnlockCfg funCfg = ConfigManager.getInstance().getConfigByKey(FunctionUnlockCfg.class, funId);
		
		if (funCfg == null) { // 未定義先通過
			return true;
		}
		
		if ((player == null)&&(roleEntity == null)) {
			return false;
		}
		
		int idx = 0;
		for (int type :funCfg.getTypeLsit()) {
			// 0.未開放 1.關卡 2. 玩家等級 3.忍娘等級 4.忍娘星級 5.VIP等級
			if (type == 0) {
				return false;
			}
			if (player != null) {
				if (type == 1) {
					if (player.getPassMapId() < funCfg.getValueList().get(idx)) {
						return false;
					}
				}
				if (type == 2) {
					if (player.getLevel() < funCfg.getValueList().get(idx)) {
						return false;
					}
				}
				if (type == 5) {
					if (player.getVipLevel() < funCfg.getValueList().get(idx)) {
						return false;
					}
				}
			}
			
			if (roleEntity != null) {
				if (type == 3) {
					if (roleEntity.getLevel() < funCfg.getValueList().get(idx)) {
						return false;
					}
				}
				if (type == 4) {
					if (roleEntity.getStarLevel() < funCfg.getValueList().get(idx)) {
						return false;
					}
				}
			}
			
			idx++;
		}
		
		return true;
	}

	public static boolean checkUnlock(Player player, RoleEntity roleEntity, FunctionType funtype) {
		return checkUnlock(player,roleEntity,funtype.getNumber());
	}
	
	/**
	 * 取出對應funtion 對應限制值
	 * @param funId
	 * @return
	 */
	public static Map<Integer,Integer> getTypeValue(int funId) {
		Map<Integer,Integer> typeValue = new HashMap<>();
		FunctionUnlockCfg funCfg = ConfigManager.getInstance().getConfigByKey(FunctionUnlockCfg.class, funId);
		
		if (funCfg == null) { // 未定義先通過
			return typeValue;
		}
		
		for (int idx = 0 ; idx < funCfg.getTypeLsit().size();idx++) {
			int type = funCfg.getTypeLsit().get(idx);
			if (type != 0) {
				typeValue.put(type,funCfg.getValueList().get(idx));
			}
		}
		
		return typeValue;
	}
}
