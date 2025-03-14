-- MySQL dump 10.13  Distrib 5.7.21, for Linux (x86_64)
--
-- Host: localhost    Database: paynotice
-- ------------------------------------------------------
-- Server version	5.7.21

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `alliance`
--

DROP TABLE IF EXISTS `t_payinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_payinfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `sid` int(11) DEFAULT 0 COMMENT '服务器ID',
  `amount` DECIMAL(20,6) DEFAULT '0' COMMENT '支付金额',
  `rmbRate` DECIMAL(20,6) DEFAULT '0' COMMENT '兑换人民币汇率',
  `uid` varchar(50) DEFAULT '' COMMENT '用户ID',
  `version` varchar(100) DEFAULT '' COMMENT '游戏版本',
  `currency` varchar(10) DEFAULT 'CNY' COMMENT '货币代码',
  `gameName` varchar(50) DEFAULT NULL COMMENT '游戏名称',
  `productName` varchar(255) DEFAULT '0' COMMENT '充值商品名字',
  `os` varchar(50) DEFAULT NULL COMMENT '系统',
  `sdkChannel` varchar(100) DEFAULT NULL COMMENT 'sdk渠道标识',
  `orderNo` varchar(255) DEFAULT NULL COMMENT '订单号',
  `payNoticeUrl` text COMMENT '游戏支付通知url',
  `statusCode` int(11) default '0' COMMENT '通知结果',
  `payTime`  timestamp NOT NULL DEFAULT '2000-01-01 00:00:00' COMMENT '支付通知时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `alliance_battle_info`
--

DROP TABLE IF EXISTS `alliance_battle_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `alliance_battle_item`
--

DROP TABLE IF EXISTS `alliance_battle_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `alliance_fight_unit`
--

DROP TABLE IF EXISTS `alliance_fight_unit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `alliance_fight_versus`
--

DROP TABLE IF EXISTS `alliance_fight_versus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `arena`
--

DROP TABLE IF EXISTS `arena`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `arena` (
  `playerId` int(11) NOT NULL,
  `rank` int(11) NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `arena_report`
--

DROP TABLE IF EXISTS `arena_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `arena_report` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `battleResult` mediumblob NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `avatar`
--

DROP TABLE IF EXISTS `avatar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `camp`
--

DROP TABLE IF EXISTS `camp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `campwar`
--

DROP TABLE IF EXISTS `campwar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `campwar_auto`
--

DROP TABLE IF EXISTS `campwar_auto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `campwar_auto` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `stageId` int(11) NOT NULL DEFAULT '0',
  `autoJoinPlayerIdsStr` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chat_msg`
--

DROP TABLE IF EXISTS `chat_msg`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chat_msg` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `typeId` int(11) unsigned zerofill NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  `msg` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `pos` int(11) unsigned zerofill NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chat_skin`
--

DROP TABLE IF EXISTS `chat_skin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cross_battle`
--

DROP TABLE IF EXISTS `cross_battle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cross_player`
--

DROP TABLE IF EXISTS `cross_player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `crystal_shop`
--

DROP TABLE IF EXISTS `crystal_shop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `daily_statistics`
--

DROP TABLE IF EXISTS `daily_statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dailyquest`
--

DROP TABLE IF EXISTS `dailyquest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `element`
--

DROP TABLE IF EXISTS `element`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `email`
--

DROP TABLE IF EXISTS `email`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=1664 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `equip`
--

DROP TABLE IF EXISTS `equip`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=26264 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `expedition_armory`
--

DROP TABLE IF EXISTS `expedition_armory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `forging_equip`
--

DROP TABLE IF EXISTS `forging_equip`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `forging_equip` (
  `stageId` int(11) DEFAULT '0',
  `totalTimes` int(11) DEFAULT '0',
  `prizeLimit` varchar(1024) DEFAULT '',
  `createTime` timestamp NOT NULL DEFAULT '1999-12-31 16:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `formation`
--

DROP TABLE IF EXISTS `formation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `formation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `playerId` int(11) NOT NULL DEFAULT '0',
  `type` int(4) unsigned zerofill NOT NULL,
  `fightingArray` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=167 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `friend`
--

DROP TABLE IF EXISTS `friend`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `friend` (
  `playerId` int(10) unsigned NOT NULL,
  `friendIds` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `applyFriendIds` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `shieldMapStr` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `dailyMsgPlayerStr` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  `bindingState` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gm_recharge`
