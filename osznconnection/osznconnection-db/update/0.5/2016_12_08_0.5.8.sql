alter table `request_file` add column `sub_type` INTEGER COMMENT 'Подтип файла';
alter table `request_file` add index `key_sub_type` (`sub_type`);

INSERT INTO `update` (`version`) VALUE ('20161208_0.5.8');