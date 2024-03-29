DROP TABLE IF EXISTS "domain_sync";

DROP TABLE IF EXISTS "service_correction";
DROP TABLE IF EXISTS "service_string_value";
DROP TABLE IF EXISTS "service_attribute";
DROP TABLE IF EXISTS "service";

DROP TABLE IF EXISTS "organization_type";
DROP TABLE IF EXISTS "organization_type_attribute";
DROP TABLE IF EXISTS "organization_type_string_value";
DROP TABLE IF EXISTS "organization";
DROP TABLE IF EXISTS "organization_attribute";
DROP TABLE IF EXISTS "organization_string_value";
DROP TABLE IF EXISTS "user_organization";
DROP TABLE IF EXISTS "organization_correction";
DROP TABLE IF EXISTS "organization_import";

DROP TABLE IF EXISTS "country";
DROP TABLE IF EXISTS "country_attribute";
DROP TABLE IF EXISTS "country_string_value";
DROP TABLE IF EXISTS "country_correction";
DROP TABLE IF EXISTS "region";
DROP TABLE IF EXISTS "region_attribute";
DROP TABLE IF EXISTS "region_string_value";
DROP TABLE IF EXISTS "region_correction";
DROP TABLE IF EXISTS "city_type";
DROP TABLE IF EXISTS "city_type_attribute";
DROP TABLE IF EXISTS "city_type_string_value";
DROP TABLE IF EXISTS "city_type_correction";
DROP TABLE IF EXISTS "city";
DROP TABLE IF EXISTS "city_attribute";
DROP TABLE IF EXISTS "city_string_value";
DROP TABLE IF EXISTS "city_correction";
DROP TABLE IF EXISTS "district";
DROP TABLE IF EXISTS "district_attribute";
DROP TABLE IF EXISTS "district_string_value";
DROP TABLE IF EXISTS "district_correction";
DROP TABLE IF EXISTS "street_type";
DROP TABLE IF EXISTS "street_type_attribute";
DROP TABLE IF EXISTS "street_type_string_value";
DROP TABLE IF EXISTS "street_type_correction";
DROP TABLE IF EXISTS "street";
DROP TABLE IF EXISTS "street_attribute";
DROP TABLE IF EXISTS "street_string_value";
DROP TABLE IF EXISTS "street_correction";
DROP TABLE IF EXISTS "building";
DROP TABLE IF EXISTS "building_attribute";
DROP TABLE IF EXISTS "building_string_value";
DROP TABLE IF EXISTS "building_code";
DROP TABLE IF EXISTS "building_correction";
DROP TABLE IF EXISTS "apartment";
DROP TABLE IF EXISTS "apartment_attribute";
DROP TABLE IF EXISTS "apartment_string_value";
DROP TABLE IF EXISTS "room";
DROP TABLE IF EXISTS "room_attribute";
DROP TABLE IF EXISTS "room_string_value";

DROP VIEW IF EXISTS "user_view";

DROP TABLE IF EXISTS "update";
DROP TABLE IF EXISTS "log_change";
DROP TABLE IF EXISTS "log";
DROP TABLE IF EXISTS "preference";
DROP TABLE IF EXISTS "config";
DROP TABLE IF EXISTS "permission";
DROP TABLE IF EXISTS "last_name";
DROP TABLE IF EXISTS "middle_name";
DROP TABLE IF EXISTS "first_name";
DROP TABLE IF EXISTS "usergroup";
DROP TABLE IF EXISTS "user_info_string_value";
DROP TABLE IF EXISTS "user_info_attribute";
DROP TABLE IF EXISTS "user_info";
DROP TABLE IF EXISTS "user";

DROP TABLE IF EXISTS entity_value_type;
DROP TABLE IF EXISTS "entity_attribute";
DROP TABLE IF EXISTS "entity";
DROP TABLE IF EXISTS "entity_string_value";
DROP TABLE IF EXISTS "locale";
DROP TABLE IF EXISTS "sequence";

DROP SEQUENCE IF EXISTS entity_string_value_id_sequence;
DROP SEQUENCE IF EXISTS user_info_string_value_id_sequence;
DROP SEQUENCE IF EXISTS organization_type_string_value_id_sequence;
DROP SEQUENCE IF EXISTS organization_string_value_id_sequence;
DROP SEQUENCE IF EXISTS service_string_value_id_sequence;
DROP SEQUENCE IF EXISTS country_string_value_id_sequence;
DROP SEQUENCE IF EXISTS region_string_value_id_sequence;
DROP SEQUENCE IF EXISTS city_type_string_value_id_sequence;
DROP SEQUENCE IF EXISTS city_string_value_id_sequence;
DROP SEQUENCE IF EXISTS district_string_value_id_sequence;
DROP SEQUENCE IF EXISTS street_type_string_value_id_sequence;
DROP SEQUENCE IF EXISTS street_string_value_id_sequence;
DROP SEQUENCE IF EXISTS building_string_value_id_sequence;
DROP SEQUENCE IF EXISTS apartment_string_value_id_sequence;

DROP SEQUENCE IF EXISTS entity_object_id_sequence;
DROP SEQUENCE IF EXISTS user_info_object_id_sequence;
DROP SEQUENCE IF EXISTS organization_type_object_id_sequence;
DROP SEQUENCE IF EXISTS organization_object_id_sequence;
DROP SEQUENCE IF EXISTS service_object_id_sequence;
DROP SEQUENCE IF EXISTS country_object_id_sequence;
DROP SEQUENCE IF EXISTS region_object_id_sequence;
DROP SEQUENCE IF EXISTS city_type_object_id_sequence;
DROP SEQUENCE IF EXISTS city_object_id_sequence;
DROP SEQUENCE IF EXISTS district_object_id_sequence;
DROP SEQUENCE IF EXISTS street_type_object_id_sequence;
DROP SEQUENCE IF EXISTS street_object_id_sequence;
DROP SEQUENCE IF EXISTS building_object_id_sequence;
DROP SEQUENCE IF EXISTS apartment_object_id_sequence;

DROP FUNCTION IF EXISTS "check_reference";
DROP FUNCTION IF EXISTS "to_cyrillic";
