CREATE TABLE `country_correction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта страны',
  `external_id` BIGINT(20) COMMENT 'Внешний идентификатор объекта',
  `correction` VARCHAR(250) NOT NULL COMMENT 'Название страны',
  `begin_date` DATE NOT NULL DEFAULT '1970-01-01' COMMENT 'Дата начала актуальности соответствия',
  `end_date` DATE NOT NULL DEFAULT '2054-12-31' COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор организации',
  `user_organization_id` BIGINT(20),
  `module_id` BIGINT(20) COMMENT 'Идентификатор модуля',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_external_id` (external_id, organization_id, user_organization_id),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_begin_date` (`begin_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  KEY `key_module_id` (`module_id`),
  CONSTRAINT `fk_country_correction__country` FOREIGN KEY (`object_id`) REFERENCES `country` (`object_id`),
  CONSTRAINT `fk_country_correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_country_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Коррекция страны';

CREATE TABLE `region_correction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор страны',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта региона',
  `external_id` BIGINT(20) COMMENT 'Внешний идентификатор объекта',
  `correction` VARCHAR(250) NOT NULL COMMENT 'Название страны',
  `begin_date` DATE NOT NULL DEFAULT '1970-01-01' COMMENT 'Дата начала актуальности соответствия',
  `end_date` DATE NOT NULL DEFAULT '2054-12-31' COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор организации',
  `user_organization_id` BIGINT(20),
  `module_id` BIGINT(20) COMMENT 'Идентификатор модуля',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_external_id` (external_id, organization_id, user_organization_id),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_begin_date` (`begin_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  KEY `key_module_id` (`module_id`),
  CONSTRAINT `fk_region_correction__country` FOREIGN KEY (`parent_id`) REFERENCES `country` (`object_id`),
  CONSTRAINT `fk_region_correction__region` FOREIGN KEY (`object_id`) REFERENCES `region` (`object_id`),
  CONSTRAINT `fk_region_correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_region_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT 'Коррекция региона';

alter table city_correction add column parent_id BIGINT(20) COMMENT 'Идентификатор региона';
alter table city_correction add key key_parent_id (parent_id);
alter table city_correction add constraint fk_city_correction__region foreign key (parent_id) references region (object_id);

alter table district_correction change column city_id parent_id bigint(20) comment 'Идентификатор объекта населенного пункта';

alter table street_correction change column city_id parent_id bigint(20) comment 'Идентификатор объекта населенного пункта';
alter table street_correction change column street_type_id additional_parent_id  bigint(20) comment 'Идентификатор объекта типа улицы';

alter table building_correction change column street_id parent_id bigint(20) comment 'Идентификатор объекта улица';
alter table building_correction change column correction_corp additional_correction varchar(20)  COMMENT 'Корпус дома';

alter table organization_correction add column parent_id bigint(20) comment 'идентификатор родительской организации';
alter table organization_correction add key key_parent_id (parent_id);
alter table organization_correction add constraint fk_organization_correction__organization_p
  foreign key (parent_id) references organization (object_id);

insert into `update` (`version`) VALUE ('20180120_0.7.3');