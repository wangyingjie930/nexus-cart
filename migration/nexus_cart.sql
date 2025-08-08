/*
 Navicat Premium Data Transfer

 Source Server         : nexus
 Source Server Type    : MySQL
 Source Server Version : 50744
 Source Host           : localhost:3306
 Source Schema         : nexus_cart

 Target Server Type    : MySQL
 Target Server Version : 50744
 File Encoding         : 65001

 Date: 09/08/2025 02:31:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for account_tbl
-- ----------------------------
DROP TABLE IF EXISTS `account_tbl`;
CREATE TABLE `account_tbl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(255) DEFAULT NULL,
  `money` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of account_tbl
-- ----------------------------
BEGIN;
INSERT INTO `account_tbl` (`id`, `user_id`, `money`) VALUES (1, 'U100001', 99600);
COMMIT;

-- ----------------------------
-- Table structure for cart_items
-- ----------------------------
DROP TABLE IF EXISTS `cart_items`;
CREATE TABLE `cart_items` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `brand` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `category` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `image_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` bigint(20) NOT NULL,
  `quantity` int(11) NOT NULL,
  `sku` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `cart_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_cart_id` (`cart_id`),
  CONSTRAINT `FKpcttvuq4mxppo8sxggjtn5i2c` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of cart_items
-- ----------------------------
BEGIN;
INSERT INTO `cart_items` (`id`, `brand`, `category`, `created_at`, `image_url`, `name`, `price`, `quantity`, `sku`, `updated_at`, `cart_id`) VALUES (101, 'Huawei', '手机', '2025-07-30 16:40:44.000000', 'http://example.com/images/huawei-pura-70.jpg', 'Pura 70', 599900, 1, 'SKU-PHONE-001', '2025-07-30 16:40:44.000000', 1);
INSERT INTO `cart_items` (`id`, `brand`, `category`, `created_at`, `image_url`, `name`, `price`, `quantity`, `sku`, `updated_at`, `cart_id`) VALUES (102, 'Xiaomi', '收集', '2025-07-30 16:41:09.000000', 'http://example.com/images/xiaomi-band-8.jpg', 'xiaomi8', 29900, 2, 'SKU-BAND-002', '2025-07-30 16:41:09.000000', 1);
INSERT INTO `cart_items` (`id`, `brand`, `category`, `created_at`, `image_url`, `name`, `price`, `quantity`, `sku`, `updated_at`, `cart_id`) VALUES (105, 'Apple', '电脑', '2025-07-31 01:04:23.799339', NULL, NULL, 899900, 3, 'SKU-LAPTOP-003', '2025-07-31 01:53:27.009629', 1);
COMMIT;

-- ----------------------------
-- Table structure for carts
-- ----------------------------
DROP TABLE IF EXISTS `carts`;
CREATE TABLE `carts` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_64t7ox312pqal3p7fg9o503c2` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of carts
-- ----------------------------
BEGIN;
INSERT INTO `carts` (`id`, `created_at`, `updated_at`, `user_id`) VALUES (1, '2025-07-30 16:35:13.000000', '2025-07-30 16:35:13.000000', '1');
COMMIT;

-- ----------------------------
-- Table structure for order_tbl
-- ----------------------------
DROP TABLE IF EXISTS `order_tbl`;
CREATE TABLE `order_tbl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(255) DEFAULT NULL,
  `commodity_code` varchar(255) DEFAULT NULL,
  `count` int(11) DEFAULT '0',
  `money` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of order_tbl
-- ----------------------------
BEGIN;
INSERT INTO `order_tbl` (`id`, `user_id`, `commodity_code`, `count`, `money`) VALUES (2, 'U100001', 'C00321', 2, 400);
INSERT INTO `order_tbl` (`id`, `user_id`, `commodity_code`, `count`, `money`) VALUES (3, 'U100001', 'C00321', 2, 400);
COMMIT;

-- ----------------------------
-- Table structure for stock_tbl
-- ----------------------------
DROP TABLE IF EXISTS `stock_tbl`;
CREATE TABLE `stock_tbl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `commodity_code` varchar(255) DEFAULT NULL,
  `count` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `commodity_code` (`commodity_code`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of stock_tbl
-- ----------------------------
BEGIN;
INSERT INTO `stock_tbl` (`id`, `commodity_code`, `count`) VALUES (1, 'C00321', 9998);
COMMIT;

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log` (
  `branch_id` bigint(20) NOT NULL COMMENT 'branch transaction id',
  `xid` varchar(128) NOT NULL COMMENT 'global transaction id',
  `context` varchar(128) NOT NULL COMMENT 'undo_log context,such as serialization',
  `rollback_info` longblob NOT NULL COMMENT 'rollback info',
  `log_status` int(11) NOT NULL COMMENT '0:normal status,1:defense status',
  `log_created` datetime(6) NOT NULL COMMENT 'create datetime',
  `log_modified` datetime(6) NOT NULL COMMENT 'modify datetime',
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`),
  KEY `ix_log_created` (`log_created`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AT transaction mode undo table';

-- ----------------------------
-- Records of undo_log
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
