INSERT INTO `config` (`name`,`value`) VALUES ('PRIVILEGE_PROLONGATION_S_FILENAME_MASK','(\\d{8}|\\d{10})\\.s\\d{2}');
INSERT INTO `config` (`name`,`value`) VALUES ('PRIVILEGE_PROLONGATION_P_FILENAME_MASK','(\\d{8}|\\d{10})\\.p\\d{2}');

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (931, 1, UPPER('Директория входящих файлов продления льгот')), (931, 2, UPPER('Директория входящих файлов продления льгот'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (931, 900, 0, 931, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (931, 931, UPPER('string'));

INSERT INTO `update` (`version`) VALUE ('20160629_0.5.1');