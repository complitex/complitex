/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;

-- ------------------------------
-- Corrections
-- ------------------------------

DROP TABLE IF EXISTS `country_correction`;
CREATE TABLE `country_correction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта страны',
  `external_id` BIGINT(20) COMMENT 'Внешний идентификатор объекта',
  `correction` VARCHAR(250) NOT NULL COMMENT 'Название страны',
  `start_date` DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия',
  `end_date` DATETIME COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор организации',
  `user_organization_id` BIGINT(20),
  `module_id` BIGINT(20) COMMENT 'Идентификатор внутренней организации',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_external_id` (external_id, organization_id, user_organization_id),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  CONSTRAINT `fk_country_correction__country` FOREIGN KEY (`object_id`) REFERENCES `country` (`object_id`),
  CONSTRAINT `fk_country_correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_country_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Коррекция страны';

DROP TABLE IF EXISTS `region_correction`;
CREATE TABLE `region_correction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор страны',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта региона',
  `external_id` BIGINT(20) COMMENT 'Внешний идентификатор объекта',
  `correction` VARCHAR(250) NOT NULL COMMENT 'Название страны',
  `start_date` DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия',
  `end_date` DATETIME COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор организации',
  `user_organization_id` BIGINT(20),
  `module_id` BIGINT(20) COMMENT 'Идентификатор внутренней организации',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_external_id` (external_id, organization_id, user_organization_id),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  CONSTRAINT `fk_region_correction__country` FOREIGN KEY (`parent_id`) REFERENCES `country` (`object_id`),
  CONSTRAINT `fk_region_correction__region` FOREIGN KEY (`object_id`) REFERENCES `region` (`object_id`),
  CONSTRAINT `fk_region_correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_region_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Коррекция региона';

DROP TABLE IF EXISTS `city_type_correction`;
CREATE TABLE `city_type_correction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта типа населенного пункта',
  `external_id` BIGINT(20) COMMENT 'Внешний идентификатор объекта',
  `correction` VARCHAR(250) NOT NULL COMMENT 'Название типа населенного пункта',
  `start_date` DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия',
  `end_date` DATETIME COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор организации',
  `user_organization_id` BIGINT(20),
  `module_id` BIGINT(20) COMMENT 'Идентификатор внутренней организации',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_external_id` (external_id, organization_id, user_organization_id),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  CONSTRAINT `fk_city_type__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_city_type_correction__city_type` FOREIGN KEY (`object_id`) REFERENCES `city_type` (`object_id`),
  CONSTRAINT `fk_city_type_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Коррекция типа населенного пункта';


DROP TABLE IF EXISTS `city_correction`;
CREATE TABLE `city_correction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор региона',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта населенного пункта',
  `external_id` BIGINT(20) COMMENT 'Внешний идентификатор объекта',
  `correction` VARCHAR(250) NOT NULL COMMENT 'Название населенного пункта',
  `start_date` DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия',
  `end_date` DATETIME COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор организации',
  `user_organization_id` BIGINT(20),
  `module_id` BIGINT(20) COMMENT 'Идентификатор внутренней организации',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_external_id` (external_id, organization_id, user_organization_id),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  CONSTRAINT `fk_city_correction__region` FOREIGN KEY (`parent_id`) REFERENCES `region` (`object_id`),
  CONSTRAINT `fk_city_correction__city` FOREIGN KEY (`object_id`) REFERENCES `city` (`object_id`),
  CONSTRAINT `fk_city_correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_city_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Коррекция населенного пункта';

DROP TABLE IF EXISTS `district_correction`;

CREATE TABLE `district_correction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор объекта населенного пункта',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта района',
  `external_id` BIGINT(20) COMMENT 'Внешний идентификатор объекта',
  `correction` VARCHAR(250) NOT NULL COMMENT 'Название типа населенного пункта',
  `start_date` DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия',
  `end_date` DATETIME COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор организации',
  `user_organization_id` BIGINT(20),
  `module_id` BIGINT(20) COMMENT 'Идентификатор внутренней организации',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_external_id` (external_id, organization_id, user_organization_id),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  CONSTRAINT `fk_district_correction__city` FOREIGN KEY (`parent_id`) REFERENCES `city` (`object_id`),
  CONSTRAINT `fk_district_correction__district` FOREIGN KEY (`object_id`) REFERENCES `district` (`object_id`),
  CONSTRAINT `fk_district_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_district_correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Коррекция района';

DROP TABLE IF EXISTS `street_type_correction`;
CREATE TABLE `street_type_correction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта типа населенного пункта',
  `external_id` BIGINT(20) COMMENT 'Внешний идентификатор объекта',
  `correction` VARCHAR(250) NOT NULL COMMENT 'Название типа населенного пункта',
  `start_date` DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия',
  `end_date` DATETIME COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор организации',
  `user_organization_id` BIGINT(20),
  `module_id` BIGINT(20) COMMENT 'Идентификатор внутренней организации',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_external_id` (external_id, organization_id, user_organization_id),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  CONSTRAINT `fk_street_type__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_street_type_correction__street_type` FOREIGN KEY (`object_id`) REFERENCES `street_type` (`object_id`),
  CONSTRAINT `fk_street_type_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Коррекция типа улицы';


