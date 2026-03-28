-- 理财大师 K线表增量补丁
set names utf8mb4;

drop table if exists fin_kline_snapshot;
create table fin_kline_snapshot (
  kline_id          bigint(20)      not null auto_increment    comment 'K线ID',
  asset_id          bigint(20)      not null                   comment '资产ID',
  trade_date        date            not null                   comment '交易日期',
  provider_code     varchar(32)     default ''                 comment '行情源编码',
  open_price        decimal(16,4)   default 0.0000             comment '开盘价',
  close_price       decimal(16,4)   default 0.0000             comment '收盘价',
  high_price        decimal(16,4)   default 0.0000             comment '最高价',
  low_price         decimal(16,4)   default 0.0000             comment '最低价',
  volume            decimal(20,4)   default 0.0000             comment '成交量',
  turnover_amount   decimal(20,2)   default 0.00               comment '成交额',
  amplitude         decimal(10,4)   default 0.0000             comment '振幅',
  change_rate       decimal(10,4)   default 0.0000             comment '涨跌幅',
  change_amount     decimal(16,4)   default 0.0000             comment '涨跌额',
  turnover_rate     decimal(10,4)   default 0.0000             comment '换手率',
  raw_payload       varchar(500)    default ''                 comment '原始载荷',
  create_time       datetime        default current_timestamp  comment '创建时间',
  primary key (kline_id),
  unique key uk_fin_kline_asset_date (asset_id, trade_date),
  key idx_fin_kline_asset_date (asset_id, trade_date)
) engine=innodb auto_increment=1 comment = '资产日K线表';

insert into fin_kline_snapshot values (1, 1, '2026-03-21', 'eastmoney', 1665.0000, 1678.2000, 1683.0000, 1658.6000, 23560.0000, 3945234000.00, 0.0147, 0.0079, 13.2000, 0.0021, '2026-03-21,1665.00,1678.20,1683.00,1658.60,23560,3945234000.00,1.47,0.79,13.20,0.21', sysdate());
insert into fin_kline_snapshot values (2, 1, '2026-03-24', 'eastmoney', 1679.0000, 1691.5000, 1696.3000, 1672.1000, 21840.0000, 3681112000.00, 0.0144, 0.0079, 13.3000, 0.0019, '2026-03-24,1679.00,1691.50,1696.30,1672.10,21840,3681112000.00,1.44,0.79,13.30,0.19', sysdate());
insert into fin_kline_snapshot values (3, 1, '2026-03-25', 'eastmoney', 1694.3000, 1688.0000, 1701.2000, 1680.6000, 20510.0000, 3469832000.00, 0.0122, -0.0021, -3.5000, 0.0018, '2026-03-25,1694.30,1688.00,1701.20,1680.60,20510,3469832000.00,1.22,-0.21,-3.50,0.18', sysdate());
insert into fin_kline_snapshot values (4, 1, '2026-03-26', 'eastmoney', 1688.5000, 1704.2000, 1708.8000, 1682.7000, 24310.0000, 4132547000.00, 0.0155, 0.0096, 16.2000, 0.0022, '2026-03-26,1688.50,1704.20,1708.80,1682.70,24310,4132547000.00,1.55,0.96,16.20,0.22', sysdate());
insert into fin_kline_snapshot values (5, 1, '2026-03-27', 'eastmoney', 1705.0000, 1698.1000, 1711.4000, 1690.5000, 22880.0000, 3891203000.00, 0.0122, -0.0036, -6.1000, 0.0020, '2026-03-27,1705.00,1698.10,1711.40,1690.50,22880,3891203000.00,1.22,-0.36,-6.10,0.20', sysdate());
insert into fin_kline_snapshot values (6, 1, '2026-03-28', 'eastmoney', 1705.0000, 1712.6000, 1718.9000, 1698.0000, 25120.0000, 4288835000.00, 0.0123, 0.0085, 14.5000, 0.0023, '2026-03-28,1705.00,1712.60,1718.90,1698.00,25120,4288835000.00,1.23,0.85,14.50,0.23', sysdate());
insert into fin_kline_snapshot values (7, 2, '2026-03-21', 'eastmoney', 2.1820, 2.1960, 2.2050, 2.1710, 15823000.0000, 3442100000.00, 0.0156, 0.0064, 0.0140, 0.0970, '2026-03-21,2.182,2.196,2.205,2.171,15823000,3442100000.00,1.56,0.64,0.014,9.70', sysdate());
insert into fin_kline_snapshot values (8, 2, '2026-03-24', 'eastmoney', 2.1970, 2.2140, 2.2180, 2.1880, 16985000.0000, 3745600000.00, 0.0137, 0.0082, 0.0180, 0.1030, '2026-03-24,2.197,2.214,2.218,2.188,16985000,3745600000.00,1.37,0.82,0.018,10.30', sysdate());
insert into fin_kline_snapshot values (9, 2, '2026-03-25', 'eastmoney', 2.2140, 2.2060, 2.2200, 2.1980, 14220000.0000, 3138800000.00, 0.0099, -0.0036, -0.0080, 0.0860, '2026-03-25,2.214,2.206,2.220,2.198,14220000,3138800000.00,0.99,-0.36,-0.008,8.60', sysdate());
insert into fin_kline_snapshot values (10, 2, '2026-03-26', 'eastmoney', 2.2070, 2.2230, 2.2300, 2.2020, 17450000.0000, 3873300000.00, 0.0127, 0.0077, 0.0170, 0.1050, '2026-03-26,2.207,2.223,2.230,2.202,17450000,3873300000.00,1.27,0.77,0.017,10.50', sysdate());
insert into fin_kline_snapshot values (11, 2, '2026-03-27', 'eastmoney', 2.2230, 2.2150, 2.2260, 2.2080, 15110000.0000, 3342200000.00, 0.0081, -0.0036, -0.0080, 0.0910, '2026-03-27,2.223,2.215,2.226,2.208,15110000,3342200000.00,0.81,-0.36,-0.008,9.10', sysdate());
insert into fin_kline_snapshot values (12, 2, '2026-03-28', 'eastmoney', 2.2200, 2.2410, 2.2450, 2.2160, 18960000.0000, 4239800000.00, 0.0131, 0.0117, 0.0260, 0.1140, '2026-03-28,2.220,2.241,2.245,2.216,18960000,4239800000.00,1.31,1.17,0.026,11.40', sysdate());
