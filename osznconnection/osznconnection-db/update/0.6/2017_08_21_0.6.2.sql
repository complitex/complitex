-- entity

ALTER TABLE `string_value` RENAME `entity_string_value`;

ALTER TABLE `entity` CHANGE `entity_table` `entity` VARCHAR(100) NOT NULL COMMENT 'Название сущности';
ALTER TABLE `entity` CHANGE `entity_name_id` `name_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локализации названия сущности';
ALTER TABLE `entity` RENAME INDEX `unique_entity_table`  TO `unique_entity`;
ALTER TABLE `entity` RENAME INDEX `key_entity_name_id`  TO `key_name_id`;
ALTER TABLE `entity` DROP FOREIGN KEY `fk_entity__string_value`;
ALTER TABLE `entity` ADD CONSTRAINT `fk_entity__entity_string_value` FOREIGN KEY (name_id) REFERENCES entity_string_value (`id`);

-- entity_attribute_value_type

ALTER TABLE `attribute_value_type` RENAME `entity_attribute_value_type`;
ALTER TABLE entity_attribute_value_type CHANGE `attribute_type_id` `entity_attribute_id` BIGINT(20) COMMENT 'Идентификатор типа атрибута';
ALTER TABLE entity_attribute_value_type CHANGE `attribute_value_type` `value_type` VARCHAR(100) NOT NULL COMMENT 'Тип значения атрибута';
ALTER TABLE entity_attribute_value_type RENAME INDEX `key_attribute_type_id` TO `key_entity_attribute_id`;
ALTER TABLE entity_attribute_value_type DROP FOREIGN KEY `fk_attribute_value_type`;

-- entity_attribute

ALTER TABLE `attribute_type` RENAME `entity_attribute`;
ALTER TABLE `entity_attribute` CHANGE `attribute_type_name_id` `name_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локализации названия атрибута';
ALTER TABLE `entity_attribute` RENAME INDEX `key_attribute_type_name_id` TO `key_name_id`;
ALTER TABLE `entity_attribute` DROP FOREIGN KEY `fk_attribute_type__string_value`;
ALTER TABLE `entity_attribute` ADD CONSTRAINT `fk_entity_attribute__entity_string_value`
FOREIGN KEY (name_id) REFERENCES entity_string_value (`id`);
ALTER TABLE entity_attribute ADD COLUMN value_type_id BIGINT(20) COMMENT  'Тип значения атрибута';
ALTER TABLE entity_attribute ADD COLUMN reference_id BIGINT(20) COMMENT  'Внешний ключ';

-- organization_type_attribute

ALTER TABLE organization_type_attribute CHANGE attribute_type_id entity_attribute_id BIGINT(20) NOT NULL
COMMENT 'Идентификатор типа атрибута: 2300 - НАИМЕНОВАНИЕ ТИПА ОРГАНИЗАЦИИ';
ALTER TABLE organization_type_attribute RENAME INDEX key_attribute_type_id TO key_entity_attribute_id;
ALTER TABLE organization_type_attribute DROP FOREIGN KEY fk_organization_type_attribute__attribute_type;
ALTER TABLE organization_type_attribute ADD CONSTRAINT fk_organization_type_attribute__entity_attribute
FOREIGN KEY (entity_attribute_id) REFERENCES entity_attribute (id);
ALTER TABLE organization_type_attribute DROP FOREIGN KEY  fk_organization_type_attribute__attribute_value_type;
ALTER TABLE organization_type_attribute DROP COLUMN value_type_id;

-- organization_attribute

ALTER TABLE organization_attribute CHANGE attribute_type_id entity_attribute_id BIGINT(20) NOT NULL
COMMENT 'Идентификатор типа атрибута: 900 - НАЗВАНИЕ, 901 - УНИКАЛЬНЫЙ КОД, 902 - РАЙОН, 903 - ПРИНАДЛЕЖИТ, 904  - ТИП ОРГАНИЗАЦИИ';
ALTER TABLE organization_attribute RENAME INDEX key_attribute_type_id TO key_entity_attribute_id;
ALTER TABLE organization_attribute DROP FOREIGN KEY fk_organization_attribute__attribute_type;
ALTER TABLE organization_attribute ADD CONSTRAINT fk_organization_attribute__entity_attribute
FOREIGN KEY (entity_attribute_id) REFERENCES entity_attribute (id);
ALTER TABLE organization_attribute DROP FOREIGN KEY  fk_organization_attribute__attribute_value_type;
ALTER TABLE organization_attribute DROP COLUMN value_type_id;

-- country_attribute

