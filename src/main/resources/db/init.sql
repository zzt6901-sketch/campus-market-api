-- ========================================
-- 校园二手交易平台 — 数据库初始化脚本
-- ========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS campus_market
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;
USE campus_market;

-- ========================================
-- 1. 用户表
-- ========================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    `nickname`    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    `avatar`      VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `school`      VARCHAR(100) DEFAULT NULL COMMENT '学校',
    `role`        TINYINT      NOT NULL DEFAULT 0 COMMENT '角色: 0-普通用户, 1-管理员',
    `status`      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态: 0-正常, 1-禁用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_school` (`school`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ========================================
-- 2. 分类表
-- ========================================
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name`        VARCHAR(50) NOT NULL COMMENT '分类名称',
    `parent_id`   BIGINT      NOT NULL DEFAULT 0 COMMENT '父分类ID，0为顶级',
    `sort`        INT         NOT NULL DEFAULT 0 COMMENT '排序',
    `icon`        VARCHAR(255) DEFAULT NULL COMMENT '图标',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- ========================================
-- 3. 商品表
-- ========================================
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id`             BIGINT         NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `title`          VARCHAR(200)   NOT NULL COMMENT '标题',
    `description`    TEXT           DEFAULT NULL COMMENT '描述',
    `price`          DECIMAL(10,2)  NOT NULL COMMENT '售价',
    `original_price` DECIMAL(10,2)  DEFAULT NULL COMMENT '原价',
    `images`         VARCHAR(2000)  DEFAULT '[]' COMMENT '图片列表（JSON数组）',
    `category_id`    BIGINT         NOT NULL COMMENT '分类ID',
    `user_id`        BIGINT         NOT NULL COMMENT '发布者用户ID',
    `condition`      TINYINT        DEFAULT 0 COMMENT '成色: 0-全新, 1-几乎全新, 2-轻微使用痕迹, 3-明显使用痕迹',
    `trade_way`      TINYINT        DEFAULT 0 COMMENT '交易方式: 0-自提, 1-可快递',
    `campus`         VARCHAR(100)   DEFAULT NULL COMMENT '校区',
    `status`         TINYINT        NOT NULL DEFAULT 0 COMMENT '状态: 0-在售, 1-已售, 2-已下架',
    `view_count`     INT            NOT NULL DEFAULT 0 COMMENT '浏览量',
    `create_time`    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `update_time`    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_price` (`price`),
    KEY `idx_create_time` (`create_time`),
    FULLTEXT KEY `ft_title_desc` (`title`, `description`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- ========================================
-- 4. 订单表
-- ========================================
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no`    VARCHAR(32)   NOT NULL COMMENT '订单编号（雪花ID）',
    `product_id`  BIGINT        NOT NULL COMMENT '商品ID',
    `buyer_id`    BIGINT        NOT NULL COMMENT '买家ID',
    `seller_id`   BIGINT        NOT NULL COMMENT '卖家ID',
    `amount`      DECIMAL(10,2) NOT NULL COMMENT '交易金额',
    `status`      TINYINT       NOT NULL DEFAULT 0 COMMENT '状态: 0-待付款, 1-已付款, 2-已发货, 3-已完成, 4-已取消',
    `remark`      VARCHAR(500)  DEFAULT NULL COMMENT '买家备注',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_buyer_id` (`buyer_id`),
    KEY `idx_seller_id` (`seller_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- ========================================
-- 5. 收藏表
-- ========================================
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
    `product_id`  BIGINT   NOT NULL COMMENT '商品ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- ========================================
-- 6. 消息表
-- ========================================
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `from_user_id` BIGINT       DEFAULT NULL COMMENT '发送者ID（NULL或0为系统消息）',
    `to_user_id`   BIGINT       NOT NULL COMMENT '接收者ID',
    `product_id`   BIGINT       DEFAULT NULL COMMENT '关联商品ID',
    `content`      TEXT         NOT NULL COMMENT '消息内容',
    `type`         TINYINT      NOT NULL DEFAULT 1 COMMENT '类型: 0-系统通知, 1-买家咨询, 2-订单通知',
    `is_read`      TINYINT      NOT NULL DEFAULT 0 COMMENT '是否已读: 0-未读, 1-已读',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    PRIMARY KEY (`id`),
    KEY `idx_to_user_id` (`to_user_id`),
    KEY `idx_from_user_id` (`from_user_id`),
    KEY `idx_is_read` (`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- ========================================
-- 7. 评价表
-- ========================================
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
    `id`             BIGINT      NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `order_id`       BIGINT      NOT NULL COMMENT '订单ID',
    `reviewer_id`    BIGINT      NOT NULL COMMENT '评价者ID',
    `target_user_id` BIGINT      NOT NULL COMMENT '被评价者ID',
    `product_id`     BIGINT      NOT NULL COMMENT '商品ID',
    `rating`         TINYINT     NOT NULL COMMENT '评分: 1-5',
    `content`        TEXT        DEFAULT NULL COMMENT '评价内容',
    `create_time`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_reviewer` (`order_id`, `reviewer_id`),
    KEY `idx_target_user_id` (`target_user_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价表';

-- ========================================
-- 初始化数据
-- ========================================

-- 管理员账号: admin / 123456
-- 普通用户: zhangsan / 123456, lisi / 123456
-- 密码都是 BCrypt($2a$10$...) 加密后的 "123456"
INSERT INTO `user` (`username`, `password`, `nickname`, `phone`, `email`, `school`, `role`, `status`) VALUES
('admin',    '$2a$10$rgRuCZFQgZXVoTsznaIsiOKYS2qJ1heJKshQquGohkL6ZCF0GFyoa', '演示管理员', NULL, 'admin@example.test',  NULL,          1, 0),
('zhangsan', '$2a$10$rgRuCZFQgZXVoTsznaIsiOKYS2qJ1heJKshQquGohkL6ZCF0GFyoa', '演示买家',   NULL, 'buyer@example.test',  '示例大学',    0, 0),
('lisi',     '$2a$10$rgRuCZFQgZXVoTsznaIsiOKYS2qJ1heJKshQquGohkL6ZCF0GFyoa', '演示卖家',   NULL, 'seller@example.test', '示例大学',    0, 0),
('wangwu',   '$2a$10$rgRuCZFQgZXVoTsznaIsiOKYS2qJ1heJKshQquGohkL6ZCF0GFyoa', '演示用户',   NULL, 'user@example.test',   '示例大学',    0, 0);

-- 初始化商品分类
INSERT INTO `category` (`id`, `name`, `parent_id`, `sort`, `icon`) VALUES
(1,  '教材教辅', 0, 1, '📚'),
(2,  '电子数码', 0, 2, '📱'),
(3,  '衣物鞋包', 0, 3, '👔'),
(4,  '生活用品', 0, 4, '🏠'),
(5,  '运动户外', 0, 5, '⚽'),
(6,  '其他闲置', 0, 6, '📦');

-- 初始化示例商品
INSERT INTO `product` (`title`, `description`, `price`, `original_price`, `images`, `category_id`, `user_id`, `condition`, `trade_way`, `campus`, `status`, `view_count`) VALUES
('高等数学第七版 上下册', '同济大学出版，只用了一学期，笔记很少，几乎全新', 25.00, 49.80, '["/uploads/product/math1.jpg","/uploads/product/math2.jpg"]', 1, 2, 1, 0, '主校区', 0, 128),
('iPhone 14 128GB 午夜色', '用了半年，无磕碰无维修，带原装充电器，换新机故出', 3800.00, 5999.00, '["/uploads/product/iphone1.jpg"]', 2, 3, 1, 0, '东校区', 0, 356),
('MacBook Pro 2022 M2 8GB', '99新，电池循环仅20次，包装齐全', 7200.00, 9999.00, '["/uploads/product/macbook1.jpg"]', 2, 2, 0, 1, '主校区', 0, 892),
('Nike Air Force 1 白色 42码', '正品，穿了一个月，码数不合适故出', 350.00, 799.00, '["/uploads/product/nike1.jpg"]', 3, 4, 2, 0, '西校区', 0, 67),
('宿舍用小冰箱 50L', '去年买的，制冷正常，毕业了带不走', 200.00, 499.00, '["/uploads/product/fridge1.jpg"]', 4, 3, 2, 0, '东校区', 0, 203),
('尤尼克斯羽毛球拍 一对', '入门款，带球包和6个球，只用过3次', 120.00, 299.00, '["/uploads/product/badminton1.jpg"]', 5, 2, 1, 0, '主校区', 0, 45),
('床帘 遮光款 上下铺通用', '深灰色，遮光效果好，含支架', 35.00, 89.00, '["/uploads/product/curtain1.jpg"]', 4, 4, 2, 0, '主校区', 0, 178),
('四级英语真题 2024版', '全新未拆封，买多了一本', 15.00, 39.90, '["/uploads/product/cet4.jpg"]', 1, 2, 0, 0, '主校区', 0, 312),
('机械键盘 青轴 87键', '达尔优EK815，用了3个月，换了茶轴所以出', 80.00, 199.00, '["/uploads/product/keyboard1.jpg"]', 2, 3, 2, 0, '西校区', 0, 156),
('瑜伽垫 加厚10mm', '紫色，几乎没有使用痕迹，买来就闲置了', 25.00, 59.00, '["/uploads/product/yogamat1.jpg"]', 5, 4, 1, 0, '东校区', 0, 89);
