/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : localhost:3306
 Source Schema         : hhh

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 19/07/2023 23:51:24
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for hd_user
-- ----------------------------
DROP TABLE IF EXISTS `hd_user`;
CREATE TABLE `hd_user`  (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户账号',
  `user_pwd` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户密码',
  `user_type` int NOT NULL COMMENT '用户类型(0管理员，1普通用户)',
  `create_user_id` int NOT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `last_modify_user_id` int NOT NULL COMMENT '最后修改人id',
  `last_modify_time` datetime NOT NULL COMMENT '最后修改时间',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of hd_user
-- ----------------------------
INSERT INTO `hd_user` VALUES (1, 'admin', '3fde6bb0541387e4ebdadf7c2ff31123', 0, 1, '2022-09-23 17:11:13', 1, '2022-11-21 13:49:12', '2023-07-19 23:27:54');
INSERT INTO `hd_user` VALUES (2, 'test', '3fde6bb0541387e4ebdadf7c2ff31123', 1, 1, '2022-09-23 17:11:13', 1, '2022-11-21 13:49:12', '2023-07-19 14:15:42');
INSERT INTO `hd_user` VALUES (3, 'junjie', '3fde6bb0541387e4ebdadf7c2ff31123', 1, 1, '2022-09-23 17:11:13', 1, '2022-11-21 13:49:12', '2023-07-19 14:15:42');
INSERT INTO `hd_user` VALUES (4, 'wf', '3fde6bb0541387e4ebdadf7c2ff31123', 1, 1, '2022-09-23 17:11:13', 1, '2022-11-21 13:49:12', '2023-07-19 14:15:42');
INSERT INTO `hd_user` VALUES (5, 'hhh', '3fde6bb0541387e4ebdadf7c2ff31123', 1, 1, '2022-09-23 17:11:13', 1, '2022-11-21 13:49:12', '2023-07-19 14:15:42');

SET FOREIGN_KEY_CHECKS = 1;
