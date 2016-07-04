INSERT INTO `config` (`name`,`value`) VALUES ('PRIVILEGE_PROLONGATION_S_FILENAME_MASK','(\\d{8}|\\d{10})\\.s\\d{2}');
INSERT INTO `config` (`name`,`value`) VALUES ('PRIVILEGE_PROLONGATION_P_FILENAME_MASK','(\\d{8}|\\d{10})\\.p\\d{2}');

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (931, 1, UPPER('Директория входящих файлов продления льгот')), (931, 2, UPPER('Директория входящих файлов продления льгот'));
INSERT INTO `attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (931, 900, 0, 931, 1);
INSERT INTO `attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (931, 931, UPPER('string'));

INSERT INTO `request_file_description` VALUES (14,'PRIVILEGE_PROLONGATION','dd.MM.yyyy');

INSERT INTO `request_file_field_description` VALUES (371,14,'COD','java.lang.Integer',4,NULL);
INSERT INTO `request_file_field_description` VALUES (372,14,'CDPR','java.lang.Long',12,NULL);
INSERT INTO `request_file_field_description` VALUES (373,14,'NCARD','java.lang.Long',10,NULL);
INSERT INTO `request_file_field_description` VALUES (374,14,'IDPIL','java.lang.String',10,NULL);
INSERT INTO `request_file_field_description` VALUES (375,14,'PASPPIL','java.lang.String',14,NULL);
INSERT INTO `request_file_field_description` VALUES (376,14,'FIOPIL','java.lang.String',50,NULL);
INSERT INTO `request_file_field_description` VALUES (377,14,'INDEX','java.lang.Integer',6,NULL);
INSERT INTO `request_file_field_description` VALUES (378,14,'CDUL','java.lang.Integer',5,NULL);
INSERT INTO `request_file_field_description` VALUES (379,14,'HOUSE','java.lang.String',7,NULL);
INSERT INTO `request_file_field_description` VALUES (380,14,'BUILD','java.lang.String',2,NULL);
INSERT INTO `request_file_field_description` VALUES (381,14,'APT','java.lang.String',4,NULL);
INSERT INTO `request_file_field_description` VALUES (382,14,'KAT','java.lang.Integer',4,NULL);
INSERT INTO `request_file_field_description` VALUES (383,14,'LGCODE','java.lang.Integer',4,NULL);
INSERT INTO `request_file_field_description` VALUES (384,14,'DATEIN','java.util.Date',10,NULL);
INSERT INTO `request_file_field_description` VALUES (385,14,'DATEOUT','java.util.Date',10,NULL);
INSERT INTO `request_file_field_description` VALUES (386,14,'RAH','java.lang.String',25,NULL);

INSERT INTO `update` (`version`) VALUE ('20160629_0.5.1');