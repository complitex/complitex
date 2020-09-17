INSERT INTO `config` (`name`,`value`) VALUES ('DEBT_FILENAME_MASK','\\d{8}\\.m\\d{2}');

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES  (936, 1, UPPER('Директория входящих файлов задолженностей')),
    (936, 2, UPPER('Директорія вхідних файлів заборгованостей'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES
    (936, 900, 0, 936, 1, 1);

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (937, 1, UPPER('Директория исходящих файлов задолженностей')),
    (937, 2, UPPER('Директорія вихідних файлів заборгованостей'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES
    (937, 900, 0, 937, 1, 1);

INSERT INTO `request_file_description`(`request_file_type`,`date_pattern`) VALUES ('DEBT','dd.MM.yyyy');
SET @request_file_description_id = LAST_INSERT_ID();
INSERT INTO `request_file_field_description`(`request_file_description_id`, `name`, `type`, `length`)
VALUES (@request_file_description_id, 'COD', 'java.lang.Integer', 4),
       (@request_file_description_id, 'CDPR', 'java.lang.Long', 12),
       (@request_file_description_id, 'NCARD', 'java.lang.Long', 10),
       (@request_file_description_id, 'IDPIL', 'java.lang.String', 10),
       (@request_file_description_id, 'PASPPIL', 'java.lang.String', 14),
       (@request_file_description_id, 'FIOPIL', 'java.lang.String', 50),
       (@request_file_description_id, 'INDEX', 'java.lang.Integer', 6),
       (@request_file_description_id, 'CDUL', 'java.lang.Integer', 5),
       (@request_file_description_id, 'HOUSE', 'java.lang.String', 7),
       (@request_file_description_id, 'BUILD', 'java.lang.String', 2),
       (@request_file_description_id, 'APT', 'java.lang.String', 4),
       (@request_file_description_id, 'KAT', 'java.lang.Integer', 4),
       (@request_file_description_id, 'LGCODE', 'java.lang.Integer', 4),
       (@request_file_description_id, 'DATEIN', 'java.lang.String', 10),
       (@request_file_description_id, 'DATEOUT', 'java.lang.String', 10),
       (@request_file_description_id, 'MONTHZV', 'java.lang.Integer', 4),
       (@request_file_description_id, 'YEARZV', 'java.lang.Integer', 4),
       (@request_file_description_id, 'RAH', 'java.lang.String', 25),
       (@request_file_description_id, 'MONEY', 'java.lang.Integer', 6),
       (@request_file_description_id, 'EBK', 'java.lang.String', 10);
INSERT INTO `request_file_field_description`(`request_file_description_id`, `name`, `type`, `length`, `scale`) VALUE
    (@request_file_description_id, 'SUM_BORG', 'java.math.BigDecimal', 10, 2);

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
  `EBK` VARCHAR(10),
  `SUM_BORG` DECIMAL(10, 2),

  PRIMARY KEY (`id`),
  KEY `key_request_file_id` (`request_file_id`),
  KEY `key_account_number` (`account_number`),
  CONSTRAINT `fk_debt__request_file` FOREIGN KEY (`request_file_id`) REFERENCES `request_file` (`id`)
)ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Задолженность';

INSERT INTO `update` (`version`) VALUE ('20200916_0.8.0');
