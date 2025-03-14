
-------------------------------------商店修改----------------------------------------
ALTER TABLE `shop`
CHANGE COLUMN `refreshTodayNums` `refreshTimes`  int(112) NOT NULL DEFAULT 0 AFTER `refreshDate`;


------------------------------2016年12月6日 20:35:17(装备强化优化)---------------------
ALTER TABLE `equip`
CHANGE COLUMN `strengthItemCount` `strengthItemStr`  varchar(2048) NOT NULL AFTER `strength`;

-------------------------------------任务与成就----------------------------------------

ALTER TABLE `dailyquest`
ADD COLUMN `dailyPoint`  int(11) UNSIGNED NOT NULL AFTER `dailyQuestStr`;

ALTER TABLE `dailyquest`
ADD COLUMN `dailyPointStateStr`  varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' AFTER `dailyQuestStr`;

drop table `quest`;

CREATE TABLE `quest` (
  `playerId` int(10) unsigned NOT NULL,
  `questStr`  text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


----------------------------------------佣兵-----------------------------------------------
ALTER TABLE `role`
ADD COLUMN `roleState`  int(11) NOT NULL DEFAULT 0 AFTER `exp`,
ADD COLUMN `soulCount`  int(11) NOT NULL DEFAULT 0 AFTER `roleState`,
ADD COLUMN `fightIndex`  int(11) NOT NULL DEFAULT 0 AFTER `status`,
ADD COLUMN `stageLevel`  int(11) NOT NULL DEFAULT 0 AFTER `starLevel`,
ADD COLUMN `ringStr`  varchar(512) NULL AFTER `stageLevel`;

---------------------------------------
ALTER TABLE `role`
DROP COLUMN `fightIndex`;
---------------连续登录------------------------
ALTER TABLE `player`
ADD COLUMN `loginDay`  int(11) NULL DEFAULT 0 AFTER `crystal`;

---------------签名字段修改（支持emoji）------------------------
ALTER TABLE `player` MODIFY COLUMN `signature`  varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL AFTER `prof`;

------------------------充值修改----------------------------
ALTER TABLE `recharge`
ADD COLUMN `isTest`  int(11) NULL DEFAULT 0 AFTER `platform`;

------------------------2017/04/27/好友修改----------------------------
ALTER TABLE `friend`
ADD COLUMN `applyFriendIds` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' AFTER `friendIds`; 
------------------------2017年4月26日 03:03:02佣兵--------------
DROP TABLE IF EXISTS `formation`;
CREATE TABLE `formation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `playerId` int(11) NOT NULL DEFAULT '0',
  `type` int(4) unsigned zerofill NOT NULL,
  `fightingArray` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;
----------------------------2017年5月10日聊天缓存记录-------------------------------
DROP TABLE IF EXISTS `chat_msg`;
CREATE TABLE `chat_msg` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `typeId` int(11) unsigned zerofill NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  `msg` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `pos` int(11) unsigned zerofill NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