ALTER TABLE country_attribute CHANGE attribute_type_id entity_attribute_id BIGINT(20) NOT NULL
COMMENT 'Идентификатор типа атрибута: 800 - НАИМЕНОВАНИЕ СТРАНЫ';
ALTER TABLE country_attribute RENAME INDEX key_attribute_type_id TO key_entity_attribute_id;
ALTER TABLE country_attribute DROP FOREIGN KEY fk_country_attribute__attribute_type;
ALTER TABLE country_attribute ADD CONSTRAINT fk_country_attribute__entity_attribute
FOREIGN KEY (entity_attribute_id) REFERENCES entity_attribute (id);
ALTER TABLE country_attribute DROP FOREIGN KEY  fk_country_attribute__attribute_value_type;
ALTER TABLE country_attribute DROP COLUMN value_type_id;

-- region_attribute

ALTER TABLE region_attribute CHANGE attribute_type_id entity_attribute_id BIGINT(20) NOT NULL
COMMENT 'Идентификатор типа атрибута: 700 - НАИМЕНОВАНИЕ РЕГИОНА';
ALTER TABLE region_attribute RENAME INDEX key_attribute_type_id TO key_entity_attribute_id;
ALTER TABLE region_attribute DROP FOREIGN KEY fk_region_attribute__attribute_type;
ALTER TABLE region_attribute ADD CONSTRAINT fk_region_attribute__entity_attribute
FOREIGN KEY (entity_attribute_id) REFERENCES entity_attribute (id);
ALTER TABLE region_attribute DROP FOREIGN KEY  fk_region_attribute__attribute_value_type;
ALTER TABLE region_attribute DROP COLUMN value_type_id;

-- city_type_attribute

ALTER TABLE city_type_attribute CHANGE attribute_type_id entity_attribute_id BIGINT(20) NOT NULL
COMMENT 'Идентификатор типа атрибута: 1300 - КРАТКОЕ НАЗВАНИЕ, 1301 - НАЗВАНИЕ';
ALTER TABLE city_type_attribute RENAME INDEX key_attribute_type_id TO key_entity_attribute_id;
ALTER TABLE city_type_attribute DROP FOREIGN KEY fk_city_type_attribute__attribute_type;
ALTER TABLE city_type_attribute ADD CONSTRAINT fk_city_type_attribute__entity_attribute
FOREIGN KEY (entity_attribute_id) REFERENCES entity_attribute (id);
ALTER TABLE city_type_attribute DROP FOREIGN KEY  fk_city_type_attribute__attribute_value_type;
ALTER TABLE city_type_attribute DROP COLUMN value_type_id;

-- city_attribute

ALTER TABLE city_attribute CHANGE attribute_type_id entity_attribute_id BIGINT(20) NOT NULL
COMMENT 'Идентификатор типа атрибута: 400 - НАИМЕНОВАНИЕ НАСЕЛЕННОГО ПУНКТА, 401 - ТИП НАСЕЛЕННОГО ПУНКТА';
ALTER TABLE city_attribute RENAME INDEX key_attribute_type_id TO key_entity_attribute_id;
ALTER TABLE city_attribute DROP FOREIGN KEY fk_city_attribute__attribute_type;
ALTER TABLE city_attribute ADD CONSTRAINT fk_city_attribute__entity_attribute
FOREIGN KEY (entity_attribute_id) REFERENCES entity_attribute (id);
ALTER TABLE city_attribute DROP FOREIGN KEY  fk_city_attribute__attribute_value_type;
ALTER TABLE city_attribute DROP COLUMN value_type_id;

-- district_attribute

ALTER TABLE district_attribute CHANGE attribute_type_id entity_attribute_id BIGINT(20) NOT NULL
COMMENT 'Идентификатор типа атрибута: 600 - НАИМЕНОВАНИЕ РАЙОНА, 601 - КОД РАЙОНА';
ALTER TABLE district_attribute RENAME INDEX key_attribute_type_id TO key_entity_attribute_id;
ALTER TABLE district_attribute DROP FOREIGN KEY fk_district_attribute__attribute_type;
ALTER TABLE district_attribute ADD CONSTRAINT fk_district_attribute__entity_attribute
FOREIGN KEY (entity_attribute_id) REFERENCES entity_attribute (id);
ALTER TABLE district_attribute DROP FOREIGN KEY  fk_district_attribute__attribute_value_type;
ALTER TABLE district_attribute DROP COLUMN value_type_id;

-- street_type_attribute

ALTER TABLE street_type_attribute CHANGE attribute_type_id entity_attribute_id BIGINT(20) NOT NULL
COMMENT 'Идентификатор типа атрибута: 1400 - КРАТКОЕ НАЗВАНИЕ, 1401 - НАЗВАНИЕ';
ALTER TABLE street_type_attribute RENAME INDEX key_attribute_type_id TO key_entity_attribute_id;
ALTER TABLE street_type_attribute DROP FOREIGN KEY fk_street_type_attribute__attribute_type;
ALTER TABLE street_type_attribute ADD CONSTRAINT fk_street_type_attribute__entity_attribute
FOREIGN KEY (entity_attribute_id) REFERENCES entity_attribute (id);
ALTER TABLE street_type_attribute DROP FOREIGN KEY  fk_street_type_attribute__attribute_value_type;
ALTER TABLE street_type_attribute DROP COLUMN value_type_id;

