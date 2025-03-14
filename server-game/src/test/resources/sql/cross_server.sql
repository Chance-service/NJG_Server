DROP TABLE IF EXISTS `cross_battle`;
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

DROP TABLE IF EXISTS `cross_player`;
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
