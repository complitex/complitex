INSERT INTO `config` (`name`,`value`) VALUES ('DEBT_FILENAME_MASK','\\.DBF');

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES  (936, 1, UPPER('Директория входящих файлов задолженностей')),
    (936, 2, UPPER('Директорія вхідних файлів заборгованостей'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES
    (936, 900, 0, 936, 1, 1);

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (937, 1, UPPER('Директория исходящих файлов задолженностей')),
    (937, 2, UPPER('Директорія вихідних файлів заборгованостей'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES
    (937, 900, 0, 937, 1, 1);

CREATE TABLE `debt` (
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

  `COD` INTEGER(4),
  `CDPR` INTEGER(12),
  `NCARD` INTEGER(7),
  `IDPIL` VARCHAR(10),
  `PASPPIL` VARCHAR(14),
  `FIOPIL` VARCHAR(50),
  `INDEX` INTEGER(6),
  `CDUL` INTEGER(5),
  `HOUSE` VARCHAR(7),
  `BUILD` VARCHAR(2),
  `APT` VARCHAR(4),
  `KAT` INTEGER(4),
  `LGCODE` INTEGER(4),
  `DATEIN` VARCHAR(10),
  `DATEOUT` VARCHAR(10),
  `MONTHZV` INTEGER(2),
  `YEARZV` INTEGER(4),
  `RAH` VARCHAR(25),
  `MONEY` INTEGER(6),
  `EBK` INTEGER(10),
  `SUM_BORG` INTEGER(9),

  PRIMARY KEY (`id`),
  KEY `key_request_file_id` (`request_file_id`),
  KEY `key_account_number` (`account_number`),
  CONSTRAINT `fk_debt__request_file` FOREIGN KEY (`request_file_id`) REFERENCES `request_file` (`id`)
)ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Задолженность';

INSERT INTO `update` (`version`) VALUE ('20200916_0.8.0');
