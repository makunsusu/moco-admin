SET NAMES utf8mb4;

set @drop_old_room_index = (
  select if (
    exists (
      select 1
      from information_schema.statistics
      where table_schema = database()
        and table_name = 'sh_room'
        and index_name = 'uk_sh_room_cloud_id'
    ),
    'alter table sh_room drop index uk_sh_room_cloud_id',
    'select 1'
  )
);
prepare stmt_drop_old_room_index from @drop_old_room_index;
execute stmt_drop_old_room_index;
deallocate prepare stmt_drop_old_room_index;

set @add_new_room_index = (
  select if (
    exists (
      select 1
      from information_schema.statistics
      where table_schema = database()
        and table_name = 'sh_room'
        and index_name = 'uk_sh_room_home_cloud_id'
    ),
    'select 1',
    'alter table sh_room add unique key uk_sh_room_home_cloud_id (home_id, cloud_room_id)'
  )
);
prepare stmt_add_new_room_index from @add_new_room_index;
execute stmt_add_new_room_index;
deallocate prepare stmt_add_new_room_index;
