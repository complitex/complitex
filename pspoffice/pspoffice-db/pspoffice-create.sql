/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Person --
DROP TABLE IF EXISTS `person`;
CREATE TABLE `person` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `object_id` BIGINT(20) NOT NULL,
  `parent_id` BIGINT(20),
  `parent_entity_id` BIGINT(20),
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_date` TIMESTAMP NULL DEFAULT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0,
  `external_id` BIGINT(20),
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (object_id),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_person__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_person__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `person_attribute`;
CREATE TABLE `person_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `attribute_id` BIGINT(20) NOT NULL,
  `object_id` BIGINT(20) NOT NULL,
  `attribute_type_id` BIGINT(20) NOT NULL,
  `value_id` BIGINT(20),
  `value_type_id` BIGINT(20) NOT NULL,
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_date` TIMESTAMP NULL DEFAULT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_person_attribute__person` FOREIGN KEY (`object_id`) REFERENCES `person`(`object_id`),
  CONSTRAINT `fk_person_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`)
    REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_person_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`)
    REFERENCES `entity_attribute_value_type` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `person_string_culture`;
CREATE TABLE `person_string_culture` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `id` BIGINT(20) NOT NULL,
  `locale_id` BIGINT(20) NOT NULL,
  `value` VARCHAR(1000),
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`),
  CONSTRAINT `fk_person_string_culture__locales` FOREIGN KEY (`locale_id`) REFERENCES `locales` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Apartment Card --
DROP TABLE IF EXISTS `apartment_card`;
CREATE TABLE `apartment_card` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `object_id` BIGINT(20) NOT NULL,
  `parent_id` BIGINT(20),
  `parent_entity_id` BIGINT(20),
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_date` TIMESTAMP NULL DEFAULT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0,
  `external_id` BIGINT(20),
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (object_id),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_apartment_card__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_apartment_card__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `apartment_card_attribute`;
CREATE TABLE `apartment_card_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `attribute_id` BIGINT(20) NOT NULL,
  `object_id` BIGINT(20) NOT NULL,
  `attribute_type_id` BIGINT(20) NOT NULL,
  `value_id` BIGINT(20),
  `value_type_id` BIGINT(20) NOT NULL,
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_date` TIMESTAMP NULL DEFAULT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_apartment_card_attribute__apartment_card` FOREIGN KEY (`object_id`) REFERENCES `apartment_card`(`object_id`),
  CONSTRAINT `fk_apartment_card_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`)
    REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_apartment_card_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`)
    REFERENCES `entity_attribute_value_type` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `apartment_card_string_culture`;
CREATE TABLE `apartment_card_string_culture` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `id` BIGINT(20) NOT NULL,
  `locale_id` BIGINT(20) NOT NULL,
  `value` VARCHAR(1000),
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`),
  CONSTRAINT `fk_apartment_card_string_culture__locales` FOREIGN KEY (`locale_id`) REFERENCES `locales` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `registration`;
CREATE TABLE `registration` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `object_id` BIGINT(20) NOT NULL,
  `parent_id` BIGINT(20),
  `parent_entity_id` BIGINT(20),
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_date` TIMESTAMP NULL DEFAULT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0,
  `external_id` BIGINT(20),
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (object_id),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_registration__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_registration__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `registration_attribute`;
CREATE TABLE `registration_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `attribute_id` BIGINT(20) NOT NULL,
  `object_id` BIGINT(20) NOT NULL,
  `attribute_type_id` BIGINT(20) NOT NULL,
  `value_id` BIGINT(20),
  `value_type_id` BIGINT(20) NOT NULL,
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_date` TIMESTAMP NULL DEFAULT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_registration_attribute__registration` FOREIGN KEY (`object_id`) REFERENCES `registration`(`object_id`),
  CONSTRAINT `fk_registration_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`)
    REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_registration_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`)
    REFERENCES `entity_attribute_value_type` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `registration_string_culture`;
CREATE TABLE `registration_string_culture` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `id` BIGINT(20) NOT NULL,
  `locale_id` BIGINT(20) NOT NULL,
  `value` VARCHAR(1000),
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`),
  CONSTRAINT `fk_registration_string_culture__locales` FOREIGN KEY (`locale_id`) REFERENCES `locales` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Owner relationship --
