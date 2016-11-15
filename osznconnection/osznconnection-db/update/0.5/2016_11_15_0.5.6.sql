-- create facility local table

CREATE TABLE `facility_local` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор объекта местной льготы',
  `request_file_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор файла запросов',

  `account_number` VARCHAR(100) COMMENT 'Номер счета',
  `status` INTEGER NOT NULL DEFAULT 240 COMMENT 'Статус',

  `COD` INTEGER(4) COMMENT 'Код района',
  `CDPR` BIGINT(12) COMMENT 'Код ОКПО балансодержателя',
  `NCARD` BIGINT(10) COMMENT 'NULL',
  `IDCODE` VARCHAR(10) COMMENT 'ИНН ответственного по л/с',
  `PASP` VARCHAR(14) COMMENT 'Серия и номер паспорта ответственного',
  `FIO` VARCHAR(50) COMMENT 'ФИО ответственного',
  `IDPIL` VARCHAR(10) COMMENT 'ИНН носителя льготы',
  `PASPPIL` VARCHAR(14) COMMENT 'Серия и номер паспорта носителя льготы',
  `FIOPIL` VARCHAR(50) COMMENT 'ФИО носителя льготы',
  `INDEX` INTEGER(6) COMMENT 'NULL',
  `NAMUL` VARCHAR(20) COMMENT 'Название и тип улицы',
  `CDUL` INTEGER(5) COMMENT 'Код улицы в СЗ',
  `HOUSE` VARCHAR(7) COMMENT 'Номер дома',
  `BUILD` VARCHAR(2) COMMENT 'Часть дома',
  `APT` VARCHAR(4) COMMENT 'Номер квартиры',
  `LGCODE` INTEGER(4) COMMENT '500 (для ЖКС) или 5061 (для мусора)',
  `KAT` INTEGER(4) COMMENT 'Код льготной категории',
  `YEARIN` INTEGER(4) COMMENT 'Год формирования',
  `MONTHIN` INTEGER(2) COMMENT 'Месяц формирования',
  `YEAR` INTEGER(4) COMMENT 'Расчетный год',
  `MONTH` INTEGER(2) COMMENT 'Расчетный месяц',
  `RAH` BIGINT(10) COMMENT 'Номер л/с',
  `RIZN` INTEGER(6) COMMENT 'NULL',
  `TARIF` BIGINT(10) COMMENT 'NULL',
  `VL` INTEGER(3) COMMENT 'NULL',
  `PLZAG` DECIMAL(6,2) COMMENT 'Общая площадь (ЖКС) или NULL (мусор)',
  `PLPIL` DECIMAL(6,2) COMMENT 'Льготная площадь (ЖКС) или NULL (мусор)',
  `TARIFS` DECIMAL(10,2) COMMENT '0.37 (при наличии услуг 050 или 160 в ЖКС) 0.28 (в остальных случаях в ЖКС) NULL (в мусоре)',
  `TARIFN` DECIMAL(10,3) COMMENT 'Тариф',
  `SUM` DECIMAL(10,2) COMMENT 'Возмещение текущего месяца',
  `LGKOL` INTEGER(2) COMMENT 'Количество пользующихся льготой',
  `SUMPER` DECIMAL(10,2) COMMENT 'Сумма перерасчетов возмещения',
  `DATN` DATE COMMENT 'Начало расчетного периода (текущего)',
  `DATK` DATE COMMENT 'Окончание расчетного периода (текущего)',
  `PSN` INTEGER(2) COMMENT 'Причина снятия льготы',
  `SUMM` DECIMAL(10,2) COMMENT 'Итоговая сумма (текущее возмещение и перерасчеты)',

  PRIMARY KEY (`id`),
  KEY `key_request_file_id` (`request_file_id`),
  KEY `key_account_number` (`account_number`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_facility_local__request_file` FOREIGN KEY (`request_file_id`) REFERENCES `request_file` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Записи местной льготы';

-- facility local file description

INSERT INTO `request_file_description` VALUES (15,'FACILITY_LOCAL','dd.MM.yyyy');

