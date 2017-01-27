alter table `request_file_group` add column `group_type` INTEGER COMMENT 'Тип группы';
update `request_file_group` set group_type = 1 where group_type is null;

INSERT INTO `update` (`version`) VALUE ('20170127_0.5.10');