DROP TABLE IF EXISTS `owner_relationship`;
CREATE TABLE `owner_relationship` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `object_id` BIGINT(20) NOT NULL,
  `parent_id` BIGINT(20),
  `parent_entity_id` BIGINT(20),
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_date` TIMESTAMP NULL DEFAULT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0,
  `external_id` BIGINT(20),
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (object_id),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_owner_relationship__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_owner_relationship__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `owner_relationship_attribute`;
CREATE TABLE `owner_relationship_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `attribute_id` BIGINT(20) NOT NULL,
  `object_id` BIGINT(20) NOT NULL,
  `attribute_type_id` BIGINT(20) NOT NULL,
  `value_id` BIGINT(20),
  `value_type_id` BIGINT(20) NOT NULL,
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_date` TIMESTAMP NULL DEFAULT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_owner_relationship_attribute__registration` FOREIGN KEY (`object_id`) REFERENCES `owner_relationship`(`object_id`),
  CONSTRAINT `fk_owner_relationship_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`)
    REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_owner_relationship_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`)
    REFERENCES `entity_attribute_value_type` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `owner_relationship_string_culture`;
CREATE TABLE `owner_relationship_string_culture` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `id` BIGINT(20) NOT NULL,
  `locale_id` BIGINT(20) NOT NULL,
  `value` VARCHAR(1000),
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`),
  CONSTRAINT `fk_owner_relationship_string_culture__locales` FOREIGN KEY (`locale_id`) REFERENCES `locales` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Form of ownership
DROP TABLE IF EXISTS `ownership_form`;

CREATE TABLE `ownership_form` (
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
  CONSTRAINT `fk_ownership_form__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_ownership_form__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Форма собственности';

DROP TABLE IF EXISTS `ownership_form_attribute`;

CREATE TABLE `ownership_form_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута. Возможные значения: 2500 - НАЗВАНИЕ',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `value_type_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа. Возможные значение: 2500 - STRING_CULTURE',
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
  CONSTRAINT `fk_ownership_form_attribute__ownership` FOREIGN KEY (`object_id`) REFERENCES `ownership_form`(`object_id`),
  CONSTRAINT `fk_ownership_form_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`)
    REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_ownership_form_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`)
    REFERENCES `entity_attribute_value_type` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты объекта формы собственности';

DROP TABLE IF EXISTS `ownership_form_string_culture`;

CREATE TABLE `ownership_form_string_culture` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор значения',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`),
  CONSTRAINT `fk_ownership_form_string_culture__locales` FOREIGN KEY (`locale_id`) REFERENCES `locales` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализированное значение атрибута формы собственности';

-- Registration type
DROP TABLE IF EXISTS `registration_type`;

CREATE TABLE `registration_type` (
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
  CONSTRAINT `fk_registration_type__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_registration_type__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Тип регистрации';

DROP TABLE IF EXISTS `registration_type_attribute`;

CREATE TABLE `registration_type_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута. Возможные значения: 2600 - НАЗВАНИЕ',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `value_type_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа. Возможные значение: 2600 - STRING_CULTURE',
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
  CONSTRAINT `fk_registration_type_attribute__ownership` FOREIGN KEY (`object_id`) REFERENCES `registration_type`(`object_id`),
  CONSTRAINT `fk_registration_type_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`)
    REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_registration_type_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`)
    REFERENCES `entity_attribute_value_type` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты объекта тип регистрации';

DROP TABLE IF EXISTS `registration_type_string_culture`;

CREATE TABLE `registration_type_string_culture` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор значения',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`),
  CONSTRAINT `fk_registration_type_string_culture__locales` FOREIGN KEY (`locale_id`) REFERENCES `locales` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализированное значение атрибута типа регистрации';

-- Document type
DROP TABLE IF EXISTS `document_type`;

CREATE TABLE `document_type` (
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
  CONSTRAINT `fk_document_type__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_document_type__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Тип документа';

DROP TABLE IF EXISTS `document_type_attribute`;

CREATE TABLE `document_type_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута. Возможные значения: 2700 - НАЗВАНИЕ',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `value_type_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа. Возможные значение: 2700 - STRING_CULTURE',
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
  CONSTRAINT `fk_document_type_attribute__ownership` FOREIGN KEY (`object_id`) REFERENCES `document_type`(`object_id`),
  CONSTRAINT `fk_document_type_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`)
    REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_document_type_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`)
    REFERENCES `entity_attribute_value_type` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты объекта тип документа';

DROP TABLE IF EXISTS `document_type_string_culture`;

CREATE TABLE `document_type_string_culture` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор значения',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`),
  CONSTRAINT `fk_document_type_string_culture__locales` FOREIGN KEY (`locale_id`) REFERENCES `locales` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализированное значение атрибута типа документа';

