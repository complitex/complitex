CREATE TABLE `privilege_prolongation` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `request_file_id` BIGINT(20) NULL COMMENT 'Идентификатор файла запросов',
  `account_number` VARCHAR(100) NULL COMMENT 'Номер счета',
  `status` INTEGER NOT NULL DEFAULT 240 COMMENT 'Код статуса',

  `internal_city_id` BIGINT(20) COMMENT 'Идентификатор населенного пункта',
  `internal_street_id` BIGINT(20) COMMENT 'Идентификатор улицы',
  `internal_street_type_id` BIGINT(20) COMMENT 'Идентификатор типа улицы',
  `internal_building_id` BIGINT(20) COMMENT 'Идентификатор дома',

  `outgoing_city` VARCHAR(100) COMMENT 'Название населенного пункта используемое центром начисления',
  `outgoing_district` VARCHAR(100) COMMENT 'Название района используемое центром начисления',
  `outgoing_street` VARCHAR(100) COMMENT 'Название улицы используемое центром начисления',
  `outgoing_street_type` VARCHAR(100) COMMENT 'Название типа улицы используемое центром начисления',
  `outgoing_building_number` VARCHAR(100) COMMENT 'Номер дома используемый центром начисления',
  `outgoing_building_corp` VARCHAR(100) COMMENT 'Корпус используемый центром начисления',
  `outgoing_apartment` VARCHAR(100) COMMENT 'Номер квартиры. Не используется',

  `date` DATE NOT NULL COMMENT 'Дата',

  `first_name` VARCHAR(100) COMMENT 'Имя',
  `last_name` VARCHAR(100) COMMENT 'Фамилия',
  `middle_name` VARCHAR(100) COMMENT 'Отчество',
  `city` VARCHAR(100) NOT NULL COMMENT 'Населенный пункт',

  `COD` INTEGER(4) COMMENT 'Код ОСЗН',
  `CDPR` BIGINT(12) COMMENT 'ЕДРПОУ код предприятия',
  `NCARD` BIGINT(10) COMMENT 'Номер дела в ОСЗН',
  `IDPIL` VARCHAR(10) COMMENT 'Иден.код льготника',
  `PASPPIL` VARCHAR(14) COMMENT 'Паспорт льготника',
  `FIOPIL` VARCHAR(50) COMMENT 'ФИО льготника',
  `INDEX` INTEGER(6) COMMENT 'Почтовый индекс',
  `CDUL` INTEGER(5) COMMENT 'Код улицы',
  `HOUSE` VARCHAR(7) COMMENT 'Номер дома',
  `BUILD` VARCHAR(2) COMMENT 'Номер корпуса',
  `APT` VARCHAR(4) COMMENT 'Номер квартиры',
  `KAT` INTEGER(4) COMMENT 'Код льготы ЕДАРП',
  `LGCODE` INTEGER(4) COMMENT 'Код услуги',
  `DATEIN` VARCHAR(10) COMMENT 'Дата начала действия льготы',
  `DATEOUT` VARCHAR(10) COMMENT 'Дата окончания действия льготы',
  `RAH` VARCHAR(25) COMMENT 'Номер л/с ПУ',

  PRIMARY KEY (`id`),
  KEY `key_request_file_id` (`request_file_id`),
  KEY `key_account_number` (`account_number`),
  CONSTRAINT `fk_privilege_prolongation__request_file` FOREIGN KEY (`request_file_id`) REFERENCES `request_file` (`id`)
)ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Файлы продления льгот';


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
INSERT INTO `request_file_field_description` VALUES (384,14,'DATEIN','java.lang.String',10,NULL);
INSERT INTO `request_file_field_description` VALUES (385,14,'DATEOUT','java.lang.String',10,NULL);
INSERT INTO `request_file_field_description` VALUES (386,14,'RAH','java.lang.String',25,NULL);

INSERT INTO `update` (`version`) VALUE ('20160629_0.5.1');