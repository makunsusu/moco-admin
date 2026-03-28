-- 理财大师菜单补丁
-- 适用场景：数据库已初始化，只想补充理财模块菜单与管理员角色授权
set names utf8mb4;

-- 执行前建议确认使用的库为当前项目业务库

-- ----------------------------
-- 1、清理旧的理财菜单按钮授权
-- ----------------------------
delete from sys_role_menu
where menu_id in (
  2000, 2001, 2002, 2003, 2004, 2005, 2006,
  2010, 2011, 2012, 2013, 2014, 2015,
  2020, 2021, 2022, 2023, 2024, 2025,
  2030, 2031, 2032, 2033, 2034, 2035, 2036, 2037,
  2038, 2039, 2040, 2041, 2042, 2043, 2044, 2045
);

delete from sys_menu
where menu_id in (
  2000, 2001, 2002, 2003, 2004, 2005, 2006,
  2010, 2011, 2012, 2013, 2014, 2015,
  2020, 2021, 2022, 2023, 2024, 2025,
  2030, 2031, 2032, 2033, 2034, 2035, 2036, 2037,
  2038, 2039, 2040, 2041, 2042, 2043, 2044, 2045
);

-- ----------------------------
-- 2、理财大师目录与页面菜单
-- ----------------------------
insert into sys_menu values('2000', '理财大师', '0',   '5', 'finance',      null,                         '', '', 1, 0, 'M', '0', '0', '',                           'money',     'admin', sysdate(), '', null, '理财大师目录');
insert into sys_menu values('2001', '家庭总览', '2000', '1', 'overview',    'finance/overview/index',    '', '', 1, 0, 'C', '0', '0', 'finance:family:list',       'dashboard', 'admin', sysdate(), '', null, '家庭总览菜单');
insert into sys_menu values('2002', '家庭账户', '2000', '2', 'account',     'finance/account/index',     '', '', 1, 0, 'C', '0', '0', 'finance:account:list',      'peoples',   'admin', sysdate(), '', null, '家庭账户菜单');
insert into sys_menu values('2003', '资产与行情', '2000', '3', 'asset',      'finance/asset/index',       '', '', 1, 0, 'C', '0', '0', 'finance:asset:list',        'chart',     'admin', sysdate(), '', null, '资产与行情菜单');
insert into sys_menu values('2004', '交易流水', '2000', '4', 'transaction', 'finance/transaction/index', '', '', 1, 0, 'C', '0', '0', 'finance:transaction:list',  'date',      'admin', sysdate(), '', null, '交易流水菜单');
insert into sys_menu values('2005', '提醒中心', '2000', '5', 'alert',       'finance/alert/index',       '', '', 1, 0, 'C', '0', '0', 'finance:alert:list',        'bell',      'admin', sysdate(), '', null, '提醒中心菜单');
insert into sys_menu values('2006', '交易市场', '2000', '6', 'market',      'finance/market/index',      '', '', 1, 0, 'C', '0', '0', 'finance:market:list',       'money',     'admin', sysdate(), '', null, '交易市场菜单');

-- ----------------------------
-- 3、家庭总览与家庭成员按钮
-- ----------------------------
insert into sys_menu values('2010', '家庭查询', '2001', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:family:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2011', '家庭新增', '2001', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:family:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2012', '家庭修改', '2001', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:family:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2013', '家庭删除', '2001', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:family:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2014', '成员维护', '2001', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:family:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2015', '成员查询', '2001', '6', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:family:query',  '#', 'admin', sysdate(), '', null, '');

-- ----------------------------
-- 4、持仓账户按钮
-- ----------------------------
insert into sys_menu values('2020', '账户查询', '2002', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:account:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2021', '账户新增', '2002', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:account:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2022', '账户修改', '2002', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:account:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2023', '账户删除', '2002', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:account:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2024', '资产查询', '2003', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:asset:query',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2025', '行情刷新', '2003', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:quote:refresh',  '#', 'admin', sysdate(), '', null, '');

-- ----------------------------
-- 5、资产、交易、提醒、市场按钮
-- ----------------------------
insert into sys_menu values('2030', '资产新增', '2003', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:asset:add',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2031', '资产修改', '2003', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:asset:edit',        '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2032', '资产删除', '2003', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:asset:remove',      '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2033', '交易维护', '2004', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:transaction:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2034', '交易新增', '2004', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:transaction:add',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2035', '交易修改', '2004', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:transaction:edit',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2036', '交易删除', '2004', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:transaction:remove','#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2037', '提醒维护', '2005', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:alert:query',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu values('2038', '提醒新增', '2005', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:alert:add',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2039', '提醒修改', '2005', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:alert:edit',        '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2040', '提醒删除', '2005', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:alert:remove',      '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2041', '交易市场查询', '2006', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:market:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2042', '交易市场新增', '2006', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:market:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2043', '交易市场修改', '2006', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:market:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2044', '交易市场删除', '2006', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:market:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2045', '交易市场导出', '2006', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'finance:market:export', '#', 'admin', sysdate(), '', null, '');

-- ----------------------------
-- 6、管理员角色授权
-- ----------------------------
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
insert into sys_role_menu values ('2', '2038');
insert into sys_role_menu values ('2', '2039');
insert into sys_role_menu values ('2', '2040');
insert into sys_role_menu values ('2', '2041');
insert into sys_role_menu values ('2', '2042');
insert into sys_role_menu values ('2', '2043');
insert into sys_role_menu values ('2', '2044');
insert into sys_role_menu values ('2', '2045');
