-- 理财大师模块一键初始化脚本
-- 执行顺序：
-- 1. 菜单与权限
-- 2. 理财业务表与演示数据
-- 3. 行情定时任务
--
-- 使用方式（MySQL 客户端）：
-- source /Users/makun/llm_project/moco-admin/sql/patch_finance_all_in_one.sql;
--
-- 注意：
-- 1. patch_finance_tables_only.sql 会重建理财业务表，请勿在已有正式理财数据的库上直接执行
-- 2. patch_finance_quartz_only.sql 默认把行情任务创建为“暂停”状态

set names utf8mb4;

source /Users/makun/llm_project/moco-admin/sql/patch_finance_menu_only.sql;
source /Users/makun/llm_project/moco-admin/sql/patch_finance_tables_only.sql;
source /Users/makun/llm_project/moco-admin/sql/patch_finance_quartz_only.sql;
