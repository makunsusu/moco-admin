SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

drop table if exists sh_device_property;
drop table if exists sh_sync_log;
drop table if exists sh_device;
drop table if exists sh_room;
drop table if exists sh_home;
drop table if exists sh_platform_account;

create table sh_platform_account (
  account_id         bigint(20)      not null auto_increment    comment '平台账号ID',
  platform_code      varchar(30)     not null                   comment '平台编码',
  platform_name      varchar(50)     not null                   comment '平台名称',
  username           varchar(100)    default ''                 comment '登录账号',
  auth_mode          varchar(20)     default 'PASSWORD'         comment '接入方式（PASSWORD/TOKEN/OAUTH）',
  encrypted_password varchar(255)    default ''                 comment '加密密码',
  mijia_user_id      varchar(100)    default ''                 comment '米家 userId',
  encrypted_ssecurity varchar(500)   default ''                 comment '加密 ssecurity',
  encrypted_service_token varchar(1000) default ''              comment '加密 serviceToken',
  encrypted_access_token varchar(2000) default ''               comment '加密 accessToken',
  encrypted_refresh_token varchar(2000) default ''              comment '加密 refreshToken',
  oauth_expires_ts   bigint(20)      default null               comment 'OAuth 过期时间戳',
  oauth_client_id    varchar(100)    default ''                 comment 'OAuth Client ID',
  oauth_redirect_uri varchar(500)    default ''                 comment 'OAuth 回调地址',
  region             varchar(20)     default 'cn'               comment '地区',
  sync_enabled       char(1)         default '0'                comment '同步开关（1启用 0关闭）',
  status             char(1)         default '0'                comment '状态（0正常 1停用）',
  last_sync_time     datetime                                   comment '最近同步时间',
  last_sync_status   varchar(20)     default ''                 comment '最近同步状态',
  last_sync_message  varchar(500)    default ''                 comment '最近同步摘要',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (account_id)
) engine=innodb auto_increment=1 comment = '智能家居平台账号表';

create table sh_home (
  home_id            bigint(20)      not null auto_increment    comment '家庭ID',
  platform_code      varchar(30)     not null                   comment '平台编码',
  cloud_home_id      varchar(64)     not null                   comment '云端家庭ID',
  home_name          varchar(100)    not null                   comment '家庭名称',
  region             varchar(20)     default 'cn'               comment '地区',
  room_count         int(11)         default 0                  comment '房间数量',
  device_count       int(11)         default 0                  comment '设备数量',
  status             char(1)         default '0'                comment '状态（0正常 1停用）',
  last_sync_time     datetime                                   comment '最近同步时间',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (home_id),
  unique key uk_sh_home_cloud_id (cloud_home_id)
) engine=innodb auto_increment=1 comment = '智能家居家庭表';

create table sh_room (
  room_id            bigint(20)      not null auto_increment    comment '房间ID',
  home_id            bigint(20)                                 comment '家庭ID',
  home_name          varchar(100)    default ''                 comment '家庭名称',
  cloud_room_id      varchar(64)     not null                   comment '云端房间ID',
  room_name          varchar(100)    not null                   comment '房间名称',
  device_count       int(11)         default 0                  comment '设备数量',
  status             char(1)         default '0'                comment '状态（0正常 1停用）',
  last_sync_time     datetime                                   comment '最近同步时间',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (room_id),
  unique key uk_sh_room_home_cloud_id (home_id, cloud_room_id)
) engine=innodb auto_increment=1 comment = '智能家居房间表';

create table sh_device (
  device_id          bigint(20)      not null auto_increment    comment '设备ID',
  home_id            bigint(20)                                 comment '家庭ID',
  room_id            bigint(20)                                 comment '房间ID',
  home_name          varchar(100)    default ''                 comment '家庭名称',
  room_name          varchar(100)    default ''                 comment '房间名称',
  did                varchar(64)     not null                   comment '米家设备DID',
  uid                varchar(64)     default ''                 comment '用户UID',
  device_name        varchar(100)    not null                   comment '设备名称',
  model              varchar(100)    default ''                 comment '设备型号',
  device_type        varchar(50)     default ''                 comment '设备类型',
  online_status      char(1)         default '0'                comment '在线状态（1在线 0离线）',
  power_status       varchar(20)     default 'UNKNOWN'          comment '开关状态',
  region             varchar(20)     default 'cn'               comment '地区',
  raw_payload        longtext                                    comment '原始返回快照',
  last_sync_time     datetime                                   comment '最近同步时间',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (device_id),
  unique key uk_sh_device_did (did)
) engine=innodb auto_increment=1 comment = '智能家居设备表';

create table sh_device_property (
  property_id        bigint(20)      not null auto_increment    comment '属性ID',
  device_id          bigint(20)      not null                   comment '设备ID',
  property_key       varchar(255)    not null                   comment '属性键',
  property_value     text                                        comment '属性值',
  property_type      varchar(30)     default 'STRING'           comment '属性类型',
  snapshot_time      datetime                                   comment '快照时间',
  primary key (property_id),
  key idx_sh_property_device_id (device_id)
) engine=innodb auto_increment=1 comment = '智能家居设备属性快照表';

create table sh_sync_log (
  log_id             bigint(20)      not null auto_increment    comment '日志ID',
  platform_code      varchar(30)     not null                   comment '平台编码',
  sync_type          varchar(30)     not null                   comment '同步类型',
  trigger_mode       varchar(30)     not null                   comment '触发方式',
  sync_status        varchar(20)     default ''                 comment '执行状态',
  success_count      int(11)         default 0                  comment '成功数量',
  fail_count         int(11)         default 0                  comment '失败数量',
  error_message      varchar(1000)   default ''                 comment '错误摘要',
  detail_json        longtext                                    comment '明细JSON',
  start_time         datetime                                   comment '开始时间',
  end_time           datetime                                   comment '结束时间',
  primary key (log_id)
) engine=innodb auto_increment=1 comment = '智能家居同步日志表';

SET FOREIGN_KEY_CHECKS = 1;
