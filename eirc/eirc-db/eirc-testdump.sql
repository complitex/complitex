-- MySQL dump 10.13  Distrib 5.5.38, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: eirc
-- ------------------------------------------------------
-- Server version	5.5.38-0ubuntu0.14.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `apartment`
--

DROP TABLE IF EXISTS `apartment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `apartment` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор сущности родительского объекта: 500 - building',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус объекта. См. класс StatusType',
  `permission_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'Ключ прав доступа к объекту',
  `external_id` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_apartment__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_apartment__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Квартира';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `apartment`
--

LOCK TABLES `apartment` WRITE;
/*!40000 ALTER TABLE `apartment` DISABLE KEYS */;
INSERT INTO `apartment` VALUES (1,1,1,500,'2014-08-19 07:14:30',NULL,1,0,NULL),(2,2,1,500,'2014-08-19 07:14:30',NULL,1,0,NULL),(3,3,1,500,'2014-08-19 07:14:30',NULL,1,0,NULL),(4,4,1,500,'2014-08-19 07:14:30',NULL,1,0,NULL),(5,5,1,500,'2014-08-19 07:14:30',NULL,1,0,NULL),(6,6,1,500,'2014-08-19 07:14:30',NULL,1,0,NULL),(7,7,1,500,'2014-08-19 07:14:30',NULL,1,0,NULL),(8,8,1,500,'2014-08-19 07:14:31',NULL,1,0,NULL),(9,9,1,500,'2014-08-19 07:14:31',NULL,1,0,NULL),(10,10,1,500,'2014-08-19 07:14:31',NULL,1,0,NULL),(11,11,1,500,'2014-08-19 07:14:31',NULL,1,0,NULL),(12,12,1,500,'2014-08-19 07:14:31',NULL,1,0,NULL),(13,13,1,500,'2014-08-19 07:14:31',NULL,1,0,NULL),(14,14,1,500,'2014-08-19 07:14:31',NULL,1,0,NULL),(15,15,1,500,'2014-08-19 07:14:31',NULL,1,0,NULL),(16,16,1,500,'2014-08-19 07:14:31',NULL,1,0,NULL),(17,17,1,500,'2014-08-19 07:14:31',NULL,1,0,NULL),(18,18,1,500,'2014-08-19 07:14:31',NULL,1,0,NULL),(19,19,1,500,'2014-08-19 07:14:31',NULL,1,0,NULL),(20,20,1,500,'2014-08-19 07:14:31',NULL,1,0,NULL),(21,21,1,500,'2014-08-19 07:14:31',NULL,1,0,NULL),(22,22,1,500,'2014-08-19 07:14:31',NULL,1,0,NULL),(23,23,1,500,'2014-08-19 07:14:31',NULL,1,0,NULL),(24,24,1,500,'2014-08-19 07:14:31',NULL,1,0,NULL),(25,25,1,500,'2014-08-19 07:14:32',NULL,1,0,NULL),(26,26,1,500,'2014-08-19 07:14:32',NULL,1,0,NULL),(27,27,1,500,'2014-08-19 07:14:32',NULL,1,0,NULL),(28,28,1,500,'2014-08-19 07:14:32',NULL,1,0,NULL),(29,29,1,500,'2014-08-19 07:14:32',NULL,1,0,NULL),(30,30,1,500,'2014-08-19 07:14:32',NULL,1,0,NULL),(31,31,1,500,'2014-08-19 07:14:32',NULL,1,0,NULL),(32,32,1,500,'2014-08-19 07:14:32',NULL,1,0,NULL),(33,33,1,500,'2014-08-19 07:14:32',NULL,1,0,NULL),(34,34,1,500,'2014-08-19 07:14:32',NULL,1,0,NULL),(35,35,1,500,'2014-08-19 07:14:32',NULL,1,0,NULL),(36,36,1,500,'2014-08-19 07:14:32',NULL,1,0,NULL);
/*!40000 ALTER TABLE `apartment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `apartment_attribute`
--

DROP TABLE IF EXISTS `apartment_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `apartment_attribute` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` bigint(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 100 - НАИМЕНОВАНИЕ КВАРТИРЫ',
  `value_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор значения',
  `value_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа значения: 100 - string_value',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_apartment_attribute__apartment` FOREIGN KEY (`object_id`) REFERENCES `apartment` (`object_id`),
  CONSTRAINT `fk_apartment_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`) REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_apartment_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`) REFERENCES `entity_attribute_value_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Атрибуты квартиры';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `apartment_attribute`
--

LOCK TABLES `apartment_attribute` WRITE;
/*!40000 ALTER TABLE `apartment_attribute` DISABLE KEYS */;
INSERT INTO `apartment_attribute` VALUES (1,1,1,100,1,100,'2014-08-19 07:14:30',NULL,1),(2,1,2,100,2,100,'2014-08-19 07:14:30',NULL,1),(3,1,3,100,3,100,'2014-08-19 07:14:30',NULL,1),(4,1,4,100,4,100,'2014-08-19 07:14:30',NULL,1),(5,1,5,100,5,100,'2014-08-19 07:14:30',NULL,1),(6,1,6,100,6,100,'2014-08-19 07:14:30',NULL,1),(7,1,7,100,7,100,'2014-08-19 07:14:30',NULL,1),(8,1,8,100,8,100,'2014-08-19 07:14:31',NULL,1),(9,1,9,100,9,100,'2014-08-19 07:14:31',NULL,1),(10,1,10,100,10,100,'2014-08-19 07:14:31',NULL,1),(11,1,11,100,11,100,'2014-08-19 07:14:31',NULL,1),(12,1,12,100,12,100,'2014-08-19 07:14:31',NULL,1),(13,1,13,100,13,100,'2014-08-19 07:14:31',NULL,1),(14,1,14,100,14,100,'2014-08-19 07:14:31',NULL,1),(15,1,15,100,15,100,'2014-08-19 07:14:31',NULL,1),(16,1,16,100,16,100,'2014-08-19 07:14:31',NULL,1),(17,1,17,100,17,100,'2014-08-19 07:14:31',NULL,1),(18,1,18,100,18,100,'2014-08-19 07:14:31',NULL,1),(19,1,19,100,19,100,'2014-08-19 07:14:31',NULL,1),(20,1,20,100,20,100,'2014-08-19 07:14:31',NULL,1),(21,1,21,100,21,100,'2014-08-19 07:14:31',NULL,1),(22,1,22,100,22,100,'2014-08-19 07:14:31',NULL,1),(23,1,23,100,23,100,'2014-08-19 07:14:31',NULL,1),(24,1,24,100,24,100,'2014-08-19 07:14:31',NULL,1),(25,1,25,100,25,100,'2014-08-19 07:14:32',NULL,1),(26,1,26,100,26,100,'2014-08-19 07:14:32',NULL,1),(27,1,27,100,27,100,'2014-08-19 07:14:32',NULL,1),(28,1,28,100,28,100,'2014-08-19 07:14:32',NULL,1),(29,1,29,100,29,100,'2014-08-19 07:14:32',NULL,1),(30,1,30,100,30,100,'2014-08-19 07:14:32',NULL,1),(31,1,31,100,31,100,'2014-08-19 07:14:32',NULL,1),(32,1,32,100,32,100,'2014-08-19 07:14:32',NULL,1),(33,1,33,100,33,100,'2014-08-19 07:14:32',NULL,1),(34,1,34,100,34,100,'2014-08-19 07:14:32',NULL,1),(35,1,35,100,35,100,'2014-08-19 07:14:32',NULL,1),(36,1,36,100,36,100,'2014-08-19 07:14:32',NULL,1);
/*!40000 ALTER TABLE `apartment_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `apartment_correction`
--

DROP TABLE IF EXISTS `apartment_correction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `apartment_correction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `building_object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта дом',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта квартира',
  `external_id` varchar(20) DEFAULT NULL COMMENT 'Внешний идентификатор объекта',
  `correction` varchar(100) NOT NULL COMMENT 'Номер квартиры',
  `begin_date` date NOT NULL DEFAULT '1970-01-01' COMMENT 'Дата начала актуальности соответствия',
  `end_date` date NOT NULL DEFAULT '2054-12-31' COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` bigint(20) NOT NULL COMMENT 'Идентификатор организации',
  `user_organization_id` bigint(20) DEFAULT NULL,
  `module_id` bigint(20) NOT NULL COMMENT 'Идентификатор модуля',
  `status` int(11) DEFAULT NULL COMMENT 'Статус',
  PRIMARY KEY (`id`),
  KEY `key_building_object_id` (`building_object_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_begin_date` (`begin_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  KEY `key_module_id` (`module_id`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_apartment_correction__apartment` FOREIGN KEY (`object_id`) REFERENCES `apartment` (`object_id`),
  CONSTRAINT `fk_apartment_correction__building` FOREIGN KEY (`building_object_id`) REFERENCES `building` (`object_id`),
  CONSTRAINT `fk_apartment_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_apartment_correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Коррекция квартиры';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `apartment_correction`
--

LOCK TABLES `apartment_correction` WRITE;
/*!40000 ALTER TABLE `apartment_correction` DISABLE KEYS */;
/*!40000 ALTER TABLE `apartment_correction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `apartment_string_value`
--

DROP TABLE IF EXISTS `apartment_string_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `apartment_string_value` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` bigint(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` bigint(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_apartment_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Локализация атрибутов квартиры';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `apartment_string_value`
--

LOCK TABLES `apartment_string_value` WRITE;
/*!40000 ALTER TABLE `apartment_string_value` DISABLE KEYS */;
INSERT INTO `apartment_string_value` VALUES (1,1,1,'1'),(2,2,1,'2'),(3,3,1,'3'),(4,4,1,'4'),(5,5,1,'5'),(6,6,1,'6'),(7,7,1,'7'),(8,8,1,'8'),(9,9,1,'9'),(10,10,1,'10'),(11,11,1,'11'),(12,12,1,'12'),(13,13,1,'13'),(14,14,1,'14'),(15,15,1,'15'),(16,16,1,'16'),(17,17,1,'17'),(18,18,1,'18'),(19,19,1,'19'),(20,20,1,'20'),(21,21,1,'21'),(22,22,1,'22'),(23,23,1,'23'),(24,24,1,'24'),(25,25,1,'25'),(26,26,1,'26'),(27,27,1,'27'),(28,28,1,'28'),(29,29,1,'29'),(30,30,1,'30'),(31,31,1,'31'),(32,32,1,'32'),(33,33,1,'33'),(34,34,1,'34'),(35,35,1,'35'),(36,36,1,'36');
/*!40000 ALTER TABLE `apartment_string_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `building`
--

DROP TABLE IF EXISTS `building`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `building` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор сущности родительского объекта: 1500 - building_address',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  `permission_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'Ключ прав доступа к объекту',
  `external_id` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_building__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_building__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Дом';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `building`
--

LOCK TABLES `building` WRITE;
/*!40000 ALTER TABLE `building` DISABLE KEYS */;
INSERT INTO `building` VALUES (1,1,1,1500,'2014-08-19 07:13:00',NULL,1,0,NULL);
/*!40000 ALTER TABLE `building` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `building_address`
--

DROP TABLE IF EXISTS `building_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `building_address` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор сущности родительского объекта: 300 - street',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  `permission_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'Ключ прав доступа к объекту',
  `external_id` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_building_address__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_building_address__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Адрес дома';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `building_address`
--

LOCK TABLES `building_address` WRITE;
/*!40000 ALTER TABLE `building_address` DISABLE KEYS */;
INSERT INTO `building_address` VALUES (1,1,1,300,'2014-08-19 07:13:00',NULL,1,0,NULL);
/*!40000 ALTER TABLE `building_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `building_address_attribute`
--

DROP TABLE IF EXISTS `building_address_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `building_address_attribute` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` bigint(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 1500 - НОМЕР ДОМА, 1501 - КОРПУС, 1502 - СТРОЕНИЕ',
  `value_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор значения',
  `value_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа значения атрибута: 1500 - string_value, 1501 - string_value, 1502 - string_value',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_building_address_attribute__building_address` FOREIGN KEY (`object_id`) REFERENCES `building_address` (`object_id`),
  CONSTRAINT `fk_building_address_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`) REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_building_address_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`) REFERENCES `entity_attribute_value_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Атрибуты адреса дома';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `building_address_attribute`
--

LOCK TABLES `building_address_attribute` WRITE;
/*!40000 ALTER TABLE `building_address_attribute` DISABLE KEYS */;
INSERT INTO `building_address_attribute` VALUES (1,1,1,1500,1,1500,'2014-08-19 07:13:00',NULL,1);
/*!40000 ALTER TABLE `building_address_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `building_address_string_value`
--

DROP TABLE IF EXISTS `building_address_string_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `building_address_string_value` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` bigint(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` bigint(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_building_address_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Локализация атрибутов адреса дома';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `building_address_string_value`
--

LOCK TABLES `building_address_string_value` WRITE;
/*!40000 ALTER TABLE `building_address_string_value` DISABLE KEYS */;
INSERT INTO `building_address_string_value` VALUES (1,1,1,'2');
/*!40000 ALTER TABLE `building_address_string_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `building_address_sync`
--

DROP TABLE IF EXISTS `building_address_sync`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `building_address_sync` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор синхронизации дома',
  `object_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор объекта дом',
  `street_type_object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта тип улицы',
  `street_object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта улица',
  `district_object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта район',
  `external_id` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Код дома (ID)',
  `street_external_id` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Код улицы (ID)',
  `name` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Номер дома',
  `part` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Номер корпуса',
  `date` datetime NOT NULL COMMENT 'Дата актуальности',
  `status` int(11) NOT NULL COMMENT 'Статус синхронизации',
  PRIMARY KEY (`id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_street_type_object_id` (`street_type_object_id`),
  KEY `key_street_object_id` (`street_object_id`),
  KEY `key_district_object_id` (`district_object_id`),
  KEY `key_external_id` (`external_id`),
  KEY `key_street_external_id` (`street_external_id`),
  KEY `key_name` (`name`),
  KEY `key_part` (`part`),
  KEY `key_date` (`date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_building_address_sync__building_address` FOREIGN KEY (`object_id`) REFERENCES `building_address` (`object_id`),
  CONSTRAINT `fk_building_address_sync__district` FOREIGN KEY (`district_object_id`) REFERENCES `district` (`object_id`),
  CONSTRAINT `fk_building_address_sync__street` FOREIGN KEY (`street_object_id`) REFERENCES `street` (`object_id`),
  CONSTRAINT `fk_building_address_sync__street_type` FOREIGN KEY (`street_type_object_id`) REFERENCES `street_type` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Синхронизация домов';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `building_address_sync`
--

LOCK TABLES `building_address_sync` WRITE;
/*!40000 ALTER TABLE `building_address_sync` DISABLE KEYS */;
/*!40000 ALTER TABLE `building_address_sync` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `building_attribute`
--

DROP TABLE IF EXISTS `building_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `building_attribute` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` bigint(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 500 - РАЙОН, 501- АЛЬТЕРНАТИВНЫЙ АДРЕС',
  `value_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор значения',
  `value_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа значения атрибута: 500 - district, 501 - building_address',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_building_attribute__building` FOREIGN KEY (`object_id`) REFERENCES `building` (`object_id`),
  CONSTRAINT `fk_building_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`) REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_building_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`) REFERENCES `entity_attribute_value_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Атрибуты дома';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `building_attribute`
--

LOCK TABLES `building_attribute` WRITE;
/*!40000 ALTER TABLE `building_attribute` DISABLE KEYS */;
INSERT INTO `building_attribute` VALUES (1,1,1,502,1,502,'2014-08-19 07:13:00',NULL,1);
/*!40000 ALTER TABLE `building_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `building_code`
--

DROP TABLE IF EXISTS `building_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `building_code` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `organization_id` bigint(20) NOT NULL COMMENT 'ID обслуживающей организации',
  `code` int(11) NOT NULL COMMENT 'Код дома для данной обслуживающей организации',
  `building_id` bigint(20) NOT NULL COMMENT 'ID дома',
  PRIMARY KEY (`id`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_building_id` (`building_id`),
  CONSTRAINT `fk_building_code__building` FOREIGN KEY (`building_id`) REFERENCES `building` (`object_id`),
  CONSTRAINT `fk_building_code__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Код дома';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `building_code`
--

LOCK TABLES `building_code` WRITE;
/*!40000 ALTER TABLE `building_code` DISABLE KEYS */;
INSERT INTO `building_code` VALUES (1,2,33,1);
/*!40000 ALTER TABLE `building_code` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `building_correction`
--

DROP TABLE IF EXISTS `building_correction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `building_correction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `street_object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта улица',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта дом',
  `external_id` varchar(20) DEFAULT NULL COMMENT 'Внешний идентификатор объекта',
  `correction` varchar(100) NOT NULL COMMENT 'Номер дома',
  `correction_corp` varchar(20) NOT NULL DEFAULT '' COMMENT 'Корпус дома',
  `begin_date` date NOT NULL DEFAULT '1970-01-01' COMMENT 'Дата начала актуальности соответствия',
  `end_date` date NOT NULL DEFAULT '2054-12-31' COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` bigint(20) NOT NULL COMMENT 'Идентификатор организации',
  `user_organization_id` bigint(20) DEFAULT NULL,
  `module_id` bigint(20) NOT NULL COMMENT 'Идентификатор модуля',
  `status` int(11) DEFAULT NULL COMMENT 'Статус',
  PRIMARY KEY (`id`),
  KEY `key_street_object_id` (`street_object_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_begin_date` (`begin_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  KEY `key_module_id` (`module_id`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_building_correction__building` FOREIGN KEY (`object_id`) REFERENCES `building` (`object_id`),
  CONSTRAINT `fk_building_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_building_correction__street` FOREIGN KEY (`street_object_id`) REFERENCES `street` (`object_id`),
  CONSTRAINT `fk_building_correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Коррекция дома';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `building_correction`
--

LOCK TABLES `building_correction` WRITE;
/*!40000 ALTER TABLE `building_correction` DISABLE KEYS */;
/*!40000 ALTER TABLE `building_correction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `building_string_value`
--

DROP TABLE IF EXISTS `building_string_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `building_string_value` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` bigint(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` bigint(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_building_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Локализация атрибутов дома ';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `building_string_value`
--

LOCK TABLES `building_string_value` WRITE;
/*!40000 ALTER TABLE `building_string_value` DISABLE KEYS */;
/*!40000 ALTER TABLE `building_string_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cash_payment`
--

DROP TABLE IF EXISTS `cash_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cash_payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `service_provider_account_id` bigint(20) NOT NULL,
  `payment_collector_id` bigint(20) NOT NULL,
  `amount` decimal(19,2) NOT NULL,
  `number_quittance` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `date_formation` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `registry_record_container_id` bigint(20) DEFAULT NULL COMMENT '????????????? ?????????? ?????? ??????? ????????? ?????????',
  PRIMARY KEY (`id`),
  KEY `cash_payment_sp_account__date_formation` (`service_provider_account_id`,`date_formation`),
  KEY `fk_cash_payment__organization` (`payment_collector_id`),
  KEY `fk_cash_payment__registry_record_container` (`registry_record_container_id`),
  CONSTRAINT `fk_cash_payment__registry_record_container` FOREIGN KEY (`registry_record_container_id`) REFERENCES `registry_record_container` (`id`) ON UPDATE SET NULL,
  CONSTRAINT `fk_cash_payment__organization` FOREIGN KEY (`payment_collector_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_cash_payment__sp_account` FOREIGN KEY (`service_provider_account_id`) REFERENCES `service_provider_account` (`object_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Наличные оплаты';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cash_payment`
--

LOCK TABLES `cash_payment` WRITE;
/*!40000 ALTER TABLE `cash_payment` DISABLE KEYS */;
/*!40000 ALTER TABLE `cash_payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cashless_payment`
--

DROP TABLE IF EXISTS `cashless_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cashless_payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `service_provider_account_id` bigint(20) NOT NULL,
  `payment_collector_id` bigint(20) NOT NULL,
  `amount` decimal(19,2) NOT NULL,
  `number_quittance` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `date_formation` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `registry_record_container_id` bigint(20) DEFAULT NULL COMMENT '????????????? ?????????? ?????? ??????? ????????? ?????????',
  PRIMARY KEY (`id`),
  KEY `cashless_payment_sp_account__date_formation_formation` (`service_provider_account_id`,`date_formation`),
  KEY `fk_cashless_payment__organization` (`payment_collector_id`),
  KEY `fk_cashless_payment__registry_record_container` (`registry_record_container_id`),
  CONSTRAINT `fk_cashless_payment__registry_record_container` FOREIGN KEY (`registry_record_container_id`) REFERENCES `registry_record_container` (`id`) ON UPDATE SET NULL,
  CONSTRAINT `fk_cashless_payment__organization` FOREIGN KEY (`payment_collector_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_cashless_payment__sp_account` FOREIGN KEY (`service_provider_account_id`) REFERENCES `service_provider_account` (`object_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Безналичные оплаты';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cashless_payment`
--

LOCK TABLES `cashless_payment` WRITE;
/*!40000 ALTER TABLE `cashless_payment` DISABLE KEYS */;
INSERT INTO `cashless_payment` VALUES (1,192,82,22.02,'22222','2008-06-25 07:41:03',NULL),(2,195,82,22.02,'22222','2008-06-25 07:41:03',NULL);
/*!40000 ALTER TABLE `cashless_payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `charge`
--

DROP TABLE IF EXISTS `charge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `charge` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `service_provider_account_id` bigint(20) NOT NULL,
  `amount` decimal(19,2) NOT NULL,
  `date_formation` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `registry_record_container_id` bigint(20) DEFAULT NULL COMMENT '????????????? ?????????? ?????? ??????? ????????? ?????????',
  PRIMARY KEY (`id`),
  UNIQUE KEY `charge_unique_sp_account__date_formation` (`service_provider_account_id`,`date_formation`),
  KEY `fk_charge__registry_record_container` (`registry_record_container_id`),
  CONSTRAINT `fk_charge__registry_record_container` FOREIGN KEY (`registry_record_container_id`) REFERENCES `registry_record_container` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_charge__sp_account` FOREIGN KEY (`service_provider_account_id`) REFERENCES `service_provider_account` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Начисление';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `charge`
--

LOCK TABLES `charge` WRITE;
/*!40000 ALTER TABLE `charge` DISABLE KEYS */;
/*!40000 ALTER TABLE `charge` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `city`
--

DROP TABLE IF EXISTS `city`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `city` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор сущности родительского объекта: 700 - region',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  `permission_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'Ключ прав доступа к объекту',
  `external_id` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_city__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`),
  CONSTRAINT `ft_city__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Населенный пункт';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `city`
--

LOCK TABLES `city` WRITE;
/*!40000 ALTER TABLE `city` DISABLE KEYS */;
INSERT INTO `city` VALUES (1,1,1,700,'2014-07-25 13:43:50',NULL,1,0,'3659391');
/*!40000 ALTER TABLE `city` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `city_attribute`
--

DROP TABLE IF EXISTS `city_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `city_attribute` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` bigint(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 400 - НАИМЕНОВАНИЕ НАСЕЛЕННОГО ПУНКТА, 401 - ТИП НАСЕЛЕННОГО ПУНКТА',
  `value_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор значения',
  `value_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа значения атрибута: 400 - string_value, 401 - city_type',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_city_attribute__city` FOREIGN KEY (`object_id`) REFERENCES `city` (`object_id`),
  CONSTRAINT `fk_city_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`) REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_city_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`) REFERENCES `entity_attribute_value_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Атрибуты населенного пункта';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `city_attribute`
--

LOCK TABLES `city_attribute` WRITE;
/*!40000 ALTER TABLE `city_attribute` DISABLE KEYS */;
INSERT INTO `city_attribute` VALUES (1,1,1,400,1,400,'2014-07-25 13:43:50',NULL,1),(2,1,1,401,1,401,'2014-07-25 13:43:50',NULL,1),(3,1,1,402,2,402,'2014-08-19 07:25:49',NULL,1);
/*!40000 ALTER TABLE `city_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `city_correction`
--

DROP TABLE IF EXISTS `city_correction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `city_correction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта населенного пункта',
  `external_id` varchar(20) DEFAULT NULL COMMENT 'Внешний идентификатор объекта',
  `correction` varchar(100) NOT NULL COMMENT 'Название населенного пункта',
  `begin_date` date NOT NULL DEFAULT '1970-01-01' COMMENT 'Дата начала актуальности соответствия',
  `end_date` date NOT NULL DEFAULT '2054-12-31' COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` bigint(20) NOT NULL COMMENT 'Идентификатор организации',
  `user_organization_id` bigint(20) DEFAULT NULL,
  `module_id` bigint(20) NOT NULL COMMENT 'Идентификатор модуля',
  `status` int(11) DEFAULT NULL COMMENT 'Статус',
  PRIMARY KEY (`id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_begin_date` (`begin_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  KEY `key_module_id` (`module_id`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_city_correction__city` FOREIGN KEY (`object_id`) REFERENCES `city` (`object_id`),
  CONSTRAINT `fk_city_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_city_correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='Коррекция населенного пункта';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `city_correction`
--

LOCK TABLES `city_correction` WRITE;
/*!40000 ALTER TABLE `city_correction` DISABLE KEYS */;
INSERT INTO `city_correction` VALUES (1,1,NULL,'','1970-01-01','2054-12-31',7,81,0,1),(2,1,NULL,'','1970-01-01','2054-12-31',82,81,0,1);
/*!40000 ALTER TABLE `city_correction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `city_string_value`
--

DROP TABLE IF EXISTS `city_string_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `city_string_value` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` bigint(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` bigint(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_city_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Локализация атрибутов населенного пункта';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `city_string_value`
--

LOCK TABLES `city_string_value` WRITE;
/*!40000 ALTER TABLE `city_string_value` DISABLE KEYS */;
INSERT INTO `city_string_value` VALUES (1,1,1,'ХАРЬКОВ'),(2,2,1,'057');
/*!40000 ALTER TABLE `city_string_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `city_type`
--

DROP TABLE IF EXISTS `city_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `city_type` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор родительского объекта: не используется',
  `parent_entity_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор сущности родительского объекта: не используется',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  `permission_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'Ключ прав доступа к объекту',
  `external_id` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_city_type__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_city_type__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Тип населенного пункта';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `city_type`
--

LOCK TABLES `city_type` WRITE;
/*!40000 ALTER TABLE `city_type` DISABLE KEYS */;
INSERT INTO `city_type` VALUES (1,1,NULL,NULL,'2014-07-25 13:43:49',NULL,1,0,'5438');
/*!40000 ALTER TABLE `city_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `city_type_attribute`
--

DROP TABLE IF EXISTS `city_type_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `city_type_attribute` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` bigint(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 1300 - КРАТКОЕ НАЗВАНИЕ, 1301 - НАЗВАНИЕ',
  `value_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор значения',
  `value_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа значения атрибута: 1300 - string_value, 1301 - string_value',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_city_type_attribute__city_type` FOREIGN KEY (`object_id`) REFERENCES `city_type` (`object_id`),
  CONSTRAINT `fk_city_type_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`) REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_city_type_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`) REFERENCES `entity_attribute_value_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Атрибуты типа населенного пункта';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `city_type_attribute`
--

LOCK TABLES `city_type_attribute` WRITE;
/*!40000 ALTER TABLE `city_type_attribute` DISABLE KEYS */;
INSERT INTO `city_type_attribute` VALUES (1,1,1,1300,1,1300,'2014-07-25 13:43:49',NULL,1),(2,1,1,1301,2,1301,'2014-07-25 13:43:49',NULL,1);
/*!40000 ALTER TABLE `city_type_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `city_type_correction`
--

DROP TABLE IF EXISTS `city_type_correction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `city_type_correction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта типа населенного пункта',
  `external_id` varchar(20) DEFAULT NULL COMMENT 'Внешний идентификатор объекта',
  `correction` varchar(100) NOT NULL COMMENT 'Название типа населенного пункта',
  `begin_date` date NOT NULL DEFAULT '1970-01-01' COMMENT 'Дата начала актуальности соответствия',
  `end_date` date NOT NULL DEFAULT '2054-12-31' COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` bigint(20) NOT NULL COMMENT 'Идентификатор организации',
  `user_organization_id` bigint(20) DEFAULT NULL,
  `module_id` bigint(20) NOT NULL COMMENT 'Идентификатор модуля',
  `status` int(11) DEFAULT NULL COMMENT 'Статус',
  PRIMARY KEY (`id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_begin_date` (`begin_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  KEY `key_module_id` (`module_id`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_city_type_correction__city_type` FOREIGN KEY (`object_id`) REFERENCES `city_type` (`object_id`),
  CONSTRAINT `fk_city_type_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_city_type__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Коррекция типа населенного пункта';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `city_type_correction`
--

LOCK TABLES `city_type_correction` WRITE;
/*!40000 ALTER TABLE `city_type_correction` DISABLE KEYS */;
/*!40000 ALTER TABLE `city_type_correction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `city_type_string_value`
--

DROP TABLE IF EXISTS `city_type_string_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `city_type_string_value` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` bigint(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` bigint(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_city_type_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Локализация атрибутов типа населенного пункта';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `city_type_string_value`
--

LOCK TABLES `city_type_string_value` WRITE;
/*!40000 ALTER TABLE `city_type_string_value` DISABLE KEYS */;
INSERT INTO `city_type_string_value` VALUES (1,1,1,'Г'),(2,2,1,'ГОРОД');
/*!40000 ALTER TABLE `city_type_string_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `config`
--

DROP TABLE IF EXISTS `config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор настройки',
  `name` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Имя',
  `value` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_key` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Настройки';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config`
--

LOCK TABLES `config` WRITE;
/*!40000 ALTER TABLE `config` DISABLE KEYS */;
INSERT INTO `config` VALUES (1,'IMPORT_FILE_STORAGE_DIR','/var/tmp/data'),(2,'SYNC_DATA_SOURCE','jdbc/eircConnectionRemoteResource'),(3,'MODULE_ID','1'),(4,'TMP_DIR','/tmp'),(5,'NUMBER_FLUSH_REGISTRY_RECORDS','1000'),(6,'NUMBER_READ_CHARS','32000');
/*!40000 ALTER TABLE `config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `container_type`
--

DROP TABLE IF EXISTS `container_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `container_type` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` bigint(20) NOT NULL COMMENT 'Registry container type code',
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `container_type`
--

LOCK TABLES `container_type` WRITE;
/*!40000 ALTER TABLE `container_type` DISABLE KEYS */;
INSERT INTO `container_type` VALUES (1,0,''),(2,1,'Открытие лицевого счета'),(3,2,'Закрытие лицевого счета'),(4,3,'ФИО основного квартиросъемщика'),(5,4,'Кол-во проживающих'),(6,5,'Площадь общая'),(7,6,'Площадь жилая'),(8,7,'Площадь отапливаемая'),(9,8,'Тип льготы'),(10,9,'ФИО носителя льготы'),(11,10,'ИНН носителя льготы'),(12,11,'Документ подтверждающий право на льготу'),(13,12,'Кол-во пользующихся льготой'),(14,13,'Изменение номера лицевого счета'),(15,14,'Добавление подуслуги на лицевой счет'),(16,15,'Номер лицевого счета сторонней организации'),(17,50,'Наличная оплата'),(18,51,'Безналичная оплата'),(19,52,'Оплата банка'),(20,100,'Базовый'),(21,101,'Начисление опер.месяца'),(22,102,'Исх.сальдо опер.месяца '),(23,150,'Принятие здания на обслуживание'),(24,151,'Снятие здания с обслуживания'),(25,500,'Идентификатор ППП'),(26,501,'Аннотация к реестру'),(27,502,'Синхронизация идентификаторов'),(28,503,'Номер экземпляра приложения'),(29,600,'Кол-во проживающих'),(30,601,'Кол-во зарегистрированных'),(31,602,'Общая площадь (приведенная)'),(32,603,'Жилая площадь'),(33,604,'Отапливаемая площадь'),(34,504,'????????? ?????????? ?????????');
/*!40000 ALTER TABLE `container_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `country`
--

DROP TABLE IF EXISTS `country`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `country` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор родительского объекта: не используется',
  `parent_entity_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор сущности родительского объекта: не используется',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  `permission_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'Ключ прав доступа к объекту',
  `external_id` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_country__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_country__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Страна';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `country`
--

LOCK TABLES `country` WRITE;
/*!40000 ALTER TABLE `country` DISABLE KEYS */;
INSERT INTO `country` VALUES (1,1,NULL,NULL,'2014-07-25 13:43:49',NULL,1,0,'1');
/*!40000 ALTER TABLE `country` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `country_attribute`
--

DROP TABLE IF EXISTS `country_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `country_attribute` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` bigint(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 800 - НАИМЕНОВАНИЕ СТРАНЫ',
  `value_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор значения',
  `value_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа значения атрибута: 800 - string_value',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_country_attribute__country` FOREIGN KEY (`object_id`) REFERENCES `country` (`object_id`),
  CONSTRAINT `fk_country_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`) REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_country_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`) REFERENCES `entity_attribute_value_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Атрибуты страны';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `country_attribute`
--

LOCK TABLES `country_attribute` WRITE;
/*!40000 ALTER TABLE `country_attribute` DISABLE KEYS */;
INSERT INTO `country_attribute` VALUES (1,1,1,800,1,800,'2014-07-25 13:43:49',NULL,1);
/*!40000 ALTER TABLE `country_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `country_string_value`
--

DROP TABLE IF EXISTS `country_string_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `country_string_value` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` bigint(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` bigint(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_country_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Локализация атрибутов страны';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `country_string_value`
--

LOCK TABLES `country_string_value` WRITE;
/*!40000 ALTER TABLE `country_string_value` DISABLE KEYS */;
INSERT INTO `country_string_value` VALUES (1,1,1,'УКРАИНА');
/*!40000 ALTER TABLE `country_string_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `district`
--

DROP TABLE IF EXISTS `district`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `district` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор сущности родительского объекта: 400 - city',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  `permission_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'Ключ прав доступа к объекту',
  `external_id` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_district__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_district__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Район';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `district`
--

LOCK TABLES `district` WRITE;
/*!40000 ALTER TABLE `district` DISABLE KEYS */;
INSERT INTO `district` VALUES (1,1,1,400,'2014-07-25 13:43:50',NULL,1,0,'3763209'),(2,2,1,400,'2014-07-25 13:43:50',NULL,1,0,'3763197'),(3,3,1,400,'2014-07-25 13:43:50',NULL,1,0,'3763199'),(4,4,1,400,'2014-07-25 13:43:50',NULL,1,0,'3763201'),(5,5,1,400,'2014-07-25 13:43:50',NULL,1,0,'3763203'),(6,6,1,400,'2014-07-25 13:43:50',NULL,1,0,'3763205'),(7,7,1,400,'2014-07-25 13:43:50',NULL,1,0,'3763195'),(8,8,1,400,'2014-07-25 13:43:50',NULL,1,0,'3763211'),(9,9,1,400,'2014-07-25 13:43:50',NULL,1,0,'3763207');
/*!40000 ALTER TABLE `district` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `district_attribute`
--

DROP TABLE IF EXISTS `district_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `district_attribute` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` bigint(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 600 - НАИМЕНОВАНИЕ РАЙОНА, 601 - КОД РАЙОНА',
  `value_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор значения',
  `value_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа значения атрибута: 600 - string_value, 601 - STRING',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_district_attribute__district` FOREIGN KEY (`object_id`) REFERENCES `district` (`object_id`),
  CONSTRAINT `fk_district_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`) REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_district_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`) REFERENCES `entity_attribute_value_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Атрибуты района';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `district_attribute`
--

LOCK TABLES `district_attribute` WRITE;
/*!40000 ALTER TABLE `district_attribute` DISABLE KEYS */;
INSERT INTO `district_attribute` VALUES (1,1,1,600,1,600,'2014-07-25 13:43:50',NULL,1),(2,1,1,601,2,601,'2014-07-25 13:43:50',NULL,1),(3,1,2,600,3,600,'2014-07-25 13:43:50',NULL,1),(4,1,2,601,4,601,'2014-07-25 13:43:50',NULL,1),(5,1,3,600,5,600,'2014-07-25 13:43:50',NULL,1),(6,1,3,601,6,601,'2014-07-25 13:43:50',NULL,1),(7,1,4,600,7,600,'2014-07-25 13:43:50',NULL,1),(8,1,4,601,8,601,'2014-07-25 13:43:50',NULL,1),(9,1,5,600,9,600,'2014-07-25 13:43:50',NULL,1),(10,1,5,601,10,601,'2014-07-25 13:43:50',NULL,1),(11,1,6,600,11,600,'2014-07-25 13:43:50',NULL,1),(12,1,6,601,12,601,'2014-07-25 13:43:50',NULL,1),(13,1,7,600,13,600,'2014-07-25 13:43:50',NULL,1),(14,1,7,601,14,601,'2014-07-25 13:43:50',NULL,1),(15,1,8,600,15,600,'2014-07-25 13:43:50',NULL,1),(16,1,8,601,16,601,'2014-07-25 13:43:50',NULL,1),(17,1,9,600,17,600,'2014-07-25 13:43:50',NULL,1),(18,1,9,601,18,601,'2014-07-25 13:43:50',NULL,1);
/*!40000 ALTER TABLE `district_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `district_correction`
--

DROP TABLE IF EXISTS `district_correction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `district_correction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `city_object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта населенного пункта',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта района',
  `external_id` varchar(20) DEFAULT NULL COMMENT 'Внешний идентификатор объекта',
  `correction` varchar(100) NOT NULL COMMENT 'Название типа населенного пункта',
  `begin_date` date NOT NULL DEFAULT '1970-01-01' COMMENT 'Дата начала актуальности соответствия',
  `end_date` date NOT NULL DEFAULT '2054-12-31' COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` bigint(20) NOT NULL COMMENT 'Идентификатор организации',
  `user_organization_id` bigint(20) DEFAULT NULL,
  `module_id` bigint(20) NOT NULL COMMENT 'Идентификатор модуля',
  `status` int(11) DEFAULT NULL COMMENT 'Статус',
  PRIMARY KEY (`id`),
  KEY `key_city_object_id` (`city_object_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_begin_date` (`begin_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  KEY `key_module_id` (`module_id`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_district_correction__city` FOREIGN KEY (`city_object_id`) REFERENCES `city` (`object_id`),
  CONSTRAINT `fk_district_correction__district` FOREIGN KEY (`object_id`) REFERENCES `district` (`object_id`),
  CONSTRAINT `fk_district_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_district_correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Коррекция района';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `district_correction`
--

LOCK TABLES `district_correction` WRITE;
/*!40000 ALTER TABLE `district_correction` DISABLE KEYS */;
/*!40000 ALTER TABLE `district_correction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `district_string_value`
--

DROP TABLE IF EXISTS `district_string_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `district_string_value` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` bigint(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` bigint(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_district_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Локализация атрибутов района';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `district_string_value`
--

LOCK TABLES `district_string_value` WRITE;
/*!40000 ALTER TABLE `district_string_value` DISABLE KEYS */;
INSERT INTO `district_string_value` VALUES (1,1,1,'ДЗЕРЖИНСКИЙ'),(2,2,1,'DZ'),(3,3,1,'КИЕВСКИЙ'),(4,4,1,'KI'),(5,5,1,'КОМИНТЕРНОВСКИЙ'),(6,6,1,'KO'),(7,7,1,'ЛЕНИНСКИЙ'),(8,8,1,'LE'),(9,9,1,'МОСКОВСКИЙ'),(10,10,1,'MO'),(11,11,1,'ОКТЯБРЬСКИЙ'),(12,12,1,'OK'),(13,13,1,'ОРДЖОНИКИДЗЕВСКИЙ'),(14,14,1,'OR'),(15,15,1,'ФРУНЗЕНСКИЙ'),(16,16,1,'FR'),(17,17,1,'ЧЕРВОНОЗАВОДСКИЙ'),(18,18,1,'CH');
/*!40000 ALTER TABLE `district_string_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `district_sync`
--

DROP TABLE IF EXISTS `district_sync`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `district_sync` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор синхронизации района',
  `object_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор объекта района',
  `city_object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта города',
  `external_id` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Код района (ID)',
  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Название района',
  `date` datetime NOT NULL COMMENT 'Дата актуальности',
  `status` int(11) NOT NULL COMMENT 'Статус синхронизации',
  PRIMARY KEY (`id`),
  KEY `key_city_object_id` (`city_object_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_external_id` (`external_id`),
  KEY `key_name` (`name`),
  KEY `key_date` (`date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_district_sync__city` FOREIGN KEY (`city_object_id`) REFERENCES `city` (`object_id`),
  CONSTRAINT `fk_district_sync__district` FOREIGN KEY (`object_id`) REFERENCES `district` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Синхронизация районов';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `district_sync`
--

LOCK TABLES `district_sync` WRITE;
/*!40000 ALTER TABLE `district_sync` DISABLE KEYS */;
/*!40000 ALTER TABLE `district_sync` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eirc_account`
--

DROP TABLE IF EXISTS `eirc_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eirc_account` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `object_id` bigint(20) NOT NULL,
  `account_number` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `address_id` bigint(20) NOT NULL,
  `address_entity_id` bigint(20) NOT NULL,
  `first_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `last_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `middle_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `begin_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`begin_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_begin_date` (`begin_date`),
  KEY `key_end_date` (`end_date`),
  KEY `fk_eirc_account__entity` (`address_entity_id`),
  CONSTRAINT `fk_eirc_account__entity` FOREIGN KEY (`address_entity_id`) REFERENCES `entity` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=196 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eirc_account`
--

LOCK TABLES `eirc_account` WRITE;
/*!40000 ALTER TABLE `eirc_account` DISABLE KEYS */;
INSERT INTO `eirc_account` VALUES (1,1,'TestEircAccount1',1,100,NULL,NULL,NULL,'2014-07-28 02:32:04','2014-08-19 07:24:30'),(2,2,'TestEircAccount1',1,100,NULL,NULL,NULL,'2014-07-28 03:02:42','2014-08-19 07:24:30'),(3,3,'TestEircAccount1',1,100,NULL,NULL,NULL,'2014-07-28 03:04:41','2014-08-19 07:24:23'),(4,4,'TestEircAccount1',1,100,NULL,NULL,NULL,'2014-07-28 03:17:44','2014-08-19 07:24:16'),(5,5,'TestEircAccount1',1,100,NULL,NULL,NULL,'2014-07-28 03:19:09','2014-08-19 07:24:23'),(6,6,'TestEircAccount1',1,100,NULL,NULL,NULL,'2014-07-28 03:46:51','2014-08-19 07:24:30'),(7,7,'TestEircAccount1',1,100,NULL,NULL,NULL,'2014-07-28 03:49:49','2014-08-19 07:24:30'),(8,8,'TestEircAccount1',1,100,NULL,NULL,NULL,'2014-07-28 04:11:31','2014-08-19 07:24:23'),(9,9,'TestEircAccount1',1,100,NULL,NULL,NULL,'2014-07-28 04:53:12','2014-08-19 07:24:23'),(10,10,'TestEircAccount1',1,100,NULL,NULL,NULL,'2014-07-29 12:53:35','2014-08-19 07:24:30'),(11,11,'TestEircAccount1',1,100,NULL,NULL,NULL,'2014-07-29 12:55:00','2014-08-19 07:24:30'),(12,12,'TestEircAccount1',1,100,NULL,NULL,NULL,'2014-07-30 02:40:53','2014-08-19 07:24:23'),(13,13,'TestEircAccount1',1,100,NULL,NULL,NULL,'2014-07-30 03:54:25','2014-08-19 07:24:24'),(14,14,'TestEircAccount1',1,100,NULL,NULL,NULL,'2014-07-30 11:59:02','2014-08-19 07:24:30'),(15,15,'TestEircAccount1',1,100,NULL,NULL,NULL,'2014-07-30 11:59:58','2014-08-19 07:24:30'),(16,16,'TestEircAccount1',1,100,NULL,NULL,NULL,'2014-08-13 04:07:33','2014-08-19 07:24:23'),(17,17,'TestEircAccount1',1,100,NULL,NULL,NULL,'2014-08-13 04:08:35','2014-08-19 07:24:23'),(18,18,'TestEircAccount1',1,100,NULL,NULL,NULL,'2014-08-13 04:08:58','2014-08-19 07:24:30'),(19,19,'TestEircAccount1',1,100,NULL,NULL,NULL,'2014-08-13 04:29:01','2014-08-19 07:24:16'),(20,20,'TestEircAccount1',1,100,NULL,NULL,NULL,'2014-08-13 04:35:19','2014-08-19 07:24:30'),(21,21,'TestEircAccount1',1,100,NULL,NULL,NULL,'2014-08-13 06:14:33','2014-08-19 07:24:23'),(22,22,'TestEircAccount1',1,100,NULL,NULL,NULL,'2014-08-13 06:19:09','2014-08-19 07:24:24'),(28,23,'57000000011',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 07:43:52','2014-08-19 08:30:03'),(29,24,'57000000028',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 07:43:52','2014-08-19 08:30:03'),(30,25,'57000000035',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 07:43:52','2014-08-19 08:30:03'),(31,26,'57000000042',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 09:04:27','2014-08-19 09:05:29'),(32,27,'57000000059',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 09:04:27','2014-08-19 09:05:29'),(33,28,'57000000066',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 09:04:27','2014-08-19 09:05:29'),(34,29,'57000000073',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 09:08:29','2014-08-19 09:08:31'),(35,30,'57000000080',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 09:08:30','2014-08-19 09:08:31'),(36,31,'57000000097',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 09:08:30','2014-08-19 09:08:31'),(37,32,'57000000103',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 09:13:36','2014-08-19 09:13:40'),(38,33,'57000000110',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 09:13:36','2014-08-19 09:13:40'),(39,34,'57000000127',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 09:13:36','2014-08-19 09:13:39'),(40,35,'57000000134',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 09:16:10','2014-08-19 09:16:14'),(41,36,'57000000141',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 09:16:10','2014-08-19 09:16:14'),(42,37,'57000000158',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 09:16:10','2014-08-19 09:16:14'),(43,38,'57000000165',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 09:17:44','2014-08-19 09:17:48'),(44,39,'57000000172',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 09:17:44','2014-08-19 09:17:48'),(45,40,'57000000189',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 09:17:44','2014-08-19 09:17:47'),(46,41,'57000000196',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 09:20:28','2014-08-19 09:20:32'),(47,42,'57000000202',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 09:20:28','2014-08-19 09:20:32'),(48,43,'57000000219',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 09:20:28','2014-08-19 09:20:32'),(49,44,'57000000226',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 09:28:31','2014-08-19 09:28:34'),(50,45,'57000000233',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 09:28:31','2014-08-19 09:28:34'),(51,46,'57000000240',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 09:28:31','2014-08-19 09:28:34'),(52,47,'57000000257',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 09:31:12','2014-08-19 09:36:53'),(53,48,'57000000264',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 09:31:12','2014-08-19 09:36:53'),(54,49,'57000000271',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 09:31:12','2014-08-19 09:36:53'),(55,50,'57000000288',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 09:37:18','2014-08-19 09:37:19'),(56,51,'57000000295',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 09:37:18','2014-08-19 09:37:19'),(57,52,'57000000301',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 09:37:18','2014-08-19 09:37:19'),(58,53,'57000000318',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 09:38:00','2014-08-19 09:40:41'),(59,54,'57000000325',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 09:38:00','2014-08-19 09:40:41'),(60,55,'57000000332',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 09:38:00','2014-08-19 09:40:41'),(61,56,'57000000349',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 09:42:10','2014-08-19 09:42:16'),(62,57,'57000000356',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 09:42:10','2014-08-19 09:42:16'),(63,58,'57000000363',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 09:42:10','2014-08-19 09:42:16'),(64,59,'57000000370',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 09:50:11','2014-08-19 09:50:17'),(65,60,'57000000387',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 09:50:11','2014-08-19 09:50:17'),(66,61,'57000000394',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 09:50:11','2014-08-19 09:50:17'),(67,62,'57000000400',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 09:54:58','2014-08-19 09:55:04'),(68,63,'57000000417',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 09:54:58','2014-08-19 09:55:04'),(69,64,'57000000424',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 09:54:58','2014-08-19 09:55:04'),(70,65,'57000000431',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 09:58:26','2014-08-19 10:00:10'),(71,66,'57000000448',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 09:58:26','2014-08-19 10:00:10'),(72,67,'57000000455',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 09:58:26','2014-08-19 10:00:10'),(73,68,'57000000462',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 10:08:40','2014-08-19 10:09:38'),(74,69,'57000000479',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 10:08:40','2014-08-19 10:09:38'),(75,70,'57000000486',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 10:08:40','2014-08-19 10:09:38'),(76,71,'57000000493',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 10:10:13','2014-08-19 10:15:51'),(77,72,'57000000509',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 10:10:13','2014-08-19 10:15:51'),(78,73,'57000000516',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 10:10:13','2014-08-19 10:15:51'),(79,74,'57000000523',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 10:16:09','2014-08-19 10:18:06'),(80,75,'57000000530',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 10:16:09','2014-08-19 10:18:06'),(81,76,'57000000547',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 10:16:09','2014-08-19 10:18:06'),(82,77,'57000000554',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 10:18:34','2014-08-19 11:07:58'),(83,78,'57000000561',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 10:18:34','2014-08-19 11:07:58'),(84,79,'57000000578',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 10:18:34','2014-08-19 11:07:58'),(85,80,'57000000585',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 11:08:47','2014-08-19 11:08:51'),(86,81,'57000000592',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 11:08:47','2014-08-19 11:08:50'),(87,82,'57000000608',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 11:08:47','2014-08-19 11:08:50'),(88,83,'57000000615',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 11:09:38','2014-08-19 11:13:35'),(89,84,'57000000622',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 11:09:38','2014-08-19 11:13:35'),(90,85,'57000000639',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 11:09:38','2014-08-19 11:13:34'),(91,86,'57000000646',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 11:13:55','2014-08-19 11:14:01'),(92,87,'57000000653',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 11:13:55','2014-08-19 11:14:01'),(93,88,'57000000660',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 11:13:55','2014-08-19 11:14:01'),(94,89,'57000000677',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 11:14:44','2014-08-19 11:18:05'),(95,90,'57000000684',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 11:14:44','2014-08-19 11:18:04'),(96,91,'57000000691',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 11:14:44','2014-08-19 11:18:04'),(97,92,'57000000707',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 11:18:31','2014-08-19 11:18:37'),(98,93,'57000000714',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 11:18:31','2014-08-19 11:18:37'),(99,94,'57000000721',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 11:18:31','2014-08-19 11:18:37'),(100,95,'57000000738',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 11:19:06','2014-08-19 11:19:12'),(101,96,'57000000745',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 11:19:06','2014-08-19 11:19:12'),(102,97,'57000000752',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 11:19:06','2014-08-19 11:19:12'),(103,98,'57000000769',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 11:19:29','2014-08-19 11:19:34'),(104,99,'57000000776',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 11:19:29','2014-08-19 11:19:34'),(105,100,'57000000783',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 11:19:29','2014-08-19 11:19:34'),(106,101,'57000000790',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 11:24:42','2014-08-19 11:24:47'),(107,102,'57000000806',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 11:24:42','2014-08-19 11:24:47'),(108,103,'57000000813',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 11:24:42','2014-08-19 11:24:47'),(109,104,'57000000820',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 11:26:42','2014-08-19 11:26:48'),(110,105,'57000000837',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 11:26:42','2014-08-19 11:26:48'),(111,106,'57000000844',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 11:26:42','2014-08-19 11:26:48'),(112,107,'57000000851',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 11:28:24','2014-08-19 11:28:29'),(113,108,'57000000868',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 11:28:24','2014-08-19 11:28:29'),(114,109,'57000000875',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 11:28:24','2014-08-19 11:28:29'),(115,110,'57000000882',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 11:29:00','2014-08-19 11:29:06'),(116,111,'57000000899',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 11:29:00','2014-08-19 11:29:06'),(117,112,'57000000905',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 11:29:00','2014-08-19 11:29:06'),(118,113,'57000000912',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 11:29:34','2014-08-19 11:29:40'),(119,114,'57000000929',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 11:29:34','2014-08-19 11:29:39'),(120,115,'57000000936',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 11:29:34','2014-08-19 11:29:39'),(121,116,'57000000943',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 11:30:02','2014-08-19 11:30:08'),(122,117,'57000000950',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 11:30:02','2014-08-19 11:30:08'),(123,118,'57000000967',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 11:30:02','2014-08-19 11:30:08'),(124,119,'57000000974',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 11:30:42','2014-08-19 11:30:48'),(125,120,'57000000981',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 11:30:42','2014-08-19 11:30:48'),(126,121,'57000000998',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 11:30:42','2014-08-19 11:30:47'),(127,122,'57000001001',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 11:42:53','2014-08-19 11:47:34'),(128,123,'57000001018',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 11:42:53','2014-08-19 11:47:34'),(129,124,'57000001025',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 11:42:53','2014-08-19 11:47:34'),(130,125,'57000001032',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 13:55:59','2014-08-19 13:56:05'),(131,126,'57000001049',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 13:55:59','2014-08-19 13:56:05'),(132,127,'57000001056',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 13:55:59','2014-08-19 13:56:04'),(133,128,'57000001063',1,100,'О','КРАВЧЕНКО','Ф','2014-08-19 14:01:34','2014-08-19 14:01:40'),(134,129,'57000001070',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-19 14:01:34','2014-08-19 14:01:40'),(135,130,'57000001087',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-19 14:01:34','2014-08-19 14:01:40'),(136,131,'57000001094',1,100,'О','КРАВЧЕНКО','Ф','2014-08-21 05:57:38','2014-08-21 05:57:44'),(137,132,'57000001100',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-21 05:57:38','2014-08-21 05:57:44'),(138,133,'57000001117',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-21 05:57:38','2014-08-21 05:57:44'),(139,134,'57000001124',1,100,'О','КРАВЧЕНКО','Ф','2014-08-21 12:17:20','2014-08-21 12:17:24'),(140,135,'57000001131',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-21 12:17:20','2014-08-21 12:17:24'),(141,136,'57000001148',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-21 12:17:20','2014-08-21 12:17:24'),(142,137,'57000001155',1,100,'О','КРАВЧЕНКО','Ф','2014-08-21 12:18:59','2014-08-21 12:19:03'),(143,138,'57000001162',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-21 12:18:59','2014-08-21 12:19:03'),(144,139,'57000001179',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-21 12:18:59','2014-08-21 12:19:03'),(145,140,'57000001186',1,100,'О','КРАВЧЕНКО','Ф','2014-08-21 12:23:02','2014-08-21 12:23:05'),(146,141,'57000001193',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-21 12:23:02','2014-08-21 12:23:05'),(147,142,'57000001209',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-21 12:23:02','2014-08-21 12:23:05'),(148,143,'57000001216',1,100,'О','КРАВЧЕНКО','Ф','2014-08-25 09:03:43','2014-08-25 09:03:44'),(149,144,'57000001223',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-25 09:03:43','2014-08-25 09:03:44'),(150,145,'57000001230',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-25 09:03:43','2014-08-25 09:03:44'),(151,146,'57000001247',1,100,'О','КРАВЧЕНКО','Ф','2014-08-25 09:08:41','2014-08-25 09:08:43'),(152,147,'57000001254',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-25 09:08:41','2014-08-25 09:08:43'),(153,148,'57000001261',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-25 09:08:41','2014-08-25 09:08:43'),(154,149,'57000001278',1,100,'О','КРАВЧЕНКО','Ф','2014-08-25 09:16:23','2014-08-25 09:23:13'),(155,150,'57000001285',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-25 09:16:23','2014-08-25 09:23:13'),(156,151,'57000001292',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-25 09:16:23','2014-08-25 09:23:13'),(157,152,'57000001308',1,100,'О','КРАВЧЕНКО','Ф','2014-08-25 09:26:26','2014-08-25 09:26:45'),(158,153,'57000001315',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-25 09:26:26','2014-08-25 09:26:45'),(159,154,'57000001322',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-25 09:26:26','2014-08-25 09:26:45'),(160,155,'57000001339',1,100,'О','КРАВЧЕНКО','Ф','2014-08-25 09:31:39','2014-08-25 09:31:41'),(161,156,'57000001346',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-25 09:31:39','2014-08-25 09:31:41'),(162,157,'57000001353',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-25 09:31:39','2014-08-25 09:31:40'),(163,158,'57000001360',1,100,'О','КРАВЧЕНКО','Ф','2014-08-25 09:48:26','2014-08-25 09:48:28'),(164,159,'57000001377',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-25 09:48:26','2014-08-25 09:48:28'),(165,160,'57000001384',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-25 09:48:26','2014-08-25 09:48:28'),(166,161,'57000001391',1,100,'О','КРАВЧЕНКО','Ф','2014-08-25 10:19:08','2014-08-25 10:19:18'),(167,162,'57000001407',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-25 10:19:08','2014-08-25 10:19:18'),(168,163,'57000001414',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-25 10:19:08','2014-08-25 10:19:18'),(169,164,'57000001421',1,100,'О','КРАВЧЕНКО','Ф','2014-08-25 10:19:37','2014-08-25 10:19:40'),(170,165,'57000001438',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-25 10:19:37','2014-08-25 10:19:40'),(171,166,'57000001445',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-25 10:19:37','2014-08-25 10:19:40'),(172,167,'57000001452',1,100,'О','КРАВЧЕНКО','Ф','2014-08-25 10:23:01','2014-08-25 10:23:24'),(173,168,'57000001469',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-25 10:23:01','2014-08-25 10:23:24'),(174,169,'57000001476',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-25 10:23:01','2014-08-25 10:23:24'),(175,170,'57000001483',1,100,'О','КРАВЧЕНКО','Ф','2014-08-25 10:25:40','2014-08-25 10:25:43'),(176,171,'57000001490',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-25 10:25:40','2014-08-25 10:25:43'),(177,172,'57000001506',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-25 10:25:40','2014-08-25 10:25:43'),(178,173,'57000001513',1,100,'О','КРАВЧЕНКО','Ф','2014-08-25 10:27:46','2014-08-25 10:37:40'),(179,174,'57000001520',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-25 10:27:46','2014-08-25 10:37:40'),(180,175,'57000001537',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-25 10:27:46','2014-08-25 10:37:40'),(181,176,'57000001544',1,100,'О','КРАВЧЕНКО','Ф','2014-08-25 10:37:59','2014-08-25 10:38:02'),(182,177,'57000001551',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-25 10:37:59','2014-08-25 10:38:02'),(183,178,'57000001568',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-25 10:37:59','2014-08-25 10:38:02'),(184,179,'57000001575',1,100,'О','КРАВЧЕНКО','Ф','2014-08-25 10:40:05','2014-08-25 10:40:08'),(185,180,'57000001582',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-25 10:40:05','2014-08-25 10:40:08'),(186,181,'57000001599',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-25 10:40:05','2014-08-25 10:40:08'),(187,182,'57000001605',1,100,'О','КРАВЧЕНКО','Ф','2014-08-25 10:43:17','2014-08-25 10:43:20'),(188,183,'57000001612',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-25 10:43:17','2014-08-25 10:43:20'),(189,184,'57000001629',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-25 10:43:17','2014-08-25 10:43:20'),(190,185,'57000001636',1,100,'О','КРАВЧЕНКО','Ф','2014-08-25 10:46:19','2014-08-25 10:51:41'),(191,186,'57000001643',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-25 10:46:19','2014-08-25 10:51:41'),(192,187,'57000001650',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-25 10:46:19','2014-08-25 10:51:41'),(193,188,'57000001667',1,100,'О','КРАВЧЕНКО','Ф','2014-08-25 10:52:02','2014-08-25 10:52:06'),(194,189,'57000001674',2,100,'С','МИХАЙЛИЧЕНКО','В','2014-08-25 10:52:02','2014-08-25 10:52:06'),(195,190,'57000001681',3,100,'Т','СЛЮСАРЕНКО','Н','2014-08-25 10:52:02','2014-08-25 10:52:06');
/*!40000 ALTER TABLE `eirc_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entity`
--

DROP TABLE IF EXISTS `entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор сущности',
  `entity` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Название таблицы сущности',
  `name_id` bigint(20) NOT NULL COMMENT 'Идентификатор локализации названия сущности',
  `strategy_factory` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Не используется',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_entity` (`entity`),
  KEY `key_name_id` (`name_id`),
  CONSTRAINT `fk_entity__string_value` FOREIGN KEY (`name_id`) REFERENCES `entity_string_value` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6001 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Сущность';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entity`
--

LOCK TABLES `entity` WRITE;
/*!40000 ALTER TABLE `entity` DISABLE KEYS */;
INSERT INTO `entity` VALUES (100,'apartment',100,''),(200,'room',200,''),(300,'street',300,''),(400,'city',400,''),(500,'building',500,''),(600,'district',600,''),(700,'region',700,''),(800,'country',800,''),(900,'organization',900,''),(1000,'user_info',1000,''),(1010,'module_instance',1010,''),(1110,'module_instance_type',1110,''),(1300,'city_type',1300,''),(1400,'street_type',1400,''),(1500,'building_address',1500,''),(2300,'organization_type',2300,''),(6000,'service_provider_account',6000,'');
/*!40000 ALTER TABLE `entity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entity_attribute_type`
--

DROP TABLE IF EXISTS `entity_attribute_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entity_attribute_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор типа атрибута',
  `entity_id` bigint(20) NOT NULL COMMENT 'Идентификатор сущности',
  `mandatory` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Является ли атрибут обязательным',
  `attribute_type_name_id` bigint(20) NOT NULL COMMENT 'Идентификатор локализации названия атрибута',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия типа атрибута',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия типа атрибута',
  `system` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Является ли тип атрибута системным',
  PRIMARY KEY (`id`),
  KEY `key_entity_id` (`entity_id`),
  KEY `key_attribute_type_name_id` (`attribute_type_name_id`),
  CONSTRAINT `fk_entity_attribute_type__entity` FOREIGN KEY (`entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_entity_attribute_type__string_value` FOREIGN KEY (`attribute_type_name_id`) REFERENCES `entity_string_value` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6006 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Тип атрибута сущности';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entity_attribute_type`
--

LOCK TABLES `entity_attribute_type` WRITE;
/*!40000 ALTER TABLE `entity_attribute_type` DISABLE KEYS */;
INSERT INTO `entity_attribute_type` VALUES (100,100,1,101,'2014-07-25 03:38:43',NULL,1),(200,200,1,201,'2014-07-25 03:38:43',NULL,1),(300,300,1,301,'2014-07-25 03:38:43',NULL,1),(301,300,1,302,'2014-07-25 03:38:43',NULL,1),(400,400,1,401,'2014-07-25 03:38:43',NULL,1),(401,400,1,402,'2014-07-25 03:38:43',NULL,1),(402,400,0,403,'2014-07-25 03:38:43',NULL,1),(500,500,0,501,'2014-07-25 03:38:43',NULL,1),(501,500,0,502,'2014-07-25 03:38:43',NULL,1),(502,500,0,503,'2014-07-25 03:38:43',NULL,1),(600,600,1,601,'2014-07-25 03:38:43',NULL,1),(601,600,1,602,'2014-07-25 03:38:43',NULL,1),(700,700,1,701,'2014-07-25 03:38:43',NULL,1),(800,800,1,801,'2014-07-25 03:38:43',NULL,1),(900,900,1,901,'2014-07-25 03:38:43',NULL,1),(901,900,1,902,'2014-07-25 03:38:43',NULL,1),(902,900,0,903,'2014-07-25 03:38:43',NULL,1),(903,900,0,904,'2014-07-25 03:38:43',NULL,1),(904,900,0,905,'2014-07-25 03:38:43',NULL,1),(906,900,0,906,'2014-07-25 03:38:43',NULL,1),(913,900,1,914,'2014-07-25 03:38:43',NULL,1),(914,900,1,915,'2014-07-25 03:38:43',NULL,1),(915,900,0,916,'2014-07-25 03:38:43',NULL,1),(916,900,1,917,'2014-07-25 03:38:43',NULL,1),(917,900,1,918,'2014-07-25 03:38:43',NULL,1),(918,900,1,919,'2014-07-25 03:38:43',NULL,1),(919,900,0,921,'2014-07-25 03:38:43',NULL,1),(1000,1000,1,1001,'2014-07-25 03:38:43',NULL,1),(1001,1000,1,1002,'2014-07-25 03:38:43',NULL,1),(1002,1000,1,1003,'2014-07-25 03:38:43',NULL,1),(1010,1010,0,1011,'2014-07-25 03:38:43',NULL,1),(1011,1010,0,1012,'2014-07-25 03:38:43',NULL,0),(1012,1010,1,1013,'2014-07-25 03:38:43',NULL,0),(1013,1010,1,1014,'2014-07-25 03:38:43',NULL,1),(1014,1010,1,1015,'2014-07-25 03:38:43',NULL,1),(1110,1110,1,1111,'2014-07-25 03:38:43',NULL,1),(1300,1300,1,1301,'2014-07-25 03:38:43',NULL,1),(1301,1300,1,1302,'2014-07-25 03:38:43',NULL,1),(1400,1400,1,1401,'2014-07-25 03:38:43',NULL,1),(1401,1400,1,1402,'2014-07-25 03:38:43',NULL,1),(1500,1500,1,1501,'2014-07-25 03:38:43',NULL,1),(1501,1500,0,1502,'2014-07-25 03:38:43',NULL,1),(1502,1500,0,1503,'2014-07-25 03:38:43',NULL,1),(2300,2300,1,2301,'2014-07-25 03:38:43',NULL,1),(6001,6000,0,6002,'2014-07-25 03:38:43',NULL,1),(6002,6000,0,6003,'2014-07-25 03:38:43',NULL,1),(6003,6000,0,6004,'2014-07-25 03:38:43',NULL,1),(6004,6000,0,6005,'2014-07-25 03:38:43',NULL,1),(6005,6000,0,6006,'2014-07-25 03:38:43',NULL,1);
/*!40000 ALTER TABLE `entity_attribute_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entity_attribute_value_type`
--

DROP TABLE IF EXISTS `entity_attribute_value_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entity_attribute_value_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор типа значения атрибута',
  `attribute_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `attribute_value_type` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Тип значения атрибута',
  PRIMARY KEY (`id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  CONSTRAINT `fk_entity_attribute_value_type` FOREIGN KEY (`attribute_type_id`) REFERENCES `entity_attribute_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6006 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Тип значения атрибута';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entity_attribute_value_type`
--

LOCK TABLES `entity_attribute_value_type` WRITE;
/*!40000 ALTER TABLE `entity_attribute_value_type` DISABLE KEYS */;
INSERT INTO `entity_attribute_value_type` VALUES (100,100,'STRING_VALUE'),(200,200,'string_value'),(300,300,'STRING_VALUE'),(301,301,'street_type'),(400,400,'STRING_VALUE'),(401,401,'city_type'),(402,402,'STRING'),(500,500,'district'),(501,501,'building_address'),(502,502,'building_organization_association'),(600,600,'STRING_VALUE'),(601,601,'STRING'),(700,700,'STRING_VALUE'),(800,800,'STRING_VALUE'),(900,900,'STRING_VALUE'),(901,901,'STRING'),(902,902,'district'),(903,903,'organization'),(904,904,'organization_type'),(906,906,'STRING_VALUE'),(913,913,'STRING'),(914,914,'STRING'),(915,915,'STRING'),(916,916,'STRING'),(917,917,'STRING'),(918,918,'STRING'),(919,919,'service'),(1000,1000,'last_name'),(1001,1001,'first_name'),(1002,1002,'middle_name'),(1010,1010,'STRING'),(1011,1011,'STRING'),(1012,1012,'STRING'),(1013,1013,'STRING'),(1014,1014,'module_instance_type'),(1110,1110,'STRING_VALUE'),(1300,1300,'STRING_VALUE'),(1301,1301,'STRING_VALUE'),(1400,1400,'STRING_VALUE'),(1401,1401,'STRING_VALUE'),(1500,1500,'STRING_VALUE'),(1501,1501,'STRING_VALUE'),(1502,1502,'STRING_VALUE'),(2300,2300,'STRING_VALUE'),(6001,6001,'STRING'),(6002,6002,'STRING'),(6003,6003,'STRING'),(6004,6004,'STRING'),(6005,6005,'STRING');
/*!40000 ALTER TABLE `entity_attribute_value_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exemption`
--

DROP TABLE IF EXISTS `exemption`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `exemption` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `object_id` bigint(20) NOT NULL,
  `owner_exemption_id` bigint(20) NOT NULL,
  `category` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `number_using` int(11) DEFAULT NULL,
  `begin_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`begin_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_begin_date` (`begin_date`),
  KEY `key_end_date` (`end_date`),
  KEY `fk_exemption__owner_exemption` (`owner_exemption_id`),
  CONSTRAINT `fk_exemption__owner_exemption` FOREIGN KEY (`owner_exemption_id`) REFERENCES `owner_exemption` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Льготы';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exemption`
--

LOCK TABLES `exemption` WRITE;
/*!40000 ALTER TABLE `exemption` DISABLE KEYS */;
/*!40000 ALTER TABLE `exemption` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `first_name`
--

DROP TABLE IF EXISTS `first_name`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `first_name` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор имени',
  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Имя',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Имя';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `first_name`
--

LOCK TABLES `first_name` WRITE;
/*!40000 ALTER TABLE `first_name` DISABLE KEYS */;
INSERT INTO `first_name` VALUES (1,'admin'),(2,'ANONYMOUS'),(3,'Test');
/*!40000 ALTER TABLE `first_name` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `import_error_type`
--

DROP TABLE IF EXISTS `import_error_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `import_error_type` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` bigint(20) NOT NULL COMMENT 'Import error type code',
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `import_error_type`
--

LOCK TABLES `import_error_type` WRITE;
/*!40000 ALTER TABLE `import_error_type` DISABLE KEYS */;
INSERT INTO `import_error_type` VALUES (1,1,'Не найден нас.пункт'),(2,2,'Не найден тип улицы'),(3,3,'Не найдена улица'),(4,4,'Не найдена улица и дом'),(5,5,'Не найден дом'),(6,6,'Не найдена квартира'),(7,7,'Соответствие для нас.пункта не может быть установлено'),(8,8,'Соответствие для типа улицы не может быть установлено'),(9,9,'Соответствие для улицы не может быть установлено'),(10,10,'Соответствие для дома не может быть установлено'),(11,11,'Соответствие для квартиры не может быть установлено'),(12,12,'Более одного соответствия для нас.пункта'),(13,13,'Более одного соответствие для типа улицы'),(14,14,'Более одного соответствие для улицы'),(15,15,'Более одного соответствие для дома'),(16,16,'Более одного соответствие для квартиры'),(17,17,'Несоответствие л/с'),(18,18,'Более одного л/с'),(19,19,'Не найдена комната'),(20,20,'Соответствие для комнаты не может быть установлено'),(21,21,'Более одного соответствие для комнаты');
/*!40000 ALTER TABLE `import_error_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `last_name`
--

DROP TABLE IF EXISTS `last_name`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `last_name` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор фамилии',
  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Фамилия',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Фамилия';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `last_name`
--

LOCK TABLES `last_name` WRITE;
/*!40000 ALTER TABLE `last_name` DISABLE KEYS */;
INSERT INTO `last_name` VALUES (1,'admin'),(2,'ANONYMOUS'),(3,'Test');
/*!40000 ALTER TABLE `last_name` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `locale`
--

DROP TABLE IF EXISTS `locale`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `locale` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор локали',
  `locale` varchar(2) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Код локали',
  `system` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Является ли локаль системной',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_key_locale` (`locale`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Локаль';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `locale`
--

LOCK TABLES `locale` WRITE;
/*!40000 ALTER TABLE `locale` DISABLE KEYS */;
INSERT INTO `locale` VALUES (1,'ru',1),(2,'uk',0);
/*!40000 ALTER TABLE `locale` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `log`
--

DROP TABLE IF EXISTS `log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор записи журнала событий',
  `date` datetime DEFAULT NULL COMMENT 'Дата',
  `login` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Имя пользователя',
  `module` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Название модуля системы',
  `object_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор объекта',
  `controller` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Название класса обработчика',
  `model` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Название класса модели данных',
  `event` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Название события',
  `status` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Статус',
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Описание',
  PRIMARY KEY (`id`),
  KEY `key_login` (`login`),
  KEY `key_date` (`date`),
  KEY `key_controller` (`controller`),
  KEY `key_model` (`model`),
  KEY `key_event` (`event`),
  KEY `key_module` (`module`),
  KEY `key_status` (`status`),
  KEY `key_description` (`description`),
  CONSTRAINT `fk_log__user` FOREIGN KEY (`login`) REFERENCES `user` (`login`)
) ENGINE=InnoDB AUTO_INCREMENT=96 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Журнал событий';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `log`
--

LOCK TABLES `log` WRITE;
/*!40000 ALTER TABLE `log` DISABLE KEYS */;
INSERT INTO `log` VALUES (1,'2014-07-25 10:44:17','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(2,'2014-07-25 19:33:27','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(3,'2014-07-25 19:43:49','admin','org.complitex.address',NULL,'org.complitex.address.service.ImportService','org.complitex.organization.entity.OrganizationImportFile','CREATE','OK','Имя файла: orgs.csv, количество записей: 80'),(4,'2014-07-25 19:43:49','admin','org.complitex.address',NULL,'org.complitex.address.service.ImportService','org.complitex.address.entity.AddressImportFile','CREATE','OK','Имя файла: country.csv, количество записей: 1'),(5,'2014-07-25 19:43:49','admin','org.complitex.address',NULL,'org.complitex.address.service.ImportService','org.complitex.address.entity.AddressImportFile','CREATE','OK','Имя файла: region.csv, количество записей: 1'),(6,'2014-07-25 19:43:50','admin','org.complitex.address',NULL,'org.complitex.address.service.ImportService','org.complitex.address.entity.AddressImportFile','CREATE','OK','Имя файла: city_type.csv, количество записей: 1'),(7,'2014-07-25 19:43:50','admin','org.complitex.address',NULL,'org.complitex.address.service.ImportService','org.complitex.address.entity.AddressImportFile','CREATE','OK','Имя файла: city.csv, количество записей: 1'),(8,'2014-07-25 19:43:50','admin','org.complitex.address',NULL,'org.complitex.address.service.ImportService','org.complitex.address.entity.AddressImportFile','CREATE','OK','Имя файла: district.csv, количество записей: 9'),(9,'2014-07-25 19:43:51','admin','org.complitex.address',NULL,'org.complitex.address.service.ImportService','org.complitex.address.entity.AddressImportFile','CREATE','OK','Имя файла: street_type.csv, количество записей: 17'),(10,'2014-07-25 19:44:42','admin','org.complitex.address',NULL,'org.complitex.address.service.ImportService','org.complitex.address.entity.AddressImportFile','CREATE','OK','Имя файла: street.csv, количество записей: 828'),(11,'2014-07-25 19:44:42','admin','org.complitex.address',NULL,'org.complitex.address.service.ImportService',NULL,'CREATE','ERROR','Связанный объект  не найден при обработки строки 1 файла building.csv'),(12,'2014-07-25 19:49:12','admin','org.complitex.dictionary',81,'org.complitex.dictionary.strategy.web.DomainObjectEditPanel','org.complitex.dictionary.entity.DomainObject#organization','CREATE','OK',NULL),(13,'2014-07-25 19:50:11','admin','org.complitex.dictionary',1,'org.complitex.dictionary.strategy.web.DomainObjectEditPanel','org.complitex.dictionary.entity.DomainObject#module_instance','CREATE','OK',NULL),(14,'2014-07-25 20:04:22','admin','org.complitex.dictionary',7,'org.complitex.dictionary.strategy.web.DomainObjectEditPanel','org.complitex.dictionary.entity.DomainObject#organization','EDIT','OK',NULL),(15,'2014-07-25 21:29:41','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(16,'2014-07-28 13:39:06','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(17,'2014-08-01 19:38:39','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(18,'2014-08-01 19:40:44','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(19,'2014-08-01 19:48:05','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(20,'2014-08-01 20:03:33','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(21,'2014-08-04 09:38:53','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(22,'2014-08-04 12:42:44','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(23,'2014-08-04 12:54:31','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(24,'2014-08-04 20:13:11','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(25,'2014-08-04 20:20:28','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(26,'2014-08-04 20:24:19','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(27,'2014-08-04 20:49:48','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(28,'2014-08-05 15:31:34','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(29,'2014-08-05 17:30:05','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(30,'2014-08-05 17:36:44','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(31,'2014-08-05 17:44:47','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(32,'2014-08-05 17:56:48','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(33,'2014-08-05 18:04:20','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(34,'2014-08-05 18:17:45','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(35,'2014-08-06 09:45:26','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(36,'2014-08-06 09:45:49','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(37,'2014-08-06 10:04:13','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(38,'2014-08-06 10:33:42','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(39,'2014-08-07 12:27:22','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(40,'2014-08-07 12:30:40','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(41,'2014-08-07 12:36:25','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(42,'2014-08-07 13:43:44','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(43,'2014-08-07 13:57:28','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(44,'2014-08-07 14:12:33','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(45,'2014-08-07 14:17:55','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(46,'2014-08-07 14:40:08','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(47,'2014-08-07 14:45:54','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(48,'2014-08-07 14:51:39','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(49,'2014-08-07 15:03:05','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(50,'2014-08-07 15:36:11','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(51,'2014-08-07 15:43:53','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(52,'2014-08-07 15:49:10','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(53,'2014-08-07 16:16:17','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(54,'2014-08-07 16:46:52','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(55,'2014-08-07 16:49:46','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(56,'2014-08-07 17:20:59','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(57,'2014-08-07 17:39:38','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(58,'2014-08-07 17:44:59','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(59,'2014-08-07 17:56:32','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(60,'2014-08-08 12:47:22','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(61,'2014-08-08 14:06:27','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(62,'2014-08-08 18:00:16','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(63,'2014-08-11 15:31:10','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(64,'2014-08-11 15:35:36','admin','org.complitex.admin',3,'org.complitex.admin.web.UserEdit','org.complitex.dictionary.entity.User','EDIT','OK',NULL),(65,'2014-08-11 15:37:47','admin','org.complitex.admin',4,'org.complitex.admin.web.UserEdit','org.complitex.dictionary.entity.User','EDIT','OK',NULL),(66,'2014-08-12 11:24:33','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(67,'2014-08-12 11:24:56','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(68,'2014-08-12 11:33:26','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(69,'2014-08-12 12:43:28','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(70,'2014-08-12 13:03:33','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(71,'2014-08-12 13:10:55','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(72,'2014-08-12 13:28:31','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(73,'2014-08-12 13:29:19','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(74,'2014-08-12 13:35:47','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(75,'2014-08-13 11:03:16','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(76,'2014-08-13 14:52:41','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(77,'2014-08-13 15:37:26','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(78,'2014-08-13 16:07:39','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(79,'2014-08-13 16:10:09','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(80,'2014-08-13 16:14:59','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(81,'2014-08-13 17:54:59','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(82,'2014-08-18 13:14:56','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(83,'2014-08-19 13:36:33','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(84,'2014-08-19 14:11:12','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(85,'2014-08-19 14:13:00','admin','org.complitex.address',1,'org.complitex.common.strategy.web.DomainObjectEditPanel','org.complitex.common.entity.DomainObject#building_address','CREATE','OK',NULL),(86,'2014-08-19 14:13:00','admin','org.complitex.common',1,'org.complitex.common.strategy.web.DomainObjectEditPanel','org.complitex.common.entity.DomainObject#building','CREATE','OK',NULL),(87,'2014-08-19 14:14:30','admin','org.complitex.address',NULL,'org.complitex.common.strategy.web.DomainObjectEditPanel','org.complitex.common.entity.DomainObject#apartment','BULK_SAVE','OK','Операция массового создания квартир запущена. Список квартир, которые будут созданы: 1, 1 - 36.'),(88,'2014-08-19 14:14:32','admin','org.complitex.address',NULL,'org.complitex.common.strategy.web.DomainObjectEditPanel','org.complitex.common.entity.DomainObject#apartment','BULK_SAVE','OK','Операция массового создания квартир успешно завершена. Список созданных квартир: 1, 1 - 36.'),(89,'2014-08-19 14:25:49','admin','org.complitex.common',1,'org.complitex.common.strategy.web.DomainObjectEditPanel','org.complitex.common.entity.DomainObject#city','EDIT','OK',NULL),(90,'2014-08-19 14:43:21','admin','org.complitex.common',7,'org.complitex.common.strategy.web.DomainObjectEditPanel','org.complitex.common.entity.DomainObject#organization','EDIT','OK',NULL),(91,'2014-08-20 10:19:32','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(92,'2014-08-25 16:41:47','ANONYMOUS','org.complitex.template',NULL,'org.complitex.template.web.security.SecurityWebListener',NULL,'SYSTEM_START','OK',NULL),(93,'2014-08-25 17:13:29','admin','org.complitex.common',82,'org.complitex.common.strategy.web.DomainObjectEditPanel','org.complitex.common.entity.DomainObject#organization','CREATE','OK',NULL),(94,'2014-08-25 17:14:09','admin','org.complitex.common',82,'org.complitex.common.strategy.web.DomainObjectEditPanel','org.complitex.common.entity.DomainObject#organization','EDIT','OK',NULL),(95,'2014-08-25 17:14:52','admin','org.complitex.common',2,'org.complitex.common.strategy.web.DomainObjectEditPanel','org.complitex.common.entity.DomainObject#module_instance','CREATE','OK',NULL);
/*!40000 ALTER TABLE `log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `log_change`
--

DROP TABLE IF EXISTS `log_change`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `log_change` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор изменения',
  `log_id` bigint(20) NOT NULL COMMENT 'Идентификатор журнала событий',
  `attribute_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор атрибута',
  `collection` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Название группы параметров',
  `property` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Свойство',
  `old_value` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Предыдущее значение',
  `new_value` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Новое значение',
  `locale` varchar(2) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Код локали',
  PRIMARY KEY (`id`),
  KEY `key_log` (`log_id`),
  CONSTRAINT `fk_log_change__log` FOREIGN KEY (`log_id`) REFERENCES `log` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Изменения модели данных';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `log_change`
--

LOCK TABLES `log_change` WRITE;
/*!40000 ALTER TABLE `log_change` DISABLE KEYS */;
INSERT INTO `log_change` VALUES (1,12,1,NULL,'НАИМЕНОВАНИЕ ОРГАНИЗАЦИИ',NULL,'ЕИРЦ-1','ru'),(2,12,1,NULL,'УНИКАЛЬНЫЙ КОД ОРГАНИЗАЦИИ',NULL,'ЕИРЦ-1',NULL),(3,12,1,NULL,'РАЙОН',NULL,'',NULL),(4,12,1,NULL,'РОДИТЕЛЬСКАЯ ОРГАНИЗАЦИЯ',NULL,'',NULL),(5,12,1,NULL,'КОРОТКОЕ НАИМЕНОВАНИЕ',NULL,'ЕИРЦ-1','ru'),(6,12,1,NULL,'КПП',NULL,'111',NULL),(7,12,1,NULL,'ИНН',NULL,'1111',NULL),(8,12,1,NULL,'ПРИМЕЧАНИЕ',NULL,'111',NULL),(9,12,1,NULL,'ЮРИДИЧЕСКИЙ АДРЕС',NULL,'111',NULL),(10,12,1,NULL,'ПОЧТОВЫЙ АДРЕС',NULL,'111',NULL),(11,12,1,NULL,'ТИП ОРГАНИЗАЦИИ',NULL,'1',NULL),(12,13,1,NULL,'НАЗВАНИЕ',NULL,'ЕИРЦ',NULL),(13,13,1,NULL,'СЕКРЕТНЫЙ КЛЮЧ',NULL,NULL,NULL),(14,13,1,NULL,'ИДЕНТИФИКАТОР',NULL,'COMMON',NULL),(15,13,1,NULL,'ОРГАНИЗАЦИЯ',NULL,'81',NULL),(16,13,1,NULL,'ТИП МОДУЛЯ',NULL,'1',NULL),(17,14,1,NULL,'РОДИТЕЛЬСКАЯ ОРГАНИЗАЦИЯ','3','',NULL),(18,14,1,NULL,'ТИП ОРГАНИЗАЦИИ','4','2',NULL),(19,14,1,NULL,'КПП',NULL,'111',NULL),(20,14,1,NULL,'ИНН',NULL,'111',NULL),(21,14,1,NULL,'ЮРИДИЧЕСКИЙ АДРЕС',NULL,'111',NULL),(22,14,1,NULL,'ПОЧТОВЫЙ АДРЕС',NULL,'111',NULL),(23,14,1,NULL,'E-MAIL',NULL,'111',NULL),(24,14,1,NULL,'ПРИМЕЧАНИЕ',NULL,'111',NULL),(25,14,1,NULL,'ДОПУСТИМЫЕ УСЛУГИ','',NULL,NULL),(26,64,1,NULL,'ФАМИЛИЯ',NULL,'3',NULL),(27,64,1,NULL,'ИМЯ',NULL,'3',NULL),(28,64,1,NULL,'ОТЧЕСТВО',NULL,'3',NULL),(29,64,NULL,NULL,'Группы привилегий',NULL,'Администраторы',NULL),(30,64,NULL,NULL,'Группы привилегий',NULL,'Сотрудники',NULL),(31,64,NULL,NULL,'Группы привилегий',NULL,'Сотрудники с правом просмотра дочерних организаций',NULL),(32,65,1,NULL,'ФАМИЛИЯ',NULL,'3',NULL),(33,65,1,NULL,'ИМЯ',NULL,'3',NULL),(34,65,1,NULL,'ОТЧЕСТВО',NULL,'3',NULL),(35,65,NULL,NULL,'Группы привилегий',NULL,'Сотрудники',NULL),(36,65,NULL,NULL,'Группы привилегий',NULL,'Сотрудники с правом просмотра дочерних организаций',NULL),(37,85,1,NULL,'НОМЕР ДОМА',NULL,'2','ru'),(38,86,1,NULL,'РАЙОН',NULL,'',NULL),(39,86,1,NULL,'СПИСОК КОДОВ ДОМА',NULL,'1',NULL),(40,89,1,NULL,'ПРЕФИКС Л/С ЕИРЦ',NULL,'057',NULL),(41,90,1,NULL,'ДОПУСТИМЫЕ УСЛУГИ','','4',NULL),(42,93,1,NULL,'НАИМЕНОВАНИЕ ОРГАНИЗАЦИИ',NULL,'ПЕЙМЕНТС 1','ru'),(43,93,1,NULL,'УНИКАЛЬНЫЙ КОД ОРГАНИЗАЦИИ',NULL,'П1',NULL),(44,93,1,NULL,'РАЙОН',NULL,'',NULL),(45,93,1,NULL,'РОДИТЕЛЬСКАЯ ОРГАНИЗАЦИЯ',NULL,'',NULL),(46,93,1,NULL,'КПП',NULL,'1111',NULL),(47,93,1,NULL,'ИНН',NULL,'1111',NULL),(48,93,1,NULL,'ПРИМЕЧАНИЕ',NULL,'1111',NULL),(49,93,1,NULL,'ЮРИДИЧЕСКИЙ АДРЕС',NULL,'1111',NULL),(50,93,1,NULL,'ПОЧТОВЫЙ АДРЕС',NULL,'11111',NULL),(51,93,1,NULL,'ТИП ОРГАНИЗАЦИИ',NULL,'3',NULL),(52,94,1,NULL,'ТИП ОРГАНИЗАЦИИ','3','1',NULL),(53,94,1,NULL,'E-MAIL',NULL,NULL,NULL),(54,94,1,NULL,'ДОПУСТИМЫЕ УСЛУГИ','',NULL,NULL),(55,94,2,NULL,'ТИП ОРГАНИЗАЦИИ',NULL,'3',NULL),(56,95,1,NULL,'НАЗВАНИЕ',NULL,'ПЕЙМЕНТС 1',NULL),(57,95,1,NULL,'СЕКРЕТНЫЙ КЛЮЧ',NULL,NULL,NULL),(58,95,1,NULL,'ИДЕНТИФИКАТОР',NULL,'PAYMENTS_1',NULL),(59,95,1,NULL,'ОРГАНИЗАЦИЯ',NULL,'82',NULL),(60,95,1,NULL,'ТИП МОДУЛЯ',NULL,'2',NULL);
/*!40000 ALTER TABLE `log_change` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `middle_name`
--

DROP TABLE IF EXISTS `middle_name`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `middle_name` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор отчества',
  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Отчество',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Отчество';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `middle_name`
--

LOCK TABLES `middle_name` WRITE;
/*!40000 ALTER TABLE `middle_name` DISABLE KEYS */;
INSERT INTO `middle_name` VALUES (1,'admin'),(2,'ANONYMOUS'),(3,'Test');
/*!40000 ALTER TABLE `middle_name` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `module_instance`
--

DROP TABLE IF EXISTS `module_instance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `module_instance` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор сущности родительского объекта',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус объекта. См. класс StatusType',
  `permission_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'Ключ прав доступа к объекту',
  `external_id` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_module_instance__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_module_instance__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Инстанс модуля';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `module_instance`
--

LOCK TABLES `module_instance` WRITE;
/*!40000 ALTER TABLE `module_instance` DISABLE KEYS */;
INSERT INTO `module_instance` VALUES (1,1,NULL,NULL,'2014-07-25 13:50:11',NULL,1,0,NULL),(2,2,NULL,NULL,'2014-08-25 10:14:52',NULL,1,0,NULL);
/*!40000 ALTER TABLE `module_instance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `module_instance_attribute`
--

DROP TABLE IF EXISTS `module_instance_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `module_instance_attribute` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` bigint(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 100 - НАИМЕНОВАНИЕ МОДУЛЯ',
  `value_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор значения',
  `value_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа значения: 100 - string_value',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_module_instance_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`) REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_module_instance_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`) REFERENCES `entity_attribute_value_type` (`id`),
  CONSTRAINT `fk_module_instance_attribute__module_instance` FOREIGN KEY (`object_id`) REFERENCES `module_instance` (`object_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Атрибуты модуля';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `module_instance_attribute`
--

LOCK TABLES `module_instance_attribute` WRITE;
/*!40000 ALTER TABLE `module_instance_attribute` DISABLE KEYS */;
INSERT INTO `module_instance_attribute` VALUES (1,1,1,1010,1,1010,'2014-07-25 13:50:11',NULL,1),(2,1,1,1012,2,1012,'2014-07-25 13:50:11',NULL,1),(3,1,1,1013,3,1013,'2014-07-25 13:50:11',NULL,1),(4,1,1,1014,1,1014,'2014-07-25 13:50:11',NULL,1),(5,1,2,1010,4,1010,'2014-08-25 10:14:52',NULL,1),(6,1,2,1012,5,1012,'2014-08-25 10:14:52',NULL,1),(7,1,2,1013,6,1013,'2014-08-25 10:14:52',NULL,1),(8,1,2,1014,2,1014,'2014-08-25 10:14:52',NULL,1);
/*!40000 ALTER TABLE `module_instance_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `module_instance_string_value`
--

DROP TABLE IF EXISTS `module_instance_string_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `module_instance_string_value` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` bigint(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` bigint(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_module_instance_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Локализация атрибутов модуля';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `module_instance_string_value`
--

LOCK TABLES `module_instance_string_value` WRITE;
/*!40000 ALTER TABLE `module_instance_string_value` DISABLE KEYS */;
INSERT INTO `module_instance_string_value` VALUES (1,1,1,'ЕИРЦ'),(2,2,1,'COMMON'),(3,3,1,'81'),(4,4,1,'ПЕЙМЕНТС 1'),(5,5,1,'PAYMENTS_1'),(6,6,1,'82');
/*!40000 ALTER TABLE `module_instance_string_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `module_instance_type`
--

DROP TABLE IF EXISTS `module_instance_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `module_instance_type` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор сущности родительского объекта',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус объекта. См. класс StatusType',
  `permission_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'Ключ прав доступа к объекту',
  `external_id` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_module_instance_type__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_module_instance_type__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Тип инстанса модуля';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `module_instance_type`
--

LOCK TABLES `module_instance_type` WRITE;
/*!40000 ALTER TABLE `module_instance_type` DISABLE KEYS */;
INSERT INTO `module_instance_type` VALUES (1,1,NULL,NULL,'2014-07-25 03:38:43',NULL,1,0,NULL),(2,2,NULL,NULL,'2014-07-25 03:38:43',NULL,1,0,NULL);
/*!40000 ALTER TABLE `module_instance_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `module_instance_type_attribute`
--

DROP TABLE IF EXISTS `module_instance_type_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `module_instance_type_attribute` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` bigint(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 100 - НАИМЕНОВАНИЕ МОДУЛЯ',
  `value_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор значения',
  `value_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа значения: 100 - STRING_VALUE',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_module_instance_type_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`) REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_module_instance_type_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`) REFERENCES `entity_attribute_value_type` (`id`),
  CONSTRAINT `fk_module_instance_type_attribute__module_instance_type` FOREIGN KEY (`object_id`) REFERENCES `module_instance_type` (`object_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Атрибуты типа модуля';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `module_instance_type_attribute`
--

LOCK TABLES `module_instance_type_attribute` WRITE;
/*!40000 ALTER TABLE `module_instance_type_attribute` DISABLE KEYS */;
INSERT INTO `module_instance_type_attribute` VALUES (1,1,1,1110,1,1110,'2014-07-25 03:38:43',NULL,1),(2,2,2,1110,2,1110,'2014-07-25 03:38:43',NULL,1);
/*!40000 ALTER TABLE `module_instance_type_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `module_instance_type_string_value`
--

DROP TABLE IF EXISTS `module_instance_type_string_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `module_instance_type_string_value` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` bigint(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` bigint(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_module_instance_type_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Локализация атрибутов типа модуля';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `module_instance_type_string_value`
--

LOCK TABLES `module_instance_type_string_value` WRITE;
/*!40000 ALTER TABLE `module_instance_type_string_value` DISABLE KEYS */;
INSERT INTO `module_instance_type_string_value` VALUES (1,1,1,'МОДУЛЬ ЕИРЦ'),(2,1,2,'МОДУЛЬ ЕIРЦ'),(3,2,1,'МОДУЛЬ ПЛАТЕЖЕЙ'),(4,2,2,'МОДУЛЬ ПЛАТЕЖІВ');
/*!40000 ALTER TABLE `module_instance_type_string_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organization`
--

DROP TABLE IF EXISTS `organization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organization` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор родительского объекта: не используется',
  `parent_entity_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор сущности родительского объекта: не используется',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  `permission_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'Ключ прав доступа к объекту',
  `external_id` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_organization__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_organization__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Организация';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organization`
--

LOCK TABLES `organization` WRITE;
/*!40000 ALTER TABLE `organization` DISABLE KEYS */;
INSERT INTO `organization` VALUES (1,0,NULL,NULL,'2014-07-25 03:38:43',NULL,1,0,NULL),(2,1,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'3659392'),(3,2,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'2144343384'),(4,3,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'5894269'),(5,4,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'2144343497'),(6,5,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'27575681'),(7,6,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'59631157'),(8,7,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'45095841'),(9,8,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'5894272'),(10,9,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'9411966'),(11,10,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'3737045'),(12,11,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'30911211'),(13,12,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'44908366'),(14,13,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'42737688'),(15,14,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'2154473691'),(16,15,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'267869803'),(17,16,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'267869805'),(18,17,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'267869802'),(19,18,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'267869807'),(20,19,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274776763'),(21,20,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274776768'),(22,21,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274776766'),(23,22,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274776764'),(24,23,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274776769'),(25,24,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274776767'),(26,25,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274773578'),(27,26,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274773580'),(28,27,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274773575'),(29,28,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274773577'),(30,29,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274773576'),(31,30,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274773579'),(32,31,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274772616'),(33,32,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274772626'),(34,33,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274772623'),(35,34,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274772625'),(36,35,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274772622'),(37,36,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274772613'),(38,37,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274772572'),(39,38,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274772614'),(40,39,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274772615'),(41,40,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274772618'),(42,41,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274772619'),(43,42,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274772620'),(44,43,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274774649'),(45,44,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274774647'),(46,45,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274774765'),(47,46,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274774565'),(48,47,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274774648'),(49,48,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274774435'),(50,49,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274776337'),(51,50,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274776546'),(52,51,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274776338'),(53,52,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274776341'),(54,53,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274776339'),(55,54,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274776340'),(56,55,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274770718'),(57,56,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274770719'),(58,57,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274770720'),(59,58,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'319927759'),(60,59,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274770721'),(61,60,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'476729235'),(62,61,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274777276'),(63,62,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274777278'),(64,63,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274777279'),(65,64,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274777277'),(66,65,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274777280'),(67,66,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274777271'),(68,67,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274777270'),(69,68,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274777272'),(70,69,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274777273'),(71,70,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274777274'),(72,71,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274775708'),(73,72,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274775711'),(74,73,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274775712'),(75,74,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274775704'),(76,75,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274775714'),(77,76,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274775713'),(78,77,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274775715'),(79,78,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274775707'),(80,79,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274775709'),(81,80,NULL,NULL,'2014-07-25 13:43:47',NULL,1,0,'274775710'),(82,81,NULL,NULL,'2014-07-25 13:49:12',NULL,1,0,NULL),(83,82,NULL,NULL,'2014-08-25 10:13:29',NULL,1,0,NULL);
/*!40000 ALTER TABLE `organization` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organization_attribute`
--

DROP TABLE IF EXISTS `organization_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organization_attribute` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` bigint(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 900 - НАЗВАНИЕ, 901 - УНИКАЛЬНЫЙ КОД, 902 - РАЙОН, 903 - ПРИНАДЛЕЖИТ, 904  - ТИП ОРГАНИЗАЦИИ',
  `value_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор значения',
  `value_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа значения атрибута: 900 - STRING_VALUE, 901 - STRING, 902 - district, 903 - organization, 904 - organization_type',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_organization_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`) REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_organization_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`) REFERENCES `entity_attribute_value_type` (`id`),
  CONSTRAINT `fk_organization_attribute__organization` FOREIGN KEY (`object_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB AUTO_INCREMENT=829 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Атрибуты организации';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organization_attribute`
--

LOCK TABLES `organization_attribute` WRITE;
/*!40000 ALTER TABLE `organization_attribute` DISABLE KEYS */;
INSERT INTO `organization_attribute` VALUES (1,1,0,900,1,900,'2014-07-25 03:38:43',NULL,1),(2,1,1,900,2,900,'2014-07-25 13:43:47',NULL,1),(3,1,1,901,NULL,901,'2014-07-25 13:43:47',NULL,1),(4,1,1,906,3,906,'2014-07-25 13:43:47',NULL,1),(5,1,1,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(6,1,1,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(7,1,1,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(8,1,1,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(9,1,1,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(10,1,1,904,4,904,'2014-07-25 13:43:47',NULL,1),(11,1,2,900,4,900,'2014-07-25 13:43:47',NULL,1),(12,1,2,901,5,901,'2014-07-25 13:43:47',NULL,1),(13,1,2,903,1,903,'2014-07-25 13:43:47',NULL,1),(14,1,2,906,6,906,'2014-07-25 13:43:47',NULL,1),(15,1,2,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(16,1,2,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(17,1,2,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(18,1,2,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(19,1,2,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(20,1,2,904,4,904,'2014-07-25 13:43:47',NULL,1),(21,1,3,900,7,900,'2014-07-25 13:43:47',NULL,1),(22,1,3,901,8,901,'2014-07-25 13:43:47',NULL,1),(23,1,3,903,1,903,'2014-07-25 13:43:47',NULL,1),(24,1,3,906,9,906,'2014-07-25 13:43:47',NULL,1),(25,1,3,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(26,1,3,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(27,1,3,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(28,1,3,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(29,1,3,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(30,1,3,904,4,904,'2014-07-25 13:43:47',NULL,1),(31,1,4,900,10,900,'2014-07-25 13:43:47',NULL,1),(32,1,4,901,11,901,'2014-07-25 13:43:47',NULL,1),(33,1,4,903,2,903,'2014-07-25 13:43:47',NULL,1),(34,1,4,906,12,906,'2014-07-25 13:43:47',NULL,1),(35,1,4,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(36,1,4,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(37,1,4,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(38,1,4,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(39,1,4,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(40,1,4,904,4,904,'2014-07-25 13:43:47',NULL,1),(41,1,5,900,13,900,'2014-07-25 13:43:47',NULL,1),(42,1,5,901,14,901,'2014-07-25 13:43:47',NULL,1),(43,1,5,903,3,903,'2014-07-25 13:43:47',NULL,1),(44,1,5,906,15,906,'2014-07-25 13:43:47',NULL,1),(45,1,5,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(46,1,5,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(47,1,5,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(48,1,5,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(49,1,5,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(50,1,5,904,4,904,'2014-07-25 13:43:47',NULL,1),(51,1,6,900,16,900,'2014-07-25 13:43:47',NULL,1),(52,1,6,901,17,901,'2014-07-25 13:43:47',NULL,1),(53,1,6,903,3,903,'2014-07-25 13:43:47',NULL,1),(54,1,6,906,18,906,'2014-07-25 13:43:47',NULL,1),(55,1,6,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(56,1,6,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(57,1,6,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(58,1,6,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(59,1,6,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(60,1,6,904,4,904,'2014-07-25 13:43:47',NULL,1),(61,1,7,900,19,900,'2014-07-25 13:43:47',NULL,1),(62,1,7,901,20,901,'2014-07-25 13:43:47',NULL,1),(63,1,7,903,3,903,'2014-07-25 13:43:47','2014-07-25 14:04:22',2),(64,1,7,906,21,906,'2014-07-25 13:43:47',NULL,1),(65,1,7,913,NULL,913,'2014-07-25 13:43:47','2014-07-25 14:04:22',2),(66,1,7,914,NULL,914,'2014-07-25 13:43:47','2014-07-25 14:04:22',2),(67,1,7,916,NULL,916,'2014-07-25 13:43:47','2014-07-25 14:04:22',2),(68,1,7,917,NULL,917,'2014-07-25 13:43:47','2014-07-25 14:04:22',2),(69,1,7,918,NULL,918,'2014-07-25 13:43:47','2014-07-25 14:04:22',2),(70,1,7,904,4,904,'2014-07-25 13:43:47','2014-07-25 14:04:22',2),(71,1,8,900,22,900,'2014-07-25 13:43:47',NULL,1),(72,1,8,901,23,901,'2014-07-25 13:43:47',NULL,1),(73,1,8,903,3,903,'2014-07-25 13:43:47',NULL,1),(74,1,8,906,24,906,'2014-07-25 13:43:47',NULL,1),(75,1,8,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(76,1,8,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(77,1,8,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(78,1,8,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(79,1,8,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(80,1,8,904,4,904,'2014-07-25 13:43:47',NULL,1),(81,1,9,900,25,900,'2014-07-25 13:43:47',NULL,1),(82,1,9,901,26,901,'2014-07-25 13:43:47',NULL,1),(83,1,9,903,3,903,'2014-07-25 13:43:47',NULL,1),(84,1,9,906,27,906,'2014-07-25 13:43:47',NULL,1),(85,1,9,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(86,1,9,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(87,1,9,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(88,1,9,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(89,1,9,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(90,1,9,904,4,904,'2014-07-25 13:43:47',NULL,1),(91,1,10,900,28,900,'2014-07-25 13:43:47',NULL,1),(92,1,10,901,29,901,'2014-07-25 13:43:47',NULL,1),(93,1,10,903,3,903,'2014-07-25 13:43:47',NULL,1),(94,1,10,906,30,906,'2014-07-25 13:43:47',NULL,1),(95,1,10,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(96,1,10,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(97,1,10,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(98,1,10,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(99,1,10,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(100,1,10,904,4,904,'2014-07-25 13:43:47',NULL,1),(101,1,11,900,31,900,'2014-07-25 13:43:47',NULL,1),(102,1,11,901,32,901,'2014-07-25 13:43:47',NULL,1),(103,1,11,903,3,903,'2014-07-25 13:43:47',NULL,1),(104,1,11,906,33,906,'2014-07-25 13:43:47',NULL,1),(105,1,11,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(106,1,11,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(107,1,11,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(108,1,11,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(109,1,11,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(110,1,11,904,4,904,'2014-07-25 13:43:47',NULL,1),(111,1,12,900,34,900,'2014-07-25 13:43:47',NULL,1),(112,1,12,901,35,901,'2014-07-25 13:43:47',NULL,1),(113,1,12,903,3,903,'2014-07-25 13:43:47',NULL,1),(114,1,12,906,36,906,'2014-07-25 13:43:47',NULL,1),(115,1,12,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(116,1,12,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(117,1,12,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(118,1,12,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(119,1,12,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(120,1,12,904,4,904,'2014-07-25 13:43:47',NULL,1),(121,1,13,900,37,900,'2014-07-25 13:43:47',NULL,1),(122,1,13,901,38,901,'2014-07-25 13:43:47',NULL,1),(123,1,13,903,3,903,'2014-07-25 13:43:47',NULL,1),(124,1,13,906,39,906,'2014-07-25 13:43:47',NULL,1),(125,1,13,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(126,1,13,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(127,1,13,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(128,1,13,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(129,1,13,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(130,1,13,904,4,904,'2014-07-25 13:43:47',NULL,1),(131,1,14,900,40,900,'2014-07-25 13:43:47',NULL,1),(132,1,14,901,41,901,'2014-07-25 13:43:47',NULL,1),(133,1,14,903,4,903,'2014-07-25 13:43:47',NULL,1),(134,1,14,906,42,906,'2014-07-25 13:43:47',NULL,1),(135,1,14,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(136,1,14,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(137,1,14,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(138,1,14,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(139,1,14,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(140,1,14,904,4,904,'2014-07-25 13:43:47',NULL,1),(141,1,15,900,43,900,'2014-07-25 13:43:47',NULL,1),(142,1,15,901,44,901,'2014-07-25 13:43:47',NULL,1),(143,1,15,903,5,903,'2014-07-25 13:43:47',NULL,1),(144,1,15,906,45,906,'2014-07-25 13:43:47',NULL,1),(145,1,15,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(146,1,15,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(147,1,15,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(148,1,15,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(149,1,15,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(150,1,15,904,4,904,'2014-07-25 13:43:47',NULL,1),(151,1,16,900,46,900,'2014-07-25 13:43:47',NULL,1),(152,1,16,901,47,901,'2014-07-25 13:43:47',NULL,1),(153,1,16,903,5,903,'2014-07-25 13:43:47',NULL,1),(154,1,16,906,48,906,'2014-07-25 13:43:47',NULL,1),(155,1,16,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(156,1,16,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(157,1,16,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(158,1,16,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(159,1,16,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(160,1,16,904,4,904,'2014-07-25 13:43:47',NULL,1),(161,1,17,900,49,900,'2014-07-25 13:43:47',NULL,1),(162,1,17,901,50,901,'2014-07-25 13:43:47',NULL,1),(163,1,17,903,5,903,'2014-07-25 13:43:47',NULL,1),(164,1,17,906,51,906,'2014-07-25 13:43:47',NULL,1),(165,1,17,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(166,1,17,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(167,1,17,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(168,1,17,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(169,1,17,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(170,1,17,904,4,904,'2014-07-25 13:43:47',NULL,1),(171,1,18,900,52,900,'2014-07-25 13:43:47',NULL,1),(172,1,18,901,53,901,'2014-07-25 13:43:47',NULL,1),(173,1,18,903,5,903,'2014-07-25 13:43:47',NULL,1),(174,1,18,906,54,906,'2014-07-25 13:43:47',NULL,1),(175,1,18,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(176,1,18,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(177,1,18,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(178,1,18,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(179,1,18,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(180,1,18,904,4,904,'2014-07-25 13:43:47',NULL,1),(181,1,19,900,55,900,'2014-07-25 13:43:47',NULL,1),(182,1,19,901,56,901,'2014-07-25 13:43:47',NULL,1),(183,1,19,903,6,903,'2014-07-25 13:43:47',NULL,1),(184,1,19,906,57,906,'2014-07-25 13:43:47',NULL,1),(185,1,19,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(186,1,19,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(187,1,19,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(188,1,19,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(189,1,19,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(190,1,19,904,4,904,'2014-07-25 13:43:47',NULL,1),(191,1,20,900,58,900,'2014-07-25 13:43:47',NULL,1),(192,1,20,901,59,901,'2014-07-25 13:43:47',NULL,1),(193,1,20,903,6,903,'2014-07-25 13:43:47',NULL,1),(194,1,20,906,60,906,'2014-07-25 13:43:47',NULL,1),(195,1,20,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(196,1,20,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(197,1,20,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(198,1,20,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(199,1,20,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(200,1,20,904,4,904,'2014-07-25 13:43:47',NULL,1),(201,1,21,900,61,900,'2014-07-25 13:43:47',NULL,1),(202,1,21,901,62,901,'2014-07-25 13:43:47',NULL,1),(203,1,21,903,6,903,'2014-07-25 13:43:47',NULL,1),(204,1,21,906,63,906,'2014-07-25 13:43:47',NULL,1),(205,1,21,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(206,1,21,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(207,1,21,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(208,1,21,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(209,1,21,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(210,1,21,904,4,904,'2014-07-25 13:43:47',NULL,1),(211,1,22,900,64,900,'2014-07-25 13:43:47',NULL,1),(212,1,22,901,65,901,'2014-07-25 13:43:47',NULL,1),(213,1,22,903,6,903,'2014-07-25 13:43:47',NULL,1),(214,1,22,906,66,906,'2014-07-25 13:43:47',NULL,1),(215,1,22,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(216,1,22,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(217,1,22,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(218,1,22,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(219,1,22,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(220,1,22,904,4,904,'2014-07-25 13:43:47',NULL,1),(221,1,23,900,67,900,'2014-07-25 13:43:47',NULL,1),(222,1,23,901,68,901,'2014-07-25 13:43:47',NULL,1),(223,1,23,903,6,903,'2014-07-25 13:43:47',NULL,1),(224,1,23,906,69,906,'2014-07-25 13:43:47',NULL,1),(225,1,23,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(226,1,23,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(227,1,23,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(228,1,23,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(229,1,23,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(230,1,23,904,4,904,'2014-07-25 13:43:47',NULL,1),(231,1,24,900,70,900,'2014-07-25 13:43:47',NULL,1),(232,1,24,901,71,901,'2014-07-25 13:43:47',NULL,1),(233,1,24,903,6,903,'2014-07-25 13:43:47',NULL,1),(234,1,24,906,72,906,'2014-07-25 13:43:47',NULL,1),(235,1,24,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(236,1,24,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(237,1,24,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(238,1,24,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(239,1,24,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(240,1,24,904,4,904,'2014-07-25 13:43:47',NULL,1),(241,1,25,900,73,900,'2014-07-25 13:43:47',NULL,1),(242,1,25,901,74,901,'2014-07-25 13:43:47',NULL,1),(243,1,25,903,7,903,'2014-07-25 13:43:47',NULL,1),(244,1,25,906,75,906,'2014-07-25 13:43:47',NULL,1),(245,1,25,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(246,1,25,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(247,1,25,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(248,1,25,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(249,1,25,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(250,1,25,904,4,904,'2014-07-25 13:43:47',NULL,1),(251,1,26,900,76,900,'2014-07-25 13:43:47',NULL,1),(252,1,26,901,77,901,'2014-07-25 13:43:47',NULL,1),(253,1,26,903,7,903,'2014-07-25 13:43:47',NULL,1),(254,1,26,906,78,906,'2014-07-25 13:43:47',NULL,1),(255,1,26,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(256,1,26,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(257,1,26,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(258,1,26,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(259,1,26,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(260,1,26,904,4,904,'2014-07-25 13:43:47',NULL,1),(261,1,27,900,79,900,'2014-07-25 13:43:47',NULL,1),(262,1,27,901,80,901,'2014-07-25 13:43:47',NULL,1),(263,1,27,903,7,903,'2014-07-25 13:43:47',NULL,1),(264,1,27,906,81,906,'2014-07-25 13:43:47',NULL,1),(265,1,27,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(266,1,27,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(267,1,27,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(268,1,27,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(269,1,27,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(270,1,27,904,4,904,'2014-07-25 13:43:47',NULL,1),(271,1,28,900,82,900,'2014-07-25 13:43:47',NULL,1),(272,1,28,901,83,901,'2014-07-25 13:43:47',NULL,1),(273,1,28,903,7,903,'2014-07-25 13:43:47',NULL,1),(274,1,28,906,84,906,'2014-07-25 13:43:47',NULL,1),(275,1,28,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(276,1,28,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(277,1,28,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(278,1,28,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(279,1,28,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(280,1,28,904,4,904,'2014-07-25 13:43:47',NULL,1),(281,1,29,900,85,900,'2014-07-25 13:43:47',NULL,1),(282,1,29,901,86,901,'2014-07-25 13:43:47',NULL,1),(283,1,29,903,7,903,'2014-07-25 13:43:47',NULL,1),(284,1,29,906,87,906,'2014-07-25 13:43:47',NULL,1),(285,1,29,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(286,1,29,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(287,1,29,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(288,1,29,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(289,1,29,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(290,1,29,904,4,904,'2014-07-25 13:43:47',NULL,1),(291,1,30,900,88,900,'2014-07-25 13:43:47',NULL,1),(292,1,30,901,89,901,'2014-07-25 13:43:47',NULL,1),(293,1,30,903,7,903,'2014-07-25 13:43:47',NULL,1),(294,1,30,906,90,906,'2014-07-25 13:43:47',NULL,1),(295,1,30,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(296,1,30,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(297,1,30,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(298,1,30,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(299,1,30,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(300,1,30,904,4,904,'2014-07-25 13:43:47',NULL,1),(301,1,31,900,91,900,'2014-07-25 13:43:47',NULL,1),(302,1,31,901,92,901,'2014-07-25 13:43:47',NULL,1),(303,1,31,903,8,903,'2014-07-25 13:43:47',NULL,1),(304,1,31,906,93,906,'2014-07-25 13:43:47',NULL,1),(305,1,31,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(306,1,31,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(307,1,31,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(308,1,31,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(309,1,31,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(310,1,31,904,4,904,'2014-07-25 13:43:47',NULL,1),(311,1,32,900,94,900,'2014-07-25 13:43:47',NULL,1),(312,1,32,901,95,901,'2014-07-25 13:43:47',NULL,1),(313,1,32,903,8,903,'2014-07-25 13:43:47',NULL,1),(314,1,32,906,96,906,'2014-07-25 13:43:47',NULL,1),(315,1,32,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(316,1,32,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(317,1,32,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(318,1,32,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(319,1,32,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(320,1,32,904,4,904,'2014-07-25 13:43:47',NULL,1),(321,1,33,900,97,900,'2014-07-25 13:43:47',NULL,1),(322,1,33,901,98,901,'2014-07-25 13:43:47',NULL,1),(323,1,33,903,8,903,'2014-07-25 13:43:47',NULL,1),(324,1,33,906,99,906,'2014-07-25 13:43:47',NULL,1),(325,1,33,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(326,1,33,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(327,1,33,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(328,1,33,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(329,1,33,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(330,1,33,904,4,904,'2014-07-25 13:43:47',NULL,1),(331,1,34,900,100,900,'2014-07-25 13:43:47',NULL,1),(332,1,34,901,101,901,'2014-07-25 13:43:47',NULL,1),(333,1,34,903,8,903,'2014-07-25 13:43:47',NULL,1),(334,1,34,906,102,906,'2014-07-25 13:43:47',NULL,1),(335,1,34,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(336,1,34,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(337,1,34,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(338,1,34,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(339,1,34,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(340,1,34,904,4,904,'2014-07-25 13:43:47',NULL,1),(341,1,35,900,103,900,'2014-07-25 13:43:47',NULL,1),(342,1,35,901,104,901,'2014-07-25 13:43:47',NULL,1),(343,1,35,903,8,903,'2014-07-25 13:43:47',NULL,1),(344,1,35,906,105,906,'2014-07-25 13:43:47',NULL,1),(345,1,35,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(346,1,35,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(347,1,35,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(348,1,35,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(349,1,35,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(350,1,35,904,4,904,'2014-07-25 13:43:47',NULL,1),(351,1,36,900,106,900,'2014-07-25 13:43:47',NULL,1),(352,1,36,901,107,901,'2014-07-25 13:43:47',NULL,1),(353,1,36,903,8,903,'2014-07-25 13:43:47',NULL,1),(354,1,36,906,108,906,'2014-07-25 13:43:47',NULL,1),(355,1,36,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(356,1,36,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(357,1,36,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(358,1,36,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(359,1,36,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(360,1,36,904,4,904,'2014-07-25 13:43:47',NULL,1),(361,1,37,900,109,900,'2014-07-25 13:43:47',NULL,1),(362,1,37,901,110,901,'2014-07-25 13:43:47',NULL,1),(363,1,37,903,8,903,'2014-07-25 13:43:47',NULL,1),(364,1,37,906,111,906,'2014-07-25 13:43:47',NULL,1),(365,1,37,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(366,1,37,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(367,1,37,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(368,1,37,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(369,1,37,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(370,1,37,904,4,904,'2014-07-25 13:43:47',NULL,1),(371,1,38,900,112,900,'2014-07-25 13:43:47',NULL,1),(372,1,38,901,113,901,'2014-07-25 13:43:47',NULL,1),(373,1,38,903,8,903,'2014-07-25 13:43:47',NULL,1),(374,1,38,906,114,906,'2014-07-25 13:43:47',NULL,1),(375,1,38,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(376,1,38,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(377,1,38,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(378,1,38,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(379,1,38,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(380,1,38,904,4,904,'2014-07-25 13:43:47',NULL,1),(381,1,39,900,115,900,'2014-07-25 13:43:47',NULL,1),(382,1,39,901,116,901,'2014-07-25 13:43:47',NULL,1),(383,1,39,903,8,903,'2014-07-25 13:43:47',NULL,1),(384,1,39,906,117,906,'2014-07-25 13:43:47',NULL,1),(385,1,39,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(386,1,39,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(387,1,39,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(388,1,39,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(389,1,39,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(390,1,39,904,4,904,'2014-07-25 13:43:47',NULL,1),(391,1,40,900,118,900,'2014-07-25 13:43:47',NULL,1),(392,1,40,901,119,901,'2014-07-25 13:43:47',NULL,1),(393,1,40,903,8,903,'2014-07-25 13:43:47',NULL,1),(394,1,40,906,120,906,'2014-07-25 13:43:47',NULL,1),(395,1,40,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(396,1,40,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(397,1,40,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(398,1,40,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(399,1,40,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(400,1,40,904,4,904,'2014-07-25 13:43:47',NULL,1),(401,1,41,900,121,900,'2014-07-25 13:43:47',NULL,1),(402,1,41,901,122,901,'2014-07-25 13:43:47',NULL,1),(403,1,41,903,8,903,'2014-07-25 13:43:47',NULL,1),(404,1,41,906,123,906,'2014-07-25 13:43:47',NULL,1),(405,1,41,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(406,1,41,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(407,1,41,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(408,1,41,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(409,1,41,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(410,1,41,904,4,904,'2014-07-25 13:43:47',NULL,1),(411,1,42,900,124,900,'2014-07-25 13:43:47',NULL,1),(412,1,42,901,125,901,'2014-07-25 13:43:47',NULL,1),(413,1,42,903,8,903,'2014-07-25 13:43:47',NULL,1),(414,1,42,906,126,906,'2014-07-25 13:43:47',NULL,1),(415,1,42,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(416,1,42,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(417,1,42,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(418,1,42,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(419,1,42,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(420,1,42,904,4,904,'2014-07-25 13:43:47',NULL,1),(421,1,43,900,127,900,'2014-07-25 13:43:47',NULL,1),(422,1,43,901,128,901,'2014-07-25 13:43:47',NULL,1),(423,1,43,903,9,903,'2014-07-25 13:43:47',NULL,1),(424,1,43,906,129,906,'2014-07-25 13:43:47',NULL,1),(425,1,43,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(426,1,43,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(427,1,43,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(428,1,43,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(429,1,43,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(430,1,43,904,4,904,'2014-07-25 13:43:47',NULL,1),(431,1,44,900,130,900,'2014-07-25 13:43:47',NULL,1),(432,1,44,901,131,901,'2014-07-25 13:43:47',NULL,1),(433,1,44,903,9,903,'2014-07-25 13:43:47',NULL,1),(434,1,44,906,132,906,'2014-07-25 13:43:47',NULL,1),(435,1,44,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(436,1,44,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(437,1,44,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(438,1,44,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(439,1,44,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(440,1,44,904,4,904,'2014-07-25 13:43:47',NULL,1),(441,1,45,900,133,900,'2014-07-25 13:43:47',NULL,1),(442,1,45,901,134,901,'2014-07-25 13:43:47',NULL,1),(443,1,45,903,9,903,'2014-07-25 13:43:47',NULL,1),(444,1,45,906,135,906,'2014-07-25 13:43:47',NULL,1),(445,1,45,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(446,1,45,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(447,1,45,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(448,1,45,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(449,1,45,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(450,1,45,904,4,904,'2014-07-25 13:43:47',NULL,1),(451,1,46,900,136,900,'2014-07-25 13:43:47',NULL,1),(452,1,46,901,137,901,'2014-07-25 13:43:47',NULL,1),(453,1,46,903,9,903,'2014-07-25 13:43:47',NULL,1),(454,1,46,906,138,906,'2014-07-25 13:43:47',NULL,1),(455,1,46,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(456,1,46,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(457,1,46,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(458,1,46,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(459,1,46,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(460,1,46,904,4,904,'2014-07-25 13:43:47',NULL,1),(461,1,47,900,139,900,'2014-07-25 13:43:47',NULL,1),(462,1,47,901,140,901,'2014-07-25 13:43:47',NULL,1),(463,1,47,903,9,903,'2014-07-25 13:43:47',NULL,1),(464,1,47,906,141,906,'2014-07-25 13:43:47',NULL,1),(465,1,47,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(466,1,47,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(467,1,47,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(468,1,47,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(469,1,47,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(470,1,47,904,4,904,'2014-07-25 13:43:47',NULL,1),(471,1,48,900,142,900,'2014-07-25 13:43:47',NULL,1),(472,1,48,901,143,901,'2014-07-25 13:43:47',NULL,1),(473,1,48,903,9,903,'2014-07-25 13:43:47',NULL,1),(474,1,48,906,144,906,'2014-07-25 13:43:47',NULL,1),(475,1,48,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(476,1,48,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(477,1,48,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(478,1,48,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(479,1,48,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(480,1,48,904,4,904,'2014-07-25 13:43:47',NULL,1),(481,1,49,900,145,900,'2014-07-25 13:43:47',NULL,1),(482,1,49,901,146,901,'2014-07-25 13:43:47',NULL,1),(483,1,49,903,10,903,'2014-07-25 13:43:47',NULL,1),(484,1,49,906,147,906,'2014-07-25 13:43:47',NULL,1),(485,1,49,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(486,1,49,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(487,1,49,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(488,1,49,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(489,1,49,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(490,1,49,904,4,904,'2014-07-25 13:43:47',NULL,1),(491,1,50,900,148,900,'2014-07-25 13:43:47',NULL,1),(492,1,50,901,149,901,'2014-07-25 13:43:47',NULL,1),(493,1,50,903,10,903,'2014-07-25 13:43:47',NULL,1),(494,1,50,906,150,906,'2014-07-25 13:43:47',NULL,1),(495,1,50,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(496,1,50,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(497,1,50,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(498,1,50,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(499,1,50,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(500,1,50,904,4,904,'2014-07-25 13:43:47',NULL,1),(501,1,51,900,151,900,'2014-07-25 13:43:47',NULL,1),(502,1,51,901,152,901,'2014-07-25 13:43:47',NULL,1),(503,1,51,903,10,903,'2014-07-25 13:43:47',NULL,1),(504,1,51,906,153,906,'2014-07-25 13:43:47',NULL,1),(505,1,51,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(506,1,51,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(507,1,51,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(508,1,51,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(509,1,51,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(510,1,51,904,4,904,'2014-07-25 13:43:47',NULL,1),(511,1,52,900,154,900,'2014-07-25 13:43:47',NULL,1),(512,1,52,901,155,901,'2014-07-25 13:43:47',NULL,1),(513,1,52,903,10,903,'2014-07-25 13:43:47',NULL,1),(514,1,52,906,156,906,'2014-07-25 13:43:47',NULL,1),(515,1,52,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(516,1,52,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(517,1,52,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(518,1,52,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(519,1,52,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(520,1,52,904,4,904,'2014-07-25 13:43:47',NULL,1),(521,1,53,900,157,900,'2014-07-25 13:43:47',NULL,1),(522,1,53,901,158,901,'2014-07-25 13:43:47',NULL,1),(523,1,53,903,10,903,'2014-07-25 13:43:47',NULL,1),(524,1,53,906,159,906,'2014-07-25 13:43:47',NULL,1),(525,1,53,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(526,1,53,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(527,1,53,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(528,1,53,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(529,1,53,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(530,1,53,904,4,904,'2014-07-25 13:43:47',NULL,1),(531,1,54,900,160,900,'2014-07-25 13:43:47',NULL,1),(532,1,54,901,161,901,'2014-07-25 13:43:47',NULL,1),(533,1,54,903,10,903,'2014-07-25 13:43:47',NULL,1),(534,1,54,906,162,906,'2014-07-25 13:43:47',NULL,1),(535,1,54,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(536,1,54,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(537,1,54,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(538,1,54,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(539,1,54,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(540,1,54,904,4,904,'2014-07-25 13:43:47',NULL,1),(541,1,55,900,163,900,'2014-07-25 13:43:47',NULL,1),(542,1,55,901,164,901,'2014-07-25 13:43:47',NULL,1),(543,1,55,903,11,903,'2014-07-25 13:43:47',NULL,1),(544,1,55,906,165,906,'2014-07-25 13:43:47',NULL,1),(545,1,55,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(546,1,55,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(547,1,55,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(548,1,55,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(549,1,55,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(550,1,55,904,4,904,'2014-07-25 13:43:47',NULL,1),(551,1,56,900,166,900,'2014-07-25 13:43:47',NULL,1),(552,1,56,901,167,901,'2014-07-25 13:43:47',NULL,1),(553,1,56,903,11,903,'2014-07-25 13:43:47',NULL,1),(554,1,56,906,168,906,'2014-07-25 13:43:47',NULL,1),(555,1,56,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(556,1,56,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(557,1,56,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(558,1,56,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(559,1,56,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(560,1,56,904,4,904,'2014-07-25 13:43:47',NULL,1),(561,1,57,900,169,900,'2014-07-25 13:43:47',NULL,1),(562,1,57,901,170,901,'2014-07-25 13:43:47',NULL,1),(563,1,57,903,11,903,'2014-07-25 13:43:47',NULL,1),(564,1,57,906,171,906,'2014-07-25 13:43:47',NULL,1),(565,1,57,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(566,1,57,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(567,1,57,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(568,1,57,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(569,1,57,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(570,1,57,904,4,904,'2014-07-25 13:43:47',NULL,1),(571,1,58,900,172,900,'2014-07-25 13:43:47',NULL,1),(572,1,58,901,173,901,'2014-07-25 13:43:47',NULL,1),(573,1,58,903,11,903,'2014-07-25 13:43:47',NULL,1),(574,1,58,906,174,906,'2014-07-25 13:43:47',NULL,1),(575,1,58,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(576,1,58,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(577,1,58,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(578,1,58,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(579,1,58,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(580,1,58,904,4,904,'2014-07-25 13:43:47',NULL,1),(581,1,59,900,175,900,'2014-07-25 13:43:47',NULL,1),(582,1,59,901,176,901,'2014-07-25 13:43:47',NULL,1),(583,1,59,903,11,903,'2014-07-25 13:43:47',NULL,1),(584,1,59,906,177,906,'2014-07-25 13:43:47',NULL,1),(585,1,59,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(586,1,59,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(587,1,59,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(588,1,59,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(589,1,59,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(590,1,59,904,4,904,'2014-07-25 13:43:47',NULL,1),(591,1,60,900,178,900,'2014-07-25 13:43:47',NULL,1),(592,1,60,901,179,901,'2014-07-25 13:43:47',NULL,1),(593,1,60,903,11,903,'2014-07-25 13:43:47',NULL,1),(594,1,60,906,180,906,'2014-07-25 13:43:47',NULL,1),(595,1,60,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(596,1,60,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(597,1,60,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(598,1,60,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(599,1,60,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(600,1,60,904,4,904,'2014-07-25 13:43:47',NULL,1),(601,1,61,900,181,900,'2014-07-25 13:43:47',NULL,1),(602,1,61,901,182,901,'2014-07-25 13:43:47',NULL,1),(603,1,61,903,12,903,'2014-07-25 13:43:47',NULL,1),(604,1,61,906,183,906,'2014-07-25 13:43:47',NULL,1),(605,1,61,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(606,1,61,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(607,1,61,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(608,1,61,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(609,1,61,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(610,1,61,904,4,904,'2014-07-25 13:43:47',NULL,1),(611,1,62,900,184,900,'2014-07-25 13:43:47',NULL,1),(612,1,62,901,185,901,'2014-07-25 13:43:47',NULL,1),(613,1,62,903,12,903,'2014-07-25 13:43:47',NULL,1),(614,1,62,906,186,906,'2014-07-25 13:43:47',NULL,1),(615,1,62,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(616,1,62,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(617,1,62,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(618,1,62,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(619,1,62,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(620,1,62,904,4,904,'2014-07-25 13:43:47',NULL,1),(621,1,63,900,187,900,'2014-07-25 13:43:47',NULL,1),(622,1,63,901,188,901,'2014-07-25 13:43:47',NULL,1),(623,1,63,903,12,903,'2014-07-25 13:43:47',NULL,1),(624,1,63,906,189,906,'2014-07-25 13:43:47',NULL,1),(625,1,63,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(626,1,63,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(627,1,63,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(628,1,63,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(629,1,63,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(630,1,63,904,4,904,'2014-07-25 13:43:47',NULL,1),(631,1,64,900,190,900,'2014-07-25 13:43:47',NULL,1),(632,1,64,901,191,901,'2014-07-25 13:43:47',NULL,1),(633,1,64,903,12,903,'2014-07-25 13:43:47',NULL,1),(634,1,64,906,192,906,'2014-07-25 13:43:47',NULL,1),(635,1,64,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(636,1,64,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(637,1,64,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(638,1,64,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(639,1,64,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(640,1,64,904,4,904,'2014-07-25 13:43:47',NULL,1),(641,1,65,900,193,900,'2014-07-25 13:43:47',NULL,1),(642,1,65,901,194,901,'2014-07-25 13:43:47',NULL,1),(643,1,65,903,12,903,'2014-07-25 13:43:47',NULL,1),(644,1,65,906,195,906,'2014-07-25 13:43:47',NULL,1),(645,1,65,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(646,1,65,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(647,1,65,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(648,1,65,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(649,1,65,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(650,1,65,904,4,904,'2014-07-25 13:43:47',NULL,1),(651,1,66,900,196,900,'2014-07-25 13:43:47',NULL,1),(652,1,66,901,197,901,'2014-07-25 13:43:47',NULL,1),(653,1,66,903,12,903,'2014-07-25 13:43:47',NULL,1),(654,1,66,906,198,906,'2014-07-25 13:43:47',NULL,1),(655,1,66,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(656,1,66,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(657,1,66,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(658,1,66,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(659,1,66,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(660,1,66,904,4,904,'2014-07-25 13:43:47',NULL,1),(661,1,67,900,199,900,'2014-07-25 13:43:47',NULL,1),(662,1,67,901,200,901,'2014-07-25 13:43:47',NULL,1),(663,1,67,903,12,903,'2014-07-25 13:43:47',NULL,1),(664,1,67,906,201,906,'2014-07-25 13:43:47',NULL,1),(665,1,67,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(666,1,67,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(667,1,67,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(668,1,67,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(669,1,67,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(670,1,67,904,4,904,'2014-07-25 13:43:47',NULL,1),(671,1,68,900,202,900,'2014-07-25 13:43:47',NULL,1),(672,1,68,901,203,901,'2014-07-25 13:43:47',NULL,1),(673,1,68,903,12,903,'2014-07-25 13:43:47',NULL,1),(674,1,68,906,204,906,'2014-07-25 13:43:47',NULL,1),(675,1,68,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(676,1,68,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(677,1,68,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(678,1,68,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(679,1,68,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(680,1,68,904,4,904,'2014-07-25 13:43:47',NULL,1),(681,1,69,900,205,900,'2014-07-25 13:43:47',NULL,1),(682,1,69,901,206,901,'2014-07-25 13:43:47',NULL,1),(683,1,69,903,12,903,'2014-07-25 13:43:47',NULL,1),(684,1,69,906,207,906,'2014-07-25 13:43:47',NULL,1),(685,1,69,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(686,1,69,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(687,1,69,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(688,1,69,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(689,1,69,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(690,1,69,904,4,904,'2014-07-25 13:43:47',NULL,1),(691,1,70,900,208,900,'2014-07-25 13:43:47',NULL,1),(692,1,70,901,209,901,'2014-07-25 13:43:47',NULL,1),(693,1,70,903,12,903,'2014-07-25 13:43:47',NULL,1),(694,1,70,906,210,906,'2014-07-25 13:43:47',NULL,1),(695,1,70,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(696,1,70,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(697,1,70,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(698,1,70,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(699,1,70,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(700,1,70,904,4,904,'2014-07-25 13:43:47',NULL,1),(701,1,71,900,211,900,'2014-07-25 13:43:47',NULL,1),(702,1,71,901,212,901,'2014-07-25 13:43:47',NULL,1),(703,1,71,903,13,903,'2014-07-25 13:43:47',NULL,1),(704,1,71,906,213,906,'2014-07-25 13:43:47',NULL,1),(705,1,71,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(706,1,71,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(707,1,71,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(708,1,71,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(709,1,71,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(710,1,71,904,4,904,'2014-07-25 13:43:47',NULL,1),(711,1,72,900,214,900,'2014-07-25 13:43:47',NULL,1),(712,1,72,901,215,901,'2014-07-25 13:43:47',NULL,1),(713,1,72,903,13,903,'2014-07-25 13:43:47',NULL,1),(714,1,72,906,216,906,'2014-07-25 13:43:47',NULL,1),(715,1,72,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(716,1,72,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(717,1,72,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(718,1,72,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(719,1,72,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(720,1,72,904,4,904,'2014-07-25 13:43:47',NULL,1),(721,1,73,900,217,900,'2014-07-25 13:43:47',NULL,1),(722,1,73,901,218,901,'2014-07-25 13:43:47',NULL,1),(723,1,73,903,13,903,'2014-07-25 13:43:47',NULL,1),(724,1,73,906,219,906,'2014-07-25 13:43:47',NULL,1),(725,1,73,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(726,1,73,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(727,1,73,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(728,1,73,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(729,1,73,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(730,1,73,904,4,904,'2014-07-25 13:43:47',NULL,1),(731,1,74,900,220,900,'2014-07-25 13:43:47',NULL,1),(732,1,74,901,221,901,'2014-07-25 13:43:47',NULL,1),(733,1,74,903,13,903,'2014-07-25 13:43:47',NULL,1),(734,1,74,906,222,906,'2014-07-25 13:43:47',NULL,1),(735,1,74,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(736,1,74,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(737,1,74,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(738,1,74,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(739,1,74,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(740,1,74,904,4,904,'2014-07-25 13:43:47',NULL,1),(741,1,75,900,223,900,'2014-07-25 13:43:47',NULL,1),(742,1,75,901,224,901,'2014-07-25 13:43:47',NULL,1),(743,1,75,903,13,903,'2014-07-25 13:43:47',NULL,1),(744,1,75,906,225,906,'2014-07-25 13:43:47',NULL,1),(745,1,75,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(746,1,75,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(747,1,75,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(748,1,75,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(749,1,75,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(750,1,75,904,4,904,'2014-07-25 13:43:47',NULL,1),(751,1,76,900,226,900,'2014-07-25 13:43:47',NULL,1),(752,1,76,901,227,901,'2014-07-25 13:43:47',NULL,1),(753,1,76,903,13,903,'2014-07-25 13:43:47',NULL,1),(754,1,76,906,228,906,'2014-07-25 13:43:47',NULL,1),(755,1,76,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(756,1,76,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(757,1,76,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(758,1,76,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(759,1,76,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(760,1,76,904,4,904,'2014-07-25 13:43:47',NULL,1),(761,1,77,900,229,900,'2014-07-25 13:43:47',NULL,1),(762,1,77,901,230,901,'2014-07-25 13:43:47',NULL,1),(763,1,77,903,13,903,'2014-07-25 13:43:47',NULL,1),(764,1,77,906,231,906,'2014-07-25 13:43:47',NULL,1),(765,1,77,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(766,1,77,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(767,1,77,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(768,1,77,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(769,1,77,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(770,1,77,904,4,904,'2014-07-25 13:43:47',NULL,1),(771,1,78,900,232,900,'2014-07-25 13:43:47',NULL,1),(772,1,78,901,233,901,'2014-07-25 13:43:47',NULL,1),(773,1,78,903,13,903,'2014-07-25 13:43:47',NULL,1),(774,1,78,906,234,906,'2014-07-25 13:43:47',NULL,1),(775,1,78,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(776,1,78,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(777,1,78,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(778,1,78,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(779,1,78,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(780,1,78,904,4,904,'2014-07-25 13:43:47',NULL,1),(781,1,79,900,235,900,'2014-07-25 13:43:47',NULL,1),(782,1,79,901,236,901,'2014-07-25 13:43:47',NULL,1),(783,1,79,903,13,903,'2014-07-25 13:43:47',NULL,1),(784,1,79,906,237,906,'2014-07-25 13:43:47',NULL,1),(785,1,79,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(786,1,79,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(787,1,79,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(788,1,79,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(789,1,79,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(790,1,79,904,4,904,'2014-07-25 13:43:47',NULL,1),(791,1,80,900,238,900,'2014-07-25 13:43:47',NULL,1),(792,1,80,901,239,901,'2014-07-25 13:43:47',NULL,1),(793,1,80,903,13,903,'2014-07-25 13:43:47',NULL,1),(794,1,80,906,240,906,'2014-07-25 13:43:47',NULL,1),(795,1,80,913,NULL,913,'2014-07-25 13:43:47',NULL,1),(796,1,80,914,NULL,914,'2014-07-25 13:43:47',NULL,1),(797,1,80,916,NULL,916,'2014-07-25 13:43:47',NULL,1),(798,1,80,917,NULL,917,'2014-07-25 13:43:47',NULL,1),(799,1,80,918,NULL,918,'2014-07-25 13:43:47',NULL,1),(800,1,80,904,4,904,'2014-07-25 13:43:47',NULL,1),(801,1,81,900,241,900,'2014-07-25 13:49:12',NULL,1),(802,1,81,901,242,901,'2014-07-25 13:49:12',NULL,1),(803,1,81,906,243,906,'2014-07-25 13:49:12',NULL,1),(804,1,81,913,244,913,'2014-07-25 13:49:12',NULL,1),(805,1,81,914,245,914,'2014-07-25 13:49:12',NULL,1),(806,1,81,915,246,915,'2014-07-25 13:49:12',NULL,1),(807,1,81,916,247,916,'2014-07-25 13:49:12',NULL,1),(808,1,81,917,248,917,'2014-07-25 13:49:12',NULL,1),(809,1,81,904,1,904,'2014-07-25 13:49:12',NULL,1),(810,1,7,904,2,904,'2014-07-25 14:04:22',NULL,1),(811,1,7,913,249,913,'2014-07-25 14:04:22',NULL,1),(812,1,7,914,250,914,'2014-07-25 14:04:22',NULL,1),(813,1,7,916,251,916,'2014-07-25 14:04:22',NULL,1),(814,1,7,917,252,917,'2014-07-25 14:04:22',NULL,1),(815,1,7,918,253,918,'2014-07-25 14:04:22',NULL,1),(816,1,7,915,254,915,'2014-07-25 14:04:22',NULL,1),(817,1,1,919,3,919,'2014-07-27 16:30:56',NULL,1),(818,1,7,919,4,919,'2014-08-19 07:43:21',NULL,1),(819,1,82,900,255,900,'2014-08-25 10:13:29',NULL,1),(820,1,82,901,256,901,'2014-08-25 10:13:29',NULL,1),(821,1,82,913,257,913,'2014-08-25 10:13:29',NULL,1),(822,1,82,914,258,914,'2014-08-25 10:13:29',NULL,1),(823,1,82,915,259,915,'2014-08-25 10:13:29',NULL,1),(824,1,82,916,260,916,'2014-08-25 10:13:29',NULL,1),(825,1,82,917,261,917,'2014-08-25 10:13:29',NULL,1),(826,1,82,904,3,904,'2014-08-25 10:13:29','2014-08-25 10:14:09',2),(827,1,82,904,1,904,'2014-08-25 10:14:09',NULL,1),(828,2,82,904,3,904,'2014-08-25 10:14:09',NULL,1);
/*!40000 ALTER TABLE `organization_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organization_correction`
--

DROP TABLE IF EXISTS `organization_correction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organization_correction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта организация',
  `external_id` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Внешний идентификатор организации',
  `correction` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Код организации',
  `begin_date` date NOT NULL DEFAULT '1970-01-01' COMMENT 'Дата начала актуальности соответствия',
  `end_date` date NOT NULL DEFAULT '2054-12-31' COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` bigint(20) NOT NULL COMMENT 'Идентификатор организации',
  `user_organization_id` bigint(20) DEFAULT NULL,
  `module_id` bigint(20) NOT NULL COMMENT 'Идентификатор модуля',
  `status` int(11) DEFAULT NULL COMMENT 'Статус',
  PRIMARY KEY (`id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_begin_date` (`begin_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  KEY `key_module_id` (`module_id`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_organization__correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_organization__correction__organization_object` FOREIGN KEY (`object_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_organization__correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Коррекция организации';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organization_correction`
--

LOCK TABLES `organization_correction` WRITE;
/*!40000 ALTER TABLE `organization_correction` DISABLE KEYS */;
/*!40000 ALTER TABLE `organization_correction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organization_import`
--

DROP TABLE IF EXISTS `organization_import`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organization_import` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `organization_id` bigint(20) NOT NULL COMMENT 'ID организации',
  `code` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Код организации',
  `short_name` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Короткое название организации',
  `full_name` varchar(500) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Полное название организации',
  `hlevel` bigint(20) DEFAULT NULL COMMENT 'Ссылка на вышестоящую организацию',
  PRIMARY KEY (`pk_id`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_hlevel` (`hlevel`)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Вспомогательная таблица для импорта организаций';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organization_import`
--

LOCK TABLES `organization_import` WRITE;
/*!40000 ALTER TABLE `organization_import` DISABLE KEYS */;
INSERT INTO `organization_import` VALUES (1,27575681,'ЧЕР','ЧЕРВОНОЗАВОДСКИЙ Р-Н','ЧЕРВОНОЗАВОДСКИЙ РАЙОН КП \"ЖИЛКОМСЕРВИС\"',5894269),(2,59631157,'ФРУ','ФРУНЗЕНСКИЙ Р-Н','ФРУНЗЕНСКИЙ РАЙОН КП \"ЖИЛКОМСЕРВИС\"',5894269),(3,274770718,'42У','УЧАСТОК № 42','УЧАСТОК № 42',30911211),(4,274772616,'34У','УЧАСТОК № 34','УЧАСТОК № 34',5894272),(5,274772626,'41У','УЧАСТОК № 41','УЧАСТОК № 41',5894272),(6,274774649,'24У','УЧАСТОК № 24','УЧАСТОК № 24',9411966),(7,274775708,'3У','УЧАСТОК № 3','УЧАСТОК № 3',42737688),(8,274776337,'46У','УЧАСТОК № 46','УЧАСТОК № 46',3737045),(9,274776546,'49У','УЧАСТОК № 49','УЧАСТОК № 49',3737045),(10,274776763,'52У','УЧАСТОК № 52','УЧАСТОК № 52',59631157),(11,274776768,'56У','УЧАСТОК № 56','УЧАСТОК № 56',59631157),(12,274777276,'16У','УЧАСТОК № 16','УЧАСТОК № 16',44908366),(13,45095841,'ЛЕН','ЛЕНИНСКИЙ Р-Н','ЛЕНИНСКИЙ РАЙОН КП \"ЖИЛКОМСЕРВИС\"',5894269),(14,274770719,'43У','УЧАСТОК № 43','УЧАСТОК № 43',30911211),(15,274772623,'39У','УЧАСТОК № 39','УЧАСТОК № 39',5894272),(16,274772625,'40У','УЧАСТОК № 40','УЧАСТОК № 40',5894272),(17,274774647,'22У','УЧАСТОК № 22','УЧАСТОК № 22',9411966),(18,274774765,'25У','УЧАСТОК № 25','УЧАСТОК № 25',9411966),(19,274775711,'6У','УЧАСТОК № 6','УЧАСТОК № 6',42737688),(20,274775712,'7У','УЧАСТОК № 7','УЧАСТОК № 7',42737688),(21,5894272,'МОС','МОСКОВСКИЙ Р-Н','МОСКОВСКИЙ РАЙОН КП \"ЖИЛКОМСЕРВИС\"',5894269),(22,9411966,'КОМ','КОМИНТЕРНОВСКИЙ Р-Н','КОМИНТЕРНОВСКИЙ РАЙОН КП \"ЖИЛКОМСЕРВИС\"',5894269),(23,274770720,'44У','УЧАСТОК № 44','УЧАСТОК № 44',30911211),(24,274772622,'38У','УЧАСТОК № 38','УЧАСТОК № 38',5894272),(25,274773578,'28У','УЧАСТОК № 28','УЧАСТОК № 28',45095841),(26,274774565,'21У','УЧАСТОК № 21','УЧАСТОК № 21',9411966),(27,274774648,'23У','УЧАСТОК № 23','УЧАСТОК № 23',9411966),(28,274776338,'47У','УЧАСТОК № 47','УЧАСТОК № 47',3737045),(29,274776341,'51У','УЧАСТОК № 51','УЧАСТОК № 51',3737045),(30,274777278,'18У','УЧАСТОК № 18','УЧАСТОК № 18',44908366),(31,267869803,'59У','УЧАСТОК № 59','УЧАСТОК № 59',27575681),(32,267869805,'60У','УЧАСТОК № 60','УЧАСТОК № 60',27575681),(33,3737045,'ОРД','ОРДЖОНИКИДЗЕВСКИЙ Р-Н','ОРДЖОНИКИДЗЕВСКИЙ РАЙОН КП \"ЖИЛКОМСЕРВИС\"',5894269),(34,274772613,'31У','УЧАСТОК № 31','УЧАСТОК № 31',5894272),(35,274773580,'63У','УЧАСТОК № 63','УЧАСТОК № 63',45095841),(36,274775704,'1У','УЧАСТОК № 1','УЧАСТОК № 1',42737688),(37,274775714,'9У','УЧАСТОК № 9','УЧАСТОК № 9',42737688),(38,274776339,'48У','УЧАСТОК № 48','УЧАСТОК № 48',3737045),(39,274776340,'50У','УЧАСТОК № 50','УЧАСТОК № 50',3737045),(40,274777279,'19У','УЧАСТОК № 19','УЧАСТОК № 19',44908366),(41,319927759,'65У','УЧАСТОК № 65','УЧАСТОК № 65',30911211),(42,30911211,'ОКТ','ОКТЯБРЬСКИЙ Р-Н','ОКТЯБРЬСКИЙ РАЙОН КП \"ЖИЛКОМСЕРВИС\"',5894269),(43,274772572,'30У','УЧАСТОК № 30','УЧАСТОК № 30',5894272),(44,274772614,'32У','УЧАСТОК № 32','УЧАСТОК № 32',5894272),(45,274772615,'33У','УЧАСТОК № 33','УЧАСТОК № 33',5894272),(46,274773575,'26У','УЧАСТОК № 26','УЧАСТОК № 26',45095841),(47,274775713,'8У','УЧАСТОК № 8','УЧАСТОК № 8',42737688),(48,274776766,'54У','УЧАСТОК № 54','УЧАСТОК № 54',59631157),(49,274777277,'17У','УЧАСТОК № 17','УЧАСТОК № 17',44908366),(50,274777280,'64У','УЧАСТОК № 64','УЧАСТОК № 64',44908366),(51,267869802,'58У','УЧАСТОК № 58','УЧАСТОК № 58',27575681),(52,267869807,'61У','УЧАСТОК № 61','УЧАСТОК № 61',27575681),(53,274772618,'35У','УЧАСТОК № 35','УЧАСТОК № 35',5894272),(54,274772619,'36У','УЧАСТОК № 36','УЧАСТОК № 36',5894272),(55,274773577,'29У','УЧАСТОК № 29','УЧАСТОК № 29',45095841),(56,274776764,'53У','УЧАСТОК № 53','УЧАСТОК № 53',59631157),(57,274777271,'12У','УЧАСТОК № 12','УЧАСТОК № 12',44908366),(58,3659392,'','ЦН','ЦЕНТР НАЧИСЛЕНИЙ',NULL),(59,2144343384,'ХСС','КП \"ХАРЬКОВСПЕЦСТРОЙ\"','КП \"ХАРЬКОВСПЕЦСТРОЙ\"',3659392),(60,2144343497,'ГУРТ','ГУРТОЖИТКИ','ГУРТОЖИТКИ',2144343384),(61,44908366,'КИЕ','КИЕВСКИЙ Р-Н','КИЕВСКИЙ РАЙОН КП \"ЖИЛКОМСЕРВИС\"',5894269),(62,274770721,'45У','УЧАСТОК № 45','УЧАСТОК № 45',30911211),(63,274772620,'37У','УЧАСТОК № 37','УЧАСТОК № 37',5894272),(64,274774435,'20У','УЧАСТОК № 20','УЧАСТОК № 20',9411966),(65,274775715,'10У','УЧАСТОК № 10','УЧАСТОК № 10',42737688),(66,274776769,'57У','УЧАСТОК № 57','УЧАСТОК № 57',59631157),(67,5894269,'ЖКС','ЖИЛКОМСЕРВИС','КП \"ЖИЛКОМСЕРВИС\"',3659392),(68,2154473691,'OK','ОКТЯБРЬСКИЙ Р-ОН','ОКТЯБРЬСКИЙ Р-ОН',2144343497),(69,42737688,'ДЗЕ','ДЗЕРЖИНСКИЙ Р-Н','ДЗЕРЖИНСКИЙ РАЙОН КП \"ЖИЛКОМСЕРВИС\"',5894269),(70,274773576,'27У','УЧАСТОК № 27','УЧАСТОК № 27',45095841),(71,274773579,'62У','УЧАСТОК № 62','УЧАСТОК № 62',45095841),(72,274775707,'2У','УЧАСТОК № 2','УЧАСТОК № 2',42737688),(73,274775709,'4У','УЧАСТОК № 4','УЧАСТОК № 4',42737688),(74,274775710,'5У','УЧАСТОК № 5','УЧАСТОК № 5',42737688),(75,274776767,'55У','УЧАСТОК № 55','УЧАСТОК № 55',59631157),(76,274777270,'11У','УЧАСТОК № 11','УЧАСТОК № 11',44908366),(77,274777272,'13У','УЧАСТОК № 13','УЧАСТОК № 13',44908366),(78,274777273,'14У','УЧАСТОК № 14','УЧАСТОК № 14',44908366),(79,274777274,'15У','УЧАСТОК № 15','УЧАСТОК № 15',44908366),(80,476729235,'66У','УЧАСТОК № 66','УЧАСТОК № 66',30911211);
/*!40000 ALTER TABLE `organization_import` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organization_string_value`
--

DROP TABLE IF EXISTS `organization_string_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organization_string_value` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` bigint(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` bigint(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_organization_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=263 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Локализация атрибутов организации';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organization_string_value`
--

LOCK TABLES `organization_string_value` WRITE;
/*!40000 ALTER TABLE `organization_string_value` DISABLE KEYS */;
INSERT INTO `organization_string_value` VALUES (1,1,1,'МОДУЛЬ ЕИРЦ №1'),(2,1,2,'МОДУЛЬ ЕIРЦ №1'),(3,2,1,'ЦЕНТР НАЧИСЛЕНИЙ'),(4,3,1,'ЦН'),(5,4,1,'КП \"ХАРЬКОВСПЕЦСТРОЙ\"'),(6,5,1,'ХСС'),(7,6,1,'КП \"ХАРЬКОВСПЕЦСТРОЙ\"'),(8,7,1,'КП \"ЖИЛКОМСЕРВИС\"'),(9,8,1,'ЖКС'),(10,9,1,'ЖИЛКОМСЕРВИС'),(11,10,1,'ГУРТОЖИТКИ'),(12,11,1,'ГУРТ'),(13,12,1,'ГУРТОЖИТКИ'),(14,13,1,'ЧЕРВОНОЗАВОДСКИЙ РАЙОН КП \"ЖИЛКОМСЕРВИС\"'),(15,14,1,'ЧЕР'),(16,15,1,'ЧЕРВОНОЗАВОДСКИЙ Р-Н'),(17,16,1,'ФРУНЗЕНСКИЙ РАЙОН КП \"ЖИЛКОМСЕРВИС\"'),(18,17,1,'ФРУ'),(19,18,1,'ФРУНЗЕНСКИЙ Р-Н'),(20,19,1,'ЛЕНИНСКИЙ РАЙОН КП \"ЖИЛКОМСЕРВИС\"'),(21,20,1,'ЛЕН'),(22,21,1,'ЛЕНИНСКИЙ Р-Н'),(23,22,1,'МОСКОВСКИЙ РАЙОН КП \"ЖИЛКОМСЕРВИС\"'),(24,23,1,'МОС'),(25,24,1,'МОСКОВСКИЙ Р-Н'),(26,25,1,'КОМИНТЕРНОВСКИЙ РАЙОН КП \"ЖИЛКОМСЕРВИС\"'),(27,26,1,'КОМ'),(28,27,1,'КОМИНТЕРНОВСКИЙ Р-Н'),(29,28,1,'ОРДЖОНИКИДЗЕВСКИЙ РАЙОН КП \"ЖИЛКОМСЕРВИС\"'),(30,29,1,'ОРД'),(31,30,1,'ОРДЖОНИКИДЗЕВСКИЙ Р-Н'),(32,31,1,'ОКТЯБРЬСКИЙ РАЙОН КП \"ЖИЛКОМСЕРВИС\"'),(33,32,1,'ОКТ'),(34,33,1,'ОКТЯБРЬСКИЙ Р-Н'),(35,34,1,'КИЕВСКИЙ РАЙОН КП \"ЖИЛКОМСЕРВИС\"'),(36,35,1,'КИЕ'),(37,36,1,'КИЕВСКИЙ Р-Н'),(38,37,1,'ДЗЕРЖИНСКИЙ РАЙОН КП \"ЖИЛКОМСЕРВИС\"'),(39,38,1,'ДЗЕ'),(40,39,1,'ДЗЕРЖИНСКИЙ Р-Н'),(41,40,1,'ОКТЯБРЬСКИЙ Р-ОН'),(42,41,1,'OK'),(43,42,1,'ОКТЯБРЬСКИЙ Р-ОН'),(44,43,1,'УЧАСТОК № 59'),(45,44,1,'59У'),(46,45,1,'УЧАСТОК № 59'),(47,46,1,'УЧАСТОК № 60'),(48,47,1,'60У'),(49,48,1,'УЧАСТОК № 60'),(50,49,1,'УЧАСТОК № 58'),(51,50,1,'58У'),(52,51,1,'УЧАСТОК № 58'),(53,52,1,'УЧАСТОК № 61'),(54,53,1,'61У'),(55,54,1,'УЧАСТОК № 61'),(56,55,1,'УЧАСТОК № 52'),(57,56,1,'52У'),(58,57,1,'УЧАСТОК № 52'),(59,58,1,'УЧАСТОК № 56'),(60,59,1,'56У'),(61,60,1,'УЧАСТОК № 56'),(62,61,1,'УЧАСТОК № 54'),(63,62,1,'54У'),(64,63,1,'УЧАСТОК № 54'),(65,64,1,'УЧАСТОК № 53'),(66,65,1,'53У'),(67,66,1,'УЧАСТОК № 53'),(68,67,1,'УЧАСТОК № 57'),(69,68,1,'57У'),(70,69,1,'УЧАСТОК № 57'),(71,70,1,'УЧАСТОК № 55'),(72,71,1,'55У'),(73,72,1,'УЧАСТОК № 55'),(74,73,1,'УЧАСТОК № 28'),(75,74,1,'28У'),(76,75,1,'УЧАСТОК № 28'),(77,76,1,'УЧАСТОК № 63'),(78,77,1,'63У'),(79,78,1,'УЧАСТОК № 63'),(80,79,1,'УЧАСТОК № 26'),(81,80,1,'26У'),(82,81,1,'УЧАСТОК № 26'),(83,82,1,'УЧАСТОК № 29'),(84,83,1,'29У'),(85,84,1,'УЧАСТОК № 29'),(86,85,1,'УЧАСТОК № 27'),(87,86,1,'27У'),(88,87,1,'УЧАСТОК № 27'),(89,88,1,'УЧАСТОК № 62'),(90,89,1,'62У'),(91,90,1,'УЧАСТОК № 62'),(92,91,1,'УЧАСТОК № 34'),(93,92,1,'34У'),(94,93,1,'УЧАСТОК № 34'),(95,94,1,'УЧАСТОК № 41'),(96,95,1,'41У'),(97,96,1,'УЧАСТОК № 41'),(98,97,1,'УЧАСТОК № 39'),(99,98,1,'39У'),(100,99,1,'УЧАСТОК № 39'),(101,100,1,'УЧАСТОК № 40'),(102,101,1,'40У'),(103,102,1,'УЧАСТОК № 40'),(104,103,1,'УЧАСТОК № 38'),(105,104,1,'38У'),(106,105,1,'УЧАСТОК № 38'),(107,106,1,'УЧАСТОК № 31'),(108,107,1,'31У'),(109,108,1,'УЧАСТОК № 31'),(110,109,1,'УЧАСТОК № 30'),(111,110,1,'30У'),(112,111,1,'УЧАСТОК № 30'),(113,112,1,'УЧАСТОК № 32'),(114,113,1,'32У'),(115,114,1,'УЧАСТОК № 32'),(116,115,1,'УЧАСТОК № 33'),(117,116,1,'33У'),(118,117,1,'УЧАСТОК № 33'),(119,118,1,'УЧАСТОК № 35'),(120,119,1,'35У'),(121,120,1,'УЧАСТОК № 35'),(122,121,1,'УЧАСТОК № 36'),(123,122,1,'36У'),(124,123,1,'УЧАСТОК № 36'),(125,124,1,'УЧАСТОК № 37'),(126,125,1,'37У'),(127,126,1,'УЧАСТОК № 37'),(128,127,1,'УЧАСТОК № 24'),(129,128,1,'24У'),(130,129,1,'УЧАСТОК № 24'),(131,130,1,'УЧАСТОК № 22'),(132,131,1,'22У'),(133,132,1,'УЧАСТОК № 22'),(134,133,1,'УЧАСТОК № 25'),(135,134,1,'25У'),(136,135,1,'УЧАСТОК № 25'),(137,136,1,'УЧАСТОК № 21'),(138,137,1,'21У'),(139,138,1,'УЧАСТОК № 21'),(140,139,1,'УЧАСТОК № 23'),(141,140,1,'23У'),(142,141,1,'УЧАСТОК № 23'),(143,142,1,'УЧАСТОК № 20'),(144,143,1,'20У'),(145,144,1,'УЧАСТОК № 20'),(146,145,1,'УЧАСТОК № 46'),(147,146,1,'46У'),(148,147,1,'УЧАСТОК № 46'),(149,148,1,'УЧАСТОК № 49'),(150,149,1,'49У'),(151,150,1,'УЧАСТОК № 49'),(152,151,1,'УЧАСТОК № 47'),(153,152,1,'47У'),(154,153,1,'УЧАСТОК № 47'),(155,154,1,'УЧАСТОК № 51'),(156,155,1,'51У'),(157,156,1,'УЧАСТОК № 51'),(158,157,1,'УЧАСТОК № 48'),(159,158,1,'48У'),(160,159,1,'УЧАСТОК № 48'),(161,160,1,'УЧАСТОК № 50'),(162,161,1,'50У'),(163,162,1,'УЧАСТОК № 50'),(164,163,1,'УЧАСТОК № 42'),(165,164,1,'42У'),(166,165,1,'УЧАСТОК № 42'),(167,166,1,'УЧАСТОК № 43'),(168,167,1,'43У'),(169,168,1,'УЧАСТОК № 43'),(170,169,1,'УЧАСТОК № 44'),(171,170,1,'44У'),(172,171,1,'УЧАСТОК № 44'),(173,172,1,'УЧАСТОК № 65'),(174,173,1,'65У'),(175,174,1,'УЧАСТОК № 65'),(176,175,1,'УЧАСТОК № 45'),(177,176,1,'45У'),(178,177,1,'УЧАСТОК № 45'),(179,178,1,'УЧАСТОК № 66'),(180,179,1,'66У'),(181,180,1,'УЧАСТОК № 66'),(182,181,1,'УЧАСТОК № 16'),(183,182,1,'16У'),(184,183,1,'УЧАСТОК № 16'),(185,184,1,'УЧАСТОК № 18'),(186,185,1,'18У'),(187,186,1,'УЧАСТОК № 18'),(188,187,1,'УЧАСТОК № 19'),(189,188,1,'19У'),(190,189,1,'УЧАСТОК № 19'),(191,190,1,'УЧАСТОК № 17'),(192,191,1,'17У'),(193,192,1,'УЧАСТОК № 17'),(194,193,1,'УЧАСТОК № 64'),(195,194,1,'64У'),(196,195,1,'УЧАСТОК № 64'),(197,196,1,'УЧАСТОК № 12'),(198,197,1,'12У'),(199,198,1,'УЧАСТОК № 12'),(200,199,1,'УЧАСТОК № 11'),(201,200,1,'11У'),(202,201,1,'УЧАСТОК № 11'),(203,202,1,'УЧАСТОК № 13'),(204,203,1,'13У'),(205,204,1,'УЧАСТОК № 13'),(206,205,1,'УЧАСТОК № 14'),(207,206,1,'14У'),(208,207,1,'УЧАСТОК № 14'),(209,208,1,'УЧАСТОК № 15'),(210,209,1,'15У'),(211,210,1,'УЧАСТОК № 15'),(212,211,1,'УЧАСТОК № 3'),(213,212,1,'3У'),(214,213,1,'УЧАСТОК № 3'),(215,214,1,'УЧАСТОК № 6'),(216,215,1,'6У'),(217,216,1,'УЧАСТОК № 6'),(218,217,1,'УЧАСТОК № 7'),(219,218,1,'7У'),(220,219,1,'УЧАСТОК № 7'),(221,220,1,'УЧАСТОК № 1'),(222,221,1,'1У'),(223,222,1,'УЧАСТОК № 1'),(224,223,1,'УЧАСТОК № 9'),(225,224,1,'9У'),(226,225,1,'УЧАСТОК № 9'),(227,226,1,'УЧАСТОК № 8'),(228,227,1,'8У'),(229,228,1,'УЧАСТОК № 8'),(230,229,1,'УЧАСТОК № 10'),(231,230,1,'10У'),(232,231,1,'УЧАСТОК № 10'),(233,232,1,'УЧАСТОК № 2'),(234,233,1,'2У'),(235,234,1,'УЧАСТОК № 2'),(236,235,1,'УЧАСТОК № 4'),(237,236,1,'4У'),(238,237,1,'УЧАСТОК № 4'),(239,238,1,'УЧАСТОК № 5'),(240,239,1,'5У'),(241,240,1,'УЧАСТОК № 5'),(242,241,1,'ЕИРЦ-1'),(243,242,1,'ЕИРЦ-1'),(244,243,1,'ЕИРЦ-1'),(245,244,1,'111'),(246,245,1,'1111'),(247,246,1,'111'),(248,247,1,'111'),(249,248,1,'111'),(250,249,1,'111'),(251,250,1,'111'),(252,251,1,'111'),(253,252,1,'111'),(254,253,1,'111'),(255,254,1,'111'),(256,255,1,'ПЕЙМЕНТС 1'),(257,256,1,'П1'),(258,257,1,'1111'),(259,258,1,'1111'),(260,259,1,'1111'),(261,260,1,'1111'),(262,261,1,'11111');
/*!40000 ALTER TABLE `organization_string_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organization_type`
--

DROP TABLE IF EXISTS `organization_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organization_type` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор родительского объекта: не используется',
  `parent_entity_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор сущности родительского объекта: не используется',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  `permission_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'Ключ прав доступа к объекту',
  `external_id` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_organization_type__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_organization_type__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Тип организации';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organization_type`
--

LOCK TABLES `organization_type` WRITE;
/*!40000 ALTER TABLE `organization_type` DISABLE KEYS */;
INSERT INTO `organization_type` VALUES (1,1,NULL,NULL,'2014-07-25 03:38:43',NULL,1,0,NULL),(2,4,NULL,NULL,'2014-07-25 03:38:43',NULL,1,0,NULL),(3,2,NULL,NULL,'2014-07-25 03:38:43',NULL,1,0,NULL),(4,3,NULL,NULL,'2014-07-25 03:38:43',NULL,1,0,NULL);
/*!40000 ALTER TABLE `organization_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organization_type_attribute`
--

DROP TABLE IF EXISTS `organization_type_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organization_type_attribute` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` bigint(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 2300 - НАИМЕНОВАНИЕ ТИПА ОРГАНИЗАЦИИ',
  `value_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор значения',
  `value_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа значения атрибута: 2300 - STRING_VALUE',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_organization_type_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`) REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_organization_type_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`) REFERENCES `entity_attribute_value_type` (`id`),
  CONSTRAINT `fk_organization_type_attribute__organization` FOREIGN KEY (`object_id`) REFERENCES `organization_type` (`object_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Атрибуты типа организации';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organization_type_attribute`
--

LOCK TABLES `organization_type_attribute` WRITE;
/*!40000 ALTER TABLE `organization_type_attribute` DISABLE KEYS */;
INSERT INTO `organization_type_attribute` VALUES (1,1,1,2300,1,2300,'2014-07-25 03:38:43',NULL,1),(2,1,4,2300,4,2300,'2014-07-25 03:38:43',NULL,1),(3,1,2,2300,2,2300,'2014-07-25 03:38:43',NULL,1),(4,2,3,2300,3,2300,'2014-07-25 03:38:43',NULL,1);
/*!40000 ALTER TABLE `organization_type_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organization_type_string_value`
--

DROP TABLE IF EXISTS `organization_type_string_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organization_type_string_value` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` bigint(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` bigint(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_organization_type_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Локализация атрибутов типа организации';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organization_type_string_value`
--

LOCK TABLES `organization_type_string_value` WRITE;
/*!40000 ALTER TABLE `organization_type_string_value` DISABLE KEYS */;
INSERT INTO `organization_type_string_value` VALUES (1,1,1,'ОРГАНИЗАЦИИ ПОЛЬЗОВАТЕЛЕЙ'),(2,1,2,'ОРГАНИЗАЦИИ ПОЛЬЗОВАТЕЛЕЙ'),(3,4,1,'ОБСЛУЖИВАЮЩАЯ ОРГАНИЗАЦИЯ'),(4,4,2,'ОБСЛУЖИВАЮЩАЯ ОРГАНИЗАЦИЯ'),(5,2,1,'ПОСТАВЩИК УСЛУГ'),(6,2,2,'ПОСТАЧАЛЬНИК ПОСЛУГ'),(7,3,1,'СБОРЩИК ПЛАТЕЖЕЙ'),(8,3,2,'ЗБИРАЧ ПЛАТЕЖІВ');
/*!40000 ALTER TABLE `organization_type_string_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `owner_exemption`
--

DROP TABLE IF EXISTS `owner_exemption`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `owner_exemption` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `object_id` bigint(20) NOT NULL,
  `service_provider_account_id` bigint(20) NOT NULL,
  `first_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `last_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `middle_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `inn` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `begin_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`begin_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_begin_date` (`begin_date`),
  KEY `key_end_date` (`end_date`),
  KEY `fk_owner_exemption__sp_account` (`service_provider_account_id`),
  CONSTRAINT `fk_owner_exemption__sp_account` FOREIGN KEY (`service_provider_account_id`) REFERENCES `service_provider_account` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Носители льготы';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `owner_exemption`
--

LOCK TABLES `owner_exemption` WRITE;
/*!40000 ALTER TABLE `owner_exemption` DISABLE KEYS */;
/*!40000 ALTER TABLE `owner_exemption` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permission`
--

DROP TABLE IF EXISTS `permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `permission` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `permission_id` bigint(20) NOT NULL COMMENT 'Идентификатор права доступа',
  `table` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Таблица',
  `entity` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Сущность',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `key_unique` (`permission_id`,`entity`,`object_id`),
  KEY `key_permission_id` (`permission_id`),
  KEY `key_table` (`table`),
  KEY `key_entity` (`entity`),
  KEY `key_object_id` (`object_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Права доступа';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permission`
--

LOCK TABLES `permission` WRITE;
/*!40000 ALTER TABLE `permission` DISABLE KEYS */;
INSERT INTO `permission` VALUES (1,0,'ALL','ALL',0);
/*!40000 ALTER TABLE `permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `preference`
--

DROP TABLE IF EXISTS `preference`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `preference` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор предпочтения',
  `user_id` bigint(20) NOT NULL COMMENT 'Идентификатор пользователя',
  `page` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Класс страницы',
  `key` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Ключ',
  `value` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_key` (`user_id`,`page`,`key`),
  KEY `key_user_id` (`user_id`),
  CONSTRAINT `fk_preference__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Предпочтения пользователя';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `preference`
--

LOCK TABLES `preference` WRITE;
/*!40000 ALTER TABLE `preference` DISABLE KEYS */;
INSERT INTO `preference` VALUES (1,2,'global','locale','ru'),(2,1,'global','locale','ru'),(3,1,'global#search_component_state','country','1'),(4,1,'global#search_component_state','city','1'),(5,1,'global#search_component_state','street','1'),(6,1,'global#search_component_state','region','1'),(7,1,'global#search_component_state','room','-1'),(8,1,'global#search_component_state','apartment','-1'),(9,1,'global#search_component_state','building','1'),(10,1,'class ru.flexpay.eirc.registry.web.list.RegistryRecordList','CURRENT_PAGE','0'),(11,1,'ru.flexpay.eirc.eirc_account.web.list.EircAccountList','CURRENT_PAGE','2'),(12,1,'org.complitex.logging.web.LogList','CURRENT_PAGE','5'),(13,3,'global','locale','ru'),(14,3,'global','is_use_default_search_component_state','false'),(15,4,'global','locale','ru'),(16,4,'global','is_use_default_search_component_state','false'),(17,1,'org.complitex.common.strategy.web.DomainObjectListPanel#organization','CURRENT_PAGE','0');
/*!40000 ALTER TABLE `preference` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `region`
--

DROP TABLE IF EXISTS `region`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `region` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор сущности родительского объекта: 800 - country',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  `permission_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'Ключ прав доступа к объекту',
  `external_id` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_region__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_region__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Регион';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `region`
--

LOCK TABLES `region` WRITE;
/*!40000 ALTER TABLE `region` DISABLE KEYS */;
INSERT INTO `region` VALUES (1,1,1,800,'2014-07-25 13:43:49',NULL,1,0,'3659379');
/*!40000 ALTER TABLE `region` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `region_attribute`
--

DROP TABLE IF EXISTS `region_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `region_attribute` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` bigint(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 700 - НАИМЕНОВАНИЕ РЕГИОНА',
  `value_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор значения',
  `value_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа значения атрибута: 700 - STRING_VALUE',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_region_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`) REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_region_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`) REFERENCES `entity_attribute_value_type` (`id`),
  CONSTRAINT `fk_region_attribute__region` FOREIGN KEY (`object_id`) REFERENCES `region` (`object_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Атрибуты региона';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `region_attribute`
--

LOCK TABLES `region_attribute` WRITE;
/*!40000 ALTER TABLE `region_attribute` DISABLE KEYS */;
INSERT INTO `region_attribute` VALUES (1,1,1,700,1,700,'2014-07-25 13:43:49',NULL,1);
/*!40000 ALTER TABLE `region_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `region_string_value`
--

DROP TABLE IF EXISTS `region_string_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `region_string_value` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` bigint(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` bigint(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_region_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Локализация атрибутов региона';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `region_string_value`
--

LOCK TABLES `region_string_value` WRITE;
/*!40000 ALTER TABLE `region_string_value` DISABLE KEYS */;
INSERT INTO `region_string_value` VALUES (1,1,1,'ХАРЬКОВСКАЯ ОБЛАСТЬ');
/*!40000 ALTER TABLE `region_string_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registry`
--

DROP TABLE IF EXISTS `registry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `registry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `registry_number` bigint(20) DEFAULT NULL,
  `type` bigint(20) NOT NULL,
  `status` bigint(20) DEFAULT NULL COMMENT 'Record status reference',
  `records_count` int(11) DEFAULT NULL,
  `errors_count` int(11) NOT NULL DEFAULT '-1' COMMENT 'Cached errors number value, -1 is not init',
  `creation_date` datetime DEFAULT NULL,
  `from_date` datetime DEFAULT NULL,
  `till_date` datetime DEFAULT NULL,
  `load_date` datetime DEFAULT NULL,
  `sender_organization_id` bigint(20) DEFAULT NULL,
  `recipient_organization_id` bigint(20) DEFAULT NULL,
  `amount` decimal(19,2) DEFAULT NULL,
  `import_error_type` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_registry__registry_type` (`type`),
  KEY `fk_registry__registry_status` (`status`),
  KEY `fk_registry__sender_organization` (`sender_organization_id`),
  KEY `fk_registry__recipient_organization` (`recipient_organization_id`),
  KEY `fk_registry__import_error_type` (`import_error_type`),
  CONSTRAINT `fk_registry__import_error_type` FOREIGN KEY (`import_error_type`) REFERENCES `import_error_type` (`code`),
  CONSTRAINT `fk_registry__recipient_organization` FOREIGN KEY (`recipient_organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_registry__registry_status` FOREIGN KEY (`status`) REFERENCES `registry_status` (`code`),
  CONSTRAINT `fk_registry__registry_type` FOREIGN KEY (`type`) REFERENCES `registry_type` (`code`),
  CONSTRAINT `fk_registry__sender_organization` FOREIGN KEY (`sender_organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registry`
--

LOCK TABLES `registry` WRITE;
/*!40000 ALTER TABLE `registry` DISABLE KEYS */;
/*!40000 ALTER TABLE `registry` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registry_changing_spa_attribute`
--

DROP TABLE IF EXISTS `registry_changing_spa_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `registry_changing_spa_attribute` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '????????????? ?????????',
  `spa_old_attribute_pk_id` bigint(20) DEFAULT NULL COMMENT '????????????? ??????????? ???????? ??????????????? ???????? ?/? ??',
  `spa_new_attribute_pk_id` bigint(20) NOT NULL COMMENT '????????????? ?????? ???????? ??????????????? ???????? ?/? ??',
  `registry_record_container_id` bigint(20) NOT NULL COMMENT '????????????? ?????????? ?????? ??????? ????????? ?????????',
  PRIMARY KEY (`id`),
  KEY `fk_registry_changing_spa_attribute__spa_attribute_new` (`spa_new_attribute_pk_id`),
  KEY `fk_registry_changing_spa_attribute__spa_attribute_old` (`spa_old_attribute_pk_id`),
  KEY `fk_registry_changing_spa_attribute__registry_record_container` (`registry_record_container_id`),
  CONSTRAINT `fk_registry_changing_spa_attribute__registry_record_container` FOREIGN KEY (`registry_record_container_id`) REFERENCES `registry_record_container` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_registry_changing_spa_attribute__spa_attribute_new` FOREIGN KEY (`spa_new_attribute_pk_id`) REFERENCES `service_provider_account_attribute` (`pk_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_registry_changing_spa_attribute__spa_attribute_old` FOREIGN KEY (`spa_old_attribute_pk_id`) REFERENCES `service_provider_account_attribute` (`pk_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='????????? ?????????????? ????????? ?/? ?? ?? ?????? ???????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registry_changing_spa_attribute`
--

LOCK TABLES `registry_changing_spa_attribute` WRITE;
/*!40000 ALTER TABLE `registry_changing_spa_attribute` DISABLE KEYS */;
/*!40000 ALTER TABLE `registry_changing_spa_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registry_container`
--

DROP TABLE IF EXISTS `registry_container`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `registry_container` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data` varchar(2048) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Registry container data',
  `type` bigint(20) NOT NULL,
  `registry_id` bigint(20) NOT NULL COMMENT 'Registry reference',
  PRIMARY KEY (`id`),
  KEY `fk_registry_container__container_type` (`type`),
  KEY `fk_registry_container__registry` (`registry_id`),
  CONSTRAINT `fk_registry_container__container_type` FOREIGN KEY (`type`) REFERENCES `container_type` (`code`),
  CONSTRAINT `fk_registry_container__registry` FOREIGN KEY (`registry_id`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registry_container`
--

LOCK TABLES `registry_container` WRITE;
/*!40000 ALTER TABLE `registry_container` DISABLE KEYS */;
/*!40000 ALTER TABLE `registry_container` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registry_file`
--

DROP TABLE IF EXISTS `registry_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `registry_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `registry_id` bigint(20) NOT NULL,
  `name_on_server` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'File name on flexpay server',
  `original_name` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Original file name',
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'File description',
  `creation_date` datetime NOT NULL COMMENT 'File creation date',
  `user_id` bigint(20) NOT NULL COMMENT 'User ID who create this file',
  `size` bigint(20) DEFAULT NULL COMMENT 'File size',
  `registry_file_type` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_registry_id__registry_file_type` (`registry_id`,`registry_file_type`),
  KEY `fk_registry_file__registry_file_type` (`registry_file_type`),
  CONSTRAINT `fk_registry_file__registry` FOREIGN KEY (`registry_id`) REFERENCES `registry` (`id`),
  CONSTRAINT `fk_registry_file__registry_file_type` FOREIGN KEY (`registry_file_type`) REFERENCES `registry_file_type` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registry_file`
--

LOCK TABLES `registry_file` WRITE;
/*!40000 ALTER TABLE `registry_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `registry_file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registry_file_type`
--

DROP TABLE IF EXISTS `registry_file_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `registry_file_type` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` bigint(20) NOT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registry_file_type`
--

LOCK TABLES `registry_file_type` WRITE;
/*!40000 ALTER TABLE `registry_file_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `registry_file_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registry_record`
--

DROP TABLE IF EXISTS `registry_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `registry_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `service_code` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `personal_account_ext` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `city_type` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `city` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `street_type` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `street` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `building_number` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `bulk_number` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `apartment_number` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `room_number` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `first_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `middle_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `last_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `operation_date` datetime NOT NULL,
  `unique_operation_number` bigint(20) DEFAULT NULL,
  `amount` decimal(19,2) DEFAULT NULL,
  `city_type_id` bigint(20) DEFAULT NULL,
  `city_id` bigint(20) DEFAULT NULL,
  `street_type_id` bigint(20) DEFAULT NULL,
  `street_id` bigint(20) DEFAULT NULL,
  `building_id` bigint(20) DEFAULT NULL,
  `apartment_id` bigint(20) DEFAULT NULL,
  `room_id` bigint(20) DEFAULT NULL,
  `status` bigint(20) DEFAULT NULL COMMENT 'Record status reference',
  `import_error_type` bigint(20) DEFAULT NULL COMMENT 'Import error type from import error',
  `registry_id` bigint(20) NOT NULL COMMENT 'Registry reference',
  PRIMARY KEY (`id`),
  KEY `fk_registry_record__import_error_type` (`import_error_type`),
  KEY `idx_registry_record__status_error` (`status`,`import_error_type`,`registry_id`),
  KEY `idx_registry_record__status` (`status`,`registry_id`),
  KEY `fk_registry_record__registry` (`registry_id`),
  CONSTRAINT `fk_registry_record__import_error_type` FOREIGN KEY (`import_error_type`) REFERENCES `import_error_type` (`code`),
  CONSTRAINT `fk_registry_record__registry` FOREIGN KEY (`registry_id`) REFERENCES `registry` (`id`),
  CONSTRAINT `fk_registry_record__registry_record_status` FOREIGN KEY (`status`) REFERENCES `registry_record_status` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=1895986 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registry_record`
--

LOCK TABLES `registry_record` WRITE;
/*!40000 ALTER TABLE `registry_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `registry_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registry_record_container`
--

DROP TABLE IF EXISTS `registry_record_container`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `registry_record_container` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data` varchar(2048) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Container data',
  `type` bigint(20) NOT NULL,
  `record_id` bigint(20) NOT NULL COMMENT 'Registry record reference',
  PRIMARY KEY (`id`),
  KEY `fk_registry_record_container__container_type` (`type`),
  KEY `fk_registry_record_container__registry` (`record_id`),
  CONSTRAINT `fk_registry_record_container__container_type` FOREIGN KEY (`type`) REFERENCES `container_type` (`code`),
  CONSTRAINT `fk_registry_record_container__registry` FOREIGN KEY (`record_id`) REFERENCES `registry_record` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9479602 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registry_record_container`
--

LOCK TABLES `registry_record_container` WRITE;
/*!40000 ALTER TABLE `registry_record_container` DISABLE KEYS */;
/*!40000 ALTER TABLE `registry_record_container` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registry_record_status`
--

DROP TABLE IF EXISTS `registry_record_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `registry_record_status` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` bigint(20) NOT NULL COMMENT 'Registry record status code',
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registry_record_status`
--

LOCK TABLES `registry_record_status` WRITE;
/*!40000 ALTER TABLE `registry_record_status` DISABLE KEYS */;
INSERT INTO `registry_record_status` VALUES (1,1,'Неверный формат данных'),(2,2,'Загружена'),(3,3,'Ошибка связывания'),(4,4,'Связана'),(5,5,'Ошибка обработки'),(6,6,'Обработана');
/*!40000 ALTER TABLE `registry_record_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registry_status`
--

DROP TABLE IF EXISTS `registry_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `registry_status` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` bigint(20) NOT NULL COMMENT 'Registry status code',
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registry_status`
--

LOCK TABLES `registry_status` WRITE;
/*!40000 ALTER TABLE `registry_status` DISABLE KEYS */;
INSERT INTO `registry_status` VALUES (1,0,'Загружается'),(2,1,'Загружается с ошибкой'),(3,2,'Загружен'),(4,3,'Загрузка отменена'),(5,4,'Загружен с ошибками'),(6,5,'Обрабатывается'),(7,6,'Обрабатывается с ошибками'),(8,7,'Обработан'),(9,8,'Обработан с ошибками'),(10,9,'Обработка отменена'),(11,10,'Откатывается'),(12,11,'Откачен'),(13,19,'Связывается'),(14,20,'Связывается с ошибками'),(15,21,'Связан'),(16,22,'Ошибка связывания'),(17,23,'Связывание отменено'),(18,12,'???????? ?????????');
/*!40000 ALTER TABLE `registry_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registry_type`
--

DROP TABLE IF EXISTS `registry_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `registry_type` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` bigint(20) NOT NULL COMMENT 'Registry type code',
  `name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registry_type`
--

LOCK TABLES `registry_type` WRITE;
/*!40000 ALTER TABLE `registry_type` DISABLE KEYS */;
INSERT INTO `registry_type` VALUES (1,0,'Неизвестный'),(2,1,'Сальдо'),(3,2,'Начисление'),(4,3,'Извещение'),(5,4,'Счета на закрытие'),(6,5,'Информационный'),(7,6,'Корректировки'),(8,7,'Наличные оплаты'),(9,8,'Безналичные оплаты'),(10,9,'Возвраты платежей'),(11,10,'Ошибки'),(12,11,'Квитанции'),(13,12,'Оплаты банка');
/*!40000 ALTER TABLE `registry_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `room` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор сущности родительского объекта: 100 - building',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Начало действия значений параметров объекта',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  `permission_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'Ключ прав доступа к объекту',
  `external_id` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_room__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_room__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Комната';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room`
--

LOCK TABLES `room` WRITE;
/*!40000 ALTER TABLE `room` DISABLE KEYS */;
/*!40000 ALTER TABLE `room` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room_attribute`
--

DROP TABLE IF EXISTS `room_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `room_attribute` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` bigint(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 200 - НАИМЕНОВАНИЕ КОМНАТЫ',
  `value_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор значения',
  `value_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа значения атрибута: 200 - STRING_VALUE',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания действия атрибута',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_room_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`) REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_room_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`) REFERENCES `entity_attribute_value_type` (`id`),
  CONSTRAINT `fk_room_attribute__room` FOREIGN KEY (`object_id`) REFERENCES `room` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Атрибуты комнаты';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room_attribute`
--

LOCK TABLES `room_attribute` WRITE;
/*!40000 ALTER TABLE `room_attribute` DISABLE KEYS */;
/*!40000 ALTER TABLE `room_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room_correction`
--

DROP TABLE IF EXISTS `room_correction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `room_correction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `building_object_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор объекта дом',
  `apartment_object_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор объекта квартира',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта комната',
  `external_id` varchar(20) DEFAULT NULL COMMENT 'Внешний идентификатор объекта',
  `correction` varchar(100) NOT NULL COMMENT 'Номер комнаты',
  `begin_date` date NOT NULL DEFAULT '1970-01-01' COMMENT 'Дата начала актуальности соответствия',
  `end_date` date NOT NULL DEFAULT '2054-12-31' COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` bigint(20) NOT NULL COMMENT 'Идентификатор организации',
  `user_organization_id` bigint(20) DEFAULT NULL,
  `module_id` bigint(20) NOT NULL COMMENT 'Идентификатор модуля',
  `status` int(11) DEFAULT NULL COMMENT 'Статус',
  PRIMARY KEY (`id`),
  KEY `key_building_object_id` (`building_object_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_begin_date` (`begin_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  KEY `key_module_id` (`module_id`),
  KEY `key_status` (`status`),
  KEY `fk_room_correction__apartment` (`apartment_object_id`),
  CONSTRAINT `fk_room_correction__apartment` FOREIGN KEY (`apartment_object_id`) REFERENCES `apartment` (`object_id`),
  CONSTRAINT `fk_room_correction__building` FOREIGN KEY (`building_object_id`) REFERENCES `building` (`object_id`),
  CONSTRAINT `fk_room_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_room_correction__room` FOREIGN KEY (`object_id`) REFERENCES `room` (`object_id`),
  CONSTRAINT `fk_room_correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Коррекция комнаты';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room_correction`
--

LOCK TABLES `room_correction` WRITE;
/*!40000 ALTER TABLE `room_correction` DISABLE KEYS */;
/*!40000 ALTER TABLE `room_correction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room_string_value`
--

DROP TABLE IF EXISTS `room_string_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `room_string_value` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` bigint(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` bigint(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_room_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Локализация атрибутов комнаты';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room_string_value`
--

LOCK TABLES `room_string_value` WRITE;
/*!40000 ALTER TABLE `room_string_value` DISABLE KEYS */;
/*!40000 ALTER TABLE `room_string_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `saldo_out`
--

DROP TABLE IF EXISTS `saldo_out`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `saldo_out` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `service_provider_account_id` bigint(20) NOT NULL,
  `amount` decimal(19,2) NOT NULL,
  `date_formation` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `registry_record_container_id` bigint(20) DEFAULT NULL COMMENT '????????????? ?????????? ?????? ??????? ????????? ?????????',
  PRIMARY KEY (`id`),
  UNIQUE KEY `saldo_out_unique_sp_account__date_formation` (`service_provider_account_id`,`date_formation`),
  KEY `fk_saldo_out__registry_record_container` (`registry_record_container_id`),
  CONSTRAINT `fk_saldo_out__registry_record_container` FOREIGN KEY (`registry_record_container_id`) REFERENCES `registry_record_container` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_saldo_out__sp_account` FOREIGN KEY (`service_provider_account_id`) REFERENCES `service_provider_account` (`object_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Исходящее сальдо';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `saldo_out`
--

LOCK TABLES `saldo_out` WRITE;
/*!40000 ALTER TABLE `saldo_out` DISABLE KEYS */;
INSERT INTO `saldo_out` VALUES (1,140,21.01,'2008-02-29 18:00:00',NULL),(2,141,41.01,'2008-02-29 18:00:00',NULL),(3,142,61.01,'2008-02-29 18:00:00',NULL),(4,143,21.01,'2008-02-29 18:00:00',NULL),(5,144,41.01,'2008-02-29 18:00:00',NULL),(6,145,61.01,'2008-02-29 18:00:00',NULL);
/*!40000 ALTER TABLE `saldo_out` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sequence`
--

DROP TABLE IF EXISTS `sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence` (
  `sequence_name` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Название таблицы сущности',
  `sequence_value` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'Значение идентификатора',
  PRIMARY KEY (`sequence_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Последовательность генерации идентификаторов объектов';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sequence`
--

LOCK TABLES `sequence` WRITE;
/*!40000 ALTER TABLE `sequence` DISABLE KEYS */;
INSERT INTO `sequence` VALUES ('057',169),('apartment',37),('apartment_string_value',37),('building',2),('building_address',2),('building_address_string_value',2),('building_string_value',1),('city',2),('city_string_value',3),('city_type',2),('city_type_string_value',3),('country',2),('country_string_value',2),('district',10),('district_string_value',19),('eirc_account',191),('module_instance',3),('module_instance_string_value',7),('organization',83),('organization_string_value',262),('organization_type',5),('organization_type_string_value',5),('permission',1),('region',2),('region_string_value',2),('room',1),('room_string_value',1),('service_provider_account',200),('service_provider_account_string_value',145),('street',829),('street_string_value',829),('street_type',18),('street_type_string_value',35),('string_value',6007),('user_info',5),('user_info_string_value',1);
/*!40000 ALTER TABLE `sequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service`
--

DROP TABLE IF EXISTS `service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_parent_service` (`parent_id`),
  CONSTRAINT `fk_parent_service` FOREIGN KEY (`parent_id`) REFERENCES `service` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service`
--

LOCK TABLES `service` WRITE;
/*!40000 ALTER TABLE `service` DISABLE KEYS */;
INSERT INTO `service` VALUES (3,'19',NULL),(4,'1',NULL);
/*!40000 ALTER TABLE `service` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_correction`
--

DROP TABLE IF EXISTS `service_correction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_correction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта услуга',
  `external_id` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Внешний идентификатор услуги',
  `correction` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Код организации',
  `begin_date` date NOT NULL DEFAULT '1970-01-01' COMMENT 'Дата начала актуальности соответствия',
  `end_date` date NOT NULL DEFAULT '2054-12-31' COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` bigint(20) NOT NULL COMMENT 'Идентификатор организации',
  `user_organization_id` bigint(20) DEFAULT NULL,
  `module_id` bigint(20) NOT NULL COMMENT 'Идентификатор модуля',
  `status` int(11) DEFAULT NULL COMMENT 'Статус',
  PRIMARY KEY (`id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_begin_date` (`begin_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  KEY `key_module_id` (`module_id`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_service__correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_service__correction__service_object` FOREIGN KEY (`object_id`) REFERENCES `service` (`id`),
  CONSTRAINT `fk_service__correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Коррекция услуги';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_correction`
--

LOCK TABLES `service_correction` WRITE;
/*!40000 ALTER TABLE `service_correction` DISABLE KEYS */;
/*!40000 ALTER TABLE `service_correction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_provider_account`
--

DROP TABLE IF EXISTS `service_provider_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_provider_account` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `object_id` bigint(20) NOT NULL,
  `eirc_account_id` bigint(20) NOT NULL,
  `organization_id` bigint(20) NOT NULL,
  `account_number` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `service_id` bigint(20) NOT NULL,
  `first_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `last_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `middle_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `begin_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`begin_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_begin_date` (`begin_date`),
  KEY `key_end_date` (`end_date`),
  KEY `fk_service_provider_account__eirc_account` (`eirc_account_id`),
  KEY `fk_service_provider_account__organization` (`organization_id`),
  KEY `fk_service_provider_account__service` (`service_id`),
  CONSTRAINT `fk_service_provider_account__eirc_account` FOREIGN KEY (`eirc_account_id`) REFERENCES `eirc_account` (`object_id`),
  CONSTRAINT `fk_service_provider_account__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_service_provider_account__service` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_provider_account`
--

LOCK TABLES `service_provider_account` WRITE;
/*!40000 ALTER TABLE `service_provider_account` DISABLE KEYS */;
INSERT INTO `service_provider_account` VALUES (1,1,1,1,'TestSPAAccount1',3,NULL,NULL,NULL,'2014-07-28 02:32:04','2014-08-19 07:24:30'),(2,2,2,1,'TestSPAAccount1',3,NULL,NULL,NULL,'2014-07-28 03:02:51','2014-08-19 07:24:30'),(3,3,3,1,'TestSPAAccount1',3,NULL,NULL,NULL,'2014-07-28 03:05:34','2014-08-19 07:24:23'),(4,4,4,1,'TestSPAAccount1',3,NULL,NULL,NULL,'2014-07-28 03:17:52','2014-08-19 07:24:16'),(5,5,5,1,'TestSPAAccount1',3,NULL,NULL,NULL,'2014-07-28 03:19:09','2014-08-19 07:24:23'),(6,6,6,1,'TestSPAAccount1',3,NULL,NULL,NULL,'2014-07-28 03:46:51','2014-08-19 07:24:30'),(7,7,7,1,'TestSPAAccount1',3,NULL,NULL,NULL,'2014-07-28 03:49:55','2014-08-19 07:24:30'),(8,8,8,1,'TestSPAAccount1',3,NULL,NULL,NULL,'2014-07-28 04:11:31','2014-08-19 07:24:23'),(9,9,9,1,'TestSPAAccount1',3,NULL,NULL,NULL,'2014-07-28 04:53:12','2014-08-19 07:24:23'),(10,10,10,1,'TestSPAAccount1',3,NULL,NULL,NULL,'2014-07-29 12:53:35','2014-08-19 07:24:30'),(11,11,11,1,'TestSPAAccount1',3,NULL,NULL,NULL,'2014-07-29 12:55:01','2014-08-19 07:24:30'),(12,12,12,1,'TestSPAAccount1',3,NULL,NULL,NULL,'2014-07-30 02:40:54','2014-08-19 07:24:23'),(13,13,13,1,'TestSPAAccount1',3,NULL,NULL,NULL,'2014-07-30 03:54:25','2014-08-19 07:24:24'),(14,14,14,1,'TestSPAAccount1',3,NULL,NULL,NULL,'2014-07-30 11:59:02','2014-08-19 07:24:30'),(15,15,15,1,'TestSPAAccount1',3,NULL,NULL,NULL,'2014-07-30 11:59:58','2014-08-19 07:24:30'),(16,16,16,1,'TestSPAAccount1',3,NULL,NULL,NULL,'2014-08-13 04:07:33','2014-08-19 07:24:23'),(17,17,17,1,'TestSPAAccount1',3,NULL,NULL,NULL,'2014-08-13 04:08:35','2014-08-19 07:24:23'),(18,18,18,1,'TestSPAAccount1',3,NULL,NULL,NULL,'2014-08-13 04:08:58','2014-08-19 07:24:30'),(19,19,19,1,'TestSPAAccount1',3,NULL,NULL,NULL,'2014-08-13 04:29:01','2014-08-19 07:24:16'),(20,20,20,1,'TestSPAAccount1',3,NULL,NULL,NULL,'2014-08-13 04:35:20','2014-08-19 07:24:30'),(21,21,21,1,'TestSPAAccount1',3,NULL,NULL,NULL,'2014-08-13 06:14:33','2014-08-19 07:24:23'),(22,22,22,1,'TestSPAAccount1',3,NULL,NULL,NULL,'2014-08-13 06:19:09','2014-08-19 07:24:24'),(23,23,23,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 08:30:03'),(24,24,24,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 08:30:03'),(25,25,25,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 08:30:03'),(26,26,26,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 09:05:29'),(27,27,27,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 09:05:29'),(28,28,28,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 09:05:29'),(29,29,29,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 09:08:31'),(30,30,30,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 09:08:31'),(31,31,31,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 09:08:31'),(32,32,32,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 09:13:40'),(33,33,33,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 09:13:40'),(34,34,34,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 09:13:39'),(35,35,35,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 09:16:14'),(36,36,36,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 09:16:14'),(37,37,37,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 09:16:14'),(38,38,38,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 09:17:48'),(39,39,39,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 09:17:48'),(40,40,40,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 09:17:47'),(41,41,41,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 09:20:32'),(42,42,42,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 09:20:32'),(43,43,43,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 09:20:32'),(44,44,44,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 09:28:34'),(45,45,45,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 09:28:34'),(46,46,46,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 09:28:34'),(47,47,47,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 09:36:53'),(48,48,48,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 09:36:53'),(49,49,49,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 09:36:53'),(50,50,50,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 09:37:19'),(51,51,51,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 09:37:19'),(52,52,52,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 09:37:19'),(53,53,53,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 09:40:41'),(54,54,54,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 09:40:41'),(55,55,55,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 09:40:41'),(56,56,53,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 09:40:41'),(57,57,54,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 09:40:41'),(58,58,55,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 09:40:41'),(59,59,56,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 09:42:16'),(60,60,57,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 09:42:16'),(61,61,58,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 09:42:16'),(62,62,59,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 09:50:17'),(63,63,60,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 09:50:17'),(64,64,61,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 09:50:17'),(65,65,62,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 09:55:04'),(66,66,63,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 09:55:04'),(67,67,64,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 09:55:04'),(68,68,65,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 10:00:10'),(69,69,66,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 10:00:10'),(70,70,67,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 10:00:10'),(71,71,68,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 10:09:38'),(72,72,69,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 10:09:38'),(73,73,70,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 10:09:38'),(74,74,71,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 10:15:51'),(75,75,72,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 10:15:51'),(76,76,73,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 10:15:51'),(77,77,74,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 10:18:06'),(78,78,75,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 10:18:06'),(79,79,76,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 10:18:06'),(80,80,77,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 11:07:58'),(81,81,78,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 11:07:58'),(82,82,79,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 11:07:58'),(83,83,77,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 11:07:58'),(84,84,78,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 11:07:58'),(85,85,79,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 11:07:58'),(86,86,80,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 11:08:51'),(87,87,81,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 11:08:50'),(88,88,82,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 11:08:50'),(89,89,83,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 11:13:35'),(90,90,84,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 11:13:35'),(91,91,85,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 11:13:34'),(92,92,86,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 11:14:01'),(93,93,87,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 11:14:01'),(94,94,88,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 11:14:01'),(95,95,89,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 11:18:05'),(96,96,90,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 11:18:04'),(97,97,91,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 11:18:04'),(98,98,92,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 11:18:37'),(99,99,93,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 11:18:37'),(100,100,94,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 11:18:37'),(101,101,95,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 11:19:12'),(102,102,96,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 11:19:12'),(103,103,97,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 11:19:12'),(104,104,98,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 11:19:34'),(105,105,99,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 11:19:34'),(106,106,100,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 11:19:34'),(107,107,101,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 11:24:47'),(108,108,102,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 11:24:47'),(109,109,103,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 11:24:47'),(110,110,104,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 11:26:48'),(111,111,105,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 11:26:48'),(112,112,106,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 11:26:48'),(113,113,107,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 11:28:29'),(114,114,108,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 11:28:29'),(115,115,109,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 11:28:29'),(116,116,110,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 11:29:06'),(117,117,111,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 11:29:06'),(118,118,112,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 11:29:06'),(119,119,113,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 11:29:40'),(120,120,114,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 11:29:40'),(121,121,115,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 11:29:39'),(122,122,116,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 11:30:08'),(123,123,117,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 11:30:08'),(124,124,118,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 11:30:08'),(125,125,119,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 11:30:48'),(126,126,120,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 11:30:48'),(127,127,121,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 11:30:47'),(128,128,122,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 11:47:34'),(129,129,123,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 11:47:34'),(130,130,124,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 11:47:34'),(131,131,125,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 13:56:05'),(132,132,126,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 13:56:05'),(133,133,127,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 13:56:04'),(134,134,128,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-19 14:01:40'),(135,135,129,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-19 14:01:40'),(136,136,130,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-19 14:01:40'),(137,137,131,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-21 05:57:44'),(138,138,132,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-21 05:57:44'),(139,139,133,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-21 05:57:44'),(140,140,134,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-21 12:17:24'),(141,141,135,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-21 12:17:24'),(142,142,136,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-21 12:17:24'),(143,143,137,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-21 12:19:03'),(144,144,138,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-21 12:19:03'),(145,145,139,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-21 12:19:03'),(146,146,140,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-21 12:23:05'),(147,147,141,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-21 12:23:05'),(148,148,142,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-21 12:23:05'),(149,149,143,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-25 09:03:44'),(150,150,144,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-25 09:03:44'),(151,151,145,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-25 09:03:44'),(152,152,146,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-25 09:08:43'),(153,153,147,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-25 09:08:43'),(154,154,148,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-25 09:08:43'),(155,155,149,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-25 09:23:13'),(156,156,150,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-25 09:23:13'),(157,157,151,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-25 09:23:13'),(158,158,149,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-25 09:23:13'),(159,159,150,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-25 09:23:13'),(160,160,151,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-25 09:23:13'),(161,161,152,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-25 09:26:45'),(162,162,153,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-25 09:26:45'),(163,163,154,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-25 09:26:45'),(164,164,155,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-25 09:31:41'),(165,165,156,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-25 09:31:41'),(166,166,157,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-25 09:31:40'),(167,167,158,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-25 09:48:28'),(168,168,159,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-25 09:48:28'),(169,169,160,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-25 09:48:28'),(170,170,161,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-25 10:19:18'),(171,171,162,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-25 10:19:18'),(172,172,163,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-25 10:19:18'),(173,173,164,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-25 10:19:40'),(174,174,165,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-25 10:19:40'),(175,175,166,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-25 10:19:40'),(176,176,167,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-25 10:23:24'),(177,177,168,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-25 10:23:24'),(178,178,169,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-25 10:23:24'),(179,179,170,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-25 10:25:43'),(180,180,171,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-25 10:25:43'),(181,181,172,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-25 10:25:43'),(182,182,173,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-25 10:37:40'),(183,183,174,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-25 10:37:40'),(184,184,175,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-25 10:37:40'),(185,185,176,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-25 10:38:02'),(186,186,177,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-25 10:38:02'),(187,187,178,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-25 10:38:02'),(188,188,179,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-25 10:40:08'),(189,189,180,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-25 10:40:08'),(190,190,181,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-25 10:40:08'),(191,191,182,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-25 10:43:20'),(192,192,183,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-25 10:43:20'),(193,193,184,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-25 10:43:20'),(194,194,185,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-25 10:51:42'),(195,195,186,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-25 10:51:41'),(196,196,187,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-25 10:51:41'),(197,197,188,7,'1000002239',4,'О','КРАВЧЕНКО','Ф','2007-02-28 18:00:00','2014-08-25 10:52:06'),(198,198,189,7,'1000002258',4,'С','МИХАЙЛИЧЕНКО','В','2007-02-28 18:00:00','2014-08-25 10:52:06'),(199,199,190,7,'1000002269',4,'Т','СЛЮСАРЕНКО','Н','2007-02-28 18:00:00','2014-08-25 10:52:06');
/*!40000 ALTER TABLE `service_provider_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_provider_account_attribute`
--

DROP TABLE IF EXISTS `service_provider_account_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_provider_account_attribute` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` bigint(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа атрибута',
  `value_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор значения',
  `value_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа значения: 100 - STRING_VALUE',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_sp_account_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`) REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_sp_account_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`) REFERENCES `entity_attribute_value_type` (`id`),
  CONSTRAINT `fk_sp_account_attribute__sp_account` FOREIGN KEY (`object_id`) REFERENCES `service_provider_account` (`object_id`)
) ENGINE=InnoDB AUTO_INCREMENT=144 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Дополнительные атрибуты л/с ПУ (кол-во проживающих и т.п.)';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_provider_account_attribute`
--

LOCK TABLES `service_provider_account_attribute` WRITE;
/*!40000 ALTER TABLE `service_provider_account_attribute` DISABLE KEYS */;
INSERT INTO `service_provider_account_attribute` VALUES (96,1,113,6002,91,6002,'2007-02-28 18:00:00','2014-02-28 17:00:00',2),(97,1,114,6003,92,6003,'2007-02-28 18:00:00','2014-02-28 17:00:00',2),(98,1,115,6004,93,6004,'2007-02-28 18:00:00','2014-02-28 17:00:00',2),(99,1,113,6002,94,6002,'2014-02-28 17:00:00',NULL,1),(100,1,114,6003,95,6003,'2014-02-28 17:00:00',NULL,1),(101,1,115,6004,96,6004,'2014-02-28 17:00:00',NULL,1),(102,1,116,6002,97,6002,'2007-02-28 18:00:00','2014-02-28 17:00:00',2),(103,1,117,6003,98,6003,'2007-02-28 18:00:00','2014-02-28 17:00:00',2),(104,1,118,6004,99,6004,'2007-02-28 18:00:00','2014-02-28 17:00:00',2),(105,1,116,6002,100,6002,'2014-02-28 17:00:00',NULL,1),(106,1,117,6003,101,6003,'2014-02-28 17:00:00',NULL,1),(107,1,118,6004,102,6004,'2014-02-28 17:00:00',NULL,1),(108,1,119,6002,103,6002,'2007-02-28 18:00:00','2014-02-28 17:00:00',2),(109,1,120,6003,104,6003,'2007-02-28 18:00:00','2014-02-28 17:00:00',2),(110,1,121,6004,105,6004,'2007-02-28 18:00:00','2014-02-28 17:00:00',2),(111,1,119,6002,106,6002,'2014-02-28 17:00:00',NULL,1),(112,1,120,6003,107,6003,'2014-02-28 17:00:00',NULL,1),(113,1,121,6004,108,6004,'2014-02-28 17:00:00',NULL,1),(114,1,122,6002,109,6002,'2007-02-28 18:00:00','2014-02-28 17:00:00',2),(115,1,123,6003,110,6003,'2007-02-28 18:00:00','2014-02-28 17:00:00',2),(116,1,124,6004,111,6004,'2007-02-28 18:00:00','2014-02-28 17:00:00',2),(117,1,122,6002,112,6002,'2014-02-28 17:00:00',NULL,1),(118,1,123,6003,113,6003,'2014-02-28 17:00:00',NULL,1),(119,1,124,6004,114,6004,'2014-02-28 17:00:00',NULL,1),(120,1,125,6002,115,6002,'2007-02-28 18:00:00','2014-02-28 17:00:00',2),(121,1,126,6003,116,6003,'2007-02-28 18:00:00','2014-02-28 17:00:00',2),(122,1,127,6004,117,6004,'2007-02-28 18:00:00','2014-02-28 17:00:00',2),(123,1,125,6002,118,6002,'2014-02-28 17:00:00',NULL,1),(124,1,126,6003,119,6003,'2014-02-28 17:00:00',NULL,1),(125,1,127,6004,120,6004,'2014-02-28 17:00:00',NULL,1),(126,1,128,6002,121,6002,'2007-02-28 18:00:00','2014-02-28 17:00:00',2),(127,1,129,6003,122,6003,'2007-02-28 18:00:00','2014-02-28 17:00:00',2),(128,1,130,6004,123,6004,'2007-02-28 18:00:00','2014-02-28 17:00:00',2),(129,1,128,6002,124,6002,'2014-02-28 17:00:00',NULL,1),(130,1,129,6003,125,6003,'2014-02-28 17:00:00',NULL,1),(131,1,130,6004,126,6004,'2014-02-28 17:00:00',NULL,1),(132,1,131,6002,127,6002,'2007-02-28 18:00:00',NULL,1),(133,1,132,6003,128,6003,'2007-02-28 18:00:00',NULL,1),(134,1,133,6004,129,6004,'2007-02-28 18:00:00',NULL,1),(138,1,134,6002,133,6002,'2007-02-28 18:00:00',NULL,1),(139,1,135,6003,134,6003,'2007-02-28 18:00:00',NULL,1),(140,1,136,6004,135,6004,'2007-02-28 18:00:00',NULL,1),(141,1,137,6002,139,6002,'2007-02-28 18:00:00',NULL,1),(142,1,138,6003,140,6003,'2007-02-28 18:00:00',NULL,1),(143,1,139,6004,141,6004,'2007-02-28 18:00:00',NULL,1);
/*!40000 ALTER TABLE `service_provider_account_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_provider_account_correction`
--

DROP TABLE IF EXISTS `service_provider_account_correction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_provider_account_correction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта л/с ПУ',
  `external_id` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Внешний идентификатор л/с ПУ',
  `correction` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Код организации',
  `begin_date` date NOT NULL DEFAULT '1970-01-01' COMMENT 'Дата начала актуальности соответствия',
  `end_date` date NOT NULL DEFAULT '2054-12-31' COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` bigint(20) NOT NULL COMMENT 'Идентификатор организации',
  `user_organization_id` bigint(20) DEFAULT NULL,
  `module_id` bigint(20) NOT NULL COMMENT 'Идентификатор модуля',
  `status` int(11) DEFAULT NULL COMMENT 'Статус',
  PRIMARY KEY (`id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_begin_date` (`begin_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  KEY `key_module_id` (`module_id`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_sp_account__correction__sp_account_object` FOREIGN KEY (`object_id`) REFERENCES `service_provider_account` (`object_id`),
  CONSTRAINT `fk_sp_account__correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_sp_account__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Коррекция л/с ПУ';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_provider_account_correction`
--

LOCK TABLES `service_provider_account_correction` WRITE;
/*!40000 ALTER TABLE `service_provider_account_correction` DISABLE KEYS */;
/*!40000 ALTER TABLE `service_provider_account_correction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_provider_account_string_value`
--

DROP TABLE IF EXISTS `service_provider_account_string_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_provider_account_string_value` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` bigint(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` bigint(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_sp_account_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=152 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Локализация атрибутов л/с ПУ';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_provider_account_string_value`
--

LOCK TABLES `service_provider_account_string_value` WRITE;
/*!40000 ALTER TABLE `service_provider_account_string_value` DISABLE KEYS */;
INSERT INTO `service_provider_account_string_value` VALUES (98,91,1,'63.1'),(99,92,1,'51.2'),(100,93,1,'20.0'),(101,94,1,'65.3'),(102,95,1,'50.2'),(103,96,1,'40.1'),(104,97,1,'63.1'),(105,98,1,'51.2'),(106,99,1,'20.0'),(107,100,1,'65.3'),(108,101,1,'50.2'),(109,102,1,'40.1'),(110,103,1,'63.1'),(111,104,1,'51.2'),(112,105,1,'20.0'),(113,106,1,'65.3'),(114,107,1,'50.2'),(115,108,1,'40.1'),(116,109,1,'63.1'),(117,110,1,'51.2'),(118,111,1,'20.0'),(119,112,1,'65.3'),(120,113,1,'50.2'),(121,114,1,'40.1'),(122,115,1,'63.1'),(123,116,1,'51.2'),(124,117,1,'20.0'),(125,118,1,'65.3'),(126,119,1,'50.2'),(127,120,1,'40.1'),(128,121,1,'63.1'),(129,122,1,'51.2'),(130,123,1,'20.0'),(131,124,1,'65.3'),(132,125,1,'50.2'),(133,126,1,'40.1'),(134,127,1,'63.1'),(135,128,1,'51.2'),(136,129,1,'20.0'),(137,130,1,'65.3'),(138,131,1,'50.2'),(139,132,1,'40.1'),(140,133,1,'63.1'),(141,134,1,'51.2'),(142,135,1,'20.0'),(143,136,1,'65.3'),(144,137,1,'50.2'),(145,138,1,'40.1'),(146,139,1,'63.1'),(147,140,1,'51.2'),(148,141,1,'20.0'),(149,142,1,'65.3'),(150,143,1,'50.2'),(151,144,1,'40.1');
/*!40000 ALTER TABLE `service_provider_account_string_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_string_value`
--

DROP TABLE IF EXISTS `service_string_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_string_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `service_id` bigint(20) NOT NULL,
  `locale_id` bigint(20) NOT NULL,
  `value` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id__locale` (`service_id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(255)),
  CONSTRAINT `fk_service_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`),
  CONSTRAINT `fk_service_string_value__service` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_string_value`
--

LOCK TABLES `service_string_value` WRITE;
/*!40000 ALTER TABLE `service_string_value` DISABLE KEYS */;
INSERT INTO `service_string_value` VALUES (1,3,1,'TestService1'),(2,4,1,'Горячая вода');
/*!40000 ALTER TABLE `service_string_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `street`
--

DROP TABLE IF EXISTS `street`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `street` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор родительского объекта',
  `parent_entity_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор сущности родительского объекта: 400 - city',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  `permission_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'Ключ прав доступа к объекту',
  `external_id` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_street__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_street__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=829 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Улица';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `street`
--

LOCK TABLES `street` WRITE;
/*!40000 ALTER TABLE `street` DISABLE KEYS */;
INSERT INTO `street` VALUES (1,1,1,400,'2014-07-25 13:43:52',NULL,1,0,'43337566'),(2,2,1,400,'2014-07-25 13:43:52',NULL,1,0,'5995116'),(3,3,1,400,'2014-07-25 13:43:52',NULL,1,0,'5995134'),(4,4,1,400,'2014-07-25 13:43:52',NULL,1,0,'5995142'),(5,5,1,400,'2014-07-25 13:43:52',NULL,1,0,'5995138'),(6,6,1,400,'2014-07-25 13:43:52',NULL,1,0,'27789866'),(7,7,1,400,'2014-07-25 13:43:52',NULL,1,0,'27789868'),(8,8,1,400,'2014-07-25 13:43:52',NULL,1,0,'27789864'),(9,9,1,400,'2014-07-25 13:43:52',NULL,1,0,'42565036'),(10,10,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555759'),(11,11,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197353'),(12,12,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197361'),(13,13,1,400,'2014-07-25 13:43:52',NULL,1,0,'5995144'),(14,14,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197395'),(15,15,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197367'),(16,16,1,400,'2014-07-25 13:43:52',NULL,1,0,'88474639'),(17,17,1,400,'2014-07-25 13:43:52',NULL,1,0,'27712761'),(18,18,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699616'),(19,19,1,400,'2014-07-25 13:43:52',NULL,1,0,'27867549'),(20,20,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326596'),(21,21,1,400,'2014-07-25 13:43:52',NULL,1,0,'20573492'),(22,22,1,400,'2014-07-25 13:43:52',NULL,1,0,'20573496'),(23,23,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658840'),(24,24,1,400,'2014-07-25 13:43:52',NULL,1,0,'43334215'),(25,25,1,400,'2014-07-25 13:43:52',NULL,1,0,'42564980'),(26,26,1,400,'2014-07-25 13:43:52',NULL,1,0,'27712755'),(27,27,1,400,'2014-07-25 13:43:52',NULL,1,0,'27712747'),(28,28,1,400,'2014-07-25 13:43:52',NULL,1,0,'27789856'),(29,29,1,400,'2014-07-25 13:43:52',NULL,1,0,'103502405'),(30,30,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326576'),(31,31,1,400,'2014-07-25 13:43:52',NULL,1,0,'27727168'),(32,32,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555673'),(33,33,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942842'),(34,34,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693313'),(35,35,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693301'),(36,36,1,400,'2014-07-25 13:43:52',NULL,1,0,'42564996'),(37,37,1,400,'2014-07-25 13:43:52',NULL,1,0,'43339113'),(38,38,1,400,'2014-07-25 13:43:52',NULL,1,0,'20538864'),(39,39,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197397'),(40,40,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881454'),(41,41,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326660'),(42,42,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658834'),(43,43,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658836'),(44,44,1,400,'2014-07-25 13:43:52',NULL,1,0,'29096838'),(45,45,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547293'),(46,46,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560663'),(47,47,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560665'),(48,48,1,400,'2014-07-25 13:43:52',NULL,1,0,'89918296'),(49,49,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560683'),(50,50,1,400,'2014-07-25 13:43:52',NULL,1,0,'43337568'),(51,51,1,400,'2014-07-25 13:43:52',NULL,1,0,'43331833'),(52,52,1,400,'2014-07-25 13:43:52',NULL,1,0,'43331843'),(53,53,1,400,'2014-07-25 13:43:52',NULL,1,0,'20573498'),(54,54,1,400,'2014-07-25 13:43:52',NULL,1,0,'88266157'),(55,55,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881412'),(56,56,1,400,'2014-07-25 13:43:52',NULL,1,0,'50700138'),(57,57,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881476'),(58,58,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814831'),(59,59,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658898'),(60,60,1,400,'2014-07-25 13:43:52',NULL,1,0,'203053851'),(61,61,1,400,'2014-07-25 13:43:52',NULL,1,0,'51117858'),(62,62,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326642'),(63,63,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326598'),(64,64,1,400,'2014-07-25 13:43:52',NULL,1,0,'5995132'),(65,65,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699618'),(66,66,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699608'),(67,67,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942864'),(68,68,1,400,'2014-07-25 13:43:52',NULL,1,0,'27867541'),(69,69,1,400,'2014-07-25 13:43:52',NULL,1,0,'27867543'),(70,70,1,400,'2014-07-25 13:43:52',NULL,1,0,'89524023'),(71,71,1,400,'2014-07-25 13:43:52',NULL,1,0,'6118887'),(72,72,1,400,'2014-07-25 13:43:52',NULL,1,0,'5995146'),(73,73,1,400,'2014-07-25 13:43:52',NULL,1,0,'27700195'),(74,74,1,400,'2014-07-25 13:43:52',NULL,1,0,'88474649'),(75,75,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326600'),(76,76,1,400,'2014-07-25 13:43:52',NULL,1,0,'42553084'),(77,77,1,400,'2014-07-25 13:43:52',NULL,1,0,'42553086'),(78,78,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547271'),(79,79,1,400,'2014-07-25 13:43:52',NULL,1,0,'3974996'),(80,80,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699626'),(81,81,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547305'),(82,82,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658788'),(83,83,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881378'),(84,84,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555691'),(85,85,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326602'),(86,86,1,400,'2014-07-25 13:43:52',NULL,1,0,'88691393'),(87,87,1,400,'2014-07-25 13:43:52',NULL,1,0,'5995148'),(88,88,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942886'),(89,89,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326604'),(90,90,1,400,'2014-07-25 13:43:52',NULL,1,0,'20439427'),(91,91,1,400,'2014-07-25 13:43:52',NULL,1,0,'20439425'),(92,92,1,400,'2014-07-25 13:43:52',NULL,1,0,'42565014'),(93,93,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326552'),(94,94,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699662'),(95,95,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326606'),(96,96,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555709'),(97,97,1,400,'2014-07-25 13:43:52',NULL,1,0,'27867555'),(98,98,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814809'),(99,99,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814805'),(100,100,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881440'),(101,101,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881422'),(102,102,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693311'),(103,103,1,400,'2014-07-25 13:43:52',NULL,1,0,'203057424'),(104,104,1,400,'2014-07-25 13:43:52',NULL,1,0,'97264048'),(105,105,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658880'),(106,106,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658882'),(107,107,1,400,'2014-07-25 13:43:52',NULL,1,0,'377022748'),(108,108,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555749'),(109,109,1,400,'2014-07-25 13:43:52',NULL,1,0,'42565034'),(110,110,1,400,'2014-07-25 13:43:52',NULL,1,0,'27700203'),(111,111,1,400,'2014-07-25 13:43:52',NULL,1,0,'88266141'),(112,112,1,400,'2014-07-25 13:43:52',NULL,1,0,'20783800'),(113,113,1,400,'2014-07-25 13:43:52',NULL,1,0,'27838183'),(114,114,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693349'),(115,115,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547307'),(116,116,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547309'),(117,117,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547311'),(118,118,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560703'),(119,119,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658868'),(120,120,1,400,'2014-07-25 13:43:52',NULL,1,0,'203057426'),(121,121,1,400,'2014-07-25 13:43:52',NULL,1,0,'27867545'),(122,122,1,400,'2014-07-25 13:43:52',NULL,1,0,'45086775'),(123,123,1,400,'2014-07-25 13:43:52',NULL,1,0,'45086805'),(124,124,1,400,'2014-07-25 13:43:52',NULL,1,0,'27700207'),(125,125,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881382'),(126,126,1,400,'2014-07-25 13:43:52',NULL,1,0,'3906098'),(127,127,1,400,'2014-07-25 13:43:52',NULL,1,0,'5995102'),(128,128,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942918'),(129,129,1,400,'2014-07-25 13:43:52',NULL,1,0,'20471566'),(130,130,1,400,'2014-07-25 13:43:52',NULL,1,0,'43336559'),(131,131,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942920'),(132,132,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814859'),(133,133,1,400,'2014-07-25 13:43:52',NULL,1,0,'27727156'),(134,134,1,400,'2014-07-25 13:43:52',NULL,1,0,'27712757'),(135,135,1,400,'2014-07-25 13:43:52',NULL,1,0,'6465709'),(136,136,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197355'),(137,137,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693317'),(138,138,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197357'),(139,139,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547241'),(140,140,1,400,'2014-07-25 13:43:52',NULL,1,0,'6338838'),(141,141,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814817'),(142,142,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814823'),(143,143,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699622'),(144,144,1,400,'2014-07-25 13:43:52',NULL,1,0,'20538854'),(145,145,1,400,'2014-07-25 13:43:52',NULL,1,0,'6118891'),(146,146,1,400,'2014-07-25 13:43:52',NULL,1,0,'42565026'),(147,147,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881442'),(148,148,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693307'),(149,149,1,400,'2014-07-25 13:43:52',NULL,1,0,'45086793'),(150,150,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814815'),(151,151,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658820'),(152,152,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658822'),(153,153,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699624'),(154,154,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555761'),(155,155,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326562'),(156,156,1,400,'2014-07-25 13:43:52',NULL,1,0,'45086795'),(157,157,1,400,'2014-07-25 13:43:52',NULL,1,0,'88266143'),(158,158,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555763'),(159,159,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814803'),(160,160,1,400,'2014-07-25 13:43:52',NULL,1,0,'42565028'),(161,161,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547243'),(162,162,1,400,'2014-07-25 13:43:52',NULL,1,0,'3975006'),(163,163,1,400,'2014-07-25 13:43:52',NULL,1,0,'3846210'),(164,164,1,400,'2014-07-25 13:43:52',NULL,1,0,'27789858'),(165,165,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881444'),(166,166,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693341'),(167,167,1,400,'2014-07-25 13:43:52',NULL,1,0,'20696436'),(168,168,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326608'),(169,169,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693325'),(170,170,1,400,'2014-07-25 13:43:52',NULL,1,0,'97264050'),(171,171,1,400,'2014-07-25 13:43:52',NULL,1,0,'42565030'),(172,172,1,400,'2014-07-25 13:43:52',NULL,1,0,'43331829'),(173,173,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326588'),(174,174,1,400,'2014-07-25 13:43:52',NULL,1,0,'3795911'),(175,175,1,400,'2014-07-25 13:43:52',NULL,1,0,'45086799'),(176,176,1,400,'2014-07-25 13:43:52',NULL,1,0,'27867577'),(177,177,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326610'),(178,178,1,400,'2014-07-25 13:43:52',NULL,1,0,'43331835'),(179,179,1,400,'2014-07-25 13:43:52',NULL,1,0,'20503518'),(180,180,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555765'),(181,181,1,400,'2014-07-25 13:43:52',NULL,1,0,'42565024'),(182,182,1,400,'2014-07-25 13:43:52',NULL,1,0,'42565032'),(183,183,1,400,'2014-07-25 13:43:52',NULL,1,0,'20783808'),(184,184,1,400,'2014-07-25 13:43:52',NULL,1,0,'43336561'),(185,185,1,400,'2014-07-25 13:43:52',NULL,1,0,'27838187'),(186,186,1,400,'2014-07-25 13:43:52',NULL,1,0,'42543932'),(187,187,1,400,'2014-07-25 13:43:52',NULL,1,0,'20439431'),(188,188,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658852'),(189,189,1,400,'2014-07-25 13:43:52',NULL,1,0,'42550661'),(190,190,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547245'),(191,191,1,400,'2014-07-25 13:43:52',NULL,1,0,'377023064'),(192,192,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881414'),(193,193,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658794'),(194,194,1,400,'2014-07-25 13:43:52',NULL,1,0,'27867561'),(195,195,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658874'),(196,196,1,400,'2014-07-25 13:43:52',NULL,1,0,'45086797'),(197,197,1,400,'2014-07-25 13:43:52',NULL,1,0,'5995104'),(198,198,1,400,'2014-07-25 13:43:52',NULL,1,0,'203054102'),(199,199,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326650'),(200,200,1,400,'2014-07-25 13:43:52',NULL,1,0,'377023106'),(201,201,1,400,'2014-07-25 13:43:52',NULL,1,0,'27867557'),(202,202,1,400,'2014-07-25 13:43:52',NULL,1,0,'9443712'),(203,203,1,400,'2014-07-25 13:43:52',NULL,1,0,'5995118'),(204,204,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197359'),(205,205,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547247'),(206,206,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814813'),(207,207,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658824'),(208,208,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555767'),(209,209,1,400,'2014-07-25 13:43:52',NULL,1,0,'27789854'),(210,210,1,400,'2014-07-25 13:43:52',NULL,1,0,'43331847'),(211,211,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881416'),(212,212,1,400,'2014-07-25 13:43:52',NULL,1,0,'27867569'),(213,213,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881466'),(214,214,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658906'),(215,215,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197393'),(216,216,1,400,'2014-07-25 13:43:52',NULL,1,0,'42564964'),(217,217,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555675'),(218,218,1,400,'2014-07-25 13:43:52',NULL,1,0,'20573500'),(219,219,1,400,'2014-07-25 13:43:52',NULL,1,0,'103385822'),(220,220,1,400,'2014-07-25 13:43:52',NULL,1,0,'27867581'),(221,221,1,400,'2014-07-25 13:43:52',NULL,1,0,'27867571'),(222,222,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326612'),(223,223,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881468'),(224,224,1,400,'2014-07-25 13:43:52',NULL,1,0,'27838169'),(225,225,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326614'),(226,226,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197375'),(227,227,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942922'),(228,228,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942924'),(229,229,1,400,'2014-07-25 13:43:52',NULL,1,0,'27867547'),(230,230,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658796'),(231,231,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658798'),(232,232,1,400,'2014-07-25 13:43:52',NULL,1,0,'20471568'),(233,233,1,400,'2014-07-25 13:43:52',NULL,1,0,'20538866'),(234,234,1,400,'2014-07-25 13:43:52',NULL,1,0,'20471576'),(235,235,1,400,'2014-07-25 13:43:52',NULL,1,0,'20503512'),(236,236,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658800'),(237,237,1,400,'2014-07-25 13:43:52',NULL,1,0,'96174415'),(238,238,1,400,'2014-07-25 13:43:52',NULL,1,0,'3846206'),(239,239,1,400,'2014-07-25 13:43:52',NULL,1,0,'27727176'),(240,240,1,400,'2014-07-25 13:43:52',NULL,1,0,'3906092'),(241,241,1,400,'2014-07-25 13:43:52',NULL,1,0,'3906104'),(242,242,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693303'),(243,243,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326616'),(244,244,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326618'),(245,245,1,400,'2014-07-25 13:43:52',NULL,1,0,'42550663'),(246,246,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881446'),(247,247,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560713'),(248,248,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658908'),(249,249,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699628'),(250,250,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942926'),(251,251,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942844'),(252,252,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560715'),(253,253,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560711'),(254,254,1,400,'2014-07-25 13:43:52',NULL,1,0,'42550665'),(255,255,1,400,'2014-07-25 13:43:52',NULL,1,0,'5995106'),(256,256,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881448'),(257,257,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693319'),(258,258,1,400,'2014-07-25 13:43:52',NULL,1,0,'42564966'),(259,259,1,400,'2014-07-25 13:43:52',NULL,1,0,'27700189'),(260,260,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881396'),(261,261,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555677'),(262,262,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547249'),(263,263,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547251'),(264,264,1,400,'2014-07-25 13:43:52',NULL,1,0,'42564968'),(265,265,1,400,'2014-07-25 13:43:52',NULL,1,0,'27789846'),(266,266,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547253'),(267,267,1,400,'2014-07-25 13:43:52',NULL,1,0,'20471570'),(268,268,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881470'),(269,269,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326574'),(270,270,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814835'),(271,271,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555679'),(272,272,1,400,'2014-07-25 13:43:52',NULL,1,0,'42564970'),(273,273,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814837'),(274,274,1,400,'2014-07-25 13:43:52',NULL,1,0,'20538860'),(275,275,1,400,'2014-07-25 13:43:52',NULL,1,0,'20538862'),(276,276,1,400,'2014-07-25 13:43:52',NULL,1,0,'20503510'),(277,277,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699644'),(278,278,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560717'),(279,279,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547303'),(280,280,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326564'),(281,281,1,400,'2014-07-25 13:43:52',NULL,1,0,'42543940'),(282,282,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326620'),(283,283,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881386'),(284,284,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547255'),(285,285,1,400,'2014-07-25 13:43:52',NULL,1,0,'20783810'),(286,286,1,400,'2014-07-25 13:43:52',NULL,1,0,'42564972'),(287,287,1,400,'2014-07-25 13:43:52',NULL,1,0,'42565022'),(288,288,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881388'),(289,289,1,400,'2014-07-25 13:43:52',NULL,1,0,'88266147'),(290,290,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555681'),(291,291,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555683'),(292,292,1,400,'2014-07-25 13:43:52',NULL,1,0,'43341904'),(293,293,1,400,'2014-07-25 13:43:52',NULL,1,0,'42564974'),(294,294,1,400,'2014-07-25 13:43:52',NULL,1,0,'42564962'),(295,295,1,400,'2014-07-25 13:43:52',NULL,1,0,'42550667'),(296,296,1,400,'2014-07-25 13:43:52',NULL,1,0,'51117848'),(297,297,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699646'),(298,298,1,400,'2014-07-25 13:43:52',NULL,1,0,'9443714'),(299,299,1,400,'2014-07-25 13:43:52',NULL,1,0,'42550639'),(300,300,1,400,'2014-07-25 13:43:52',NULL,1,0,'90277439'),(301,301,1,400,'2014-07-25 13:43:52',NULL,1,0,'27789870'),(302,302,1,400,'2014-07-25 13:43:52',NULL,1,0,'42550669'),(303,303,1,400,'2014-07-25 13:43:52',NULL,1,0,'27838181'),(304,304,1,400,'2014-07-25 13:43:52',NULL,1,0,'42543944'),(305,305,1,400,'2014-07-25 13:43:52',NULL,1,0,'377023610'),(306,306,1,400,'2014-07-25 13:43:52',NULL,1,0,'42564978'),(307,307,1,400,'2014-07-25 13:43:52',NULL,1,0,'42564982'),(308,308,1,400,'2014-07-25 13:43:52',NULL,1,0,'27727164'),(309,309,1,400,'2014-07-25 13:43:52',NULL,1,0,'42543936'),(310,310,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942846'),(311,311,1,400,'2014-07-25 13:43:52',NULL,1,0,'27727166'),(312,312,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942848'),(313,313,1,400,'2014-07-25 13:43:52',NULL,1,0,'377023628'),(314,314,1,400,'2014-07-25 13:43:52',NULL,1,0,'45086801'),(315,315,1,400,'2014-07-25 13:43:52',NULL,1,0,'27727170'),(316,316,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560669'),(317,317,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658842'),(318,318,1,400,'2014-07-25 13:43:52',NULL,1,0,'89524025'),(319,319,1,400,'2014-07-25 13:43:52',NULL,1,0,'3795893'),(320,320,1,400,'2014-07-25 13:43:52',NULL,1,0,'43337570'),(321,321,1,400,'2014-07-25 13:43:52',NULL,1,0,'43338121'),(322,322,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693321'),(323,323,1,400,'2014-07-25 13:43:52',NULL,1,0,'20538852'),(324,324,1,400,'2014-07-25 13:43:52',NULL,1,0,'27727172'),(325,325,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326662'),(326,326,1,400,'2014-07-25 13:43:52',NULL,1,0,'3906106'),(327,327,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881390'),(328,328,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555685'),(329,329,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942850'),(330,330,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881424'),(331,331,1,400,'2014-07-25 13:43:52',NULL,1,0,'20783802'),(332,332,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326566'),(333,333,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555687'),(334,334,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693337'),(335,335,1,400,'2014-07-25 13:43:52',NULL,1,0,'42564984'),(336,336,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881426'),(337,337,1,400,'2014-07-25 13:43:52',NULL,1,0,'43341906'),(338,338,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658848'),(339,339,1,400,'2014-07-25 13:43:52',NULL,1,0,'90103497'),(340,340,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555689'),(341,341,1,400,'2014-07-25 13:43:52',NULL,1,0,'117611344'),(342,342,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693327'),(343,343,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693339'),(344,344,1,400,'2014-07-25 13:43:52',NULL,1,0,'27867553'),(345,345,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814807'),(346,346,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658862'),(347,347,1,400,'2014-07-25 13:43:52',NULL,1,0,'42543938'),(348,348,1,400,'2014-07-25 13:43:52',NULL,1,0,'88266149'),(349,349,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658846'),(350,350,1,400,'2014-07-25 13:43:52',NULL,1,0,'27700187'),(351,351,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814847'),(352,352,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555693'),(353,353,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560673'),(354,354,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560671'),(355,355,1,400,'2014-07-25 13:43:52',NULL,1,0,'377023744'),(356,356,1,400,'2014-07-25 13:43:52',NULL,1,0,'5995110'),(357,357,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881450'),(358,358,1,400,'2014-07-25 13:43:52',NULL,1,0,'42564986'),(359,359,1,400,'2014-07-25 13:43:52',NULL,1,0,'43336555'),(360,360,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658818'),(361,361,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555757'),(362,362,1,400,'2014-07-25 13:43:52',NULL,1,0,'27727158'),(363,363,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942854'),(364,364,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942852'),(365,365,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197379'),(366,366,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881452'),(367,367,1,400,'2014-07-25 13:43:52',NULL,1,0,'43334219'),(368,368,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881472'),(369,369,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326624'),(370,370,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658904'),(371,371,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942856'),(372,372,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699660'),(373,373,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942858'),(374,374,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699656'),(375,375,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326648'),(376,376,1,400,'2014-07-25 13:43:52',NULL,1,0,'20439429'),(377,377,1,400,'2014-07-25 13:43:52',NULL,1,0,'43331845'),(378,378,1,400,'2014-07-25 13:43:52',NULL,1,0,'43341908'),(379,379,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658854'),(380,380,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658870'),(381,381,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693361'),(382,382,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942860'),(383,383,1,400,'2014-07-25 13:43:52',NULL,1,0,'149502910'),(384,384,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197369'),(385,385,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197381'),(386,386,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547259'),(387,387,1,400,'2014-07-25 13:43:52',NULL,1,0,'27838185'),(388,388,1,400,'2014-07-25 13:43:52',NULL,1,0,'42564988'),(389,389,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881392'),(390,390,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881394'),(391,391,1,400,'2014-07-25 13:43:52',NULL,1,0,'27727162'),(392,392,1,400,'2014-07-25 13:43:52',NULL,1,0,'377023910'),(393,393,1,400,'2014-07-25 13:43:52',NULL,1,0,'3795899'),(394,394,1,400,'2014-07-25 13:43:52',NULL,1,0,'3906094'),(395,395,1,400,'2014-07-25 13:43:52',NULL,1,0,'42564990'),(396,396,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547261'),(397,397,1,400,'2014-07-25 13:43:52',NULL,1,0,'42564992'),(398,398,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326656'),(399,399,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942862'),(400,400,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555695'),(401,401,1,400,'2014-07-25 13:43:52',NULL,1,0,'43341896'),(402,402,1,400,'2014-07-25 13:43:52',NULL,1,0,'27789860'),(403,403,1,400,'2014-07-25 13:43:52',NULL,1,0,'42564994'),(404,404,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942866'),(405,405,1,400,'2014-07-25 13:43:52',NULL,1,0,'97264052'),(406,406,1,400,'2014-07-25 13:43:52',NULL,1,0,'42543942'),(407,407,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881428'),(408,408,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326626'),(409,409,1,400,'2014-07-25 13:43:52',NULL,1,0,'20696438'),(410,410,1,400,'2014-07-25 13:43:52',NULL,1,0,'42564998'),(411,411,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699668'),(412,412,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693331'),(413,413,1,400,'2014-07-25 13:43:52',NULL,1,0,'5995112'),(414,414,1,400,'2014-07-25 13:43:52',NULL,1,0,'20660761'),(415,415,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555697'),(416,416,1,400,'2014-07-25 13:43:52',NULL,1,0,'45086777'),(417,417,1,400,'2014-07-25 13:43:52',NULL,1,0,'5995114'),(418,418,1,400,'2014-07-25 13:43:52',NULL,1,0,'20471564'),(419,419,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693357'),(420,420,1,400,'2014-07-25 13:43:52',NULL,1,0,'27838173'),(421,421,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693323'),(422,422,1,400,'2014-07-25 13:43:52',NULL,1,0,'88691397'),(423,423,1,400,'2014-07-25 13:43:52',NULL,1,0,'45086803'),(424,424,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560675'),(425,425,1,400,'2014-07-25 13:43:52',NULL,1,0,'27838175'),(426,426,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942868'),(427,427,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560677'),(428,428,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197014'),(429,429,1,400,'2014-07-25 13:43:52',NULL,1,0,'42550641'),(430,430,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197363'),(431,431,1,400,'2014-07-25 13:43:52',NULL,1,0,'3819255'),(432,432,1,400,'2014-07-25 13:43:52',NULL,1,0,'3925312'),(433,433,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814833'),(434,434,1,400,'2014-07-25 13:43:52',NULL,1,0,'45400880'),(435,435,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693309'),(436,436,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555699'),(437,437,1,400,'2014-07-25 13:43:52',NULL,1,0,'27712749'),(438,438,1,400,'2014-07-25 13:43:52',NULL,1,0,'27712759'),(439,439,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326628'),(440,440,1,400,'2014-07-25 13:43:52',NULL,1,0,'20538856'),(441,441,1,400,'2014-07-25 13:43:52',NULL,1,0,'20538858'),(442,442,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197365'),(443,443,1,400,'2014-07-25 13:43:52',NULL,1,0,'3861578'),(444,444,1,400,'2014-07-25 13:43:52',NULL,1,0,'377024136'),(445,445,1,400,'2014-07-25 13:43:52',NULL,1,0,'3819257'),(446,446,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326630'),(447,447,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881456'),(448,448,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326652'),(449,449,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658884'),(450,450,1,400,'2014-07-25 13:43:52',NULL,1,0,'42550643'),(451,451,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699630'),(452,452,1,400,'2014-07-25 13:43:52',NULL,1,0,'88474641'),(453,453,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881398'),(454,454,1,400,'2014-07-25 13:43:52',NULL,1,0,'3906100'),(455,455,1,400,'2014-07-25 13:43:52',NULL,1,0,'88266151'),(456,456,1,400,'2014-07-25 13:43:52',NULL,1,0,'29096840'),(457,457,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814801'),(458,458,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814829'),(459,459,1,400,'2014-07-25 13:43:52',NULL,1,0,'20783804'),(460,460,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881458'),(461,461,1,400,'2014-07-25 13:43:52',NULL,1,0,'27700201'),(462,462,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699648'),(463,463,1,400,'2014-07-25 13:43:52',NULL,1,0,'27712753'),(464,464,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699606'),(465,465,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560709'),(466,466,1,400,'2014-07-25 13:43:52',NULL,1,0,'43337572'),(467,467,1,400,'2014-07-25 13:43:52',NULL,1,0,'27700191'),(468,468,1,400,'2014-07-25 13:43:52',NULL,1,0,'43331837'),(469,469,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547257'),(470,470,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942874'),(471,471,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658814'),(472,472,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555701'),(473,473,1,400,'2014-07-25 13:43:52',NULL,1,0,'98156118'),(474,474,1,400,'2014-07-25 13:43:52',NULL,1,0,'20573502'),(475,475,1,400,'2014-07-25 13:43:52',NULL,1,0,'27789844'),(476,476,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658886'),(477,477,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547263'),(478,478,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555703'),(479,479,1,400,'2014-07-25 13:43:52',NULL,1,0,'90277441'),(480,480,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693353'),(481,481,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699632'),(482,482,1,400,'2014-07-25 13:43:52',NULL,1,0,'3795901'),(483,483,1,400,'2014-07-25 13:43:52',NULL,1,0,'20503516'),(484,484,1,400,'2014-07-25 13:43:52',NULL,1,0,'43333524'),(485,485,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658826'),(486,486,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658808'),(487,487,1,400,'2014-07-25 13:43:52',NULL,1,0,'88474643'),(488,488,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814811'),(489,489,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814851'),(490,490,1,400,'2014-07-25 13:43:52',NULL,1,0,'42565000'),(491,491,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699666'),(492,492,1,400,'2014-07-25 13:43:52',NULL,1,0,'105898493'),(493,493,1,400,'2014-07-25 13:43:52',NULL,1,0,'43331839'),(494,494,1,400,'2014-07-25 13:43:52',NULL,1,0,'103385820'),(495,495,1,400,'2014-07-25 13:43:52',NULL,1,0,'88877878'),(496,496,1,400,'2014-07-25 13:43:52',NULL,1,0,'3925314'),(497,497,1,400,'2014-07-25 13:43:52',NULL,1,0,'27700197'),(498,498,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881400'),(499,499,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555705'),(500,500,1,400,'2014-07-25 13:43:52',NULL,1,0,'88266153'),(501,501,1,400,'2014-07-25 13:43:52',NULL,1,0,'89918298'),(502,502,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555707'),(503,503,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555711'),(504,504,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658860'),(505,505,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693315'),(506,506,1,400,'2014-07-25 13:43:52',NULL,1,0,'27712745'),(507,507,1,400,'2014-07-25 13:43:52',NULL,1,0,'20503496'),(508,508,1,400,'2014-07-25 13:43:52',NULL,1,0,'42553088'),(509,509,1,400,'2014-07-25 13:43:52',NULL,1,0,'42565002'),(510,510,1,400,'2014-07-25 13:43:52',NULL,1,0,'1060287851'),(511,511,1,400,'2014-07-25 13:43:52',NULL,1,0,'42550645'),(512,512,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693299'),(513,513,1,400,'2014-07-25 13:43:52',NULL,1,0,'29096842'),(514,514,1,400,'2014-07-25 13:43:52',NULL,1,0,'88266155'),(515,515,1,400,'2014-07-25 13:43:52',NULL,1,0,'42550647'),(516,516,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560681'),(517,517,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547267'),(518,518,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881474'),(519,519,1,400,'2014-07-25 13:43:52',NULL,1,0,'27727174'),(520,520,1,400,'2014-07-25 13:43:52',NULL,1,0,'20503514'),(521,521,1,400,'2014-07-25 13:43:52',NULL,1,0,'3819259'),(522,522,1,400,'2014-07-25 13:43:52',NULL,1,0,'51117856'),(523,523,1,400,'2014-07-25 13:43:52',NULL,1,0,'653474875'),(524,524,1,400,'2014-07-25 13:43:52',NULL,1,0,'42565010'),(525,525,1,400,'2014-07-25 13:43:52',NULL,1,0,'42550649'),(526,526,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326634'),(527,527,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326658'),(528,528,1,400,'2014-07-25 13:43:52',NULL,1,0,'89918300'),(529,529,1,400,'2014-07-25 13:43:52',NULL,1,0,'6067833'),(530,530,1,400,'2014-07-25 13:43:52',NULL,1,0,'20439433'),(531,531,1,400,'2014-07-25 13:43:52',NULL,1,0,'20503508'),(532,532,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547269'),(533,533,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814839'),(534,534,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814841'),(535,535,1,400,'2014-07-25 13:43:52',NULL,1,0,'42543930'),(536,536,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197401'),(537,537,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699638'),(538,538,1,400,'2014-07-25 13:43:52',NULL,1,0,'6036645'),(539,539,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547273'),(540,540,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693335'),(541,541,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942876'),(542,542,1,400,'2014-07-25 13:43:52',NULL,1,0,'43334213'),(543,543,1,400,'2014-07-25 13:43:52',NULL,1,0,'27867563'),(544,544,1,400,'2014-07-25 13:43:52',NULL,1,0,'27867567'),(545,545,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814861'),(546,546,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942878'),(547,547,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555715'),(548,548,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658850'),(549,549,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197399'),(550,550,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547275'),(551,551,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326622'),(552,552,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942880'),(553,553,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699636'),(554,554,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560685'),(555,555,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547277'),(556,556,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658838'),(557,557,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560687'),(558,558,1,400,'2014-07-25 13:43:52',NULL,1,0,'45086785'),(559,559,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693305'),(560,560,1,400,'2014-07-25 13:43:52',NULL,1,0,'88474645'),(561,561,1,400,'2014-07-25 13:43:52',NULL,1,0,'6067831'),(562,562,1,400,'2014-07-25 13:43:52',NULL,1,0,'5995098'),(563,563,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560689'),(564,564,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560691'),(565,565,1,400,'2014-07-25 13:43:52',NULL,1,0,'42550651'),(566,566,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560707'),(567,567,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326636'),(568,568,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693329'),(569,569,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658812'),(570,570,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555717'),(571,571,1,400,'2014-07-25 13:43:52',NULL,1,0,'27838177'),(572,572,1,400,'2014-07-25 13:43:52',NULL,1,0,'27838179'),(573,573,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881402'),(574,574,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658872'),(575,575,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326654'),(576,576,1,400,'2014-07-25 13:43:52',NULL,1,0,'20503500'),(577,577,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560693'),(578,578,1,400,'2014-07-25 13:43:52',NULL,1,0,'27867539'),(579,579,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326578'),(580,580,1,400,'2014-07-25 13:43:52',NULL,1,0,'3846208'),(581,581,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326580'),(582,582,1,400,'2014-07-25 13:43:52',NULL,1,0,'9443716'),(583,583,1,400,'2014-07-25 13:43:52',NULL,1,0,'27727152'),(584,584,1,400,'2014-07-25 13:43:52',NULL,1,0,'29096844'),(585,585,1,400,'2014-07-25 13:43:52',NULL,1,0,'43334217'),(586,586,1,400,'2014-07-25 13:43:52',NULL,1,0,'3975004'),(587,587,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547279'),(588,588,1,400,'2014-07-25 13:43:52',NULL,1,0,'5995122'),(589,589,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197387'),(590,590,1,400,'2014-07-25 13:43:52',NULL,1,0,'27700185'),(591,591,1,400,'2014-07-25 13:43:52',NULL,1,0,'27867575'),(592,592,1,400,'2014-07-25 13:43:52',NULL,1,0,'27867559'),(593,593,1,400,'2014-07-25 13:43:52',NULL,1,0,'27700205'),(594,594,1,400,'2014-07-25 13:43:52',NULL,1,0,'27700193'),(595,595,1,400,'2014-07-25 13:43:52',NULL,1,0,'88474647'),(596,596,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814849'),(597,597,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814855'),(598,598,1,400,'2014-07-25 13:43:52',NULL,1,0,'97719058'),(599,599,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547281'),(600,600,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326554'),(601,601,1,400,'2014-07-25 13:43:52',NULL,1,0,'43341898'),(602,602,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814857'),(603,603,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699642'),(604,604,1,400,'2014-07-25 13:43:52',NULL,1,0,'20661152'),(605,605,1,400,'2014-07-25 13:43:52',NULL,1,0,'20670172'),(606,606,1,400,'2014-07-25 13:43:52',NULL,1,0,'5995124'),(607,607,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881484'),(608,608,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326568'),(609,609,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658888'),(610,610,1,400,'2014-07-25 13:43:52',NULL,1,0,'43336551'),(611,611,1,400,'2014-07-25 13:43:52',NULL,1,0,'27838167'),(612,612,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197389'),(613,613,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881404'),(614,614,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547283'),(615,615,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555719'),(616,616,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555721'),(617,617,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555723'),(618,618,1,400,'2014-07-25 13:43:52',NULL,1,0,'6118889'),(619,619,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560679'),(620,620,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547285'),(621,621,1,400,'2014-07-25 13:43:52',NULL,1,0,'3925316'),(622,622,1,400,'2014-07-25 13:43:52',NULL,1,0,'42565004'),(623,623,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547287'),(624,624,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555725'),(625,625,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555727'),(626,626,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942882'),(627,627,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942884'),(628,628,1,400,'2014-07-25 13:43:52',NULL,1,0,'3906110'),(629,629,1,400,'2014-07-25 13:43:52',NULL,1,0,'3906108'),(630,630,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942888'),(631,631,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881406'),(632,632,1,400,'2014-07-25 13:43:52',NULL,1,0,'5995126'),(633,633,1,400,'2014-07-25 13:43:52',NULL,1,0,'20696440'),(634,634,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814819'),(635,635,1,400,'2014-07-25 13:43:52',NULL,1,0,'27789850'),(636,636,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547289'),(637,637,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699650'),(638,638,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658804'),(639,639,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881460'),(640,640,1,400,'2014-07-25 13:43:52',NULL,1,0,'45086787'),(641,641,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658790'),(642,642,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881420'),(643,643,1,400,'2014-07-25 13:43:52',NULL,1,0,'45086779'),(644,644,1,400,'2014-07-25 13:43:52',NULL,1,0,'20660764'),(645,645,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555729'),(646,646,1,400,'2014-07-25 13:43:52',NULL,1,0,'377025124'),(647,647,1,400,'2014-07-25 13:43:52',NULL,1,0,'89077807'),(648,648,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560695'),(649,649,1,400,'2014-07-25 13:43:52',NULL,1,0,'117610476'),(650,650,1,400,'2014-07-25 13:43:52',NULL,1,0,'20783806'),(651,651,1,400,'2014-07-25 13:43:52',NULL,1,0,'3795905'),(652,652,1,400,'2014-07-25 13:43:52',NULL,1,0,'103385824'),(653,653,1,400,'2014-07-25 13:43:52',NULL,1,0,'103385826'),(654,654,1,400,'2014-07-25 13:43:52',NULL,1,0,'90103499'),(655,655,1,400,'2014-07-25 13:43:52',NULL,1,0,'27727160'),(656,656,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942890'),(657,657,1,400,'2014-07-25 13:43:52',NULL,1,0,'88266145'),(658,658,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814845'),(659,659,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326638'),(660,660,1,400,'2014-07-25 13:43:52',NULL,1,0,'27838171'),(661,661,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881462'),(662,662,1,400,'2014-07-25 13:43:52',NULL,1,0,'377025172'),(663,663,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658816'),(664,664,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942892'),(665,665,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326560'),(666,666,1,400,'2014-07-25 13:43:52',NULL,1,0,'20439435'),(667,667,1,400,'2014-07-25 13:43:52',NULL,1,0,'5995128'),(668,668,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942894'),(669,669,1,400,'2014-07-25 13:43:52',NULL,1,0,'89292063'),(670,670,1,400,'2014-07-25 13:43:52',NULL,1,0,'3906088'),(671,671,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560697'),(672,672,1,400,'2014-07-25 13:43:52',NULL,1,0,'43331831'),(673,673,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881464'),(674,674,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814843'),(675,675,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547291'),(676,676,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658792'),(677,677,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197391'),(678,678,1,400,'2014-07-25 13:43:52',NULL,1,0,'29096846'),(679,679,1,400,'2014-07-25 13:43:52',NULL,1,0,'20503492'),(680,680,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555731'),(681,681,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555733'),(682,682,1,400,'2014-07-25 13:43:52',NULL,1,0,'42550653'),(683,683,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555735'),(684,684,1,400,'2014-07-25 13:43:52',NULL,1,0,'20503502'),(685,685,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693347'),(686,686,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693345'),(687,687,1,400,'2014-07-25 13:43:52',NULL,1,0,'43331841'),(688,688,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326556'),(689,689,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555737'),(690,690,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555739'),(691,691,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658902'),(692,692,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658856'),(693,693,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881482'),(694,694,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881408'),(695,695,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881410'),(696,696,1,400,'2014-07-25 13:43:52',NULL,1,0,'51117840'),(697,697,1,400,'2014-07-25 13:43:52',NULL,1,0,'20660768'),(698,698,1,400,'2014-07-25 13:43:52',NULL,1,0,'20439437'),(699,699,1,400,'2014-07-25 13:43:52',NULL,1,0,'20503504'),(700,700,1,400,'2014-07-25 13:43:52',NULL,1,0,'20439439'),(701,701,1,400,'2014-07-25 13:43:52',NULL,1,0,'51117842'),(702,702,1,400,'2014-07-25 13:43:52',NULL,1,0,'377025288'),(703,703,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658828'),(704,704,1,400,'2014-07-25 13:43:52',NULL,1,0,'42565006'),(705,705,1,400,'2014-07-25 13:43:52',NULL,1,0,'45086783'),(706,706,1,400,'2014-07-25 13:43:52',NULL,1,0,'45086781'),(707,707,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560699'),(708,708,1,400,'2014-07-25 13:43:52',NULL,1,0,'20783812'),(709,709,1,400,'2014-07-25 13:43:52',NULL,1,0,'42565008'),(710,710,1,400,'2014-07-25 13:43:52',NULL,1,0,'6067835'),(711,711,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658896'),(712,712,1,400,'2014-07-25 13:43:52',NULL,1,0,'96174411'),(713,713,1,400,'2014-07-25 13:43:52',NULL,1,0,'96174413'),(714,714,1,400,'2014-07-25 13:43:52',NULL,1,0,'42553090'),(715,715,1,400,'2014-07-25 13:43:52',NULL,1,0,'43331849'),(716,716,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658810'),(717,717,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555741'),(718,718,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555743'),(719,719,1,400,'2014-07-25 13:43:52',NULL,1,0,'6118885'),(720,720,1,400,'2014-07-25 13:43:52',NULL,1,0,'51117838'),(721,721,1,400,'2014-07-25 13:43:52',NULL,1,0,'43336553'),(722,722,1,400,'2014-07-25 13:43:52',NULL,1,0,'43336549'),(723,723,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881434'),(724,724,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658900'),(725,725,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693343'),(726,726,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326644'),(727,727,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326582'),(728,728,1,400,'2014-07-25 13:43:52',NULL,1,0,'88266135'),(729,729,1,400,'2014-07-25 13:43:52',NULL,1,0,'51117844'),(730,730,1,400,'2014-07-25 13:43:52',NULL,1,0,'9443710'),(731,731,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197016'),(732,732,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814827'),(733,733,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699652'),(734,734,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942896'),(735,735,1,400,'2014-07-25 13:43:52',NULL,1,0,'27727154'),(736,736,1,400,'2014-07-25 13:43:52',NULL,1,0,'20503494'),(737,737,1,400,'2014-07-25 13:43:52',NULL,1,0,'20503506'),(738,738,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814799'),(739,739,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814863'),(740,740,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881478'),(741,741,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547239'),(742,742,1,400,'2014-07-25 13:43:52',NULL,1,0,'27867551'),(743,743,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942898'),(744,744,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658844'),(745,745,1,400,'2014-07-25 13:43:52',NULL,1,0,'43336557'),(746,746,1,400,'2014-07-25 13:43:52',NULL,1,0,'42550655'),(747,747,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942900'),(748,748,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942902'),(749,749,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555745'),(750,750,1,400,'2014-07-25 13:43:52',NULL,1,0,'88266137'),(751,751,1,400,'2014-07-25 13:43:52',NULL,1,0,'20439441'),(752,752,1,400,'2014-07-25 13:43:52',NULL,1,0,'20439443'),(753,753,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197403'),(754,754,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555747'),(755,755,1,400,'2014-07-25 13:43:52',NULL,1,0,'5995130'),(756,756,1,400,'2014-07-25 13:43:52',NULL,1,0,'20573494'),(757,757,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942904'),(758,758,1,400,'2014-07-25 13:43:52',NULL,1,0,'3795907'),(759,759,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326584'),(760,760,1,400,'2014-07-25 13:43:52',NULL,1,0,'3795909'),(761,761,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693355'),(762,762,1,400,'2014-07-25 13:43:52',NULL,1,0,'5995136'),(763,763,1,400,'2014-07-25 13:43:52',NULL,1,0,'27867579'),(764,764,1,400,'2014-07-25 13:43:52',NULL,1,0,'45086789'),(765,765,1,400,'2014-07-25 13:43:52',NULL,1,0,'88691389'),(766,766,1,400,'2014-07-25 13:43:52',NULL,1,0,'42565012'),(767,767,1,400,'2014-07-25 13:43:52',NULL,1,0,'20471572'),(768,768,1,400,'2014-07-25 13:43:52',NULL,1,0,'27867573'),(769,769,1,400,'2014-07-25 13:43:52',NULL,1,0,'20471574'),(770,770,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560701'),(771,771,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547295'),(772,772,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814821'),(773,773,1,400,'2014-07-25 13:43:52',NULL,1,0,'42550659'),(774,774,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547301'),(775,775,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693359'),(776,776,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881376'),(777,777,1,400,'2014-07-25 13:43:52',NULL,1,0,'97264044'),(778,778,1,400,'2014-07-25 13:43:52',NULL,1,0,'97264046'),(779,779,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699640'),(780,780,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881480'),(781,781,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555751'),(782,782,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658802'),(783,783,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658876'),(784,784,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658878'),(785,785,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547299'),(786,786,1,400,'2014-07-25 13:43:52',NULL,1,0,'42565016'),(787,787,1,400,'2014-07-25 13:43:52',NULL,1,0,'20783814'),(788,788,1,400,'2014-07-25 13:43:52',NULL,1,0,'45086791'),(789,789,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560705'),(790,790,1,400,'2014-07-25 13:43:52',NULL,1,0,'42565018'),(791,791,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942910'),(792,792,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326590'),(793,793,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197371'),(794,794,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693333'),(795,795,1,400,'2014-07-25 13:43:52',NULL,1,0,'27755384'),(796,796,1,400,'2014-07-25 13:43:52',NULL,1,0,'27814825'),(797,797,1,400,'2014-07-25 13:43:52',NULL,1,0,'42555753'),(798,798,1,400,'2014-07-25 13:43:52',NULL,1,0,'3819261'),(799,799,1,400,'2014-07-25 13:43:52',NULL,1,0,'50693351'),(800,800,1,400,'2014-07-25 13:43:52',NULL,1,0,'43333522'),(801,801,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326586'),(802,802,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699612'),(803,803,1,400,'2014-07-25 13:43:52',NULL,1,0,'27789862'),(804,804,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197373'),(805,805,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547265'),(806,806,1,400,'2014-07-25 13:43:52',NULL,1,0,'43326646'),(807,807,1,400,'2014-07-25 13:43:52',NULL,1,0,'20696442'),(808,808,1,400,'2014-07-25 13:43:52',NULL,1,0,'42560667'),(809,809,1,400,'2014-07-25 13:43:52',NULL,1,0,'42565020'),(810,810,1,400,'2014-07-25 13:43:52',NULL,1,0,'42547297'),(811,811,1,400,'2014-07-25 13:43:52',NULL,1,0,'6277443'),(812,812,1,400,'2014-07-25 13:43:52',NULL,1,0,'3819263'),(813,813,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197383'),(814,814,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197377'),(815,815,1,400,'2014-07-25 13:43:52',NULL,1,0,'53197385'),(816,816,1,400,'2014-07-25 13:43:52',NULL,1,0,'50699610'),(817,817,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881380'),(818,818,1,400,'2014-07-25 13:43:52',NULL,1,0,'20783816'),(819,819,1,400,'2014-07-25 13:43:52',NULL,1,0,'27838189'),(820,820,1,400,'2014-07-25 13:43:52',NULL,1,0,'27867565'),(821,821,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881436'),(822,822,1,400,'2014-07-25 13:43:52',NULL,1,0,'50658890'),(823,823,1,400,'2014-07-25 13:43:52',NULL,1,0,'88691391'),(824,824,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942912'),(825,825,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942914'),(826,826,1,400,'2014-07-25 13:43:52',NULL,1,0,'5942916'),(827,827,1,400,'2014-07-25 13:43:52',NULL,1,0,'94881438'),(828,828,1,400,'2014-07-25 13:43:52',NULL,1,0,'42543934');
/*!40000 ALTER TABLE `street` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `street_attribute`
--

DROP TABLE IF EXISTS `street_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `street_attribute` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` bigint(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 300 - НАИМЕНОВАНИЕ УЛИЦЫ, 301 - ТИП УЛИЦЫ',
  `value_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор значения',
  `value_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа значения атрибута: 300 - STRING_VALUE, 301 - street_type',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_street_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`) REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_street_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`) REFERENCES `entity_attribute_value_type` (`id`),
  CONSTRAINT `fk_street_attribute__street` FOREIGN KEY (`object_id`) REFERENCES `street` (`object_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1657 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Атрибуты улицы';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `street_attribute`
--

LOCK TABLES `street_attribute` WRITE;
/*!40000 ALTER TABLE `street_attribute` DISABLE KEYS */;
INSERT INTO `street_attribute` VALUES (1,1,1,300,1,300,'2014-07-25 13:43:52',NULL,1),(2,1,1,301,16,301,'2014-07-25 13:43:52',NULL,1),(3,1,2,300,2,300,'2014-07-25 13:43:52',NULL,1),(4,1,2,301,5,301,'2014-07-25 13:43:52',NULL,1),(5,1,3,300,3,300,'2014-07-25 13:43:52',NULL,1),(6,1,3,301,6,301,'2014-07-25 13:43:52',NULL,1),(7,1,4,300,4,300,'2014-07-25 13:43:52',NULL,1),(8,1,4,301,16,301,'2014-07-25 13:43:52',NULL,1),(9,1,5,300,5,300,'2014-07-25 13:43:52',NULL,1),(10,1,5,301,6,301,'2014-07-25 13:43:52',NULL,1),(11,1,6,300,6,300,'2014-07-25 13:43:52',NULL,1),(12,1,6,301,16,301,'2014-07-25 13:43:52',NULL,1),(13,1,7,300,7,300,'2014-07-25 13:43:52',NULL,1),(14,1,7,301,2,301,'2014-07-25 13:43:52',NULL,1),(15,1,8,300,8,300,'2014-07-25 13:43:52',NULL,1),(16,1,8,301,10,301,'2014-07-25 13:43:52',NULL,1),(17,1,9,300,9,300,'2014-07-25 13:43:52',NULL,1),(18,1,9,301,16,301,'2014-07-25 13:43:52',NULL,1),(19,1,10,300,10,300,'2014-07-25 13:43:52',NULL,1),(20,1,10,301,16,301,'2014-07-25 13:43:52',NULL,1),(21,1,11,300,11,300,'2014-07-25 13:43:52',NULL,1),(22,1,11,301,16,301,'2014-07-25 13:43:52',NULL,1),(23,1,12,300,12,300,'2014-07-25 13:43:52',NULL,1),(24,1,12,301,11,301,'2014-07-25 13:43:52',NULL,1),(25,1,13,300,13,300,'2014-07-25 13:43:52',NULL,1),(26,1,13,301,16,301,'2014-07-25 13:43:52',NULL,1),(27,1,14,300,14,300,'2014-07-25 13:43:52',NULL,1),(28,1,14,301,16,301,'2014-07-25 13:43:52',NULL,1),(29,1,15,300,15,300,'2014-07-25 13:43:52',NULL,1),(30,1,15,301,16,301,'2014-07-25 13:43:52',NULL,1),(31,1,16,300,16,300,'2014-07-25 13:43:52',NULL,1),(32,1,16,301,16,301,'2014-07-25 13:43:52',NULL,1),(33,1,17,300,17,300,'2014-07-25 13:43:52',NULL,1),(34,1,17,301,16,301,'2014-07-25 13:43:52',NULL,1),(35,1,18,300,18,300,'2014-07-25 13:43:52',NULL,1),(36,1,18,301,16,301,'2014-07-25 13:43:52',NULL,1),(37,1,19,300,19,300,'2014-07-25 13:43:52',NULL,1),(38,1,19,301,16,301,'2014-07-25 13:43:52',NULL,1),(39,1,20,300,20,300,'2014-07-25 13:43:52',NULL,1),(40,1,20,301,16,301,'2014-07-25 13:43:52',NULL,1),(41,1,21,300,21,300,'2014-07-25 13:43:52',NULL,1),(42,1,21,301,16,301,'2014-07-25 13:43:52',NULL,1),(43,1,22,300,22,300,'2014-07-25 13:43:52',NULL,1),(44,1,22,301,6,301,'2014-07-25 13:43:52',NULL,1),(45,1,23,300,23,300,'2014-07-25 13:43:52',NULL,1),(46,1,23,301,6,301,'2014-07-25 13:43:52',NULL,1),(47,1,24,300,24,300,'2014-07-25 13:43:52',NULL,1),(48,1,24,301,16,301,'2014-07-25 13:43:52',NULL,1),(49,1,25,300,25,300,'2014-07-25 13:43:52',NULL,1),(50,1,25,301,16,301,'2014-07-25 13:43:52',NULL,1),(51,1,26,300,26,300,'2014-07-25 13:43:52',NULL,1),(52,1,26,301,2,301,'2014-07-25 13:43:52',NULL,1),(53,1,27,300,27,300,'2014-07-25 13:43:52',NULL,1),(54,1,27,301,6,301,'2014-07-25 13:43:52',NULL,1),(55,1,28,300,28,300,'2014-07-25 13:43:52',NULL,1),(56,1,28,301,16,301,'2014-07-25 13:43:52',NULL,1),(57,1,29,300,29,300,'2014-07-25 13:43:52',NULL,1),(58,1,29,301,16,301,'2014-07-25 13:43:52',NULL,1),(59,1,30,300,30,300,'2014-07-25 13:43:52',NULL,1),(60,1,30,301,2,301,'2014-07-25 13:43:52',NULL,1),(61,1,31,300,31,300,'2014-07-25 13:43:52',NULL,1),(62,1,31,301,6,301,'2014-07-25 13:43:52',NULL,1),(63,1,32,300,32,300,'2014-07-25 13:43:52',NULL,1),(64,1,32,301,16,301,'2014-07-25 13:43:52',NULL,1),(65,1,33,300,33,300,'2014-07-25 13:43:52',NULL,1),(66,1,33,301,16,301,'2014-07-25 13:43:52',NULL,1),(67,1,34,300,34,300,'2014-07-25 13:43:52',NULL,1),(68,1,34,301,6,301,'2014-07-25 13:43:52',NULL,1),(69,1,35,300,35,300,'2014-07-25 13:43:52',NULL,1),(70,1,35,301,16,301,'2014-07-25 13:43:52',NULL,1),(71,1,36,300,36,300,'2014-07-25 13:43:52',NULL,1),(72,1,36,301,16,301,'2014-07-25 13:43:52',NULL,1),(73,1,37,300,37,300,'2014-07-25 13:43:52',NULL,1),(74,1,37,301,16,301,'2014-07-25 13:43:52',NULL,1),(75,1,38,300,38,300,'2014-07-25 13:43:52',NULL,1),(76,1,38,301,16,301,'2014-07-25 13:43:52',NULL,1),(77,1,39,300,39,300,'2014-07-25 13:43:52',NULL,1),(78,1,39,301,16,301,'2014-07-25 13:43:52',NULL,1),(79,1,40,300,40,300,'2014-07-25 13:43:52',NULL,1),(80,1,40,301,16,301,'2014-07-25 13:43:52',NULL,1),(81,1,41,300,41,300,'2014-07-25 13:43:52',NULL,1),(82,1,41,301,16,301,'2014-07-25 13:43:52',NULL,1),(83,1,42,300,42,300,'2014-07-25 13:43:52',NULL,1),(84,1,42,301,16,301,'2014-07-25 13:43:52',NULL,1),(85,1,43,300,43,300,'2014-07-25 13:43:52',NULL,1),(86,1,43,301,6,301,'2014-07-25 13:43:52',NULL,1),(87,1,44,300,44,300,'2014-07-25 13:43:52',NULL,1),(88,1,44,301,16,301,'2014-07-25 13:43:52',NULL,1),(89,1,45,300,45,300,'2014-07-25 13:43:52',NULL,1),(90,1,45,301,16,301,'2014-07-25 13:43:52',NULL,1),(91,1,46,300,46,300,'2014-07-25 13:43:52',NULL,1),(92,1,46,301,16,301,'2014-07-25 13:43:52',NULL,1),(93,1,47,300,47,300,'2014-07-25 13:43:52',NULL,1),(94,1,47,301,6,301,'2014-07-25 13:43:52',NULL,1),(95,1,48,300,48,300,'2014-07-25 13:43:52',NULL,1),(96,1,48,301,16,301,'2014-07-25 13:43:52',NULL,1),(97,1,49,300,49,300,'2014-07-25 13:43:52',NULL,1),(98,1,49,301,16,301,'2014-07-25 13:43:52',NULL,1),(99,1,50,300,50,300,'2014-07-25 13:43:52',NULL,1),(100,1,50,301,16,301,'2014-07-25 13:43:52',NULL,1),(101,1,51,300,51,300,'2014-07-25 13:43:52',NULL,1),(102,1,51,301,16,301,'2014-07-25 13:43:52',NULL,1),(103,1,52,300,52,300,'2014-07-25 13:43:52',NULL,1),(104,1,52,301,2,301,'2014-07-25 13:43:52',NULL,1),(105,1,53,300,53,300,'2014-07-25 13:43:52',NULL,1),(106,1,53,301,2,301,'2014-07-25 13:43:52',NULL,1),(107,1,54,300,54,300,'2014-07-25 13:43:52',NULL,1),(108,1,54,301,16,301,'2014-07-25 13:43:52',NULL,1),(109,1,55,300,55,300,'2014-07-25 13:43:52',NULL,1),(110,1,55,301,16,301,'2014-07-25 13:43:52',NULL,1),(111,1,56,300,56,300,'2014-07-25 13:43:52',NULL,1),(112,1,56,301,16,301,'2014-07-25 13:43:52',NULL,1),(113,1,57,300,57,300,'2014-07-25 13:43:52',NULL,1),(114,1,57,301,16,301,'2014-07-25 13:43:52',NULL,1),(115,1,58,300,58,300,'2014-07-25 13:43:52',NULL,1),(116,1,58,301,16,301,'2014-07-25 13:43:52',NULL,1),(117,1,59,300,59,300,'2014-07-25 13:43:52',NULL,1),(118,1,59,301,16,301,'2014-07-25 13:43:52',NULL,1),(119,1,60,300,60,300,'2014-07-25 13:43:52',NULL,1),(120,1,60,301,16,301,'2014-07-25 13:43:52',NULL,1),(121,1,61,300,61,300,'2014-07-25 13:43:52',NULL,1),(122,1,61,301,16,301,'2014-07-25 13:43:52',NULL,1),(123,1,62,300,62,300,'2014-07-25 13:43:52',NULL,1),(124,1,62,301,6,301,'2014-07-25 13:43:52',NULL,1),(125,1,63,300,63,300,'2014-07-25 13:43:52',NULL,1),(126,1,63,301,16,301,'2014-07-25 13:43:52',NULL,1),(127,1,64,300,64,300,'2014-07-25 13:43:52',NULL,1),(128,1,64,301,6,301,'2014-07-25 13:43:52',NULL,1),(129,1,65,300,65,300,'2014-07-25 13:43:52',NULL,1),(130,1,65,301,16,301,'2014-07-25 13:43:52',NULL,1),(131,1,66,300,66,300,'2014-07-25 13:43:52',NULL,1),(132,1,66,301,16,301,'2014-07-25 13:43:52',NULL,1),(133,1,67,300,67,300,'2014-07-25 13:43:52',NULL,1),(134,1,67,301,16,301,'2014-07-25 13:43:52',NULL,1),(135,1,68,300,68,300,'2014-07-25 13:43:52',NULL,1),(136,1,68,301,16,301,'2014-07-25 13:43:52',NULL,1),(137,1,69,300,69,300,'2014-07-25 13:43:52',NULL,1),(138,1,69,301,6,301,'2014-07-25 13:43:52',NULL,1),(139,1,70,300,70,300,'2014-07-25 13:43:52',NULL,1),(140,1,70,301,16,301,'2014-07-25 13:43:52',NULL,1),(141,1,71,300,71,300,'2014-07-25 13:43:52',NULL,1),(142,1,71,301,16,301,'2014-07-25 13:43:52',NULL,1),(143,1,72,300,72,300,'2014-07-25 13:43:52',NULL,1),(144,1,72,301,16,301,'2014-07-25 13:43:52',NULL,1),(145,1,73,300,73,300,'2014-07-25 13:43:52',NULL,1),(146,1,73,301,16,301,'2014-07-25 13:43:52',NULL,1),(147,1,74,300,74,300,'2014-07-25 13:43:52',NULL,1),(148,1,74,301,1,301,'2014-07-25 13:43:52',NULL,1),(149,1,75,300,75,300,'2014-07-25 13:43:52',NULL,1),(150,1,75,301,16,301,'2014-07-25 13:43:52',NULL,1),(151,1,76,300,76,300,'2014-07-25 13:43:52',NULL,1),(152,1,76,301,16,301,'2014-07-25 13:43:52',NULL,1),(153,1,77,300,77,300,'2014-07-25 13:43:52',NULL,1),(154,1,77,301,6,301,'2014-07-25 13:43:52',NULL,1),(155,1,78,300,78,300,'2014-07-25 13:43:52',NULL,1),(156,1,78,301,16,301,'2014-07-25 13:43:52',NULL,1),(157,1,79,300,79,300,'2014-07-25 13:43:52',NULL,1),(158,1,79,301,16,301,'2014-07-25 13:43:52',NULL,1),(159,1,80,300,80,300,'2014-07-25 13:43:52',NULL,1),(160,1,80,301,6,301,'2014-07-25 13:43:52',NULL,1),(161,1,81,300,81,300,'2014-07-25 13:43:52',NULL,1),(162,1,81,301,16,301,'2014-07-25 13:43:52',NULL,1),(163,1,82,300,82,300,'2014-07-25 13:43:52',NULL,1),(164,1,82,301,16,301,'2014-07-25 13:43:52',NULL,1),(165,1,83,300,83,300,'2014-07-25 13:43:52',NULL,1),(166,1,83,301,6,301,'2014-07-25 13:43:52',NULL,1),(167,1,84,300,84,300,'2014-07-25 13:43:52',NULL,1),(168,1,84,301,16,301,'2014-07-25 13:43:52',NULL,1),(169,1,85,300,85,300,'2014-07-25 13:43:52',NULL,1),(170,1,85,301,16,301,'2014-07-25 13:43:52',NULL,1),(171,1,86,300,86,300,'2014-07-25 13:43:52',NULL,1),(172,1,86,301,16,301,'2014-07-25 13:43:52',NULL,1),(173,1,87,300,87,300,'2014-07-25 13:43:52',NULL,1),(174,1,87,301,6,301,'2014-07-25 13:43:52',NULL,1),(175,1,88,300,88,300,'2014-07-25 13:43:52',NULL,1),(176,1,88,301,16,301,'2014-07-25 13:43:52',NULL,1),(177,1,89,300,89,300,'2014-07-25 13:43:52',NULL,1),(178,1,89,301,16,301,'2014-07-25 13:43:52',NULL,1),(179,1,90,300,90,300,'2014-07-25 13:43:52',NULL,1),(180,1,90,301,16,301,'2014-07-25 13:43:52',NULL,1),(181,1,91,300,91,300,'2014-07-25 13:43:52',NULL,1),(182,1,91,301,6,301,'2014-07-25 13:43:52',NULL,1),(183,1,92,300,92,300,'2014-07-25 13:43:52',NULL,1),(184,1,92,301,16,301,'2014-07-25 13:43:52',NULL,1),(185,1,93,300,93,300,'2014-07-25 13:43:52',NULL,1),(186,1,93,301,12,301,'2014-07-25 13:43:52',NULL,1),(187,1,94,300,94,300,'2014-07-25 13:43:52',NULL,1),(188,1,94,301,2,301,'2014-07-25 13:43:52',NULL,1),(189,1,95,300,95,300,'2014-07-25 13:43:52',NULL,1),(190,1,95,301,16,301,'2014-07-25 13:43:52',NULL,1),(191,1,96,300,96,300,'2014-07-25 13:43:52',NULL,1),(192,1,96,301,16,301,'2014-07-25 13:43:52',NULL,1),(193,1,97,300,97,300,'2014-07-25 13:43:52',NULL,1),(194,1,97,301,16,301,'2014-07-25 13:43:52',NULL,1),(195,1,98,300,98,300,'2014-07-25 13:43:52',NULL,1),(196,1,98,301,16,301,'2014-07-25 13:43:52',NULL,1),(197,1,99,300,99,300,'2014-07-25 13:43:52',NULL,1),(198,1,99,301,6,301,'2014-07-25 13:43:52',NULL,1),(199,1,100,300,100,300,'2014-07-25 13:43:52',NULL,1),(200,1,100,301,16,301,'2014-07-25 13:43:52',NULL,1),(201,1,101,300,101,300,'2014-07-25 13:43:52',NULL,1),(202,1,101,301,6,301,'2014-07-25 13:43:52',NULL,1),(203,1,102,300,102,300,'2014-07-25 13:43:52',NULL,1),(204,1,102,301,16,301,'2014-07-25 13:43:52',NULL,1),(205,1,103,300,103,300,'2014-07-25 13:43:52',NULL,1),(206,1,103,301,16,301,'2014-07-25 13:43:52',NULL,1),(207,1,104,300,104,300,'2014-07-25 13:43:52',NULL,1),(208,1,104,301,6,301,'2014-07-25 13:43:52',NULL,1),(209,1,105,300,105,300,'2014-07-25 13:43:52',NULL,1),(210,1,105,301,16,301,'2014-07-25 13:43:52',NULL,1),(211,1,106,300,106,300,'2014-07-25 13:43:52',NULL,1),(212,1,106,301,6,301,'2014-07-25 13:43:52',NULL,1),(213,1,107,300,107,300,'2014-07-25 13:43:52',NULL,1),(214,1,107,301,6,301,'2014-07-25 13:43:52',NULL,1),(215,1,108,300,108,300,'2014-07-25 13:43:52',NULL,1),(216,1,108,301,16,301,'2014-07-25 13:43:52',NULL,1),(217,1,109,300,109,300,'2014-07-25 13:43:52',NULL,1),(218,1,109,301,16,301,'2014-07-25 13:43:52',NULL,1),(219,1,110,300,110,300,'2014-07-25 13:43:52',NULL,1),(220,1,110,301,16,301,'2014-07-25 13:43:52',NULL,1),(221,1,111,300,111,300,'2014-07-25 13:43:52',NULL,1),(222,1,111,301,16,301,'2014-07-25 13:43:52',NULL,1),(223,1,112,300,112,300,'2014-07-25 13:43:52',NULL,1),(224,1,112,301,16,301,'2014-07-25 13:43:52',NULL,1),(225,1,113,300,113,300,'2014-07-25 13:43:52',NULL,1),(226,1,113,301,16,301,'2014-07-25 13:43:52',NULL,1),(227,1,114,300,114,300,'2014-07-25 13:43:52',NULL,1),(228,1,114,301,16,301,'2014-07-25 13:43:52',NULL,1),(229,1,115,300,115,300,'2014-07-25 13:43:52',NULL,1),(230,1,115,301,16,301,'2014-07-25 13:43:52',NULL,1),(231,1,116,300,116,300,'2014-07-25 13:43:52',NULL,1),(232,1,116,301,6,301,'2014-07-25 13:43:52',NULL,1),(233,1,117,300,117,300,'2014-07-25 13:43:52',NULL,1),(234,1,117,301,2,301,'2014-07-25 13:43:52',NULL,1),(235,1,118,300,118,300,'2014-07-25 13:43:52',NULL,1),(236,1,118,301,16,301,'2014-07-25 13:43:52',NULL,1),(237,1,119,300,119,300,'2014-07-25 13:43:52',NULL,1),(238,1,119,301,6,301,'2014-07-25 13:43:52',NULL,1),(239,1,120,300,120,300,'2014-07-25 13:43:52',NULL,1),(240,1,120,301,16,301,'2014-07-25 13:43:52',NULL,1),(241,1,121,300,121,300,'2014-07-25 13:43:52',NULL,1),(242,1,121,301,6,301,'2014-07-25 13:43:52',NULL,1),(243,1,122,300,122,300,'2014-07-25 13:43:52',NULL,1),(244,1,122,301,16,301,'2014-07-25 13:43:52',NULL,1),(245,1,123,300,123,300,'2014-07-25 13:43:52',NULL,1),(246,1,123,301,6,301,'2014-07-25 13:43:52',NULL,1),(247,1,124,300,124,300,'2014-07-25 13:43:52',NULL,1),(248,1,124,301,7,301,'2014-07-25 13:43:52',NULL,1),(249,1,125,300,125,300,'2014-07-25 13:43:52',NULL,1),(250,1,125,301,16,301,'2014-07-25 13:43:52',NULL,1),(251,1,126,300,126,300,'2014-07-25 13:43:52',NULL,1),(252,1,126,301,16,301,'2014-07-25 13:43:52',NULL,1),(253,1,127,300,127,300,'2014-07-25 13:43:52',NULL,1),(254,1,127,301,16,301,'2014-07-25 13:43:52',NULL,1),(255,1,128,300,128,300,'2014-07-25 13:43:52',NULL,1),(256,1,128,301,16,301,'2014-07-25 13:43:52',NULL,1),(257,1,129,300,129,300,'2014-07-25 13:43:52',NULL,1),(258,1,129,301,11,301,'2014-07-25 13:43:52',NULL,1),(259,1,130,300,130,300,'2014-07-25 13:43:52',NULL,1),(260,1,130,301,16,301,'2014-07-25 13:43:52',NULL,1),(261,1,131,300,131,300,'2014-07-25 13:43:52',NULL,1),(262,1,131,301,16,301,'2014-07-25 13:43:52',NULL,1),(263,1,132,300,132,300,'2014-07-25 13:43:52',NULL,1),(264,1,132,301,16,301,'2014-07-25 13:43:52',NULL,1),(265,1,133,300,133,300,'2014-07-25 13:43:52',NULL,1),(266,1,133,301,16,301,'2014-07-25 13:43:52',NULL,1),(267,1,134,300,134,300,'2014-07-25 13:43:52',NULL,1),(268,1,134,301,16,301,'2014-07-25 13:43:52',NULL,1),(269,1,135,300,135,300,'2014-07-25 13:43:52',NULL,1),(270,1,135,301,16,301,'2014-07-25 13:43:52',NULL,1),(271,1,136,300,136,300,'2014-07-25 13:43:52',NULL,1),(272,1,136,301,16,301,'2014-07-25 13:43:52',NULL,1),(273,1,137,300,137,300,'2014-07-25 13:43:52',NULL,1),(274,1,137,301,16,301,'2014-07-25 13:43:52',NULL,1),(275,1,138,300,138,300,'2014-07-25 13:43:52',NULL,1),(276,1,138,301,16,301,'2014-07-25 13:43:52',NULL,1),(277,1,139,300,139,300,'2014-07-25 13:43:52',NULL,1),(278,1,139,301,16,301,'2014-07-25 13:43:52',NULL,1),(279,1,140,300,140,300,'2014-07-25 13:43:52',NULL,1),(280,1,140,301,16,301,'2014-07-25 13:43:52',NULL,1),(281,1,141,300,141,300,'2014-07-25 13:43:52',NULL,1),(282,1,141,301,16,301,'2014-07-25 13:43:52',NULL,1),(283,1,142,300,142,300,'2014-07-25 13:43:52',NULL,1),(284,1,142,301,2,301,'2014-07-25 13:43:52',NULL,1),(285,1,143,300,143,300,'2014-07-25 13:43:52',NULL,1),(286,1,143,301,6,301,'2014-07-25 13:43:52',NULL,1),(287,1,144,300,144,300,'2014-07-25 13:43:52',NULL,1),(288,1,144,301,11,301,'2014-07-25 13:43:52',NULL,1),(289,1,145,300,145,300,'2014-07-25 13:43:52',NULL,1),(290,1,145,301,16,301,'2014-07-25 13:43:52',NULL,1),(291,1,146,300,146,300,'2014-07-25 13:43:52',NULL,1),(292,1,146,301,16,301,'2014-07-25 13:43:52',NULL,1),(293,1,147,300,147,300,'2014-07-25 13:43:52',NULL,1),(294,1,147,301,16,301,'2014-07-25 13:43:52',NULL,1),(295,1,148,300,148,300,'2014-07-25 13:43:52',NULL,1),(296,1,148,301,16,301,'2014-07-25 13:43:52',NULL,1),(297,1,149,300,149,300,'2014-07-25 13:43:52',NULL,1),(298,1,149,301,16,301,'2014-07-25 13:43:52',NULL,1),(299,1,150,300,150,300,'2014-07-25 13:43:52',NULL,1),(300,1,150,301,16,301,'2014-07-25 13:43:52',NULL,1),(301,1,151,300,151,300,'2014-07-25 13:43:52',NULL,1),(302,1,151,301,16,301,'2014-07-25 13:43:52',NULL,1),(303,1,152,300,152,300,'2014-07-25 13:43:52',NULL,1),(304,1,152,301,6,301,'2014-07-25 13:43:52',NULL,1),(305,1,153,300,153,300,'2014-07-25 13:43:52',NULL,1),(306,1,153,301,16,301,'2014-07-25 13:43:52',NULL,1),(307,1,154,300,154,300,'2014-07-25 13:43:52',NULL,1),(308,1,154,301,16,301,'2014-07-25 13:43:52',NULL,1),(309,1,155,300,155,300,'2014-07-25 13:43:52',NULL,1),(310,1,155,301,6,301,'2014-07-25 13:43:52',NULL,1),(311,1,156,300,156,300,'2014-07-25 13:43:52',NULL,1),(312,1,156,301,16,301,'2014-07-25 13:43:52',NULL,1),(313,1,157,300,157,300,'2014-07-25 13:43:52',NULL,1),(314,1,157,301,6,301,'2014-07-25 13:43:52',NULL,1),(315,1,158,300,158,300,'2014-07-25 13:43:52',NULL,1),(316,1,158,301,16,301,'2014-07-25 13:43:52',NULL,1),(317,1,159,300,159,300,'2014-07-25 13:43:52',NULL,1),(318,1,159,301,16,301,'2014-07-25 13:43:52',NULL,1),(319,1,160,300,160,300,'2014-07-25 13:43:52',NULL,1),(320,1,160,301,16,301,'2014-07-25 13:43:52',NULL,1),(321,1,161,300,161,300,'2014-07-25 13:43:52',NULL,1),(322,1,161,301,16,301,'2014-07-25 13:43:52',NULL,1),(323,1,162,300,162,300,'2014-07-25 13:43:52',NULL,1),(324,1,162,301,1,301,'2014-07-25 13:43:52',NULL,1),(325,1,163,300,163,300,'2014-07-25 13:43:52',NULL,1),(326,1,163,301,16,301,'2014-07-25 13:43:52',NULL,1),(327,1,164,300,164,300,'2014-07-25 13:43:52',NULL,1),(328,1,164,301,16,301,'2014-07-25 13:43:52',NULL,1),(329,1,165,300,165,300,'2014-07-25 13:43:52',NULL,1),(330,1,165,301,16,301,'2014-07-25 13:43:52',NULL,1),(331,1,166,300,166,300,'2014-07-25 13:43:52',NULL,1),(332,1,166,301,16,301,'2014-07-25 13:43:52',NULL,1),(333,1,167,300,167,300,'2014-07-25 13:43:52',NULL,1),(334,1,167,301,16,301,'2014-07-25 13:43:52',NULL,1),(335,1,168,300,168,300,'2014-07-25 13:43:52',NULL,1),(336,1,168,301,16,301,'2014-07-25 13:43:52',NULL,1),(337,1,169,300,169,300,'2014-07-25 13:43:52',NULL,1),(338,1,169,301,16,301,'2014-07-25 13:43:52',NULL,1),(339,1,170,300,170,300,'2014-07-25 13:43:52',NULL,1),(340,1,170,301,16,301,'2014-07-25 13:43:52',NULL,1),(341,1,171,300,171,300,'2014-07-25 13:43:52',NULL,1),(342,1,171,301,6,301,'2014-07-25 13:43:52',NULL,1),(343,1,172,300,172,300,'2014-07-25 13:43:52',NULL,1),(344,1,172,301,6,301,'2014-07-25 13:43:52',NULL,1),(345,1,173,300,173,300,'2014-07-25 13:43:52',NULL,1),(346,1,173,301,16,301,'2014-07-25 13:43:52',NULL,1),(347,1,174,300,174,300,'2014-07-25 13:43:52',NULL,1),(348,1,174,301,16,301,'2014-07-25 13:43:52',NULL,1),(349,1,175,300,175,300,'2014-07-25 13:43:52',NULL,1),(350,1,175,301,16,301,'2014-07-25 13:43:52',NULL,1),(351,1,176,300,176,300,'2014-07-25 13:43:52',NULL,1),(352,1,176,301,16,301,'2014-07-25 13:43:52',NULL,1),(353,1,177,300,177,300,'2014-07-25 13:43:52',NULL,1),(354,1,177,301,16,301,'2014-07-25 13:43:52',NULL,1),(355,1,178,300,178,300,'2014-07-25 13:43:52',NULL,1),(356,1,178,301,16,301,'2014-07-25 13:43:52',NULL,1),(357,1,179,300,179,300,'2014-07-25 13:43:52',NULL,1),(358,1,179,301,16,301,'2014-07-25 13:43:52',NULL,1),(359,1,180,300,180,300,'2014-07-25 13:43:52',NULL,1),(360,1,180,301,6,301,'2014-07-25 13:43:52',NULL,1),(361,1,181,300,181,300,'2014-07-25 13:43:52',NULL,1),(362,1,181,301,6,301,'2014-07-25 13:43:52',NULL,1),(363,1,182,300,182,300,'2014-07-25 13:43:52',NULL,1),(364,1,182,301,11,301,'2014-07-25 13:43:52',NULL,1),(365,1,183,300,183,300,'2014-07-25 13:43:52',NULL,1),(366,1,183,301,16,301,'2014-07-25 13:43:52',NULL,1),(367,1,184,300,184,300,'2014-07-25 13:43:52',NULL,1),(368,1,184,301,16,301,'2014-07-25 13:43:52',NULL,1),(369,1,185,300,185,300,'2014-07-25 13:43:52',NULL,1),(370,1,185,301,16,301,'2014-07-25 13:43:52',NULL,1),(371,1,186,300,186,300,'2014-07-25 13:43:52',NULL,1),(372,1,186,301,16,301,'2014-07-25 13:43:52',NULL,1),(373,1,187,300,187,300,'2014-07-25 13:43:52',NULL,1),(374,1,187,301,16,301,'2014-07-25 13:43:52',NULL,1),(375,1,188,300,188,300,'2014-07-25 13:43:52',NULL,1),(376,1,188,301,16,301,'2014-07-25 13:43:52',NULL,1),(377,1,189,300,189,300,'2014-07-25 13:43:52',NULL,1),(378,1,189,301,16,301,'2014-07-25 13:43:52',NULL,1),(379,1,190,300,190,300,'2014-07-25 13:43:52',NULL,1),(380,1,190,301,16,301,'2014-07-25 13:43:52',NULL,1),(381,1,191,300,191,300,'2014-07-25 13:43:52',NULL,1),(382,1,191,301,6,301,'2014-07-25 13:43:52',NULL,1),(383,1,192,300,192,300,'2014-07-25 13:43:52',NULL,1),(384,1,192,301,16,301,'2014-07-25 13:43:52',NULL,1),(385,1,193,300,193,300,'2014-07-25 13:43:52',NULL,1),(386,1,193,301,16,301,'2014-07-25 13:43:52',NULL,1),(387,1,194,300,194,300,'2014-07-25 13:43:52',NULL,1),(388,1,194,301,6,301,'2014-07-25 13:43:52',NULL,1),(389,1,195,300,195,300,'2014-07-25 13:43:52',NULL,1),(390,1,195,301,6,301,'2014-07-25 13:43:52',NULL,1),(391,1,196,300,196,300,'2014-07-25 13:43:52',NULL,1),(392,1,196,301,16,301,'2014-07-25 13:43:52',NULL,1),(393,1,197,300,197,300,'2014-07-25 13:43:52',NULL,1),(394,1,197,301,6,301,'2014-07-25 13:43:52',NULL,1),(395,1,198,300,198,300,'2014-07-25 13:43:52',NULL,1),(396,1,198,301,16,301,'2014-07-25 13:43:52',NULL,1),(397,1,199,300,199,300,'2014-07-25 13:43:52',NULL,1),(398,1,199,301,6,301,'2014-07-25 13:43:52',NULL,1),(399,1,200,300,200,300,'2014-07-25 13:43:52',NULL,1),(400,1,200,301,2,301,'2014-07-25 13:43:52',NULL,1),(401,1,201,300,201,300,'2014-07-25 13:43:52',NULL,1),(402,1,201,301,16,301,'2014-07-25 13:43:52',NULL,1),(403,1,202,300,202,300,'2014-07-25 13:43:52',NULL,1),(404,1,202,301,16,301,'2014-07-25 13:43:52',NULL,1),(405,1,203,300,203,300,'2014-07-25 13:43:52',NULL,1),(406,1,203,301,16,301,'2014-07-25 13:43:52',NULL,1),(407,1,204,300,204,300,'2014-07-25 13:43:52',NULL,1),(408,1,204,301,10,301,'2014-07-25 13:43:52',NULL,1),(409,1,205,300,205,300,'2014-07-25 13:43:52',NULL,1),(410,1,205,301,16,301,'2014-07-25 13:43:52',NULL,1),(411,1,206,300,206,300,'2014-07-25 13:43:52',NULL,1),(412,1,206,301,16,301,'2014-07-25 13:43:52',NULL,1),(413,1,207,300,207,300,'2014-07-25 13:43:52',NULL,1),(414,1,207,301,16,301,'2014-07-25 13:43:52',NULL,1),(415,1,208,300,208,300,'2014-07-25 13:43:52',NULL,1),(416,1,208,301,6,301,'2014-07-25 13:43:52',NULL,1),(417,1,209,300,209,300,'2014-07-25 13:43:52',NULL,1),(418,1,209,301,16,301,'2014-07-25 13:43:52',NULL,1),(419,1,210,300,210,300,'2014-07-25 13:43:52',NULL,1),(420,1,210,301,16,301,'2014-07-25 13:43:52',NULL,1),(421,1,211,300,211,300,'2014-07-25 13:43:52',NULL,1),(422,1,211,301,16,301,'2014-07-25 13:43:52',NULL,1),(423,1,212,300,212,300,'2014-07-25 13:43:52',NULL,1),(424,1,212,301,16,301,'2014-07-25 13:43:52',NULL,1),(425,1,213,300,213,300,'2014-07-25 13:43:52',NULL,1),(426,1,213,301,6,301,'2014-07-25 13:43:52',NULL,1),(427,1,214,300,214,300,'2014-07-25 13:43:52',NULL,1),(428,1,214,301,16,301,'2014-07-25 13:43:52',NULL,1),(429,1,215,300,215,300,'2014-07-25 13:43:52',NULL,1),(430,1,215,301,11,301,'2014-07-25 13:43:52',NULL,1),(431,1,216,300,216,300,'2014-07-25 13:43:52',NULL,1),(432,1,216,301,16,301,'2014-07-25 13:43:52',NULL,1),(433,1,217,300,217,300,'2014-07-25 13:43:52',NULL,1),(434,1,217,301,16,301,'2014-07-25 13:43:52',NULL,1),(435,1,218,300,218,300,'2014-07-25 13:43:52',NULL,1),(436,1,218,301,6,301,'2014-07-25 13:43:52',NULL,1),(437,1,219,300,219,300,'2014-07-25 13:43:52',NULL,1),(438,1,219,301,16,301,'2014-07-25 13:43:52',NULL,1),(439,1,220,300,220,300,'2014-07-25 13:43:52',NULL,1),(440,1,220,301,16,301,'2014-07-25 13:43:52',NULL,1),(441,1,221,300,221,300,'2014-07-25 13:43:52',NULL,1),(442,1,221,301,6,301,'2014-07-25 13:43:52',NULL,1),(443,1,222,300,222,300,'2014-07-25 13:43:52',NULL,1),(444,1,222,301,16,301,'2014-07-25 13:43:52',NULL,1),(445,1,223,300,223,300,'2014-07-25 13:43:52',NULL,1),(446,1,223,301,16,301,'2014-07-25 13:43:52',NULL,1),(447,1,224,300,224,300,'2014-07-25 13:43:52',NULL,1),(448,1,224,301,16,301,'2014-07-25 13:43:52',NULL,1),(449,1,225,300,225,300,'2014-07-25 13:43:52',NULL,1),(450,1,225,301,16,301,'2014-07-25 13:43:52',NULL,1),(451,1,226,300,226,300,'2014-07-25 13:43:52',NULL,1),(452,1,226,301,16,301,'2014-07-25 13:43:52',NULL,1),(453,1,227,300,227,300,'2014-07-25 13:43:52',NULL,1),(454,1,227,301,6,301,'2014-07-25 13:43:52',NULL,1),(455,1,228,300,228,300,'2014-07-25 13:43:52',NULL,1),(456,1,228,301,5,301,'2014-07-25 13:43:52',NULL,1),(457,1,229,300,229,300,'2014-07-25 13:43:52',NULL,1),(458,1,229,301,16,301,'2014-07-25 13:43:52',NULL,1),(459,1,230,300,230,300,'2014-07-25 13:43:52',NULL,1),(460,1,230,301,16,301,'2014-07-25 13:43:52',NULL,1),(461,1,231,300,231,300,'2014-07-25 13:43:52',NULL,1),(462,1,231,301,6,301,'2014-07-25 13:43:52',NULL,1),(463,1,232,300,232,300,'2014-07-25 13:43:52',NULL,1),(464,1,232,301,16,301,'2014-07-25 13:43:52',NULL,1),(465,1,233,300,233,300,'2014-07-25 13:43:52',NULL,1),(466,1,233,301,6,301,'2014-07-25 13:43:52',NULL,1),(467,1,234,300,234,300,'2014-07-25 13:43:52',NULL,1),(468,1,234,301,10,301,'2014-07-25 13:43:52',NULL,1),(469,1,235,300,235,300,'2014-07-25 13:43:52',NULL,1),(470,1,235,301,2,301,'2014-07-25 13:43:52',NULL,1),(471,1,236,300,236,300,'2014-07-25 13:43:52',NULL,1),(472,1,236,301,16,301,'2014-07-25 13:43:52',NULL,1),(473,1,237,300,237,300,'2014-07-25 13:43:52',NULL,1),(474,1,237,301,6,301,'2014-07-25 13:43:52',NULL,1),(475,1,238,300,238,300,'2014-07-25 13:43:52',NULL,1),(476,1,238,301,16,301,'2014-07-25 13:43:52',NULL,1),(477,1,239,300,239,300,'2014-07-25 13:43:52',NULL,1),(478,1,239,301,6,301,'2014-07-25 13:43:52',NULL,1),(479,1,240,300,240,300,'2014-07-25 13:43:52',NULL,1),(480,1,240,301,1,301,'2014-07-25 13:43:52',NULL,1),(481,1,241,300,241,300,'2014-07-25 13:43:52',NULL,1),(482,1,241,301,6,301,'2014-07-25 13:43:52',NULL,1),(483,1,242,300,242,300,'2014-07-25 13:43:52',NULL,1),(484,1,242,301,16,301,'2014-07-25 13:43:52',NULL,1),(485,1,243,300,243,300,'2014-07-25 13:43:52',NULL,1),(486,1,243,301,16,301,'2014-07-25 13:43:52',NULL,1),(487,1,244,300,244,300,'2014-07-25 13:43:52',NULL,1),(488,1,244,301,6,301,'2014-07-25 13:43:52',NULL,1),(489,1,245,300,245,300,'2014-07-25 13:43:52',NULL,1),(490,1,245,301,6,301,'2014-07-25 13:43:52',NULL,1),(491,1,246,300,246,300,'2014-07-25 13:43:52',NULL,1),(492,1,246,301,16,301,'2014-07-25 13:43:52',NULL,1),(493,1,247,300,247,300,'2014-07-25 13:43:52',NULL,1),(494,1,247,301,11,301,'2014-07-25 13:43:52',NULL,1),(495,1,248,300,248,300,'2014-07-25 13:43:52',NULL,1),(496,1,248,301,16,301,'2014-07-25 13:43:52',NULL,1),(497,1,249,300,249,300,'2014-07-25 13:43:52',NULL,1),(498,1,249,301,16,301,'2014-07-25 13:43:52',NULL,1),(499,1,250,300,250,300,'2014-07-25 13:43:52',NULL,1),(500,1,250,301,16,301,'2014-07-25 13:43:52',NULL,1),(501,1,251,300,251,300,'2014-07-25 13:43:52',NULL,1),(502,1,251,301,6,301,'2014-07-25 13:43:52',NULL,1),(503,1,252,300,252,300,'2014-07-25 13:43:52',NULL,1),(504,1,252,301,16,301,'2014-07-25 13:43:52',NULL,1),(505,1,253,300,253,300,'2014-07-25 13:43:52',NULL,1),(506,1,253,301,16,301,'2014-07-25 13:43:52',NULL,1),(507,1,254,300,254,300,'2014-07-25 13:43:52',NULL,1),(508,1,254,301,16,301,'2014-07-25 13:43:52',NULL,1),(509,1,255,300,255,300,'2014-07-25 13:43:52',NULL,1),(510,1,255,301,16,301,'2014-07-25 13:43:52',NULL,1),(511,1,256,300,256,300,'2014-07-25 13:43:52',NULL,1),(512,1,256,301,16,301,'2014-07-25 13:43:52',NULL,1),(513,1,257,300,257,300,'2014-07-25 13:43:52',NULL,1),(514,1,257,301,16,301,'2014-07-25 13:43:52',NULL,1),(515,1,258,300,258,300,'2014-07-25 13:43:52',NULL,1),(516,1,258,301,17,301,'2014-07-25 13:43:52',NULL,1),(517,1,259,300,259,300,'2014-07-25 13:43:52',NULL,1),(518,1,259,301,6,301,'2014-07-25 13:43:52',NULL,1),(519,1,260,300,260,300,'2014-07-25 13:43:52',NULL,1),(520,1,260,301,16,301,'2014-07-25 13:43:52',NULL,1),(521,1,261,300,261,300,'2014-07-25 13:43:52',NULL,1),(522,1,261,301,16,301,'2014-07-25 13:43:52',NULL,1),(523,1,262,300,262,300,'2014-07-25 13:43:52',NULL,1),(524,1,262,301,16,301,'2014-07-25 13:43:52',NULL,1),(525,1,263,300,263,300,'2014-07-25 13:43:52',NULL,1),(526,1,263,301,6,301,'2014-07-25 13:43:52',NULL,1),(527,1,264,300,264,300,'2014-07-25 13:43:52',NULL,1),(528,1,264,301,16,301,'2014-07-25 13:43:52',NULL,1),(529,1,265,300,265,300,'2014-07-25 13:43:52',NULL,1),(530,1,265,301,16,301,'2014-07-25 13:43:52',NULL,1),(531,1,266,300,266,300,'2014-07-25 13:43:52',NULL,1),(532,1,266,301,16,301,'2014-07-25 13:43:52',NULL,1),(533,1,267,300,267,300,'2014-07-25 13:43:52',NULL,1),(534,1,267,301,16,301,'2014-07-25 13:43:52',NULL,1),(535,1,268,300,268,300,'2014-07-25 13:43:52',NULL,1),(536,1,268,301,16,301,'2014-07-25 13:43:52',NULL,1),(537,1,269,300,269,300,'2014-07-25 13:43:52',NULL,1),(538,1,269,301,6,301,'2014-07-25 13:43:52',NULL,1),(539,1,270,300,270,300,'2014-07-25 13:43:52',NULL,1),(540,1,270,301,16,301,'2014-07-25 13:43:52',NULL,1),(541,1,271,300,271,300,'2014-07-25 13:43:52',NULL,1),(542,1,271,301,16,301,'2014-07-25 13:43:52',NULL,1),(543,1,272,300,272,300,'2014-07-25 13:43:52',NULL,1),(544,1,272,301,16,301,'2014-07-25 13:43:52',NULL,1),(545,1,273,300,273,300,'2014-07-25 13:43:52',NULL,1),(546,1,273,301,16,301,'2014-07-25 13:43:52',NULL,1),(547,1,274,300,274,300,'2014-07-25 13:43:52',NULL,1),(548,1,274,301,16,301,'2014-07-25 13:43:52',NULL,1),(549,1,275,300,275,300,'2014-07-25 13:43:52',NULL,1),(550,1,275,301,10,301,'2014-07-25 13:43:52',NULL,1),(551,1,276,300,276,300,'2014-07-25 13:43:52',NULL,1),(552,1,276,301,16,301,'2014-07-25 13:43:52',NULL,1),(553,1,277,300,277,300,'2014-07-25 13:43:52',NULL,1),(554,1,277,301,16,301,'2014-07-25 13:43:52',NULL,1),(555,1,278,300,278,300,'2014-07-25 13:43:52',NULL,1),(556,1,278,301,16,301,'2014-07-25 13:43:52',NULL,1),(557,1,279,300,279,300,'2014-07-25 13:43:52',NULL,1),(558,1,279,301,16,301,'2014-07-25 13:43:52',NULL,1),(559,1,280,300,280,300,'2014-07-25 13:43:52',NULL,1),(560,1,280,301,6,301,'2014-07-25 13:43:52',NULL,1),(561,1,281,300,281,300,'2014-07-25 13:43:52',NULL,1),(562,1,281,301,6,301,'2014-07-25 13:43:52',NULL,1),(563,1,282,300,282,300,'2014-07-25 13:43:52',NULL,1),(564,1,282,301,16,301,'2014-07-25 13:43:52',NULL,1),(565,1,283,300,283,300,'2014-07-25 13:43:52',NULL,1),(566,1,283,301,6,301,'2014-07-25 13:43:52',NULL,1),(567,1,284,300,284,300,'2014-07-25 13:43:52',NULL,1),(568,1,284,301,6,301,'2014-07-25 13:43:52',NULL,1),(569,1,285,300,285,300,'2014-07-25 13:43:52',NULL,1),(570,1,285,301,16,301,'2014-07-25 13:43:52',NULL,1),(571,1,286,300,286,300,'2014-07-25 13:43:52',NULL,1),(572,1,286,301,16,301,'2014-07-25 13:43:52',NULL,1),(573,1,287,300,287,300,'2014-07-25 13:43:52',NULL,1),(574,1,287,301,6,301,'2014-07-25 13:43:52',NULL,1),(575,1,288,300,288,300,'2014-07-25 13:43:52',NULL,1),(576,1,288,301,16,301,'2014-07-25 13:43:52',NULL,1),(577,1,289,300,289,300,'2014-07-25 13:43:52',NULL,1),(578,1,289,301,16,301,'2014-07-25 13:43:52',NULL,1),(579,1,290,300,290,300,'2014-07-25 13:43:52',NULL,1),(580,1,290,301,16,301,'2014-07-25 13:43:52',NULL,1),(581,1,291,300,291,300,'2014-07-25 13:43:52',NULL,1),(582,1,291,301,6,301,'2014-07-25 13:43:52',NULL,1),(583,1,292,300,292,300,'2014-07-25 13:43:52',NULL,1),(584,1,292,301,16,301,'2014-07-25 13:43:52',NULL,1),(585,1,293,300,293,300,'2014-07-25 13:43:52',NULL,1),(586,1,293,301,16,301,'2014-07-25 13:43:52',NULL,1),(587,1,294,300,294,300,'2014-07-25 13:43:52',NULL,1),(588,1,294,301,6,301,'2014-07-25 13:43:52',NULL,1),(589,1,295,300,295,300,'2014-07-25 13:43:52',NULL,1),(590,1,295,301,16,301,'2014-07-25 13:43:52',NULL,1),(591,1,296,300,296,300,'2014-07-25 13:43:52',NULL,1),(592,1,296,301,10,301,'2014-07-25 13:43:52',NULL,1),(593,1,297,300,297,300,'2014-07-25 13:43:52',NULL,1),(594,1,297,301,16,301,'2014-07-25 13:43:52',NULL,1),(595,1,298,300,298,300,'2014-07-25 13:43:52',NULL,1),(596,1,298,301,16,301,'2014-07-25 13:43:52',NULL,1),(597,1,299,300,299,300,'2014-07-25 13:43:52',NULL,1),(598,1,299,301,16,301,'2014-07-25 13:43:52',NULL,1),(599,1,300,300,300,300,'2014-07-25 13:43:52',NULL,1),(600,1,300,301,10,301,'2014-07-25 13:43:52',NULL,1),(601,1,301,300,301,300,'2014-07-25 13:43:52',NULL,1),(602,1,301,301,16,301,'2014-07-25 13:43:52',NULL,1),(603,1,302,300,302,300,'2014-07-25 13:43:52',NULL,1),(604,1,302,301,17,301,'2014-07-25 13:43:52',NULL,1),(605,1,303,300,303,300,'2014-07-25 13:43:52',NULL,1),(606,1,303,301,16,301,'2014-07-25 13:43:52',NULL,1),(607,1,304,300,304,300,'2014-07-25 13:43:52',NULL,1),(608,1,304,301,16,301,'2014-07-25 13:43:52',NULL,1),(609,1,305,300,305,300,'2014-07-25 13:43:52',NULL,1),(610,1,305,301,16,301,'2014-07-25 13:43:52',NULL,1),(611,1,306,300,306,300,'2014-07-25 13:43:52',NULL,1),(612,1,306,301,16,301,'2014-07-25 13:43:52',NULL,1),(613,1,307,300,307,300,'2014-07-25 13:43:52',NULL,1),(614,1,307,301,6,301,'2014-07-25 13:43:52',NULL,1),(615,1,308,300,308,300,'2014-07-25 13:43:52',NULL,1),(616,1,308,301,7,301,'2014-07-25 13:43:52',NULL,1),(617,1,309,300,309,300,'2014-07-25 13:43:52',NULL,1),(618,1,309,301,6,301,'2014-07-25 13:43:52',NULL,1),(619,1,310,300,310,300,'2014-07-25 13:43:52',NULL,1),(620,1,310,301,6,301,'2014-07-25 13:43:52',NULL,1),(621,1,311,300,311,300,'2014-07-25 13:43:52',NULL,1),(622,1,311,301,16,301,'2014-07-25 13:43:52',NULL,1),(623,1,312,300,312,300,'2014-07-25 13:43:52',NULL,1),(624,1,312,301,16,301,'2014-07-25 13:43:52',NULL,1),(625,1,313,300,313,300,'2014-07-25 13:43:52',NULL,1),(626,1,313,301,6,301,'2014-07-25 13:43:52',NULL,1),(627,1,314,300,314,300,'2014-07-25 13:43:52',NULL,1),(628,1,314,301,16,301,'2014-07-25 13:43:52',NULL,1),(629,1,315,300,315,300,'2014-07-25 13:43:52',NULL,1),(630,1,315,301,6,301,'2014-07-25 13:43:52',NULL,1),(631,1,316,300,316,300,'2014-07-25 13:43:52',NULL,1),(632,1,316,301,16,301,'2014-07-25 13:43:52',NULL,1),(633,1,317,300,317,300,'2014-07-25 13:43:52',NULL,1),(634,1,317,301,16,301,'2014-07-25 13:43:52',NULL,1),(635,1,318,300,318,300,'2014-07-25 13:43:52',NULL,1),(636,1,318,301,16,301,'2014-07-25 13:43:52',NULL,1),(637,1,319,300,319,300,'2014-07-25 13:43:52',NULL,1),(638,1,319,301,11,301,'2014-07-25 13:43:52',NULL,1),(639,1,320,300,320,300,'2014-07-25 13:43:52',NULL,1),(640,1,320,301,16,301,'2014-07-25 13:43:52',NULL,1),(641,1,321,300,321,300,'2014-07-25 13:43:52',NULL,1),(642,1,321,301,16,301,'2014-07-25 13:43:52',NULL,1),(643,1,322,300,322,300,'2014-07-25 13:43:52',NULL,1),(644,1,322,301,16,301,'2014-07-25 13:43:52',NULL,1),(645,1,323,300,323,300,'2014-07-25 13:43:52',NULL,1),(646,1,323,301,16,301,'2014-07-25 13:43:52',NULL,1),(647,1,324,300,324,300,'2014-07-25 13:43:52',NULL,1),(648,1,324,301,6,301,'2014-07-25 13:43:52',NULL,1),(649,1,325,300,325,300,'2014-07-25 13:43:52',NULL,1),(650,1,325,301,16,301,'2014-07-25 13:43:52',NULL,1),(651,1,326,300,326,300,'2014-07-25 13:43:52',NULL,1),(652,1,326,301,16,301,'2014-07-25 13:43:52',NULL,1),(653,1,327,300,327,300,'2014-07-25 13:43:52',NULL,1),(654,1,327,301,16,301,'2014-07-25 13:43:52',NULL,1),(655,1,328,300,328,300,'2014-07-25 13:43:52',NULL,1),(656,1,328,301,16,301,'2014-07-25 13:43:52',NULL,1),(657,1,329,300,329,300,'2014-07-25 13:43:52',NULL,1),(658,1,329,301,16,301,'2014-07-25 13:43:52',NULL,1),(659,1,330,300,330,300,'2014-07-25 13:43:52',NULL,1),(660,1,330,301,16,301,'2014-07-25 13:43:52',NULL,1),(661,1,331,300,331,300,'2014-07-25 13:43:52',NULL,1),(662,1,331,301,16,301,'2014-07-25 13:43:52',NULL,1),(663,1,332,300,332,300,'2014-07-25 13:43:52',NULL,1),(664,1,332,301,6,301,'2014-07-25 13:43:52',NULL,1),(665,1,333,300,333,300,'2014-07-25 13:43:52',NULL,1),(666,1,333,301,6,301,'2014-07-25 13:43:52',NULL,1),(667,1,334,300,334,300,'2014-07-25 13:43:52',NULL,1),(668,1,334,301,16,301,'2014-07-25 13:43:52',NULL,1),(669,1,335,300,335,300,'2014-07-25 13:43:52',NULL,1),(670,1,335,301,16,301,'2014-07-25 13:43:52',NULL,1),(671,1,336,300,336,300,'2014-07-25 13:43:52',NULL,1),(672,1,336,301,16,301,'2014-07-25 13:43:52',NULL,1),(673,1,337,300,337,300,'2014-07-25 13:43:52',NULL,1),(674,1,337,301,16,301,'2014-07-25 13:43:52',NULL,1),(675,1,338,300,338,300,'2014-07-25 13:43:52',NULL,1),(676,1,338,301,16,301,'2014-07-25 13:43:52',NULL,1),(677,1,339,300,339,300,'2014-07-25 13:43:52',NULL,1),(678,1,339,301,16,301,'2014-07-25 13:43:52',NULL,1),(679,1,340,300,340,300,'2014-07-25 13:43:52',NULL,1),(680,1,340,301,16,301,'2014-07-25 13:43:52',NULL,1),(681,1,341,300,341,300,'2014-07-25 13:43:52',NULL,1),(682,1,341,301,6,301,'2014-07-25 13:43:52',NULL,1),(683,1,342,300,342,300,'2014-07-25 13:43:52',NULL,1),(684,1,342,301,16,301,'2014-07-25 13:43:52',NULL,1),(685,1,343,300,343,300,'2014-07-25 13:43:52',NULL,1),(686,1,343,301,6,301,'2014-07-25 13:43:52',NULL,1),(687,1,344,300,344,300,'2014-07-25 13:43:52',NULL,1),(688,1,344,301,16,301,'2014-07-25 13:43:52',NULL,1),(689,1,345,300,345,300,'2014-07-25 13:43:52',NULL,1),(690,1,345,301,6,301,'2014-07-25 13:43:52',NULL,1),(691,1,346,300,346,300,'2014-07-25 13:43:52',NULL,1),(692,1,346,301,16,301,'2014-07-25 13:43:52',NULL,1),(693,1,347,300,347,300,'2014-07-25 13:43:52',NULL,1),(694,1,347,301,16,301,'2014-07-25 13:43:52',NULL,1),(695,1,348,300,348,300,'2014-07-25 13:43:52',NULL,1),(696,1,348,301,16,301,'2014-07-25 13:43:52',NULL,1),(697,1,349,300,349,300,'2014-07-25 13:43:52',NULL,1),(698,1,349,301,16,301,'2014-07-25 13:43:52',NULL,1),(699,1,350,300,350,300,'2014-07-25 13:43:52',NULL,1),(700,1,350,301,5,301,'2014-07-25 13:43:52',NULL,1),(701,1,351,300,351,300,'2014-07-25 13:43:52',NULL,1),(702,1,351,301,6,301,'2014-07-25 13:43:52',NULL,1),(703,1,352,300,352,300,'2014-07-25 13:43:52',NULL,1),(704,1,352,301,16,301,'2014-07-25 13:43:52',NULL,1),(705,1,353,300,353,300,'2014-07-25 13:43:52',NULL,1),(706,1,353,301,16,301,'2014-07-25 13:43:52',NULL,1),(707,1,354,300,354,300,'2014-07-25 13:43:52',NULL,1),(708,1,354,301,16,301,'2014-07-25 13:43:52',NULL,1),(709,1,355,300,355,300,'2014-07-25 13:43:52',NULL,1),(710,1,355,301,5,301,'2014-07-25 13:43:52',NULL,1),(711,1,356,300,356,300,'2014-07-25 13:43:52',NULL,1),(712,1,356,301,16,301,'2014-07-25 13:43:52',NULL,1),(713,1,357,300,357,300,'2014-07-25 13:43:52',NULL,1),(714,1,357,301,6,301,'2014-07-25 13:43:52',NULL,1),(715,1,358,300,358,300,'2014-07-25 13:43:52',NULL,1),(716,1,358,301,16,301,'2014-07-25 13:43:52',NULL,1),(717,1,359,300,359,300,'2014-07-25 13:43:52',NULL,1),(718,1,359,301,16,301,'2014-07-25 13:43:52',NULL,1),(719,1,360,300,360,300,'2014-07-25 13:43:52',NULL,1),(720,1,360,301,16,301,'2014-07-25 13:43:52',NULL,1),(721,1,361,300,361,300,'2014-07-25 13:43:52',NULL,1),(722,1,361,301,6,301,'2014-07-25 13:43:52',NULL,1),(723,1,362,300,362,300,'2014-07-25 13:43:52',NULL,1),(724,1,362,301,16,301,'2014-07-25 13:43:52',NULL,1),(725,1,363,300,363,300,'2014-07-25 13:43:52',NULL,1),(726,1,363,301,6,301,'2014-07-25 13:43:52',NULL,1),(727,1,364,300,364,300,'2014-07-25 13:43:52',NULL,1),(728,1,364,301,16,301,'2014-07-25 13:43:52',NULL,1),(729,1,365,300,365,300,'2014-07-25 13:43:52',NULL,1),(730,1,365,301,2,301,'2014-07-25 13:43:52',NULL,1),(731,1,366,300,366,300,'2014-07-25 13:43:52',NULL,1),(732,1,366,301,6,301,'2014-07-25 13:43:52',NULL,1),(733,1,367,300,367,300,'2014-07-25 13:43:52',NULL,1),(734,1,367,301,16,301,'2014-07-25 13:43:52',NULL,1),(735,1,368,300,368,300,'2014-07-25 13:43:52',NULL,1),(736,1,368,301,16,301,'2014-07-25 13:43:52',NULL,1),(737,1,369,300,369,300,'2014-07-25 13:43:52',NULL,1),(738,1,369,301,16,301,'2014-07-25 13:43:52',NULL,1),(739,1,370,300,370,300,'2014-07-25 13:43:52',NULL,1),(740,1,370,301,16,301,'2014-07-25 13:43:52',NULL,1),(741,1,371,300,371,300,'2014-07-25 13:43:52',NULL,1),(742,1,371,301,6,301,'2014-07-25 13:43:52',NULL,1),(743,1,372,300,372,300,'2014-07-25 13:43:52',NULL,1),(744,1,372,301,2,301,'2014-07-25 13:43:52',NULL,1),(745,1,373,300,373,300,'2014-07-25 13:43:52',NULL,1),(746,1,373,301,16,301,'2014-07-25 13:43:52',NULL,1),(747,1,374,300,374,300,'2014-07-25 13:43:52',NULL,1),(748,1,374,301,16,301,'2014-07-25 13:43:52',NULL,1),(749,1,375,300,375,300,'2014-07-25 13:43:52',NULL,1),(750,1,375,301,16,301,'2014-07-25 13:43:52',NULL,1),(751,1,376,300,376,300,'2014-07-25 13:43:52',NULL,1),(752,1,376,301,16,301,'2014-07-25 13:43:52',NULL,1),(753,1,377,300,377,300,'2014-07-25 13:43:52',NULL,1),(754,1,377,301,11,301,'2014-07-25 13:43:52',NULL,1),(755,1,378,300,378,300,'2014-07-25 13:43:52',NULL,1),(756,1,378,301,16,301,'2014-07-25 13:43:52',NULL,1),(757,1,379,300,379,300,'2014-07-25 13:43:52',NULL,1),(758,1,379,301,16,301,'2014-07-25 13:43:52',NULL,1),(759,1,380,300,380,300,'2014-07-25 13:43:52',NULL,1),(760,1,380,301,2,301,'2014-07-25 13:43:52',NULL,1),(761,1,381,300,381,300,'2014-07-25 13:43:52',NULL,1),(762,1,381,301,16,301,'2014-07-25 13:43:52',NULL,1),(763,1,382,300,382,300,'2014-07-25 13:43:52',NULL,1),(764,1,382,301,16,301,'2014-07-25 13:43:52',NULL,1),(765,1,383,300,383,300,'2014-07-25 13:43:52',NULL,1),(766,1,383,301,16,301,'2014-07-25 13:43:52',NULL,1),(767,1,384,300,384,300,'2014-07-25 13:43:52',NULL,1),(768,1,384,301,6,301,'2014-07-25 13:43:52',NULL,1),(769,1,385,300,385,300,'2014-07-25 13:43:52',NULL,1),(770,1,385,301,6,301,'2014-07-25 13:43:52',NULL,1),(771,1,386,300,386,300,'2014-07-25 13:43:52',NULL,1),(772,1,386,301,16,301,'2014-07-25 13:43:52',NULL,1),(773,1,387,300,387,300,'2014-07-25 13:43:52',NULL,1),(774,1,387,301,16,301,'2014-07-25 13:43:52',NULL,1),(775,1,388,300,388,300,'2014-07-25 13:43:52',NULL,1),(776,1,388,301,16,301,'2014-07-25 13:43:52',NULL,1),(777,1,389,300,389,300,'2014-07-25 13:43:52',NULL,1),(778,1,389,301,5,301,'2014-07-25 13:43:52',NULL,1),(779,1,390,300,390,300,'2014-07-25 13:43:52',NULL,1),(780,1,390,301,6,301,'2014-07-25 13:43:52',NULL,1),(781,1,391,300,391,300,'2014-07-25 13:43:52',NULL,1),(782,1,391,301,6,301,'2014-07-25 13:43:52',NULL,1),(783,1,392,300,392,300,'2014-07-25 13:43:52',NULL,1),(784,1,392,301,16,301,'2014-07-25 13:43:52',NULL,1),(785,1,393,300,393,300,'2014-07-25 13:43:52',NULL,1),(786,1,393,301,16,301,'2014-07-25 13:43:52',NULL,1),(787,1,394,300,394,300,'2014-07-25 13:43:52',NULL,1),(788,1,394,301,2,301,'2014-07-25 13:43:52',NULL,1),(789,1,395,300,395,300,'2014-07-25 13:43:52',NULL,1),(790,1,395,301,16,301,'2014-07-25 13:43:52',NULL,1),(791,1,396,300,396,300,'2014-07-25 13:43:52',NULL,1),(792,1,396,301,16,301,'2014-07-25 13:43:52',NULL,1),(793,1,397,300,397,300,'2014-07-25 13:43:52',NULL,1),(794,1,397,301,16,301,'2014-07-25 13:43:52',NULL,1),(795,1,398,300,398,300,'2014-07-25 13:43:52',NULL,1),(796,1,398,301,11,301,'2014-07-25 13:43:52',NULL,1),(797,1,399,300,399,300,'2014-07-25 13:43:52',NULL,1),(798,1,399,301,16,301,'2014-07-25 13:43:52',NULL,1),(799,1,400,300,400,300,'2014-07-25 13:43:52',NULL,1),(800,1,400,301,16,301,'2014-07-25 13:43:52',NULL,1),(801,1,401,300,401,300,'2014-07-25 13:43:52',NULL,1),(802,1,401,301,16,301,'2014-07-25 13:43:52',NULL,1),(803,1,402,300,402,300,'2014-07-25 13:43:52',NULL,1),(804,1,402,301,16,301,'2014-07-25 13:43:52',NULL,1),(805,1,403,300,403,300,'2014-07-25 13:43:52',NULL,1),(806,1,403,301,16,301,'2014-07-25 13:43:52',NULL,1),(807,1,404,300,404,300,'2014-07-25 13:43:52',NULL,1),(808,1,404,301,16,301,'2014-07-25 13:43:52',NULL,1),(809,1,405,300,405,300,'2014-07-25 13:43:52',NULL,1),(810,1,405,301,16,301,'2014-07-25 13:43:52',NULL,1),(811,1,406,300,406,300,'2014-07-25 13:43:52',NULL,1),(812,1,406,301,16,301,'2014-07-25 13:43:52',NULL,1),(813,1,407,300,407,300,'2014-07-25 13:43:52',NULL,1),(814,1,407,301,16,301,'2014-07-25 13:43:52',NULL,1),(815,1,408,300,408,300,'2014-07-25 13:43:52',NULL,1),(816,1,408,301,16,301,'2014-07-25 13:43:52',NULL,1),(817,1,409,300,409,300,'2014-07-25 13:43:52',NULL,1),(818,1,409,301,16,301,'2014-07-25 13:43:52',NULL,1),(819,1,410,300,410,300,'2014-07-25 13:43:52',NULL,1),(820,1,410,301,6,301,'2014-07-25 13:43:52',NULL,1),(821,1,411,300,411,300,'2014-07-25 13:43:52',NULL,1),(822,1,411,301,16,301,'2014-07-25 13:43:52',NULL,1),(823,1,412,300,412,300,'2014-07-25 13:43:52',NULL,1),(824,1,412,301,16,301,'2014-07-25 13:43:52',NULL,1),(825,1,413,300,413,300,'2014-07-25 13:43:52',NULL,1),(826,1,413,301,16,301,'2014-07-25 13:43:52',NULL,1),(827,1,414,300,414,300,'2014-07-25 13:43:52',NULL,1),(828,1,414,301,11,301,'2014-07-25 13:43:52',NULL,1),(829,1,415,300,415,300,'2014-07-25 13:43:52',NULL,1),(830,1,415,301,16,301,'2014-07-25 13:43:52',NULL,1),(831,1,416,300,416,300,'2014-07-25 13:43:52',NULL,1),(832,1,416,301,6,301,'2014-07-25 13:43:52',NULL,1),(833,1,417,300,417,300,'2014-07-25 13:43:52',NULL,1),(834,1,417,301,6,301,'2014-07-25 13:43:52',NULL,1),(835,1,418,300,418,300,'2014-07-25 13:43:52',NULL,1),(836,1,418,301,16,301,'2014-07-25 13:43:52',NULL,1),(837,1,419,300,419,300,'2014-07-25 13:43:52',NULL,1),(838,1,419,301,16,301,'2014-07-25 13:43:52',NULL,1),(839,1,420,300,420,300,'2014-07-25 13:43:52',NULL,1),(840,1,420,301,16,301,'2014-07-25 13:43:52',NULL,1),(841,1,421,300,421,300,'2014-07-25 13:43:52',NULL,1),(842,1,421,301,16,301,'2014-07-25 13:43:52',NULL,1),(843,1,422,300,422,300,'2014-07-25 13:43:52',NULL,1),(844,1,422,301,16,301,'2014-07-25 13:43:52',NULL,1),(845,1,423,300,423,300,'2014-07-25 13:43:52',NULL,1),(846,1,423,301,16,301,'2014-07-25 13:43:52',NULL,1),(847,1,424,300,424,300,'2014-07-25 13:43:52',NULL,1),(848,1,424,301,16,301,'2014-07-25 13:43:52',NULL,1),(849,1,425,300,425,300,'2014-07-25 13:43:52',NULL,1),(850,1,425,301,17,301,'2014-07-25 13:43:52',NULL,1),(851,1,426,300,426,300,'2014-07-25 13:43:52',NULL,1),(852,1,426,301,16,301,'2014-07-25 13:43:52',NULL,1),(853,1,427,300,427,300,'2014-07-25 13:43:52',NULL,1),(854,1,427,301,6,301,'2014-07-25 13:43:52',NULL,1),(855,1,428,300,428,300,'2014-07-25 13:43:52',NULL,1),(856,1,428,301,16,301,'2014-07-25 13:43:52',NULL,1),(857,1,429,300,429,300,'2014-07-25 13:43:52',NULL,1),(858,1,429,301,16,301,'2014-07-25 13:43:52',NULL,1),(859,1,430,300,430,300,'2014-07-25 13:43:52',NULL,1),(860,1,430,301,1,301,'2014-07-25 13:43:52',NULL,1),(861,1,431,300,431,300,'2014-07-25 13:43:52',NULL,1),(862,1,431,301,16,301,'2014-07-25 13:43:52',NULL,1),(863,1,432,300,432,300,'2014-07-25 13:43:52',NULL,1),(864,1,432,301,6,301,'2014-07-25 13:43:52',NULL,1),(865,1,433,300,433,300,'2014-07-25 13:43:52',NULL,1),(866,1,433,301,16,301,'2014-07-25 13:43:52',NULL,1),(867,1,434,300,434,300,'2014-07-25 13:43:52',NULL,1),(868,1,434,301,16,301,'2014-07-25 13:43:52',NULL,1),(869,1,435,300,435,300,'2014-07-25 13:43:52',NULL,1),(870,1,435,301,16,301,'2014-07-25 13:43:52',NULL,1),(871,1,436,300,436,300,'2014-07-25 13:43:52',NULL,1),(872,1,436,301,16,301,'2014-07-25 13:43:52',NULL,1),(873,1,437,300,437,300,'2014-07-25 13:43:52',NULL,1),(874,1,437,301,2,301,'2014-07-25 13:43:52',NULL,1),(875,1,438,300,438,300,'2014-07-25 13:43:52',NULL,1),(876,1,438,301,6,301,'2014-07-25 13:43:52',NULL,1),(877,1,439,300,439,300,'2014-07-25 13:43:52',NULL,1),(878,1,439,301,16,301,'2014-07-25 13:43:52',NULL,1),(879,1,440,300,440,300,'2014-07-25 13:43:52',NULL,1),(880,1,440,301,16,301,'2014-07-25 13:43:52',NULL,1),(881,1,441,300,441,300,'2014-07-25 13:43:52',NULL,1),(882,1,441,301,16,301,'2014-07-25 13:43:52',NULL,1),(883,1,442,300,442,300,'2014-07-25 13:43:52',NULL,1),(884,1,442,301,16,301,'2014-07-25 13:43:52',NULL,1),(885,1,443,300,443,300,'2014-07-25 13:43:52',NULL,1),(886,1,443,301,11,301,'2014-07-25 13:43:52',NULL,1),(887,1,444,300,444,300,'2014-07-25 13:43:52',NULL,1),(888,1,444,301,16,301,'2014-07-25 13:43:52',NULL,1),(889,1,445,300,445,300,'2014-07-25 13:43:52',NULL,1),(890,1,445,301,16,301,'2014-07-25 13:43:52',NULL,1),(891,1,446,300,446,300,'2014-07-25 13:43:52',NULL,1),(892,1,446,301,16,301,'2014-07-25 13:43:52',NULL,1),(893,1,447,300,447,300,'2014-07-25 13:43:52',NULL,1),(894,1,447,301,16,301,'2014-07-25 13:43:52',NULL,1),(895,1,448,300,448,300,'2014-07-25 13:43:52',NULL,1),(896,1,448,301,6,301,'2014-07-25 13:43:52',NULL,1),(897,1,449,300,449,300,'2014-07-25 13:43:52',NULL,1),(898,1,449,301,16,301,'2014-07-25 13:43:52',NULL,1),(899,1,450,300,450,300,'2014-07-25 13:43:52',NULL,1),(900,1,450,301,16,301,'2014-07-25 13:43:52',NULL,1),(901,1,451,300,451,300,'2014-07-25 13:43:52',NULL,1),(902,1,451,301,16,301,'2014-07-25 13:43:52',NULL,1),(903,1,452,300,452,300,'2014-07-25 13:43:52',NULL,1),(904,1,452,301,16,301,'2014-07-25 13:43:52',NULL,1),(905,1,453,300,453,300,'2014-07-25 13:43:52',NULL,1),(906,1,453,301,16,301,'2014-07-25 13:43:52',NULL,1),(907,1,454,300,454,300,'2014-07-25 13:43:52',NULL,1),(908,1,454,301,10,301,'2014-07-25 13:43:52',NULL,1),(909,1,455,300,455,300,'2014-07-25 13:43:52',NULL,1),(910,1,455,301,16,301,'2014-07-25 13:43:52',NULL,1),(911,1,456,300,456,300,'2014-07-25 13:43:52',NULL,1),(912,1,456,301,16,301,'2014-07-25 13:43:52',NULL,1),(913,1,457,300,457,300,'2014-07-25 13:43:52',NULL,1),(914,1,457,301,16,301,'2014-07-25 13:43:52',NULL,1),(915,1,458,300,458,300,'2014-07-25 13:43:52',NULL,1),(916,1,458,301,1,301,'2014-07-25 13:43:52',NULL,1),(917,1,459,300,459,300,'2014-07-25 13:43:52',NULL,1),(918,1,459,301,16,301,'2014-07-25 13:43:52',NULL,1),(919,1,460,300,460,300,'2014-07-25 13:43:52',NULL,1),(920,1,460,301,16,301,'2014-07-25 13:43:52',NULL,1),(921,1,461,300,461,300,'2014-07-25 13:43:52',NULL,1),(922,1,461,301,16,301,'2014-07-25 13:43:52',NULL,1),(923,1,462,300,462,300,'2014-07-25 13:43:52',NULL,1),(924,1,462,301,16,301,'2014-07-25 13:43:52',NULL,1),(925,1,463,300,463,300,'2014-07-25 13:43:52',NULL,1),(926,1,463,301,6,301,'2014-07-25 13:43:52',NULL,1),(927,1,464,300,464,300,'2014-07-25 13:43:52',NULL,1),(928,1,464,301,16,301,'2014-07-25 13:43:52',NULL,1),(929,1,465,300,465,300,'2014-07-25 13:43:52',NULL,1),(930,1,465,301,13,301,'2014-07-25 13:43:52',NULL,1),(931,1,466,300,466,300,'2014-07-25 13:43:52',NULL,1),(932,1,466,301,16,301,'2014-07-25 13:43:52',NULL,1),(933,1,467,300,467,300,'2014-07-25 13:43:52',NULL,1),(934,1,467,301,16,301,'2014-07-25 13:43:52',NULL,1),(935,1,468,300,468,300,'2014-07-25 13:43:52',NULL,1),(936,1,468,301,16,301,'2014-07-25 13:43:52',NULL,1),(937,1,469,300,469,300,'2014-07-25 13:43:52',NULL,1),(938,1,469,301,16,301,'2014-07-25 13:43:52',NULL,1),(939,1,470,300,470,300,'2014-07-25 13:43:52',NULL,1),(940,1,470,301,16,301,'2014-07-25 13:43:52',NULL,1),(941,1,471,300,471,300,'2014-07-25 13:43:52',NULL,1),(942,1,471,301,16,301,'2014-07-25 13:43:52',NULL,1),(943,1,472,300,472,300,'2014-07-25 13:43:52',NULL,1),(944,1,472,301,16,301,'2014-07-25 13:43:52',NULL,1),(945,1,473,300,473,300,'2014-07-25 13:43:52',NULL,1),(946,1,473,301,6,301,'2014-07-25 13:43:52',NULL,1),(947,1,474,300,474,300,'2014-07-25 13:43:52',NULL,1),(948,1,474,301,16,301,'2014-07-25 13:43:52',NULL,1),(949,1,475,300,475,300,'2014-07-25 13:43:52',NULL,1),(950,1,475,301,16,301,'2014-07-25 13:43:52',NULL,1),(951,1,476,300,476,300,'2014-07-25 13:43:52',NULL,1),(952,1,476,301,16,301,'2014-07-25 13:43:52',NULL,1),(953,1,477,300,477,300,'2014-07-25 13:43:52',NULL,1),(954,1,477,301,6,301,'2014-07-25 13:43:52',NULL,1),(955,1,478,300,478,300,'2014-07-25 13:43:52',NULL,1),(956,1,478,301,16,301,'2014-07-25 13:43:52',NULL,1),(957,1,479,300,479,300,'2014-07-25 13:43:52',NULL,1),(958,1,479,301,16,301,'2014-07-25 13:43:52',NULL,1),(959,1,480,300,480,300,'2014-07-25 13:43:52',NULL,1),(960,1,480,301,16,301,'2014-07-25 13:43:52',NULL,1),(961,1,481,300,481,300,'2014-07-25 13:43:52',NULL,1),(962,1,481,301,16,301,'2014-07-25 13:43:52',NULL,1),(963,1,482,300,482,300,'2014-07-25 13:43:52',NULL,1),(964,1,482,301,11,301,'2014-07-25 13:43:52',NULL,1),(965,1,483,300,483,300,'2014-07-25 13:43:52',NULL,1),(966,1,483,301,16,301,'2014-07-25 13:43:52',NULL,1),(967,1,484,300,484,300,'2014-07-25 13:43:52',NULL,1),(968,1,484,301,6,301,'2014-07-25 13:43:52',NULL,1),(969,1,485,300,485,300,'2014-07-25 13:43:52',NULL,1),(970,1,485,301,16,301,'2014-07-25 13:43:52',NULL,1),(971,1,486,300,486,300,'2014-07-25 13:43:52',NULL,1),(972,1,486,301,6,301,'2014-07-25 13:43:52',NULL,1),(973,1,487,300,487,300,'2014-07-25 13:43:52',NULL,1),(974,1,487,301,16,301,'2014-07-25 13:43:52',NULL,1),(975,1,488,300,488,300,'2014-07-25 13:43:52',NULL,1),(976,1,488,301,16,301,'2014-07-25 13:43:52',NULL,1),(977,1,489,300,489,300,'2014-07-25 13:43:52',NULL,1),(978,1,489,301,6,301,'2014-07-25 13:43:52',NULL,1),(979,1,490,300,490,300,'2014-07-25 13:43:52',NULL,1),(980,1,490,301,16,301,'2014-07-25 13:43:52',NULL,1),(981,1,491,300,491,300,'2014-07-25 13:43:52',NULL,1),(982,1,491,301,6,301,'2014-07-25 13:43:52',NULL,1),(983,1,492,300,492,300,'2014-07-25 13:43:52',NULL,1),(984,1,492,301,6,301,'2014-07-25 13:43:52',NULL,1),(985,1,493,300,493,300,'2014-07-25 13:43:52',NULL,1),(986,1,493,301,16,301,'2014-07-25 13:43:52',NULL,1),(987,1,494,300,494,300,'2014-07-25 13:43:52',NULL,1),(988,1,494,301,16,301,'2014-07-25 13:43:52',NULL,1),(989,1,495,300,495,300,'2014-07-25 13:43:52',NULL,1),(990,1,495,301,16,301,'2014-07-25 13:43:52',NULL,1),(991,1,496,300,496,300,'2014-07-25 13:43:52',NULL,1),(992,1,496,301,16,301,'2014-07-25 13:43:52',NULL,1),(993,1,497,300,497,300,'2014-07-25 13:43:52',NULL,1),(994,1,497,301,6,301,'2014-07-25 13:43:52',NULL,1),(995,1,498,300,498,300,'2014-07-25 13:43:52',NULL,1),(996,1,498,301,10,301,'2014-07-25 13:43:52',NULL,1),(997,1,499,300,499,300,'2014-07-25 13:43:52',NULL,1),(998,1,499,301,6,301,'2014-07-25 13:43:52',NULL,1),(999,1,500,300,500,300,'2014-07-25 13:43:52',NULL,1),(1000,1,500,301,16,301,'2014-07-25 13:43:52',NULL,1),(1001,1,501,300,501,300,'2014-07-25 13:43:52',NULL,1),(1002,1,501,301,6,301,'2014-07-25 13:43:52',NULL,1),(1003,1,502,300,502,300,'2014-07-25 13:43:52',NULL,1),(1004,1,502,301,6,301,'2014-07-25 13:43:52',NULL,1),(1005,1,503,300,503,300,'2014-07-25 13:43:52',NULL,1),(1006,1,503,301,16,301,'2014-07-25 13:43:52',NULL,1),(1007,1,504,300,504,300,'2014-07-25 13:43:52',NULL,1),(1008,1,504,301,16,301,'2014-07-25 13:43:52',NULL,1),(1009,1,505,300,505,300,'2014-07-25 13:43:52',NULL,1),(1010,1,505,301,7,301,'2014-07-25 13:43:52',NULL,1),(1011,1,506,300,506,300,'2014-07-25 13:43:52',NULL,1),(1012,1,506,301,16,301,'2014-07-25 13:43:52',NULL,1),(1013,1,507,300,507,300,'2014-07-25 13:43:52',NULL,1),(1014,1,507,301,16,301,'2014-07-25 13:43:52',NULL,1),(1015,1,508,300,508,300,'2014-07-25 13:43:52',NULL,1),(1016,1,508,301,16,301,'2014-07-25 13:43:52',NULL,1),(1017,1,509,300,509,300,'2014-07-25 13:43:52',NULL,1),(1018,1,509,301,16,301,'2014-07-25 13:43:52',NULL,1),(1019,1,510,300,510,300,'2014-07-25 13:43:52',NULL,1),(1020,1,510,301,16,301,'2014-07-25 13:43:52',NULL,1),(1021,1,511,300,511,300,'2014-07-25 13:43:52',NULL,1),(1022,1,511,301,6,301,'2014-07-25 13:43:52',NULL,1),(1023,1,512,300,512,300,'2014-07-25 13:43:52',NULL,1),(1024,1,512,301,16,301,'2014-07-25 13:43:52',NULL,1),(1025,1,513,300,513,300,'2014-07-25 13:43:52',NULL,1),(1026,1,513,301,6,301,'2014-07-25 13:43:52',NULL,1),(1027,1,514,300,514,300,'2014-07-25 13:43:52',NULL,1),(1028,1,514,301,16,301,'2014-07-25 13:43:52',NULL,1),(1029,1,515,300,515,300,'2014-07-25 13:43:52',NULL,1),(1030,1,515,301,16,301,'2014-07-25 13:43:52',NULL,1),(1031,1,516,300,516,300,'2014-07-25 13:43:52',NULL,1),(1032,1,516,301,16,301,'2014-07-25 13:43:52',NULL,1),(1033,1,517,300,517,300,'2014-07-25 13:43:52',NULL,1),(1034,1,517,301,6,301,'2014-07-25 13:43:52',NULL,1),(1035,1,518,300,518,300,'2014-07-25 13:43:52',NULL,1),(1036,1,518,301,6,301,'2014-07-25 13:43:52',NULL,1),(1037,1,519,300,519,300,'2014-07-25 13:43:52',NULL,1),(1038,1,519,301,6,301,'2014-07-25 13:43:52',NULL,1),(1039,1,520,300,520,300,'2014-07-25 13:43:52',NULL,1),(1040,1,520,301,16,301,'2014-07-25 13:43:52',NULL,1),(1041,1,521,300,521,300,'2014-07-25 13:43:52',NULL,1),(1042,1,521,301,16,301,'2014-07-25 13:43:52',NULL,1),(1043,1,522,300,522,300,'2014-07-25 13:43:52',NULL,1),(1044,1,522,301,10,301,'2014-07-25 13:43:52',NULL,1),(1045,1,523,300,523,300,'2014-07-25 13:43:52',NULL,1),(1046,1,523,301,9,301,'2014-07-25 13:43:52',NULL,1),(1047,1,524,300,524,300,'2014-07-25 13:43:52',NULL,1),(1048,1,524,301,9,301,'2014-07-25 13:43:52',NULL,1),(1049,1,525,300,525,300,'2014-07-25 13:43:52',NULL,1),(1050,1,525,301,16,301,'2014-07-25 13:43:52',NULL,1),(1051,1,526,300,526,300,'2014-07-25 13:43:52',NULL,1),(1052,1,526,301,16,301,'2014-07-25 13:43:52',NULL,1),(1053,1,527,300,527,300,'2014-07-25 13:43:52',NULL,1),(1054,1,527,301,11,301,'2014-07-25 13:43:52',NULL,1),(1055,1,528,300,528,300,'2014-07-25 13:43:52',NULL,1),(1056,1,528,301,16,301,'2014-07-25 13:43:52',NULL,1),(1057,1,529,300,529,300,'2014-07-25 13:43:52',NULL,1),(1058,1,529,301,16,301,'2014-07-25 13:43:52',NULL,1),(1059,1,530,300,530,300,'2014-07-25 13:43:52',NULL,1),(1060,1,530,301,16,301,'2014-07-25 13:43:52',NULL,1),(1061,1,531,300,531,300,'2014-07-25 13:43:52',NULL,1),(1062,1,531,301,2,301,'2014-07-25 13:43:52',NULL,1),(1063,1,532,300,532,300,'2014-07-25 13:43:52',NULL,1),(1064,1,532,301,6,301,'2014-07-25 13:43:52',NULL,1),(1065,1,533,300,533,300,'2014-07-25 13:43:52',NULL,1),(1066,1,533,301,16,301,'2014-07-25 13:43:52',NULL,1),(1067,1,534,300,534,300,'2014-07-25 13:43:52',NULL,1),(1068,1,534,301,2,301,'2014-07-25 13:43:52',NULL,1),(1069,1,535,300,535,300,'2014-07-25 13:43:52',NULL,1),(1070,1,535,301,16,301,'2014-07-25 13:43:52',NULL,1),(1071,1,536,300,536,300,'2014-07-25 13:43:52',NULL,1),(1072,1,536,301,16,301,'2014-07-25 13:43:52',NULL,1),(1073,1,537,300,537,300,'2014-07-25 13:43:52',NULL,1),(1074,1,537,301,16,301,'2014-07-25 13:43:52',NULL,1),(1075,1,538,300,538,300,'2014-07-25 13:43:52',NULL,1),(1076,1,538,301,16,301,'2014-07-25 13:43:52',NULL,1),(1077,1,539,300,539,300,'2014-07-25 13:43:52',NULL,1),(1078,1,539,301,11,301,'2014-07-25 13:43:52',NULL,1),(1079,1,540,300,540,300,'2014-07-25 13:43:52',NULL,1),(1080,1,540,301,16,301,'2014-07-25 13:43:52',NULL,1),(1081,1,541,300,541,300,'2014-07-25 13:43:52',NULL,1),(1082,1,541,301,6,301,'2014-07-25 13:43:52',NULL,1),(1083,1,542,300,542,300,'2014-07-25 13:43:52',NULL,1),(1084,1,542,301,11,301,'2014-07-25 13:43:52',NULL,1),(1085,1,543,300,543,300,'2014-07-25 13:43:52',NULL,1),(1086,1,543,301,16,301,'2014-07-25 13:43:52',NULL,1),(1087,1,544,300,544,300,'2014-07-25 13:43:52',NULL,1),(1088,1,544,301,6,301,'2014-07-25 13:43:52',NULL,1),(1089,1,545,300,545,300,'2014-07-25 13:43:52',NULL,1),(1090,1,545,301,16,301,'2014-07-25 13:43:52',NULL,1),(1091,1,546,300,546,300,'2014-07-25 13:43:52',NULL,1),(1092,1,546,301,16,301,'2014-07-25 13:43:52',NULL,1),(1093,1,547,300,547,300,'2014-07-25 13:43:52',NULL,1),(1094,1,547,301,6,301,'2014-07-25 13:43:52',NULL,1),(1095,1,548,300,548,300,'2014-07-25 13:43:52',NULL,1),(1096,1,548,301,16,301,'2014-07-25 13:43:52',NULL,1),(1097,1,549,300,549,300,'2014-07-25 13:43:52',NULL,1),(1098,1,549,301,16,301,'2014-07-25 13:43:52',NULL,1),(1099,1,550,300,550,300,'2014-07-25 13:43:52',NULL,1),(1100,1,550,301,6,301,'2014-07-25 13:43:52',NULL,1),(1101,1,551,300,551,300,'2014-07-25 13:43:52',NULL,1),(1102,1,551,301,2,301,'2014-07-25 13:43:52',NULL,1),(1103,1,552,300,552,300,'2014-07-25 13:43:52',NULL,1),(1104,1,552,301,16,301,'2014-07-25 13:43:52',NULL,1),(1105,1,553,300,553,300,'2014-07-25 13:43:52',NULL,1),(1106,1,553,301,16,301,'2014-07-25 13:43:52',NULL,1),(1107,1,554,300,554,300,'2014-07-25 13:43:52',NULL,1),(1108,1,554,301,1,301,'2014-07-25 13:43:52',NULL,1),(1109,1,555,300,555,300,'2014-07-25 13:43:52',NULL,1),(1110,1,555,301,6,301,'2014-07-25 13:43:52',NULL,1),(1111,1,556,300,556,300,'2014-07-25 13:43:52',NULL,1),(1112,1,556,301,16,301,'2014-07-25 13:43:52',NULL,1),(1113,1,557,300,557,300,'2014-07-25 13:43:52',NULL,1),(1114,1,557,301,16,301,'2014-07-25 13:43:52',NULL,1),(1115,1,558,300,558,300,'2014-07-25 13:43:52',NULL,1),(1116,1,558,301,16,301,'2014-07-25 13:43:52',NULL,1),(1117,1,559,300,559,300,'2014-07-25 13:43:52',NULL,1),(1118,1,559,301,2,301,'2014-07-25 13:43:52',NULL,1),(1119,1,560,300,560,300,'2014-07-25 13:43:52',NULL,1),(1120,1,560,301,6,301,'2014-07-25 13:43:52',NULL,1),(1121,1,561,300,561,300,'2014-07-25 13:43:52',NULL,1),(1122,1,561,301,11,301,'2014-07-25 13:43:52',NULL,1),(1123,1,562,300,562,300,'2014-07-25 13:43:52',NULL,1),(1124,1,562,301,11,301,'2014-07-25 13:43:52',NULL,1),(1125,1,563,300,563,300,'2014-07-25 13:43:52',NULL,1),(1126,1,563,301,16,301,'2014-07-25 13:43:52',NULL,1),(1127,1,564,300,564,300,'2014-07-25 13:43:52',NULL,1),(1128,1,564,301,16,301,'2014-07-25 13:43:52',NULL,1),(1129,1,565,300,565,300,'2014-07-25 13:43:52',NULL,1),(1130,1,565,301,16,301,'2014-07-25 13:43:52',NULL,1),(1131,1,566,300,566,300,'2014-07-25 13:43:52',NULL,1),(1132,1,566,301,16,301,'2014-07-25 13:43:52',NULL,1),(1133,1,567,300,567,300,'2014-07-25 13:43:52',NULL,1),(1134,1,567,301,16,301,'2014-07-25 13:43:52',NULL,1),(1135,1,568,300,568,300,'2014-07-25 13:43:52',NULL,1),(1136,1,568,301,16,301,'2014-07-25 13:43:52',NULL,1),(1137,1,569,300,569,300,'2014-07-25 13:43:52',NULL,1),(1138,1,569,301,16,301,'2014-07-25 13:43:52',NULL,1),(1139,1,570,300,570,300,'2014-07-25 13:43:52',NULL,1),(1140,1,570,301,16,301,'2014-07-25 13:43:52',NULL,1),(1141,1,571,300,571,300,'2014-07-25 13:43:52',NULL,1),(1142,1,571,301,16,301,'2014-07-25 13:43:52',NULL,1),(1143,1,572,300,572,300,'2014-07-25 13:43:52',NULL,1),(1144,1,572,301,6,301,'2014-07-25 13:43:52',NULL,1),(1145,1,573,300,573,300,'2014-07-25 13:43:52',NULL,1),(1146,1,573,301,6,301,'2014-07-25 13:43:52',NULL,1),(1147,1,574,300,574,300,'2014-07-25 13:43:52',NULL,1),(1148,1,574,301,16,301,'2014-07-25 13:43:52',NULL,1),(1149,1,575,300,575,300,'2014-07-25 13:43:52',NULL,1),(1150,1,575,301,6,301,'2014-07-25 13:43:52',NULL,1),(1151,1,576,300,576,300,'2014-07-25 13:43:52',NULL,1),(1152,1,576,301,6,301,'2014-07-25 13:43:52',NULL,1),(1153,1,577,300,577,300,'2014-07-25 13:43:52',NULL,1),(1154,1,577,301,6,301,'2014-07-25 13:43:52',NULL,1),(1155,1,578,300,578,300,'2014-07-25 13:43:52',NULL,1),(1156,1,578,301,6,301,'2014-07-25 13:43:52',NULL,1),(1157,1,579,300,579,300,'2014-07-25 13:43:52',NULL,1),(1158,1,579,301,6,301,'2014-07-25 13:43:52',NULL,1),(1159,1,580,300,580,300,'2014-07-25 13:43:52',NULL,1),(1160,1,580,301,16,301,'2014-07-25 13:43:52',NULL,1),(1161,1,581,300,581,300,'2014-07-25 13:43:52',NULL,1),(1162,1,581,301,6,301,'2014-07-25 13:43:52',NULL,1),(1163,1,582,300,582,300,'2014-07-25 13:43:52',NULL,1),(1164,1,582,301,16,301,'2014-07-25 13:43:52',NULL,1),(1165,1,583,300,583,300,'2014-07-25 13:43:52',NULL,1),(1166,1,583,301,7,301,'2014-07-25 13:43:52',NULL,1),(1167,1,584,300,584,300,'2014-07-25 13:43:52',NULL,1),(1168,1,584,301,16,301,'2014-07-25 13:43:52',NULL,1),(1169,1,585,300,585,300,'2014-07-25 13:43:52',NULL,1),(1170,1,585,301,16,301,'2014-07-25 13:43:52',NULL,1),(1171,1,586,300,586,300,'2014-07-25 13:43:52',NULL,1),(1172,1,586,301,16,301,'2014-07-25 13:43:52',NULL,1),(1173,1,587,300,587,300,'2014-07-25 13:43:52',NULL,1),(1174,1,587,301,16,301,'2014-07-25 13:43:52',NULL,1),(1175,1,588,300,588,300,'2014-07-25 13:43:52',NULL,1),(1176,1,588,301,6,301,'2014-07-25 13:43:52',NULL,1),(1177,1,589,300,589,300,'2014-07-25 13:43:52',NULL,1),(1178,1,589,301,16,301,'2014-07-25 13:43:52',NULL,1),(1179,1,590,300,590,300,'2014-07-25 13:43:52',NULL,1),(1180,1,590,301,7,301,'2014-07-25 13:43:52',NULL,1),(1181,1,591,300,591,300,'2014-07-25 13:43:52',NULL,1),(1182,1,591,301,16,301,'2014-07-25 13:43:52',NULL,1),(1183,1,592,300,592,300,'2014-07-25 13:43:52',NULL,1),(1184,1,592,301,6,301,'2014-07-25 13:43:52',NULL,1),(1185,1,593,300,593,300,'2014-07-25 13:43:52',NULL,1),(1186,1,593,301,6,301,'2014-07-25 13:43:52',NULL,1),(1187,1,594,300,594,300,'2014-07-25 13:43:52',NULL,1),(1188,1,594,301,16,301,'2014-07-25 13:43:52',NULL,1),(1189,1,595,300,595,300,'2014-07-25 13:43:52',NULL,1),(1190,1,595,301,16,301,'2014-07-25 13:43:52',NULL,1),(1191,1,596,300,596,300,'2014-07-25 13:43:52',NULL,1),(1192,1,596,301,16,301,'2014-07-25 13:43:52',NULL,1),(1193,1,597,300,597,300,'2014-07-25 13:43:52',NULL,1),(1194,1,597,301,6,301,'2014-07-25 13:43:52',NULL,1),(1195,1,598,300,598,300,'2014-07-25 13:43:52',NULL,1),(1196,1,598,301,16,301,'2014-07-25 13:43:52',NULL,1),(1197,1,599,300,599,300,'2014-07-25 13:43:52',NULL,1),(1198,1,599,301,6,301,'2014-07-25 13:43:52',NULL,1),(1199,1,600,300,600,300,'2014-07-25 13:43:52',NULL,1),(1200,1,600,301,16,301,'2014-07-25 13:43:52',NULL,1),(1201,1,601,300,601,300,'2014-07-25 13:43:52',NULL,1),(1202,1,601,301,16,301,'2014-07-25 13:43:52',NULL,1),(1203,1,602,300,602,300,'2014-07-25 13:43:52',NULL,1),(1204,1,602,301,16,301,'2014-07-25 13:43:52',NULL,1),(1205,1,603,300,603,300,'2014-07-25 13:43:52',NULL,1),(1206,1,603,301,16,301,'2014-07-25 13:43:52',NULL,1),(1207,1,604,300,604,300,'2014-07-25 13:43:52',NULL,1),(1208,1,604,301,16,301,'2014-07-25 13:43:52',NULL,1),(1209,1,605,300,605,300,'2014-07-25 13:43:52',NULL,1),(1210,1,605,301,10,301,'2014-07-25 13:43:52',NULL,1),(1211,1,606,300,606,300,'2014-07-25 13:43:52',NULL,1),(1212,1,606,301,17,301,'2014-07-25 13:43:52',NULL,1),(1213,1,607,300,607,300,'2014-07-25 13:43:52',NULL,1),(1214,1,607,301,2,301,'2014-07-25 13:43:52',NULL,1),(1215,1,608,300,608,300,'2014-07-25 13:43:52',NULL,1),(1216,1,608,301,6,301,'2014-07-25 13:43:52',NULL,1),(1217,1,609,300,609,300,'2014-07-25 13:43:52',NULL,1),(1218,1,609,301,16,301,'2014-07-25 13:43:52',NULL,1),(1219,1,610,300,610,300,'2014-07-25 13:43:52',NULL,1),(1220,1,610,301,6,301,'2014-07-25 13:43:52',NULL,1),(1221,1,611,300,611,300,'2014-07-25 13:43:52',NULL,1),(1222,1,611,301,16,301,'2014-07-25 13:43:52',NULL,1),(1223,1,612,300,612,300,'2014-07-25 13:43:52',NULL,1),(1224,1,612,301,16,301,'2014-07-25 13:43:52',NULL,1),(1225,1,613,300,613,300,'2014-07-25 13:43:52',NULL,1),(1226,1,613,301,16,301,'2014-07-25 13:43:52',NULL,1),(1227,1,614,300,614,300,'2014-07-25 13:43:52',NULL,1),(1228,1,614,301,6,301,'2014-07-25 13:43:52',NULL,1),(1229,1,615,300,615,300,'2014-07-25 13:43:52',NULL,1),(1230,1,615,301,16,301,'2014-07-25 13:43:52',NULL,1),(1231,1,616,300,616,300,'2014-07-25 13:43:52',NULL,1),(1232,1,616,301,2,301,'2014-07-25 13:43:52',NULL,1),(1233,1,617,300,617,300,'2014-07-25 13:43:52',NULL,1),(1234,1,617,301,16,301,'2014-07-25 13:43:52',NULL,1),(1235,1,618,300,618,300,'2014-07-25 13:43:52',NULL,1),(1236,1,618,301,16,301,'2014-07-25 13:43:52',NULL,1),(1237,1,619,300,619,300,'2014-07-25 13:43:52',NULL,1),(1238,1,619,301,16,301,'2014-07-25 13:43:52',NULL,1),(1239,1,620,300,620,300,'2014-07-25 13:43:52',NULL,1),(1240,1,620,301,16,301,'2014-07-25 13:43:52',NULL,1),(1241,1,621,300,621,300,'2014-07-25 13:43:52',NULL,1),(1242,1,621,301,6,301,'2014-07-25 13:43:52',NULL,1),(1243,1,622,300,622,300,'2014-07-25 13:43:52',NULL,1),(1244,1,622,301,16,301,'2014-07-25 13:43:52',NULL,1),(1245,1,623,300,623,300,'2014-07-25 13:43:52',NULL,1),(1246,1,623,301,6,301,'2014-07-25 13:43:52',NULL,1),(1247,1,624,300,624,300,'2014-07-25 13:43:52',NULL,1),(1248,1,624,301,16,301,'2014-07-25 13:43:52',NULL,1),(1249,1,625,300,625,300,'2014-07-25 13:43:52',NULL,1),(1250,1,625,301,2,301,'2014-07-25 13:43:52',NULL,1),(1251,1,626,300,626,300,'2014-07-25 13:43:52',NULL,1),(1252,1,626,301,16,301,'2014-07-25 13:43:52',NULL,1),(1253,1,627,300,627,300,'2014-07-25 13:43:52',NULL,1),(1254,1,627,301,6,301,'2014-07-25 13:43:52',NULL,1),(1255,1,628,300,628,300,'2014-07-25 13:43:52',NULL,1),(1256,1,628,301,16,301,'2014-07-25 13:43:52',NULL,1),(1257,1,629,300,629,300,'2014-07-25 13:43:52',NULL,1),(1258,1,629,301,6,301,'2014-07-25 13:43:52',NULL,1),(1259,1,630,300,630,300,'2014-07-25 13:43:52',NULL,1),(1260,1,630,301,16,301,'2014-07-25 13:43:52',NULL,1),(1261,1,631,300,631,300,'2014-07-25 13:43:52',NULL,1),(1262,1,631,301,16,301,'2014-07-25 13:43:52',NULL,1),(1263,1,632,300,632,300,'2014-07-25 13:43:52',NULL,1),(1264,1,632,301,16,301,'2014-07-25 13:43:52',NULL,1),(1265,1,633,300,633,300,'2014-07-25 13:43:52',NULL,1),(1266,1,633,301,16,301,'2014-07-25 13:43:52',NULL,1),(1267,1,634,300,634,300,'2014-07-25 13:43:52',NULL,1),(1268,1,634,301,6,301,'2014-07-25 13:43:52',NULL,1),(1269,1,635,300,635,300,'2014-07-25 13:43:52',NULL,1),(1270,1,635,301,16,301,'2014-07-25 13:43:52',NULL,1),(1271,1,636,300,636,300,'2014-07-25 13:43:52',NULL,1),(1272,1,636,301,6,301,'2014-07-25 13:43:52',NULL,1),(1273,1,637,300,637,300,'2014-07-25 13:43:52',NULL,1),(1274,1,637,301,6,301,'2014-07-25 13:43:52',NULL,1),(1275,1,638,300,638,300,'2014-07-25 13:43:52',NULL,1),(1276,1,638,301,16,301,'2014-07-25 13:43:52',NULL,1),(1277,1,639,300,639,300,'2014-07-25 13:43:52',NULL,1),(1278,1,639,301,16,301,'2014-07-25 13:43:52',NULL,1),(1279,1,640,300,640,300,'2014-07-25 13:43:52',NULL,1),(1280,1,640,301,16,301,'2014-07-25 13:43:52',NULL,1),(1281,1,641,300,641,300,'2014-07-25 13:43:52',NULL,1),(1282,1,641,301,11,301,'2014-07-25 13:43:52',NULL,1),(1283,1,642,300,642,300,'2014-07-25 13:43:52',NULL,1),(1284,1,642,301,16,301,'2014-07-25 13:43:52',NULL,1),(1285,1,643,300,643,300,'2014-07-25 13:43:52',NULL,1),(1286,1,643,301,6,301,'2014-07-25 13:43:52',NULL,1),(1287,1,644,300,644,300,'2014-07-25 13:43:52',NULL,1),(1288,1,644,301,16,301,'2014-07-25 13:43:52',NULL,1),(1289,1,645,300,645,300,'2014-07-25 13:43:52',NULL,1),(1290,1,645,301,16,301,'2014-07-25 13:43:52',NULL,1),(1291,1,646,300,646,300,'2014-07-25 13:43:52',NULL,1),(1292,1,646,301,16,301,'2014-07-25 13:43:52',NULL,1),(1293,1,647,300,647,300,'2014-07-25 13:43:52',NULL,1),(1294,1,647,301,6,301,'2014-07-25 13:43:52',NULL,1),(1295,1,648,300,648,300,'2014-07-25 13:43:52',NULL,1),(1296,1,648,301,6,301,'2014-07-25 13:43:52',NULL,1),(1297,1,649,300,649,300,'2014-07-25 13:43:52',NULL,1),(1298,1,649,301,2,301,'2014-07-25 13:43:52',NULL,1),(1299,1,650,300,650,300,'2014-07-25 13:43:52',NULL,1),(1300,1,650,301,16,301,'2014-07-25 13:43:52',NULL,1),(1301,1,651,300,651,300,'2014-07-25 13:43:52',NULL,1),(1302,1,651,301,16,301,'2014-07-25 13:43:52',NULL,1),(1303,1,652,300,652,300,'2014-07-25 13:43:52',NULL,1),(1304,1,652,301,16,301,'2014-07-25 13:43:52',NULL,1),(1305,1,653,300,653,300,'2014-07-25 13:43:52',NULL,1),(1306,1,653,301,6,301,'2014-07-25 13:43:52',NULL,1),(1307,1,654,300,654,300,'2014-07-25 13:43:52',NULL,1),(1308,1,654,301,16,301,'2014-07-25 13:43:52',NULL,1),(1309,1,655,300,655,300,'2014-07-25 13:43:52',NULL,1),(1310,1,655,301,6,301,'2014-07-25 13:43:52',NULL,1),(1311,1,656,300,656,300,'2014-07-25 13:43:52',NULL,1),(1312,1,656,301,16,301,'2014-07-25 13:43:52',NULL,1),(1313,1,657,300,657,300,'2014-07-25 13:43:52',NULL,1),(1314,1,657,301,16,301,'2014-07-25 13:43:52',NULL,1),(1315,1,658,300,658,300,'2014-07-25 13:43:52',NULL,1),(1316,1,658,301,16,301,'2014-07-25 13:43:52',NULL,1),(1317,1,659,300,659,300,'2014-07-25 13:43:52',NULL,1),(1318,1,659,301,16,301,'2014-07-25 13:43:52',NULL,1),(1319,1,660,300,660,300,'2014-07-25 13:43:52',NULL,1),(1320,1,660,301,16,301,'2014-07-25 13:43:52',NULL,1),(1321,1,661,300,661,300,'2014-07-25 13:43:52',NULL,1),(1322,1,661,301,16,301,'2014-07-25 13:43:52',NULL,1),(1323,1,662,300,662,300,'2014-07-25 13:43:52',NULL,1),(1324,1,662,301,6,301,'2014-07-25 13:43:52',NULL,1),(1325,1,663,300,663,300,'2014-07-25 13:43:52',NULL,1),(1326,1,663,301,16,301,'2014-07-25 13:43:52',NULL,1),(1327,1,664,300,664,300,'2014-07-25 13:43:52',NULL,1),(1328,1,664,301,16,301,'2014-07-25 13:43:52',NULL,1),(1329,1,665,300,665,300,'2014-07-25 13:43:52',NULL,1),(1330,1,665,301,6,301,'2014-07-25 13:43:52',NULL,1),(1331,1,666,300,666,300,'2014-07-25 13:43:52',NULL,1),(1332,1,666,301,16,301,'2014-07-25 13:43:52',NULL,1),(1333,1,667,300,667,300,'2014-07-25 13:43:52',NULL,1),(1334,1,667,301,16,301,'2014-07-25 13:43:52',NULL,1),(1335,1,668,300,668,300,'2014-07-25 13:43:52',NULL,1),(1336,1,668,301,6,301,'2014-07-25 13:43:52',NULL,1),(1337,1,669,300,669,300,'2014-07-25 13:43:52',NULL,1),(1338,1,669,301,10,301,'2014-07-25 13:43:52',NULL,1),(1339,1,670,300,670,300,'2014-07-25 13:43:52',NULL,1),(1340,1,670,301,16,301,'2014-07-25 13:43:52',NULL,1),(1341,1,671,300,671,300,'2014-07-25 13:43:52',NULL,1),(1342,1,671,301,16,301,'2014-07-25 13:43:52',NULL,1),(1343,1,672,300,672,300,'2014-07-25 13:43:52',NULL,1),(1344,1,672,301,16,301,'2014-07-25 13:43:52',NULL,1),(1345,1,673,300,673,300,'2014-07-25 13:43:52',NULL,1),(1346,1,673,301,16,301,'2014-07-25 13:43:52',NULL,1),(1347,1,674,300,674,300,'2014-07-25 13:43:52',NULL,1),(1348,1,674,301,16,301,'2014-07-25 13:43:52',NULL,1),(1349,1,675,300,675,300,'2014-07-25 13:43:52',NULL,1),(1350,1,675,301,16,301,'2014-07-25 13:43:52',NULL,1),(1351,1,676,300,676,300,'2014-07-25 13:43:52',NULL,1),(1352,1,676,301,16,301,'2014-07-25 13:43:52',NULL,1),(1353,1,677,300,677,300,'2014-07-25 13:43:52',NULL,1),(1354,1,677,301,16,301,'2014-07-25 13:43:52',NULL,1),(1355,1,678,300,678,300,'2014-07-25 13:43:52',NULL,1),(1356,1,678,301,16,301,'2014-07-25 13:43:52',NULL,1),(1357,1,679,300,679,300,'2014-07-25 13:43:52',NULL,1),(1358,1,679,301,16,301,'2014-07-25 13:43:52',NULL,1),(1359,1,680,300,680,300,'2014-07-25 13:43:52',NULL,1),(1360,1,680,301,16,301,'2014-07-25 13:43:52',NULL,1),(1361,1,681,300,681,300,'2014-07-25 13:43:52',NULL,1),(1362,1,681,301,6,301,'2014-07-25 13:43:52',NULL,1),(1363,1,682,300,682,300,'2014-07-25 13:43:52',NULL,1),(1364,1,682,301,16,301,'2014-07-25 13:43:52',NULL,1),(1365,1,683,300,683,300,'2014-07-25 13:43:52',NULL,1),(1366,1,683,301,6,301,'2014-07-25 13:43:52',NULL,1),(1367,1,684,300,684,300,'2014-07-25 13:43:52',NULL,1),(1368,1,684,301,6,301,'2014-07-25 13:43:52',NULL,1),(1369,1,685,300,685,300,'2014-07-25 13:43:52',NULL,1),(1370,1,685,301,16,301,'2014-07-25 13:43:52',NULL,1),(1371,1,686,300,686,300,'2014-07-25 13:43:52',NULL,1),(1372,1,686,301,16,301,'2014-07-25 13:43:52',NULL,1),(1373,1,687,300,687,300,'2014-07-25 13:43:52',NULL,1),(1374,1,687,301,16,301,'2014-07-25 13:43:52',NULL,1),(1375,1,688,300,688,300,'2014-07-25 13:43:52',NULL,1),(1376,1,688,301,16,301,'2014-07-25 13:43:52',NULL,1),(1377,1,689,300,689,300,'2014-07-25 13:43:52',NULL,1),(1378,1,689,301,16,301,'2014-07-25 13:43:52',NULL,1),(1379,1,690,300,690,300,'2014-07-25 13:43:52',NULL,1),(1380,1,690,301,16,301,'2014-07-25 13:43:52',NULL,1),(1381,1,691,300,691,300,'2014-07-25 13:43:52',NULL,1),(1382,1,691,301,16,301,'2014-07-25 13:43:52',NULL,1),(1383,1,692,300,692,300,'2014-07-25 13:43:52',NULL,1),(1384,1,692,301,16,301,'2014-07-25 13:43:52',NULL,1),(1385,1,693,300,693,300,'2014-07-25 13:43:52',NULL,1),(1386,1,693,301,6,301,'2014-07-25 13:43:52',NULL,1),(1387,1,694,300,694,300,'2014-07-25 13:43:52',NULL,1),(1388,1,694,301,6,301,'2014-07-25 13:43:52',NULL,1),(1389,1,695,300,695,300,'2014-07-25 13:43:52',NULL,1),(1390,1,695,301,6,301,'2014-07-25 13:43:52',NULL,1),(1391,1,696,300,696,300,'2014-07-25 13:43:52',NULL,1),(1392,1,696,301,16,301,'2014-07-25 13:43:52',NULL,1),(1393,1,697,300,697,300,'2014-07-25 13:43:52',NULL,1),(1394,1,697,301,16,301,'2014-07-25 13:43:52',NULL,1),(1395,1,698,300,698,300,'2014-07-25 13:43:52',NULL,1),(1396,1,698,301,16,301,'2014-07-25 13:43:52',NULL,1),(1397,1,699,300,699,300,'2014-07-25 13:43:52',NULL,1),(1398,1,699,301,6,301,'2014-07-25 13:43:52',NULL,1),(1399,1,700,300,700,300,'2014-07-25 13:43:52',NULL,1),(1400,1,700,301,2,301,'2014-07-25 13:43:52',NULL,1),(1401,1,701,300,701,300,'2014-07-25 13:43:52',NULL,1),(1402,1,701,301,16,301,'2014-07-25 13:43:52',NULL,1),(1403,1,702,300,702,300,'2014-07-25 13:43:52',NULL,1),(1404,1,702,301,16,301,'2014-07-25 13:43:52',NULL,1),(1405,1,703,300,703,300,'2014-07-25 13:43:52',NULL,1),(1406,1,703,301,16,301,'2014-07-25 13:43:52',NULL,1),(1407,1,704,300,704,300,'2014-07-25 13:43:52',NULL,1),(1408,1,704,301,16,301,'2014-07-25 13:43:52',NULL,1),(1409,1,705,300,705,300,'2014-07-25 13:43:52',NULL,1),(1410,1,705,301,7,301,'2014-07-25 13:43:52',NULL,1),(1411,1,706,300,706,300,'2014-07-25 13:43:52',NULL,1),(1412,1,706,301,6,301,'2014-07-25 13:43:52',NULL,1),(1413,1,707,300,707,300,'2014-07-25 13:43:52',NULL,1),(1414,1,707,301,16,301,'2014-07-25 13:43:52',NULL,1),(1415,1,708,300,708,300,'2014-07-25 13:43:52',NULL,1),(1416,1,708,301,16,301,'2014-07-25 13:43:52',NULL,1),(1417,1,709,300,709,300,'2014-07-25 13:43:52',NULL,1),(1418,1,709,301,16,301,'2014-07-25 13:43:52',NULL,1),(1419,1,710,300,710,300,'2014-07-25 13:43:52',NULL,1),(1420,1,710,301,16,301,'2014-07-25 13:43:52',NULL,1),(1421,1,711,300,711,300,'2014-07-25 13:43:52',NULL,1),(1422,1,711,301,16,301,'2014-07-25 13:43:52',NULL,1),(1423,1,712,300,712,300,'2014-07-25 13:43:52',NULL,1),(1424,1,712,301,6,301,'2014-07-25 13:43:52',NULL,1),(1425,1,713,300,713,300,'2014-07-25 13:43:52',NULL,1),(1426,1,713,301,6,301,'2014-07-25 13:43:52',NULL,1),(1427,1,714,300,714,300,'2014-07-25 13:43:52',NULL,1),(1428,1,714,301,6,301,'2014-07-25 13:43:52',NULL,1),(1429,1,715,300,715,300,'2014-07-25 13:43:52',NULL,1),(1430,1,715,301,16,301,'2014-07-25 13:43:52',NULL,1),(1431,1,716,300,716,300,'2014-07-25 13:43:52',NULL,1),(1432,1,716,301,6,301,'2014-07-25 13:43:52',NULL,1),(1433,1,717,300,717,300,'2014-07-25 13:43:52',NULL,1),(1434,1,717,301,16,301,'2014-07-25 13:43:52',NULL,1),(1435,1,718,300,718,300,'2014-07-25 13:43:52',NULL,1),(1436,1,718,301,2,301,'2014-07-25 13:43:52',NULL,1),(1437,1,719,300,719,300,'2014-07-25 13:43:52',NULL,1),(1438,1,719,301,11,301,'2014-07-25 13:43:52',NULL,1),(1439,1,720,300,720,300,'2014-07-25 13:43:52',NULL,1),(1440,1,720,301,16,301,'2014-07-25 13:43:52',NULL,1),(1441,1,721,300,721,300,'2014-07-25 13:43:52',NULL,1),(1442,1,721,301,16,301,'2014-07-25 13:43:52',NULL,1),(1443,1,722,300,722,300,'2014-07-25 13:43:52',NULL,1),(1444,1,722,301,2,301,'2014-07-25 13:43:52',NULL,1),(1445,1,723,300,723,300,'2014-07-25 13:43:52',NULL,1),(1446,1,723,301,6,301,'2014-07-25 13:43:52',NULL,1),(1447,1,724,300,724,300,'2014-07-25 13:43:52',NULL,1),(1448,1,724,301,16,301,'2014-07-25 13:43:52',NULL,1),(1449,1,725,300,725,300,'2014-07-25 13:43:52',NULL,1),(1450,1,725,301,16,301,'2014-07-25 13:43:52',NULL,1),(1451,1,726,300,726,300,'2014-07-25 13:43:52',NULL,1),(1452,1,726,301,16,301,'2014-07-25 13:43:52',NULL,1),(1453,1,727,300,727,300,'2014-07-25 13:43:52',NULL,1),(1454,1,727,301,6,301,'2014-07-25 13:43:52',NULL,1),(1455,1,728,300,728,300,'2014-07-25 13:43:52',NULL,1),(1456,1,728,301,16,301,'2014-07-25 13:43:52',NULL,1),(1457,1,729,300,729,300,'2014-07-25 13:43:52',NULL,1),(1458,1,729,301,16,301,'2014-07-25 13:43:52',NULL,1),(1459,1,730,300,730,300,'2014-07-25 13:43:52',NULL,1),(1460,1,730,301,16,301,'2014-07-25 13:43:52',NULL,1),(1461,1,731,300,731,300,'2014-07-25 13:43:52',NULL,1),(1462,1,731,301,16,301,'2014-07-25 13:43:52',NULL,1),(1463,1,732,300,732,300,'2014-07-25 13:43:52',NULL,1),(1464,1,732,301,16,301,'2014-07-25 13:43:52',NULL,1),(1465,1,733,300,733,300,'2014-07-25 13:43:52',NULL,1),(1466,1,733,301,16,301,'2014-07-25 13:43:52',NULL,1),(1467,1,734,300,734,300,'2014-07-25 13:43:52',NULL,1),(1468,1,734,301,16,301,'2014-07-25 13:43:52',NULL,1),(1469,1,735,300,735,300,'2014-07-25 13:43:52',NULL,1),(1470,1,735,301,16,301,'2014-07-25 13:43:52',NULL,1),(1471,1,736,300,736,300,'2014-07-25 13:43:52',NULL,1),(1472,1,736,301,16,301,'2014-07-25 13:43:52',NULL,1),(1473,1,737,300,737,300,'2014-07-25 13:43:52',NULL,1),(1474,1,737,301,6,301,'2014-07-25 13:43:52',NULL,1),(1475,1,738,300,738,300,'2014-07-25 13:43:52',NULL,1),(1476,1,738,301,6,301,'2014-07-25 13:43:52',NULL,1),(1477,1,739,300,739,300,'2014-07-25 13:43:52',NULL,1),(1478,1,739,301,16,301,'2014-07-25 13:43:52',NULL,1),(1479,1,740,300,740,300,'2014-07-25 13:43:52',NULL,1),(1480,1,740,301,6,301,'2014-07-25 13:43:52',NULL,1),(1481,1,741,300,741,300,'2014-07-25 13:43:52',NULL,1),(1482,1,741,301,2,301,'2014-07-25 13:43:52',NULL,1),(1483,1,742,300,742,300,'2014-07-25 13:43:52',NULL,1),(1484,1,742,301,16,301,'2014-07-25 13:43:52',NULL,1),(1485,1,743,300,743,300,'2014-07-25 13:43:52',NULL,1),(1486,1,743,301,16,301,'2014-07-25 13:43:52',NULL,1),(1487,1,744,300,744,300,'2014-07-25 13:43:52',NULL,1),(1488,1,744,301,16,301,'2014-07-25 13:43:52',NULL,1),(1489,1,745,300,745,300,'2014-07-25 13:43:52',NULL,1),(1490,1,745,301,6,301,'2014-07-25 13:43:52',NULL,1),(1491,1,746,300,746,300,'2014-07-25 13:43:52',NULL,1),(1492,1,746,301,16,301,'2014-07-25 13:43:52',NULL,1),(1493,1,747,300,747,300,'2014-07-25 13:43:52',NULL,1),(1494,1,747,301,16,301,'2014-07-25 13:43:52',NULL,1),(1495,1,748,300,748,300,'2014-07-25 13:43:52',NULL,1),(1496,1,748,301,7,301,'2014-07-25 13:43:52',NULL,1),(1497,1,749,300,749,300,'2014-07-25 13:43:52',NULL,1),(1498,1,749,301,16,301,'2014-07-25 13:43:52',NULL,1),(1499,1,750,300,750,300,'2014-07-25 13:43:52',NULL,1),(1500,1,750,301,16,301,'2014-07-25 13:43:52',NULL,1),(1501,1,751,300,751,300,'2014-07-25 13:43:52',NULL,1),(1502,1,751,301,16,301,'2014-07-25 13:43:52',NULL,1),(1503,1,752,300,752,300,'2014-07-25 13:43:52',NULL,1),(1504,1,752,301,2,301,'2014-07-25 13:43:52',NULL,1),(1505,1,753,300,753,300,'2014-07-25 13:43:52',NULL,1),(1506,1,753,301,16,301,'2014-07-25 13:43:52',NULL,1),(1507,1,754,300,754,300,'2014-07-25 13:43:52',NULL,1),(1508,1,754,301,16,301,'2014-07-25 13:43:52',NULL,1),(1509,1,755,300,755,300,'2014-07-25 13:43:52',NULL,1),(1510,1,755,301,16,301,'2014-07-25 13:43:52',NULL,1),(1511,1,756,300,756,300,'2014-07-25 13:43:52',NULL,1),(1512,1,756,301,16,301,'2014-07-25 13:43:52',NULL,1),(1513,1,757,300,757,300,'2014-07-25 13:43:52',NULL,1),(1514,1,757,301,16,301,'2014-07-25 13:43:52',NULL,1),(1515,1,758,300,758,300,'2014-07-25 13:43:52',NULL,1),(1516,1,758,301,16,301,'2014-07-25 13:43:52',NULL,1),(1517,1,759,300,759,300,'2014-07-25 13:43:52',NULL,1),(1518,1,759,301,6,301,'2014-07-25 13:43:52',NULL,1),(1519,1,760,300,760,300,'2014-07-25 13:43:52',NULL,1),(1520,1,760,301,11,301,'2014-07-25 13:43:52',NULL,1),(1521,1,761,300,761,300,'2014-07-25 13:43:52',NULL,1),(1522,1,761,301,16,301,'2014-07-25 13:43:52',NULL,1),(1523,1,762,300,762,300,'2014-07-25 13:43:52',NULL,1),(1524,1,762,301,16,301,'2014-07-25 13:43:52',NULL,1),(1525,1,763,300,763,300,'2014-07-25 13:43:52',NULL,1),(1526,1,763,301,16,301,'2014-07-25 13:43:52',NULL,1),(1527,1,764,300,764,300,'2014-07-25 13:43:52',NULL,1),(1528,1,764,301,5,301,'2014-07-25 13:43:52',NULL,1),(1529,1,765,300,765,300,'2014-07-25 13:43:52',NULL,1),(1530,1,765,301,16,301,'2014-07-25 13:43:52',NULL,1),(1531,1,766,300,766,300,'2014-07-25 13:43:52',NULL,1),(1532,1,766,301,6,301,'2014-07-25 13:43:52',NULL,1),(1533,1,767,300,767,300,'2014-07-25 13:43:52',NULL,1),(1534,1,767,301,16,301,'2014-07-25 13:43:52',NULL,1),(1535,1,768,300,768,300,'2014-07-25 13:43:52',NULL,1),(1536,1,768,301,16,301,'2014-07-25 13:43:52',NULL,1),(1537,1,769,300,769,300,'2014-07-25 13:43:52',NULL,1),(1538,1,769,301,16,301,'2014-07-25 13:43:52',NULL,1),(1539,1,770,300,770,300,'2014-07-25 13:43:52',NULL,1),(1540,1,770,301,16,301,'2014-07-25 13:43:52',NULL,1),(1541,1,771,300,771,300,'2014-07-25 13:43:52',NULL,1),(1542,1,771,301,16,301,'2014-07-25 13:43:52',NULL,1),(1543,1,772,300,772,300,'2014-07-25 13:43:52',NULL,1),(1544,1,772,301,6,301,'2014-07-25 13:43:52',NULL,1),(1545,1,773,300,773,300,'2014-07-25 13:43:52',NULL,1),(1546,1,773,301,16,301,'2014-07-25 13:43:52',NULL,1),(1547,1,774,300,774,300,'2014-07-25 13:43:52',NULL,1),(1548,1,774,301,6,301,'2014-07-25 13:43:52',NULL,1),(1549,1,775,300,775,300,'2014-07-25 13:43:52',NULL,1),(1550,1,775,301,16,301,'2014-07-25 13:43:52',NULL,1),(1551,1,776,300,776,300,'2014-07-25 13:43:52',NULL,1),(1552,1,776,301,16,301,'2014-07-25 13:43:52',NULL,1),(1553,1,777,300,777,300,'2014-07-25 13:43:52',NULL,1),(1554,1,777,301,2,301,'2014-07-25 13:43:52',NULL,1),(1555,1,778,300,778,300,'2014-07-25 13:43:52',NULL,1),(1556,1,778,301,2,301,'2014-07-25 13:43:52',NULL,1),(1557,1,779,300,779,300,'2014-07-25 13:43:52',NULL,1),(1558,1,779,301,16,301,'2014-07-25 13:43:52',NULL,1),(1559,1,780,300,780,300,'2014-07-25 13:43:52',NULL,1),(1560,1,780,301,6,301,'2014-07-25 13:43:52',NULL,1),(1561,1,781,300,781,300,'2014-07-25 13:43:52',NULL,1),(1562,1,781,301,16,301,'2014-07-25 13:43:52',NULL,1),(1563,1,782,300,782,300,'2014-07-25 13:43:52',NULL,1),(1564,1,782,301,16,301,'2014-07-25 13:43:52',NULL,1),(1565,1,783,300,783,300,'2014-07-25 13:43:52',NULL,1),(1566,1,783,301,16,301,'2014-07-25 13:43:52',NULL,1),(1567,1,784,300,784,300,'2014-07-25 13:43:52',NULL,1),(1568,1,784,301,6,301,'2014-07-25 13:43:52',NULL,1),(1569,1,785,300,785,300,'2014-07-25 13:43:52',NULL,1),(1570,1,785,301,16,301,'2014-07-25 13:43:52',NULL,1),(1571,1,786,300,786,300,'2014-07-25 13:43:52',NULL,1),(1572,1,786,301,16,301,'2014-07-25 13:43:52',NULL,1),(1573,1,787,300,787,300,'2014-07-25 13:43:52',NULL,1),(1574,1,787,301,16,301,'2014-07-25 13:43:52',NULL,1),(1575,1,788,300,788,300,'2014-07-25 13:43:52',NULL,1),(1576,1,788,301,16,301,'2014-07-25 13:43:52',NULL,1),(1577,1,789,300,789,300,'2014-07-25 13:43:52',NULL,1),(1578,1,789,301,6,301,'2014-07-25 13:43:52',NULL,1),(1579,1,790,300,790,300,'2014-07-25 13:43:52',NULL,1),(1580,1,790,301,16,301,'2014-07-25 13:43:52',NULL,1),(1581,1,791,300,791,300,'2014-07-25 13:43:52',NULL,1),(1582,1,791,301,16,301,'2014-07-25 13:43:52',NULL,1),(1583,1,792,300,792,300,'2014-07-25 13:43:52',NULL,1),(1584,1,792,301,16,301,'2014-07-25 13:43:52',NULL,1),(1585,1,793,300,793,300,'2014-07-25 13:43:52',NULL,1),(1586,1,793,301,16,301,'2014-07-25 13:43:52',NULL,1),(1587,1,794,300,794,300,'2014-07-25 13:43:52',NULL,1),(1588,1,794,301,16,301,'2014-07-25 13:43:52',NULL,1),(1589,1,795,300,795,300,'2014-07-25 13:43:52',NULL,1),(1590,1,795,301,16,301,'2014-07-25 13:43:52',NULL,1),(1591,1,796,300,796,300,'2014-07-25 13:43:52',NULL,1),(1592,1,796,301,2,301,'2014-07-25 13:43:52',NULL,1),(1593,1,797,300,797,300,'2014-07-25 13:43:52',NULL,1),(1594,1,797,301,6,301,'2014-07-25 13:43:52',NULL,1),(1595,1,798,300,798,300,'2014-07-25 13:43:52',NULL,1),(1596,1,798,301,16,301,'2014-07-25 13:43:52',NULL,1),(1597,1,799,300,799,300,'2014-07-25 13:43:52',NULL,1),(1598,1,799,301,16,301,'2014-07-25 13:43:52',NULL,1),(1599,1,800,300,800,300,'2014-07-25 13:43:52',NULL,1),(1600,1,800,301,16,301,'2014-07-25 13:43:52',NULL,1),(1601,1,801,300,801,300,'2014-07-25 13:43:52',NULL,1),(1602,1,801,301,6,301,'2014-07-25 13:43:52',NULL,1),(1603,1,802,300,802,300,'2014-07-25 13:43:52',NULL,1),(1604,1,802,301,6,301,'2014-07-25 13:43:52',NULL,1),(1605,1,803,300,803,300,'2014-07-25 13:43:52',NULL,1),(1606,1,803,301,16,301,'2014-07-25 13:43:52',NULL,1),(1607,1,804,300,804,300,'2014-07-25 13:43:52',NULL,1),(1608,1,804,301,16,301,'2014-07-25 13:43:52',NULL,1),(1609,1,805,300,805,300,'2014-07-25 13:43:52',NULL,1),(1610,1,805,301,16,301,'2014-07-25 13:43:52',NULL,1),(1611,1,806,300,806,300,'2014-07-25 13:43:52',NULL,1),(1612,1,806,301,16,301,'2014-07-25 13:43:52',NULL,1),(1613,1,807,300,807,300,'2014-07-25 13:43:52',NULL,1),(1614,1,807,301,16,301,'2014-07-25 13:43:52',NULL,1),(1615,1,808,300,808,300,'2014-07-25 13:43:52',NULL,1),(1616,1,808,301,16,301,'2014-07-25 13:43:52',NULL,1),(1617,1,809,300,809,300,'2014-07-25 13:43:52',NULL,1),(1618,1,809,301,16,301,'2014-07-25 13:43:52',NULL,1),(1619,1,810,300,810,300,'2014-07-25 13:43:52',NULL,1),(1620,1,810,301,16,301,'2014-07-25 13:43:52',NULL,1),(1621,1,811,300,811,300,'2014-07-25 13:43:52',NULL,1),(1622,1,811,301,16,301,'2014-07-25 13:43:52',NULL,1),(1623,1,812,300,812,300,'2014-07-25 13:43:52',NULL,1),(1624,1,812,301,16,301,'2014-07-25 13:43:52',NULL,1),(1625,1,813,300,813,300,'2014-07-25 13:43:52',NULL,1),(1626,1,813,301,10,301,'2014-07-25 13:43:52',NULL,1),(1627,1,814,300,814,300,'2014-07-25 13:43:52',NULL,1),(1628,1,814,301,6,301,'2014-07-25 13:43:52',NULL,1),(1629,1,815,300,815,300,'2014-07-25 13:43:52',NULL,1),(1630,1,815,301,6,301,'2014-07-25 13:43:52',NULL,1),(1631,1,816,300,816,300,'2014-07-25 13:43:52',NULL,1),(1632,1,816,301,6,301,'2014-07-25 13:43:52',NULL,1),(1633,1,817,300,817,300,'2014-07-25 13:43:52',NULL,1),(1634,1,817,301,16,301,'2014-07-25 13:43:52',NULL,1),(1635,1,818,300,818,300,'2014-07-25 13:43:52',NULL,1),(1636,1,818,301,16,301,'2014-07-25 13:43:52',NULL,1),(1637,1,819,300,819,300,'2014-07-25 13:43:52',NULL,1),(1638,1,819,301,16,301,'2014-07-25 13:43:52',NULL,1),(1639,1,820,300,820,300,'2014-07-25 13:43:52',NULL,1),(1640,1,820,301,16,301,'2014-07-25 13:43:52',NULL,1),(1641,1,821,300,821,300,'2014-07-25 13:43:52',NULL,1),(1642,1,821,301,16,301,'2014-07-25 13:43:52',NULL,1),(1643,1,822,300,822,300,'2014-07-25 13:43:52',NULL,1),(1644,1,822,301,16,301,'2014-07-25 13:43:52',NULL,1),(1645,1,823,300,823,300,'2014-07-25 13:43:52',NULL,1),(1646,1,823,301,1,301,'2014-07-25 13:43:52',NULL,1),(1647,1,824,300,824,300,'2014-07-25 13:43:52',NULL,1),(1648,1,824,301,16,301,'2014-07-25 13:43:52',NULL,1),(1649,1,825,300,825,300,'2014-07-25 13:43:52',NULL,1),(1650,1,825,301,6,301,'2014-07-25 13:43:52',NULL,1),(1651,1,826,300,826,300,'2014-07-25 13:43:52',NULL,1),(1652,1,826,301,16,301,'2014-07-25 13:43:52',NULL,1),(1653,1,827,300,827,300,'2014-07-25 13:43:52',NULL,1),(1654,1,827,301,6,301,'2014-07-25 13:43:52',NULL,1),(1655,1,828,300,828,300,'2014-07-25 13:43:52',NULL,1),(1656,1,828,301,16,301,'2014-07-25 13:43:52',NULL,1);
/*!40000 ALTER TABLE `street_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `street_correction`
--

DROP TABLE IF EXISTS `street_correction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `street_correction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `city_object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта населенного пункта',
  `street_type_object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта типа улицы',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта улицы',
  `external_id` varchar(20) DEFAULT NULL COMMENT 'Внешний идентификатор объекта',
  `correction` varchar(100) NOT NULL COMMENT 'Название типа населенного пункта',
  `begin_date` date NOT NULL DEFAULT '1970-01-01' COMMENT 'Дата начала актуальности соответствия',
  `end_date` date NOT NULL DEFAULT '2054-12-31' COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` bigint(20) NOT NULL COMMENT 'Идентификатор организации',
  `user_organization_id` bigint(20) DEFAULT NULL,
  `module_id` bigint(20) NOT NULL COMMENT 'Идентификатор модуля',
  `status` int(11) DEFAULT NULL COMMENT 'Статус',
  PRIMARY KEY (`id`),
  KEY `key_city_object_id` (`city_object_id`),
  KEY `key_street_type_object_id` (`street_type_object_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_begin_date` (`begin_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  KEY `key_module_id` (`module_id`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_street_correction__city` FOREIGN KEY (`city_object_id`) REFERENCES `city` (`object_id`),
  CONSTRAINT `fk_street_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_street_correction__street` FOREIGN KEY (`object_id`) REFERENCES `street` (`object_id`),
  CONSTRAINT `fk_street_correction__street_type` FOREIGN KEY (`street_type_object_id`) REFERENCES `street_type` (`object_id`),
  CONSTRAINT `fk_street_correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Коррекция улицы';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `street_correction`
--

LOCK TABLES `street_correction` WRITE;
/*!40000 ALTER TABLE `street_correction` DISABLE KEYS */;
/*!40000 ALTER TABLE `street_correction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `street_string_value`
--

DROP TABLE IF EXISTS `street_string_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `street_string_value` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` bigint(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` bigint(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_street_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=829 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Локализация атрибутов улицы';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `street_string_value`
--

LOCK TABLES `street_string_value` WRITE;
/*!40000 ALTER TABLE `street_string_value` DISABLE KEYS */;
INSERT INTO `street_string_value` VALUES (1,1,1,'АВИАЦИОННАЯ'),(2,2,1,'АВТОСТРАДНАЯ'),(3,3,1,'АВТОСТРАДНЫЙ'),(4,4,1,'АДЫГЕЙСКАЯ'),(5,5,1,'АДЫГЕЙСКИЙ'),(6,6,1,'АЗЕРБАЙДЖАНСКАЯ'),(7,7,1,'АЗЕРБАЙДЖАНСКИЙ'),(8,8,1,'АЗЕРБАЙДЖАНСКИЙ'),(9,9,1,'АЗОВСТАЛЬСКАЯ'),(10,10,1,'АКАДЕМИКА БОГОМОЛЬЦА'),(11,11,1,'АКАДЕМИКА ВАЛЬТЕРА'),(12,12,1,'АКАДЕМИКА КУРЧАТОВА'),(13,13,1,'АКАДЕМИКА ПАВЛОВА'),(14,14,1,'АКАДЕМИКА ПРОСКУРЫ'),(15,15,1,'АКАДЕМИКА СИНЕЛЬНИКОВА'),(16,16,1,'АКАДЕМИКА ФИЛИППОВА'),(17,17,1,'АЛЕКСАНДРА НЕВСКОГО'),(18,18,1,'АЛЕКСАНДРОВСКАЯ'),(19,19,1,'АЛЕКСЕЕВА ПЕТРА'),(20,20,1,'АЛЕКСЕЕВСКАЯ'),(21,21,1,'АНАДЫРСКАЯ'),(22,22,1,'АНАДЫРСКИЙ'),(23,23,1,'АНДРЕЕВСКИЙ'),(24,24,1,'АНРИ БАРБЮСА'),(25,25,1,'АНТОКОЛЬСКОГО'),(26,26,1,'АПТЕКАРСКИЙ'),(27,27,1,'АПТЕКАРСКИЙ'),(28,28,1,'АРМАТУРНАЯ'),(29,29,1,'АРМЕЙСКАЯ'),(30,30,1,'АРМЕЙСКИЙ'),(31,31,1,'АРМЯНСКИЙ'),(32,32,1,'АРСЕНАЛЬНАЯ'),(33,33,1,'АРТЕЛЬНАЯ'),(34,34,1,'АРТЕМА'),(35,35,1,'АРТЕМА'),(36,36,1,'АРХАНГЕЛЬСКАЯ'),(37,37,1,'АРХИТЕКТОРОВ'),(38,38,1,'АСКОЛЬДОВСКАЯ'),(39,39,1,'АСТРОНОМИЧЕСКАЯ'),(40,40,1,'АФАНАСЬЕВСКАЯ'),(41,41,1,'АХСАРОВА'),(42,42,1,'АШХАБАДСКАЯ'),(43,43,1,'АШХАБАДСКИЙ'),(44,44,1,'АЭРОФЛОТСКАЯ'),(45,45,1,'БАБУШКИНА'),(46,46,1,'БАВАРСКАЯ'),(47,47,1,'БАВАРСКИЙ'),(48,48,1,'БАГРАТИОНА'),(49,49,1,'БАЗАРНАЯ'),(50,50,1,'БАКУЛИНА'),(51,51,1,'БАЛАКИРЕВА'),(52,52,1,'БАЛАКИРЕВА'),(53,53,1,'БАЛАКЛЕЙСКИЙ'),(54,54,1,'БАЛКАНСКАЯ'),(55,55,1,'БАЛТИЙСКАЯ'),(56,56,1,'БАРАБАШОВА'),(57,57,1,'БАРРИКАДНАЯ'),(58,58,1,'БАТУРИНСКАЯ'),(59,59,1,'БАШКИРСКАЯ'),(60,60,1,'БЕЗЛЮДОВСКАЯ'),(61,61,1,'БЕКЕТОВА'),(62,62,1,'БЕЛОБРОВСКИЙ'),(63,63,1,'БЕЛОГОРСКАЯ'),(64,64,1,'БЕЛОСТОЦКИЙ'),(65,65,1,'БЕРЕЗОВСКАЯ'),(66,66,1,'БЕСКРАЙНЯЯ'),(67,67,1,'БЕСТУЖЕВА'),(68,68,1,'БИОЛОГИЧЕСКАЯ'),(69,69,1,'БИОЛОГИЧЕСКИЙ'),(70,70,1,'БЛАГОДАТНАЯ'),(71,71,1,'БЛЮХЕРА'),(72,72,1,'БОБРУЙСКАЯ'),(73,73,1,'БОГДАНА ХМЕЛЬНИЦКОГО'),(74,74,1,'БОГДАНА ХМЕЛЬНИЦКОГО'),(75,75,1,'БОЕВАЯ'),(76,76,1,'БОЛГАРСКАЯ'),(77,77,1,'БОЛГАРСКИЙ'),(78,78,1,'БОЛЬШАЯ ГОНЧАРОВСКАЯ'),(79,79,1,'БОЛЬШАЯ КОЛЬЦЕВАЯ'),(80,80,1,'БОЛЬШОЙ ДАНИЛОВСКИЙ'),(81,81,1,'БОНДАРЕВСКАЯ'),(82,82,1,'БОРЗЕНКО'),(83,83,1,'БОРЗЫЙ'),(84,84,1,'БОРОВАЯ'),(85,85,1,'БОРОДИНОВСКАЯ'),(86,86,1,'БОРЬБЫ'),(87,87,1,'БОТКИНА'),(88,88,1,'БРЕСТСКАЯ'),(89,89,1,'БРОНЕВАЯ'),(90,90,1,'БРОНЕНОСЦА ПОТЕМКИН'),(91,91,1,'БРЯНСКИЙ'),(92,92,1,'БУЛЬВАРНАЯ'),(93,93,1,'БУРСАЦКИЙ'),(94,94,1,'БУТОВСКИЙ'),(95,95,1,'ВАВИЛОВА'),(96,96,1,'ВАГОННАЯ'),(97,97,1,'ВАЛДАЙСКАЯ'),(98,98,1,'ВАЛЕРЬЯНОВСКАЯ'),(99,99,1,'ВАЩЕНКОВСКИЙ'),(100,100,1,'ВЕРХНЕ-ГИЕВСКАЯ'),(101,101,1,'ВЕРХОВСКИЙ'),(102,102,1,'ВЕСНИНА'),(103,103,1,'ВЕШЕНСКАЯ'),(104,104,1,'ВИННИЦКИЙ'),(105,105,1,'ВИНОГРАДНАЯ'),(106,106,1,'ВИНОГРАДНЫЙ'),(107,107,1,'ВИШНЕВЫЙ'),(108,108,1,'ВЛАДИМИРСКАЯ'),(109,109,1,'ВЛАСЕНКО'),(110,110,1,'ВОЕННАЯ'),(111,111,1,'ВОЗРОЖДЕНИЯ'),(112,112,1,'ВОЙКОВА'),(113,113,1,'ВОКЗАЛЬНАЯ'),(114,114,1,'ВОЛОГОДСКАЯ'),(115,115,1,'ВОЛОДАРСКОГО'),(116,116,1,'ВОЛОДАРСКОГО'),(117,117,1,'ВОЛОДАРСКОГО'),(118,118,1,'ВОЛОЖАНОВСКАЯ'),(119,119,1,'ВОЛОШИНСКИЙ'),(120,120,1,'ВОЛЫНСКАЯ'),(121,121,1,'ВОЛЫНСКИЙ'),(122,122,1,'ВОРОБЬЕВА'),(123,123,1,'ВОРОБЬЕВА'),(124,124,1,'ВОССТАНИЯ'),(125,125,1,'ВОСЬМОГО МАРТА'),(126,126,1,'ВТОРОЙ ПЯТИЛЕТКИ'),(127,127,1,'ВЫСОЧИНЕНКО'),(128,128,1,'ВЯТСКАЯ'),(129,129,1,'ГАГАРИНА'),(130,130,1,'ГАЛАНА'),(131,131,1,'ГАЛИНСКАЯ'),(132,132,1,'ГАЛУШКИНСКАЯ'),(133,133,1,'ГАМАРНИКА'),(134,134,1,'ГАННЫ'),(135,135,1,'ГАРИБАЛЬДИ'),(136,136,1,'ГАРКУШИ'),(137,137,1,'ГАРШИНА'),(138,138,1,'ГАЦЕВА'),(139,139,1,'ГВАРДЕЙЦЕВ-ЖЕЛЕЗНОДОРОЖНИКОВ'),(140,140,1,'ГВАРДЕЙЦЕВ-ШИРОНИНЦЕВ'),(141,141,1,'ГЕОРГИЕВСКАЯ'),(142,142,1,'ГЕОРГИЕВСКИЙ 1-Й'),(143,143,1,'ГЕРАСИМОВСКИЙ'),(144,144,1,'ГЕРОЕВ СТАЛИНГРАДА'),(145,145,1,'ГЕРОЕВ ТРУДА'),(146,146,1,'ГЕРЦЕНА'),(147,147,1,'ГИЕВСКАЯ'),(148,148,1,'ГИРШМАНА'),(149,149,1,'ГОГОЛЯ'),(150,150,1,'ГОРДИЕНКОВСКАЯ'),(151,151,1,'ГОРНАЯ'),(152,152,1,'ГОРНЫЙ'),(153,153,1,'ГОРСОВЕТОВСКАЯ'),(154,154,1,'ГОСТИННАЯ'),(155,155,1,'ГРАБОВСКОГО'),(156,156,1,'ГРАЖДАНСКАЯ'),(157,157,1,'ГРАЖДАНСКИЙ'),(158,158,1,'ГРАЙВОРОНСКАЯ'),(159,159,1,'ГРЕКОВСКАЯ'),(160,160,1,'ГРИБОЕДОВА'),(161,161,1,'ГРИГОРОВСКАЯ'),(162,162,1,'ГРИЦЕВЦА'),(163,163,1,'ГРИЦЕВЦА'),(164,164,1,'ГРОЗНЕНСКАЯ'),(165,165,1,'ГУБКОМОВСКАЯ'),(166,166,1,'ГУДАНОВА'),(167,167,1,'ГЮГО'),(168,168,1,'ДАНИЛЕВСКОГО'),(169,169,1,'ДАРВИНА'),(170,170,1,'ДАЦЬКО'),(171,171,1,'ДАЧНЫЙ'),(172,172,1,'ДВАДЦАТЬ ТРЕТЬЕГО АВГУСТА'),(173,173,1,'ДВАДЦАТЬ ТРЕТЬЕГО АВГУСТА'),(174,174,1,'ДВЕНАДЦАТОГО АПРЕЛЯ'),(175,175,1,'ДЕМЧЕНКО'),(176,176,1,'ДЕПОВСКАЯ'),(177,177,1,'ДЕРГАЧЕВСКАЯ'),(178,178,1,'ДЕРЕВЯНКО'),(179,179,1,'ДЕРЖАВИНСКАЯ'),(180,180,1,'ДЖЕРЕЛО'),(181,181,1,'ДЖУТОВЫЙ'),(182,182,1,'ДЗЮБЫ'),(183,183,1,'ДИЗЕЛЬНАЯ'),(184,184,1,'ДИНАМОВСКАЯ'),(185,185,1,'ДИСПЕТЧЕРСКАЯ'),(186,186,1,'ДМИТРИЕВСКАЯ'),(187,187,1,'ДНЕПРОВСКАЯ'),(188,188,1,'ДОБРОДЕЦКОГО'),(189,189,1,'ДОБРОЛЮБОВА'),(190,190,1,'ДОВАТОРА'),(191,191,1,'ДОВАТОРА'),(192,192,1,'ДОВГАЛЕВСКАЯ'),(193,193,1,'ДОКУЧАЕВА'),(194,194,1,'ДОЛГОЖДАННЫЙ'),(195,195,1,'ДОНБАССОВСКИЙ'),(196,196,1,'ДОНЕЦ-ЗАХАРЖЕВСКОГО'),(197,197,1,'ДОНСКОЙ'),(198,198,1,'ДОРОЖНАЯ'),(199,199,1,'ДОСВИДНЫЙ'),(200,200,1,'ДОСТОЕВСКОГО'),(201,201,1,'ДОСТОЕВСКОГО'),(202,202,1,'ДРУЖБЫ НАРОДОВ'),(203,203,1,'ДЫБЕНКО ПАВЛА'),(204,204,1,'ЕВПАТОРИЙСКИЙ'),(205,205,1,'ЕЛЕНИНСКАЯ'),(206,206,1,'ЕЛИЗАВЕТИНСКАЯ'),(207,207,1,'ЕЛИЗАРОВА'),(208,208,1,'ЕРЕМОВСКИЙ'),(209,209,1,'ЕРМАКОВСКАЯ'),(210,210,1,'ЕСЕНИНА'),(211,211,1,'ЖЕЛЕЗНОДОРОЖНАЯ'),(212,212,1,'ЖЕЛЕЗНЯКОВА'),(213,213,1,'ЖЕЛЯБОВА'),(214,214,1,'ЖИТНАЯ'),(215,215,1,'ЖУКОВСКОГО'),(216,216,1,'ЖУКОВСКОГО'),(217,217,1,'ЖУТОВСКАЯ'),(218,218,1,'ЗАБАЙКАЛЬСКИЙ'),(219,219,1,'ЗАВОДА КОМСОМОЛЕЦ'),(220,220,1,'ЗАВОДСКАЯ'),(221,221,1,'ЗАВОДСКОЙ'),(222,222,1,'ЗАЛЕССКАЯ'),(223,223,1,'ЗАЛЮТИНСКАЯ'),(224,224,1,'ЗАПАДНАЯ'),(225,225,1,'ЗАЧЕПИЛОВСКАЯ'),(226,226,1,'ЗВЕЗДНАЯ'),(227,227,1,'ЗДОРОВЬЯ'),(228,228,1,'ЗДОРОВЬЯ'),(229,229,1,'ЗЕЛЕНАЯ'),(230,230,1,'ЗЕМОВСКАЯ'),(231,231,1,'ЗЕМОВСКИЙ'),(232,232,1,'ЗЕРНОВАЯ'),(233,233,1,'ЗЕРНОВОЙ'),(234,234,1,'ЗЕРНОВОЙ 1-Й'),(235,235,1,'ЗОЛОТОЙ 2-Й'),(236,236,1,'ЗОЛОЧЕВСКАЯ'),(237,237,1,'ЗОЛОЧЕВСКИЙ 1-Й'),(238,238,1,'ЗУБАРЕВА'),(239,239,1,'ИВ. ДУБОВОГО'),(240,240,1,'ИВАНА КАРКАЧА'),(241,241,1,'ИВАНА КАРКАЧА'),(242,242,1,'ИВАНОВА'),(243,243,1,'ИВАНОВСКАЯ'),(244,244,1,'ИВАНОВСКИЙ'),(245,245,1,'ИЖЕВСКИЙ'),(246,246,1,'ИЛЬИНСКАЯ'),(247,247,1,'ИЛЬИЧА'),(248,248,1,'ИНИЦИАТИВНАЯ'),(249,249,1,'ИСАЕВСКАЯ'),(250,250,1,'ИСКРИНСКАЯ'),(251,251,1,'ИСКРИНСКИЙ'),(252,252,1,'ИСПОЛКОМОВСКАЯ'),(253,253,1,'КАЛИНИНА'),(254,254,1,'КАМСКАЯ'),(255,255,1,'КАМЫШЕВА ИВАНА'),(256,256,1,'КАНДАУРОВА'),(257,257,1,'КАРАЗИНА'),(258,258,1,'КАРАЧЕВСКОЕ'),(259,259,1,'КАРБЫШЕВА'),(260,260,1,'КАРЛА МАРКСА'),(261,261,1,'КАРПИНСКОГО'),(262,262,1,'КАРПОВСКАЯ'),(263,263,1,'КАРПОВСКИЙ'),(264,264,1,'КАТАЕВА'),(265,265,1,'КАЧАНОВСКАЯ'),(266,266,1,'КАШИРСКАЯ'),(267,267,1,'КАШТАНОВАЯ'),(268,268,1,'КАШУБЫ'),(269,269,1,'КВИТКИ-ОСНОВЬЯНЕНКО'),(270,270,1,'КВИТКИНСКАЯ'),(271,271,1,'КЕРЧЕНСКАЯ'),(272,272,1,'КИБАЛЬЧИЧА'),(273,273,1,'КИЕВСКАЯ'),(274,274,1,'КИРГИЗСКАЯ'),(275,275,1,'КИРГИЗСКИЙ'),(276,276,1,'КИРОВА'),(277,277,1,'КИСЛОВОДСКАЯ'),(278,278,1,'КИТАЕНКО'),(279,279,1,'КЛАПЦОВА'),(280,280,1,'КЛАССИЧЕСКИЙ'),(281,281,1,'КЛЕЩЕВСКИЙ'),(282,282,1,'КЛОЧКОВСКАЯ'),(283,283,1,'КЛОЧКОВСКИЙ'),(284,284,1,'КНЫШЕВСКИЙ'),(285,285,1,'КОВТУНА'),(286,286,1,'КОКСОВАЯ'),(287,287,1,'КОКСОВЫЙ'),(288,288,1,'КОКЧЕТАВСКАЯ'),(289,289,1,'КОЛЛЕКТИВНАЯ'),(290,290,1,'КОЛОДЕЗНАЯ'),(291,291,1,'КОЛОДЕЗНЫЙ'),(292,292,1,'КОЛОМЕНСКАЯ'),(293,293,1,'КОЛОННАЯ'),(294,294,1,'КОЛОННЫЙ 1-Й'),(295,295,1,'КОЛХОЗНАЯ'),(296,296,1,'КОЛЬЦЕВОЙ'),(297,297,1,'КОЛЬЦОВСКАЯ'),(298,298,1,'КОМАНДАРМА КОРКА'),(299,299,1,'КОМБАЙНОВСКАЯ'),(300,300,1,'КОММУНАЛЬНЫЙ'),(301,301,1,'КОМСОМОЛЬСКАЯ'),(302,302,1,'КОМСОМОЛЬСКОЕ'),(303,303,1,'КОНДУКТОРСКАЯ'),(304,304,1,'КОНЕВА'),(305,305,1,'КОНОВАЛОВА'),(306,306,1,'КОНОТОПСКАЯ'),(307,307,1,'КОНОТОПСКИЙ'),(308,308,1,'КОНСТИТУЦИИ'),(309,309,1,'КОНТОРСКИЙ'),(310,310,1,'КОНЮШЕННЫЙ'),(311,311,1,'КООПЕРАТИВНАЯ'),(312,312,1,'КОПЕРНИКА'),(313,313,1,'КОРОБЕЙНИЦКИЙ'),(314,314,1,'КОРОЛЕНКО'),(315,315,1,'КОРОЛЕНКО'),(316,316,1,'КОРОСТЕЛЬСКАЯ'),(317,317,1,'КОРСУНСКАЯ'),(318,318,1,'КОРЧАГИНЦЕВ'),(319,319,1,'КОСИОРА'),(320,320,1,'КОСМИЧЕСКАЯ'),(321,321,1,'КОСМОНАВТОВ'),(322,322,1,'КОСТОМАРОВСКАЯ'),(323,323,1,'КОСТЫЧЕВА'),(324,324,1,'КОСТЮРИНСКИЙ'),(325,325,1,'КОТЕЛЬНИКОВСКАЯ'),(326,326,1,'КОТЛАССКАЯ'),(327,327,1,'КОТЛОВА'),(328,328,1,'КОТЛЯРЕВСКОГО'),(329,329,1,'КОТОВСКОГО'),(330,330,1,'КОЦАРСКАЯ'),(331,331,1,'КОШКИНА'),(332,332,1,'КРАВЦОВА'),(333,333,1,'КРАМАТОРСКИЙ'),(334,334,1,'КРАСИНА'),(335,335,1,'КРАСНАЯ АЛЛЕЯ'),(336,336,1,'КРАСНОАРМЕЙСКАЯ'),(337,337,1,'КРАСНОГО ЛЕТЧИКА'),(338,338,1,'КРАСНОГО ОКТЯБРЯ'),(339,339,1,'КРАСНОДАРСКАЯ'),(340,340,1,'КРАСНОДОНСКАЯ'),(341,341,1,'КРАСНОДОНСКИЙ'),(342,342,1,'КРАСНОЗНАМЕННАЯ'),(343,343,1,'КРАСНОЗНАМЕННЫЙ'),(344,344,1,'КРАСНОЙ ЗВЕЗДЫ'),(345,345,1,'КРАСНОКУТСКИЙ'),(346,346,1,'КРАСНОМАЯЦКАЯ'),(347,347,1,'КРАСНООКТЯБРЬСКАЯ'),(348,348,1,'КРАСНОПОЛЬСКАЯ'),(349,349,1,'КРАСНОПОСЕЛКОВАЯ'),(350,350,1,'КРАСНОШКОЛЬНАЯ'),(351,351,1,'КРАХМАЛЕВСКИЙ'),(352,352,1,'КРИВОМАЗОВА'),(353,353,1,'КРИВОРОЖСКАЯ'),(354,354,1,'КРОПИВНИЦКОГО'),(355,355,1,'КРУПСКОЙ'),(356,356,1,'КРУПСКОЙ'),(357,357,1,'КРУТОГОРСКИЙ'),(358,358,1,'КРЫЛОВА'),(359,359,1,'КРЫМСКАЯ'),(360,360,1,'КУБАСОВА'),(361,361,1,'КУБРАКОВСКИЙ'),(362,362,1,'КУЗНЕЧНАЯ'),(363,363,1,'КУЙБЫШЕВА'),(364,364,1,'КУЙБЫШЕВА'),(365,365,1,'КУЛИКА ИВАНА'),(366,366,1,'КУЛЬБИЦКИЙ'),(367,367,1,'КУЛЬТУРЫ'),(368,368,1,'КУРЯЖАНСКАЯ'),(369,369,1,'КУТОВАЯ'),(370,370,1,'ЛАГЕРНАЯ'),(371,371,1,'ЛАДОЖСКИЙ'),(372,372,1,'ЛАДЫГИНА'),(373,373,1,'ЛАЗО'),(374,374,1,'ЛАЗЬКОВА-ЛУЖОК'),(375,375,1,'ЛЕБЕДЕВА ПАВЛА'),(376,376,1,'ЛЕБЕДИНСКАЯ'),(377,377,1,'ЛЕНИНА'),(378,378,1,'ЛЕНИНА'),(379,379,1,'ЛЕНИНГРАДСКАЯ'),(380,380,1,'ЛЕНИНГРАДСКИЙ'),(381,381,1,'ЛЕРМОНТОВСКАЯ'),(382,382,1,'ЛЕСИ УКРАИНКИ'),(383,383,1,'ЛЕСНАЯ'),(384,384,1,'ЛЕСОПАРКОВСКИЙ 1-Й'),(385,385,1,'ЛЕСОПАРКОВСКИЙ 2-Й'),(386,386,1,'ЛИНЕЙНАЯ'),(387,387,1,'ЛОКОМОТИВНАЯ'),(388,388,1,'ЛОМОНОСОВА'),(389,389,1,'ЛОПАНСКАЯ'),(390,390,1,'ЛОПАНСКИЙ'),(391,391,1,'ЛОПАТИНСКИЙ'),(392,392,1,'ЛУГАНСКАЯ'),(393,393,1,'ЛУИ ПАСТЕРА'),(394,394,1,'ЛУИ ПАСТЕРА 2-Й'),(395,395,1,'ЛУНАЧАРСКОГО'),(396,396,1,'ЛЫСАЯ'),(397,397,1,'ЛЫСЕНКО'),(398,398,1,'ЛЮДВИГА СВОБОДЫ'),(399,399,1,'ЛЮСИНСКАЯ'),(400,400,1,'ЛЮТОВСКАЯ'),(401,401,1,'ЛЯПУНОВА'),(402,402,1,'МАКЕЕВСКАЯ'),(403,403,1,'МАКОВСКОГО'),(404,404,1,'МАЛИНОВСКАЯ'),(405,405,1,'МАЛИНОВСКОГО'),(406,406,1,'МАЛО-ГОНЧАРОВСКАЯ'),(407,407,1,'МАЛО-ПАНАСОВСКАЯ'),(408,408,1,'МАЛОДЖАНКОЙСКАЯ'),(409,409,1,'МАЛЫШЕВА'),(410,410,1,'МАРИУПОЛЬСКИЙ'),(411,411,1,'МАРСЕЛЯ КАШЕНА'),(412,412,1,'МАРШАЛА БАЖАНОВА'),(413,413,1,'МАРШАЛА БАТИЦКОГО'),(414,414,1,'МАРШАЛА ЖУКОВА'),(415,415,1,'МАРЬИНСКАЯ'),(416,416,1,'МАРЬЯНЕНКО'),(417,417,1,'МАТЕРИАЛИСТИЧЕСКИЙ'),(418,418,1,'МАТРОСОВА'),(419,419,1,'МАТЮШЕНКО'),(420,420,1,'МАШИНИСТОВ'),(421,421,1,'МАЯКОВСКОГО'),(422,422,1,'МЕЖЛАУКА'),(423,423,1,'МЕЛЬНИКОВА'),(424,424,1,'МЕНДЕЛЕЕВА'),(425,425,1,'МЕРЕФЯНСКОЕ'),(426,426,1,'МЕТАЛЛИСТА'),(427,427,1,'МЕТИЗНЫЙ'),(428,428,1,'МЕТРОСТРОИТЕЛЕЙ'),(429,429,1,'МИНАЙЛЕНКО'),(430,430,1,'МИРА'),(431,431,1,'МИРА'),(432,432,1,'МИРА'),(433,433,1,'МИРГОРОДСКАЯ'),(434,434,1,'МИРНАЯ'),(435,435,1,'МИРОНОСИЦКАЯ'),(436,436,1,'МОЕЧНАЯ'),(437,437,1,'МОЛЧАНОВСКИЙ'),(438,438,1,'МОЛЧАНОВСКИЙ'),(439,439,1,'МОНТАЖНАЯ'),(440,440,1,'МОНЮШКО'),(441,441,1,'МОРОЗОВА'),(442,442,1,'МОРОЗОВА ПАВЛИКА'),(443,443,1,'МОСКОВСКИЙ'),(444,444,1,'МОТОРНАЯ'),(445,445,1,'МОХНАЧАНСКАЯ'),(446,446,1,'МУЗЫКАЛЬНАЯ'),(447,447,1,'МУРАНОВА'),(448,448,1,'НАБЕРЕЖНЫЙ'),(449,449,1,'НАРВСКАЯ'),(450,450,1,'НАРИМАНОВА'),(451,451,1,'НАРОФОМИНСКАЯ'),(452,452,1,'НЕВЕЛЬСКАЯ'),(453,453,1,'НЕЖИНСКАЯ'),(454,454,1,'НЕМАНСКИЙ 4-Й'),(455,455,1,'НЕМЫШЛЯНСКАЯ'),(456,456,1,'НЕСТЕРОВА'),(457,457,1,'НЕТЕЧЕНСКАЯ'),(458,458,1,'НЕТЕЧЕНСКИЙ'),(459,459,1,'НЕХАЕНКО'),(460,460,1,'НИЖНЕГИЕВСКАЯ'),(461,461,1,'НИКИТИНА'),(462,462,1,'НИКИТИНОЙ ГАЛИНЫ'),(463,463,1,'НИКИТИНСКИЙ'),(464,464,1,'НИКОНОВСКАЯ'),(465,465,1,'НОВАЯ БАВАРИЯ'),(466,466,1,'НОВГОРОДСКАЯ'),(467,467,1,'НОВОМЯСНИЦКАЯ'),(468,468,1,'НОВОПРУДНАЯ'),(469,469,1,'НОВОСЕЛОВСКАЯ'),(470,470,1,'НОВОХАРЬКОВСКАЯ'),(471,471,1,'НОВЫЙ БЫТ'),(472,472,1,'НОГИНА'),(473,473,1,'НОГИНА'),(474,474,1,'НЬЮТОНА'),(475,475,1,'ОБОЯНСКАЯ'),(476,476,1,'ОГАРЕВСКОГО'),(477,477,1,'ОДОЕВСКИЙ'),(478,478,1,'ОКТЯБРЬСКОЙ РЕВОЛЮЦИИ'),(479,479,1,'ОЛИМПИЙСКАЯ'),(480,480,1,'ОЛЬМИНСКОГО'),(481,481,1,'ОМСКАЯ'),(482,482,1,'ОРДЖОНИКИДЗЕ'),(483,483,1,'ОРЕНБУРГСКАЯ'),(484,484,1,'ОРСКИЙ'),(485,485,1,'ОСЕТИНСКАЯ'),(486,486,1,'ОСЕТИНСКИЙ'),(487,487,1,'ОСИПЕНКО'),(488,488,1,'ОСНОВЯНСКАЯ'),(489,489,1,'ОСНОВЯНСКИЙ'),(490,490,1,'ОСТРОВСКОГО'),(491,491,1,'ОСТРОГРАДСКИЙ'),(492,492,1,'ОТАКАРА ЯРОША'),(493,493,1,'ОТАКАРА ЯРОША'),(494,494,1,'ОЧАКОВСКАЯ'),(495,495,1,'ОЩЕПКОВА'),(496,496,1,'П. СВИСТУНА'),(497,497,1,'ПАВЛЕНКОВСКИЙ'),(498,498,1,'ПАНАСОВСКИЙ 2-Й'),(499,499,1,'ПАРНИКОВСКИЙ'),(500,500,1,'ПАРОВОЗНАЯ'),(501,501,1,'ПАРХОМЕНКО'),(502,502,1,'ПАХАРЯ'),(503,503,1,'ПАХАРЯ'),(504,504,1,'ПАЩЕНКОВСКАЯ'),(505,505,1,'ПЕРВОГО МАЯ'),(506,506,1,'ПЕРВОЙ КОННОЙ АРМИИ'),(507,507,1,'ПЕРЕЕЗДНАЯ'),(508,508,1,'ПЕРМСКАЯ'),(509,509,1,'ПЕРОВСКОЙ'),(510,510,1,'ПЕТРА ШИРОНИНА'),(511,511,1,'ПЕТРАШЕВСКОГО'),(512,512,1,'ПЕТРОВСКОГО'),(513,513,1,'ПИЛОТОВ'),(514,514,1,'ПИОНЕРСКАЯ'),(515,515,1,'ПИСАРЕВА'),(516,516,1,'ПЛАНОВАЯ'),(517,517,1,'ПЛАНОВЫЙ'),(518,518,1,'ПЛАСТИЧНЫЙ'),(519,519,1,'ПЛЕТНЕВСКИЙ'),(520,520,1,'ПЛЕХАНОВСКАЯ'),(521,521,1,'ПЛИТОЧНАЯ'),(522,522,1,'ПЛИТОЧНЫЙ'),(523,523,1,'ПЛИТОЧНЫЙ'),(524,524,1,'ПОБЕДА 2-Й'),(525,525,1,'ПОБЕДИТЕЛЕЙ'),(526,526,1,'ПОБЕДОНОСНАЯ'),(527,527,1,'ПОБЕДЫ'),(528,528,1,'ПОЖАРСКОГО'),(529,529,1,'ПОЗНАНСКАЯ'),(530,530,1,'ПОЛЕВАЯ'),(531,531,1,'ПОЛЕВОЙ 1-Й'),(532,532,1,'ПОЛЗУНОВА'),(533,533,1,'ПОЛТАВСКАЯ'),(534,534,1,'ПОЛТАВСКИЙ'),(535,535,1,'ПОЛТАВСКИЙ ШЛЯХ'),(536,536,1,'ПОМЕРКИ'),(537,537,1,'ПОНОМАРЕВСКАЯ'),(538,538,1,'ПОПЕРЕЧНАЯ 1-Я'),(539,539,1,'ПОСТЫШЕВА'),(540,540,1,'ПОТЕБНИ'),(541,541,1,'ПОЧТОВЫЙ'),(542,542,1,'ПРАВДЫ'),(543,543,1,'ПРИВОКЗАЛЬНАЯ'),(544,544,1,'ПРИВОКЗАЛЬНЫЙ'),(545,545,1,'ПРИМАКОВА'),(546,546,1,'ПРИМЕРОВСКАЯ'),(547,547,1,'ПРИХОДЬКОВСКИЙ'),(548,548,1,'ПРОГРЕСС'),(549,549,1,'ПРОДОЛЬНАЯ'),(550,550,1,'ПРОЕЗЖИЙ'),(551,551,1,'ПРОЕКТНЫЙ'),(552,552,1,'ПРОЛЕТАРСКАЯ'),(553,553,1,'ПРОРЕЗНАЯ'),(554,554,1,'ПРОФСОЮЗНЫЙ'),(555,555,1,'ПСАРЕВСКИЙ'),(556,556,1,'ПСКОВСКАЯ'),(557,557,1,'ПУШКАРЕВСКАЯ'),(558,558,1,'ПУШКИНСКАЯ'),(559,559,1,'ПУШКИНСКИЙ'),(560,560,1,'ПЯТИГОРСКИЙ'),(561,561,1,'ПЯТИДЕСЯТИЛЕТИЯ ВЛКСМ'),(562,562,1,'ПЯТИДЕСЯТИЛЕТИЯ СССР'),(563,563,1,'ПЯТИСОТНИЦКАЯ'),(564,564,1,'РАБКОРОВСКАЯ'),(565,565,1,'РАДИЩЕВА'),(566,566,1,'РАЙКОМОВСКАЯ'),(567,567,1,'РЕВКОМОВСКАЯ'),(568,568,1,'РЕВОЛЮЦИИ'),(569,569,1,'РЕВОЛЮЦИИ 1905 ГОДА'),(570,570,1,'РЕГИСТРАТОРСКАЯ'),(571,571,1,'РЕЗЕРВНАЯ'),(572,572,1,'РЕЗЕРВНЫЙ'),(573,573,1,'РЕЗНИКОВСКИЙ'),(574,574,1,'РЕЛЬЕФНАЯ'),(575,575,1,'РЕЧНОЙ'),(576,576,1,'РЕШЕТНИКОВСКИЙ'),(577,577,1,'РЖЕВСКИЙ'),(578,578,1,'РИЖСКИЙ'),(579,579,1,'РОВНЫЙ'),(580,580,1,'РОГАНСКАЯ'),(581,581,1,'РОГАТИНСКИЙ'),(582,582,1,'РОДНИКОВАЯ'),(583,583,1,'РОЗЫ ЛЮКСЕМБУРГ'),(584,584,1,'РОМАШКИНА'),(585,585,1,'РОМЕНА РОЛЛАНА'),(586,586,1,'РОСТОВСКАЯ'),(587,587,1,'РУБАНОВСКАЯ'),(588,588,1,'РУБЕЖАНСКИЙ'),(589,589,1,'РУДИКА'),(590,590,1,'РУДНЕВА'),(591,591,1,'РУДНИЧНАЯ'),(592,592,1,'РУДНИЧНЫЙ 1-Й'),(593,593,1,'РУСТАВЕЛИ'),(594,594,1,'РУСТАВЕЛИ'),(595,595,1,'РЫБАЛКО'),(596,596,1,'РЫБАСОВСКАЯ'),(597,597,1,'РЫБАСОВСКИЙ'),(598,598,1,'РЫЛЕЕВА'),(599,599,1,'РЫЛЕЕВА'),(600,600,1,'РЫМАРСКАЯ'),(601,601,1,'РЯЗАНСКАЯ'),(602,602,1,'С. ОРЕШКОВА'),(603,603,1,'САГАЙДАЧНОГО'),(604,604,1,'САДОВОПАРКОВАЯ'),(605,605,1,'САДОВЫЙ'),(606,606,1,'САЛТОВСКОЕ'),(607,607,1,'САМАРКАНДСКИЙ 2-Й'),(608,608,1,'САММЕРОВСКИЙ'),(609,609,1,'САМОДЕЯТЕЛЬНАЯ'),(610,610,1,'САМОКИША'),(611,611,1,'САМОЛЕТНАЯ'),(612,612,1,'САПЕРНАЯ'),(613,613,1,'САХАРОЗАВОДСКАЯ'),(614,614,1,'СВЕРДЛОВА'),(615,615,1,'СВЕТ ШАХТЕРА'),(616,616,1,'СВЕТ ШАХТЕРА'),(617,617,1,'СВЕТЛАНОВСКАЯ'),(618,618,1,'СВЕТЛАЯ'),(619,619,1,'СВИНАРЕНКО ПЕТРА'),(620,620,1,'СВИРСКАЯ'),(621,621,1,'СЕВЕРНЫЙ'),(622,622,1,'СЕВЕРО-КАВКАЗСКАЯ'),(623,623,1,'СЕЛЬКОРОВСКИЙ'),(624,624,1,'СЕЛЯНСКАЯ'),(625,625,1,'СЕЛЯНСКИЙ'),(626,626,1,'СЕМИГРАДСКАЯ'),(627,627,1,'СЕМИГРАДСКИЙ'),(628,628,1,'СЕМНАДЦАТОГО ПАРТСЪЕЗДА'),(629,629,1,'СЕМНАДЦАТОГО ПАРТСЪЕЗДА'),(630,630,1,'СЕРГИЕВСКАЯ'),(631,631,1,'СЕРИКОВСКАЯ'),(632,632,1,'СЕРП И МОЛОТ'),(633,633,1,'СЕЧЕНОВА'),(634,634,1,'СИДЕЛЬНИКОВСКИЙ'),(635,635,1,'СИДОРЕНКОВСКАЯ'),(636,636,1,'СИРЕНЕВЫЙ'),(637,637,1,'СКАДОВСКОГО'),(638,638,1,'СКОВОРОДИНОВСКАЯ'),(639,639,1,'СКОРОХОДА'),(640,640,1,'СКРЫПНИКА'),(641,641,1,'СЛАВЫ'),(642,642,1,'СЛАВЯНСКАЯ'),(643,643,1,'СЛЕСАРНЫЙ'),(644,644,1,'СЛИНЬКО'),(645,645,1,'СЛУЖЕБНАЯ'),(646,646,1,'СМОЛЬНАЯ'),(647,647,1,'СНЕГИРЕВСКИЙ'),(648,648,1,'СОВЕТСКИЙ'),(649,649,1,'СОВЕТСКИЙ'),(650,650,1,'СОИЧА'),(651,651,1,'СОКОЛОВА'),(652,652,1,'СОЛДАТСКАЯ'),(653,653,1,'СОЛДАТСКИЙ'),(654,654,1,'СОЛНЕЧНАЯ'),(655,655,1,'СОЛЯНИКОВСКИЙ'),(656,656,1,'СОМОВСКАЯ'),(657,657,1,'СОРЕВНОВАНИЯ'),(658,658,1,'СОРОЧИНСКАЯ'),(659,659,1,'СОФИЕВСКАЯ'),(660,660,1,'СОХОРА'),(661,661,1,'СОЦИАЛИСТИЧЕСКАЯ'),(662,662,1,'СОЦИАЛИСТИЧЕСКИЙ'),(663,663,1,'СОЧИНСКАЯ'),(664,664,1,'СПАРТАКА'),(665,665,1,'СПАРТАКОВСКИЙ'),(666,666,1,'СПИРИДОНОВСКАЯ'),(667,667,1,'СПОРТИВНАЯ'),(668,668,1,'СПОРТИВНЫЙ'),(669,669,1,'СТАДИОННЫЙ'),(670,670,1,'СТАНКОСТРОИТЕЛЬНАЯ'),(671,671,1,'СТАНЦИОННАЯ'),(672,672,1,'СТАРИЦКОГО'),(673,673,1,'СТАРО-КРЫМСКАЯ'),(674,674,1,'СТАРОДЕСЯТИСАЖЕННАЯ'),(675,675,1,'СТАРОМАЛООСНОВЯНСКАЯ'),(676,676,1,'СТАРОПРУДНАЯ'),(677,677,1,'СТАРОШИШКОВСКАЯ'),(678,678,1,'СТАРТОВАЯ'),(679,679,1,'СТАХАНОВСКАЯ'),(680,680,1,'СТЕПНАЯ'),(681,681,1,'СТЕПНОЙ'),(682,682,1,'СТЕФЕНСОНА'),(683,683,1,'СТОЛЯРНЫЙ'),(684,684,1,'СТРЕЛЕЦКИЙ'),(685,685,1,'СТРОИТЕЛЬНАЯ'),(686,686,1,'СТУДЕНЧЕСКАЯ'),(687,687,1,'СУМГАИТСКАЯ'),(688,688,1,'СУМСКАЯ'),(689,689,1,'СУХАРЕВСКАЯ'),(690,690,1,'СУЩЕНСКАЯ'),(691,691,1,'СЧАСТЛИВАЯ'),(692,692,1,'ТАГАНСКАЯ'),(693,693,1,'ТАГАНСКИЙ 1-Й'),(694,694,1,'ТАГАНСКИЙ 2-Й'),(695,695,1,'ТАГАНСКИЙ 3-Й'),(696,696,1,'ТАДЖИКСКАЯ'),(697,697,1,'ТАНКОПИЯ'),(698,698,1,'ТАРАСОВСКАЯ'),(699,699,1,'ТАРАСОВСКИЙ'),(700,700,1,'ТАРАСОВСКИЙ'),(701,701,1,'ТАРХОВА'),(702,702,1,'ТАХИАТАШСКАЯ'),(703,703,1,'ТАШКЕНТСКАЯ'),(704,704,1,'ТВЕРСКАЯ'),(705,705,1,'ТЕАТРАЛЬНАЯ'),(706,706,1,'ТЕАТРАЛЬНЫЙ'),(707,707,1,'ТЕЛЬМАНА'),(708,708,1,'ТЕПЛОВОЗНАЯ'),(709,709,1,'ТИМИРЯЗЕВА'),(710,710,1,'ТИМУРОВЦЕВ'),(711,711,1,'ТИНЯКОВА'),(712,712,1,'ТИНЯКОВСКИЙ 1-Й'),(713,713,1,'ТИНЯКОВСКИЙ 2-Й'),(714,714,1,'ТИТАРЕНКОВСКИЙ'),(715,715,1,'ТОБОЛЬСКАЯ'),(716,716,1,'ТОКАРЕВСКИЙ'),(717,717,1,'ТОРГОВАЯ'),(718,718,1,'ТОРГОВЫЙ'),(719,719,1,'ТРАКТОРОСТРОИТЕЛЕЙ'),(720,720,1,'ТРЕТЬЕГО ИНТЕРНАЦИОНАЛА'),(721,721,1,'ТРИНКЛЕРА'),(722,722,1,'ТРИНКЛЕРА'),(723,723,1,'ТРОФИМОВСКИЙ'),(724,724,1,'ТРУДА'),(725,725,1,'ТРУФАНОВА'),(726,726,1,'ТУРГЕНЕВСКАЯ'),(727,727,1,'ТУРГЕНЕВСКИЙ'),(728,728,1,'ТУРКЕСТАНСКАЯ'),(729,729,1,'ТУХАЧЕВСКОГО'),(730,730,1,'УБОРЕВИЧА'),(731,731,1,'УЖВИЙ НАТАЛЬИ'),(732,732,1,'УКРАИНСКАЯ'),(733,733,1,'УЛЬЯНОВА АЛЕКСАНДРА'),(734,734,1,'УМАНСКАЯ'),(735,735,1,'УНИВЕРСИТЕТСКАЯ'),(736,736,1,'УРАЛЬСКАЯ'),(737,737,1,'УРАЛЬСКИЙ'),(738,738,1,'УРИЦКОГО'),(739,739,1,'УРИЦКОГО'),(740,740,1,'УСОВСКИЙ'),(741,741,1,'УСОВСКИЙ 2-Й'),(742,742,1,'УССУРИЙСКАЯ'),(743,743,1,'УШАКОВА'),(744,744,1,'ФАБРИЦИУСА'),(745,745,1,'ФАНИНСКИЙ'),(746,746,1,'ФЕДОРОВСКАЯ'),(747,747,1,'ФЕЙЕРБАХА'),(748,748,1,'ФЕЙЕРБАХА'),(749,749,1,'ФЕЛЬДШЕРСКАЯ'),(750,750,1,'ФЕРГАНСКАЯ'),(751,751,1,'ФЕСЕНКОВСКАЯ'),(752,752,1,'ФЕСЕНКОВСКИЙ'),(753,753,1,'ФЕСТИВАЛЬНАЯ'),(754,754,1,'ФИЛИППОВСКАЯ'),(755,755,1,'ФИСАНОВИЧА'),(756,756,1,'ФОНВИЗИНА'),(757,757,1,'ФРАНКОВСКАЯ'),(758,758,1,'ФРАНТИШЕКА КРАЛА'),(759,759,1,'ФРОЛОВСКИЙ'),(760,760,1,'ФРУНЗЕ'),(761,761,1,'ФРУНЗЕ'),(762,762,1,'ХАЛТУРИНА'),(763,763,1,'ХАРЬКОВСКАЯ'),(764,764,1,'ХАРЬКОВСКАЯ'),(765,765,1,'ХАРЬКОВСКИХ ДИВИЗИЙ'),(766,766,1,'ХИМИЧЕСКИЙ'),(767,767,1,'ХЛЕБОРОБНАЯ'),(768,768,1,'ХОРОЛЬСКАЯ'),(769,769,1,'ХУТОРЯНСКАЯ'),(770,770,1,'ЦЕМЕНТНАЯ'),(771,771,1,'ЦЕПКОВСКАЯ'),(772,772,1,'ЦЫГАРЕВСКИЙ'),(773,773,1,'ЦЮРУПЫ'),(774,774,1,'ЧАЙКИНОЙ ЛИЗЫ'),(775,775,1,'ЧАЙКОВСКОГО'),(776,776,1,'ЧЕБОТАРСКАЯ'),(777,777,1,'ЧЕБОТАРСКИЙ 1-Й'),(778,778,1,'ЧЕБОТАРСКИЙ 2-Й'),(779,779,1,'ЧЕЛЮСКИНЦЕВ'),(780,780,1,'ЧЕРЕДНИЧЕНКОВСКИЙ'),(781,781,1,'ЧЕРЕПАНОВЫХ'),(782,782,1,'ЧЕРКАССКАЯ'),(783,783,1,'ЧЕРНИГОВСКАЯ'),(784,784,1,'ЧЕРНИГОВСКИЙ'),(785,785,1,'ЧЕРНИКОВА'),(786,786,1,'ЧЕРНОЗЕМНАЯ'),(787,787,1,'ЧЕРНОМОРСКАЯ'),(788,788,1,'ЧЕРНЫШЕВСКОГО'),(789,789,1,'ЧЕТВЕРТЫЙ'),(790,790,1,'ЧЕХОВА'),(791,791,1,'ЧИГИРИНА ЮЛИЯ'),(792,792,1,'ЧИЧИБАБИНА'),(793,793,1,'ЧКАЛОВА'),(794,794,1,'ЧУБАРЯ'),(795,795,1,'ЧУГУЕВСКАЯ'),(796,796,1,'ЧУГУЕВСКИЙ 3-Й'),(797,797,1,'ШАПОВАЛОВСКИЙ'),(798,798,1,'ШАРИКОВАЯ'),(799,799,1,'ШЕВЧЕНКО'),(800,800,1,'ШЕКСПИРА'),(801,801,1,'ШЕКСПИРА'),(802,802,1,'ШИЛОВСКИЙ'),(803,803,1,'ШИРЯЕВА'),(804,804,1,'ШИШКОВСКАЯ'),(805,805,1,'ШМИДТА ОТТО'),(806,806,1,'ШТУРМОВАЯ'),(807,807,1,'ШУБЕРТА'),(808,808,1,'ШУЛЬЖЕНКО КЛАВДИИ'),(809,809,1,'ЩИГРОВСКАЯ'),(810,810,1,'ЩОРСА'),(811,811,1,'ЭЙДЕМАНА РОБЕРТА'),(812,812,1,'ЭЛЕКТРОВОЗНАЯ'),(813,813,1,'ЭЛЕКТРОИНСТРУМЕНТАЛЬНЫЙ'),(814,814,1,'ЭЛЕКТРОИНСТРУМЕНТАЛЬНЫЙ'),(815,815,1,'ЭЛЕКТРОИНСТРУМЕНТАЛЬНЫЙ 1-Й'),(816,816,1,'ЭЛЬБРУССКИЙ'),(817,817,1,'ЭНГЕЛЬСА'),(818,818,1,'ЭНЕРГЕТИЧЕСКАЯ'),(819,819,1,'ЭСТАКАДНАЯ'),(820,820,1,'ЮЖНОПРОЕКТНАЯ'),(821,821,1,'ЮМАШЕВА'),(822,822,1,'ЮННАТОВ'),(823,823,1,'ЮРЬЕВА'),(824,824,1,'ЮРЬЕВСКАЯ'),(825,825,1,'ЮРЬЕВСКИЙ'),(826,826,1,'ЯКИРА'),(827,827,1,'ЯКУБОВСКИЙ'),(828,828,1,'ЯРОСЛАВСКАЯ');
/*!40000 ALTER TABLE `street_string_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `street_sync`
--

DROP TABLE IF EXISTS `street_sync`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `street_sync` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор синхронизации улицы',
  `object_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор объекта улица',
  `street_type_object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта тип улицы',
  `city_object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта город',
  `external_id` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Код улицы (ID)',
  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Название типа улицы',
  `street_type_short_name` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Тип улицы (краткое название)',
  `date` datetime NOT NULL COMMENT 'Дата актуальности',
  `status` int(11) NOT NULL COMMENT 'Статус синхронизации',
  PRIMARY KEY (`id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_street_type_object_id` (`street_type_object_id`),
  KEY `key_city_object_id` (`city_object_id`),
  KEY `key_external_id` (`external_id`),
  KEY `key_name` (`name`),
  KEY `key_street_type_short_name` (`street_type_short_name`),
  KEY `key_date` (`date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_street_sync__city` FOREIGN KEY (`city_object_id`) REFERENCES `city` (`object_id`),
  CONSTRAINT `fk_street_sync__street` FOREIGN KEY (`object_id`) REFERENCES `street` (`object_id`),
  CONSTRAINT `fk_street_sync__street_type` FOREIGN KEY (`street_type_object_id`) REFERENCES `street_type` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Синхронизация улиц';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `street_sync`
--

LOCK TABLES `street_sync` WRITE;
/*!40000 ALTER TABLE `street_sync` DISABLE KEYS */;
/*!40000 ALTER TABLE `street_sync` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `street_type`
--

DROP TABLE IF EXISTS `street_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `street_type` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор родительского объекта: не используется',
  `parent_entity_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор сущности родительского объекта: не используется',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  `permission_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'Ключ прав доступа к объекту',
  `external_id` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_street_type__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_street_type__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Тип улицы';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `street_type`
--

LOCK TABLES `street_type` WRITE;
/*!40000 ALTER TABLE `street_type` DISABLE KEYS */;
INSERT INTO `street_type` VALUES (1,1,NULL,NULL,'2014-07-25 13:43:50',NULL,1,0,'6490'),(2,2,NULL,NULL,'2014-07-25 13:43:50',NULL,1,0,'65221520'),(3,3,NULL,NULL,'2014-07-25 13:43:50',NULL,1,0,'6491'),(4,4,NULL,NULL,'2014-07-25 13:43:50',NULL,1,0,'6492'),(5,5,NULL,NULL,'2014-07-25 13:43:50',NULL,1,0,'65219650'),(6,6,NULL,NULL,'2014-07-25 13:43:50',NULL,1,0,'6493'),(7,7,NULL,NULL,'2014-07-25 13:43:50',NULL,1,0,'6494'),(8,8,NULL,NULL,'2014-07-25 13:43:50',NULL,1,0,'6495'),(9,9,NULL,NULL,'2014-07-25 13:43:50',NULL,1,0,'6496'),(10,10,NULL,NULL,'2014-07-25 13:43:50',NULL,1,0,'6497'),(11,11,NULL,NULL,'2014-07-25 13:43:50',NULL,1,0,'6498'),(12,12,NULL,NULL,'2014-07-25 13:43:50',NULL,1,0,'6499'),(13,13,NULL,NULL,'2014-07-25 13:43:50',NULL,1,0,'119443802'),(14,14,NULL,NULL,'2014-07-25 13:43:50',NULL,1,0,'6500'),(15,15,NULL,NULL,'2014-07-25 13:43:50',NULL,1,0,'6501'),(16,16,NULL,NULL,'2014-07-25 13:43:50',NULL,1,0,'6502'),(17,17,NULL,NULL,'2014-07-25 13:43:50',NULL,1,0,'6503');
/*!40000 ALTER TABLE `street_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `street_type_attribute`
--

DROP TABLE IF EXISTS `street_type_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `street_type_attribute` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` bigint(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 1400 - КРАТКОЕ НАЗВАНИЕ, 1401 - НАЗВАНИЕ',
  `value_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор значения',
  `value_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа значения атрибута: 1400 - STRING_VALUE, 1401 - STRING_VALUE',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_street_type_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`) REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_street_type_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`) REFERENCES `entity_attribute_value_type` (`id`),
  CONSTRAINT `fk_street_type_attribute__street_type` FOREIGN KEY (`object_id`) REFERENCES `street_type` (`object_id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Атрибуты типа улицы';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `street_type_attribute`
--

LOCK TABLES `street_type_attribute` WRITE;
/*!40000 ALTER TABLE `street_type_attribute` DISABLE KEYS */;
INSERT INTO `street_type_attribute` VALUES (1,1,1,1400,1,1400,'2014-07-25 13:43:50',NULL,1),(2,1,1,1401,2,1401,'2014-07-25 13:43:50',NULL,1),(3,1,2,1400,3,1400,'2014-07-25 13:43:50',NULL,1),(4,1,2,1401,4,1401,'2014-07-25 13:43:50',NULL,1),(5,1,3,1400,5,1400,'2014-07-25 13:43:50',NULL,1),(6,1,3,1401,6,1401,'2014-07-25 13:43:50',NULL,1),(7,1,4,1400,7,1400,'2014-07-25 13:43:50',NULL,1),(8,1,4,1401,8,1401,'2014-07-25 13:43:50',NULL,1),(9,1,5,1400,9,1400,'2014-07-25 13:43:50',NULL,1),(10,1,5,1401,10,1401,'2014-07-25 13:43:50',NULL,1),(11,1,6,1400,11,1400,'2014-07-25 13:43:50',NULL,1),(12,1,6,1401,12,1401,'2014-07-25 13:43:50',NULL,1),(13,1,7,1400,13,1400,'2014-07-25 13:43:50',NULL,1),(14,1,7,1401,14,1401,'2014-07-25 13:43:50',NULL,1),(15,1,8,1400,15,1400,'2014-07-25 13:43:50',NULL,1),(16,1,8,1401,16,1401,'2014-07-25 13:43:50',NULL,1),(17,1,9,1400,17,1400,'2014-07-25 13:43:50',NULL,1),(18,1,9,1401,18,1401,'2014-07-25 13:43:50',NULL,1),(19,1,10,1400,19,1400,'2014-07-25 13:43:50',NULL,1),(20,1,10,1401,20,1401,'2014-07-25 13:43:50',NULL,1),(21,1,11,1400,21,1400,'2014-07-25 13:43:50',NULL,1),(22,1,11,1401,22,1401,'2014-07-25 13:43:50',NULL,1),(23,1,12,1400,23,1400,'2014-07-25 13:43:50',NULL,1),(24,1,12,1401,24,1401,'2014-07-25 13:43:50',NULL,1),(25,1,13,1400,25,1400,'2014-07-25 13:43:50',NULL,1),(26,1,13,1401,26,1401,'2014-07-25 13:43:50',NULL,1),(27,1,14,1400,27,1400,'2014-07-25 13:43:50',NULL,1),(28,1,14,1401,28,1401,'2014-07-25 13:43:50',NULL,1),(29,1,15,1400,29,1400,'2014-07-25 13:43:50',NULL,1),(30,1,15,1401,30,1401,'2014-07-25 13:43:50',NULL,1),(31,1,16,1400,31,1400,'2014-07-25 13:43:50',NULL,1),(32,1,16,1401,32,1401,'2014-07-25 13:43:50',NULL,1),(33,1,17,1400,33,1400,'2014-07-25 13:43:50',NULL,1),(34,1,17,1401,34,1401,'2014-07-25 13:43:50',NULL,1);
/*!40000 ALTER TABLE `street_type_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `street_type_correction`
--

DROP TABLE IF EXISTS `street_type_correction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `street_type_correction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта типа населенного пункта',
  `external_id` varchar(20) DEFAULT NULL COMMENT 'Внешний идентификатор объекта',
  `correction` varchar(100) NOT NULL COMMENT 'Название типа населенного пункта',
  `begin_date` date NOT NULL DEFAULT '1970-01-01' COMMENT 'Дата начала актуальности соответствия',
  `end_date` date NOT NULL DEFAULT '2054-12-31' COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` bigint(20) NOT NULL COMMENT 'Идентификатор организации',
  `user_organization_id` bigint(20) DEFAULT NULL,
  `module_id` bigint(20) NOT NULL COMMENT 'Идентификатор модуля',
  `status` int(11) DEFAULT NULL COMMENT 'Статус',
  PRIMARY KEY (`id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_begin_date` (`begin_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  KEY `key_module_id` (`module_id`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_street_type_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_street_type_correction__street_type` FOREIGN KEY (`object_id`) REFERENCES `street_type` (`object_id`),
  CONSTRAINT `fk_street_type__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Коррекция типа улицы';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `street_type_correction`
--

LOCK TABLES `street_type_correction` WRITE;
/*!40000 ALTER TABLE `street_type_correction` DISABLE KEYS */;
/*!40000 ALTER TABLE `street_type_correction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `street_type_string_value`
--

DROP TABLE IF EXISTS `street_type_string_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `street_type_string_value` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` bigint(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` bigint(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_street_type_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Локализация атрибутов типа улицы';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `street_type_string_value`
--

LOCK TABLES `street_type_string_value` WRITE;
/*!40000 ALTER TABLE `street_type_string_value` DISABLE KEYS */;
INSERT INTO `street_type_string_value` VALUES (1,1,1,'Б-Р'),(2,2,1,'БУЛЬВАР'),(3,3,1,'В-Д'),(4,4,1,'ВЪЕЗД'),(5,5,1,'М'),(6,6,1,'МАГИСТРАЛЬ'),(7,7,1,'М-Н'),(8,8,1,'МИКРОРАЙОН'),(9,9,1,'НАБ'),(10,10,1,'НАБЕРЕЖНАЯ'),(11,11,1,'ПЕР'),(12,12,1,'ПЕРЕУЛОК'),(13,13,1,'ПЛ'),(14,14,1,'ПЛОЩАДЬ'),(15,15,1,'П'),(16,16,1,'ПОДЪЕМ'),(17,17,1,'ПОС'),(18,18,1,'ПОСЕЛОК'),(19,19,1,'ПР-Д'),(20,20,1,'ПРОЕЗД'),(21,21,1,'ПРОСП'),(22,22,1,'ПРОСПЕКТ'),(23,23,1,'СП'),(24,24,1,'СПУСК'),(25,25,1,'СТ'),(26,26,1,'СТАНЦИЯ'),(27,27,1,'Т'),(28,28,1,'ТЕРРИТОРИЯ'),(29,29,1,'ТУП'),(30,30,1,'ТУПИК'),(31,31,1,'УЛ'),(32,32,1,'УЛИЦА'),(33,33,1,'ШОССЕ'),(34,34,1,'ШОССЕ');
/*!40000 ALTER TABLE `street_type_string_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `street_type_sync`
--

DROP TABLE IF EXISTS `street_type_sync`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `street_type_sync` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор синхронизации типа улицы',
  `object_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор объекта типа улицы',
  `external_id` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Код типа улицы (ID)',
  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Название типа улицы',
  `short_name` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Короткое название типа улицы',
  `date` datetime NOT NULL COMMENT 'Дата актуальности',
  `status` int(11) NOT NULL COMMENT 'Статус синхронизации',
  PRIMARY KEY (`id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_external_id` (`external_id`),
  KEY `key_name` (`name`),
  KEY `key_short_name` (`short_name`),
  KEY `key_date` (`date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_street_type_sync__street_type` FOREIGN KEY (`object_id`) REFERENCES `street_type` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Синхронизация типов улиц';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `street_type_sync`
--

LOCK TABLES `street_type_sync` WRITE;
/*!40000 ALTER TABLE `street_type_sync` DISABLE KEYS */;
/*!40000 ALTER TABLE `street_type_sync` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entity_string_value`
--

DROP TABLE IF EXISTS `entity_string_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entity_string_value` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` bigint(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` bigint(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=133 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Локализация';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entity_string_value`
--

LOCK TABLES `entity_string_value` WRITE;
/*!40000 ALTER TABLE `entity_string_value` DISABLE KEYS */;
INSERT INTO `entity_string_value` VALUES (1,100,1,'Квартира'),(2,100,2,'Квартира'),(3,101,1,'НОМЕР КВАРТИРЫ'),(4,101,2,'НОМЕР КВАРТИРИ'),(5,200,1,'Комната'),(6,200,2,'Кімната'),(7,201,1,'НОМЕР КОМНАТЫ'),(8,201,2,'НОМЕР КІМНАТИ'),(9,300,1,'Улица'),(10,300,2,'Вулиця'),(11,301,1,'НАИМЕНОВАНИЕ УЛИЦЫ'),(12,301,2,'НАЙМЕНУВАННЯ ВУЛИЦІ'),(13,302,1,'ТИП УЛИЦЫ'),(14,302,2,'ТИП УЛИЦЫ'),(15,1400,1,'Тип улицы'),(16,1400,2,'Тип улицы'),(17,1401,1,'КРАТКОЕ НАЗВАНИЕ'),(18,1401,2,'КРАТКОЕ НАЗВАНИЕ'),(19,1402,1,'НАЗВАНИЕ'),(20,1402,2,'НАЗВАНИЕ'),(21,400,1,'Населенный пункт'),(22,400,2,'Населений пункт'),(23,401,1,'НАИМЕНОВАНИЕ НАСЕЛЕННОГО ПУНКТА'),(24,401,2,'НАЙМЕНУВАННЯ НАСЕЛЕНОГО ПУНКТУ'),(25,402,1,'ТИП НАСЕЛЕННОГО ПУНКТА'),(26,402,2,'ТИП НАСЕЛЕННОГО ПУНКТА'),(27,1300,1,'Тип нас. пункта'),(28,1300,2,'Тип населенного пункта'),(29,1301,1,'КРАТКОЕ НАЗВАНИЕ'),(30,1301,2,'КРАТКОЕ НАЗВАНИЕ'),(31,1302,1,'НАЗВАНИЕ'),(32,1302,2,'НАЗВАНИЕ'),(33,500,1,'Дом'),(34,500,2,'Будинок'),(35,501,1,'РАЙОН'),(36,501,2,'РАЙОН'),(37,502,1,'АЛЬТЕРНАТИВНЫЙ АДРЕС'),(38,502,2,'АЛЬТЕРНАТИВНЫЙ АДРЕС'),(39,503,1,'СПИСОК КОДОВ ДОМА'),(40,503,2,'СПИСОК КОДОВ ДОМА'),(41,1500,1,'Адрес здания'),(42,1500,2,'Адрес здания'),(43,1501,1,'НОМЕР ДОМА'),(44,1501,2,'НОМЕР БУДИНКУ'),(45,1502,1,'КОРПУС'),(46,1502,2,'КОРПУС'),(47,1503,1,'СТРОЕНИЕ'),(48,1503,2,'БУДОВА'),(49,600,1,'Район'),(50,600,2,'Район'),(51,601,1,'НАИМЕНОВАНИЕ РАЙОНА'),(52,601,2,'НАЙМЕНУВАННЯ РАЙОНУ'),(53,602,1,'КОД РАЙОНА'),(54,602,2,'КОД РАЙОНУ'),(55,700,1,'Регион'),(56,700,2,'Регіон'),(57,701,1,'НАИМЕНОВАНИЕ РЕГИОНА'),(58,701,2,'НАЙМЕНУВАННЯ РЕГІОНУ'),(59,800,1,'Страна'),(60,800,2,'Країна'),(61,801,1,'НАИМЕНОВАНИЕ СТРАНЫ'),(62,801,2,'НАЙМЕНУВАННЯ КРАЇНИ'),(63,1000,1,'Пользователь'),(64,1000,2,'Користувач'),(65,1001,1,'ФАМИЛИЯ'),(66,1001,2,'ПРІЗВИЩЕ'),(67,1002,1,'ИМЯ'),(68,1002,2,'ІМ\'Я'),(69,1003,1,'ОТЧЕСТВО'),(70,1003,2,'ПО БАТЬКОВІ'),(71,2300,1,'Тип организации'),(72,2300,2,'Тип организации'),(73,2301,1,'ТИП ОРГАНИЗАЦИИ'),(74,2301,2,'ТИП ОРГАНИЗАЦИИ'),(75,900,1,'Организация'),(76,900,2,'Організація'),(77,901,1,'НАИМЕНОВАНИЕ ОРГАНИЗАЦИИ'),(78,901,2,'НАЙМЕНУВАННЯ ОРГАНІЗАЦІЇ'),(79,902,1,'УНИКАЛЬНЫЙ КОД ОРГАНИЗАЦИИ'),(80,902,2,'УНІКАЛЬНИЙ КОД ОРГАНІЗАЦІЇ'),(81,903,1,'РАЙОН'),(82,903,2,'РАЙОН'),(83,904,1,'РОДИТЕЛЬСКАЯ ОРГАНИЗАЦИЯ'),(84,904,2,'РОДИТЕЛЬСКАЯ ОРГАНИЗАЦИЯ'),(85,905,1,'ТИП ОРГАНИЗАЦИИ'),(86,905,2,'ТИП ОРГАНИЗАЦИИ'),(87,906,1,'КОРОТКОЕ НАИМЕНОВАНИЕ'),(88,906,2,'КОРОТКОЕ НАИМЕНОВАНИЕ'),(89,914,1,'КПП'),(90,914,2,'КПП'),(91,915,1,'ИНН'),(92,915,2,'ІПН'),(93,916,1,'ПРИМЕЧАНИЕ'),(94,916,2,'ПРИМІТКА'),(95,917,1,'ЮРИДИЧЕСКИЙ АДРЕС'),(96,917,2,'ЮРИДИЧНА АДРЕСА'),(97,918,1,'ПОЧТОВЫЙ АДРЕС'),(98,918,2,'ПОШТОВА АДРЕСА'),(99,919,1,'E-MAIL'),(100,919,2,'E-MAIL'),(101,921,1,'ДОПУСТИМЫЕ УСЛУГИ'),(102,921,2,'ДОПУСТИМI ПОСЛУГИ'),(103,403,1,'ПРЕФИКС Л/С ЕИРЦ'),(104,403,2,'ПРЕФІКС Л/Р ЄIРЦ'),(105,6000,1,'Л/c ПУ'),(106,6000,2,'Л/п ПП'),(107,6002,1,'КОЛ-ВО ПРОЖИВАЮЩИХ'),(108,6002,2,'КІЛЬКІСТЬ ПРОЖИВАЮЧИХ'),(109,6003,1,'ПЛОЩАДЬ ОБЩАЯ'),(110,6003,2,'ПЛОЩА ЗАГАЛЬНА'),(111,6004,1,'ПЛОЩАДЬ ЖИЛАЯ'),(112,6004,2,'ПЛОЩА ЖИТЛОВА'),(113,6005,1,'ПЛОЩАДЬ ОТАПЛИВАЕМАЯ'),(114,6005,2,'ПЛОЩА ОПАЛЮВАЛЬНА'),(115,6006,1,'ФИО ОСНОВНОГО КВАРТИРОСЪЕМЩИКА'),(116,6006,2,'ПIБ ОСНОВНОГО КВАРТИРОНАЙМАЧА'),(117,1010,1,'Модуль'),(118,1010,2,'Модуль'),(119,1110,1,'Тип модуля'),(120,1110,2,'Тип модуля'),(121,1111,1,'ТИП МОДУЛЯ'),(122,1111,2,'ТИП МОДУЛЯ'),(123,1011,1,'НАЗВАНИЕ'),(124,1011,2,'НАЗВА'),(125,1012,1,'СЕКРЕТНЫЙ КЛЮЧ'),(126,1012,2,'СЕКРЕТНИЙ КЛЮЧ'),(127,1013,1,'ИДЕНТИФИКАТОР'),(128,1013,2,'ІДЕНТИФІКАТОР'),(129,1014,1,'ОРГАНИЗАЦИЯ'),(130,1014,2,'ОРГАНИЗАЦІЯ'),(131,1015,1,'ТИП МОДУЛЯ'),(132,1015,2,'ТИП МОДУЛЯ');
/*!40000 ALTER TABLE `entity_string_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `update`
--

DROP TABLE IF EXISTS `update`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `update` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор обновления',
  `version` varchar(64) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Версия',
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата обновления',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Обновление базы данных';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `update`
--

LOCK TABLES `update` WRITE;
/*!40000 ALTER TABLE `update` DISABLE KEYS */;
INSERT INTO `update` VALUES (1,'20140610_201_0.0.3','2014-07-25 03:38:43'),(2,'eirc_20140814_0.0.4','2014-08-14 10:56:30'),(3,'eirc_20140818_0.0.5','2014-08-19 09:26:09'),(4,'eirc_20140818_0.0.5','2014-08-19 09:26:48'),(5,'eirc_20140818_0.0.5','2014-08-19 09:28:09'),(6,'eirc_20140818_0.0.5','2014-08-19 09:40:14'),(7,'eirc_20140821_0.0.6','2014-08-21 11:49:47'),(8,'eirc_20140821_0.0.6','2014-08-21 11:50:44'),(9,'eirc_20140821_0.0.6','2014-08-21 12:16:45'),(10,'eirc_20140822_0.0.7','2014-08-25 08:00:39');
/*!40000 ALTER TABLE `update` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор пользователя',
  `login` varchar(45) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Имя пользователя',
  `password` varchar(45) COLLATE utf8_unicode_ci NOT NULL COMMENT 'MD5 хэш пароля',
  `user_info_object_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор объекта информация о пользователе',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_key_login` (`login`),
  KEY `key_user_info_object_id` (`user_info_object_id`),
  CONSTRAINT `fk_user__user_info` FOREIGN KEY (`user_info_object_id`) REFERENCES `user_info` (`object_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Пользователь';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','21232f297a57a5a743894a0e4a801fc3',1),(2,'ANONYMOUS','ANONYMOUS',2),(3,'testadmin','9283a03246ef2dacdc21a9b137817ec1',3),(4,'testuser','5d9c68c6c50ed3d02a2fcf54f63993b6',4);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_info`
--

DROP TABLE IF EXISTS `user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_info` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор родительского объекта: не используется',
  `parent_entity_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор сущности родительского объекта: не используется',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия объекта',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия объекта',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  `permission_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'Ключ прав доступа к объекту',
  `external_id` bigint(20) DEFAULT NULL COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_user_info__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_user_info__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Информация о пользователе';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_info`
--

LOCK TABLES `user_info` WRITE;
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
INSERT INTO `user_info` VALUES (1,1,NULL,NULL,'2014-07-25 03:38:43',NULL,1,0,NULL),(2,2,NULL,NULL,'2014-07-25 03:38:43',NULL,1,0,NULL),(3,3,NULL,NULL,'2014-08-11 08:35:35',NULL,1,0,NULL),(4,4,NULL,NULL,'2014-08-11 08:37:46',NULL,1,0,NULL);
/*!40000 ALTER TABLE `user_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_info_attribute`
--

DROP TABLE IF EXISTS `user_info_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_info_attribute` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` bigint(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` bigint(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа атрибута: 1000 - ФАМИЛИЯ, 1001 - ИМЯ, 1002 - ОТЧЕСТВО',
  `value_id` bigint(20) DEFAULT NULL COMMENT 'Идентификатор значения',
  `value_type_id` bigint(20) NOT NULL COMMENT 'Идентификатор типа значения атрибута: 1000 - last_name, 1001 - first_name, 1002 - middle_name',
  `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия атрибута',
  `end_date` timestamp NULL DEFAULT NULL COMMENT 'Дата окончания периода действия атрибута',
  `status` bigint(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT 1 COMMENT 'Статус: ACTIVE, INACTIVE, ARCHIVE',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`,`start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_user_info__entity_attribute_type` FOREIGN KEY (`attribute_type_id`) REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_user_info__entity_attribute_value_type` FOREIGN KEY (`value_type_id`) REFERENCES `entity_attribute_value_type` (`id`),
  CONSTRAINT `fk_user_info__user_info` FOREIGN KEY (`object_id`) REFERENCES `user_info` (`object_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Атрибуты информации о пользователе';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_info_attribute`
--

LOCK TABLES `user_info_attribute` WRITE;
/*!40000 ALTER TABLE `user_info_attribute` DISABLE KEYS */;
INSERT INTO `user_info_attribute` VALUES (1,1,1,1000,1,1000,'2014-07-25 03:38:43',NULL,1),(2,1,1,1001,1,1001,'2014-07-25 03:38:43',NULL,1),(3,1,1,1002,1,1002,'2014-07-25 03:38:43',NULL,1),(4,1,2,1000,2,1000,'2014-07-25 03:38:43',NULL,1),(5,1,2,1001,2,1001,'2014-07-25 03:38:43',NULL,1),(6,1,2,1002,2,1002,'2014-07-25 03:38:43',NULL,1),(7,1,3,1000,3,1000,'2014-08-11 08:35:35',NULL,1),(8,1,3,1001,3,1001,'2014-08-11 08:35:35',NULL,1),(9,1,3,1002,3,1002,'2014-08-11 08:35:35',NULL,1),(10,1,4,1000,3,1000,'2014-08-11 08:37:46',NULL,1),(11,1,4,1001,3,1001,'2014-08-11 08:37:46',NULL,1),(12,1,4,1002,3,1002,'2014-08-11 08:37:46',NULL,1);
/*!40000 ALTER TABLE `user_info_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_info_string_value`
--

DROP TABLE IF EXISTS `user_info_string_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_info_string_value` (
  `pk_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` bigint(20) NOT NULL COMMENT 'Идентификатор локализации',
  `locale_id` bigint(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`(128)),
  CONSTRAINT `fk_user_info_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Локализация атрибутов информации о пользователе';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_info_string_value`
--

LOCK TABLES `user_info_string_value` WRITE;
/*!40000 ALTER TABLE `user_info_string_value` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_info_string_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_organization`
--

DROP TABLE IF EXISTS `user_organization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_organization` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор организации пользователей',
  `user_id` bigint(20) NOT NULL COMMENT 'Идентификатор пользователя',
  `organization_object_id` bigint(20) NOT NULL COMMENT 'Идентификатор организации',
  `main` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Является ли организация основной',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_unique` (`user_id`,`organization_object_id`),
  KEY `fk_user_organization__organization` (`organization_object_id`),
  CONSTRAINT `fk_user_organization__organization` FOREIGN KEY (`organization_object_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_user_organization__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Организация пользователей';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_organization`
--

LOCK TABLES `user_organization` WRITE;
/*!40000 ALTER TABLE `user_organization` DISABLE KEYS */;
INSERT INTO `user_organization` VALUES (1,4,81,1);
/*!40000 ALTER TABLE `user_organization` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usergroup`
--

DROP TABLE IF EXISTS `usergroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usergroup` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор группы пользователей',
  `login` varchar(45) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Имя пользователя',
  `group_name` varchar(45) COLLATE utf8_unicode_ci NOT NULL COMMENT 'Название группы',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_login__group_name` (`login`,`group_name`),
  CONSTRAINT `fk_usergroup__user` FOREIGN KEY (`login`) REFERENCES `user` (`login`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Группа пользователей';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usergroup`
--

LOCK TABLES `usergroup` WRITE;
/*!40000 ALTER TABLE `usergroup` DISABLE KEYS */;
INSERT INTO `usergroup` VALUES (1,'admin','ADMINISTRATORS'),(2,'admin','EMPLOYEES'),(3,'admin','EMPLOYEES_CHILD_VIEW'),(4,'testadmin','ADMINISTRATORS'),(5,'testadmin','EMPLOYEES'),(6,'testadmin','EMPLOYEES_CHILD_VIEW'),(7,'testuser','EMPLOYEES'),(8,'testuser','EMPLOYEES_CHILD_VIEW');
/*!40000 ALTER TABLE `usergroup` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-08-25 18:02:08
