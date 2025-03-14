package com.guaji.game.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.guaji.app.App;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.GuaJiTime;

import com.guaji.game.config.LevelExpCfg;
import com.guaji.game.config.RebirthStageCfg;
import com.guaji.game.config.VipPrivilegeCfg;
import com.guaji.game.entity.PlayerAllianceEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Attribute.Attr;
import com.guaji.game.protocol.Chat.HPAllianceSwitch;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.RankType;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Player.RoleInfo;

/**
 * 游戏帮助类
 */
public class GameUtil {
	
	private static String[] iosPlatform = new String[] { "TBUSR", "PPUSR", "ITOUSR", "KuaiyongUSR", "91" };
	private static String[] andPlatform = new String[] { "91", "uc", "360", "dl", "xm", "wdj", "bddk", "az", "yyh", "js", "oppo", "lenovo", "sg", "hw" };

	// 通过puid获取渠道名
	public static String getPlatformFromPuid(String puid) {
		for (String pf : iosPlatform) {
			if (puid.startsWith(pf)) {
				return pf;
			}
		}

		for (String pf : andPlatform) {
			if (puid.startsWith(pf)) {
				return pf;
			}
		}
		return "unknown";
	}

	/**
	 * 同类型限制多次使用
	 * @param type
	 * @return
	 */
	public static boolean isCdkTypeLimitMultiUse(String type) {
		if("x1".equals(type) || "x2".equals(type)|| "x3".equals(type)|| "x4".equals(type)|| "x5".equals(type)|| "x6".equals(type)) {
			return false;
		}
		return true;
	}
	
	/**
	 * 同类型限制多次使用
	 * @param type
	 * @return
	 */
	public static boolean isUniteCdkType(String type) {
		
		return true;
	}
	
	/**
	 * 字符串过滤
	 * 
	 * @param info
	 * @return
	 */
	public static String filterString(String info) {
		return info.trim().
				replace("#", "*").
				replace("&", "*").
				replace("<", "*").
				replace(">", "*").
				replace("%", "*").
				replace("^", "*");
	}
	
	/**
	 * puid修复
	 * @param puid
	 * @return
	 */
	public static String ito_91_amendPuid(String puid) {
		while (puid.indexOf("91_91_") >= 0) {
			puid = puid.replace("91_91_", "91_");
		}
		
		while (puid.indexOf("itousr_itousr_") >= 0) {
			puid = puid.replace("itousr_itousr_", "itousr_");
		}
		
		return puid;
	}
	
	/**
	 * 是否能堆叠
	 * 
	 * @param itemType
	 * @return
	 */
	public static boolean itemCanOverlap(int itemType) {
		if (itemType >= GsConst.ITEM_TYPE_BASE) {
			itemType /= GsConst.ITEM_TYPE_BASE;
		}

		return itemType == Const.itemType.PLAYER_ATTR_VALUE 
				|| itemType == Const.itemType.TOOL_VALUE
				|| itemType == Const.itemType.SOUL_VALUE;
	}

	/**
	 * 转换到标准物品类型定义
	 * 
	 * @param itemType
	 * @return
	 */
	public static int convertToStandardItemType(int itemType) {
		if (itemType >= GsConst.ITEM_TYPE_BASE) {
			return (itemType / GsConst.ITEM_TYPE_BASE) * GsConst.ITEM_TYPE_BASE;
		} else {
			return itemType * GsConst.ITEM_TYPE_BASE;
		}
	}

	/**
	 * 根据充值钻石计算vip
	 * 
	 * @param recharge
	 * @return
	 */
	public static int getVipLevelByRecharge(int recharge ,String platform) {
		int vipLevel = 0;
		for (int i = 0; i <= VipPrivilegeCfg.getMaxVipLevel(); i++) {
			VipPrivilegeCfg vipPrivilegeCfg = ConfigManager.getInstance().getConfigByKey(VipPrivilegeCfg.class, i);
			int needRecharge = vipPrivilegeCfg.getNeedRechargeGold();
			if (vipPrivilegeCfg != null && needRecharge <= recharge) {
				if (vipLevel < i) {
					vipLevel = i;
				}
			}
		}
		return vipLevel;
	}
	
	/**
	 * 根据充值货币计算vip
	 * 
	 * @param money
	 * @return
	 */
	public static int getVipLevelByPayMoney(float money) {
		int vipLevel = 0;
		for (int i = 0; i <= VipPrivilegeCfg.getMaxVipLevel(); i++) {
			VipPrivilegeCfg vipPrivilegeCfg = ConfigManager.getInstance().getConfigByKey(VipPrivilegeCfg.class, i);
			if (vipPrivilegeCfg != null && vipPrivilegeCfg.getNeedPayMoney() <= money) {
				if (vipLevel < i) {
					vipLevel = i;
				}
			}
		}
		return vipLevel;
	}