INSERT INTO `request_file_field_description` VALUES (387,15,'COD','java.lang.Integer',4,NULL);
INSERT INTO `request_file_field_description` VALUES (388,15,'CDPR','java.lang.Long',12,NULL);
INSERT INTO `request_file_field_description` VALUES (389,15,'NCARD','java.lang.Long',10,NULL);
INSERT INTO `request_file_field_description` VALUES (390,15,'IDCODE','java.lang.String',10,NULL);
INSERT INTO `request_file_field_description` VALUES (391,15,'PASP','java.lang.String',14,NULL);
INSERT INTO `request_file_field_description` VALUES (392,15,'FIO','java.lang.String',50,NULL);
INSERT INTO `request_file_field_description` VALUES (393,15,'IDPIL','java.lang.String',10,NULL);
INSERT INTO `request_file_field_description` VALUES (394,15,'PASPPIL','java.lang.String',14,NULL);
INSERT INTO `request_file_field_description` VALUES (395,15,'FIOPIL','java.lang.String',50,NULL);
INSERT INTO `request_file_field_description` VALUES (396,15,'INDEX','java.lang.Integer',6,NULL);
INSERT INTO `request_file_field_description` VALUES (397,15,'NAMUL','java.lang.String',20,NULL);
INSERT INTO `request_file_field_description` VALUES (398,15,'CDUL','java.lang.Integer',5,NULL);
INSERT INTO `request_file_field_description` VALUES (399,15,'HOUSE','java.lang.String',7,NULL);
INSERT INTO `request_file_field_description` VALUES (400,15,'BUILD','java.lang.String',2,NULL);
INSERT INTO `request_file_field_description` VALUES (401,15,'APT','java.lang.String',4,NULL);
INSERT INTO `request_file_field_description` VALUES (402,15,'LGCODE','java.lang.Integer',4,NULL);
INSERT INTO `request_file_field_description` VALUES (403,15,'KAT','java.lang.Integer',4,NULL);
INSERT INTO `request_file_field_description` VALUES (404,15,'YEARIN','java.lang.Integer',4,NULL);
INSERT INTO `request_file_field_description` VALUES (405,15,'MONTHIN','java.lang.Integer',2,NULL);
INSERT INTO `request_file_field_description` VALUES (406,15,'YEAR','java.lang.Integer',4,NULL);
INSERT INTO `request_file_field_description` VALUES (407,15,'MONTH','java.lang.Integer',2,NULL);
INSERT INTO `request_file_field_description` VALUES (408,15,'RAH','java.lang.Long',10,NULL);
INSERT INTO `request_file_field_description` VALUES (409,15,'RIZN','java.lang.Integer',6,NULL);
INSERT INTO `request_file_field_description` VALUES (410,15,'TARIF','java.lang.Long',10,NULL);
INSERT INTO `request_file_field_description` VALUES (411,15,'VL','java.lang.Integer',3,NULL);
INSERT INTO `request_file_field_description` VALUES (412,15,'PLZAG','java.math.BigDecimal',6,2);
INSERT INTO `request_file_field_description` VALUES (413,15,'PLPIL','java.math.BigDecimal',6,2);
INSERT INTO `request_file_field_description` VALUES (414,15,'TARIFS','java.math.BigDecimal',10,2);
INSERT INTO `request_file_field_description` VALUES (415,15,'TARIFN','java.math.BigDecimal',10,3);
INSERT INTO `request_file_field_description` VALUES (416,15,'SUM','java.math.BigDecimal',10,2);
INSERT INTO `request_file_field_description` VALUES (417,15,'LGKOL','java.lang.Integer',2,NULL);
INSERT INTO `request_file_field_description` VALUES (418,15,'SUMPER','java.math.BigDecimal',10,2);
INSERT INTO `request_file_field_description` VALUES (419,15,'DATN','java.util.Date',8,NULL);
INSERT INTO `request_file_field_description` VALUES (420,15,'DATK','java.util.Date',8,NULL);
INSERT INTO `request_file_field_description` VALUES (421,15,'PSN','java.lang.Integer',2,NULL);
INSERT INTO `request_file_field_description` VALUES (422,15,'SUMM','java.math.BigDecimal',10,2);

INSERT INTO `update` (`version`) VALUE ('20161115_0.5.6');