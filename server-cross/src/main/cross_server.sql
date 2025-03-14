/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50624
Source Host           : localhost:3306
Source Database       : cross_server

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2017-08-17 19:35:27
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `cross_battle`
-- ----------------------------
DROP TABLE IF EXISTS `cross_battle`;
CREATE TABLE `cross_battle` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `identify` varchar(128) NOT NULL,
  `initiator` varchar(128) NOT NULL,
  `winner` tinyint(1) NOT NULL,
  `scoreChange` int(11) NOT NULL,
  `battle` blob NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of cross_battle
-- ----------------------------

-- ----------------------------
-- Table structure for `player_data`
-- ----------------------------
DROP TABLE IF EXISTS `player_data`;
CREATE TABLE `player_data` (
  `identify` varchar(128) NOT NULL,
  `serverName` varchar(128) NOT NULL,
  `snapshotInfo` blob NOT NULL,
  PRIMARY KEY (`identify`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of player_data
-- ----------------------------

-- ----------------------------
-- Table structure for `rank_data`
-- ----------------------------
DROP TABLE IF EXISTS `rank_data`;
CREATE TABLE `rank_data` (
  `identify` varchar(128) NOT NULL,
  `winTimes` int(11) NOT NULL,
  `score` int(11) NOT NULL,
  `rank` int(11) NOT NULL,
  PRIMARY KEY (`identify`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of rank_data
-- ----------------------------
