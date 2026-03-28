-- 理财大师业务表补丁
-- 适用场景：数据库已初始化，只想补充理财模块业务表与演示数据
set names utf8mb4;

-- 说明：
-- 1. 本脚本会重建理财相关表，请勿在已有正式数据的库上直接执行
-- 2. 如果已有生产数据，建议改成增量 DDL/DML 后再执行

-- ----------------------------
-- 1、家庭资产表
-- ----------------------------
drop table if exists fin_alert_event;
drop table if exists fin_alert_rule;
drop table if exists fin_holding_snapshot;
drop table if exists fin_quote_snapshot;
drop table if exists fin_transaction;
drop table if exists fin_asset;
drop table if exists fin_holding_account;
drop table if exists fin_member_account;
drop table if exists fin_family;

create table fin_family (
  family_id         bigint(20)      not null auto_increment    comment '家庭ID',
  family_code       varchar(32)     not null                   comment '家庭编码',
  family_name       varchar(100)    not null                   comment '家庭名称',
  owner_name        varchar(50)     default ''                 comment '家庭负责人',
  family_sort       int(4)          default 0                  comment '显示顺序',
  status            char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0存在 2删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (family_id),
  unique key uk_fin_family_code (family_code)
) engine=innodb auto_increment=1 comment = '家庭资产表';

insert into fin_family values (1, 'MAKUN_HOME', '木坤家庭', '木坤', 1, '0', '0', 'admin', sysdate(), '', null, '家庭资产总账');

-- ----------------------------
-- 2、家庭成员账户表
-- ----------------------------
create table fin_member_account (
  member_id         bigint(20)      not null auto_increment    comment '成员ID',
  family_id         bigint(20)      not null                   comment '家庭ID',
  member_name       varchar(50)     not null                   comment '成员名称',
  member_role       varchar(30)     default ''                 comment '成员角色',
  risk_level        varchar(30)     default ''                 comment '风险偏好',
  contact_phone     varchar(20)     default ''                 comment '联系电话',
  member_sort       int(4)          default 0                  comment '显示顺序',
  status            char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0存在 2删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (member_id),
  key idx_fin_member_family (family_id)
) engine=innodb auto_increment=1 comment = '家庭成员账户表';

insert into fin_member_account values (1, 1, '木坤', '家庭负责人', '稳健成长', '13800000001', 1, '0', '0', 'admin', sysdate(), '', null, '主要负责股票账户');
insert into fin_member_account values (2, 1, '家人A', '共同账户', '稳健', '13800000002', 2, '0', '0', 'admin', sysdate(), '', null, '主要负责基金定投');

-- ----------------------------
-- 3、持仓账户表
-- ----------------------------
create table fin_holding_account (
  account_id        bigint(20)      not null auto_increment    comment '账户ID',
  family_id         bigint(20)      not null                   comment '家庭ID',
  member_id         bigint(20)      not null                   comment '成员ID',
  account_name      varchar(100)    not null                   comment '账户名称',
  account_type      varchar(30)     default ''                 comment '账户类型',
  broker_name       varchar(100)    default ''                 comment '所属平台',
  cash_balance      decimal(16,2)   default 0.00               comment '现金余额',
  account_sort      int(4)          default 0                  comment '显示顺序',
  status            char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0存在 2删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (account_id),
  key idx_fin_account_family_member (family_id, member_id)
) engine=innodb auto_increment=1 comment = '持仓账户表';

insert into fin_holding_account values (1, 1, 1, '招商证券A股账户', 'STOCK', '招商证券', 28000.00, 1, '0', '0', 'admin', sysdate(), '', null, '股票主账户');
insert into fin_holding_account values (2, 1, 2, '支付宝基金账户', 'FUND', '支付宝', 12000.00, 2, '0', '0', 'admin', sysdate(), '', null, '基金定投账户');

