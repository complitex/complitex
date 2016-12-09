-- add request file subtype

alter table `request_file` add column `sub_type` INTEGER COMMENT 'Подтип файла';
alter table `request_file` add index `key_sub_type` (`sub_type`);

-- update privilege prolongation subtypes
update `request_file` set sub_type = 1401 where type = 14 and name like ('%S%');
update `request_file` set sub_type = 1402 where type = 14 and name like ('%P%');

INSERT INTO `update` (`version`) VALUE ('20161208_0.5.8');