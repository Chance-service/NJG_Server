SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS `gvg_alliance`;
CREATE TABLE `gvg_alliance` (
`allianceId`  int(11) NOT NULL ,
`declareTimes`  int(11) NOT NULL ,
`addCount`  int(11) NOT NULL ,
`createTime`  timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ,
`updateTime`  timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP ,
`invalid`  tinyint(1) NOT NULL ,
PRIMARY KEY (`allianceId`)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4;

DROP TABLE IF EXISTS `gvg_city`;
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

DROP TABLE IF EXISTS `gvg_city_reward`;
CREATE TABLE `gvg_city_reward` (
`allianceId`  int(11) NOT NULL ,
`cityIds`  varchar(256) DEFAULT NULL ,
`createTime`  timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ,
`updateTime`  timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP ,
`invalid`  tinyint(1) NOT NULL ,
PRIMARY KEY (`allianceId`)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4;

DROP TABLE IF EXISTS `gvg_log`;
CREATE TABLE `gvg_log` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`attackerName`  varchar(256) NOT NULL ,
`defenderName`  varchar(256) DEFAULT NULL ,
`result`  int(11) NOT NULL ,
`cityId`  int(11) NOT NULL ,
`isFightback` tinyint(1) NOT NULL,
`createTime`  timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ,
`updateTime`  timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP ,
`invalid`  tinyint(1) NOT NULL ,
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4;

DROP TABLE IF EXISTS `gvg_reward`;
CREATE TABLE `gvg_reward` (
  `playerId` int(11) NOT NULL,
  `received` varchar(128) NOT NULL,
  `refreshTime` bigint(20) NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `invalid` tinyint(1) NOT NULL,
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `gvg_time`;
CREATE TABLE `gvg_time` (
`gvgId`  int(11) NOT NULL ,
`isFirst`  tinyint(1) NOT NULL ,
`refreshTime`  bigint(20) NOT NULL ,
`pushState`  int(11) NOT NULL ,
`createTime`  timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ,
`updateTime`  timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP ,
`invalid`  tinyint(1) NOT NULL ,
PRIMARY KEY (`gvgId`)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4;

ALTER TABLE `role` ADD COLUMN `power`  int(11) NOT NULL AFTER `status`;
ALTER TABLE `role` ADD COLUMN `refreshTime`  bigint(20) NOT NULL AFTER `power`;

DROP TABLE IF EXISTS `vitality_rank`;
CREATE TABLE `vitality_rank` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`rank`  int(11) NOT NULL ,
`allianceId`  int(11) NOT NULL ,
`vitality`  int(11) NOT NULL ,
`createTime`  timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ,
`updateTime`  timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP ,
`invalid`  tinyint(1) NOT NULL ,
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4;

SET FOREIGN_KEY_CHECKS=1;