/*!40101 SET NAMES 'utf8' */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- ------------------------------
-- Forms of Ownership
-- ------------------------------
DROP TABLE IF EXISTS `ownership`;

CREATE TABLE `ownership` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Не используется',
  `parent_entity_id` BIGINT(20) COMMENT 'Не используется',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия параметров объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата завершения периода действия параметров объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус объекта: INACTIVE(0), ACTIVE(1), или ARCHIVE(2)',
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
  CONSTRAINT `fk_ownership__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_ownership__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Форма собственности';

DROP TABLE IF EXISTS `ownership_attribute`;

CREATE TABLE `ownership_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута. Возможные значения: 1100 - НАЗВАНИЕ',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия параметров атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия параметров атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус атрибута: INACTIVE(0), ACTIVE(1), или ARCHIVE(2)',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`entity_attribute_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_ownership_attribute__ownership` FOREIGN KEY (`object_id`) REFERENCES `ownership`(`object_id`),
  CONSTRAINT `fk_ownership_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`) REFERENCES `entity_attribute` (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Атрибуты объекта формы собственности';

DROP TABLE IF EXISTS `ownership_string_value`;

CREATE TABLE `ownership_string_value` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор значения',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`),
  CONSTRAINT `fk_ownership_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Локализированное значение атрибута формы собственности';

-- ------------------------------
-- Privileges
-- ------------------------------
DROP TABLE IF EXISTS `privilege`;

CREATE TABLE `privilege` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Не используется',
  `parent_entity_id` BIGINT(20) COMMENT 'Не используется',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия параметров объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия параметров объекта',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус объекта: INACTIVE(0), ACTIVE(1), или ARCHIVE(2)',
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
  CONSTRAINT `fk_privilege__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_privilege__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Привилегия';

DROP TABLE IF EXISTS `privilege_attribute`;

CREATE TABLE `privilege_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `entity_attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута. Возможные значения: 1200 - НАЗВАНИЕ, 1201 - КОД',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия значений атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата завершения периода действия значений атрибута',
  `status` INTEGER NOT NULL DEFAULT 1 COMMENT 'Статус атрибута: INACTIVE(0), ACTIVE(1), или ARCHIVE(2)',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`entity_attribute_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_entity_attribute_id` (`entity_attribute_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_privilege_attribute__privilege` FOREIGN KEY (`object_id`) REFERENCES `privilege`(`object_id`),
  CONSTRAINT `fk_privilege_attribute__entity_attribute` FOREIGN KEY (`entity_attribute_id`) REFERENCES `entity_attribute` (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Атрибуты объекта привилегии';

DROP TABLE IF EXISTS `privilege_string_value`;

CREATE TABLE `privilege_string_value` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор значения',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`),
  CONSTRAINT `fk_privilege_string_value__locale` FOREIGN KEY (`locale_id`) REFERENCES `locale` (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Локализированное значение атрибута привилегий';

-- ------------------------------
-- Request File Group
-- ------------------------------
DROP TABLE IF EXISTS `request_file_group`;

CREATE TABLE `request_file_group` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор группы файлов',
  `group_type` INTEGER COMMENT 'Тип группы',
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата создания',
  `status` INTEGER COMMENT 'Статус',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Группа файлов';

-- ------------------------------
-- Request File
-- ------------------------------
DROP TABLE IF EXISTS `request_file`;

CREATE TABLE `request_file` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор файла запроса',
  `group_id` BIGINT(20) COMMENT 'Идентификатор группы',
  `loaded` DATETIME NOT NULL COMMENT 'Дата загрузки',
  `name` VARCHAR(32) NOT NULL COMMENT 'Имя файла',
  `directory` VARCHAR(255) COMMENT 'Директория файла',
  `organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор организации',
  `begin_date` DATE NOT NULL COMMENT 'Дата начала',
  `end_date` DATE NULL COMMENT 'Дата окончания',
  `dbf_record_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'Количество записей в исходном файле',
  `length` BIGINT(20) COMMENT 'Размер файла. Не используется',
  `check_sum` VARCHAR(32) COMMENT 'Контрольная сумма. Не используется',
  `type` INTEGER COMMENT 'Тип файла',
  `sub_type` INTEGER COMMENT 'Подтип файла',
  `status` INTEGER COMMENT 'Статус',
  `user_organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор организации пользователя',
  `user_id` BIGINT(20) COMMENT 'Идентификатор пользователя',
  PRIMARY KEY (`id`),
  UNIQUE KEY `request_file_unique_id` (`name`, `organization_id`, `user_organization_id`, `begin_date`, `end_date`),
  KEY `key_group_id` (`group_id`),
  KEY `key_loaded` (`loaded`),
  KEY `key_name` (`name`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_begin_date` (`begin_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_type` (`type`),
  KEY `key_sub_type` (`sub_type`),
  KEY `key_user_organization_id` (`user_organization_id`),
  KEY `key_user_id` (`user_id`),
  CONSTRAINT `fk_request_file__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_request_file__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_request_file__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Файл запросов';

-- ------------------------------
-- Payment
-- ------------------------------
DROP TABLE IF EXISTS `payment`;

CREATE TABLE `payment` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор начисления',
  `request_file_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор файло запросов',
  `account_number` VARCHAR(100) COMMENT 'Номер счета',

  `internal_city_id` BIGINT(20) COMMENT 'Идентификатор населенного пункта',
  `internal_street_id` BIGINT(20) COMMENT 'Идентификатор улицы',
  `internal_street_type_id` BIGINT(20) COMMENT 'Идентификатор типа улицы',
  `internal_building_id` BIGINT(20) COMMENT 'Идентификатор дома',
  `internal_apartment_id` BIGINT(20) COMMENT 'Идентификатор квартиры. Не используется',

  `outgoing_city` VARCHAR(100) COMMENT 'Название населенного пункта используемое центром начисления',
  `outgoing_district` VARCHAR(100) COMMENT 'Название района используемое центром начисления',
  `outgoing_street` VARCHAR(100) COMMENT 'Название улицы используемое центром начисления',
  `outgoing_street_type` VARCHAR(100) COMMENT 'Название типа улицы используемое центром начисления',
  `outgoing_building_number` VARCHAR(100) COMMENT 'Номер дома используемый центром начисления',
  `outgoing_building_corp` VARCHAR(100) COMMENT 'Корпус используемый центром начисления',
  `outgoing_apartment` VARCHAR(100) COMMENT 'Номер квартиры. Не используется',

  `status` INTEGER NOT NULL DEFAULT 240 COMMENT 'Статус',

  `OWN_NUM` BIGINT(15) COMMENT 'Номер дела',
  `REE_NUM` INTEGER(2) COMMENT 'Номер реестра',
  `OPP` VARCHAR(8) COMMENT 'Признаки наличия услуг',
  `NUMB` INTEGER(2) COMMENT 'Общее число зарегистрированных',
  `MARK` INTEGER(2) COMMENT 'К-во людей, которые пользуются льготами',
  `CODE` INTEGER(4) COMMENT 'Код ЖЭО',
  `ENT_COD` BIGINT(10) COMMENT 'Код ЖЭО ОКПО',
  `FROG`  DECIMAL(5,1) COMMENT 'Процент льгот',
  `FL_PAY` DECIMAL(9,2) COMMENT 'Общая плата',
  `NM_PAY` DECIMAL(9,2) COMMENT 'Плата в пределах норм потребления',
  `DEBT` DECIMAL(9,2) COMMENT 'Сумма долга',
  `CODE2_1` INTEGER(6) COMMENT 'Оплата жилья',
  `CODE2_2` INTEGER(6) COMMENT 'система',
  `CODE2_3` INTEGER(6) COMMENT 'Горячее водоснабжение',
  `CODE2_4` INTEGER(6) COMMENT 'Холодное водоснабжение',
  `CODE2_5` INTEGER(6) COMMENT 'Газоснабжение',
  `CODE2_6` INTEGER(6) COMMENT 'Электроэнергия',
  `CODE2_7` INTEGER(6) COMMENT 'Вывоз мусора',
  `CODE2_8` INTEGER(6) COMMENT 'Водоотведение',
  `NORM_F_1` DECIMAL(10,4) COMMENT 'Общая площадь (оплата жилья)',
  `NORM_F_2` DECIMAL(10,4) COMMENT 'Объемы потребления (отопление)',
  `NORM_F_3` DECIMAL(10,4) COMMENT 'Объемы потребления (горячего водо.)',
  `NORM_F_4` DECIMAL(10,4) COMMENT 'Объемы потребления (холодное водо.)',
  `NORM_F_5` DECIMAL(10,4) COMMENT 'Объемы потребления (газоснабжение)',
  `NORM_F_6` DECIMAL(10,4) COMMENT 'Объемы потребления (электроэнергия)',
  `NORM_F_7` DECIMAL(10,4) COMMENT 'Объемы потребления (вывоз мусора)',
  `NORM_F_8` DECIMAL(10,4) COMMENT 'Объемы потребления (водоотведение)',
  `OWN_NUM_SR` VARCHAR(15) COMMENT 'Лицевой счет в обслуж. организации',
  `DAT1` DATE COMMENT 'Дата начала действия субсидии',
  `DAT2` DATE COMMENT 'Дата формирования запроса',
  `OZN_PRZ` INTEGER(1) COMMENT 'Признак (0 - автоматическое назначение, 1-для ручного расчета)',
  `DAT_F_1` DATE COMMENT 'Дата начала для факта',
  `DAT_F_2` DATE COMMENT 'Дата конца для факта',
  `DAT_FOP_1` DATE COMMENT 'Дата начала для факта отопления',
  `DAT_FOP_2` DATE COMMENT 'Дата конца для факта отопления',
  `ID_RAJ` VARCHAR(5) COMMENT 'Код района',
  `SUR_NAM` VARCHAR(30) COMMENT 'Фамилия',
  `F_NAM` VARCHAR(15) COMMENT 'Имя',
  `M_NAM` VARCHAR(20) COMMENT 'Отчество',
  `IND_COD` VARCHAR(10) COMMENT 'Идентификационный номер',
  `INDX` VARCHAR(6) COMMENT 'Индекс почтового отделения',
  `N_NAME` VARCHAR(30) COMMENT 'Название населенного пункта',
  `VUL_NAME` VARCHAR(30) COMMENT 'Название улицы',
  `BLD_NUM` VARCHAR(7) COMMENT 'Номер дома',
  `CORP_NUM` VARCHAR(2) COMMENT 'Номер корпуса',
  `FLAT` VARCHAR(9) COMMENT 'Номер квартиры',
  `CODE3_1` INTEGER(6) COMMENT 'Код тарифа оплаты жилья',
  `CODE3_2` INTEGER(6) COMMENT 'Код тарифа отопления',
  `CODE3_3` INTEGER(6) COMMENT 'Код тарифа горячего водоснабжения',
  `CODE3_4` INTEGER(6) COMMENT 'Код тарифа холодного водоснабжения',
  `CODE3_5` INTEGER(6) COMMENT 'Код тарифа - газоснабжение',
  `CODE3_6` INTEGER(6) COMMENT 'Код тарифа-электроэнергии',
  `CODE3_7` INTEGER(6) COMMENT 'Код тарифа - вывоз мусора',
  `CODE3_8` INTEGER(6) COMMENT 'Код тарифа - водоотведение',
  `OPP_SERV` VARCHAR(8) COMMENT 'Резерв',
  `RESERV1` BIGINT(10) COMMENT 'Резерв',
  `RESERV2` VARCHAR(10) COMMENT 'Резер',
  PRIMARY KEY (`id`),
  KEY `key_request_file_id` (`request_file_id`),
  KEY `key_account_number` (`account_number`),
  KEY `key_status` (`status`),
  KEY `key_internal_city_id` (`internal_city_id`),
  KEY `key_internal_street_id` (`internal_street_id`),
  KEY `key_internal_street_type_id` (`internal_street_type_id`),
  KEY `key_internal_building_id` (`internal_building_id`),
  KEY `key_internal_apartment_id` (`internal_apartment_id`),
  KEY `key_F_NAM` (`F_NAM`),
  KEY `key_M_NAM` (`M_NAM`),
  KEY `key_SUR_NAM` (`SUR_NAM`),
  KEY `key_N_NAME` (`N_NAME`),
  KEY `key_VUL_NAME` (`VUL_NAME`),
  KEY `key_BLD_NUM` (`BLD_NUM`),
  KEY `key_FLAT` (`FLAT`),
  KEY `key_OWN_NUM_SR` (`OWN_NUM_SR`),
  CONSTRAINT `fk_payment__request_file` FOREIGN KEY (`request_file_id`)  REFERENCES `request_file` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_payment__city` FOREIGN KEY (`internal_city_id`) REFERENCES `city` (`object_id`),
  CONSTRAINT `fk_payment__street` FOREIGN KEY (`internal_street_id`) REFERENCES `street` (`object_id`),
  CONSTRAINT `fk_payment__street_type` FOREIGN KEY (`internal_street_type_id`) REFERENCES `street_type` (`object_id`),
  CONSTRAINT `fk_payment__building` FOREIGN KEY (`internal_building_id`) REFERENCES `building` (`object_id`),
  CONSTRAINT `fk_payment__apartment` FOREIGN KEY (`internal_apartment_id`) REFERENCES `apartment` (`object_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Начисления';

-- ------------------------------
-- Benefit
-- ------------------------------
DROP TABLE IF EXISTS `benefit`;

CREATE TABLE `benefit` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор льготы',
  `request_file_id` BIGINT(20) NULL COMMENT 'Идентификатор файла запросов',
  `account_number` VARCHAR(100) NULL COMMENT 'Номер счета',
  `status` INTEGER NOT NULL DEFAULT 240 COMMENT 'Статус',

  `OWN_NUM` BIGINT(15) COMMENT 'Номер дела',
  `REE_NUM` INTEGER(2) COMMENT 'Номер реестра',
  `OWN_NUM_SR` VARCHAR(15) COMMENT 'Лицевой счет в обслуж. организации',
  `FAM_NUM` INTEGER(2) COMMENT 'Номер члена семьи',
  `SUR_NAM` VARCHAR(30) COMMENT 'Фамилия',
  `F_NAM` VARCHAR(15) COMMENT 'Имя',
  `M_NAM` VARCHAR(20) COMMENT 'Отчество',
  `IND_COD` VARCHAR(10) COMMENT 'Идентификационный номер',
  `PSP_SER` VARCHAR(6) COMMENT 'Серия паспорта',
  `PSP_NUM` VARCHAR(10) COMMENT 'Номер паспорта',
  `OZN` INTEGER(1) COMMENT 'Признак владельца',
  `CM_AREA` DECIMAL(10,2) COMMENT 'Общая площадь',
  `HEAT_AREA` DECIMAL(10,2) COMMENT 'Обогреваемая площадь',
  `OWN_FRM` INTEGER(6) COMMENT 'Форма собственности',
  `HOSTEL` INTEGER(2) COMMENT 'Количество комнат',
  `PRIV_CAT` INTEGER(3) COMMENT 'Категория льготы на платежи',
  `ORD_FAM` INTEGER(2) COMMENT 'Порядок семьи льготников для расчета платежей',
  `OZN_SQ_ADD` INTEGER(1) COMMENT 'Признак учета дополнительной площади',
  `OZN_ABS` INTEGER(1) COMMENT 'Признак отсутствия данных в базе ЖЭО',
  `RESERV1` DECIMAL(10,2) COMMENT 'Резерв',
  `RESERV2` VARCHAR(10) COMMENT 'Резерв',
  PRIMARY KEY (`id`),
  KEY `key_request_file_id` (`request_file_id`),
  KEY `key_account_number` (`account_number`),
  KEY `key_status` (`status`),
  KEY `key_F_NAM` (`F_NAM`),
  KEY `key_M_NAM` (`M_NAM`),
  KEY `key_SUR_NAM` (`SUR_NAM`),
  KEY `key_OWN_NUM_SR` (`OWN_NUM_SR`),
  CONSTRAINT `fk_benefit__request_file` FOREIGN KEY (`request_file_id`)  REFERENCES `request_file` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Льготы';

-- ------------------------------
-- Subsudy Tarif
-- ------------------------------
DROP TABLE IF EXISTS `subsidy_tarif`;

CREATE TABLE `subsidy_tarif` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор тарифа',
  `request_file_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор файла тарифов',
  `status` INTEGER NULL COMMENT 'Статус',

  `T11_DATA_T` DATE COMMENT '',
  `T11_DATA_E` DATE COMMENT '',
  `T11_DATA_R` DATE COMMENT '',
  `T11_MARK` INTEGER(3) COMMENT '',
  `T11_TARN` INTEGER(6) COMMENT '',
  `T11_CODE1` INTEGER(3) COMMENT '',
  `T11_CODE2` INTEGER(6) COMMENT '',
  `T11_COD_NA` VARCHAR(40) COMMENT '',
  `T11_CODE3` INTEGER(6) COMMENT '',
  `T11_NORM_U` DECIMAL(15, 4) COMMENT '',
  `T11_NOR_US` DECIMAL(15, 4) COMMENT '',
  `T11_CODE_N` INTEGER(3) COMMENT '',
  `T11_COD_ND` INTEGER(3) COMMENT '',
  `T11_CD_UNI` INTEGER(3) COMMENT '',
  `T11_CS_UNI` DECIMAL (15, 4) COMMENT '',
  `T11_NORM` DECIMAL (15, 4) COMMENT '',
  `T11_NRM_DO` DECIMAL (15, 4) COMMENT '',
  `T11_NRM_MA` DECIMAL (15, 4) COMMENT '',
  `T11_K_NADL` DECIMAL (15, 4) COMMENT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Тарифы';

-- ------------------------------
-- Actual Payment
-- ------------------------------

DROP TABLE IF EXISTS `actual_payment`;

CREATE TABLE `actual_payment` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор фактического начисления',
  `request_file_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор файла запросов',
  `account_number` VARCHAR(100) COMMENT 'Номер счета',

  `internal_city_id` BIGINT(20) COMMENT 'Идентификатор населенного пункта',
  `internal_street_id` BIGINT(20) COMMENT 'Идентификатор улицы',
  `internal_street_type_id` BIGINT(20) COMMENT 'Идентификатор типа улицы',
  `internal_building_id` BIGINT(20) COMMENT 'Идентификатор дома',

  `outgoing_city` VARCHAR(100) COMMENT 'Название населенного пункта используемое центром начисления',
  `outgoing_district` VARCHAR(100) COMMENT 'Название района используемое центром начисления',
  `outgoing_street` VARCHAR(100) COMMENT 'Название улицы используемое центром начисления',
  `outgoing_street_type` VARCHAR(100) COMMENT 'Название типа улицы используемое центром начисления',
  `outgoing_building_number` VARCHAR(100) COMMENT 'Номер дома используемый центром начисления',
  `outgoing_building_corp` VARCHAR(100) COMMENT 'Корпус используемый центром начисления',
  `outgoing_apartment` VARCHAR(100) COMMENT 'Номер квартиры. Не используется',

  `status` INTEGER NOT NULL DEFAULT 240 COMMENT 'Статус',

  `SUR_NAM` VARCHAR(30) COMMENT 'Фамилия',
  `F_NAM` VARCHAR(15) COMMENT 'Имя',
  `M_NAM` VARCHAR(20) COMMENT 'Отчество',
  `INDX` VARCHAR(6) COMMENT 'Индекс почтового отделения',
  `N_NAME` VARCHAR(30) COMMENT 'Название населенного пункта',
  `N_CODE` VARCHAR(5) COMMENT '',
  `VUL_CAT` VARCHAR(7) COMMENT 'Тип улицы',
  `VUL_NAME` VARCHAR(30) COMMENT 'Название улицы',
  `VUL_CODE` VARCHAR(5) COMMENT 'Код улицы',
  `BLD_NUM` VARCHAR(7) COMMENT 'Номер дома',
  `CORP_NUM` VARCHAR(2) COMMENT 'Номер корпуса',
  `FLAT` VARCHAR(9) COMMENT 'Номер квартиры',
  `OWN_NUM` VARCHAR (15) COMMENT 'Номер дела',
  `APP_NUM` VARCHAR(8) COMMENT '',
  `DAT_BEG` DATE COMMENT '',
  `DAT_END` DATE COMMENT '',
  `CM_AREA` DECIMAL(7,2) COMMENT '',
  `NM_AREA` DECIMAL(7,2) COMMENT '',
  `BLC_AREA` DECIMAL(5,2) COMMENT '',
  `FROG` DECIMAL(5,1) COMMENT '',
  `DEBT` DECIMAL(10,2) COMMENT '',
  `NUMB` INTEGER(2) COMMENT '',
  `P1` DECIMAL(10,4) COMMENT 'фактическое начисление',
  `N1` DECIMAL(10,4) COMMENT 'фактический тариф',
  `P2` DECIMAL(10,4) COMMENT '',
  `N2` DECIMAL(10,4) COMMENT '',
  `P3` DECIMAL(10,4) COMMENT '',
  `N3` DECIMAL(10,4) COMMENT '',
  `P4` DECIMAL(10,4) COMMENT '',
  `N4` DECIMAL(10,4) COMMENT '',
  `P5` DECIMAL(10,4) COMMENT '',
  `N5` DECIMAL(10,4) COMMENT '',
  `P6` DECIMAL(10,4) COMMENT '',
  `N6` DECIMAL(10,4) COMMENT '',
  `P7` DECIMAL(10,4) COMMENT '',
  `N7` DECIMAL(10,4) COMMENT '',
  `P8` DECIMAL(10,4) COMMENT '',
  `N8` DECIMAL(10,4) COMMENT '',

  PRIMARY KEY (`id`),
  KEY `key_request_file_id` (`request_file_id`),
  KEY `key_account_number` (`account_number`),
  KEY `key_status` (`status`),
  KEY `key_internal_city_id` (`internal_city_id`),
  KEY `key_internal_street_id` (`internal_street_id`),
  KEY `key_internal_street_type_id` (`internal_street_type_id`),
  KEY `key_internal_building_id` (`internal_building_id`),
  KEY `key_F_NAM` (`F_NAM`),
  KEY `key_M_NAM` (`M_NAM`),
  KEY `key_SUR_NAM` (`SUR_NAM`),
  KEY `key_N_NAME` (`N_NAME`),
  KEY `key_VUL_NAME` (`VUL_NAME`),
  KEY `key_BLD_NUM` (`BLD_NUM`),
  KEY `key_FLAT` (`FLAT`),
  CONSTRAINT `fk_actual_payment__request_file` FOREIGN KEY (`request_file_id`)  REFERENCES `request_file` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_actual_payment__city` FOREIGN KEY (`internal_city_id`) REFERENCES `city` (`object_id`),
  CONSTRAINT `fk_actual_payment__street` FOREIGN KEY (`internal_street_id`) REFERENCES `street` (`object_id`),
  CONSTRAINT `fk_actual_payment__street_type` FOREIGN KEY (`internal_street_type_id`) REFERENCES `street_type` (`object_id`),
  CONSTRAINT `fk_actual_payment__building` FOREIGN KEY (`internal_building_id`) REFERENCES `building` (`object_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Фактические начисления';

-- ------------------------------
-- Subsidy
-- ------------------------------

DROP TABLE IF EXISTS `subsidy`;

CREATE TABLE `subsidy` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор субсидии',
  `request_file_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор файла запросов',
  `account_number` VARCHAR(100) COMMENT 'Номер счета',

  `internal_city_id` BIGINT(20) COMMENT 'Идентификатор населенного пункта',
  `internal_street_id` BIGINT(20) COMMENT 'Идентификатор улицы',
  `internal_street_type_id` BIGINT(20) COMMENT 'Идентификатор типа улицы',
  `internal_building_id` BIGINT(20) COMMENT 'Идентификатор дома',

  `outgoing_city` VARCHAR(100) COMMENT 'Название населенного пункта используемое центром начисления',
  `outgoing_district` VARCHAR(100) COMMENT 'Название района используемое центром начисления',
  `outgoing_street` VARCHAR(100) COMMENT 'Название улицы используемое центром начисления',
  `outgoing_street_type` VARCHAR(100) COMMENT 'Название типа улицы используемое центром начисления',
  `outgoing_building_number` VARCHAR(100) COMMENT 'Номер дома используемый центром начисления',
  `outgoing_building_corp` VARCHAR(100) COMMENT 'Корпус используемый центром начисления',
  `outgoing_apartment` VARCHAR(100) COMMENT 'Номер квартиры. Не используется',

  `status` INTEGER NOT NULL DEFAULT 240 COMMENT 'Статус',

  `first_name` VARCHAR(100) COMMENT 'Имя',
  `last_name` VARCHAR(100) COMMENT 'Фамилия',
  `middle_name` VARCHAR(100) COMMENT 'Отчество',

  `FIO` VARCHAR(30) COMMENT 'ФИО',
  `ID_RAJ` VARCHAR(5) COMMENT 'Код района',
  `NP_CODE` VARCHAR(5) COMMENT 'Код населенного пункта',
  `NP_NAME`VARCHAR(30) COMMENT 'Название населенного пункта',
  `CAT_V` VARCHAR(7) COMMENT 'Тип улицы',
  `VULCOD` VARCHAR(8) COMMENT 'Код улицы',
  `NAME_V` VARCHAR(30) COMMENT 'Название улицы',
  `BLD` VARCHAR(7) COMMENT 'Номер дома',
  `CORP` VARCHAR(2) COMMENT 'Номер корпуса',
  `FLAT` VARCHAR(9) COMMENT 'Номер квартиры',
  `RASH` VARCHAR(15) COMMENT 'Номер л/с ПУ',
  `NUMB` VARCHAR(8),
  `DAT1` DATE COMMENT 'Дата начала периода, на который предоставляется субсидия',
  `DAT2` DATE COMMENT 'Дата конца периода, на который предоставляется субсидия',
  `NM_PAY` DECIMAL(9,2) COMMENT 'Начисление в пределах нормы',

  `P1` DECIMAL(9,4),
  `P2` DECIMAL(9,4),
  `P3` DECIMAL(9,4),
  `P4` DECIMAL(9,4),
  `P5` DECIMAL(9,4),
  `P6` DECIMAL(9,4),
  `P7` DECIMAL(9,4),
  `P8` DECIMAL(9,4),

  `SM1` DECIMAL(9,2),
  `SM2` DECIMAL(9,2),
  `SM3` DECIMAL(9,2),
  `SM4` DECIMAL(9,2),
  `SM5` DECIMAL(9,2),
  `SM6` DECIMAL(9,2),
  `SM7` DECIMAL(9,2),
  `SM8` DECIMAL(9,2),

  `SB1` DECIMAL(9,2),
  `SB2` DECIMAL(9,2),
  `SB3` DECIMAL(9,2),
  `SB4` DECIMAL(9,2),
  `SB5` DECIMAL(9,2),
  `SB6` DECIMAL(9,2),
  `SB7` DECIMAL(9,2),
  `SB8` DECIMAL(9,2),

  `OB1` DECIMAL(9,2),
  `OB2` DECIMAL(9,2),
  `OB3` DECIMAL(9,2),
  `OB4` DECIMAL(9,2),
  `OB5` DECIMAL(9,2),
  `OB6` DECIMAL(9,2),
  `OB7` DECIMAL(9,2),
  `OB8` DECIMAL(9,2),

  `SUMMA` DECIMAL(13,2),
  `NUMM` INTEGER(2),
  `SUBS` DECIMAL(13,2),
  `KVT` INTEGER(3),
  `MON` INTEGER(1),
  `EBK` VARCHAR(10),

  PRIMARY KEY (`id`),
  KEY `key_request_file_id` (`request_file_id`),
  KEY `key_account_number` (`account_number`),
  KEY `key_status` (`status`),
  KEY `key_internal_city_id` (`internal_city_id`),
  KEY `key_internal_street_id` (`internal_street_id`),
  KEY `key_internal_street_type_id` (`internal_street_type_id`),
  KEY `key_internal_building_id` (`internal_building_id`),
  KEY `key_FIO` (`FIO`),
  KEY `key_NP_NAME` (`NP_NAME`),
  KEY `key_NAME_V` (`NAME_V`),
  KEY `key_BLD` (`BLD`),
  KEY `key_CORP` (`CORP`),
  KEY `key_FLAT` (`FLAT`),
  CONSTRAINT `fk_subsidy__request_file` FOREIGN KEY (`request_file_id`)  REFERENCES `request_file` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_subsidy__city` FOREIGN KEY (`internal_city_id`) REFERENCES `city` (`object_id`),
  CONSTRAINT `fk_subsidy__street` FOREIGN KEY (`internal_street_id`) REFERENCES `street` (`object_id`),
  CONSTRAINT `fk_subsidy__street_type` FOREIGN KEY (`internal_street_type_id`) REFERENCES `street_type` (`object_id`),
  CONSTRAINT `fk_subsidy__building` FOREIGN KEY (`internal_building_id`) REFERENCES `building` (`object_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Файлы субсидий';

-- ------------------------------
-- Subsidy Split
-- ------------------------------
DROP TABLE IF EXISTS `subsidy_split`;
CREATE TABLE `subsidy_split` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор разбивки субсидии помесячно',
  `subsidy_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор субсидии',

  `DAT1` DATE COMMENT 'Дата начала периода',
  `DAT2` DATE COMMENT 'Дата конца периода',

  `SM1` DECIMAL(9,2),
  `SM2` DECIMAL(9,2),
  `SM3` DECIMAL(9,2),
  `SM4` DECIMAL(9,2),
  `SM5` DECIMAL(9,2),
  `SM6` DECIMAL(9,2),
  `SM7` DECIMAL(9,2),
  `SM8` DECIMAL(9,2),

  `SUMMA` DECIMAL(13,2),
  `NUMM` INTEGER(2),
  `SUBS` DECIMAL(13,2),

  PRIMARY KEY (`id`),
  KEY `key_subsidy_id` (`subsidy_id`),
  CONSTRAINT `fk_subsidy_split__subsidy` FOREIGN KEY (`subsidy_id`) REFERENCES `subsidy` (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Разбивка судсидий помесячно';

-- ------------------------------
-- Dwelling Characteristic
-- ------------------------------

DROP TABLE IF EXISTS `dwelling_characteristics`;

CREATE TABLE `dwelling_characteristics` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор объекта характеристик жилья',
  `request_file_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор файла запросов',
  `account_number` VARCHAR(100) COMMENT 'Номер счета',

  `internal_city_id` BIGINT(20) COMMENT 'Идентификатор населенного пункта',
  `internal_street_id` BIGINT(20) COMMENT 'Идентификатор улицы',
  `internal_street_type_id` BIGINT(20) COMMENT 'Идентификатор типа улицы',
  `internal_building_id` BIGINT(20) COMMENT 'Идентификатор дома',

  `outgoing_city` VARCHAR(100) COMMENT 'Название населенного пункта используемое центром начисления',
  `outgoing_district` VARCHAR(100) COMMENT 'Название района используемое центром начисления',
  `outgoing_street` VARCHAR(100) COMMENT 'Название улицы используемое центром начисления',
  `outgoing_street_type` VARCHAR(100) COMMENT 'Название типа улицы используемое центром начисления',
  `outgoing_building_number` VARCHAR(100) COMMENT 'Номер дома используемый центром начисления',
  `outgoing_building_corp` VARCHAR(100) COMMENT 'Корпус используемый центром начисления',
  `outgoing_apartment` VARCHAR(100) COMMENT 'Номер квартиры. Не используется',

  `date` DATE NOT NULL COMMENT 'Дата. Производное поле. Первое число месяца, который был указан при загрузке файла, содержащего данную запись.',

  `status` INTEGER NOT NULL DEFAULT 240 COMMENT 'Статус',

  `first_name` VARCHAR(100) COMMENT 'Имя',
  `last_name` VARCHAR(100) COMMENT 'Фамилия',
  `middle_name` VARCHAR(100) COMMENT 'Отчество',
  `city` VARCHAR(100) NOT NULL COMMENT 'Населенный пункт. Исскуственное поле(значение берется из конфигураций)',

  `COD` INTEGER(4) COMMENT 'Код района',
  `CDPR` BIGINT(12) COMMENT 'Код ЄДРПОУ (ОГРН) организации',
  `NCARD` BIGINT(10) COMMENT 'Идентификатор льготника',
  `IDCODE` VARCHAR(10) COMMENT 'ИНН собственника жилья/льготника (ставят ИНН льготника)',
  `PASP` VARCHAR(14) COMMENT 'Серия и номер паспорта собственника жилья/льготника (ставят паспорт льготника)',
  `FIO` VARCHAR(50) COMMENT 'ФИО собственника жилья/льготника (ставят ФИО льготника)',
  `IDPIL` VARCHAR(10) COMMENT 'ИНН льготника',
  `PASPPIL` VARCHAR(14) COMMENT 'Серия и номер паспорта льготника',
  `FIOPIL` VARCHAR(50) COMMENT 'ФИО льготника',
  `INDEX` INTEGER(6) COMMENT 'Почтовый индекс',
  `CDUL` INTEGER(5) COMMENT 'Код улицы',
  `HOUSE` VARCHAR(7) COMMENT 'Номер дома',
  `BUILD` VARCHAR(2) COMMENT 'Корпус',
  `APT` VARCHAR(4) COMMENT 'Номер квартиры',
  `VL` INTEGER(3) COMMENT 'Тип собственности',
  `PLZAG` DECIMAL(8,2) COMMENT 'Общая площадь помещения',
  `PLOPAL` DECIMAL(8,2) COMMENT 'Отапливаемая площадь помещения',

  PRIMARY KEY (`id`),
  KEY `key_request_file_id` (`request_file_id`),
  KEY `key_account_number` (`account_number`),
  KEY `key_status` (`status`),
  KEY `key_internal_city_id` (`internal_city_id`),
  KEY `key_internal_street_id` (`internal_street_id`),
  KEY `key_internal_street_type_id` (`internal_street_type_id`),
  KEY `key_internal_building_id` (`internal_building_id`),
  KEY `key_FIO` (`FIO`),
  KEY `key_CDUL` (`CDUL`),
  KEY `key_HOUSE` (`HOUSE`),
  KEY `key_BUILD` (`BUILD`),
  KEY `key_APT` (`APT`),
  CONSTRAINT `fk_dwelling_characteristics__request_file` FOREIGN KEY (`request_file_id`)  REFERENCES `request_file` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_dwelling_characteristics__city` FOREIGN KEY (`internal_city_id`) REFERENCES `city` (`object_id`),
  CONSTRAINT `fk_dwelling_characteristics__street` FOREIGN KEY (`internal_street_id`) REFERENCES `street` (`object_id`),
  CONSTRAINT `fk_dwelling_characteristics__street_type` FOREIGN KEY (`internal_street_type_id`) REFERENCES `street_type` (`object_id`),
  CONSTRAINT `fk_dwelling_characteristics__building` FOREIGN KEY (`internal_building_id`) REFERENCES `building` (`object_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Файлы-запросы характеристик жилья';

-- ------------------------------
-- Facility Service Type
-- ------------------------------

DROP TABLE IF EXISTS `facility_service_type`;

CREATE TABLE `facility_service_type` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор объекта вид услуги',
  `request_file_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор файла запросов',
  `account_number` VARCHAR(100) COMMENT 'Номер счета',

  `internal_city_id` BIGINT(20) COMMENT 'Идентификатор населенного пункта',
  `internal_street_id` BIGINT(20) COMMENT 'Идентификатор улицы',
  `internal_street_type_id` BIGINT(20) COMMENT 'Идентификатор типа улицы',
  `internal_building_id` BIGINT(20) COMMENT 'Идентификатор дома',

  `outgoing_city` VARCHAR(100) COMMENT 'Название населенного пункта используемое центром начисления',
  `outgoing_district` VARCHAR(100) COMMENT 'Название района используемое центром начисления',
  `outgoing_street` VARCHAR(100) COMMENT 'Название улицы используемое центром начисления',
  `outgoing_street_type` VARCHAR(100) COMMENT 'Название типа улицы используемое центром начисления',
  `outgoing_building_number` VARCHAR(100) COMMENT 'Номер дома используемый центром начисления',
  `outgoing_building_corp` VARCHAR(100) COMMENT 'Корпус используемый центром начисления',
  `outgoing_apartment` VARCHAR(100) COMMENT 'Номер квартиры. Не используется',

  `date` DATE NOT NULL COMMENT 'Дата. Производное поле. Первое число месяца, который был указан при загрузке файла, содержащего данную запись.',

  `status` INTEGER NOT NULL DEFAULT 240 COMMENT 'Статус',

  `first_name` VARCHAR(100) COMMENT 'Имя',
  `last_name` VARCHAR(100) COMMENT 'Фамилия',
  `middle_name` VARCHAR(100) COMMENT 'Отчество',

  `city` VARCHAR(100) NOT NULL COMMENT 'Населенный пункт. Исскуственное поле(значение берется из конфигураций)',

  `COD` INTEGER(4) COMMENT 'Код района',
  `CDPR` BIGINT(12) COMMENT 'Код ЄДРПОУ (ОГРН) организации',
  `NCARD` BIGINT(10) COMMENT 'Идентификатор льготника',
  `IDCODE` VARCHAR(10) COMMENT 'ИНН собственника жилья/льготника (ставят ИНН льготника)',
  `PASP` VARCHAR(14) COMMENT 'Серия и номер паспорта собственника жилья/льготника (ставят паспорт льготника)',
  `FIO` VARCHAR(50) COMMENT 'ФИО собственника жилья/льготника (ставят ФИО льготника)',
  `IDPIL` VARCHAR(10) COMMENT 'ИНН льготника',
  `PASPPIL` VARCHAR(14) COMMENT 'Серия и номер паспорта льготника',
  `FIOPIL` VARCHAR(50) COMMENT 'ФИО льготника',
  `INDEX` INTEGER(6) COMMENT 'Почтовый индекс',
  `CDUL` INTEGER(5) COMMENT 'Код улицы',
  `HOUSE` VARCHAR(7) COMMENT 'Номер дома',
  `BUILD` VARCHAR(2) COMMENT 'Корпус',
  `APT` VARCHAR(4) COMMENT 'Номер квартиры',
  `KAT` INTEGER(4) COMMENT 'Категория льготы ЕДАРП',
  `LGCODE` INTEGER(4) COMMENT 'Код возмещения',
  `YEARIN` INTEGER(4) COMMENT 'Год начала действия льготы',
  `MONTHIN` INTEGER(2) COMMENT 'Месяц начала действия льготы',
  `YEAROUT` INTEGER(4) COMMENT 'Год окончания действия льготы',
  `MONTHOUT` INTEGER(2) COMMENT 'Месяц окончания действия льготы',
  `RAH` VARCHAR(25) COMMENT 'Номер л/с ПУ',
  `RIZN` INTEGER(6) COMMENT 'Тип услуги',
  `TARIF` BIGINT(10) COMMENT 'Код тарифа услуги',

  PRIMARY KEY (`id`),
  KEY `key_request_file_id` (`request_file_id`),
  KEY `key_account_number` (`account_number`),
  KEY `key_status` (`status`),
  KEY `key_internal_city_id` (`internal_city_id`),
  KEY `key_internal_street_id` (`internal_street_id`),
  KEY `key_internal_street_type_id` (`internal_street_type_id`),
  KEY `key_internal_building_id` (`internal_building_id`),
  KEY `key_FIO` (`FIO`),
  KEY `key_CDUL` (`CDUL`),
  KEY `key_HOUSE` (`HOUSE`),
  KEY `key_BUILD` (`BUILD`),
  KEY `key_APT` (`APT`),
  CONSTRAINT `fk_facility_service_type__request_file` FOREIGN KEY (`request_file_id`)  REFERENCES `request_file` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_facility_service_type__city` FOREIGN KEY (`internal_city_id`) REFERENCES `city` (`object_id`),
  CONSTRAINT `fk_facility_service_type__street` FOREIGN KEY (`internal_street_id`) REFERENCES `street` (`object_id`),
  CONSTRAINT `fk_facility_service_type__street_type` FOREIGN KEY (`internal_street_type_id`) REFERENCES `street_type` (`object_id`),
  CONSTRAINT `fk_facility_service_type__building` FOREIGN KEY (`internal_building_id`) REFERENCES `building` (`object_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Файлы-запросы видов услуг';

-- ------------------------------
-- Facility Form-2
-- ------------------------------

DROP TABLE IF EXISTS `facility_form2`;

CREATE TABLE `facility_form2` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор объекта форма-2 льгота',
  `request_file_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор файла запросов',

  `account_number` VARCHAR(100) COMMENT 'Номер счета',
  `status` INTEGER NOT NULL DEFAULT 240 COMMENT 'Статус',

  `CDPR` BIGINT(12) COMMENT 'Код ЄДРПОУ (ОГРН) организации',
  `IDCODE` VARCHAR(10) COMMENT 'ИНН льготника',
  `FIO` VARCHAR(50) COMMENT 'ФИО льготника',
  `PPOS` VARCHAR(15) COMMENT '0',
  `RS` VARCHAR(25) COMMENT 'Номер л/с ПУ',
  `YEARIN` INTEGER(4) COMMENT 'Год выгрузки данных',
  `MONTHIN` INTEGER(2) COMMENT 'Месяц выгрузки данных',
  `LGCODE` INTEGER(4) COMMENT 'Код льготы',
  `DATA1` DATE COMMENT 'Дата начала периода',
  `DATA2` DATE COMMENT 'Дата окончания периода',
  `LGKOL` INTEGER(2) COMMENT 'Кол-во пользующихся льготой',
  `LGKAT` VARCHAR(3) COMMENT 'Категория льготы ЕДАРП',
  `LGPRC` INTEGER(3) COMMENT 'Процент льготы',
  `SUMM` DECIMAL(8,2) COMMENT 'Сумма возмещения',
  `FACT` DECIMAL(19,6) COMMENT 'Объем фактического потребления (для услуг со счетчиком)',
  `TARIF` DECIMAL(14,7) COMMENT 'Ставка тарифа',
  `FLAG` INTEGER(1) COMMENT '',

  `DEPART` INTEGER COMMENT 'Код участка',

  PRIMARY KEY (`id`),
  KEY `key_request_file_id` (`request_file_id`),
  KEY `key_account_number` (`account_number`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_facility_form2__request_file` FOREIGN KEY (`request_file_id`)  REFERENCES `request_file` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Файлы форма-2 льгота';

-- ------------------------------
-- Facility Local
-- ------------------------------

DROP TABLE IF EXISTS `facility_local`;

CREATE TABLE `facility_local` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор объекта местной льготы',
  `request_file_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор файла запросов',

  `account_number` VARCHAR(100) COMMENT 'Номер счета',
  `status` INTEGER NOT NULL DEFAULT 240 COMMENT 'Статус',

  `COD` INTEGER(4) COMMENT 'Код района',
  `CDPR` BIGINT(12) COMMENT 'Код ОКПО балансодержателя',
  `NCARD` BIGINT(10) COMMENT 'NULL',
  `IDCODE` VARCHAR(10) COMMENT 'ИНН ответственного по л/с',
  `PASP` VARCHAR(14) COMMENT 'Серия и номер паспорта ответственного',
  `FIO` VARCHAR(50) COMMENT 'ФИО ответственного',
  `IDPIL` VARCHAR(10) COMMENT 'ИНН носителя льготы',
  `PASPPIL` VARCHAR(14) COMMENT 'Серия и номер паспорта носителя льготы',
  `FIOPIL` VARCHAR(50) COMMENT 'ФИО носителя льготы',
  `INDEX` INTEGER(6) COMMENT 'NULL',
  `NAMUL` VARCHAR(20) COMMENT 'Название и тип улицы',
  `CDUL` INTEGER(5) COMMENT 'Код улицы в СЗ',
  `HOUSE` VARCHAR(7) COMMENT 'Номер дома',
  `BUILD` VARCHAR(2) COMMENT 'Часть дома',
  `APT` VARCHAR(4) COMMENT 'Номер квартиры',
  `LGCODE` INTEGER(4) COMMENT '500 (для ЖКС) или 5061 (для мусора)',
  `KAT` INTEGER(4) COMMENT 'Код льготной категории',
  `YEARIN` INTEGER(4) COMMENT 'Год формирования',
  `MONTHIN` INTEGER(2) COMMENT 'Месяц формирования',
  `YEAR` INTEGER(4) COMMENT 'Расчетный год',
  `MONTH` INTEGER(2) COMMENT 'Расчетный месяц',
  `RAH` BIGINT(10) COMMENT 'Номер л/с',
  `RIZN` INTEGER(6) COMMENT 'NULL',
  `TARIF` BIGINT(10) COMMENT 'NULL',
  `VL` INTEGER(3) COMMENT 'NULL',
  `PLZAG` DECIMAL(6,2) COMMENT 'Общая площадь (ЖКС) или NULL (мусор)',
  `PLPIL` DECIMAL(6,2) COMMENT 'Льготная площадь (ЖКС) или NULL (мусор)',
  `TARIFS` DECIMAL(10,2) COMMENT '0.37 (при наличии услуг 050 или 160 в ЖКС) 0.28 (в остальных случаях в ЖКС) NULL (в мусоре)',
  `TARIFN` DECIMAL(10,3) COMMENT 'Тариф',
  `SUM` DECIMAL(10,2) COMMENT 'Возмещение текущего месяца',
  `LGKOL` INTEGER(2) COMMENT 'Количество пользующихся льготой',
  `SUMPER` DECIMAL(10,2) COMMENT 'Сумма перерасчетов возмещения',
  `DATN` DATE COMMENT 'Начало расчетного периода (текущего)',
  `DATK` DATE COMMENT 'Окончание расчетного периода (текущего)',
  `PSN` INTEGER(2) COMMENT 'Причина снятия льготы',
  `SUMM` DECIMAL(10,2) COMMENT 'Итоговая сумма (текущее возмещение и перерасчеты)',

  `DEPART` INTEGER COMMENT 'Код участка',

  PRIMARY KEY (`id`),
  KEY `key_request_file_id` (`request_file_id`),
  KEY `key_account_number` (`account_number`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_facility_local__request_file` FOREIGN KEY (`request_file_id`)  REFERENCES `request_file` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Записи местной льготы';

-- ------------------------------
-- Facility Street Type Reference
-- ------------------------------

DROP TABLE IF EXISTS `facility_street_type_reference`;

CREATE TABLE `facility_street_type_reference` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор объекта тип улицы',
  `request_file_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор файла типов улиц',
  `status` INTEGER NULL COMMENT 'Код статуса. Статус',

  `KLKUL_CODE` VARCHAR(100) COMMENT 'Код типа улицы',
  `KLKUL_NAME` VARCHAR(100) COMMENT 'Наименование типа улицы',

  PRIMARY KEY (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Файлы-справочники типов улиц';

-- ------------------------------
-- Facility Street Reference
-- ------------------------------

DROP TABLE IF EXISTS `facility_street_reference`;

CREATE TABLE `facility_street_reference` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор объекта улица',
  `request_file_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор файла улиц',
  `status` INTEGER NULL COMMENT 'Статус',

  `KL_CODERN` VARCHAR(100) COMMENT 'Код района',
  `KL_CODEUL` VARCHAR(100) COMMENT 'Код улицы',
  `KL_NAME` VARCHAR(100) COMMENT 'Наименование улицы',
  `KL_CODEKUL` VARCHAR(100) COMMENT 'Код типа улицы',

  PRIMARY KEY (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Файлы-справочники улиц';

-- ------------------------------
-- Facility Tarif Reference
-- ------------------------------

DROP TABLE IF EXISTS `facility_tarif_reference`;

CREATE TABLE `facility_tarif_reference` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор объекта тариф',
  `request_file_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор файла тарифов',
  `status` INTEGER NULL COMMENT 'Статус',

  `TAR_CODE` BIGINT(10) COMMENT 'Код тарифа',
  `TAR_CDPLG` BIGINT(10) COMMENT 'Код услуги',
  `TAR_SERV` BIGINT(10) COMMENT 'Номер тарифа в услуге',
  `TAR_DATEB` DATE COMMENT 'Дата начала действия тарифа',
  `TAR_DATEE` DATE COMMENT 'Дата окончания действия тарифа',
  `TAR_COEF` DECIMAL(11,2) COMMENT '',
  `TAR_COST` DECIMAL(14,7) COMMENT 'Ставка тарифа',
  `TAR_UNIT` BIGINT(10) COMMENT '',
  `TAR_METER` INTEGER(3) COMMENT '',
  `TAR_NMBAS` DECIMAL(11,2) COMMENT '',
  `TAR_NMSUP` DECIMAL(11,2) COMMENT '',
  `TAR_NMUBS` DECIMAL(22,6) COMMENT '',
  `TAR_NMUSP` DECIMAL(22,6) COMMENT '',
  `TAR_NMUMX` DECIMAL(11,4) COMMENT '',
  `TAR_TPNMB` BIGINT(10) COMMENT '',
  `TAR_TPNMS` BIGINT(10) COMMENT '',
  `TAR_NMUPL` INTEGER(3) COMMENT '',
  `TAR_PRIV` BIGINT(10) COMMENT '',

  PRIMARY KEY (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Файлы-справочники тарифов для запросов по льготам';

-- ------------------------------
-- Person Account
-- ------------------------------
DROP TABLE IF EXISTS `person_account`;

CREATE TABLE `person_account` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор счета абонента',
  `first_name`VARCHAR(100) NOT NULL COMMENT 'Имя',
  `middle_name` VARCHAR(100) NOT NULL COMMENT 'Отчество',
  `last_name` VARCHAR(100) NOT NULL COMMENT 'Фамилия',

  `city` VARCHAR(100) COMMENT 'Населенный пункт',
  `street_type` VARCHAR(50) COMMENT 'Тип улицы',
  `street` VARCHAR(100) COMMENT 'Улица',
  `building_number` VARCHAR(20) COMMENT 'Номер дома',
  `building_corp` VARCHAR(20) COMMENT 'Корпус',
  `apartment` VARCHAR(20) COMMENT 'Номер квартиры',

  `city_object_id` BIGINT(20) COMMENT 'Идентификатор населенного пункта',
  `street_object_id` BIGINT(20) COMMENT 'Идентификатор улицы',
  `street_type_object_id` BIGINT(20) COMMENT 'Идентификатор типа улицы',
  `building_object_id` BIGINT(20) COMMENT 'Идентификатор дома',
  `apartment_object_id` BIGINT (20) COMMENT 'Идентификатор квартиры',

  `account_number` VARCHAR(100) NOT NULL COMMENT 'Счет абонента',
  `pu_account_number` VARCHAR(100) NOT NULL COMMENT 'Личный счет в обслуживающей организации',
  `organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор отдела соц. защиты населения',
  `user_organization_id` BIGINT(20),
  `calc_center_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор центра начислений',

  `created` TIMESTAMP DEFAULT current_timestamp COMMENT 'Время создания',

  PRIMARY KEY (`id`),

  KEY `key_city_object_id` (`city_object_id`),
  KEY `key_street_object_id` (`street_object_id`),
  KEY `key_street_type_object_id` (`street_type_object_id`),
  KEY `key_building_object_id` (`building_object_id`),
  KEY `key_apartment_object_id` (`apartment_object_id`),

  KEY `key_organization_id` (`organization_id`),
  KEY `key_calc_center_id` (`calc_center_id`),
  KEY `key_user_organization_id` (`user_organization_id`),

  KEY `key_first_name` (first_name),
  KEY `key_middle_name` (middle_name),
  KEY `key_last_name` (last_name),
  KEY `key_city` (city),
  KEY `key_street_type` (street_type),
  KEY `key_street` (street),
  KEY `key_building_number` (building_number),
  KEY `key_building_corp` (building_corp),
  KEY `key_apartment` (apartment),
  KEY `key_pu_account_number` (pu_account_number),

  CONSTRAINT `fk_person_account__city` FOREIGN KEY (`city_object_id`) REFERENCES `city` (`object_id`),
  CONSTRAINT `fk_person_account__street` FOREIGN KEY (`street_object_id`) REFERENCES `street` (`object_id`),
  CONSTRAINT `fk_person_account__street_type` FOREIGN KEY (`street_type_object_id`) REFERENCES `street_type` (`object_id`),
  CONSTRAINT `fk_person_account__building` FOREIGN KEY (`building_object_id`) REFERENCES `building` (`object_id`),
  CONSTRAINT `fk_person_account__apartment` FOREIGN KEY (`apartment_object_id`) REFERENCES `apartment` (`object_id`),

  CONSTRAINT `fk_person_account__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_person_account__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_person_account__calc_center_organization` FOREIGN KEY (`calc_center_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Соответствие лицевого счета';

DROP TABLE IF EXISTS `ownership_correction`;

CREATE TABLE `ownership_correction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `external_id` VARCHAR(20) COMMENT 'Код организации',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта формы собственности',
  `correction` VARCHAR(100) NOT NULL COMMENT 'Название формы собственности',
  `start_date` DATE NOT NULL DEFAULT '1970-01-01' COMMENT 'Дата начала актуальности соответствия',
  `end_date` DATE NOT NULL DEFAULT '2054-12-31' COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор организации',
  `user_organization_id` BIGINT(20),
  `module_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор внутренней организации',
  `status` INTEGER COMMENT 'Статус',
  PRIMARY KEY (`id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_ownership_correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_ownership_correction` FOREIGN KEY (`object_id`) REFERENCES `ownership` (`object_id`),
  CONSTRAINT `fk_ownership_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Коррекция формы собственности';

DROP TABLE IF EXISTS `privilege_correction`;

CREATE TABLE `privilege_correction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
  `external_id` VARCHAR(20) COMMENT 'Внешний идентификатор объекта',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта льготы',
  `correction` VARCHAR(100) NOT NULL COMMENT 'Название льготы',
  `start_date` DATE NOT NULL DEFAULT '1970-01-01' COMMENT 'Дата начала актуальности соответствия',
  `end_date` DATE NOT NULL DEFAULT '2054-12-31' COMMENT 'Дата окончания актуальности соответствия',
  `organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор организации',
  `user_organization_id` BIGINT(20),
  `module_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор модуля',
  `status` INTEGER COMMENT 'Статус',
  PRIMARY KEY (`id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_correction` (`correction`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_user_organization_id` (`user_organization_id`),
  KEY `key_module_id` (`module_id`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_privilege_correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_privilege_correction` FOREIGN KEY (`object_id`) REFERENCES `privilege` (`object_id`),
  CONSTRAINT `fk_privilege_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Коррекция льгот';

-- ------------------------------
-- Status Descriptions. Read only, use only for reports.
-- ------------------------------

DROP TABLE IF EXISTS `status_description`;

CREATE TABLE `status_description` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `code` INTEGER NOT NULL COMMENT 'Код описания статуса',
  `name` VARCHAR(500) NOT NULL COMMENT 'Описание статуса',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_status_description` (`code`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Описание статутов';

-- ------------------------------
-- Type Descriptions. Read only, use only for reports.
-- ------------------------------

DROP TABLE IF EXISTS `type_description`;

CREATE TABLE `type_description` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `code` INTEGER NOT NULL COMMENT 'Код описания типа',
  `name` VARCHAR(500) NOT NULL COMMENT 'Описание типа',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_description` (`code`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Описание типов';

-- ------------------------------
-- Request Warning
-- ------------------------------

DROP TABLE IF EXISTS `request_warning`;

CREATE TABLE `request_warning` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор предупреждения',
  `request_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор файла запроса',
  `request_file_type` INTEGER NOT NULL COMMENT 'Типа файла запроса. См. RequestFileType',
  `status` BIGINT(20) NOT NULL COMMENT 'Код статуса. См. класс RequestWarningStatus',
  PRIMARY KEY (`id`),
  KEY `key_request_warning__request` (`request_id`),
  KEY `key_request_warning__request_file` (`request_file_type`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Расширенные сообщения предупреждений обработки файлов запросов';

DROP TABLE IF EXISTS `request_warning_parameter`;

CREATE TABLE `request_warning_parameter` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор параметра предупреждений',
  `request_warning_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор предупреждения',
  `order` INTEGER NOT NULL COMMENT 'Порядок',
  `type` VARCHAR(100) NULL COMMENT 'Тип',
  `value` VARCHAR(500) NOT NULL COMMENT 'Значение',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_request_warning_parameter` (`request_warning_id`, `order`),
  CONSTRAINT `fk_request_warning_parameter__request_warning` FOREIGN KEY (`request_warning_id`) REFERENCES `request_warning` (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Параметры предупреждений';

-- ------------------------------
-- Request File Description
-- ------------------------------
DROP TABLE IF EXISTS `request_file_description`;

CREATE TABLE `request_file_description` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `request_file_type` VARCHAR(50) NOT NULL COMMENT 'Тип файла запроса',
  `date_pattern` VARCHAR(50) NOT NULL COMMENT 'Шаблон значений типа дата',
  PRIMARY KEY (`id`),
  KEY `key_request_file_type` (`request_file_type`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Описание структуры файлов запросов';

DROP TABLE IF EXISTS `request_file_field_description`;

CREATE TABLE `request_file_field_description` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `request_file_description_id` BIGINT(20) NOT NULL COMMENT 'Ссылка на описание структуры файла запроса',
  `name` VARCHAR(100) NOT NULL COMMENT 'Имя поля',
  `type` VARCHAR(100) NOT NULL COMMENT 'Java тип значений поля',
  `length` INTEGER NOT NULL COMMENT 'Длина поля',
  `scale` INTEGER COMMENT 'Количество знаков после запятой, если тип поля дробный',
  PRIMARY KEY (`id`),
  KEY `key_request_file_description_id` (`request_file_description_id`),
  CONSTRAINT `fk_request_file_field_description__request_file_description_id` FOREIGN KEY (`request_file_description_id`) REFERENCES `request_file_description` (`id`)
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Описание структуры поля файла запросов';

-- ------------------------------
-- Request File Status
-- ------------------------------

DROP TABLE IF EXISTS `request_file_history`;

CREATE TABLE `request_file_history` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `request_file_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор файла запроса',
  `status` INTEGER NOT NULL COMMENT 'Статус файла запроса',
  `date` TIMESTAMP NOT NULL COMMENT 'Дата утановки статуса',
  PRIMARY KEY (`id`),
  KEY `key_request_file_id` (`request_file_id`),
  KEY `status` (`status`),
  KEY `date` (`date`),
  CONSTRAINT `fk_request_file_history__request_file` FOREIGN KEY (`request_file_id`)  REFERENCES `request_file` (`id`) ON DELETE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'История файла запроса';

-- ------------------------------
-- Privilege Prolongation
-- ------------------------------

DROP TABLE IF EXISTS `privilege_prolongation`;

CREATE TABLE `privilege_prolongation` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `request_file_id` BIGINT(20) NULL COMMENT 'Идентификатор файла запросов',
  `account_number` VARCHAR(100) NULL COMMENT 'Номер счета',
  `status` INTEGER NOT NULL DEFAULT 240 COMMENT 'Код статуса',

  `internal_city_id` BIGINT(20) COMMENT 'Идентификатор населенного пункта',
  `internal_street_id` BIGINT(20) COMMENT 'Идентификатор улицы',
  `internal_street_type_id` BIGINT(20) COMMENT 'Идентификатор типа улицы',
  `internal_building_id` BIGINT(20) COMMENT 'Идентификатор дома',

  `outgoing_city` VARCHAR(100) COMMENT 'Название населенного пункта используемое центром начисления',
  `outgoing_district` VARCHAR(100) COMMENT 'Название района используемое центром начисления',
  `outgoing_street` VARCHAR(100) COMMENT 'Название улицы используемое центром начисления',
  `outgoing_street_type` VARCHAR(100) COMMENT 'Название типа улицы используемое центром начисления',
  `outgoing_building_number` VARCHAR(100) COMMENT 'Номер дома используемый центром начисления',
  `outgoing_building_corp` VARCHAR(100) COMMENT 'Корпус используемый центром начисления',
  `outgoing_apartment` VARCHAR(100) COMMENT 'Номер квартиры. Не используется',

  `date` DATE NOT NULL COMMENT 'Дата',

  `first_name` VARCHAR(100) COMMENT 'Имя',
  `last_name` VARCHAR(100) COMMENT 'Фамилия',
  `middle_name` VARCHAR(100) COMMENT 'Отчество',
  `city` VARCHAR(100) NOT NULL COMMENT 'Населенный пункт',

  `COD` INTEGER(4) COMMENT 'Код ОСЗН',
  `CDPR` BIGINT(12) COMMENT 'ЕДРПОУ код предприятия',
  `NCARD` BIGINT(10) COMMENT 'Номер дела в ОСЗН',
  `IDPIL` VARCHAR(10) COMMENT 'Иден.код льготника',
  `PASPPIL` VARCHAR(14) COMMENT 'Паспорт льготника',
  `FIOPIL` VARCHAR(50) COMMENT 'ФИО льготника',
  `INDEX` INTEGER(6) COMMENT 'Почтовый индекс',
  `CDUL` INTEGER(5) COMMENT 'Код улицы',
  `HOUSE` VARCHAR(7) COMMENT 'Номер дома',
  `BUILD` VARCHAR(2) COMMENT 'Номер корпуса',
  `APT` VARCHAR(4) COMMENT 'Номер квартиры',
  `KAT` INTEGER(4) COMMENT 'Код льготы ЕДАРП',
  `LGCODE` INTEGER(4) COMMENT 'Код услуги',
  `DATEIN` VARCHAR(10) COMMENT 'Дата начала действия льготы',
  `DATEOUT` VARCHAR(10) COMMENT 'Дата окончания действия льготы',
  `RAH` VARCHAR(25) COMMENT 'Номер л/с ПУ',
  `MONEY` INTEGER(1),
  `EBK` VARCHAR(10),

  PRIMARY KEY (`id`),
  KEY `key_request_file_id` (`request_file_id`),
  KEY `key_account_number` (`account_number`),
  CONSTRAINT `fk_privilege_prolongation__request_file` FOREIGN KEY (`request_file_id`)  REFERENCES `request_file` (`id`) ON DELETE CASCADE
)ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Файлы продления льгот';

-- ------------------------------
-- Oschadbank request file
-- ------------------------------
DROP TABLE IF EXISTS `oschadbank_request_file`;
CREATE TABLE `oschadbank_request_file` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор файла запросов от ощадбанка',
  `request_file_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор файла запросов',
  `EDRPOU` VARCHAR(16) COMMENT 'ЕДРПОУ',
  `PROVIDER_NAME` VARCHAR(128) COMMENT 'Название поставщика',
  `DOCUMENT_NUMBER` VARCHAR(64) COMMENT '№ Анкеты',
  `SERVICE_NAME` VARCHAR(1024) COMMENT 'Название услуги',
  `REPORTING_PERIOD` VARCHAR(16) COMMENT 'Отчетный период',
  `PROVIDER_CODE` VARCHAR(16) COMMENT 'Код Банка Поставщика услуги',
  `PROVIDER_ACCOUNT` VARCHAR(32) COMMENT 'р/с Поставщика услуги',
  `PROVIDER_IBAN` VARCHAR(64) COMMENT 'IBAN Поставщика',
  PRIMARY KEY (`id`),
  KEY `key_request_file_id` (`request_file_id`),
  CONSTRAINT `fk_oschadbank_request_file__request_file` FOREIGN KEY (`request_file_id`)  REFERENCES `request_file` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB CHARSET = utf8 COLLATE = utf8_unicode_ci COMMENT 'Файл запроса от ощадбанка';

-- ------------------------------
-- Oschadbank request
-- ------------------------------
DROP TABLE IF EXISTS `oschadbank_request`;
CREATE TABLE `oschadbank_request` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор запросов от ощадбанка',
  `request_file_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор файла запросов',
  `account_number` VARCHAR(100) NULL COMMENT 'Номер счета',
  `status` INTEGER NOT NULL DEFAULT 240 COMMENT 'Статус',

  `UTSZN` VARCHAR(64) COMMENT 'Номер УТСЗН',
  `OSCHADBANK_ACCOUNT` VARCHAR(64) COMMENT 'Номер учетной записи получателя жилищной субсидии в АО «Ощадбанк»',
  `FIO` VARCHAR(128) COMMENT 'ФИО получателя субсидии',
  `SERVICE_ACCOUNT` VARCHAR(64) COMMENT 'Номер лицевого счета у поставщика',
  `MONTH_SUM` DECIMAL(13,2) COMMENT 'Общая начисленная сумма за потребленные услуги в отчетном месяце (грн.)',
  `SUM` DECIMAL(13,2) COMMENT 'Общая сумма к оплате, включающая задолженность / переплату за предыдущие периоды (грн.)',
  PRIMARY KEY (`id`),
  KEY `key_request_file_id` (`request_file_id`),
  CONSTRAINT `fk_oschadbank_request__request_file` FOREIGN KEY (`request_file_id`)  REFERENCES `request_file` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB CHARSET = utf8 COLLATE = utf8_unicode_ci COMMENT 'Запросы от ощадбанка';

-- ------------------------------
-- Oschadbank response file
-- ------------------------------
DROP TABLE IF EXISTS `oschadbank_response_file`;
CREATE TABLE `oschadbank_response_file` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор файла запросов от ощадбанка',
  `request_file_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор файла запросов',
  `EDRPOU` VARCHAR(16) COMMENT 'ЕДРПОУ',
  `PROVIDER_NAME` VARCHAR(128) COMMENT 'Название поставщика',
  `DOCUMENT_NUMBER` VARCHAR(64) COMMENT '№ Анкеты',
  `SERVICE_NAME` VARCHAR(1024) COMMENT 'Название услуги',
  `REPORTING_PERIOD` VARCHAR(16) COMMENT 'Отчетный период',
  `PROVIDER_CODE` VARCHAR(16) COMMENT 'Код Банка Поставщика услуги',
  `PROVIDER_ACCOUNT` VARCHAR(32) COMMENT 'р/с Поставщика услуги',
  `PROVIDER_IBAN` VARCHAR(64) COMMENT 'IBAN Поставщика',
  `PAYMENT_NUMBER` VARCHAR(64) COMMENT 'Номер платежного документа',
  `REFERENCE_DOCUMENT` VARCHAR(32) COMMENT 'Референс документа',
  `PAYMENT_DATE` VARCHAR(16) COMMENT 'Дата формирования платежного документа',
  `TOTAL_AMOUNT` DECIMAL(13,2) COMMENT 'Общая сумма перечисления',
  `ANALYTICAL_ACCOUNT` VARCHAR(32) COMMENT 'Номер аналитического счета',
  `FEE` DECIMAL(13,2) COMMENT 'Комиссионное вознаграждение Банка',
  `FEE_CODE` VARCHAR(16) COMMENT 'Код банка для перечисления комиссии',
  `REGISTRY_ID` VARCHAR(32) COMMENT 'ID реестра',
  PRIMARY KEY (`id`),
  KEY `key_request_file_id` (`request_file_id`),
  CONSTRAINT `fk_oschadbank_responce_file__request_file` FOREIGN KEY (`request_file_id`)  REFERENCES `request_file` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB CHARSET = utf8 COLLATE = utf8_unicode_ci COMMENT 'Файл ответа от ощадбанка';

-- ------------------------------
-- Oschadbank response
-- ------------------------------
DROP TABLE IF EXISTS `oschadbank_response`;
CREATE TABLE `oschadbank_response` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор запросов от ощадбанка',
  `request_file_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор файла запросов',
  `account_number` VARCHAR(100) NULL COMMENT 'Номер счета',
  `status` INTEGER NOT NULL DEFAULT 240 COMMENT 'Статус',

  `UTSZN` VARCHAR(64) COMMENT 'Номер УТСЗН',
  `OSCHADBANK_ACCOUNT` VARCHAR(64) COMMENT 'Номер учетной записи получателя жилищной субсидии в АО «Ощадбанк»',
  `FIO` VARCHAR(128) COMMENT 'ФИО получателя субсидии',
  `SERVICE_ACCOUNT` VARCHAR(64) COMMENT 'Номер лицевого счета у поставщика',
  `MONTH_SUM` DECIMAL(13,2) COMMENT 'Общая начисленная сумма за потребленные услуги в отчетном месяце (грн.)',
  `SUM` DECIMAL(13,2) COMMENT 'Общая сумма к оплате, включающая задолженность / переплату за предыдущие периоды (грн.)',
  `SUBSIDY_SUM` DECIMAL(13,2) COMMENT 'Сумма, уплаченная за счет субсидии',
  `DESCRIPTION` VARCHAR(1024) COMMENT 'Описание ошибок',
  PRIMARY KEY (`id`),
  KEY `key_request_file_id` (`request_file_id`),
  CONSTRAINT `fk_oschadbank_responce__request_file` FOREIGN KEY (`request_file_id`)  REFERENCES `request_file` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB CHARSET = utf8 COLLATE = utf8_unicode_ci COMMENT 'Ответы от ощадбанка';

-- ------------------------------
-- Debt
-- ------------------------------

DROP TABLE IF EXISTS `debt`;

CREATE TABLE `debt` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `request_file_id` BIGINT(20) NULL COMMENT 'Идентификатор файла запросов',
  `account_number` VARCHAR(100) NULL COMMENT 'Номер счета',
  `status` INTEGER NOT NULL DEFAULT 240 COMMENT 'Код статуса',

  `internal_city_id` BIGINT(20) COMMENT 'Идентификатор населенного пункта',
  `internal_street_id` BIGINT(20) COMMENT 'Идентификатор улицы',
  `internal_street_type_id` BIGINT(20) COMMENT 'Идентификатор типа улицы',
  `internal_building_id` BIGINT(20) COMMENT 'Идентификатор дома',

  `outgoing_city` VARCHAR(100) COMMENT 'Название населенного пункта используемое центром начисления',
  `outgoing_district` VARCHAR(100) COMMENT 'Название района используемое центром начисления',
  `outgoing_street` VARCHAR(100) COMMENT 'Название улицы используемое центром начисления',
  `outgoing_street_type` VARCHAR(100) COMMENT 'Название типа улицы используемое центром начисления',
  `outgoing_building_number` VARCHAR(100) COMMENT 'Номер дома используемый центром начисления',
  `outgoing_building_corp` VARCHAR(100) COMMENT 'Корпус используемый центром начисления',
  `outgoing_apartment` VARCHAR(100) COMMENT 'Номер квартиры. Не используется',

  `date` DATE NOT NULL COMMENT 'Дата',

  `first_name` VARCHAR(100) COMMENT 'Имя',
  `last_name` VARCHAR(100) COMMENT 'Фамилия',
  `middle_name` VARCHAR(100) COMMENT 'Отчество',
  `city` VARCHAR(100) NOT NULL COMMENT 'Населенный пункт',

  `COD` INTEGER(4),
  `CDPR` BIGINT(12),
  `NCARD` BIGINT(7),
  `IDPIL` VARCHAR(10),
  `PASPPIL` VARCHAR(14),
  `FIOPIL` VARCHAR(50),
  `INDEX` INTEGER(6),
  `CDUL` INTEGER(5),
  `HOUSE` VARCHAR(7),
  `BUILD` VARCHAR(2),
  `APT` VARCHAR(4),
  `KAT` INTEGER(4),
  `LGCODE` INTEGER(4),
  `DATEIN` VARCHAR(10),
  `DATEOUT` VARCHAR(10),
  `MONTHZV` INTEGER(2),
  `YEARZV` INTEGER(4),
  `RAH` VARCHAR(25),
  `MONEY` INTEGER(6),
  `EBK` VARCHAR(10),
  `SUM_BORG` DECIMAL(10, 2),

  PRIMARY KEY (`id`),
  KEY `key_request_file_id` (`request_file_id`),
  KEY `key_account_number` (`account_number`),
  CONSTRAINT `fk_debt__request_file` FOREIGN KEY (`request_file_id`)  REFERENCES `request_file` (`id`) ON DELETE CASCADE
)ENGINE=InnoDB CHARSET=ut8mb4 COLLATE=ut8mb4_unicode_ci COMMENT 'Задолженность';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