-- Document
DROP TABLE IF EXISTS `document`;

CREATE TABLE `document` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `object_id` BIGINT(20) NOT NULL,
  `parent_id` BIGINT(20),
  `parent_entity_id` BIGINT(20),
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_date` TIMESTAMP NULL DEFAULT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0,
  `external_id` BIGINT(20),
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
  CONSTRAINT `fk_document__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_document__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Документ';

DROP TABLE IF EXISTS `document_attribute`;

CREATE TABLE `document_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `attribute_id` BIGINT(20) NOT NULL,
  `object_id` BIGINT(20) NOT NULL,
  `attribute_type_id` BIGINT(20) NOT NULL,
  `value_id` BIGINT(20),
  `value_type_id` BIGINT(20) NOT NULL,
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_date` TIMESTAMP NULL DEFAULT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_document_attribute__ownership` FOREIGN KEY (`object_id`) REFERENCES `document`(`object_id`),
  CONSTRAINT `fk_document_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`)
    REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_document_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`)
    REFERENCES `entity_attribute_value_type` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты объекта документ';

DROP TABLE IF EXISTS `document_string_culture`;

CREATE TABLE `document_string_culture` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `id` BIGINT(20) NOT NULL,
  `locale_id` BIGINT(20) NOT NULL,
  `value` VARCHAR(1000),
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`),
  CONSTRAINT `fk_document_string_culture__locales` FOREIGN KEY (`locale_id`) REFERENCES `locales` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализированное значение атрибута документа';

-- Military service relation
DROP TABLE IF EXISTS `military_service_relation`;

CREATE TABLE `military_service_relation` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `object_id` BIGINT(20) NOT NULL,
  `parent_id` BIGINT(20),
  `parent_entity_id` BIGINT(20),
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_date` TIMESTAMP NULL DEFAULT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0,
  `external_id` BIGINT(20),
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
  CONSTRAINT `fk_military_service_relation__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_military_service_relation__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Отношение к воинской обязанности';

DROP TABLE IF EXISTS `military_service_relation_attribute`;

CREATE TABLE `military_service_relation_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `attribute_id` BIGINT(20) NOT NULL,
  `object_id` BIGINT(20) NOT NULL,
  `attribute_type_id` BIGINT(20) NOT NULL,
  `value_id` BIGINT(20),
  `value_type_id` BIGINT(20) NOT NULL,
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_date` TIMESTAMP NULL DEFAULT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_military_attribute__ownership` FOREIGN KEY (`object_id`) REFERENCES `military_service_relation`(`object_id`),
  CONSTRAINT `fk_military_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`)
    REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_military_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`)
    REFERENCES `entity_attribute_value_type` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты отношения к воинской обязанности';

DROP TABLE IF EXISTS `military_service_relation_string_culture`;

CREATE TABLE `military_service_relation_string_culture` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `id` BIGINT(20) NOT NULL,
  `locale_id` BIGINT(20) NOT NULL,
  `value` VARCHAR(1000),
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`),
  CONSTRAINT `fk_military_service_relation_string_culture__locales` FOREIGN KEY (`locale_id`) REFERENCES `locales` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализированное значение атрибута отношения к воинской обязанности';

-- Departure reason
DROP TABLE IF EXISTS `departure_reason`;

CREATE TABLE `departure_reason` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `object_id` BIGINT(20) NOT NULL,
  `parent_id` BIGINT(20),
  `parent_entity_id` BIGINT(20),
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_date` TIMESTAMP NULL DEFAULT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0,
  `external_id` BIGINT(20),
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
  CONSTRAINT `fk_departure_reason__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_departure_reason__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Причина выбытия';

DROP TABLE IF EXISTS `departure_reason_attribute`;

CREATE TABLE `departure_reason_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `attribute_id` BIGINT(20) NOT NULL,
  `object_id` BIGINT(20) NOT NULL,
  `attribute_type_id` BIGINT(20) NOT NULL,
  `value_id` BIGINT(20),
  `value_type_id` BIGINT(20) NOT NULL,
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_date` TIMESTAMP NULL DEFAULT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_departure_reason_attribute__ownership` FOREIGN KEY (`object_id`) REFERENCES `departure_reason`(`object_id`),
  CONSTRAINT `fk_departure_reason_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`)
    REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_departure_reason_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`)
    REFERENCES `entity_attribute_value_type` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты причины выбытия';

DROP TABLE IF EXISTS `departure_reason_string_culture`;