--

DROP TABLE IF EXISTS `gm_recharge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=817 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `goldfish_reward`
--

DROP TABLE IF EXISTS `goldfish_reward`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `goldfish_reward` (
  `stageId` int(11) NOT NULL DEFAULT '0',
  `isSendReward` int(11) NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  KEY `stageId` (`stageId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gvg_alliance`
--

DROP TABLE IF EXISTS `gvg_alliance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gvg_city`
--

DROP TABLE IF EXISTS `gvg_city`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gvg_city` (
  `cityId` int(11) NOT NULL,
  `holderId` int(11) NOT NULL,
  `marauderId` int(11) NOT NULL,
  `attackerIds` varchar(1024) DEFAULT NULL,
  `defenderIds` varchar(1024) DEFAULT NULL,
  `fightbackTime` bigint(20) NOT NULL,
  `isFightback` tinyint(1) NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `invalid` tinyint(1) NOT NULL,
  PRIMARY KEY (`cityId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gvg_city_reward`
--

DROP TABLE IF EXISTS `gvg_city_reward`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gvg_city_reward` (
  `allianceId` int(11) NOT NULL,
  `cityIds` varchar(256) DEFAULT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `invalid` tinyint(1) NOT NULL,
  PRIMARY KEY (`allianceId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gvg_log`
--

DROP TABLE IF EXISTS `gvg_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gvg_reward`
--

DROP TABLE IF EXISTS `gvg_reward`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gvg_reward` (
  `playerId` int(11) NOT NULL,
  `received` varchar(128) NOT NULL,
  `refreshTime` bigint(20) NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `invalid` tinyint(1) NOT NULL,
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gvg_time`
--

DROP TABLE IF EXISTS `gvg_time`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gvg_time` (
  `gvgId` int(11) NOT NULL,
  `isFirst` tinyint(1) NOT NULL,
  `refreshTime` bigint(20) NOT NULL,
  `pushState` int(11) NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `invalid` tinyint(1) NOT NULL,
  PRIMARY KEY (`gvgId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `harem_activity`
--

DROP TABLE IF EXISTS `harem_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `harem_activity` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `playerId` int(11) DEFAULT '0',
  `lastCommonFreeTime` bigint(255) DEFAULT '0',
  `commonFreeChance` int(255) DEFAULT '0',
  `commonTotalTimes` int(11) DEFAULT '0',
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hero_token_task`
--

DROP TABLE IF EXISTS `hero_token_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `honor_shop`
--

DROP TABLE IF EXISTS `honor_shop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ip_addr`
--

DROP TABLE IF EXISTS `ip_addr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=861 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `login`
--

DROP TABLE IF EXISTS `login`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=227 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `maiden_encounter_activity`
--

DROP TABLE IF EXISTS `maiden_encounter_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `map`
--

DROP TABLE IF EXISTS `map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `map` (
  `playerId` int(11) NOT NULL DEFAULT '0',
  `state` varchar(8192) COLLATE utf8_bin DEFAULT NULL,
  `eliteState` varchar(4096) COLLATE utf8_bin DEFAULT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  `hasGemPrice` int(11) NOT NULL DEFAULT '0',
  `itemOneRate` double(16,4) NOT NULL DEFAULT '0.0000',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `map_statistics`
--

DROP TABLE IF EXISTS `map_statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mercenaryexpedition`
--

DROP TABLE IF EXISTS `mercenaryexpedition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mission`
--

DROP TABLE IF EXISTS `mission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `msg`
--

DROP TABLE IF EXISTS `msg`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `multi_elite_info`
--

DROP TABLE IF EXISTS `multi_elite_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `multi_elite_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `multiMapStr` varchar(512) DEFAULT NULL,
  `multiLuckRoleStr` varchar(512) DEFAULT NULL,
  `nextRefreshTime` bigint(50) DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `invalid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `multi_elite_report`
--

DROP TABLE IF EXISTS `multi_elite_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `multi_elite_report` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `battleResult` blob NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `player`
--

DROP TABLE IF EXISTS `player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `puid` varchar(255) COLLATE utf8_bin NOT NULL,
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
  PRIMARY KEY (`id`),
  UNIQUE KEY `puid` (`puid`),
  KEY `puid_index` (`puid`) USING BTREE,
  KEY `name_index` (`name`) USING BTREE,
  KEY `device_index` (`device`(255)) USING BTREE,
  KEY `platform_index` (`platform`(255)) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `player_activity`
--

DROP TABLE IF EXISTS `player_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=806 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `player_alliance`
--

DROP TABLE IF EXISTS `player_alliance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `player_archive`
--

DROP TABLE IF EXISTS `player_archive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player_archive` (
  `playerId` int(11) NOT NULL DEFAULT '0',
  `fetterStr` varchar(1024) COLLATE utf8mb4_bin NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `player_facebook_share`
--

DROP TABLE IF EXISTS `player_facebook_share`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player_facebook_share` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `puid` varchar(256) COLLATE utf8_bin NOT NULL,
  `serverId` int(11) DEFAULT '0',
  `count` int(11) DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `puid` (`puid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `player_prince_devils`
--

DROP TABLE IF EXISTS `player_prince_devils`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  UNIQUE KEY `playerId` (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `player_snapshot`
--

DROP TABLE IF EXISTS `player_snapshot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player_snapshot` (
  `playerId` int(11) NOT NULL,
  `snapshot` blob,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `player_star_soul`
--

DROP TABLE IF EXISTS `player_star_soul`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player_star_soul` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `playerId` int(11) NOT NULL,
  `starSoul` varchar(255) NOT NULL DEFAULT '',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `playerId_index` (`playerId`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `player_talent`
--

DROP TABLE IF EXISTS `player_talent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player_talent` (
  `playerId` int(11) NOT NULL DEFAULT '0',
  `talentNum` int(11) DEFAULT '0',
  `elementAttr` varchar(2048) COLLATE utf8_bin DEFAULT NULL,
  `createTime` timestamp NULL DEFAULT NULL,
  `updateTime` timestamp NULL DEFAULT NULL,
  `invalid` int(11) DEFAULT NULL,
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `player_wing`
--

DROP TABLE IF EXISTS `player_wing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `player_world_boss`
--

DROP TABLE IF EXISTS `player_world_boss`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player_world_boss` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `worldBossId` int(11) NOT NULL,
  `playerId` int(11) DEFAULT '0',
  `allianceId` int(11) DEFAULT '0',
  `rebirthTimes` int(11) DEFAULT '0',
  `harm` bigint(20) DEFAULT '0',
  `lastAttackTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `lastRebirthAttackTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `rebirthType` int(11) DEFAULT '0',
  `attackPart` int(11) DEFAULT '0',
  `autoState` int(11) DEFAULT '0',
  `offlineActionTimes` int(11) DEFAULT '0',
  `attackTimes` int(11) DEFAULT '0',
  `buffCfgId` int(11) DEFAULT '0',
  `cacheBuffCfgId` int(11) DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `worldBossId` (`worldBossId`) USING BTREE,
  KEY `playerId` (`playerId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quest`
--

DROP TABLE IF EXISTS `quest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quest` (
  `playerId` int(10) unsigned NOT NULL,
  `questStr` text CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `recharge`
--

DROP TABLE IF EXISTS `recharge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  PRIMARY KEY (`orderSerial`),
  KEY `puid_index` (`puid`(255)) USING BTREE,
  KEY `playerId_index` (`playerId`) USING BTREE,
  KEY `level_index` (`level`) USING BTREE,
  KEY `device_index` (`device`(255)) USING BTREE,
  KEY `platform_index` (`platform`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `playerId` int(11) NOT NULL DEFAULT '0',
  `type` int(11) NOT NULL DEFAULT '0',
  `itemId` int(11) NOT NULL DEFAULT '0',
  `name` varchar(256) COLLATE utf8_bin DEFAULT NULL,
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
  `ringStr` varchar(512) COLLATE utf8_bin DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  `power` int(11) NOT NULL,
  `refreshTime` bigint(20) NOT NULL,
  `hide` int(11) NOT NULL DEFAULT '0',
  `broadcasted` int(11) NOT NULL DEFAULT '0',
  `roleBaptizeAttrStr` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  `elements` varchar(2048) COLLATE utf8_bin DEFAULT '0,0,0,0,0,0,0',
  PRIMARY KEY (`id`),
  KEY `playerId_index` (`playerId`) USING BTREE,
  KEY `name_index` (`name`(255)) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2061 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_ring`
--

DROP TABLE IF EXISTS `role_ring`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `server_timelimit`
--

DROP TABLE IF EXISTS `server_timelimit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `server_timelimit` (
  `stageId` int(11) NOT NULL,
  `buyMapStr` varchar(2048) DEFAULT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`stageId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `serverdata`
--

DROP TABLE IF EXISTS `serverdata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `serverdata` (
  `id` int(11) NOT NULL,
  `statusStr` varchar(4096) COLLATE utf8_bin DEFAULT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `shoot_activity`
--

DROP TABLE IF EXISTS `shoot_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `shoot_activity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `shootRefreshTime` int(11) NOT NULL,
  `shootState` int(11) NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `shop`
--

DROP TABLE IF EXISTS `shop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `skill`
--

DROP TABLE IF EXISTS `skill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=175 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `status`
--

DROP TABLE IF EXISTS `status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  `roleBaptizeAttr` varchar(2048) COLLATE utf8_bin NOT NULL DEFAULT '',
  `roleBaptizeAttrTransferTime` varchar(31) COLLATE utf8_bin NOT NULL DEFAULT '',
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
  `musicOn` int(11) NOT NULL DEFAULT '0',
  `soundOn` int(11) NOT NULL DEFAULT '0',
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
  `multiFirstBattle` int(11) NOT NULL,
  `multiBattleInfoStr` varchar(1024) COLLATE utf8_bin DEFAULT NULL,
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
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `team`
--

DROP TABLE IF EXISTS `team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `team_battle_cache`
--

DROP TABLE IF EXISTS `team_battle_cache`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `team_battle_report`
--

DROP TABLE IF EXISTS `team_battle_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `title`
--

DROP TABLE IF EXISTS `title`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vitality_rank`
--

DROP TABLE IF EXISTS `vitality_rank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vitality_rank` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rank` int(11) NOT NULL,
  `allianceId` int(11) NOT NULL,
  `vitality` int(11) NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `wealth_club`
--

DROP TABLE IF EXISTS `wealth_club`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `wealth_club` (
  `stageId` int(11) DEFAULT '0',
  `wealthData` varchar(1024) DEFAULT '',
  `createTime` timestamp NOT NULL DEFAULT '1999-12-31 16:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `world_boss`
--

DROP TABLE IF EXISTS `world_boss`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `world_boss` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `startDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `bossNpcId` int(11) DEFAULT '0',
  `name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `level` int(11) DEFAULT '1',
  `clonePlayerId` int(11) DEFAULT '0',
  `arenaNpcId` int(11) DEFAULT '0',
  `currBossHp` bigint(20) DEFAULT '0',
  `maxBossHp` bigint(20) DEFAULT '0',
  `deadTime` bigint(20) DEFAULT '0',
  `lastKillPlayerId` int(11) DEFAULT '0',
  `hpMultiple` int(11) DEFAULT '0',
  `failingInfo` varchar(512) COLLATE utf8_bin DEFAULT NULL,
  `failingStartTime` bigint(20) DEFAULT '0',
  `nextFailingTime` bigint(20) DEFAULT '0',
  `isFailing` int(11) DEFAULT '1',
  `curFailingBuffCfg` int(11) DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `startDate` (`startDate`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=165 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `worship`
--

DROP TABLE IF EXISTS `worship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `worship` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `playerId` int(11) NOT NULL,
  `worshipStamp` int(11) DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-04-27 18:13:56
