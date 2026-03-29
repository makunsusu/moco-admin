SET NAMES utf8mb4;

delete from sys_role_menu where menu_id in (2100,2101,2103,2104,2110,2111,2112,2113,2116,2117,2118,2102,2114,2115);
delete from sys_menu where menu_id in (2100,2101,2103,2104,2110,2111,2112,2113,2116,2117,2118,2102,2114,2115);

insert into sys_menu values('2100', '智能家居', '0',   '6', 'smarthome',  null,                       '', '', 1, 0, 'M', '0', '0', '',                        'dashboard',     'admin', sysdate(), '', null, '智能家居目录');
insert into sys_menu values('2101', '平台接入', '2100', '1', 'platform',   'smarthome/platform/index', '', '', 1, 0, 'C', '0', '0', 'smarthome:platform:query', 'edit',          'admin', sysdate(), '', null, '平台接入菜单');
insert into sys_menu values('2103', '设备工作台', '2100', '2', 'device',   'smarthome/device/index',   '', '', 1, 0, 'C', '0', '0', 'smarthome:device:list',   'list',          'admin', sysdate(), '', null, '设备工作台菜单');
insert into sys_menu values('2104', '同步日志', '2100', '3', 'log',        'smarthome/log/index',      '', '', 1, 0, 'C', '0', '0', 'smarthome:log:list',      'job',           'admin', sysdate(), '', null, '同步日志菜单');
insert into sys_menu values('2110', '平台查询', '2101', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'smarthome:platform:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2111', '平台修改', '2101', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'smarthome:platform:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2112', '测试连接', '2101', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'smarthome:platform:sync',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2113', '手动同步', '2101', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'smarthome:platform:sync',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2116', '设备列表', '2103', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'smarthome:device:list',     '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2117', '设备详情', '2103', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'smarthome:device:query',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2118', '日志查询', '2104', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'smarthome:log:list',        '#', 'admin', sysdate(), '', null, '');

insert into sys_role_menu values ('2', '2100');
insert into sys_role_menu values ('2', '2101');
insert into sys_role_menu values ('2', '2103');
insert into sys_role_menu values ('2', '2104');
insert into sys_role_menu values ('2', '2110');
insert into sys_role_menu values ('2', '2111');
insert into sys_role_menu values ('2', '2112');
insert into sys_role_menu values ('2', '2113');
insert into sys_role_menu values ('2', '2116');
insert into sys_role_menu values ('2', '2117');
insert into sys_role_menu values ('2', '2118');