	/**
	 * 检测活动经验后能升级到哪个等级
	 * @param roleEntity
	 * @param exp
	 * @return
	 */
	public static int checkWillLevelUp(RoleEntity roleEntity, int exp) {
		
		int oldLevel = roleEntity.getLevel();
		long newExp = roleEntity.getExp() + exp;
		int level = oldLevel;
		
		// 转生限定等级提升
		int rebirthStage = roleEntity.getRebirthStage() + 1;
		RebirthStageCfg config = ConfigManager.getInstance().getConfigByKey(RebirthStageCfg.class, rebirthStage);

		for (; level < LevelExpCfg.getMaxLevel(); level++) {
			LevelExpCfg levelExpCfg = ConfigManager.getInstance().getConfigByKey(LevelExpCfg.class, level);
			if (levelExpCfg == null) {
				break;
			}
			
			// 达到转生前等级上限
			if (config != null && level >= config.getLevelLimit()) {
				break;
			}
			
			long levelUpNeedExp = ConfigManager.getInstance().getConfigByKey(LevelExpCfg.class, level).getExp();
			if (newExp < levelUpNeedExp) {
				break;
			}
			newExp -= levelUpNeedExp;
		}
		return level;
	}
	
	/**
	 * 获取技能栏位个数
	 * 
	 * @param level
	 * @return
	 */
	public static int getSkillSlotNumByLevel(RoleEntity roleEntity) {
		// 佣兵开放技能
		if (isHero(roleEntity.getType())||isSprite(roleEntity.getType())) {
			if ((roleEntity.getLevel() >= 1)&&(roleEntity.getLevel() <= 20)) {
				return 1;
			} else if ((roleEntity.getLevel() >= 21)&&(roleEntity.getLevel() <= 60)) {
				return 2;
			} else if (roleEntity.getLevel() >= 61) {
				return 3;
			}
//			else if (roleEntity.getLevel() >= 100) {
//				return 4;
//			}
			return 1;
		} else {  // 主帳號無技能
			return 0;
		}
	}
	
