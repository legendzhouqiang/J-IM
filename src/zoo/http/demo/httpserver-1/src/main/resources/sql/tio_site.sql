/*
Navicat MySQL Data Transfer

Source Server         : 112.74.183.177
Source Server Version : 50718
Source Host           : 112.74.183.177:3306
Source Database       : tio_site

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2017-07-27 09:05:35
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for blog
-- ----------------------------
DROP TABLE IF EXISTS `blog`;
CREATE TABLE `blog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `loginname` varchar(32) NOT NULL,
  `pwd` varchar(64) NOT NULL,
  `salt` varchar(16) NOT NULL,
  `nick` varchar(16) NOT NULL,
  `avatar` varchar(64) CHARACTER SET utf8 NOT NULL,
  `ip` varchar(16) CHARACTER SET utf8 NOT NULL COMMENT '注册ip',
  `createtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` tinyint(2) NOT NULL DEFAULT '1' COMMENT '用户状态, 1：正常，2：注销，3：被拉黑',
  PRIMARY KEY (`id`),
  KEY `loginname` (`loginname`,`pwd`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of blog
-- ----------------------------

-- ----------------------------
-- Table structure for donate
-- ----------------------------
DROP TABLE IF EXISTS `donate`;
CREATE TABLE `donate` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `amount` double NOT NULL,
  `name` varchar(16) NOT NULL,
  `url` varchar(128) DEFAULT NULL COMMENT '用户或公司的url',
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `way` varchar(32) DEFAULT NULL,
  `remark` varchar(256) DEFAULT NULL,
  `leavemsg` varchar(1024) DEFAULT NULL COMMENT '留言',
  `myremark` varchar(255) DEFAULT NULL COMMENT '给作者自己的备注，不对外显示',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of donate
-- ----------------------------
INSERT INTO `donate` VALUES ('1', '20', '马文龙', null, '2016-12-13 10:02:10', '码云', 'qq：237903488', '感谢您的开源项目！希望多给些详细的文档 我的 qq是237903488', null);
INSERT INTO `donate` VALUES ('2', '10', '坏小孩', null, '2016-12-13 10:19:37', '码云', null, '感谢您的开源项目！', null);
INSERT INTO `donate` VALUES ('3', '10', 'draper', 'https://gitee.com/websterlu', '2016-12-13 10:20:35', '码云', 'qq：10558813', '感谢您的开源项目！', null);
INSERT INTO `donate` VALUES ('4', '20', '莉莉', null, '2016-12-13 10:25:16', '码云', '南京皓叶腾信息科技有限公司CEO，也是作者曾经的同事，长期为t-io免费提供服务器支持', '再接再厉	', null);
INSERT INTO `donate` VALUES ('5', '5', '钛合金核桃', 'https://gitee.com/wu1g119', '2016-12-22 15:00:53', '码云', null, '虽然没用过不过看起来很厉害', null);
INSERT INTO `donate` VALUES ('6', '5', '爱犯迷糊的meallon', 'https://gitee.com/meallon', '2016-12-22 17:07:16', '码云', 'qq：376487342，作者前同事', '感谢您的开源项目！', null);
INSERT INTO `donate` VALUES ('7', '10', 'lihc超哥', 'https://gitee.com/lihc2015', '2017-01-02 17:58:17', '码云', null, '感谢您的开源项目！向你学习NIO，设计开发IM这类的系统需要注意哪些点', null);
INSERT INTO `donate` VALUES ('8', '5', 'WhatAKitty', 'https://gitee.com/wustart', '2017-01-08 00:07:37', '码云', null, '感谢您的开源项目！', null);
INSERT INTO `donate` VALUES ('9', '5', 'mahengyang', 'https://gitee.com/enyo', '2017-01-14 20:35:21', '码云', null, '感谢您的开源项目！', null);
INSERT INTO `donate` VALUES ('10', '10', 'commonrpc', 'https://gitee.com/284520459', '2017-01-19 16:55:37', '码云', 'commonrpc作者', '感谢您的开源项目！', null);
INSERT INTO `donate` VALUES ('11', '5', 'wilsonbrant', 'https://gitee.com/wilsonbrant', '2017-02-04 15:10:48', '码云', null, '感谢您的开源项目！', null);
INSERT INTO `donate` VALUES ('12', '10', '小房', null, '2017-02-07 14:14:00', '码云', 'qq：2667624395', '感谢您的开源项目，感谢您的无私付出！', null);
INSERT INTO `donate` VALUES ('13', '10', 'YY守护天使YY', 'https://gitee.com/yyljlyy', '2017-02-15 00:13:31', '码云', '这位朋友尝试阅读过tio源代码，并且多次捐赠，最后作者都有点不好意思了', '	感谢您的开源项目！', null);
INSERT INTO `donate` VALUES ('14', '100', '捐赠者要求匿名', '', '2017-03-19 10:02:53', '码云', '捐赠者要求匿名，感谢这位同学，作者私下已备注该条捐赠信息', '开源不易 多谢开源', 'qq：787702029，章小凡，一袭白衣微胜雪，https://gitee.com/SJRSB');
INSERT INTO `donate` VALUES ('15', '188', 'YY守护天使YY', 'https://gitee.com/yyljlyy', '2017-03-26 09:33:48', '码云', '这位朋友尝试阅读过tio源代码，并且多次捐赠，最后作者都有点不好意思了', '愿talent-aio越来越好！', null);
INSERT INTO `donate` VALUES ('16', '100', '漂泊', null, '2017-04-03 13:16:10', '码云', null, '感谢您的开源项目！', null);
INSERT INTO `donate` VALUES ('17', '58', 'YY守护天使YY', 'https://gitee.com/yyljlyy', '2017-04-05 23:22:43', '码云', '这位朋友尝试阅读过tio源代码，并且多次捐赠，最后作者都有点不好意思了', '行动支持tio', null);
INSERT INTO `donate` VALUES ('18', '10', '精灵007', 'https://gitee.com/null_346_8382', '2017-04-07 13:03:38', '码云', 'qq：270249250', '感谢您的开源项目！	', null);
INSERT INTO `donate` VALUES ('19', '10', '叶昭良', null, '2017-04-16 14:03:09', '码云', 'qq：977962857', '感谢您的开源项目！	', null);
INSERT INTO `donate` VALUES ('20', '6', '未知', 'mailto:tywo45@163.com', '2017-01-09 11:34:45', '微信二维码收款', '作者很想知道您是谁！因为作者不想让雷锋吃亏，为了您自己的合理利益，请及时反馈，谢谢！', '', null);
INSERT INTO `donate` VALUES ('21', '15', '未知', 'mailto:tywo45@163.com', '2017-04-07 17:40:09', '微信二维码收款', '作者很想知道您是谁！因为作者不想让雷锋吃亏，为了您自己的合理利益，请及时反馈，谢谢！', '', null);
INSERT INTO `donate` VALUES ('22', '20', 'KevinBlandy', null, '2017-05-03 00:18:24', '微信二维码收款', 'qq：747692844', '', null);
INSERT INTO `donate` VALUES ('23', '100', '未知', 'mailto:tywo45@163.com', '2017-05-05 09:34:28', '微信二维码收款', '作者很想知道您是谁！因为作者不想让雷锋吃亏，为了您自己的合理利益，请及时反馈，谢谢！', '', null);
INSERT INTO `donate` VALUES ('24', '20', '未知', 'mailto:tywo45@163.com', '2017-05-05 10:11:29', '微信二维码收款', '作者很想知道您是谁！因为作者不想让雷锋吃亏，为了您自己的合理利益，请及时反馈，谢谢！', '', null);
INSERT INTO `donate` VALUES ('25', '128', 'orpherus', 'https://my.oschina.net/u/3239976', '2017-05-07 19:24:09', '微信二维码收款', '后来双帮助tio指正了一个认识错误', '继yii和vue之后第三个我觉得不错的国产基础库，小捐128以表鼓励。', null);
INSERT INTO `donate` VALUES ('26', '66', '未知', 'mailto:tywo45@163.com', '2017-05-09 10:10:29', '微信二维码收款', '作者很想知道您是谁！因为作者不想让雷锋吃亏，为了您自己的合理利益，请及时反馈，谢谢！', '', null);
INSERT INTO `donate` VALUES ('27', '50', '何勇波', 'http://www.citis.cc', '2017-05-16 11:09:30', '微信二维码收款', 'qq：834659942，公司地址：浙江省宁波市江东区民安路新天地大厦1号楼702室。正准备用tio的httpserver。', 'tio.谭.Java即时通讯框架', null);
INSERT INTO `donate` VALUES ('28', '10', '未知', 'mailto:tywo45@163.com', '2017-05-25 10:01:31', '微信二维码收款', '作者很想知道您是谁！因为作者不想让雷锋吃亏，为了您自己的合理利益，请及时反馈，谢谢！', '', null);
INSERT INTO `donate` VALUES ('29', '66', '未知', 'mailto:tywo45@163.com', '2017-06-13 16:12:31', '微信二维码收款', '作者很想知道您是谁！因为作者不想让雷锋吃亏，为了您自己的合理利益，请及时反馈，谢谢！', '', null);
INSERT INTO `donate` VALUES ('30', '5', '未知', 'mailto:tywo45@163.com', '2017-06-14 17:24:35', '微信二维码收款', '作者很想知道您是谁！因为作者不想让雷锋吃亏，为了您自己的合理利益，请及时反馈，谢谢！', '', null);
INSERT INTO `donate` VALUES ('31', '66', '捐赠者要求匿名', '', '2017-06-16 22:26:13', '微信二维码收款', '捐赠者要求匿名，感谢这位同学，作者私下已备注该条捐赠信息', '', '203025368，墨言');
INSERT INTO `donate` VALUES ('32', '600', '贤心', 'https://my.oschina.net/u/1168184', '2017-05-11 17:26:24', '支付宝', 'layui作者，贤心太客气，在我捐赠layui后，还回赠', '支持t-io', null);
INSERT INTO `donate` VALUES ('33', '88', '匠人', 'https://gitee.com/openWolf/gopush', '2017-03-18 07:31:15', '微信红包', 'gopush作者', '', null);
INSERT INTO `donate` VALUES ('34', '58.88', 'YY守护天使YY', 'https://gitee.com/yyljlyy', '2017-03-02 19:09:24', '微信红包', '这位朋友尝试阅读过tio源代码，并且多次捐赠，最后作者都有点不好意思了', '框架写得太霸道了！怒赞。读得我心潮澎湃，久违的感觉。', null);
INSERT INTO `donate` VALUES ('35', '68', '独孤求BUG', null, '2017-03-18 00:14:44', '微信红包', 'qq：171707767', '辛苦了，小小心意', null);
INSERT INTO `donate` VALUES ('36', '50', '(り涛声依旧', null, '2017-04-25 18:41:18', '支付宝', 'qq：253044990', '谭主加油，lam小涛', null);
INSERT INTO `donate` VALUES ('37', '42', '捐赠者要求匿名', null, '2017-04-25 18:08:10', '微信二维码收款', '捐赠者要求匿名，感谢这位同学，作者私下已备注该条捐赠信息', '', '何喜');
INSERT INTO `donate` VALUES ('38', '59', '木予', null, '2017-04-23 15:50:04', '支付宝', 'qq：18332024，提前预定企业版服务', '支持t-io发展', '提前预定企业版服务');
INSERT INTO `donate` VALUES ('39', '168', '贤心', 'https://my.oschina.net/u/1168184', '2017-06-13 23:18:57', '支付宝', 'layui作者，贤心太客气，在我码云上捐赠过layui后，还加价回赠', '总算找到地方回赠了', '贤心太客气，我在码云上捐赠过layui后，还加价回赠');
INSERT INTO `donate` VALUES ('40', '29', '绝尘', null, '2017-05-08 21:55:40', '支付宝', 'qq：237809796', '情怀助力()困了累了，喝杯咖啡', 'jf');
INSERT INTO `donate` VALUES ('41', '9', '木予', null, '2017-05-09 09:18:14', '支付宝', 'qq：18332024', '捐助', null);
INSERT INTO `donate` VALUES ('42', '20', 'KevinBlandy', null, '2017-05-03 14:22:35', '支付宝', 'qq：747692844', '', null);
INSERT INTO `donate` VALUES ('43', '6', '小徐同学', '', '2017-04-28 11:04:48', '微信二维码收款', 'qq：409413474。我上线的项目服务器用的是tio框架，作为server端与温控设备（客户端）进行socket通讯，客户端是客户那边的硬件设备，有自己的协议实现，所以没有用到tio，tio帮我实现了自动重连和心跳检测，所以基本没什么事可以干了。上线两个月，一直很稳定。', null, null);
INSERT INTO `donate` VALUES ('45', '8', '常尚全', null, '2017-05-04 10:18:25', null, 'qq：244627250', null, null);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `loginname` varchar(32) NOT NULL,
  `pwd` varchar(64) NOT NULL,
  `salt` varchar(16) NOT NULL,
  `nick` varchar(16) NOT NULL,
  `avatar` varchar(64) CHARACTER SET utf8 NOT NULL,
  `ip` varchar(16) CHARACTER SET utf8 NOT NULL COMMENT '注册ip',
  `createtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` tinyint(2) NOT NULL DEFAULT '1' COMMENT '用户状态, 1：正常，2：注销，3：被拉黑',
  PRIMARY KEY (`id`),
  KEY `loginname` (`loginname`,`pwd`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of user
-- ----------------------------

-- ----------------------------
-- Table structure for user_osc
-- ----------------------------
DROP TABLE IF EXISTS `user_osc`;
CREATE TABLE `user_osc` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` int(11) NOT NULL,
  `oscid` varchar(32) NOT NULL,
  `oscnick` varchar(64) DEFAULT NULL,
  `createtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of user_osc
-- ----------------------------
