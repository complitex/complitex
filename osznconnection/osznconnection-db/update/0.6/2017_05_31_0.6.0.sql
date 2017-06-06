INSERT INTO `update` (`version`) VALUE ('20170206_0.6.0');

ALTER TABLE `locales` RENAME `locale`;

ALTER TABLE `string_culture` RENAME `string_value`;
ALTER TABLE `string_value` DROP FOREIGN KEY `fk_string_culture__locales`;
ALTER TABLE `string_value` ADD CONSTRAINT `fk_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`);

ALTER TABLE `entity` DROP FOREIGN KEY `fk_entity__string_culture`;
ALTER TABLE `entity` ADD CONSTRAINT `fk_entity__string_value` FOREIGN KEY (`entity_name_id`) REFERENCES `string_value` (`id`);

ALTER TABLE `attribute_type` DROP FOREIGN KEY `fk_attribute_type__string_culture`;
ALTER TABLE `attribute_type` ADD CONSTRAINT `fk_attribute_type__string_value` FOREIGN KEY (`attribute_type_name_id`) REFERENCES `string_value` (`id`);

ALTER TABLE `organization_type_string_culture` RENAME `organization_type_string_value`;
ALTER TABLE `organization_type_string_value` DROP FOREIGN KEY `fk_organization_type_string_culture__locales`;
ALTER TABLE `organization_type_string_value` ADD CONSTRAINT `fk_organization_type_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`);

ALTER TABLE `organization_string_culture` RENAME `organization_string_value`;
ALTER TABLE `organization_string_value` DROP FOREIGN KEY `fk_organization_string_culture__locales`;
ALTER TABLE `organization_string_value` ADD CONSTRAINT `fk_organization_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`);

ALTER TABLE `country_string_culture` RENAME `country_string_value`;
ALTER TABLE `country_string_value` DROP FOREIGN KEY `fk_country_string_culture__locales`;
ALTER TABLE `country_string_value` ADD CONSTRAINT `fk_country_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`);

ALTER TABLE `region_string_culture` RENAME `region_string_value`;
ALTER TABLE `region_string_value` DROP FOREIGN KEY `fk_region_string_culture__locales`;
ALTER TABLE `region_string_value` ADD CONSTRAINT `fk_region_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`);

ALTER TABLE `city_type_string_culture` RENAME `city_type_string_value`;
ALTER TABLE `city_type_string_value` DROP FOREIGN KEY `fk_city_type_string_culture__locales`;
ALTER TABLE `city_type_string_value` ADD CONSTRAINT `fk_city_type_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`);

ALTER TABLE `city_string_culture` RENAME `city_string_value`;
ALTER TABLE `city_string_value` DROP FOREIGN KEY `fk_city_string_culture__locales`;
ALTER TABLE `city_string_value` ADD CONSTRAINT `fk_city_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`);

ALTER TABLE `district_string_culture` RENAME `district_string_value`;
ALTER TABLE `district_string_value` DROP FOREIGN KEY `fk_district_string_culture__locales`;
ALTER TABLE `district_string_value` ADD CONSTRAINT `fk_district_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`);

ALTER TABLE `street_type_string_culture` RENAME `street_type_string_value`;
ALTER TABLE `street_type_string_value` DROP FOREIGN KEY `fk_street_type_string_culture__locales`;
ALTER TABLE `street_type_string_value` ADD CONSTRAINT `fk_street_type_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`);

ALTER TABLE `street_string_culture` RENAME `street_string_value`;
ALTER TABLE `street_string_value` DROP FOREIGN KEY `fk_street_string_culture__locales`;
ALTER TABLE `street_string_value` ADD CONSTRAINT `fk_street_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`);

ALTER TABLE `building_string_culture` RENAME `building_string_value`;
ALTER TABLE `building_string_value` DROP FOREIGN KEY `fk_building_string_culture__locales`;
ALTER TABLE `building_string_value` ADD CONSTRAINT `fk_building_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`);

ALTER TABLE `building_address_string_culture` RENAME `building_address_string_value`;
ALTER TABLE `building_address_string_value` DROP FOREIGN KEY `fk_building_address_string_culture__locales`;
ALTER TABLE `building_address_string_value` ADD CONSTRAINT `fk_building_address_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`);

ALTER TABLE `apartment_string_culture` RENAME `apartment_string_value`;
ALTER TABLE `apartment_string_value` DROP FOREIGN KEY `fk_apartment_string_culture__locales`;
ALTER TABLE `apartment_string_value` ADD CONSTRAINT `fk_apartment_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`);

ALTER TABLE `room_string_culture` RENAME `room_string_value`;
ALTER TABLE `room_string_value` DROP FOREIGN KEY `fk_room_string_culture__locales`;
ALTER TABLE `room_string_value` ADD CONSTRAINT `fk_room_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`);

ALTER TABLE `user_info_string_culture` RENAME `user_info_string_value`;
ALTER TABLE `user_info_string_value` DROP FOREIGN KEY `fk_user_info_string_culture__locales`;
ALTER TABLE `user_info_string_value` ADD CONSTRAINT `fk_user_info_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`);

ALTER TABLE `service_string_culture` RENAME `service_string_value`;
ALTER TABLE `service_string_value` DROP FOREIGN KEY `fk_service_string_culture__locales`;
ALTER TABLE `service_string_value` ADD CONSTRAINT `fk_service_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`);

ALTER TABLE `ownership_string_culture` RENAME `ownership_string_value`;
ALTER TABLE `ownership_string_value` DROP FOREIGN KEY `fk_ownership_string_culture__locales`;
ALTER TABLE `ownership_string_value` ADD CONSTRAINT `fk_ownership_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`);

ALTER TABLE `privilege_string_culture` RENAME `privilege_string_value`;
ALTER TABLE `privilege_string_value` DROP FOREIGN KEY `fk_privilege_string_culture__locales`;
ALTER TABLE `privilege_string_value` ADD CONSTRAINT `fk_privilege_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`);

UPDATE `attribute_value_type` t SET  `attribute_value_type` = 'STRING_VALUE' where `attribute_value_type` = 'STRING_CULTURE';

