-- 理财大师定时任务补丁
-- 适用场景：数据库已初始化，只想补充理财行情刷新任务
set names utf8mb4;

-- 说明：默认创建为“暂停”状态，确认外部行情可访问后再在任务中心启用

-- ----------------------------
-- 1、清理旧任务
-- ----------------------------
delete from sys_job where job_id = 100 or (job_name = '理财行情自动刷新' and job_group = 'DEFAULT');

-- 如果存在历史日志，可以按需清理
delete from sys_job_log where job_name = '理财行情自动刷新' and job_group = 'DEFAULT';

-- ----------------------------
-- 2、新增行情刷新任务
-- ----------------------------
insert into sys_job values(
  100,
  '理财行情自动刷新',
  'DEFAULT',
  'finQuoteTask.refreshLatestQuotes',
  '0 0/30 9-15 ? * MON-FRI',
  '3',
  '1',
  '1',
  'admin',
  sysdate(),
  '',
  null,
  '每个交易日 9:00-15:59 每 30 分钟刷新一次基金与股票行情，默认暂停，确认行情源可用后启用'
);
