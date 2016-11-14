-- update long description
update `request_file_field_description` set type = 'java.lang.Long' where type = 'java.lang.Integer' and length >= 10;

-- update facility form 2 table
DROP TABLE IF EXISTS `facility_form2`;

CREATE TABLE `facility_form2` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор объекта форма-2 льгота',
  `request_file_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор файла запросов',

  `account_number` VARCHAR(100) COMMENT 'Номер счета',
  `status` INTEGER NOT NULL DEFAULT 240 COMMENT 'Статус',

  `first_name` VARCHAR(100) COMMENT 'Имя',
  `last_name` VARCHAR(100) COMMENT 'Фамилия',
  `middle_name` VARCHAR(100) COMMENT 'Отчество',

  `CDPR` BIGINT(12) COMMENT 'Код ЄДРПОУ (ОГРН) организации',
  `IDCODE` VARCHAR(10) COMMENT 'ИНН льготника',
  `FIO` VARCHAR(50) COMMENT 'ФИО льготника',
  `PPOS` VARCHAR(15) COMMENT '0',
  `RS` VARCHAR(25) COMMENT 'Номер л/с ПУ',
  `YEARIN` INTEGER(4) COMMENT 'Год выгрузки данных',
  `MONTHIN` INTEGER(2) COMMENT 'Месяц выгрузки данных',
  `LGCODE` INTEGER(4) COMMENT 'Код льготы',
  `DATA1` DATE COMMENT 'Дата начала периода',
  `DATA2` DATE COMMENT 'Дата окончания периода',
  `LGKOL` INTEGER(2) COMMENT 'Кол-во пользующихся льготой',
  `LGKAT` VARCHAR(3) COMMENT 'Категория льготы ЕДАРП',
  `LGPRC` INTEGER(3) COMMENT 'Процент льготы',
  `SUMM` DECIMAL(8,2) COMMENT 'Сумма возмещения',
  `FACT` DECIMAL(19,6) COMMENT 'Объем фактического потребления (для услуг со счетчиком)',
  `TARIF` DECIMAL(14,7) COMMENT 'Ставка тарифа',
  `FLAG` INTEGER(1) COMMENT '',

  `DEPART` INTEGER COMMENT 'Код участка',

  PRIMARY KEY (`id`),
  KEY `key_request_file_id` (`request_file_id`),
  KEY `key_account_number` (`account_number`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_facility_form2__request_file` FOREIGN KEY (`request_file_id`) REFERENCES `request_file` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Файлы форма-2 льгота';

INSERT INTO `update` (`version`) VALUE ('20161114_0.5.5');
