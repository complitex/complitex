ALTER TABLE `ownership_correction` ADD COLUMN `status` INTEGER COMMENT 'Статус';

INSERT INTO `update` (`version`) VALUE ('20160219_0.4.11');