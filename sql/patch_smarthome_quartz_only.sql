SET NAMES utf8mb4;

delete from sys_job where job_id in (101, 102) or invoke_target in ('smarthomeTask.syncDeviceStatus', 'smarthomeTask.syncFullSnapshot');

insert into sys_job values(101, '米家设备状态刷新', 'DEFAULT', 'smarthomeTask.syncDeviceStatus', '0 0/10 * * * ?', '3', '1', '1', 'admin', sysdate(), '', null, '智能家居设备状态定时刷新');
insert into sys_job values(102, '米家全量快照同步', 'DEFAULT', 'smarthomeTask.syncFullSnapshot', '0 0 3 * * ?', '3', '1', '1', 'admin', sysdate(), '', null, '智能家居家庭/房间/设备全量同步');
