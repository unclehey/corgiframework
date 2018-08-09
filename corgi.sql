
CREATE TABLE `app_banner`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'pk',
  `pic_type` int(1) NULL DEFAULT 0 COMMENT '图片类型：0首页',
  `pic_title` varchar(100) NULL DEFAULT NULL COMMENT '标题',
  `pic_url` varchar(50) NULL DEFAULT NULL COMMENT '图片地址',
  `link_url` varchar(200) NULL DEFAULT NULL COMMENT '跳转链接',
  `status` int(1) NULL DEFAULT 0 COMMENT '状态：0有效，1下架',
  `pic_weight` int(11) NULL DEFAULT 0 COMMENT '图片权重',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT = 'App图片表';

CREATE TABLE `app_rule`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'pk',
  `rule_name` varchar(50) NULL DEFAULT NULL COMMENT '规则名称',
  `rule_type` int(1) NULL DEFAULT NULL COMMENT '规则类型：0-邀请企业',
  `rule_content` varchar(1024) NULL DEFAULT NULL COMMENT '规则内容',
  `status` int(1) NULL DEFAULT 0 COMMENT '规则状态：0有效，1作废',
  `start_date` date NULL DEFAULT NULL COMMENT '规则开始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '规则结束日期',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT = 'App规则表';

CREATE TABLE `app_version`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'pk',
  `version_number` varchar(20) NULL DEFAULT NULL COMMENT 'App版本号',
  `api_version` varchar(10) NULL DEFAULT NULL COMMENT 'api版本',
  `description` varchar(500) NULL DEFAULT NULL COMMENT '版本说明',
  `download_url` varchar(200) NULL DEFAULT NULL COMMENT '下载地址',
  `operating_system` varchar(10) NULL DEFAULT NULL COMMENT '操作系统：iOS或Android',
  `force_upgrade` int(1) NULL DEFAULT NULL COMMENT '强制更新：1-强制，0-非强制',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT = 'App版本表';

CREATE TABLE `mgr_depart`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'pk',
  `cn_name` varchar(32) NOT NULL COMMENT '中文描述',
  `parent_id` int(11) NULL DEFAULT NULL COMMENT '上级部门id',
  `type` varchar(10) NULL DEFAULT NULL COMMENT '所处级数',
  `remark` varchar(50) NULL DEFAULT NULL COMMENT '备注',
  `operator` int(11) NULL DEFAULT NULL COMMENT '操作人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT = '部门表';

CREATE TABLE `mgr_depart_func`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'pk',
  `depart_id` int(11) NOT NULL COMMENT '部门id',
  `func_id` int(11) NOT NULL COMMENT '功能id',
  `remark` varchar(50) NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_depart_id`(`depart_id`) USING BTREE,
  INDEX `idx_func_id`(`func_id`) USING BTREE
) ENGINE = InnoDB COMMENT = '部门权限表';

CREATE TABLE `mgr_func`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'pk',
  `code` varchar(20) NULL DEFAULT NULL COMMENT '功能编码',
  `cn_name` varchar(32) NOT NULL COMMENT '中文描述',
  `parent_id` int(11) NULL DEFAULT NULL COMMENT '父功能节点id',
  `type` varchar(10) NULL DEFAULT NULL COMMENT '功能类型：1-业务菜单，2-功能菜单，3-按钮',
  `url` varchar(100) NULL DEFAULT NULL COMMENT 'url路径',
  `menu_icon` varchar(50) NULL DEFAULT NULL COMMENT '菜单图标',
  `is_use` varchar(2) NULL DEFAULT '1' COMMENT '作废标识（1：在用；2：作废）',
  `remark` varchar(50) NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT = '功能资源表';

CREATE TABLE `mgr_user`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'pk',
  `account` varchar(20) NOT NULL COMMENT '账号',
  `password` varchar(32) NOT NULL COMMENT '密码',
  `email` varchar(50) NULL DEFAULT NULL COMMENT '邮箱',
  `real_name` varchar(20) NULL DEFAULT NULL COMMENT '姓名',
  `status` int(1) NULL DEFAULT 0 COMMENT '激活状态：默认0未激活；1已激活；2激活失败；3已禁用',
  `token` varchar(50) NULL DEFAULT NULL COMMENT '激活码',
  `effective_time` datetime(0) NULL DEFAULT NULL COMMENT '激活有效时间',
  `last_login_time` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT = '后台用户表';

CREATE TABLE `mgr_user_depart`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'pk',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `depart_id` int(11) NOT NULL COMMENT '部门id',
  `remark` varchar(50) NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_depart_id`(`depart_id`) USING BTREE
) ENGINE = InnoDB COMMENT = '用户部门表';