-- ----------------------------
-- 4、资产标的表
-- ----------------------------
create table fin_asset (
  asset_id          bigint(20)      not null auto_increment    comment '资产ID',
  market_id         bigint(20)      not null                   comment '市场ID',
  asset_code        varchar(32)     not null                   comment '资产代码',
  asset_name        varchar(100)    not null                   comment '资产名称',
  asset_type        varchar(20)     not null                   comment '资产类型（STOCK/FUND）',
  quote_code        varchar(32)     default ''                 comment '行情代码',
  quote_enabled     char(1)         default '1'                comment '是否启用行情（1是 0否）',
  status            char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0存在 2删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (asset_id),
  unique key uk_fin_asset_market_code (market_id, asset_code)
) engine=innodb auto_increment=1 comment = '资产标的表';

insert into fin_asset values (1, 1, '600519', '贵州茅台', 'STOCK', '600519', '1', '0', '0', 'admin', sysdate(), '', null, '白酒核心仓位');
insert into fin_asset values (2, 2, '159915', '创业板ETF', 'FUND', '159915', '1', '0', '0', 'admin', sysdate(), '', null, '指数增强配置');
insert into fin_asset values (3, 2, '012348', '中欧时代先锋混合', 'FUND', '012348', '1', '0', '0', 'admin', sysdate(), '', null, '主动权益基金');

-- ----------------------------
-- 5、交易流水表
-- ----------------------------
create table fin_transaction (
  transaction_id    bigint(20)      not null auto_increment    comment '流水ID',
  family_id         bigint(20)      not null                   comment '家庭ID',
  member_id         bigint(20)      not null                   comment '成员ID',
  account_id        bigint(20)      not null                   comment '账户ID',
  asset_id          bigint(20)      not null                   comment '资产ID',
  transaction_type  varchar(30)     not null                   comment '交易类型',
  trade_date        datetime        not null                   comment '交易时间',
  quantity          decimal(18,4)   not null                   comment '数量/份额',
  price             decimal(16,4)   default 0.0000             comment '单价',
  fee               decimal(16,2)   default 0.00               comment '手续费',
  amount            decimal(16,2)   default 0.00               comment '成交金额',
  del_flag          char(1)         default '0'                comment '删除标志（0存在 2删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (transaction_id),
  key idx_fin_transaction_scope (family_id, member_id, account_id, asset_id),
  key idx_fin_transaction_trade_date (trade_date)
) engine=innodb auto_increment=1 comment = '交易流水表';

insert into fin_transaction values (1, 1, 1, 1, 1, 'BUY',        '2026-01-08 10:00:00', 10.0000, 1520.0000, 5.00, 15200.00, '0', 'admin', sysdate(), '', null, '开年建仓');
insert into fin_transaction values (2, 1, 1, 1, 1, 'BUY',        '2026-02-06 10:30:00', 5.0000, 1588.0000, 5.00, 7940.00,  '0', 'admin', sysdate(), '', null, '回调加仓');
insert into fin_transaction values (3, 1, 1, 1, 1, 'SELL',       '2026-03-15 09:45:00', 2.0000, 1685.0000, 5.00, 3370.00,  '0', 'admin', sysdate(), '', null, '兑现部分收益');
insert into fin_transaction values (4, 1, 2, 2, 2, 'BUY',        '2026-01-10 15:00:00', 500.0000, 2.0860, 0.00, 1043.00,  '0', 'admin', sysdate(), '', null, '指数基金定投');
insert into fin_transaction values (5, 1, 2, 2, 2, 'BUY',        '2026-02-10 15:00:00', 600.0000, 2.1330, 0.00, 1279.80,  '0', 'admin', sysdate(), '', null, '指数基金加仓');
insert into fin_transaction values (6, 1, 2, 2, 3, 'BUY',        '2026-01-18 15:00:00', 1200.0000, 1.0260, 0.00, 1231.20, '0', 'admin', sysdate(), '', null, '主动基金建仓');
insert into fin_transaction values (7, 1, 2, 2, 3, 'DIVIDEND',   '2026-03-01 09:00:00', 30.0000, 0.0000, 0.00, 30.00,    '0', 'admin', sysdate(), '', null, '基金分红再投资');
insert into fin_transaction values (8, 1, 1, 1, 1, 'TRANSFER_IN','2026-03-20 09:00:00', 1.0000, 0.0000, 0.00, 0.00,      '0', 'admin', sysdate(), '', null, '家人账户转入体验单');

