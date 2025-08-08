/*
 Navicat Premium Data Transfer

 Source Server         : nexus
 Source Server Type    : MySQL
 Source Server Version : 50744
 Source Host           : localhost:3306
 Source Schema         : nexus_seata

 Target Server Type    : MySQL
 Target Server Version : 50744
 File Encoding         : 65001

 Date: 09/08/2025 02:32:02
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for branch_table
-- ----------------------------
DROP TABLE IF EXISTS `branch_table`;
CREATE TABLE `branch_table` (
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(128) NOT NULL,
  `transaction_id` bigint(20) DEFAULT NULL,
  `resource_group_id` varchar(32) DEFAULT NULL,
  `resource_id` varchar(256) DEFAULT NULL,
  `branch_type` varchar(8) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `client_id` varchar(64) DEFAULT NULL,
  `application_data` varchar(2000) DEFAULT NULL,
  `gmt_create` datetime(6) DEFAULT NULL,
  `gmt_modified` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`branch_id`),
  KEY `idx_xid` (`xid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of branch_table
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for distributed_lock
-- ----------------------------
DROP TABLE IF EXISTS `distributed_lock`;
CREATE TABLE `distributed_lock` (
  `lock_key` char(20) NOT NULL,
  `lock_value` varchar(20) NOT NULL,
  `expire` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`lock_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of distributed_lock
-- ----------------------------
BEGIN;
INSERT INTO `distributed_lock` (`lock_key`, `lock_value`, `expire`) VALUES ('AsyncCommitting', '172.23.0.2:8091', 1754673291855);
INSERT INTO `distributed_lock` (`lock_key`, `lock_value`, `expire`) VALUES ('RetryCommitting', '172.23.0.2:8091', 1754673291854);
INSERT INTO `distributed_lock` (`lock_key`, `lock_value`, `expire`) VALUES ('RetryRollbacking', ' ', 0);
INSERT INTO `distributed_lock` (`lock_key`, `lock_value`, `expire`) VALUES ('TxTimeoutCheck', '172.23.0.2:8091', 1754673291856);
INSERT INTO `distributed_lock` (`lock_key`, `lock_value`, `expire`) VALUES ('UndologDelete', ' ', 0);
COMMIT;

-- ----------------------------
-- Table structure for global_table
-- ----------------------------
DROP TABLE IF EXISTS `global_table`;
CREATE TABLE `global_table` (
  `xid` varchar(128) NOT NULL,
  `transaction_id` bigint(20) DEFAULT NULL,
  `status` tinyint(4) NOT NULL,
  `application_id` varchar(32) DEFAULT NULL,
  `transaction_service_group` varchar(32) DEFAULT NULL,
  `transaction_name` varchar(128) DEFAULT NULL,
  `timeout` int(11) DEFAULT NULL,
  `begin_time` bigint(20) DEFAULT NULL,
  `application_data` varchar(2000) DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`xid`),
  KEY `idx_status_gmt_modified` (`status`,`gmt_modified`),
  KEY `idx_transaction_id` (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of global_table
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for lock_table
-- ----------------------------
DROP TABLE IF EXISTS `lock_table`;
CREATE TABLE `lock_table` (
  `row_key` varchar(128) NOT NULL,
  `xid` varchar(128) DEFAULT NULL,
  `transaction_id` bigint(20) DEFAULT NULL,
  `branch_id` bigint(20) NOT NULL,
  `resource_id` varchar(256) DEFAULT NULL,
  `table_name` varchar(32) DEFAULT NULL,
  `pk` varchar(36) DEFAULT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:locked ,1:rollbacking',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`row_key`),
  KEY `idx_status` (`status`),
  KEY `idx_branch_id` (`branch_id`),
  KEY `idx_xid` (`xid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of lock_table
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for vgroup_table
-- ----------------------------
DROP TABLE IF EXISTS `vgroup_table`;
CREATE TABLE `vgroup_table` (
  `vGroup` varchar(255) DEFAULT NULL,
  `namespace` varchar(255) DEFAULT NULL,
  `cluster` varchar(255) DEFAULT NULL,
  UNIQUE KEY `idx_vgroup_namespace_cluster` (`vGroup`,`namespace`,`cluster`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of vgroup_table
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
