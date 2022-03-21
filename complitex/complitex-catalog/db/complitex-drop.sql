DROP TABLE IF EXISTS "catalog" CASCADE;
DROP TABLE IF EXISTS "type" CASCADE ;
DROP TABLE IF EXISTS "locale" CASCADE ;
DROP TABLE IF EXISTS "value" CASCADE;

DROP FUNCTION IF EXISTS role_relevance;
DROP FUNCTION IF EXISTS group_relevance;
DROP FUNCTION IF EXISTS user_relevance;

DROP VIEW IF EXISTS role_view;
DROP VIEW IF EXISTS group_view;
DROP VIEW IF EXISTS user_view;

DROP TABLE IF EXISTS "user_data", "user";
DROP TABLE IF EXISTS "group_data", "group";
DROP TABLE IF EXISTS "role_data", "role";

DROP TABLE IF EXISTS "flat_correction_data", "flat_correction", "flat_data", "flat";
DROP TABLE IF EXISTS "house_correction_data", "house_correction", "house_data", "house";
DROP TABLE IF EXISTS "street_correction_data", "street_correction", "street_data", "street";
DROP TABLE IF EXISTS "street_type_correction_data", "street_type_correction", "street_type_data", "street_type";
DROP TABLE IF EXISTS "district_correction_data", "district_correction", "district_data", "district";
DROP TABLE IF EXISTS "city_type_correction_data", "city_type_correction", "city_type_data", "city_type";
DROP TABLE IF EXISTS "city_correction_data", "city_correction", "city_data", "city";
DROP TABLE IF EXISTS "region_correction_data", "region_correction", "region_data", "region";
DROP TABLE IF EXISTS "country_correction_data", "country_correction", "country_data", "country";
DROP TABLE IF EXISTS "organization_type_data", "organization_type";
DROP TABLE IF EXISTS "organization_correction_data", "organization_correction", "organization_data", "organization";

DROP FUNCTION IF EXISTS "organization_type_relevance";
DROP FUNCTION IF EXISTS "organization_correction_relevance", "organization_relevance";
DROP FUNCTION IF EXISTS "country_correction_relevance", "country_relevance";
DROP FUNCTION IF EXISTS "region_correction_relevance", "region_relevance";
DROP FUNCTION IF EXISTS "city_correction_relevance", "city_relevance";
DROP FUNCTION IF EXISTS "city_type_correction_relevance", "city_type_relevance";
DROP FUNCTION IF EXISTS "district_correction_relevance", "district_relevance";
DROP FUNCTION IF EXISTS "street_type_correction_relevance", "street_type_relevance";
DROP FUNCTION IF EXISTS "street_correction_relevance", "street_relevance";
DROP FUNCTION IF EXISTS "house_correction_relevance", "house_relevance";
DROP FUNCTION IF EXISTS "flat_correction_relevance", "flat_relevance";

DROP VIEW IF EXISTS catalog_view;

DROP FUNCTION IF EXISTS value_relevance;
DROP FUNCTION IF EXISTS check_data_reference;
DROP FUNCTION IF EXISTS check_data_relevance;

DROP PROCEDURE IF EXISTS create_catalog;
DROP PROCEDURE IF EXISTS create_value;
DROP PROCEDURE IF EXISTS create_numeric;
DROP PROCEDURE IF EXISTS create_text;
DROP PROCEDURE IF EXISTS create_local_text;
DROP PROCEDURE IF EXISTS create_timestamp;
DROP PROCEDURE IF EXISTS create_reference;

DROP PROCEDURE IF EXISTS add_item;
DROP PROCEDURE IF EXISTS add_text;
DROP PROCEDURE IF EXISTS add_reference;