CREATE TABLE `payment_order`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'pk',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `mobile` varchar(11) NULL DEFAULT NULL COMMENT '手机号',
  `order_type` int(11) NULL DEFAULT NULL COMMENT '订单类型：1个人会员',
  `trade_no` varchar(32) NULL DEFAULT NULL COMMENT '订单号',
  `amount` decimal(18, 2) NULL DEFAULT NULL COMMENT '订单金额',
  `order_count` int(11) NULL DEFAULT NULL COMMENT '订单数量',
  `product_id` varchar(50) NULL DEFAULT NULL COMMENT '商品ID',
  `body` varchar(500) NULL DEFAULT NULL COMMENT '商品描述',
  `pay_channel` varchar(10) NULL DEFAULT NULL COMMENT '支付渠道：W-微信，A-支付宝',
  `trade_type` varchar(10) NULL DEFAULT NULL COMMENT '交易类型：APP、JSAPI、MWEB',
  `pay_source` varchar(10) NULL DEFAULT NULL COMMENT '支付来源：iOS、Android、wechat',
  `pay_status` int(1) NULL DEFAULT 0 COMMENT '支付状态：0-未支付，1-支付成功，2-支付失败 3-已退款',
  `start_date` date DEFAULT NULL COMMENT '有效开始日期',
  `end_date` date DEFAULT NULL COMMENT '有效结束日期',
  `seller_id` varchar(32) NULL DEFAULT NULL COMMENT '商户号',
  `open_id` varchar(50) NULL DEFAULT NULL COMMENT '用户在商户的唯一标识',
  `transaction_id` varchar(32) NULL DEFAULT NULL COMMENT '第三方支付返回订单号',
  `bank_type` varchar(16) NULL DEFAULT NULL COMMENT '付款银行',
  `success_time` datetime(0) NULL DEFAULT NULL COMMENT '支付完成时间（微信返回）',
  `refund_status` int(1) NULL DEFAULT 0 COMMENT '退款状态：默认为0未退款，1部分退款，2全额退款',
  `refund_amount` decimal(18, 2) NULL DEFAULT NULL COMMENT '退款金额',
  `refund_request_no` varchar(32) NULL DEFAULT NULL COMMENT '标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传。',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_trade_no`(`trade_no`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB COMMENT = '订单表';

CREATE TABLE `sms_record`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'pk',
  `mobile` varchar(11) NOT NULL COMMENT '手机号',
  `content` varchar(256) NOT NULL COMMENT '短信内容',
  `type` int(2) NOT NULL COMMENT '短信类型：1注册；2找回密码',
  `flag` int(1) NULL DEFAULT NULL COMMENT '短信发送状态：0失败，1成功',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_mobile`(`mobile`) USING BTREE
) ENGINE = InnoDB COMMENT = '短信发送记录表';

CREATE TABLE `sys_category`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'pk',
  `category_name` varchar(20) NOT NULL COMMENT '类目名称',
  `category_weight` int(11) NULL DEFAULT 0 COMMENT '权重',
  `category_type` int(11) NULL DEFAULT 0 COMMENT '类型：1头条，2杂志',
  `category_pic` varchar(50) DEFAULT NULL COMMENT '图片',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT = '系统分类表';

CREATE TABLE `sys_properties`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'pk',
  `prop_key` varchar(50) NULL DEFAULT NULL COMMENT 'key值',
  `prop_value` varchar(255) NULL DEFAULT NULL COMMENT 'value值',
  `description` varchar(255) NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_key`(`prop_key`) USING BTREE
) ENGINE = InnoDB COMMENT = '系统配置表';

CREATE TABLE `sys_price` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'pk',
  `title` varchar(50) DEFAULT NULL COMMENT '描述',
  `price` decimal(18,2) DEFAULT NULL COMMENT '单价',
  `duration` int(2) DEFAULT NULL COMMENT '时长（单位：月）',
  `type` int(1) DEFAULT '1' COMMENT '类型：1个人会员',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB COMMENT='系统价格表';