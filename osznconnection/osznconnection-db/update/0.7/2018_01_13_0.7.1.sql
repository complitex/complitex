delimiter //

create procedure copy_building_attribute(b_object_id bigint, b_entity_attribute_id bigint,
                                       a_object_id bigint, a_entity_attribute_id bigint)
  begin
    declare baa_attribute_id bigint;
    declare baa_value_id bigint;
    declare baa_start_date timestamp;
    declare baa_end_date timestamp;
    declare baa_status int;

    declare next_value_id bigint;
    declare a_value_1 varchar(1000);
    declare a_value_2 varchar(1000);

    declare done int default false;
    declare baa_cursor cursor for select attribute_id, value_id, start_date, end_date, status
      from building_address_attribute where object_id = a_object_id and entity_attribute_id = a_entity_attribute_id;
    declare continue handler for not found set done = true;

    open baa_cursor;

    baa_loop: loop
      fetch baa_cursor into baa_attribute_id, baa_value_id, baa_start_date, baa_end_date, baa_status;

      if done then
        leave baa_loop;
      end if;

      update `sequence` s set s.`sequence_value` = s.`sequence_value` + 1 where s.`sequence_name` = 'building_string_value';
      select s.`sequence_value` from `sequence` s where s.`sequence_name` = 'building_string_value' into next_value_id;

      select `value` from building_address_string_value where id = baa_value_id and locale_id = 1 into a_value_1;
      if (a_value_1 is not null) then
        insert into building_string_value(id, locale_id, value) value (next_value_id, 1, a_value_1);
      end if;

      select `value` from building_address_string_value where id = baa_value_id and locale_id = 2 into a_value_2;
      if (a_value_2 is not null) then
        insert into building_string_value(id, locale_id, value) value (next_value_id, 2, a_value_2);
      end if;

      insert into building_attribute(attribute_id, object_id, entity_attribute_id, value_id, start_date, end_date, status)
        value (baa_attribute_id, b_object_id, b_entity_attribute_id, next_value_id, baa_start_date, baa_end_date, baa_status);

    end loop;

    close baa_cursor;
  end//

create procedure ref_building()
  begin
    declare b_object_id bigint;
    declare b_parent_id bigint;

    declare a_parent_id bigint;
    declare a_parent_entity_id bigint;

    declare done int default false;
    declare b_cursor cursor for select object_id, parent_id from building where parent_entity_id = 1500;
    declare continue handler for not found set done = true;

    set foreign_key_checks=0;

    open b_cursor;

    b_loop: loop
      fetch b_cursor into b_object_id, b_parent_id;

      if done then
        leave b_loop;
      end if;

      update building_attribute set entity_attribute_id = 503 where entity_attribute_id = 500 and object_id = b_object_id;
      update building_attribute set entity_attribute_id = 504 where entity_attribute_id = 502 and object_id = b_object_id;
      update building_attribute set entity_attribute_id = 505 where entity_attribute_id = 501 and object_id = b_object_id;

      call copy_building_attribute(b_object_id, 500, b_parent_id, 1500);
      call copy_building_attribute(b_object_id, 501, b_parent_id, 1501);
      call copy_building_attribute(b_object_id, 502, b_parent_id, 1502);

      select parent_id, parent_entity_id from building_address where object_id = b_parent_id into a_parent_id, a_parent_entity_id;
      update building set parent_id = a_parent_id, parent_entity_id = a_parent_entity_id;

    end loop;

    close b_cursor;

  end//

delimiter ;

call ref_building();

drop procedure copy_building_attribute;
drop procedure ref_building;

set foreign_key_checks=0;

delete from entity_string_value where id in (501, 502, 503);
delete from entity_attribute where entity_id = 500;

delete from entity_attribute where entity_id = 1500;
delete from entity_string_value where id in (1500, 1501, 1502, 1503);
delete from entity where id = 1500;

drop table building_address;
drop table building_address_attribute;
drop table building_address_string_value;

insert into `entity_string_value`(`id`, `locale_id`, `value`) value (501, 1, upper('Номер дома')), (501, 2, upper('Номер будинку'));
insert into `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) value (500, 500, 1, 501, 1, 0);

insert into `entity_string_value`(`id`, `locale_id`, `value`) value (502, 1, upper('Корпус')), (502, 2, upper('Корпус'));
insert into `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) value (501, 500, 0, 502, 1, 0);

insert into `entity_string_value`(`id`, `locale_id`, `value`) value (503, 1, upper('Строение')), (503, 2, upper('Будова'));
insert into `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) value (502, 500, 0, 503, 1, 0);


insert into `entity_string_value`(`id`, `locale_id`, `value`) value (504, 1, upper('Район')), (504, 2, upper('Район'));
insert into `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`, `reference_id`) value (503, 500, 0, 504, 1, 10, 600);

insert into `entity_string_value`(`id`, `locale_id`, `value`) value (505, 1, upper('Список кодов дома')), (505, 2, upper('Список кодов дома'));
insert into `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) value (504, 500, 0, 505, 1, 20);

insert into `update` (`version`) VALUE ('20180113_0.7.1');