-- ----------------------------
-- 6、行情快照表
-- ----------------------------
create table fin_quote_snapshot (
  quote_id          bigint(20)      not null auto_increment    comment '行情快照ID',
  asset_id          bigint(20)      not null                   comment '资产ID',
  asset_code        varchar(32)     not null                   comment '资产代码',
  asset_name        varchar(100)    not null                   comment '资产名称',
  snapshot_date     date            not null                   comment '快照日期',
  quote_time        datetime        not null                   comment '行情时间',
  provider_code     varchar(32)     default ''                 comment '行情源编码',
  last_price        decimal(16,4)   default 0.0000             comment '最新价',
  open_price        decimal(16,4)   default 0.0000             comment '开盘价',
  prev_close_price  decimal(16,4)   default 0.0000             comment '昨收价',
  change_amount     decimal(16,4)   default 0.0000             comment '涨跌额',
  change_rate       decimal(16,4)   default 0.0000             comment '涨跌幅',
  raw_payload       text                                       comment '原始响应',
  create_time       datetime                                   comment '创建时间',
  primary key (quote_id),
  key idx_fin_quote_asset_time (asset_id, quote_time)
) engine=innodb auto_increment=1 comment = '行情快照表';

insert into fin_quote_snapshot values (1, 1, '600519', '贵州茅台', '2026-03-28', '2026-03-28 15:00:00', 'eastmoney', 1712.6000, 1705.0000, 1698.1000, 14.5000, 0.0085, '{"mock":true}', sysdate());
insert into fin_quote_snapshot values (2, 2, '159915', '创业板ETF', '2026-03-28', '2026-03-28 15:00:00', 'eastmoney', 2.2410, 2.2200, 2.2150, 0.0260, 0.0117, '{"mock":true}', sysdate());
insert into fin_quote_snapshot values (3, 3, '012348', '中欧时代先锋混合', '2026-03-28', '2026-03-28 15:00:00', 'eastmoney', 1.0840, 1.0700, 1.0720, 0.0120, 0.0112, '{"mock":true}', sysdate());

-- ----------------------------
-- 7、持仓快照表
-- ----------------------------
create table fin_holding_snapshot (
  snapshot_id             bigint(20)      not null auto_increment    comment '持仓快照ID',
  family_id               bigint(20)      not null                   comment '家庭ID',
  member_id               bigint(20)      not null                   comment '成员ID',
  account_id              bigint(20)      not null                   comment '账户ID',
  asset_id                bigint(20)      not null                   comment '资产ID',
  snapshot_date           date            not null                   comment '快照日期',
  quote_time              datetime        not null                   comment '行情时间',
  holding_qty             decimal(18,4)   default 0.0000             comment '持仓数量',
  cost_amount             decimal(16,2)   default 0.00               comment '持仓成本',
  market_value            decimal(16,2)   default 0.00               comment '持仓市值',
  floating_profit_amount  decimal(16,2)   default 0.00               comment '浮动收益',
  realized_profit_amount  decimal(16,2)   default 0.00               comment '已实现收益',
  profit_amount           decimal(16,2)   default 0.00               comment '累计收益',
  profit_rate             decimal(16,4)   default 0.0000             comment '收益率',
  position_ratio          decimal(16,4)   default 0.0000             comment '仓位占比',
  create_time             datetime                                   comment '创建时间',
  primary key (snapshot_id),
  key idx_fin_holding_asset_time (asset_id, quote_time)
) engine=innodb auto_increment=1 comment = '持仓快照表';

insert into fin_holding_snapshot values (1, 1, 1, 1, 1, '2026-03-28', '2026-03-28 15:00:00', 14.0000, 21795.00, 23976.40, 2181.40, 320.00, 2501.40, 0.1148, 0.7474, sysdate());
insert into fin_holding_snapshot values (2, 1, 2, 2, 2, '2026-03-28', '2026-03-28 15:00:00', 1100.0000, 2322.80, 2465.10, 142.30, 0.00, 142.30, 0.0613, 0.0769, sysdate());
insert into fin_holding_snapshot values (3, 1, 2, 2, 3, '2026-03-28', '2026-03-28 15:00:00', 1230.0000, 1231.20, 1333.32, 102.12, 30.00, 132.12, 0.1073, 0.0416, sysdate());

