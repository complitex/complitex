-- /*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- ------------------------------
-- Tarif group
-- ------------------------------
DROP TABLE IF EXISTS `tarif_group`;

CREATE TABLE `tarif_group` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Не используется',
  `parent_entity_id` BIGINT(20) COMMENT 'Не используется',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия параметров объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата завершения периода действия параметров объекта',
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'Статус объекта: ACTIVE, INACTIVE или ARCHIVE',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  `external_id` BIGINT(20) COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_tarif_group__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_tarif_group__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Тарифная группа';

DROP TABLE IF EXISTS `tarif_group_attribute`;

CREATE TABLE `tarif_group_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута. Возможные значения: 3200 - НАЗВАНИЕ, 3201 - КОД ТАРИФНОЙ ГРУППЫ',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `value_type_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа. Возможные значение: 3200 - STRING_CULTURE, 3201 - STRING',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия параметров атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия параметров атрибута',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'Статус атрибута: ACTIVE, INACTIVE или ARCHIVE',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_tarif_group_attribute__tarif_group` FOREIGN KEY (`object_id`) REFERENCES `tarif_group`(`object_id`),
  CONSTRAINT `fk_tarif_group_attribute__attribute_type` FOREIGN KEY (`attribute_type_id`)
  REFERENCES `attribute_type` (`id`),
  CONSTRAINT `fk_tarif_group_attribute__attribute_value_type` FOREIGN KEY (`value_type_id`)
  REFERENCES `attribute_value_type` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты объекта тарифная группа';

DROP TABLE IF EXISTS `tarif_group_string_culture`;

CREATE TABLE `tarif_group_string_culture` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор значения',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`),
  CONSTRAINT `fk_tarif_group_string_culture__locales` FOREIGN KEY (`locale_id`) REFERENCES `locales` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализированное значение атрибута тарифной группы';

-- ------------------------------
-- Tarif
-- ------------------------------
DROP TABLE IF EXISTS `tarif`;

CREATE TABLE `tarif` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Не используется',
  `parent_entity_id` BIGINT(20) COMMENT 'Не используется',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия параметров объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата завершения периода действия параметров объекта',
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'Статус объекта: ACTIVE, INACTIVE или ARCHIVE',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  `external_id` BIGINT(20) COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_tarif__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_tarif__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Тарифная группа';

DROP TABLE IF EXISTS `tarif_attribute`;

CREATE TABLE `tarif_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута. Возможные значения: 3300 - НАЗВАНИЕ, 3301 - КОД ТАРИФА, 3302 - ССЫЛКА НА ТАРИФНУЮ ГРУППУ',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `value_type_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа. Возможные значение: 3300 - STRING_CULTURE, 3301 - STRING, 3302 - tarif_group',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия параметров атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия параметров атрибута',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'Статус атрибута: ACTIVE, INACTIVE или ARCHIVE',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_tarif_attribute__tarif` FOREIGN KEY (`object_id`) REFERENCES `tarif`(`object_id`),
  CONSTRAINT `fk_tarif_attribute__attribute_type` FOREIGN KEY (`attribute_type_id`)
  REFERENCES `attribute_type` (`id`),
  CONSTRAINT `fk_tarif_attribute__attribute_value_type` FOREIGN KEY (`value_type_id`)
  REFERENCES `attribute_value_type` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты объекта тариф';

DROP TABLE IF EXISTS `tarif_string_culture`;

CREATE TABLE `tarif_string_culture` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор значения',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`),
  CONSTRAINT `fk_tarif_string_culture__locales` FOREIGN KEY (`locale_id`) REFERENCES `locales` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализированное значение атрибута тарифа';

-- ------------------------------
-- Heatmeter Type
-- ------------------------------
DROP TABLE IF EXISTS `heatmeter_type`;

CREATE TABLE `heatmeter_type` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Не используется',
  `parent_entity_id` BIGINT(20) COMMENT 'Не используется',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия параметров объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата завершения периода действия параметров объекта',
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'Статус объекта: ACTIVE, INACTIVE или ARCHIVE',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  `external_id` BIGINT(20) COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_heatmeter_type__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_heatmeter_type__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Тип теплосчетчика';

DROP TABLE IF EXISTS `heatmeter_type_attribute`;

CREATE TABLE `heatmeter_type_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута. Возможные значения: 3400 - НАЗВАНИЕ',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `value_type_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа. Возможные значение: 3400 - STRING_CULTURE',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия параметров атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия параметров атрибута',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'Статус атрибута: ACTIVE, INACTIVE или ARCHIVE',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_heatmeter_type_attribute__heatmeter_type` FOREIGN KEY (`object_id`) REFERENCES `heatmeter_type`(`object_id`),
  CONSTRAINT `fk_heatmeter_type_attribute__attribute_type` FOREIGN KEY (`attribute_type_id`)
  REFERENCES `attribute_type` (`id`),
  CONSTRAINT `fk_heatmeter_type_attribute__attribute_value_type` FOREIGN KEY (`value_type_id`)
  REFERENCES `attribute_value_type` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты типа теплосчетчика';

DROP TABLE IF EXISTS `heatmeter_type_string_culture`;

CREATE TABLE `heatmeter_type_string_culture` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор значения',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`),
  CONSTRAINT `fk_heatmeter_type_string_culture__locales` FOREIGN KEY (`locale_id`) REFERENCES `locales` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализированное значение атрибута типа теплосчетчика';

-- ------------------------------
-- heatmeter Period Type
-- ------------------------------
DROP TABLE IF EXISTS `heatmeter_period_type`;

CREATE TABLE `heatmeter_period_type` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Не используется',
  `parent_entity_id` BIGINT(20) COMMENT 'Не используется',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия параметров объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата завершения периода действия параметров объекта',
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'Статус объекта: ACTIVE, INACTIVE или ARCHIVE',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  `external_id` BIGINT(20) COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_heatmeter_period_type__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_heatmeter_period_type__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Тип периода теплосчетчика';

DROP TABLE IF EXISTS `heatmeter_period_type_attribute`;

CREATE TABLE `heatmeter_period_type_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута. Возможные значения: 3500 - НАЗВАНИЕ',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `value_type_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа. Возможные значение: 3500 - STRING_CULTURE',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия параметров атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия параметров атрибута',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'Статус атрибута: ACTIVE, INACTIVE или ARCHIVE',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_heatmeter_period_type_attribute__heatmeter_period_type` FOREIGN KEY (`object_id`) REFERENCES `heatmeter_period_type`(`object_id`),
  CONSTRAINT `fk_heatmeter_period_type_attribute__attribute_type` FOREIGN KEY (`attribute_type_id`)
  REFERENCES `attribute_type` (`id`),
  CONSTRAINT `fk_heatmeter_period_type_attribute__attribute_value_type` FOREIGN KEY (`value_type_id`)
  REFERENCES `attribute_value_type` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты типа периода теплосчетчика';

DROP TABLE IF EXISTS `heatmeter_period_type_string_culture`;

CREATE TABLE `heatmeter_period_type_string_culture` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор значения',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`),
  CONSTRAINT `fk_heatmeter_period_type_string_culture__locales` FOREIGN KEY (`locale_id`) REFERENCES `locales` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализированное значение атрибута типа периода теплосчетчика';

-- ------------------------------
-- Heatmeter
-- ------------------------------

DROP TABLE IF EXISTS `heatmeter`;
CREATE TABLE `heatmeter`(
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `ls` INT(7) NOT NULL COMMENT 'Номер л/с теплосчетчика',
  `organization_id` BIGINT(20) NOT NULL COMMENT 'Ссылка на ПУ',
  `type` BIGINT(20) NOT NULL COMMENT 'Ссылка на тип счетчика',
  `calculating` TINYINT(1) COMMENT 'Используется в расчетах',
  `updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `heatmeter_unique_id` (`ls`, `organization_id`),
  KEY `key_type_id` (`type`),
  KEY `key_organization_id` (`organization_id`),
  CONSTRAINT `fk_heatmeter__heatmeter_type` FOREIGN KEY (`type`) REFERENCES `heatmeter_type` (`object_id`),
  CONSTRAINT `fk_heatmeter__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Теплосчетчик';

-- ------------------------------
-- Period
-- ------------------------------

DROP TABLE IF EXISTS `heatmeter_period`;
CREATE TABLE `heatmeter_period`(
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `heatmeter_id` BIGINT(20) NOT NULL COMMENT 'Теплосчетчик',
  `type` BIGINT(20) NOT NULL COMMENT 'Ссылка на тип теплосчетчика',
  `sub_type` BIGINT(20) NULL COMMENT 'Ссылка на подтип',
  `begin_date` DATETIME NOT NULL COMMENT 'Дата начала периода',
  `end_date` DATETIME NOT NULL COMMENT 'Дата окончания периода',
  `object_id` BIGINT(20) NULL COMMENT 'Ссылка на объект',
  `begin_om` DATE NOT NULL COMMENT 'Опер.месяц начиная с которого действует данный параметр расчета',
  `end_om` DATE NOT NULL COMMENT 'Последний опер.месяц в котором действует данный параметр расчета',
  `updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `key_heatmeater_connection_id` (`heatmeter_id`),
  KEY `key_type` (`type`),
  KEY `key_sub_type` (`sub_type`),
  KEY `key_begin_date` (`begin_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_begin_om` (`begin_om`),
  KEY `key_end_om` (`end_om`),
  CONSTRAINT `fk_heatmeter_period__heatmeter` FOREIGN KEY (`heatmeter_id`) REFERENCES `heatmeter` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Период теплосчетчика';

-- ------------------------------
-- Payload
-- ------------------------------

DROP TABLE IF EXISTS `heatmeter_payload`;
CREATE TABLE `heatmeter_payload`(
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор',
  `tablegram_record_id` BIGINT(20) COMMENT 'Идентификатор записи файла табуляграммы',
  `payload1` DECIMAL(5, 2) COMMENT 'Процент распределения расхода для тарифной группы 1',
  `payload2` DECIMAL(5, 2) COMMENT 'Процент распределения расхода для тарифной группы 2',
  `payload3` DECIMAL(5, 2) COMMENT 'Процент распределения расхода для тарифной группы 3',
  PRIMARY KEY (`id`),
  KEY `key_tablegram_record_id` (`tablegram_record_id`),
  CONSTRAINT `fk_heatmeter_payload__heatmeter_period` FOREIGN KEY (`id`) REFERENCES `heatmeter_period` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_heatmeter_payload__tablegram_record` FOREIGN KEY (`tablegram_record_id`) REFERENCES `tablegram_record` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Проценты распределения расходов';

-- ------------------------------
-- Input
-- ------------------------------

DROP TABLE IF EXISTS `heatmeter_input`;
CREATE TABLE `heatmeter_input`(
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор',
  `value` DECIMAL(15, 7),
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_heatmeter_input__heatmeter_period` FOREIGN KEY (`id`) REFERENCES `heatmeter_period` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Расход со счетчика';

-- ------------------------------
-- Consumption
-- ------------------------------

DROP TABLE IF EXISTS `heatmeter_consumption`;
CREATE TABLE `heatmeter_consumption`(
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `heatmeter_input_id` BIGINT(20) NOT NULL COMMENT 'Ссылка на расход',
  `om` DATE NOT NULL COMMENT  'Операционный месяц',
  `consumption1` DECIMAL(15, 7) COMMENT 'Расхода для тарифной группы 1',
  `consumption2` DECIMAL(15, 7) COMMENT 'Расхода для тарифной группы 2',
  `consumption3` DECIMAL(15, 7) COMMENT 'Расхода для тарифной группы 3',
  `begin_date` DATETIME NOT NULL COMMENT 'Дата начала',
  `end_date` DATETIME NOT NULL COMMENT 'Дата окончания',
  `status` INT COMMENT 'Статус',
  PRIMARY KEY (`id`),
  UNIQUE KEY `consumption_unique_id` (`heatmeter_input_id`, `begin_date`, `end_date`, `om`),
  KEY `key_heatmeter_input_id` (`heatmeter_input_id`),
  KEY `key_om` (`om`),
  CONSTRAINT `fk_heatmeter_consumption__heatmeter_input` FOREIGN KEY (`heatmeter_input_id`)
    REFERENCES `heatmeter_input` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Результаты расчета';

-- ------------------------------
-- Tablegram
-- ------------------------------

DROP TABLE IF EXISTS `tablegram`;
CREATE TABLE `tablegram`(
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
    `file_name` VARCHAR (255) NOT NULL COMMENT 'Название файла',
    `begin_date` DATE NOT NULL COMMENT  'Операционный месяц',
    `uploaded` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT  'Дата загрузки',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Файлы табуляграмм';

-- ------------------------------
-- Tablegram Record
-- ------------------------------

DROP TABLE IF EXISTS `tablegram_record`;
CREATE TABLE `tablegram_record`(
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
    `tablegram_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор файла табуляграммы',
    `heatmeter_id` BIGINT(20) NULL COMMENT 'Ссылка на теплосчетчик',
    `ls` INT(7) NOT NULL COMMENT 'Лицевой счет',
    `name` VARCHAR(255) NOT NULL COMMENT 'Абонент',
    `address` VARCHAR(255) NOT NULL COMMENT 'Адрес',
    `payload1` DECIMAL(5, 2) COMMENT 'Процент распределения расхода для тарифной группы 1',
    `payload2` DECIMAL(5, 2) COMMENT 'Процент распределения расхода для тарифной группы 2',
    `payload3` DECIMAL(5, 2) COMMENT 'Процент распределения расхода для тарифной группы 3',
    `status` INT COMMENT 'Статус',
    PRIMARY KEY (`id`),
    UNIQUE KEY `tablegram_record_unique_id` (`tablegram_id`, `ls`),
    KEY `key_tablegram_id` (`tablegram_id`),
    KEY `key_heatmeter_id` (`heatmeter_id`),
    CONSTRAINT `fk_tablegram_record__tablegram` FOREIGN KEY (`tablegram_id`) REFERENCES `tablegram` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_tablegram_record__heatmeter` FOREIGN KEY (`heatmeter_id`) REFERENCES `heatmeter` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Запись файла табуляграммы';

-- ------------------------------
-- Corrections
-- ------------------------------

DROP TABLE IF EXISTS `heatmeter_correction`;
CREATE TABLE `heatmeter_correction` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
    `system_heatmeter_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор счетчика в системе',
    `external_heatmeter_id` VARCHAR(50) COMMENT 'Внешний идентификатор счетчика',
    `heatmeter_number` VARCHAR(50) COMMENT 'Номер счетчика во внешней системе',
    `binding_date` DATETIME NOT NULL COMMENT 'Время связывания',
    `binding_status` INT NOT NULL COMMENT 'Статус связывания',
    `history` TINYINT (1) DEFAULT 0 NOT NULL COMMENT '', 
    PRIMARY KEY (`id`),
    KEY `key_system_heatmeter_id` (`system_heatmeter_id`),
    CONSTRAINT `fk_heatmeter_correction__heatmeter` FOREIGN KEY (`system_heatmeter_id`) REFERENCES `heatmeter` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Коррекция счетчика';

DROP TABLE IF EXISTS `heatmeter_bind`;
CREATE TABLE `heatmeter_bind` (
    `heatmeter_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор счетчика в системе',
    `processed` TINYINT (1) DEFAULT 0 NOT NULL COMMENT 'Индикатор обработки',
    PRIMARY KEY (`heatmeter_id`),
    CONSTRAINT `fk_heatmeter_bind__heatmeter` FOREIGN KEY (`heatmeter_id`) REFERENCES `heatmeter` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Вспомогательная таблица для связывания счетчиков';

-- ------------------------------
-- Operating month
-- ------------------------------

DROP TABLE IF EXISTS `operating_month`;
CREATE TABLE `operating_month`(
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
    `organization_id` BIGINT(20) NOT NULL COMMENT 'Ссылка на организацию',
    `begin_om` DATE NOT NULL COMMENT  'Операционный месяц',
    `updated` TIMESTAMP NULL COMMENT  'Время изменения опер. месяца',
    PRIMARY KEY (`id`),
    KEY `key_organization_id` (`organization_id`),
    CONSTRAINT `fk_operating_month__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Журнал операционных месяцев';

-- ------------------------------
-- Service
-- ------------------------------

DROP TABLE IF EXISTS `service`;
CREATE TABLE `service` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Не используется',
  `parent_entity_id` BIGINT(20) COMMENT 'Не используется',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия параметров объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата завершения периода действия параметров объекта',
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'Статус объекта: ACTIVE, INACTIVE или ARCHIVE',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  `external_id` BIGINT(20) COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_service__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_service__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Услуга';

DROP TABLE IF EXISTS `service_attribute`;
CREATE TABLE `service_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута. Возможные значения: todo',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `value_type_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа. Возможные значение: todo',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия параметров атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия параметров атрибута',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'Статус атрибута: ACTIVE, INACTIVE или ARCHIVE',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_service_attribute__service` FOREIGN KEY (`object_id`) REFERENCES `service`(`object_id`),
  CONSTRAINT `fk_service_attribute__attribute_type` FOREIGN KEY (`attribute_type_id`)
  REFERENCES `attribute_type` (`id`),
  CONSTRAINT `fk_service_attribute__attribute_value_type` FOREIGN KEY (`value_type_id`)
  REFERENCES `attribute_value_type` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты услуги';

DROP TABLE IF EXISTS `service_string_culture`;
CREATE TABLE `service_string_culture` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор значения',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`),
  CONSTRAINT `fk_service_string_culture__locales` FOREIGN KEY (`locale_id`) REFERENCES `locales` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализированное значение атрибута услуги';

-- ------------------------------
-- Contract services
-- ------------------------------

DROP TABLE IF EXISTS `service_contract`;
CREATE TABLE `service_contract`(
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `begin_date` DATE NOT NULL COMMENT  'Дата начала',
  `end_date` DATE COMMENT  'Дата окончания',
  `number` VARCHAR(64) NOT NULL COMMENT 'Номер',
  `service_provider_id` BIGINT(20) NOT NULL COMMENT 'Поставщик услуг',
  `organization_id` BIGINT(20) NOT NULL COMMENT 'Обслуживающая организация',

  PRIMARY KEY (`id`),
  KEY `key_service_provider_id` (`service_provider_id`),
  CONSTRAINT `fk_service_contract__service_provider` FOREIGN KEY (`service_provider_id`) REFERENCES `organization` (`object_id`),
  KEY `key_organization_id` (`organization_id`),
  CONSTRAINT `fk_service_contract__servicing_organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Договор на поставку услуги';

DROP TABLE IF EXISTS `service_contract_building`;
CREATE TABLE `service_contract_building`(
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `building_code_id` BIGINT(20) NOT NULL COMMENT 'Код дома',
  `service_contract_id` BIGINT(20) NOT NULL COMMENT 'Договор на поставку услуги',

  PRIMARY KEY (`id`),
  KEY `key_building_code_id` (`building_code_id`),
  CONSTRAINT `fk_service_contract_building__building_code` FOREIGN KEY (`building_code_id`) REFERENCES `building_code` (`id`),
  KEY `key_service_contract_id` (`service_contract_id`),
  CONSTRAINT `fk_service_contract_building__service_contract` FOREIGN KEY (`service_contract_id`) REFERENCES `service_contract` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Список домов договора поставки услуг';

DROP TABLE IF EXISTS `service_contract_service`;
CREATE TABLE `service_contract_service`(
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `service_id` BIGINT(20) NOT NULL COMMENT 'Услуга',
  `service_contract_id` BIGINT(20) NOT NULL COMMENT 'Договор на поставку услуги',

  PRIMARY KEY (`id`),
  KEY `key_service_object_id` (`service_id`),
  CONSTRAINT `fk_service_contract_service__service` FOREIGN KEY (`service_id`) REFERENCES `service` (`object_id`),
  KEY `key_service_contract_id` (`service_contract_id`),
  CONSTRAINT `fk_service_contract_building__service_contract2` FOREIGN KEY (`service_contract_id`) REFERENCES `service_contract` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Список услуг договора поставки услуг';

-- ------------------------------
-- Consumption File
-- ------------------------------

DROP TABLE IF EXISTS `consumption_file`;
CREATE TABLE `consumption_file`(
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `name` VARCHAR(255) NOT NULL COMMENT 'Название файла',
  `om` DATE NOT NULL COMMENT 'Операционный месяц',
  `service_provider_id` BIGINT COMMENT 'Поставщик услуг',
  `service_id` BIGINT COMMENT 'Услуга',
  `user_organization_id` BIGINT COMMENT 'Организация пользователей',
  `type` INT NOT NULL COMMENT 'Тип файла',
  `status` INT NOT NULL COMMENT 'Статус файла',
  `loaded` DATETIME NOT NULL COMMENT 'Дата загрузки',
  `check_sum` VARCHAR(32) COMMENT 'Контрольная сумма',

  PRIMARY KEY (`id`),
  KEY `key_service_provider_id` (`service_provider_id`),
  CONSTRAINT `fk_consumption_file__organization` FOREIGN KEY (`service_provider_id`) REFERENCES `organization` (`object_id`),
  KEY `key_service_id` (`service_id`),
  CONSTRAINT `fk_consumption_file__service` FOREIGN KEY (`service_id`) REFERENCES `service` (`object_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  CONSTRAINT `fk_consumption_file__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `user_organization` (`id`)
) CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Файлы потреблений';

-- ------------------------------
-- Central Heating Consumption
-- ------------------------------
DROP TABLE IF EXISTS `central_heating_consumption`;
CREATE TABLE `central_heating_consumption`(
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '',
  `consumption_file_id` BIGINT NOT NULL COMMENT 'Файл начислений',
  `number` VARCHAR(100) COMMENT 'Номер по порядку',
  `district_code` VARCHAR(100) COMMENT 'Название района тепловых сетей',
  `organization_code` VARCHAR(100) COMMENT 'Код ЖЭКа ГИВЦ',
  `building_code` VARCHAR(100) COMMENT 'Код дома ГИВЦ',
  `account_number` VARCHAR(100) COMMENT 'ГПУ',
  `street` VARCHAR(100) COMMENT 'Адрес дома',
  `building_number` VARCHAR(100) COMMENT 'Номер дома',
  `common_volume` VARCHAR(100) COMMENT 'Информация ПУ в расходе по коллективному счетчику отопления',
  `appartment_range` VARCHAR(100) COMMENT 'Дипазон квартир, потребляющих услугу с этого счетчика',
  `begin_date` VARCHAR(100) COMMENT 'Дата установки счетчика',
  `end_date` VARCHAR(100) COMMENT 'Дата отключения счетчика',
  `common_area` VARCHAR(100) COMMENT 'Отапливаемая площадь квартир, потребляемых услугу с данного коллективного счетчика',
  `meter_volume` VARCHAR(100) COMMENT 'Объем услуги потребленный по инливидуальным счетчикам',
  `meter_area` VARCHAR(100) COMMENT 'Отапливаемая площадь квартир с индивидуальными счетчиками',
  `common_rent_area` VARCHAR(100) COMMENT 'Отапливаемая площадь арендованных помещений, потребляющих услугу с данного коллективного счетчика',
  `meter_rent_volume` VARCHAR(100) COMMENT 'Объем услуги потребленный арендуемым помещениями по индивидуальным счетчикам',
  `meter_rent_area` VARCHAR(100) COMMENT 'Отапливаемая площадь арендованных помещений с индивидуальными счетчиками',
  `no_meter_area` VARCHAR(100) COMMENT 'Отапливаемая площадь без индивидуальных счетчиков',
  `no_meter_rate` VARCHAR(100) COMMENT 'Кол-во Гкал на 1 м2 площади без индивидуальных счетчиков',
  `rate` VARCHAR(100) COMMENT 'Расчетный тариф отопления на 1 м2 для населения',
  `no_meter_volume` VARCHAR(100) COMMENT 'Согласованный объем услуги для квартир без индивидуальных счетчиков',
  `status` INT NOT NULL COMMENT 'Статус записи',

  PRIMARY KEY (`id`),
  KEY `key_consumption_file_id` (`consumption_file_id`),
  CONSTRAINT `fk_central_heating_consumption__consumption_file` FOREIGN KEY (`consumption_file_id`)
    REFERENCES `consumption_file` (`id`) ON DELETE CASCADE
) CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Записи начислений центрального отопления';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
