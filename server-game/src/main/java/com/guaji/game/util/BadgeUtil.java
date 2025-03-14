package com.guaji.game.util;

import java.util.List;

import org.guaji.config.ConfigManager;
import org.guaji.db.DBManager;
import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;

import com.guaji.game.config.BadgeCfg;
import com.guaji.game.config.BadgeGachaListCfg;
import com.guaji.game.entity.BadgeEntity;
import com.guaji.game.player.Player;

/**
 * 徽章帮助类
 */
public class BadgeUtil {


    /**
     * 生成徽章
     */
    public static BadgeEntity generateBadge(Player player, int badgeId) {
        try {
            BadgeCfg badgeCfg = ConfigManager.getInstance().getConfigByKey(BadgeCfg.class, badgeId);
            if (badgeCfg == null) {
                return null;
            }
            BadgeEntity badgeEntity = new BadgeEntity();
            badgeEntity.setPlayerId(player.getId());
            badgeEntity.setBadgeId(badgeCfg.getId());
//            badgeEntity.setSkill(badgeCfg.RandomSkill());
            badgeEntity.setAttr(badgeCfg.RandomAttr());
            // 洗鍊資料
			List<Integer> addList = BadgeGachaListCfg.getRandomBadage(badgeCfg.getSkillpool(), badgeCfg.getSlots(),null);
    		if (addList.size() == badgeCfg.getSlots()) {
        		badgeEntity.setRefineList(addList);
        		badgeEntity.setSkillList(addList);
    		} 
            badgeEntity.setFusionLock(0);
            return badgeEntity;
        } catch (Exception e) {
            MyException.catchException(e);
        }
        return null;
    }


    /**
     * 同步数据库创建徽章
     *
     * @param badgeEntities
     * @return
     */
    public static boolean createBadgesSync(List<BadgeEntity> badgeEntities) {
        String insertSql = "INSERT INTO badge (playerId,badgeId,skill,fusionLock,attr,skillStr,refineStr,"
                + "createTime,updateTime,invalid) values ";
        StringBuilder sb = new StringBuilder(insertSql);
        for (int i = 0; i < badgeEntities.size(); i++) {
            if (i == badgeEntities.size() - 1) {
                sb.append(generatePartSql(badgeEntities.get(i)));
            } else {
                sb.append(generatePartSql(badgeEntities.get(i))).append(",");
            }
        }

        sb.append(";");
        insertSql = sb.toString();
        List<Long> primaryKeyList = DBManager.getInstance().executeInsert(insertSql);
        for (int i = 0; i < primaryKeyList.size(); i++) {
            badgeEntities.get(i).setId(primaryKeyList.get(i));
        }
        return true;
    }

    private static String generatePartSql(BadgeEntity badgeEntity) {
        StringBuilder sb = new StringBuilder(2048).append("(");
        sb.append(badgeEntity.getPlayerId()).append(",").append(badgeEntity.getBadgeId()).append(",").append(badgeEntity.getSkill()).append(",").append(badgeEntity.getFusionLock()).append(",'").append(badgeEntity.getAttr()).append("','")
        		.append(badgeEntity.getSkillStr()).append("','").append(badgeEntity.getRefineStr()).append("','")
                .append(GuaJiTime.getTimeString(badgeEntity.getCreateTime())).append("','")
                .append(GuaJiTime.getTimeString(badgeEntity.getUpdateTime())).append("',").append(badgeEntity.isInvalid() ? 1 : 0).append(")");
        return sb.toString();
    }


//    public static Attribute refreshAttribute(BadgeEntity badgeEntity, PlayerData playerData) {
//        if (badgeEntity == null) {
//            return null;
//        }
//        BadgeCfg badgeCfg = ConfigManager.getInstance().getConfigByKey(BadgeCfg.class, badgeEntity.getBadgeId());
//        if (badgeCfg == null) {
//            return null;
//        }
//        Attribute attribute = badgeEntity.getAttribute();
//        attribute.clear();
//
//        if (badgeEntity != null) {
//            attribute.add(badgeCfg.getBasicAttribute());
//        }
//        return attribute;
//    }


}
