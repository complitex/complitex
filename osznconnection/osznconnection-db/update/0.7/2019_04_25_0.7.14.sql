-- ------------------------------
-- Oschadbank response file
-- ------------------------------
DROP TABLE IF EXISTS `oschadbank_response_file`;
CREATE TABLE `oschadbank_response_file` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `request_file_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор файла',
  `EDRPOU` VARCHAR(16) COMMENT 'ЕДРПОУ',
  `PROVIDER_NAME` VARCHAR(128) COMMENT 'Название поставщика',
  `DOCUMENT_NUMBER` VARCHAR(64) COMMENT '№ Анкеты',
  `SERVICE_NAME` VARCHAR(1024) COMMENT 'Название услуги',
  `REPORTING_PERIOD` VARCHAR(16) COMMENT 'Отчетный период',
  `PROVIDER_CODE` VARCHAR(16) COMMENT 'Код Банка Поставщика услуги',
  `PROVIDER_ACCOUNT` VARCHAR(32) COMMENT 'р/с Поставщика услуги',
  `PROVIDER_IBAN` VARCHAR(64) COMMENT 'IBAN Поставщика',
  `PAYMENT_NUMBER` VARCHAR(64) COMMENT 'Номер платежного документа',
  `REFERENCE_DOCUMENT` VARCHAR(32) COMMENT 'Референс документа',
  `PAYMENT_DATE` VARCHAR(16) COMMENT 'Дата формирования платежного документа',
  `TOTAL_AMOUNT` VARCHAR(16) COMMENT 'Общая сумма перечисления',
  `ANALYTICAL_ACCOUNT` VARCHAR(32) COMMENT 'Номер аналитического счета',
  `FEE` VARCHAR(16) COMMENT 'Комиссионное вознаграждение Банка',
  `FEE_CODE` VARCHAR(16) COMMENT 'Код банка для перечисления комиссии',
  `REGISTRY_ID` VARCHAR(32) COMMENT 'ID реестра',
  PRIMARY KEY (`id`),
  KEY `key_request_file_id` (`request_file_id`),
  CONSTRAINT `fk_oschadbank_response_file__request_file` FOREIGN KEY (`request_file_id`) REFERENCES `request_file` (`id`)
) ENGINE = InnoDB CHARSET = utf8 COLLATE = utf8_unicode_ci COMMENT 'Файл ответов от ощадбанка';

-- ------------------------------
-- Oschadbank response
-- ------------------------------
DROP TABLE IF EXISTS `oschadbank_response`;
CREATE TABLE `oschadbank_response` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `request_file_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор файла',
  `account_number` VARCHAR(100) NULL COMMENT 'Номер счета',
  `status` INTEGER NOT NULL DEFAULT 240 COMMENT 'Статус',

  `UTSZN` VARCHAR(64) COMMENT 'Номер УТСЗН',
  `OSCHADBANK_ACCOUNT` VARCHAR(64) COMMENT 'Номер учетной записи получателя жилищной субсидии в АО «Ощадбанк»',
  `FIO` VARCHAR(128) COMMENT 'ФИО получателя субсидии',
  `SERVICE_ACCOUNT` VARCHAR(64) COMMENT 'Номер лицевого счета у поставщика',
  `MONTH_SUM` VARCHAR(16) COMMENT 'Общая начисленная сумма за потребленные услуги в отчетном месяце (грн.)',
  `SUM` VARCHAR(16) COMMENT 'Общая сумма к оплате, включающая задолженность / переплату за предыдущие периоды (грн.)',
  `SUBSIDY_SUM` VARCHAR(16) COMMENT 'Сумма, уплаченная за счет субсидии',
  `DESCRIPTION` VARCHAR(1024) COMMENT 'Описание ошибок',
  PRIMARY KEY (`id`),
  KEY `key_request_file_id` (`request_file_id`),
  CONSTRAINT `fk_oschadbank_response__request_file` FOREIGN KEY (`request_file_id`) REFERENCES `request_file` (`id`)
) ENGINE = InnoDB CHARSET = utf8 COLLATE = utf8_unicode_ci COMMENT 'Ответы от ощадбанка';

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES
    (934, 1, UPPER('Директория входящих файлов ответов от ощадбанка')),
    (934, 2, UPPER('Директорія вхідних файлів відповідей від ощадбанку'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES
    (934, 900, 0, 934, 1, 1);

INSERT INTO `entity_string_value`(`id`, `locale_id`, `value`) VALUES
    (935, 1, UPPER('Директория исходящих файлов ответов от ощадбанка')),
    (935, 2, UPPER('Директорія вихідних файлів відповідей від ощадбанку'));
INSERT INTO `entity_attribute`(`id`, `entity_id`, `required`, `name_id`, `system`, `value_type_id`) VALUES
    (935, 900, 0, 935, 1, 1);

INSERT INTO `config` (`name`,`value`) VALUES ('OSCHADBANK_RESPONSE_FILENAME_MASK','.*\\.xlsx');

INSERT INTO `update` (`version`) VALUE ('20190425_0.7.14');