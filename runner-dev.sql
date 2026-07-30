/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.10.20(实训)
 Source Server Type    : MySQL
 Source Server Version : 80045
 Source Host           : 192.168.10.20:3306
 Source Schema         : runner-dev

 Target Server Type    : MySQL
 Target Server Version : 80045
 File Encoding         : 65001

 Date: 28/06/2026 17:37:04
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin_user
-- ----------------------------
DROP TABLE IF EXISTS `admin_user`;
CREATE TABLE `admin_user`  (
  `id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码（BCrypt加密）',
  `face_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '人脸入库图片ID（MongoDB GridFS）',
  `admin_name` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '管理员姓名',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  `updated_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_user
-- ----------------------------
INSERT INTO `admin_user` VALUES ('admin001', 'admin', '$2a$10$yzcGgFWQ88V0lHHRDK0MKOMgkCrNWMU7dOu00M3pm2EToh3Xg8ppq', NULL, '管理员', '2026-06-28 01:59:32', '2026-06-28 01:59:32');

-- ----------------------------
-- Table structure for app_user
-- ----------------------------
DROP TABLE IF EXISTS `app_user`;
CREATE TABLE `app_user`  (
  `id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `mobile` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名（登录用）',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码（BCrypt加密）',
  `nickname` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '昵称',
  `face` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `realname` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `email` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱地址',
  `sex` int NULL DEFAULT NULL COMMENT '性别 1:男 0:女 2:保密',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `province` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '城市',
  `district` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '区县',
  `user_role` int NOT NULL DEFAULT 1 COMMENT '用户角色 1:普通用户 2:跑腿员 3:管理员',
  `active_status` int NOT NULL DEFAULT 1 COMMENT '用户状态 1:正常 2:冻结 3:审核中',
  `balance` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '账户余额',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  `updated_time` datetime NOT NULL COMMENT '更新时间',
  `face_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'MongoDB GridFS 人脸ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `mobile`(`mobile`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of app_user
-- ----------------------------
INSERT INTO `app_user` VALUES ('9a60ab1989b1451d', '13234567890', 'user_7890', '$2a$10$9Nr5bit3sOvVbCs64GoV6.eUbbyzmjwgzwL4x5yWODIiior7eVsuS', '1', 'http://192.168.10.20:8888/gzmu/M00/00/00/wKgKFGpAE7SAaIiIABRcbRdnEp8947.png', '未设置', 'default@example.com', 2, NULL, NULL, NULL, NULL, 1, 1, 3.00, '2026-06-28 13:44:12', '2026-06-28 15:19:16', NULL);
INSERT INTO `app_user` VALUES ('c94ea35c635f4fd3', '13985104200', 'user_4200', '$2a$10$JGwr2OYUU6kpsk8NM/nAIuuG94vuMXSi6sW2quTMeR0FVkuroR1tG', '沈', 'http://192.168.10.20:8888/gzmu/M00/00/00/wKgKFGpAEMaAFl22AAALzRZJbeE097.PNG', '沈', '3277315837@qq.com', 1, '2004-11-09', '贵州省', '贵阳市', '花溪区', 2, 1, 208.00, '2026-06-28 02:00:04', '2026-06-28 17:28:49', NULL);

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名',
  `tag_color` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签颜色',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '任务分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '代取快递', '#FF6B6B');
INSERT INTO `category` VALUES (2, '代买外卖', '#4ECDC4');
INSERT INTO `category` VALUES (3, '代送文件', '#45B7D1');
INSERT INTO `category` VALUES (4, '排队跑腿', '#96CEB4');
INSERT INTO `category` VALUES (5, '校园跑腿', '#FFEAA7');
INSERT INTO `category` VALUES (6, '其他', '#DDA0DD');

-- ----------------------------
-- Table structure for conversation
-- ----------------------------
DROP TABLE IF EXISTS `conversation`;
CREATE TABLE `conversation`  (
  `id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `task_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '关联任务ID',
  `user_a_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '参与者A（发布者）',
  `user_a_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '冗余昵称',
  `user_a_face` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '冗余头像',
  `user_b_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '参与者B（跑腿员）',
  `user_b_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '冗余昵称',
  `user_b_face` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '冗余头像',
  `last_message` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后一条消息内容',
  `last_time` datetime NULL DEFAULT NULL COMMENT '最后一条消息时间',
  `user_a_unread` int NOT NULL DEFAULT 0 COMMENT 'A的未读消息数',
  `user_b_unread` int NOT NULL DEFAULT 0 COMMENT 'B的未读消息数',
  `user_a_deleted` int NOT NULL DEFAULT 0 COMMENT 'A是否删除（0:否 1:是）',
  `user_b_deleted` int NOT NULL DEFAULT 0 COMMENT 'B是否删除（0:否 1:是）',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  `updated_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `task_id`(`task_id`) USING BTREE,
  INDEX `user_a_id`(`user_a_id`) USING BTREE,
  INDEX `user_b_id`(`user_b_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of conversation
-- ----------------------------
INSERT INTO `conversation` VALUES ('484e582c50e44923', '0ad56b409bfe42c9', '9a60ab1989b1451d', '1', '', 'c94ea35c635f4fd3', '%E6%B2%88', '', NULL, NULL, 0, 0, 0, 0, '2026-06-28 15:20:43', '2026-06-28 15:20:43');
INSERT INTO `conversation` VALUES ('dba751624b9144ae', '91d9869e51dc4bfb', 'c94ea35c635f4fd3', '沈', '', 'c94ea35c635f4fd3', '%E6%B2%88', '', NULL, NULL, 0, 0, 0, 0, '2026-06-28 17:30:37', '2026-06-28 17:30:37');

-- ----------------------------
-- Table structure for evaluation
-- ----------------------------
DROP TABLE IF EXISTS `evaluation`;
CREATE TABLE `evaluation`  (
  `id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `task_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务ID',
  `publisher_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评价人ID（发布者）',
  `publisher_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评价人昵称',
  `runner_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '被评价人ID（跑腿员）',
  `runner_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '被评价人昵称',
  `rating` int NOT NULL COMMENT '评分 1-5',
  `comment` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评价内容',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `runner_id`(`runner_id`) USING BTREE,
  INDEX `task_id`(`task_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of evaluation
-- ----------------------------
INSERT INTO `evaluation` VALUES ('cc35bb3e2f7b4f8f', '005a7d46a99f442b', '9a60ab1989b1451d', '1', 'c94ea35c635f4fd3', '%E6%B2%88', 5, '可以', '2026-06-28 15:02:20');

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `conversation_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '所属会话ID',
  `from_user_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发送方',
  `from_user_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发送方昵称',
  `from_user_face` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发送方头像',
  `to_user_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '接收方',
  `content` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `status` int NOT NULL DEFAULT 0 COMMENT '0:未读 1:已读',
  `is_recalled` int NOT NULL DEFAULT 0 COMMENT '0:正常 1:已撤回',
  `recall_time` datetime NULL DEFAULT NULL COMMENT '撤回时间',
  `created_time` datetime NOT NULL COMMENT '发送时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `conversation_id`(`conversation_id`) USING BTREE,
  INDEX `from_user_id`(`from_user_id`) USING BTREE,
  INDEX `to_user_id`(`to_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message
-- ----------------------------

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '接收用户ID',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '通知类型',
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `status` int NOT NULL DEFAULT 0 COMMENT '0:未读 1:已读',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `status`(`status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notification
-- ----------------------------

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `task_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务ID',
  `publisher_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发布者ID',
  `runner_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '跑腿员ID',
  `reward_amount` decimal(10, 2) NOT NULL COMMENT '酬劳金额',
  `status` int NOT NULL DEFAULT 2 COMMENT '状态 2:进行中 3:待确认 4:已完成 5:已取消',
  `completed_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  `updated_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no`) USING BTREE,
  INDEX `task_id`(`task_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES ('2367f32311b14d08', '1782631242503d4c93f6b', '0ad56b409bfe42c9', '9a60ab1989b1451d', 'c94ea35c635f4fd3', 7.00, 3, NULL, '2026-06-28 15:20:43', '2026-06-28 17:26:36');
INSERT INTO `order` VALUES ('b6b9c103f4f44ef3', '1782627670735810ccc6e', '005a7d46a99f442b', '9a60ab1989b1451d', 'c94ea35c635f4fd3', 6.00, 4, '2026-06-28 15:02:15', '2026-06-28 14:21:11', '2026-06-28 15:02:15');
INSERT INTO `order` VALUES ('f123a0fb700e4136', '17826390373628fcba6e1', '91d9869e51dc4bfb', 'c94ea35c635f4fd3', 'c94ea35c635f4fd3', 8.00, 2, NULL, '2026-06-28 17:30:37', '2026-06-28 17:30:37');

-- ----------------------------
-- Table structure for runner_application
-- ----------------------------
DROP TABLE IF EXISTS `runner_application`;
CREATE TABLE `runner_application`  (
  `id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `real_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '真实姓名',
  `id_card` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '身份证号',
  `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `face_image` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '人脸图片URL',
  `status` int NOT NULL DEFAULT 3 COMMENT '状态 1:通过 2:拒绝 3:审核中',
  `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注（拒绝原因）',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  `updated_time` datetime NOT NULL COMMENT '更新时间',
  `face_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'MongoDB GridFS 人脸ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '跑腿员申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of runner_application
-- ----------------------------
INSERT INTO `runner_application` VALUES ('1119116178c64ea3', 'c94ea35c635f4fd3', '沈', '520103200411091617', '13985104200', NULL, 1, NULL, '2026-06-28 02:01:31', '2026-06-28 02:01:37', NULL);

-- ----------------------------
-- Table structure for task
-- ----------------------------
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task`  (
  `id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务标题',
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务描述',
  `reward_amount` decimal(10, 2) NOT NULL COMMENT '酬劳金额',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态 1:待接单 2:进行中 3:待确认 4:已完成 5:已取消',
  `publisher_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发布者ID',
  `publisher_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发布者昵称',
  `publisher_avatar` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发布者头像',
  `runner_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接单跑腿员ID',
  `runner_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接单跑腿员昵称',
  `runner_avatar` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接单跑腿员头像',
  `pickup_location` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '取件地址',
  `pickup_lng` decimal(12, 8) NULL DEFAULT NULL COMMENT '取件经度',
  `pickup_lat` decimal(12, 8) NULL DEFAULT NULL COMMENT '取件纬度',
  `delivery_location` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '送件地址',
  `delivery_lng` decimal(12, 8) NULL DEFAULT NULL COMMENT '送件经度',
  `delivery_lat` decimal(12, 8) NULL DEFAULT NULL COMMENT '送件纬度',
  `delivery_photo` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '送达照片',
  `face_verified` int NULL DEFAULT 0 COMMENT '人脸验证 0:未验证 1:已验证',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  `updated_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `publisher_id`(`publisher_id`) USING BTREE,
  INDEX `runner_id`(`runner_id`) USING BTREE,
  INDEX `status`(`status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of task
-- ----------------------------
INSERT INTO `task` VALUES ('005a7d46a99f442b', '6', '6', 6.00, 4, '9a60ab1989b1451d', '1', '', 'c94ea35c635f4fd3', '%E6%B2%88', '', '经度 106.705836，纬度 26.598035', 106.70583600, 26.59803500, '经度 106.698627，纬度 26.607206', 106.69862700, 26.60720600, NULL, NULL, NULL, '2026-06-28 14:20:12', '2026-06-28 15:02:15');
INSERT INTO `task` VALUES ('0ad56b409bfe42c9', '7', '7', 7.00, 3, '9a60ab1989b1451d', '1', '', 'c94ea35c635f4fd3', '%E6%B2%88', '', '经度 106.704535，纬度 26.599609', 106.70453500, 26.59960900, '经度 106.705908，纬度 26.596884', 106.70590800, 26.59688400, NULL, NULL, NULL, '2026-06-28 15:19:16', '2026-06-28 17:26:36');
INSERT INTO `task` VALUES ('3c0ff105c88b4919', '4', '4', 4.00, 5, '9a60ab1989b1451d', '1', '', NULL, NULL, NULL, '经度 106.709398，纬度 26.602794', 106.70939800, 26.60279400, '经度 106.692361，纬度 26.603369', 106.69236100, 26.60336900, NULL, NULL, NULL, '2026-06-28 14:19:58', '2026-06-28 14:23:50');
INSERT INTO `task` VALUES ('91d9869e51dc4bfb', '8', '8', 8.00, 2, 'c94ea35c635f4fd3', '沈', '', 'c94ea35c635f4fd3', '%E6%B2%88', '', '经度 106.700114，纬度 26.599494', 106.70011400, 26.59949400, '经度 106.698827，纬度 26.601873', 106.69882700, 26.60187300, NULL, NULL, NULL, '2026-06-28 15:45:40', '2026-06-28 17:30:37');
INSERT INTO `task` VALUES ('b84f94b9343e4c58', '3', '3', 3.00, 1, '9a60ab1989b1451d', '1', '', NULL, NULL, NULL, '经度 106.697339，纬度 26.597421', 106.69733900, 26.59742100, '经度 106.701931，纬度 26.597498', 106.70193100, 26.59749800, NULL, NULL, NULL, '2026-06-28 14:19:47', '2026-06-28 14:19:47');
INSERT INTO `task` VALUES ('d61c02ad331b4633', '2', '2', 2.00, 1, 'c94ea35c635f4fd3', '沈', '', NULL, NULL, NULL, '经度 106.705224，纬度 26.600156', 106.70522400, 26.60015600, '经度 106.698069，纬度 26.600837', 106.69806900, 26.60083700, NULL, NULL, NULL, '2026-06-28 14:09:17', '2026-06-28 14:09:17');
INSERT INTO `task` VALUES ('e197ca1d7cf542f1', '1', '1', 1.00, 1, 'c94ea35c635f4fd3', '沈', '', NULL, NULL, NULL, '经度 106.698240，纬度 26.600837', 106.69824000, 26.60083700, '经度 106.705193，纬度 26.600376', 106.70519300, 26.60037600, NULL, NULL, NULL, '2026-06-28 14:04:48', '2026-06-28 14:04:48');

-- ----------------------------
-- Table structure for wallet_transaction
-- ----------------------------
DROP TABLE IF EXISTS `wallet_transaction`;
CREATE TABLE `wallet_transaction`  (
  `id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `type` int NOT NULL COMMENT '类型 1:充值 2:提现 3:收入 4:支出 5:退款',
  `amount` decimal(10, 2) NOT NULL COMMENT '金额',
  `balance` decimal(10, 2) NULL DEFAULT NULL COMMENT '交易后余额',
  `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态 0:处理中 1:成功 2:失败',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '外部订单号（支付宝订单号）',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  `updated_time` datetime NOT NULL COMMENT '更新时间',
  `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注/错误信息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `order_no`(`order_no`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '钱包交易记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wallet_transaction
-- ----------------------------
INSERT INTO `wallet_transaction` VALUES ('0fe96d7e58ca49d6', 'c94ea35c635f4fd3', 1, 10.00, NULL, '支付宝充值：校园闪电侠充值', 1, '1782638891363e8247209', '2026-06-28 17:28:11', '2026-06-28 17:28:49', NULL);
INSERT INTO `wallet_transaction` VALUES ('41522f9377804355', 'c94ea35c635f4fd3', 1, 10.00, NULL, '支付宝充值：校园闪电侠充值', 0, '1782638884754d5939ae5', '2026-06-28 17:28:05', '2026-06-28 17:28:05', NULL);
INSERT INTO `wallet_transaction` VALUES ('54975b279b8740bf', '9a60ab1989b1451d', 1, 6.00, NULL, '支付宝充值：校园闪电侠充值', 1, '1782627448688de2eed85', '2026-06-28 14:17:29', '2026-06-28 14:18:05', NULL);
INSERT INTO `wallet_transaction` VALUES ('72c1ce524c15473a', 'c94ea35c635f4fd3', 2, 20.00, 180.00, '提现到支付宝：avjykq1667@sandbox.com', 1, '1782628728620750724db', '2026-06-28 14:38:49', '2026-06-28 14:39:03', NULL);
INSERT INTO `wallet_transaction` VALUES ('77d7b58d0e684e86', 'c94ea35c635f4fd3', 2, 3.00, 163.00, '提现到支付宝：avjykq1667@sandbox.com', 1, '1782632597296851b3152', '2026-06-28 15:43:17', '2026-06-28 15:43:20', '转账成功');
INSERT INTO `wallet_transaction` VALUES ('85f5a16aebaf4393', '9a60ab1989b1451d', 1, 10.00, NULL, '支付宝充值：校园闪电侠充值', 0, '1782627358675654b7c2b', '2026-06-28 14:15:59', '2026-06-28 14:15:59', NULL);
INSERT INTO `wallet_transaction` VALUES ('a1647567bbd741e0', 'c94ea35c635f4fd3', 1, 200.00, NULL, '支付宝充值：校园闪电侠充值', 1, '178262751290531e352d1', '2026-06-28 14:18:33', '2026-06-28 14:19:56', NULL);
INSERT INTO `wallet_transaction` VALUES ('a1c7a6d862fc4649', 'c94ea35c635f4fd3', 2, 20.00, NULL, '提现到支付宝：avjykq1667@sandbox.com', 2, '17826283592096d26957a', '2026-06-28 14:32:39', '2026-06-28 14:32:50', NULL);
INSERT INTO `wallet_transaction` VALUES ('ab5c6502e18a41a5', '9a60ab1989b1451d', 4, 7.00, NULL, '发布任务：7', 1, NULL, '2026-06-28 15:19:16', '2026-06-28 15:19:16', NULL);
INSERT INTO `wallet_transaction` VALUES ('ad42408093b040f1', 'c94ea35c635f4fd3', 3, 6.00, NULL, '任务完成收入：6', 1, NULL, '2026-06-28 15:02:15', '2026-06-28 15:02:15', NULL);
INSERT INTO `wallet_transaction` VALUES ('e230b2f03399426a', 'c94ea35c635f4fd3', 4, 8.00, NULL, '发布任务：8', 1, NULL, '2026-06-28 15:45:40', '2026-06-28 15:45:40', NULL);
INSERT INTO `wallet_transaction` VALUES ('e56c8bf7b8c44861', '9a60ab1989b1451d', 5, 4.00, NULL, '任务取消退款: 3c0ff105c88b4919 (任务取消)', 1, 'REFUND_1782627830496', '2026-06-28 14:23:50', '2026-06-28 14:23:50', NULL);
INSERT INTO `wallet_transaction` VALUES ('edd67326aab54701', 'c94ea35c635f4fd3', 2, 20.00, 140.00, '提现到支付宝：avjykq1667@sandbox.com', 1, '17826287736470ccf0115', '2026-06-28 14:39:34', '2026-06-28 14:39:35', NULL);
INSERT INTO `wallet_transaction` VALUES ('f2a3bd44ae114670', 'c94ea35c635f4fd3', 2, 13.00, 163.00, '提现到支付宝：avjykq1667@sandbox.com', 2, '17826326787604b17cbab', '2026-06-28 15:44:39', '2026-06-28 15:44:49', '支付宝返回失败：系统繁忙');
INSERT INTO `wallet_transaction` VALUES ('f9fd31c43c1b4dde', 'c94ea35c635f4fd3', 2, 13.00, 150.00, '提现到支付宝：avjykq1667@sandbox.com', 1, '1782632699235409287e1', '2026-06-28 15:44:59', '2026-06-28 15:45:00', '转账成功');

SET FOREIGN_KEY_CHECKS = 1;
