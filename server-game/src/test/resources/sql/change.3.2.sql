DROP TABLE IF EXISTS `shoot_activity`;
CREATE TABLE `shoot_activity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `shootRefreshTime` int(11) NOT NULL,
  `shootState` int(11) NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `invalid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


---------------------------------------------------------------2016年11月8日------------------------------------------------------------
ALTER TABLE `status`
ADD COLUMN `iosGetState`  int(11) NOT NULL AFTER `gemShopBuyCount`;

create table wealth_club（
	stageId int(11) DEFAULT 0,
	wealthData varchar(1024) DEFAULT '',
	createTime timestamp NOT NULL DEFAULT '2000-01-01 00:00:00',
  	updateTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  	invalid int(11) NOT NULL DEFAULT '0'
）ENGINE=InnoDB DEFAULT CHARSET=utf8;