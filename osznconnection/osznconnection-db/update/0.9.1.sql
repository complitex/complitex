ALTER TABLE `region_correction` MODIFY COLUMN `correction` VARCHAR(250) NOT NULL COMMENT 'Название региона';
ALTER TABLE `district_correction` MODIFY COLUMN  `correction` VARCHAR(250) NOT NULL COMMENT 'Название района';
ALTER TABLE `street_type_correction` MODIFY COLUMN `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта типа улицы';
ALTER TABLE `street_type_correction` MODIFY COLUMN  `correction` VARCHAR(250) NOT NULL COMMENT 'Название типа улицы';
ALTER TABLE `street_correction` MODIFY COLUMN `correction` VARCHAR(250) NOT NULL COMMENT 'Название улицы';

INSERT INTO `update` (`version`) VALUE ('0.9.1');
