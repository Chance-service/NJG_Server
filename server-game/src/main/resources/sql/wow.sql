/*
SQLyog Professional v12.08 (64 bit)
MySQL - 5.6.42-log : Database - wow
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Table structure for table `alliance` */

CREATE TABLE `alliance` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `playerId` int(11) DEFAULT NULL,
  `playerName` varchar(256) DEFAULT NULL,
  `name` varchar(256) DEFAULT NULL,
  `level` int(11) DEFAULT '1',
  `exp` int(11) DEFAULT '0',
  `joinLimit` int(11) DEFAULT '0',
  `notice` varchar(512) DEFAULT NULL,
  `createAllianceTime` bigint(20) DEFAULT '0',
  `bossOpen` int(11) DEFAULT '0',
  `bossOpenTime` bigint(20) DEFAULT '0',
  `bossOpenSize` int(11) DEFAULT '0',
  `bossId` int(11) DEFAULT '0',
  `bossJoinStr` varchar(4096) DEFAULT NULL,
  `bossHp` int(11) DEFAULT '0',
  `bossMaxTime` bigint(20) DEFAULT '0',
  `bossAttTime` bigint(20) DEFAULT '0',
  `bossAddProp` varchar(2048) DEFAULT '0',
  `bossVitality` int(11) NOT NULL DEFAULT '0',
  `curDayAddVitality` int(11) NOT NULL,
  `beforeDayAddVitality` int(11) NOT NULL,
  `luckyScore` int(11) NOT NULL DEFAULT '0',
  `lastResetLuckyScoreTime` bigint(20) NOT NULL DEFAULT '0',
  `isDelete` int(11) DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  `teamMapStr` varchar(2048) DEFAULT NULL,
  `sendEmailNum` int(11) DEFAULT '0',
  `sendEmailTime` timestamp NOT NULL DEFAULT '2013-12-31 16:00:00',
  `canChangeName` int(11) NOT NULL DEFAULT '0',
  `automaticOpen` varchar(1024) NOT NULL DEFAULT '',
  `everydayBossOpenTimes` int(11) NOT NULL DEFAULT '0',
  `hasCheckLeaderMail` int(11) NOT NULL DEFAULT '0',
  `refreshTime` bigint(20) NOT NULL DEFAULT '0',
  `activeValue` int(11) DEFAULT '0',
  `totalFight` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `alliance_battle_info` */

CREATE TABLE `alliance_battle_info` (
  `stageId` int(11) NOT NULL DEFAULT '0',
  `state` int(11) DEFAULT NULL,
  `allianceItemsStr` varchar(2048) DEFAULT NULL,
  `battleResultStr` blob,
  `championStr` varchar(2048) DEFAULT NULL,
  `createTime` timestamp NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`stageId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*Table structure for table `alliance_battle_item` */

CREATE TABLE `alliance_battle_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `allianceId` int(11) DEFAULT NULL,
  `stageId` int(11) DEFAULT NULL,
  `vitality` int(11) DEFAULT NULL,
  `battleResult` int(11) DEFAULT NULL,
  `teamMapStr` varchar(2048) DEFAULT NULL,
  `allianceName` varchar(2048) DEFAULT NULL,
  `allianceLevel` int(11) DEFAULT '0',
  `captainName` varchar(2048) DEFAULT NULL,
  `memberListStr` varchar(2048) DEFAULT NULL,
  `buffId` int(11) DEFAULT '0',
  `inspireInfoMapStr` varchar(4096) DEFAULT '{}',
  `streakTimes` int(11) DEFAULT '0',
  `createTime` timestamp NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) DEFAULT NULL,
  `hasDraw` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*Table structure for table `alliance_fight_unit` */

CREATE TABLE `alliance_fight_unit` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `stageId` int(11) DEFAULT NULL,
  `versusId` int(11) DEFAULT NULL,
  `leftIndex` int(11) DEFAULT NULL,
  `rightIndex` int(11) DEFAULT NULL,
  `winIndex` int(11) DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  `fightReport` mediumblob,
  `createTime` timestamp NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*Table structure for table `alliance_fight_versus` */

CREATE TABLE `alliance_fight_versus` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `stageId` int(11) NOT NULL,
  `fightGroup` int(11) NOT NULL,
  `leftId` int(11) DEFAULT NULL,
  `rightId` int(11) DEFAULT NULL,
  `winId` int(11) DEFAULT NULL,
  `investLeftStr` text,
  `investRightStr` text,
  `isRewardInvest` int(11) NOT NULL DEFAULT '0',
  `state` int(11) DEFAULT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*Table structure for table `arena` */