-- street_attribute

ALTER TABLE street_attribute CHANGE attribute_type_id entity_attribute_id BIGINT(20) NOT NULL
COMMENT 'Идентификатор типа атрибута: 300 - НАИМЕНОВАНИЕ УЛИЦЫ, 301 - ТИП УЛИЦЫ';
ALTER TABLE street_attribute RENAME INDEX key_attribute_type_id TO key_entity_attribute_id;
ALTER TABLE street_attribute DROP FOREIGN KEY fk_street_attribute__attribute_type;
ALTER TABLE street_attribute ADD CONSTRAINT fk_street_attribute__entity_attribute
FOREIGN KEY (entity_attribute_id) REFERENCES entity_attribute (id);
ALTER TABLE street_attribute DROP FOREIGN KEY  fk_street_attribute__attribute_value_type;
ALTER TABLE street_attribute DROP COLUMN value_type_id;

-- building_attribute

ALTER TABLE building_attribute CHANGE attribute_type_id entity_attribute_id BIGINT(20) NOT NULL
COMMENT 'Идентификатор типа атрибута: 500 - РАЙОН, 501- АЛЬТЕРНАТИВНЫЙ АДРЕС';
ALTER TABLE building_attribute RENAME INDEX key_attribute_type_id TO key_entity_attribute_id;
ALTER TABLE building_attribute DROP FOREIGN KEY fk_building_attribute__attribute_type;
ALTER TABLE building_attribute ADD CONSTRAINT fk_building_attribute__entity_attribute
FOREIGN KEY (entity_attribute_id) REFERENCES entity_attribute (id);
ALTER TABLE building_attribute DROP FOREIGN KEY  fk_building_attribute__attribute_value_type;
ALTER TABLE building_attribute DROP COLUMN value_type_id;

-- building_address_attribute

ALTER TABLE building_address_attribute CHANGE attribute_type_id entity_attribute_id BIGINT(20) NOT NULL
COMMENT 'Идентификатор типа атрибута: 1500 - НОМЕР ДОМА, 1501 - КОРПУС, 1502 - СТРОЕНИЕ';
ALTER TABLE building_address_attribute RENAME INDEX key_attribute_type_id TO key_entity_attribute_id;
ALTER TABLE building_address_attribute DROP FOREIGN KEY fk_building_address_attribute__attribute_type;
ALTER TABLE building_address_attribute ADD CONSTRAINT fk_building_address_attribute__entity_attribute
FOREIGN KEY (entity_attribute_id) REFERENCES entity_attribute (id);
ALTER TABLE building_address_attribute DROP FOREIGN KEY  fk_building_address_attribute__attribute_value_type;
ALTER TABLE building_address_attribute DROP COLUMN value_type_id;


-- apartment_attribute

ALTER TABLE apartment_attribute CHANGE attribute_type_id entity_attribute_id BIGINT(20) NOT NULL
COMMENT 'Идентификатор типа атрибута: 100 - НАИМЕНОВАНИЕ КВАРТИРЫ';
ALTER TABLE apartment_attribute RENAME INDEX key_attribute_type_id TO key_entity_attribute_id;
ALTER TABLE apartment_attribute DROP FOREIGN KEY fk_apartment_attribute__attribute_type;
ALTER TABLE apartment_attribute ADD CONSTRAINT fk_apartment_attribute__entity_attribute
FOREIGN KEY (entity_attribute_id) REFERENCES entity_attribute (id);
ALTER TABLE apartment_attribute DROP FOREIGN KEY  fk_apartment_attribute__attribute_value_type;
ALTER TABLE apartment_attribute DROP COLUMN value_type_id;

-- room_attribute

ALTER TABLE room_attribute CHANGE attribute_type_id entity_attribute_id BIGINT(20) NOT NULL
COMMENT 'Идентификатор типа атрибута: 200 - НАИМЕНОВАНИЕ КОМНАТЫ';
ALTER TABLE room_attribute RENAME INDEX key_attribute_type_id TO key_entity_attribute_id;
ALTER TABLE room_attribute DROP FOREIGN KEY fk_room_attribute__attribute_type;
ALTER TABLE room_attribute ADD CONSTRAINT fk_room_attribute__entity_attribute
FOREIGN KEY (entity_attribute_id) REFERENCES entity_attribute (id);
ALTER TABLE room_attribute DROP FOREIGN KEY  fk_room_attribute__attribute_value_type;
ALTER TABLE room_attribute DROP COLUMN value_type_id;