DROP TABLE IF EXISTS `street_correction`;

CREATE TABLE `street_correction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор объекта населенного пункта',
  `additional_parent_id` BIGINT(20) COMMENT 'Идентификатор объекта типа улицы',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта улицы',
  `external_id` BIGINT(20) COMMENT 'Внешний идентификатор объекта',
  `correction` VARCHAR(250) NOT NULL COMMENT 'Название типа населенного пункта',
  `start_date` DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия',
  `end_date` DATETIME COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор организации',
  `user_organization_id` BIGINT(20),
  `module_id` BIGINT(20) COMMENT 'Идентификатор внутренней организации',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_external_id` (external_id, organization_id, user_organization_id),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_street_type_id` (`additional_parent_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  CONSTRAINT `fk_street_correction__city` FOREIGN KEY (`parent_id`) REFERENCES `city` (`object_id`),
  CONSTRAINT `fk_street_correction__street_type` FOREIGN KEY (`additional_parent_id`) REFERENCES `street_type` (`object_id`),
  CONSTRAINT `fk_street_correction__street` FOREIGN KEY (`object_id`) REFERENCES `street` (`object_id`),
  CONSTRAINT `fk_street_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_street_correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Коррекция улицы';

DROP TABLE IF EXISTS `building_correction`;
CREATE TABLE `building_correction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор объекта улица',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта дом',
  `external_id` BIGINT(20) COMMENT 'Внешний идентификатор объекта',
  `correction` VARCHAR(250) NOT NULL COMMENT 'Номер дома',
  `additional_correction` VARCHAR(20) NOT NULL DEFAULT '' COMMENT 'Корпус дома',
  `start_date` DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия',
  `end_date` DATETIME COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор организации',
  `user_organization_id` BIGINT(20),
  `module_id` BIGINT(20) COMMENT 'Идентификатор внутренней организации',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_external_id` (external_id, organization_id, user_organization_id),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  CONSTRAINT `fk_building_correction__street` FOREIGN KEY (`parent_id`) REFERENCES `street` (`object_id`),
  CONSTRAINT `fk_building_correction__building` FOREIGN KEY (`object_id`) REFERENCES `building` (`object_id`),
  CONSTRAINT `fk_building_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_building_correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Коррекция дома';

-- ------------------------------
-- Organization Correction
-- ------------------------------

DROP TABLE IF EXISTS `organization_correction`;

CREATE TABLE `organization_correction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор родительской организации',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта организация',
  `external_id` BIGINT(20) COMMENT 'Внешний идентификатор организации',
  `correction` VARCHAR(250) NOT NULL COMMENT 'Код организации',
  `start_date` DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия',
  `end_date` DATETIME COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор организации',
  `user_organization_id` BIGINT(20),
  `module_id` BIGINT(20) COMMENT 'Идентификатор внутренней организации',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_external_id` (external_id, organization_id, user_organization_id),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  CONSTRAINT `fk_organization_correction__organization_p` FOREIGN KEY (`parent_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_organization_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_organization_correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Коррекция организации';


-- ------------------------------
-- Service Correction
-- ------------------------------

DROP TABLE IF EXISTS `service_correction`;

CREATE TABLE `service_correction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта услуги',
  `external_id` BIGINT(20) COMMENT 'Внешний идентификатор услуги',
  `correction` VARCHAR(250) NOT NULL COMMENT 'Код услуги',
  `start_date` DATETIME NOT NULL DEFAULT NOW() COMMENT 'Дата начала актуальности соответствия',
  `end_date` DATETIME COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор услуги',
  `user_organization_id` BIGINT(20),
  `module_id` BIGINT(20) COMMENT 'Идентификатор внутренней организации',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_external_id` (external_id, organization_id, user_organization_id),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  CONSTRAINT `fk_service_correction__service_object` FOREIGN KEY (`object_id`) REFERENCES `service` (`object_id`),
  CONSTRAINT `fk_service_correction__service` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_service_correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Коррекция услуги';

/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