-- ----------------------------
-- 8、提醒规则表
-- ----------------------------
create table fin_alert_rule (
  rule_id                 bigint(20)      not null auto_increment    comment '规则ID',
  family_id               bigint(20)      default null               comment '家庭ID',
  member_id               bigint(20)      default null               comment '成员ID',
  asset_id                bigint(20)      default null               comment '资产ID',
  rule_name               varchar(100)    not null                   comment '规则名称',
  rule_type               varchar(30)     not null                   comment '规则类型',
  threshold_value         decimal(16,4)   default null               comment '阈值',
  second_threshold_value  decimal(16,4)   default null               comment '第二阈值',
  enabled                 char(1)         default '1'                comment '是否启用（1是 0否）',
  suggestion_text         varchar(255)    default ''                 comment '建议动作',
  last_triggered_time     datetime        default null               comment '上次触发时间',
  del_flag                char(1)         default '0'                comment '删除标志（0存在 2删除）',
  create_by               varchar(64)     default ''                 comment '创建者',
  create_time             datetime                                   comment '创建时间',
  update_by               varchar(64)     default ''                 comment '更新者',
  update_time             datetime                                   comment '更新时间',
  remark                  varchar(500)    default null               comment '备注',
  primary key (rule_id),
  key idx_fin_alert_rule_scope (family_id, member_id, asset_id)
) engine=innodb auto_increment=1 comment = '提醒规则表';

insert into fin_alert_rule values (1, 1, 1, 1, '茅台收益率目标提醒', 'TARGET_PROFIT_RATE', 0.1000, null, '1', '建议复核仓位，决定是否分批止盈。', null, '0', 'admin', sysdate(), '', null, '收益到达 10% 触发');
insert into fin_alert_rule values (2, 1, 2, 2, '创业板ETF价格区间提醒', 'TARGET_PRICE_RANGE', 2.2000, 2.2800, '1', '建议结合定投节奏调整买入频率。', null, '0', 'admin', sysdate(), '', null, '进入目标区间时提醒');
insert into fin_alert_rule values (3, 1, null, null, '家庭现金比例下限提醒', 'CASH_RATIO_LOW', 0.1500, null, '1', '建议保留必要现金缓冲，避免满仓。', null, '0', 'admin', sysdate(), '', null, '现金比例过低时提醒');

-- ----------------------------
-- 9、提醒事件表
-- ----------------------------
create table fin_alert_event (
  event_id                 bigint(20)      not null auto_increment    comment '事件ID',
  rule_id                  bigint(20)      not null                   comment '规则ID',
  family_id                bigint(20)      default null               comment '家庭ID',
  member_id                bigint(20)      default null               comment '成员ID',
  asset_id                 bigint(20)      default null               comment '资产ID',
  event_title              varchar(100)    not null                   comment '事件标题',
  rule_type                varchar(30)     not null                   comment '规则类型',
  trigger_value            decimal(16,4)   default null               comment '触发值',
  threshold_value          decimal(16,4)   default null               comment '阈值',
  second_threshold_value   decimal(16,4)   default null               comment '第二阈值',
  trigger_time             datetime        not null                   comment '触发时间',
  status                   char(1)         default '0'                comment '处理状态（0未处理 1已读 2忽略）',
  suggestion_text          varchar(255)    default ''                 comment '建议动作',
  detail_text              varchar(500)    default ''                 comment '详情说明',
  create_time              datetime                                   comment '创建时间',
  primary key (event_id),
  key idx_fin_alert_event_rule_time (rule_id, trigger_time)
) engine=innodb auto_increment=1 comment = '提醒事件表';

insert into fin_alert_event values (1, 1, 1, 1, 1, '规则命中：茅台收益率目标提醒', 'TARGET_PROFIT_RATE', 0.1148, 0.1000, null, '2026-03-28 15:05:00', '0', '建议复核仓位，决定是否分批止盈。', '规则[茅台收益率目标提醒]已触发，当前值为0.1148，阈值为0.1', sysdate());