CREATE TABLE `departure_reason_string_culture` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `id` BIGINT(20) NOT NULL,
  `locale_id` BIGINT(20) NOT NULL,
  `value` VARCHAR(1000),
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`),
  CONSTRAINT `fk_departure_reason_string_culture__locales` FOREIGN KEY (`locale_id`) REFERENCES `locales` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализированное значение атрибута причины выбытия';

-- Housing rights
DROP TABLE IF EXISTS `housing_rights`;

CREATE TABLE `housing_rights` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `object_id` BIGINT(20) NOT NULL,
  `parent_id` BIGINT(20),
  `parent_entity_id` BIGINT(20),
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_date` TIMESTAMP NULL DEFAULT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0,
  `external_id` BIGINT(20),
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
  CONSTRAINT `fk_housing_rights__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_housing_rights__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Документ права на жилплощадь';

DROP TABLE IF EXISTS `housing_rights_attribute`;

CREATE TABLE `housing_rights_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `attribute_id` BIGINT(20) NOT NULL,
  `object_id` BIGINT(20) NOT NULL,
  `attribute_type_id` BIGINT(20) NOT NULL,
  `value_id` BIGINT(20),
  `value_type_id` BIGINT(20) NOT NULL,
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_date` TIMESTAMP NULL DEFAULT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_housing_rights_attribute__ownership` FOREIGN KEY (`object_id`) REFERENCES `housing_rights`(`object_id`),
  CONSTRAINT `fk_housing_rights_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`)
    REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_housing_rights_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`)
    REFERENCES `entity_attribute_value_type` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Атрибуты документа права на жилплощадь';

DROP TABLE IF EXISTS `housing_rights_string_culture`;

CREATE TABLE `housing_rights_string_culture` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `id` BIGINT(20) NOT NULL,
  `locale_id` BIGINT(20) NOT NULL,
  `value` VARCHAR(1000),
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`),
  CONSTRAINT `fk_housing_rights_string_culture__locales` FOREIGN KEY (`locale_id`) REFERENCES `locales` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Локализированное значение атрибута документа права на жилплощадь';


-- ------------------------------
-- Person First Name
-- ------------------------------

DROP TABLE IF EXISTS `person_first_name`;

CREATE TABLE `person_first_name` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор имени персоны',
  `name` VARCHAR(100) NOT NULL COMMENT 'Имя персоны',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_name` (`name`, `locale_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Имя персоны';

-- ------------------------------
-- Person Middle Name
-- ------------------------------

DROP TABLE IF EXISTS `person_middle_name`;

CREATE TABLE `person_middle_name` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор отчества персоны',
  `name` VARCHAR(100) NOT NULL COMMENT 'Отчество персоны',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_name` (`name`, `locale_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Отчество персоны';

-- ------------------------------
-- Person Last Name
-- ------------------------------

DROP TABLE IF EXISTS `person_last_name`;

CREATE TABLE `person_last_name` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор фамилии персоны',
  `name` VARCHAR(100) NOT NULL COMMENT 'Фамилия персоны',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_name` (`name`, `locale_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Фамилия персоны';


-- ------------------------------
-- Import temporal tables
-- ------------------------------

-- Street correction --
DROP TABLE IF EXISTS `street_correction`;