	public static int getRoleSkillLimitLevel(RoleEntity roleEntity) {
		if (roleEntity != null) {
//			if (roleEntity.getType() == Const.roleType.MAIN_ROLE_VALUE) {
				return roleEntity.getLevel();
//			}
//			return roleEntity.getStarLevel();
		}
		return 9999;
	}
	/**
	 * 是否為英雄
	 * @return
	 */
	public static boolean isHero(int roleType) {
		if ((roleType == GsConst.RoleType.MERCENARY)) {
			return true;
		}
		return false;
	}
	/**
	 * 是否為應精靈
	 * @return
	 */
	public static boolean isSprite(int roleType) {
		if ((roleType ==  GsConst.RoleType.SPRITE)) {
			return true;
		}
		return false;
	}
	/**
	 * 以排行榜種類找英雄屬性
	 * @param atype
	 * @return
	 */
	public static int getAttrByRankType(RankType atype) {
		if (atype == RankType.HERO_FIRE_RANK) {
			return GsConst.HeroAttrType.Fire;
		} else if (atype == RankType.HERO_WATER_RANK) {
			return GsConst.HeroAttrType.Water;
		} else if (atype == RankType.HERO_WIND_RANK) {
			return GsConst.HeroAttrType.Wind;
		} else if (atype == RankType.HERO_LIGHT_RANK) {
			return GsConst.HeroAttrType.Light;
		} else if (atype == RankType.HERO_DARK_RANK) {
			return GsConst.HeroAttrType.Dark;
		}
		return 0;
	}
	/**
	 * 拼接字符串
	 * @param objs
	 * @param s
	 * @return
	 */
	public static String join(Collection<?> objs, String s) {
		StringBuilder sb = new StringBuilder();
		Iterator<?> iter = objs.iterator();
		while(iter.hasNext()) {
			Object obj = iter.next();
			sb.append(obj);
			if(iter.hasNext()) {
				sb.append(s);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 拼接字符串
	 * @param objs
	 * @param s
	 * @return
	 */
	public static String join(Object[] objs, String s) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i < objs.length; i++) {
			Object object = objs[i];
			if(i == (objs.length - 1)) {
				sb.append(object);
			} else {
				sb.append(object).append(",");
			}
			
		}
		return sb.toString();
	}
	
	
	/**
	 * ip转化成int
	 * @param ip
	 * @return
	 */
	public static int convertIP2Int(String ip) {
		String[] ips = ip.split("\\.");
		return (Integer.parseInt(ips[0]) << 24) + (Integer.parseInt(ips[1]) << 16) + (Integer.parseInt(ips[2]) << 8) + Integer.parseInt(ips[3]);
	}
	
	/**
	 * 在roleInfoBuilder里面取出对应的属性的值
	 * @param roleInfoBuilder
	 * @param attr
	 * @return
	 */
	public static int getAttr(RoleInfo.Builder roleInfoBuilder, Const.attr attr){
		for(Attr.Builder attrInfo : roleInfoBuilder.getAttributeBuilder().getAttributeBuilderList()) {
			if(attrInfo.getAttrId() == attr.getNumber()) {
				return attrInfo.getAttrValue();
			}
		}
		return 0;
	}
	
	/**
	 * 在roleInfoBuilder里面取出对应的属性的值
	 * @param roleInfoBuilder
	 * @param attr
	 * @return
	 */
	public static Attr.Builder getAttrBuilder(RoleInfo.Builder roleInfoBuilder, Const.attr attr){
		for(Attr.Builder attrInfo : roleInfoBuilder.getAttributeBuilder().getAttributeBuilderList()) {
			if(attrInfo.getAttrId() == attr.getNumber()) {
				return attrInfo;
			}
		}
		return null;
	}
	
	/**
	 * 发送玩家公会聊天标记
	 * @param player
	 */
	public static void sendAllianceChatTag(Player player) {
		HPAllianceSwitch.Builder builder = HPAllianceSwitch.newBuilder();
		if(player.getPlayerData() == null) {
			return ;
		}
		PlayerAllianceEntity playerAllianceEntity = player.getPlayerData().getPlayerAllianceEntity();
		if(playerAllianceEntity == null) {
			return ;
		}
		int allianceId = playerAllianceEntity.getAllianceId();
		builder.setTag(allianceId > 0 ? true : false);
		if (allianceId > 0) {
			builder.setAllianceId(allianceId);
		}
		player.sendProtocol(Protocol.valueOf(HP.code.ALLIANCE_SWITCH_S, builder));
	}

	public static String getCurDate_yyyyMMdd() {
		return getDate_yyyyMMdd(GuaJiTime.getCalendar().getTime());
	}
	
	public static String getDate_yyyyMMdd(Date date) {
		return GuaJiTime.DATE_FORMATOR_YYYYMMDD(date);
	}
	
	
	public static String getTabDBId(int platformId) {
		if (!App.getInstance().getAppCfg().isDebug()) {
			if (platformId == 1 ) { // h365
				return "kad6kdj4p1vjwuzg";
			}
			if (platformId == 2) {
				return "hv4udrq9iv8oubgu"; // 工口
			}
			if (platformId == 6) {
				return "h4rj7bbhehxks2ml"; // KUSO69 h4rj7bbhehxks2ml APlus ojd31nr7523qt1pq
			}
		} else {
			return "nzh5yq3ncmpf4gbb"; // debug use
		}
		return "";
	}
	
	public static int transtoPlatformId(String channel) {
		
		if (channel.contains("h365")) {
			return GsConst.PlatformById.H365;
		} else if (channel.contains("r18")) {
			return GsConst.PlatformById.R18;
		} else if (channel.contains("jsg")) {
			return GsConst.PlatformById.JSG;
		}else if (channel.contains("lsj")) {
			return GsConst.PlatformById.LSJ;
		}else if (channel.contains("mura")) {
			return GsConst.PlatformById.MURA;
		}else if (channel.contains("kuso")) {
			return GsConst.PlatformById.KUSO;
		}
		
		if (App.getInstance().getAppCfg().isDebug()) {
			return GsConst.PlatformById.H365;
		} else {
			return 0;
		}
	}
	
	/**
	 * 字符串轉換成數列表
	 * 
	 * @param idWeights
	 * @return
	 */
	public static List<Integer> StringToIntList(String aStr) {
		List<Integer> itemList = new ArrayList<>();
		if (aStr != null && !aStr.isEmpty()) {
			String items[] = aStr.split(",");
			if (items.length > 0) {
				for (String item : items) {
					itemList.add(Integer.valueOf(item.trim()));
				}
			}
		}
		return itemList;
	}
	
}