CREATE TABLE `arena` (
  `playerId` int(11) NOT NULL,
  `rank` int(11) NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `arena_report` */

CREATE TABLE `arena_report` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `battleResult` mediumblob NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT;

/*Table structure for table `arena_snapshot` */

CREATE TABLE `arena_snapshot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `builddate` int(11) DEFAULT NULL,
  `createTime` datetime NOT NULL,
  `invalid` bit(1) DEFAULT NULL,
  `playerId` int(11) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `snapshot` mediumblob,
  `systype` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `updateTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `avatar` */

CREATE TABLE `avatar` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `playerId` int(11) NOT NULL DEFAULT '0',
  `avatarId` int(11) NOT NULL DEFAULT '0',
  `checked` int(11) NOT NULL DEFAULT '0',
  `endTime` bigint(20) NOT NULL DEFAULT '-1',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `badge` */

CREATE TABLE `badge` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `playerId` int(11) DEFAULT '0',
  `badgeId` int(11) DEFAULT '0',
  `skill` int(11) DEFAULT '0',
  `createTime` datetime NOT NULL,
  `updateTime` datetime DEFAULT NULL,
  `invalid` int(11) DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `id` (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*Table structure for table `camp` */

CREATE TABLE `camp` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `campId` int(11) NOT NULL,
  `stageId` int(11) NOT NULL,
  `isWin` int(11) NOT NULL DEFAULT '0',
  `totalBattleScore` int(11) NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `stageId_Index` (`stageId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `campwar` */

CREATE TABLE `campwar` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `playerId` int(11) NOT NULL,
  `playerName` varchar(256) NOT NULL DEFAULT '',
  `roleCfgId` int(11) NOT NULL DEFAULT '0',
  `stageId` int(11) NOT NULL,
  `fightValue` int(11) NOT NULL DEFAULT '0',
  `campId` int(11) NOT NULL DEFAULT '0',
  `baseMaxBlood` int(11) NOT NULL DEFAULT '0',
  `curRemainBlood` int(11) NOT NULL DEFAULT '0',
  `addRemainBlood` int(11) NOT NULL DEFAULT '0',
  `curMaxBlood` int(11) NOT NULL DEFAULT '0',
  `inspireTimes` int(11) NOT NULL DEFAULT '0',
  `maxWinStreak` int(11) NOT NULL DEFAULT '0',
  `curWinStreak` int(11) NOT NULL DEFAULT '0',
  `totalWin` int(11) NOT NULL DEFAULT '0',
  `totalLose` int(11) NOT NULL DEFAULT '0',
  `totalReputation` int(11) NOT NULL DEFAULT '0',
  `totalCoins` int(11) NOT NULL DEFAULT '0',
  `allBattleResult` varchar(256) NOT NULL DEFAULT '',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `playerId_Index` (`playerId`) USING BTREE,
  KEY `stageId_Index` (`stageId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*Table structure for table `campwar_auto` */

CREATE TABLE `campwar_auto` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `stageId` int(11) NOT NULL DEFAULT '0',
  `autoJoinPlayerIdsStr` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `chat_msg` */

CREATE TABLE `chat_msg` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `typeId` int(11) unsigned zerofill NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  `msg` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `pos` int(11) unsigned zerofill NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `chat_record` */

CREATE TABLE `chat_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `playerId` int(11) NOT NULL DEFAULT '0',
  `chatMsg` varchar(1024) NOT NULL DEFAULT '',
  `createTime` timestamp NULL DEFAULT '1999-12-31 08:00:00',
  `updateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `playerId_index` (`playerId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `chat_skin` */

CREATE TABLE `chat_skin` (
  `playerId` int(11) NOT NULL DEFAULT '0',
  `curSkinId` int(11) NOT NULL DEFAULT '0',
  `ownedSkinsStr` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `redPoint` int(11) NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

/*Table structure for table `cross_battle` */

CREATE TABLE `cross_battle` (
  `id` int(11) NOT NULL,
  `playerIds` varchar(256) DEFAULT NULL,
  `refreshTime` bigint(20) NOT NULL,
  `rewardTime` bigint(20) NOT NULL,
  `pushState` int(11) NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `invalid` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `cross_player` */

CREATE TABLE `cross_player` (
  `playerId` int(11) NOT NULL,
  `score` int(11) NOT NULL,
  `rank` int(11) NOT NULL,
  `crossCoin` int(11) NOT NULL,
  `battleTimes` int(11) NOT NULL,
  `buyTimes` int(11) NOT NULL,
  `synchroTime` bigint(20) NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `invalid` tinyint(1) NOT NULL,
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `crystal_shop` */

CREATE TABLE `crystal_shop` (
  `playerId` int(11) NOT NULL,
  `shopItemMapStr` text COLLATE utf8_bin,
  `lucky` int(11) DEFAULT '0',
  `refreshCount` int(11) DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '1999-12-31 16:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

/*Table structure for table `daily_statistics` */

CREATE TABLE `daily_statistics` (
  `date` varchar(32) COLLATE utf8_bin NOT NULL,
  `totalUsers` int(11) NOT NULL DEFAULT '0',
  `totalDevice` int(11) NOT NULL DEFAULT '0',
  `totalPayUsers` int(11) NOT NULL DEFAULT '0',
  `totalPayDevice` int(11) NOT NULL DEFAULT '0',
  `totalPayMoney` int(11) NOT NULL DEFAULT '0',
  `newUsers` int(11) NOT NULL DEFAULT '0',
  `newDevice` int(11) NOT NULL DEFAULT '0',
  `dailyActiveUsers` int(11) NOT NULL DEFAULT '0',
  `userRetentionRate` float NOT NULL DEFAULT '0',
  `deviceRetentionRate` float NOT NULL DEFAULT '0',
  `payUsers` int(11) NOT NULL DEFAULT '0',
  `payDevice` int(11) NOT NULL DEFAULT '0',
  `payMoney` int(11) NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `dailyquest` */

CREATE TABLE `dailyquest` (
  `playerId` int(11) NOT NULL,
  `dailyQuestStr` varchar(1024) COLLATE utf8_bin DEFAULT '',
  `dailyPointStateStr` varchar(1024) COLLATE utf8_bin DEFAULT '',
  `dailyPoint` int(11) DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

/*Table structure for table `eighteenprinces` */

CREATE TABLE `eighteenprinces` (
  `playerId` int(11) NOT NULL,
  `eighteenPrinceCount` int(11) DEFAULT NULL,
  `eighteenPrinceHelpRoleItemId` int(11) DEFAULT NULL,
  `enemyformation` varchar(1024) DEFAULT NULL,
  `firstChallenge` int(11) DEFAULT '1',
  `formation` varchar(2048) DEFAULT NULL,
  `formation_history` mediumtext,
  `help` varchar(255) DEFAULT NULL,
  `helpHistory` varchar(2048) DEFAULT NULL,
  `layerId` int(11) DEFAULT '0',
  `dropItemId` int(11) NOT NULL DEFAULT '0' COMMENT '掉落对应品质奖励',
  `resetTime` int(11) DEFAULT NULL,
  `bigMedicalKit` int(11) DEFAULT NULL,
  `midleMedicalKit` int(11) DEFAULT NULL,
  `smallMedicalKit` int(11) DEFAULT NULL,
  `createTime` datetime NOT NULL,
  `updateTime` datetime DEFAULT NULL,
  `invalid` int(11) DEFAULT '0',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `eighteenprinces_help_history` */

CREATE TABLE `eighteenprinces_help_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createTime` datetime NOT NULL,
  `friendPlayerId` int(11) DEFAULT NULL,
  `helpCount` int(11) DEFAULT NULL,
  `invalid` int(11) DEFAULT '0',
  `playerId` int(11) DEFAULT NULL,
  `reward` int(11) DEFAULT '0',
  `updateTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `element` */

CREATE TABLE `element` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `playerId` int(11) DEFAULT NULL,
  `itemId` int(11) DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  `exp` int(11) DEFAULT NULL,
  `quality` int(11) DEFAULT NULL,
  `basicAttrStr` varchar(2048) DEFAULT NULL,
  `recastAttrIdStr` varchar(2048) DEFAULT NULL,
  `recastAttrValueStr` varchar(2048) DEFAULT NULL,
  `extraAttrStr` varchar(2048) DEFAULT NULL,
  `recastAttrId` int(11) DEFAULT '0',
  `createTime` timestamp NULL DEFAULT NULL,
  `updateTime` timestamp NULL DEFAULT NULL,
  `invalid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*Table structure for table `email` */

CREATE TABLE `email` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `playerId` int(11) NOT NULL DEFAULT '0',
  `type` int(11) NOT NULL DEFAULT '0',
  `mailId` int(11) NOT NULL DEFAULT '0',
  `title` varchar(2048) COLLATE utf8_bin DEFAULT '',
  `content` varchar(2048) COLLATE utf8_bin DEFAULT '',
  `params` varchar(2048) COLLATE utf8_bin DEFAULT '',
  `classification` int(11) DEFAULT '0',
  `effectTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `playerId_index` (`playerId`),
  KEY `query_index` (`playerId`,`invalid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `equip` */

CREATE TABLE `equip` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `playerId` int(11) NOT NULL DEFAULT '0',
  `equipId` int(11) NOT NULL DEFAULT '0',
  `strength` int(11) NOT NULL DEFAULT '0',
  `strengthItemStr` varchar(2048) COLLATE utf8_bin NOT NULL,
  `starLevel` int(11) NOT NULL DEFAULT '0',
  `starExp` int(11) NOT NULL DEFAULT '0',
  `godlyAttrId` int(11) NOT NULL DEFAULT '0',
  `starLevel2` int(11) NOT NULL DEFAULT '0',
  `starExp2` int(11) NOT NULL DEFAULT '0',
  `godlyAttrId2` int(11) NOT NULL DEFAULT '0',
  `primaryAttrType1` int(11) NOT NULL DEFAULT '0',
  `primaryAttrValue1` int(11) NOT NULL DEFAULT '0',
  `primaryAttrType2` int(11) NOT NULL DEFAULT '0',
  `primaryAttrValue2` int(11) NOT NULL DEFAULT '0',
  `secondaryAttrType1` int(11) NOT NULL DEFAULT '0',
  `secondaryAttrValue1` int(11) NOT NULL DEFAULT '0',
  `secondaryAttrType2` int(11) NOT NULL DEFAULT '0',
  `secondaryAttrValue2` int(11) NOT NULL DEFAULT '0',
  `secondaryAttrType3` int(11) NOT NULL DEFAULT '0',
  `secondaryAttrValue3` int(11) NOT NULL DEFAULT '0',
  `secondaryAttrType4` int(11) NOT NULL DEFAULT '0',
  `secondaryAttrValue4` int(11) NOT NULL DEFAULT '0',
  `gem1` int(11) NOT NULL DEFAULT '-1',
  `gem2` int(11) NOT NULL DEFAULT '-1',
  `gem3` int(11) NOT NULL DEFAULT '-1',
  `gem4` int(11) NOT NULL DEFAULT '-1',
  `status` int(11) NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `playerId_index` (`playerId`),
  KEY `query_index` (`playerId`,`invalid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `expedition_armory` */

CREATE TABLE `expedition_armory` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `stageId` int(11) NOT NULL DEFAULT '0',
  `curDonateStage` int(11) NOT NULL DEFAULT '1',
  `stageExp` int(11) NOT NULL DEFAULT '0',
  `isGrantLastStage` int(11) NOT NULL DEFAULT '0',
  `isGrantRank` int(11) NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  `nextSysAutoAddStageExpTime` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `forging_equip` */

CREATE TABLE `forging_equip` (
  `stageId` int(11) DEFAULT '0',
  `totalTimes` int(11) DEFAULT '0',
  `prizeLimit` varchar(1024) DEFAULT '',
  `createTime` timestamp NOT NULL DEFAULT '1999-12-31 16:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `formation` */

CREATE TABLE `formation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `playerId` int(11) NOT NULL DEFAULT '0',
  `type` int(4) unsigned zerofill NOT NULL,
  `fightingArray` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `friend` */

CREATE TABLE `friend` (
  `playerId` int(10) unsigned NOT NULL,
  `friendIds` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `applyFriendIds` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `shieldMapStr` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `pointMapStr` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `dailyMsgPlayerStr` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `dailyGiftStr` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  `bindingState` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `gm_recharge` */

CREATE TABLE `gm_recharge` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `puid` varchar(256) COLLATE utf8_bin NOT NULL,
  `serverId` int(11) DEFAULT '0',
  `playerId` int(11) NOT NULL DEFAULT '0',
  `goodsId` int(11) NOT NULL DEFAULT '0',
  `goodsCost` int(11) NOT NULL DEFAULT '0',
  `addGold` int(11) NOT NULL DEFAULT '0',
  `isFirstPay` int(11) NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '1999-12-31 16:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `puid_index` (`puid`(255)) USING BTREE,
  KEY `playerId_index` (`playerId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `goldfish_reward` */

CREATE TABLE `goldfish_reward` (
  `stageId` int(11) NOT NULL DEFAULT '0',
  `isSendReward` int(11) NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  KEY `stageId` (`stageId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

/*Table structure for table `gvg_alliance` */

CREATE TABLE `gvg_alliance` (
  `allianceId` int(11) NOT NULL,
  `allianceLevel` int(11) NOT NULL DEFAULT '0',
  `declareTimes` int(11) NOT NULL,
  `addCount` int(11) NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `invalid` tinyint(1) NOT NULL,
  PRIMARY KEY (`allianceId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `gvg_city` */

CREATE TABLE `gvg_city` (
  `cityId` int(11) NOT NULL,
  `holderId` int(11) NOT NULL,
  `marauderId` int(11) NOT NULL,
  `attackerIds` mediumtext,
  `defenderIds` mediumtext,
  `fightbackTime` bigint(20) NOT NULL,
  `isFightback` tinyint(1) NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `invalid` tinyint(1) NOT NULL,
  PRIMARY KEY (`cityId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `gvg_city_reward` */

CREATE TABLE `gvg_city_reward` (
  `allianceId` int(11) NOT NULL,
  `cityIds` varchar(256) DEFAULT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `invalid` tinyint(1) NOT NULL,
  PRIMARY KEY (`allianceId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `gvg_history_rank` */

CREATE TABLE `gvg_history_rank` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rank` int(11) DEFAULT NULL,
  `allianceName` varchar(300) DEFAULT NULL,
  `masterName` varchar(300) DEFAULT NULL,
  `allianceId` int(11) DEFAULT NULL,
  `holdCityInfo` varchar(1500) DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  `Score` int(11) DEFAULT NULL,
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `invalid` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `gvg_log` */

CREATE TABLE `gvg_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `attackerName` varchar(256) NOT NULL,
  `defenderName` varchar(256) DEFAULT NULL,
  `result` int(11) NOT NULL,
  `cityId` int(11) NOT NULL,
  `isFightback` tinyint(1) NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `invalid` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `gvg_revive_log` */

CREATE TABLE `gvg_revive_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `allianceName` varchar(100) NOT NULL COMMENT '盟会名称',
  `playerId` int(11) DEFAULT NULL COMMENT '盟主或付盟主',
  `allianceId` int(11) DEFAULT NULL COMMENT '盟会编号',
  `consume` int(11) NOT NULL,
  `createtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间',
  `pointId` int(11) NOT NULL COMMENT '复活编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `gvg_revive_point` */

CREATE TABLE `gvg_revive_point` (
  `id` int(4) NOT NULL COMMENT '复活点编号',
  `allianceId` int(11) unsigned DEFAULT NULL COMMENT '联盟编号(状态为0时未被占领 非0时被占领)',
  `point_X` int(10) unsigned DEFAULT NULL COMMENT '复活点位置_X',
  `point_Y` int(10) unsigned DEFAULT NULL COMMENT '复活点位置_Y',
  `playerID` int(10) unsigned DEFAULT NULL,
  `actionTime` timestamp NULL DEFAULT NULL COMMENT '未被操作复活时为0',
  `invalid` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `gvg_reward` */

CREATE TABLE `gvg_reward` (
  `playerId` int(11) NOT NULL,
  `received` varchar(128) NOT NULL,
  `refreshTime` bigint(20) NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `invalid` tinyint(1) NOT NULL,
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `gvg_time` */

CREATE TABLE `gvg_time` (
  `gvgId` int(11) NOT NULL,
  `isFirst` tinyint(1) NOT NULL,
  `refreshTime` bigint(20) NOT NULL,
  `pushState` int(11) NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `invalid` tinyint(1) NOT NULL,
  `resettime` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`gvgId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `harem_activity` */

CREATE TABLE `harem_activity` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `playerId` int(11) DEFAULT '0',
  `lastCommonFreeTime` bigint(255) DEFAULT '0',
  `commonFreeChance` int(255) DEFAULT '0',
  `commonTotalTimes` int(11) DEFAULT '0',
  `commonDayTotalTimes` int(11) DEFAULT '0',
  `lastMiddleFreeTime` bigint(255) DEFAULT '0',
  `middleFreeChance` int(255) DEFAULT '0',
  `middleTotalTimes` int(255) DEFAULT '0',
  `lastAdvancedFreeTime` bigint(255) DEFAULT '0',
  `advancedFreeChance` int(255) DEFAULT '0',
  `advancedTotalTimes` int(11) DEFAULT '0',
  `lastStrictFreeTime` bigint(255) DEFAULT '0',
  `strictFreeChance` int(255) DEFAULT '0',
  `strictTotalTimes` int(255) DEFAULT '0',
  `newStrictEndTime` int(11) NOT NULL DEFAULT '0',
  `lastNewStrictFreeTime` int(11) NOT NULL DEFAULT '0',
  `newStrictFreeChance` int(11) NOT NULL DEFAULT '0',
  `newStrictTotalTimes` int(11) NOT NULL DEFAULT '0',
  `limitEndTime` int(11) NOT NULL DEFAULT '0',
  `lastLimitFreeTime` int(11) NOT NULL DEFAULT '0',
  `limitFreeChance` int(11) NOT NULL DEFAULT '0',
  `limitTotalTimes` int(11) NOT NULL DEFAULT '0',
  `score` int(255) DEFAULT '0',
  `exchangeStr` varchar(255) DEFAULT '',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL,
  `exchangeNextResetTime` bigint(255) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `hero_token_task` */

CREATE TABLE `hero_token_task` (
  `playerId` int(11) NOT NULL,
  `tasks` varchar(1024) COLLATE utf8_bin DEFAULT '',
  `completeTasks` varchar(1024) COLLATE utf8_bin DEFAULT '',
  `shopData` varchar(1024) COLLATE utf8_bin DEFAULT '',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

/*Table structure for table `honor_shop` */

CREATE TABLE `honor_shop` (
  `playerId` int(10) unsigned NOT NULL,
  `shopItemMapStr` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `lucky` int(11) NOT NULL DEFAULT '0',
  `refreshCount` int(11) NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `ip_addr` */

CREATE TABLE `ip_addr` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `beginIp` varchar(255) DEFAULT NULL,
  `beginIpInt` int(11) DEFAULT NULL,
  `endIp` varchar(255) DEFAULT NULL,
  `endIpInt` int(11) DEFAULT NULL,
  `position` varchar(255) DEFAULT NULL,
  `province` int(11) DEFAULT NULL,
  `city` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `beginIpIntKey` (`beginIpInt`),
  UNIQUE KEY `endIpIntKey` (`endIpInt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `item` */

CREATE TABLE `item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `playerId` int(11) NOT NULL DEFAULT '0',
  `itemId` int(11) NOT NULL DEFAULT '0',
  `itemCount` int(11) NOT NULL DEFAULT '0',
  `levelUpTimes` int(11) NOT NULL DEFAULT '0',
  `status` int(11) NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `playerId_index` (`playerId`),
  KEY `query_index` (`playerId`,`invalid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `login` */

CREATE TABLE `login` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `playerId` int(11) NOT NULL DEFAULT '0',
  `playerName` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `puid` varchar(255) COLLATE utf8_bin NOT NULL,
  `period` int(11) NOT NULL DEFAULT '0',
  `date` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  `serverId` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_login` (`puid`,`date`),
  KEY `playerId_index` (`playerId`) USING BTREE,
  KEY `playerName_index` (`playerName`) USING BTREE,
  KEY `puid_index` (`puid`) USING BTREE,
  KEY `date_index` (`date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

/*Table structure for table `maiden_encounter_activity` */

CREATE TABLE `maiden_encounter_activity` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `playerId` int(11) NOT NULL,
  `stageId` int(11) NOT NULL,
  `currentIndex` int(11) NOT NULL,
  `surplusFreeInteractTimes` int(11) NOT NULL,
  `surplusFreeRefreshTimes` int(11) NOT NULL,
  `devilRefreshTime` int(11) NOT NULL,
  `progress` varchar(255) NOT NULL,
  `maidenStage` varchar(255) NOT NULL,
  `exchange` varchar(255) NOT NULL,
  `historyRandomTimes` int(11) NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `invalid` int(4) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `playerIdIndex` (`playerId`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `map` */

CREATE TABLE `map` (
  `playerId` int(11) NOT NULL DEFAULT '0',
  `state` text COLLATE utf8_bin,
  `eliteState` text COLLATE utf8_bin,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  `hasGemPrice` int(11) NOT NULL DEFAULT '0',
  `itemOneRate` double(16,4) NOT NULL DEFAULT '0.0000',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `map_statistics` */

CREATE TABLE `map_statistics` (
  `playerId` int(11) NOT NULL,
  `mapId` int(11) NOT NULL DEFAULT '0',
  `fightTimes` int(11) NOT NULL DEFAULT '0',
  `averageTime` int(11) NOT NULL DEFAULT '0',
  `winRate` int(11) NOT NULL DEFAULT '0',
  `equipRate` int(11) NOT NULL DEFAULT '0',
  `expRate` int(11) NOT NULL DEFAULT '0',
  `coinRate` int(11) NOT NULL DEFAULT '0',
  `mapExpRatio` int(11) NOT NULL DEFAULT '0',
  `mapCoinRatio` int(11) NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  `itemOneRate` double(16,4) NOT NULL DEFAULT '0.0000',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `mercenaryexpedition` */

CREATE TABLE `mercenaryexpedition` (
  `playerId` int(11) NOT NULL,
  `refreshCount` int(11) DEFAULT '0',
  `dispatchCount` int(11) DEFAULT '0',
  `expeditionTaskStr` varchar(1024) COLLATE utf8_bin DEFAULT '',
  `lucky` int(11) DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

/*Table structure for table `mission` */

CREATE TABLE `mission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `playerId` int(11) DEFAULT '0',
  `currentCount` varchar(2048) COLLATE utf8_bin DEFAULT NULL,
  `completeCount` varchar(2048) COLLATE utf8_bin DEFAULT NULL,
  `keepCount` varchar(4096) COLLATE utf8_bin DEFAULT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `PLAYERID_QUERY_INDEX` (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `msg` */

CREATE TABLE `msg` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `moduleId` int(11) NOT NULL DEFAULT '0',
  `senderId` int(11) NOT NULL DEFAULT '0',
  `recverId` int(11) NOT NULL DEFAULT '0',
  `content` varchar(512) COLLATE utf8_bin NOT NULL DEFAULT '',
  `senderSkinId` int(11) NOT NULL DEFAULT '0',
  `csSenderIdentify` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `csSenderInfo` varchar(256) COLLATE utf8_bin NOT NULL DEFAULT '',
  `msgType` int(11) NOT NULL DEFAULT '0',
  `createSysTime` int(11) unsigned NOT NULL DEFAULT '0',
  `lastReadTime` int(11) unsigned NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  `jsonType` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `senderIdIndex` (`senderId`) USING BTREE,
  KEY `recverIdIndex` (`recverId`) USING BTREE,
  KEY `invalid_index` (`invalid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `multi_elite_info` */

CREATE TABLE `multi_elite_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `multiMapStr` varchar(512) DEFAULT NULL,
  `multiLuckRoleStr` varchar(512) DEFAULT NULL,
  `nextRefreshTime` bigint(50) DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `invalid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `multi_elite_report` */

CREATE TABLE `multi_elite_report` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `battleResult` mediumblob,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

/*Table structure for table `player` */

CREATE TABLE `player` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `puid` varchar(255) COLLATE utf8_bin NOT NULL,
  `pwd` varchar(255) COLLATE utf8_bin DEFAULT '888888' COMMENT 'password',
  `name` varchar(255) COLLATE utf8_bin NOT NULL,
  `rebirthStage` int(11) DEFAULT '0',
  `level` int(11) NOT NULL DEFAULT '0',
  `exp` bigint(20) DEFAULT NULL,
  `gold` int(11) NOT NULL DEFAULT '0',
  `rmbGold` int(11) NOT NULL DEFAULT '0',
  `coin` bigint(20) NOT NULL DEFAULT '0',
  `goldBean` int(11) NOT NULL DEFAULT '0',
  `exchangeGoldBeanCostrmbGold` int(11) NOT NULL DEFAULT '0',
  `recharge` int(11) NOT NULL DEFAULT '0',
  `webRecharge` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'web棣栧厖',
  `gameRecharge` tinyint(2) NOT NULL DEFAULT '0',
  `isguest` tinyint(2) DEFAULT '0' COMMENT '0.web;1.guest;2.綁定',
  `vipLevel` int(11) NOT NULL DEFAULT '0',
  `smeltValue` int(11) NOT NULL DEFAULT '0',
  `honorValue` int(11) NOT NULL DEFAULT '0',
  `reputationValue` int(11) NOT NULL DEFAULT '0',
  `skillEnhanceOpen` int(11) NOT NULL DEFAULT '0',
  `questStep` int(11) NOT NULL DEFAULT '0',
  `fightValue` int(11) NOT NULL DEFAULT '0',
  `prof` int(11) NOT NULL DEFAULT '0',
  `signature` varchar(256) CHARACTER SET utf8mb4 DEFAULT NULL,
  `device` varchar(256) COLLATE utf8_bin NOT NULL DEFAULT '',
  `platform` varchar(256) COLLATE utf8_bin NOT NULL DEFAULT '',
  `phoneInfo` varchar(256) COLLATE utf8_bin NOT NULL DEFAULT '',
  `forbidenTime` timestamp NULL DEFAULT NULL,
  `silentTime` timestamp NULL DEFAULT NULL,
  `loginTime` timestamp NULL DEFAULT NULL,
  `logoutTime` timestamp NULL DEFAULT NULL,
  `resetTime` timestamp NULL DEFAULT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  `langArea` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `serverId` int(11) NOT NULL DEFAULT '0',
  `loginDay` int(11) DEFAULT '0',
  `avatarId` int(11) NOT NULL DEFAULT '0',
  `crystal` int(11) NOT NULL DEFAULT '0',
  `googleAchieve` varchar(1024) COLLATE utf8_bin DEFAULT '',
  `isComment` varchar(1024) COLLATE utf8_bin DEFAULT '',
  `payMoney` double(16,4) NOT NULL DEFAULT '0.0000',
  `rechargeSoul` int(11) NOT NULL DEFAULT '0',
  `todayRechargeNum` int(11) NOT NULL DEFAULT '0',
  `activeValue` int(11) DEFAULT '0',
  `poolRateStr` varchar(100) COLLATE utf8_bin DEFAULT '',
  `cdkCode` varchar(200) COLLATE utf8_bin DEFAULT '',
  `headIcon` int(11) DEFAULT '0',
  `buyIconList` varchar(1024) COLLATE utf8_bin DEFAULT NULL,
  `mergeTime` int(11) DEFAULT '0' COMMENT '合服时间重置时间',
  `headIconStr` varchar(255) COLLATE utf8_bin NOT NULL,
  `heroDataStr` varchar(255) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `puid` (`puid`),
  KEY `puid_index` (`puid`) USING BTREE,
  KEY `name_index` (`name`) USING BTREE,
  KEY `device_index` (`device`(255)) USING BTREE,
  KEY `platform_index` (`platform`(255)) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `player_activity` */

CREATE TABLE `player_activity` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `playerId` int(11) NOT NULL,
  `activityId` int(11) NOT NULL,
  `stageId` int(11) NOT NULL,
  `statusStr` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `playerIdIndex` (`playerId`) USING HASH,
  KEY `activityIdIndex` (`activityId`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `player_alliance` */

CREATE TABLE `player_alliance` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `allianceId` int(11) DEFAULT '0',
  `playerId` int(11) DEFAULT '0',
  `contribution` int(11) DEFAULT '0',
  `postion` int(11) DEFAULT '0',
  `autoFight` int(11) NOT NULL DEFAULT '0',
  `reportTime` bigint(20) DEFAULT '0',
  `shopStr` varchar(2048) DEFAULT NULL,
  `shopTime` bigint(20) DEFAULT '0',
  `luckyScore` int(11) NOT NULL DEFAULT '0',
  `refreshShopCount` int(11) NOT NULL DEFAULT '0',
  `shopItemsStr` varchar(2048) DEFAULT NULL,
  `exitTime` bigint(20) DEFAULT '0',
  `joinTime` bigint(20) DEFAULT '0',
  `vitality` int(11) DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  `addVitalityTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `applyAllianceData` varchar(1024) DEFAULT '',
  `everydayContribution` int(11) NOT NULL DEFAULT '0',
  `refreshTime` bigint(20) NOT NULL DEFAULT '0',
  `dailyDonateStr` varchar(100) DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `player_archive` */

CREATE TABLE `player_archive` (
  `playerId` int(11) NOT NULL DEFAULT '0',
  `fetterStr` varchar(1024) COLLATE utf8mb4_bin NOT NULL DEFAULT '0',
  `albumStr` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

/*Table structure for table `player_facebook_share` */

CREATE TABLE `player_facebook_share` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `puid` varchar(256) COLLATE utf8_bin NOT NULL,
  `serverId` int(11) DEFAULT '0',
  `count` int(11) DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `puid` (`puid`(255)) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

/*Table structure for table `player_prince_devils` */

CREATE TABLE `player_prince_devils` (
  `playerId` int(11) DEFAULT NULL,
  `score` int(11) DEFAULT '0',
  `scoreExchangeStr` varchar(1024) DEFAULT '',
  `refreshTime` bigint(20) DEFAULT '0',
  `stageId` int(11) DEFAULT '0',
  `count` int(11) DEFAULT '0',
  `freeTime` int(11) DEFAULT '0',
  `luckCount` int(11) DEFAULT '0',
  `rewardInfoStr` varchar(1024) DEFAULT '',
  `createTime` timestamp NOT NULL DEFAULT '1999-12-31 16:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  `poolRateStr` varchar(1024) DEFAULT '',
  UNIQUE KEY `playerId` (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `player_snapshot` */

CREATE TABLE `player_snapshot` (
  `playerId` int(11) NOT NULL,
  `snapshot` mediumblob,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `player_star_soul` */

CREATE TABLE `player_star_soul` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `playerId` int(11) NOT NULL,
  `starSoul` varchar(2048) NOT NULL DEFAULT '',
  `spriteSoul` varchar(2048) NOT NULL DEFAULT '',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `playerId_index` (`playerId`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `player_talent` */

CREATE TABLE `player_talent` (
  `playerId` int(11) NOT NULL DEFAULT '0',
  `talentNum` int(11) DEFAULT '0',
  `elementAttr` varchar(2048) COLLATE utf8_bin DEFAULT NULL,
  `createTime` timestamp NULL DEFAULT NULL,
  `updateTime` timestamp NULL DEFAULT NULL,
  `invalid` int(11) DEFAULT NULL,
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `player_wing` */

CREATE TABLE `player_wing` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `playerId` int(11) NOT NULL DEFAULT '0',
  `level` int(11) NOT NULL DEFAULT '0',
  `starTime` bigint(20) NOT NULL DEFAULT '0',
  `whiteTime` bigint(20) NOT NULL DEFAULT '0',
  `greenTime` bigint(20) NOT NULL DEFAULT '0',
  `blueTime` bigint(20) NOT NULL DEFAULT '0',
  `purpleTime` bigint(20) NOT NULL DEFAULT '0',
  `orangeTime` bigint(20) NOT NULL DEFAULT '0',
  `luckyNum` int(11) NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

/*Table structure for table `player_world_boss` */

CREATE TABLE `player_world_boss` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `worldBossId` int(11) NOT NULL,
  `playerId` int(11) DEFAULT '0',
  `allianceId` int(11) DEFAULT '0',
  `attackTimes` int(11) DEFAULT '0',
  `harm` bigint(20) DEFAULT '0',
  `hurt` int(11) DEFAULT '0',
  `Attack` int(11) DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `worldBossId` (`worldBossId`) USING BTREE,
  KEY `playerId` (`playerId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

/*Table structure for table `profrank` */

CREATE TABLE `profrank` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `createTime` datetime NOT NULL,
  `fightvalue` int(11) DEFAULT NULL,
  `invalid` bit(1) DEFAULT NULL,
  `playerId` int(11) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `updateTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `quest` */

CREATE TABLE `quest` (
  `playerId` int(10) unsigned NOT NULL,
  `questStr` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `recharge` */

CREATE TABLE `recharge` (
  `orderSerial` varchar(255) COLLATE utf8_bin NOT NULL,
  `puid` varchar(256) COLLATE utf8_bin NOT NULL,
  `serverId` int(11) DEFAULT '0',
  `playerId` int(11) NOT NULL DEFAULT '0',
  `goodsId` int(11) NOT NULL DEFAULT '0',
  `goodsCount` int(11) NOT NULL DEFAULT '0',
  `goodsCost` int(11) NOT NULL DEFAULT '0',
  `currency` varchar(64) COLLATE utf8_bin NOT NULL,
  `addGold` int(11) NOT NULL DEFAULT '0',
  `isFirstPay` int(11) NOT NULL DEFAULT '0',
  `level` int(11) NOT NULL DEFAULT '0',
  `vipLevel` int(11) NOT NULL DEFAULT '0',
  `device` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `platform` varchar(64) COLLATE utf8_bin NOT NULL,
  `isTest` int(11) DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '1999-12-31 16:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  UNIQUE KEY `orderSerial` (`orderSerial`),
  KEY `puid_index` (`puid`(255)) USING BTREE,
  KEY `playerId_index` (`playerId`) USING BTREE,
  KEY `level_index` (`level`) USING BTREE,
  KEY `device_index` (`device`(255)) USING BTREE,
  KEY `platform_index` (`platform`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `recharge_dailycollet` */

CREATE TABLE `recharge_dailycollet` (
  `orderSerial` varchar(255) COLLATE utf8_bin NOT NULL,
  `puid` varchar(256) COLLATE utf8_bin NOT NULL,
  `serverId` int(11) DEFAULT '0',
  `playerId` int(11) NOT NULL DEFAULT '0',
  `goodsCost` int(11) NOT NULL DEFAULT '0',
  `currency` varchar(64) COLLATE utf8_bin NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '1999-12-31 16:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  `platform` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`orderSerial`),
  KEY `puid_index` (`puid`(255)) USING BTREE,
  KEY `playerId_index` (`playerId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `role` */

CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `playerId` int(11) NOT NULL DEFAULT '0',
  `type` int(11) NOT NULL DEFAULT '0',
  `itemId` int(11) NOT NULL DEFAULT '0',
  `name` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `fightvalue` int(11) NOT NULL DEFAULT '0',
  `attr` int(11) NOT NULL DEFAULT '0',
  `rebirthStage` int(11) DEFAULT '0',
  `level` int(11) NOT NULL DEFAULT '0',
  `exp` bigint(20) DEFAULT NULL,
  `roleState` int(11) NOT NULL DEFAULT '0',
  `soulCount` int(11) NOT NULL DEFAULT '0',
  `equip1` bigint(20) NOT NULL DEFAULT '0',
  `equip2` bigint(20) NOT NULL DEFAULT '0',
  `equip3` bigint(20) NOT NULL DEFAULT '0',
  `equip4` bigint(20) NOT NULL DEFAULT '0',
  `equip5` bigint(20) NOT NULL DEFAULT '0',
  `equip6` bigint(20) NOT NULL DEFAULT '0',
  `equip7` bigint(20) NOT NULL DEFAULT '0',
  `equip8` bigint(20) NOT NULL DEFAULT '0',
  `equip9` bigint(20) NOT NULL DEFAULT '0',
  `equip10` bigint(20) NOT NULL DEFAULT '0',
  `skill1` int(11) NOT NULL DEFAULT '0',
  `skill2` int(11) NOT NULL DEFAULT '0',
  `skill3` int(11) NOT NULL DEFAULT '0',
  `skill4` int(11) NOT NULL DEFAULT '0',
  `skill5` int(11) DEFAULT '0',
  `attrInfo` varchar(2048) COLLATE utf8_bin NOT NULL DEFAULT '',
  `skill2idListStr` varchar(2048) COLLATE utf8_bin DEFAULT '',
  `skill3idListStr` varchar(2048) COLLATE utf8_bin DEFAULT '',
  `starExp` int(11) NOT NULL DEFAULT '0',
  `starLevel` int(11) NOT NULL DEFAULT '0',
  `stageLevel` int(11) NOT NULL DEFAULT '0',
  `stageLevel2` int(11) NOT NULL DEFAULT '0',
  `ringStr` varchar(512) COLLATE utf8_bin DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  `power` int(11) NOT NULL,
  `refreshTime` bigint(20) NOT NULL,
  `broadcasted` int(11) NOT NULL DEFAULT '0',
  `roleBaptizeAttrStr` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  `elements` varchar(2048) COLLATE utf8_bin DEFAULT '0,0,0,0,0,0,0',
  `badge1` bigint(20) NOT NULL DEFAULT '0',
  `badge2` bigint(20) NOT NULL DEFAULT '0',
  `badge3` bigint(20) NOT NULL DEFAULT '0',
  `badge4` bigint(20) NOT NULL DEFAULT '0',
  `badge5` bigint(20) NOT NULL DEFAULT '0',
  `badge6` bigint(20) NOT NULL DEFAULT '0',
  `memories` int(11) DEFAULT NULL,
  `skinId` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `playerId_index` (`playerId`) USING BTREE,
  KEY `name_index` (`name`(255)) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `role_ring` */

CREATE TABLE `role_ring` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `playerId` int(11) NOT NULL DEFAULT '0',
  `roleId` int(11) NOT NULL DEFAULT '0',
  `itemId` int(11) NOT NULL DEFAULT '0',
  `exp` int(11) NOT NULL DEFAULT '0',
  `lvlUpTimes` int(11) NOT NULL DEFAULT '0',
  `level` int(11) NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `playerId_index` (`playerId`) USING BTREE,
  KEY `roleId_index` (`roleId`) USING BTREE,
  KEY `itemId_index` (`itemId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `server_timelimit` */

CREATE TABLE `server_timelimit` (
  `stageId` int(11) NOT NULL,
  `buyMapStr` varchar(2048) DEFAULT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`stageId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

/*Table structure for table `serverdata` */

CREATE TABLE `serverdata` (
  `id` int(11) NOT NULL,
  `statusStr` varchar(4096) COLLATE utf8_bin DEFAULT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

/*Table structure for table `sevendayquest` */

CREATE TABLE `sevendayquest` (
  `playerId` int(11) NOT NULL,
  `questStr` text,
  `pointStateStr` varchar(3072) DEFAULT NULL,
  `point` int(11) DEFAULT NULL,
  `awardstate` smallint(6) DEFAULT '0',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `invalid` smallint(6) DEFAULT NULL,
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `shoot_activity` */

CREATE TABLE `shoot_activity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `shootRefreshTime` int(11) NOT NULL,
  `shootState` int(11) NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `shop` */

CREATE TABLE `shop` (
  `playerId` int(11) NOT NULL DEFAULT '0',
  `refreshDate` date DEFAULT NULL,
  `refreshTimes` int(112) NOT NULL DEFAULT '0',
  `shopItems` varchar(2048) COLLATE utf8_bin DEFAULT '',
  `shopLuckValue` int(11) NOT NULL DEFAULT '0',
  `buyCoinCount` int(11) NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `skill` */

CREATE TABLE `skill` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `playerId` int(11) NOT NULL DEFAULT '0',
  `roleId` int(11) NOT NULL DEFAULT '0',
  `itemId` int(11) NOT NULL DEFAULT '0',
  `skillLevel` int(11) NOT NULL DEFAULT '0',
  `status` int(11) NOT NULL DEFAULT '0',
  `exp` int(11) NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `status` */

CREATE TABLE `status` (
  `playerId` int(11) NOT NULL,
  `platformData` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `autoSellEquip` int(11) NOT NULL DEFAULT '0',
  `autoDecoElement` int(11) DEFAULT '0',
  `chatClose` int(11) NOT NULL DEFAULT '0',
  `fastFightTimes` int(11) NOT NULL DEFAULT '0',
  `fastFightBuyTimes` int(11) NOT NULL DEFAULT '0',
  `bossFightTimes` int(11) NOT NULL DEFAULT '0',
  `bossFightBuyTimes` int(11) NOT NULL DEFAULT '0',
  `eliteMapTimes` int(11) NOT NULL DEFAULT '0',
  `eliteMapBuyTimes` int(11) NOT NULL DEFAULT '0',
  `nextBattleTime` int(11) NOT NULL DEFAULT '0',
  `currMapId` int(11) NOT NULL DEFAULT '0',
  `passMapId` int(11) NOT NULL DEFAULT '0',
  `passEliteMapId` int(11) NOT NULL DEFAULT '0',
  `equipSmeltCreate` varchar(2048) COLLATE utf8_bin NOT NULL DEFAULT '',
  `itemSmeltCreate` varchar(2048) COLLATE utf8_bin NOT NULL DEFAULT '',
  `equipSmeltRefesh` int(11) NOT NULL DEFAULT '0',
  `arenaBuyTimes` int(11) NOT NULL DEFAULT '0',
  `arenaLastBuyTime` int(11) NOT NULL DEFAULT '0',
  `surplusChallengeTimes` int(11) NOT NULL DEFAULT '0',
  `equipBagSize` int(11) NOT NULL DEFAULT '0',
  `equipBagExtendTimes` int(11) NOT NULL DEFAULT '0',
  `cdkeyType` varchar(512) COLLATE utf8_bin DEFAULT NULL,
  `giftStatus` int(11) NOT NULL DEFAULT '0',
  `wipeBoss` int(11) NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  `gongceWordDay` timestamp NULL DEFAULT NULL,
  `music` int(11) NOT NULL DEFAULT '10',
  `sound` int(11) NOT NULL DEFAULT '10',
  `showArea` int(11) NOT NULL DEFAULT '1',
  `newSerGiftRewardCount` int(11) NOT NULL DEFAULT '0',
  `itemLuck` varchar(2048) COLLATE utf8_bin DEFAULT NULL,
  `latestBattleType` int(11) NOT NULL DEFAULT '0',
  `starStoneTimes` int(11) NOT NULL DEFAULT '0',
  `newGuideState` int(11) NOT NULL DEFAULT '2',
  `evaluateRewardsState` int(11) NOT NULL DEFAULT '0',
  `newbieRewardMonthCard` int(11) NOT NULL DEFAULT '0',
  `resetTimeMapStr` varchar(2048) COLLATE utf8_bin DEFAULT NULL,
  `onlyText` int(11) NOT NULL DEFAULT '0',
  `worldBossAutoState` int(11) DEFAULT '0',
  `worldBossBuffFreeTimes` int(11) DEFAULT '0',
  `passMaxMultiEliteId` int(11) NOT NULL DEFAULT '0',
  `multiEliteTimes` int(11) NOT NULL DEFAULT '0',
  `challengeStar` int(11) NOT NULL DEFAULT '1',
  `multiFirstBattle` int(11) NOT NULL,
  `multiBattleInfoStr` varchar(1024) COLLATE utf8_bin DEFAULT NULL,
  `multiGiftInfoStr` varchar(1024) COLLATE utf8_bin DEFAULT '',
  `todayBuyMultiEliteTimes` int(11) NOT NULL DEFAULT '0',
  `lastRefreshMultiEliteTime` bigint(20) NOT NULL DEFAULT '0',
  `multiEliteScore` int(11) NOT NULL DEFAULT '0',
  `multiEliteHistoryScore` int(11) NOT NULL DEFAULT '0',
  `lastShowMultiEliteResultId` int(11) NOT NULL DEFAULT '0',
  `askTickIds` varchar(1024) COLLATE utf8_bin DEFAULT NULL,
  `todayRefreshHireListTimes` int(11) NOT NULL DEFAULT '0',
  `elementBagSize` int(11) DEFAULT '0',
  `accountBoundStatus` int(11) NOT NULL DEFAULT '0',
  `maxArenaRecordId` int(11) DEFAULT '0',
  `largessGoldTime` int(4) NOT NULL DEFAULT '0',
  `lastlargessDay` timestamp NOT NULL DEFAULT '2014-01-01 00:00:00',
  `iosGetState` int(11) NOT NULL,
  `firstFastBattle` int(11) NOT NULL DEFAULT '0',
  `roleFirstFastBattle` int(11) NOT NULL DEFAULT '0',
  `exchangeCount` int(11) NOT NULL DEFAULT '0',
  `firstBattle` int(11) NOT NULL DEFAULT '0',
  `firstFalse` int(11) NOT NULL DEFAULT '0',
  `firstSuccess` int(11) NOT NULL DEFAULT '0',
  `fontSize` int(11) NOT NULL DEFAULT '0',
  `gemShopBuyCount` int(11) NOT NULL DEFAULT '0',
  `guideStr` varchar(1024) COLLATE utf8_bin DEFAULT '',
  `hourCardUseCountOneDay` int(11) NOT NULL DEFAULT '0',
  `lastExchangeBeanTime` bigint(20) NOT NULL DEFAULT '0',
  `lastResetExchangeBeanTime` bigint(20) DEFAULT '0',
  `totalFastFightCount` int(11) DEFAULT '0',
  `lastSnapBattle` mediumblob,
  `exchangeStr` varchar(1024) COLLATE utf8_bin DEFAULT NULL,
  `todayLoginCount` int(11) DEFAULT '0',
  `badgeBagSize` int(11) NOT NULL DEFAULT '50',
  `badgeBagExtendTimes` int(11) NOT NULL DEFAULT '0',
  `intoLevelTime` int(11) DEFAULT '0',
  `lastTakeBattleAwardTime` int(11) DEFAULT '0',
  `playstory` int(11) DEFAULT '0',
  `newbieDate` timestamp NULL DEFAULT NULL,
  `friendship` int(11) DEFAULT '0',
  `vipPoint` int(11) DEFAULT '0',
  `fristLoginTimes` int(11) DEFAULT '0',
  `friendship` int(11) DEFAULT '0',
  `accConsumeGold` int(11) DEFAULT '0',
  `arenaWinTimes` int(11) DEFAULT '0',
  `ordealFloor` int(11) DEFAULT '0',
  `miningLevel` int(11) DEFAULT '0',
  `secretMsg` varchar(2048) NOT NULL DEFAULT '',
  `lastMsgTime` int(11) DEFAULT '0',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `team` */

CREATE TABLE `team` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `captainId` int(11) NOT NULL,
  `teamMemberStr` varchar(256) COLLATE utf8_bin NOT NULL DEFAULT '',
  `stageId` int(10) unsigned NOT NULL DEFAULT '0',
  `totalFight` int(10) unsigned NOT NULL DEFAULT '0',
  `round` int(10) NOT NULL DEFAULT '0',
  `isWeedOut` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `kickTimes` int(10) unsigned NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `team_battle_cache` */

CREATE TABLE `team_battle_cache` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `battleState` int(11) NOT NULL DEFAULT '0',
  `waitPlayerIdStr` text COLLATE utf8_bin NOT NULL,
  `nextRoundTeamsAgainstPlanStr` text COLLATE utf8_bin NOT NULL,
  `stageId` int(11) NOT NULL DEFAULT '0',
  `lastSaveTime` int(10) NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `team_battle_report` */

CREATE TABLE `team_battle_report` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `stageId` int(11) NOT NULL,
  `leftTeamId` int(11) NOT NULL,
  `rightTeamId` int(11) NOT NULL,
  `round` int(11) NOT NULL,
  `teamRoundInfo` blob NOT NULL,
  `result` int(11) NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `stageIdIndex` (`stageId`) USING BTREE,
  KEY `leftTeamIdIndex` (`leftTeamId`) USING BTREE,
  KEY `rightTeamIdIndex` (`rightTeamId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `title` */

CREATE TABLE `title` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `playerid` int(10) unsigned NOT NULL,
  `finishIds` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `useId` int(10) NOT NULL DEFAULT '0',
  `ischange` int(10) NOT NULL DEFAULT '0',
  `teambattlechampiondate` timestamp NULL DEFAULT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `ur_rank_activity` */

CREATE TABLE `ur_rank_activity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `refreshTime` bigint(20) DEFAULT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `invalid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `ur_rank_history` */

CREATE TABLE `ur_rank_history` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `awardlist` mediumtext,
  `ranklist` mediumtext,
  `stageId` int(11) NOT NULL,
  `createTime` datetime NOT NULL,
  `updateTime` datetime DEFAULT NULL,
  `invalid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `vitality_rank` */

CREATE TABLE `vitality_rank` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rank` int(11) NOT NULL,
  `allianceId` int(11) NOT NULL,
  `vitality` int(11) NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `wealth_club` */

CREATE TABLE `wealth_club` (
  `stageId` int(11) DEFAULT '0',
  `wealthData` varchar(1024) DEFAULT '',
  `createTime` timestamp NOT NULL DEFAULT '1999-12-31 16:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `world_boss` */

CREATE TABLE `world_boss` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `startDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `bossNpcId` int(11) DEFAULT '0',
  `currBossHp` bigint(20) DEFAULT '0',
  `maxBossHp` bigint(20) DEFAULT '0',
  `deadTime` bigint(20) DEFAULT '0',
  `lastKillPlayerId` int(11) DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `startDate` (`startDate`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

/*Table structure for table `worship` */

CREATE TABLE `worship` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `playerId` int(11) NOT NULL,
  `worshipStamp` int(11) DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