CREATE TABLE `street_correction` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `id` BIGINT(20) NOT NULL,
  `idjek` VARCHAR(100) NOT NULL,
  `utype` VARCHAR(100),
  `nkod` VARCHAR(100),
  `rtype` VARCHAR(100),
  `nkod1` VARCHAR(100),
  `system_street_id` BIGINT(20),
  `processed` TINYINT(1) NOT NULL default 0,
  `content` VARCHAR(1000) NOT NULL,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `street_correction_id` (`idjek`, `id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Building correction --
DROP TABLE IF EXISTS `building_correction`;

CREATE TABLE `building_correction` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `id` BIGINT(20) NOT NULL,
  `idjek` VARCHAR(100) NOT NULL,
  `idul` VARCHAR(100),
  `dom` VARCHAR(100),
  `korpus` VARCHAR(100),
  `system_building_id` BIGINT(20),
  `processed` TINYINT(1) NOT NULL default 0,
  `content` VARCHAR(1000) NOT NULL,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `building_correction_id` (`idjek`, `id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Form ownerships correction --
DROP TABLE IF EXISTS `ownership_form_correction`;

CREATE TABLE `ownership_form_correction` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `id` BIGINT(20) NOT NULL,
  `idjek` VARCHAR(100) NOT NULL,
  `nkod` VARCHAR(100),
  `system_object_id` BIGINT(20),
  `processed` TINYINT(1) NOT NULL default 0,
  `content` VARCHAR(1000) NOT NULL,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `ownership_form_correction_id` (`idjek`, `id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Military duty corrections --
DROP TABLE IF EXISTS `military_duty_correction`;

CREATE TABLE `military_duty_correction` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `id` BIGINT(20) NOT NULL,
  `idjek` VARCHAR(100) NOT NULL,
  `nkod` VARCHAR(100),
  `system_object_id` BIGINT(20) COMMENT 'Не используется',
  `processed` TINYINT(1) NOT NULL default 0,
  `content` VARCHAR(1000) NOT NULL,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `military_duty_correction_id` (`idjek`, `id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Owner relationship corrections --
DROP TABLE IF EXISTS `owner_relationship_correction`;

CREATE TABLE `owner_relationship_correction` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `id` BIGINT(20) NOT NULL,
  `idjek` VARCHAR(100) NOT NULL,
  `nkod` VARCHAR(100),
  `system_object_id` BIGINT(20),
  `processed` TINYINT(1) NOT NULL default 0,
  `content` VARCHAR(1000) NOT NULL,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `owner_relationship_correction_id` (`idjek`, `id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Departure reason corrections --
DROP TABLE IF EXISTS `departure_reason_correction`;

CREATE TABLE `departure_reason_correction` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `id` BIGINT(20) NOT NULL,
  `idjek` VARCHAR(100) NOT NULL,
  `nkod` VARCHAR(100),
  `system_object_id` BIGINT(20) COMMENT 'Не используется',
  `processed` TINYINT(1) NOT NULL default 0,
  `content` VARCHAR(1000) NOT NULL,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `departure_reason_correction_id` (`idjek`, `id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Registration type corrections --
DROP TABLE IF EXISTS `registration_type_correction`;

CREATE TABLE `registration_type_correction` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `id` BIGINT(20) NOT NULL,
  `idjek` VARCHAR(100) NOT NULL,
  `nkod` VARCHAR(100),
  `system_object_id` BIGINT(20),
  `processed` TINYINT(1) NOT NULL default 0,
  `content` VARCHAR(1000) NOT NULL,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `registration_type_correction_id` (`idjek`, `id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Document type corrections --
DROP TABLE IF EXISTS `document_type_correction`;

CREATE TABLE `document_type_correction` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `id` BIGINT(20) NOT NULL,
  `idjek` VARCHAR(100) NOT NULL,
  `nkod` VARCHAR(100),
  `system_object_id` BIGINT(20),
  `processed` TINYINT(1) NOT NULL default 0,
  `content` VARCHAR(1000) NOT NULL,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `document_type_correction_id` (`idjek`, `id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Owner type corrections --
DROP TABLE IF EXISTS `owner_type_correction`;

CREATE TABLE `owner_type_correction` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `id` BIGINT(20) NOT NULL,
  `idjek` VARCHAR(100) NOT NULL,
  `nkod` VARCHAR(100),
  `system_object_id` BIGINT(20),
  `processed` TINYINT(1) NOT NULL default 0,
  `content` VARCHAR(1000) NOT NULL,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `owner_type_correction_id` (`idjek`, `id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Person corrections --
DROP TABLE IF EXISTS `person_correction`;

CREATE TABLE `person_correction` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `id` BIGINT(20) NOT NULL,
  `idbud` VARCHAR(100) COMMENT 'Ссылка на дом',
  `rah` VARCHAR(100) COMMENT 'Счет',
  `kv` VARCHAR(100) COMMENT 'Квартира',
  `fam` VARCHAR(100) COMMENT 'Фамилия',
  `im` VARCHAR(100) COMMENT 'Имя',
  `ot` VARCHAR(100) COMMENT 'Отчество',
  `datar` VARCHAR(100) COMMENT 'Дата рождения',
  `reltovlaskv` VARCHAR(100) COMMENT 'Отношение к владельцу квартиры',
  `grajd` VARCHAR(100) COMMENT 'Гражданство',
  `pol` VARCHAR(100) COMMENT 'Пол',
  `idrel` VARCHAR(100) COMMENT 'Ссылка на owner_relationship_correction',
  `nkra` VARCHAR(100) COMMENT 'Страна рождения',
  `nobl` VARCHAR(100) COMMENT 'Область рождения',
  `nrayon` VARCHAR(100) COMMENT 'Район рождения',
  `nmisto` VARCHAR(100) COMMENT 'Город рождения',
  `iddok` VARCHAR(100) COMMENT 'Ссылка на тип документа',
  `dokseria` VARCHAR(100) COMMENT 'Серия документа',
  `doknom` VARCHAR(100) COMMENT 'Номер документа',
  `dokvidan` VARCHAR(100) COMMENT 'Орган, выдавший документ',
  `dokdatvid` VARCHAR(100) COMMENT 'Дата выдачи документа',
  `idarm` VARCHAR(100) COMMENT 'Ссылка на military_duty_correction',
  `pkra` VARCHAR(100) COMMENT 'Страна прибытия',
  `pobl` VARCHAR(100) COMMENT 'Область прибытия',
  `prayon` VARCHAR(100) COMMENT 'Район прибытия',
  `pmisto` VARCHAR(100) COMMENT 'Город прибытия',
  `pdpribza` VARCHAR(100) COMMENT 'Дата прибытия из-за пределов города',
  `pidul` VARCHAR(100) COMMENT 'Ссылка на улицу прибытия',
  `pbud` VARCHAR(100) COMMENT 'Номер дома прибытия',
  `pkorp` VARCHAR(100) COMMENT 'Корпус дома прибытия',
  `pkv` VARCHAR(100) COMMENT 'Квартира прибытия',
  `pdpribvm` VARCHAR(100) COMMENT 'Дата прибытия в пределах города',
  `dprop` VARCHAR(100) COMMENT 'Дата прописки',
  `idvidp` VARCHAR(100) COMMENT 'Ссылка на вид прописки',
  `vkra` VARCHAR(100) COMMENT 'Страна выбытия',
  `vobl` VARCHAR(100) COMMENT 'Область выбытия',
  `vrayon` VARCHAR(100) COMMENT 'Район выбытия',
  `vmisto` VARCHAR(100) COMMENT 'Город выбытия',
  `vidul` VARCHAR(100) COMMENT 'Ссылка на улицу выбытия',
  `vbud` VARCHAR(100) COMMENT 'Номер дома выбытия',
  `vkorp` VARCHAR(100) COMMENT 'Корпус дома выбытия',
  `vkv` VARCHAR(100) COMMENT 'Квартира выбытия',
  `vdata` VARCHAR(100) COMMENT 'Дата выбытия',
  `idvip` VARCHAR(100) COMMENT 'Ссылка на причину выписки',
  `larc` VARCHAR(100) COMMENT 'Признак архивной записи',
  `nom` VARCHAR(100) COMMENT 'Персональный номер персоны в квартире',
  `parentnom` VARCHAR(100) COMMENT 'Персональный номер родителя персоны в квартире',
  `kid` TINYINT(1) NOT NULL default 0,
  `system_person_id` BIGINT(20),
  `system_registration_id` BIGINT(20),
  `processed` TINYINT(1) NOT NULL default 0,
  `content` VARCHAR(1000) NOT NULL,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `person_correction_id` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Apartment card corrections --
DROP TABLE IF EXISTS `apartment_card_correction`;

CREATE TABLE `apartment_card_correction` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `id` BIGINT(20) NOT NULL,
  `idbud` VARCHAR(100) COMMENT 'Ссылка на дом',
  `rah` VARCHAR(100) COMMENT 'Счет',
  `kv` VARCHAR(100) COMMENT 'Квартира',
  `fio` VARCHAR(100) COMMENT 'ФИО',
  `idprivat` VARCHAR(100) COMMENT 'Ссылка на форму собственности',
  `larc` VARCHAR(100) COMMENT 'Признак архивной записи',
  `system_apartment_card_id` BIGINT(20),
  `processed` TINYINT(1) NOT NULL default 0,
  `content` VARCHAR(1000) NOT NULL,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `apartment_card_correction_id` (`id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Report
DROP TABLE IF EXISTS `report`;

CREATE TABLE `report`(
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `markup` TEXT NOT NULL,
  `updated` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY(id),
  KEY `key_name` (`name`),
  KEY `key_updated` (`updated`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Report SQL
DROP TABLE IF EXISTS `report_sql`;

CREATE TABLE `report_sql`(
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `report_id` BIGINT(20) NOT NULL,
  `sql` TEXT NOT NULL,
  PRIMARY KEY(id),
  KEY `key_report_id` (`report_id`),
  CONSTRAINT `fk_report_sql__report` FOREIGN KEY (`report_id`) REFERENCES `report` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci;


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;