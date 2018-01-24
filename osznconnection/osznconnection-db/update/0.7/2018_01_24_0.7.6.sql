INSERT INTO `sequence` (`sequence_name`, `sequence_value`) VALUES ('service',1), ('service_string_value', 1);
update sequence set sequence_value = (select IFNULL(max(`object_id`), 0) from `service`)+1 where sequence_name = 'service';
update sequence set sequence_value = (select IFNULL(max(`id`), 0) from `service_string_value`)+1 where sequence_name = 'service_string_value';

update entity_attribute set required = 0 where id = 1602;
update entity_attribute set required = 0 where id = 1603;

drop table building_code;
drop table subsidy_master_data_part;
drop table subsidy_master_data;

insert into `update` (`version`) VALUE ('20180124_0.7.6');