-- user_info_attribute

ALTER TABLE user_info_attribute CHANGE attribute_type_id entity_attribute_id BIGINT(20) NOT NULL
COMMENT 'Идентификатор типа атрибута: 1000 - ФАМИЛИЯ, 1001 - ИМЯ, 1002 - ОТЧЕСТВО';
ALTER TABLE user_info_attribute RENAME INDEX key_attribute_type_id TO key_entity_attribute_id;
ALTER TABLE user_info_attribute DROP FOREIGN KEY fk_user_info__attribute_type;
ALTER TABLE user_info_attribute ADD CONSTRAINT fk_user_info_attribute__entity_attribute
FOREIGN KEY (entity_attribute_id) REFERENCES entity_attribute (id);
ALTER TABLE user_info_attribute DROP FOREIGN KEY  fk_user_info__attribute_value_type;
ALTER TABLE user_info_attribute DROP COLUMN value_type_id;

-- service_attribute

ALTER TABLE service_attribute CHANGE attribute_type_id entity_attribute_id BIGINT(20) NOT NULL
COMMENT 'Идентификатор типа атрибута';
ALTER TABLE service_attribute RENAME INDEX key_attribute_type_id TO key_entity_attribute_id;
ALTER TABLE service_attribute DROP FOREIGN KEY fk_service_attribute__attribute_type;
ALTER TABLE service_attribute ADD CONSTRAINT fk_service_attribute__entity_attribute
FOREIGN KEY (entity_attribute_id) REFERENCES entity_attribute (id);
ALTER TABLE service_attribute DROP FOREIGN KEY  fk_service_attribute__attribute_value_type;
ALTER TABLE service_attribute DROP COLUMN value_type_id;

-- ownership_attribute

ALTER TABLE ownership_attribute CHANGE attribute_type_id entity_attribute_id BIGINT(20) NOT NULL
COMMENT 'Идентификатор типа атрибута. Возможные значения: 1100 - НАЗВАНИЕ';
ALTER TABLE ownership_attribute RENAME INDEX key_attribute_type_id TO key_entity_attribute_id;
ALTER TABLE ownership_attribute DROP FOREIGN KEY fk_ownership_attribute__attribute_type;
ALTER TABLE ownership_attribute ADD CONSTRAINT fk_ownership_attribute__entity_attribute
FOREIGN KEY (entity_attribute_id) REFERENCES entity_attribute (id);
ALTER TABLE ownership_attribute DROP FOREIGN KEY  fk_ownership_attribute__attribute_value_type;
ALTER TABLE ownership_attribute DROP COLUMN value_type_id;

-- privilege_attribute

ALTER TABLE privilege_attribute CHANGE attribute_type_id entity_attribute_id BIGINT(20) NOT NULL
COMMENT 'Идентификатор типа атрибута. Возможные значения: 1200 - НАЗВАНИЕ, 1201 - КОД';
ALTER TABLE privilege_attribute RENAME INDEX key_attribute_type_id TO key_entity_attribute_id;
ALTER TABLE privilege_attribute DROP FOREIGN KEY fk_privilege_attribute__attribute_type;
ALTER TABLE privilege_attribute ADD CONSTRAINT fk_privilege_attribute__entity_attribute
FOREIGN KEY (entity_attribute_id) REFERENCES entity_attribute (id);
ALTER TABLE privilege_attribute DROP FOREIGN KEY  fk_privilege_attribute__attribute_value_type;
ALTER TABLE privilege_attribute DROP COLUMN value_type_id;

-- value type
INSERT INTO entity_attribute_value_type (id, value_type) VALUE (0, 'string_value');
INSERT INTO entity_attribute_value_type (id, value_type) VALUE (1, 'string');
INSERT INTO entity_attribute_value_type (id, value_type) VALUE (2, 'boolean');
INSERT INTO entity_attribute_value_type (id, value_type) VALUE (3, 'decimal');
INSERT INTO entity_attribute_value_type (id, value_type) VALUE (4, 'integer');
INSERT INTO entity_attribute_value_type (id, value_type) VALUE (5, 'date');

INSERT INTO entity_attribute_value_type (id, value_type) VALUE (10, 'entity');

INSERT INTO entity_attribute_value_type (id, value_type) VALUE (20, 'building_code');
INSERT INTO entity_attribute_value_type (id, value_type) VALUE (21, 'last_name');
INSERT INTO entity_attribute_value_type (id, value_type) VALUE (22, 'first_name');
INSERT INTO entity_attribute_value_type (id, value_type) VALUE (23, 'middle_name');


INSERT INTO `update` (`version`) VALUE ('20170821_0.6.2');
