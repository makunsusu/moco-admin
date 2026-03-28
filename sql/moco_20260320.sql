SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1、部门表
-- ----------------------------
drop table if exists sys_dept;
create table sys_dept (
  dept_id           bigint(20)      not null auto_increment    comment '部门id',
  parent_id         bigint(20)      default 0                  comment '父部门id',
  ancestors         varchar(50)     default ''                 comment '祖级列表',
  dept_name         varchar(30)     default ''                 comment '部门名称',
  order_num         int(4)          default 0                  comment '显示顺序',
  leader            varchar(20)     default null               comment '负责人',
  phone             varchar(11)     default null               comment '联系电话',
  email             varchar(50)     default null               comment '邮箱',
  status            char(1)         default '0'                comment '部门状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time 	    datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  primary key (dept_id)
) engine=innodb auto_increment=200 comment = '部门表';

-- ----------------------------
-- 初始化-部门表数据
-- ----------------------------
insert into sys_dept values(100,  0,   '0',          'moco科技',   0, 'moco', '15888888888', 'moco@qq.com', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(101,  100, '0,100',      '深圳总公司', 1, 'moco', '15888888888', 'moco@qq.com', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(102,  100, '0,100',      '长沙分公司', 2, 'moco', '15888888888', 'moco@qq.com', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(103,  101, '0,100,101',  '研发部门',   1, 'moco', '15888888888', 'moco@qq.com', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(104,  101, '0,100,101',  '市场部门',   2, 'moco', '15888888888', 'moco@qq.com', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(105,  101, '0,100,101',  '测试部门',   3, 'moco', '15888888888', 'moco@qq.com', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(106,  101, '0,100,101',  '财务部门',   4, 'moco', '15888888888', 'moco@qq.com', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(107,  101, '0,100,101',  '运维部门',   5, 'moco', '15888888888', 'moco@qq.com', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(108,  102, '0,100,102',  '市场部门',   1, 'moco', '15888888888', 'moco@qq.com', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(109,  102, '0,100,102',  '财务部门',   2, 'moco', '15888888888', 'moco@qq.com', '0', '0', 'admin', sysdate(), '', null);


-- ----------------------------
-- 2、用户信息表
-- ----------------------------
drop table if exists sys_user;
create table sys_user (
  user_id           bigint(20)      not null auto_increment    comment '用户ID',
  dept_id           bigint(20)      default null               comment '部门ID',
  user_name         varchar(30)     not null                   comment '用户账号',
  nick_name         varchar(30)     not null                   comment '用户昵称',
  user_type         varchar(2)      default '00'               comment '用户类型（00系统用户）',
  email             varchar(50)     default ''                 comment '用户邮箱',
  phonenumber       varchar(11)     default ''                 comment '手机号码',
  sex               char(1)         default '0'                comment '用户性别（0男 1女 2未知）',
  avatar            varchar(100)    default ''                 comment '头像地址',
  password          varchar(100)    default ''                 comment '密码',
  status            char(1)         default '0'                comment '账号状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  login_ip          varchar(128)    default ''                 comment '最后登录IP',
  login_date        datetime                                   comment '最后登录时间',
  pwd_update_date   datetime                                   comment '密码最后更新时间',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (user_id)
) engine=innodb auto_increment=100 comment = '用户信息表';

-- ----------------------------
-- 初始化-用户信息表数据
-- ----------------------------
insert into sys_user values(1,  103, 'admin', 'moco', '00', 'moco@163.com', '15888888888', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '管理员');
insert into sys_user values(2,  105, 'moco',    'moco', '00', 'moco@qq.com',  '15666666666', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '测试员');


-- ----------------------------
-- 3、岗位信息表
-- ----------------------------
drop table if exists sys_post;
create table sys_post
(
  post_id       bigint(20)      not null auto_increment    comment '岗位ID',
  post_code     varchar(64)     not null                   comment '岗位编码',
  post_name     varchar(50)     not null                   comment '岗位名称',
  post_sort     int(4)          not null                   comment '显示顺序',
  status        char(1)         not null                   comment '状态（0正常 1停用）',
  create_by     varchar(64)     default ''                 comment '创建者',
  create_time   datetime                                   comment '创建时间',
  update_by     varchar(64)     default ''			       comment '更新者',
  update_time   datetime                                   comment '更新时间',
  remark        varchar(500)    default null               comment '备注',
  primary key (post_id)
) engine=innodb comment = '岗位信息表';

-- ----------------------------
-- 初始化-岗位信息表数据
-- ----------------------------
insert into sys_post values(1, 'ceo',  '董事长',    1, '0', 'admin', sysdate(), '', null, '');
insert into sys_post values(2, 'se',   '项目经理',  2, '0', 'admin', sysdate(), '', null, '');
insert into sys_post values(3, 'hr',   '人力资源',  3, '0', 'admin', sysdate(), '', null, '');
insert into sys_post values(4, 'user', '普通员工',  4, '0', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 4、角色信息表
-- ----------------------------
drop table if exists sys_role;
create table sys_role (
  role_id              bigint(20)      not null auto_increment    comment '角色ID',
  role_name            varchar(30)     not null                   comment '角色名称',
  role_key             varchar(100)    not null                   comment '角色权限字符串',
  role_sort            int(4)          not null                   comment '显示顺序',
  data_scope           char(1)         default '1'                comment '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  menu_check_strictly  tinyint(1)      default 1                  comment '菜单树选择项是否关联显示',
  dept_check_strictly  tinyint(1)      default 1                  comment '部门树选择项是否关联显示',
  status               char(1)         not null                   comment '角色状态（0正常 1停用）',
  del_flag             char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by            varchar(64)     default ''                 comment '创建者',
  create_time          datetime                                   comment '创建时间',
  update_by            varchar(64)     default ''                 comment '更新者',
  update_time          datetime                                   comment '更新时间',
  remark               varchar(500)    default null               comment '备注',
  primary key (role_id)
) engine=innodb auto_increment=100 comment = '角色信息表';

-- ----------------------------
-- 初始化-角色信息表数据
-- ----------------------------
insert into sys_role values('1', '超级管理员',  'admin',  1, 1, 1, 1, '0', '0', 'admin', sysdate(), '', null, '超级管理员');
insert into sys_role values('2', '普通角色',    'common', 2, 2, 1, 1, '0', '0', 'admin', sysdate(), '', null, '普通角色');


-- ----------------------------
-- 5、菜单权限表
-- ----------------------------
drop table if exists sys_menu;
create table sys_menu (
  menu_id           bigint(20)      not null auto_increment    comment '菜单ID',
  menu_name         varchar(50)     not null                   comment '菜单名称',
  parent_id         bigint(20)      default 0                  comment '父菜单ID',
  order_num         int(4)          default 0                  comment '显示顺序',
  path              varchar(200)    default ''                 comment '路由地址',
  component         varchar(255)    default null               comment '组件路径',
  query             varchar(255)    default null               comment '路由参数',
  route_name        varchar(50)     default ''                 comment '路由名称',
  is_frame          int(1)          default 1                  comment '是否为外链（0是 1否）',
  is_cache          int(1)          default 0                  comment '是否缓存（0缓存 1不缓存）',
  menu_type         char(1)         default ''                 comment '菜单类型（M目录 C菜单 F按钮）',
  visible           char(1)         default 0                  comment '菜单状态（0显示 1隐藏）',
  status            char(1)         default 0                  comment '菜单状态（0正常 1停用）',
  perms             varchar(100)    default null               comment '权限标识',
  icon              varchar(100)    default '#'                comment '菜单图标',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default ''                 comment '备注',
  primary key (menu_id)
) engine=innodb auto_increment=2000 comment = '菜单权限表';

-- ----------------------------
-- 初始化-菜单信息表数据
-- ----------------------------
-- 一级菜单
insert into sys_menu values('1', '系统管理', '0', '1', 'system',           null, '', '', 1, 0, 'M', '0', '0', '', 'system',   'admin', sysdate(), '', null, '系统管理目录');
insert into sys_menu values('2', '系统监控', '0', '2', 'monitor',          null, '', '', 1, 0, 'M', '0', '0', '', 'monitor',  'admin', sysdate(), '', null, '系统监控目录');
insert into sys_menu values('3', '系统工具', '0', '3', 'tool',             null, '', '', 1, 0, 'M', '0', '0', '', 'tool',     'admin', sysdate(), '', null, '系统工具目录');
insert into sys_menu values('4', 'moco官网', '0', '4', 'https://moco.local', null, '', '', 0, 0, 'M', '0', '0', '', 'guide',    'admin', sysdate(), '', null, 'moco官网地址');
-- 二级菜单
insert into sys_menu values('100',  '用户管理', '1',   '1', 'user',       'system/user/index',        '', '', 1, 0, 'C', '0', '0', 'system:user:list',        'user',          'admin', sysdate(), '', null, '用户管理菜单');
insert into sys_menu values('101',  '角色管理', '1',   '2', 'role',       'system/role/index',        '', '', 1, 0, 'C', '0', '0', 'system:role:list',        'peoples',       'admin', sysdate(), '', null, '角色管理菜单');
insert into sys_menu values('102',  '菜单管理', '1',   '3', 'menu',       'system/menu/index',        '', '', 1, 0, 'C', '0', '0', 'system:menu:list',        'tree-table',    'admin', sysdate(), '', null, '菜单管理菜单');
insert into sys_menu values('103',  '部门管理', '1',   '4', 'dept',       'system/dept/index',        '', '', 1, 0, 'C', '0', '0', 'system:dept:list',        'tree',          'admin', sysdate(), '', null, '部门管理菜单');
insert into sys_menu values('104',  '岗位管理', '1',   '5', 'post',       'system/post/index',        '', '', 1, 0, 'C', '0', '0', 'system:post:list',        'post',          'admin', sysdate(), '', null, '岗位管理菜单');
insert into sys_menu values('105',  '字典管理', '1',   '6', 'dict',       'system/dict/index',        '', '', 1, 0, 'C', '0', '0', 'system:dict:list',        'dict',          'admin', sysdate(), '', null, '字典管理菜单');
insert into sys_menu values('106',  '参数设置', '1',   '7', 'config',     'system/config/index',      '', '', 1, 0, 'C', '0', '0', 'system:config:list',      'edit',          'admin', sysdate(), '', null, '参数设置菜单');
insert into sys_menu values('107',  '通知公告', '1',   '8', 'notice',     'system/notice/index',      '', '', 1, 0, 'C', '0', '0', 'system:notice:list',      'message',       'admin', sysdate(), '', null, '通知公告菜单');
insert into sys_menu values('108',  '日志管理', '1',   '9', 'log',        '',                         '', '', 1, 0, 'M', '0', '0', '',                        'log',           'admin', sysdate(), '', null, '日志管理菜单');
insert into sys_menu values('109',  '在线用户', '2',   '1', 'online',     'monitor/online/index',     '', '', 1, 0, 'C', '0', '0', 'monitor:online:list',     'online',        'admin', sysdate(), '', null, '在线用户菜单');
insert into sys_menu values('110',  '定时任务', '2',   '2', 'job',        'monitor/job/index',        '', '', 1, 0, 'C', '0', '0', 'monitor:job:list',        'job',           'admin', sysdate(), '', null, '定时任务菜单');
insert into sys_menu values('111',  '数据监控', '2',   '3', 'druid',      'monitor/druid/index',      '', '', 1, 0, 'C', '0', '0', 'monitor:druid:list',      'druid',         'admin', sysdate(), '', null, '数据监控菜单');
insert into sys_menu values('112',  '服务监控', '2',   '4', 'server',     'monitor/server/index',     '', '', 1, 0, 'C', '0', '0', 'monitor:server:list',     'server',        'admin', sysdate(), '', null, '服务监控菜单');
insert into sys_menu values('113',  '缓存监控', '2',   '5', 'cache',      'monitor/cache/index',      '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list',      'redis',         'admin', sysdate(), '', null, '缓存监控菜单');
insert into sys_menu values('114',  '缓存列表', '2',   '6', 'cacheList',  'monitor/cache/list',       '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list',      'redis-list',    'admin', sysdate(), '', null, '缓存列表菜单');
insert into sys_menu values('115',  '表单构建', '3',   '1', 'build',      'tool/build/index',         '', '', 1, 0, 'C', '0', '0', 'tool:build:list',         'build',         'admin', sysdate(), '', null, '表单构建菜单');
insert into sys_menu values('116',  '代码生成', '3',   '2', 'gen',        'tool/gen/index',           '', '', 1, 0, 'C', '0', '0', 'tool:gen:list',           'code',          'admin', sysdate(), '', null, '代码生成菜单');
insert into sys_menu values('117',  '系统接口', '3',   '3', 'swagger',    'tool/swagger/index',       '', '', 1, 0, 'C', '0', '0', 'tool:swagger:list',       'swagger',       'admin', sysdate(), '', null, '系统接口菜单');
insert into sys_menu values('2000', '理财大师', '0',   '5', 'finance',      null,                         '', '', 1, 0, 'M', '0', '0', '',                           'money',     'admin', sysdate(), '', null, '理财大师目录');
insert into sys_menu values('2001', '家庭总览', '2000', '1', 'overview',    'finance/overview/index',   '', '', 1, 0, 'C', '0', '0', 'finance:family:list',       'dashboard', 'admin', sysdate(), '', null, '家庭总览菜单');
insert into sys_menu values('2002', '家庭账户', '2000', '2', 'account',     'finance/account/index',    '', '', 1, 0, 'C', '0', '0', 'finance:account:list',      'peoples',   'admin', sysdate(), '', null, '家庭账户菜单');
insert into sys_menu values('2003', '资产与行情', '2000', '3', 'asset',       'finance/asset/index',      '', '', 1, 0, 'C', '0', '0', 'finance:asset:list',        'chart',     'admin', sysdate(), '', null, '资产与行情菜单');
insert into sys_menu values('2004', '交易流水', '2000', '4', 'transaction', 'finance/transaction/index','', '', 1, 0, 'C', '0', '0', 'finance:transaction:list',  'date',      'admin', sysdate(), '', null, '交易流水菜单');
insert into sys_menu values('2005', '提醒中心', '2000', '5', 'alert',       'finance/alert/index',      '', '', 1, 0, 'C', '0', '0', 'finance:alert:list',        'bell',      'admin', sysdate(), '', null, '提醒中心菜单');
insert into sys_menu values('2006', '交易市场', '2000', '6', 'market',      'finance/market/index',     '', '', 1, 0, 'C', '0', '0', 'finance:market:list',       'money',     'admin', sysdate(), '', null, '交易市场菜单');
-- 三级菜单
insert into sys_menu values('500',  '操作日志', '108', '1', 'operlog',    'monitor/operlog/index',    '', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list',    'form',          'admin', sysdate(), '', null, '操作日志菜单');
insert into sys_menu values('501',  '登录日志', '108', '2', 'logininfor', 'monitor/logininfor/index', '', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor',    'admin', sysdate(), '', null, '登录日志菜单');
-- 用户管理按钮
insert into sys_menu values('1000', '用户查询', '100', '1',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:query',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1001', '用户新增', '100', '2',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:add',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1002', '用户修改', '100', '3',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit',           '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1003', '用户删除', '100', '4',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1004', '用户导出', '100', '5',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:export',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1005', '用户导入', '100', '6',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:import',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1006', '重置密码', '100', '7',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd',       '#', 'admin', sysdate(), '', null, '');
-- 角色管理按钮
insert into sys_menu values('1007', '角色查询', '101', '1',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:query',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1008', '角色新增', '101', '2',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:add',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1009', '角色修改', '101', '3',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit',           '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1010', '角色删除', '101', '4',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1011', '角色导出', '101', '5',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:export',         '#', 'admin', sysdate(), '', null, '');
-- 菜单管理按钮
insert into sys_menu values('1012', '菜单查询', '102', '1',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1013', '菜单新增', '102', '2',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1014', '菜单修改', '102', '3',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit',           '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1015', '菜单删除', '102', '4',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove',         '#', 'admin', sysdate(), '', null, '');
-- 部门管理按钮
insert into sys_menu values('1016', '部门查询', '103', '1',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1017', '部门新增', '103', '2',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1018', '部门修改', '103', '3',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit',           '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1019', '部门删除', '103', '4',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove',         '#', 'admin', sysdate(), '', null, '');
-- 岗位管理按钮
insert into sys_menu values('1020', '岗位查询', '104', '1',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:query',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1021', '岗位新增', '104', '2',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:add',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1022', '岗位修改', '104', '3',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit',           '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1023', '岗位删除', '104', '4',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1024', '岗位导出', '104', '5',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:export',         '#', 'admin', sysdate(), '', null, '');
-- 字典管理按钮
insert into sys_menu values('1025', '字典查询', '105', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:query',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1026', '字典新增', '105', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:add',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1027', '字典修改', '105', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit',           '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1028', '字典删除', '105', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1029', '字典导出', '105', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:export',         '#', 'admin', sysdate(), '', null, '');
-- 参数设置按钮
insert into sys_menu values('1030', '参数查询', '106', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:query',        '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1031', '参数新增', '106', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:add',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1032', '参数修改', '106', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:edit',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1033', '参数删除', '106', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:remove',       '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1034', '参数导出', '106', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:export',       '#', 'admin', sysdate(), '', null, '');
-- 通知公告按钮
insert into sys_menu values('1035', '公告查询', '107', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:query',        '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1036', '公告新增', '107', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:add',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1037', '公告修改', '107', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:edit',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1038', '公告删除', '107', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:remove',       '#', 'admin', sysdate(), '', null, '');
-- 操作日志按钮
insert into sys_menu values('1039', '操作查询', '500', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query',      '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1040', '操作删除', '500', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove',     '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1041', '日志导出', '500', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export',     '#', 'admin', sysdate(), '', null, '');
-- 登录日志按钮
insert into sys_menu values('1042', '登录查询', '501', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1043', '登录删除', '501', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1044', '日志导出', '501', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1045', '账户解锁', '501', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:unlock',  '#', 'admin', sysdate(), '', null, '');
-- 在线用户按钮
insert into sys_menu values('1046', '在线查询', '109', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query',       '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1047', '批量强退', '109', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1048', '单条强退', '109', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', sysdate(), '', null, '');
-- 定时任务按钮
insert into sys_menu values('1049', '任务查询', '110', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:query',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1050', '任务新增', '110', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:add',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1051', '任务修改', '110', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:edit',           '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1052', '任务删除', '110', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:remove',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1053', '状态修改', '110', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:changeStatus',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1054', '任务导出', '110', '6', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:export',         '#', 'admin', sysdate(), '', null, '');
-- 代码生成按钮
insert into sys_menu values('1055', '生成查询', '116', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:query',             '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1056', '生成修改', '116', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:edit',              '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1057', '生成删除', '116', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:remove',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1058', '导入代码', '116', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:import',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1059', '预览代码', '116', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:preview',           '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1060', '生成代码', '116', '6', '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:code',              '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2010', '家庭查询', '2001', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:family:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2011', '家庭新增', '2001', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:family:add',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2012', '家庭修改', '2001', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:family:edit',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2013', '家庭删除', '2001', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:family:remove','#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2014', '账户查询', '2002', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:account:query','#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2015', '账户新增', '2002', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:account:add',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2016', '账户修改', '2002', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:account:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2017', '账户删除', '2002', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:account:remove','#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2018', '资产查询', '2003', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:asset:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2019', '资产新增', '2003', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:asset:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2020', '资产修改', '2003', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:asset:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2021', '资产删除', '2003', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:asset:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2022', '行情刷新', '2003', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:quote:refresh','#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2023', '行情查询', '2003', '6', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:quote:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2024', '交易查询', '2004', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:transaction:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2025', '交易新增', '2004', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:transaction:add',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2026', '交易修改', '2004', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:transaction:edit',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2027', '交易删除', '2004', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:transaction:remove','#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2028', '交易导出', '2004', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:transaction:export','#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2029', '提醒查询', '2005', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:alert:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2030', '提醒新增', '2005', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:alert:add',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2031', '提醒修改', '2005', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:alert:edit',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2032', '提醒删除', '2005', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:alert:remove','#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2033', '市场查询', '2006', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:market:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2034', '市场新增', '2006', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:market:add',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2035', '市场修改', '2006', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:market:edit',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2036', '市场删除', '2006', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:market:remove','#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2037', '市场导出', '2006', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:market:export','#', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 6、用户和角色关联表  用户N-1角色
-- ----------------------------
drop table if exists sys_user_role;
create table sys_user_role (
  user_id   bigint(20) not null comment '用户ID',
  role_id   bigint(20) not null comment '角色ID',
  primary key(user_id, role_id)
) engine=innodb comment = '用户和角色关联表';

-- ----------------------------
-- 初始化-用户和角色关联表数据
-- ----------------------------
insert into sys_user_role values ('1', '1');
insert into sys_user_role values ('2', '2');


-- ----------------------------
-- 7、角色和菜单关联表  角色1-N菜单
-- ----------------------------
drop table if exists sys_role_menu;
create table sys_role_menu (
  role_id   bigint(20) not null comment '角色ID',
  menu_id   bigint(20) not null comment '菜单ID',
  primary key(role_id, menu_id)
) engine=innodb comment = '角色和菜单关联表';

-- ----------------------------
-- 初始化-角色和菜单关联表数据
-- ----------------------------
insert into sys_role_menu values ('2', '1');
insert into sys_role_menu values ('2', '2');
insert into sys_role_menu values ('2', '3');
insert into sys_role_menu values ('2', '4');
insert into sys_role_menu values ('2', '100');
insert into sys_role_menu values ('2', '101');
insert into sys_role_menu values ('2', '102');
insert into sys_role_menu values ('2', '103');
insert into sys_role_menu values ('2', '104');
insert into sys_role_menu values ('2', '105');
insert into sys_role_menu values ('2', '106');
insert into sys_role_menu values ('2', '107');
insert into sys_role_menu values ('2', '108');
insert into sys_role_menu values ('2', '109');
insert into sys_role_menu values ('2', '110');
insert into sys_role_menu values ('2', '111');
insert into sys_role_menu values ('2', '112');
insert into sys_role_menu values ('2', '113');
insert into sys_role_menu values ('2', '114');
insert into sys_role_menu values ('2', '115');
insert into sys_role_menu values ('2', '116');
insert into sys_role_menu values ('2', '117');
insert into sys_role_menu values ('2', '500');
insert into sys_role_menu values ('2', '501');
insert into sys_role_menu values ('2', '1000');
insert into sys_role_menu values ('2', '1001');
insert into sys_role_menu values ('2', '1002');
insert into sys_role_menu values ('2', '1003');
insert into sys_role_menu values ('2', '1004');
insert into sys_role_menu values ('2', '1005');
insert into sys_role_menu values ('2', '1006');
insert into sys_role_menu values ('2', '1007');
insert into sys_role_menu values ('2', '1008');
insert into sys_role_menu values ('2', '1009');
insert into sys_role_menu values ('2', '1010');
insert into sys_role_menu values ('2', '1011');
insert into sys_role_menu values ('2', '1012');
insert into sys_role_menu values ('2', '1013');
insert into sys_role_menu values ('2', '1014');
insert into sys_role_menu values ('2', '1015');
insert into sys_role_menu values ('2', '1016');
insert into sys_role_menu values ('2', '1017');
insert into sys_role_menu values ('2', '1018');
insert into sys_role_menu values ('2', '1019');
insert into sys_role_menu values ('2', '1020');
insert into sys_role_menu values ('2', '1021');
insert into sys_role_menu values ('2', '1022');
insert into sys_role_menu values ('2', '1023');
insert into sys_role_menu values ('2', '1024');
insert into sys_role_menu values ('2', '1025');
insert into sys_role_menu values ('2', '1026');
insert into sys_role_menu values ('2', '1027');
insert into sys_role_menu values ('2', '1028');
insert into sys_role_menu values ('2', '1029');
insert into sys_role_menu values ('2', '1030');
insert into sys_role_menu values ('2', '1031');
insert into sys_role_menu values ('2', '1032');
insert into sys_role_menu values ('2', '1033');
insert into sys_role_menu values ('2', '1034');
insert into sys_role_menu values ('2', '1035');
insert into sys_role_menu values ('2', '1036');
insert into sys_role_menu values ('2', '1037');
insert into sys_role_menu values ('2', '1038');
insert into sys_role_menu values ('2', '1039');
insert into sys_role_menu values ('2', '1040');
insert into sys_role_menu values ('2', '1041');
insert into sys_role_menu values ('2', '1042');
insert into sys_role_menu values ('2', '1043');
insert into sys_role_menu values ('2', '1044');
insert into sys_role_menu values ('2', '1045');
insert into sys_role_menu values ('2', '1046');
insert into sys_role_menu values ('2', '1047');
insert into sys_role_menu values ('2', '1048');
insert into sys_role_menu values ('2', '1049');
insert into sys_role_menu values ('2', '1050');
insert into sys_role_menu values ('2', '1051');
insert into sys_role_menu values ('2', '1052');
insert into sys_role_menu values ('2', '1053');
insert into sys_role_menu values ('2', '1054');
insert into sys_role_menu values ('2', '1055');
insert into sys_role_menu values ('2', '1056');
insert into sys_role_menu values ('2', '1057');
insert into sys_role_menu values ('2', '1058');
insert into sys_role_menu values ('2', '1059');
insert into sys_role_menu values ('2', '1060');
insert into sys_role_menu values ('2', '2000');
insert into sys_role_menu values ('2', '2001');
insert into sys_role_menu values ('2', '2002');
insert into sys_role_menu values ('2', '2003');
insert into sys_role_menu values ('2', '2004');
insert into sys_role_menu values ('2', '2005');
insert into sys_role_menu values ('2', '2006');
insert into sys_role_menu values ('2', '2010');
insert into sys_role_menu values ('2', '2011');
insert into sys_role_menu values ('2', '2012');
insert into sys_role_menu values ('2', '2013');
insert into sys_role_menu values ('2', '2014');
insert into sys_role_menu values ('2', '2015');
insert into sys_role_menu values ('2', '2020');
insert into sys_role_menu values ('2', '2021');
insert into sys_role_menu values ('2', '2022');
insert into sys_role_menu values ('2', '2023');
insert into sys_role_menu values ('2', '2024');
insert into sys_role_menu values ('2', '2025');
insert into sys_role_menu values ('2', '2030');
insert into sys_role_menu values ('2', '2031');
insert into sys_role_menu values ('2', '2032');
insert into sys_role_menu values ('2', '2033');
insert into sys_role_menu values ('2', '2034');
insert into sys_role_menu values ('2', '2035');
insert into sys_role_menu values ('2', '2036');
insert into sys_role_menu values ('2', '2037');

-- ----------------------------
-- 8、角色和部门关联表  角色1-N部门
-- ----------------------------
drop table if exists sys_role_dept;
create table sys_role_dept (
  role_id   bigint(20) not null comment '角色ID',
  dept_id   bigint(20) not null comment '部门ID',
  primary key(role_id, dept_id)
) engine=innodb comment = '角色和部门关联表';

-- ----------------------------
-- 初始化-角色和部门关联表数据
-- ----------------------------
insert into sys_role_dept values ('2', '100');
insert into sys_role_dept values ('2', '101');
insert into sys_role_dept values ('2', '105');


-- ----------------------------
-- 9、用户与岗位关联表  用户1-N岗位
-- ----------------------------
drop table if exists sys_user_post;
create table sys_user_post
(
  user_id   bigint(20) not null comment '用户ID',
  post_id   bigint(20) not null comment '岗位ID',
  primary key (user_id, post_id)
) engine=innodb comment = '用户与岗位关联表';

-- ----------------------------
-- 初始化-用户与岗位关联表数据
-- ----------------------------
insert into sys_user_post values ('1', '1');
insert into sys_user_post values ('2', '2');


-- ----------------------------
-- 10、操作日志记录
-- ----------------------------
drop table if exists sys_oper_log;
create table sys_oper_log (
  oper_id           bigint(20)      not null auto_increment    comment '日志主键',
  title             varchar(50)     default ''                 comment '模块标题',
  business_type     int(2)          default 0                  comment '业务类型（0其它 1新增 2修改 3删除）',
  method            varchar(200)    default ''                 comment '方法名称',
  request_method    varchar(10)     default ''                 comment '请求方式',
  operator_type     int(1)          default 0                  comment '操作类别（0其它 1后台用户 2手机端用户）',
  oper_name         varchar(50)     default ''                 comment '操作人员',
  dept_name         varchar(50)     default ''                 comment '部门名称',
  oper_url          varchar(255)    default ''                 comment '请求URL',
  oper_ip           varchar(128)    default ''                 comment '主机地址',
  oper_location     varchar(255)    default ''                 comment '操作地点',
  oper_param        varchar(2000)   default ''                 comment '请求参数',
  json_result       varchar(2000)   default ''                 comment '返回参数',
  status            int(1)          default 0                  comment '操作状态（0正常 1异常）',
  error_msg         varchar(2000)   default ''                 comment '错误消息',
  oper_time         datetime                                   comment '操作时间',
  cost_time         bigint(20)      default 0                  comment '消耗时间',
  primary key (oper_id),
  key idx_sys_oper_log_bt (business_type),
  key idx_sys_oper_log_s  (status),
  key idx_sys_oper_log_ot (oper_time)
) engine=innodb auto_increment=100 comment = '操作日志记录';


-- ----------------------------
-- 11、字典类型表
-- ----------------------------
drop table if exists sys_dict_type;
create table sys_dict_type
(
  dict_id          bigint(20)      not null auto_increment    comment '字典主键',
  dict_name        varchar(100)    default ''                 comment '字典名称',
  dict_type        varchar(100)    default ''                 comment '字典类型',
  status           char(1)         default '0'                comment '状态（0正常 1停用）',
  create_by        varchar(64)     default ''                 comment '创建者',
  create_time      datetime                                   comment '创建时间',
  update_by        varchar(64)     default ''                 comment '更新者',
  update_time      datetime                                   comment '更新时间',
  remark           varchar(500)    default null               comment '备注',
  primary key (dict_id),
  unique (dict_type)
) engine=innodb auto_increment=100 comment = '字典类型表';

insert into sys_dict_type values(1,  '用户性别', 'sys_user_sex',        '0', 'admin', sysdate(), '', null, '用户性别列表');
insert into sys_dict_type values(2,  '菜单状态', 'sys_show_hide',       '0', 'admin', sysdate(), '', null, '菜单状态列表');
insert into sys_dict_type values(3,  '系统开关', 'sys_normal_disable',  '0', 'admin', sysdate(), '', null, '系统开关列表');
insert into sys_dict_type values(4,  '任务状态', 'sys_job_status',      '0', 'admin', sysdate(), '', null, '任务状态列表');
insert into sys_dict_type values(5,  '任务分组', 'sys_job_group',       '0', 'admin', sysdate(), '', null, '任务分组列表');
insert into sys_dict_type values(6,  '系统是否', 'sys_yes_no',          '0', 'admin', sysdate(), '', null, '系统是否列表');
insert into sys_dict_type values(7,  '通知类型', 'sys_notice_type',     '0', 'admin', sysdate(), '', null, '通知类型列表');
insert into sys_dict_type values(8,  '通知状态', 'sys_notice_status',   '0', 'admin', sysdate(), '', null, '通知状态列表');
insert into sys_dict_type values(9,  '操作类型', 'sys_oper_type',       '0', 'admin', sysdate(), '', null, '操作类型列表');
insert into sys_dict_type values(10, '系统状态', 'sys_common_status',   '0', 'admin', sysdate(), '', null, '登录状态列表');


-- ----------------------------
-- 12、字典数据表
-- ----------------------------
drop table if exists sys_dict_data;
create table sys_dict_data
(
  dict_code        bigint(20)      not null auto_increment    comment '字典编码',
  dict_sort        int(4)          default 0                  comment '字典排序',
  dict_label       varchar(100)    default ''                 comment '字典标签',
  dict_value       varchar(100)    default ''                 comment '字典键值',
  dict_type        varchar(100)    default ''                 comment '字典类型',
  css_class        varchar(100)    default null               comment '样式属性（其他样式扩展）',
  list_class       varchar(100)    default null               comment '表格回显样式',
  is_default       char(1)         default 'N'                comment '是否默认（Y是 N否）',
  status           char(1)         default '0'                comment '状态（0正常 1停用）',
  create_by        varchar(64)     default ''                 comment '创建者',
  create_time      datetime                                   comment '创建时间',
  update_by        varchar(64)     default ''                 comment '更新者',
  update_time      datetime                                   comment '更新时间',
  remark           varchar(500)    default null               comment '备注',
  primary key (dict_code)
) engine=innodb auto_increment=100 comment = '字典数据表';

insert into sys_dict_data values(1,  1,  '男',       '0',       'sys_user_sex',        '',   '',        'Y', '0', 'admin', sysdate(), '', null, '性别男');
insert into sys_dict_data values(2,  2,  '女',       '1',       'sys_user_sex',        '',   '',        'N', '0', 'admin', sysdate(), '', null, '性别女');
insert into sys_dict_data values(3,  3,  '未知',     '2',       'sys_user_sex',        '',   '',        'N', '0', 'admin', sysdate(), '', null, '性别未知');
insert into sys_dict_data values(4,  1,  '显示',     '0',       'sys_show_hide',       '',   'primary', 'Y', '0', 'admin', sysdate(), '', null, '显示菜单');
insert into sys_dict_data values(5,  2,  '隐藏',     '1',       'sys_show_hide',       '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '隐藏菜单');
insert into sys_dict_data values(6,  1,  '正常',     '0',       'sys_normal_disable',  '',   'primary', 'Y', '0', 'admin', sysdate(), '', null, '正常状态');
insert into sys_dict_data values(7,  2,  '停用',     '1',       'sys_normal_disable',  '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '停用状态');
insert into sys_dict_data values(8,  1,  '正常',     '0',       'sys_job_status',      '',   'primary', 'Y', '0', 'admin', sysdate(), '', null, '正常状态');
insert into sys_dict_data values(9,  2,  '暂停',     '1',       'sys_job_status',      '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '停用状态');
insert into sys_dict_data values(10, 1,  '默认',     'DEFAULT', 'sys_job_group',       '',   '',        'Y', '0', 'admin', sysdate(), '', null, '默认分组');
insert into sys_dict_data values(11, 2,  '系统',     'SYSTEM',  'sys_job_group',       '',   '',        'N', '0', 'admin', sysdate(), '', null, '系统分组');
insert into sys_dict_data values(12, 1,  '是',       'Y',       'sys_yes_no',          '',   'primary', 'Y', '0', 'admin', sysdate(), '', null, '系统默认是');
insert into sys_dict_data values(13, 2,  '否',       'N',       'sys_yes_no',          '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '系统默认否');
insert into sys_dict_data values(14, 1,  '通知',     '1',       'sys_notice_type',     '',   'warning', 'Y', '0', 'admin', sysdate(), '', null, '通知');
insert into sys_dict_data values(15, 2,  '公告',     '2',       'sys_notice_type',     '',   'success', 'N', '0', 'admin', sysdate(), '', null, '公告');
insert into sys_dict_data values(16, 1,  '正常',     '0',       'sys_notice_status',   '',   'primary', 'Y', '0', 'admin', sysdate(), '', null, '正常状态');
insert into sys_dict_data values(17, 2,  '关闭',     '1',       'sys_notice_status',   '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '关闭状态');
insert into sys_dict_data values(18, 99, '其他',     '0',       'sys_oper_type',       '',   'info',    'N', '0', 'admin', sysdate(), '', null, '其他操作');
insert into sys_dict_data values(19, 1,  '新增',     '1',       'sys_oper_type',       '',   'info',    'N', '0', 'admin', sysdate(), '', null, '新增操作');
insert into sys_dict_data values(20, 2,  '修改',     '2',       'sys_oper_type',       '',   'info',    'N', '0', 'admin', sysdate(), '', null, '修改操作');
insert into sys_dict_data values(21, 3,  '删除',     '3',       'sys_oper_type',       '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '删除操作');
insert into sys_dict_data values(22, 4,  '授权',     '4',       'sys_oper_type',       '',   'primary', 'N', '0', 'admin', sysdate(), '', null, '授权操作');
insert into sys_dict_data values(23, 5,  '导出',     '5',       'sys_oper_type',       '',   'warning', 'N', '0', 'admin', sysdate(), '', null, '导出操作');
insert into sys_dict_data values(24, 6,  '导入',     '6',       'sys_oper_type',       '',   'warning', 'N', '0', 'admin', sysdate(), '', null, '导入操作');
insert into sys_dict_data values(25, 7,  '强退',     '7',       'sys_oper_type',       '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '强退操作');
insert into sys_dict_data values(26, 8,  '生成代码', '8',       'sys_oper_type',       '',   'warning', 'N', '0', 'admin', sysdate(), '', null, '生成操作');
insert into sys_dict_data values(27, 9,  '清空数据', '9',       'sys_oper_type',       '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '清空操作');
insert into sys_dict_data values(28, 1,  '成功',     '0',       'sys_common_status',   '',   'primary', 'N', '0', 'admin', sysdate(), '', null, '正常状态');
insert into sys_dict_data values(29, 2,  '失败',     '1',       'sys_common_status',   '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '停用状态');


-- ----------------------------
-- 13、参数配置表
-- ----------------------------
drop table if exists sys_config;
create table sys_config (
  config_id         int(5)          not null auto_increment    comment '参数主键',
  config_name       varchar(100)    default ''                 comment '参数名称',
  config_key        varchar(100)    default ''                 comment '参数键名',
  config_value      varchar(500)    default ''                 comment '参数键值',
  config_type       char(1)         default 'N'                comment '系统内置（Y是 N否）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (config_id)
) engine=innodb auto_increment=100 comment = '参数配置表';

insert into sys_config values(1, '主框架页-默认皮肤样式名称',     'sys.index.skinName',               'skin-blue',     'Y', 'admin', sysdate(), '', null, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow' );
insert into sys_config values(2, '用户管理-账号初始密码',         'sys.user.initPassword',            '123456',        'Y', 'admin', sysdate(), '', null, '初始化密码 123456' );
insert into sys_config values(3, '主框架页-侧边栏主题',           'sys.index.sideTheme',              'theme-dark',    'Y', 'admin', sysdate(), '', null, '深色主题theme-dark，浅色主题theme-light' );
insert into sys_config values(4, '账号自助-验证码开关',           'sys.account.captchaEnabled',       'true',          'Y', 'admin', sysdate(), '', null, '是否开启验证码功能（true开启，false关闭）');
insert into sys_config values(5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser',         'false',         'Y', 'admin', sysdate(), '', null, '是否开启注册用户功能（true开启，false关闭）');
insert into sys_config values(6, '用户登录-黑名单列表',           'sys.login.blackIPList',            '',              'Y', 'admin', sysdate(), '', null, '设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）');
insert into sys_config values(7, '用户管理-初始密码修改策略',     'sys.account.initPasswordModify',   '1',             'Y', 'admin', sysdate(), '', null, '0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框');
insert into sys_config values(8, '用户管理-账号密码更新周期',     'sys.account.passwordValidateDays', '0',             'Y', 'admin', sysdate(), '', null, '密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框');


-- ----------------------------
-- 14、系统访问记录
-- ----------------------------
drop table if exists sys_logininfor;
create table sys_logininfor (
  info_id        bigint(20)     not null auto_increment   comment '访问ID',
  user_name      varchar(50)    default ''                comment '用户账号',
  ipaddr         varchar(128)   default ''                comment '登录IP地址',
  login_location varchar(255)   default ''                comment '登录地点',
  browser        varchar(50)    default ''                comment '浏览器类型',
  os             varchar(50)    default ''                comment '操作系统',
  status         char(1)        default '0'               comment '登录状态（0成功 1失败）',
  msg            varchar(255)   default ''                comment '提示消息',
  login_time     datetime                                 comment '访问时间',
  primary key (info_id),
  key idx_sys_logininfor_s  (status),
  key idx_sys_logininfor_lt (login_time)
) engine=innodb auto_increment=100 comment = '系统访问记录';


-- ----------------------------
-- 15、定时任务调度表
-- ----------------------------
drop table if exists sys_job;
create table sys_job (
  job_id              bigint(20)    not null auto_increment    comment '任务ID',
  job_name            varchar(64)   default ''                 comment '任务名称',
  job_group           varchar(64)   default 'DEFAULT'          comment '任务组名',
  invoke_target       varchar(500)  not null                   comment '调用目标字符串',
  cron_expression     varchar(255)  default ''                 comment 'cron执行表达式',
  misfire_policy      varchar(20)   default '3'                comment '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  concurrent          char(1)       default '1'                comment '是否并发执行（0允许 1禁止）',
  status              char(1)       default '0'                comment '状态（0正常 1暂停）',
  create_by           varchar(64)   default ''                 comment '创建者',
  create_time         datetime                                 comment '创建时间',
  update_by           varchar(64)   default ''                 comment '更新者',
  update_time         datetime                                 comment '更新时间',
  remark              varchar(500)  default ''                 comment '备注信息',
  primary key (job_id, job_name, job_group)
) engine=innodb auto_increment=100 comment = '定时任务调度表';

insert into sys_job values(1, '系统默认（无参）', 'DEFAULT', 'mocoTask.mocoNoParams',        '0/10 * * * * ?', '3', '1', '1', 'admin', sysdate(), '', null, '');
insert into sys_job values(2, '系统默认（有参）', 'DEFAULT', 'mocoTask.mocoParams(\'moco\')',  '0/15 * * * * ?', '3', '1', '1', 'admin', sysdate(), '', null, '');
insert into sys_job values(3, '系统默认（多参）', 'DEFAULT', 'mocoTask.mocoMultipleParams(\'moco\', true, 2000L, 316.50D, 100)',  '0/20 * * * * ?', '3', '1', '1', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 16、定时任务调度日志表
-- ----------------------------
drop table if exists sys_job_log;
create table sys_job_log (
  job_log_id          bigint(20)     not null auto_increment    comment '任务日志ID',
  job_name            varchar(64)    not null                   comment '任务名称',
  job_group           varchar(64)    not null                   comment '任务组名',
  invoke_target       varchar(500)   not null                   comment '调用目标字符串',
  job_message         varchar(500)                              comment '日志信息',
  status              char(1)        default '0'                comment '执行状态（0正常 1失败）',
  exception_info      varchar(2000)  default ''                 comment '异常信息',
  start_time          datetime                                  comment '执行开始时间',
  end_time            datetime                                  comment '执行结束时间',
  create_time         datetime                                  comment '创建时间',
  primary key (job_log_id)
) engine=innodb comment = '定时任务调度日志表';


-- ----------------------------
-- 17、通知公告表
-- ----------------------------
drop table if exists sys_notice;
create table sys_notice (
  notice_id         int(4)          not null auto_increment    comment '公告ID',
  notice_title      varchar(50)     not null                   comment '公告标题',
  notice_type       char(1)         not null                   comment '公告类型（1通知 2公告）',
  notice_content    longblob        default null               comment '公告内容',
  status            char(1)         default '0'                comment '公告状态（0正常 1关闭）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(255)    default null               comment '备注',
  primary key (notice_id)
) engine=innodb auto_increment=10 comment = '通知公告表';

-- ----------------------------
-- 初始化-公告信息表数据
-- ----------------------------
insert into sys_notice values('1', '温馨提醒：2018-07-01 moco新版本发布啦', '2', '新版本内容', '0', 'admin', sysdate(), '', null, '管理员');
insert into sys_notice values('2', '维护通知：2018-07-01 moco系统凌晨维护', '1', '维护内容',   '0', 'admin', sysdate(), '', null, '管理员');
insert into sys_notice values('3', 'moco开源框架介绍', '1', '<p><span style=\"color: rgb(230, 0, 0);\">项目介绍</span></p><p><font color=\"#333333\">moco开源项目是为企业用户定制的后台脚手架框架，为企业打造的一站式解决方案，降低企业开发成本，提升开发效率。主要包括用户管理、角色管理、部门管理、菜单管理、参数管理、字典管理、</font><span style=\"color: rgb(51, 51, 51);\">岗位管理</span><span style=\"color: rgb(51, 51, 51);\">、定时任务</span><span style=\"color: rgb(51, 51, 51);\">、</span><span style=\"color: rgb(51, 51, 51);\">服务监控、登录日志、操作日志、代码生成等功能。其中，还支持多数据源、数据权限、国际化、Redis缓存、Docker部署、滑动验证码、第三方认证登录、分布式事务、</span><font color=\"#333333\">分布式文件存储</font><span style=\"color: rgb(51, 51, 51);\">、分库分表处理等技术特点。</span></p><p><img src=\"https://foruda.gitee.com/images/1773931848342439032/a4d22313_1815095.png\" style=\"width: 64px;\"><br></p><p><span style=\"color: rgb(230, 0, 0);\">官网及演示</span></p><p><span style=\"color: rgb(51, 51, 51);\">moco官网地址：&nbsp;</span><a href=\"https://moco.local\" target=\"_blank\">https://moco.local</a><a href=\"https://moco.local\" target=\"_blank\"></a></p><p><span style=\"color: rgb(51, 51, 51);\">moco文档地址：&nbsp;</span><a href=\"https://moco.local/docs\" target=\"_blank\">https://moco.local/docs</a><br></p><p><span style=\"color: rgb(51, 51, 51);\">演示地址【不分离版】：&nbsp;</span><a href=\"https://moco.local/demo\" target=\"_blank\">https://moco.local/demo</a></p><p><span style=\"color: rgb(51, 51, 51);\">演示地址【分离版本】：&nbsp;</span><a href=\"https://moco.local/demo/vue\" target=\"_blank\">https://moco.local/demo/vue</a></p><p><span style=\"color: rgb(51, 51, 51);\">演示地址【微服务版】：&nbsp;</span><a href=\"https://moco.local/demo/cloud\" target=\"_blank\">https://moco.local/demo/cloud</a></p><p><span style=\"color: rgb(51, 51, 51);\">演示地址【移动端版】：&nbsp;</span><a href=\"https://moco.local/demo/h5\" target=\"_blank\">https://moco.local/demo/h5</a></p><p><br style=\"color: rgb(48, 49, 51); font-family: &quot;Helvetica Neue&quot;, Helvetica, Arial, sans-serif; font-size: 12px;\"></p>', '0', 'admin', sysdate(), '', null, '管理员');


-- ----------------------------
-- 18、交易市场表
-- ----------------------------
drop table if exists fin_market;
create table fin_market (
  market_id         bigint(20)      not null auto_increment    comment '市场ID',
  market_code       varchar(32)     not null                   comment '市场编码',
  market_name       varchar(100)    not null                   comment '市场名称',
  market_short_name varchar(50)     default ''                 comment '市场简称',
  market_type       varchar(50)     default ''                 comment '市场类型',
  market_region     varchar(50)     default ''                 comment '所属地区',
  website           varchar(255)    default ''                 comment '官网地址',
  market_sort       int(4)          default 0                  comment '显示顺序',
  status            char(1)         default '0'                comment '状态（0正常 1停用）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (market_id),
  unique key uk_market_code (market_code),
  unique key uk_market_name (market_name)
) engine=innodb auto_increment=1 comment = '交易市场表';

-- ----------------------------
-- 初始化-交易市场表数据
-- ----------------------------
insert into fin_market values (1, 'SSE',  '上海证券交易所',       '上交所',   '证券交易所', '中国大陆', 'https://www.sse.com.cn',   1, '0', 'admin', sysdate(), '', null, 'A股核心证券交易所');
insert into fin_market values (2, 'SZSE', '深圳证券交易所',       '深交所',   '证券交易所', '中国大陆', 'https://www.szse.cn',      2, '0', 'admin', sysdate(), '', null, '创业板与主板重要市场');
insert into fin_market values (3, 'BSE',  '北京证券交易所',       '北交所',   '证券交易所', '中国大陆', 'https://www.bse.cn',       3, '0', 'admin', sysdate(), '', null, '服务创新型中小企业');
insert into fin_market values (4, 'CFFEX','中国金融期货交易所',   '中金所',   '期货交易所', '中国大陆', 'http://www.cffex.com.cn',  4, '0', 'admin', sysdate(), '', null, '股指与国债期货市场');
insert into fin_market values (5, 'SHFE', '上海期货交易所',       '上期所',   '期货交易所', '中国大陆', 'https://www.shfe.com.cn',  5, '0', 'admin', sysdate(), '', null, '有色金属与能源衍生品');
insert into fin_market values (6, 'DCE',  '大连商品交易所',       '大商所',   '期货交易所', '中国大陆', 'http://www.dce.com.cn',    6, '0', 'admin', sysdate(), '', null, '农产品与化工品市场');
insert into fin_market values (7, 'CZCE', '郑州商品交易所',       '郑商所',   '期货交易所', '中国大陆', 'http://www.czce.com.cn',   7, '0', 'admin', sysdate(), '', null, '农产品与商品期货市场');
insert into fin_market values (8, 'INE',  '上海国际能源交易中心', '上能所',   '能源交易中心', '中国大陆', 'https://www.ine.cn',      8, '0', 'admin', sysdate(), '', null, '原油等能源衍生品市场');
insert into fin_market values (9, 'GFEX', '广州期货交易所',       '广期所',   '期货交易所', '中国大陆', 'http://www.gfex.com.cn',   9, '0', 'admin', sysdate(), '', null, '绿色与创新型期货市场');
insert into fin_market values (10,'HKEX', '香港交易及结算所有限公司', '港交所', '综合交易所', '中国香港', 'https://www.hkex.com.hk', 10, '0', 'admin', sysdate(), '', null, '香港资本市场核心交易所');

-- ----------------------------
-- 19、家庭资产表
-- ----------------------------
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
-- 20、家庭成员账户表
-- ----------------------------
drop table if exists fin_member_account;
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
-- 21、持仓账户表
-- ----------------------------
drop table if exists fin_holding_account;
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
-- 22、资产标的表
-- ----------------------------
drop table if exists fin_asset;
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
-- 23、交易流水表
-- ----------------------------
drop table if exists fin_transaction;
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
-- 24、行情快照表
-- ----------------------------
drop table if exists fin_quote_snapshot;
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
-- 25、资产日K线表
-- ----------------------------
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

-- ----------------------------
-- 26、持仓快照表
-- ----------------------------
drop table if exists fin_holding_snapshot;
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
-- 26、提醒规则表
-- ----------------------------
drop table if exists fin_alert_rule;
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
-- 27、提醒事件表
-- ----------------------------
drop table if exists fin_alert_event;
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

-- ----------------------------
-- 28、公告已读记录表
-- ----------------------------
drop table if exists sys_notice_read;
create table sys_notice_read (
  read_id          bigint(20)       not null auto_increment    comment '已读主键',
  notice_id        int(4)           not null                   comment '公告id',
  user_id          bigint(20)       not null                   comment '用户id',
  read_time        datetime         not null                   comment '阅读时间',
  primary key (read_id),
  unique key uk_user_notice (user_id, notice_id)   comment '同一用户同一公告只记录一次'
) engine=innodb auto_increment=1 comment='公告已读记录表';


-- ----------------------------
-- 29、代码生成业务表
-- ----------------------------
drop table if exists gen_table;
create table gen_table (
  table_id          bigint(20)      not null auto_increment    comment '编号',
  table_name        varchar(200)    default ''                 comment '表名称',
  table_comment     varchar(500)    default ''                 comment '表描述',
  sub_table_name    varchar(64)     default null               comment '关联子表的表名',
  sub_table_fk_name varchar(64)     default null               comment '子表关联的外键名',
  class_name        varchar(100)    default ''                 comment '实体类名称',
  tpl_category      varchar(200)    default 'crud'             comment '使用的模板（crud单表操作 tree树表操作）',
  tpl_web_type      varchar(30)     default ''                 comment '前端模板类型（element-ui模版 element-plus模版）',
  package_name      varchar(100)                               comment '生成包路径',
  module_name       varchar(30)                                comment '生成模块名',
  business_name     varchar(30)                                comment '生成业务名',
  function_name     varchar(50)                                comment '生成功能名',
  function_author   varchar(50)                                comment '生成功能作者',
  gen_type          char(1)         default '0'                comment '生成代码方式（0zip压缩包 1自定义路径）',
  gen_path          varchar(200)    default '/'                comment '生成路径（不填默认项目路径）',
  options           varchar(1000)                              comment '其它生成选项',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time 	    datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (table_id)
) engine=innodb auto_increment=1 comment = '代码生成业务表';


-- ----------------------------
-- 30、代码生成业务表字段
-- ----------------------------
drop table if exists gen_table_column;
create table gen_table_column (
  column_id         bigint(20)      not null auto_increment    comment '编号',
  table_id          bigint(20)                                 comment '归属表编号',
  column_name       varchar(200)                               comment '列名称',
  column_comment    varchar(500)                               comment '列描述',
  column_type       varchar(100)                               comment '列类型',
  java_type         varchar(500)                               comment 'JAVA类型',
  java_field        varchar(200)                               comment 'JAVA字段名',
  is_pk             char(1)                                    comment '是否主键（1是）',
  is_increment      char(1)                                    comment '是否自增（1是）',
  is_required       char(1)                                    comment '是否必填（1是）',
  is_insert         char(1)                                    comment '是否为插入字段（1是）',
  is_edit           char(1)                                    comment '是否编辑字段（1是）',
  is_list           char(1)                                    comment '是否列表字段（1是）',
  is_query          char(1)                                    comment '是否查询字段（1是）',
  query_type        varchar(200)    default 'EQ'               comment '查询方式（等于、不等于、大于、小于、范围）',
  html_type         varchar(200)                               comment '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  dict_type         varchar(200)    default ''                 comment '字典类型',
  sort              int                                        comment '排序',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time 	    datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  primary key (column_id)
) engine=innodb auto_increment=1 comment = '代码生成业务表字段';
