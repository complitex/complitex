-- ------------------------------
-- Oschadbank request file
-- ------------------------------
DROP TABLE IF EXISTS `oschadbank_request_file`;
CREATE TABLE `oschadbank_request_file` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор файла запросов от ощадбанка',
  `request_file_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор файла запросов',

  `EDRPOU` VARCHAR(16) COMMENT 'ЕДРПОУ',
  `PROVIDER_NAME` VARCHAR(128) COMMENT 'Название поставщика',
  `DOCUMENT_NUMBER` VARCHAR(64) COMMENT '№ Анкеты',
  `SERVICE_NAME` VARCHAR(1024) COMMENT 'Название услуги',
  `REPORTING_PERIOD` VARCHAR(16) COMMENT 'Отчетный период',
  `PROVIDER_CODE` VARCHAR(16) COMMENT 'Код Банка Поставщика услуги',
  `PROVIDER_ACCOUNT` VARCHAR(32) COMMENT 'р/с Поставщика услуги',
  `PROVIDER_IBAN` VARCHAR(64) COMMENT 'IBAN Поставщика',

  PRIMARY KEY (`id`),
  KEY `key_request_file_id` (`request_file_id`),
  CONSTRAINT `fk_oschadbank_request_file__request_file` FOREIGN KEY (`request_file_id`) REFERENCES `request_file` (`id`)
) ENGINE = InnoDB CHARSET = utf8 COLLATE = utf8_unicode_ci COMMENT 'Файл запроса от ощадбанка';

-- ------------------------------
-- Oschadbank request
-- ------------------------------
DROP TABLE IF EXISTS `oschadbank_request`;
CREATE TABLE `oschadbank_request` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор запросов от ощадбанка',
  `request_file_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор файла запросов',

  `UTSZN` VARCHAR(64) COMMENT 'Номер УТСЗН',
  `ACCOUNT_NUMBER` VARCHAR(64) COMMENT 'Номер учетной записи получателя жилищной субсидии в АО «Ощадбанк»',

  `FIO` VARCHAR(128) COMMENT 'ФИО получателя субсидии',
  `SERVICE_ACCOUNT` VARCHAR(64) COMMENT 'Номер лицевого счета у поставщика',
  `MONTH_SUM` DECIMAL(13,2) COMMENT 'Общая начисленная сумма за потребленные услуги в отчетном месяце (грн.)',
  `SUM` DECIMAL(13,2) COMMENT 'Общая сумма к оплате, включающая задолженность / переплату за предыдущие периоды (грн.)',

  PRIMARY KEY (`id`),
  KEY `key_request_file_id` (`request_file_id`),
  CONSTRAINT `fk_oschadbank_request__request_file` FOREIGN KEY (`request_file_id`) REFERENCES `request_file` (`id`)
) ENGINE = InnoDB CHARSET = utf8 COLLATE = utf8_unicode_ci COMMENT 'Запросы от ощадбанка';

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES (932, 1, UPPER('Директория входящих файлов запросов от ощадбанка')),
  (932, 2, UPPER('Директорія вхідних файлів запитів від ощадбанку'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES (932, 900, 0, 932, 1, 1);

INSERT INTO `config` (`name`,`value`) VALUES ('OSCHADBANK_REQUEST_FILENAME_MASK','\\d*_{YYYY}{MM}\\d*\\.xlsx');

INSERT INTO `update` (`version`) VALUE ('20190326_0.7.11');