/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;

DROP TABLE IF EXISTS `sequence`;
CREATE TABLE `sequence`(
   `sequence_name` VARCHAR(100) NOT NULL COMMENT 'Название таблицы сущности',
   `sequence_value` bigint UNSIGNED NOT NULL DEFAULT '0' COMMENT 'Значение идентификатора',
   PRIMARY KEY (`sequence_name`)
 )ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Последовательность генерации идентификаторов объектов';

DROP TABLE IF EXISTS `entity_string_value`;

CREATE TABLE `entity_string_value` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`, `locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Локализация';

DROP TABLE IF EXISTS `entity`;

CREATE TABLE `entity` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор сущности',
  `entity` VARCHAR(100) NOT NULL COMMENT 'Название сущности',
  `name_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локализации названия сущности',
  `strategy_factory` VARCHAR(100) NOT NULL COMMENT 'Не используется',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_entity` (entity),
  KEY `key_name_id` (`name_id`),
  CONSTRAINT `fk_entity__entity_string_value` FOREIGN KEY (name_id) REFERENCES entity_string_value (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Сущность';

DROP TABLE IF EXISTS `entity_attribute`;

CREATE TABLE `entity_attribute` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор типа атрибута',
  `entity_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор сущности',
  `name_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локализации названия атрибута',
  `start_date` TIMESTAMP NOT NULL default CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия типа атрибута',
  `end_date` TIMESTAMP NULL default NULL COMMENT 'Дата окончания периода действия типа атрибута',
  `value_type_id` BIGINT(20) COMMENT  'Тип значения атрибута',
  `reference_id` BIGINT(20) COMMENT  'Внешний ключ',
  `system` TINYINT(1) default 0 NOT NULL COMMENT 'Является ли тип атрибута системным',
  `required` TINYINT(1) default 0 NOT NULL COMMENT 'Является ли атрибут обязательным',
  PRIMARY KEY (`id`),
  KEY `key_entity_id` (`entity_id`),
  KEY `key_name_id` (`name_id`),
  CONSTRAINT `fk_attribute_type__entity` FOREIGN KEY (`entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_entity_attribute__entity_string_value` FOREIGN KEY (`name_id`) REFERENCES entity_string_value (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Тип атрибута сущности';


DROP TABLE IF EXISTS entity_value_type;

CREATE TABLE `entity_value_type` (
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа значения атрибута',
  `value_type` VARCHAR(100) NOT NULL COMMENT 'Тип значения атрибута',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Тип значения атрибута';

/* Entities */

-- ------------------------------
-- Organization type
-- ------------------------------
DROP TABLE IF EXISTS `organization_type`;

CREATE TABLE `organization_type` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта: не используется',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта: не используется',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_organization_type__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_organization_type__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Тип организации';

DROP TABLE IF EXISTS `organization_type_attribute`;

CREATE TABLE `organization_type_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 2300 - НАИМЕНОВАНИЕ ТИПА ОРГАНИЗАЦИИ',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`entity_attribute_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_organization_type_attribute__organization` FOREIGN KEY (`object_id`) REFERENCES `organization_type`(`object_id`),
  CONSTRAINT `fk_organization_type_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`) REFERENCES entity_attribute (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Атрибуты типа организации';

DROP TABLE IF EXISTS `organization_type_string_value`;

CREATE TABLE `organization_type_string_value` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_organization_type_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Локализация атрибутов типа организации';

-- ------------------------------
-- Organization
-- ------------------------------
DROP TABLE IF EXISTS `organization`;

CREATE TABLE `organization` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта: не используется',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта: не используется',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_organization__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_organization__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Организация';

DROP TABLE IF EXISTS `organization_attribute`;

CREATE TABLE `organization_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 900 - НАЗВАНИЕ, 901 - УНИКАЛЬНЫЙ КОД, 902 - РАЙОН, 903 - ПРИНАДЛЕЖИТ, 904  - ТИП ОРГАНИЗАЦИИ',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`entity_attribute_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_organization_attribute__organization` FOREIGN KEY (`object_id`) REFERENCES `organization`(`object_id`),
  CONSTRAINT `fk_organization_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`) REFERENCES entity_attribute (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Атрибуты организации';

DROP TABLE IF EXISTS `organization_string_value`;

CREATE TABLE `organization_string_value` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_organization_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Локализация атрибутов организации';

-- ------------------------------
-- Country
-- ------------------------------
DROP TABLE IF EXISTS `country`;

CREATE TABLE `country` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта: не используется',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта: не используется',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_country__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_country__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Страна';

DROP TABLE IF EXISTS `country_attribute`;

CREATE TABLE `country_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 800 - НАИМЕНОВАНИЕ СТРАНЫ',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`entity_attribute_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_country_attribute__country` FOREIGN KEY (`object_id`) REFERENCES `country`(`object_id`),
  CONSTRAINT `fk_country_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`) REFERENCES entity_attribute (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Атрибуты страны';

DROP TABLE IF EXISTS `country_string_value`;

CREATE TABLE `country_string_value` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_country_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Локализация атрибутов страны';

-- ------------------------------
-- Region
-- ------------------------------
DROP TABLE IF EXISTS `region`;

CREATE TABLE `region` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта: 800 - country',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_region__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_region__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Регион';

DROP TABLE IF EXISTS `region_attribute`;

CREATE TABLE `region_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 700 - НАИМЕНОВАНИЕ РЕГИОНА',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`entity_attribute_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_region_attribute__region` FOREIGN KEY (`object_id`) REFERENCES `region`(`object_id`),
  CONSTRAINT `fk_region_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`) REFERENCES entity_attribute (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Атрибуты региона';

DROP TABLE IF EXISTS `region_string_value`;

CREATE TABLE `region_string_value` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_region_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Локализация атрибутов региона';

-- ------------------------------
-- City Type
-- ------------------------------

DROP TABLE IF EXISTS `city_type`;

CREATE TABLE `city_type` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта: не используется',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта: не используется',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  KEY `key_object_id` (object_id),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_city_type__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_city_type__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Тип населенного пункта';

DROP TABLE IF EXISTS `city_type_attribute`;

CREATE TABLE `city_type_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 1300 - КРАТКОЕ НАЗВАНИЕ, 1301 - НАЗВАНИЕ',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`entity_attribute_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_city_type_attribute__city_type` FOREIGN KEY (`object_id`) REFERENCES `city_type`(`object_id`),
  CONSTRAINT `fk_city_type_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`)
  REFERENCES entity_attribute (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Атрибуты типа населенного пункта';

DROP TABLE IF EXISTS `city_type_string_value`;

CREATE TABLE `city_type_string_value` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_city_type_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Локализация атрибутов типа населенного пункта';

-- ------------------------------
-- City
-- ------------------------------
DROP TABLE IF EXISTS `city`;

CREATE TABLE `city` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта: 700 - region',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `ft_city__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_city__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Населенный пункт';

DROP TABLE IF EXISTS `city_attribute`;

CREATE TABLE `city_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 400 - НАИМЕНОВАНИЕ НАСЕЛЕННОГО ПУНКТА, 401 - ТИП НАСЕЛЕННОГО ПУНКТА',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`entity_attribute_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_city_attribute__city` FOREIGN KEY (`object_id`) REFERENCES `city`(`object_id`),
  CONSTRAINT `fk_city_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`) REFERENCES entity_attribute (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Атрибуты населенного пункта';

DROP TABLE IF EXISTS `city_string_value`;

CREATE TABLE `city_string_value` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_city_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Локализация атрибутов населенного пункта';

-- ------------------------------
-- District --
-- ------------------------------
DROP TABLE IF EXISTS `district`;

CREATE TABLE `district` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта: 400 - city',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_district__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_district__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Район';

DROP TABLE IF EXISTS `district_attribute`;

CREATE TABLE `district_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 600 - НАИМЕНОВАНИЕ РАЙОНА, 601 - КОД РАЙОНА',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`entity_attribute_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_district_attribute__district` FOREIGN KEY (`object_id`) REFERENCES `district`(`object_id`),
  CONSTRAINT `fk_district_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`) REFERENCES entity_attribute (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Атрибуты района';

DROP TABLE IF EXISTS `district_string_value`;

CREATE TABLE `district_string_value` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_district_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Локализация атрибутов района';

-- ------------------------------
-- Street Type
-- ------------------------------

DROP TABLE IF EXISTS `street_type`;

CREATE TABLE `street_type` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта: не используется',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта: не используется',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  KEY `key_object_id` (object_id),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_street_type__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_street_type__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Тип улицы';

DROP TABLE IF EXISTS `street_type_attribute`;

CREATE TABLE `street_type_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 1400 - КРАТКОЕ НАЗВАНИЕ, 1401 - НАЗВАНИЕ',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`entity_attribute_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_street_type_attribute__street_type` FOREIGN KEY (`object_id`) REFERENCES `street_type`(`object_id`),
  CONSTRAINT `fk_street_type_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`) REFERENCES entity_attribute (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Атрибуты типа улицы';

DROP TABLE IF EXISTS `street_type_string_value`;
CREATE TABLE `street_type_string_value` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_street_type_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Локализация атрибутов типа улицы';

-- ------------------------------
-- Street
-- ------------------------------
DROP TABLE IF EXISTS `street`;

CREATE TABLE `street` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта: 400 - city',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_street__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_street__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Улица';

DROP TABLE IF EXISTS `street_attribute`;

CREATE TABLE `street_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 300 - НАИМЕНОВАНИЕ УЛИЦЫ, 301 - ТИП УЛИЦЫ',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`entity_attribute_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_street_attribute__street` FOREIGN KEY (`object_id`) REFERENCES `street`(`object_id`),
  CONSTRAINT `fk_street_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`) REFERENCES entity_attribute (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Атрибуты улицы';

DROP TABLE IF EXISTS `street_string_value`;
CREATE TABLE `street_string_value` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_street_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Локализация атрибутов улицы';

-- ------------------------------
-- Building
-- ------------------------------
DROP TABLE IF EXISTS `building`;

CREATE TABLE `building` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_building__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_building__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Дом';

DROP TABLE IF EXISTS `building_attribute`;

CREATE TABLE `building_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`entity_attribute_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_building_attribute__building` FOREIGN KEY (`object_id`) REFERENCES `building`(`object_id`),
  CONSTRAINT `fk_building_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`) REFERENCES entity_attribute (`id`)
  ) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Атрибуты дома';

DROP TABLE IF EXISTS `building_string_value`;

CREATE TABLE `building_string_value` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_building_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Локализация атрибутов дома ';

-- ------------------------------
-- Apartment
-- ------------------------------
DROP TABLE IF EXISTS `apartment`;

CREATE TABLE `apartment` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта: 500 - building',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  KEY `key_object_id` (object_id),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_apartment__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_apartment__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Квартира';

DROP TABLE IF EXISTS `apartment_attribute`;

CREATE TABLE `apartment_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 100 - НАИМЕНОВАНИЕ КВАРТИРЫ',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`entity_attribute_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_apartment_attribute__apartment` FOREIGN KEY (`object_id`) REFERENCES `apartment`(`object_id`),
  CONSTRAINT `fk_apartment_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`) REFERENCES entity_attribute (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Атрибуты квартиры';

DROP TABLE IF EXISTS `apartment_string_value`;

CREATE TABLE `apartment_string_value` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_apartment_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Локализация атрибутов квартиры';

-- ------------------------------
-- Room --
-- ------------------------------
DROP TABLE IF EXISTS `room`;

CREATE TABLE `room` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта: 100 - building',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Начало действия значений параметров объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_room__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_room__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Комната';

DROP TABLE IF EXISTS `room_attribute`;

CREATE TABLE `room_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 200 - НАИМЕНОВАНИЕ КОМНАТЫ',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`entity_attribute_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_room_attribute__room` FOREIGN KEY (`object_id`) REFERENCES `room`(`object_id`),
  CONSTRAINT `fk_room_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`) REFERENCES entity_attribute (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Атрибуты комнаты';

DROP TABLE IF EXISTS `room_string_value`;

CREATE TABLE `room_string_value` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_room_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Локализация атрибутов комнаты';

-- ------------------------------
-- User info
-- ------------------------------
DROP TABLE IF EXISTS `user_info`;

CREATE TABLE `user_info` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительского объекта: не используется',
  `parent_entity_id` BIGINT(20) COMMENT 'Идентификатор сущности родительского объекта: не используется',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_user_info__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_user_info__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Информация о пользователе';

DROP TABLE IF EXISTS `user_info_attribute`;

CREATE TABLE `user_info_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 1000 - ФАМИЛИЯ, 1001 - ИМЯ, 1002 - ОТЧЕСТВО',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`entity_attribute_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_user_info_attribute__user_info` FOREIGN KEY (`object_id`) REFERENCES `user_info`(`object_id`),
  CONSTRAINT `fk_user_info_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`) REFERENCES entity_attribute (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Атрибуты информации о пользователе';

DROP TABLE IF EXISTS `user_info_string_value`;

CREATE TABLE `user_info_string_value` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_user_info_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Локализация атрибутов информации о пользователе';


-- ------------------------------
-- User Organization
-- ------------------------------
DROP TABLE IF EXISTS `user_organization`;

CREATE TABLE `user_organization` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор организации пользователей',
    `user_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор пользователя',
    `organization_object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор организации',
    `main` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Является ли организация основной',
    PRIMARY KEY (`id`),
    UNIQUE KEY `key_unique` (`user_id`, `organization_object_id`),
    CONSTRAINT `fk_user_organization__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_user_organization__organization` FOREIGN KEY (`organization_object_id`)
      REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Организация пользователей';

-- ------------------------------
-- Permission
-- ------------------------------

DROP TABLE IF EXISTS `permission`;

CREATE TABLE `permission` (
    `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT  COMMENT 'Суррогатный ключ',
    `permission_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор права доступа',
    `table` VARCHAR(64) NOT NULL COMMENT 'Таблица',
    `entity` VARCHAR(64) NOT NULL COMMENT 'Сущность',
    `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
    PRIMARY KEY (`pk_id`),
    UNIQUE KEY `key_unique` (`permission_id`, `entity`, `object_id`),
    KEY `key_permission_id` (`permission_id`),
    KEY `key_table` (`table`),
    KEY `key_entity` (`entity`),
    KEY `key_object_id` (`object_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Права доступа';

-- ------------------------------
-- First Name
-- ------------------------------

DROP TABLE IF EXISTS `first_name`;

CREATE TABLE `first_name` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор имени',
  `name` VARCHAR(100) NOT NULL COMMENT 'Имя',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_name` (`name`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Имя';

-- ------------------------------
-- Middle Name
-- ------------------------------

DROP TABLE IF EXISTS `middle_name`;

CREATE TABLE `middle_name` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор отчества',
  `name` VARCHAR(100) NOT NULL COMMENT 'Отчество',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_name` (`name`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Отчество';

-- ------------------------------
-- Last Name
-- ------------------------------

DROP TABLE IF EXISTS `last_name`;

CREATE TABLE `last_name` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор фамилии',
  `name` VARCHAR(100) NOT NULL COMMENT 'Фамилия',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_name` (`name`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Фамилия';

-- ------------------------------
-- Building Code
-- ------------------------------

DROP TABLE IF EXISTS `building_code`;
CREATE TABLE `building_code` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `organization_id` BIGINT(20) NOT NULL COMMENT 'ID обслуживающей организации',
  `code` BIGINT(20) NOT NULL COMMENT 'Код дома для данной обслуживающей организации',
  `building_id` BIGINT(20) NOT NULL COMMENT 'ID дома',
  PRIMARY KEY (`id`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_building_id` (`building_id`),
  CONSTRAINT `fk_building_code__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_building_code__building` FOREIGN KEY (`building_id`) REFERENCES `building` (`object_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Код дома';

-- ------------------------------
--  Organization Import
-- ------------------------------

DROP TABLE IF EXISTS `organization_import`;
CREATE TABLE `organization_import` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `organization_id` BIGINT(20) NOT NULL COMMENT 'ID организации',
  `code` VARCHAR(100) NOT NULL COMMENT 'Код организации',
  `short_name` VARCHAR(100) NOT NULL COMMENT 'Короткое название организации',
  `full_name` VARCHAR(500) NOT NULL COMMENT 'Полное название организации',
  `hlevel` BIGINT(20) COMMENT 'Ссылка на вышестоящую организацию',
  PRIMARY KEY (`pk_id`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_hlevel` (`hlevel`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Вспомогательная таблица для импорта организаций';

-- ------------------------------
--  Domain Sync
-- ------------------------------

DROP TABLE IF EXISTS `domain_sync`;
CREATE TABLE `domain_sync`(
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор записи синхронизации',
  `parent_id` BIGINT(20) COMMENT 'Внешний идентификатор родительского объекта',
  `additional_parent_id` VARCHAR(16) COMMENT 'Внешний идентификатор дополнительного родительского объекта',
  `external_id` BIGINT(20) NOT NULL COMMENT 'Внешний идентификатор',
  `additional_external_id` VARCHAR(20) COMMENT 'Дополнительный внешний идентификатор',
  `name` VARCHAR(250) NOT NULL COMMENT 'Название',
  `additional_name` VARCHAR(50) COMMENT 'Дополнительное название',
  `alt_name` VARCHAR(250) COMMENT 'Украинское название',
  `alt_additional_name` VARCHAR(50) COMMENT 'Украинское дополнительное название',
  `servicing_organization` BIGINT(20) COMMENT 'Обслуживающая организация',
  `balance_holder` BIGINT(20) COMMENT 'Балансодержатель',
  `type` INTEGER NOT NULL COMMENT 'Тип синхронизации',
  `status` INTEGER NOT NULL COMMENT 'Статус синхронизации',
  `status_detail` INTEGER COMMENT 'Дополнительный статус синхронизации',
  `date` DATETIME NOT NULL COMMENT 'Дата актуальности',
  PRIMARY KEY (`id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_additional_parent_id` (`additional_parent_id`),
  KEY `key_external_id` (`external_id`),
  KEY `key_additional_external_id` (`additional_external_id`),
  KEY `key_name` (`name`),
  KEY `key_additional_name` (`additional_name`),
  KEY `key_servicing_organization` (`servicing_organization`),
  KEY `key_balance_holder` (`balance_holder`),
  KEY `key_type` (`type`),
  KEY `key_status` (`status`),
  KEY `key_date` (`date`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Синхронизация справочников';

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
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_service__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_service__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Услуга';

DROP TABLE IF EXISTS `service_attribute`;
CREATE TABLE `service_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута. Возможные значения: todo',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия параметров атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия параметров атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус: INACTIVE(0), ACTIVE(1), ARCHIVE(2)',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`entity_attribute_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_service_attribute__service` FOREIGN KEY (`object_id`) REFERENCES `service`(`object_id`),
  CONSTRAINT `fk_service_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`) REFERENCES entity_attribute (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Атрибуты услуги';

DROP TABLE IF EXISTS `service_string_value`;
CREATE TABLE `service_string_value` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор значения',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  CONSTRAINT `fk_service_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Локализированное значение атрибута услуги';

/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
