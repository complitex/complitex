ALTER TABLE locales ADD COLUMN `alternative` TINYINT(1) NOT NULL default 0 COMMENT 'Является ли локаль альтернативной';
UPDATE locales set alternative = 1 where locale = 'uk';

ALTER TABLE `address_sync` ADD COLUMN `alt_name` VARCHAR(100) COMMENT 'Украинское название адресного элемента';
ALTER TABLE `address_sync` ADD COLUMN `alt_additional_name` VARCHAR(20) COMMENT 'Украинское дополнительное название адресного элемента';

INSERT INTO `update` (`version`) VALUE ('20160119_0.4.4');